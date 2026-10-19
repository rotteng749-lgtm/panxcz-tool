package android.ext;

import android.content.DialogInterface;
import android.ext.ParserNumbers;
import android.ext.ProcessList;
import android.ext.Script;
import android.view.View;
import catch_.me_.if_.you_.can_.R;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class SearchButtonListener extends SearchMenuItem implements View.OnClickListener {
    private static final int AREA_MAX = 65536;
    private static final int AREA_MIN = 2;
    public static final int GROUP_MAX = 32;
    public static String lastTextSearch = null;

    public static class XorMode {
        public String input;
        public int x;
    }

    public SearchButtonListener() {
        super(R.string.search_known_value, R.drawable.ic_magnify_white_24dp);
    }

    public static class RangeResult {
        public long from = 0;
        public long to = 0;
        public int type = 0;
        public int sign = AddressItem.FLAG_VALUE_EQUAL;

        public String toString() {
            return String.valueOf(super.toString()) + ": " + this.from + (this.sign == 536870912 ? "~" : "~~") + this.to + ' ' + this.type;
        }
    }

    public static RangeResult parseRange(ParserNumbers.Result result, String input, int defaultType, int sign) throws NumberFormatException {
        String input2 = input.trim();
        String separator = "~~";
        boolean inverse = input2.contains("~~");
        if (inverse) {
            sign = sign == 536870912 ? AddressItem.FLAG_VALUE_NOT_EQUAL : AddressItem.FLAG_VALUE_EQUAL;
        } else {
            separator = "~";
        }
        String[] parts = input2.split(separator);
        if (parts.length > 2) {
            throw new NumberFormatException(String.valueOf(Re.s(R.string.too_many)) + " '" + separator + SearchMenuItem.HEX_UTF_8);
        }
        if (parts.length != 2) {
            parts = (String[]) Arrays.copyOf(parts, 2);
        }
        long[] numbers = new long[2];
        int[] flags = new int[2];
        int i = 1;
        while (i >= 0) {
            String pi = parts[i];
            String pi2 = pi == null ? "" : pi.trim();
            result = ParserNumbers.parse(result, pi2, i == 1 ? defaultType : flags[1], false);
            if (defaultType == 127 && result.type == 64 && "BWDXFQEAbwdxfqea".indexOf(pi2.charAt(pi2.length() - 1)) == -1) {
                result.type |= 16;
            }
            numbers[i] = result.value;
            flags[i] = result.type;
            i--;
        }
        int usedFlags = flags[0] & flags[1];
        String msg = "";
        if (usedFlags == 0) {
            msg = String.valueOf("") + Re.s(R.string.range_bounds_diff_types) + '\n';
        }
        if (msg.length() > 0) {
            throw new NumberFormatException(msg);
        }
        if (defaultType == 127 && (usedFlags & 32) == 0) {
            for (int i2 = 0; i2 < 2; i2++) {
                if ((flags[i2] & 32) != 0) {
                    numbers[i2] = Double.doubleToRawLongBits(numbers[i2]);
                }
            }
        }
        RangeResult ret = new RangeResult();
        ret.from = numbers[0];
        ret.to = numbers[1];
        ret.type = usedFlags;
        ret.sign = sign;
        fixUnsigned(ret, input2);
        return ret;
    }

    private static void fixUnsigned(RangeResult ret, String input) {
        int i = AddressItem.FLAG_VALUE_EQUAL;
        int flags = ret.type & AddressItem.FLAG_AUTO;
        long signMask = 1 << ((AddressItem.getSize(flags) * 8) - 1);
        if (Integer.bitCount(flags) == 1 && flags != 16 && flags != 64 && input.indexOf(45) == -1 && (ret.from & signMask) == 0 && (ret.to & signMask) != 0) {
            Log.d("unsigned search 3: '" + input + "' " + ret.from + ' ' + ret.to + ' ' + Integer.toHexString(flags) + ' ' + Integer.toHexString(ret.sign));
            long t = ret.to;
            ret.to = ret.from;
            ret.from = t;
            if (ret.sign == 536870912) {
                i = AddressItem.FLAG_VALUE_NOT_EQUAL;
            }
            ret.sign = i;
            Log.d("unsigned search 4: '" + input + "' " + ret.from + ' ' + ret.to + ' ' + Integer.toHexString(flags) + ' ' + Integer.toHexString(ret.sign));
        }
    }

    private static boolean doRangeSearch(byte seq, String input, int defaultType, boolean valueEncrypted, int sign, long memoryFrom, long memoryTo, boolean forceNew) throws NumberFormatException {
        String input2 = input.trim();
        XorMode xorMode = parseXorMode(input2, true);
        int x = xorMode != null ? xorMode.x : 0;
        if (xorMode != null) {
            input2 = xorMode.input;
        }
        RangeResult ret = parseRange(null, input2, defaultType, sign);
        String msg = "";
        if (valueEncrypted) {
            msg = String.valueOf("") + Tools.stringFormat(Re.s(R.string.does_not_support), Re.s(R.string.help_range_search_title), Re.s(R.string.mode_hacking)) + '\n';
        }
        if (sign != 536870912 && sign != 268435456) {
            msg = String.valueOf(msg) + Tools.stringFormat(Re.s(R.string.does_not_support), Re.s(R.string.help_range_search_title), AddressItem.getSignNames().get(sign)) + '\n';
        }
        if (msg.length() > 0) {
            throw new NumberFormatException(msg);
        }
        if (x != 0) {
            ret.type |= AddressItem.FLAG_MODE_XOR;
        }
        return startRangeSearch(seq, ret.from, ret.to, x, ret.sign | ret.type, memoryFrom, memoryTo, forceNew);
    }

    private static boolean startRangeSearch(byte seq, long number, long number2, int x, int flags, long memoryFrom, long memoryTo, boolean forceNew) {
        MainService service = MainService.instance;
        if (service.showBusyDialog()) {
            if (service.mResultCount != 0 && forceNew) {
                service.clear(seq);
            }
            if (service.mResultCount == 0) {
                service.usedFuzzy = false;
                service.mDaemonManager.sendConfig(seq);
            }
            service.lockApp(seq);
            service.showSearchHint = false;
            service.mDaemonManager.searchRangeNumber(seq, number, number2, x, flags, memoryFrom, memoryTo);
            MainService.setLastFlags(flags, seq);
            return false;
        }
        throw new NumberFormatException(Re.s(R.string.busy_dialog_fail));
    }

    private static class GroupSearch {
        int area = 2;
        int[] flags;
        long[] from;
        long[] to;

        public GroupSearch(int size) {
            this.from = new long[size];
            this.to = new long[size];
            this.flags = new int[size];
        }
    }

    private static GroupSearch parseGroupSearch(String input, int defaultType, boolean ordered) throws NumberFormatException {
        String input2 = input.trim();
        String splitter = ordered ? "::" : ":";
        String[] parts = input2.split(splitter);
        if (parts.length > 2) {
            throw new NumberFormatException(String.valueOf(Re.s(R.string.too_many)) + " '" + splitter + SearchMenuItem.HEX_UTF_8);
        }
        int area = (int) (parts.length == 1 ? 512L : ParserNumbers.parseLong(parts[1]).value);
        if (area < 2 || area > 65536) {
            ParserNumbers.NumberFormatException_ e = new ParserNumbers.NumberFormatException_(String.valueOf(Tools.stringFormat(Re.s(R.string.invalid_area), 2, 65536)) + ' ' + area);
            e.addNumber(65536L);
            throw e;
        }
        String[] parts2 = parts[0].split(";");
        if (parts2.length < 2) {
            throw new NumberFormatException(Re.s(R.string.union_need_two_numbers));
        }
        if (parts2.length > 32) {
            throw new NumberFormatException(String.valueOf(Tools.stripColon(R.string.too_many_union_numbers)) + ": 32");
        }
        GroupSearch search = new GroupSearch(parts2.length);
        ParserNumbers.Result res = null;
        for (int i = 0; i < parts2.length; i++) {
            if (parts2[i].indexOf(126) != -1) {
                RangeResult result = parseRange(res, parts2[i], defaultType, AddressItem.FLAG_VALUE_EQUAL);
                search.from[i] = result.from;
                search.to[i] = result.to;
                search.flags[i] = result.type | result.sign;
            } else {
                res = ParserNumbers.parse(res, parts2[i], defaultType, false);
                search.from[i] = res.value;
                search.to[i] = 0;
                search.flags[i] = res.type;
            }
        }
        search.area = area;
        return search;
    }

    public static int getMinGroupSize(String input, int defaultType, boolean ordered) {
        int size = 2;
        try {
            GroupSearch search = parseGroupSearch(input, defaultType, ordered);
            int cnt = 0;
            int s = search.flags.length - 1;
            for (int i = 0; i < s; i++) {
                cnt += AddressItem.getSize(search.flags[i]);
            }
            size = cnt + 1;
            return size;
        } catch (NumberFormatException e) {
            return size;
        }
    }

    private static boolean doGroupSearch(byte seq, String input, int defaultType, boolean ordered, boolean valueEncrypted, int sign, long memoryFrom, long memoryTo, boolean forceNew) throws NumberFormatException {
        GroupSearch search = parseGroupSearch(input.trim(), defaultType, ordered);
        int general_flags = ordered ? AddressItem.FLAG_ORDERED : 0;
        String msg = "";
        if (valueEncrypted) {
            msg = String.valueOf("") + Tools.stringFormat(Re.s(R.string.does_not_support), Re.s(R.string.help_group_search_title), Re.s(R.string.mode_hacking)) + '\n';
        }
        if (sign != 536870912) {
            msg = String.valueOf(msg) + Tools.stringFormat(Re.s(R.string.does_not_support), Re.s(R.string.help_group_search_title), AddressItem.getSignNames().get(sign)) + '\n';
        }
        if (msg.length() > 0) {
            throw new NumberFormatException(msg);
        }
        return startGroupSearch(seq, search.from, search.to, search.flags, search.area, general_flags, memoryFrom, memoryTo, forceNew);
    }

    private static boolean startGroupSearch(byte seq, long[] from, long[] to, int[] flags, int area, int general_flags, long memoryFrom, long memoryTo, boolean forceNew) {
        MainService service = MainService.instance;
        if (service.showBusyDialog()) {
            if (service.mResultCount != 0 && forceNew) {
                service.clear(seq);
            }
            if (service.mResultCount == 0) {
                service.usedFuzzy = false;
                service.mDaemonManager.sendConfig(seq);
            }
            service.lockApp();
            service.showSearchHint = false;
            service.mDaemonManager.searchGroup(seq, general_flags, area, flags, from, to, memoryFrom, memoryTo);
            int lastFlags = 0;
            for (int i : flags) {
                lastFlags |= i;
            }
            MainService.setLastFlags(lastFlags, seq);
            return false;
        }
        throw new NumberFormatException(Re.s(R.string.busy_dialog_fail));
    }

    public static XorMode parseXorMode(String input, boolean forSearch) throws NumberFormatException {
        XorMode ret = null;
        if (!ParserNumbers.isString(input) && ParserNumbers.typeAsm(input) == 0 && (input.indexOf(88) != -1 || input.indexOf(120) != -1)) {
            ret = new XorMode();
            String[] parts = input.split("[Xx]");
            String strX = parts.length == 2 ? parts[1] : "";
            ParserNumbers.Result parsed = ParserNumbers.parse(null, strX, 2, false);
            int x = (int) parsed.value;
            ret.input = parts.length > 0 ? parts[0] : "";
            if (forSearch && (x < 1 || x > 4096)) {
                ParserNumbers.NumberFormatException_ e = new ParserNumbers.NumberFormatException_(String.valueOf(Tools.stringFormat(Re.s(R.string.number_out_of_range), strX)) + " [1; 4096]");
                e.addNumber(strX);
                throw e;
            }
            ret.x = x;
        }
        return ret;
    }

    public static boolean doSearch(String input, int flags, boolean valueEncrypted, int sign, long memoryFrom, long memoryTo, boolean forceNew) throws NumberFormatException {
        String input2 = checkScript(input);
        boolean ret = doSearch((byte) 0, input2, flags, valueEncrypted, sign, memoryFrom, memoryTo, forceNew);
        Record record = MainService.instance.currentRecord;
        if (record != null) {
            if (forceNew) {
                record.write("\ngg.clearResults()\n");
            }
            if (forceNew || MainService.instance.mResultCount == 0) {
                record.write("gg.searchNumber(");
            } else {
                record.write("gg.refineNumber(");
            }
            Script.Consts.logNumberString(record, input2);
            record.write(", ");
            Script.Consts.logConst(record, record.consts.TYPE_, flags);
            record.write(", ");
            record.write(Boolean.toString(valueEncrypted));
            record.write(", ");
            Script.Consts.logConst(record, record.consts.SIGN_, sign);
            record.write(", ");
            Script.Consts.logHex(record, memoryFrom);
            record.write(", ");
            Script.Consts.logHex(record, memoryTo);
            record.write(")\n");
        }
        return ret;
    }

    public static boolean doSearch(byte seq, int flags, byte[] text, long memoryFrom, long memoryTo, boolean forceNew) throws NumberFormatException {
        int flags2 = flags & AddressItem.FLAG_AUTO;
        MainService service = MainService.instance;
        if (service.mResultCount != 0 && !forceNew) {
            flags2 &= MainService.getLastFlags() & AddressItem.FLAG_AUTO;
        }
        if (flags2 == 0) {
            service.clear(seq);
            return true;
        }
        if (service.showBusyDialog()) {
            if (service.mResultCount != 0 && forceNew) {
                service.clear(seq);
            }
            if (service.mResultCount == 0) {
                service.usedFuzzy = false;
                service.mDaemonManager.sendConfig(seq);
            }
            service.lockApp(seq);
            service.showSearchHint = false;
            service.mDaemonManager.searchText(seq, flags2, text, memoryFrom, memoryTo);
            MainService.setLastFlags(flags2, seq);
            return false;
        }
        throw new NumberFormatException(Re.s(R.string.busy_dialog_fail));
    }

    public static boolean doSearch(byte seq, short maxOffset, long memoryFrom, long memoryTo) throws NumberFormatException {
        MainService service = MainService.instance;
        if (service.mResultCount == 0) {
            service.clear(seq);
            return true;
        }
        if (service.showBusyDialog()) {
            service.lockApp(seq);
            service.showSearchHint = false;
            ProcessList.ProcessInfo pi = service.processInfo;
            int flags = (pi == null || !pi.x64) ? 4 : 32;
            service.mDaemonManager.searchPointer(seq, maxOffset, flags, memoryFrom, memoryTo);
            MainService.setLastFlags(flags, seq);
            return false;
        }
        throw new NumberFormatException(Re.s(R.string.busy_dialog_fail));
    }

    public static boolean doSearch(byte seq, String input, int flags, boolean valueEncrypted, int sign, long memoryFrom, long memoryTo, boolean forceNew) throws NumberFormatException {
        int flags2 = flags & AddressItem.FLAG_AUTO;
        int sign2 = sign & AddressItem.FLAG_SIGN;
        MainService service = MainService.instance;
        if (ParserNumbers.isString(input)) {
            boolean ret = doSearch(seq, input.charAt(0) == ';' ? 2 : 1, ParserNumbers.getBytes(input), memoryFrom, memoryTo, forceNew);
            lastTextSearch = input;
            return ret;
        }
        if (input.indexOf(59) != -1) {
            boolean ret2 = doGroupSearch(seq, input, flags2, input.contains("::"), valueEncrypted, sign2, memoryFrom, memoryTo, forceNew);
            return ret2;
        }
        if (input.indexOf(126) != -1 && ParserNumbers.typeAsm(input) == 0) {
            boolean ret3 = doRangeSearch(seq, input, flags2, valueEncrypted, sign2, memoryFrom, memoryTo, forceNew);
            return ret3;
        }
        if (service.mResultCount != 0 && !forceNew) {
            flags2 &= MainService.getLastFlags() & AddressItem.FLAG_AUTO;
        }
        if (flags2 == 0) {
            service.clear(seq);
            return true;
        }
        XorMode xorMode = parseXorMode(input, true);
        int x = xorMode != null ? xorMode.x : 0;
        if (xorMode != null) {
            input = xorMode.input;
        }
        ParserNumbers.Result result = new ParserNumbers.Result();
        long i = AddressItem.dataForSearch(result, input, flags2);
        int flags3 = flags2 & result.type;
        if (Integer.bitCount(flags3) > 1) {
            flags3 = AddressItem.fixAutoFlag(flags3, i, result.isNegative);
        } else if (!valueEncrypted && flags3 != 16 && flags3 != 64 && ((sign2 == 134217728 || sign2 == 67108864) && input.indexOf(45) == -1 && ((1 << ((AddressItem.getSize(flags3) * 8) - 1)) & i) != 0)) {
            Log.d("unsigned search 1: '" + input + "' " + Integer.toHexString(flags3) + ' ' + valueEncrypted + ' ' + Integer.toHexString(sign2));
            if (sign2 == 134217728) {
                input = String.valueOf(input) + "~0";
                sign2 = AddressItem.FLAG_VALUE_NOT_EQUAL;
            } else if (sign2 == 67108864) {
                input = String.valueOf(input) + "~-1";
                sign2 = AddressItem.FLAG_VALUE_EQUAL;
            }
            Log.d("unsigned search 2: '" + input + "' " + Integer.toHexString(flags3) + ' ' + valueEncrypted + ' ' + Integer.toHexString(sign2));
            boolean ret4 = doSearch(seq, input, flags3, valueEncrypted, sign2, memoryFrom, memoryTo, forceNew);
            return ret4;
        }
        int lastFlags = flags3;
        int flags4 = flags3 | ((valueEncrypted && x == 0) ? AddressItem.FLAG_MODE_HACKING : sign2);
        if (x != 0) {
            if (valueEncrypted) {
                throw new NumberFormatException(Tools.stringFormat(Re.s(R.string.does_not_support), Re.s(R.string.help_xor_search_title), Re.s(R.string.mode_hacking)));
            }
            flags4 |= AddressItem.FLAG_MODE_XOR;
        }
        if (service.showBusyDialog()) {
            if (service.mResultCount != 0 && forceNew) {
                service.clear(seq);
            }
            if (service.mResultCount == 0) {
                service.usedFuzzy = false;
                service.mDaemonManager.sendConfig(seq);
            }
            service.lockApp(seq);
            service.showSearchHint = false;
            service.mDaemonManager.searchNumber(seq, i, x, flags4, memoryFrom, memoryTo);
            MainService.setLastFlags(lastFlags, seq);
            return false;
        }
        throw new NumberFormatException(Re.s(R.string.busy_dialog_fail));
    }

    @Override // android.ext.SearchMenuItem
    public void onClickCallback(DialogInterface d, int which) {
        String input = this.searcher.getNumber();
        doSearch(input, which, this.searcher.isModeHacking(), this.searcher.getSign(), this.searcher.getMem(0), this.searcher.getMem(1), this.lastButton == -3);
    }

    public void openSearch(String text) {
        if (text == null) {
            text = "";
        }
        this.lastInput = text;
        getButton().performClick();
    }

    public void searchNearby(String address) {
        getButton().performClick();
        MemoryRange memoryRange = this.searcher.memoryRange;
        memoryRange.setType(2);
        memoryRange.setAddress(address);
    }

    @Override // android.ext.SearchMenuItem, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        this.lastInput = this.searcher.getNumber();
        this.signLastSelect = this.searcher.getSign();
        MainService.lastType = this.searcher.getType();
        super.onDismiss(dialog);
    }
}
