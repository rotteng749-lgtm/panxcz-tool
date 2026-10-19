package luaj;

/* JADX INFO: loaded from: classes.dex */
public class LuaThread extends LuaValue {
    public static final int MAX_CALLSTACK = 256;
    public static final int STATUS_DEAD = 4;
    public static final int STATUS_INITIAL = 0;
    public static final int STATUS_NORMAL = 3;
    public static final int STATUS_RUNNING = 2;
    public static final int STATUS_SUSPENDED = 1;
    public static LuaValue s_metatable = null;
    public static final long thread_orphan_check_interval = 5000;
    public Object callstack;
    public LuaValue errorfunc;
    public final Globals globals;
    public final State state;
    public static int coroutine_count = 0;
    public static final String[] STATUS_NAMES = {"suspended", "suspended", "running", "normal", "dead"};

    public LuaThread(Globals globals) {
        this.state = new State(globals, this, null);
        this.state.status = 2;
        this.globals = globals;
    }

    public LuaThread(Globals globals, LuaValue func) {
        LuaValue.assert_(func != null, "function cannot be null");
        this.state = new State(globals, this, func);
        this.globals = globals;
    }

    @Override // luaj.LuaValue
    public int type() {
        return 8;
    }

    @Override // luaj.LuaValue
    public String typename() {
        return "thread";
    }

    @Override // luaj.LuaValue
    public boolean isthread() {
        return true;
    }

    @Override // luaj.LuaValue
    public LuaThread optthread(LuaThread defval) {
        return this;
    }

    @Override // luaj.LuaValue
    public LuaThread checkthread() {
        return this;
    }

    @Override // luaj.LuaValue
    public LuaValue getmetatable() {
        return s_metatable;
    }

    public String getStatus() {
        return STATUS_NAMES[this.state.status];
    }

    public boolean isMainThread() {
        return this.state.function == null;
    }

    public static class State implements Runnable {
        public int bytecodes;
        public final LuaValue function;
        private final Globals globals;
        public boolean hookcall;
        public int hookcount;
        public LuaFunction hookfunc;
        public boolean hookline;
        public boolean hookrtrn;
        public int inhook;
        public int lastline;
        public int oldpc;
        Varargs args = LuaValue.NONE;
        Varargs result = LuaValue.NONE;
        String error = null;
        public int status = 0;

        State(Globals globals, LuaThread lua_thread, LuaValue function) {
            this.globals = globals;
            this.function = function;
        }

        @Override // java.lang.Runnable
        public synchronized void run() {
            try {
                try {
                    Varargs a = this.args;
                    this.args = LuaValue.NONE;
                    this.result = this.function.invoke(a);
                } catch (Throwable t) {
                    this.error = t.getMessage();
                    this.status = 4;
                    notify();
                }
            } finally {
                this.status = 4;
                notify();
            }
        }
    }
}
