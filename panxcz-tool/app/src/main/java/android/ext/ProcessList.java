package android.ext;

import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ProcessList {
    private static final int BUGGED_UID = -1;
    private static final int WEIGHT_STEP = 1000;
    private static final Map<String, AppInfo> appCache;
    private static final SparseIntArray buggedUids;
    private static ProcessList lastInstance;
    static HashMap<String, Integer> realUids;
    static final SparseIntArray tracers;
    private ActivityManager mActivityManager;
    private PackageManager mPackageManager;

    static {
        SparseIntArray bUids = new SparseIntArray();
        buggedUids = bUids;
        appCache = new HashMap();
        CheckNativeCrash.loadLastCrash();
        addBuggedPackages();
        for (String bad : CheckNativeCrash.getBuggedUids()) {
            if (bad != null && bad.length() != 0) {
                try {
                    bUids.put(Integer.parseInt(bad), 1);
                } catch (NumberFormatException e) {
                    Log.e("Failed load bugged uid: " + bad, e);
                }
            }
        }
        tracers = new SparseIntArray();
        lastInstance = null;
        realUids = null;
    }

    public static void clearCache() {
        int size = appCache.size();
        if (size != 0) {
            appCache.clear();
            addBuggedPackages();
            Log.d("PL: " + size + " -> " + appCache.size());
        }
    }

    private static void addBuggedPackages() {
        Map<String, AppInfo> cache = appCache;
        for (String bad : CheckNativeCrash.getBuggedPackages()) {
            if (bad != null && bad.length() != 0) {
                cache.put(bad, getBuggedPackage(bad, null));
            }
        }
    }

    private AppInfo getAppInfo(String pkg) {
        String ownPkg = Tools.getPackageName();
        if (pkg.equals(ownPkg)) {
            AppInfo ret = getBuggedPackage(ownPkg, "GG");
            return ret;
        }
        if (pkg.startsWith("com.bluestacks.")) {
            AppInfo ret2 = getBuggedPackage(pkg, pkg);
            return ret2;
        }
        AppInfo ret3 = appCache.get(pkg);
        return ret3;
    }

    private static AppInfo getBuggedPackage(String pkg, String name) {
        AppInfo aInfo = new AppInfo(BUGGED_UID, pkg);
        if (name != null) {
            aInfo.name = name;
        }
        aInfo.isStub = true;
        return aInfo;
    }

    public ProcessList(ActivityManager am, PackageManager pm) {
        this.mActivityManager = am;
        this.mPackageManager = pm;
        lastInstance = this;
    }

    public static class AppInfo {
        public String name;
        public String pkg;
        public int pkgUid;
        public int uid;
        public String libsPath = "";
        public boolean isSystem = false;
        public boolean isGame = false;
        public boolean isStub = false;

        public AppInfo(int uid, String pkg) {
            this.uid = uid;
            this.pkg = pkg;
            this.name = pkg;
            this.pkgUid = uid;
        }

        public String toString() {
            return "AppInfo [uid=" + this.uid + ", pkg=" + this.pkg + ", name=" + this.name + ", isSystem=" + this.isSystem + ", isGame=" + this.isGame + ']';
        }
    }

    public static class ProcessInfo implements Comparable<ProcessInfo> {
        public String cmdline;
        public volatile Drawable icon;
        private volatile boolean iconLoaded;
        public boolean isGame;
        public boolean isSystem;
        public String libsPath;
        public boolean main;
        public String name;
        public int order;
        public String packageName;
        public int pid;
        public int pkgUid;
        public int rss;
        public int uid;
        public long weight;
        public boolean x64;
        private static WeakReference<Drawable> EMPTY = null;
        public static final int colorPid = Tools.getColor(R.color.process_pid, -5570561);
        public static final int colorName = Tools.getColor(R.color.process_name, -1);
        public static final int colorSize = Tools.getColor(R.color.process_size, -5570646);

        public ProcessInfo(AppInfo appInfo, int pid, int uid, String cmdline, int order, boolean x64, int rss) {
            this.iconLoaded = false;
            this.cmdline = cmdline;
            this.name = appInfo.name;
            this.packageName = appInfo.pkg;
            this.libsPath = appInfo.libsPath == null ? "" : appInfo.libsPath;
            this.icon = null;
            this.pid = pid;
            this.uid = uid;
            this.pkgUid = appInfo.isStub ? uid : appInfo.pkgUid;
            this.main = cmdline.equals(appInfo.pkg);
            this.isSystem = appInfo.isSystem;
            this.isGame = appInfo.isGame;
            this.order = order;
            this.x64 = x64;
            this.rss = rss;
            if (appInfo.isStub) {
                this.iconLoaded = true;
            }
        }

        public void resolveLibs() {
            ApplicationInfo vs;
            if (this.uid != this.pkgUid) {
                Log.w("vs app: " + this.pkgUid + " != " + this.uid);
                try {
                    String p = ProcessList.lastInstance.getPackagesForUid(this.uid);
                    if (p != null && p != null && (vs = Tools.getApplicationInfo(p)) != null && vs.nativeLibraryDir != null) {
                        Log.w("vs: " + this.libsPath + " => " + vs.nativeLibraryDir);
                        this.libsPath = vs.nativeLibraryDir;
                    }
                } catch (Throwable e) {
                    Log.w("Failed get vs info for " + this.uid, e);
                }
            }
        }

        public void loadIcon() {
            if (!this.iconLoaded) {
                try {
                    this.icon = Tools.tryCloneIcon(Tools.getResized(Tools.getApplicationIcon(Tools.getApplicationInfo(this.packageName)), Tools.dp2px48()));
                } catch (Throwable th) {
                    Log.w("Failed load icon for " + this.packageName);
                }
                this.iconLoaded = true;
            }
        }

        public void loadIcon(final TextView tv) {
            if (tv != null) {
                boolean iconLoaded = this.iconLoaded;
                Drawable icon_ = this.icon;
                if (icon_ == null) {
                    WeakReference<Drawable> wrEmpty = EMPTY;
                    if (wrEmpty != null) {
                        icon_ = wrEmpty.get();
                    }
                    if (icon_ == null) {
                        icon_ = Tools.getDrawable(R.drawable.empty);
                        EMPTY = new WeakReference<>(icon_);
                    }
                }
                Tools.addIconToTextView(tv, icon_, 48);
                if (!iconLoaded) {
                    ThreadManager.runOnMainThread(new Runnable() { // from class: android.ext.ProcessList.ProcessInfo.1
                        @Override // java.lang.Runnable
                        public void run() {
                            ProcessInfo.this.loadIcon();
                            if (ProcessInfo.this.icon != null) {
                                final TextView textView = tv;
                                ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ProcessList.ProcessInfo.1.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        Object tag = textView.getTag();
                                        if (tag == ProcessInfo.this) {
                                            Tools.addIconToTextView(textView, ProcessInfo.this.icon, 48);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }

        public CharSequence toCharSequence() {
            CharSequenceBuilder csb = new CharSequenceBuilder();
            StringBuilder out = new StringBuilder();
            if (getTracer() > 0) {
                out.append('#');
            }
            if (getTrace() > 0) {
                out.append('!');
            }
            if (!Config.vSpace && this.uid != this.pkgUid) {
                out.append('v');
            }
            if (this.pid > 1) {
                out.append('[');
                out.append(Integer.toString(this.pid));
                out.append("] ");
            }
            csb.append(Tools.colorize(out.toString(), colorPid));
            csb.append(Tools.colorize(String.valueOf(this.name) + getCmdline(), colorName));
            StringBuilder out2 = new StringBuilder();
            if (this.x64) {
                out2.append(" [x64]");
            }
            if (this.rss != 0) {
                out2.append(" [");
                out2.append(Tools.formatFileSize(Tools.getContext(), 1024 * ((long) this.rss)));
                out2.append(']');
            }
            csb.append(Tools.colorize(out2.toString(), colorSize));
            return csb.toCharSequence();
        }

        public String toString() {
            return toCharSequence().toString();
        }

        public String dump() {
            return "ProcessInfo [cmdline=" + this.cmdline + ", name=" + this.name + ", packageName=" + this.packageName + ", icon=" + this.icon + ", pid=" + this.pid + ", uid=" + this.uid + ", isSystem=" + this.isSystem + ", isGame=" + this.isGame + ", weight=" + this.weight + ", main=" + this.main + ", order=" + this.order + ", x64=" + this.x64 + ", rss=" + this.rss + ", getTracer()=" + getTracer() + ", getTrace()=" + getTrace() + ']';
        }

        public int getTrace() {
            int index = ProcessList.tracers.indexOfValue(this.pid);
            if (index >= 0) {
                return ProcessList.tracers.keyAt(index);
            }
            return index;
        }

        private String getCmdline() {
            return this.main ? "" : " (" + this.cmdline.replace(String.valueOf(this.packageName) + ':', "") + ')';
        }

        public int getTracer() {
            return ProcessList.tracers.get(this.pid);
        }

        @Override // java.lang.Comparable
        public int compareTo(ProcessInfo another) {
            if (this.order != another.order) {
                return this.order > another.order ? -1 : 1;
            }
            if (this.isGame != another.isGame) {
                return this.isGame ? -1 : 1;
            }
            if (this.isSystem != another.isSystem) {
                return this.isSystem ? 1 : -1;
            }
            if (this.weight != another.weight) {
                return this.weight > another.weight ? -1 : 1;
            }
            if (this.main != another.main) {
                return this.main ? -1 : 1;
            }
            if ((getTrace() > 0) != (another.getTrace() > 0)) {
                return getTrace() > 0 ? 1 : -1;
            }
            if (this.pid != another.pid) {
                return this.pid > another.pid ? -1 : 1;
            }
            return 0;
        }
    }

    public static void loadData(BufferReader reader) {
        lastInstance.load(reader);
    }

    private void load(BufferReader reader) {
        int len = -1;
        AppInfo aInfo;
        reader.reset();
        boolean endByPid0 = false;
        List<ProcessInfo> listProcesses = new ArrayList<>();
        tracers.clear();
        List<ActivityManager.RunningAppProcessInfo> run = null;
        try {
            Log.d("getRunningAppProcesses: start");
            run = this.mActivityManager.getRunningAppProcesses();
            Log.d("getRunningAppProcesses: end " + run.size());
        } catch (Throwable e) {
            Log.e("Failed getRunningAppProcesses", e);
        }
        try {
            int myPid = Process.myPid();
            int daemonPid = MainService.instance.mDaemonManager.getDaemonPid();
            int suPid = MainService.instance.mDaemonManager.getSuPid();
            SparseArray<String> cacheUid = new android.fix.SparseArray<>();
            while (true) {
                int pid = reader.readInt();
                if (pid == 0) {
                    endByPid0 = true;
                    break;
                }
                int uid = reader.readInt();
                boolean x64 = reader.readByte() != 0;
                int rss = reader.readInt();
                int order = reader.readInt();
                int tracer = reader.readInt();
                len = reader.readInt();
                if (len < 0 || len > 200) {
                    int pos = reader.getPosition();
                    int size = reader.size();
                    byte[] next = reader.copyRange(pos, 64);
                    Log.dbg("B", "processlist_bad_cmdline_len", "seq=" + ((int) reader.getSeq()) + " pid=" + pid + " uid=" + uid + " x64=" + x64 + " rss=" + rss + " order=" + order + " tracer=" + tracer + " len=" + len + " pos=" + pos + " size=" + size + " nextHex=" + BufferReader.toHex(next));
                    break;
                }
                String cmdline = reader.readString(len, null);
                if (pid != myPid && pid != daemonPid && pid != suPid && (aInfo = getAppInfo(pid, uid, cmdline, run, cacheUid)) != null && !aInfo.pkg.equals(Tools.getPackageName())) {
                    listProcesses.add(new ProcessInfo(aInfo, pid, uid, cmdline, order, x64, rss));
                    if (tracer != 0) {
                        tracers.put(pid, tracer);
                        Log.d("Tracer: " + tracer + " -> " + pid);
                    }
                }
            }
            Log.dbg("B", "processlist_end", "seq=" + ((int) reader.getSeq()) + " endByPid0=" + endByPid0 + " lastLen=" + len + " pos=" + reader.getPosition() + " size=" + reader.size() + " count=" + listProcesses.size());
            Log.e("Bad cmdline length: " + len, new RuntimeException());
        } catch (IOException e2) {
            Log.e("???", e2);
        }
        MainService.instance.mAppDetector.detectAppResume(sort(listProcesses, run));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getPackagesForUid(int uid) {
        if (Config.vSpace) {
            return Config.vSpacePkg;
        }
        String p = null;
        if (Config.vSpaceReal) {
            HashMap<String, Integer> realUids2 = realUids;
            if (realUids2 == null) {
                return null;
            }
            Iterator<Map.Entry<String, Integer>> it = realUids2.entrySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Map.Entry<String, Integer> pair = it.next();
                if (pair != null && pair.getValue().intValue() == uid) {
                    pair.getKey();
                    break;
                }
            }
            return null;
        }
        if (buggedUids.get(uid) == 1) {
            return null;
        }
        try {
            PackageManager packageManager = this.mPackageManager;
            boolean entered = CheckNativeCrash.enter("uid:" + uid, "uid");
            try {
                String[] arr = packageManager.getPackagesForUid(uid);
                if (arr != null && arr.length > 0) {
                    p = arr[0];
                }
                return p;
            } finally {
                CheckNativeCrash.exit(entered);
            }
        } catch (Throwable e) {
            Log.e("Failed getPackagesForUid", e);
            return p;
        }
    }

    private AppInfo getAppInfo(int pid, int uid, String key, List<ActivityManager.RunningAppProcessInfo> run, SparseArray<String> cacheUid) {
        Integer realUid;
        AppInfo aInfo;
        String p;
        String p2;
        if (key.indexOf(58) != -1) {
            key = key.split(":", -1)[0];
        }
        String pkg = key;
        boolean isPkg = pkg.indexOf(46) != -1 && pkg.indexOf(47) == -1;
        AppInfo aInfo2 = isPkg ? getAppInfo(pkg) : null;
        if (aInfo2 != null) {
            if (aInfo2.uid == BUGGED_UID) {
                aInfo2.uid = uid;
            }
            return aInfo2;
        }
        ApplicationInfo info = null;
        boolean fixed = false;
        if (isPkg) {
            try {
                info = Tools.getApplicationInfo(pkg);
                if (info != null) {
                    fixed = true;
                }
            } catch (PackageManager.NameNotFoundException e) {
            } catch (NoSuchMethodError e2) {
                Log.badImplementation(e2);
            }
        }
        if (info == null) {
            if (run != null) {
                Iterator<ActivityManager.RunningAppProcessInfo> it = run.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    ActivityManager.RunningAppProcessInfo rapi = it.next();
                    if (rapi != null && rapi.pid == pid && rapi.pkgList != null && rapi.pkgList.length > 0) {
                        pkg = rapi.pkgList[0];
                        fixed = true;
                        break;
                    }
                }
            }
            if (!Config.vSpaceReal) {
                if (!fixed && (p2 = cacheUid.get(uid)) != null) {
                    pkg = p2;
                    fixed = true;
                }
                if (!fixed && (p = getPackagesForUid(uid)) != null) {
                    pkg = p;
                    fixed = true;
                    cacheUid.put(uid, pkg);
                }
            }
            if (fixed && (aInfo = getAppInfo(pkg)) != null) {
                if (aInfo.uid == BUGGED_UID) {
                    aInfo.uid = uid;
                }
                if (isPkg) {
                    appCache.put(key, aInfo);
                }
                return aInfo;
            }
        }
        AppInfo aInfo3 = new AppInfo(uid, pkg);
        if (fixed) {
            if (info == null) {
                try {
                    info = Tools.getApplicationInfo(pkg);
                } catch (PackageManager.NameNotFoundException e3) {
                    Log.e("Package not found: " + aInfo3.pkg + " [" + aInfo3.uid + ']', e3);
                } catch (NoSuchMethodError e4) {
                    Log.badImplementation(e4);
                }
            }
            if (info != null) {
                aInfo3.name = getApplicationLabel(pkg, info);
                if (aInfo3.name != null) {
                    aInfo3.name = new StringBuilder(String.valueOf(aInfo3.name)).toString();
                }
                aInfo3.isSystem = (!info.sourceDir.startsWith("/system/") && (info.flags & 128) == 0 && (info.flags & 1) == 0) ? false : true;
                aInfo3.isGame = (info.flags & AddressItem.FLAG_MODE_HACKING) != 0;
                aInfo3.libsPath = info.nativeLibraryDir;
                aInfo3.pkgUid = info.uid;
                HashMap<String, Integer> realUids2 = realUids;
                if (realUids2 != null && (realUid = realUids2.get(pkg)) != null && realUid.intValue() != info.uid) {
                    Log.w("real uid: " + info.uid + " != " + realUid);
                    aInfo3.pkgUid = realUid.intValue();
                }
                if (aInfo3.pkgUid != uid) {
                    Log.w("vs app: " + aInfo3.pkgUid + " != " + uid);
                }
            }
        }
        appCache.put(pkg, aInfo3);
        if (isPkg && !key.equals(pkg)) {
            appCache.put(key, aInfo3);
        }
        return aInfo3;
    }

    private List<ProcessInfo> sort(List<ProcessInfo> listProcesses, List<ActivityManager.RunningAppProcessInfo> run) {
        ProcessInfo pi;
        ProcessInfo[] arr = (ProcessInfo[]) listProcesses.toArray(new ProcessInfo[listProcesses.size()]);
        for (ProcessInfo processInfo : arr) {
            processInfo.weight = 0L;
        }
        List[] lists = new List[2];
        try {
            lists[0] = this.mActivityManager.getRunningTasks(50);
        } catch (Throwable e) {
            Log.e("Failed getRunningTasks", e);
        }
        lists[1] = run;
        int length = lists.length;
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < length) {
                List list = lists[i2];
                if (list != null) {
                    boolean[] used = new boolean[arr.length];
                    for (int k = 0; k < list.size(); k++) {
                        Object item = list.get(k);
                        if (item != null) {
                            String[] pkgs = null;
                            long mul = 0;
                            try {
                                if (item instanceof ActivityManager.RunningTaskInfo) {
                                    ActivityManager.RunningTaskInfo task = (ActivityManager.RunningTaskInfo) item;
                                    String[] pkgs2 = new String[2];
                                    pkgs2[0] = task.topActivity != null ? task.topActivity.getPackageName() : null;
                                    pkgs2[1] = task.baseActivity != null ? task.baseActivity.getPackageName() : null;
                                    try {
                                        if (pkgs2[0] != null && pkgs2[0].equals(pkgs2[1])) {
                                            pkgs2[1] = null;
                                        }
                                        mul = 1000000;
                                        pkgs = pkgs2;
                                    } catch (NoClassDefFoundError e2) {
                                        pkgs = pkgs2;
                                        Log.w("Class not found", e2);
                                    }
                                }
                            } catch (NoClassDefFoundError e3) {
                                Log.w("Class not found", e3);
                            }
                            try {
                                if (item instanceof ActivityManager.RunningAppProcessInfo) {
                                    ActivityManager.RunningAppProcessInfo rapi = (ActivityManager.RunningAppProcessInfo) item;
                                    pkgs = rapi.pkgList;
                                    mul = 1000;
                                }
                            } catch (NoClassDefFoundError e4) {
                                Log.w("Class not found", e4);
                            }
                            if (pkgs != null) {
                                for (String pkg : pkgs) {
                                    if (pkg != null) {
                                        for (int j = 0; j < arr.length; j++) {
                                            if (!used[j] && (pi = arr[j]) != null && pkg.equals(pi.packageName)) {
                                                pi.weight += ((long) (list.size() - k)) * mul;
                                                used[j] = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                i = i2 + 1;
            } else {
                try {
                    break;
                } catch (Throwable e5) {
                    Log.badImplementation(e5);
                }
            }
        }
        Arrays.sort(arr);
        List<ProcessInfo> ordered = new ArrayList<>(arr.length);
        for (ProcessInfo processInfo2 : arr) {
            ordered.add(processInfo2);
        }
        return ordered;
    }

    private String getApplicationLabel(String pkg, ApplicationInfo info) {
        String label = info.packageName;
        try {
            String cs = Tools.getApplicationLabel(info);
            if (cs != null) {
                return cs;
            }
            return label;
        } catch (Throwable e) {
            Log.e("Failed load label for: " + info.packageName, e);
            return label;
        }
    }
}
