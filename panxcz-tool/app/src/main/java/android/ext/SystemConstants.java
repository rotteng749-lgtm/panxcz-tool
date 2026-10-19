package android.ext;

import android.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class SystemConstants {
    public static final int JUMP_ITEM_COLOR = -2137746366;
    public static final int SELECTED_ITEM_COLOR = -2139864428;
    public static final boolean useEdgeGlow = true;
    public static volatile boolean useFloatWindows = true;
    public static final boolean useInstrumentation = true;

    public static int getTypePhone() {
        int type = Config.floatType;
        if (type < 0) {
            type = 2002;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            return 2038;
        }
        return type;
    }

    public static int getTypeSystemAlert() {
        int type = Config.floatType;
        if (type < 0) {
            type = 2003;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            return 2038;
        }
        return type;
    }

    public static int getFlags() {
        int flags = Config.floatFlags;
        if (flags < 0) {
            return 8;
        }
        return flags;
    }
}
