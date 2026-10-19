package android.ext;

import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseAdapterLC extends BaseAdapter implements View.OnLongClickListener {
    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        ListView listView;
        int pos;
        if (view == null || (listView = Tools.getListView(view)) == null || (pos = listView.getPositionForView(view)) == -1) {
            return false;
        }
        AdapterView.OnItemLongClickListener listener = listView.getOnItemLongClickListener();
        if (listener != null) {
            listener.onItemLongClick(listView, view, pos, getItemId(pos));
        }
        return true;
    }
}
