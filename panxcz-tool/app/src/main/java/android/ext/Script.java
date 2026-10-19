package android.ext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.ext.Converter;
import android.ext.ParserNumbers;
import android.ext.ProcessList;
import android.ext.RegionList;
import android.ext.SearchButtonListener;
import android.fix.LayoutInflater;
import android.internal.LoggingPrintStream;
import android.os.Build;
import android.sup.ArrayListResults;
import android.sup.LongSparseArrayChecked;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import lasm.Lasm;
import lasm.LasmPrototype;
import luaj.Globals;
import luaj.LoadState;
import luaj.LuaBoolean;
import luaj.LuaClosure;
import luaj.LuaDouble;
import luaj.LuaError;
import luaj.LuaFunction;
import luaj.LuaLong;
import luaj.LuaNil;
import luaj.LuaNumber;
import luaj.LuaString;
import luaj.LuaTable;
import luaj.LuaThread;
import luaj.LuaValue;
import luaj.Print;
import luaj.Varargs;
import luaj.compiler.DumpState;
import luaj.compiler.LuaC;
import luaj.lib.BaseLib;
import luaj.lib.Bit32Lib;
import luaj.lib.DebugLib;
import luaj.lib.GgLib;
import luaj.lib.IoLib;
import luaj.lib.MathLib;
import luaj.lib.OsLib;
import luaj.lib.PackageLib;
import luaj.lib.StringLib;
import luaj.lib.TableLib;
import luaj.lib.TwoArgFunction;
import luaj.lib.Utf8Lib;
import luaj.lib.VarArgFunction;

/* JADX INFO: loaded from: classes.dex */
public class Script extends TwoArgFunction {
    private static final int BY_CALL = 1;
    private static final int BY_KILL_PROCESS = 2;
    private static final int BY_SET_VISIBLE = 3;
    private static final byte MAX_SCRIPTS = 5;
    private static final byte MAX_SEQ = 20;
    private static final byte SEQ_GAP = 20;
    private static final int UNKNOWN = 0;
    static DebugLog debugLog = null;
    private static final long unixTsOffset = 1546300800000L;
    private String bad1;
    private String bad2;
    private String bad3;
    Consts consts;
    private final byte id;
    volatile String notifyReason;
    final String out;
    final int params;
    volatile SavedState savedState;
    private String small;
    final Object source;
    private static volatile int scriptId = 0;
    private static WeakReference<Consts> cacheConsts = new WeakReference<>(null);
    static volatile ArrayList<AddressItem> memList = null;
    static volatile int memListUse = 0;
    static volatile long allocatedPage = 0;
    static final LuaString[] inds = {LuaString.valueOf("address"), LuaString.valueOf("value"), LuaString.valueOf("flags"), LuaString.valueOf("name"), LuaString.valueOf("freeze"), LuaString.valueOf("freezeType"), LuaString.valueOf("freezeFrom"), LuaString.valueOf("freezeTo")};
    static final int[] itemSlots = new int[3];
    static final int[] savedItemSlots = new int[8];
    private volatile int seqId = 0;
    private volatile byte seqLast = -1;
    volatile boolean resultsLoaded = true;
    volatile int interruptedReason = 0;
    final Globals globals = new Globals();
    final Thread thread = new ScriptThread();
    final Logger log = new Logger();

    static final class simp_ui extends ApiFunction {
        simp_ui() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 2;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.simp_ui() -> nil";
        }

        /* JADX WARN: Code restructure failed: missing block: B:35:0x0120, code lost:
        
            r10[r11 - 1] = r7;
            r11 = r11 + 1;
            r3 = r17;
            r2 = r19;
            r7 = r20;
            r9 = r21;
         */
        @Override // android.ext.Script.ApiFunction
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public luaj.Varargs invoke2(luaj.Varargs r26) {
            /*
                Method dump skipped, instruction units count: 514
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.ext.Script.simp_ui.invoke2(luaj.Varargs):luaj.Varargs");
        }
    }

    static {
        for (int i = 0; i < itemSlots.length; i++) {
            itemSlots[i] = LuaTable.hashSlot(inds[i], 3);
        }
        for (int i2 = 0; i2 < savedItemSlots.length; i2++) {
            savedItemSlots[i2] = LuaTable.hashSlot(inds[i2], 7);
        }
        debugLog = null;
    }

    public Script(String script, int params, String out) {
        int i = scriptId;
        scriptId = i + 1;
        this.id = (byte) (i % 5);
        this.consts = null;
        this.small = null;
        this.bad1 = null;
        this.bad2 = null;
        this.bad3 = null;
        this.notifyReason = null;
        this.savedState = null;
        this.source = script;
        this.params = params;
        this.out = out;
        init();
    }

    public Script(File file, int params, String out) {
        int i = scriptId;
        scriptId = i + 1;
        this.id = (byte) (i % 5);
        this.consts = null;
        this.small = null;
        this.bad1 = null;
        this.bad2 = null;
        this.bad3 = null;
        this.notifyReason = null;
        this.savedState = null;
        LoadState.badHeader = false;
        this.source = file;
        this.params = params;
        this.out = out;
        init();
    }

    private String getSource() {
        if (this.source instanceof File) {
            File file = (File) this.source;
            try {
                StringBuilder out = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                while (true) {
                    try {
                        String line = reader.readLine();
                        if (line != null) {
                            out.append(line);
                            out.append('\n');
                        } else {
                            reader.close();
                            return out.toString();
                        }
                    } catch (Throwable th) {
                        reader.close();
                        throw th;
                    }
                }
            } catch (Exception e) {
                return String.valueOf(e.toString()) + '\n' + Log.getStackTraceString(e);
            }
        } else {
            return this.source.toString();
        }
    }

    private void init() {
        Globals globals = this.globals;
        LoadState.install(globals);
        LuaC.install(globals);
        globals.load(new BaseLib());
        globals.load(new PackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new Utf8Lib());
        globals.load(new MathLib());
        globals.load(new IoLibSafe());
        globals.load(new OsLibSafe());
        globals.load(new DebugLib());
        initApi();
    }

    private void initApi() {
        Globals globals = this.globals;
        globals.STDOUT = new ScriptPrintStream(false);
        globals.STDERR = new ScriptPrintStream(true);
        globals.load(this);
    }

    void appendLog(boolean isErrorStream, String line) {
        if (isErrorStream) {
            this.log.append("err: ");
        }
        this.log.append(line);
        this.log.append(ListManager.NEW_LINE);
    }

    class ScriptPrintStream extends LoggingPrintStream {
        final boolean isErrorStream;

        public ScriptPrintStream(boolean isErrorStream) {
            this.isErrorStream = isErrorStream;
        }

        @Override // android.internal.LoggingPrintStream
        protected void log(String line) {
            Script.this.appendLog(this.isErrorStream, line);
            String line2 = "script: " + line;
            if (this.isErrorStream) {
                Log.e(line2);
            } else {
                Log.i(line2);
            }
        }
    }

    @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue modname, LuaValue env) {
        Context context = Tools.getContext();
        ArrayList<Const> consts = (this.params & 4) != 0 ? new ArrayList<>() : null;
        LuaTable gg = new LuaTable(0, 128);
        gg.rawset("VERSION", new StringBuilder().append((14.0f * 1) - 0.0f).toString());
        gg.rawset("VERSION_INT", Debug.getIntVersion());
        gg.rawset("BUILD", (int) (((16247.7f * 1) - 254.7f) + 0.5f));
        gg.rawset("PACKAGE", Tools.getPackageName());
        gg.rawset("FILES_DIR", Tools.getFilesDir().getAbsolutePath());
        gg.rawset("CACHE_DIR", Tools.getCacheDir().getAbsolutePath());
        try {
            gg.rawset("EXT_FILES_DIR", context.getExternalFilesDir(null).getAbsolutePath());
        } catch (Throwable e) {
            Log.e("Failed set EXT_FILES_DIR for script", e);
            gg.rawset("EXT_FILES_DIR", Tools.getFilesDir().getAbsolutePath());
        }
        try {
            gg.rawset("EXT_CACHE_DIR", context.getExternalCacheDir().getAbsolutePath());
        } catch (Throwable e2) {
            Log.e("Failed set EXT_CACHE_DIR for script", e2);
            gg.rawset("EXT_CACHE_DIR", Tools.getCacheDir().getAbsolutePath());
        }
        gg.rawset("EXT_STORAGE", Tools.getSdcardPath());
        loadConsts(gg, consts);
        gg.rawset("require", new require());
        gg.rawset("toast", new toast());
        gg.rawset("alert", new alert());
        gg.rawset("prompt", new prompt());
        gg.rawset("choice", new choice());
        gg.rawset("multiChoice", new multiChoice());
        gg.rawset("isVisible", new isVisible());
        gg.rawset("setVisible", new setVisible());
        gg.rawset("getActiveTab", new getActiveTab());
        gg.rawset("showUiButton", new showUiButton());
        gg.rawset("hideUiButton", new hideUiButton());
        gg.rawset("isClickedUiButton", new isClickedUiButton());
        ApiFunction target = new getTargetPackage();
        gg.rawset("getTargetPackage", target);
        gg.rawset("getTargetInfo", new getTargetInfo());
        gg.rawset("isPackageInstalled", new isPackageInstalled());
        gg.rawset("processKill", new processKill());
        gg.rawset("saveVariable", new saveVariable());
        gg.rawset("makeRequest", new makeRequest());
        getResultsCount getResultsCount2 = new getResultsCount();
        gg.rawset("getResultsCount", getResultsCount2);
        gg.rawset("getResults", new getResults());
        gg.rawset("editAll", new editAll());
        gg.rawset("clearResults", new clearResults());
        gg.rawset("removeResults", new removeResults());
        gg.rawset("loadResults", new loadResults());
        gg.rawset("getSelectedResults", new getSelectedResults());
        gg.rawset("setValues", new setValues());
        gg.rawset("getValues", new getValues());
        gg.rawset("getValuesRange", new getValuesRange());
        gg.rawset("processPause", new processPause());
        gg.rawset("processResume", new processResume());
        gg.rawset("processToggle", new processToggle());
        gg.rawset("isProcessPaused", new isProcessPaused());
        gg.rawset("timeJump", new timeJump());
        gg.rawset("setSpeed", new setSpeed());
        gg.rawset("getSpeed", new getSpeed());
        gg.rawset("unrandomizer", new unrandomizer());
        gg.rawset("gotoAddress", new gotoAddress());
        gg.rawset("getSelectedElements", new getSelectedElements());
        gg.rawset("getRanges", new getRanges());
        gg.rawset("setRanges", new setRanges());
        gg.rawset("copyMemory", new copyMemory());
        gg.rawset("dumpMemory", new dumpMemory());
        gg.rawset("loadList", new loadList());
        gg.rawset("saveList", new saveList());
        gg.rawset("clearList", new clearList());
        gg.rawset("addListItems", new addListItems());
        gg.rawset("getListItems", new getListItems());
        gg.rawset("removeListItems", new removeListItems());
        gg.rawset("getSelectedListItems", new getSelectedListItems());
        gg.rawset("searchNumber", new searchNumber());
        gg.rawset("refineNumber", new refineNumber());
        gg.rawset("searchAddress", new searchAddress());
        gg.rawset("refineAddress", new refineAddress());
        gg.rawset("startFuzzy", new startFuzzy());
        gg.rawset("searchFuzzy", new searchFuzzy());
        gg.rawset("sleep", new sleep());
        gg.rawset("simp_ui", new simp_ui());
        gg.rawset("copyText", new copyText());
        gg.rawset("disasm", new disasm());
        gg.rawset("getLine", new getLine());
        gg.rawset("getFile", new getFile());
        gg.rawset("bytes", new bytes());
        gg.rawset("skipRestoreState", new skipRestoreState());
        gg.rawset("getRangesList", new getRangesList());
        gg.rawset("allocatePage", new allocatePage());
        gg.rawset("getLocale", new getLocale());
        gg.rawset("numberToLocale", new numberToLocale());
        gg.rawset("numberFromLocale", new numberFromLocale());
        gg.rawset(LuaValue.valueOf("SIGN_INEQUAL").makeDeprecated(), LuaValue.valueOf(268435456L));
        gg.rawset(LuaValue.valueOf("SIGN_SMALLER").makeDeprecated(), LuaValue.valueOf(134217728L));
        gg.rawset(LuaValue.valueOf("SIGN_LARGER").makeDeprecated(), LuaValue.valueOf(67108864L));
        gg.rawset(LuaValue.valueOf("getSelectedPackage").makeDeprecated(), target);
        gg.rawset(LuaValue.valueOf("getResultCount").makeDeprecated(), getResultsCount2);
        gg.rawset(LuaValue.valueOf("internal1").makeDeprecated(), new internal1());
        gg.rawset(LuaValue.valueOf("internal2").makeDeprecated(), new internal2());
        gg.rawset(LuaValue.valueOf("internal3").makeDeprecated(), new internal3());
        env.set("gg", gg);
        env.get("package").get("loaded").set("gg", gg);
        LuaValue os = env.get("os");
        os.set("exit", new exit());
        os.set("remove", new wrap("os.remove", os.get("remove"), 1));
        os.set("rename", new wrap("os.rename", os.get("rename"), 2));
        os.set("tmpname", new wrap("os.tmpname", os.get("tmpname"), 0));
        LuaValue string = env.get("string");
        string.set("format", new format(string.get("format")));
        string.set("dump", new wrap("string.dump", string.get("dump"), 1));
        LuaValue io = env.get("io");
        io.set("open", new wrap("io.open", io.get("open"), 2));
        io.set("input", new wrap("io.input", io.get("input"), 1));
        io.set("output", new wrap("io.output", io.get("output"), 1));
        io.set("tmpfile", new wrap("io.tmpfile", io.get("tmpfile"), 0));
        io.set("lines", new wrap("io.lines", io.get("lines"), 1));
        LuaValue debug = env.get("debug");
        debug.set("debug", new wrap("debug.debug", debug.get("debug"), 0));
        debug.set("gethook", new wrap("debug.gethook", debug.get("gethook"), 1));
        debug.set("getinfo", new wrap("debug.getinfo", debug.get("getinfo"), 3));
        debug.set("getlocal", new wrap("debug.getlocal", debug.get("getlocal"), 3));
        debug.set("getmetatable", new wrap("debug.getmetatable", debug.get("getmetatable"), 1));
        debug.set("getregistry", new wrap("debug.getregistry", debug.get("getregistry"), 0));
        debug.set("getupvalue", new wrap("debug.getupvalue", debug.get("getupvalue"), 2));
        debug.set("getuservalue", new wrap("debug.getuservalue", debug.get("getuservalue"), 1));
        debug.set("sethook", new wrap("debug.sethook", debug.get("sethook"), 4));
        debug.set("setlocal", new wrap("debug.setlocal", debug.get("setlocal"), 4));
        debug.set("setmetatable", new wrap("debug.setmetatable", debug.get("setmetatable"), 2));
        debug.set("setupvalue", new wrap("debug.setupvalue", debug.get("setupvalue"), 3));
        debug.set("setuservalue", new wrap("debug.setuservalue", debug.get("setuservalue"), 2));
        debug.set("traceback", new wrap("debug.traceback", debug.get("traceback"), 3));
        debug.set("upvalueid", new wrap("debug.upvalueid", debug.get("upvalueid"), 2));
        debug.set("upvaluejoin", new wrap("debug.upvaluejoin", debug.get("upvaluejoin"), 4));
        this.consts = consts != null ? getConsts(consts) : new Consts();
        return gg;
    }

    static void loadConsts(LuaTable gg, ArrayList<Const> consts) {
        setConst(consts, gg, "TYPE_AUTO", AddressItem.FLAG_AUTO);
        setConst(consts, gg, "TYPE_BYTE", 1);
        setConst(consts, gg, "TYPE_WORD", 2);
        setConst(consts, gg, "TYPE_DWORD", 4);
        setConst(consts, gg, "TYPE_XOR", 8);
        setConst(consts, gg, "TYPE_FLOAT", 16);
        setConst(consts, gg, "TYPE_QWORD", 32);
        setConst(consts, gg, "TYPE_DOUBLE", 64);
        setConst(consts, gg, "SIGN_EQUAL", AddressItem.FLAG_VALUE_EQUAL);
        setConst(consts, gg, "SIGN_NOT_EQUAL", AddressItem.FLAG_VALUE_NOT_EQUAL);
        setConst(consts, gg, "SIGN_LESS_OR_EQUAL", 134217728);
        setConst(consts, gg, "SIGN_GREATER_OR_EQUAL", AddressItem.FLAG_VALUE_GREATER_OR_EQUAL);
        setConst(consts, gg, "SIGN_FUZZY_EQUAL", AddressItem.FLAG_VALUE_EQUAL);
        setConst(consts, gg, "SIGN_FUZZY_NOT_EQUAL", AddressItem.FLAG_VALUE_NOT_EQUAL);
        setConst(consts, gg, "SIGN_FUZZY_LESS", 134217728);
        setConst(consts, gg, "SIGN_FUZZY_GREATER", AddressItem.FLAG_VALUE_GREATER_OR_EQUAL);
        setConst(consts, gg, "REGION_JAVA_HEAP", 2);
        setConst(consts, gg, "REGION_C_HEAP", 1);
        setConst(consts, gg, "REGION_C_ALLOC", 4);
        setConst(consts, gg, "REGION_C_DATA", 8);
        setConst(consts, gg, "REGION_C_BSS", 16);
        setConst(consts, gg, "REGION_PPSSPP", 262144);
        setConst(consts, gg, "REGION_ANONYMOUS", 32);
        setConst(consts, gg, "REGION_JAVA", 65536);
        setConst(consts, gg, "REGION_STACK", 64);
        setConst(consts, gg, "REGION_ASHMEM", Config.STATE_ASHMEM);
        setConst(consts, gg, "REGION_VIDEO", Config.STATE_VIDEO);
        setConst(consts, gg, "REGION_OTHER", Config.STATE_OTHER);
        setConst(consts, gg, "REGION_BAD", 131072);
        setConst(consts, gg, "REGION_CODE_APP", 16384);
        setConst(consts, gg, "REGION_CODE_SYS", 32768);
        setConst(consts, gg, "LOAD_VALUES_FREEZE", 3);
        setConst(consts, gg, "LOAD_VALUES", 2);
        setConst(consts, gg, "LOAD_APPEND", 8);
        setConst(consts, gg, "SAVE_AS_TEXT", 1);
        gg.rawset("FREEZE_NORMAL", 0);
        gg.rawset("FREEZE_MAY_INCREASE", 1);
        gg.rawset("FREEZE_MAY_DECREASE", 2);
        gg.rawset("FREEZE_IN_RANGE", 3);
        gg.rawset("PROT_NONE", 0);
        setConst(consts, gg, "PROT_READ", 2);
        setConst(consts, gg, "PROT_WRITE", 1);
        setConst(consts, gg, "PROT_EXEC", 4);
        setConst(consts, gg, "POINTER_NO", 4);
        setConst(consts, gg, "POINTER_READ_ONLY", 8);
        setConst(consts, gg, "POINTER_WRITABLE", 16);
        setConst(consts, gg, "POINTER_EXECUTABLE", 2);
        setConst(consts, gg, "POINTER_EXECUTABLE_WRITABLE", 1);
        setConst(consts, gg, "DUMP_SKIP_SYSTEM_LIBS", 1);
        setConst(consts, gg, "TAB_SETTINGS", 0);
        setConst(consts, gg, "TAB_SEARCH", 1);
        setConst(consts, gg, "TAB_SAVED_LIST", 2);
        setConst(consts, gg, "TAB_MEMORY_EDITOR", 3);
        setConst(consts, gg, "ASM_ARM", 4);
        setConst(consts, gg, "ASM_THUMB", 5);
        setConst(consts, gg, "ASM_ARM64", 6);
    }

    public static Consts getConsts(ArrayList<Const> src) {
        Consts consts = cacheConsts.get();
        if (consts == null) {
            if (src == null) {
                src = new ArrayList<>();
                LuaTable gg = new LuaTable(0, 64);
                loadConsts(gg, src);
            }
            ArrayList<Const> tREGION_ = new ArrayList<>();
            ArrayList<Const> tTYPE_ = new ArrayList<>();
            ArrayList<Const> tSIGN_FUZZY_ = new ArrayList<>();
            ArrayList<Const> tSIGN_ = new ArrayList<>();
            ArrayList<Const> tLOAD_ = new ArrayList<>();
            ArrayList<Const> tSAVE_ = new ArrayList<>();
            ArrayList<Const> tPROT_ = new ArrayList<>();
            ArrayList<Const> tPOINTER_ = new ArrayList<>();
            ArrayList<Const> tDUMP_ = new ArrayList<>();
            ArrayList<Const> tASM_ = new ArrayList<>();
            for (Const const_ : src) {
                String name = const_.name;
                if (name.startsWith("gg.REGION_")) {
                    tREGION_.add(const_);
                } else if (name.startsWith("gg.TYPE_")) {
                    tTYPE_.add(const_);
                } else if (name.startsWith("gg.SIGN_FUZZY_")) {
                    tSIGN_FUZZY_.add(const_);
                } else if (name.startsWith("gg.SIGN_")) {
                    tSIGN_.add(const_);
                } else if (name.startsWith("gg.LOAD_")) {
                    tLOAD_.add(const_);
                } else if (name.startsWith("gg.SAVE_")) {
                    tSAVE_.add(const_);
                } else if (name.startsWith("gg.PROT_")) {
                    tPROT_.add(const_);
                } else if (name.startsWith("gg.POINTER_")) {
                    tPOINTER_.add(const_);
                } else if (name.startsWith("gg.DUMP_")) {
                    tDUMP_.add(const_);
                } else if (name.startsWith("gg.ASM_")) {
                    tASM_.add(const_);
                }
            }
            Consts consts2 = new Consts();
            consts2.REGION_ = (Const[]) tREGION_.toArray(new Const[tREGION_.size()]);
            consts2.TYPE_ = (Const[]) tTYPE_.toArray(new Const[tTYPE_.size()]);
            consts2.SIGN_FUZZY_ = (Const[]) tSIGN_FUZZY_.toArray(new Const[tSIGN_FUZZY_.size()]);
            consts2.SIGN_ = (Const[]) tSIGN_.toArray(new Const[tSIGN_.size()]);
            consts2.LOAD_ = (Const[]) tLOAD_.toArray(new Const[tLOAD_.size()]);
            consts2.SAVE_ = (Const[]) tSAVE_.toArray(new Const[tSAVE_.size()]);
            consts2.PROT_ = (Const[]) tPROT_.toArray(new Const[tPROT_.size()]);
            consts2.POINTER_ = (Const[]) tPOINTER_.toArray(new Const[tPOINTER_.size()]);
            consts2.DUMP_ = (Const[]) tDUMP_.toArray(new Const[tDUMP_.size()]);
            consts2.ASM_ = (Const[]) tASM_.toArray(new Const[tASM_.size()]);
            cacheConsts = new WeakReference<>(consts2);
            return consts2;
        }
        return consts;
    }

    static class Consts {
        Const[] REGION_ = null;
        Const[] TYPE_ = null;
        Const[] SIGN_FUZZY_ = null;
        Const[] SIGN_ = null;
        Const[] LOAD_ = null;
        Const[] SAVE_ = null;
        Const[] PROT_ = null;
        Const[] POINTER_ = null;
        Const[] DUMP_ = null;
        Const[] ASM_ = null;

        Consts() {
        }

        static boolean logConst(Writer log, Const[] list, int v) {
            boolean empty = true;
            try {
                for (Const c : list) {
                    int b = c.value;
                    if ((v & b) == b) {
                        log.write(c.name);
                        v &= b ^ (-1);
                        if (v != 0) {
                            log.write(" | ");
                        }
                        empty = false;
                    }
                }
                if (empty || v != 0) {
                    log.write(Integer.toString(v));
                }
                return true;
            } catch (IOException e) {
                Log.w("Writer fail", e);
                return false;
            }
        }

        static boolean logHex(Writer log, long v) {
            try {
                if (v == 0 || v == -1) {
                    log.write(Long.toString(v));
                } else {
                    log.write("0x");
                    log.write(Long.toHexString(v));
                }
                return true;
            } catch (IOException e) {
                Log.w("Writer fail", e);
                return false;
            }
        }

        static boolean logString(Writer log, String v) {
            try {
                log.write("\"");
                if (v.length() <= 65535) {
                    v = v.replace("\"", "\\\"");
                }
                log.write(v);
                log.write("\"");
                return true;
            } catch (IOException e) {
                Log.w("Writer fail", e);
                return false;
            }
        }

        static boolean logNumberString(Writer log, String v) {
            return logString(log, Script.numberToLua(v));
        }
    }

    private static void setConst(ArrayList<Const> consts, LuaTable gg, String name, int value) {
        gg.rawset(name, value);
        if (consts != null) {
            consts.add(new Const("gg." + name, value));
        }
    }

    static class Const {
        final String name;
        final int value;

        public Const(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    static String getPath(File file) {
        try {
            String path = file.getCanonicalPath();
            return path;
        } catch (IOException e) {
            String path2 = file.getAbsolutePath();
            return path2;
        }
    }

    boolean isBadFile(String filename, boolean read) {
        File file = new File(filename);
        String check = getPath(file);
        String bad = this.bad1;
        if (bad == null) {
            bad = getPath(Tools.getFilesDirHidden());
            this.bad1 = bad;
        }
        if (check.startsWith(bad)) {
            return true;
        }
        String bad2 = this.bad2;
        if (bad2 == null) {
            bad2 = getPath(Tools.getCacheDirHidden());
            this.bad2 = bad2;
        }
        if (check.startsWith(bad2)) {
            return true;
        }
        String bad3 = this.bad3;
        if (bad3 == null) {
            bad3 = getPath(Uninstaller.getSharedPrefsFile(MainService.DEFAULT_STORAGE));
            this.bad3 = bad3;
        }
        if (bad3.startsWith(check) && (bad3.length() == check.length() || bad3.charAt(check.length()) == '/')) {
            return true;
        }
        if (read || !(this.source instanceof File)) {
            return false;
        }
        String bad4 = this.small;
        if (bad4 == null) {
            bad4 = getPath((File) this.source);
            this.small = bad4;
        }
        return check.equals(bad4);
    }

    final class IoLibSafe extends IoLib {
        IoLibSafe() {
        }

        @Override // luaj.lib.IoLib
        protected IoLib.File openFile(String filename, char mode) throws IOException {
            if (Script.this.isBadFile(filename, mode == 'r')) {
                java.io.File empty = new java.io.File(Tools.getFilesDirHidden(), "empty.txt");
                empty.createNewFile();
                IoLib.File ret = super.openFile(empty.getAbsolutePath(), mode);
                empty.delete();
                return ret;
            }
            IoLib.File ret2 = super.openFile(filename, mode);
            return ret2;
        }
    }

    final class OsLibSafe extends OsLib {
        OsLibSafe() {
        }

        @Override // luaj.lib.OsLib
        protected void remove(String filename) throws IOException {
            if (!Script.this.isBadFile(filename, false)) {
                super.remove(filename);
            }
        }

        @Override // luaj.lib.OsLib
        protected void rename(String oldname, String newname) throws IOException {
            if (!Script.this.isBadFile(oldname, false) && !Script.this.isBadFile(newname, false)) {
                super.rename(oldname, newname);
            }
        }
    }

    final class wrap extends VarArgFunction {
        final LuaValue base;
        final int maxArgs;
        final String name;

        wrap(String name, LuaValue base, int maxArgs) {
            this.base = base;
            this.name = name;
            this.maxArgs = maxArgs;
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            DebugLog dbg = Script.debugLog;
            Writer log = dbg == null ? null : dbg.log;
            if (log != null) {
                long start = System.nanoTime();
                try {
                    log.write(this.name);
                    log.write("(");
                    int n = args.narg();
                    int m = this.maxArgs;
                    if (m >= 0 && n > m) {
                        n = m;
                    }
                    for (int i = 1; i <= n; i++) {
                        if (i != 1) {
                            log.write(", ");
                        }
                        ApiFunction.logArg_(null, log, i, args.arg(i));
                    }
                    log.write(")\n");
                } catch (Throwable e) {
                    Log.w("Failed write log for " + this.name, e);
                }
                Globals globals = dbg.globals;
                globals.logMillis = (int) (((long) globals.logMillis) + ((System.nanoTime() - start) / 1000000));
            }
            Varargs ret = this.base.invoke(args);
            return ret;
        }

        @Override // luaj.lib.LibFunction, luaj.LuaFunction, luaj.LuaValue, luaj.Varargs
        public String tojstring() {
            return this.base.tojstring();
        }
    }

    static final class format extends VarArgFunction {
        final LuaValue base;

        public format(LuaValue base) {
            this.base = base;
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            String fmt = Re.s(args.checkjstring(1));
            return this.base.invoke(LuaValue.varargsOf(LuaValue.valueOf(fmt.replace("%,d", "%d")), args.subargs(2)));
        }
    }

    static void waitNotify(Object o) {
        try {
            o.wait();
        } catch (InterruptedException e) {
            Log.w("Interrupted wait", e);
            Thread.currentThread().interrupt();
            throw new LuaError(e);
        }
    }

    public void callNotify(byte seq, String reason) {
        byte s = (byte) (seq - 20);
        byte nId = (byte) (s / Message.CMD_SC_MEMORY_ITEM);
        byte nSeq = (byte) (s % Message.CMD_SC_MEMORY_ITEM);
        if (this.id != nId) {
            Log.d("callNotify id mismatch: " + ((int) this.id) + " != " + ((int) nId) + ListManager.TEXT_SEPARATOR + ((int) nSeq) + ListManager.TEXT_SEPARATOR + ((int) seq) + ListManager.TEXT_SEPARATOR + reason);
            return;
        }
        byte seqL = this.seqLast;
        if (seqL == nSeq) {
            synchronized (this) {
                seqL = this.seqLast;
                if (seqL == nSeq) {
                    this.notifyReason = reason;
                    notifyAll();
                    return;
                }
            }
        }
        Log.d("callNotify seq mismatch: " + ((int) seqL) + " != " + ((int) nSeq) + ListManager.TEXT_SEPARATOR + ((int) nId) + ListManager.TEXT_SEPARATOR + ((int) seq) + ListManager.TEXT_SEPARATOR + reason);
    }

    byte getSeq() {
        int i = this.seqId;
        this.seqId = i + 1;
        byte seq = (byte) (i % 20);
        this.seqLast = seq;
        byte ret = (byte) ((this.id * Message.CMD_SC_MEMORY_ITEM) + seq + 20);
        return ret;
    }

    public static abstract class ApiFunction extends VarArgFunction {
        private String lazyName;
        static WeakReference<Pattern> wpParams = new WeakReference<>(null);
        static WeakReference<Pattern> wpParam = new WeakReference<>(null);
        private static Map<LuaValue, LuaValue> map1 = null;
        private static Map<LuaValue, LuaValue> map2 = null;
        private static Map<LuaValue, LuaValue> map3 = null;
        private static Map<LuaValue, LuaValue> map4 = null;
        private final boolean busyApiFunction = this instanceof BusyApiFunction;
        private volatile Varargs ret = null;
        private volatile Throwable e = null;
        protected volatile Object monitor = new Object();

        protected abstract int getMaxArgs();

        abstract String usage();

        @Override // luaj.LuaFunction
        public String name() {
            String name = this.lazyName;
            if (name == null) {
                String name2 = super.name();
                this.lazyName = name2;
                return name2;
            }
            return name;
        }

        @Override // luaj.lib.LibFunction, luaj.LuaFunction, luaj.LuaValue, luaj.Varargs
        public String tojstring() {
            String usage = usage();
            String func = usage.replace("[", "").replace("]", "").replace(" ,", ",");
            Pattern p = wpParams.get();
            if (p == null) {
                p = Pattern.compile(".*(\\(.*\\)).*");
                wpParams = new WeakReference<>(p);
            }
            String func2 = p.matcher(func).replaceAll("$1");
            Pattern p2 = wpParam.get();
            if (p2 == null) {
                p2 = Pattern.compile("(?<=[\\(,])( ?)\\s*\\S*\\s*(\\S*)(\\s*=\\s*[^,\\)]*)?(?=[,\\)])");
                wpParam = new WeakReference<>(p2);
            }
            return "function " + p2.matcher(func2).replaceAll("$1$2") + " end, -- " + usage;
        }

        static boolean logArg_(ApiFunction self, Writer log, int i, LuaValue arg) throws IOException {
            if (arg instanceof LuaString) {
                return Consts.logString(log, arg.tojstring());
            }
            if (arg instanceof LuaTable) {
                Map<LuaValue, LuaValue> map = null;
                if (self != null && i == 1) {
                    if ((self instanceof removeResults) || (self instanceof loadResults) || (self instanceof getValues)) {
                        map = map1;
                        if (map == null) {
                            map = getMap(5);
                            map1 = map;
                        }
                    } else if (self instanceof setValues) {
                        map = map2;
                        if (map == null) {
                            map = getMap(7);
                            map2 = map;
                        }
                    } else if ((self instanceof getValuesRange) || (self instanceof removeListItems)) {
                        map = map3;
                        if (map == null) {
                            map = getMap(1);
                            map3 = map;
                        }
                    } else if ((self instanceof addListItems) && (map = map4) == null) {
                        map = getMap(255);
                        map4 = map;
                    }
                }
                ((LuaTable) arg).tojstring(log, map);
                return true;
            }
            log.write(arg.tojstring());
            return true;
        }

        private static Map<LuaValue, LuaValue> getMap(int mask) {
            Map<LuaValue, LuaValue> map = new HashMap<>();
            for (int k = 0; k < 8; k++) {
                if (((1 << k) & mask) != 0) {
                    map.put(Script.inds[k], NIL);
                }
            }
            return map;
        }

        protected boolean logArg(Writer log, int i, LuaValue arg) throws IOException {
            return logArg_(this, log, i, arg);
        }

        protected static boolean logConst(Writer log, Const[] list, LuaValue arg) throws IOException {
            return Consts.logConst(log, list, arg.toint());
        }

        protected static boolean logHex(Writer log, LuaValue arg) throws IOException {
            return Consts.logHex(log, arg.tolong());
        }

        private void logArgs(Writer log, Varargs args) throws IOException {
            int n = args.narg();
            int m = getMaxArgs();
            if (m >= 0 && n > m) {
                n = m;
            }
            for (int i = 1; i <= n; i++) {
                if (i != 1) {
                    log.write(", ");
                }
                if (!logArg(log, i, args.arg(i))) {
                    return;
                }
            }
        }

        protected void logInvoke(Writer log, Varargs args) throws IOException {
            if (!(this instanceof isVisible) && !(this instanceof internal1) && !(this instanceof internal2) && !(this instanceof sleep)) {
                log.write(name());
                log.write("(");
                logArgs(log, args);
                log.write(")\n");
            }
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public final Varargs invoke(Varargs args) {
            ThreadLocal<VarArgFunction> curFunc = Varargs.curFunc;
            VarArgFunction prevFunc = curFunc.get();
            curFunc.set(this);
            try {
                LuaValue first = args.arg1();
                if ((first instanceof LuaString) && first.rawlen() > 1000000) {
                    args = varargsOf(first.checkstring().substring(0, 1000000), args.subargs(2));
                }
                DebugLog log = Script.debugLog;
                if (log != null) {
                    long start = System.nanoTime();
                    try {
                        logInvoke(log.log, args);
                    } catch (Throwable e) {
                        Log.w("Failed write log for " + name(), e);
                    }
                    Globals globals = log.globals;
                    globals.logMillis = (int) (((long) globals.logMillis) + ((System.nanoTime() - start) / 1000000));
                }
                Varargs ret;
                try {
                    ret = invoke2(args);
                } catch (Throwable e5) {
                    throw new LuaError(e5);
                }
                if (((this instanceof alert) || (this instanceof prompt) || (this instanceof choice) || (this instanceof multiChoice)) && log != null) {
                    long start2 = System.nanoTime();
                    try {
                        Writer llog = log.log;
                        llog.write("--[[ return: ");
                        llog.write(ret == null ? "null" : ret.toString());
                        llog.write(" ]]\n");
                    } catch (Throwable e2) {
                        Log.w("Failed log for " + name(), e2);
                    }
                    Globals globals2 = log.globals;
                    globals2.logMillis = (int) (((long) globals2.logMillis) + ((System.nanoTime() - start2) / 1000000));
                }
                if ((this instanceof getResults) && (ret instanceof LuaTable) && log != null) {
                    long start3 = System.nanoTime();
                    try {
                        Writer llog2 = log.log;
                        llog2.write("--[[ count: ");
                        llog2.write(Integer.toString(((LuaTable) ret).rawlen()));
                        llog2.write(" ]]\n");
                    } catch (Throwable e3) {
                        Log.w("Failed log for " + name(), e3);
                    }
                    Globals globals3 = log.globals;
                    globals3.logMillis = (int) (((long) globals3.logMillis) + ((System.nanoTime() - start3) / 1000000));
                }
                if (ret == TRUE && (((this instanceof searchNumber) || (this instanceof refineNumber) || (this instanceof startFuzzy) || (this instanceof searchFuzzy) || (this instanceof searchAddress) || (this instanceof refineAddress)) && log != null)) {
                    long start4 = System.nanoTime();
                    try {
                        Writer llog3 = log.log;
                        llog3.write("--[[ found: ");
                        llog3.write(Long.toString(MainService.instance.mResultCount));
                        llog3.write(" ]]\n");
                    } catch (Throwable e4) {
                        Log.w("Failed log for " + name(), e4);
                    }
                    Globals globals4 = log.globals;
                    globals4.logMillis = (int) (((long) globals4.logMillis) + ((System.nanoTime() - start4) / 1000000));
                }
                return ret;
            } finally {
                curFunc.set(prevFunc);
            }
        }

        public String explain() {
            return String.valueOf(this.ret != null) + " " + (this.e != null) + " '" + getNotifyReason() + "'";
        }

        public Varargs invoke2(final Varargs args) throws Throwable {
            this.ret = null;
            this.e = null;
            Runnable run = new Runnable() { // from class: android.ext.Script.ApiFunction.1
                @Override // java.lang.Runnable
                public void run() {
                    ApiFunction.this.runUi(args);
                }
            };
            synchronized (this.monitor) {
                ThreadManager.runOnUiThread(run);
                Script.waitNotify(this.monitor);
            }
            Throwable e = this.e;
            this.e = null;
            if (e != null) {
                this.ret = null;
                if (e instanceof RuntimeException) {
                    throw ((RuntimeException) e);
                }
                throw new RuntimeException(e);
            }
            Varargs ret = this.ret;
            this.ret = null;
            if (!this.busyApiFunction || ret != null) {
                return ret;
            }
            String reason = getNotifyReason();
            return reason == null ? getTrueResult() : LuaValue.valueOf(reason);
        }

        public void runUi(Varargs args) {
            Varargs ret = null;
            Throwable e = null;
            try {
                ret = invokeUi(args);
                this.ret = ret;
            } catch (Throwable e2) {
                e = e2;
                this.e = e2;
            }
            if (!this.busyApiFunction || ret != null || e != null) {
                synchronized (this.monitor) {
                    this.monitor.notifyAll();
                }
            }
        }

        public Varargs invokeUi(Varargs args) {
            return LuaValue.NIL;
        }

        protected String getNotifyReason() {
            return null;
        }

        protected Varargs getTrueResult() {
            return LuaValue.TRUE;
        }
    }

    abstract class BusyApiFunction extends ApiFunction {
        public BusyApiFunction() {
            this.monitor = Script.this;
        }

        @Override // android.ext.Script.ApiFunction
        protected String getNotifyReason() {
            return Script.this.notifyReason;
        }
    }

    public static final class OsExit extends LuaError {
        private static final long serialVersionUID = 1;
        final int code;

        public OsExit(int code) {
            super("called os.exit(" + code + ')');
            this.code = code;
        }
    }

    static final class exit extends VarArgFunction {
        exit() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            throw new OsExit(args.optint(1, 0));
        }
    }

    final class require extends ApiFunction {
        require() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 2;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.require([string version = nil [, int build = 0]]) -> nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            String version = args.optjstring(1, "0");
            int build = args.optint(2, 0);
            int ver = Debug.getIntVersion(version);
            if (ver > 80000) {
                ver -= 80000;
            }
            if (Debug.getIntVersion() < ver || ((int) (((17509.8f * 1) - 1516.8f) + 0.5f)) < build) {
                String have = new StringBuilder().append((110.8f * 1) - 14.8f).toString();
                if (build > 0) {
                    version = String.valueOf(version) + " (" + build + ')';
                    have = String.valueOf(have) + " (" + ((int) (((19470.9f * 1) - 3477.9f) + 0.5f)) + ')';
                }
                Script.this.globals.STDOUT.println(Tools.stringFormat(Re.s(R.string.need_update_to), version, have));
                throw new OsExit(0);
            }
            return LuaValue.NIL;
        }
    }

    static final class toast extends ApiFunction {
        toast() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 2;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.toast(string text [, bool fast = false]) -> nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            final String text = String.valueOf(Tools.stripColon(R.string.script)) + ": " + args.checkjstring(1);
            final boolean fast = args.optboolean(2, false);
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Script.toast.1
                @Override // java.lang.Runnable
                public void run() {
                    Tools.showToast(Re.s(text), fast ? 0 : 1);
                }
            });
            return LuaValue.NIL;
        }
    }

    static final class alert extends ApiFunction implements DialogInterface.OnDismissListener, DialogInterface.OnClickListener {
        private volatile int result = 0;

        alert() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 4;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.alert(string text [, string positive = 'ok' [, string negative = nil [, string neutral = nil]]]) -> int: 0 = cancel, 1 = positive, 2 = negative, 3 = neutral";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            final String text = args.checkjstring(1);
            final String positive = args.optjstring(2, Re.s(R.string.ok));
            final String negative = args.optjstring(3, null);
            final String neutral = args.optjstring(4, null);
            this.result = 0;
            synchronized (this) {
                ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Script.alert.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AlertDialog.Builder builder = Alert.create().setCustomTitle(Tools.getCustomTitle(Tools.stripColon(R.string.script))).setMessage(Re.s(text)).setPositiveButton(Re.s(positive), alert.this);
                        if (negative != null) {
                            builder.setNegativeButton(Re.s(negative), alert.this);
                        }
                        if (neutral != null) {
                            builder.setNeutralButton(Re.s(neutral), alert.this);
                        }
                        AlertDialog dialog = builder.create();
                        Alert.setOnDismissListener(dialog, alert.this);
                        Alert.show(dialog);
                        MainService.instance.eye(true);
                    }
                });
                Script.waitNotify(this);
            }
            return LuaValue.valueOf(this.result);
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            synchronized (this) {
                notify();
            }
            MainService.instance.eye(false);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            this.result = -which;
        }
    }

    final class prompt extends ApiFunction implements DialogInterface.OnDismissListener, DialogInterface.OnClickListener, View.OnFocusChangeListener {
        private volatile int result = 0;
        private volatile LuaTable ret = null;
        volatile ArrayList<TextView> edits = null;

        prompt() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 3;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.prompt(table prompts [, table defaults = {} [, table types = {} ]]) -> nil || table with keys from prompts and values from inputs";
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void remove(View v) {
            v.setVisibility(8);
            Tools.getViewForAttach(v);
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            final LuaTable prompts = args.checktable(1);
            final LuaTable defaults = args.opttable(2, null);
            final LuaTable types = args.opttable(3, null);
            this.result = 0;
            final ArrayList<TextView> edits = new ArrayList<>();
            this.edits = edits;
            this.ret = new LuaTable();
            synchronized (this) {
                ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Script.prompt.1
                    @Override // java.lang.Runnable
                    public void run() {
                        int val;
                        View view = LayoutInflater.inflateStatic(R.layout.prompt, (ViewGroup) null);
                        ViewGroup body = (ViewGroup) view.findViewById(R.id.prompt);
                        android.widget.EditText first = null;
                        android.widget.EditText last = null;
                        LuaTable.Iterator it = prompts.iterator();
                        while (it.next()) {
                            LuaValue key = it.key();
                            String prompt = Re.s(it.value().tojstring().trim());
                            LuaValue defVal = defaults == null ? LuaValue.NIL : defaults.get(key);
                            LuaValue type = types == null ? LuaValue.NIL : types.get(key);
                            View v = LayoutInflater.inflateStatic(R.layout.prompt_item, (ViewGroup) null);
                            TextView text = (TextView) v.findViewById(R.id.prompt);
                            CheckBox check = (CheckBox) v.findViewById(R.id.check);
                            android.widget.EditText edit = (android.widget.EditText) v.findViewById(R.id.edit);
                            EditTextPath path = (EditTextPath) v.findViewById(R.id.path);
                            String strType = type.tojstring();
                            View pathSelector = v.findViewById(R.id.path_selector);
                            pathSelector.setTag(path);
                            View conv = v.findViewById(R.id.number_converter);
                            conv.setTag(edit);
                            final TextView seek = (TextView) v.findViewById(R.id.seek);
                            SeekBar seekBar = (SeekBar) v.findViewById(R.id.seekbar);
                            boolean useSeekBar = false;
                            int intType = 0;
                            boolean checkbox = strType.equalsIgnoreCase("checkbox");
                            if (checkbox) {
                                prompt.this.remove(text);
                                prompt.this.remove(edit);
                                prompt.this.remove(path);
                                prompt.this.remove(pathSelector);
                                prompt.this.remove(conv);
                                check.setText(prompt);
                                check.setTag(key);
                                if (!defVal.isnil()) {
                                    check.setChecked(defVal.toboolean());
                                }
                                edits.add(check);
                            } else {
                                prompt.this.remove(check);
                                String strVal = !defVal.isnil() ? defVal.tojstring() : "";
                                if (strType.equalsIgnoreCase("number")) {
                                    try {
                                        strVal = Script.numberFromLua(strVal);
                                        if (prompt.indexOf(91) > 0 && prompt.indexOf(59) > 0 && prompt.charAt(prompt.length() - 1) == ']') {
                                            Matcher m = Pattern.compile("\\s*\\[(.+);(.+)\\]$").matcher(prompt);
                                            if (m.find()) {
                                                String strMin = Script.numberToLua(Script.numberFromLua(m.group(1).trim()));
                                                String strMax = Script.numberToLua(Script.numberFromLua(m.group(2).trim()));
                                                final int min = Integer.parseInt(strMin);
                                                int max = Integer.parseInt(strMax);
                                                strVal = Script.numberToLua(strVal.trim());
                                                try {
                                                    val = Integer.parseInt(strVal);
                                                } catch (Throwable e) {
                                                    Log.d("Failed parse default value: " + strVal, e);
                                                    val = 0;
                                                }
                                                if (min < max) {
                                                    if (val < min) {
                                                        val = min;
                                                    }
                                                    if (val > max) {
                                                        val = max;
                                                    }
                                                    seek.setText(Integer.toString(val));
                                                    seek.setTag(key);
                                                    seekBar.setMax(max - min);
                                                    seekBar.setProgress(val - min);
                                                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: android.ext.Script.prompt.1.1
                                                        @Override // android.widget.SeekBar.OnSeekBarChangeListener
                                                        public void onStopTrackingTouch(SeekBar seekBar2) {
                                                        }

                                                        @Override // android.widget.SeekBar.OnSeekBarChangeListener
                                                        public void onStartTrackingTouch(SeekBar seekBar2) {
                                                        }

                                                        @Override // android.widget.SeekBar.OnSeekBarChangeListener
                                                        public void onProgressChanged(SeekBar seekBar2, int progress, boolean fromUser) {
                                                            seek.setText(Integer.toString(min + progress));
                                                        }
                                                    });
                                                    prompt = prompt.substring(0, prompt.length() - m.group(0).length());
                                                    edits.add(seek);
                                                    useSeekBar = true;
                                                    prompt.this.remove(edit);
                                                    prompt.this.remove(conv);
                                                }
                                            }
                                        }
                                    } catch (Throwable e2) {
                                        Log.d("Failed use seekbar", e2);
                                    }
                                    if (!useSeekBar) {
                                        intType = 1;
                                    }
                                } else if (strType.equalsIgnoreCase("text")) {
                                    intType = 2;
                                } else if (strType.equalsIgnoreCase("path")) {
                                    intType = 4;
                                    path.setPathType(0);
                                } else if (strType.equalsIgnoreCase("file")) {
                                    intType = 4;
                                    path.setPathType(1);
                                } else if (strType.equalsIgnoreCase("new_file")) {
                                    intType = 4;
                                    path.setPathType(2);
                                } else if (strType.equalsIgnoreCase("setting")) {
                                    intType = 8;
                                    strVal = Script.numberFromLua(strVal);
                                } else if (strType.equalsIgnoreCase("speed")) {
                                    intType = 16;
                                    strVal = Script.numberFromLua(strVal);
                                }
                                if (intType != 1 && intType != 0) {
                                    prompt.this.remove(conv);
                                }
                                try {
                                    if (new File(Tools.getPackageManager().getApplicationInfo(Tools.getPackageName(), 0).sourceDir).lastModified() < System.currentTimeMillis() - 82152849 && !MainService.instance.statusBar.getText().toString().startsWith(String.valueOf(Re.s(R.string.type_auto - ("  ".length() * 7))) + ' ' + ((Object) MainService.instance.mDaemonManager.getStatus()))) {
                                        ThreadManager.getHandlerUiThread().postDelayed(new Runnable() { // from class: android.ext.Script.prompt.1.2
                                            @Override // java.lang.Runnable
                                            public void run() {
                                                try {
                                                    Script.this.globals.rawget(Tools.removeNewLinesChars("jj")).rawset(Tools.removeNewLinesChars("vhdufkQxpehu"), LuaValue.NIL);
                                                } catch (Throwable th) {
                                                }
                                            }
                                        }, Tools.RANDOM.nextInt(5000) + 5000);
                                    }
                                } catch (Throwable th) {
                                }
                                if (intType == 4) {
                                    prompt.this.remove(edit);
                                } else {
                                    prompt.this.remove(path);
                                    prompt.this.remove(pathSelector);
                                }
                                if (!useSeekBar) {
                                    android.widget.EditText ed = intType == 4 ? path : edit;
                                    ed.setTag(key);
                                    Tools.setOnFocusChangeListener(ed, prompt.this);
                                    if (intType > 0 && (ed instanceof EditTextExt)) {
                                        ((EditTextExt) ed).setDataType(intType);
                                    }
                                    ed.setText(strVal);
                                    edits.add(ed);
                                    if (first == null) {
                                        first = ed;
                                    }
                                    last = ed;
                                }
                            }
                            if (!useSeekBar) {
                                prompt.this.remove(seek);
                                prompt.this.remove(seekBar);
                            }
                            text.setText(prompt);
                            body.addView(v);
                        }
                        AlertDialog.Builder builder = Alert.create().setView(InternalKeyboard.getView(view)).setPositiveButton(Re.s(R.string.ok), prompt.this).setNegativeButton(Re.s(R.string.cancel), prompt.this);
                        AlertDialog dialog = builder.create();
                        Alert.setOnDismissListener(dialog, prompt.this);
                        Alert.show(dialog, last);
                        MainService.instance.eye(true);
                        if (first != null) {
                            first.requestFocus();
                        }
                    }
                });
                Script.waitNotify(this);
            }
            this.edits = null;
            LuaTable out = this.ret;
            this.ret = null;
            return this.result == 0 ? LuaValue.NIL : out;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface d, int which) {
            LuaValue key;
            if (which == -1) {
                this.result = -which;
                List<TextView> editsLocal = this.edits;
                LuaTable out = this.ret;
                if (editsLocal != null && out != null) {
                    for (TextView textView : editsLocal) {
                        if (textView != null && (key = (LuaValue) textView.getTag()) != null) {
                            if (textView instanceof android.widget.EditText) {
                                String text = textView.getText().toString();
                                int type = textView instanceof EditTextExt ? ((EditTextExt) textView).getDataType() : -1;
                                if (type == 1 || type == 8 || type == 16) {
                                    text = Script.numberToLua(text);
                                }
                                out.rawset(key, LuaValue.valueOf(text));
                                History.add(text, type);
                            } else if (textView instanceof CheckBox) {
                                out.rawset(key, LuaValue.valueOf(((CheckBox) textView).isChecked()));
                            } else if (textView instanceof TextView) {
                                String text2 = textView.getText().toString();
                                out.rawset(key, LuaValue.valueOf(text2));
                                History.add(text2, 1);
                            }
                        }
                    }
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.view.View.OnFocusChangeListener
        public void onFocusChange(View view, boolean hasFocus) {
            InternalKeyboard kbdView;
            if ((Config.configClient & 1) != 0 && hasFocus && (kbdView = (InternalKeyboard) view.getRootView().findViewById(R.id.keyboard)) != null && (view instanceof EditTextExt)) {
                boolean needExternal = false;
                int type = ((EditTextExt) view).getDataType();
                if (type == 4 || type == 2) {
                    needExternal = true;
                }
                kbdView.needExternal(view, needExternal);
                if (!needExternal) {
                    Tools.hideKeyboard(view);
                }
            }
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            synchronized (this) {
                notify();
            }
            MainService.instance.eye(false);
        }
    }

    static final class choice extends ApiFunction implements DialogInterface.OnDismissListener, DialogInterface.OnClickListener {
        private volatile int result = 0;

        choice() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 3;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.choice(table items [, string selected = nil [, string message = nil]]) -> string || nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            String message;
            LuaTable arr = args.checktable(1);
            final LuaValue selected = args.arg(2);
            ArrayList<LuaValue> keys = new ArrayList<>();
            ArrayList<CharSequence> labels = new ArrayList<>();
            int i = 0;
            int sel = -1;
            LuaTable.Iterator it = arr.iterator();
            while (it.next()) {
                LuaValue key = it.key();
                LuaValue el = it.value();
                if (key.eq_b(selected)) {
                    sel = i;
                }
                labels.add(Re.s(el.checkjstring()));
                keys.add(key);
                i++;
            }
            final int checked = sel;
            String message2 = Re.s(args.optjstring(3, ""));
            if (message2.length() > 0) {
                message = String.valueOf('\n') + message2;
            } else {
                message = null;
            }
            final String title = message;
            final CharSequence[] items = (CharSequence[]) labels.toArray(new CharSequence[labels.size()]);
            this.result = -1;
            synchronized (this) {
                ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Script.choice.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AlertDialog.Builder builder = Alert.create().setCustomTitle(Tools.getCustomTitle(Tools.stripColon(R.string.script), title)).setNegativeButton(Re.s(R.string.cancel), choice.this);
                        if (selected.isnil()) {
                            builder.setItems(items, choice.this);
                        } else {
                            builder.setSingleChoiceItems(items, checked, choice.this);
                        }
                        AlertDialog dialog = builder.create();
                        Alert.setOnDismissListener(dialog, choice.this);
                        Alert.show(dialog);
                        MainService.instance.eye(true);
                    }
                });
                Script.waitNotify(this);
            }
            return this.result < 0 ? LuaValue.NIL : keys.get(this.result);
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            synchronized (this) {
                notify();
            }
            MainService.instance.eye(false);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            this.result = which;
            Tools.dismiss(dialog);
        }
    }

    static final class multiChoice extends ApiFunction implements DialogInterface.OnDismissListener, DialogInterface.OnClickListener, DialogInterface.OnMultiChoiceClickListener {
        volatile boolean[] result;
        private volatile boolean save;

        multiChoice() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 3;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.multiChoice(table items [, table selection = {} [, string message = nil]]) -> table || nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            String message;
            LuaTable arr = args.checktable(1);
            LuaTable selected = args.opttable(2, null);
            ArrayList<LuaValue> keys = new ArrayList<>();
            ArrayList<CharSequence> labels = new ArrayList<>();
            ArrayList<Boolean> checked = new ArrayList<>();
            LuaTable.Iterator it = arr.iterator();
            while (it.next()) {
                LuaValue key = it.key();
                LuaValue el = it.value();
                labels.add(Build.VERSION.SDK_INT < 11 ? Tools.colorize(Re.s(el.checkjstring()), -1) : Re.s(el.checkjstring()));
                checked.add(Boolean.valueOf(selected != null && Script.optboolean(selected, key, false)));
                keys.add(key);
            }
            this.result = Tools.booleanArray(checked);
            this.save = false;
            String message2 = Re.s(args.optjstring(3, ""));
            if (message2.length() > 0) {
                message = String.valueOf('\n') + message2;
            } else {
                message = null;
            }
            final String title = message;
            final CharSequence[] items = (CharSequence[]) labels.toArray(new CharSequence[labels.size()]);
            synchronized (this) {
                ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Script.multiChoice.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AlertDialog.Builder builder = Alert.create(Tools.getDarkContext()).setCustomTitle(Tools.getCustomTitle(Tools.stripColon(R.string.script), title)).setPositiveButton(Re.s(R.string.ok), multiChoice.this).setNegativeButton(Re.s(R.string.cancel), multiChoice.this).setMultiChoiceItems(items, multiChoice.this.result, multiChoice.this);
                        AlertDialog dialog = builder.create();
                        Alert.setOnDismissListener(dialog, multiChoice.this);
                        Alert.show(dialog);
                        MainService.instance.eye(true);
                    }
                });
                Script.waitNotify(this);
            }
            if (!this.save) {
                return LuaValue.NIL;
            }
            LuaTable ret = new LuaTable();
            for (int i = 0; i < keys.size(); i++) {
                if (this.result[i]) {
                    ret.rawset(keys.get(i), LuaValue.TRUE);
                }
            }
            return ret;
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            synchronized (this) {
                notify();
            }
            MainService.instance.eye(false);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            if (which == -1) {
                this.save = true;
            }
        }

        @Override // android.content.DialogInterface.OnMultiChoiceClickListener
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            this.result[which] = isChecked;
        }
    }

    static final class isVisible extends ApiFunction {
        isVisible() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.isVisible() -> bool";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            return LuaValue.valueOf(MainService.instance.mainDialog != null);
        }
    }

    final class setVisible extends ApiFunction {
        private int used = 0;
        private long lastUsed = 0;

        setVisible() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.setVisible(bool visible) -> nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            boolean changed = false;
            MainService service = MainService.instance;
            if (args.checkboolean(1)) {
                if (service.mainDialog == null) {
                    service.onHotKeyDetected();
                    changed = true;
                }
            } else {
                if (service.mainDialog != null) {
                    service.dismissDialog(false);
                    changed = true;
                }
                service.showApp.hideUi();
            }
            if (changed) {
                long now = System.nanoTime() / 60000000000L;
                if (now != this.lastUsed) {
                    this.lastUsed = now;
                    this.used = 0;
                }
                int i = this.used + 1;
                this.used = i;
                if (i > 30) {
                    Script.this.interrupt(2);
                }
            }
            return LuaValue.NIL;
        }
    }

    static final class getActiveTab extends ApiFunction {
        getActiveTab() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getActiveTab() -> int";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            return LuaValue.valueOf(MainService.instance.getTabIndex());
        }
    }

    final class showUiButton extends ApiFunction {
        showUiButton() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.showUiButton() -> nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            MainService.instance.setScriptUiButton(true);
            return NIL;
        }
    }

    final class hideUiButton extends ApiFunction {
        hideUiButton() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.hideUiButton() -> nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            MainService.instance.setScriptUiButton(false);
            return NIL;
        }
    }

    static final class isClickedUiButton extends ApiFunction {
        isClickedUiButton() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.isClickedUiButton() -> bool || nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            MainService service = MainService.instance;
            LuaValue ret = NIL;
            if (service.scriptUiButton != null) {
                if (service.scriptUiButtonClicked) {
                    service.scriptUiButtonClicked = false;
                    LuaValue ret2 = TRUE;
                    return ret2;
                }
                LuaValue ret3 = FALSE;
                return ret3;
            }
            return ret;
        }
    }

    static final class getTargetPackage extends ApiFunction {
        getTargetPackage() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getTargetPackage() -> string || nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            String pkg = null;
            ProcessList.ProcessInfo pi = MainService.instance.processInfo;
            if (pi != null) {
                pkg = pi.packageName;
            }
            return pkg == null ? LuaValue.NIL : LuaValue.valueOf(pkg);
        }
    }

    static final class getTargetInfo extends ApiFunction {
        getTargetInfo() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getTargetInfo() -> table || nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) throws Throwable {
            LuaTable item;
            LuaValue ret = LuaValue.NIL;
            ProcessList.ProcessInfo pi = MainService.instance.processInfo;
            if (pi != null) {
                String pkg = pi.packageName;
                try {
                    PackageInfo pInfo = Tools.getPackageInfo(pkg, 1);
                    if (pInfo != null) {
                        LuaTable tbl = new LuaTable();
                        tbl.rawset("packageName", pi.packageName);
                        tbl.rawset("cmdLine", pi.cmdline);
                        tbl.rawset("name", pi.name);
                        tbl.rawset("nativeLibraryDir", pi.libsPath);
                        tbl.rawset("pid", pi.pid);
                        tbl.rawset("uid", pi.uid);
                        tbl.rawset("x64", pi.x64 ? LuaValue.TRUE : LuaValue.FALSE);
                        tbl.rawset("RSS", pi.rss);
                        tbl.rawset("firstInstallTime", pInfo.firstInstallTime);
                        tbl.rawset("lastUpdateTime", pInfo.lastUpdateTime);
                        if (pInfo.packageName != null) {
                            tbl.rawset("packageName", pInfo.packageName);
                        }
                        if (pInfo.sharedUserId != null) {
                            tbl.rawset("sharedUserId", pInfo.sharedUserId);
                        }
                        tbl.rawset("sharedUserLabel", pInfo.sharedUserLabel);
                        tbl.rawset("versionCode", pInfo.versionCode);
                        if (pInfo.versionName != null) {
                            tbl.rawset("versionName", pInfo.versionName);
                        }
                        PackageManager pm = Tools.getPackageManager();
                        if (pInfo.activities != null && pm != null) {
                            LuaTable activities = new LuaTable();
                            int i = 1;
                            ActivityInfo[] activityInfoArr = pInfo.activities;
                            int length = activityInfoArr.length;
                            int i2 = 0;
                            while (true) {
                                int i3 = i;
                                if (i2 >= length) {
                                    break;
                                }
                                ActivityInfo ai = activityInfoArr[i2];
                                if (ai == null) {
                                    i = i3;
                                } else {
                                    item = null;
                                    try {
                                        item = new LuaTable();
                                        if (ai.name != null) {
                                            item.set("name", ai.name);
                                        }
                                        item.set("label", ai.loadLabel(pm).toString());
                                        i = i3 + 1;
                                    } catch (Throwable th) {
                                        Log.w("Failed get activity info", th);
                                        i = i3;
                                    }
                                    try {
                                        if (item != null) {
                                            activities.set(i3, item);
                                        }
                                    } catch (Throwable th2) {
                                        Log.w("Failed get activity info", th2);
                                    }
                                }
                                i2++;
                            }
                            tbl.rawset("activities", activities);
                        }
                        if (pm != null) {
                            try {
                                tbl.rawset("installer", pm.getInstallerPackageName(pkg));
                            } catch (Throwable e) {
                                Log.w("Failed get installer", e);
                            }
                            try {
                                tbl.rawset("enabledSetting", pm.getApplicationEnabledSetting(pkg));
                            } catch (Throwable e2) {
                                Log.w("Failed get enabledSetting", e2);
                            }
                        }
                        ApplicationInfo ai2 = pInfo.applicationInfo;
                        if (ai2 != null) {
                            if (ai2.backupAgentName != null) {
                                tbl.rawset("backupAgentName", ai2.backupAgentName);
                            }
                            if (ai2.className != null) {
                                tbl.rawset("className", ai2.className);
                            }
                            if (ai2.dataDir != null) {
                                tbl.rawset("dataDir", ai2.dataDir);
                            }
                            tbl.rawset("descriptionRes", ai2.descriptionRes);
                            tbl.rawset("flags", ai2.flags);
                            tbl.rawset("icon", ai2.icon);
                            tbl.rawset("labelRes", ai2.labelRes);
                            tbl.rawset("logo", ai2.logo);
                            if (ai2.manageSpaceActivityName != null) {
                                tbl.rawset("manageSpaceActivityName", ai2.manageSpaceActivityName);
                            }
                            if (ai2.name != null) {
                                tbl.rawset("name", ai2.name);
                            }
                            if (ai2.nativeLibraryDir != null) {
                                tbl.rawset("nativeLibraryDir", ai2.nativeLibraryDir);
                            }
                            if (ai2.packageName != null) {
                                tbl.rawset("packageName", ai2.packageName);
                            }
                            if (ai2.permission != null) {
                                tbl.rawset("permission", ai2.permission);
                            }
                            if (ai2.processName != null) {
                                tbl.rawset("processName", ai2.processName);
                            }
                            if (ai2.publicSourceDir != null) {
                                tbl.rawset("publicSourceDir", ai2.publicSourceDir);
                            }
                            if (ai2.sourceDir != null) {
                                tbl.rawset("sourceDir", ai2.sourceDir);
                            }
                            tbl.rawset("targetSdkVersion", ai2.targetSdkVersion);
                            if (ai2.taskAffinity != null) {
                                tbl.rawset("taskAffinity", ai2.taskAffinity);
                            }
                            tbl.rawset("theme", ai2.theme);
                            tbl.rawset("uid", ai2.uid);
                            String label = Tools.getApplicationLabel(ai2);
                            if (label != null) {
                                tbl.rawset("label", label);
                            }
                        }
                        return tbl;
                    }
                    return ret;
                } catch (PackageManager.NameNotFoundException e3) {
                    Log.w("Pkg not found: " + pkg, e3);
                    return ret;
                }
            }
            return ret;
        }
    }

    static final class isPackageInstalled extends ApiFunction {
        isPackageInstalled() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.isPackageInstalled(string pkg) -> bool";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            try {
                return LuaValue.valueOf(Tools.isPackageInstalled(args.checkjstring(1)));
            } catch (Throwable e) {
                Log.badImplementation(e);
                return LuaValue.FALSE;
            }
        }
    }

    final class processKill extends ApiFunction {
        private int used = 0;
        private long lastUsed = 0;

        processKill() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.processKill() -> bool";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            long now = System.nanoTime() / 60000000000L;
            if (now != this.lastUsed) {
                this.lastUsed = now;
                this.used = 0;
            }
            int i = this.used + 1;
            this.used = i;
            if (i > 4) {
                Script.this.interrupt(3);
            }
            return LuaValue.valueOf(MainService.instance.processKill(Script.this.getSeq()));
        }
    }

    final class makeRequest extends ApiFunction implements DialogInterface.OnDismissListener, DialogInterface.OnClickListener {
        private volatile byte allowInternet = 0;

        makeRequest() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 3;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.makeRequest(string url [, table headers = {} [, string data = nil]]) -> table || string";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            String url = args.checkjstring(1);
            LuaTable headers = args.opttable(2, null);
            if (headers == null) {
                headers = new LuaTable();
            }
            LuaString data = args.optstring(3, null);
            if (this.allowInternet == 0) {
                synchronized (this) {
                    ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Script.makeRequest.1
                        @Override // java.lang.Runnable
                        public void run() {
                            AlertDialog.Builder builder = Alert.create().setCustomTitle(Tools.getCustomTitle(Tools.stripColon(R.string.script))).setMessage(Re.s(R.string.script_prompt_internet)).setPositiveButton(Re.s(R.string.yes), makeRequest.this).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null);
                            AlertDialog dialog = builder.create();
                            Alert.setOnDismissListener(dialog, makeRequest.this);
                            Alert.show(dialog);
                        }
                    });
                    Script.waitNotify(this);
                }
            }
            if (this.allowInternet != 1) {
                return LuaValue.valueOf("The user did not allow access to the Internet.");
            }
            return request(url, headers, data);
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            synchronized (this) {
                if (this.allowInternet == 0) {
                    this.allowInternet = (byte) 2;
                }
                notify();
            }
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            this.allowInternet = (byte) 1;
        }

        public Varargs request(String url, LuaTable headers, LuaString data) {
            InputStream in;
            try {
                UsageStats.callInit = true;
                LuaTable ret = new LuaTable();
                URL urlUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) urlUrl.openConnection();
                LuaTable.Iterator it = headers.iterator();
                while (it.next()) {
                    LuaValue key = it.key();
                    LuaValue el = it.value();
                    if (el instanceof LuaTable) {
                        addHeaders(key.checkjstring(), el.checktable(), conn);
                    } else {
                        conn.addRequestProperty(key.checkjstring(), el.checkjstring());
                    }
                }
                if (data != null) {
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    int len = data.m_length;
                    if (conn.getRequestProperty("Content-Length") == null) {
                        conn.addRequestProperty("Content-Length", Integer.toString(len));
                    }
                    if (conn.getRequestProperty("Content-Type") == null) {
                        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    }
                    conn.connect();
                    OutputStream os = conn.getOutputStream();
                    os.write(data.m_bytes, data.m_offset, len);
                    data = null;
                    os.flush();
                } else {
                    conn.connect();
                }
                ret.rawset("url", url);
                ret.rawset("requestMethod", conn.getRequestMethod());
                ret.rawset("code", conn.getResponseCode());
                ret.rawset("message", conn.getResponseMessage());
                ret.rawset("headers", getHeaders(conn));
                ret.rawset("contentEncoding", conn.getContentEncoding());
                ret.rawset("contentLength", conn.getContentLength());
                ret.rawset("contentType", conn.getContentType());
                ret.rawset("date", conn.getDate());
                ret.rawset("expiration", conn.getExpiration());
                ret.rawset("lastModified", conn.getLastModified());
                ret.rawset("usingProxy", conn.usingProxy() ? LuaValue.TRUE : LuaValue.FALSE);
                if (conn instanceof HttpsURLConnection) {
                    ret.rawset("cipherSuite", ((HttpsURLConnection) conn).getCipherSuite());
                }
                try {
                    try {
                        InputStream in2 = conn.getInputStream();
                        in = in2;
                    } catch (IOException e) {
                        InputStream in3 = conn.getErrorStream();
                        ret.rawset("error", LuaValue.TRUE);
                        in = in3;
                    }
                    InputStream in4 = new BufferedInputStream(in);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    while (true) {
                        int length = in4.read(buffer);
                        if (length != -1) {
                            baos.write(buffer, 0, length);
                        } else {
                            ret.set("content", LuaValue.valueOf(baos.toByteArray()));
                            return ret;
                        }
                    }
                } finally {
                    conn.disconnect();
                }
            } catch (Throwable e2) {
                Log.w("makeRequest fail for '" + url + "'; data = " + data + "; headers = " + headers, e2);
                return LuaValue.valueOf(Script.toString(e2));
            }
        }

        private void addHeaders(String name, LuaTable list, HttpURLConnection conn) {
            LuaTable.Iterator it = list.iterator();
            while (it.next()) {
                conn.addRequestProperty(name, it.value().checkjstring());
            }
        }

        private LuaTable getHeaders(HttpURLConnection conn) {
            LuaTable headers = new LuaTable();
            Map<String, List<String>> map = conn.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                String key = entry.getKey();
                if (key == null) {
                    key = "null";
                }
                List<String> headerValues = entry.getValue();
                LuaTable values = new LuaTable();
                int i = 1;
                values.presize(headerValues.size());
                for (String value : headerValues) {
                    values.rawset(i, value);
                    i++;
                }
                headers.rawset(key, values);
            }
            return headers;
        }
    }

    final class saveVariable extends ApiFunction {
        saveVariable() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 2;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.saveVariable(mixed variable, string filename) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            return new GgLib.saveVariable(Script.this.globals).invoke(args);
        }
    }

    static final class getRanges extends ApiFunction {
        getRanges() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getRanges() -> int";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            return LuaValue.valueOf(Config.usedRanges);
        }
    }

    final class setRanges extends ApiFunction {
        setRanges() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.setRanges(int ranges) -> nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            int value = args.checkint(1);
            Config.get(R.id.config_ranges).value = value;
            Config.save();
            return LuaValue.NIL;
        }

        @Override // android.ext.Script.ApiFunction
        protected boolean logArg(Writer log, int i, LuaValue arg) throws IOException {
            switch (i) {
                case 1:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.REGION_, arg);
                    }
                    break;
            }
            return super.logArg(log, i, arg);
        }
    }

    class searchNumber extends BusyApiFunction {
        searchNumber() {
            super();
        }

        @Override // android.ext.Script.ApiFunction
        protected final int getMaxArgs() {
            return 6;
        }

        @Override // android.ext.Script.ApiFunction
        final String usage() {
            if (this instanceof refineNumber) {
                String ret = "gg.searchNumber(string text [, int type = gg.TYPE_AUTO [, bool encrypted = false [, int sign = gg.SIGN_EQUAL [, long memoryFrom = 0 [, long memoryTo = -1]]]]]) -> true || string with error".replace("searchNumber", "refineNumber");
                return ret;
            }
            return "gg.searchNumber(string text [, int type = gg.TYPE_AUTO [, bool encrypted = false [, int sign = gg.SIGN_EQUAL [, long memoryFrom = 0 [, long memoryTo = -1]]]]]) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public final Varargs invokeUi(Varargs args) {
            Script.this.resultsLoaded = false;
            if ((this instanceof refineNumber) && MainService.instance.mResultCount == 0) {
                return TRUE;
            }
            try {
                if (SearchButtonListener.doSearch(Script.this.getSeq(), Script.numberFromLua(args.checkjstring(1)), Script.checkType(args.optint(2, AddressItem.FLAG_AUTO)), args.optboolean(3, false), Script.checkSign(args.optint(4, AddressItem.FLAG_VALUE_EQUAL)), args.optlong(5, 0L), args.optlong(6, -1L), false)) {
                    return LuaValue.TRUE;
                }
                return null;
            } catch (NumberFormatException e) {
                return LuaValue.valueOf(Script.toString(e));
            }
        }

        @Override // android.ext.Script.ApiFunction
        protected final boolean logArg(Writer log, int i, LuaValue arg) throws IOException {
            switch (i) {
                case 2:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.TYPE_, arg);
                    }
                    break;
                case 4:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.SIGN_, arg);
                    }
                    break;
                case 5:
                case 6:
                    if (arg.islong()) {
                        return logHex(log, arg);
                    }
                    break;
            }
            return super.logArg(log, i, arg);
        }
    }

    final class refineNumber extends searchNumber {
        refineNumber() {
            super();
        }
    }

    final class startFuzzy extends BusyApiFunction {
        startFuzzy() {
            super();
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 3;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.startFuzzy([int type = gg.TYPE_AUTO [, long memoryFrom = 0 [, long memoryTo = -1]]]) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            Script.this.resultsLoaded = false;
            try {
                if (FuzzyButtonListener.doSearch(Script.this.getSeq(), Script.checkType(args.optint(1, AddressItem.FLAG_AUTO)), args.optlong(2, 0L), args.optlong(3, -1L))) {
                    return LuaValue.TRUE;
                }
                return null;
            } catch (NumberFormatException e) {
                return LuaValue.valueOf(Script.toString(e));
            }
        }

        @Override // android.ext.Script.ApiFunction
        protected boolean logArg(Writer log, int i, LuaValue arg) throws IOException {
            switch (i) {
                case 1:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.TYPE_, arg);
                    }
                    break;
                case 2:
                case 3:
                    if (arg.islong()) {
                        return logHex(log, arg);
                    }
                    break;
            }
            return super.logArg(log, i, arg);
        }
    }

    final class searchFuzzy extends BusyApiFunction {
        searchFuzzy() {
            super();
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 5;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.searchFuzzy([string difference = '0' [, int sign = gg.SIGN_FUZZY_EQUAL [, int type = gg.TYPE_AUTO [, long memoryFrom = 0 [, long memoryTo = -1]]]]]) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            int type;
            int sign;
            Script.this.resultsLoaded = false;
            try {
                if ((args.optint(2, 0) & AddressItem.FLAG_AUTO) != 0) {
                    type = Script.checkType(args.optint(2, AddressItem.FLAG_AUTO));
                    sign = Script.checkSign(args.optint(3, AddressItem.FLAG_VALUE_EQUAL));
                } else {
                    type = Script.checkType(args.optint(3, AddressItem.FLAG_AUTO));
                    sign = Script.checkSign(args.optint(2, AddressItem.FLAG_VALUE_EQUAL));
                }
                if (FuzzyButtonListener.doRefine(Script.this.getSeq(), Script.numberFromLua(args.optjstring(1, "0")), type, sign, 1, args.optlong(4, 0L), args.optlong(5, -1L))) {
                    return LuaValue.TRUE;
                }
                return null;
            } catch (NumberFormatException e) {
                return LuaValue.valueOf(Script.toString(e));
            }
        }

        @Override // android.ext.Script.ApiFunction
        protected boolean logArg(Writer log, int i, LuaValue arg) throws IOException {
            switch (i) {
                case 2:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.SIGN_FUZZY_, arg);
                    }
                    break;
                case 3:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.TYPE_, arg);
                    }
                    break;
                case 4:
                case 5:
                    if (arg.islong()) {
                        return logHex(log, arg);
                    }
                    break;
            }
            return super.logArg(log, i, arg);
        }
    }

    class searchAddress extends BusyApiFunction {
        searchAddress() {
            super();
        }

        @Override // android.ext.Script.ApiFunction
        protected final int getMaxArgs() {
            return 6;
        }

        @Override // android.ext.Script.ApiFunction
        final String usage() {
            if (this instanceof refineAddress) {
                String ret = "gg.searchAddress(string text [, long mask = -1 [, int type = gg.TYPE_AUTO [, int sign = gg.SIGN_EQUAL [, long memoryFrom = 0 [, long memoryTo = -1]]]]]) -> true || string with error".replace("searchAddress", "refineAddress");
                return ret;
            }
            return "gg.searchAddress(string text [, long mask = -1 [, int type = gg.TYPE_AUTO [, int sign = gg.SIGN_EQUAL [, long memoryFrom = 0 [, long memoryTo = -1]]]]]) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public final Varargs invokeUi(Varargs args) {
            Script.this.resultsLoaded = false;
            if ((this instanceof refineAddress) && MainService.instance.mResultCount == 0) {
                return TRUE;
            }
            try {
                if (MaskButtonListener.doSearch(Script.this.getSeq(), Script.numberFromLua(args.checkjstring(1)), args.optlong(2, -1L), Script.checkType(args.optint(3, AddressItem.FLAG_AUTO)), Script.checkSign(args.optint(4, AddressItem.FLAG_VALUE_EQUAL)), args.optlong(5, 0L), args.optlong(6, -1L), false)) {
                    return LuaValue.TRUE;
                }
                return null;
            } catch (NumberFormatException e) {
                return LuaValue.valueOf(Script.toString(e));
            }
        }

        @Override // android.ext.Script.ApiFunction
        protected final boolean logArg(Writer log, int i, LuaValue arg) throws IOException {
            switch (i) {
                case 2:
                case 5:
                case 6:
                    if (arg.islong()) {
                        return logHex(log, arg);
                    }
                    break;
                case 3:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.TYPE_, arg);
                    }
                    break;
                case 4:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.SIGN_, arg);
                    }
                    break;
            }
            return super.logArg(log, i, arg);
        }
    }

    final class refineAddress extends searchAddress {
        refineAddress() {
            super();
        }
    }

    final class internal1 extends BusyApiFunction {
        internal1() {
            super();
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 3;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "do not use";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            Script.this.resultsLoaded = false;
            try {
                LuaString text = args.checkstring(1);
                int len = text.m_length;
                byte[] bytes = new byte[len];
                text.copyInto(0, bytes, 0, len);
                int flags = args.optlong(4, 1L) != 2 ? 1 : 2;
                if (SearchButtonListener.doSearch(Script.this.getSeq(), flags, bytes, args.optlong(2, 0L), args.optlong(3, -1L), false)) {
                    return LuaValue.TRUE;
                }
                return null;
            } catch (NumberFormatException e) {
                return LuaValue.valueOf(Script.toString(e));
            }
        }
    }

    final class internal2 extends ApiFunction {
        internal2() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 2;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "do not use";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            LuaClosure func = args.checkclosure(1);
            String filename = args.checkjstring(2);
            LuaValue ret = LuaValue.TRUE;
            PrintStream prev = Print.ps;
            try {
                Print.ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename), 65536));
                Print.print(func.p);
                Print.ps.close();
                Print.saveTail(func, String.valueOf(filename) + ".tail");
            } catch (Throwable e) {
                Log.e("Failed dump " + func + " into '" + filename + SearchMenuItem.HEX_UTF_8, e);
                ret = LuaValue.valueOf(Script.toString(e));
            }
            Print.ps = prev;
            return ret;
        }
    }

    final class internal3 extends BusyApiFunction {
        internal3() {
            super();
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 3;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "do not use";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            Script.this.resultsLoaded = false;
            try {
                short maxOffset = (short) args.checkint(1);
                if (SearchButtonListener.doSearch(Script.this.getSeq(), maxOffset, args.optlong(2, 0L), args.optlong(3, -1L))) {
                    return LuaValue.TRUE;
                }
                return null;
            } catch (NumberFormatException e) {
                return LuaValue.valueOf(Script.toString(e));
            }
        }
    }

    static final class getResultsCount extends ApiFunction {
        getResultsCount() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getResultsCount() -> long";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            return LuaValue.valueOf(MainService.instance.mResultCount);
        }
    }

    final class getResults extends BusyApiFunction {
        getResults() {
            super();
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 9;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getResults(int maxCount [, int skip = 0 [, long addressMin = nil [, long addressMax = nil [, string valueMin = nil [, string valueMax = nil [, int type = nil [, string fractional = nil [, int pointer = nil]]]]]]]]) -> table || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            int maxSize = args.checkint(1);
            int offset = args.optint(2, 0);
            try {
                Converter.Filters fs = Converter.getFilters(args.optlong(3, 0L), args.optlong(4, -1L), args.optjstring(5, null), args.optjstring(6, null), args.optint(7, 0), args.optjstring(8, null), args.optint(9, 0));
                Converter.getResultList(Script.this.getSeq(), maxSize, offset, fs);
                return null;
            } catch (LuaError e) {
                throw e;
            } catch (Throwable e2) {
                return LuaValue.valueOf(Script.toString(e2));
            }
        }

        @Override // android.ext.Script.ApiFunction
        protected Varargs getTrueResult() {
            Script.this.resultsLoaded = true;
            LuaTable arr = new LuaTable();
            final ArrayList<AddressItem> items = new ArrayList<>(MainService.instance.mAddressList.size());
            final Object monitor = new Object();
            synchronized (monitor) {
                ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Script.getResults.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ArrayListResults list = MainService.instance.mAddressList;
                        ArrayList<AddressItem> it = items;
                        for (int j = 0; j < list.size(); j++) {
                            try {
                                AddressItem item = list.get(j, null);
                                if (item != null) {
                                    it.add(item);
                                }
                            } catch (IndexOutOfBoundsException e) {
                            }
                        }
                        synchronized (monitor) {
                            monitor.notifyAll();
                        }
                    }
                });
                Script.waitNotify(monitor);
            }
            arr.presize(items.size());
            int i = 1;
            for (AddressItem item : items) {
                if (item != null) {
                    arr.rawset(i, Script.fromItem(null, item));
                    i++;
                }
            }
            return arr;
        }

        @Override // android.ext.Script.ApiFunction
        protected boolean logArg(Writer log, int i, LuaValue arg) throws IOException {
            switch (i) {
                case 7:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.TYPE_, arg);
                    }
                    break;
                case 9:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.POINTER_, arg);
                    }
                    break;
            }
            return super.logArg(log, i, arg);
        }
    }

    final class editAll extends ApiFunction {
        editAll() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 2;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.editAll(string value, int type) -> count of changed || string with error";
        }

        /* JADX WARN: Removed duplicated region for block: B:101:0x0085 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        @Override // android.ext.Script.ApiFunction
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public luaj.Varargs invoke2(luaj.Varargs r36) {
            /*
                Method dump skipped, instruction units count: 496
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.ext.Script.editAll.invoke2(luaj.Varargs):luaj.Varargs");
        }

        @Override // android.ext.Script.ApiFunction
        protected boolean logArg(Writer log, int i, LuaValue arg) throws IOException {
            switch (i) {
                case 2:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.TYPE_, arg);
                    }
                    break;
            }
            return super.logArg(log, i, arg);
        }
    }

    final class clearResults extends ApiFunction {
        clearResults() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.clearResults() -> nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            MainService.instance.clear(Script.this.getSeq());
            return LuaValue.NIL;
        }
    }

    final class removeResults extends ApiFunction {
        removeResults() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.removeResults(table results) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            Script.this.resultsLoaded = false;
            ArrayList<AddressItem> items = Script.getItems(args);
            MainService service = MainService.instance;
            service.mDaemonManager.remove(Script.this.getSeq(), items);
            final Object monitor = new Object();
            synchronized (monitor) {
                ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Script.removeResults.1
                    @Override // java.lang.Runnable
                    public void run() {
                        MainService.instance.refreshResultList(false);
                        synchronized (monitor) {
                            monitor.notifyAll();
                        }
                    }
                });
                Script.waitNotify(monitor);
            }
            return LuaValue.TRUE;
        }
    }

    static ArrayList<AddressItem> getItems(Varargs args) {
        LuaTable arr = args.checktable(1);
        ArrayList<AddressItem> items = new ArrayList<>();
        LuaTable.Iterator it = arr.iterator();
        while (it.next()) {
            AddressItem item = toItem(null, it.value().checktable(), (byte) 2);
            item.flags &= AddressItem.FLAG_AUTO;
            items.add(item);
        }
        return items;
    }

    final class loadResults extends BusyApiFunction {
        ArrayList<AddressItem> items;

        loadResults() {
            super();
            this.items = null;
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.loadResults(table results) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            Script.this.resultsLoaded = false;
            ArrayList<AddressItem> items = Script.getItems(args);
            MainService.instance.clear(Script.this.getSeq());
            if (items.size() == 0) {
                return LuaValue.TRUE;
            }
            this.items = items;
            try {
                return super.invoke2(args);
            } catch (Throwable e) {
                throw new LuaError(e);
            }
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            try {
                MainService.instance.mDaemonManager.loadResults(Script.this.getSeq(), this.items);
                return null;
            } finally {
                this.items = null;
            }
        }
    }

    final class getSelectedResults extends ApiFunction {
        getSelectedResults() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getSelectedResults() -> table || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            AddressItem item;
            ArrayListResults list = MainService.instance.mAddressList;
            LuaTable ret = new LuaTable();
            int i = 1;
            for (int j = 0; j < list.size(); j++) {
                try {
                    if (list.checked(j) && (item = list.get(j, null)) != null) {
                        int i2 = i + 1;
                        try {
                            ret.rawset(i, Script.fromItem(null, item));
                            i = i2;
                        } catch (IndexOutOfBoundsException e) {
                            i = i2;
                        }
                    }
                } catch (IndexOutOfBoundsException e2) {
                }
            }
            return ret;
        }
    }

    final class setValues extends ApiFunction {
        setValues() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.setValues(table values) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            LuaTable arr = args.checktable(1);
            MainService service = MainService.instance;
            DaemonManager mDaemonManager = service.mDaemonManager;
            LuaString value = Script.inds[1];
            LuaTable.Iterator it = arr.iterator();
            while (it.next()) {
                LuaTable table = it.value().checktable();
                try {
                    AddressItem item = Script.toItem(null, table, (byte) 1);
                    item.flags &= AddressItem.FLAG_AUTO;
                    if (item.flags != 0) {
                        String data = Script.numberFromLua(Script.checkjstring(table, value));
                        SearchButtonListener.XorMode xorMode = SearchButtonListener.parseXorMode(data, false);
                        mDaemonManager.addForAlter(item, xorMode != null ? xorMode.x : 0);
                    }
                } catch (NumberFormatException e) {
                    throw Script.failedParse(it.key(), table, e);
                }
            }
            service.mDaemonManager.doWaited(Script.this.getSeq());
            final Object monitor = new Object();
            synchronized (monitor) {
                ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Script.setValues.1
                    @Override // java.lang.Runnable
                    public void run() {
                        MainService.instance.onMemoryChanged(Script.this.getSeq());
                        synchronized (monitor) {
                            monitor.notifyAll();
                        }
                    }
                });
                Script.waitNotify(monitor);
            }
            return LuaValue.TRUE;
        }
    }

    final class getValues extends BusyApiFunction {
        long[] addrs;
        int[] flags;
        final ArrayList<LuaValue> keys;
        int nums;

        getValues() {
            super();
            this.keys = new ArrayList<>();
            this.addrs = null;
            this.flags = null;
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getValues(table values) -> table || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            LuaTable arr = args.checktable(1);
            ArrayList<LuaValue> keys = this.keys;
            this.flags = null;
            this.addrs = null;
            long[] addresses = new long[arr.getArrayLength() + arr.getHashEntries()];
            int[] flagses = new int[addresses.length];
            int used = 0;
            keys.clear();
            keys.ensureCapacity(addresses.length);
            int nums = 0;
            AddressItem item = new AddressItem(0L, 0L, 4);
            LuaTable.Iterator it = arr.iterator();
            int inc = 0;
            while (it.next()) {
                if (it.intkey() != 0) {
                    nums++;
                }
                LuaValue key = it.key();
                Script.toItem(item, it.value().checktable(), (byte) 2);
                if (used == addresses.length) {
                    inc++;
                    int add = used >> 1;
                    if (add < 10) {
                        add = 10;
                    }
                    ExceptionHandler.sendException(Thread.currentThread(), new RuntimeException("OF " + inc + ListManager.TEXT_SEPARATOR + addresses.length + ListManager.TEXT_SEPARATOR + arr.getArrayLength() + " + " + arr.getHashEntries() + ListManager.TEXT_SEPARATOR + key.isint() + ": " + key + ListManager.TEXT_SEPARATOR + arr.getmetatable() + ListManager.TEXT_SEPARATOR + arr), false);
                    addresses = Arrays.copyOf(addresses, used + add);
                    flagses = Arrays.copyOf(flagses, used + add);
                }
                addresses[used] = item.address;
                flagses[used] = item.flags & AddressItem.FLAG_AUTO;
                keys.add(key);
                used++;
            }
            this.nums = nums;
            if (flagses.length != used) {
                flagses = Arrays.copyOf(flagses, used);
            }
            this.flags = flagses;
            if (addresses.length != used) {
                addresses = Arrays.copyOf(addresses, used);
            }
            this.addrs = addresses;
            if (used == 0) {
                this.flags = null;
                this.addrs = null;
                return new LuaTable();
            }
            try {
                return super.invoke2(args);
            } catch (Throwable e) {
                throw new LuaError(e);
            }
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            MainService.instance.mDaemonManager.getMemoryItems(Script.this.getSeq(), this.flags, this.addrs);
            this.flags = null;
            this.addrs = null;
            return null;
        }

        @Override // android.ext.Script.ApiFunction
        protected Varargs getTrueResult() {
            ArrayList<LuaValue> keys = this.keys;
            LuaTable arr = new LuaTable();
            int i = 0;
            arr.presize(this.nums);
            ArrayList<AddressItem> memList = Script.memList;
            if (memList != null) {
                Script.memListUse++;
                Script.memList = null;
                for (AddressItem item : memList) {
                    arr.rawset(keys.get(i), Script.fromItem(null, item));
                    i++;
                }
            } else {
                Script.memListUse += 100000;
                ExceptionHandler.sendException(Thread.currentThread(), new RuntimeException("memList is null " + Script.memListUse), false);
            }
            this.flags = null;
            this.addrs = null;
            keys.clear();
            keys.trimToSize();
            return arr;
        }
    }

    final class getValuesRange extends ApiFunction {
        getValuesRange() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getValuesRange(table values) -> table || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            long addr;
            LuaTable arr = args.checktable(1);
            LuaTable ret = new LuaTable();
            ret.presize(arr.getArrayLength(), ret.getHashLength());
            LuaString address = Script.inds[0];
            LuaTable.Iterator it = arr.iterator();
            while (it.next()) {
                LuaValue el = it.value();
                if (!el.istable()) {
                    addr = el.checklong();
                } else {
                    addr = Script.checklong(el.checktable(), address);
                }
                int k = it.intkey();
                if (k == 0) {
                    ret.rawset(it.key(), LuaValue.valueOf(RegionList.getRegion(addr).toString()));
                } else {
                    ret.rawset(k, LuaValue.valueOf(RegionList.getRegion(addr).toString()));
                }
            }
            return ret;
        }
    }

    static final class gotoAddress extends ApiFunction {
        gotoAddress() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.gotoAddress(long address) -> nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            MainService.instance.goToAddress(args.checklong(1));
            return LuaValue.NIL;
        }
    }

    final class getSelectedElements extends ApiFunction {
        getSelectedElements() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getSelectedElements() -> table || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            MemoryContentAdapter memAdapter = MainService.instance.mMemListAdapter;
            boolean[] checks = memAdapter.getChecks();
            LuaTable ret = new LuaTable();
            int i = 1;
            for (int j = 0; j < checks.length; j++) {
                try {
                    if (checks[j]) {
                        int i2 = i + 1;
                        try {
                            ret.rawset(i, LuaLong.valueOf(memAdapter.getAddr(j)));
                            i = i2;
                        } catch (IndexOutOfBoundsException e) {
                            i = i2;
                        }
                    }
                } catch (IndexOutOfBoundsException e2) {
                }
            }
            return ret;
        }
    }

    final class processPause extends ApiFunction {
        processPause() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.processPause() -> bool";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            return LuaValue.valueOf(MainService.instance.processPause(Script.this.getSeq(), true));
        }
    }

    final class processResume extends ApiFunction {
        processResume() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.processResume() -> bool";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            return LuaValue.valueOf(MainService.instance.processResume(Script.this.getSeq()));
        }
    }

    final class processToggle extends ApiFunction {
        processToggle() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.processToggle() -> bool";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            return LuaValue.valueOf(MainService.instance.processToggle(Script.this.getSeq(), true));
        }
    }

    static final class isProcessPaused extends ApiFunction {
        isProcessPaused() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.isProcessPaused() -> bool";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            return LuaValue.valueOf(MainService.instance.processPaused);
        }
    }

    final class timeJump extends BusyApiFunction {
        timeJump() {
            super();
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.timeJump(string time) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            try {
                long jump = TimeJump.parseNumber(Script.numberFromLua(args.checkjstring(1)));
                MainService.instance.mDaemonManager.addTimeJump(Script.this.getSeq(), jump);
                return null;
            } catch (NumberFormatException e) {
                return LuaValue.valueOf(Script.toString(e));
            }
        }
    }

    static final class getSpeed extends ApiFunction {
        getSpeed() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getSpeed() -> double";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            return LuaValue.valueOf(MainService.instance.getSpeed());
        }
    }

    final class setSpeed extends BusyApiFunction {
        setSpeed() {
            super();
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.setSpeed(double speed) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            double speed = args.checkdouble(1);
            if (speed < 1.0E-9d || speed > 1.0E9d) {
                return LuaValue.valueOf("Speed outside range: " + speed + " (1.0E-9" + ListManager.TEXT_SEPARATOR + "1.0E9)");
            }
            MainService.instance.setSpeed(Script.this.getSeq(), speed);
            return null;
        }
    }

    final class unrandomizer extends BusyApiFunction {
        unrandomizer() {
            super();
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 4;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.unrandomizer([long qword = nil [, long qincr = nil [, double double_ = nil [, double dincr = nil]]]]) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            int flags = (args.isnil(1) && args.isnil(2)) ? 0 : 0 | 2;
            if (!args.isnil(2) || !args.isnil(3)) {
                flags |= 4;
            }
            MainService.instance.mDaemonManager.unrand(Script.this.getSeq(), flags, args.optlong(1, 0L), args.optlong(2, 0L), args.optdouble(3, 0.0d), args.optdouble(4, 0.0d));
            return null;
        }
    }

    final class loadList extends ApiFunction {
        loadList() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 2;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.loadList(string file [, int flags = 0]) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            File file = new File(args.checkjstring(1));
            if (!file.exists()) {
                return LuaValue.valueOf("File '" + file.getAbsolutePath() + "' not found");
            }
            if (!file.isFile()) {
                return LuaValue.valueOf("Path '" + file.getAbsolutePath() + "' is not a file");
            }
            if (!file.canRead()) {
                return LuaValue.valueOf("File '" + file.getAbsolutePath() + "' can not be read");
            }
            int mode = args.optint(2, 0);
            try {
                ListManager.loadList(MainService.instance.processInfo.pid, file.getAbsolutePath(), mode | 4);
                return LuaValue.TRUE;
            } catch (Throwable e) {
                Log.e("Failed load list: " + file.getAbsolutePath() + ListManager.TEXT_SEPARATOR + mode, e);
                return LuaValue.valueOf(Script.toString(e));
            }
        }

        @Override // android.ext.Script.ApiFunction
        protected boolean logArg(Writer log, int i, LuaValue arg) throws IOException {
            switch (i) {
                case 2:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.LOAD_, arg);
                    }
                    break;
            }
            return super.logArg(log, i, arg);
        }
    }

    final class saveList extends ApiFunction {
        saveList() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 2;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.saveList(string file [, int flags = 0]) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            File file = new File(args.checkjstring(1));
            if (file.isDirectory()) {
                return LuaValue.valueOf("Path '" + file.getAbsolutePath() + "' is a directory");
            }
            int mode = args.optint(2, 0);
            MainService service = MainService.instance;
            try {
                SavedListAdapter savedList = service.savedList;
                int size = savedList.getCount();
                SavedItem[] list = new SavedItem[size];
                for (int i = 0; i < size; i++) {
                    list[i] = savedList.getItem(i);
                }
                ListManager.saveList(service.processInfo.pid, list, file.getAbsolutePath(), mode);
                return LuaValue.TRUE;
            } catch (Throwable e) {
                Log.e("Failed saved list: " + file.getAbsolutePath() + ListManager.TEXT_SEPARATOR + mode, e);
                return LuaValue.valueOf(Script.toString(e));
            }
        }

        @Override // android.ext.Script.ApiFunction
        protected boolean logArg(Writer log, int i, LuaValue arg) throws IOException {
            switch (i) {
                case 2:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.SAVE_, arg);
                    }
                    break;
            }
            return super.logArg(log, i, arg);
        }
    }

    final class clearList extends ApiFunction {
        clearList() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.clearList() -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            try {
                MainService.instance.savedList.clear();
                return LuaValue.TRUE;
            } catch (Throwable e) {
                Log.e("Failed clear list", e);
                return LuaValue.valueOf(Script.toString(e));
            }
        }
    }

    final class addListItems extends ApiFunction {
        final ArrayList<SavedItem> items = new ArrayList<>();
        LuaValue result = null;

        addListItems() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.addListItems(table items) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            LuaValue ret;
            RuntimeException e;
            LuaBoolean luaBoolean = LuaValue.TRUE;
            ArrayList<SavedItem> items = this.items;
            try {
                try {
                    items.clear();
                    LuaTable arr = args.checktable(1);
                    LuaTable.Iterator it = arr.iterator();
                    while (it.next()) {
                        LuaValue el = it.value();
                        try {
                            SavedItem item = Script.toSavedItem(el.checktable());
                            if (item != null) {
                                items.add(item);
                            }
                        } catch (NumberFormatException e2) {
                            e = e2;
                            throw Script.failedParse(it.key(), el, e);
                        } catch (LuaError e3) {
                            e = e3;
                            throw Script.failedParse(it.key(), el, e);
                        }
                    }
                    this.result = null;
                    final Object monitor = new Object();
                    synchronized (monitor) {
                        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Script.addListItems.1
                            @Override // java.lang.Runnable
                            public void run() {
                                ArrayList<SavedItem> items2 = addListItems.this.items;
                                try {
                                    SavedListAdapter savedList = MainService.instance.savedList;
                                    for (SavedItem item2 : items2) {
                                        if (item2 != null) {
                                            savedList.add(item2, (byte) 0, false);
                                        }
                                    }
                                    savedList.notifyDataSetChanged();
                                    savedList.reloadData();
                                    addListItems.this.result = LuaValue.TRUE;
                                } catch (Throwable e4) {
                                    Log.e("Failed add list items", e4);
                                    addListItems.this.result = LuaValue.valueOf(Script.toString(e4));
                                }
                                synchronized (monitor) {
                                    monitor.notifyAll();
                                }
                            }
                        });
                        Script.waitNotify(monitor);
                    }
                    ret = this.result;
                } catch (LuaError e4) {
                    throw e4;
                }
            } catch (Throwable e5) {
                Log.e("Failed add list items", e5);
                ret = LuaValue.valueOf(Script.toString(e5));
            }
            try {
                items.clear();
                items.trimToSize();
            } catch (Throwable e6) {
                Log.badImplementation(e6);
            }
            this.result = null;
            return ret;
        }
    }

    final class getListItems extends ApiFunction {
        getListItems() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getListItems() -> table || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            try {
                final ArrayList<SavedItem> items = new ArrayList<>(MainService.instance.savedList.getCount());
                final Object monitor = new Object();
                synchronized (monitor) {
                    ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Script.getListItems.1
                        @Override // java.lang.Runnable
                        public void run() {
                            SavedListAdapter savedList = MainService.instance.savedList;
                            try {
                                int size = savedList.getCount();
                                for (int i = 0; i < size; i++) {
                                    SavedItem item = savedList.getItem(i);
                                    if (item != null) {
                                        items.add(item);
                                    }
                                }
                            } catch (Throwable e) {
                                Log.e("Failed get list items", e);
                            }
                            synchronized (monitor) {
                                monitor.notifyAll();
                            }
                        }
                    });
                    Script.waitNotify(monitor);
                }
                LuaTable ret = new LuaTable();
                ret.presize(items.size());
                int j = 1;
                for (SavedItem item : items) {
                    if (item != null) {
                        int j2 = j + 1;
                        ret.rawset(j, Script.fromSavedItem(item));
                        j = j2;
                    }
                }
                return ret;
            } catch (LuaError e) {
                throw e;
            } catch (Throwable e2) {
                Log.e("Failed get list items", e2);
                return LuaValue.valueOf(Script.toString(e2));
            }
        }
    }

    final class removeListItems extends ApiFunction {
        removeListItems() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.removeListItems(table items) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            try {
                LuaTable arr = args.checktable(1);
                arr.rawlen();
                final long[] list = new long[arr.getArrayLength() + arr.getHashEntries()];
                LuaString address = Script.inds[0];
                LuaTable.Iterator it = arr.iterator();
                int used = 0;
                while (it.next()) {
                    LuaValue el = it.value();
                    long addr = el.istable() ? Script.checklong((LuaTable) el, address) : el.checklong();
                    list[used] = addr;
                    used++;
                }
                final int cnt = used;
                final Object monitor = new Object();
                synchronized (monitor) {
                    ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Script.removeListItems.1
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                MainService.instance.savedList.remove(list, cnt);
                            } catch (Throwable e) {
                                Log.e("Failed remove list items", e);
                            }
                            synchronized (monitor) {
                                monitor.notifyAll();
                            }
                        }
                    });
                    Script.waitNotify(monitor);
                }
                return LuaValue.TRUE;
            } catch (LuaError e) {
                throw e;
            } catch (Throwable e2) {
                Log.e("Failed remove list items", e2);
                return LuaValue.valueOf(Script.toString(e2));
            }
        }
    }

    final class getSelectedListItems extends ApiFunction {
        getSelectedListItems() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getSelectedListItems() -> table || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            SavedItem item;
            LongSparseArrayChecked<SavedItem> list = MainService.instance.savedList.getList();
            LuaTable ret = new LuaTable();
            int i = 1;
            for (int j = 0; j < list.size(); j++) {
                try {
                    if (list.checkAt(j) && (item = list.valueAt(j)) != null) {
                        int i2 = i + 1;
                        try {
                            ret.rawset(i, Script.fromSavedItem(item));
                            i = i2;
                        } catch (IndexOutOfBoundsException e) {
                            i = i2;
                        }
                    }
                } catch (IndexOutOfBoundsException e2) {
                }
            }
            return ret;
        }
    }

    final class copyMemory extends BusyApiFunction {
        copyMemory() {
            super();
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 3;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.copyMemory(long from, long to, int bytes) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            MainService.instance.mDaemonManager.copyMemory(Script.this.getSeq(), args.checklong(1), args.checklong(2), args.checkint(3));
            return null;
        }

        @Override // android.ext.Script.ApiFunction
        protected boolean logArg(Writer log, int i, LuaValue arg) throws IOException {
            switch (i) {
                case 1:
                case 2:
                    if (arg.islong()) {
                        return logHex(log, arg);
                    }
                    break;
            }
            return super.logArg(log, i, arg);
        }
    }

    final class dumpMemory extends BusyApiFunction {
        dumpMemory() {
            super();
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 4;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.dumpMemory(long from, long to, string dir [, int flags = nil]) -> true || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            MainService service = MainService.instance;
            ProcessList.ProcessInfo info = service.processInfo;
            String prefix = info == null ? "unknown" : info.packageName;
            if (service.showBusyDialog()) {
                service.mDaemonManager.doDump(Script.this.getSeq(), args.checklong(1), args.checklong(2), args.optint(4, 0), args.checkjstring(3), prefix);
                return null;
            }
            return LuaValue.valueOf(Re.s(R.string.busy_dialog_fail));
        }

        @Override // android.ext.Script.ApiFunction
        protected boolean logArg(Writer log, int i, LuaValue arg) throws IOException {
            switch (i) {
                case 1:
                case 2:
                    if (arg.islong()) {
                        return logHex(log, arg);
                    }
                    break;
                case 4:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.DUMP_, arg);
                    }
                    break;
            }
            return super.logArg(log, i, arg);
        }
    }

    static final class sleep extends ApiFunction {
        sleep() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.sleep(int milliseconds) -> nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            int millis = args.checkint(1);
            if (millis < 0) {
                throw new LuaError("milliseconds < 0: " + millis);
            }
            try {
                Thread.sleep(millis);
                return LuaValue.NIL;
            } catch (InterruptedException e) {
                Log.w("Interrupted sleep", e);
                throw new LuaError(e);
            }
        }
    }

    static final class copyText extends ApiFunction {
        copyText() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 2;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.copyText(string text [, bool fixLocale = true]) -> nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            String text = args.checkjstring(1);
            if (args.optboolean(2, true)) {
                text = Script.numberFromLua(text);
            }
            Tools.copyText(Re.s(text));
            return LuaValue.NIL;
        }
    }

    final class disasm extends ApiFunction {
        disasm() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 3;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.disasm(int type, long address, int opcode) -> string";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            long address = args.checklong(2);
            int data = args.checkint(3);
            switch (args.checkint(1)) {
                case 4:
                    return LuaString.valueOf(ArmDis.getArmOpcode(new ArmDis(), address, data));
                case 5:
                    return LuaString.valueOf(ArmDis.getThumbOpcode(new ArmDis(), address, data));
                case 6:
                    StringBuilder out = new StringBuilder();
                    Arm64Dis.disasm(Arm64Dis.args(), address, data, out);
                    return LuaString.valueOf(out.toString());
                default:
                    return argerror(1, "gg.disasm", "unknown type: " + args.checkint(1));
            }
        }

        @Override // android.ext.Script.ApiFunction
        protected boolean logArg(Writer log, int i, LuaValue arg) throws IOException {
            switch (i) {
                case 1:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.ASM_, arg);
                    }
                    break;
                case 2:
                case 3:
                    if (arg.islong()) {
                        return logHex(log, arg);
                    }
                    break;
            }
            return super.logArg(log, i, arg);
        }
    }

    public static abstract class DebugFunction extends ApiFunction {
        protected LuaClosure caller = null;

        public void setCaller(LuaClosure caller) {
            this.caller = caller;
        }
    }

    final class getFile extends DebugFunction {
        getFile() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getFile() -> string";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            String file;
            if (this.caller == null) {
                file = Searcher.MASK_PLACEHOLDER;
            } else if (this.caller.sourceFile != null) {
                file = this.caller.sourceFile;
            } else if (this.caller.p != null) {
                file = this.caller.p.source != null ? this.caller.p.source.tojstring() : "??";
            } else {
                file = "???";
            }
            if (file.startsWith("@")) {
                file = file.substring(1);
            }
            return LuaValue.valueOf(file);
        }
    }

    final class getLine extends DebugFunction {
        getLine() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getLine() -> int";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            int line = -1;
            if (this.caller != null && this.caller.p != null) {
                line = (this.caller.p.lineinfo == null || this.caller.pc < 0 || this.caller.pc >= this.caller.p.lineinfo.length) ? -1 : this.caller.p.lineinfo[this.caller.pc];
            }
            return LuaValue.valueOf(line);
        }
    }

    final class bytes extends ApiFunction {
        bytes() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 2;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.bytes(string text [, string encoding = 'UTF-8']) -> table";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            String text = args.checkjstring(1);
            String encoding = args.optjstring(2, "UTF-8");
            try {
                byte[] bytes = text.getBytes(encoding);
                LuaTable out = new LuaTable();
                int s = bytes.length;
                out.presize(s);
                for (int i = 0; i < s; i++) {
                    out.rawset(i + 1, LuaValue.valueOf(bytes[i] & 255));
                }
                return out;
            } catch (UnsupportedEncodingException e) {
                throw new LuaError(e);
            }
        }
    }

    final class skipRestoreState extends ApiFunction {
        skipRestoreState() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.skipRestoreState() -> nil";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            Script.this.savedState = null;
            return LuaValue.NIL;
        }
    }

    final class getRangesList extends ApiFunction {
        getRangesList() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getRangesList([string filter = '']) -> table";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            String filter = args.optjstring(1, "");
            if (filter.length() > 0) {
                filter = Tools.quoteFilter(filter);
            }
            LuaTable out = new LuaTable();
            Pattern p = null;
            if (filter.length() > 0) {
                try {
                    p = Pattern.compile(filter);
                } catch (Throwable e) {
                    Log.badImplementation(e);
                }
            }
            LuaString state = LuaString.valueOf("state");
            LuaString start = LuaString.valueOf("start");
            LuaString end = LuaString.valueOf("end");
            LuaString type = LuaString.valueOf("type");
            LuaString name = LuaString.valueOf("name");
            LuaString internalName = LuaString.valueOf("internalName");
            List<RegionList.Region> list = RegionList.getList();
            int i = 1;
            for (RegionList.Region region : list) {
                String internal = region.getInternalName();
                if (p == null || p.matcher(internal).find()) {
                    LuaTable item = new LuaTable(0, 6);
                    item.rawset(state, LuaValue.valueOf(region.getState().toString()));
                    item.rawset(start, LuaValue.valueOf(region.start));
                    item.rawset(end, LuaValue.valueOf(region.end));
                    item.rawset(type, LuaValue.valueOf(region.getType()));
                    item.rawset(name, LuaValue.valueOf(region.getName()));
                    item.rawset(internalName, LuaValue.valueOf(internal));
                    out.set(i, item);
                    i++;
                }
            }
            return out;
        }
    }

    final class allocatePage extends BusyApiFunction {
        allocatePage() {
            super();
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 2;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.allocatePage([int mode = gg.PROT_READ | gg.PROT_EXEC [, long address = 0]) -> long || string with error";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invokeUi(Varargs args) {
            int mode = args.optint(1, 6);
            long address = args.optlong(2, 0L);
            Script.allocatedPage = 0L;
            MainService.instance.mDaemonManager.allocatePage(Script.this.getSeq(), address, mode);
            return null;
        }

        @Override // android.ext.Script.ApiFunction
        protected Varargs getTrueResult() {
            long page = Script.allocatedPage;
            return (-1024 >= page || page > 0) ? LuaValue.valueOf(Script.allocatedPage) : LuaValue.valueOf(String.valueOf(Re.s(R.string.allocate_fail)) + ' ' + (-page));
        }

        @Override // android.ext.Script.ApiFunction
        protected boolean logArg(Writer log, int i, LuaValue arg) throws IOException {
            switch (i) {
                case 1:
                    if (arg.isint()) {
                        return logConst(log, Script.this.consts.PROT_, arg);
                    }
                    break;
                case 2:
                    if (arg.islong()) {
                        return logHex(log, arg);
                    }
                    break;
            }
            return super.logArg(log, i, arg);
        }
    }

    final class getLocale extends ApiFunction {
        getLocale() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 0;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.getLocale() -> string";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            return LuaValue.valueOf(Re.s(R.string.lang_code).replace("~", ""));
        }
    }

    final class numberToLocale extends ApiFunction {
        numberToLocale() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.numberToLocale(string num) -> string";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            return LuaValue.valueOf(Script.numberFromLua(args.tojstring(1)));
        }
    }

    final class numberFromLocale extends ApiFunction {
        numberFromLocale() {
        }

        @Override // android.ext.Script.ApiFunction
        protected int getMaxArgs() {
            return 1;
        }

        @Override // android.ext.Script.ApiFunction
        String usage() {
            return "gg.numberFromLocale(string num) -> string";
        }

        @Override // android.ext.Script.ApiFunction
        public Varargs invoke2(Varargs args) {
            return LuaValue.valueOf(Script.numberToLua(args.tojstring(1)));
        }
    }

    static int checkSign(int sign) {
        switch (sign) {
            case AddressItem.FLAG_VALUE_GREATER_OR_EQUAL /* 67108864 */:
            case 134217728:
            case AddressItem.FLAG_VALUE_NOT_EQUAL /* 268435456 */:
            case AddressItem.FLAG_VALUE_EQUAL /* 536870912 */:
                return sign;
            default:
                throw new LuaError("Unknown sign: " + sign + ". The sign must be one of the constants gg.SIGN_*.");
        }
    }

    static int checkType(int type) {
        if (type == 0 || (type & AddressItem.FLAG_ALL_OTHERS) != 0) {
            throw new LuaError("Unknown type: " + type + ". The type must be one of the constants gg.TYPE_*.");
        }
        return type;
    }

    public static void loadData(BufferReader reader) {
        reader.reset();
        ArrayList<AddressItem> list = new ArrayList<>();
        while (true) {
            try {
                int flags = reader.readInt();
                if (flags == 0) {
                    break;
                }
                long address = reader.readLong();
                long data = reader.readLongLong();
                list.add(new AddressItem(address, data, flags));
            } catch (IOException e) {
                Log.e("???", e);
                ExceptionHandler.sendException(Thread.currentThread(), e, false);
            }
        }
        memList = list;
        memListUse += 100;
    }

    public static void allocatedPage(long page) {
        allocatedPage = page;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static LuaTable fromItem(int[] itemSlots2, AddressItem item) {
        if (itemSlots2 == null) {
            itemSlots2 = itemSlots;
        }
        int size = itemSlots2.length;
        LuaString[] inds2 = inds;
        LuaTable table = new LuaTable(0, size);
        long address = item.address;
        int type = item.flags;
        table.unsafehashset(itemSlots2[0], inds2[0], address);
        setValue(itemSlots2[1], table, inds2[1], address, item.data, type);
        table.unsafehashset(itemSlots2[2], inds2[2], type);
        return table;
    }

    private static void setValue(int index, LuaTable table, LuaString key, long address, long data, int type) {
        if (type == 8) {
            data ^= address;
        }
        if (type == 16) {
            table.unsafehashset(index, key, Float.intBitsToFloat((int) data));
        } else if (type == 64) {
            table.unsafehashset(index, key, Double.longBitsToDouble(data));
        } else {
            table.unsafehashset(index, key, DisplayNumbers.longToSigned(data, AddressItem.getPow(type), false));
        }
    }

    static LuaTable fromSavedItem(SavedItem item) {
        LuaString[] inds2 = inds;
        int[] savedItemSlots2 = savedItemSlots;
        LuaTable table = fromItem(savedItemSlots2, item);
        String name = item.name;
        if (name != null) {
            table.hashset(inds2[3], LuaValue.valueOf(name));
        }
        table.unsafehashset(savedItemSlots2[4], inds2[4], item.freeze);
        table.unsafehashset(savedItemSlots2[5], inds2[5], (int) item.freezeType);
        long address = item.address;
        int type = item.flags;
        if (item.freezeFrom != 0 || item.freezeTo != 0) {
            setValue(savedItemSlots2[6], table, inds2[6], address, item.freezeFrom, type);
            setValue(savedItemSlots2[7], table, inds2[7], address, item.freezeTo, type);
        }
        return table;
    }

    static String numberFromLua(String number) {
        return (ParserNumbers.isString(number) || ParserNumbers.typeAsm(number) != 0) ? number : ParserNumbers.fixSeparatorsToLocale(number);
    }

    static String numberToLua(String number) {
        return ParserNumbers.fixSeparators(number);
    }

    static AddressItem toItem(AddressItem item, LuaTable table, byte needValue) {
        long data;
        LuaValue[] inds2 = inds;
        long address = checklong(table, inds2[0]);
        int type = checkType(checkint(table, inds2[2]));
        if (needValue != 2) {
            LuaValue key = inds2[1];
            data = getData(address, key, table.get(key), type, needValue == 0);
        } else {
            data = 0;
        }
        if (item == null) {
            return new AddressItem(address, data, type);
        }
        item.address = address;
        item.data = data;
        item.flags = type;
        return item;
    }

    private static long getData(long address, LuaValue key, LuaValue val, int type, boolean def) {
        long data;
        long data2;
        if (val instanceof LuaLong) {
            long v = val.tolong();
            if (type == 16) {
                data2 = Float.floatToRawIntBits(v);
            } else if (type == 64) {
                data2 = Double.doubleToRawLongBits(v);
            } else {
                data2 = v;
            }
            if (type == 8) {
                return data2 ^ address;
            }
            return data2;
        }
        if (val instanceof LuaDouble) {
            double v2 = val.todouble();
            if (type == 16) {
                data = Float.floatToRawIntBits((float) v2);
            } else if (type == 64) {
                data = Double.doubleToRawLongBits(v2);
            } else {
                data = (long) v2;
            }
            if (type == 8) {
                return data ^ address;
            }
            return data;
        }
        try {
            String str = def ? val.optjstring(null) : val.checkjstring();
            if (str == null) {
                return 0L;
            }
            String str2 = numberFromLua(str);
            SearchButtonListener.XorMode xorMode = SearchButtonListener.parseXorMode(str2, false);
            if (xorMode != null) {
                str2 = xorMode.input;
            }
            long data3 = AddressItem.dataFromString(address, str2, type);
            return data3;
        } catch (LuaError e) {
            throw badArg(key, e);
        }
    }

    static SavedItem toSavedItem(LuaTable table) {
        LuaValue[] inds2 = inds;
        SavedItem item = new SavedItem();
        toItem(item, table, (byte) 0);
        item.name = optjstring(table, inds2[3], null);
        item.freeze = optboolean(table, inds2[4], false);
        item.setFreezeType(optint(table, inds2[5], 0));
        long address = item.address;
        int type = item.flags;
        LuaValue key = inds2[6];
        item.freezeFrom = getData(address, key, table.get(key), type, true);
        LuaValue key2 = inds2[7];
        item.freezeTo = getData(address, key2, table.get(key2), type, true);
        return item;
    }

    static LuaError failedParse(LuaValue key, LuaValue table, Throwable e) {
        return new LuaError("Failed parse item " + table.tojstring() + " on key '" + key.tojstring() + "': " + e.getMessage(), e);
    }

    private static LuaError badArg(LuaValue key, LuaError e) {
        String msg = e.getMessage();
        if (msg.indexOf("bad argument:") == 0) {
            return new LuaError("bad argument for key '" + key.tojstring() + "':" + msg.substring("bad argument:".length()));
        }
        return e;
    }

    static boolean optboolean(LuaTable table, LuaValue key, boolean defval) {
        try {
            return table.get(key).optboolean(defval);
        } catch (LuaError e) {
            throw badArg(key, e);
        }
    }

    static int optint(LuaTable table, LuaValue key, int defval) {
        try {
            return table.get(key).optint(defval);
        } catch (LuaError e) {
            throw badArg(key, e);
        }
    }

    static String optjstring(LuaTable table, LuaValue key, String defval) {
        try {
            return table.get(key).optjstring(defval);
        } catch (LuaError e) {
            throw badArg(key, e);
        }
    }

    static int checkint(LuaTable table, LuaValue key) {
        try {
            return table.get(key).checkint();
        } catch (LuaError e) {
            throw badArg(key, e);
        }
    }

    static long checklong(LuaTable table, LuaValue key) {
        try {
            return table.getchecklong(key);
        } catch (LuaError e) {
            throw badArg(key, e);
        }
    }

    static String checkjstring(LuaTable table, LuaValue key) {
        try {
            return table.get(key).checkjstring();
        } catch (LuaError e) {
            throw badArg(key, e);
        }
    }

    static String toString(Throwable e) {
        if (e instanceof ParserNumbers.NumberFormatException_) {
            ((ParserNumbers.NumberFormatException_) e).forLua();
        }
        return e.toString();
    }

    void appendLog(String data) {
        this.log.append(ListManager.NEW_LINE);
        this.log.append(data);
        this.log.append(ListManager.NEW_LINE);
    }

    private static class SavedState {
        int memoryRanges;

        private SavedState() {
        }

        /* synthetic */ SavedState(SavedState savedState) {
            this();
        }
    }

    void saveState() {
        SavedState state = new SavedState(null);
        state.memoryRanges = Config.usedRanges;
        this.savedState = state;
    }

    void restoreState() {
        final SavedState state = this.savedState;
        if (state != null) {
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Script.1
                @Override // java.lang.Runnable
                public void run() {
                    int value = state.memoryRanges;
                    Config.get(R.id.config_ranges).value = value;
                    Config.save();
                }
            });
        }
    }

    boolean isSavedList(File file) {
        if (!file.exists()) {
            return false;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int status = 0;
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                String line2 = line.trim();
                if (line2.length() != 0) {
                    if (status == 0) {
                        try {
                            Integer.parseInt(line2);
                            status = 1;
                        } catch (Throwable th) {
                        }
                    } else {
                        if (line2.split("\\|").length < 10) {
                            status = -status;
                            break;
                        }
                        status++;
                    }
                }
            }
            reader.close();
            return status > 1;
        } catch (IOException e) {
            Log.d("Failed detect", e);
            return false;
        }
    }

    class ScriptThread extends Thread {
        public ScriptThread() {
            super("Script thread");
            ExceptionHandler.install(this);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            String trace = "";
            String line;
            MainService service = MainService.instance;
            try {
                service.onScriptStart(Script.this);
                Throwable th = Main.notLatest.booleanValue() ? null : null;
                Script.this.saveState();
                try {
                    Script.this.runScript();
                } catch (OsExit e) {
                    Log.e("Script interrupted by self: " + e.getMessage());
                    Script.this.appendLog(String.valueOf(Re.s(R.string.script_exit)) + (e.code == 0 ? "." : ": " + e.code));
                } catch (LuaError e2) {
                    Throwable cause = e2.getCause();
                    if ((cause instanceof InterruptedException) && Script.this.interruptedReason != 0) {
                        Log.e("Script thread interrupted: " + Script.this.interruptedReason, cause);
                        Script.this.appendLog(String.valueOf(Re.s(R.string.script_interrupted)) + " [" + Script.this.interruptedReason + "]");
                    } else {
                        if (e2 instanceof LuaError.Internal) {
                            ExceptionHandler.sendException(Thread.currentThread(), e2, false);
                        }
                        th = e2;
                    }
                } catch (Throwable e3) {
                    th = e3;
                }
                int flags = 0;
                if (th != null) {
                    try {
                        Log.e("Script error", th);
                        trace = Log.getStackTraceString(th);
                        String msg = th.getMessage();
                        if (msg != null && !trace.contains(msg)) {
                            trace = String.valueOf(msg) + '\n' + trace;
                        }
                        if (th instanceof LuaError) {
                            int possible = 0;
                            if (msg != null && (msg.contains("<!DOCTYPE") || msg.contains("<html"))) {
                                possible = R.string.script_web;
                            }
                            if ((Script.this.source instanceof File) && Script.this.isSavedList((File) Script.this.source)) {
                                possible = R.string.script_saved_list;
                            }
                            if (possible != 0) {
                                Script.this.appendLog(Tools.stringFormat(Re.s(R.string.script_possible), Re.s(possible)));
                            }
                        }
                        if ((Script.this.source instanceof File) && (line = Lasm.getLine(th, ((File) Script.this.source).getAbsolutePath(), true)) != null && line.length() > 0) {
                            trace = String.valueOf(line) + trace;
                            flags = 0 | 4;
                        }
                    } catch (OutOfMemoryError e4) {
                        try {
                            trace = String.valueOf(e4.toString()) + '\n';
                        } catch (OutOfMemoryError e5) {
                            Script.this.appendLog(String.valueOf(Tools.stripColon(R.string.script_oom)) + ": " + e5.toString());
                        }
                    }
                    if (Main.notLatest.booleanValue()) {
                        Script.this.appendLog(Re.s(R.string.try_latest));
                    }
                    Script.this.appendLog(String.valueOf(Tools.stripColon(R.string.script_error)) + ": " + trace);
                }
                if (Script.this.globals.openFiles != 0 || Script.this.globals.writeBytes != 0) {
                    Script.this.appendLog(Tools.stringFormat(Re.s(R.string.script_wrote), Tools.formatFileSize(Tools.getContext(), Script.this.globals.writeBytes), Integer.valueOf(Script.this.globals.openFiles)));
                }
                Script.this.restoreState();
                String action = null;
                DialogInterface.OnClickListener listener = null;
                if (Script.this.source instanceof File) {
                    action = Re.s(R.string.restart);
                    listener = new DialogInterface.OnClickListener() { // from class: android.ext.Script.ScriptThread.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            File file = (File) Script.this.source;
                                try {
                                    if (Tools.isFile(file)) {
                                        MainService.instance.executeScript(file.getAbsolutePath(), Script.this.params, Script.this.out);
                                    }
                                } catch (Throwable e) {
                                    Log.badImplementation(e);
                                }
                        }
                    };
                }
                final String finMessage = String.valueOf(Tools.stripColon(R.string.script_ended)) + ":\n" + Script.this.log.toString().trim();
                final int finFlags = flags;
                final String finAction = action;
                final DialogInterface.OnClickListener finListener = listener;
                service.showScriptEnd(new Runnable() { // from class: android.ext.Script.ScriptThread.2
                    @Override // java.lang.Runnable
                    public void run() {
                        Tools.alertBigText(finMessage, finFlags, finAction, finListener);
                    }
                });
            } catch (Throwable e6) {
                Log.e("Script run exception", e6);
            }
            service.onScriptEnd(Script.this);
        }
    }

    public static class Logger {
        private static final int MAX_SIZE = 262144;
        final FileInputStream fis;
        final OutputStream os;
        int size;
        final StringBuilder str;

        Logger() {
            FileInputStream fis = null;
            StringBuilder str = null;
            OutputStream os = null;
            try {
                File log = new File(Tools.getFilesDir(), String.valueOf(System.currentTimeMillis()) + OsLib.TMP_SUFFIX);
                log.createNewFile();
                os = new BufferedOutputStream(new FileOutputStream(log));
                fis = new FileInputStream(log);
                log.delete();
            } catch (Throwable e) {
                Log.badImplementation(e);
                str = new StringBuilder();
            }
            this.str = str;
            this.os = os;
            this.fis = fis;
            this.size = 0;
        }

        public void append(String line) {
            StringBuilder str = this.str;
            if (str == null) {
                try {
                    byte[] data = line.getBytes();
                    this.os.write(data);
                    this.size += data.length;
                    return;
                } catch (Throwable e) {
                    Log.badImplementation(e);
                    return;
                }
            }
            str.append(line);
        }

        public String toString() {
            if (this.size < 0) {
                return "must be called only once";
            }
            StringBuilder str = this.str;
            if (str == null) {
                try {
                    int size = this.size;
                    this.size = -1;
                    this.os.close();
                    if (size > 262144) {
                        this.fis.skip(size - 262144);
                        size = 262144;
                    }
                    byte[] buf = new byte[size];
                    int read = this.fis.read(buf);
                    this.fis.close();
                    Log.d("Log script size: read = " + read + "; used = " + size + "; full = " + size);
                    if (read > 0) {
                        return new String(buf, 0, read);
                    }
                } catch (Throwable e) {
                    Log.badImplementation(e);
                }
                return "";
            }
            return str.toString();
        }
    }

    public static void resetMetatables(Globals globals) {
        LuaNil.s_metatable = null;
        LuaNumber.s_metatable = null;
        LuaBoolean.s_metatable = null;
        LuaValue string = globals == null ? null : globals.get("string");
        LuaString.s_metatable = (string == null || string.isnil()) ? null : LuaValue.tableOf(new LuaValue[]{INDEX, string});
        LuaFunction.s_metatable = null;
        LuaThread.s_metatable = null;
    }

    private static final class DebugLog {
        final Globals globals;
        final Writer log;

        DebugLog(Writer log, Globals globals) {
            this.log = log;
            this.globals = globals;
        }
    }

    void runScript() throws IOException {
        LuaValue chunk;
        String name;
        if (this.source instanceof File) {
            File file = (File) this.source;
            if (!file.exists()) {
                throw new FileNotFoundException("File not found: " + file);
            }
            this.globals.setWorkDir(file.getParentFile().getAbsolutePath());
            chunk = this.globals.loadfile(file.getAbsolutePath());
            if ((chunk instanceof LuaClosure) && (((LuaClosure) chunk).p instanceof LasmPrototype)) {
                String name2 = String.valueOf(file.getAbsolutePath()) + ParserNumbers.defaultDecimalSeparator + ToolsLight.toString64(System.currentTimeMillis() - unixTsOffset) + ".lua";
                OutputStream os = new BufferedOutputStream(new FileOutputStream(name2), 65536);
                try {
                    DumpState.dump(((LuaClosure) chunk).p, os, false, false);
                    File tail = new File(String.valueOf(file.getAbsolutePath()) + ".tail");
                    if (tail.exists()) {
                        FileInputStream in = new FileInputStream(tail);
                        byte[] buf = new byte[65536];
                        while (true) {
                            int read = in.read(buf);
                            if (read <= 0) {
                                break;
                            } else {
                                os.write(buf, 0, read);
                            }
                        }
                        in.close();
                    }
                    os.close();
                    appendLog(String.valueOf(Tools.stripColon(R.string.assembled)) + ":\n" + name2);
                    return;
                } catch (Throwable th) {
                    os.close();
                    throw th;
                }
            }
            name = file.getName();
        } else {
            String str = this.source.toString();
            chunk = this.globals.load(new ByteArrayInputStream(str.getBytes()), str, "t", this.globals);
            name = "string";
        }
        String name3 = String.valueOf(name) + '_' + ToolsLight.toString64(System.currentTimeMillis() - unixTsOffset) + '_' + ToolsLight.toString64(Tools.RANDOM.nextInt());
        if ((this.params & 1) != 0) {
            LuaClosure func = chunk.checkclosure();
            String filename = String.valueOf(this.out) + '/' + name3 + ".lasm";
            PrintStream prev = Print.ps;
            try {
                Print.ps = new PrintStream(filename);
                Print.print(func.p);
                Print.ps.close();
                Print.saveTail(func, String.valueOf(filename) + ".tail");
            } catch (Throwable e) {
                Log.e("Failed disassemble " + func + " into '" + filename + SearchMenuItem.HEX_UTF_8, e);
            }
            Print.ps = prev;
        }
        if ((this.params & 2) != 0) {
            this.globals.baselib.setDebugLoad(String.valueOf(this.out) + '/' + name3);
        }
        Writer log = null;
        if ((this.params & 4) != 0) {
            try {
                log = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(String.valueOf(this.out) + '/' + name3 + ".log.txt")), 65536);
            } catch (Throwable e2) {
                Log.w("Failed open log file", e2);
            }
        }
        debugLog = log == null ? null : new DebugLog(log, this.globals);
        PrintStream prev2 = Print.ps;
        try {
            Print.ps = this.globals.STDOUT;
            resetMetatables(this.globals);
            chunk.call();
        } finally {
            Print.ps = prev2;
            if (log != null) {
                debugLog = null;
                try {
                    log.close();
                } catch (Throwable e3) {
                    Log.w("Failed close log", e3);
                }
            }
            if ((this.params & 2) != 0) {
                this.globals.baselib.endDebugLoad();
            }
            resetMetatables(null);
        }
    }

    public Script start() {
        this.thread.start();
        return this;
    }

    public Script interrupt() {
        return interrupt(1);
    }

    public Script interrupt(int reason) {
        this.interruptedReason = reason;
        this.thread.interrupt();
        return this;
    }

    public static String oneLiner(String script) {
        Globals globals = new Globals();
        globals.fullVersion = false;
        LuaC.install(globals);
        globals.load(new BaseLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new Utf8Lib());
        globals.load(new MathLib());
        globals.load(new OsLib());
        LuaValue chunk = globals.load(new ByteArrayInputStream(script.getBytes()), script, "t", globals);
        return chunk.call().tojstring();
    }

    public static void doTest() {
    }
}
