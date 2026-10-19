package luaj.lib;

import java.util.Random;
import luaj.LuaDouble;
import luaj.LuaError;
import luaj.LuaLong;
import luaj.LuaTable;
import luaj.LuaValue;
import luaj.Varargs;

/* JADX INFO: loaded from: classes.dex */
public class MathLib extends TwoArgFunction {
    @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable math = new LuaTable(0, 64);
        math.set("abs", new abs());
        math.set("acos", new acos());
        math.set("asin", new asin());
        LuaValue atan = new atan2();
        math.set("atan", atan);
        math.set("atan2", atan);
        math.set("ceil", new ceil());
        math.set("cos", new cos());
        math.set("cosh", new cosh());
        math.set("deg", new deg());
        math.set("exp", new exp());
        math.set("floor", new floor());
        math.set("fmod", new fmod());
        math.set("frexp", new frexp());
        math.set("huge", LuaDouble.POSINF);
        math.set("ldexp", new ldexp());
        math.set("log", new log());
        math.set("max", new max());
        math.set("maxinteger", LuaLong.MAX_VALUE);
        math.set("min", new min());
        math.set("mininteger", LuaLong.MIN_VALUE);
        math.set("modf", new modf());
        math.set("pi", 3.141592653589793d);
        math.set("pow", new pow());
        random r = new random();
        math.set("random", r);
        math.set("randomseed", new randomseed(r));
        math.set("rad", new rad());
        math.set("sin", new sin());
        math.set("sinh", new sinh());
        math.set("sqrt", new sqrt());
        math.set("tan", new tan());
        math.set("tanh", new tanh());
        math.set("tointeger", new tointeger());
        math.set("type", new type());
        math.set("ult", new ult());
        env.set("math", math);
        if (!env.get("package").isnil()) {
            env.get("package").get("loaded").set("math", math);
        }
        return math;
    }

    static String getFuncName(LibFunction func) {
        return func.getClass().getName().replace(String.valueOf(MathLib.class.getName()) + '$', "math.");
    }

    static double checkdouble(LibFunction func, int arg, LuaValue x) {
        try {
            return x.checkdouble();
        } catch (LuaError e) {
            argerror(arg, getFuncName(func), e.getMessage());
            return 0.0d;
        }
    }

    static double optdouble(LibFunction func, int arg, LuaValue x, double def) {
        try {
            return x.optdouble(def);
        } catch (LuaError e) {
            argerror(arg, getFuncName(func), e.getMessage());
            return 0.0d;
        }
    }

    static long checklong(LibFunction func, int arg, LuaValue x) {
        try {
            return x.checklong();
        } catch (LuaError e) {
            argerror(arg, getFuncName(func), e.getMessage());
            return 0L;
        }
    }

    protected static abstract class UnaryOp extends OneArgFunction {
        protected abstract double call(double d);

        protected UnaryOp() {
        }

        @Override // luaj.lib.OneArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            return valueOf(call(MathLib.checkdouble(this, 1, arg)));
        }
    }

    protected static abstract class BinaryOp extends TwoArgFunction {
        protected abstract double call(double d, double d2);

        protected BinaryOp() {
        }

        @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue x, LuaValue y) {
            return valueOf(call(MathLib.checkdouble(this, 1, x), MathLib.checkdouble(this, 2, y)));
        }
    }

    static final class abs extends OneArgFunction {
        abs() {
        }

        @Override // luaj.lib.OneArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            return arg.islong() ? valueOf(Math.abs(arg.tolong())) : valueOf(Math.abs(MathLib.checkdouble(this, 1, arg)));
        }
    }

    static final class ceil extends UnaryOp {
        ceil() {
        }

        @Override // luaj.lib.MathLib.UnaryOp
        protected double call(double d) {
            return Math.ceil(d);
        }
    }

    static final class cos extends UnaryOp {
        cos() {
        }

        @Override // luaj.lib.MathLib.UnaryOp
        protected double call(double d) {
            return Math.cos(d);
        }
    }

    static final class deg extends UnaryOp {
        deg() {
        }

        @Override // luaj.lib.MathLib.UnaryOp
        protected double call(double d) {
            return Math.toDegrees(d);
        }
    }

    static final class floor extends UnaryOp {
        floor() {
        }

        @Override // luaj.lib.MathLib.UnaryOp
        protected double call(double d) {
            return Math.floor(d);
        }
    }

    static final class rad extends UnaryOp {
        rad() {
        }

        @Override // luaj.lib.MathLib.UnaryOp
        protected double call(double d) {
            return Math.toRadians(d);
        }
    }

    static final class sin extends UnaryOp {
        sin() {
        }

        @Override // luaj.lib.MathLib.UnaryOp
        protected double call(double d) {
            return Math.sin(d);
        }
    }

    static final class sqrt extends UnaryOp {
        sqrt() {
        }

        @Override // luaj.lib.MathLib.UnaryOp
        protected double call(double d) {
            return Math.sqrt(d);
        }
    }

    static final class tan extends UnaryOp {
        tan() {
        }

        @Override // luaj.lib.MathLib.UnaryOp
        protected double call(double d) {
            return Math.tan(d);
        }
    }

    static final class tointeger extends OneArgFunction {
        tointeger() {
        }

        @Override // luaj.lib.OneArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            return arg.islongnumber() ? valueOf(arg.tolong()) : NIL;
        }
    }

    static final class type extends OneArgFunction {
        type() {
        }

        @Override // luaj.lib.OneArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            if (arg instanceof LuaLong) {
                return valueOf("integer");
            }
            if (arg instanceof LuaDouble) {
                return valueOf("float");
            }
            return NIL;
        }
    }

    static final class ult extends TwoArgFunction {
        ult() {
        }

        @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue m_, LuaValue n_) {
            long m = MathLib.checklong(this, 1, m_);
            long n = MathLib.checklong(this, 2, n_);
            return (n < 0) ^ (((m > 0L ? 1 : (m == 0L ? 0 : -1)) < 0) ^ ((m > n ? 1 : (m == n ? 0 : -1)) < 0)) ? TRUE : FALSE;
        }
    }

    static final class fmod extends TwoArgFunction {
        fmod() {
        }

        @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue xv, LuaValue yv) {
            return (xv.islong() && yv.islong()) ? valueOf(xv.tolong() % yv.tolong()) : valueOf(MathLib.checkdouble(this, 1, xv) % MathLib.checkdouble(this, 2, yv));
        }
    }

    static final class ldexp extends BinaryOp {
        ldexp() {
        }

        @Override // luaj.lib.MathLib.BinaryOp
        protected double call(double x, double y) {
            return Double.longBitsToDouble((((long) y) + 1023) << 52) * x;
        }
    }

    static class frexp extends VarArgFunction {
        frexp() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            double x = args.checkdouble(1);
            if (x == 0.0d) {
                return varargsOf(ZERO, ZERO);
            }
            long bits = Double.doubleToLongBits(x);
            double m = ((4503599627370495L & bits) + 4503599627370496L) * (bits >= 0 ? 1.1102230246251565E-16d : -1.1102230246251565E-16d);
            double e = (((int) (bits >> 52)) & 2047) - 1022;
            return varargsOf(valueOf(m), valueOf(e));
        }
    }

    static class max extends VarArgFunction {
        max() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaValue m = args.checkvalue(1);
            int n = args.narg();
            for (int i = 2; i <= n; i++) {
                LuaValue v = args.checkvalue(i);
                if (m.lt_b(v)) {
                    m = v;
                }
            }
            return m;
        }
    }

    static class min extends VarArgFunction {
        min() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaValue m = args.checkvalue(1);
            int n = args.narg();
            for (int i = 2; i <= n; i++) {
                LuaValue v = args.checkvalue(i);
                if (v.lt_b(m)) {
                    m = v;
                }
            }
            return m;
        }
    }

    static class modf extends VarArgFunction {
        modf() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaValue n = args.arg1();
            if (n.islongnumber()) {
                return varargsOf(n, valueOf(0.0d));
            }
            double x = n.checkdouble();
            double intPart = x > 0.0d ? Math.floor(x) : Math.ceil(x);
            double fracPart = x != intPart ? x - intPart : 0.0d;
            return varargsOf(valueOf(intPart), valueOf(fracPart));
        }
    }

    static class random extends LibFunction {
        Random random = new Random();

        random() {
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call() {
            return valueOf(this.random.nextDouble());
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue a) {
            long m = a.checklong();
            if (m < 1) {
                argerror(1, "interval is empty");
            }
            return valueOf((nextLong() % m) + 1);
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue a, LuaValue b) {
            long m = a.checklong();
            long n = b.checklong();
            if (n < m) {
                argerror(2, "interval is empty");
            }
            long d = n - m;
            if (d < 0) {
                argerror(2, "interval too large");
            }
            long r = nextLong();
            if (d != Long.MAX_VALUE) {
                r %= 1 + d;
            }
            return valueOf(m + r);
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue a, LuaValue b, LuaValue c) {
            return argerror(3, "too many arguments");
        }

        private long nextLong() {
            long r = this.random.nextLong();
            return r < 0 ? Long.MAX_VALUE + r + 1 : r;
        }
    }

    static class randomseed extends OneArgFunction {
        final random random;

        randomseed(random random) {
            this.random = random;
        }

        @Override // luaj.lib.OneArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            long seed = MathLib.checklong(this, 1, arg);
            this.random.random = new Random(seed);
            return NONE;
        }
    }

    static final class acos extends UnaryOp {
        acos() {
        }

        @Override // luaj.lib.MathLib.UnaryOp
        protected double call(double d) {
            return Math.acos(d);
        }
    }

    static final class asin extends UnaryOp {
        asin() {
        }

        @Override // luaj.lib.MathLib.UnaryOp
        protected double call(double d) {
            return Math.asin(d);
        }
    }

    static final class atan2 extends TwoArgFunction {
        atan2() {
        }

        @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue x, LuaValue y) {
            return valueOf(Math.atan2(MathLib.checkdouble(this, 1, x), MathLib.optdouble(this, 2, y, 1.0d)));
        }
    }

    static final class cosh extends UnaryOp {
        cosh() {
        }

        @Override // luaj.lib.MathLib.UnaryOp
        protected double call(double d) {
            return Math.cosh(d);
        }
    }

    static final class exp extends UnaryOp {
        exp() {
        }

        @Override // luaj.lib.MathLib.UnaryOp
        protected double call(double d) {
            return Math.exp(d);
        }
    }

    static final class log extends TwoArgFunction {
        log() {
        }

        @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue x, LuaValue base) {
            double nat = Math.log(MathLib.checkdouble(this, 1, x));
            double b = MathLib.optdouble(this, 2, base, 2.718281828459045d);
            if (b != 2.718281828459045d) {
                nat /= Math.log(b);
            }
            return valueOf(nat);
        }
    }

    static final class pow extends BinaryOp {
        pow() {
        }

        @Override // luaj.lib.MathLib.BinaryOp
        protected double call(double x, double y) {
            return Math.pow(x, y);
        }
    }

    static final class sinh extends UnaryOp {
        sinh() {
        }

        @Override // luaj.lib.MathLib.UnaryOp
        protected double call(double d) {
            return Math.sinh(d);
        }
    }

    static final class tanh extends UnaryOp {
        tanh() {
        }

        @Override // luaj.lib.MathLib.UnaryOp
        protected double call(double d) {
            return Math.tanh(d);
        }
    }

    public static void main(String[] args) throws Throwable {
    }
}
