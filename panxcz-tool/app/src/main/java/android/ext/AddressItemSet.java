package android.ext;

import android.sup.LongSparseLongArray;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AddressItemSet {
    private final LongSparseLongArray[] types;

    public static class Result {
        public long data;
        public boolean found;
    }

    public AddressItemSet() {
        this(10);
    }

    public AddressItemSet(int initialCapacity) {
        LongSparseLongArray[] types = new LongSparseLongArray[7];
        for (int i = 0; i < 7; i++) {
            types[i] = new LongSparseLongArray(initialCapacity);
        }
        this.types = types;
    }

    public void get(long address, int flags, Result ret) {
        LongSparseLongArray list;
        int index;
        int type = Integer.numberOfTrailingZeros(flags);
        if (type < 7 && (index = (list = this.types[type]).indexOfKey(address)) >= 0) {
            ret.data = list.valueAt(index);
            ret.found = true;
        } else {
            ret.found = false;
        }
    }

    public boolean put(long address, int flags, long data) {
        int type = Integer.numberOfTrailingZeros(flags);
        if (type >= 7) {
            return false;
        }
        LongSparseLongArray list = this.types[type];
        return list.put(address, data);
    }

    public void clear() {
        LongSparseLongArray[] types = this.types;
        for (int i = 0; i < 7; i++) {
            types[i].clear();
        }
    }

    public void truncate() {
        LongSparseLongArray[] types = this.types;
        for (int i = 0; i < 7; i++) {
            types[i].truncate();
        }
    }

    public int size() {
        LongSparseLongArray[] types = this.types;
        int size = 0;
        for (int i = 0; i < 7; i++) {
            size += types[i].size();
        }
        return size;
    }

    public void revert() {
        LongSparseLongArray[] types = this.types;
        boolean changed = false;
        for (int i = 0; i < 7; i++) {
            LongSparseLongArray list = types[i];
            int n = list.size();
            if (n > 0) {
                if (!changed) {
                    changed = true;
                }
                int flags = (1 << i) | AddressItem.FLAG_REVERT;
                for (int j = 0; j < n; j++) {
                    try {
                        AddressItem item = new AddressItem();
                        item.flags = flags;
                        item.address = list.keyAt(j);
                        item.data = list.valueAt(j);
                        item.alter();
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Log.w("Concurent modification for " + i, e);
                    }
                }
            }
        }
        if (changed) {
            MainService.instance.onMemoryChanged();
        }
    }

    public List<AddressItem> getAll() {
        List<AddressItem> items = new ArrayList<>();
        LongSparseLongArray[] types = this.types;
        for (int i = 0; i < 7; i++) {
            LongSparseLongArray list = types[i];
            int n = list.size();
            if (n > 0) {
                int flags = 1 << i;
                for (int j = 0; j < n; j++) {
                    try {
                        AddressItem item = new AddressItem();
                        item.flags = flags;
                        item.address = list.keyAt(j);
                        item.data = list.valueAt(j);
                        items.add(item);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Log.w("Concurent modification for " + i, e);
                    }
                }
            }
        }
        return items;
    }
}
