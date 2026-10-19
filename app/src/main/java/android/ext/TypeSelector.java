package android.ext;

import android.R;
import android.content.DialogInterface;
import android.ext.AddressItem;
import android.fix.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public class TypeSelector implements DialogInterface.OnClickListener {
    private final DialogInterface.OnClickListener listener;
    final AddressItem.Pair[] pairs;
    private final String value;

    public TypeSelector(SparseArray<CharSequence> types, String value, String message, DialogInterface.OnClickListener listener) {
        this(types, null, value, message, listener);
    }

    public TypeSelector(SparseArray<CharSequence> types, SparseIntArray counts, String value, String message, DialogInterface.OnClickListener listener) {
        this.pairs = AddressItem.getOrderedPairs(types, counts);
        this.value = value;
        this.listener = listener;
        if (types.size() == 1) {
            onClick(null, 0);
        } else {
            Alert.show(Alert.create(Tools.getDarkContext()).setCustomTitle(Tools.getCustomTitle(message)).setAdapter(new ArrayAdapter<AddressItem.Pair>(MainService.context, this.pairs) { // from class: android.ext.TypeSelector.1
                @Override // android.ext.ArrayAdapter, android.widget.ArrayAdapter, android.widget.Adapter
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    TextView tv = (TextView) v.findViewById(R.id.text1);
                    if (tv != null) {
                        Tools.setTextAppearance(tv, catch_.me_.if_.you_.can_.R.style.SmallText);
                        if (position >= 0 && position < TypeSelector.this.pairs.length) {
                            AddressItem.Pair pair = TypeSelector.this.pairs[position];
                            if (pair != null) {
                                tv.setTextColor(AddressItem.getColor(TypeSelector.this.pairs[position].flags));
                            }
                        }
                    }
                    return v;
                }
            }, this).setNegativeButton(Re.s(catch_.me_.if_.you_.can_.R.string.cancel), (DialogInterface.OnClickListener) null));
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        try {
            int flags = this.pairs[which].flags;
            if (!"0".equals(this.value)) {
                AddressItem.dataFromString(0L, this.value, flags);
            }
            if (dialog != null) {
                Tools.dismiss(dialog);
            }
            this.listener.onClick(dialog, flags);
        } catch (NumberFormatException e) {
            SearchMenuItem.inputError(e, null);
        }
    }
}
