package android.ext;

import android.annotation.TargetApi;
import android.ext.Log;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/* JADX INFO: loaded from: classes.dex */
public class ThreadManager {
    private static final int THREAD_COUNT = 5;
    private static final int THREAD_LOG = 2;
    private static final int THREAD_MAIN = 1;
    private static final int THREAD_UI = 0;
    private static final int THREAD_WRITE = 3;
    private static final int THREAD_WORKER = 4;
    private static final Handler[] handlers;
    private static final Looper[] loopers;

    static {
        HandlerThread thread = new HandlerThreadEx("Main");
        thread.start();
        HandlerThread thread2 = new HandlerThreadEx("Log");
        thread2.start();
        HandlerThread thread3 = new HandlerThreadEx("Write");
        thread3.start();
        HandlerThread thread4 = new HandlerThreadEx("Worker");
        thread4.start();
        Looper[] loopers_ = {Looper.getMainLooper(), thread.getLooper(), thread2.getLooper(), thread3.getLooper(), thread4.getLooper()};
        Handler[] handlers_ = {new Handler(loopers_[0]), new Handler(loopers_[1]), new Handler(loopers_[2]), new Handler(loopers_[3]), new Handler(loopers_[4])};
        loopers = loopers_;
        handlers = handlers_;
    }

    private static class HandlerThreadEx extends HandlerThread {
        public HandlerThreadEx(String name) {
            super(name);
            ExceptionHandler.install(this);
        }
    }

    private static void run(int thread, Runnable task, boolean post) {
        if (loopers == null) {
            Log.LogWrapper.e(MainService.TAG, Log.BAD_IMPLEMENTATION, new NullPointerException("loopers"));
            return;
        }
        if (loopers[thread] == null) {
            Log.LogWrapper.e(MainService.TAG, Log.BAD_IMPLEMENTATION, new NullPointerException("loopers[" + thread + ']'));
        } else if (Looper.myLooper() == loopers[thread] && !post) {
            task.run();
        } else {
            handlers[thread].post(task);
        }
    }

    public static void runOnUiThread(Runnable task) {
        run(0, task, false);
    }

    public static void runOnMainThread(Runnable task) {
        run(1, task, false);
    }

    public static void runOnLogThread(Runnable task) {
        try {
            run(2, task, false);
        } catch (Throwable e) {
            Log.LogWrapper.e(MainService.TAG, Log.BAD_IMPLEMENTATION, e);
        }
    }

    public static void runOnWriteThread(Runnable task) {
        run(3, task, false);
    }

    public static void runOnWorkerThread(Runnable task) {
        run(THREAD_WORKER, task, false);
    }

    private static Handler getHandler(int thread) {
        return handlers[thread];
    }

    public static Handler getHandlerUiThread() {
        return getHandler(0);
    }

    public static Handler getHandlerMainThread() {
        return getHandler(1);
    }

    public static Handler getHandlerLogThread() {
        return getHandler(2);
    }

    public static Handler getHandlerWriteThread() {
        return getHandler(3);
    }

    public static Handler getHandlerWorkerThread() {
        return getHandler(THREAD_WORKER);
    }

    private static Looper getLooper(int thread) {
        return loopers[thread];
    }

    public static Looper getLooperUiThread() {
        return getLooper(0);
    }

    public static Looper getLooperMainThread() {
        return getLooper(1);
    }

    public static Looper getLooperLogThread() {
        return getLooper(2);
    }

    public static Looper getLooperWriteThread() {
        return getLooper(3);
    }

    public static Looper getLooperWorkerThread() {
        return getLooper(THREAD_WORKER);
    }

    private static boolean isInThread(int thread) {
        return Looper.myLooper() == loopers[thread];
    }

    public static boolean isInUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean isInMainThread() {
        return isInThread(1);
    }

    public static boolean isInLogThread() {
        return isInThread(2);
    }

    public static boolean isInWriteThread() {
        return isInThread(3);
    }

    public static boolean isInWorkerThread() {
        return isInThread(THREAD_WORKER);
    }

    @TargetApi(18)
    public static void exit() {
        for (int i = 0; i < loopers.length; i++) {
            if (i != 0) {
                Looper looper = loopers[i];
                try {
                    looper.quitSafely();
                } catch (NoSuchMethodError e) {
                    looper.quit();
                }
            }
        }
    }
}
