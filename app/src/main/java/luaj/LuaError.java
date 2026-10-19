package luaj;

import android.ext.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/* JADX INFO: loaded from: classes.dex */
public class LuaError extends RuntimeException {
    public static final int MAX_STRING_LEN = 124;
    private static final long serialVersionUID = 1;
    protected Throwable cause;
    protected String detailMessage;
    protected String file;
    protected String fileline;
    protected int level;
    protected int line;
    LuaValue object;
    public String prefix;
    protected String traceback;

    @Override // java.lang.Throwable
    public String getMessage() {
        if (this.traceback != null) {
            return (this.prefix == null || this.traceback.startsWith(this.prefix)) ? this.traceback : String.valueOf(this.prefix) + this.traceback;
        }
        String m = this.detailMessage == null ? super.getMessage() : this.detailMessage;
        if (m == null) {
            return null;
        }
        if (this.fileline != null) {
            return String.valueOf((this.prefix == null || m.startsWith(this.prefix)) ? "" : this.prefix) + getFileLine() + '\n' + m;
        }
        if (this.prefix != null && !m.startsWith(this.prefix)) {
            m = String.valueOf(this.prefix) + m;
        }
        return m;
    }

    private String limitStr(String ret) {
        return (ret == null || ret.length() <= 124) ? ret : String.valueOf(ret.substring(0, MAX_STRING_LEN)) + "...";
    }

    private String getFileLine() {
        String srcLine;
        InputStream is;
        if (this.file == null || this.line < 0) {
            return String.valueOf(this.fileline) + '\n';
        }
        try {
            String file = this.file;
            if (file.length() > 0 && file.charAt(0) == '@') {
                file = file.substring(1);
            }
            File f = new File(file);
            if (f.exists()) {
                is = new BufferedInputStream(new FileInputStream(f));
            } else {
                is = new ByteArrayInputStream(file.getBytes());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int i = 1;
            while (i < this.line && br.readLine() != null) {
                i++;
            }
            srcLine = i == this.line ? limitStr(br.readLine()) : "failed read line";
        } catch (Throwable e) {
            Log.e("Failed read source", e);
            srcLine = "failed read source";
        }
        return String.valueOf(this.fileline) + "\n`" + srcLine + '`';
    }

    public LuaValue getMessageObject() {
        if (this.object != null) {
            return this.object;
        }
        String m = getMessage();
        if (m != null) {
            return LuaValue.valueOf(m);
        }
        return null;
    }

    public LuaError(Throwable cause) {
        super("vm error: " + cause);
        this.prefix = null;
        this.cause = cause;
        this.level = 1;
    }

    public LuaError(String message) {
        super(message);
        this.prefix = null;
        this.level = 1;
    }

    public LuaError(String file, int line, String message) {
        super(message);
        this.prefix = null;
        this.level = 1;
        this.file = file;
        this.line = line;
        setFileLine();
    }

    public void setFileLine() {
        this.fileline = String.valueOf(limitStr(this.file)) + ":" + this.line;
    }

    public LuaError(String message, Throwable cause) {
        super(message);
        this.prefix = null;
        this.cause = cause;
        this.level = 1;
    }

    public LuaError(String message, int level) {
        super(message);
        this.prefix = null;
        this.level = level;
    }

    public LuaError(LuaValue message_object) {
        super(message_object.tojstring());
        this.prefix = null;
        this.object = message_object;
        this.level = 1;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }

    public static class Internal extends LuaError {
        public Internal(Throwable cause) {
            super(cause);
        }
    }

    public static class StackOverflow extends LuaError {
        public StackOverflow(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static void main(String[] args) throws Throwable {
    }
}
