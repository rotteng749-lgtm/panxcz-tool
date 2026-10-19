package luaj.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import luaj.Globals;
import luaj.LuaClosure;
import luaj.LuaFunction;
import luaj.LuaString;
import luaj.LuaValue;
import luaj.Prototype;

/* JADX INFO: loaded from: classes.dex */
public class LuaC extends Constants implements Globals.Compiler, Globals.Loader {
    public static final LuaC instance = new LuaC();

    public static void install(Globals globals) {
        globals.compiler = instance;
        globals.loader = instance;
    }

    protected LuaC() {
    }

    @Override // luaj.Globals.Compiler
    public Prototype compile(InputStream stream, String chunkname) throws IOException {
        return new CompileState().luaY_parser(stream, chunkname);
    }

    @Override // luaj.Globals.Loader
    public LuaFunction load(Prototype prototype, String chunkname, LuaValue env) throws IOException {
        return new LuaClosure(prototype, env, true);
    }

    static class CompileState {
        int nCcalls = 0;
        private Hashtable strings = new Hashtable();

        protected CompileState() {
        }

        Prototype luaY_parser(InputStream z, String name) throws IOException {
            boolean z2 = false;
            LexState lexstate = new LexState(this, z);
            FuncState funcstate = new FuncState();
            lexstate.fs = funcstate;
            lexstate.setinput(this, z.read(), z, LuaValue.valueOf(name));
            funcstate.f = new Prototype();
            funcstate.f.source = LuaValue.valueOf(name);
            lexstate.mainfunc(funcstate);
            LuaC._assert(funcstate.prev == null);
            if (lexstate.dyd == null || (lexstate.dyd.n_actvar == 0 && lexstate.dyd.n_gt == 0 && lexstate.dyd.n_label == 0)) {
                z2 = true;
            }
            LuaC._assert(z2);
            return funcstate.f;
        }

        public LuaString newTString(String s) {
            return cachedLuaString(LuaString.valueOf(s));
        }

        public LuaString newTString(LuaString s) {
            return cachedLuaString(s);
        }

        public LuaString cachedLuaString(LuaString s) {
            LuaString c = (LuaString) this.strings.get(s);
            if (c == null) {
                this.strings.put(s, s);
                return s;
            }
            return c;
        }

        public String pushfstring(String string) {
            return string;
        }
    }
}
