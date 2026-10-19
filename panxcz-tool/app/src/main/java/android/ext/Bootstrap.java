package android.ext;

import android.content.Context;
import android.fix.ContextWrapper;

/* JADX INFO: loaded from: classes.dex */
public class Bootstrap {
    public static void startService() {
        Context context;
        Log.d("Bootstrap startService");
        if (MainService.instance != null) {
            Log.d("Bootstrap startService: already");
            return;
        }
        if (BootstrapInstrumentation.isBootstraped()) {
            context = BootstrapInstrumentation.mInstance.getContext(false);
        } else {
            context = BootstrapService.instance != null ? BootstrapService.instance : MainService.context;
        }
        if (context != null) {
            start(context);
        } else {
            Log.w("Bootstrap startService: null");
        }
    }

    public static void start(Context context) {
        if (MainService.instance != null) {
            Log.d("Bootstrap start: already");
        }
        new MainService(ContextWrapper.wrap(context));
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Bootstrap.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (MainService.instance != null) {
                        MainService.instance.onCreate();
                    }
                } catch (Throwable e) {
                    Log.w("MainService onCreate fail", e);
                    throw e;
                }
            }
        });
    }

    public static void onDestroy() {
        Log.d("Bootstrap onDestroy");
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Bootstrap.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (MainService.instance != null) {
                        MainService.instance.onDestroy();
                    }
                } catch (Throwable e) {
                    Log.w("MainService onDestroy fail", e);
                }
            }
        });
    }
}
