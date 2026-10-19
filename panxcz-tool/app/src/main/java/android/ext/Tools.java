package android.ext;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.ext.ExceptionHandler;
import android.ext.ParserNumbers;
import android.fix.FrameLayout;
import android.fix.LayoutInflater;
import android.fix.ScrollView;
import android.fix.ViewHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.lang.ProcessBuilder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.sup.ArrayListResults;
import android.sup.ContainerHelpers;
import android.sup.LongSparseArrayChecked;
import android.system.Os;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.StateSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import catch_.me_.if_.you_.can_.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import luaj.lib.OsLib;

/* JADX INFO: loaded from: classes.dex */
public class Tools {
    public static final int AID_USER = 100000;
    public static final int CLEAR = 0;
    public static final int COLORIZE = 4;
    public static final int DISABLE_COPY = 2;
    public static final char ERR_MARK = 8291;
    private static final String HIDDEN_PREFIX = "GG-";
    private static final int ICON_END = 4;
    private static final int ICON_LEFT = 1;
    private static final int ICON_RIGHT = 2;
    private static final int ICON_START = 3;
    private static final int ICON_UNKNOWN = 0;
    public static final int INVERT = 2;
    public static final Random RANDOM = new Random(System.currentTimeMillis());
    public static final int SCROLL_TO_END = 1;
    public static final int SET = 1;
    private static int allowFloatWindows;
    private static String cacheDir;
    private static volatile String dataDir;
    private static float density;
    private static volatile int dp2px48;
    static volatile int dp5;
    static volatile String[] fakeToReal;
    private static String filesDir;
    private static int fixToLegacy;
    private static String hiddenDir;
    private static volatile Object iPm;
    static InputMethodManager imm;
    private static volatile int isLandscape;
    private static volatile boolean overrideExec;
    private static volatile byte overrideNative;
    private static Pattern p;
    private static String pkg;
    private static volatile PackageManager pm;
    private static final Semaphore sePM;
    private static int[] unitTime;
    private static volatile WeakReference<Colorize[]> weakColorize;
    private static WeakReference<SharedPreferences> weakSp;

    static {
        Semaphore semaphore = null;
        allowFloatWindows = Build.VERSION.SDK_INT >= 23 ? -1 : 1;
        density = 0.0f;
        dp2px48 = -1;
        dp5 = -1;
        p = Pattern.compile("(\\d+)");
        pkg = null;
        filesDir = null;
        cacheDir = null;
        pm = null;
        dataDir = null;
        hiddenDir = null;
        sePM = new Semaphore(semaphore);
        unitTime = new int[]{R.string.unit_seconds_short, 60, R.string.unit_minutes_short, 60, R.string.unit_hours_short, 24, R.string.unit_days_short, 365, R.string.unit_years_short, -1};
        iPm = null;
        weakColorize = new WeakReference<>(null);
        fixToLegacy = 0;
        weakSp = new WeakReference<>(null);
        overrideExec = false;
        overrideNative = (byte) 0;
        fakeToReal = null;
        isLandscape = -1;
    }

    @TargetApi(23)
    static void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                if (!canDrawOverlays(getContext())) {
                    Log.w("canDrawOverlays = false");
                }
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
    }

    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                return Settings.canDrawOverlays(context);
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
        return true;
    }

    public static final class ViewWrapper extends FrameLayout {
        public ViewWrapper(Context context) {
            super(context);
        }
    }

    public static void addViewWithWrapper(final View view, final WindowManager.LayoutParams params) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.1
            @Override // java.lang.Runnable
            public void run() {
                Tools.removeViewWithWrapper(view);
                ViewWrapper wrapper = new ViewWrapper(view.getContext());
                wrapper.addView(view);
                Tools.addView(wrapper, params);
            }
        });
    }

    public static void removeViewWithWrapper(final View view) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.2
            @Override // java.lang.Runnable
            public void run() {
                ViewParent p2 = view.getParent();
                if (p2 instanceof ViewWrapper) {
                    ViewWrapper wrapper = (ViewWrapper) p2;
                    Tools.removeView(wrapper);
                    wrapper.removeAllViews();
                }
            }
        });
    }

    public static void addView(View view, WindowManager.LayoutParams params) {
        Log.d("dbg2: " + view + ' ' + params);
        setView(view, params, true);
    }

    public static void fixViewParams(Window window) {
        window.setAttributes(fixViewParams(window.getAttributes()));
    }

    public static WindowManager.LayoutParams fixViewParams(WindowManager.LayoutParams params) {
        if ((Config.configClient & 2) != 0) {
            params.flags |= AddressItem.FLAG_REVERT;
        } else {
            params.flags &= -16777217;
        }
        boolean useFloatWindows = SystemConstants.useFloatWindows;
        if (useFloatWindows) {
            if (allowFloatWindows == -1) {
                allowFloatWindows = canDrawOverlays(getContext()) ? 1 : 0;
            }
            if (allowFloatWindows == 0) {
                useFloatWindows = false;
                Log.d("canDrawOverlays = false; fixed");
            }
        }
        if (!useFloatWindows) {
            params.type = 2;
            IBinder token = BaseActivity.instance.getWindow().getAttributes().token;
            if (token == null) {
                token = BaseActivity.instance.getWindow().getDecorView().getRootView().getWindowToken();
            }
            params.token = token;
        } else if ((params.type == SystemConstants.getTypePhone() || params.type == SystemConstants.getTypeSystemAlert()) && params.token != null) {
            Log.d("token: null " + params.token);
            params.token = null;
        }
        if (Config.vSpaceReal && !Config.vSpaceGuest) {
            Log.d("Use 10 " + Config.vSpacePkg + " instead of " + params.packageName);
            params.packageName = Config.vSpacePkg;
        }
        return params;
    }

    private static void setView(final View view, final WindowManager.LayoutParams params, final boolean add) {
        fixViewParams(params);
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.3
            @Override // java.lang.Runnable
            public void run() {
                Tools.checkPermission();
                try {
                    if (add) {
                        MainService.instance.mWindowManager.addView(view, params);
                    } else {
                        MainService.instance.mWindowManager.updateViewLayout(view, params);
                    }
                } catch (WindowManager.BadTokenException e) {
                    String msg = e.getMessage();
                    if (SystemConstants.useFloatWindows && msg != null && msg.contains("permission denied") && params.type != SystemConstants.getTypeSystemAlert()) {
                        int old = params.type;
                        params.type = SystemConstants.getTypeSystemAlert();
                        try {
                            if (add) {
                                MainService.instance.mWindowManager.addView(view, params);
                            } else {
                                MainService.instance.mWindowManager.updateViewLayout(view, params);
                            }
                        } catch (WindowManager.BadTokenException e2) {
                            params.type = old;
                            throw e2;
                        }
                    } else {
                        throw e;
                    }
                } catch (IllegalArgumentException e3) {
                    Log.w("Fail", e3);
                } catch (NullPointerException e4) {
                    Log.badImplementation(e4);
                } catch (Throwable e5) {
                    Log.w("Fail", e5);
                }
                if (add) {
                    Tools.fixViews(view);
                }
            }
        });
    }

    public static void updateViewLayout(View view, WindowManager.LayoutParams params) {
        View target = view;
        ViewParent p2 = view.getParent();
        if (p2 instanceof ViewWrapper) {
            target = (ViewWrapper) p2;
        }
        setView(target, params, false);
    }

    public static void removeView(final View view) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    MainService.instance.mWindowManager.removeView(view);
                } catch (Throwable e) {
                    Log.e("Failed removeView: " + view, e);
                }
            }
        });
    }

    public static void removeViewImmediate(final View view) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.5
            @Override // java.lang.Runnable
            public void run() {
                try {
                    MainService.instance.mWindowManager.removeViewImmediate(view);
                } catch (Throwable e) {
                    Log.e("Failed removeViewImmediate: " + view, e);
                }
            }
        });
    }

    @TargetApi(16)
    public static void setImageAlpha(final ImageView image, final float alpha) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.6
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (Build.VERSION.SDK_INT < 16) {
                        image.setAlpha((int) alpha);
                    } else {
                        try {
                            image.setImageAlpha((int) alpha);
                        } catch (NoSuchMethodError e) {
                            Log.w("setImageAlpha 1", e);
                            image.setAlpha((int) alpha);
                        }
                    }
                } catch (NoSuchMethodError e2) {
                    Log.w("setImageAlpha 2", e2);
                }
            }
        });
    }

    public static String concat(String[] parts, String delimiter) {
        StringBuilder out = new StringBuilder();
        for (String part : parts) {
            if (out.length() != 0) {
                out.append(delimiter);
            }
            out.append(part);
        }
        return out.toString();
    }

    public static String removeNewLinesChars(String text) {
        StringBuilder fixed = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            boolean skip = false;
            for (int j = 0; j < 2; j++) {
                skip = skip || ch == '\n';
                if (j != 1) {
                    ch = (char) (ch - 3);
                }
            }
            if (skip) {
                fixed.append("");
            }
            fixed.append(ch);
        }
        return fixed.toString();
    }

    public static int getPid(Process p2) {
        try {
            Field f = p2.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            int pid = f.getInt(p2);
            f.setAccessible(false);
            return pid;
        } catch (Throwable th) {
            return -1;
        }
    }

    public static float dp2px(float dp) {
        Context context;
        float dens = density;
        if (dens == 0.0f && (context = getContext()) != null) {
            dens = context.getResources().getDisplayMetrics().density;
        }
        if (dens == 0.0f) {
            dens = 1.0f;
        }
        return dp * dens;
    }

    public static int dp2px48() {
        if (dp2px48 < 0) {
            dp2px48 = (int) dp2px(48.0f);
        }
        return dp2px48;
    }

    public static void addIconToTextView(final TextView tv, final Drawable icon, final int iconSize) {
        if (tv != null) {
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.7
                @Override // java.lang.Runnable
                public void run() {
                    if (Tools.dp5 == -1) {
                        Tools.dp5 = (int) (Tools.dp2px(12.0f) + 0.5f);
                    }
                    if (icon != null) {
                        int is = (int) Tools.dp2px(iconSize);
                        try {
                            icon.setBounds(0, 0, is, is);
                        } catch (Throwable e) {
                            Log.badImplementation(e);
                        }
                    }
                    try {
                        Object o = tv.getTag(R.id.icon);
                        int own = o instanceof Integer ? ((Integer) o).intValue() : 0;
                        Drawable[] old = null;
                        if (own == 0) {
                            Drawable[] abs = tv.getCompoundDrawables();
                            old = abs;
                            if (Build.VERSION.SDK_INT >= 17) {
                                Drawable[] rel = tv.getCompoundDrawablesRelative();
                                if (rel[0] != null) {
                                    own = 4;
                                    old = rel;
                                } else if (abs[0] != null) {
                                    own = 2;
                                } else if (Build.VERSION.SDK_INT < 18) {
                                    own = 1;
                                } else {
                                    own = 3;
                                    old = rel;
                                }
                            } else if (abs[0] != null) {
                                own = 2;
                            } else {
                                own = 1;
                            }
                            tv.setTag(R.id.icon, Integer.valueOf(own));
                        }
                        boolean abs2 = own == 1 || own == 2;
                        if (old == null) {
                            old = abs2 ? tv.getCompoundDrawables() : tv.getCompoundDrawablesRelative();
                        }
                        old[(own == 1 || own == 3) ? (char) 0 : (char) 2] = icon;
                        if (abs2) {
                            tv.setCompoundDrawables(old[0], old[1], old[2], old[3]);
                        } else {
                            tv.setCompoundDrawablesRelative(old[0], old[1], old[2], old[3]);
                        }
                    } catch (StringIndexOutOfBoundsException e2) {
                        Log.badImplementation(e2);
                    }
                    tv.setCompoundDrawablePadding(Tools.dp5);
                }
            });
        }
    }

    public static void setTextAppearance(ListView listView, final int textResId) {
        listView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() { // from class: android.ext.Tools.8
            @Override // android.view.ViewGroup.OnHierarchyChangeListener
            public void onChildViewAdded(View parent, View child) {
                try {
                    View v = child.findViewById(android.R.id.text1);
                    if (v instanceof TextView) {
                        TextView tv = (TextView) v;
                        if (textResId != 0) {
                            Tools.setTextAppearance(tv, textResId);
                        }
                    }
                } catch (Throwable e) {
                    Log.badImplementation(e);
                }
            }

            @Override // android.view.ViewGroup.OnHierarchyChangeListener
            public void onChildViewRemoved(View parent, View child) {
            }
        });
    }

    public static void addIconsToListViewItems(ListView listView, final CharSequence[] list, final Drawable[] icons, final int iconSize, final int textResId) {
        try {
            listView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() { // from class: android.ext.Tools.9
                @Override // android.view.ViewGroup.OnHierarchyChangeListener
                public void onChildViewRemoved(View parent, View child) {
                }

                @Override // android.view.ViewGroup.OnHierarchyChangeListener
                public void onChildViewAdded(View parent, View child) {
                    try {
                        View v = child.findViewById(android.R.id.text1);
                        if (v instanceof TextView) {
                            final TextView tv = (TextView) v;
                            if (textResId != 0) {
                                Tools.setTextAppearance(tv, textResId);
                            }
                            if (tv.getTag() == null) {
                                final CharSequence[] charSequenceArr = list;
                                final Drawable[] drawableArr = icons;
                                final int i = iconSize;
                                TextWatcher watcher = new TextWatcher() { // from class: android.ext.Tools.9.1
                                    @Override // android.text.TextWatcher
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        try {
                                            String text = s.toString();
                                            for (int i2 = 0; i2 < charSequenceArr.length; i2++) {
                                                if (charSequenceArr[i2].toString().equals(text)) {
                                                    Tools.addIconToTextView(tv, drawableArr[i2], i);
                                                    return;
                                                }
                                            }
                                        } catch (Throwable e) {
                                            Log.badImplementation(e);
                                        }
                                    }

                                    @Override // android.text.TextWatcher
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    }

                                    @Override // android.text.TextWatcher
                                    public void afterTextChanged(Editable s) {
                                        onTextChanged(s, 0, 0, 0);
                                    }
                                };
                                tv.addTextChangedListener(watcher);
                                tv.setTag(watcher);
                                watcher.onTextChanged(tv.getText(), 0, 0, 0);
                            }
                        }
                    } catch (Throwable e) {
                        Log.badImplementation(e);
                    }
                }
            });
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    public static void copyText(String text) {
        copyText(text, true);
    }

    @TargetApi(11)
    public static void copyText(final String text_, final boolean withHistory) {
        if (text_ != null) {
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.10
                @Override // java.lang.Runnable
                public void run() {
                    String text = text_.replace(" ", " ");
                    if (withHistory) {
                        History.add(text, 1);
                    }
                    try {
                        Object service = MainService.context.getSystemService("clipboard");
                        if (Build.VERSION.SDK_INT < 11) {
                            ClipboardManager clipboard = (ClipboardManager) service;
                            clipboard.setText(text);
                        } else {
                            android.content.ClipboardManager clipboard2 = (android.content.ClipboardManager) service;
                            clipboard2.setPrimaryClip(ClipData.newPlainText(text, text));
                        }
                        Tools.showToast(R.string.copied);
                    } catch (Throwable e) {
                        Log.e("Failed to copy text to the clipboard", e);
                        Log.d("Text: " + text);
                        Tools.showToast(String.valueOf(Re.s(R.string.failed_copy_text)) + ' ' + e.getMessage());
                    }
                }
            });
        }
    }

    public static void showToast(final String text, final int time) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.11
            @Override // java.lang.Runnable
            public void run() {
                try {
                    ToastManager.show(Toast.makeText(ToastManager.getContext(), Re.s(text == null ? "null" : text), time));
                } catch (Throwable e) {
                    Log.w("Toast fail", e);
                }
            }
        });
    }

    public static void showToast(int resId, int time) {
        showToast(Re.s(resId), time);
    }

    public static void showToast(String text) {
        showToast(text, 1);
    }

    public static void showToast(int resId) {
        showToast(Re.s(resId), 1);
    }

    public static class RunnableWrapper implements Runnable {
        final Runnable base;

        public static Runnable wrap(Runnable base) {
            if (!(base instanceof RunnableWrapper)) {
                return new RunnableWrapper(base);
            }
            return base;
        }

        public RunnableWrapper(Runnable base) {
            this.base = base;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.base.run();
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
    }

    public static class HandlerWrapper extends Handler {
        final Handler base;

        public static Handler wrap(Handler base) {
            if (!(base instanceof HandlerWrapper)) {
                return new HandlerWrapper(base);
            }
            return base;
        }

        public HandlerWrapper(Handler base) {
            super(base.getLooper());
            this.base = base;
        }

        @Override // android.os.Handler
        public void handleMessage(android.os.Message msg) {
            try {
                this.base.handleMessage(msg);
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
    }

    public static boolean hasEditText(View view) {
        if (view == null || view.getVisibility() != 0) {
            return false;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child;
                try {
                    child = viewGroup.getChildAt(i);
                } catch (ArrayIndexOutOfBoundsException e) {
                    continue;
                }
                if (child != null && child.getVisibility() == 0 && ((child instanceof android.widget.EditText) || ((child instanceof ViewGroup) && hasEditText(child)))) {
                    return true;
                }
            }
            return false;
        }
        if (!(view instanceof android.widget.EditText)) {
            return false;
        }
        return true;
    }

    public static View getViewForAttach(View view) {
        ViewParent parent;
        if (view != null && (parent = view.getParent()) != null && (parent instanceof ViewGroup)) {
            try {
                ((ViewGroup) parent).removeView(view);
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
        return view;
    }

    public static void fixLeak(TextView text) {
        try {
            ViewTreeObserver obs = text.getViewTreeObserver();
            if (obs != null) {
                obs.removeOnPreDrawListener(text);
            }
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    public static void dumpHierarchy(View view) {
        dump("", view);
    }

    private static void dump(String indent, View view) {
        Log.d(String.valueOf(indent) + view);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            String ind = String.valueOf(indent) + "  ";
            for (int i = 0; i < group.getChildCount(); i++) {
                try {
                    View child = group.getChildAt(i);
                    if (child != null) {
                        dump(ind, child);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }
        }
    }

    @TargetApi(16)
    public static void hideStatusBar(final Dialog dialog) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.12
            @Override // java.lang.Runnable
            public void run() {
                Window window = dialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                params.flags |= 1024;
                params.flags &= -2049;
                if (Build.VERSION.SDK_INT >= 11) {
                    params.systemUiVisibility = 1;
                    if (Build.VERSION.SDK_INT >= 16) {
                        View view = window.peekDecorView();
                        if (view != null) {
                            try {
                                view.setSystemUiVisibility(4);
                            } catch (Throwable e) {
                                Log.badImplementation(e);
                            }
                        }
                        params.systemUiVisibility = 4;
                        ActionBar actionBar = dialog.getActionBar();
                        if (actionBar != null) {
                            actionBar.hide();
                        }
                    }
                }
                window.setAttributes(params);
            }
        });
    }

    public static void externalKeyboard(Window window, View v, boolean show) {
        InputMethodManager imm2 = (InputMethodManager) getContext().getSystemService("input_method");
        WindowManager.LayoutParams lp = window.getAttributes();
        int mode = lp.softInputMode & (-16);
        if (show) {
            window.clearFlags(131072);
            window.setSoftInputMode(mode | 5);
            imm2.showSoftInput(v, 0);
        } else {
            imm2.hideSoftInputFromWindow(v.getWindowToken(), 0);
            window.addFlags(131072);
            window.setSoftInputMode(mode | 2);
            imm2.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static AlertDialog setClickableText(AlertDialog dialog) {
        try {
            TextView message = (TextView) dialog.findViewById(android.R.id.message);
            if (message != null) {
                setClickableText(message, message.getText().toString());
            }
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
        return dialog;
    }

    public static void setClickableText(final TextView view, final String text) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.13
            @Override // java.lang.Runnable
            public void run() {
                int i = 1;
                while (i < 4) {
                    try {
                        view.setLinksClickable(i <= 2);
                        view.setAutoLinkMask(i <= 2 ? 1 : 0);
                        view.setMovementMethod(i <= 1 ? LinkMovementMethod.getInstance() : null);
                        view.setText(text);
                        break;
                    } catch (Throwable e) {
                        Log.e("Failed set clickable text " + i, e);
                        i++;
                    }
                }
                if (i == 4) {
                    view.setText("Failed set text");
                }
                DefensiveURLSpan.fixTextView(view);
            }
        });
    }

    public static String trimDirPath(String text) {
        String text2 = text.trim();
        int last = text2.length();
        for (int i = last - 1; i > 0 && text2.charAt(i) == '/'; i--) {
            last--;
        }
        if (last < text2.length()) {
            return text2.substring(0, last);
        }
        return text2;
    }

    public static int getRamSizeKb() {
        int ret = -1;
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/meminfo", "r");
            Matcher m = p.matcher(reader.readLine());
            String value = "-2";
            while (m.find()) {
                value = m.group(1);
            }
            reader.close();
            ret = Integer.parseInt(value);
            return ret;
        } catch (Throwable ex) {
            Log.w("Failed get RAM size", ex);
            return ret;
        }
    }

    public static void hideKeyboard(WeakReference<AlertDialog> weakDialog) {
        AlertDialog dialog;
        if (weakDialog != null && (dialog = weakDialog.get()) != null) {
            hideKeyboard(dialog);
        }
    }

    public static void hideKeyboard(final AlertDialog dialog) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.14
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Tools.hideKeyboard(dialog.getWindow());
                } catch (Throwable e) {
                    Log.e("hideSoftInputFromWindow", e);
                }
            }
        });
    }

    public static void hideKeyboard(final Window window) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.15
            @Override // java.lang.Runnable
            public void run() {
                try {
                    View view = window.getCurrentFocus();
                    if (view == null) {
                        view = window.peekDecorView();
                    }
                    if (view != null) {
                        Tools.hideKeyboard(view);
                    }
                } catch (Throwable e) {
                    Log.e("hideSoftInputFromWindow", e);
                }
            }
        });
    }

    public static void hideKeyboard(final View view) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.16
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (Tools.imm == null) {
                        Tools.imm = (InputMethodManager) MainService.context.getSystemService("input_method");
                    }
                    if (view != null) {
                        Tools.imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } catch (Throwable e) {
                    Log.e("hideSoftInputFromWindow", e);
                }
            }
        });
    }

    public static void selectAll(final android.widget.EditText edit) {
        if (edit != null) {
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.17
                @Override // java.lang.Runnable
                public void run() {
                    Editable text = edit.getText();
                    if (text.length() > 0) {
                        try {
                            text.replace(0, 1, text.subSequence(0, 1), 0, 1);
                        } catch (Throwable e) {
                            Log.badImplementation(e);
                        }
                        edit.selectAll();
                    }
                }
            });
        }
    }

    public static int getSelectedCount(Object obj) {
        int count = 0;
        if (obj != null) {
            if (obj instanceof ArrayListResults) {
                ArrayListResults list = (ArrayListResults) obj;
                int s = list.size();
                for (int i = 0; i < s; i++) {
                    try {
                        if (list.checked(i)) {
                            count++;
                        }
                    } catch (IndexOutOfBoundsException e) {
                    }
                }
            } else if (obj instanceof LongSparseArrayChecked) {
                LongSparseArrayChecked list2 = (LongSparseArrayChecked) obj;
                int s2 = list2.size();
                for (int i2 = 0; i2 < s2; i2++) {
                    if (list2.checkAt(i2)) {
                        count++;
                    }
                }
            } else if (obj instanceof boolean[]) {
                boolean[] arr = (boolean[]) obj;
                int s3 = arr.length - 1;
                for (int i3 = 1; i3 < s3; i3++) {
                    if (arr[i3]) {
                        count++;
                    }
                }
            } else {
                throw new IllegalArgumentException();
            }
        }
        return count;
    }

    public static void changeSelection(Object obj, int mode) {
        if (obj != null) {
            boolean value = mode == 1;
            if (obj instanceof ArrayListResults) {
                ArrayListResults list = (ArrayListResults) obj;
                int s = list.size();
                for (int i = 0; i < s; i++) {
                    list.checked(i, mode == 2 ? !list.checked(i) : value);
                }
                return;
            }
            if (obj instanceof LongSparseArrayChecked) {
                LongSparseArrayChecked<SavedItem> list2 = (LongSparseArrayChecked) obj;
                int s2 = list2.size();
                for (int i2 = 0; i2 < s2; i2++) {
                    list2.setCheckAt(i2, mode == 2 ? !list2.checkAt(i2) : value);
                }
                return;
            }
            if (obj instanceof boolean[]) {
                boolean[] arr = (boolean[]) obj;
                int s3 = arr.length - 1;
                for (int i3 = 1; i3 < s3; i3++) {
                    arr[i3] = mode == 2 ? !arr[i3] : value;
                }
                return;
            }
            throw new IllegalArgumentException();
        }
    }

    public static boolean allowExecute(String filename) {
        File file = new File(filename);
        if (file.canExecute()) {
            return true;
        }
        String[] chmod = {"chmod", "0755", getRealPath(filename)};
        try {
            if (!waitForTimeout(exec(chmod), 15)) {
                Log.w("timeout fail 1: " + Arrays.toString(chmod), new RuntimeException());
            }
        } catch (Exception e) {
            Log.w("exec", e);
        }
        if (!Config.vSpace) {
            if (file.canExecute()) {
                return true;
            }
            try {
                if (!waitForTimeout(RootDetector.tryRoot("exec " + concat(chmod, " ")), 15)) {
                    Log.w("timeout fail 2: " + Arrays.toString(chmod), new RuntimeException());
                }
            } catch (Exception e2) {
                Log.w("root", e2);
            }
        }
        return file.canExecute();
    }

    public static boolean isExited(Process process) {
        try {
            process.exitValue();
            return true;
        } catch (IllegalThreadStateException e) {
            return false;
        }
    }

    public static boolean waitForTimeout(final Process process, int seconds) {
        FutureTask<Boolean> task = new FutureTask<>(new Callable<Boolean>() { // from class: android.ext.Tools.18
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Boolean call() throws Exception {
                process.waitFor();
                return true;
            }
        });
        new ExceptionHandler.ThreadEx(task, "waitForTimeout: " + process).start();
        try {
            return task.get(seconds, TimeUnit.SECONDS).booleanValue();
        } catch (Exception e) {
            return isExited(process);
        }
    }

    public static void chmod(String path, String mode) {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                Os.chmod(path, Integer.parseInt(mode, 8));
            } else if (Build.VERSION.SDK_INT >= 12) {
                Object os = Class.forName("libcore.io.Libcore").getField("os").get(null);
                Class.forName("libcore.io.Os").getMethod("chmod", String.class, Integer.TYPE).invoke(os, path, Integer.valueOf(Integer.parseInt(mode, 8)));
            }
        } catch (Throwable e) {
            Log.w("Failed chmod " + mode + " '" + path + "'", e);
        }
        String[] chmod = {"chmod", mode, getRealPath(path)};
        try {
            if (!waitForTimeout(exec(chmod), 15)) {
                Log.w("timeout fail 1: " + Arrays.toString(chmod), new RuntimeException());
            }
        } catch (Exception e2) {
            Log.w("exec", e2);
        }
        try {
            if (!Config.vSpace && !waitForTimeout(RootDetector.tryRoot("exec " + concat(chmod, " ")), 15)) {
                Log.w("timeout fail 2: " + Arrays.toString(chmod), new RuntimeException());
            }
        } catch (Exception e3) {
            Log.w("root", e3);
        }
    }

    public static void chmod(File path, int mode) {
        path.setExecutable((mode & 64) != 0, (mode & 9) == 0);
        path.setWritable((mode & 128) != 0, (mode & 18) == 0);
        path.setReadable((mode & 256) != 0, (mode & 36) == 0);
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                Os.chmod(path.getAbsolutePath(), mode);
            } catch (Throwable e) {
                Log.w("Failed chmod " + Integer.toOctalString(mode) + " '" + path + "'", e);
            }
        }
    }

    public static Throwable isGoodContext(Context context) {
        try {
            context.getPackageName();
            context.getPackageManager();
            context.getCacheDir();
            context.getFilesDir();
            context.getExternalCacheDir();
            context.getExternalFilesDir(null);
            return null;
        } catch (Throwable th) {
            return th;
        }
    }

    public static void checkForBadContext() {
        ThreadManager.runOnLogThread(new Runnable() { // from class: android.ext.Tools.19
            @Override // java.lang.Runnable
            @TargetApi(19)
            public void run() {
                Throwable check;
                Throwable check2;
                List<Throwable> list = new ArrayList<>();
                StringBuilder report = new StringBuilder();
                report.append("Context info. act - ");
                report.append(Tools.getInfoAboutContext(BaseActivity.context));
                report.append("; app - ");
                report.append(Tools.getInfoAboutContext(BaseActivity.appContext));
                report.append("; dae - ");
                report.append(Tools.getInfoAboutContext(MainService.context));
                report.append("; bad - ");
                int bad = 0;
                Throwable check3 = Tools.isGoodContext(BaseActivity.context);
                if (check3 != null) {
                    bad = 0 + 1;
                    list.add(check3);
                }
                Throwable check4 = Tools.isGoodContext(BaseActivity.appContext);
                if (check4 != null) {
                    bad += 10;
                    list.add(check4);
                }
                if (MainService.context != null && (check2 = Tools.isGoodContext(MainService.context)) != null) {
                    bad += 100;
                    list.add(check2);
                }
                report.append(bad);
                String msg = report.toString();
                if (bad > 0) {
                    if (Build.VERSION.SDK_INT < 19) {
                        for (Throwable e : list) {
                            msg = String.valueOf(msg) + "\nSupressed: " + Log.getStackTraceString(e);
                        }
                        check = new RuntimeException(msg);
                    } else {
                        check = new RuntimeException(msg);
                        for (Throwable e2 : list) {
                            check.addSuppressed(e2);
                        }
                    }
                    ExceptionHandler.sendException(Thread.currentThread(), check, false);
                }
                Log.d(msg);
            }
        });
    }

    public static Context removeContextWrappers(Context context) {
        while (context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        return context;
    }

    public static String getInfoAboutContext(Context context) {
        StringBuilder out = new StringBuilder();
        out.append("Context: ");
        out.append(context);
        Context ctx = context;
        while (ctx != null && (ctx instanceof ContextWrapper)) {
            ContextWrapper wrap = (ContextWrapper) ctx;
            ctx = wrap.getBaseContext();
            out.append(", base: ");
            out.append(ctx);
        }
        return out.toString();
    }

    public static Context getContext() {
        Context context = MainService.context;
        if (context == null && BaseActivity.context != null) {
            if (removeContextWrappers(BaseActivity.context) == null) {
                return BaseActivity.appContext;
            }
            return BaseActivity.context;
        }
        return context;
    }

    public static Drawable getDrawable(int resId) {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                Drawable out = getContext().getDrawable(resId);
                return out;
            } catch (NoSuchMethodError e) {
                Drawable out2 = getContext().getResources().getDrawable(resId);
                return out2;
            }
        }
        Drawable out3 = getContext().getResources().getDrawable(resId);
        return out3;
    }

    private static class MethodDescr {
        Class<? extends Object> cl;
        Class<?>[] defs;
        String meth;
        Method method = null;
        Object obj;
        Object[] params;

        public MethodDescr(Class<? extends Object> cl, String meth, Class<?>[] clsArr, Object obj, Object[] params) {
            this.cl = cl;
            this.meth = meth;
            this.defs = clsArr;
            this.obj = obj;
            this.params = params;
        }

        public MethodDescr check() throws NoSuchMethodException, SecurityException {
            this.method = this.cl.getMethod(this.meth, this.defs);
            return this;
        }

        public Object invoke() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            return this.method.invoke(this.obj, this.params);
        }
    }

    public static boolean isPackageInstalled(String pkg2) {
        try {
            getApplicationInfo(pkg2, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        } catch (Throwable e2) {
            Log.badImplementation(e2);
            return false;
        }
    }

    private static MethodDescr getMethodDescr(Class<? extends Object> cl, MethodDescr[] meths) {
        int preffered = Build.VERSION.SDK_INT > 15 ? 0 : 1;
        MethodDescr meth = null;
        try {
            meth = meths[preffered].check();
        } catch (NoSuchMethodException e) {
            for (int i = 0; i < meths.length; i++) {
                if (i != preffered) {
                    try {
                        meth = meths[i].check();
                    } catch (NoSuchMethodException e2) {
                    }
                }
            }
        }
        if (meth == null) {
            Log.d("Methods: " + Arrays.toString(cl.getMethods()));
        }
        return meth;
    }

    public static final String byteArrayToHexString(byte[] arr) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : arr) {
            String h = Integer.toHexString(b & 255).toUpperCase(Locale.US);
            while (h.length() < 2) {
                h = String.valueOf('0') + h;
            }
            hexString.append(h);
        }
        return hexString.toString();
    }

    public static Drawable getResized(Drawable icon, int px) {
        try {
            if (icon instanceof BitmapDrawable) {
                BitmapDrawable bd = (BitmapDrawable) icon;
                Bitmap b = bd.getBitmap();
                if (b == null) {
                    return icon;
                }
                if (b.getWidth() > px + 4 || b.getHeight() > px + 4) {
                    long t = System.currentTimeMillis();
                    Drawable icon2 = new BitmapDrawable(getContext().getResources(), Bitmap.createScaledBitmap(b, px, px, false));
                    Log.d("getResized: " + b.getWidth() + SearchMenuItem.XOR_MODE_SMALL + b.getHeight() + " -> " + px + SearchMenuItem.XOR_MODE_SMALL + px + " in " + (System.currentTimeMillis() - t));
                    return icon2;
                } else {
                    return icon;
                }
            } else {
                return icon;
            }
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
        return icon;
    }

    public static Drawable tryCloneIcon(Drawable icon) {
        if (icon != null) {
            try {
                Drawable.ConstantState cs = icon.getConstantState();
                if (cs != null) {
                    return cs.newDrawable();
                }
                return icon;
            } catch (Throwable e) {
                Log.badImplementation(e);
                return icon;
            }
        }
        return icon;
    }

    public static void init(Context context) {
        try {
            _init(context);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    public static void init(Activity activity) {
        try {
            _init(activity);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
        try {
            _init(activity.getApplication());
        } catch (Throwable e2) {
            Log.badImplementation(e2);
        }
        try {
            _init(activity.getApplicationContext());
        } catch (Throwable e3) {
            Log.badImplementation(e3);
        }
    }

    @SuppressLint({"SdCardPath"})
    private static void _init(Context context) {
        if (pm == null) {
            try {
                pm = context.getPackageManager();
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
        if (pkg == null) {
            try {
                pkg = context.getPackageName();
            } catch (Throwable e2) {
                Log.badImplementation(e2);
            }
        }
        if (pkg == null) {
            pkg = Apk.PACKAGE;
        }
        if (filesDir == null) {
            try {
                filesDir = context.getFilesDir().getAbsolutePath();
            } catch (Throwable e3) {
                Log.badImplementation(e3);
            }
        }
        if (cacheDir == null) {
            try {
                cacheDir = context.getCacheDir().getAbsolutePath();
            } catch (Throwable e4) {
                Log.badImplementation(e4);
            }
        }
        if (filesDir == null && pkg != null) {
            filesDir = String.valueOf(getDataDirSafe()) + "/files";
        }
        if (cacheDir == null && pkg != null) {
            cacheDir = String.valueOf(getDataDirSafe()) + "/cache";
        }
        if (filesDir != null) {
            try {
                File file = new File(filesDir);
                if (!file.exists()) {
                    file.mkdirs();
                }
            } catch (Throwable e5) {
                Log.badImplementation(e5);
            }
        }
        if (cacheDir != null) {
            try {
                File file2 = new File(cacheDir);
                if (!file2.exists()) {
                    file2.mkdirs();
                }
            } catch (Throwable e6) {
                Log.badImplementation(e6);
            }
        }
    }

    public static String getDataDirSafe() {
        StringBuilder d = new StringBuilder();
        d.append("/data/");
        int user = android.os.Process.myUid() / 100000;
        if (user == 0) {
            d.append("data");
        } else {
            d.append("user/");
            d.append(user);
        }
        d.append('/');
        String pkgName = pkg;
        if (pkgName == null) {
            pkgName = Apk.PACKAGE;
        }
        d.append(pkgName);
        return d.toString();
    }

    public static String getPackageName() {
        String ret = pkg;
        if (ret == null) {
            return Apk.PACKAGE;
        }
        return ret;
    }

    private static String getDataDir(String inner) {
        if (dataDir == null) {
            try {
                ApplicationInfo ai = getApplicationInfo(Apk.PACKAGE);
                if (ai != null) {
                    dataDir = ai.dataDir;
                }
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
        return (dataDir == null || inner == null) ? dataDir : String.valueOf(dataDir) + inner;
    }

    public static File getCacheDir() {
        String ret = cacheDir;
        if (ret == null) {
            ret = getDataDir("/cache");
        }
        if (ret != null) {
            return new File(ret);
        }
        return null;
    }

    public static File getFilesDir() {
        String ret = filesDir;
        if (ret == null) {
            ret = getDataDir("/files");
        }
        if (ret != null) {
            return new File(ret);
        }
        return null;
    }

    public static String getHiddenDir() {
        if (hiddenDir == null) {
            String suffix = null;
            try {
                SharedPreferences pref = getSharedPreferences();
                if (!pref.contains("hidden-dir")) {
                    String val = getHiddenDirFallback();
                    if (val == null) {
                        val = getRnd();
                    }
                    pref.edit().putString("hidden-dir", val).commit();
                }
                try {
                    suffix = pref.getString("hidden-dir", null);
                } catch (ClassCastException e) {
                }
                if (suffix == null) {
                    int val2 = pref.getInt("hidden-dir", 0);
                    if (val2 == 0) {
                        pref.edit().remove("hidden-dir").commit();
                    } else {
                        suffix = Integer.toString(val2);
                        pref.edit().putString("hidden-dir", suffix).commit();
                    }
                }
            } catch (Throwable e2) {
                Log.w("Failed get num", e2);
                suffix = getHiddenDirFallback();
            }
            if (suffix == null) {
                suffix = getRnd();
            }
            hiddenDir = HIDDEN_PREFIX + suffix;
        }
        return hiddenDir;
    }

    private static String getRnd() {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int val = RANDOM.nextInt(63);
            if (val < 10) {
                ret.append((char) (val + 48));
            } else if (val < 36) {
                ret.append((char) ((val + 65) - 10));
            } else {
                ret.append((char) (((val + 97) - 10) - 26));
            }
        }
        return ret.toString();
    }

    private static String getHiddenDirFallback() {
        String[] list;
        try {
            File dir = getFilesDir();
            if (dir == null || (list = dir.list()) == null) {
                return null;
            }
            for (String file : list) {
                if (file != null && file.startsWith(HIDDEN_PREFIX)) {
                    String ret = file.substring(HIDDEN_PREFIX.length());
                    return ret;
                }
            }
            return null;
        } catch (Throwable e) {
            Log.w("Failed get dir", e);
            return null;
        }
    }

    public static File getCacheDirHidden() {
        File ret = getCacheDir();
        if (ret != null) {
            File ret2 = new File(ret, getHiddenDir());
            ret2.mkdirs();
            return ret2;
        }
        return ret;
    }

    public static File getFilesDirHidden() {
        File ret = getFilesDir();
        if (ret != null) {
            File ret2 = new File(ret, getHiddenDir());
            ret2.mkdirs();
            return ret2;
        }
        return ret;
    }

    private static class Semaphore {
        public static final int S0 = 1;
        public static final int S1 = 2;
        public static final int S2 = 4;
        public static final int S3 = 8;
        public static final int S4 = 16;
        public static final int S5 = 32;
        public static final int S6 = 64;
        public static final int S7 = 128;
        public static final int S8 = 256;
        public static final int S9 = 512;
        private static final ThreadLocal<Integer> store = new ThreadLocal<Integer>() { // from class: android.ext.Tools.Semaphore.1
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.lang.ThreadLocal
            public Integer initialValue() {
                return 0;
            }
        };

        private Semaphore() {
        }

        /* synthetic */ Semaphore(Semaphore semaphore) {
            this();
        }

        public boolean use(int bit) {
            int val = store.get().intValue();
            if ((val & bit) != 0) {
                return false;
            }
            store.set(Integer.valueOf(val | bit));
            return true;
        }

        public void free(int bit) {
            store.set(Integer.valueOf(store.get().intValue() & (bit ^ (-1))));
        }
    }

    public static PackageManager getPackageManager() {
        if (pm == null && sePM.use(1)) {
            try {
                pm = BaseActivity.instance.getPackageManager();
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
            sePM.free(1);
        }
        if (pm == null && sePM.use(2)) {
            try {
                pm = BaseActivity.instance.getApplication().getPackageManager();
            } catch (Throwable e2) {
                Log.badImplementation(e2);
            }
            sePM.free(2);
        }
        if (pm == null && sePM.use(4)) {
            try {
                pm = BaseActivity.instance.getApplicationContext().getPackageManager();
            } catch (Throwable e3) {
                Log.badImplementation(e3);
            }
            sePM.free(4);
        }
        if (pm == null && sePM.use(8)) {
            try {
                pm = MainService.context.getPackageManager();
            } catch (Throwable e4) {
                Log.badImplementation(e4);
            }
            sePM.free(8);
        }
        if (pm == null && sePM.use(16)) {
            try {
                pm = MainService.context.getApplicationContext().getPackageManager();
            } catch (Throwable e5) {
                Log.badImplementation(e5);
            }
            sePM.free(16);
        }
        return pm;
    }

    public static void dismiss(WeakReference<AlertDialog> weakDialog) {
        AlertDialog dialog;
        if (weakDialog != null && (dialog = weakDialog.get()) != null) {
            dismiss(dialog);
        }
    }

    public static void dismiss(final DialogInterface dialog) {
        if (dialog == null) {
            throw new NullPointerException();
        }
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.20
            @Override // java.lang.Runnable
            public void run() {
                try {
                    dialog.dismiss();
                } catch (Throwable e) {
                    Log.badImplementation(e);
                }
            }
        });
    }

    public static int gcd(int a, int b) {
        while (b != 0) {
            int tmp = a % b;
            a = b;
            b = tmp;
        }
        return a;
    }

    public static long getFreeMem() {
        try {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) getContext().getSystemService("activity");
            if (activityManager == null) {
                return -1L;
            }
            activityManager.getMemoryInfo(mi);
            long availableMegs = mi.availMem / 1048576;
            return availableMegs;
        } catch (Throwable e) {
            Log.e("Failed get free mem", e);
            return -1L;
        }
    }

    public static String doubleToTime(double t) {
        long show;
        StringBuilder out = new StringBuilder();
        long time = Math.abs((long) t);
        for (int i = 0; i < unitTime.length; i += 2) {
            int size = unitTime[i + 1];
            if (size <= 0) {
                show = time;
                time = 0;
            } else {
                show = time % ((long) size);
                time /= (long) size;
            }
            if (show >= 0) {
                if (out.length() > 0) {
                    out.insert(0, ":");
                }
                if (i == 0) {
                    out.insert(0, stringFormat("%f", Double.valueOf(Math.abs(t) % ((double) size))).replaceFirst("\\D?0+$", ""));
                } else {
                    out.insert(0, show);
                }
            }
            if (time == 0) {
                break;
            }
        }
        if (out.length() == 0) {
            out.insert(0, "0");
        }
        if (t < 0.0d) {
            out.insert(0, "-");
        }
        return out.toString();
    }

    public static String longToTime(long t) {
        long show;
        StringBuilder out = new StringBuilder();
        long time = Math.abs(t);
        for (int i = 0; i < unitTime.length; i += 2) {
            int size = unitTime[i + 1];
            if (size <= 0) {
                show = time;
                time = 0;
            } else {
                show = time % ((long) size);
                time /= (long) size;
            }
            if (show > 0) {
                if (out.length() > 0) {
                    out.insert(0, " ");
                }
                out.insert(0, Re.s(unitTime[i]));
                out.insert(0, " ");
                out.insert(0, show);
            }
            if (time == 0) {
                break;
            }
        }
        if (out.length() == 0) {
            out.insert(0, Re.s(unitTime[0]));
            out.insert(0, "0 ");
        }
        if (t < 0) {
            out.insert(0, "-");
        }
        return out.toString();
    }

    public static double parseTime(String str) throws NumberFormatException {
        String str2 = str.trim();
        double mul = 1.0d;
        if (str2.startsWith("-")) {
            str2 = str2.substring(1);
            mul = -1.0d;
        }
        String[] parts = str2.split(":", unitTime.length / 2);
        double out = 0.0d;
        ParserNumbers.Result result = new ParserNumbers.Result();
        for (int i = 0; i < parts.length; i++) {
            int index = (((parts.length - 1) - i) * 2) + 1;
            if (index < unitTime.length) {
                double add = Double.longBitsToDouble(ParserNumbers.parseDouble(result, parts[i], str2).value);
                out = (((double) unitTime[index]) * out) + add;
            }
        }
        return out * mul;
    }

    public static String runTest() {
        String out = "";
        long[] time = {0, 1, 10, 80, 800, 8000, 80000, 800000, 8000000, 80000000, 800000000, 8000000000L};
        for (int i = 0; i < time.length; i++) {
            out = String.valueOf(out) + time[i] + ": " + longToTime(time[i]) + '\n';
        }
        return out;
    }

    public static boolean click(View v) {
        if (v == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 15) {
            return v.performClick();
        }
        return v.callOnClick();
    }

    public static void copy(String src, String dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                byte[] buf = new byte[8192];
                while (true) {
                    int len = in.read(buf);
                    if (len > 0) {
                        out.write(buf, 0, len);
                    } else {
                        return;
                    }
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public static String stringFormat(String format, Object... args) {
        String result;
        try {
            result = String.format(Locale.US, format.replace((char) 65292, ','), args);
        } catch (Throwable e) {
            String msg = "Failed String.format('" + format + "', " + Arrays.toString(args) + ") with '" + Locale.US + "'";
            Log.e(msg, e);
            ExceptionHandler.sendMessage(String.valueOf(msg) + "\n\n" + Log.getStackTraceString(e));
            if (format != null && args != null) {
                String[] parts = format.split("%(\\d+\\$)?([\\-#+ 0;\\(])?(\\d+)?(\\.\\d+)?([bBhHsScCdoxXeEfgGaA]|[tT][HIklMSLNpzZsQBbhAaCYyjmdeRTrDFc])", args.length + 1);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    if (i < parts.length) {
                        sb.append(parts[i]);
                    }
                    sb.append(args[i]);
                }
                if (args.length < parts.length) {
                    sb.append(parts[args.length]);
                }
                result = sb.toString().replace("%n", ListManager.NEW_LINE).replace("%%", "%");
            } else {
                result = format;
            }
        }
        if (result == null) {
            return "Failed call String.format";
        }
        return result;
    }

    public static String stringFormatOne(String format, String format2, String def, Object value) {
        String s = null;
        if (0 == 0) {
            try {
                s = String.format(Locale.US, format, value);
            } catch (Throwable e) {
                Log.w("Format fail 1", e);
            }
        }
        if (s == null) {
            try {
                s = String.format(Locale.US, format2, value);
            } catch (Throwable e2) {
                Log.w("Format fail 2", e2);
            }
        }
        if (s == null) {
            return def;
        }
        return s;
    }

    private static void disableSoundEffects(ViewGroup group) {
        if (group != null) {
            for (int i = 0; i < group.getChildCount(); i++) {
                try {
                    View child = group.getChildAt(i);
                    if (child != null) {
                        child.setSoundEffectsEnabled(false);
                        if (child instanceof ViewGroup) {
                            disableSoundEffects((ViewGroup) child);
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }
        }
    }

    public static View getRootView(View view) {
        View root = view.getRootView();
        if (root == null) {
            root = view;
            while (true) {
                Object parent = root.getParent();
                if (parent == null || !(parent instanceof View)) {
                    break;
                }
                root = (View) parent;
            }
        }
        return root;
    }

    public static void setUseSoundEffects(View view) {
        if ((Config.configClient & 32) == 0 && view != null) {
            View root = getRootView(view);
            root.setSoundEffectsEnabled(false);
            if (root instanceof ViewGroup) {
                disableSoundEffects((ViewGroup) root);
            }
        }
    }

    public static String stripColon(int res) {
        return stripColon(Re.s(res));
    }

    public static String stripColon(String text) {
        return stripEnd(text, ":");
    }

    private static String stripEnd(String str, String stripChars) {
        int end;
        if (str != null && (end = str.length()) != 0) {
            if (stripChars == null) {
                while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
                    end--;
                }
            } else if (!stripChars.isEmpty()) {
                while (end != 0) {
                    char ch = str.charAt(end - 1);
                    if (stripChars.indexOf(ch) == -1 && !Character.isWhitespace(ch)) {
                        break;
                    }
                    end--;
                }
            } else {
                return str;
            }
            return str.substring(0, end);
        }
        return str;
    }

    public static Intent getStartIntent(Context context, String packageName, String className) {
        if (context == null) {
            context = getContext();
        }
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            intent = new Intent("android.intent.action.MAIN");
        }
        intent.setPackage(packageName);
        if (className != null) {
            intent.setClassName(packageName, className);
        }
        intent.setFlags(AddressItem.FLAG_VALUE_NOT_EQUAL);
        return intent;
    }

    public static boolean openAppInfo(String pkg2) {
        Intent intent;
        try {
            Context context = getContext();
            try {
                intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", pkg2, null));
            } catch (ActivityNotFoundException e) {
                Log.w("Failed open app info for: " + pkg2, e);
                intent = new Intent("android.settings.MANAGE_APPLICATIONS_SETTINGS");
            }
            intent.setFlags(AddressItem.FLAG_VALUE_NOT_EQUAL);
            context.startActivity(intent);
            return true;
        } catch (Throwable e2) {
            Log.w("Failed open app info for: " + pkg2, e2);
            return false;
        }
    }

    public static boolean setButtonListener(DialogInterface dialog, int button, Object tag, View.OnClickListener listener) {
        if (dialog instanceof AlertDialog) {
            AlertDialog d = (AlertDialog) dialog;
            Button b = d.getButton(button);
            if (b != null) {
                b.setTag(tag);
                b.setOnClickListener(listener);
                return true;
            }
        }
        return false;
    }

    public static boolean setButtonListener(DialogInterface dialog, int button, Object tag, View.OnClickListener listener, EditText edit) {
        if (edit != null) {
            edit.requestFocus();
            selectAll(edit);
        }
        return setButtonListener(dialog, button, tag, listener);
    }

    public static View getCustomTitle(int resId) {
        return getCustomTitle(Re.s(resId));
    }

    public static View getCustomTitle(String title) {
        return getCustomTitle(title, null);
    }

    public static View getCustomTitle(String title, String subTitle) {
        return getCustomTitle(title, subTitle, -1);
    }

    public static View getCustomTitle(final String title, String subTitle, final int helpRes) {
        View view = LayoutInflater.inflateStatic(R.layout.dialog_title, (ViewGroup) null);
        TextView tv = (TextView) view.findViewById(R.id.alertTitle);
        tv.setText(title);
        if (helpRes != -1 && helpRes != 0) {
            setButtonHelpBackground(tv);
            tv.setOnClickListener(new View.OnClickListener() { // from class: android.ext.Tools.21
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    final AlertDialog dialog = Alert.create().setMessage(String.valueOf(Re.s(title)) + '\n' + Re.s(helpRes)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null).create();
                    Alert.show(dialog);
                    Alert.setOnShowListener(dialog, new DialogInterface.OnShowListener() { // from class: android.ext.Tools.21.1
                        @Override // android.content.DialogInterface.OnShowListener
                        public void onShow(DialogInterface d) {
                            Tools.setClickableText(dialog);
                        }
                    });
                }
            });
        }
        if (subTitle != null) {
            TextView sub = (TextView) view.findViewById(R.id.subTitle);
            sub.setText(subTitle);
            sub.setVisibility(0);
        }
        return view;
    }

    public static void fixDialogButtonsSize(AlertDialog dialog) {
        if (dialog != null) {
            Button[] buttons = {dialog.getButton(-1), dialog.getButton(-2), dialog.getButton(-3)};
            ViewGroup parent = null;
            for (Button btn : buttons) {
                if (btn != null && (parent = (ViewGroup) btn.getParent()) != null) {
                    break;
                }
            }
            if (parent != null) {
                int max = parent.getWidth();
                if (max == 0) {
                    parent.requestLayout();
                    max = parent.getWidth();
                    if (max == 0) {
                        max = parent.getMeasuredWidth();
                    }
                    if (max == 0) {
                        parent.measure(-1, -2);
                        max = parent.getMeasuredWidth();
                    }
                }
                if (max != 0 && !goodSize(max, parent, 0)) {
                    for (Button btn2 : buttons) {
                        if (btn2 != null && Build.VERSION.SDK_INT >= 14) {
                            btn2.setAllCaps(false);
                        }
                    }
                    if (!goodSize(max, parent, 5)) {
                        for (Button btn3 : buttons) {
                            if (btn3 != null) {
                                ColorStateList old = btn3.getTextColors();
                                setTextAppearance(btn3, R.style.SmallText);
                                btn3.setTextColor(old);
                            }
                        }
                        if (!goodSize(max, parent, 10)) {
                            String[][] sames = {new String[]{Re.s(R.string.cancel), Re.s(R.string.no)}, new String[]{Re.s(R.string.save), Re.s(R.string.yes), Re.s(R.string.ok)}, new String[]{Re.s(R.string.new_search), Re.s(R.string.new_search_short)}};
                            for (int s = 0; s < sames.length; s++) {
                                boolean changed = false;
                                String[] same = sames[s];
                                if (same != null) {
                                    int shortest = 0;
                                    for (int i = 0; i < same.length; i++) {
                                        if (same[shortest].length() >= same[i].length()) {
                                            shortest = i;
                                        }
                                    }
                                    for (Button btn4 : buttons) {
                                        if (btn4 != null) {
                                            String text = btn4.getText().toString();
                                            for (int i2 = 0; i2 < same.length; i2++) {
                                                if (i2 != shortest && same[i2].equals(text)) {
                                                    btn4.setText(same[shortest]);
                                                    changed = true;
                                                }
                                            }
                                        }
                                    }
                                    if (changed && goodSize(max, parent, s + 20)) {
                                        return;
                                    }
                                }
                            }
                            boolean changed2 = false;
                            for (Button btn5 : buttons) {
                                if (btn5 != null && btn5.getText().toString().indexOf(32) != -1) {
                                    btn5.setText(btn5.getText().toString().replace(' ', '\n'));
                                    changed2 = true;
                                }
                            }
                            if (!changed2 || !goodSize(max, parent, 40)) {
                                boolean changed3 = false;
                                if ((parent instanceof LinearLayout) && ((LinearLayout) parent).getOrientation() == 0) {
                                    ((LinearLayout) parent).setOrientation(1);
                                    changed3 = true;
                                }
                                if (!changed3 || !goodSize(max, parent, 100)) {
                                    goodSize(max, parent, -1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean goodSize(int max, ViewGroup parent, int check) {
        parent.requestLayout();
        parent.measure(-2, -2);
        int used = parent.getMeasuredWidth();
        boolean good = used <= max;
        if (check != 0 || !good) {
            Log.d("fixDialogButtonsSize: " + used + " <= " + max + " = " + good + " [" + check + ']');
        }
        return good;
    }

    public static Context getDarkContext() {
        return getDarkContext(getContext());
    }

    public static Context getDarkContext(Context context) {
        if (context != null && Build.VERSION.SDK_INT < 11) {
            return new ContextThemeWrapper(context, R.style.DarkFixTheme);
        }
        return context;
    }

    public static void alertBigText(String bigText, int flags) {
        alertBigText(bigText, flags, null, null);
    }

    public static void alertBigText(final String bigText, final int flags, final String action, final DialogInterface.OnClickListener listener) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Tools.22
            @Override // java.lang.Runnable
            public void run() {
                int err;
                View v = LayoutInflater.inflateStatic(R.layout.logcat, (ViewGroup) null);
                final TextView text = (TextView) v.findViewById(R.id.logcat_text);
                text.setText(bigText);
                if ((flags & 4) != 0 && (err = bigText.indexOf(8291)) >= 0) {
                    Spannable sp = new SpannableString(bigText);
                    BackgroundColorSpan fs = new BackgroundColorSpan(-7864320);
                    sp.setSpan(fs, err, err + 4, 33);
                    text.setText(sp);
                }
                final ScrollView view = (ScrollView) v.findViewById(R.id.logcat_page);
                view.setFastScrollEnabled(true);
                AlertDialog.Builder builder = Alert.create().setPositiveButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null).setView(v);
                if ((flags & 2) == 0) {
                    builder.setNegativeButton(R.string.copy, new DialogInterface.OnClickListener() { // from class: android.ext.Tools.22.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            Tools.copyText(text.getText().toString().trim(), false);
                        }
                    });
                }
                if (action != null) {
                    builder.setNeutralButton(action, listener);
                }
                AlertDialog dialog = builder.create();
                if ((flags & 1) != 0) {
                    Alert.setOnShowListener(dialog, new DialogInterface.OnShowListener() { // from class: android.ext.Tools.22.2
                        @Override // android.content.DialogInterface.OnShowListener
                        public void onShow(DialogInterface dialog2) {
                            ScrollView scrollView = view;
                            final ScrollView scrollView2 = view;
                            scrollView.postDelayed(new Runnable() { // from class: android.ext.Tools.22.2.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    scrollView2.fullScroll(130);
                                }
                            }, 100L);
                        }
                    });
                }
                Alert.show(dialog);
                Window win = dialog.getWindow();
                WindowManager.LayoutParams lp = win.getAttributes();
                lp.width = -1;
                win.setAttributes(lp);
            }
        });
    }

    public static void setButtonBackground(View view) {
        setButtonBackground(view, view.getBackground());
    }

    public static void setButtonHelpBackground(View view) {
        setButtonBackground(view, getDrawable(R.drawable.ic_information_white_12dp));
    }

    public static void setButtonBackground(View view, Drawable background) {
        if (background == null) {
            try {
                background = new ColorDrawable(0);
            } catch (Throwable e) {
                Log.badImplementation(e);
                return;
            }
        }
        view.setFocusable(true);
        StateListDrawable replace = new StateListDrawable();
        Drawable old = new ImageButton(view.getContext()).getBackground();
        replace.addState(ViewHelper.FOCUSED_STATE_SET_, old);
        replace.addState(ViewHelper.SELECTED_STATE_SET_, old);
        replace.addState(ViewHelper.PRESSED_STATE_SET_, old);
        replace.addState(StateSet.WILD_CARD, background);
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
        int left = view.getPaddingLeft();
        int right = view.getPaddingRight();
        int top = view.getPaddingTop();
        int bottom = view.getPaddingBottom();
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(replace);
        } else {
            view.setBackgroundDrawable(replace);
        }
        view.setPadding(left, top, right, bottom);
    }

    public static void setWidth(View v, float width) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        if (lp != null) {
            lp.width = (int) width;
            v.setLayoutParams(lp);
        }
    }

    public static void setHeight(View v, float height) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        if (lp != null) {
            lp.height = (int) height;
            v.setLayoutParams(lp);
        }
    }

    public static void setTextAppearance(TextView tv, int resId) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                tv.setTextAppearance(resId);
            } else {
                tv.setTextAppearance(tv.getContext(), resId);
            }
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    public static Object getIPackageManager() {
        if (iPm == null) {
            try {
                Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
                Method getIServiceManager = serviceManagerClass.getDeclaredMethod("getIServiceManager", new Class[0]);
                getIServiceManager.setAccessible(true);
                Object iServiceManager = getIServiceManager.invoke(null, ContainerHelpers.EMPTY_OBJECTS);
                Method getService = iServiceManager.getClass().getDeclaredMethod("getService", String.class);
                getService.setAccessible(true);
                Object iBinder = getService.invoke(iServiceManager, "package");
                Class<?> IPackageManagerStub = Class.forName("android.content.pm.IPackageManager$Stub");
                Method asInterface = IPackageManagerStub.getDeclaredMethod("asInterface", IBinder.class);
                asInterface.setAccessible(true);
                iPm = asInterface.invoke(null, iBinder);
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
        return iPm;
    }

    public static String getApkPath() {
        String ret = null;
        if (0 == 0) {
            try {
                Context context = getContext();
                if (context != null) {
                    ret = context.getPackageCodePath();
                }
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
        if (ret == null) {
            try {
                ApplicationInfo ai = getApplicationInfo(Apk.PACKAGE);
                if (ai != null) {
                    ret = ai.sourceDir;
                }
            } catch (Throwable e2) {
                Log.badImplementation(e2);
            }
        }
        if (ret == null) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/self/maps")));
                String odex = null;
                while (true) {
                    String line = in.readLine();
                    if (line == null) {
                        break;
                    }
                    if (line.contains(Apk.PACKAGE) && line.contains(".apk")) {
                        String line2 = String.valueOf(line.split("\\.apk", 2)[0]) + ".apk";
                        if (line2.indexOf(64) != -1) {
                            String[] parts = line2.trim().split("/");
                            odex = String.valueOf('/') + parts[parts.length - 1].trim().replace('@', '/');
                        } else {
                            ret = String.valueOf('/') + line2.split("/", 2)[1].trim();
                            break;
                        }
                    }
                }
                in.close();
                if (ret == null && odex != null) {
                    return odex;
                }
                return ret;
            } catch (Throwable e3) {
                Log.badImplementation(e3);
                return ret;
            }
        }
        return ret;
    }

    public static ApplicationInfo getApplicationInfo(String packageName) throws PackageManager.NameNotFoundException {
        try {
            return getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw e;
        } catch (Throwable e2) {
            throw new PackageManager.NameNotFoundException(String.valueOf(e2));
        }
    }

    public static ApplicationInfo getApplicationInfo(String packageName, int flags) throws Throwable {
        Object IPackageManager;
        boolean entered;
        ApplicationInfo ret = null;
        if (CheckNativeCrash.isBuggedPackage(packageName)) {
            Log.e("Failed getApplicationInfo, isBuggedPackage: " + packageName);
            return null;
        }
        PackageManager pm2 = getPackageManager();
        if (0 == 0 && pm2 != null) {
            try {
                entered = CheckNativeCrash.enter(packageName, "app_info");
                try {
                    ret = pm2.getApplicationInfo(packageName, flags);
                } finally {
                }
            } catch (Throwable e) {
                if (e instanceof PackageManager.NameNotFoundException) {
                    throw ((PackageManager.NameNotFoundException) e);
                }
                Log.e("Failed getApplicationInfo", e);
            }
        }
        if (ret == null && (IPackageManager = getIPackageManager()) != null) {
            Object ApplicationInfo = null;
            try {
                Class<?> cls = IPackageManager.getClass();
                MethodDescr[] meths = {new MethodDescr(cls, "getApplicationInfo", new Class[]{String.class, Integer.TYPE, Integer.TYPE}, IPackageManager, new Object[]{packageName, Integer.valueOf(flags), 0}), new MethodDescr(cls, "getApplicationInfo", new Class[]{String.class, Integer.TYPE}, IPackageManager, new Object[]{packageName, Integer.valueOf(flags)})};
                MethodDescr meth = getMethodDescr(cls, meths);
                if (meth != null) {
                    entered = CheckNativeCrash.enter(packageName, "app_info2");
                    try {
                        ApplicationInfo = meth.invoke();
                        if (ApplicationInfo == null) {
                            throw new PackageManager.NameNotFoundException(packageName);
                        }
                    } finally {
                    }
                }
                if (ApplicationInfo instanceof ApplicationInfo) {
                    return (ApplicationInfo) ApplicationInfo;
                }
                return ret;
            } catch (Throwable e2) {
                if (e2 instanceof PackageManager.NameNotFoundException) {
                    throw ((PackageManager.NameNotFoundException) e2);
                }
                Log.e("Failed getApplicationInfo", e2);
                return ret;
            }
        }
        return ret;
    }

    public static void describeActivities(String packageName, StringBuilder out) {
        StringBuilder msg = out == null ? new StringBuilder() : out;
        msg.append("\nActivities:\n");
        if (out == null) {
            Log.d(msg.toString().trim());
            msg = new StringBuilder();
        }
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageInfo(packageName, 1);
        } catch (Throwable th) {
        }
        if (pInfo == null) {
            msg.append("Null for: " + packageName);
            if (out == null) {
                Log.d(msg.toString().trim());
                new StringBuilder();
                return;
            }
            return;
        }
        if (pInfo.activities == null) {
            msg.append("Null");
            if (out == null) {
                Log.d(msg.toString().trim());
                new StringBuilder();
                return;
            }
            return;
        }
        for (ActivityInfo ai : pInfo.activities) {
            try {
                msg.append(ai);
                if (ai != null) {
                    msg.append('\n');
                    for (Field f : ActivityInfo.class.getFields()) {
                        int modifiers = f.getModifiers();
                        if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
                            msg.append(f.getName());
                            msg.append(": ");
                            try {
                                msg.append(f.get(ai));
                            } catch (Throwable e) {
                                msg.append(e);
                            }
                            msg.append(ListManager.TEXT_SEPARATOR);
                        }
                    }
                }
            } catch (Throwable e2) {
                msg.append(e2);
            }
            try {
                ComponentName cn = new ComponentName(packageName, ai.name);
                msg.append("enabled: ");
                msg.append(getPackageManager().getComponentEnabledSetting(cn));
                msg.append(ListManager.TEXT_SEPARATOR);
            } catch (Throwable e3) {
                msg.append(e3);
            }
            msg.append('\n');
            if (out == null) {
                Log.d(msg.toString().trim());
                msg = new StringBuilder();
            }
        }
    }

    public static PackageInfo getPackageInfo(String packageName) throws PackageManager.NameNotFoundException {
        try {
            return getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw e;
        } catch (Throwable e2) {
            throw new PackageManager.NameNotFoundException(String.valueOf(e2));
        }
    }

    public static PackageInfo getPackageInfo(String packageName, int flags) throws Throwable {
        Object IPackageManager;
        boolean entered;
        PackageInfo ret = null;
        if (CheckNativeCrash.isBuggedPackage(packageName)) {
            Log.e("Failed getPackageInfo, isBuggedPackage: " + packageName);
            return null;
        }
        PackageManager pm2 = getPackageManager();
        if (0 == 0 && pm2 != null) {
            try {
                entered = CheckNativeCrash.enter(packageName, "pkg_info");
                try {
                    ret = pm2.getPackageInfo(packageName, flags);
                } finally {
                }
            } catch (Throwable e) {
                if (e instanceof PackageManager.NameNotFoundException) {
                    throw ((PackageManager.NameNotFoundException) e);
                }
                Log.e("Failed getPackageInfo", e);
            }
        }
        if (ret == null && (IPackageManager = getIPackageManager()) != null) {
            Object PackageInfo = null;
            try {
                Class<?> cls = IPackageManager.getClass();
                MethodDescr[] meths = {new MethodDescr(cls, "getPackageInfo", new Class[]{String.class, Integer.TYPE, Integer.TYPE}, IPackageManager, new Object[]{packageName, Integer.valueOf(flags), 0}), new MethodDescr(cls, "getPackageInfo", new Class[]{String.class, Integer.TYPE}, IPackageManager, new Object[]{packageName, Integer.valueOf(flags)})};
                MethodDescr meth = getMethodDescr(cls, meths);
                if (meth != null) {
                    entered = CheckNativeCrash.enter(packageName, "pkg_info2");
                    try {
                        PackageInfo = meth.invoke();
                        if (PackageInfo == null) {
                            throw new PackageManager.NameNotFoundException(packageName);
                        }
                    } finally {
                    }
                }
                if (PackageInfo instanceof PackageInfo) {
                    return (PackageInfo) PackageInfo;
                }
                return ret;
            } catch (Throwable e2) {
                if (e2 instanceof PackageManager.NameNotFoundException) {
                    throw ((PackageManager.NameNotFoundException) e2);
                }
                Log.e("Failed getPackageInfo", e2);
                return ret;
            }
        }
        return ret;
    }

    public static String getApplicationLabel(ApplicationInfo info) {
        PackageManager packageManager = pm;
        if (info == null || packageManager == null) {
            return null;
        }
        CharSequence label = null;
        try {
            boolean entered = CheckNativeCrash.enter(info.packageName, "label");
            try {
                label = info.loadLabel(packageManager);
                if (label == null) {
                    entered = CheckNativeCrash.enter(info.packageName, "label2");
                    try {
                        label = packageManager.getApplicationLabel(info);
                    } finally {
                    }
                }
            } finally {
            }
        } catch (Throwable e) {
            Log.e("Failed load label for: " + info.packageName, e);
        }
        if (label != null) {
            return label.toString();
        }
        return null;
    }

    public static Drawable getApplicationIcon(ApplicationInfo info) {
        PackageManager packageManager = pm;
        if (info == null || packageManager == null) {
            return null;
        }
        Drawable icon = null;
        try {
            boolean entered = CheckNativeCrash.enter(info.packageName, "icon");
            try {
                icon = info.loadIcon(packageManager);
                if (icon == null) {
                    entered = CheckNativeCrash.enter(info.packageName, "icon2");
                    try {
                        icon = packageManager.getApplicationIcon(info);
                        return icon;
                    } finally {
                    }
                }
                return icon;
            } finally {
            }
        } catch (Throwable e) {
            Log.e("Failed load icon for: " + info.packageName, e);
            return icon;
        }
    }

    public static CharSequence colorizeText(CharSequence haystack, CharSequence needle, int color) {
        try {
            if (haystack instanceof Spannable) {
                Spannable sp = (Spannable) haystack;
                int len = needle.length();
                int offset = 0;
                String haystack2 = haystack.toString();
                String needle2 = needle.toString();
                while (true) {
                    int start = haystack2.indexOf(needle2, offset);
                    if (start < 0) {
                        break;
                    }
                    offset = start + len;
                    sp.setSpan(new ForegroundColorSpan(color), start, start + len, 33);
                }
            }
        } catch (Throwable e) {
            Log.w("Failed colorize text", e);
        }
        return haystack;
    }

    private static class Colorize {
        public final int color;
        public final CharSequence word;

        public Colorize(CharSequence word, int color) {
            this.word = word;
            this.color = color;
        }
    }

    public static CharSequence colorizeHelp(CharSequence help) {
        if (help instanceof Spannable) {
            Colorize[] colorize = weakColorize.get();
            if (colorize == null) {
                colorize = new Colorize[]{new Colorize(AddressItem.getNameShort(1), AddressItem.getColor(1)), new Colorize(AddressItem.getNameShort(2), AddressItem.getColor(2)), new Colorize(AddressItem.getNameShort(4), AddressItem.getColor(4)), new Colorize(AddressItem.getNameShort(8), AddressItem.getColor(8)), new Colorize(AddressItem.getNameShort(16), AddressItem.getColor(16)), new Colorize(AddressItem.getNameShort(32), AddressItem.getColor(32)), new Colorize(AddressItem.getNameShort(64), AddressItem.getColor(64)), new Colorize(Re.s(R.string.executable), getColor(R.color.pointer_executable)), new Colorize(Re.s(R.string.writable), getColor(R.color.pointer_writable)), new Colorize(Re.s(R.string.read_only), getColor(R.color.pointer_readable)), new Colorize(Re.s(R.string.writable_and_executable), getColor(R.color.pointer_executable_writable))};
                weakColorize = new WeakReference<>(colorize);
            }
            for (int i = 0; i < colorize.length; i++) {
                colorizeText(help, colorize[i].word, colorize[i].color);
            }
        }
        return help;
    }

    public static CharSequence colorize(CharSequence text, int color) {
        Spannable sp;
        try {
            if (text instanceof Spannable) {
                sp = (Spannable) text;
            } else {
                sp = new SpannableString(text);
                if (!(sp instanceof CharSequence)) {
                    Log.badImplementation(new RuntimeException("Class 'android.text.SpannableString' does not implement interface 'java.lang.CharSequence'"));
                    return text;
                }
            }
            sp.setSpan(new ForegroundColorSpan(color), 0, text.length(), 33);
            return sp;
        } catch (Throwable e) {
            Log.w("Failed colorize", e);
            return text;
        }
    }

    public static int getColor(int resId) {
        return getColor(resId, -1);
    }

    public static int getColor(int resId, int defaultColor) {
        try {
            Context context = getContext();
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    defaultColor = context.getColor(resId);
                    return defaultColor;
                } catch (Throwable e) {
                    Log.badImplementation(e);
                }
            }
            return context.getResources().getColor(resId);
        } catch (Throwable e2) {
            Log.badImplementation(e2);
            return defaultColor;
        }
    }

    public static void extractFile(String fileId, File output) {
        Resources r = getContext().getResources();
        int id = Re.i("chunk" + fileId, R.raw.class);
        if (id == 0) {
            Log.d("Nothing extract file " + fileId + ' ' + output.getAbsolutePath());
            throw new RuntimeException("Nothing extract");
        }
        try {
            byte[] buf = new byte[8192];
            OutputStream os = new FileOutputStream(output);
            InputStream is = r.openRawResource(id);
            while (true) {
                int read = is.read(buf);
                if (read > 0) {
                    os.write(buf, 0, read);
                } else {
                    is.close();
                    os.close();
                    return;
                }
            }
        } catch (IOException e) {
            Log.d("Failed extract file " + fileId + ' ' + output.getAbsolutePath(), e);
            throw new RuntimeException(e);
        }
    }

    public static String getSdcardPath() {
        String ret = null;
        try {
            ret = Environment.getExternalStorageDirectory().getAbsolutePath();
        } catch (Throwable e) {
            Log.e("Fail get sdcard path", e);
        }
        if (ret == null) {
            return "/sdcard";
        }
        if (ret.startsWith("/storage/emulated/0")) {
            if (fixToLegacy == 0) {
                fixToLegacy = (isGoodDir(ret) || !isGoodDir(new StringBuilder("/storage/emulated/legacy").append(ret.substring("/storage/emulated/0".length())).toString())) ? -1 : 1;
            }
            if (fixToLegacy > 0) {
                return "/storage/emulated/legacy" + ret.substring("/storage/emulated/0".length());
            }
            return ret;
        }
        return ret;
    }

    private static boolean isGoodDir(String path) {
        try {
            File dir = new File(path);
            String[] list = dir.list();
            if (list != null) {
                if (list.length == 0) {
                    String tmp = OsLib.TMP_PREFIX + System.nanoTime();
                    File file = new File(dir, tmp);
                    file.createNewFile();
                    list = dir.list();
                    file.delete();
                }
                if (list.length > 0) {
                    return true;
                }
            }
        } catch (Throwable e) {
            Log.w("isGoodDir: '" + path + "'", e);
        }
        return false;
    }

    public static ListView getListView(View view) {
        while (view != null) {
            if (view instanceof ListView) {
                ListView list = (ListView) view;
                return list;
            }
            Object parent = view.getParent();
            if (!(parent instanceof View)) {
                return null;
            }
            view = (View) parent;
        }
        return null;
    }

    public static SharedPreferences getSharedPreferences() {
        SharedPreferences ret = weakSp.get();
        if (ret == null) {
            SharedPreferences ret2 = getContext().getSharedPreferences(MainService.DEFAULT_STORAGE, 0);
            weakSp = new WeakReference<>(ret2);
            return ret2;
        }
        return ret;
    }

    public static boolean unsignedLess(long left, long right) {
        return (right < 0) ^ ((left < 0) ^ (left < right));
    }

    public static boolean unsignedLessOrEqual(long left, long right) {
        return left == right || unsignedLess(left, right);
    }

    public static void fixViews(View v) {
        View v2 = getRootView(v);
        setUseSoundEffects(v2);
        fixEdgeGlow(v2);
    }

    public static void fixEdgeGlow(View v) {
    }

    private static void fixEdgeGlow_(View view) {
        if (view != null) {
            boolean fix = false;
            if (view instanceof android.widget.ScrollView) {
                if (!(view instanceof ScrollView)) {
                    fix = true;
                }
            } else if (view instanceof HorizontalScrollView) {
                if (!(view instanceof android.fix.HorizontalScrollView)) {
                    fix = true;
                }
            } else if ((view instanceof AbsListView) && !(view instanceof android.fix.ListView)) {
                fix = true;
            }
            if (fix) {
                int mode = view.getOverScrollMode();
                if (mode != 2) {
                    Log.d("Clear OverScrollMode: " + mode + " for " + view);
                }
                view.setOverScrollMode(2);
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int count = viewGroup.getChildCount();
                for (int i = 0; i < count; i++) {
                    try {
                        fixEdgeGlow_(viewGroup.getChildAt(i));
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                }
            }
        }
    }

    public static String quoteFilter(String text) {
        return Pattern.quote(text).replace("^", "\\E^\\Q").replace("$", "\\E$\\Q").replace(Searcher.MASK_PLACEHOLDER, "\\E.\\Q").replace("*", "\\E.*\\Q").replace("\\Q\\E", "");
    }

    public static void setComponentEnabledSetting(int label, ComponentName cn, int state) {
        try {
            PackageManager pm2 = getPackageManager();
            Log.d("hide " + label + ": " + state + ' ' + cn);
            describeActivities(cn.getPackageName(), null);
            pm2.setComponentEnabledSetting(cn, state, 1);
            Log.d("hide " + (label + 1) + ": " + state + ' ' + cn);
            describeActivities(cn.getPackageName(), null);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    private static class FocusChangeListener implements View.OnFocusChangeListener {
        private final ArrayList<View.OnFocusChangeListener> list;

        private FocusChangeListener() {
            this.list = new ArrayList<>();
        }

        /* synthetic */ FocusChangeListener(FocusChangeListener focusChangeListener) {
            this();
        }

        void add(View.OnFocusChangeListener l) {
            if (l != null && !this.list.contains(l)) {
                this.list.add(l);
            }
        }

        @Override // android.view.View.OnFocusChangeListener
        public void onFocusChange(View v, boolean hasFocus) {
            for (View.OnFocusChangeListener l : this.list) {
                l.onFocusChange(v, hasFocus);
            }
        }
    }

    public static void setOnFocusChangeListener(View v, View.OnFocusChangeListener l) {
        FocusChangeListener p2 = (FocusChangeListener) v.getTag(R.layout.memory_range);
        if (p2 == null) {
            p2 = new FocusChangeListener(null);
            v.setOnFocusChangeListener(p2);
            v.setTag(R.layout.memory_range, p2);
        }
        p2.add(l);
    }

    public static String formatFileSize(Context context, long number) {
        try {
            return Formatter.formatFileSize(context, number);
        } catch (Throwable e) {
            Log.badImplementation(e);
            try {
                return formatFileSize(number, false);
            } catch (Throwable e2) {
                Log.badImplementation(e2);
                return Long.toString(number);
            }
        }
    }

    private static String formatFileSize(long number, boolean shorter) {
        String value;
        float result = number;
        String suffix = "B";
        if (result > 900.0f) {
            suffix = "KB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "MB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "GB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "TB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "PB";
            result /= 1024.0f;
        }
        if (result < 1.0f) {
            value = stringFormat("%.2f", Float.valueOf(result));
        } else if (result < 10.0f) {
            if (shorter) {
                value = stringFormat("%.1f", Float.valueOf(result));
            } else {
                value = stringFormat("%.2f", Float.valueOf(result));
            }
        } else if (result >= 100.0f || shorter) {
            value = stringFormat("%.0f", Float.valueOf(result));
        } else {
            value = stringFormat("%.2f", Float.valueOf(result));
        }
        return String.valueOf(value) + ' ' + suffix;
    }

    public static boolean[] booleanArray(List<Boolean> list) {
        boolean[] ret = new boolean[list.size()];
        int i = 0;
        for (Boolean item : list) {
            ret[i] = item.booleanValue();
            i++;
        }
        return ret;
    }

    public static long[] longArray(List<Long> list) {
        long[] ret = new long[list.size()];
        int i = 0;
        for (Long item : list) {
            ret[i] = item.longValue();
            i++;
        }
        return ret;
    }

    public static int[] intArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        int i = 0;
        for (Integer item : list) {
            ret[i] = item.intValue();
            i++;
        }
        return ret;
    }

    public static Process exec(String[] cmdarray) throws IOException {
        Process ret = exec(cmdarray, null, null);
        return ret;
    }

    public static Process exec(String[] cmdarray, String[] envp) throws IOException {
        Process ret = exec(cmdarray, envp, null);
        return ret;
    }

    public static Process exec(String[] cmdarray, String[] envp, File dir) throws IOException {
        if (!overrideExec) {
            try {
                return Runtime.getRuntime().exec(cmdarray, envp, dir);
            } catch (Throwable e) {
                String msg = e.getMessage();
                if (msg.contains("nknown error") || msg.contains("xec failed") || msg.contains("ead failed")) {
                    Log.w("Failed exec: " + Arrays.toString(cmdarray) + ListManager.TEXT_SEPARATOR + envp + ListManager.TEXT_SEPARATOR + dir, e);
                } else {
                    throw e;
                }
            }
        }
        Process processExec = ProcessBuilder.exec(cmdarray, envp, dir);
        overrideExec = true;
        return processExec;
    }

    public static String getNativePath(String path) {
        if (overrideNative == 0 || overrideNative == 1) {
            try {
                File file = new File(path);
                String name = String.valueOf('/') + file.getName();
                FileInputStream is = new FileInputStream(file);
                FileChannel fileChannel = is.getChannel();
                MappedByteBuffer buf = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, 4096L);
                BufferedReader br = new BufferedReader(new FileReader("/proc/self/maps"));
                while (true) {
                    String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    int pos = line.indexOf(name);
                    if (pos >= 0) {
                        int start = pos;
                        while (start > 0 && line.charAt(start) > ' ') {
                            start--;
                        }
                        if (start < pos) {
                            path = line.substring(start, line.length()).trim();
                            if (overrideNative == 0) {
                                overrideNative = (byte) 1;
                            }
                        }
                    }
                }
                br.close();
                if (overrideNative == 0) {
                    overrideNative = (byte) 2;
                }
                buf.capacity();
                is.close();
            } catch (IOException e) {
                Log.w("Failed getNativePath: " + path, e);
            }
        }
        return path;
    }

    public static String getRealPath(String str) {
        if (fakeToReal != null) {
            return str.replace(fakeToReal[0], fakeToReal[1]);
        }
        return str;
    }

    public static class ScrollPos {
        public int offset;
        public int top;

        public ScrollPos(int top, int offset) {
            this.top = top;
            this.offset = offset;
        }
    }

    public static ScrollPos getScrollPos(ListView list) {
        int index = list.getFirstVisiblePosition();
        View v = null;
        try {
            v = list.getChildAt(0);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        int top = v != null ? v.getTop() - list.getPaddingTop() : 0;
        return new ScrollPos(index, top);
    }

    public static void setScrollPos(ListView list, ScrollPos scrollPos) {
        if (scrollPos != null) {
            setSelectionFromTop(list, scrollPos.top, scrollPos.offset);
        }
    }

    public static void setSelectionFromTop(final ListView list, final int pos, final int offset) {
        list.setSelectionFromTop(pos, offset);
        list.post(new Runnable() { // from class: android.ext.Tools.23
            @Override // java.lang.Runnable
            public void run() {
                list.setSelectionFromTop(pos, offset);
            }
        });
    }

    public static boolean isPath(String path) {
        if (path.length() >= 2 && path.charAt(0) == '/') {
            return true;
        }
        Alert.show(Alert.create().setCustomTitle(getCustomTitle(R.string.error)).setMessage(String.valueOf(path) + "\n\n" + Re.s(R.string.path_must_be)).setPositiveButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
        return false;
    }

    public static boolean isFile(Object filename) {
        String msg;
        File file = filename instanceof File ? (File) filename : new File(filename.toString());
        int err = 0;
        if (!file.exists()) {
            err = R.string.file_not_found;
        } else if (!file.isFile()) {
            err = R.string.not_file;
        }
        if (err != 0) {
            msg = String.valueOf(stripColon(err)) + ": " + filename;
        } else {
            try (FileInputStream is = new FileInputStream(file)) {
                is.read();
                return true;
            } catch (IOException e) {
                msg = filename + "\n\n" + e.getMessage();
                Alert.show(Alert.create().setCustomTitle(getCustomTitle(R.string.error)).setMessage(msg).setPositiveButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                return false;
            }
        }
        Alert.show(Alert.create().setCustomTitle(getCustomTitle(R.string.error)).setMessage(msg).setPositiveButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
        return false;
    }

    public static int indexOf(byte[] data, byte[] pattern) {
        if (data.length == 0) {
            return -1;
        }
        int[] failure = computeFailure(pattern);
        int j = 0;
        for (int i = 0; i < data.length; i++) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) {
                j++;
            }
            if (j == pattern.length) {
                return (i - pattern.length) + 1;
            }
        }
        return -1;
    }

    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];
        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }
        return failure;
    }

    public static CharSequence concat(CharSequence... text) {
        try {
            return TextUtils.concat(text);
        } catch (Throwable e) {
            Log.badImplementation(e);
            StringBuilder sb = new StringBuilder();
            for (CharSequence part : text) {
                sb.append(part);
            }
            return sb.toString();
        }
    }

    public static void updateOrientation(Configuration newConfig) {
        int isLandscape2 = 0;
        if (newConfig == null) {
            try {
                newConfig = getContext().getResources().getConfiguration();
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
        isLandscape2 = newConfig.orientation == 2 ? 1 : 0;
        isLandscape = isLandscape2;
    }

    public static int isLandscape() {
        int isLandscape2 = isLandscape;
        if (isLandscape2 == -1) {
            updateOrientation(null);
            return isLandscape;
        }
        return isLandscape2;
    }
}
