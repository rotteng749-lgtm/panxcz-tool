package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.ext.ProcessList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import catch_.me_.if_.you_.can_.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class MemoryHistory implements DialogInterface.OnClickListener {
    private static final int MAX_SIZE = 100;
    private int position = 0;
    private final List<HistoryItem> list = new ArrayList();

    public boolean add(long address, CharSequence description) {
        return add(address, description, 0);
    }

    public boolean add(long address, CharSequence description, int top) {
        int size = this.list.size();
        if (this.position < size) {
            for (int i = size - 1; i >= this.position; i--) {
                this.list.remove(i);
            }
            size = this.position;
        }
        if (size >= 100) {
            this.position--;
            this.list.remove(0);
        }
        this.position++;
        ProcessList.ProcessInfo pi = MainService.instance.processInfo;
        String packageName = pi != null ? pi.packageName : null;
        return this.list.add(new HistoryItem(address, description, packageName, top));
    }

    public long getCurrent() {
        int size = this.list.size();
        if (this.position <= 0 || this.position > size) {
            return 0L;
        }
        long ret = this.list.get(this.position - 1).address;
        return ret;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        HistoryItem item = this.list.get(which);
        if (item != null) {
            this.position = which + 1;
            MainService.instance.goToAddress(item.address, item.top);
        }
        if (dialog != null) {
            Tools.dismiss(dialog);
        }
    }

    public void choice() {
        int size = this.list.size();
        if (size == 0) {
            Tools.showToast(R.string.history_empty);
            return;
        }
        CharSequence[] items = new CharSequence[size];
        Drawable[] icons = new Drawable[size];
        Map<String, Drawable> cache = new HashMap<>();
        for (int i = 0; i < size; i++) {
            HistoryItem item = this.list.get(i);
            items[i] = item.toCharSequence();
            if (item.packageName != null) {
                Drawable cached = cache.get(item.packageName);
                if (cached == null) {
                    try {
                        cached = Tools.tryCloneIcon(Tools.getResized(Tools.getApplicationIcon(Tools.getApplicationInfo(item.packageName)), Tools.dp2px48()));
                    } catch (Throwable e) {
                        Log.e("Failed load icon for " + item.packageName, e);
                    }
                    cache.put(item.packageName, cached);
                }
                icons[i] = cached;
            }
        }
        AlertDialog dialog = Alert.create(Tools.getDarkContext()).setCustomTitle(Tools.getCustomTitle(R.string.goto_address)).setSingleChoiceItems(items, this.position - 1, this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).create();
        ListView listView = dialog.getListView();
        if (listView != null) {
            Tools.addIconsToListViewItems(listView, items, icons, 48, R.style.SmallText);
        }
        Alert.show(dialog);
    }

    public void back() {
        int size = this.list.size();
        if (size == 0) {
            Tools.showToast(R.string.history_empty);
        } else if (this.position < 2) {
            Tools.showToast(R.string.no_previous, 0);
        } else {
            onClick(null, this.position - 2);
        }
    }

    public void forward() {
        int size = this.list.size();
        if (size == 0) {
            Tools.showToast(R.string.history_empty);
        } else if (this.position >= size) {
            Tools.showToast(R.string.no_next, 0);
        } else {
            onClick(null, this.position);
        }
    }

    public static class HistoryItem {
        long address;
        private CharSequence description;
        String packageName;
        int top;

        public HistoryItem(long address, CharSequence description, String packageName, int top) {
            this.address = address;
            this.description = description;
            this.packageName = packageName;
            this.top = top;
        }

        public CharSequence toCharSequence() {
            return Tools.concat(Tools.colorize(AddressItem.getStringAddress(this.address, 0), Tools.getColor(R.color.address)), Tools.colorize(": " + ((Object) this.description), -1));
        }
    }

    public static class MemoryBack extends MenuItem implements View.OnLongClickListener {
        private final MemoryHistory history;

        public MemoryBack(MemoryHistory history) {
            super(R.string.back, R.drawable.ic_history_back_24dp);
            this.history = history;
        }

        @Override // android.ext.MenuItem, android.view.View.OnClickListener
        public void onClick(View v) {
            this.history.back();
        }

        @Override // android.ext.MenuItem
        public ImageView getButton(boolean fromUI) {
            ImageView ret = super.getButton(fromUI);
            ret.setOnLongClickListener(this);
            return ret;
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View v) {
            this.history.choice();
            return true;
        }
    }

    public static class MemoryForward extends MenuItem implements View.OnLongClickListener {
        private final MemoryHistory history;

        public MemoryForward(MemoryHistory history) {
            super(R.string.forward, R.drawable.ic_history_forward_24dp);
            this.history = history;
        }

        @Override // android.ext.MenuItem, android.view.View.OnClickListener
        public void onClick(View v) {
            this.history.forward();
        }

        @Override // android.ext.MenuItem
        public ImageView getButton(boolean fromUI) {
            ImageView ret = super.getButton(fromUI);
            ret.setOnLongClickListener(this);
            return ret;
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View v) {
            this.history.choice();
            return true;
        }
    }
}
