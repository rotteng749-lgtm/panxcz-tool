package luaj.lib;

import luaj.LuaError;
import luaj.LuaLong;
import luaj.LuaTable;
import luaj.LuaValue;
import luaj.Varargs;

/* JADX INFO: loaded from: classes.dex */
public class TableLib extends TwoArgFunction {
    @Override // luaj.lib.TwoArgFunction, luaj.lib.LibFunction, luaj.LuaValue
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable table = new LuaTable();
        table.set("concat", new concat());
        table.set("insert", new insert());
        table.set("move", new move());
        table.set("pack", new pack());
        table.set("remove", new remove());
        table.set("sort", new sort());
        table.set("unpack", new unpack());
        env.set("table", table);
        if (!env.get("package").isnil()) {
            env.get("package").get("loaded").set("table", table);
        }
        return NIL;
    }

    static class concat extends TableLibFunction {
        concat() {
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue list) {
            return list.checktable().concat(EMPTYSTRING, 1, list.length());
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue list, LuaValue sep) {
            return list.checktable().concat(sep.checkstring(), 1, list.length());
        }

        @Override // luaj.lib.LibFunction, luaj.LuaValue
        public LuaValue call(LuaValue list, LuaValue sep, LuaValue i) {
            return list.checktable().concat(sep.checkstring(), i.checkint(), list.length());
        }

        @Override // luaj.lib.LibFunction
        public LuaValue call(LuaValue list, LuaValue sep, LuaValue i, LuaValue j) {
            return list.checktable().concat(sep.checkstring(), i.checkint(), j.checkint());
        }
    }

    static class insert extends VarArgFunction {
        insert() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            int narg = args.narg();
            if (narg != 2 && narg != 3) {
                throw new LuaError("wrong number of arguments to 'table.insert': " + args.narg() + " (must be 2 or 3)");
            }
            LuaTable table = args.checktable(1);
            int len = table.length();
            if (len >= 16777215) {
                throw new LuaError("array too big: " + len + " >= " + LuaTable.MAX_LEN);
            }
            int len2 = len + 1;
            int pos = narg == 2 ? len2 : args.checkint(2);
            if (pos < 1 || pos > len2) {
                argerror(2, "table.insert", "position out of bounds: " + pos + " not between 1 and " + len2);
            }
            if (pos >= 16777215) {
                throw new LuaError("position too big: " + pos + " >= " + LuaTable.MAX_LEN);
            }
            table.insert(pos, args.arg(narg != 2 ? 3 : 2));
            return NONE;
        }
    }

    static class move extends VarArgFunction {
        move() {
        }

        private static long subtractExact(long x, long y) {
            long r = x - y;
            if (((x ^ y) & (x ^ r)) < 0) {
                return Long.MAX_VALUE;
            }
            return r;
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaTable a1 = args.checktable(1);
            long f = args.checklong(2);
            long e = args.checklong(3);
            long t = args.checklong(4);
            LuaTable a2 = args.opttable(5, a1);
            if (f <= e) {
                long diff = subtractExact(e, f);
                if (diff > 16777215 - 1) {
                    throw new LuaError("too many elements to move: " + diff + " (max is 16777215)");
                }
                if (t > 0 && diff > Long.MAX_VALUE - t) {
                    throw new LuaError("move: destination wrap around");
                }
                boolean fitInt = ((long) ((int) f)) == f && ((long) ((int) e)) == e;
                if (a1 != a2) {
                    for (long i = f; i <= e; i++) {
                        a2.set(LuaLong.valueOf((i - f) + t), fitInt ? a1.get((int) i) : a1.get(LuaLong.valueOf(i)));
                        if (i == e) {
                            break;
                        }
                    }
                } else if (t <= f || e < t) {
                    for (long i2 = f; i2 <= e; i2++) {
                        a2.set(LuaLong.valueOf((i2 - f) + t), fitInt ? a1.get((int) i2) : a1.get(LuaLong.valueOf(i2)));
                        if (i2 == e) {
                            break;
                        }
                    }
                } else {
                    for (long i3 = e; i3 >= f; i3--) {
                        a2.set(LuaLong.valueOf((i3 - f) + t), fitInt ? a1.get((int) i3) : a1.get(LuaLong.valueOf(i3)));
                        if (i3 == f) {
                            break;
                        }
                    }
                }
            }
            return a2;
        }
    }

    static class pack extends VarArgFunction {
        pack() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaValue t = tableOf(args, 1);
            t.set("n", args.narg());
            return t;
        }
    }

    static class remove extends VarArgFunction {
        remove() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaTable table = args.checktable(1);
            int size = table.length();
            int pos = args.optint(2, size);
            if (pos != size && (pos < 1 || pos > size + 1)) {
                argerror(2, "table.remove", "position out of bounds: " + pos + " not between 1 and " + (size + 1));
            }
            if (pos >= 16777215) {
                throw new LuaError("position too big: " + pos + " >= " + LuaTable.MAX_LEN);
            }
            return table.remove(pos, size);
        }
    }

    static class sort extends VarArgFunction {
        sort() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            args.checktable(1).sort(args.isnil(2) ? NIL : args.checkfunction(2));
            return NONE;
        }
    }

    static class unpack extends VarArgFunction {
        unpack() {
        }

        @Override // luaj.lib.VarArgFunction, luaj.lib.LibFunction, luaj.LuaValue
        public Varargs invoke(Varargs args) {
            LuaTable t = args.checktable(1);
            long len = args.arg(3).isnil() ? t.length() : 0;
            return t.unpack(args.optlong(2, 1L), args.optlong(3, len));
        }
    }

    public static void main(String[] args) throws Throwable {
    }
}
