package android.ext;

import android.ext.ArmDis;
import android.ext.ParserNumbers;
import android.fix.SparseArray;
import android.util.SparseIntArray;
import catch_.me_.if_.you_.can_.R;
import lasm.LasmConstants;
import luaj.LuaTable;
import luaj.compiler.Constants;

/* JADX INFO: loaded from: classes.dex */
public class AddressItem {
    public static final int FLAG_ALL_OTHERS = -128;
    public static final int FLAG_ALTER_ADD = 1073741824;
    public static final int FLAG_AUTO = 127;
    public static final int FLAG_FUZZY = Integer.MIN_VALUE;
    public static final int FLAG_MODE_HACKING = 33554432;
    public static final int FLAG_MODE_XOR = 2097152;
    public static final int FLAG_ORDERED = 4194304;
    public static final int FLAG_RANGE = 8388608;
    public static final int FLAG_REVERT = 16777216;
    public static final int FLAG_SIGN = 1006632960;
    public static final int FLAG_VALUE_EQUAL = 536870912;
    public static final int FLAG_VALUE_GREATER_OR_EQUAL = 67108864;
    public static final int FLAG_VALUE_LESS_OR_EQUAL = 134217728;
    public static final int FLAG_VALUE_NOT_EQUAL = 268435456;
    public static final int TYPE_BYTE = 1;
    public static final int TYPE_COUNT = 7;
    public static final int TYPE_DOUBLE = 64;
    public static final int TYPE_DWORD = 4;
    public static final int TYPE_FLOAT = 16;
    public static final int TYPE_NONE = 0;
    public static final int TYPE_QWORD = 32;
    public static final int TYPE_WORD = 2;
    public static final int TYPE_XOR = 8;
    public static final int USED_TYPE_COUNT = 128;
    private static SparseArray<CharSequence> names;
    private static SparseArray<CharSequence> namesShort;
    private static final int[] order;
    private static SparseArray<CharSequence> signNames;
    private static SparseArray<CharSequence> signNamesSmall;
    public long address;
    public long data;
    public int flags;
    public static final long[] DATA_MASK = {0, 255, 65535, 16777215, ParserNumbers.MAX_UINT, 1099511627775L, 281474976710655L, 72057594037927935L, -1};
    private static SparseIntArray colors = null;
    private static int colorDefault = LuaTable.MAX_LEN;

    public static class Steps {
        double dl;
        float fl;
        long val;
    }

    static {
        names = null;
        namesShort = null;
        signNames = null;
        signNamesSmall = null;
        Object[] ranges = ParserNumbers.getRanges();
        SparseArray<CharSequence> names_ = new SparseArray<>();
        SparseArray<CharSequence> namesShort_ = new SparseArray<>();
        initName(names_, namesShort_, 1, "B: %s (%,d - %,d)", R.string.type_byte, ranges[0], ranges[1]);
        initName(names_, namesShort_, 2, "W: %s (%,d - %,d)", R.string.type_word, ranges[2], ranges[3]);
        initName(names_, namesShort_, 4, "D: %s (%,d - %,d)", R.string.type_dword, ranges[4], ranges[5]);
        initName(names_, namesShort_, 8, "X: %s (%,d - %,d)", R.string.type_xor, ranges[4], ranges[5]);
        initName(names_, namesShort_, 16, "F: %s (%.1e - %.1e)", R.string.type_float, Float.valueOf(-3.4028235E38f), Float.valueOf(Float.MAX_VALUE));
        initName(names_, namesShort_, 32, "Q: %s (%,d - %,d)", R.string.type_qword, ranges[6], ranges[7]);
        initName(names_, namesShort_, 64, "E: %s (%.1e - %.1e)", R.string.type_double, Double.valueOf(-1.7976931348623157E308d), Double.valueOf(Double.MAX_VALUE));
        namesShort = namesShort_;
        names = names_;
        updateLocale();
        SparseArray<CharSequence> signNames_ = new SparseArray<>();
        signNames_.append(FLAG_VALUE_EQUAL, "=");
        signNames_.append(FLAG_VALUE_GREATER_OR_EQUAL, "≥");
        signNames_.append(134217728, "≤");
        signNames_.append(FLAG_VALUE_NOT_EQUAL, "≠");
        signNames = signNames_;
        SparseArray<CharSequence> signNamesSmall_ = new SparseArray<>();
        signNamesSmall_.append(FLAG_VALUE_EQUAL, "=");
        signNamesSmall_.append(FLAG_VALUE_NOT_EQUAL, "≠");
        signNamesSmall = signNamesSmall_;
        AppLocale.registerClass(AddressItem.class);
        order = new int[]{8, 32, 1, 2, 64, 16, 4};
    }

    protected AddressItem() {
    }

    public AddressItem(AddressItem item) {
        this(item.address, item.data, item.flags);
    }

    public AddressItem(long address, long data, int flags) {
        this.address = address;
        this.data = data;
        this.flags = flags;
    }

    public AddressItem copy() {
        return new AddressItem(this);
    }

    private static void initName(SparseArray<CharSequence> names2, SparseArray<CharSequence> namesShort2, int type, String format, int resId, Object from, Object to) {
        String name = Re.s(resId);
        int color = getColor(type);
        String str = Tools.stringFormat(format, name, from, to);
        names2.append(type, Tools.colorize(str, color));
        namesShort2.append(type, Tools.colorize(name, color));
    }

    public static void updateLocale() {
        initName(names, namesShort, FLAG_AUTO, "A: %s (%.1e - %.1e) (" + Re.s(R.string.slow) + ')', R.string.type_auto, Double.valueOf(-1.7976931348623157E308d), Double.valueOf(Double.MAX_VALUE));
    }

    public static int getOrder(int flags) {
        switch (flags) {
            case 1:
                return 50;
            case 2:
                return 40;
            case 4:
                return 10;
            case 8:
                return 70;
            case 16:
                return 20;
            case 32:
                return 60;
            case 64:
                return 30;
            default:
                return Constants.MAXSTACK;
        }
    }

    public static class Pair {
        public int count;
        int flags;
        CharSequence name;

        public Pair(int flags, CharSequence name, int count) {
            this.count = 0;
            this.flags = flags;
            this.name = name;
            this.count = count;
        }

        public String toString() {
            return this.count > 0 ? String.valueOf(this.count) + ": " + this.name.toString() : this.name.toString();
        }
    }

    public static Pair[] getOrderedPairs(SparseArray<CharSequence> types, SparseIntArray counts) {
        int count = types.size();
        Pair[] ret = new Pair[count];
        if (count == 1) {
            int key = types.keyAt(0);
            ret[0] = new Pair(key, types.valueAt(0), counts == null ? 0 : counts.get(key));
        } else {
            boolean[] used = new boolean[count];
            int i = count - 1;
            for (int j = 0; j < count; j++) {
                int key2 = types.keyAt(j);
                if (key2 > -10) {
                    break;
                }
                ret[i] = new Pair(key2, types.valueAt(j), counts == null ? 0 : counts.get(key2));
                used[j] = true;
                i--;
            }
            for (int key3 : order) {
                int index = types.indexOfKey(key3);
                if (index >= 0 && !used[index]) {
                    ret[i] = new Pair(key3, types.valueAt(index), counts == null ? 0 : counts.get(key3));
                    used[index] = true;
                    i--;
                }
            }
            int i2 = 0;
            for (int j2 = 0; j2 < count; j2++) {
                if (!used[j2]) {
                    int key4 = types.keyAt(j2);
                    ret[i2] = new Pair(key4, types.valueAt(j2), counts == null ? 0 : counts.get(key4));
                    i2++;
                }
            }
        }
        return ret;
    }

    public static SparseArray<CharSequence> getSignNames() {
        return signNames;
    }

    public static SparseArray<CharSequence> getSignNamesSmall() {
        return signNamesSmall;
    }

    public SparseArray<CharSequence> getDataForSearch() {
        return getDataForSearch(this.flags, false);
    }

    public static SparseArray<CharSequence> getDataForSearch(int flags) {
        return getDataForSearch(flags, false);
    }

    public SparseArray<CharSequence> getDataForSearch(boolean useAuto) {
        return getDataForSearch(this.flags, useAuto);
    }

    public static SparseArray<CharSequence> getDataForSearch(int flags, boolean useAuto) {
        SparseArray<CharSequence> list = new SparseArray<>();
        int size = names.size();
        for (int i = 0; i < size; i++) {
            int key = names.keyAt(i);
            if ((flags & key) == key) {
                list.append(key, names.valueAt(i));
            }
        }
        if (useAuto && list.get(flags) == null) {
            list.append(flags, names.get(FLAG_AUTO));
        }
        return list;
    }

    public SparseArray<CharSequence> getDataForEdit() {
        return getDataForEdit(this.flags);
    }

    public static SparseArray<CharSequence> getDataForEdit(int flags) {
        SparseArray<CharSequence> list = new SparseArray<>();
        if (flags == 32 || flags == 64) {
            list.append(32, names.get(32));
            list.append(64, names.get(64));
        } else if (flags == 4 || flags == 16 || flags == 8) {
            list.append(4, names.get(4));
            list.append(16, names.get(16));
            list.append(8, names.get(8));
        } else if ((flags & 2) != 0) {
            list.append(flags & 2, names.get(2));
        } else if ((flags & 1) != 0) {
            list.append(flags & 1, names.get(1));
        } else {
            Log.d("Unknown flags: " + flags + ' ' + Integer.toBinaryString(flags));
        }
        return list;
    }

    public SparseArray<CharSequence> getDataForEditAll() {
        return getDataForEditAll(this.flags);
    }

    public static SparseArray<CharSequence> getDataForEditAll(int flags) {
        return getDataForEditAll(flags, 0L);
    }

    public static SparseArray<CharSequence> getDataForEditAll(int flags, long addr) {
        SparseArray<CharSequence> list = getDataForSearch(flags);
        list.remove(FLAG_AUTO);
        if ((addr & 3) != 0) {
            list.remove(32);
            list.remove(64);
        }
        if ((addr & 3) != 0) {
            list.remove(4);
            list.remove(8);
            list.remove(16);
        }
        if ((1 & addr) != 0) {
            list.remove(2);
        }
        return list;
    }

    public static int getTypeForAddress(long addr, boolean single) {
        return getTypeForAddress(addr, single, null);
    }

    public static int getTypeForAddress(long addr, boolean single, SparseIntArray counts) {
        int ret = 0;
        if ((3 & addr) == 0) {
            if (single) {
                return 32;
            }
            int ret2 = 0 | 32;
            ret = ret2 | 64;
            if (counts != null) {
                counts.put(32, counts.get(32) + 1);
                counts.put(64, counts.get(64) + 1);
            }
        }
        if ((3 & addr) == 0) {
            if (single) {
                return 4;
            }
            ret = ret | 4 | 8 | 16;
            if (counts != null) {
                counts.put(4, counts.get(4) + 1);
                counts.put(8, counts.get(8) + 1);
                counts.put(16, counts.get(16) + 1);
            }
        }
        if ((1 & addr) == 0) {
            if (single) {
                return 2;
            }
            ret |= 2;
            if (counts != null) {
                counts.put(2, counts.get(2) + 1);
            }
        }
        int ret3 = ret | 1;
        if (counts != null) {
            counts.put(1, counts.get(1) + 1);
        }
        return ret3;
    }

    public int getSize() {
        return getSize(this.flags);
    }

    public static int getSize(int flags) {
        int flags2 = flags & FLAG_AUTO;
        if ((flags2 & 96) != 0) {
            return 8;
        }
        if ((flags2 & 28) != 0) {
            return 4;
        }
        if ((flags2 & 2) != 0) {
            return 2;
        }
        return (flags2 & 1) != 0 ? 1 : 8;
    }

    public int getAlign() {
        return getAlign(this.flags);
    }

    public static int getAlign(int flags) {
        int flags2 = flags & FLAG_AUTO;
        if ((flags2 & 96) != 0 || (flags2 & 28) != 0) {
            return 4;
        }
        if ((flags2 & 2) != 0) {
            return 2;
        }
        return (flags2 & 1) != 0 ? 1 : 4;
    }

    public static int getPow(int flags) {
        int flags2 = flags & FLAG_AUTO;
        if ((flags2 & 96) != 0) {
            return 3;
        }
        if ((flags2 & 28) != 0) {
            return 2;
        }
        if ((flags2 & 2) != 0) {
            return 1;
        }
        return (flags2 & 1) != 0 ? 0 : 3;
    }

    public long getAddress() {
        return getAddress(this.address, this.flags);
    }

    public static long getAddress(long address, int flags) {
        return address;
    }

    public String getStringAddress() {
        return getStringAddress(this.address, this.flags);
    }

    public static String getStringAddress(long address, int flags) {
        return ToolsLight.prefixLongHex(8, getAddress(address, flags));
    }

    public String getStringDataTrim() {
        return getStringDataTrim(this.address, this.data, this.flags);
    }

    public static String getStringDataTrim(long address, long data, int flags) {
        return getStringData(address, data, flags).trim();
    }

    public String getStringData() {
        return getStringData(this.address, this.data, this.flags);
    }

    public static String getStringData(long address, long data, int flags) {
        String out = null;
        int flags2 = flags & FLAG_AUTO;
        switch (flags2) {
            case 8:
                data ^= address;
                break;
            case 16:
                out = Converter.floatToString(data);
                break;
            case 64:
            case LasmConstants.HEX /* 80 */:
                out = Converter.doubleToString(data);
                break;
        }
        if (out == null) {
            return DisplayNumbers.longToString(data, flags2);
        }
        return out;
    }

    public String getStringRhexData() {
        return getStringHexData(this.address, this.data, this.flags, true);
    }

    public static String getStringRhexData(long address, long data, int flags) {
        return getStringHexData(address, data, flags, true);
    }

    public String getStringHexData() {
        return getStringHexData(this.address, this.data, this.flags, false);
    }

    public static String getStringHexData(long address, long data, int flags) {
        return getStringHexData(address, data, flags, false);
    }

    private static String getStringHexData(long address, long data, int flags, boolean rhex) {
        int flags2 = flags & FLAG_AUTO;
        if (flags2 == 8) {
            data ^= address;
        }
        if (rhex) {
            int size = getSize(flags2);
            data = Long.reverseBytes(data) >> (64 - (size * 8));
            if (size != 8) {
                data &= (1 << (size * 8)) - 1;
            }
        }
        return DisplayNumbers.longToString(data, flags2, true);
    }

    public void setDataFromString(String data) throws NumberFormatException {
        this.data = dataFromString(this.address, data, this.flags);
    }

    public static int fixAutoFlag(int flags, long number, boolean negative) {
        if (flags != 80 && flags != 64) {
            int excludeFlags = 0;
            if (negative) {
                if ((number >> 7) != -1) {
                    excludeFlags = 0 | 1;
                }
                if ((number >> 15) != -1) {
                    excludeFlags |= 2;
                }
                if ((number >> 31) != -1) {
                    excludeFlags = excludeFlags | 4 | 8;
                }
            } else {
                if ((number >> 8) != 0) {
                    excludeFlags = 0 | 1;
                }
                if ((number >> 16) != 0) {
                    excludeFlags |= 2;
                }
                if ((number >> 32) != 0) {
                    excludeFlags = excludeFlags | 4 | 8;
                }
            }
            return flags & (excludeFlags ^ (-1));
        }
        return flags;
    }

    public static long fixValue(long value, int flags) {
        switch (flags & FLAG_AUTO) {
            case 1:
                value = (byte) value;
                break;
            case 2:
                value = (short) value;
                break;
            case 4:
            case 8:
                value = (int) value;
                break;
        }
        if (value != value) {
            Log.d("fixValue[" + flags + ", " + Integer.toHexString(flags) + "]: " + value + '[' + Long.toHexString(value) + "] -> " + value + '[' + Long.toHexString(value) + ']');
        }
        return value;
    }

    public static ParserNumbers.Result parseString(ParserNumbers.Result result, String data, int flags, int nameId, long addr) throws NumberFormatException {
        try {
            return ParserNumbers.parse(result, data, flags, true, addr);
        } catch (NumberFormatException e) {
            String name = Re.s(nameId);
            Log.w("Failed parse '" + data + "' as " + flags + " on '" + name + SearchMenuItem.HEX_UTF_8, e);
            if ((e instanceof ParserNumbers.NumberFormatException_) || (e instanceof ArmDis.AsmFailedException)) {
                throw e;
            }
            NumberFormatException nfe = new NumberFormatException(String.valueOf(name) + ": " + e.getMessage());
            nfe.initCause(e);
            throw nfe;
        }
    }

    public static long dataForSearch(ParserNumbers.Result result, String data, int flags) throws NumberFormatException {
        return parseString(result, data, flags & FLAG_AUTO, R.string.number_name, 0L).value;
    }

    public static long dataFromString(long address, String data, int flags) throws NumberFormatException {
        int flags2 = flags & FLAG_AUTO;
        ParserNumbers.Result out = parseString(null, data, flags2, R.string.number_name, address);
        long result = out.value;
        if (flags2 == 8) {
            return result ^ address;
        }
        return result;
    }

    public Steps dataFromString(Steps steps, ParserNumbers.Result result, String data, long addr, String step, int stepCount) throws NumberFormatException {
        if (result == null) {
            result = parseString(null, data, this.flags, R.string.number_name, addr);
        }
        long value = result.value;
        if (stepCount != 0) {
            if (steps == null) {
                ParserNumbers.Result stepValue = parseString(null, step, this.flags, R.string.step_name, 0L);
                steps = new Steps();
                steps.fl = Float.intBitsToFloat((int) stepValue.value);
                steps.dl = Double.longBitsToDouble(stepValue.value);
                steps.val = stepValue.value;
            }
            if (result.type == 16) {
                value = Float.floatToRawIntBits(Float.intBitsToFloat((int) value) + (steps.fl * stepCount));
            } else if (result.type == 64) {
                value = Double.doubleToRawLongBits(Double.longBitsToDouble(value) + (steps.dl * ((double) stepCount)));
            } else {
                value += steps.val * ((long) stepCount);
            }
        }
        if (this.flags == 8) {
            value ^= this.address;
        }
        this.data = value;
        return steps;
    }

    public CharSequence getShortName() {
        return getShortName(this.flags);
    }

    public static CharSequence getShortName(int flags) {
        return getName(flags & FLAG_AUTO).toString().substring(0, 1).intern();
    }

    public int getType() {
        return getType(this.flags);
    }

    public static int getType(int flags) {
        int flags2 = flags & FLAG_AUTO;
        switch (flags2) {
            case 0:
            case 1:
            case 2:
            case 4:
            case 8:
            case 16:
            case 32:
            case 64:
            case FLAG_AUTO /* 127 */:
                return flags2;
            default:
                return FLAG_AUTO;
        }
    }

    public CharSequence getNameShort() {
        return getNameShort(this.flags);
    }

    public static CharSequence getNameShort(int flags) {
        CharSequence out = namesShort.get(getType(flags));
        if (out == null) {
            return "Unknown";
        }
        return out;
    }

    public CharSequence getName() {
        return getName(this.flags);
    }

    public static CharSequence getName(int flags) {
        CharSequence out = names.get(getType(flags));
        if (out == null) {
            return "Unknown";
        }
        return out;
    }

    public int getColor() {
        return getColor(this.flags);
    }

    public static int getColor(int flags) {
        try {
            SparseIntArray cl = colors;
            if (cl == null) {
                cl = new SparseIntArray(8);
                cl.append(1, Tools.getColor(R.color.type_byte));
                cl.append(2, Tools.getColor(R.color.type_word));
                cl.append(4, Tools.getColor(R.color.type_dword));
                cl.append(8, Tools.getColor(R.color.type_xor));
                cl.append(16, Tools.getColor(R.color.type_float));
                cl.append(32, Tools.getColor(R.color.type_qword));
                cl.append(64, Tools.getColor(R.color.type_double));
                colors = cl;
                colorDefault = Tools.getColor(R.color.type_unknown);
            }
            int out = cl.get(getType(flags));
            if (out == 0) {
                return colorDefault;
            }
            return out;
        } catch (Throwable e) {
            Log.e("Failed getColor for: " + flags, e);
            return colorDefault;
        }
    }

    public static String getLimits(int flags) {
        if (flags == 0 || (flags & 64) != 0) {
            return Tools.stringFormat(Re.s(R.string.type_edit_limit), Tools.stringFormat("%.1e", Double.valueOf(-1.7976931348623157E308d)), Tools.stringFormat("%.1e", Double.valueOf(Double.MAX_VALUE)));
        }
        if ((flags & 16) != 0) {
            return Tools.stringFormat(Re.s(R.string.type_edit_limit), Tools.stringFormat("%.1e", Float.valueOf(-3.4028235E38f)), Tools.stringFormat("%.1e", Float.valueOf(Float.MAX_VALUE)));
        }
        Object[] ranges = ParserNumbers.getRanges();
        int pow = getPow(flags);
        return Tools.stringFormat(Re.s(R.string.type_edit_limit), Tools.stringFormat("%,d", ranges[(pow * 2) + 0]), Tools.stringFormat("%,d", ranges[(pow * 2) + 1]));
    }

    public void alter() {
        alter(0);
    }

    public void alter(int x) {
        MainService.instance.mDaemonManager.addForAlter(this, x);
    }

    public String toString() {
        return String.valueOf(getStringAddress()) + ": " + getStringData() + ' ' + ((Object) getNameShort()) + " (" + this.flags + ')';
    }

    public int hashCode() {
        int result = ((int) (this.address ^ (this.address >>> 32))) + 31;
        return (result * 31) + (this.flags & FLAG_AUTO);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof AddressItem)) {
            AddressItem other = (AddressItem) obj;
            return this.address == other.address && ((this.flags ^ other.flags) & FLAG_AUTO) == 0;
        }
        return false;
    }

    public boolean isPossibleItem() {
        return (this.address & ((long) (getAlign() + (-1)))) == 0;
    }
}
