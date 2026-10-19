package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.fix.LayoutInflater;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class Unrandomizer extends MenuItem {
    static volatile boolean lastQwordUse = false;
    static volatile long lastQword = 0;
    static volatile boolean lastQwordIncUse = false;
    static volatile long lastQwordInc = 1;
    static volatile boolean lastDoubleUse = false;
    static volatile double lastDouble = 0.0d;
    static volatile boolean lastDoubleIncUse = false;
    static volatile double lastDoubleInc = 0.01d;
    static volatile Unrandomizer lastInstance = null;

    public static void reset() {
        lastQwordUse = false;
        lastQwordIncUse = false;
        lastDoubleUse = false;
        lastDoubleIncUse = false;
    }

    public static void apply(int flags, long qBase, long qInc, double dBase, double dInc) {
        MainService.instance.mDaemonManager.unrand(flags, qBase, qInc, dBase, dInc);
    }

    public Unrandomizer() {
        super(R.string.unrandomizer, R.drawable.ic_dice_multiple_white_24dp);
        lastInstance = this;
    }

    @Override // android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        new Impl(this, null).onClick(v);
    }

    private class Impl implements View.OnClickListener, DialogInterface.OnClickListener, DialogInterface.OnShowListener {
        AlertDialog dialog;
        EditText doubleEdit;
        EditText doubleIncEdit;
        CheckBox doubleIncUse;
        CheckBox doubleUse;
        EditText qwordEdit;
        EditText qwordIncEdit;
        CheckBox qwordIncUse;
        CheckBox qwordUse;

        private Impl() {
        }

        /* synthetic */ Impl(Unrandomizer unrandomizer, Impl impl) {
            this();
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            Tools.setButtonListener(dialog, -1, null, this);
        }

        private long parse(EditText ed, boolean qword) {
            String val = SearchMenuItem.checkScript(ed.getText().toString().trim());
            long ret = qword ? ParserNumbers.parseLong(val).value : ParserNumbers.parseDouble(val).value;
            History.add(val, 1);
            return ret;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            int flags = 0;
            try {
                boolean qUse = this.qwordUse.isChecked();
                boolean qIncUse = false;
                long qBase = 0;
                long qInc = 0;
                if (qUse) {
                    flags = 0 | 2;
                    qBase = parse(this.qwordEdit, true);
                    Unrandomizer.lastQword = qBase;
                    qIncUse = this.qwordIncUse.isChecked();
                    if (qIncUse) {
                        qInc = parse(this.qwordIncEdit, true);
                        Unrandomizer.lastQwordInc = qInc;
                    }
                }
                Unrandomizer.lastQwordUse = qUse;
                Unrandomizer.lastQwordIncUse = qIncUse;
                boolean dUse = this.doubleUse.isChecked();
                boolean dIncUse = false;
                double dBase = 0.0d;
                double dInc = 0.0d;
                if (dUse) {
                    flags |= 4;
                    dBase = Double.longBitsToDouble(parse(this.doubleEdit, false));
                    Unrandomizer.lastDouble = dBase;
                    dIncUse = this.doubleIncUse.isChecked();
                    if (dIncUse) {
                        dInc = Double.longBitsToDouble(parse(this.doubleIncEdit, false));
                        Unrandomizer.lastDoubleInc = dInc;
                    }
                }
                Unrandomizer.lastDoubleUse = dUse;
                Unrandomizer.lastDoubleIncUse = dIncUse;
                Unrandomizer.apply(flags, qBase, qInc, dBase, dInc);
                Tools.dismiss(dialog);
                Record record = MainService.instance.currentRecord;
                if (record != null) {
                    record.write("gg.unrandomizer(");
                    if (qUse) {
                        record.write(Long.toString(qBase));
                        record.write(", ");
                        record.write(Long.toString(qInc));
                        record.write(", ");
                    } else {
                        record.write("nil, nil, ");
                    }
                    if (dUse) {
                        record.write(Double.toString(dBase));
                        record.write(", ");
                        record.write(Double.toString(dInc));
                    } else {
                        record.write("nil, nil");
                    }
                    record.write(")\n");
                }
            } catch (NumberFormatException e) {
                SearchMenuItem.inputError(e, null);
            }
        }

        private void initRow(final CheckBox prev, final CheckBox ch, boolean checked, EditText ed, String text) {
            ch.setChecked(checked);
            ch.setText(Re.s(ch.getText().toString()));
            ed.setDataType(1);
            ed.setText(text);
            ed.setTag(ch);
            ed.addTextChangedListener(new TextWatcher() { // from class: android.ext.Unrandomizer.Impl.1
                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                    ch.setChecked(true);
                    if (prev != null) {
                        prev.setChecked(true);
                    }
                }
            });
            if (prev != null) {
                prev.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: android.ext.Unrandomizer.Impl.2
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            ch.setChecked(false);
                        }
                    }
                });
                ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: android.ext.Unrandomizer.Impl.3
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            prev.setChecked(true);
                        }
                    }
                });
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == null || (view.getTag() instanceof MenuItem)) {
                View v = LayoutInflater.inflateStatic(R.layout.unrand, (ViewGroup) null);
                TextView message = (TextView) v.findViewById(R.id.message);
                Tools.setButtonHelpBackground(message);
                message.setOnClickListener(this);
                this.qwordUse = (CheckBox) v.findViewById(R.id.rd_qword_use);
                this.qwordEdit = (EditText) v.findViewById(R.id.rd_qword);
                this.qwordIncUse = (CheckBox) v.findViewById(R.id.rd_qword_inc_use);
                this.qwordIncEdit = (EditText) v.findViewById(R.id.rd_qword_inc);
                initRow(null, this.qwordUse, Unrandomizer.lastQwordUse, this.qwordEdit, DisplayNumbers.longToString(Unrandomizer.lastQword, 32));
                initRow(this.qwordUse, this.qwordIncUse, Unrandomizer.lastQwordIncUse, this.qwordIncEdit, DisplayNumbers.longToString(Unrandomizer.lastQwordInc, 32));
                this.doubleUse = (CheckBox) v.findViewById(R.id.rd_double_use);
                this.doubleEdit = (EditText) v.findViewById(R.id.rd_double);
                this.doubleIncUse = (CheckBox) v.findViewById(R.id.rd_double_inc_use);
                this.doubleIncEdit = (EditText) v.findViewById(R.id.rd_double_inc);
                initRow(null, this.doubleUse, Unrandomizer.lastDoubleUse, this.doubleEdit, Double.toString(Unrandomizer.lastDouble));
                initRow(this.doubleUse, this.doubleIncUse, Unrandomizer.lastDoubleIncUse, this.doubleIncEdit, Double.toString(Unrandomizer.lastDoubleInc));
                this.dialog = Alert.create().setView(InternalKeyboard.getView(v)).setPositiveButton(Re.s(R.string.ok), this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).create();
                Alert.setOnShowListener(this.dialog, this);
                Alert.show(this.dialog, this.qwordEdit);
            }
            switch (view.getId()) {
                case R.id.message /* 2131427342 */:
                    ConfigListAdapter.showHelp(R.string.help_unrandomizer);
                    break;
                default:
                    onClick(this.dialog, -1);
                    break;
            }
        }
    }
}
