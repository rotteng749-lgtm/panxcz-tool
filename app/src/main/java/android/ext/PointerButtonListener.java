package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.ext.ProcessList;
import android.view.View;
import catch_.me_.if_.you_.can_.R;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class PointerButtonListener extends SearchMenuItem {
    private static String lastOffset = "0";
    private static boolean useHex = true;

    public PointerButtonListener() {
        super(R.string.help_pointer_search_title, R.drawable.ic_pointer_white_24dp);
    }

    @Override // android.ext.SearchMenuItem, android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        if (v != null && (v.getTag() instanceof Integer)) {
            onClick(null, ((Integer) v.getTag()).intValue());
        } else {
            show(null);
        }
    }

    private int getFlags() {
        ProcessList.ProcessInfo info = MainService.instance.processInfo;
        return (info == null || !info.x64) ? 4 : 36;
    }

    public void show(String addrString) {
        if (MainService.instance.mDaemonManager.isDaemonRun()) {
            MainService service = MainService.instance;
            int flags = getFlags();
            if (this.searcher == null) {
                this.searcher = new Searcher(4, flags);
            }
            this.searcher.setFocusValue(addrString == null);
            Searcher searcher = this.searcher;
            if (addrString == null) {
                addrString = this.lastInput;
            }
            searcher.setNumber(addrString);
            this.searcher.setMessage(Re.s(R.string.mask_request));
            this.searcher.setOffset(lastOffset);
            this.searcher.setOffsetHex(useHex);
            this.searcher.setType(AddressItem.getDataForSearch(flags, flags != 4), flags);
            AlertDialog.Builder builder = Alert.create().setView(this.searcher.getViewForAttach()).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null);
            if (service.mResultCount == 0 || (MainService.getLastFlags() & flags) == 0) {
                builder.setPositiveButton(Re.s(R.string.new_search), this);
            } else {
                builder.setPositiveButton(Re.s(R.string.refine), this).setNeutralButton(Re.s(R.string.new_search), this);
            }
            AlertDialog dialog = builder.create();
            Alert.setOnDismissListener(dialog, this);
            Alert.setOnShowListener(dialog, this);
            this.weakDialog = new WeakReference<>(dialog);
            Alert.show(dialog, this.searcher.getNumberEdit());
        }
    }

    @Override // android.ext.SearchMenuItem
    public void onClickCallback(DialogInterface d, int which) {
    }

    @Override // android.ext.SearchMenuItem, android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface d, int which) {
        try {
            long addr = ParserNumbers.parseBigLong(checkScript(this.searcher.getNumber()), 16);
            long offset = ParserNumbers.parseBigLong(checkScript(this.searcher.getOffset()), this.searcher.isOffsetHex() ? 16 : 10);
            int flags = this.searcher.getType();
            String input = String.valueOf(AddressItem.getStringAddress(addr, flags)) + SearchMenuItem.HEX;
            String second = String.valueOf(AddressItem.getStringAddress(addr - offset, flags)) + SearchMenuItem.HEX;
            if (offset > 0) {
                input = String.valueOf(second) + SearchMenuItem.TILDE + input;
            } else if (offset < 0) {
                input = String.valueOf(input) + SearchMenuItem.TILDE + second;
            }
            SearchButtonListener.doSearch(input, flags, false, AddressItem.FLAG_VALUE_EQUAL, this.searcher.getMem(0), this.searcher.getMem(1), which == -3 || (MainService.getLastFlags() & flags) == 0);
            MainService.instance.setTabIndex(1);
            Tools.hideKeyboard(this.weakDialog);
        } catch (NumberFormatException e) {
            SearchMenuItem.inputError(e, this.searcher.getNumberEdit());
            return;
        } catch (Throwable e2) {
            Log.e("Exception on start search", e2);
        }
        Tools.dismiss(this.weakDialog);
    }

    @Override // android.ext.SearchMenuItem, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        if (this.searcher != null) {
            useHex = this.searcher.isOffsetHex();
            lastOffset = this.searcher.getOffset();
            this.lastInput = this.searcher.getNumber();
        }
        super.onDismiss(dialog);
    }
}
