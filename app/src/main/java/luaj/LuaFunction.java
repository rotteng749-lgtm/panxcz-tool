package luaj;

import android.ext.ParserNumbers;
import android.ext.Script;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public abstract class LuaFunction extends LuaValue {
    private static final String GG_LIB = Script.class.getName();
    public static LuaValue s_metatable;

    @Override // luaj.LuaValue
    public int type() {
        return 6;
    }

    @Override // luaj.LuaValue
    public String typename() {
        return "function";
    }

    @Override // luaj.LuaValue
    public boolean isfunction() {
        return true;
    }

    @Override // luaj.LuaValue
    public LuaFunction checkfunction() {
        return this;
    }

    @Override // luaj.LuaValue
    public LuaFunction optfunction(LuaFunction defval) {
        return this;
    }

    @Override // luaj.LuaValue
    public LuaValue getmetatable() {
        return s_metatable;
    }

    @Override // luaj.LuaValue, luaj.Varargs
    public String tojstring() {
        return "function: " + classnamestub();
    }

    @Override // luaj.LuaValue
    public LuaString strvalue() {
        return valueOf(tojstring());
    }

    public String classnamestub() {
        return name(getClass().getName());
    }

    public static String name(String className) {
        int base = className.lastIndexOf(36);
        int pkg = className.lastIndexOf(46);
        int offset = Math.max(pkg, base) + 1;
        if (className.charAt(offset) == '_') {
            offset++;
        }
        String ret = className.substring(offset);
        String lib = null;
        if (className.startsWith(GG_LIB)) {
            lib = "gg.";
        } else if (base > pkg && className.contains("Lib$") && !className.contains("BaseLib$")) {
            lib = String.valueOf(className.substring(pkg + 1, base - 3).toLowerCase(Locale.ENGLISH)) + ParserNumbers.defaultDecimalSeparator;
        }
        return lib == null ? ret : String.valueOf(lib) + ret;
    }

    public String name() {
        return classnamestub();
    }

    public static void main(String[] args) throws Throwable {
    }
}
