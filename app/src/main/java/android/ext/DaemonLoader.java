package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.system.ErrnoException;
import android.system.Os;
import android.system.StructStat;
import catch_.me_.if_.you_.can_.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import lasm.LasmConstants;

/* JADX INFO: loaded from: classes.dex */
public class DaemonLoader {
    private static final int ARCH_ARM = 103;
    private static final int ARCH_ARM_64 = 105;
    private static final int ARCH_ARM_V7A = 104;
    private static final int ARCH_UNKNOWN = 100;
    private static final int ARCH_X86 = 101;
    private static final int ARCH_X86_64 = 102;
    private static final char LIB_ARM = 'a';
    private static final char LIB_ARM64_CLIENT = 'r';
    private static final char LIB_ARM64_PATCH = 'l';
    private static final char LIB_ARM64_PIE = '5';
    private static final char LIB_ARM64_SH = '6';
    private static final char LIB_ARM_CLIENT = 't';
    private static final char LIB_ARM_PATCH = 'o';
    private static final char LIB_ARM_PIE = 'b';
    private static final char LIB_ARM_SH = 'd';
    private static final char LIB_ARM_V7A = '7';
    private static final char LIB_ARM_V7A_CLIENT = 's';
    private static final char LIB_ARM_V7A_PATCH = 'n';
    private static final char LIB_ARM_V7A_PIE = '8';
    private static final char LIB_ARM_V7A_SH = '9';
    private static final char LIB_ARM_V7A_STUB = 'h';
    private static final char LIB_X86 = '0';
    private static final char LIB_X86_64_CLIENT = 'q';
    private static final char LIB_X86_64_PATCH = 'k';
    private static final char LIB_X86_64_PIE = '2';
    private static final char LIB_X86_64_SH = '4';
    private static final char LIB_X86_64_STUB = 'f';
    private static final char LIB_X86_CLIENT = 'p';
    private static final char LIB_X86_PATCH = 'j';
    private static final char LIB_X86_PIE = '1';
    private static final char LIB_X86_SH = '3';
    private static final char LIB_X86_STUB = 'e';
    private static final String LOADER = "0";
    private final String DAEMON_PATH = getDaemonPath_();
    private static volatile String path = null;
    private static volatile String dir = null;
    private static volatile int arch = 100;
    private static final char LIB_ARM_STUB = 'i';
    private static volatile char stub32 = LIB_ARM_STUB;
    private static final char LIB_ARM64_STUB = 'g';
    private static volatile char stub64 = LIB_ARM64_STUB;
    public static StringBuilder TESTED = new StringBuilder();
    public static StringBuilder DEBUG = new StringBuilder();
    private static boolean fixer = false;

    public static String getDaemonPath() {
        String cached = path;
        if (cached != null) {
            return cached;
        }
        synchronized (DaemonLoader.class) {
            if (path == null) {
                DaemonLoader loader = new DaemonLoader();
                path = loader.DAEMON_PATH;
            }
            return path;
        }
    }

    public static int getDaemonArch() {
        int cached = arch;
        if (cached != 100) {
            return cached;
        }
        synchronized (DaemonLoader.class) {
            if (arch == 100) {
                DaemonLoader loader = new DaemonLoader();
                if (path == null) {
                    path = loader.DAEMON_PATH;
                }
            }
            return arch;
        }
    }

    public static boolean isX86() {
        int arch2 = getDaemonArch();
        return arch2 == ARCH_X86 || arch2 == ARCH_X86_64;
    }

    private static void addFiles(ArrayList<File> list, String dir2, String hidden, boolean ifExists) {
        File check = new File(dir2);
        if (!ifExists || check.exists()) {
            list.add(new File(String.valueOf(dir2) + "/files", hidden));
            list.add(new File(String.valueOf(dir2) + "/cache", hidden));
            list.add(new File(String.valueOf(dir2) + "/files"));
            list.add(new File(String.valueOf(dir2) + "/cache"));
            list.add(check);
        }
    }

    private static File getInternalDir() {
        File files = Tools.getFilesDir();
        File cache = Tools.getCacheDir();
        String hidden = Tools.getHiddenDir();
        ArrayList<File> list = new ArrayList<>(64);
        list.add(Tools.getFilesDirHidden());
        list.add(Tools.getCacheDirHidden());
        list.add(files);
        list.add(cache);
        list.add(new File(files.getParentFile(), hidden));
        list.add(new File(cache.getParentFile(), hidden));
        list.add(files.getParentFile());
        list.add(cache.getParentFile());
        String data = files.getAbsolutePath();
        String data2 = data.startsWith("/data/user/") ? "/data/user/" + data.split("/", -1)[3] + "/" : "/data/data/";
        if (Config.vSpaceReal) {
            String data3 = String.valueOf(data2) + Config.vSpacePkg;
            addFiles(list, String.valueOf(data3) + "/parallel_intl/0/" + Apk.PACKAGE, hidden, true);
            addFiles(list, String.valueOf(data3) + "/parallel_lite/0/" + Apk.PACKAGE, hidden, true);
            addFiles(list, String.valueOf(data3) + "/virtual/data/user/0/" + Apk.PACKAGE, hidden, true);
            addFiles(list, String.valueOf(data3) + "/gaia/data/user/0/" + Apk.PACKAGE, hidden, true);
            addFiles(list, String.valueOf(data3) + "/com.vmos.glb/osimg/r/ot01/data/data/" + Apk.PACKAGE, hidden, true);
            addFiles(list, String.valueOf(data3) + "/mopen/data/user/0/" + Apk.PACKAGE, hidden, true);
            addFiles(list, String.valueOf(data3) + "/vbox/data/user/0/" + Apk.PACKAGE, hidden, true);
            addFiles(list, String.valueOf(data3) + "/sandboxdata/" + Apk.PACKAGE, hidden, true);
            addFiles(list, String.valueOf(data3) + "/gameplugins/" + Apk.PACKAGE, hidden, true);
            addFiles(list, String.valueOf(data3) + "/Plugin/0/" + Apk.PACKAGE + "/data/" + Apk.PACKAGE, hidden, true);
            addFiles(list, data3, hidden, false);
        } else {
            addFiles(list, String.valueOf(data2) + Apk.PACKAGE, hidden, false);
            list.add(new File("/data/data"));
            list.add(new File("/data"));
        }
        for (File dir2 : list) {
            if (dir2 != null) {
                if (!dir2.exists()) {
                    dir2.mkdirs();
                }
                if (dir2.exists()) {
                    return dir2;
                }
                try {
                    if (Build.VERSION.SDK_INT >= 21) {
                        String path2 = dir2.getAbsolutePath();
                        try {
                            StructStat stat = Os.stat(path2);
                            if ((stat.st_mode & 16384) != 0) {
                                return dir2;
                            }
                        } catch (Exception e) {
                            if (e instanceof ErrnoException) {
                                Log.w("Errno for path '" + path2 + "': " + ((ErrnoException) e).errno, e);
                            } else {
                                Log.w("Exception for path '" + path2 + "'", e);
                            }
                        }
                    } else {
                        continue;
                    }
                } catch (Throwable e2) {
                    Log.badImplementation(e2);
                }
            }
        }
        Log.w("Failed get dir for fix");
        return null;
    }

    private static String makeExecutable(String libPath) {
        for (char i : new char[]{LIB_X86, LIB_X86_PIE, LIB_X86_64_PIE, LIB_X86_SH, LIB_X86_64_SH, LIB_ARM64_PIE, LIB_ARM64_SH, LIB_ARM_V7A, LIB_ARM_V7A_PIE, LIB_ARM_V7A_SH, 'a', LIB_ARM_PIE, LIB_ARM_SH}) {
            File file = new File(libPath, libName(i));
            if (file.exists()) {
                String name = file.getAbsolutePath();
                if (!Tools.allowExecute(name)) {
                    Log.d("Can not execute " + name);
                }
            }
        }
        return libPath;
    }

    public static String getDaemonDir() {
        if (dir == null) {
            File newDir = getInternalDir();
            if (newDir == null) {
                newDir = Tools.getFilesDirHidden();
            }
            dir = makeExecutable(newDir.getAbsolutePath());
        }
        return dir;
    }

    private boolean isValid(char lib, int result) {
        switch (lib) {
            case LasmConstants.SHL /* 48 */:
                return ARCH_X86 == result;
            case LasmConstants.SHR /* 49 */:
                return ARCH_X86 == result;
            case '2':
                return ARCH_X86_64 == result;
            case LasmConstants.OP /* 53 */:
                return 105 == result;
            case LasmConstants.SKIP_NEXT /* 55 */:
                return ARCH_ARM_V7A == result;
            case LasmConstants.NIL /* 56 */:
                return ARCH_ARM_V7A == result;
            case 'a':
                return ARCH_ARM == result;
            case 'b':
                return ARCH_ARM == result;
            default:
                throw new RuntimeException("Unknown lib: " + lib);
        }
    }

    private static void extractLib(char libId) {
        extractLib(Character.toString(libId));
    }

    private static void extractLib(String libId) {
        String dir2 = getDaemonDir();
        File lib = new File(dir2, libName(libId));
        if (!lib.isFile()) {
            Log.d("Extract file " + lib);
            Resources r = Tools.getContext().getResources();
            int id = Re.i("chunk" + libId, R.raw.class);
            if (id == 0) {
                Log.d("Nothing extract file " + libId + ' ' + lib.getAbsolutePath());
                return;
            }
            try {
                byte[] buf = new byte[8192];
                InputStream is = r.openRawResource(id);
                OutputStream os = new FileOutputStream(lib);
                while (true) {
                    int read = is.read(buf);
                    if (read > 0) {
                        os.write(buf, 0, read);
                    } else {
                        os.close();
                        is.close();
                        return;
                    }
                }
            } catch (Throwable e) {
                Log.d("Failed extract file " + libId + ' ' + lib.getAbsolutePath(), e);
            }
        }
    }

    public static String getStubLib(boolean x64) {
        return "/lib" + (x64 ? stub64 : stub32) + ".so";
    }

    private void extractShLibs(char libId) {
        extractLib(libId);
        switch (libId) {
            case '2':
                extractLib("01");
                extractLib(LIB_X86_64_SH);
                extractLib(LIB_X86_64_STUB);
                stub64 = LIB_X86_64_STUB;
                extractLib(LIB_X86_64_PATCH);
            case LasmConstants.SHL /* 48 */:
            case LasmConstants.SHR /* 49 */:
                extractLib(LIB_X86_SH);
                extractLib(LIB_X86_STUB);
                stub32 = LIB_X86_STUB;
                extractLib(LIB_X86_PATCH);
                break;
            case LasmConstants.OP /* 53 */:
                extractLib("08");
                extractLib(LIB_ARM64_SH);
                extractLib(LIB_ARM64_STUB);
                stub64 = LIB_ARM64_STUB;
                extractLib(LIB_ARM64_PATCH);
            case LasmConstants.SKIP_NEXT /* 55 */:
            case LasmConstants.NIL /* 56 */:
                extractLib(LIB_ARM_V7A_SH);
                extractLib('h');
                stub32 = 'h';
                extractLib(LIB_ARM_V7A_PATCH);
                break;
            case 'a':
            case 'b':
                extractLib(LIB_ARM_SH);
                extractLib(LIB_ARM_STUB);
                stub32 = LIB_ARM_STUB;
                extractLib(LIB_ARM_PATCH);
                break;
        }
        try {
            File output = new File(Tools.getFilesDirHidden(), "sh.jar");
            Tools.extractFile(RootDetector.SH_KEY, output);
        } catch (Throwable e) {
            Log.e("JVM fail", e);
        }
    }

    public static void initNative() {
        int sdk = Build.VERSION.SDK_INT;
        char[] libs;
        if (sdk < 21) {
            libs = new char[]{'\0', LIB_X86_CLIENT, LIB_ARM_V7A_CLIENT, LIB_ARM_CLIENT, LIB_X86_64_CLIENT, LIB_ARM64_CLIENT};
        } else {
            libs = new char[]{'\0', LIB_X86_64_CLIENT, LIB_X86_CLIENT, LIB_ARM64_CLIENT, LIB_ARM_V7A_CLIENT, LIB_ARM_CLIENT};
        }
        String daemonDir = getDaemonDir();
        for (int i = 0; i < libs.length; i++) {
            if (i == 0) {
                for (int j = 1; j < libs.length; j++) {
                    File libFile = new File(daemonDir, libName(libs[j]));
                    if (libFile.exists()) {
                        libs[0] = libs[j];
                        break;
                    }
                }
                if (libs[0] == '\0') {
                    continue;
                }
            }
            File lib = new File(daemonDir, libName(libs[i]));
            if (!lib.isFile()) {
                extractLib(libs[i]);
            }
            if (!lib.isFile()) {
                Log.d("Not a file " + lib + " " + lib.getAbsolutePath());
            } else {
                String path2 = lib.getAbsolutePath();
                try {
                    System.load(path2);
                    try {
                        android.lang.ProcessBuilder.loaded();
                        return;
                    } catch (Throwable e) {
                        Log.d("Failed check " + lib + ' ' + lib.getAbsolutePath(), e);
                    }
                } catch (Throwable e2) {
                    Log.d("Failed load " + lib + ' ' + lib.getAbsolutePath(), e2);
                }
            }
        }
    }

    private static String libName(char lib) {
        return libName(Character.toString(lib));
    }

    private static String libName(String lib) {
        return "lib" + lib + ".so";
    }

    private String getDaemonPath_() {
        try {
            String dir2 = getDaemonDir();
            int sdk = Build.VERSION.SDK_INT;
            String[] abis;
            if (sdk >= 21) {
                abis = Build.SUPPORTED_ABIS;
            } else {
                abis = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
            }
            String abiText = abis == null ? "" : java.util.Arrays.toString(abis).toLowerCase(java.util.Locale.US);

            char lib2;
            int res2;
            if (abiText.contains("x86_64")) {
                lib2 = '2';
                res2 = ARCH_X86_64;
            } else if (abiText.contains("arm64")) {
                lib2 = '5';
                res2 = ARCH_ARM_64;
            } else if (abiText.contains("x86")) {
                lib2 = sdk >= 21 ? '1' : LIB_X86;
                res2 = ARCH_X86;
            } else if (abiText.contains("armeabi-v7a")) {
                lib2 = sdk >= 21 ? '8' : LIB_ARM_V7A;
                res2 = ARCH_ARM_V7A;
            } else {
                lib2 = sdk >= 21 ? 'b' : 'a';
                res2 = ARCH_ARM;
            }

            InOut.setX64(res2 == ARCH_ARM_64 || res2 == ARCH_X86_64);
            arch = res2;
            String path2 = new File(dir2, libName(lib2)).getAbsolutePath();
            Log.e("Daemon: " + path2);
            extractShLibs(lib2);
            Tools.allowExecute(path2);
            return path2;
        } catch (Throwable e) {
            Log.w("getDaemonPath_ error", e);
        }
        
        // Fallback if all else fails
        return "no_binary_for_your_arch";
    }

    private int getArch(String filename) {
        int result = 100;
        Tools.allowExecute(filename);
        int i = 0;
        while (true) {
            if (i >= 2) {
                break;
            }
            try {
                String[] strArr = new String[2];
                strArr[0] = Tools.getRealPath(filename);
                strArr[1] = Config.vSpaceReal ? Config.vSpacePkg : "1";
                Process p = Tools.exec(strArr);
                if (Tools.waitForTimeout(p, 30)) {
                    result = p.exitValue();
                } else {
                    throw new RuntimeException("timeout: 30");
                }
            } catch (IOException e) {
                if (i != 0) {
                    break;
                }
                try {
                    String msg = e.getMessage();
                    if (msg == null || msg.indexOf("denied") == -1) {
                        break;
                    }
                    Tools.chmod(filename, "0755");
                    i++;
                } catch (Throwable e2) {
                    Log.w("getArch: " + filename, e2);
                    DEBUG.append(e2.toString());
                    if (Config.vSpace && ((Config.vSpace64 == 1 || (Config.vSpace64 == 0 && Config.vSpacePkg.contains("64"))) && !fixer && (result & AddressItem.FLAG_AUTO) < 100)) {
                        fixer = true;
                        try {
                            final File output = new File(Tools.getSdcardPath(), "gg_fixer.apk");
                            Tools.extractFile("fx", output);
                            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.DaemonLoader.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    AlertDialog.Builder message = Alert.create().setMessage(Tools.stringFormat(Re.s(R.string.need_fixer), output.getAbsolutePath()));
                                    String strS = Re.s(R.string.fix_it);
                                    final File file = output;
                                    Alert.show(message.setPositiveButton(strS, new DialogInterface.OnClickListener() { // from class: android.ext.DaemonLoader.1.1
                                        @Override // android.content.DialogInterface.OnClickListener
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent[] intents = Installer.getInstallIntent(file);
                                            for (Intent intent : intents) {
                                                try {
                                                    Tools.getContext().startActivity(intent);
                                                    return;
                                                } catch (Throwable e3) {
                                                    Log.badImplementation(e3);
                                                }
                                            }
                                        }
                                    }).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                                }
                            });
                        } catch (Throwable e3) {
                            Log.w("Failed extract fx", e3);
                        }
                    }
                    return result;
                }
                return result;
            }
        }
        return result;
    }
}
