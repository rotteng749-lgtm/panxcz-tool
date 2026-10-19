package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.ext.AddressItemSet;
import android.fix.SparseArray;
import android.os.Build;
import android.os.Handler;
import android.sup.ContainerHelpers;
import catch_.me_.if_.you_.can_.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* JADX INFO: loaded from: classes.dex */
class DaemonManager {
    public static final byte CLIENT = 0;
    public static final String LABEL = "android-daemon";
    public static final int MAX_PATH_SIZE = 4000;
    public static final String ROOT_OK = "root-ok";
    private static final int SIGBUS = 7;
    private static final int SIGKILL = 9;
    private static final int SIGKILL_OOM = 137;
    private static final int SIGTERM = 15;
    public static final long TIME_JUMP_MUL = 1000000000;
    public static final int TIME_JUMP_MUL_INT = 1000;
    public static volatile boolean bypassDaemonFail = false;
    public static volatile boolean lastSearchFuzzy = false;
    public static volatile boolean warnAboutLags = false;
    static Proc sProcess = null;
    private static final List<Runnable> waitForDaemonStart = new ArrayList();
    private static boolean waitForDaemon = true;
    static volatile String lastReadError = null;
    private static volatile String lastSendError = null;
    Proc mProcess = null;
    Exchanger<byte[]> mMemItemExchanger = new Exchanger<>();
    private volatile boolean needCancel = false;
    volatile int daemonPid = -1;
    volatile int suPid = -1;
    volatile String daemonName = "???";
    private volatile int countFuzzyEqualRun = 0;
    private volatile int countFuzzyEqualRunMax = 0;
    private volatile long fuzzyEqualMemoryFrom = 0;
    private volatile long fuzzyEqualMemoryTo = -1;
    Thread mReaderThread = new DaemonThread("mReaderThread") { // from class: android.ext.DaemonManager.1
        private void handleMemoryItem(byte[] buffer) {
            try {
                Exchanger<byte[]> mMemItemExchanger = DaemonManager.this.mMemItemExchanger;
                while (!interrupted()) {
                    try {
                        mMemItemExchanger.exchange(buffer, 500L, TimeUnit.MILLISECONDS);
                        return;
                    } catch (InterruptedException e) {
                    }
                }
            } catch (Throwable e2) {
                Log.e("handleMemoryItem failed", e2);
            }
        }

        private void postMessage(final byte[] buffer) throws IOException {
            if (buffer[0] == 20 && buffer[1] == 0) {
                handleMemoryItem(buffer);
            } else {
                ThreadManager.getHandlerUiThread().post(new Runnable() { // from class: android.ext.DaemonManager.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            DaemonManager.this.processMessage(buffer);
                        } catch (Throwable e) {
                            Log.e("Failed process message: " + ((int) buffer[0]) + ' ' + buffer.length, e);
                        }
                    }
                });
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            InOut inOut = DaemonManager.this.inOut;
            while (!interrupted()) {
                try {
                    byte[] buffer = inOut.readPacket();
                    DaemonManager.lastReadError = null;
                    if (buffer != null) {
                        postMessage(buffer);
                    }
                } catch (Throwable e) {
                    Log.e("Read error", e);
                    DaemonManager.lastReadError = e.toString();
                    ThreadManager.getHandlerUiThread().post(new Runnable() { // from class: android.ext.DaemonManager.1.2
                        @Override // java.lang.Runnable
                        public void run() {
                            DaemonManager.this.messageFailed();
                        }
                    });
                    return;
                }
            }
        }
    };
    private volatile int M = 1;
    private volatile int N = 1;
    private volatile long timeJump = 0;
    private final ArrayList<Object> waitAlter = new ArrayList<>();
    private final ArrayList<SavedItem> waitFreeze = new ArrayList<>();
    private final ArrayList<SavedItem> waitUnfreeze = new ArrayList<>();
    long prevConfigCrc = 0;
    private Integer oldDataInRam = null;
    final InOut inOut = new InOut();

    public static void runAfterDaemonStart(Runnable run) {
        synchronized (waitForDaemonStart) {
            if (waitForDaemon) {
                waitForDaemonStart.add(run);
            } else {
                ThreadManager.runOnWriteThread(run);
            }
        }
    }

    static void daemonStarted() {
        synchronized (waitForDaemonStart) {
            waitForDaemon = false;
            for (Runnable run : waitForDaemonStart) {
                ThreadManager.runOnWriteThread(run);
            }
        }
    }

    private static boolean checkInputStream(InputStream is, String checkString) throws IOException {
        byte[] check = checkString.getBytes();
        byte[] skipBytes = new byte[4096];
        int index = 0;
        int skip = 0;
        Log.e("Start: " + checkString);
        while (true) {
            int chr = is.read();
            if (chr == -1) {
                break;
            }
            byte b = (byte) chr;
            if (skip >= skipBytes.length) {
                break;
            }
            int skip2 = skip + 1;
            skipBytes[skip] = b;
            if (b == check[index]) {
                index++;
                if (index == check.length) {
                    Log.e("Good: " + checkString);
                    return true;
                }
            } else {
                index = 0;
            }
            if (is.available() == 0) {
                for (int i = 0; i < 30; i++) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                    }
                    if (is.available() != 0) {
                        break;
                    }
                }
            }
            if (is.available() == 0) {
                RootDetector.debug = String.valueOf(RootDetector.debug) + "No more data: " + skip2 + '\n';
                skip = skip2;
                break;
            }
            skip = skip2;
        }
        String str = new String(skipBytes, 0, skip);
        String msg = "Fail: '" + checkString + "' " + skip + " '" + str + SearchMenuItem.HEX_UTF_8;
        RootDetector.debug = String.valueOf(RootDetector.debug) + msg + '\n';
        Log.e(msg);
        return false;
    }

    private static boolean checkDaemon(Proc proc, File dir) throws IOException {
        if (proc == null) {
            return false;
        }
        Process process = proc.process;
        if (process == null) {
            return false;
        }
        InputStream out = proc.stdout;
        boolean ret = checkInputStream(proc.stderr, "android-daemon err") && checkInputStream(out, "android-daemon out");
        if (!Config.vSpace && ret) {
            new SPEditor().putInt(ROOT_OK, (int) (((16176.7f * 1) - 183.7f) + 0.5f)).commit();
        }
        if (ret) {
            int fifo = out.read();
            if (fifo == 49 || fifo == 48) {
                out.read();
            }
            if (fifo == 49) {
                boolean use = false;
                fixDaemonFifo(dir);
                try {
                    OutputStream old = proc.stdin;
                    try {
                        Log.d("IF 0");
                        InputStream stdout = new FileInputStream(new File(dir, "o"));
                        Log.d("IF 1");
                        InputStream stderr = new FileInputStream(new File(dir, "e"));
                        Log.d("IF 2");
                        OutputStream stdin = new FileOutputStream(new File(dir, "i"));
                        Log.d("IF 3");
                        proc.stdin = stdin;
                        proc.stdout = stdout;
                        proc.stderr = stderr;
                        use = true;
                    } catch (Throwable e) {
                        Log.e("Fifo fail", e);
                    }
                    old.write(use ? 89 : 78);
                    old.write(10);
                    old.flush();
                    InOut.fifo = use;
                    return ret;
                } catch (Throwable e2) {
                    Log.e("Fifo fail 2", e2);
                    return ret;
                }
            }
            if (fifo != 48) {
                Log.w("Odd fifo: " + fifo);
                return ret;
            }
            return ret;
        }
        return ret;
    }

    /**
     * The daemon runs as root (su), so the FIFOs it creates may have a root-owned
     * umask mode and a root MLS level (s0) that does not match the app's data file
     * categories (s0:cXXX,...). On enforcing SELinux devices the app then fails to
     * open them (EACCES - "Failed to load daemon"). Fix both as root before the
     * app tries to open the FIFOs: world rw mode + copy the parent dir context.
     */
    private static void fixDaemonFifo(File dir) {
        try {
            String path = dir.getAbsolutePath();
            String cmd = "exec sh -c 'chmod 0666 \"$1/i\" \"$1/o\" \"$1/e\" 2>/dev/null; "
                    + "O=$(ls -laZd \"$1\" 2>/dev/null); C=\"\"; for w in $O; do case $w in *:object_r:*) C=$w;; esac; done; "
                    + "[ -n \"$C\" ] && chcon \"$C\" \"$1/i\" \"$1/o\" \"$1/e\" 2>/dev/null; exit' _ \"" + path + "\"";
            String r = RootDetector.runCmd(cmd, 10);
            Log.d("Fifo fix: " + r);
        } catch (Throwable e) {
            Log.w("Fifo fix fail", e);
        }
    }

    public static String getNativeCheck() {
        return Hash.hash(Debug.getInfo(), "S!H!A!-!3!8!4".replace("!", ""));
    }

    public static String system(String execPath) {
        String type = null;
        String dev = null;
        try {
            File lib = new File(execPath);
            if (!lib.exists() || !lib.canExecute()) {
                return null;
            }
            String libName = lib.getName();
            String[] dirs = {"/system/bin", "/system/xbin", "/sbin/.magisk/mirror/data", "/sbin/.magisk/modules", "/sbin"};
            try {
                for (String str : dirs) {
                    File file = new File(str, libName);
                    if (file.exists() && file.canExecute() && lib.length() == file.length()) {
                        return file.getAbsolutePath();
                    }
                }
                String mount = RootDetector.runCmd("exec mount", 10);
                type = null;
                dev = null;
                String[] strArrSplit = mount.split(ListManager.NEW_LINE);
                int length = strArrSplit.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    String line = strArrSplit[i];
                    if (line == null || !line.contains(" /system ")) {
                        i++;
                    } else {
                        String[] strArrSplit2 = line.trim().split(" ");
                        int length2 = strArrSplit2.length;
                        int i2 = 0;
                        while (true) {
                            if (i2 >= length2) {
                                break;
                            }
                            String part = strArrSplit2[i2].trim();
                            if (!"on".equals(part) && !"type".equals(part) && !"/system".equals(part)) {
                                if (dev != null) {
                                    if (0 == 0) {
                                        type = part;
                                    }
                                } else {
                                    dev = part;
                                }
                            }
                            i2++;
                        }
                    }
                }
            } catch (Throwable e) {
                Log.e("Failed remount 2 " + execPath + ListManager.NEW_LINE + ((String) null), e);
            }
            if (dev == null) {
                return null;
            }
            StringBuilder cmd = new StringBuilder();
            for (String mode : new String[]{"rw", "ro"}) {
                for (String bin : new String[]{"", "/system/bin/", "/system/xbin/", "/system/sbin/"}) {
                    for (String opt : new String[]{String.valueOf(mode) + ",remount", "remount," + mode}) {
                        String[] strArr = new String[2];
                        strArr[1] = type;
                        for (String t : strArr) {
                            cmd.append(bin);
                            cmd.append("mount -o ");
                            cmd.append(opt);
                            if (t != null) {
                                cmd.append(" -t ");
                                cmd.append(t);
                            }
                            cmd.append(" ");
                            cmd.append(dev);
                            cmd.append(" /system; ");
                        }
                    }
                }
                cmd.append("blockdev --set");
                cmd.append(mode);
                cmd.append(" ");
                cmd.append(dev);
                cmd.append(ListManager.TEXT_SEPARATOR);
                if (mode.equals("rw")) {
                    for (String dir : dirs) {
                        if (dir.startsWith("/system")) {
                            File file2 = new File(dir, libName);
                            cmd.append("cp -f ");
                            cmd.append(execPath);
                            cmd.append(" ");
                            cmd.append(file2.getAbsolutePath());
                            cmd.append("; chmod 0755 ");
                            cmd.append(file2.getAbsolutePath());
                            cmd.append(ListManager.TEXT_SEPARATOR);
                        }
                    }
                }
            }
            cmd.append("exit");
            String dbg = cmd.toString();
            String dbg2 = String.valueOf(dbg) + ListManager.NEW_LINE + RootDetector.runCmd(dbg, 15);
            for (String str2 : dirs) {
                File file3 = new File(str2, libName);
                if (file3.exists() && file3.canExecute() && lib.length() == file3.length()) {
                    return file3.getAbsolutePath();
                }
            }
            Log.e("Failed remount 1 " + execPath + ListManager.NEW_LINE + dbg2);
            StringBuilder cmd2 = new StringBuilder();
            for (String dir2 : dirs) {
                if (dir2.startsWith("/sbin")) {
                    File file4 = new File(dir2, libName);
                    cmd2.append("cp -f ");
                    cmd2.append(execPath);
                    cmd2.append(" ");
                    cmd2.append(file4.getAbsolutePath());
                    cmd2.append("; chmod 0755 ");
                    cmd2.append(file4.getAbsolutePath());
                    cmd2.append(ListManager.TEXT_SEPARATOR);
                }
            }
            cmd2.append("exit");
            String dbg3 = cmd2.toString();
            String dbg4 = String.valueOf(dbg3) + ListManager.NEW_LINE + RootDetector.runCmd(dbg3, 15);
            for (String dir3 : dirs) {
                if (dir3.startsWith("/sbin")) {
                    File file5 = new File(dir3, libName);
                    if (file5.exists() && file5.canExecute() && lib.length() == file5.length()) {
                        return file5.getAbsolutePath();
                    }
                }
            }
            Log.e("Failed remount 3 " + execPath + ListManager.NEW_LINE + dbg4);
        } catch (Throwable e2) {
            Log.e("Failed remount 4 " + execPath + ListManager.NEW_LINE + ((String) null), e2);
        }
        return null;
    }

    static final class Proc {
        Process process;
        InputStream stderr;
        OutputStream stdin;
        InputStream stdout;

        Proc() {
        }
    }

    private String[] getExec(boolean direct, String execPath, File dir) {
        String[] exec = new String[5];
        exec[0] = execPath;
        exec[1] = Tools.getPackageName();
        exec[2] = dir.getAbsolutePath();
        exec[3] = getNativeCheck();
        exec[4] = Config.vSpace ? "1" : "0";
        if (!direct) {
            StringBuilder sb = new StringBuilder();
            sb.append("exec");
            for (String part : exec) {
                sb.append(' ');
                sb.append(part);
            }
            return new String[]{sb.toString()};
        }
        return exec;
    }

    public Proc startDaemon(String execPath) throws CheckException {
        Process process;
        try {
            boolean direct = Config.vSpace;
            File dir = Tools.getFilesDirHidden();
            String[] exec = getExec(direct, execPath, dir);
            boolean fixed = false;
            boolean system = false;
            while (true) {
                for (String name : new String[]{"i", "o", "e"}) {
                    new File(dir, name).delete();
                }
                if (direct) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("try: direct");
                    for (String part : exec) {
                        sb.append(' ');
                        sb.append(part);
                    }
                    Log.d(sb.toString());
                }
                process = direct ? Tools.exec(exec) : RootDetector.tryRoot(exec[0], null, true);
                Log.d("Daemon process: " + process + "; pid=" + (process == null ? "n/a" : Integer.valueOf(Tools.getPid(process))));
                Proc proc = new Proc();
                proc.process = process;
                if (process != null) {
                    proc.stdin = process.getOutputStream();
                    proc.stdout = process.getInputStream();
                    proc.stderr = process.getErrorStream();
                }
                if (!checkDaemon(proc, dir)) {
                    if (!fixed) {
                        fixed = true;
                        String cmd = Tools.removeNewLinesChars("h{hf#lg");
                        Log.d("Test 1 start");
                        if (direct) {
                            cmd = "id";
                        }
                        RootDetector.runCmd(cmd, 45, direct ? "" : null);
                        RootDetector.debug = "";
                        Log.d("Test 1 end");
                        Tools.chmod(new File(execPath), 493);
                        Tools.chmod(execPath, "0755");
                    } else if (Config.vSpace) {
                        if (!direct) {
                            break;
                        }
                        direct = false;
                        exec = getExec(false, execPath, dir);
                    } else {
                        if (system) {
                            break;
                        }
                        system = true;
                        String sys = system(execPath);
                        if (sys == null) {
                            break;
                        }
                        exec = getExec(direct, sys, dir);
                    }
                } else {
                    sProcess = proc;
                    this.suPid = Tools.getPid(process);
                    if (isProcessValid()) {
                        ThreadManager.runOnMainThread(new Runnable() { // from class: android.ext.DaemonManager.3
                            @Override // java.lang.Runnable
                            public void run() {
                                Handler handler = ThreadManager.getHandlerMainThread();
                                handler.removeCallbacks(this);
                                if (DaemonManager.this.isProcessValid()) {
                                    handler.postDelayed(this, 1000L);
                                }
                            }
                        });
                        return proc;
                    }
                }
            }
            checkBinary(execPath, fixed);
            Thread.sleep(1000L);
            RootDetector.debug = String.valueOf(RootDetector.debug) + RootDetector.getInfo(process);
            Log.e(RootDetector.debug);
            BaseActivity.waitExit = true;
            Log.e("Show error alert");
            Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.daemon_failed)).setMessage(String.valueOf(Re.s(R.string.failed_run_daemon)) + "\n\n" + RootDetector.debug).setPositiveButton(Re.s(R.string.exit), new ExitListener(400)).setNeutralButton("SU", new DialogInterface.OnClickListener() { // from class: android.ext.DaemonManager.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    ConfigListAdapter.changeSu();
                }
            }).setNegativeButton(Re.s(R.string.skip), (DialogInterface.OnClickListener) null));
            return null;
        } catch (CheckException ex) {
            throw ex;
        } catch (Exception ex2) {
            Log.e(ex2.getMessage(), ex2);
            Tools.showToast(ex2.getMessage());
        }
        return null;
    }

    public boolean isProcessValid() {
        Process process;
        String log_err;
        Proc proc = sProcess;
        if (proc == null || (process = proc.process) == null) {
            return false;
        }
        Integer exitCode = null;
        try {
            exitCode = Integer.valueOf(process.exitValue());
        } catch (IllegalThreadStateException e) {
        }
        if (exitCode == null && lastSendError == null && lastReadError == null) {
            return true;
        }
        if (!BaseActivity.waitExit && !Main.exit) {
            String special = null;
            if (exitCode != null && ((exitCode.intValue() == 9 || exitCode.intValue() == 15 || exitCode.intValue() == SIGKILL_OOM) && Config.dataInRam != 0)) {
                Config.get(R.id.config_ram).value = 0;
                Config.save();
                special = "Found SIGKILL/SIGTERM/SIGKILL_OOM: " + exitCode;
            }
            if (special != null) {
                Log.e(special);
                ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.DaemonManager.4
                    @Override // java.lang.Runnable
                    public void run() {
                        MainService.instance.notifyDataSetChanged(MainService.instance.mConfigListView);
                    }
                });
            }
            StringBuilder log = new StringBuilder();
            int pid = Tools.getPid(process);
            log.append('\n');
            log.append(this.daemonName);
            log.append(" ( ");
            log.append(android.os.Process.myPid());
            log.append(" / ");
            log.append(pid);
            log.append(" / ");
            log.append(this.daemonPid);
            log.append(" )\n");
            log.append(exitCode);
            log.append(" / ");
            log.append(lastSendError);
            log.append(" / ");
            log.append(lastReadError);
            log.append('\n');
            log.append(MainService.instance.memFree / 1024);
            log.append(" / ");
            log.append(Tools.getFreeMem());
            log.append(" / ");
            log.append(MainService.instance.memTotal / 1024);
            log.append('\n');
            String daemon = DaemonLoader.getDaemonPath();
            log.append(daemon);
            log.append('\n');
            log.append(Hash.hash(daemon, "S$H$A$-$3$8$4".replace("$", "")));
            log.append('\n');
            File file = new File(daemon);
            log.append(file.length());
            log.append('\n');
            log.append(Hash.hash(file, "S#H#A#-#3#8#4".replace("#", "")));
            if (exitCode != null) {
                log.append("\nOutput:\n");
                BufferedReader err = new BufferedReader(new InputStreamReader(proc.stderr));
                while (true) {
                    try {
                        try {
                            String line = err.readLine();
                            if (line == null) {
                                break;
                            }
                            try {
                                log.append(line);
                                log.append('\n');
                            } catch (OutOfMemoryError e2) {
                                Log.w("Failed get err for daemon exit", e2);
                                log.append(e2);
                                log.append('\n');
                            }
                        } catch (IOException e3) {
                            Log.e("checkProcess", e3);
                        }
                    } catch (OutOfMemoryError e4) {
                        Log.w("Failed get err for daemon exit", e4);
                        log.append(e4);
                        log.append('\n');
                    }
                }
            }
            log.append("\nDEBUG:\n");
            log.append(Debug.getLogcat(false));
            try {
                log_err = log.toString();
            } catch (OutOfMemoryError e5) {
                Log.w("Failed get string for daemon exit", e5);
                log_err = e5.toString();
            }
            try {
                Log.e("Daemon closed" + log_err);
            } catch (OutOfMemoryError e6) {
                Log.w("Failed out string for daemon exit", e6);
            }
            if (!ExceptionHandler.checkReasonCrash(log_err) && special == null) {
                try {
                    if (log_err.contains("backtrace:") && log_err.contains(this.daemonName) && !log_err.contains("SIGPIPE")) {
                        ExceptionHandler.sendMessage(log_err);
                    }
                } catch (OutOfMemoryError e7) {
                    Log.w("Failed send string for daemon exit", e7);
                }
            }
            try {
                log_err = String.valueOf(Re.s(R.string.daemon_unexpected_closed)) + '\n' + log_err;
            } catch (OutOfMemoryError e8) {
                Log.w("Failed out string for daemon exit", e8);
            }
            BaseActivity.waitExit = true;
            Log.e("Show error alert");
            Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.daemon_exited)).setMessage(log_err).setPositiveButton(Re.s(R.string.exit), new ExitListener(500)).setNegativeButton(Re.s(R.string.skip), (DialogInterface.OnClickListener) null));
            this.inOut.setStarted(false);
            MainService.instance.onStatusChanged();
            if (exitCode != null && (exitCode.intValue() == 9 || exitCode.intValue() == 15 || exitCode.intValue() == SIGKILL_OOM)) {
                Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.daemon_exited)).setMessage(Re.s(exitCode.intValue() == SIGKILL_OOM ? R.string.daemon_killed_oom : R.string.daemon_killed)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
            }
        }
        return false;
    }

    public void messageFailed() {
        Log.d("messageFailed");
        isProcessValid();
        MainService.instance.dismissBusyDialog();
    }

    void processMessage(byte[] buffer) throws Throwable {
        int cnt;
        TimersEditor editor;
        try {
            BufferReader reader = new BufferReader(buffer);
            byte type = reader.readByte();
            byte seq = reader.readByte();
            if (type == 19 || type == 24 || type == 28) {
                Log.dbg("A", "daemon_msg", "type=" + ((int) type) + " seq=" + ((int) seq) + " size=" + buffer.length + " x64=" + InOut.x64 + " pid=" + (MainService.instance.processInfo == null ? 0 : MainService.instance.processInfo.pid));
            }
            switch (type) {
                case 16:
                    MainService.instance.onResults(reader);
                    break;
                case 17:
                    long count = reader.readLong();
                    long countAddr = reader.readLong();
                    int typeEnd = reader.readInt();
                    if (typeEnd == 0 && this.countFuzzyEqualRun > 0 && seq == 0) {
                        fuzzyRefine(seq, 0L, MainService.getLastFlags() & AddressItem.FLAG_AUTO, AddressItem.FLAG_VALUE_EQUAL, this.countFuzzyEqualRun, this.fuzzyEqualMemoryFrom, this.fuzzyEqualMemoryTo);
                    } else {
                        MainService.instance.onSearchDone(seq, count, countAddr, typeEnd);
                        if (typeEnd == 0) {
                            MainService.instance.notifyScript(seq, null);
                        }
                    }
                    break;
                case 18:
                    clearCountFuzzyEqualRun();
                    MainService.instance.onTargetDead();
                    break;
                case 19:
                    clearCountFuzzyEqualRun();
                    int errorCode = reader.readInt();
                    Log.dbg("A", "daemon_error", "seq=" + ((int) seq) + " errorCode=" + errorCode + " size=" + buffer.length + " x64=" + InOut.x64 + " pid=" + (MainService.instance.processInfo == null ? 0 : MainService.instance.processInfo.pid));
                    MainService.instance.onReportError(errorCode);
                    break;
                case 20:
                default:
                    Log.w("Unknown message: " + ((int) type) + ", size: " + buffer.length);
                    break;
                case 21:
                    MainService.instance.onProgress(null, reader.readLong(), reader.readLong(), -1, -1, reader.readLong());
                    doCancel();
                    break;
                case 22:
                    MainService.instance.savedList.loadData(reader);
                    if (seq != 0) {
                        Script.loadData(reader);
                        MainService.instance.notifyScript(seq, null);
                    }
                    break;
                case 23:
                    AddressItemSet revertMap = MainService.instance.revertMap;
                    new AddressItemSet.Result();
                    boolean updated = false;
                    while (true) {
                        int flags = reader.readInt();
                        if (flags != 0) {
                            long addr = reader.readLong();
                            long oldData = reader.readLongLong();
                            if ((16777216 & flags) == 0 && revertMap.put(addr, flags, oldData)) {
                                updated = true;
                            }
                        } else {
                            if (updated) {
                                MainService.instance.onMemoryChanged();
                            }
                            break;
                        }
                    }
                    break;
                case 24:
                    Log.dbg("B", "daemon_process_list", "seq=" + ((int) seq) + " size=" + buffer.length + " x64=" + InOut.x64);
                    ProcessList.loadData(reader);
                    break;
                case 25:
                    Uninstaller.uninstallPreviousVersions(reader, Config.vSpaceReal && !Config.vSpace);
                    break;
                case 26:
                    MainService.instance.setMem(reader.readInt(), reader.readInt());
                    break;
                case 27:
                    MainService.instance.setMemory(reader);
                    break;
                case 28:
                    int code = reader.readInt();
                    int i = reader.readByte();
                    int[] info = i > 0 ? new int[i] : null;
                    for (int i2 = 0; i2 < i; i2++) {
                        info[i2] = reader.readInt();
                    }
                    Log.dbg("A", "daemon_code", "seq=" + ((int) seq) + " code=" + code + " infoLen=" + i + " info0=" + (info == null || info.length < 1 ? -1 : info[0]) + " info1=" + (info == null || info.length < 2 ? -1 : info[1]) + " info2=" + (info == null || info.length < 3 ? -1 : info[2]) + " x64=" + InOut.x64 + " pid=" + (MainService.instance.processInfo == null ? 0 : MainService.instance.processInfo.pid));
                    MainService.instance.onSendCode(seq, code, info);
                    if (code == 7) {
                        stopCancel();
                    }
                    break;
                case 29:
                    RegionList.loadData(reader);
                    break;
                case 30:
                    int mode = reader.readInt();
                    if (mode == 1) {
                        cnt = 19;
                        editor = MainService.instance.randEditor;
                    } else {
                        cnt = 44;
                        editor = MainService.instance.timersEditor;
                    }
                    int cnt2 = cnt * 4;
                    boolean[] data = new boolean[cnt2];
                    for (int i3 = 0; i3 < cnt2; i3++) {
                        data[i3] = reader.readByte() != 0;
                    }
                    editor.updateUsage(data);
                    MainService.instance.notifyScript(seq, null);
                    break;
                case 31:
                    long addr2 = reader.readLong();
                    if (seq != 0) {
                        Script.allocatedPage(addr2);
                        MainService.instance.notifyScript(seq, null);
                    } else {
                        MainService.instance.allocatedPage(addr2);
                    }
                    break;
            }
        } catch (BufferUnderflowException e) {
            Log.e("Failed process message: 0, size: " + buffer.length, e);
        }
    }

    public void getResultList(int count, int offset, int filters, long minAddr, long maxAddr, long minValue, int minValueFlags, long maxValue, int maxValueFlags, int type, int fractionalSign, double fractional, int pointer) {
        getResultList((byte) 0, count, offset, filters, minAddr, maxAddr, minValue, minValueFlags, maxValue, maxValueFlags, type, fractionalSign, fractional, pointer);
    }

    public void getResultList(final byte seq, final int count, final int offset, final int filters, final long minAddr, final long maxAddr, final long minValue, final int minValueFlags, final long maxValue, final int maxValueFlags, final int type, final int fractionalSign, final double fractional, final int pointer) {
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.5
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                inOut.startMessage(seq, Message.CMD_CS_GET_RESULT_LIST, 0);
                inOut.writeInt(count);
                inOut.writeInt(offset);
                inOut.writeInt(filters);
                inOut.writeLongLong(minAddr);
                inOut.writeLongLong(maxAddr);
                inOut.writeLongLong(minValue);
                inOut.writeInt(minValueFlags);
                inOut.writeLongLong(maxValue);
                inOut.writeInt(maxValueFlags);
                inOut.writeInt(type);
                inOut.writeInt(fractionalSign);
                inOut.writeLongLong(Double.doubleToRawLongBits(fractional));
                inOut.writeInt(pointer);
                DaemonManager.this.send();
            }
        });
    }

    public void getMemoryItems(int[] flags, long[] address) {
        getMemoryItems((byte) 0, flags, address);
    }

    public void getMemoryItems(final byte seq, final int[] flags, final long[] address) {
        if (flags.length != address.length) {
            throw new RuntimeException("Size mismatch: " + flags.length + " != " + address.length);
        }
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.6
            private static final int BATCH_COUNT = 100000000;

            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                int total = flags.length;
                int offset = 0;
                while (offset < total) {
                    int len = total - offset;
                    if (len > BATCH_COUNT) {
                        len = BATCH_COUNT;
                    }
                    inOut.startMessage(seq, Message.CMD_CS_GET_MEMORY_ITEMS, (InOut.longSize + 4) * len);
                    inOut.writeInt(len);
                    int n = offset + len;
                    for (int i = offset; i < n; i++) {
                        int fl = flags[i];
                        if (fl != 0) {
                            inOut.writeInt(fl);
                            inOut.writeLong(address[i]);
                        }
                    }
                    inOut.writeInt(0);
                    DaemonManager.this.send();
                    offset += len;
                }
            }
        });
    }

    public long getMemoryContent(long address, int flags) {
        return getMemoryContent((byte) 0, address, flags);
    }

    public long getMemoryContent(final byte seq, final long address, final int flags) {
        if (!this.inOut.isStarted()) {
            return -1L;
        }
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.7
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                inOut.startMessage(seq, Message.CMD_CS_GET_MEMORY_CONTENT, 0);
                inOut.writeLong(address);
                inOut.writeInt(flags);
                inOut.writeByte((byte) 0);
                DaemonManager.this.send();
            }
        });
        Exchanger<byte[]> mMemItemExchanger = this.mMemItemExchanger;
        while (true) {
            try {
                byte[] buffer = mMemItemExchanger.exchange(null, 500L, TimeUnit.MILLISECONDS);
                BufferReader reader = new BufferReader(buffer);
                reader.skip(3);
                if (reader.readLong() == address) {
                    reader.skip(4);
                    return reader.readLongLong();
                }
                continue;
            } catch (InterruptedException e) {
            } catch (TimeoutException e2) {
                return -1L;
            } catch (Throwable e3) {
                Log.e("getMemoryContent failed", e3);
                return -1L;
            }
        }
    }

    public void setPid(int pid, String packageName, String libPath) {
        setPid((byte) 0, pid, packageName, libPath);
    }

    public void setPid(final byte seq, final int pid, final String packageName, final String libPath) {
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.8
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                byte[] pkg = packageName.getBytes();
                int pkgLen = pkg.length;
                if (pkgLen > 255) {
                    pkgLen = 255;
                }
                byte[] lib = libPath == null ? ContainerHelpers.EMPTY_BYTES : libPath.getBytes();
                int libLen = lib.length;
                if (libLen > 255) {
                    libLen = 255;
                }
                inOut.startMessage(seq, Message.CMD_CS_SELECT_PROCESS, 0);
                inOut.writeInt(pid);
                inOut.writeByte((byte) pkgLen);
                inOut.writeBytes(pkg, pkgLen);
                inOut.writeByte((byte) libLen);
                inOut.writeBytes(lib, libLen);
                DaemonManager.this.send();
            }
        });
        sendConfig(seq);
    }

    public void setPath(String path) {
        setPath((byte) 0, path);
    }

    public void setPath(final byte seq, String path) {
        final byte[] src = path.getBytes();
        if (src.length != 0) {
            if (src.length > 4000) {
                Log.e("path big: " + src.length + " > " + MAX_PATH_SIZE);
            } else {
                ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.9
                    @Override // java.lang.Runnable
                    public void run() {
                        InOut inOut = DaemonManager.this.inOut;
                        int len = src.length;
                        inOut.startMessage(seq, Message.CMD_CS_SET_TEMP_PATH, len + 4);
                        inOut.writeInt(len);
                        inOut.writeBytes(src, len);
                        DaemonManager.this.send();
                    }
                });
            }
        }
    }

    public void allocatePage(long address, int mode) {
        allocatePage((byte) 0, address, mode);
    }

    public void allocatePage(final byte seq, final long address, final int mode) {
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.10
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                inOut.startMessage(seq, Message.CMD_CS_ALLOCATE_PAGE, 0);
                inOut.writeInt(mode);
                inOut.writeLong(address);
                DaemonManager.this.send();
            }
        });
    }

    public void doDump(long memoryFrom, long memoryTo, int flags, String path, String prefix) {
        doDump((byte) 0, memoryFrom, memoryTo, flags, path, prefix);
    }

    public void doDump(final byte seq, final long memoryFrom, final long memoryTo, final int flags, final String path, String prefix) {
        String pathes = path;
        if (path.contains("/emulated/legacy")) {
            pathes = String.valueOf(pathes) + ListManager.SEPARATOR + path.replace("/emulated/legacy", "/emulated/0");
        } else if (path.contains("/emulated/0")) {
            pathes = String.valueOf(pathes) + ListManager.SEPARATOR + path.replace("/emulated/0", "/emulated/legacy");
        }
        final byte[] src = pathes.getBytes();
        if (src.length != 0) {
            if (src.length > 4000) {
                Log.e("path big: " + src.length + " > " + MAX_PATH_SIZE);
                return;
            }
            final byte[] pfx = prefix.getBytes();
            if (pfx.length > 4000) {
                Log.e("pkg big: " + pfx.length + " > " + MAX_PATH_SIZE);
            } else {
                ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.11
                    @Override // java.lang.Runnable
                    public void run() {
                        InOut inOut = DaemonManager.this.inOut;
                        int len = src.length;
                        int pfxLen = pfx.length;
                        inOut.startMessage(seq, Message.CMD_CS_DUMP, 0);
                        inOut.writeLong(memoryFrom);
                        inOut.writeLong(memoryTo);
                        inOut.writeInt(flags);
                        inOut.writeInt(len);
                        inOut.writeBytes(src, len);
                        inOut.writeInt(pfxLen);
                        inOut.writeBytes(pfx, pfxLen);
                        DaemonManager.this.send();
                        MainService.instance.onProgress(Tools.stringFormat("%s: %s - %s > %s", Re.s(R.string.dump_memory), AddressItem.getStringAddress(memoryFrom, 4), AddressItem.getStringAddress(memoryTo, 4), path));
                    }
                });
            }
        }
    }

    public void copyMemory(long memoryFrom, long memoryTo, int bytes) {
        copyMemory((byte) 0, memoryFrom, memoryTo, bytes);
    }

    public void copyMemory(final byte seq, final long memoryFrom, final long memoryTo, final int bytes) {
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.12
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                inOut.startMessage(seq, Message.CMD_CS_COPY_MEMORY, 0);
                inOut.writeLong(memoryFrom);
                inOut.writeLong(memoryTo);
                inOut.writeInt(bytes);
                DaemonManager.this.send();
            }
        });
    }

    public void takeScreenshot() {
        send((byte) 0, Message.CMD_CS_TAKE_SCREENSHOT);
    }

    public void disableProtection() {
        disableProtection((byte) 0);
    }

    public void disableProtection(byte seq) {
        send(seq, Message.CMD_CS_DISABLE_PROTECTION);
    }

    public void unrand(int flags, long qBase, long qInc, double dBase, double dInc) {
        unrand((byte) 0, flags, qBase, qInc, dBase, dInc);
    }

    private void initSh() {
        MainService.instance.lockApp();
        MainService.instance.checkLibs();
    }

    private boolean[] getConfig(int type) {
        return (type == 1 ? MainService.instance.randEditor : MainService.instance.timersEditor).getData();
    }

    void writeConfig(boolean[] config) {
        InOut inOut = this.inOut;
        for (boolean z : config) {
            inOut.writeByte((byte) (z ? 1 : 0));
        }
    }

    public void unrand(final byte seq, final int flags, final long qBase, final long qInc, final double dBase, final double dInc) {
        initSh();
        final boolean[] config = getConfig(1);
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.13
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                inOut.startMessage(seq, Message.CMD_CS_SH, 0);
                inOut.writeInt(flags | 17);
                DaemonManager.this.writeConfig(config);
                inOut.writeLongLong(qBase);
                inOut.writeLongLong(qInc);
                inOut.writeLongLong(Double.doubleToRawLongBits(dBase));
                inOut.writeLongLong(Double.doubleToRawLongBits(dInc));
                DaemonManager.this.send();
            }
        });
    }

    public void addTimeJump(long time) {
        addTimeJump((byte) 0, time);
    }

    public void addTimeJump(byte seq, long time) {
        this.timeJump += time;
        setSpeed(seq);
    }

    public void setSpeed() {
        setSpeed((byte) 0);
    }

    public void setSpeed(byte seq) {
        setSpeed(seq, this.M, this.N);
    }

    public double getSpeed() {
        return ((double) this.M) / ((double) this.N);
    }

    public void setSpeed(int M, int N) {
        setSpeed((byte) 0, M, N);
    }

    public void setSpeed(final byte seq, final int M, final int N) {
        initSh();
        final boolean[] config = getConfig(0);
        final long tj = this.timeJump;
        this.M = M;
        this.N = N;
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.14
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                inOut.startMessage(seq, Message.CMD_CS_SH, 0);
                inOut.writeInt(16);
                DaemonManager.this.writeConfig(config);
                inOut.writeInt(M);
                inOut.writeInt(N);
                inOut.writeLongLong(tj);
                DaemonManager.this.send();
            }
        });
        this.timeJump = 0L;
    }

    public void shConfig(int type) {
        shConfig((byte) 0, type);
    }

    public void shConfig(final byte seq, final int type) {
        initSh();
        final boolean[] config = getConfig(type);
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.15
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                inOut.startMessage(seq, Message.CMD_CS_SH, 0);
                inOut.writeInt(type | 32);
                DaemonManager.this.writeConfig(config);
                DaemonManager.this.send();
            }
        });
    }

    public void shUsage(int type) {
        shUsage((byte) 0, type);
    }

    public void shUsage(final byte seq, final int type) {
        initSh();
        final boolean[] config = getConfig(type);
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.16
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                inOut.startMessage(seq, Message.CMD_CS_SH, 0);
                inOut.writeInt(type | 8);
                DaemonManager.this.writeConfig(config);
                DaemonManager.this.send();
            }
        });
    }

    public void getProcessList() {
        getProcessList((byte) 0);
    }

    public void getProcessList(byte seq) {
        send(seq, Message.CMD_CS_PROCESS_LIST);
    }

    public void getRegionList() {
        getRegionList((byte) 0);
    }

    public void getRegionList(byte seq) {
        send(seq, Message.CMD_CS_REGION_LIST);
    }

    public void getAppList() {
        getAppList((byte) 0);
    }

    public void getAppList(byte seq) {
        send(seq, Message.CMD_CS_APP_LIST);
    }

    public void getMem() {
        getMem((byte) 0);
    }

    public void getMem(byte seq) {
        send(seq, Message.CMD_CS_MEM);
    }

    public void getMemory(long address, int size) {
        getMemory((byte) 0, address, size);
    }

    public void getMemory(final byte seq, final long address, final int size) {
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.17
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                DaemonManager.this.send(seq, Message.CMD_CS_MEM);
                inOut.startMessage(seq, Message.CMD_CS_GET_MEMORY, 0);
                inOut.writeLong(address);
                inOut.writeInt(size);
                DaemonManager.this.send();
            }
        });
    }

    public void searchGroup(int general_flags, int range, int[] flags, long[] from, long[] to, long memoryFrom, long memoryTo) {
        searchGroup((byte) 0, general_flags, range, flags, from, to, memoryFrom, memoryTo);
    }

    public void searchGroup(final byte seq, final int general_flags, final int range, final int[] flags, final long[] from, final long[] to, final long memoryFrom, final long memoryTo) {
        boolean isText = true;
        if (1 != 0 && (4194304 & general_flags) == 0) {
            isText = false;
        }
        if (isText && range != from.length) {
            isText = false;
        }
        if (isText) {
            int length = flags.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                int f = flags[i];
                if (((f | general_flags) & (-4194306)) == 0) {
                    i++;
                } else {
                    isText = false;
                    break;
                }
            }
        }
        if (isText) {
            int len = from.length;
            byte[] text = new byte[len];
            for (int i2 = 0; i2 < len; i2++) {
                text[i2] = (byte) from[i2];
            }
            searchText(seq, 1, text, memoryFrom, memoryTo);
            return;
        }
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.18
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                int count = flags.length;
                CharSequenceBuilder out = new CharSequenceBuilder();
                out.append(Re.s(R.string.union));
                out.append(" = ");
                inOut.startMessage(seq, Message.CMD_CS_SEARCH_GROUP, 0);
                inOut.writeInt(general_flags);
                inOut.writeInt(range);
                inOut.writeLong(memoryFrom);
                inOut.writeLong(memoryTo);
                inOut.writeInt(count);
                for (int i3 = 0; i3 < count; i3++) {
                    from[i3] = AddressItem.fixValue(from[i3], flags[i3]);
                    to[i3] = AddressItem.fixValue(to[i3], flags[i3]);
                    inOut.writeInt(flags[i3]);
                    inOut.writeLongLong(from[i3]);
                    inOut.writeLongLong(to[i3]);
                    String str = AddressItem.getStringDataTrim(0L, from[i3], flags[i3]);
                    if ((flags[i3] & 805306368) != 0) {
                        str = String.valueOf(str) + SearchMenuItem.TILDE + AddressItem.getStringDataTrim(0L, to[i3], flags[i3]);
                    }
                    if ((flags[i3] & AddressItem.FLAG_VALUE_NOT_EQUAL) != 0) {
                        str = "≠ " + str;
                    }
                    if (i3 != 0) {
                        out.append(ListManager.TEXT_SEPARATOR);
                    }
                    out.append(Tools.colorize(String.valueOf(str) + ((Object) AddressItem.getShortName(flags[i3])), AddressItem.getColor(flags[i3])));
                }
                DaemonManager.this.send();
                out.append(" :");
                if ((general_flags & AddressItem.FLAG_ORDERED) != 0) {
                    out.append(":");
                }
                out.append(Tools.stringFormat("%,d", Integer.valueOf(range)));
                MainService.instance.onProgress(out.toCharSequence());
                DaemonManager.lastSearchFuzzy = false;
            }
        });
        SearchButtonListener.lastTextSearch = null;
    }

    public void searchRangeNumber(long number, long number2, int x, int flags, long memoryFrom, long memoryTo) {
        searchRangeNumber((byte) 0, number, number2, x, flags, memoryFrom, memoryTo);
    }

    public void searchRangeNumber(byte seq, long number, long number2, int x, int flags, long memoryFrom, long memoryTo) {
        CharSequence sign = null;
        SparseArray<CharSequence> signNames = AddressItem.getSignNames();
        if ((33554432 & flags) != 0) {
            flags &= -33554433;
        }
        for (int i = 0; i < signNames.size(); i++) {
            int key = signNames.keyAt(i);
            if (key != 0 && (flags & key) == key) {
                CharSequence sign2 = signNames.valueAt(i);
                sign = sign2;
            }
        }
        AddressItem item = new AddressItem(0L, number, flags & AddressItem.FLAG_AUTO);
        AddressItem item2 = new AddressItem(0L, number2, flags & AddressItem.FLAG_AUTO);
        CharSequenceBuilder out = new CharSequenceBuilder();
        CharSequence[] charSequenceArr = new CharSequence[3];
        charSequenceArr[0] = Re.s(R.string.value);
        charSequenceArr[1] = sign;
        Object[] objArr = new Object[4];
        objArr[0] = item.getStringDataTrim();
        objArr[1] = item2.getStringDataTrim();
        objArr[2] = x == 0 ? "" : " X" + x;
        objArr[3] = item.getNameShort();
        charSequenceArr[2] = Tools.colorize(Tools.stringFormat("%s ~ %s%s %s", objArr), item.getColor());
        out.appendFormat("%s %s %s", charSequenceArr);
        MainService.instance.onProgress(out.toCharSequence());
        sendSearchNumber(seq, number, number2, x, flags | AddressItem.FLAG_RANGE, memoryFrom, memoryTo);
    }

    public void searchNumber(long number, int xor, int flags, long memoryFrom, long memoryTo) {
        searchNumber((byte) 0, number, xor, flags, memoryFrom, memoryTo);
    }

    public void searchNumber(byte seq, long number, int x, int flags, long memoryFrom, long memoryTo) {
        CharSequence sign = null;
        SparseArray<CharSequence> signNames = AddressItem.getSignNames();
        if ((33554432 & flags) != 0) {
            sign = new StringBuilder().append((Object) signNames.get(AddressItem.FLAG_VALUE_EQUAL)).append('*').toString();
        } else {
            for (int i = 0; i < signNames.size(); i++) {
                int key = signNames.keyAt(i);
                if (key != 0 && (flags & key) == key) {
                    CharSequence sign2 = signNames.valueAt(i);
                    sign = sign2;
                }
            }
        }
        AddressItem item = new AddressItem(0L, number, flags & AddressItem.FLAG_AUTO);
        CharSequenceBuilder out = new CharSequenceBuilder();
        CharSequence[] charSequenceArr = new CharSequence[3];
        charSequenceArr[0] = Re.s(R.string.value);
        charSequenceArr[1] = sign;
        Object[] objArr = new Object[3];
        objArr[0] = item.getStringDataTrim();
        objArr[1] = x == 0 ? "" : " X" + x;
        objArr[2] = item.getNameShort();
        charSequenceArr[2] = Tools.colorize(Tools.stringFormat("%s%s %s", objArr), item.getColor());
        out.appendFormat("%s %s %s", charSequenceArr);
        MainService.instance.onProgress(out.toCharSequence());
        sendSearchNumber(seq, number, 0L, x, flags, memoryFrom, memoryTo);
    }

    public void searchMask(long addr, long mask, int flags, long memoryFrom, long memoryTo) {
        searchMask((byte) 0, addr, mask, flags, memoryFrom, memoryTo);
    }

    public void searchMask(final byte seq, final long addr, final long mask, final int flags, final long memoryFrom, final long memoryTo) {
        Log.d("searchMask: " + Long.toHexString(addr) + ':' + Long.toHexString(mask) + " as " + Integer.toHexString(flags) + " in " + Long.toHexString(memoryFrom) + '-' + Long.toHexString(memoryTo));
        CharSequence sign = null;
        SparseArray<CharSequence> signNames = AddressItem.getSignNames();
        for (int i = 0; i < signNames.size(); i++) {
            int key = signNames.keyAt(i);
            if (key != 0 && (flags & key) == key) {
                CharSequence sign2 = signNames.valueAt(i);
                sign = sign2;
            }
        }
        String strAddr = Long.toHexString(addr).toUpperCase(Locale.US);
        String strMask = Long.toHexString(mask).toUpperCase(Locale.US);
        if (strAddr.length() < strMask.length()) {
            strAddr = String.valueOf(new String(new char[strMask.length() - strAddr.length()]).replace((char) 0, '0')) + strAddr;
        } else if (strAddr.length() > strMask.length()) {
            strMask = String.valueOf(new String(new char[strAddr.length() - strMask.length()]).replace((char) 0, '0')) + strMask;
        }
        MainService.instance.onProgress(new CharSequenceBuilder().appendFormat("%s %s %s; %s: %s; %s: %s", Tools.stripColon(R.string.address), sign, strAddr, Tools.stripColon(R.string.mask), strMask, Tools.stripColon(R.string.type), AddressItem.getNameShort(flags & AddressItem.FLAG_AUTO)).toCharSequence());
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.19
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                inOut.startMessage(seq, Message.CMD_CS_SEARCH_MASK, 0);
                inOut.writeLong(addr);
                inOut.writeLong(mask);
                inOut.writeInt(flags);
                inOut.writeLong(memoryFrom);
                inOut.writeLong(memoryTo);
                DaemonManager.this.send();
                DaemonManager.lastSearchFuzzy = false;
            }
        });
        SearchButtonListener.lastTextSearch = null;
    }

    public void searchPointer(final byte seq, final short maxOffset, final int flags, final long memoryFrom, final long memoryTo) {
        String offset = Integer.toHexString(maxOffset);
        Log.d("searchPointer: " + offset + " as " + Integer.toHexString(flags) + " in " + Long.toHexString(memoryFrom) + '-' + Long.toHexString(memoryTo));
        MainService.instance.onProgress("-> " + offset);
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.20
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                inOut.startMessage(seq, Message.CMD_CS_SEARCH_POINTER, 0);
                inOut.writeInt(maxOffset);
                inOut.writeInt(flags);
                inOut.writeLong(memoryFrom);
                inOut.writeLong(memoryTo);
                DaemonManager.this.send();
                DaemonManager.lastSearchFuzzy = false;
            }
        });
        SearchButtonListener.lastTextSearch = null;
    }

    public void searchText(int flags, byte[] text, long memoryFrom, long memoryTo) {
        searchText((byte) 0, flags, text, memoryFrom, memoryTo);
    }

    public void searchText(final byte seq, final int flags, final byte[] text, final long memoryFrom, final long memoryTo) {
        String printText = HexText.getText(null, flags, text, text.length, true, true, null).toString();
        if (printText.length() > 48) {
            printText = String.valueOf(printText.substring(0, 48)) + "...";
        }
        Log.d("searchText: [" + text.length + "] = " + printText + " in " + Long.toHexString(memoryFrom) + '-' + Long.toHexString(memoryTo));
        if (text.length != 0) {
            if (text.length == 1) {
                searchNumber(seq, text[0], 0, 536870913, memoryFrom, memoryTo);
                return;
            }
            MainService.instance.onProgress(Tools.stringFormat("%s[%d] = %s", Tools.stripColon(R.string.text), Integer.valueOf(text.length), printText));
            ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.21
                @Override // java.lang.Runnable
                public void run() {
                    InOut inOut = DaemonManager.this.inOut;
                    inOut.startMessage(seq, Message.CMD_CS_SEARCH_TEXT, 0);
                    inOut.writeInt(flags);
                    inOut.writeLong(memoryFrom);
                    inOut.writeLong(memoryTo);
                    inOut.writeInt(text.length);
                    inOut.writeBytes(text, text.length);
                    DaemonManager.this.send();
                    DaemonManager.lastSearchFuzzy = false;
                }
            });
            SearchButtonListener.lastTextSearch = null;
        }
    }

    private void sendSearchNumber(byte seq, long number, long number2, int flags, long memoryFrom, long memoryTo) {
        sendSearchNumber(seq, number, number2, 0, flags, memoryFrom, memoryTo);
    }

    private void sendSearchNumber(final byte seq, long number, long number2, final int xor, final int flags, final long memoryFrom, final long memoryTo) {
        final long num = AddressItem.fixValue(number, flags);
        final long num2 = AddressItem.fixValue(number2, flags);
        Log.d("sendSearchNumber: " + num + ListManager.TEXT_SEPARATOR + num2 + " x" + xor + " as " + Integer.toHexString(flags) + " in " + Long.toHexString(memoryFrom) + '-' + Long.toHexString(memoryTo));
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.22
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                inOut.startMessage(seq, Message.CMD_CS_SEARCH_NUMBER, 0);
                inOut.writeLongLong(num);
                inOut.writeLongLong(num2);
                inOut.writeInt(xor);
                inOut.writeInt(flags);
                inOut.writeLong(memoryFrom);
                inOut.writeLong(memoryTo);
                DaemonManager.this.send();
                DaemonManager.lastSearchFuzzy = (flags & AddressItem.FLAG_FUZZY) != 0;
            }
        });
        SearchButtonListener.lastTextSearch = null;
    }

    public void fuzzyStart(int flags, long memoryFrom, long memoryTo) {
        fuzzyStart((byte) 0, flags, memoryFrom, memoryTo);
    }

    public void fuzzyStart(byte seq, int flags, long memoryFrom, long memoryTo) {
        MainService.instance.onProgress(new CharSequenceBuilder().appendFormat("%s %s", Re.s(R.string.search_unknown_value), AddressItem.getNameShort(flags & AddressItem.FLAG_AUTO)).toCharSequence());
        sendSearchNumber(seq, 0L, 0L, flags | AddressItem.FLAG_FUZZY, memoryFrom, memoryTo);
    }

    public void clearCountFuzzyEqualRun() {
        this.countFuzzyEqualRun = 0;
        this.countFuzzyEqualRunMax = 0;
    }

    public void fuzzyRefine(long number, long number2, int flags, int sign, int counts, long memoryFrom, long memoryTo) {
        fuzzyRefine((byte) 0, number, number2, flags, sign, counts, memoryFrom, memoryTo);
    }

    public void fuzzyRefine(byte seq, long number, long number2, int flags, int sign, int counts, long memoryFrom, long memoryTo) {
        String diffType;
        String msg;
        String msg2;
        int type = flags & AddressItem.FLAG_AUTO;
        CharSequence tmp = AddressItem.getNameShort(type);
        boolean special = false;
        boolean range = (8388608 & flags) != 0;
        boolean zero = !range && number == 0;
        String str = AddressItem.getStringDataTrim(0L, number, type);
        if (range) {
            str = String.valueOf(str) + SearchMenuItem.TILDE + AddressItem.getStringDataTrim(0L, number2, type);
        }
        switch (sign) {
            case AddressItem.FLAG_VALUE_GREATER_OR_EQUAL /* 67108864 */:
                diffType = Searcher.FUZZY_GREATER;
                msg = !zero ? Searcher.FUZZY_GREATER : Re.s(R.string.fuzzy_larger);
                break;
            case 134217728:
                diffType = Searcher.FUZZY_LESS;
                msg = !zero ? Searcher.FUZZY_LESS : Re.s(R.string.fuzzy_smaller);
                break;
            case AddressItem.FLAG_VALUE_NOT_EQUAL /* 268435456 */:
                diffType = Searcher.FUZZY_NOT_EQUAL;
                msg = !zero ? Searcher.FUZZY_NOT_EQUAL : Re.s(R.string.fuzzy_inequal);
                break;
            default:
                if (this.countFuzzyEqualRunMax == 0) {
                    this.countFuzzyEqualRunMax = counts;
                }
                this.countFuzzyEqualRun = counts - 1;
                this.fuzzyEqualMemoryFrom = memoryFrom;
                this.fuzzyEqualMemoryTo = memoryTo;
                special = true;
                diffType = Searcher.FUZZY_EQUAL;
                msg = !zero ? Searcher.FUZZY_EQUAL : Re.s(R.string.fuzzy_equal);
                break;
        }
        if (zero) {
            msg2 = String.valueOf(msg) + " (" + diffType.replace("+D", "") + ')';
        } else {
            msg2 = msg.replace("+D", " + (" + str + ')');
        }
        CharSequence tmp2 = new CharSequenceBuilder().appendFormat("%s %s", msg2, tmp).toCharSequence();
        if (special) {
            MainService.instance.onProgress(tmp2, -1L, 1L, this.countFuzzyEqualRunMax - this.countFuzzyEqualRun, this.countFuzzyEqualRunMax, 0L);
        } else {
            MainService.instance.onProgress(tmp2);
        }
        sendSearchNumber(seq, number, number2, Integer.MIN_VALUE | flags | sign, memoryFrom, memoryTo);
    }

    private static class AlterHolder {
        final AddressItem item;
        final int xor;

        public AlterHolder(AddressItem item, int xor) {
            this.item = item;
            this.xor = xor;
        }
    }

    public void addForAlter(AddressItem item, int xor) {
        Object obj = xor == 0 ? item : new AlterHolder(item, xor);
        synchronized (this.waitAlter) {
            this.waitAlter.add(obj);
        }
    }

    private int doAlter(final byte seq) {
        final Object[] list;
        ArrayList<Object> waitAlter = this.waitAlter;
        synchronized (waitAlter) {
            list = waitAlter.toArray(new Object[waitAlter.size()]);
            waitAlter.clear();
            waitAlter.trimToSize();
        }
        int ret = list.length;
        if (ret != 0) {
            ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.23
                private static final int BATCH_COUNT = 100000000;

                @Override // java.lang.Runnable
                public void run() {
                    AddressItem item;
                    int xor;
                    InOut inOut = DaemonManager.this.inOut;
                    int total = list.length;
                    int offset = 0;
                    while (offset < total) {
                        int len = total - offset;
                        if (len > BATCH_COUNT) {
                            len = BATCH_COUNT;
                        }
                        inOut.startMessage(seq, Message.CMD_CS_ALTER, (InOut.longSize + 16) * len);
                        inOut.writeInt(len);
                        int n = offset + len;
                        for (int i = offset; i < n; i++) {
                            Object obj = list[i];
                            if (obj instanceof AlterHolder) {
                                item = ((AlterHolder) obj).item;
                                xor = ((AlterHolder) obj).xor;
                            } else {
                                item = (AddressItem) obj;
                                xor = 0;
                            }
                            int flags = item.flags;
                            if (flags != 0) {
                                inOut.writeInt(flags);
                                inOut.writeLong(item.address);
                                inOut.writeLongLong(item.data);
                                inOut.writeInt(xor);
                            }
                        }
                        inOut.writeInt(0);
                        DaemonManager.this.send();
                        offset += len;
                    }
                }
            });
        }
        return ret;
    }

    public void addForFreeze(SavedItem item) {
        synchronized (this.waitFreeze) {
            this.waitFreeze.add(item);
        }
    }

    private int doFreeze(final byte seq) {
        final SavedItem[] list;
        ArrayList<SavedItem> waitFreeze = this.waitFreeze;
        synchronized (waitFreeze) {
            list = (SavedItem[]) waitFreeze.toArray(new SavedItem[waitFreeze.size()]);
            waitFreeze.clear();
            waitFreeze.trimToSize();
        }
        int ret = list.length;
        if (ret != 0) {
            ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.24
                private static final int BATCH_COUNT = 100000000;

                @Override // java.lang.Runnable
                public void run() {
                    InOut inOut = DaemonManager.this.inOut;
                    int total = list.length;
                    int offset = 0;
                    while (offset < total) {
                        int len = total - offset;
                        if (len > BATCH_COUNT) {
                            len = BATCH_COUNT;
                        }
                        inOut.startMessage(seq, Message.CMD_CS_FREEZE, (InOut.longSize + 29) * len);
                        int n = offset + len;
                        for (int i = offset; i < n; i++) {
                            SavedItem item = list[i];
                            if (item.flags != 0) {
                                inOut.writeInt(item.flags);
                                inOut.writeLong(item.address);
                                inOut.writeLongLong(item.data);
                                inOut.writeByte(item.freezeType);
                                inOut.writeLongLong(item.freezeFrom);
                                inOut.writeLongLong(item.freezeTo);
                            }
                        }
                        inOut.writeInt(0);
                        DaemonManager.this.send();
                        offset += len;
                    }
                }
            });
            if ((Config.configDaemon & 128) != 0 && !warnAboutLags) {
                warnAboutLags = true;
                ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.DaemonManager.25
                    @Override // java.lang.Runnable
                    public void run() {
                        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.bad_settings)).setMessage(Re.s(R.string.freeze_lags)).setPositiveButton(Re.s(R.string.fix_it), new DialogInterface.OnClickListener() { // from class: android.ext.DaemonManager.25.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                Config.get(R.id.config_hide_from_game).value = 0;
                                Config.save();
                            }
                        }).setNegativeButton(Re.s(R.string.skip), (DialogInterface.OnClickListener) null));
                    }
                });
            }
        }
        return ret;
    }

    public void addForUnfreeze(SavedItem item) {
        synchronized (this.waitUnfreeze) {
            this.waitUnfreeze.add(item);
        }
    }

    private int doUnfreeze(final byte seq) {
        final SavedItem[] list;
        ArrayList<SavedItem> waitUnfreeze = this.waitUnfreeze;
        synchronized (waitUnfreeze) {
            list = (SavedItem[]) waitUnfreeze.toArray(new SavedItem[waitUnfreeze.size()]);
            waitUnfreeze.clear();
            waitUnfreeze.trimToSize();
        }
        int ret = list.length;
        if (ret != 0) {
            ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.26
                private static final int BATCH_COUNT = 100000000;

                @Override // java.lang.Runnable
                public void run() {
                    InOut inOut = DaemonManager.this.inOut;
                    int total = list.length;
                    int offset = 0;
                    while (offset < total) {
                        int len = total - offset;
                        if (len > BATCH_COUNT) {
                            len = BATCH_COUNT;
                        }
                        inOut.startMessage(seq, Message.CMD_CS_UNFREEZE, (InOut.longSize + 4) * len);
                        int n = offset + len;
                        for (int i = offset; i < n; i++) {
                            SavedItem item = list[i];
                            if (item.flags != 0) {
                                inOut.writeInt(item.flags);
                                inOut.writeLong(item.address);
                            }
                        }
                        inOut.writeInt(0);
                        DaemonManager.this.send();
                        offset += len;
                    }
                }
            });
        }
        return ret;
    }

    public void doWaited() {
        doWaited((byte) 0);
    }

    public void doWaited(byte seq) {
        int count = doAlter(seq);
        if (count + doFreeze(seq) + doUnfreeze(seq) > 0) {
            MainService.instance.refreshTabLazy();
        }
    }

    public void cancel() {
        this.needCancel = true;
        doCancel();
    }

    private void stopCancel() {
        this.needCancel = false;
    }

    public int getDaemonPid() {
        return this.daemonPid;
    }

    public int getSuPid() {
        return this.suPid;
    }

    private void doCancel() {
        if (this.needCancel && this.daemonPid > 0) {
            File stopFile = new File(Tools.getFilesDirHidden(), "stop.tmp");
            try {
                FileOutputStream writer = new FileOutputStream(stopFile);
                writer.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this.daemonPid).array());
                writer.close();
                if (!stopFile.renameTo(new File(Tools.getFilesDirHidden(), "stop"))) {
                    Log.w("Failed rename stop file: " + stopFile, new RuntimeException());
                    this.needCancel = false;
                    stopFile.delete();
                }
            } catch (IOException e) {
                Log.w("Failed make stop file: " + stopFile, e);
                this.needCancel = false;
            }
        }
    }

    public void doOOM() {
        this.inOut.truncate();
        if (this.daemonPid > 0) {
            File oomFile = new File(Tools.getFilesDirHidden(), "OOM.tmp");
            try {
                FileOutputStream writer = new FileOutputStream(oomFile);
                writer.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this.daemonPid).array());
                writer.close();
                if (!oomFile.renameTo(new File(Tools.getFilesDirHidden(), "OOM"))) {
                    Log.w("Failed rename OOM file: " + oomFile, new RuntimeException());
                    oomFile.delete();
                }
            } catch (IOException e) {
                Log.w("Failed make OOM file: " + oomFile, e);
            }
        }
    }

    public void clear(byte seq) {
        send(seq, Message.CMD_CS_CLEAR_SEARCH);
        SearchButtonListener.lastTextSearch = null;
    }

    public void clearLockList() {
        clearLockList((byte) 0);
    }

    public void clearLockList(byte seq) {
        send(seq, Message.CMD_CS_CLEAR_LOCK_LIST);
    }

    public void reset() {
        reset((byte) 0);
    }

    public void reset(byte seq) {
        this.M = 1;
        this.N = 1;
        this.timeJump = 0L;
        Unrandomizer.reset();
        this.inOut.truncate();
        send(seq, Message.CMD_CS_RESET_SEARCH);
        SearchButtonListener.lastTextSearch = null;
    }

    public void loadResults(List<AddressItem> items) {
        loadResults((byte) 0, items);
    }

    public void loadResults(final byte seq, List<AddressItem> items) {
        final AddressFlags info = sort(items);
        if (info.address.length != 0) {
            if (info.allFlags == 0) {
                clear(seq);
                return;
            }
            ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.27
                private static final int BATCH_COUNT = 100000000;

                @Override // java.lang.Runnable
                public void run() {
                    InOut inOut = DaemonManager.this.inOut;
                    int total = info.address.length;
                    int offset = 0;
                    while (offset < total) {
                        int len = total - offset;
                        if (len > BATCH_COUNT) {
                            len = BATCH_COUNT;
                        }
                        inOut.startMessage(seq, Message.CMD_CS_LOAD_RESULTS, (InOut.longSize + 4) * len);
                        inOut.writeInt(len);
                        int[] flags = info.flags;
                        long[] address = info.address;
                        int n = offset + len;
                        for (int i = offset; i < n; i++) {
                            inOut.writeInt(flags[i]);
                            inOut.writeLong(address[i]);
                        }
                        DaemonManager.this.send();
                        offset += len;
                    }
                }
            });
            MainService.setLastFlags(info.allFlags, seq);
            SearchButtonListener.lastTextSearch = null;
        }
    }

    public void remove(List<AddressItem> items) {
        remove((byte) 0, items);
    }

    public void remove(final byte seq, List<AddressItem> items) {
        final AddressFlags info = sort(items);
        if (info.address.length != 0) {
            ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.28
                private static final int BATCH_COUNT = 100000000;

                @Override // java.lang.Runnable
                public void run() {
                    InOut inOut = DaemonManager.this.inOut;
                    int total = info.address.length;
                    int offset = 0;
                    while (offset < total) {
                        int len = total - offset;
                        if (len > BATCH_COUNT) {
                            len = BATCH_COUNT;
                        }
                        inOut.startMessage(seq, Message.CMD_CS_REMOVE_RESULT, (InOut.longSize + 4) * len);
                        inOut.writeInt(len);
                        int[] flags = info.flags;
                        long[] address = info.address;
                        int n = offset + len;
                        for (int i = offset; i < n; i++) {
                            inOut.writeInt(flags[i]);
                            inOut.writeLong(address[i]);
                        }
                        DaemonManager.this.send();
                        offset += len;
                    }
                }
            });
        }
    }

    AddressFlags sort(List<AddressItem> items) {
        ArrayList<ItemInfo> list = new ArrayList<>(items.size());
        int size = items.size();
        for (int i = 0; i < size; i++) {
            AddressItem item = items.get(i);
            if (item != null) {
                list.add(new ItemInfo(item.address, item.flags));
            }
        }
        ItemInfo[] arr = (ItemInfo[]) list.toArray(new ItemInfo[list.size()]);
        Arrays.sort(arr);
        ArrayList<ItemInfo> list2 = new ArrayList<>(arr.length);
        ItemInfo last = null;
        for (ItemInfo item2 : arr) {
            if (item2 != null) {
                if (last == null || last.address != item2.address) {
                    list2.add(item2);
                    last = item2;
                } else {
                    last.flags |= item2.flags;
                }
            }
        }
        int size2 = list2.size();
        long[] address = new long[size2];
        int[] flags = new int[size2];
        int allFlags = 0;
        for (int i2 = 0; i2 < size2; i2++) {
            ItemInfo item3 = list2.get(i2);
            address[i2] = item3.address;
            flags[i2] = item3.flags;
            allFlags |= item3.flags;
        }
        int allFlags2 = allFlags & AddressItem.FLAG_AUTO;
        AddressFlags ret = new AddressFlags(address, flags);
        ret.allFlags = allFlags2;
        return ret;
    }

    static class AddressFlags {
        long[] address;
        int allFlags;
        int[] flags;

        public AddressFlags(long[] address, int[] flags) {
            this.address = address;
            this.flags = flags;
        }
    }

    static class ItemInfo implements Comparable<ItemInfo> {
        long address;
        int flags;

        public ItemInfo(long address, int flags) {
            this.address = address;
            this.flags = flags;
        }

        @Override // java.lang.Comparable
        public int compareTo(ItemInfo rhs) {
            long lhsA = this.address;
            long rhsA = rhs.address;
            if (lhsA < rhsA) {
                return -1;
            }
            return lhsA == rhsA ? 0 : 1;
        }
    }

    public void signal(byte signal) {
        signal((byte) 0, signal);
    }

    public void signal(final byte seq, final byte signal) {
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.29
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                inOut.startMessage(seq, Message.CMD_CS_PAUSE_PROCESS, 0);
                inOut.writeByte(signal);
                DaemonManager.this.send();
            }
        });
    }

    public void sendConfig() {
        sendConfig((byte) 0);
    }

    public void sendConfig(final byte seq) {
        if (this.oldDataInRam == null || Config.dataInRam != this.oldDataInRam.intValue()) {
            if (this.oldDataInRam != null) {
                MainService.instance.clear();
            }
            this.oldDataInRam = Integer.valueOf(Config.dataInRam);
        }
        final String dir = Tools.getFilesDirHidden().getAbsolutePath();
        final byte[] dataDir = dir.getBytes();
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.30
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                inOut.startMessage(seq, Message.CMD_CS_CONFIG, 0);
                inOut.writeInt(Config.dataInRam);
                inOut.writeInt(Config.configDaemon);
                inOut.writeLongLong(Config.interceptTimers);
                inOut.writeInt(Config.freezeInterval);
                inOut.writeInt(Config.usedRanges);
                inOut.writeInt(dataDir.length);
                inOut.writeBytes(dataDir, dataDir.length);
                long crc = inOut.crc();
                if (crc != DaemonManager.this.prevConfigCrc) {
                    DaemonManager.this.prevConfigCrc = crc;
                    DaemonManager.this.send();
                    Log.d("Sended config: " + Config.dataInRam + ' ' + Integer.toBinaryString(Config.configDaemon) + ' ' + Long.toBinaryString(Config.interceptTimers) + ' ' + Config.freezeInterval + ' ' + Config.usedRanges + ' ' + dir + ListManager.TEXT_SEPARATOR + dataDir.length);
                }
            }
        });
    }

    void sendClientPid(final byte seq) {
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.31
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                inOut.startMessage(seq, Message.CMD_CS_KEEP_ALIVE, 0);
                inOut.writeInt(android.os.Process.myPid());
                inOut.writeInt(DaemonManager.this.suPid);
                DaemonManager.this.send();
            }
        });
    }

    void send(final byte seq, final byte cmd) {
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.32
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                inOut.startMessage(seq, cmd, 0);
                DaemonManager.this.send();
            }
        });
    }

    void send() {
        InOut inOut = this.inOut;
        if (lastSendError != null) {
            Log.e("Last send error: " + lastSendError);
            inOut.clear();
            return;
        }
        try {
            inOut.send();
            lastSendError = null;
        } catch (Throwable e) {
            inOut.clear();
            Log.e("Send error", e);
            lastSendError = e.toString();
            ThreadManager.getHandlerUiThread().post(new Runnable() { // from class: android.ext.DaemonManager.33
                @Override // java.lang.Runnable
                public void run() {
                    DaemonManager.this.messageFailed();
                }
            });
        }
    }

    void checkBinary(String filename, boolean fixed) throws CheckException {
        File file = new File(filename);
        int err = 0;
        if (!file.exists()) {
            err = R.string.file_not_found;
        } else if (!file.isFile()) {
            err = R.string.not_file;
        } else {
            String[] cmd = {"ls", "-l", Tools.getRealPath(filename)};
            try {
                Process process = Tools.exec(cmd);
                BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
                Log.w("ls for '" + filename + "' got: " + out.readLine());
                out.close();
            } catch (Exception e) {
                Log.e("run '" + Arrays.toString(cmd) + "' error", e);
            }
            if (!file.canExecute()) {
                err = R.string.not_execute;
            }
        }
        if (err != 0) {
            throw new CheckException(String.valueOf(Tools.stripColon(err)) + ": '" + filename + "'\n\n" + Re.s(R.string.check_install) + (err == R.string.not_execute ? "\n\n" + Re.s(R.string.sdcard_install) : ""));
        }
    }

    private static class CheckException extends Exception {
        private static final long serialVersionUID = 5048233518751878881L;

        public CheckException(String detailMessage) {
            super(detailMessage);
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [android.ext.DaemonManager$34] */
    public void start() {
        ThreadManager.runOnWorkerThread(new Runnable() { // from class: android.ext.DaemonManager.34
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                try {
                    Log.e("DaemonManager.start begin");
                    String execPath = DaemonLoader.getDaemonPath();
                    Log.e("DaemonManager.start execPath=" + execPath);
                    DaemonManager daemonManager = DaemonManager.this;
                    Proc proc = DaemonManager.this.startDaemon(execPath);
                    daemonManager.mProcess = proc;
                    Log.e("DaemonManager.start proc=" + proc + "; pid=" + (proc == null || proc.process == null ? "n/a" : Integer.valueOf(Tools.getPid(proc.process))));
                    if (proc == null || proc.process == null || proc.stdout == null || proc.stdin == null || proc.stderr == null) {
                        inOut.setStarted(false);
                        MainService.instance.onStatusChanged();
                        return;
                    }
                    RootDetector.debug = "";
                    inOut.setStreams(proc.stdout, proc.stdin);
                    inOut.setStarted(true);
                    MainService.instance.onStatusChanged();
                    Log.runLogOnProcessErrStream(proc.process, proc.stderr);
                    byte[] buf = inOut.readPacket();
                    if (buf == null) {
                        Log.d("DI: null");
                    } else if (buf.length != 7) {
                        Log.d("DI: " + buf.length);
                    } else {
                        BufferReader reader = new BufferReader(buf);
                        byte cmd = reader.readByte();
                        InOut.setX64(cmd == 8);
                        byte seq = reader.readByte();
                        if (seq != 4) {
                            Log.e("DI: QE " + ((int) seq));
                        }
                        DaemonManager.this.daemonPid = reader.readInt();
                        Log.d("DI: " + ((int) cmd) + " " + ((int) seq) + " " + DaemonManager.this.daemonPid);
                    }
                    BufferReader reader2 = new BufferReader(inOut.readPacket());
                    reader2.readByte();
                    byte[] name = new byte[reader2.readByte()];
                    reader2.read(name);
                    DaemonManager.this.daemonName = new String(name);
                    Log.d("InOut: x64: " + InOut.x64 + "; QE_ALIGN: 4" + ListManager.TEXT_SEPARATOR + DaemonManager.this.daemonPid + ListManager.TEXT_SEPARATOR + DaemonManager.this.daemonName);
                    DaemonManager.this.mReaderThread.start();
                    MainService.instance.onStatusChanged();
                    inOut.sendWait();
                    DaemonManager.this.sendClientPid((byte) 0);
                } catch (CheckException e) {
                    Log.e("start daemon failed", e);
                    String msg = e.getMessage();
                    StringBuilder abis = new StringBuilder();
                    boolean needArm = false;
                    try {
                        abis.append(Build.CPU_ABI);
                        abis.append(ListManager.TEXT_SEPARATOR);
                        if ("armeabi".equals(Build.CPU_ABI)) {
                            needArm = true;
                        }
                        abis.append(Build.CPU_ABI2);
                        abis.append(ListManager.TEXT_SEPARATOR);
                        if ("armeabi".equals(Build.CPU_ABI2)) {
                            needArm = true;
                        }
                    } catch (Throwable e2) {
                        Log.w("Failed get ABI", e2);
                    }
                    if (Build.VERSION.SDK_INT >= 21) {
                        try {
                            for (String abi : Build.SUPPORTED_ABIS) {
                                abis.append(abi);
                                abis.append(ListManager.TEXT_SEPARATOR);
                                if ("armeabi".equals(abi)) {
                                    needArm = true;
                                }
                            }
                        } catch (Throwable e22) {
                            Log.w("Failed get ABI", e22);
                        }
                        try {
                            for (String abi2 : Build.SUPPORTED_32_BIT_ABIS) {
                                abis.append(abi2);
                                abis.append(ListManager.TEXT_SEPARATOR);
                                if ("armeabi".equals(abi2)) {
                                    needArm = true;
                                }
                            }
                        } catch (Throwable e23) {
                            Log.w("Failed get ABI", e23);
                        }
                        try {
                            for (String abi3 : Build.SUPPORTED_64_BIT_ABIS) {
                                abis.append(abi3);
                                abis.append(ListManager.TEXT_SEPARATOR);
                                if ("armeabi".equals(abi3)) {
                                    needArm = true;
                                }
                            }
                        } catch (Throwable e24) {
                            Log.w("Failed get ABI", e24);
                        }
                    }
                    if (needArm && Re.i("chunka", R.raw.class) == 0) {
                        msg = String.valueOf(msg) + "\n\n" + Tools.stripColon(R.string.try_arm_version) + ": " + Apk.ARM_VERSION;
                    }
                    String listAbis = abis.toString();
                    if (listAbis.contains("mips")) {
                        msg = String.valueOf(msg) + "\n\n" + Re.s(R.string.mips_arch);
                    }
                    String msg2 = String.valueOf(msg) + "\n\nABIs: " + listAbis + '\n' + ((Object) DaemonLoader.TESTED) + '\n' + Build.VERSION.SDK_INT + '\n' + ((Object) DaemonLoader.DEBUG);
                    Log.d("Supported ABIs: " + listAbis);
                    Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.failed_load_daemon)).setMessage(msg2).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                    inOut.setStarted(false);
                    MainService.instance.onStatusChanged();
                } catch (Throwable e3) {
                    Log.e("start daemon failed", e3);
                    ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.DaemonManager.34.1
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                String text = String.valueOf(Re.s(R.string.is_device_rooted)) + "\n\n" + Re.s(R.string.must_be_root);
                                AlertDialog dialog = Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.failed_load_daemon)).setMessage(text).setPositiveButton(Re.s(R.string.fix_it), new DialogInterface.OnClickListener() { // from class: android.ext.DaemonManager.34.1.1
                                    @Override // android.content.DialogInterface.OnClickListener
                                    public void onClick(DialogInterface dialog2, int which) {
                                        if (Config.vSpaceReal && !Config.vSpace && (Config.configClient & 512) != 0) {
                                            Config.get(R.id.config_vspace_root).change();
                                        } else {
                                            ConfigListAdapter.showHelp(R.string.work_without_root);
                                        }
                                    }
                                }).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null).create();
                                Alert.show(dialog);
                                Tools.setClickableText(dialog);
                            } catch (Throwable e4) {
                                Log.badImplementation(e4);
                            }
                        }
                    });
                    inOut.setStarted(false);
                    MainService.instance.onStatusChanged();
                }
                DaemonManager.daemonStarted();
            }
        });
    }

    public void stop() {
        stop((byte) 0);
    }

    public void stop(final byte seq) {
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.DaemonManager.35
            @Override // java.lang.Runnable
            public void run() {
                InOut inOut = DaemonManager.this.inOut;
                DaemonManager.this.signal(seq, (byte) 1);
                DaemonManager.this.send(seq, Message.CMD_CS_STOP_SERVICE);
                inOut.setStarted(false);
                MainService.instance.onStatusChanged();
            }
        });
        try {
            Thread.sleep(3L);
        } catch (InterruptedException e) {
        }
    }

    public CharSequence getStatus() {
        String str;
        if (this.inOut.isStarted()) {
            return Config.vSpace ? Tools.colorize("$", Tools.getColor(R.color.daemon_user)) : Config.vSpaceReal ? Tools.colorize("@", Tools.getColor(R.color.daemon_vroot)) : Tools.colorize("#", Tools.getColor(R.color.daemon_root));
        }
        Proc proc = this.mProcess;
        if (proc != null && proc.process != null) {
            return Tools.colorize(Config.vSpace ? "V" : Config.vSpaceReal ? "U" : "W", Tools.getColor(R.color.daemon_wait));
        }
        if (Config.vSpace) {
            str = "!";
        } else {
            str = Config.vSpaceReal ? "I" : Searcher.MASK_PLACEHOLDER;
        }
        return Tools.colorize(str, Tools.getColor(R.color.daemon_bad));
    }

    public boolean isDaemonRun() {
        boolean isStop = (bypassDaemonFail || this.inOut.isStarted()) ? false : true;
        if (isStop) {
            Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.daemon_fail)).setMessage(Re.s(R.string.wait_daemon)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
        }
        return !isStop;
    }
}
