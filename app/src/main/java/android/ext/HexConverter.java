package android.ext;

import android.content.Context;
import android.content.DialogInterface;
import android.ext.Config;
import android.ext.ParserNumbers;
import android.fix.ImageButtonView;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import catch_.me_.if_.you_.can_.R;
import java.util.ArrayList;
import java.util.Locale;
import luaj.lib.IoLib;

/* JADX INFO: loaded from: classes.dex */
public class HexConverter extends ImageButtonView implements View.OnClickListener {
    private boolean useXor;

    public HexConverter(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.useXor = true;
        init();
    }

    public HexConverter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.useXor = true;
        init();
    }

    public HexConverter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.useXor = true;
        init();
    }

    public HexConverter(Context context) {
        super(context);
        this.useXor = true;
        init();
    }

    private void init() {
        setOnClickListener(this);
        if (!isInEditMode()) {
            setImageResource(R.drawable.ic_arrow_down_drop_circle_white_24dp);
            Config.setIconSize(this);
        }
    }

    public void setUseXor(boolean value) {
        this.useXor = value;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        String value;
        int showMask;
        Object o = getTag();
        EditText ed = null;
        int flags = 0;
        if (o instanceof EditText) {
            ed = (EditText) o;
        } else if (o instanceof Object[]) {
            Object[] arr = (Object[]) o;
            if (arr.length == 2 && (arr[0] instanceof EditText)) {
                ed = (EditText) arr[0];
                if (arr[1] instanceof Integer) {
                    flags = ((Integer) arr[1]).intValue();
                } else if (arr[1] instanceof SystemSpinnerType) {
                    SystemSpinnerType sst = (SystemSpinnerType) arr[1];
                    flags = sst.getSelected();
                }
            }
        }
        if (flags == 0) {
            flags = 32;
        }
        if (ed != null) {
            final EditText edit = ed;
            edit.requestFocus();
            final Editable text = edit.getText();
            int start = edit.getSelectionStart();
            int end = edit.getSelectionEnd();
            boolean fullText = start < 0 || end < 0 || start == end;
            if (fullText) {
                start = 0;
                end = text.length();
            }
            try {
                value = text.subSequence(start, end).toString().trim();
            } catch (StringIndexOutOfBoundsException e) {
                Log.w("subSequence fail for " + start + ", " + end + ListManager.TEXT_SEPARATOR + text.length(), e);
                start = 0;
                end = text.length();
                value = text.toString();
                fullText = true;
            }
            if (value.length() == 0) {
                Tools.showToast(Re.s(R.string.empty_field));
                return;
            }
            try {
                if (ParserNumbers.typeAsm(value) != 0) {
                    fullText = false;
                    char ch1 = value.charAt(1);
                    if (ch1 == 'T') {
                        value = String.valueOf(Integer.toHexString(ArmDis.getThumbOpcode((ArmDis) null, 0L, value.substring(2).trim())).toUpperCase(Locale.US)) + SearchMenuItem.HEX;
                    } else if (ch1 == 'A' && value.charAt(2) != '8') {
                        value = String.valueOf(Integer.toHexString(ArmDis.getArmOpcode((ArmDis) null, 0L, value.substring(2).trim())).toUpperCase(Locale.US)) + SearchMenuItem.HEX;
                    }
                }
                if (fullText) {
                    for (char ch : new char[]{ParserNumbers.decimalSeparator, ':', ';', SearchMenuItem.TILDE, SearchMenuItem.XOR_MODE, 'W', SearchMenuItem.HEX_UTF}) {
                        int index = value.indexOf(ch);
                        if (index > 0) {
                            value = value.substring(0, index);
                        }
                    }
                    end = value.length();
                }
                ParserNumbers.Result result = ParserNumbers.parseLong(value);
                final long val = result.value;
                ArrayList<String> list = new ArrayList<>();
                final ArrayList<String> replace = new ArrayList<>();
                String str = Tools.stringFormat("%,d", Long.valueOf(val));
                list.add(str);
                replace.add(str);
                String str2 = String.valueOf(trim(AddressItem.getStringHexData(0L, val, flags), '0', true)) + SearchMenuItem.HEX;
                list.add(str2);
                replace.add(str2);
                String str3 = String.valueOf(trim(AddressItem.getStringRhexData(0L, val, flags), '0', false)) + IoLib.R;
                list.add(str3);
                replace.add(str3);
                if (this.useXor) {
                    String str4 = Tools.stringFormat("%,d", Long.valueOf(((long) Config.xorKey) ^ val));
                    list.add(Tools.stringFormat("XOR %,d = %s", Integer.valueOf(Config.xorKey), str4));
                    replace.add(str4);
                    list.add("XOR ... = ???");
                    replace.add(null);
                }
                if (val != 0) {
                    byte[] bytes = ParserNumbers.getBytes(val);
                    int i = bytes[8];
                    if (i != -1) {
                        try {
                            String str5 = String.valueOf(':') + new String(bytes, 0, i + 1, "UTF-8");
                            list.add("UTF-8: " + str5);
                            replace.add(str5);
                        } catch (Throwable e2) {
                            Log.d("HexConverter: " + value, e2);
                        }
                        if ((i & 1) == 0) {
                            i++;
                        }
                        try {
                            String str6 = String.valueOf(';') + new String(bytes, 0, i + 1, "UTF-16LE");
                            list.add("UTF-16LE: " + str6);
                            replace.add(str6);
                        } catch (Throwable e3) {
                            Log.d("HexConverter: " + value, e3);
                        }
                    }
                }
                int tab = MainService.instance.getTabIndex();
                if (tab == 1) {
                    showMask = AddressArrayAdapter.showMask;
                } else if (tab == 3) {
                    showMask = MainService.instance.mMemListAdapter.showMask;
                } else {
                    showMask = tab == 2 ? SavedListAdapter.showMask : 0;
                }
                if (showMask != 0) {
                    for (int i2 = 0; i2 < 3; i2++) {
                        String op = null;
                        if ((i2 != 0 || (showMask & 16) != 0) && ((i2 != 1 || (showMask & 32) != 0) && (i2 != 2 || (showMask & 64) != 0))) {
                            if (i2 == 0) {
                                op = ArmDis.getArmOpcode((ArmDis) null, 0L, val);
                            }
                            if (i2 == 1) {
                                op = ArmDis.getThumbOpcode((ArmDis) null, 0L, val);
                            }
                            if (i2 == 2) {
                                op = Arm64Dis.disasm(Arm64Dis.args(), 0L, (int) val, new StringBuilder()).toString();
                            }
                            if (op != null) {
                                String op2 = op.trim();
                                int pos = op2.indexOf(59);
                                if (pos != -1) {
                                    op2 = op2.substring(0, pos).trim();
                                }
                                if (op2.length() > 0) {
                                    String name = null;
                                    if (i2 == 0) {
                                        name = "ARM (x32)";
                                        op2 = "~A " + op2;
                                    }
                                    if (i2 == 1) {
                                        name = "Thumb";
                                        op2 = "~T " + op2;
                                    }
                                    if (i2 == 2) {
                                        name = "ARM (x64)";
                                        op2 = "~A8 " + op2;
                                    }
                                    list.add(String.valueOf(name) + ": " + op2);
                                    replace.add(op2);
                                }
                            }
                        }
                    }
                }
                final int from = start;
                final int to = end;
                DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() { // from class: android.ext.HexConverter.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                            try {
                                if (which < replace.size()) {
                                    String repl = (String) replace.get(which);
                                    if (repl == null) {
                                        final Config.Option option = Config.get(R.id.config_xor_key);
                                        final long j = val;
                                        final EditText editText = edit;
                                        final int i3 = from;
                                        final int i4 = to;
                                        final Editable editable = text;
                                        option.setOnChangedListner(new Config.Option.OnChangedListener() { // from class: android.ext.HexConverter.1.1
                                            @Override // android.ext.Config.Option.OnChangedListener
                                            public void onChanged(int value2) {
                                                option.setOnChangedListner(null);
                                                String rep = Tools.stringFormat("%,d", Long.valueOf(j ^ ((long) Config.xorKey)));
                                                try {
                                                    editText.setSelection(i3, i4);
                                                    editable.replace(i3, i4, rep);
                                                } catch (IndexOutOfBoundsException e4) {
                                                    Log.w("Failed replace text", e4);
                                                    editText.setText(rep);
                                                }
                                                editText.requestFocus();
                                            }
                                        });
                                        option.change();
                                    } else {
                                        try {
                                            edit.setSelection(from, to);
                                            text.replace(from, to, repl);
                                        } catch (IndexOutOfBoundsException e4) {
                                            Log.w("Failed replace text", e4);
                                            edit.setText(repl);
                                        }
                                    }
                                }
                            } catch (Throwable e5) {
                                Log.d("HexConverter failed with " + which, e5);
                                return;
                            }
                        }
                        edit.requestFocus();
                    }
                };
                Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(String.valueOf(value) + " →")).setItems((CharSequence[]) list.toArray(new String[list.size()]), l).setNegativeButton(Re.s(R.string.cancel), l).create());
            } catch (Throwable e4) {
                Tools.showToast(String.valueOf(Re.s(R.string.need_integer)) + "\n\n" + e4.getMessage());
                Log.d("HexConverter: " + value, e4);
            }
        }
    }

    private String trim(String hex, char remove, boolean fromStart) {
        int start = 0;
        int end = hex.length();
        if (fromStart) {
            for (int i = 0; i < end - 2 && hex.charAt(i) == remove; i++) {
                start = (i + 1) & (-2);
            }
        } else {
            for (int i2 = end - 1; i2 >= 2 && hex.charAt(i2) == remove; i2--) {
                end = (i2 + 1) & (-2);
            }
        }
        return hex.substring(start, end);
    }
}
