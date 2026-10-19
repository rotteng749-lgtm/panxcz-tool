package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.ext.ParserNumbers;
import android.ext.Script;
import android.ext.SearchButtonListener;
import android.fix.LayoutInflater;
import android.fix.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class FuzzyButtonListener extends SearchMenuItem implements View.OnClickListener {
    private SparseArray<CharSequence> data;
    private long memoryFrom;
    private long memoryTo;

    public FuzzyButtonListener() {
        super(R.string.search_unknown_value, R.drawable.ic_fuzzy_white_24dp);
        this.memoryFrom = 0L;
        this.memoryTo = -1L;
    }

    public static boolean doSearch(int flags, long memoryFrom, long memoryTo) throws NumberFormatException {
        boolean ret = doSearch((byte) 0, flags, memoryFrom, memoryTo);
        Record record = MainService.instance.currentRecord;
        if (record != null) {
            record.write("\ngg.startFuzzy(");
            Script.Consts.logConst(record, record.consts.TYPE_, flags);
            record.write(", ");
            Script.Consts.logHex(record, memoryFrom);
            record.write(", ");
            Script.Consts.logHex(record, memoryTo);
            record.write(")\n");
        }
        return ret;
    }

    public static boolean doSearch(byte seq, int flags, long memoryFrom, long memoryTo) throws NumberFormatException {
        int flags2 = flags & AddressItem.FLAG_AUTO;
        MainService service = MainService.instance;
        if (flags2 == 0) {
            service.clear(seq);
            return true;
        }
        if (service.showBusyDialog()) {
            if (service.mResultCount != 0) {
                service.clear(seq);
            }
            service.usedFuzzy = true;
            service.mDaemonManager.sendConfig(seq);
            service.lockApp(seq);
            service.showSearchHint = false;
            service.mDaemonManager.fuzzyStart(seq, flags2, memoryFrom, memoryTo);
            MainService.setLastFlags(flags2, seq);
            return false;
        }
        throw new NumberFormatException(Re.s(R.string.busy_dialog_fail));
    }

    public static boolean doRefine(String input, int flags, int sign, int count, long memoryFrom, long memoryTo) throws NumberFormatException {
        String input2 = checkScript(input);
        boolean ret = doRefine((byte) 0, input2, flags, sign, count, memoryFrom, memoryTo);
        Record record = MainService.instance.currentRecord;
        if (record != null) {
            record.write("gg.searchFuzzy(");
            Script.Consts.logString(record, input2);
            record.write(", ");
            Script.Consts.logConst(record, record.consts.SIGN_FUZZY_, sign);
            record.write(", ");
            Script.Consts.logConst(record, record.consts.TYPE_, flags);
            record.write(", ");
            Script.Consts.logHex(record, memoryFrom);
            record.write(", ");
            Script.Consts.logHex(record, memoryTo);
            record.write(")\n");
        }
        return ret;
    }

    public static boolean doRefine(byte seq, String input, int flags, int sign, int count, long memoryFrom, long memoryTo) throws NumberFormatException {
        long number;
        int flags2;
        int flags3 = flags & AddressItem.FLAG_AUTO;
        int sign2 = sign & AddressItem.FLAG_SIGN;
        MainService service = MainService.instance;
        int flags4 = flags3 & MainService.getLastFlags() & AddressItem.FLAG_AUTO;
        if (flags4 == 0) {
            service.clear(seq);
            return true;
        }
        long number2 = 0;
        boolean range = input.indexOf(126) != -1;
        ParserNumbers.Result res = new ParserNumbers.Result();
        if (range) {
            if (sign2 != 536870912 && sign2 != 268435456) {
                throw new NumberFormatException(Tools.stringFormat(Re.s(R.string.does_not_support), Re.s(R.string.help_range_search_title), AddressItem.getSignNames().get(sign2)));
            }
            SearchButtonListener.RangeResult result = SearchButtonListener.parseRange(res, input, flags4, sign2);
            number = result.from;
            number2 = result.to;
            flags2 = result.type;
            sign2 = result.sign;
        } else {
            number = AddressItem.dataForSearch(res, input, flags4);
            flags2 = flags4 & res.type;
        }
        if (Integer.bitCount(flags2) > 1) {
            flags2 = AddressItem.fixAutoFlag(flags2, number, res.isNegative);
            if (number2 != 0) {
                flags2 = AddressItem.fixAutoFlag(flags2, number2, res.isNegative);
            }
        }
        int lastFlags = flags2;
        if (service.showBusyDialog()) {
            service.lockApp(seq);
            service.showSearchHint = false;
            service.mDaemonManager.fuzzyRefine(seq, number, number2, range ? flags2 | AddressItem.FLAG_RANGE : flags2, sign2, count, memoryFrom, memoryTo);
            MainService.setLastFlags(lastFlags, seq);
            return false;
        }
        throw new NumberFormatException(Re.s(R.string.busy_dialog_fail));
    }

    private void show() {
        MainService service = MainService.instance;
        if (service.mDaemonManager.isDaemonRun()) {
            int flags = MainService.getLastFlags();
            if (service.mResultCount == 0) {
                this.searcher = new Searcher(1, flags);
                this.data = this.searcher.setTypeInternal(flags);
                this.searcher.setMessage(Re.s(R.string.type_request));
                AlertDialog dialog = Alert.create().setView(this.searcher.getViewForAttach()).setPositiveButton(Re.s(R.string.search), this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).create();
                Alert.setOnDismissListener(dialog, this);
                Alert.setOnShowListener(dialog, this);
                this.weakDialog = new WeakReference<>(dialog);
                Alert.show(dialog);
                return;
            }
            this.searcher = new Searcher(2, flags);
            this.data = this.searcher.setTypeInternal(flags);
            this.searcher.setMessage(Re.s(R.string.compare_current_last));
            this.searcher.setOnCancelClickListener(this);
            View view = this.searcher.getViewForAttach();
            ((Button) view.findViewById(R.id.equal_btn)).setOnClickListener(this);
            ((Button) view.findViewById(R.id.not_equal_btn)).setOnClickListener(this);
            ((Button) view.findViewById(R.id.larger_btn)).setOnClickListener(this);
            ((Button) view.findViewById(R.id.smaller_btn)).setOnClickListener(this);
            AlertDialog dialog2 = Alert.create().setView(view).create();
            Alert.setOnDismissListener(dialog2, this);
            Alert.setOnShowListener(dialog2, this);
            this.weakDialog = new WeakReference<>(dialog2);
            Alert.show(dialog2, this.searcher.getNumberEdit());
        }
    }

    @Override // android.ext.SearchMenuItem, android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        MainService service = MainService.instance;
        if (v == null || (v.getTag() instanceof MenuItem)) {
            new FuzzyButtonListener().show();
            return;
        }
        if ((v.getTag() instanceof Integer) && service.mResultCount == 0) {
            try {
                this.memoryFrom = this.searcher.getMem(0);
                this.memoryTo = this.searcher.getMem(1);
                Tools.hideKeyboard(this.weakDialog);
                int flags = this.searcher.getType();
                if (flags == 0) {
                    new TypeSelector(this.data, "0", Re.s(R.string.type_request), this);
                } else {
                    doSearch(flags, this.memoryFrom, this.memoryTo);
                }
            } catch (NumberFormatException e) {
                SearchMenuItem.inputError(e, null);
                return;
            }
        }
        int id = v.getId();
        int sign = 0;
        switch (id) {
            case R.id.equal_btn /* 2131427644 */:
                if (Searcher.fuzzyExtended || service.processPaused) {
                    sign = AddressItem.FLAG_VALUE_EQUAL;
                } else {
                    multiEqual();
                }
                break;
            case R.id.not_equal_btn /* 2131427645 */:
                sign = AddressItem.FLAG_VALUE_NOT_EQUAL;
                break;
            case R.id.larger_btn /* 2131427646 */:
                sign = AddressItem.FLAG_VALUE_GREATER_OR_EQUAL;
                break;
            case R.id.smaller_btn /* 2131427647 */:
                sign = 134217728;
                break;
        }
        if (sign != 0) {
            try {
                int flags2 = this.searcher.getType();
                if (flags2 == 0 || !Searcher.fuzzyExtended) {
                    flags2 = MainService.getLastFlags() & AddressItem.FLAG_AUTO;
                }
                doRefine(this.searcher.getNumber(), flags2, sign, 1, this.searcher.getMem(0), this.searcher.getMem(1));
            } catch (NumberFormatException e2) {
                SearchMenuItem.inputError(e2, this.searcher.getNumberEdit());
                return;
            }
        }
        Tools.hideKeyboard(this.weakDialog);
        Tools.dismiss(this.weakDialog);
    }

    private void multiEqual() {
        View view = LayoutInflater.inflateStatic(R.layout.service_fuzzy_equal, (ViewGroup) null);
        final SeekBar countSeekbar = (SeekBar) view.findViewById(R.id.count_fuzzy_equal_seekbar);
        final TextView countTextView = (TextView) view.findViewById(R.id.count_fuzzy_equal);
        countSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: android.ext.FuzzyButtonListener.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                countTextView.setText(new StringBuilder().append(progress + 1).toString());
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.fuzzy_equal)).setMessage(Re.s(R.string.count_fuzzy_equal_run)).setPositiveButton(Re.s(R.string.ok), new DialogInterface.OnClickListener() { // from class: android.ext.FuzzyButtonListener.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                try {
                    int flags = MainService.getLastFlags() & AddressItem.FLAG_AUTO;
                    FuzzyButtonListener.doRefine("0", flags, AddressItem.FLAG_VALUE_EQUAL, countSeekbar.getProgress() + 1, FuzzyButtonListener.this.searcher.getMem(0), FuzzyButtonListener.this.searcher.getMem(1));
                } catch (NumberFormatException e) {
                    SearchMenuItem.inputError(e, FuzzyButtonListener.this.searcher.getNumberEdit());
                }
            }
        }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).setView(view));
    }

    @Override // android.ext.SearchMenuItem, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        MainService.lastType = this.searcher.getType();
        super.onDismiss(dialog);
    }

    @Override // android.ext.SearchMenuItem
    public void onClickCallback(DialogInterface d, int which) {
    }

    @Override // android.ext.SearchMenuItem, android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) {
            onClick(((AlertDialog) dialog).getButton(-1));
        } else if (MainService.instance.mResultCount == 0) {
            MainService.lastType = which;
            doSearch(which, this.memoryFrom, this.memoryTo);
        }
    }
}
