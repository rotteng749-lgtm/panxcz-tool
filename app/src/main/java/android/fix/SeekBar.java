package android.fix;

import android.annotation.TargetApi;
import android.content.Context;
import android.ext.ExceptionHandler;
import android.ext.Log;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes.dex */
public class SeekBar extends android.widget.SeekBar {
    @TargetApi(21)
    public SeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBar(Context context) {
        super(context);
    }

    @Override // android.widget.ProgressBar
    public synchronized void setProgress(int progress) {
        try {
            super.setProgress(progress);
        } catch (NullPointerException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View changedView, int visibility) {
        try {
            super.onVisibilityChanged(changedView, visibility);
        } catch (RuntimeException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected synchronized void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (RuntimeException e) {
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

    @Override // android.widget.ProgressBar
    public void setInterpolator(Context context, int resID) {
        try {
            super.setInterpolator(context, resID);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.ProgressBar
    public void setInterpolator(Interpolator interpolator) {
        try {
            super.setInterpolator(interpolator);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Throwable e) {
            Log.badImplementation(e);
            ExceptionHandler.sendException(Thread.currentThread(), e, false);
        }
    }
}
