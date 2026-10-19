package luaj;

import luaj.LuaTable;

/* JADX INFO: loaded from: classes.dex */
class NonTableMetatable implements Metatable {
    private final LuaValue value;

    public NonTableMetatable(LuaValue value) {
        this.value = value;
    }

    @Override // luaj.Metatable
    public boolean useWeakKeys() {
        return false;
    }

    @Override // luaj.Metatable
    public boolean useWeakValues() {
        return false;
    }

    @Override // luaj.Metatable
    public LuaValue toLuaValue() {
        return this.value;
    }

    @Override // luaj.Metatable
    public LuaTable.Slot entry(LuaValue key, LuaValue value) {
        return LuaTable.defaultEntry(key, value);
    }

    @Override // luaj.Metatable
    public LuaValue wrap(LuaValue value) {
        return value;
    }

    @Override // luaj.Metatable
    public LuaValue arrayget(LuaValue[] array, int index) {
        return array[index];
    }
}
