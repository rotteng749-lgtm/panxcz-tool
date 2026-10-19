package android.ext;

import android.content.DialogInterface;
import android.ext.ProcessList;
import android.os.Build;
import android.sup.ArrayListResults;
import android.sup.LongSparseArrayChecked;
import catch_.me_.if_.you_.can_.R;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

/* JADX INFO: loaded from: classes.dex */
public class Debug {
    /* JADX WARN: Type inference failed for: r0v1, types: [android.ext.Debug$1] */
    public static void runCollectRegionLog() {
        Tools.showToast(R.string.collect_data_to_region_log);
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.Debug.1
            @Override // java.lang.Runnable
            public void run() {
                Debug.collectRegionLog();
            }
        });
    }

    public static void collectRegionLog() {
        String logFilename = new File(Tools.getSdcardPath(), "GG_" + Build.VERSION.RELEASE + "_API_" + Build.VERSION.SDK_INT + ".log").getAbsolutePath();
        int resId = R.string.failed_save;
        try {
            BufferedWriter log = new BufferedWriter(new FileWriter(logFilename, false));
            Calendar calendar = new GregorianCalendar();
            log.write(calendar.getTime().toString());
            log.write(ListManager.NEW_LINE);
            log.write("Release: ");
            log.write(Build.VERSION.RELEASE);
            log.write(ListManager.NEW_LINE);
            log.write("SDK: ");
            log.write(new StringBuilder().append(Build.VERSION.SDK_INT).toString());
            log.write(ListManager.NEW_LINE);
            try {
                String cmd = Tools.removeNewLinesChars("hfkr#%olvw=%>#ov#0o#2surf2>#iru#l#lq#2surf2->#gr#hfkr#%fdw#'l2pdsv=%>#fdw#'l2pdsv>#grqh>#h{lw>");
                Process process = RootDetector.tryRoot(cmd, null, true);
                if (process == null) {
                    log.write(RootDetector.debug);
                } else {
                    BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    while (true) {
                        String line = out.readLine();
                        if (line == null) {
                            break;
                        }
                        log.write(line);
                        log.write(ListManager.NEW_LINE);
                    }
                    out.close();
                }
                RootDetector.debug = "";
                log.write("current pid: ");
                ProcessList.ProcessInfo info = MainService.instance.processInfo;
                log.write(info != null ? new StringBuilder().append(info.pid).toString() : "0");
                log.write(ListManager.NEW_LINE);
                log.write("search results:\n");
                ArrayListResults mAddressList = MainService.instance.mAddressList;
                AddressItem item = new AddressItem();
                synchronized (mAddressList) {
                    int s = mAddressList.size();
                    for (int i = 0; i < s; i++) {
                        mAddressList.get(i, item);
                        log.write(String.valueOf(ToolsLight.prefixLongHex(8, item.address)) + ' ' + item.getStringDataTrim() + ' ' + ((Object) item.getNameShort()) + " (" + Integer.toBinaryString(item.flags) + ")\n");
                    }
                }
                log.write("saved list:\n");
                LongSparseArrayChecked<SavedItem> items = MainService.instance.savedList.getList();
                for (int i2 = 0; i2 < items.size(); i2++) {
                    SavedItem item2 = (SavedItem) items.valueAt(i2);
                    if (item2 != null) {
                        log.write(String.valueOf(ToolsLight.prefixLongHex(8, item2.address)) + ' ' + item2.getStringDataTrim() + ' ' + ((Object) item2.getNameShort()) + " (" + Integer.toBinaryString(item2.flags) + ")\n");
                    }
                }
            } catch (Exception e) {
                log.write(e.getMessage());
                log.write(ListManager.NEW_LINE);
            }
            log.close();
            resId = R.string.log_saved;
        } catch (Exception e2) {
            Log.e("Error opening file to save: " + logFilename, e2);
        }
        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.region_log)).setMessage(Tools.stringFormat(Re.s(resId), logFilename)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
    }

    public static int getIntVersion() {
        return getIntVersion(new StringBuilder().append((107.4f * 1) - 11.4f).toString());
    }

    public static int getIntVersion(String version) {
        String[] parts = version.replaceAll("[^\\d.]", "").split("\\.", -1);
        int out = 0;
        for (int i = 0; i < parts.length; i++) {
            try {
                out = (out * 100) + Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                Log.w("bad part of version: " + parts[i] + '(' + version + ')', e);
            }
        }
        return out;
    }

    public static String getAndroidVersion() {
        StringBuilder out = new StringBuilder();
        out.append(" Android(");
        for (Class<?> cl : new Class[]{Build.VERSION.class, Build.class}) {
            Field[] declaredFields = cl.getFields();
            for (Field field : declaredFields) {
                try {
                    int modifiers = field.getModifiers();
                    if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
                        String name = field.getName();
                        out.append(name);
                        out.append(": ");
                        try {
                            Object o = field.get(null);
                            if (o instanceof Object[]) {
                                out.append(Arrays.toString((Object[]) o));
                            } else {
                                out.append(o);
                            }
                        } catch (Throwable e) {
                            out.append(e.getMessage());
                        }
                        out.append(ListManager.TEXT_SEPARATOR);
                    }
                } catch (Throwable e2) {
                    out.append(e2.getMessage());
                }
            }
        }
        out.append(')');
        return out.toString();
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [android.ext.Debug$2] */
    public static void showInfo() {
        ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.Debug.2
            @Override // java.lang.Runnable
            public void run() {
                Log.iFull("GG started: " + Debug.getInfo());
            }
        });
    }

    public static String getInfo() {
        return String.valueOf((98.1f * 1) - 2.1f) + " (" + ((int) (((16210.7f * 1) - 217.7f) + 0.5f)) + ") " + Installer.getHashes() + " on " + getAndroidVersion();
    }

    public static String getLogcat(boolean withTime) {
        return getLogcat(withTime, 0);
    }

    private static String getLogcat(boolean withTime, int limit) {
        String cmd;
        String line;
        StringBuilder log = new StringBuilder();
        try {
            String cmd2 = Tools.removeNewLinesChars("h{hf#orjfdw#0g#");
            if (withTime) {
                cmd = String.valueOf(cmd2) + "-v threadtime ";
            } else {
                cmd = String.valueOf(cmd2) + "-v brief ";
            }
            Process process = RootDetector.tryRoot(cmd, null, true);
            if (process == null) {
                log.append(RootDetector.debug);
            } else {
                BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String[] buffer = null;
                if (limit > 0) {
                    buffer = new String[limit];
                }
                int i = 0;
                while (!Thread.interrupted() && (line = out.readLine()) != null) {
                    boolean add = false;
                    if (0 == 0 && line.contains(MainService.TAG)) {
                        add = true;
                    }
                    if (!add && line.contains(DaemonManager.LABEL)) {
                        add = true;
                    }
                    if (!add && line.contains("AndroidRuntime")) {
                        add = true;
                    }
                    if (!add && line.contains("libc")) {
                        add = true;
                    }
                    if (!add && line.contains("Vold")) {
                        add = true;
                    }
                    if (!add && line.contains("DEBUG")) {
                        add = true;
                    }
                    if (!add && line.contains("CRASH")) {
                        add = true;
                    }
                    if (!add && line.contains("*** *** *** *** *** *** ***")) {
                        add = true;
                    }
                    if (!add && line.contains(Tools.getPackageName())) {
                        add = true;
                    }
                    if (!add && line.contains("--- beginning of ")) {
                        add = true;
                    }
                    int pid = android.os.Process.myPid();
                    if (!add && pid > 0 && line.contains(Integer.toString(pid))) {
                        add = true;
                    }
                    int pid2 = MainService.instance.mDaemonManager.getDaemonPid();
                    if (!add && pid2 > 0 && line.contains(Integer.toString(pid2))) {
                        add = true;
                    }
                    int pid3 = MainService.instance.mDaemonManager.getSuPid();
                    if (!add && pid3 > 0 && line.contains(Integer.toString(pid3))) {
                        add = true;
                    }
                    if (add) {
                        if (buffer != null) {
                            int i2 = i + 1;
                            buffer[i] = line;
                            i = i2 >= limit ? 0 : i2;
                        } else {
                            log.append(line);
                            log.append('\n');
                        }
                    }
                }
                if (buffer != null) {
                    int j = i;
                    int j2 = j;
                    while (!Thread.interrupted()) {
                        int j3 = j2 + 1;
                        String s = buffer[j2];
                        if (s != null) {
                            log.append(s);
                            log.append('\n');
                        }
                        if (j3 >= limit) {
                            j3 = 0;
                        }
                        if (j3 == i) {
                            break;
                        }
                        j2 = j3;
                    }
                }
            }
        } catch (OutOfMemoryError e) {
            try {
                StringBuilder log2 = new StringBuilder();
                try {
                    log2.append(getLogcat(withTime, 500));
                    log = log2;
                } catch (OutOfMemoryError e2) {
                    log = new StringBuilder();
                    log.append("OutOfMemoryError\n");
                }
            } catch (OutOfMemoryError e3) {
            }
        } catch (Throwable e4) {
            log.append(e4.getMessage());
            log.append('\n');
            Log.d("getLogcat fail.", e4);
        }
        RootDetector.debug = "";
        try {
            return log.toString();
        } catch (OutOfMemoryError e5) {
            return "OutOfMemoryError\n";
        }
    }

    public static boolean isCalledFromMethod(String method) {
        StackTraceElement[] stack;
        if (method == null || (stack = Thread.currentThread().getStackTrace()) == null) {
            return false;
        }
        for (StackTraceElement el : stack) {
            if (el != null && method.equals(el.getMethodName())) {
                return true;
            }
        }
        return false;
    }
}
