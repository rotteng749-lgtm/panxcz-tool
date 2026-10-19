package luaj.lib;

import android.ext.ParserNumbers;
import android.ext.SearchMenuItem;
import android.ext.Searcher;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import luaj.Globals;
import luaj.LuaError;
import luaj.LuaFunction;
import luaj.LuaString;
import luaj.LuaTable;
import luaj.LuaValue;
import luaj.Varargs;

/* JADX INFO: loaded from: classes.dex */
public class PackageLib extends TwoArgFunction {
    public static final String DEFAULT_LUA_PATH;
    static final String FILE_SEP;
    static final LuaString _LOADED;
    private static final LuaString _LOADLIB;
    static final LuaString _PATH;
    static final LuaString _PRELOAD;
    static final LuaString _SEARCHERS;
    static final LuaString _SEARCHPATH;
    static final LuaString _SENTINEL;
    Globals globals;
    public java_searcher java_searcher;
    public lua_searcher lua_searcher;
    LuaTable package_;
    public preload_searcher preload_searcher;

    static {
        String path = null;
        try {
            path = System.getProperty("luaj.package.path");
        } catch (Exception e) {
            Log.w("PackageLib", "Failed read system property: luaj.package.path", e);
        }
        if (path == null) {
            path = "?.lua";
        }
        DEFAULT_LUA_PATH = path;
        _LOADED = valueOf("loaded");
        _LOADLIB = valueOf("loadlib");
        _PRELOAD = valueOf("preload");
        _PATH = valueOf("path");
        _SEARCHPATH = valueOf("searchpath");
        _SEARCHERS = valueOf("searchers");
        _SENTINEL = valueOf("\u0001");
        FILE_SEP = System.getProperty("file.separator");
    }

    @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue modname, LuaValue env) {
        this.globals = env.checkglobals();
        this.globals.set("require", new require());
        this.package_ = new LuaTable();
        this.package_.set(_LOADED, new LuaTable());
        this.package_.set(_PRELOAD, new LuaTable());
        this.package_.set(_PATH, LuaValue.valueOf(DEFAULT_LUA_PATH));
        this.package_.set(_LOADLIB, new loadlib());
        this.package_.set(_SEARCHPATH, new searchpath());
        LuaTable searchers = new LuaTable();
        preload_searcher preload_searcherVar = new preload_searcher();
        this.preload_searcher = preload_searcherVar;
        searchers.set(1, preload_searcherVar);
        lua_searcher lua_searcherVar = new lua_searcher();
        this.lua_searcher = lua_searcherVar;
        searchers.set(2, lua_searcherVar);
        java_searcher java_searcherVar = new java_searcher();
        this.java_searcher = java_searcherVar;
        searchers.set(3, java_searcherVar);
        this.package_.set(_SEARCHERS, searchers);
        this.package_.set("config", String.valueOf(FILE_SEP) + "\n;\n?\n!\n-\n");
        this.package_.get(_LOADED).set("package", this.package_);
        env.set("package", this.package_);
        this.globals.package_ = this;
        return env;
    }

    public void setIsLoaded(String name, LuaTable value) {
        this.package_.get(_LOADED).set(name, value);
    }

    public void setLuaPath(String newLuaPath) {
        this.package_.set(_PATH, LuaValue.valueOf(newLuaPath));
    }

    @Override // luaj.lib.LibFunction, luaj.LuaFunction, luaj.LuaValue, luaj.Varargs
    public String tojstring() {
        return "package";
    }

    public class require extends OneArgFunction {
        public require() {
        }

        @Override // luaj.lib.OneArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            Varargs loader;
            LuaString name = arg.checkstring();
            LuaValue loaded = PackageLib.this.package_.get(PackageLib._LOADED);
            LuaValue result = loaded.get(name);
            if (result.toboolean()) {
                if (result == PackageLib._SENTINEL) {
                    error("loop or previous error loading module '" + name + SearchMenuItem.HEX_UTF_8);
                }
                return result;
            }
            LuaValue srch = PackageLib.this.package_.get(PackageLib._SEARCHERS);
            if (!srch.istable()) {
                error("'package.searchers' must be a table");
            }
            LuaTable tbl = srch.checktable();
            StringBuffer sb = new StringBuffer();
            LuaValue src = null;
            int i = 1;
            while (true) {
                try {
                    LuaValue searcher = tbl.get(i);
                    if (searcher.isnil()) {
                        error("module '" + name + "' not found: " + name + ((Object) sb));
                    }
                    loader = searcher.invoke(name);
                    if (loader.isfunction(1)) {
                        break;
                    }
                    if (loader.isstring(1)) {
                        sb.append(loader.tojstring(1));
                    }
                    i++;
                } catch (Exception e) {
                    if (e instanceof LuaError) {
                        ((LuaError) e).prefix = "error loading module '" + name + "'" + (src == null ? "" : " from file '" + src + "'") + ":\n";
                    }
                    throw e;
                }
            }
            loaded.set(name, PackageLib._SENTINEL);
            src = loader.arg(2);
            LuaValue result2 = loader.arg1().call(name, src);
            if (!result2.isnil()) {
                loaded.set(name, result2);
            } else {
                result2 = loaded.get(name);
                if (result2 == PackageLib._SENTINEL) {
                    result2 = LuaValue.TRUE;
                    loaded.set(name, result2);
                }
            }
            return result2;
        }
    }

    public static class loadlib extends VarArgFunction {
        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            args.checkstring(1);
            return varargsOf(NIL, valueOf("dynamic libraries not enabled"), valueOf("absent"));
        }
    }

    public class preload_searcher extends VarArgFunction {
        public preload_searcher() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaString name = args.checkstring(1);
            LuaValue val = PackageLib.this.package_.get(PackageLib._PRELOAD).get(name);
            if (!val.isnil()) {
                return val;
            }
            return valueOf("\n\tno field package.preload['" + name + "']");
        }
    }

    public class lua_searcher extends VarArgFunction {
        public lua_searcher() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaString name = args.checkstring(1);
            LuaValue path = PackageLib.this.package_.get(PackageLib._PATH);
            if (!path.isstring()) {
                return valueOf("\n\tpackage.path is not a string");
            }
            Varargs v = PackageLib.this.package_.get(PackageLib._SEARCHPATH).invoke(varargsOf(name, path));
            if (!v.isstring(1)) {
                return v.arg(2).tostring();
            }
            LuaString filename = v.arg1().strvalue();
            String file = filename.tojstring();
            PackageLib.this.globals.baselib.debugLoad(null, file, true, null);
            Varargs v2 = PackageLib.this.globals.loadfile(file);
            if (v2.arg1().isfunction()) {
                return LuaValue.varargsOf(v2.arg1(), filename);
            }
            return varargsOf(NIL, valueOf("'" + filename + "': " + v2.arg(2).tojstring()));
        }
    }

    public class searchpath extends VarArgFunction {
        public searchpath() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            String name = args.checkjstring(1);
            String path = args.checkjstring(2);
            String sep = args.optjstring(3, ".");
            String rep = args.optjstring(4, PackageLib.FILE_SEP);
            int e = -1;
            int n = path.length();
            StringBuffer sb = null;
            if (sep.length() > 0) {
                name = name.replace(sep, rep);
            }
            while (e < n) {
                int b = e + 1;
                e = path.indexOf(59, b);
                if (e < 0) {
                    e = path.length();
                }
                String template = path.substring(b, e);
                String filename = template.replace(Searcher.MASK_PLACEHOLDER, name);
                InputStream is = PackageLib.this.globals.finder.findResource(filename);
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e2) {
                    }
                    return valueOf(filename);
                }
                if (sb == null) {
                    sb = new StringBuffer();
                }
                sb.append("\n\t" + filename);
            }
            return varargsOf(NIL, valueOf(sb.toString()));
        }
    }

    public class java_searcher extends VarArgFunction {
        public java_searcher() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            String name = args.checkjstring(1);
            String classname = PackageLib.toClassname(name);
            try {
                LuaValue v = (LuaValue) Class.forName(classname).newInstance();
                if (v.isfunction()) {
                    ((LuaFunction) v).initupvalue1(PackageLib.this.globals);
                }
                return varargsOf(v, PackageLib.this.globals);
            } catch (ClassNotFoundException e) {
                return valueOf("\n\tno class '" + classname + SearchMenuItem.HEX_UTF_8);
            } catch (Exception e2) {
                return valueOf("\n\tjava load failed on '" + classname + "', " + e2);
            }
        }
    }

    public static final String toClassname(String filename) {
        char c;
        int n = filename.length();
        int j = n;
        if (filename.endsWith(".lua")) {
            j -= 4;
        }
        for (int k = 0; k < j; k++) {
            char c2 = filename.charAt(k);
            if (!isClassnamePart(c2) || c2 == '/' || c2 == '\\') {
                StringBuffer sb = new StringBuffer(j);
                for (int i = 0; i < j; i++) {
                    char c3 = filename.charAt(i);
                    if (isClassnamePart(c3)) {
                        c = c3;
                    } else {
                        c = (c3 == '/' || c3 == '\\') ? ParserNumbers.defaultDecimalSeparator : '_';
                    }
                    sb.append(c);
                }
                return sb.toString();
            }
        }
        return n == j ? filename : filename.substring(0, j);
    }

    private static final boolean isClassnamePart(char c) {
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c >= 'A' && c <= 'Z') {
            return true;
        }
        if (c >= '0' && c <= '9') {
            return true;
        }
        switch (c) {
            case '$':
            case '.':
            case '_':
                break;
        }
        return true;
    }

    public static void main(String[] args) throws Throwable {
    }
}
