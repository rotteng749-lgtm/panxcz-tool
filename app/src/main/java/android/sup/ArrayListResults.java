package android.sup;

import android.ext.AddressItem;

/* JADX INFO: loaded from: classes.dex */
public class ArrayListResults {
    private static final int MIN_CAPACITY_INCREMENT = 12;
    private long[] mAddrData;
    private boolean[] mChecks;
    private int[] mFlags;
    int size;

    static IndexOutOfBoundsException throwIndexOutOfBoundsException(int index, int size) {
        throw new IndexOutOfBoundsException("Invalid index " + index + ", size is " + size);
    }

    public ArrayListResults() {
        this(0);
    }

    public ArrayListResults(int capacity) {
        setCapacity(ContainerHelpers.idealLongArraySize(capacity));
    }

    private void setCapacity(int capacity) {
        long[] newAddrData;
        int[] newFlags;
        boolean[] newChecks;
        if (capacity == 0) {
            newAddrData = ContainerHelpers.EMPTY_LONGS;
            newFlags = ContainerHelpers.EMPTY_INTS;
            newChecks = ContainerHelpers.EMPTY_BOOLEANS;
        } else {
            newAddrData = new long[capacity * 2];
            newFlags = new int[capacity];
            newChecks = new boolean[capacity];
        }
        int size = this.size;
        if (size != 0) {
            System.arraycopy(this.mAddrData, 0, newAddrData, 0, size * 2);
            System.arraycopy(this.mFlags, 0, newFlags, 0, size);
            System.arraycopy(this.mChecks, 0, newChecks, 0, size);
        }
        this.mAddrData = newAddrData;
        this.mFlags = newFlags;
        this.mChecks = newChecks;
    }

    private void grow() {
        int size = this.size;
        setCapacity(size + (size < 6 ? 12 : size >> 1));
    }

    public void add(long addr, long data, int flags, boolean check) {
        int size = this.size;
        if (size == this.mFlags.length) {
            grow();
        }
        this.mAddrData[(size * 2) + 0] = addr;
        this.mAddrData[(size * 2) + 1] = data;
        this.mFlags[size] = flags;
        this.mChecks[size] = check;
        this.size = size + 1;
    }

    public void ensureCapacity(int minimumCapacity) {
        if (this.mFlags.length < minimumCapacity) {
            setCapacity(minimumCapacity);
        }
    }

    public AddressItem get(int index, AddressItem item) {
        if (index >= this.size) {
            throwIndexOutOfBoundsException(index, this.size);
        }
        if (item == null) {
            return new AddressItem(this.mAddrData[(index * 2) + 0], this.mAddrData[(index * 2) + 1], this.mFlags[index]);
        }
        item.address = this.mAddrData[(index * 2) + 0];
        item.data = this.mAddrData[(index * 2) + 1];
        item.flags = this.mFlags[index];
        return item;
    }

    public boolean checked(int index) {
        return this.mChecks[index];
    }

    public void checked(int index, boolean check) {
        this.mChecks[index] = check;
    }

    public void trimToSize() {
        if (this.size != this.mChecks.length) {
            setCapacity(this.size);
        }
    }

    public int size() {
        return this.size;
    }

    public void clear() {
        this.size = 0;
    }
}
