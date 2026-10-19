package luaj.lib;

import luaj.LuaValue;
import luaj.Varargs;

/* JADX INFO: loaded from: classes.dex */
public abstract class ZeroArgFunction extends LibFunction {
    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public abstract LuaValue call();

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue arg) {
        return call();
    }

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return call();
    }

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        return call();
    }

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public Varargs invoke(Varargs varargs) {
        return call();
    }
}
