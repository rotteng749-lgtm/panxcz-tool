package android.ext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.fix.Button;
import android.fix.LayoutInflater;
import android.fix.ListView;
import android.fix.TextView;
import android.os.Build;
import android.sup.ContainerHelpers;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import catch_.me_.if_.you_.can_.R;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class PathSelector extends Button implements View.OnClickListener {
    public static final int FILE = 1;
    public static final int FOLDER = 0;
    public static final int NEW_FILE = 2;
    static boolean sortByName = true;

    public PathSelector(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public PathSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PathSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PathSelector(Context context) {
        super(context);
        init();
    }

    private void init() {
        setText("...");
        setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        new Impl(this, null).onClick(v);
    }

    static class ViewHolder {
        public TextView date;
        public ImageView icon;
        public TextView name;
        public TextView size;

        ViewHolder() {
        }
    }

    private class Impl implements View.OnClickListener, AdapterView.OnItemClickListener, DialogInterface.OnClickListener {
        private ArrayAdapter<File> adapter;
        private int colorLasm;
        private int colorLua;
        private int colorTxt;
        private android.widget.EditText create;
        private File currentPath;
        private AlertDialog dialog;
        private android.widget.EditText edit;
        private TextView failed;
        ListView files;
        private TextView path;
        private int pathType;
        private ImageView sort;

        private Impl() {
            this.colorLua = Tools.getColor(R.color.file_lua, -16755456);
            this.colorTxt = Tools.getColor(R.color.file_txt, -16777131);
            this.colorLasm = Tools.getColor(R.color.file_lasm, -16755371);
        }

        /* synthetic */ Impl(PathSelector pathSelector, Impl impl) {
            this();
        }

        private void up() {
            this.currentPath = this.currentPath.getParentFile();
            if (this.currentPath == null) {
                this.currentPath = new File("/");
            }
        }

        private void go(String folder) {
            this.currentPath = new File(this.currentPath, folder);
        }

        private void update() {
            this.path.setText(this.currentPath.getAbsolutePath());
            String[] list = PathSelector.list(this.currentPath);
            setFailedLoadList(list == null);
            if (list == null) {
                list = ContainerHelpers.EMPTY_STRINGS;
            }
            ArrayList<SortItem> items = PathSelector.sort(this.pathType, this.currentPath, list, null, this.sort.getTag() == null);
            this.adapter.clear();
            this.adapter.setNotifyOnChange(false);
            for (SortItem item : items) {
                this.adapter.add(item.file);
            }
            this.adapter.notifyDataSetChanged();
            this.adapter.setNotifyOnChange(true);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            File file;
            boolean isDir;
            long length;
            long lastModified;
            if (convertView == null) {
                view = LayoutInflater.from(PathSelector.this.getContext()).inflate(R.layout.path_item, (ViewGroup) null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.name = (TextView) view.findViewById(R.id.name);
                viewHolder.date = (TextView) view.findViewById(R.id.date);
                viewHolder.size = (TextView) view.findViewById(R.id.size);
                viewHolder.icon = Config.setIconSize((ImageView) view.findViewById(R.id.icon));
                view.setTag(viewHolder);
            } else {
                view = convertView;
            }
            try {
                file = this.adapter.getItem(position);
            } catch (IndexOutOfBoundsException e) {
                Log.w("Failed get item: " + position, e);
                file = null;
            }
            if (file == null) {
                file = new File(this.currentPath, "???");
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            String name = file.getName();
            if (name.endsWith(".lua")) {
                view.setBackgroundColor(this.colorLua);
            } else if (name.endsWith(".txt")) {
                view.setBackgroundColor(this.colorTxt);
            } else if (name.endsWith(".lasm")) {
                view.setBackgroundColor(this.colorLasm);
            } else {
                view.setBackgroundColor(0);
            }
            holder.name.setText(name);
            try {
                isDir = PathSelector.isDir(file);
                length = isDir ? -1L : file.length();
                lastModified = file.lastModified();
            } catch (Throwable e2) {
                Log.w("Failed get info: " + file.getAbsolutePath(), e2);
                isDir = false;
                length = -1;
                lastModified = -1;
            }
            holder.icon.setImageResource(isDir ? R.drawable.ic_folder_outline_white_24dp : R.drawable.ic_file_white_24dp);
            holder.size.setText(length >= 0 ? Tools.formatFileSize(PathSelector.this.getContext(), length) : "");
            holder.date.setText(lastModified > 0 ? DateFormat.format("yyyy-MM-dd kk:mm:ss", lastModified) : "");
            return view;
        }

        private void create(String name) {
            android.widget.EditText create = new EditText(Tools.getContext());
            this.create = create;
            create.setText(name);
            Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(Re.s(R.string.folder_new), String.valueOf(this.currentPath.getAbsolutePath()) + "/...")).setView(InternalKeyboard.getView(create, false)).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).setPositiveButton(Re.s(R.string.ok), this));
        }

        private void scrollTo(File src, boolean isDir) {
            ArrayAdapter<File> adapter = this.adapter;
            int cnt = adapter.getCount();
            int best = 0;
            String abs = src.getAbsolutePath().toLowerCase(Locale.US);
            int i = 0;
            while (true) {
                if (i >= cnt) {
                    break;
                }
                File curr = adapter.getItem(i);
                if (src.equals(curr)) {
                    best = i;
                    break;
                }
                if (isDir == PathSelector.isDir(curr) && abs.compareTo(curr.getAbsolutePath().toLowerCase(Locale.US)) > 0) {
                    best = i;
                }
                i++;
            }
            scrollTo(best);
        }

        private void scrollTo(final int best) {
            this.files.setSelection(best);
            this.files.post(new Runnable() { // from class: android.ext.PathSelector.Impl.1
                @Override // java.lang.Runnable
                public void run() {
                    Impl.this.files.setSelection(best);
                }
            });
        }

        private void setSort(boolean byName) {
            this.sort.setTag(byName ? null : this.sort);
            this.sort.setImageDrawable(Tools.getDrawable(byName ? R.drawable.ic_sort_name_white_24dp : R.drawable.ic_sort_date_white_24dp));
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add /* 2131427412 */:
                    create("");
                    break;
                case R.id.sort /* 2131427413 */:
                    boolean z = this.sort.getTag() != null;
                    PathSelector.sortByName = z;
                    setSort(z);
                    update();
                    scrollTo(0);
                    break;
                case R.id.up /* 2131427414 */:
                    File src = this.currentPath;
                    up();
                    update();
                    scrollTo(src, true);
                    break;
                default:
                    Object tag = PathSelector.this.getTag();
                    if (tag instanceof EditTextPath) {
                        EditTextPath edit = (EditTextPath) tag;
                        this.edit = edit;
                        View view = LayoutInflater.inflateStatic(R.layout.path_selector, (ViewGroup) null);
                        this.path = (TextView) view.findViewById(R.id.path);
                        Config.setIconSize((ImageView) view.findViewById(R.id.add)).setOnClickListener(this);
                        ImageView imageView = (ImageView) view.findViewById(R.id.sort);
                        this.sort = imageView;
                        Config.setIconSize(imageView).setOnClickListener(this);
                        setSort(PathSelector.sortByName);
                        Config.setIconSize((ImageView) view.findViewById(R.id.up)).setOnClickListener(this);
                        this.failed = (TextView) view.findViewById(R.id.failed);
                        ListView files = (ListView) view.findViewById(R.id.files);
                        this.files = files;
                        ArrayAdapter<File> adapter = new ArrayAdapter<File>(PathSelector.this.getContext(), new ArrayList()) { // from class: android.ext.PathSelector.Impl.2
                            @Override // android.ext.ArrayAdapter, android.widget.ArrayAdapter, android.widget.Adapter
                            public View getView(int position, View convertView, ViewGroup parent) {
                                return Impl.this.getView(position, convertView, parent);
                            }
                        };
                        this.adapter = adapter;
                        files.setAdapter((ListAdapter) adapter);
                        files.setOnItemClickListener(this);
                        this.pathType = edit.getPathType();
                        File src2 = new File(edit.getText().toString());
                        this.currentPath = src2;
                        while (!PathSelector.isDir(this.currentPath)) {
                            up();
                        }
                        update();
                        scrollTo(src2, this.pathType == 0);
                        AlertDialog.Builder builder = Alert.create().setView(view).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null);
                        if (this.pathType != 1) {
                            builder.setPositiveButton(Re.s(R.string.ok), this);
                        }
                        AlertDialog dialog = builder.create();
                        this.dialog = dialog;
                        Alert.show(dialog);
                        break;
                    }
                    break;
            }
        }

        private void setFailedLoadList(boolean status) {
            this.failed.setVisibility(status ? 0 : 8);
            this.files.setVisibility(status ? 8 : 0);
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position >= 0 && position < this.adapter.getCount()) {
                try {
                    File file = this.adapter.getItem(position);
                    if (file != null) {
                        if (PathSelector.isDir(file)) {
                            go(file.getName());
                            update();
                            scrollTo(0);
                        } else if (this.pathType != 0) {
                            this.edit.setText(file.getAbsolutePath());
                            Tools.dismiss(this.dialog);
                        }
                    }
                } catch (Exception e) {
                    Log.w("Failed get Item: " + position, e);
                }
            }
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            if (dialog == this.dialog) {
                String text = this.currentPath.getAbsolutePath();
                if (this.pathType == 2) {
                    String name = new File(this.edit.getText().toString()).getName();
                    if (name == null || name.length() == 0) {
                        name = "file.txt";
                    }
                    text = String.valueOf(text) + "/" + name;
                }
                this.edit.setText(text);
                return;
            }
            android.widget.EditText create = this.create;
            if (create != null) {
                String name2 = create.getText().toString().trim();
                if (name2.length() > 0) {
                    File newFolder = new File(this.currentPath, name2);
                    if (newFolder.exists()) {
                        if (PathSelector.isDir(newFolder)) {
                            Tools.showToast(R.string.folder_exists);
                        } else {
                            Tools.showToast(R.string.folder_file);
                        }
                        create(name2);
                        return;
                    }
                    if (!newFolder.mkdirs()) {
                        Tools.showToast(R.string.folder_failed);
                        create(name2);
                        return;
                    } else {
                        History.add(name2, 4);
                        update();
                        return;
                    }
                }
                Tools.showToast(R.string.folder_failed);
                create(name2);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x002f A[Catch: Throwable -> 0x00a1, TryCatch #0 {Throwable -> 0x00a1, blocks: (B:10:0x000d, B:12:0x0015, B:21:0x005d, B:15:0x002f, B:17:0x0035, B:18:0x004c, B:19:0x0053, B:23:0x0062, B:25:0x0068, B:30:0x0089, B:32:0x008f, B:27:0x006e, B:29:0x0086), top: B:39:0x000d }] */
    /* JADX WARN: Removed duplicated region for block: B:18:0x004c A[Catch: Throwable -> 0x00a1, PHI: r0
  0x004c: PHI (r0v5 'check' java.lang.String) = (r0v4 'check' java.lang.String), (r0v6 'check' java.lang.String) binds: [B:14:0x002d, B:17:0x0035] A[DONT_GENERATE, DONT_INLINE], TryCatch #0 {Throwable -> 0x00a1, blocks: (B:10:0x000d, B:12:0x0015, B:21:0x005d, B:15:0x002f, B:17:0x0035, B:18:0x004c, B:19:0x0053, B:23:0x0062, B:25:0x0068, B:30:0x0089, B:32:0x008f, B:27:0x006e, B:29:0x0086), top: B:39:0x000d }] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0062 A[Catch: Throwable -> 0x00a1, TryCatch #0 {Throwable -> 0x00a1, blocks: (B:10:0x000d, B:12:0x0015, B:21:0x005d, B:15:0x002f, B:17:0x0035, B:18:0x004c, B:19:0x0053, B:23:0x0062, B:25:0x0068, B:30:0x0089, B:32:0x008f, B:27:0x006e, B:29:0x0086), top: B:39:0x000d }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void addPath(java.lang.String r12, java.util.ArrayList<java.lang.String> r13, java.lang.String r14, java.lang.String r15) {
        /*
            r8 = 1
            if (r15 != 0) goto L4
        L3:
            return
        L4:
            r0 = r15
            r5 = 0
            if (r14 != 0) goto L60
            r7 = r8
        L9:
            if (r5 >= r7) goto L3
            if (r5 != r8) goto L2c
            java.lang.String r9 = "/data/data/"
            boolean r9 = r15.startsWith(r9)     // Catch: java.lang.Throwable -> La1
            if (r9 == 0) goto L5d
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> La1
            java.lang.String r10 = java.lang.String.valueOf(r14)     // Catch: java.lang.Throwable -> La1
            r9.<init>(r10)     // Catch: java.lang.Throwable -> La1
            r10 = 11
            java.lang.String r10 = r15.substring(r10)     // Catch: java.lang.Throwable -> La1
            java.lang.StringBuilder r9 = r9.append(r10)     // Catch: java.lang.Throwable -> La1
            java.lang.String r0 = r9.toString()     // Catch: java.lang.Throwable -> La1
        L2c:
            r9 = 2
            if (r5 != r9) goto L4c
            boolean r9 = r15.startsWith(r14)     // Catch: java.lang.Throwable -> La1
            if (r9 == 0) goto L5d
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> La1
            java.lang.String r10 = "/data/data/"
            r9.<init>(r10)     // Catch: java.lang.Throwable -> La1
            int r10 = r14.length()     // Catch: java.lang.Throwable -> La1
            java.lang.String r10 = r15.substring(r10)     // Catch: java.lang.Throwable -> La1
            java.lang.StringBuilder r9 = r9.append(r10)     // Catch: java.lang.Throwable -> La1
            java.lang.String r0 = r9.toString()     // Catch: java.lang.Throwable -> La1
        L4c:
            java.io.File r4 = new java.io.File     // Catch: java.lang.Throwable -> La1
            r4.<init>(r0)     // Catch: java.lang.Throwable -> La1
            r1 = 0
            r2 = r1
        L53:
            java.lang.String r6 = r4.getAbsolutePath()     // Catch: java.lang.Throwable -> La1
            boolean r9 = r6.startsWith(r12)     // Catch: java.lang.Throwable -> La1
            if (r9 != 0) goto L62
        L5d:
            int r5 = r5 + 1
            goto L9
        L60:
            r7 = 3
            goto L9
        L62:
            boolean r9 = r4.isDirectory()     // Catch: java.lang.Throwable -> La1
            if (r9 != 0) goto L6e
            boolean r9 = r4.isFile()     // Catch: java.lang.Throwable -> La1
            if (r9 == 0) goto L89
        L6e:
            int r9 = r12.length()     // Catch: java.lang.Throwable -> La1
            java.lang.String r9 = r6.substring(r9)     // Catch: java.lang.Throwable -> La1
            java.lang.String r10 = "/"
            r11 = -1
            java.lang.String[] r9 = r9.split(r10, r11)     // Catch: java.lang.Throwable -> La1
            r10 = 0
            r6 = r9[r10]     // Catch: java.lang.Throwable -> La1
            boolean r9 = r13.contains(r6)     // Catch: java.lang.Throwable -> La1
            if (r9 != 0) goto L89
            r13.add(r6)     // Catch: java.lang.Throwable -> La1
        L89:
            java.io.File r4 = r4.getParentFile()     // Catch: java.lang.Throwable -> La1
            if (r4 == 0) goto L5d
            java.lang.String r9 = r4.getAbsolutePath()     // Catch: java.lang.Throwable -> La1
            int r9 = r9.length()     // Catch: java.lang.Throwable -> La1
            if (r9 <= r8) goto L5d
            int r1 = r2 + 1
            r9 = 20
            if (r2 >= r9) goto L5d
            r2 = r1
            goto L53
        La1:
            r3 = move-exception
            android.ext.Log.badImplementation(r3)
            goto L3
        */
        throw new UnsupportedOperationException("Method not decompiled: android.ext.PathSelector.addPath(java.lang.String, java.util.ArrayList, java.lang.String, java.lang.String):void");
    }

    static boolean isDir(File file) {
        try {
            return file.isDirectory();
        } catch (Throwable th) {
            return false;
        }
    }

    static String[] list(File dir) {
        String[] ret = null;
        try {
            ret = dir.list();
        } catch (SecurityException e) {
            Log.w("Failed list dir: " + dir.getAbsolutePath(), e);
        }
        if (ret == null) {
            try {
                String path = dir.getAbsolutePath();
                String data = path.startsWith("/data/user/") ? "/data/user/" + path.split("/", -1)[3] + "/" : "/data/user/0/";
                String path2 = String.valueOf(path) + '/';
                ArrayList<String> list = new ArrayList<>();
                addPath(path2, list, data, "/data/anr");
                addPath(path2, list, data, "/data/app");
                addPath(path2, list, data, "/data/app-lib");
                addPath(path2, list, data, "/data/app-private");
                addPath(path2, list, data, "/data/dalvik-cache/profiles");
                addPath(path2, list, data, "/data/dalvik-cache/x86");
                addPath(path2, list, data, "/data/dalvik-cache/armeabi");
                addPath(path2, list, data, "/data/dalvik-cache/armeabi-v7a");
                addPath(path2, list, data, "/data/dalvik-cache/arm");
                addPath(path2, list, data, "/data/dalvik-cache/arm64-v8a");
                addPath(path2, list, data, "/data/dalvik-cache/arm64");
                addPath(path2, list, data, "/data/dalvik-cache/x86");
                addPath(path2, list, data, "/data/dalvik-cache/x86_64");
                addPath(path2, list, data, "/data/local/tmp");
                addPath(path2, list, data, "/data/misc/user");
                addPath(path2, list, data, "/data/misc/zoneinfo");
                addPath(path2, list, data, "/data/resource-cache");
                addPath(path2, list, data, "/data/security");
                addPath(path2, list, data, "/data/system/cache");
                addPath(path2, list, data, "/data/system/registered_services");
                addPath(path2, list, data, "/data/system/shared_prefs");
                addPath(path2, list, data, "/data/system/users");
                addPath(path2, list, data, "/data/tmp");
                addPath(path2, list, data, data);
                addPath(path2, list, data, "/storage/emulated/legacy");
                addPath(path2, list, data, "/storage/emulated/0");
                addPath(path2, list, data, "/storage/emulated/sdcard0");
                addPath(path2, list, data, "/storage/emulated/sdcard1");
                addPath(path2, list, data, "/storage/sdcard0");
                addPath(path2, list, data, "/storage/sdcard1");
                addPath(path2, list, data, "/sdcard");
                addPath(path2, list, data, Tools.getSdcardPath());
                for (ApplicationInfo ai : Tools.getPackageManager().getInstalledApplications(0)) {
                    addPath(path2, list, data, ai.dataDir);
                    addPath(path2, list, data, ai.nativeLibraryDir);
                    addPath(path2, list, data, ai.publicSourceDir);
                    addPath(path2, list, data, ai.sourceDir);
                    if (Build.VERSION.SDK_INT >= 21 && ai.splitPublicSourceDirs != null) {
                        for (String check : ai.splitPublicSourceDirs) {
                            addPath(path2, list, data, check);
                        }
                    }
                    if (Build.VERSION.SDK_INT >= 24) {
                        addPath(path2, list, data, ai.deviceProtectedDataDir);
                    }
                    addPath(path2, list, data, "/data/data/" + ai.packageName);
                    if (Build.VERSION.SDK_INT >= 21) {
                        addPath(path2, list, data, "/data/app/" + ai.packageName + "-1/base.apk");
                        addPath(path2, list, data, "/data/app/" + ai.packageName + "-2/base.apk");
                    } else {
                        addPath(path2, list, data, "/data/app/" + ai.packageName + "-1.apk");
                        addPath(path2, list, data, "/data/app/" + ai.packageName + "-2.apk");
                    }
                }
                String[] ret2 = list.size() == 0 ? null : (String[]) list.toArray(new String[list.size()]);
                return ret2;
            } catch (Throwable e2) {
                Log.badImplementation(e2);
                return null;
            }
        }
        return ret;
    }

    static class SortItem {
        final File file;
        final boolean isDir;
        final String name;

        SortItem(File file, String name, boolean isDir) {
            this.file = file;
            this.name = name;
            this.isDir = isDir;
        }
    }

    static ArrayList<SortItem> sort(int pathType, File parent, String[] list, String start, final boolean byName) {
        ArrayList<SortItem> items = new ArrayList<>();
        for (String name : list) {
            String lower = name.toLowerCase(Locale.US);
            if (start == null || lower.startsWith(start)) {
                File file = new File(parent, name);
                boolean isDir = isDir(file);
                if (pathType != 0 || isDir) {
                    items.add(new SortItem(file, lower, isDir));
                }
            }
        }
        if (items.size() > 1) {
            Comparator<SortItem> cmp = new Comparator<SortItem>() { // from class: android.ext.PathSelector.1
                @Override // java.util.Comparator
                public int compare(SortItem o1, SortItem o2) {
                    int iCompare = -1;
                    boolean isDir1 = o1.isDir;
                    boolean isDir2 = o2.isDir;
                    if (isDir1 != isDir2) {
                        return isDir1 ? -1 : 1;
                    }
                    try {
                        if (byName) {
                            iCompare = NaturalOrderComparator.compare(o1.name, o2.name);
                        } else if (o1.file.lastModified() <= o2.file.lastModified()) {
                            iCompare = 1;
                        }
                        return iCompare;
                    } catch (Throwable e) {
                        Log.badImplementation(e);
                        return 0;
                    }
                }
            };
            try {
                long tmr = -System.currentTimeMillis();
                Collections.sort(items, cmp);
                Log.d("Sort " + items.size() + " in " + (tmr + System.currentTimeMillis()));
            } catch (IllegalArgumentException e) {
                final StringBuilder log = new StringBuilder();
                log.append("Failed sort:\n");
                Comparator<SortItem> cmp2 = new Comparator<SortItem>() { // from class: android.ext.PathSelector.2
                    @Override // java.util.Comparator
                    public int compare(SortItem o1, SortItem o2) {
                        int ret;
                        boolean isDir1 = o1.isDir;
                        boolean isDir2 = o2.isDir;
                        if (isDir1 != isDir2) {
                            ret = isDir1 ? -1 : 1;
                        } else {
                            ret = NaturalOrderComparator.compare(o1.name, o2.name);
                        }
                        log.append(isDir1 ? 'D' : 'F');
                        log.append(':');
                        log.append(o1);
                        log.append(':');
                        log.append(o1.name);
                        log.append('\n');
                        log.append(isDir2 ? 'D' : 'F');
                        log.append(':');
                        log.append(o2);
                        log.append(':');
                        log.append(o2.name);
                        log.append('\n');
                        log.append(ret);
                        log.append('\n');
                        return ret;
                    }
                };
                try {
                    Collections.sort(items, cmp2);
                } catch (IllegalArgumentException e2) {
                    ExceptionHandler.sendMessage(log.toString());
                }
                Log.badImplementation(e);
            }
        }
        return items;
    }
}
