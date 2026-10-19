package luaj;

/* JADX INFO: loaded from: classes.dex */
public final class UpValue {
    LuaValue[] array;
    int index;

    public UpValue(LuaValue[] stack, int index) {
        this.array = stack;
        this.index = index;
    }

    public String toString() {
        return new StringBuilder().append(this.index).append('/').append(this.array.length).append(' ').append(this.array[this.index]).toString();
    }

    public String tojstring() {
        return this.array[this.index].tojstring();
    }

    public final LuaValue getValue() {
        return this.array[this.index];
    }

    public final void setValue(LuaValue value) {
        this.array[this.index] = value;
    }

    public final void close() {
        LuaValue[] old = this.array;
        this.array = new LuaValue[]{old[this.index]};
        old[this.index] = null;
        this.index = 0;
    }
}
