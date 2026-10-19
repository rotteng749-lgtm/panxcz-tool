package android.ext;

import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.fix.ContextWrapper;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class BootstrapInstrumentation extends Instrumentation {
    public static volatile BootstrapInstrumentation mInstance;
    public static volatile boolean mIsBootstraped = false;

    static {
        Log.d("Instrumentation clinit");
        Apk.init();
        ExceptionHandler.install();
    }

    public BootstrapInstrumentation() {
        mInstance = this;
        Log.d("Instrumentation init");
        ExceptionHandler.install();
    }

    public static boolean isBootstraped() {
        return mIsBootstraped;
    }

    public static void exit() {
        BootstrapInstrumentation instance = mInstance;
        if (instance != null) {
            instance.finish(0, new Bundle());
        }
    }

    @Override // android.app.Instrumentation
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        BaseActivity.setHwBundle(paramBundle);
        Tools.init(getTargetContext());
        Tools.init(getContext());
        if (MainService.context == null) {
            MainService.context = ServiceContext.wrap(getContext(false));
        }
        Log.d("Instrumentation onCreate " + paramBundle);
        ExceptionHandler.install();
        mInstance = this;
        mIsBootstraped = true;
        start();
    }

    public Context getContext(boolean target) {
        Context context = target ? getTargetContext() : getContext();
        Context context2 = !target ? getTargetContext() : getContext();
        if (context == null) {
            context = context2;
        }
        return ContextWrapper.wrap(context);
    }

    @Override // android.app.Instrumentation
    public void onStart() {
        super.onStart();
        String clName = BaseActivity.getClassName();
        Log.d("Instrumentation onStart " + clName);
        Context context = getContext(true);
        String packageName = context.getPackageName();
        Tools.setComponentEnabledSetting(100, new ComponentName(packageName, clName), 1);
        context.startActivity(Tools.getStartIntent(context, packageName, clName));
    }

    @Override // android.app.Instrumentation
    public boolean onException(Object obj, Throwable e) {
        Log.w("Instrumentation onException: " + obj, e);
        return super.onException(obj, e);
    }

    @Override // android.app.Instrumentation
    public void onDestroy() {
        Bootstrap.onDestroy();
        super.onDestroy();
    }
}
