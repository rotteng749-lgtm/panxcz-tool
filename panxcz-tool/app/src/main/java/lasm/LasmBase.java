package lasm;

import android.ext.ListManager;
import android.ext.Tools;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import luaj.LuaClosure;
import luaj.LuaValue;
import luaj.Prototype;

/* JADX INFO: loaded from: classes.dex */
public class LasmBase {
    private static final int STATE_DECIMAL = 3;
    private static final int STATE_DEFALUT = 0;
    private static final int STATE_ESCAPE = 1;
    private static final int STATE_HEX = 2;
    private static final int STATE_SKIP_WHITE_SPACE = 4;
    private static final int STATE_UNICODE = 6;

    public static void dump(Prototype p, String filename, boolean strip, boolean fix, String src) throws IOException {
    }

    public static void print(LuaClosure func, String filename) throws IOException {
    }

    public static void main(String[] args) throws Throwable {
    }

    public static String getLine(Throwable e, String file, boolean color) throws IOException {
        String ret = "";
        int line = 0;
        int column = 0;
        if (e instanceof LasmParseException) {
            line = ((LasmParseException) e).currentToken.beginLine;
            column = ((LasmParseException) e).currentToken.beginColumn;
        } else if (e instanceof ParseException) {
            line = ((ParseException) e).currentToken.next.beginLine;
            column = ((ParseException) e).currentToken.next.beginColumn;
        } else if (e instanceof TokenMgrError) {
            line = ((TokenMgrError) e).errorLine;
            column = ((TokenMgrError) e).errorColumn;
        }
        if (line > 0) {
            BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            int i = 0;
            while (true) {
                String s = read.readLine();
                if (s == null) {
                    break;
                }
                i++;
                if (i == line) {
                    ret = color ? ListManager.NEW_LINE + s.substring(0, column - 1) + Tools.ERR_MARK + s.substring(column - 1) + ListManager.NEW_LINE : ListManager.NEW_LINE + s + ListManager.NEW_LINE + new String(new char[column - 1]).replace("\u0000", "_") + "^\n";
                }
            }
            read.close();
        }
        return ret;
    }

    static int parseIntMax(Token n, int max) throws ParseException {
        int ret = parseInt(n, 0);
        if (ret > max) {
            throw new LasmParseException(n, "'" + n.image + "' parsed as " + ret + " out of possible range [0; " + max + "]");
        }
        return ret;
    }

    static int parseInt(Token n) throws ParseException {
        return parseInt(n, 0);
    }

    static int parseInt(Token n, int skip) throws ParseException {
        try {
            return Integer.parseInt(n.image.substring(skip));
        } catch (NumberFormatException e) {
            throw new LasmParseException(n, "Failed parse '" + n.image + "' as integer", e);
        }
    }

    static int parseHex(Token n) throws ParseException {
        try {
            return (int) Long.parseLong(n.image.substring(2), 16);
        } catch (NumberFormatException e) {
            throw new LasmParseException(n, "Failed parse '" + n.image + "' as hex", e);
        }
    }

    static long parseLong(Token n) throws ParseException {
        try {
            return Long.parseLong(n.image);
        } catch (NumberFormatException e) {
            throw new LasmParseException(n, "Failed parse '" + n.image + "' as long", e);
        }
    }

    static LuaValue parseLuaNumber(Token n) throws ParseException {
        try {
            return LuaValue.parseNumber(n.image, LuaValue.ZERO);
        } catch (NumberFormatException e) {
            throw new LasmParseException(n, "Failed parse '" + n.image + "' as Lua number", e);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:20:0x005d A[PHI: r1 r2 r4 r5 r7
  0x005d: PHI (r1v11 'ch' byte) = (r1v0 'ch' byte), (r1v1 'ch' byte), (r1v2 'ch' byte), (r1v10 'ch' byte), (r1v0 'ch' byte), (r1v0 'ch' byte) binds: [B:19:0x005a, B:63:0x00c7, B:53:0x00b1, B:31:0x007a, B:26:0x006c, B:23:0x0065] A[DONT_GENERATE, DONT_INLINE]
  0x005d: PHI (r2v6 'i' int) = (r2v1 'i' int), (r2v1 'i' int), (r2v3 'i' int), (r2v1 'i' int), (r2v1 'i' int), (r2v1 'i' int) binds: [B:19:0x005a, B:63:0x00c7, B:53:0x00b1, B:31:0x007a, B:26:0x006c, B:23:0x0065] A[DONT_GENERATE, DONT_INLINE]
  0x005d: PHI (r4v13 'num' int) = (r4v1 'num' int), (r4v7 'num' int), (r4v8 'num' int), (r4v1 'num' int), (r4v1 'num' int), (r4v1 'num' int) binds: [B:19:0x005a, B:63:0x00c7, B:53:0x00b1, B:31:0x007a, B:26:0x006c, B:23:0x0065] A[DONT_GENERATE, DONT_INLINE]
  0x005d: PHI (r5v13 'pos' int) = (r5v1 'pos' int), (r5v4 'pos' int), (r5v6 'pos' int), (r5v1 'pos' int), (r5v1 'pos' int), (r5v1 'pos' int) binds: [B:19:0x005a, B:63:0x00c7, B:53:0x00b1, B:31:0x007a, B:26:0x006c, B:23:0x0065] A[DONT_GENERATE, DONT_INLINE]
  0x005d: PHI (r7v13 'state' int) = (r7v1 'state' int), (r7v3 'state' int), (r7v4 'state' int), (r7v8 'state' int), (r7v10 'state' int), (r7v1 'state' int) binds: [B:19:0x005a, B:63:0x00c7, B:53:0x00b1, B:31:0x007a, B:26:0x006c, B:23:0x0065] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static luaj.LuaString parseLuaString(lasm.Token r15) throws lasm.ParseException {
        /*
            Method dump skipped, instruction units count: 478
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: lasm.LasmBase.parseLuaString(lasm.Token):luaj.LuaString");
    }

    static void checkRange(V a, V b, int num) throws ParseException {
    }

    static void checkSame(V a, V b) throws ParseException {
        if (a.code != b.code) {
            throw new LasmParseException(b.token, "Register must be " + a.token.image);
        }
    }

    static class Internal extends LuaValue {
        int code;
        Token token;

        Internal(Token token, int code) {
            this.token = token;
            this.code = code;
        }

        @Override // luaj.LuaValue
        public int type() {
            return this.code;
        }

        @Override // luaj.LuaValue
        public String typename() {
            return null;
        }
    }

    static class Const extends Internal {
        Const(Token n) throws ParseException {
            super(n, LasmBase.parseInt(n));
        }
    }

    static class U extends Internal {
        U(Token n) throws ParseException {
            super(n, LasmBase.parseInt(n, 1));
        }
    }

    static class V extends Internal {
        V(Token n) throws ParseException {
            super(n, LasmBase.parseInt(n, 1));
        }
    }
}
