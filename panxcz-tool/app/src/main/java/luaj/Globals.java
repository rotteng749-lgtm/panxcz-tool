package luaj;

import android.content.Context;
import android.ext.Tools;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PushbackInputStream;
import java.nio.channels.FileChannel;
import lasm.Lasm;
import lasm.ParseException;
import luaj.compiler.LuaC;
import luaj.lib.BaseLib;
import luaj.lib.Bit32Lib;
import luaj.lib.DebugLib;
import luaj.lib.IoLib;
import luaj.lib.MathLib;
import luaj.lib.OsLib;
import luaj.lib.PackageLib;
import luaj.lib.ResourceFinder;
import luaj.lib.StringLib;
import luaj.lib.TableLib;
import luaj.lib.Utf8Lib;

/* JADX INFO: loaded from: classes.dex */
public class Globals extends LuaTable {
    private static final int MAX_OPEN_FILES = 1024;
    private static final long MAX_WRITE_BYTES = 1073741824;
    public BaseLib baselib;
    public Compiler compiler;
    public DebugLib debuglib;
    public ResourceFinder finder;
    public Loader loader;
    public PackageLib package_;
    public Undumper undumper;
    public boolean fullVersion = true;
    public int openFiles = 0;
    private String lastFile = null;
    public long writeBytes = 0;
    public int logMillis = 0;
    public PrintStream STDOUT = System.out;
    public PrintStream STDERR = System.err;
    public LuaThread running = new LuaThread(this);
    private String workDir = null;

    public interface Compiler {
        Prototype compile(InputStream inputStream, String str) throws IOException;
    }

    public interface Loader {
        LuaFunction load(Prototype prototype, String str, LuaValue luaValue) throws IOException;
    }

    public interface Undumper {
        Prototype undump(InputStream inputStream, String str) throws IOException;
    }

    public void addOpenFiles(String filename) {
        if (filename != null) {
            if (!filename.equals(this.lastFile)) {
                this.lastFile = filename;
            } else {
                return;
            }
        }
        this.openFiles++;
        if (this.openFiles >= 1024) {
            throw new LuaError("Too many open files: " + this.openFiles + "; last: " + filename);
        }
    }

    public void addWriteBytes(long writeBytes) {
        this.writeBytes += writeBytes;
        if (this.writeBytes >= MAX_WRITE_BYTES) {
            Context context = Tools.getContext();
            throw new LuaError("Too many write to files: " + Tools.formatFileSize(context, this.writeBytes) + "; last: " + Tools.formatFileSize(context, writeBytes));
        }
    }

    public static Globals initTest() {
        Globals globals = new Globals();
        LoadState.install(globals);
        LuaC.install(globals);
        globals.load(new BaseLib());
        globals.load(new PackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new Utf8Lib());
        globals.load(new MathLib());
        globals.load(new IoLib());
        globals.load(new OsLib());
        globals.load(new DebugLib());
        return globals;
    }

    @Override // luaj.LuaValue
    public Globals checkglobals() {
        return this;
    }

    public LuaValue loadfile(String filename) {
        try {
            InputStream is = this.finder.findResource(filename);
            String fullPath = newFile(filename).getAbsolutePath();
            LuaValue ret = load(is, String.valueOf('@') + fullPath, "bt", this);
            if (ret instanceof LuaClosure) {
                ((LuaClosure) ret).sourceFile = fullPath;
                return ret;
            }
            return ret;
        } catch (Exception e) {
            return error("load " + filename + ": " + e, e);
        }
    }

    public LuaValue load(InputStream is, String chunkname, String mode, LuaValue environment) {
        LuaValue luaValueError;
        try {
            try {
                Prototype p = loadPrototype(is, chunkname, mode);
                luaValueError = this.loader.load(p, chunkname, environment);
            } catch (LuaError l) {
                throw l;
            } catch (Exception e3) {
                if (e3 instanceof EOFException) {
                    luaValueError = error("truncated precompiled chunk", e3);
                } else {
                    luaValueError = error("load " + chunkname + ": " + e3, e3);
                }
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return luaValueError;
    }

    public Prototype loadPrototype(InputStream is, String chunkname, String mode) throws IOException {
        if (mode.indexOf(98) >= 0) {
            if (this.undumper == null) {
                error("No undumper.");
            }
            byte[] sig = new byte[4];
            int read = is.read(sig);
            if (sig[0] == LoadState.LUA_SIGNATURE[0] && sig[1] == LoadState.LUA_SIGNATURE[1] && sig[2] == LoadState.LUA_SIGNATURE[2] && sig[3] == LoadState.LUA_SIGNATURE[3]) {
                Prototype p = this.undumper.undump(is, chunkname);
                if (is instanceof BaseLib.CountingBufferedInputStream) {
                    BaseLib.CountingBufferedInputStream cbis = (BaseLib.CountingBufferedInputStream) is;
                    InputStream src = cbis.getSource();
                    if (src instanceof FileInputStream) {
                        long pos = cbis.getCount();
                        if (pos < ((FileInputStream) src).getChannel().size()) {
                            p.tailPos = (int) pos;
                            return p;
                        }
                        return p;
                    }
                    return p;
                }
                if (is instanceof FileInputStream) {
                    FileInputStream fis = (FileInputStream) is;
                    FileChannel channel = fis.getChannel();
                    long pos2 = channel.position();
                    long size = channel.size();
                    if (pos2 < size) {
                        p.tailPos = (int) pos2;
                        return p;
                    }
                    return p;
                }
                return p;
            }
            if (read > 0) {
                is = PbInputStream.wrap(is);
                ((PbInputStream) is).unread(sig, 0, read);
            }
        }
        if (mode.indexOf(116) >= 0) {
            byte[] header = new byte[Print.HEADER.length()];
            int read2 = is.read(header);
            if (read2 > 0) {
                is = PbInputStream.wrap(is);
                ((PbInputStream) is).unread(header, 0, read2);
            }
            if (new String(header).equals(Print.HEADER)) {
                try {
                    Lasm lasm2 = new Lasm(is);
                    Prototype p2 = lasm2.assemble();
                    return p2;
                } catch (ParseException e) {
                    throw new LuaError("Failed assemble: " + e.getMessage(), e);
                }
            }
            if (header[0] == LoadState.LUA_SIGNATURE[0] && header[1] == LoadState.LUA_SIGNATURE[1] && header[2] == LoadState.LUA_SIGNATURE[2] && header[3] == LoadState.LUA_SIGNATURE[3]) {
                throw new LuaError("attempt to load a binary chunk (mode is '" + mode + "')");
            }
            Prototype p3 = compilePrototype(is, chunkname);
            return p3;
        }
        throw new LuaError("attempt to load a text chunk (mode is '" + mode + "'");
    }

    public Prototype compilePrototype(InputStream stream, String chunkname) throws IOException {
        if (this.compiler == null) {
            error("No compiler.");
        }
        return this.compiler.compile(PbInputStream.wrap(stream).setSkipBOM(true), chunkname);
    }

    static class PbInputStream extends PushbackInputStream {
        static final int SIZE = Print.HEADER.length() + 4;
        boolean skipBOM;

        static PbInputStream wrap(InputStream is) {
            if (!(is instanceof PbInputStream)) {
                is = new PbInputStream(is);
            }
            return (PbInputStream) is;
        }

        private PbInputStream(InputStream in) {
            super(in, SIZE);
            this.skipBOM = false;
        }

        @Override // java.io.PushbackInputStream, java.io.FilterInputStream, java.io.InputStream
        public int read() throws IOException {
            int r1;
            byte[] buf = this.buf;
            if (buf == null) {
                throw new IOException();
            }
            if (this.pos < SIZE) {
                int i = this.pos;
                this.pos = i + 1;
                r1 = buf[i] & 255;
            } else {
                r1 = this.in.read();
            }
            if (r1 == 239 && this.skipBOM) {
                int r2 = super.read();
                if (r2 != 187) {
                    unread(r2);
                    return r1;
                }
                int r3 = super.read();
                if (r3 != 191) {
                    unread(r3);
                    unread(r2);
                    return r1;
                }
                int r12 = read();
                return r12;
            }
            return r1;
        }

        public PbInputStream setSkipBOM(boolean skipBOM) {
            this.skipBOM = skipBOM;
            return this;
        }
    }

    public void setWorkDir(String dir) {
        this.workDir = dir;
    }

    public File newFile(String filename) {
        String dir = this.workDir;
        return (dir == null || (filename.length() != 0 && filename.charAt(0) == '/')) ? new File(filename) : new File(dir, filename);
    }
}
