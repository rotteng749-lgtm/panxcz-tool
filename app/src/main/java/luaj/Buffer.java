package luaj;

/* JADX INFO: loaded from: classes.dex */
public final class Buffer {
    private static final int DEFAULT_CAPACITY = 64;
    private static final int MAXUNICODE = 1114111;
    private static final byte[] NOBYTES = new byte[0];
    private byte[] bytes;
    private int length;
    private int offset;
    private LuaValue value;

    public Buffer() {
        this(64);
    }

    public Buffer(int initialCapacity) {
        this.bytes = new byte[initialCapacity];
        this.length = 0;
        this.offset = 0;
        this.value = null;
    }

    public Buffer(LuaValue value) {
        this.bytes = NOBYTES;
        this.offset = 0;
        this.length = 0;
        this.value = value;
    }

    public LuaValue value() {
        return this.value != null ? this.value : tostring();
    }

    public Buffer setvalue(LuaValue value) {
        this.bytes = NOBYTES;
        this.length = 0;
        this.offset = 0;
        this.value = value;
        return this;
    }

    public int length() {
        return this.length;
    }

    public final LuaString tostring() {
        realloc(this.length, 0);
        return LuaString.valueOf(this.bytes, this.offset, this.length);
    }

    public String tojstring() {
        return value().tojstring();
    }

    public String toString() {
        return tojstring();
    }

    public final Buffer append(byte b) {
        makeroom(0, 1);
        byte[] bArr = this.bytes;
        int i = this.offset;
        int i2 = this.length;
        this.length = i2 + 1;
        bArr[i + i2] = b;
        return this;
    }

    public final Buffer appendCodePoint(int cp) {
        int s;
        int m;
        if (cp < 0 || cp > MAXUNICODE) {
            throw new IllegalArgumentException("Code point (" + cp + ") out of range [0; " + MAXUNICODE + "]");
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
        makeroom(0, s);
        int offset = this.offset + this.length;
        byte[] bytes = this.bytes;
        for (int j = s - 1; j > 0; j--) {
            bytes[offset + j] = (byte) ((cp & 63) | 128);
            cp >>= 6;
        }
        bytes[offset] = (byte) (cp | m);
        this.length += s;
        return this;
    }

    public final Buffer append(LuaValue val) {
        append(val.strvalue());
        return this;
    }

    public final Buffer append(LuaString str) {
        int n = str.m_length;
        makeroom(0, n);
        str.copyInto(0, this.bytes, this.offset + this.length, n);
        this.length += n;
        return this;
    }

    public final Buffer append(String str) {
        byte[] b = str.getBytes();
        return append(b, 0, b.length);
    }

    public final Buffer append(byte[] b, int start, int n) {
        makeroom(0, n);
        System.arraycopy(b, start, this.bytes, this.offset + this.length, n);
        this.length += n;
        return this;
    }

    public Buffer concatTo(LuaValue lhs) {
        return setvalue(lhs.concat(value()));
    }

    public Buffer concatTo(LuaString lhs) {
        return (this.value == null || this.value.isstring()) ? prepend(lhs) : setvalue(lhs.concat(this.value));
    }

    public Buffer concatTo(LuaNumber lhs) {
        return (this.value == null || this.value.isstring()) ? prepend(lhs.strvalue()) : setvalue(lhs.concat(this.value));
    }

    public Buffer prepend(LuaString s) {
        int n = s.m_length;
        makeroom(n, 0);
        System.arraycopy(s.m_bytes, s.m_offset, this.bytes, this.offset - n, n);
        this.offset -= n;
        this.length += n;
        this.value = null;
        return this;
    }

    public final void makeroom(int nbefore, int nafter) {
        int m = 32;
        if (this.value != null) {
            LuaString s = this.value.strvalue();
            this.value = null;
            this.length = s.m_length;
            this.offset = nbefore;
            this.bytes = new byte[this.length + nbefore + nafter];
            System.arraycopy(s.m_bytes, s.m_offset, this.bytes, this.offset, this.length);
            return;
        }
        if (this.offset + this.length + nafter > this.bytes.length || this.offset < nbefore) {
            int n = this.length + nbefore + nafter;
            if (n >= 32) {
                m = n < this.length * 2 ? this.length * 2 : n;
            }
            realloc(m, nbefore == 0 ? 0 : (m - this.length) - nafter);
        }
    }

    private final void realloc(int newSize, int newOffset) {
        if (newSize != this.bytes.length) {
            byte[] newBytes = new byte[newSize];
            System.arraycopy(this.bytes, this.offset, newBytes, newOffset, this.length);
            this.bytes = newBytes;
            this.offset = newOffset;
        }
    }
}
