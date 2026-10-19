package luaj.compiler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import luaj.LoadState;
import luaj.LocVars;
import luaj.LuaString;
import luaj.LuaValue;
import luaj.Print;
import luaj.Prototype;
import luaj.Upvaldesc;

/* JADX INFO: loaded from: classes.dex */
public class DumpState {
    public static final boolean ALLOW_INTEGER_CASTING = false;
    public static final int NUMBER_FORMAT_DEFAULT = 0;
    public static final int NUMBER_FORMAT_FLOATS_OR_DOUBLES = 0;
    public static final int NUMBER_FORMAT_INTS_ONLY = 1;
    public static final int NUMBER_FORMAT_NUM_PATCH_INT32 = 4;
    public static final int SIZEOF_INSTRUCTION = 4;
    public static final int SIZEOF_INT = 4;
    public static final int SIZEOF_SIZET = 4;
    boolean fix;
    boolean strip;
    DataOutputStream writer;
    private boolean IS_LITTLE_ENDIAN = true;
    private int NUMBER_FORMAT = 0;
    private int SIZEOF_LUA_NUMBER = 8;
    int status = 0;

    public DumpState(OutputStream w, boolean strip, boolean fix) {
        this.writer = new DataOutputStream(w);
        this.strip = strip;
        this.fix = fix;
    }

    void dumpBlock(byte[] b, int size) throws IOException {
        this.writer.write(b, 0, size);
    }

    void dumpChar(int b) throws IOException {
        this.writer.write(b);
    }

    void dumpInt(int x) throws IOException {
        DataOutputStream writer = this.writer;
        if (this.IS_LITTLE_ENDIAN) {
            x = Integer.reverseBytes(x);
        }
        writer.writeInt(x);
    }

    void dumpString(LuaString s) throws IOException {
        if (s == null) {
            s = LuaValue.valueOf("");
        }
        int len = s.len().toint();
        dumpInt(len + 1);
        s.write(this.writer, 0, len);
        this.writer.write(0);
    }

    void dumpDouble(double d) throws IOException {
        long l = Double.doubleToLongBits(d);
        if (this.IS_LITTLE_ENDIAN) {
            l = Long.reverseBytes(l);
        }
        this.writer.writeLong(l);
    }

    void dumpCode(Prototype f) throws IOException {
        int[] code = f.code;
        int n = code.length;
        dumpInt(n);
        int[] fixed = this.fix ? new int[n] : code;
        if (this.fix) {
            Print.fix(f, fixed);
        }
        for (int pc = 0; pc < n; pc++) {
            dumpInt(fixed[pc]);
        }
    }

    void dumpConstants(Prototype f) throws IOException {
        DataOutputStream writer = this.writer;
        LuaValue[] k = f.k;
        int n = k.length;
        dumpInt(n);
        for (LuaValue o : k) {
            switch (o.type()) {
                case 0:
                    writer.write(0);
                    break;
                case 1:
                    writer.write(1);
                    dumpChar(o.toboolean() ? 1 : 0);
                    break;
                case 2:
                default:
                    throw new IllegalArgumentException("bad type for " + o);
                case 3:
                    switch (this.NUMBER_FORMAT) {
                        case 0:
                            writer.write(3);
                            dumpDouble(o.todouble());
                            break;
                        case 1:
                            if (!o.isint()) {
                                throw new IllegalArgumentException("not an integer: " + o);
                            }
                            writer.write(3);
                            dumpInt(o.toint());
                            break;
                        case 2:
                        case 3:
                        default:
                            throw new IllegalArgumentException("number format not supported: " + this.NUMBER_FORMAT);
                        case 4:
                            if (o.isint()) {
                                writer.write(-2);
                                dumpInt(o.toint());
                            } else {
                                writer.write(3);
                                dumpDouble(o.todouble());
                            }
                            break;
                    }
                    break;
                case 4:
                    writer.write(4);
                    dumpString((LuaString) o);
                    break;
            }
        }
        int n2 = f.p.length;
        dumpInt(n2);
        for (int i = 0; i < n2; i++) {
            dumpFunction(f.p[i], f.source);
        }
    }

    void dumpUpvalues(Prototype f) throws IOException {
        DataOutputStream writer = this.writer;
        Upvaldesc[] upvalues = f.upvalues;
        int n = upvalues.length;
        dumpInt(n);
        for (Upvaldesc u : upvalues) {
            writer.writeByte(u.instack ? 1 : 0);
            writer.writeByte(u.idx);
        }
    }

    void dumpDebug(Prototype f, LuaString psource) throws IOException {
        if (this.strip || (psource != null && psource.eq_b(f.source))) {
            dumpInt(0);
        } else {
            dumpString(f.source);
        }
        int[] lineinfo = f.lineinfo;
        int n = this.strip ? 0 : lineinfo.length;
        dumpInt(n);
        for (int i = 0; i < n; i++) {
            dumpInt(lineinfo[i]);
        }
        LocVars[] locvars = f.locvars;
        int n2 = this.strip ? 0 : locvars.length;
        dumpInt(n2);
        for (int i2 = 0; i2 < n2; i2++) {
            LocVars lvi = locvars[i2];
            dumpString(lvi.varname);
            dumpInt(lvi.startpc);
            dumpInt(lvi.endpc);
        }
        Upvaldesc[] upvalues = f.upvalues;
        int n3 = this.strip ? 0 : upvalues.length;
        dumpInt(n3);
        for (int i3 = 0; i3 < n3; i3++) {
            dumpString(upvalues[i3].name);
        }
    }

    void dumpFunction(Prototype f, LuaString psource) throws IOException {
        dumpInt(f.linedefined);
        dumpInt(f.lastlinedefined);
        dumpChar(f.numparams);
        dumpChar(f.is_vararg);
        dumpChar(f.maxstacksize);
        dumpCode(f);
        dumpConstants(f);
        dumpUpvalues(f);
        dumpDebug(f, psource);
    }

    void dumpHeader() throws IOException {
        DataOutputStream writer = this.writer;
        writer.write(LoadState.LUA_SIGNATURE);
        writer.write(82);
        writer.write(0);
        writer.write(this.IS_LITTLE_ENDIAN ? 1 : 0);
        writer.write(4);
        writer.write(4);
        writer.write(4);
        writer.write(this.SIZEOF_LUA_NUMBER);
        writer.write(this.NUMBER_FORMAT);
        writer.write(LoadState.LUAC_TAIL);
    }

    public static int dump(Prototype f, OutputStream w, boolean strip, boolean fix) throws IOException {
        DumpState D = new DumpState(w, strip, fix);
        D.dumpHeader();
        D.dumpFunction(f, null);
        return D.status;
    }

    public static int dump(Prototype f, OutputStream w, boolean stripDebug, int numberFormat, boolean littleendian) throws IOException {
        switch (numberFormat) {
            case 0:
            case 1:
            case 4:
                DumpState D = new DumpState(w, stripDebug, false);
                D.IS_LITTLE_ENDIAN = littleendian;
                D.NUMBER_FORMAT = numberFormat;
                D.SIZEOF_LUA_NUMBER = numberFormat == 1 ? 4 : 8;
                D.dumpHeader();
                D.dumpFunction(f, null);
                return D.status;
            case 2:
            case 3:
            default:
                throw new IllegalArgumentException("number format not supported: " + numberFormat);
        }
    }
}
