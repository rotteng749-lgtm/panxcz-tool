package android.ext;

import android.content.DialogInterface;
import android.ext.Script;
import android.view.View;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class MaskButtonListener extends SearchMenuItem implements View.OnClickListener {
    public MaskButtonListener() {
        super(R.string.help_mask_search_title, R.drawable.ic_mask_white_24dp);
    }

    public static boolean doSearch(String input, long mask, int flags, int sign, long memoryFrom, long memoryTo, boolean forceNew) throws NumberFormatException {
        String input2 = checkScript(input);
        boolean ret = doSearch((byte) 0, input2, mask, flags, sign, memoryFrom, memoryTo, forceNew);
        Record record = MainService.instance.currentRecord;
        if (record != null) {
            if (forceNew) {
                record.write("\ngg.clearResults()\n");
            }
            if (forceNew || MainService.instance.mResultCount == 0) {
                record.write("gg.searchAddress(");
            } else {
                record.write("gg.refineAddress(");
            }
            Script.Consts.logNumberString(record, input2);
            record.write(", ");
            Script.Consts.logHex(record, mask);
            record.write(", ");
            Script.Consts.logConst(record, record.consts.TYPE_, flags);
            record.write(", ");
            Script.Consts.logConst(record, record.consts.SIGN_, sign);
            record.write(", ");
            Script.Consts.logHex(record, memoryFrom);
            record.write(", ");
            Script.Consts.logHex(record, memoryTo);
            record.write(")\n");
        }
        return ret;
    }

    public static boolean doSearch(byte seq, String input, long mask, int flags, int sign, long memoryFrom, long memoryTo, boolean forceNew) throws NumberFormatException {
        int flags2 = flags & AddressItem.FLAG_AUTO;
        MainService service = MainService.instance;
        if (service.mResultCount != 0 && !forceNew) {
            flags2 &= MainService.getLastFlags() & AddressItem.FLAG_AUTO;
        }
        if (flags2 == 0) {
            service.clear(seq);
            return true;
        }
        long[] addr = Searcher.parseMask(input, mask);
        int lastFlags = flags2;
        int flags3 = flags2 | sign;
        if (service.showBusyDialog()) {
            if (service.mResultCount != 0 && forceNew) {
                service.clear(seq);
            }
            if (service.mResultCount == 0) {
                service.usedFuzzy = false;
                service.mDaemonManager.sendConfig(seq);
            }
            service.lockApp(seq);
            service.showSearchHint = false;
            service.mDaemonManager.searchMask(seq, addr[0], addr[1], flags3, memoryFrom, memoryTo);
            MainService.setLastFlags(lastFlags, seq);
            return false;
        }
        throw new NumberFormatException(Re.s(R.string.busy_dialog_fail));
    }

    @Override // android.ext.SearchMenuItem
    public void onClickCallback(DialogInterface d, int which) {
        String input = this.searcher.getNumber();
        doSearch(input, this.searcher.getDirectMask(), which, this.searcher.getSign(), this.searcher.getMem(0), this.searcher.getMem(1), this.lastButton == -3);
    }

    @Override // android.ext.SearchMenuItem, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        this.lastInput = this.searcher.getNumber();
        this.lastMask = this.searcher.getMask();
        MainService.lastType = this.searcher.getType();
        this.signLastSelect = this.searcher.getSign();
        super.onDismiss(dialog);
    }
}
