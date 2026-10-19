package luaj;

/* JADX INFO: loaded from: classes.dex */
public abstract class LuaNumber extends LuaValue {
    public static LuaValue s_metatable;

    @Override // luaj.LuaValue
    public int type() {
        return 3;
    }

    @Override // luaj.LuaValue
    public String typename() {
        return "number";
    }

    @Override // luaj.LuaValue
    public LuaNumber checknumber() {
        return this;
    }

    @Override // luaj.LuaValue
    public LuaNumber checknumber(String errmsg) {
        return this;
    }

    @Override // luaj.LuaValue
    public LuaNumber optnumber(LuaNumber defval) {
        return this;
    }

    @Override // luaj.LuaValue
    public LuaValue tonumber() {
        return this;
    }

    @Override // luaj.LuaValue
    public boolean isnumber() {
        return true;
    }

    @Override // luaj.LuaValue
    public boolean isstring() {
        return true;
    }

    @Override // luaj.LuaValue
    public LuaValue getmetatable() {
        return s_metatable;
    }

    @Override // luaj.LuaValue
    public LuaValue concat(LuaValue rhs) {
        return rhs.concatTo(this);
    }

    @Override // luaj.LuaValue
    public LuaValue concatTo(LuaNumber lhs) {
        return strvalue().concatTo(lhs.strvalue());
    }

    @Override // luaj.LuaValue
    public LuaValue concatTo(LuaString lhs) {
        return strvalue().concatTo(lhs);
    }

    @Override // luaj.LuaValue
    public int strcmp(LuaString rhs) {
        String str = rhs.toString();
        if (str.length() > 50) {
            str = String.valueOf(str.substring(0, 50)) + "...";
        }
        throw new LuaError("attempt to compare number with string (" + this + " with '" + str + "')");
    }
}
