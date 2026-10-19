package android.ext;

import android.content.DialogInterface;
import android.fix.LayoutInflater;
import android.fix.SparseArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class Searcher implements DialogInterface.OnClickListener, TextWatcher, View.OnFocusChangeListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener, Runnable {
    private static final int ASM_ARM = -500;
    private static final int ASM_ARM64 = -700;
    private static final int ASM_THUMB = -600;
    public static final String FUZZY_EQUAL = "N = O+D";
    public static final String FUZZY_GREATER = "N > O+D";
    public static final String FUZZY_LESS = "N < O+D";
    public static final String FUZZY_NOT_EQUAL = "N ≠ O+D";
    private static final int HEX = -300;
    private static final int HEX_UTF = -400;
    public static final String MASK_PLACEHOLDER = "?";
    public static final int MODE_SEARCH = 0;
    public static final int MODE_SEARCH_DIFF = 2;
    public static final int MODE_SEARCH_FUZZY = 1;
    public static final int MODE_SEARCH_MASK = 3;
    public static final int MODE_SEARCH_POINTER = 4;
    private static final int UTF_16LE = -200;
    private static final int UTF_8 = -100;
    private final Button cancel;
    private String examplesList;
    private final Button extBtn;
    private final int flags;
    private final View fuzzyRow;
    private final Button hex1;
    private final Button hex2;
    private final EditText mask;
    private final View maskRow;
    private final TextView maskView;
    final MemoryRange memoryRange;
    private final Button minGroupSize;
    private final CheckBox modeHacking;
    private final View modeHackingRow;
    private final EditText number;

    /* JADX INFO: renamed from: numberСonverter, reason: contains not printable characters */
    private final View f0numberonverter;
    private final EditText offset;
    private final CheckBox offsetHex;
    private final View offsetRow;
    private final CheckBox ordered;
    private int type;
    private final TextView typeHint;
    private final int[][] typeMatrix;
    private final View[] typeMatrixHeader;
    private final View typeRow;
    private final SystemSpinnerType typeSpinner;
    private final TextView value;
    private final View valueRow;
    public static final int[] ranges = new int[5];
    public static volatile boolean fuzzyExtended = false;
    public volatile boolean focusValue = true;
    private boolean prevKbd = false;
    private char prevStr = 0;
    private int prevFlags = 0;
    private final View view = LayoutInflater.inflateStatic(R.layout.service_searcher, (ViewGroup) null);
    private final TextView message = (TextView) this.view.findViewById(R.id.message);
    private final SystemSpinner signSpinner = (SystemSpinner) this.view.findViewById(R.id.sign_spinner);

    public Searcher(int type, int flags) {
        this.type = type;
        this.flags = flags;
        this.signSpinner.setOnItemSelectedListener(this);
        this.typeHint = (TextView) this.view.findViewById(R.id.type_hint);
        this.number = (EditText) this.view.findViewById(R.id.number);
        this.number.setDataType(1);
        this.f0numberonverter = this.view.findViewById(R.id.number_converter);
        this.valueRow = this.view.findViewById(R.id.value_row);
        this.maskRow = this.view.findViewById(R.id.edit_mask_row);
        this.mask = (EditText) this.view.findViewById(R.id.edit_mask);
        this.maskView = (TextView) this.view.findViewById(R.id.mask_view);
        TextWatcher maskWatcher = new TextWatcher() { // from class: android.ext.Searcher.1
            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Searcher.this.updateMaskView();
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                Searcher.this.updateMaskView();
            }
        };
        this.mask.addTextChangedListener(maskWatcher);
        this.offsetRow = this.view.findViewById(R.id.offset_row);
        this.offset = (EditText) this.view.findViewById(R.id.offset);
        this.offsetHex = (CheckBox) this.view.findViewById(R.id.hex);
        this.typeRow = this.view.findViewById(R.id.type_row);
        this.typeSpinner = (SystemSpinnerType) this.view.findViewById(R.id.type);
        this.f0numberonverter.setTag(new Object[]{this.number, this.typeSpinner});
        this.typeSpinner.setOnItemSelectedListener(this);
        this.modeHackingRow = this.view.findViewById(R.id.mode_hacking_row);
        this.modeHacking = (CheckBox) this.view.findViewById(R.id.mode_hacking);
        this.modeHacking.setOnCheckedChangeListener(this);
        this.ordered = (CheckBox) this.view.findViewById(R.id.ordered);
        this.ordered.setOnCheckedChangeListener(this);
        this.hex1 = (Button) this.view.findViewById(R.id.hex1);
        this.hex1.setOnClickListener(this);
        this.hex2 = (Button) this.view.findViewById(R.id.hex2);
        this.hex2.setOnClickListener(this);
        this.minGroupSize = (Button) this.view.findViewById(R.id.min_group_size);
        this.minGroupSize.setTag(2);
        this.minGroupSize.setOnClickListener(this);
        this.fuzzyRow = this.view.findViewById(R.id.fuzzy_row);
        this.memoryRange = (MemoryRange) this.view.findViewById(R.id.memory_range);
        this.memoryRange.init();
        this.extBtn = (Button) this.view.findViewById(R.id.ext_btn);
        this.cancel = (Button) this.view.findViewById(R.id.cancel);
        for (View v : this.view.getFocusables(2)) {
            if (v instanceof EditText) {
                Tools.setOnFocusChangeListener(v, this);
            }
        }
        this.memoryRange.setType(ranges[type]);
        this.memoryRange.setTypeChangeListener(this);
        this.typeMatrixHeader = new View[]{this.signSpinner, this.valueRow, this.typeRow, this.modeHackingRow, this.fuzzyRow, this.extBtn, this.cancel, this.maskRow, this.typeHint, this.f0numberonverter, this.offsetRow};
        int[] iArr = new int[11];
        iArr[2] = 1;
        iArr[8] = 1;
        iArr[9] = 1;
        int[] iArr2 = new int[11];
        iArr2[0] = 1;
        iArr2[1] = 1;
        iArr2[2] = 1;
        iArr2[7] = 1;
        int[] iArr3 = new int[11];
        iArr3[1] = 1;
        iArr3[2] = 1;
        iArr3[10] = 1;
        this.typeMatrix = new int[][]{new int[]{1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0}, iArr, new int[]{0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0}, iArr2, iArr3};
        if (type < 0 || type >= this.typeMatrix.length) {
            throw new IllegalArgumentException("Type must be between: 0 and " + (this.typeMatrix.length - 1) + " but got: " + type);
        }
        setVisibility(type);
        this.examplesList = Re.s(R.string.examples_search);
        this.value = (TextView) this.view.findViewById(R.id.value);
        if (type == 2) {
            this.value.setText(R.string.difference);
            ((TextView) this.view.findViewById(R.id.fuzzy_text)).setText(Re.s("N - __new_value__; O - __old_value__; D - __difference__;"));
            Tools.setButtonHelpBackground(this.value);
            this.value.setOnClickListener(this);
            this.examplesList = Re.s(R.string.diff_examples);
            setFuzzyExtended(fuzzyExtended);
        } else if (type == 0) {
            Tools.setButtonHelpBackground(this.value);
            this.value.setOnClickListener(this);
        } else if (type == 3) {
            Tools.setButtonHelpBackground(this.value);
            this.value.setOnClickListener(this);
            this.value.setText(R.string.address);
            this.examplesList = Re.s(R.string.mask_examples);
            this.number.addTextChangedListener(maskWatcher);
            updateMaskView();
        } else if (type == 4) {
            this.value.setText(R.string.address);
            TextView offsetText = (TextView) this.view.findViewById(R.id.offset_text);
            Tools.setButtonHelpBackground(offsetText);
            offsetText.setOnClickListener(this);
        }
        this.extBtn.setOnClickListener(this);
        String valueText = Tools.stripColon(this.value.getText().toString());
        this.value.setText(this.signSpinner.getVisibility() != 0 ? String.valueOf(valueText) + ':' : valueText);
        init();
    }

    public void setOnCancelClickListener(View.OnClickListener l) {
        this.cancel.setOnClickListener(l);
    }

    public void setExamplesList(String examplesList) {
        this.examplesList = examplesList;
    }

    public static void showExamples(String examplesList) {
        Alert.show(Alert.create().setMessage(String.valueOf(Re.s(R.string.examples)) + ":\n" + examplesList).setPositiveButton(Re.s(R.string.help), ConfigListAdapter.getShowHelpListener()).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.ext_btn /* 2131427353 */:
                    if (this.type == 2) {
                        setFuzzyExtended(!fuzzyExtended);
                    }
                    if (this.number.getVisibility() == 0 && this.valueRow.getVisibility() == 0) {
                        this.number.requestFocus();
                        break;
                    }
                    break;
                case R.id.value /* 2131427355 */:
                    showExamples(this.examplesList);
                    break;
                case R.id.offset_text /* 2131427634 */:
                    Alert.show(Alert.create().setMessage(Re.s(R.string.offset_help)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                    break;
                case R.id.min_group_size /* 2131427639 */:
                case R.id.hex1 /* 2131427640 */:
                case R.id.hex2 /* 2131427641 */:
                    if (isGroupSearch()) {
                        Object tag = v.getTag();
                        if (tag instanceof Integer) {
                            int size = ((Integer) tag).intValue();
                            String text = this.number.getText().toString();
                            String split = text.contains("::") ? "::" : ":";
                            int st = text.indexOf(split);
                            int en = text.length();
                            if (st == -1) {
                                st = en;
                            }
                            this.number.getText().replace(st, en, String.valueOf(split) + size);
                        }
                    } else {
                        Editable ed = this.number.getText();
                        try {
                            if (ed.length() > 0) {
                                int id = v.getId();
                                char ch = ed.charAt(0);
                                boolean hex = ch == 'h' || ch == 'H';
                                if (id == R.id.min_group_size && !hex) {
                                    ParserNumbers.getBytes(ed);
                                    ed.replace(0, 1, "h");
                                } else if (id == R.id.hex1 && ch == ':') {
                                    ed.replace(0, ed.length(), "Q '" + ed.toString().substring(1).replace("'", "' 27 '") + SearchMenuItem.HEX_UTF_8);
                                } else if (id == R.id.hex1 && ch == ';') {
                                    ed.replace(0, ed.length(), "Q \"" + ed.toString().substring(1).replace("\"", "\" 22 \"") + SearchMenuItem.HEX_UTF_16LE);
                                } else if (hex) {
                                    byte[] bytes = ParserNumbers.getBytes(ed.toString());
                                    HexText.getText(null, 0, bytes, bytes.length, id == R.id.min_group_size || id == R.id.hex2, id == R.id.hex1 || id == R.id.hex2, ed);
                                    ed.replace(0, 1, "Q");
                                } else {
                                    char str = id == R.id.hex1 ? SearchMenuItem.HEX_UTF_16LE : SearchMenuItem.HEX_UTF_8;
                                    int start = 0;
                                    int i = 1;
                                    while (i < ed.length()) {
                                        if (ed.charAt(i) == str) {
                                            if (start == 0) {
                                                start = i + 1;
                                            } else {
                                                byte[] bytes2 = ed.toString().substring(start, i).getBytes(ParserNumbers.getCharset(str == '\"'));
                                                String codes = InOut.bytesToHex(bytes2, 0, bytes2.length).trim();
                                                ed.replace(start - 1, i + 1, codes);
                                                i += codes.length() - ((i + 1) - (start - 1));
                                                start = 0;
                                            }
                                        }
                                        i++;
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            Log.d("Failed convert '" + ((Object) ed) + "'", e);
                            return;
                        }
                    }
                    break;
            }
        }
    }

    private void setFuzzyExtended(boolean value) {
        fuzzyExtended = value;
        this.extBtn.setText(value ? Re.s(R.string.less) : Re.s(R.string.more));
        this.valueRow.setVisibility(value ? 0 : 8);
        this.typeRow.setVisibility(value ? 0 : 8);
        this.view.findViewById(R.id.fuzzy_text).setVisibility(value ? 0 : 8);
        ((TextView) this.view.findViewById(R.id.equal_btn)).setText(value ? FUZZY_EQUAL : Re.s(R.string.equal));
        ((TextView) this.view.findViewById(R.id.not_equal_btn)).setText(value ? FUZZY_NOT_EQUAL : Re.s(R.string.inequal));
        ((TextView) this.view.findViewById(R.id.larger_btn)).setText(value ? FUZZY_GREATER : Re.s(R.string.larger));
        ((TextView) this.view.findViewById(R.id.smaller_btn)).setText(value ? FUZZY_LESS : Re.s(R.string.smaller));
    }

    public void onShow() {
        EditText focus;
        switch (this.type) {
            case 1:
                focus = null;
                break;
            case 2:
                focus = !fuzzyExtended ? null : this.number;
                break;
            case 3:
                focus = !this.focusValue ? this.mask : this.number;
                break;
            case 4:
                focus = !this.focusValue ? this.offset : this.number;
                break;
            default:
                focus = this.number;
                break;
        }
        if (focus != null) {
            focus.requestFocus();
            Tools.selectAll(focus);
        } else {
            onFocusChange(this.view, false);
        }
    }

    @Override // android.view.View.OnFocusChangeListener
    public void onFocusChange(View v, boolean hasFocus) {
        boolean hide = true;
        if (hasFocus || (this.type != 1 && (this.type != 2 || fuzzyExtended))) {
            hide = false;
        }
        if ((Config.configClient & 1) == 0) {
            if (hide) {
                Tools.hideKeyboard(this.view);
                return;
            }
            return;
        }
        InternalKeyboard kbdView = (InternalKeyboard) v.getRootView().findViewById(R.id.keyboard);
        if (kbdView != null) {
            if (this.type == 0 && ParserNumbers.needExtKbd(this.number.getText().toString())) {
                InternalKeyboard.needExternalKbd(this.number, this.number.isFocused());
            } else {
                kbdView.setHideKeyboard(hide);
                Tools.hideKeyboard(v);
            }
        }
    }

    private void setVisibility(int type) {
        for (int i = 0; i < this.typeMatrix[type].length; i++) {
            this.typeMatrixHeader[i].setVisibility(this.typeMatrix[type][i] == 1 ? 0 : 8);
        }
    }

    private static String getMask(String input) {
        char placeholder = MASK_PLACEHOLDER.charAt(0);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            out.append(input.charAt(i) == placeholder ? "0" : "F");
        }
        return out.toString();
    }

    public long[] parseMask() throws NumberFormatException {
        return parseMask(getNumber(), getDirectMask());
    }

    public static long[] parseMask(String rawAddr, long directMask) throws NumberFormatException {
        long addr = ParserNumbers.parseBigLong(rawAddr.replace(MASK_PLACEHOLDER, "0"), 16);
        long indirectMask = ParserNumbers.parseBigLong(getMask(rawAddr), 16);
        long mask = indirectMask & directMask;
        return new long[]{addr & mask, mask};
    }

    void updateMaskView() {
        String mask;
        if (this.type == 3) {
            try {
                long[] data = parseMask();
                mask = Long.toHexString(data[1]).toUpperCase(Locale.US);
            } catch (Throwable th) {
                mask = "???";
            }
            this.maskView.setText(String.valueOf(Tools.stripColon(R.string.resultant_mask)) + ": " + mask);
        }
    }

    private void updateGroupSearchHelpers(boolean skip) {
        if (this.type == 0) {
            String text = this.number.getText().toString();
            boolean isString = ParserNumbers.isString(text);
            boolean isAsm = ParserNumbers.typeAsm(text) != 0;
            boolean isGroup = !isString && isGroupSearch();
            boolean needExt = ParserNumbers.needExtKbd(text);
            boolean strExt = isString && needExt;
            if ((this.ordered.getTag() != null) != strExt) {
                this.ordered.setText(strExt ? "UTF-16LE" : Re.s(R.string.ordered));
                this.ordered.setTag(strExt ? this.ordered : null);
            }
            if ((Config.configClient & 1) != 0) {
                boolean curr = needExt && this.number.isFocused();
                if (this.prevKbd != curr && (!curr || !skip)) {
                    this.prevKbd = curr;
                    InternalKeyboard.needExternalKbd(this.number, curr);
                }
            }
            setHint(isString ? text.charAt(0) : (char) 0, this.typeSpinner.getSelected());
            this.typeRow.setVisibility((isString || isAsm) ? 8 : 0);
            this.signSpinner.setVisibility((isString || isGroup) ? 8 : 0);
            this.modeHacking.setVisibility((isString || isAsm || isGroup) ? 8 : 0);
            this.ordered.setVisibility((strExt || isGroup) ? 0 : 8);
            this.minGroupSize.setVisibility((isString || isGroup) ? 0 : 8);
            this.hex1.setVisibility(isString ? 0 : 8);
            this.hex2.setVisibility(8);
            if (isString || isGroup) {
                if (this.modeHacking.isChecked()) {
                    this.modeHacking.setChecked(false);
                }
                if (getSign() != 536870912) {
                    setSign(AddressItem.FLAG_VALUE_EQUAL);
                }
                if (isGroup) {
                    String text2 = text.trim();
                    boolean isOrdered = text2.contains("::");
                    int type = getType();
                    if (type == 0) {
                        type = 4;
                    }
                    int min = SearchButtonListener.getMinGroupSize(text2, type, isOrdered);
                    this.ordered.setChecked(isOrdered);
                    this.minGroupSize.setTag(Integer.valueOf(min));
                    this.minGroupSize.setText(String.valueOf(isOrdered ? "::" : ":") + min);
                    return;
                }
                char invert = ParserNumbers.invertPrefix(text);
                this.ordered.setChecked(invert == ':' || invert == '\'');
                char ch = text.charAt(0);
                boolean hex = ch == 'h' || ch == 'H';
                this.minGroupSize.setText(hex ? "HEX+U8" : "HEX");
                this.hex1.setText((hex || ch == ';') ? "HEX+U16" : "HEX+U8");
                if (hex || ch == 'Q' || ch == 'q') {
                    this.hex2.setVisibility(0);
                }
                this.hex2.setText(hex ? "HEX+U8+U16" : "HEX+U16");
            }
        }
    }

    private void init() {
        setHint(this.prevStr, this.flags);
        this.signSpinner.setData(this.type == 3 ? AddressItem.getSignNamesSmall() : AddressItem.getSignNames());
        setType(AddressItem.getDataForSearch(this.flags, true), this.flags);
        this.number.addTextChangedListener(this);
    }

    public void setMessage(CharSequence message) {
        this.message.setText(message);
    }

    public int getType() {
        if (this.type == 0) {
            String input = this.number.getText().toString();
            if (ParserNumbers.isString(input)) {
                return input.charAt(0) == ';' ? 2 : 1;
            }
            int typeAsm = ParserNumbers.typeAsm(input);
            if (typeAsm != 0) {
                return typeAsm;
            }
        }
        return this.typeSpinner.getSelected();
    }

    private void setHint(char str, int flags) {
        String limits;
        if (this.prevFlags != flags || this.prevStr != str) {
            TextView textView = this.typeHint;
            if (this.type == 0 && str != 0) {
                limits = String.valueOf(Tools.stripColon(R.string.examples)) + ": " + ParserNumbers.example(this.number.getText().toString());
            } else {
                limits = AddressItem.getLimits(flags);
            }
            textView.setText(limits);
            this.prevStr = str;
            this.prevFlags = flags;
        }
    }

    private void updateHint() {
        setHint(this.prevStr, this.typeSpinner.getSelected());
        updateGroupSearchHelpers(false);
    }

    public void setType(int type) {
        this.typeSpinner.setSelected(type);
        updateHint();
    }

    SparseArray<CharSequence> setTypeInternal(int flags) {
        SparseArray<CharSequence> data = AddressItem.getDataForSearch(flags, true);
        int lastType = MainService.getLastType();
        if (lastType != 0 && data.get(lastType) == null) {
            lastType = flags;
        }
        setType(data, lastType);
        return data;
    }

    public void setType(SparseArray<CharSequence> data, int type) {
        if (this.type == 0) {
            String text = Re.s(R.string.text);
            data.put(UTF_8, String.valueOf(text) + " UTF-8");
            data.put(UTF_16LE, String.valueOf(text) + " UTF-16LE");
            data.put(HEX, "HEX");
            data.put(HEX_UTF, "HEX + UTF-8 + UTF-16LE");
            data.put(ASM_ARM, "ARM (x32)");
            data.put(ASM_THUMB, "Thumb");
        }
        this.typeSpinner.setData(data);
        setType(type);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        if (which <= UTF_8) {
            String prefix = null;
            int type = 0;
            switch (which) {
                case ASM_ARM64 /* -700 */:
                    prefix = "~A8 ";
                    type = 4;
                    break;
                case ASM_THUMB /* -600 */:
                    prefix = "~T ";
                    type = 2;
                    break;
                case ASM_ARM /* -500 */:
                    prefix = "~A ";
                    type = 4;
                    break;
                case HEX_UTF /* -400 */:
                    prefix = "Q";
                    type = 1;
                    break;
                case HEX /* -300 */:
                    prefix = "h";
                    type = 1;
                    break;
                case UTF_16LE /* -200 */:
                    prefix = ";";
                    type = 2;
                    break;
                case UTF_8 /* -100 */:
                    prefix = ":";
                    type = 1;
                    break;
            }
            if (prefix != null) {
                this.number.getText().insert(0, prefix);
                setType(type);
            }
        }
        updateHint();
        if (isModeHacking() && getSign() != 536870912) {
            this.modeHacking.setChecked(false);
        }
    }

    public int getSign() {
        return this.signSpinner.getSelected();
    }

    public void setSign(int sign) {
        this.signSpinner.setSelected(sign);
        if (isModeHacking() && sign != 536870912) {
            this.modeHacking.setChecked(false);
        }
    }

    public String getNumber() {
        if (this.type == 2 && !fuzzyExtended) {
            return "0";
        }
        String str = this.number.getText().toString().trim();
        History.add(str, 1);
        return str;
    }

    public void setNumber(String number) {
        this.number.setText(number.trim());
    }

    public String getMask() {
        String str = this.mask.getText().toString().trim();
        History.add(str, 1);
        return str;
    }

    public long getDirectMask() {
        String rawMask = getMask();
        return ParserNumbers.parseBigLong(rawMask, 16);
    }

    public void setMask(String number) {
        this.mask.setText(number.trim());
    }

    public String getOffset() {
        String str = this.offset.getText().toString().trim();
        History.add(str, 1);
        return str;
    }

    public void setOffset(String number) {
        this.offset.setText(number.trim());
    }

    public void setOffsetHex(boolean checked) {
        this.offsetHex.setChecked(checked);
    }

    public boolean isOffsetHex() {
        return this.offsetHex.isChecked();
    }

    public void setFocusValue(boolean focus) {
        this.focusValue = focus;
    }

    public boolean isModeHacking() {
        return this.modeHacking.isChecked();
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

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        updateGroupSearchHelpers(start == 0 && before == 1 && count == 0);
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
    }

    private boolean isGroupSearch() {
        if (this.type != 0) {
            return false;
        }
        String input = this.number.getText().toString().trim();
        return input.length() >= 1 && input.charAt(0) != '=' && input.indexOf(59) > 0;
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.mode_hacking /* 2131427637 */:
                if (isChecked && getSign() != 536870912) {
                    setSign(AddressItem.FLAG_VALUE_EQUAL);
                    break;
                }
                break;
            case R.id.ordered /* 2131427638 */:
                if (this.type == 0) {
                    String input = this.number.getText().toString();
                    if (ParserNumbers.isString(input)) {
                        char invert = ParserNumbers.invertPrefix(input);
                        if ((invert == ':' || invert == '\'') != isChecked) {
                            if (invert == '\'' || invert == '\"') {
                                char from = isChecked ? SearchMenuItem.HEX_UTF_8 : SearchMenuItem.HEX_UTF_16LE;
                                String to = String.valueOf(invert);
                                Editable ed = this.number.getText();
                                int is = input.length();
                                for (int i = 1; i < is; i++) {
                                    if (input.charAt(i) == from) {
                                        ed.replace(i, i + 1, to);
                                    }
                                }
                            } else {
                                this.number.getText().replace(0, 1, String.valueOf(invert));
                            }
                        }
                    }
                }
                if (isGroupSearch()) {
                    String input2 = this.number.getText().toString();
                    if (input2.contains("::") != isChecked) {
                        String from2 = isChecked ? ":" : "::";
                        int st = input2.indexOf(from2);
                        int en = st + from2.length();
                        if (st == -1) {
                            st = input2.length();
                            en = st;
                        }
                        this.number.getText().replace(st, en, isChecked ? "::" : ":");
                    }
                }
                break;
        }
    }

    public long getMem(int index) throws NumberFormatException {
        return this.memoryRange.getMem(index);
    }

    @Override // java.lang.Runnable
    public void run() {
        ranges[this.type] = this.memoryRange.getType();
    }
}
