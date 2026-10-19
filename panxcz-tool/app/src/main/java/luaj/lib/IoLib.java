package luaj.lib;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import luaj.Globals;
import luaj.LuaArgError;
import luaj.LuaError;
import luaj.LuaString;
import luaj.LuaTable;
import luaj.LuaValue;
import luaj.Varargs;
import luaj.compiler.Constants;

/* JADX INFO: loaded from: classes.dex */
public class IoLib extends TwoArgFunction {
    public static final char A = 'a';
    public static final char A_PLUS = 140;
    private static final int FILE_CLOSE = 11;
    private static final int FILE_FLUSH = 12;
    private static final int FILE_LINES = 13;
    private static final int FILE_READ = 14;
    private static final int FILE_SEEK = 15;
    private static final int FILE_SETVBUF = 16;
    private static final int FILE_WRITE = 17;
    protected static final int FTYPE_NAMED = 3;
    protected static final int FTYPE_STDERR = 2;
    protected static final int FTYPE_STDOUT = 1;
    private static final int IO_CLOSE = 0;
    private static final int IO_FLUSH = 1;
    private static final int IO_INPUT = 2;
    private static final int IO_LINES = 3;
    private static final int IO_OPEN = 4;
    private static final int IO_OUTPUT = 5;
    private static final int IO_POPEN = 6;
    private static final int IO_READ = 7;
    private static final int IO_TMPFILE = 8;
    private static final int IO_TYPE = 9;
    private static final int IO_WRITE = 10;
    private static final int LINES_ITER = 18;
    private static final int L_MAXLENNUM = 200;
    public static final char R = 'r';
    public static final char R_PLUS = 157;
    public static final char W = 'w';
    public static final char W_PLUS = 162;
    LuaTable filemethods;
    public Globals globals;
    private static final LuaValue STDOUT = valueOf("stdout");
    private static final LuaValue STDERR = valueOf("stderr");
    private static final LuaValue FILE = valueOf("file");
    private static final LuaValue CLOSED_FILE = valueOf("closed file");
    public static final String[] IO_NAMES = {"close", "flush", "input", "lines", "open", "output", "popen", "read", "tmpfile", "type", "write"};
    public static final String[] FILE_NAMES = {"close", "flush", "lines", "read", "seek", "setvbuf", "write"};
    private File infile = null;
    private File outfile = null;
    private File errfile = null;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract class File extends LuaValue {
        public abstract void close() throws IOException;

        public abstract void flush() throws IOException;

        public abstract boolean isclosed();

        public abstract boolean isstdfile();

        public abstract int peek() throws IOException;

        public abstract int read() throws IOException;

        public abstract int read(byte[] bArr, int i, int i2) throws IOException;

        public abstract int remaining() throws IOException;

        public abstract int seek(String str, int i) throws IOException;

        public abstract void setvbuf(String str, int i) throws IOException;

        public abstract void write(LuaString luaString) throws IOException;

        protected File() {
        }

        public boolean eof() throws IOException {
            try {
                return peek() < 0;
            } catch (EOFException e) {
                return true;
            }
        }

        @Override // luaj.LuaValue
        public LuaValue getmetatable() {
            return IoLib.this.filemethods;
        }

        @Override // luaj.LuaValue
        public LuaValue get(LuaValue key) {
            return IoLib.this.filemethods.get(key);
        }

        @Override // luaj.LuaValue
        public int type() {
            return 7;
        }

        @Override // luaj.LuaValue
        public String typename() {
            return "userdata";
        }

        @Override // luaj.LuaValue, luaj.Varargs
        public String tojstring() {
            return "file: " + Integer.toHexString(hashCode());
        }

        public void finalize() {
            if (!isclosed()) {
                try {
                    close();
                } catch (IOException e) {
                }
            }
        }
    }

    private File wrapStdout() throws IOException {
        return new StdoutFile(this, 1, null);
    }

    private File wrapStderr() throws IOException {
        return new StdoutFile(this, 2, null);
    }

    protected File openFile(String filename, char mode) throws IOException {
        return new FileImpl(this, this.globals.newFile(filename), mode, null);
    }

    private File tmpFile() throws IOException {
        java.io.File f = java.io.File.createTempFile(OsLib.TMP_PREFIX, OsLib.TMP_SUFFIX);
        FileImpl ret = new FileImpl(this, f, W_PLUS, null);
        f.delete();
        return ret;
    }

    private File openProgram(String prog, String mode) throws IOException {
        throw new LuaError("Failed openProgram('" + prog + "', '" + mode + "'): disabled by security reason");
    }

    private final class FileImpl extends File {
        private byte[] buf;
        private boolean closed;
        private final RandomAccessFile file;
        private boolean lineBuf;
        private final char mode;
        private int pos;

        private FileImpl(java.io.File file, char mode) throws IOException {
            super();
            this.closed = false;
            this.buf = null;
            RandomAccessFile f = new RandomAccessFile(file, mode == 'r' ? "r" : "rw");
            if (mode != 'r') {
                IoLib.this.globals.addOpenFiles(file.getAbsolutePath());
            }
            if (mode == 'a' || mode == 140) {
                f.seek(f.length());
            } else if (mode == 'w' || mode == 162) {
                f.setLength(0L);
            }
            this.file = f;
            this.mode = mode;
        }

        /* synthetic */ FileImpl(IoLib ioLib, java.io.File file, char c, FileImpl fileImpl) throws IOException {
            this(file, c);
        }

        @Override // luaj.lib.IoLib.File, luaj.LuaValue, luaj.Varargs
        public String tojstring() {
            return "file (" + (this.closed ? "closed" : Integer.valueOf(hashCode())) + ')';
        }

        @Override // luaj.lib.IoLib.File
        public boolean isstdfile() {
            return false;
        }

        @Override // luaj.lib.IoLib.File
        public void close() throws IOException {
            flush();
            this.closed = true;
            this.file.close();
        }

        @Override // luaj.lib.IoLib.File
        public void write(LuaString s) throws IOException {
            checkMode(true);
            IoLib.this.globals.addWriteBytes(s.m_length);
            writeBuf(s.m_bytes, s.m_offset, s.m_length);
        }

        private void writeBuf(byte[] data, int offset, int length) throws IOException {
            if (this.buf == null) {
                write(data, offset, length);
                return;
            }
            if (length != 0) {
                byte[] buf = this.buf;
                int blen = buf.length;
                int pos = this.pos;
                if (this.lineBuf) {
                    int i = (offset + length) - 1;
                    while (true) {
                        if (i < offset) {
                            break;
                        }
                        if (data[i] != 10) {
                            i--;
                        } else {
                            if (pos > 0) {
                                write(buf, 0, pos);
                                pos = 0;
                                this.pos = 0;
                            }
                            int wlen = (i - offset) + 1;
                            write(data, offset, wlen);
                            offset += wlen;
                            length -= wlen;
                        }
                    }
                } else if (pos > 0 || length <= blen) {
                    int clen = blen - pos;
                    if (clen > length) {
                        clen = length;
                    }
                    System.arraycopy(data, offset, buf, pos, clen);
                    offset += clen;
                    length -= clen;
                    pos += clen;
                    if (pos == blen) {
                        write(buf, 0, pos);
                        pos = 0;
                    }
                    this.pos = pos;
                    if (length == 0) {
                        return;
                    }
                }
                int tlen = pos + length;
                if (tlen >= blen) {
                    int wlen2 = (tlen / blen) * blen;
                    if (pos > 0) {
                        write(buf, 0, pos);
                        wlen2 -= pos;
                        pos = 0;
                        this.pos = 0;
                    }
                    write(data, offset, wlen2);
                    offset += wlen2;
                    length -= wlen2;
                }
                if (length > 0) {
                    System.arraycopy(data, offset, buf, pos, length);
                    this.pos = pos + length;
                }
            }
        }

        @Override // luaj.lib.IoLib.File
        public void flush() throws IOException {
            byte[] buf = this.buf;
            int pos = this.pos;
            if (buf != null && pos > 0) {
                write(buf, 0, pos);
                this.pos = 0;
            }
        }

        private void write(byte[] buf, int offset, int length) throws IOException {
            RandomAccessFile file = this.file;
            if (this.mode == 140) {
                long pos = file.getFilePointer();
                file.seek(file.length());
                file.write(buf, offset, length);
                file.seek(pos);
                return;
            }
            file.write(buf, offset, length);
        }

        @Override // luaj.lib.IoLib.File
        public boolean isclosed() {
            return this.closed;
        }

        @Override // luaj.lib.IoLib.File
        public int seek(String option, int pos) throws IOException {
            RandomAccessFile file = this.file;
            if ("set".equals(option)) {
                file.seek(pos);
            } else if ("end".equals(option)) {
                file.seek(file.length() + ((long) pos));
            } else {
                file.seek(file.getFilePointer() + ((long) pos));
            }
            return (int) file.getFilePointer();
        }

        private void bufFree() throws IOException {
            if (this.buf != null) {
                flush();
                this.buf = null;
            }
        }

        @Override // luaj.lib.IoLib.File
        public void setvbuf(String mode, int size) throws IOException {
            if ("no".equals(mode)) {
                bufFree();
                return;
            }
            boolean lineBuf = "line".equals(mode);
            byte[] buf = this.buf;
            if (buf == null || buf.length != size) {
                try {
                    byte[] newBuf = new byte[size];
                    bufFree();
                    this.buf = newBuf;
                    this.pos = 0;
                } catch (OutOfMemoryError e) {
                    throw new IOException("Out of memory for create buffer " + size + " bytes", e);
                }
            } else if (this.lineBuf != lineBuf) {
                flush();
            }
            this.lineBuf = lineBuf;
        }

        private void checkMode(boolean write) throws IOException {
            switch (this.mode) {
                case 'a':
                case 'w':
                    if (!write) {
                        throw new IOException("invalid mode for read data: '" + this.mode + "'");
                    }
                    return;
                case 'r':
                    if (write) {
                        throw new IOException("invalid mode for write data: '" + this.mode + "'");
                    }
                    return;
                default:
                    return;
            }
        }

        @Override // luaj.lib.IoLib.File
        public int remaining() throws IOException {
            checkMode(false);
            return (int) (this.file.length() - this.file.getFilePointer());
        }

        @Override // luaj.lib.IoLib.File
        public int peek() throws IOException {
            checkMode(false);
            RandomAccessFile file = this.file;
            long fp = file.getFilePointer();
            int c = file.read();
            file.seek(fp);
            return c;
        }

        @Override // luaj.lib.IoLib.File
        public int read() throws IOException {
            checkMode(false);
            return this.file.read();
        }

        @Override // luaj.lib.IoLib.File
        public int read(byte[] bytes, int offset, int length) throws IOException {
            checkMode(false);
            return this.file.read(bytes, offset, length);
        }
    }

    private final class StdoutFile extends File {
        private final int file_type;

        private StdoutFile(int file_type) {
            super();
            this.file_type = file_type;
        }

        /* synthetic */ StdoutFile(IoLib ioLib, int i, StdoutFile stdoutFile) {
            this(i);
        }

        @Override // luaj.lib.IoLib.File, luaj.LuaValue, luaj.Varargs
        public String tojstring() {
            return "file (" + hashCode() + ')';
        }

        private final PrintStream getPrintStream() {
            if (this.file_type == 2) {
                return IoLib.this.globals.STDERR;
            }
            return IoLib.this.globals.STDOUT;
        }

        @Override // luaj.lib.IoLib.File
        public void write(LuaString string) throws IOException {
            getPrintStream().write(string.m_bytes, string.m_offset, string.m_length);
        }

        @Override // luaj.lib.IoLib.File
        public void flush() throws IOException {
            getPrintStream().flush();
        }

        @Override // luaj.lib.IoLib.File
        public boolean isstdfile() {
            return true;
        }

        @Override // luaj.lib.IoLib.File
        public void close() throws IOException {
        }

        @Override // luaj.lib.IoLib.File
        public boolean isclosed() {
            return false;
        }

        @Override // luaj.lib.IoLib.File
        public int seek(String option, int bytecount) throws IOException {
            return 0;
        }

        @Override // luaj.lib.IoLib.File
        public void setvbuf(String mode, int size) throws IOException {
        }

        @Override // luaj.lib.IoLib.File
        public int remaining() throws IOException {
            return 0;
        }

        @Override // luaj.lib.IoLib.File
        public int peek() throws IOException {
            return 0;
        }

        @Override // luaj.lib.IoLib.File
        public int read() throws IOException {
            return 0;
        }

        @Override // luaj.lib.IoLib.File
        public int read(byte[] bytes, int offset, int length) throws IOException {
            return 0;
        }
    }

    @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue modname, LuaValue env) {
        this.globals = env.checkglobals();
        LuaTable t = new LuaTable();
        bind(t, IoLibV.class, IO_NAMES);
        this.filemethods = new LuaTable();
        bind(this.filemethods, IoLibV.class, FILE_NAMES, 11);
        setLibInstance(t);
        setLibInstance(this.filemethods);
        this.filemethods.set("__name", "FILE*");
        this.filemethods.set("__index", this.filemethods);
        t.set(STDOUT, output(false));
        t.set(STDERR, errput());
        env.set("io", t);
        if (!env.get("package").isnil()) {
            env.get("package").get("loaded").set("io", t);
        }
        return t;
    }

    private void setLibInstance(LuaTable t) {
        LuaValue[] k = t.keys();
        for (LuaValue luaValue : k) {
            ((IoLibV) t.get(luaValue)).iolib = this;
        }
    }

    static final class IoLibV extends VarArgFunction {
        private Varargs args;
        private File f;
        public IoLib iolib;
        private boolean toclose;

        public IoLibV() {
        }

        public IoLibV(File f, String name, int opcode, IoLib iolib, boolean toclose, Varargs args) {
            this(f, name, opcode, iolib);
            this.toclose = toclose;
            this.args = args.dealias();
        }

        public IoLibV(File f, String name, int opcode, IoLib iolib) {
            this.f = f;
            this.name = name;
            this.opcode = opcode;
            this.iolib = iolib;
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            String funcname;
            Varargs varargs_lines_iter;
            try {
                switch (this.opcode) {
                    case 0:
                        varargs_lines_iter = this.iolib._io_close(args.arg1());
                        break;
                    case 1:
                        varargs_lines_iter = this.iolib._io_flush();
                        break;
                    case 2:
                        varargs_lines_iter = this.iolib._io_input(args.arg1());
                        break;
                    case 3:
                        varargs_lines_iter = this.iolib._io_lines(args);
                        break;
                    case 4:
                        varargs_lines_iter = this.iolib._io_open(args.checkjstring(1), args.optjstring(2, "r"));
                        break;
                    case 5:
                        varargs_lines_iter = this.iolib._io_output(args.arg1());
                        break;
                    case 6:
                        varargs_lines_iter = this.iolib._io_popen(args.checkjstring(1), args.optjstring(2, "r"));
                        break;
                    case 7:
                        varargs_lines_iter = this.iolib._io_read(args);
                        break;
                    case 8:
                        varargs_lines_iter = this.iolib._io_tmpfile();
                        break;
                    case 9:
                        varargs_lines_iter = this.iolib._io_type(args.arg1());
                        break;
                    case 10:
                        varargs_lines_iter = this.iolib._io_write(args);
                        break;
                    case 11:
                        varargs_lines_iter = this.iolib._file_close(args.arg1());
                        break;
                    case 12:
                        varargs_lines_iter = this.iolib._file_flush(args.arg1());
                        break;
                    case 13:
                        varargs_lines_iter = this.iolib._file_lines(args);
                        break;
                    case 14:
                        varargs_lines_iter = this.iolib._file_read(args.arg1(), args.subargs(2));
                        break;
                    case 15:
                        varargs_lines_iter = this.iolib._file_seek(args.arg1(), args.optjstring(2, "cur"), args.optint(3, 0));
                        break;
                    case 16:
                        varargs_lines_iter = this.iolib._file_setvbuf(args.arg1(), args.checkjstring(2), args.optint(3, 8192));
                        break;
                    case 17:
                        varargs_lines_iter = this.iolib._file_write(args.arg1(), args.subargs(2));
                        break;
                    case 18:
                        varargs_lines_iter = this.iolib._lines_iter(this.f, this.toclose, this.args);
                        break;
                    default:
                        varargs_lines_iter = NONE;
                        break;
                }
                return varargs_lines_iter;
            } catch (IOException ioe) {
                if (this.opcode == 18) {
                    String s = ioe.getMessage();
                    if (s == null) {
                        s = ioe.toString();
                    }
                    error(s, ioe);
                }
                return IoLib.errorresult(ioe, this.opcode);
            } catch (LuaArgError lae) {
                if (this.opcode < IoLib.IO_NAMES.length) {
                    funcname = "io." + IoLib.IO_NAMES[this.opcode];
                } else if (this.opcode < IoLib.IO_NAMES.length + IoLib.FILE_NAMES.length) {
                    funcname = "file:" + IoLib.IO_NAMES[this.opcode - IoLib.IO_NAMES.length];
                } else {
                    funcname = this.opcode == 18 ? "(io.lines iterator)" : lae.funcname;
                }
                LuaArgError e = new LuaArgError(lae.iarg, funcname, lae.msg);
                e.initCause(lae);
                throw e;
            }
        }
    }

    private File input(boolean checkopen) {
        File f = this.infile;
        if (f == null) {
            error("stdin not supported");
        }
        if (checkopen && f.isclosed()) {
            error("standard input file is closed");
        }
        return f;
    }

    public Varargs _io_flush() throws IOException {
        output(true);
        this.outfile.flush();
        return LuaValue.TRUE;
    }

    public Varargs _io_tmpfile() throws IOException {
        return tmpFile();
    }

    public Varargs _io_close(LuaValue file) throws IOException {
        File f = file.isnil() ? output(false) : checkfile("io.close", file);
        checkopen(f);
        return ioclose(f);
    }

    public Varargs _io_input(LuaValue file) {
        File fileIoopenfile;
        if (file.isnil()) {
            fileIoopenfile = input(false);
        } else {
            fileIoopenfile = file.isstring() ? ioopenfile(3, file.checkjstring(), "r") : checkfile("io.input", file);
        }
        this.infile = fileIoopenfile;
        return this.infile;
    }

    public Varargs _io_output(LuaValue filename) {
        File fileIoopenfile;
        if (filename.isnil()) {
            fileIoopenfile = output(false);
        } else {
            fileIoopenfile = filename.isstring() ? ioopenfile(3, filename.checkjstring(), "w") : checkfile("io.output", filename);
        }
        this.outfile = fileIoopenfile;
        return this.outfile;
    }

    public Varargs _io_type(LuaValue obj) {
        File f = optfile(obj);
        if (f != null) {
            return f.isclosed() ? CLOSED_FILE : FILE;
        }
        return NIL;
    }

    public Varargs _io_popen(String prog, String mode) throws IOException {
        throw new LuaError("Failed io.popen('" + prog + "', '" + mode + "'): disabled by security reason");
    }

    public Varargs _io_open(String filename, String mode) throws IOException {
        return rawopenfile(3, filename, mode);
    }

    public Varargs _io_lines(Varargs args) {
        String filename = args.optjstring(1, null);
        File infile = filename == null ? input(false) : ioopenfile(3, filename, "r");
        checkopen(infile);
        return lines(infile, filename != null, args.subargs(2));
    }

    public Varargs _io_read(Varargs args) throws IOException {
        input(true);
        return ioread(this.infile, args);
    }

    public Varargs _io_write(Varargs args) throws IOException {
        output(true);
        return iowrite(this.outfile, args);
    }

    public Varargs _file_close(LuaValue file) throws IOException {
        return ioclose(checkfile("file:close", file));
    }

    public Varargs _file_flush(LuaValue file) throws IOException {
        checkfile("file:flush", file).flush();
        return LuaValue.TRUE;
    }

    public Varargs _file_setvbuf(LuaValue file, String mode, int size) throws IOException {
        if ("no".equals(mode)) {
            size = 1;
        } else if (!"full".equals(mode) && !"line".equals(mode)) {
            argerror(1, "file:setvbuf", "invalid value: '" + mode + "'; must be one of 'no', 'full' or 'line'");
        }
        if (size <= 0) {
            argerror(2, "file:setvbuf", "invalid value '" + size + "'; must be positive");
        }
        checkfile("file:setvbuf", file).setvbuf(mode, size);
        return LuaValue.TRUE;
    }

    public Varargs _file_lines(Varargs args) {
        return lines(checkfile("file:lines", args.arg1()), false, args.subargs(2));
    }

    public Varargs _file_read(LuaValue file, Varargs subargs) throws IOException {
        return ioread(checkfile("file:read", file), subargs);
    }

    public Varargs _file_seek(LuaValue file, String whence, int offset) throws IOException {
        if (!"set".equals(whence) && !"end".equals(whence) && !"cur".equals(whence)) {
            argerror(1, "file:seek", "invalid value: '" + whence + "'; must be one of 'set', 'cur' or 'end'");
        }
        return valueOf(checkfile("file:seek", file).seek(whence, offset));
    }

    public Varargs _file_write(LuaValue file, Varargs subargs) throws IOException {
        return iowrite(checkfile("file:write", file), subargs);
    }

    public Varargs _lines_iter(LuaValue file, boolean toclose, Varargs args) throws IOException {
        File f = optfile(file);
        if (f == null) {
            argerror(1, "lines", "not a file: " + file);
        }
        if (f.isclosed()) {
            error("file is already closed");
        }
        Varargs ret = ioread(f, args);
        if (toclose && ret.isnil(1) && f.eof()) {
            f.close();
        }
        return ret;
    }

    private File output(boolean checkopen) {
        File f = this.outfile;
        if (f == null) {
            f = ioopenfile(1, "-", "w");
            this.outfile = f;
        }
        if (checkopen && f.isclosed()) {
            error("standard output file is closed");
        }
        return f;
    }

    private File errput() {
        if (this.errfile != null) {
            return this.errfile;
        }
        File fileIoopenfile = ioopenfile(2, "-", "w");
        this.errfile = fileIoopenfile;
        return fileIoopenfile;
    }

    private File ioopenfile(int filetype, String filename, String mode) {
        try {
            return rawopenfile(filetype, filename, mode);
        } catch (Exception e) {
            error("io error: " + e.getMessage(), e);
            return null;
        }
    }

    private static Varargs ioclose(File f) throws IOException {
        if (f.isstdfile()) {
            return errorresult("cannot close standard file", 1000);
        }
        f.close();
        return successresult();
    }

    private static Varargs successresult() {
        return LuaValue.TRUE;
    }

    static Varargs errorresult(IOException ioe, int errcode) {
        String s = ioe.getMessage();
        StringBuilder sb = new StringBuilder("io error: ");
        if (s == null) {
            s = ioe.toString();
        }
        return errorresult(sb.append(s).toString(), errcode);
    }

    private static Varargs errorresult(String errortext, int errcode) {
        return varargsOf(NIL, valueOf(errortext), valueOf(errcode));
    }

    private Varargs lines(File f, boolean toclose, Varargs args) {
        int n = args.narg();
        if (n > 250) {
            if (toclose) {
                try {
                    f.close();
                } catch (IOException e) {
                }
            }
            error("too many arguments: " + n + " max = " + Constants.MAXSTACK);
        }
        try {
            return new IoLibV(f, "lnext", 18, this, toclose, args);
        } catch (Exception e2) {
            return error("lines: " + e2, e2);
        }
    }

    private static Varargs iowrite(File f, Varargs args) throws IOException {
        int n = args.narg();
        for (int i = 1; i <= n; i++) {
            f.write(args.checkstring(i));
        }
        return f;
    }

    /* JADX WARN: Code restructure failed: missing block: B:37:?, code lost:
    
        return luaj.lib.IoLib.NIL;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:?, code lost:
    
        return varargsOf(r6, 0, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0014, code lost:
    
        if (r2 != 0) goto L31;
     */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0056 A[LOOP:0: B:7:0x0011->B:17:0x0056, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0014 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private luaj.Varargs ioread(luaj.lib.IoLib.File r13, luaj.Varargs r14) throws java.io.IOException {
        File f = r13;
        Varargs args = r14;
        int n = args.narg();
        if (n == 0) {
            return freadline(f, false);
        }
        LuaValue[] out = new LuaValue[n];
        int count = 0;
        for (int i = 1; i <= n; i++) {
            LuaValue fmt = args.arg(i);
            LuaValue r;
            if (fmt.isnumber()) {
                r = freadbytes(f, fmt.toint());
            } else if (fmt.isstring()) {
                LuaString s = fmt.checkstring();
                if (s.m_length < 1) {
                    return argerror(i, "read", "invalid format: '" + fmt + "'");
                }
                byte c = s.m_bytes[s.m_offset];
                if (c == 42 && s.m_length >= 2) {
                    c = s.m_bytes[s.m_offset + 1];
                }
                if (c == 76) {
                    r = freadline(f, true);
                } else if (c == 108) {
                    r = freadline(f, false);
                } else if (c == 110) {
                    r = freadnumber(f);
                } else if (c == 97) {
                    r = freadall(f);
                } else {
                    return argerror(i, "read", "invalid format: '" + fmt + "'");
                }
            } else {
                return argerror(i, "read", "invalid format: '" + fmt + "'");
            }
            if (r.isnil()) {
                break;
            }
            out[count++] = r;
        }
        if (count == 0) {
            return NIL;
        }
        return varargsOf(out, 0, count);
    }

    private static File checkfile(String func, LuaValue val) {
        File f = optfile(val);
        if (f == null) {
            argerror(1, func, "FILE* expected, got " + val.typenamemt());
        }
        checkopen(f);
        return f;
    }

    private static File optfile(LuaValue val) {
        if (val instanceof File) {
            return (File) val;
        }
        return null;
    }

    private static File checkopen(File file) {
        if (file.isclosed()) {
            error("attempt to use a closed file");
        }
        return file;
    }

    private File rawopenfile(int filetype, String filename, String mode) throws IOException {
        int len = mode.length();
        for (int i = 0; i < len; i++) {
            char ch = mode.charAt(i);
            if ((i != 0 || "rwa".indexOf(ch) < 0) && ((i != 1 || ch != '+') && (i < 1 || ch != 'b'))) {
                len = -1;
            }
        }
        if (len <= 0) {
            argerror(2, "io.open", "invalid mode: '" + mode + "'");
        }
        switch (filetype) {
            case 1:
                return wrapStdout();
            case 2:
                return wrapStderr();
            default:
                return openFile(filename, (char) ((mode.indexOf(43) <= 0 ? (char) 0 : '+') + mode.charAt(0)));
        }
    }

    public static LuaValue freadbytes(File f, int count) throws IOException {
        if (count == 0) {
            return f.eof() ? NIL : EMPTYSTRING;
        }
        byte[] b = new byte[count];
        int r = f.read(b, 0, b.length);
        if (r < 0) {
            return NIL;
        }
        return LuaString.valueUsing(b, 0, r);
    }

    public static LuaValue freaduntil(File f, boolean lineonly, boolean withend) throws IOException {
        int c;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            if (lineonly) {
                while (true) {
                    c = f.read();
                    if (c >= 0) {
                        switch (c) {
                            case 10:
                                if (withend) {
                                    baos.write(c);
                                }
                                break;
                            case 11:
                            case 12:
                            default:
                                baos.write(c);
                                continue;
                            case 13:
                                if (withend) {
                                    baos.write(c);
                                } else {
                                    continue;
                                }
                                break;
                        }
                    }
                }
            } else {
                while (true) {
                    c = f.read();
                    if (c < 0) {
                        break;
                    }
                    baos.write(c);
                }
            }
        } catch (EOFException e) {
            c = -1;
        }
        if (c < 0 && baos.size() == 0) {
            return NIL;
        }
        return LuaString.valueUsing(baos.toByteArray());
    }

    public static LuaValue freadline(File f, boolean withend) throws IOException {
        return freaduntil(f, true, withend);
    }

    public static LuaValue freadall(File f) throws IOException {
        int n = f.remaining();
        if (n >= 0) {
            return n == 0 ? EMPTYSTRING : freadbytes(f, n);
        }
        return freaduntil(f, false, false);
    }

    public static LuaValue freadnumber(File f) throws IOException {
        boolean hex = false;
        int count = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        freadchars(f, " \t\r\n", null, 0);
        freadchars(f, "-+", baos, 1);
        if (freadchars(f, "0", baos, 1) > 0) {
            if (freadchars(f, "xX", baos, 1) > 0) {
                hex = true;
            } else {
                count = 1;
            }
        }
        int count2 = count + freadchars(f, hex ? "0123456789abcdefABCDEF" : "0123456789", baos, 0);
        if (freadchars(f, ".", baos, 1) > 0) {
            count2 += freadchars(f, hex ? "0123456789abcdefABCDEF" : "0123456789", baos, 0);
        }
        if (count2 > 0) {
            if (freadchars(f, hex ? "pP" : "eE", baos, 1) > 0) {
                freadchars(f, "-+", baos, 1);
                freadchars(f, "0123456789", baos, 0);
            }
        }
        String s = baos.toString();
        return s.length() > 0 ? parseNumber(s, NIL) : NIL;
    }

    private static int freadchars(File f, String chars, ByteArrayOutputStream baos, int max) throws IOException {
        int found = 0;
        while (true) {
            int c = f.peek();
            if (chars.indexOf(c) < 0) {
                return found;
            }
            f.read();
            found++;
            if (baos != null) {
                if (baos.size() >= L_MAXLENNUM) {
                    baos.reset();
                    return 0;
                }
                baos.write(c);
            }
            if (max > 0 && found >= max) {
                return found;
            }
        }
    }

    public static void main(String[] args) throws Throwable {
    }
}
