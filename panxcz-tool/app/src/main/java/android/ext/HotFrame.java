package android.ext;

import android.content.Context;
import android.fix.FrameLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/* JADX INFO: loaded from: classes.dex */
public class HotFrame extends FrameLayout implements View.OnLongClickListener {
    private static final int LONG_PRESS_TIMEOUT = 750;
    private final Runnable makeLongClick;

    public HotFrame(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setHapticFeedbackEnabled(false);
        setOnLongClickListener(this);
        this.makeLongClick = new Runnable() { // from class: android.ext.HotFrame.1
            @Override // java.lang.Runnable
            public void run() {
                boolean feedback = false;
                try {
                    HotFrame.this.setHapticFeedbackEnabled(true);
                    feedback = HotFrame.this.performHapticFeedback(0, 1);
                    HotFrame.this.setHapticFeedbackEnabled(false);
                } catch (Throwable e) {
                    Log.d("Failed make HapticFeedback", e);
                    HotFrame.this.setHapticFeedbackEnabled(false);
                }
                Log.d("Real long click: " + feedback);
                MainService.instance.onLongClick(null);
            }
        };
    }

    public HotFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setHapticFeedbackEnabled(false);
        setOnLongClickListener(this);
        this.makeLongClick = new Runnable() { // from class: android.ext.HotFrame.1
            @Override // java.lang.Runnable
            public void run() {
                boolean feedback = false;
                try {
                    HotFrame.this.setHapticFeedbackEnabled(true);
                    feedback = HotFrame.this.performHapticFeedback(0, 1);
                    HotFrame.this.setHapticFeedbackEnabled(false);
                } catch (Throwable e) {
                    Log.d("Failed make HapticFeedback", e);
                    HotFrame.this.setHapticFeedbackEnabled(false);
                }
                Log.d("Real long click: " + feedback);
                MainService.instance.onLongClick(null);
            }
        };
    }

    public HotFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHapticFeedbackEnabled(false);
        setOnLongClickListener(this);
        this.makeLongClick = new Runnable() { // from class: android.ext.HotFrame.1
            @Override // java.lang.Runnable
            public void run() {
                boolean feedback = false;
                try {
                    HotFrame.this.setHapticFeedbackEnabled(true);
                    feedback = HotFrame.this.performHapticFeedback(0, 1);
                    HotFrame.this.setHapticFeedbackEnabled(false);
                } catch (Throwable e) {
                    Log.d("Failed make HapticFeedback", e);
                    HotFrame.this.setHapticFeedbackEnabled(false);
                }
                Log.d("Real long click: " + feedback);
                MainService.instance.onLongClick(null);
            }
        };
    }

    public HotFrame(Context context) {
        super(context);
        setHapticFeedbackEnabled(false);
        setOnLongClickListener(this);
        this.makeLongClick = new Runnable() { // from class: android.ext.HotFrame.1
            @Override // java.lang.Runnable
            public void run() {
                boolean feedback = false;
                try {
                    HotFrame.this.setHapticFeedbackEnabled(true);
                    feedback = HotFrame.this.performHapticFeedback(0, 1);
                    HotFrame.this.setHapticFeedbackEnabled(false);
                } catch (Throwable e) {
                    Log.d("Failed make HapticFeedback", e);
                    HotFrame.this.setHapticFeedbackEnabled(false);
                }
                Log.d("Real long click: " + feedback);
                MainService.instance.onLongClick(null);
            }
        };
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        int time = ViewConfiguration.getLongPressTimeout();
        if (time < 0) {
            time = 0;
        }
        int timeout = 750 - time;
        Log.d("Fake long click: " + timeout);
        if (timeout > 0) {
            removeCallbacks(this.makeLongClick);
            postDelayed(this.makeLongClick, timeout);
            return true;
        }
        this.makeLongClick.run();
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            switch (event.getAction()) {
                case 0:
                case 1:
                case 3:
                    removeCallbacks(this.makeLongClick);
                    break;
            }
        } catch (NoSuchMethodError e) {
            Log.badImplementation(e);
        }
        try {
            return super.dispatchTouchEvent(event);
        } catch (Throwable e2) {
            Log.badImplementation(e2);
            return false;
        }
    }
}
