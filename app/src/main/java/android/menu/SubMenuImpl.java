package android.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class SubMenuImpl extends ActionMenu implements SubMenu {
    Drawable headerIcon;
    View headerView;
    Drawable icon;
    MenuItem item;
    ActionMenu parent;
    private CharSequence title;

    public SubMenuImpl(Context context, ActionMenu parent, MenuItem item) {
        super(context);
        this.title = "";
        this.headerIcon = null;
        this.icon = null;
        this.headerView = null;
        this.item = item;
        this.parent = parent;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderTitle(int titleRes) {
        return setHeaderTitle(this.mResources.getString(titleRes));
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderTitle(CharSequence title) {
        this.title = title;
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderIcon(int iconRes) {
        return setHeaderIcon(this.mResources.getDrawable(iconRes));
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderIcon(Drawable icon) {
        this.headerIcon = icon;
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderView(View view) {
        this.headerView = view;
        return this;
    }

    @Override // android.view.SubMenu
    public void clearHeader() {
        this.headerView = null;
        this.title = "";
        this.headerIcon = null;
    }

    @Override // android.view.SubMenu
    public SubMenu setIcon(int iconRes) {
        return setIcon(this.mResources.getDrawable(iconRes));
    }

    @Override // android.view.SubMenu
    public SubMenu setIcon(Drawable icon) {
        this.icon = icon;
        return this;
    }

    @Override // android.view.SubMenu
    public MenuItem getItem() {
        return this.item;
    }
}
