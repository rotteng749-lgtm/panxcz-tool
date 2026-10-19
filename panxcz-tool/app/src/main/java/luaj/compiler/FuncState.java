package luaj.compiler;

import java.util.Hashtable;
import luaj.LocVars;
import luaj.Lua;
import luaj.LuaString;
import luaj.LuaValue;
import luaj.Prototype;
import luaj.Upvaldesc;
import luaj.compiler.LexState;

/* JADX INFO: loaded from: classes.dex */
public class FuncState extends Constants {
    BlockCnt bl;
    Prototype f;
    int firstlocal;
    short freereg;
    Hashtable<LuaValue, Integer> h;
    IntPtr jpc;
    int lasttarget;
    LexState ls;
    short nactvar;
    int nk;
    short nlocvars;
    int np;
    short nups;
    int pc;
    FuncState prev;

    static class BlockCnt {
        short firstgoto;
        short firstlabel;
        boolean isloop;
        short nactvar;
        BlockCnt previous;
        boolean upval;

        BlockCnt() {
        }

        public String toString() {
            return "BlockCnt [firstlabel=" + ((int) this.firstlabel) + ", firstgoto=" + ((int) this.firstgoto) + ", nactvar=" + ((int) this.nactvar) + ", upval=" + this.upval + ", isloop=" + this.isloop + ']';
        }
    }

    public String toString() {
        return "FuncState [pc=" + this.pc + ", lasttarget=" + this.lasttarget + ", nk=" + this.nk + ", np=" + this.np + ", firstlocal=" + this.firstlocal + ", nlocvars=" + ((int) this.nlocvars) + ", nactvar=" + ((int) this.nactvar) + ", nups=" + ((int) this.nups) + ", freereg=" + ((int) this.freereg) + ']';
    }

    FuncState() {
    }

    InstructionPtr getcodePtr(LexState.expdesc e) {
        return new InstructionPtr(this.f.code, e.u.info);
    }

    int getcode(LexState.expdesc e) {
        return this.f.code[e.u.info];
    }

    int codeAsBx(int o, int A, int sBx) {
        return codeABx(o, A, Lua.MAXARG_sBx + sBx);
    }

    void setmultret(LexState.expdesc e) {
        setreturns(e, -1);
    }

    void checkrepeated(LexState.Labeldesc[] ll, int ll_n, LuaString label) {
        for (int i = this.bl.firstlabel; i < ll_n; i++) {
            if (label.eq_b(ll[i].name)) {
                String msg = this.ls.L.pushfstring("label '" + label + "' already defined on line " + ll[i].line);
                this.ls.semerror(msg);
            }
        }
    }

    void checklimit(int v, int l, String msg) {
        if (v > l) {
            errorlimit(l, msg);
        }
    }

    void errorlimit(int limit, String what) {
        String msg;
        if (this.f.linedefined == 0) {
            msg = this.ls.L.pushfstring("main function has more than " + limit + ' ' + what);
        } else {
            msg = this.ls.L.pushfstring("function at line " + this.f.linedefined + " has more than " + limit + ' ' + what);
        }
        this.ls.lexerror(msg, 0);
    }

    LocVars getlocvar(int i) {
        int idx = this.ls.dyd.actvar[this.firstlocal + i].idx;
        _assert(idx < this.nlocvars);
        return this.f.locvars[idx];
    }

    void removevars(int tolevel) {
        this.ls.dyd.n_actvar -= this.nactvar - tolevel;
        while (this.nactvar > tolevel) {
            short s = (short) (this.nactvar - 1);
            this.nactvar = s;
            getlocvar(s).endpc = this.pc;
        }
    }

    int searchupvalue(LuaString name) {
        Upvaldesc[] up = this.f.upvalues;
        for (int i = 0; i < this.nups; i++) {
            if (up[i].name.eq_b(name)) {
                return i;
            }
        }
        return -1;
    }

    int newupvalue(LuaString name, LexState.expdesc v) {
        checklimit(this.nups + 1, 255, "upvalues");
        if (this.f.upvalues == null || this.nups + 1 > this.f.upvalues.length) {
            this.f.upvalues = realloc(this.f.upvalues, this.nups > 0 ? this.nups * 2 : 1);
        }
        this.f.upvalues[this.nups] = new Upvaldesc(name, v.k == 7, v.u.info);
        short s = this.nups;
        this.nups = (short) (s + 1);
        return s;
    }

    int searchvar(LuaString n) {
        for (int i = this.nactvar - 1; i >= 0; i--) {
            if (n.eq_b(getlocvar(i).varname)) {
                return i;
            }
        }
        return -1;
    }

    void markupval(int level) {
        BlockCnt bl = this.bl;
        while (bl.nactvar > level) {
            bl = bl.previous;
        }
        bl.upval = true;
    }

    static int singlevaraux(FuncState fs, LuaString n, LexState.expdesc var, int base) {
        if (fs == null) {
            return 0;
        }
        int v = fs.searchvar(n);
        if (v >= 0) {
            var.init(7, v);
            if (base == 0) {
                fs.markupval(v);
            }
            return 7;
        }
        int idx = fs.searchupvalue(n);
        if (idx < 0) {
            if (singlevaraux(fs.prev, n, var, 0) == 0) {
                return 0;
            }
            idx = fs.newupvalue(n, var);
        }
        var.init(8, idx);
        return 8;
    }

    void movegotosout(BlockCnt bl) {
        int i = bl.firstgoto;
        LexState.Labeldesc[] gl = this.ls.dyd.gt;
        while (i < this.ls.dyd.n_gt) {
            LexState.Labeldesc gt = gl[i];
            if (gt.nactvar > bl.nactvar) {
                if (bl.upval) {
                    patchclose(gt.pc, bl.nactvar);
                }
                gt.nactvar = bl.nactvar;
            }
            if (!this.ls.findlabel(i)) {
                i++;
            }
        }
    }

    void enterblock(BlockCnt bl, boolean isloop) {
        bl.isloop = isloop;
        bl.nactvar = this.nactvar;
        bl.firstlabel = (short) this.ls.dyd.n_label;
        bl.firstgoto = (short) this.ls.dyd.n_gt;
        bl.upval = false;
        bl.previous = this.bl;
        this.bl = bl;
        _assert(this.freereg == this.nactvar);
    }

    void leaveblock() {
        BlockCnt bl = this.bl;
        if (bl.previous != null && bl.upval) {
            int j = jump();
            patchclose(j, bl.nactvar);
            patchtohere(j);
        }
        if (bl.isloop) {
            this.ls.breaklabel();
        }
        this.bl = bl.previous;
        removevars(bl.nactvar);
        _assert(bl.nactvar == this.nactvar);
        this.freereg = this.nactvar;
        this.ls.dyd.n_label = bl.firstlabel;
        if (bl.previous != null) {
            movegotosout(bl);
        } else if (bl.firstgoto < this.ls.dyd.n_gt) {
            this.ls.undefgoto(this.ls.dyd.gt[bl.firstgoto], this, bl);
        }
    }

    void closelistfield(LexState.ConsControl cc) {
        if (cc.v.k != 0) {
            exp2nextreg(cc.v);
            cc.v.k = 0;
            if (cc.tostore == 50) {
                setlist(cc.t.u.info, cc.na, cc.tostore);
                cc.tostore = 0;
            }
        }
    }

    boolean hasmultret(int k) {
        return k == 12 || k == 13;
    }

    void lastlistfield(LexState.ConsControl cc) {
        if (cc.tostore != 0) {
            if (hasmultret(cc.v.k)) {
                setmultret(cc.v);
                setlist(cc.t.u.info, cc.na, -1);
                cc.na--;
            } else {
                if (cc.v.k != 0) {
                    exp2nextreg(cc.v);
                }
                setlist(cc.t.u.info, cc.na, cc.tostore);
            }
        }
    }

    void nil(int from, int n) {
        int l = (from + n) - 1;
        if (this.pc > this.lasttarget && this.pc > 0) {
            int previous_code = this.f.code[this.pc - 1];
            if (GET_OPCODE(previous_code) == 4) {
                int pfrom = GETARG_A(previous_code);
                int pl = pfrom + GETARG_B(previous_code);
                if ((pfrom <= from && from <= pl + 1) || (from <= pfrom && pfrom <= l + 1)) {
                    if (pfrom < from) {
                        from = pfrom;
                    }
                    if (pl > l) {
                        l = pl;
                    }
                    InstructionPtr previous = new InstructionPtr(this.f.code, this.pc - 1);
                    SETARG_A(previous, from);
                    SETARG_B(previous, l - from);
                    return;
                }
            }
        }
        codeABC(4, from, n - 1, 0);
    }

    int jump() {
        int jpc = this.jpc.i;
        this.jpc.i = -1;
        IntPtr j = new IntPtr(codeAsBx(23, 0, -1));
        concat(j, jpc);
        return j.i;
    }

    void ret(int first, int nret) {
        codeABC(31, first, nret + 1, 0);
    }

    int condjump(int op, int A, int B, int C) {
        codeABC(op, A, B, C);
        return jump();
    }

    void fixjump(int pc, int dest) {
        InstructionPtr jmp = new InstructionPtr(this.f.code, pc);
        int offset = dest - (pc + 1);
        _assert(dest != -1);
        if (Math.abs(offset) > 131071) {
            this.ls.syntaxerror("control structure too long");
        }
        SETARG_sBx(jmp, offset);
    }

    int getlabel() {
        this.lasttarget = this.pc;
        return this.pc;
    }

    int getjump(int pc) {
        int offset = GETARG_sBx(this.f.code[pc]);
        if (offset == -1) {
            return -1;
        }
        return pc + 1 + offset;
    }

    InstructionPtr getjumpcontrol(int pc) {
        InstructionPtr pi = new InstructionPtr(this.f.code, pc);
        if (pc >= 1 && testTMode(GET_OPCODE(pi.code[pi.idx - 1]))) {
            return new InstructionPtr(pi.code, pi.idx - 1);
        }
        return pi;
    }

    boolean need_value(int list) {
        while (list != -1) {
            int i = getjumpcontrol(list).get();
            if (GET_OPCODE(i) == 28) {
                list = getjump(list);
            } else {
                return true;
            }
        }
        return false;
    }

    boolean patchtestreg(int node, int reg) {
        InstructionPtr i = getjumpcontrol(node);
        if (GET_OPCODE(i.get()) != 28) {
            return false;
        }
        if (reg != 255 && reg != GETARG_B(i.get())) {
            SETARG_A(i, reg);
        } else {
            i.set(CREATE_ABC(27, GETARG_B(i.get()), 0, Lua.GETARG_C(i.get())));
        }
        return true;
    }

    void removevalues(int list) {
        while (list != -1) {
            patchtestreg(list, 255);
            list = getjump(list);
        }
    }

    void patchlistaux(int list, int vtarget, int reg, int dtarget) {
        while (list != -1) {
            int next = getjump(list);
            if (patchtestreg(list, reg)) {
                fixjump(list, vtarget);
            } else {
                fixjump(list, dtarget);
            }
            list = next;
        }
    }

    void dischargejpc() {
        patchlistaux(this.jpc.i, this.pc, 255, this.pc);
        this.jpc.i = -1;
    }

    void patchlist(int list, int target) {
        if (target == this.pc) {
            patchtohere(list);
        } else {
            _assert(target < this.pc);
            patchlistaux(list, target, 255, target);
        }
    }

    void patchclose(int list, int level) {
        int level2 = level + 1;
        while (list != -1) {
            int next = getjump(list);
            _assert(GET_OPCODE(this.f.code[list]) == 23 && (GETARG_A(this.f.code[list]) == 0 || GETARG_A(this.f.code[list]) >= level2));
            SETARG_A(this.f.code, list, level2);
            list = next;
        }
    }

    void patchtohere(int list) {
        getlabel();
        concat(this.jpc, list);
    }

    void concat(IntPtr l1, int l2) {
        if (l2 != -1) {
            if (l1.i == -1) {
                l1.i = l2;
                return;
            }
            int list = l1.i;
            while (true) {
                int next = getjump(list);
                if (next != -1) {
                    list = next;
                } else {
                    fixjump(list, l2);
                    return;
                }
            }
        }
    }

    void checkstack(int n) {
        int newstack = this.freereg + n;
        if (newstack > this.f.maxstacksize) {
            if (newstack >= 250) {
                this.ls.syntaxerror("function or expression needs too many registers");
            }
            this.f.maxstacksize = newstack;
        }
    }

    void reserveregs(int n) {
        checkstack(n);
        this.freereg = (short) (this.freereg + n);
    }

    void freereg(int reg) {
        if (!ISK(reg) && reg >= this.nactvar) {
            this.freereg = (short) (this.freereg - 1);
            _assert(reg == this.freereg);
        }
    }

    void freeexp(LexState.expdesc e) {
        if (e.k == 6) {
            freereg(e.u.info);
        }
    }

    int addk(LuaValue v) {
        Hashtable<LuaValue, Integer> h = this.h;
        if (h == null) {
            h = new Hashtable<>();
            this.h = h;
        } else {
            Integer i = h.get(v);
            if (i != null) {
                return i.intValue();
            }
        }
        int idx = this.nk;
        h.put(v, Integer.valueOf(idx));
        Prototype f = this.f;
        if (f.k == null || this.nk + 1 >= f.k.length) {
            f.k = realloc(f.k, (this.nk * 2) + 1);
        }
        LuaValue[] luaValueArr = f.k;
        int i2 = this.nk;
        this.nk = i2 + 1;
        luaValueArr[i2] = v;
        return idx;
    }

    int stringK(LuaString s) {
        return addk(s);
    }

    int numberK(LuaValue r) {
        return addk(r);
    }

    int boolK(boolean b) {
        return addk(b ? LuaValue.TRUE : LuaValue.FALSE);
    }

    int nilK() {
        return addk(LuaValue.NIL);
    }

    void setreturns(LexState.expdesc e, int nresults) {
        if (e.k == 12) {
            SETARG_C(getcodePtr(e), nresults + 1);
        } else if (e.k == 13) {
            SETARG_B(getcodePtr(e), nresults + 1);
            SETARG_A(getcodePtr(e), this.freereg);
            reserveregs(1);
        }
    }

    void setoneret(LexState.expdesc e) {
        if (e.k == 12) {
            e.k = 6;
            e.u.info = GETARG_A(getcode(e));
        } else if (e.k == 13) {
            SETARG_B(getcodePtr(e), 2);
            e.k = 11;
        }
    }

    void dischargevars(LexState.expdesc e) {
        int op;
        switch (e.k) {
            case 7:
                e.k = 6;
                break;
            case 8:
                e.u.info = codeABC(5, 0, e.u.info, 0);
                e.k = 11;
                break;
            case 9:
                freereg(e.u.ind_idx);
                if (e.u.ind_vt == 7) {
                    freereg(e.u.ind_t);
                    op = 7;
                } else {
                    op = 6;
                }
                e.u.info = codeABC(op, 0, e.u.ind_t, e.u.ind_idx);
                e.k = 11;
                break;
            case 12:
            case 13:
                setoneret(e);
                break;
        }
    }

    int code_label(int A, int b, int jump) {
        getlabel();
        return codeABC(3, A, b, jump);
    }

    void discharge2reg(LexState.expdesc e, int reg) {
        dischargevars(e);
        switch (e.k) {
            case 1:
                nil(reg, 1);
                break;
            case 2:
            case 3:
                codeABC(3, reg, e.k != 2 ? 0 : 1, 0);
                break;
            case 4:
                codeK(reg, e.u.info);
                break;
            case 5:
                codeK(reg, numberK(e.u.nval()));
                break;
            case 6:
                if (reg != e.u.info) {
                    codeABC(0, reg, e.u.info, 0);
                }
                break;
            case 7:
            case 8:
            case 9:
            case 10:
            default:
                _assert(e.k == 0 || e.k == 10);
                return;
            case 11:
                InstructionPtr pc = getcodePtr(e);
                SETARG_A(pc, reg);
                break;
        }
        e.u.info = reg;
        e.k = 6;
    }

    void discharge2anyreg(LexState.expdesc e) {
        if (e.k != 6) {
            reserveregs(1);
            discharge2reg(e, this.freereg - 1);
        }
    }

    void exp2reg(LexState.expdesc e, int reg) {
        discharge2reg(e, reg);
        if (e.k == 10) {
            concat(e.t, e.u.info);
        }
        if (e.hasjumps()) {
            int p_f = -1;
            int p_t = -1;
            if (need_value(e.t.i) || need_value(e.f.i)) {
                int fj = e.k == 10 ? -1 : jump();
                p_f = code_label(reg, 0, 1);
                p_t = code_label(reg, 1, 0);
                patchtohere(fj);
            }
            int _final = getlabel();
            patchlistaux(e.f.i, _final, reg, p_f);
            patchlistaux(e.t.i, _final, reg, p_t);
        }
        IntPtr intPtr = e.f;
        e.t.i = -1;
        intPtr.i = -1;
        e.u.info = reg;
        e.k = 6;
    }

    void exp2nextreg(LexState.expdesc e) {
        dischargevars(e);
        freeexp(e);
        reserveregs(1);
        exp2reg(e, this.freereg - 1);
    }

    int exp2anyreg(LexState.expdesc e) {
        dischargevars(e);
        if (e.k == 6) {
            if (!e.hasjumps()) {
                return e.u.info;
            }
            if (e.u.info >= this.nactvar) {
                exp2reg(e, e.u.info);
                return e.u.info;
            }
        }
        exp2nextreg(e);
        return e.u.info;
    }

    void exp2anyregup(LexState.expdesc e) {
        if (e.k != 8 || e.hasjumps()) {
            exp2anyreg(e);
        }
    }

    void exp2val(LexState.expdesc e) {
        if (e.hasjumps()) {
            exp2anyreg(e);
        } else {
            dischargevars(e);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    int exp2RK(LexState.expdesc e) {
        int iBoolK;
        exp2val(e);
        switch (e.k) {
            case 1:
            case 2:
            case 3:
                if (this.nk <= 255) {
                    LexState.expdesc.U u = e.u;
                    if (e.k == 1) {
                        iBoolK = nilK();
                    } else {
                        iBoolK = boolK(e.k == 2);
                    }
                    u.info = iBoolK;
                    e.k = 4;
                    return RKASK(e.u.info);
                }
                return exp2anyreg(e);
            case 4:
                break;
            case 5:
                e.u.info = numberK(e.u.nval());
                e.k = 4;
                break;
            default:
                return exp2anyreg(e);
        }
        if (e.u.info <= 255) {
            return RKASK(e.u.info);
        }
        return exp2anyreg(e);
    }

    void storevar(LexState.expdesc var, LexState.expdesc ex) {
        switch (var.k) {
            case 7:
                freeexp(ex);
                exp2reg(ex, var.u.info);
                return;
            case 8:
                int e = exp2anyreg(ex);
                codeABC(9, e, var.u.info, 0);
                break;
            case 9:
                int op = var.u.ind_vt == 7 ? 10 : 8;
                int e2 = exp2RK(ex);
                codeABC(op, var.u.ind_t, var.u.ind_idx, e2);
                break;
            default:
                _assert(false);
                break;
        }
        freeexp(ex);
    }

    void self(LexState.expdesc e, LexState.expdesc key) {
        exp2anyreg(e);
        freeexp(e);
        int func = this.freereg;
        reserveregs(2);
        codeABC(12, func, e.u.info, exp2RK(key));
        freeexp(key);
        e.u.info = func;
        e.k = 6;
    }

    void invertjump(LexState.expdesc e) {
        InstructionPtr pc = getjumpcontrol(e.u.info);
        _assert((!testTMode(GET_OPCODE(pc.get())) || GET_OPCODE(pc.get()) == 28 || Lua.GET_OPCODE(pc.get()) == 27) ? false : true);
        int a = GETARG_A(pc.get());
        int nota = a != 0 ? 0 : 1;
        SETARG_A(pc, nota);
    }

    int jumponcond(LexState.expdesc e, int cond) {
        if (e.k == 11) {
            int ie = getcode(e);
            if (GET_OPCODE(ie) == 20) {
                this.pc--;
                return condjump(27, GETARG_B(ie), 0, cond != 0 ? 0 : 1);
            }
        }
        discharge2anyreg(e);
        freeexp(e);
        return condjump(28, 255, e.u.info, cond);
    }

    void goiftrue(LexState.expdesc e) {
        int pc;
        dischargevars(e);
        switch (e.k) {
            case 2:
            case 4:
            case 5:
                pc = -1;
                break;
            case 3:
            case 6:
            case 7:
            case 8:
            case 9:
            default:
                pc = jumponcond(e, 0);
                break;
            case 10:
                invertjump(e);
                pc = e.u.info;
                break;
        }
        concat(e.f, pc);
        patchtohere(e.t.i);
        e.t.i = -1;
    }

    void goiffalse(LexState.expdesc e) {
        int pc;
        dischargevars(e);
        switch (e.k) {
            case 1:
            case 3:
                pc = -1;
                break;
            case 10:
                pc = e.u.info;
                break;
            default:
                pc = jumponcond(e, 1);
                break;
        }
        concat(e.t, pc);
        patchtohere(e.f.i);
        e.f.i = -1;
    }

    void codenot(LexState.expdesc e) {
        dischargevars(e);
        switch (e.k) {
            case 1:
            case 3:
                e.k = 2;
                break;
            case 2:
            case 4:
            case 5:
                e.k = 3;
                break;
            case 6:
            case 11:
                discharge2anyreg(e);
                freeexp(e);
                e.u.info = codeABC(20, 0, e.u.info, 0);
                e.k = 11;
                break;
            case 7:
            case 8:
            case 9:
            default:
                _assert(false);
                break;
            case 10:
                invertjump(e);
                break;
        }
        int temp = e.f.i;
        e.f.i = e.t.i;
        e.t.i = temp;
        removevalues(e.f.i);
        removevalues(e.t.i);
    }

    static boolean vkisinreg(int k) {
        return k == 6 || k == 7;
    }

    void indexed(LexState.expdesc t, LexState.expdesc k) {
        t.u.ind_t = (short) t.u.info;
        t.u.ind_idx = (short) exp2RK(k);
        LuaC._assert(t.k == 8 || vkisinreg(t.k));
        t.u.ind_vt = (short) (t.k == 8 ? 8 : 7);
        t.k = 9;
    }

    boolean constfolding(int op, LexState.expdesc e1, LexState.expdesc e2) {
        LuaValue r;
        if (!e1.isnumeral() || !e2.isnumeral()) {
            return false;
        }
        if ((op == 16 || op == 40 || op == 17) && e2.u.nval().eq_b(LuaValue.ZERO)) {
            return false;
        }
        LuaValue v1 = e1.u.nval();
        LuaValue v2 = e2.u.nval();
        switch (op) {
            case 13:
                r = v1.add(v2);
                break;
            case 14:
                r = v1.sub(v2);
                break;
            case 15:
                r = v1.mul(v2);
                break;
            case 16:
                r = v1.div(v2);
                break;
            case 17:
                r = v1.mod(v2);
                break;
            case 18:
                r = v1.pow(v2);
                break;
            case 19:
                r = v1.neg();
                break;
            case 20:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            default:
                _assert(false);
                r = null;
                break;
            case 21:
                return false;
            case 40:
                r = v1.idiv(v2);
                break;
            case 41:
                if (!v1.islongnumber()) {
                    return false;
                }
                r = v1.bnot();
                break;
            case 42:
                if (!v1.islongnumber() || !v2.islongnumber()) {
                    return false;
                }
                r = v1.band(v2);
                break;
            case 43:
                if (!v1.islongnumber() || !v2.islongnumber()) {
                    return false;
                }
                r = v1.bor(v2);
                break;
            case 44:
                if (!v1.islongnumber() || !v2.islongnumber()) {
                    return false;
                }
                r = v1.bxor(v2);
                break;
            case 45:
                if (!v1.islongnumber() || !v2.islongnumber()) {
                    return false;
                }
                r = v1.shl(v2);
                break;
            case 46:
                if (!v1.islongnumber() || !v2.islongnumber()) {
                    return false;
                }
                r = v1.shr(v2);
                break;
        }
        if (r == null || Double.isNaN(r.todouble())) {
            return false;
        }
        e1.u.setNval(r);
        return true;
    }

    void codearith(int op, LexState.expdesc e1, LexState.expdesc e2, int line) {
        if (!constfolding(op, e1, e2)) {
            int o2 = (op == 19 || op == 21 || op == 41) ? 0 : exp2RK(e2);
            int o1 = exp2RK(e1);
            if (o1 > o2) {
                freeexp(e1);
                freeexp(e2);
            } else {
                freeexp(e2);
                freeexp(e1);
            }
            e1.u.info = codeABC(op, 0, o1, o2);
            e1.k = 11;
            fixline(line);
        }
    }

    void codecomp(int op, int cond, LexState.expdesc e1, LexState.expdesc e2) {
        int o1 = exp2RK(e1);
        int o2 = exp2RK(e2);
        freeexp(e2);
        freeexp(e1);
        if (cond == 0 && op != 24) {
            o1 = o2;
            o2 = o1;
            cond = 1;
        }
        e1.u.info = condjump(op, cond, o1, o2);
        e1.k = 10;
    }

    void prefix(int op, LexState.expdesc e, int line) {
        LexState.expdesc e2 = new LexState.expdesc();
        e2.init(5, 0);
        switch (op) {
            case 0:
                if (e.isnumeral()) {
                    e.u.setNval(e.u.nval().neg());
                } else {
                    exp2anyreg(e);
                    codearith(19, e, e2, line);
                }
                break;
            case 1:
                codenot(e);
                break;
            case 2:
                exp2anyreg(e);
                codearith(21, e, e2, line);
                break;
            case 3:
            default:
                _assert(false);
                break;
            case 4:
                if (e.isnumeral()) {
                    e.u.setNval(e.u.nval().bnot());
                } else {
                    exp2anyreg(e);
                    codearith(41, e, e2, line);
                }
                break;
        }
    }

    void infix(int op, LexState.expdesc v) {
        switch (op) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
                if (!v.isnumeral()) {
                    exp2RK(v);
                }
                break;
            case 6:
                exp2nextreg(v);
                break;
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            default:
                exp2RK(v);
                break;
            case 13:
                goiftrue(v);
                break;
            case 14:
                goiffalse(v);
                break;
        }
    }

    void posfix(int op, LexState.expdesc e1, LexState.expdesc e2, int line) {
        switch (op) {
            case 0:
                codearith(13, e1, e2, line);
                break;
            case 1:
                codearith(14, e1, e2, line);
                break;
            case 2:
                codearith(15, e1, e2, line);
                break;
            case 3:
                codearith(16, e1, e2, line);
                break;
            case 4:
                codearith(17, e1, e2, line);
                break;
            case 5:
                codearith(18, e1, e2, line);
                break;
            case 6:
                exp2val(e2);
                if (e2.k == 11 && GET_OPCODE(getcode(e2)) == 22) {
                    _assert(e1.u.info == GETARG_B(getcode(e2)) + (-1));
                    freeexp(e1);
                    SETARG_B(getcodePtr(e2), e1.u.info);
                    e1.k = 11;
                    e1.u.info = e2.u.info;
                } else {
                    exp2nextreg(e2);
                    codearith(22, e1, e2, line);
                }
                break;
            case 7:
                codecomp(24, 0, e1, e2);
                break;
            case 8:
                codecomp(24, 1, e1, e2);
                break;
            case 9:
                codecomp(25, 1, e1, e2);
                break;
            case 10:
                codecomp(26, 1, e1, e2);
                break;
            case 11:
                codecomp(25, 0, e1, e2);
                break;
            case 12:
                codecomp(26, 0, e1, e2);
                break;
            case 13:
                _assert(e1.t.i == -1);
                dischargevars(e2);
                concat(e2.f, e1.f.i);
                e1.setvalue(e2);
                break;
            case 14:
                _assert(e1.f.i == -1);
                dischargevars(e2);
                concat(e2.t, e1.t.i);
                e1.setvalue(e2);
                break;
            case 15:
                codearith(40, e1, e2, line);
                break;
            case 16:
                codearith(42, e1, e2, line);
                break;
            case 17:
                codearith(43, e1, e2, line);
                break;
            case 18:
                codearith(44, e1, e2, line);
                break;
            case 19:
                codearith(45, e1, e2, line);
                break;
            case 20:
                codearith(46, e1, e2, line);
                break;
            default:
                _assert(false);
                break;
        }
    }

    void fixline(int line) {
        this.f.lineinfo[this.pc - 1] = line;
    }

    int code(int instruction, int line) {
        Prototype f = this.f;
        dischargejpc();
        if (f.code == null || this.pc + 1 > f.code.length) {
            f.code = LuaC.realloc(f.code, (this.pc * 2) + 1);
        }
        f.code[this.pc] = instruction;
        if (f.lineinfo == null || this.pc + 1 > f.lineinfo.length) {
            f.lineinfo = LuaC.realloc(f.lineinfo, (this.pc * 2) + 1);
        }
        f.lineinfo[this.pc] = line;
        int i = this.pc;
        this.pc = i + 1;
        return i;
    }

    int codeABC(int o, int a, int b, int c) {
        _assert(getOpMode(o) == 0);
        _assert(getBMode(o) != 0 || b == 0);
        _assert(getCMode(o) != 0 || c == 0);
        return code(CREATE_ABC(o, a, b, c), this.ls.lastline);
    }

    int codeABx(int o, int a, int bc) {
        _assert(getOpMode(o) == 1 || getOpMode(o) == 2);
        _assert(getCMode(o) == 0);
        _assert(bc >= 0 && bc <= 262143);
        return code(CREATE_ABx(o, a, bc), this.ls.lastline);
    }

    int codeextraarg(int a) {
        _assert(a <= 67108863);
        return code(CREATE_Ax(39, a), this.ls.lastline);
    }

    int codeK(int reg, int k) {
        if (k <= 262143) {
            return codeABx(1, reg, k);
        }
        int iCodeABx = codeABx(2, reg, 0);
        codeextraarg(k);
        return iCodeABx;
    }

    void setlist(int base, int nelems, int tostore) {
        int c = ((nelems - 1) / 50) + 1;
        int b = tostore == -1 ? 0 : tostore;
        if (c <= 511) {
            codeABC(36, base, b, c);
        } else {
            codeABC(36, base, b, 0);
            code(c, this.ls.lastline);
        }
        this.freereg = (short) (base + 1);
    }
}
