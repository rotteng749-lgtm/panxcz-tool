package luaj.compiler;

import luaj.LocVars;
import luaj.Lua;
import luaj.LuaError;
import luaj.LuaString;
import luaj.LuaValue;
import luaj.Prototype;
import luaj.Upvaldesc;
import luaj.compiler.LexState;

/* JADX INFO: loaded from: classes.dex */
public class Constants extends Lua {
    static final int LUAI_MAXUPVAL = 255;
    static final int LUAI_MAXVARS = 200;
    public static final int MAXSTACK = 250;
    static final int NO_REG = 255;
    static final int OpArgK = 3;
    static final int OpArgN = 0;
    static final int OpArgR = 2;
    static final int OpArgU = 1;
    static final int iABC = 0;
    static final int iABx = 1;
    static final int iAsBx = 2;

    protected static void _assert(boolean b) {
        if (!b) {
            throw new LuaError("compiler assert failed");
        }
    }

    public static void SET_OPCODE(InstructionPtr i, int o) {
        i.set((i.get() & (-64)) | ((o << 0) & 63));
    }

    public static void SETARG_A(int[] code, int index, int u) {
        code[index] = (code[index] & Lua.MASK_NOT_A) | ((u << 6) & Lua.MASK_A);
    }

    public static void SETARG_A(InstructionPtr i, int u) {
        i.set((i.get() & Lua.MASK_NOT_A) | ((u << 6) & Lua.MASK_A));
    }

    public static void SETARG_B(InstructionPtr i, int u) {
        i.set((i.get() & Lua.MASK_NOT_B) | ((u << 23) & Lua.MASK_B));
    }

    public static void SETARG_C(InstructionPtr i, int u) {
        i.set((i.get() & Lua.MASK_NOT_C) | ((u << 14) & Lua.MASK_C));
    }

    public static void SETARG_Bx(InstructionPtr i, int u) {
        i.set((i.get() & Lua.MASK_NOT_Bx) | ((u << 14) & Lua.MASK_Bx));
    }

    public static void SETARG_sBx(InstructionPtr i, int u) {
        SETARG_Bx(i, Lua.MAXARG_sBx + u);
    }

    public static int CREATE_ABC(int o, int a, int b, int c) {
        return ((o << 0) & 63) | ((a << 6) & Lua.MASK_A) | ((b << 23) & Lua.MASK_B) | ((c << 14) & Lua.MASK_C);
    }

    public static int CREATE_ABx(int o, int a, int bc) {
        return ((o << 0) & 63) | ((a << 6) & Lua.MASK_A) | ((bc << 14) & Lua.MASK_Bx);
    }

    public static int CREATE_Ax(int o, int a) {
        return ((o << 0) & 63) | ((a << 6) & (-64));
    }

    static LuaValue[] realloc(LuaValue[] v, int n) {
        LuaValue[] a = new LuaValue[n];
        if (v != null) {
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        }
        return a;
    }

    static Prototype[] realloc(Prototype[] v, int n) {
        Prototype[] a = new Prototype[n];
        if (v != null) {
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        }
        return a;
    }

    static LuaString[] realloc(LuaString[] v, int n) {
        LuaString[] a = new LuaString[n];
        if (v != null) {
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        }
        return a;
    }

    static LocVars[] realloc(LocVars[] v, int n) {
        LocVars[] a = new LocVars[n];
        if (v != null) {
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        }
        return a;
    }

    static Upvaldesc[] realloc(Upvaldesc[] v, int n) {
        Upvaldesc[] a = new Upvaldesc[n];
        if (v != null) {
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        }
        return a;
    }

    static LexState.Vardesc[] realloc(LexState.Vardesc[] v, int n) {
        LexState.Vardesc[] a = new LexState.Vardesc[n];
        if (v != null) {
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        }
        return a;
    }

    static LexState.Labeldesc[] grow(LexState.Labeldesc[] v, int min_n) {
        return v == null ? new LexState.Labeldesc[2] : v.length < min_n ? realloc(v, v.length * 2) : v;
    }

    static LexState.Labeldesc[] realloc(LexState.Labeldesc[] v, int n) {
        LexState.Labeldesc[] a = new LexState.Labeldesc[n];
        if (v != null) {
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        }
        return a;
    }

    static int[] realloc(int[] v, int n) {
        int[] a = new int[n];
        if (v != null) {
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        }
        return a;
    }

    static byte[] realloc(byte[] v, int n) {
        byte[] a = new byte[n];
        if (v != null) {
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        }
        return a;
    }

    static char[] realloc(char[] v, int n) {
        char[] a = new char[n];
        if (v != null) {
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        }
        return a;
    }

    protected Constants() {
    }
}
