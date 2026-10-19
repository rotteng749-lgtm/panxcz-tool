package luaj.lib;

import android.ext.ListManager;
import android.ext.Log;
import android.ext.Message;
import android.ext.Tools;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import lasm.LasmConstants;
import luaj.Buffer;
import luaj.Globals;
import luaj.LuaError;
import luaj.LuaTable;
import luaj.LuaValue;
import luaj.Varargs;

/* JADX INFO: loaded from: classes.dex */
public class OsLib extends TwoArgFunction {
    private static final int CLOCK = 0;
    private static final int DATE = 1;
    private static final int DIFFTIME = 2;
    private static final int EXECUTE = 3;
    private static final int EXIT = 4;
    private static final int GETENV = 5;
    private static final int REMOVE = 6;
    private static final int RENAME = 7;
    private static final int SETLOCALE = 8;
    private static final int TIME = 9;
    private static final int TMPNAME = 10;
    public static final String TMP_PREFIX = ".gg.";
    public static final String TMP_SUFFIX = ".tmp";
    private static final long t0 = System.currentTimeMillis();
    private static long tmpnames = t0;
    private Globals globals;

    @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue modname, LuaValue env) {
        String[] names = {"clock", "date", "difftime", "execute", "exit", "getenv", "remove", "rename", "setlocale", "time", "tmpname"};
        this.globals = env.checkglobals();
        LuaTable os = new LuaTable();
        boolean full = this.globals.fullVersion;
        for (int opcode = 0; opcode < names.length; opcode++) {
            if (!full) {
                switch (opcode) {
                    case 3:
                    case 4:
                    case 6:
                    case 7:
                    case 10:
                        break;
                    default:
                        os.set(names[opcode], new OsLibFunc(opcode, names[opcode]));
                        break;
                }
            } else {
                os.set(names[opcode], new OsLibFunc(opcode, names[opcode]));
            }
        }
        env.set("os", os);
        LuaValue pkg = env.get("package");
        if (!pkg.isnil()) {
            LuaValue loaded = pkg.get("loaded");
            loaded.set("os", os);
        }
        return os;
    }

    class OsLibFunc extends VarArgFunction {
        public OsLibFunc(int opcode, String name) {
            this.opcode = opcode;
            this.name = name;
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            try {
                switch (this.opcode) {
                    case 0:
                        return valueOf(OsLib.this.clock());
                    case 1:
                        String s = args.optjstring(1, "%c");
                        long t = args.isnumber(2) ? args.tolong(2) : OsLib.this.time(null);
                        if (t == -1) {
                            t = OsLib.this.time(null);
                        }
                        if (t > 9223372036854775L || t < -9223372036854775L) {
                            throw new LuaError("time result cannot be represented in this installation");
                        }
                        Calendar d = Calendar.getInstance(s.startsWith("!") ? TimeZone.getTimeZone("UTC") : TimeZone.getDefault());
                        d.setTimeInMillis(1000 * t);
                        if (s.startsWith("!")) {
                            s = s.substring(1);
                        }
                        if (s.equals("*t")) {
                            LuaTable tbl = LuaValue.tableOf();
                            OsLib.this.fillTime(tbl, d);
                            return tbl;
                        }
                        return valueOf(OsLib.this.date(s, d));
                    case 2:
                        return valueOf(OsLib.this.difftime(args.checkdouble(1), args.checkdouble(2)));
                    case 3:
                        return OsLib.this.execute(args.optjstring(1, null));
                    case 4:
                        OsLib.this.exit(args.optint(1, 0));
                        return NONE;
                    case 5:
                        String val = OsLib.this.getenv(args.checkjstring(1));
                        return val != null ? valueOf(val) : NIL;
                    case 6:
                        OsLib.this.remove(args.checkjstring(1));
                        return LuaValue.TRUE;
                    case 7:
                        OsLib.this.rename(args.checkjstring(1), args.checkjstring(2));
                        return LuaValue.TRUE;
                    case 8:
                        String s2 = OsLib.this.setlocale(args.optjstring(1, null), args.optjstring(2, "all"));
                        return s2 != null ? valueOf(s2) : NIL;
                    case 9:
                        return valueOf(OsLib.this.time(args.opttable(1, null)));
                    case 10:
                        return valueOf(OsLib.this.tmpname());
                    default:
                        return NONE;
                }
            } catch (IOException e) {
                return varargsOf(NIL, valueOf(e.getMessage()), valueOf(this.opcode));
            }
        }
    }

    void fillTime(LuaTable tbl, Calendar d) {
        tbl.set("year", LuaValue.valueOf(d.get(1)));
        tbl.set("month", LuaValue.valueOf(d.get(2) + 1));
        tbl.set("day", LuaValue.valueOf(d.get(5)));
        tbl.set("hour", LuaValue.valueOf(d.get(11)));
        tbl.set("min", LuaValue.valueOf(d.get(12)));
        tbl.set("sec", LuaValue.valueOf(d.get(13)));
        tbl.set("wday", LuaValue.valueOf(d.get(7)));
        tbl.set("yday", LuaValue.valueOf(d.get(6)));
        tbl.set("isdst", LuaValue.valueOf(d.getTimeZone().inDaylightTime(d.getTime())));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public double clock() {
        return ((System.currentTimeMillis() - t0) - ((long) this.globals.logMillis)) / 1000.0d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public double difftime(double t2, double t1) {
        return t2 - t1;
    }

    private static LuaValue invconv(byte[] fmt, int off, int i) {
        return argerror(1, "os.date", "invalid conversion specifier '" + valueOf(fmt, off, i - off) + "' at offset " + off);
    }

    public String date(String format, Calendar d) {
        int i;
        byte[] fmt = format.getBytes();
        int n = fmt.length;
        Buffer result = new Buffer(n);
        int i2 = 0;
        while (i2 < n) {
            int off = i2;
            int i3 = i2 + 1;
            byte c = fmt[i2];
            switch (c) {
                case 10:
                    result.append(ListManager.NEW_LINE);
                    i2 = i3;
                    break;
                case 37:
                    if (i3 >= n) {
                        invconv(fmt, off, i3);
                    }
                    byte set = 0;
                    int i4 = i3 + 1;
                    byte c2 = fmt[i3];
                    if (c2 == 69 || c2 == 79) {
                        if (i4 >= n) {
                            invconv(fmt, off, i4);
                        }
                        String allow = c2 == 69 ? "cCxXyY" : "deHImMSuUVwwy";
                        i = i4 + 1;
                        c2 = fmt[i4];
                        if (allow.indexOf((char) (c2 & 255)) == -1) {
                            invconv(fmt, off, i);
                        }
                    } else {
                        i = i4;
                    }
                    switch (c2) {
                        case 38:
                        case 39:
                        case 40:
                        case 41:
                        case 42:
                        case 43:
                        case 44:
                        case 45:
                        case 46:
                        case 47:
                        case LasmConstants.SHL /* 48 */:
                        case LasmConstants.SHR /* 49 */:
                        case 50:
                        case LasmConstants.PROTO /* 51 */:
                        case LasmConstants.GOTO /* 52 */:
                        case LasmConstants.OP /* 53 */:
                        case LasmConstants.SET_TOP /* 54 */:
                        case LasmConstants.SKIP_NEXT /* 55 */:
                        case LasmConstants.NIL /* 56 */:
                        case LasmConstants.TRUE /* 57 */:
                        case LasmConstants.FALSE /* 58 */:
                        case 59:
                        case 60:
                        case 61:
                        case 62:
                        case 63:
                        case 64:
                        case LasmConstants.LINE /* 69 */:
                        case LasmConstants.EOL /* 74 */:
                        case LasmConstants.RANGE /* 75 */:
                        case LasmConstants.LABEL /* 76 */:
                        case LasmConstants.U /* 78 */:
                        case LasmConstants.INT /* 79 */:
                        case LasmConstants.HEX /* 80 */:
                        case LasmConstants.STRING /* 81 */:
                        case 91:
                        case 92:
                        case 93:
                        case 94:
                        case 95:
                        case 96:
                        case 102:
                        case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                        case 111:
                        case 113:
                        case 118:
                        default:
                            invconv(fmt, off, i);
                        case 37:
                            result.append("%");
                            break;
                        case LasmConstants.NUMPARAMS /* 65 */:
                        case LasmConstants.IS_VARARG /* 66 */:
                        case LasmConstants.UPVAL /* 68 */:
                        case LasmConstants.LOCAL /* 70 */:
                        case LasmConstants.FUNC /* 72 */:
                        case LasmConstants.END /* 73 */:
                        case LasmConstants.V /* 77 */:
                        case 82:
                        case LasmConstants.NL /* 83 */:
                        case LasmConstants.FLOAT /* 84 */:
                        case 89:
                        case 90:
                        case 97:
                        case 98:
                        case Message.ERROR_FATAL /* 100 */:
                        case Message.ERROR_EPERM_ON_ATTACH_DEFEND /* 106 */:
                        case Message.ERROR_SPEEDHACK_FAIL /* 107 */:
                        case Message.ERROR_GROUP_FAIL /* 108 */:
                        case 109:
                        case 112:
                        case 114:
                        case 115:
                        case 121:
                        case 122:
                            set = c2;
                            break;
                        case LasmConstants.MAXSTACKSIZE /* 67 */:
                            result.append(String.valueOf((d.get(1) / 100) + 100).substring(1));
                            break;
                        case LasmConstants.END_LOCAL /* 71 */:
                            result.append(String.valueOf(isoYear(d) + 10000).substring(1));
                            break;
                        case LasmConstants.DIGIT /* 85 */:
                            result.append(String.valueOf(usaWeek(d, false) + 100).substring(1));
                            break;
                        case LasmConstants.EXP /* 86 */:
                            result.append(String.valueOf(isoWeek(d) + 100).substring(1));
                            break;
                        case LasmConstants.HEXDIGIT /* 87 */:
                            result.append(String.valueOf(usaWeek(d, true) + 100).substring(1));
                            break;
                        case 88:
                            set = 84;
                            break;
                        case 99:
                            result.append(date("%a %b %d %H:%M:%S %Y", d));
                            break;
                        case 101:
                            int day = d.get(5);
                            if (day < 10) {
                                result.append(" ");
                            }
                            result.append(String.valueOf(day));
                            break;
                        case 103:
                            result.append(String.valueOf(isoYear(d) + 10000).substring(3));
                            break;
                        case 104:
                            set = 98;
                            break;
                        case 110:
                            result.append(ListManager.NEW_LINE);
                            break;
                        case 116:
                            result.append("\t");
                            break;
                        case 117:
                            result.append(String.valueOf(((d.get(7) + 5) % 7) + 1));
                            break;
                        case 119:
                            result.append(String.valueOf((d.get(7) + 6) % 7));
                            break;
                        case 120:
                            set = 68;
                            break;
                    }
                    if (set != 0) {
                        result.append(String.format(Locale.US, "%" + (set == 112 ? 'T' : 't') + ((char) (set & 255)), d));
                    }
                    i2 = i;
                    break;
                default:
                    result.append(c);
                    i2 = i3;
                    break;
            }
        }
        return result.tojstring();
    }

    private Calendar isoThursday(Calendar d) {
        int doy = d.get(6);
        int dow = d.get(7);
        Calendar c = Calendar.getInstance();
        c.setLenient(true);
        c.set(1, d.get(1));
        c.set(6, (doy - (((dow + 5) % 7) + 1)) + 4);
        return c;
    }

    private int isoWeek(Calendar d) {
        Calendar c = isoThursday(d);
        int week = ((c.get(6) - 1) / 7) + 1;
        return week;
    }

    private int isoYear(Calendar d) {
        Calendar c = isoThursday(d);
        return c.get(1);
    }

    private int usaWeek(Calendar d, boolean monday) {
        int doy = d.get(6);
        int dow = d.get(7);
        if (monday) {
            dow = ((dow + 5) % 7) + 1;
        }
        if (doy <= dow) {
            return 0;
        }
        int firstWeek = (doy - dow) % 7;
        int week = ((int) Math.ceil((doy - firstWeek) / 7.0f)) - 1;
        return firstWeek != 0 ? week + 1 : week;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Varargs execute(String command) {
        throw new LuaError("Failed os.execute('" + command + "'): disabled by security reason");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void exit(int code) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getenv(String varname) {
        if (varname == null || varname.length() == 0) {
            return null;
        }
        String s = null;
        try {
            s = System.getenv(varname);
        } catch (Throwable e) {
            Log.d("Failed getenv '" + varname + "'", e);
        }
        if (s == null) {
            try {
                String s2 = System.getProperty(varname);
                return s2;
            } catch (Throwable e2) {
                Log.d("Failed getProperty '" + varname + "'", e2);
                return s;
            }
        }
        return s;
    }

    protected void remove(String filename) throws IOException {
        File f = this.globals.newFile(filename);
        if (!f.exists()) {
            throw new IOException("No such file or directory: '" + filename + "'");
        }
        if (!f.delete()) {
            throw new IOException("Failed to delete: '" + filename + "'");
        }
    }

    protected void rename(String oldname, String newname) throws IOException {
        File f = this.globals.newFile(oldname);
        if (!f.exists()) {
            throw new IOException("No such file or directory: '" + oldname + "'");
        }
        if (!f.renameTo(this.globals.newFile(newname))) {
            throw new IOException("Failed to rename '" + oldname + "' to '" + newname + "'");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String setlocale(String locale, String category) {
        return "C";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long time(LuaTable table) {
        Calendar c = Calendar.getInstance();
        if (table == null) {
            c.add(14, -this.globals.logMillis);
        } else {
            String field = "";
            try {
                c.set(1, checkTimeField(table.get("year"), true).checkint());
                c.set(2, checkTimeField(table.get("month"), true).checkint() - 1);
                c.set(5, checkTimeField(table.get("day"), true).checkint());
                c.set(11, checkTimeField(table.get("hour"), false).optint(12));
                c.set(12, checkTimeField(table.get("min"), false).optint(0));
                field = "sec";
                c.set(13, checkTimeField(table.get("sec"), false).optint(0));
                c.set(14, 0);
                fillTime(table, c);
            } catch (LuaError e) {
                throw new LuaError("field '" + field + "' " + e.getMessage(), e);
            }
        }
        return c.getTimeInMillis() / 1000;
    }

    private LuaValue checkTimeField(LuaValue field, boolean required) {
        if (field.isnil()) {
            if (required) {
                throw new LuaError("missing in date table");
            }
        } else if (!field.isintnumber()) {
            throw new LuaError("is not an integer");
        }
        return field;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String tmpname() {
        try {
            File f = File.createTempFile(TMP_PREFIX, TMP_SUFFIX);
            try {
                f.deleteOnExit();
                f.delete();
            } catch (Throwable th) {
            }
            return f.getAbsolutePath();
        } catch (IOException e) {
            synchronized (OsLib.class) {
                long num = tmpnames;
                tmpnames = 1 + num;
                String ret = TMP_PREFIX + num + TMP_SUFFIX;
                return Tools.getCacheDir() + "/" + ret;
            }
        }
    }
}
