package android.fix;

import android.content.Context;
import android.ext.Config;
import android.ext.ListManager;
import android.ext.Log;
import android.ext.Tools;
import android.util.AttributeSet;

/* JADX INFO: loaded from: classes.dex */
public class ToolbarButton extends ImageButtonView {
    private static final int WIDTH_DP = 45;
    private boolean loaded;
    private int res;

    public ToolbarButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.res = 0;
        this.loaded = false;
    }

    public ToolbarButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.res = 0;
        this.loaded = false;
    }

    public ToolbarButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.res = 0;
        this.loaded = false;
    }

    public ToolbarButton(Context context) {
        super(context);
        this.res = 0;
        this.loaded = false;
    }

    public void setIcon(int res) {
        this.res = res;
        setMinimumWidth((int) Tools.dp2px(45.0f));
        setMinimumHeight(Tools.dp2px48());
    }

    public void setIcon(boolean load) {
        if (this.loaded != load) {
            this.loaded = load;
            if (load) {
                try {
                    setImageResource(this.res);
                    Config.setIconSize(this, (int) Tools.dp2px(45.0f), Tools.dp2px48());
                    return;
                } catch (Throwable e) {
                    Log.badImplementation(e);
                    return;
                }
            }
            try {
                setImageDrawable(null);
            } catch (Throwable e2) {
                Log.badImplementation(e2);
            }
        }
    }

    @Override // android.view.View
    public String toString() {
        return String.valueOf(super.toString()) + " " + this.res + ListManager.TEXT_SEPARATOR + this.loaded;
    }
}
