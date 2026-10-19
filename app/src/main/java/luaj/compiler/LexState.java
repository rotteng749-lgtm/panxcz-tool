package luaj.compiler;

import android.ext.Message;
import android.ext.SearchMenuItem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Hashtable;
import lasm.LasmConstants;
import luaj.LocVars;
import luaj.Lua;
import luaj.LuaDouble;
import luaj.LuaError;
import luaj.LuaLong;
import luaj.LuaString;
import luaj.LuaValue;
import luaj.Prototype;
import luaj.compiler.FuncState;
import luaj.compiler.LuaC;

/* JADX INFO: loaded from: classes.dex */
public class LexState extends Constants {
    private static final int EOZ = -1;
    static final int FIRST_RESERVED = 257;
    private static final int LUAI_MAXCCALLS = 200;
    private static final int LUA_COMPAT_LSTR = 1;
    private static final boolean LUA_COMPAT_VARARG = true;
    private static final int MAX_INT = 2147483645;
    static final int NO_JUMP = -1;
    static final int NUM_RESERVED = 22;
    static final int OPR_ADD = 0;
    static final int OPR_AND = 13;
    static final int OPR_BAND = 16;
    static final int OPR_BNOT = 4;
    static final int OPR_BOR = 17;
    static final int OPR_BXOR = 18;
    static final int OPR_CONCAT = 6;
    static final int OPR_DIV = 3;
    static final int OPR_EQ = 8;
    static final int OPR_GE = 12;
    static final int OPR_GT = 11;
    static final int OPR_IDIV = 15;
    static final int OPR_LE = 10;
    static final int OPR_LEN = 2;
    static final int OPR_LT = 9;
    static final int OPR_MINUS = 0;
    static final int OPR_MOD = 4;
    static final int OPR_MUL = 2;
    static final int OPR_NE = 7;
    static final int OPR_NOBINOPR = 21;
    static final int OPR_NOT = 1;
    static final int OPR_NOUNOPR = 3;
    static final int OPR_OR = 14;
    static final int OPR_POW = 5;
    static final int OPR_SHL = 19;
    static final int OPR_SHR = 20;
    static final int OPR_SUB = 1;
    static final Hashtable<LuaString, Integer> RESERVED;
    static final int TK_AND = 257;
    static final int TK_BREAK = 258;
    static final int TK_CONCAT = 280;
    static final int TK_DBCOLON = 288;
    static final int TK_DO = 259;
    static final int TK_DOTS = 281;
    static final int TK_ELSE = 260;
    static final int TK_ELSEIF = 261;
    static final int TK_END = 262;
    static final int TK_EOF = 289;
    static final int TK_EQ = 282;
    static final int TK_FALSE = 263;
    static final int TK_FOR = 264;
    static final int TK_FUNCTION = 265;
    static final int TK_GE = 283;
    static final int TK_GOTO = 266;
    static final int TK_IDIV = 279;
    static final int TK_IF = 267;
    static final int TK_IN = 268;
    static final int TK_INTEGER = 291;
    static final int TK_LE = 284;
    static final int TK_LOCAL = 269;
    static final int TK_NAME = 292;
    static final int TK_NE = 285;
    static final int TK_NIL = 270;
    static final int TK_NOT = 271;
    static final int TK_NUMBER = 290;
    static final int TK_OR = 272;
    static final int TK_REPEAT = 273;
    static final int TK_RETURN = 274;
    static final int TK_SHL = 286;
    static final int TK_SHR = 287;
    static final int TK_STRING = 293;
    static final int TK_THEN = 275;
    static final int TK_TRUE = 276;
    static final int TK_UNTIL = 277;
    static final int TK_WHILE = 278;
    private static final int UCHAR_MAX = 255;
    static final int UNARY_PRIORITY = 80;
    static final int VCALL = 12;
    static final int VFALSE = 3;
    static final int VINDEXED = 9;
    static final int VJMP = 10;
    static final int VK = 4;
    static final int VKNUM = 5;
    static final int VLOCAL = 7;
    static final int VNIL = 1;
    static final int VNONRELOC = 6;
    static final int VRELOCABLE = 11;
    static final int VTRUE = 2;
    static final int VUPVAL = 8;
    static final int VVARARG = 13;
    static final int VVOID = 0;
    static final String[] luaX_tokens;
    static Priority[] priority;
    LuaC.CompileState L;
    int current;
    byte decpoint;
    LuaString envn;
    FuncState fs;
    int lastline;
    int linenumber;
    final Token lookahead;
    int nbuff;
    LuaString source;
    final Token t;
    InputStream z;
    protected static final String RESERVED_LOCAL_VAR_FOR_CONTROL = "(for control)";
    protected static final String RESERVED_LOCAL_VAR_FOR_GENERATOR = "(for generator)";
    protected static final String RESERVED_LOCAL_VAR_FOR_INDEX = "(for index)";
    protected static final String RESERVED_LOCAL_VAR_FOR_LIMIT = "(for limit)";
    protected static final String RESERVED_LOCAL_VAR_FOR_STATE = "(for state)";
    protected static final String RESERVED_LOCAL_VAR_FOR_STEP = "(for step)";
    protected static final String RESERVED_LABEL_BREAK = "break";
    protected static final String[] RESERVED_LOCAL_VAR_KEYWORDS = {RESERVED_LOCAL_VAR_FOR_CONTROL, RESERVED_LOCAL_VAR_FOR_GENERATOR, RESERVED_LOCAL_VAR_FOR_INDEX, RESERVED_LOCAL_VAR_FOR_LIMIT, RESERVED_LOCAL_VAR_FOR_STATE, RESERVED_LOCAL_VAR_FOR_STEP, RESERVED_LABEL_BREAK};
    private static final Hashtable<String, Boolean> RESERVED_LOCAL_VAR_KEYWORDS_TABLE = new Hashtable<>();
    Dyndata dyd = new Dyndata();
    private CharBuffer charBuffer = new CharBuffer();
    private final int[] ec = new int[24];
    char[] buff = new char[32];

    static {
        for (int i = 0; i < RESERVED_LOCAL_VAR_KEYWORDS.length; i++) {
            RESERVED_LOCAL_VAR_KEYWORDS_TABLE.put(RESERVED_LOCAL_VAR_KEYWORDS[i], Boolean.TRUE);
        }
        luaX_tokens = new String[]{"and", RESERVED_LABEL_BREAK, "do", "else", "elseif", "end", "false", "for", "function", "goto", "if", "in", "local", "nil", "not", "or", "repeat", "return", "then", "true", "until", "while", "//", "..", "...", "==", ">=", "<=", "~=", "<<", ">>", "::", "<eof>", "<number>", "<integer>", "<name>", "<string>"};
        RESERVED = new Hashtable<>();
        for (int i2 = 0; i2 < 22; i2++) {
            LuaString ts = LuaValue.valueOf(luaX_tokens[i2]);
            RESERVED.put(ts, Integer.valueOf(i2 + 257));
        }
        priority = new Priority[]{new Priority(60, 60), new Priority(60, 60), new Priority(70, 70), new Priority(70, 70), new Priority(70, 70), new Priority(100, 90), new Priority(50, 40), new Priority(30, 30), new Priority(30, 30), new Priority(30, 30), new Priority(30, 30), new Priority(30, 30), new Priority(30, 30), new Priority(20, 20), new Priority(10, 10), new Priority(70, 70), new Priority(36, 36), new Priority(32, 32), new Priority(34, 34), new Priority(38, 38), new Priority(38, 38)};
    }

    private static final String LUA_QS(String s) {
        return String.valueOf(SearchMenuItem.HEX_UTF_8) + s + SearchMenuItem.HEX_UTF_8;
    }

    private static final String LUA_QL(Object o) {
        return LUA_QS(String.valueOf(o));
    }

    public static boolean isReservedKeyword(String varName) {
        return RESERVED_LOCAL_VAR_KEYWORDS_TABLE.containsKey(varName);
    }

    private static class SemInfo {
        LuaValue r;
        LuaString ts;

        private SemInfo() {
        }

        /* synthetic */ SemInfo(SemInfo semInfo) {
            this();
        }
    }

    private static class Token {
        final SemInfo seminfo;
        int token;

        private Token() {
            this.seminfo = new SemInfo(null);
        }

        /* synthetic */ Token(Token token) {
            this();
        }

        public void set(Token other) {
            this.token = other.token;
            this.seminfo.r = other.seminfo.r;
            this.seminfo.ts = other.seminfo.ts;
        }
    }

    public static boolean isalnum(int c) {
        return (c >= 48 && c <= 57) || (c >= 97 && c <= 122) || ((c >= 65 && c <= 90) || c == 95);
    }

    public static boolean isalpha(int c) {
        return (c >= 97 && c <= 122) || (c >= 65 && c <= 90);
    }

    public static boolean isdigit(int c) {
        return c >= 48 && c <= 57;
    }

    public static boolean isxdigit(int c) {
        return (c >= 48 && c <= 57) || (c >= 97 && c <= 102) || (c >= 65 && c <= 70);
    }

    public static boolean isspace(int c) {
        return c >= 0 && c <= 32;
    }

    public LexState(LuaC.CompileState state, InputStream stream) {
        Token token = null;
        this.t = new Token(token);
        this.lookahead = new Token(token);
        this.z = stream;
        this.L = state;
    }

    void nextChar() {
        try {
            this.current = this.z.read();
        } catch (IOException e) {
            e.printStackTrace();
            this.current = -1;
        }
    }

    boolean currIsNewline() {
        return this.current == 10 || this.current == 13;
    }

    void save_and_next() {
        save(this.current);
        nextChar();
    }

    void save(int c) {
        char[] buff = this.buff;
        int nbuff = this.nbuff;
        if (buff == null || nbuff + 1 > buff.length) {
            buff = realloc(buff, (nbuff * 2) + 1);
            this.buff = buff;
        }
        buff[nbuff] = (char) c;
        this.nbuff = nbuff + 1;
    }

    String token2str(int token) {
        if (token < 257) {
            if (iscntrl(token)) {
                return this.L.pushfstring("'<\\" + token + ">' (symbol with decimal code " + token + " or 0x" + Integer.toHexString(token) + ")");
            }
            return this.L.pushfstring(String.valueOf((char) token));
        }
        String ret = luaX_tokens[token - 257];
        switch (token) {
            case TK_EOF /* 289 */:
            case TK_NUMBER /* 290 */:
            case TK_INTEGER /* 291 */:
            case TK_NAME /* 292 */:
            case TK_STRING /* 293 */:
                return ret;
            default:
                return String.valueOf(SearchMenuItem.HEX_UTF_8) + ret + SearchMenuItem.HEX_UTF_8;
        }
    }

    private static boolean iscntrl(int token) {
        return token < 32 || token == 255;
    }

    String txtToken(int token) {
        switch (token) {
            case TK_NUMBER /* 290 */:
            case TK_INTEGER /* 291 */:
            case TK_NAME /* 292 */:
            case TK_STRING /* 293 */:
                return String.valueOf(SearchMenuItem.HEX_UTF_8) + new String(this.buff, 0, this.nbuff) + SearchMenuItem.HEX_UTF_8;
            default:
                return token2str(token);
        }
    }

    void lexerror(String msg, int token) {
        String cid = Lua.chunkid(this.source.tojstring());
        this.L.pushfstring(String.valueOf(cid) + ':' + this.linenumber + ": " + msg);
        if (token != 0) {
            msg = String.valueOf(msg) + " near " + txtToken(token);
            this.L.pushfstring("syntax error: " + msg);
        }
        throw new LuaError(cid, this.linenumber, msg);
    }

    void syntaxerror(String msg) {
        lexerror(msg, this.t.token);
    }

    LuaString newstring(String s) {
        return this.L.newTString(s);
    }

    LuaString newstring(char[] chars, int offset, int len) {
        return this.L.newTString(new String(chars, offset, len));
    }

    void inclinenumber() {
        int old = this.current;
        _assert(currIsNewline());
        nextChar();
        if (currIsNewline() && this.current != old) {
            nextChar();
        }
        int i = this.linenumber + 1;
        this.linenumber = i;
        if (i >= MAX_INT) {
            syntaxerror("chunk has too many lines (> 2147483645)");
        }
    }

    void setinput(LuaC.CompileState L, int firstByte, InputStream z, LuaString source) {
        this.decpoint = Message.CMD_CS_CLEAR_LOCK_LIST;
        this.L = L;
        this.lookahead.token = TK_EOF;
        this.z = z;
        this.fs = null;
        this.linenumber = 1;
        this.lastline = 1;
        this.source = source;
        this.envn = LuaValue.ENV;
        this.nbuff = 0;
        this.current = firstByte;
        skipShebang();
    }

    private void skipShebang() {
        if (this.current == 35) {
            while (!currIsNewline() && this.current != -1) {
                nextChar();
            }
        }
    }

    boolean check_next(String set) {
        if (set.indexOf(this.current) < 0) {
            return false;
        }
        save_and_next();
        return true;
    }

    void buffreplace(char from, char to) {
        int n = this.nbuff;
        char[] p = this.buff;
        while (true) {
            n--;
            if (n >= 0) {
                if (p[n] == from) {
                    p[n] = to;
                }
            } else {
                return;
            }
        }
    }

    static class CharBuffer implements CharSequence {
        char[] buff;
        int nbuff;

        CharBuffer() {
        }

        @Override // java.lang.CharSequence
        public CharSequence subSequence(int start, int end) {
            return null;
        }

        @Override // java.lang.CharSequence
        public int length() {
            return this.nbuff;
        }

        @Override // java.lang.CharSequence
        public char charAt(int index) {
            return this.buff[index];
        }
    }

    void read_numeral(SemInfo seminfo, boolean int_) {
        LuaValue val;
        int first = this.current;
        save_and_next();
        boolean hex = first == 48 && check_next("Xx");
        String expo = hex ? "Pp" : "Ee";
        while (true) {
            if (check_next(expo)) {
                int_ = false;
                check_next("+-");
            }
            int curr = this.current;
            if ((curr >= 48 && curr <= 57) || (hex && ((curr >= 97 && curr <= 102) || (curr >= 65 && curr <= 70)))) {
                save_and_next();
            } else {
                if (curr != 46) {
                    break;
                }
                int_ = false;
                save_and_next();
            }
        }
        if (!hex && int_ && this.nbuff > 19) {
            int_ = false;
        }
        try {
            if (hex) {
                CharBuffer charBuffer = this.charBuffer;
                charBuffer.buff = this.buff;
                charBuffer.nbuff = this.nbuff;
                val = LuaValue.parseHex(charBuffer);
            } else if (int_) {
                long v = 0;
                char[] buff = this.buff;
                int nbuff = this.nbuff;
                if (nbuff != 19) {
                    for (int j = 0; j < nbuff; j++) {
                        v = (10 * v) + ((long) (buff[j] - '0'));
                    }
                    val = LuaLong.valueOf(v);
                } else {
                    for (int j2 = 0; j2 < 18; j2++) {
                        v = (10 * v) + ((long) (buff[j2] - '0'));
                    }
                    long prev = v;
                    long v2 = (10 * v) + ((long) (buff[18] - '0'));
                    if (v2 < prev) {
                        String str = new String(buff, 0, nbuff);
                        val = LuaDouble.valueOf(Double.parseDouble(str));
                    } else {
                        val = LuaLong.valueOf(v2);
                    }
                }
            } else {
                String str2 = new String(this.buff, 0, this.nbuff);
                val = LuaDouble.valueOf(Double.parseDouble(str2));
            }
            seminfo.r = val;
        } catch (NumberFormatException e) {
            lexerror("malformed number (" + e.getMessage() + ")", TK_NUMBER);
        }
    }

    int skip_sep() {
        int count = 0;
        int s = this.current;
        save_and_next();
        while (this.current == 61) {
            save_and_next();
            count++;
        }
        return this.current == s ? count : (-count) - 1;
    }

    void read_long_string(SemInfo seminfo, int sep) {
        int cont = 0;
        save_and_next();
        if (currIsNewline()) {
            inclinenumber();
        }
        boolean endloop = false;
        while (!endloop) {
            switch (this.current) {
                case -1:
                    lexerror(seminfo != null ? "unfinished long string" : "unfinished long comment", TK_EOF);
                    break;
                case 10:
                case 13:
                    save(10);
                    inclinenumber();
                    if (seminfo == null) {
                        this.nbuff = 0;
                    }
                    break;
                case 91:
                    if (skip_sep() == sep) {
                        save_and_next();
                        cont++;
                        if (sep == 0) {
                            lexerror("nesting of [[...]] is deprecated", 91);
                        }
                    }
                    break;
                case 93:
                    if (skip_sep() == sep) {
                        save_and_next();
                        endloop = true;
                    }
                    break;
                default:
                    if (seminfo != null) {
                        save_and_next();
                    } else {
                        nextChar();
                    }
                    break;
            }
        }
        if (seminfo != null) {
            seminfo.ts = this.L.newTString(LuaString.valueOf(this.buff, sep + 2, this.nbuff - ((sep + 2) * 2)));
        }
    }

    public static int hexvalue(int c) {
        return c <= 57 ? c - 48 : c <= 70 ? (c + 10) - 65 : (c + 10) - 97;
    }

    void escerror(int n, String msg) {
        save(92);
        for (int i = 0; i < n && this.ec[i] != -1; i++) {
            save(this.ec[i]);
        }
        lexerror(msg, TK_STRING);
    }

    int readhexaesc() {
        int r = 0;
        for (int i = 1; i < 3; i++) {
            nextChar();
            int[] iArr = this.ec;
            int c = this.current;
            iArr[i] = c;
            if (!isxdigit(c)) {
                this.ec[0] = 120;
                escerror(i + 1, "hexadecimal digit expected");
            }
            r = (r << 4) + hexvalue(c);
        }
        return r;
    }

    int readdecesc() {
        int i;
        int r = 0;
        int i2 = 0;
        while (true) {
            i = i2;
            if (i >= 3 || !isdigit(this.current)) {
                break;
            }
            int[] iArr = this.ec;
            int c = this.current;
            iArr[i] = c;
            r = ((r * 10) + c) - 48;
            nextChar();
            i2 = i + 1;
        }
        if (r > 255) {
            this.ec[i] = this.current;
            escerror(i + 1, "decimal escape too large");
        }
        return r;
    }

    void escerrorutf8(int ch, int n, String msg) {
        this.ec[0] = 117;
        this.ec[1] = ch;
        escerror(n, msg);
    }

    void escerrorutf8(int n, String msg) {
        escerrorutf8(123, n, msg);
    }

    void readutf8esc() {
        int ch;
        nextChar();
        int ch2 = this.current;
        if (ch2 != 123) {
            escerrorutf8(ch2, 2, "missing '{', got '" + ((char) ch2) + "'");
        }
        int num = 0;
        int cnt = 2;
        while (true) {
            nextChar();
            if (cnt == 23) {
                escerrorutf8(cnt, "unicode escape too long");
            }
            int[] iArr = this.ec;
            ch = this.current;
            iArr[cnt] = ch;
            if (!isxdigit(ch)) {
                break;
            }
            num = (num << 4) + hexvalue(ch);
            if (num > 1114111) {
                escerrorutf8(cnt + 1, "UTF-8 value too large: 0x" + Integer.toHexString(num) + " (max allowed = 0x" + Integer.toHexString(1114111) + ")");
            }
            cnt++;
        }
        if (cnt == 2) {
            escerrorutf8(3, "hexadecimal digit expected, got '" + ((char) ch) + "'");
        }
        if (ch != 125) {
            escerrorutf8(cnt + 1, "missing '}', got '" + ((char) ch) + "'");
        }
        byte[] bytes = new String(Character.toChars(num)).getBytes();
        for (byte b : bytes) {
            save(b);
        }
        nextChar();
    }

    void read_string(int del, SemInfo seminfo) {
        int c = 0;
        save_and_next();
        while (this.current != del) {
            switch (this.current) {
                case -1:
                    lexerror("unfinished string", TK_EOF);
                    break;
                case 10:
                case 13:
                    lexerror("unfinished string", TK_STRING);
                    break;
                case 92:
                    nextChar();
                    switch (this.current) {
                        case -1:
                            break;
                        case 10:
                        case 13:
                            save(10);
                            inclinenumber();
                            continue;
                        case 34:
                        case 39:
                        case 92:
                            save_and_next();
                            continue;
                        case 97:
                            c = 7;
                            break;
                        case 98:
                            c = 8;
                            break;
                        case 102:
                            c = 12;
                            break;
                        case 110:
                            c = 10;
                            break;
                        case 114:
                            c = 13;
                            break;
                        case 116:
                            c = 9;
                            break;
                        case 117:
                            readutf8esc();
                            continue;
                        case 118:
                            c = 11;
                            break;
                        case 120:
                            c = readhexaesc();
                            break;
                        case 122:
                            nextChar();
                            while (isspace(this.current)) {
                                if (currIsNewline()) {
                                    inclinenumber();
                                } else {
                                    nextChar();
                                }
                            }
                            continue;
                        default:
                            if (!isdigit(this.current)) {
                                this.ec[0] = this.current;
                                escerror(1, "invalid escape sequence");
                            } else {
                                int c2 = readdecesc();
                                save(c2);
                                continue;
                            }
                            break;
                    }
                    save(c);
                    nextChar();
                    break;
                default:
                    save_and_next();
                    break;
            }
        }
        save_and_next();
        seminfo.ts = this.L.newTString(LuaString.valueOf(this.buff, 1, this.nbuff - 2));
    }

    /* JADX WARN: Code restructure failed: missing block: B:112:?, code lost:
    
        return 61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:113:?, code lost:
    
        return luaj.compiler.LexState.TK_EQ;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0089, code lost:
    
        nextChar();
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x008e, code lost:
    
        if (r10.current == 61) goto L39;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x0092, code lost:
    
        nextChar();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    int llex(luaj.compiler.LexState.SemInfo r11) {
        this.nbuff = 0;
        while (true) {
            switch (this.current) {
                case -1:
                    return TK_EOF;
                case 10:
                case 13:
                    inclinenumber();
                    this.nbuff = 0;
                    break;
                case 9:
                case 11:
                case 12:
                case 32:
                    nextChar();
                    this.nbuff = 0;
                    break;
                case 45:
                    nextChar();
                    if (this.current != 45) {
                        return 45;
                    }
                    nextChar();
                    if (this.current == 91) {
                        this.nbuff = 0;
                        int sep = skip_sep();
                        this.nbuff = 0;
                        if (sep >= 0) {
                            read_long_string(null, sep);
                            this.nbuff = 0;
                            break;
                        }
                    }
                    while (!currIsNewline() && this.current != -1) {
                        nextChar();
                    }
                    this.nbuff = 0;
                    break;
                case 91:
                    int sep2 = skip_sep();
                    if (sep2 >= 0) {
                        read_long_string(r11, sep2);
                        return TK_STRING;
                    } else if (sep2 == -1) {
                        return 91;
                    } else {
                        lexerror("invalid long string delimiter", TK_STRING);
                        return TK_STRING;
                    }
                case 61:
                    nextChar();
                    if (this.current != 61) {
                        return 61;
                    }
                    nextChar();
                    return TK_EQ;
                case 60:
                    nextChar();
                    if (this.current == 61) {
                        nextChar();
                        return TK_LE;
                    } else if (this.current == 60) {
                        nextChar();
                        return TK_SHL;
                    } else {
                        return 60;
                    }
                case 62:
                    nextChar();
                    if (this.current == 61) {
                        nextChar();
                        return TK_GE;
                    } else if (this.current == 62) {
                        nextChar();
                        return TK_SHR;
                    } else {
                        return 62;
                    }
                case 47:
                    nextChar();
                    if (this.current != 47) {
                        return 47;
                    }
                    nextChar();
                    return TK_IDIV;
                case 126:
                    nextChar();
                    if (this.current != 61) {
                        return 126;
                    }
                    nextChar();
                    return TK_NE;
                case 58:
                    nextChar();
                    if (this.current != 58) {
                        return 58;
                    }
                    nextChar();
                    return TK_DBCOLON;
                case 34:
                case 39:
                    read_string(this.current, r11);
                    return TK_STRING;
                case 46:
                    save_and_next();
                    if (this.current == 46) {
                        save_and_next();
                        if (this.current == 46) {
                            save_and_next();
                            return TK_DOTS;
                        }
                        return TK_CONCAT;
                    } else if (!isdigit(this.current)) {
                        return 46;
                    } else {
                        read_numeral(r11, false);
                        return TK_NUMBER;
                    }
                default:
                    if (isdigit(this.current)) {
                        read_numeral(r11, true);
                        return TK_NUMBER;
                    } else if (isalpha(this.current) || this.current == 95) {
                        do {
                            save_and_next();
                        } while (isalnum(this.current));
                        r11.ts = newstring(this.buff, 0, this.nbuff);
                        Integer reserved = RESERVED.get(r11.ts);
                        if (reserved != null) {
                            return reserved.intValue();
                        }
                        return TK_NAME;
                    } else {
                        int c = this.current;
                        nextChar();
                        return c;
                    }
            }
        }
    }

    void next() {
        this.lastline = this.linenumber;
        if (this.lookahead.token != TK_EOF) {
            this.t.set(this.lookahead);
            this.lookahead.token = TK_EOF;
        } else {
            this.t.token = llex(this.t.seminfo);
        }
    }

    void lookahead() {
        _assert(this.lookahead.token == TK_EOF);
        this.lookahead.token = llex(this.lookahead.seminfo);
    }

    static final boolean vkisvar(int k) {
        return 7 <= k && k <= 9;
    }

    static final boolean vkisinreg(int k) {
        return k == 6 || k == 7;
    }

    static class expdesc {
        int k;
        final U u = new U();
        final IntPtr t = new IntPtr();
        final IntPtr f = new IntPtr();

        expdesc() {
        }

        static class U {
            LuaValue _nval;
            short ind_idx;
            short ind_t;
            short ind_vt;
            int info;

            U() {
            }

            public void setNval(LuaValue r) {
                this._nval = r;
            }

            public LuaValue nval() {
                return this._nval == null ? LuaLong.valueOf(this.info) : this._nval;
            }
        }

        void init(int k, int i) {
            this.f.i = -1;
            this.t.i = -1;
            this.k = k;
            this.u.info = i;
        }

        boolean hasjumps() {
            return this.t.i != this.f.i;
        }

        boolean isnumeral() {
            return this.k == 5 && this.t.i == -1 && this.f.i == -1;
        }

        public void setvalue(expdesc other) {
            this.f.i = other.f.i;
            this.k = other.k;
            this.t.i = other.t.i;
            this.u._nval = other.u._nval;
            this.u.ind_idx = other.u.ind_idx;
            this.u.ind_t = other.u.ind_t;
            this.u.ind_vt = other.u.ind_vt;
            this.u.info = other.u.info;
        }
    }

    static class Vardesc {
        final short idx;

        Vardesc(int idx) {
            this.idx = (short) idx;
        }

        public String toString() {
            return "Vardesc [idx=" + ((int) this.idx) + ']';
        }
    }

    static class Labeldesc {
        int line;
        short nactvar;
        LuaString name;
        int pc;

        public Labeldesc(LuaString name, int pc, int line, short nactvar) {
            this.name = name;
            this.pc = pc;
            this.line = line;
            this.nactvar = nactvar;
        }

        public String toString() {
            return "Labeldesc [name=" + this.name + ", pc=" + this.pc + ", line=" + this.line + ", nactvar=" + ((int) this.nactvar) + ']';
        }
    }

    static class Dyndata {
        Vardesc[] actvar;
        Labeldesc[] gt;
        Labeldesc[] label;
        int n_actvar = 0;
        int n_gt = 0;
        int n_label = 0;

        Dyndata() {
        }

        public String toString() {
            return "Dyndata [actvar=" + Arrays.toString(this.actvar) + ", n_actvar=" + this.n_actvar + ", gt=" + Arrays.toString(this.gt) + ", n_gt=" + this.n_gt + ", label=" + Arrays.toString(this.label) + ", n_label=" + this.n_label + ']';
        }
    }

    boolean hasmultret(int k) {
        return k == 12 || k == 13;
    }

    void anchor_token() {
        _assert(this.fs != null || this.t.token == TK_EOF);
        if (this.t.token == TK_NAME || this.t.token == TK_STRING) {
            LuaString luaString = this.t.seminfo.ts;
            this.L.cachedLuaString(this.t.seminfo.ts);
        }
    }

    void semerror(String msg) {
        this.t.token = 0;
        syntaxerror(msg);
    }

    void error_expected(int token) {
        syntaxerror(this.L.pushfstring(String.valueOf(LUA_QS(token2str(token))) + " expected"));
    }

    boolean testnext(int c) {
        if (this.t.token != c) {
            return false;
        }
        next();
        return true;
    }

    void check(int c) {
        if (this.t.token != c) {
            error_expected(c);
        }
    }

    void checknext(int c) {
        check(c);
        next();
    }

    void check_condition(boolean c, String msg) {
        if (!c) {
            syntaxerror(msg);
        }
    }

    void check_match(int what, int who, int where) {
        if (!testnext(what)) {
            if (where == this.linenumber) {
                error_expected(what);
            } else {
                syntaxerror(this.L.pushfstring(String.valueOf(LUA_QS(token2str(what))) + " expected (to close " + LUA_QS(token2str(who)) + " at line " + where + ')'));
            }
        }
    }

    LuaString str_checkname() {
        check(TK_NAME);
        LuaString ts = this.t.seminfo.ts;
        next();
        return ts;
    }

    void codestring(expdesc e, LuaString s) {
        e.init(4, this.fs.stringK(s));
    }

    void checkname(expdesc e) {
        codestring(e, str_checkname());
    }

    int registerlocalvar(LuaString varname) {
        FuncState fs = this.fs;
        Prototype f = fs.f;
        if (f.locvars == null || fs.nlocvars + 1 > f.locvars.length) {
            f.locvars = realloc(f.locvars, (fs.nlocvars * 2) + 1);
        }
        f.locvars[fs.nlocvars] = new LocVars(varname, 0, 0);
        short s = fs.nlocvars;
        fs.nlocvars = (short) (s + 1);
        return s;
    }

    void new_localvar(LuaString name) {
        int reg = registerlocalvar(name);
        this.fs.checklimit(this.dyd.n_actvar + 1, LUAI_MAXCCALLS, "local variables");
        if (this.dyd.actvar == null || this.dyd.n_actvar + 1 > this.dyd.actvar.length) {
            this.dyd.actvar = realloc(this.dyd.actvar, Math.max(1, this.dyd.n_actvar * 2));
        }
        Vardesc[] vardescArr = this.dyd.actvar;
        Dyndata dyndata = this.dyd;
        int i = dyndata.n_actvar;
        dyndata.n_actvar = i + 1;
        vardescArr[i] = new Vardesc(reg);
    }

    void new_localvarliteral(String v) {
        LuaString ts = newstring(v);
        new_localvar(ts);
    }

    void adjustlocalvars(int nvars) {
        FuncState fs = this.fs;
        fs.nactvar = (short) (fs.nactvar + nvars);
        while (nvars > 0) {
            fs.getlocvar(fs.nactvar - nvars).startpc = fs.pc;
            nvars--;
        }
    }

    void removevars(int tolevel) {
        FuncState fs = this.fs;
        while (fs.nactvar > tolevel) {
            short s = (short) (fs.nactvar - 1);
            fs.nactvar = s;
            fs.getlocvar(s).endpc = fs.pc;
        }
    }

    void singlevar(expdesc var) {
        boolean z = true;
        LuaString varname = str_checkname();
        FuncState fs = this.fs;
        if (FuncState.singlevaraux(fs, varname, var, 1) == 0) {
            expdesc key = new expdesc();
            FuncState.singlevaraux(fs, this.envn, var, 1);
            if (var.k != 7 && var.k != 8) {
                z = false;
            }
            _assert(z);
            codestring(key, varname);
            fs.indexed(var, key);
        }
    }

    void adjust_assign(int nvars, int nexps, expdesc e) {
        FuncState fs = this.fs;
        int extra = nvars - nexps;
        if (hasmultret(e.k)) {
            int extra2 = extra + 1;
            if (extra2 < 0) {
                extra2 = 0;
            }
            fs.setreturns(e, extra2);
            if (extra2 > 1) {
                fs.reserveregs(extra2 - 1);
                return;
            }
            return;
        }
        if (e.k != 0) {
            fs.exp2nextreg(e);
        }
        if (extra > 0) {
            int reg = fs.freereg;
            fs.reserveregs(extra);
            fs.nil(reg, extra);
        }
    }

    void enterlevel() {
        LuaC.CompileState compileState = this.L;
        int i = compileState.nCcalls + 1;
        compileState.nCcalls = i;
        if (i > LUAI_MAXCCALLS) {
            lexerror("chunk has too many syntax levels", 0);
        }
    }

    void leavelevel() {
        LuaC.CompileState compileState = this.L;
        compileState.nCcalls--;
    }

    void closegoto(int g, Labeldesc label) {
        FuncState fs = this.fs;
        Labeldesc[] gl = this.dyd.gt;
        Labeldesc gt = gl[g];
        _assert(gt.name.eq_b(label.name));
        if (gt.nactvar < label.nactvar) {
            LuaString vname = fs.getlocvar(gt.nactvar).varname;
            String msg = this.L.pushfstring("<goto " + gt.name + "> at line " + gt.line + " jumps into the scope of local '" + vname.tojstring() + SearchMenuItem.HEX_UTF_8);
            semerror(msg);
        }
        fs.patchlist(gt.pc, label.pc);
        System.arraycopy(gl, g + 1, gl, g, (this.dyd.n_gt - g) - 1);
        Dyndata dyndata = this.dyd;
        int i = dyndata.n_gt - 1;
        dyndata.n_gt = i;
        gl[i] = null;
    }

    boolean findlabel(int g) {
        FuncState.BlockCnt bl = this.fs.bl;
        Dyndata dyd = this.dyd;
        Labeldesc gt = dyd.gt[g];
        for (int i = bl.firstlabel; i < dyd.n_label; i++) {
            Labeldesc lb = dyd.label[i];
            if (lb.name.eq_b(gt.name)) {
                if (gt.nactvar > lb.nactvar && (bl.upval || dyd.n_label > bl.firstlabel)) {
                    this.fs.patchclose(gt.pc, lb.nactvar);
                }
                closegoto(g, lb);
                return true;
            }
        }
        return false;
    }

    int newlabelentry(Labeldesc[] l, int index, LuaString name, int line, int pc) {
        l[index] = new Labeldesc(name, pc, line, this.fs.nactvar);
        return index;
    }

    void findgotos(Labeldesc lb) {
        Labeldesc[] gl = this.dyd.gt;
        int i = this.fs.bl.firstgoto;
        while (i < this.dyd.n_gt) {
            if (gl[i].name.eq_b(lb.name)) {
                closegoto(i, lb);
            } else {
                i++;
            }
        }
    }

    void breaklabel() {
        LuaString n = LuaString.valueOf(RESERVED_LABEL_BREAK);
        Dyndata dyndata = this.dyd;
        Labeldesc[] labeldescArrGrow = grow(this.dyd.label, this.dyd.n_label + 1);
        dyndata.label = labeldescArrGrow;
        Dyndata dyndata2 = this.dyd;
        int i = dyndata2.n_label;
        dyndata2.n_label = i + 1;
        int l = newlabelentry(labeldescArrGrow, i, n, 0, this.fs.pc);
        findgotos(this.dyd.label[l]);
    }

    void undefgoto(Labeldesc gt, FuncState fs, FuncState.BlockCnt bl) {
        String str;
        this.linenumber = gt.line;
        String name = gt.name.tojstring();
        boolean reserved = isReservedKeyword(name);
        LuaC.CompileState compileState = this.L;
        if (reserved) {
            str = "<" + gt.name + "> at line " + gt.line + " not inside a loop";
        } else {
            str = "no visible label '" + gt.name + "' for <goto> at line " + gt.line;
        }
        String msg = compileState.pushfstring(str);
        if (!reserved) {
            Dyndata dyd = this.dyd;
            for (int i = bl.firstlabel; i < dyd.n_label; i++) {
                Labeldesc lb = dyd.label[i];
                if (lb != null && lb.name != null) {
                    String label = lb.name.tojstring();
                    if (label.equalsIgnoreCase(name)) {
                        msg = String.valueOf(msg) + "\ndid you mean label '" + label + "' at line " + lb.line + '?';
                    }
                }
            }
        }
        semerror(msg);
    }

    Prototype addprototype() {
        Prototype f = this.fs.f;
        if (f.p == null || this.fs.np >= f.p.length) {
            f.p = realloc(f.p, Math.max(1, this.fs.np * 2));
        }
        Prototype[] prototypeArr = f.p;
        FuncState funcState = this.fs;
        int i = funcState.np;
        funcState.np = i + 1;
        Prototype clp = new Prototype();
        prototypeArr[i] = clp;
        return clp;
    }

    void codeclosure(expdesc v) {
        FuncState fs = this.fs.prev;
        v.init(11, fs.codeABx(37, 0, fs.np - 1));
        fs.exp2nextreg(v);
    }

    void open_func(FuncState fs, FuncState.BlockCnt bl) {
        fs.prev = this.fs;
        fs.ls = this;
        this.fs = fs;
        fs.pc = 0;
        fs.lasttarget = -1;
        fs.jpc = new IntPtr(-1);
        fs.freereg = (short) 0;
        fs.nk = 0;
        fs.np = 0;
        fs.nups = (short) 0;
        fs.nlocvars = (short) 0;
        fs.nactvar = (short) 0;
        fs.firstlocal = this.dyd.n_actvar;
        fs.bl = null;
        fs.f.source = this.source;
        fs.f.maxstacksize = 2;
        fs.enterblock(bl, false);
    }

    void close_func() {
        FuncState fs = this.fs;
        Prototype f = fs.f;
        fs.ret(0, 0);
        fs.leaveblock();
        f.code = realloc(f.code, fs.pc);
        f.lineinfo = realloc(f.lineinfo, fs.pc);
        f.k = realloc(f.k, fs.nk);
        f.p = realloc(f.p, fs.np);
        f.locvars = realloc(f.locvars, fs.nlocvars);
        f.upvalues = realloc(f.upvalues, fs.nups);
        _assert(fs.bl == null);
        this.fs = fs.prev;
    }

    void fieldsel(expdesc v) {
        FuncState fs = this.fs;
        expdesc key = new expdesc();
        fs.exp2anyregup(v);
        next();
        checkname(key);
        fs.indexed(v, key);
    }

    void yindex(expdesc v) {
        next();
        expr(v);
        this.fs.exp2val(v);
        checknext(93);
    }

    static class ConsControl {
        int na;
        int nh;
        expdesc t;
        int tostore;
        expdesc v = new expdesc();

        ConsControl() {
        }
    }

    void recfield(ConsControl cc) {
        FuncState fs = this.fs;
        int reg = this.fs.freereg;
        expdesc key = new expdesc();
        expdesc val = new expdesc();
        if (this.t.token == TK_NAME) {
            fs.checklimit(cc.nh, MAX_INT, "items in a constructor");
            checkname(key);
        } else {
            yindex(key);
        }
        cc.nh++;
        checknext(61);
        int rkkey = fs.exp2RK(key);
        expr(val);
        fs.codeABC(10, cc.t.u.info, rkkey, fs.exp2RK(val));
        fs.freereg = (short) reg;
    }

    void listfield(ConsControl cc) {
        expr(cc.v);
        this.fs.checklimit(cc.na, MAX_INT, "items in a constructor");
        cc.na++;
        cc.tostore++;
    }

    void constructor(expdesc t) {
        FuncState fs = this.fs;
        int line = this.linenumber;
        int pc = fs.codeABC(11, 0, 0, 0);
        ConsControl cc = new ConsControl();
        cc.tostore = 0;
        cc.nh = 0;
        cc.na = 0;
        cc.t = t;
        t.init(11, pc);
        cc.v.init(0, 0);
        fs.exp2nextreg(t);
        checknext(123);
        while (this.t.token != 125) {
            _assert(cc.v.k == 0 || cc.tostore > 0);
            fs.closelistfield(cc);
            switch (this.t.token) {
                case 91:
                    recfield(cc);
                    break;
                case TK_NAME /* 292 */:
                    lookahead();
                    if (this.lookahead.token != 61) {
                        listfield(cc);
                    } else {
                        recfield(cc);
                    }
                    break;
                default:
                    listfield(cc);
                    break;
            }
            if (testnext(44) || testnext(59)) {
            }
        }
        check_match(125, 123, line);
        fs.lastlistfield(cc);
        InstructionPtr i = new InstructionPtr(fs.f.code, pc);
        SETARG_B(i, luaO_int2fb(cc.na));
        SETARG_C(i, luaO_int2fb(cc.nh));
    }

    static int luaO_int2fb(int x) {
        int e = 0;
        while (x >= 16) {
            x = (x + 1) >> 1;
            e++;
        }
        return x < 8 ? x : ((e + 1) << 3) | (x - 8);
    }

    void parlist() {
        FuncState fs = this.fs;
        Prototype f = fs.f;
        int nparams = 0;
        f.is_vararg = 0;
        if (this.t.token != 41) {
            do {
                switch (this.t.token) {
                    case TK_DOTS /* 281 */:
                        next();
                        f.is_vararg = 1;
                        break;
                    case TK_NAME /* 292 */:
                        new_localvar(str_checkname());
                        nparams++;
                        break;
                    default:
                        syntaxerror("<name> or " + LUA_QL("...") + " expected");
                        break;
                }
                if (f.is_vararg == 0) {
                }
            } while (testnext(44));
        }
        adjustlocalvars(nparams);
        f.numparams = fs.nactvar;
        fs.reserveregs(fs.nactvar);
    }

    void body(expdesc e, boolean needself, int line) {
        FuncState new_fs = new FuncState();
        FuncState.BlockCnt bl = new FuncState.BlockCnt();
        new_fs.f = addprototype();
        new_fs.f.linedefined = line;
        open_func(new_fs, bl);
        checknext(40);
        if (needself) {
            new_localvarliteral("self");
            adjustlocalvars(1);
        }
        parlist();
        checknext(41);
        statlist();
        new_fs.f.lastlinedefined = this.linenumber;
        check_match(TK_END, TK_FUNCTION, line);
        codeclosure(e);
        close_func();
    }

    int explist(expdesc v) {
        int n = 1;
        expr(v);
        while (testnext(44)) {
            this.fs.exp2nextreg(v);
            expr(v);
            n++;
        }
        return n;
    }

    void funcargs(expdesc f, int line) {
        int nparams;
        FuncState fs = this.fs;
        expdesc args = new expdesc();
        switch (this.t.token) {
            case 40:
                next();
                if (this.t.token == 41) {
                    args.k = 0;
                } else {
                    explist(args);
                    fs.setmultret(args);
                }
                check_match(41, 40, line);
                break;
            case 123:
                constructor(args);
                break;
            case TK_STRING /* 293 */:
                codestring(args, this.t.seminfo.ts);
                next();
                break;
            default:
                syntaxerror("function arguments expected");
                return;
        }
        _assert(f.k == 6);
        int base = f.u.info;
        if (hasmultret(args.k)) {
            nparams = -1;
        } else {
            if (args.k != 0) {
                fs.exp2nextreg(args);
            }
            nparams = fs.freereg - (base + 1);
        }
        f.init(12, fs.codeABC(29, base, nparams + 1, 2));
        fs.fixline(line);
        fs.freereg = (short) (base + 1);
    }

    void primaryexp(expdesc v) {
        switch (this.t.token) {
            case 40:
                int line = this.linenumber;
                next();
                expr(v);
                check_match(41, 40, line);
                this.fs.dischargevars(v);
                break;
            case TK_NAME /* 292 */:
                singlevar(v);
                break;
            default:
                syntaxerror("unexpected symbol");
                break;
        }
    }

    void suffixedexp(expdesc v) {
        int line = this.linenumber;
        primaryexp(v);
        while (true) {
            switch (this.t.token) {
                case 40:
                case 123:
                case TK_STRING /* 293 */:
                    this.fs.exp2nextreg(v);
                    funcargs(v, line);
                    break;
                case 46:
                    fieldsel(v);
                    break;
                case LasmConstants.FALSE /* 58 */:
                    expdesc key = new expdesc();
                    next();
                    checkname(key);
                    this.fs.self(v, key);
                    funcargs(v, line);
                    break;
                case 91:
                    expdesc key2 = new expdesc();
                    this.fs.exp2anyregup(v);
                    yindex(key2);
                    this.fs.indexed(v, key2);
                    break;
                default:
                    return;
            }
        }
    }

    void simpleexp(expdesc v) {
        switch (this.t.token) {
            case 123:
                constructor(v);
                return;
            case TK_FALSE /* 263 */:
                v.init(3, 0);
                break;
            case TK_FUNCTION /* 265 */:
                next();
                body(v, false, this.linenumber);
                return;
            case TK_NIL /* 270 */:
                v.init(1, 0);
                break;
            case TK_TRUE /* 276 */:
                v.init(2, 0);
                break;
            case TK_DOTS /* 281 */:
                FuncState fs = this.fs;
                check_condition(fs.f.is_vararg != 0, "cannot use " + LUA_QL("...") + " outside a vararg function");
                v.init(13, fs.codeABC(38, 0, 1, 0));
                break;
            case TK_NUMBER /* 290 */:
                v.init(5, 0);
                v.u.setNval(this.t.seminfo.r);
                break;
            case TK_STRING /* 293 */:
                codestring(v, this.t.seminfo.ts);
                break;
            default:
                suffixedexp(v);
                return;
        }
        next();
    }

    int getunopr(int op) {
        switch (op) {
            case 35:
                return 2;
            case 45:
                return 0;
            case 126:
                return 4;
            case TK_NOT /* 271 */:
                return 1;
            default:
                return 3;
        }
    }

    int getbinopr(int op) {
        switch (op) {
            case 37:
                return 4;
            case 38:
                return 16;
            case 42:
                return 2;
            case 43:
                return 0;
            case 45:
                return 1;
            case 47:
                return 3;
            case 60:
                return 9;
            case 62:
                return 11;
            case 94:
                return 5;
            case LuaError.MAX_STRING_LEN /* 124 */:
                return 17;
            case 126:
                return 18;
            case 257:
                return 13;
            case TK_OR /* 272 */:
                return 14;
            case TK_IDIV /* 279 */:
                return 15;
            case TK_CONCAT /* 280 */:
                return 6;
            case TK_EQ /* 282 */:
                return 8;
            case TK_GE /* 283 */:
                return 12;
            case TK_LE /* 284 */:
                return 10;
            case TK_NE /* 285 */:
                return 7;
            case TK_SHL /* 286 */:
                return 19;
            case TK_SHR /* 287 */:
                return 20;
            default:
                return 21;
        }
    }

    static class Priority {
        final byte left;
        final byte right;

        public Priority(int i, int j) {
            this.left = (byte) i;
            this.right = (byte) j;
        }
    }

    int subexpr(expdesc v, int limit) {
        enterlevel();
        int uop = getunopr(this.t.token);
        if (uop != 3) {
            int line = this.linenumber;
            next();
            subexpr(v, 80);
            this.fs.prefix(uop, v, line);
        } else {
            simpleexp(v);
        }
        int op = getbinopr(this.t.token);
        while (op != 21 && priority[op].left > limit) {
            expdesc v2 = new expdesc();
            int line2 = this.linenumber;
            next();
            this.fs.infix(op, v);
            int nextop = subexpr(v2, priority[op].right);
            this.fs.posfix(op, v, v2, line2);
            op = nextop;
        }
        leavelevel();
        return op;
    }

    void expr(expdesc v) {
        subexpr(v, 0);
    }

    boolean block_follow(boolean withuntil) {
        switch (this.t.token) {
            case TK_ELSE /* 260 */:
            case TK_ELSEIF /* 261 */:
            case TK_END /* 262 */:
            case TK_EOF /* 289 */:
                return true;
            case TK_UNTIL /* 277 */:
                return withuntil;
            default:
                return false;
        }
    }

    void block() {
        FuncState fs = this.fs;
        FuncState.BlockCnt bl = new FuncState.BlockCnt();
        fs.enterblock(bl, false);
        statlist();
        fs.leaveblock();
    }

    static class LHS_assign {
        LHS_assign prev;
        expdesc v = new expdesc();

        LHS_assign() {
        }
    }

    void check_conflict(LHS_assign lh, expdesc v) {
        FuncState fs = this.fs;
        short extra = fs.freereg;
        boolean conflict = false;
        while (lh != null) {
            if (lh.v.k == 9) {
                if (lh.v.u.ind_vt == v.k && lh.v.u.ind_t == v.u.info) {
                    conflict = true;
                    lh.v.u.ind_vt = (short) 7;
                    lh.v.u.ind_t = extra;
                }
                if (v.k == 7 && lh.v.u.ind_idx == v.u.info) {
                    conflict = true;
                    lh.v.u.ind_idx = extra;
                }
            }
            lh = lh.prev;
        }
        if (conflict) {
            int op = v.k == 7 ? 0 : 5;
            fs.codeABC(op, extra, v.u.info, 0);
            fs.reserveregs(1);
        }
    }

    void assignment(LHS_assign lh, int nvars) {
        expdesc e = new expdesc();
        check_condition(7 <= lh.v.k && lh.v.k <= 9, "syntax error");
        if (testnext(44)) {
            LHS_assign nv = new LHS_assign();
            nv.prev = lh;
            suffixedexp(nv.v);
            if (nv.v.k != 9) {
                check_conflict(lh, nv.v);
            }
            assignment(nv, nvars + 1);
        } else {
            checknext(61);
            int nexps = explist(e);
            if (nexps != nvars) {
                adjust_assign(nvars, nexps, e);
                if (nexps > nvars) {
                    FuncState funcState = this.fs;
                    funcState.freereg = (short) (funcState.freereg - (nexps - nvars));
                }
            } else {
                this.fs.setoneret(e);
                this.fs.storevar(lh.v, e);
                return;
            }
        }
        e.init(6, this.fs.freereg - 1);
        this.fs.storevar(lh.v, e);
    }

    int cond() {
        expdesc v = new expdesc();
        expr(v);
        if (v.k == 1) {
            v.k = 3;
        }
        this.fs.goiftrue(v);
        return v.f.i;
    }

    void gotostat(int pc) {
        LuaString label;
        int line = this.linenumber;
        if (testnext(TK_GOTO)) {
            label = str_checkname();
        } else {
            next();
            label = LuaString.valueOf(RESERVED_LABEL_BREAK);
        }
        Dyndata dyndata = this.dyd;
        Labeldesc[] labeldescArrGrow = grow(this.dyd.gt, this.dyd.n_gt + 1);
        dyndata.gt = labeldescArrGrow;
        Dyndata dyndata2 = this.dyd;
        int i = dyndata2.n_gt;
        dyndata2.n_gt = i + 1;
        int g = newlabelentry(labeldescArrGrow, i, label, line, pc);
        findlabel(g);
    }

    void skipnoopstat() {
        while (true) {
            if (this.t.token == 59 || this.t.token == TK_DBCOLON) {
                statement();
            } else {
                return;
            }
        }
    }

    void labelstat(LuaString label, int line) {
        this.fs.checkrepeated(this.dyd.label, this.dyd.n_label, label);
        checknext(TK_DBCOLON);
        Dyndata dyndata = this.dyd;
        Labeldesc[] labeldescArrGrow = grow(this.dyd.label, this.dyd.n_label + 1);
        dyndata.label = labeldescArrGrow;
        Dyndata dyndata2 = this.dyd;
        int i = dyndata2.n_label;
        dyndata2.n_label = i + 1;
        int l = newlabelentry(labeldescArrGrow, i, label, line, this.fs.getlabel());
        skipnoopstat();
        if (block_follow(false)) {
            this.dyd.label[l].nactvar = this.fs.bl.nactvar;
        }
        findgotos(this.dyd.label[l]);
    }

    void whilestat(int line) {
        FuncState fs = this.fs;
        FuncState.BlockCnt bl = new FuncState.BlockCnt();
        next();
        int whileinit = fs.getlabel();
        int condexit = cond();
        fs.enterblock(bl, true);
        checknext(TK_DO);
        block();
        fs.patchlist(fs.jump(), whileinit);
        check_match(TK_END, TK_WHILE, line);
        fs.leaveblock();
        fs.patchtohere(condexit);
    }

    void repeatstat(int line) {
        FuncState fs = this.fs;
        int repeat_init = fs.getlabel();
        FuncState.BlockCnt bl1 = new FuncState.BlockCnt();
        FuncState.BlockCnt bl2 = new FuncState.BlockCnt();
        fs.enterblock(bl1, true);
        fs.enterblock(bl2, false);
        next();
        statlist();
        check_match(TK_UNTIL, TK_REPEAT, line);
        int condexit = cond();
        if (bl2.upval) {
            fs.patchclose(condexit, bl2.nactvar);
        }
        fs.leaveblock();
        fs.patchlist(condexit, repeat_init);
        fs.leaveblock();
    }

    int exp1() {
        expdesc e = new expdesc();
        expr(e);
        int k = e.k;
        this.fs.exp2nextreg(e);
        return k;
    }

    void forbody(int base, int line, int nvars, boolean isnum) {
        int endfor;
        FuncState.BlockCnt bl = new FuncState.BlockCnt();
        FuncState fs = this.fs;
        adjustlocalvars(3);
        checknext(TK_DO);
        int prep = isnum ? fs.codeAsBx(33, base, -1) : fs.jump();
        fs.enterblock(bl, false);
        adjustlocalvars(nvars);
        fs.reserveregs(nvars);
        block();
        fs.leaveblock();
        fs.patchtohere(prep);
        if (isnum) {
            endfor = fs.codeAsBx(32, base, -1);
        } else {
            fs.codeABC(34, base, 0, nvars);
            fs.fixline(line);
            endfor = fs.codeAsBx(35, base + 2, -1);
        }
        fs.patchlist(endfor, prep + 1);
        fs.fixline(line);
    }

    void fornum(LuaString varname, int line) {
        FuncState fs = this.fs;
        int base = fs.freereg;
        new_localvarliteral(RESERVED_LOCAL_VAR_FOR_INDEX);
        new_localvarliteral(RESERVED_LOCAL_VAR_FOR_LIMIT);
        new_localvarliteral(RESERVED_LOCAL_VAR_FOR_STEP);
        new_localvar(varname);
        checknext(61);
        exp1();
        checknext(44);
        exp1();
        if (testnext(44)) {
            exp1();
        } else {
            fs.codeK(fs.freereg, fs.numberK(LuaLong.valueOf(1L)));
            fs.reserveregs(1);
        }
        forbody(base, line, 1, true);
    }

    void forlist(LuaString indexname) {
        FuncState fs = this.fs;
        expdesc e = new expdesc();
        int nvars = 4;
        int base = fs.freereg;
        new_localvarliteral(RESERVED_LOCAL_VAR_FOR_GENERATOR);
        new_localvarliteral(RESERVED_LOCAL_VAR_FOR_STATE);
        new_localvarliteral(RESERVED_LOCAL_VAR_FOR_CONTROL);
        new_localvar(indexname);
        while (testnext(44)) {
            new_localvar(str_checkname());
            nvars++;
        }
        checknext(TK_IN);
        int line = this.linenumber;
        adjust_assign(3, explist(e), e);
        fs.checkstack(3);
        forbody(base, line, nvars - 3, false);
    }

    void forstat(int line) {
        FuncState fs = this.fs;
        FuncState.BlockCnt bl = new FuncState.BlockCnt();
        fs.enterblock(bl, true);
        next();
        LuaString varname = str_checkname();
        switch (this.t.token) {
            case 44:
            case TK_IN /* 268 */:
                forlist(varname);
                break;
            case 61:
                fornum(varname, line);
                break;
            default:
                syntaxerror(String.valueOf(LUA_QL("=")) + " or " + LUA_QL("in") + " expected");
                break;
        }
        check_match(TK_END, TK_FOR, line);
        fs.leaveblock();
    }

    void test_then_block(IntPtr escapelist) {
        int jf;
        expdesc v = new expdesc();
        FuncState.BlockCnt bl = new FuncState.BlockCnt();
        next();
        expr(v);
        checknext(TK_THEN);
        if (this.t.token == TK_GOTO || this.t.token == TK_BREAK) {
            this.fs.goiffalse(v);
            this.fs.enterblock(bl, false);
            gotostat(v.t.i);
            skipnoopstat();
            if (block_follow(false)) {
                this.fs.leaveblock();
                return;
            }
            jf = this.fs.jump();
        } else {
            this.fs.goiftrue(v);
            this.fs.enterblock(bl, false);
            jf = v.f.i;
        }
        statlist();
        this.fs.leaveblock();
        if (this.t.token == TK_ELSE || this.t.token == TK_ELSEIF) {
            this.fs.concat(escapelist, this.fs.jump());
        }
        this.fs.patchtohere(jf);
    }

    void ifstat(int line) {
        IntPtr escapelist = new IntPtr(-1);
        test_then_block(escapelist);
        while (this.t.token == TK_ELSEIF) {
            test_then_block(escapelist);
        }
        if (testnext(TK_ELSE)) {
            block();
        }
        check_match(TK_END, TK_IF, line);
        this.fs.patchtohere(escapelist.i);
    }

    void localfunc() {
        expdesc b = new expdesc();
        FuncState fs = this.fs;
        new_localvar(str_checkname());
        adjustlocalvars(1);
        body(b, false, this.linenumber);
        fs.getlocvar(fs.nactvar - 1).startpc = fs.pc;
    }

    void localstat() {
        int nexps;
        int nvars = 0;
        expdesc e = new expdesc();
        do {
            new_localvar(str_checkname());
            nvars++;
        } while (testnext(44));
        if (testnext(61)) {
            nexps = explist(e);
        } else {
            e.k = 0;
            nexps = 0;
        }
        adjust_assign(nvars, nexps, e);
        adjustlocalvars(nvars);
    }

    boolean funcname(expdesc v) {
        singlevar(v);
        while (this.t.token == 46) {
            fieldsel(v);
        }
        if (this.t.token != 58) {
            return false;
        }
        fieldsel(v);
        return true;
    }

    void funcstat(int line) {
        expdesc v = new expdesc();
        expdesc b = new expdesc();
        next();
        boolean needself = funcname(v);
        body(b, needself, line);
        this.fs.storevar(v, b);
        this.fs.fixline(line);
    }

    void exprstat() {
        FuncState fs = this.fs;
        LHS_assign v = new LHS_assign();
        suffixedexp(v.v);
        if (this.t.token == 61 || this.t.token == 44) {
            v.prev = null;
            assignment(v, 1);
        } else {
            check_condition(v.v.k == 12, "syntax error");
            SETARG_C(fs.getcodePtr(v.v), 1);
        }
    }

    void retstat() {
        int nret;
        int first;
        FuncState fs = this.fs;
        expdesc e = new expdesc();
        if (block_follow(true) || this.t.token == 59) {
            nret = 0;
            first = 0;
        } else {
            nret = explist(e);
            if (hasmultret(e.k)) {
                fs.setmultret(e);
                if (e.k == 12 && nret == 1) {
                    SET_OPCODE(fs.getcodePtr(e), 30);
                    _assert(Lua.GETARG_A(fs.getcode(e)) == fs.nactvar);
                }
                first = fs.nactvar;
                nret = -1;
            } else if (nret == 1) {
                first = fs.exp2anyreg(e);
            } else {
                fs.exp2nextreg(e);
                first = fs.nactvar;
                _assert(nret == fs.freereg - first);
            }
        }
        fs.ret(first, nret);
        testnext(59);
    }

    void statement() {
        int line = this.linenumber;
        enterlevel();
        switch (this.t.token) {
            case 59:
                next();
                break;
            case TK_BREAK /* 258 */:
            case TK_GOTO /* 266 */:
                gotostat(this.fs.jump());
                break;
            case TK_DO /* 259 */:
                next();
                block();
                check_match(TK_END, TK_DO, line);
                break;
            case TK_FOR /* 264 */:
                forstat(line);
                break;
            case TK_FUNCTION /* 265 */:
                funcstat(line);
                break;
            case TK_IF /* 267 */:
                ifstat(line);
                break;
            case TK_LOCAL /* 269 */:
                next();
                if (testnext(TK_FUNCTION)) {
                    localfunc();
                } else {
                    localstat();
                }
                break;
            case TK_REPEAT /* 273 */:
                repeatstat(line);
                break;
            case TK_RETURN /* 274 */:
                next();
                retstat();
                break;
            case TK_WHILE /* 278 */:
                whilestat(line);
                break;
            case TK_DBCOLON /* 288 */:
                next();
                labelstat(str_checkname(), line);
                break;
            default:
                exprstat();
                break;
        }
        _assert(this.fs.f.maxstacksize >= this.fs.freereg && this.fs.freereg >= this.fs.nactvar);
        this.fs.freereg = this.fs.nactvar;
        leavelevel();
    }

    void statlist() {
        while (!block_follow(true)) {
            if (this.t.token == TK_RETURN) {
                statement();
                return;
            }
            statement();
        }
    }

    public void mainfunc(FuncState funcstate) {
        FuncState.BlockCnt bl = new FuncState.BlockCnt();
        open_func(funcstate, bl);
        this.fs.f.is_vararg = 1;
        expdesc v = new expdesc();
        v.init(7, 0);
        this.fs.newupvalue(this.envn, v);
        next();
        statlist();
        check(TK_EOF);
        close_func();
    }

    public static void main(String[] args) throws Throwable {
    }
}
