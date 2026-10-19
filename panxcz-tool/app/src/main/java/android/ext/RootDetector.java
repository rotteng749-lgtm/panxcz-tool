package android.ext;

import android.content.SharedPreferences;
import android.ext.ExceptionHandler;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* JADX INFO: loaded from: classes.dex */
public class RootDetector {
    public static final String SH_KEY = "sh";
    public static final String SU_KEY = "su";
    public static String debug = "";
    static volatile int runCmdPid;
    private static String suGlobal;
    private static boolean useSh;

    static {
        useSh = false;
        suGlobal = SU_KEY;
        suGlobal = SU_KEY;
        SharedPreferences storage = Tools.getSharedPreferences();
        suGlobal = storage.getString(SU_KEY, SU_KEY);
        useSh = storage.getBoolean(SH_KEY, false);
        runCmdPid = 0;
    }

    private static String getData(String name, InputStream is) {
        byte[] buf;
        int cnt;
        StringBuilder data = new StringBuilder();
        data.append(name);
        data.append(": ");
        try {
            int av = is.available();
            if (av > 0 && (cnt = is.read((buf = new byte[av]))) > 0) {
                data.append(new String(Arrays.copyOf(buf, cnt)));
            }
        } catch (IOException e) {
            data.append(e.toString());
        }
        data.append('\n');
        return data.toString();
    }

    public static String getInfo(java.lang.Process process) {
        StringBuilder dbg = new StringBuilder();
        if (process != null) {
            dbg.append(getData("stdout", process.getInputStream()));
            dbg.append(getData("stderr", process.getErrorStream()));
            dbg.append("exit value: ");
            try {
                int ev = process.exitValue();
                dbg.append(ev);
            } catch (IllegalThreadStateException e) {
                dbg.append("already run");
            }
            dbg.append('\n');
        } else {
            dbg.append("process is null\n");
        }
        return dbg.toString();
    }

    private static String addPath() {
        String[] pathes = {"/vendor/bin", "/system/bin", "/bin", "/data/local", "/data/local/bin", "/system/sd/bin", "/system/bin/failsafe", "/system/bin/bstk", "/su/bin", "/magisk/.core/bin", "/magisk/phh/bin"};
        List<String> list = new ArrayList<>(pathes.length * 3);
        for (String path : pathes) {
            list.add(path);
            if (path.contains("/bin")) {
                list.add(path.replace("/bin", "/xbin"));
                list.add(path.replace("/bin", "/sbin"));
            }
        }
        return TextUtils.join(":", list);
    }

    private static String[] getEnv() {
        Map<String, String> env = System.getenv();
        int size = env.size();
        int i = 0;
        String path = env.get("PATH");
        if (path == null) {
            size++;
        }
        String[] envp = new String[size];
        for (Map.Entry<String, String> e : env.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            if ("PATH".equals(key)) {
                value = String.valueOf(value) + ':' + addPath();
            }
            envp[i] = String.valueOf(key) + '=' + value;
            i++;
        }
        if (path == null) {
            int i2 = i + 1;
            envp[i] = "PATH=" + addPath();
        }
        return envp;
    }

    static String getSu(String su) {
        return Config.vSpace ? SH_KEY : su == null ? suGlobal : su;
    }

    public static java.lang.Process tryRoot(String cmd) {
        return tryRoot(cmd, null, false);
    }

    public static java.lang.Process tryRoot(String cmd, String suCmd, boolean useDebug) {
        java.lang.Process process = null;
        StringBuilder dbg = new StringBuilder();
        
        Log.d("try: " + cmd);
        
        // Check if command contains exec or exit
        if (!cmd.contains("exec") && !cmd.contains("exit")) {
            throw new RuntimeException("You forgot exec or exit");
        }
        
        // Check for /emulated/0 path
        if (cmd.contains("/emulated/0")) {
            Log.w("Possible bugged call to /emulated/0 from root: " + cmd);
        }
        
        dbg.append("try: ").append(cmd).append('\n');
        
        String su = getSu(suCmd);
        dbg.append("su: ").append(su).append('\n');
        
        String runBin;
        // Check if vSpace or useSh is enabled
        if (Config.vSpace || useSh) {
            runBin = "sh";
            // Construct command
            StringBuilder sb = new StringBuilder();
            sb.append("exec ").append(su).append('\n');
            sb.append(cmd);
            cmd = sb.toString();
        } else {
            runBin = su;
        }
        
        String[] binary = new String[]{runBin};
        
        Log.d("suCmd: '" + suCmd + "'; su: '" + su + "'; runBin: '" + runBin + "'");
        
        Throwable ex = null;
        // Try 2 different methods
        for (int i = 0; i < 2; i++) {
            try {
                switch (i) {
                    case 0:
                        Log.d("exec 0 start");
                        process = Tools.exec(binary);
                        Log.d("exec 0 end: " + process);
                        break;
                    case 1:
                        Log.d("exec 1 start");
                        process = Tools.exec(binary, getEnv());
                        Log.d("exec 1 end: " + process);
                        break;
                }
                
                if (process != null) {
                    // Write command to process
                    java.io.DataOutputStream in = new java.io.DataOutputStream(process.getOutputStream());
                    String fullCmd = Tools.getRealPath(cmd) + "\n";
                    in.writeBytes(fullCmd);
                    in.flush();
                    
                    dbg.append("process started: ").append(i).append('\n');
                    break;
                }
            } catch (Throwable t) {
                ex = t;
                if (ex != null) {
                    dbg.append(ex.toString()).append('\n');
                    ex = ex.getCause();
                }
            }
        }
        
        Log.d("tryRoot end");
        
        if (useDebug) {
            if (ex == null) {
                dbg.append('\n');
                dbg.append(getInfo(process));
            }
            debug = dbg.toString();
        }
        
        return process;
    }

    public static String runCmd(String cmd, int timeout) throws InterruptedException, IOException {
        String ret = runCmd(cmd, timeout, null);
        debug = "";
        return ret;
    }

    public static String runCmd(final String cmd, int timeout, final String suCmd) throws InterruptedException, IOException {
        final StringBuilder ret = new StringBuilder();
        runCmdPid = 0;
        FutureTask<String> task = new FutureTask<>(new Callable<String>() { // from class: android.ext.RootDetector.1
            @Override // java.util.concurrent.Callable
            public String call() throws Exception {
                Log.d("cmd (" + cmd + ") start");
                String su = (Config.vSpace && "".equals(suCmd)) ? suCmd : RootDetector.getSu(suCmd);
                Log.d("su (" + su + ") used");
                Process check = "".equals(su) ? Tools.exec(new String[]{cmd}) : RootDetector.tryRoot(cmd, su, true);
                if (check == null) {
                    Log.e("cmd fail: " + RootDetector.debug);
                    return "cmd fail - got null";
                }
                int pid = Tools.getPid(check);
                RootDetector.runCmdPid = pid;
                BufferedReader out = new BufferedReader(new InputStreamReader(check.getInputStream()));
                while (true) {
                    try {
                        String line = out.readLine();
                        if (line == null) {
                            break;
                        }
                        Log.d("cmd (" + cmd + ") out: " + line);
                        ret.append(line);
                        ret.append('\n');
                    } catch (Throwable th) {
                        out.close();
                        throw th;
                    }
                }
                out.close();
                ret.append("exit code: ");
                ret.append(check.waitFor());
                Log.d("cmd (" + cmd + ") exit: " + check.exitValue());
                if (pid > 0) {
                    try {
                        Log.d("Kill at end: " + pid);
                        android.os.Process.killProcess(pid);
                        Log.d("Kill at end: ok");
                    } catch (Throwable e) {
                        Log.d("Failed kill at end", e);
                    }
                }
                RootDetector.runCmdPid = 0;
                return null;
            }
        });
        new ExceptionHandler.ThreadEx(task, "runCmd: " + cmd).start();
        try {
            task.get(timeout, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            Log.e("cmd (" + cmd + ") expection", e);
            ret.append("expection: ");
            ret.append(e.getMessage());
        } catch (TimeoutException e2) {
            Log.d("cmd (" + cmd + ") exit: timeout " + timeout, e2);
            ret.append("timeout: ");
            ret.append(timeout);
        }
        int pid = runCmdPid;
        if (pid > 0) {
            try {
                Log.d("Kill: " + pid);
                android.os.Process.killProcess(pid);
                Log.d("Kill: ok");
            } catch (Throwable e3) {
                Log.d("Failed kill", e3);
            }
        }
        runCmdPid = 0;
        return ret.toString();
    }
}
