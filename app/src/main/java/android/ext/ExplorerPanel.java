package android.ext;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import catch_.me_.if_.you_.can_.Panxcz;

/**
 * Controller tab Explorer: browser kelas IL2CPP lewat panxcz_rpc (root).
 *
 * UX drill-down (nggak pakai dialog -- service context rawan BadToken):
 *   CLASSES  : daftar kelas (filter RPC by=class)
 *   MEMBERS  : fields + methods satu kelas (tap kelas)
 *   ACTIONS  : aksi satu method (hex/asm/codegen/force) (tap method)
 * Field numeric bisa dibaca/ditulis: isi kotak input dulu, lalu tap field.
 */
public final class ExplorerPanel {

    private static final int ST_CLASSES = 0;
    private static final int ST_MEMBERS = 1;
    private static final int ST_ACTIONS = 2;

    private final MainService service;
    private final Context ctx;
    private final Handler ui = new Handler(Looper.getMainLooper());
    private final ExecutorService bg = Executors.newSingleThreadExecutor();

    private final EditText input;
    private final EditText filter;
    private final TextView attachBtn;
    private final TextView listBtn;
    private final TextView backBtn;
    private final TextView dumpBtn;
    private final ListView list;
    private final TextView status;
    private final TextView output;
    private final ScrollView scroll;

    private ArrayAdapter<String> adapter;
    private int state = ST_CLASSES;

    // data members
    private String currentClass = "";
    private JSONObject currentInfo;
    private final List<Integer> memberKind = new ArrayList<>();  // 0=header 1=field 2=method
    private final List<Integer> memberIdx = new ArrayList<>(); // index asli di info
    private String currentMethod = "";
    private String currentMethodSig = "";
    private boolean busy;

    public ExplorerPanel(MainService service, View root) {
        this.service = service;
        this.ctx = service.getContext();
        // id asli (R.id.* milik aplikasi -- diakses lewat resources)
        input = (EditText) find(root, "explorer_input");
        filter = (EditText) find(root, "explorer_filter");
        attachBtn = (TextView) find(root, "explorer_attach");
        listBtn = (TextView) find(root, "explorer_search");
        backBtn = (TextView) find(root, "explorer_back");
        dumpBtn = (TextView) find(root, "explorer_dump");
        list = (ListView) find(root, "explorer_list");
        status = (TextView) find(root, "explorer_status");
        output = (TextView) find(root, "explorer_output");
        scroll = (ScrollView) find(root, "explorer_scroll");

        adapter = new ArrayAdapter<String>(ctx,
                android.R.layout.simple_list_item_1) {
            @Override
            public android.view.View getView(int position, android.view.View convertView,
                                             android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTextColor(0xFFFFFFFF);
                String s = getItem(position);
                if (s != null && s.startsWith("──")) {
                    v.setTextColor(0xFF888888);
                }
                return v;
            }
        };
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, position, id) -> onRowTap(position));

        attachBtn.setOnClickListener(v -> doAttach());
        listBtn.setOnClickListener(v -> doListOrReload());
        backBtn.setOnClickListener(v -> doBack());
        dumpBtn.setOnClickListener(v -> doDump());

        setState(ST_CLASSES, "belum attach — ketik package lalu Attach");
    }

    private View find(View root, String name) {
        int id = ctx.getResources().getIdentifier(name, "id", ctx.getPackageName());
        return id == 0 ? null : root.findViewById(id);
    }

    // ------------------------------------------------------------------ util
    private void setStatus(final String s) {
        ui.post(() -> {
            if (status != null) status.setText(s);
        });
    }

    private void out(final String s) {
        ui.post(() -> {
            if (output == null) return;
            output.setText(s);
            if (scroll != null) scroll.post(() -> scroll.fullScroll(View.FOCUS_UP));
        });
    }

    private void outAppend(final String s) {
        ui.post(() -> {
            if (output == null) return;
            output.append("\n" + s);
            if (scroll != null) scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
        });
    }

    private void setRows(final List<String> rows) {
        ui.post(() -> {
            adapter.clear();
            adapter.addAll(rows);
            adapter.notifyDataSetChanged();
        });
    }

    private void setState(int st, String statusText) {
        state = st;
        if (backBtn != null) {
            backBtn.setVisibility(st == ST_CLASSES ? View.GONE : View.VISIBLE);
        }
        if (listBtn != null) {
            listBtn.setText(st == ST_MEMBERS ? "Filter" : "List");
        }
        setStatus(statusText);
    }

    private void busy(final boolean b, final String what) {
        busy = b;
        ui.post(() -> {
            if (attachBtn != null) attachBtn.setEnabled(!b);
            if (listBtn != null) listBtn.setEnabled(!b);
            if (dumpBtn != null) dumpBtn.setEnabled(!b);
            if (list != null) list.setEnabled(!b);
        });
        if (b) setStatus(what + " ...");
    }

    private String inputText() {
        return input == null || input.getText() == null ? "" : input.getText().toString().trim();
    }

    private String filterText() {
        return filter == null || filter.getText() == null ? "" : filter.getText().toString().trim();
    }

    private boolean icontains(String hay, String needle) {
        return needle.isEmpty() || hay.toLowerCase().contains(needle.toLowerCase());
    }

    // ---------------------------------------------------------------- attach
    private void doAttach() {
        if (busy) return;
        String pkg = inputText();
        busy(true, pkg.isEmpty() ? "cari proses il2cpp" : "attach " + pkg);
        bg.execute(() -> {
            String target = pkg;
            try {
                if (target.isEmpty()) {
                    // auto: proses dengan skor il2cpp tertinggi (tolak biner
                    // sistem kayak surfaceflinger yang skornya sama tapi bukan app)
                    JSONArray ps = Panxcz.procs(ctx).optJSONArray("procs");
                    if (ps == null || ps.length() == 0)
                        throw new Panxcz.RpcException("nggak ada proses il2cpp");
                    String best = "";
                    int bestScore = -1;
                    for (int i = 0; i < ps.length(); i++) {
                        JSONObject pr = ps.getJSONObject(i);
                        String name = pr.optString("pkg", "");
                        if (name.isEmpty() || name.charAt(0) == '/') continue;  // biner sistem
                        int sc = pr.optInt("score", 0);
                        if (sc > bestScore) {
                            bestScore = sc;
                            best = name;
                        }
                    }
                    if (best.isEmpty()) best = ps.getJSONObject(0).optString("pkg", "");
                    final String top = best;
                    ui.post(() -> {
                        if (input != null && !top.isEmpty()) input.setText(top);
                    });
                    target = top;
                }
                final String usePkg = target;
                JSONObject d = Panxcz.attach(ctx, usePkg, "");
                final String msg = "pid " + d.optInt("pid", 0) + " | " +
                        d.optString("lib", "?") + " @ " + d.optString("base", "?") +
                        " | pool " + (d.optBoolean("pool", false)
                        ? d.optLong("classes", 0) + " kelas"
                        : "NGGAK SIAP (" + d.optString("poolErr", "?") + ")");
                out("ATTACH OK\n" + msg);
                busy(false, msg);
                // langsung load daftar kelas
                loadClasses("");
            } catch (Throwable t) {
                busy(false, "gagal: " + t.getMessage());
                out("ERROR: " + t.getMessage());
            }
        });
    }

    // ---------------------------------------------------------------- classes
    private void doListOrReload() {
        if (busy) return;
        if (state == ST_MEMBERS) {
            // filter client-side di daftar member
            applyMemberFilter();
            return;
        }
        loadClasses(filterText());
    }

    private void loadClasses(final String f) {
        busy(true, "ambil daftar kelas");
        bg.execute(() -> {
            try {
                JSONObject d = Panxcz.classes(f, "class");
                JSONArray arr = d.optJSONArray("classes");
                final List<String> rows = new ArrayList<>();
                if (arr != null) {
                    for (int i = 0; i < arr.length(); ++i) rows.add(arr.optString(i, ""));
                }
                final long total = d.optLong("total", rows.size());
                ui.post(() -> {
                    setState(ST_CLASSES, rows.size() + " / " + total + " kelas" +
                            (f.isEmpty() ? "" : " (filter: " + f + ")"));
                });
                setRows(rows);
                busy(false, null);
                if (rows.isEmpty()) out("nggak ada kelas cocok sama filter '" + f + "'");
            } catch (Throwable t) {
                busy(false, null);
                out("ERROR: " + t.getMessage());
                setStatus("gagal: " + t.getMessage());
            }
        });
    }

    // ---------------------------------------------------------------- members
    private void openClass(final String name) {
        busy(true, "buka " + name);
        bg.execute(() -> {
            try {
                JSONObject info = Panxcz.classInfo(name);
                currentInfo = info;
                currentClass = name;
                ui.post(() -> buildMemberRows(filterText()));
            } catch (Throwable t) {
                busy(false, null);
                out("ERROR: " + t.getMessage());
            }
        });
    }

    private void buildMemberRows(String f) {
        memberKind.clear();
        memberIdx.clear();
        List<String> rows = new ArrayList<>();
        try {
            JSONArray fields = currentInfo.optJSONArray("fields");
            JSONArray methods = currentInfo.optJSONArray("methods");
            int nf = fields == null ? 0 : fields.length();
            int nm = methods == null ? 0 : methods.length();

            rows.add("── FIELDS (" + nf + ") ──");
            memberKind.add(0);
            memberIdx.add(-1);
            for (int i = 0; i < nf; ++i) {
                JSONObject fd = fields.optJSONObject(i);
                if (fd == null) continue;
                String line = (fd.optBoolean("static", false) ? "static " : "inst   ") +
                        fd.optString("type", "?") + " " + fd.optString("name", "?") +
                        " @0x" + Integer.toHexString(fd.optInt("off", 0));
                if (!icontains(line, f)) continue;
                rows.add(line);
                memberKind.add(1);
                memberIdx.add(i);
            }
            rows.add("── METHODS (" + nm + ") ──");
            memberKind.add(0);
            memberIdx.add(-1);
            for (int i = 0; i < nm; ++i) {
                JSONObject md = methods.optJSONObject(i);
                if (md == null) continue;
                String line = md.optString("name", "?") +
                        "   [" + md.optString("rva", "0x0") + " " +
                        md.optString("module", "?") + "]";
                if (!icontains(line, f)) continue;
                rows.add(line);
                memberKind.add(2);
                memberIdx.add(i);
            }
        } catch (Throwable ignored) {
        }
        final String hdr = currentClass + "  (" + currentInfo.optString("ns", "") +
                (currentInfo.optString("image", "").isEmpty() ? "" :
                        " / " + currentInfo.optString("image", "")) + ")";
        setState(ST_MEMBERS, hdr);
        setRows(rows);
        busy(false, null);
    }

    private void applyMemberFilter() {
        if (currentInfo == null) return;
        ui.post(() -> buildMemberRows(filterText()));
    }

    // ---------------------------------------------------------------- actions
    private void openMethod(int infoIdx) {
        try {
            JSONObject md = currentInfo.optJSONArray("methods").optJSONObject(infoIdx);
            if (md == null) return;
            currentMethod = md.optString("name", "");
            // nama polos buat RPC (tanpa signature)
            String bare = currentMethod;
            int par = bare.indexOf('(');
            if (par >= 0) bare = bare.substring(0, par);
            int sp = bare.lastIndexOf(' ');
            if (sp >= 0) bare = bare.substring(sp + 1);
            currentMethod = bare;
            currentMethodSig = md.optString("name", "");

            List<String> rows = new ArrayList<>();
            rows.add("── " + currentMethodSig + " ──");
            memberKind.clear();
            memberIdx.clear();
            memberKind.add(0);
            memberIdx.add(-1);
            String[] actions = {
                    "Hex view (64 byte)",
                    "Asm view (AArch64)",
                    "Copy C++ hook code",
                    "Copy GG Lua script",
                    "Force return = [input]",
                    "Restore method (unforce)",
                    "Live instances (max 32)",
            };
            for (int i = 0; i < actions.length; ++i) {
                rows.add(actions[i]);
                memberKind.add(3);
                memberIdx.add(i);
            }
            setState(ST_ACTIONS, currentClass + "::" + currentMethod +
                    "  rva " + md.optString("rva", "?") + " (" + md.optString("module", "?") + ")");
            setRows(rows);
        } catch (Throwable ignored) {
        }
    }

    private void runAction(final int action) {
        if (busy) return;
        final String cls = currentClass;
        final String mth = currentMethod;
        final String val = inputText();
        final String spec = cls + "::" + mth;
        switch (action) {
            case 0:
                busy(true, "hex " + spec);
                bg.execute(() -> {
                    try {
                        out(Panxcz.hex(spec, 64));
                    } catch (Throwable t) {
                        out("ERROR: " + t.getMessage());
                    }
                    busy(false, null);
                });
                break;
            case 1:
                busy(true, "asm " + spec);
                bg.execute(() -> {
                    try {
                        out(Panxcz.asm(spec, 160));
                    } catch (Throwable t) {
                        out("ERROR: " + t.getMessage());
                    }
                    busy(false, null);
                });
                break;
            case 2:
                busy(true, "generate C++ code");
                bg.execute(() -> {
                    try {
                        JSONObject d = Panxcz.gencode(cls, mth, val, null);
                        final String code = d.optString("code", "");
                        Panxcz.copy(ctx, code);
                        out("=== Dobby C++ hook (" + spec + ") — SUDAH DI-COPY ===\n" + code +
                                (d.optBoolean("sigOk", false) ? "" :
                                        "\n\n[warning: signature nggak kebaca — nama method polos doang]"));
                    } catch (Throwable t) {
                        out("ERROR: " + t.getMessage());
                    }
                    busy(false, null);
                });
                break;
            case 3:
                busy(true, "generate GG Lua");
                bg.execute(() -> {
                    try {
                        JSONObject d = Panxcz.ggcode(cls, mth, val, null);
                        final String code = d.optString("code", "");
                        Panxcz.copy(ctx, code);
                        out("=== GG Lua (" + spec + ") — SUDAH DI-COPY ===\n" + code);
                    } catch (Throwable t) {
                        out("ERROR: " + t.getMessage());
                    }
                    busy(false, null);
                });
                break;
            case 4:
                if (val.isEmpty()) {
                    out("isi kotak input dulu sama nilainya (mis. 9999 / true / 0.5)");
                    return;
                }
                busy(true, "force " + spec + " = " + val);
                bg.execute(() -> {
                    try {
                        JSONObject d = Panxcz.force(cls, mth, val, null);
                        out("FORCE OK: " + spec + " = " + val + "\n" +
                                "addr " + d.optString("addr", "?") + "\n" +
                                d.optString("summary", ""));
                    } catch (Throwable t) {
                        out("ERROR: " + t.getMessage());
                    }
                    busy(false, null);
                });
                break;
            case 5:
                busy(true, "unforce " + spec);
                bg.execute(() -> {
                    try {
                        JSONObject d = Panxcz.unforce(cls, mth);
                        out(d.optBoolean("ok", false)
                                ? "RESTORED: " + spec + " (sisa force: " + d.optInt("forced", 0) + ")"
                                : "nggak ada force di " + spec);
                    } catch (Throwable t) {
                        out("ERROR: " + t.getMessage());
                    }
                    busy(false, null);
                });
                break;
            case 6:
                busy(true, "cari instance " + cls);
                bg.execute(() -> {
                    try {
                        JSONObject d = Panxcz.instances(cls, 32);
                        JSONArray a = d.optJSONArray("addrs");
                        StringBuilder sb = new StringBuilder();
                        sb.append("instance ").append(cls).append(": ")
                                .append(d.optInt("count", 0)).append(" hidup\n");
                        if (a != null) {
                            for (int i = 0; i < a.length(); ++i)
                                sb.append("  ").append(a.optString(i, "")).append("\n");
                        }
                        out(sb.toString());
                    } catch (Throwable t) {
                        out("ERROR: " + t.getMessage());
                    }
                    busy(false, null);
                });
                break;
            default:
                break;
        }
    }

    // ---------------------------------------------------------------- field
    private void tapField(final int infoIdx) {
        if (currentInfo == null) return;
        try {
            final JSONObject fd = currentInfo.optJSONArray("fields").optJSONObject(infoIdx);
            if (fd == null) return;
            final String name = fd.optString("name", "?");
            final int off = fd.optInt("off", 0);
            final String rpcType = ftypeToRpc(fd.optInt("ft", 1));
            final boolean isStatic = fd.optBoolean("static", false);
            final String val = inputText();
            final boolean doWrite = !val.isEmpty() && isNumericType(rpcType);

            busy(true, (doWrite ? "tulis " : "baca ") + currentClass + "." + name);
            bg.execute(() -> {
                try {
                    if (doWrite) {
                        // tulis ke SEMUA instance hidup (atau static: lewat alamat statics? -- v1: instance)
                        JSONObject d = Panxcz.instances(currentClass, 8);
                        JSONArray a = d.optJSONArray("addrs");
                        int n = a == null ? 0 : a.length();
                        StringBuilder sb = new StringBuilder();
                        sb.append("write ").append(currentClass).append(".").append(name)
                                .append(" = ").append(val).append(" @").append(n)
                                .append(" instance\n");
                        for (int i = 0; i < n; ++i) {
                            long addr = parseHex(a.optString(i, "0")) + off;
                            JSONObject w = Panxcz.writeValue(addr, rpcType, val);
                            sb.append("  0x").append(Long.toHexString(addr))
                                    .append(" = ").append(val)
                                    .append(w.optBoolean("verified", false) ? " (ok)" : " (??)")
                                    .append("\n");
                        }
                        out(sb.toString());
                    } else {
                        JSONObject d = Panxcz.instances(currentClass, 3);
                        JSONArray a = d.optJSONArray("addrs");
                        StringBuilder sb = new StringBuilder();
                        sb.append(currentClass).append(".").append(name)
                                .append(" (").append(fd.optString("type", "?"))
                                .append(isStatic ? ", static" : "").append(")\n");
                        int n = a == null ? 0 : a.length();
                        if (n == 0) sb.append("  nggak ada instance hidup\n");
                        for (int i = 0; i < n; ++i) {
                            long addr = parseHex(a.optString(i, "0")) + off;
                            try {
                                JSONObject r = Panxcz.readValue(addr, rpcType);
                                sb.append("  [0x").append(Long.toHexString(parseHex(a.optString(i, "0"))))
                                        .append("+0x").append(Integer.toHexString(off)).append("] = ")
                                        .append(r.optDouble("val", 0)).append("\n");
                            } catch (Throwable ignored) {
                                sb.append("  0x").append(Long.toHexString(addr)).append(" = ?\n");
                            }
                        }
                        out(sb.toString());
                    }
                } catch (Throwable t) {
                    out("ERROR: " + t.getMessage());
                }
                busy(false, null);
            });
        } catch (Throwable ignored) {
        }
    }

    private static String ftypeToRpc(int ft) {
        switch (ft) {
            case 0: return "ptr";    // Ptr
            case 1: return "i32";    // Int32
            case 2: return "u32";    // UInt32
            case 3: return "i64";    // Int64
            case 4: return "f32";    // Float
            case 5: return "f64";    // Double
            case 6: return "i8";     // Bool
            default: return "i32";
        }
    }

    private static boolean isNumericType(String t) {
        return !"ptr".equals(t);
    }

    private static long parseHex(String s) {
        try {
            return Long.parseLong(s.replace("0x", "").replace("0X", ""), 16);
        } catch (Throwable t) {
            return 0;
        }
    }

    // ---------------------------------------------------------------- tap
    private void onRowTap(int position) {
        switch (state) {
            case ST_CLASSES:
                if (adapter.getCount() > position) {
                    String name = adapter.getItem(position);
                    if (name != null && !name.startsWith("──")) openClass(name);
                }
                break;
            case ST_MEMBERS: {
                if (position >= memberKind.size()) break;
                int kind = memberKind.get(position);
                int idx = memberIdx.get(position);
                if (kind == 1) tapField(idx);
                else if (kind == 2) openMethod(idx);
                break;
            }
            case ST_ACTIONS: {
                if (position >= memberKind.size()) break;
                if (memberKind.get(position) == 3) runAction(memberIdx.get(position));
                break;
            }
            default:
                break;
        }
    }

    private void doBack() {
        if (state == ST_ACTIONS) {
            ui.post(() -> buildMemberRows(filterText()));
        } else if (state == ST_MEMBERS) {
            setState(ST_CLASSES, "kelas — ketuk untuk buka");
            bg.execute(() -> loadClasses(filterText()));
        }
    }

    // ---------------------------------------------------------------- dump
    private void doDump() {
        if (busy) return;
        busy(true, "dump semua lib il2cpp (bisa 1-3 menit)");
        bg.execute(() -> {
            try {
                JSONObject d = Panxcz.dump();
                StringBuilder sb = new StringBuilder();
                sb.append(d.optBoolean("ok", false) ? "DUMP OK\n" : "DUMP GAGAL\n");
                sb.append("kelas ").append(d.optLong("classes", 0))
                        .append(" | field ").append(d.optLong("fields", 0))
                        .append(" | method ").append(d.optLong("methods", 0)).append("\n");
                sb.append("dir: ").append(d.optString("dir", "?")).append("\n");
                sb.append("cs : ").append(d.optString("path", "-")).append("\n");
                if (!d.optString("jsonPath", "").isEmpty())
                    sb.append("json: ").append(d.optString("jsonPath")).append("\n");
                if (!d.optString("headerPath", "").isEmpty())
                    sb.append("h   : ").append(d.optString("headerPath")).append("\n");
                if (!d.optString("ptrsPath", "").isEmpty())
                    sb.append("ptrs: ").append(d.optString("ptrsPath")).append("\n");
                out(sb.toString());
                busy(false, "dump selesai");
            } catch (Throwable t) {
                busy(false, "dump gagal");
                out("ERROR: " + t.getMessage());
            }
        });
    }
}
