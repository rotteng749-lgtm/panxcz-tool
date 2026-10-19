package android.fix;

import android.content.Context;
import android.content.res.Configuration;
import android.ext.ExceptionHandler;
import android.ext.Log;
import android.util.AttributeSet;

/* JADX INFO: loaded from: classes.dex */
public class LinearLayout extends android.widget.LinearLayout {
    public LinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public LinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayout(Context context) {
        super(context);
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

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        String str = newConfig == null ? null : newConfig.toString();
        Log.d("LinearLayout onConfigurationChanged: " + str);
        super.onConfigurationChanged(ContextWrapper.onConfigurationChanged(newConfig));
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Throwable e) {
            Log.badImplementation(e);
            ExceptionHandler.sendException(Thread.currentThread(), e, false);
        }
    }
}
