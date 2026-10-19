package android.ext;

import android.content.Context;
import android.util.AttributeSet;

/* JADX INFO: loaded from: classes.dex */
public class EditTextSpeeds extends EditTextMulti {
    private OnChangedListener changedListener;

    public interface OnChangedListener {
        void onSelectionChanged(EditText editText, int i, int i2);

        void onTextChanged(EditText editText, CharSequence charSequence, int i, int i2, int i3);
    }

    public EditTextSpeeds(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.changedListener = null;
    }

    public EditTextSpeeds(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.changedListener = null;
    }

    public EditTextSpeeds(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.changedListener = null;
    }

    public EditTextSpeeds(Context context) {
        super(context);
        this.changedListener = null;
    }

    @Override // android.widget.TextView
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        OnChangedListener changedListener = this.changedListener;
        if (changedListener != null) {
            changedListener.onSelectionChanged(this, selStart, selEnd);
        }
    }

    @Override // android.widget.TextView
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        OnChangedListener changedListener = this.changedListener;
        if (changedListener != null) {
            changedListener.onTextChanged(this, text, start, lengthBefore, lengthAfter);
        }
    }

    public void setOnChangedListener(OnChangedListener listener) {
        this.changedListener = listener;
    }
}
