package android.fix;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.ext.ExceptionHandler;
import android.ext.Log;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public class CheckBox extends android.widget.CheckBox {
    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBox(Context context) {
        super(context);
    }

    @Override // android.widget.TextView
    public void setText(CharSequence text, TextView.BufferType type) {
        try {
            super.setText(text, type);
        } catch (SecurityException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected void drawableStateChanged() {
        try {
            super.drawableStateChanged();
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return super.onTouchEvent(event);
        } catch (ActivityNotFoundException e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.widget.TextView, android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        try {
            super.invalidateDrawable(drawable);
        } catch (NoSuchFieldError e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.TextView
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        try {
            super.setCompoundDrawables(left, top, right, bottom);
        } catch (StringIndexOutOfBoundsException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.view.View
    public void refreshDrawableState() {
        try {
            super.refreshDrawableState();
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.view.View
    @TargetApi(14)
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        try {
            super.onInitializeAccessibilityNodeInfo(info);
        } catch (NullPointerException e) {
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

    @Override // android.widget.TextView, android.view.View
    public boolean performLongClick() {
        try {
            return super.performLongClick();
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.badImplementation(e);
        } catch (NullPointerException e2) {
            Log.badImplementation(e2);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onDragEvent(DragEvent event) {
        try {
            return super.onDragEvent(event);
        } catch (ClassCastException e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Throwable e) {
            Log.badImplementation(e);
            ExceptionHandler.sendException(Thread.currentThread(), e, false);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        try {
            return super.onKeyShortcut(keyCode, event);
        } catch (NullPointerException e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.widget.TextView
    public boolean onTextContextMenuItem(int id) {
        try {
            return super.onTextContextMenuItem(id);
        } catch (NullPointerException e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        try {
            super.onWindowFocusChanged(hasWindowFocus);
        } catch (NullPointerException e) {
            Log.badImplementation(e);
        }
    }

    public boolean getSecClipboardEnabled() {
        return false;
    }
}
