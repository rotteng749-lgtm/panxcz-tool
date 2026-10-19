package android.ext;

import android.content.Context;
import android.os.Handler;
import android.os.Build;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class Log {
    public static final String BAD_IMPLEMENTATION = "Bad implementation";
    private static final boolean DISABLE_LOG = true;
    private static final long FLUSH_LOG_EVERY_MS = 5000;
    static volatile boolean closed = false;
    static volatile BufferedWriter writer = null;
    private static long lastFlush = 0;
    static final Runnable flushTask = new Runnable() { // from class: android.ext.Log.1
        @Override // java.lang.Runnable
        public void run() {
            Log.flush(true);
        }
    };
    static String LOG_FILE = "/last_run.log";
    public static String lastSearchLog = "- none -";
    static StringBuilder lastSearchLogCurrent = null;

    public static void dbg(String hypothesisId, String event, String data) {
        // Disabled: do not emit runtime debug telemetry from production code.
    }

    private static File getPath() throws IOException {
        List<File> list = new ArrayList<>();
        Context context = Tools.getContext();
        try {
            list.add(context.getExternalFilesDir(null));
        } catch (Throwable e) {
            w("Failed getExternalFilesDir", e);
        }
        try {
            list.add(context.getExternalCacheDir());
        } catch (Throwable e2) {
            w("Failed getExternalCacheDir", e2);
        }
        list.add(Tools.getFilesDir());
        list.add(Tools.getCacheDir());
        for (File path : list) {
            if (path != null) {
                try {
                    path.mkdirs();
                    File check = new File(path, "write.tmp");
                    check.delete();
                    if (check.exists()) {
                        continue;
                    } else {
                        check.createNewFile();
                        if (check.exists()) {
                            check.delete();
                            return path;
                        }
                        continue;
                    }
                } catch (IOException e3) {
                    w("check " + path, e3);
                }
            }
        }
        throw new IOException("Failed find path");
    }

    static void reopen() {
    }

    public static synchronized void s(final String line) {
        if (!ThreadManager.isInLogThread() && !closed) {
            ThreadManager.runOnLogThread(new Runnable() { // from class: android.ext.Log.2
                @Override // java.lang.Runnable
                public void run() {
                    Context context = Tools.getContext();
                    if (context != null) {
                        if (Log.writer == null) {
                            Log.reopen();
                        }
                        if (Log.writer != null) {
                            try {
                                Log.writer.append((CharSequence) line);
                                Log.writer.newLine();
                                if (!Log.flush(false)) {
                                    Handler handler = ThreadManager.getHandlerLogThread();
                                    handler.removeCallbacks(Log.flushTask);
                                    handler.postDelayed(Log.flushTask, 5000L);
                                }
                            } catch (IOException e) {
                                LogWrapper.d(MainService.TAG, "Log write: I/O", e);
                                String msg = e.getMessage();
                                if (msg != null && msg.contains("EBADF")) {
                                    Log.reopen();
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public static boolean flush(boolean force) {
        if (writer == null) {
            return false;
        }
        try {
            long now = System.currentTimeMillis();
            if (now - lastFlush <= 5000 && !force) {
                return false;
            }
            try {
                writer.flush();
            } catch (NullPointerException e) {
            }
            lastFlush = now;
            return true;
        } catch (IOException e2) {
            LogWrapper.d(MainService.TAG, "Log write: I/O", e2);
            return false;
        }
    }

    public static void close() {
        ThreadManager.runOnLogThread(new Runnable() { // from class: android.ext.Log.3
            @Override // java.lang.Runnable
            public void run() {
                Log.closed = true;
                if (Log.writer != null) {
                    try {
                        Log.writer.flush();
                        Log.writer.close();
                        Log.writer = null;
                    } catch (IOException e) {
                        LogWrapper.d(MainService.TAG, "Log close: I/O", e);
                    }
                }
            }
        });
    }

    public static void crash() {
        ThreadManager.runOnLogThread(new Runnable() { // from class: android.ext.Log.4
            @Override // java.lang.Runnable
            public void run() {
                Log.close();
            }
        });
    }

    public static int v(String msg) {
        return v(MainService.TAG, msg);
    }

    public static int v(String msg, Throwable tr) {
        return v(MainService.TAG, msg, tr);
    }

    public static int v(String tag, String msg) {
        return LogWrapper.v(tag, msg);
    }

    public static int v(String tag, String msg, Throwable tr) {
        return LogWrapper.v(tag, msg, tr);
    }

    public static int d(String msg) {
        return d(MainService.TAG, msg);
    }

    public static int d(String msg, Throwable tr) {
        return d(MainService.TAG, msg, tr);
    }

    public static int d(String tag, String msg) {
        return LogWrapper.d(tag, msg);
    }

    public static int d(String tag, String msg, Throwable tr) {
        return LogWrapper.d(tag, msg, tr);
    }

    public static int i(String msg) {
        return i(MainService.TAG, msg);
    }

    public static int i(String msg, Throwable tr) {
        return i(MainService.TAG, msg, tr);
    }

    public static int i(String tag, String msg) {
        return LogWrapper.i(tag, msg);
    }

    public static int i(String tag, String msg, Throwable tr) {
        return LogWrapper.i(tag, msg, tr);
    }

    public static int w(String msg) {
        return w(MainService.TAG, msg);
    }

    public static int w(String msg, Throwable tr) {
        return w(MainService.TAG, msg, tr);
    }

    public static int w(String tag, String msg) {
        return LogWrapper.w(tag, msg);
    }

    public static int w(String tag, String msg, Throwable tr) {
        return LogWrapper.w(tag, msg, tr);
    }

    public static int wt(String tag, Throwable tr) {
        return LogWrapper.w(tag, tr);
    }

    public static int e(String msg) {
        return e(MainService.TAG, msg);
    }

    public static int e(String msg, Throwable tr) {
        return e(MainService.TAG, msg, tr);
    }

    public static int e(String tag, String msg) {
        return LogWrapper.e(tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        return LogWrapper.e(tag, msg, tr);
    }

    public static int badImplementation(Throwable tr) {
        return w(BAD_IMPLEMENTATION, tr);
    }

    public static int iFull(String info) {
        int len = info.length();
        int pos = 0;
        while (pos < len) {
            int end = pos + 1003;
            i(info.substring(pos, end < len ? end : len));
            pos = end;
        }
        return 0;
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        for (Throwable t = tr; t != null; t = t.getCause()) {
            if (t instanceof UnknownHostException) {
                return "";
            }
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            tr.printStackTrace(pw);
        } catch (OutOfMemoryError e) {
            pw.write("OutOfMemoryError 1\n");
            for (Throwable t2 = tr; t2 != null; t2 = t2.getCause()) {
                try {
                    pw.write(t2.toString());
                    pw.write(ListManager.NEW_LINE);
                } catch (OutOfMemoryError e2) {
                    pw.write("OutOfMemoryError 2\n");
                }
            }
        } catch (Throwable e3) {
            pw.write("Exception on printStackTrace: " + e3.getMessage());
        }
        pw.flush();
        String ret = sw.toString();
        return ret == null ? "" : ret;
    }

    public static void runLogOnProcessErrStream(final Process process, final InputStream errStream) {
        if (process != null && errStream != null) {
            Thread t = new ExceptionHandler.ThreadEx(new Runnable() {
                @Override
                public void run() {
                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(new InputStreamReader(errStream));
                        int lines = 0;
                        while (true) {
                            String line = br.readLine();
                            if (line == null) {
                                break;
                            }
                            if (line.length() != 0) {
                                LogWrapper.d(DaemonManager.LABEL, line);
                            }
                            lines++;
                            if (lines > 4000) {
                                LogWrapper.d(DaemonManager.LABEL, "stderr: stop after " + lines + " lines");
                                break;
                            }
                        }
                    } catch (Throwable e) {
                        LogWrapper.e(DaemonManager.LABEL, "stderr reader failed", e);
                    } finally {
                        if (br != null) {
                            try {
                                br.close();
                            } catch (IOException e2) {
                            }
                        }
                    }
                }
            }, "stderr-reader");
            t.setDaemon(true);
            t.start();
        }
    }

    public static void here() {
        Thread thread = Thread.currentThread();
        StackTraceElement el = thread.getStackTrace()[3];
        d("here: " + thread.getName() + " : " + thread.getId() + " : " + el.getFileName() + " : " + el.getClassName() + " : " + el.getMethodName() + " : " + el.getLineNumber());
    }

    public static class LogWrapper {
        public static final int MAX_MSG_LEN = 1023;
        public static final int MAX_TAG_LEN = 23;
        private static final boolean OFF = false;

        private static String fixTag(String in) {
            if (in == null) {
                return "null";
            }
            if (in.length() > 23) {
                return in.substring(0, 23);
            }
            return in;
        }

        private static int log(int meth, String tag, String msg, Throwable tr) {
            String tag2 = fixTag(tag);
            if (msg == null) {
                msg = "null";
            }
            int offset = 0;
            int max = msg.length();
            do {
                int len = max - offset;
                if (len > 1023) {
                    len = MAX_MSG_LEN;
                }
                String msg2 = msg.substring(offset, offset + len);
                offset += len;
                switch (meth) {
                    case 0:
                        try {
                            android.util.Log.e(tag2, msg2);
                        } catch (OutOfMemoryError e) {
                            android.util.Log.e(tag2, "OOM for Log 1");
                        }
                        break;
                    case 1:
                        if (offset == max) {
                            try {
                                android.util.Log.e(tag2, msg2, tr);
                            } catch (OutOfMemoryError e2) {
                                android.util.Log.e(tag2, msg2);
                            }
                        } else {
                            android.util.Log.e(tag2, msg2);
                        }
                        break;
                    case 2:
                        android.util.Log.e(tag2, msg2);
                        break;
                    case 3:
                        if (offset == max) {
                            try {
                                android.util.Log.e(tag2, msg2, tr);
                            } catch (OutOfMemoryError e3) {
                                android.util.Log.e(tag2, msg2);
                            }
                        } else {
                            android.util.Log.e(tag2, msg2);
                        }
                        break;
                    case 4:
                        android.util.Log.e(tag2, msg2);
                        break;
                    case 5:
                        if (offset == max) {
                            try {
                                android.util.Log.e(tag2, msg2, tr);
                            } catch (OutOfMemoryError e4) {
                                android.util.Log.e(tag2, msg2);
                            }
                        } else {
                            android.util.Log.e(tag2, msg2);
                        }
                        break;
                    case 6:
                        android.util.Log.e(tag2, msg2);
                        break;
                    case 7:
                        if (offset == max) {
                            try {
                                android.util.Log.e(tag2, msg2, tr);
                            } catch (OutOfMemoryError e5) {
                                android.util.Log.e(tag2, msg2);
                            }
                        } else {
                            android.util.Log.e(tag2, msg2);
                        }
                        break;
                    case 8:
                        try {
                            android.util.Log.e(tag2, "", tr);
                        } catch (OutOfMemoryError e6) {
                            android.util.Log.e(tag2, "OOM for Log 2");
                        }
                        break;
                    case 9:
                        android.util.Log.e(tag2, msg2);
                        break;
                    case 10:
                        if (offset == max) {
                            try {
                                android.util.Log.e(tag2, msg2, tr);
                            } catch (OutOfMemoryError e7) {
                                android.util.Log.e(tag2, msg2);
                            }
                        } else {
                            android.util.Log.e(tag2, msg2);
                        }
                        break;
                }
            } while (offset < max);
            return 0;
        }

        public static int v(String tag, String msg) {
            return log(0, tag, msg, null);
        }

        public static int v(String tag, String msg, Throwable tr) {
            return log(1, tag, msg, tr);
        }

        public static int d(String tag, String msg) {
            return log(2, tag, msg, null);
        }

        public static int d(String tag, String msg, Throwable tr) {
            return log(3, tag, msg, tr);
        }

        public static int i(String tag, String msg) {
            return log(4, tag, msg, null);
        }

        public static int i(String tag, String msg, Throwable tr) {
            return log(5, tag, msg, tr);
        }

        public static int w(String tag, String msg) {
            return log(6, tag, msg, null);
        }

        public static int w(String tag, String msg, Throwable tr) {
            return log(7, tag, msg, tr);
        }

        public static int w(String tag, Throwable tr) {
            return log(8, tag, null, tr);
        }

        public static int e(String tag, String msg) {
            return log(9, tag, msg, null);
        }

        public static int e(String tag, String msg, Throwable tr) {
            return log(10, tag, msg, tr);
        }
    }
}
