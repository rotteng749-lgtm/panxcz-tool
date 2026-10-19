package android.ext;

import android.content.Context;
import android.content.DialogInterface;
import android.fix.Button;
import android.fix.SparseArray;
import android.util.AttributeSet;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class SystemSpinner extends Button implements View.OnClickListener, DialogInterface.OnClickListener {
    protected SparseArray<CharSequence> data;
    protected DialogInterface.OnClickListener mListener;
    protected int selected;

    public SystemSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.data = null;
    }

    public SystemSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.data = null;
    }

    public SystemSpinner(Context context) {
        super(context);
        this.data = null;
    }

    public void setData(SparseArray<CharSequence> data) {
        if (data != null) {
            this.data = data;
            int size = data.size();
            if (size != 0) {
                this.selected = data.keyAt(size - 1);
                setOnClickListener(this);
            }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        SparseArray<CharSequence> data = this.data;
        if (data != null && data.size() != 0) {
            onClick();
        }
    }

    protected void onClick() {
        SparseArray<CharSequence> data = this.data;
        int size = data.size();
        CharSequence[] arr = new CharSequence[size];
        for (int i = 0; i < size; i++) {
            arr[i] = data.valueAt((size - 1) - i);
        }
        Alert.show(Alert.create(getContext()).setSingleChoiceItems(arr, (size - 1) - data.indexOfKey(this.selected), this));
    }

    public void onClick(DialogInterface dialog, int which) {
        setSelected(this.data.keyAt((this.data.size() - 1) - which));
        Tools.dismiss(dialog);
    }

    public void setOnItemSelectedListener(DialogInterface.OnClickListener listener) {
        this.mListener = listener;
    }

    public void setSelected(int code) {
        SparseArray<CharSequence> data = this.data;
        if (data != null) {
            CharSequence name = data.get(code);
            if (name == null) {
                if (data.size() == 1) {
                    name = data.valueAt(0);
                    code = data.keyAt(0);
                } else {
                    name = "???";
                }
            }
            setText(name);
            this.selected = code;
            if (this.mListener != null) {
                this.mListener.onClick(null, code);
            }
        }
    }

    public int getSelected() {
        return this.selected;
    }
}
