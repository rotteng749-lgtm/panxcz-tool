package luaj;

import android.ext.AddressItem;
import android.ext.Message;
import android.ext.SearchMenuItem;
import luaj.Varargs;
import luaj.compiler.LexState;

/* JADX INFO: loaded from: classes.dex */
public abstract class LuaValue extends Varargs {
    private static final int MAXSTACK = 250;
    private static final int MAXTAGLOOP = 100;
    public static final int TBOOLEAN = 1;
    public static final int TFUNCTION = 6;
    public static final int TINT = -2;
    public static final int TNIL = 0;
    public static final int TNONE = -1;
    public static final int TNUMBER = 3;
    public static final int TSTRING = 4;
    public static final int TTABLE = 5;
    public static final int TTHREAD = 8;
    public static final int TUSERDATA = 7;
    public static final int TVALUE = 9;
    public static final String[] TYPE_NAMES = {"nil", "boolean", "lightuserdata", "number", "string", "table", "function", "userdata", "thread", "value"};
    public static final LuaValue NIL = new LuaNil();
    public static final LuaBoolean TRUE = new LuaBoolean(true);
    public static final LuaBoolean FALSE = new LuaBoolean(false);
    public static final LuaValue NONE = new None(null);
    public static final LuaNumber ZERO = LuaLong.valueOf(0L);
    public static final LuaNumber ONE = LuaLong.valueOf(1L);
    public static final LuaNumber MINUSONE = LuaLong.valueOf(-1L);
    public static final LuaValue[] NOVALS = new LuaValue[0];
    public static final LuaString ENV = LuaString.rawValueOf("_ENV");
    public static final LuaString INDEX = LuaString.rawValueOf("__index");
    public static final LuaString NEWINDEX = LuaString.rawValueOf("__newindex");
    public static final LuaString CALL = LuaString.rawValueOf("__call");
    public static final LuaString MODE = LuaString.rawValueOf("__mode");
    public static final LuaString METATABLE = LuaString.rawValueOf("__metatable");
    public static final LuaString ADD = LuaString.rawValueOf("__add");
    public static final LuaString SUB = LuaString.rawValueOf("__sub");
    public static final LuaString DIV = LuaString.rawValueOf("__div");
    public static final LuaString MUL = LuaString.rawValueOf("__mul");
    public static final LuaString POW = LuaString.rawValueOf("__pow");
    public static final LuaString MOD = LuaString.rawValueOf("__mod");
    public static final LuaString UNM = LuaString.rawValueOf("__unm");
    public static final LuaString LEN = LuaString.rawValueOf("__len");
    public static final LuaString EQ = LuaString.rawValueOf("__eq");
    public static final LuaString LT = LuaString.rawValueOf("__lt");
    public static final LuaString LE = LuaString.rawValueOf("__le");
    public static final LuaString TOSTRING = LuaString.rawValueOf("__tostring");
    public static final LuaString CONCAT = LuaString.rawValueOf("__concat");
    public static final LuaString IDIV = LuaString.rawValueOf("__idiv");
    public static final LuaString BNOT = LuaString.rawValueOf("__bnot");
    public static final LuaString BAND = LuaString.rawValueOf("__band");
    public static final LuaString BOR = LuaString.rawValueOf("__bor");
    public static final LuaString BXOR = LuaString.rawValueOf("__bxor");
    public static final LuaString SHL = LuaString.rawValueOf("__shl");
    public static final LuaString SHR = LuaString.rawValueOf("__shr");
    public static final LuaString NAME = LuaString.rawValueOf("__name");
    public static final LuaString EMPTYSTRING = LuaString.rawValueOf("");
    public static final LuaString ZEROSTRING = LuaString.rawValueOf("0");
    public static final LuaString ONESTRING = LuaString.rawValueOf("1");
    public static final LuaValue[] NILS = new LuaValue[250];

    public abstract int type();

    public abstract String typename();

    static {
        for (int i = 0; i < 250; i++) {
            NILS[i] = NIL;
        }
    }

    public boolean isboolean() {
        return false;
    }

    public boolean isclosure() {
        return false;
    }

    public boolean isfunction() {
        return false;
    }

    public boolean isint() {
        return false;
    }

    public boolean isinttype() {
        return false;
    }

    public boolean isintnumber() {
        return false;
    }

    public boolean islongnumber() {
        return false;
    }

    public boolean islong() {
        return false;
    }

    public boolean isnil() {
        return false;
    }

    public boolean isnumber() {
        return false;
    }

    public boolean isstring() {
        return false;
    }

    public boolean isthread() {
        return false;
    }

    public boolean istable() {
        return false;
    }

    public boolean toboolean() {
        return true;
    }

    public byte tobyte() {
        return (byte) 0;
    }

    public char tochar() {
        return (char) 0;
    }

    public double todouble() {
        return 0.0d;
    }

    public float tofloat() {
        return 0.0f;
    }

    public int toint() {
        return 0;
    }

    public long tolong() {
        return 0L;
    }

    public short toshort() {
        return (short) 0;
    }

    @Override // luaj.Varargs
    public String tojstring() {
        return String.valueOf(typenamemt()) + ": " + Integer.toHexString(hashCode());
    }

    @Override // luaj.Varargs
    public String toString() {
        return tojstring();
    }

    public LuaValue tonumber() {
        return NIL;
    }

    public LuaValue tostring() {
        return NIL;
    }

    public boolean optboolean(boolean defval) {
        argerror("boolean");
        return false;
    }

    public LuaClosure optclosure(LuaClosure defval) {
        argerror("closure");
        return null;
    }

    public double optdouble(double defval) {
        argerror("number");
        return 0.0d;
    }

    public LuaFunction optfunction(LuaFunction defval) {
        argerror("function");
        return null;
    }

    public int optint(int defval) {
        argerror("int");
        return 0;
    }

    public LuaLong optinteger(LuaLong defval) {
        argerror("integer");
        return null;
    }

    public long optlong(long defval) {
        argerror("long");
        return 0L;
    }

    public LuaNumber optnumber(LuaNumber defval) {
        argerror("number");
        return null;
    }

    public String optjstring(String defval) {
        argerror("String");
        return null;
    }

    public LuaString optstring(LuaString defval) {
        argerror("string");
        return null;
    }

    public LuaTable opttable(LuaTable defval) {
        argerror("table");
        return null;
    }

    public LuaThread optthread(LuaThread defval) {
        argerror("thread");
        return null;
    }

    public LuaValue optvalue(LuaValue defval) {
        return this;
    }

    public boolean checkboolean() {
        argerror("boolean");
        return false;
    }

    public LuaClosure checkclosure() {
        argerror("closure");
        return null;
    }

    public double checkdouble() {
        argerror("number");
        return 0.0d;
    }

    public LuaFunction checkfunction() {
        argerror("function");
        return null;
    }

    public Globals checkglobals() {
        argerror("globals");
        return null;
    }

    public int checkint() {
        argerror("int");
        return 0;
    }

    public LuaLong checkinteger() {
        argerror("integer");
        return null;
    }

    public long checklong() {
        argerror("long");
        return 0L;
    }

    public LuaNumber checknumber() {
        argerror("number");
        return null;
    }

    public LuaNumber checknumber(String msg) {
        throw new LuaError(msg);
    }

    public String checkjstring() {
        argerror("string");
        return null;
    }

    public LuaString checkstring() {
        argerror("string");
        return null;
    }

    public LuaTable checktable() {
        argerror("table");
        return null;
    }

    public LuaThread checkthread() {
        argerror("thread");
        return null;
    }

    public LuaValue checknotnil() {
        return this;
    }

    public boolean isvalidkey() {
        return true;
    }

    public static LuaValue error(String message) {
        throw new LuaError(message);
    }

    public static LuaValue error(String message, Throwable cause) {
        throw new LuaError(message, cause);
    }

    public static void assert_(boolean b, String msg) {
        if (!b) {
            throw new LuaError(msg);
        }
    }

    protected LuaValue argerror(String expected) {
        throw new LuaError("bad argument: " + expected + " expected, got " + typenamemt());
    }

    public static LuaValue argerror(int iarg, String msg) {
        throw new LuaArgError(iarg, null, msg);
    }

    public static LuaValue argerror(int iarg, String funcname, String msg) {
        throw new LuaArgError(iarg, funcname, msg);
    }

    protected LuaValue typerror(String expected) {
        throw new LuaError(String.valueOf(expected) + " expected, got " + typenamemt());
    }

    protected LuaValue unimplemented(String fun) {
        throw new LuaError(String.valueOf(SearchMenuItem.HEX_UTF_8) + fun + "' not implemented for " + typename());
    }

    protected LuaValue illegal(String op, String typename) {
        throw new LuaError("illegal operation '" + op + "' for " + typename);
    }

    protected LuaValue lenerror() {
        throw new LuaError("attempt to get length of a " + typename() + " value");
    }

    protected LuaValue aritherror() {
        throw new LuaError("attempt to perform arithmetic on " + typename());
    }

    protected LuaValue aritherror(String fun) {
        throw new LuaError("attempt to perform arithmetic '" + fun + "' on " + typename());
    }

    protected LuaValue compareerror(String rhs) {
        throw new LuaError("attempt to compare " + typename() + " with " + rhs);
    }

    protected LuaValue compareerror(LuaValue rhs) {
        throw new LuaError("attempt to compare " + typename() + " with " + rhs.typename());
    }

    public LuaValue get(LuaValue key) {
        return gettable(this, key);
    }

    public LuaValue get(int key) {
        return get(LuaLong.valueOf(key));
    }

    public LuaValue get(String key) {
        return get(valueOf(key));
    }

    public void set(LuaValue key, LuaValue value) {
        if (key == null || value == null) {
            return;
        }
        settable(this, key, value);
    }

    public void set(int key, LuaValue value) {
        if (value != null) {
            set(LuaLong.valueOf(key), value);
        }
    }

    public void set(int key, String value) {
        if (value != null) {
            set(key, valueOf(value));
        }
    }

    public void set(String key, LuaValue value) {
        if (key == null || value == null) {
            return;
        }
        set(valueOf(key), value);
    }

    public void set(String key, double value) {
        if (key != null) {
            set(valueOf(key), valueOf(value));
        }
    }

    public void set(String key, int value) {
        if (key != null) {
            set(valueOf(key), valueOf(value));
        }
    }

    public void set(String key, long value) {
        if (key != null) {
            set(valueOf(key), valueOf(value));
        }
    }

    public void set(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        set(valueOf(key), valueOf(value));
    }

    public LuaValue rawget(LuaValue key) {
        return unimplemented("rawget");
    }

    public LuaValue rawget(int key) {
        return rawget(valueOf(key));
    }

    public LuaValue rawget(String key) {
        return rawget(valueOf(key));
    }

    public void rawset(LuaValue key, LuaValue value) {
        if (key == null || value == null) {
            return;
        }
        unimplemented("rawset");
    }

    public void rawset(int key, LuaValue value) {
        if (value != null) {
            rawset(valueOf(key), value);
        }
    }

    public void rawset(int key, String value) {
        if (value != null) {
            rawset(key, valueOf(value));
        }
    }

    public void rawset(String key, LuaValue value) {
        if (key == null || value == null) {
            return;
        }
        rawset(valueOf(key), value);
    }

    public void rawset(String key, double value) {
        if (key != null) {
            rawset(valueOf(key), valueOf(value));
        }
    }

    public void rawset(String key, int value) {
        if (key != null) {
            rawset(valueOf(key), valueOf(value));
        }
    }

    public void rawset(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        rawset(valueOf(key), valueOf(value));
    }

    public void rawsetlist(int key0, Varargs values) {
        int n = values.narg();
        for (int i = 0; i < n; i++) {
            rawset(key0 + i, values.arg(i + 1));
        }
    }

    public void presize(int i) {
        typerror("table");
    }

    public Varargs next(LuaValue index) {
        return typerror("table");
    }

    public Varargs inext(LuaValue index) {
        return typerror("table");
    }

    public LuaValue load(LuaValue library) {
        return library.call(EMPTYSTRING, this);
    }

    @Override // luaj.Varargs
    public LuaValue arg(int index) {
        return index == 1 ? this : NIL;
    }

    @Override // luaj.Varargs
    public int narg() {
        return 1;
    }

    @Override // luaj.Varargs
    public LuaValue arg1() {
        return this;
    }

    public LuaValue getmetatable() {
        return null;
    }

    public LuaValue setmetatable(LuaValue metatable) {
        return argerror("table");
    }

    public LuaValue call() {
        return callmt().call(this);
    }

    public LuaValue call(LuaValue arg) {
        return callmt().call(this, arg);
    }

    public LuaValue call(String arg) {
        return call(valueOf(arg));
    }

    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return callmt().call(this, arg1, arg2);
    }

    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        return callmt().invoke(new LuaValue[]{this, arg1, arg2, arg3}).arg1();
    }

    public LuaValue method(String name) {
        return get(name).call(this);
    }

    public LuaValue method(LuaValue name) {
        return get(name).call(this);
    }

    public LuaValue method(String name, LuaValue arg) {
        return get(name).call(this, arg);
    }

    public LuaValue method(LuaValue name, LuaValue arg) {
        return get(name).call(this, arg);
    }

    public LuaValue method(String name, LuaValue arg1, LuaValue arg2) {
        return get(name).call(this, arg1, arg2);
    }

    public LuaValue method(LuaValue name, LuaValue arg1, LuaValue arg2) {
        return get(name).call(this, arg1, arg2);
    }

    public Varargs invoke() {
        return invoke(NONE);
    }

    public Varargs invoke(Varargs args) {
        return callmt().invoke(this, args);
    }

    public Varargs invoke(LuaValue arg, Varargs varargs) {
        return invoke(varargsOf(arg, varargs));
    }

    public Varargs invoke(LuaValue arg1, LuaValue arg2, Varargs varargs) {
        return invoke(varargsOf(arg1, arg2, varargs));
    }

    public Varargs invoke(LuaValue[] args) {
        return invoke(varargsOf(args));
    }

    public Varargs invoke(LuaValue[] args, Varargs varargs) {
        return invoke(varargsOf(args, varargs));
    }

    public Varargs invokemethod(String name) {
        return get(name).invoke(this);
    }

    public Varargs invokemethod(LuaValue name) {
        return get(name).invoke(this);
    }

    public Varargs invokemethod(String name, Varargs args) {
        return get(name).invoke(varargsOf(this, args));
    }

    public Varargs invokemethod(LuaValue name, Varargs args) {
        return get(name).invoke(varargsOf(this, args));
    }

    public Varargs invokemethod(String name, LuaValue[] args) {
        return get(name).invoke(varargsOf(this, varargsOf(args)));
    }

    public Varargs invokemethod(LuaValue name, LuaValue[] args) {
        return get(name).invoke(varargsOf(this, varargsOf(args)));
    }

    protected LuaValue callmt() {
        return checkmetatag(CALL, "attempt to call ");
    }

    public LuaValue not() {
        return FALSE;
    }

    public LuaValue neg() {
        return checkmetatag(UNM, "attempt to perform arithmetic on ").call(this);
    }

    public LuaValue bnot() {
        return checkmetatag(BNOT, "attempt to perform bitwise on ").call(this);
    }

    public LuaValue len() {
        return checkmetatag(LEN, "attempt to get length of ").call(this);
    }

    public int length() {
        return len().toint();
    }

    public int rawlen() {
        typerror("table or string");
        return 0;
    }

    public boolean equals(Object obj) {
        return this == obj;
    }

    public LuaValue eq(LuaValue val) {
        return eq_b(val) ? TRUE : FALSE;
    }

    public boolean eq_b(LuaValue val) {
        return this == val;
    }

    public LuaValue neq(LuaValue val) {
        return eq_b(val) ? FALSE : TRUE;
    }

    public boolean neq_b(LuaValue val) {
        return !eq_b(val);
    }

    public boolean raweq(LuaValue val) {
        return this == val;
    }

    public boolean raweq(LuaString val) {
        return false;
    }

    public static final boolean eqmtcall(LuaValue lhs, LuaValue rhs) {
        LuaValue mt;
        LuaValue h = NIL;
        LuaValue mt2 = lhs.getmetatable();
        if (mt2 != null) {
            h = mt2.rawget(EQ);
        }
        if (h.isnil() && (mt = rhs.getmetatable()) != null) {
            h = mt.rawget(EQ);
        }
        if (h.isnil()) {
            return false;
        }
        return h.call(lhs, rhs).toboolean();
    }

    public LuaValue add(LuaValue rhs) {
        return arithmt(ADD, this, rhs);
    }

    public LuaValue sub(LuaValue rhs) {
        return arithmt(SUB, this, rhs);
    }

    public LuaValue mul(LuaValue rhs) {
        return arithmt(MUL, this, rhs);
    }

    public LuaValue pow(LuaValue rhs) {
        return arithmt(POW, this, rhs);
    }

    public LuaValue div(LuaValue rhs) {
        return arithmt(DIV, this, rhs);
    }

    public LuaValue idiv(LuaValue rhs) {
        return arithmt(IDIV, this, rhs);
    }

    public LuaValue band(LuaValue rhs) {
        return bitwisemt(BAND, this, rhs);
    }

    public LuaValue bor(LuaValue rhs) {
        return bitwisemt(BOR, this, rhs);
    }

    public LuaValue bxor(LuaValue rhs) {
        return bitwisemt(BXOR, this, rhs);
    }

    public LuaValue shl(LuaValue rhs) {
        return bitwisemt(SHL, this, rhs);
    }

    public LuaValue shr(LuaValue rhs) {
        return bitwisemt(SHR, this, rhs);
    }

    public LuaValue mod(LuaValue rhs) {
        return arithmt(MOD, this, rhs);
    }

    public String typenamemt() {
        LuaValue h = metatag(NAME);
        return h.isstring() ? h.tojstring() : typename();
    }

    public static LuaValue arithmt(LuaValue tag, LuaValue op1, LuaValue op2) {
        LuaValue h = op1.metatag(tag);
        if (h.isnil()) {
            h = op2.metatag(tag);
            if (h.isnil()) {
                error("attempt to perform arithmetic " + tag + " on a " + op1.typenamemt() + " value and a " + op2.typenamemt() + " value");
            }
        }
        return h.call(op1, op2);
    }

    public static String tagToOp(LuaValue tag) {
        return tag == BAND ? "&" : tag == BOR ? "|" : tag == BXOR ? "~" : tag == SHL ? "<<" : tag == SHR ? ">>" : tag.tojstring();
    }

    public static LuaValue bitwisemt(LuaValue tag, LuaValue op1, LuaValue op2) {
        LuaValue h = op1.metatag(tag);
        if (h.isnil()) {
            h = op2.metatag(tag);
            if (h.isnil()) {
                LuaValue bad = null;
                if (op1.isnumber() && !op1.islongnumber()) {
                    bad = op1;
                } else if (op2.isnumber() && !op2.islongnumber()) {
                    bad = op2;
                }
                String op = tagToOp(tag);
                if (bad != null) {
                    throw new LuaError("number '" + bad.tojstring() + "' has no integer representation (for bitwise operation '" + op1.tojstring() + " " + op + " " + op2.tojstring() + "')");
                }
                error("attempt to perform bitwise operation " + op + " on a " + op1.typename() + " value and a " + op2.typename() + " value");
            }
        }
        return h.call(op1, op2);
    }

    public LuaValue lt(LuaValue rhs) {
        return lt_b(rhs) ? TRUE : FALSE;
    }

    public boolean lt_b(LuaValue rhs) {
        return comparemt(LT, rhs).toboolean();
    }

    public LuaValue lteq(LuaValue rhs) {
        return lteq_b(rhs) ? TRUE : FALSE;
    }

    public boolean lteq_b(LuaValue rhs) {
        return comparemt(LE, rhs).toboolean();
    }

    public LuaValue gt(LuaValue rhs) {
        return gt_b(rhs) ? TRUE : FALSE;
    }

    public boolean gt_b(LuaValue rhs) {
        return rhs.comparemt(LE, this).toboolean();
    }

    public LuaValue gteq(LuaValue rhs) {
        return gteq_b(rhs) ? TRUE : FALSE;
    }

    public boolean gteq_b(LuaValue rhs) {
        return rhs.comparemt(LT, this).toboolean();
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0037, code lost:
    
        if (r0.isnil() == false) goto L14;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public luaj.LuaValue comparemt(luaj.LuaValue r7, luaj.LuaValue r8) {
        /*
            r6 = this;
            luaj.LuaValue r0 = r6.metatag(r7)
            boolean r4 = r0.isnil()
            if (r4 == 0) goto L14
            luaj.LuaValue r0 = r8.metatag(r7)
            boolean r4 = r0.isnil()
            if (r4 != 0) goto L19
        L14:
            luaj.LuaValue r4 = r0.call(r6, r8)
        L18:
            return r4
        L19:
            luaj.LuaString r4 = luaj.LuaValue.LE
            boolean r4 = r4.raweq(r7)
            if (r4 == 0) goto L42
            luaj.LuaString r4 = luaj.LuaValue.LT
            luaj.LuaValue r0 = r6.metatag(r4)
            boolean r4 = r0.isnil()
            if (r4 == 0) goto L39
            luaj.LuaString r4 = luaj.LuaValue.LT
            luaj.LuaValue r0 = r8.metatag(r4)
            boolean r4 = r0.isnil()
            if (r4 != 0) goto L42
        L39:
            luaj.LuaValue r4 = r0.call(r8, r6)
            luaj.LuaValue r4 = r4.not()
            goto L18
        L42:
            java.lang.String r2 = r6.typenamemt()
            java.lang.String r3 = r8.typenamemt()
            boolean r4 = r2.equals(r3)
            if (r4 == 0) goto L79
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            java.lang.String r5 = "two "
            r4.<init>(r5)
            java.lang.StringBuilder r4 = r4.append(r2)
            java.lang.String r5 = " values"
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r1 = r4.toString()
        L65:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            java.lang.String r5 = "attempt to compare "
            r4.<init>(r5)
            java.lang.StringBuilder r4 = r4.append(r1)
            java.lang.String r4 = r4.toString()
            luaj.LuaValue r4 = error(r4)
            goto L18
        L79:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            java.lang.String r5 = java.lang.String.valueOf(r2)
            r4.<init>(r5)
            java.lang.String r5 = " with "
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r4 = r4.append(r3)
            java.lang.String r1 = r4.toString()
            goto L65
        */
        throw new UnsupportedOperationException("Method not decompiled: luaj.LuaValue.comparemt(luaj.LuaValue, luaj.LuaValue):luaj.LuaValue");
    }

    public int strcmp(LuaValue rhs) {
        error("attempt to compare " + typename());
        return 0;
    }

    public int strcmp(LuaString rhs) {
        error("attempt to compare " + typename());
        return 0;
    }

    public LuaValue concat(LuaValue rhs) {
        return concatmt(rhs);
    }

    public LuaValue concatTo(LuaValue lhs) {
        return lhs.concatmt(this);
    }

    public LuaValue concatTo(LuaNumber lhs) {
        return lhs.concatmt(this);
    }

    public LuaValue concatTo(LuaString lhs) {
        return lhs.concatmt(this);
    }

    public Buffer buffer() {
        return new Buffer(this);
    }

    public Buffer concat(Buffer rhs) {
        return rhs.concatTo(this);
    }

    public LuaValue concatmt(LuaValue rhs) {
        LuaValue h = metatag(CONCAT);
        if (h.isnil()) {
            h = rhs.metatag(CONCAT);
            if (h.isnil()) {
                LuaValue bad = null;
                if (!isstring()) {
                    bad = this;
                } else if (!rhs.isstring()) {
                    bad = rhs;
                }
                if (bad != null) {
                    throw new LuaError("attempt to concatenate a " + bad.typename() + " value");
                }
                error("attempt to concatenate " + typename() + " and " + rhs.typename());
            }
        }
        return h.call(this, rhs);
    }

    public LuaValue and(LuaValue rhs) {
        return toboolean() ? rhs : this;
    }

    public LuaValue or(LuaValue rhs) {
        return toboolean() ? this : rhs;
    }

    public boolean testfor_b(LuaValue limit, LuaValue step) {
        return step.gt_b(ZERO) ? lteq_b(limit) : gteq_b(limit);
    }

    public LuaString strvalue() {
        typerror("string or number");
        return null;
    }

    public LuaValue strongvalue() {
        return this;
    }

    public static LuaBoolean valueOf(boolean b) {
        return b ? TRUE : FALSE;
    }

    public static LuaLong valueOf(long l) {
        return LuaLong.valueOf(l);
    }

    public static LuaNumber valueOf(double d) {
        return LuaDouble.valueOf(d);
    }

    public static LuaNumber numberOf(String s) {
        if (s.length() <= 20 && s.indexOf(46) == -1 && s.indexOf(101) == -1 && s.indexOf(69) == -1) {
            try {
                return LuaLong.valueOf(Long.parseLong(s));
            } catch (NumberFormatException e) {
            }
        }
        return LuaDouble.valueOf(Double.parseDouble(s));
    }

    public static LuaString valueOf(String s) {
        return LuaString.valueOf(s);
    }

    public static LuaString valueOf(byte[] bytes) {
        return LuaString.valueOf(bytes);
    }

    public static LuaString valueOf(byte[] bytes, int off, int len) {
        return LuaString.valueOf(bytes, off, len);
    }

    public static LuaTable tableOf() {
        return new LuaTable();
    }

    public static LuaTable tableOf(Varargs varargs, int firstarg) {
        return new LuaTable(varargs, firstarg);
    }

    public static LuaTable tableOf(int narray, int nhash) {
        return new LuaTable(narray, nhash);
    }

    public static LuaTable listOf(LuaValue[] unnamedValues) {
        return new LuaTable(null, unnamedValues, null);
    }

    public static LuaTable listOf(LuaValue[] unnamedValues, Varargs lastarg) {
        return new LuaTable(null, unnamedValues, lastarg);
    }

    public static LuaTable tableOf(LuaValue[] namedValues) {
        return new LuaTable(namedValues, null, null);
    }

    public static LuaTable tableOf(LuaValue[] namedValues, LuaValue[] unnamedValues) {
        return new LuaTable(namedValues, unnamedValues, null);
    }

    public static LuaTable tableOf(LuaValue[] namedValues, LuaValue[] unnamedValues, Varargs lastarg) {
        return new LuaTable(namedValues, unnamedValues, lastarg);
    }

    protected static LuaValue gettable(LuaValue t, LuaValue key) {
        LuaValue tm;
        int loop = 0;
        do {
            if (t.istable()) {
                LuaValue res = t.rawget(key);
                if (!res.isnil()) {
                    return res;
                }
                tm = t.metatag(INDEX);
                if (tm.isnil()) {
                    return res;
                }
            } else {
                tm = t.metatag(INDEX);
                if (tm.isnil()) {
                    t.indexerror(key.tojstring());
                }
            }
            if (tm.isfunction()) {
                LuaValue res2 = tm.call(t, key);
                return res2;
            }
            t = tm;
            loop++;
        } while (loop < 100);
        error("loop in gettable");
        LuaValue res3 = NIL;
        return res3;
    }

    protected static boolean settable(LuaValue t, LuaValue key, LuaValue value) {
        LuaValue tm;
        int loop = 0;
        do {
            if (t.istable()) {
                if (t.rawget(key).isnil()) {
                    tm = t.metatag(NEWINDEX);
                    if (tm.isnil()) {
                    }
                }
                t.rawset(key, value);
                return true;
            }
            tm = t.metatag(NEWINDEX);
            if (tm.isnil()) {
                throw new LuaError("table expected for set index ('" + key + "') value, got " + t.typename());
            }
            if (tm.isfunction()) {
                tm.call(t, key, value);
                return true;
            }
            t = tm;
            loop++;
        } while (loop < 100);
        error("loop in settable");
        return false;
    }

    public LuaValue metatag(LuaValue tag) {
        LuaValue mt = getmetatable();
        return mt == null ? NIL : mt.rawget(tag);
    }

    protected LuaValue checkmetatag(LuaValue tag, String reason) {
        LuaValue h = metatag(tag);
        if (h.isnil()) {
            if (tag == BNOT && isnumber()) {
                throw new LuaError("number has no integer representation (for bitwise operation '~" + tojstring() + "')");
            }
            throw new LuaError(String.valueOf(reason) + "a " + typenamemt() + " value");
        }
        return h;
    }

    protected static Metatable metatableOf(LuaValue mt) {
        if (mt != null && mt.istable()) {
            LuaValue mode = mt.rawget(MODE);
            if (mode.isstring()) {
                String m = mode.tojstring();
                boolean weakkeys = m.indexOf(Message.ERROR_SPEEDHACK_FAIL) >= 0;
                boolean weakvalues = m.indexOf(118) >= 0;
                if (weakkeys || weakvalues) {
                    return new WeakTable(weakkeys, weakvalues, mt);
                }
            }
            return (LuaTable) mt;
        }
        if (mt != null) {
            return new NonTableMetatable(mt);
        }
        return null;
    }

    private void indexerror(String key) {
        error("attempt to index ? (a " + typename() + " value) with key '" + key + SearchMenuItem.HEX_UTF_8);
    }

    public static Varargs varargsOf(LuaValue[] v) {
        switch (v.length) {
            case 0:
                return NONE;
            case 1:
                return v[0];
            case 2:
                return new Varargs.PairVarargs(v[0], v[1]);
            default:
                return new Varargs.ArrayVarargs(v, NONE);
        }
    }

    public static Varargs varargsOf(LuaValue[] v, Varargs r) {
        switch (v.length) {
            case 0:
                return r;
            case 1:
                if (r.narg() > 0) {
                    return new Varargs.PairVarargs(v[0], r);
                }
                return v[0];
            case 2:
                if (r.narg() > 0) {
                    return new Varargs.ArrayVarargs(v, r);
                }
                return new Varargs.PairVarargs(v[0], v[1]);
            default:
                return new Varargs.ArrayVarargs(v, r);
        }
    }

    public static Varargs varargsOf(LuaValue[] v, int offset, int length) {
        switch (length) {
            case 0:
                return NONE;
            case 1:
                return v[offset];
            case 2:
                return new Varargs.PairVarargs(v[offset + 0], v[offset + 1]);
            default:
                return new Varargs.ArrayPartVarargs(v, offset, length, NONE);
        }
    }

    public static Varargs varargsOf(LuaValue[] v, int offset, int length, Varargs more) {
        switch (length) {
            case 0:
                return more;
            case 1:
                if (more.narg() > 0) {
                    return new Varargs.PairVarargs(v[offset], more);
                }
                return v[offset];
            case 2:
                if (more.narg() > 0) {
                    return new Varargs.ArrayPartVarargs(v, offset, length, more);
                }
                return new Varargs.PairVarargs(v[offset], v[offset + 1]);
            default:
                return new Varargs.ArrayPartVarargs(v, offset, length, more);
        }
    }

    public static Varargs varargsOf(LuaValue v, Varargs r) {
        switch (r.narg()) {
            case 0:
                return v;
            default:
                return new Varargs.PairVarargs(v, r);
        }
    }

    public static Varargs varargsOf(LuaValue v1, LuaValue v2, Varargs v3) {
        switch (v3.narg()) {
            case 0:
                return new Varargs.PairVarargs(v1, v2);
            default:
                return new Varargs.ArrayPartVarargs(new LuaValue[]{v1, v2}, 0, 2, v3);
        }
    }

    public Varargs onInvoke(Varargs args, boolean tailcall) {
        return invoke(args);
    }

    public void initupvalue1(LuaValue env) {
    }

    private static final class None extends LuaNil {
        private None() {
        }

        /* synthetic */ None(None none) {
            this();
        }

        @Override // luaj.LuaValue, luaj.Varargs
        public LuaValue arg(int i) {
            return NIL;
        }

        @Override // luaj.LuaValue, luaj.Varargs
        public int narg() {
            return 0;
        }

        @Override // luaj.LuaValue, luaj.Varargs
        public LuaValue arg1() {
            return NIL;
        }

        @Override // luaj.LuaNil, luaj.LuaValue, luaj.Varargs
        public String tojstring() {
            return "none";
        }

        @Override // luaj.LuaValue, luaj.Varargs
        public Varargs subargs(int start) {
            return start > 0 ? this : argerror(1, "start must be > 0");
        }

        @Override // luaj.Varargs
        void copyto(LuaValue[] dest, int offset, int length) {
            int offset2 = offset;
            while (length > 0) {
                dest[offset2] = NIL;
                length--;
                offset2++;
            }
        }
    }

    @Override // luaj.Varargs
    public Varargs subargs(int start) {
        if (start != 1) {
            if (start > 1) {
                return NONE;
            }
            return argerror(1, "start must be > 0");
        }
        return this;
    }

    public static LuaValue parseHex(CharSequence str) {
        int s;
        char ch;
        char ch2;
        char ch3;
        double sgn = 1.0d;
        boolean neg1 = false;
        int length = str.length();
        int s2 = 0;
        while (s2 < length && LexState.isspace(str.charAt(s2))) {
            s2++;
        }
        if (s2 >= length || !((ch3 = str.charAt(s2)) == '-' || ch3 == '+')) {
            s = s2;
        } else {
            sgn = ch3 == '-' ? -1.0d : 1.0d;
            s = s2 + 1;
        }
        if (s + 2 >= length) {
            return null;
        }
        int s3 = s + 1;
        if (str.charAt(s) != '0') {
            return null;
        }
        char ch4 = str.charAt(s3);
        if (ch4 != 'x' && ch4 != 'X') {
            return null;
        }
        int s4 = s3 + 1;
        double m = 0.0d;
        long l = 0;
        int e = 0;
        int longSize = 0;
        while (s4 < length) {
            char ch5 = str.charAt(s4);
            if (!LexState.isxdigit(ch5)) {
                break;
            }
            int v = LexState.hexvalue(ch5);
            if (longSize != 0 || v != 0) {
                if (longSize < 30) {
                    m = (16.0d * m) + ((double) v);
                } else {
                    e += 4;
                }
                l = (16 * l) + ((long) v);
                longSize++;
            }
            s4++;
        }
        if (s4 < length && str.charAt(s4) == '.') {
            longSize += AddressItem.FLAG_RANGE;
            s4++;
            int max = Integer.MAX_VALUE;
            while (s4 < length) {
                char ch6 = str.charAt(s4);
                if (!LexState.isxdigit(ch6)) {
                    break;
                }
                if (max == Integer.MAX_VALUE && m != 0.0d) {
                    max = s4 + 30;
                }
                if (s4 < max) {
                    m = (16.0d * m) + ((double) LexState.hexvalue(ch6));
                    e -= 4;
                    longSize++;
                }
                s4++;
            }
        }
        if ((8388607 & longSize) == 0) {
            return null;
        }
        if (s4 < length && ((ch = str.charAt(s4)) == 'p' || ch == 'P')) {
            longSize += AddressItem.FLAG_REVERT;
            s4++;
            int exp1 = 0;
            if (s4 < length && ((ch2 = str.charAt(s4)) == '-' || ch2 == '+')) {
                neg1 = ch2 == '-';
                s4++;
            }
            boolean found = false;
            while (s4 < length) {
                char ch7 = str.charAt(s4);
                if (!LexState.isdigit(ch7)) {
                    break;
                }
                exp1 = ((exp1 * 10) + ch7) - 48;
                s4++;
                found = true;
            }
            if (!found) {
                return null;
            }
            if (neg1) {
                exp1 = -exp1;
            }
            e += exp1;
        }
        while (s4 < length && LexState.isspace(str.charAt(s4))) {
            s4++;
        }
        if (s4 != length) {
            return null;
        }
        if (longSize >= 8388608) {
            return valueOf(sgn * m * Math.pow(2.0d, e));
        }
        if (sgn <= 0.0d) {
            l = -l;
        }
        return LuaLong.valueOf(l);
    }

    public static LuaValue parseNumber(String str, LuaValue fallback) {
        LuaValue ret = null;
        try {
            ret = parseNumber(str);
        } catch (NumberFormatException e) {
        }
        if (ret == null) {
            return fallback;
        }
        LuaValue fallback2 = ret;
        return fallback2;
    }

    public static LuaValue parseNumber(String str) {
        return (str.indexOf(120) >= 0 || str.indexOf(88) >= 0) ? parseHex(str) : numberOf(str.trim());
    }

    public boolean isDeprecated() {
        return false;
    }

    public static void main(String[] args) throws Throwable {
    }
}
