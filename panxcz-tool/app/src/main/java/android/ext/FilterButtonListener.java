package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.fix.LayoutInflater;
import android.fix.SparseArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class FilterButtonListener extends MenuItem {
    static final SparseBooleanArray checked = new SparseBooleanArray();

    public FilterButtonListener(MainService service) {
        super(R.string.filter, R.drawable.ic_filter_white_24dp);
    }

    @Override // android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        new Impl(this, null).onClick(v);
    }

    private class Impl implements View.OnClickListener, DialogInterface.OnClickListener, DialogInterface.OnShowListener, View.OnLongClickListener {
        private CheckBox cAddrGreat;
        private CheckBox cAddrLess;
        private CheckBox cFractional;
        private CheckBox cPointer;
        private CheckBox cType;
        private CheckBox cValGreat;
        private CheckBox cValLess;
        private AlertDialog dialog;
        private EditText eAddrGreat;
        private EditText eAddrLess;
        private EditText eFractional;
        private EditText eMaxShow;
        private EditText eValGreat;
        private EditText eValLess;
        private SharedPreferences preferences;
        private SystemSpinner sFractional;
        private SystemSpinner sPointer;
        private SystemSpinner sType;
        private final int[] showMasks;
        private final CheckBox[] values;
        private View view;

        private Impl() {
            this.showMasks = new int[]{1, 2, 4, 8, 16, 32, 64};
            this.values = new CheckBox[this.showMasks.length];
        }

        /* synthetic */ Impl(FilterButtonListener filterButtonListener, Impl impl) {
            this();
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            onClick(((AlertDialog) dialog).getButton(which));
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            Tools.setButtonListener(dialog, -1, null, this, this.eMaxShow);
            Tools.setButtonListener(dialog, -3, true, this);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            String str;
            if (v == null || (v.getTag() instanceof MenuItem)) {
                loadResources();
                AlertDialog dialog = Alert.create().setView(InternalKeyboard.getView(Tools.getViewForAttach(this.view))).setPositiveButton(Re.s(R.string.apply), this).setNeutralButton(Re.s(R.string.reset_button), this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).create();
                this.dialog = dialog;
                Alert.setOnShowListener(dialog, this);
                Alert.show(dialog, this.eMaxShow);
                return;
            }
            if (v.getTag() instanceof Boolean) {
                this.cAddrGreat.setChecked(false);
                this.cAddrLess.setChecked(false);
                this.cValGreat.setChecked(false);
                this.cValLess.setChecked(false);
                this.cType.setChecked(false);
                this.cFractional.setChecked(false);
                this.cPointer.setChecked(false);
                for (int i = 0; i < this.showMasks.length; i++) {
                    this.values[i].setChecked(false);
                }
            }
            try {
                String str2 = this.eMaxShow.getText().toString().trim();
                int show = Converter.parseStringToInt(str2);
                if (show < 1) {
                    throw new NumberFormatException(Tools.stringFormat(Re.s(R.string.number_less), Re.s(R.string.max_show_rec), 1));
                }
                if (show > 100000) {
                    throw new NumberFormatException(Tools.stringFormat(Re.s(R.string.number_greater), Re.s(R.string.max_show_rec), 100000));
                }
                String string = !this.cAddrGreat.isChecked() ? null : this.eAddrGreat.getText().toString();
                String string2 = !this.cAddrLess.isChecked() ? null : this.eAddrLess.getText().toString();
                String string3 = !this.cValGreat.isChecked() ? null : this.eValGreat.getText().toString();
                String string4 = !this.cValLess.isChecked() ? null : this.eValLess.getText().toString();
                int selected = !this.cType.isChecked() ? 0 : this.sType.getSelected();
                if (this.cFractional.isChecked()) {
                    str = String.valueOf(this.sFractional.getSelected() == 268435456 ? "!" : "") + this.eFractional.getText().toString();
                } else {
                    str = null;
                }
                Converter.setFilters(string, string2, string3, string4, selected, str, !this.cPointer.isChecked() ? 0 : this.sPointer.getSelected());
                Converter.setShowCount(show);
                int mask = 0;
                for (int i2 = 0; i2 < this.showMasks.length; i2++) {
                    if (this.values[i2].isChecked()) {
                        mask |= this.showMasks[i2];
                    }
                }
                AddressArrayAdapter.setShowMask(mask);
                SPEditor editor = new SPEditor(this.preferences.edit());
                doView(editor, this.view);
                editor.commit();
                Tools.dismiss(this.dialog);
                MainService.instance.refreshResultList(false);
            } catch (NumberFormatException ex) {
                Log.w("Error", ex);
                Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.error)).setMessage(ex.getMessage()).setPositiveButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
            }
        }

        private void doView(SPEditor editor, View view) {
            if (view != null) {
                if (view instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) view;
                    int count = viewGroup.getChildCount();
                    for (int i = 0; i < count; i++) {
                        try {
                            View child = viewGroup.getChildAt(i);
                            if (child != null) {
                                doView(editor, child);
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                        }
                    }
                    return;
                }
                int id = view.getId();
                if (id != -1) {
                    if (view instanceof EditText) {
                        EditText edit = (EditText) view;
                        String defValue = id == R.id.eMaxShow ? "100" : "";
                        if (editor == null) {
                            edit.setText(this.preferences.getString(Re.getName(id), defValue));
                            edit.setDataType(1);
                            return;
                        } else {
                            String value = edit.getText().toString().trim();
                            History.add(value, 1);
                            editor.putString(Re.getName(id), value, "");
                            return;
                        }
                    }
                    if (view instanceof SystemSpinner) {
                        SystemSpinner choice = (SystemSpinner) view;
                        int defValue2 = id == R.id.sFractional ? AddressItem.FLAG_VALUE_EQUAL : 0;
                        if (editor == null) {
                            choice.setSelected(this.preferences.getInt(Re.getName(id), defValue2));
                            return;
                        } else {
                            editor.putInt(Re.getName(id), choice.getSelected(), defValue2);
                            return;
                        }
                    }
                    if (view instanceof CheckBox) {
                        CheckBox checkbox = (CheckBox) view;
                        if (editor == null) {
                            checkbox.setChecked(FilterButtonListener.checked.get(id, false));
                        } else {
                            FilterButtonListener.checked.put(id, checkbox.isChecked());
                        }
                    }
                }
            }
        }

        private void addWatcher(View view, final CheckBox check) {
            if (view instanceof EditText) {
                ((EditText) view).addTextChangedListener(new TextWatcher() { // from class: android.ext.FilterButtonListener.Impl.1
                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                        check.setChecked(true);
                    }
                });
            } else if (view instanceof SystemSpinner) {
                ((SystemSpinner) view).setOnItemSelectedListener(new DialogInterface.OnClickListener() { // from class: android.ext.FilterButtonListener.Impl.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        check.setChecked(true);
                    }
                });
            }
        }

        private void loadResources() {
            if (this.view == null) {
                this.view = LayoutInflater.inflateStatic(R.layout.service_filter_search, (ViewGroup) null);
                this.preferences = Tools.getSharedPreferences();
                this.eMaxShow = (EditText) this.view.findViewById(R.id.eMaxShow);
                this.cAddrGreat = (CheckBox) this.view.findViewById(R.id.cAddrGreat);
                this.eAddrGreat = (EditText) this.view.findViewById(R.id.eAddrGreat);
                this.cAddrLess = (CheckBox) this.view.findViewById(R.id.cAddrLess);
                this.eAddrLess = (EditText) this.view.findViewById(R.id.eAddrLess);
                this.cValGreat = (CheckBox) this.view.findViewById(R.id.cValGreat);
                this.eValGreat = (EditText) this.view.findViewById(R.id.eValGreat);
                this.cValLess = (CheckBox) this.view.findViewById(R.id.cValLess);
                this.eValLess = (EditText) this.view.findViewById(R.id.eValLess);
                this.cType = (CheckBox) this.view.findViewById(R.id.cType);
                this.sType = (SystemSpinner) this.view.findViewById(R.id.sType);
                this.cFractional = (CheckBox) this.view.findViewById(R.id.cFractional);
                this.sFractional = (SystemSpinner) this.view.findViewById(R.id.sFractional);
                this.eFractional = (EditText) this.view.findViewById(R.id.eFractional);
                this.cPointer = (CheckBox) this.view.findViewById(R.id.cPointer);
                this.sPointer = (SystemSpinner) this.view.findViewById(R.id.sPointer);
                int[] ids = {R.id.value_hex, R.id.value_rhex, R.id.value_string, R.id.value_java, R.id.value_arm, R.id.value_thumb, R.id.value_arm8};
                CharSequence[] names = MemoryContentAdapter.getNames();
                int[] colors = MemoryContentAdapter.getColors();
                for (int i = 0; i < this.showMasks.length; i++) {
                    CheckBox ch = (CheckBox) this.view.findViewById(ids[i]);
                    ch.setTextColor(colors[i]);
                    ch.setText(names[i]);
                    ch.setChecked((AddressArrayAdapter.showMask & this.showMasks[i]) != 0);
                    ch.setId(-1);
                    this.values[i] = ch;
                }
                this.cAddrGreat.setText(String.valueOf(Tools.stripColon(R.string.address)) + " ≥");
                this.cAddrLess.setText(String.valueOf(Tools.stripColon(R.string.address)) + " ≤");
                this.cValGreat.setText(String.valueOf(Tools.stripColon(R.string.number)) + " ≥");
                this.cValLess.setText(String.valueOf(Tools.stripColon(R.string.number)) + " ≤");
                this.sType.setData(AddressItem.getDataForEditAll(-1));
                Tools.setButtonHelpBackground(this.cFractional);
                this.cFractional.setOnLongClickListener(this);
                this.sFractional.setData(AddressItem.getSignNamesSmall());
                SparseArray<CharSequence> data = new SparseArray<>();
                data.put(4, Tools.colorize("-: " + Re.s(R.string.no), Tools.getColor(R.color.pointer_unknown)));
                data.put(8, Tools.colorize("R: " + Re.s(R.string.read_only), Tools.getColor(R.color.pointer_readable)));
                data.put(16, Tools.colorize("W: " + Re.s(R.string.writable), Tools.getColor(R.color.pointer_writable)));
                data.put(2, Tools.colorize("X: " + Re.s(R.string.executable), Tools.getColor(R.color.pointer_executable)));
                data.put(1, Tools.colorize("WX: " + Re.s(R.string.writable_and_executable), Tools.getColor(R.color.pointer_executable_writable)));
                this.sPointer.setData(data);
                doView(null, this.view);
                addWatcher(this.eAddrGreat, this.cAddrGreat);
                addWatcher(this.eAddrLess, this.cAddrLess);
                addWatcher(this.eValGreat, this.cValGreat);
                addWatcher(this.eValLess, this.cValLess);
                addWatcher(this.sType, this.cType);
                addWatcher(this.sFractional, this.cFractional);
                addWatcher(this.eFractional, this.cFractional);
                addWatcher(this.sPointer, this.cPointer);
            }
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View v) {
            if (v == null) {
                return false;
            }
            switch (v.getId()) {
                case R.id.cFractional /* 2131427606 */:
                    Alert.show(Alert.create().setMessage(Re.s(R.string.fractional_hint_)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                    return true;
                default:
                    return false;
            }
        }
    }
}
