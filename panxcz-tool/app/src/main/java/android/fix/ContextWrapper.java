package android.fix;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.ext.ConfigListAdapter;
import android.ext.ListManager;
import android.ext.Log;
import android.ext.SP;
import android.ext.ThreadManager;
import android.ext.Tools;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import catch_.me_.if_.you_.can_.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class ContextWrapper extends ContextThemeWrapper {
    private Configuration localConfig;
    private Resources localRes;
    private static volatile boolean useFix = true;
    private static Configuration globalConfig = null;
    private static Locale globalLocale = Locale.getDefault();

    public ContextWrapper(Context base) {
        super(base, R.style.FixTheme);
        this.localConfig = null;
        this.localRes = null;
    }

    public static boolean isUseFix() {
        return useFix;
    }

    public static ContextWrapper getContextWrapper(Context context) {
        try {
            Class.forName("com.wobian.wv.wb.WBContext");
            return new ContextWrapper(context) { // from class: android.fix.ContextWrapper.1
                private Object mPackageInfo;

                {
                    try {
                        Context context2 = getBaseContext();
                        Class<?> cl = Class.forName("android.app.ContextImpl");
                        if (cl.isInstance(context2)) {
                            Field f = cl.getDeclaredField("mPackageInfo");
                            f.setAccessible(true);
                            this.mPackageInfo = f.get(context2);
                            Log.d("WBContext: " + this.mPackageInfo);
                        }
                    } catch (Throwable e) {
                        Log.badImplementation(e);
                    }
                }
            };
        } catch (Throwable th) {
            return new ContextWrapper(context);
        }
    }

    public static Context wrap(Context context) {
        if (context != null && !(context instanceof ContextWrapper)) {
            return getContextWrapper(context);
        }
        return context;
    }

    public static void setOverride(Configuration newConfig, Locale newLocale) {
        if (newLocale != null) {
            globalLocale = newLocale;
        }
        Configuration config = newConfig == null ? globalConfig : newConfig;
        if (config == null) {
            config = Tools.getContext().getResources().getConfiguration();
        }
        Configuration config2 = fixConfig(config);
        if (!config2.equals(globalConfig)) {
            globalConfig = config2;
        }
    }

    public static Configuration onConfigurationChanged(Configuration config) {
        Configuration config2 = fixConfig(config);
        setOverride(config2, null);
        return config2;
    }

    public static Configuration fixConfig(Configuration config) {
        Locale locale = globalLocale;
        ConfigListAdapter.setLocale(config, locale);
        return new Configuration(config);
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public Resources getResources() {
        Configuration global = globalConfig;
        Configuration local = this.localConfig;
        Resources superRes = null;
        Resources localRes = this.localRes;
        if (local != global || (localRes != null && !localRes.getConfiguration().equals(local))) {
            this.localConfig = global;
            superRes = super.getResources();
            if (global != null) {
                try {
                    superRes.updateConfiguration(global, null);
                    if (localRes != null && localRes != superRes) {
                        localRes.updateConfiguration(global, null);
                    }
                } catch (Throwable e) {
                    Log.e("Failed set locale", e);
                }
                Context tmp = null;
                if (Build.VERSION.SDK_INT >= 17) {
                    try {
                        tmp = createConfigurationContext(global);
                    } catch (Throwable e2) {
                        Log.badImplementation(e2);
                    }
                }
                localRes = tmp == null ? superRes : tmp.getResources();
            } else {
                localRes = superRes;
            }
        }
        if (localRes == null) {
            if (superRes == null) {
                superRes = super.getResources();
            }
            localRes = superRes;
        }
        this.localRes = localRes;
        return localRes;
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    public static void checkFix(Context con) {
        if (con instanceof ContextWrapper) {
            ContextWrapper context = (ContextWrapper) con;
            try {
                android.view.LayoutInflater.from(context);
            } catch (AssertionError e) {
                try {
                    android.view.LayoutInflater.from(context.getBaseContext());
                    Log.e("Failed use fix for LayoutInflater", e);
                    useFix = false;
                } catch (AssertionError e2) {
                    Log.e("Can not get LayoutInflater", e2);
                }
            }
            Log.e("Check LayoutInflater - ok");
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public File getFilesDir() {
        try {
            return super.getFilesDir();
        } catch (NullPointerException e) {
            Log.badImplementation(e);
            return Tools.getFilesDir();
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public File getCacheDir() {
        try {
            return super.getCacheDir();
        } catch (NullPointerException e) {
            Log.badImplementation(e);
            return Tools.getCacheDir();
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public String getPackageName() {
        try {
            return super.getPackageName();
        } catch (NullPointerException e) {
            Log.badImplementation(e);
            return Tools.getPackageName();
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public File getExternalFilesDir(String type) {
        try {
            return super.getExternalFilesDir(type);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.badImplementation(e);
            return null;
        } catch (NullPointerException e2) {
            Log.badImplementation(e2);
            return null;
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public File getExternalCacheDir() {
        try {
            return super.getExternalCacheDir();
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.badImplementation(e);
            return null;
        } catch (NullPointerException e2) {
            Log.badImplementation(e2);
            return null;
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public PackageManager getPackageManager() {
        try {
            return super.getPackageManager();
        } catch (NullPointerException e) {
            Log.badImplementation(e);
            return Tools.getPackageManager();
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void unregisterReceiver(BroadcastReceiver receiver) {
        try {
            super.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public Object getSystemService(String name) {
        return SystemService.wrap(super.getSystemService(name));
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivity(Intent intent) {
        boolean zStartActivityUI = false;
        boolean zExecIntent = false;
        try {
            super.startActivity(intent);
        } catch (Throwable e) {
            zStartActivityUI = startActivityUI(e, this, intent);
            if (!zStartActivityUI) {
                zExecIntent = execIntent(intent);
            }
            if (!zStartActivityUI && !zExecIntent) {
                if (e instanceof RuntimeException) {
                    throw ((RuntimeException) e);
                }
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean startActivityUI(Throwable e, final Context context, final Intent intent) {
        String msg = e.getMessage();
        if (ThreadManager.isInUiThread() || msg == null || !msg.contains("Looper.prepare()")) {
            return false;
        }
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.fix.ContextWrapper.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    context.startActivity(intent);
                } catch (Throwable e2) {
                    Log.w("Failed startActivity from UI thread: " + intent, e2);
                }
            }
        });
        return true;
    }

    private boolean execIntent(Intent intent) {
        String[] cmds = {"am", "start", intent.toUri(5)};
        Log.w("Try exec intent: " + intent + ListManager.TEXT_SEPARATOR + Arrays.toString(cmds));
        try {
            Process process = Tools.exec(cmds);
            process.getInputStream().close();
            BufferedReader out = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder err = new StringBuilder();
            while (true) {
                String line = out.readLine();
                if (line == null) {
                    break;
                }
                err.append(line);
                err.append('\n');
            }
            out.close();
            if (err.length() <= 0) {
                return true;
            }
            Log.w("Failed exec intent: " + intent + ListManager.TEXT_SEPARATOR + Arrays.toString(cmds) + ":\n" + err.toString());
            return false;
        } catch (Throwable e) {
            Log.w("Failed exec intent: " + intent + ListManager.TEXT_SEPARATOR + Arrays.toString(cmds), e);
            return false;
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        try {
            boolean ret = super.bindService(service, conn, flags);
            return ret;
        } catch (IllegalArgumentException e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments) {
        try {
            return super.startInstrumentation(className, profileFile, arguments);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return SP.wrap(super.getSharedPreferences(name, mode));
    }
}
