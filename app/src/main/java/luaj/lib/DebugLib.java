package luaj.lib;

import android.ext.Message;
import android.ext.ParserNumbers;
import android.ext.SearchMenuItem;
import android.ext.Searcher;
import lasm.LasmConstants;
import luaj.Globals;
import luaj.LoadState;
import luaj.Lua;
import luaj.LuaBoolean;
import luaj.LuaClosure;
import luaj.LuaError;
import luaj.LuaFunction;
import luaj.LuaNil;
import luaj.LuaNumber;
import luaj.LuaString;
import luaj.LuaTable;
import luaj.LuaThread;
import luaj.LuaValue;
import luaj.Print;
import luaj.Prototype;
import luaj.Upvaldesc;
import luaj.Varargs;

/* JADX INFO: loaded from: classes.dex */
public class DebugLib extends TwoArgFunction {
    static final LuaString ACTIVELINES;
    private static final LuaString CALL;
    public static boolean CALLS;
    private static final LuaString COUNT;
    static final LuaString CURRENTLINE;
    static final LuaString FUNC;
    static final LuaString ISTAILCALL;
    static final LuaString ISVARARG;
    static final LuaString LASTLINEDEFINED;
    private static final LuaString LINE;
    static final LuaString LINEDEFINED;
    static final LuaString NAME;
    static final LuaString NAMEWHAT;
    static final LuaString NPARAMS;
    static final LuaString NUPS;
    private static final LuaString QMARK;
    private static final LuaString RETURN;
    static final LuaString SHORT_SRC;
    static final LuaString SOURCE;
    private static final LuaString TAILCALL;
    static final LuaString TEMPORARY;
    public static boolean TRACE;
    static final LuaString VARARG;
    static final LuaString WHAT;
    Globals globals;

    static {
        try {
            CALLS = System.getProperty("CALLS") != null;
        } catch (Exception e) {
        }
        try {
            TRACE = System.getProperty("TRACE") != null;
        } catch (Exception e2) {
        }
        QMARK = valueOf(Searcher.MASK_PLACEHOLDER);
        CALL = valueOf("call");
        TAILCALL = valueOf("tail call");
        LINE = valueOf("line");
        COUNT = valueOf("count");
        RETURN = valueOf("return");
        FUNC = valueOf("func");
        ISTAILCALL = valueOf("istailcall");
        ISVARARG = valueOf("isvararg");
        NUPS = valueOf("nups");
        NPARAMS = valueOf("nparams");
        NAME = valueOf("name");
        NAMEWHAT = valueOf("namewhat");
        WHAT = valueOf("what");
        SOURCE = valueOf("source");
        SHORT_SRC = valueOf("short_src");
        LINEDEFINED = valueOf("linedefined");
        LASTLINEDEFINED = valueOf("lastlinedefined");
        CURRENTLINE = valueOf("currentline");
        ACTIVELINES = valueOf("activelines");
        TEMPORARY = valueOf("(*temporary)");
        VARARG = valueOf("(*vararg)");
    }

    @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue modname, LuaValue env) {
        this.globals = env.checkglobals();
        this.globals.debuglib = this;
        LuaTable debug2 = new LuaTable();
        debug2.set("debug", new debug());
        debug2.set("gethook", new gethook());
        debug2.set("getinfo", new getinfo());
        debug2.set("getlocal", new getlocal());
        debug2.set("getmetatable", new getmetatable());
        debug2.set("getregistry", new getregistry());
        debug2.set("getupvalue", new getupvalue());
        debug2.set("sethook", new sethook());
        debug2.set("setlocal", new setlocal());
        debug2.set("setmetatable", new setmetatable());
        debug2.set("setupvalue", new setupvalue());
        debug2.set("traceback", new traceback());
        debug2.set("upvalueid", new upvalueid());
        debug2.set("upvaluejoin", new upvaluejoin());
        env.set("debug", debug2);
        if (!env.get("package").isnil()) {
            env.get("package").get("loaded").set("debug", debug2);
        }
        return debug2;
    }

    static final class debug extends ZeroArgFunction {
        debug() {
        }

        @Override // luaj.lib.ZeroArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call() {
            return NONE;
        }
    }

    final class gethook extends VarArgFunction {
        gethook() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaThread t = args.narg() > 0 ? args.checkthread(1) : DebugLib.this.globals.running;
            LuaThread.State s = t.state;
            return varargsOf(s.hookfunc != null ? s.hookfunc : NIL, valueOf(String.valueOf(s.hookcall ? "c" : "") + (s.hookrtrn ? "r" : "") + (s.hookline ? "l" : "")), valueOf(s.hookcount));
        }
    }

    final class getinfo extends VarArgFunction {
        getinfo() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaThread thread;
            CallFrame frame;
            int a = 1;
            if (args.isthread(1)) {
                int a2 = 1 + 1;
                thread = args.checkthread(1);
                a = a2;
            } else {
                thread = DebugLib.this.globals.running;
            }
            int a3 = a + 1;
            LuaValue func = args.arg(a);
            int a4 = a3 + 1;
            String what = args.optjstring(a3, "flnStu");
            CallStack callstack = DebugLib.this.callstack(thread);
            if (func.isnumber()) {
                int level = func.toint();
                if (callstack.getCallFrame(1).f == this) {
                    level++;
                }
                frame = callstack.getCallFrame(level);
                if (frame == null) {
                    return NONE;
                }
                func = frame.f;
            } else if (func.isfunction()) {
                frame = callstack.findCallFrame(func);
            } else {
                return argerror(a4 - 2, "function or level");
            }
            DebugInfo ar = callstack.auxgetinfo(what, (LuaFunction) func, frame);
            LuaTable info = new LuaTable();
            for (int i = 0; i < what.length(); i++) {
                switch (what.charAt(i)) {
                    case LasmConstants.LABEL /* 76 */:
                        if (func.isclosure()) {
                            LuaTable lines = new LuaTable();
                            info.set(DebugLib.ACTIVELINES, lines);
                            int[] li = func.checkclosure().p.lineinfo;
                            for (int i2 : li) {
                                lines.rawset(valueOf(i2), TRUE);
                            }
                        }
                        break;
                    case LasmConstants.NL /* 83 */:
                        info.set(DebugLib.WHAT, valueOf(ar.what));
                        info.set(DebugLib.SOURCE, valueOf(ar.source));
                        info.set(DebugLib.SHORT_SRC, valueOf(ar.short_src));
                        info.set(DebugLib.LINEDEFINED, valueOf(ar.linedefined));
                        info.set(DebugLib.LASTLINEDEFINED, valueOf(ar.lastlinedefined));
                        break;
                    case 'f':
                        if (func != null) {
                            info.set(DebugLib.FUNC, func);
                        }
                        break;
                    case Message.ERROR_GROUP_FAIL /* 108 */:
                        info.set(DebugLib.CURRENTLINE, valueOf(ar.currentline));
                        break;
                    case 'n':
                        info.set(DebugLib.NAME, ar.name != null ? LuaValue.valueOf(ar.name) : NIL);
                        info.set(DebugLib.NAMEWHAT, LuaValue.valueOf(ar.namewhat));
                        break;
                    case 't':
                        info.set(DebugLib.ISTAILCALL, valueOf(ar.istailcall));
                        break;
                    case 'u':
                        info.set(DebugLib.NUPS, valueOf(ar.nups));
                        info.set(DebugLib.NPARAMS, valueOf(ar.nparams));
                        info.set(DebugLib.ISVARARG, ar.isvararg ? TRUE : FALSE);
                        break;
                    default:
                        char ch = what.charAt(i);
                        argerror(a4 - 1, "debug.getinfo", "invalid option: '" + ch + "' [0x" + Integer.toHexString(ch) + "] at index " + (i + 1) + " in '" + what + "'");
                        break;
                }
            }
            return info;
        }
    }

    final class getlocal extends VarArgFunction {
        getlocal() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaThread thread;
            int a = 1;
            if (args.isthread(1)) {
                int a2 = 1 + 1;
                thread = args.checkthread(1);
                a = a2;
            } else {
                thread = DebugLib.this.globals.running;
            }
            int a3 = a + 1;
            int farg = a;
            LuaValue func = args.arg(farg);
            int i = a3 + 1;
            int local = args.checkint(a3);
            if (func.isfunction()) {
                return !func.isclosure() ? NIL : func.checkclosure().p.getlocalname(local, 0);
            }
            int level = args.checkint(farg);
            CallStack callstack = DebugLib.this.callstack(thread);
            int max = callstack.calls;
            if (callstack.getCallFrame(1).f == this) {
                level++;
                max--;
            }
            CallFrame f = callstack.getCallFrame(level);
            return f != null ? f.getLocal(local) : argerror(farg, "debug.getlocal", "level out of range: " + level + " not between 1 and " + max);
        }
    }

    static final class getmetatable extends LibFunction {
        getmetatable() {
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue v) {
            LuaValue mt = v.getmetatable();
            return mt != null ? mt : NIL;
        }
    }

    final class getregistry extends ZeroArgFunction {
        getregistry() {
        }

        @Override // luaj.lib.ZeroArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call() {
            return DebugLib.this.globals;
        }
    }

    static final class getupvalue extends VarArgFunction {
        getupvalue() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaClosure c;
            LuaString name;
            LuaValue func = args.checkfunction(1);
            int up = args.checkint(2);
            return (!(func instanceof LuaClosure) || (name = DebugLib.findupvalue((c = (LuaClosure) func), up)) == null) ? NIL : varargsOf(name, c.upValues[up - 1].getValue());
        }
    }

    final class sethook extends VarArgFunction {
        sethook() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaThread t;
            int a = 1;
            if (args.isthread(1)) {
                int a2 = 1 + 1;
                t = args.checkthread(1);
                a = a2;
            } else {
                t = DebugLib.this.globals.running;
            }
            int a3 = a + 1;
            LuaFunction func = args.optfunction(a, null);
            int a4 = a3 + 1;
            String str = args.optjstring(a3, "");
            int i = a4 + 1;
            int count = args.optint(a4, 0);
            boolean call = false;
            boolean line = false;
            boolean rtrn = false;
            for (int i2 = 0; i2 < str.length(); i2++) {
                switch (str.charAt(i2)) {
                    case 'c':
                        call = true;
                        break;
                    case Message.ERROR_GROUP_FAIL /* 108 */:
                        line = true;
                        break;
                    case 'r':
                        rtrn = true;
                        break;
                }
            }
            LuaThread.State s = t.state;
            s.hookfunc = func;
            s.hookcall = call;
            s.hookline = line;
            s.hookcount = count;
            s.hookrtrn = rtrn;
            DebugLib.this.callstack().setInfo(s);
            return NONE;
        }
    }

    final class setlocal extends VarArgFunction {
        setlocal() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaThread thread;
            int a = 1;
            if (args.isthread(1)) {
                int a2 = 1 + 1;
                thread = args.checkthread(1);
                a = a2;
            } else {
                thread = DebugLib.this.globals.running;
            }
            int larg = a;
            int a3 = a + 1;
            int level = args.checkint(a);
            int a4 = a3 + 1;
            int local = args.checkint(a3);
            int i = a4 + 1;
            LuaValue value = args.arg(a4);
            CallStack callstack = DebugLib.this.callstack(thread);
            int max = callstack.calls;
            if (callstack.getCallFrame(1).f == this) {
                level++;
                max--;
            }
            CallFrame f = callstack.getCallFrame(level);
            return f != null ? f.setLocal(local, value) : argerror(larg, "debug.setlocal", "level out of range: " + level + " not between 1 and " + max);
        }
    }

    static final class setmetatable extends TwoArgFunction {
        setmetatable() {
        }

        @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue value, LuaValue table) {
            LuaValue mt = table.opttable(null);
            switch (value.type()) {
                case 0:
                    LuaNil.s_metatable = mt;
                    return value;
                case 1:
                    LuaBoolean.s_metatable = mt;
                    return value;
                case 2:
                case 5:
                case 7:
                default:
                    value.setmetatable(mt);
                    return value;
                case 3:
                    LuaNumber.s_metatable = mt;
                    return value;
                case 4:
                    LuaString.s_metatable = mt;
                    return value;
                case 6:
                    LuaFunction.s_metatable = mt;
                    return value;
                case 8:
                    LuaThread.s_metatable = mt;
                    return value;
            }
        }
    }

    static final class setupvalue extends VarArgFunction {
        setupvalue() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaClosure c;
            LuaString name;
            LuaValue func = args.checkfunction(1);
            int up = args.checkint(2);
            LuaValue value = args.arg(3);
            if (!(func instanceof LuaClosure) || (name = DebugLib.findupvalue((c = (LuaClosure) func), up)) == null) {
                return NIL;
            }
            c.upValues[up - 1].setValue(value);
            return name;
        }
    }

    final class traceback extends VarArgFunction {
        traceback() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaThread thread;
            int a = 1;
            if (args.isthread(1)) {
                int a2 = 1 + 1;
                thread = args.checkthread(1);
                a = a2;
            } else {
                thread = DebugLib.this.globals.running;
            }
            int a3 = a + 1;
            LuaValue message = args.arg(a);
            if (message.isstring() || message.isnil()) {
                int a4 = a3 + 1;
                int level = args.optint(a3, 1);
                CallStack callstack = DebugLib.this.callstack(thread);
                if (callstack.getCallFrame(1).f == this) {
                    level++;
                }
                String tb = callstack.traceback(DebugLib.this.globals, level);
                if (message.isstring()) {
                    tb = String.valueOf(message.tojstring()) + '\n' + tb;
                }
                return valueOf(tb);
            }
            return message;
        }
    }

    static final class upvalueid extends VarArgFunction {
        upvalueid() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaValue func = args.checkfunction(1);
            int up = args.checkint(2);
            if (func instanceof LuaClosure) {
                LuaClosure c = (LuaClosure) func;
                if (c.upValues != null && up > 0 && up <= c.upValues.length) {
                    return valueOf(c.upValues[up - 1].hashCode());
                }
            }
            return argerror(2, "debug.upvalueid", "invalid upvalue index " + up);
        }
    }

    static final class upvaluejoin extends VarArgFunction {
        upvaluejoin() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaClosure f1 = args.checkclosure(1);
            int n1 = args.checkint(2);
            LuaClosure f2 = args.checkclosure(3);
            int n2 = args.checkint(4);
            if (n1 < 1 || n1 > f1.upValues.length) {
                argerror("index out of range");
            }
            if (n2 < 1 || n2 > f2.upValues.length) {
                argerror("index out of range");
            }
            f1.upValues[n1 - 1] = f2.upValues[n2 - 1];
            return NONE;
        }
    }

    public void onCall(LuaFunction f, boolean tailcall) {
        LuaThread.State s = this.globals.running.state;
        boolean hookcall = s.inhook == 1;
        if (hookcall) {
            s.inhook = 2;
        }
        callstack().onCall(f, hookcall, tailcall);
        if (s.inhook == 0) {
            if (s.hookline) {
                s.lastline = 0;
                s.oldpc = 0;
            }
            if (s.hookcall) {
                callHook(s, tailcall ? TAILCALL : CALL, NIL);
            }
        }
    }

    public void onCall(LuaClosure c, Varargs varargs, LuaValue[] stack, boolean tailcall) {
        LuaThread.State s = this.globals.running.state;
        boolean hookcall = s.inhook == 1;
        if (hookcall) {
            s.inhook = 2;
        }
        callstack().onCall(c, varargs, stack, hookcall, tailcall);
        if (s.inhook == 0) {
            if (s.hookline) {
                s.lastline = 0;
                s.oldpc = 0;
            }
            if (s.hookcall) {
                callHook(s, tailcall ? TAILCALL : CALL, NIL);
            }
        }
    }

    public void onInstruction(int pc, Varargs v, int top) {
        LuaThread thread = this.globals.running;
        LuaThread.State s = thread.state;
        callstack(thread).onInstruction(pc, v, top);
        if (s.inhook == 0 && s.hookfunc != null) {
            if (s.hookcount > 0) {
                int i = s.bytecodes + 1;
                s.bytecodes = i;
                if (i % s.hookcount == 0) {
                    callHook(s, COUNT, NIL);
                }
            }
            if (s.hookline) {
                int newline = callstack().currentline();
                if (newline != s.lastline || pc <= s.oldpc) {
                    s.lastline = newline;
                    callHook(s, LINE, LuaValue.valueOf(newline));
                }
                s.oldpc = pc;
            }
        }
    }

    public void onReturn() {
        LuaThread.State s = this.globals.running.state;
        if (s.inhook == 0 && s.hookrtrn) {
            callHook(s, RETURN, NIL);
        }
        callstack().onReturn(s);
    }

    public String traceback(Globals globals, int level) {
        return callstack().traceback(globals, level);
    }

    public CallFrame getCallFrame(int level) {
        return callstack().getCallFrame(level);
    }

    void callHook(LuaThread.State s, LuaValue type, LuaValue arg) {
        LuaFunction func = s.hookfunc;
        if (s.inhook == 0 && func != null) {
            s.inhook = 1;
            try {
                try {
                    boolean notcl = func.isclosure() ? false : true;
                    if (notcl) {
                        onCall(func, false);
                    }
                    try {
                        func.call(type, arg);
                    } finally {
                        if (notcl) {
                            onReturn();
                        }
                    }
                } catch (LuaError e) {
                    throw e;
                } catch (RuntimeException e2) {
                    throw new LuaError.Internal(e2);
                }
            } finally {
                s.inhook = 0;
            }
        }
    }

    CallStack callstack() {
        return callstack(this.globals.running);
    }

    CallStack callstack(LuaThread t) {
        if (t.callstack == null) {
            t.callstack = new CallStack();
        }
        return (CallStack) t.callstack;
    }

    static class DebugInfo {
        CallFrame cf;
        int currentline;
        boolean istailcall;
        boolean isvararg;
        int lastlinedefined;
        int linedefined;
        String name;
        String namewhat;
        short nparams;
        short nups;
        String short_src;
        String source;
        String what;

        DebugInfo() {
        }

        public void funcinfo(LuaFunction f) {
            if (f.isclosure()) {
                Prototype p = f.checkclosure().p;
                this.source = p.source != null ? p.source.tojstring() : LoadState.SOURCE_BINARY_STRING;
                this.linedefined = p.linedefined;
                this.lastlinedefined = p.lastlinedefined;
                this.what = this.linedefined == 0 ? "main" : "Lua";
                this.short_src = p.shortsource();
                return;
            }
            this.source = "=[Java]";
            this.linedefined = -1;
            this.lastlinedefined = -1;
            this.what = "Java";
            this.short_src = "[Java]";
        }

        public String toString() {
            return "DebugInfo [\n\tname=" + this.name + "\n\tnamewhat=" + this.namewhat + "\n\twhat=" + this.what + "\n\tsource=" + this.source + "\n\tcurrentline=" + this.currentline + "\n\tlinedefined=" + this.linedefined + "\n\tlastlinedefined=" + this.lastlinedefined + "\n\tnups=" + ((int) this.nups) + "\n\tnparams=" + ((int) this.nparams) + "\n\tisvararg=" + this.isvararg + "\n\tistailcall=" + this.istailcall + "\n\tshort_src=" + this.short_src + "\n\tcf=" + this.cf + "\n]";
        }
    }

    public static class CallStack {
        static final CallFrame[] EMPTY = new CallFrame[0];
        CallFrame[] frame = EMPTY;
        int calls = 0;

        CallStack() {
        }

        synchronized int currentline() {
            return this.calls > 0 ? this.frame[this.calls - 1].currentline() : -1;
        }

        synchronized void setInfo(LuaThread.State s) {
            if (s.hookline && this.calls > 0) {
                CallFrame f = this.frame[this.calls - 1];
                s.lastline = f.currentline();
                s.oldpc = f.pc;
            }
        }

        private synchronized CallFrame pushcall(boolean tailcall) {
            CallFrame callFrame;
            if (tailcall) {
                callFrame = this.frame[this.calls - 1];
            } else {
                if (this.calls >= this.frame.length) {
                    int n = Math.max(4, (this.frame.length * 3) / 2);
                    CallFrame[] f = new CallFrame[n];
                    System.arraycopy(this.frame, 0, f, 0, this.frame.length);
                    for (int i = this.frame.length; i < n; i++) {
                        f[i] = new CallFrame();
                    }
                    this.frame = f;
                    for (int i2 = 1; i2 < n; i2++) {
                        f[i2].previous = f[i2 - 1];
                    }
                }
                CallFrame[] callFrameArr = this.frame;
                int i3 = this.calls;
                this.calls = i3 + 1;
                callFrame = callFrameArr[i3];
            }
            return callFrame;
        }

        final synchronized void onCall(LuaFunction function, boolean hookcall, boolean tailcall) {
            pushcall(tailcall).set(function, hookcall, tailcall);
        }

        final synchronized void onCall(LuaClosure function, Varargs varargs, LuaValue[] stack, boolean hookcall, boolean tailcall) {
            pushcall(tailcall).set(function, varargs, stack, hookcall, tailcall);
        }

        final synchronized void onReturn(LuaThread.State s) {
            if (this.calls > 0) {
                CallFrame[] callFrameArr = this.frame;
                int i = this.calls - 1;
                this.calls = i;
                callFrameArr[i].reset();
            }
            setInfo(s);
        }

        final synchronized void onInstruction(int pc, Varargs v, int top) {
            int calls = this.calls;
            if (calls > 0) {
                this.frame[calls - 1].instr(pc, v, top);
            }
        }

        private static LuaValue findfield(StringBuffer sb, LuaTable table, LuaValue obj, int level) {
            LuaValue name;
            if (table != null) {
                LuaTable.Iterator it = table.iterator();
                while (it.next()) {
                    if (it.intkey() == 0) {
                        LuaValue key = it.key();
                        if (key instanceof LuaString) {
                            LuaValue value = it.value();
                            if (value == obj) {
                                if (level != 2) {
                                    return key;
                                }
                                sb.append("function '");
                                sb.append(key.tojstring());
                                sb.append(SearchMenuItem.HEX_UTF_8);
                                return DebugLib.NIL;
                            }
                            if (level > 1 && value.istable() && (name = findfield(sb, value.checktable(), obj, level - 1)) != null) {
                                String kname = key.tojstring();
                                sb.append("function '");
                                if (!kname.equals("_G")) {
                                    sb.append(kname);
                                    sb.append(ParserNumbers.defaultDecimalSeparator);
                                }
                                sb.append(name.tojstring());
                                sb.append(SearchMenuItem.HEX_UTF_8);
                                return DebugLib.NIL;
                            }
                        } else {
                            continue;
                        }
                    }
                }
            }
            return null;
        }

        private static void pushfuncname(StringBuffer sb, Globals globals, DebugInfo ar) {
            if (findfield(sb, globals, ar.cf.f, 2) == null) {
                if (ar.namewhat.length() > 0) {
                    sb.append(ar.namewhat);
                    sb.append(" '");
                    sb.append(ar.name);
                    sb.append(SearchMenuItem.HEX_UTF_8);
                    return;
                }
                if (ar.what.charAt(0) == 'm') {
                    sb.append("main chunk");
                    return;
                }
                if (ar.what.charAt(0) != 'J') {
                    sb.append("function <");
                    sb.append(ar.short_src);
                    sb.append(':');
                    sb.append(ar.linedefined);
                    sb.append('>');
                    return;
                }
                sb.append('?');
            }
        }

        synchronized String traceback(Globals globals, int level) {
            StringBuffer sb;
            sb = new StringBuffer();
            sb.append("stack traceback:");
            while (true) {
                int level2 = level;
                level = level2 + 1;
                CallFrame c = getCallFrame(level2);
                if (c != null) {
                    DebugInfo ar = auxgetinfo("Slnt", c.f, c);
                    ar.cf = c;
                    sb.append("\n\t");
                    sb.append(ar.short_src);
                    sb.append(':');
                    if (ar.currentline > 0) {
                        sb.append(ar.currentline);
                    }
                    sb.append(" in ");
                    pushfuncname(sb, globals, ar);
                    if (ar.istailcall) {
                        sb.append("\n\t(...tail calls...)");
                    }
                } else {
                    sb.append("\n\t[Java]: in ?");
                    break;
                }
            }
            return sb.toString();
        }

        /* JADX WARN: Removed duplicated region for block: B:7:0x0008  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        synchronized luaj.lib.DebugLib.CallFrame getCallFrame(int level) {
            if (level < 1 || level > this.calls) {
                return null;
            }
            return this.frame[this.calls - level];
        }

        /* JADX WARN: Code restructure failed: missing block: B:11:0x0014, code lost:
        
            r1 = r3.frame[r0];
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        synchronized luaj.lib.DebugLib.CallFrame findCallFrame(luaj.LuaValue func) {
            for (int level = 1; level <= this.calls; level++) {
                CallFrame frame = this.frame[this.calls - level];
                if (frame != null && frame.f == func) {
                    return frame;
                }
            }
            return null;
        }

        synchronized DebugInfo auxgetinfo(String what, LuaFunction f, CallFrame ci) {
            DebugInfo ar;
            NameWhat nw;
            ar = new DebugInfo();
            int n = what.length();
            for (int i = 0; i < n; i++) {
                switch (what.charAt(i)) {
                    case LasmConstants.NL /* 83 */:
                        ar.funcinfo(f);
                        break;
                    case Message.ERROR_GROUP_FAIL /* 108 */:
                        ar.currentline = (ci == null || !ci.f.isclosure()) ? -1 : ci.currentline();
                        break;
                    case 'n':
                        if (ci != null && !ci.tailcall) {
                            if (ci.hookcall) {
                                ar.namewhat = "hook";
                                ar.name = Searcher.MASK_PLACEHOLDER;
                            } else if (ci.previous != null && ci.previous.f.isclosure() && (nw = DebugLib.getfuncname(ci.previous)) != null) {
                                ar.name = nw.name;
                                ar.namewhat = nw.namewhat;
                            }
                        }
                        if (ar.namewhat == null) {
                            ar.namewhat = "";
                            ar.name = null;
                        }
                        break;
                    case 't':
                        ar.istailcall = ci != null && ci.tailcall;
                        break;
                    case 'u':
                        if (f != null && f.isclosure()) {
                            Prototype p = f.checkclosure().p;
                            ar.nups = (short) p.upvalues.length;
                            ar.nparams = (short) p.numparams;
                            ar.isvararg = p.is_vararg != 0;
                        } else {
                            ar.nups = (short) 0;
                            ar.isvararg = true;
                            ar.nparams = (short) 0;
                        }
                        break;
                }
            }
            return ar;
        }
    }

    public static class CallFrame {
        LuaFunction f;
        boolean hookcall;
        int pc;
        CallFrame previous;
        private LuaValue[] stack;
        boolean tailcall;

        void set(LuaClosure function, Varargs varargs, LuaValue[] stack, boolean hookcall, boolean tailcall) {
            this.f = function;
            this.stack = stack;
            this.pc = 0;
            this.hookcall = hookcall;
            this.tailcall = tailcall;
        }

        public String shortsource() {
            return this.f.isclosure() ? this.f.checkclosure().p.shortsource() : "[Java]";
        }

        void set(LuaFunction function, boolean hookcall, boolean tailcall) {
            this.f = function;
            this.stack = null;
            this.pc = 0;
            this.hookcall = hookcall;
            this.tailcall = tailcall;
        }

        void reset() {
            this.f = null;
            this.stack = null;
            this.pc = 0;
            this.hookcall = false;
            this.tailcall = false;
        }

        void instr(int pc, Varargs v, int top) {
            this.pc = pc;
            if (DebugLib.TRACE) {
                Print.printState(this.f.checkclosure(), pc, this.stack, top, v);
            }
        }

        Varargs getLocal(int i) {
            if (i < 0) {
                if (!this.f.isclosure()) {
                    return DebugLib.NIL;
                }
                LuaClosure cl = this.f.checkclosure();
                int i2 = -i;
                Varargs v = cl.getVarargs();
                return (v == null || i2 > v.narg()) ? DebugLib.NIL : DebugLib.varargsOf(DebugLib.VARARG, v.arg(i2));
            }
            LuaString name = getlocalname(i);
            if (this.stack != null && i >= 1 && i <= this.stack.length && this.stack[i - 1] != null) {
                if (name == null) {
                    name = DebugLib.TEMPORARY;
                }
                return DebugLib.varargsOf(name, this.stack[i - 1]);
            }
            return DebugLib.NIL;
        }

        Varargs setLocal(int i, LuaValue value) {
            if (i < 0) {
                if (!this.f.isclosure()) {
                    return DebugLib.NIL;
                }
                LuaClosure cl = this.f.checkclosure();
                int i2 = -i;
                Varargs v = cl.getVarargs();
                if (v == null || i2 > v.narg()) {
                    return DebugLib.NIL;
                }
                int narg = v.narg();
                LuaValue[] arr = new LuaValue[narg];
                int j = 1;
                while (j <= narg) {
                    arr[j - 1] = i2 == j ? value : v.arg(j);
                    j++;
                }
                cl.setVarargs(DebugLib.varargsOf(arr));
                return DebugLib.VARARG;
            }
            LuaString name = getlocalname(i);
            if (this.stack != null && i >= 1 && i <= this.stack.length && this.stack[i - 1] != null) {
                this.stack[i - 1] = value;
                return name == null ? DebugLib.TEMPORARY : name;
            }
            return DebugLib.NIL;
        }

        public int currentline() {
            int[] li;
            if (this.f.isclosure() && (li = this.f.checkclosure().p.lineinfo) != null && this.pc >= 0 && this.pc < li.length) {
                return li[this.pc];
            }
            return -1;
        }

        String sourceline() {
            return !this.f.isclosure() ? this.f.tojstring() : String.valueOf(this.f.checkclosure().p.shortsource()) + ':' + currentline();
        }

        int linedefined() {
            if (this.f.isclosure()) {
                return this.f.checkclosure().p.linedefined;
            }
            return -1;
        }

        LuaString getlocalname(int index) {
            if (this.f.isclosure()) {
                return this.f.checkclosure().p.getlocalname(index, this.pc);
            }
            return null;
        }

        public String toString() {
            return "CallFrame [f=" + this.f + ", pc=" + this.pc + ", hookcall=" + this.hookcall + "]";
        }
    }

    private static LuaString defupvalue(Upvaldesc uv, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("(.upval ");
        if (uv != null) {
            sb.append(uv.instack ? 'v' : 'u');
            sb.append((int) uv.idx);
        } else {
            sb.append("???");
        }
        sb.append(" \"(*no name)\"; u");
        sb.append(i);
        sb.append(')');
        return LuaString.valueOf(sb.toString());
    }

    static LuaString findupvalue(LuaClosure c, int up) {
        if (c.upValues == null || up <= 0 || up > c.upValues.length) {
            return null;
        }
        if (c.p.upvalues != null && up <= c.p.upvalues.length) {
            LuaString ret = c.p.upvalues[up - 1].name;
            if (ret == null) {
                return defupvalue(c.p.upvalues[up - 1], up);
            }
            return ret;
        }
        return defupvalue(null, up);
    }

    static void lua_assert(boolean x) {
        if (!x) {
            throw new RuntimeException("lua_assert failed");
        }
    }

    static class NameWhat {
        final String name;
        final String namewhat;

        NameWhat(String name, String namewhat) {
            this.name = name;
            this.namewhat = namewhat;
        }

        public String toString() {
            return String.valueOf(this.namewhat) + " '" + this.name + "'";
        }
    }

    static NameWhat getfuncname(CallFrame frame) {
        LuaString tm;
        if (!frame.f.isclosure()) {
            return new NameWhat(frame.f.classnamestub(), "Java");
        }
        Prototype p = frame.f.checkclosure().p;
        int pc = frame.pc;
        if (pc >= p.code.length) {
            return null;
        }
        int i = p.code[pc];
        switch (Lua.GET_OPCODE(i)) {
            case 6:
            case 7:
            case 12:
                tm = LuaValue.INDEX;
                break;
            case 8:
            case 10:
                tm = LuaValue.NEWINDEX;
                break;
            case 9:
            case 11:
            case 20:
            case 23:
            case 27:
            case 28:
            case 31:
            case 32:
            case 33:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            default:
                return null;
            case 13:
                tm = LuaValue.ADD;
                break;
            case 14:
                tm = LuaValue.SUB;
                break;
            case 15:
                tm = LuaValue.MUL;
                break;
            case 16:
                tm = LuaValue.DIV;
                break;
            case 17:
                tm = LuaValue.MOD;
                break;
            case 18:
                tm = LuaValue.POW;
                break;
            case 19:
                tm = LuaValue.UNM;
                break;
            case 21:
                tm = LuaValue.LEN;
                break;
            case 22:
                tm = LuaValue.CONCAT;
                break;
            case 24:
                tm = LuaValue.EQ;
                break;
            case 25:
                tm = LuaValue.LT;
                break;
            case 26:
                tm = LuaValue.LE;
                break;
            case 29:
            case 30:
                return getobjname(p, pc, Lua.GETARG_A(i));
            case 34:
                return new NameWhat("for iterator", "for iterator");
            case 40:
                tm = LuaValue.IDIV;
                break;
            case 41:
                tm = LuaValue.BNOT;
                break;
            case 42:
                tm = LuaValue.BAND;
                break;
            case 43:
                tm = LuaValue.BOR;
                break;
            case 44:
                tm = LuaValue.BXOR;
                break;
            case 45:
                tm = LuaValue.SHL;
                break;
            case 46:
                tm = LuaValue.SHR;
                break;
        }
        return new NameWhat(tm.tojstring(), "metamethod");
    }

    public static String getobjname(int lastpc, int reg, Prototype p) {
        NameWhat name = getobjname(p, lastpc, reg);
        if (name == null) {
            return null;
        }
        return name.toString();
    }

    static NameWhat getobjname(Prototype p, int lastpc, int reg) {
        LuaString vn;
        LuaString name = p.getlocalname(reg + 1, lastpc);
        if (name != null) {
            return new NameWhat(name.tojstring(), "local");
        }
        int pc = findsetreg(p, lastpc, reg);
        if (pc == -1) {
            return null;
        }
        int i = p.code[pc];
        switch (Lua.GET_OPCODE(i)) {
            case 0:
                int a = Lua.GETARG_A(i);
                int b = Lua.GETARG_B(i);
                if (b < a) {
                    return getobjname(p, pc, b);
                }
                return null;
            case 1:
            case 2:
                int b2 = Lua.GET_OPCODE(i) == 1 ? Lua.GETARG_Bx(i) : Lua.GETARG_Ax(p.code[pc + 1]);
                if (p.k[b2].isstring()) {
                    return new NameWhat(p.k[b2].strvalue().tojstring(), "constant");
                }
                return null;
            case 3:
            case 4:
            case 8:
            case 9:
            case 10:
            case 11:
            default:
                return null;
            case 5:
                int u = Lua.GETARG_B(i);
                LuaString name2 = u < p.upvalues.length ? p.upvalues[u].name : QMARK;
                if (name2 != null) {
                    return new NameWhat(name2.tojstring(), "upvalue");
                }
                return null;
            case 6:
            case 7:
                int k = Lua.GETARG_C(i);
                int t = Lua.GETARG_B(i);
                if (Lua.GET_OPCODE(i) == 7) {
                    vn = p.getlocalname(t + 1, pc);
                } else {
                    vn = t < p.upvalues.length ? p.upvalues[t].name : QMARK;
                }
                String jname = kname(p, pc, k);
                return new NameWhat(jname, (vn == null || !vn.eq_b(ENV)) ? "field" : "global");
            case 12:
                int k2 = Lua.GETARG_C(i);
                String jname2 = kname(p, pc, k2);
                return new NameWhat(jname2, "method");
        }
    }

    static String kname(Prototype p, int pc, int c) {
        if (Lua.ISK(c)) {
            LuaValue k = p.k[Lua.INDEXK(c)];
            if (k.isstring()) {
                return k.tojstring();
            }
        } else {
            NameWhat what = getobjname(p, pc, c);
            if (what != null && "constant".equals(what.namewhat)) {
                return what.name;
            }
        }
        return Searcher.MASK_PLACEHOLDER;
    }

    static int findsetreg(Prototype p, int lastpc, int reg) {
        int setreg = -1;
        int pc = 0;
        while (pc < lastpc) {
            int i = p.code[pc];
            int op = Lua.GET_OPCODE(i);
            if (op < 47) {
                int a = Lua.GETARG_A(i);
                switch (op) {
                    case 4:
                        int b = Lua.GETARG_B(i);
                        if (a <= reg && reg <= a + b) {
                            setreg = pc;
                        }
                        break;
                    case 23:
                        int b2 = Lua.GETARG_sBx(i);
                        int dest = pc + 1 + b2;
                        if (pc < dest && dest <= lastpc) {
                            pc += b2;
                        }
                        break;
                    case 27:
                        if (reg == a) {
                            setreg = pc;
                        }
                        break;
                    case 29:
                    case 30:
                        if (reg >= a) {
                            setreg = pc;
                        }
                        break;
                    case 34:
                        if (reg >= a + 2) {
                            setreg = pc;
                        }
                        break;
                    case 36:
                        if (((i >> 14) & 511) == 0) {
                            pc++;
                        }
                        break;
                    default:
                        if (Lua.testAMode(op) && reg == a) {
                            setreg = pc;
                        }
                        break;
                }
            }
            pc++;
        }
        return setreg;
    }

    public static void main(String[] args) throws Throwable {
    }
}
