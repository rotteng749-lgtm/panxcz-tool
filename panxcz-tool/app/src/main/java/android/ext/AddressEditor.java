package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.ext.Script;
import android.fix.LayoutInflater;
import android.fix.TextView;
import android.sup.LongSparseArrayChecked;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import catch_.me_.if_.you_.can_.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AddressEditor extends MenuItem {
    public AddressEditor() {
        super(R.string.change_address_type, R.drawable.ic_cursor_mover_white_24dp);
    }

    @Override // android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        new Impl(null).onClick(v);
    }

    public static void onClick(String address) {
        new Impl(address).onClick(null);
    }

    private static class Impl implements View.OnClickListener, DialogInterface.OnClickListener, DialogInterface.OnDismissListener, DialogInterface.OnShowListener, TextWatcher, CompoundButton.OnCheckedChangeListener {
        private static String lastInput = "0";
        private static boolean useHex = true;
        private final String address;
        private TextView alignWarn;
        private AlertDialog dialog;
        private EditText edit;
        private CheckBox hex;
        private CheckBox makeCopy;
        private SystemSpinnerType typeSpinner;
        private final int[] alignCount = new int[3];
        private final int[] modCount = new int[4];

        Impl(String address) {
            this.address = address;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            checkWarn();
            if (which == -1) {
                onClick(((AlertDialog) dialog).getButton(which));
            }
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            Tools.setButtonListener(dialog, -1, null, this, this.edit);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            char c;
            Object obj = MainService.instance.getCheckList();
            int count = Tools.getSelectedCount(obj);
            if (count == 0) {
                Tools.showToast(R.string.nothing_selected);
                return;
            }
            if (obj instanceof LongSparseArrayChecked) {
                if (v == null || (v.getTag() instanceof MenuItem)) {
                    View view = LayoutInflater.inflateStatic(R.layout.service_address_editor, (ViewGroup) null);
                    EditText edit = (EditText) view.findViewById(R.id.mem_edit_addr);
                    this.edit = edit;
                    this.makeCopy = (CheckBox) view.findViewById(R.id.make_copy);
                    CheckBox hex = (CheckBox) view.findViewById(R.id.hex);
                    this.hex = hex;
                    hex.setChecked(useHex);
                    hex.setOnCheckedChangeListener(this);
                    this.alignWarn = (TextView) view.findViewById(R.id.align_warn);
                    SystemSpinnerType typeSpinner = (SystemSpinnerType) view.findViewById(R.id.type);
                    this.typeSpinner = typeSpinner;
                    typeSpinner.setData(AddressItem.getDataForSearch(AddressItem.FLAG_AUTO, true));
                    typeSpinner.setSelected(AddressItem.FLAG_AUTO);
                    typeSpinner.setOnItemSelectedListener(this);
                    LongSparseArrayChecked<SavedItem> list = (LongSparseArrayChecked) obj;
                    int s = list.size();
                    int[] alignCount = this.alignCount;
                    Arrays.fill(alignCount, 0);
                    int[] modCount = this.modCount;
                    Arrays.fill(modCount, 0);
                    for (int i = 0; i < s; i++) {
                        if (list.checkAt(i)) {
                            SavedItem item = list.valueAt(i);
                            if (item instanceof SavedItem) {
                                int align = item.getAlign();
                                if (align == 1) {
                                    c = 0;
                                } else {
                                    c = align == 2 ? (char) 1 : (char) 2;
                                }
                                alignCount[c] = alignCount[c] + 1;
                                long addr = item.address;
                                int i2 = (int) (3 & addr);
                                modCount[i2] = modCount[i2] + 1;
                            }
                        }
                    }
                    edit.setDataType(1);
                    edit.addTextChangedListener(this);
                    if (this.address != null) {
                        LinearLayout group = (LinearLayout) view.findViewById(R.id.group);
                        group.setOrientation(1);
                        TextView name = (TextView) view.findViewById(R.id.name);
                        name.setText(String.valueOf(Tools.stripColon(R.string.new_address)) + ":");
                        hex.setVisibility(8);
                    } else {
                        edit.setText(lastInput);
                    }
                    checkWarn();
                    AlertDialog dialog = Alert.create().setView(InternalKeyboard.getView(view)).setPositiveButton(Re.s(R.string.ok), this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).create();
                    this.dialog = dialog;
                    Alert.setOnDismissListener(dialog, this);
                    Alert.setOnShowListener(dialog, this);
                    Alert.show(dialog, edit);
                    return;
                }
                try {
                    if (this.edit != null) {
                        long offset = getOffset(true);
                        List<SavedItem> changed = new ArrayList<>();
                        LongSparseArrayChecked<SavedItem> list2 = (LongSparseArrayChecked) obj;
                        int s2 = list2.size();
                        for (int i3 = 0; i3 < s2; i3++) {
                            if (list2.checkAt(i3)) {
                                SavedItem item2 = list2.valueAt(i3);
                                if (item2 instanceof SavedItem) {
                                    changed.add(item2.copy());
                                }
                            }
                        }
                        MainService.instance.changeCheckList(0);
                        boolean copy = this.makeCopy.isChecked();
                        SavedListAdapter savedList = MainService.instance.savedList;
                        if (!copy) {
                            savedList.remove(changed);
                        }
                        int type = this.typeSpinner.getSelected();
                        Iterator<SavedItem> it = changed.iterator();
                        while (it.hasNext()) {
                            SavedItem curItem = it.next().copy();
                            if (copy) {
                                curItem.name = String.valueOf(curItem.getName()) + " #2";
                            }
                            if (type != 127) {
                                curItem.flags = type;
                            }
                            curItem.address += offset;
                            if (!curItem.isPossibleItem()) {
                                curItem.flags = AddressItem.getTypeForAddress(curItem.address, true);
                            }
                            savedList.add(curItem, (byte) 1, false);
                        }
                        savedList.notifyDataSetChanged();
                        savedList.reloadData();
                        Record record = MainService.instance.currentRecord;
                        if (record != null) {
                            record.write("\nlocal copy = ");
                            record.write(copy ? "true" : "false");
                            record.write(ListManager.NEW_LINE);
                            record.write("local t = gg.getListItems()\n");
                            record.write("if not copy then gg.removeListItems(t) end\n");
                            record.write("for i, v in ipairs(t) do\n");
                            record.write("\tv.address = v.address + ");
                            if (this.address != null || this.hex.isChecked()) {
                                Script.Consts.logHex(record, offset);
                            } else {
                                record.write(Long.toString(offset));
                            }
                            record.write(ListManager.NEW_LINE);
                            record.write("\tif copy then v.name = v.name..' #2' end\n");
                            record.write("end\n");
                            record.write("gg.addListItems(t)\n");
                            record.write("t = nil\n");
                            record.write("copy = nil\n\n");
                        }
                    }
                    Tools.dismiss(this.dialog);
                } catch (NumberFormatException e) {
                    SearchMenuItem.inputError(e, this.edit);
                }
            }
        }

        private long getOffset(boolean withHistory) {
            String str = SearchMenuItem.checkScript(this.edit.getText().toString().trim());
            if (str.length() == 0) {
                str = "0";
            }
            boolean hex = this.address != null || this.hex.isChecked();
            long offset = ParserNumbers.parseBigLong(str, hex ? 16 : 10);
            if (this.address != null) {
                offset -= ParserNumbers.parseBigLong(this.address, 16);
            }
            if (withHistory) {
                History.add(str, 1);
            }
            return offset;
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            EditText edit;
            if (this.address == null && (edit = this.edit) != null) {
                lastInput = edit.getText().toString().trim();
                useHex = this.hex.isChecked();
            }
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkWarn();
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
            checkWarn();
        }

        private void checkWarn() {
            EditText edit = this.edit;
            if (edit != null) {
                int type = this.typeSpinner.getSelected();
                int align = 1;
                int fix = 0;
                try {
                    long offset = getOffset(false);
                    if (type == 127) {
                        int[] alignCount = this.alignCount;
                        align = 1;
                        for (int i = 0; i < alignCount.length; i++) {
                            if (alignCount[i] != 0 && (((long) ((1 << i) - 1)) & offset) != 0) {
                                align = 1 << i;
                                fix += alignCount[i];
                            }
                        }
                    } else {
                        int[] modCount = this.modCount;
                        align = AddressItem.getAlign(type);
                        for (int i2 = 0; i2 < modCount.length; i2++) {
                            if (modCount[i2] != 0 && ((((long) i2) + offset) & ((long) (align - 1))) != 0) {
                                fix += modCount[i2];
                            }
                        }
                    }
                } catch (Throwable th) {
                }
                this.alignWarn.setText(Tools.stringFormat(Re.s(type == 127 ? R.string.align_warn_offset : R.string.align_warn_result), Integer.valueOf(align), Integer.valueOf(fix)));
                this.alignWarn.setVisibility(fix > 0 ? 0 : 4);
            }
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            checkWarn();
        }
    }
}
