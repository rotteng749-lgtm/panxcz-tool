package luaj;

import android.ext.Script;
import android.ext.Searcher;
import luaj.LuaError;
import luaj.lib.DebugLib;

/* JADX INFO: loaded from: classes.dex */
public class LuaClosure extends LuaFunction {
    private static final UpValue[] NOUPVALUES = new UpValue[0];
    private static int deep = 0;
    final Globals globals;
    public final Prototype p;
    public volatile int pc;
    public UpValue[] upValues;
    public String sourceFile = null;
    private Varargs varargs = NONE;

    public LuaClosure(Prototype p, LuaValue env, boolean fillUpValues) {
        this.p = p;
        if (p.upvalues == null || p.upvalues.length == 0) {
            this.upValues = NOUPVALUES;
        } else {
            int len = p.upvalues.length;
            UpValue[] upValues = new UpValue[len];
            if (fillUpValues) {
                LuaValue[] stack = new LuaValue[len];
                stack[0] = env;
                upValues[0] = new UpValue(stack, 0);
                for (int i = 1; i < len; i++) {
                    stack[i] = NIL;
                    upValues[i] = new UpValue(stack, i);
                }
            }
            this.upValues = upValues;
        }
        this.globals = env instanceof Globals ? (Globals) env : null;
    }

    @Override // luaj.LuaValue
    public boolean isclosure() {
        return true;
    }

    @Override // luaj.LuaValue
    public LuaClosure optclosure(LuaClosure defval) {
        return this;
    }

    @Override // luaj.LuaValue
    public LuaClosure checkclosure() {
        return this;
    }

    @Override // luaj.LuaFunction, luaj.LuaValue, luaj.Varargs
    public String tojstring() {
        return "function: " + this.p.toString();
    }

    private LuaValue[] getNewStack() {
        int max = this.p.maxstacksize;
        LuaValue[] stack = new LuaValue[max];
        System.arraycopy(NILS, 0, stack, 0, max);
        return stack;
    }

    @Override // luaj.LuaValue
    public final LuaValue call() {
        LuaValue[] stack = getNewStack();
        return execute(stack, NONE, false).arg1();
    }

    @Override // luaj.LuaValue
    public final LuaValue call(LuaValue arg) {
        LuaValue[] stack = getNewStack();
        switch (this.p.numparams) {
            case 0:
                if (this.p.is_vararg == 0) {
                    arg = NONE;
                }
                return execute(stack, arg, false).arg1();
            default:
                stack[0] = arg;
                return execute(stack, NONE, false).arg1();
        }
    }

    @Override // luaj.LuaValue
    public final LuaValue call(LuaValue arg1, LuaValue arg2) {
        LuaValue[] stack = getNewStack();
        switch (this.p.numparams) {
            case 0:
                return execute(stack, this.p.is_vararg == 0 ? NONE : varargsOf(arg1, arg2), false).arg1();
            case 1:
                stack[0] = arg1;
                if (this.p.is_vararg == 0) {
                    arg2 = NONE;
                }
                return execute(stack, arg2, false).arg1();
            default:
                stack[0] = arg1;
                stack[1] = arg2;
                return execute(stack, NONE, false).arg1();
        }
    }

    @Override // luaj.LuaValue
    public final LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        LuaValue[] stack = getNewStack();
        switch (this.p.numparams) {
            case 0:
                return execute(stack, this.p.is_vararg == 0 ? NONE : varargsOf(arg1, arg2, arg3), false).arg1();
            case 1:
                stack[0] = arg1;
                return execute(stack, this.p.is_vararg == 0 ? NONE : varargsOf(arg2, arg3), false).arg1();
            case 2:
                stack[0] = arg1;
                stack[1] = arg2;
                if (this.p.is_vararg == 0) {
                    arg3 = NONE;
                }
                return execute(stack, arg3, false).arg1();
            default:
                stack[0] = arg1;
                stack[1] = arg2;
                stack[2] = arg3;
                return execute(stack, NONE, false).arg1();
        }
    }

    @Override // luaj.LuaValue
    public final Varargs invoke(Varargs varargs) {
        return onInvoke(varargs, false).eval();
    }

    @Override // luaj.LuaValue
    public final Varargs onInvoke(Varargs varargs, boolean tailcall) {
        LuaValue[] stack = getNewStack();
        if (this.p.maxstacksize < this.p.numparams) {
            throw new LuaError("Damaged script: .maxstacksize (" + this.p.maxstacksize + ") < .numparams (" + this.p.numparams + ")");
        }
        for (int i = 0; i < this.p.numparams; i++) {
            stack[i] = varargs.arg(i + 1);
        }
        return execute(stack, this.p.is_vararg == 0 ? NONE : varargs.subargs(this.p.numparams + 1), tailcall);
    }

    private static LuaError upValueOutOfRange(int type, int index, int len) {
        return new LuaError("Damaged script " + type + ": an attempt to get an upvalue with index " + index + " when there are " + len + " in total.");
    }

    public Varargs getVarargs() {
        return this.varargs;
    }

    public void setVarargs(Varargs varargs) {
        this.varargs = varargs;
    }

    /* JADX WARN: Removed duplicated region for block: B:364:0x098e A[Catch: LuaError -> 0x00a6, all -> 0x017c, StackOverflowError -> 0x01d5, InterruptedException -> 0x01fb, Throwable -> 0x0253, NullPointerException -> 0x0993, TRY_ENTER, TRY_LEAVE, TryCatch #7 {NullPointerException -> 0x0993, blocks: (B:346:0x0923, B:350:0x092f, B:352:0x0937, B:364:0x098e, B:382:0x0a35, B:383:0x0a38), top: B:605:0x0923 }] */
    /* JADX WARN: Removed duplicated region for block: B:374:0x09f1  */
    /* JADX WARN: Removed duplicated region for block: B:662:0x01f0 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected luaj.Varargs execute(luaj.LuaValue[] r71, luaj.Varargs r72, boolean r73) {
        LuaValue[] stack = r71;
        Varargs varargs = r72;
        boolean tailcall = r73;
        int i;
        int a;
        int b;
        int c;
        int pc = 0;
        int top = 0;
        LuaValue o;
        Varargs v = NONE;
        int[] code = this.p.code;
        LuaValue[] k = this.p.k;
        UpValue[] openups = this.p.p.length > 0 ? new UpValue[stack.length] : null;
        Globals globals = this.globals;
        if (globals != null && globals.debuglib != null) {
            globals.debuglib.onCall(this, varargs, stack, tailcall);
        }
        try {
            while (true) {
                this.pc = pc;
                if (globals != null && globals.debuglib != null) {
                    globals.debuglib.onInstruction(pc, v, top);
                }
                i = code[pc];
                a = (i >> 6) & 255;
                switch (i & 63) {
                    case Lua.OP_MOVE:
                        stack[a] = stack[i >>> 23];
                        pc++;
                        break;
                    case Lua.OP_LOADK:
                        stack[a] = k[i >>> 14];
                        pc++;
                        break;
                    case Lua.OP_LOADKX:
                        pc++;
                        i = code[pc];
                        if ((i & 63) != Lua.OP_EXTRAARG) {
                            int op = i & 63;
                            throw new LuaError("OP_EXTRAARG expected after OP_LOADKX, got " + (op < Print.OPNAMES.length - 1 ? Print.OPNAMES[op] : "UNKNOWN_OP_" + op));
                        }
                        stack[a] = k[i >>> 6];
                        pc++;
                        break;
                    case Lua.OP_LOADBOOL:
                        stack[a] = (i >>> 23) != 0 ? TRUE : FALSE;
                        if ((i & (511 << 14)) != 0) {
                            pc++;
                        }
                        pc++;
                        break;
                    case Lua.OP_LOADNIL:
                        for (b = i >>> 23; b-- >= 0; ) {
                            stack[a++] = NIL;
                        }
                        pc++;
                        break;
                    case Lua.OP_GETUPVAL:
                        stack[a] = this.upValues[i >>> 23].getValue();
                        pc++;
                        break;
                    case Lua.OP_GETTABUP:
                        c = (i >> 14) & 511;
                        stack[a] = this.upValues[i >>> 23].getValue().get(c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_GETTABLE:
                        c = (i >> 14) & 511;
                        stack[a] = stack[i >>> 23].get(c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_SETTABUP:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        this.upValues[a].getValue().set(b > 255 ? k[b & 255] : stack[b], c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_SETUPVAL:
                        this.upValues[i >>> 23].setValue(stack[a]);
                        pc++;
                        break;
                    case Lua.OP_SETTABLE:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        stack[a].set(b > 255 ? k[b & 255] : stack[b], c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_NEWTABLE:
                        stack[a] = new LuaTable(i >>> 23, (i >> 14) & 511);
                        pc++;
                        break;
                    case Lua.OP_SELF:
                        o = stack[i >>> 23];
                        stack[a + 1] = o;
                        c = (i >> 14) & 511;
                        stack[a] = o.get(c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_ADD:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        stack[a] = (b > 255 ? k[b & 255] : stack[b]).add(c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_SUB:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        stack[a] = (b > 255 ? k[b & 255] : stack[b]).sub(c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_MUL:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        stack[a] = (b > 255 ? k[b & 255] : stack[b]).mul(c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_DIV:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        stack[a] = (b > 255 ? k[b & 255] : stack[b]).div(c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_IDIV:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        stack[a] = (b > 255 ? k[b & 255] : stack[b]).idiv(c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_BAND:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        stack[a] = (b > 255 ? k[b & 255] : stack[b]).band(c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_BOR:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        stack[a] = (b > 255 ? k[b & 255] : stack[b]).bor(c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_BXOR:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        stack[a] = (b > 255 ? k[b & 255] : stack[b]).bxor(c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_SHL:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        stack[a] = (b > 255 ? k[b & 255] : stack[b]).shl(c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_SHR:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        stack[a] = (b > 255 ? k[b & 255] : stack[b]).shr(c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_MOD:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        stack[a] = (b > 255 ? k[b & 255] : stack[b]).mod(c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_POW:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        stack[a] = (b > 255 ? k[b & 255] : stack[b]).pow(c > 255 ? k[c & 255] : stack[c]);
                        pc++;
                        break;
                    case Lua.OP_UNM:
                        stack[a] = stack[i >>> 23].neg();
                        pc++;
                        break;
                    case Lua.OP_BNOT:
                        stack[a] = stack[i >>> 23].bnot();
                        pc++;
                        break;
                    case Lua.OP_NOT:
                        stack[a] = stack[i >>> 23].not();
                        pc++;
                        break;
                    case Lua.OP_LEN:
                        stack[a] = stack[i >>> 23].len();
                        pc++;
                        break;
                    case Lua.OP_CONCAT:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        if (c > b + 1) {
                            Buffer sb = stack[c].buffer();
                            while (--c >= b) {
                                sb.concatTo(stack[c]);
                            }
                            stack[a] = sb.value();
                        } else {
                            stack[a] = stack[c - 1].concat(stack[c]);
                        }
                        pc++;
                        break;
                    case Lua.OP_JMP:
                        pc += (i >>> 14) - 131071;
                        if (a > 0 && openups != null) {
                            a--;
                            for (b = openups.length; --b >= 0; ) {
                                if (openups[b] != null && openups[b].index >= a) {
                                    openups[b].close();
                                    openups[b] = null;
                                }
                            }
                        }
                        pc++;
                        break;
                    case Lua.OP_EQ:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        if (((b > 255 ? k[b & 255] : stack[b]).eq_b(c > 255 ? k[c & 255] : stack[c])) != (a != 0)) {
                            pc++;
                        }
                        pc++;
                        break;
                    case Lua.OP_LT:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        if (((b > 255 ? k[b & 255] : stack[b]).lt_b(c > 255 ? k[c & 255] : stack[c])) != (a != 0)) {
                            pc++;
                        }
                        pc++;
                        break;
                    case Lua.OP_LE:
                        b = i >>> 23;
                        c = (i >> 14) & 511;
                        if (((b > 255 ? k[b & 255] : stack[b]).lteq_b(c > 255 ? k[c & 255] : stack[c])) != (a != 0)) {
                            pc++;
                        }
                        pc++;
                        break;
                    case Lua.OP_TEST:
                        if (stack[a].toboolean() != ((i & (511 << 14)) != 0)) {
                            pc++;
                        }
                        pc++;
                        break;
                    case Lua.OP_TESTSET:
                        o = stack[i >>> 23];
                        if (o.toboolean() != ((i & (511 << 14)) != 0)) {
                            pc++;
                        } else {
                            stack[a] = o;
                        }
                        pc++;
                        break;
                    case Lua.OP_CALL:
                        setCaller(stack[a]);
                        switch (i & (Lua.MASK_B | Lua.MASK_C)) {
                            case (1 << Lua.POS_B) | (0 << Lua.POS_C):
                                v = checkRet(stack[a].invoke(NONE));
                                top = a + v.narg();
                                pc++;
                                break;
                            case (2 << Lua.POS_B) | (0 << Lua.POS_C):
                                v = checkRet(stack[a].invoke(stack[a + 1]));
                                top = a + v.narg();
                                pc++;
                                break;
                            case (1 << Lua.POS_B) | (1 << Lua.POS_C):
                                checkRet(stack[a].call());
                                pc++;
                                break;
                            case (2 << Lua.POS_B) | (1 << Lua.POS_C):
                                checkRet(stack[a].call(stack[a + 1]));
                                pc++;
                                break;
                            case (3 << Lua.POS_B) | (1 << Lua.POS_C):
                                checkRet(stack[a].call(stack[a + 1], stack[a + 2]));
                                pc++;
                                break;
                            case (4 << Lua.POS_B) | (1 << Lua.POS_C):
                                checkRet(stack[a].call(stack[a + 1], stack[a + 2], stack[a + 3]));
                                pc++;
                                break;
                            case (1 << Lua.POS_B) | (2 << Lua.POS_C):
                                stack[a] = checkRet(stack[a].call());
                                pc++;
                                break;
                            case (2 << Lua.POS_B) | (2 << Lua.POS_C):
                                stack[a] = checkRet(stack[a].call(stack[a + 1]));
                                pc++;
                                break;
                            case (3 << Lua.POS_B) | (2 << Lua.POS_C):
                                stack[a] = checkRet(stack[a].call(stack[a + 1], stack[a + 2]));
                                pc++;
                                break;
                            case (4 << Lua.POS_B) | (2 << Lua.POS_C):
                                stack[a] = checkRet(stack[a].call(stack[a + 1], stack[a + 2], stack[a + 3]));
                                pc++;
                                break;
                            default:
                                b = i >>> 23;
                                c = (i >> 14) & 511;
                                v = checkRet(stack[a].invoke(b > 0 ? varargsOf(stack, a + 1, b - 1) : varargsOf(stack, a + 1, (top - v.narg()) - (a + 1), v)));
                                if (c > 0) {
                                    v.copyto(stack, a, c - 1);
                                    v = NONE;
                                } else {
                                    top = a + v.narg();
                                    v = v.dealias();
                                }
                                pc++;
                                break;
                        }
                        break;
                    case Lua.OP_TAILCALL:
                        setCaller(stack[a]);
                        switch (i & Lua.MASK_B) {
                            case (1 << Lua.POS_B):
                                return new TailcallVarargs(globals, stack[a], NONE);
                            case (2 << Lua.POS_B):
                                return new TailcallVarargs(globals, stack[a], stack[a + 1]);
                            case (3 << Lua.POS_B):
                                return new TailcallVarargs(globals, stack[a], varargsOf(stack[a + 1], stack[a + 2]));
                            case (4 << Lua.POS_B):
                                return new TailcallVarargs(globals, stack[a], varargsOf(stack[a + 1], stack[a + 2], stack[a + 3]));
                            default:
                                b = i >>> 23;
                                v = b > 0 ? varargsOf(stack, a + 1, b - 1) : varargsOf(stack, a + 1, (top - v.narg()) - (a + 1), v);
                                return new TailcallVarargs(globals, stack[a], v);
                        }
                    case Lua.OP_RETURN:
                        b = i >>> 23;
                        switch (b) {
                            case 0:
                                return varargsOf(stack, a, (top - v.narg()) - a, v);
                            case 1:
                                return NONE;
                            case 2:
                                return stack[a];
                            default:
                                return varargsOf(stack, a, b - 1);
                        }
                    case Lua.OP_FORLOOP: {
                        LuaValue limit = stack[a + 1];
                        LuaValue step = stack[a + 2];
                        LuaValue idx = stack[a].add(step);
                        if (step.gt_b(ZERO) ? idx.lteq_b(limit) : idx.gteq_b(limit)) {
                            stack[a] = idx;
                            stack[a + 3] = idx;
                            pc += (i >>> 14) - 131071;
                        }
                        pc++;
                        break;
                    }
                    case Lua.OP_FORPREP: {
                        LuaValue init = stack[a].checknumber("'for' initial value must be a number");
                        LuaValue limit = stack[a + 1].checknumber("'for' limit must be a number");
                        LuaValue step = stack[a + 2].checknumber("'for' step must be a number");
                        stack[a] = init.sub(step);
                        stack[a + 1] = limit;
                        stack[a + 2] = step;
                        pc += (i >>> 14) - 131071;
                        pc++;
                        break;
                    }
                    case Lua.OP_TFORCALL:
                        v = checkRet(stack[a].invoke(varargsOf(stack[a + 1], stack[a + 2])));
                        c = (i >> 14) & 511;
                        while (--c >= 0) {
                            stack[a + 3 + c] = v.arg(c + 1);
                        }
                        v = NONE;
                        pc++;
                        break;
                    case Lua.OP_TFORLOOP:
                        if (!stack[a + 1].isnil()) {
                            stack[a] = stack[a + 1];
                            pc += (i >>> 14) - 131071;
                        }
                        pc++;
                        break;
                    case Lua.OP_SETLIST: {
                        c = (i >> 14) & 511;
                        if (c == 0) {
                            c = code[++pc];
                        }
                        int offset = (c - 1) * Lua.LFIELDS_PER_FLUSH;
                        o = stack[a];
                        b = i >>> 23;
                        if (b == 0) {
                            b = (top - a) - 1;
                            int m = b - v.narg();
                            int j = 1;
                            for (; j <= m; j++) {
                                o.set(offset + j, stack[a + j]);
                            }
                            for (; j <= b; j++) {
                                o.set(offset + j, v.arg(j - m));
                            }
                        } else {
                            o.presize(offset + b);
                            for (int j2 = 1; j2 <= b; j2++) {
                                o.set(offset + j2, stack[a + j2]);
                            }
                        }
                        pc++;
                        break;
                    }
                    case Lua.OP_CLOSURE: {
                        Prototype newp = this.p.p[i >>> 14];
                        LuaValue env = globals != null ? globals : NIL;
                        LuaClosure ncl = new LuaClosure(newp, env, false);
                        Upvaldesc[] uv = newp.upvalues;
                        for (int j = 0, nup = uv.length; j < nup; j++) {
                            if (uv[j].instack) {
                                ncl.upValues[j] = findupval(stack, uv[j].idx, openups);
                            } else {
                                ncl.upValues[j] = this.upValues[uv[j].idx];
                            }
                        }
                        stack[a] = ncl;
                        pc++;
                        break;
                    }
                    case Lua.OP_VARARG:
                        b = i >>> 23;
                        if (b == 0) {
                            top = a + (b = varargs.narg());
                            v = varargs;
                        } else {
                            for (int j = 1; j < b; j++) {
                                stack[(a + j) - 1] = varargs.arg(j);
                            }
                        }
                        pc++;
                        break;
                    case Lua.OP_EXTRAARG:
                        throw new IllegalArgumentException("Unexecutable opcode: OP_EXTRAARG");
                    default:
                        throw new IllegalArgumentException("Illegal opcode: " + (i & 63));
                }
            }
        } catch (LuaError le) {
            if (le.traceback == null) {
                processErrorHooks(globals, le, this.p, pc, null);
            }
            throw le;
        } catch (Throwable e) {
            LuaError le2 = e instanceof LuaError ? (LuaError) e : new LuaError(e);
            if (le2.traceback == null) {
                processErrorHooks(globals, le2, this.p, pc, null);
            }
            throw le2;
        } finally {
            if (openups != null) {
                for (int u = openups.length; --u >= 0; ) {
                    if (openups[u] != null) {
                        openups[u].close();
                    }
                }
            }
            if (globals != null && globals.debuglib != null) {
                globals.debuglib.onReturn();
            }
        }
    }

    private void setCaller(LuaValue val) {
        if (val instanceof Script.DebugFunction) {
            ((Script.DebugFunction) val).setCaller(this);
        }
    }

    private LuaValue checkRet(LuaValue ret) {
        if (ret == null) {
            throw new NullPointerException("return null as LuaValue");
        }
        return ret;
    }

    private Varargs checkRet(Varargs ret) {
        if (ret == null) {
            throw new NullPointerException("return null as Varargs");
        }
        return ret;
    }

    private static void errorHook(Globals globals, LuaError le, StringBuilder msg) {
        if (globals != null) {
            LuaThread r = globals.running;
            if (r.errorfunc != null) {
                le.traceback = msg.toString();
                try {
                    le.object = r.errorfunc.call(le.object != null ? le.object : LuaValue.valueOf(le.traceback));
                    return;
                } catch (Throwable th) {
                    le.object = LuaValue.valueOf("error in error handling");
                    return;
                }
            }
            if (globals.debuglib != null) {
                msg.append('\n');
                msg.append(globals.debuglib.traceback(globals, le.level));
            }
        }
        le.traceback = msg.toString();
    }

    private static void processErrorHooks(Globals globals, LuaError le, Prototype p, int pc, String name) {
        if (le.traceback == null) {
            if (p != null) {
                DebugLib.CallFrame frame = null;
                if (globals != null && globals.debuglib != null && (frame = globals.debuglib.getCallFrame(le.level)) != null) {
                    String src = frame.shortsource();
                    if (src == null) {
                        src = Searcher.MASK_PLACEHOLDER;
                    }
                    le.file = src;
                    le.line = frame.currentline();
                }
                if (frame == null) {
                    le.file = p.source != null ? p.source.tojstring() : Searcher.MASK_PLACEHOLDER;
                    le.line = (p.lineinfo == null || pc < 0 || pc >= p.lineinfo.length) ? -1 : p.lineinfo[pc];
                }
                le.setFileLine();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(le.getMessage());
            if (name != null) {
                sb.append(" (");
                sb.append(name);
                sb.append(")");
            }
            sb.append("\nlevel = ");
            sb.append(le.level);
            if (p != null) {
                sb.append(", const = ");
                sb.append(p.k.length);
                sb.append(", proto = ");
                sb.append(p.p.length);
                sb.append(", upval = ");
                sb.append(p.upvalues.length);
                sb.append(", vars = ");
                sb.append(p.maxstacksize);
                sb.append(", code = ");
                sb.append(p.code.length);
                sb.append('\n');
                if (pc >= 0 && pc < p.code.length) {
                    Print.printOpCode(sb, p, pc, null);
                    sb.append('\n');
                    Print.describe(sb, pc, p.code[pc]);
                } else {
                    sb.append("PC ");
                    sb.append(pc);
                }
            }
            errorHook(globals, le, sb);
        }
    }

    public static Varargs protectedCall(Globals globals, LuaFunction caller, LuaValue func, Varargs args) {
        Varargs varargsVarargsOf;
        if (globals != null && globals.debuglib != null) {
            globals.debuglib.onCall(caller, false);
        }
        int old = deep;
        deep = 0;
        try {
            try {
                varargsVarargsOf = varargsOf(TRUE, func.invoke(args));
            } catch (Throwable e) {
                LuaError le = e instanceof LuaError ? (LuaError) e : new LuaError.Internal(e);
                if (le.traceback == null) {
                    processErrorHooks(globals, le, null, 0, null);
                }
                LuaValue m = le.getMessageObject();
                LuaBoolean luaBoolean = FALSE;
                if (m == null) {
                    m = NIL;
                }
                varargsVarargsOf = varargsOf(luaBoolean, m);
                deep = old;
                if (globals != null && globals.debuglib != null) {
                    globals.debuglib.onReturn();
                }
            }
            return varargsVarargsOf;
        } finally {
            deep = old;
            if (globals != null && globals.debuglib != null) {
                globals.debuglib.onReturn();
            }
        }
    }

    private UpValue findupval(LuaValue[] stack, short idx, UpValue[] openups) {
        int n = openups.length;
        int free = -1;
        for (int i = 0; i < n; i++) {
            UpValue up = openups[i];
            if (up == null) {
                if (free < 0) {
                    free = i;
                }
            } else if (up.index == idx) {
                return up;
            }
        }
        if (free < 0) {
            error("No space for upvalue " + n);
            return null;
        }
        UpValue upValue = new UpValue(stack, idx);
        openups[free] = upValue;
        return upValue;
    }

    @Override // luaj.LuaFunction
    public String name() {
        return String.valueOf('<') + this.p.shortsource() + ':' + this.p.linedefined + '>';
    }

    public static void main(String[] args) throws Throwable {
    }
}
