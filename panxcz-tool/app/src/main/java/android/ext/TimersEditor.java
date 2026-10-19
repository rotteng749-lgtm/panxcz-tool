package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.ext.ProcessList;
import android.fix.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class TimersEditor implements DialogInterface.OnClickListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int CHECKED = 0;
    private static final int CNT = 3;
    private static final int COLS = 4;
    private static final int CURRENT = 2;
    private static final int GREEN = -16755456;
    private static final int RED = -11206656;
    private static final int USED = 1;
    private static final int[] ids = {R.id.app, R.id.java, R.id.system, R.id.other};
    private final int ROWS;
    private final boolean[] defaults;
    private final String logName;
    private final boolean[] rows;
    private final String spPrefix;
    private final boolean[] state;
    private final int type;
    private WeakReference<BaseAdapter> weakAdapter = new WeakReference<>(null);
    private final boolean[] cols = new boolean[4];

    public TimersEditor(String logName, String spPrefix, int ROWS, boolean[] defaults, int type) {
        this.logName = logName;
        this.spPrefix = spPrefix;
        this.ROWS = ROWS;
        this.defaults = defaults;
        this.type = type;
        this.rows = new boolean[ROWS];
        this.state = new boolean[ROWS * 4 * 3];
        Log.d(String.valueOf(logName) + ':');
        getData();
    }

    View getHeader(View convertView, ViewGroup parent) {
        if (convertView == null || convertView.getId() != R.layout.timer_header) {
            View header = LayoutInflater.inflateStatic(R.layout.timer_header, parent, false);
            header.setId(R.layout.timer_header);
            for (int j = 0; j < 4; j++) {
                TextView v = (TextView) header.findViewById(ids[j]);
                v.setTag(Integer.valueOf(j));
                v.setOnClickListener(this);
                v.setPaintFlags(v.getPaintFlags() | 8);
                v.setFocusable(true);
            }
            return header;
        }
        return convertView;
    }

    View getRow(int position, View convertView, ViewGroup parent) {
        View row;
        LayoutInflater inflater = LayoutInflater.getInflater();
        int i = position - 1;
        if (convertView == null || convertView.getId() != R.layout.timer_row) {
            row = inflater.inflate(R.layout.timer_row, parent, false);
            row.setId(R.layout.timer_row);
            View[] holder = new View[5];
            for (int j = 0; j < 4; j++) {
                CheckBox ch = (CheckBox) row.findViewById(ids[j]);
                ch.setFocusable(true);
                ch.setOnCheckedChangeListener(this);
                holder[j] = ch;
            }
            TextView name = (TextView) row.findViewById(R.id.name);
            name.setPaintFlags(name.getPaintFlags() | 8);
            name.setOnClickListener(this);
            name.setFocusable(true);
            holder[4] = name;
            row.setTag(holder);
        } else {
            row = convertView;
        }
        View[] holder2 = (View[]) row.getTag();
        for (int j2 = 0; j2 < 4; j2++) {
            CheckBox ch2 = (CheckBox) holder2[j2];
            int id = (i * 4) + j2;
            ch2.setTag(null);
            ch2.setChecked(this.state[(id * 3) + 2]);
            ch2.setTag(Integer.valueOf(id));
            ch2.setBackgroundColor(this.state[(id * 3) + 1] ? GREEN : RED);
        }
        TextView name2 = (TextView) holder2[4];
        name2.setTag(Integer.valueOf(i));
        name2.setText(Integer.toString(i + 1));
        return row;
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Object tag;
        if (buttonView != null && (tag = buttonView.getTag()) != null && (tag instanceof Integer)) {
            int id = ((Integer) tag).intValue();
            this.state[(id * 3) + 2] = isChecked;
        }
    }

    private View getView() {
        boolean[] state = this.state;
        for (int i = 0; i < this.ROWS * 4; i++) {
            state[(i * 3) + 2] = state[(i * 3) + 0];
        }
        View view = LayoutInflater.inflateStatic(R.layout.timer_table, (ViewGroup) null);
        ListView listView = (ListView) view;
        new FastScrollerFix(listView);
        listView.setItemsCanFocus(true);
        BaseAdapter adapter = new BaseAdapter() { // from class: android.ext.TimersEditor.1
            @Override // android.widget.Adapter
            public View getView(int position, View convertView, ViewGroup parent) {
                return position == 0 ? TimersEditor.this.getHeader(convertView, parent) : TimersEditor.this.getRow(position, convertView, parent);
            }

            @Override // android.widget.Adapter
            public long getItemId(int position) {
                return position;
            }

            @Override // android.widget.Adapter
            public Object getItem(int position) {
                return Integer.valueOf(position);
            }

            @Override // android.widget.Adapter
            public int getCount() {
                return TimersEditor.this.ROWS + 1;
            }
        };
        listView.setAdapter((ListAdapter) adapter);
        this.weakAdapter = new WeakReference<>(adapter);
        return view;
    }

    private void updateView() {
        BaseAdapter adapter = this.weakAdapter.get();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.name /* 2131427409 */:
                int i = ((Integer) v.getTag()).intValue();
                this.rows[i] = !this.rows[i];
                for (int j = 0; j < 4; j++) {
                    int id = (i * 4) + j;
                    this.state[(id * 3) + 2] = this.rows[i];
                }
                break;
            default:
                int j2 = ((Integer) v.getTag()).intValue();
                this.cols[j2] = this.cols[j2] ? false : true;
                for (int i2 = 0; i2 < this.ROWS; i2++) {
                    int id2 = (i2 * 4) + j2;
                    this.state[(id2 * 3) + 2] = this.cols[j2];
                }
                break;
        }
        updateView();
    }

    public void show() {
        load();
        MainService.instance.mDaemonManager.shUsage(this.type);
        AlertDialog dialog = Alert.create().setView(Tools.getViewForAttach(getView())).setNeutralButton(Re.s(R.string.reset_button), this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).setPositiveButton(Re.s(R.string.save), this).create();
        Alert.show(dialog);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        boolean[] state = this.state;
        if (which == -3) {
            boolean[] def = this.defaults;
            for (int i = 0; i < this.ROWS * 4; i++) {
                state[(i * 3) + 2] = def[i];
            }
        }
        for (int i2 = 0; i2 < this.ROWS * 4; i2++) {
            state[(i2 * 3) + 0] = state[(i2 * 3) + 2];
        }
        save();
        MainService.instance.mDaemonManager.shConfig(this.type);
    }

    private void save() {
        ProcessList.ProcessInfo info = MainService.instance.processInfo;
        if (info != null) {
            boolean[] state = this.state;
            SPEditor edit = new SPEditor();
            String key = String.valueOf(this.spPrefix) + '-' + info.packageName + '-';
            boolean[] def = this.defaults;
            for (int i = 0; i < this.ROWS * 4; i++) {
                edit.putBoolean(String.valueOf(key) + i, state[(i * 3) + 0], def[i]);
            }
            edit.commit();
        }
    }

    public void load() {
        boolean z;
        ProcessList.ProcessInfo info = MainService.instance.processInfo;
        if (info != null) {
            boolean[] state = this.state;
            SharedPreferences storage = Tools.getSharedPreferences();
            String key = String.valueOf(this.spPrefix) + '-' + info.packageName + '-';
            boolean[] def = this.defaults;
            for (int i = 0; i < this.ROWS * 4; i++) {
                try {
                    state[(i * 3) + 0] = storage.getBoolean(String.valueOf(key) + i, def[i]);
                } catch (Throwable th) {
                    int val = storage.getInt(String.valueOf(key) + i, def[i] ? 1 : 0);
                    int i2 = (i * 3) + 0;
                    if (val == 0 || val == 1) {
                        z = val != 0;
                    } else {
                        z = def[i];
                    }
                    state[i2] = z;
                }
            }
            getData();
        }
    }

    public boolean[] getData() {
        boolean[] state = this.state;
        boolean[] data = new boolean[this.ROWS * 4];
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < this.ROWS * 4; i++) {
            if (i % 4 == 0) {
                s.append(' ');
                s.append((i / 4) + 1);
                s.append(':');
            }
            if (state[(i * 3) + 0]) {
                data[i] = true;
                s.append('1');
            } else {
                s.append('0');
            }
        }
        Log.d(String.valueOf(this.logName) + " get:" + s.toString());
        return data;
    }

    public void updateUsage(boolean[] data) {
        boolean[] state = this.state;
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < this.ROWS * 4; i++) {
            if (i % 4 == 0) {
                s.append(' ');
                s.append((i / 4) + 1);
                s.append(':');
            }
            boolean checked = data[i];
            state[(i * 3) + 1] = checked;
            if (checked) {
                s.append('1');
            } else {
                s.append('0');
            }
        }
        Log.d(String.valueOf(this.logName) + " usage:" + s.toString());
        updateView();
    }
}
