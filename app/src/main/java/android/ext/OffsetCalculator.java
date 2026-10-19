package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.fix.LayoutInflater;
import android.fix.SparseArray;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class OffsetCalculator extends MenuItem implements DialogInterface.OnClickListener, DialogInterface.OnShowListener, TextWatcher, Runnable, CompoundButton.OnCheckedChangeListener, DialogInterface.OnDismissListener {
    private static String lastInput = "";
    private static String lastOffset = "";
    private static boolean useHex = true;
    private EditText editAddr;
    private EditText editOffset;
    private CheckBox hex;
    long result;
    private ImageView save;
    private final Long source;
    private TextView textResult;
    private TextView valueByte;
    private TextView valueDouble;
    private TextView valueDword;
    private TextView valueFloat;
    private TextView valueHex;
    private TextView valueJava;
    private TextView valueQword;
    private TextView valueRHex;
    private TextView valueString;
    private TextView valueWord;
    private TextView valueXor;

    public OffsetCalculator() {
        this(null);
    }

    public OffsetCalculator(Long source) {
        super(R.string.offset_calculator, R.drawable.ic_calculator_white_24dp);
        this.result = 0L;
        this.editAddr = null;
        this.editOffset = null;
        this.textResult = null;
        this.hex = null;
        this.save = null;
        this.source = source;
    }

    @Override // android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        if (v != null && v.getId() == R.id.save) {
            save();
        } else {
            new OffsetCalculator().show(null);
        }
    }

    private void save() {
        SparseIntArray counts = new SparseIntArray();
        SparseArray<CharSequence> list = AddressItem.getDataForEditAll(AddressItem.getTypeForAddress(this.result, false, counts));
        new TypeSelector(list, counts, "0", Re.s(R.string.type_change_request), new DialogInterface.OnClickListener() { // from class: android.ext.OffsetCalculator.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int flags) {
                SavedListAdapter savedList = MainService.instance.savedList;
                SavedItem curItem = new SavedItem(OffsetCalculator.this.result, 0L, flags);
                if (!curItem.isPossibleItem()) {
                    curItem.flags = AddressItem.getTypeForAddress(curItem.address, true);
                }
                SavedItem savedItem = savedList.getItemByAddress(OffsetCalculator.this.result);
                if (savedItem == null || savedItem.flags != curItem.flags) {
                    savedList.add(curItem);
                    savedList.reloadData();
                }
                Tools.showToast(Tools.stringFormat(Re.s(R.string.items_added), 1));
            }
        });
    }

    public void show(String addrString) {
        View view = LayoutInflater.inflateStatic(R.layout.service_offset, (ViewGroup) null);
        this.editAddr = (EditText) view.findViewById(R.id.address);
        this.editOffset = (EditText) view.findViewById(R.id.offset);
        this.textResult = (TextView) view.findViewById(R.id.result);
        this.save = Config.setIconSize((ImageView) view.findViewById(R.id.save));
        this.save.setOnClickListener(this);
        this.hex = (CheckBox) view.findViewById(R.id.hex);
        this.hex.setChecked(useHex);
        this.hex.setOnCheckedChangeListener(this);
        this.valueHex = (TextView) view.findViewById(R.id.value_hex);
        this.valueRHex = (TextView) view.findViewById(R.id.value_rhex);
        this.valueString = (TextView) view.findViewById(R.id.value_string);
        this.valueJava = (TextView) view.findViewById(R.id.value_java);
        this.valueDword = (TextView) view.findViewById(R.id.value_dword);
        this.valueFloat = (TextView) view.findViewById(R.id.value_float);
        this.valueDouble = (TextView) view.findViewById(R.id.value_double);
        this.valueWord = (TextView) view.findViewById(R.id.value_word);
        this.valueByte = (TextView) view.findViewById(R.id.value_byte);
        this.valueQword = (TextView) view.findViewById(R.id.value_qword);
        this.valueXor = (TextView) view.findViewById(R.id.value_xor);
        EditText editText = this.editAddr;
        if (addrString == null) {
            addrString = lastInput;
        }
        editText.setText(addrString);
        this.editAddr.setDataType(1);
        this.editOffset.setText(lastOffset);
        this.editOffset.setDataType(1);
        this.editAddr.addTextChangedListener(this);
        this.editOffset.addTextChangedListener(this);
        update();
        AlertDialog dialog = Alert.create().setView(InternalKeyboard.getView(view)).setPositiveButton(Re.s(R.string.goto1), this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).setNeutralButton(Re.s(R.string.copy), this).create();
        Alert.setOnDismissListener(dialog, this);
        Alert.setOnShowListener(dialog, this);
        Alert.show(dialog, this.editOffset);
    }

    private void update() {
        long addr;
        if (this.editOffset != null && this.textResult != null) {
            StringBuilder out = new StringBuilder();
            try {
                addr = ParserNumbers.parseBigLong(this.editAddr.getText().toString().trim(), 16);
            } catch (Throwable e) {
                Log.d("Failed parse", e);
                if (out.length() != 0) {
                    out.append(ListManager.TEXT_SEPARATOR);
                }
                out.append(Re.s(R.string.address));
                out.append(' ');
                out.append(e.getMessage());
                addr = 0;
            }
            String data = this.editOffset.getText().toString().trim();
            try {
                long offset = ParserNumbers.parseBigLong(data, this.hex.isChecked() ? 16 : 10);
                this.result = addr + offset;
            } catch (Throwable e2) {
                Log.d("Failed parse", e2);
                if (out.length() != 0) {
                    out.append(ListManager.TEXT_SEPARATOR);
                }
                out.append(Re.s(R.string.offset));
                out.append(' ');
                out.append(e2.getMessage());
                this.result = 0L;
            }
            boolean clear = out.length() != 0;
            if (!clear) {
                out.append(AddressItem.getStringAddress(this.result, 4));
                Handler handler = ThreadManager.getHandlerMainThread();
                handler.removeCallbacks(this);
                handler.post(this);
            } else {
                updateValue(0L, 0L, clear);
            }
            this.textResult.setText(out.toString());
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        final long address = this.result;
        try {
            int type = AddressItem.getTypeForAddress(address, true);
            final long value = MainService.instance.mDaemonManager.getMemoryContent(address, type);
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.OffsetCalculator.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        OffsetCalculator.this.updateValue(address, value, false);
                    } catch (Throwable e) {
                        Log.w("Failed update value: " + address + ' ' + value, e);
                    }
                }
            });
        } catch (Throwable e) {
            Log.w("Failed update value: " + address, e);
        }
    }

    void updateValue(long address, long value, boolean clear) {
        String text;
        String text2;
        String text3;
        String text4;
        if (this.textResult != null) {
            int size = getSize(address, value, getSize(address, value, getSize(address, value, getSize(address, value, getSize(address, value, getSize(address, value, getSize(address, value, 0, this.valueDouble, 64, 8, 4, clear), this.valueQword, 32, 8, 4, clear), this.valueFloat, 16, 4, 4, clear), this.valueDword, 4, 4, 4, clear), this.valueXor, 8, 4, 4, clear), this.valueWord, 2, 2, 2, clear), this.valueByte, 1, 1, 1, clear);
            if (size == 0) {
                size = 1;
            }
            if (!clear) {
                if (size != 8) {
                    value &= (1 << (size * 8)) - 1;
                }
                text = String.valueOf(ToolsLight.prefixLongHex(size * 2, value)) + "h;";
            } else {
                text = "-h;";
            }
            this.valueHex.setText(text);
            if (!clear) {
                long revData = Long.reverseBytes(value) >> (64 - (size * 8));
                if (size != 8) {
                    revData &= (1 << (size * 8)) - 1;
                }
                text2 = String.valueOf(ToolsLight.prefixLongHex(size * 2, revData)) + "r;";
            } else {
                text2 = "-r;";
            }
            this.valueRHex.setText(text2);
            if (!clear) {
                text3 = String.valueOf(SearchMenuItem.HEX_UTF_8) + MemoryContentAdapter.longToString(value, size) + "';";
            } else {
                text3 = "-;";
            }
            this.valueString.setText(text3);
            if (!clear) {
                text4 = String.valueOf(SearchMenuItem.HEX_UTF_16LE) + MemoryContentAdapter.longToJava(value, size) + "\";";
            } else {
                text4 = "-;";
            }
            this.valueJava.setText(text4);
        }
    }

    private int getSize(long address, long data, int size, TextView view, int type, int typeSize, int align, boolean clear) {
        String value;
        if (!clear && (((long) (align - 1)) & address) == 0) {
            value = AddressItem.getStringDataTrim(address, data, type);
            if (size == 0) {
                size = typeSize;
            }
        } else {
            value = "-";
        }
        view.setText(String.valueOf(value) + ((Object) AddressItem.getShortName(type)) + ';');
        return size;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        String resultString = AddressItem.getStringAddress(this.result, 4);
        switch (which) {
            case -3:
                History.add(this.editOffset.getText().toString().trim(), 1);
                Tools.copyText(resultString);
                break;
            case -1:
                History.add(this.editOffset.getText().toString().trim(), 1);
                MainService.instance.goToAddress(this.source, resultString, String.valueOf(Tools.stripColon(R.string.from_calc)) + ": (" + this.editAddr.getText().toString().trim() + " + " + this.editOffset.getText().toString().trim() + ')');
                break;
        }
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialog) {
        if (this.editAddr.getText().length() > 0) {
            this.editOffset.requestFocus();
        } else {
            this.editAddr.requestFocus();
        }
        Tools.selectAll(this.editOffset);
        update();
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        if (this.editAddr != null) {
            useHex = this.hex.isChecked();
            lastOffset = this.editOffset.getText().toString().trim();
            lastInput = this.editAddr.getText().toString().trim();
        }
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        update();
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
        update();
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        update();
    }
}
