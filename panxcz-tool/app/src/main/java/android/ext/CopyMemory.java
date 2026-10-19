package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.ext.Script;
import android.fix.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class CopyMemory extends MenuItem {
    public CopyMemory() {
        super(R.string.copy_memory, R.drawable.ic_content_duplicate_white_24dp);
    }

    @Override // android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        new Impl(null).onClick(v);
    }

    private static class Impl implements View.OnClickListener, DialogInterface.OnClickListener, DialogInterface.OnShowListener {
        private int bytes;
        private AlertDialog dialog;
        private EditText edit;
        private long memFrom;
        private long memTo;
        private EditText memoryFrom;
        private EditText memoryTo;

        private Impl() {
            this.memFrom = 0L;
            this.memTo = 0L;
            this.bytes = 0;
        }

        /* synthetic */ Impl(Impl impl) {
            this();
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            Tools.setButtonListener(dialog, -1, null, this);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            EditText edit = this.edit;
            if (edit != null) {
                try {
                    getMem(0);
                    getMem(1);
                    String data = SearchMenuItem.checkScript(edit.getText().toString().trim());
                    if (data.length() == 0) {
                        data = "0";
                    }
                    try {
                        this.bytes = (int) ParserNumbers.parseLong(data).value;
                        if (this.bytes != 0) {
                            History.add(data, 1);
                        }
                        Log.d("Copy: " + this.bytes + ListManager.TEXT_SEPARATOR + Long.toHexString(this.memFrom) + '-' + Long.toHexString(this.memTo));
                        Tools.dismiss(this.dialog);
                        MainService.instance.mDaemonManager.copyMemory(this.memFrom, this.memTo, this.bytes);
                        Record record = MainService.instance.currentRecord;
                        if (record != null) {
                            record.write("gg.copyMemory(");
                            Script.Consts.logHex(record, this.memFrom);
                            record.write(", ");
                            Script.Consts.logHex(record, this.memTo);
                            record.write(", ");
                            record.write(Integer.toString(this.bytes));
                            record.write(")\n");
                        }
                    } catch (NumberFormatException e) {
                        edit.requestFocus();
                        throw e;
                    }
                } catch (NumberFormatException e2) {
                    SearchMenuItem.inputError(e2, edit);
                }
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == null || (view.getTag() instanceof MenuItem)) {
                View v = LayoutInflater.inflateStatic(R.layout.memory_copy, (ViewGroup) null);
                EditText edit = (EditText) v.findViewById(R.id.count_bytes);
                this.edit = edit;
                v.findViewById(R.id.bytes_converter).setTag(edit);
                edit.setText(DisplayNumbers.longToString(this.bytes, 4));
                edit.setDataType(1);
                EditText memoryFrom = (EditText) v.findViewById(R.id.memory_from);
                this.memoryFrom = memoryFrom;
                v.findViewById(R.id.region_from).setTag(memoryFrom);
                EditText memoryTo = (EditText) v.findViewById(R.id.memory_dst);
                this.memoryTo = memoryTo;
                v.findViewById(R.id.region_dst).setTag(memoryTo);
                memoryFrom.setText(AddressItem.getStringAddress(this.memFrom, 4));
                memoryFrom.setDataType(1);
                memoryTo.setText(AddressItem.getStringAddress(this.memTo, 4));
                memoryTo.setDataType(1);
                AlertDialog dialog = Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.copy_memory)).setView(InternalKeyboard.getView(v)).setPositiveButton(Re.s(R.string.ok), this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).create();
                this.dialog = dialog;
                Alert.setOnShowListener(dialog, this);
                Alert.show(dialog, edit);
                return;
            }
            onClick(null, -1);
        }

        private long getMem(int index) throws NumberFormatException {
            long val = MemoryRange.getMem(index, this.memoryFrom, "0", this.memoryTo, "0");
            if (index == 0) {
                this.memFrom = val;
            } else {
                this.memTo = val;
            }
            return val;
        }
    }
}
