package luaj;

/* JADX INFO: loaded from: classes.dex */
public class LocVars {
    public int endpc;
    public int startpc;
    public LuaString varname;

    public LocVars(LuaString varname, int startpc, int endpc) {
        this.varname = varname;
        this.startpc = startpc;
        this.endpc = endpc;
    }

    public String tojstring() {
        return new StringBuilder().append(this.varname).append(' ').append(this.startpc).append('-').append(this.endpc).toString();
    }

    public String toString() {
        return tojstring();
    }

    public static void main(String[] args) throws Throwable {
    }
}
