package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.fix.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class RecordScript extends MenuItem {
    private static final String RECORD_PATH = "record-path";

    public RecordScript() {
        super(R.string.record_script, R.drawable.ic_record_white_24dp);
    }

    @Override // android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        new Impl(this, null).onClick(v);
    }

    private class Impl implements DialogInterface.OnClickListener, DialogInterface.OnShowListener, DialogInterface.OnDismissListener, View.OnClickListener {
        private AlertDialog dialog;
        private android.widget.EditText edit;

        private Impl() {
        }

        /* synthetic */ Impl(RecordScript recordScript, Impl impl) {
            this();
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            Tools.setButtonListener(dialog, -1, null, this);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface d, final int which) {
            android.widget.EditText edit = this.edit;
            if (edit != null) {
                String file = edit.getText().toString().trim();
                if (Tools.isPath(file)) {
                    if (MainService.instance.currentRecord != null) {
                        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.error)).setMessage(Re.s(R.string.another_record)).setPositiveButton(Re.s(R.string.stop), new DialogInterface.OnClickListener() { // from class: android.ext.RecordScript.Impl.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface d2, int w) {
                                MainService.instance.interruptRecord();
                                Impl.this.onClick(null, which);
                            }
                        }).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null));
                        return;
                    }
                    try {
                        Record record = new Record(file);
                        History.add(file, 4);
                        MainService.instance.startRecord(record);
                        Tools.dismiss(this.dialog);
                    } catch (Throwable e) {
                        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.error)).setMessage(String.valueOf(file) + "\n\n" + e.toString()).setPositiveButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                    }
                }
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == null || (view.getTag() instanceof MenuItem)) {
                if (MainService.instance.mDaemonManager.isDaemonRun()) {
                    View v = LayoutInflater.inflateStatic(R.layout.save, (ViewGroup) null);
                    TextView message = (TextView) v.findViewById(R.id.message);
                    message.setText(Re.s(R.string.record_script));
                    EditTextPath edit = (EditTextPath) v.findViewById(R.id.file);
                    this.edit = edit;
                    edit.setText(PkgPath.load(RecordScript.RECORD_PATH, "-record", ".lua"));
                    edit.setDataType(4);
                    edit.setPathType(2);
                    v.findViewById(R.id.path_selector).setTag(edit);
                    AlertDialog.Builder builder = Alert.create().setView(InternalKeyboard.getView(v, false)).setPositiveButton(Re.s(R.string.record), this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null);
                    AlertDialog dialog = builder.create();
                    Alert.setOnShowListener(dialog, this);
                    Alert.setOnDismissListener(dialog, this);
                    this.dialog = dialog;
                    Alert.show(dialog, edit);
                    return;
                }
                return;
            }
            onClick(null, -1);
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            android.widget.EditText edit = this.edit;
            if (edit != null) {
                PkgPath.save(edit.getText().toString(), RecordScript.RECORD_PATH, "-record", ".lua");
            }
        }
    }
}
