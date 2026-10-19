package luaj.lib;

import luaj.LuaValue;
import luaj.Varargs;

/* JADX INFO: loaded from: classes.dex */
public abstract class VarArgFunction extends LibFunction {
    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call() {
        return invoke(NONE).arg1();
    }

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue arg) {
        return invoke(arg).arg1();
    }

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return invoke(varargsOf(arg1, arg2)).arg1();
    }

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        return invoke(varargsOf(arg1, arg2, arg3)).arg1();
    }

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public Varargs invoke(Varargs args) {
        return onInvoke(args, false).eval();
    }
}
