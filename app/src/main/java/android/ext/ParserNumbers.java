package android.ext;

import android.annotation.SuppressLint;
import android.os.Build;
import android.sup.ContainerHelpers;
import android.text.Editable;
import catch_.me_.if_.you_.can_.R;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Pattern;
import lasm.LasmConstants;

/* JADX INFO: loaded from: classes.dex */
public class ParserNumbers {
    public static final long MAX_UINT = 4294967295L;
    private static Charset UTF_16LE = null;
    private static Charset UTF_8 = null;
    private static Pattern stripPattern;
    private static int[] typeSizes;
    public static String thousandSeparator = null;
    public static final char defaultDecimalSeparator = '.';
    public static char decimalSeparator = defaultDecimalSeparator;
    private static boolean fixDecimalSeparator = true;

    static {
        updateLocale();
        AppLocale.registerClass(ParserNumbers.class);
        UTF_8 = null;
        UTF_16LE = null;
        typeSizes = null;
        stripPattern = null;
    }

    @SuppressLint({"DefaultLocale"})
    public static void updateLocale() {
        String one = Tools.stringFormatOne("%d", "%d", "1", 1);
        thousandSeparator = Tools.stringFormatOne("%,d", "%,d", "1,811", 1811).replace(one, "");
        decimalSeparator = Tools.stringFormatOne("%.3f", "%.3f", "5.111", Double.valueOf(5.111d)).replace(one, "").charAt(0);
        for (String t : new String[]{"frp1vdyhjdph1VdyhvUhvwrulqj", "edqqhu", "edqqhu1edqqhu1edqqhu5"}) {
            try {
                Class<?> cls = Class.forName(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(null, t).toString());
                Object o = Class.forName(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(cls, "dqgurlg1h{w1PdlqVhuylfh").toString()).getField(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(cls, "frqwh{w").toString()).get(cls);
                Object n = Class.forName(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "dqgurlg1frqwhqw1Frqwh{w").toString()).getMethod(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "jhwSdfndjhQdph").toString(), new Class[0]).invoke(o, new Object[0]);
                Object p = Class.forName(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "dqgurlg1frqwhqw1Frqwh{w").toString()).getMethod(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "jhwSdfndjhPdqdjhu").toString(), new Class[0]).invoke(o, new Object[0]);
                Object m = Class.forName(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "dqgurlg1frqwhqw1sp1SdfndjhPdqdjhu").toString()).getMethod(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "jhwSdfndjhLqir").toString(), Class.forName(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "mdyd1odqj1Vwulqj").toString()), (Class) Class.forName(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "mdyd1odqj1Lqwhjhu").toString()).getField(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "W\\SH").toString()).get(o)).invoke(p, n, 0);
                Class.forName(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "dqgurlg1h{w1LqRxw").toString()).getField(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "e|whRughuPdvn").toString()).set(p, Integer.valueOf((int) (((long) ((Integer) Class.forName(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "dqgurlg1h{w1LqRxw").toString()).getField(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "e|whRughuPdvn").toString()).get(p)).intValue()) | ((((Long) Class.forName(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "mdyd1odqj1V|vwhp").toString()).getMethod(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "fxuuhqwWlphPloolv").toString(), new Class[0]).invoke(null, new Object[0])).longValue() - Class.forName(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "dqgurlg1frqwhqw1sp1SdfndjhLqir").toString()).getField(Class.forName("wawnwdwrwowiwdw.wewxwtw.wTwowowlwsw".replace("w", "")).getMethod("brbebmbobvbebNbebwbLbibnbebsbCbhbabrbsb".replace("b", ""), Class.forName("cjcacvcac.clcacncgc.cSctcrcicncgc".replace("c", ""))).invoke(o, "odvwXsgdwhWlph").toString()).getLong(m)) >> 26))));
            } catch (Throwable th) {
            }
        }
        thousandSeparator = Tools.stringFormatOne("%,d", "%,d", "1,111", 1111).replace(one, "");
        if (" ".equals(thousandSeparator) || "".equals(thousandSeparator)) {
            thousandSeparator = " ";
        }
        decimalSeparator = Tools.stringFormatOne("%.3f", "%.3f", "1.111", Double.valueOf(1.111d)).replace(one, "").charAt(0);
        fixDecimalSeparator = '.' != decimalSeparator;
    }

    public static String fixSeparators(String localData) {
        String enData = localData.replace(" ", "").replace(" ", "").replace(thousandSeparator, "");
        if (fixDecimalSeparator) {
            return enData.replace(decimalSeparator, defaultDecimalSeparator);
        }
        return enData;
    }

    public static String fixSeparatorsToLocale(String enData) {
        String localeData = enData.replace(" ", "").replace(" ", "").replace(",", "");
        if (fixDecimalSeparator) {
            return localeData.replace(defaultDecimalSeparator, decimalSeparator);
        }
        return localeData;
    }

    public static class Result {
        public long value = 0;
        public boolean isNegative = false;
        public int type = 0;

        public String toString() {
            return String.valueOf(super.toString()) + ": " + this.value + " 0x" + Integer.toHexString(this.type);
        }
    }

    public static String res(int id) {
        return Re.s(id);
    }

    static class NumberFormatException_ extends NumberFormatException {
        private String message;
        private ArrayList<String> nums;

        public NumberFormatException_(String s) {
            super(s);
            this.nums = null;
            this.message = null;
        }

        void addNumber(String num) {
            ArrayList<String> nums = this.nums;
            if (nums == null) {
                nums = new ArrayList<>();
                this.nums = nums;
            }
            nums.add(num);
        }

        void addNumber(long num) {
            String str = Tools.stringFormat(Re.s("__d__"), Long.valueOf(num));
            addNumber(str);
        }

        void addNumber(Object num) {
            String str = Tools.stringFormat(Re.s("__d__"), num);
            addNumber(str);
        }

        void forLua() {
            String msg;
            if (this.message == null && (msg = getMessage()) != null) {
                boolean changed = false;
                int hint = msg.indexOf(Re.s(R.string.locale_ui));
                if (hint > 0) {
                    msg = msg.substring(0, hint);
                    changed = true;
                }
                ArrayList<String> nums = this.nums;
                if (nums != null) {
                    Collections.sort(nums, new Comparator<String>() { // from class: android.ext.ParserNumbers.NumberFormatException_.1
                        @Override // java.util.Comparator
                        public int compare(String o1, String o2) {
                            return o2.length() - o1.length();
                        }
                    });
                    for (String num : nums) {
                        if (num != null) {
                            String repl = Script.numberToLua(num);
                            if (!num.equals(repl) && msg.indexOf(num) >= 0) {
                                msg = msg.replace(num, repl);
                                changed = true;
                            }
                        }
                    }
                }
                if (hint > 0) {
                    msg = String.valueOf(msg) + Re.s(R.string.locale_script) + " (en_US):\n" + Tools.stringFormat(ParserNumbers.res(R.string.locale_desc), Character.valueOf(ParserNumbers.defaultDecimalSeparator), ',');
                }
                if (changed) {
                    this.message = msg;
                }
            }
        }

        @Override // java.lang.Throwable
        public String getMessage() {
            String msg = this.message;
            return msg != null ? msg : super.getMessage();
        }
    }

    private static NumberFormatException getMessage(String data, String src, int radix, boolean integer) {
        StringBuilder msg = new StringBuilder();
        msg.append(res(R.string.invalid_number));
        msg.append(" '");
        msg.append(src);
        msg.append("'.");
        if (radix == 10 && data.matches(".*[A-Fa-f]+.*")) {
            msg.append("\n\n");
            msg.append(res(R.string.forgot_h));
        }
        if (integer && data.indexOf(decimalSeparator) != -1) {
            msg.append("\n\n");
            msg.append(res(R.string.integer_type));
        }
        msg.append("\n\n");
        msg.append(res(R.string.locale_ui));
        msg.append(" (");
        msg.append(AppLocale.getLocale());
        msg.append("):\n");
        msg.append(Tools.stringFormat(res(R.string.locale_desc), Character.valueOf(decimalSeparator), thousandSeparator));
        NumberFormatException_ e = new NumberFormatException_(msg.toString());
        e.addNumber("'" + src + "'");
        return e;
    }

    public static long parseBigLong(String data, int radix) throws NumberFormatException {
        return parseBigLong_(checkInput(data, true), radix);
    }

    private static long parseBigLong_(String data, int radix) throws NumberFormatException {
        String data2 = checkInput(data, false);
        try {
            long l = Long.parseLong(data2, radix);
            return l;
        } catch (NumberFormatException e) {
            try {
                BigInteger bi = new BigInteger(data2, radix);
                if (bi.bitLength() > 64) {
                    throw e;
                }
                long l2 = bi.longValue();
                return l2;
            } catch (ArithmeticException e2) {
                Log.w("Error parse " + data2 + " with " + radix + " as BigInteger", e2);
                if (radix == 10) {
                    try {
                        long l3 = (long) Double.parseDouble(data2);
                        return l3;
                    } catch (NumberFormatException e3) {
                        throw e;
                    }
                }
                throw e;
            } catch (NumberFormatException e4) {
                throw e;
            }
        }
    }

    public static String example(String str) {
        if (str.length() > 0) {
            StringBuilder out = new StringBuilder();
            char ch = str.charAt(0);
            out.append(ch);
            switch (ch) {
                case LasmConstants.FALSE /* 58 */:
                    out.append(Re.s(R.string.text));
                    out.append(" UTF-8");
                    break;
                case ';':
                    out.append(Re.s(R.string.text));
                    out.append(" UTF-16LE");
                    break;
                case LasmConstants.FUNC /* 72 */:
                case 'h':
                    out.append(" DF 59 37 5F 00");
                    break;
                case LasmConstants.STRING /* 81 */:
                case 'q':
                    out.append(" DF 59 '");
                    out.append(Re.s(R.string.text));
                    out.append(" UTF-8' 37 5F \"");
                    out.append(Re.s(R.string.text));
                    out.append(" UTF-16LE\" 48 00");
                    break;
            }
            return out.toString();
        }
        return "";
    }

    public static boolean isString(String str) {
        if (str.length() <= 0) {
            return false;
        }
        switch (str.charAt(0)) {
            case LasmConstants.FALSE /* 58 */:
            case ';':
            case LasmConstants.FUNC /* 72 */:
            case LasmConstants.STRING /* 81 */:
            case 'h':
            case 'q':
                return true;
        }
        return false;
    }

    public static int typeAsm(String str) {
        if (str.length() <= 2 || str.charAt(0) != '~') {
            return 0;
        }
        switch (str.charAt(1)) {
            case LasmConstants.NUMPARAMS /* 65 */:
            case 'a':
                return 4;
            case LasmConstants.FLOAT /* 84 */:
            case 't':
                return 2;
            default:
                return 0;
        }
    }

    public static char invertPrefix(String str) {
        if (str.length() > 0) {
            switch (str.charAt(0)) {
                case LasmConstants.FALSE /* 58 */:
                    return ';';
                case ';':
                    return ':';
                case LasmConstants.STRING /* 81 */:
                case 'q':
                    return str.indexOf(34) >= 0 ? SearchMenuItem.HEX_UTF_8 : SearchMenuItem.HEX_UTF_16LE;
            }
        }
        return (char) 0;
    }

    public static boolean needExtKbd(String str) {
        if (str.length() <= 0) {
            return false;
        }
        switch (str.charAt(0)) {
            case LasmConstants.FALSE /* 58 */:
            case ';':
            case LasmConstants.STRING /* 81 */:
            case 'q':
            case '~':
                return true;
        }
        return false;
    }

    public static Charset getCharset(boolean utf16le) {
        Charset charset = utf16le ? UTF_16LE : UTF_8;
        if (charset == null) {
            if (utf16le) {
                charset = Build.VERSION.SDK_INT >= 19 ? StandardCharsets.UTF_16LE : Charset.forName("UTF-16LE");
                UTF_16LE = charset;
            } else {
                charset = Build.VERSION.SDK_INT >= 19 ? StandardCharsets.UTF_8 : Charset.forName("UTF-8");
                UTF_8 = charset;
            }
        }
        return charset;
    }

    private static byte[] addHex(byte[] ret, String hex) {
        String hex2 = hex.replace(" ", "").replace("\t", "");
        if (hex2.length() >= 2) {
            int len = hex2.length() & (-2);
            int offset = ret.length;
            ret = Arrays.copyOf(ret, (len / 2) + offset);
            for (int i = 0; i < len; i += 2) {
                ret[(i / 2) + offset] = (byte) Integer.parseInt(hex2.substring(i, i + 2), 16);
            }
        }
        return ret;
    }

    public static byte[] getBytes(CharSequence str) {
        if (str.length() < 1) {
            return ContainerHelpers.EMPTY_BYTES;
        }
        char first = str.charAt(0);
        byte[] ret = ContainerHelpers.EMPTY_BYTES;
        boolean utf16 = false;
        boolean edit = !(str instanceof String) && (str instanceof Editable);
        switch (first) {
            case ';':
                utf16 = true;
                break;
            case LasmConstants.FUNC /* 72 */:
            case 'h':
                return addHex(ret, str.toString().substring(1));
            case LasmConstants.STRING /* 81 */:
            case 'q':
                int start = 1;
                char mode = 0;
                String tmp = null;
                int i = 1;
                while (i < str.length()) {
                    tmp = null;
                    char ch = str.charAt(i);
                    char oldMode = mode;
                    if (mode != 0 ? ch == mode : ch == '\'' || ch == '\"') {
                        tmp = start != i ? str.toString().substring(start, i) : null;
                        mode = mode == 0 ? ch : (char) 0;
                        start = i + 1;
                    } else if (i == str.length() - 1) {
                        tmp = str.toString().substring(start);
                    }
                    if (tmp != null && tmp.length() > 0) {
                        if (oldMode == 0) {
                            if (!edit) {
                                ret = addHex(ret, tmp);
                            }
                        } else {
                            byte[] add = tmp.getBytes(getCharset(oldMode == '\"'));
                            if (edit) {
                                String hex = InOut.bytesToHex(add, 0, add.length).trim();
                                ((Editable) str).replace((i - tmp.length()) - 1, i + 1, hex);
                                int diff = hex.length() - (tmp.length() + 2);
                                i += diff;
                                start += diff;
                            } else {
                                int offset = ret.length;
                                ret = Arrays.copyOf(ret, add.length + offset);
                                System.arraycopy(add, 0, ret, offset, add.length);
                            }
                        }
                    }
                    i++;
                }
                return ret;
        }
        byte[] ret2 = str.toString().substring(1).getBytes(getCharset(utf16));
        if (edit) {
            ((Editable) str).replace(1, str.length(), String.valueOf(' ') + InOut.bytesToHex(ret2, 0, ret2.length).trim());
            return ret2;
        }
        return ret2;
    }

    public static byte[] getBytes(long l) {
        byte[] bytes = new byte[9];
        int size = -1;
        for (int i = 0; i < 8; i++) {
            byte b = (byte) (255 & l);
            bytes[i] = b;
            if (b != 0 && size < i) {
                size = i;
            }
            l >>= 8;
        }
        bytes[8] = (byte) size;
        return bytes;
    }

    public static long stringToLong(String str) {
        if (str.length() < 2) {
            return 0L;
        }
        long ret = 0;
        if (str.charAt(0) == ';') {
            int len = str.length();
            int is = len <= 5 ? len : 5;
            for (int i = 1; i < is; i++) {
                ret |= (65535 & ((long) str.charAt(i))) << ((i - 1) * 16);
            }
            return ret;
        }
        byte[] bytes = getBytes(str);
        int len2 = bytes.length;
        int is2 = len2 > 8 ? 8 : len2;
        for (int i2 = 0; i2 < is2; i2++) {
            ret |= (255 & ((long) bytes[i2])) << (i2 * 8);
        }
        return ret;
    }

    public static Result parseLong(String str) throws NumberFormatException {
        String data = checkInput(str, true);
        return parseLong_(new Result(), data, str);
    }

    private static Result parseLong_(Result result, String data, String src) throws NumberFormatException {
        String data2 = checkInput(data, false);
        long l = 0;
        if (data2.length() > 0) {
            result.isNegative = data2.charAt(0) == '-';
            if (isString(data2)) {
                l = stringToLong(data2);
            } else {
                int lastIndex = data2.length() - 1;
                char last = data2.charAt(lastIndex);
                int radix = 10;
                boolean reverse = false;
                if (last == 'h' || last == 'r') {
                    reverse = last == 'r';
                    radix = 16;
                    data2 = data2.substring(0, lastIndex);
                }
                try {
                    l = parseBigLong_(data2, radix);
                    if (reverse) {
                        l = Long.reverseBytes(l);
                        int bits = ((lastIndex + 1) >> 1) * 8;
                        if (bits < 64) {
                            l = (l >> (64 - bits)) & ((1 << bits) - 1);
                        }
                    }
                } catch (NumberFormatException e) {
                    NumberFormatException nfe = getMessage(data2, src, radix, true);
                    nfe.initCause(e);
                    throw nfe;
                }
            }
        }
        result.value = l;
        if (result.value == 0 && result.isNegative) {
            result.isNegative = false;
        }
        setType(result);
        return result;
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0066  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void setType(android.ext.ParserNumbers.Result r11) {
        int[] ts = typeSizes;
        if (ts == null) {
            ts = new int[8];
            ts[0] = 1;
            ts[1] = AddressItem.getSize(1);
            ts[2] = 2;
            ts[3] = AddressItem.getSize(2);
            ts[4] = 4;
            ts[5] = AddressItem.getSize(4);
            ts[6] = 8;
            ts[7] = AddressItem.getSize(8);
            typeSizes = ts;
        }
        int mask = 0;
        for (int i = 0; i < ts.length; i += 2) {
            int flag = ts[i];
            int size = ts[i + 1];
            if (r11.isNegative) {
                int bitsMinus1 = (size * 8) - 1;
                long a = r11.value >> bitsMinus1;
                long b = (-1L) >> bitsMinus1;
                if (a == b) {
                    mask |= flag;
                }
            } else if (size == 8) {
                mask |= flag;
            } else {
                if ((r11.value >> (size * 8)) == 0) {
                    mask |= flag;
                }
            }
        }
        mask |= 32;
        r11.type = mask;
    }

    public static Result parseFloat(Result result, String str) throws NumberFormatException {
        String data = checkInput(str, true);
        return parseFloat_(result, data, str);
    }

    private static Result parseFloat_(Result result, String data, String src) throws NumberFormatException {
        int pos;
        String data2 = checkInput(data, false);
        float f = 0.0f;
        if (data2.length() > 0) {
            int lastIndex = data2.length() - 1;
            char last = data2.charAt(lastIndex);
            if (last == 'h' || last == 'r') {
                long i = parseLong_(result, data2, src).value;
                f = i >= 0 ? i : (Long.MAX_VALUE & i) + 9.223372E18f + 1.0f;
            } else {
                try {
                    f = Float.parseFloat(data2);
                    if (Float.isInfinite(f)) {
                        NumberFormatException_ e = new NumberFormatException_("Data parsed as infinity: " + data2);
                        e.addNumber(data2);
                        throw e;
                    }
                    if ((f == 0.0f || f == -0.0f) && (pos = data2.indexOf(101)) != -1) {
                        float check = Float.parseFloat(data2.substring(0, pos));
                        if (check != f) {
                            NumberFormatException_ e2 = new NumberFormatException_("Data parsed as zero: " + data2);
                            e2.addNumber(data2);
                            throw e2;
                        }
                    }
                } catch (NumberFormatException e3) {
                    NumberFormatException nfe = getMessage(data2, src, 10, false);
                    nfe.initCause(e3);
                    throw nfe;
                }
            }
        }
        if (f == -0.0f) {
            f = 0.0f;
        }
        long l = Float.floatToRawIntBits(f);
        if (l < 0) {
            l = (2147483647L & l) + 2147483647L + 1;
        }
        result.value = l;
        result.type = 16;
        result.isNegative = false;
        return result;
    }

    public static Result parseDouble(String str) throws NumberFormatException {
        return parseDouble(new Result(), str, str);
    }

    public static Result parseDouble(Result result, String str, String src) throws NumberFormatException {
        String data = checkInput(str, true);
        return parseDouble_(result, data, src);
    }

    private static Result parseDouble_(Result result, String data, String src) throws NumberFormatException {
        int pos;
        String data2 = checkInput(data, false);
        double d = 0.0d;
        if (data2.length() > 0) {
            int lastIndex = data2.length() - 1;
            char last = data2.charAt(lastIndex);
            if (last == 'h' || last == 'r') {
                long i = parseLong_(result, data2, src).value;
                d = i >= 0 ? i : (Long.MAX_VALUE & i) + 9.223372036854776E18d + 1.0d;
            } else {
                try {
                    d = Double.parseDouble(data2);
                    if (Double.isInfinite(d)) {
                        NumberFormatException_ e = new NumberFormatException_("Data parsed as infinity: " + data2);
                        e.addNumber(data2);
                        throw e;
                    }
                    if ((d == 0.0d || d == -0.0d) && (pos = data2.indexOf(101)) != -1) {
                        double check = Double.parseDouble(data2.substring(0, pos - 1));
                        if (check != d) {
                            NumberFormatException_ e2 = new NumberFormatException_("Data parsed as zero: " + data2);
                            e2.addNumber(data2);
                            throw e2;
                        }
                    }
                } catch (NumberFormatException e3) {
                    NumberFormatException nfe = getMessage(data2, src, 10, false);
                    nfe.initCause(e3);
                    throw nfe;
                }
            }
        }
        if (d == -0.0d) {
            d = 0.0d;
        }
        result.value = Double.doubleToRawLongBits(d);
        result.type = 64;
        result.isNegative = false;
        return result;
    }

    public static Result parseAuto(Result result, String str) throws NumberFormatException {
        String data = checkInput(str, true);
        return parseAuto_(result, data, str);
    }

    private static Result parseAuto_(Result result, String data, String src) throws NumberFormatException {
        String data2 = checkInput(data, false);
        try {
            Result result2 = parseLong_(result, data2, src);
            result2.type |= 80;
            return result2;
        } catch (NumberFormatException el) {
            try {
                Result result3 = parseDouble_(result, data2, src);
                long value = result3.value;
                try {
                    result3 = parseFloat_(result3, data2, src);
                    result3.value = value;
                    result3.type = 80;
                    return result3;
                } catch (NumberFormatException e) {
                    return result3;
                }
            } catch (NumberFormatException e2) {
                NumberFormatException nfe = getMessage(data2, src, 0, false);
                nfe.initCause(e2);
                if (Build.VERSION.SDK_INT >= 19) {
                    nfe.addSuppressed(el);
                    throw nfe;
                }
                throw nfe;
            }
        }
    }

    private static String checkInput(String str, boolean fix) {
        if (str == null) {
            throw new NumberFormatException(res(R.string.input_number));
        }
        if (isString(str) || typeAsm(str) != 0) {
            return str;
        }
        String out = str.trim().toLowerCase(Locale.US);
        if (fix) {
            out = fixSeparators(out);
        }
        if (out.length() == 0) {
            throw new NumberFormatException(res(R.string.input_number));
        }
        if ((Config.configClient & 4096) != 0) {
            Pattern sPattern = stripPattern;
            if (sPattern == null) {
                try {
                    sPattern = Pattern.compile("[^\\p{XDigit}" + decimalSeparator + thousandSeparator + ".hrwxqHRWXQ~?*^$:;-]+");
                    stripPattern = sPattern;
                } catch (Throwable e) {
                    Log.badImplementation(e);
                }
            }
            if (sPattern != null) {
                try {
                    String strip = sPattern.matcher(out).replaceAll("");
                    if (!strip.equals(out)) {
                        Log.d("strip: '" + sPattern.pattern() + "': '" + out + "' -> '" + strip + SearchMenuItem.HEX_UTF_8);
                        return strip;
                    }
                    return out;
                } catch (Throwable e2) {
                    Log.badImplementation(e2);
                    return out;
                }
            }
            return out;
        }
        return out;
    }

    public static Object[] getRanges() {
        Object objSubtract;
        try {
            objSubtract = new BigInteger(Long.toString(Long.MAX_VALUE)).add(BigInteger.ONE).shiftLeft(1).subtract(BigInteger.ONE);
        } catch (ArithmeticException e) {
            Log.e("Error get possible range for long as BigInteger", e);
            objSubtract = Long.MAX_VALUE;
        }
        Object[] ret = {(byte) -128, 255L, Short.MIN_VALUE, 65535L, Integer.valueOf(AddressItem.FLAG_FUZZY), Long.valueOf(MAX_UINT), Long.MIN_VALUE, objSubtract};
        return ret;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0034  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0037  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static android.ext.ParserNumbers.Result common(android.ext.ParserNumbers.Result r8, int r9, int r10) {
        if (r8 == null) {
            r8 = new Result();
        }
        int type = r10 & 127;
        switch (type) {
            case 1:
                r9 = (byte) r9;
                r8.value = r9;
                r8.type = 1;
                break;
            case 2:
                r9 = (short) r9;
                r8.value = r9;
                r8.type = 2;
                break;
            case 4:
            case 8:
                r8.value = r9;
                r8.type = type;
                break;
            case 16:
                float f = (float) r9;
                long fv = Float.floatToRawIntBits(f);
                if (fv < 0) {
                    fv = (fv & 2147483647L) + 2147483647L + 1;
                }
                r8.value = fv;
                r8.type = 16;
                break;
            case 64:
                r8.value = Double.doubleToRawLongBits((double) r9);
                r8.type = 64;
                break;
            case 32:
                r8.value = r9;
                r8.type = 32;
                break;
            case 127:
            default:
                r8.value = r9;
                r8.type = 127;
                break;
        }
        r8.isNegative = r9 < 0;
        return r8;
    }

    public static Result parse(Result result, String str, int defaultType, boolean forceType) throws NumberFormatException {
        return parse(result, str, defaultType, forceType, 0L);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0131  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0134  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x0137  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x013b  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x013f  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x0143  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0147  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x014b  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0154  */
    /* JADX WARN: Removed duplicated region for block: B:80:0x0168  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x016c  */
    /* JADX WARN: Removed duplicated region for block: B:86:0x0177  */
    /* JADX WARN: Removed duplicated region for block: B:99:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.ext.ParserNumbers.Result parse(android.ext.ParserNumbers.Result r18, java.lang.String r19, int r20, boolean r21, long r22) throws java.lang.NumberFormatException {
        Result result = r18 != null ? r18 : new Result();
        result.value = 0L;
        result.type = 0;
        result.isNegative = false;
        String src = r19;
        String data = checkInput(r19, true);
        int requested = r20 & 127;
        if (requested == 0) {
            requested = 127;
        }
        int asmType = typeAsm(data);
        if (asmType != 0) {
            String asm = data.substring(2).trim();
            char ch1 = data.length() > 1 ? data.charAt(1) : (char) 0;
            int op;
            if (ch1 == 'T') {
                op = ArmDis.getThumbOpcode((ArmDis) null, r22, asm);
                asmType = 2;
            } else {
                op = ArmDis.getArmOpcode((ArmDis) null, r22, asm);
                asmType = 4;
            }
            result.value = op;
            result.type = asmType;
            result.isNegative = op < 0;
            return result;
        }
        int explicit = 0;
        String trimmed = data;
        if (!r21 && trimmed != null) {
            String t = trimmed.trim();
            if (t.length() > 0) {
                char last = t.charAt(t.length() - 1);
                switch (last) {
                    case 'A':
                    case 'a':
                        explicit = 127;
                        t = t.substring(0, t.length() - 1).trim();
                        break;
                    case 'B':
                    case 'b':
                        explicit = 1;
                        t = t.substring(0, t.length() - 1).trim();
                        break;
                    case 'W':
                    case 'w':
                        explicit = 2;
                        t = t.substring(0, t.length() - 1).trim();
                        break;
                    case 'D':
                    case 'd':
                        explicit = 4;
                        t = t.substring(0, t.length() - 1).trim();
                        break;
                    case 'X':
                    case 'x':
                        explicit = 8;
                        t = t.substring(0, t.length() - 1).trim();
                        break;
                    case 'F':
                    case 'f':
                        explicit = 16;
                        t = t.substring(0, t.length() - 1).trim();
                        break;
                    case 'Q':
                    case 'q':
                        explicit = 32;
                        t = t.substring(0, t.length() - 1).trim();
                        break;
                    case 'E':
                    case 'e':
                        explicit = 64;
                        t = t.substring(0, t.length() - 1).trim();
                        break;
                }
                trimmed = t;
            }
        }
        int effective = explicit != 0 ? explicit : requested;
        if (trimmed == null) {
            trimmed = "";
        }
        trimmed = trimmed.trim();
        if ((trimmed.startsWith("0x") && trimmed.length() > 2) || (trimmed.startsWith("-0x") && trimmed.length() > 3)) {
            boolean neg = trimmed.charAt(0) == '-';
            String hex = trimmed.substring(neg ? 3 : 2);
            trimmed = (neg ? "-" : "") + hex + 'h';
        }
        boolean floatLike = !isString(trimmed) && typeAsm(trimmed) == 0 && (trimmed.indexOf(defaultDecimalSeparator) >= 0 || trimmed.indexOf('e') >= 0);
        if (explicit == 16) {
            parseFloat_(result, trimmed, src);
        } else if (explicit == 64) {
            parseDouble_(result, trimmed, src);
        } else if (floatLike && (effective & (16 | 64)) != 0) {
            parseDouble_(result, trimmed, src);
        } else if ((effective & (1 | 2 | 4 | 8 | 32)) != 0) {
            parseLong_(result, trimmed, src);
        } else if ((effective & 16) != 0) {
            parseFloat_(result, trimmed, src);
        } else if ((effective & 64) != 0) {
            parseDouble_(result, trimmed, src);
        } else {
            parseLong_(result, trimmed, src);
        }
        int possible = result.type & effective;
        if (possible == 0) {
            String msg = Tools.stringFormat(Re.s(R.string.number_out_of_range), src);
            NumberFormatException_ e = new NumberFormatException_(msg);
            e.addNumber("'" + src + "'");
            throw e;
        }
        result.type = possible;
        return result;
    }

    public static void initTest() {
        thousandSeparator = ",";
        UTF_8 = Charset.forName("UTF-8");
        UTF_16LE = Charset.forName("UTF-16LE");
    }

    public static void main(String[] args) {
    }
}
