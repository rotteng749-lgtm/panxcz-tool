package android.ext;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

/* JADX INFO: loaded from: classes.dex */
public class WindowCallbackWrapper implements Window.Callback {
    Window.Callback source;
    private InternalKeyboard kbdView = null;
    private boolean calledStarting = false;
    private boolean calledStarted = false;
    private boolean calledFinished = false;

    public WindowCallbackWrapper(Window.Callback source) {
        this.source = source;
    }

    @Override // android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            boolean ret = this.source.dispatchKeyEvent(event);
            return ret;
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.view.Window.Callback
    @TargetApi(11)
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        try {
            boolean ret = this.source.dispatchKeyShortcutEvent(event);
            return ret;
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            boolean ret = this.source.dispatchTouchEvent(event);
            return ret;
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTrackballEvent(MotionEvent event) {
        return this.source.dispatchTrackballEvent(event);
    }

    @Override // android.view.Window.Callback
    @TargetApi(12)
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        try {
            return this.source.dispatchGenericMotionEvent(event);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.view.Window.Callback
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return this.source.dispatchPopulateAccessibilityEvent(event);
    }

    @Override // android.view.Window.Callback
    public View onCreatePanelView(int featureId) {
        return this.source.onCreatePanelView(featureId);
    }

    @Override // android.view.Window.Callback
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return this.source.onCreatePanelMenu(featureId, menu);
    }

    @Override // android.view.Window.Callback
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        return this.source.onPreparePanel(featureId, view, menu);
    }

    @Override // android.view.Window.Callback
    public boolean onMenuOpened(int featureId, Menu menu) {
        return this.source.onMenuOpened(featureId, menu);
    }

    @Override // android.view.Window.Callback
    public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
        return this.source.onMenuItemSelected(featureId, item);
    }

    @Override // android.view.Window.Callback
    public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
        try {
            this.source.onWindowAttributesChanged(attrs);
        } catch (IllegalArgumentException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.view.Window.Callback
    public void onContentChanged() {
        this.source.onContentChanged();
    }

    public void setKeyboard(InternalKeyboard kbdView) {
        this.kbdView = kbdView;
    }

    @Override // android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        try {
            this.source.onWindowFocusChanged(hasFocus);
        } catch (IllegalArgumentException e) {
            Log.badImplementation(e);
        }
        if (this.kbdView != null) {
            this.kbdView.onWindowFocusChanged();
        }
    }

    @Override // android.view.Window.Callback
    public void onAttachedToWindow() {
        this.source.onAttachedToWindow();
    }

    @Override // android.view.Window.Callback
    @SuppressLint({"MissingSuperCall"})
    public void onDetachedFromWindow() {
        this.source.onDetachedFromWindow();
    }

    @Override // android.view.Window.Callback
    public void onPanelClosed(int featureId, Menu menu) {
        this.source.onPanelClosed(featureId, menu);
    }

    @Override // android.view.Window.Callback
    public boolean onSearchRequested() {
        return this.source.onSearchRequested();
    }

    @Override // android.view.Window.Callback
    @TargetApi(11)
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        if (this.calledStarting) {
            return null;
        }
        this.calledStarting = true;
        ActionMode mode = null;
        try {
            mode = this.source.onWindowStartingActionMode(callback);
            if (mode == null) {
                ActionModeImpl actionMode = new ActionModeImpl(callback);
                mode = actionMode;
                if (callback.onCreateActionMode(mode, mode.getMenu())) {
                    actionMode.invalidate();
                    actionMode.show();
                }
            }
            return mode;
        } catch (Throwable e) {
            Log.e("Fail on onWindowStartingActionMode", e);
            return mode;
        } finally {
            this.calledStarting = false;
        }
    }

    @Override // android.view.Window.Callback
    @TargetApi(11)
    public void onActionModeStarted(ActionMode mode) {
        if (!this.calledStarted) {
            this.calledStarted = true;
            try {
                this.source.onActionModeStarted(mode);
            } finally {
                this.calledStarted = false;
            }
        }
    }

    @Override // android.view.Window.Callback
    @TargetApi(11)
    public void onActionModeFinished(ActionMode mode) {
        if (!this.calledFinished) {
            this.calledFinished = true;
            try {
                this.source.onActionModeFinished(mode);
                mode.finish();
            } finally {
                this.calledFinished = false;
            }
        }
    }

    @Override // android.view.Window.Callback
    @TargetApi(23)
    public boolean onSearchRequested(SearchEvent searchEvent) {
        return this.source.onSearchRequested(searchEvent);
    }

    @Override // android.view.Window.Callback
    @TargetApi(23)
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
        try {
            ActionMode mode = this.source.onWindowStartingActionMode(callback, type);
            if (mode == null && type == 0) {
                return onWindowStartingActionMode(callback);
            }
            return mode;
        } catch (Throwable e) {
            Log.e("Fail on onWindowStartingActionMode", e);
            return null;
        }
    }
}
