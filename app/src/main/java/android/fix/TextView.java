package android.fix;

import android.annotation.TargetApi;
import android.content.Context;
import android.ext.AddressItem;
import android.ext.ExceptionHandler;
import android.ext.Log;
import android.ext.Tools;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

/* JADX INFO: loaded from: classes.dex */
public class TextView extends android.widget.TextView {
    private Rect bounds;
    private Paint.FontMetricsInt fm;

    @TargetApi(21)
    public TextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextView(Context context) {
        super(context);
    }

    @Override // android.widget.TextView
    public void setText(CharSequence text, android.widget.TextView.BufferType type) {
        try {
            super.setText(text, type);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.TextView, android.view.View
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
        } catch (Throwable e) {
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

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Throwable e) {
            Log.badImplementation(e);
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

    private static int getDefSize(int size, int measureSpec) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case AddressItem.FLAG_FUZZY /* -2147483648 */:
                int result = Math.min(size, specSize);
                break;
            case 0:
                break;
            case AddressItem.FLAG_ALTER_ADD /* 1073741824 */:
                break;
        }
        return size;
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Throwable e) {
            Log.badImplementation(e);
            try {
                int measuredWidth = getSuggestedMinimumWidth();
                int measuredHeight = getSuggestedMinimumHeight();
                int atMost = View.MeasureSpec.getMode(widthMeasureSpec) == Integer.MIN_VALUE ? View.MeasureSpec.getSize(widthMeasureSpec) : Integer.MAX_VALUE;
                if (atMost == 0) {
                    atMost = Integer.MAX_VALUE;
                }
                Paint paint = getPaint();
                CharSequence cs = getText();
                int lines = 0;
                int maxWidth = (int) (((double) Tools.dp2px(1.0f)) + 0.99d);
                int start = 0;
                int m = cs.length();
                for (int i = 0; i <= m; i++) {
                    if (i == m || cs.charAt(i) == '\n') {
                        lines++;
                        if (i > start) {
                            int lineWidth = (int) (((double) paint.measureText(cs, start, i)) + 0.99d);
                            if (maxWidth < lineWidth) {
                                maxWidth = lineWidth;
                            }
                            lines += lineWidth / atMost;
                        }
                        start = i + 1;
                    }
                }
                if (this.bounds == null) {
                    this.bounds = new Rect();
                }
                if (this.fm == null) {
                    this.fm = new Paint.FontMetricsInt();
                }
                paint.getTextBounds("!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~", 0, 94, this.bounds);
                int perLine = Math.max((this.bounds.bottom - this.bounds.top) + 1, paint.getFontMetricsInt(this.fm));
                if (perLine <= 5) {
                    perLine = Math.max(perLine, (int) ((((double) getTextSize()) * 1.5d) + 0.99d));
                }
                setMeasuredDimension(getDefSize(Math.max(measuredWidth, getCompoundPaddingLeft() + maxWidth + getCompoundPaddingRight()), widthMeasureSpec), getDefSize(Math.max(measuredHeight, (perLine * lines) + getCompoundPaddingTop() + getCompoundPaddingBottom() + ((this.fm.bottom - this.fm.top) - (this.fm.descent - this.fm.ascent))), heightMeasureSpec));
            } catch (Throwable e2) {
                Log.badImplementation(e2);
                ExceptionHandler.sendException(Thread.currentThread(), e, false);
                ExceptionHandler.sendException(Thread.currentThread(), e2, false);
            }
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

    @Override // android.view.View
    public void setVisibility(int visibility) {
        try {
            super.setVisibility(visibility);
        } catch (StackOverflowError e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        Tools.fixLeak(this);
        super.onDetachedFromWindow();
    }

    public boolean getSecClipboardEnabled() {
        return false;
    }
}
