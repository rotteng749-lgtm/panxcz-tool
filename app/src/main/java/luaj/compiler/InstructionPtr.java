package luaj.compiler;

/* JADX INFO: loaded from: classes.dex */
public class InstructionPtr {
    final int[] code;
    final int idx;

    public InstructionPtr(int[] code, int idx) {
        this.code = code;
        this.idx = idx;
    }

    int get() {
        return this.code[this.idx];
    }

    void set(int value) {
        this.code[this.idx] = value;
    }
}
