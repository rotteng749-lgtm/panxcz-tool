package android.ext;

import android.ext.AddressItemSet;
import android.ext.ViewHolderSearch;
import android.fix.LayoutInflater;
import android.sup.ArrayListResults;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import catch_.me_.if_.you_.can_.R;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class AddressArrayAdapter extends BaseAdapterIndexer implements ViewHolderSearch.Listener {
    private static final int MASK_DEFAULT = 0;
    private static final String SEARCH_RESULTS_FORMAT = "search-results-format";
    static volatile int showMask = 0;
    private ArrayListResults mList;
    private final AddressItemSet.Result result = new AddressItemSet.Result();
    private AddressItem tmp = new AddressItem();

    public AddressArrayAdapter(ArrayListResults mAddressList) {
        this.mList = mAddressList;
        showMask = Tools.getSharedPreferences().getInt(SEARCH_RESULTS_FORMAT, 0);
    }

    static void setShowMask(int val) {
        showMask = val;
        new SPEditor().putInt(SEARCH_RESULTS_FORMAT, val, 0).commit();
    }

    private View getNewView(ViewGroup parent) {
        View view = LayoutInflater.inflateStatic(R.layout.service_address_item, parent, false);
        new ViewHolderSearch(view, this);
        return view;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || view.getTag() == null) {
            view = getNewView(parent);
        }
        boolean smallItems = (Config.smallItems & (1 << (Tools.isLandscape() + 0))) != 0;
        ViewHolderSearch holder = (ViewHolderSearch) view.getTag();
        if (holder.wrongOrientation()) {
            view = getNewView(parent);
            holder = (ViewHolderSearch) view.getTag();
        }
        holder.pos = position;
        holder.remove.setTag(Integer.valueOf(position));
        holder.type.setTag(Integer.valueOf(position));
        AddressItem item = null;
        boolean checked = false;
        ArrayListResults mList = this.mList;
        if (mList.size() > position) {
            try {
                item = mList.get(position, this.tmp);
                checked = mList.checked(position);
            } catch (IndexOutOfBoundsException e) {
                item = null;
            }
        }
        holder.info.setOrientation(smallItems ? 0 : 1);
        SavedListAdapter.sizeRemove(holder.remove, smallItems);
        holder.ch.setChecked(checked);
        holder.ch.getLayoutParams().height = smallItems ? -2 : Tools.dp2px48();
        holder.updateBackground(checked, -2);
        if (item != null) {
            int color = item.getColor();
            holder.value.setTextColor(color);
            holder.type.setTextColor(color);
            String data = item.getStringData();
            AddressItemSet.Result result = this.result;
            MainService.instance.revertMap.get(item.address, item.flags, result);
            if (result.found) {
                data = String.valueOf(data) + " (" + AddressItem.getStringData(item.address, result.data, item.flags) + ')';
            }
            holder.address.setText(item.getStringAddress());
            holder.value.setText(data);
            holder.type.setText(item.getShortName());
            holder.region.setText(RegionList.getRegion(item.address));
            MemoryContentAdapter.fillData(holder, item.address, item.data, showMask, true, item.getSize());
        } else {
            holder.address.setText("null");
            holder.value.setText("null");
            holder.type.setText("null");
            holder.region.setText("null");
            MemoryContentAdapter.fillData(holder, 0L, 0L, 0, false, 0);
        }
        return view;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        Integer position = (Integer) v.getTag();
        if (position != null) {
            try {
                AddressItem item = this.mList.get(position.intValue(), this.tmp);
                ArrayList<AddressItem> items = new ArrayList<>();
                items.add(item);
                MainService.instance.mDaemonManager.remove(items);
            } catch (IndexOutOfBoundsException e) {
                Log.e("ArrayAdapter onClick Failed", e);
            }
            MainService.instance.refreshResultList(false);
        }
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton v, boolean isChecked) {
        ViewHolderBase holder = (ViewHolderBase) v.getTag();
        if (holder != null) {
            try {
                this.mList.checked(holder.pos, isChecked);
                MainService.instance.updateStatusBar();
            } catch (IndexOutOfBoundsException e) {
                Log.e("ArrayAdapter onCheckedChanged failed", e);
            }
            holder.updateBackground(isChecked, -2);
        }
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return false;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mList.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        try {
            return this.mList.get(position, null);
        } catch (IndexOutOfBoundsException e) {
            Log.w("Failed get item in memory list", e);
            return null;
        }
    }
}
