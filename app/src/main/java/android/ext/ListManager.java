package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.ext.ProcessList;
import android.ext.RegionList;
import android.ext.Script;
import android.fix.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class ListManager implements DialogInterface.OnClickListener, DialogInterface.OnShowListener, DialogInterface.OnDismissListener, View.OnClickListener, View.OnLongClickListener {
    private static final String LIST_PATH = "save-path";
    public static final int LOAD_API = 4;
    public static final int LOAD_APPEND = 8;
    public static final int LOAD_FREEZE = 1;
    public static final int LOAD_VALUES = 2;
    public static final int LOAD_VALUES_FREEZE = 3;
    public static final String NEW_LINE = "\n";
    public static final int SAVE_AS_TEXT = 1;
    public static final char SEPARATOR = '|';
    public static final String TEXT_SEPARATOR = "; ";
    private static int lastMode = -1;
    private CheckBox asText;
    private AlertDialog dialog;
    private android.widget.EditText edit;
    boolean fromExport = false;
    private RadioGroup group;
    private SavedItem[] list;
    private ProcessList.ProcessInfo processInfo;

    public static void saveList(int pid, SavedItem[] list, String filename, int mode) throws IOException {
        String access;
        String name;
        long offset;
        File file = new File(filename);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        boolean asText = (mode & 1) != 0;
        FileWriter writer = new FileWriter(file);
        if (!asText) {
            writer.write(String.valueOf(pid) + NEW_LINE);
        }
        for (SavedItem item : list) {
            if (item != null) {
                RegionList.Region region = RegionList.getRegionItem(item.address);
                if (region == null || region.getInternalName().length() == 0) {
                    access = "";
                    name = "";
                    offset = 0;
                } else {
                    access = region.getType();
                    name = region.getInternalName();
                    offset = item.address - region.start;
                }
                StringBuilder line = new StringBuilder();
                if (asText) {
                    line.append(RegionList.getRegion(item.address));
                    line.append(TEXT_SEPARATOR);
                    line.append(item.getStringAddress());
                    line.append(TEXT_SEPARATOR);
                    line.append(item.getName());
                    line.append(TEXT_SEPARATOR);
                    line.append(item.getStringDataTrim());
                    line.append(TEXT_SEPARATOR);
                    line.append(item.getNameShort());
                    line.append(NEW_LINE);
                } else {
                    line.append(escape(item.getName()));
                    line.append(SEPARATOR);
                    line.append(Long.toHexString(item.address));
                    line.append(SEPARATOR);
                    line.append(Integer.toHexString(item.flags));
                    line.append(SEPARATOR);
                    line.append(Long.toHexString(item.data));
                    line.append(SEPARATOR);
                    line.append(item.freeze ? '1' : '0');
                    line.append(SEPARATOR);
                    line.append((int) item.freezeType);
                    line.append(SEPARATOR);
                    line.append(Long.toHexString(item.freezeFrom));
                    line.append(SEPARATOR);
                    line.append(Long.toHexString(item.freezeTo));
                    line.append(SEPARATOR);
                    line.append(escape(access));
                    line.append(SEPARATOR);
                    line.append(escape(name));
                    line.append(SEPARATOR);
                    line.append(Long.toHexString(offset));
                    line.append(NEW_LINE);
                }
                writer.write(line.toString());
            }
        }
        writer.close();
    }

    private static String escape(String str) {
        return str.replace(SEPARATOR, '/');
    }

    public static void loadList(int pid, String filename, int mode) throws IOException {
        File file = new File(filename);
        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            boolean useFreeze = (mode & 1) != 0;
            boolean useValues = (mode & 2) != 0;
            SavedListAdapter savedList = MainService.instance.savedList;
            if ((mode & 8) == 0) {
                savedList.clear();
            }
            int lineNum = 0;
            boolean sameRun = false;
            while (true) {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    lineNum++;
                    if (lineNum == 1) {
                        try {
                            sameRun = Integer.parseInt(line) == pid;
                        } catch (Throwable e) {
                            Log.w("Failed parse pid: '" + line + SearchMenuItem.HEX_UTF_8, e);
                        }
                    } else {
                        String[] parts = line.split("\\|");
                        if (parts.length < 10) {
                            Log.w("Failed parse line: '" + line + "' " + parts.length);
                        } else {
                            try {
                                SavedItem item = new SavedItem(ParserNumbers.parseBigLong(parts[1], 16), ParserNumbers.parseBigLong(parts[3], 16), Integer.parseInt(parts[2], 16), parts[0], useFreeze ? parts[4].equals("1") : false, Byte.parseByte(parts[5]), ParserNumbers.parseBigLong(parts[6], 16), ParserNumbers.parseBigLong(parts[7], 16));
                                long offset = ParserNumbers.parseBigLong(parts[10], 16);
                                if (!sameRun) {
                                    RegionList.Region region = RegionList.getRegionItem(item.address);
                                    if (region == null || !region.getType().equals(parts[8]) || !region.getInternalName().equals(parts[9]) || item.address != region.start + offset) {
                                        RegionList.Region region2 = RegionList.getRegionItem(parts[8], parts[9], offset);
                                        if (region2 != null) {
                                            long address = region2.start + offset;
                                            Log.d("Fix: " + Long.toHexString(item.address) + " -> " + Long.toHexString(address) + TEXT_SEPARATOR + parts[8] + ' ' + parts[9] + ' ' + Long.toHexString(offset) + TEXT_SEPARATOR + region2.getType() + ' ' + region2.getInternalName());
                                            item.address = address;
                                        }
                                    } else {
                                        Log.d("ASLR: " + Long.toHexString(item.address) + " ??? " + Long.toHexString(region.start + offset) + TEXT_SEPARATOR + parts[8] + ' ' + parts[9] + ' ' + Long.toHexString(offset) + TEXT_SEPARATOR + region.getType() + ' ' + region.getInternalName());
                                    }
                                }
                                if (useValues) {
                                    item.alter();
                                }
                                savedList.add(item, (byte) 0, false);
                            } catch (Throwable e2) {
                                Log.w("Failed parse line: '" + line + SearchMenuItem.HEX_UTF_8, e2);
                            }
                        }
                    }
                } catch (OutOfMemoryError e3) {
                    Log.w("OOM on load list", e3);
                }
            }
            reader.close();
            savedList.notifyDataSetChanged();
            savedList.reloadData();
            if (!sameRun && (mode & 4) == 0) {
                Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.load_list)).setMessage(Re.s(R.string.load_warning)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
            }
        }
    }

    private static boolean updateOldList(String pkg) {
        try {
            SharedPreferences storage = MainService.instance.getSharedPreferences(pkg, 0);
            int size = storage.getInt("size", 0);
            SavedItem[] list = new SavedItem[size];
            for (int i = 0; i < size; i++) {
                try {
                    list[i] = new SavedItem(storage.getLong(String.valueOf(i) + "-address", 0L), storage.getLong(String.valueOf(i) + "-data", 0L), storage.getInt(String.valueOf(i) + "-flags", 4), storage.getString(String.valueOf(i) + "-name", null), storage.getBoolean(String.valueOf(i) + "-freeze", false), (byte) storage.getInt(String.valueOf(i) + "-freezeType", 0), storage.getLong(String.valueOf(i) + "-freezeFrom", 0L), storage.getLong(String.valueOf(i) + "-freezeTo", 0L));
                } catch (Exception e) {
                    Log.w("Failed load item: " + i, e);
                }
            }
            int pid = storage.getInt("pid", 0);
            int i2 = 0;
            while (i2 < 10) {
                File file = new File(PkgPath.path(null, LIST_PATH), String.valueOf(pkg) + (i2 == 0 ? "" : Integer.valueOf(i2 + 45)) + ".txt");
                if (!file.exists()) {
                    try {
                        saveList(pid, list, file.getAbsolutePath(), 0);
                        if (file.exists()) {
                            return true;
                        }
                    } catch (IOException e2) {
                        Log.w("Failed save saved list", e2);
                    }
                }
                i2++;
            }
        } catch (Exception e3) {
            Log.w("Failed load saved list", e3);
        }
        return false;
    }

    public static void updateOldLists() {
        String[] list;
        File folder = Uninstaller.getSharedPrefsFile("tmp").getParentFile();
        if (folder.exists() && (list = folder.list()) != null) {
            for (String file : list) {
                if (file != null && !file.endsWith("_preferences.xml") && file.endsWith(".xml")) {
                    String name = file.substring(0, file.length() - 4);
                    if (!name.equals(MainService.DEFAULT_STORAGE) && !name.equals(MainService.OLD_DEFAULT_STORAGE)) {
                        Log.d("Try convert '" + name + "'.");
                        if (updateOldList(name)) {
                            Log.d("All ok - remove '" + name + "'.");
                            new File(folder, file).delete();
                        }
                    }
                }
            }
        }
    }

    public ListManager(ProcessList.ProcessInfo processInfo, SavedItem[] list) {
        if (processInfo == null) {
            Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.load_list)).setMessage(Re.s(R.string.no_application)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
            return;
        }
        if (list != null && list.length == 0) {
            Alert.show(Alert.create().setMessage(Re.s(R.string.empty_save)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
            return;
        }
        MainService.instance.mDaemonManager.getRegionList();
        this.processInfo = processInfo;
        this.list = list;
        boolean load = list == null;
        View v = LayoutInflater.inflateStatic(R.layout.save, (ViewGroup) null);
        TextView message = (TextView) v.findViewById(R.id.message);
        message.setText(Re.s(load ? R.string.load_saved_list : R.string.save_saved_list));
        EditTextPath edit = (EditTextPath) v.findViewById(R.id.file);
        this.edit = edit;
        edit.setText(PkgPath.load(LIST_PATH, "-list", ".txt"));
        edit.setDataType(4);
        edit.setPathType(load ? 1 : 2);
        v.findViewById(R.id.path_selector).setTag(edit);
        if (load) {
            v.findViewById(R.id.load_stuff).setVisibility(0);
        } else {
            v.findViewById(R.id.save_stuff).setVisibility(0);
        }
        RadioGroup group = (RadioGroup) v.findViewById(R.id.mode);
        this.group = group;
        if (lastMode > 0) {
            group.check(lastMode);
        }
        ((RadioButton) v.findViewById(R.id.mode_0)).setText(Re.s(R.string.load_0));
        ((RadioButton) v.findViewById(R.id.mode_1)).setText(Re.s(R.string.load_1));
        ((RadioButton) v.findViewById(R.id.mode_2)).setText(Re.s(R.string.load_2));
        CheckBox asText = (CheckBox) v.findViewById(R.id.as_text);
        this.asText = asText;
        Tools.setButtonHelpBackground(asText);
        asText.setOnLongClickListener(this);
        AlertDialog.Builder builder = Alert.create().setView(InternalKeyboard.getView(v, false)).setPositiveButton(Re.s(load ? R.string.load : R.string.save), this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null);
        if (load && MainService.instance.savedList.getCount() != 0) {
            builder.setNeutralButton(Re.s(R.string.append), this);
        }
        AlertDialog dialog = builder.create();
        this.dialog = dialog;
        Alert.setOnShowListener(dialog, this);
        Alert.setOnDismissListener(dialog, this);
        Alert.show(dialog, edit);
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        if (v == null) {
            return false;
        }
        switch (v.getId()) {
            case R.id.as_text /* 2131427422 */:
                Alert.show(Alert.create().setMessage(Re.s(R.string.as_text_help)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                return true;
            default:
                return false;
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        android.widget.EditText edit = this.edit;
        if (edit != null) {
            PkgPath.save(edit.getText().toString(), LIST_PATH, "-list", ".txt");
        }
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialog) {
        Tools.setButtonListener(dialog, -1, null, this);
        Tools.setButtonListener(dialog, -3, -3, this);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        Button btn = ((AlertDialog) dialog).getButton(which);
        if (btn != null) {
            onClick(btn);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        int mode;
        android.widget.EditText edit = this.edit;
        if (v != null && edit != null) {
            String file = edit.getText().toString().trim();
            if (Tools.isPath(file)) {
                boolean load = this.list == null;
                boolean exists = false;
                if (load) {
                    try {
                        exists = Tools.isFile(file);
                    } catch (Throwable e) {
                        Log.badImplementation(e);
                    }
                }
                if (!load || exists) {
                    History.add(file, 4);
                    if (load) {
                        MainService.instance.lockApp();
                        RadioGroup group = this.group;
                        if (group != null) {
                            int id = group.getCheckedRadioButtonId();
                            lastMode = id;
                            switch (id) {
                                case R.id.mode_1 /* 2131427426 */:
                                    mode = 2;
                                    break;
                                case R.id.mode_2 /* 2131427427 */:
                                    mode = 0;
                                    break;
                                default:
                                    mode = 3;
                                    break;
                            }
                            if (v.getTag() instanceof Integer) {
                                mode |= 8;
                            }
                            try {
                                loadList(this.processInfo.pid, file, mode);
                                Tools.dismiss(this.dialog);
                                Record record = MainService.instance.currentRecord;
                                if (record != null) {
                                    record.write("gg.loadList(");
                                    Script.Consts.logString(record, file);
                                    record.write(", ");
                                    Script.Consts.logConst(record, record.consts.LOAD_, mode);
                                    record.write(")\n");
                                    return;
                                }
                                return;
                            } catch (IOException e) {
                                Log.w("Failed load list", e);
                                Tools.showToast(e.getMessage());
                                return;
                            }
                        }
                        return;
                    }
                    CheckBox asText = this.asText;
                    if (asText != null) {
                        try {
                            int mode2 = asText.isChecked() ? 1 : 0;
                            saveList(this.processInfo.pid, this.list, file, mode2);
                            Tools.dismiss(this.dialog);
                            Record record2 = MainService.instance.currentRecord;
                            if (record2 != null) {
                                if (this.fromExport) {
                                    record2.write("\nlocal prev = gg.getListItems()\n");
                                    record2.write("gg.clearList()\n");
                                    record2.write("local t = ");
                                    Converter.recordGetResults(record2, true);
                                    record2.write("gg.addListItems(t)\n");
                                    record2.write("t = nil\n");
                                }
                                record2.write("gg.saveList(");
                                Script.Consts.logString(record2, file);
                                record2.write(", ");
                                Script.Consts.logConst(record2, record2.consts.SAVE_, mode2);
                                record2.write(")");
                                if (this.fromExport) {
                                    record2.write("\ngg.clearList()\n");
                                    record2.write("gg.addListItems(prev)\n");
                                    record2.write("prev = nil\n");
                                }
                                record2.write(NEW_LINE);
                            }
                        } catch (IOException e2) {
                            Log.w("Failed save list", e2);
                            Tools.showToast(e2.getMessage());
                        }
                    }
                }
            }
        }
    }
}
