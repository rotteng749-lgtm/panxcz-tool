package android.ext;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes.dex */
public class EditText extends android.fix.EditText implements EditTextExt {
    private static volatile CheckContrast[] checkedContrast = new CheckContrast[0];
    private int dataType;

    @Override // android.ext.EditTextExt
    public int getDataType() {
        return this.dataType;
    }

    @Override // android.ext.EditTextExt
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    @TargetApi(21)
    public EditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.dataType = -1;
        if (!isInEditMode()) {
            setInputType((Config.configClient & 8) != 0 ? 1 : 524289);
        }
        setSingleLine(true);
        setSelectAllOnFocus(true);
        setMinEms(10);
        setCursorVisible(true);
        setFocusableInTouchMode(true);
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(this, 0);
        } catch (Exception e) {
        }
        if (isInEditMode()) {
            return;
        }
        checkContrast(this);
    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.dataType = -1;
        if (!isInEditMode()) {
            setInputType((Config.configClient & 8) != 0 ? 1 : 524289);
        }
        setSingleLine(true);
        setSelectAllOnFocus(true);
        setMinEms(10);
        setCursorVisible(true);
        setFocusableInTouchMode(true);
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(this, 0);
        } catch (Exception e) {
        }
        if (isInEditMode()) {
            return;
        }
        checkContrast(this);
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.dataType = -1;
        if (!isInEditMode()) {
            setInputType((Config.configClient & 8) != 0 ? 1 : 524289);
        }
        setSingleLine(true);
        setSelectAllOnFocus(true);
        setMinEms(10);
        setCursorVisible(true);
        setFocusableInTouchMode(true);
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(this, 0);
        } catch (Exception e) {
        }
        if (isInEditMode()) {
            return;
        }
        checkContrast(this);
    }

    public EditText(Context context) {
        super(context);
        this.dataType = -1;
        if (!isInEditMode()) {
            setInputType((Config.configClient & 8) != 0 ? 1 : 524289);
        }
        setSingleLine(true);
        setSelectAllOnFocus(true);
        setMinEms(10);
        setCursorVisible(true);
        setFocusableInTouchMode(true);
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(this, 0);
        } catch (Exception e) {
        }
        if (isInEditMode()) {
            return;
        }
        checkContrast(this);
    }

    private static int getHighlightColor(android.widget.EditText edit) {
        try {
            if (Build.VERSION.SDK_INT >= 16) {
                return edit.getHighlightColor();
            }
        } catch (Throwable e) {
            Log.e("Failed getHighlightColor", e);
        }
        try {
            Field mHighlightColor = TextView.class.getDeclaredField("mHighlightColor");
            mHighlightColor.setAccessible(true);
            return mHighlightColor.getInt(edit);
        } catch (Throwable e2) {
            Log.e("Failed getHighlightColor2", e2);
            return 0;
        }
    }

    static void checkContrast(android.widget.EditText edit) {
        int[] state = View.ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
        int bg = -16777216;
        Drawable d = edit.getBackground();
        if (d != null) {
            int size = (int) Tools.dp2px(36.0f);
            Rect old = d.copyBounds();
            int[] oldState = d.getState();
            d.setBounds(0, 0, size, size);
            d.setState(state);
            try {
                Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                d.draw(canvas);
                bg = bitmap.getPixel(size / 2, size / 2);
                d.setBounds(old);
                d.setState(oldState);
            } catch (OutOfMemoryError e) {
                Log.w("Failed checkContrast", e);
                return;
            }
        }
        int fg_ = edit.getTextColors().getColorForState(state, edit.getCurrentTextColor());
        int hg_ = getHighlightColor(edit);
        int fg = removeAlpha(fg_, bg);
        int hg = removeAlpha(hg_, bg);
        boolean fix = checkContrast(fg, bg, hg);
        if (fix) {
            edit.setTextColor(-1);
            edit.setHighlightColor(-6262672);
            edit.setBackgroundResource(R.drawable.abc_textfield_default_mtrl_alpha);
        }
    }

    private static int removeAlpha(int fg, int bg) {
        int alpha = Color.alpha(fg);
        int ret = -16777216;
        for (int i = 0; i < 3; i++) {
            ret |= (((((fg >> (i * 8)) & 255) * alpha) + (((bg >> (i * 8)) & 255) * (255 - alpha))) / 255) << (i * 8);
        }
        return ret;
    }

    private static boolean checkContrast(int fg, int bg, int hg) {
        float[] limits = {4.5f, 125.0f, 500.0f};
        float[] softLimits = {2.0f, 50.0f, 150.0f};
        return checkContrast(fg, bg, limits) || checkContrast(fg, hg, softLimits) || checkContrast(hg, bg, softLimits);
    }

    static class CheckContrast {
        int bg;
        float brigh;
        float diff;
        int fg;
        float ratio;

        CheckContrast() {
        }
    }

    private static boolean checkContrast(int fg, int bg, float[] limits) {
        CheckContrast[] checkedContrast2 = checkedContrast;
        CheckContrast found = null;
        int length = checkedContrast2.length;
        int i = 0;
        while (true) {
            if (i < length) {
                CheckContrast check = checkedContrast2[i];
                if (check.fg != fg || check.bg != bg) {
                    i++;
                } else {
                    found = check;
                    break;
                }
            } else {
                break;
            }
        }
        if (found == null) {
            found = new CheckContrast();
            found.fg = fg;
            found.bg = bg;
            checkContrast_(found);
            int len = checkedContrast2.length;
            CheckContrast[] newArray = new CheckContrast[len + 1];
            System.arraycopy(checkedContrast2, 0, newArray, 0, len);
            newArray[len] = found;
            checkedContrast = newArray;
        }
        return found.ratio < limits[0] || found.brigh < limits[1] || found.diff < limits[2];
    }

    private static void checkContrast_(CheckContrast check) {
        int fg = check.fg;
        int bg = check.bg;
        Log.d("checkContrast(fg, bg): " + Integer.toHexString(fg) + ' ' + Integer.toHexString(bg));
        check.ratio = (float) getContrastRatio(fg, bg);
        check.brigh = (float) Math.abs(getColorBrightness(fg) - getColorBrightness(bg));
        check.diff = (float) getColorDifference(fg, bg);
        try {
            Log.d("checkContrast(fg, bg): " + check.ratio + ", " + check.brigh + ", " + check.diff);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    private static double getRelativeluminance(int color) {
        double r = ((double) Color.red(color)) / 255.0d;
        double g = ((double) Color.green(color)) / 255.0d;
        double b = ((double) Color.blue(color)) / 255.0d;
        return (0.2126d * (r <= 0.03928d ? r / 12.92d : Math.pow((0.055d + r) / 1.055d, 2.4d))) + (0.7152d * (g <= 0.03928d ? g / 12.92d : Math.pow((0.055d + g) / 1.055d, 2.4d))) + (0.0722d * (b <= 0.03928d ? b / 12.92d : Math.pow((0.055d + b) / 1.055d, 2.4d)));
    }

    private static double getContrastRatio(int colorA, int colorB) {
        double lA = getRelativeluminance(colorA) + 0.05d;
        double lB = getRelativeluminance(colorB) + 0.05d;
        return lA > lB ? lA / lB : lB / lA;
    }

    private static double getColorBrightness(int color) {
        return ((double) (((Color.red(color) * 299) + (Color.green(color) * 587)) + (Color.blue(color) * 114))) / 1000.0d;
    }

    private static double getColorDifference(int colorA, int colorB) {
        int rA = Color.red(colorA);
        int gA = Color.green(colorA);
        int bA = Color.blue(colorA);
        int rB = Color.red(colorB);
        int gB = Color.green(colorB);
        int bB = Color.blue(colorB);
        return (Math.max(rA, rB) - Math.min(rA, rB)) + (Math.max(gA, gB) - Math.min(gA, gB)) + (Math.max(bA, bB) - Math.min(bA, bB));
    }
}
