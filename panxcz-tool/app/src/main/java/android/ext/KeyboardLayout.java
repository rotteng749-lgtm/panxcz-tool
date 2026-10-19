package android.ext;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.fix.ContextWrapper;
import android.fix.FrameLayout;
import android.fix.LinearLayout;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class KeyboardLayout extends LinearLayout {
    private FrameLayout frame;
    private InternalKeyboard kbdView;
    private ScrollView scroll;
    private int usedOrientation;

    public KeyboardLayout(Context context) {
        super(context);
    }

    public KeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(11)
    public KeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public KeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        if (this.scroll == null) {
            this.usedOrientation = 0;
            this.scroll = (ScrollView) findViewById(R.id.kbd_scroll);
            this.frame = (FrameLayout) findViewById(R.id.kbd_frame);
            this.kbdView = (InternalKeyboard) findViewById(R.id.keyboard);
        }
    }

    @Override // android.fix.LinearLayout, android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        Log.d("KeyboardLayout onConfigurationChanged: " + newConfig);
        Configuration newConfig2 = ContextWrapper.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig2);
        updateOrientation();
        MainService.onConfigurationChanged(newConfig2);
    }

    public void updateOrientation() {
        int[] p;
        Configuration conf = Tools.getContext().getResources().getConfiguration();
        int orientation = conf.orientation;
        if (orientation != this.usedOrientation) {
            this.usedOrientation = orientation;
            if (Build.VERSION.SDK_INT >= 13) {
                Log.d("Screen: " + conf.screenWidthDp + "dp x " + conf.screenHeightDp + "dp");
            }
            init();
            if (orientation == 1) {
                p = new int[]{1, -1, 0};
            } else {
                p = new int[]{0, -1, -2};
            }
            setOrientation(p[0]);
            setSize(this.scroll.getVisibility() == 0 ? this.scroll : this.frame, p[1], p[2]);
            this.kbdView.updateOrientation();
        }
    }

    private void setSize(View v, int w, int h) {
        ViewGroup.LayoutParams p = v.getLayoutParams();
        p.width = w;
        p.height = h;
        v.setLayoutParams(p);
    }
}
