package android.ext;

import android.content.Context;
import android.ext.Tools;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes.dex */
public class ToastManager {
    private static Context fix;
    private static Context source;
    private static volatile Toast lastToast = null;
    private static final Object lastMonitor = new Object();
    private static volatile WeakReference<Toast> lastUserToast = null;
    private static final Object lastUserMonitor = new Object();
    private static volatile WeakReference<Runnable> lastUserHide = null;

    static {
        final Handler handler = ThreadManager.getHandlerUiThread();
        handler.postDelayed(new Runnable() { // from class: android.ext.ToastManager.1
            @Override // java.lang.Runnable
            public void run() {
                Toast prevToast;
                try {
                    try {
                        synchronized (ToastManager.lastMonitor) {
                            prevToast = ToastManager.lastToast;
                            ToastManager.lastToast = null;
                        }
                        if ((Config.configClient & 8192) != 0) {
                            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ToastManager.1.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    try {
                                        Toast toast = Toast.makeText(ToastManager.getContext(), " ", 1);
                                        View v = toast.getView();
                                        v.setBackgroundColor(0);
                                        toast.setView(v);
                                        ToastManager.fixToast(toast).show();
                                        synchronized (ToastManager.lastMonitor) {
                                            ToastManager.lastToast = toast;
                                        }
                                    } catch (Throwable e2) {
                                        Log.w("Toast show", e2);
                                    }
                                }
                            });
                        }
                        ToastManager.cancel(prevToast);
                    } catch (Throwable e2) {
                        Log.e("toastThread", e2);
                    }
                } catch (Throwable e3) {
                    Log.e("toastThread", e3);
                }
                handler.postDelayed(this, 1500L);
            }
        }, 3000L);
        source = null;
        fix = null;
    }

    public static void init() {
    }

    public static Context getContext() {
        Context curr = Tools.getContext();
        if (Config.vSpaceReal && !Config.vSpaceGuest) {
            if (source == null || source != curr) {
                fix = Config.fixContext(curr);
                source = curr;
            }
            return fix;
        }
        return curr;
    }

    public static void show(final Toast toast) {
        Toast prevUserToast;
        Runnable lastHide;
        Toast prevToast;
        synchronized (lastUserMonitor) {
            WeakReference<Toast> wrToast = lastUserToast;
            prevUserToast = wrToast == null ? null : wrToast.get();
            lastUserToast = null;
            WeakReference<Runnable> wrRunnable = lastUserHide;
            lastHide = wrRunnable != null ? wrRunnable.get() : null;
            lastUserHide = null;
        }
        fixToast(toast).show();
        Runnable hide = new Runnable() { // from class: android.ext.ToastManager.2
            @Override // java.lang.Runnable
            public void run() {
                ToastManager.cancel(toast);
            }
        };
        synchronized (lastUserMonitor) {
            lastUserToast = new WeakReference<>(toast);
            lastUserHide = new WeakReference<>(hide);
        }
        synchronized (lastMonitor) {
            prevToast = lastToast;
            lastToast = null;
        }
        cancel(prevToast);
        cancel(prevUserToast);
        Handler handler = ThreadManager.getHandlerUiThread();
        if (lastHide != null) {
            handler.removeCallbacks(lastHide);
        }
        handler.postDelayed(hide, 3000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void cancel(final Toast prevToast) {
        if (prevToast != null) {
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ToastManager.3
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        prevToast.cancel();
                    } catch (Throwable e) {
                        Log.w("Toast cancel", e);
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Toast fixToast(Toast t) {
        try {
            Field f = Toast.class.getDeclaredField("mTN");
            f.setAccessible(true);
            Object mTN = f.get(t);
            for (Field field : mTN.getClass().getDeclaredFields()) {
                String name = field.getName();
                if (name.equals("mShow") || name.equals("mHide")) {
                    field.setAccessible(true);
                    Object mShow = field.get(mTN);
                    if (mShow instanceof Runnable) {
                        field.set(mTN, Tools.RunnableWrapper.wrap((Runnable) mShow));
                    }
                } else if (name.equals("mHandler")) {
                    field.setAccessible(true);
                    Object mHandler = field.get(mTN);
                    if (mHandler instanceof Handler) {
                        field.set(mTN, Tools.HandlerWrapper.wrap((Handler) mHandler));
                    }
                }
            }
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
        return t;
    }
}
