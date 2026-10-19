package luaj;

/* JADX INFO: loaded from: classes.dex */
public class Upvaldesc {
    public final short idx;
    public final boolean instack;
    public LuaString name;

    public Upvaldesc(LuaString name, boolean instack, int idx) {
        this.name = name;
        this.instack = instack;
        this.idx = (short) idx;
    }

    public String toString() {
        return String.valueOf(String.valueOf(this.instack ? 'v' : 'u')) + ((int) this.idx) + " \"" + String.valueOf(this.name) + "\"";
    }
}
