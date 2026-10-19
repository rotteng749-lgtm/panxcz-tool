package android.ext;

import android.ext.ProcessList;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class PkgPath {
    public static String path(String path, String key) {
        String def = String.valueOf(Tools.getSdcardPath()) + "/Notes";
        if (path == null) {
            return Tools.getSharedPreferences().getString(key, def);
        }
        new SPEditor().putString(key, path, def).commit();
        return null;
    }

    public static void save(String filename, String dirIndex, String pkgKey, String ext) {
        File file = new File(filename.trim());
        String dir = file.getParent();
        if (dirIndex != null && dir != null && dir.length() > 0) {
            path(dir, dirIndex);
        }
        ProcessList.ProcessInfo info = MainService.instance.processInfo;
        if (info != null) {
            String pkg = info.packageName;
            new SPEditor().putString(String.valueOf(pkg) + pkgKey, file.getAbsolutePath(), String.valueOf(path(null, dirIndex)) + '/' + pkg + ext).commit();
        }
    }

    public static String load(String dirIndex, String pkgKey, String ext) {
        ProcessList.ProcessInfo info = MainService.instance.processInfo;
        if (info == null) {
            return String.valueOf(path(null, dirIndex)) + "/file" + ext;
        }
        String pkg = info.packageName;
        String last = Tools.getSharedPreferences().getString(String.valueOf(pkg) + pkgKey, null);
        return last == null ? String.valueOf(path(null, dirIndex)) + '/' + pkg + ext : last;
    }
}
