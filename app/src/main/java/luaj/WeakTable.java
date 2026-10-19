package luaj;

import java.lang.ref.WeakReference;
import luaj.LuaTable;

/* JADX INFO: loaded from: classes.dex */
public class WeakTable implements Metatable {
    private final LuaValue backing;
    private final boolean weakkeys;
    private final boolean weakvalues;

    public static LuaTable make(boolean weakkeys, boolean weakvalues) {
        LuaString mode;
        if (weakkeys && weakvalues) {
            mode = LuaString.valueOf("kv");
        } else if (weakkeys) {
            mode = LuaString.valueOf("k");
        } else if (weakvalues) {
            mode = LuaString.valueOf("v");
        } else {
            return LuaTable.tableOf();
        }
        LuaTable table = LuaTable.tableOf();
        LuaTable mt = LuaTable.tableOf(new LuaValue[]{LuaValue.MODE, mode});
        table.setmetatable(mt);
        return table;
    }

    public WeakTable(boolean weakkeys, boolean weakvalues, LuaValue backing) {
        this.weakkeys = weakkeys;
        this.weakvalues = weakvalues;
        this.backing = backing;
    }

    @Override // luaj.Metatable
    public boolean useWeakKeys() {
        return this.weakkeys;
    }

    @Override // luaj.Metatable
    public boolean useWeakValues() {
        return this.weakvalues;
    }

    @Override // luaj.Metatable
    public LuaValue toLuaValue() {
        return this.backing;
    }

    @Override // luaj.Metatable
    public LuaTable.Slot entry(LuaValue key, LuaValue value) {
        LuaValue value2 = value.strongvalue();
        if (value2 == null) {
            return null;
        }
        if (this.weakkeys && !key.isnumber() && !key.isstring() && !key.isboolean()) {
            if (this.weakvalues && !value2.isnumber() && !value2.isstring() && !value2.isboolean()) {
                return new WeakKeyAndValueSlot(key, value2, null);
            }
            return new WeakKeySlot(key, value2, null);
        }
        if (this.weakvalues && !value2.isnumber() && !value2.isstring() && !value2.isboolean()) {
            return new WeakValueSlot(key, value2, null);
        }
        return LuaTable.defaultEntry(key, value2);
    }

    public static abstract class WeakSlot implements LuaTable.Slot {
        protected Object key;
        protected LuaTable.Slot next;
        protected Object value;

        protected abstract WeakSlot copy(LuaTable.Slot slot);

        @Override // luaj.LuaTable.Slot
        public abstract int keyindex(int i);

        public abstract LuaTable.Slot set(LuaValue luaValue);

        protected WeakSlot(Object key, Object value, LuaTable.Slot next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override // luaj.LuaTable.Slot
        public LuaTable.StrongSlot first() {
            LuaValue key = strongkey();
            LuaValue value = strongvalue();
            if (key != null && value != null) {
                return new LuaTable.NormalEntry(key, value);
            }
            this.key = null;
            this.value = null;
            return null;
        }

        @Override // luaj.LuaTable.Slot
        public LuaTable.StrongSlot find(LuaValue key) {
            LuaTable.StrongSlot first = first();
            if (first != null) {
                return first.find(key);
            }
            return null;
        }

        @Override // luaj.LuaTable.Slot
        public boolean keyeq(LuaValue key) {
            LuaTable.StrongSlot first = first();
            return first != null && first.keyeq(key);
        }

        @Override // luaj.LuaTable.Slot
        public LuaTable.Slot rest() {
            return this.next;
        }

        @Override // luaj.LuaTable.Slot
        public int arraykey(int max) {
            return 0;
        }

        @Override // luaj.LuaTable.Slot
        public LuaTable.Slot set(LuaTable.StrongSlot target, LuaValue value) {
            LuaValue key = strongkey();
            if (key != null && target.find(key) != null) {
                return set(value);
            }
            if (key != null) {
                this.next = this.next.set(target, value);
                return this;
            }
            return this.next.set(target, value);
        }

        @Override // luaj.LuaTable.Slot
        public LuaTable.Slot add(LuaTable.Slot entry) {
            if (this.next != null) {
                entry = this.next.add(entry);
            }
            this.next = entry;
            return (strongkey() == null || strongvalue() == null) ? this.next : this;
        }

        @Override // luaj.LuaTable.Slot
        public LuaTable.Slot remove(LuaTable.StrongSlot target) {
            LuaValue key = strongkey();
            if (key == null) {
                return this.next.remove(target);
            }
            if (target.keyeq(key)) {
                this.value = null;
                return this;
            }
            this.next = this.next.remove(target);
            return this;
        }

        @Override // luaj.LuaTable.Slot
        public LuaTable.Slot relink(LuaTable.Slot rest) {
            if (strongkey() == null || strongvalue() == null) {
                return rest;
            }
            if (rest != null || this.next != null) {
                return copy(rest);
            }
            return this;
        }

        public LuaValue strongkey() {
            return (LuaValue) this.key;
        }

        public LuaValue strongvalue() {
            return (LuaValue) this.value;
        }
    }

    static class WeakKeySlot extends WeakSlot {
        private final int keyhash;

        protected WeakKeySlot(LuaValue key, LuaValue value, LuaTable.Slot next) {
            super(WeakTable.weaken(key), value, next);
            this.keyhash = key.hashCode();
        }

        protected WeakKeySlot(WeakKeySlot copyFrom, LuaTable.Slot next) {
            super(copyFrom.key, copyFrom.value, next);
            this.keyhash = copyFrom.keyhash;
        }

        @Override // luaj.WeakTable.WeakSlot, luaj.LuaTable.Slot
        public int keyindex(int mask) {
            return LuaTable.hashmod(this.keyhash, mask);
        }

        @Override // luaj.WeakTable.WeakSlot
        public LuaTable.Slot set(LuaValue value) {
            this.value = value;
            return this;
        }

        @Override // luaj.WeakTable.WeakSlot
        public LuaValue strongkey() {
            return WeakTable.strengthen(this.key);
        }

        @Override // luaj.WeakTable.WeakSlot
        protected WeakSlot copy(LuaTable.Slot rest) {
            return new WeakKeySlot(this, rest);
        }
    }

    static class WeakValueSlot extends WeakSlot {
        protected WeakValueSlot(LuaValue key, LuaValue value, LuaTable.Slot next) {
            super(key, WeakTable.weaken(value), next);
        }

        protected WeakValueSlot(WeakValueSlot copyFrom, LuaTable.Slot next) {
            super(copyFrom.key, copyFrom.value, next);
        }

        @Override // luaj.WeakTable.WeakSlot, luaj.LuaTable.Slot
        public int keyindex(int mask) {
            return LuaTable.hashSlot(strongkey(), mask);
        }

        @Override // luaj.WeakTable.WeakSlot
        public LuaTable.Slot set(LuaValue value) {
            this.value = WeakTable.weaken(value);
            return this;
        }

        @Override // luaj.WeakTable.WeakSlot
        public LuaValue strongvalue() {
            return WeakTable.strengthen(this.value);
        }

        @Override // luaj.WeakTable.WeakSlot
        protected WeakSlot copy(LuaTable.Slot next) {
            return new WeakValueSlot(this, next);
        }
    }

    static class WeakKeyAndValueSlot extends WeakSlot {
        private final int keyhash;

        protected WeakKeyAndValueSlot(LuaValue key, LuaValue value, LuaTable.Slot next) {
            super(WeakTable.weaken(key), WeakTable.weaken(value), next);
            this.keyhash = key.hashCode();
        }

        protected WeakKeyAndValueSlot(WeakKeyAndValueSlot copyFrom, LuaTable.Slot next) {
            super(copyFrom.key, copyFrom.value, next);
            this.keyhash = copyFrom.keyhash;
        }

        @Override // luaj.WeakTable.WeakSlot, luaj.LuaTable.Slot
        public int keyindex(int hashMask) {
            return LuaTable.hashmod(this.keyhash, hashMask);
        }

        @Override // luaj.WeakTable.WeakSlot
        public LuaTable.Slot set(LuaValue value) {
            this.value = WeakTable.weaken(value);
            return this;
        }

        @Override // luaj.WeakTable.WeakSlot
        public LuaValue strongkey() {
            return WeakTable.strengthen(this.key);
        }

        @Override // luaj.WeakTable.WeakSlot
        public LuaValue strongvalue() {
            return WeakTable.strengthen(this.value);
        }

        @Override // luaj.WeakTable.WeakSlot
        protected WeakSlot copy(LuaTable.Slot next) {
            return new WeakKeyAndValueSlot(this, next);
        }
    }

    protected static LuaValue weaken(LuaValue value) {
        switch (value.type()) {
            case 5:
            case 6:
            case 7:
            case 8:
                return new WeakValue(value);
            default:
                return value;
        }
    }

    protected static LuaValue strengthen(Object ref) {
        if (ref instanceof WeakReference) {
            ref = ((WeakReference) ref).get();
        }
        return ref instanceof WeakValue ? ((WeakValue) ref).strongvalue() : (LuaValue) ref;
    }

    static class WeakValue extends LuaValue {
        WeakReference ref;

        protected WeakValue(LuaValue value) {
            this.ref = new WeakReference(value);
        }

        @Override // luaj.LuaValue
        public int type() {
            illegal("type", "weak value");
            return 0;
        }

        @Override // luaj.LuaValue
        public String typename() {
            illegal("typename", "weak value");
            return null;
        }

        @Override // luaj.LuaValue, luaj.Varargs
        public String toString() {
            return "weak<" + this.ref.get() + '>';
        }

        @Override // luaj.LuaValue
        public LuaValue strongvalue() {
            Object o = this.ref.get();
            return (LuaValue) o;
        }

        @Override // luaj.LuaValue
        public boolean raweq(LuaValue rhs) {
            Object o = this.ref.get();
            return o != null && rhs.raweq((LuaValue) o);
        }
    }

    @Override // luaj.Metatable
    public LuaValue wrap(LuaValue value) {
        return this.weakvalues ? weaken(value) : value;
    }

    @Override // luaj.Metatable
    public LuaValue arrayget(LuaValue[] array, int index) {
        LuaValue value = array[index];
        if (value != null && (value = strengthen(value)) == null) {
            array[index] = null;
        }
        return value;
    }
}
