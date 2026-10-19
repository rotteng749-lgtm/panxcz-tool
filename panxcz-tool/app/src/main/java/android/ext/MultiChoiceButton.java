package android.ext;

import android.content.Context;
import android.content.DialogInterface;
import android.fix.SparseArray;
import android.os.Build;
import android.util.AttributeSet;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class MultiChoiceButton extends SystemSpinner implements DialogInterface.OnMultiChoiceClickListener {
    protected int current;

    public MultiChoiceButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MultiChoiceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiChoiceButton(Context context) {
        super(context);
    }

    @Override // android.ext.SystemSpinner
    protected void onClick() {
        SparseArray<CharSequence> data = this.data;
        int size = data.size();
        CharSequence[] arr = new CharSequence[size];
        boolean[] sel = new boolean[arr.length];
        this.current = 0;
        for (int i = 0; i < size; i++) {
            int key = data.keyAt((size - 1) - i);
            arr[i] = data.valueAt((size - 1) - i);
            boolean isChecked = (this.selected & key) != 0;
            sel[i] = isChecked;
            onClick(null, i, isChecked);
        }
        Alert.show(Alert.create(Tools.getDarkContext(getContext())).setMultiChoiceItems(arr, sel, this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).setNeutralButton(Re.s(R.string.reset_button), this).setPositiveButton(Re.s(R.string.ok), this));
    }

    @Override // android.content.DialogInterface.OnMultiChoiceClickListener
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (isChecked) {
            this.current |= 1 << which;
        } else {
            this.current &= (1 << which) ^ (-1);
        }
    }

    @Override // android.ext.SystemSpinner, android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        int size = this.data.size();
        int sel = 0;
        if (which == -1) {
            for (int i = 0; i < size; i++) {
                if ((this.current & (1 << i)) != 0) {
                    sel |= this.data.keyAt((size - 1) - i);
                }
            }
        }
        setSelected(sel);
        Tools.dismiss(dialog);
    }

    @Override // android.ext.SystemSpinner
    public void setSelected(int code) {
        SparseArray<CharSequence> data = this.data;
        if (data != null) {
            int size = data.size();
            CharSequenceBuilder out = new CharSequenceBuilder();
            for (int i = 0; i < size; i++) {
                int key = data.keyAt((size - 1) - i);
                if ((code & key) != 0) {
                    if (out.size() != 0) {
                        out.append(",");
                    }
                    CharSequence full = data.valueAt((size - 1) - i);
                    out.append(full.subSequence(0, full.toString().indexOf(58)));
                }
            }
            if (Build.VERSION.SDK_INT < 11) {
                Tools.setButtonBackground(this, Tools.getDrawable(R.drawable.btn_default_normal_holo_dark));
            }
            setText(out.toCharSequence());
            this.selected = code;
            if (this.mListener != null) {
                this.mListener.onClick(null, code);
            }
        }
    }
}
