package android.fix;

import android.annotation.TargetApi;
import android.content.Context;
import android.ext.ExceptionHandler;
import android.ext.Log;
import android.ext.Tools;
import android.graphics.Rect;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/* JADX INFO: loaded from: classes.dex */
public class ScrollView extends android.widget.ScrollView {
    private static final int STATE_DRAGGING = 2;
    private static final int STATE_VISIBLE = 1;
    private static final long TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    private long mPendingDrag;
    private int mState;
    private boolean useFastScroll;

    @TargetApi(23)
    public ScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.useFastScroll = false;
        this.mPendingDrag = -1L;
        this.mState = 1;
    }

    public ScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.useFastScroll = false;
        this.mPendingDrag = -1L;
        this.mState = 1;
    }

    public ScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.useFastScroll = false;
        this.mPendingDrag = -1L;
        this.mState = 1;
    }

    public ScrollView(Context context) {
        super(context);
        this.useFastScroll = false;
        this.mPendingDrag = -1L;
        this.mState = 1;
    }

    @Override // android.view.View
    public void setOverScrollMode(int mode) {
        try {
            super.setOverScrollMode(mode);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.ScrollView, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch (IllegalArgumentException e) {
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

    @Override // android.widget.ScrollView, android.view.ViewGroup
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        try {
            return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Throwable e) {
            Log.badImplementation(e);
            ExceptionHandler.sendException(Thread.currentThread(), e, false);
        }
    }

    public void setFastScrollEnabled(boolean enabled) {
        this.useFastScroll = enabled;
        if (Build.VERSION.SDK_INT >= 16) {
            setScrollBarSize(Tools.dp2px48() / 2);
        }
    }

    private void cancelPendingDrag() {
        this.mPendingDrag = -1L;
    }

    private void startPendingDrag() {
        this.mPendingDrag = SystemClock.uptimeMillis() + TAP_TIMEOUT;
    }

    private void beginDrag() {
        this.mPendingDrag = -1L;
        this.mState = 2;
        requestDisallowInterceptTouchEvent(true);
        cancelFling();
    }

    private void cancelFling() {
        MotionEvent cancelFling = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
        onTouchEvent(cancelFling);
        cancelFling.recycle();
    }

    private boolean isPointInsideX(float x) {
        return x >= ((float) (getWidth() - Tools.dp2px48()));
    }

    private int getPos(float y) {
        int height = getHeight();
        float gap = Tools.dp2px48() / 2;
        float max = (height - 1) - (2.0f * gap);
        float maxY = getChildAt(0).getHeight();
        return (int) Math.min(Math.max(((y - gap) / max) * maxY, 0.0f), maxY);
    }

    @Override // android.widget.ScrollView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.useFastScroll) {
            switch (ev.getActionMasked()) {
                case 0:
                    if (isPointInsideX(ev.getX())) {
                        startPendingDrag();
                    }
                    break;
                case 1:
                case 3:
                    cancelPendingDrag();
                    break;
                case 2:
                    if (!isPointInsideX(ev.getX())) {
                        cancelPendingDrag();
                    } else if (this.mPendingDrag >= 0 && this.mPendingDrag <= SystemClock.uptimeMillis()) {
                        beginDrag();
                        scrollTo(getScrollX(), getPos(ev.getY()));
                        return onTouchEvent(ev);
                    }
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override // android.widget.ScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent me) {
        if (this.useFastScroll) {
            switch (me.getActionMasked()) {
                case 0:
                    if (isPointInsideX(me.getX())) {
                        beginDrag();
                        return true;
                    }
                    break;
                case 1:
                    if (this.mPendingDrag >= 0) {
                        beginDrag();
                        scrollTo(getScrollX(), getPos(me.getY()));
                    }
                    if (this.mState == 2) {
                        requestDisallowInterceptTouchEvent(false);
                        this.mState = 1;
                        return true;
                    }
                    break;
                case 2:
                    if (this.mPendingDrag >= 0) {
                        beginDrag();
                    }
                    if (this.mState == 2) {
                        scrollTo(getScrollX(), getPos(me.getY()));
                        return true;
                    }
                    break;
                case 3:
                    cancelPendingDrag();
                    break;
            }
        }
        try {
            return super.onTouchEvent(me);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }
}
