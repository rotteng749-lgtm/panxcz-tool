package luaj.lib;

import android.ext.SearchMenuItem;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import luaj.Globals;
import luaj.LuaString;
import luaj.LuaTable;
import luaj.LuaValue;
import luaj.Varargs;

/* JADX INFO: loaded from: classes.dex */
public class GgLib {
    private GgLib() {
    }

    public static final class saveVariable extends VarArgFunction {
        static final String[] INDS = {"address", "value", "flags", "name", "freeze", "freezeType", "freezeFrom", "freezeTo"};
        final Integer ZERO = 0;
        final Globals globals;
        List<LuaTable> list;
        Writer out;
        Map<LuaTable, Integer> tables;

        public saveVariable(Globals globals) {
            this.globals = globals;
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaValue var = args.arg(1);
            String file = args.checkjstring(2);
            this.globals.addOpenFiles(file);
            try {
                save(var, file);
                return LuaValue.NIL;
            } catch (Throwable e) {
                return LuaValue.valueOf(e.toString());
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        private void save(LuaValue luaValue, String str) throws Throwable {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(str) { // from class: luaj.lib.GgLib.saveVariable.1
                    @Override // java.io.FileOutputStream, java.io.OutputStream
                    public void write(int b) throws IOException {
                        saveVariable.this.globals.addWriteBytes(1L);
                        super.write(b);
                    }

                    @Override // java.io.FileOutputStream, java.io.OutputStream
                    public void write(byte[] b) throws IOException {
                        saveVariable.this.globals.addWriteBytes(b.length);
                        super.write(b);
                    }

                    @Override // java.io.FileOutputStream, java.io.OutputStream
                    public void write(byte[] b, int off, int len) throws IOException {
                        saveVariable.this.globals.addWriteBytes(len);
                        super.write(b, off, len);
                    }
                }), 65536);
                this.out = bufferedWriter;
                if (luaValue instanceof LuaTable) {
                    this.tables = new HashMap();
                    buildSet((LuaTable) luaValue);
                    this.list = new ArrayList(this.tables.keySet());
                    dumpList();
                    buildTables();
                    bufferedWriter.write("\nreturn v[" + this.tables.get(luaValue) + "]\n");
                    this.tables = null;
                } else if (luaValue instanceof LuaString) {
                    bufferedWriter.write("return ");
                    escape(bufferedWriter, luaValue.tojstring());
                    bufferedWriter.write(10);
                } else {
                    bufferedWriter.write("return ");
                    bufferedWriter.write(luaValue.tojstring());
                    bufferedWriter.write(10);
                }
                bufferedWriter.close();
            } finally {
                this.out = null;
                this.tables = null;
                this.list = null;
            }
        }

        private void buildSet(LuaTable var) {
            Integer old = this.tables.put(var, this.ZERO);
            if (old == null && (var instanceof LuaTable)) {
                LuaTable.Iterator it = var.iterator();
                while (it.next()) {
                    int j = 0;
                    while (j < 2) {
                        LuaValue t = j == 0 ? it.key() : it.value();
                        if (t instanceof LuaTable) {
                            buildSet((LuaTable) t);
                        }
                        j++;
                    }
                }
            }
        }

        private void dumpList() throws IOException {
            String[] inds = INDS;
            this.out.write("local ");
            int nj = inds.length;
            for (int j = 0; j < nj; j++) {
                this.out.write((char) (j + 97));
                this.out.write(44);
            }
            this.out.write("v,t=");
            for (String str : inds) {
                this.out.write(39);
                this.out.write(str);
                this.out.write("',");
            }
            this.out.write("nil\nv={");
            Writer out = this.out;
            Map<LuaTable, Integer> tables = this.tables;
            int i = 0;
            for (LuaTable table : this.list) {
                i++;
                tables.put(table, Integer.valueOf(i));
                out.write("{");
                LuaTable.Iterator it = table.iterator();
                int p = 1;
                while (it.next()) {
                    int k = it.intkey();
                    if (k == 0) {
                        LuaValue key = it.key();
                        if (!(key instanceof LuaTable)) {
                            out.write("[");
                            dumpValue(out, inds, tables, key);
                            out.write("]=");
                        }
                    } else if (k != p) {
                        out.write("[");
                        out.write(Integer.toString(k));
                        out.write("]=");
                    } else {
                        p++;
                    }
                    LuaValue v = it.value();
                    if (v instanceof LuaTable) {
                        out.write("nil");
                    } else {
                        dumpValue(out, inds, tables, v);
                    }
                    out.write(";");
                }
                out.write("};");
            }
            out.write("}");
        }

        private void dumpValue(Writer out, String[] inds, Map<LuaTable, Integer> tables, LuaValue v) throws IOException {
            if (v instanceof LuaTable) {
                out.write("v[");
                out.write(Integer.toString(tables.get(v).intValue()));
                out.write(93);
            } else {
                if (v instanceof LuaString) {
                    String str = v.tojstring();
                    int nj = inds.length;
                    for (int j = 0; j < nj; j++) {
                        if (str.equals(inds[j])) {
                            out.write(j + 97);
                            return;
                        }
                    }
                    escape(out, str);
                    return;
                }
                out.write(v.tojstring());
            }
        }

        private void buildTables() throws IOException {
            String[] inds = INDS;
            Writer out = this.out;
            List<LuaTable> list = this.list;
            Map<LuaTable, Integer> tables = this.tables;
            int i = 0;
            for (LuaTable table : list) {
                i++;
                boolean first = true;
                LuaTable.Iterator it = table.iterator();
                while (it.next()) {
                    LuaValue key = null;
                    int k = it.intkey();
                    LuaValue v = it.value();
                    if (!(v instanceof LuaTable)) {
                        if (k == 0) {
                            key = it.key();
                            if (key instanceof LuaTable) {
                            }
                        }
                    } else {
                        key = k != 0 ? null : it.key();
                    }
                    if (first) {
                        first = false;
                        out.write("\nt=v[");
                        out.write(Integer.toString(i));
                        out.write(93);
                    }
                    out.write("\nt[");
                    if (key == null) {
                        out.write(Integer.toString(k));
                    } else {
                        dumpValue(out, inds, tables, key);
                    }
                    out.write("]=");
                    dumpValue(out, inds, tables, v);
                }
            }
        }

        private Appendable escape(Appendable sb, String str) throws IOException {
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append(SearchMenuItem.HEX_UTF_8);
            int n = str.length();
            for (int i = 0; i < n; i++) {
                char ch = str.charAt(i);
                switch (ch) {
                    case 7:
                        ch = IoLib.A;
                        break;
                    case '\b':
                        ch = 'b';
                        break;
                    case '\t':
                        ch = 't';
                        break;
                    case '\n':
                        ch = 'n';
                        break;
                    case 11:
                        ch = 'v';
                        break;
                    case '\f':
                        ch = 'f';
                        break;
                    case '\r':
                        ch = IoLib.R;
                        break;
                    case '\'':
                    case '\\':
                        break;
                    default:
                        sb.append(ch);
                        continue;
                }
                sb.append('\\');
                sb.append(ch);
            }
            sb.append(SearchMenuItem.HEX_UTF_8);
            return sb;
        }
    }

    public static void main(String[] args) throws Throwable {
    }
}
