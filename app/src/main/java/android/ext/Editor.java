package android.ext;

import android.content.DialogInterface;
import android.ext.InternalKeyboard;
import android.fix.LayoutInflater;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class Editor implements TextWatcher, View.OnClickListener, View.OnLongClickListener {
    public static final int MODE_EDIT = 1;
    public static final int MODE_EDIT_ALL = 2;
    public static final int MODE_EDIT_MEM = 3;
    public static final int MODE_EDIT_SAVED = 0;
    private static volatile boolean editAllExtended = false;
    final CheckBox addNotReplace;
    final CheckBox changeValue;
    private final EditText editStep;
    private final View editStepRow;
    private String examplesList;
    private final Button extBtn;
    private final View fill;
    private final View fillHex;
    private final View fillHexUtf16le;
    private final View fillHexUtf8;
    private final View fillHexUtf8Utf16le;
    private final View fillUtf16le;
    private final View fillUtf8;
    private final View fillValues;
    final CheckBox freezeCheck;
    private final EditText freezeFrom;
    final View freezeRange;
    private final EditText freezeTo;
    private final SystemSpinner freezeType;
    private final AddressItem item;
    private final EditText number;
    private int type;
    private final TextView typeHint;
    private final int[][] typeMatrix;
    private final View[] typeMatrixHeader;
    private final View view = LayoutInflater.inflateStatic(R.layout.service_editor, (ViewGroup) null);
    private final TextView message = (TextView) this.view.findViewById(R.id.message);
    private final View nameRow = this.view.findViewById(R.id.name_row);
    private final CheckBox saveAs = (CheckBox) this.view.findViewById(R.id.save_as);
    private final View nameText = this.view.findViewById(R.id.name_text);
    final EditText name = (EditText) this.view.findViewById(R.id.name);

    public Editor(int type, AddressItem item) {
        this.type = type;
        this.item = item.copy();
        this.name.setDataType(3);
        this.typeHint = (TextView) this.view.findViewById(R.id.type_hint);
        this.number = (EditText) this.view.findViewById(R.id.number);
        this.number.setDataType(1);
        View viewFindViewById = this.view.findViewById(R.id.number_converter);
        Object[] objArr = new Object[2];
        objArr[0] = this.number;
        objArr[1] = Integer.valueOf(item == null ? 0 : item.flags);
        viewFindViewById.setTag(objArr);
        this.editStepRow = this.view.findViewById(R.id.edit_step_row);
        this.editStep = (EditText) this.view.findViewById(R.id.edit_step);
        this.editStep.setDataType(1);
        this.view.findViewById(R.id.step_converter).setTag(this.editStep);
        View label = this.view.findViewById(R.id.edit_step_label);
        Tools.setButtonHelpBackground(label);
        label.setOnClickListener(this);
        this.addNotReplace = (CheckBox) this.view.findViewById(R.id.add_not_replace);
        Tools.setButtonHelpBackground(this.addNotReplace);
        this.addNotReplace.setOnLongClickListener(this);
        this.fill = this.view.findViewById(R.id.fill);
        this.fillValues = this.view.findViewById(R.id.fill_values);
        this.fillUtf8 = this.view.findViewById(R.id.fill_utf8);
        this.fillUtf16le = this.view.findViewById(R.id.fill_utf16le);
        this.fillHex = this.view.findViewById(R.id.fill_hex);
        this.fillHexUtf8 = this.view.findViewById(R.id.fill_hex_utf8);
        this.fillHexUtf16le = this.view.findViewById(R.id.fill_hex_utf16le);
        this.fillHexUtf8Utf16le = this.view.findViewById(R.id.fill_hex_utf8_utf16le);
        this.changeValue = (CheckBox) this.view.findViewById(R.id.change_value);
        this.freezeCheck = (CheckBox) this.view.findViewById(R.id.freeze);
        this.freezeType = (SystemSpinner) this.view.findViewById(R.id.freeze_spinner);
        this.freezeRange = this.view.findViewById(R.id.freeze_range);
        this.freezeFrom = (EditText) this.view.findViewById(R.id.freeze_from);
        this.freezeFrom.setDataType(1);
        this.view.findViewById(R.id.from_converter).setTag(this.freezeFrom);
        this.freezeTo = (EditText) this.view.findViewById(R.id.freeze_to);
        this.freezeTo.setDataType(1);
        this.view.findViewById(R.id.to_converter).setTag(this.freezeTo);
        this.extBtn = (Button) this.view.findViewById(R.id.ext_btn);
        View.OnFocusChangeListener focus = new InternalKeyboard.FocusListener() { // from class: android.ext.Editor.1
            @Override // android.ext.InternalKeyboard.FocusListener
            protected boolean useExternal(View v, boolean hasFocus) {
                return Editor.this.updateKbd() || v == Editor.this.name;
            }
        };
        for (View v : this.view.getFocusables(2)) {
            if (v instanceof EditText) {
                Tools.setOnFocusChangeListener(v, focus);
            }
        }
        this.typeMatrixHeader = new View[]{this.nameRow, this.saveAs, this.nameText, this.editStepRow, this.fill, this.extBtn, this.changeValue};
        int[] iArr = new int[7];
        iArr[0] = 1;
        iArr[2] = 1;
        int[] iArr2 = new int[7];
        iArr2[0] = 1;
        iArr2[1] = 1;
        int[] iArr3 = new int[7];
        iArr3[0] = 1;
        iArr3[1] = 1;
        this.typeMatrix = new int[][]{iArr, iArr2, new int[]{0, 0, 0, 1, 1, 1, 1}, iArr3};
        if (type < 0 || type >= this.typeMatrix.length) {
            throw new IllegalArgumentException("Type must be between: 0 and " + (this.typeMatrix.length - 1) + " but got: " + type);
        }
        setVisibility(type);
        this.examplesList = Re.s(R.string.edit_examples);
        TextView value = (TextView) this.view.findViewById(R.id.value);
        if (type == 2) {
            Tools.setButtonHelpBackground(value);
            value.setOnClickListener(this);
            this.examplesList = Re.s(R.string.edit_all_examples);
            setEditAllExtended(editAllExtended);
        } else if (type == 1) {
            Tools.setButtonHelpBackground(value);
            value.setOnClickListener(this);
        }
        this.extBtn.setOnClickListener(this);
        String valueText = Tools.stripColon(value.getText().toString());
        value.setText(String.valueOf(valueText) + ':');
        init();
        this.name.addTextChangedListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.ext_btn /* 2131427353 */:
                    if (this.type == 2) {
                        setEditAllExtended(!editAllExtended);
                    }
                    this.number.requestFocus();
                    break;
                case R.id.value /* 2131427355 */:
                    Searcher.showExamples(this.examplesList);
                    break;
                case R.id.edit_step_label /* 2131427572 */:
                    ConfigListAdapter.showHelp(R.string.help_fill);
                    break;
            }
        }
    }

    public void setFillListener(View.OnClickListener listener) {
        this.fillValues.setOnClickListener(listener);
        this.fillUtf8.setOnClickListener(listener);
        this.fillUtf16le.setOnClickListener(listener);
        this.fillHex.setOnClickListener(listener);
        this.fillHexUtf8.setOnClickListener(listener);
        this.fillHexUtf16le.setOnClickListener(listener);
        this.fillHexUtf8Utf16le.setOnClickListener(listener);
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        if (v == null) {
            return false;
        }
        switch (v.getId()) {
            case R.id.add_not_replace /* 2131427575 */:
                ConfigListAdapter.showHelp(R.string.help_add_to_value);
                return true;
            default:
                return false;
        }
    }

    private void setEditAllExtended(boolean value) {
        editAllExtended = value;
        this.extBtn.setText(value ? Re.s(R.string.less) : Re.s(R.string.more));
        this.editStepRow.setVisibility(value ? 0 : 8);
        this.fill.setVisibility(value ? 0 : 8);
        this.fillUtf8.setVisibility((value && this.item.flags == 1) ? 0 : 8);
        this.fillUtf16le.setVisibility((!value || (this.item.flags & 7) == 0) ? 8 : 0);
        int hex = (value && this.item.flags == 1) ? 0 : 8;
        this.fillHex.setVisibility(hex);
        this.fillHexUtf8.setVisibility(hex);
        this.fillHexUtf16le.setVisibility(hex);
        this.fillHexUtf8Utf16le.setVisibility(hex);
    }

    private void setVisibility(int type) {
        for (int i = 0; i < this.typeMatrix[type].length; i++) {
            this.typeMatrixHeader[i].setVisibility(this.typeMatrix[type][i] == 1 ? 0 : 8);
        }
    }

    private void init() {
        this.typeHint.setText(AddressItem.getLimits(this.item.flags));
        this.number.setText(this.item.getStringDataTrim());
        this.editStep.setText("0");
        this.changeValue.setChecked(true);
        TextWatcher valueChanged = new TextWatcher() { // from class: android.ext.Editor.2
            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                action();
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                action();
            }

            private void action() {
                Editor.this.changeValue.setChecked(true);
                Editor.this.updateKbd();
            }
        };
        this.number.addTextChangedListener(valueChanged);
        this.editStep.addTextChangedListener(valueChanged);
        this.addNotReplace.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: android.ext.Editor.3
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Editor.this.freezeCheck.setChecked(false);
                }
                Editor.this.changeValue.setChecked(true);
            }
        });
        this.freezeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: android.ext.Editor.4
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Editor.this.addNotReplace.setChecked(false);
                }
            }
        });
        this.freezeType.setData(SavedItem.getFreezeNames());
        this.freezeType.setOnItemSelectedListener(new DialogInterface.OnClickListener() { // from class: android.ext.Editor.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int position) {
                Editor.this.freezeRange.setVisibility(position == 3 ? 0 : 8);
            }
        });
        if (this.item instanceof SavedItem) {
            SavedItem savedItem = (SavedItem) this.item;
            this.name.setText(savedItem.getName());
            this.freezeCheck.setChecked(savedItem.freeze);
            this.freezeType.setSelected(savedItem.freezeType);
            this.freezeFrom.setText(savedItem.getRangeString(true));
            this.freezeTo.setText(savedItem.getRangeString(false));
            return;
        }
        this.freezeCheck.setChecked(false);
        this.freezeType.setSelected(0);
        this.freezeFrom.setText("0");
        this.freezeTo.setText("0");
    }

    public void setMessage(CharSequence message) {
        this.message.setText(message);
    }

    public String getName() {
        String str = this.name.getText().toString().trim();
        History.add(str, 2);
        return str;
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public String getNumber() {
        String str = this.number.getText().toString().trim();
        History.add(str, 1);
        return str;
    }

    public void setNumber(String number) {
        this.number.setText(number.trim());
    }

    public String getEditStep() {
        if (this.editStepRow.getVisibility() != 0) {
            return "0";
        }
        String str = this.editStep.getText().toString().trim();
        History.add(str, 1);
        return str;
    }

    public boolean isValueChange() {
        return this.changeValue.isChecked();
    }

    public View getViewForAttach() {
        return InternalKeyboard.getView(getViewForAttachSimple());
    }

    public View getViewForAttachSimple() {
        return Tools.getViewForAttach(getView());
    }

    public View getView() {
        return this.view;
    }

    public EditText getNumberEdit() {
        return this.number;
    }

    public AddressItem getItem() {
        return this.item;
    }

    public void inSavedList() {
        this.saveAs.setChecked(true);
    }

    public boolean isNeedSave() {
        if (this.freezeCheck.isChecked()) {
            return true;
        }
        return this.nameRow.getVisibility() == 0 && ((this.saveAs.getVisibility() == 0 && this.saveAs.isChecked()) || this.nameText.getVisibility() == 0);
    }

    public SavedItem getSavedItem(AddressItem item) {
        SavedItem savedItem = new SavedItem(item);
        savedItem.freeze = this.freezeCheck.isChecked();
        savedItem.setFreezeType(this.freezeType.getSelected());
        if (savedItem.freezeType == 3) {
            savedItem.setRangeFromString(SearchMenuItem.checkScript(this.freezeFrom.getText().toString().trim()), SearchMenuItem.checkScript(this.freezeTo.getText().toString().trim()));
        }
        if (!savedItem.freeze && this.addNotReplace.isChecked()) {
            savedItem.flags |= AddressItem.FLAG_ALTER_ADD;
        }
        if ((this.saveAs.getVisibility() == 0 && this.saveAs.isChecked()) || this.nameText.getVisibility() == 0) {
            String name = getName();
            if (!savedItem.getName().equals(name)) {
                savedItem.name = name;
            }
        }
        return savedItem;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateKbd() {
        boolean str = false;
        if ((Config.configClient & 1) != 0) {
            if (this.number.isFocused() && ParserNumbers.needExtKbd(this.number.getText().toString())) {
                str = true;
            }
            InternalKeyboard.needExternalKbd(this.number, str);
        }
        return str;
    }

    public SavedItem getSavedItem() {
        return getSavedItem(this.item);
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.saveAs.setChecked(true);
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
        this.saveAs.setChecked(true);
    }
}
