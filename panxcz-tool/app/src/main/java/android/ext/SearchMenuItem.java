package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.ext.ArmDis;
import android.ext.InternalKeyboard;
import android.fix.SparseArray;
import android.text.Editable;
import android.view.View;
import catch_.me_.if_.you_.can_.R;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public abstract class SearchMenuItem extends MenuItem implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener, DialogInterface.OnShowListener {
    public static final char COLON = ':';
    public static final char HEX = 'h';
    public static final char HEX_UTF = 'Q';
    public static final char HEX_UTF_16LE = '\"';
    public static final char HEX_UTF_8 = '\'';
    public static final char SEMICOLON = ';';
    public static final char TILDE = '~';
    public static final char UTF_16LE = ';';
    public static final char UTF_8 = ':';
    public static final char XOR_MODE = 'X';
    public static final char XOR_MODE_SMALL = 'x';
    protected int lastButton;
    protected String lastInput;
    protected String lastMask;
    protected Searcher searcher;
    protected int signLastSelect;
    protected WeakReference<AlertDialog> weakDialog;

    public abstract void onClickCallback(DialogInterface dialogInterface, int i);

    public SearchMenuItem(int title, int resId) {
        super(title, resId);
        this.lastInput = "";
        this.signLastSelect = AddressItem.FLAG_VALUE_EQUAL;
        this.lastMask = "FFFFFFFFFFFFFFFF";
        this.lastButton = -1;
        this.weakDialog = new WeakReference<>(null);
    }

    public SearchMenuItem(int title) {
        super(title);
        this.lastInput = "";
        this.signLastSelect = AddressItem.FLAG_VALUE_EQUAL;
        this.lastMask = "FFFFFFFFFFFFFFFF";
        this.lastButton = -1;
        this.weakDialog = new WeakReference<>(null);
    }

    public SearchMenuItem(String title, int resId) {
        super(title, resId);
        this.lastInput = "";
        this.signLastSelect = AddressItem.FLAG_VALUE_EQUAL;
        this.lastMask = "FFFFFFFFFFFFFFFF";
        this.lastButton = -1;
        this.weakDialog = new WeakReference<>(null);
    }

    public SearchMenuItem(String title) {
        super(title);
        this.lastInput = "";
        this.signLastSelect = AddressItem.FLAG_VALUE_EQUAL;
        this.lastMask = "FFFFFFFFFFFFFFFF";
        this.lastButton = -1;
        this.weakDialog = new WeakReference<>(null);
    }

    public static String checkScript(String input) {
        if (input != null) {
            String script = input.trim();
            if (script.length() > 0 && input.charAt(0) == '=') {
                String script2 = script.substring(1);
                if (script2.indexOf("return") < 0) {
                    script2 = "return " + script2;
                }
                try {
                    Log.d("oneLiner IN: '" + script2 + "'");
                    String input2 = Script.oneLiner(script2);
                    Log.d("oneLiner OUT: '" + input2 + "'");
                    return input2;
                } catch (Throwable e) {
                    Log.d("oneLiner ERR:", e);
                    NumberFormatException e2 = new NumberFormatException(e.getMessage());
                    e2.initCause(e);
                    throw e2;
                }
            }
            return input;
        }
        return input;
    }

    public static void inputError(NumberFormatException e, final android.widget.EditText edit) {
        Log.w("Error", e);
        if (edit != null && (e instanceof ArmDis.AsmFailedException)) {
            final ArmDis.AsmFailedException asm = (ArmDis.AsmFailedException) e;
            Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.error)).setMessage(e.getMessage()).setPositiveButton(Re.s(R.string.yes), new DialogInterface.OnClickListener() { // from class: android.ext.SearchMenuItem.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    Editable ed = edit.getText();
                    String text = ed.toString();
                    String data = asm.data;
                    if (text.contains(data)) {
                        int st = text.indexOf(data);
                        ed.replace(st, data.length() + st, asm.suggestion);
                    }
                }
            }).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null));
        } else {
            Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.error)).setMessage(e.getMessage()).setPositiveButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
        }
    }

    public void onClick(DialogInterface d, int which) {
        if (which == -1 || which == -3) {
            this.lastButton = which;
            onClick(null);
            return;
        }
        if (which != -2) {
            try {
                this.searcher.setType(which);
                onClickCallback(d, which);
                Tools.hideKeyboard(this.weakDialog);
            } catch (NumberFormatException e) {
                inputError(e, this.searcher.getNumberEdit());
                return;
            } catch (Throwable e2) {
                Log.e("Exception on start search", e2);
            }
            Tools.dismiss(this.weakDialog);
        }
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialog) {
        Tools.setButtonListener(dialog, -1, -1, this);
        Tools.setButtonListener(dialog, -3, -3, this);
        if (this.searcher != null) {
            this.searcher.onShow();
        }
    }

    public void onDismiss(DialogInterface dialog) {
        if (this.searcher != null) {
            this.searcher.getViewForAttachSimple();
        }
    }

    @Override // android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        if (v != null && (v.getTag() instanceof Integer)) {
            this.lastButton = ((Integer) v.getTag()).intValue();
        }
        int flags = AddressItem.FLAG_AUTO;
        boolean mask = this instanceof MaskButtonListener;
        if (v != null && (v.getTag() instanceof MenuItem)) {
            if (MainService.instance.mDaemonManager.isDaemonRun()) {
                if (this.searcher == null) {
                    this.searcher = new Searcher(mask ? 3 : 0, AddressItem.FLAG_AUTO);
                }
                this.searcher.setMessage(Re.s(mask ? R.string.mask_request : R.string.value_request));
                this.searcher.setSign(this.signLastSelect);
                this.searcher.setNumber(this.lastInput);
                if (mask) {
                    this.searcher.setMask(this.lastMask);
                    this.searcher.getView().setTag(new InternalKeyboard.Flags(1));
                }
                this.searcher.setTypeInternal(AddressItem.FLAG_AUTO);
                AlertDialog.Builder builder = Alert.create().setView(this.searcher.getViewForAttach()).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null);
                if (MainService.instance.mResultCount == 0) {
                    builder.setPositiveButton(Re.s(R.string.new_search), this);
                } else {
                    builder.setPositiveButton(Re.s(R.string.refine), this).setNeutralButton(Re.s(R.string.new_search), this);
                }
                this.lastButton = -1;
                AlertDialog dialog = builder.create();
                Alert.setOnDismissListener(dialog, this);
                Alert.setOnShowListener(dialog, this);
                this.weakDialog = new WeakReference<>(dialog);
                Alert.show(dialog, this.searcher.getNumberEdit());
                return;
            }
            return;
        }
        int type = this.searcher.getType();
        if (type == 0) {
            if (mask) {
                SparseArray<CharSequence> data = AddressItem.getDataForSearch(AddressItem.FLAG_AUTO, true);
                new TypeSelector(data, "0", Re.s(R.string.type_request), this);
                return;
            }
            try {
                String input = checkScript(this.searcher.getNumber());
                boolean special = (input.indexOf(59) == -1 && input.indexOf(126) == -1 && input.indexOf(88) == -1 && input.indexOf(120) == -1) ? false : true;
                if (special) {
                    flags = AddressItem.FLAG_AUTO;
                }
                SparseArray<CharSequence> data2 = AddressItem.getDataForSearch(flags, true);
                if (special) {
                    input = "0";
                }
                new TypeSelector(data2, input, Re.s(R.string.type_request), this);
                return;
            } catch (NumberFormatException e) {
                inputError(e, this.searcher.getNumberEdit());
                return;
            }
        }
        onClick(null, type);
    }
}
