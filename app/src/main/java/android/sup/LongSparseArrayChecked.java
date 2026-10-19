package android.sup;

/* JADX INFO: loaded from: classes.dex */
public class LongSparseArrayChecked<E> implements Cloneable {
    private static final Object DELETED = new Object();
    private boolean[] mChecks;
    private boolean mGarbage;
    private long[] mKeys;
    private int mSize;
    private Object[] mValues;

    public LongSparseArrayChecked() {
        this(16);
    }

    public LongSparseArrayChecked(int initialCapacity) {
        this.mGarbage = false;
        if (initialCapacity == 0) {
            this.mKeys = ContainerHelpers.EMPTY_LONGS;
            this.mValues = ContainerHelpers.EMPTY_OBJECTS;
            this.mChecks = ContainerHelpers.EMPTY_BOOLEANS;
        } else {
            int initialCapacity2 = ContainerHelpers.idealLongArraySize(initialCapacity);
            this.mKeys = new long[initialCapacity2];
            this.mValues = new Object[initialCapacity2];
            this.mChecks = new boolean[initialCapacity2];
        }
        this.mSize = 0;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public LongSparseArrayChecked<E> m34clone() {
        LongSparseArrayChecked<E> clone = null;
        try {
            clone = (LongSparseArrayChecked) super.clone();
            clone.mKeys = (long[]) this.mKeys.clone();
            clone.mValues = (Object[]) this.mValues.clone();
            clone.mChecks = (boolean[]) this.mChecks.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return clone;
        }
    }

    public E get(long key) {
        return get(key, null);
    }

    public E get(long j, E e) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, j);
        return (iBinarySearch < 0 || this.mValues[iBinarySearch] == DELETED) ? e : (E) this.mValues[iBinarySearch];
    }

    public boolean isChecked(long key) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i < 0 || this.mValues[i] == DELETED) {
            return false;
        }
        return this.mChecks[i];
    }

    public E delete(long j) {
        E e;
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, j);
        if (iBinarySearch < 0 || (e = (E) this.mValues[iBinarySearch]) == DELETED) {
            return null;
        }
        this.mValues[iBinarySearch] = DELETED;
        this.mGarbage = true;
        return e;
    }

    public E removeAt(int i) {
        E e = (E) this.mValues[i];
        if (e == DELETED) {
            return null;
        }
        this.mValues[i] = DELETED;
        this.mGarbage = true;
        return e;
    }

    private void gc() {
        int n = this.mSize;
        int o = 0;
        long[] keys = this.mKeys;
        Object[] values = this.mValues;
        boolean[] checks = this.mChecks;
        for (int i = 0; i < n; i++) {
            Object val = values[i];
            if (val != DELETED) {
                if (i != o) {
                    keys[o] = keys[i];
                    values[o] = val;
                    checks[o] = checks[i];
                    values[i] = null;
                }
                o++;
            }
        }
        this.mGarbage = false;
        this.mSize = o;
    }

    public void put(long key, E value, boolean check) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i >= 0) {
            this.mValues[i] = value;
            this.mChecks[i] = check;
            return;
        }
        int i2 = i ^ (-1);
        if (i2 < this.mSize && this.mValues[i2] == DELETED) {
            this.mKeys[i2] = key;
            this.mValues[i2] = value;
            this.mChecks[i2] = check;
            return;
        }
        if (this.mGarbage && this.mSize >= this.mKeys.length) {
            gc();
            i2 = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key) ^ (-1);
        }
        this.mKeys = GrowingArrayUtils.insert(this.mKeys, this.mSize, i2, key);
        this.mValues = GrowingArrayUtils.insert((E[]) this.mValues, this.mSize, i2, value);
        this.mChecks = GrowingArrayUtils.insert(this.mChecks, this.mSize, i2, check);
        this.mSize++;
    }

    public E put(long j, E e, byte b) {
        E e2 = null;
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, j);
        if (iBinarySearch >= 0) {
            e2 = (E) this.mValues[iBinarySearch];
            this.mValues[iBinarySearch] = e;
            if (b != 0) {
                this.mChecks[iBinarySearch] = b == 1;
            }
        } else {
            int iBinarySearch2 = iBinarySearch ^ (-1);
            if (iBinarySearch2 < this.mSize && this.mValues[iBinarySearch2] == DELETED) {
                this.mKeys[iBinarySearch2] = j;
                this.mValues[iBinarySearch2] = e;
                if (b != 0) {
                    this.mChecks[iBinarySearch2] = b == 1;
                }
            } else {
                if (this.mGarbage && this.mSize >= this.mKeys.length) {
                    gc();
                    iBinarySearch2 = ContainerHelpers.binarySearch(this.mKeys, this.mSize, j) ^ (-1);
                }
                this.mKeys = GrowingArrayUtils.insert(this.mKeys, this.mSize, iBinarySearch2, j);
                this.mValues = GrowingArrayUtils.insert((E[]) this.mValues, this.mSize, iBinarySearch2, e);
                this.mChecks = GrowingArrayUtils.insert(this.mChecks, this.mSize, iBinarySearch2, b == 1);
                this.mSize++;
            }
        }
        return e2;
    }

    public int size() {
        if (this.mGarbage) {
            gc();
        }
        return this.mSize;
    }

    public long keyAt(int index) {
        if (this.mGarbage) {
            gc();
        }
        return this.mKeys[index];
    }

    public E valueAt(int i) {
        if (this.mGarbage) {
            gc();
        }
        return (E) this.mValues[i];
    }

    public boolean checkAt(int index) {
        if (this.mGarbage) {
            gc();
        }
        return this.mChecks[index];
    }

    public void setValueAt(int index, E value) {
        if (this.mGarbage) {
            gc();
        }
        this.mValues[index] = value;
    }

    public void setCheckAt(int index, boolean check) {
        if (this.mGarbage) {
            gc();
        }
        this.mChecks[index] = check;
    }

    public int indexOfKey(long key) {
        if (this.mGarbage) {
            gc();
        }
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
    }

    public int indexOfValue(E value) {
        if (this.mGarbage) {
            gc();
        }
        for (int i = 0; i < this.mSize; i++) {
            if (this.mValues[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public int indexOfValueByValue(E value) {
        if (this.mGarbage) {
            gc();
        }
        for (int i = 0; i < this.mSize; i++) {
            if (value == null) {
                if (this.mValues[i] == null) {
                    return i;
                }
            } else if (value.equals(this.mValues[i])) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        int n = this.mSize;
        Object[] values = this.mValues;
        for (int i = 0; i < n; i++) {
            values[i] = null;
        }
        this.mSize = 0;
        this.mGarbage = false;
    }

    public void truncate() {
        this.mKeys = new long[16];
        this.mValues = new Object[16];
        this.mChecks = new boolean[16];
        this.mSize = 0;
        this.mGarbage = false;
    }

    public void append(long key, E value, boolean check) {
        if (this.mSize != 0 && key <= this.mKeys[this.mSize - 1]) {
            put(key, value, check);
            return;
        }
        if (this.mGarbage && this.mSize >= this.mKeys.length) {
            gc();
        }
        this.mKeys = GrowingArrayUtils.append(this.mKeys, this.mSize, key);
        this.mValues = GrowingArrayUtils.append((E[]) this.mValues, this.mSize, value);
        this.mChecks = GrowingArrayUtils.append(this.mChecks, this.mSize, check);
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
            Object value = valueAt(i);
            if (value != this) {
                buffer.append(value);
            } else {
                buffer.append("(this Map)");
            }
            buffer.append(" [");
            buffer.append(checkAt(i));
            buffer.append("]");
        }
        buffer.append('}');
        return buffer.toString();
    }
}
