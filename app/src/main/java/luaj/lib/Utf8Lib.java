package luaj.lib;

import android.ext.AddressItem;
import android.ext.Message;
import java.util.Arrays;
import luaj.LuaError;
import luaj.LuaString;
import luaj.LuaTable;
import luaj.LuaValue;
import luaj.Varargs;

/* JADX INFO: loaded from: classes.dex */
public class Utf8Lib extends TwoArgFunction {
    private static final int ERROR = 5;
    private static final int MAXUNICODE = 1114111;
    private static final int SHIFT = 28;

    @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable table = new LuaTable();
        table.set("char", new _char());
        table.set("charpattern", LuaString.valueUsing(new byte[]{91, 0, Message.CMD_CS_STOP_SERVICE, 127, -62, Message.CMD_CS_STOP_SERVICE, -12, 93, 91, -128, Message.CMD_CS_STOP_SERVICE, -65, 93, Message.CMD_CS_RESET_SEARCH}));
        table.set("codes", new codes());
        table.set("codepoint", new codepoint());
        table.set("len", new len());
        table.set("offset", new offset());
        env.set("utf8", table);
        if (!env.get("package").isnil()) {
            env.get("package").get("loaded").set("utf8", table);
        }
        return NIL;
    }

    static int getCodePoint(byte[] bytes, int offset2, int len2) {
        int s;
        int cp;
        int min;
        int b = bytes[offset2] & 255;
        if (b <= 127) {
            s = 1;
            cp = b;
            min = -1;
        } else {
            if (b < 192) {
                return 1342177280 | offset2;
            }
            if (b < 224) {
                s = 2;
                cp = b ^ 192;
                min = AddressItem.FLAG_AUTO;
            } else if (b < 240) {
                s = 3;
                cp = b ^ 224;
                min = 2047;
            } else {
                if (b >= 248) {
                    return 1342177280 | offset2;
                }
                s = 4;
                cp = b ^ 240;
                min = 65535;
            }
        }
        for (int i = 1; i < s; i++) {
            if (i >= len2) {
                return 1342177280 | offset2;
            }
            int b2 = bytes[offset2 + i] & 255;
            if ((b2 & 192) != 128) {
                return 1342177280 | offset2;
            }
            cp = (cp << 6) | (b2 & 63);
        }
        return (cp > MAXUNICODE || cp <= min) ? 1342177280 | offset2 : (s << 28) | cp;
    }

    static final class _char extends VarArgFunction {
        _char() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            int s;
            int m;
            int n = args.narg();
            if (n == 0) {
                return EMPTYSTRING;
            }
            byte[] bytes = new byte[n * 4];
            int offset = 0;
            for (int i = 1; i <= n; i++) {
                int cp = args.checkint(i);
                if (cp < 0 || cp > Utf8Lib.MAXUNICODE) {
                    argerror(i, name(), "'" + cp + "': value out of range [0; " + Utf8Lib.MAXUNICODE + "]");
                }
                if (cp <= 127) {
                    s = 1;
                    m = 0;
                } else if (cp <= 2047) {
                    s = 2;
                    m = 192;
                } else if (cp <= 65535) {
                    s = 3;
                    m = 224;
                } else {
                    s = 4;
                    m = 240;
                }
                for (int j = s - 1; j > 0; j--) {
                    bytes[offset + j] = (byte) ((cp & 63) | 128);
                    cp >>= 6;
                }
                bytes[offset] = (byte) (cp | m);
                offset += s;
            }
            if (offset < bytes.length) {
                bytes = Arrays.copyOf(bytes, offset);
            }
            return LuaString.valueUsing(bytes, 0, offset);
        }
    }

    static final class codes extends VarArgFunction {
        codes() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaString src = args.checkstring(1);
            return new CodesAux(src);
        }
    }

    static class CodesAux extends VarArgFunction {
        private int offset = 0;
        private final LuaString str;

        public CodesAux(LuaString src) {
            this.str = src;
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            int offset = this.offset;
            int max = this.str.m_offset + this.str.m_length;
            if (offset >= max) {
                return NIL;
            }
            int cp = Utf8Lib.getCodePoint(this.str.m_bytes, this.str.m_offset + offset, max);
            int s = cp >> 28;
            int cp2 = cp & 268435455;
            if (s == 5) {
                throw Utf8Lib.invalidUtf((cp2 - this.str.m_offset) + 1);
            }
            this.offset = offset + s;
            return varargsOf(valueOf(offset + 1), valueOf(cp2));
        }
    }

    static LuaError invalidUtf(int at) {
        return new LuaError("invalid UTF-8 code at " + (at + 1) + " byte");
    }

    static void out(String name, int n, int val, int len2) {
        argerror(n, name, "'" + val + "': position out of range [1; " + len2 + "]");
    }

    static void badFinal(String name, int n, int val, int len2) {
        argerror(n, name, "'" + val + "': final position out of string length [" + len2 + "]");
    }

    static final class codepoint extends VarArgFunction {
        codepoint() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaString str = args.checkstring(1);
            int len = str.m_length;
            int i = StringLib.posrelat(args.optint(2, 1), len);
            int j = StringLib.posrelat(args.optint(3, i), len);
            if (i < 1 || i > len + 1) {
                Utf8Lib.out(name(), 2, i, len);
            }
            if (j > len) {
                Utf8Lib.badFinal(name(), 3, j, len);
            }
            if (i > j) {
                return NONE;
            }
            LuaValue[] cps = new LuaValue[(j - i) + 1];
            byte[] bytes = str.m_bytes;
            int offset = str.m_offset;
            int max = str.m_offset + str.m_length;
            int k = i - 1;
            int used = 0;
            while (k < j) {
                int cp = Utf8Lib.getCodePoint(bytes, offset + k, max);
                int s = cp >> 28;
                int cp2 = cp & 268435455;
                if (s == 5) {
                    throw Utf8Lib.invalidUtf((cp2 - offset) + 1);
                }
                k += s;
                cps[used] = valueOf(cp2);
                used++;
            }
            if (used == 0) {
                return NONE;
            }
            if (used < cps.length) {
                cps = (LuaValue[]) Arrays.copyOf(cps, used);
            }
            return varargsOf(cps);
        }
    }

    static final class len extends VarArgFunction {
        len() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaString str = args.checkstring(1);
            int len = str.m_length;
            int i = StringLib.posrelat(args.optint(2, 1), len);
            int j = StringLib.posrelat(args.optint(3, -1), len);
            if (i < 1 || i > len + 1) {
                Utf8Lib.out(name(), 2, i, len);
            }
            if (j > len) {
                Utf8Lib.badFinal(name(), 3, j, len);
            }
            if (i > j) {
                return ZERO;
            }
            int n = 0;
            byte[] bytes = str.m_bytes;
            int offset = str.m_offset;
            int max = str.m_offset + str.m_length;
            int k = i - 1;
            while (k < j) {
                int cp = Utf8Lib.getCodePoint(bytes, offset + k, max);
                int s = cp >> 28;
                if (s == 5) {
                    return varargsOf(NIL, valueOf(((268435455 & cp) - offset) + 1));
                }
                k += s;
                n++;
            }
            return valueOf(n);
        }
    }

    static final class offset extends VarArgFunction {
        offset() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaString str = args.checkstring(1);
            int len = str.m_length;
            int n = args.checkint(2);
            int j = StringLib.posrelat(args.optint(3, n >= 0 ? 1 : len + 1), len);
            if (j < 1 || j > len + 1) {
                Utf8Lib.out(name(), 3, j, len);
            }
            byte[] bytes = str.m_bytes;
            int offset = str.m_offset;
            int j2 = j - 1;
            if (n == 0) {
                while (j2 > 0 && j2 < len && (bytes[offset + j2] & 192) == 128) {
                    j2--;
                }
            } else {
                if (j2 < len && (bytes[offset + j2] & 192) == 128) {
                    throw new LuaError("initial position (" + j2 + ") is a continuation byte (0x" + Integer.toHexString(bytes[offset + j2] & 255) + ")");
                }
                if (n < 0) {
                    while (n < 0 && j2 > 0) {
                        do {
                            j2--;
                            if (j2 <= 0 || j2 >= len) {
                                break;
                            }
                            n++;
                        } while ((bytes[offset + j2] & 192) == 128);
                        n++;
                    }
                } else {
                    n--;
                    while (n > 0 && j2 < len) {
                        do {
                            j2++;
                            if (j2 < 0 || j2 >= len) {
                                break;
                            }
                            n--;
                        } while ((bytes[offset + j2] & 192) == 128);
                        n--;
                    }
                }
            }
            return n == 0 ? valueOf(j2 + 1) : NIL;
        }
    }

    public static void main(String[] args) throws Throwable {
    }
}
