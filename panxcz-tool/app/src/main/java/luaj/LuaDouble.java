package luaj;

/* JADX INFO: loaded from: classes.dex */
public class LuaDouble extends LuaNumber {
    public static final String JSTR_NAN = "nan";
    public static final String JSTR_NEGINF = "-inf";
    public static final String JSTR_POSINF = "inf";
    private static final double MAX_LONG = 9.007199254740992E15d;
    final double v;
    public static final LuaDouble NAN = new LuaDouble(Double.NaN);
    public static final LuaDouble POSINF = new LuaDouble(Double.POSITIVE_INFINITY);
    public static final LuaDouble NEGINF = new LuaDouble(Double.NEGATIVE_INFINITY);

    public static LuaNumber valueOf(double d) {
        return new LuaDouble(d);
    }

    private LuaDouble(double d) {
        this.v = d;
    }

    public int hashCode() {
        int iv = (int) this.v;
        long x = ((double) iv) == this.v ? iv : Double.doubleToRawLongBits(this.v);
        return (int) ((x >>> 32) ^ x);
    }

    @Override // luaj.LuaValue
    public boolean isintnumber() {
        return ((double) ((int) this.v)) == this.v;
    }

    @Override // luaj.LuaValue
    public boolean islongnumber() {
        return -9.007199254740992E15d <= this.v && this.v <= MAX_LONG && ((double) ((long) this.v)) == this.v;
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
        return (float) this.v;
    }

    @Override // luaj.LuaValue
    public int toint() {
        return (int) this.v;
    }

    @Override // luaj.LuaValue
    public long tolong() {
        return (long) this.v;
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
        return LuaLong.valueOf((long) this.v);
    }

    @Override // luaj.LuaValue
    public long optlong(long defval) {
        return (long) this.v;
    }

    @Override // luaj.LuaValue
    public LuaLong checkinteger() {
        return LuaLong.valueOf((long) this.v);
    }

    @Override // luaj.LuaValue
    public LuaValue neg() {
        return valueOf(-this.v);
    }

    @Override // luaj.LuaValue
    public LuaValue bnot() {
        return !islongnumber() ? super.bnot() : valueOf(tolong() ^ (-1));
    }

    @Override // luaj.LuaValue
    public boolean equals(Object o) {
        return (o instanceof LuaDouble) && Double.doubleToRawLongBits(((LuaDouble) o).v) == Double.doubleToRawLongBits(this.v);
    }

    @Override // luaj.LuaValue
    public boolean eq_b(LuaValue val) {
        return !val.isnumber() ? super.eq_b(val) : this.v == val.todouble();
    }

    @Override // luaj.LuaValue
    public boolean raweq(LuaValue val) {
        return val.isnumber() && this.v == val.todouble();
    }

    @Override // luaj.LuaValue
    public LuaValue add(LuaValue rhs) {
        return !rhs.isnumber() ? super.add(rhs) : valueOf(this.v + rhs.todouble());
    }

    @Override // luaj.LuaValue
    public LuaValue sub(LuaValue rhs) {
        return !rhs.isnumber() ? super.sub(rhs) : valueOf(this.v - rhs.todouble());
    }

    @Override // luaj.LuaValue
    public LuaValue mul(LuaValue rhs) {
        return !rhs.isnumber() ? super.mul(rhs) : valueOf(this.v * rhs.todouble());
    }

    @Override // luaj.LuaValue
    public LuaValue pow(LuaValue rhs) {
        return !rhs.isnumber() ? super.pow(rhs) : valueOf(Math.pow(this.v, rhs.todouble()));
    }

    @Override // luaj.LuaValue
    public LuaValue div(LuaValue rhs) {
        return !rhs.isnumber() ? super.div(rhs) : ddiv(this.v, rhs.todouble());
    }

    @Override // luaj.LuaValue
    public LuaValue idiv(LuaValue rhs) {
        return !rhs.isnumber() ? super.idiv(rhs) : didiv(this.v, rhs.todouble());
    }

    @Override // luaj.LuaValue
    public LuaValue mod(LuaValue rhs) {
        return !rhs.isnumber() ? super.mod(rhs) : dmod(this.v, rhs.todouble());
    }

    @Override // luaj.LuaValue
    public LuaValue band(LuaValue rhs) {
        return noint("&", rhs) ? super.band(rhs) : valueOf(tolong() & rhs.tolong());
    }

    @Override // luaj.LuaValue
    public LuaValue bor(LuaValue rhs) {
        return noint("|", rhs) ? super.bor(rhs) : valueOf(tolong() | rhs.tolong());
    }

    @Override // luaj.LuaValue
    public LuaValue bxor(LuaValue rhs) {
        return noint("~", rhs) ? super.bxor(rhs) : valueOf(tolong() ^ rhs.tolong());
    }

    @Override // luaj.LuaValue
    public LuaValue shl(LuaValue rhs) {
        return noint("<<", rhs) ? super.shl(rhs) : LuaLong.shl(tolong(), rhs.tolong());
    }

    @Override // luaj.LuaValue
    public LuaValue shr(LuaValue rhs) {
        return noint(">>", rhs) ? super.shr(rhs) : LuaLong.shr(tolong(), rhs.tolong());
    }

    private boolean noint(String op, LuaValue rhs) {
        if (!rhs.isnumber()) {
            return true;
        }
        if (!islongnumber()) {
            throw new LuaError("number '" + tojstring() + "' has no integer representation (for bitwise operation '" + tojstring() + " " + op + " " + rhs.tojstring() + "')");
        }
        if (!rhs.islongnumber()) {
            throw new LuaError("number '" + rhs.tojstring() + "' has no integer representation (for bitwise operation '" + tojstring() + " " + op + " " + rhs.tojstring() + "')");
        }
        return false;
    }

    public static LuaValue ddiv(double lhs, double rhs) {
        return rhs != 0.0d ? valueOf(lhs / rhs) : lhs > 0.0d ? POSINF : lhs == 0.0d ? NAN : NEGINF;
    }

    public static LuaValue didiv(double lhs, double rhs) {
        return rhs != 0.0d ? valueOf(Math.floor(lhs / rhs)) : lhs > 0.0d ? POSINF : lhs == 0.0d ? NAN : NEGINF;
    }

    public static double ddiv_d(double lhs, double rhs) {
        if (rhs != 0.0d) {
            return lhs / rhs;
        }
        if (lhs > 0.0d) {
            return Double.POSITIVE_INFINITY;
        }
        return lhs == 0.0d ? Double.NaN : Double.NEGATIVE_INFINITY;
    }

    public static LuaValue dmod(double lhs, double rhs) {
        if (rhs == 0.0d || lhs == Double.POSITIVE_INFINITY || lhs == Double.NEGATIVE_INFINITY) {
            return NAN;
        }
        if (rhs == Double.POSITIVE_INFINITY) {
            return lhs < 0.0d ? POSINF : valueOf(lhs);
        }
        if (rhs == Double.NEGATIVE_INFINITY) {
            return lhs > 0.0d ? NEGINF : valueOf(lhs);
        }
        return valueOf(lhs - (Math.floor(lhs / rhs) * rhs));
    }

    public static double dmod_d(double lhs, double rhs) {
        if (rhs == 0.0d || lhs == Double.POSITIVE_INFINITY || lhs == Double.NEGATIVE_INFINITY) {
            return Double.NaN;
        }
        if (rhs == Double.POSITIVE_INFINITY) {
            if (lhs < 0.0d) {
                return Double.POSITIVE_INFINITY;
            }
            return lhs;
        }
        if (rhs != Double.NEGATIVE_INFINITY) {
            return lhs - (Math.floor(lhs / rhs) * rhs);
        }
        if (lhs > 0.0d) {
            return Double.NEGATIVE_INFINITY;
        }
        return lhs;
    }

    @Override // luaj.LuaValue
    public boolean lt_b(LuaValue rhs) {
        return !(rhs instanceof LuaNumber) ? super.lt_b(rhs) : this.v < rhs.todouble();
    }

    @Override // luaj.LuaValue
    public boolean lteq_b(LuaValue rhs) {
        return !(rhs instanceof LuaNumber) ? super.lteq_b(rhs) : this.v <= rhs.todouble();
    }

    @Override // luaj.LuaValue
    public boolean gt_b(LuaValue rhs) {
        return !(rhs instanceof LuaNumber) ? super.gt_b(rhs) : this.v > rhs.todouble();
    }

    @Override // luaj.LuaValue
    public boolean gteq_b(LuaValue rhs) {
        return !(rhs instanceof LuaNumber) ? super.gteq_b(rhs) : this.v >= rhs.todouble();
    }

    @Override // luaj.LuaValue, luaj.Varargs
    public String tojstring() {
        if (this.v == 0.0d) {
            long bits = Double.doubleToLongBits(this.v);
            return (bits >> 63) == 0 ? "0.0" : "-0.0";
        }
        if (Double.isNaN(this.v)) {
            return JSTR_NAN;
        }
        if (Double.isInfinite(this.v)) {
            return this.v < 0.0d ? JSTR_NEGINF : JSTR_POSINF;
        }
        long l = (long) this.v;
        String ret = ((double) l) == this.v ? Long.toString(l) : Double.toString(this.v);
        if (ret.indexOf(46) == -1 && ret.indexOf(101) == -1 && ret.indexOf(69) == -1) {
            ret = String.valueOf(ret) + ".0";
        }
        return ret;
    }

    @Override // luaj.LuaValue
    public LuaString strvalue() {
        return LuaString.valueOf(tojstring());
    }

    @Override // luaj.LuaValue
    public LuaString optstring(LuaString defval) {
        return LuaString.valueOf(tojstring());
    }

    @Override // luaj.LuaValue
    public LuaValue tostring() {
        return LuaString.valueOf(tojstring());
    }

    @Override // luaj.LuaValue
    public String optjstring(String defval) {
        return tojstring();
    }

    @Override // luaj.LuaValue
    public int checkint() {
        int i = (int) this.v;
        if (i == this.v) {
            return i;
        }
        throw new LuaError("number '" + tojstring() + "' has no integer representation");
    }

    @Override // luaj.LuaValue
    public long checklong() {
        long i = (long) this.v;
        if (i == this.v) {
            return i;
        }
        throw new LuaError("number '" + tojstring() + "' has no integer representation");
    }

    @Override // luaj.LuaValue
    public double checkdouble() {
        return this.v;
    }

    @Override // luaj.LuaValue
    public String checkjstring() {
        return tojstring();
    }

    @Override // luaj.LuaValue
    public LuaString checkstring() {
        return LuaString.valueOf(tojstring());
    }

    @Override // luaj.LuaValue
    public boolean isvalidkey() {
        return !Double.isNaN(this.v);
    }
}
