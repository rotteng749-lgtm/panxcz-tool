package android.fix;

import android.annotation.TargetApi;
import android.ext.Log;
import android.os.Build;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes.dex */
public class SparseArray<E> extends android.util.SparseArray<E> {
    public SparseArray() {
    }

    public SparseArray(int initialCapacity) {
        super(initialCapacity);
    }

    private void fixSize() {
        try {
            Field mKeys = android.util.SparseArray.class.getDeclaredField("mKeys");
            mKeys.setAccessible(true);
            Field mValues = android.util.SparseArray.class.getDeclaredField("mValues");
            mValues.setAccessible(true);
            Field mSize = android.util.SparseArray.class.getDeclaredField("mSize");
            mSize.setAccessible(true);
            int size = mSize.getInt(this);
            int keysLen = ((int[]) mKeys.get(this)).length;
            int valuesLen = ((Object[]) mValues.get(this)).length;
            if (size > keysLen || size > valuesLen) {
                mSize.setInt(this, Math.min(Math.min(size, keysLen), valuesLen));
            }
        } catch (Throwable e) {
            Log.w("Failed fix", e);
        }
    }

    @Override // android.util.SparseArray
    public void clear() {
        try {
            super.clear();
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.badImplementation(e);
            fixSize();
            super.clear();
        }
    }

    @Override // android.util.SparseArray
    public int size() {
        try {
            return super.size();
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.badImplementation(e);
            fixSize();
            return super.size();
        }
    }

    @Override // android.util.SparseArray
    public E get(int i, E e) {
        try {
            return (E) super.get(i, e);
        } catch (ArrayIndexOutOfBoundsException e2) {
            Log.badImplementation(e2);
            fixSize();
            return (E) super.get(i, e);
        }
    }

    public synchronized E syncGet(int key, E valueIfKeyNotFound) {
        return get(key, valueIfKeyNotFound);
    }

    public synchronized E syncGet(int key) {
        return get(key);
    }

    @Override // android.util.SparseArray
    public void delete(int key) {
        try {
            super.delete(key);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.badImplementation(e);
            fixSize();
            super.delete(key);
        }
    }

    @Override // android.util.SparseArray
    public void put(int key, E value) {
        try {
            super.put(key, value);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.badImplementation(e);
            fixSize();
            super.put(key, value);
        }
    }

    public synchronized void syncPut(int key, E value) {
        put(key, value);
    }

    @Override // android.util.SparseArray
    public int indexOfKey(int key) {
        try {
            return super.indexOfKey(key);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.badImplementation(e);
            fixSize();
            return super.indexOfKey(key);
        }
    }

    @Override // android.util.SparseArray
    @TargetApi(11)
    public void removeAt(int index) {
        if (Build.VERSION.SDK_INT > 11) {
            super.removeAt(index);
        } else {
            remove(keyAt(index));
        }
    }
}
