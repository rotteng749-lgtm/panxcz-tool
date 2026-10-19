package android.ext;

import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class UsedList {
    private static final int CAPACITY = 32;
    private final int index;
    private int used = 0;
    private int[] data = new int[32];

    public UsedList(int index) {
        this.index = index;
    }

    public void add(int value) {
        int used = this.used;
        int[] data = this.data;
        for (int i = 0; i < used; i++) {
            if (data[i] == value) {
                return;
            }
        }
        if (used == data.length) {
            data = Arrays.copyOf(data, data.length + 32);
            this.data = data;
        }
        data[used] = value;
        this.used = used + 1;
        saveData();
    }

    private void saveData() {
        // Disabled: do not persist usage statistics.
    }
}
