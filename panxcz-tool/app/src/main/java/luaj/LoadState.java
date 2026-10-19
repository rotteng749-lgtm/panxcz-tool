package luaj;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.ext.Alert;
import android.ext.BaseActivity;
import android.ext.ListManager;
import android.ext.Log;
import android.ext.Message;
import android.ext.ParserNumbers;
import android.ext.Re;
import android.ext.ThreadManager;
import android.ext.Tools;
import catch_.me_.if_.you_.can_.R;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import luaj.Globals;

/* JADX INFO: loaded from: classes.dex */
public class LoadState {
    public static final int LUAC_FORMAT = 0;
    public static final int LUAC_HEADERSIZE = 12;
    public static final int LUAC_VERSION = 82;
    public static final int LUA_TBOOLEAN = 1;
    public static final int LUA_TFUNCTION = 6;
    public static final int LUA_TINT = -2;
    public static final int LUA_TNIL = 0;
    public static final int LUA_TNONE = -1;
    public static final int LUA_TNUMBER = 3;
    public static final int LUA_TSTRING = 4;
    public static final int LUA_TTABLE = 5;
    public static final int LUA_TTHREAD = 8;
    public static final int LUA_TUSERDATA = 7;
    public static final int LUA_TVALUE = 9;
    public static final int NUMBER_FORMAT_FLOATS_OR_DOUBLES = 0;
    public static final int NUMBER_FORMAT_INTS_ONLY = 1;
    public static final int NUMBER_FORMAT_NUM_PATCH_INT32 = 4;
    public static final String SOURCE_BINARY_STRING = "=?";
    private byte[] buf = new byte[512];
    public final DataInputStream is;
    private int luacFormat;
    private boolean luacLittleEndian;
    private int luacNumberFormat;
    private int luacSizeofInstruction;
    private int luacSizeofInt;
    private int luacSizeofLuaNumber;
    private int luacSizeofSizeT;
    private int luacVersion;
    String name;
    public static final Globals.Undumper instance = new GlobalsUndumper(null);
    public static String encoding = null;
    public static final byte[] LUA_SIGNATURE = {Message.CMD_SC_GET_MEMORY, 76, 117, 97};
    public static final byte[] LUAC_TAIL = {Message.CMD_SC_APP_LIST, -109, 13, 10, Message.CMD_SC_MEM, 10};
    private static final LuaValue[] NOVALUES = new LuaValue[0];
    private static final Prototype[] NOPROTOS = new Prototype[0];
    private static final LocVars[] NOLOCVARS = new LocVars[0];
    private static final Upvaldesc[] NOUPVALDESCS = new Upvaldesc[0];
    private static final int[] NOINTS = new int[0];
    public static volatile boolean badHeader = false;

    public static class BadHeader {
        String msg;
    }

    public static void install(Globals globals) {
        globals.undumper = instance;
    }

    int loadInt() throws IOException {
        this.is.readFully(this.buf, 0, 4);
        if (this.luacLittleEndian) {
            return (this.buf[3] << Message.CMD_SC_PROCESS_LIST) | ((this.buf[2] & 255) << 16) | ((this.buf[1] & 255) << 8) | (this.buf[0] & 255);
        }
        return (this.buf[0] << Message.CMD_SC_PROCESS_LIST) | ((this.buf[1] & 255) << 16) | ((this.buf[2] & 255) << 8) | (this.buf[3] & 255);
    }

    int[] loadIntArray() throws IOException {
        int i;
        byte b;
        int n = loadInt();
        if (n == 0) {
            return NOINTS;
        }
        int m = n << 2;
        if (this.buf.length < m) {
            this.buf = new byte[m];
        }
        this.is.readFully(this.buf, 0, m);
        int[] array = new int[n];
        int i2 = 0;
        int j = 0;
        while (i2 < n) {
            if (this.luacLittleEndian) {
                i = (this.buf[j + 3] << Message.CMD_SC_PROCESS_LIST) | ((this.buf[j + 2] & 255) << 16) | ((this.buf[j + 1] & 255) << 8);
                b = this.buf[j + 0];
            } else {
                i = (this.buf[j + 0] << Message.CMD_SC_PROCESS_LIST) | ((this.buf[j + 1] & 255) << 16) | ((this.buf[j + 2] & 255) << 8);
                b = this.buf[j + 3];
            }
            array[i2] = i | (b & 255);
            i2++;
            j += 4;
        }
        return array;
    }

    long loadInt64() throws IOException {
        int b;
        int a;
        if (this.luacLittleEndian) {
            a = loadInt();
            b = loadInt();
        } else {
            b = loadInt();
            a = loadInt();
        }
        return (((long) b) << 32) | (((long) a) & ParserNumbers.MAX_UINT);
    }

    LuaString loadString() throws IOException {
        int size = this.luacSizeofSizeT == 8 ? (int) loadInt64() : loadInt();
        if (size == 0) {
            return null;
        }
        byte[] bytes = new byte[size];
        this.is.readFully(bytes, 0, size);
        return LuaString.valueUsing(bytes, 0, bytes.length - 1);
    }

    public static LuaValue longBitsToLuaNumber(long bits) {
        if ((Long.MAX_VALUE & bits) == 0) {
            return LuaValue.ZERO;
        }
        int e = ((int) ((bits >> 52) & 2047)) - 1023;
        if (e >= 0 && e < 31) {
            long f = bits & 4503599627370495L;
            int shift = 52 - e;
            long intPrecMask = (1 << shift) - 1;
            if ((f & intPrecMask) == 0) {
                int intValue = ((int) (f >> shift)) | (1 << e);
                if ((bits >> 63) != 0) {
                    intValue = -intValue;
                }
                return LuaLong.valueOf(intValue);
            }
        }
        return LuaValue.valueOf(Double.longBitsToDouble(bits));
    }

    LuaValue loadNumber() throws IOException {
        return this.luacNumberFormat == 1 ? LuaLong.valueOf(loadInt()) : longBitsToLuaNumber(loadInt64());
    }

    void loadConstants(Prototype f) throws IOException {
        int n = loadInt();
        LuaValue[] values = n > 0 ? new LuaValue[n] : NOVALUES;
        for (int i = 0; i < n; i++) {
            switch (this.is.readByte()) {
                case -2:
                    values[i] = LuaLong.valueOf(loadInt());
                    break;
                case -1:
                case 2:
                default:
                    throw new IllegalStateException("bad constant");
                case 0:
                    values[i] = LuaValue.NIL;
                    break;
                case 1:
                    values[i] = this.is.readUnsignedByte() != 0 ? LuaValue.TRUE : LuaValue.FALSE;
                    break;
                case 3:
                    values[i] = loadNumber();
                    break;
                case 4:
                    values[i] = loadString();
                    break;
            }
        }
        f.k = values;
        int n2 = loadInt();
        Prototype[] protos = n2 > 0 ? new Prototype[n2] : NOPROTOS;
        for (int i2 = 0; i2 < n2; i2++) {
            protos[i2] = loadFunction();
        }
        f.p = protos;
    }

    void loadUpvalues(Prototype f) throws IOException {
        int n = loadInt();
        f.upvalues = n > 0 ? new Upvaldesc[n] : NOUPVALDESCS;
        for (int i = 0; i < n; i++) {
            boolean instack = this.is.readByte() != 0;
            int idx = this.is.readByte() & 255;
            f.upvalues[i] = new Upvaldesc(null, instack, idx);
        }
    }

    void loadDebug(Prototype f) throws IOException {
        LuaString src = loadString();
        if (src != null) {
            f.source = src;
        }
        f.lineinfo = loadIntArray();
        int n = loadInt();
        f.locvars = n > 0 ? new LocVars[n] : NOLOCVARS;
        for (int i = 0; i < n; i++) {
            LuaString varname = loadString();
            int startpc = loadInt();
            int endpc = loadInt();
            f.locvars[i] = new LocVars(varname, startpc, endpc);
        }
        int n2 = loadInt();
        for (int i2 = 0; i2 < n2; i2++) {
            f.upvalues[i2].name = loadString();
        }
    }

    public Prototype loadFunction() throws IOException {
        Prototype f = new Prototype();
        f.linedefined = loadInt();
        f.lastlinedefined = loadInt();
        f.numparams = this.is.readUnsignedByte();
        f.is_vararg = this.is.readUnsignedByte();
        f.maxstacksize = this.is.readUnsignedByte();
        if (f.maxstacksize < f.numparams) {
            throw new LuaError("Damaged script 2: .maxstacksize (" + f.maxstacksize + ") < .numparams (" + f.numparams + ")");
        }
        f.code = loadIntArray();
        loadConstants(f);
        loadUpvalues(f);
        loadDebug(f);
        return f;
    }

    public void loadHeader() throws IOException {
        this.luacVersion = this.is.readByte() & 255;
        this.luacFormat = this.is.readByte() & 255;
        byte b = this.is.readByte();
        this.luacLittleEndian = b != 0;
        this.luacSizeofInt = this.is.readByte() & 255;
        this.luacSizeofSizeT = this.is.readByte() & 255;
        this.luacSizeofInstruction = this.is.readByte() & 255;
        this.luacSizeofLuaNumber = this.is.readByte() & 255;
        this.luacNumberFormat = this.is.readByte() & 255;
        String error = "";
        if (this.luacVersion != 82) {
            error = String.valueOf("") + Re.s(R.string.unknown_version) + " " + Tools.stringFormat(Re.s(R.string.expected_found), "'" + Integer.toHexString(82) + "'", "'" + Integer.toHexString(this.luacVersion) + "'") + ListManager.NEW_LINE;
        }
        if (this.luacFormat != 0) {
            error = String.valueOf(error) + Re.s(R.string.unknown_format) + " " + Tools.stringFormat(Re.s(R.string.expected_found), "'0'", "'" + this.luacFormat + "'") + ListManager.NEW_LINE;
        }
        if (b != 0 && b != 1) {
            error = String.valueOf(error) + Re.s(R.string.unknown_endianness) + " " + Tools.stringFormat(Re.s(R.string.expected_found), "'0' " + Re.s(R.string.or) + " '1'", "'" + (b & 255) + "'") + ListManager.NEW_LINE;
        }
        if (this.luacSizeofInt != 4) {
            error = String.valueOf(error) + Tools.stringFormat(Re.s(R.string.unknown_size_s), "int") + " " + Tools.stringFormat(Re.s(R.string.expected_found), "'4'", "'" + this.luacSizeofInt + "'") + ListManager.NEW_LINE;
        }
        if (this.luacSizeofSizeT != 4) {
            error = String.valueOf(error) + Tools.stringFormat(Re.s(R.string.unknown_size_s), "size_t") + " " + Tools.stringFormat(Re.s(R.string.expected_found), "'4'", "'" + this.luacSizeofSizeT + "'") + ListManager.NEW_LINE;
        }
        if (this.luacSizeofInstruction != 4) {
            error = String.valueOf(error) + Re.s(R.string.unknown_size_instr) + " " + Tools.stringFormat(Re.s(R.string.expected_found), "'4'", "'" + this.luacSizeofInstruction + "'") + ListManager.NEW_LINE;
        }
        if (this.luacSizeofLuaNumber != 4 && this.luacSizeofLuaNumber != 8) {
            error = String.valueOf(error) + Re.s(R.string.unknown_size_num) + " " + Tools.stringFormat(Re.s(R.string.expected_found), "'4' " + Re.s(R.string.or) + " '8'", "'" + this.luacSizeofLuaNumber + "'") + ListManager.NEW_LINE;
        }
        if (error.length() > 0) {
            if (!badHeader && !ThreadManager.isInUiThread()) {
                badHeader = true;
                final BadHeader monitor = new BadHeader();
                monitor.msg = error.trim();
                synchronized (monitor) {
                    ThreadManager.runOnUiThread(new Runnable() { // from class: luaj.LoadState.1
                        @Override // java.lang.Runnable
                        public void run() {
                            AlertDialog.Builder builder = Alert.create(Tools.getContext()).setMessage(String.valueOf(LoadState.this.name) + "\n\n" + Tools.stringFormat(Re.s(R.string.invalid_header), monitor.msg)).setNeutralButton(Re.s(R.string.fix_it), new BaseActivity.GoOnForum(BaseActivity.GoOnForum.S12));
                            final BadHeader badHeader2 = monitor;
                            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { // from class: luaj.LoadState.1.1
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog, int which) {
                                    badHeader2.msg = null;
                                }
                            };
                            if (Tools.RANDOM.nextBoolean()) {
                                builder.setPositiveButton(Re.s(R.string.yes), listener).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null);
                            } else {
                                builder.setNegativeButton(Re.s(R.string.yes), listener).setPositiveButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null);
                            }
                            AlertDialog dialog = builder.create();
                            final BadHeader badHeader3 = monitor;
                            Alert.setOnDismissListener(dialog, new DialogInterface.OnDismissListener() { // from class: luaj.LoadState.1.2
                                @Override // android.content.DialogInterface.OnDismissListener
                                public void onDismiss(DialogInterface dialog2) {
                                    synchronized (badHeader3) {
                                        badHeader3.notify();
                                    }
                                }
                            });
                            Alert.show(dialog);
                        }
                    });
                    try {
                        monitor.wait();
                        if (monitor.msg != null) {
                            throw new LuaError(String.valueOf(Re.s(R.string.invalid_header_1).trim()) + "\n\n" + error + ListManager.NEW_LINE + Re.s(R.string.invalid_header_3).trim());
                        }
                    } catch (InterruptedException e) {
                        Log.w("Interrupted wait", e);
                        Thread.currentThread().interrupt();
                        throw new LuaError(e);
                    }
                }
            } else {
                String error2 = "-------------------\n" + Re.s(R.string.invalid_header_1).trim() + "\n\n" + error + ListManager.NEW_LINE + Re.s(R.string.invalid_header_3).trim() + "\n-------------------\n";
                Tools.showToast(error2.trim(), 1);
                Print.ps.println(error2);
            }
        }
        for (int i = 0; i < LUAC_TAIL.length; i++) {
            byte b2 = this.is.readByte();
            if (b2 != LUAC_TAIL[i]) {
                throw new LuaError("Unexpected byte in luac tail of header, index = " + i + " (" + (b2 & 255) + ", not " + (LUAC_TAIL[i] & 255) + ")");
            }
        }
    }

    public static Prototype undump(InputStream stream, String chunkname) throws IOException {
        String sname = chunkname == null ? null : getSourceName(chunkname);
        LoadState s = new LoadState(stream, sname);
        s.loadHeader();
        if (chunkname == null) {
            return null;
        }
        switch (s.luacNumberFormat) {
            case 0:
            case 1:
            case 4:
                return fillSource(s.loadFunction(), LuaString.valueOf(sname));
            case 2:
            case 3:
            default:
                throw new LuaError("unsupported int size: " + s.luacNumberFormat);
        }
    }

    private static Prototype fillSource(Prototype p, LuaString def) {
        if (p.source == null) {
            p.source = def;
        }
        for (int i = 0; i < p.p.length; i++) {
            fillSource(p.p[i], p.source);
        }
        return p;
    }

    public static String getSourceName(String name) {
        if (name.startsWith("@") || name.startsWith("=")) {
            String sname = name.substring(1);
            return sname;
        }
        if (!name.startsWith("\u001b")) {
            return name;
        }
        return SOURCE_BINARY_STRING;
    }

    private LoadState(InputStream stream, String name) {
        this.name = name;
        this.is = new DataInputStream(stream);
    }

    private static final class GlobalsUndumper implements Globals.Undumper {
        private GlobalsUndumper() {
        }

        /* synthetic */ GlobalsUndumper(GlobalsUndumper globalsUndumper) {
            this();
        }

        @Override // luaj.Globals.Undumper
        public Prototype undump(InputStream stream, String chunkname) throws IOException {
            return LoadState.undump(stream, chunkname);
        }
    }
}
