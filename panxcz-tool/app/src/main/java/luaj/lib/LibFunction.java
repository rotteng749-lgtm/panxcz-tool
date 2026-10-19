package luaj.lib;

import luaj.LuaError;
import luaj.LuaFunction;
import luaj.LuaValue;
import luaj.Varargs;

/* JADX INFO: loaded from: classes.dex */
public abstract class LibFunction extends LuaFunction {
    protected String name;
    protected int opcode;

    protected LibFunction() {
    }

    @Override // luaj.LuaFunction, luaj.LuaValue, luaj.Varargs
    public String tojstring() {
        return this.name != null ? "function: " + this.name : super.tojstring();
    }

    protected void bind(LuaValue env, Class factory, String[] names) {
        bind(env, factory, names, 0);
    }

    protected void bind(LuaValue env, Class factory, String[] names, int firstopcode) {
        try {
            int n = names.length;
            for (int i = 0; i < n; i++) {
                LibFunction f = (LibFunction) factory.newInstance();
                f.opcode = firstopcode + i;
                f.name = names[i];
                env.set(f.name, f);
            }
        } catch (Exception e) {
            throw new LuaError("bind failed: " + e);
        }
    }

    protected static LuaValue[] newupe() {
        return new LuaValue[1];
    }

    protected static LuaValue[] newupn() {
        return new LuaValue[]{NIL};
    }

    protected static LuaValue[] newupl(LuaValue v) {
        return new LuaValue[]{v};
    }

    @Override // luaj.LuaValue
    public LuaValue call() {
        return argerror(1, "value expected");
    }

    @Override // luaj.LuaValue
    public LuaValue call(LuaValue a) {
        return call();
    }

    @Override // luaj.LuaValue
    public LuaValue call(LuaValue a, LuaValue b) {
        return call(a);
    }

    @Override // luaj.LuaValue
    public LuaValue call(LuaValue a, LuaValue b, LuaValue c) {
        return call(a, b);
    }

    public LuaValue call(LuaValue a, LuaValue b, LuaValue c, LuaValue d) {
        return call(a, b, c);
    }

    @Override // luaj.LuaValue
    public Varargs invoke(Varargs args) {
        switch (args.narg()) {
            case 0:
                return call();
            case 1:
                return call(args.arg1());
            case 2:
                return call(args.arg1(), args.arg(2));
            case 3:
                return call(args.arg1(), args.arg(2), args.arg(3));
            default:
                return call(args.arg1(), args.arg(2), args.arg(3), args.arg(4));
        }
    }
}
