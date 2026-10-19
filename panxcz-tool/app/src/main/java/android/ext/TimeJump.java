package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.ext.Config;
import android.ext.ParserNumbers;
import android.ext.Script;
import android.fix.LayoutInflater;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class TimeJump extends MenuItem {
    static volatile String last = "0";
    static volatile TimeJump lastInstance = null;

    public static long parseNumber(String number) throws NumberFormatException {
        String number2 = number.trim();
        double value = Tools.parseTime(number2);
        if (value < (-92233720L)) {
            ParserNumbers.NumberFormatException_ e = new ParserNumbers.NumberFormatException_(Tools.stringFormat(Re.s(R.string.number_less), number2, Long.valueOf(-92233720L)));
            e.addNumber(number2);
            e.addNumber(-92233720L);
            throw e;
        }
        if (value > 92233720) {
            ParserNumbers.NumberFormatException_ e2 = new ParserNumbers.NumberFormatException_(Tools.stringFormat(Re.s(R.string.number_greater), number2, 92233720L));
            e2.addNumber(number2);
            e2.addNumber(92233720L);
            throw e2;
        }
        return (long) (1.0E9d * value);
    }

    public static void makeJump(final long jump) {
        if (MainService.instance.mAppDetector.checkAppSelect(false, new Runnable() { // from class: android.ext.TimeJump.1
            @Override // java.lang.Runnable
            public void run() {
                MainService.instance.mDaemonManager.addTimeJump(jump);
            }
        })) {
            MainService.instance.mDaemonManager.addTimeJump(jump);
        }
        saveJump(jump);
        Record record = MainService.instance.currentRecord;
        if (record != null) {
            String text = Tools.doubleToTime(jump / 1.0E9d);
            record.write("gg.timeJump(");
            Script.Consts.logNumberString(record, text);
            record.write(")\n");
        }
    }

    static void saveJump(long jump) {
        Config.get(R.id.config_time_jump_last).value = (int) ((jump / 1.0E9d) * 1000.0d);
        Config.save();
        TimeJumpPanel panel = MainService.instance.timeJumpPanel;
        if (panel != null) {
            panel.setLast();
        }
    }

    public TimeJump() {
        super(R.string.time_jump, R.drawable.ic_fast_forward_white_24dp);
        lastInstance = this;
    }

    @Override // android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        new Impl(this, null).onClick(v);
    }

    private class Impl implements View.OnClickListener, DialogInterface.OnClickListener, DialogInterface.OnShowListener, DialogInterface.OnDismissListener, TextWatcher {
        AlertDialog dialog;
        EditText edit;
        TextView hint;
        TextView panel;

        private Impl() {
        }

        /* synthetic */ Impl(TimeJump timeJump, Impl impl) {
            this();
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            Tools.setButtonListener(dialog, -1, null, this);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            try {
                String number = SearchMenuItem.checkScript(this.edit.getText().toString().trim());
                long jump = TimeJump.parseNumber(number);
                TimeJump.makeJump(jump);
                History.add(number, 1);
                Tools.dismiss(dialog);
            } catch (NumberFormatException e) {
                SearchMenuItem.inputError(e, this.edit);
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == null || (view.getTag() instanceof MenuItem)) {
                View v = LayoutInflater.inflateStatic(R.layout.time_jump, (ViewGroup) null);
                ImageView examples = Config.setIconSize((ImageView) v.findViewById(R.id.examples));
                examples.setOnClickListener(this);
                this.hint = (TextView) v.findViewById(R.id.hint);
                this.panel = (TextView) v.findViewById(R.id.config_time_jump_panel);
                this.panel.setOnClickListener(this);
                this.panel.setText(Config.get(R.id.config_time_jump_panel).toString());
                this.edit = (EditText) v.findViewById(R.id.time_jump);
                String text = TimeJump.last;
                if (text.length() == 0 || "0".equals(text)) {
                    text = Tools.doubleToTime(((double) Config.timeJumpLast) / 1000.0d);
                }
                this.edit.setText(text);
                this.edit.addTextChangedListener(this);
                this.edit.setDataType(1);
                updateHint();
                this.dialog = Alert.create().setView(InternalKeyboard.getView(v)).setPositiveButton(Re.s(R.string.ok), this).setNegativeButton(Re.s(R.string.back), (DialogInterface.OnClickListener) null).create();
                Alert.setOnShowListener(this.dialog, this);
                Alert.setOnDismissListener(this.dialog, this);
                Alert.show(this.dialog, this.edit);
            }
            switch (view.getId()) {
                case R.id.examples /* 2131427345 */:
                    Searcher.showExamples(Re.s(R.string.examples_timejump));
                    break;
                case R.id.config_time_jump_panel /* 2131427460 */:
                    final Config.Option option = Config.get(R.id.config_time_jump_panel);
                    option.setOnChangedListner(new Config.Option.OnChangedListener() { // from class: android.ext.TimeJump.Impl.1
                        @Override // android.ext.Config.Option.OnChangedListener
                        public void onChanged(int value) {
                            option.setOnChangedListner(null);
                            Impl.this.panel.setText(option.toString());
                        }
                    });
                    option.change();
                    break;
                default:
                    onClick(this.dialog, -1);
                    break;
            }
        }

        public void updateHint() {
            String text;
            EditText edit = this.edit;
            TextView hint = this.hint;
            if (edit == null) {
                text = Searcher.MASK_PLACEHOLDER;
            } else {
                try {
                    String number = edit.getText().toString().trim();
                    long jump = TimeJump.parseNumber(number) / DaemonManager.TIME_JUMP_MUL;
                    text = Tools.longToTime(jump);
                } catch (NumberFormatException e) {
                    text = "???";
                }
            }
            if (hint != null) {
                hint.setText(text);
            }
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            TimeJump.last = this.edit.getText().toString().trim();
            try {
                String number = TimeJump.last;
                long jump = TimeJump.parseNumber(SearchMenuItem.checkScript(number));
                TimeJump.saveJump(jump);
            } catch (NumberFormatException e) {
            }
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateHint();
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
            updateHint();
        }
    }
}
