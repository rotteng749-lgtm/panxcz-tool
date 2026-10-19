package luaj.lib;

import luaj.LuaValue;
import luaj.Varargs;

/* JADX INFO: loaded from: classes.dex */
public abstract class OneArgFunction extends LibFunction {
    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public abstract LuaValue call(LuaValue luaValue);

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public final LuaValue call() {
        return call(NIL);
    }

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public final LuaValue call(LuaValue arg1, LuaValue arg2) {
        return call(arg1);
    }

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        return call(arg1);
    }

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public Varargs invoke(Varargs varargs) {
        return call(varargs.arg1());
    }
}
