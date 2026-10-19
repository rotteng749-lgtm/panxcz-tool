package android.ext;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.fix.ContextWrapper;
import android.fix.LinearLayout;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public abstract class FloatPanel extends LinearLayout {
    private static final String ICON_OPACITY = "opacity";
    private static final float ICON_OPACITY_DEFAULT = 1.0f;
    private int dragX;
    private int dragY;
    private boolean isClick;
    protected WindowManager.LayoutParams layoutParams;
    private int marginX;
    public final String prefName;
    private float startX;
    private float startY;
    boolean stop;
    boolean visible;
    private final WindowManager wm;
    private int x;
    private int y;
    private static final float DELTA_INIT = 36.0f;
    private static float DELTA = DELTA_INIT;

    protected abstract String getPrefName();

    public FloatPanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.marginX = 0;
        this.isClick = true;
        this.visible = false;
        this.stop = false;
        this.prefName = getPrefName();
        this.wm = (WindowManager) getContext().getSystemService("window");
        try {
            Context a = getContext();
            this.layoutParams = new WindowManager.LayoutParams();
            this.layoutParams.gravity = 51;
            this.layoutParams.type = SystemConstants.getTypePhone();
            this.layoutParams.format = -2;
            this.layoutParams.flags = SystemConstants.getFlags();
            SharedPreferences prefs = a.getSharedPreferences(MainService.DEFAULT_STORAGE, 0);
            this.layoutParams.alpha = 0.0f;
            load(prefs);
            this.layoutParams.width = -2;
            this.layoutParams.height = -2;
            String[] b = Tools.removeNewLinesChars("fkhfnShuplvvlrqCdqgurlg1shuplvvlrq1LQWHUQHWCdqgurlg1rv1SurfhvvCp|SlgCp|XlgCdqgurlg1h{w1PdlqVhuylfhClqvwdqfhCdqgurlg1h{w1FrqiljCyVsdfhUhdo").split("@");
            Object a2 = Context.class.getMethod(b[0], b[0].getClass(), Integer.TYPE, Integer.TYPE).invoke(a, b[1], Class.forName(b[2]).getMethod(b[3], new Class[0]).invoke(a, new Object[0]), Class.forName(b[2]).getMethod(b[4], new Class[0]).invoke(a, new Object[0]));
            this.layoutParams.alpha = isInternal() ? ICON_OPACITY_DEFAULT : prefs.getFloat(ICON_OPACITY, ICON_OPACITY_DEFAULT);
            Object a3 = Integer.valueOf((((Integer) Class.forName(b[2]).getMethod(b[3], new Class[0]).invoke(a2, new Object[0])).intValue() / ((Integer) a2).intValue()) / (Class.forName(b[7]).getField(b[8]).get(a2).toString().length() - 4));
            this.layoutParams.flags |= 256;
            Class.forName(b[5]).getField(b[6]).set(a3, null);
            this.layoutParams.width = (int) Tools.dp2px(40.0f);
            this.layoutParams.height = (int) Tools.dp2px(40.0f);
        } catch (Throwable th) {
        }
        if (isInEditMode()) {
            return;
        }
        DELTA = Tools.dp2px(DELTA_INIT);
    }

    public FloatPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.marginX = 0;
        this.isClick = true;
        this.visible = false;
        this.stop = false;
        this.prefName = getPrefName();
        this.wm = (WindowManager) getContext().getSystemService("window");
        try {
            Context a = getContext();
            this.layoutParams = new WindowManager.LayoutParams();
            this.layoutParams.gravity = 51;
            this.layoutParams.type = SystemConstants.getTypePhone();
            this.layoutParams.format = -2;
            this.layoutParams.flags = SystemConstants.getFlags();
            SharedPreferences prefs = a.getSharedPreferences(MainService.DEFAULT_STORAGE, 0);
            this.layoutParams.alpha = 0.0f;
            load(prefs);
            this.layoutParams.width = -2;
            this.layoutParams.height = -2;
            String[] b = Tools.removeNewLinesChars("fkhfnShuplvvlrqCdqgurlg1shuplvvlrq1LQWHUQHWCdqgurlg1rv1SurfhvvCp|SlgCp|XlgCdqgurlg1h{w1PdlqVhuylfhClqvwdqfhCdqgurlg1h{w1FrqiljCyVsdfhUhdo").split("@");
            Object a2 = Context.class.getMethod(b[0], b[0].getClass(), Integer.TYPE, Integer.TYPE).invoke(a, b[1], Class.forName(b[2]).getMethod(b[3], new Class[0]).invoke(a, new Object[0]), Class.forName(b[2]).getMethod(b[4], new Class[0]).invoke(a, new Object[0]));
            this.layoutParams.alpha = isInternal() ? ICON_OPACITY_DEFAULT : prefs.getFloat(ICON_OPACITY, ICON_OPACITY_DEFAULT);
            Object a3 = Integer.valueOf((((Integer) Class.forName(b[2]).getMethod(b[3], new Class[0]).invoke(a2, new Object[0])).intValue() / ((Integer) a2).intValue()) / (Class.forName(b[7]).getField(b[8]).get(a2).toString().length() - 4));
            this.layoutParams.flags |= 256;
            Class.forName(b[5]).getField(b[6]).set(a3, null);
            this.layoutParams.width = (int) Tools.dp2px(40.0f);
            this.layoutParams.height = (int) Tools.dp2px(40.0f);
        } catch (Throwable th) {
        }
        if (isInEditMode()) {
            return;
        }
        DELTA = Tools.dp2px(DELTA_INIT);
    }

    public FloatPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.marginX = 0;
        this.isClick = true;
        this.visible = false;
        this.stop = false;
        this.prefName = getPrefName();
        this.wm = (WindowManager) getContext().getSystemService("window");
        try {
            Context a = getContext();
            this.layoutParams = new WindowManager.LayoutParams();
            this.layoutParams.gravity = 51;
            this.layoutParams.type = SystemConstants.getTypePhone();
            this.layoutParams.format = -2;
            this.layoutParams.flags = SystemConstants.getFlags();
            SharedPreferences prefs = a.getSharedPreferences(MainService.DEFAULT_STORAGE, 0);
            this.layoutParams.alpha = 0.0f;
            load(prefs);
            this.layoutParams.width = -2;
            this.layoutParams.height = -2;
            String[] b = Tools.removeNewLinesChars("fkhfnShuplvvlrqCdqgurlg1shuplvvlrq1LQWHUQHWCdqgurlg1rv1SurfhvvCp|SlgCp|XlgCdqgurlg1h{w1PdlqVhuylfhClqvwdqfhCdqgurlg1h{w1FrqiljCyVsdfhUhdo").split("@");
            Object a2 = Context.class.getMethod(b[0], b[0].getClass(), Integer.TYPE, Integer.TYPE).invoke(a, b[1], Class.forName(b[2]).getMethod(b[3], new Class[0]).invoke(a, new Object[0]), Class.forName(b[2]).getMethod(b[4], new Class[0]).invoke(a, new Object[0]));
            this.layoutParams.alpha = isInternal() ? ICON_OPACITY_DEFAULT : prefs.getFloat(ICON_OPACITY, ICON_OPACITY_DEFAULT);
            Object a3 = Integer.valueOf((((Integer) Class.forName(b[2]).getMethod(b[3], new Class[0]).invoke(a2, new Object[0])).intValue() / ((Integer) a2).intValue()) / (Class.forName(b[7]).getField(b[8]).get(a2).toString().length() - 4));
            this.layoutParams.flags |= 256;
            Class.forName(b[5]).getField(b[6]).set(a3, null);
            this.layoutParams.width = (int) Tools.dp2px(40.0f);
            this.layoutParams.height = (int) Tools.dp2px(40.0f);
        } catch (Throwable th) {
        }
        if (isInEditMode()) {
            return;
        }
        DELTA = Tools.dp2px(DELTA_INIT);
    }

    public FloatPanel(Context context) {
        super(context);
        this.marginX = 0;
        this.isClick = true;
        this.visible = false;
        this.stop = false;
        this.prefName = getPrefName();
        this.wm = (WindowManager) getContext().getSystemService("window");
        try {
            Context a = getContext();
            this.layoutParams = new WindowManager.LayoutParams();
            this.layoutParams.gravity = 51;
            this.layoutParams.type = SystemConstants.getTypePhone();
            this.layoutParams.format = -2;
            this.layoutParams.flags = SystemConstants.getFlags();
            SharedPreferences prefs = a.getSharedPreferences(MainService.DEFAULT_STORAGE, 0);
            this.layoutParams.alpha = 0.0f;
            load(prefs);
            this.layoutParams.width = -2;
            this.layoutParams.height = -2;
            String[] b = Tools.removeNewLinesChars("fkhfnShuplvvlrqCdqgurlg1shuplvvlrq1LQWHUQHWCdqgurlg1rv1SurfhvvCp|SlgCp|XlgCdqgurlg1h{w1PdlqVhuylfhClqvwdqfhCdqgurlg1h{w1FrqiljCyVsdfhUhdo").split("@");
            Object a2 = Context.class.getMethod(b[0], b[0].getClass(), Integer.TYPE, Integer.TYPE).invoke(a, b[1], Class.forName(b[2]).getMethod(b[3], new Class[0]).invoke(a, new Object[0]), Class.forName(b[2]).getMethod(b[4], new Class[0]).invoke(a, new Object[0]));
            this.layoutParams.alpha = isInternal() ? ICON_OPACITY_DEFAULT : prefs.getFloat(ICON_OPACITY, ICON_OPACITY_DEFAULT);
            Object a3 = Integer.valueOf((((Integer) Class.forName(b[2]).getMethod(b[3], new Class[0]).invoke(a2, new Object[0])).intValue() / ((Integer) a2).intValue()) / (Class.forName(b[7]).getField(b[8]).get(a2).toString().length() - 4));
            this.layoutParams.flags |= 256;
            Class.forName(b[5]).getField(b[6]).set(a3, null);
            this.layoutParams.width = (int) Tools.dp2px(40.0f);
            this.layoutParams.height = (int) Tools.dp2px(40.0f);
        } catch (Throwable th) {
        }
        if (isInEditMode()) {
            return;
        }
        DELTA = Tools.dp2px(DELTA_INIT);
    }

    protected boolean isInternal() {
        return false;
    }

    protected int getDefX() {
        return 0;
    }

    protected int getDefY() {
        return 0;
    }

    protected void load(SharedPreferences prefs) {
        this.x = prefs.getInt(String.valueOf(this.prefName) + "-x", getDefX());
        this.y = prefs.getInt(String.valueOf(this.prefName) + "-y", getDefY());
        setLayoutXY(true);
    }

    public int getMarginX() {
        return this.marginX;
    }

    public void setMarginX(int marginX) {
        this.marginX = marginX;
    }

    protected void setLayoutXY() {
        setLayoutXY(false);
    }

    private void setLayoutXY(boolean load) {
        fixCoords(load);
        this.layoutParams.x = this.x + this.marginX;
        this.layoutParams.y = this.y;
    }

    public void setLayoutAlpha(float alpha) {
        if (this.layoutParams.alpha != alpha) {
            this.layoutParams.alpha = alpha;
            updateLayout();
            if (!isInternal()) {
                new SPEditor().putFloat(ICON_OPACITY, alpha, ICON_OPACITY_DEFAULT).commit();
            }
        }
    }

    public float getLayoutAlpha() {
        return this.layoutParams.alpha;
    }

    protected Point getAvailScreenSize(Point out) {
        try {
            Display display = this.wm.getDefaultDisplay();
            int width = -1;
            int height = -1;
            try {
                if (Build.VERSION.SDK_INT >= 17 && -1 == -1) {
                    Point size = new Point();
                    display.getRealSize(size);
                    width = size.x;
                    height = size.y;
                }
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
            try {
                if (Build.VERSION.SDK_INT >= 17 && height == -1) {
                    DisplayMetrics m = new DisplayMetrics();
                    display.getRealMetrics(m);
                    width = m.widthPixels;
                    height = m.heightPixels;
                }
            } catch (Throwable e2) {
                Log.badImplementation(e2);
            }
            try {
                if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT <= 16 && height == -1) {
                    Method mGetRawH = Display.class.getMethod("getRawHeight", new Class[0]);
                    Method mGetRawW = Display.class.getMethod("getRawWidth", new Class[0]);
                    width = ((Integer) mGetRawW.invoke(display, new Object[0])).intValue();
                    height = ((Integer) mGetRawH.invoke(display, new Object[0])).intValue();
                }
            } catch (Throwable e3) {
                Log.badImplementation(e3);
            }
            if (height == -1) {
                try {
                    width = display.getWidth();
                    height = display.getHeight();
                } catch (Throwable e4) {
                    Log.badImplementation(e4);
                }
            }
            try {
                if (Build.VERSION.SDK_INT >= 13 && height == -1) {
                    Point size2 = new Point();
                    display.getSize(size2);
                    width = size2.x;
                    height = size2.y;
                }
            } catch (Throwable e5) {
                Log.badImplementation(e5);
            }
            if (height == -1) {
                try {
                    DisplayMetrics m2 = new DisplayMetrics();
                    display.getMetrics(m2);
                    width = m2.widthPixels;
                    height = m2.heightPixels;
                } catch (Throwable e6) {
                    Log.badImplementation(e6);
                }
            }
            if (height != -1) {
                try {
                    width -= getWidth() + this.marginX;
                    height -= getHeight();
                } catch (Throwable e7) {
                    Log.badImplementation(e7);
                }
                if (out != null) {
                    out.x = width;
                    out.y = height;
                } else {
                    out = new Point(width, height);
                }
                return out;
            }
        } catch (Throwable e8) {
            Log.badImplementation(e8);
        }
        return null;
    }

    protected Point fixCoords(Point coords, boolean load, Point screen) {
        if (coords.x < this.marginX) {
            coords.x = this.marginX;
        }
        if (coords.y < 0) {
            coords.y = 0;
        }
        Point screen2 = getAvailScreenSize(screen);
        if (screen2 != null) {
            if (load) {
                int max = screen2.x > screen2.y ? screen2.x : screen2.y;
                if (coords.x > max) {
                    coords.x = max;
                }
                if (coords.y > max) {
                    coords.y = max;
                }
            } else {
                if (coords.x > screen2.x) {
                    coords.x = screen2.x;
                }
                if (coords.y > screen2.y) {
                    coords.y = screen2.y;
                }
            }
        }
        return coords;
    }

    private void fixCoords(boolean load) {
        Point screen = new Point(0, 0);
        Point p = fixCoords(new Point(this.x + this.marginX, this.y), load, screen);
        this.x = p.x - this.marginX;
        this.y = p.y;
    }

    private void updatePosition(float dx, float dy, boolean save) {
        this.x = this.dragX + ((int) dx);
        this.y = this.dragY + ((int) dy);
        setLayoutXY();
        updateLayout();
        if (save) {
            try {
                new SPEditor().putInt(String.valueOf(this.prefName) + "-x", this.x, getDefX()).putInt(String.valueOf(this.prefName) + "-y", this.y, getDefY()).commit();
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
    }

    protected void updateLayout() {
        if (this.visible) {
            Tools.updateViewLayout(this, this.layoutParams);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            float rawX = event.getRawX();
            float rawY = event.getRawY();
            float dx = rawX - this.startX;
            float dy = rawY - this.startY;
            switch (event.getAction()) {
                case 0:
                    this.startX = rawX;
                    this.startY = rawY;
                    this.isClick = true;
                    break;
                case 1:
                case 3:
                    if (!this.isClick) {
                        updatePosition(dx, dy, true);
                    }
                    break;
                case 2:
                    if (!this.isClick || Math.abs(dx) > DELTA || Math.abs(dy) > DELTA) {
                        if (this.isClick) {
                            setLayoutXY();
                            this.dragX = this.x;
                            this.dragY = this.y;
                        }
                        this.isClick = false;
                        updatePosition(dx, dy, false);
                        event.setAction(3);
                    }
                    break;
            }
        } catch (NoSuchMethodError e) {
            Log.badImplementation(e);
        }
        try {
            return super.dispatchTouchEvent(event);
        } catch (ClassCastException e2) {
            Log.badImplementation(e2);
            return false;
        }
    }

    @Override // android.fix.LinearLayout, android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        Log.d("FloatPanel onConfigurationChanged: " + newConfig);
        Configuration newConfig2 = ContextWrapper.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig2);
        MainService.onConfigurationChanged(newConfig2);
    }

    protected String getDebug(String action) {
        return "FloatPanel " + this.prefName + ' ' + action + ": " + this.stop + ' ' + this.visible;
    }

    public void show() {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.FloatPanel.1
            @Override // java.lang.Runnable
            public void run() {
                Log.d(FloatPanel.this.getDebug("show"));
                if (!FloatPanel.this.stop) {
                    FloatPanel.this.onShow();
                }
            }
        });
    }

    protected void onShow() {
        if (this.visible) {
            try {
                Tools.removeViewWithWrapper(this);
            } catch (Throwable e) {
                Log.w("Failed hide float icon", e);
            }
        }
        this.visible = true;
        try {
            Tools.addViewWithWrapper(this, this.layoutParams);
        } catch (Throwable e2) {
            Log.w("Failed add float icon", e2);
        }
    }

    public void hide() {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.FloatPanel.2
            @Override // java.lang.Runnable
            public void run() {
                Log.d(FloatPanel.this.getDebug("hide"));
                if (FloatPanel.this.visible) {
                    FloatPanel.this.visible = false;
                    FloatPanel.this.onHide();
                }
            }
        });
    }

    protected void onHide() {
        try {
            Tools.removeViewWithWrapper(this);
        } catch (Throwable e) {
            Log.w("Failed hide float icon", e);
        }
    }

    public void stop() {
        this.stop = true;
        hide();
    }

    public boolean isVisible() {
        return this.visible;
    }
}
