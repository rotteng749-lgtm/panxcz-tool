package catch_.me_.if_.you_.can_;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * RPC client ke panxcz_rpc (ELF root dari panxcz-libtool core).
 *
 * Kenapa RPC, bukan JNI: core libtool dibuat buat jalan sebagai ELF external
 * (root, akses /proc/&lt;pid&gt;_mem). Waktu dipindah in-process di app (uid
 * biasa) semua bacaan memori EPERM -- makanya explorer lama "ga fungsi".
 * Sekarang core TETAP jalan sebagai root: app spawn `su -c panxcz_rpc --rpc`
 * lalu ngobrol JSON satu baris per request di stdin/stdout. Protokol lihat
 * panxcz-libtool/jni/src/rpc/rpc.cpp.
 *
 * DESAIN KONKURENSI (penting!):
 *   - M  = lock lifecycle doang (proc/to start-kill). JANGAN pernah di-hold
 *          saat nunggu response -- dulu semua method `synchronized (M)` +
 *          reader thread juga butuh M => deadlock => SEMUA call timeout.
 *   - W  = serialisasi write baris request (hindari baris kecampur).
 *   - pending = ConcurrentHashMap; reader thread route response TANPA lock.
 *
 * Semua method blocking (panggil dari background thread).
 */
public final class Panxcz {

    public static final class RpcException extends Exception {
        public RpcException(String msg) {
            super(msg);
        }
    }

    private static final Object M = new Object();  // lifecycle only
    private static final Object W = new Object();  // write serialization
    private static final long TIMEOUT_DEFAULT = 60_000L;
    private static final long TIMEOUT_ATTACH = 180_000L;
    private static final long TIMEOUT_DUMP = 900_000L;

    private static volatile Process proc;
    private static volatile BufferedWriter to;
    private static final ConcurrentHashMap<Long, ArrayBlockingQueue<String>> pending =
            new ConcurrentHashMap<>();
    private static final AtomicLong nextId = new AtomicLong();
    private static volatile String elfPath;
    private static volatile String attachedPkg;
    private static volatile Context appCtx;

    private Panxcz() {
    }

    public static boolean isRunning() {
        return proc != null && alive();
    }

    public static String getAttachedPkg() {
        String p = attachedPkg;
        return p == null ? "" : p;
    }

    private static boolean alive() {
        Process p = proc;
        if (p == null) return false;
        try {
            p.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

    // =======================================================================
    //  Lifecycle
    // =======================================================================

    /** Copy ELF dari assets ke files dir (sekali). */
    private static String ensureElf(Context ctx) throws java.io.IOException {
        String ep = elfPath;
        if (ep != null) return ep;
        File f = new File(ctx.getFilesDir(), "panxcz_rpc");
        InputStream in = ctx.getAssets().open("panxcz_rpc");
        try {
            FileOutputStream fos = new FileOutputStream(f);
            try {
                byte[] buf = new byte[1 << 16];
                int n;
                while ((n = in.read(buf)) > 0) {
                    fos.write(buf, 0, n);
                }
            } finally {
                fos.close();
            }
        } finally {
            in.close();
        }
        //noinspection ResultOfMethodCallIgnored
        f.setExecutable(true, false);
        elfPath = f.getAbsolutePath();
        return elfPath;
    }

    /** Spawn `su -c '<elf> --rpc'` kalau belum jalan. Panggil dengan M. */
    private static void startLocked(Context ctx) throws Exception {
        ensureElf(ctx);
        if (alive()) return;
        killLocked();
        File logFile = new File(ctx.getFilesDir(), "rpc.log");
        ProcessBuilder pb = new ProcessBuilder("su", "-c", elfPath + " --rpc");
        pb.redirectError(ProcessBuilder.Redirect.appendTo(logFile));
        Process p = pb.start();
        proc = p;
        to = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                readerLoop();
            }
        }, "panxcz-rpc-reader");
        t.setDaemon(true);
        t.start();
        // hidup? (ping cepat) -- rawCallLocked TANPA M, jadi reader bisa route
        rawCallLocked("ping", null, 15_000L);
    }

    /** Start kalau perlu (buat API yang tanpa Context-argumen). */
    private static void ensureStarted() throws Exception {
        Context ctx = appCtx;
        if (ctx == null) throw new RpcException("belum init (panggil ensureStarted(ctx))");
        synchronized (M) {
            startLocked(ctx);
        }
    }

    /** Init konteks sekali dari UI thread / bg thread. */
    public static void ensureStarted(Context ctx) throws Exception {
        appCtx = ctx.getApplicationContext();
        synchronized (M) {
            startLocked(ctx);
        }
    }

    private static void readerLoop() {
        Process p = proc;
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = r.readLine()) != null) {
                try {
                    long id = new JSONObject(line).getLong("id");
                    ArrayBlockingQueue<String> q = pending.get(id);  // tanpa lock!
                    if (q != null) q.offer(line);
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable ignored) {
        }
        // EOF: proses mati -> buang semua yang nunggu
        for (ArrayBlockingQueue<String> q : pending.values()) {
            q.offer("{\"id\":-1,\"ok\":false,\"err\":\"rpc process exited\"}");
        }
        pending.clear();
    }

    /** Request JSON tunggal; balas lengkap {"id","ok","d"/"err"}. TANPA M. */
    private static JSONObject rawCallLocked(String cmd, JSONObject args, long timeoutMs)
            throws Exception {
        if (!alive()) throw new RpcException("rpc process mati");
        long id = nextId.incrementAndGet();
        ArrayBlockingQueue<String> q = new ArrayBlockingQueue<>(1);
        pending.put(id, q);
        StringBuilder sb = new StringBuilder("{\"id\":").append(id)
                .append(",\"cmd\":\"").append(cmd).append("\"");
        if (args != null) {
            String a = args.toString();
            // buang '{' depan DAN '}' belakang -> nanti sb.append('}') sendiri
            sb.append(',').append(a, 1, a.length() - 1);
        }
        sb.append('}');
        String reqLine = sb.toString();
        logReq(reqLine);
        synchronized (W) {
            BufferedWriter w = to;
            if (w == null) throw new RpcException("rpc stdin mati");
            w.write(reqLine);
            w.write('\n');
            w.flush();
        }
        String resp = q.poll(timeoutMs, TimeUnit.MILLISECONDS);
        pending.remove(id);
        if (resp == null) throw new RpcException("timeout " + timeoutMs + "ms: " + cmd);
        JSONObject o = new JSONObject(resp);
        if (!o.optBoolean("ok", false)) {
            throw new RpcException(o.optString("err", "rpc error"));
        }
        return o;
    }

    /** Catat request keluar ke filesDir/req.log (debug: jejak apa yang dikirim). */
    private static void logReq(String line) {
        try {
            Context ctx = appCtx;
            if (ctx == null) return;
            FileWriter fw = new FileWriter(new File(ctx.getFilesDir(), "req.log"), true);
            try {
                fw.write(line);
                fw.write('\n');
            } finally {
                fw.close();
            }
        } catch (Throwable ignored) {
        }
    }

    /** Call + ambil payload "d". */
    private static JSONObject callLocked(String cmd, JSONObject args, long timeoutMs)
            throws Exception {
        return rawCallLocked(cmd, args, timeoutMs).getJSONObject("d");
    }

    private static void killLocked() {
        BufferedWriter w = to;
        to = null;
        if (w != null) {
            try {
                w.close();
            } catch (Throwable ignored) {
            }
        }
        Process p = proc;
        proc = null;
        if (p != null) {
            try {
                p.getOutputStream().close();
            } catch (Throwable ignored) {
            }
            p.destroy();
        }
        pending.clear();
        attachedPkg = null;
    }

    public static void kill() {
        synchronized (M) {
            killLocked();
        }
    }

    // =======================================================================
    //  API -- semua blocking, panggil dari background thread
    // =======================================================================

    /** Spawn (kalau perlu) + attach ke package game. Berat: bisa 1-2 menit. */
    public static JSONObject attach(Context ctx, String pkg, String lib) throws Exception {
        appCtx = ctx.getApplicationContext();
        synchronized (M) {
            startLocked(ctx);
        }
        JSONObject a = new JSONObject();
        if (pkg != null && !pkg.isEmpty()) a.put("pkg", pkg);
        if (lib != null && !lib.isEmpty()) a.put("lib", lib);
        a.put("pool", true);
        a.put("wait", 60);
        JSONObject d = callLocked("attach", a, TIMEOUT_ATTACH);
        attachedPkg = pkg;
        return d;
    }

    public static JSONObject procs(Context ctx) throws Exception {
        appCtx = ctx.getApplicationContext();
        ensureStarted();
        return callLocked("procs", null, TIMEOUT_DEFAULT);
    }

    public static JSONObject status() throws Exception {
        ensureStarted();
        return callLocked("status", null, 15_000L);
    }

    /** Daftar kelas (by = class|method|field). Return {"total","count","classes"}. */
    public static JSONObject classes(String filter, String by) throws Exception {
        JSONObject a = new JSONObject();
        if (filter != null) a.put("filter", filter);
        if (by != null) a.put("by", by);
        return callLocked("classes", a, TIMEOUT_ATTACH);
    }

    /** Detail satu kelas: {"name","ns","image","fields":[...],"methods":[...]}. */
    public static JSONObject classInfo(String name) throws Exception {
        JSONObject a = new JSONObject();
        a.put("name", name);
        return callLocked("class", a, 90_000L);
    }

    public static JSONObject instances(String name, int max) throws Exception {
        JSONObject a = new JSONObject();
        a.put("name", name);
        a.put("max", max);
        return callLocked("instances", a, 90_000L);
    }

    /** Hex dump method/alamat. spec = "Class::Method" / "lib.so:0xRVA" / "0xADDR". */
    public static String hex(String spec, int len) throws Exception {
        JSONObject a = new JSONObject();
        a.put("spec", spec);
        a.put("len", len);
        return rawCallLocked("hex", a, 30_000L).optString("d", "");
    }

    public static String asm(String spec, int len) throws Exception {
        JSONObject a = new JSONObject();
        a.put("spec", spec);
        a.put("len", len);
        return rawCallLocked("asm", a, 30_000L).optString("d", "");
    }

    /** Snippet hook Dobby-C++ (port GenerateCppCode). */
    public static JSONObject gencode(String cls, String method, String val, String type)
            throws Exception {
        JSONObject a = new JSONObject();
        a.put("cls", cls);
        a.put("method", method);
        if (val != null) a.put("val", val);
        if (type != null) a.put("type", type);
        return callLocked("gencode", a, 60_000L);
    }

    /** Script GG-Lua (port GenerateGGPatchCode). */
    public static JSONObject ggcode(String cls, String method, String val, String type)
            throws Exception {
        JSONObject a = new JSONObject();
        a.put("cls", cls);
        a.put("method", method);
        if (val != null) a.put("val", val);
        if (type != null) a.put("type", type);
        return callLocked("ggcode", a, 60_000L);
    }

    /** Dump .cs/.json/.h/.ptrs.tsv semua lib il2cpp. Berat: bisa bermenit. */
    public static JSONObject dump() throws Exception {
        JSONObject a = new JSONObject();
        a.put("allLibs", true);
        a.put("json", true);
        a.put("header", true);
        a.put("ptr", true);
        return callLocked("dump", a, TIMEOUT_DUMP);
    }

    public static void dumpStop() {
        try {
            if (alive()) {
                rawCallLocked("dumpstop", null, 5_000L);
            }
        } catch (Throwable ignored) {
        }
    }

    /** Force method balikin nilai (stub ret) -- port PatcherView. */
    public static JSONObject force(String cls, String method, String val, String type)
            throws Exception {
        JSONObject a = new JSONObject();
        a.put("cls", cls);
        a.put("method", method);
        a.put("val", val == null ? "" : val);
        if (type != null) a.put("type", type);
        return callLocked("force", a, 60_000L);
    }

    public static JSONObject unforce(String cls, String method) throws Exception {
        JSONObject a = new JSONObject();
        if (cls != null) a.put("cls", cls);
        if (method != null) a.put("method", method);
        return callLocked("unforce", a, 30_000L);
    }

    public static JSONObject unforceAll() throws Exception {
        JSONObject a = new JSONObject();
        a.put("all", true);
        return callLocked("unforce", a, 30_000L);
    }

    public static JSONObject forced() throws Exception {
        return callLocked("forced", null, 15_000L);
    }

    public static JSONObject readValue(long addr, String type) throws Exception {
        JSONObject a = new JSONObject();
        a.put("addr", addr);
        a.put("type", type);
        return callLocked("read", a, 15_000L);
    }

    public static JSONObject writeValue(long addr, String type, String val) throws Exception {
        JSONObject a = new JSONObject();
        a.put("addr", addr);
        a.put("type", type);
        a.put("val", val);
        return callLocked("write", a, 15_000L);
    }

    public static JSONArray logLines(int n) throws Exception {
        JSONObject a = new JSONObject();
        a.put("n", n);
        return callLocked("log", a, 15_000L).optJSONArray("lines");
    }

    public static void detach() {
        try {
            if (alive()) {
                rawCallLocked("detach", null, 15_000L);
            }
        } catch (Throwable ignored) {
        }
        attachedPkg = null;
    }

    // =======================================================================
    //  Util UI
    // =======================================================================

    public static void copy(Context ctx, String text) {
        try {
            ClipboardManager cm = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setPrimaryClip(ClipData.newPlainText("panxcz", text));
            }
        } catch (Throwable ignored) {
        }
    }

    public static String getLoadError() {
        return "";
    }
}
