package android.fix;

import android.annotation.TargetApi;
import android.content.Context;
import android.ext.ExceptionHandler;
import android.ext.Log;
import android.ext.MainService;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

/* JADX INFO: loaded from: classes.dex */
public class ListView extends android.widget.ListView {
    public ListView(Context context) {
        super(context);
    }

    public ListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public ListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.view.View
    public void setOverScrollMode(int mode) {
        try {
            super.setOverScrollMode(mode);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.AbsListView, android.view.View
    public void draw(Canvas canvas) {
        try {
            super.draw(canvas);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = false;
        boolean badFirmware = true;
        try {
            result = super.dispatchTouchEvent(ev);
            badFirmware = false;
        } catch (IndexOutOfBoundsException | NullPointerException | OutOfMemoryError e) {
            Log.badImplementation(e);
        }
        if (badFirmware) {
            MainService.reportBadFirmware();
        }
        return result;
    }

    @Override // android.widget.ListView, android.widget.AbsListView
    protected void layoutChildren() {
        try {
            super.layoutChildren();
        } catch (IllegalStateException e) {
            Log.w("Layout failed 1", e);
            ListAdapter adapter = getAdapter();
            if (adapter instanceof BaseAdapter) {
                ((BaseAdapter) adapter).notifyDataSetChanged();
                try {
                    super.layoutChildren();
                } catch (IllegalStateException e2) {
                    Log.w("Layout failed 2", e2);
                }
            }
        }
    }

    @Override // android.widget.AbsListView
    public void setFastScrollEnabled(boolean enabled) {
        try {
            super.setFastScrollEnabled(enabled);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Throwable e) {
            Log.badImplementation(e);
            ExceptionHandler.sendException(Thread.currentThread(), e, false);
        }
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        try {
            super.onLayout(changed, l, t, r, b);
        } catch (NoSuchMethodError | NullPointerException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.AbsListView, android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        try {
            super.onAttachedToWindow();
        } catch (StackOverflowError e) {
            Log.badImplementation(e);
        }
        View empty = getEmptyView();
        if (empty != null) {
            setEmptyView(empty);
        }
    }
}
