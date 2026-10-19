package android.fix;

import android.content.Context;
import android.content.res.Configuration;
import android.ext.AddressItem;
import android.ext.Log;
import android.sup.ContainerHelpers;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes.dex */
public class WrapLayout extends ViewGroup {
    public static final int FILL_FROM_CENTER = 0;
    public static final int FILL_FROM_LEFT = -1;
    public static final int FILL_FROM_RIGHT = 1;
    private int fill;
    private int[] x;

    public WrapLayout(Context context) {
        super(context);
        this.x = ContainerHelpers.EMPTY_INTS;
        this.fill = -1;
    }

    public WrapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.x = ContainerHelpers.EMPTY_INTS;
        this.fill = -1;
    }

    public WrapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.x = ContainerHelpers.EMPTY_INTS;
        this.fill = -1;
    }

    public WrapLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.x = ContainerHelpers.EMPTY_INTS;
        this.fill = -1;
    }

    public void setFill(int fill) {
        int old = this.fill;
        this.fill = fill;
        if (old != fill) {
            requestLayout();
            postInvalidate();
        }
    }

    private int update(boolean force, int width, int height) {
        int right;
        int left;
        int mayWidth;
        int count = getChildCount();
        if (!force && this.x.length == count * 2) {
            return 0;
        }
        boolean fillCenter = this.fill == 0;
        boolean fillRight = this.fill > 0;
        int[] x = new int[count * 2];
        int[] h = new int[count];
        int[] w = null;
        if (fillRight) {
            w = new int[count];
        }
        if (fillRight) {
            right = getPaddingLeft();
            left = getPaddingRight();
        } else {
            right = getPaddingRight();
            left = getPaddingLeft();
        }
        int freeWidth = (width - right) - left;
        int halfWidth = width - right;
        int startX = left;
        int offsetX = startX;
        int offsetY = getPaddingTop();
        int lineHeight = 0;
        int lineStart = 0;
        int lineXLeft = 0;
        int lineXRight = 0;
        int line = 0;
        for (int i = 0; i < count; i++) {
            View child = null;
            try {
                child = getChildAt(i);
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            if (child != null) {
                if (child.getVisibility() == 8) {
                    if (child instanceof ToolbarButton) {
                        ((ToolbarButton) child).setIcon(false);
                    }
                } else {
                    ViewGroup.LayoutParams lp = child.getLayoutParams();
                    if (lp.width == -1) {
                        mayWidth = View.MeasureSpec.makeMeasureSpec(freeWidth, AddressItem.FLAG_ALTER_ADD);
                    } else {
                        mayWidth = lp.width > 0 ? View.MeasureSpec.makeMeasureSpec(lp.width, AddressItem.FLAG_ALTER_ADD) : View.MeasureSpec.makeMeasureSpec(freeWidth, AddressItem.FLAG_FUZZY);
                    }
                    int mayHeight = lp.height > 0 ? View.MeasureSpec.makeMeasureSpec(lp.height, AddressItem.FLAG_ALTER_ADD) : View.MeasureSpec.makeMeasureSpec(height, 0);
                    child.measure(mayWidth, mayHeight);
                    int childWidth = child.getMeasuredWidth();
                    int childHeight = child.getMeasuredHeight();
                    if (offsetX <= startX || offsetX + childWidth <= halfWidth) {
                        if (lineHeight < childHeight) {
                            lineHeight = childHeight;
                        }
                    } else {
                        for (int j = lineStart; j < i; j++) {
                            int i2 = count + j;
                            x[i2] = x[i2] + ((lineHeight - h[j]) / 2);
                            if (fillCenter) {
                                x[j] = x[j] + ((((freeWidth - offsetX) / 2) + left) - lineXLeft);
                            }
                        }
                        line++;
                        lineStart = i;
                        offsetX = startX;
                        offsetY += lineHeight;
                        lineXLeft = 0;
                        lineXRight = 0;
                        lineHeight = childHeight;
                    }
                    if (fillCenter) {
                        if ((i - lineStart) % 2 != 0) {
                            x[i] = lineXRight;
                            lineXRight += childWidth;
                        } else {
                            lineXLeft -= childWidth;
                            x[i] = lineXLeft;
                        }
                    } else {
                        x[i] = offsetX;
                    }
                    x[count + i] = offsetY;
                    h[i] = childHeight;
                    if (fillRight) {
                        w[i] = childWidth;
                    }
                    offsetX += childWidth;
                    if (child instanceof ToolbarButton) {
                        ((ToolbarButton) child).setIcon(line == 0);
                    }
                }
            }
        }
        for (int j2 = lineStart; j2 < count; j2++) {
            int i3 = count + j2;
            x[i3] = x[i3] + ((lineHeight - h[j2]) / 2);
            if (fillCenter) {
                x[j2] = x[j2] + ((((freeWidth - offsetX) / 2) + left) - lineXLeft);
            }
        }
        if (fillRight) {
            for (int i4 = 0; i4 < count; i4++) {
                x[i4] = (width - x[i4]) - w[i4];
            }
        }
        this.x = x;
        return offsetY + lineHeight + getPaddingBottom();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        int usedHeight = update(true, width, height);
        if (View.MeasureSpec.getMode(heightMeasureSpec) == 0) {
            height = usedHeight;
        } else if (View.MeasureSpec.getMode(heightMeasureSpec) == Integer.MIN_VALUE && usedHeight <= height) {
            height = usedHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.LayoutParams(-2, -2);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof ViewGroup.LayoutParams;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        update(false, r - l, b - t);
        int[] xa = this.x;
        for (int i = 0; i < count; i++) {
            try {
                View child = getChildAt(i);
                if (child != null && child.getVisibility() != 8) {
                    int w = child.getMeasuredWidth();
                    int h = child.getMeasuredHeight();
                    int x = xa[i];
                    int y = xa[count + i];
                    child.layout(x, y, x + w, y + h);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
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

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean toolbar = false;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = null;
            try {
                child = getChildAt(i);
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            if (child != null && (child instanceof ToolbarButton)) {
                ((ToolbarButton) child).setIcon(false);
                toolbar = true;
            }
        }
        if (toolbar) {
            requestLayout();
        }
    }
}
