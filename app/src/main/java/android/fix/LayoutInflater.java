package android.fix;

import android.annotation.TargetApi;
import android.content.Context;
import android.ext.Config;
import android.ext.Log;
import android.ext.Tools;
import android.os.Build;
import android.view.ContextThemeWrapper;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import catch_.me_.if_.you_.can_.R;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class LayoutInflater extends android.view.LayoutInflater {
    private static final int[] themes;
    private android.view.LayoutInflater base;
    private Context context;

    public static LayoutInflater getInflater() {
        return (LayoutInflater) SystemService.wrap(from(Tools.getContext()));
    }

    public static View inflateStatic(int resource, ViewGroup root) {
        return getInflater().inflate(resource, root);
    }

    public static View inflateStatic(XmlPullParser parser, ViewGroup root) {
        return getInflater().inflate(parser, root);
    }

    public static View inflateStatic(int resource, ViewGroup root, boolean attachToRoot) {
        return getInflater().inflate(resource, root, attachToRoot);
    }

    public static View inflateStatic(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        return getInflater().inflate(parser, root, attachToRoot);
    }

    public LayoutInflater(android.view.LayoutInflater parent) {
        super(parent, parent.getContext());
        this.base = null;
        this.base = parent;
        this.context = parent.getContext();
    }

    public boolean equals(Object o) {
        return this.base == null ? super.equals(o) : this.base.equals(o);
    }

    @Override // android.view.LayoutInflater
    public android.view.LayoutInflater cloneInContext(Context newContext) {
        return new LayoutInflater(this.base.cloneInContext(newContext));
    }

    @Override // android.view.LayoutInflater
    public Context getContext() {
        return this.base == null ? super.getContext() : this.base.getContext();
    }

    @Override // android.view.LayoutInflater
    public void setFactory(android.view.LayoutInflater.Factory factory) {
        if (this.base == null) {
            super.setFactory(factory);
        } else {
            this.base.setFactory(factory);
        }
    }

    public int hashCode() {
        return this.base == null ? super.hashCode() : this.base.hashCode();
    }

    @Override // android.view.LayoutInflater
    @TargetApi(11)
    public void setFactory2(android.view.LayoutInflater.Factory2 factory) {
        if (this.base == null) {
            super.setFactory2(factory);
        } else {
            this.base.setFactory2(factory);
        }
    }

    @TargetApi(11)
    public void setPrivateFactory(android.view.LayoutInflater.Factory2 factory) {
        Object receiver = this.base == null ? this : this.base;
        try {
            Method m = android.view.LayoutInflater.class.getDeclaredMethod("setPrivateFactory", android.view.LayoutInflater.Factory2.class);
            m.invoke(receiver, factory);
        } catch (Throwable e) {
            Log.e("setPrivateFactory failed call", e);
        }
    }

    @Override // android.view.LayoutInflater
    public android.view.LayoutInflater.Filter getFilter() {
        return this.base == null ? super.getFilter() : this.base.getFilter();
    }

    @Override // android.view.LayoutInflater
    public void setFilter(android.view.LayoutInflater.Filter filter) {
        if (this.base == null) {
            super.setFilter(filter);
        } else {
            this.base.setFilter(filter);
        }
    }

    public String toString() {
        return this.base == null ? super.toString() : this.base.toString();
    }

    @Override // android.view.LayoutInflater
    public View inflate(int resource, ViewGroup root) {
        return inflate(resource, root, root != null);
    }

    @Override // android.view.LayoutInflater
    public View inflate(XmlPullParser parser, ViewGroup root) {
        return inflate(parser, root, root != null);
    }

    @Override // android.view.LayoutInflater
    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        return inflate(resource, null, root, attachToRoot);
    }

    @Override // android.view.LayoutInflater
    public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        return inflate(0, parser, root, attachToRoot);
    }

    private View inflate(int resource, XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        if (this.base == null) {
            return parser == null ? super.inflate(resource, root, attachToRoot) : super.inflate(parser, root, attachToRoot);
        }
        try {
            return inflateTry(resource, parser, root, attachToRoot);
        } catch (InflateException e) {
            if (Build.VERSION.SDK_INT >= 14 && (isCausedBy(e, "tab_indicator_holo") || isCausedBy(e, "tab_selected_holo"))) {
                Log.e("Try fix", e);
                return inflateTry(R.layout.tab_indicator_holo, null, root, attachToRoot);
            }
            throw e;
        }
    }

    private boolean isCausedBy(Throwable e, String check) {
        if (check == null || e == null) {
            return false;
        }
        String msg = e.getMessage();
        if (msg == null || !msg.contains(check)) {
            return isCausedBy(e.getCause(), check);
        }
        return true;
    }

    private View inflateTry(int resource, XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        View viewInflate = null;
        RuntimeException ex = null;
        for (int theme : themes) {
            android.view.LayoutInflater prev = this.base;
            try {
                prev = this.base;
                checkBase(theme);
                viewInflate = parser == null ? this.base.inflate(resource, root, attachToRoot) : this.base.inflate(parser, root, attachToRoot);
                return viewInflate;
            } catch (Exception e) {
                if (ex == null) {
                    ex = e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
                }
                if ((e instanceof InflateException) || (e instanceof InvocationTargetException)) {
                    Log.e("Exception on inflate with theme: " + theme, e);
                    if (Config.vSpaceReal) {
                        if (getContext().getPackageName().equals(Config.vSpacePkg)) {
                            continue;
                        } else {
                            this.base = this.base.cloneInContext(Config.fixContext(this.base.getContext()));
                            try {
                                viewInflate = parser == null ? this.base.inflate(resource, root, attachToRoot) : this.base.inflate(parser, root, attachToRoot);
                            } catch (InflateException e2) {
                                Log.e("Exception on inflate with theme: " + theme, e2);
                                this.base = prev;
                            }
                        }
                    }
                    this.base = prev;
                } else {
                    throw ex;
                }
            }
        }
        throw ex;
    }

    private void checkBase(int theme) {
        if (theme != 0) {
            Context curr = this.base.getContext();
            int currId = getThemeResId(curr);
            if (currId != theme) {
                this.base = this.base.cloneInContext(getContext(theme));
            }
        }
    }

    private Context getContext(int themeResId) {
        if (themeResId == 1) {
            Context ret = this.context;
            return ret;
        }
        Context ret2 = new ContextThemeWrapper(this.context, themeResId);
        return ret2;
    }

    private int getThemeResId(Context curr) {
        if (curr == this.context) {
            return 1;
        }
        if (!(curr instanceof ContextThemeWrapper)) {
            return 0;
        }
        try {
            Method m = ContextThemeWrapper.class.getDeclaredMethod("getThemeResId", new Class[0]);
            Object ret = m.invoke(curr, new Object[0]);
            if (!(ret instanceof Integer)) {
                return 0;
            }
            int id = ((Integer) ret).intValue();
            return id;
        } catch (Throwable e) {
            Log.e("getThemeResId failed call", e);
            return 0;
        }
    }

    static {
        ArrayList<Integer> th = new ArrayList<>();
        th.add(0);
        th.add(1);
        if (Build.VERSION.SDK_INT >= 14) {
            th.add(Integer.valueOf(android.R.style.Theme_DeviceDefault));
        }
        if (Build.VERSION.SDK_INT >= 21) {
            th.add(Integer.valueOf(android.R.style.Theme_Material));
        }
        if (Build.VERSION.SDK_INT >= 11) {
            th.add(Integer.valueOf(android.R.style.Theme_Holo));
        }
        th.add(Integer.valueOf(android.R.style.Theme));
        th.add(Integer.valueOf(android.R.style.Theme_Black));
        Field[] declaredFields = android.R.class.getFields();
        for (Field field : declaredFields) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && field.getName().startsWith("Theme") && field.getType().equals(Integer.TYPE)) {
                try {
                    th.add(Integer.valueOf(field.getInt(null)));
                } catch (IllegalAccessException e) {
                } catch (IllegalArgumentException e2) {
                }
            }
        }
        themes = new int[th.size()];
        int n = th.size();
        for (int i = 0; i < n; i++) {
            themes[i] = th.get(i).intValue();
        }
    }
}
