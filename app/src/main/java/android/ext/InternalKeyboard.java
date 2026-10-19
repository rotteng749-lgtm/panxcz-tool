package android.ext;

import android.content.Context;
import android.content.res.Resources;
import android.fix.KeyboardView;
import android.fix.LayoutInflater;
import android.inputmethodservice.Keyboard;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import catch_.me_.if_.you_.can_.R;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class InternalKeyboard extends KeyboardView {
    public static final int COLON_TO_QUESTION = 1;
    public static final int PATTERN = 15;
    public static final int R_TO_DOLLAR = 8;
    public static final int SEMICOLON_TO_ASTERISK = 2;
    public static final int TILDE_TO_CARET = 4;
    private boolean allowUseInternal;
    private int fixed;
    private int flags;
    private boolean fn;
    private boolean hex;
    private boolean hide;
    private WeakReference<Window> weakWindow;

    public InternalKeyboard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.allowUseInternal = true;
        this.hide = false;
        this.flags = 0;
        this.weakWindow = new WeakReference<>(null);
        this.fixed = -1;
        init(context);
    }

    public InternalKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.allowUseInternal = true;
        this.hide = false;
        this.flags = 0;
        this.weakWindow = new WeakReference<>(null);
        this.fixed = -1;
        init(context);
    }

    public InternalKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.allowUseInternal = true;
        this.hide = false;
        this.flags = 0;
        this.weakWindow = new WeakReference<>(null);
        this.fixed = -1;
        init(context);
    }

    public void setAllowUseInternal(boolean allowUseInternal) {
        this.allowUseInternal = allowUseInternal;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public int getFlags() {
        return this.flags;
    }

    private Window getWindow() {
        return this.weakWindow.get();
    }

    void processKey(int primaryCode) {
        Window window = getWindow();
        if (window != null) {
            View focus = window.getCurrentFocus();
            if (focus instanceof android.widget.EditText) {
                android.widget.EditText edit = (android.widget.EditText) focus;
                int start = Math.max(edit.getSelectionStart(), 0);
                int end = Math.max(edit.getSelectionEnd(), 0);
                String str = null;
                int key = 0;
                if (primaryCode < 5000) {
                    str = String.valueOf((char) primaryCode);
                } else {
                    switch (primaryCode) {
                        case 5000:
                            key = 67;
                            break;
                        case 5001:
                            key = 112;
                            break;
                        case 5002:
                            str = ParserNumbers.thousandSeparator;
                            break;
                        case 5003:
                            str = String.valueOf(ParserNumbers.decimalSeparator);
                            break;
                        case 5004:
                        case 5005:
                            boolean skip = start == end && ((start == 0 && primaryCode == 5004) || (start == edit.getText().length() && primaryCode == 5005));
                            if (!skip) {
                                key = primaryCode != 5004 ? 22 : 21;
                            }
                            break;
                        case 5006:
                            setHex();
                            break;
                        case 5007:
                            History.show(edit);
                            break;
                        case 5008:
                            Tools.selectAll(edit);
                            break;
                        case 5009:
                            setSmall();
                            break;
                        case 5010:
                            setFn();
                            break;
                    }
                }
                if (str != null) {
                    edit.getText().replace(Math.min(start, end), Math.max(start, end), str, 0, str.length());
                    edit.setSelection(Math.max(edit.getSelectionStart(), edit.getSelectionEnd()));
                }
                if (key != 0) {
                    try {
                        edit.dispatchKeyEvent(new KeyEvent(0, key));
                        edit.dispatchKeyEvent(new KeyEvent(1, key));
                    } catch (Throwable e) {
                        Log.e("Failed send key to app", e);
                    }
                }
            }
        }
    }

    private void setSmall() {
        boolean small = (Config.configClient & 64) == 0;
        Config.get(R.id.config_kbd_small).value = small ? 1 : 0;
        Config.save();
        setKeyboard();
    }

    private void setHex() {
        this.hex = !this.hex;
        setKeyboard();
    }

    private void setFn() {
        this.fn = !this.fn;
        setKeyboard();
    }

    private void init(Context context) {
        this.hex = false;
        this.fn = false;
        if (!isInEditMode()) {
            try {
                setPreviewEnabled(false);
                setOnKeyboardActionListener(new android.inputmethodservice.KeyboardView.OnKeyboardActionListener() { // from class: android.ext.InternalKeyboard.1
                    public void swipeUp() {
                    }

                    public void swipeRight() {
                    }

                    public void swipeLeft() {
                    }

                    public void swipeDown() {
                    }

                    public void onText(CharSequence text) {
                    }

                    public void onRelease(int primaryCode) {
                    }

                    public void onPress(int primaryCode) {
                    }

                    public void onKey(int primaryCode, int[] keyCodes) {
                        InternalKeyboard.this.processKey(primaryCode);
                    }
                });
            } catch (Throwable e) {
                Log.e("Failed init internal keyboard", e);
            }
        }
    }

    private void setKeyboard() {
        boolean useInternalKeyboard = this.allowUseInternal && (Config.configClient & 1) != 0;
        setKeyboard(useInternalKeyboard);
    }

    public void setHideKeyboard(boolean hide) {
        boolean old = this.hide;
        this.hide = hide;
        if (old != hide) {
            setKeyboard();
        }
    }

    private int getKeyboardId(boolean useInternalKeyboard, boolean hex, boolean fn, boolean small) {
        return useInternalKeyboard ? hex ? R.id.k_hex : fn ? R.id.k_fn : small ? R.id.k_small : R.id.k_num : R.id.k_ext;
    }

    private void replaceKey(Keyboard.Key key, String replace) {
        key.label = replace;
        if (key.codes == null) {
            key.codes = new int[1];
        }
        key.codes[0] = replace.charAt(0);
    }

    private void replaceKey(Keyboard.Key key) {
        if ((this.flags & 1) != 0 && ":".equals(key.label)) {
            replaceKey(key, Searcher.MASK_PLACEHOLDER);
        }
        if ((this.flags & 2) != 0 && ";".equals(key.label)) {
            replaceKey(key, "*");
        }
        if ((this.flags & 4) != 0 && "~".equals(key.label)) {
            replaceKey(key, "^");
        }
        if ((this.flags & 8) != 0 && "r".equals(key.label)) {
            replaceKey(key, "$");
        }
    }

    private void setKeyboard(boolean useInternalKeyboard) {
        boolean h = this.hex;
        boolean f = this.fn;
        boolean s = (Config.configClient & 64) != 0;
        int resId = this.hide ? R.xml.empty : R.xml.keyboard;
        Keyboard kbd = new Keyboard(MainService.context, resId, getKeyboardId(useInternalKeyboard, h, f, s));
        if (!this.hide) {
            if (h && kbd.getKeys().size() == 0) {
                h = false;
                kbd = new Keyboard(MainService.context, resId, getKeyboardId(useInternalKeyboard, false, f, s));
            }
            if (f && kbd.getKeys().size() == 0) {
                f = false;
                kbd = new Keyboard(MainService.context, resId, getKeyboardId(useInternalKeyboard, h, false, s));
            }
            if (s && kbd.getKeys().size() == 0) {
                kbd = new Keyboard(MainService.context, resId, getKeyboardId(useInternalKeyboard, h, f, false));
            }
        }
        SparseIntArray coords = new SparseIntArray();
        for (Keyboard.Key key : kbd.getKeys()) {
            if (key != null) {
                coords.put(key.y, coords.get(key.y, 0) + 1);
                if (!f) {
                    replaceKey(key);
                }
                if (key.codes != null && key.codes.length != 0) {
                    if (key.codes[0] == 5002) {
                        key.label = ParserNumbers.thousandSeparator;
                    }
                    if (key.codes[0] == 5003) {
                        key.label = String.valueOf(ParserNumbers.decimalSeparator);
                    }
                }
            }
        }
        Resources r = getContext().getResources();
        ViewGroup.LayoutParams p = getLayoutParams();
        if (p != null && coords.size() > 0) {
            p.width = Math.round(r.getDimension(R.dimen.key_width) * coords.valueAt(0));
            p.height = Math.round(r.getDimension(R.dimen.key_height) * coords.size());
            setLayoutParams(p);
            Log.d("Kbd: " + coords.valueAt(0) + " x " + coords.size() + ListManager.TEXT_SEPARATOR + p.width + SearchMenuItem.XOR_MODE_SMALL + p.height + ListManager.TEXT_SEPARATOR + kbd.getMinWidth() + SearchMenuItem.XOR_MODE_SMALL + kbd.getHeight());
        }
        setKeyboard(kbd);
    }

    public void load(Window window) {
        this.weakWindow = new WeakReference<>(window);
        boolean useInternalKeyboard = this.allowUseInternal && (Config.configClient & 1) != 0;
        if (useInternalKeyboard) {
            window.addFlags(131072);
        } else {
            window.clearFlags(131072);
        }
        setKeyboard();
        View view = window.getDecorView();
        Window.Callback callback = window.getCallback();
        if ((callback instanceof WindowCallbackWrapper) && view.getWidth() == 0) {
            ((WindowCallbackWrapper) callback).setKeyboard(this);
        } else {
            onWindowFocusChanged();
        }
    }

    public static void needExternalKbd(View v, boolean needExternal) {
        InternalKeyboard kbdView = (InternalKeyboard) v.getRootView().findViewById(R.id.keyboard);
        if (kbdView != null) {
            kbdView.needExternal(v, needExternal);
            if (!needExternal) {
                Tools.hideKeyboard(v);
            }
        }
    }

    public void needExternal(View v, boolean hasFocus) {
        if (v != null) {
            try {
                Window window = getWindow();
                if (window != null) {
                    Tools.externalKeyboard(window, v, hasFocus);
                }
                if (hasFocus) {
                    setKeyboard(false);
                } else {
                    setKeyboard();
                }
            } catch (Throwable e) {
                Log.e("Failed show/hide kbd: " + hasFocus, e);
            }
        }
    }

    public void updateOrientation() {
        setKeyboard();
        onWindowFocusChanged();
    }

    public void onWindowFocusChanged() {
        int orientation;
        Window window = getWindow();
        if (window != null && this.fixed != (orientation = MainService.context.getResources().getConfiguration().orientation)) {
            this.fixed = orientation;
            fixWidth(window, orientation != 1);
        }
    }

    private void fixWidth(Window window, boolean horizontal) {
        WindowManager.LayoutParams lp = window.getAttributes();
        View view = window.getDecorView();
        int viewWidth = view.getWidth();
        try {
            view.measure(-2, -2);
            viewWidth = Math.max(viewWidth, view.getMeasuredWidth());
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
        if (horizontal) {
            int width = getWidth();
            try {
                measure(-2, -2);
                viewWidth += Math.max(width, getMeasuredWidth());
            } catch (Throwable e2) {
                Log.badImplementation(e2);
            }
        } else {
            viewWidth += Tools.dp2px48();
        }
        lp.width = viewWidth;
        if (lp.width >= MainService.context.getResources().getDisplayMetrics().widthPixels) {
            lp.width = -1;
        }
        window.setAttributes(lp);
    }

    public static View getView(View child) {
        return getView(child, true);
    }

    public static class Flags {
        public int value;

        public Flags(int value) {
            this.value = value;
        }
    }

    public static View getView(View child, boolean allowUseInternal) {
        if (!allowUsage()) {
            return child;
        }
        KeyboardLayout parent = (KeyboardLayout) LayoutInflater.inflateStatic(R.layout.keyboard, (ViewGroup) null);
        int id = child instanceof ScrollView ? R.id.kbd_frame : R.id.kbd_scroll;
        ViewGroup frame = (ViewGroup) parent.findViewById(id);
        frame.addView(child);
        frame.setVisibility(0);
        InternalKeyboard kbd = (InternalKeyboard) parent.findViewById(R.id.keyboard);
        kbd.setAllowUseInternal(allowUseInternal);
        Object o = child.getTag();
        if (o instanceof Flags) {
            kbd.setFlags(((Flags) o).value);
        }
        parent.updateOrientation();
        return parent;
    }

    public static boolean allowUsage() {
        try {
            View check = LayoutInflater.inflateStatic(R.layout.keyboard, (ViewGroup) null);
            if (check != null) {
                return true;
            }
            return false;
        } catch (Throwable e) {
            Log.e("Check internal keyboard fail", e);
            return false;
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

    public static abstract class FocusListener implements View.OnFocusChangeListener {
        protected abstract boolean useExternal(View view, boolean z);

        @Override // android.view.View.OnFocusChangeListener
        public void onFocusChange(View v, boolean hasFocus) {
            InternalKeyboard kbdView;
            if ((Config.configClient & 1) != 0 && (kbdView = (InternalKeyboard) v.getRootView().findViewById(R.id.keyboard)) != null) {
                boolean needExternal = useExternal(v, hasFocus);
                if (!needExternal) {
                    hasFocus = false;
                }
                kbdView.needExternal(v, hasFocus);
                if (!needExternal) {
                    Tools.hideKeyboard(v);
                }
            }
        }
    }

    public static void setFlagsFor(View v, final int flags) {
        Tools.setOnFocusChangeListener(v, new View.OnFocusChangeListener() { // from class: android.ext.InternalKeyboard.2
            int oldFlags = -1;

            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v2, boolean hasFocus) {
                InternalKeyboard kbdView;
                if ((Config.configClient & 1) != 0 && (kbdView = (InternalKeyboard) v2.getRootView().findViewById(R.id.keyboard)) != null) {
                    if (this.oldFlags == -1) {
                        this.oldFlags = kbdView.getFlags();
                    }
                    kbdView.setFlags((hasFocus ? flags : 0) | this.oldFlags);
                    kbdView.updateOrientation();
                }
            }
        });
    }
}
