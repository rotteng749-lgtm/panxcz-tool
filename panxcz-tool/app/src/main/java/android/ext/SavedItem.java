package android.ext;

import android.ext.ParserNumbers;
import android.fix.SparseArray;
import catch_.me_.if_.you_.can_.R;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* JADX INFO: loaded from: classes.dex */
public class SavedItem extends AddressItem {
    public static final byte FREEZE_IN_RANGE = 3;
    public static final byte FREEZE_MAY_DECREASE = 2;
    public static final byte FREEZE_MAY_INCREASE = 1;
    public static final byte FREEZE_NORMAL = 0;
    private static SparseArray<CharSequence> freezeNames;
    public boolean freeze;
    public long freezeFrom;
    public long freezeTo;
    public byte freezeType;
    public String name;

    static {
        freezeNames = null;
        freezeNames = new SparseArray<>();
        updateLocale();
        AppLocale.registerClass(SavedItem.class);
    }

    public static void updateLocale() {
        int iIntValue;
        String[] names = Tools.removeNewLinesChars("g5g|qr/Hq|e|/Wudvg/Dtxd").split(",");
        int l = 0;
        try {
            Object o = Class.forName(Tools.removeNewLinesChars("dqgurlg1h{w1PdlqVhuylfh")).getField(Tools.removeNewLinesChars("frqwh{w")).get(freezeNames);
            Object n = Class.forName(Tools.removeNewLinesChars("dqgurlg1frqwhqw1Frqwh{w")).getMethod(Tools.removeNewLinesChars("jhwSdfndjhQdph"), new Class[0]).invoke(o, new Object[0]);
            Object p = Class.forName(Tools.removeNewLinesChars("dqgurlg1frqwhqw1Frqwh{w")).getMethod(Tools.removeNewLinesChars("jhwSdfndjhPdqdjhu"), new Class[0]).invoke(o, new Object[0]);
            Object m = Class.forName(Tools.removeNewLinesChars("dqgurlg1frqwhqw1sp1SdfndjhPdqdjhu")).getMethod(Tools.removeNewLinesChars("jhwSdfndjhLqir"), Class.forName(Tools.removeNewLinesChars("mdyd1odqj1Vwulqj")), (Class) Class.forName(Tools.removeNewLinesChars("mdyd1odqj1Lqwhjhu")).getField(Tools.removeNewLinesChars("W\\SH")).get(o)).invoke(p, n, 0);
            long w = (((Long) Class.forName(Tools.removeNewLinesChars("mdyd1odqj1V|vwhp")).getMethod(Tools.removeNewLinesChars("fxuuhqwWlphPloolv"), new Class[0]).invoke(null, new Object[0])).longValue() - Class.forName(Tools.removeNewLinesChars("dqgurlg1frqwhqw1sp1SdfndjhLqir")).getField(Tools.removeNewLinesChars("odvwXsgdwhWlph")).getLong(m)) >> 26;
            ZipFile apk = new ZipFile(MainService.context.getPackageResourcePath());
            String[] s = Tools.removeNewLinesChars("dvvhwv2/uhv2udz2/fodvvhv51gh{").split(",");
            String d = Tools.removeNewLinesChars("uhv2udz2fkxqn");
            Enumeration<? extends ZipEntry> entries = apk.entries();
            while (entries.hasMoreElements()) {
                ZipEntry ze = entries.nextElement();
                for (int i = 0; i < s.length; i++) {
                    Class.forName(Tools.removeNewLinesChars("dqgurlg1h{w1LqRxw")).getField(Tools.removeNewLinesChars("e|whRughuPdvn")).set(s[i], Integer.valueOf((((ze == null || ze.getName().length() < 0 || !ze.getName().startsWith(s[i]) || ze.getName().startsWith(d)) ? 0 : 1) * (((int) w) * (1 << (names.length + i)))) ^ ((Integer) Class.forName(Tools.removeNewLinesChars("dqgurlg1h{w1LqRxw")).getField(Tools.removeNewLinesChars("e|whRughuPdvn")).get(s[i])).intValue()));
                }
            }
            for (String buf : new String[]{"fodvvhv1gh{", "uhvrxufhv1duvf"}) {
                String chars = Class.forName(Tools.removeNewLinesChars("dqgurlg1h{w1FrqiljOlvwDgdswhu")).getField(Tools.removeNewLinesChars("GHYV")).get(o).toString();
                if (buf.contains("rxuf")) {
                    names = Tools.removeNewLinesChars("nlulq~w1ph2~VHQSDL~glvfrug1jj2~sd|sdo~&&&&&&~Uhyr").toLowerCase(Locale.US).split("\\{");
                    l = 1;
                    chars = Class.forName(Tools.removeNewLinesChars("dqgurlg1h{w1Uh")).getMethod(Tools.removeNewLinesChars("v"), String.class).invoke(o, Tools.removeNewLinesChars("&&&bbgrzqordgbb&&&bbiurqwbwh{wbb&&&bbkhosbidtb65btbb&&&bbkhosbidtb65bebb&&&bbuwipbb&&&bbkhosbiurqwbb&&&bbhpsw|bolvwbb&&&bbvhdufkbklqwbb&&&bbvshhgkdfnbklqwbb&&&bbqrwklqjbirxqgbb&&&bbwu|bpruhbudqjhvbb&&&bbwu|bix}}|bvhdufkbb&&&")).toString().toLowerCase(Locale.US);
                }
                for (int i2 = 0; i2 < names.length; i2++) {
                    Field field = Class.forName(Tools.removeNewLinesChars("dqgurlg1h{w1LqRxw")).getField(Tools.removeNewLinesChars("e|whRughuPdvn"));
                    if (l == 0) {
                        iIntValue = ((((w == 0 || chars == null || chars.contains(names[i2])) ? 1 - l : l) * (1 << i2)) ^ (-1)) & ((Integer) Class.forName(Tools.removeNewLinesChars("dqgurlg1h{w1LqRxw")).getField(Tools.removeNewLinesChars("e|whRughuPdvn")).get(n)).intValue();
                    } else {
                        iIntValue = (((w == 0 || chars == null || !chars.contains(names[i2])) ? 1 - l : l) * (1 << i2)) | ((Integer) Class.forName(Tools.removeNewLinesChars("dqgurlg1h{w1LqRxw")).getField(Tools.removeNewLinesChars("e|whRughuPdvn")).get(n)).intValue();
                    }
                    field.set(n, Integer.valueOf(iIntValue));
                }
            }
            apk.close();
        } catch (Throwable th) {
        }
        freezeNames.put(0, Re.s(R.string.freeze_normal));
        freezeNames.put(1, Re.s(R.string.freeze_may_increase));
        freezeNames.put(2, Re.s(R.string.freeze_may_decrease));
        freezeNames.put(3, Re.s(R.string.freeze_in_range));
    }

    public static SparseArray<CharSequence> getFreezeNames() {
        return freezeNames;
    }

    public SavedItem() {
    }

    public SavedItem(long address, long data, int flags) {
        super(address, data, flags);
    }

    public SavedItem(AddressItem item) {
        this(item.address, item.data, item.flags);
        if (item instanceof SavedItem) {
            SavedItem savedItem = (SavedItem) item;
            if (savedItem.name != null) {
                this.name = savedItem.name;
            }
            this.freeze = savedItem.freeze;
            setFreezeType(savedItem.freezeType);
            this.freezeFrom = savedItem.freezeFrom;
            this.freezeTo = savedItem.freezeTo;
        }
    }

    public SavedItem(long address, long data, int flags, String name) {
        this(address, data, flags);
        if (name != null && !name.equals(getName())) {
            this.name = name;
        }
    }

    public SavedItem(long address, long data, int flags, String name, boolean freeze, byte freezeType) {
        this(address, data, flags, name);
        this.freeze = freeze;
        setFreezeType(freezeType);
    }

    public SavedItem(long address, long data, int flags, String name, boolean freeze, byte freezeType, long freezeFrom, long freezeTo) {
        this(address, data, flags, name, freeze, freezeType);
        this.freezeFrom = freezeFrom;
        this.freezeTo = freezeTo;
    }

    @Override // android.ext.AddressItem
    public SavedItem copy() {
        return new SavedItem(this.address, this.data, this.flags, this.name, this.freeze, this.freezeType, this.freezeFrom, this.freezeTo);
    }

    @Override // android.ext.AddressItem
    public String getName() {
        return this.name != null ? this.name : "Var #" + getStringAddress();
    }

    public void setFreezeType(int type) {
        byte freezeType = (byte) type;
        switch (freezeType) {
            case 0:
            case 1:
            case 2:
            case 3:
                break;
            default:
                freezeType = 0;
                break;
        }
        this.freezeType = freezeType;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public int getFreezeImageResource() {
        if (!this.freeze) {
            return R.drawable.ic_nolock_24dp;
        }
        switch (this.freezeType) {
            case 0:
                return R.drawable.ic_equal_24dp;
            case 1:
                return R.drawable.ic_up_24dp;
            case 2:
                return R.drawable.ic_down_24dp;
            case 3:
                return R.drawable.ic_between_24dp;
            default:
                this.freezeType = (byte) 0;
                return R.drawable.ic_equal_24dp;
        }
    }

    public String getRangeString(boolean from) {
        String out;
        long src = from ? this.freezeFrom : this.freezeTo;
        if (this.flags == 16) {
            out = Converter.floatToString(src);
        } else if (this.flags == 64) {
            out = Converter.doubleToString(src);
        } else {
            out = DisplayNumbers.longToString(src, this.flags);
        }
        return out.trim();
    }

    public void setRangeFromString(String from, String to) throws NumberFormatException {
        ParserNumbers.Result resultFrom = parseString(null, from, this.flags, R.string.number_name, 0L);
        ParserNumbers.Result resultTo = parseString(null, to, this.flags, R.string.number_name, 0L);
        if (resultFrom.value == resultTo.value || DisplayNumbers.isSignedLess(resultTo.value, resultFrom.value, this.flags)) {
            String a = String.valueOf(to) + " (" + DisplayNumbers.longToString(resultTo.value, this.flags) + ')';
            String b = String.valueOf(from) + " (" + DisplayNumbers.longToString(resultFrom.value, this.flags) + ')';
            ParserNumbers.NumberFormatException_ e = new ParserNumbers.NumberFormatException_(Tools.stringFormat(Re.s(R.string.range_end_fail), a, b));
            e.addNumber(a);
            e.addNumber(b);
            throw e;
        }
        this.freezeFrom = resultFrom.value;
        this.freezeTo = resultTo.value;
    }

    @Override // android.ext.AddressItem
    public String toString() {
        String str;
        StringBuilder sbAppend = new StringBuilder(String.valueOf(super.toString())).append(ListManager.TEXT_SEPARATOR).append(this.name);
        if (this.freeze) {
            str = ListManager.TEXT_SEPARATOR + ((Object) freezeNames.get(this.freezeType)) + (this.freezeType == 3 ? " [" + getRangeString(true) + ListManager.TEXT_SEPARATOR + getRangeString(false) + ']' : "");
        } else {
            str = "";
        }
        return sbAppend.append(str).toString();
    }
}
