package android.ext;

import android.content.Context;
import android.fix.ImageButtonView;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import catch_.me_.if_.you_.can_.R;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import luaj.LuaTable;

/* JADX INFO: loaded from: classes.dex */
public class RegionList extends ImageButtonView implements View.OnClickListener {
    private static final int MODE_EXECUTE = 1024;
    private static final int MODE_MASK = 3840;
    private static final int MODE_READ = 256;
    private static final int MODE_SHARED = 2048;
    private static final int MODE_WRITE = 512;
    private static final int STATE_C_BSS = 16;
    private static final int STATE_MASK = 255;
    private static final int STATE_SYS_C_BSS = 2048;
    static int[] colors;
    private static int[] map;
    static CharSequence[] shortValues;
    private static volatile List<Region> sList = new ArrayList();
    private static volatile Region last = null;
    private static volatile SparseIntArray sSizes = new SparseIntArray();

    private static void loadMaps() {
        Config.get(R.id.config_ranges).asString();
    }

    public static void setMaps(int[] map_, CharSequence[] shortValues_, int[] colors_) {
        map = map_;
        shortValues = shortValues_;
        colors = colors_;
    }

    public static class Region {
        final long end;
        final int flags;
        String internal;
        String name;
        final long start;
        private static SparseIntArray colors = null;
        private static int colorDefault = LuaTable.MAX_LEN;

        public Region(int flags, long start, long end) {
            this.flags = flags;
            this.start = start;
            this.end = end;
        }

        public String getType() {
            int type = this.flags;
            return String.valueOf((type & 256) == 256 ? "r" : "-") + ((type & 512) == 512 ? "w" : "-") + ((type & 1024) == 1024 ? "x" : "-") + ((type & 2048) == 2048 ? "s" : "p");
        }

        public CharSequence getState() {
            return RegionList.shortValues[this.flags & 255];
        }

        public String getName() {
            return this.name;
        }

        public String getInternalName() {
            return this.internal == null ? this.name : String.valueOf(this.internal) + ":bss";
        }

        public String toString() {
            return ((Object) RegionList.shortValues[this.flags & 255]) + ": " + AddressItem.getStringAddress(this.start, 4) + '-' + AddressItem.getStringAddress(this.end, 4) + ' ' + getType() + " '" + this.name + SearchMenuItem.HEX_UTF_8;
        }

        public CharSequence toCharSequence() {
            return Tools.colorize(toString(), RegionList.colors[this.flags & 255]);
        }

        private int getPointerType() {
            int type = this.flags;
            if ((type & 1536) == 1536) {
                return 1;
            }
            if ((type & 1024) == 1024) {
                return 2;
            }
            if ((type & 512) == 512) {
                return 16;
            }
            if ((type & 256) == 256) {
                return 8;
            }
            return 4;
        }

        public static int getPointerColor(Region region) {
            int out = 4;
            try {
                SparseIntArray cl = colors;
                if (cl == null) {
                    cl = new SparseIntArray(8);
                    cl.append(4, Tools.getColor(R.color.pointer_unknown));
                    cl.append(1, Tools.getColor(R.color.pointer_executable_writable));
                    cl.append(2, Tools.getColor(R.color.pointer_executable));
                    cl.append(16, Tools.getColor(R.color.pointer_writable));
                    cl.append(8, Tools.getColor(R.color.pointer_readable));
                    colors = cl;
                    colorDefault = Tools.getColor(R.color.pointer_unknown);
                }
                if (region != null) {
                    out = cl.get(region.getPointerType());
                }
                if (out == 4) {
                    int out2 = colorDefault;
                    return out2;
                }
                return out;
            } catch (Throwable e) {
                Log.e("Failed getPointerColor for: " + region, e);
                int out3 = colorDefault;
                return out3;
            }
        }
    }

    private static int getState(int state) {
        int ret = 0;
        int len = map.length;
        for (int i = 0; i < len; i++) {
            int val = map[i];
            if ((val & state) == state) {
                ret = i;
            }
        }
        return ret;
    }

    public static CharSequence getRegion(long addr) {
        Region region = getRegionItem(addr);
        return region == null ? Searcher.MASK_PLACEHOLDER : region.getState();
    }

    public static Region getRegionItem(long addr) {
        Region check = last;
        if (check == null || !Tools.unsignedLessOrEqual(check.start, addr) || !Tools.unsignedLess(addr, check.end)) {
            List<Region> list = sList;
            int low = 0;
            int high = list.size() - 1;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                Region midVal = list.get(mid);
                if (Tools.unsignedLessOrEqual(midVal.end, addr)) {
                    low = mid + 1;
                } else if (Tools.unsignedLess(addr, midVal.start)) {
                    high = mid - 1;
                } else {
                    last = midVal;
                    return midVal;
                }
            }
            return null;
        }
        return check;
    }

    public static Region getRegionItem(String access, String name, long offset) {
        String access2 = access.trim();
        String name2 = name.trim();
        if (name2.length() > 0 && offset >= 0) {
            List<Region> list = sList;
            Region[] matches = new Region[6];
            int[] diff = new int[matches.length];
            File file = new File(name2);
            String base = String.valueOf('/') + file.getName();
            String halfBase = null;
            if (file.getParentFile() != null) {
                halfBase = String.valueOf('/') + file.getParentFile().getName() + base;
            }
            int type = getType(access2);
            Iterator<Region> it = list.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Region region = it.next();
                if (region != null) {
                    String regionName = region.getInternalName();
                    if (regionName.length() != 0 && region.end - region.start > offset) {
                        int bits = (region.flags ^ type) & MODE_MASK;
                        if (bits != 0) {
                            bits = Integer.bitCount(bits);
                        }
                        if (regionName.equals(name2)) {
                            if (bits == 0) {
                                matches[0] = region;
                                break;
                            }
                            if (matches[1] == null || diff[1] > bits) {
                                matches[1] = region;
                                diff[1] = bits;
                            }
                        }
                        if (halfBase != null && regionName.endsWith(halfBase)) {
                            if (bits == 0) {
                                matches[2] = region;
                            } else if (matches[3] == null || diff[3] > bits) {
                                matches[3] = region;
                                diff[3] = bits;
                            }
                        }
                        if (regionName.endsWith(base)) {
                            if (bits == 0) {
                                matches[4] = region;
                            } else if (matches[5] == null || diff[5] > bits) {
                                matches[5] = region;
                                diff[5] = bits;
                            }
                        }
                    }
                }
            }
            for (Region region2 : matches) {
                if (region2 != null) {
                    return region2;
                }
            }
        }
        return null;
    }

    public static int getType(String type) {
        if (type.length() != 4) {
            return 0;
        }
        int ret = type.charAt(0) == 'r' ? 0 | 256 : 0;
        if (type.charAt(1) == 'w') {
            ret |= 512;
        }
        if (type.charAt(2) == 'x') {
            ret |= 1024;
        }
        return type.charAt(3) == 's' ? ret | 2048 : ret;
    }

    public static void loadData(BufferReader reader) {
        int len = -1;
        boolean badNameLength = false;
        reader.reset();
        ArrayList<Region> list = new ArrayList<>();
        SparseIntArray sizes = new SparseIntArray();
        List<Region> old = sList;
        if (old != null) {
            list.ensureCapacity(old.size());
        }
        loadMaps();
        String lastName = "";
        while (true) {
            try {
                int state = reader.readInt();
                if (state == 0) {
                    break;
                }
                int st = getState(state);
                long start = reader.readLongLong();
                long end = reader.readLongLong();
                int flags = st | (reader.readInt() << 8);
                if ((state & 2064) == 0) {
                    lastName = null;
                }
                len = reader.readInt();
                if (len < 0 || len > 1024) {
                    badNameLength = true;
                    break;
                }
                Region region = getRegionItem(start);
                String name = reader.readString(len, region == null ? null : region.name).trim().intern();
                if (region == null || region.flags != flags || region.start != start || region.end != end) {
                    region = new Region(flags, start, end);
                }
                region.name = name;
                region.internal = lastName;
                lastName = name;
                list.add(region);
                long diff = (region.end - region.start) / 4096;
                if (diff < 0) {
                    diff = 0;
                }
                sizes.put(st, sizes.get(st) + ((int) diff));
            } catch (IOException e) {
                Log.e("???", e);
            }
        }
        if (badNameLength) {
            Log.e("Bad name length: " + len, new RuntimeException());
        }
        sList = list;
        sSizes = sizes;
        last = null;
        MainService.instance.mMemListAdapter.forceUpdateCache();
    }

    public static long getSize(int type) {
        return 4096 * ((long) sSizes.get(type));
    }

    public RegionList(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public RegionList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RegionList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RegionList(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnClickListener(this);
        if (!isInEditMode()) {
            setImageResource(R.drawable.ic_arrow_down_drop_circle_white_24dp);
            Config.setIconSize(this);
        }
    }

    public static List<Region> getList() {
        return sList;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        onClick(v, this instanceof RegionListFiltered ? Config.usedRanges : 0);
    }

    /* JADX WARN: Removed duplicated region for block: B:38:0x00d3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onClick(android.view.View r32, int r33) {
        /*
            Method dump skipped, instruction units count: 338
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.ext.RegionList.onClick(android.view.View, int):void");
    }
}
