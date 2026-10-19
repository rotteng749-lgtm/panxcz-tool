package luaj.lib;

import android.ext.Log;
import android.ext.Re;
import android.ext.Tools;
import android.os.Build;
import android.system.Os;
import android.system.OsConstants;
import catch_.me_.if_.you_.can_.R;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import luaj.Globals;
import luaj.Lua;
import luaj.LuaClosure;
import luaj.LuaError;
import luaj.LuaFunction;
import luaj.LuaString;
import luaj.LuaTable;
import luaj.LuaThread;
import luaj.LuaValue;
import luaj.Varargs;

/* JADX INFO: loaded from: classes.dex */
public class BaseLib extends TwoArgFunction implements ResourceFinder {
    private DebugLoad debugLoad = null;
    byte[] debugLoadBuf = null;
    Globals globals;

    @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue modname, LuaValue env) {
        Globals globals = env.checkglobals();
        this.globals = globals;
        globals.finder = this;
        globals.baselib = this;
        if (globals.fullVersion) {
            env.set("_G", env);
            env.set("_VERSION", Lua._VERSION);
            env.set("assert", new _assert());
            env.set("assert2", new assert2());
            env.set("collectgarbage", new collectgarbage());
            env.set("dofile", new dofile());
            env.set("error", new error());
            env.set("getmetatable", new getmetatable());
            env.set("load", new load());
            env.set("loadfile", new loadfile());
            env.set("pcall", new pcall());
            env.set("print", new print(this));
        }
        env.set("rawequal", new rawequal());
        env.set("rawget", new rawget());
        env.set("rawlen", new rawlen());
        env.set("rawset", new rawset());
        env.set("select", new select());
        if (globals.fullVersion) {
            env.set("setmetatable", new setmetatable());
        }
        env.set("tonumber", new tonumber());
        env.set("tostring", new tostring());
        env.set("type", new type());
        if (globals.fullVersion) {
            env.set("xpcall", new xpcall());
        }
        next next2 = new next();
        env.set("next", next2);
        env.set("pairs", new pairs(next2));
        env.set("ipairs", new ipairs());
        return env;
    }

    @Override // luaj.lib.ResourceFinder
    public InputStream findResource(String filename) {
        File f = this.globals.newFile(filename);
        try {
            return new CountingBufferedInputStream(new FileInputStream(f));
        } catch (IOException e) {
            return null;
        }
    }

    static final class _assert extends VarArgFunction {
        _assert() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            if (args.narg() == 0) {
                argerror(1, "assert", "value expected");
            }
            if (!args.toboolean(1)) {
                if (args.narg() == 1) {
                    throw new LuaError("assertion failed!");
                }
                throw new LuaError(args.arg(2));
            }
            return args;
        }
    }

    static final class assert2 extends VarArgFunction {
        assert2() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            String src;
            LuaValue v1 = args.arg(1);
            LuaValue v2 = args.arg(2);
            if (!v1.eq_b(v2)) {
                if (!v1.tostring().equals(v2.tostring())) {
                    src = v1 + " ~= " + v2;
                } else if (!v1.typename().equals(v2.typename())) {
                    src = v1 + " [" + v1.typename() + "] ~= " + v2 + " [" + v2.typename() + "]";
                } else {
                    src = v1 + " {" + v1.getClass() + "} ~= " + v2 + " {" + v2.getClass() + "}";
                }
                error(String.valueOf(src) + ": " + args.optjstring(3, "assertion failed!"));
            }
            return args;
        }
    }

    static final class collectgarbage extends VarArgFunction {
        collectgarbage() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            String s = args.optjstring(1, "collect");
            if ("collect".equals(s)) {
                System.gc();
                return ZERO;
            }
            if ("count".equals(s)) {
                Runtime rt = Runtime.getRuntime();
                long used = rt.totalMemory() - rt.freeMemory();
                return varargsOf(valueOf(used / 1024.0d), valueOf(used % 1024));
            }
            if ("step".equals(s)) {
                System.gc();
                return LuaValue.TRUE;
            }
            argerror(1, "collectgarbage", "invalid option '" + s + "'");
            return NIL;
        }
    }

    final class dofile extends VarArgFunction {
        dofile() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaValue ld = args.arg1();
            if (!ld.isstring() && !ld.isnil()) {
                argerror(1, "dofile", "string expected, got " + ld.typename());
            }
            String filename = ld.checkjstring();
            Varargs v = BaseLib.this.loadFile(filename, "bt", BaseLib.this.globals);
            return v.isnil(1) ? error(v.tojstring(2)) : v.arg1().invoke();
        }
    }

    static final class error extends TwoArgFunction {
        error() {
        }

        @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            if (arg1.isnil()) {
                throw new LuaError(NIL);
            }
            if (!arg1.isstring() || arg2.optint(1) == 0) {
                throw new LuaError(arg1);
            }
            throw new LuaError(arg1.tojstring(), arg2.optint(1));
        }
    }

    static final class getmetatable extends LibFunction {
        getmetatable() {
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call() {
            return argerror(1, "value expected");
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            LuaValue mt = arg.getmetatable();
            return mt != null ? mt.rawget(METATABLE).optvalue(mt) : NIL;
        }
    }

    final class load extends VarArgFunction {
        load() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaValue ld = args.arg1();
            if (!ld.isstring() && !ld.isfunction()) {
                argerror(1, "load", "string or function expected, got " + ld.typename());
            }
            String source = args.optjstring(2, ld.isstring() ? ld.tojstring() : "=(load)");
            String mode = args.optjstring(3, "bt");
            LuaValue env = args.optvalue(4, BaseLib.this.globals);
            LuaString str = ld.isstring() ? ld.strvalue() : null;
            InputStream is = str != null ? str.toInputStream() : new StringInputStream(ld.checkfunction());
            if (BaseLib.this.debugLoad != null) {
                is = BaseLib.this.debugLoad(str, null, false, is);
            }
            return BaseLib.this.loadStream(is, source, mode, env);
        }
    }

    class DebugLoad {
        OutputStream data;
        File dataFile;
        DataOutputStream index;
        File indexFile;

        DebugLoad(String debugLoad) throws FileNotFoundException {
            this.dataFile = new File(String.valueOf(debugLoad) + ".load");
            this.data = new BufferedOutputStream(new FileOutputStream(this.dataFile));
            this.indexFile = new File(String.valueOf(debugLoad) + ".load.tmp");
            this.index = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.indexFile)));
        }

        int writeOct(byte[] header, int offset, int num) {
            String oct = Integer.toOctalString(num);
            int len = oct.length();
            int off = offset - len;
            int sum = 0;
            for (int j = 0; j < len; j++) {
                char ch = oct.charAt(j);
                sum += (ch & 255) - 48;
                header[off + j] = (byte) ch;
            }
            return sum;
        }

        void init(byte[] header, int offset, String init) {
            int s = init.length();
            for (int j = 0; j < s; j++) {
                header[offset + j] = (byte) init.charAt(j);
            }
        }

        void end() {
            try {
                this.data.close();
                this.index.close();
                int cnt = ((int) this.indexFile.length()) / 4;
                if (cnt > 0) {
                    Tools.showToast(String.valueOf(Re.s(R.string.debug_load)) + ": " + cnt);
                    InputStream data = new BufferedInputStream(new FileInputStream(this.dataFile));
                    DataInputStream index = new DataInputStream(new BufferedInputStream(new FileInputStream(this.indexFile)));
                    byte[] buf = BaseLib.this.debugLoadBuf;
                    if (buf == null) {
                        buf = new byte[8192];
                        BaseLib.this.debugLoadBuf = buf;
                    }
                    byte[] header = new byte[512];
                    init(header, 0, "load_0000000.lua");
                    init(header, 100, "0000666\u00000000000\u00000002004\u000000000000000\u000000000000000\u0000000000\u0000 0");
                    init(header, 257, "ustar00\u0000root");
                    init(header, 297, "sdcard_r");
                    int checksum = -64;
                    for (int j = 0; j < 512; j++) {
                        byte b = header[j];
                        if (b != 0) {
                            checksum += b & 255;
                        }
                    }
                    OutputStream tar = new BufferedOutputStream(new FileOutputStream(String.valueOf(this.dataFile.getAbsolutePath()) + ".tar"));
                    for (int i = 0; i < cnt; i++) {
                        int size = index.readInt();
                        int check = checksum;
                        String name = Integer.toString(i);
                        int len = name.length();
                        int off = 12 - len;
                        for (int j2 = 0; j2 < len; j2++) {
                            char ch = name.charAt(j2);
                            check += (ch & 255) - 48;
                            header[off + j2] = (byte) ch;
                        }
                        init(header, LuaError.MAX_STRING_LEN, "00000000000\u000000000000000\u0000000000\u0000");
                        writeOct(header, 154, check + writeOct(header, 135, size));
                        tar.write(header, 0, 512);
                        int need = size;
                        while (need > 0) {
                            int read = data.read(buf, 0, need > buf.length ? buf.length : need);
                            if (read > 0) {
                                tar.write(buf, 0, read);
                                need -= read;
                            }
                        }
                        int pad = size % 512;
                        if (pad > 0) {
                            int pad2 = 512 - pad;
                            Arrays.fill(buf, 0, pad2, (byte) 0);
                            tar.write(buf, 0, pad2);
                        }
                    }
                    tar.write(new byte[1024], 0, 1024);
                    tar.close();
                    data.close();
                    index.close();
                }
                this.dataFile.delete();
                this.indexFile.delete();
            } catch (Throwable e) {
                Log.d("Failed end log load for " + this.dataFile, e);
            }
        }
    }

    public void endDebugLoad() {
        if (this.debugLoad != null) {
            this.debugLoad.end();
        }
    }

    public void setDebugLoad(String debugLoad) {
        try {
            this.debugLoad = new DebugLoad(debugLoad);
        } catch (Throwable e) {
            Log.d("Failed prepare log loads for " + debugLoad, e);
        }
    }

    InputStream debugLoad(LuaString src, String file, boolean filename, final InputStream base) {
        final DebugLoad debugLoad = this.debugLoad;
        if (debugLoad != null) {
            if (filename) {
                if (file != null) {
                    try {
                        byte[] buf = this.debugLoadBuf;
                        if (buf == null) {
                            buf = new byte[8192];
                            this.debugLoadBuf = buf;
                        }
                        int written = 0;
                        if (Build.VERSION.SDK_INT >= 21) {
                            FileDescriptor rfd = Os.open(file, OsConstants.O_RDONLY, 438);
                            while (true) {
                                int read = Os.read(rfd, buf, 0, buf.length);
                                if (read <= 0) {
                                    break;
                                }
                                debugLoad.data.write(buf, 0, read);
                                written += read;
                            }
                            Os.close(rfd);
                        } else {
                            InputStream is = new FileInputStream(this.globals.newFile(file));
                            while (true) {
                                int read2 = is.read(buf);
                                if (read2 <= 0) {
                                    break;
                                }
                                debugLoad.data.write(buf, 0, read2);
                                written += read2;
                            }
                            is.close();
                        }
                        if (written > 0) {
                            debugLoad.index.writeInt(written);
                            return base;
                        }
                        return base;
                    } catch (Throwable e) {
                        Log.d("Failed write log file from " + file, e);
                        return base;
                    }
                }
                return base;
            }
            if (src != null) {
                try {
                    int len = src.m_length;
                    if (len > 0) {
                        debugLoad.data.write(src.m_bytes, src.m_offset, len);
                        debugLoad.index.writeInt(len);
                        return base;
                    }
                    return base;
                } catch (Throwable e2) {
                    Log.w("Failed write log file", e2);
                    return base;
                }
            }
            return new InputStream() { // from class: luaj.lib.BaseLib.1
                private int written = 0;

                @Override // java.io.InputStream
                public int read() throws IOException {
                    int ret = base.read();
                    try {
                        if (ret != -1) {
                            debugLoad.data.write(ret);
                            this.written++;
                        } else if (this.written > 0) {
                            debugLoad.index.writeInt(this.written);
                        }
                    } catch (Throwable e3) {
                        Log.w("Failed write log file", e3);
                    }
                    return ret;
                }
            };
        }
        return base;
    }

    final class loadfile extends VarArgFunction {
        loadfile() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaValue ld = args.arg1();
            if (!ld.isstring() && !ld.isnil()) {
                argerror(1, "loadfile", "string expected, got " + ld.typename());
            }
            String filename = ld.checkjstring();
            String mode = args.optjstring(2, "bt");
            LuaValue env = args.optvalue(3, BaseLib.this.globals);
            return BaseLib.this.loadFile(filename, mode, env);
        }
    }

    final class print extends VarArgFunction {
        final BaseLib baselib;

        print(BaseLib baselib) {
            this.baselib = baselib;
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaValue tostring = BaseLib.this.globals.get("tostring");
            int n = args.narg();
            for (int i = 1; i <= n; i++) {
                if (i > 1) {
                    BaseLib.this.globals.STDOUT.print(" \t");
                }
                LuaValue v = tostring.call(args.arg(i));
                if (!v.isstring()) {
                    throw new LuaError("'tostring' must return a string to 'print', got " + v.typename());
                }
                BaseLib.this.globals.STDOUT.print(v.strvalue().tojstring());
            }
            BaseLib.this.globals.STDOUT.println();
            return NONE;
        }
    }

    static final class rawequal extends LibFunction {
        rawequal() {
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call() {
            return argerror(1, "value expected");
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            return argerror(2, "value expected");
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            return valueOf(arg1.raweq(arg2));
        }
    }

    static final class rawget extends TableLibFunction {
        rawget() {
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            return argerror(2, "value expected");
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            return arg1.checktable().rawget(arg2);
        }
    }

    static final class rawlen extends LibFunction {
        rawlen() {
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            return valueOf(arg.rawlen());
        }
    }

    static final class rawset extends TableLibFunction {
        rawset() {
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue table) {
            return argerror(2, "value expected");
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue table, LuaValue index) {
            return argerror(3, "value expected");
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue table, LuaValue index, LuaValue value) {
            LuaTable t = table.checktable();
            if (!index.isvalidkey()) {
                argerror(2, "table index is nil");
            }
            t.rawset(index, value);
            return t;
        }
    }

    static final class select extends VarArgFunction {
        select() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            int n = args.narg() - 1;
            if (args.arg1().equals(valueOf("#"))) {
                return valueOf(n);
            }
            int i = args.checkint(1);
            if (i == 0 || i < (-n)) {
                argerror(1, "index out of range");
            }
            return args.subargs(i < 0 ? n + i + 2 : i + 1);
        }
    }

    static final class setmetatable extends TableLibFunction {
        setmetatable() {
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue table) {
            return argerror(2, "nil or table expected");
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue table, LuaValue metatable) {
            LuaValue mt0 = table.checktable().getmetatable();
            if (mt0 != null && !mt0.rawget(METATABLE).isnil()) {
                error("cannot change a protected metatable");
            }
            return table.setmetatable(metatable.isnil() ? null : metatable.checktable());
        }
    }

    static final class tonumber extends LibFunction {
        tonumber() {
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue e) {
            return e.tonumber();
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue e, LuaValue base) {
            if (base.isnil()) {
                return e.tonumber();
            }
            int b = base.checkint();
            if (b < 2 || b > 36) {
                argerror(2, "base out of range");
            }
            return e.checkstring().tonumber(b);
        }
    }

    static final class tostring extends LibFunction {
        tostring() {
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            return BaseLib.tostring(arg);
        }
    }

    static LuaString tostring(LuaValue arg) {
        LuaValue h = arg.metatag(TOSTRING);
        if (!h.isnil()) {
            LuaValue h2 = h.call(arg);
            LuaValue v = h2.tostring();
            if (v.isnil()) {
                throw new LuaError("'__tostring' must return a string, got '" + h2.typename() + "' ('" + h2 + "')");
            }
            return v.checkstring();
        }
        LuaValue v2 = arg.tostring();
        if (!v2.isnil()) {
            return v2.checkstring();
        }
        return valueOf(arg.tojstring());
    }

    static final class type extends LibFunction {
        type() {
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue arg) {
            return valueOf(arg.typename());
        }
    }

    final class pcall extends VarArgFunction {
        pcall() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            return LuaClosure.protectedCall(BaseLib.this.globals, this, args.checkvalue(1), args.subargs(2));
        }
    }

    final class xpcall extends VarArgFunction {
        xpcall() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaThread t = BaseLib.this.globals.running;
            LuaValue preverror = t.errorfunc;
            t.errorfunc = args.checkvalue(2);
            try {
                return LuaClosure.protectedCall(BaseLib.this.globals, this, args.checkvalue(1), args.subargs(3));
            } finally {
                t.errorfunc = preverror;
            }
        }
    }

    static final class pairs extends VarArgFunction {
        final next next;

        pairs(next next) {
            this.next = next;
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            return varargsOf(this.next, args.checktable(1), NIL);
        }
    }

    static final class ipairs extends VarArgFunction {
        inext inext = new inext();

        ipairs() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            return varargsOf(this.inext, args.checktable(1), ZERO);
        }
    }

    static final class next extends VarArgFunction {
        next() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            return args.checktable(1).next(args.arg(2));
        }
    }

    static final class inext extends VarArgFunction {
        inext() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            return args.checktable(1).inext(args.arg(2));
        }
    }

    public Varargs loadFile(String filename, String mode, LuaValue env) {
        InputStream is = this.globals.finder.findResource(filename);
        if (is == null) {
            return varargsOf(NIL, valueOf("cannot open " + filename + ": No such file or directory"));
        }
        try {
            try {
                String fullPath = this.globals.newFile(filename).getAbsolutePath();
                Varargs ret = loadStream(is, String.valueOf('@') + fullPath, mode, env);
                if (ret instanceof LuaClosure) {
                    ((LuaClosure) ret).sourceFile = fullPath;
                }
                if (this.debugLoad != null && (ret instanceof LuaFunction)) {
                    debugLoad(null, filename, true, null);
                }
                try {
                    is.close();
                    return ret;
                } catch (Exception e) {
                    e.printStackTrace();
                    return ret;
                }
            } catch (Throwable e2) {
                String msg = e2.getMessage();
                if (msg == null) {
                    msg = e2.getClass().getName();
                }
                Varargs varargsVarargsOf = varargsOf(NIL, valueOf(msg));
                try {
                    is.close();
                    return varargsVarargsOf;
                } catch (Exception e3) {
                    e3.printStackTrace();
                    return varargsVarargsOf;
                }
            }
        } catch (Throwable th) {
            try {
                is.close();
            } catch (Exception e4) {
                e4.printStackTrace();
            }
            throw th;
        }
    }

    public Varargs loadStream(InputStream is, String chunkname, String mode, LuaValue env) {
        Varargs varargsLoad;
        try {
            if (is == null) {
                varargsLoad = varargsOf(NIL, valueOf("not found: " + chunkname));
            } else {
                varargsLoad = this.globals.load(is, chunkname, mode, env);
            }
            return varargsLoad;
        } catch (Exception e) {
            return varargsOf(NIL, valueOf(e.getMessage()));
        }
    }

    private static class StringInputStream extends InputStream {
        byte[] bytes;
        final LuaValue func;
        int offset;
        int remaining = 0;

        StringInputStream(LuaValue func) {
            this.func = func;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            if (this.remaining < 0) {
                return -1;
            }
            if (this.remaining == 0) {
                LuaValue s = this.func.call();
                if (s.isnil()) {
                    this.remaining = -1;
                    return -1;
                }
                LuaString ls = s.strvalue();
                this.bytes = ls.m_bytes;
                this.offset = ls.m_offset;
                this.remaining = ls.m_length;
                if (this.remaining <= 0) {
                    return -1;
                }
            }
            this.remaining--;
            byte[] bArr = this.bytes;
            int i = this.offset;
            this.offset = i + 1;
            return bArr[i] & 255;
        }
    }

    public static class CountingBufferedInputStream extends BufferedInputStream {
        private long count;
        private long mark;

        public CountingBufferedInputStream(InputStream in, int size) {
            super(in, size);
            this.mark = -1L;
        }

        public CountingBufferedInputStream(InputStream in) {
            super(in);
            this.mark = -1L;
        }

        public InputStream getSource() {
            return this.in;
        }

        public long getCount() {
            return this.count;
        }

        @Override // java.io.BufferedInputStream, java.io.FilterInputStream, java.io.InputStream
        public int read() throws IOException {
            int result = super.read();
            if (result != -1) {
                this.count++;
            }
            return result;
        }

        @Override // java.io.BufferedInputStream, java.io.FilterInputStream, java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            int result = super.read(b, off, len);
            if (result != -1) {
                this.count += (long) result;
            }
            return result;
        }

        @Override // java.io.BufferedInputStream, java.io.FilterInputStream, java.io.InputStream
        public long skip(long n) throws IOException {
            long result = super.skip(n);
            this.count += result;
            return result;
        }

        @Override // java.io.BufferedInputStream, java.io.FilterInputStream, java.io.InputStream
        public synchronized void mark(int readlimit) {
            super.mark(readlimit);
            this.mark = this.count;
        }

        @Override // java.io.BufferedInputStream, java.io.FilterInputStream, java.io.InputStream
        public synchronized void reset() throws IOException {
            if (!super.markSupported()) {
                throw new IOException("Mark not supported");
            }
            if (this.mark == -1) {
                throw new IOException("Mark not set");
            }
            super.reset();
            this.count = this.mark;
        }
    }

    public static void main(String[] args) throws Throwable {
    }
}
