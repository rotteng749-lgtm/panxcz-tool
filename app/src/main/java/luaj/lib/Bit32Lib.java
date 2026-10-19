package luaj.lib;

import android.ext.ParserNumbers;
import luaj.LuaTable;
import luaj.LuaValue;
import luaj.Varargs;

/* JADX INFO: loaded from: classes.dex */
public class Bit32Lib extends TwoArgFunction {
    @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable t = new LuaTable();
        bind(t, Bit32LibV.class, new String[]{"band", "bnot", "bor", "btest", "bxor", "extract", "replace"});
        bind(t, Bit32Lib2.class, new String[]{"arshift", "lrotate", "lshift", "rrotate", "rshift"});
        env.set("bit32", t);
        if (!env.get("package").isnil()) {
            env.get("package").get("loaded").set("bit32", t);
        }
        return t;
    }

    static final class Bit32LibV extends VarArgFunction {
        Bit32LibV() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            switch (this.opcode) {
                case 0:
                    return Bit32Lib.band(args);
                case 1:
                    return Bit32Lib.bnot(args);
                case 2:
                    return Bit32Lib.bor(args);
                case 3:
                    return Bit32Lib.btest(args);
                case 4:
                    return Bit32Lib.bxor(args);
                case 5:
                    return Bit32Lib.extract((int) args.checklong(1), (int) args.checklong(2), (int) args.optlong(3, 1L));
                case 6:
                    return Bit32Lib.replace((int) args.checklong(1), (int) args.checklong(2), (int) args.checklong(3), (int) args.optlong(4, 1L));
                default:
                    return NIL;
            }
        }
    }

    static final class Bit32Lib2 extends TwoArgFunction {
        Bit32Lib2() {
        }

        @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            switch (this.opcode) {
                case 0:
                    return Bit32Lib.arshift((int) arg1.checklong(), (int) arg2.checklong());
                case 1:
                    return Bit32Lib.lrotate((int) arg1.checklong(), (int) arg2.checklong());
                case 2:
                    return Bit32Lib.lshift((int) arg1.checklong(), (int) arg2.checklong());
                case 3:
                    return Bit32Lib.rrotate((int) arg1.checklong(), (int) arg2.checklong());
                case 4:
                    return Bit32Lib.rshift((int) arg1.checklong(), (int) arg2.checklong());
                default:
                    return NIL;
            }
        }
    }

    static LuaValue arshift(int x, int disp) {
        return disp >= 0 ? bitsToValue(x >> disp) : bitsToValue(x << (-disp));
    }

    static LuaValue rshift(int x, int disp) {
        if (disp >= 32 || disp <= -32) {
            return ZERO;
        }
        if (disp >= 0) {
            return bitsToValue(x >>> disp);
        }
        return bitsToValue(x << (-disp));
    }

    static LuaValue lshift(int x, int disp) {
        if (disp >= 32 || disp <= -32) {
            return ZERO;
        }
        if (disp >= 0) {
            return bitsToValue(x << disp);
        }
        return bitsToValue(x >>> (-disp));
    }

    static Varargs band(Varargs args) {
        int result = -1;
        for (int i = 1; i <= args.narg(); i++) {
            result &= (int) args.checklong(i);
        }
        return bitsToValue(result);
    }

    static Varargs bnot(Varargs args) {
        return bitsToValue(((int) args.checklong(1)) ^ (-1));
    }

    static Varargs bor(Varargs args) {
        int result = 0;
        for (int i = 1; i <= args.narg(); i++) {
            result |= (int) args.checklong(i);
        }
        return bitsToValue(result);
    }

    static Varargs btest(Varargs args) {
        int bits = -1;
        for (int i = 1; i <= args.narg(); i++) {
            bits &= (int) args.checklong(i);
        }
        return valueOf(bits != 0);
    }

    static Varargs bxor(Varargs args) {
        int result = 0;
        for (int i = 1; i <= args.narg(); i++) {
            result ^= (int) args.checklong(i);
        }
        return bitsToValue(result);
    }

    static LuaValue lrotate(int x, int disp) {
        if (disp < 0) {
            return rrotate(x, -disp);
        }
        int disp2 = disp & 31;
        return bitsToValue((x << disp2) | (x >>> (32 - disp2)));
    }

    static LuaValue rrotate(int x, int disp) {
        if (disp < 0) {
            return lrotate(x, -disp);
        }
        int disp2 = disp & 31;
        return bitsToValue((x >>> disp2) | (x << (32 - disp2)));
    }

    static LuaValue extract(int n, int field, int width) {
        if (field < 0) {
            argerror(2, "field cannot be negative");
        }
        if (width < 0) {
            argerror(3, "width must be postive");
        }
        if (field + width > 32) {
            error("trying to access non-existent bits");
        }
        return bitsToValue((n >>> field) & ((-1) >>> (32 - width)));
    }

    static LuaValue replace(int n, int v, int field, int width) {
        if (field < 0) {
            argerror(3, "field cannot be negative");
        }
        if (width < 0) {
            argerror(4, "width must be postive");
        }
        if (field + width > 32) {
            error("trying to access non-existent bits");
        }
        int mask = ((-1) >>> (32 - width)) << field;
        return bitsToValue(((mask ^ (-1)) & n) | ((v << field) & mask));
    }

    private static LuaValue bitsToValue(int x) {
        return x < 0 ? valueOf(((long) x) & ParserNumbers.MAX_UINT) : valueOf(x);
    }

    public static void main(String[] args) throws Throwable {
    }
}
