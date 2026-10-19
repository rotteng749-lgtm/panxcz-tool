package android.ext;

/* JADX INFO: loaded from: classes.dex */
public class DisplayNumbers {
    private static final int FULL_MASK = 3;
    private static final int NEGATIVE_MASK = 2;
    private static final int POSITIVE_MASK = 1;
    private static final int SIGN_MASK = 0;
    private static final int SIZE_MASK = 4;
    private static long[] masks = new long[12];

    static {
        for (int i = 0; i < 3; i++) {
            long mask = (1L << ((1 << i) * 8)) - 1;
            long value_mask = mask >> 1;
            long sign_mask = mask & ((-1) ^ value_mask);
            int pos = i * 4;
            masks[pos + 0] = sign_mask;
            masks[pos + 1] = value_mask;
            masks[pos + 2] = (-1) ^ value_mask;
            masks[pos + 3] = mask;
        }
    }

    public static String longToString(long l, int flags) {
        return longToString(l, flags, false);
    }

    public static long longToSigned(long l, int pow, boolean hex) {
        if (pow != 3) {
            int p = pow * 4;
            if (hex) {
                long v = l & masks[p + 3];
                return v;
            }
            if ((masks[p + 0] & l) == 0) {
                long v2 = l & masks[p + 1];
                return v2;
            }
            long v3 = l | masks[p + 2];
            return v3;
        }
        return l;
    }

    public static String longToString(long l, int flags, boolean hex) {
        int pow = AddressItem.getPow(flags);
        long v = longToSigned(l, pow, hex);
        if (hex) {
            String ret = ToolsLight.prefixLongHex(1 << (pow + 1), v);
            return ret;
        }
        String ret2 = longToString_(v);
        return ret2;
    }

    private static String longToString_(long l) {
        String s = null;
        if (0 == 0) {
            s = Tools.stringFormatOne("%,d", "%d", null, Long.valueOf(l));
        }
        if (s == null) {
            try {
                s = Long.toString(l);
            } catch (Throwable e) {
                Log.w("Format fail 3", e);
            }
        }
        if (s == null) {
            try {
                s = new StringBuilder().append(l).toString();
            } catch (Throwable e2) {
                Log.w("Format fail 4", e2);
            }
        }
        if (s == null) {
            return "fail";
        }
        return s;
    }

    public static boolean isSignedLess(long A, long B, int flags) {
        int pow = AddressItem.getPow(flags);
        if (pow == 3) {
            return A < B;
        }
        int pow2 = pow * 4;
        long sign_A = A & masks[pow2 + 0];
        long sign_B = B & masks[pow2 + 0];
        long value_A = A & masks[pow2 + 1];
        long value_B = B & masks[pow2 + 1];
        return sign_A == sign_B ? value_A < value_B : sign_A != 0;
    }

    public static void main(String[] args) {
    }
}
