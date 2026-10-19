package android.ext;

/* JADX INFO: loaded from: classes.dex */
public class DaemonThread extends Thread {
    public DaemonThread(String threadName) {
        super(threadName);
        setDaemon(true);
        ExceptionHandler.install(this);
    }

    public DaemonThread(Runnable runnable, String threadName) {
        super(runnable, threadName);
        setDaemon(true);
        ExceptionHandler.install(this);
    }
}
