package android.ext;

import android.content.Context;
import android.fix.LayoutInflater;
import android.fix.LinearLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import catch_.me_.if_.you_.can_.R;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class MemoryRange extends LinearLayout implements View.OnClickListener {
    public static final int MEMORY_FROM = 0;
    public static final int MEMORY_TO = 1;
    public static final int TYPE_ALL = 0;
    public static final int TYPE_NEARBY = 2;
    public static final int TYPE_RANGE = 1;
    private EditText address;
    private CheckBox after;
    private CheckBox before;
    private EditText distance;
    private Runnable listener;
    private EditText memoryFrom;
    private EditText memoryTo;
    private View nearbyRow;
    private Button range;
    private View rangeRow;
    private int type;

    public MemoryRange(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.listener = null;
        this.type = 0;
        this.range = null;
    }

    public MemoryRange(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.listener = null;
        this.type = 0;
        this.range = null;
    }

    public MemoryRange(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.listener = null;
        this.type = 0;
        this.range = null;
    }

    public MemoryRange(Context context) {
        super(context);
        this.listener = null;
        this.type = 0;
        this.range = null;
    }

    public void init() {
        if (this.range == null) {
            LayoutInflater.inflateStatic(R.layout.memory_range, this);
            this.range = (Button) findViewById(R.id.range);
            this.rangeRow = findViewById(R.id.range_row);
            this.memoryFrom = (EditText) findViewById(R.id.memory_from);
            this.memoryFrom.setDataType(1);
            findViewById(R.id.region_from).setTag(this.memoryFrom);
            this.memoryTo = (EditText) findViewById(R.id.memory_to);
            this.memoryTo.setDataType(1);
            findViewById(R.id.region_to).setTag(this.memoryTo);
            this.memoryFrom.setText(AddressItem.getStringAddress(Config.memoryFrom, 4));
            this.memoryTo.setText(AddressItem.getStringAddress(Config.memoryTo, 4));
            this.nearbyRow = findViewById(R.id.nearby_row);
            this.address = (EditText) findViewById(R.id.address);
            this.address.setDataType(1);
            this.before = (CheckBox) findViewById(R.id.before);
            this.after = (CheckBox) findViewById(R.id.after);
            this.distance = (EditText) findViewById(R.id.distance);
            this.distance.setDataType(1);
            this.distance.setText(Long.toHexString(Config.nearbyDistance).toUpperCase(Locale.US));
            this.range.setOnClickListener(this);
            InternalKeyboard.setFlagsFor(this.memoryFrom, 1);
            update();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        setType((this.type + 1) % 3);
    }

    private void update() {
        int i;
        int type = this.type;
        Button button = this.range;
        if (type == 1) {
            i = R.string.only_memory;
        } else {
            i = type == 2 ? R.string.nearby : R.string.all_memory;
        }
        button.setText(Re.s(i));
        this.rangeRow.setVisibility(type == 1 ? 0 : 8);
        this.nearbyRow.setVisibility(type != 2 ? 8 : 0);
    }

    public void setTypeChangeListener(Runnable listener) {
        this.listener = listener;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        int old = this.type;
        this.type = type;
        update();
        Runnable l = this.listener;
        if (old == type || l == null) {
            return;
        }
        l.run();
    }

    public void setAddress(String address) {
        this.address.setText(address);
    }

    public long getMem(int index) throws NumberFormatException {
        if (this.type == 1) {
            long val = getMem(index, this.memoryFrom, "0", this.memoryTo, "-1");
            if (index == 0) {
                Config.memoryFrom = val;
            } else {
                Config.memoryTo = val;
            }
            Config.save();
            return val;
        }
        if (this.type == 2) {
            String data = SearchMenuItem.checkScript(this.address.getText().toString().trim());
            try {
                long address = ParserNumbers.parseBigLong(data, 16);
                History.add(data, 1);
                CheckBox ch = index == 0 ? this.before : this.after;
                if (ch.isChecked()) {
                    String data2 = SearchMenuItem.checkScript(this.distance.getText().toString().trim());
                    try {
                        long distance = ParserNumbers.parseBigLong(data2, 16);
                        History.add(data2, 1);
                        Config.nearbyDistance = distance;
                        Config.save();
                        if (index == 0) {
                            distance = -distance;
                        }
                        address += distance;
                    } catch (NumberFormatException e) {
                        this.distance.requestFocus();
                        throw e;
                    }
                }
                long val2 = address;
                return val2;
            } catch (NumberFormatException e2) {
                this.address.requestFocus();
                throw e2;
            }
        }
        long val3 = index == 0 ? 0 : -1;
        return val3;
    }

    public static long getMem(int index, EditText memoryFrom, String defaultFrom, EditText memoryTo, String defaultTo) throws NumberFormatException {
        EditText edit = index == 0 ? memoryFrom : memoryTo;
        String data = SearchMenuItem.checkScript(edit.getText().toString().trim());
        if (data.length() == 0) {
            data = index == 0 ? defaultFrom : defaultTo;
        }
        String from = SearchMenuItem.checkScript(memoryFrom.getText().toString().trim());
        boolean mask = from.contains(Searcher.MASK_PLACEHOLDER);
        if (mask) {
            data = from.replace(Searcher.MASK_PLACEHOLDER, index == 0 ? "0" : "F");
            if (index != 0) {
                edit.setText(data);
            }
        }
        try {
            long val = ParserNumbers.parseBigLong(data, 16);
            if (index == 0 && mask) {
                History.add(from, 1);
            }
            History.add(data, 1);
            return val;
        } catch (NumberFormatException e) {
            edit.requestFocus();
            throw e;
        }
    }
}
