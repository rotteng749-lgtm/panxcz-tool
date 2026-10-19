package android.ext;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.ext.Log;
import android.fix.ContextWrapper;
import android.os.Build;
import android.os.IBinder;
import catch_.me_.if_.you_.can_.AnalyticsService;

/* JADX INFO: loaded from: classes.dex */
public class BootstrapService extends Service {
    public static volatile BootstrapService instance;
    public static volatile boolean fromApp = false;
    public static volatile Runnable forExit = null;
    public static volatile boolean allow = false;
    public static volatile boolean stop = false;

    public BootstrapService() {
        instance = this;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        Log.d("BootstrapService onBind: " + intent);
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        Log.d("BootstrapService onCreate: " + fromApp + '-' + allow + ListManager.TEXT_SEPARATOR + instance + ListManager.TEXT_SEPARATOR + this);
        if (!fromApp) {
            stopService();
            return;
        }
        instance = this;
        super.onCreate();
        init();
    }

    private void init() {
        Tools.init(this);
        if (allow) {
            if (MainService.context == null) {
                MainService.context = ServiceContext.wrap(this);
            }
            ExceptionHandler.install();
        }
    }

    @Override // android.app.Service
    public void onStart(Intent intent, int startId) {
        Log.d("BootstrapService onStart: " + startId + ListManager.TEXT_SEPARATOR + intent);
        super.onStart(intent, startId);
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        Log.d("BootstrapService onUnbind: " + intent);
        return super.onUnbind(intent);
    }

    @Override // android.app.Service
    public void onRebind(Intent intent) {
        Log.d("BootstrapService onRebind: " + intent);
        super.onRebind(intent);
    }

    @Override // android.app.Service
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("BootstrapService onTaskRemoved: " + fromApp + '-' + stop + '-' + allow + ListManager.TEXT_SEPARATOR + rootIntent, new RuntimeException());
        if (fromApp) {
            checkReasonStop();
            super.onTaskRemoved(rootIntent);
        }
    }

    private void checkReasonStop() {
        if (!stop && allow) {
            String[] APPS = ExceptionHandler.getApps();
            for (int i = 2; i < APPS.length; i += 2) {
                String check = APPS[i];
                try {
                    if (Tools.isPackageInstalled(APPS[i + 1])) {
                        RuntimeException e = new RuntimeException("BootstrapService stop: " + check);
                        Log.w("Found: " + check, e);
                        ExceptionHandler.storeException(Thread.currentThread(), e);
                    }
                } catch (Throwable e) {
                    Log.badImplementation(e);
                }
            }
        }
    }

    void stopService() {
        try {
            stopForeground(true);
            Log.d("BootstrapService stopForeground");
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
        try {
            String packageName = Tools.getPackageName();
            boolean ret = stopService(new Intent().setClassName(packageName, AnalyticsService.class.getName()));
            Log.d("BootstrapService stop: " + ret);
        } catch (Throwable e2) {
            Log.e("Failed stop Service", e2);
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("BootstrapService onStartCommand: " + fromApp + '-' + allow + ListManager.TEXT_SEPARATOR + instance + ListManager.TEXT_SEPARATOR + this + ListManager.TEXT_SEPARATOR + MainService.context);
        Log.d("BootstrapService onStartCommand: " + startId + ' ' + flags + ' ' + intent);
        if (!fromApp) {
            stopService();
            return 2;
        }
        init();
        if (allow) {
            Main.onStart();
            Throwable e = null;
            int i = 0;
            int n = Config.vSpaceReal ? 4 : 2;
            while (i < n) {
                try {
                    Notification notification = MainService.instance.getNotification(i % 2 == 0, i >= 2);
                    startForeground(1, notification);
                    Log.d("startForeground 1." + i);
                    break;
                } catch (Throwable e2) {
                    if (e != null && Build.VERSION.SDK_INT >= 19) {
                        e2.addSuppressed(e);
                    }
                    if (i == n - 1) {
                        Log.w("Failed startForeground", e2);
                        ExceptionHandler.sendException(Thread.currentThread(), e2, false);
                    }
                    e = e2;
                    i++;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (forExit != null) {
            Log.LogWrapper.d(MainService.TAG, "wait 3");
            forExit.run();
        } else {
            if (!fromApp) {
                Log.d("BootstrapService onDestroy 1: " + fromApp + '-' + stop);
                return;
            }
            Log.d("BootstrapService onDestroy 2: " + fromApp + '-' + stop, new RuntimeException());
            checkReasonStop();
            stopForeground(true);
            if (!BootstrapInstrumentation.isBootstraped()) {
                Bootstrap.onDestroy();
            }
            super.onDestroy();
        }
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d("BootstrapService onConfigurationChanged: " + newConfig);
        if (fromApp) {
            Configuration newConfig2 = ContextWrapper.onConfigurationChanged(newConfig);
            super.onConfigurationChanged(newConfig2);
            MainService.onConfigurationChanged(newConfig2);
        }
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onLowMemory() {
        Log.d("BootstrapService onLowMemory");
        if (fromApp) {
            super.onLowMemory();
            MainService.onTrimMemory(-1);
        }
    }

    @Override // android.app.Service, android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
        Log.d("BootstrapService onTrimMemory: " + level);
        if (fromApp) {
            super.onTrimMemory(level);
            MainService.onTrimMemory(level);
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return SP.wrap(super.getSharedPreferences(name, mode));
    }
}
