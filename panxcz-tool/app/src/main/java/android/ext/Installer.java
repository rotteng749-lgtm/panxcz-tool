package android.ext;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.ext.BaseActivity;
import android.ext.ExceptionHandler;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.ActivityMain;
import catch_.me_.if_.you_.can_.AnalyticsService;
import catch_.me_.if_.you_.can_.Application;
import catch_.me_.if_.you_.can_.BuildConfig;
import catch_.me_.if_.you_.can_.FileProvider;
import catch_.me_.if_.you_.can_.Instrumentation;
import catch_.me_.if_.you_.can_.MainActivity;
import catch_.me_.if_.you_.can_.R;
import catch_.me_.if_.you_.can_.Receiver;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import luaj.LuaThread;
import luaj.lib.IoLib;

/* JADX INFO: loaded from: classes.dex */
public class Installer {
    private static final int BUFFER_SIZE = 65536;
    private static final int DEX_CHECKSUM_OFFSET = 8;
    private static final int DEX_SHA_OFFSET = 12;
    private static final int DEX_SHA_SIZE = 20;
    private static final int FILE_ANDROID_MANIFEST_XML = 1;
    private static final int FILE_CERT_RSA = 5;
    private static final int FILE_CERT_SF = 4;
    private static final int FILE_CLASSES_DEX = 0;
    private static final int FILE_MANIFEST_MF = 3;
    private static final int FILE_NOT_FOUND = -1;
    private static final int FILE_RESOURCES_ARSC = 2;
    private static final String INSTALLER_FILE = "installer.fail";
    private static final int INSTALL_APK = 1;
    private static final String INTENT_FROM_INSTALLER = ".fromInstaller";
    private static final int LABEL = 1;
    private static final int MAX_STEP = 1000;
    public static final byte MODE_32 = 1;
    public static final byte MODE_64 = 2;
    public static final byte MODE_DEFAULT = 0;
    private static final int PACKAGE = 0;
    private static final int REMOVE_APK = 2;
    private static final int SKIP_END = 11604;
    private static final int SKIP_START = 4700;
    private static final int STRING_IDS_OFFSET = 56;
    private static final String TEMP_APK = "temp.apk";
    private static final int VERSION = 2;
    private static final String chunk3a = "res/raw/chunk3a";
    private static final String chunk3x = "res/raw/chunk3x";
    static Installer instance = null;
    private static final String lib3a = "lib/armeabi/libAndroid.so";
    private static final String lib3x = "lib/x86/libAndroid.so";
    private static final String lib6a = "lib/arm64-v8a/libAndroid.so";
    private static final String lib6x = "lib/x86_64/libAndroid.so";
    File apk;
    private byte[] buf;
    private final File intDir;
    private Manifest mf;
    private final String newLabel;
    private final String newPackage;
    private static final String[] FIX_FILES = {"classes.dex", "AndroidManifest.xml", "resources.arsc", "META-INF/MANIFEST.MF", "META-INF/CERT.SF", "META-INF/CERT.RSA"};
    static byte mode = 0;
    static byte[][] memCache = null;
    static final boolean[] useMemCache = {false, true, false, true, true, true};
    static volatile State state = null;
    public static volatile WeakReference<Button> weakCancel = new WeakReference<>(null);
    private static final Runnable updater = new Runnable() { // from class: android.ext.Installer.1
        @Override // java.lang.Runnable
        public void run() {
            Installer.updateProgress();
            Handler handler = ThreadManager.getHandlerUiThread();
            handler.removeCallbacks(this);
            handler.postDelayed(this, 500L);
        }
    };
    public static volatile WeakReference<ProgressBar> weakBar = new WeakReference<>(null);
    public static volatile WeakReference<TextView> weakBarText = new WeakReference<>(null);
    public static volatile WeakReference<TextView> weakMsg = new WeakReference<>(null);
    private static volatile int wait = 0;
    private static volatile boolean sGrant = false;
    static volatile int nextInstallIntent = 0;
    private static volatile WeakReference<File> source = new WeakReference<>(null);
    private static String hash = null;
    private static final Object syncGetHashes = new Object();
    private final RandomAccessFile[] rafs = new RandomAccessFile[FIX_FILES.length];
    private final Item[] list = new Item[FIX_FILES.length];
    volatile AlertDialog dialog = null;
    private final MessageDigest sha1 = MessageDigest.getInstance("SHA1");
    private final MessageDigest sha1_2 = MessageDigest.getInstance("SHA1");

    public static class Item {
        public ZipEntry entry;
        public String extractHash;
        public String putHash;

        public Item(ZipEntry entry, String extractHash) {
            this.entry = entry;
            this.extractHash = extractHash;
        }

        public String toString() {
            return "Item [entry=" + this.entry + ", extractHash=" + this.extractHash + ", putHash=" + this.putHash + ']';
        }
    }

    public static class State implements Parcelable {
        public static final Parcelable.Creator<State> CREATOR = new Parcelable.Creator<State>() { // from class: android.ext.Installer.State.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public State createFromParcel(Parcel in) {
                return new State(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public State[] newArray(int size) {
                return new State[size];
            }
        };
        String apk;
        String dbg;
        boolean isInstaller;
        boolean manualInstall;
        String newLabel;
        String newPackage;
        int step;
        int waitResult;

        State() {
            this.isInstaller = false;
            this.step = 0;
            this.apk = null;
            this.newPackage = null;
            this.newLabel = null;
            this.manualInstall = false;
            this.waitResult = 0;
            this.dbg = "";
        }

        protected State(Parcel in) {
            this.isInstaller = false;
            this.step = 0;
            this.apk = null;
            this.newPackage = null;
            this.newLabel = null;
            this.manualInstall = false;
            this.waitResult = 0;
            this.dbg = "";
            this.isInstaller = in.readByte() != 0;
            this.step = in.readInt();
            this.apk = in.readString();
            this.newPackage = in.readString();
            this.newLabel = in.readString();
            this.manualInstall = in.readByte() != 0;
            this.waitResult = in.readInt();
            this.dbg = in.readString();
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte((byte) (this.isInstaller ? 1 : 0));
            dest.writeInt(this.step);
            dest.writeString(this.apk);
            dest.writeString(this.newPackage);
            dest.writeString(this.newLabel);
            dest.writeByte((byte) (this.manualInstall ? 1 : 0));
            dest.writeInt(this.waitResult);
            dest.writeString(this.dbg);
        }
    }

    static void setState(State st) {
        state = st;
    }

    static State getState() {
        if (state == null) {
            State st = new State();
            st.isInstaller = needInstall();
            state = st;
        }
        return state;
    }

    public static boolean needInstall() {
        return false;
    }

    public static void removeInstaller(Runnable callback) {
        boolean fromIntent = false;
        Intent intent = BaseActivity.instance.getIntent();
        if (intent != null && intent.getBooleanExtra(String.valueOf(Apk.INSTALLER_PACKAGE) + INTENT_FROM_INSTALLER, false)) {
            fromIntent = true;
        }
        Log.d("removeInstaller: " + fromIntent + " + " + BaseActivity.removeInstaller);
        if (BaseActivity.removeInstaller || fromIntent) {
            BaseActivity.removeInstaller = true;
            Log.d(callback + ": 30");
            Uninstaller.uninstallPackage(Apk.INSTALLER_PACKAGE, callback);
            Log.d(callback + ": 40");
            return;
        }
        callback.run();
    }

    private static void setVersion() {
        try {
            File file = getFile(Tools.removeNewLinesChars("yhuvlrq1jj"));
            byte[] ver = (String.valueOf(Integer.toString((int) (((16290.9f * 1) - 297.9f) + 0.5f))) + ':' + ((101.9f * 1) - 5.9f)).getBytes();
            if (file.exists()) {
                try {
                    InputStream is = new FileInputStream(file);
                    try {
                        byte[] buffer = new byte[ver.length];
                        if (is.read(buffer) != buffer.length || !Arrays.equals(buffer, ver)) {
                            getFile(INSTALLER_FILE).delete();
                            File dir = Tools.getFilesDirHidden();
                            String[] files = dir.list();
                            if (files != null) {
                                for (String old : files) {
                                    if ((old.startsWith("lib") && old.endsWith(".so")) || "gr".equals(old)) {
                                        new File(dir, old).delete();
                                    }
                                }
                            }
                        }
                    } finally {
                        is.close();
                    }
                } catch (Throwable e) {
                    Log.e("Fail get version", e);
                }
            }
            OutputStream os = new FileOutputStream(file);
            Tools.chmod(file, 384);
            try {
                os.write(ver);
            } finally {
                os.close();
            }
        } catch (Throwable e2) {
            Log.e("Fail set version", e2);
        }
    }

    public static void startInstall() {
    }

    static void install() {
        memCache = new byte[FIX_FILES.length][];
        try {
            instance = new Installer();
            instance._install();
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Installer.3
                @Override // java.lang.Runnable
                public void run() {
                    BaseActivity.instance.finish();
                }
            });
        } catch (Throwable e) {
            Log.e("Failed install", e);
            final String msg = e.getMessage();
            if (msg != null && (msg.contains("ENOSPC") || msg.contains("o space left"))) {
                ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Installer.4
                    @Override // java.lang.Runnable
                    public void run() {
                        String file = null;
                        if (Installer.instance != null && Installer.instance.apk != null) {
                            file = Installer.instance.apk.getAbsolutePath();
                        }
                        AlertDialog dialog = Alert.create(BaseActivity.context).setMessage(String.valueOf(Re.s(R.string.no_space)) + '\n' + msg + '\n' + file).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: android.ext.Installer.4.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog2, int which) {
                                BaseActivity.restartApp();
                            }
                        }).setCancelable(false).create();
                        Alert.show(dialog);
                    }
                });
                return;
            }
            if (!(e instanceof InstallException)) {
                ExceptionHandler.sendException(Thread.currentThread(), e, false);
            }
            cancelInstall(false);
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Installer.5
                @Override // java.lang.Runnable
                public void run() {
                    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { // from class: android.ext.Installer.5.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == -1) {
                                new BaseActivity.GoOnForum(BaseActivity.GoOnForum.S11).onClick(dialog, which);
                                ThreadManager.getHandlerUiThread().postDelayed(new Runnable() { // from class: android.ext.Installer.5.1.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        Log.d("Kill by Installer");
                                        Main.die();
                                    }
                                }, LuaThread.thread_orphan_check_interval);
                            } else {
                                Installer.cancelInstall(true);
                            }
                        }
                    };
                    AlertDialog dialog = Alert.create(BaseActivity.context).setMessage(String.valueOf(Tools.stripColon(R.string.reinstall_fail)) + ":\n\n" + Log.getStackTraceString(e)).setPositiveButton(R.string.more, listener).setNegativeButton(R.string.ok, listener).setCancelable(false).create();
                    Alert.show(dialog);
                }
            });
        }
    }

    static void cancelInstall(boolean restart) {
        try {
            File file = getFile(INSTALLER_FILE);
            file.createNewFile();
        } catch (IOException e) {
            Log.e("Failed set install flag", e);
        }
        if (restart) {
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Installer.6
                @Override // java.lang.Runnable
                public void run() {
                    BaseActivity.restartApp();
                }
            });
        }
    }

    private static void promptRetry(final String message, final int requestCode, final int resultCode, final Intent[] intents) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Installer.7
            @Override // java.lang.Runnable
            public void run() {
                final int i = requestCode;
                final Intent[] intentArr = intents;
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { // from class: android.ext.Installer.7.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case -2:
                                Installer.state.waitResult &= i ^ (-1);
                                break;
                            case -1:
                                int i2 = -1;
                                int next = i == 1 ? Installer.nextInstallIntent % intentArr.length : 0;
                                for (Intent intent : intentArr) {
                                    i2++;
                                    if (i2 >= next) {
                                        try {
                                            if (i == 1) {
                                                Installer.nextInstallIntent++;
                                            }
                                            BaseActivity.instance.startActivityForResult(intent, i);
                                        } catch (Throwable e) {
                                            Log.e("intent fail", e);
                                        }
                                        break;
                                    }
                                }
                                break;
                        }
                        Installer.instance.dialog = null;
                    }
                };
                Installer.instance.dismissDialog();
                AlertDialog dialog = Alert.create(BaseActivity.context).setMessage(String.valueOf(Installer.nextInstallIntent) + ": [" + resultCode + "] " + message).setPositiveButton(R.string.yes, listener).setNegativeButton(R.string.no, listener).setCancelable(false).create();
                Alert.show(dialog);
                Installer.instance.dialog = dialog;
            }
        });
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent intent) {
        State state2 = state;
        Log.d("instance: " + instance + ListManager.TEXT_SEPARATOR + state2);
        if (state2 != null) {
            state2.dbg = String.valueOf(state2.dbg) + "\nget: " + resultCode + " for: " + requestCode + " with: " + intent;
            if (instance != null) {
                switch (requestCode) {
                    case 1:
                        if (isPackageInstalledSafe(instance.newPackage)) {
                            state2.waitResult &= requestCode ^ (-1);
                        } else {
                            promptRetry(String.valueOf(Re.s(R.string.failed_install)) + "\n\n" + Tools.stripColon(R.string.manual_install) + ":\n" + instance.apk.getAbsolutePath(), requestCode, resultCode, instance.getInstallIntent());
                        }
                        break;
                    case 2:
                        promptRetry(Re.s(R.string.failed_uninstall), requestCode, resultCode, getUninstallIntent(Apk.INSTALLER_PACKAGE));
                        break;
                }
            }
        }
    }

    private Installer() throws NoSuchAlgorithmException {
        File intDir = getDir(true);
        intDir.mkdirs();
        this.intDir = intDir;
        Log.d("Int dir: " + intDir.getAbsolutePath());
        if (state.apk == null) {
            File dir = getDir(false);
            dir.mkdirs();
            state.apk = new File(dir, TEMP_APK).getAbsolutePath();
        }
        this.apk = new File(state.apk);
        Log.d("Apk: " + this.apk.getAbsolutePath());
        if (state.newPackage == null) {
            state.newPackage = getNewPackage();
        }
        if (state.newLabel == null) {
            state.newLabel = getNewString(1);
        }
        this.newPackage = state.newPackage;
        this.newLabel = state.newLabel;
        Log.d("package: " + Apk.INSTALLER_PACKAGE + " -> " + this.newPackage);
    }

    private static PrivateKey getPrivateKey() throws GeneralSecurityException {
        KeySpec spec = new PKCS8EncodedKeySpec(Base64.decode("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDWkxkE3sYLJLHtx2Lg2dglPj7NbOsd4v8GjKjovKjNa9N4bqcKp2zmDrsPmTVZ/9k+d6lD5+g9S2S45P6i0+ZW8eJnqBu/sjC1eMIEQ75Mchi4RvUhFYbwOKFOicK+OH+Ovs+PysPaHuMwyeqT0KfD3ErzUCINUAgHMuCAlxfuagUzWeamlOwss/KEoKRmyHqU2DsxCTpnNy4vZBLAbm1C8VgY3/4DgcwM1ETabN3DuCRYGUgBsyVkE0+/3pjJKHdI2/VnalQNgVTIu8oHueJHVTMRxGua92/e7MyOaefIotCOeCYglD+Zcn08BP5ymR2Z35uuOKCyF3+jHVtq/ukfAgEDAoIBAQCPDLtYlIQHbcvz2kHrO+VuKX8znfIT7KoEXcXwfcXeR+JQScSxxPNECdIKZiORVTt++nDX7/Ao3O3QmKnB4pmPS+xFGr0qdssjpdatgn7doWXQL04WDln1exY0W9cpev+0fzUKhy08FJd12/G34G/X6DH3isFeNVqvd0BVug/0RXWihnmONcUztAJ25E5YNqHadWSt+vU4pJOpvxDyE6ZXrBIpHBvlaZf8atJ7maf8iXfSZUzrqnx1O5zaTGRnGo7o/UdrfuLDfpVXnXBEHm+rk6QTq2ZKyZj6JZQ/K1LB+cXqZO9KG8oBSecXohQBeJYIDEikB9xHdsvelr1MoYR7AoGBAOrAmRccm5UnjAe/npdFGIVXkXaep7Ur9rqT4NaoSMSnDRim6Kii2lNoZ2szvvKYuxRNmvi1u60iRvQsLM10duqyG+FKdx+S5632ALWTKvdH97l3VYcRCrDYAyMYdotYavF8bcT9QKgYHoWHb18KLL27A4afIXmrVXCnWXp1e2GbAoGBAOn+9xk0qK83mecSq5edXgJ1lq2NaRVmSZYc5KKtCC8YYiQ0TSuIiRSpzJ3tR28wLtxO5lvqd72R8vBMPzS6CbY5RCj7tOBVW8bPTuwOYUN+AAN87csZvlmPsUsXMmBNQTYycvo0Keh/ZR0RIoFmN37SyagZC1ybj90t4cUCkUDNAoGBAJyAZg9oZ7jFCAUqabouEFjlC6RpxSNypHxileRwMIMaCLsZ8HBskYzwRPIif0xl0g2JEfsj0nNsL01yyIj4T0chZ+uG+hUMmnP5Vc5iHKTapSZPjloLXHXlV2y6+bI68fZS89io1cVlaa5aSj9cHdPSAlm/a6ZyOPXE5lGjp5ZnAoGBAJv/T2YjGx96ZpoMcmUTlAGjuckI8Lju27lomGxzWsoQQW14M3JbBg3GiGlI2kogHz2J7ufxpSkL90rdf3h8Bnl7gsX9I0A459nfifK0QNepVVeonodmfuZfy4dkzEAzgM7MTKbNcUWqQ2i2FwDuz6nh28VmB5MSX+jJQS4BtiszAoGAYyqt2RrdpGLZlaZyYlsFzalGIfTpWXPuj5ot63Ghwawb0xoN1qKJdYcbanvrblVhtKEsYKOkae96d1grNcf4Vbm3bMrPwHdIRf6pRS+x46mMBfuap1JoGcXESY4NwdsbpYo71PuBgykeNHaO2nq0BYcm/RyNFHuJZd+PFfOevDc=", 0));
        try {
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (InvalidKeySpecException e) {
            return KeyFactory.getInstance("DSA").generatePrivate(spec);
        }
    }

    @SuppressLint({"SdCardPath"})
    @TargetApi(19)
    private File getDir(boolean internal) {
        ArrayList<File> list = new ArrayList<>();
        if (internal) {
            list.add(Tools.getFilesDir());
            list.add(Tools.getCacheDir());
        }
        try {
            list.add(BaseActivity.context.getExternalCacheDir());
        } catch (Throwable e) {
            Log.e("Fail get path", e);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            try {
                for (File file : BaseActivity.context.getExternalCacheDirs()) {
                    list.add(file);
                }
            } catch (Throwable e2) {
                Log.e("Fail get path", e2);
            }
        }
        try {
            list.add(BaseActivity.context.getExternalFilesDir(null));
        } catch (Throwable e3) {
            Log.e("Fail get path", e3);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            try {
                for (File file2 : BaseActivity.context.getExternalFilesDirs(null)) {
                    list.add(file2);
                }
            } catch (Throwable e4) {
                Log.e("Fail get path", e4);
            }
        }
        try {
            list.add(Environment.getExternalStorageDirectory());
        } catch (Throwable e5) {
            Log.e("Fail get path", e5);
        }
        try {
            list.add(new File("/sdcard/Android/data/" + Tools.getPackageName() + "/files"));
            list.add(new File("/mnt/sdcard/Android/data/" + Tools.getPackageName() + "/files"));
            list.add(new File("/sdcard/Android/data/" + Tools.getPackageName() + "/cache"));
            list.add(new File("/mnt/sdcard/Android/data/" + Tools.getPackageName() + "/cache"));
        } catch (Throwable e6) {
            Log.e("Fail get path", e6);
        }
        list.add(new File("/sdcard"));
        list.add(new File("/mnt/sdcard"));
        list.add(new File("/data/local/tmp"));
        list.add(Tools.getCacheDir());
        list.add(Tools.getFilesDir());
        StringBuilder debug = new StringBuilder();
        debug.append("Failed getDir: ");
        for (File dir : list) {
            String reason = isGoodDir(dir);
            if (reason == null) {
                return dir;
            }
            if (dir != null) {
                debug.append(dir.getAbsolutePath());
                debug.append(": ");
                debug.append(reason);
                debug.append(ListManager.TEXT_SEPARATOR);
            }
        }
        throw new RuntimeException(debug.toString());
    }

    private String isGoodDir(File dir) {
        String str;
        if (dir == null) {
            Log.d("path: " + dir + " - null");
            return "is null";
        }
        dir.mkdirs();
        try {
            File test = new File(dir, TEMP_APK);
            test.delete();
            byte[] write = "We need test read/write to this file".getBytes();
            byte[] read = new byte[write.length];
            OutputStream os = new FileOutputStream(test);
            Tools.chmod(test, 384);
            os.write(write);
            os.close();
            InputStream is = new FileInputStream(test);
            int readed = is.read(read);
            is.close();
            if (readed != write.length) {
                str = "Length mismatch: " + readed + " != " + write.length;
            } else if (!Arrays.equals(read, write)) {
                str = "Data mismatch: '" + new String(read) + "' != '" + new String(write) + SearchMenuItem.HEX_UTF_8;
            } else {
                test.delete();
                Log.d("path: " + dir + " - success");
                str = null;
            }
            return str;
        } catch (IOException e) {
            Log.d("path: " + dir + " - fail: " + e.getMessage());
            return e.getMessage();
        }
    }

    private String getNewPackage() {
        String pkg;
        do {
            pkg = getNewString(0);
        } while (isPackageInstalledSafe(pkg));
        return pkg;
    }

    private static String getNewString(int mode2) {
        String str;
        char[] chars = mode2 == 2 ? new char[]{ParserNumbers.defaultDecimalSeparator, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'} : new char[]{ParserNumbers.defaultDecimalSeparator, IoLib.A, 'b', 'c', 'd', 'e', 'f', 'g', SearchMenuItem.HEX, 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', IoLib.R, 's', 't', 'u', 'v', IoLib.W, SearchMenuItem.XOR_MODE_SMALL, 'y', 'z'};
        StringBuilder sb = new StringBuilder();
        if (mode2 == 0) {
            sb.append("com.");
        }
        Random rnd = Tools.RANDOM;
        String ver = new StringBuilder().append((111.2f * 1) - 15.2f).toString();
        if (mode2 == 1) {
            str = Apk.INSTALLER_LABEL;
        } else {
            str = mode2 == 0 ? Apk.INSTALLER_PACKAGE : ver;
        }
        int max = str.length() - sb.length();
        int len = mode2 == 2 ? max : rnd.nextInt((max + 1) - 3) + 3;
        boolean lastDot = true;
        for (int i = 0; i < len; i++) {
            if (mode2 == 2 && ver.charAt(i) == '.') {
                sb.append(ParserNumbers.defaultDecimalSeparator);
            } else {
                int offset = (mode2 == 2 || lastDot) ? 1 : 0;
                int cnt = chars.length;
                if (i == 0 && mode2 != 1) {
                    offset = 2;
                }
                if (i == len - 1 && offset == 0) {
                    offset = 1;
                }
                int pos = offset + rnd.nextInt(cnt - offset);
                char ch = chars[pos];
                if (mode2 == 1) {
                    if (pos == 0) {
                        ch = ' ';
                    } else if (lastDot) {
                        ch = Character.toUpperCase(ch);
                    }
                }
                sb.append(ch);
                lastDot = pos == 0;
            }
        }
        return sb.toString();
    }

    private static void initProgressBar() {
        Button cancel = weakCancel.get();
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() { // from class: android.ext.Installer.8
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    Alert.show(Alert.create(BaseActivity.context).setMessage(Re.s(R.string.install_cancel_prompt)).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // from class: android.ext.Installer.8.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            Installer.cancelInstall(true);
                        }
                    }).setNegativeButton(R.string.no, (DialogInterface.OnClickListener) null).setCancelable(false), BaseActivity.instance);
                }
            });
        }
        ProgressBar bar = weakBar.get();
        if (bar != null) {
            bar.setIndeterminate(false);
        }
        updater.run();
    }

    static void updateProgress() {
        int step = state.step + wait;
        if (step > 1000) {
            step = 1000;
        }
        ProgressBar bar = weakBar.get();
        if (bar != null) {
            bar.setMax(1000);
            bar.setProgress(step);
        }
        TextView barText = weakBarText.get();
        if (barText != null) {
            barText.setText(Tools.stringFormat("%.1f %%", Double.valueOf((100.0d * ((double) step)) / 1000.0d)));
        }
        if (wait < 199) {
            wait++;
        }
        if (state.step == 600) {
            if (wait == 60) {
                TextView msg = weakMsg.get();
                String apk = state.apk;
                if (msg != null && apk != null) {
                    msg.setText(String.valueOf(Re.s(R.string.installing)) + "\n\n" + Tools.stripColon(R.string.manual_install) + ":\n" + apk);
                }
            }
            if (wait == 198 && instance != null) {
                promptRetry(String.valueOf(Re.s(R.string.failed_install)) + "\n\n" + Tools.stripColon(R.string.manual_install) + ":\n" + instance.apk.getAbsolutePath(), 1, -123, instance.getInstallIntent());
            }
        }
    }

    private static void setStep(int step) {
        if (state.step != step) {
            wait = 0;
        }
        state.step = step;
    }

    private static boolean isPackageInstalledSafe(String pkg) {
        try {
            return Tools.isPackageInstalled(pkg);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }

    private boolean _install() throws Throwable {
        if (isPackageInstalledSafe(this.newPackage)) {
            setStep(800);
        }
        if (state.step < 600) {
            setStep(0);
            extractEntries();
            setStep(200);
            fixEntries();
            setStep(400);
            copyApk();
            setStep(600);
        }
        if (state.step < 800) {
            setStep(600);
            deleteFiles();
            State state2 = state;
            state2.dbg = String.valueOf(state2.dbg) + instApk();
            setStep(800);
        }
        if (!isPackageInstalledSafe(this.newPackage)) {
            throw new InstallException("package not installed: " + this.newPackage + '\n' + state.dbg);
        }
        if (state.step < 1000) {
            setStep(800);
            setInstaller();
            try {
                runNewPackage();
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
            setStep(1000);
        }
        try {
            uninstallOldPackage();
            return true;
        } catch (Throwable e2) {
            Log.badImplementation(e2);
            return true;
        }
    }

    static class InstallException extends RuntimeException {
        private static final long serialVersionUID = 1;

        public InstallException(String detailMessage) {
            super(detailMessage);
        }
    }

    private void setInstaller() {
        if (Build.VERSION.SDK_INT >= 11) {
            try {
                PackageManager pm = Tools.getPackageManager();
                pm.setInstallerPackageName(this.newPackage, null);
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
    }

    private void runNewPackage() {
        Intent intent = Tools.getStartIntent(BaseActivity.context, this.newPackage, BaseActivity.getClassName().replace(Tools.getPackageName(), this.newPackage));
        if (!Uninstaller.isInstalled()) {
            Log.d("removeInstaller: just installer");
            intent.putExtra(String.valueOf(Apk.INSTALLER_PACKAGE) + INTENT_FROM_INSTALLER, true);
        }
        BaseActivity.context.startActivity(intent);
    }

    @TargetApi(14)
    public static Intent[] getUninstallIntent(String pkg) {
        boolean newAPI = Build.VERSION.SDK_INT >= 14;
        Intent[] arr = new Intent[newAPI ? 2 : 1];
        int i = 0;
        if (newAPI) {
            Intent intent = new Intent("android.intent.action.UNINSTALL_PACKAGE");
            intent.setData(Uri.fromParts("package", pkg, null));
            intent.putExtra("android.intent.extra.RETURN_RESULT", true);
            intent.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", true);
            int i2 = 0 + 1;
            arr[0] = intent;
            i = i2;
        }
        Intent intent2 = new Intent("android.intent.action.DELETE");
        intent2.setData(Uri.fromParts("package", pkg, null));
        int i3 = i + 1;
        arr[i] = intent2;
        return arr;
    }

    @TargetApi(14)
    private void uninstallOldPackage() throws InterruptedException, IOException {
        if (!Uninstaller.isInstalled()) {
            Alert.show(Alert.create(BaseActivity.context).setMessage(Re.s(R.string.need_uninstall)).setPositiveButton(Re.s(R.string.ok), new DialogInterface.OnClickListener() { // from class: android.ext.Installer.9
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    Uninstaller.uninstallPackageWithIntent(Apk.INSTALLER_PACKAGE);
                }
            }));
        } else {
            Alert.show(Alert.create(BaseActivity.context).setMessage(Re.s(R.string.install_finished)).setPositiveButton(Re.s(R.string.ok), new ExitListener(1600)));
        }
    }

    private static Intent grant(Intent intent, boolean grant) {
        if (grant) {
            Context context = Tools.getContext();
            try {
                List<ResolveInfo> resInfoList = Tools.getPackageManager().queryIntentActivities(intent, 65536);
                for (ResolveInfo resolveInfo : resInfoList) {
                    try {
                        String packageName = resolveInfo.activityInfo.packageName;
                        context.grantUriPermission(packageName, intent.getData(), 1);
                    } catch (Throwable e) {
                        Log.w("Failed grant 0 " + intent + ListManager.TEXT_SEPARATOR + resolveInfo, e);
                    }
                }
            } catch (Throwable e2) {
                Log.w("Failed grant 1 " + intent, e2);
            }
            try {
                context.grantUriPermission("com.android.packageinstaller", intent.getData(), 1);
            } catch (Throwable e3) {
                Log.w("Failed grant 2 " + intent, e3);
            }
        }
        return intent;
    }

    private Intent[] getInstallIntent() {
        return getInstallIntent(this.apk);
    }

    @TargetApi(14)
    public static Intent[] getInstallIntent(File apk) {
        int i;
        boolean newAPI = Build.VERSION.SDK_INT >= 14;
        Intent[] arr = new Intent[newAPI ? 4 : 2];
        boolean Q = Build.VERSION.SDK_INT >= 24;
        boolean grant = sGrant;
        int j = 0;
        int i2 = 0;
        while (j < 2) {
            boolean file = (j == 0) ^ Q;
            Uri uri = file ? Uri.fromFile(apk) : FileProvider.getUriForFile(Tools.getContext(), Apk.PACKAGE, apk);
            if (newAPI) {
                Intent intent = new Intent("android.intent.action.INSTALL_PACKAGE");
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.addFlags(1);
                intent.putExtra("android.intent.extra.NOT_UNKNOWN_SOURCE", true);
                intent.putExtra("android.intent.extra.RETURN_RESULT", true);
                i = i2 + 1;
                arr[i2] = grant(intent, (grant || file) ? false : true);
            } else {
                i = i2;
            }
            Intent intent2 = new Intent("android.intent.action.VIEW");
            intent2.setDataAndType(uri, "application/vnd.android.package-archive");
            intent2.addFlags(1);
            i2 = i + 1;
            arr[i] = grant(intent2, (grant || file) ? false : true);
            j++;
        }
        sGrant = true;
        return arr;
    }

    private String instApk() throws InterruptedException, IOException {
        String[] pathes;
        String absPath = this.apk.getAbsolutePath();
        if (absPath.contains("/emulated/0")) {
            pathes = new String[]{absPath.replace("/emulated/0", "/emulated/legacy"), absPath};
        } else {
            pathes = new String[]{absPath};
        }
        for (String path : pathes) {
            try {
                Tools.chmod(new File(path), 493);
            } catch (Throwable e) {
                Log.w("Failed allow execute 1: " + path, e);
            }
            try {
                Tools.chmod(path, "0755");
            } catch (Throwable e2) {
                Log.w("Failed allow execute 2: " + path, e2);
            }
        }
        String dbg = "Install:";
        for (String str : pathes) {
            try {
                dbg = String.valueOf(dbg) + '\n' + RootDetector.runCmd(String.valueOf(Tools.removeNewLinesChars("h{hf#sp#lqvwdoo#0i#")) + str, 45);
            } catch (Throwable e3) {
                Log.e("run cmd fail", e3);
                dbg = String.valueOf(dbg) + '\n' + e3.getMessage();
            }
            if (isPackageInstalledSafe(this.newPackage)) {
                break;
            }
        }
        if (!isPackageInstalledSafe(this.newPackage)) {
            state.manualInstall = true;
            Intent[] intents = getInstallIntent();
            nextInstallIntent = 0;
            for (Intent intent : intents) {
                nextInstallIntent++;
                dbg = String.valueOf(dbg) + "\nManual: " + intent;
                try {
                    waitForResult(intent, 1);
                    break;
                } catch (Throwable e4) {
                    dbg = String.valueOf(dbg) + '\n' + e4.getMessage();
                }
            }
        }
        return dbg;
    }

    private void waitForResult(Intent intent, final int requestCode) {
        state.waitResult |= requestCode;
        Handler handler = ThreadManager.getHandlerUiThread();
        Runnable fail = new Runnable() { // from class: android.ext.Installer.10
            @Override // java.lang.Runnable
            public void run() {
                Installer.state.waitResult &= requestCode ^ (-1);
            }
        };
        handler.postDelayed(fail, 300000L);
        BaseActivity.instance.startActivityForResult(intent, requestCode);
        while ((state.waitResult & requestCode) == requestCode) {
            try {
                Thread.sleep(500L);
                if (requestCode == 1 && isPackageInstalledSafe(this.newPackage)) {
                    state.waitResult &= requestCode ^ (-1);
                }
            } catch (InterruptedException e) {
                Log.e("Interrupted wait", e);
            }
        }
        handler.removeCallbacks(fail);
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Installer.11
            @Override // java.lang.Runnable
            public void run() {
                Installer.this.dismissDialog();
            }
        });
    }

    void dismissDialog() {
        if (this.dialog != null) {
            try {
                Tools.dismiss(this.dialog);
                this.dialog = null;
            } catch (Throwable e) {
                Log.w("Exception on dismiss", e);
            }
        }
    }

    private void fixEntry(ZipEntry ze, ZipInputStream zis, ZipOutputStream zos) throws IOException {
        int found = getIndex(ze);
        ZipEntry ze2 = new ZipEntry(ze);
        if (found != -1) {
            updateEntry(ze2, found);
            if (this.list[found] == null) {
                Log.w("List: " + found + " is null!");
                return;
            } else {
                this.list[found].putHash = putEntry(ze2, zos, found);
                return;
            }
        }
        putEntry(ze2, zis, zos);
    }

    private static int getIndex(ZipEntry ze) {
        String name = ze.getName();
        for (int i = 0; i < FIX_FILES.length; i++) {
            if (FIX_FILES[i].equals(name)) {
                int found = i;
                return found;
            }
        }
        return -1;
    }

    private String putEntry(ZipEntry ze, ZipOutputStream zos, int index) throws IOException {
        zos.putNextEntry(ze);
        this.sha1.reset();
        InputStream is = new DigestInputStream(getInputStream(index), this.sha1);
        long write = 0;
        try {
            byte[] buffer = getBuf();
            while (true) {
                int count = is.read(buffer);
                if (count != -1) {
                    if (count != 0) {
                        zos.write(buffer, 0, count);
                        write += (long) count;
                    }
                } else {
                    is.close();
                    String hash2 = Base64.encodeToString(this.sha1.digest(), 2);
                    Log.d("put " + ze.getName() + ": " + hash2 + ListManager.TEXT_SEPARATOR + write);
                    zos.flush();
                    zos.closeEntry();
                    return hash2;
                }
            }
        } catch (Throwable th) {
            is.close();
            Log.d("put " + ze.getName() + ": " + Base64.encodeToString(this.sha1.digest(), 2) + ListManager.TEXT_SEPARATOR + write);
            throw th;
        }
    }

    private byte[] getBuf() {
        byte[] buf = this.buf;
        if (buf == null) {
            byte[] buf2 = new byte[65536];
            this.buf = buf2;
            return buf2;
        }
        return buf;
    }

    private InputStream getInputStream(int index) throws IOException {
        if (useMemCache[index]) {
            if (memCache == null || memCache[index] == null) {
                throw new InstallException("memCache is null 1: " + index + ' ' + memCache);
            }
            InputStream is = new ByteArrayInputStream(memCache[index]);
            return is;
        }
        Log.d("getInputStream: " + index);
        InputStream is2 = new RAFInputStream(getRAF(index), null);
        return is2;
    }

    private long getLength(int index) throws IOException {
        if (useMemCache[index]) {
            if (memCache == null || memCache[index] == null) {
                throw new InstallException("memCache is null 2: " + index + ' ' + memCache);
            }
            long ret = memCache[index].length;
            return ret;
        }
        long ret2 = getRAF(index).length();
        return ret2;
    }

    private OutputStream getOutputStream(final int index) throws IOException {
        int size;
        OutputStream os = null;
        boolean useMem = useMemCache[index];
        if (!useMem) {
            try {
                Log.d("getOutputStream: " + index);
                os = new RAFOutputStream(getRAF(index), null);
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
            if (os == null) {
                Log.d("Use mem cache for " + index);
                useMem = true;
                useMemCache[index] = true;
            }
        }
        if (useMem) {
            if (index == 1) {
                size = 9000;
            } else if (index == 3 || index == 4) {
                size = 75000;
            } else {
                size = index == 5 ? 2000 : 8192;
            }
            return new ByteArrayOutputStream(size) { // from class: android.ext.Installer.12
                @Override // java.io.ByteArrayOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
                public void close() throws IOException {
                    super.close();
                    byte[][] cache = Installer.memCache;
                    if (cache != null) {
                        byte[] arr = toByteArray();
                        if (arr == null) {
                            Log.badImplementation(new RuntimeException("toByteArray return null"));
                        }
                        cache[index] = arr;
                    }
                }
            };
        }
        return os;
    }

    private void checkHash(ZipEntry ze, int index, String hash2, String readHash) throws IOException {
        this.sha1.reset();
        InputStream is = new DigestInputStream(getInputStream(index), this.sha1);
        try {
            byte[] buffer = getBuf();
            do {
            } while (is.read(buffer) != -1);
            is.close();
            String check = Base64.encodeToString(this.sha1.digest(), 2);
            Log.d("check hash for " + ze.getName() + " [" + index + "]: " + check + " : " + readHash + " -> " + hash2);
            if (!hash2.equals(check)) {
                throw new InstallException("check hash for " + ze.getName() + " [" + index + "]: " + check + " != " + readHash + " -> " + hash2);
            }
        } catch (Throwable th) {
            is.close();
            Log.d("check hash for " + ze.getName() + " [" + index + "]: " + Base64.encodeToString(this.sha1.digest(), 2) + " : " + readHash + " -> " + hash2);
            throw th;
        }
    }

    private void putEntry(ZipEntry ze, ZipInputStream zis, ZipOutputStream zos) throws IOException {
        if (ze.getMethod() != 0) {
            ze.setCompressedSize(-1L);
        }
        if (mode != 0) {
            String name = ze.getName();
            String replace = null;
            if (chunk3a.equals(name)) {
                replace = mode == 1 ? lib3a : lib6a;
            }
            if (chunk3x.equals(name)) {
                replace = mode == 1 ? lib3x : lib6x;
            }
            if (replace != null) {
                try {
                    Field f = ZipEntry.class.getDeclaredField("name");
                    f.setAccessible(true);
                    f.set(ze, replace);
                } catch (Throwable e) {
                    Log.badImplementation(e);
                }
            }
        }
        zos.putNextEntry(ze);
        byte[] buffer = getBuf();
        while (true) {
            int count = zis.read(buffer);
            if (count != -1) {
                zos.write(buffer, 0, count);
            } else {
                zos.flush();
                zos.closeEntry();
                return;
            }
        }
    }

    private String extractEntry(ZipEntry ze, ZipInputStream zis, int index) throws IOException {
        this.sha1.reset();
        OutputStream os = new DigestOutputStream(getOutputStream(index), this.sha1);
        this.sha1_2.reset();
        InputStream is = new DigestInputStream(zis, this.sha1_2);
        try {
            byte[] buffer = getBuf();
            while (true) {
                int count = is.read(buffer);
                if (count != -1) {
                    os.write(buffer, 0, count);
                } else {
                    os.flush();
                    os.close();
                    String hash2 = Base64.encodeToString(this.sha1.digest(), 2);
                    String readHash = Base64.encodeToString(this.sha1_2.digest(), 2);
                    Log.d("extract " + ze.getName() + ": " + readHash + " -> " + hash2);
                    checkHash(ze, index, hash2, readHash);
                    return hash2;
                }
            }
        } catch (Throwable th) {
            os.close();
            Log.d("extract " + ze.getName() + ": " + Base64.encodeToString(this.sha1_2.digest(), 2) + " -> " + Base64.encodeToString(this.sha1.digest(), 2));
            throw th;
        }
    }

    private void generateSignatureFile(Manifest manifest, OutputStream out) throws IOException {
        out.write("Signature-Version: 1.0\r\n".getBytes());
        out.write("Created-By: 1.0 (Android SignApk)\r\n".getBytes());
        this.sha1.reset();
        PrintStream print = new PrintStream((OutputStream) new DigestOutputStream(new NullOutputStream(null), this.sha1), true, "UTF-8");
        manifest.write(print);
        print.flush();
        out.write(("SHA1-Digest-Manifest: " + Base64.encodeToString(this.sha1.digest(), 2) + "\r\n\r\n").getBytes());
        Map<String, Attributes> entries = manifest.getEntries();
        for (Map.Entry<String, Attributes> entry : entries.entrySet()) {
            String nameEntry = "Name: " + entry.getKey() + "\r\n";
            print.print(nameEntry);
            for (Map.Entry<Object, Object> att : entry.getValue().entrySet()) {
                print.print(att.getKey() + ": " + att.getValue() + "\r\n");
            }
            print.print("\r\n");
            print.flush();
            out.write(nameEntry.getBytes());
            out.write(("SHA1-Digest: " + Base64.encodeToString(this.sha1.digest(), 2) + "\r\n\r\n").getBytes());
        }
        out.flush();
    }

    private String getSHA1(InputStream is) throws IOException {
        this.sha1.reset();
        DigestInputStream dis = new DigestInputStream(is, this.sha1);
        try {
            byte[] buffer = getBuf();
            do {
            } while (dis.read(buffer) != -1);
            dis.close();
            return Base64.encodeToString(this.sha1.digest(), 2);
        } catch (Throwable th) {
            dis.close();
            throw th;
        }
    }

    private void writeSignatureBlock(OutputStream out) throws GeneralSecurityException, IOException {
        byte[] signatureFileBytes = new byte[(int) getLength(4)];
        InputStream is = getInputStream(4);
        int read = 0;
        try {
            int len = signatureFileBytes.length;
            int i = 0;
            while (true) {
                if (i >= 10 || read >= len) {
                    break;
                }
                int readed = is.read(signatureFileBytes, read, len - read);
                if (readed < 0) {
                    Log.w("Failed read cert 1: " + readed + ' ' + read + ' ' + len);
                    break;
                } else {
                    read += readed;
                    i++;
                }
            }
            if (read < len) {
                Log.w("Failed read cert 2: " + read + ' ' + len);
            }
            is.close();
            ZipSignature signature = new ZipSignature();
            signature.initSign(getPrivateKey());
            signature.update(signatureFileBytes);
            byte[] signatureBytes = signature.sign();
            out.write(Base64.decode("MIIGrgYJKoZIhvcNAQcCoIIGnzCCBpsCAQExCzAJBgUrDgMCGgUAMAsGCSqGSIb3DQEHAaCCBKwwggSoMIIDkKADAgECAgkAk26svgfyAd8wDQYJKoZIhvcNAQEFBQAwgZQxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1Nb3VudGFpbiBWaWV3MRAwDgYDVQQKEwdBbmRyb2lkMRAwDgYDVQQLEwdBbmRyb2lkMRAwDgYDVQQDEwdBbmRyb2lkMSIwIAYJKoZIhvcNAQkBFhNhbmRyb2lkQGFuZHJvaWQuY29tMB4XDTA4MDIyOTAxMzM0NloXDTM1MDcxNzAxMzM0NlowgZQxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1Nb3VudGFpbiBWaWV3MRAwDgYDVQQKEwdBbmRyb2lkMRAwDgYDVQQLEwdBbmRyb2lkMRAwDgYDVQQDEwdBbmRyb2lkMSIwIAYJKoZIhvcNAQkBFhNhbmRyb2lkQGFuZHJvaWQuY29tMIIBIDANBgkqhkiG9w0BAQEFAAOCAQ0AMIIBCAKCAQEA1pMZBN7GCySx7cdi4NnYJT4+zWzrHeL/Boyo6LyozWvTeG6nCqds5g67D5k1Wf/ZPnepQ+foPUtkuOT+otPmVvHiZ6gbv7IwtXjCBEO+THIYuEb1IRWG8DihTonCvjh/jr7Pj8rD2h7jMMnqk9Cnw9xK81AiDVAIBzLggJcX7moFM1nmppTsLLPyhKCkZsh6lNg7MQk6ZzcuL2QSwG5tQvFYGN/+A4HMDNRE2mzdw7gkWBlIAbMlZBNPv96YySh3SNv1Z2pUDYFUyLvKB7niR1UzEcRrmvdv3uzMjmnnyKLQjngmIJQ/mXJ9PAT+cpkdmd+brjigshd/ox1bav7pHwIBA6OB/DCB+TAdBgNVHQ4EFgQUSFkAVj0nLEauEYYFpHQZrAnKjBEwgckGA1UdIwSBwTCBvoAUSFkAVj0nLEauEYYFpHQZrAnKjBGhgZqkgZcwgZQxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1Nb3VudGFpbiBWaWV3MRAwDgYDVQQKEwdBbmRyb2lkMRAwDgYDVQQLEwdBbmRyb2lkMRAwDgYDVQQDEwdBbmRyb2lkMSIwIAYJKoZIhvcNAQkBFhNhbmRyb2lkQGFuZHJvaWQuY29tggkAk26svgfyAd8wDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOCAQEAeq+WjOtQxEEFURjQ2quvAVuKdlonpxWiwrRPIhQV/9rOAwlav6Qt9wcIcmwgaeXDbt2uBAC+KUUsCEvCfrahfqydvhgsIE6xUxH0Vdgktlbb5NwiQJEtdYb+iJUdAaj+ta5aQmBTXfg0MQUkIkaMNuIsKl75lNYd1zBq5Mn2lRujwS8dGRTdxh8aYtot+Cf2A/6lYDssVA29fAGcNrqymkJxwRffUjzbxfOBekng76YMvX90F356Txk9Q/QiB3JmbkxNg+G9WoYIfPNPLewh4kXKbCuwFuaDY4BQ0sQw7qfCahxJ03YKWKt/GoLMk4tIMThDJL0EAfoSFjpQVw5oTTGCAcowggHGAgEBMIGiMIGUMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNTW91bnRhaW4gVmlldzEQMA4GA1UEChMHQW5kcm9pZDEQMA4GA1UECxMHQW5kcm9pZDEQMA4GA1UEAxMHQW5kcm9pZDEiMCAGCSqGSIb3DQEJARYTYW5kcm9pZEBhbmRyb2lkLmNvbQIJAJNurL4H8gHfMAkGBSsOAwIaBQAwDQYJKoZIhvcNAQEBBQAEggEA", 0));
            out.write(signatureBytes);
            out.flush();
        } catch (Throwable th) {
            is.close();
            throw th;
        }
    }

    private static byte[] intToByteArray(byte[] ret, int offset, int a2) {
        if (ret == null) {
            ret = new byte[offset + 4];
        }
        for (int i = 0; i < 4; i++) {
            ret[offset + i] = (byte) ((a2 >> (i * 8)) & 255);
        }
        return ret;
    }

    private Manifest getManifest(ZipEntry ze) throws IOException {
        try {
            this.sha1.reset();
            InputStream is = new DigestInputStream(getInputStream(3), this.sha1);
            try {
                Manifest m = new Manifest(is);
                is.close();
                Log.d("Manifest: " + Base64.encodeToString(this.sha1.digest(), 2) + " -> " + this.list[3]);
                return m;
            } catch (Throwable th) {
                is.close();
                throw th;
            }
        } catch (IOException e) {
            throw new IOException("Failed get manifest: " + Base64.encodeToString(this.sha1.digest(), 2) + " -> " + this.list[3], e);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0031 A[DONT_GENERATE] */
    /* JADX WARN: Removed duplicated region for block: B:53:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void fixEntry(java.util.zip.ZipEntry r9, int r10) throws java.security.GeneralSecurityException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 212
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.ext.Installer.fixEntry(java.util.zip.ZipEntry, int):void");
    }

    private static class RAFInputStream extends InputStream {
        private final RandomAccessFile raf;

        private RAFInputStream(RandomAccessFile raf) throws IOException {
            this.raf = raf;
            raf.seek(0L);
        }

        /* synthetic */ RAFInputStream(RandomAccessFile randomAccessFile, RAFInputStream rAFInputStream) throws IOException {
            this(randomAccessFile);
        }

        @Override // java.io.InputStream
        public int read(byte[] b) throws IOException {
            return this.raf.read(b);
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            return this.raf.read(b, off, len);
        }

        @Override // java.io.InputStream
        public long skip(long n) throws IOException {
            RandomAccessFile raf = this.raf;
            long offset = raf.getFilePointer();
            raf.seek(offset + n);
            return n;
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            int rest = (int) (this.raf.length() - this.raf.getFilePointer());
            if (rest > 0) {
                return rest;
            }
            return 0;
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
        }

        @Override // java.io.InputStream
        public synchronized void mark(int readlimit) {
        }

        @Override // java.io.InputStream
        public synchronized void reset() throws IOException {
        }

        @Override // java.io.InputStream
        public boolean markSupported() {
            return false;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            return this.raf.read();
        }
    }

    private static class RAFOutputStream extends OutputStream {
        private final RandomAccessFile raf;

        private RAFOutputStream(RandomAccessFile raf) throws IOException {
            this.raf = raf;
            raf.seek(0L);
            raf.setLength(0L);
        }

        /* synthetic */ RAFOutputStream(RandomAccessFile randomAccessFile, RAFOutputStream rAFOutputStream) throws IOException {
            this(randomAccessFile);
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            this.raf.write(b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b) throws IOException {
            this.raf.write(b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            this.raf.write(b, off, len);
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
        }
    }

    private class RandomAccessData {
        private ByteBuffer bb;
        private RandomAccessFile raf;
        private final boolean useMem;

        private RandomAccessData(int index) throws IOException {
            this.raf = null;
            this.bb = null;
            this.useMem = Installer.useMemCache[index];
            if (this.useMem) {
                if (Installer.memCache == null || Installer.memCache[index] == null) {
                    throw new InstallException("memCache is null 3: " + index + ' ' + Installer.memCache);
                }
                this.bb = ByteBuffer.wrap(Installer.memCache[index]);
                return;
            }
            this.raf = Installer.this.getRAF(index);
        }

        /* synthetic */ RandomAccessData(Installer installer, int i, RandomAccessData randomAccessData) throws IOException {
            this(i);
        }

        public void seek(long offset) throws IOException {
            if (this.useMem) {
                this.bb.position((int) offset);
            } else {
                this.raf.seek(offset);
            }
        }

        public void skip(int count) throws IOException {
            if (this.useMem) {
                this.bb.position(this.bb.position() + count);
            } else {
                this.raf.skipBytes(count);
            }
        }

        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            if (this.useMem) {
                int ret = Math.min(byteCount, this.bb.remaining());
                if (ret != 0) {
                    this.bb.get(buffer, byteOffset, ret);
                    return ret;
                }
                return -1;
            }
            return this.raf.read(buffer, byteOffset, byteCount);
        }

        public void write(byte[] buffer) throws IOException {
            if (this.useMem) {
                this.bb.put(buffer);
            } else {
                this.raf.write(buffer);
            }
        }

        public void write(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            if (this.useMem) {
                this.bb.put(buffer, byteOffset, byteCount);
            } else {
                this.raf.write(buffer, byteOffset, byteCount);
            }
        }

        public int read(byte[] buffer) throws IOException {
            return read(buffer, 0, buffer.length);
        }

        public void close() throws IOException {
        }
    }

    private static class Chunk {
        short headerSize;
        int size;
        short type;

        Chunk(ByteBuffer bb) throws IOException {
            bb.position(0);
            this.type = bb.getShort();
            this.headerSize = bb.getShort();
            this.size = bb.getInt();
        }
    }

    private void fixResources(RandomAccessData raf) throws IOException {
        byte[] buf = new byte[8];
        ByteBuffer bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
        raf.seek(0L);
        while (raf.read(buf) != -1) {
            Chunk chunk = new Chunk(bb);
            if (chunk.type == 512) {
                raf.skip(4);
                byte[] name = Arrays.copyOf(this.newPackage.getBytes(ParserNumbers.getCharset(true)), 60);
                raf.write(name);
                return;
            } else if (chunk.type == 2) {
                raf.skip(chunk.headerSize - 8);
            } else {
                raf.skip(chunk.size - 8);
            }
        }
    }

    public static final String hashResources(InputStream is, MessageDigest md) {
        int read;
        try {
            byte[] buffer = new byte[8192];
            ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
            while (read(is, buffer, 0, 8) != -1) {
                md.update(buffer, 0, 8);
                Chunk chunk = new Chunk(bb);
                if (chunk.type == 512) {
                    byte[] name = new byte[260];
                    read(is, name, 0, 4);
                    md.update(name);
                    read(is, name, 4, 256);
                    read = ((chunk.size - 8) - 4) - 256;
                } else if (chunk.type == 2) {
                    read = chunk.headerSize - 8;
                } else {
                    read = chunk.size - 8;
                }
                hash(is, md, buffer, read);
            }
            return Hash.toString(md);
        } catch (Throwable e) {
            Log.e("hash fail", e);
            return e.toString();
        }
    }

    private static int hash(InputStream is, MessageDigest md, byte[] buffer, int count) throws IOException {
        while (count > 0) {
            int len = count > buffer.length ? buffer.length : count;
            int actual = is.read(buffer, 0, len);
            if (actual <= 0) {
                break;
            }
            count -= actual;
            md.update(buffer, 0, actual);
        }
        return count;
    }

    private void fixDex(RandomAccessData raf) throws IOException {
        byte[] buf = new byte[8];
        ByteBuffer bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
        raf.seek(56L);
        raf.read(buf, 0, 8);
        bb.position(0);
        int string_ids_size = bb.getInt();
        int string_ids_off = bb.getInt() + 18800;
        int string_ids_size2 = string_ids_size - 16304;
        byte[] string_ids_buf = new byte[string_ids_size2 * 4];
        raf.seek(string_ids_off);
        raf.read(string_ids_buf);
        ByteBuffer bb2 = ByteBuffer.wrap(string_ids_buf).order(ByteOrder.LITTLE_ENDIAN);
        int[] string_ids = new int[string_ids_size2];
        for (int i = 0; i < string_ids_size2; i++) {
            string_ids[i] = bb2.getInt();
        }
        int size = string_ids[string_ids.length - 1] - string_ids[0];
        byte[] string_data_items = new byte[size];
        raf.seek(string_ids[0]);
        raf.read(string_data_items);
        int offset = Tools.indexOf(string_data_items, "\u0017Lcar$".getBytes(ParserNumbers.getCharset(false)));
        if (offset < 0) {
            Log.d("fixDex fail 1");
            return;
        }
        byte[] put = (String.valueOf(this.newLabel) + SearchMenuItem.TILDE).getBytes(ParserNumbers.getCharset(false));
        System.arraycopy(put, 0, string_data_items, offset + 1 + Apk.LABEL_DELIMITER.length(), put.length);
        int first = Arrays.binarySearch(string_ids, string_ids[0] + offset);
        if (first < 0) {
            Log.d("fixDex fail 2");
            return;
        }
        int first2 = first + 1;
        int diff = Apk.INSTALLER_PACKAGE.length() - this.newPackage.length();
        Class<?>[] cls = getCls();
        for (int i2 = 0; i2 < cls.length; i2++) {
            String name = cls[i2].getName().replace(Apk.PACKAGE, this.newPackage);
            int len = name.length() + 2;
            String sig = name.replace(ParserNumbers.defaultDecimalSeparator, '/');
            int pos = first2 + (i2 * 2);
            bb2.putInt((pos + 1) * 4, string_ids[pos + 1] - diff);
            byte[] put2 = (((char) len) + 'L' + sig + ";\u0000" + ((char) ((diff * 2) + len)) + 'L' + sig + '=' + new String(new char[diff * 2]).replace((char) 0, '=') + (char) 0).getBytes(ParserNumbers.getCharset(false));
            System.arraycopy(put2, 0, string_data_items, string_ids[pos] - string_ids[0], put2.length);
        }
        byte[] put3 = (String.valueOf(this.newPackage.replace(ParserNumbers.defaultDecimalSeparator, '/')) + new String(new char[diff]).replace((char) 0, SearchMenuItem.TILDE)).getBytes(ParserNumbers.getCharset(false));
        System.arraycopy(put3, 0, string_data_items, (string_ids[(cls.length * 2) + first2] - string_ids[0]) + 2, put3.length);
        raf.seek((first2 * 4) + string_ids_off);
        raf.write(string_ids_buf, first2 * 4, cls.length * 8);
        raf.seek(string_ids[first2 - 1]);
        raf.write(string_data_items, string_ids[first2 - 1] - string_ids[0], string_ids[((cls.length * 2) + first2) + 1] - string_ids[first2 - 1]);
        byte[] buffer = getBuf();
        raf.seek(32L);
        this.sha1.reset();
        while (true) {
            int count = raf.read(buffer);
            if (count == -1) {
                break;
            } else {
                this.sha1.update(buffer, 0, count);
            }
        }
        raf.seek(12L);
        raf.write(this.sha1.digest());
        Adler32 a32 = new Adler32();
        raf.seek(12L);
        while (true) {
            int count2 = raf.read(buffer);
            if (count2 != -1) {
                a32.update(buffer, 0, count2);
            } else {
                raf.seek(8L);
                bb2.putInt(0, (int) a32.getValue());
                raf.write(bb2.array(), 0, 4);
                return;
            }
        }
    }

    private static Class<?>[] getCls() {
        return new Class[]{ActivityMain.class, AnalyticsService.class, Application.class, BuildConfig.class, FileProvider.class, Instrumentation.class, MainActivity.class, R.class, Receiver.class};
    }

    public static final String hashDex(InputStream is, MessageDigest md) {
        try {
            byte[] buffer = new byte[8192];
            ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
            int pos = 0 + read(is, buffer, 0, 64);
            if (pos >= 64) {
                md.update(buffer, 56, 8);
            }
            bb.position(56);
            int string_ids_size = bb.getInt();
            int string_ids_off = bb.getInt();
            int string_ids_size2 = string_ids_size - 16304;
            if (string_ids_size2 <= 0) {
                throw new Exception("Invalid string_ids_size2: " + string_ids_size2);
            }
            int pos2 = pos + hash(is, md, buffer, (string_ids_off + 18800) - pos);
            byte[] string_ids_buf = new byte[string_ids_size2 * 4];
            int tmp = read(is, string_ids_buf);
            if (tmp <= 0) {
                throw new Exception("Failed to read string_ids_buf");
            }
            int pos3 = pos2 + tmp;
            ByteBuffer bb2 = ByteBuffer.wrap(string_ids_buf).order(ByteOrder.LITTLE_ENDIAN);
            int start = 0;
            int end = 0;
            if (string_ids_buf.length >= 4) {
                start = bb2.getInt(0);
            }
            if (string_ids_buf.length >= (string_ids_size2 * 4)) {
                int endIndex = (string_ids_size2 - 1) * 4;
                if (endIndex >= 0 && endIndex + 4 <= string_ids_buf.length) {
                    end = bb2.getInt(endIndex);
                }
            }
            Log.d("DBG: " + tmp + ListManager.TEXT_SEPARATOR + string_ids_buf.length + ", " + start + ListManager.TEXT_SEPARATOR + end);
            int size = end - start;
            if (size <= 0 || size > 1024 * 1024 * 10) { // Sanity check, max 10MB
                throw new Exception("Invalid size: " + size);
            }
            byte[] string_data_items = new byte[size];
            int pos4 = pos3 + hash(is, md, buffer, start - pos3) + read(is, string_data_items);
            int offset = Tools.indexOf(string_data_items, "\u0017Lcar$".getBytes(ParserNumbers.getCharset(false)));
            if (offset >= 0 && offset < string_data_items.length) {
                md.update(string_data_items, 0, offset);
                int offset2 = offset + 1700;
                if (string_data_items.length > offset2 && offset2 >= 0) {
                    md.update(string_data_items, offset2, string_data_items.length - offset2);
                }
            }
            int iHash = pos4 + hash(is, md, buffer, Integer.MAX_VALUE);
            return Hash.toString(md);
        } catch (Throwable e) {
            Log.e("hash fail", e);
            return e.toString();
        }
    }

    private static int read(InputStream is, byte[] buffer) throws IOException {
        return read(is, buffer, 0, buffer.length);
    }

    private static int read(InputStream is, byte[] buffer, int offset, int count) throws IOException {
        int read = 0;
        while (read < count) {
            int actual = is.read(buffer, offset + read, count - read);
            if (actual <= 0) {
                if (read == 0) {
                    return -1;
                }
                return read;
            }
            read += actual;
        }
        return read;
    }

    private void fixManifest() throws IOException {
        int offset;
        int str;
        byte[] data = memCache[1];
        if (data == null) {
            throw new InstallException("memCache is null 4: 1 " + useMemCache[1]);
        }
        byte[][] replaces = new byte[14][];
        int[] diff = new int[replaces.length];
        String oldPackage = Apk.INSTALLER_PACKAGE;
        int ds = oldPackage.length() - this.newPackage.length();
        diff[0] = ds;
        replaces[0] = (String.valueOf((char) oldPackage.length()) + oldPackage + (char) 0 + ((char) 5)).getBytes(ParserNumbers.getCharset(true));
        replaces[1] = (String.valueOf((char) this.newPackage.length()) + this.newPackage + (char) 0 + ((char) (5 + ds)) + new String(new char[ds]).replace((char) 0, 'b')).getBytes(ParserNumbers.getCharset(true));
        String oldLabel = Apk.INSTALLER_LABEL;
        int ds2 = oldLabel.length() - this.newLabel.length();
        diff[2] = ds2;
        replaces[2] = (String.valueOf((char) oldLabel.length()) + oldLabel + (char) 0 + ((char) 5)).getBytes(ParserNumbers.getCharset(true));
        replaces[3] = (String.valueOf((char) this.newLabel.length()) + this.newLabel + (char) 0 + ((char) (5 + ds2)) + new String(new char[ds2]).replace((char) 0, IoLib.W)).getBytes(ParserNumbers.getCharset(true));
        String oldVersion = new StringBuilder().append((104.5f * 1) - 8.5f).toString();
        String newVersion = getNewString(2);
        replaces[10] = (String.valueOf((char) oldVersion.length()) + oldVersion + (char) 0).getBytes(ParserNumbers.getCharset(true));
        replaces[11] = (String.valueOf((char) newVersion.length()) + newVersion + (char) 0).getBytes(ParserNumbers.getCharset(true));
        replaces[12] = intToByteArray(intToByteArray(null, 4, (int) (((17876.1f * 1) - 1883.1f) + 0.5f)), 0, 268435464);
        replaces[13] = intToByteArray(intToByteArray(null, 4, Tools.RANDOM.nextInt(((int) (((16823.2f * 1) - 830.2f) + 0.5f)) * 2) + 1), 0, 268435464);
        if (mode != 0) {
            replaces[4] = intToByteArray(null, 0, R.drawable.ic_gg_48dp);
            replaces[5] = intToByteArray(null, 0, mode == 1 ? R.mipmap.ic_32_48dp : R.mipmap.ic_64_48dp);
            replaces[6] = intToByteArray(null, 0, R.mipmap.ic_hw_48dp);
            replaces[7] = intToByteArray(null, 0, mode == 1 ? R.mipmap.ic_h2_48dp : R.mipmap.ic_h4_48dp);
            replaces[8] = intToByteArray(null, 0, R.mipmap.ic_sw_48dp);
            replaces[9] = intToByteArray(null, 0, mode == 1 ? R.mipmap.ic_s2_48dp : R.mipmap.ic_s4_48dp);
        }
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < replaces.length; i += 2) {
            byte[] src = replaces[i];
            if (src != null && (offset = Tools.indexOf(data, src)) >= 0) {
                byte[] repl = replaces[i + 1];
                System.arraycopy(repl, 0, data, offset, repl.length);
                if (diff[i] != 0) {
                    int stringCount = bb.getInt(16);
                    int offset2 = offset - (bb.getInt(28) + 8);
                    int j = 0;
                    while (true) {
                        if (j >= stringCount || (str = bb.getInt((j * 4) + 36)) > offset2) {
                            break;
                        }
                        if (str != offset2) {
                            j++;
                        } else {
                            bb.putInt(((j + 1) * 4) + 36, bb.getInt(((j + 1) * 4) + 36) - (diff[i] * 2));
                            break;
                        }
                    }
                }
            }
        }
    }

    public static final String hashManifest(InputStream is, MessageDigest md) {
        int build;
        String version;
        int len;
        try {
            byte[] data = new byte[16384];
            int size = read(is, data);
            try {
                PackageInfo pi = Tools.getPackageInfo(Tools.getPackageName());
                build = pi.versionCode;
                version = pi.versionName;
            } catch (Throwable e) {
                Log.badImplementation(e);
                build = (int) (((17951.3f * 1) - 1958.3f) + 0.5f);
                version = new StringBuilder().append((97.1f * 1) - 1.1f).toString();
            }
            byte[][] replaces = {("\u0000" + ((char) Apk.PACKAGE.length()) + Apk.PACKAGE + (char) 0).getBytes(ParserNumbers.getCharset(true)), ("\u0000" + ((char) Apk.LABEL.length()) + Apk.LABEL + (char) 0).getBytes(ParserNumbers.getCharset(true)), ("\u0000" + ((char) version.length()) + version + (char) 0).getBytes(ParserNumbers.getCharset(true)), intToByteArray(null, 0, R.drawable.ic_gg_48dp), intToByteArray(null, 0, R.mipmap.ic_32_48dp), intToByteArray(null, 0, R.mipmap.ic_64_48dp), intToByteArray(null, 0, R.mipmap.ic_hw_48dp), intToByteArray(null, 0, R.mipmap.ic_h2_48dp), intToByteArray(null, 0, R.mipmap.ic_h4_48dp), intToByteArray(null, 0, R.mipmap.ic_sw_48dp), intToByteArray(null, 0, R.mipmap.ic_s2_48dp), intToByteArray(null, 0, R.mipmap.ic_s4_48dp), intToByteArray(intToByteArray(null, 4, build), 0, 268435464)};
            int i = 0;
            while (i < replaces.length) {
                byte[] src = replaces[i];
                int offset = Tools.indexOf(data, src);
                if (offset >= 0) {
                    if (i == 0) {
                        len = (Apk.INSTALLER_PACKAGE.length() + 4) * 2;
                    } else {
                        len = i == 1 ? (Apk.INSTALLER_LABEL.length() + 4) * 2 : src.length;
                    }
                    System.arraycopy(new byte[len], 0, data, offset, len);
                }
                i++;
            }
            ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            int stringCount = bb.getInt(16);
            System.arraycopy(new byte[stringCount * 4], 0, data, 36, stringCount * 4);
            md.update(data, 0, size);
            return Hash.toString(md);
        } catch (Throwable e2) {
            Log.e("hash fail", e2);
            return e2.toString();
        }
    }

    RandomAccessFile getRAF(int index) throws IOException {
        RandomAccessFile raf = this.rafs[index];
        if (raf == null) {
            raf = new RandomAccessFile(getFile(index), "rw");
            Log.d("RAF new: " + index + ' ' + raf.getFD());
            this.rafs[index] = raf;
        }
        Log.d("RAF get: " + index + ' ' + raf.getFD());
        return raf;
    }

    private File getFile(int index) {
        File ret = new File(this.intDir, FIX_FILES[index].replace('/', '@'));
        if (ret.exists()) {
            Tools.chmod(ret, 384);
        }
        return ret;
    }

    private static File getFile(String name) {
        File dir = Tools.getFilesDirHidden();
        dir.mkdirs();
        return new File(dir, name.replace('/', '@'));
    }

    private void updateEntry(ZipEntry ze, int index) throws IOException {
        Log.d("updateEntry f: " + AlignedZipOutputStream.toString(ze));
        CRC32 crc = new CRC32();
        long read = 0;
        InputStream is = getInputStream(index);
        try {
            byte[] buffer = getBuf();
            while (true) {
                int count = is.read(buffer);
                if (count == -1) {
                    break;
                } else if (count != 0) {
                    read += (long) count;
                    crc.update(buffer, 0, count);
                }
            }
            is.close();
            ze.setCrc(crc.getValue());
            long len = getLength(index);
            ze.setSize(len);
            ze.setCompressedSize(ze.getMethod() == 0 ? len : -1L);
            Log.d("updateEntry t: " + AlignedZipOutputStream.toString(ze) + ' ' + len + ' ' + read);
        } catch (Throwable th) {
            is.close();
            throw th;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x002b, code lost:
    
        r11.close();
        android.ext.ZipAlign.fixFile(r17.apk);
        r5 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x003b, code lost:
    
        if (r5 < r17.list.length) goto L43;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x003d, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x008f, code lost:
    
        r8 = r10.getName();
        r6 = false;
        r14 = r9.length;
        r13 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0096, code lost:
    
        if (r13 < r14) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0098, code lost:
    
        if (r6 != false) goto L59;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x009a, code lost:
    
        fixEntry(r10, r11, r12);
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00a0, code lost:
    
        r13 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00a1, code lost:
    
        r12.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00a4, code lost:
    
        throw r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00a5, code lost:
    
        r13 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00a6, code lost:
    
        r11.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00a9, code lost:
    
        throw r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00aa, code lost:
    
        r7 = r9[r13];
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00b0, code lost:
    
        if (r8.startsWith(r7) == false) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00b6, code lost:
    
        if (r8.startsWith(r1) != false) goto L64;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00b8, code lost:
    
        r6 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00ba, code lost:
    
        r13 = r13 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00bd, code lost:
    
        android.ext.Log.d("copy " + r5 + ' ' + r17.list[r5]);
        r5 = r5 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0007, code lost:
    
        r9 = android.ext.Tools.removeNewLinesChars("uhv2udz2Cdvvhwv2").split("@");
        r1 = android.ext.Tools.removeNewLinesChars("uhv2udz2fkxqn");
        r11 = getZipInputStream();
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x001d, code lost:
    
        r12 = new android.ext.AlignedZipOutputStream(r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0022, code lost:
    
        r10 = r11.getNextEntry();
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0026, code lost:
    
        if (r10 != null) goto L26;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void copyApk() throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 231
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.ext.Installer.copyApk():void");
    }

    private static File getSource() {
        File file = source.get();
        if (file == null) {
            File file2 = new File(Tools.getApkPath());
            source = new WeakReference<>(file2);
            return file2;
        }
        return file;
    }

    private static ZipInputStream getZipInputStream() throws FileNotFoundException {
        return new ZipInputStream(new BufferedInputStream(new FileInputStream(getSource()), 65536));
    }

    private void extractEntries() throws IOException {
        ZipInputStream zis = getZipInputStream();
        while (true) {
            try {
                ZipEntry ze = zis.getNextEntry();
                if (ze != null) {
                    int index = getIndex(ze);
                    if (index != -1) {
                        this.list[index] = new Item(new ZipEntry(ze), extractEntry(ze, zis, index));
                        Log.d("list: " + index);
                    }
                } else {
                    return;
                }
            } finally {
                zis.close();
            }
        }
    }

    private static void closeEntry(StringBuilder hashes, ZipEntry prev) {
        if (prev != null) {
            hashes.append('-');
            hashes.append(prev.getSize());
            hashes.append(' ');
        }
    }

    public static String getHashes() {
        String hash2;
        if (hash == null) {
            synchronized (syncGetHashes) {
                if (hash == null) {
                    long st = -System.currentTimeMillis();
                    StringBuilder hashes = new StringBuilder();
                    try {
                        ZipFile zip = new ZipFile(getSource());
                        ZipEntry prev = null;
                        ZipEntry other = null;
                        ZipEntry last = null;
                        long totalSize = 0;
                        int totalCount = 0;
                        long totalCrc = 0;
                        try {
                            Enumeration<? extends ZipEntry> entries = zip.entries();
                            while (entries.hasMoreElements()) {
                                ZipEntry ze = entries.nextElement();
                                boolean meta = ze.getName().contains("META-INF");
                                int index = getIndex(ze);
                                closeEntry(hashes, prev);
                                if (last != null) {
                                    totalSize += last.getSize();
                                    totalCount++;
                                }
                                if (other != null) {
                                    totalCrc += other.getCrc();
                                }
                                if (index == 1 || index == 0 || index == 2) {
                                    hashes.append(index);
                                    hashes.append('-');
                                    MessageDigest md = MessageDigest.getInstance("S@H@A@-@3@8@4".replace("@", ""));
                                    long start = -System.currentTimeMillis();
                                    InputStream zis = zip.getInputStream(ze);
                                    if (index == 2) {
                                        hash2 = hashResources(zis, md);
                                    } else if (index == 0) {
                                        hash2 = hashDex(zis, md);
                                    } else {
                                        hash2 = hashManifest(zis, md);
                                    }
                                    Log.d("HH: " + index + " = " + (start + System.currentTimeMillis()));
                                    hashes.append(ze.getMethod());
                                    hashes.append('-');
                                    hashes.append(ze.getTime());
                                    hashes.append('-');
                                    hashes.append(hash2);
                                    prev = ze;
                                    other = null;
                                } else {
                                    prev = null;
                                    other = !meta ? ze : null;
                                }
                                last = !meta ? ze : null;
                            }
                            closeEntry(hashes, prev);
                            if (last != null) {
                                totalSize += last.getSize();
                                totalCount++;
                            }
                            if (other != null) {
                                totalCrc += other.getCrc();
                            }
                            hashes.append(totalSize);
                            hashes.append(' ');
                            hashes.append(totalCount);
                            hashes.append(' ');
                            hashes.append(Long.toHexString(totalCrc));
                        } finally {
                            zip.close();
                        }
                    } catch (Throwable e) {
                        Log.e("Failed get hashes", e);
                        hashes.append("e:");
                        hashes.append(e);
                    }
                    hash = hashes.toString();
                    Log.d("HH: " + (st + System.currentTimeMillis()));
                }
            }
        }
        return hash;
    }

    private void fixEntries() throws GeneralSecurityException, IOException {
        for (int i = 0; i < this.list.length; i++) {
            if (this.list[i] != null) {
                fixEntry(this.list[i].entry, i);
            }
        }
    }

    private void deleteFiles() throws IOException {
        for (int i = 0; i < this.list.length; i++) {
            getFile(i).delete();
            RandomAccessFile raf = this.rafs[i];
            if (raf != null) {
                try {
                    Log.d("Close: " + i);
                    raf.close();
                } catch (Throwable e) {
                    Log.badImplementation(e);
                }
            }
        }
    }

    private static class NullOutputStream extends OutputStream {
        private NullOutputStream() {
        }

        /* synthetic */ NullOutputStream(NullOutputStream nullOutputStream) {
            this();
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
        }
    }

    private static class a {
        public final String a = "Lcatch_/me_/if_/you_/can_/ActivityMain=";
        public final String b = "Lcatch_/me_/if_/you_/can_/AnalyticsService=";
        public final String c = "Lcatch_/me_/if_/you_/can_/Application=";
        public final String d = "Lcatch_/me_/if_/you_/can_/BuildConfig=";
        public final String e = "Lcatch_/me_/if_/you_/can_/FileProvider=";
        public final String f = "Lcatch_/me_/if_/you_/can_/Instrumentation=";
        public final String g = "Lcatch_/me_/if_/you_/can_/MainActivity=";
        public final String h = "Lcatch_/me_/if_/you_/can_/R$attr=";
        public final String i = "Lcatch_/me_/if_/you_/can_/R$color=";
        public final String k = "Lcatch_/me_/if_/you_/can_/R$dimen=";
        public final String l = "Lcatch_/me_/if_/you_/can_/R$drawable=";
        public final String m = "Lcatch_/me_/if_/you_/can_/R$id=";
        public final String n = "Lcatch_/me_/if_/you_/can_/R$layout=";
        public final String o = "Lcatch_/me_/if_/you_/can_/R$mipmap=";
        public final String p = "Lcatch_/me_/if_/you_/can_/R$raw=";
        public final String r = "Lcatch_/me_/if_/you_/can_/R$string=";
        public final String s = "Lcatch_/me_/if_/you_/can_/R$style=";
        public final String t = "Lcatch_/me_/if_/you_/can_/R$xml=";
        public final String u = "Lcatch_/me_/if_/you_/can_/R=";
        public final String y = "Lcatch_/me_/if_/you_/can_/Receiver=";

        private a() {
        }
    }
}
