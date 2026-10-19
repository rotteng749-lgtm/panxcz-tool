package luaj;

import luaj.lib.DebugLib;

/* JADX INFO: loaded from: classes.dex */
public class TailcallVarargs extends Varargs {
    private Varargs args;
    private LuaValue func;
    private final Globals globals;
    private Varargs result;

    public TailcallVarargs(Globals globals, LuaValue f, Varargs args) {
        this.globals = globals;
        this.func = f;
        this.args = args;
    }

    @Override // luaj.Varargs
    public boolean isTailcall() {
        return true;
    }

    @Override // luaj.Varargs
    public Varargs eval() {
        Varargs result = this.result;
        if (result == null) {
            Globals glob = this.globals;
            DebugLib debuglib = glob != null ? glob.debuglib : null;
            LuaValue func = this.func;
            Varargs args = this.args;
            while (result == null) {
                if (debuglib != null && !func.isclosure() && func.isfunction()) {
                    debuglib.onCall(func.checkfunction(), true);
                }
                Varargs r = func.onInvoke(args, true);
                if (r.isTailcall()) {
                    TailcallVarargs t = (TailcallVarargs) r;
                    func = t.func;
                    this.func = func;
                    args = t.args;
                    this.args = args;
                } else {
                    result = r;
                    this.result = r;
                    this.func = null;
                    this.args = null;
                }
            }
        }
        return result;
    }

    @Override // luaj.Varargs
    public LuaValue arg(int i) {
        if (this.result == null) {
            eval();
        }
        return this.result.arg(i);
    }

    @Override // luaj.Varargs
    public LuaValue arg1() {
        if (this.result == null) {
            eval();
        }
        return this.result.arg1();
    }

    @Override // luaj.Varargs
    public int narg() {
        if (this.result == null) {
            eval();
        }
        return this.result.narg();
    }

    @Override // luaj.Varargs
    public Varargs subargs(int start) {
        if (this.result == null) {
            eval();
        }
        return this.result.subargs(start);
    }
}
