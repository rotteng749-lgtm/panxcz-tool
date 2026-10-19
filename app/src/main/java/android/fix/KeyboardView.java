package android.fix;

import android.annotation.TargetApi;
import android.content.Context;
import android.ext.ExceptionHandler;
import android.ext.Log;
import android.graphics.Canvas;
import android.util.AttributeSet;

/* JADX INFO: loaded from: classes.dex */
public class KeyboardView extends android.inputmethodservice.KeyboardView {
    public static volatile boolean inEditMode = true;

    @TargetApi(21)
    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(inEditMode ? new ContextWrapperInner(context) : context, attrs, defStyleAttr, defStyleRes);
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(inEditMode ? new ContextWrapperInner(context) : context, attrs, defStyleAttr);
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(inEditMode ? new ContextWrapperInner(context) : context, attrs);
    }

    @Override // android.inputmethodservice.KeyboardView, android.view.View
    public void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (IllegalArgumentException e) {
            Log.badImplementation(e);
        } catch (OutOfMemoryError e2) {
            Log.badImplementation(e2);
        }
    }

    public static class ContextWrapperInner extends android.content.ContextWrapper {
        Context base;

        public ContextWrapperInner(Context base) {
            super(base);
            this.base = base;
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public Object getSystemService(String name) {
            if ("audio".equals(name)) {
                return null;
            }
            return this.base.getSystemService(name);
        }
    }

    @Override // android.inputmethodservice.KeyboardView, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Throwable e) {
            Log.badImplementation(e);
            ExceptionHandler.sendException(Thread.currentThread(), e, false);
        }
    }
}
