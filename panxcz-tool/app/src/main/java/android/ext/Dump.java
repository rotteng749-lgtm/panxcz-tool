package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.ext.InternalKeyboard;
import android.ext.ProcessList;
import android.ext.Script;
import android.fix.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class Dump extends MenuItem {
    public Dump() {
        super(R.string.dump_memory, R.drawable.ic_folder_download_white_24dp);
    }

    @Override // android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        new Impl(null).onClick(v);
    }

    private static class Impl implements View.OnClickListener, DialogInterface.OnClickListener, DialogInterface.OnShowListener, DialogInterface.OnDismissListener {
        private static final String DUMP_PATH = "dump-path";
        private AlertDialog dialog;
        private android.widget.EditText edit;
        private long memFrom;
        private long memTo;
        private EditText memoryFrom;
        private EditText memoryTo;
        private CheckBox skipSystemLibs;

        private Impl() {
            this.memFrom = 0L;
            this.memTo = -1L;
        }

        /* synthetic */ Impl(Impl impl) {
            this();
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            Tools.setButtonListener(dialog, -1, null, this);
        }

        private String getDefaultPath() {
            return String.valueOf(Tools.getSdcardPath()) + "/dump";
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            android.widget.EditText edit = this.edit;
            if (edit != null) {
                new SPEditor().putString(DUMP_PATH, edit.getText().toString().trim(), getDefaultPath()).commit();
            }
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            android.widget.EditText edit = this.edit;
            if (edit != null) {
                try {
                    getMem(0);
                    getMem(1);
                    String path = Tools.trimDirPath(edit.getText().toString().trim());
                    if (Tools.isPath(path)) {
                        History.add(path, 4);
                        int flags = this.skipSystemLibs.isChecked() ? 0 | 1 : 0;
                        Log.d("Dump: " + path + ListManager.TEXT_SEPARATOR + Long.toHexString(this.memFrom) + '-' + Long.toHexString(this.memTo) + ListManager.TEXT_SEPARATOR + Integer.toHexString(flags));
                        Tools.dismiss(this.dialog);
                        ProcessList.ProcessInfo info = MainService.instance.processInfo;
                        String prefix = info == null ? "unknown" : info.packageName;
                        if (MainService.instance.showBusyDialog()) {
                            MainService.instance.mDaemonManager.doDump(this.memFrom, this.memTo, flags, path, prefix);
                            Record record = MainService.instance.currentRecord;
                            if (record != null) {
                                record.write("gg.dumpMemory(");
                                Script.Consts.logHex(record, this.memFrom);
                                record.write(", ");
                                Script.Consts.logHex(record, this.memTo);
                                record.write(", ");
                                Script.Consts.logString(record, path);
                                record.write(", ");
                                Script.Consts.logConst(record, record.consts.DUMP_, flags);
                                record.write(")\n");
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    SearchMenuItem.inputError(e, edit);
                }
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == null || (view.getTag() instanceof MenuItem)) {
                View v = LayoutInflater.inflateStatic(R.layout.dump, (ViewGroup) null);
                final EditTextPath edit = (EditTextPath) v.findViewById(R.id.path);
                this.edit = edit;
                edit.setText(Tools.getSharedPreferences().getString(DUMP_PATH, getDefaultPath()));
                edit.setDataType(4);
                edit.setPathType(0);
                v.findViewById(R.id.path_selector).setTag(edit);
                TypicalValues.setCacheDirs((Button) v.findViewById(R.id.typical_values), edit);
                EditText memoryFrom = (EditText) v.findViewById(R.id.memory_from);
                this.memoryFrom = memoryFrom;
                v.findViewById(R.id.region_from).setTag(memoryFrom);
                EditText memoryTo = (EditText) v.findViewById(R.id.memory_to);
                this.memoryTo = memoryTo;
                v.findViewById(R.id.region_to).setTag(memoryTo);
                memoryFrom.setText(AddressItem.getStringAddress(this.memFrom, 4));
                memoryFrom.setDataType(1);
                InternalKeyboard.setFlagsFor(memoryFrom, 1);
                memoryTo.setText(AddressItem.getStringAddress(this.memTo, 4));
                memoryTo.setDataType(1);
                this.skipSystemLibs = (CheckBox) v.findViewById(R.id.skip_system_libs);
                View.OnFocusChangeListener focus = new InternalKeyboard.FocusListener() { // from class: android.ext.Dump.Impl.1
                    @Override // android.ext.InternalKeyboard.FocusListener
                    protected boolean useExternal(View v2, boolean hasFocus) {
                        return v2 == edit;
                    }
                };
                for (View child : v.getFocusables(2)) {
                    if (child instanceof EditText) {
                        Tools.setOnFocusChangeListener(child, focus);
                    }
                }
                AlertDialog dialog = Alert.create().setView(InternalKeyboard.getView(v)).setPositiveButton(Re.s(R.string.save), this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).create();
                this.dialog = dialog;
                Alert.setOnShowListener(dialog, this);
                Alert.setOnDismissListener(dialog, this);
                Alert.show(dialog, edit);
                return;
            }
            onClick(null, -1);
        }

        private long getMem(int index) throws NumberFormatException {
            long val = MemoryRange.getMem(index, this.memoryFrom, "0", this.memoryTo, "-1");
            if (index == 0) {
                this.memFrom = val;
            } else {
                this.memTo = val;
            }
            return val;
        }
    }
}
