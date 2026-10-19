package android.ext;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.UiAutomation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.ext.BaseActivity;
import android.fix.TextView;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import catch_.me_.if_.you_.can_.R;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
public class CheckFloatingWindow implements View.OnClickListener, DialogInterface.OnClickListener, DialogInterface.OnDismissListener, DialogInterface.OnShowListener, View.OnTouchListener {
    Activity parent;
    private static volatile boolean pass = false;
    private static View[] views = null;
    private static AlertDialog dialog = null;
    private static boolean beBad = false;

    public CheckFloatingWindow(Activity parent) {
        this.parent = parent;
    }

    public void doCheck() {
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.CheckFloatingWindow.1
            @Override // java.lang.Runnable
            public void run() {
                CheckFloatingWindow.this.check();
            }
        });
    }

    private void showView(View testView, int size) throws InterruptedException {
        showView(testView, size, SystemConstants.getTypePhone(), SystemConstants.getFlags() | 256, 0, 0);
    }

    private void showView(final View testView, int size, int type, int flags, int x, int y) throws InterruptedException {
        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = type;
        layoutParams.width = size;
        layoutParams.height = size;
        layoutParams.format = -2;
        layoutParams.flags = flags;
        layoutParams.alpha = 1.0f;
        layoutParams.x = x;
        layoutParams.y = y;
        layoutParams.gravity = 51;
        Runnable show = new Runnable() { // from class: android.ext.CheckFloatingWindow.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Tools.addView(testView, layoutParams);
                } catch (Throwable e) {
                    Log.e("Failed show test view(" + layoutParams.type + ", " + layoutParams.flags + "): " + testView + ListManager.TEXT_SEPARATOR + layoutParams, e);
                }
                synchronized (this) {
                    notify();
                }
            }
        };
        synchronized (show) {
            ThreadManager.runOnUiThread(show);
            show.wait();
        }
    }

    @TargetApi(12)
    public void sendClick(int x, int y) {
        try {
            java.lang.Process ps = Tools.exec(new String[]{"input", "tap", Integer.toString(x), Integer.toString(y)});
            Tools.waitForTimeout(ps, 15);
        } catch (IOException e) {
            Log.w("sendClick", e);
        }
        long time = SystemClock.uptimeMillis();
        MotionEvent eventDown = MotionEvent.obtain(time, time, 0, x, y, 0);
        MotionEvent eventUp = MotionEvent.obtain(time, time, 1, x, y, 0);
        if (Build.VERSION.SDK_INT >= 12) {
            eventDown.setSource(4098);
            eventUp.setSource(4098);
        }
        injectInputEvent(eventDown, true);
        injectInputEvent(eventUp, true);
        eventDown.recycle();
        eventUp.recycle();
    }

    @TargetApi(18)
    private void injectInputEvent(MotionEvent event, boolean sync) {
        UiAutomation ua;
        if (BootstrapInstrumentation.mInstance != null) {
            BootstrapInstrumentation.mInstance.sendPointerSync(event);
            if (Build.VERSION.SDK_INT >= 18 && (ua = BootstrapInstrumentation.mInstance.getUiAutomation()) != null) {
                ua.injectInputEvent(event, sync);
            }
        }
    }

    private void clearAll(boolean withDialog) {
        AlertDialog d;
        View[] list = views;
        if (list != null) {
            for (View v : list) {
                if (v != null) {
                    Tools.removeView(v);
                }
            }
            views = null;
        }
        if (withDialog && (d = dialog) != null) {
            Tools.dismiss(d);
            dialog = null;
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        Log.d("TestButton: onClick for " + v, new RuntimeException());
        if (v instanceof TestButton) {
            TestButton test = (TestButton) v;
            Log.d("onClick for " + test.name);
            Config.get(R.id.config_float_type).value = test.type;
            Config.get(R.id.config_float_flags).value = test.flags;
            Config.save();
            clearAll(true);
            BaseActivity.restartApp();
            return;
        }
        pass = true;
    }

    private static class TestButton extends TextView implements View.OnTouchListener {
        int flags;
        String name;
        int type;

        public TestButton(Context context, String name, int type, int flags) {
            super(context);
            this.name = name;
            this.type = type;
            this.flags = flags;
            setTextColor(Color.WHITE);
            setText(name);
            setOnTouchListener(this);
        }

        @Override // android.view.View
        public boolean dispatchTouchEvent(MotionEvent event) {
            Log.d("TestButton: dispatchTouchEvent for " + this.name + ": " + event, new RuntimeException());
            return super.dispatchTouchEvent(event);
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            Log.d("TestButton: onTouch for " + this.name + ": " + event, new RuntimeException());
            return false;
        }

        @Override // android.fix.TextView, android.widget.TextView, android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            Log.d("TestButton: onTouchEvent for " + this.name + ": " + event, new RuntimeException());
            return super.onTouchEvent(event);
        }

        @Override // android.view.View
        public boolean dispatchGenericMotionEvent(MotionEvent event) {
            Log.d("TestButton: dispatchGenericMotionEvent for " + this.name + ": " + event, new RuntimeException());
            return super.dispatchGenericMotionEvent(event);
        }

        @Override // android.view.View
        protected boolean dispatchGenericPointerEvent(MotionEvent event) {
            Log.d("TestButton: dispatchGenericPointerEvent for " + this.name + ": " + event, new RuntimeException());
            return super.dispatchGenericPointerEvent(event);
        }

        @Override // android.view.View
        protected boolean dispatchGenericFocusedEvent(MotionEvent event) {
            Log.d("TestButton: dispatchGenericFocusedEvent for " + this.name + ": " + event, new RuntimeException());
            return super.dispatchGenericFocusedEvent(event);
        }

        @Override // android.widget.TextView, android.view.View
        public boolean onGenericMotionEvent(MotionEvent event) {
            Log.d("TestButton: onGenericMotionEvent for " + this.name + ": " + event, new RuntimeException());
            return super.onGenericMotionEvent(event);
        }

        @Override // android.view.View
        public boolean dispatchKeyEvent(KeyEvent event) {
            Log.d("TestButton: dispatchKeyEvent for " + this.name + ": " + event, new RuntimeException());
            return super.dispatchKeyEvent(event);
        }

        @Override // android.view.View
        public boolean dispatchTrackballEvent(MotionEvent event) {
            Log.d("TestButton: dispatchTrackballEvent for " + this.name + ": " + event, new RuntimeException());
            return super.dispatchTrackballEvent(event);
        }
    }

    public void showDialog() {
        final AlertDialog d = Alert.create(this.parent).setNegativeButton(Re.s(R.string.cancel), this).setCancelable(true).create();
        d.setOnDismissListener(this);
        d.setCanceledOnTouchOutside(false);
        d.setCancelable(true);
        d.setOnShowListener(this);
        dialog = d;
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.CheckFloatingWindow.3
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Alert.tryShow(d);
                } catch (Throwable e) {
                    Log.e("Failed show dialog: " + d, e);
                }
            }
        });
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialog2) {
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.CheckFloatingWindow.4
            @Override // java.lang.Runnable
            public void run() {
                CheckFloatingWindow.this.checkAll();
            }
        });
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog2) {
        clearAll(true);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog2, int which) {
        clearAll(true);
    }

    private int getColor(Random rnd) {
        return Color.rgb(rnd.nextInt(AddressItem.FLAG_AUTO) + 64, rnd.nextInt(AddressItem.FLAG_AUTO) + 64, rnd.nextInt(AddressItem.FLAG_AUTO) + 64);
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        Log.d("dbg: " + v + ' ' + event, new RuntimeException());
        return false;
    }

    void checkAll() {
        View[] list = null;
        try {
            Random rnd = Tools.RANDOM;
            DisplayMetrics metrics = new DisplayMetrics();
            ((WindowManager) Tools.getContext().getSystemService("window")).getDefaultDisplay().getMetrics(metrics);
            int size = Tools.dp2px48();
            int line = ((metrics.widthPixels / size) / 2) * 2;
            int lines = metrics.heightPixels / size;
            int cnt = line * lines;
            if (cnt > 100) {
                cnt = 100;
            }
            Log.d("checkAll: " + size + ListManager.TEXT_SEPARATOR + line + ListManager.TEXT_SEPARATOR + cnt + ListManager.TEXT_SEPARATOR + size + ListManager.TEXT_SEPARATOR + metrics);
            int half = cnt / 2;
            int halfLine = line / 2;
            list = new View[cnt];
            for (int i = 0; i < cnt; i++) {
                boolean firstHalf = i / half == 0;
                int type = (i % half) + 2000;
                int flags = (firstHalf ? 0 : 32) | 8;
                int checkColor = getColor(rnd);
                TestButton btn = new TestButton(MainService.context.getApplicationContext(), String.valueOf(Integer.toString(i % half)) + (firstHalf ? "" : "'"), type, flags);
                btn.setBackgroundColor(checkColor);
                btn.setOnClickListener(this);
                try {
                    int x = (i % halfLine) + (firstHalf ? 0 : halfLine);
                    int y = (i % half) / halfLine;
                    showView(btn, size, type, flags, x * size, y * size);
                    list[i] = btn;
                } catch (Throwable e) {
                    Log.e("Failed show: " + type, e);
                }
            }
        } catch (Throwable e2) {
            Log.e("Failed checkAll floating window", e2);
        }
        clearAll(false);
        views = list;
    }

    void check() {
        try {
            Random rnd = Tools.RANDOM;
            int checkColor = getColor(rnd);
            int size = (int) Tools.dp2px(128.0f);
            View testView = new View(MainService.context);
            testView.setBackgroundColor(checkColor);
            testView.setOnClickListener(this);
            try {
                showView(testView, size);
                Thread.sleep(1000L);
                int[] coords = new int[2];
                testView.getLocationOnScreen(coords);
                int size2 = size >> 1;
                sendClick(coords[0] + size2, coords[1] + size2);
                Thread.sleep(1000L);
                Tools.removeView(testView);
                if (pass) {
                    Tools.showToast(R.string.check_success);
                } else {
                    showAlert();
                }
            } catch (Throwable th) {
                Tools.removeView(testView);
                throw th;
            }
        } catch (Throwable e) {
            Log.e("Failed check floating window", e);
        }
    }

    public static AlertDialog.Builder setupDialog(AlertDialog.Builder builder) {
        String app = Config.vSpaceReal ? Config.vSpaceName : Re.s(R.string.app_name);
        return builder.setCustomTitle(Tools.getCustomTitle(R.string.floating_window_fail)).setMessage(String.valueOf(Tools.stringFormat(Tools.stripColon(R.string.float_prompt), app, app, app, app)) + ":\nhttps://productforums.google.com/forum/#!topic/translate/1Pywh5vI1kE\n\n" + Tools.stripColon(R.string.without_float) + ":\n" + Tools.removeNewLinesChars("kwws=22jdphjxdugldq1qhw2y0573")).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null);
    }

    private void showAlert() {
        final AlertDialog.Builder builder = setupDialog(Alert.create(this.parent));
        builder.setPositiveButton(Re.s(R.string.site), new BaseActivity.GoOnForum(BaseActivity.GoOnForum.S3));
        if (Build.VERSION.SDK_INT >= 23) {
            builder.setNeutralButton(Re.s(R.string.fix_it), new DialogInterface.OnClickListener() { // from class: android.ext.CheckFloatingWindow.5
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog2, int which) {
                    Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION");
                    intent.setData(Uri.parse("package:" + (Config.vSpaceReal ? Config.vSpacePkg : Tools.getPackageName())));
                    CheckFloatingWindow.this.parent.startActivity(intent);
                }
            });
        }
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.CheckFloatingWindow.6
            @Override // java.lang.Runnable
            public void run() {
                try {
                    AlertDialog dialog2 = builder.create();
                    Alert.tryShow(dialog2);
                    Tools.setClickableText(dialog2);
                } catch (Throwable e) {
                    Log.e("Failed show float dialog", e);
                }
            }
        });
    }

    private static boolean canDrawOverlays(Context context) {
        boolean simple = Tools.canDrawOverlays(context);
        if (simple && Config.vSpaceReal) {
            int uid = Process.myUid();
            try {
                Method isCallingPackageAllowedToDrawOverlays = Settings.class.getDeclaredMethod("isCallingPackageAllowedToDrawOverlays", Context.class, Integer.TYPE, String.class, Boolean.TYPE);
                isCallingPackageAllowedToDrawOverlays.setAccessible(true);
                Object extended = isCallingPackageAllowedToDrawOverlays.invoke(null, context, Integer.valueOf(uid), Config.vSpacePkg, false);
                if ((extended instanceof Boolean) && !((Boolean) extended).booleanValue()) {
                    simple = false;
                }
                Log.d("canDrawOverlays extended 1: " + extended);
            } catch (Throwable e) {
                Log.w("Failed check canDrawOverlays", e);
            }
            try {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService("appops");
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", uid, Config.vSpacePkg);
                boolean extended2 = false;
                switch (mode) {
                    case 0:
                        extended2 = true;
                        break;
                    case 3:
                        if (Tools.getPackageManager().checkPermission("android.permission.SYSTEM_ALERT_WINDOW", Config.vSpacePkg) == 0) {
                            extended2 = true;
                        }
                        break;
                }
                if (!extended2) {
                    simple = false;
                }
                Log.d("canDrawOverlays extended 2: " + extended2 + ' ' + mode);
            } catch (Throwable e2) {
                Log.w("Failed check canDrawOverlays", e2);
            }
        }
        return simple;
    }

    @TargetApi(23)
    public static void checkAPI() {
        if (Build.VERSION.SDK_INT >= 23) {
            final Activity parent = BaseActivity.instance;
            try {
                boolean canDrawOverlays = canDrawOverlays(parent);
                Log.d("canDrawOverlays: " + canDrawOverlays);
                if (!canDrawOverlays) {
                    beBad = true;
                    final AlertDialog.Builder builder = Alert.create(parent);
                    builder.setCustomTitle(Tools.getCustomTitle(R.string.floating_window_fail)).setMessage(R.string.enable_draw_perm).setNegativeButton(Re.s(R.string.skip), (DialogInterface.OnClickListener) null).setPositiveButton(Re.s(R.string.ok), new DialogInterface.OnClickListener() { // from class: android.ext.CheckFloatingWindow.7
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog2, int which) {
                            try {
                                Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION");
                                intent.setData(Uri.parse("package:" + Tools.getPackageName()));
                                parent.startActivityForResult(intent, 0);
                            } catch (Throwable e) {
                                Log.e("Failed call intent", e);
                            }
                        }
                    });
                    ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.CheckFloatingWindow.8
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                Alert.tryShow(builder.create());
                            } catch (Throwable e) {
                                Log.e("Failed show float dialog", e);
                            }
                        }
                    });
                } else if (beBad) {
                    beBad = false;
                    ConfigListAdapter.needRestart();
                }
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
    }
}
