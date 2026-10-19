package android.ext;

import android.content.DialogInterface;
import android.ext.ProcessList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ItemContextMenu implements AdapterView.OnItemLongClickListener {
    private static final ThreadLocal<Long> lastArmAddr = new ThreadLocal<>();
    private final int inMemoryEditor;
    private final int[] list = {0, 4, 16, 64, 2, 1, 32, 8};
    private final String[] cache = new String[this.list.length];
    private final String[] hexCache = new String[this.list.length];
    private final String[] rhexCache = new String[this.list.length];

    public ItemContextMenu(int inMemoryEditor) {
        this.inMemoryEditor = inMemoryEditor;
    }

    public static long setLastArmAddr(long addr) {
        lastArmAddr.set(Long.valueOf(addr));
        return addr;
    }

    @Override // android.widget.AdapterView.OnItemLongClickListener
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        int canBePointer;
        int showMask;
        Object hold = parent.getItemAtPosition(position);
        if (!(hold instanceof AddressItem)) {
            return false;
        }
        final AddressItem item = (AddressItem) hold;
        SavedItem savedItem = new SavedItem(item);
        String address = savedItem.getStringAddress();
        long addr = savedItem.address;
        int flags = savedItem.flags & AddressItem.FLAG_AUTO;
        long data = savedItem.data;
        ProcessList.ProcessInfo processInfo = MainService.instance.processInfo;
        int size = this.inMemoryEditor == 3 ? 8 : AddressItem.getSize(flags);
        if ((3 & addr) != 0 || size < 4) {
            canBePointer = 0;
        } else {
            canBePointer = (processInfo == null || !processInfo.x64 || size != 8 || ((-4294967296L) & data) == 0) ? 1 : 2;
        }
        boolean canBeHex = (flags & (-81)) != 0;
        final ArrayList<String> texts = new ArrayList<>();
        List<String> items = new ArrayList<>();
        final List<Integer> icons = new ArrayList<>();
        texts.add(address);
        items.add(Re.s(R.string.offset_calculator));
        icons.add(Integer.valueOf(R.drawable.ic_calculator_white_24dp));
        texts.add(address);
        items.add(Re.s(R.string.help_pointer_search_title));
        icons.add(Integer.valueOf(R.drawable.ic_pointer_white_24dp));
        texts.add(address);
        items.add(Re.s(R.string.search_nearby));
        icons.add(Integer.valueOf(R.drawable.ic_search_nearby_white_24dp));
        if (parent != MainService.instance.mMemListView) {
            texts.add(address);
            items.add(String.valueOf(Re.s(R.string.goto_address)) + ' ' + address);
            icons.add(Integer.valueOf(R.drawable.ic_forward_white_24dp));
        }
        int i = 0;
        while (i < canBePointer) {
            int pointer = i == 0 ? 4 : 32;
            String pointerValue = AddressItem.getStringHexData(addr, data, pointer);
            texts.add(pointerValue);
            items.add(String.valueOf(Re.s(R.string.goto_pointer)) + ' ' + pointerValue);
            icons.add(Integer.valueOf(R.drawable.ic_forward_white_24dp));
            long page = data & (-4096);
            if (page != 0 && RegionList.getRegionItem(page) == null) {
                String pointerValue2 = AddressItem.getStringHexData(addr, page, pointer);
                texts.add(pointerValue2);
                items.add(String.valueOf(Tools.stripColon(R.string.allocate_page)) + ": " + pointerValue2);
                icons.add(Integer.valueOf(R.drawable.ic_code_braces_white_24dp));
            }
            i++;
        }
        long page2 = addr & (-4096);
        if (page2 != 0 && RegionList.getRegionItem(page2) == null) {
            String pageAddress = AddressItem.getStringAddress(page2, 4);
            texts.add(pageAddress);
            items.add(String.valueOf(Tools.stripColon(R.string.allocate_page)) + ": " + pageAddress);
            icons.add(Integer.valueOf(R.drawable.ic_code_braces_white_24dp));
        }
        texts.add(address);
        items.add(String.valueOf(Re.s(R.string.copy_address)) + ' ' + address);
        icons.add(Integer.valueOf(R.drawable.ic_content_copy_white_24dp));
        String opArm = null;
        String opThumb = null;
        String opArm64 = null;
        int tab = MainService.instance.getTabIndex();
        if (tab == 1) {
            showMask = AddressArrayAdapter.showMask;
        } else if (tab == 3) {
            showMask = MainService.instance.mMemListAdapter.showMask;
        } else {
            showMask = tab == 2 ? SavedListAdapter.showMask : 0;
        }
        if (showMask != 0) {
            int i2 = 0;
            while (i2 < 3) {
                if ((i2 != 0 || ((3 & addr) == 0 && (showMask & 16) != 0)) && ((i2 != 1 || ((1 & addr) == 0 && (showMask & 32) != 0)) && (i2 != 2 || ((3 & addr) == 0 && (showMask & 64) != 0)))) {
                    lastArmAddr.set(0L);
                    if (i2 == 0) {
                        opArm = ArmDis.getArmOpcode((ArmDis) null, addr, data);
                    }
                    if (i2 == 1) {
                        opThumb = ArmDis.getThumbOpcode((ArmDis) null, addr, data);
                    }
                    if (i2 == 2) {
                        opArm64 = Arm64Dis.disasm(Arm64Dis.args(), addr, (int) data, new StringBuilder()).toString();
                    }
                    long last = lastArmAddr.get().longValue();
                    if (last != 0) {
                        String prefix = i2 == 0 ? "ARM (x32)" : null;
                        if (i2 == 1) {
                            prefix = "Thumb";
                        }
                        if (i2 == 2) {
                            prefix = "ARM (x64)";
                        }
                        String prefix2 = String.valueOf(prefix) + ": ";
                        String hexLast = AddressItem.getStringAddress(last, 4);
                        texts.add(hexLast);
                        items.add(String.valueOf(prefix2) + Re.s(R.string.goto_address) + ' ' + hexLast);
                        icons.add(Integer.valueOf(R.drawable.ic_forward_white_24dp));
                        texts.add(hexLast);
                        items.add(String.valueOf(prefix2) + Re.s(R.string.copy_address) + ' ' + hexLast);
                        icons.add(Integer.valueOf(R.drawable.ic_content_copy_white_24dp));
                        long page3 = last & (-4096);
                        if (page3 != 0 && RegionList.getRegionItem(page3) == null) {
                            String pageAddress2 = AddressItem.getStringAddress(page3, 4);
                            texts.add(pageAddress2);
                            items.add(String.valueOf(prefix2) + Tools.stripColon(R.string.allocate_page) + ": " + pageAddress2);
                            icons.add(Integer.valueOf(R.drawable.ic_code_braces_white_24dp));
                        }
                    }
                }
                i2++;
            }
        }
        int used = 0;
        int[] list = this.list;
        String[] cache = this.cache;
        String[] hexCache = this.hexCache;
        String[] rhexCache = this.rhexCache;
        for (int check : list) {
            if (check == 0 || (flags & check) != 0) {
                if (check == 0) {
                    check = flags;
                }
                String value = AddressItem.getStringDataTrim(addr, data, check);
                boolean found = false;
                int j = 0;
                while (true) {
                    if (j >= used) {
                        break;
                    }
                    if (!cache[j].equals(value)) {
                        j++;
                    } else {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    cache[used] = value;
                    if (canBeHex) {
                        hexCache[used] = (check & (-81)) == 0 ? null : AddressItem.getStringHexData(addr, data, check);
                        rhexCache[used] = (check & (-81)) == 0 ? null : AddressItem.getStringRhexData(addr, data, check);
                    }
                    used++;
                }
            }
        }
        if (used > 0) {
            for (int i3 = 0; i3 < used; i3++) {
                texts.add(cache[i3]);
                items.add(String.valueOf(Re.s(R.string.copy_value)) + ' ' + cache[i3]);
                icons.add(Integer.valueOf(R.drawable.ic_content_copy_white_24dp));
                if (canBeHex && hexCache[i3] != null) {
                    texts.add(hexCache[i3]);
                    items.add(String.valueOf(Re.s(R.string.copy_hex_value)) + ' ' + hexCache[i3]);
                    icons.add(Integer.valueOf(R.drawable.ic_content_copy_white_24dp));
                    texts.add(rhexCache[i3]);
                    items.add(String.valueOf(Re.s(R.string.copy_rhex_value)) + ' ' + rhexCache[i3]);
                    icons.add(Integer.valueOf(R.drawable.ic_content_copy_white_24dp));
                }
            }
        }
        for (int i4 = 0; i4 < 3; i4++) {
            String op = null;
            String name = null;
            if (i4 == 0) {
                op = opArm;
                name = "ARM (x32)";
            }
            if (i4 == 1) {
                op = opThumb;
                name = "Thumb";
            }
            if (i4 == 2) {
                op = opArm64;
                name = "ARM (x64)";
            }
            if (op != null) {
                String op2 = op.trim();
                int pos = op2.indexOf(59);
                if (pos != -1) {
                    op2 = op2.substring(0, pos).trim();
                }
                if (op2.length() > 0) {
                    texts.add(op2);
                    items.add(String.valueOf(Tools.stringFormat(Re.s(R.string.copy_opcode), name)) + ' ' + op2);
                    icons.add(Integer.valueOf(R.drawable.ic_content_copy_white_24dp));
                }
            }
        }
        if (parent == MainService.instance.mSavedListView) {
            texts.add(address);
            items.add(Re.s(R.string.change_address_type));
            icons.add(Integer.valueOf(R.drawable.ic_cursor_mover_white_24dp));
        }
        ListAdapter adapter = new ArrayAdapter<String>(MainService.context, items) { // from class: android.ext.ItemContextMenu.1
            @Override // android.ext.ArrayAdapter, android.widget.ArrayAdapter, android.widget.Adapter
            public View getView(int position2, View convertView, ViewGroup parent2) {
                View v = super.getView(position2, convertView, parent2);
                Drawable icon = Tools.getDrawable(((Integer) icons.get(position2)).intValue());
                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                if (tv != null) {
                    Tools.setTextAppearance(tv, R.style.ListItemText);
                    Tools.addIconToTextView(tv, icon, Config.getIconSize());
                }
                return v;
            }
        };
        Alert.show(Alert.create(Tools.getDarkContext()).setAdapter(adapter, new DialogInterface.OnClickListener() { // from class: android.ext.ItemContextMenu.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                if (which < 0 || which >= icons.size()) {
                    Log.e("Unknown index in menu: " + which + " for " + icons.size());
                    return;
                }
                switch (((Integer) icons.get(which)).intValue()) {
                    case R.drawable.ic_calculator_white_24dp /* 2130837520 */:
                        new OffsetCalculator(getSource()).show((String) texts.get(which));
                        break;
                    case R.drawable.ic_code_braces_white_24dp /* 2130837525 */:
                        Allocate.show((String) texts.get(which));
                        break;
                    case R.drawable.ic_content_copy_white_24dp /* 2130837526 */:
                        Tools.copyText((String) texts.get(which));
                        break;
                    case R.drawable.ic_cursor_mover_white_24dp /* 2130837529 */:
                        AddressEditor.onClick((String) texts.get(which));
                        break;
                    case R.drawable.ic_forward_white_24dp /* 2130837546 */:
                        MainService.instance.goToAddress(getSource(), (String) texts.get(which), String.valueOf(Tools.stripColon(R.string.from_context)) + ": (" + item + ')');
                        break;
                    case R.drawable.ic_pointer_white_24dp /* 2130837568 */:
                        new PointerButtonListener().show((String) texts.get(which));
                        break;
                    case R.drawable.ic_search_nearby_white_24dp /* 2130837574 */:
                        MainService.instance.searchListener.searchNearby((String) texts.get(which));
                        break;
                    default:
                        Log.e("Unknown icon in menu: " + Integer.toHexString(((Integer) icons.get(which)).intValue()));
                        break;
                }
                if (dialog != null) {
                    Tools.dismiss(dialog);
                }
            }

            private Long getSource() {
                if (ItemContextMenu.this.inMemoryEditor != 3 || item == null) {
                    return null;
                }
                return Long.valueOf(item.address);
            }
        }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
        return true;
    }
}
