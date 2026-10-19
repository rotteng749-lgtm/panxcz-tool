package android.fix;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.ext.Alert;
import android.ext.Log;
import android.ext.MainService;
import android.ext.SP;
import android.ext.ThreadManager;
import android.ext.Tools;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class Activity extends android.app.Activity {
    private View mainView = null;
    private AlertDialog mainDialog = null;
    private Context wrapper = null;

    @Override // android.app.Activity
    public void startActivityForResult(final Intent intent, final int requestCode) {
        try {
            super.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            Log.badImplementation(e);
        } catch (SecurityException e2) {
            if (intent.getAction().equals("android.intent.action.UNINSTALL_PACKAGE")) {
                intent.setAction("android.intent.action.DELETE");
                startActivityForResult(intent, requestCode);
            } else {
                if (intent.getAction().equals("android.intent.action.INSTALL_PACKAGE")) {
                    intent.setAction("android.intent.action.VIEW");
                    startActivityForResult(intent, requestCode);
                    return;
                }
                throw e2;
            }
        } catch (RuntimeException e3) {
            String msg = e3.getMessage();
            if (!ThreadManager.isInUiThread() && msg != null && msg.contains("Looper.prepare()")) {
                ThreadManager.runOnUiThread(new Runnable() { // from class: android.fix.Activity.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Activity.this.startActivityForResult(intent, requestCode);
                    }
                });
                return;
            }
            throw e3;
        }
    }

    @Override // android.app.Activity, android.content.ContextWrapper, android.content.Context
    public void startActivity(Intent intent) {
        try {
            super.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.badImplementation(e);
        } catch (SecurityException e2) {
            if (intent.getAction().equals("android.intent.action.UNINSTALL_PACKAGE")) {
                intent.setAction("android.intent.action.DELETE");
                startActivity(intent);
            } else {
                if (intent.getAction().equals("android.intent.action.INSTALL_PACKAGE")) {
                    intent.setAction("android.intent.action.VIEW");
                    startActivity(intent);
                    return;
                }
                throw e2;
            }
        } catch (RuntimeException e3) {
            if (!ContextWrapper.startActivityUI(e3, this, intent)) {
                throw e3;
            }
        }
    }

    @Override // android.app.Activity
    public View findViewById(int id) {
        return this.mainView == null ? super.findViewById(id) : this.mainView.findViewById(id);
    }

    @Override // android.app.Activity
    public void setContentView(int layoutResID) {
        RuntimeException dbg = new RuntimeException();
        try {
            super.setContentView(layoutResID);
        } catch (RuntimeException e) {
            if (dbg != e) {
                try {
                    Log.e("Failed call super setContentView", e);
                } catch (RuntimeException e2) {
                    try {
                        View view = LayoutInflater.inflateStatic(layoutResID, (ViewGroup) null);
                        this.mainView = view;
                        AlertDialog dialog = Alert.create(this).setView(view).setTitle(getTitle()).setCancelable(false).create();
                        try {
                            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: android.fix.Activity.2
                                @Override // android.content.DialogInterface.OnCancelListener
                                public void onCancel(DialogInterface dialog2) {
                                    Activity.this.finish_();
                                }
                            });
                            dialog.setCancelable(true);
                        } catch (Throwable e22) {
                            Log.badImplementation(e22);
                        }
                        this.mainDialog = dialog;
                        dialog.show();
                    } finally {
                    }
                }
            } else {
                dbg = e;
            }
            try {
                getWindow().setContentView(layoutResID);
                if (Build.VERSION.SDK_INT >= 11) {
                    try {
                        getActionBar();
                    } catch (RuntimeException e23) {
                        if (dbg != e23) {
                            Log.e("Failed call setup action bar", e23);
                        } else {
                            dbg = e23;
                        }
                    }
                }
            } catch (RuntimeException e24) {
                if (dbg != e24) {
                    Log.e("Failed fix call setContentView", e24);
                }
                if (Build.VERSION.SDK_INT >= 19) {
                    e.addSuppressed(e24);
                    throw e;
                }
                throw e;
            }
        }
        if (Log.getStackTraceString(dbg).contains(getPackageName())) {
            this.mainView = null;
            super.setContentView(layoutResID - 1);
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
    public PackageManager getPackageManager() {
        try {
            return super.getPackageManager();
        } catch (NullPointerException e) {
            Log.badImplementation(e);
            return Tools.getPackageManager();
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
        } catch (SecurityException e3) {
            Log.badImplementation(e3);
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
        } catch (SecurityException e3) {
            Log.badImplementation(e3);
            return null;
        }
    }

    private void hideDialog() {
        try {
            if (this.mainDialog != null) {
                this.mainDialog.dismiss();
            }
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.app.Activity
    public void onDestroy() {
        hideDialog();
        try {
            super.onDestroy();
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    public void finish_() {
        try {
            super.finish();
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.app.Activity
    public void finish() {
        hideDialog();
        finish_();
    }

    @Override // android.app.Activity, android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public Object getSystemService(String name) {
        try {
            return SystemService.wrap(super.getSystemService(name));
        } catch (NullPointerException e) {
            Log.badImplementation(e);
            return null;
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

    @Override // android.app.Activity, android.view.ContextThemeWrapper, android.content.ContextWrapper
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ContextWrapper.wrap(newBase));
    }

    private Context getWrapper() {
        if (this.wrapper == null) {
            Context base = getBaseContext();
            if (base == null) {
                return null;
            }
            this.wrapper = ContextWrapper.wrap(base);
        }
        return this.wrapper;
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public AssetManager getAssets() {
        Context wrap = getWrapper();
        return wrap == null ? super.getAssets() : wrap.getAssets();
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public Resources getResources() {
        Context wrap = getWrapper();
        return wrap == null ? super.getResources() : wrap.getResources();
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onLowMemory() {
        Log.d("Activity onLowMemory");
        super.onLowMemory();
        MainService.onTrimMemory(-1);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
        Log.d("Activity onTrimMemory: " + level);
        super.onTrimMemory(level);
        MainService.onTrimMemory(level);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return SP.wrap(super.getSharedPreferences(name, mode));
    }

    @Override // android.app.Activity
    protected void onStart() {
        try {
            super.onStart();
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.app.Activity
    protected void onRestart() {
        try {
            super.onRestart();
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        try {
            super.onResume();
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.app.Activity
    protected void onPostResume() {
        try {
            super.onPostResume();
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        try {
            super.onPause();
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.app.Activity
    protected void onStop() {
        try {
            super.onStop();
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        try {
            super.onCreate(savedInstanceState, persistentState);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.app.Activity
    protected void onPostCreate(Bundle savedInstanceState) {
        try {
            super.onPostCreate(savedInstanceState);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.app.Activity
    public boolean moveTaskToBack(boolean nonRoot) {
        try {
            hideDialog();
            return super.moveTaskToBack(nonRoot);
        } catch (Throwable e) {
            Log.badImplementation(e);
            finish();
            return true;
        }
    }
}
