package luaj.lib;

import android.ext.AddressItem;
import android.ext.ListManager;
import android.ext.Log;
import android.ext.MainService;
import android.ext.Message;
import android.ext.ProcessList;
import android.ext.SearchMenuItem;
import android.ext.ToolsLight;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Locale;
import lasm.LasmConstants;
import luaj.Buffer;
import luaj.LuaClosure;
import luaj.LuaDouble;
import luaj.LuaError;
import luaj.LuaLong;
import luaj.LuaNumber;
import luaj.LuaString;
import luaj.LuaTable;
import luaj.LuaValue;
import luaj.Varargs;
import luaj.compiler.DumpState;

/* JADX INFO: loaded from: classes.dex */
public class StringLib extends TwoArgFunction {
    private static final int CAP_POSITION = -2;
    private static final int CAP_UNFINISHED = -1;
    private static final String FLAGS = "-+ #0";
    private static final byte Kchar = 4;
    private static final byte Kdouble = 9;
    private static final byte Kfloat = 3;
    private static final byte Kint = 1;
    private static final byte Klua = 10;
    private static final byte Knop = 0;
    private static final byte Kpaddalign = 8;
    private static final byte Kpadding = 7;
    private static final byte Kstring = 5;
    private static final byte Kuint = 2;
    private static final byte Kzstr = 6;
    private static final int L_ESC = 37;
    private static final byte MASK_ALPHA = 1;
    private static final byte MASK_CONTROL = 64;
    private static final byte MASK_DIGIT = 8;
    private static final byte MASK_HEXDIGIT = -128;
    private static final byte MASK_LOWERCASE = 2;
    private static final byte MASK_PUNCT = 16;
    private static final byte MASK_SPACE = 32;
    private static final byte MASK_UPPERCASE = 4;
    private static final int MAXCCALLS = 200;
    private static final int MAXINTSIZE = 8;
    private static final int MAX_CAPTURES = 32;
    private static final LuaString SPECIALS = valueOf("^$*+?.([%-");
    static final byte[] CHAR_TABLE = new byte[256];

    @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable string = new LuaTable();
        string.set("byte", new _byte());
        string.set("char", new _char());
        string.set("dump", new dump());
        string.set("find", new find());
        string.set("format", new format());
        string.set("gmatch", new gmatch());
        string.set("gsub", new gsub());
        string.set("len", new len());
        string.set("lower", new lower());
        string.set("match", new match());
        string.set("pack", new pack());
        string.set("packsize", new packsize());
        string.set("rep", new rep());
        string.set("reverse", new reverse());
        string.set("sub", new sub());
        string.set("unpack", new unpack());
        string.set("upper", new upper());
        env.set("string", string);
        if (!env.get("package").isnil()) {
            env.get("package").get("loaded").set("string", string);
        }
        if (LuaString.s_metatable == null) {
            LuaString.s_metatable = LuaValue.tableOf(new LuaValue[]{INDEX, string});
        }
        return string;
    }

    static final class _byte extends VarArgFunction {
        _byte() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaString s = args.checkstring(1);
            int l = s.m_length;
            int posi = StringLib.posrelat(args.optint(2, 1), l);
            int pose = StringLib.posrelat(args.optint(3, posi), l);
            if (posi <= 0) {
                posi = 1;
            }
            if (pose > l) {
                pose = l;
            }
            if (posi > pose) {
                return NONE;
            }
            int n = (pose - posi) + 1;
            if (posi + n <= pose) {
                error("string slice too long");
            }
            LuaValue[] v = new LuaValue[n];
            for (int i = 0; i < n; i++) {
                v[i] = valueOf(s.luaByte((posi + i) - 1));
            }
            return varargsOf(v);
        }
    }

    static final class _char extends VarArgFunction {
        _char() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            int n = args.narg();
            byte[] bytes = new byte[n];
            int i = 0;
            int a = 1;
            while (i < n) {
                int c = args.checkint(a);
                if (c < 0 || c >= 256) {
                    argerror(a, "invalid value for string.char [0; 255]: " + c);
                }
                bytes[i] = (byte) c;
                i++;
                a++;
            }
            return LuaString.valueUsing(bytes);
        }
    }

    static final class dump extends VarArgFunction {
        dump() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaValue f = args.checkfunction(1);
            if (!(f instanceof LuaClosure)) {
                throw new LuaError("unable to dump given function");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                DumpState.dump(((LuaClosure) f).p, baos, args.optboolean(2, false), args.optboolean(3, false));
                return LuaString.valueUsing(baos.toByteArray());
            } catch (IOException e) {
                return error(e.getMessage());
            }
        }
    }

    static final class find extends VarArgFunction {
        find() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            try {
                return StringLib.str_find_aux(args, true);
            } catch (Throwable e) {
                throw new LuaError(e);
            }
        }
    }

    static final class format extends VarArgFunction {
        format() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaString fmt = args.checkstring(1);
            int n = fmt.length();
            Buffer result = new Buffer(n);
            int arg = 1;
            FormatDesc doubleFmt = null;
            int i = 0;
            while (i < n) {
                int i2 = i + 1;
                int c = fmt.luaByte(i);
                switch (c) {
                    case 10:
                        result.append(ListManager.NEW_LINE);
                        i = i2;
                        break;
                    case 37:
                        if (i2 < n) {
                            if (fmt.luaByte(i2) == 37) {
                                result.append(Message.CMD_CS_SELECT_PROCESS);
                                i = i2 + 1;
                                break;
                            } else {
                                arg++;
                                FormatDesc fdsc = new FormatDesc(args, fmt, i2);
                                i2 += fdsc.length;
                                switch (fdsc.conversion) {
                                    case LasmConstants.NUMPARAMS /* 65 */:
                                    case LasmConstants.LINE /* 69 */:
                                    case LasmConstants.END_LOCAL /* 71 */:
                                    case 97:
                                    case 101:
                                    case 102:
                                    case 103:
                                        fdsc.format(result, args.checkdouble(arg));
                                        i = i2;
                                        continue;
                                    case 88:
                                    case Message.ERROR_FATAL /* 100 */:
                                    case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                                    case 111:
                                    case 117:
                                    case 120:
                                        fdsc.format(result, args.checklong(arg));
                                        i = i2;
                                        continue;
                                    case 99:
                                        fdsc.format(result, (byte) args.checkint(arg));
                                        i = i2;
                                        continue;
                                    case 113:
                                        LuaValue v = args.arg(arg);
                                        switch (v.type()) {
                                            case 0:
                                            case 1:
                                                result.append(v.tojstring());
                                                i = i2;
                                                continue;
                                            case 2:
                                            default:
                                                argerror(arg, "string.format", "'" + v + "': value has no literal form");
                                                i = i2;
                                                break;
                                            case 3:
                                                if (v instanceof LuaDouble) {
                                                    if (doubleFmt == null) {
                                                        doubleFmt = new FormatDesc(args, LuaString.valueOf("%a"), 1);
                                                    }
                                                    doubleFmt.format(result, v.checkdouble());
                                                    i = i2;
                                                } else if (v instanceof LuaLong) {
                                                    long val = v.tolong();
                                                    if (val == Long.MIN_VALUE) {
                                                        result.append("0x8000000000000000");
                                                        i = i2;
                                                    } else {
                                                        result.append(v);
                                                        i = i2;
                                                    }
                                                } else {
                                                    result.append(v);
                                                    i = i2;
                                                }
                                                break;
                                            case 4:
                                                StringLib.addquoted(result, args.checkstring(arg));
                                                i = i2;
                                                break;
                                        }
                                        break;
                                    case 115:
                                        LuaString s = BaseLib.tostring(args.arg(arg));
                                        if (fdsc.precision == -1 && s.length() >= 100) {
                                            result.append(s);
                                            i = i2;
                                        } else {
                                            fdsc.format(result, s.checkstring());
                                            i = i2;
                                            continue;
                                        }
                                        break;
                                    default:
                                        error("invalid option '%" + ((char) fdsc.conversion) + "' to 'string.format'");
                                        break;
                                }
                            }
                        }
                        i = i2;
                        break;
                    default:
                        result.append((byte) c);
                        i = i2;
                        break;
                }
            }
            return result.tostring();
        }
    }

    static void addquoted(Buffer buf, LuaString s) {
        buf.append(Message.CMD_CS_SEARCH_TEXT);
        int n = s.length();
        for (int i = 0; i < n; i++) {
            int c = s.luaByte(i);
            switch (c) {
                case 10:
                case 34:
                case 92:
                    buf.append((byte) 92);
                    buf.append((byte) c);
                    break;
                default:
                    if (c <= 31 || c == 127) {
                        buf.append((byte) 92);
                        if (i + 1 == n || s.luaByte(i + 1) < 48 || s.luaByte(i + 1) > 57) {
                            buf.append(Integer.toString(c));
                        } else {
                            buf.append(Message.CMD_CS_SET_TEMP_PATH);
                            buf.append((byte) ((char) ((c / 10) + 48)));
                            buf.append((byte) ((char) ((c % 10) + 48)));
                        }
                    } else {
                        buf.append((byte) c);
                    }
                    break;
            }
        }
        buf.append(Message.CMD_CS_SEARCH_TEXT);
    }

    static class FormatDesc {
        private static final int MAX_FLAGS = 5;
        private boolean alternateForm;
        public final int conversion;
        private boolean explicitPlus;
        private boolean leftAdjust;
        public final int length;
        int precision;
        private boolean space;
        public final String src;
        private int width;
        private boolean zeroPad;

        public FormatDesc(Varargs args, LuaString strfrmt, int start) {
            int p;
            int p2;
            int n = strfrmt.length();
            int c = 0;
            boolean moreFlags = true;
            int p3 = start;
            while (moreFlags) {
                if (p3 < n) {
                    p2 = p3 + 1;
                    c = strfrmt.luaByte(p3);
                } else {
                    c = 0;
                    p2 = p3;
                }
                switch (c) {
                    case 32:
                        this.space = true;
                        p3 = p2;
                        break;
                    case 35:
                        this.alternateForm = true;
                        p3 = p2;
                        break;
                    case 43:
                        this.explicitPlus = true;
                        p3 = p2;
                        break;
                    case 45:
                        this.leftAdjust = true;
                        p3 = p2;
                        break;
                    case LasmConstants.SHL /* 48 */:
                        this.zeroPad = true;
                        p3 = p2;
                        break;
                    default:
                        moreFlags = false;
                        p3 = p2;
                        break;
                }
            }
            if (p3 - start > 5) {
                StringLib.error("invalid format (repeated flags)");
            }
            this.width = -1;
            if (Character.isDigit((char) c)) {
                this.width = c - 48;
                if (p3 < n) {
                    p = p3 + 1;
                    c = strfrmt.luaByte(p3);
                } else {
                    c = 0;
                    p = p3;
                }
                if (Character.isDigit((char) c)) {
                    this.width = (this.width * 10) + (c - 48);
                    if (p < n) {
                        c = strfrmt.luaByte(p);
                        p++;
                    } else {
                        c = 0;
                    }
                }
            } else {
                p = p3;
            }
            this.precision = -1;
            if (c == 46) {
                if (p < n) {
                    c = strfrmt.luaByte(p);
                    p++;
                } else {
                    c = 0;
                }
                if (Character.isDigit((char) c)) {
                    this.precision = c - 48;
                    if (p < n) {
                        c = strfrmt.luaByte(p);
                        p++;
                    } else {
                        c = 0;
                    }
                    if (Character.isDigit((char) c)) {
                        this.precision = (this.precision * 10) + (c - 48);
                        if (p < n) {
                            c = strfrmt.luaByte(p);
                            p++;
                        } else {
                            c = 0;
                        }
                    }
                }
            }
            if (Character.isDigit((char) c)) {
                StringLib.error("invalid format (width or precision too long) at pos " + p);
            }
            this.zeroPad = (this.leftAdjust ? false : true) & this.zeroPad;
            this.conversion = c;
            this.length = p - start;
            this.src = strfrmt.substring(start - 1, p).tojstring();
        }

        public void format(Buffer buf, byte c) {
            buf.append(c);
        }

        public void format(Buffer buf, long number) {
            String digits;
            int nzeros;
            if (number == 0 && this.precision == 0) {
                digits = "";
            } else {
                switch (this.conversion) {
                    case 88:
                    case 120:
                        digits = Long.toHexString(number);
                        break;
                    case 111:
                        digits = Long.toOctalString(number);
                        break;
                    case 117:
                        if (number >= 0) {
                            digits = Long.toString(number);
                        } else {
                            long quot = (number >>> 1) / 5;
                            long rem = number - (10 * quot);
                            digits = String.valueOf(Long.toString(quot)) + rem;
                        }
                        break;
                    default:
                        digits = Long.toString(number);
                        break;
                }
                if (this.conversion == 88) {
                    digits = digits.toUpperCase(Locale.US);
                }
            }
            int minwidth = digits.length();
            int ndigits = minwidth;
            if (number < 0) {
                ndigits--;
            } else if (this.explicitPlus || this.space) {
                minwidth++;
            }
            if (this.precision > ndigits) {
                nzeros = this.precision - ndigits;
            } else if (this.precision == -1 && this.zeroPad && this.width > minwidth) {
                nzeros = this.width - minwidth;
            } else {
                nzeros = 0;
            }
            int minwidth2 = minwidth + nzeros;
            int nspaces = this.width > minwidth2 ? this.width - minwidth2 : 0;
            if (!this.leftAdjust) {
                pad(buf, ' ', nspaces);
            }
            if (number < 0) {
                if (nzeros > 0) {
                    buf.append(Message.CMD_CS_STOP_SERVICE);
                    digits = digits.substring(1);
                }
            } else if (this.explicitPlus) {
                buf.append(Message.CMD_CS_ALTER);
            } else if (this.space) {
                buf.append((byte) 32);
            }
            if (nzeros > 0) {
                pad(buf, '0', nzeros);
            }
            buf.append(digits);
            if (this.leftAdjust) {
                pad(buf, ' ', nspaces);
            }
        }

        public void format(Buffer buf, double x) {
            String out = null;
            if (Double.isNaN(x)) {
                out = LuaDouble.JSTR_NAN;
            } else if (Double.isInfinite(x)) {
                out = x < 0.0d ? LuaDouble.JSTR_NEGINF : LuaDouble.JSTR_POSINF;
            }
            if (out != null) {
                if (this.conversion < 97) {
                    out = out.toUpperCase(Locale.US);
                }
            } else {
                try {
                    out = String.format(Locale.US, this.src, Double.valueOf(x));
                } catch (Throwable e) {
                    String msg = "Failed format double " + x + " with '" + this.src + SearchMenuItem.HEX_UTF_8;
                    Log.w(msg, e);
                    out = String.valueOf(x);
                }
            }
            buf.append(out);
        }

        public void format(Buffer buf, LuaString s) {
            int len = s.length();
            int min = this.width;
            int max = this.precision;
            if (max == -1) {
                max = len;
            }
            if (len > max) {
                len = max;
            }
            if (len < min && !this.leftAdjust) {
                byte b = (byte) (this.zeroPad ? 48 : 32);
                for (int i = min - len; i > 0; i--) {
                    buf.append(b);
                }
            }
            buf.append(s.m_bytes, s.m_offset, len);
            if (len < min && this.leftAdjust) {
                byte b2 = (byte) (this.zeroPad ? 48 : 32);
                for (int i2 = min - len; i2 > 0; i2--) {
                    buf.append(b2);
                }
            }
        }

        public static final void pad(Buffer buf, char c, int n) {
            byte b = (byte) c;
            while (true) {
                int n2 = n;
                n = n2 - 1;
                if (n2 > 0) {
                    buf.append(b);
                } else {
                    return;
                }
            }
        }
    }

    static final class gmatch extends VarArgFunction {
        gmatch() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaString src = args.checkstring(1);
            LuaString pat = args.checkstring(2);
            return new GMatchAux(args, src, pat);
        }
    }

    static class GMatchAux extends VarArgFunction {
        private final MatchState ms;
        private final int srclen;
        private int soffset = 0;
        private int lastmatch = -1;

        public GMatchAux(Varargs args, LuaString src, LuaString pat) {
            this.srclen = src.length();
            this.ms = new MatchState(args, src, pat);
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            try {
                while (this.soffset <= this.srclen) {
                    this.ms.reset();
                    int res = this.ms.match(this.soffset, 0);
                    if (res < 0 || res == this.lastmatch) {
                        this.soffset++;
                    } else {
                        int soff = this.soffset;
                        this.soffset = res;
                        this.lastmatch = res;
                        return this.ms.push_captures(true, soff, res);
                    }
                }
                return NIL;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    static final class gsub extends VarArgFunction {
        gsub() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            try {
                int soffset;
                LuaString src = args.checkstring(1);
                int srclen = src.length();
                LuaString p = args.checkstring(2);
                int lastmatch = -1;
                LuaValue repl = args.arg(3);
                int max_s = args.optint(4, srclen + 1);
                boolean anchor = p.length() > 0 && p.charAt(0) == 94;
                Buffer lbuf = new Buffer(srclen);
                MatchState ms = new MatchState(args, src, p);
                int n = 0;
                int soffset2 = 0;
                while (n < max_s) {
                    ms.reset();
                    int res = ms.match(soffset2, anchor ? 1 : 0);
                    if (res != -1 && res != lastmatch) {
                        n++;
                        ms.add_value(lbuf, soffset2, res, repl);
                        lastmatch = res;
                        soffset = res;
                    } else {
                        if (soffset2 >= srclen) {
                            break;
                        }
                        soffset = soffset2 + 1;
                        lbuf.append((byte) src.luaByte(soffset2));
                    }
                    if (anchor) {
                        break;
                    }
                    soffset2 = soffset;
                }
                soffset = soffset2;
                lbuf.append(src.substring(soffset, srclen));
                return varargsOf(lbuf.tostring(), valueOf(n));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    static final class len extends OneArgFunction {
        len() {
        }

        @Override // luaj.lib.OneArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            return arg.checkstring().len();
        }
    }

    static final class lower extends OneArgFunction {
        lower() {
        }

        @Override // luaj.lib.OneArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            return valueOf(arg.checkjstring().toLowerCase(Locale.US));
        }
    }

    static final class match extends VarArgFunction {
        match() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            try {
                return StringLib.str_find_aux(args, false);
            } catch (Throwable e) {
                throw new LuaError(e);
            }
        }
    }

    static final class rep extends VarArgFunction {
        rep() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaString s = args.checkstring(1);
            int n = args.checkint(2);
            if (n <= 0) {
                return EMPTYSTRING;
            }
            LuaString sep = args.optstring(3, EMPTYSTRING);
            int l = s.length();
            int lsep = sep.length();
            if (l + lsep < l || l + lsep > Integer.MAX_VALUE / n) {
                throw new LuaError("resulting string too large");
            }
            int cnt = ((l + lsep) * (n - 1)) + l;
            byte[] bytes = new byte[cnt];
            byte[] sBytes = s.m_bytes;
            int sOffset = s.m_offset;
            byte[] sepBytes = sep.m_bytes;
            int sepOffset = sep.m_offset;
            int p = 0;
            while (true) {
                int n2 = n;
                n = n2 - 1;
                if (n2 > 1) {
                    System.arraycopy(sBytes, sOffset, bytes, p, l);
                    p += l;
                    if (lsep > 0) {
                        System.arraycopy(sepBytes, sepOffset, bytes, p, lsep);
                        p += lsep;
                    }
                } else {
                    System.arraycopy(sBytes, sOffset, bytes, p, l);
                    return LuaString.valueUsing(bytes);
                }
            }
        }
    }

    static final class reverse extends OneArgFunction {
        reverse() {
        }

        @Override // luaj.lib.OneArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            LuaString s = arg.checkstring();
            int n = s.length();
            if (n == 0) {
                return EMPTYSTRING;
            }
            byte[] b = new byte[n];
            byte[] src = s.m_bytes;
            int j = n - 1;
            int n2 = n + s.m_offset;
            int i = s.m_offset;
            int j2 = j;
            while (i < n2) {
                b[j2] = src[i];
                i++;
                j2--;
            }
            return LuaString.valueUsing(b);
        }
    }

    static final class sub extends VarArgFunction {
        sub() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaString s = args.checkstring(1);
            int l = s.length();
            int start = StringLib.posrelat(args.checkint(2), l);
            int end = StringLib.posrelat(args.optint(3, -1), l);
            if (start < 1) {
                start = 1;
            }
            if (end > l) {
                end = l;
            }
            return start <= end ? s.substring(start - 1, end) : EMPTYSTRING;
        }
    }

    static final class upper extends OneArgFunction {
        upper() {
        }

        @Override // luaj.lib.OneArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            return valueOf(arg.checkjstring().toUpperCase(Locale.US));
        }
    }

    static Varargs str_find_aux(Varargs args, boolean find2) throws Throwable {
        LuaString s = args.checkstring(1);
        LuaString pat = args.checkstring(2);
        int slen = s.length();
        int init = posrelat(args.optint(3, 1), slen);
        if (init < 1) {
            init = 1;
        } else if (init > slen + 1) {
            return NIL;
        }
        int init2 = init - 1;
        boolean fastMatch = find2 && (args.arg(4).toboolean() || pat.indexOfAny(SPECIALS) == -1);
        if (fastMatch) {
            int result = s.indexOf(pat, init2);
            if (result != -1) {
                return varargsOf(valueOf(result + 1), valueOf(pat.length() + result));
            }
        } else {
            MatchState ms = new MatchState(args, s, pat);
            boolean anchor = false;
            int poff = 0;
            if (pat.length() > 0 && pat.luaByte(0) == 94) {
                anchor = true;
                poff = 1;
            }
            int soff = init2;
            while (true) {
                ms.reset();
                int res = ms.match(soff, poff);
                if (res != -1) {
                    if (find2) {
                        return varargsOf(valueOf(soff + 1), valueOf(res), ms.push_captures(false, soff, res));
                    }
                    return ms.push_captures(true, soff, res);
                }
                int soff2 = soff + 1;
                if (soff >= slen || anchor) {
                    break;
                }
                soff = soff2;
            }
        }
        return NIL;
    }

    static int posrelat(int pos, int len2) {
        return pos >= 0 ? pos : len2 + pos + 1;
    }

    static {
        for (int i = 0; i < 128; i++) {
            char c = (char) i;
            CHAR_TABLE[i] = (byte) (((c < ' ' || c == 127) ? 64 : 0) | (Character.isLowerCase(c) ? 2 : 0) | (Character.isDigit(c) ? 8 : 0) | (Character.isUpperCase(c) ? 4 : 0));
            if ((c >= 'a' && c <= 'f') || ((c >= 'A' && c <= 'F') || (c >= '0' && c <= '9'))) {
                byte[] bArr = CHAR_TABLE;
                bArr[i] = (byte) (bArr[i] | MASK_HEXDIGIT);
            }
            if ((c >= '!' && c <= '/') || ((c >= ':' && c <= '@') || ((c >= '[' && c <= '`') || (c >= '{' && c <= '~')))) {
                byte[] bArr2 = CHAR_TABLE;
                bArr2[i] = (byte) (bArr2[i] | 16);
            }
            if ((CHAR_TABLE[i] & Kzstr) != 0) {
                byte[] bArr3 = CHAR_TABLE;
                bArr3[i] = (byte) (bArr3[i] | 1);
            }
        }
        CHAR_TABLE[32] = 32;
        byte[] bArr4 = CHAR_TABLE;
        bArr4[13] = (byte) (bArr4[13] | 32);
        byte[] bArr5 = CHAR_TABLE;
        bArr5[10] = (byte) (bArr5[10] | 32);
        byte[] bArr6 = CHAR_TABLE;
        bArr6[9] = (byte) (bArr6[9] | 32);
        byte[] bArr7 = CHAR_TABLE;
        bArr7[11] = (byte) (bArr7[11] | 32);
        byte[] bArr8 = CHAR_TABLE;
        bArr8[12] = (byte) (bArr8[12] | 32);
    }

    static class MatchState {
        final Varargs args;
        final LuaString p;
        final LuaString s;
        int level = 0;
        int[] cinit = new int[32];
        int[] clen = new int[32];
        int matchdepth = StringLib.MAXCCALLS;

        MatchState(Varargs args, LuaString s, LuaString pattern) {
            this.s = s;
            this.p = pattern;
            this.args = args;
        }

        void reset() {
            this.level = 0;
            this.matchdepth = StringLib.MAXCCALLS;
        }

        private void add_s(Buffer lbuf, LuaString news, int soff, int e) {
            String str;
            int l = news.length();
            int i = 0;
            while (i < l) {
                byte b = (byte) news.luaByte(i);
                if (b != 37) {
                    lbuf.append(b);
                } else {
                    i++;
                    byte b2 = (byte) (i < l ? news.luaByte(i) : 0);
                    if (!Character.isDigit((char) b2)) {
                        if (b2 != 37) {
                            StringBuilder sb = new StringBuilder("invalid use of '%' in replacement string: after '%' must be '0'-'9' or '%', but found ");
                            if (i < l) {
                                str = "symbol '" + ((char) b2) + "' with code " + ((int) b2) + " at pos " + (i + 1);
                            } else {
                                str = "end of string";
                            }
                            StringLib.error(sb.append(str).toString());
                        }
                        lbuf.append(b2);
                    } else if (b2 == 48) {
                        lbuf.append(this.s.substring(soff, e));
                    } else {
                        lbuf.append(push_onecapture(b2 - 49, soff, e).strvalue());
                    }
                }
                i++;
            }
        }

        public void add_value(Buffer lbuf, int soffset, int end, LuaValue repl) {
            LuaValue repl2;
            switch (repl.type()) {
                case 3:
                case 4:
                    add_s(lbuf, repl.strvalue(), soffset, end);
                    return;
                case 5:
                    repl2 = repl.get(push_onecapture(0, soffset, end));
                    break;
                case 6:
                    repl2 = repl.invoke(push_captures(true, soffset, end)).arg1();
                    break;
                default:
                    StringLib.error("bad argument: string/function/table expected");
                    return;
            }
            if (!repl2.toboolean()) {
                repl2 = this.s.substring(soffset, end);
            } else if (!repl2.isstring()) {
                StringLib.error("invalid replacement value (a " + repl2.typename() + ')');
            }
            lbuf.append(repl2.strvalue());
        }

        Varargs push_captures(boolean wholeMatch, int soff, int end) {
            int nlevels = (this.level == 0 && wholeMatch) ? 1 : this.level;
            switch (nlevels) {
                case 0:
                    return StringLib.NONE;
                case 1:
                    return push_onecapture(0, soff, end);
                default:
                    LuaValue[] v = new LuaValue[nlevels];
                    for (int i = 0; i < nlevels; i++) {
                        v[i] = push_onecapture(i, soff, end);
                    }
                    return StringLib.varargsOf(v);
            }
        }

        private LuaValue push_onecapture(int i, int soff, int end) {
            if (i >= this.level) {
                if (i == 0) {
                    return this.s.substring(soff, end);
                }
                return StringLib.error("invalid capture index %" + (i + 1));
            }
            int l = this.clen[i];
            if (l == -1) {
                return StringLib.error("unfinished capture");
            }
            if (l == -2) {
                return StringLib.valueOf(this.cinit[i] + 1);
            }
            int begin = this.cinit[i];
            return this.s.substring(begin, begin + l);
        }

        private int check_capture(int l) {
            int l2 = l - 49;
            if (l2 < 0 || l2 >= this.level || this.clen[l2] == -1) {
                StringLib.error("invalid capture index %" + (l2 + 1));
            }
            return l2;
        }

        private int capture_to_close() {
            int level = this.level;
            for (int level2 = level - 1; level2 >= 0; level2--) {
                if (this.clen[level2] == -1) {
                    return level2;
                }
            }
            StringLib.error("invalid pattern capture");
            return 0;
        }

        int classend(int poffset) {
            int poffset2 = poffset + 1;
            switch (this.p.luaByte(poffset)) {
                case 37:
                    if (poffset2 == this.p.length()) {
                        StringLib.error("malformed pattern (ends with '%')");
                    }
                    return poffset2 + 1;
                case 91:
                    int poffset3 = (poffset2 == this.p.length() || this.p.luaByte(poffset2) != 94) ? poffset2 : poffset2 + 1;
                    while (true) {
                        if (poffset3 == this.p.length()) {
                            StringLib.error("malformed pattern (missing ']')");
                        }
                        int poffset4 = poffset3 + 1;
                        poffset3 = (this.p.luaByte(poffset3) != 37 || poffset4 >= this.p.length()) ? poffset4 : poffset4 + 1;
                        if (poffset3 != this.p.length() && this.p.luaByte(poffset3) == 93) {
                            return poffset3 + 1;
                        }
                    }
                default:
                    return poffset2;
            }
        }

        static boolean match_class(int c, int cl) {
            boolean res;
            char lcl = Character.toLowerCase((char) cl);
            int cdata = StringLib.CHAR_TABLE[c];
            switch (lcl) {
                case 'a':
                    res = (cdata & 1) != 0;
                    break;
                case 'b':
                case 'e':
                case 'f':
                case 'h':
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                case Message.ERROR_EPERM_ON_ATTACH_DEFEND /* 106 */:
                case Message.ERROR_SPEEDHACK_FAIL /* 107 */:
                case 'm':
                case 'n':
                case 'o':
                case 'q':
                case 'r':
                case 't':
                case 'v':
                case 'y':
                default:
                    return cl == c;
                case 'c':
                    res = (cdata & 64) != 0;
                    break;
                case Message.ERROR_FATAL /* 100 */:
                    res = (cdata & 8) != 0;
                    break;
                case 'g':
                    res = (cdata & 25) != 0;
                    break;
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    res = (cdata & 2) != 0;
                    break;
                case 'p':
                    res = (cdata & 16) != 0;
                    break;
                case 's':
                    res = (cdata & 32) != 0;
                    break;
                case 'u':
                    res = (cdata & 4) != 0;
                    break;
                case 'w':
                    res = (cdata & 9) != 0;
                    break;
                case 'x':
                    res = (cdata & AddressItem.FLAG_ALL_OTHERS) != 0;
                    break;
                case 'z':
                    res = c == 0;
                    break;
            }
            if (lcl != cl) {
                res = !res;
            }
            return res;
        }

        boolean matchbracketclass(int c, int poff, int ec) {
            boolean sig = true;
            if (this.p.luaByte(poff + 1) == 94) {
                sig = false;
                poff++;
            }
            while (true) {
                poff++;
                if (poff < ec) {
                    if (this.p.luaByte(poff) == 37) {
                        poff++;
                        if (match_class(c, this.p.luaByte(poff))) {
                            return sig;
                        }
                    } else if (this.p.luaByte(poff + 1) == 45 && poff + 2 < ec) {
                        poff += 2;
                        if (this.p.luaByte(poff - 2) <= c && c <= this.p.luaByte(poff)) {
                            return sig;
                        }
                    } else if (this.p.luaByte(poff) == c) {
                        return sig;
                    }
                } else {
                    boolean sig2 = !sig;
                    return sig2;
                }
            }
        }

        boolean singlematch(int c, int poff, int ep) {
            switch (this.p.luaByte(poff)) {
                case 37:
                    break;
                case 46:
                    break;
                case 91:
                    break;
                default:
                    if (this.p.luaByte(poff) == c) {
                    }
                    break;
            }
            return true;
        }

        /* JADX WARN: Removed duplicated region for block: B:112:0x0155 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:113:0x016d A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:114:0x0187 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:115:0x0194 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:116:0x01a5 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:121:0x0054 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:14:0x0036 A[Catch: all -> 0x01b8, TryCatch #1 {all -> 0x01b8, blocks: (B:6:0x0010, B:10:0x0021, B:11:0x0027, B:12:0x002a, B:14:0x0036, B:17:0x0043, B:19:0x004b, B:87:0x0173, B:91:0x0187, B:95:0x0198, B:97:0x01a5, B:24:0x0060, B:26:0x0068, B:28:0x0072, B:31:0x0082, B:34:0x0090, B:37:0x009e, B:39:0x00a6, B:40:0x00ab, B:41:0x00b3, B:42:0x00b6, B:44:0x00c5, B:48:0x00d5, B:53:0x00ea, B:55:0x00f2, B:58:0x0101, B:61:0x0108, B:64:0x0111, B:66:0x0119, B:70:0x0133, B:69:0x012a, B:57:0x00fc, B:77:0x014d, B:79:0x0155), top: B:108:0x0010 }] */
        /* JADX WARN: Removed duplicated region for block: B:19:0x004b A[Catch: all -> 0x01b8, TRY_LEAVE, TryCatch #1 {all -> 0x01b8, blocks: (B:6:0x0010, B:10:0x0021, B:11:0x0027, B:12:0x002a, B:14:0x0036, B:17:0x0043, B:19:0x004b, B:87:0x0173, B:91:0x0187, B:95:0x0198, B:97:0x01a5, B:24:0x0060, B:26:0x0068, B:28:0x0072, B:31:0x0082, B:34:0x0090, B:37:0x009e, B:39:0x00a6, B:40:0x00ab, B:41:0x00b3, B:42:0x00b6, B:44:0x00c5, B:48:0x00d5, B:53:0x00ea, B:55:0x00f2, B:58:0x0101, B:61:0x0108, B:64:0x0111, B:66:0x0119, B:70:0x0133, B:69:0x012a, B:57:0x00fc, B:77:0x014d, B:79:0x0155), top: B:108:0x0010 }] */
        /* JADX WARN: Removed duplicated region for block: B:83:0x0167  */
        /* JADX WARN: Removed duplicated region for block: B:84:0x016a  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        int match(int r13, int r14) throws java.lang.Throwable {
            /*
                Method dump skipped, instruction units count: 496
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: luaj.lib.StringLib.MatchState.match(int, int):int");
        }

        int max_expand(int soff, int poff, int ep) throws Throwable {
            int i = 0;
            while (soff + i < this.s.length() && singlematch(this.s.luaByte(soff + i), poff, ep)) {
                i++;
            }
            while (i >= 0) {
                int res = match(soff + i, ep + 1);
                if (res == -1) {
                    i--;
                } else {
                    return res;
                }
            }
            return -1;
        }

        int min_expand(int soff, int poff, int ep) throws Throwable {
            while (true) {
                int res = match(soff, ep + 1);
                if (res == -1) {
                    if (soff >= this.s.length() || !singlematch(this.s.luaByte(soff), poff, ep)) {
                        break;
                    }
                    soff++;
                } else {
                    return res;
                }
            }
            return -1;
        }

        int start_capture(int soff, int poff, int what) throws Throwable {
            int level = this.level;
            if (level >= 32) {
                StringLib.error("too many captures");
            }
            this.cinit[level] = soff;
            this.clen[level] = what;
            this.level = level + 1;
            int res = match(soff, poff);
            if (res == -1) {
                this.level--;
            }
            return res;
        }

        int end_capture(int soff, int poff) throws Throwable {
            int l = capture_to_close();
            this.clen[l] = soff - this.cinit[l];
            int res = match(soff, poff);
            if (res == -1) {
                this.clen[l] = -1;
            }
            return res;
        }

        int match_capture(int soff, int l) {
            int l2 = check_capture(l);
            int len = this.clen[l2];
            if (this.s.length() - soff < len || !LuaString.equals(this.s, this.cinit[l2], this.s, soff, len)) {
                return -1;
            }
            return soff + len;
        }

        int matchbalance(int soff, int poff) {
            int b;
            int plen = this.p.length();
            if (poff == plen || poff + 1 == plen) {
                StringLib.error("malformed pattern (missing arguments to '%b')");
            }
            int slen = this.s.length();
            if (soff >= slen || this.s.luaByte(soff) != (b = this.p.luaByte(poff))) {
                return -1;
            }
            int e = this.p.luaByte(poff + 1);
            int cont = 1;
            while (true) {
                soff++;
                if (soff >= slen) {
                    return -1;
                }
                if (this.s.luaByte(soff) == e) {
                    cont--;
                    if (cont == 0) {
                        return soff + 1;
                    }
                } else if (this.s.luaByte(soff) == b) {
                    cont++;
                }
            }
        }
    }

    static final class PackState {
        final String fmt;
        final byte size_t;
        boolean islittle = true;
        int maxalign = 1;
        int pos = 0;
        char last = 0;
        int align = 0;
        int ntoalign = 0;
        int size = 0;
        final byte[] buff = new byte[8];
        final ByteBuffer byteBuffer = ByteBuffer.wrap(this.buff);

        public PackState(String fmt) {
            this.fmt = fmt;
            ProcessList.ProcessInfo pi = MainService.instance.processInfo;
            this.size_t = (pi == null || !pi.x64) ? (byte) 4 : (byte) 8;
        }

        public String toString() {
            return "PackState [size_t=" + ((int) this.size_t) + ", islittle=" + this.islittle + ", maxalign=" + this.maxalign + ", fmt=" + this.fmt + ", pos=" + this.pos + ", last=" + this.last + ", align=" + this.align + ", ntoalign=" + this.ntoalign + ", size=" + this.size + "]";
        }
    }

    static int getnum(PackState state, int df, boolean limit) {
        char c;
        String str = state.fmt;
        int len2 = str.length();
        int pos = state.pos;
        if (pos < len2 && (c = str.charAt(pos)) <= '9' && c >= '0') {
            df = 0;
            do {
                df = (df * 10) + (c - '0');
                pos++;
                if (pos >= len2 || (c = str.charAt(pos)) > '9' || c < '0') {
                    break;
                }
            } while (df <= 214748363);
            state.pos = pos;
        }
        if (limit && (df > 8 || df <= 0)) {
            error(ToolsLight.stringFormat("integral size (%d) out of limits [1,%d] at pos %d", Integer.valueOf(df), 8, Integer.valueOf(state.pos)));
        }
        return df;
    }

    /* JADX WARN: Finally extract failed */
    static byte getoption(PackState state) {
        int i = 4;
        String str = state.fmt;
        int i2 = state.pos;
        state.pos = i2 + 1;
        char opt = str.charAt(i2);
        state.last = opt;
        try {
            switch (opt) {
                case ' ':
                    break;
                case '!':
                    state.maxalign = getnum(state, state.size_t, true);
                    break;
                case '<':
                case '=':
                    state.islittle = true;
                    break;
                case '>':
                    state.islittle = false;
                    break;
                case LasmConstants.IS_VARARG /* 66 */:
                    state.size = 1;
                    state.align = opt != 'c' ? 1 >= 8 ? 4 : 1 : 1;
                    return (byte) 2;
                case LasmConstants.FUNC /* 72 */:
                    state.size = 2;
                    state.align = opt != 'c' ? 2 >= 8 ? 4 : 2 : 1;
                    return (byte) 2;
                case LasmConstants.END /* 73 */:
                    int size = getnum(state, 4, true);
                    state.size = size;
                    state.align = opt != 'c' ? size >= 8 ? 4 : size : 1;
                    return (byte) 2;
                case LasmConstants.EOL /* 74 */:
                    state.size = 8;
                    state.align = opt != 'c' ? 8 >= 8 ? 4 : 8 : 1;
                    return (byte) 2;
                case LasmConstants.LABEL /* 76 */:
                    state.size = 8;
                    state.align = opt != 'c' ? 8 >= 8 ? 4 : 8 : 1;
                    return (byte) 2;
                case LasmConstants.FLOAT /* 84 */:
                    int size2 = state.size_t;
                    state.size = size2;
                    state.align = opt != 'c' ? size2 >= 8 ? 4 : size2 : 1;
                    return (byte) 2;
                case 'X':
                    state.size = 0;
                    state.align = opt != 'c' ? 0 >= 8 ? 4 : 0 : 1;
                    return (byte) 8;
                case 'b':
                    state.size = 1;
                    if (opt == 'c' || 1 < 8) {
                        i = 1;
                    }
                    state.align = i;
                    return (byte) 1;
                case 'c':
                    int size3 = getnum(state, -1, false);
                    if (size3 == -1) {
                        error("missing size for format option 'c' at pos " + state.pos);
                    }
                    state.size = size3;
                    state.align = opt != 'c' ? size3 >= 8 ? 4 : size3 : 1;
                    return (byte) 4;
                case Message.ERROR_FATAL /* 100 */:
                    state.size = 8;
                    state.align = opt != 'c' ? 8 >= 8 ? 4 : 8 : 1;
                    return Kdouble;
                case 'f':
                    state.size = 4;
                    state.align = opt != 'c' ? 4 >= 8 ? 4 : 4 : 1;
                    return (byte) 3;
                case 'h':
                    state.size = 2;
                    if (opt == 'c') {
                        i = 1;
                    } else if (2 < 8) {
                        i = 2;
                    }
                    state.align = i;
                    return (byte) 1;
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    int size4 = getnum(state, 4, true);
                    state.size = size4;
                    if (opt == 'c') {
                        i = 1;
                    } else if (size4 < 8) {
                        i = size4;
                    }
                    state.align = i;
                    return (byte) 1;
                case Message.ERROR_EPERM_ON_ATTACH_DEFEND /* 106 */:
                    state.size = 8;
                    if (opt == 'c') {
                        i = 1;
                    } else if (8 < 8) {
                        i = 8;
                    }
                    state.align = i;
                    return (byte) 1;
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    state.size = 8;
                    if (opt == 'c') {
                        i = 1;
                    } else if (8 < 8) {
                        i = 8;
                    }
                    state.align = i;
                    return (byte) 1;
                case 'n':
                    state.size = 9;
                    state.align = opt != 'c' ? 9 >= 8 ? 4 : 9 : 1;
                    return Klua;
                case 's':
                    int size5 = getnum(state, state.size_t, true);
                    state.size = size5;
                    state.align = opt != 'c' ? size5 >= 8 ? 4 : size5 : 1;
                    return Kstring;
                case 'x':
                    state.size = 1;
                    state.align = opt != 'c' ? 1 >= 8 ? 4 : 1 : 1;
                    return Kpadding;
                case 'z':
                    state.size = 0;
                    state.align = opt != 'c' ? 0 >= 8 ? 4 : 0 : 1;
                    return Kzstr;
                default:
                    error(ToolsLight.stringFormat("invalid format option '%c' at pos %d", Character.valueOf(opt), Integer.valueOf(state.pos)));
                    break;
            }
            state.size = 0;
            state.align = opt != 'c' ? 0 >= 8 ? 4 : 0 : 1;
            return (byte) 0;
        } catch (Throwable th) {
            state.size = 0;
            state.align = opt != 'c' ? 0 >= 8 ? 4 : 0 : 1;
            throw th;
        }
    }

    static byte getdetails(PackState state, int totalsize) {
        byte opt = getoption(state);
        int align = state.align;
        if (opt == 8) {
            if (state.fmt.length() <= state.pos) {
                argerror(1, "no next option for option 'X' at pos " + state.pos);
            }
            if (getoption(state) == 4 || (align = state.align) == 0) {
                argerror(1, ToolsLight.stringFormat("invalid next option '%c' for option 'X' at pos %d", Character.valueOf(state.last), Integer.valueOf(state.pos)));
            }
            state.size = 0;
        }
        if (align <= 1 || opt == 4) {
            state.ntoalign = 0;
        } else {
            if (align > state.maxalign) {
                align = state.maxalign;
                state.align = align;
            }
            if (((align - 1) & align) != 0) {
                argerror(1, ToolsLight.stringFormat("format option '%c' asks for alignment not power of 2 at pos %d", Character.valueOf(state.last), Integer.valueOf(state.pos)));
            }
            state.ntoalign = (align - ((align - 1) & totalsize)) & (align - 1);
        }
        return opt;
    }

    static void packint(PackState state, Buffer b, long n) {
        state.byteBuffer.order(state.islittle ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN).putLong(0, n);
        b.append(state.buff, state.islittle ? 0 : 8 - state.size, state.size);
    }

    static final class pack extends VarArgFunction {
        pack() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            String fmt = args.checkjstring(1);
            PackState state = new PackState(fmt);
            Buffer b = new Buffer();
            int arg = 1;
            int totalsize = 0;
            int len = fmt.length();
            while (state.pos < len) {
                int opt = StringLib.getdetails(state, totalsize);
                int ntoalign = state.ntoalign;
                int size = state.size;
                totalsize += ntoalign + size;
                if (ntoalign > 0) {
                    b.append(new byte[ntoalign], 0, ntoalign);
                }
                arg++;
                switch (opt) {
                    case 1:
                    case 2:
                        long n = args.checklong(arg);
                        if (size < 8) {
                            if (opt == 1) {
                                long lim = 1 << ((size * 8) - 1);
                                argcheck((-lim) <= n && n < lim, arg, "integer overflow");
                            } else {
                                argcheck(((((1 << (size * 8)) - 1) ^ (-1)) & n) == 0, arg, "unsigned overflow");
                            }
                        }
                        StringLib.packint(state, b, n);
                        continue;
                    case 3:
                        StringLib.packint(state, b, Float.floatToRawIntBits((float) args.checkdouble(arg)));
                        continue;
                    case 4:
                        LuaString str = args.checkstring(arg);
                        int slen = str.length();
                        argcheck(slen <= size, arg, "string longer (" + slen + ") than given size (" + size + ")");
                        b.append(str);
                        int slen2 = size - slen;
                        if (slen2 > 0) {
                            b.append(new byte[slen2], 0, slen2);
                        } else {
                            continue;
                        }
                        break;
                    case 5:
                        LuaString str2 = args.checkstring(arg);
                        int slen3 = str2.length();
                        argcheck(size >= state.size_t || ((long) slen3) < (1 << (size * 8)), arg, "string length (" + slen3 + ") does not fit in given size (" + size + " bytes)");
                        StringLib.packint(state, b, slen3);
                        b.append(str2);
                        totalsize += slen3;
                        continue;
                    case 6:
                        LuaString str3 = args.checkstring(arg);
                        int slen4 = str3.length();
                        argcheck(str3.tojstring().indexOf(0) == -1, arg, "string contains zeros");
                        b.append(str3);
                        b.append((byte) 0);
                        totalsize += slen4 + 1;
                        continue;
                    case 7:
                        b.append((byte) 0);
                        break;
                    case 9:
                        StringLib.packint(state, b, Double.doubleToRawLongBits(args.checkdouble(arg)));
                        continue;
                    case 10:
                        LuaNumber n2 = args.checknumber(arg);
                        state.size = 8;
                        if (n2 instanceof LuaLong) {
                            StringLib.packint(state, b, n2.tolong());
                        } else {
                            StringLib.packint(state, b, Double.doubleToRawLongBits(n2.todouble()));
                        }
                        state.size = 1;
                        StringLib.packint(state, b, n2 instanceof LuaLong ? 1 : 0);
                        continue;
                }
                arg--;
            }
            return b.tostring();
        }
    }

    static final class packsize extends VarArgFunction {
        packsize() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            String fmt = args.checkjstring(1);
            PackState state = new PackState(fmt);
            int totalsize = 0;
            int len = fmt.length();
            while (state.pos < len) {
                int opt = StringLib.getdetails(state, totalsize);
                int size = state.size + state.ntoalign;
                argcheck(totalsize <= Integer.MAX_VALUE - size, 1, "format result too large at pos " + state.pos);
                totalsize += size;
                switch (opt) {
                    case 5:
                    case 6:
                        argerror(1, ToolsLight.stringFormat("variable-length format option '%c' at pos %d", Character.valueOf(state.last), Integer.valueOf(state.pos)));
                        break;
                }
            }
            return LuaValue.valueOf(totalsize);
        }
    }

    static long unpackint(PackState state, LuaString str, int pos, boolean issigned) {
        int size = state.size;
        if (size == 1) {
            long n = str.m_bytes[str.m_offset + pos];
            return !issigned ? n & 255 : n;
        }
        byte[] buff = state.buff;
        int offset = 0;
        if (size < 8) {
            if (state.islittle) {
                int i = size - 1;
                while (true) {
                    i++;
                    if (i >= 8) {
                        break;
                    }
                    buff[i] = 0;
                }
            } else {
                int i2 = 8 - size;
                offset = i2;
                while (true) {
                    i2--;
                    if (i2 < 0) {
                        break;
                    }
                    buff[i2] = 0;
                }
            }
        }
        str.copyInto(pos, buff, offset, size);
        long n2 = state.byteBuffer.order(state.islittle ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN).getLong(0);
        if (size < 8 && issigned) {
            long mask = 1 << ((size * 8) - 1);
            return (n2 ^ mask) - mask;
        }
        return n2;
    }

    static final class unpack extends VarArgFunction {
        unpack() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            String fmt = args.checkjstring(1);
            LuaString data = args.checkstring(2);
            int ld = data.length();
            int pos = StringLib.posrelat(args.optint(3, 1), ld) - 1;
            argcheck(pos >= 0 && pos <= ld, 3, "initial position (" + (pos + 1) + ") out of string (length = " + ld + ")");
            PackState state = new PackState(fmt);
            int len = fmt.length();
            ArrayList<LuaValue> list = new ArrayList<>();
            while (state.pos < len) {
                int opt = StringLib.getdetails(state, pos);
                int ntoalign = state.ntoalign;
                int size = state.size;
                if (pos + ntoalign + size > ld) {
                    argerror(2, ToolsLight.stringFormat("data string too short (%d) for option '%c' (need %d) at pos %d", Integer.valueOf(ld), Character.valueOf(state.last), Integer.valueOf(pos + ntoalign + size), Integer.valueOf(state.pos)));
                }
                int pos2 = pos + ntoalign;
                switch (opt) {
                    case 1:
                    case 2:
                        list.add(LuaLong.valueOf(StringLib.unpackint(state, data, pos2, opt == 1)));
                        break;
                    case 3:
                        float f = Float.intBitsToFloat((int) StringLib.unpackint(state, data, pos2, false));
                        list.add(LuaDouble.valueOf(f));
                        break;
                    case 4:
                        list.add(data.substring(pos2, pos2 + size));
                        break;
                    case 5:
                        int slen = (int) StringLib.unpackint(state, data, pos2, false);
                        argcheck((pos2 + slen) + size <= ld, 2, "data string too short (" + ld + " but need " + (pos2 + slen + size) + ") at pos " + state.pos);
                        list.add(data.substring(pos2 + size, pos2 + size + slen));
                        pos2 += slen;
                        break;
                    case 6:
                        int slen2 = data.indexOf((byte) 0, pos2);
                        argcheck(slen2 != -1, 2, ToolsLight.stringFormat("zero char not found for option '%c' at pos %d", Character.valueOf(state.last), Integer.valueOf(state.pos)));
                        list.add(data.substring(pos2, slen2));
                        pos2 = slen2 + 1;
                        break;
                    case 9:
                        double d = Double.longBitsToDouble(StringLib.unpackint(state, data, pos2, false));
                        list.add(LuaDouble.valueOf(d));
                        break;
                    case 10:
                        state.size = 8;
                        long n = StringLib.unpackint(state, data, pos2, false);
                        state.size = 1;
                        long type = StringLib.unpackint(state, data, pos2 + 8, false);
                        if (type == 1) {
                            list.add(LuaLong.valueOf(n));
                        } else {
                            list.add(LuaDouble.valueOf(Double.longBitsToDouble(n)));
                        }
                        break;
                }
                pos = pos2 + size;
            }
            list.add(LuaValue.valueOf(pos + 1));
            return varargsOf((LuaValue[]) list.toArray(new LuaValue[list.size()]));
        }
    }

    public static void main(String[] args) throws Throwable {
    }
}
