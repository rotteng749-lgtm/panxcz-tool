package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.sup.ArrayListResults;
import android.sup.LongSparseArrayChecked;
import android.view.View;
import android.widget.ListView;
import catch_.me_.if_.you_.can_.R;
import luaj.lib.IoLib;

/* JADX INFO: loaded from: classes.dex */
public class CopySelected extends MenuItem {
    private static final int HEX = 0;
    private static final int HEX_TEXT = 6;
    private static final int HEX_UTF16LE = 8;
    private static final int HEX_UTF8 = 7;
    private static final int HEX_UTF8_UTF16LE = 9;
    private static final int MAX_ITEMS = 100;
    private static final int RHEX = 1;
    private static final int SIZE = 3;
    private static final int TYPES = 2;
    private static final int UTF16LE = 5;
    private static final int UTF8 = 4;

    public CopySelected() {
        super(R.string.copy_as_search, R.drawable.ic_content_copy_white_24dp);
    }

    @Override // android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        CharSequence[] list = {Re.s(R.string.format_hex), Re.s(R.string.format_rhex), Re.s(R.string.add_types), Re.s(R.string.add_size), "UTF-8", "UTF-16LE", "HEX", "HEX + UTF-8", "HEX + UTF-16LE", "HEX + UTF-8 + UTF-16LE"};
        int mask = Config.copyParams;
        final boolean[] checked = new boolean[list.length];
        for (int i = 0; i < list.length; i++) {
            checked[i] = ((1 << i) & mask) != 0;
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { // from class: android.ext.CopySelected.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                int newMask = 0;
                if (which == -1) {
                    for (int i2 = 0; i2 < checked.length; i2++) {
                        if (checked[i2]) {
                            newMask |= 1 << i2;
                        } else {
                            newMask &= (1 << i2) ^ (-1);
                        }
                    }
                }
                Config.get(R.id.config_copy_params).value = newMask;
                Config.save();
                CopySelected.this.copy(newMask);
            }
        };
        Alert.show(Alert.create(Tools.getDarkContext()).setCustomTitle(Tools.getCustomTitle(R.string.copy_as_search)).setMultiChoiceItems(list, checked, new DialogInterface.OnMultiChoiceClickListener() { // from class: android.ext.CopySelected.2
            @Override // android.content.DialogInterface.OnMultiChoiceClickListener
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checked[which] = isChecked;
                if (isChecked) {
                    int mode = which >= 4 ? which : 0;
                    ListView lv = ((AlertDialog) dialog).getListView();
                    if (lv != null) {
                        int i2 = 0;
                        while (i2 < checked.length) {
                            if (mode != (i2 >= 4 ? i2 : 0)) {
                                checked[i2] = false;
                                lv.setItemChecked(i2, false);
                            }
                            i2++;
                        }
                    }
                }
            }
        }).setPositiveButton(Re.s(R.string.yes), listener).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null));
    }

    private void add(Bytes bytes, StringBuilder out, AddressItem item, int mask) {
        if (bytes != null) {
            if (bytes.nextAddr == 0) {
                bytes.nextAddr = item.address;
            }
            int size = item.getSize();
            long l = item.data;
            for (int i = 0; i < size; i++) {
                long addr = item.address + ((long) i);
                if (bytes.nextAddr <= addr) {
                    byte[] bArr = bytes.bytes;
                    int i2 = bytes.len;
                    bytes.len = i2 + 1;
                    bArr[i2] = (byte) (255 & l);
                    bytes.nextAddr = addr;
                }
                l >>= 8;
            }
            return;
        }
        if (out.length() != 0) {
            out.append(';');
        }
        if ((mask & 2) != 0) {
            out.append(item.getStringRhexData());
            out.append(IoLib.R);
        } else if ((mask & 1) != 0) {
            out.append(item.getStringHexData());
            out.append(SearchMenuItem.HEX);
        } else {
            out.append(item.getStringDataTrim());
        }
        if ((mask & 4) != 0) {
            out.append(item.getShortName());
        }
    }

    static class Bytes {
        byte[] bytes = new byte[800];
        int len = 0;
        long nextAddr = 0;

        Bytes() {
        }
    }

    void copy(int mask) {
        Object obj = MainService.instance.getCheckList();
        StringBuilder out = new StringBuilder();
        int count = 0;
        int total = 0;
        long start = 0;
        long end = 0;
        int mode = Integer.numberOfTrailingZeros(mask & 1008);
        Bytes bytes = mode != 32 ? new Bytes() : null;
        if (obj instanceof ArrayListResults) {
            ArrayListResults list = (ArrayListResults) obj;
            AddressItem item = new AddressItem();
            int s = list.size();
            for (int i = 0; i < s; i++) {
                if (list.checked(i)) {
                    if (count < 100) {
                        list.get(i, item);
                        add(bytes, out, item, mask);
                        end = item.address;
                        if (count == 0) {
                            start = end;
                        }
                        count++;
                    }
                    total++;
                    if (total > 100) {
                        break;
                    }
                }
            }
        } else if (obj instanceof LongSparseArrayChecked) {
            LongSparseArrayChecked<SavedItem> list2 = (LongSparseArrayChecked) obj;
            int s2 = list2.size();
            for (int i2 = 0; i2 < s2; i2++) {
                if (list2.checkAt(i2)) {
                    SavedItem item2 = list2.valueAt(i2);
                    if (item2 instanceof SavedItem) {
                        if (count < 100) {
                            add(bytes, out, item2, mask);
                            end = item2.address;
                            if (count == 0) {
                                start = end;
                            }
                            count++;
                        }
                        total++;
                        if (total > 100) {
                            break;
                        }
                    } else {
                        continue;
                    }
                }
            }
        } else if (obj instanceof boolean[]) {
            MemoryContentAdapter memAdapter = MainService.instance.mMemListAdapter;
            boolean[] arr = (boolean[]) obj;
            int s3 = arr.length - 1;
            for (int i3 = 1; i3 < s3; i3++) {
                if (arr[i3]) {
                    if (count < 100) {
                        AddressItem item3 = (AddressItem) memAdapter.getItem(i3);
                        add(bytes, out, item3, mask);
                        end = item3.address;
                        if (count == 0) {
                            start = end;
                        }
                        count++;
                    }
                    total++;
                    if (total > 100) {
                        break;
                    }
                }
            }
        }
        int need = bytes == null ? 2 : 1;
        if (count >= need) {
            if (total > count) {
                Tools.showToast(Tools.stringFormat(Re.s(R.string.used_only_first), Integer.valueOf(count)));
            }
            if (bytes != null) {
                if (mode == 4 || mode == 5) {
                    boolean utf16le = mode == 5;
                    out.append(utf16le ? ';' : ':');
                    out.append(new String(bytes.bytes, 0, bytes.len, ParserNumbers.getCharset(utf16le)));
                } else if (mode == 6) {
                    out.append(SearchMenuItem.HEX);
                    char[] hexArr = HexText.hexArray;
                    byte[] b = bytes.bytes;
                    int is = bytes.len;
                    for (int i4 = 0; i4 < is; i4++) {
                        out.append(' ');
                        int data = b[i4];
                        out.append(hexArr[(data & 240) >> 4]);
                        out.append(hexArr[data & 15]);
                    }
                } else {
                    out.append(SearchMenuItem.HEX_UTF);
                    out.append(' ');
                    HexText.getText(out, 0, bytes.bytes, bytes.len, mode == 7 || mode == 9, mode == 8 || mode == 9, null);
                }
            } else if ((mask & 8) != 0) {
                out.append(':');
                out.append((1 + end) - start);
            }
            final String text = out.toString();
            final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { // from class: android.ext.CopySelected.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    if (which == -1) {
                        Tools.copyText(text, true);
                    } else {
                        MainService.instance.mMainScreen.setCurrentTab(1);
                        MainService.instance.searchListener.openSearch(text);
                    }
                }
            };
            if (bytes == null && end - start >= 65535) {
                Alert.show(Alert.create().setMessage(Tools.stringFormat(Re.s(R.string.too_far), Long.valueOf((1 + end) - start), 65535)).setPositiveButton(Re.s(R.string.ok), new DialogInterface.OnClickListener() { // from class: android.ext.CopySelected.4
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        CopySelected.this.dialog(text, listener);
                    }
                }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
                return;
            } else {
                dialog(text, listener);
                return;
            }
        }
        Tools.showToast(Tools.stringFormat(Re.s(R.string.need_select_more), Integer.valueOf(need)));
    }

    void dialog(String text, DialogInterface.OnClickListener listener) {
        Alert.show(Alert.create().setMessage(text).setPositiveButton(Re.s(R.string.copy), listener).setNeutralButton(Re.s(R.string.search), listener).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
    }
}
