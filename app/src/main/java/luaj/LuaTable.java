package luaj;

import android.ext.AddressItem;
import android.ext.ListManager;
import android.ext.Log;
import android.ext.Script;
import android.ext.SearchMenuItem;
import android.ext.ToolsLight;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class LuaTable extends LuaValue implements Metatable {
    public static final int MAX_LEN = 16777215;
    private static final int MIN_HASH_CAPACITY = 2;
    private static final LuaString N = valueOf("n");
    private static final Slot[] NOBUCKETS = new Slot[0];
    private static volatile Set<LuaTable> dumped = null;
    private static volatile int totalCnt = 0;
    private LuaValue[] array;
    private Slot[] hash;
    protected int hashEntries;
    protected Metatable m_metatable;

    interface Slot {
        Slot add(Slot slot);

        int arraykey(int i);

        StrongSlot find(LuaValue luaValue);

        StrongSlot first();

        boolean keyeq(LuaValue luaValue);

        int keyindex(int i);

        Slot relink(Slot slot);

        Slot remove(StrongSlot strongSlot);

        Slot rest();

        Slot set(StrongSlot strongSlot, LuaValue luaValue);
    }

    interface StrongSlot extends Slot {
        LuaValue key();

        Varargs toVarargs();

        LuaValue value();
    }

    public LuaTable() {
        this.array = NOVALS;
        this.hash = NOBUCKETS;
    }

    public LuaTable(int narray, int nhash) {
        presize(narray, nhash);
    }

    public LuaTable(LuaValue[] named, LuaValue[] unnamed, Varargs lastarg) {
        int nn = named != null ? named.length : 0;
        int nu = unnamed != null ? unnamed.length : 0;
        int nl = lastarg != null ? lastarg.narg() : 0;
        presize(nu + nl, nn >> 1);
        for (int i = 0; i < nu; i++) {
            rawset(i + 1, unnamed[i]);
        }
        if (lastarg != null) {
            int n = lastarg.narg();
            for (int i2 = 1; i2 <= n; i2++) {
                rawset(nu + i2, lastarg.arg(i2));
            }
        }
        for (int i3 = 0; i3 < nn; i3 += 2) {
            if (!named[i3 + 1].isnil()) {
                rawset(named[i3], named[i3 + 1]);
            }
        }
    }

    public LuaTable(Varargs varargs) {
        this(varargs, 1);
    }

    public LuaTable(Varargs varargs, int firstarg) {
        int nskip = firstarg - 1;
        int n = Math.max(varargs.narg() - nskip, 0);
        presize(n, 1);
        set(N, valueOf(n));
        for (int i = 1; i <= n; i++) {
            set(i, varargs.arg(i + nskip));
        }
    }

    @Override // luaj.LuaValue
    public int type() {
        return 5;
    }

    @Override // luaj.LuaValue
    public String typename() {
        return "table";
    }

    @Override // luaj.LuaValue
    public boolean istable() {
        return true;
    }

    @Override // luaj.LuaValue
    public LuaTable checktable() {
        return this;
    }

    @Override // luaj.LuaValue
    public LuaTable opttable(LuaTable defval) {
        return this;
    }

    @Override // luaj.LuaValue
    public void presize(int narray) {
        if (narray > this.array.length) {
            this.array = resize(this.array, 1 << log2(narray));
        }
    }

    public void presize(int narray, int nhash) {
        if (nhash > 0 && nhash < 2) {
            nhash = 2;
        }
        this.array = narray > 0 ? new LuaValue[1 << log2(narray)] : NOVALS;
        this.hash = nhash > 0 ? new Slot[1 << log2(nhash)] : NOBUCKETS;
        this.hashEntries = 0;
    }

    private static LuaValue[] resize(LuaValue[] old, int n) {
        LuaValue[] v = new LuaValue[n];
        System.arraycopy(old, 0, v, 0, old.length);
        return v;
    }

    public int getArrayLength() {
        return this.array.length;
    }

    public int getHashLength() {
        return this.hash.length;
    }

    @Override // luaj.LuaValue
    public LuaValue getmetatable() {
        if (this.m_metatable != null) {
            return this.m_metatable.toLuaValue();
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x003f  */
    @Override // luaj.LuaValue
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public luaj.LuaValue setmetatable(luaj.LuaValue r6) {
        /*
            r5 = this;
            r2 = 1
            r3 = 0
            luaj.Metatable r4 = r5.m_metatable
            if (r4 == 0) goto L43
            luaj.Metatable r4 = r5.m_metatable
            boolean r4 = r4.useWeakKeys()
            if (r4 == 0) goto L43
            r0 = r2
        Lf:
            luaj.Metatable r4 = r5.m_metatable
            if (r4 == 0) goto L45
            luaj.Metatable r4 = r5.m_metatable
            boolean r4 = r4.useWeakValues()
            if (r4 == 0) goto L45
            r1 = r2
        L1c:
            luaj.Metatable r4 = metatableOf(r6)
            r5.m_metatable = r4
            luaj.Metatable r4 = r5.m_metatable
            if (r4 == 0) goto L47
            luaj.Metatable r4 = r5.m_metatable
            boolean r4 = r4.useWeakKeys()
            if (r4 == 0) goto L47
            r4 = r2
        L2f:
            if (r0 != r4) goto L3f
            luaj.Metatable r4 = r5.m_metatable
            if (r4 == 0) goto L49
            luaj.Metatable r4 = r5.m_metatable
            boolean r4 = r4.useWeakValues()
            if (r4 == 0) goto L49
        L3d:
            if (r1 == r2) goto L42
        L3f:
            r5.rehash(r3)
        L42:
            return r5
        L43:
            r0 = r3
            goto Lf
        L45:
            r1 = r3
            goto L1c
        L47:
            r4 = r3
            goto L2f
        L49:
            r2 = r3
            goto L3d
        */
        throw new UnsupportedOperationException("Method not decompiled: luaj.LuaTable.setmetatable(luaj.LuaValue):luaj.LuaValue");
    }

    @Override // luaj.LuaValue
    public LuaValue get(int key) {
        LuaValue v = rawget(key);
        return (!v.isnil() || this.m_metatable == null) ? v : gettable(this, valueOf(key));
    }

    @Override // luaj.LuaValue
    public LuaValue get(LuaValue key) {
        LuaValue v = rawget(key);
        return (!v.isnil() || this.m_metatable == null) ? v : gettable(this, key);
    }

    @Override // luaj.LuaValue
    public LuaValue rawget(int key) {
        if (key > 0 && key <= this.array.length) {
            LuaValue v = this.m_metatable == null ? this.array[key - 1] : this.m_metatable.arrayget(this.array, key - 1);
            if (v == null) {
                return NIL;
            }
            return v;
        }
        return hashget(LuaLong.valueOf(key));
    }

    @Override // luaj.LuaValue
    public LuaValue rawget(LuaValue key) {
        int ikey;
        if (key.isintnumber() && (ikey = key.toint()) > 0 && ikey <= this.array.length) {
            LuaValue v = this.m_metatable == null ? this.array[ikey - 1] : this.m_metatable.arrayget(this.array, ikey - 1);
            if (v == null) {
                return NIL;
            }
            return v;
        }
        return hashget(key);
    }

    protected LuaValue hashget(LuaValue key) {
        StrongSlot foundSlot = slotget(key);
        return foundSlot == null ? NIL : foundSlot.value();
    }

    private StrongSlot slotget(LuaValue key) {
        if (this.hashEntries > 0) {
            for (Slot slot = this.hash[hashSlot(key)]; slot != null; slot = slot.rest()) {
                StrongSlot foundSlot = slot.find(key);
                if (foundSlot != null) {
                    return foundSlot;
                }
            }
        }
        return null;
    }

    public long getchecklong(LuaValue key) {
        StrongSlot foundSlot = slotget(key);
        if (foundSlot == null) {
            return (this.m_metatable != null ? gettable(this, key) : NIL).checklong();
        }
        if (foundSlot instanceof IntValueEntry) {
            return ((IntValueEntry) foundSlot).getValue();
        }
        if (foundSlot instanceof LongValueEntry) {
            return ((LongValueEntry) foundSlot).getValue();
        }
        return foundSlot.value().checklong();
    }

    @Override // luaj.LuaValue
    public void set(int key, LuaValue value) {
        if (this.m_metatable == null || !rawget(key).isnil() || !settable(this, LuaLong.valueOf(key), value)) {
            rawset(key, value);
        }
    }

    @Override // luaj.LuaValue
    public void set(LuaValue key, LuaValue value) {
        if (key == null || (!key.isvalidkey() && !metatag(NEWINDEX).isfunction())) {
            throw new LuaError("value ('" + key + "') can not be used as a table index");
        }
        if (this.m_metatable == null || !rawget(key).isnil() || !settable(this, key, value)) {
            rawset(key, value);
        }
    }

    @Override // luaj.LuaValue
    public void rawset(int key, LuaValue value) {
        if (value != null && !arrayset(key, value)) {
            hashset(LuaLong.valueOf(key), value);
        }
    }

    @Override // luaj.LuaValue
    public void rawset(LuaValue key, LuaValue value) {
        if (key != null && value != null) {
            if (!key.isintnumber() || !arrayset(key.toint(), value)) {
                hashset(key, value);
            }
        }
    }

    private boolean arrayset(int key, LuaValue value) {
        if (key > 0 && key <= this.array.length) {
            LuaValue[] luaValueArr = this.array;
            int i = key - 1;
            if (value.isnil()) {
                value = null;
            } else if (this.m_metatable != null) {
                value = this.m_metatable.wrap(value);
            }
            luaValueArr[i] = value;
            return true;
        }
        return false;
    }

    public LuaValue remove(int pos, int n) {
        if (pos == 0) {
            pos = n;
        } else if (pos > n) {
            return NONE;
        }
        LuaValue v = get(pos);
        LuaValue r = v;
        while (!r.isnil()) {
            r = get(pos + 1);
            set(pos, r);
            pos++;
        }
        if (!v.isnil()) {
            return v;
        }
        return NONE;
    }

    public void insert(int pos, LuaValue value) {
        while (!value.isnil()) {
            LuaValue v = get(pos);
            set(pos, value);
            value = v;
            pos++;
        }
    }

    public LuaValue concat(LuaString sep, int i, int j) {
        Buffer sb = new Buffer();
        if (i <= j) {
            sb.append(get(i).checkstring());
            while (i < Integer.MAX_VALUE && (i = i + 1) <= j) {
                sb.append(sep);
                sb.append(get(i).checkstring());
            }
        }
        return sb.tostring();
    }

    @Override // luaj.LuaValue
    public int length() {
        if (this.m_metatable == null) {
            return rawlen();
        }
        LuaValue len = len();
        if (len.isintnumber()) {
            return len.toint();
        }
        throw new LuaError("table length is not an integer: " + len);
    }

    @Override // luaj.LuaValue
    public LuaValue len() {
        LuaValue h = metatag(LEN);
        return h.toboolean() ? h.call(this) : LuaLong.valueOf(rawlen());
    }

    @Override // luaj.LuaValue
    public int rawlen() {
        int a = getArrayLength();
        int n = a;
        int m = 0;
        if (n == 0 || !rawget(n).isnil()) {
            m = n;
            n++;
            while (!rawget(n).isnil()) {
                m = n;
                n += getHashLength() + a + 1;
            }
        }
        while (n > m + 1) {
            int k = (m + n) / 2;
            if (rawget(k).isnil()) {
                n = k;
            } else {
                m = k;
            }
        }
        return m;
    }

    @Override // luaj.LuaValue
    public Varargs next(LuaValue key) {
        int i = 0;
        if (!key.isnil() && (!key.isintnumber() || (i = key.toint()) <= 0 || i > this.array.length)) {
            if (this.hash.length == 0) {
                error("invalid key to 'next' 1: " + key);
            }
            int i2 = hashSlot(key);
            boolean found = false;
            for (Slot slot = this.hash[i2]; slot != null; slot = slot.rest()) {
                if (found) {
                    StrongSlot nextEntry = slot.first();
                    if (nextEntry != null) {
                        return nextEntry.toVarargs();
                    }
                } else if (slot.keyeq(key)) {
                    found = true;
                }
            }
            if (!found) {
                error("invalid key to 'next' 2: " + key);
            }
            i = i2 + this.array.length + 1;
        }
        while (i < this.array.length) {
            if (this.array[i] != null) {
                LuaValue value = this.m_metatable == null ? this.array[i] : this.m_metatable.arrayget(this.array, i);
                if (value != null) {
                    return varargsOf(LuaLong.valueOf(i + 1), value);
                }
            }
            i++;
        }
        for (int i3 = i - this.array.length; i3 < this.hash.length; i3++) {
            for (Slot slot2 = this.hash[i3]; slot2 != null; slot2 = slot2.rest()) {
                StrongSlot first = slot2.first();
                if (first != null) {
                    return first.toVarargs();
                }
            }
        }
        return NIL;
    }

    public LuaValue testIterator() {
        return NIL;
    }

    public Iterator iterator() {
        return new Iterator();
    }

    public class Iterator {
        private int i = -1;
        private Slot slot = null;
        private StrongSlot first = null;

        public Iterator() {
        }

        public boolean next() {
            StrongSlot first;
            Slot slot = this.slot;
            while (slot != null) {
                slot = slot.rest();
                if (slot != null && (first = slot.first()) != null) {
                    this.slot = slot;
                    this.first = first;
                    return true;
                }
            }
            this.slot = null;
            this.first = null;
            int i = this.i + 1;
            while (i < LuaTable.this.array.length) {
                if (LuaTable.this.array[i] == null || (LuaTable.this.m_metatable != null && LuaTable.this.m_metatable.arrayget(LuaTable.this.array, i) == null)) {
                    i++;
                } else {
                    this.i = i;
                    return true;
                }
            }
            for (int i2 = i - LuaTable.this.array.length; i2 < LuaTable.this.hash.length; i2++) {
                for (Slot slot2 = LuaTable.this.hash[i2]; slot2 != null; slot2 = slot2.rest()) {
                    StrongSlot first2 = slot2.first();
                    if (first2 != null) {
                        this.i = LuaTable.this.array.length + i2;
                        this.slot = slot2;
                        this.first = first2;
                        return true;
                    }
                }
            }
            return false;
        }

        public int intkey() {
            if (this.first == null) {
                return this.i + 1;
            }
            return 0;
        }

        public LuaValue key() {
            StrongSlot first = this.first;
            return first != null ? first.key() : LuaLong.valueOf(this.i + 1);
        }

        public LuaValue value() {
            StrongSlot first = this.first;
            return first != null ? first.value() : LuaTable.this.m_metatable == null ? LuaTable.this.array[this.i] : LuaTable.this.m_metatable.arrayget(LuaTable.this.array, this.i);
        }

        public Varargs varargs() {
            StrongSlot first = this.first;
            return first != null ? first.toVarargs() : LuaTable.varargsOf(key(), value());
        }
    }

    @Override // luaj.LuaValue
    public Varargs inext(LuaValue key) {
        int k = key.checkint() + 1;
        LuaValue v = get(k);
        return v.isnil() ? NONE : varargsOf(LuaLong.valueOf(k), v);
    }

    public void hashset(LuaValue key, LuaValue value) {
        Slot entry;
        if (value.isnil()) {
            hashRemove(key);
            return;
        }
        int index = 0;
        if (this.hash.length > 0) {
            index = hashSlot(key);
            for (Slot slot = this.hash[index]; slot != null; slot = slot.rest()) {
                StrongSlot foundSlot = slot.find(key);
                if (foundSlot != null) {
                    this.hash[index] = this.hash[index].set(foundSlot, value);
                    return;
                }
            }
        }
        if (this.hashEntries >= this.hash.length) {
            if ((this.m_metatable == null || !this.m_metatable.useWeakValues()) && key.isintnumber() && key.toint() > 0) {
                rehash(key.toint());
                if (arrayset(key.toint(), value)) {
                    return;
                }
            } else {
                rehash(-1);
            }
            index = hashSlot(key);
        }
        if (this.m_metatable != null) {
            entry = this.m_metatable.entry(key, value);
        } else {
            entry = defaultEntry(key, value);
        }
        Slot[] slotArr = this.hash;
        if (this.hash[index] != null) {
            entry = this.hash[index].add(entry);
        }
        slotArr[index] = entry;
        this.hashEntries++;
    }

    public static int hashmod(int hashCode, int mask) {
        return hashCode & mask;
    }

    public static int hashSlot(LuaValue key, int hashMask) {
        return key.hashCode() & hashMask;
    }

    private int hashSlot(LuaValue key) {
        return key.hashCode() & (this.hash.length - 1);
    }

    private void hashRemove(LuaValue key) {
        if (this.hash.length > 0) {
            int index = hashSlot(key);
            for (Slot slot = this.hash[index]; slot != null; slot = slot.rest()) {
                StrongSlot foundSlot = slot.find(key);
                if (foundSlot != null) {
                    this.hash[index] = this.hash[index].remove(foundSlot);
                    this.hashEntries--;
                    return;
                }
            }
        }
    }

    private int countHashKeys() {
        int keys = 0;
        for (int i = 0; i < this.hash.length; i++) {
            for (Slot slot = this.hash[i]; slot != null; slot = slot.rest()) {
                if (slot.first() != null) {
                    keys++;
                }
            }
        }
        return keys;
    }

    private void dropWeakArrayValues() {
        for (int i = 0; i < this.array.length; i++) {
            this.m_metatable.arrayget(this.array, i);
        }
    }

    private int countIntKeys(int[] nums) {
        int i;
        int total = 0;
        int bit = 0;
        for (int i2 = 1; bit < 31 && i2 <= this.array.length; i2 = i) {
            int j = Math.min(this.array.length, 1 << bit);
            int c = 0;
            i = i2;
            while (i <= j) {
                int i3 = i + 1;
                if (this.array[i - 1] != null) {
                    c++;
                    i = i3;
                } else {
                    i = i3;
                }
            }
            nums[bit] = c;
            total += c;
            bit++;
        }
        for (int i4 = 0; i4 < this.hash.length; i4++) {
            for (Slot s = this.hash[i4]; s != null; s = s.rest()) {
                int k = s.arraykey(Integer.MAX_VALUE);
                if (k > 0) {
                    int iLog2 = log2(k);
                    nums[iLog2] = nums[iLog2] + 1;
                    total++;
                }
            }
        }
        return total;
    }

    static int log2(int x) {
        int lg = 0;
        int x2 = x - 1;
        if (x2 < 0) {
            return AddressItem.FLAG_FUZZY;
        }
        if (((-65536) & x2) != 0) {
            lg = 16;
            x2 >>>= 16;
        }
        if ((65280 & x2) != 0) {
            lg += 8;
            x2 >>>= 8;
        }
        if ((x2 & 240) != 0) {
            lg += 4;
            x2 >>>= 4;
        }
        switch (x2) {
            case 0:
                return 0;
            case 1:
                lg++;
                break;
            case 2:
            case 3:
                lg += 2;
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                lg += 3;
                break;
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                lg += 4;
                break;
        }
        return lg;
    }

    private void rehash(int newKey) {
        LuaValue[] newArray;
        int newHashMask;
        Slot[] newHash;
        Slot newEntry;
        int newCapacity;
        if (this.m_metatable != null && (this.m_metatable.useWeakKeys() || this.m_metatable.useWeakValues())) {
            this.hashEntries = countHashKeys();
            if (this.m_metatable.useWeakValues()) {
                dropWeakArrayValues();
            }
        }
        int[] nums = new int[32];
        int total = countIntKeys(nums);
        if (newKey > 0) {
            total++;
            int iLog2 = log2(newKey);
            nums[iLog2] = nums[iLog2] + 1;
        }
        int keys = nums[0];
        int newArraySize = 0;
        for (int log = 1; log < 32; log++) {
            keys += nums[log];
            if (total * 2 < (1 << log)) {
                break;
            }
            if (keys >= (1 << (log - 1))) {
                newArraySize = 1 << log;
            }
        }
        LuaValue[] oldArray = this.array;
        Slot[] oldHash = this.hash;
        int movingToArray = 0;
        if (newKey > 0 && newKey <= newArraySize) {
            movingToArray = 0 - 1;
        }
        if (newArraySize != oldArray.length) {
            newArray = new LuaValue[newArraySize];
            if (newArraySize > oldArray.length) {
                int j = log2(newArraySize) + 1;
                for (int i = log2(oldArray.length + 1); i < j; i++) {
                    movingToArray += nums[i];
                }
            } else if (oldArray.length > newArraySize) {
                int j2 = log2(oldArray.length) + 1;
                for (int i2 = log2(newArraySize + 1); i2 < j2; i2++) {
                    movingToArray -= nums[i2];
                }
            }
            System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newArraySize));
        } else {
            newArray = this.array;
        }
        int newHashSize = (this.hashEntries - movingToArray) + ((newKey < 0 || newKey > newArraySize) ? 1 : 0);
        if (newHashSize > 0) {
            if (newHashSize < 2) {
                newCapacity = 2;
            } else {
                newCapacity = 1 << log2(newHashSize);
            }
            newHashMask = newCapacity - 1;
            newHash = new Slot[newCapacity];
        } else {
            newHashMask = 0;
            newHash = NOBUCKETS;
        }
        for (Slot slot : oldHash) {
            for (; slot != null; slot = slot.rest()) {
                int k = slot.arraykey(newArraySize);
                if (k > 0) {
                    StrongSlot entry = slot.first();
                    if (entry != null) {
                        newArray[k - 1] = entry.value();
                    }
                } else if (!(slot instanceof DeadSlot)) {
                    int j3 = slot.keyindex(newHashMask);
                    newHash[j3] = slot.relink(newHash[j3]);
                }
            }
        }
        int i3 = newArraySize;
        while (i3 < oldArray.length) {
            int i4 = i3 + 1;
            LuaValue v = oldArray[i3];
            if (v != null) {
                int slot2 = hashmod(LuaLong.hashCode(i4), newHashMask);
                if (this.m_metatable != null) {
                    newEntry = this.m_metatable.entry(valueOf(i4), v);
                    if (newEntry == null) {
                        i3 = i4;
                    }
                } else {
                    newEntry = defaultEntry(valueOf(i4), v);
                }
                if (newHash[slot2] != null) {
                    newEntry = newHash[slot2].add(newEntry);
                }
                newHash[slot2] = newEntry;
            }
            i3 = i4;
        }
        this.hash = newHash;
        this.array = newArray;
        this.hashEntries -= movingToArray;
    }

    @Override // luaj.Metatable
    public Slot entry(LuaValue key, LuaValue value) {
        return defaultEntry(key, value);
    }

    protected static boolean isLargeKey(LuaValue key) {
        switch (key.type()) {
            case 4:
                if (key.rawlen() <= 32) {
                }
                break;
        }
        return true;
    }

    public void sort(LuaValue comparator) {
        int len = length();
        if (len >= 16777215) {
            throw new LuaError("array too big: " + len + " >= " + MAX_LEN);
        }
        if (this.m_metatable != null && this.m_metatable.useWeakValues()) {
            dropWeakArrayValues();
        }
        if (len > 1) {
            if (comparator.isnil()) {
                comparator = null;
            }
            heapSort(len, comparator);
        }
    }

    private void heapSort(int count, LuaValue cmpfunc) {
        heapify(count, cmpfunc);
        int end = count;
        while (end > 1) {
            LuaValue a = get(end);
            set(end, get(1));
            set(1, a);
            end--;
            siftDown(1, end, cmpfunc);
        }
    }

    private void heapify(int count, LuaValue cmpfunc) {
        for (int start = count / 2; start > 0; start--) {
            siftDown(start, count, cmpfunc);
        }
    }

    private void siftDown(int start, int end, LuaValue cmpfunc) {
        int child;
        for (int root = start; root * 2 <= end; root = child) {
            child = root * 2;
            if (child < end && compare(child, child + 1, cmpfunc)) {
                child++;
            }
            if (compare(root, child, cmpfunc)) {
                LuaValue a = get(root);
                set(root, get(child));
                set(child, a);
            } else {
                return;
            }
        }
    }

    private boolean compare(int i, int j, LuaValue cmpfunc) {
        LuaValue a = get(i);
        LuaValue b = get(j);
        if (a == null || b == null) {
            return false;
        }
        if (cmpfunc != null) {
            return cmpfunc.call(a, b).toboolean();
        }
        return a.lt_b(b);
    }

    public LuaValue[] keys() {
        ArrayList<LuaValue> l = new ArrayList<>();
        Iterator it = iterator();
        while (it.next()) {
            l.add(it.key());
        }
        LuaValue[] a = new LuaValue[l.size()];
        l.toArray(a);
        return a;
    }

    @Override // luaj.LuaValue
    public boolean eq_b(LuaValue val) {
        if (this == val) {
            return true;
        }
        if (val.istable()) {
            return eqmtcall(this, val);
        }
        return false;
    }

    public Varargs unpack(long i, long j) {
        if (j < i) {
            return NONE;
        }
        long count = j - i;
        if (count < 0) {
            throw new LuaError("too many results to unpack: greater 9223372036854775807");
        }
        if (count >= 16777215) {
            throw new LuaError("too many results to unpack: " + count + " (max is 16777215)");
        }
        int n = (int) (1 + count);
        boolean fitInt = ((long) ((int) i)) == i && ((long) ((int) j)) == j;
        if (n <= 0) {
            return NONE;
        }
        if (n == 1) {
            return fitInt ? get((int) i) : get(LuaLong.valueOf(i));
        }
        if (n == 2) {
            return varargsOf(fitInt ? get((int) i) : get(LuaLong.valueOf(i)), fitInt ? get((int) (1 + i)) : get(LuaLong.valueOf(1 + i)));
        }
        try {
            LuaValue[] v = new LuaValue[n];
            while (true) {
                n--;
                if (n >= 0) {
                    v[n] = fitInt ? get((int) (((long) n) + i)) : get(LuaLong.valueOf(((long) n) + i));
                } else {
                    return varargsOf(v);
                }
            }
        } catch (OutOfMemoryError e) {
            Log.e("OOM on unpack: " + n, e);
            throw new LuaError("too many results to unpack [out of memory]: " + n);
        }
    }

    private static class LinkSlot implements StrongSlot {
        private Entry entry;
        private Slot next;

        LinkSlot(Entry entry, Slot next) {
            this.entry = entry;
            this.next = next;
        }

        @Override // luaj.LuaTable.StrongSlot
        public LuaValue key() {
            return this.entry.key();
        }

        @Override // luaj.LuaTable.Slot
        public int keyindex(int hashMask) {
            return this.entry.keyindex(hashMask);
        }

        @Override // luaj.LuaTable.StrongSlot
        public LuaValue value() {
            return this.entry.value();
        }

        @Override // luaj.LuaTable.StrongSlot
        public Varargs toVarargs() {
            return this.entry.toVarargs();
        }

        @Override // luaj.LuaTable.Slot
        public StrongSlot first() {
            return this.entry;
        }

        @Override // luaj.LuaTable.Slot
        public StrongSlot find(LuaValue key) {
            if (this.entry.keyeq(key)) {
                return this;
            }
            return null;
        }

        @Override // luaj.LuaTable.Slot
        public boolean keyeq(LuaValue key) {
            return this.entry.keyeq(key);
        }

        @Override // luaj.LuaTable.Slot
        public Slot rest() {
            return this.next;
        }

        @Override // luaj.LuaTable.Slot
        public int arraykey(int max) {
            return this.entry.arraykey(max);
        }

        @Override // luaj.LuaTable.Slot
        public Slot set(StrongSlot target, LuaValue value) {
            if (target != this) {
                return setnext(this.next.set(target, value));
            }
            this.entry = this.entry.set(value);
            return this;
        }

        @Override // luaj.LuaTable.Slot
        public Slot add(Slot entry) {
            return setnext(this.next.add(entry));
        }

        @Override // luaj.LuaTable.Slot
        public Slot remove(StrongSlot target) {
            if (this == target) {
                return new DeadSlot(key(), this.next, null);
            }
            this.next = this.next.remove(target);
            return this;
        }

        @Override // luaj.LuaTable.Slot
        public Slot relink(Slot rest) {
            return rest != null ? new LinkSlot(this.entry, rest) : this.entry;
        }

        private Slot setnext(Slot next) {
            if (next == null) {
                return this.entry;
            }
            this.next = next;
            return this;
        }

        public String toString() {
            return this.entry + ListManager.TEXT_SEPARATOR + this.next;
        }
    }

    static abstract class Entry extends Varargs implements StrongSlot {
        @Override // luaj.LuaTable.StrongSlot
        public abstract LuaValue key();

        @Override // luaj.LuaTable.Slot
        public abstract boolean keyeq(LuaValue luaValue);

        @Override // luaj.LuaTable.Slot
        public abstract int keyindex(int i);

        abstract Entry set(LuaValue luaValue);

        public abstract LuaValue value();

        Entry() {
        }

        @Override // luaj.LuaTable.Slot
        public int arraykey(int max) {
            return 0;
        }

        @Override // luaj.Varargs
        public LuaValue arg(int i) {
            switch (i) {
                case 1:
                    return key();
                case 2:
                    return value();
                default:
                    return LuaTable.NIL;
            }
        }

        @Override // luaj.Varargs
        public int narg() {
            return 2;
        }

        @Override // luaj.LuaTable.StrongSlot
        public Varargs toVarargs() {
            return this;
        }

        @Override // luaj.Varargs
        public LuaValue arg1() {
            return key();
        }

        @Override // luaj.Varargs
        public Varargs subargs(int start) {
            switch (start) {
                case 1:
                    return this;
                case 2:
                    return value();
                default:
                    return LuaTable.NONE;
            }
        }

        @Override // luaj.LuaTable.Slot
        public StrongSlot first() {
            return this;
        }

        @Override // luaj.LuaTable.Slot
        public Slot rest() {
            return null;
        }

        @Override // luaj.LuaTable.Slot
        public StrongSlot find(LuaValue key) {
            if (keyeq(key)) {
                return this;
            }
            return null;
        }

        @Override // luaj.LuaTable.Slot
        public Slot set(StrongSlot target, LuaValue value) {
            return set(value);
        }

        @Override // luaj.LuaTable.Slot
        public Slot add(Slot entry) {
            return new LinkSlot(this, entry);
        }

        @Override // luaj.LuaTable.Slot
        public Slot remove(StrongSlot target) {
            DeadSlot deadSlot = null;
            return new DeadSlot(key(), deadSlot, deadSlot);
        }

        @Override // luaj.LuaTable.Slot
        public Slot relink(Slot rest) {
            return rest != null ? new LinkSlot(this, rest) : this;
        }
    }

    private static class IntKeyEntry extends Entry {
        private final int key;
        private LuaValue value;

        IntKeyEntry(int key, LuaValue value) {
            this.key = key;
            this.value = value;
        }

        @Override // luaj.LuaTable.Entry, luaj.LuaTable.StrongSlot
        public LuaValue key() {
            return LuaTable.valueOf(this.key);
        }

        @Override // luaj.LuaTable.Entry, luaj.LuaTable.Slot
        public int arraykey(int max) {
            if (this.key < 1 || this.key > max) {
                return 0;
            }
            return this.key;
        }

        @Override // luaj.LuaTable.Entry, luaj.LuaTable.StrongSlot
        public LuaValue value() {
            return this.value;
        }

        @Override // luaj.LuaTable.Entry
        public Entry set(LuaValue value) {
            this.value = value;
            return this;
        }

        @Override // luaj.LuaTable.Entry, luaj.LuaTable.Slot
        public int keyindex(int mask) {
            return LuaTable.hashmod(LuaLong.hashCode(this.key), mask);
        }

        @Override // luaj.LuaTable.Entry, luaj.LuaTable.Slot
        public boolean keyeq(LuaValue key) {
            return key.isnumber() && this.key == key.toint();
        }
    }

    private static abstract class ValueEntry extends Entry {
        protected final LuaValue key;

        ValueEntry(LuaValue key) {
            this.key = key;
        }

        @Override // luaj.LuaTable.Entry, luaj.LuaTable.StrongSlot
        public final LuaValue key() {
            return this.key;
        }

        @Override // luaj.LuaTable.Entry, luaj.LuaTable.Slot
        public final int keyindex(int mask) {
            return LuaTable.hashSlot(this.key, mask);
        }

        @Override // luaj.LuaTable.Entry, luaj.LuaTable.Slot
        public final boolean keyeq(LuaValue key) {
            return key.raweq(this.key);
        }
    }

    public void unsafehashset(int index, LuaString key, boolean value) {
        Slot[] hash = this.hash;
        Slot entry = new BooleanValueEntry(key, value);
        if (hash[index] != null) {
            entry = hash[index].add(entry);
        }
        hash[index] = entry;
        this.hashEntries++;
    }

    public void unsafehashset(int index, LuaString key, int value) {
        Slot[] hash = this.hash;
        Slot entry = new IntValueEntry(key, value);
        if (hash[index] != null) {
            entry = hash[index].add(entry);
        }
        hash[index] = entry;
        this.hashEntries++;
    }

    public void unsafehashset(int index, LuaString key, long value) {
        Slot[] hash = this.hash;
        int i = (int) value;
        Slot entry = ((long) i) == value ? new IntValueEntry(key, i) : new LongValueEntry(key, value);
        if (hash[index] != null) {
            entry = hash[index].add(entry);
        }
        hash[index] = entry;
        this.hashEntries++;
    }

    public void unsafehashset(int index, LuaString key, float value) {
        Slot[] hash = this.hash;
        Slot entry = new FloatValueEntry(key, value);
        if (hash[index] != null) {
            entry = hash[index].add(entry);
        }
        hash[index] = entry;
        this.hashEntries++;
    }

    public void unsafehashset(int index, LuaString key, double value) {
        Slot[] hash = this.hash;
        float f = (float) value;
        Slot entry = ((double) f) == value ? new FloatValueEntry(key, f) : new DoubleValueEntry(key, value);
        if (hash[index] != null) {
            entry = hash[index].add(entry);
        }
        hash[index] = entry;
        this.hashEntries++;
    }

    protected static Entry defaultEntry(LuaValue key, LuaValue value) {
        if (key.isintnumber()) {
            return new IntKeyEntry(key.toint(), value);
        }
        if (value instanceof LuaBoolean) {
            return new BooleanValueEntry(key, value.toboolean());
        }
        if (value instanceof LuaLong) {
            long v = value.tolong();
            int i = (int) v;
            return ((long) i) == v ? new IntValueEntry(key, i) : new LongValueEntry(key, v);
        }
        if (value instanceof LuaDouble) {
            double v2 = value.todouble();
            float f = (float) v2;
            return ((double) f) == v2 ? new FloatValueEntry(key, f) : new DoubleValueEntry(key, v2);
        }
        return new NormalEntry(key, value);
    }

    static class NormalEntry extends ValueEntry {
        private LuaValue value;

        NormalEntry(LuaValue key, LuaValue value) {
            super(key);
            this.value = value;
        }

        @Override // luaj.LuaTable.Entry, luaj.LuaTable.StrongSlot
        public LuaValue value() {
            return this.value;
        }

        @Override // luaj.LuaTable.Entry
        public Entry set(LuaValue value) {
            if (value instanceof LuaBoolean) {
                return new BooleanValueEntry(this.key, value.toboolean());
            }
            if (value instanceof LuaLong) {
                long v = value.tolong();
                int i = (int) v;
                return ((long) i) == v ? new IntValueEntry(this.key, i) : new LongValueEntry(this.key, v);
            }
            if (value instanceof LuaDouble) {
                double v2 = value.todouble();
                float f = (float) v2;
                return ((double) f) == v2 ? new FloatValueEntry(this.key, f) : new DoubleValueEntry(this.key, v2);
            }
            this.value = value;
            return this;
        }

        @Override // luaj.LuaTable.Entry, luaj.LuaTable.StrongSlot
        public Varargs toVarargs() {
            return this;
        }
    }

    private static class BooleanValueEntry extends ValueEntry {
        private boolean value;

        BooleanValueEntry(LuaValue key, boolean value) {
            super(key);
            this.value = value;
        }

        @Override // luaj.LuaTable.Entry, luaj.LuaTable.StrongSlot
        public LuaValue value() {
            return LuaTable.valueOf(this.value);
        }

        @Override // luaj.LuaTable.Entry
        public Entry set(LuaValue value) {
            if (value instanceof LuaBoolean) {
                this.value = value == LuaValue.TRUE;
                return this;
            }
            if (value instanceof LuaLong) {
                long v = value.tolong();
                int i = (int) v;
                return ((long) i) == v ? new IntValueEntry(this.key, i) : new LongValueEntry(this.key, v);
            }
            if (value instanceof LuaDouble) {
                double v2 = value.todouble();
                float f = (float) v2;
                return ((double) f) == v2 ? new FloatValueEntry(this.key, f) : new DoubleValueEntry(this.key, v2);
            }
            return new NormalEntry(this.key, value);
        }
    }

    private static class FloatValueEntry extends ValueEntry {
        private float value;

        FloatValueEntry(LuaValue key, float value) {
            super(key);
            this.value = value;
        }

        @Override // luaj.LuaTable.Entry, luaj.LuaTable.StrongSlot
        public LuaValue value() {
            return LuaTable.valueOf(this.value);
        }

        @Override // luaj.LuaTable.Entry
        public Entry set(LuaValue value) {
            if (value instanceof LuaBoolean) {
                return new BooleanValueEntry(this.key, value.toboolean());
            }
            LuaValue n = value.tonumber();
            if (n.isnil()) {
                return new NormalEntry(this.key, value);
            }
            if (n instanceof LuaLong) {
                long v = n.tolong();
                int i = (int) v;
                return ((long) i) == v ? new IntValueEntry(this.key, i) : new LongValueEntry(this.key, v);
            }
            double v2 = n.todouble();
            float f = (float) v2;
            if (f == v2) {
                this.value = f;
                return this;
            }
            return new DoubleValueEntry(this.key, v2);
        }
    }

    private static class DoubleValueEntry extends ValueEntry {
        private double value;

        DoubleValueEntry(LuaValue key, double value) {
            super(key);
            this.value = value;
        }

        @Override // luaj.LuaTable.Entry, luaj.LuaTable.StrongSlot
        public LuaValue value() {
            return LuaTable.valueOf(this.value);
        }

        @Override // luaj.LuaTable.Entry
        public Entry set(LuaValue value) {
            if (value instanceof LuaBoolean) {
                return new BooleanValueEntry(this.key, value.toboolean());
            }
            LuaValue n = value.tonumber();
            if (n.isnil()) {
                return new NormalEntry(this.key, value);
            }
            if (n instanceof LuaLong) {
                long v = n.tolong();
                int i = (int) v;
                return ((long) i) == v ? new IntValueEntry(this.key, i) : new LongValueEntry(this.key, v);
            }
            double v2 = n.todouble();
            float f = (float) v2;
            if (f == v2) {
                return new FloatValueEntry(this.key, f);
            }
            this.value = v2;
            return this;
        }
    }

    private static class IntValueEntry extends ValueEntry {
        private int value;

        IntValueEntry(LuaValue key, int value) {
            super(key);
            this.value = value;
        }

        @Override // luaj.LuaTable.Entry, luaj.LuaTable.StrongSlot
        public LuaValue value() {
            return LuaTable.valueOf(this.value);
        }

        int getValue() {
            return this.value;
        }

        @Override // luaj.LuaTable.Entry
        public Entry set(LuaValue value) {
            if (value instanceof LuaBoolean) {
                return new BooleanValueEntry(this.key, value.toboolean());
            }
            LuaValue n = value.tonumber();
            if (n.isnil()) {
                return new NormalEntry(this.key, value);
            }
            if (n instanceof LuaLong) {
                long v = n.tolong();
                int i = (int) v;
                if (i == v) {
                    this.value = i;
                    return this;
                }
                return new LongValueEntry(this.key, v);
            }
            double v2 = n.todouble();
            float f = (float) v2;
            return ((double) f) == v2 ? new FloatValueEntry(this.key, f) : new DoubleValueEntry(this.key, v2);
        }
    }

    private static class LongValueEntry extends ValueEntry {
        private long value;

        LongValueEntry(LuaValue key, long value) {
            super(key);
            this.value = value;
        }

        @Override // luaj.LuaTable.Entry, luaj.LuaTable.StrongSlot
        public LuaValue value() {
            return LuaTable.valueOf(this.value);
        }

        long getValue() {
            return this.value;
        }

        @Override // luaj.LuaTable.Entry
        public Entry set(LuaValue value) {
            if (value instanceof LuaBoolean) {
                return new BooleanValueEntry(this.key, value.toboolean());
            }
            LuaValue n = value.tonumber();
            if (n.isnil()) {
                return new NormalEntry(this.key, value);
            }
            if (n instanceof LuaLong) {
                long v = n.tolong();
                int i = (int) v;
                if (i == v) {
                    return new IntValueEntry(this.key, i);
                }
                this.value = v;
                return this;
            }
            double v2 = n.todouble();
            float f = (float) v2;
            return ((double) f) == v2 ? new FloatValueEntry(this.key, f) : new DoubleValueEntry(this.key, v2);
        }
    }

    private static class DeadSlot implements Slot {
        private final Object key;
        private Slot next;

        private DeadSlot(LuaValue luaValue, Slot slot) {
            this.key = LuaTable.isLargeKey(luaValue) ? new WeakReference(luaValue) : luaValue;
            this.next = slot;
        }

        /* synthetic */ DeadSlot(LuaValue luaValue, Slot slot, DeadSlot deadSlot) {
            this(luaValue, slot);
        }

        private LuaValue key() {
            return (LuaValue) (this.key instanceof WeakReference ? ((WeakReference) this.key).get() : this.key);
        }

        @Override // luaj.LuaTable.Slot
        public int keyindex(int hashMask) {
            return 0;
        }

        @Override // luaj.LuaTable.Slot
        public StrongSlot first() {
            return null;
        }

        @Override // luaj.LuaTable.Slot
        public StrongSlot find(LuaValue key) {
            return null;
        }

        @Override // luaj.LuaTable.Slot
        public boolean keyeq(LuaValue key) {
            LuaValue k = key();
            return k != null && key.raweq(k);
        }

        @Override // luaj.LuaTable.Slot
        public Slot rest() {
            return this.next;
        }

        @Override // luaj.LuaTable.Slot
        public int arraykey(int max) {
            return -1;
        }

        @Override // luaj.LuaTable.Slot
        public Slot set(StrongSlot target, LuaValue value) {
            Slot next = this.next != null ? this.next.set(target, value) : null;
            if (key() == null) {
                return next;
            }
            this.next = next;
            return this;
        }

        @Override // luaj.LuaTable.Slot
        public Slot add(Slot newEntry) {
            return this.next != null ? this.next.add(newEntry) : newEntry;
        }

        @Override // luaj.LuaTable.Slot
        public Slot remove(StrongSlot target) {
            if (key() == null) {
                return this.next;
            }
            this.next = this.next.remove(target);
            return this;
        }

        @Override // luaj.LuaTable.Slot
        public Slot relink(Slot rest) {
            return rest;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("<dead");
            LuaValue k = key();
            if (k != null) {
                buf.append(": ");
                buf.append(k.toString());
            }
            buf.append('>');
            if (this.next != null) {
                buf.append(ListManager.TEXT_SEPARATOR);
                buf.append(this.next.toString());
            }
            return buf.toString();
        }
    }

    @Override // luaj.Metatable
    public boolean useWeakKeys() {
        return false;
    }

    @Override // luaj.Metatable
    public boolean useWeakValues() {
        return false;
    }

    @Override // luaj.Metatable
    public LuaValue toLuaValue() {
        return this;
    }

    @Override // luaj.Metatable
    public LuaValue wrap(LuaValue value) {
        return value;
    }

    @Override // luaj.Metatable
    public LuaValue arrayget(LuaValue[] array, int index) {
        return array[index];
    }

    @Override // luaj.LuaValue, luaj.Varargs
    public String tojstring() {
        try {
            return tojstring(new StringBuilder(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String tojstring(Appendable out, Map<LuaValue, LuaValue> allow) throws IOException {
        Set<LuaTable> d = dumped;
        if (d == null) {
            Set<LuaTable> d2 = new HashSet<>();
            dumped = d2;
            totalCnt = 0;
            String ret = dump("", out, allow).toString();
            totalCnt = 0;
            dumped = null;
            return ret;
        }
        String ret2 = dump("", out, allow).toString();
        return ret2;
    }

    private Appendable dump(String level, Appendable out, Map<LuaValue, LuaValue> allow) throws IOException {
        Set<LuaTable> d = dumped;
        if (d != null) {
            d.add(this);
        }
        out.append("{ -- ");
        out.append(typenamemt());
        out.append("(");
        out.append(Integer.toHexString(hashCode()));
        out.append(")\n");
        int len = length();
        int totCnt = totalCnt + len;
        totalCnt = totCnt;
        List<String> list = null;
        int digits = len == 0 ? 1 : (int) Math.max(1.0d, Math.min(10.0d, Math.log10(Math.abs(len)) + 1.0d));
        String nextLevel = null;
        Iterator it = iterator();
        boolean useLevel = level.length() > 0;
        boolean allowLevel = level.length() == 1;
        int intKey = 1;
        boolean useOut = true;
        boolean beSlow = totCnt < 10000;
        while (it.next()) {
            LuaValue key = it.key();
            if (!allowLevel || allow == null || allow.get(key) != null) {
                if (beSlow && useOut) {
                    if (key.isintnumber()) {
                        int intKey2 = intKey + 1;
                        if (key.toint() != intKey) {
                            intKey = intKey2;
                            useOut = false;
                        } else {
                            intKey = intKey2;
                        }
                    } else {
                        useOut = false;
                    }
                }
                if (!key.isDeprecated()) {
                    LuaValue value = it.value();
                    if (!value.isDeprecated()) {
                        Appendable val = useOut ? out : new StringBuilder();
                        if (useLevel) {
                            val.append(level);
                        }
                        val.append("\t[");
                        String strKey = null;
                        if (key.isintnumber()) {
                            ToolsLight.prefix(val, digits, ' ', Integer.toString(key.toint()));
                        } else if (d != null && (key instanceof LuaTable)) {
                            LuaTable t = (LuaTable) key;
                            if (d.contains(t)) {
                                val.append("{ -- table(");
                                val.append(Integer.toHexString(t.hashCode()));
                                val.append(")\n");
                                if (useLevel) {
                                    val.append(level);
                                }
                                val.append("\t\t-- *** RECURSION ***\n");
                                if (useLevel) {
                                    val.append(level);
                                }
                                val.append("\t}");
                            } else {
                                if (nextLevel == null) {
                                    nextLevel = String.valueOf(level) + '\t';
                                }
                                t.dump(nextLevel, val, allow);
                            }
                        } else if (key instanceof LuaString) {
                            val.append(SearchMenuItem.HEX_UTF_8);
                            strKey = key.tojstring();
                            if (strKey.indexOf(39) >= 0) {
                                strKey = strKey.replace("'", "\\'");
                            }
                            val.append(strKey);
                            val.append(SearchMenuItem.HEX_UTF_8);
                        } else {
                            val.append(key.tojstring());
                        }
                        val.append("] = ");
                        String note = null;
                        if (d != null && (value instanceof LuaTable)) {
                            LuaTable t2 = (LuaTable) value;
                            if (d.contains(t2)) {
                                val.append("{ -- table(");
                                val.append(Integer.toHexString(t2.hashCode()));
                                val.append(")\n");
                                if (useLevel) {
                                    val.append(level);
                                }
                                val.append("\t\t-- *** RECURSION ***\n");
                                if (useLevel) {
                                    val.append(level);
                                }
                                val.append("\t}");
                            } else {
                                if (nextLevel == null) {
                                    nextLevel = String.valueOf(level) + '\t';
                                }
                                t2.dump(nextLevel, val, allow);
                            }
                            val.append(',');
                        } else if (value instanceof LuaString) {
                            val.append(SearchMenuItem.HEX_UTF_8);
                            String strVal = value.tojstring();
                            if (strVal.indexOf(39) >= 0) {
                                strVal = strVal.replace("'", "\\'");
                            }
                            val.append(strVal);
                            val.append("',");
                        } else if (value instanceof LuaLong) {
                            boolean isStrKey = strKey != null && (key instanceof LuaString);
                            if (isStrKey && strKey.equals("address")) {
                                val.append("0x");
                                val.append(Long.toHexString(value.tolong()));
                            } else {
                                val.append(value.tojstring());
                                if (isStrKey) {
                                    if (strKey.equals("flags")) {
                                        switch (value.toint()) {
                                            case 1:
                                                note = "gg.TYPE_BYTE";
                                                break;
                                            case 2:
                                                note = "gg.TYPE_WORD";
                                                break;
                                            case 4:
                                                note = "gg.TYPE_DWORD";
                                                break;
                                            case 8:
                                                note = "gg.TYPE_XOR";
                                                break;
                                            case 16:
                                                note = "gg.TYPE_FLOAT";
                                                break;
                                            case 32:
                                                note = "gg.TYPE_QWORD";
                                                break;
                                            case 64:
                                                note = "gg.TYPE_DOUBLE";
                                                break;
                                        }
                                    } else if (strKey.equals("freezeType")) {
                                        switch (value.toint()) {
                                            case 0:
                                                note = "gg.FREEZE_NORMAL";
                                                break;
                                            case 1:
                                                note = "gg.FREEZE_MAY_INCREASE";
                                                break;
                                            case 2:
                                                note = "gg.FREEZE_MAY_DECREASE";
                                                break;
                                            case 3:
                                                note = "gg.FREEZE_IN_RANGE";
                                                break;
                                        }
                                    }
                                }
                            }
                            val.append(',');
                        } else {
                            val.append(value.tojstring());
                            if (!(value instanceof Script.ApiFunction)) {
                                val.append(',');
                            }
                        }
                        if (note != null) {
                            val.append(" -- ");
                            val.append(note);
                        }
                        val.append('\n');
                        if (!useOut) {
                            if (list == null) {
                                int capacity = len - (intKey - 1);
                                list = capacity < 0 ? new ArrayList<>() : new ArrayList<>(capacity);
                            }
                            list.add(val.toString());
                        }
                    }
                }
            }
        }
        if (list != null) {
            try {
                Collections.sort(list);
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
            for (String line : list) {
                out.append(line);
            }
        }
        if (useLevel) {
            out.append(level);
        }
        out.append('}');
        return out;
    }

    public int getHashEntries() {
        return this.hashEntries;
    }
}
