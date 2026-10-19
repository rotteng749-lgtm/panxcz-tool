package android.ext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.fix.LayoutInflater;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/* JADX INFO: loaded from: classes.dex */
public class History {
    private static final String DEFAULT_NOTE = "...";
    public static final int NUMBER = 1;
    public static final int PATH = 4;
    public static final int SETTING = 8;
    public static final int SPEED = 16;
    public static final int TEXT = 2;
    static final LinkedList<Item> list = new LinkedList<>();
    static int sticked = 0;

    private static final class Item {
        int hash = 0;
        String note;
        final int type;
        final String value;

        public Item(String value, int type, String note) {
            this.value = value;
            this.type = type;
            this.note = note;
        }

        public String toString() {
            return this.value;
        }

        public int hashCode() {
            int h = this.hash;
            if (h == 0) {
                h = this.value.hashCode();
                String end = "#$@$#" + this.type;
                int count = end.length();
                for (int i = 0; i < count; i++) {
                    h = (h * 31) + end.charAt(i);
                }
                this.hash = h;
            }
            return h;
        }

        public boolean equals(Object obj) {
            return (obj instanceof Item) && this.type == ((Item) obj).type && this.value.equals(((Item) obj).value);
        }
    }

    public static void init() {
        load();
    }

    private static int roundUpToPowerOfTwo(int x) {
        int x2 = x | (x >> 1);
        int x3 = x2 | (x2 >> 2);
        int x4 = x3 | (x3 >> 4);
        int x5 = x4 | (x4 >> 8);
        return (x5 | (x5 >> 16)) + 1;
    }

    private static void load() {
        SharedPreferences sp = Tools.getSharedPreferences();
        if (sp != null) {
            LinkedList<Item> list2 = list;
            int size = sp.getInt("history-size", 0);
            int max = size > Config.historyLimit ? Config.historyLimit : size;
            boolean[] used = new boolean[roundUpToPowerOfTwo(max)];
            int usedMask = used.length - 1;
            for (int i = 0; i < size; i++) {
                String item = sp.getString("history-" + i, null);
                if (item != null && item.length() != 0) {
                    int type = sp.getInt("h-" + i, 1);
                    if (type == 0) {
                        type = 1;
                    }
                    Item add = new Item(item, type, sp.getString("hi-" + i, DEFAULT_NOTE));
                    int pos = add.hashCode() & usedMask;
                    if (used[pos]) {
                        if (list2.indexOf(add) != -1) {
                            continue;
                        }
                    } else {
                        used[pos] = true;
                    }
                    list2.addLast(add);
                    if (list2.size() >= max) {
                        break;
                    }
                }
            }
            sticked = sp.getInt("history-sticked", 0);
            fixSticked();
            truncate();
        }
    }

    static void save() {
        SharedPreferences sp = Tools.getSharedPreferences();
        if (sp != null) {
            SPEditor edit = new SPEditor(sp.edit());
            int i = 0;
            for (Item item : list) {
                edit.putString("history-" + i, item.value);
                edit.putInt("h-" + i, item.type, 1);
                edit.putString("hi-" + i, item.note, DEFAULT_NOTE);
                i++;
            }
            edit.putInt("history-size", i, 0);
            edit.putInt("history-sticked", sticked, 0);
            edit.commit();
        }
    }

    public static void add(String item, int type) {
        add(item, (byte) 0, type);
    }

    static void fixSticked() {
        int val = sticked;
        if (val < 0) {
            sticked = 0;
            return;
        }
        int max = list.size();
        if (val > max) {
            sticked = max;
        }
    }

    public static int getSticked() {
        fixSticked();
        return sticked;
    }

    public static void add(String item, byte sticky, int type) {
        if (item != null && item.length() != 0 && !"0".equals(item) && !"-1".equals(item) && !"1".equals(item)) {
            Item tmp = new Item(item, type, DEFAULT_NOTE);
            int i = 0;
            ListIterator<Item> it = list.listIterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Item old = it.next();
                if (old.equals(tmp)) {
                    tmp.note = old.note;
                    it.remove();
                    if (i < sticked) {
                        sticked--;
                        if (sticky == 0) {
                            sticky = 1;
                        }
                    }
                } else {
                    i++;
                }
            }
            if (sticky == 0) {
                sticky = 2;
            }
            if (sticky == 1) {
                list.addFirst(tmp);
                sticked++;
            } else {
                fixSticked();
                list.add(sticked, tmp);
            }
            truncate();
            save();
        }
    }

    static class HistoryAdapter extends BaseAdapter implements View.OnClickListener, View.OnLongClickListener, View.OnFocusChangeListener {
        static boolean editMode = false;
        final Context context;
        final ArrayList<HItem> items;
        AlertDialog dialog = null;
        private boolean softState = false;

        static class ViewHolder {
            public ImageView delete;
            HItem hItem;
            public TextView note;
            public EditText noteEdit;
            public ImageView pin;
            int position;
            public TextView value;
            public View view;

            ViewHolder() {
            }
        }

        static class HItem {
            Item item;
            boolean pin;

            public HItem(Item item, boolean pin) {
                this.item = item;
                this.pin = pin;
            }

            public String toString() {
                return this.item.toString();
            }
        }

        public HistoryAdapter(Context context, int type) {
            this.context = context;
            History.fixSticked();
            ArrayList<HItem> items = new ArrayList<>();
            this.items = items;
            int i = 0;
            int sticked = History.sticked;
            for (Item item : History.list) {
                if (item != null && (item.type & type) != 0) {
                    items.add(new HItem(item, i < sticked));
                }
                i++;
            }
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null || !(view.getTag() instanceof ViewHolder)) {
                view = LayoutInflater.from(this.context).inflate(R.layout.history_item, parent, false);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.value = (TextView) view.findViewById(R.id.value);
                viewHolder.note = (TextView) view.findViewById(R.id.note);
                viewHolder.noteEdit = (EditText) view.findViewById(R.id.note_edit);
                viewHolder.noteEdit.addTextChangedListener(new TextWatcher() { // from class: android.ext.History.HistoryAdapter.1
                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                        HItem hitem = viewHolder.hItem;
                        if (hitem != null) {
                            HistoryAdapter.this.setNote(hitem.item, viewHolder.noteEdit.getText().toString(), false);
                        }
                    }
                });
                Tools.setOnFocusChangeListener(viewHolder.noteEdit, this);
                viewHolder.pin = Config.setIconSize((ImageView) view.findViewById(R.id.pin));
                viewHolder.pin.setOnClickListener(this);
                viewHolder.pin.setOnLongClickListener(this);
                viewHolder.pin.setTag(viewHolder);
                viewHolder.delete = Config.setIconSize((ImageView) view.findViewById(R.id.delete));
                viewHolder.delete.setOnClickListener(this);
                viewHolder.delete.setOnLongClickListener(this);
                viewHolder.delete.setTag(viewHolder);
                viewHolder.view = view;
                view.setTag(viewHolder);
                view.setOnLongClickListener(this);
                view.setOnClickListener(this);
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            HItem hitem = this.items.get(position);
            holder.hItem = hitem;
            holder.value.setText(hitem.item.value);
            String note = hitem.item.note;
            holder.note.setText(note);
            holder.noteEdit.setText(getEditNote(note));
            holder.position = position;
            holder.note.setVisibility(!editMode ? 0 : 8);
            int edit = editMode ? 0 : 8;
            holder.noteEdit.setVisibility(edit);
            holder.delete.setVisibility(edit);
            ((ViewGroup) view).setDescendantFocusability(editMode ? 131072 : 393216);
            updateStyle(holder);
            return view;
        }

        private String getEditNote(String note) {
            String note2 = note.trim();
            return History.DEFAULT_NOTE.equals(note2) ? "" : note2;
        }

        private void updateStyle(ViewHolder holder) {
            if (holder.hItem.pin) {
                updateStyle(holder, R.drawable.ic_pin_off_white_24dp, SystemConstants.SELECTED_ITEM_COLOR);
            } else {
                updateStyle(holder, R.drawable.ic_pin_white_24dp, 0);
            }
        }

        private void updateStyle(ViewHolder holder, int resId, int color) {
            holder.pin.setImageResource(resId);
            holder.view.setBackgroundColor(color);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return this.items.get(position);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.items.size();
        }

        private void setSoftState(View v, boolean state) {
            Window window;
            if (this.softState != state && this.dialog != null && (window = this.dialog.getWindow()) != null) {
                Tools.externalKeyboard(window, v, state);
                this.softState = state;
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            AdapterView.OnItemClickListener onItemClickListener;
            if (view != null) {
                int id = view.getId();
                if (id == R.id.custom_title) {
                    editMode = editMode ? false : true;
                    if (!editMode) {
                        setSoftState(view, false);
                    }
                    notifyDataSetChanged();
                    return;
                }
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                if (viewHolder != null) {
                    int i = viewHolder.position;
                    switch (id) {
                        case R.id.pin /* 2131427354 */:
                            boolean z = !viewHolder.hItem.pin;
                            viewHolder.hItem.pin = z;
                            updateStyle(viewHolder);
                            History.add(viewHolder.hItem.item.value, z ? (byte) 1 : (byte) 2, viewHolder.hItem.item.type);
                            break;
                        case R.id.delete /* 2131427358 */:
                            Item item = viewHolder.hItem.item;
                            int i2 = 0;
                            ListIterator<Item> listIterator = History.list.listIterator();
                            while (listIterator.hasNext()) {
                                if (listIterator.next().equals(item)) {
                                    listIterator.remove();
                                    if (i2 < History.sticked) {
                                        History.sticked--;
                                    }
                                    this.items.remove(viewHolder.hItem);
                                    notifyDataSetChanged();
                                    History.save();
                                } else {
                                    i2++;
                                }
                                break;
                            }
                            break;
                        default:
                            ListView listView = Tools.getListView(view);
                            if (listView != null && (onItemClickListener = listView.getOnItemClickListener()) != null) {
                                onItemClickListener.onItemClick(listView, view, i, getItemId(i));
                                break;
                            }
                            break;
                    }
                }
            }
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View view) {
            if (editMode || view == null) {
                return false;
            }
            ViewHolder holder = null;
            while (true) {
                if (view != null) {
                    Object tag = view.getTag();
                    if (tag instanceof ViewHolder) {
                        holder = (ViewHolder) tag;
                        break;
                    }
                    Object parent = view.getParent();
                    if (!(parent instanceof View)) {
                        break;
                    }
                    view = (View) parent;
                } else {
                    break;
                }
            }
            if (holder == null) {
                return false;
            }
            editNote(holder.hItem.item, view.getContext());
            return true;
        }

        void setNote(Item item, String note, boolean notify) {
            Iterator<Item> it = History.list.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Item it2 = it.next();
                if (item.equals(it2)) {
                    item = it2;
                    break;
                }
            }
            String text = note.trim();
            if (text.length() == 0) {
                text = History.DEFAULT_NOTE;
            }
            if (!text.equals(item.note)) {
                item.note = text;
                if (item != item) {
                    item.note = text;
                }
                if (notify) {
                    notifyDataSetChanged();
                }
                History.save();
            }
        }

        private void editNote(final Item item, Context context) {
            final EditText edit = new EditText(context);
            edit.setText(getEditNote(item.note));
            AlertDialog dialog = Alert.create(context).setCustomTitle(Tools.getCustomTitle(item.value)).setView(edit).setPositiveButton(Re.s(R.string.ok), new DialogInterface.OnClickListener() { // from class: android.ext.History.HistoryAdapter.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog2, int which) {
                    HistoryAdapter.this.setNote(item, edit.getText().toString(), true);
                }
            }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).create();
            Alert.setOnShowListener(dialog, new DialogInterface.OnShowListener() { // from class: android.ext.History.HistoryAdapter.3
                @Override // android.content.DialogInterface.OnShowListener
                public void onShow(DialogInterface d) {
                    Tools.selectAll(edit);
                    edit.requestFocus();
                }
            });
            Alert.show(dialog, edit);
        }

        @Override // android.view.View.OnFocusChangeListener
        public void onFocusChange(View v, boolean hasFocus) {
            if (!((!hasFocus) & editMode)) {
                setSoftState(v, editMode & hasFocus);
            }
        }
    }

    private static void truncate() {
        LinkedList<Item> list2 = list;
        int limit = Config.historyLimit;
        if (list2.size() > limit) {
            for (int i = list2.size() - limit; i > 0; i--) {
                list2.removeLast();
            }
            fixSticked();
        }
    }

    public static void clear() {
        list.clear();
        fixSticked();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void show(final android.widget.EditText editText) {
        final HistoryAdapter adapter = new HistoryAdapter(Tools.getDarkContext(), editText instanceof EditTextExt ? ((EditTextExt) editText).getDataType() : -1);
        if (adapter.getCount() == 0) {
            Tools.showToast(R.string.history_empty);
            return;
        }
        HistoryAdapter.editMode = false;
        View header = Tools.getCustomTitle(Re.s(R.string.history), Re.s(R.string.click_edit));
        header.setOnClickListener(adapter);
        adapter.dialog = Alert.create(Tools.getDarkContext()).setCustomTitle(header).setAdapter(adapter, new DialogInterface.OnClickListener() { // from class: android.ext.History.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                String str = adapter.getItem(which).toString();
                int start = Math.max(editText.getSelectionStart(), 0);
                int end = Math.max(editText.getSelectionEnd(), 0);
                editText.getText().replace(Math.min(start, end), Math.max(start, end), str, 0, str.length());
                editText.setSelection(Math.max(editText.getSelectionStart(), editText.getSelectionEnd()));
            }
        }).create();
        Alert.show(adapter.dialog);
    }
}
