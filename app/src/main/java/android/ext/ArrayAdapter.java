package android.ext;

import android.R;
import android.content.Context;
import android.fix.LayoutInflater;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Collection;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ArrayAdapter<T> extends android.widget.ArrayAdapter<T> {
    private LayoutInflater mInflater;

    public ArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, objects);
        this.mInflater = null;
    }

    public ArrayAdapter(Context context, int resource, int textViewResourceId, T[] tArr) {
        super(context, resource, textViewResourceId, tArr);
        this.mInflater = null;
    }

    public ArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.mInflater = null;
    }

    public ArrayAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        this.mInflater = null;
    }

    public ArrayAdapter(Context context, int resource, T[] tArr) {
        super(context, resource, tArr);
        this.mInflater = null;
    }

    public ArrayAdapter(Context context, int resource) {
        super(context, resource);
        this.mInflater = null;
    }

    public ArrayAdapter(Context context, List<T> objects) {
        super(context, R.layout.select_dialog_item, R.id.text1, objects);
        this.mInflater = null;
    }

    public ArrayAdapter(Context context, T[] tArr) {
        super(context, R.layout.select_dialog_item, R.id.text1, tArr);
        this.mInflater = null;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        Object obj;
        T item = null;
        TextView text = null;
        try {
            view = super.getView(position, convertView, parent);
        } catch (Throwable e) {
            Log.e("Failed get view for ArrayAdapter", e);
            if (convertView == null) {
                if (this.mInflater == null) {
                    this.mInflater = LayoutInflater.getInflater();
                }
                view = this.mInflater.inflate(catch_.me_.if_.you_.can_.R.layout.select_dialog_item, parent, false);
            } else {
                view = convertView;
            }
            text = (TextView) view;
            item = getItem(position);
            if (item instanceof CharSequence) {
                text.setText((CharSequence) item);
            } else {
                text.setText(item.toString());
            }
        }
        if (item == null && (view instanceof TextView)) {
            T item2 = getItem(position);
            text = (TextView) view;
            obj = item2;
        } else {
            obj = item;
        }
        if ((obj instanceof MenuItem) && text != null) {
            MenuItem menuItem = (MenuItem) obj;
            Drawable icon = menuItem.getDrawable();
            if (icon != null) {
                Tools.addIconToTextView(text, icon, Config.getIconSize());
            }
        }
        return view;
    }

    @Override // android.widget.ArrayAdapter
    public void addAll(Collection<? extends T> collection) {
        if (Build.VERSION.SDK_INT < 11) {
            for (T item : collection) {
                add(item);
            }
            return;
        }
        super.addAll(collection);
    }
}
