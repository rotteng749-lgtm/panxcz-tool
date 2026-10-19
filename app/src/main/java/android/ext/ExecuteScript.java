package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.ext.BaseActivity;
import android.ext.Script;
import android.fix.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class ExecuteScript extends MenuItem {
    public static final int DISASSEMBLE = 1;
    public static final int LOAD = 2;
    public static final int LOG = 4;
    private static final String SCRIPT_DEBUG = "script-debug";
    private static final String SCRIPT_PATH = "script-path";
    static boolean debug = false;
    static boolean disassemble = true;
    static boolean load = true;
    static boolean log = true;

    public ExecuteScript() {
        super(R.string.execute_script, R.drawable.ic_play_white_24dp);
    }

    @Override // android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        new Impl(this, null).onClick(v);
    }

    private class Impl implements DialogInterface.OnClickListener, DialogInterface.OnShowListener, DialogInterface.OnDismissListener, View.OnClickListener, View.OnLongClickListener {
        private AlertDialog dialog;
        private CheckBox disassemble;
        private android.widget.EditText edit;
        private Button ext;
        private CheckBox load;
        private CheckBox log;
        private android.widget.EditText path;
        private View stuff;

        private Impl() {
        }

        /* synthetic */ Impl(ExecuteScript executeScript, Impl impl) {
            this();
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            Tools.setButtonListener(dialog, -1, null, this);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface d, final int which) {
            ExecuteScript.disassemble = this.disassemble.isChecked();
            ExecuteScript.load = this.load.isChecked();
            ExecuteScript.log = this.log.isChecked();
            String out = this.path.getText().toString().trim();
            int params = (ExecuteScript.disassemble ? 1 : 0) | (ExecuteScript.load ? 2 : 0) | (ExecuteScript.log ? 4 : 0);
            if (ExecuteScript.debug) {
                if (Tools.isPath(out)) {
                } else {
                    return;
                }
            }
            History.add(out, 4);
            android.widget.EditText edit = this.edit;
            if (edit != null) {
                String file = edit.getText().toString().trim();
                boolean ok = false;
                try {
                    ok = Tools.isPath(file) && Tools.isFile(file);
                } catch (Throwable e) {
                    Log.badImplementation(e);
                }
                if (ok) {
                    History.add(file, 4);
                    if (MainService.instance.currentScript != null) {
                        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.error)).setMessage(Re.s(R.string.another_script)).setPositiveButton(Re.s(R.string.interrupt), new DialogInterface.OnClickListener() { // from class: android.ext.ExecuteScript.Impl.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface d2, int w) {
                                MainService.instance.interruptScript();
                                Impl.this.onClick(null, which);
                            }
                        }).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null));
                        return;
                    }
                    MainService.instance.executeScript(file, params, out);
                    Tools.dismiss(this.dialog);
                    Record record = MainService.instance.currentRecord;
                    if (record != null) {
                        record.write("loadfile(");
                        Script.Consts.logString(record, file);
                        record.write(")()\n");
                    }
                }
            }
        }

        private void update() {
            boolean debug = ExecuteScript.debug;
            Button ext = this.ext;
            ext.setText(Re.s(debug ? R.string.less : R.string.more));
            this.stuff.setVisibility(debug ? 0 : 8);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == null || (view.getTag() instanceof MenuItem)) {
                if (MainService.instance.mDaemonManager.isDaemonRun()) {
                    View v = LayoutInflater.inflateStatic(R.layout.execute, (ViewGroup) null);
                    TextView message = (TextView) v.findViewById(R.id.message);
                    message.setText(Re.s(R.string.execute_script));
                    EditTextPath edit = (EditTextPath) v.findViewById(R.id.file);
                    this.edit = edit;
                    edit.setText(PkgPath.load(ExecuteScript.SCRIPT_PATH, "-script", ".lua"));
                    edit.setDataType(4);
                    edit.setPathType(1);
                    v.findViewById(R.id.file_selector).setTag(edit);
                    Config.setIconSize((ImageView) v.findViewById(R.id.examples)).setOnClickListener(this);
                    v.findViewById(R.id.download_scripts).setOnClickListener(new BaseActivity.GoOnForum(BaseActivity.GoOnForum.S6));
                    v.findViewById(R.id.help).setOnClickListener(new BaseActivity.GoOnForum(BaseActivity.GoOnForum.S7));
                    v.findViewById(R.id.help_extract).setOnClickListener(this);
                    this.stuff = v.findViewById(R.id.debug);
                    EditTextPath path = (EditTextPath) v.findViewById(R.id.path);
                    this.path = path;
                    path.setText(PkgPath.path(null, ExecuteScript.SCRIPT_DEBUG));
                    path.setDataType(4);
                    path.setPathType(0);
                    v.findViewById(R.id.path_selector).setTag(path);
                    CheckBox disassemble = (CheckBox) v.findViewById(R.id.disassemble);
                    this.disassemble = disassemble;
                    disassemble.setChecked(ExecuteScript.disassemble);
                    Tools.setButtonHelpBackground(disassemble);
                    disassemble.setOnLongClickListener(this);
                    CheckBox load = (CheckBox) v.findViewById(R.id.load);
                    this.load = load;
                    load.setChecked(ExecuteScript.load);
                    CheckBox log = (CheckBox) v.findViewById(R.id.log);
                    this.log = log;
                    log.setChecked(ExecuteScript.log);
                    Button ext = (Button) v.findViewById(R.id.ext_btn);
                    this.ext = ext;
                    ext.setOnClickListener(this);
                    update();
                    AlertDialog.Builder builder = Alert.create().setView(InternalKeyboard.getView(v, false)).setPositiveButton(Re.s(R.string.execute), this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null);
                    AlertDialog dialog = builder.create();
                    Alert.setOnShowListener(dialog, this);
                    Alert.setOnDismissListener(dialog, this);
                    this.dialog = dialog;
                    Alert.show(dialog, edit);
                    return;
                }
            }
            switch (view.getId()) {
                case R.id.examples /* 2131427345 */:
                    new Script("print('gg =', gg)", 0, "").start();
                    break;
                case R.id.help_extract /* 2131427348 */:
                    String err = null;
                    File output = new File(Tools.getSdcardPath(), "help.zip");
                    try {
                        Tools.extractFile("he", output);
                    } catch (RuntimeException e) {
                        err = e.getMessage();
                    }
                    Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.help_extract)).setMessage(err == null ? Tools.stringFormat(Re.s(R.string.help_extract_done), output.getAbsolutePath()) : String.valueOf(Tools.stripColon(R.string.help_extract_fail)) + ": " + err).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                    break;
                case R.id.ext_btn /* 2131427353 */:
                    ExecuteScript.debug = !ExecuteScript.debug;
                    update();
                    break;
                default:
                    onClick(null, -1);
                    break;
            }
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View v) {
            if (v == null) {
                return false;
            }
            switch (v.getId()) {
                case R.id.disassemble /* 2131427350 */:
                    ConfigListAdapter.showHelp(R.string.help_lasm);
                    return true;
                default:
                    return false;
            }
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            android.widget.EditText edit = this.edit;
            if (edit != null) {
                PkgPath.save(edit.getText().toString(), ExecuteScript.SCRIPT_PATH, "-script", ".lua");
            }
            android.widget.EditText path = this.path;
            if (path != null) {
                PkgPath.path(path.getText().toString().trim(), ExecuteScript.SCRIPT_DEBUG);
            }
        }
    }
}
