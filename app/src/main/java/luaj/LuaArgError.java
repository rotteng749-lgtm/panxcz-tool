package luaj;

/* JADX INFO: loaded from: classes.dex */
public class LuaArgError extends LuaError {
    private static final long serialVersionUID = -3358127546674542239L;
    public final String funcname;
    public final int iarg;
    public final String msg;

    private String msg() {
        if (this.funcname == null) {
            return String.valueOf(this.iarg == 0 ? "calling method on bad self" : "bad argument #" + this.iarg + ": ") + this.msg;
        }
        return String.valueOf(this.iarg == 0 ? "calling '" + this.funcname + "' on bad self" : "bad argument #" + this.iarg + " to '" + this.funcname + "'") + " (" + this.msg + ")";
    }

    private String funcname(String funcname) {
        StackTraceElement[] trace;
        if (funcname == null && (trace = getStackTrace()) != null) {
            int in = trace.length;
            for (int i = 1; i < in; i++) {
                StackTraceElement el = trace[i];
                String className = el.getClassName();
                if (className != null) {
                    if (!className.endsWith("LuaClosure")) {
                        if (className.contains("Lib$")) {
                            return LuaFunction.name(className);
                        }
                    } else {
                        return funcname;
                    }
                }
            }
            return funcname;
        }
        return funcname;
    }

    public LuaArgError(int iarg, String funcname, String msg) {
        super((String) null);
        this.iarg = iarg;
        this.msg = msg;
        this.funcname = funcname(funcname);
        this.detailMessage = msg();
    }

    LuaArgError(LuaArgError src) {
        super((String) null, src);
        this.iarg = src.iarg - 1;
        this.msg = src.msg;
        this.funcname = src.funcname;
        this.detailMessage = msg();
    }
}
