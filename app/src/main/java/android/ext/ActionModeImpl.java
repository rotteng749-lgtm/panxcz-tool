package android.ext;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.menu.ActionMenu;
import android.menu.ActionMenuItem;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

/* JADX INFO: loaded from: classes.dex */
@TargetApi(11)
public class ActionModeImpl extends ActionMode implements View.OnClickListener {
    private ActionMode.Callback callback;
    private WindowManager.LayoutParams mLayoutParams;
    private ActionMenu menu;
    private LinearLayout view;
    private CharSequence title = null;
    private CharSequence subtitle = null;
    private View customView = null;
    private boolean showed = false;
    private boolean finished = false;
    private Context context = MainService.context;

    public ActionModeImpl(ActionMode.Callback callback) {
        this.view = null;
        this.menu = null;
        this.menu = new ActionMenu(this.context);
        this.view = new android.fix.LinearLayout(this.context);
        this.view.setAlpha(1.0f);
        Drawable background = this.view.getBackground();
        int color = background instanceof ColorDrawable ? ((ColorDrawable) background).getColor() : 0;
        this.view.setBackgroundColor(color | (-16777216));
        this.mLayoutParams = new WindowManager.LayoutParams();
        this.mLayoutParams.type = SystemConstants.getTypePhone();
        this.mLayoutParams.width = -1;
        this.mLayoutParams.height = -2;
        this.mLayoutParams.format = -2;
        this.mLayoutParams.flags = SystemConstants.getFlags();
        this.mLayoutParams.gravity = 55;
        this.mLayoutParams.y = 0;
        this.mLayoutParams.alpha = 1.0f;
        this.callback = callback;
    }

    @Override // android.view.ActionMode
    public void setTitle(CharSequence title) {
        this.title = title;
    }

    @Override // android.view.ActionMode
    public void setTitle(int resId) {
        this.title = Re.s(resId);
    }

    @Override // android.view.ActionMode
    public void setSubtitle(CharSequence subtitle) {
        this.subtitle = subtitle;
    }

    @Override // android.view.ActionMode
    public void setSubtitle(int resId) {
        this.subtitle = Re.s(resId);
    }

    @Override // android.view.ActionMode
    public void setCustomView(View view) {
        this.customView = view;
        invalidate();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        android.view.MenuItem item = (android.view.MenuItem) v.getTag();
        if (item instanceof ActionMenuItem) {
            ((ActionMenuItem) item).invoke();
        }
        this.callback.onActionItemClicked(this, item);
    }

    @Override // android.view.ActionMode
    @TargetApi(16)
    public void invalidate() {
        try {
            this.callback.onPrepareActionMode(this, this.menu);
        } finally {
            this.view.removeAllViews();
            int size = this.menu.size();
            for (int i = 0; i < size; i++) {
                android.view.MenuItem item = this.menu.getItem(i);
                Button button = new android.fix.Button(this.context);
                button.setAlpha(1.0f);
                button.setFocusable(false);
                Drawable icon = item.getIcon();
                Tools.addIconToTextView(button, icon, 32);
                if (icon == null) {
                    button.setText(item.getTitle());
                }
                button.setTag(item);
                button.setOnClickListener(this);
                this.view.addView(button);
            }
            if (this.customView != null) {
                this.view.addView(this.customView);
            }
            this.view.invalidate();
            Log.d("invalidate");
        }
    }

    @Override // android.view.ActionMode
    public void finish() {
        if (!this.finished) {
            this.finished = true;
            hide();
            this.callback.onDestroyActionMode(this);
        }
    }

    @Override // android.view.ActionMode
    public Menu getMenu() {
        return this.menu;
    }

    @Override // android.view.ActionMode
    public CharSequence getTitle() {
        return this.title;
    }

    @Override // android.view.ActionMode
    public CharSequence getSubtitle() {
        return this.subtitle;
    }

    @Override // android.view.ActionMode
    public View getCustomView() {
        return this.customView;
    }

    @Override // android.view.ActionMode
    public MenuInflater getMenuInflater() {
        return new MenuInflater(this.context);
    }

    public void show() {
        if (!this.showed) {
            try {
                Tools.addView(this.view, this.mLayoutParams);
                this.showed = true;
            } catch (Throwable e) {
                Log.e("addView failed", e);
            }
        }
    }

    public void hide() {
        if (this.showed) {
            try {
                Tools.removeView(this.view);
            } catch (Throwable e) {
                Log.e("removeView failed", e);
            }
            this.showed = false;
        }
    }
}
