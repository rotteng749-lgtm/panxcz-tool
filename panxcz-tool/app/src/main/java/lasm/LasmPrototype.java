package lasm;

import android.sup.GrowingArrayUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import lasm.LasmBase;
import luaj.LocVars;
import luaj.Lua;
import luaj.LuaValue;
import luaj.Prototype;
import luaj.Upvaldesc;
import luaj.compiler.Constants;
import luaj.compiler.InstructionPtr;
import luaj.compiler.LasmLua;

/* JADX INFO: loaded from: classes.dex */
public class LasmPrototype extends Prototype {
    private static final int INITIAL_CAPACITY = 8;
    HashMap<LuaValue, Integer> consts;
    Link[] dests;
    Link[] funcs;
    HashMap<String, Link> labels;
    LasmLua lasmLua;
    HashMap<String, Link> pNames;
    Link[] stack;
    int kSize = 0;
    int codeSize = 0;
    int currLine = 0;
    int pSize = 0;
    int lineinfoSize = 0;
    int locvarsSize = 0;
    int upvaluesSize = 0;
    int destsSize = 0;
    int funcsSize = 0;

    static class Link {
        Token name;
        int pc;

        Link(Token name, int pc) {
            this.name = name;
            this.pc = pc;
        }
    }

    LasmPrototype() {
        this.k = new LuaValue[8];
        this.code = new int[8];
        this.p = new Prototype[8];
        this.lineinfo = new int[8];
        this.locvars = new LocVars[8];
        this.upvalues = new Upvaldesc[8];
        this.dests = new Link[8];
        this.funcs = new Link[8];
        this.consts = new HashMap<>();
        this.labels = new HashMap<>();
        this.pNames = new HashMap<>();
        this.lasmLua = new LasmLua(this);
    }

    void init(Token n) throws ParseException {
        this.maxstacksize = Math.max(2, LasmBase.parseIntMax(n, 255));
        if (this.maxstacksize < this.numparams) {
            throw new LasmParseException(n, ".maxstacksize (" + this.maxstacksize + ") must be not less .numparams (" + this.numparams + ")");
        }
        this.stack = new Link[this.maxstacksize];
    }

    LasmPrototype build() throws ParseException {
        if (this.k.length != this.kSize) {
            this.k = (LuaValue[]) Arrays.copyOf(this.k, this.kSize);
        }
        if (this.code.length != this.codeSize) {
            this.code = Arrays.copyOf(this.code, this.codeSize);
        }
        if (this.p.length != this.pSize) {
            this.p = (Prototype[]) Arrays.copyOf(this.p, this.pSize);
        }
        if (this.lineinfo.length != this.lineinfoSize) {
            this.lineinfo = Arrays.copyOf(this.lineinfo, this.lineinfoSize);
        }
        if (this.locvars.length != this.locvarsSize) {
            this.locvars = (LocVars[]) Arrays.copyOf(this.locvars, this.locvarsSize);
        }
        if (this.upvalues.length != this.upvaluesSize) {
            this.upvalues = (Upvaldesc[]) Arrays.copyOf(this.upvalues, this.upvaluesSize);
        }
        int in = this.destsSize;
        for (int i = 0; i < in; i++) {
            Link dest = this.dests[i];
            Link label = this.labels.get(dest.name.image.toLowerCase(Locale.ENGLISH));
            if (label == null) {
                throw new LasmParseException(dest.name, "The label " + dest.name.image + " is missing");
            }
            Constants.SETARG_sBx(new InstructionPtr(this.code, dest.pc), (label.pc - dest.pc) - 1);
        }
        int in2 = this.funcsSize;
        for (int i2 = 0; i2 < in2; i2++) {
            Link func = this.funcs[i2];
            Link proto = this.pNames.get(func.name.image.toLowerCase(Locale.ENGLISH));
            if (proto == null) {
                throw new LasmParseException(func.name, "The function " + func.name.image + " is missing");
            }
            Constants.SETARG_Bx(new InstructionPtr(this.code, func.pc), proto.pc);
        }
        int in3 = this.stack.length;
        for (int i3 = 0; i3 < in3; i3++) {
            Link local = this.stack[i3];
            if (local != null) {
                throw new LasmParseException(local.name, "No '.end local' for local variable v" + i3);
            }
        }
        this.dests = null;
        this.funcs = null;
        this.consts = null;
        this.labels = null;
        this.pNames = null;
        this.lasmLua = null;
        this.stack = null;
        return this;
    }

    void addFunc(Token name, LasmPrototype f) throws ParseException {
        String key = name.image.toLowerCase(Locale.ENGLISH);
        Link prev = this.pNames.get(key);
        if (prev != null) {
            throw new LasmParseException(name, "Duplicate function name '" + name.image + "' (used at line " + prev.name.beginLine + ")");
        }
        this.pNames.put(key, new Link(name, this.pSize));
        Prototype[] prototypeArr = this.p;
        int i = this.pSize;
        this.pSize = i + 1;
        this.p = (Prototype[]) GrowingArrayUtils.append((LasmPrototype[]) prototypeArr, i, f);
    }

    void addUpval(LasmBase.Internal vu, Token name) throws ParseException {
        Upvaldesc[] upvaldescArr = this.upvalues;
        int i = this.upvaluesSize;
        this.upvaluesSize = i + 1;
        this.upvalues = (Upvaldesc[]) GrowingArrayUtils.append(upvaldescArr, i, new Upvaldesc(Lasm.parseLuaString(name), vu instanceof LasmBase.V, vu.code));
    }

    void check(LuaValue r) throws ParseException {
    }

    void addLabel(Token name) throws ParseException {
        String key = name.image.toLowerCase(Locale.ENGLISH);
        Link prev = this.labels.get(key);
        if (prev != null) {
            throw new LasmParseException(name, "Try redefine label '" + name.image + "' (already defined at line " + prev.name.beginLine + ")");
        }
        this.labels.put(key, new Link(name, this.codeSize));
    }

    void addLine(Token n) throws ParseException {
        this.currLine = Lasm.parseInt(n);
    }

    void startLocal(LasmBase.V v, Token name) throws ParseException {
        check(v);
        int var = v.type();
        if (this.stack[var] != null) {
            throw new LasmParseException(v.token, "Try redefine not ended local variable v" + var + " (v" + var + " defined at line " + this.stack[var].name.beginLine + " as '" + this.stack[var].name.image + "')");
        }
        this.stack[var] = new Link(name, this.locvarsSize);
        LocVars[] locVarsArr = this.locvars;
        int i = this.locvarsSize;
        this.locvarsSize = i + 1;
        this.locvars = (LocVars[]) GrowingArrayUtils.append(locVarsArr, i, new LocVars(Lasm.parseLuaString(name), this.codeSize, this.codeSize + 1));
    }

    void endLocal(LasmBase.V v, Token name, int offset) throws ParseException {
        check(v);
        int var = v.type();
        if (this.stack[var] == null) {
            throw new LasmParseException(v.token, "Try end not defined local variable v" + var);
        }
        if (!this.stack[var].name.image.equals(name.image)) {
            throw new LasmParseException(v.token, "Try end local variable v" + var + " with different name (v" + var + " defined at line " + this.stack[var].name.beginLine + " as '" + this.stack[var].name.image + "')");
        }
        this.locvars[this.stack[var].pc].endpc = this.codeSize + offset;
        this.stack[var] = null;
    }

    int getConst(LuaValue v) {
        if (v instanceof LasmBase.Const) {
            return v.type();
        }
        Integer pos = this.consts.get(v);
        if (pos == null) {
            pos = Integer.valueOf(this.kSize);
            this.consts.put(v, pos);
            LuaValue[] luaValueArr = this.k;
            int i = this.kSize;
            this.kSize = i + 1;
            this.k = (LuaValue[]) GrowingArrayUtils.append(luaValueArr, i, v);
        }
        return pos.intValue();
    }

    int getDest(Token d) throws ParseException {
        int offset = d.kind == 60 ? Lasm.parseInt(d) : -1;
        if (d.kind != 60) {
            Link label = this.labels.get(d.image.toLowerCase(Locale.ENGLISH));
            if (label != null) {
                return (label.pc - this.codeSize) - 1;
            }
            Link[] linkArr = this.dests;
            int i = this.destsSize;
            this.destsSize = i + 1;
            this.dests = (Link[]) GrowingArrayUtils.append(linkArr, i, new Link(d, this.codeSize));
            return offset;
        }
        return offset;
    }

    int getFunc(Token d) throws ParseException {
        if (d.kind == 79) {
            return Lasm.parseInt(d);
        }
        Link[] linkArr = this.funcs;
        int i = this.funcsSize;
        this.funcsSize = i + 1;
        this.funcs = (Link[]) GrowingArrayUtils.append(linkArr, i, new Link(d, this.codeSize));
        return Lua.MAXARG_Bx;
    }

    int RK(LuaValue a) {
        return a instanceof LasmBase.Internal ? a.type() : Lua.RKASK(getConst(a));
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:102:0x0194  */
    /* JADX WARN: Removed duplicated region for block: B:105:0x019e  */
    /* JADX WARN: Removed duplicated region for block: B:107:0x01a8 A[Catch: Throwable -> 0x002e, TryCatch #0 {Throwable -> 0x002e, blocks: (B:3:0x000c, B:4:0x000e, B:5:0x0011, B:6:0x002d, B:11:0x0034, B:13:0x003c, B:14:0x004a, B:17:0x0058, B:19:0x005e, B:20:0x0073, B:35:0x00a5, B:72:0x00fa, B:75:0x0112, B:76:0x012a, B:85:0x014f, B:87:0x0155, B:88:0x0159, B:91:0x0168, B:98:0x017b, B:103:0x0196, B:106:0x019f, B:107:0x01a8, B:112:0x01b3, B:116:0x01d0, B:113:0x01c0, B:121:0x01e4, B:124:0x01ed, B:125:0x01f3, B:127:0x0201, B:130:0x0208, B:134:0x0218, B:135:0x0224), top: B:139:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:120:0x01e2  */
    /* JADX WARN: Removed duplicated region for block: B:123:0x01ec  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x01f3 A[Catch: Throwable -> 0x002e, TryCatch #0 {Throwable -> 0x002e, blocks: (B:3:0x000c, B:4:0x000e, B:5:0x0011, B:6:0x002d, B:11:0x0034, B:13:0x003c, B:14:0x004a, B:17:0x0058, B:19:0x005e, B:20:0x0073, B:35:0x00a5, B:72:0x00fa, B:75:0x0112, B:76:0x012a, B:85:0x014f, B:87:0x0155, B:88:0x0159, B:91:0x0168, B:98:0x017b, B:103:0x0196, B:106:0x019f, B:107:0x01a8, B:112:0x01b3, B:116:0x01d0, B:113:0x01c0, B:121:0x01e4, B:124:0x01ed, B:125:0x01f3, B:127:0x0201, B:130:0x0208, B:134:0x0218, B:135:0x0224), top: B:139:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0090  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0094  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0098  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x009c  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00a0  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00a4  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00b9  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00bc  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00c0  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00c4  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00c8  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00cc  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00d0  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x00d4  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00d8  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x00dc  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x00e0  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x00e4  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x00e8  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x00ec  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x00f0  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x00f4  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x00f8  */
    /* JADX WARN: Removed duplicated region for block: B:80:0x0145  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x0149  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x014d  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x0155 A[Catch: Throwable -> 0x002e, TryCatch #0 {Throwable -> 0x002e, blocks: (B:3:0x000c, B:4:0x000e, B:5:0x0011, B:6:0x002d, B:11:0x0034, B:13:0x003c, B:14:0x004a, B:17:0x0058, B:19:0x005e, B:20:0x0073, B:35:0x00a5, B:72:0x00fa, B:75:0x0112, B:76:0x012a, B:85:0x014f, B:87:0x0155, B:88:0x0159, B:91:0x0168, B:98:0x017b, B:103:0x0196, B:106:0x019f, B:107:0x01a8, B:112:0x01b3, B:116:0x01d0, B:113:0x01c0, B:121:0x01e4, B:124:0x01ed, B:125:0x01f3, B:127:0x0201, B:130:0x0208, B:134:0x0218, B:135:0x0224), top: B:139:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0164  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x0175  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x0179  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void addOp(lasm.Token r13, luaj.LuaValue r14, luaj.LuaValue r15, luaj.LuaValue r16, lasm.Token r17, lasm.Token r18) throws lasm.ParseException {
        /*
            Method dump skipped, instruction units count: 750
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: lasm.LasmPrototype.addOp(lasm.Token, luaj.LuaValue, luaj.LuaValue, luaj.LuaValue, lasm.Token, lasm.Token):void");
    }

    public int addOp(int op) {
        if (this.currLine != 0) {
            if (this.lineinfo.length <= this.codeSize) {
                this.lineinfo = Arrays.copyOf(this.lineinfo, GrowingArrayUtils.growSize(this.codeSize + 1));
            }
            this.lineinfo[this.codeSize] = this.currLine;
            this.lineinfoSize = this.codeSize + 1;
        }
        int[] iArr = this.code;
        int i = this.codeSize;
        this.codeSize = i + 1;
        this.code = GrowingArrayUtils.append(iArr, i, op);
        return 0;
    }
}
