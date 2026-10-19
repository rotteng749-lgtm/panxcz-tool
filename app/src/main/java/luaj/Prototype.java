package luaj;

import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class Prototype {
    public int[] code;
    public int is_vararg;
    public LuaValue[] k;
    public int lastlinedefined;
    public int linedefined;
    public int[] lineinfo;
    public LocVars[] locvars;
    public int maxstacksize;
    public int numparams;
    public Prototype[] p;
    public LuaString source;
    int tailPos;
    public Upvaldesc[] upvalues;
    private static final Upvaldesc[] NOUPVALUES = new Upvaldesc[0];
    private static final Prototype[] NOSUBPROTOS = new Prototype[0];

    public Prototype() {
        this.tailPos = -1;
        this.p = NOSUBPROTOS;
        this.upvalues = NOUPVALUES;
    }

    public Prototype(int n_upvalues) {
        this.tailPos = -1;
        this.p = NOSUBPROTOS;
        this.upvalues = new Upvaldesc[n_upvalues];
    }

    public String toString() {
        return new StringBuilder().append(this.source).append(':').append(this.linedefined).append('-').append(this.lastlinedefined).toString();
    }

    public LuaString getlocalname(int number, int pc) {
        for (int i = 0; i < this.locvars.length && this.locvars[i].startpc <= pc; i++) {
            if (pc < this.locvars[i].endpc && number - 1 == 0) {
                return this.locvars[i].varname;
            }
        }
        return null;
    }

    public String shortsource() {
        if (this.source == null) {
            return "null";
        }
        String name = this.source.tojstring();
        if ((!name.startsWith("/") || !new File(name).exists()) && name.length() > 124) {
            name = String.valueOf(name.substring(0, LuaError.MAX_STRING_LEN)) + "...";
        }
        if (name.startsWith("@") || name.startsWith("=")) {
            return name.substring(1);
        }
        if (name.startsWith("\u001b")) {
            return LoadState.SOURCE_BINARY_STRING;
        }
        return name;
    }
}
