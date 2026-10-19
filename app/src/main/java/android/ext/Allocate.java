package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.ext.Script;
import android.fix.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class Allocate extends MenuItem {
    public static final int PROT_EXEC = 4;
    public static final int PROT_NONE = 0;
    public static final int PROT_READ = 2;
    public static final int PROT_SEM = 8;
    public static final int PROT_WRITE = 1;

    public Allocate() {
        super(R.string.allocate_page, R.drawable.ic_code_braces_white_24dp);
    }

    @Override // android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        new Impl(null).onClick(v);
    }

    public static void show(String page) {
        new Impl(null).show(page);
    }

    private static class Impl implements DialogInterface.OnClickListener, DialogInterface.OnShowListener, View.OnClickListener {
        private EditText addr;
        private AlertDialog dialog;
        private CheckBox exec;
        private CheckBox read;
        private CheckBox write;

        private Impl() {
            this.read = null;
            this.write = null;
            this.exec = null;
            this.addr = null;
            this.dialog = null;
        }

        /* synthetic */ Impl(Impl impl) {
            this();
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface d) {
            Tools.setButtonListener(this.dialog, -1, null, this);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface d, int which) {
            EditText addr = this.addr;
            if (addr != null) {
                try {
                    String data = SearchMenuItem.checkScript(addr.getText().toString().trim());
                    if (data.endsWith("h")) {
                        data = data.substring(0, data.length() - 1);
                    }
                    if (data.length() == 0) {
                        data = "0";
                    }
                    try {
                        long address = ParserNumbers.parseBigLong(data, 16);
                        if (address != 0) {
                            History.add(data, 1);
                        }
                        int mode = this.read.isChecked() ? 0 | 2 : 0;
                        if (this.write.isChecked()) {
                            mode |= 1;
                        }
                        if (this.exec.isChecked()) {
                            mode |= 4;
                        }
                        Log.d("Allocate: " + mode + ListManager.TEXT_SEPARATOR + address);
                        Tools.dismiss(this.dialog);
                        MainService.instance.mDaemonManager.allocatePage(address, mode);
                        Record record = MainService.instance.currentRecord;
                        if (record != null) {
                            record.write("gg.allocatePage(");
                            Script.Consts.logConst(record, record.consts.PROT_, mode);
                            record.write(", ");
                            Script.Consts.logHex(record, address);
                            record.write(")\n");
                        }
                    } catch (NumberFormatException e) {
                        addr.requestFocus();
                        throw e;
                    }
                } catch (NumberFormatException e2) {
                    SearchMenuItem.inputError(e2, addr);
                }
            }
        }

        public void show(String page) {
            View v = LayoutInflater.inflateStatic(R.layout.alloc, (ViewGroup) null);
            EditText addr = (EditText) v.findViewById(R.id.memory_from);
            this.addr = addr;
            v.findViewById(R.id.region_from).setTag(addr);
            if (page != null) {
                addr.setText(page);
            }
            addr.setDataType(1);
            this.read = (CheckBox) v.findViewById(R.id.read);
            this.write = (CheckBox) v.findViewById(R.id.write);
            this.exec = (CheckBox) v.findViewById(R.id.exec);
            AlertDialog dialog = Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.allocate_page)).setView(InternalKeyboard.getView(v)).setPositiveButton(Re.s(R.string.ok), this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).create();
            this.dialog = dialog;
            Alert.setOnShowListener(dialog, this);
            Alert.show(dialog, addr);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == null || (view.getTag() instanceof MenuItem)) {
                show(null);
            } else {
                onClick(null, -1);
            }
        }
    }
}
