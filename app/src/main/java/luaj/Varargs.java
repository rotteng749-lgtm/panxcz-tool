package luaj;

import luaj.lib.VarArgFunction;

/* JADX INFO: loaded from: classes.dex */
public abstract class Varargs {
    public static final ThreadLocal<VarArgFunction> curFunc = new ThreadLocal<>();

    public abstract LuaValue arg(int i);

    public abstract LuaValue arg1();

    public abstract int narg();

    public abstract Varargs subargs(int i);

    public Varargs eval() {
        return this;
    }

    public boolean isTailcall() {
        return false;
    }

    public int type(int i) {
        return arg(i).type();
    }

    public boolean isnil(int i) {
        return arg(i).isnil();
    }

    public boolean isfunction(int i) {
        return arg(i).isfunction();
    }

    public boolean isnumber(int i) {
        return arg(i).isnumber();
    }

    public boolean isstring(int i) {
        return arg(i).isstring();
    }

    public boolean istable(int i) {
        return arg(i).istable();
    }

    public boolean isthread(int i) {
        return arg(i).isthread();
    }

    public boolean isvalue(int i) {
        return i > 0 && i <= narg();
    }

    public boolean optboolean(int i, boolean defval) {
        try {
            return arg(i).optboolean(defval);
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaClosure optclosure(int i, LuaClosure defval) {
        try {
            return arg(i).optclosure(defval);
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public double optdouble(int i, double defval) {
        try {
            return arg(i).optdouble(defval);
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaFunction optfunction(int i, LuaFunction defval) {
        try {
            return arg(i).optfunction(defval);
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public int optint(int i, int defval) {
        try {
            return arg(i).optint(defval);
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaLong optinteger(int i, LuaLong defval) {
        try {
            return arg(i).optinteger(defval);
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public long optlong(int i, long defval) {
        try {
            return arg(i).optlong(defval);
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaNumber optnumber(int i, LuaNumber defval) {
        try {
            return arg(i).optnumber(defval);
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public String optjstring(int i, String defval) {
        try {
            return arg(i).optjstring(defval);
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaString optstring(int i, LuaString defval) {
        try {
            return arg(i).optstring(defval);
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaTable opttable(int i, LuaTable defval) {
        try {
            return arg(i).opttable(defval);
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaThread optthread(int i, LuaThread defval) {
        try {
            return arg(i).optthread(defval);
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaValue optvalue(int i, LuaValue defval) {
        return (i <= 0 || i > narg()) ? defval : arg(i);
    }

    private LuaError argerr(int iarg, LuaError source) {
        String msg = source.getMessage();
        if (msg.startsWith("bad argument: ")) {
            msg = msg.substring("bad argument: ".length());
        }
        String msg2 = String.valueOf(iarg <= narg() ? arg(iarg).typename() : "no value") + ": " + msg;
        VarArgFunction current = curFunc.get();
        return new LuaArgError(iarg, current == null ? null : current.name(), msg2);
    }

    public boolean checkboolean(int i) {
        try {
            return arg(i).checkboolean();
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaClosure checkclosure(int i) {
        try {
            return arg(i).checkclosure();
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public double checkdouble(int i) {
        try {
            return arg(i).checkdouble();
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaFunction checkfunction(int i) {
        try {
            return arg(i).checkfunction();
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public int checkint(int i) {
        try {
            return arg(i).checkint();
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaLong checkinteger(int i) {
        try {
            return arg(i).checkinteger();
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public long checklong(int i) {
        try {
            return arg(i).checklong();
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaNumber checknumber(int i) {
        try {
            return arg(i).checknumber();
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public String checkjstring(int i) {
        try {
            return arg(i).checkjstring();
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaString checkstring(int i) {
        try {
            return arg(i).checkstring();
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaTable checktable(int i) {
        try {
            return arg(i).checktable();
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaThread checkthread(int i) {
        try {
            return arg(i).checkthread();
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public LuaValue checkvalue(int i) {
        return i <= narg() ? arg(i) : LuaValue.argerror(i, "value expected");
    }

    public LuaValue checknotnil(int i) {
        try {
            return arg(i).checknotnil();
        } catch (LuaError e) {
            throw argerr(i, e);
        }
    }

    public void argcheck(boolean test, int i, String msg) {
        if (test) {
            return;
        }
        LuaValue.argerror(i, msg);
    }

    public boolean isnoneornil(int i) {
        return i > narg() || arg(i).isnil();
    }

    public boolean toboolean(int i) {
        return arg(i).toboolean();
    }

    public byte tobyte(int i) {
        return arg(i).tobyte();
    }

    public char tochar(int i) {
        return arg(i).tochar();
    }

    public double todouble(int i) {
        return arg(i).todouble();
    }

    public float tofloat(int i) {
        return arg(i).tofloat();
    }

    public int toint(int i) {
        return arg(i).toint();
    }

    public long tolong(int i) {
        return arg(i).tolong();
    }

    public String tojstring(int i) {
        return arg(i).tojstring();
    }

    public short toshort(int i) {
        return arg(i).toshort();
    }

    public String tojstring() {
        Buffer sb = new Buffer();
        sb.append("(");
        int n = narg();
        for (int i = 1; i <= n; i++) {
            if (i > 1) {
                sb.append(",");
            }
            sb.append(arg(i).tojstring());
        }
        sb.append(")");
        return sb.tojstring();
    }

    public String toString() {
        return tojstring();
    }

    static class SubVarargs extends Varargs {
        private final int end;
        private final int start;
        private final Varargs v;

        public SubVarargs(Varargs varargs, int start, int end) {
            this.v = varargs;
            this.start = start;
            this.end = end;
        }

        @Override // luaj.Varargs
        public LuaValue arg(int i) {
            int i2 = i + (this.start - 1);
            return (i2 < this.start || i2 > this.end) ? LuaValue.NIL : this.v.arg(i2);
        }

        @Override // luaj.Varargs
        public LuaValue arg1() {
            return this.v.arg(this.start);
        }

        @Override // luaj.Varargs
        public int narg() {
            return (this.end + 1) - this.start;
        }

        @Override // luaj.Varargs
        public Varargs subargs(int start) {
            if (start != 1) {
                int newstart = (this.start + start) - 1;
                if (start > 0) {
                    if (newstart >= this.end) {
                        return LuaValue.NONE;
                    }
                    if (newstart == this.end) {
                        return this.v.arg(this.end);
                    }
                    if (newstart == this.end - 1) {
                        return new PairVarargs(this.v.arg(this.end - 1), this.v.arg(this.end));
                    }
                    return new SubVarargs(this.v, newstart, this.end);
                }
                return new SubVarargs(this.v, newstart, this.end);
            }
            return this;
        }
    }

    static final class PairVarargs extends Varargs {
        private final LuaValue v1;
        private final Varargs v2;

        PairVarargs(LuaValue v1, Varargs v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        @Override // luaj.Varargs
        public LuaValue arg(int i) {
            return i == 1 ? this.v1 : this.v2.arg(i - 1);
        }

        @Override // luaj.Varargs
        public int narg() {
            return this.v2.narg() + 1;
        }

        @Override // luaj.Varargs
        public LuaValue arg1() {
            return this.v1;
        }

        @Override // luaj.Varargs
        public Varargs subargs(int start) {
            if (start != 1) {
                if (start == 2) {
                    return this.v2;
                }
                if (start > 2) {
                    return this.v2.subargs(start - 1);
                }
                return LuaValue.argerror(1, "start must be > 0");
            }
            return this;
        }
    }

    static final class ArrayVarargs extends Varargs {
        private final Varargs r;
        private final LuaValue[] v;

        ArrayVarargs(LuaValue[] v, Varargs r) {
            this.v = v;
            this.r = r;
        }

        @Override // luaj.Varargs
        public LuaValue arg(int i) {
            return i < 1 ? LuaValue.NIL : i <= this.v.length ? this.v[i - 1] : this.r.arg(i - this.v.length);
        }

        @Override // luaj.Varargs
        public int narg() {
            return this.v.length + this.r.narg();
        }

        @Override // luaj.Varargs
        public LuaValue arg1() {
            return this.v.length > 0 ? this.v[0] : this.r.arg1();
        }

        @Override // luaj.Varargs
        public Varargs subargs(int start) {
            if (start <= 0) {
                LuaValue.argerror(1, "start must be > 0");
            }
            if (start != 1) {
                if (start > this.v.length) {
                    return this.r.subargs(start - this.v.length);
                }
                return LuaValue.varargsOf(this.v, start - 1, this.v.length - (start - 1), this.r);
            }
            return this;
        }

        @Override // luaj.Varargs
        void copyto(LuaValue[] dest, int offset, int length) {
            int n = Math.min(this.v.length, length);
            System.arraycopy(this.v, 0, dest, offset, n);
            this.r.copyto(dest, offset + n, length - n);
        }
    }

    static final class ArrayPartVarargs extends Varargs {
        private final int length;
        private final Varargs more;
        private final int offset;
        private final LuaValue[] v;

        ArrayPartVarargs(LuaValue[] v, int offset, int length) {
            this.v = v;
            this.offset = offset;
            this.length = length;
            this.more = LuaValue.NONE;
        }

        public ArrayPartVarargs(LuaValue[] v, int offset, int length, Varargs more) {
            this.v = v;
            this.offset = offset;
            this.length = length;
            this.more = more;
        }

        @Override // luaj.Varargs
        public LuaValue arg(int i) {
            return i < 1 ? LuaValue.NIL : i <= this.length ? this.v[(this.offset + i) - 1] : this.more.arg(i - this.length);
        }

        @Override // luaj.Varargs
        public int narg() {
            return this.length + this.more.narg();
        }

        @Override // luaj.Varargs
        public LuaValue arg1() {
            return this.length > 0 ? this.v[this.offset] : this.more.arg1();
        }

        @Override // luaj.Varargs
        public Varargs subargs(int start) {
            if (start <= 0) {
                LuaValue.argerror(1, "start must be > 0");
            }
            if (start != 1) {
                if (start > this.length) {
                    return this.more.subargs(start - this.length);
                }
                return LuaValue.varargsOf(this.v, (this.offset + start) - 1, this.length - (start - 1), this.more);
            }
            return this;
        }

        @Override // luaj.Varargs
        void copyto(LuaValue[] dest, int offset, int length) {
            int n = Math.min(this.length, length);
            System.arraycopy(this.v, this.offset, dest, offset, n);
            this.more.copyto(dest, offset + n, length - n);
        }
    }

    void copyto(LuaValue[] dest, int offset, int length) {
        for (int i = 0; i < length; i++) {
            dest[offset + i] = arg(i + 1);
        }
    }

    public Varargs dealias() {
        int n = narg();
        switch (n) {
            case 0:
                return LuaValue.NONE;
            case 1:
                return arg1();
            case 2:
                return new PairVarargs(arg1(), arg(2));
            default:
                LuaValue[] v = new LuaValue[n];
                copyto(v, 0, n);
                return new ArrayVarargs(v, LuaValue.NONE);
        }
    }

    public static void main(String[] args) throws Throwable {
    }
}
