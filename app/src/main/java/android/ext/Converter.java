package android.ext;

import android.content.SharedPreferences;
import android.ext.ParserNumbers;
import android.ext.Script;
import catch_.me_.if_.you_.can_.R;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class Converter {
    public static final int MAX_ITEMS = 100000;
    private static Filters filters = new Filters(0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0d, 0);
    private static final DecimalFormat formatGeneric = new DecimalFormat("0.0#######E0", DecimalFormatSymbols.getInstance(Locale.US));
    private static final DecimalFormat formatSmall = new DecimalFormat("###,###,##0.0##########", DecimalFormatSymbols.getInstance(Locale.US));
    private static int show;

    public static class Filters {
        long addrMax;
        long addrMin;
        int filters;
        double fractional;
        int fractionalSign;
        int pointer;
        int type;
        long valMax;
        int valMaxFlags;
        long valMin;
        int valMinFlags;

        public Filters(int filters, long addrMin, long addrMax, long valMin, int valMinFlags, long valMax, int valMaxFlags, int type, int fractionalSign, double fractional, int pointer) {
            this.filters = filters;
            this.addrMin = addrMin;
            this.addrMax = addrMax;
            this.valMin = valMin;
            this.valMinFlags = valMinFlags;
            this.valMax = valMax;
            this.valMaxFlags = valMaxFlags;
            this.type = type;
            this.fractionalSign = fractionalSign;
            this.fractional = fractional;
            this.pointer = pointer;
        }
    }

    public static Filters getFilters(long addrMin, long addrMax, String valueMin, String valueMax, int type, String fractional, int pointer) {
        int fs = 0;
        if (addrMin != 0) {
            fs = 0 | 1;
        }
        if (addrMax != -1) {
            fs |= 2;
        }
        long valMin = 0;
        int valMinFlags = 0;
        if (valueMin != null) {
            fs |= 4;
            ParserNumbers.Result res = ParserNumbers.parse(null, valueMin.trim(), AddressItem.FLAG_AUTO, false);
            valMin = res.value;
            valMinFlags = res.type;
        }
        long valMax = 0;
        int valMaxFlags = 0;
        if (valueMax != null) {
            fs |= 8;
            ParserNumbers.Result res2 = ParserNumbers.parse(null, valueMax.trim(), AddressItem.FLAG_AUTO, false);
            valMax = res2.value;
            valMaxFlags = res2.type;
        }
        if (type != 0) {
            fs |= 16;
        }
        int fractionalSign = 0;
        double fractionalValue = 0.0d;
        if (fractional != null) {
            String fractional2 = fractional.trim();
            fs |= 32;
            if (fractional2.length() > 0 && fractional2.charAt(0) == '!') {
                fractional2 = fractional2.substring(1);
                fractionalSign = AddressItem.FLAG_VALUE_NOT_EQUAL;
            } else {
                fractionalSign = AddressItem.FLAG_VALUE_EQUAL;
            }
            fractionalValue = Double.longBitsToDouble(ParserNumbers.parse(null, fractional2, 64, false).value);
        }
        if (pointer != 0) {
            fs |= 64;
        }
        return new Filters(fs, addrMin, addrMax, valMin, valMinFlags, valMax, valMaxFlags, type, fractionalSign, fractionalValue, pointer);
    }

    public static void setFilters(String addressMin, String addressMax, String valueMin, String valueMax, int type, String fractional, int pointer) {
        filters = getFilters(addressMin == null ? 0L : ParserNumbers.parse(null, String.valueOf(addressMin.trim()) + SearchMenuItem.HEX, AddressItem.FLAG_AUTO, false).value, addressMax == null ? -1L : ParserNumbers.parse(null, String.valueOf(addressMax.trim()) + SearchMenuItem.HEX, AddressItem.FLAG_AUTO, false).value, valueMin, valueMax, type, fractional, pointer);
    }

    public static int getShowCount() {
        return show;
    }

    public static void recordGetResults(Record record, boolean withFilters) {
        record.write("gg.getResults(");
        record.write(Integer.toString(getShowCount()));
        if (withFilters) {
            Filters filters2 = filters;
            int fs = filters2.filters;
            record.write(", ");
            record.write("nil");
            record.write(", ");
            if ((fs & 1) == 0) {
                record.write("nil");
            } else {
                Script.Consts.logHex(record, filters2.addrMin);
            }
            record.write(", ");
            if ((fs & 2) == 0) {
                record.write("nil");
            } else {
                Script.Consts.logHex(record, filters2.addrMax);
            }
            record.write(", ");
            if ((fs & 4) == 0) {
                record.write("nil");
            } else {
                Script.Consts.logString(record, AddressItem.getStringData(0L, filters2.valMin, filters2.valMinFlags));
            }
            record.write(", ");
            if ((fs & 8) == 0) {
                record.write("nil");
            } else {
                Script.Consts.logString(record, AddressItem.getStringData(0L, filters2.valMax, filters2.valMaxFlags));
            }
            record.write(", ");
            if ((fs & 16) == 0) {
                record.write("nil");
            } else {
                Script.Consts.logConst(record, record.consts.TYPE_, filters2.type);
            }
            record.write(", ");
            if ((fs & 32) == 0) {
                record.write("nil");
            } else {
                Script.Consts.logString(record, String.valueOf(filters2.fractionalSign == 268435456 ? "|" : "") + filters2.fractional);
            }
            record.write(", ");
            if ((fs & 64) == 0) {
                record.write("nil");
            } else {
                Script.Consts.logConst(record, record.consts.POINTER_, filters2.pointer);
            }
        }
        record.write(")\n");
    }

    public static void setShowCount(int show_value) {
        show = Math.min(Math.max(1, show_value), 100000);
    }

    public static boolean isFiltersActive() {
        return filters.filters != 0;
    }

    public static void init(MainService service) {
        int show2;
        SharedPreferences preferences = Tools.getSharedPreferences();
        try {
            show2 = parseStringToInt(preferences.getString(Re.getName(R.id.eMaxShow), "100"));
        } catch (NumberFormatException e) {
            show2 = 100;
        }
        setShowCount(show2);
    }

    public static void getResultList() {
        getResultList((byte) 0, getShowCount(), 0, filters);
    }

    public static void getResultList(byte seq, int maxSize, int offset, Filters fs) {
        MainService.instance.mDaemonManager.getResultList(seq, maxSize, offset, fs.filters, fs.addrMin, fs.addrMax, fs.valMin, fs.valMinFlags, fs.valMax, fs.valMaxFlags, fs.type, fs.fractionalSign, fs.fractional, fs.pointer);
    }

    public static long intToLong(int i) {
        return ((long) i) & ParserNumbers.MAX_UINT;
    }

    public static int longToInt(long l) {
        return ((-2147483648L) & l) != 0 ? Integer.MIN_VALUE | ((int) (l & 2147483647L)) : (int) (l & 2147483647L);
    }

    public static int parseStringToInt(String data) {
        String data2 = data.trim();
        Integer out = null;
        int radix = 10;
        if (data2.indexOf(46) != -1) {
            out = Integer.valueOf(Float.floatToRawIntBits(Float.parseFloat(data2)));
        }
        int len = data2.length();
        if (len >= 1) {
            char lastChar = data2.charAt(len - 1);
            if (lastChar == 'h') {
                radix = 16;
                data2 = data2.substring(0, len - 1);
            }
            if (lastChar == 'f') {
                data2 = data2.substring(0, len - 1);
                out = Integer.valueOf(Float.floatToRawIntBits(Float.parseFloat(data2)));
            }
        }
        if (out == null) {
            out = Integer.valueOf(longToInt(ParserNumbers.parseBigLong(data2, radix)));
        }
        return out.intValue();
    }

    public static String doubleToString(long l) {
        return doubleToString(Double.longBitsToDouble(l));
    }

    public static String doubleToString(double d) {
        DecimalFormat formatter;
        double dabs = Math.abs(d);
        if ((1.0E-6d < dabs && dabs < 1.0E10d) || dabs == 0.0d) {
            formatter = formatSmall;
        } else {
            formatter = formatGeneric;
        }
        return formatter.format(d).replace('E', 'e');
    }

    public static String floatToString(long l) {
        return floatToString((int) l);
    }

    public static String floatToString(int i) {
        return floatToString(Float.intBitsToFloat(i));
    }

    public static String floatToString(float f) {
        return doubleToString(f);
    }
}
