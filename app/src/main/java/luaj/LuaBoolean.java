package luaj;

/* JADX INFO: loaded from: classes.dex */
public final class LuaBoolean extends LuaValue {
    public static LuaValue s_metatable;
    public final boolean v;

    LuaBoolean(boolean b) {
        this.v = b;
    }

    @Override // luaj.LuaValue
    public int type() {
        return 1;
    }

    @Override // luaj.LuaValue
    public String typename() {
        return "boolean";
    }

    @Override // luaj.LuaValue
    public boolean isboolean() {
        return true;
    }

    @Override // luaj.LuaValue
    public LuaValue not() {
        return this.v ? FALSE : LuaValue.TRUE;
    }

    public boolean booleanValue() {
        return this.v;
    }

    @Override // luaj.LuaValue
    public boolean toboolean() {
        return this.v;
    }

    @Override // luaj.LuaValue, luaj.Varargs
    public String tojstring() {
        return this.v ? "true" : "false";
    }

    @Override // luaj.LuaValue
    public boolean optboolean(boolean defval) {
        return this.v;
    }

    @Override // luaj.LuaValue
    public boolean checkboolean() {
        return this.v;
    }

    @Override // luaj.LuaValue
    public LuaValue getmetatable() {
        return s_metatable;
    }
}
