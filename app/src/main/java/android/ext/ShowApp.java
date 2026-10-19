package android.ext;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.ext.BaseActivity;
import android.fix.ImageView;
import android.fix.TextView;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
@SuppressLint({"ClickableViewAccessibility"})
public class ShowApp extends ImageView {
    private static ArrayList<WindowStatus> wins = new ArrayList<>();
    final Runnable hideScriptStop;
    volatile boolean hintShowed;
    TextView icon;
    WindowManager.LayoutParams iconParams;
    volatile boolean iconShowed;
    WindowManager.LayoutParams layoutParams;
    volatile View recordShowed;
    WindowManager.LayoutParams scriptParams;
    volatile View scriptShowed;
    volatile View scriptUiButtonShowed;

    @TargetApi(21)
    public ShowApp(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.hintShowed = false;
        this.iconShowed = false;
        this.scriptShowed = null;
        this.scriptUiButtonShowed = null;
        this.recordShowed = null;
        if (!isInEditMode()) {
            TextView icon = new TextView(getContext());
            this.icon = icon;
            try {
                icon.setGravity(17);
            } catch (NoSuchMethodError e) {
                Log.badImplementation(e);
            }
            icon.setText(Tools.removeNewLinesChars(BaseActivity.GoOnForum.S1));
            icon.setBackgroundColor(-16777216);
            WindowManager.LayoutParams scriptParams = new WindowManager.LayoutParams();
            this.scriptParams = scriptParams;
            scriptParams.type = SystemConstants.getTypeSystemAlert();
            scriptParams.width = -2;
            scriptParams.height = -2;
            scriptParams.format = -2;
            scriptParams.gravity = 51;
            scriptParams.x = -10000;
            scriptParams.y = -10000;
            scriptParams.flags = 8;
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.layoutParams = layoutParams;
            layoutParams.type = SystemConstants.getTypeSystemAlert();
            layoutParams.width = (int) Tools.dp2px(60.0f);
            layoutParams.height = (int) Tools.dp2px(20.0f);
            layoutParams.format = -2;
            layoutParams.gravity = 51;
            layoutParams.x = 10000;
            layoutParams.y = 10000;
            layoutParams.flags = 264;
            WindowManager.LayoutParams iconParams = new WindowManager.LayoutParams();
            this.iconParams = iconParams;
            iconParams.type = SystemConstants.getTypePhone();
            iconParams.width = -2;
            iconParams.height = -2;
            iconParams.format = -2;
            iconParams.gravity = 49;
            iconParams.x = 0;
            iconParams.y = -1000;
            iconParams.flags = 24;
            setOnTouchListener(new View.OnTouchListener() { // from class: android.ext.ShowApp.1
                @Override // android.view.View.OnTouchListener
                @SuppressLint({"ClickableViewAccessibility"})
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case 0:
                            ShowApp.this.makeTransparentUI(true);
                            return true;
                        case 1:
                        case 3:
                            ShowApp.this.makeTransparentUI(false);
                            return true;
                        case 2:
                        default:
                            return false;
                    }
                }
            });
        }
        this.hideScriptStop = new Runnable() { // from class: android.ext.ShowApp.2
            @Override // java.lang.Runnable
            public void run() {
                ShowApp.this.hideScript();
            }
        };
    }

    public ShowApp(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.hintShowed = false;
        this.iconShowed = false;
        this.scriptShowed = null;
        this.scriptUiButtonShowed = null;
        this.recordShowed = null;
        if (!isInEditMode()) {
            TextView icon = new TextView(getContext());
            this.icon = icon;
            try {
                icon.setGravity(17);
            } catch (NoSuchMethodError e) {
                Log.badImplementation(e);
            }
            icon.setText(Tools.removeNewLinesChars(BaseActivity.GoOnForum.S1));
            icon.setBackgroundColor(-16777216);
            WindowManager.LayoutParams scriptParams = new WindowManager.LayoutParams();
            this.scriptParams = scriptParams;
            scriptParams.type = SystemConstants.getTypeSystemAlert();
            scriptParams.width = -2;
            scriptParams.height = -2;
            scriptParams.format = -2;
            scriptParams.gravity = 51;
            scriptParams.x = -10000;
            scriptParams.y = -10000;
            scriptParams.flags = 8;
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.layoutParams = layoutParams;
            layoutParams.type = SystemConstants.getTypeSystemAlert();
            layoutParams.width = (int) Tools.dp2px(60.0f);
            layoutParams.height = (int) Tools.dp2px(20.0f);
            layoutParams.format = -2;
            layoutParams.gravity = 51;
            layoutParams.x = 10000;
            layoutParams.y = 10000;
            layoutParams.flags = 264;
            WindowManager.LayoutParams iconParams = new WindowManager.LayoutParams();
            this.iconParams = iconParams;
            iconParams.type = SystemConstants.getTypePhone();
            iconParams.width = -2;
            iconParams.height = -2;
            iconParams.format = -2;
            iconParams.gravity = 49;
            iconParams.x = 0;
            iconParams.y = -1000;
            iconParams.flags = 24;
            setOnTouchListener(new View.OnTouchListener() { // from class: android.ext.ShowApp.1
                @Override // android.view.View.OnTouchListener
                @SuppressLint({"ClickableViewAccessibility"})
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case 0:
                            ShowApp.this.makeTransparentUI(true);
                            return true;
                        case 1:
                        case 3:
                            ShowApp.this.makeTransparentUI(false);
                            return true;
                        case 2:
                        default:
                            return false;
                    }
                }
            });
        }
        this.hideScriptStop = new Runnable() { // from class: android.ext.ShowApp.2
            @Override // java.lang.Runnable
            public void run() {
                ShowApp.this.hideScript();
            }
        };
    }

    public ShowApp(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.hintShowed = false;
        this.iconShowed = false;
        this.scriptShowed = null;
        this.scriptUiButtonShowed = null;
        this.recordShowed = null;
        if (!isInEditMode()) {
            TextView icon = new TextView(getContext());
            this.icon = icon;
            try {
                icon.setGravity(17);
            } catch (NoSuchMethodError e) {
                Log.badImplementation(e);
            }
            icon.setText(Tools.removeNewLinesChars(BaseActivity.GoOnForum.S1));
            icon.setBackgroundColor(-16777216);
            WindowManager.LayoutParams scriptParams = new WindowManager.LayoutParams();
            this.scriptParams = scriptParams;
            scriptParams.type = SystemConstants.getTypeSystemAlert();
            scriptParams.width = -2;
            scriptParams.height = -2;
            scriptParams.format = -2;
            scriptParams.gravity = 51;
            scriptParams.x = -10000;
            scriptParams.y = -10000;
            scriptParams.flags = 8;
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.layoutParams = layoutParams;
            layoutParams.type = SystemConstants.getTypeSystemAlert();
            layoutParams.width = (int) Tools.dp2px(60.0f);
            layoutParams.height = (int) Tools.dp2px(20.0f);
            layoutParams.format = -2;
            layoutParams.gravity = 51;
            layoutParams.x = 10000;
            layoutParams.y = 10000;
            layoutParams.flags = 264;
            WindowManager.LayoutParams iconParams = new WindowManager.LayoutParams();
            this.iconParams = iconParams;
            iconParams.type = SystemConstants.getTypePhone();
            iconParams.width = -2;
            iconParams.height = -2;
            iconParams.format = -2;
            iconParams.gravity = 49;
            iconParams.x = 0;
            iconParams.y = -1000;
            iconParams.flags = 24;
            setOnTouchListener(new View.OnTouchListener() { // from class: android.ext.ShowApp.1
                @Override // android.view.View.OnTouchListener
                @SuppressLint({"ClickableViewAccessibility"})
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case 0:
                            ShowApp.this.makeTransparentUI(true);
                            return true;
                        case 1:
                        case 3:
                            ShowApp.this.makeTransparentUI(false);
                            return true;
                        case 2:
                        default:
                            return false;
                    }
                }
            });
        }
        this.hideScriptStop = new Runnable() { // from class: android.ext.ShowApp.2
            @Override // java.lang.Runnable
            public void run() {
                ShowApp.this.hideScript();
            }
        };
    }

    public ShowApp(Context context) {
        super(context);
        this.hintShowed = false;
        this.iconShowed = false;
        this.scriptShowed = null;
        this.scriptUiButtonShowed = null;
        this.recordShowed = null;
        if (!isInEditMode()) {
            TextView icon = new TextView(getContext());
            this.icon = icon;
            try {
                icon.setGravity(17);
            } catch (NoSuchMethodError e) {
                Log.badImplementation(e);
            }
            icon.setText(Tools.removeNewLinesChars(BaseActivity.GoOnForum.S1));
            icon.setBackgroundColor(-16777216);
            WindowManager.LayoutParams scriptParams = new WindowManager.LayoutParams();
            this.scriptParams = scriptParams;
            scriptParams.type = SystemConstants.getTypeSystemAlert();
            scriptParams.width = -2;
            scriptParams.height = -2;
            scriptParams.format = -2;
            scriptParams.gravity = 51;
            scriptParams.x = -10000;
            scriptParams.y = -10000;
            scriptParams.flags = 8;
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.layoutParams = layoutParams;
            layoutParams.type = SystemConstants.getTypeSystemAlert();
            layoutParams.width = (int) Tools.dp2px(60.0f);
            layoutParams.height = (int) Tools.dp2px(20.0f);
            layoutParams.format = -2;
            layoutParams.gravity = 51;
            layoutParams.x = 10000;
            layoutParams.y = 10000;
            layoutParams.flags = 264;
            WindowManager.LayoutParams iconParams = new WindowManager.LayoutParams();
            this.iconParams = iconParams;
            iconParams.type = SystemConstants.getTypePhone();
            iconParams.width = -2;
            iconParams.height = -2;
            iconParams.format = -2;
            iconParams.gravity = 49;
            iconParams.x = 0;
            iconParams.y = -1000;
            iconParams.flags = 24;
            setOnTouchListener(new View.OnTouchListener() { // from class: android.ext.ShowApp.1
                @Override // android.view.View.OnTouchListener
                @SuppressLint({"ClickableViewAccessibility"})
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case 0:
                            ShowApp.this.makeTransparentUI(true);
                            return true;
                        case 1:
                        case 3:
                            ShowApp.this.makeTransparentUI(false);
                            return true;
                        case 2:
                        default:
                            return false;
                    }
                }
            });
        }
        this.hideScriptStop = new Runnable() { // from class: android.ext.ShowApp.2
            @Override // java.lang.Runnable
            public void run() {
                ShowApp.this.hideScript();
            }
        };
    }

    void makeTransparentUI(boolean transparent) {
        for (WindowStatus win : wins) {
            Window window = win.ref.get();
            if (window != null) {
                try {
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.alpha = transparent ? 0.0f : 1.0f;
                    if (win.useDim) {
                        params.flags = transparent ? params.flags & (-3) : params.flags | 2;
                        params.dimAmount = transparent ? 0.0f : win.dimAmount;
                    }
                    window.setAttributes(params);
                } catch (Throwable e) {
                    Log.w("Failed set transparency", e);
                }
            }
        }
        float alpha = transparent ? 0.0f : 1.0f;
        if (this.hintShowed) {
            setTransparency(this.icon, alpha);
        }
        setTransparency(this.scriptShowed, alpha);
        setTransparency(this.scriptUiButtonShowed, alpha);
        setTransparency(this.recordShowed, alpha);
    }

    private void setTransparency(View view, float alpha) {
        if (view != null) {
            try {
                if (Build.VERSION.SDK_INT >= 11) {
                    view.setAlpha(alpha);
                }
            } catch (Throwable e) {
                Log.w("Failed set transparency", e);
            }
        }
    }

    public void hideUi() {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ShowApp.3
            @Override // java.lang.Runnable
            public void run() {
                if (ShowApp.this.scriptShowed == null) {
                    ShowApp.this.showScript();
                    ThreadManager.getHandlerUiThread().postDelayed(ShowApp.this.hideScriptStop, 2000L);
                }
            }
        });
    }

    boolean show(View view, WindowManager.LayoutParams params) {
        boolean ret = false;
        if (view != null) {
            try {
                if (view instanceof FloatPanel) {
                    ((FloatPanel) view).show();
                } else {
                    Tools.addViewWithWrapper(view, params);
                }
                ret = true;
                if (Build.VERSION.SDK_INT >= 11) {
                    view.setAlpha(1.0f);
                }
            } catch (Throwable e) {
                Log.w("Failed add view " + view, e);
            }
        }
        return ret;
    }

    boolean hide(View view) {
        if (view == null) {
            return false;
        }
        try {
            if (view instanceof FloatPanel) {
                ((FloatPanel) view).hide();
            } else {
                Tools.removeViewWithWrapper(view);
            }
            return true;
        } catch (Throwable e) {
            Log.w("Failed remove view " + view, e);
            return false;
        }
    }

    public void showScript() {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ShowApp.4
            @Override // java.lang.Runnable
            public void run() {
                View view;
                ThreadManager.getHandlerUiThread().removeCallbacks(ShowApp.this.hideScriptStop);
                ShowApp.this.hideScript(ShowApp.this.scriptShowed);
                MainService service = MainService.instance;
                if (service != null && (view = service.scriptInterrupt) != null) {
                    View uiButton = service.scriptUiButton;
                    if (uiButton != null && MainService.instance.mainDialog != null && ShowApp.this.show(uiButton, ShowApp.this.scriptParams)) {
                        ShowApp.this.scriptUiButtonShowed = uiButton;
                    }
                    if (ShowApp.this.show(view, ShowApp.this.scriptParams)) {
                        ShowApp.this.scriptShowed = view;
                    }
                }
            }
        });
    }

    void hideScript(View view) {
        if (view != null) {
            if (hide(view)) {
                this.scriptShowed = null;
            }
            if (hide(this.scriptUiButtonShowed)) {
                this.scriptUiButtonShowed = null;
            }
        }
    }

    public void hideScript() {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ShowApp.5
            @Override // java.lang.Runnable
            public void run() {
                View view = ShowApp.this.scriptShowed;
                if (view != null) {
                    if (MainService.instance.scriptInterrupt == null || MainService.instance.mainDialog == null) {
                        ShowApp.this.hideScript(view);
                    }
                }
            }
        });
    }

    public void showRecord() {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ShowApp.6
            @Override // java.lang.Runnable
            public void run() {
                if (ShowApp.this.hide(ShowApp.this.recordShowed)) {
                    ShowApp.this.recordShowed = null;
                }
                MainService service = MainService.instance;
                if (service != null) {
                    View view = service.recordInterrupt;
                    if (ShowApp.this.show(view, ShowApp.this.scriptParams)) {
                        ShowApp.this.recordShowed = view;
                    }
                }
            }
        });
    }

    public void hideRecord() {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ShowApp.7
            @Override // java.lang.Runnable
            public void run() {
                if (ShowApp.this.hide(ShowApp.this.recordShowed)) {
                    ShowApp.this.recordShowed = null;
                }
            }
        });
    }

    public void show() {
        showScript();
        showRecord();
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ShowApp.8
            @Override // java.lang.Runnable
            public void run() {
                if (ShowApp.this.iconShowed && ShowApp.this.hide(ShowApp.this)) {
                    ShowApp.this.iconShowed = false;
                }
                if (ShowApp.this.show(ShowApp.this, ShowApp.this.layoutParams)) {
                    ShowApp.this.iconShowed = true;
                }
            }
        });
    }

    public void update() {
        if (this.scriptShowed != null) {
            showScript();
        }
        if (this.recordShowed != null) {
            showRecord();
        }
        if (this.iconShowed) {
            show();
        }
    }

    public void showHint() {
        if (this.scriptShowed == null) {
            showScript();
        }
        if (this.recordShowed == null) {
            showRecord();
        }
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ShowApp.9
            @Override // java.lang.Runnable
            public void run() {
                if (ShowApp.this.hintShowed && ShowApp.this.hide(ShowApp.this.icon)) {
                    ShowApp.this.hintShowed = false;
                }
                if (ShowApp.this.show(ShowApp.this.icon, ShowApp.this.iconParams)) {
                    ShowApp.this.hintShowed = true;
                }
            }
        });
    }

    public void hideHint() {
        hideScript();
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ShowApp.10
            @Override // java.lang.Runnable
            public void run() {
                if (ShowApp.this.hintShowed && ShowApp.this.hide(ShowApp.this.icon)) {
                    ShowApp.this.hintShowed = false;
                }
            }
        });
    }

    public void hide(final boolean hideScript) {
        if (hideScript) {
            hideScript();
        } else {
            ThreadManager.getHandlerUiThread().postDelayed(this.hideScriptStop, 2000L);
        }
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ShowApp.11
            @Override // java.lang.Runnable
            public void run() {
                if (ShowApp.this.hide(ShowApp.this)) {
                    ShowApp.this.iconShowed = false;
                }
                if (hideScript || !ShowApp.this.hide(ShowApp.this.scriptUiButtonShowed)) {
                    return;
                }
                ShowApp.this.scriptUiButtonShowed = null;
            }
        });
    }

    public static void register(Window w) {
        WindowManager.LayoutParams params = w.getAttributes();
        ArrayList<WindowStatus> oldWins = wins;
        ArrayList<WindowStatus> newWins = new ArrayList<>();
        for (WindowStatus win : oldWins) {
            Window window = win.ref.get();
            if (window != null && window != w) {
                newWins.add(win);
            }
        }
        newWins.add(new WindowStatus(new WeakReference(w), (params.flags & 2) != 0, params.dimAmount));
        wins = newWins;
        oldWins.clear();
        oldWins.trimToSize();
    }

    static class WindowStatus {
        float dimAmount;
        WeakReference<Window> ref;
        boolean useDim;

        public WindowStatus(WeakReference<Window> ref, boolean useDim, float dimAmount) {
            this.useDim = false;
            this.dimAmount = 0.0f;
            this.ref = ref;
            this.useDim = useDim;
            this.dimAmount = dimAmount;
        }
    }
}
