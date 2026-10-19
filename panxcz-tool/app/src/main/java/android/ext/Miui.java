package android.ext;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import catch_.me_.if_.you_.can_.R;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class Miui {
    public static final int FLAG_SHOW_FLOATING_WINDOW = 134217728;
    public static final int OP_SYSTEM_ALERT_WINDOW = 24;
    private static final int VERSION_NOT_MIUI = -1;
    private static final int VERSION_UNKNOWN = 0;
    final Context base = BaseActivity.instance;
    private StringBuilder logMiui = new StringBuilder();
    private int version = -1;
    private Map<String, Integer> codes = new HashMap();

    public Miui() {
        this.codes.put("android.permission.SYSTEM_ALERT_WINDOW", 24);
    }

    public void runCheck() {
        if (Build.VERSION.SDK_INT >= 16) {
            ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.Miui.1
                @Override // java.lang.Runnable
                public void run() {
                    Miui.this.check();
                }
            });
        }
    }

    private void detect() {
        try {
            Process process = Tools.exec(new String[]{"getprop"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            this.logMiui.append("\ngetprop:\n");
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                if (line.contains("ro.product.device") && line.contains("_sprout")) {
                    this.version = -1;
                    break;
                }
                if (!line.contains("fpc.fp.miui.") && !line.contains("ro.ril.miui.imei")) {
                    if (line.contains("miui") || line.contains("MIUI")) {
                        Log.d("MIUI found: " + line);
                        this.logMiui.append(line);
                        this.logMiui.append('\n');
                        if (this.version == -1) {
                            this.version = 0;
                        }
                    }
                    if (line.contains("ro.miui.ui.version.name")) {
                        if (line.contains("V5")) {
                            this.version = 5;
                        }
                        if (line.contains("V6")) {
                            this.version = 6;
                        }
                    }
                }
            }
            in.close();
        } catch (Throwable e) {
            Log.e("MIUI-detect", e);
            this.logMiui.append("\nMIUI-detect failed:\n");
            this.logMiui.append(Log.getStackTraceString(e));
        }
    }

    @TargetApi(19)
    private int checkOp(int op) {
        try {
            ApplicationInfo appInfo = this.base.getApplicationInfo();
            AppOpsManager appOpsManager = (AppOpsManager) this.base.getSystemService("appops");
            Method m = AppOpsManager.class.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
            return ((Integer) m.invoke(appOpsManager, Integer.valueOf(op), Integer.valueOf(appInfo.uid), appInfo.packageName)).intValue();
        } catch (Throwable e) {
            Log.d("MIUI-checkOp(" + op + ") failed", e);
            this.logMiui.append("\nMIUI-checkOp(" + op + ") failed:\n");
            this.logMiui.append(Log.getStackTraceString(e));
            return -1;
        }
    }

    private boolean checkPermission(String permission) {
        this.logMiui.append("\nMIUI-checkPermission ");
        this.logMiui.append(permission);
        this.logMiui.append(" - ");
        Integer code = this.codes.get(permission);
        boolean result = false;
        if (code == null) {
            this.logMiui.append("unknown - ");
            result = true;
        } else if (code.intValue() == 24 && this.version == 5) {
            int SHOW_FLOATING_WINDOW = SHOW_FLOATING_WINDOW();
            result = (this.base.getApplicationInfo().flags & SHOW_FLOATING_WINDOW) == SHOW_FLOATING_WINDOW;
        } else {
            try {
                result = checkOp(code.intValue()) == 0;
            } catch (Throwable e) {
                Log.d("MIUI-checkPermission failed", e);
                this.logMiui.append("failed:\n");
                this.logMiui.append(Log.getStackTraceString(e));
                this.logMiui.append('\n');
            }
        }
        this.logMiui.append(result);
        return result;
    }

    @TargetApi(19)
    private void fixPermission(String permission) {
        this.logMiui.append("\nMIUI-fixPermission ");
        this.logMiui.append(permission);
        this.logMiui.append(" - ");
        Integer code = this.codes.get(permission);
        if (code == null) {
            this.logMiui.append("unknown");
            return;
        }
        if (code.intValue() == 24 && this.version == 5) {
            try {
                Tools.getPackageManager().setApplicationEnabledSetting(Tools.getPackageName(), 134217728, SHOW_FLOATING_WINDOW());
                this.logMiui.append("success 1");
            } catch (Throwable e) {
                Log.d("MIUI-fixPermission failed", e);
                this.logMiui.append("failed 1:\n");
                this.logMiui.append(Log.getStackTraceString(e));
                this.logMiui.append('\n');
            }
        }
        try {
            ApplicationInfo appInfo = this.base.getApplicationInfo();
            Method m = AppOpsManager.class.getMethod("setMode", Integer.TYPE, Integer.TYPE, String.class, Integer.TYPE);
            m.invoke((AppOpsManager) this.base.getSystemService("appops"), code, Integer.valueOf(appInfo.uid), appInfo.packageName, 0);
            this.logMiui.append("success 2");
        } catch (Throwable e2) {
            Log.d("MIUI-fixPermission failed", e2);
            this.logMiui.append("failed 2:\n");
            this.logMiui.append(Log.getStackTraceString(e2));
            this.logMiui.append('\n');
        }
    }

    String getYoutubeUrl() {
        switch (this.version) {
            case 6:
                return "kwws=22zzz1|rxwxeh1frp2zdwfkBy@k]ifLW:7SV;";
            default:
                return "kwws=22zzz1|rxwxeh1frp2zdwfkBy@3[{m:N}:ZmT";
        }
    }

    public void check() {
        try {
            detect();
            if (this.version != -1 && (Config.ignore & 2) == 0) {
                if (!checkPermission("android.permission.SYSTEM_ALERT_WINDOW")) {
                    fixPermission("android.permission.SYSTEM_ALERT_WINDOW");
                    final AlertDialog.Builder builder = Alert.create(this.base).setNeutralButton(Re.s(R.string.skip), Main.getSkipListener(2L));
                    if (!checkPermission("android.permission.SYSTEM_ALERT_WINDOW")) {
                        CheckFloatingWindow.setupDialog(builder);
                        builder.setPositiveButton(Re.s(R.string.fix_it), new DialogInterface.OnClickListener() { // from class: android.ext.Miui.2
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(Tools.removeNewLinesChars(Miui.this.getYoutubeUrl())));
                                    Miui.this.base.startActivity(browserIntent);
                                } catch (Throwable e) {
                                    Log.w("Failed call activity", e);
                                }
                            }
                        });
                    } else {
                        builder.setMessage(Re.s(R.string.miui_restart)).setPositiveButton(Re.s(R.string.restart), new ExitListener(1200, true)).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null);
                    }
                    ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Miui.3
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                AlertDialog dialog = builder.create();
                                Alert.tryShow(dialog);
                                Tools.setClickableText(dialog);
                            } catch (Throwable e) {
                                Log.e("Failed show miui dialog", e);
                            }
                        }
                    });
                }
            } else {
                return;
            }
        } catch (Throwable e) {
            Log.e("MIUI-check", e);
            this.logMiui.append("\nMIUI-check failed:\n");
            this.logMiui.append(Log.getStackTraceString(e));
        }
        sendLog();
    }

    private int SHOW_FLOATING_WINDOW() {
        if (Build.VERSION.SDK_INT != 19) {
            return 134217728;
        }
        return AddressItem.FLAG_MODE_HACKING;
    }

    private void sendLog() {
        String log = this.logMiui.toString();
        this.logMiui = new StringBuilder();
        this.logMiui.append("MIUI\nGG: ");
        this.logMiui.append((111.8f * 1) - 15.8f);
        this.logMiui.append(" [");
        this.logMiui.append((int) (((16968.1f * 1) - 975.1f) + 0.5f));
        this.logMiui.append("]\nAndroid: ");
        this.logMiui.append(Build.VERSION.RELEASE);
        this.logMiui.append("\nSDK: ");
        this.logMiui.append(Build.VERSION.SDK_INT);
        this.logMiui.append("\nappInfo.flags: ");
        this.logMiui.append("0x" + ToolsLight.prefixLongHex(8, this.base.getApplicationInfo().flags));
        this.logMiui.append("\ncheckOp(OP_SYSTEM_ALERT_WINDOW): ");
        this.logMiui.append(checkOp(24));
        this.logMiui.append(log);
        String log2 = this.logMiui.toString();
        String[] lines = log2.split("[\\r\\n]+");
        for (String line : lines) {
            Log.d(line);
        }
    }
}
