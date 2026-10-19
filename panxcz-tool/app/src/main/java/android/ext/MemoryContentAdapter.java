package android.ext;

import android.content.DialogInterface;
import android.ext.AddressItemSet;
import android.ext.InternalKeyboard;
import android.ext.ProcessList;
import android.ext.RegionList;
import android.ext.Tools;
import android.ext.ViewHolderBase;
import android.fix.LayoutInflater;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
class MemoryContentAdapter extends BaseAdapterLC implements ViewHolderBase.Listener, SectionIndexer, Filterable {
    private static final int AREA = 8192;
    static final int F_ARM = 4;
    static final int F_ARM8 = 6;
    private static final int F_BYTE = 11;
    private static final int F_DOUBLE = 9;
    private static final int F_DWORD = 7;
    private static final int F_FLOAT = 8;
    static final int F_HEX = 0;
    static final int F_JAVA = 3;
    private static final int F_OFFSET = 7;
    private static final int F_QWORD = 12;
    static final int F_RHEX = 1;
    static final int F_STR = 2;
    static final int F_THUMB = 5;
    private static final int F_WORD = 10;
    private static final int F_XOR = 13;
    private static final int MASK_DEFAULT = -15999;
    private static final String MEMORY_EDITOR_FORMAT = "memory-editor-format";
    private static final int PAGES = 2;
    private static final int PAGE_SIZE = 4096;
    private static final int SECTIONS_COUNT = 32;
    private static final String VALUE_SEPARATOR = "\n";
    private static volatile int[] colors;
    private static volatile CharSequence[] shortNames;
    private ByteBuffer buffer;
    private final byte[] data;
    private volatile long dataOffset;
    private long offsetBuffer;
    volatile int showMask;
    private static ArmDis mRet = null;
    private static int[] mArgs = null;
    String filterString = "";
    private Filter filter = null;
    short[] filtered = null;
    private int selected = -1;
    private boolean[] checks = null;
    private OldChecks oldChecks = null;
    private int cacheShift = 2;
    private long cacheShiftCheck = 0;
    private final AddressItemSet.Result result = new AddressItemSet.Result();
    private final CharSequence[] cacheRegion = new CharSequence[2];
    private long cacheRegionOffset = 1;
    private int cacheFlags = 0;
    private int cacheMask = 0;
    private FilterMenuItem filterMenuItem = null;

    private static class OldChecks {
        short[] filtered;
        int shift;

        /* synthetic */ OldChecks(int i, short[] sArr, OldChecks oldChecks) {
            this(i, sArr);
        }

        private OldChecks(int shift, short[] filtered) {
            this.shift = shift;
            this.filtered = filtered;
        }
    }

    public MemoryContentAdapter() {
        this.showMask = MASK_DEFAULT;
        this.showMask = Tools.getSharedPreferences().getInt(MEMORY_EDITOR_FORMAT, MASK_DEFAULT);
        checkCount();
        this.data = new byte[8200];
        this.buffer = ByteBuffer.wrap(this.data);
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
        this.offsetBuffer = 0L;
        this.dataOffset = 0L;
        MainService.instance.mValueFormat.setText(getValueFormat());
    }

    public boolean[] getChecks() {
        return this.checks;
    }

    public int getPossibleTypes(boolean[] list, SparseIntArray counts) {
        int ret = 0;
        int s = list.length - 1;
        for (int i = 1; i < s; i++) {
            if (list[i]) {
                long addr = getAddr(i);
                ret |= AddressItem.getTypeForAddress(addr, false, counts);
            }
        }
        if ((Config.configClient & 1024) != 0) {
            int showFlags = getFlags();
            return (Integer.bitCount(showFlags) != 1 || (ret & showFlags) == 0) ? ret : ret & showFlags;
        }
        return ret;
    }

    private void checkCount() {
        int k;
        int count = getCount();
        boolean[] checks = this.checks;
        OldChecks old = this.oldChecks;
        int shift = getShift();
        short[] filtered = this.filtered;
        if (checks == null || count != checks.length || old == null || shift != old.shift || filtered != this.filtered) {
            boolean[] newChecks = new boolean[count];
            this.checks = newChecks;
            if (old != null) {
                int oldShift = old.shift;
                short[] oldFiltered = old.filtered;
                int oldMask = (1 << oldShift) - 1;
                int count2 = count - 1;
                for (int i = 1; i < count2; i++) {
                    int j = pos2index(i - 1, filtered, shift) << shift;
                    if ((j & oldMask) == 0) {
                        int j2 = j >> oldShift;
                        int k2 = index2pos(j2, oldFiltered);
                        if (j2 == pos2index(k2, oldFiltered, oldShift) && (k = k2 + 1) >= 0 && k < checks.length && checks[k]) {
                            newChecks[i] = true;
                        }
                    }
                }
            }
            this.oldChecks = new OldChecks(shift, filtered, null);
        }
    }

    @Override // android.widget.BaseAdapter
    public void notifyDataSetChanged() {
        getFilter().filter(this.filterString);
        superNotifyDataSetChanged();
    }

    void superNotifyDataSetChanged() {
        checkCount();
        super.notifyDataSetChanged();
    }

    private int getShift() {
        int shift = this.cacheShift;
        int mask = this.showMask;
        long check = 17179869184L | ((long) mask);
        if (check != this.cacheShiftCheck) {
            if ((mask & 2048) != 0) {
                shift = 0;
            } else if ((mask & 1056) != 0) {
                shift = 1;
            } else {
                shift = 2;
            }
            this.cacheShift = shift;
            this.cacheShiftCheck = check;
        }
        return shift;
    }

    public void setBaseAddress(long addr, int top) {
        long offset = addr & (-8192);
        if (offset != this.offsetBuffer) {
            this.offsetBuffer = offset;
            Arrays.fill(this.checks, false);
            notifyDataSetChanged();
        }
        ListView mMemListView = MainService.instance.mMemListView;
        int index = getJumpPos(addr);
        Tools.setSelectionFromTop(mMemListView, index, top);
    }

    private long getOffset() {
        return this.offsetBuffer;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return getCountItems() + 2;
    }

    private int getCountItems() {
        return this.filtered == null ? getRawCountItems() : this.filtered.length;
    }

    int getRawCountItems() {
        return 8192 >> getShift();
    }

    private int pos2index(int pos) {
        return pos2index(pos, this.filtered, getShift());
    }

    private static int pos2index(int pos, short[] filtered, int shift) {
        return (filtered == null || pos < 0) ? pos : pos >= filtered.length ? (pos - filtered.length) + (8192 >> shift) : filtered[pos];
    }

    private int index2pos(int index) {
        return index2pos(index, this.filtered);
    }

    private static int index2pos(int index, short[] filtered) {
        if (filtered == null) {
            return index;
        }
        int found = Arrays.binarySearch(filtered, (short) index);
        if (found < 0) {
            int found2 = (-found) - 2;
            if (found2 < 0) {
                return 0;
            }
            return found2;
        }
        return found;
    }

    private int getOffset(long address) {
        return (int) (8191 & address);
    }

    public long getAddr(int pos) {
        long position = pos2index(pos - 1);
        return getRawAddr(position);
    }

    public long getRawAddr(long index) {
        return (index << getShift()) + getOffset();
    }

    public int getPos(long addr) {
        return index2pos((int) ((addr - getOffset()) >> getShift())) + 1;
    }

    private int getJumpPos(long addr) {
        int pos = getPos(addr);
        this.selected = pos;
        return pos;
    }

    public void loadData() {
        MainService.instance.lockApp();
        MainService.instance.mDaemonManager.getMemory(getOffset(), 8196);
    }

    public void loadData(BufferReader reader) {
        long addr;
        int size;
        long offset = getOffset();
        try {
            reader.reset();
            addr = reader.readLong();
            size = reader.readInt();
        } catch (IOException e) {
            Log.w("Failed load data to memory editor", e);
            Arrays.fill(this.data, (byte) 0);
            return;
        } catch (RuntimeException e2) {
            Log.w("Failed load data to memory editor", e2);
            Arrays.fill(this.data, (byte) 0);
            return;
        }
        if (addr != offset) {
            throw new RuntimeException("Offset mismatch: " + addr + " (" + Long.toHexString(addr).toUpperCase(Locale.US) + ") != " + offset + " (" + Long.toHexString(offset).toUpperCase(Locale.US) + ")");
        }
        if (size < 8192) {
            throw new RuntimeException("Size mismatch: " + size + " (" + Integer.toHexString(size).toUpperCase(Locale.US) + ") != 8192 (" + Integer.toHexString(8192).toUpperCase(Locale.US) + ")");
        }
        try {
            reader.read(this.data, 0, 8192);
        } catch (IOException e3) {
            Log.w("Failed load data to memory editor", e3);
            Arrays.fill(this.data, (byte) 0);
            return;
        }
        this.dataOffset = offset;
        MainService.instance.mMemListView.post(new Runnable() { // from class: android.ext.MemoryContentAdapter.1
            @Override // java.lang.Runnable
            public void run() {
                MemoryContentAdapter.this.notifyDataSetChanged();
            }
        });
    }

    public void reloadAll() {
        loadData();
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton v, boolean isChecked) {
        ViewHolderBase holder = (ViewHolderBase) v.getTag();
        if (holder != null) {
            try {
                this.checks[holder.pos] = isChecked;
                MainService.instance.updateStatusBar();
            } catch (IndexOutOfBoundsException e) {
                Log.e("ArrayAdapter onCheckedChanged failed", e);
            }
            holder.updateBackground(isChecked, this.selected);
        }
    }

    private static void setText(TextView view, CharSequence text) {
        if (text == null) {
            view.setVisibility(8);
        } else {
            view.setVisibility(0);
            view.setText(text);
        }
    }

    private int getSize(boolean goodData, AddressItemSet revertMap, long address, long data, int size, TextView view, int id, int type, int typeSize, int align) {
        String value;
        if ((((long) (align - 1)) & address) == 0 && (this.showMask & (1 << id)) != 0) {
            if (goodData) {
                String val = AddressItem.getStringDataTrim(address, data, type);
                AddressItemSet.Result result = this.result;
                revertMap.get(address, type, result);
                if (result.found) {
                    val = String.valueOf(val) + " (" + AddressItem.getStringData(address, result.data, type) + ')';
                }
                value = String.valueOf(val) + ((Object) AddressItem.getShortName(type)) + ';';
            } else {
                value = Searcher.MASK_PLACEHOLDER;
            }
            setText(view, value);
            return size == 0 ? typeSize : size;
        }
        setText(view, null);
        return size;
    }

    private int getSize(long address, long data, int size, StringBuilder out, int id, int type, int typeSize, int align) {
        if ((((long) (align - 1)) & address) == 0 && (this.showMask & (1 << id)) != 0) {
            String val = AddressItem.getStringDataTrim(address, data, type);
            out.append(val);
            out.append("\n");
            return size == 0 ? typeSize : size;
        }
        return size;
    }

    private long[] getSpecialOffset(boolean last) {
        long ret;
        long offset = getOffset();
        if (!last) {
            ret = offset == 0 ? offset : offset - 1;
        } else {
            offset += 8192;
            ret = offset == 0 ? offset - 1 : offset;
        }
        return new long[]{ret, offset};
    }

    private CharSequence getRegion(long addr) {
        CharSequence[] cacheRegion = this.cacheRegion;
        long offset = getOffset();
        if (this.cacheRegionOffset != offset) {
            this.cacheRegionOffset = offset;
            for (int i = 0; i < cacheRegion.length; i++) {
                cacheRegion[i] = RegionList.getRegion((((long) i) * 4096) + offset);
            }
        }
        for (int i2 = 0; i2 < cacheRegion.length; i2++) {
            if (Tools.unsignedLessOrEqual(addr, ((((long) (i2 + 1)) * 4096) + offset) - 1)) {
                return cacheRegion[i2];
            }
        }
        return "??";
    }

    public void forceUpdateCache() {
        long offset = getOffset();
        this.cacheRegionOffset = offset;
        boolean changed = false;
        for (int i = 0; i < this.cacheRegion.length; i++) {
            CharSequence reg = RegionList.getRegion((((long) i) * 4096) + offset);
            if (!reg.equals(this.cacheRegion[i])) {
                this.cacheRegion[i] = reg;
                changed = true;
            }
        }
        if (changed) {
            notifyDataSetChanged();
        }
    }

    boolean isDataGood() {
        return this.dataOffset == getOffset();
    }

    String getString(ArmDis ret, int[] args, int index) {
        if (!isDataGood()) {
            return "";
        }
        long address = getRawAddr(index);
        long data = this.buffer.getLong(getOffset(address));
        StringBuilder out = new StringBuilder();
        int size = getSize(address, data, getSize(address, data, getSize(address, data, getSize(address, data, getSize(address, data, getSize(address, data, getSize(address, data, 0, out, 9, 64, 8, 4), out, 12, 32, 8, 4), out, 8, 16, 4, 4), out, 7, 4, 4, 4), out, 13, 8, 4, 4), out, 10, 2, 2, 2), out, 11, 1, 1, 1);
        if (size == 0) {
            size = 1 << getShift();
        }
        boolean skipRHex = false;
        if ((this.showMask & 1) != 0) {
            if (size != 8) {
                data &= (1 << (size * 8)) - 1;
            }
            String val = ToolsLight.prefixLongHex(size * 2, data);
            out.append(val);
            out.append("\n");
            skipRHex = size == 1;
        }
        if (!skipRHex && (this.showMask & 2) != 0) {
            long revData = Long.reverseBytes(data) >> (64 - (size * 8));
            if (size != 8) {
                revData &= (1 << (size * 8)) - 1;
            }
            String val2 = ToolsLight.prefixLongHex(size * 2, revData);
            out.append(val2);
            out.append("\n");
        }
        if ((this.showMask & 4) != 0) {
            out.append(longToString(data, size));
            out.append("\n");
        }
        if ((this.showMask & 8) != 0) {
            out.append(longToJava(data, size));
            out.append("\n");
        }
        if ((this.showMask & 16) != 0) {
            out.append(ArmDis.getArmOpcode(ret, address, data));
            out.append("\n");
        }
        if ((this.showMask & 32) != 0) {
            out.append(ArmDis.getThumbOpcode(ret, address, data));
            out.append("\n");
        }
        if ((this.showMask & 64) != 0) {
            Arm64Dis.disasm(args, address, (int) data, out);
            out.append("\n");
        }
        return out.toString();
    }

    private void updateHolder(ViewHolderMemory holder, int pos) {
        boolean checked;
        holder.ch.getLayoutParams().height = (Config.smallItems & (1 << (Tools.isLandscape() + 4))) != 0 ? -2 : Tools.dp2px48();
        if (pos == 0 || pos == getCount() - 1) {
            holder.ch.setVisibility(4);
            holder.address.setVisibility(8);
            holder.valueHex.setVisibility(8);
            holder.valueRHex.setVisibility(8);
            holder.valueString.setVisibility(0);
            holder.valueJava.setVisibility(8);
            holder.valueArm.setVisibility(8);
            holder.valueThumb.setVisibility(8);
            holder.valueArm8.setVisibility(8);
            holder.valueDword.setVisibility(8);
            holder.valueFloat.setVisibility(8);
            holder.valueDouble.setVisibility(8);
            holder.valueWord.setVisibility(8);
            holder.valueByte.setVisibility(8);
            holder.valueQword.setVisibility(8);
            holder.valueXor.setVisibility(8);
            holder.region.setVisibility(8);
            holder.updateBackground(false, -2);
            long[] offset = getSpecialOffset(pos != 0);
            holder.valueString.setText(String.valueOf(Re.s(R.string.goto_address)) + ' ' + AddressItem.getStringAddress(offset[0], 32));
            return;
        }
        holder.ch.setVisibility(0);
        holder.address.setVisibility(0);
        holder.region.setVisibility(0);
        holder.pos = pos;
        long address = getAddr(pos);
        holder.region.setText(getRegion(address));
        holder.address.setText(AddressItem.getStringAddress(address, 32));
        try {
            checked = this.checks[pos];
        } catch (IndexOutOfBoundsException e) {
            Log.e("ArrayAdapter getView failed", e);
            checked = false;
        }
        holder.ch.setChecked(checked);
        holder.updateBackground(checked, this.selected);
        long data = this.buffer.getLong(getOffset(address));
        boolean goodData = isDataGood();
        AddressItemSet revertMap = MainService.instance.revertMap;
        int size = getSize(goodData, revertMap, address, data, getSize(goodData, revertMap, address, data, getSize(goodData, revertMap, address, data, getSize(goodData, revertMap, address, data, getSize(goodData, revertMap, address, data, getSize(goodData, revertMap, address, data, getSize(goodData, revertMap, address, data, 0, holder.valueDouble, 9, 64, 8, 4), holder.valueQword, 12, 32, 8, 4), holder.valueFloat, 8, 16, 4, 4), holder.valueDword, 7, 4, 4, 4), holder.valueXor, 13, 8, 4, 4), holder.valueWord, 10, 2, 2, 2), holder.valueByte, 11, 1, 1, 1);
        if (size == 0) {
            size = 1 << getShift();
        }
        fillData(holder, address, data, this.showMask, goodData, size);
    }

    private static int setText(TextView view, CharSequence text, int size, int min) {
        setText(view, text);
        return (text == null || size >= min) ? size : min;
    }

    static void fillData(ViewHolderBase holder, long address, long data, int showMask, boolean goodData, int size) {
        String str;
        String str2;
        String string;
        String str3;
        String str4;
        String val;
        String val2;
        int[] iArrArgs;
        ArmDis armDis;
        ArmDis armDis2;
        TextView textView = holder.valueThumb;
        if ((1 & address) != 0 || (showMask & 32) == 0) {
            str = null;
        } else {
            if (mRet != null) {
                armDis2 = mRet;
            } else {
                armDis2 = new ArmDis();
                mRet = armDis2;
            }
            str = String.valueOf(ArmDis.getThumbOpcode(armDis2, address, data)) + ';';
        }
        int size2 = setText(textView, str, size, 2);
        TextView textView2 = holder.valueArm;
        if ((3 & address) != 0 || (showMask & 16) == 0) {
            str2 = null;
        } else {
            if (mRet != null) {
                armDis = mRet;
            } else {
                armDis = new ArmDis();
                mRet = armDis;
            }
            str2 = String.valueOf(ArmDis.getArmOpcode(armDis, address, data)) + ';';
        }
        int size3 = setText(textView2, str2, size2, 4);
        TextView textView3 = holder.valueArm8;
        if ((3 & address) != 0 || (showMask & 64) == 0) {
            string = null;
        } else {
            if (mArgs != null) {
                iArrArgs = mArgs;
            } else {
                iArrArgs = Arm64Dis.args();
                mArgs = iArrArgs;
            }
            string = Arm64Dis.disasm(iArrArgs, address, (int) data, new StringBuilder()).append(';').toString();
        }
        int size4 = setText(textView3, string, size3, 4);
        boolean skipRHex = false;
        if ((showMask & 1) != 0) {
            RegionList.Region region = null;
            if (goodData) {
                if (size4 != 8) {
                    data &= (1 << (size4 * 8)) - 1;
                }
                val2 = String.valueOf(ToolsLight.prefixLongHex(size4 * 2, data)) + "h;";
                ProcessList.ProcessInfo processInfo = MainService.instance.processInfo;
                region = (size4 == 4 || (processInfo != null && processInfo.x64 && size4 == 8)) ? RegionList.getRegionItem(data) : null;
            } else {
                val2 = Searcher.MASK_PLACEHOLDER;
            }
            setText(holder.valueHex, val2);
            holder.valueHex.setTextColor(RegionList.Region.getPointerColor(region));
            skipRHex = size4 == 1;
        } else {
            setText(holder.valueHex, null);
        }
        if (!skipRHex && (showMask & 2) != 0) {
            if (goodData) {
                long revData = Long.reverseBytes(data) >> (64 - (size4 * 8));
                if (size4 != 8) {
                    revData &= (1 << (size4 * 8)) - 1;
                }
                val = String.valueOf(ToolsLight.prefixLongHex(size4 * 2, revData)) + "r;";
            } else {
                val = Searcher.MASK_PLACEHOLDER;
            }
            setText(holder.valueRHex, val);
        } else {
            setText(holder.valueRHex, null);
        }
        TextView textView4 = holder.valueString;
        if ((showMask & 4) == 0) {
            str3 = null;
        } else {
            str3 = goodData ? String.valueOf(SearchMenuItem.HEX_UTF_8) + longToString(data, size4) + "';" : Searcher.MASK_PLACEHOLDER;
        }
        setText(textView4, str3);
        TextView textView5 = holder.valueJava;
        if ((showMask & 8) == 0) {
            str4 = null;
        } else {
            str4 = goodData ? String.valueOf(SearchMenuItem.HEX_UTF_16LE) + longToJava(data, size4) + "\";" : Searcher.MASK_PLACEHOLDER;
        }
        setText(textView5, str4);
    }

    private View getNewView(ViewGroup parent) {
        View view = LayoutInflater.inflateStatic(R.layout.service_memory_item, parent, false);
        ViewHolderMemory viewHolder = new ViewHolderMemory(view, this);
        viewHolder.valueDword = (TextView) view.findViewById(R.id.value_dword);
        viewHolder.valueFloat = (TextView) view.findViewById(R.id.value_float);
        viewHolder.valueDouble = (TextView) view.findViewById(R.id.value_double);
        viewHolder.valueWord = (TextView) view.findViewById(R.id.value_word);
        viewHolder.valueByte = (TextView) view.findViewById(R.id.value_byte);
        viewHolder.valueQword = (TextView) view.findViewById(R.id.value_qword);
        viewHolder.valueXor = (TextView) view.findViewById(R.id.value_xor);
        return view;
    }

    @Override // android.widget.Adapter
    public View getView(int pos, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null || !(view.getTag() instanceof ViewHolderMemory)) {
            view = getNewView(parent);
        }
        ViewHolderMemory holder = (ViewHolderMemory) view.getTag();
        if (holder.wrongOrientation()) {
            view = getNewView(parent);
            holder = (ViewHolderMemory) view.getTag();
        }
        updateHolder(holder, pos);
        return view;
    }

    public static String longToString(long in, int size) {
        byte[] b = new byte[8];
        b[0] = (byte) ((255 & in) >> 0);
        b[1] = (byte) ((65280 & in) >> 8);
        b[2] = (byte) ((16711680 & in) >> 16);
        b[3] = (byte) ((4278190080L & in) >> 24);
        b[4] = (byte) ((1095216660480L & in) >> 32);
        b[5] = (byte) ((280375465082880L & in) >> 40);
        b[6] = (byte) ((71776119061217280L & in) >> 48);
        b[7] = (byte) (((-72057594037927936L) & in) >> 56);
        for (int i = 0; i < b.length; i++) {
            if (b[i] < 32) {
                b[i] = Message.CMD_CS_CLEAR_LOCK_LIST;
            }
        }
        return new String(b).substring(0, size);
    }

    public static String longToJava(long in, int size) {
        byte[] bytes = ParserNumbers.getBytes(in);
        int i = bytes[8];
        if (i > size) {
            i = size;
        }
        if ((i & 1) == 0) {
            i++;
        }
        return new String(bytes, 0, i + 1, ParserNumbers.getCharset(true));
    }

    @Override // android.widget.Adapter
    public Object getItem(int pos) {
        int flags;
        if (pos == 0 || pos == getCount() - 1) {
            long[] offset = getSpecialOffset(pos != 0);
            GotoAddress ret = new GotoAddress(offset[0], offset[1]);
            return ret;
        }
        long address = getAddr(pos);
        int flags2 = AddressItem.getTypeForAddress(address, false);
        int showFlags = getFlags();
        if ((flags2 & showFlags) != 0) {
            flags = flags2 & showFlags;
        } else {
            flags = AddressItem.getTypeForAddress(address, true);
        }
        long data = this.buffer.getLong(getOffset(address));
        return new AddressItem(address, data, flags);
    }

    @Override // android.widget.Adapter
    public long getItemId(int pos) {
        return getAddr(pos);
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return false;
    }

    void updateShowMask(int mask) {
        if (mask != this.showMask) {
            int oldShift = getShift();
            Tools.ScrollPos scrollPos = Tools.getScrollPos(MainService.instance.mMemListView);
            long oldAddr = getAddr(scrollPos.top);
            long oldPos = this.selected < 0 ? -1L : getAddr(this.selected);
            this.showMask = mask;
            new SPEditor().putInt(MEMORY_EDITOR_FORMAT, this.showMask, MASK_DEFAULT).commit();
            notifyDataSetChanged();
            if (oldShift != getShift()) {
                scrollPos.top = getPos(oldAddr);
                Tools.setScrollPos(MainService.instance.mMemListView, scrollPos);
                if (this.selected >= 0) {
                    this.selected = getPos(oldPos);
                }
            }
            MainService.instance.mValueFormat.setText(getValueFormat());
        }
    }

    private int getFlags() {
        int mask = this.showMask;
        if (mask != this.cacheMask) {
            int[] list = {0, 0, 0, 0, 4, 2, 4, 4, 16, 64, 2, 1, 32, 8};
            int ret = 0;
            for (int i = 0; i < list.length; i++) {
                if (((1 << i) & mask) != 0) {
                    ret |= list[i];
                }
            }
            this.cacheMask = mask;
            this.cacheFlags = ret;
        }
        return this.cacheFlags;
    }

    private CharSequence getValueFormat() {
        return getValueFormat(this.showMask);
    }

    static CharSequence getValueFormat(int mask) {
        CharSequence[] list = getShortNames();
        CharSequence[] out = new CharSequence[list.length * 2];
        int used = 0;
        for (int i = 0; i < list.length; i++) {
            if (((1 << i) & mask) != 0) {
                if (used > 0) {
                    out[used] = ",";
                    used++;
                }
                out[used] = list[i];
                used++;
            }
        }
        return Tools.concat((CharSequence[]) Arrays.copyOf(out, used));
    }

    private static CharSequence[] getShortNames() {
        if (shortNames == null) {
            CharSequence[] names = new CharSequence[14];
            names[0] = Re.s(R.string.hex);
            names[1] = Re.s(R.string.rhex);
            names[2] = "S";
            names[3] = "J";
            names[4] = "A";
            names[5] = "T";
            names[6] = "A8";
            names[7] = Re.s(R.string.type_dword_short);
            names[8] = Re.s(R.string.type_float_short);
            names[9] = Re.s(R.string.type_double_short);
            names[10] = Re.s(R.string.type_word_short);
            names[11] = Re.s(R.string.type_byte_short);
            names[12] = Re.s(R.string.type_qword_short);
            names[13] = Re.s(R.string.type_xor_short);
            int[] colors2 = getColors();
            for (int i = 0; i < names.length; i++) {
                names[i] = Tools.colorize(names[i], colors2[i]);
            }
            shortNames = names;
            return names;
        }
        return shortNames;
    }

    static int[] getColors() {
        if (colors != null) {
            return colors;
        }
        int exec = Tools.getColor(R.color.region_code);
        int execThumb = Tools.getColor(R.color.region_system_code);
        int[] iArr = {-1, -1, -1, -1, exec, execThumb, exec, AddressItem.getColor(4), AddressItem.getColor(16), AddressItem.getColor(64), AddressItem.getColor(2), AddressItem.getColor(1), AddressItem.getColor(32), AddressItem.getColor(8)};
        colors = iArr;
        return iArr;
    }

    static CharSequence[] getNames() {
        return new CharSequence[]{Re.s(R.string.format_hex), Re.s(R.string.format_rhex), Re.s(R.string.format_string), "UTF-16LE", Tools.stringFormat(Re.s(R.string.format_asm_opcodes), "ARM (x32)"), Tools.stringFormat(Re.s(R.string.format_asm_opcodes), "Thumb"), Tools.stringFormat(Re.s(R.string.format_asm_opcodes), "ARM (x64)"), Re.s(R.string.type_dword), Re.s(R.string.type_float), Re.s(R.string.type_double), Re.s(R.string.type_word), Re.s(R.string.type_byte), Re.s(R.string.type_qword), Re.s(R.string.type_xor)};
    }

    public void chooseValueFormat() {
        final boolean savedList = MainService.instance.getTabIndex() == 2;
        CharSequence[] list = getNames();
        int mask = this.showMask;
        if (savedList) {
            list = (CharSequence[]) Arrays.copyOf(list, 7);
            mask = SavedListAdapter.showMask;
        }
        final boolean[] checked = new boolean[list.length];
        CharSequence[] shortNames2 = getShortNames();
        int[] colors2 = getColors();
        for (int i = 0; i < list.length; i++) {
            list[i] = Tools.colorize(((Object) shortNames2[i]) + ": " + ((Object) list[i]), colors2[i]);
            checked[i] = ((1 << i) & mask) != 0;
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { // from class: android.ext.MemoryContentAdapter.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                int newMask = savedList ? 0 : MemoryContentAdapter.MASK_DEFAULT;
                if (which == -1) {
                    for (int i2 = 0; i2 < checked.length; i2++) {
                        if (checked[i2]) {
                            newMask |= 1 << i2;
                        } else {
                            newMask &= (1 << i2) ^ (-1);
                        }
                    }
                }
                if (!savedList && (((1 << checked.length) - 1) & newMask) == 0) {
                    newMask |= 8;
                }
                if (savedList) {
                    SavedListAdapter.setShowMask(newMask);
                } else {
                    MemoryContentAdapter.this.updateShowMask(newMask);
                }
            }
        };
        Alert.show(Alert.create(Tools.getDarkContext()).setCustomTitle(Tools.getCustomTitle(R.string.value_format)).setMultiChoiceItems(list, checked, new DialogInterface.OnMultiChoiceClickListener() { // from class: android.ext.MemoryContentAdapter.3
            @Override // android.content.DialogInterface.OnMultiChoiceClickListener
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checked[which] = isChecked;
            }
        }).setPositiveButton(Re.s(R.string.yes), listener).setNeutralButton(Re.s(R.string.reset_button), listener).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null));
    }

    @Override // android.widget.SectionIndexer
    public Object[] getSections() {
        Object[] ret = new Object[32];
        for (int i = 0; i < 32; i++) {
            ret[i] = AddressItem.getStringAddress(this.offsetBuffer + (((long) i) * 256), 4);
        }
        return ret;
    }

    @Override // android.widget.SectionIndexer
    public int getPositionForSection(int sectionIndex) {
        return getPos(this.offsetBuffer + (((long) sectionIndex) * 256));
    }

    @Override // android.widget.SectionIndexer
    public int getSectionForPosition(int pos) {
        long addr = getAddr(pos);
        int ret = (int) ((addr - getOffset()) / 256);
        if (ret < 0) {
            ret = 0;
        }
        if (ret >= 32) {
            return 31;
        }
        return ret;
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        Filter filter = this.filter;
        if (filter == null) {
            Filter filter2 = new MemoryFilter(this, null);
            this.filter = filter2;
            return filter2;
        }
        return filter;
    }

    private class MemoryFilter extends Filter {
        int[] mArgs;
        ArmDis mRet;

        private MemoryFilter() {
            this.mRet = null;
            this.mArgs = null;
        }

        /* synthetic */ MemoryFilter(MemoryContentAdapter memoryContentAdapter, MemoryFilter memoryFilter) {
            this();
        }

        @Override // android.widget.Filter
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            ArmDis ret;
            int[] args;
            int j;
            Filter.FilterResults results = new Filter.FilterResults();
            results.values = null;
            try {
                boolean noText = (MemoryContentAdapter.this.showMask & 12) == 0;
                if (constraint != null && constraint.length() > 0 && MemoryContentAdapter.this.isDataGood()) {
                    String check = constraint.toString();
                    if (noText) {
                        check = prepareNoText(check);
                    }
                    String check2 = Tools.quoteFilter(check);
                    Log.d("Filter: '" + check2 + SearchMenuItem.HEX_UTF_8);
                    Pattern p = Pattern.compile(check2, 8);
                    if ((MemoryContentAdapter.this.showMask & 48) == 0) {
                        ret = null;
                    } else if (this.mRet != null) {
                        ret = this.mRet;
                    } else {
                        ret = new ArmDis();
                        this.mRet = ret;
                    }
                    if ((MemoryContentAdapter.this.showMask & 64) == 0) {
                        args = null;
                    } else if (this.mArgs != null) {
                        args = this.mArgs;
                    } else {
                        args = Arm64Dis.args();
                        this.mArgs = args;
                    }
                    int rawCountItems = (short) MemoryContentAdapter.this.getRawCountItems();
                    short[] filtered = new short[rawCountItems];
                    short i = 0;
                    int j2 = 0;
                    while (i < rawCountItems) {
                        String str = MemoryContentAdapter.this.getString(ret, args, i);
                        if (noText) {
                            str = prepareNoText(str);
                        }
                        if (p.matcher(str).find()) {
                            j = j2 + 1;
                            filtered[j2] = i;
                        } else {
                            j = j2;
                        }
                        i = (short) (i + 1);
                        j2 = j;
                    }
                    results.values = Arrays.copyOf(filtered, j2);
                }
            } catch (Throwable e) {
                Log.d("Failed filter", e);
                ExceptionHandler.sendException(Thread.currentThread(), e, false);
            }
            return results;
        }

        private String prepareNoText(String in) {
            return in.toLowerCase(Locale.US).replace(ParserNumbers.thousandSeparator, "");
        }

        @Override // android.widget.Filter
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            MemoryContentAdapter.this.filtered = (short[]) results.values;
            MemoryContentAdapter.this.superNotifyDataSetChanged();
        }
    }

    public MenuItem getFilterMenuItem() {
        FilterMenuItem filterMenuItem = this.filterMenuItem;
        if (filterMenuItem == null) {
            FilterMenuItem filterMenuItem2 = new FilterMenuItem();
            this.filterMenuItem = filterMenuItem2;
            return filterMenuItem2;
        }
        return filterMenuItem;
    }

    private class FilterMenuItem extends MenuItem implements DialogInterface.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher {
        boolean ext;
        private WeakReference<EditText> weakEdit;
        private WeakReference<CheckBox> weakEnds;
        private WeakReference<CheckBox> weakStarts;

        public FilterMenuItem() {
            super(R.string.filter, R.drawable.ic_filter_white_24dp);
            this.weakEdit = new WeakReference<>(null);
            this.weakStarts = new WeakReference<>(null);
            this.weakEnds = new WeakReference<>(null);
            this.ext = false;
        }

        @Override // android.ext.MenuItem, android.view.View.OnClickListener
        public void onClick(View view) {
            if (view != null && view.getId() == R.id.message) {
                Alert.show(Alert.create().setMessage(String.valueOf(Re.s(R.string.examples)) + ":\n" + Re.s(R.string.memory_filter_examples)).setPositiveButton(Re.s(R.string.help), new DialogInterface.OnClickListener() { // from class: android.ext.MemoryContentAdapter.FilterMenuItem.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        ConfigListAdapter.showHelp(R.string.help_memory_filter);
                    }
                }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
                return;
            }
            View v = LayoutInflater.inflateStatic(R.layout.memory_filter, (ViewGroup) null);
            TextView message = (TextView) v.findViewById(R.id.message);
            Tools.setButtonHelpBackground(message);
            message.setOnClickListener(this);
            CheckBox external = (CheckBox) v.findViewById(R.id.external);
            external.setChecked(this.ext);
            external.setText(Re.s(R.string.keyboard_external));
            external.setOnCheckedChangeListener(this);
            CheckBox starts = (CheckBox) v.findViewById(R.id.starts);
            starts.setOnCheckedChangeListener(this);
            this.weakStarts = new WeakReference<>(starts);
            CheckBox ends = (CheckBox) v.findViewById(R.id.ends);
            ends.setOnCheckedChangeListener(this);
            this.weakEnds = new WeakReference<>(ends);
            String filter = MemoryContentAdapter.this.filterString;
            if (filter == null) {
                filter = "";
            }
            View.OnFocusChangeListener focus = new InternalKeyboard.FocusListener() { // from class: android.ext.MemoryContentAdapter.FilterMenuItem.2
                @Override // android.ext.InternalKeyboard.FocusListener
                protected boolean useExternal(View v2, boolean hasFocus) {
                    return FilterMenuItem.this.ext;
                }
            };
            EditText edit = (EditText) v.findViewById(R.id.filter);
            edit.setText(filter);
            edit.setDataType(3);
            Tools.setOnFocusChangeListener(edit, focus);
            edit.addTextChangedListener(this);
            this.weakEdit = new WeakReference<>(edit);
            afterTextChanged(edit.getText());
            ((HexConverter) v.findViewById(R.id.number_converter)).setTag(edit);
            v.setTag(new InternalKeyboard.Flags(15));
            Alert.show(Alert.create().setView(InternalKeyboard.getView(v)).setPositiveButton(R.string.ok, this).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).setNeutralButton(R.string.clear, this).create(), edit);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            String filter;
            if (which == -1) {
                EditText edit = this.weakEdit.get();
                if (edit != null) {
                    filter = edit.getText().toString();
                    if (filter.length() > 0) {
                        History.add(filter, this.ext ? 2 : 1);
                    }
                } else {
                    return;
                }
            } else {
                filter = "";
            }
            MemoryContentAdapter.this.filterString = filter;
            MainService.instance.mFilter.setText(filter.length() > 0 ? String.valueOf(Tools.stripColon(R.string.filter)) + ": " + filter : Re.s(R.string.no_filter));
            MemoryContentAdapter.this.notifyDataSetChanged();
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            EditText edit;
            if (buttonView != null && (edit = this.weakEdit.get()) != null) {
                switch (buttonView.getId()) {
                    case R.id.starts /* 2131427395 */:
                        String text = edit.getText().toString();
                        if (text.contains("^") != isChecked) {
                            if (isChecked) {
                                edit.getText().replace(0, 0, "^");
                            } else {
                                int st = text.indexOf("^");
                                edit.getText().replace(st, "^".length() + st, "");
                            }
                        }
                        break;
                    case R.id.ends /* 2131427396 */:
                        String text2 = edit.getText().toString();
                        if (text2.contains("$") != isChecked) {
                            if (isChecked) {
                                edit.getText().replace(text2.length(), text2.length(), "$");
                            } else {
                                int st2 = text2.indexOf("$");
                                edit.getText().replace(st2, "$".length() + st2, "");
                            }
                        }
                        break;
                    case R.id.external /* 2131427397 */:
                        this.ext = isChecked;
                        View.OnFocusChangeListener focus = edit.getOnFocusChangeListener();
                        if (focus != null) {
                            focus.onFocusChange(edit, true);
                        }
                        break;
                }
            }
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            CheckBox starts = this.weakStarts.get();
            if (starts != null) {
                starts.setChecked(str.indexOf(94) != -1);
                CheckBox ends = this.weakEnds.get();
                if (ends != null) {
                    ends.setChecked(str.indexOf(36) != -1);
                }
            }
        }
    }
}
