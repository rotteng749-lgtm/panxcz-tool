package android.sup;

/* JADX INFO: loaded from: classes.dex */
public class LongSparseLongArray implements Cloneable {
    private long[] mKeys;
    private int mSize;
    private long[] mValues;

    public LongSparseLongArray() {
        this(16);
    }

    public LongSparseLongArray(int initialCapacity) {
        if (initialCapacity == 0) {
            this.mKeys = ContainerHelpers.EMPTY_LONGS;
            this.mValues = ContainerHelpers.EMPTY_LONGS;
        } else {
            int initialCapacity2 = ContainerHelpers.idealLongArraySize(initialCapacity);
            this.mKeys = new long[initialCapacity2];
            this.mValues = new long[initialCapacity2];
        }
        this.mSize = 0;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public LongSparseLongArray m35clone() {
        LongSparseLongArray clone = null;
        try {
            clone = (LongSparseLongArray) super.clone();
            clone.mKeys = (long[]) this.mKeys.clone();
            clone.mValues = (long[]) this.mValues.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return clone;
        }
    }

    public long get(long key) {
        return get(key, 0L);
    }

    public long get(long key, long valueIfKeyNotFound) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i < 0) {
            return valueIfKeyNotFound;
        }
        long valueIfKeyNotFound2 = this.mValues[i];
        return valueIfKeyNotFound2;
    }

    public void delete(long key) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i >= 0) {
            removeAt(i);
        }
    }

    public void removeAt(int index) {
        System.arraycopy(this.mKeys, index + 1, this.mKeys, index, this.mSize - (index + 1));
        System.arraycopy(this.mValues, index + 1, this.mValues, index, this.mSize - (index + 1));
        this.mSize--;
    }

    public boolean put(long key, long value) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i >= 0) {
            long[] mValues = this.mValues;
            if (mValues[i] == value) {
                return false;
            }
            mValues[i] = value;
        } else {
            int i2 = i ^ (-1);
            this.mKeys = GrowingArrayUtils.insert(this.mKeys, this.mSize, i2, key);
            this.mValues = GrowingArrayUtils.insert(this.mValues, this.mSize, i2, value);
            this.mSize++;
        }
        return true;
    }

    public int size() {
        return this.mSize;
    }

    public long keyAt(int index) {
        return this.mKeys[index];
    }

    public long valueAt(int index) {
        return this.mValues[index];
    }

    public int indexOfKey(long key) {
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
    }

    public int indexOfValue(long value) {
        for (int i = 0; i < this.mSize; i++) {
            if (this.mValues[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        this.mSize = 0;
    }

    public void truncate() {
        this.mKeys = new long[16];
        this.mValues = new long[16];
        this.mSize = 0;
    }

    public void append(long key, long value) {
        if (this.mSize != 0 && key <= this.mKeys[this.mSize - 1]) {
            put(key, value);
            return;
        }
        this.mKeys = GrowingArrayUtils.append(this.mKeys, this.mSize, key);
        this.mValues = GrowingArrayUtils.append(this.mValues, this.mSize, value);
        this.mSize++;
    }

    public String toString() {
        if (size() <= 0) {
            return "{}";
        }
        StringBuilder buffer = new StringBuilder(this.mSize * 28);
        buffer.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            long key = keyAt(i);
            buffer.append(key);
            buffer.append('=');
            long value = valueAt(i);
            buffer.append(value);
        }
        buffer.append('}');
        return buffer.toString();
    }
}
