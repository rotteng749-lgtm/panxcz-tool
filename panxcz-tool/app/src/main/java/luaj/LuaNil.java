package luaj;

/* JADX INFO: loaded from: classes.dex */
public class LuaNil extends LuaValue {
    public static LuaValue s_metatable;

    LuaNil() {
    }

    @Override // luaj.LuaValue
    public int type() {
        return 0;
    }

    @Override // luaj.LuaValue, luaj.Varargs
    public String toString() {
        return "nil";
    }

    @Override // luaj.LuaValue
    public String typename() {
        return "nil";
    }

    @Override // luaj.LuaValue, luaj.Varargs
    public String tojstring() {
        return "nil";
    }

    @Override // luaj.LuaValue
    public LuaValue not() {
        return LuaValue.TRUE;
    }

    @Override // luaj.LuaValue
    public boolean toboolean() {
        return false;
    }

    @Override // luaj.LuaValue
    public boolean isnil() {
        return true;
    }

    @Override // luaj.LuaValue
    public LuaValue getmetatable() {
        return s_metatable;
    }

    @Override // luaj.LuaValue
    public boolean equals(Object o) {
        return o instanceof LuaNil;
    }

    @Override // luaj.LuaValue
    public LuaValue checknotnil() {
        return argerror("value");
    }

    @Override // luaj.LuaValue
    public boolean isvalidkey() {
        return false;
    }

    @Override // luaj.LuaValue
    public boolean optboolean(boolean defval) {
        return defval;
    }

    @Override // luaj.LuaValue
    public LuaClosure optclosure(LuaClosure defval) {
        return defval;
    }

    @Override // luaj.LuaValue
    public double optdouble(double defval) {
        return defval;
    }

    @Override // luaj.LuaValue
    public LuaFunction optfunction(LuaFunction defval) {
        return defval;
    }

    @Override // luaj.LuaValue
    public int optint(int defval) {
        return defval;
    }

    @Override // luaj.LuaValue
    public LuaLong optinteger(LuaLong defval) {
        return defval;
    }

    @Override // luaj.LuaValue
    public long optlong(long defval) {
        return defval;
    }

    @Override // luaj.LuaValue
    public LuaNumber optnumber(LuaNumber defval) {
        return defval;
    }

    @Override // luaj.LuaValue
    public LuaTable opttable(LuaTable defval) {
        return defval;
    }

    @Override // luaj.LuaValue
    public LuaThread optthread(LuaThread defval) {
        return defval;
    }

    @Override // luaj.LuaValue
    public String optjstring(String defval) {
        return defval;
    }

    @Override // luaj.LuaValue
    public LuaString optstring(LuaString defval) {
        return defval;
    }

    @Override // luaj.LuaValue
    public LuaValue optvalue(LuaValue defval) {
        return defval;
    }
}
