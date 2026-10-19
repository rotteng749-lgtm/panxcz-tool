package luaj.lib;

import luaj.LuaValue;
import luaj.Varargs;

/* JADX INFO: loaded from: classes.dex */
public abstract class TwoArgFunction extends LibFunction {
    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public abstract LuaValue call(LuaValue luaValue, LuaValue luaValue2);

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public final LuaValue call() {
        return call(NIL, NIL);
    }

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public final LuaValue call(LuaValue arg) {
        return call(arg, NIL);
    }

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        return call(arg1, arg2);
    }

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public Varargs invoke(Varargs varargs) {
        return call(varargs.arg1(), varargs.arg(2));
    }
}
