package android.fix;

import android.annotation.TargetApi;
import android.content.Context;
import android.ext.ExceptionHandler;
import android.ext.Log;
import android.graphics.Canvas;
import android.util.AttributeSet;

/* JADX INFO: loaded from: classes.dex */
public class ImageView extends android.widget.ImageView {
    @TargetApi(21)
    public ImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageView(Context context) {
        super(context);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (NoSuchFieldError e) {
            Log.badImplementation(e);
        } catch (RuntimeException e2) {
            Log.badImplementation(e2);
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

    @Override // android.widget.ImageView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Throwable e) {
            Log.badImplementation(e);
            ExceptionHandler.sendException(Thread.currentThread(), e, false);
        }
    }
}
