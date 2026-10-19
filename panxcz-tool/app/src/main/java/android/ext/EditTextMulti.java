package android.ext;

import android.content.Context;
import android.util.AttributeSet;

/* JADX INFO: loaded from: classes.dex */
public class EditTextMulti extends EditText {
    public EditTextMulti(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setInputType(getInputType() | 262144);
        setHorizontallyScrolling(false);
        setMaxLines(Integer.MAX_VALUE);
    }

    public EditTextMulti(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setInputType(getInputType() | 262144);
        setHorizontallyScrolling(false);
        setMaxLines(Integer.MAX_VALUE);
    }

    public EditTextMulti(Context context, AttributeSet attrs) {
        super(context, attrs);
        setInputType(getInputType() | 262144);
        setHorizontallyScrolling(false);
        setMaxLines(Integer.MAX_VALUE);
    }

    public EditTextMulti(Context context) {
        super(context);
        setInputType(getInputType() | 262144);
        setHorizontallyScrolling(false);
        setMaxLines(Integer.MAX_VALUE);
    }

    @Override // android.widget.TextView
    public void setHorizontallyScrolling(boolean whether) {
        super.setHorizontallyScrolling(false);
    }

    @Override // android.widget.TextView
    public void setMaxLines(int maxlines) {
        super.setMaxLines(Integer.MAX_VALUE);
    }
}
