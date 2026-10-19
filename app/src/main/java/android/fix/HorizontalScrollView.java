package android.fix;

import android.content.Context;
import android.ext.ExceptionHandler;
import android.ext.Log;
import android.util.AttributeSet;

/* JADX INFO: loaded from: classes.dex */
public class HorizontalScrollView extends android.widget.HorizontalScrollView {
    public HorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public HorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalScrollView(Context context) {
        super(context);
    }

    @Override // android.view.View
    public void setOverScrollMode(int mode) {
        try {
            super.setOverScrollMode(mode);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.view.View
    public void playSoundEffect(int soundConstant) {
        try {
            super.playSoundEffect(soundConstant);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.view.View, android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEvent(int eventType) {
        try {
            super.sendAccessibilityEvent(eventType);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Throwable e) {
            Log.badImplementation(e);
            ExceptionHandler.sendException(Thread.currentThread(), e, false);
        }
    }
}
