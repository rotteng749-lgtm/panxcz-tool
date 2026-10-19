package android.ext;

import android.content.DialogInterface;
import android.ext.AddressItemSet;
import android.ext.ViewHolderSearch;
import android.fix.LayoutInflater;
import android.sup.LongSparseArrayChecked;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SavedListAdapter extends BaseAdapterIndexer implements ListAdapter, ViewHolderSearch.Listener {
    static final int MASK_DEFAULT = 0;
    private static final String SAVED_LIST_FORMAT = "saved-list-format";
    static volatile int showMask = 0;
    private final AddressItemSet.Result result = new AddressItemSet.Result();
    private final LongSparseArrayChecked<SavedItem> list = new LongSparseArrayChecked<>();

    static class ViewHolderSaved extends ViewHolderSearch {
        final ImageView freeze;
        final TextView name;

        ViewHolderSaved(View view, ViewHolderSearch.Listener listener) {
            super(view, listener);
            this.freeze = Config.setIconSize((ImageView) view.findViewById(R.id.freeze));
            this.freeze.setOnClickListener(listener);
            this.freeze.setOnLongClickListener(listener);
            this.name = (TextView) view.findViewById(R.id.name);
        }
    }

    public SavedListAdapter() {
        showMask = Tools.getSharedPreferences().getInt(SAVED_LIST_FORMAT, 0);
    }

    static void updateMask() {
        String fill;
        TextView mSavedFormat = MainService.instance.mSavedFormat;
        if (mSavedFormat != null) {
            if (showMask == 0) {
                fill = "   ";
            } else {
                fill = Integer.bitCount(showMask) == 1 ? "  " : "";
            }
            mSavedFormat.setText(Tools.concat("F: ", MemoryContentAdapter.getValueFormat(showMask), fill));
        }
    }

    static void setShowMask(int val) {
        showMask = val;
        new SPEditor().putInt(SAVED_LIST_FORMAT, val, 0).commit();
        updateMask();
        MainService.instance.savedList.notifyDataSetChanged();
    }

    public LongSparseArrayChecked<SavedItem> getList() {
        return this.list;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.list.size();
    }

    @Override // android.widget.Adapter
    public SavedItem getItem(int position) {
        return this.list.valueAt(position);
    }

    public SavedItem getItemByAddress(long addr) {
        SavedItem item = this.list.get(addr);
        if (item == null) {
            return null;
        }
        return item;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return false;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        SavedItem item = this.list.valueAt(position);
        if (item == null) {
            return 0L;
        }
        return item.getAddress();
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton v, boolean isChecked) {
        ViewHolderBase holder = (ViewHolderBase) v.getTag();
        if (holder != null) {
            try {
                this.list.setCheckAt(holder.pos, isChecked);
                MainService.instance.updateStatusBar();
            } catch (IndexOutOfBoundsException e) {
                Log.e("ArrayAdapter onCheckedChanged failed", e);
            }
            holder.updateBackground(isChecked, -2);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.address /* 2131427401 */:
                ViewHolderBase holder = (ViewHolderBase) v.getTag();
                if (holder != null) {
                    Tools.click(holder.ch);
                }
                break;
            case R.id.addr_item_remove /* 2131427446 */:
                final SavedItem selected = (SavedItem) v.getTag();
                if (selected != null) {
                    try {
                        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(selected.getName())).setMessage(Re.s(R.string.delete_prompt)).setPositiveButton(Re.s(R.string.yes), new DialogInterface.OnClickListener() { // from class: android.ext.SavedListAdapter.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                SavedListAdapter.this.remove(selected);
                            }
                        }).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null));
                    } catch (IndexOutOfBoundsException e) {
                        Log.e("SavedListAdapter onClick Failed", e);
                        return;
                    }
                }
                break;
            case R.id.freeze /* 2131427585 */:
                SavedItem item = ((SavedItem) v.getTag()).copy();
                item.freeze = !item.freeze;
                add(item);
                if (v instanceof ImageButton) {
                    ((ImageButton) v).setImageResource(item.getFreezeImageResource());
                }
                MainService.instance.mDaemonManager.doWaited();
                break;
        }
    }

    private View getNewView(ViewGroup parent) {
        View view = LayoutInflater.inflateStatic(R.layout.service_saved_item, parent, false);
        new ViewHolderSaved(view, this);
        return view;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        SavedItem item;
        boolean checked;
        if (convertView == null) {
            view = getNewView(parent);
        } else {
            view = convertView;
        }
        ViewHolderSaved holder = (ViewHolderSaved) view.getTag();
        if (holder.wrongOrientation()) {
            view = getNewView(parent);
            holder = (ViewHolderSaved) view.getTag();
        }
        boolean smallItems = (Config.smallItems & (1 << (Tools.isLandscape() + 2))) != 0;
        if (position >= this.list.size()) {
            item = new SavedItem(0L, 0L, 4, "null for " + position);
            checked = false;
        } else {
            item = this.list.valueAt(position);
            checked = this.list.checkAt(position);
        }
        holder.pos = position;
        sizeRemove(holder.freeze, smallItems);
        holder.name.getLayoutParams().width = smallItems ? -2 : -1;
        holder.info.setOrientation(smallItems ? 0 : 1);
        sizeRemove(holder.remove, smallItems);
        holder.ch.getLayoutParams().height = smallItems ? -2 : Tools.dp2px48();
        holder.freeze.setTag(item);
        if (holder.ch.isChecked() != checked) {
            holder.ch.setOnCheckedChangeListener(null);
            holder.ch.setChecked(checked);
        }
        holder.ch.setOnCheckedChangeListener(this);
        holder.type.setTag(item);
        holder.remove.setTag(item);
        holder.updateBackground(checked, -2);
        int color = item.getColor();
        holder.value.setTextColor(color);
        holder.type.setTextColor(color);
        String data = item.getStringDataTrim();
        AddressItemSet.Result result = this.result;
        MainService.instance.revertMap.get(item.address, item.flags, result);
        if (result.found) {
            data = String.valueOf(data) + " (" + AddressItem.getStringData(item.address, result.data, item.flags) + ')';
        }
        holder.name.setText(item.getName());
        holder.type.setText(item.getShortName());
        holder.region.setText(RegionList.getRegion(item.address));
        holder.address.setText(String.valueOf(item.getStringAddress()) + ": ");
        holder.value.setText(data);
        holder.freeze.setImageResource(item.getFreezeImageResource());
        MemoryContentAdapter.fillData(holder, item.address, item.data, showMask, true, item.getSize());
        return view;
    }

    public static void sizeRemove(ImageView remove, boolean smallItems) {
        ViewGroup.LayoutParams lp = remove.getLayoutParams();
        int dp = smallItems ? 24 : 48;
        int is = Config.getIconSize();
        if (is > dp) {
            dp = is;
        }
        int px = (int) Tools.dp2px(dp);
        lp.height = px;
        Config.setIconSize(remove, px, px, is);
    }

    public void add(SavedItem item) {
        add(item, (byte) 0, true);
    }

    public void add(SavedItem item, byte checked, boolean notify) {
        DaemonManager daemon = MainService.instance.mDaemonManager;
        if ((item.flags & AddressItem.FLAG_ALTER_ADD) == 1073741824) {
            item = item.copy();
        }
        if (item.freeze) {
            item.flags &= -1073741825;
            daemon.addForFreeze(item);
        } else if ((item.flags & AddressItem.FLAG_ALTER_ADD) == 1073741824) {
            item.flags &= -1073741825;
        }
        long addr = item.getAddress();
        SavedItem prev = this.list.put(addr, item, checked);
        if (prev != null && prev.freeze && !item.freeze) {
            daemon.addForUnfreeze(item);
        }
        if (notify) {
            notifyDataSetChanged();
        }
    }

    public void removeAddr(long addr) {
        int position = this.list.indexOfKey(addr);
        if (position >= 0) {
            remove(position);
        }
    }

    public void remove(SavedItem item) {
        removeAddr(item.getAddress());
    }

    public void remove(long[] addrs, int cnt) {
        LongSparseArrayChecked<SavedItem> list = this.list;
        DaemonManager daemon = MainService.instance.mDaemonManager;
        for (int i = 0; i < cnt; i++) {
            SavedItem previous = list.delete(addrs[i]);
            if (previous != null && previous.freeze) {
                daemon.addForUnfreeze(previous);
            }
        }
        daemon.doWaited();
        if (list.size() == 0) {
            truncate();
        }
        notifyDataSetChanged();
    }

    public void remove(List<SavedItem> remove) {
        SavedItem previous;
        LongSparseArrayChecked<SavedItem> list = this.list;
        DaemonManager daemon = MainService.instance.mDaemonManager;
        for (SavedItem item : remove) {
            if (item != null && (previous = list.delete(item.getAddress())) != null && previous.freeze) {
                daemon.addForUnfreeze(previous);
            }
        }
        daemon.doWaited();
        if (list.size() == 0) {
            truncate();
        }
        notifyDataSetChanged();
    }

    public void remove(int position) {
        SavedItem previous = this.list.removeAt(position);
        if (previous != null) {
            if (previous.freeze) {
                DaemonManager daemon = MainService.instance.mDaemonManager;
                daemon.addForUnfreeze(previous);
                daemon.doWaited();
            }
            if (this.list.size() == 0) {
                truncate();
            }
            notifyDataSetChanged();
        }
    }

    public void clear() {
        MainService.instance.mDaemonManager.clearLockList();
        truncate();
        notifyDataSetChanged();
    }

    private void truncate() {
        this.list.truncate();
        MainService.instance.mDaemonManager.inOut.truncate();
    }

    public void loadData(BufferReader reader) {
        SavedItem item;
        reader.reset();
        boolean updated = false;
        ListView listview = MainService.instance.mSavedListView;
        int first = listview.getFirstVisiblePosition();
        int last = listview.getLastVisiblePosition();
        LongSparseArrayChecked<SavedItem> list = this.list;
        while (true) {
            try {
                int flags = reader.readInt();
                if (flags == 0) {
                    break;
                }
                long address = reader.readLong();
                long value = reader.readLongLong();
                int position = list.indexOfKey(address);
                if (position >= 0 && (item = list.valueAt(position)) != null && item.data != value) {
                    if (first <= position && position <= last) {
                        updated = true;
                    }
                    item.data = value;
                }
            } catch (IOException e) {
                Log.e("???", e);
            }
        }
        if (updated) {
            notifyDataSetChanged();
        }
    }

    public void reloadData() {
        DaemonManager daemon = MainService.instance.mDaemonManager;
        daemon.doWaited();
        LongSparseArrayChecked<SavedItem> list = this.list;
        if (list.size() != 0) {
            try {
                ArrayList<AddressItem> used = new ArrayList<>(list.size());
                for (int i = 0; i < list.size(); i++) {
                    try {
                        SavedItem item = list.valueAt(i);
                        if (item != null) {
                            used.add(item);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        Log.w("list changed", e);
                    }
                }
                if (used.size() != 0) {
                    int cnt = used.size();
                    int[] flags = new int[cnt];
                    long[] addresses = new long[cnt];
                    for (int i2 = 0; i2 < cnt; i2++) {
                        AddressItem item2 = used.get(i2);
                        flags[i2] = item2.flags;
                        addresses[i2] = item2.address;
                    }
                    daemon.getMemoryItems(flags, addresses);
                }
            } catch (OutOfMemoryError e2) {
                Log.w("OOM in reloadData", e2);
            }
        }
    }
}
