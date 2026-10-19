package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.ext.BaseActivity;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;
import catch_.me_.if_.you_.can_.R;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.Thread;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    public static final int BREVENT = 2;
    public static final int FIELDS = 2;
    public static final String LAST_EXCEPTION = "last_exception";
    public static final String LAST_EXCEPTION_TRACE = "last_exception_trace";
    private static final String NATIVE_CRASH_LOG = "client_crash.log";
    public static final int NEOSAFE = 5;
    public static final int PREVENT_RUNNING = 1;
    public static final int SECURITY = 3;
    private static final String STORE_EXCEPTION_DELIMITER = "@#%~~%#@";
    private static final String STORE_EXCEPTION_FILENAME = "store_ex.txt";
    public static final int WHETSTONE = 4;
    public static final int XPRIVACY = 0;
    private static volatile Throwable last = null;
    private static volatile boolean restart = false;
    private final Thread.UncaughtExceptionHandler previousHandler;
    private final String type;

    public static String[] getApps() {
        return new String[]{"XPrivacy", "biz.bokhorst.xprivacy", "Prevent Running", "me.piebridge.forcestopgb", "Brevent", "me.piebridge.brevent", "Security", "com.chaozhuo.permission.controller", "Whetstone", "com.miui.whetstone", "NeoSafe", "cn.nubia.security2"};
    }

    public static void install() {
        install(Thread.currentThread());
    }

    public static class ThreadEx extends Thread {
        public ThreadEx(Runnable target, String name) {
            super(target, name);
            ExceptionHandler.install(this);
        }
    }

    public static void install(Thread thread) {
        Thread.UncaughtExceptionHandler ex = Thread.getDefaultUncaughtExceptionHandler();
        if (ex == null || !(ex instanceof ExceptionHandler)) {
            ExceptionHandler e = new ExceptionHandler(ex, "default");
            Thread.setDefaultUncaughtExceptionHandler(e);
        }
        Thread.UncaughtExceptionHandler ex2 = thread.getUncaughtExceptionHandler();
        if (ex2 == null || !(ex2 instanceof ExceptionHandler)) {
            if (ex2 == null) {
                ex2 = thread.getThreadGroup();
            }
            ExceptionHandler e2 = new ExceptionHandler(ex2, "thread:" + thread.getName());
            thread.setUncaughtExceptionHandler(e2);
        }
    }

    private ExceptionHandler(Thread.UncaughtExceptionHandler previousHandler, String type) {
        this.previousHandler = previousHandler;
        this.type = type;
    }

    private static void showLastException(final String message, String trace, final Runnable onDone) {
        final String[] APPS = getApps();
        int reason = -1;
        int i = 0;
        while (true) {
            if (i >= APPS.length) {
                break;
            }
            String check = APPS[i];
            if (!message.contains(": " + check)) {
                i += 2;
            } else {
                reason = i;
                break;
            }
        }
        final int fReason = reason;
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { // from class: android.ext.ExceptionHandler.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                if (which == -3) {
                    Tools.openAppInfo(APPS[fReason + 1]);
                }
                onDone.run();
            }
        };
        if (fReason >= 0) {
            Log.w(String.valueOf(APPS[fReason]) + " is reason of last fail");
            Alert.showAfterDaemonStart(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.last_run_failed)).setMessage(String.valueOf(Tools.stringFormat(Re.s(R.string.killed_by), APPS[fReason])) + "\n\n" + trace).setNeutralButton(Re.s(R.string.more), listener).setNegativeButton(Re.s(R.string.ok), listener));
        } else {
            Alert.showAfterDaemonStart(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.last_run_failed)).setMessage(String.valueOf(Re.s(R.string.unhandled_exception)) + "\n\n" + message).setPositiveButton(Re.s(R.string.ok), listener));
        }
    }

    public static void checkLastException() {
        checkNativeCrash();
        try {
            final SharedPreferences pref = Tools.getSharedPreferences();
            if (pref.contains(LAST_EXCEPTION)) {
                showLastException(pref.getString(LAST_EXCEPTION, "- unknown -"), pref.getString(LAST_EXCEPTION_TRACE, "- unknown -"), new Runnable() { // from class: android.ext.ExceptionHandler.2
                    @Override // java.lang.Runnable
                    public void run() {
                        pref.edit().remove(ExceptionHandler.LAST_EXCEPTION).apply();
                    }
                });
            }
        } catch (StackOverflowError e) {
            Log.badImplementation(e);
        }
        try {
            final File file = new File(Tools.getFilesDirHidden(), STORE_EXCEPTION_FILENAME);
            if (file.exists()) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    StringBuilder out = new StringBuilder();
                    while (true) {
                        String line = reader.readLine();
                        if (line != null) {
                            out.append(line);
                            out.append('\n');
                        } else {
                            reader.close();
                            String[] parts = out.toString().split(STORE_EXCEPTION_DELIMITER, 2);
                            showLastException(parts[0], parts[1], new Runnable() { // from class: android.ext.ExceptionHandler.3
                                @Override // java.lang.Runnable
                                public void run() {
                                    file.delete();
                                }
                            });
                            return;
                        }
                    }
                } catch (IOException e2) {
                    Log.e("Failed load exception", e2);
                }
            }
        } catch (StackOverflowError e3) {
            Log.badImplementation(e3);
        }
    }

    private static void checkNativeCrash() {
        String line;
        File file = new File(Tools.getFilesDirHidden(), NATIVE_CRASH_LOG);
        if ((Config.ignore & 64) == 0 && file.exists() && file.length() > 0) {
            String msg = "Native crash detected: " + file.length();
            Log.w(msg);
            try {
                StringBuilder log = new StringBuilder();
                log.append(msg);
                log.append("\n\n");
                boolean send = false;
                try {
                    BufferedReader out = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    while (!Thread.interrupted() && (line = out.readLine()) != null) {
                        if (!send) {
                            String check = DaemonManager.getNativeCheck();
                            if (!line.contains(check) || line.trim().equals(check)) {
                                Log.w("Native crash check fail.");
                                break;
                            }
                            send = true;
                        }
                        log.append(line);
                        log.append('\n');
                    }
                    out.close();
                } catch (Throwable e) {
                    log.append(e.getMessage());
                    log.append('\n');
                    Log.w("Native crash load fail.", e);
                }
                if (send) {
                    String crash = log.toString();
                    if (!checkReasonCrash(crash)) {
                        if (crash.contains(">>> " + Tools.getPackageName() + " <<<") && crash.contains("backtrace:")) {
                            Log.w("Native crash sended.");
                            sendMessage(crash);
                        } else {
                            Log.w("Native crash skipped.");
                        }
                    }
                }
                file.delete();
            } catch (Throwable e2) {
                Log.w("Native crash check file fail.", e2);
            }
        }
        try {
            file.createNewFile();
        } catch (Throwable e3) {
            Log.w("Native crash create file fail.", e3);
        }
    }

    public static boolean checkReasonCrash(String crash) {
        final String[] APPS = getApps();
        if (crash.contains(" Process  (") && crash.contains(") has open file /")) {
            Log.w("unmount is reason of last fail");
            Alert.showAfterDaemonStart(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.last_run_failed)).setMessage(String.valueOf(Re.s(R.string.unmount_kill)) + "\n\n" + crash).setPositiveButton(Re.s(R.string.fix_it), new DialogInterface.OnClickListener() { // from class: android.ext.ExceptionHandler.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    String internal;
                    try {
                        internal = Tools.getContext().getFilesDir().getAbsolutePath();
                    } catch (Throwable e) {
                        internal = "error";
                        Log.w("Failed get files dir", e);
                    }
                    ConfigListAdapter.changePath(internal);
                }
            }).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
            return true;
        }
        if (crash.contains("CZAutoRunController")) {
            alert(crash, 3, new BaseActivity.GoOnForum(BaseActivity.GoOnForum.S9));
            return true;
        }
        if (crash.contains(APPS[8])) {
            alert(crash, 4, new DialogInterface.OnClickListener() { // from class: android.ext.ExceptionHandler.5
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        RootDetector.runCmd("exec setprop persist.sys.whetstone.level 0", 15);
                        Tools.showToast(Re.s(String.valueOf(APPS[8]) + ": " + Re.s(R.string.ok)));
                    } catch (Throwable th) {
                        Log.w("Failed fix " + APPS[8]);
                        Tools.showToast(Re.s(String.valueOf(APPS[8]) + ": " + Re.s(R.string.patch_failed)));
                    }
                }
            });
            return true;
        }
        if (crash.contains(APPS[10])) {
            alert(crash, 5, null);
            return true;
        }
        return false;
    }

    private static void alert(String crash, final int fReason, DialogInterface.OnClickListener listener) {
        final String[] APPS = getApps();
        Log.w(String.valueOf(APPS[fReason * 2]) + " is reason of last fail");
        AlertDialog.Builder builder = Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.last_run_failed)).setMessage(String.valueOf(Tools.stringFormat(Re.s(R.string.killed_by), APPS[fReason * 2])) + "\n\n" + crash).setNeutralButton(Re.s(R.string.more), new DialogInterface.OnClickListener() { // from class: android.ext.ExceptionHandler.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                Tools.openAppInfo(APPS[(fReason * 2) + 1]);
            }
        }).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null);
        if (listener != null) {
            builder.setPositiveButton(Re.s(R.string.fix_it), listener);
        }
        Alert.showAfterDaemonStart(builder);
    }

    public static void storeException(String message, String thread, String exceptionTrace) {
        if (Tools.getContext() != null) {
            Tools.getSharedPreferences().edit().putString(LAST_EXCEPTION, message).putString(LAST_EXCEPTION_TRACE, exceptionTrace).commit();
            return;
        }
        File file = new File(Tools.getFilesDirHidden(), STORE_EXCEPTION_FILENAME);
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(String.valueOf(message) + STORE_EXCEPTION_DELIMITER + exceptionTrace);
            writer.close();
        } catch (IOException e) {
            Log.e("Failed store exception", e);
        }
    }

    public static void storeException(Thread th, Throwable ex) {
        String thread = th.getName();
        String exceptionTrace = Log.getStackTraceString(ex);
        String message = "GG: " + ((102.5f * 1) - 6.5f) + " [" + ((int) (((16337.8f * 1) - 344.8f) + 0.5f)) + "]\nAndroid: " + Build.VERSION.RELEASE + "\nSDK: " + Build.VERSION.SDK_INT + "\nvSpace: " + Main.getVSpace() + "\nThread: " + thread + '\n' + exceptionTrace + "\nHash: " + Installer.getHashes();
        storeException(message, thread, exceptionTrace);
    }

    public static String sendException(Thread th, Throwable ex, boolean crash) {
        String toast = Tools.removeNewLinesChars("JJ#fudvkhg");
        String thread = th.getName();
        if (crash) {
            Log.e("Exception in thread " + thread, ex);
            Log.crash();
        }
        StringBuilder msg = new StringBuilder();
        msg.append("GG: ");
        msg.append((105.1f * 1) - 9.1f);
        msg.append(" [");
        msg.append((int) (((16415.3f * 1) - 422.3f) + 0.5f));
        msg.append("]\nAndroid: ");
        msg.append(Build.VERSION.RELEASE);
        msg.append("\nSDK: ");
        msg.append(Build.VERSION.SDK_INT);
        msg.append("\nvSpace: ");
        msg.append(Main.getVSpace());
        msg.append("\nThread: ");
        msg.append(thread);
        msg.append('\n');
        String exceptionTrace = Log.getStackTraceString(ex);
        boolean badSOE = ((ex instanceof StackOverflowError) || exceptionTrace.contains("java.lang.StackOverflowError")) && exceptionTrace.contains("com.lody.virtual.client.ipc.");
        msg.append(exceptionTrace);
        if (exceptionTrace.contains("ActivityNotFoundException")) {
            Tools.describeActivities(Apk.PACKAGE, msg);
        }
        msg.append("\nHash: ");
        try {
            msg.append(Installer.getHashes());
        } catch (Throwable e) {
            msg.append("???");
            Log.e("dbg", e);
        }
        String message = msg.toString();
        boolean haveContext = Tools.getContext() != null;
        boolean eglCause = message.contains("EGL_") || message.contains("egl") || message.contains("HardwareRenderer");
        if (crash && haveContext && eglCause && (Config.configClient & 2) != 0) {
            Config.get(R.id.config_acceleration).value = 0;
            Config.save();
            return String.valueOf(toast) + ". Hardware acceleration fail.";
        }
        if (crash && eglCause && (Config.configClient & 2) == 0 && BaseActivity.hw) {
            String toast2 = String.valueOf(toast) + ". Hardware acceleration fail. Try use 'GG (SW)' icon for start.";
            restart = true;
            BaseActivity.hw = false;
            return toast2;
        }
        if (crash && haveContext && message.contains("Suggestion") && (Config.configClient & 8) != 0) {
            Config.get(R.id.config_suggestions).value = 0;
            Config.save();
            return String.valueOf(toast) + ". Cause: suggestions.";
        }
        if (crash && haveContext && message.contains("playSoundEffect") && (Config.configClient & 32) != 0) {
            Config.get(R.id.config_use_sound_effects).value = 0;
            Config.save();
            return String.valueOf(toast) + ". Cause: sound effects.";
        }
        try {
            File file = new File(Tools.getSdcardPath(), "crash.txt");
            FileOutputStream fos = new FileOutputStream(file, true);
            BaseActivity.writeLogcatHeader(fos, file.getAbsolutePath(), "");
            fos.write("".getBytes());
            fos.write(message.getBytes());
            fos.close();
            toast = String.valueOf(toast) + "\nLog: " + file.getAbsolutePath();
        } catch (Throwable e2) {
            Log.w("Failed out crash to file", e2);
        }
        try {
            File file2 = new File(Tools.getSdcardPath(), "logcat.txt");
            BaseActivity.writeLogcatHeader(new FileOutputStream(file2, true), file2.getAbsolutePath(), "");
            String[] logcat = {"logcat", "-f", Tools.getRealPath(file2.getAbsolutePath()), "-d", "-v", "threadtime", "-b", "main", "-b", "system", "*:V"};
            try {
                if (!Tools.waitForTimeout(Tools.exec(logcat), 15)) {
                    Log.w("timeout fail 1: " + Arrays.toString(logcat), new RuntimeException());
                }
            } catch (Exception e3) {
                Log.w("exec", e3);
            }
            toast = String.valueOf(toast) + "\nLogcat: " + file2.getAbsolutePath();
        } catch (Throwable e4) {
            Log.w("Failed out logcat to file", e4);
        }
        try {
            Config.get(R.id.record_logcat).value = (int) (((16474.8f * 1) - 481.8f) + 0.5f);
            Config.save();
            toast = String.valueOf(toast) + "\nNext run rec logcat: " + BaseActivity.getLogcatFilename();
        } catch (Throwable e5) {
            Log.w("Failed enable logcat on next run", e5);
        }
        try {
            String[] APPS = getApps();
            if (crash && message.contains(": " + APPS[0])) {
                throw new RuntimeException(String.valueOf(APPS[0]) + " is cause");
            }
            if (badSOE) {
                Log.d("VSpace StackOverflowError - reporting disabled");
                return toast;
            }
            if (crash) {
                storeException(message, thread, exceptionTrace);
            }
            return toast;
        } catch (Throwable e6) {
            Log.w("Cannot persist exception info", e6);
            if (crash) {
                storeException(message, thread, Log.getStackTraceString(ex));
                return toast;
            }
            return toast;
        }
    }

    public static void sendMessage(String msg) {
        Log.d("Crash reporting disabled; skip sendMessage");
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread th, Throwable ex) {
        if (last != ex) {
            last = ex;
            if (!(ex instanceof IgnoredException)) {
                String toast = sendException(th, ex, true);
                showToast(toast);
                if (restart) {
                    BaseActivity.restartApp();
                }
            }
        } else {
            Log.d("uncaughtException already called for: " + ex);
        }
        if (this.previousHandler != null) {
            this.previousHandler.uncaughtException(th, ex);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [android.ext.ExceptionHandler$7] */
    private void showToast(final String message) {
        try {
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ExceptionHandler.7
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        ToastManager.show(Toast.makeText(ToastManager.getContext(), message, 1));
                    } catch (Throwable th) {
                    }
                }
            });
            try {
                Thread.sleep(4000L);
            } catch (InterruptedException e) {
            }
        } catch (Throwable th) {
        }
    }

    public static class IgnoredException extends RuntimeException {
        private static final long serialVersionUID = 3496448765514398615L;

        public IgnoredException() {
        }

        public IgnoredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }

        public IgnoredException(String message, Throwable cause) {
            super(message, cause);
        }

        public IgnoredException(String message) {
            super(message);
        }

        public IgnoredException(Throwable cause) {
            super(cause);
        }
    }
}
