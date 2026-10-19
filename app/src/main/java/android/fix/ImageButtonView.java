package android.fix;

import android.content.Context;
import android.ext.Tools;
import android.util.AttributeSet;

/* JADX INFO: loaded from: classes.dex */
public class ImageButtonView extends ImageView {
    public ImageButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Tools.setButtonBackground(this);
    }

    public ImageButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Tools.setButtonBackground(this);
    }

    public ImageButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Tools.setButtonBackground(this);
    }

    public ImageButtonView(Context context) {
        super(context);
        Tools.setButtonBackground(this);
    }
}
