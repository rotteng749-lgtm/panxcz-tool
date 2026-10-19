package luaj;

/* JADX INFO: loaded from: classes.dex */
public class LuaLong extends LuaNumber {
    public static final LuaLong MAX_VALUE = new LuaLong(Long.MAX_VALUE);
    public static final LuaLong MIN_VALUE = new LuaLong(Long.MIN_VALUE);
    private static final LuaLong[] longValues = new LuaLong[512];
    private static final LuaString[] luaStrings = new LuaString[512];
    private static final String[] strings = new String[512];
    public final long v;

    public static LuaLong valueOf(long l) {
        if (l <= 255 && l >= -256) {
            LuaLong ret = longValues[(int) (l + 256)];
            if (ret == null) {
                LuaLong ret2 = new LuaLong(l);
                longValues[(int) (l + 256)] = ret2;
                return ret2;
            }
            return ret;
        }
        if (l == Long.MAX_VALUE) {
            LuaLong ret3 = MAX_VALUE;
            return ret3;
        }
        if (l == Long.MIN_VALUE) {
            LuaLong ret4 = MIN_VALUE;
            return ret4;
        }
        LuaLong ret5 = new LuaLong(l);
        return ret5;
    }

    LuaLong(long l) {
        this.v = l;
    }

    @Override // luaj.LuaValue
    public boolean isint() {
        return true;
    }

    @Override // luaj.LuaValue
    public boolean isinttype() {
        return true;
    }

    @Override // luaj.LuaValue
    public boolean islong() {
        return true;
    }

    @Override // luaj.LuaValue
    public boolean isintnumber() {
        return ((long) ((int) this.v)) == this.v;
    }

    @Override // luaj.LuaValue
    public boolean islongnumber() {
        return true;
    }

    @Override // luaj.LuaValue
    public byte tobyte() {
        return (byte) this.v;
    }

    @Override // luaj.LuaValue
    public char tochar() {
        return (char) this.v;
    }

    @Override // luaj.LuaValue
    public double todouble() {
        return this.v;
    }

    @Override // luaj.LuaValue
    public float tofloat() {
        return this.v;
    }

    @Override // luaj.LuaValue
    public int toint() {
        return (int) this.v;
    }

    @Override // luaj.LuaValue
    public long tolong() {
        return this.v;
    }

    @Override // luaj.LuaValue
    public short toshort() {
        return (short) this.v;
    }

    @Override // luaj.LuaValue
    public double optdouble(double defval) {
        return this.v;
    }

    @Override // luaj.LuaValue
    public int optint(int defval) {
        return (int) this.v;
    }

    @Override // luaj.LuaValue
    public LuaLong optinteger(LuaLong defval) {
        return this;
    }

    @Override // luaj.LuaValue
    public long optlong(long defval) {
        return this.v;
    }

    @Override // luaj.LuaValue, luaj.Varargs
    public String tojstring() {
        return getStr();
    }

    private String getStr() {
        if (this.v > 255 || this.v < -256) {
            return Long.toString(this.v);
        }
        String ret = strings[(int) (this.v + 256)];
        if (ret == null) {
            String[] strArr = strings;
            int i = (int) (this.v + 256);
            String ret2 = Long.toString(this.v);
            strArr[i] = ret2;
            return ret2;
        }
        return ret;
    }

    private LuaString getLuaStr() {
        if (this.v > 255 || this.v < -256) {
            return LuaString.valueOf(getStr());
        }
        LuaString ret = luaStrings[(int) (this.v + 256)];
        if (ret == null) {
            LuaString[] luaStringArr = luaStrings;
            int i = (int) (this.v + 256);
            LuaString ret2 = LuaString.valueOf(getStr());
            luaStringArr[i] = ret2;
            return ret2;
        }
        return ret;
    }

    @Override // luaj.LuaValue
    public LuaString strvalue() {
        return getLuaStr();
    }

    @Override // luaj.LuaValue
    public LuaString optstring(LuaString defval) {
        return getLuaStr();
    }

    @Override // luaj.LuaValue
    public LuaValue tostring() {
        return getLuaStr();
    }

    @Override // luaj.LuaValue
    public String optjstring(String defval) {
        return getStr();
    }

    @Override // luaj.LuaValue
    public LuaLong checkinteger() {
        return this;
    }

    public int hashCode() {
        return hashCode(this.v);
    }

    public static int hashCode(long x) {
        return (int) ((x >>> 32) ^ x);
    }

    @Override // luaj.LuaValue
    public LuaValue neg() {
        return valueOf(-this.v);
    }

    @Override // luaj.LuaValue
    public LuaValue bnot() {
        return valueOf(this.v ^ (-1));
    }

    @Override // luaj.LuaValue
    public boolean equals(Object o) {
        return (o instanceof LuaLong) && ((LuaLong) o).v == this.v;
    }

    @Override // luaj.LuaValue
    public boolean eq_b(LuaValue val) {
        return !val.isnumber() ? super.eq_b(val) : val.islongnumber() ? this.v == val.tolong() : ((double) this.v) == val.todouble();
    }

    @Override // luaj.LuaValue
    public boolean raweq(LuaValue val) {
        if (val.isnumber()) {
            return val.islongnumber() ? this.v == val.tolong() : ((double) this.v) == val.todouble();
        }
        return false;
    }

    @Override // luaj.LuaValue
    public LuaValue add(LuaValue rhs) {
        return !rhs.isnumber() ? super.add(rhs) : rhs.islongnumber() ? valueOf(this.v + rhs.tolong()) : valueOf(this.v + rhs.todouble());
    }

    @Override // luaj.LuaValue
    public LuaValue sub(LuaValue rhs) {
        return !rhs.isnumber() ? super.sub(rhs) : rhs.islongnumber() ? valueOf(this.v - rhs.tolong()) : valueOf(this.v - rhs.todouble());
    }

    @Override // luaj.LuaValue
    public LuaValue mul(LuaValue rhs) {
        return !rhs.isnumber() ? super.mul(rhs) : rhs.islongnumber() ? valueOf(this.v * rhs.tolong()) : valueOf(this.v * rhs.todouble());
    }

    @Override // luaj.LuaValue
    public LuaValue pow(LuaValue rhs) {
        return !rhs.isnumber() ? super.pow(rhs) : valueOf(Math.pow(this.v, rhs.todouble()));
    }

    @Override // luaj.LuaValue
    public LuaValue div(LuaValue rhs) {
        return !rhs.isnumber() ? super.div(rhs) : (!rhs.islongnumber() || rhs.tolong() == 0) ? LuaDouble.ddiv(this.v, rhs.todouble()) : valueOf(this.v / rhs.tolong());
    }

    @Override // luaj.LuaValue
    public LuaValue idiv(LuaValue rhs) {
        return !rhs.isnumber() ? super.idiv(rhs) : rhs.islong() ? valueOf(floorDiv(this.v, rhs.tolong())) : LuaDouble.didiv(this.v, rhs.todouble());
    }

    @Override // luaj.LuaValue
    public LuaValue mod(LuaValue rhs) {
        return !rhs.isnumber() ? super.mod(rhs) : rhs.islong() ? valueOf(mod(this.v, rhs.tolong())) : LuaDouble.dmod(this.v, rhs.todouble());
    }

    @Override // luaj.LuaValue
    public LuaValue band(LuaValue rhs) {
        return !rhs.islongnumber() ? super.band(rhs) : valueOf(this.v & rhs.tolong());
    }

    @Override // luaj.LuaValue
    public LuaValue bor(LuaValue rhs) {
        return !rhs.islongnumber() ? super.bor(rhs) : valueOf(this.v | rhs.tolong());
    }

    @Override // luaj.LuaValue
    public LuaValue bxor(LuaValue rhs) {
        return !rhs.islongnumber() ? super.bxor(rhs) : valueOf(this.v ^ rhs.tolong());
    }

    @Override // luaj.LuaValue
    public LuaValue shl(LuaValue rhs) {
        return !rhs.islongnumber() ? super.shl(rhs) : shl(this.v, rhs.tolong());
    }

    @Override // luaj.LuaValue
    public LuaValue shr(LuaValue rhs) {
        return !rhs.islongnumber() ? super.shr(rhs) : shr(this.v, rhs.tolong());
    }

    @Override // luaj.LuaValue
    public boolean lt_b(LuaValue rhs) {
        return !(rhs instanceof LuaNumber) ? super.lt_b(rhs) : rhs.islongnumber() ? this.v < rhs.tolong() : ((double) this.v) < rhs.todouble();
    }

    @Override // luaj.LuaValue
    public boolean lteq_b(LuaValue rhs) {
        return !(rhs instanceof LuaNumber) ? super.lteq_b(rhs) : rhs.islongnumber() ? this.v <= rhs.tolong() : ((double) this.v) <= rhs.todouble();
    }

    @Override // luaj.LuaValue
    public boolean gt_b(LuaValue rhs) {
        return !(rhs instanceof LuaNumber) ? super.gt_b(rhs) : rhs.islongnumber() ? this.v > rhs.tolong() : ((double) this.v) > rhs.todouble();
    }

    @Override // luaj.LuaValue
    public boolean gteq_b(LuaValue rhs) {
        return !(rhs instanceof LuaNumber) ? super.gteq_b(rhs) : rhs.islongnumber() ? this.v >= rhs.tolong() : ((double) this.v) >= rhs.todouble();
    }

    @Override // luaj.LuaValue
    public int checkint() {
        return (int) this.v;
    }

    @Override // luaj.LuaValue
    public long checklong() {
        return this.v;
    }

    @Override // luaj.LuaValue
    public double checkdouble() {
        return this.v;
    }

    @Override // luaj.LuaValue
    public String checkjstring() {
        return String.valueOf(this.v);
    }

    @Override // luaj.LuaValue
    public LuaString checkstring() {
        return valueOf(String.valueOf(this.v));
    }

    public static LuaValue shl(long n, long m) {
        return m < 0 ? shr(n, -m) : m > 63 ? ZERO : valueOf(n << ((int) m));
    }

    public static LuaValue shr(long n, long m) {
        return m < 0 ? shl(n, -m) : m > 63 ? ZERO : valueOf(n >>> ((int) m));
    }

    public static long floorDiv(long x, long y) {
        if (y == 0) {
            throw new LuaError("attempt to divide by zero (as '" + x + " // " + y + "')");
        }
        long r = x / y;
        if ((x ^ y) < 0 && r * y != x) {
            return r - 1;
        }
        return r;
    }

    public static long mod(long a, long b) {
        if (b == 0) {
            throw new LuaError("attempt to perform 'n%0' (as '" + a + " % " + b + "')");
        }
        long ret = a % b;
        if (ret != 0) {
            return (b < 0) ^ ((ret > 0L ? 1 : (ret == 0L ? 0 : -1)) < 0) ? ret + b : ret;
        }
        return ret;
    }
}
