package android.ext;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.ext.BaseActivity;
import android.ext.ExceptionHandler;
import android.ext.Log;
import android.fix.LayoutInflater;
import android.fix.SystemService;
import android.os.Build;
import android.os.Process;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.AnalyticsService;
import catch_.me_.if_.you_.can_.Instrumentation;
import catch_.me_.if_.you_.can_.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
public class Main {
    public static final int FROM_ACTIVITY = 2;
    public static final int FROM_SERVICE = 1;
    private static final String IMPROVE_TRANSLATE = "improve-translate";
    public static final int NO = 0;
    public static volatile Boolean notLatest = null;
    public static volatile boolean exit = false;
    static volatile int doRestart = 0;
    static volatile boolean loaded = false;

    static void logDebug(int pos, String str) {
        Log.d("main " + pos + ':' + Process.myUid() + ": " + BootstrapInstrumentation.isBootstraped() + ' ' + (BaseActivity.instance == null ? null : Boolean.valueOf(BaseActivity.instance.installMode)) + ' ' + BootstrapInstrumentation.mInstance + ' ' + BootstrapService.instance + ' ' + str);
    }

    static void checkInstrumentation(byte instr) {
        File file = new File(Tools.getFilesDirHidden(), "instr.check");
        int code = (int) (((16096.4f * 1) - 103.4f) + 0.5f);
        if (instr == 0) {
            try {
                OutputStream os = new FileOutputStream(file);
                try {
                    os.write(Integer.toString(code).getBytes());
                    return;
                } finally {
                    os.close();
                }
            } catch (Throwable e) {
                Log.e("Fail set instr file", e);
                return;
            }
        }
        if (instr == 1) {
            if (!file.exists()) {
                Log.e("No instr file: " + file);
            }
            file.delete();
            return;
        }
        if (file.exists()) {
            InputStream is = null;
            try {
                is = new FileInputStream(file);
                byte[] buffer = new byte[12];
                int read = is.read(buffer);
                int readCode = read > 0 ? Integer.parseInt(new String(buffer, 0, read)) : 0;
                if (readCode == code && Config.contextSource == 0) {
                    Config.get(R.id.config_context_source).value = 1;
                    Config.save();
                }
                file.delete();
            } catch (Throwable e2) {
                Log.e("Fail get instr file", e2);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Throwable e3) {
                        Log.badImplementation(e3);
                    }
                }
            }
        }
    }

    private static boolean isPackageInstalledSafe(String pkg) {
        try {
            return Tools.isPackageInstalled(pkg);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }

    static void startInstall() {
        BaseActivity.instance.setInstallerMode(1);
        Installer.startInstall();
    }

    static void startInstall64() {
        final Runnable callback = new Runnable() { // from class: android.ext.Main.1
            private boolean called = false;

            @Override // java.lang.Runnable
            public void run() {
                if (!this.called) {
                    this.called = true;
                    Main.startInstall();
                }
            }
        };
        AlertDialog dialog = Alert.create(BaseActivity.context).setTitle(Re.s(R.string.install_mode)).setCancelable(false).setItems(new String[]{Re.s(R.string.default_mark), Re.s(R.string.for32), Re.s(R.string.for64)}, new DialogInterface.OnClickListener() { // from class: android.ext.Main.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog2, int which) {
                if (which == 1) {
                    Installer.mode = (byte) 1;
                } else if (which == 2) {
                    Installer.mode = (byte) 2;
                }
                callback.run();
            }
        }).create();
        Alert.show(dialog, callback, false);
    }

    public static void start() {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Main.3
            @Override // java.lang.Runnable
            public void run() {
                Main.logDebug(1, null);
                boolean installMode = Installer.getState().isInstaller;
                BaseActivity.instance.installMode = installMode && (!Config.vSpaceReal || Config.vSpaceGuest);
                if (installMode) {
                    boolean isX64 = ConfigListAdapter.isClient64();
                    Log.d("Install64: " + isX64);
                    if (isX64) {
                        Main.startInstall64();
                        return;
                    } else {
                        Main.startInstall();
                        return;
                    }
                }
                BaseActivity.exitOnDestroy = false;
                if (Main.loaded) {
                    Main.logDebug(8, null);
                    Main.onStart();
                    return;
                }
                boolean instr = BootstrapInstrumentation.isBootstraped();
                Main.checkInstrumentation(instr ? (byte) 1 : (byte) 2);
                if (!instr) {
                    Log.d("Not instr: " + Process.myUid());
                    Main.logDebug(2, new StringBuilder().append(Config.contextSource).toString());
                    ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Main.3.1
                        @Override // java.lang.Runnable
                        public void run() {
                            boolean tryInstr = false;
                            boolean done = false;
                            try {
                                if (!Config.vSpaceReal && Config.contextSource == 0) {
                                    ComponentName component = Main.getInstrumentationComponent();
                                    if (component != null) {
                                        Log.d("Try instr: " + Process.myUid());
                                        Main.checkInstrumentation((byte) 0);
                                        done = BaseActivity.context.startInstrumentation(component, null, BaseActivity.getHwBundle());
                                        Main.checkInstrumentation((byte) 1);
                                        tryInstr = false;
                                        Log.d("Fail instr: " + Process.myUid());
                                    }
                                } else {
                                    Main.checkPhoenixOs();
                                }
                                Main.logDebug(3, new StringBuilder().append(done).append(' ').append(Config.contextSource).toString());
                            } catch (Throwable e) {
                                Log.e("Failed load Instrumentation", e);
                            }
                            if (tryInstr) {
                                Main.checkInstrumentation((byte) 1);
                            }
                            Main.logDebug(6, null);
                            Main.setContext();
                        }
                    });
                    Main.logDebug(4, null);
                    return;
                }
                Log.d("Use instr: " + Process.myUid());
                Main.logDebug(9, null);
                Main.useNotificationService();
                Main.onStart();
            }
        });
    }

    static void setContext() {
        Context context = null;
        try {
            switch (Config.contextSource) {
                case 2:
                    context = BaseActivity.instance.createPackageContext(BaseActivity.instance.getPackageName(), 3);
                    break;
                case 3:
                    context = BaseActivity.appContext;
                    break;
                case 4:
                    context = BaseActivity.instance.getBaseContext();
                    break;
            }
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
        Log.d("setContext: " + Config.contextSource + ' ' + context);
        if (context != null) {
            try {
                LayoutInflater li = (LayoutInflater) SystemService.wrap(LayoutInflater.from(context));
                li.inflate(R.layout.service_dialog, (ViewGroup) null);
            } catch (Throwable e2) {
                Log.w("Failed use context: " + context, e2);
                context = null;
            }
        }
        if (context != null) {
            Tools.init(context);
            MainService.context = ServiceContext.wrap(context);
            useNotificationService();
            onStart();
            return;
        }
        BootstrapService.allow = true;
        if (!loadService()) {
            Tools.showToast(String.valueOf(Tools.stripColon(R.string.could_not_use)) + ": " + Re.s(R.string.service));
            BaseActivity.instance.switchContext();
        }
    }

    static void useNotificationService() {
        if (Config.contextSource != 1) {
            if ((Config.configClient & 256) != 0) {
                loadService();
            } else {
                unloadService();
            }
        }
    }

    static DialogInterface.OnClickListener getSkipListener(final long mask) {
        return new DialogInterface.OnClickListener() { // from class: android.ext.Main.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                Config.ignore |= mask;
                Config.save();
            }
        };
    }

    static void checkPhoenixOs() {
        if (isPackageInstalledSafe("com.chaozhuo.permission.controller") && (Config.ignore & 4) == 0) {
            String message = String.valueOf(Re.s(R.string.need_allow_background)) + "\n\n" + Tools.removeNewLinesChars(BaseActivity.GoOnForum.S9);
            ThreadManager.runOnUiThread(showSkipDialog(BaseActivity.context, message, 4L, true));
        }
    }

    static Runnable showSkipDialog(final Context context, final String message, final long mask, boolean makeClickable) {
        return new Runnable() { // from class: android.ext.Main.5
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Context cont = context;
                    if (cont == null) {
                        cont = Tools.getContext();
                    }
                    AlertDialog dialog = Alert.create(cont).setMessage(message).setPositiveButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null).setNeutralButton(Re.s(R.string.skip), Main.getSkipListener(mask)).create();
                    Alert.show(dialog);
                    Tools.setClickableText(dialog);
                } catch (Throwable e) {
                    Log.badImplementation(e);
                }
            }
        };
    }

    private static void checkLocale() {
        if ((Config.ignore & 128) == 0) {
            try {
                String num = Tools.stringFormat("%e", Float.valueOf(1.2E34f));
                if (num.indexOf(101) == -1 && num.indexOf(69) == -1 && num.indexOf(1077) == -1 && num.indexOf(1045) == -1) {
                    ThreadManager.runOnUiThread(showSkipDialog(BaseActivity.context, Tools.stringFormat(Re.s(R.string.wrong_float_), Float.toString(1.2E34f), num), 128L, false));
                }
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
    }

    private static void checkSlowEmulator() {
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.Main.7
            @Override // java.lang.Runnable
            public void run() {
                if ((Config.ignore & 32) == 0) {
                    String str = null;
                    String[] strArr = {"/system/priv-app/com.bluestacks.settings", "/system/priv-app/com.bluestacks.settings/com.bluestacks.settings.apk", "/system/priv-app/com.bluestacks.bstfolder", "/system/priv-app/com.bluestacks.bstfolder/com.bluestacks.bstfolder.apk", "/system/priv-app/com.bluestacks.BstCommandProcessor", "/system/priv-app/com.bluestacks.BstCommandProcessor/com.bluestacks.BstCommandProcessor.apk", "/boot/android/android/system/priv-app/com.bluestacks.settings", "/boot/android/android/system/priv-app/com.bluestacks.settings/com.bluestacks.settings.apk", "/boot/android/android/system/priv-app/com.bluestacks.bstfolder", "/boot/android/android/system/priv-app/com.bluestacks.bstfolder/com.bluestacks.bstfolder.apk", "/boot/android/android/system/priv-app/com.bluestacks.BstCommandProcessor", "/boot/android/android/system/priv-app/com.bluestacks.BstCommandProcessor/com.bluestacks.BstCommandProcessor.apk", "/storage/emulated/0/Android/data/com.bluestacks.settings", "/storage/emulated/0/Android/data/com.bluestacks.home", "/mnt/runtime/write/emulated/0/Android/data/com.bluestacks.settings", "/mnt/runtime/write/emulated/0/Android/data/com.bluestacks.home", "/mnt/runtime/read/emulated/0/Android/data/com.bluestacks.settings", "/mnt/runtime/read/emulated/0/Android/data/com.bluestacks.home", "/mnt/runtime/default/emulated/0/Android/data/com.bluestacks.settings", "/mnt/runtime/default/emulated/0/Android/data/com.bluestacks.home", "/data/.bluestacks.prop", "/data/misc/profiles/cur/0/com.bluestacks.settings", "/data/misc/profiles/cur/0/com.bluestacks.BstCommandProcessor", "/data/misc/profiles/cur/0/com.bluestacks.appmart", "/data/misc/profiles/cur/0/com.bluestacks.bstfolder", "/data/misc/profiles/cur/0/com.bluestacks.home", "/data/misc/profiles/cur/0/com.bluestacks.filemanager", "/data/misc/profiles/cur/0/com.bluestacks.appguidance", "/data/misc/profiles/ref/com.bluestacks.settings", "/data/misc/profiles/ref/com.bluestacks.BstCommandProcessor", "/data/misc/profiles/ref/com.bluestacks.appmart", "/data/misc/profiles/ref/com.bluestacks.bstfolder", "/data/misc/profiles/ref/com.bluestacks.home", "/data/misc/profiles/ref/com.bluestacks.filemanager", "/data/misc/profiles/ref/com.bluestacks.appguidance", "/data/media/0/Android/data/com.bluestacks.settings", "/data/media/0/Android/data/com.bluestacks.home", "/data/data/com.bluestacks.settings", "/data/data/com.bluestacks.BstCommandProcessor", "/data/data/com.bluestacks.appmart", "/data/data/com.bluestacks.bstfolder", "/data/data/com.bluestacks.home", "/data/data/com.bluestacks.filemanager", "/data/data/com.bluestacks.appguidance", "/data/downloads/com.bluestacks.appmart", "/data/downloads/com.bluestacks.home", "/data/downloads/com.bluestacks.filemanager", "/data/app-lib/com.bluestacks.settings", "/data/app-lib/com.bluestacks.BstCommandProcessor", "/data/app-lib/com.bluestacks.appmart", "/data/app-lib/com.bluestacks.bstfolder", "/data/app-lib/com.bluestacks.home", "/data/app-lib/com.bluestacks.filemanager", "/data/user_de/0/com.bluestacks.settings", "/data/user_de/0/com.bluestacks.BstCommandProcessor", "/data/user_de/0/com.bluestacks.appmart", "/data/user_de/0/com.bluestacks.bstfolder", "/data/user_de/0/com.bluestacks.home", "/data/user_de/0/com.bluestacks.filemanager", "/data/user_de/0/com.bluestacks.appguidance", "/data/dalvik-cache/x86/system@priv-app@com.bluestacks.BstCommandProcessor@com.bluestacks.BstCommandProcessor.apk@classes.dex", "/data/dalvik-cache/x86/system@priv-app@com.bluestacks.bstfolder@com.bluestacks.bstfolder.apk@classes.dex", "/data/dalvik-cache/x86/data@downloads@com.bluestacks.appmart@com.bluestacks.appmart.apk@classes.dex", "/data/dalvik-cache/x86/data@downloads@com.bluestacks.filemanager@com.bluestacks.filemanager.apk@classes.dex", "/data/dalvik-cache/x86/data@downloads@com.bluestacks.home@com.bluestacks.home.apk@classes.dex", "/data/dalvik-cache/x86/system@priv-app@com.bluestacks.settings@com.bluestacks.settings.apk@classes.dex", "/data/dalvik-cache/x86_64/system@priv-app@com.bluestacks.BstCommandProcessor@com.bluestacks.BstCommandProcessor.apk@classes.dex", "/data/dalvik-cache/x86_64/system@priv-app@com.bluestacks.bstfolder@com.bluestacks.bstfolder.apk@classes.dex", "/data/dalvik-cache/x86_64/data@downloads@com.bluestacks.appmart@com.bluestacks.appmart.apk@classes.dex", "/data/dalvik-cache/x86_64/data@downloads@com.bluestacks.filemanager@com.bluestacks.filemanager.apk@classes.dex", "/data/dalvik-cache/x86_64/data@downloads@com.bluestacks.home@com.bluestacks.home.apk@classes.dex", "/data/dalvik-cache/x86_64/system@priv-app@com.bluestacks.settings@com.bluestacks.settings.apk@classes.dex", "/system/lib64/libbstfolder_jni.so", "/system/lib64/egl/libGLES_bst.so", "/system/lib64/hw/gralloc.bst.so", "/system/lib/libbstfolder_jni.so", "/system/lib/egl/libGLES_bst.so", "/system/lib/hw/gralloc.bst.so", "/system/bin/bstsvcmgrtest", "/system/bin/bstshutdown_core", "/system/bin/bstfolderd", "/system/bin/bstfolder_ctl", "/system/bin/bstime", "/system/bin/bstsyncfs", "/system/bin/bstshutdown", "/system/xbin/bstk", "/storage/emulated/0/.bstshutdown_sync", "/mnt/runtime/write/emulated/0/.bstshutdown_sync", "/mnt/runtime/read/emulated/0/.bstshutdown_sync", "/mnt/runtime/default/emulated/0/.bstshutdown_sync", "/data/media/0/.bstshutdown_sync", "/data/downloads/.bstABI2Apps", "/sys/devices/virtual/bstsensor", "/sys/devices/virtual/misc/bst_ime", "/sys/devices/virtual/misc/bst_gps", "/sys/devices/virtual/misc/bstpgaipc", "/sys/class/bstsensor", "/sys/class/misc/bst_ime", "/sys/class/misc/bst_gps", "/sys/class/misc/bstpgaipc", "/sys/bus/pci/drivers/bstsensor", "/sys/bus/pci/drivers/bstvideo", "/sys/bus/pci/drivers/bstaudio", "/sys/bus/pci/drivers/bstcamera", "/sys/bus/pci/drivers/bstinput", "/sys/bus/pci/drivers/bstpgaipc", "/sys/module/bstsensor", "/sys/module/bstvideo", "/sys/module/bstaudio", "/sys/module/bstcamera", "/sys/module/videobuf_core/holders/bstcamera", "/sys/module/bstinput", "/sys/module/bstpgaipc", "/proc/irq/16/bstcamera", "/proc/irq/17/bstaudio", "/proc/irq/18/bstinput", "/proc/irq/22/bstpgaipc", "/proc/irq/23/bstsensor", "/proc/bstid", "/proc/asound/bstaudio", "/proc/bstfolder_exports", "/boot/android/android/system/lib64/egl/libGLES_bst.so", "/boot/android/android/system/lib64/hw/gralloc.bst.so", "/boot/android/android/system/lib/egl/libGLES_bst.so", "/boot/android/android/system/lib/hw/gralloc.bst.so", "/boot/android/android/system/bin/bstsvcmgrtest", "/boot/android/android/system/bin/bstshutdown_core", "/boot/android/android/system/bin/bstfolderd", "/boot/android/android/system/bin/bstfolder_ctl", "/boot/android/android/system/bin/bstime", "/boot/android/android/system/bin/bstsyncfs", "/boot/android/android/system/bin/bstshutdown", "/boot/android/android/system/xbin/bstk", "/boot/bstsetup.env", "/boot/bstmods", "/dev/bstpgaipc", "/dev/bst_gps", "/dev/bst_ime", "/dev/bstgyro", "/dev/bstorie", "/dev/bstmegn", "/dev/bstacce", "/dev/socket/bstfolderd"};
                    int length = strArr.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        }
                        String check = strArr[i];
                        if (!new File(check).exists()) {
                            i++;
                        } else {
                            str = Re.s(R.string.bluestacks);
                            break;
                        }
                    }
                    if (str != null) {
                        String slow = str;
                        String message = String.valueOf(Tools.stringFormat(Tools.stripColon(R.string.slow_emulator), slow)) + Re.s(R.string.fast_emulators);
                        ThreadManager.getHandlerUiThread().postDelayed(Main.showSkipDialog(null, message, 32L, true), 30000L);
                    }
                }
            }
        });
    }

    private static void checkBadApps() {
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.Main.8
            @Override // java.lang.Runnable
            public void run() {
                if ((Config.ignore & 8) == 0) {
                    StringBuilder str = new StringBuilder();
                    String[] APPS = ExceptionHandler.getApps();
                    for (int i = 2; i < APPS.length; i += 2) {
                        String check = APPS[i];
                        if (isPackageInstalledSafe(APPS[i + 1])) {
                            str.append('\n');
                            str.append(check);
                            str.append(" (");
                            str.append(APPS[i + 1]);
                            str.append(')');
                        }
                    }
                    if (str.length() != 0) {
                        str.append('\n');
                        String bad = str.toString();
                        ThreadManager.runOnUiThread(Main.showSkipDialog(BaseActivity.context, Tools.stringFormat(Re.s(R.string.killers_found), bad), 8L, false));
                    }
                }
            }
        });
    }

    public static boolean isRootMode() {
        return isRootMode(null, null);
    }

    public static boolean isRootMode(Context context) {
        return isRootMode(context, null);
    }

    public static boolean isRootMode(Context context, DialogInterface.OnDismissListener listener) {
        if (!Config.vSpace) {
            return true;
        }
        if (context == null) {
            context = Tools.getContext();
        }
        AlertDialog dialog = Alert.create(context).setMessage(Re.s(R.string.need_root_mode)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null).create();
        if (listener != null) {
            Alert.setOnDismissListener(dialog, listener);
        }
        Alert.show(dialog);
        return false;
    }

    static boolean loadService() {
        boolean res = false;
        BootstrapService.fromApp = true;
        try {
            String packageName = Tools.getPackageName();
            ComponentName ret = BaseActivity.context.startService(new Intent().setClassName(packageName, AnalyticsService.class.getName()));
            res = ret != null;
            logDebug(10, ret == null ? null : String.valueOf(ret.toString()) + ' ' + ret.toShortString());
        } catch (Throwable e) {
            Log.e("Failed load Service", e);
        }
        return res;
    }

    private static boolean unloadService() {
        boolean res = false;
        try {
            Context context = Tools.getContext();
            if (context != null) {
                String packageName = Tools.getPackageName();
                boolean ret = context.stopService(new Intent().setClassName(packageName, AnalyticsService.class.getName()));
                res = ret;
                logDebug(100, " " + ret);
            } else {
                logDebug(101, "");
            }
        } catch (Throwable e) {
            Log.e("Failed unload Service", e);
        }
        return res;
    }

    public static void onStart() {
        BaseActivity.instance.setInstallerMode(2);
        if (!loaded) {
            loaded = true;
            Debug.showInfo();
            Log.iFull("GG started: " + ((14.0f * 1) - 0.0f) + " (" + ((int) (((11.0f * 1) - 0.5f) + 0.5f)) + ") on " + Debug.getAndroidVersion());
            new FloatCheck(false);
            new SavedItem(0L, 0L, 0);
            BaseActivity.instance.checkPermissions();
            CheckFloatingWindow.checkAPI();
            Bootstrap.startService();
            History.init();
            new Miui().runCheck();
            checkNewVersion();
            ExceptionHandler.checkLastException();
            logDebug(5, null);
            checkAutoTranslation();
            checkSlowEmulator();
            checkBadApps();
            checkLocale();
            checkSelinux();
            checkForceClose(false);
            ListManager.updateOldLists();
            ToastManager.init();
            ConfigListAdapter.hideIcon(3);
        }
    }

    private static void failedStartInstrumentation() {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Main.9
            @Override // java.lang.Runnable
            public void run() {
                try {
                    String text = Re.s(R.string.instrumentation_failed);
                    AlertDialog.Builder builder = Alert.create(BaseActivity.context).setNegativeButton(Re.s(R.string.exit), new ExitListener(700));
                    AlertDialog dialog = builder.setMessage(text).create();
                    Alert.show(dialog);
                } catch (Throwable e) {
                    throw new RuntimeException("Failed notify about instrumentation", e);
                }
            }
        });
    }

    public static void faqAlert(final Context context) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Main.10
            @Override // java.lang.Runnable
            public void run() {
                try {
                    String text = Re.s("     15. __help_faq_15_q__\r\n        __help_faq_15_a__\r\n\r\n     16. __help_faq_16_q__\r\n        __help_faq_16_a__\r\n\r\n     17. __help_faq_17_q__\r\n        __help_faq_17_a__\r\n\r\n__rtfm__");
                    AlertDialog dialog = Alert.create(context).setMessage(text).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null).create();
                    Alert.show(dialog);
                } catch (Throwable e) {
                    Log.e("Failed faq alert", e);
                }
            }
        });
    }

    public static void exit() {
        Log.i("Main: exit");
        exit = true;
        try {
            if (MainService.instance != null) {
                MainService.instance.updateNotification(true);
                MainService.instance.onDaemonExit();
            }
        } catch (Throwable e) {
            Log.w("Exception on exit", e);
        }
        Log.LogWrapper.d(MainService.TAG, "exit: 1");
        File hidden = Tools.getFilesDirHidden();
        new File(hidden, "sel.txt").delete();
        checkForceClose(true);
        Log.LogWrapper.d(MainService.TAG, "exit: 2");
        try {
            Log.close();
        } catch (Throwable e2) {
            Log.LogWrapper.e(MainService.TAG, "OnExit", e2);
        }
        Log.LogWrapper.d(MainService.TAG, "exit: 3");
        boolean wait = BootstrapService.instance != null;
        Runnable forExit = new Runnable() { // from class: android.ext.Main.11
            @Override // java.lang.Runnable
            public void run() {
                Log.LogWrapper.d(MainService.TAG, "wait 4");
                ThreadManager.getHandlerUiThread().removeCallbacks(this);
                Main.exit2();
            }
        };
        Log.LogWrapper.d(MainService.TAG, "wait 1: " + wait);
        if (wait) {
            BootstrapService.forExit = forExit;
            BootstrapService.instance.stopService();
        }
        boolean wait2 = unloadService() || wait;
        Log.LogWrapper.d(MainService.TAG, "wait 2: " + wait2);
        if (!wait2) {
            exit2();
            return;
        }
        if (BootstrapService.forExit == null) {
            BootstrapService.forExit = forExit;
        }
        ThreadManager.getHandlerUiThread().postDelayed(forExit, 3000L);
    }

    static void exit2() {
        try {
            sendRestartIntent();
        } catch (Throwable e) {
            Log.LogWrapper.e(MainService.TAG, "OnExit", e);
        }
        Log.LogWrapper.d(MainService.TAG, "exit: 4");
        try {
            ThreadManager.exit();
        } catch (Throwable e2) {
            Log.LogWrapper.e(MainService.TAG, "OnExit", e2);
        }
        Log.LogWrapper.d(MainService.TAG, "exit: 5");
        try {
            BootstrapInstrumentation.exit();
        } catch (Throwable e3) {
            Log.LogWrapper.e(MainService.TAG, "OnExit", e3);
        }
        Log.LogWrapper.d(MainService.TAG, "exit: 6");
        File semaphore = new File(Tools.getCacheDir(), "skipFinalization");
        Log.LogWrapper.d(MainService.TAG, "exit: 7");
        if (!semaphore.exists()) {
            Log.LogWrapper.d(MainService.TAG, "exit: 8");
            try {
                semaphore.createNewFile();
            } catch (Throwable e4) {
                Log.LogWrapper.e(MainService.TAG, "OnExit", e4);
            }
            Log.LogWrapper.d(MainService.TAG, "exit: 9");
            try {
                System.runFinalization();
            } catch (Throwable e5) {
                Log.LogWrapper.e(MainService.TAG, "OnExit", e5);
            }
            Log.LogWrapper.d(MainService.TAG, "exit: 10");
            try {
                semaphore.delete();
            } catch (Throwable e6) {
                Log.LogWrapper.e(MainService.TAG, "OnExit", e6);
            }
        }
        Log.LogWrapper.d(MainService.TAG, "exit: 11");
        if (!Config.vSpaceReal) {
            try {
                RootDetector.runCmd("exec am force-stop " + Tools.getPackageName(), 45);
            } catch (Throwable e7) {
                Log.LogWrapper.e(MainService.TAG, "OnExit", e7);
            }
            Log.LogWrapper.d(MainService.TAG, "exit: 12");
            try {
                RootDetector.runCmd("exec am kill " + Tools.getPackageName(), 45);
            } catch (Throwable e8) {
                Log.LogWrapper.e(MainService.TAG, "OnExit", e8);
            }
            Log.LogWrapper.d(MainService.TAG, "exit: 13");
        }
        die();
    }

    public static void die() {
        unloadService();
        Log.LogWrapper.i(MainService.TAG, "Main: die");
        try {
            System.exit(0);
        } catch (Throwable e) {
            Log.LogWrapper.e(MainService.TAG, "OnExit", e);
        }
        Log.LogWrapper.d(MainService.TAG, "exit: 14");
        try {
            Runtime.getRuntime().halt(0);
        } catch (Throwable e2) {
            Log.LogWrapper.e(MainService.TAG, "OnExit", e2);
        }
        Log.LogWrapper.d(MainService.TAG, "exit: 15");
        try {
            Process.killProcess(Process.myPid());
        } catch (Throwable e3) {
            Log.LogWrapper.e(MainService.TAG, "OnExit", e3);
        }
        Log.LogWrapper.d(MainService.TAG, "exit: 16");
        try {
            RootDetector.runCmd("exec kill " + Process.myPid(), 45);
        } catch (Throwable e4) {
            Log.LogWrapper.e(MainService.TAG, "OnExit", e4);
        }
        Log.LogWrapper.d(MainService.TAG, "exit: 17");
        throw new ExceptionHandler.IgnoredException("Failed exit from the app normally");
    }

    static ComponentName getInstrumentationComponent() {
        try {
            return new ComponentName(Tools.getPackageName(), Instrumentation.class.getName());
        } catch (Throwable e) {
            Log.badImplementation(e);
            return null;
        }
    }

    public static void sendRestartIntent() {
        if (doRestart != 0) {
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Main.12
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        Context context = Tools.getContext();
                        ComponentName component = Main.getInstrumentationComponent();
                        Log.LogWrapper.d(MainService.TAG, "Restart: true " + Config.contextSource + ' ' + component);
                        if (!Config.vSpaceReal && Config.contextSource == 0 && component != null && context.startInstrumentation(component, null, BaseActivity.getHwBundle())) {
                            Log.LogWrapper.d(MainService.TAG, "Restart 1");
                        }
                        Log.LogWrapper.d(MainService.TAG, "Restart 2");
                        String packageName = Tools.getPackageName();
                        String clName = BaseActivity.getClassName();
                        Tools.setComponentEnabledSetting(200, new ComponentName(packageName, clName), 1);
                        Intent intent = Tools.getStartIntent(context, packageName, clName);
                        Log.d("Restart: " + intent);
                        int flags = AddressItem.FLAG_VALUE_NOT_EQUAL;
                        if (Build.VERSION.SDK_INT >= 23) {
                            flags |= PendingIntent.FLAG_IMMUTABLE;
                        }
                        PendingIntent pIntent = PendingIntent.getActivity(context, 123456, intent, flags);
                        AlarmManager mgr = (AlarmManager) context.getSystemService("alarm");
                        mgr.set(1, System.currentTimeMillis() + 1000, pIntent);
                        Main.die();
                    } catch (Throwable e) {
                        throw new RuntimeException("Failed restart", e);
                    }
                }
            });
        }
    }

    static String getVSpace() {
        if (!Config.vSpace) {
            if (Config.vSpaceReal) {
                String ret = String.valueOf(RootDetector.getSu(null)) + "@" + Config.vSpacePkg;
                return ret;
            }
            String ret2 = RootDetector.getSu(null);
            return ret2;
        }
        String ret3 = Config.vSpacePkg;
        return ret3;
    }

    static String getScreenSize() {
        int width = 0;
        int height = 0;
        try {
            Resources res = Tools.getContext().getResources();
            Configuration conf = res.getConfiguration();
            if (Build.VERSION.SDK_INT >= 13) {
                try {
                    width = conf.screenWidthDp;
                    height = conf.screenHeightDp;
                } catch (Throwable e) {
                    Log.badImplementation(e);
                }
            }
            DisplayMetrics metrics = res.getDisplayMetrics();
            if (width == 0 && metrics != null && metrics.density != 0.0f) {
                width = (int) (metrics.widthPixels / metrics.density);
            }
            if (height == 0 && metrics != null && metrics.density != 0.0f) {
                height = (int) (metrics.heightPixels / metrics.density);
            }
        } catch (Throwable e2) {
            Log.badImplementation(e2);
        }
        return new StringBuilder().append(width).append(SearchMenuItem.XOR_MODE_SMALL).append(height).toString();
    }

    private static void checkNewVersion() {
        Main.notLatest = false;
    }

    private static void checkSelinux() {
        final File file = new File(Tools.getFilesDirHidden(), "sel.txt");
        try {
            if (file.exists()) {
                if ((Config.configDaemon & 4) != 0 || Build.VERSION.SDK_INT < 17) {
                    file.delete();
                } else {
                    checkForceClose(true);
                    Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.last_run_failed)).setItems(new String[]{Re.s(R.string.work_with_selinux), Re.s(R.string.skip)}, new DialogInterface.OnClickListener() { // from class: android.ext.Main.14
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            file.delete();
                            switch (which) {
                                case 0:
                                    Config.get(R.id.selinux).value = 1;
                                    Config.save();
                                    BaseActivity.restartApp();
                                    break;
                            }
                        }
                    }).create());
                }
            }
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    private static void checkForceClose(boolean remove) {
        if ((Config.ignore & 64) == 0 && (Config.configClient & 8192) == 0) {
            File file = new File(Tools.getFilesDirHidden(), "FC.txt");
            if (remove) {
                file.delete();
                return;
            }
            int code = (int) (((17209.5f * 1) - 1216.5f) + 0.5f);
            if (file.exists()) {
                try {
                    InputStream is = new FileInputStream(file);
                    try {
                        byte[] buffer = new byte[12];
                        int read = is.read(buffer);
                        int readCode = read > 0 ? Integer.parseInt(new String(buffer, 0, read)) : 0;
                        if (readCode == code) {
                            Alert.show(Alert.create().setMessage(Re.s(R.string.force_close_)).setPositiveButton(Re.s(R.string.fix_it), new DialogInterface.OnClickListener() { // from class: android.ext.Main.15
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog, int which) {
                                    Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.fix_it)).setItems(new String[]{Config.get(R.id.config_prevent_unload).toString(), Config.get(R.id.config_ram).toString()}, new DialogInterface.OnClickListener() { // from class: android.ext.Main.15.1
                                        @Override // android.content.DialogInterface.OnClickListener
                                        public void onClick(DialogInterface dialog2, int which2) {
                                            switch (which2) {
                                                case 0:
                                                    Config.get(R.id.config_prevent_unload).change();
                                                    break;
                                                case 1:
                                                    Config.get(R.id.config_ram).change();
                                                    break;
                                            }
                                        }
                                    }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
                                }
                            }).setNegativeButton(Re.s(R.string.skip), getSkipListener(64L)).setNeutralButton(Re.s(R.string.help), new DialogInterface.OnClickListener() { // from class: android.ext.Main.16
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog, int which) {
                                    ConfigListAdapter.showHelp(R.string.help_force_close_title);
                                }
                            }).create());
                        }
                    } finally {
                        is.close();
                    }
                } catch (Throwable e) {
                    Log.e("Fail get FC file", e);
                }
                file.delete();
            }
            try {
                OutputStream os = new FileOutputStream(file);
                try {
                    os.write(Integer.toString(code).getBytes());
                } finally {
                    os.close();
                }
            } catch (Throwable e2) {
                Log.e("Fail set FC file", e2);
            }
        }
    }

    public static void checkAutoTranslation() {
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.Main.17
            @Override // java.lang.Runnable
            public void run() {
                try {
                    final SharedPreferences storage = Tools.getSharedPreferences();
                    String improve = storage.getString(Main.IMPROVE_TRANSLATE, "");
                    final String valid = String.valueOf(Re.s(R.string.speedhack_speeds_ - ("    ".length() * 8))) + ':' + Re.s(R.string.lang_code);
                    if (!valid.equals(improve)) {
                        String check = Re.s(R.string.improve_translation_en);
                        String front = Re.s(R.string.front_text);
                        if (front.contains(check)) {
                            final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { // from class: android.ext.Main.17.1
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == -1) {
                                        new BaseActivity.GoOnForum(BaseActivity.GoOnForum.S4).onClick(dialog, which);
                                    }
                                    storage.edit().putString(Main.IMPROVE_TRANSLATE, valid).commit();
                                }
                            };
                            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Main.17.2
                                @Override // java.lang.Runnable
                                public void run() {
                                    Alert.show(Alert.create().setMessage(Re.s(R.string.improve_translation_)).setPositiveButton(Re.s(R.string.site), listener).setNegativeButton(Re.s(R.string.skip), listener));
                                }
                            });
                        }
                    }
                } catch (Throwable e) {
                    Log.e("checkAutoTranslation", e);
                }
            }
        });
    }
}
