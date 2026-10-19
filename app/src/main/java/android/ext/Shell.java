package android.ext;

import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class Shell {
    private static final String[] systemDirs = {"/system/bin/", "/system/xbin/"};

    public static boolean isCmdPresent(String cmd) {
        for (String path : systemDirs) {
            File file = new File(String.valueOf(path) + cmd);
            boolean present = file.exists();
            if (present) {
                break;
            }
        }
        return false;
    }
}
