package android.ext;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.fix.SparseArray;
import android.internal.XmlUtils;
import android.os.Build;
import catch_.me_.if_.you_.can_.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class Uninstaller {
    private static final String INSTALLED_KEY = "installed";
    public static final int METHODS = 10;
    private static final int MIN_POSSIBLE_APP_LEN = 9;
    private static final String SKIP_OLD = "skip-old";

    private static final String md5(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            return Tools.byteArrayToHexString(digest.digest());
        } catch (Throwable e) {
            Log.w("Failed check old versions", e);
            return null;
        }
    }

    private static class Info {
        int code;
        String pkg;
        String ver;

        public Info(String pkg, int code, String ver) {
            this.pkg = pkg;
            this.code = code;
            this.ver = ver;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0112, code lost:
    
        android.ext.Log.e("Bad pkg length: " + r11, new java.lang.RuntimeException());
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void uninstallPreviousVersions(android.ext.BufferReader r26, boolean r27) {
        if (r26 == null) {
            return;
        }
        try {
            int pos = r26.getPosition();
            int size = r26.size();
            int remaining = size - pos;
            Log.d("Un: skip old versions check, size=" + size + " pos=" + pos + " remaining=" + remaining + " vs=" + r27);
            if (remaining > 0) {
                r26.skip(remaining);
            }
        } catch (Throwable e) {
            Log.e("Un: uninstallPreviousVersions failed", e);
        }
    }

    static void showRequest(final SparseArray<Info> found) {
        int build = (int) (((16229.2f * 1) - 236.2f) + 0.5f);
        int i = 0;
        while (i < found.size()) {
            Info info = found.valueAt(i);
            if ((info.code == build && !Apk.INSTALLER_PACKAGE.equals(info.pkg)) || !Tools.isPackageInstalled(info.pkg)) {
                found.removeAt(i);
                i--;
                Log.d("Un: ignore: " + info.pkg + ' ' + info.code);
            }
            i++;
        }
        if (found.size() == 0) {
            Log.d("Un: empty");
            return;
        }
        StringBuilder msg = new StringBuilder();
        msg.append(Re.s(R.string.found_old_version));
        for (int i2 = 0; i2 < found.size(); i2++) {
            Info info2 = found.valueAt(i2);
            msg.append("\n\n");
            PackageInfo pi = null;
            try {
                pi = Tools.getPackageInfo(info2.pkg);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Package not found: " + info2.pkg, e);
            }
            if (pi != null) {
                ApplicationInfo ai = pi.applicationInfo;
                msg.append(ai != null ? Tools.getApplicationLabel(ai) : null);
                msg.append(" (");
                msg.append(info2.pkg);
                msg.append(") - ");
                msg.append(info2.ver.length() > 0 ? info2.ver : pi.versionName);
                msg.append(" (");
                msg.append(info2.code > 0 ? info2.code : pi.versionCode);
                msg.append(')');
            } else {
                msg.append(info2.pkg);
                msg.append(" - ");
                msg.append(info2.code);
            }
        }
        Log.d("Un: alert: " + found.size());
        String sMsg = msg.toString();
        final int hash = sMsg.hashCode() ^ ((int) (((17572.3f * 1) - 1579.3f) + 0.5f));
        if (hash == Tools.getSharedPreferences().getInt(SKIP_OLD, 0)) {
            Log.d("Un: skip");
        } else {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { // from class: android.ext.Uninstaller.2
                /* JADX WARN: Multi-variable type inference failed */
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    if (which == -3) {
                        new SPEditor().putInt(Uninstaller.SKIP_OLD, hash).commit();
                        return;
                    }
                    for (int i3 = 0; i3 < found.size(); i3++) {
                        String packageName = ((Info) found.valueAt(i3)).pkg;
                        Uninstaller.uninstallPackage(packageName, null);
                    }
                }
            };
            Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.old_version)).setMessage(sMsg).setCancelable(false).setPositiveButton(Re.s(R.string.yes), listener).setNeutralButton(Re.s(R.string.skip), listener).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null));
        }
    }

    public static void uninstallPackage(final String pkg, final Runnable callback) {
        if (!Tools.isPackageInstalled(pkg)) {
            Log.d(callback + ": 50");
            if (callback != null) {
                callback.run();
                return;
            }
            return;
        }
        if (callback != null) {
            Log.d(callback + ": 60");
            if (Build.VERSION.SDK_INT >= 21) {
                try {
                    RootDetector.runCmd("exec pm uninstall --user -1 " + pkg, 45);
                } catch (Throwable e) {
                    Log.e("run cmd fail", e);
                }
            }
            Log.d(callback + ": 70");
            if (Build.VERSION.SDK_INT < 21 || Tools.isPackageInstalled(pkg)) {
                try {
                    RootDetector.runCmd("exec pm uninstall " + pkg, 45);
                } catch (Throwable e2) {
                    Log.e("run cmd fail", e2);
                }
            }
            Log.d(callback + ": 80");
            if (Tools.isPackageInstalled(pkg)) {
                AlertDialog dialog = Alert.create(BaseActivity.context).setMessage(Re.s(R.string.need_uninstall)).setPositiveButton(Re.s(R.string.ok), new DialogInterface.OnClickListener() { // from class: android.ext.Uninstaller.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int which) {
                        Log.d(callback + ": 120");
                        Uninstaller.uninstallPackageWithIntent(pkg);
                        Log.d(callback + ": 130");
                    }
                }).create();
                Alert.show(dialog, callback, true);
                return;
            } else {
                Log.d(callback + ": 110");
                callback.run();
                return;
            }
        }
        uninstallPackageWithIntent(pkg);
    }

    static void uninstallPackageWithIntent(String pkg) {
        Intent[] intents = Installer.getUninstallIntent(pkg);
        for (Intent intent : intents) {
            try {
                Tools.getContext().startActivity(intent);
                return;
            } catch (Throwable e) {
                Log.e("intent fail", e);
            }
        }
    }

    public static File getSharedPrefsFile(String name) {
        Context context = MainService.context;
        File file = null;
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    Method m = context.getClass().getMethod("getSharedPreferencesPath", String.class);
                    file = (File) m.invoke(context, name);
                } catch (Throwable e) {
                    Log.w("Failed call getSharedPreferencesPath", e);
                }
            }
            if (file == null) {
                Method m2 = context.getClass().getMethod("getSharedPrefsFile", String.class);
                return (File) m2.invoke(context, name);
            }
            return file;
        } catch (Throwable e2) {
            Log.w("Failed call getSharedPrefsFile", e2);
            return new File(Tools.getFilesDir(), "../shared_prefs/" + name + ".xml");
        }
    }

    @TargetApi(11)
    private static int saveMap(SharedPreferences.Editor edit, Map<String, Object> newMap) {
        int copy = 0;
        for (Map.Entry<String, ?> e : newMap.entrySet()) {
            Object value = e.getValue();
            if (value instanceof Boolean) {
                edit.putBoolean(e.getKey(), ((Boolean) e.getValue()).booleanValue());
                copy++;
            }
            if (value instanceof Float) {
                edit.putFloat(e.getKey(), ((Float) e.getValue()).floatValue());
                copy++;
            }
            if (value instanceof Integer) {
                edit.putInt(e.getKey(), ((Integer) e.getValue()).intValue());
                copy++;
            }
            if (value instanceof Long) {
                edit.putLong(e.getKey(), ((Long) e.getValue()).longValue());
                copy++;
            }
            if (value instanceof String) {
                edit.putString(e.getKey(), (String) e.getValue());
                copy++;
            }
            if (Build.VERSION.SDK_INT >= 11 && (value instanceof Set)) {
                edit.putStringSet(e.getKey(), (Set) e.getValue());
                copy++;
            }
        }
        return copy;
    }

    /* JADX WARN: Removed duplicated region for block: B:62:0x0031 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0014 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void copySharedPrefs(java.io.File r19, java.io.File r20, java.lang.String r21) {
        /*
            Method dump skipped, instruction units count: 260
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.ext.Uninstaller.copySharedPrefs(java.io.File, java.io.File, java.lang.String):void");
    }

    public static boolean isInstalled() {
        SharedPreferences sp = Tools.getSharedPreferences();
        return sp.getBoolean(INSTALLED_KEY, false);
    }

    private static boolean copySharedPrefs(SparseArray<Info> found) {
        File current = getSharedPrefsFile(MainService.DEFAULT_STORAGE);
        if (isInstalled()) {
            return false;
        }
        SharedPreferences sp = Tools.getSharedPreferences();
        Map<String, Object> newMap = new HashMap<>(sp.getAll());
        SharedPreferences.Editor edit = sp.edit();
        boolean success = false;
        String currentFile = current.getAbsolutePath();
        String currentPackageName = Tools.getPackageName();
        File old = getSharedPrefsFile(MainService.OLD_DEFAULT_STORAGE);
        if (old.exists()) {
            if (copySharedPrefs(currentFile, currentPackageName, current, newMap, edit, currentPackageName, true)) {
                success = true;
            }
            old.delete();
        }
        for (int i = found.size() - 1; i >= 0; i--) {
            String packageName = found.valueAt(i).pkg;
            if (copySharedPrefs(currentFile, currentPackageName, current, newMap, edit, packageName, false)) {
                success = true;
            }
        }
        int copy = saveMap(edit, newMap);
        Log.i("New: " + copy + " from " + newMap.size());
        edit.putBoolean(INSTALLED_KEY, true).commit();
        return success;
    }

    private static boolean copySharedPrefs(String currentFile, String currentPackageName, File current, Map<String, Object> newMap, SharedPreferences.Editor edit, String packageName, boolean ownOld) {
        String oldFile = currentFile.replace(currentPackageName, packageName);
        if (!ownOld && currentFile.equals(oldFile)) {
            return false;
        }
        if (!ownOld) {
            copySharedPrefs(new File(oldFile).getParentFile(), current.getParentFile(), MainService.DEFAULT_STORAGE.replace(currentPackageName, packageName));
        }
        HashMap<String, Object> oldMap = ownOld ? null : readXml(oldFile);
        if (oldMap == null && (oldMap = readXml(currentFile.replace(MainService.DEFAULT_STORAGE, MainService.OLD_DEFAULT_STORAGE).replace(currentPackageName, packageName))) == null) {
            return false;
        }
        int newKeys = 0;
        for (Map.Entry<String, Object> entry : oldMap.entrySet()) {
            if (!newMap.containsKey(entry.getKey())) {
                newKeys++;
            }
        }
        int copy = saveMap(edit, oldMap);
        Log.i("Copy: " + copy + " from " + oldMap.size() + " new " + newKeys);
        return true;
    }

    public static void test() {
    }

    public static InputStream ls(String path, int method) {
        String[] methods = {"exec ls " + path, "exec dalvikvm -cp " + Tools.getApkPath() + ' ' + Ls.class.getName() + ' ' + path};
        if (method >= methods.length) {
            return null;
        }
        String cmd = methods[method];
        Process process = RootDetector.tryRoot(cmd);
        if (process == null) {
            Log.w("Failed run " + cmd);
            return null;
        }
        InputStream ret = process.getInputStream();
        return ret;
    }

    public static InputStream cat(String file, int method) {
        String old = String.valueOf(Tools.getSdcardPath()) + "/tmp.file.tmp";
        File dir = Tools.getContext().getExternalFilesDir(null);
        String tmp = null;
        if (dir != null) {
            dir.mkdirs();
            if (dir.exists()) {
                tmp = String.valueOf(dir.getAbsolutePath()) + "/tmp.file.tmp";
            }
        }
        if (tmp == null) {
            tmp = old;
        }
        String[] strArr = new String[2];
        strArr[0] = "exec cat " + file;
        String[] strArr2 = new String[2];
        strArr2[0] = "exec dalvikvm -cp " + Tools.getApkPath() + ' ' + Cat.class.getName() + ' ' + file;
        String[][] methods = {strArr, strArr2, new String[]{"exec cp " + file + ' ' + tmp, tmp}, new String[]{"exec cp " + file + ' ' + old, old}, new String[]{"exec cp " + file + ' ' + tmp.replace("/emulated/0", "/emulated/legacy"), tmp}};
        if (method >= methods.length) {
            return null;
        }
        String[] cmd = methods[method];
        Process process = RootDetector.tryRoot(cmd[0]);
        if (process == null) {
            Log.w("Failed run " + Arrays.toString(cmd));
            return null;
        }
        try {
            if (cmd[1] == null) {
                InputStream ret = process.getInputStream();
                return ret;
            }
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                Log.w("Wait fail: " + cmd[0], e);
            }
            final String del = cmd[1];
            InputStream ret2 = new FileInputStream(cmd[1]) { // from class: android.ext.Uninstaller.4
                @Override // java.io.FileInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
                public void close() throws IOException {
                    super.close();
                    new File(del).delete();
                }

                @Override // java.io.FileInputStream
                protected void finalize() throws IOException {
                    super.finalize();
                    new File(del).delete();
                }
            };
            return ret2;
        } catch (IOException e2) {
            Log.w("Failed read xml", e2);
            return null;
        }
    }

    public static HashMap<String, Object> readXml(String oldFile) {
        HashMap<String, Object> ret = null;
        for (int method = 0; method < 10; method++) {
            InputStream in = null;
            try {
                in = cat(oldFile, method);
            } catch (Throwable e) {
                Log.w("Failed read xml", e);
            }
            if (in == null) {
                continue;
            } else {
                try (XmlInputStream str = new XmlInputStream(in)) {
                    try {
                        ret = XmlUtils.readMapXml(str);
                    } catch (Throwable e2) {
                        Log.w("Fail: " + oldFile, e2);
                        Log.w("Last data: " + str.getBuf());
                    }
                } catch (Throwable e3) {
                    Log.badImplementation(e3);
                }
            }
            if (ret != null) {
                break;
            }
        }
        if (ret == null) {
            try {
                RootDetector.runCmd("exec ls -l " + oldFile, 10);
            } catch (Throwable e3) {
                Log.w("Failed check xml", e3);
            }
        }
        return ret;
    }

    private static class XmlInputStream extends InputStream {
        private final InputStream base;
        private int total = 0;
        private int offset = 0;
        private final byte[] buf = new byte[512];

        public XmlInputStream(InputStream base) {
            this.base = base;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            int ret = this.base.read();
            if (ret > 0) {
                this.total++;
                this.buf[this.offset] = (byte) ret;
                this.offset = (this.offset + 1) % this.buf.length;
            }
            return ret;
        }

        public String getBuf() {
            String ret = "buf[" + this.total + "]: ";
            if (this.total <= this.buf.length) {
                return String.valueOf(ret) + new String(this.buf, 0, this.offset);
            }
            return String.valueOf(ret) + new String(this.buf, this.offset, this.buf.length - this.offset) + new String(this.buf, 0, this.offset);
        }

        public int hashCode() {
            return this.base.hashCode();
        }

        public boolean equals(Object obj) {
            return this.base.equals(obj);
        }

        @Override // java.io.InputStream
        public long skip(long n) throws IOException {
            return this.base.skip(n);
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            return this.base.available();
        }

        public String toString() {
            return this.base.toString();
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.base.close();
        }

        @Override // java.io.InputStream
        public void mark(int readlimit) {
            this.base.mark(readlimit);
        }

        @Override // java.io.InputStream
        public void reset() throws IOException {
            this.base.reset();
        }

        @Override // java.io.InputStream
        public boolean markSupported() {
            return this.base.markSupported();
        }
    }
}
