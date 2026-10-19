package android.ext;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.ext.PathSelector;
import android.fix.LayoutInflater;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class EditTextPath extends AutoCompleteTextView implements EditTextExt {
    private static final int imeForce = 1912602624;
    private int dataType;
    private int pathType;

    public int getPathType() {
        return this.pathType;
    }

    public void setPathType(int pathType) {
        this.pathType = pathType;
    }

    @Override // android.ext.EditTextExt
    public int getDataType() {
        return this.dataType;
    }

    @Override // android.ext.EditTextExt
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public EditTextPath(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, defStyleRes, popupTheme);
        this.pathType = 0;
        this.dataType = -1;
        setAdapter(new PathAdapter());
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
        if (!isInEditMode()) {
            EditText.checkContrast(this);
        }
        setInputType(getInputType() | 262144);
        setHorizontallyScrolling(false);
        setMaxLines(Integer.MAX_VALUE);
    }

    public EditTextPath(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.pathType = 0;
        this.dataType = -1;
        setAdapter(new PathAdapter());
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
        if (!isInEditMode()) {
            EditText.checkContrast(this);
        }
        setInputType(getInputType() | 262144);
        setHorizontallyScrolling(false);
        setMaxLines(Integer.MAX_VALUE);
    }

    public EditTextPath(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.pathType = 0;
        this.dataType = -1;
        setAdapter(new PathAdapter());
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
        if (!isInEditMode()) {
            EditText.checkContrast(this);
        }
        setInputType(getInputType() | 262144);
        setHorizontallyScrolling(false);
        setMaxLines(Integer.MAX_VALUE);
    }

    public EditTextPath(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.pathType = 0;
        this.dataType = -1;
        setAdapter(new PathAdapter());
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
        if (!isInEditMode()) {
            EditText.checkContrast(this);
        }
        setInputType(getInputType() | 262144);
        setHorizontallyScrolling(false);
        setMaxLines(Integer.MAX_VALUE);
    }

    public EditTextPath(Context context) {
        super(context);
        this.pathType = 0;
        this.dataType = -1;
        setAdapter(new PathAdapter());
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
        if (!isInEditMode()) {
            EditText.checkContrast(this);
        }
        setInputType(getInputType() | 262144);
        setHorizontallyScrolling(false);
        setMaxLines(Integer.MAX_VALUE);
    }

    class PathAdapter extends BaseAdapter implements Filterable {
        private File[] items = null;
        final Filter filter = new PathFilter();

        public PathAdapter() {
        }

        @Override // android.widget.Adapter
        public int getCount() {
            File[] items = this.items;
            if (items == null) {
                return 0;
            }
            return items.length;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            File[] items = this.items;
            if (items == null || position < 0 || position >= items.length) {
                return null;
            }
            return items[position];
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.inflateStatic(R.layout.path_suggestion, parent, false);
            }
            Object obj = getItem(position);
            if (obj instanceof File) {
                TextView text = (TextView) convertView.findViewById(R.id.text1);
                text.setText(".../" + ((File) obj).getName());
            }
            return convertView;
        }

        class PathFilter extends Filter {
            PathFilter() {
            }

            @Override // android.widget.Filter
            protected Filter.FilterResults performFiltering(CharSequence constraint) {
                String[] list;
                Filter.FilterResults results = new Filter.FilterResults();
                if (constraint != null) {
                    String str = getStr(constraint);
                    File file = new File(str);
                    boolean empty = str.endsWith("/");
                    File parent = empty ? file : file.getParentFile();
                    String start = (empty ? "" : file.getName()).toLowerCase(Locale.US);
                    if (parent.exists() && (list = PathSelector.list(parent)) != null && list.length > 0) {
                        ArrayList<PathSelector.SortItem> items = PathSelector.sort(EditTextPath.this.pathType, parent, list, start, true);
                        int cnt = items.size();
                        if (cnt == 1 && items.get(0).name.equals(start)) {
                            cnt = 0;
                        }
                        if (cnt > 0) {
                            File[] files = new File[cnt];
                            for (int i = 0; i < cnt; i++) {
                                files[i] = items.get(i).file;
                            }
                            results.values = files;
                            results.count = cnt;
                        }
                    }
                }
                return results;
            }

            private String getStr(CharSequence constraint) {
                return (constraint == null || constraint.length() == 0) ? "/" : constraint.toString();
            }

            @Override // android.widget.Filter
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                PathAdapter.this.items = (results == null || !(results.values instanceof File[])) ? null : (File[]) results.values;
                PathAdapter.this.notifyDataSetChanged();
            }
        }

        @Override // android.widget.Filterable
        public Filter getFilter() {
            return this.filter;
        }
    }

    @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        boolean show = isPopupShowing();
        if (keyCode == 66 && show) {
            return true;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override // android.widget.TextView
    public void setImeOptions(int imeOptions) {
        try {
            super.setImeOptions(imeForce | imeOptions);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        try {
            int imeOptions = super.getImeOptions();
            if ((imeOptions & imeForce) != imeForce) {
                super.setImeOptions(imeOptions | imeForce);
            }
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
        try {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        } catch (Throwable e2) {
            Log.badImplementation(e2);
        }
    }

    @Override // android.view.View
    public void dispatchWindowFocusChanged(boolean hasFocus) {
        try {
            super.dispatchWindowFocusChanged(hasFocus);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.EditText, android.widget.TextView
    public void setText(CharSequence text, TextView.BufferType type) {
        try {
            super.setText(text, type);
        } catch (NullPointerException | SecurityException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void drawableStateChanged() {
        try {
            super.drawableStateChanged();
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return super.onTouchEvent(event);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.widget.TextView, android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        try {
            super.invalidateDrawable(drawable);
        } catch (NoSuchFieldError e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.TextView
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        try {
            super.setCompoundDrawables(left, top, right, bottom);
        } catch (StringIndexOutOfBoundsException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.view.View
    public void refreshDrawableState() {
        try {
            super.refreshDrawableState();
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.view.View
    @TargetApi(14)
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        try {
            super.onInitializeAccessibilityNodeInfo(info);
        } catch (NullPointerException e) {
            Log.badImplementation(e);
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

    @Override // android.widget.TextView, android.view.View
    public boolean performLongClick() {
        try {
            return super.performLongClick();
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.badImplementation(e);
        } catch (NullPointerException e2) {
            Log.badImplementation(e2);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onDragEvent(DragEvent event) {
        try {
            return super.onDragEvent(event);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Throwable e) {
            Log.badImplementation(e);
            ExceptionHandler.sendException(Thread.currentThread(), e, false);
        }
    }

    @Override // android.widget.EditText, android.widget.TextView, android.view.View
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        try {
            return super.onKeyShortcut(keyCode, event);
        } catch (NullPointerException e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.widget.EditText, android.widget.TextView
    public boolean onTextContextMenuItem(int id) {
        try {
            return super.onTextContextMenuItem(id);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        try {
            super.onWindowFocusChanged(hasWindowFocus);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.widget.TextView
    public void setHorizontallyScrolling(boolean whether) {
        super.setHorizontallyScrolling(false);
    }

    @Override // android.widget.TextView
    public void setMaxLines(int maxlines) {
        super.setMaxLines(Integer.MAX_VALUE);
    }

    public boolean getSecClipboardEnabled() {
        return false;
    }
}
