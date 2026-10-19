package android.ext;

import android.content.SharedPreferences;
import android.fix.ToolbarButton;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public abstract class MenuItem implements View.OnClickListener {
    public static final int NO_INDEX = -1;
    public static final String SP_USAGE = "m-";
    public static final int TAB_BITS = 64;
    public static final int TAB_COUNT = 3;
    public static final long[] USAGE = new long[3];
    private int icon;
    private WeakReference<Drawable> iconCache;
    private int index;
    protected String title;

    @Override // android.view.View.OnClickListener
    public abstract void onClick(View view);

    public MenuItem(String title) {
        this.icon = 0;
        this.iconCache = new WeakReference<>(null);
        this.index = -1;
        this.title = title;
    }

    public MenuItem(int title) {
        this.icon = 0;
        this.iconCache = new WeakReference<>(null);
        this.index = -1;
        this.title = Re.s(title);
    }

    public MenuItem(String title, int resId) {
        this.icon = 0;
        this.iconCache = new WeakReference<>(null);
        this.index = -1;
        this.title = title;
        this.icon = resId;
    }

    public MenuItem(int title, int resId) {
        this.icon = 0;
        this.iconCache = new WeakReference<>(null);
        this.index = -1;
        this.title = Re.s(title);
        this.icon = resId;
    }

    public final ImageView getButton() {
        return getButton(false);
    }

    public ImageView getButton(boolean fromUI) {
        ToolbarButton button = new ToolbarButton(Tools.getContext());
        button.setIcon(this.icon);
        View.OnClickListener listener = !fromUI ? this : new View.OnClickListener() { // from class: android.ext.MenuItem.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                MenuItem.this.onClickFromUI(v);
            }
        };
        button.setOnClickListener(listener);
        button.setTag(this);
        return button;
    }

    public Drawable getDrawable() {
        if (this.icon == 0) {
            return null;
        }
        Drawable cache = this.iconCache.get();
        if (cache == null) {
            Drawable cache2 = Tools.getDrawable(this.icon);
            this.iconCache = new WeakReference<>(cache2);
            return cache2;
        }
        return cache;
    }

    public void setIndex(int tab, int offset) {
        this.index = (tab * 64) + offset;
    }

    private static void saveUsage() {
        SPEditor ed = new SPEditor();
        for (int i = 0; i < 3; i++) {
            ed.putLong(SP_USAGE + i, USAGE[i], 0L);
        }
        ed.commit();
    }

    public static String getUsage() {
        SharedPreferences sp = Tools.getSharedPreferences();
        SharedPreferences.Editor ed = sp.edit();
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            String key = SP_USAGE + i;
            long val = sp.getLong(key, 0L);
            ed.remove(key);
            if (val != 0) {
                out.append("&m");
                out.append(i);
                out.append('=');
                out.append(Long.toHexString(val));
            }
        }
        ed.commit();
        return out.toString();
    }

    public void onClickFromUI(View v) {
        int tab;
        int index = this.index;
        if (index != -1 && (tab = index / 64) >= 0 && tab < 3) {
            long bit = 1 << (index % 64);
            long old = USAGE[tab];
            if ((old & bit) == 0) {
                long[] jArr = USAGE;
                jArr[tab] = jArr[tab] | bit;
                saveUsage();
            }
        }
        onClick(v);
    }

    public String toString() {
        return this.title;
    }
}
