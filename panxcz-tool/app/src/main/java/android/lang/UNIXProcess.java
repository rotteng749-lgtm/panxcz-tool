package android.lang;

import android.ext.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/* JADX INFO: loaded from: classes.dex */
final class UNIXProcess extends Process {
    private static final Executor processReaperExecutor = (Executor) doPrivileged(new PrivilegedAction<Executor>() { // from class: android.lang.UNIXProcess.1
        @Override // java.security.PrivilegedAction
        public Executor run() {
            ProcessReaperThreadFactory factory = new ProcessReaperThreadFactory(null);
            Executor ret = Executors.newCachedThreadPool(factory);
            return ret;
        }
    });
    private int exitcode;
    private boolean hasExited;
    private final int pid;
    private InputStream stderr;
    private OutputStream stdin;
    private InputStream stdout;

    private static native void destroyProcess(int i);

    private native int forkAndExec(byte[] bArr, byte[] bArr2, int i, byte[] bArr3, int i2, byte[] bArr4, int[] iArr, boolean z) throws IOException;

    private static native void initIDs();

    /* JADX INFO: Access modifiers changed from: private */
    public native int waitForProcessExit(int i);

    private static class ProcessReaperThreadFactory implements ThreadFactory {
        private static final ThreadGroup group = getRootThreadGroup();

        private ProcessReaperThreadFactory() {
        }

        /* synthetic */ ProcessReaperThreadFactory(ProcessReaperThreadFactory processReaperThreadFactory) {
            this();
        }

        private static ThreadGroup getRootThreadGroup() {
            ThreadGroup ret = (ThreadGroup) UNIXProcess.doPrivileged(new PrivilegedAction<ThreadGroup>() { // from class: android.lang.UNIXProcess.ProcessReaperThreadFactory.1
                @Override // java.security.PrivilegedAction
                public ThreadGroup run() {
                    ThreadGroup root = Thread.currentThread().getThreadGroup();
                    while (true) {
                        ThreadGroup parent = root.getParent();
                        if (parent == null || root == parent) {
                            break;
                        }
                        root = parent;
                    }
                    return root;
                }
            });
            return ret;
        }

        @Override // java.util.concurrent.ThreadFactory
        public Thread newThread(Runnable grimReaper) {
            Thread t = new Thread(group, grimReaper, "process reaper", 32768L);
            t.setDaemon(true);
            t.setPriority(10);
            return t;
        }
    }

    public static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
        return (T) AccessController.doPrivileged(privilegedAction);
    }

    public static <T> T doPrivileged(PrivilegedExceptionAction<T> privilegedExceptionAction) throws PrivilegedActionException {
        return (T) AccessController.doPrivileged(privilegedExceptionAction);
    }

    static {
        initIDs();
    }

    UNIXProcess(byte[] prog, byte[] argBlock, int argc, byte[] envBlock, int envc, byte[] dir, final int[] fds, boolean redirectErrorStream) throws IOException {
        this.pid = forkAndExec(prog, argBlock, argc, envBlock, envc, dir, fds, redirectErrorStream);
        try {
            doPrivileged(new PrivilegedExceptionAction<Void>() { // from class: android.lang.UNIXProcess.2
                @Override // java.security.PrivilegedExceptionAction
                public Void run() throws IOException {
                    UNIXProcess.this.initStreams(fds);
                    return null;
                }
            });
        } catch (PrivilegedActionException ex) {
            throw ((IOException) ex.getException());
        }
    }

    static FileDescriptor newFileDescriptor(int fd) {
        FileDescriptor fileDescriptor = new FileDescriptor();
        try {
            Field descriptor = FileDescriptor.class.getDeclaredField("descriptor");
            descriptor.setAccessible(true);
            descriptor.set(fileDescriptor, Integer.valueOf(fd));
            return fileDescriptor;
        } catch (Throwable e) {
            throw new RuntimeException("Failed set fd", e);
        }
    }

    static class NullOutputStream extends OutputStream {
        static final NullOutputStream INSTANCE = new NullOutputStream();

        private NullOutputStream() {
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            throw new IOException("Stream closed");
        }
    }

    static class NullInputStream extends InputStream {
        static final NullInputStream INSTANCE = new NullInputStream();

        private NullInputStream() {
        }

        @Override // java.io.InputStream
        public int read() {
            return -1;
        }

        @Override // java.io.InputStream
        public int available() {
            return 0;
        }
    }

    void initStreams(int[] fds) throws IOException {
        OutputStream processPipeOutputStream;
        InputStream processPipeInputStream;
        InputStream processPipeInputStream2;
        if (fds[0] == -1) {
            processPipeOutputStream = NullOutputStream.INSTANCE;
        } else {
            processPipeOutputStream = new ProcessPipeOutputStream(fds[0]);
        }
        this.stdin = processPipeOutputStream;
        if (fds[1] == -1) {
            processPipeInputStream = NullInputStream.INSTANCE;
        } else {
            processPipeInputStream = new ProcessPipeInputStream(fds[1]);
        }
        this.stdout = processPipeInputStream;
        if (fds[2] == -1) {
            processPipeInputStream2 = NullInputStream.INSTANCE;
        } else {
            processPipeInputStream2 = new ProcessPipeInputStream(fds[2]);
        }
        this.stderr = processPipeInputStream2;
        processReaperExecutor.execute(new Runnable() { // from class: android.lang.UNIXProcess.3
            @Override // java.lang.Runnable
            public void run() {
                int exitcode = UNIXProcess.this.waitForProcessExit(UNIXProcess.this.pid);
                UNIXProcess.this.processExited(exitcode);
            }
        });
    }

    void processExited(int exitcode) {
        synchronized (this) {
            this.exitcode = exitcode;
            this.hasExited = true;
            notifyAll();
        }
        if (this.stdout instanceof ProcessPipeInputStream) {
            ((ProcessPipeInputStream) this.stdout).processExited();
        }
        if (this.stderr instanceof ProcessPipeInputStream) {
            ((ProcessPipeInputStream) this.stderr).processExited();
        }
        if (this.stdin instanceof ProcessPipeOutputStream) {
            ((ProcessPipeOutputStream) this.stdin).processExited();
        }
    }

    @Override // java.lang.Process
    public OutputStream getOutputStream() {
        return this.stdin;
    }

    @Override // java.lang.Process
    public InputStream getInputStream() {
        return this.stdout;
    }

    @Override // java.lang.Process
    public InputStream getErrorStream() {
        return this.stderr;
    }

    @Override // java.lang.Process
    public synchronized int waitFor() throws InterruptedException {
        while (!this.hasExited) {
            wait();
        }
        return this.exitcode;
    }

    @Override // java.lang.Process
    public synchronized int exitValue() {
        if (!this.hasExited) {
            throw new IllegalThreadStateException("process hasn't exited");
        }
        return this.exitcode;
    }

    @Override // java.lang.Process
    public void destroy() {
        synchronized (this) {
            if (!this.hasExited) {
                destroyProcess(this.pid);
            }
        }
        try {
            this.stdin.close();
        } catch (IOException e) {
        }
        try {
            this.stdout.close();
        } catch (IOException e2) {
        }
        try {
            this.stderr.close();
        } catch (IOException e3) {
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Process[pid=");
        sb.append(this.pid);
        if (this.hasExited) {
            sb.append(", hasExited=true, exitcode=");
            sb.append(this.exitcode);
            sb.append(']');
        } else {
            sb.append(", hasExited=false]");
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static FileInputStream getFileInputStream(int fd) {
        Field isFdOwner;
        FileInputStream ret = new FileInputStream(newFileDescriptor(fd));
        try {
            try {
                isFdOwner = FileInputStream.class.getDeclaredField("isFdOwner");
            } catch (NoSuchFieldException e) {
                isFdOwner = FileInputStream.class.getDeclaredField("shouldClose");
            }
            isFdOwner.setAccessible(true);
            isFdOwner.set(ret, true);
        } catch (Throwable e2) {
            Log.d("Failed set isFdOwner", e2);
        }
        return ret;
    }

    static class ProcessPipeInputStream extends BufferedInputStream {
        ProcessPipeInputStream(int fd) {
            super(UNIXProcess.getFileInputStream(fd));
        }

        private static byte[] drainInputStream(InputStream in) throws IOException {
            if (in == null) {
                return null;
            }
            int n = 0;
            byte[] a = null;
            while (true) {
                int j = in.available();
                if (j <= 0) {
                    break;
                }
                a = a == null ? new byte[j] : Arrays.copyOf(a, n + j);
                n += in.read(a, n, j);
            }
            return (a == null || n == a.length) ? a : Arrays.copyOf(a, n);
        }

        synchronized void processExited() {
            InputStream byteArrayInputStream;
            try {
                InputStream in = this.in;
                if (in != null) {
                    byte[] stragglers = drainInputStream(in);
                    in.close();
                    if (stragglers == null) {
                        byteArrayInputStream = NullInputStream.INSTANCE;
                    } else {
                        byteArrayInputStream = new ByteArrayInputStream(stragglers);
                    }
                    this.in = byteArrayInputStream;
                    if (this.buf == null) {
                        this.in = null;
                    }
                }
            } catch (IOException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static FileOutputStream getFileOutputStream(int fd) {
        Field isFdOwner;
        FileOutputStream ret = new FileOutputStream(newFileDescriptor(fd));
        try {
            try {
                isFdOwner = FileOutputStream.class.getDeclaredField("isFdOwner");
            } catch (NoSuchFieldException e) {
                isFdOwner = FileOutputStream.class.getDeclaredField("shouldClose");
            }
            isFdOwner.setAccessible(true);
            isFdOwner.set(ret, true);
        } catch (Throwable e2) {
            Log.d("Failed set isFdOwner", e2);
        }
        return ret;
    }

    static class ProcessPipeOutputStream extends BufferedOutputStream {
        ProcessPipeOutputStream(int fd) {
            super(UNIXProcess.getFileOutputStream(fd));
        }

        synchronized void processExited() {
            OutputStream out = this.out;
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
                this.out = NullOutputStream.INSTANCE;
            }
        }
    }
}
