package luaj.lib;

import luaj.LuaValue;

/* JADX INFO: loaded from: classes.dex */
class TableLibFunction extends LibFunction {
    TableLibFunction() {
    }

    @Override // luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call() {
        return argerror(1, "table expected, got no value");
    }
}
