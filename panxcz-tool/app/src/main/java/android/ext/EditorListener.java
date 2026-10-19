package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.ext.AddressItem;
import android.ext.ParserNumbers;
import android.ext.Script;
import android.ext.SearchButtonListener;
import android.fix.SparseArray;
import android.sup.ArrayListResults;
import android.sup.LongSparseArrayChecked;
import android.text.SpannableString;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.AdapterView;
import catch_.me_.if_.you_.can_.R;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class EditorListener extends MenuItem implements AdapterView.OnItemClickListener {
    private final boolean inMemoryEditor;

    public EditorListener() {
        this(false);
    }

    public EditorListener(boolean inMemoryEditor) {
        super(R.string.edit_selected, R.drawable.ic_tooltip_edit_white_24dp);
        this.inMemoryEditor = inMemoryEditor;
    }

    @Override // android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        new Impl(this, null).onClick(v);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        new Impl(this, null).onItemClick(parent, view, position, id);
    }

    private class Impl implements View.OnClickListener, AdapterView.OnItemClickListener, DialogInterface.OnClickListener, DialogInterface.OnShowListener, DialogInterface.OnDismissListener {
        private List<AddressItem> checked;
        private AlertDialog dialog;
        private boolean editAll;
        int editFlags;
        private Editor editor;
        int memoryFlags;
        private int tab;
        private boolean updateSave;

        private Impl() {
            this.updateSave = false;
            this.tab = 0;
            this.checked = null;
            this.memoryFlags = 0;
            this.editFlags = 0;
        }

        /* synthetic */ Impl(EditorListener editorListener, Impl impl) {
            this();
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            Tools.setButtonListener(dialog, -1, null, this, this.editor.getNumberEdit());
        }

        void startEdit(AddressItem item) {
            this.checked = null;
            this.memoryFlags = 0;
            this.editFlags = 0;
            this.tab = 0;
            internalEdit(item);
        }

        void internalEdit(AddressItem item) {
            int i;
            String strStringFormat;
            this.editAll = false;
            this.updateSave = false;
            int total = 0;
            if (item == null) {
                this.editAll = true;
                Object obj = MainService.instance.getCheckList();
                int count = Tools.getSelectedCount(obj);
                if (count == 0) {
                    Tools.showToast(R.string.nothing_selected);
                    return;
                }
                if (this.checked == null) {
                    this.checked = new ArrayList();
                    if (obj instanceof boolean[]) {
                        this.tab = 3;
                        if (this.memoryFlags == 0) {
                            this.checked = null;
                            SparseIntArray counts = new SparseIntArray();
                            new TypeSelector(AddressItem.getDataForEditAll(MainService.instance.mMemListAdapter.getPossibleTypes((boolean[]) obj, counts)), counts, "0", Re.s(R.string.type_change_request), new DialogInterface.OnClickListener() { // from class: android.ext.EditorListener.Impl.1
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog, int flags) {
                                    Impl.this.memoryFlags = flags;
                                    Impl.this.internalEdit(null);
                                }
                            });
                            return;
                        }
                        MemoryContentAdapter memAdapter = MainService.instance.mMemListAdapter;
                        boolean[] list = (boolean[]) obj;
                        int s = list.length - 1;
                        for (int j = 1; j < s; j++) {
                            if (list[j]) {
                                AddressItem curItem = new AddressItem(memAdapter.getAddr(j), 0L, this.memoryFlags);
                                if (curItem.isPossibleItem()) {
                                    curItem.data = MainService.instance.getContentInAddress(curItem.address, curItem.flags);
                                    this.checked.add(curItem);
                                }
                            }
                        }
                        this.memoryFlags = 0;
                    } else if (obj instanceof LongSparseArrayChecked) {
                        this.tab = 2;
                        LongSparseArrayChecked<SavedItem> list2 = (LongSparseArrayChecked) obj;
                        int s2 = list2.size();
                        for (int j2 = 0; j2 < s2; j2++) {
                            if (list2.checkAt(j2)) {
                                this.checked.add(list2.valueAt(j2));
                            }
                        }
                    } else if (obj instanceof ArrayListResults) {
                        this.tab = 1;
                        ArrayListResults list3 = (ArrayListResults) obj;
                        int s3 = list3.size();
                        for (int i2 = 0; i2 < s3; i2++) {
                            if (list3.checked(i2)) {
                                this.checked.add(list3.get(i2, null));
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("Obj is unknown: " + obj);
                    }
                }
                if (this.checked != null) {
                    if (this.checked.size() == 0) {
                        Tools.showToast(R.string.nothing_selected);
                        Log.d("It must never happen.");
                        return;
                    }
                    if (this.editFlags == 0) {
                        int curFlags = 0;
                        SparseIntArray counts2 = new SparseIntArray();
                        for (AddressItem curItem2 : this.checked) {
                            int flags = curItem2.flags;
                            curFlags |= flags;
                            counts2.put(flags, counts2.get(flags) + 1);
                        }
                        new TypeSelector(AddressItem.getDataForEditAll(curFlags), counts2, "0", Re.s(R.string.type_change_request), new DialogInterface.OnClickListener() { // from class: android.ext.EditorListener.Impl.2
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int flags2) {
                                Impl.this.editFlags = flags2;
                                Impl.this.internalEdit(null);
                            }
                        });
                        return;
                    }
                    int j3 = 0;
                    while (j3 < this.checked.size()) {
                        AddressItem curItem3 = this.checked.get(j3);
                        if (curItem3.flags != this.editFlags) {
                            this.checked.remove(j3);
                            j3--;
                        }
                        j3++;
                    }
                    this.editFlags = 0;
                    if (this.checked.size() == 0) {
                        Tools.showToast(R.string.no_items_for_editing);
                        Log.d("It must never happen. " + this.editFlags);
                        return;
                    } else {
                        item = this.checked.get(0).copy();
                        total = this.checked.size();
                        if (total == 1) {
                            this.editAll = false;
                        }
                    }
                }
                if (item == null) {
                    throw new NullPointerException("Something going wrong");
                }
            }
            if (!this.editAll) {
                SavedItem savedItem = MainService.instance.savedList.getItemByAddress(item.address);
                if (savedItem != null && savedItem.getType() == item.getType()) {
                    SavedItem savedItem2 = savedItem.copy();
                    savedItem2.data = item.data;
                    item = savedItem2;
                    this.updateSave = true;
                } else {
                    item = new SavedItem(item);
                }
            }
            if (this.editAll) {
                item.flags = item.getType();
            }
            if (this.editAll) {
                i = 2;
            } else {
                i = this.updateSave ? 0 : 1;
            }
            this.editor = new Editor(i, item);
            if (this.editAll) {
                String input = SearchButtonListener.lastTextSearch;
                if (input == null || this.tab != 1) {
                    input = item.getStringDataTrim();
                }
                this.editor.setNumber(input);
                this.editor.setFillListener(this);
            } else if (this.updateSave) {
                this.editor.inSavedList();
            }
            CharSequence nameShort = item.getNameShort();
            Editor editor = this.editor;
            if (this.editAll) {
                strStringFormat = Tools.stringFormat(Re.s(R.string.edit_all_info), Integer.valueOf(total), nameShort);
            } else {
                strStringFormat = Tools.stringFormat(Re.s(R.string.edit_info), item.getStringAddress(), nameShort);
            }
            editor.setMessage(Tools.colorizeText(new SpannableString(strStringFormat), nameShort, item.getColor()));
            AlertDialog dialog = Alert.create().setView(this.editor.getViewForAttach()).setPositiveButton(Re.s(R.string.yes), this).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null).create();
            this.dialog = dialog;
            if (!this.editAll) {
                dialog.setButton(-3, Re.s(R.string.goto1), this);
            }
            Alert.setOnShowListener(dialog, this);
            Alert.setOnDismissListener(dialog, this);
            Alert.show(dialog, this.editor.getNumberEdit());
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            if (which != -1 && which != -2 && which == -3 && !this.editAll) {
                AddressItem item = this.editor.getItem();
                MainService.instance.goToAddress(!EditorListener.this.inMemoryEditor ? null : Long.valueOf(item.address), item.getStringAddress(), String.valueOf(Tools.stripColon(R.string.from_item)) + ": (" + item + ')');
            }
        }

        private boolean fill(View v) {
            StringBuilder text = null;
            switch (v.getId()) {
                case R.id.fill_values /* 2131427577 */:
                    text = new StringBuilder();
                    int is = this.checked.size();
                    for (int i = 0; i < is; i++) {
                        AddressItem item = this.checked.get(i);
                        if (i != 0) {
                            text.append(';');
                        }
                        text.append(item.getStringDataTrim());
                    }
                    break;
                case R.id.fill_utf8 /* 2131427578 */:
                case R.id.fill_utf16le /* 2131427579 */:
                    text = new StringBuilder();
                    boolean utf8 = v.getId() == R.id.fill_utf8;
                    text.append(utf8 ? ':' : ';');
                    if (utf8 || this.editor.getItem().flags == 1) {
                        long lastAddr = 0;
                        int size = this.checked.size();
                        byte[] bytes = new byte[size];
                        int i2 = 0;
                        while (i2 < size) {
                            AddressItem item2 = this.checked.get(i2);
                            if (lastAddr != 0 && 1 + lastAddr != item2.address) {
                                size = i2;
                                break;
                            }
                            lastAddr = item2.address;
                            bytes[i2] = (byte) item2.data;
                            i2++;
                        }
                        text.append(new String(bytes, 0, size, ParserNumbers.getCharset(!utf8)));
                    } else if (this.editor.getItem().flags == 2) {
                        long lastAddr2 = 0;
                        int size2 = this.checked.size();
                        char[] chars = new char[size2];
                        int i3 = 0;
                        while (i3 < size2) {
                            AddressItem item3 = this.checked.get(i3);
                            if (lastAddr2 != 0 && 2 + lastAddr2 != item3.address) {
                                size2 = i3;
                                break;
                            }
                            lastAddr2 = item3.address;
                            chars[i3] = (char) item3.data;
                            i3++;
                        }
                        text.append(new String(chars, 0, size2));
                    } else if (this.editor.getItem().flags == 4) {
                        long lastAddr3 = 0;
                        int size3 = this.checked.size();
                        char[] chars2 = new char[size3 * 2];
                        int i4 = 0;
                        while (i4 < size3) {
                            AddressItem item4 = this.checked.get(i4);
                            if (lastAddr3 != 0 && 4 + lastAddr3 != item4.address) {
                                size3 = i4;
                                break;
                            }
                            lastAddr3 = item4.address;
                            chars2[i4 * 2] = (char) item4.data;
                            chars2[(i4 * 2) + 1] = (char) (item4.data >> 16);
                            i4++;
                        }
                        text.append(new String(chars2, 0, size3 * 2));
                    }
                    break;
                case R.id.fill_hex /* 2131427580 */:
                    text = new StringBuilder();
                    text.append(SearchMenuItem.HEX);
                    long lastAddr4 = 0;
                    int size4 = this.checked.size();
                    char[] hexArr = HexText.hexArray;
                    for (int i5 = 0; i5 < size4; i5++) {
                        AddressItem item5 = this.checked.get(i5);
                        if (lastAddr4 == 0 || 1 + lastAddr4 == item5.address) {
                            lastAddr4 = item5.address;
                            text.append(' ');
                            int data = (int) item5.data;
                            text.append(hexArr[(data & 240) >> 4]);
                            text.append(hexArr[data & 15]);
                        } else {
                            break;
                        }
                    }
                    break;
                case R.id.fill_hex_utf8 /* 2131427581 */:
                case R.id.fill_hex_utf16le /* 2131427582 */:
                case R.id.fill_hex_utf8_utf16le /* 2131427583 */:
                    text = new StringBuilder();
                    text.append(SearchMenuItem.HEX_UTF);
                    text.append(' ');
                    int id = v.getId();
                    long lastAddr5 = 0;
                    int size5 = this.checked.size();
                    byte[] bytes2 = new byte[size5];
                    int i6 = 0;
                    while (i6 < size5) {
                        AddressItem item6 = this.checked.get(i6);
                        if (lastAddr5 != 0 && 1 + lastAddr5 != item6.address) {
                            size5 = i6;
                            break;
                        }
                        lastAddr5 = item6.address;
                        bytes2[i6] = (byte) item6.data;
                        i6++;
                    }
                    HexText.getText(text, 0, bytes2, size5, id == R.id.fill_hex_utf8 || id == R.id.fill_hex_utf8_utf16le, id == R.id.fill_hex_utf16le || id == R.id.fill_hex_utf8_utf16le, null);
                    break;
            }
            if (text != null) {
                this.editor.setNumber(text.toString());
            }
            return text != null;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            long j;
            long j2;
            long j3;
            if (v == null || (v.getTag() instanceof MenuItem)) {
                startEdit(null);
                return;
            }
            try {
                if (this.editAll) {
                    if (!fill(v)) {
                        try {
                            String input = SearchMenuItem.checkScript(this.editor.getNumber());
                            SearchButtonListener.XorMode xorMode = SearchButtonListener.parseXorMode(input, false);
                            int x = xorMode != null ? xorMode.x : 0;
                            String data = xorMode != null ? xorMode.input : input;
                            boolean needSave = this.editor.isNeedSave();
                            String step = this.editor.getEditStep();
                            String step2 = SearchMenuItem.checkScript(step);
                            int j4 = 0;
                            int size = this.checked.size();
                            boolean valueChange = this.editor.isValueChange();
                            String[] parts = null;
                            byte[] bytes = null;
                            char[] chars = null;
                            int flags = this.editor.getItem().flags;
                            if (flags == 1 && ParserNumbers.isString(data)) {
                                bytes = ParserNumbers.getBytes(data);
                            } else if ((flags & 6) != 0 && ParserNumbers.isString(data) && data.charAt(0) == ';') {
                                chars = data.substring(1).toCharArray();
                            } else if (data.indexOf(59) != -1) {
                                parts = data.split(";");
                                if (parts.length == 0) {
                                    parts = null;
                                }
                            }
                            SavedListAdapter savedList = MainService.instance.savedList;
                            AddressItem.Steps steps = null;
                            ParserNumbers.Result result = null;
                            ParserNumbers.Result[] results = parts == null ? null : new ParserNumbers.Result[parts.length];
                            int n = 0;
                            long lastAddr = 0;
                            for (int i = 0; i < size; i++) {
                                AddressItem item = this.checked.get(i);
                                SavedItem saved = MainService.instance.savedList.getItemByAddress(item.address);
                                boolean updateSave = saved != null && saved.getType() == item.getType();
                                SavedItem savedItem = this.editor.getSavedItem(item);
                                if (valueChange) {
                                    if (bytes != null) {
                                        int n2 = 1 + lastAddr != item.address ? 0 : n;
                                        lastAddr = item.address;
                                        if (n2 < bytes.length) {
                                            n = n2 + 1;
                                            j3 = 255 & ((long) bytes[n2]);
                                        } else {
                                            j3 = 0;
                                            n = n2;
                                        }
                                        savedItem.data = j3;
                                    } else if (chars != null) {
                                        boolean word = item.flags == 2;
                                        int n3 = ((long) (word ? 2 : 4)) + lastAddr != item.address ? 0 : n;
                                        lastAddr = item.address;
                                        if (n3 < chars.length) {
                                            j = 65535 & ((long) chars[n3]);
                                            n3++;
                                        } else {
                                            j = 0;
                                        }
                                        savedItem.data = j;
                                        if (word) {
                                            n = n3;
                                        } else {
                                            long j5 = savedItem.data;
                                            if (n3 < chars.length) {
                                                n = n3 + 1;
                                                j2 = (65535 & ((long) chars[n3])) << 16;
                                            } else {
                                                j2 = 0;
                                                n = n3;
                                            }
                                            savedItem.data = j2 | j5;
                                        }
                                    } else {
                                        if (parts == null) {
                                            if (result == null) {
                                                result = AddressItem.parseString(null, data, savedItem.flags, R.string.number_name, item.address);
                                            }
                                        } else {
                                            n = i % parts.length;
                                            data = parts[n];
                                            result = results[n];
                                            if (result == null) {
                                                result = AddressItem.parseString(null, data, savedItem.flags, R.string.number_name, item.address);
                                                results[n] = result;
                                            }
                                        }
                                        j4++;
                                        steps = savedItem.dataFromString(steps, result, data, item.address, step2, j4);
                                    }
                                    savedItem.alter(x);
                                }
                                if (needSave || updateSave) {
                                    savedList.add(savedItem, (byte) 0, false);
                                }
                            }
                            savedList.notifyDataSetChanged();
                            MainService.instance.onMemoryChanged();
                            Tools.dismiss(this.dialog);
                            if (SearchButtonListener.lastTextSearch != null && this.tab == 1 && (bytes != null || chars != null)) {
                                SearchButtonListener.lastTextSearch = input;
                            }
                            record(false, needSave, input);
                            return;
                        } catch (NumberFormatException e) {
                            SearchMenuItem.inputError(e, this.editor.getNumberEdit());
                            return;
                        } catch (Throwable e2) {
                            Log.e("Exception in EditorListener", e2);
                            return;
                        }
                    }
                    return;
                }
                String data2 = SearchMenuItem.checkScript(this.editor.getNumber());
                SearchButtonListener.XorMode xorMode2 = SearchButtonListener.parseXorMode(data2, false);
                int x2 = xorMode2 != null ? xorMode2.x : 0;
                if (xorMode2 != null) {
                    data2 = xorMode2.input;
                }
                SavedItem savedItem2 = this.editor.getSavedItem();
                savedItem2.setDataFromString(data2);
                savedItem2.alter(x2);
                boolean needSave2 = this.editor.isNeedSave();
                if (needSave2 || this.updateSave) {
                    MainService.instance.savedList.add(savedItem2);
                }
                Tools.dismiss(this.dialog);
                record(true, needSave2, data2);
                MainService.instance.onMemoryChanged();
            } catch (NumberFormatException e3) {
                SearchMenuItem.inputError(e3, this.editor.getNumberEdit());
            } catch (Throwable e4) {
                Log.e("Exception in EditorListener", e4);
            }
        }

        private void record(boolean single, boolean needSave, String input) {
            Record record;
            String note;
            if (single) {
                int index = MainService.instance.getTabIndex();
                if (index == 1 && MainService.instance.mResultCount == 1) {
                    this.tab = 1;
                } else if (index == 2 && MainService.instance.savedList.getCount() == 1) {
                    this.tab = 2;
                } else {
                    return;
                }
            }
            if ((this.tab == 1 || this.tab == 2) && (record = MainService.instance.currentRecord) != null) {
                SavedItem item = this.editor.getSavedItem();
                if (this.tab == 2 || needSave) {
                    record.write("\nrevert = ");
                    if (this.tab == 1) {
                        Converter.recordGetResults(record, true);
                    } else {
                        record.write("gg.getListItems()\n");
                    }
                    record.write("local t = ");
                    if (this.tab == 1) {
                        Converter.recordGetResults(record, true);
                    } else {
                        record.write("gg.getListItems()\n");
                    }
                    record.write("for i, v in ipairs(t) do\n");
                    record.write("\tif v.flags == ");
                    Script.Consts.logConst(record, record.consts.TYPE_, item.flags);
                    record.write(" then\n");
                    record.write("\t\tv.value = ");
                    if ((item.flags & AddressItem.FLAG_ALTER_ADD) == 1073741824) {
                        item.flags &= -1073741825;
                        record.write("v.value + ");
                    }
                    Script.Consts.logNumberString(record, input);
                    record.write(ListManager.NEW_LINE);
                    if (this.tab == 2 || item.freeze) {
                        record.write("\t\tv.freeze = ");
                        record.write(Boolean.toString(item.freeze));
                        record.write(ListManager.NEW_LINE);
                        if (this.tab == 2 || item.freezeType != 0) {
                            switch (item.freezeType) {
                                case 0:
                                    note = "gg.FREEZE_NORMAL";
                                    break;
                                case 1:
                                    note = "gg.FREEZE_MAY_INCREASE";
                                    break;
                                case 2:
                                    note = "gg.FREEZE_MAY_DECREASE";
                                    break;
                                case 3:
                                    note = "gg.FREEZE_IN_RANGE";
                                    break;
                                default:
                                    note = Integer.toString(item.freezeType);
                                    break;
                            }
                            record.write("\t\tv.freezeType = ");
                            record.write(note);
                            record.write(ListManager.NEW_LINE);
                            if (item.freezeType == 3) {
                                record.write("\t\tv.freezeFrom = ");
                                Script.Consts.logNumberString(record, item.getRangeString(true));
                                record.write(ListManager.NEW_LINE);
                                record.write("\t\tv.freezeTo = ");
                                Script.Consts.logNumberString(record, item.getRangeString(false));
                                record.write(ListManager.NEW_LINE);
                            }
                        }
                    }
                    record.write("\tend\n");
                    record.write("end\n");
                    record.write("gg.addListItems(t)\n");
                    record.write("t = nil\n\n");
                    return;
                }
                record.write("revert = ");
                Converter.recordGetResults(record, true);
                if ((item.flags & AddressItem.FLAG_ALTER_ADD) == 1073741824) {
                    item.flags &= -1073741825;
                    record.write("local t = ");
                    Converter.recordGetResults(record, true);
                    record.write("for i, v in ipairs(t) do\n");
                    record.write("\tif v.flags == ");
                    Script.Consts.logConst(record, record.consts.TYPE_, item.flags);
                    record.write(" then\n");
                    record.write("\t\tv.value = ");
                    record.write("v.value + ");
                    Script.Consts.logNumberString(record, input);
                    record.write(ListManager.NEW_LINE);
                    record.write("\tend\n");
                    record.write("end\n");
                    record.write("gg.setValues(t)\n");
                    record.write("t = nil\n\n");
                    return;
                }
                record.write("gg.editAll(");
                Script.Consts.logNumberString(record, input);
                record.write(", ");
                Script.Consts.logConst(record, record.consts.TYPE_, item.flags);
                record.write(")\n");
            }
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AddressItem item = null;
            Object hold = parent.getItemAtPosition(position);
            if (hold instanceof GotoAddress) {
                MainService.instance.goToAddress(Long.valueOf(((GotoAddress) hold).getSource()), ((GotoAddress) hold).getAddress(), Tools.stripColon(R.string.from_boundary));
                return;
            }
            if (hold instanceof AddressItem) {
                item = (AddressItem) hold;
            }
            if (item != null) {
                final AddressItem itemFinal = item;
                if (parent.getId() == R.id.mem_listview && ((Config.configClient & 1024) == 0 || Integer.bitCount(itemFinal.flags) != 1)) {
                    SparseArray<CharSequence> list = AddressItem.getDataForEditAll(AddressItem.FLAG_AUTO, item.address);
                    new TypeSelector(list, "0", Re.s(R.string.type_change_request), new DialogInterface.OnClickListener() { // from class: android.ext.EditorListener.Impl.3
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            itemFinal.flags = which;
                            Impl.this.startEdit(itemFinal);
                        }
                    });
                } else {
                    startEdit(item);
                }
            }
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            try {
                this.editor.getViewForAttachSimple();
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
    }
}
