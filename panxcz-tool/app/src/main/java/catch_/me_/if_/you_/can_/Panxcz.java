package catch_.me_.if_.you_.can_;

/**
 * JNI bridge ke panxcz-libtool core (libpanxcz.so).
 *
 * Core libtool (mem/il2cpp/explore/dumpout/scanner/patch)原本 external ELF
 * buat /proc/<pid>/mem. Di sini core yang SAMA dipanggil dari dalam app, jadi
 * explorer class IL2CPP, dump Assembly-CSharp, dan patch bisa dipanggil
 * langsung dari UI / Lua tanpa perlu ELF terpisah.
 */
public final class Panxcz {

    public static final int TYPE_INT32 = 0;
    public static final int TYPE_INT16 = 1;
    public static final int TYPE_FLOAT = 3;
    public static final int TYPE_DOUBLE = 4;

    private static boolean loaded;
    private static String loadError = "";

    static {
        try {
            System.loadLibrary("panxcz");
            loaded = true;
        } catch (Throwable t) {
            loadError = t.getMessage();
            loaded = false;
        }
    }

    private Panxcz() {
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static String getLoadError() {
        return loadError;
    }

    /* ---- native ---- */
    private static native void nativeInit(String pkg, String lib);

    private static native void nativeDetach();

    private static native String nativeListClasses(String filter);

    private static native String nativeGetFields(String className);

    private static native String nativeGetMethods(String className);

    private static native String nativeExplore(String className);

    private static native String nativeInstances(String className);

    private static native double nativeReadValue(long address, int valueType);

    private static native boolean nativeWriteValue(long address, int valueType, double value);

    private static native String nativeDumpOutput(String filter);

    private static native String nativeScan(long value, int type, String region);

    private static native String nativeAttachInfo();

    private static native String nativeSpoofIdentity();

    private static native String nativeDumpAssemblyCSharp(String outFilePath);

    /* ---- safe wrappers ---- */
    public static void init(String pkg, String lib) {
        if (!loaded) return;
        try {
            nativeInit(pkg, lib == null ? "" : lib);
        } catch (Throwable ignored) {
        }
    }

    public static void detach() {
        if (!loaded) return;
        try {
            nativeDetach();
        } catch (Throwable ignored) {
        }
    }

    public static String listClasses(String filter) {
        if (!loaded) return "{\"classes\":[]}";
        try {
            return nativeListClasses(filter == null ? "" : filter);
        } catch (Throwable t) {
            return "{\"classes\":[]}";
        }
    }

    public static String getFields(String className) {
        if (!loaded) return "{\"fields\":[]}";
        try {
            return nativeGetFields(className);
        } catch (Throwable t) {
            return "{\"fields\":[]}";
        }
    }

    public static String getMethods(String className) {
        if (!loaded) return "{\"methods\":[]}";
        try {
            return nativeGetMethods(className);
        } catch (Throwable t) {
            return "{\"methods\":[]}";
        }
    }

    public static String explore(String className) {
        if (!loaded) return "{\"ok\":false}";
        try {
            return nativeExplore(className);
        } catch (Throwable t) {
            return "{\"ok\":false}";
        }
    }

    public static String instances(String className) {
        if (!loaded) return "{\"count\":0,\"instances\":[]}";
        try {
            return nativeInstances(className);
        } catch (Throwable t) {
            return "{\"count\":0,\"instances\":[]}";
        }
    }

    public static double readValue(long address, int valueType) {
        if (!loaded) return 0;
        try {
            return nativeReadValue(address, valueType);
        } catch (Throwable t) {
            return 0;
        }
    }

    public static boolean writeValue(long address, int valueType, double value) {
        if (!loaded) return false;
        try {
            return nativeWriteValue(address, valueType, value);
        } catch (Throwable t) {
            return false;
        }
    }

    public static String dumpOutput(String filter) {
        if (!loaded) return "{\"ok\":false}";
        try {
            return nativeDumpOutput(filter == null ? "" : filter);
        } catch (Throwable t) {
            return "{\"ok\":false}";
        }
    }

    public static String scan(long value, int type, String region) {
        if (!loaded) return "{\"count\":0,\"hits\":[]}";
        try {
            return nativeScan(value, type, region == null ? "" : region);
        } catch (Throwable t) {
            return "{\"count\":0,\"hits\":[]}";
        }
    }

    public static String attachInfo() {
        if (!loaded) return "{\"pid\":0}";
        try {
            return nativeAttachInfo();
        } catch (Throwable t) {
            return "{\"pid\":0}";
        }
    }

    public static String spoofIdentity() {
        if (!loaded) return "{\"package_name\":\"com.android.systemui\",\"version_name\":\"14.0-spoofed\"}";
        try {
            return nativeSpoofIdentity();
        } catch (Throwable t) {
            return "{\"package_name\":\"com.android.systemui\",\"version_name\":\"14.0-spoofed\"}";
        }
    }

    public static String dumpAssemblyCSharp(String outFilePath) {
        if (!loaded) return "{\"ok\":false}";
        try {
            return nativeDumpAssemblyCSharp(outFilePath == null ? "" : outFilePath);
        } catch (Throwable t) {
            return "{\"ok\":false}";
        }
    }
}
