package android.ext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import luaj.lib.IoLib;

/* JADX INFO: loaded from: classes.dex */
public class ToolsLight {
    private static final ThreadLocal<char[]> prefixBuf = new ThreadLocal<char[]>() { // from class: android.ext.ToolsLight.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public char[] initialValue() {
            return new char[16];
        }
    };
    private static final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', SearchMenuItem.HEX_UTF, 'R', 'S', 'T', 'U', 'V', 'W', SearchMenuItem.XOR_MODE, 'Y', 'Z', '_', IoLib.A, 'b', 'c', 'd', 'e', 'f', 'g', SearchMenuItem.HEX, 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', IoLib.R, 's', 't', 'u', 'v', IoLib.W, SearchMenuItem.XOR_MODE_SMALL, 'y', 'z'};

    public static Appendable prefixIntegerHex(Appendable out, int cnt, int v) {
        return prefix(out, cnt, '0', Integer.toHexString(v).toUpperCase(Locale.US));
    }

    public static Appendable prefixLongHex(Appendable out, int cnt, long v) {
        return prefix(out, cnt, '0', Long.toHexString(v).toUpperCase(Locale.US));
    }

    public static Appendable prefix(Appendable out, int cnt, char filler, String str) {
        try {
            int sj = cnt - str.length();
            for (int j = 0; j < sj; j++) {
                out.append(filler);
            }
            return out.append(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String prefixLongHex(int cnt, long v) {
        return prefix(cnt, '0', Long.toHexString(v).toUpperCase(Locale.US));
    }

    public static String prefix(int cnt, char filler, String str) {
        int len = str.length();
        if (len < cnt) {
            char[] buf = prefixBuf.get();
            int add = cnt - len;
            Arrays.fill(buf, 0, add, filler);
            str.getChars(0, len, buf, add);
            return new String(buf, 0, cnt);
        }
        return str;
    }

    public static String stringFormat(String format, Object... args) {
        return Tools.stringFormat(format, args);
    }

    public static String toString64(long i) {
        int charPos;
        int charPos2;
        char[] buf = new char[12];
        int charPos3 = 11;
        boolean negative = i < 0;
        if (!negative) {
            i = -i;
        }
        char[] digits2 = digits;
        while (true) {
            charPos = charPos3;
            if (i > -64) {
                break;
            }
            charPos3 = charPos - 1;
            buf[charPos] = digits2[(int) (-(i % 64))];
            i /= 64;
        }
        buf[charPos] = digits2[(int) (-i)];
        if (negative) {
            charPos2 = charPos - 1;
            buf[charPos2] = '-';
        } else {
            charPos2 = charPos;
        }
        return new String(buf, charPos2, 12 - charPos2);
    }

    public static String toString64(int i) {
        int charPos;
        int charPos2;
        char[] buf = new char[7];
        int charPos3 = 6;
        boolean negative = i < 0;
        if (!negative) {
            i = -i;
        }
        char[] digits2 = digits;
        while (true) {
            charPos = charPos3;
            if (i > -64) {
                break;
            }
            charPos3 = charPos - 1;
            buf[charPos] = digits2[-(i % 64)];
            i /= 64;
        }
        buf[charPos] = digits2[-i];
        if (negative) {
            charPos2 = charPos - 1;
            buf[charPos2] = '-';
        } else {
            charPos2 = charPos;
        }
        return new String(buf, charPos2, 7 - charPos2);
    }
}
