package android.ext;

import android.R;
import android.content.Context;
import android.content.DialogInterface;
import android.ext.AddressItem;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public class SystemSpinnerType extends SystemSpinner {
    AddressItem.Pair[] pairs;

    public SystemSpinnerType(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SystemSpinnerType(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SystemSpinnerType(Context context) {
        super(context);
    }

    @Override // android.ext.SystemSpinner
    protected void onClick() {
        this.pairs = AddressItem.getOrderedPairs(this.data, null);
        int sel = 0;
        int i = 0;
        while (true) {
            if (i >= this.pairs.length) {
                break;
            }
            if (this.pairs[i].flags != this.selected) {
                i++;
            } else {
                sel = i;
                break;
            }
        }
        Alert.show(Alert.create(Tools.getDarkContext(getContext())).setSingleChoiceItems(new ArrayAdapter<AddressItem.Pair>(MainService.context, R.layout.select_dialog_singlechoice, R.id.text1, this.pairs) { // from class: android.ext.SystemSpinnerType.1
            @Override // android.ext.ArrayAdapter, android.widget.ArrayAdapter, android.widget.Adapter
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView) v.findViewById(R.id.text1);
                if (tv != null) {
                    Tools.setTextAppearance(tv, catch_.me_.if_.you_.can_.R.style.SmallText);
                    if (position > 0 && position < SystemSpinnerType.this.pairs.length && SystemSpinnerType.this.pairs[position] != null) {
                        tv.setTextColor(AddressItem.getColor(SystemSpinnerType.this.pairs[position].flags));
                    }
                }
                return v;
            }
        }, sel, this));
    }

    @Override // android.ext.SystemSpinner, android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        setSelected(this.pairs[which].flags);
        Tools.dismiss(dialog);
    }

    @Override // android.ext.SystemSpinner
    public void setSelected(int code) {
        super.setSelected(code);
        if (Build.VERSION.SDK_INT < 11) {
            Tools.setButtonBackground(this, Tools.getDrawable(catch_.me_.if_.you_.can_.R.drawable.btn_default_normal_holo_dark));
        }
        setTextColor(AddressItem.getColor(code));
    }
}
