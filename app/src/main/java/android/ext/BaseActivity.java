package android.ext;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.ext.Config;
import android.ext.Installer;
import android.fix.Activity;
import android.fix.ContextWrapper;
import android.fix.KeyboardView;
import android.fix.LayoutInflater;
import android.lang.ProcessImpl;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Process;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.ActivityMain;
import catch_.me_.if_.you_.can_.MainActivity;
import catch_.me_.if_.you_.can_.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class BaseActivity extends Activity implements View.OnClickListener {
    private static final String BUNDLE_HW_KEY = "hw";
    private static final String BUNDLE_REMOVE_INSTALLER = "reminst";
    private static final String BUNDLE_UID_KEY = "uid";
    private static final String BUNDLE_V_SPACE_KEY = "vspace";
    private static final String INSTALLER_STATE = "installer-state";
    public static volatile Context appContext;
    public static volatile Context context;
    public static volatile BaseActivity instance;
    private static volatile boolean mntExpand;
    public static volatile boolean waitExit = false;
    public static volatile boolean hw = true;
    public static volatile boolean removeInstaller = false;
    public static volatile boolean exitOnDestroy = true;
    volatile boolean installMode = true;
    private volatile boolean onTop = false;
    private WeakReference<View> weakRoot = new WeakReference<>(null);
    private WeakReference<TextView> weakTextArea = new WeakReference<>(null);
    private WeakReference<CharSequence> textCache = new WeakReference<>(null);
    private StringBuilder vSpaceLog = new StringBuilder();

    static {
        Log.d("BaseActivity clinit");
        Apk.init();
        ExceptionHandler.install();
        mntExpand = false;
    }

    public static Bundle getHwBundle() {
        Bundle params = new Bundle();
        params.putBoolean(BUNDLE_HW_KEY, hw);
        params.putInt(BUNDLE_UID_KEY, Process.myUid());
        params.putBoolean(BUNDLE_V_SPACE_KEY, Config.vSpace);
        if (removeInstaller) {
            Log.d("removeInstaller: to bundle");
            params.putBoolean(BUNDLE_REMOVE_INSTALLER, true);
        }
        return params;
    }

    public static void setHwBundle(Bundle params) {
        if (params != null) {
            if (params.containsKey(BUNDLE_REMOVE_INSTALLER)) {
                Log.d("removeInstaller: from bundle");
                removeInstaller = true;
            }
            if (params.containsKey(BUNDLE_HW_KEY)) {
                hw = params.getBoolean(BUNDLE_HW_KEY);
            }
            if (params.containsKey(BUNDLE_UID_KEY)) {
                int uid = params.getInt(BUNDLE_UID_KEY);
                int myUid = Process.myUid();
                if (params.getBoolean(BUNDLE_V_SPACE_KEY) && myUid != uid) {
                    Log.d("Kill by diff uids: " + myUid + " != " + uid);
                    Main.die();
                }
            }
        }
    }

    public static String getClassName() {
        return hw ? ActivityMain.class.getName() : MainActivity.class.getName();
    }

    public BaseActivity() {
        boolean sw = this instanceof MainActivity;
        if (sw || instance == null) {
            instance = this;
        }
        if (sw || context == null) {
            context = this;
        }
        hw = sw ? false : true;
        Log.d("BaseActivity init: " + this);
        ExceptionHandler.install();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.help /* 2131427347 */:
                    ConfigListAdapter.showHelp();
                    break;
                case R.id.language /* 2131427383 */:
                    ConfigListAdapter.configLanguage();
                    break;
                case R.id.fix_it /* 2131427384 */:
                    try {
                        showFixIt(context).show();
                    } catch (Throwable e) {
                        Log.badImplementation(e);
                        Alert.show(showFixIt(Tools.getContext()));
                        return;
                    }
                    break;
                case R.id.btn_stop_service /* 2131427385 */:
                    Log.d("Pushed exit");
                    finish();
                    new ExitListener(100).onClick(null, 0);
                    break;
                case R.id.check /* 2131427386 */:
                    new CheckFloatingWindow(this).doCheck();
                    break;
                case R.id.btn_start_usage /* 2131427387 */:
                    if (SystemConstants.useFloatWindows) {
                        try {
                            if (!moveTaskToBack(true)) {
                                finish();
                            }
                        } catch (Throwable e2) {
                            Log.badImplementation(e2);
                        }
                    }
                    MainService.instance.startHotkeyDetection();
                    break;
            }
        }
    }

    private AlertDialog showFixIt(Context context2) {
        AlertDialog.Builder customTitle = Alert.create(context2).setCustomTitle(Tools.getCustomTitle(R.string.fix_it));
        String[] strArr = new String[15];
        strArr[0] = Re.s(SystemConstants.useFloatWindows ? R.string.disable_floating_windows : R.string.enable_floating_windows);
        strArr[1] = Re.s((Config.configDaemon & 4) != 0 ? R.string.work_without_selinux : R.string.work_with_selinux);
        strArr[2] = Re.s(R.string.change_float_type);
        strArr[3] = Re.s(R.string.reset_float_type);
        strArr[4] = Config.get(R.id.config_use_notification).toString();
        strArr[5] = Config.get(R.id.config_hot_key).toString();
        strArr[6] = Config.get(R.id.config_context_source).toString();
        strArr[7] = Re.s(R.string.reset_ignore);
        strArr[8] = Re.s(R.string.su_command);
        strArr[9] = Re.s(R.string.record_logcat);
        strArr[10] = Re.s(R.string.show_debug_info);
        strArr[11] = Config.get(R.id.config_prevent_unload).toString();
        strArr[12] = Config.get(R.id.config_acceleration).toString();
        strArr[13] = Config.get(R.id.config_vspace_root).toString();
        strArr[14] = Config.get(R.id.config_number_locale).toString();
        return customTitle.setItems(strArr, new DialogInterface.OnClickListener() { // from class: android.ext.BaseActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        FloatCheck.setPref(SystemConstants.useFloatWindows);
                        break;
                    case 1:
                        Config.get(R.id.selinux).value = (Config.configDaemon & 4) == 0 ? 1 : 0;
                        Config.save();
                        break;
                    case 2:
                        new CheckFloatingWindow(BaseActivity.this).showDialog();
                        return;
                    case 3:
                        Config.get(R.id.config_float_type).value = -1;
                        Config.get(R.id.config_float_flags).value = -1;
                        Config.save();
                        break;
                    case 4:
                        Config.get(R.id.config_use_notification).value = (Config.configClient & 256) == 0 ? 1 : 0;
                        Config.save();
                        break;
                    case 5:
                        Config.get(R.id.config_hot_key).change();
                        return;
                    case 6:
                        BaseActivity.this.switchContext();
                        return;
                    case 7:
                        Config.ignore = 0L;
                        Config.save();
                        break;
                    case 8:
                        ConfigListAdapter.changeSu();
                        return;
                    case 9:
                        BaseActivity.recordLogcat();
                        return;
                    case 10:
                        BaseActivity.showDebugInfo();
                        return;
                    case 11:
                        Config.get(R.id.config_prevent_unload).change();
                        return;
                    case 12:
                        Config.get(R.id.config_acceleration).change();
                        return;
                    case 13:
                        Config.get(R.id.config_vspace_root).change();
                        return;
                    case 14:
                        Config.get(R.id.config_number_locale).change();
                        return;
                }
                BaseActivity.restartApp();
            }
        }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).create();
    }

    void switchContext() {
        final Config.Option option = Config.get(R.id.config_context_source);
        option.setOnChangedListner(new Config.Option.OnChangedListener() { // from class: android.ext.BaseActivity.2
            @Override // android.ext.Config.Option.OnChangedListener
            public void onChanged(int value) {
                option.setOnChangedListner(null);
                BaseActivity.restartApp();
            }
        });
        option.change();
    }

    static void writeLogcatHeader(OutputStream os, String logFilename, String vSpaceLog) {
        try {
            Calendar calendar = new GregorianCalendar();
            os.write(("\n\n" + calendar.getTime().toString() + '\n' + logFilename + '\n' + Debug.getInfo() + '\n' + Main.getVSpace() + '\n' + Tools.getContext().getFilesDir().getAbsolutePath() + '\n' + vSpaceLog + "\n\n").getBytes());
            os.flush();
        } catch (Throwable e) {
            Log.w("Fail write logcat header", e);
        }
    }

    void startRecordLogcat() {
        final String vSpaceLog = getVSpaceLog();
        if (Config.get(R.id.record_logcat).value == ((int) (((16331.2f * 1) - 338.2f) + 0.5f))) {
            Config.get(R.id.record_logcat).value = 0;
            Config.save();
            Thread recordLogcatThread = new ExceptionHandler.ThreadEx(new Runnable() { // from class: android.ext.BaseActivity.3
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        String logFilename = BaseActivity.getLogcatFilename();
                        File file = new File(logFilename);
                        OutputStream os = new FileOutputStream(file);
                        BaseActivity.writeLogcatHeader(os, logFilename, vSpaceLog);
                        String cmd = "exec logcat -v threadtime -b main -b system 2>&1 ; exit\n";
                        java.lang.Process process = RootDetector.tryRoot(cmd);
                        InputStream is = process.getInputStream();
                        byte[] buf = new byte[8192];
                        if (!Tools.isExited(process)) {
                            Tools.showToast(String.valueOf(Re.s(R.string.record_logcat)) + ": " + logFilename);
                        } else {
                            Tools.showToast(String.valueOf(Re.s(R.string.record_logcat)) + ": " + Re.s(R.string.error));
                        }
                        while (!Thread.interrupted()) {
                            int read = is.read(buf);
                            if (read > 0) {
                                os.write(buf, 0, read);
                                os.flush();
                            } else if (read == -1 && Tools.isExited(process)) {
                                break;
                            }
                        }
                        os.close();
                        Tools.showToast(String.valueOf(Re.s(R.string.record_logcat)) + ": " + Re.s(R.string.exit));
                    } catch (Throwable e) {
                        Log.badImplementation(e);
                        Tools.showToast(String.valueOf(Re.s(R.string.record_logcat)) + "> " + Re.s(R.string.error));
                    }
                }
            }, "RecordLogcat");
            recordLogcatThread.setDaemon(true);
            recordLogcatThread.start();
        }
    }

    static String getLogcatFilename() {
        String str;
        StringBuilder sb = new StringBuilder("GG_logcat_");
        if (Config.vSpace) {
            str = "v";
        } else {
            str = Config.vSpaceReal ? "R" : "r";
        }
        File baseDir = Tools.getFilesDirHidden();
        if (baseDir == null) {
            try {
                baseDir = Tools.getContext().getFilesDir();
            } catch (Throwable e) {
                baseDir = null;
            }
        }
        if (baseDir == null) {
            baseDir = new File(Tools.getSdcardPath());
        }
        File dir = new File(baseDir, "logs");
        dir.mkdirs();
        return new File(dir, sb.append(str).append((110.3f * 1) - 14.3f).append('_').append((int) (((16318.3f * 1) - 325.3f) + 0.5f)).append('_').append(Build.VERSION.RELEASE).append('_').append(Build.VERSION.SDK_INT).append(".log").toString()).getAbsolutePath();
    }

    static void recordLogcat() {
        String logFilename = getLogcatFilename();
        Alert.show(Alert.create(context).setMessage(Tools.stringFormat(Re.s(R.string.record_logcat_info), logFilename)).setPositiveButton(Re.s(R.string.ok), new DialogInterface.OnClickListener() { // from class: android.ext.BaseActivity.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                Config.get(R.id.record_logcat).value = (int) (((16571.4f * 1) - 578.4f) + 0.5f);
                Config.save();
                new ExitListener(1400, true).onClick(null, 0);
            }
        }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
    }

    private static void dbgAppend(StringBuilder dbg, int code) {
        Log.d("START: " + code);
        dbg.append('\n');
        dbg.append(code);
        dbg.append(": ");
    }

    static void showDebugInfo() {
        StringBuilder dbg = new StringBuilder();
        dbgAppend(dbg, 1);
        try {
            File dir = instance.getFilesDir();
            dbg.append(dir);
        } catch (Throwable e) {
            dbg.append(e);
        }
        dbgAppend(dbg, 2);
        int uid = Process.myUid();
        try {
            dbg.append(uid);
        } catch (Throwable e2) {
            dbg.append(e2);
        }
        dbgAppend(dbg, 3);
        try {
            dbg.append(Arrays.toString(instance.getPackageManager().getPackagesForUid(uid)));
        } catch (Throwable e3) {
            dbg.append(e3);
        }
        dbgAppend(dbg, 4);
        try {
            dbg.append(Arrays.toString(instance.getPackageManager().getPackageGids(instance.getPackageName())));
        } catch (Throwable e4) {
            dbg.append(e4);
        }
        dbgAppend(dbg, 5);
        try {
            dbg.append(instance.getPackageManager().getPackageUid(instance.getPackageName(), 0));
        } catch (Throwable e5) {
            dbg.append(e5);
        }
        dbgAppend(dbg, 6);
        try {
            dbg.append(instance.checkCallingOrSelfPermission(Tools.removeNewLinesChars("dqgurlg1shuplvvlrq1UHDGbVPV")));
        } catch (Throwable e6) {
            dbg.append(e6);
        }
        dbg.append(' ');
        dbg.append(Tools.getSharedPreferences().getInt("odd-vspace", 0));
        dbgAppend(dbg, 7);
        try {
            dbg.append(Build.VERSION.SDK_INT);
        } catch (Throwable e7) {
            dbg.append(e7);
        }
        dbgAppend(dbg, 8);
        try {
            String name = String.valueOf(Tools.RANDOM.nextInt()) + ".txt";
            File file = new File(instance.getFilesDir(), name);
            try {
                FileOutputStream os = new FileOutputStream(file);
                os.write(new byte[4096]);
                os.flush();
                FileDescriptor fdj = os.getFD();
                int fd = ProcessImpl.getFD(fdj);
                File procFd = new File("/proc/self/fd/" + fd);
                dbg.append(procFd.getPath());
                dbg.append(" -> ");
                dbg.append(procFd.getCanonicalPath());
                try {
                    FileInputStream is = new FileInputStream(file);
                    FileChannel fileChannel = is.getChannel();
                    MappedByteBuffer buf = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, 4096L);
                    checkMaps(dbg, name);
                    buf.capacity();
                    is.close();
                } catch (Throwable e8) {
                    dbg.append('\n');
                    dbg.append(e8);
                }
                dbg.append("\nN: '");
                dbg.append(Tools.getNativePath(file.getAbsolutePath()));
                dbg.append(SearchMenuItem.HEX_UTF_8);
                os.close();
            } finally {
                file.delete();
            }
        } catch (Throwable e9) {
            dbg.append(e9);
        }
        dbgAppend(dbg, 9);
        try {
            String pkg = instance.getPackageName();
            checkMaps(dbg, pkg);
        } catch (Throwable e10) {
            dbg.append(e10);
        }
        dbgAppend(dbg, 10);
        try {
            dbg.append(ConfigListAdapter.isClient64());
        } catch (Throwable e11) {
            dbg.append(e11);
        }
        dbgAppend(dbg, 11);
        try {
            File procFd2 = new File("/proc/self/exe");
            dbg.append(procFd2.getPath());
            dbg.append(" -> ");
            dbg.append(procFd2.getCanonicalPath());
        } catch (Throwable e12) {
            dbg.append(e12);
        }
        dbgAppend(dbg, 12);
        try {
            int pid = Process.myPid();
            dbg.append(pid);
        } catch (Throwable e13) {
            dbg.append(e13);
        }
        dbgAppend(dbg, 13);
        try {
            FileInputStream fis = new FileInputStream("/proc/self/status");
            byte[] bytes = new byte[65536];
            int read = fis.read(bytes);
            dbg.append(read);
            dbg.append(" -> ");
            if (read > 0) {
                dbg.append(new String(bytes, 0, read));
            }
            fis.close();
        } catch (Throwable e14) {
            dbg.append(e14);
        }
        Log.d("FIN");
        Tools.alertBigText(dbg.toString().trim(), 0);
    }

    private static void checkMaps(StringBuilder dbg, String name) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("/proc/self/maps"));
        while (true) {
            String line = br.readLine();
            if (line != null) {
                int pos = line.indexOf(name);
                if (pos >= 0) {
                    int start = pos;
                    while (start > 0 && line.charAt(start) > ' ') {
                        start--;
                    }
                    if (start < pos) {
                        dbg.append("\n'");
                        dbg.append(line.substring(start, line.length()).trim());
                        dbg.append(SearchMenuItem.HEX_UTF_8);
                    }
                }
            } else {
                br.close();
                return;
            }
        }
    }

    private void loadMainScreen() {
        fixDurationScale();
        setContentView(R.layout.main);
        View root = findViewById(R.id.root);
        this.weakRoot = new WeakReference<>(root);
        TextView installMsg = (TextView) root.findViewById(R.id.installing);
        installMsg.setText(Re.s(R.string.installing));
        Installer.weakBar = new WeakReference<>((ProgressBar) root.findViewById(R.id.progress_bar));
        Installer.weakBarText = new WeakReference<>((TextView) root.findViewById(R.id.progress_bar_text));
        Installer.weakMsg = new WeakReference<>(installMsg);
        Installer.weakCancel = new WeakReference<>((Button) root.findViewById(R.id.abort_button));
        setInstallerMode(0);
        load(true);
    }

    private void load(boolean selected) {
        View root = this.weakRoot.get();
        if (root == null) {
            return;
        }
        View starter = root.findViewById(R.id.starter);
        if (!selected) {
            if (starter != null) {
                TextView text = this.weakTextArea.get();
                if (text != null) {
                    Tools.fixLeak(text);
                    Tools.getViewForAttach(text);
                }
                Tools.getViewForAttach(starter);
                return;
            }
            return;
        }
        if (starter == null) {
            ViewGroup parent = (ViewGroup) LayoutInflater.inflateStatic(R.layout.main, (ViewGroup) null);
            ((ViewGroup) root).addView(Tools.getViewForAttach(parent.findViewById(R.id.starter)));
        }
        TextView textArea = (TextView) root.findViewById(R.id.front_text);
        this.weakTextArea = new WeakReference<>(textArea);
        for (int id : new int[]{R.id.btn_stop_service, R.id.btn_start_usage, R.id.check, R.id.fix_it, R.id.language, R.id.help}) {
            View v = root.findViewById(id);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(Re.s(tv.getText().toString()));
            }
            v.setOnClickListener(this);
        }
        Tools.fixViews(textArea);
    }

    private void loadText() {
        TextView text = this.weakTextArea.get();
        if (text != null) {
            CharSequence textData = this.textCache.get();
            if (textData != null) {
                text.setText(textData);
                return;
            }
            String before = String.valueOf(Re.s(R.string.download)) + Tools.removeNewLinesChars("#kwws=22jdphjxdugldq1qhw2grzqordg") + "\n\n" + Re.s(R.string.front_text) + "\n\n";
            String red = Re.s("     32. __help_faq_32_q__\r\n        __help_faq_32_b__\r\n\r\n__rtfm__");
            String after = "\n\n" + Re.s(R.string.help_front);
            Tools.setClickableText(text, String.valueOf(before) + red + after);
            Tools.colorizeHelp(text.getText());
            Tools.colorizeText(text.getText(), red, Tools.getColor(R.color.alert));
            this.textCache = new WeakReference<>(text.getText());
        }
    }

    private void clearText() {
        TextView text = this.weakTextArea.get();
        if (text != null) {
            text.setText("");
        }
        load(false);
    }

    void setInstallerMode(final int isInstallerMode) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.BaseActivity.5
            @Override // java.lang.Runnable
            public void run() {
                View root = (View) BaseActivity.this.weakRoot.get();
                if (root != null) {
                    View loader = root.findViewById(R.id.loader);
                    if (loader != null) {
                        loader.setVisibility(isInstallerMode == 0 ? 0 : 8);
                    }
                    View installer = root.findViewById(R.id.installer);
                    if (installer != null) {
                        installer.setVisibility(isInstallerMode == 1 ? 0 : 8);
                    }
                    View starter = root.findViewById(R.id.starter);
                    if (starter != null) {
                        starter.setVisibility(isInstallerMode != 2 ? 8 : 0);
                    }
                    if (isInstallerMode == 2) {
                        if (loader != null) {
                            Tools.getViewForAttach(loader);
                        }
                        if (installer != null) {
                            Tools.getViewForAttach(installer);
                        }
                    }
                }
            }
        });
    }

    public static class GoOnForum implements DialogInterface.OnClickListener, View.OnClickListener {
        public static final String S1 = "kwws=22jdphjxdugldq1qhw2grzqordg";
        public static final String S10 = "kwws=22jdphjxdugldq1qhw2iruxp2wrslf24<7540yluwxdo0vsdfhv0wr0uxq0jdphjxdugldq0zlwkrxw0urrw2";
        public static final String S11 = "kwws=22jdphjxdugldq1qhw2iruxp2wrslf2:6<;0jdwkhulqj0lqirupdwlrq0derxw0jj0huuruv2";
        public static final String S12 = "kwws=22jdphjxdugldq1qhw2iruxp2wrslf2594440elqdu|0vfulswv0zlwk0fruuxswhg0ru0lqydolg0khdghuv2";
        public static final String S2 = "kwws=22jdphjxdugldq1qhw2grqdwh";
        public static final String S3 = "kwws=22jdphjxdugldq1qhw2iruxp2wrslf2:7360nqrzq0sureohpv2Bgr@ilqgFrpphqw)frpphqw@76<88";
        public static final String S4 = "kwws=22jdphjxdugldq1qhw2iruxp2wrslf2:8770li0|rx0zdqw0wr0dgg0d0qhz0wudqvodwlrq0ru0lpsuryh0dq0h{lvwlqj2";
        public static final String S5 = "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2fdwhjru|250ylghr0wxwruldov2";
        public static final String S6 = "kwws=22jdphjxdugldq1qhw2iruxp2ilohv2fdwhjru|290oxd0vfulswv2";
        public static final String S7 = "kwws=22jdphjxdugldq1qhw2khos2";
        public static final String S8 = "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh278:0krz0wr0uhsodfh0dup0oleudulhv0zlwk0{;90oleudulhv0{0soruh2";
        public static final String S9 = "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh25<70krz0wr0uxq0lq0skrhql{rv0jdphjxdugldq2";
        private String url;

        public GoOnForum(String url) {
            this.url = url == null ? S1 : url;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            try {
                Tools.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(Tools.removeNewLinesChars(this.url))));
                Alert.dismissAll();
            } catch (Throwable e) {
                Log.w("Failed call activity", e);
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            onClick(null, 0);
        }
    }

    public static void restartApp() {
        try {
            Main.doRestart = 2;
            Main.exit();
        } catch (Throwable e) {
            throw new RuntimeException("Failed restart", e);
        }
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(INSTALLER_STATE, Installer.getState());
        super.onSaveInstanceState(outState);
    }

    private String getVSpaceLog() {
        StringBuilder log = this.vSpaceLog;
        this.vSpaceLog = null;
        return log == null ? "" : log.toString();
    }

    private void detectVSpace() {
        StringBuilder log = this.vSpaceLog;
        try {
            File filesFile = getFilesDir();
            String files = filesFile.getAbsolutePath();
            log.append(files);
            log.append('\n');
            log.append(Apk.PACKAGE);
            log.append('\n');
            setVSpace(files);
            if (!Config.vSpace && (instance.checkCallingOrSelfPermission(Tools.removeNewLinesChars("dqgurlg1shuplvvlrq1UHDGbVPV")) == 0 || !new File("/proc/self/exe").getCanonicalPath().startsWith("/system/"))) {
                log.append("? ");
                String name = String.valueOf(Tools.RANDOM.nextInt()) + ".txt";
                File check = new File(filesFile, name);
                FileOutputStream os = new FileOutputStream(check);
                os.write(new byte[4096]);
                os.flush();
                os.close();
                String realFiles = Tools.getNativePath(check.getAbsolutePath()).replace(String.valueOf('/') + name, "");
                check.delete();
                log.append(realFiles);
                log.append('\n');
                setVSpace(realFiles);
                if (Config.vSpace) {
                    String dirName = String.valueOf('/') + filesFile.getName();
                    String realPath = realFiles.replace(dirName, "");
                    String fakePath = files.replace(dirName, "");
                    String msg = "F2R: [" + realPath.length() + "] " + fakePath + " -> " + realPath;
                    Log.w(msg);
                    log.append(msg);
                    log.append('\n');
                    Tools.fakeToReal = new String[]{fakePath, realPath};
                } else {
                    String msg2 = "Odd vSpace: " + files + ListManager.TEXT_SEPARATOR + realFiles;
                    Log.w(msg2);
                    log.append(msg2);
                    log.append('\n');
                    int odd = Tools.getSharedPreferences().getInt("odd-vspace", 0);
                    log.append(odd);
                    log.append('\n');
                    if (odd == 1) {
                        Config.vSpace = true;
                    } else if (odd == 0) {
                        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { // from class: android.ext.BaseActivity.6
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                boolean yes = which == -1;
                                new SPEditor().putInt("odd-vspace", yes ? 1 : 2).commit();
                                if (yes) {
                                    ConfigListAdapter.needRestart();
                                }
                            }
                        };
                        Alert.show(Alert.create(context).setMessage(Re.s(R.string.odd_vspace)).setCancelable(false).setPositiveButton(Re.s(R.string.yes), listener).setNegativeButton(Re.s(R.string.no), listener));
                    }
                }
            }
            String msg3 = "Files: " + files + ' ' + Apk.PACKAGE;
            Log.d(msg3);
            log.append(msg3);
            log.append('\n');
        } catch (Throwable e) {
            Log.badImplementation(e);
            log.append(e.toString());
            log.append('\n');
        }
        Config.vSpaceReal = Config.vSpace;
        if (Config.vSpace && (Config.configClient & 512) != 0) {
            Config.vSpace = false;
        }
    }

    private void setVSpace(String files) {
        StringBuilder log = this.vSpaceLog;
        String vpkg = null;
        if (files.startsWith("/mnt/expand/")) {
            files = files.replace("/mnt/expand/" + files.split("/", -1)[3], "/data");
            mntExpand = true;
            log.append("1\n");
        }
        if (files.startsWith("/data/data/")) {
            vpkg = files.split("/", -1)[3];
            log.append("2\n");
        } else if (files.startsWith("/data/user/")) {
            vpkg = files.split("/", -1)[4];
            log.append("3\n");
        }
        Config.vSpaceName = "";
        if (vpkg != null) {
            log.append(vpkg);
            log.append("\n4\n");
            String selfPkg = null;
            try {
                selfPkg = getPackageName();
            } catch (Throwable th) {
            }
            boolean isOwn = selfPkg != null && selfPkg.length() > 0 && (vpkg.equals(selfPkg) || vpkg.startsWith(selfPkg) || selfPkg.startsWith(vpkg));
            boolean isOfficial = vpkg.startsWith(Apk.PACKAGE);
            Config.vSpace = (!isOwn && !isOfficial) ? true : false;
            Config.vSpacePkg = vpkg;
            Config.vSpaceGuest = vpkg.contains("vmos");
            try {
                Config.vSpaceName = Tools.getApplicationLabel(Tools.getApplicationInfo(vpkg));
            } catch (Throwable e) {
                Log.w("Failed get vSpace name for " + vpkg, e);
            }
        }
        if (Config.vSpaceName.length() == 0) {
            Config.vSpaceName = Re.s(R.string.virtual_space);
        }
        log.append(Config.vSpace);
        log.append('\n');
    }

    @Override // android.fix.Activity, android.app.Activity
    public void onCreate(final Bundle savedInstanceState) {
        String str;
        super.onCreate(savedInstanceState);
        ExceptionHandler.install();
        boolean sw = this instanceof MainActivity;
        if (sw || instance == null) {
            instance = this;
        }
        if (sw || !(context instanceof ContextWrapper)) {
            context = ContextWrapper.wrap(this);
        }
        if (sw || appContext == null) {
            appContext = ContextWrapper.wrap(getApplicationContext());
        }
        try {
            Button btn = new android.fix.Button(this);
            btn.setText("Fix it");
            btn.setId(R.id.fix_it);
            btn.setOnClickListener(this);
            setContentView(btn);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
        detectVSpace();
        KeyboardView.inEditMode = false;
        Tools.init((android.app.Activity) this);
        Window window = getWindow();
        if (window != null) {
            if (hw) {
                window.addFlags(AddressItem.FLAG_REVERT);
            } else {
                window.clearFlags(AddressItem.FLAG_REVERT);
            }
        }
        ContextWrapper.checkFix(context);
        AppLocale.loadLocale();
        startRecordLogcat();
        if (Config.vSpace) {
            str = "$";
        } else {
            str = Config.vSpaceReal ? "@" : "#";
        }
        setTitle(String.valueOf(str) + ' ' + Re.s(R.string.app_name) + ' ' + ((14.0f * 1) - 0.0f) + " (" + ((int) (((11.0f * 1) - 0.5f) + 0.5f)) + ") " + (hw ? "HW" : "SW"));
        loadMainScreen();
        Tools.checkForBadContext();
        Runnable callback = new Runnable() { // from class: android.ext.BaseActivity.7
            private boolean called = false;

            @Override // java.lang.Runnable
            public void run() {
                Log.d(this + ": 200");
                if (this.called) {
                    Log.d(this + ": already");
                } else {
                    this.called = true;
                    BaseActivity.this.onCreate2(savedInstanceState);
                }
            }
        };
        Log.d(callback + ": 10");
        Installer.removeInstaller(callback);
        Log.d(callback + ": 20");
    }

    void onCreate2(final Bundle savedInstanceState) {
        boolean work = true;
        if (!Config.vSpaceReal && !Installer.needInstall() && Tools.getSharedPreferences().getInt(DaemonManager.ROOT_OK, 0) != ((int) (((17016.5f * 1) - 1023.5f) + 0.5f))) {
            try {
                String out = RootDetector.runCmd("exec id", 10);
                if (!out.contains("uid=0")) {
                    work = false;
                } else {
                    new SPEditor().putInt(DaemonManager.ROOT_OK, (int) (((23817.6f * 1) - 7824.6f) + 0.5f)).commit();
                }
            } catch (Throwable th) {
                work = false;
            }
        }
        if (work) {
            onCreate3(savedInstanceState);
            return;
        }
        final Runnable callback = new Runnable() { // from class: android.ext.BaseActivity.8
            private boolean called = false;

            @Override // java.lang.Runnable
            public void run() {
                Log.d(this + ": 1200");
                if (this.called) {
                    Log.d(this + ": already");
                } else {
                    this.called = true;
                    BaseActivity.this.onCreate3(savedInstanceState);
                }
            }
        };
        AlertDialog dialog = Alert.create(context).setTitle(Re.s(R.string.work_mode)).setCancelable(false).setItems(new String[]{Re.s(R.string.root), Re.s(R.string.no_root), Re.s(R.string.exit)}, new DialogInterface.OnClickListener() { // from class: android.ext.BaseActivity.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog2, int which) {
                if (which == 0) {
                    callback.run();
                } else if (which == 1) {
                    Alert.show(Alert.create(BaseActivity.context).setMessage(Re.s(R.string.how_no_root)).setCancelable(false).setPositiveButton(Re.s(R.string.ok), new DialogInterface.OnClickListener() { // from class: android.ext.BaseActivity.9.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog3, int which2) {
                            try {
                                BaseActivity.context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(Tools.removeNewLinesChars(GoOnForum.S10))));
                            } catch (Throwable e) {
                                Log.w("Failed call activity", e);
                            }
                            new ExitListener(1400).onClick(null, 0);
                        }
                    }));
                    BaseActivity.checkExpand();
                } else {
                    new ExitListener(1500).onClick(null, 0);
                }
            }
        }).create();
        Alert.show(dialog, callback, false);
    }

    void onCreate3(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Parcelable p = savedInstanceState.getParcelable(INSTALLER_STATE);
            if (p instanceof Installer.State) {
                Installer.setState((Installer.State) p);
            }
        }
        if (Main.loaded) {
            setInstallerMode(2);
        } else {
            Main.start();
        }
    }

    private void nothing() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkExpand() {
        if (mntExpand) {
            Alert.show(Alert.create(context).setMessage(Re.s(R.string.mnt_expand)).setNeutralButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null).setPositiveButton(Re.s(R.string.fix_it), new DialogInterface.OnClickListener() { // from class: android.ext.BaseActivity.10
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        BaseActivity.context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://support.google.com/android/answer/6088895")));
                    } catch (Throwable e) {
                        Log.w("Failed call activity", e);
                    }
                }
            }));
        }
    }

    @Override // android.fix.Activity, android.app.Activity
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.i(String.valueOf(this.installMode ? "Installer: " : "") + "onPostCreate()");
        super.onPostCreate(savedInstanceState);
    }

    @Override // android.fix.Activity, android.app.Activity
    protected void onRestart() {
        Log.i(String.valueOf(this.installMode ? "Installer: " : "") + "onRestart()");
        setOnTop(true);
        load(true);
        super.onRestart();
    }

    @Override // android.fix.Activity, android.app.Activity
    protected void onPostResume() {
        Log.i(String.valueOf(this.installMode ? "Installer: " : "") + "onPostResume()");
        setOnTop(true);
        load(true);
        super.onPostResume();
    }

    @Override // android.fix.Activity, android.app.Activity
    protected void onStart() {
        Log.i(String.valueOf(this.installMode ? "Installer: " : "") + "onStart()");
        setOnTop(true);
        load(true);
        loadText();
        super.onStart();
    }

    @Override // android.fix.Activity, android.app.Activity
    public void onResume() {
        Log.i(String.valueOf(this.installMode ? "Installer: " : "") + "onResume()");
        setOnTop(true);
        load(true);
        super.onResume();
        if (!this.installMode && MainService.instance != null) {
            if (SystemConstants.useFloatWindows) {
                MainService.instance.stopHotkeyDetection();
            } else if (MainService.instance.mainDialog == null) {
                staticPost();
            }
        }
    }

    private static void staticPost() {
        ThreadManager.getHandlerUiThread().post(new Runnable() { // from class: android.ext.BaseActivity.11
            @Override // java.lang.Runnable
            public void run() {
                MainService.instance.startHotkeyDetection();
            }
        });
    }

    @Override // android.fix.Activity, android.app.Activity
    public void onPause() {
        Log.i(String.valueOf(this.installMode ? "Installer: " : "") + "onPause()");
        setOnTop(false);
        if (!this.installMode && MainService.instance != null && SystemConstants.useFloatWindows) {
            MainService.instance.startHotkeyDetection();
        }
        super.onPause();
    }

    @Override // android.fix.Activity, android.app.Activity
    public void onStop() {
        Log.i(String.valueOf(this.installMode ? "Installer: " : "") + "onStop()");
        setOnTop(false);
        clearText();
        super.onStop();
    }

    @Override // android.fix.Activity, android.app.Activity
    public void onDestroy() {
        setOnTop(false);
        clearText();
        super.onDestroy();
        if (!this.installMode && exitOnDestroy) {
            Log.i("onDestroy(exit)");
            Main.exit();
        } else {
            Log.i(String.valueOf(this.installMode ? "Installer: " : "") + "onDestroy()");
        }
    }

    public void setOnTop(boolean value) {
        boolean old = this.onTop;
        this.onTop = value;
        if (old != value && MainService.instance != null) {
            MainService.instance.updateNotification();
        }
    }

    public boolean isOnTop() {
        return this.onTop;
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(String.valueOf(this.installMode ? "Installer: " : "") + "onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);
        if (this.installMode) {
            Installer.onActivityResult(requestCode, resultCode, data);
        } else {
            CheckFloatingWindow.checkAPI();
        }
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        try {
            return super.onKeyUp(keyCode, event);
        } catch (IllegalStateException e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (IllegalStateException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            return super.dispatchKeyEvent(event);
        } catch (IllegalStateException e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions != null && grantResults != null) {
            int count = Math.min(permissions.length, grantResults.length);
            for (int i = 0; i < count; i++) {
                String permission = permissions[i];
                if (permission != null) {
                    Log.d(String.valueOf(permission) + ": " + grantResults[i]);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static void fixDurationScale() {
        if (Build.VERSION.SDK_INT >= 11) {
            try {
                Field f = ValueAnimator.class.getDeclaredField("sDurationScale");
                f.setAccessible(true);
                Object sDurationScale = f.get(null);
                if ((sDurationScale instanceof Float) && Float.valueOf(0.0f).equals(sDurationScale)) {
                    f.set(null, Float.valueOf(1.0f));
                    Log.d("Fix sDurationScale");
                }
            } catch (Throwable e) {
                Log.w("failed fix sDurationScale", e);
            }
        }
    }

    public void checkPermissions() {
        if (!Config.vSpaceReal) {
            StringBuilder report = new StringBuilder();
            try {
                report.append("INTERNET: 0~");
                report.append(Config.vSpaceReal ? '$' : '#');
                report.append(";1~");
                Context context2 = Tools.getContext();
                int status = context2.checkPermission("android.permission.INTERNET", Process.myPid(), Process.myUid());
                report.append(status);
                report.append(";2~");
                report.append(context2.checkCallingOrSelfPermission("android.permission.INTERNET"));
                report.append(";3~");
                report.append(context2.checkCallingPermission("android.permission.INTERNET"));
                Log.d(report.toString());
                if (status != 0) {
                    ExceptionHandler.sendException(Thread.currentThread(), new RuntimeException(report.toString()), false);
                }
            } catch (Throwable e) {
                RuntimeException re = new RuntimeException(report.toString(), e);
                Log.badImplementation(re);
                ExceptionHandler.sendException(Thread.currentThread(), re, false);
            }
        }
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Context context3 = Tools.getContext();
                PackageInfo info = Tools.getPackageInfo(Tools.getPackageName(), 4096);
                if (info != null && info.requestedPermissions != null) {
                    List<String> list = new ArrayList<>();
                    PackageManager pm = Tools.getPackageManager();
                    for (String permission : info.requestedPermissions) {
                        if (permission != null && (context3.checkSelfPermission(permission) != 0 || (Config.vSpaceReal && pm.checkPermission(permission, Config.vSpacePkg) != 0))) {
                            list.add(permission);
                        }
                    }
                    if (list.size() > 0) {
                        String[] perms = (String[]) list.toArray(new String[list.size()]);
                        Log.d("requestPermissions: " + Arrays.toString(perms));
                        requestPermissions(perms, 0);
                    }
                }
            } catch (Throwable e2) {
                Log.badImplementation(e2);
            }
        }
    }
}
