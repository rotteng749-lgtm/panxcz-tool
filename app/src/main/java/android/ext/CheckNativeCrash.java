package android.ext;

import android.content.SharedPreferences;
import android.os.Process;
import android.sup.ContainerHelpers;
import android.text.TextUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/* JADX INFO: loaded from: classes.dex */
public class CheckNativeCrash {
    private static volatile String buggedPackages = ";;";
    private static volatile String buggedUids = ";;";
    private static final boolean IS_CM13 = isCm13();
    private static final String BUGGED_PACKAGES = "bugged-packages";
    private static final String BUGGED_UIDS = "bugged-uids";
    private static final String FILENAME = "native_crash.txt";
    private static final String DELIMITER = " _QAZ_WSX_EDC_RFV_TGB_YHN_UJM_ ";

    private static boolean isCm13() {
        boolean ret = false;
        try {
            File lib = new File("/system/lib/libandroidfw.so");
            if (lib.exists()) {
                InputStream is = new FileInputStream(lib);
                byte[] pattern = "_ZN7android12AssetManager10getPkgNameEPKc".getBytes();
                byte[] buffer = new byte[8192];
                int len = pattern.length;
                int offset = 0;
                while (true) {
                    int count = is.read(buffer, offset, buffer.length - offset);
                    if (count <= 0) {
                        break;
                    }
                    int size = offset + count;
                    if (size >= len) {
                        int check = (size - len) + 1;
                        int i = 0;
                        while (true) {
                            if (i >= check) {
                                break;
                            }
                            boolean found = true;
                            int j = 0;
                            while (true) {
                                if (j >= len) {
                                    break;
                                }
                                if (buffer[i + j] == pattern[j]) {
                                    j++;
                                } else {
                                    found = false;
                                    break;
                                }
                            }
                            if (!found) {
                                i++;
                            } else {
                                ret = true;
                                break;
                            }
                        }
                        if (ret) {
                            break;
                        }
                        offset = len - 1;
                        for (int j2 = 0; j2 < offset; j2++) {
                            buffer[j2] = buffer[(size - offset) + j2];
                        }
                    } else {
                        offset = size;
                    }
                }
                is.close();
            }
        } catch (Throwable e) {
            Log.w("Failed detect CM13", e);
        }
        Log.d("CM13: " + ret);
        return ret;
    }

    public static boolean isBuggedPackage(String pkg) {
        if (IS_CM13) {
            return buggedPackages.contains(String.valueOf(';') + pkg + ';');
        }
        return false;
    }

    public static boolean isBuggedUid(String uid) {
        if (IS_CM13) {
            return buggedUids.contains(String.valueOf(';') + uid + ';');
        }
        return false;
    }

    private static String unique(String list) {
        ArrayList<String> arr = new ArrayList<>(new HashSet(Arrays.asList(list.split(";"))));
        return TextUtils.join(";", arr.toArray(new String[arr.size()]));
    }

    private static String getBuggedPackagesString() {
        SharedPreferences pref = Tools.getSharedPreferences();
        String list = unique(pref.getString(BUGGED_PACKAGES, ""));
        if (list.length() > 0) {
            buggedPackages = String.valueOf(';') + list + ';';
        }
        return list;
    }

    private static String getBuggedUidsString() {
        SharedPreferences pref = Tools.getSharedPreferences();
        String list = unique(pref.getString(BUGGED_UIDS, ""));
        if (list.length() > 0) {
            buggedUids = String.valueOf(';') + list + ';';
        }
        return list;
    }

    public static String[] getBuggedPackages() {
        if (IS_CM13) {
            String list = getBuggedPackagesString();
            if (list.length() > 0) {
                Log.d("BUGGED_PACKAGES: " + list);
                return list.split(";");
            }
        }
        return ContainerHelpers.EMPTY_STRINGS;
    }

    public static String[] getBuggedUids() {
        if (IS_CM13) {
            String list = getBuggedUidsString();
            if (list.length() > 0) {
                Log.d("BUGGED_UIDS: " + list);
                return list.split(";");
            }
        }
        return ContainerHelpers.EMPTY_STRINGS;
    }

    private static String[] getLastCrash() {
        File[] list;
        String[] ret = null;
        File parent = getFile().getParentFile();
        if (parent != null && (list = parent.listFiles()) != null) {
            int len = list.length;
            ret = new String[len * 2];
            for (int i = 0; i < len; i++) {
                File file = list[i];
                if (file != null && file.getName().endsWith(FILENAME)) {
                    try {
                        InputStream is = new FileInputStream(file);
                        byte[] data = new byte[Math.min(((int) file.length()) + 8192, 102400)];
                        int read = is.read(data);
                        is.close();
                        if (read > 0) {
                            String[] parts = new String(data, 0, read).split(DELIMITER, 2);
                            if (parts.length == 2) {
                                ret[(i * 2) + 0] = parts[0];
                                ret[(i * 2) + 1] = parts[1];
                            }
                        }
                    } catch (Throwable e) {
                        Log.e("Failed read native crash", e);
                    }
                    file.delete();
                }
            }
        }
        return ret;
    }

    public static void loadLastCrash() {
        Log.d("lLC - 1");
        getBuggedPackagesString();
        getBuggedUidsString();
        String[] list = getLastCrash();
        if (list != null) {
            int len = list.length;
            for (int i = 0; i < len; i += 2) {
                String bad = list[i];
                if (bad != null) {
                    String msg = "Bad package: '" + bad + "' " + list[i + 1];
                    Log.e(msg);
                    String key = BUGGED_PACKAGES;
                    if (bad.contains("uid:")) {
                        key = BUGGED_UIDS;
                        bad = bad.split(":", -1)[1];
                    }
                    SharedPreferences pref = Tools.getSharedPreferences();
                    String bads = pref.getString(key, "");
                    if (bads == null || bads.length() == 0) {
                        bads = bad;
                    } else if (!(String.valueOf(';') + bads + ';').contains(String.valueOf(';') + bad + ';')) {
                        bads = String.valueOf(bads) + ';' + bad;
                    }
                    pref.edit().putString(key, unique(bads)).commit();
                }
            }
        }
        getBuggedPackagesString();
        getBuggedUidsString();
        Log.d("lLC - 0");
    }

    public static boolean enter(String pkg, String info) {
        if (!IS_CM13 || Apk.PACKAGE.equals(pkg)) {
            return false;
        }
        if (pkg.startsWith("uid:") && pkg.equals("uid:" + Process.myUid())) {
            return false;
        }
        try {
            Log.d("CNC[" + (IS_CM13 ? '1' : '0') + "] - n: " + info + ListManager.TEXT_SEPARATOR + pkg);
            if (IS_CM13) {
                OutputStream os = new FileOutputStream(getFile());
                os.write(pkg.getBytes());
                os.write(DELIMITER.getBytes());
                os.write((String.valueOf(info) + '\n' + Debug.getInfo()).getBytes());
                os.flush();
                os.close();
            }
        } catch (Throwable e) {
            Log.e("Failed n CNC", e);
        }
        return true;
    }

    public static void exit(boolean entered) {
        if (IS_CM13 && entered) {
            try {
                Log.d("CNC[" + (IS_CM13 ? '1' : '0') + "] - x");
                if (IS_CM13) {
                    getFile().delete();
                }
            } catch (Throwable e) {
                Log.e("Failed x CNC", e);
            }
        }
    }

    private static File getFile() {
        File file = new File(Tools.getFilesDirHidden(), String.valueOf(Process.myPid()) + FILENAME);
        try {
            file.getParentFile().mkdirs();
        } catch (Throwable e) {
            Log.e("Failed mkdirs CNC", e);
        }
        return file;
    }
}
