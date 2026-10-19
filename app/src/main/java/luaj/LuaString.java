package luaj;

import android.ext.AddressItem;
import android.ext.Message;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class LuaString extends LuaValue {
    static final int RECENT_STRINGS_CACHE_SIZE = 128;
    static final int RECENT_STRINGS_MAX_LENGTH = 32;
    private static final boolean[] numeral = new boolean[128];
    public static LuaValue s_metatable;
    public final byte[] m_bytes;
    public final int m_hashcode;
    public final int m_length;
    public final int m_offset;
    private LuaValue numValue;

    private static final class RecentShortStrings {
        static final LuaString[] recent_short_strings = new LuaString[128];

        private RecentShortStrings() {
        }
    }

    public static LuaString valueOf(String string) {
        LuaString special = getSpecial(string);
        if (special == null) {
            byte[] b = string.getBytes();
            return valueUsing(b, 0, b.length);
        }
        return special;
    }

    private static LuaString getSpecial(String string) {
        int len = string.length();
        if (len == 0) {
            return EMPTYSTRING;
        }
        if (len == 1) {
            char ch = string.charAt(0);
            if (ch == '0') {
                return ZEROSTRING;
            }
            if (ch == '1') {
                return ONESTRING;
            }
        }
        return null;
    }

    static LuaString rawValueOf(String string) {
        byte[] bytes = string.getBytes();
        return new LuaString(bytes, 0, bytes.length);
    }

    public static LuaString valueOf(byte[] bytes, int off, int len) {
        LuaString special = getSpecial(bytes, off, len);
        if (special == null) {
            if (len > 32) {
                return valueFromCopy(bytes, off, len);
            }
            int hash = hashCode(bytes, off, len);
            int bucket = hash & AddressItem.FLAG_AUTO;
            LuaString t = RecentShortStrings.recent_short_strings[bucket];
            if (t != null && t.m_hashcode == hash && t.byteseq(bytes, off, len)) {
                return t;
            }
            LuaString s = valueFromCopy(bytes, off, len);
            RecentShortStrings.recent_short_strings[bucket] = s;
            return s;
        }
        return special;
    }

    private static LuaString valueFromCopy(byte[] bytes, int off, int len) {
        byte[] copy = new byte[len];
        System.arraycopy(bytes, off, copy, 0, len);
        return new LuaString(copy, 0, len);
    }

    private static LuaString getSpecial(byte[] bytes, int off, int len) {
        if (len == 0) {
            return EMPTYSTRING;
        }
        if (len == 1) {
            if (bytes[off] == 48) {
                return ZEROSTRING;
            }
            if (bytes[off] == 49) {
                return ONESTRING;
            }
        }
        return null;
    }

    public static LuaString valueUsing(byte[] bytes, int off, int len) {
        LuaString special = getSpecial(bytes, off, len);
        if (special == null) {
            if (bytes.length > 32) {
                return new LuaString(bytes, off, len);
            }
            int hash = hashCode(bytes, off, len);
            int bucket = hash & AddressItem.FLAG_AUTO;
            LuaString t = RecentShortStrings.recent_short_strings[bucket];
            if (t != null && t.m_hashcode == hash && t.byteseq(bytes, off, len)) {
                return t;
            }
            LuaString s = new LuaString(bytes, off, len);
            RecentShortStrings.recent_short_strings[bucket] = s;
            return s;
        }
        return special;
    }

    public static LuaString valueOf(char[] bytes) {
        return valueOf(bytes, 0, bytes.length);
    }

    public static LuaString valueOf(char[] bytes, int off, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[i] = (byte) bytes[i + off];
        }
        return valueUsing(b, 0, len);
    }

    public static LuaString valueOf(byte[] bytes) {
        return valueOf(bytes, 0, bytes.length);
    }

    public static LuaString valueUsing(byte[] bytes) {
        return valueUsing(bytes, 0, bytes.length);
    }

    private LuaString(byte[] bytes, int offset, int length) {
        this.numValue = null;
        this.m_bytes = bytes;
        this.m_offset = offset;
        this.m_length = length;
        this.m_hashcode = hashCode(bytes, offset, length);
    }

    /* synthetic */ LuaString(byte[] bArr, int i, int i2, LuaString luaString) {
        this(bArr, i, i2);
    }

    @Override // luaj.LuaValue
    public boolean isstring() {
        return true;
    }

    @Override // luaj.LuaValue
    public LuaValue getmetatable() {
        return s_metatable;
    }

    @Override // luaj.LuaValue
    public int type() {
        return 4;
    }

    @Override // luaj.LuaValue
    public String typename() {
        return "string";
    }

    @Override // luaj.LuaValue, luaj.Varargs
    public String tojstring() {
        return decodeAsUtf8(this.m_bytes, this.m_offset, this.m_length);
    }

    private LuaValue getNum() {
        LuaValue n = this.numValue;
        if (n == null) {
            LuaValue n2 = tonumber();
            this.numValue = n2;
            return n2;
        }
        return n;
    }

    @Override // luaj.LuaValue
    public LuaValue neg() {
        LuaValue n = getNum();
        return n.isnil() ? super.neg() : n.neg();
    }

    @Override // luaj.LuaValue
    public LuaValue bnot() {
        LuaValue n = getNum();
        return n.isnil() ? super.bnot() : n.bnot();
    }

    @Override // luaj.LuaValue
    public LuaValue add(LuaValue rhs) {
        LuaValue n = getNum();
        return n.isnil() ? super.add(rhs) : n.add(rhs);
    }

    @Override // luaj.LuaValue
    public LuaValue sub(LuaValue rhs) {
        LuaValue n = getNum();
        return n.isnil() ? super.sub(rhs) : n.sub(rhs);
    }

    @Override // luaj.LuaValue
    public LuaValue mul(LuaValue rhs) {
        LuaValue n = getNum();
        return n.isnil() ? super.mul(rhs) : n.mul(rhs);
    }

    @Override // luaj.LuaValue
    public LuaValue pow(LuaValue rhs) {
        LuaValue n = getNum();
        return n.isnil() ? super.pow(rhs) : n.pow(rhs);
    }

    @Override // luaj.LuaValue
    public LuaValue div(LuaValue rhs) {
        LuaValue n = getNum();
        return n.isnil() ? super.div(rhs) : n.div(rhs);
    }

    @Override // luaj.LuaValue
    public LuaValue idiv(LuaValue rhs) {
        LuaValue n = getNum();
        return n.isnil() ? super.idiv(rhs) : n.idiv(rhs);
    }

    @Override // luaj.LuaValue
    public LuaValue mod(LuaValue rhs) {
        LuaValue n = getNum();
        return n.isnil() ? super.mod(rhs) : n.mod(rhs);
    }

    @Override // luaj.LuaValue
    public LuaValue band(LuaValue rhs) {
        LuaValue n = getNum();
        return n.isnil() ? super.band(rhs) : n.band(rhs);
    }

    @Override // luaj.LuaValue
    public LuaValue bor(LuaValue rhs) {
        LuaValue n = getNum();
        return n.isnil() ? super.bor(rhs) : n.bor(rhs);
    }

    @Override // luaj.LuaValue
    public LuaValue bxor(LuaValue rhs) {
        LuaValue n = getNum();
        return n.isnil() ? super.bxor(rhs) : n.bxor(rhs);
    }

    @Override // luaj.LuaValue
    public LuaValue shl(LuaValue rhs) {
        LuaValue n = getNum();
        return n.isnil() ? super.shl(rhs) : n.shl(rhs);
    }

    @Override // luaj.LuaValue
    public LuaValue shr(LuaValue rhs) {
        LuaValue n = getNum();
        return n.isnil() ? super.shr(rhs) : n.shr(rhs);
    }

    @Override // luaj.LuaValue
    public boolean lt_b(LuaValue rhs) {
        return rhs.isstring() ? rhs.strcmp(this) > 0 : super.lt_b(rhs);
    }

    @Override // luaj.LuaValue
    public boolean lteq_b(LuaValue rhs) {
        return rhs.isstring() ? rhs.strcmp(this) >= 0 : super.lteq_b(rhs);
    }

    @Override // luaj.LuaValue
    public boolean gt_b(LuaValue rhs) {
        return rhs.isstring() ? rhs.strcmp(this) < 0 : super.gt_b(rhs);
    }

    @Override // luaj.LuaValue
    public boolean gteq_b(LuaValue rhs) {
        return rhs.isstring() ? rhs.strcmp(this) <= 0 : super.gteq_b(rhs);
    }

    @Override // luaj.LuaValue
    public LuaValue concat(LuaValue rhs) {
        return rhs.concatTo(this);
    }

    @Override // luaj.LuaValue
    public LuaValue concatTo(LuaNumber lhs) {
        return concatTo(lhs.strvalue());
    }

    @Override // luaj.LuaValue
    public LuaValue concatTo(LuaString lhs) {
        byte[] b = new byte[lhs.m_length + this.m_length];
        System.arraycopy(lhs.m_bytes, lhs.m_offset, b, 0, lhs.m_length);
        System.arraycopy(this.m_bytes, this.m_offset, b, lhs.m_length, this.m_length);
        return valueUsing(b, 0, b.length);
    }

    @Override // luaj.LuaValue
    public int strcmp(LuaValue lhs) {
        return -lhs.strcmp(this);
    }

    @Override // luaj.LuaValue
    public int strcmp(LuaString rhs) {
        int i = 0;
        for (int j = 0; i < this.m_length && j < rhs.m_length; j++) {
            if (this.m_bytes[this.m_offset + i] == rhs.m_bytes[rhs.m_offset + j]) {
                i++;
            } else {
                return this.m_bytes[this.m_offset + i] - rhs.m_bytes[rhs.m_offset + j];
            }
        }
        return this.m_length - rhs.m_length;
    }

    private LuaValue checkarith() {
        LuaValue n = getNum();
        if (n.isnil()) {
            aritherror();
        }
        return n;
    }

    @Override // luaj.LuaValue
    public int checkint() {
        LuaValue n = getNum();
        return n.isnil() ? super.checkint() : n.checkint();
    }

    @Override // luaj.LuaValue
    public LuaLong checkinteger() {
        LuaValue n = getNum();
        return n.isnil() ? super.checkinteger() : n.checkinteger();
    }

    @Override // luaj.LuaValue
    public long checklong() {
        LuaValue n = getNum();
        return n.isnil() ? super.checklong() : n.checklong();
    }

    @Override // luaj.LuaValue
    public double checkdouble() {
        LuaValue n = getNum();
        return n.isnil() ? super.checkdouble() : n.checkdouble();
    }

    @Override // luaj.LuaValue
    public LuaNumber checknumber() {
        LuaValue n = getNum();
        return n.isnil() ? super.checknumber() : n.checknumber();
    }

    @Override // luaj.LuaValue
    public LuaNumber checknumber(String msg) {
        LuaValue n = getNum();
        return n.isnil() ? super.checknumber(msg) : n.checknumber(msg);
    }

    @Override // luaj.LuaValue
    public boolean isnumber() {
        return getNum().isnumber();
    }

    @Override // luaj.LuaValue
    public boolean isint() {
        return getNum().isint();
    }

    @Override // luaj.LuaValue
    public boolean islong() {
        return getNum().islong();
    }

    @Override // luaj.LuaValue
    public boolean islongnumber() {
        return getNum().islongnumber();
    }

    @Override // luaj.LuaValue
    public byte tobyte() {
        return getNum().tobyte();
    }

    @Override // luaj.LuaValue
    public char tochar() {
        return getNum().tochar();
    }

    @Override // luaj.LuaValue
    public double todouble() {
        return getNum().todouble();
    }

    @Override // luaj.LuaValue
    public float tofloat() {
        return getNum().tofloat();
    }

    @Override // luaj.LuaValue
    public int toint() {
        return getNum().toint();
    }

    @Override // luaj.LuaValue
    public long tolong() {
        return getNum().tolong();
    }

    @Override // luaj.LuaValue
    public short toshort() {
        return getNum().toshort();
    }

    @Override // luaj.LuaValue
    public double optdouble(double defval) {
        return checkdouble();
    }

    @Override // luaj.LuaValue
    public int optint(int defval) {
        return checkint();
    }

    @Override // luaj.LuaValue
    public LuaLong optinteger(LuaLong defval) {
        return checkinteger();
    }

    @Override // luaj.LuaValue
    public long optlong(long defval) {
        return checklong();
    }

    @Override // luaj.LuaValue
    public LuaNumber optnumber(LuaNumber defval) {
        return checknumber();
    }

    @Override // luaj.LuaValue
    public LuaString optstring(LuaString defval) {
        return this;
    }

    @Override // luaj.LuaValue
    public LuaValue tostring() {
        return this;
    }

    @Override // luaj.LuaValue
    public String optjstring(String defval) {
        return tojstring();
    }

    @Override // luaj.LuaValue
    public LuaString strvalue() {
        return this;
    }

    public LuaString substring(int beginIndex, int endIndex) {
        int off = this.m_offset + beginIndex;
        int len = endIndex - beginIndex;
        if (len >= this.m_length / 2) {
            return valueUsing(this.m_bytes, off, len);
        }
        return valueOf(this.m_bytes, off, len);
    }

    public int hashCode() {
        return this.m_hashcode;
    }

    public static int hashCode(byte[] bytes, int offset, int length) {
        int h = length;
        int step = (length >> 5) + 1;
        for (int l1 = length; l1 >= step; l1 -= step) {
            h ^= ((h << 5) + (h >> 2)) + (bytes[(offset + l1) - 1] & 255);
        }
        return (h >>> 6) | (h << 26);
    }

    @Override // luaj.LuaValue
    public boolean equals(Object o) {
        if (o instanceof LuaString) {
            return raweq((LuaString) o);
        }
        return false;
    }

    @Override // luaj.LuaValue
    public boolean eq_b(LuaValue val) {
        return val.raweq(this);
    }

    @Override // luaj.LuaValue
    public boolean raweq(LuaValue val) {
        return val.raweq(this);
    }

    @Override // luaj.LuaValue
    public boolean raweq(LuaString s) {
        if (this == s) {
            return true;
        }
        if (s.m_length != this.m_length) {
            return false;
        }
        if (s.m_bytes == this.m_bytes && s.m_offset == this.m_offset) {
            return true;
        }
        if (s.hashCode() != hashCode()) {
            return false;
        }
        for (int i = 0; i < this.m_length; i++) {
            if (s.m_bytes[s.m_offset + i] != this.m_bytes[this.m_offset + i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(LuaString a, int i, LuaString b, int j, int n) {
        return equals(a.m_bytes, a.m_offset + i, b.m_bytes, b.m_offset + j, n);
    }

    private boolean byteseq(byte[] bytes, int off, int len) {
        return this.m_length == len && equals(this.m_bytes, this.m_offset, bytes, off, len);
    }

    public static boolean equals(byte[] a, int i, byte[] b, int j, int n) {
        int j2;
        int i2;
        if (a.length < i + n || b.length < j + n) {
            return false;
        }
        do {
            j2 = j;
            i2 = i;
            n--;
            if (n >= 0) {
                i = i2 + 1;
                j = j2 + 1;
            } else {
                return true;
            }
        } while (a[i2] == b[j2]);
        return false;
    }

    public void write(DataOutputStream writer, int i, int len) throws IOException {
        writer.write(this.m_bytes, this.m_offset + i, len);
    }

    @Override // luaj.LuaValue
    public LuaValue len() {
        return LuaLong.valueOf(this.m_length);
    }

    @Override // luaj.LuaValue
    public int length() {
        return this.m_length;
    }

    @Override // luaj.LuaValue
    public int rawlen() {
        return this.m_length;
    }

    public int luaByte(int index) {
        return this.m_bytes[this.m_offset + index] & 255;
    }

    public int charAt(int index) {
        if (index < 0 || index >= this.m_length) {
            throw new IndexOutOfBoundsException();
        }
        return luaByte(index);
    }

    @Override // luaj.LuaValue
    public String checkjstring() {
        return tojstring();
    }

    @Override // luaj.LuaValue
    public LuaString checkstring() {
        return this;
    }

    public InputStream toInputStream() {
        return new ByteArrayInputStream(this.m_bytes, this.m_offset, this.m_length);
    }

    public void copyInto(int strOffset, byte[] bytes, int arrayOffset, int len) {
        System.arraycopy(this.m_bytes, this.m_offset + strOffset, bytes, arrayOffset, len);
    }

    public int indexOfAny(LuaString accept) {
        int ilimit = this.m_offset + this.m_length;
        int jlimit = accept.m_offset + accept.m_length;
        for (int i = this.m_offset; i < ilimit; i++) {
            for (int j = accept.m_offset; j < jlimit; j++) {
                if (this.m_bytes[i] == accept.m_bytes[j]) {
                    return i - this.m_offset;
                }
            }
        }
        return -1;
    }

    public int indexOf(byte b, int start) {
        for (int i = start; i < this.m_length; i++) {
            if (this.m_bytes[this.m_offset + i] == b) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(LuaString s, int start) {
        int slen = s.length();
        int limit = this.m_length - slen;
        for (int i = start; i <= limit; i++) {
            if (equals(this.m_bytes, this.m_offset + i, s.m_bytes, s.m_offset, slen)) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(LuaString s) {
        int slen = s.length();
        int limit = this.m_length - slen;
        for (int i = limit; i >= 0; i--) {
            if (equals(this.m_bytes, this.m_offset + i, s.m_bytes, s.m_offset, slen)) {
                return i;
            }
        }
        return -1;
    }

    public static String decodeAsUtf8(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length);
    }

    public static int lengthAsUtf8(char[] chars) {
        return new String(chars).getBytes().length;
    }

    public boolean isValidUtf8() {
        byte[] m_bytes = this.m_bytes;
        int i = this.m_offset;
        int j = this.m_offset + this.m_length;
        int i2 = i;
        while (i2 < j) {
            int i3 = i2 + 1;
            int c = m_bytes[i2];
            if (c >= 0) {
                i2 = i3;
            } else if ((c & 224) == 192 && i3 < j) {
                i2 = i3 + 1;
                if ((m_bytes[i3] & 192) != 128) {
                    if ((c & 240) != 224) {
                    }
                    if ((c & 248) != 240) {
                    }
                    return false;
                }
                continue;
            } else {
                i2 = i3;
                if ((c & 240) != 224 && i2 + 1 < j) {
                    int i4 = i2 + 1;
                    if ((m_bytes[i2] & 192) == 128) {
                        i2 = i4 + 1;
                        if ((m_bytes[i4] & 192) == 128) {
                            continue;
                        }
                    } else {
                        i2 = i4;
                    }
                }
                if ((c & 248) != 240 && i2 + 2 < j) {
                    int i5 = i2 + 1;
                    if ((m_bytes[i2] & 192) == 128) {
                        i2 = i5 + 1;
                        if ((m_bytes[i5] & 192) == 128) {
                            int i6 = i2 + 1;
                            if ((m_bytes[i2] & 192) == 128) {
                                i2 = i6;
                            }
                        }
                    }
                    return false;
                }
                return false;
            }
        }
        return true;
    }

    static {
        for (byte i = Message.CMD_CS_SET_TEMP_PATH; i <= 57; i = (byte) (i + 1)) {
            numeral[i] = true;
        }
        for (byte i2 = 65; i2 <= 70; i2 = (byte) (i2 + 1)) {
            numeral[i2] = true;
        }
        for (byte i3 = 97; i3 <= 102; i3 = (byte) (i3 + 1)) {
            numeral[i3] = true;
        }
        numeral[13] = true;
        numeral[10] = true;
        numeral[9] = true;
        numeral[32] = true;
        numeral[43] = true;
        numeral[45] = true;
        numeral[46] = true;
        numeral[80] = true;
        numeral[112] = true;
        numeral[88] = true;
        numeral[120] = true;
    }

    @Override // luaj.LuaValue
    public LuaValue tonumber() {
        byte[] bytes = this.m_bytes;
        int offset = this.m_offset;
        int length = this.m_length;
        boolean[] num = numeral;
        for (int i = 0; i < length; i++) {
            byte ch = bytes[offset + i];
            if (ch < 9 || !num[ch]) {
                return NIL;
            }
        }
        try {
            return LuaValue.parseNumber(new String(bytes, offset, length), NIL);
        } catch (Throwable th) {
            return NIL;
        }
    }

    public LuaValue tonumber(int radix) {
        int i;
        byte[] m_bytes = this.m_bytes;
        int offset = this.m_offset;
        int end = offset + this.m_length;
        while (offset < end && m_bytes[offset] <= 32 && m_bytes[offset] > 0) {
            offset++;
        }
        while (offset < end && m_bytes[end - 1] <= 32 && m_bytes[end - 1] > 0) {
            end--;
        }
        if (offset == end) {
            return NIL;
        }
        byte firstByte = m_bytes[offset];
        if (firstByte == 45 || firstByte == 43) {
            offset++;
        }
        if (offset == end) {
            return NIL;
        }
        long max = Long.MIN_VALUE / ((long) radix);
        long result = 0;
        int offset2 = offset;
        while (offset2 < end) {
            int offset3 = offset2 + 1;
            int digit = m_bytes[offset2];
            if (radix <= 10 || (digit >= 48 && digit <= 57)) {
                i = 48;
            } else {
                i = (digit < 65 || digit > 90) ? 87 : 55;
            }
            int digit2 = digit - i;
            if (digit2 < 0 || digit2 >= radix) {
                return NIL;
            }
            if (max > result) {
                return NIL;
            }
            long next = (((long) radix) * result) - ((long) digit2);
            if (next > result) {
                return NIL;
            }
            result = next;
            offset2 = offset3;
        }
        if (firstByte != 45) {
            result = -result;
            if (result < 0) {
                return NIL;
            }
        }
        return LuaLong.valueOf(result);
    }

    public LuaString makeDeprecated() {
        return new DeprecatedLuaString(this.m_bytes, this.m_offset, this.m_length);
    }

    private static class DeprecatedLuaString extends LuaString {
        public DeprecatedLuaString(byte[] bytes, int offset, int length) {
            super(bytes, offset, length, null);
        }

        @Override // luaj.LuaValue
        public boolean isDeprecated() {
            return true;
        }
    }
}
