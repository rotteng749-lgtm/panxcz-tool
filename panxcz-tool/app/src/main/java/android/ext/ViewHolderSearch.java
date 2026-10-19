package android.ext;

import android.ext.ViewHolderBase;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: compiled from: AddressArrayAdapter.java */
/* JADX INFO: loaded from: classes.dex */
class ViewHolderSearch extends ViewHolderBase {
    final LinearLayout info;
    final ImageView remove;
    final TextView type;
    final TextView value;

    /* JADX INFO: compiled from: AddressArrayAdapter.java */
    interface Listener extends ViewHolderBase.Listener, View.OnClickListener {
    }

    ViewHolderSearch(View view, Listener listener) {
        super(view, listener);
        this.value = (TextView) view.findViewById(R.id.addr_item_value);
        this.info = (LinearLayout) view.findViewById(R.id.addr_item_info);
        this.type = (TextView) view.findViewById(R.id.addr_item_type);
        this.remove = Config.setIconSize((ImageView) view.findViewById(R.id.addr_item_remove));
        this.remove.setOnClickListener(listener);
        this.remove.setOnLongClickListener(listener);
    }
}
