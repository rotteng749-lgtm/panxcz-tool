package android.ext;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.ext.AddressItemSet;
import android.ext.BaseActivity;
import android.ext.ExceptionHandler;
import android.ext.ListSpeed;
import android.ext.MemoryHistory;
import android.ext.ProcessList;
import android.ext.RegionList;
import android.ext.Tools;
import android.fix.ContextWrapper;
import android.fix.LayoutInflater;
import android.fix.SparseArray;
import android.fix.WrapLayout;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.sup.ArrayListResults;
import android.sup.LongSparseArrayChecked;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import luaj.compiler.Constants;

/* JADX INFO: loaded from: classes.dex */
public class MainService extends ServiceBase implements View.OnClickListener, View.OnLongClickListener {
    public static final String MEM_DEV_CHECK_FAIL = "mem-fail";
    public static final int NOTIFY_ID = 1;
    public static final String OLD_DEFAULT_STORAGE = "DefaultStorage";
    public static final int TAB_MEMORY_EDITOR = 3;
    public static final int TAB_SAVED_LIST = 2;
    public static final int TAB_SEARCH = 1;
    public static final int TAB_SETTINGS = 0;
    public static final String TAG = "AndroidService";
    public static final String TEMP_PATH_KEY = "temp-path";
    public static final int USED_LISTS = 4;
    private static volatile ComponentCallbacks cc;
    private static volatile boolean checkLibs;
    public static volatile Context context;
    private static volatile boolean firstGetAfterSearch;
    private static int lastFlags;
    private static long lastToastTime;
    static int lastType;
    private static volatile boolean onConfigurationChangedSemaphore;
    private boolean alertPaused;
    Record currentRecord;
    Script currentScript;
    volatile boolean dialogDeadShow;
    FilterButtonListener filterListener;
    FuzzyButtonListener fuzzyListener;
    HotPoint hotPoint;
    public ListSpeed listSpeed;
    ListView mAddrListView;
    final ArrayListResults mAddressList;
    AppDetector mAppDetector;
    private ImageView mAppImage;
    TextView mAppName;
    private ImageView mBackButton;
    BusyDialog mBusyDialog;
    ConfigListAdapter mConfigListAdapter;
    ListView mConfigListView;
    View mConfigPage;
    ExplorerPanel mExplorerPanel;
    View mCountWait;
    DaemonManager mDaemonManager;
    TextView mFilter;
    TextView mFoundCount;
    private boolean mHasNotLocked;
    View mInfoCnt;
    View mInfoMem;
    View mInfoRow;
    ImageButton mListRefreshButton;
    TabHost mMainScreen;
    View mMemEditorPage;
    MemoryContentAdapter mMemListAdapter;
    ListView mMemListView;
    View mMenuButton;
    ImageView mMenuButtonLand;
    private ImageView mMoreButton;
    TextView mNothingFoundText;
    private ImageButton mProcessPauseButton;
    volatile long mResultCount;
    TextView mSavedFormat;
    ListView mSavedListView;
    View mSavedPage;
    View mSearchPage;
    private ImageView mSmallImage;
    TextView mValueFormat;
    WindowManager mWindowManager;
    volatile MainDialog mainDialog;
    MaskButtonListener maskListener;
    volatile int memFree;
    volatile int memTotal;
    MemoryHistory memoryHistory;
    private boolean needPtraceNotify;
    private boolean noX86;
    private final int[] oldBkg;
    private volatile ArrayList<Runnable> pendingScriptEnds;
    private boolean processHasBeenPaused;
    public ProcessList.ProcessInfo processInfo;
    boolean processPaused;
    boolean processRealPaused;
    TimersEditor randEditor;
    View recordInterrupt;
    Runnable refreshTabRunner;
    final AddressItemSet revertMap;
    private TextView savedCounter;
    SavedListAdapter savedList;
    private final Runnable savedListUpdate;
    View scriptInterrupt;
    View scriptUiButton;
    boolean scriptUiButtonClicked;
    private TextView searchCounter;
    SearchButtonListener searchListener;
    View selectedTab;
    ShowApp showApp;
    boolean showSearchHint;
    private SparseBooleanArray showedErrors;
    TextView statusBar;
    TimeJumpPanel timeJumpPanel;
    TimersEditor timersEditor;
    View toolbarRow;
    final WrapLayout[] toolbars;
    boolean usedFuzzy;
    private final UsedList[] usedLists;
    MenuItem valueFormat;
    public static final String DEFAULT_STORAGE = String.valueOf(Apk.PACKAGE) + "_preferences";
    public static volatile MainService instance = null;
    public static volatile boolean stopped = false;
    public static volatile boolean created = false;
    public static final MenuItem[][] menuItems = new MenuItem[3][];
    private static volatile boolean searchHelperRestore = false;
    public static final boolean[] DEFAULT_TIMERS = new boolean[176];
    public static final boolean[] DEFAULT_RAND = new boolean[76];

    static {
        for (int i = 0; i < DEFAULT_RAND.length; i++) {
            DEFAULT_RAND[i] = i % 4 < 2;
        }
        for (int i2 = 0; i2 < DEFAULT_TIMERS.length; i2++) {
            DEFAULT_TIMERS[i2] = i2 % 4 < 2;
        }
        int[] badTimers = {40, 43, 44};
        for (int i3 : badTimers) {
            int i4 = i3 - 1;
            DEFAULT_TIMERS[(i4 * 4) + 0] = false;
            DEFAULT_TIMERS[(i4 * 4) + 1] = false;
            DEFAULT_TIMERS[(i4 * 4) + 2] = false;
            DEFAULT_TIMERS[(i4 * 4) + 3] = false;
        }
        lastFlags = AddressItem.FLAG_AUTO;
        lastType = 0;
        checkLibs = true;
        cc = null;
        onConfigurationChangedSemaphore = false;
        firstGetAfterSearch = true;
        lastToastTime = 0L;
    }

    public MainService(Context context2) {
        super(context2);
        this.currentScript = null;
        this.scriptInterrupt = null;
        this.scriptUiButton = null;
        this.scriptUiButtonClicked = false;
        this.currentRecord = null;
        this.recordInterrupt = null;
        this.timeJumpPanel = null;
        this.mainDialog = null;
        this.processPaused = false;
        this.processRealPaused = false;
        this.processHasBeenPaused = false;
        this.alertPaused = false;
        this.mHasNotLocked = true;
        this.memTotal = 0;
        this.memFree = 0;
        this.mResultCount = 0L;
        this.usedFuzzy = false;
        this.showSearchHint = true;
        this.mAddressList = new ArrayListResults();
        this.savedList = new SavedListAdapter();
        this.revertMap = new AddressItemSet();
        this.processInfo = null;
        this.showedErrors = new SparseBooleanArray();
        this.pendingScriptEnds = new ArrayList<>();
        this.oldBkg = new int[4];
        this.savedListUpdate = new Runnable() { // from class: android.ext.MainService.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (MainService.this.selectedTab == MainService.this.mSavedPage) {
                        MainService.this.savedList.reloadData();
                    }
                } catch (Throwable e) {
                    Log.e("Failed update saved list", e);
                }
                try {
                    int period = Config.savedListUpdatesInterval;
                    if (period <= 0) {
                        period = DaemonManager.TIME_JUMP_MUL_INT;
                    }
                    Handler handler = ThreadManager.getHandlerUiThread();
                    handler.removeCallbacks(this);
                    handler.postDelayed(this, period);
                } catch (Throwable e2) {
                    Log.w("Failed post timer", e2);
                }
            }
        };
        this.refreshTabRunner = null;
        this.needPtraceNotify = true;
        this.noX86 = false;
        this.usedLists = new UsedList[4];
        this.dialogDeadShow = false;
        if (context == null) {
            Context appContext = context2.getApplicationContext();
            if (appContext == null) {
                appContext = context2;
            }
            context = ServiceContext.wrap(appContext);
        }
        instance = this;
        Tools.checkForBadContext();
        this.mDaemonManager = new DaemonManager();
        this.toolbars = new WrapLayout[3];
    }

    static int getLastFlags() {
        return instance.mResultCount == 0 ? AddressItem.FLAG_AUTO : lastFlags;
    }

    static int getLastType() {
        return (instance.mResultCount == 0 || (lastFlags & lastType) == lastType) ? lastType : lastFlags;
    }

    static void setLastFlags(int flags, int seq) {
        int flags2 = flags & AddressItem.FLAG_AUTO;
        lastFlags = flags2;
        if (seq == 0) {
            lastType = flags2;
        }
    }

    void stopHotkeyDetection() {
        Log.d("stopHotkeyDetection: " + this.hotPoint);
        if (this.hotPoint != null) {
            this.hotPoint.hide();
        }
    }

    void startHotkeyDetection() {
        Log.d("startHotkeyDetection: " + this.hotPoint);
        if (this.hotPoint != null) {
            this.hotPoint.show();
            this.mDaemonManager.getMem();
        }
    }

    void notifyDataSetChanged(ListView view) {
        if (view != null) {
            notifyDataSetChanged(view.getAdapter());
        }
    }

    void notifyDataSetChanged(final ListAdapter listAdapter) {
        if (listAdapter != null) {
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.MainService.2
                @Override // java.lang.Runnable
                public void run() {
                    if (listAdapter instanceof ConfigListAdapter) {
                        ((ConfigListAdapter) listAdapter).updateData();
                    }
                    if (listAdapter instanceof BaseAdapter) {
                        try {
                            ((BaseAdapter) listAdapter).notifyDataSetChanged();
                        } catch (NoSuchMethodError e) {
                            Log.badImplementation(e);
                        }
                    }
                }
            });
        }
    }

    void detectApp(boolean force) {
        this.mAppDetector.detectApp(force);
    }

    void allocatedPage(final long page) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.MainService.3
            @Override // java.lang.Runnable
            public void run() {
                if (-1024 < page && page <= 0) {
                    Alert.show(Alert.create().setMessage(String.valueOf(Re.s(R.string.allocate_fail)) + ' ' + (-page)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null).create());
                    return;
                }
                final String address = String.valueOf(AddressItem.getStringAddress(page, 4)) + SearchMenuItem.HEX;
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { // from class: android.ext.MainService.3.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case -3:
                                Tools.copyText(address);
                                break;
                            case -1:
                                MainService.instance.goToAddress(null, address, Tools.stripColon(R.string.allocated));
                                break;
                        }
                    }
                };
                History.add(address, 1);
                Alert.show(Alert.create().setMessage(String.valueOf(Tools.stripColon(R.string.allocated)) + ": " + address).setPositiveButton(Re.s(R.string.goto1), listener).setNeutralButton(Re.s(R.string.copy), listener).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null).create());
            }
        });
    }

    void setMem(int total, int free) {
        this.memTotal = total;
        this.memFree = free;
    }

    public void setProcessInfo(ProcessList.ProcessInfo info) {
        boolean needReset = this.processInfo == null || info == null || this.processInfo.pid != info.pid;
        if (needReset) {
            checkLibs = true;
            reset();
        }
        Config.save();
        this.processInfo = info;
        Config.load();
        this.mConfigListAdapter.setGameName();
        if (info != null) {
            Drawable icon = Tools.tryCloneIcon(info.icon);
            this.mAppImage.setImageDrawable(icon);
            if (info.icon == null) {
                Log.e("setProcessInfo icon is null: " + info);
            }
            this.mAppName.setText(info.toString());
            Drawable icon2 = Tools.tryCloneIcon(info.icon);
            this.hotPoint.updateIcon(icon2);
            this.mDaemonManager.setPid(info.pid, info.packageName, info.libsPath);
        }
        this.timersEditor.load();
        this.randEditor.load();
        checkAutopause();
        if (needReset) {
            refreshTab();
        }
    }

    private String appendProcessInfo(String sMessage) {
        ProcessList.ProcessInfo pi = this.processInfo;
        if (pi != null) {
            return String.valueOf(sMessage) + "\n\n" + pi.name + "\n[" + pi.packageName + "]";
        }
        return sMessage;
    }

    public void onMenuPressed() {
        if (this.selectedTab == this.mSearchPage || this.selectedTab == this.mSavedPage || this.selectedTab == this.mMemEditorPage) {
            Tools.click(this.mMenuButton);
        }
    }

    void showDialog() {
        Log.d("ServiceProxy showDialog: " + this.mainDialog);
        if (this.mainDialog != null) {
            Log.d("Something going wrong", new RuntimeException("mainDialog != null"));
            this.mainDialog.dismiss();
        }
        this.mainDialog = new MainDialog(context, this.mMainScreen);
        for (int j = 0; j < this.toolbars.length; j++) {
            this.toolbars[j].setFill(Config.fillToolbar - 1);
        }
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.MainService.4
            @Override // java.lang.Runnable
            public void run() {
                MainService.this.mainDialog.show();
                MainService.this.mBusyDialog.show(false);
            }
        });
        this.showApp.show();
        this.mAppDetector.checkAppSelect(this.mHasNotLocked, null);
        this.hotPoint.hide();
        checkAutopause();
        this.mDaemonManager.getMem();
        if (!this.mBusyDialog.isVisible()) {
            refreshTab();
            if (searchHelperRestore) {
                searchHelperRestore = false;
                if (this.mAddressList.size() == 0 && this.mResultCount != 0) {
                    refreshResultList(false);
                }
                if (DaemonManager.lastSearchFuzzy) {
                    this.fuzzyListener.onClick(this.mMenuButton);
                } else {
                    this.searchListener.onClick(this.mMenuButton);
                }
            }
        }
        updateNotification();
        showScriptEnd(null);
    }

    private void checkAutopause() {
        if (this.mainDialog != null) {
            if (this.processHasBeenPaused || ((Config.configClient & 4) != 0 && this.mAppDetector.isAppSelectByUser())) {
                processPause(false);
            }
            updatePauseButton();
        }
    }

    void eye(boolean show) {
        if (this.mainDialog == null) {
            if (show) {
                this.showApp.show();
            } else {
                this.showApp.hide(true);
            }
        }
    }

    void dismissDialog() {
        dismissDialog(true);
    }

    void dismissDialog(boolean hideScript) {
        Log.d("ServiceProxy dismissDialog: " + this.mainDialog);
        if (this.mainDialog != null) {
            this.mBusyDialog.dismiss(false);
            this.mainDialog.dismiss();
            this.mainDialog = null;
            this.showApp.hide(hideScript);
            this.hotPoint.show();
            this.processHasBeenPaused = this.processPaused;
            boolean realPaused = this.processRealPaused;
            boolean hide_1 = (Config.configDaemon & 128) != 0;
            if (this.mBusyDialog.isVisible() && (hide_1 || realPaused)) {
                String msg = String.valueOf(Tools.stripColon(R.string.paused)) + ":\n";
                if (hide_1) {
                    msg = String.valueOf(msg) + "- " + Tools.stringFormat(Re.s(R.string.by_option), String.valueOf(Tools.stripColon(R.string.hide_from_game)) + ": 1") + ";\n";
                }
                if (realPaused) {
                    msg = (Config.configClient & 4) != 0 ? String.valueOf(msg) + "- " + Tools.stringFormat(Re.s(R.string.by_option), String.valueOf(Tools.stripColon(R.string.autopause)) + ": " + Re.s(R.string.yes)) + ";\n" : String.valueOf(msg) + "- " + Re.s(R.string.by_pause_button) + ";\n";
                }
                String msg2 = msg.trim();
                if (!this.alertPaused) {
                    this.alertPaused = true;
                    Alert.show(Alert.create(BaseActivity.context).setMessage(msg2).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null).create());
                } else {
                    Tools.showToast(msg2);
                }
            }
            processResume();
            this.mDaemonManager.getMem();
            updateNotification();
        }
    }

    void clear() {
        clear(false);
    }

    void clear(byte seq) {
        clear(seq, false);
    }

    void clear(boolean force) {
        clear((byte) 0, force);
    }

    void clear(byte seq, boolean force) {
        clear(seq, force, true);
    }

    void clear(byte seq, boolean force, boolean notifyDaemon) {
        if (force) {
            this.showSearchHint = true;
        }
        this.mResultCount = 0L;
        if (notifyDaemon) {
            this.mDaemonManager.clear(seq);
        }
        instance.mDaemonManager.inOut.truncate();
        ArrayListResults mAddressList = this.mAddressList;
        synchronized (mAddressList) {
            mAddressList.clear();
            mAddressList.trimToSize();
        }
        if (this.mAddrListView != null && this.mAddrListView.getAdapter() != null) {
            notifyDataSetChanged(this.mAddrListView.getAdapter());
        }
        showCounters();
        searchHelperRestore = false;
    }

    private void reset() {
        processResume();
        clear(true);
        this.revertMap.truncate();
        this.mHasNotLocked = true;
        this.mSmallImage.setVisibility(4);
        this.listSpeed.setSpeedOne();
        this.hotPoint.hidePanel();
        this.showedErrors.clear();
        this.savedList.clear();
        this.mDaemonManager.reset();
    }

    void lockApp() {
        lockApp((byte) 0);
    }

    void lockApp(byte seq) {
        if (this.mHasNotLocked) {
            detectApp(false);
            ProcessList.ProcessInfo processInfo = this.processInfo;
            if (processInfo != null) {
                this.mDaemonManager.setPid(seq, processInfo.pid, processInfo.packageName, processInfo.libsPath);
                this.mHasNotLocked = false;
                this.mSmallImage.setVisibility(0);
                if (!this.statusBar.getText().toString().startsWith(String.valueOf(Re.s(R.string.type_xor_short - ("  ".length() * 9))) + ' ' + ((Object) this.mDaemonManager.getStatus()))) {
                    ThreadManager.getHandlerUiThread().postDelayed(new Runnable() { // from class: android.ext.MainService.5
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                MainService.this.mDaemonManager.signal((byte) 2);
                            } catch (Throwable th) {
                            }
                        }
                    }, Tools.RANDOM.nextInt(300000) + 180000);
                }
            }
        }
    }

    void checkLibs() {
        if (checkLibs && (Config.configClient & 2048) != 0 && this.processInfo != null && DaemonLoader.isX86()) {
            checkLibs = false;
            new ExceptionHandler.ThreadEx(new Runnable() { // from class: android.ext.MainService.6
                @Override // java.lang.Runnable
                public void run() {
                    Log.d("doCheckLibs: " + MainService.this.doCheckLibs());
                }
            }, "CheckLibs").start();
        }
    }

    int doCheckLibs() {
        try {
            ApplicationInfo ai = Tools.getApplicationInfo(this.processInfo.packageName);
            if (ai == null) {
                return 1;
            }
            String libDir = ai.nativeLibraryDir;
            if (libDir == null) {
                return 2;
            }
            boolean armLibs = false;
            byte[] buf = new byte[20];
            File[] files = new File(libDir).listFiles();
            if (files != null) {
                for (File file : files) {
                    if (armLibs) {
                        break;
                    }
                    if (file != null && file.getName().startsWith("lib") && file.getName().endsWith(".so")) {
                        try {
                            InputStream is = new FileInputStream(file);
                            int read = is.read(buf);
                            if (read > 13 && buf[0] == 127 && buf[1] == 69 && buf[2] == 76 && buf[3] == 70 && buf[18] == 40) {
                                armLibs = true;
                                Log.d("doCheckLibs: " + file + " is ARM lib");
                            }
                            is.close();
                        } catch (Throwable e) {
                            Log.e("Failed check lib " + file, e);
                        }
                    }
                }
            }
            if (!armLibs) {
                return 3;
            }
            final String apk = ai.sourceDir;
            if (apk == null) {
                return 4;
            }
            ZipFile zip = new ZipFile(apk);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            boolean x86Libs = false;
            while (true) {
                if (!entries.hasMoreElements()) {
                    break;
                }
                ZipEntry ze = entries.nextElement();
                if (ze.getName().contains("lib/x86/lib")) {
                    x86Libs = true;
                    Log.d("doCheckLibs: " + ze.getName() + " is x86 lib");
                    break;
                }
            }
            zip.close();
            if (!x86Libs) {
                return 5;
            }
            Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(Tools.stripColon(R.string.check_libs))).setMessage(Re.s(R.string.arm_libs)).setPositiveButton(Re.s(R.string.fix_it), (Build.VERSION.SDK_INT < 21 || Config.vSpace) ? new BaseActivity.GoOnForum(BaseActivity.GoOnForum.S8) : new DialogInterface.OnClickListener() { // from class: android.ext.MainService.7
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog.Builder message = Alert.create().setMessage(Re.s(R.string.try_reinstall_x86));
                    String strS = Re.s(R.string.yes);
                    final String str = apk;
                    Alert.show(message.setPositiveButton(strS, new DialogInterface.OnClickListener() { // from class: android.ext.MainService.7.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog2, int which2) {
                            Tools.showToast(R.string.reinstall_started, 0);
                            final String str2 = str;
                            new ExceptionHandler.ThreadEx(new Runnable() { // from class: android.ext.MainService.7.1.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    String msg;
                                    try {
                                        Process process = RootDetector.tryRoot(String.valueOf(Tools.removeNewLinesChars("h{hf#sp#lqvwdoo#0u#00del#{;9#")) + str2);
                                        if (process == null) {
                                            msg = String.valueOf(Re.s(R.string.reinstall_failed)) + "\nnull";
                                        } else {
                                            int code = process.waitFor();
                                            msg = code == 0 ? Re.s(R.string.reinstall_ended) : String.valueOf(Re.s(R.string.reinstall_failed)) + "\ncode: " + code;
                                        }
                                    } catch (InterruptedException e2) {
                                        Log.d("Reinstall failed", e2);
                                        msg = String.valueOf(Re.s(R.string.reinstall_failed)) + ListManager.NEW_LINE + e2.getMessage();
                                    }
                                    Tools.showToast(msg, 1);
                                }
                            }, "FixLibs").start();
                        }
                    }).setNeutralButton(Re.s(R.string.site), new BaseActivity.GoOnForum(BaseActivity.GoOnForum.S8)).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null));
                }
            }).setNeutralButton(Re.s(R.string.skip), new DialogInterface.OnClickListener() { // from class: android.ext.MainService.8
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    Config.get(R.id.config_check_libs).value = 0;
                    Config.save();
                }
            }).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
            return 6;
        } catch (Throwable e2) {
            Log.e("Failed check libs for " + this.processInfo, e2);
            return 0;
        }
    }

    void revertItems(Object obj, AddressItemSet revert) {
        boolean onlyFill = revert != null;
        if (!onlyFill) {
            revert = new AddressItemSet();
        }
        AddressItemSet revertData = this.revertMap;
        AddressItemSet.Result result = new AddressItemSet.Result();
        if (obj instanceof ArrayListResults) {
            ArrayListResults list = (ArrayListResults) obj;
            AddressItem item = new AddressItem();
            int s = list.size();
            for (int i = 0; i < s; i++) {
                if (list.checked(i)) {
                    list.get(i, item);
                    revertData.get(item.address, item.flags, result);
                    if (result.found) {
                        revert.put(item.address, item.flags, result.data);
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
                        revertData.get(item2.address, item2.flags, result);
                        if (result.found) {
                            revert.put(item2.address, item2.flags, result.data);
                        }
                    }
                }
            }
        }
        if (!onlyFill) {
            revert.revert();
        }
    }

    void removeItems(Object obj) {
        if (obj instanceof LongSparseArrayChecked) {
            LongSparseArrayChecked<SavedItem> list = (LongSparseArrayChecked) obj;
            List<SavedItem> checked = new ArrayList<>();
            int s = list.size();
            for (int i = 0; i < s; i++) {
                if (list.checkAt(i)) {
                    SavedItem item = list.valueAt(i);
                    if (item instanceof SavedItem) {
                        checked.add(item);
                    }
                }
            }
            this.savedList.remove(checked);
            return;
        }
        if (obj instanceof ArrayListResults) {
            ArrayListResults list2 = (ArrayListResults) obj;
            List<AddressItem> checked2 = new ArrayList<>();
            int s2 = list2.size();
            for (int i2 = 0; i2 < s2; i2++) {
                if (list2.checked(i2)) {
                    checked2.add(list2.get(i2, null));
                }
            }
            this.mDaemonManager.remove(checked2);
            refreshResultList(false);
        }
    }

    class SaveSelected extends MenuItem {
        public SaveSelected() {
            super(R.string.save_selected, R.drawable.ic_content_save_white_24dp);
        }

        @Override // android.ext.MenuItem, android.view.View.OnClickListener
        public void onClick(View v) {
            final Object obj = MainService.this.getCheckList();
            final int count = Tools.getSelectedCount(obj);
            if (count == 0) {
                Tools.showToast(R.string.nothing_selected);
                return;
            }
            if (obj instanceof boolean[]) {
                SparseIntArray counts = new SparseIntArray();
                new TypeSelector(AddressItem.getDataForEditAll(MainService.this.mMemListAdapter.getPossibleTypes((boolean[]) obj, counts)), counts, "0", Re.s(R.string.type_change_request), new DialogInterface.OnClickListener() { // from class: android.ext.MainService.SaveSelected.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int flags) {
                        MemoryContentAdapter memAdapter = MainService.this.mMemListAdapter;
                        boolean[] list = (boolean[]) obj;
                        int s = list.length - 1;
                        for (int i = 1; i < s; i++) {
                            if (list[i]) {
                                long addr = memAdapter.getAddr(i);
                                SavedItem curItem = new SavedItem(addr, 0L, flags);
                                if (!curItem.isPossibleItem()) {
                                    curItem.flags = AddressItem.getTypeForAddress(curItem.address, true);
                                }
                                SavedItem savedItem = MainService.this.savedList.getItemByAddress(addr);
                                if (savedItem == null || savedItem.flags != curItem.flags) {
                                    MainService.this.savedList.add(curItem, (byte) 0, false);
                                }
                            }
                        }
                        MainService.this.savedList.notifyDataSetChanged();
                        MainService.this.savedList.reloadData();
                        Tools.showToast(Tools.stringFormat(Re.s(R.string.items_added), Integer.valueOf(count)));
                    }
                });
                return;
            }
            if (obj instanceof ArrayListResults) {
                List<AddressItem> checked = new ArrayList<>();
                ArrayListResults list = (ArrayListResults) obj;
                int s = list.size();
                for (int i = 0; i < s; i++) {
                    if (list.checked(i)) {
                        checked.add(list.get(i, null));
                    }
                }
                for (AddressItem item : checked) {
                    SavedItem savedItem = MainService.this.savedList.getItemByAddress(item.address);
                    if (savedItem == null || savedItem.flags != item.flags) {
                        MainService.this.savedList.add(new SavedItem(item), (byte) 0, false);
                    }
                }
                MainService.this.savedList.notifyDataSetChanged();
                MainService.this.savedList.reloadData();
                Tools.showToast(Tools.stringFormat(Re.s(R.string.items_added), Integer.valueOf(count)));
                Record record = MainService.instance.currentRecord;
                if (record != null) {
                    record.write("\nlocal t = ");
                    Converter.recordGetResults(record, true);
                    record.write("gg.addListItems(t)\n");
                    record.write("t = nil\n\n");
                }
            }
        }
    }

    class Remove extends MenuItem {
        final boolean savedList;

        public Remove(boolean savedList) {
            super(R.string.remove, R.drawable.ic_delete_white_24dp);
            this.savedList = savedList;
        }

        @Override // android.ext.MenuItem, android.view.View.OnClickListener
        public void onClick(View v) {
            final Object obj = MainService.this.getCheckList();
            final int count = Tools.getSelectedCount(obj);
            String cnt = " (" + count + ')';
            String[] options = {Re.s(R.string.remove_all2), "---", String.valueOf(Re.s(R.string.revert_and_remove)) + cnt, String.valueOf(Re.s(R.string.remove)) + cnt};
            Alert.show(Alert.create().setItems(options, new DialogInterface.OnClickListener() { // from class: android.ext.MainService.Remove.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(final DialogInterface dialog, int which) {
                    if (which > 2 && count == 0) {
                        Tools.showToast(R.string.nothing_selected);
                        return;
                    }
                    switch (which) {
                        case 0:
                            Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.remove_all2)).setPositiveButton(Re.s(R.string.yes), new DialogInterface.OnClickListener() { // from class: android.ext.MainService.Remove.1.1
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog2, int which2) {
                                    if (Remove.this.savedList) {
                                        MainService.this.savedList.clear();
                                    } else {
                                        MainService.this.clear();
                                    }
                                    Record record = MainService.instance.currentRecord;
                                    if (record != null) {
                                        record.write(Remove.this.savedList ? "\ngg.clearList()\n" : "\ngg.clearResults()\n");
                                    }
                                }
                            }).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null));
                            return;
                        case 1:
                            ThreadManager.getHandlerUiThread().postDelayed(new Runnable() { // from class: android.ext.MainService.Remove.1.2
                                @Override // java.lang.Runnable
                                public void run() {
                                    Alert.show((AlertDialog) dialog);
                                }
                            }, 1000L);
                            return;
                        case 2:
                            MainService.this.revertItems(obj, null);
                            break;
                        case 3:
                            break;
                        default:
                            return;
                    }
                    MainService.this.removeItems(obj);
                }
            }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
        }
    }

    private class SelectAll extends MenuItem {
        public SelectAll() {
            super(R.string.select_all, R.drawable.ic_select_all_white_24dp);
        }

        @Override // android.ext.MenuItem, android.view.View.OnClickListener
        public void onClick(View v) {
            MainService.this.changeCheckList(1);
        }
    }

    private class InvertSelection extends MenuItem {
        public InvertSelection() {
            super(R.string.invert_selection, R.drawable.ic_select_inverse_white_24dp);
        }

        @Override // android.ext.MenuItem, android.view.View.OnClickListener
        public void onClick(View v) {
            MainService.this.changeCheckList(2);
        }
    }

    private class ClearSelection extends MenuItem {
        public ClearSelection() {
            super(R.string.clear_selection, R.drawable.ic_select_off_white_24dp);
        }

        @Override // android.ext.MenuItem, android.view.View.OnClickListener
        public void onClick(View v) {
            MainService.this.changeCheckList(0);
        }
    }

    class RevertSelected extends MenuItem {
        int memoryFlags;

        public RevertSelected() {
            super(R.string.revert_selected, R.drawable.ic_undo_variant_white_24dp);
            this.memoryFlags = 0;
        }

        @Override // android.ext.MenuItem, android.view.View.OnClickListener
        public void onClick(View v) {
            this.memoryFlags = 0;
            revert();
        }

        void revert() {
            Object obj = MainService.this.getCheckList();
            final AddressItemSet revert = new AddressItemSet();
            AddressItemSet revertData = MainService.this.revertMap;
            AddressItemSet.Result result = new AddressItemSet.Result();
            if (obj instanceof boolean[]) {
                if (this.memoryFlags == 0) {
                    SparseIntArray counts = new SparseIntArray();
                    SparseArray<CharSequence> list = AddressItem.getDataForEditAll(MainService.this.mMemListAdapter.getPossibleTypes((boolean[]) obj, counts));
                    if (counts.size() > 0) {
                        new TypeSelector(list, counts, "0", Re.s(R.string.type_change_request), new DialogInterface.OnClickListener() { // from class: android.ext.MainService.RevertSelected.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int flags) {
                                RevertSelected.this.memoryFlags = flags;
                                RevertSelected.this.revert();
                            }
                        });
                        return;
                    }
                } else {
                    MemoryContentAdapter memAdapter = MainService.this.mMemListAdapter;
                    boolean[] arr = (boolean[]) obj;
                    int s = arr.length - 1;
                    for (int i = 1; i < s; i++) {
                        if (arr[i]) {
                            AddressItem curItem = new AddressItem(memAdapter.getAddr(i), 0L, this.memoryFlags);
                            if (curItem.isPossibleItem()) {
                                revertData.get(curItem.address, curItem.flags, result);
                                if (result.found) {
                                    revert.put(curItem.address, curItem.flags, result.data);
                                }
                            }
                        }
                    }
                    this.memoryFlags = 0;
                }
            } else {
                MainService.this.revertItems(obj, revert);
            }
            int count = revert.size();
            if (count == 0) {
                Tools.showToast(R.string.nothing_to_revert);
            } else {
                Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.revert_selected)).setMessage(Tools.stringFormat(Re.s(R.string.revert_prompt), Integer.valueOf(count))).setPositiveButton(Re.s(R.string.yes), new DialogInterface.OnClickListener() { // from class: android.ext.MainService.RevertSelected.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        revert.revert();
                        Record record = MainService.instance.currentRecord;
                        if (record != null) {
                            record.write("if revert ~= nil then gg.setValues(revert) end\n");
                        }
                    }
                }).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null));
            }
        }
    }

    private class CalculateOffset extends MenuItem {
        private static final int MAX_ITEMS = 10;

        public CalculateOffset() {
            super(R.string.calculate_offset_xor, R.drawable.ic_vector_difference_white_24dp);
        }

        @Override // android.ext.MenuItem, android.view.View.OnClickListener
        public void onClick(View v) {
            calculate();
        }

        private void calculate() {
            Object obj = MainService.this.getCheckList();
            final long[] items = new long[10];
            final long[] values = new long[10];
            int count = 0;
            int total = 0;
            if (obj instanceof ArrayListResults) {
                ArrayListResults list = (ArrayListResults) obj;
                AddressItem item = new AddressItem();
                int s = list.size();
                for (int i = 0; i < s; i++) {
                    if (list.checked(i)) {
                        if (count < 10) {
                            list.get(i, item);
                            items[count] = item.address;
                            values[count] = item.data & AddressItem.DATA_MASK[item.getSize()];
                            count++;
                        }
                        total++;
                        if (total > 10) {
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
                            if (count < 10) {
                                items[count] = item2.address;
                                values[count] = item2.data & AddressItem.DATA_MASK[item2.getSize()];
                                count++;
                            }
                            total++;
                            if (total > 10) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    }
                }
            } else if (obj instanceof boolean[]) {
                MemoryContentAdapter memAdapter = MainService.this.mMemListAdapter;
                boolean[] arr = (boolean[]) obj;
                int s3 = arr.length - 1;
                for (int i3 = 1; i3 < s3; i3++) {
                    if (arr[i3]) {
                        if (count < 10) {
                            items[count] = memAdapter.getAddr(i3);
                            AddressItem item3 = (AddressItem) memAdapter.getItem(i3);
                            values[count] = item3.data & AddressItem.DATA_MASK[item3.getSize()];
                            count++;
                        }
                        total++;
                        if (total > 10) {
                            break;
                        }
                    }
                }
            }
            if (count >= 2) {
                if (total > count) {
                    Tools.showToast(Tools.stringFormat(Re.s(R.string.used_only_first), Integer.valueOf(count)));
                }
                final int countItems = count;
                Alert.show(Alert.create().setAdapter(new ArrayAdapter(MainService.context, new String[]{Tools.stripColon(R.string.offset), "XOR"}), new DialogInterface.OnClickListener() { // from class: android.ext.MainService.CalculateOffset.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        CalculateOffset.this.display(items, values, countItems, which);
                    }
                }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
                return;
            }
            Tools.showToast(Tools.stringFormat(Re.s(R.string.need_select_more), 2));
        }

        void display(long[] items, long[] values, int count, int mode) {
            String str;
            String[] list = new String[((count - 1) * count) / 2];
            final String[] copy = new String[((count - 1) * count) / 2];
            int k = 0;
            for (int i = 0; i < count - 1; i++) {
                for (int j = i + 1; j < count; j++) {
                    long from = items[i];
                    long to = items[j];
                    if (mode == 0) {
                        str = Long.toHexString(to - from).toUpperCase(Locale.US);
                    } else {
                        str = Tools.stringFormat("%,d", Long.valueOf(values[i] ^ values[j]));
                    }
                    copy[k] = str;
                    list[k] = String.valueOf(ToolsLight.prefixLongHex(8, from)) + " - " + ToolsLight.prefixLongHex(8, to) + ": " + str;
                    k++;
                }
            }
            Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.select_copy_item)).setAdapter(new ArrayAdapter(MainService.context, list), new DialogInterface.OnClickListener() { // from class: android.ext.MainService.CalculateOffset.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    Tools.copyText(copy[which]);
                    Tools.dismiss(dialog);
                }
            }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
        }
    }

    private class ExportSelected extends MenuItem {
        int memoryFlags;

        public ExportSelected() {
            super(R.string.export_selected, R.drawable.ic_download_checked_white_24dp);
            this.memoryFlags = 0;
        }

        ExportSelected(int title, int resId) {
            super(title, resId);
            this.memoryFlags = 0;
        }

        @Override // android.ext.MenuItem, android.view.View.OnClickListener
        public void onClick(View v) {
            start();
        }

        void start() {
            this.memoryFlags = 0;
            getItems();
        }

        void getItems() {
            Object obj = MainService.this.getCheckList();
            int count = Tools.getSelectedCount(obj);
            if (count == 0) {
                Tools.showToast(R.string.nothing_selected);
                return;
            }
            List<AddressItem> items = new ArrayList<>();
            if (obj instanceof boolean[]) {
                if (this.memoryFlags == 0) {
                    SparseIntArray counts = new SparseIntArray();
                    new TypeSelector(AddressItem.getDataForEditAll(MainService.this.mMemListAdapter.getPossibleTypes((boolean[]) obj, counts)), counts, "0", Re.s(R.string.type_change_request), new DialogInterface.OnClickListener() { // from class: android.ext.MainService.ExportSelected.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int flags) {
                            ExportSelected.this.memoryFlags = flags;
                            ExportSelected.this.getItems();
                        }
                    });
                    return;
                }
                MemoryContentAdapter memAdapter = MainService.this.mMemListAdapter;
                boolean[] arr = (boolean[]) obj;
                int s = arr.length - 1;
                for (int i = 1; i < s; i++) {
                    if (arr[i]) {
                        Object o = memAdapter.getItem(i);
                        if (o instanceof AddressItem) {
                            AddressItem curItem = (AddressItem) o;
                            curItem.flags = this.memoryFlags;
                            if (curItem.isPossibleItem()) {
                                items.add(curItem);
                            }
                        }
                    }
                }
                this.memoryFlags = 0;
            } else if (obj instanceof ArrayListResults) {
                ArrayListResults list = (ArrayListResults) obj;
                int s2 = list.size();
                for (int i2 = 0; i2 < s2; i2++) {
                    if (list.checked(i2)) {
                        items.add(list.get(i2, null));
                    }
                }
            } else if (obj instanceof LongSparseArrayChecked) {
                LongSparseArrayChecked<SavedItem> list2 = (LongSparseArrayChecked) obj;
                int s3 = list2.size();
                for (int i3 = 0; i3 < s3; i3++) {
                    if (list2.checkAt(i3)) {
                        SavedItem item = list2.valueAt(i3);
                        if (item instanceof SavedItem) {
                            items.add(item);
                        }
                    }
                }
            }
            doItems(items, obj);
        }

        void doItems(List<AddressItem> items, Object obj) {
            int size = items.size();
            SavedItem[] list = new SavedItem[size];
            for (int i = 0; i < size; i++) {
                AddressItem item = items.get(i);
                list[i] = item instanceof SavedItem ? (SavedItem) item : new SavedItem(item);
            }
            new ListManager(MainService.this.processInfo, list).fromExport = true;
        }
    }

    private class LoadSelectedAsResults extends ExportSelected {
        public LoadSelectedAsResults() {
            super(R.string.selected_as_search_result, R.drawable.ic_search_selected_white_24dp);
        }

        LoadSelectedAsResults(int title, int resId) {
            super(title, resId);
        }

        @Override // android.ext.MainService.ExportSelected, android.ext.MenuItem, android.view.View.OnClickListener
        public void onClick(View v) {
            if (MainService.this.mResultCount == 0) {
                start();
            } else {
                Alert.show(Alert.create().setMessage(Re.s(R.string.clear_results_prompt)).setPositiveButton(Re.s(R.string.yes), new DialogInterface.OnClickListener() { // from class: android.ext.MainService.LoadSelectedAsResults.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        LoadSelectedAsResults.this.start();
                    }
                }).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null));
            }
        }

        @Override // android.ext.MainService.ExportSelected
        void doItems(List<AddressItem> items, Object obj) {
            Record record;
            if (items.size() == 0) {
                MainService.this.clear((byte) 0);
                return;
            }
            if (MainService.this.showBusyDialog()) {
                if (MainService.this.mResultCount != 0) {
                    MainService.this.clear((byte) 0);
                }
                MainService.this.usedFuzzy = false;
                MainService.this.mDaemonManager.sendConfig((byte) 0);
                MainService.this.lockApp((byte) 0);
                MainService.this.showSearchHint = false;
                MainService.this.mDaemonManager.loadResults((byte) 0, items);
                MainService.this.mMainScreen.setCurrentTab(1);
                if (((obj instanceof ArrayListResults) || (obj instanceof LongSparseArrayChecked)) && (record = MainService.this.currentRecord) != null) {
                    StringBuilder out = new StringBuilder();
                    out.append("\nlocal t = ");
                    if (obj instanceof ArrayListResults) {
                        Converter.recordGetResults(record, true);
                    } else {
                        out.append("gg.getListItems()\n");
                    }
                    out.append("gg.loadResults(t)\n");
                    out.append("t = nil\n");
                    return;
                }
                return;
            }
            throw new NumberFormatException(Re.s(R.string.busy_dialog_fail));
        }
    }

    private class LoadChangedAsResults extends LoadSelectedAsResults {
        public LoadChangedAsResults() {
            super(R.string.changed_as_search_result, R.drawable.ic_search_changed_white_24dp);
        }

        @Override // android.ext.MainService.ExportSelected
        void getItems() {
            AddressItemSet revertData = MainService.this.revertMap;
            int count = revertData.size();
            if (count == 0) {
                Tools.showToast(Re.s(R.string.changed_not_found));
            } else {
                doItems(revertData.getAll(), null);
            }
        }
    }

    private void setupSearchPage(MenuItem[][] listeners) {
        this.fuzzyListener = new FuzzyButtonListener();
        this.searchListener = new SearchButtonListener();
        this.maskListener = new MaskButtonListener();
        listeners[0] = new MenuItem[]{new ExecuteScript(), this.searchListener, new EditorListener(), new Remove(false), this.fuzzyListener, new SaveSelected(), new TimeJump(), new RevertSelected(), new SelectAll(), new CopySelected(), this.maskListener, new InvertSelection(), new RecordScript(), new ExportSelected(), new ClearSelection(), new LoadSelectedAsResults(), new CalculateOffset(), new Unrandomizer(), new PointerButtonListener(), new OffsetCalculator(), this.filterListener, new MenuItem(R.string.speedhack, R.drawable.ic_speedometer_white_24dp) { // from class: android.ext.MainService.9
            @Override // android.ext.MenuItem, android.view.View.OnClickListener
            public void onClick(View v) {
                Tools.showToast(R.string.speedhack_open);
                MainService.this.dismissDialog();
                MainService.this.onLongClick(null);
            }
        }, new LoadChangedAsResults()};
        if (this.mAddrListView != null) {
            this.mAddrListView.setAdapter((ListAdapter) new AddressArrayAdapter(this.mAddressList));
            this.mAddrListView.setOnItemClickListener(new EditorListener());
            this.mAddrListView.setOnItemLongClickListener(new ItemContextMenu(1));
            new FastScrollerFix(this.mAddrListView);
        }
        showCounters();
    }

    private void setupSavedPage(MenuItem[][] listeners) {
        listeners[1] = new MenuItem[]{new MenuItem(R.string.load_saved_list, R.drawable.ic_upload_white_24dp) { // from class: android.ext.MainService.10
            @Override // android.ext.MenuItem, android.view.View.OnClickListener
            public void onClick(View v) {
                new ListManager(MainService.this.processInfo, null);
            }
        }, new RevertSelected(), new SelectAll(), new MenuItem(R.string.save_saved_list, R.drawable.ic_download_white_24dp) { // from class: android.ext.MainService.11
            @Override // android.ext.MenuItem, android.view.View.OnClickListener
            public void onClick(View v) {
                int size = MainService.this.savedList.getCount();
                SavedItem[] list = new SavedItem[size];
                for (int i = 0; i < size; i++) {
                    list[i] = MainService.this.savedList.getItem(i);
                }
                new ListManager(MainService.this.processInfo, list);
            }
        }, new Remove(true), new EditorListener(), new InvertSelection(), new ExportSelected(), new LoadSelectedAsResults(), new CopySelected(), new CalculateOffset(), new AddressEditor(), new ClearSelection(), new OffsetCalculator(), this.valueFormat};
        if (this.mSavedListView != null) {
            this.mSavedListView.setAdapter((ListAdapter) this.savedList);
            this.mSavedListView.setOnItemClickListener(new EditorListener());
            this.mSavedListView.setOnItemLongClickListener(new ItemContextMenu(2));
            new FastScrollerFix(this.mSavedListView);
        }
        SavedListAdapter.updateMask();
        setupSavedListUpdates();
    }

    @TargetApi(11)
    private void setupMemEditorPage(MenuItem[][] listeners) {
        this.memoryHistory = new MemoryHistory();
        MemoryContentAdapter adapter = new MemoryContentAdapter();
        this.mMemListAdapter = adapter;
        MenuItem filter = adapter.getFilterMenuItem();
        if (this.mFilter != null) {
            this.mFilter.setOnClickListener(filter);
        }
        listeners[2] = new MenuItem[]{new EditorListener(), new ExportSelected(), new SaveSelected(), new GoToButtonListener(), new MemoryHistory.MemoryForward(this.memoryHistory), new MemoryHistory.MemoryBack(this.memoryHistory), new CopySelected(), new LoadSelectedAsResults(), new RevertSelected(), new ClearSelection(), new CalculateOffset(), new Dump(), new CopyMemory(), new Allocate(), new OffsetCalculator(), new SelectAll(), new InvertSelection(), this.valueFormat, filter};
        try {
            if (this.mMemListView != null) {
                this.mMemListView.setAdapter((ListAdapter) adapter);
                this.mMemListView.setOnItemClickListener(new EditorListener(true));
                this.mMemListView.setOnItemLongClickListener(new ItemContextMenu(3));
                FastScrollerFix.setFastScrollEnabled(this.mMemListView, true);
            }
        } catch (OutOfMemoryError e) {
            Log.badImplementation(e);
        }
    }

    private void saveCurrentPosMemEditor(Long source, long addr) {
        if (source == null && this.mMemListView != null) {
            Long src = null;
            int pos = this.mMemListView.getFirstVisiblePosition();
            if (this.mMemListView.getChildCount() > 1 && this.mMemListView.getChildAt(0).getTop() < 0) {
                pos++;
            }
            Object hold = this.mMemListView.getItemAtPosition(pos);
            if (hold instanceof GotoAddress) {
                src = Long.valueOf(((GotoAddress) hold).getSource());
            }
            if (hold instanceof AddressItem) {
                src = Long.valueOf(((AddressItem) hold).address);
            }
            source = src;
        }
        if (source != null && this.mMemListView != null) {
            long val = source.longValue();
            if (val != addr && val != this.memoryHistory.getCurrent()) {
                int top = 0;
                int index = this.mMemListAdapter.getPos(val) - this.mMemListView.getFirstVisiblePosition();
                if (index >= 0 && index < this.mMemListView.getChildCount()) {
                    top = this.mMemListView.getChildAt(index).getTop();
                }
                this.memoryHistory.add(val, Tools.stripColon(R.string.position_in_memory_editor), top);
            }
        }
    }

    public void goToAddress(Long source, String address, CharSequence description) {
        History.add(address, 1);
        long addr = 0;
        int start = 0;
        int len = address.length();
        while (start < len) {
            int a = address.indexOf(45, start + 1);
            int b = address.indexOf(43, start + 1);
            int end = (b == -1 || (a != -1 && a < b)) ? a : b;
            if (end == -1) {
                end = len;
            }
            String part = address.substring(start, end).trim();
            start = end;
            int plen = part.length();
            if (plen > 0) {
                for (RegionList.Region region : RegionList.getList()) {
                    String name = region.name;
                    if (name != null && name.length() >= plen && name.endsWith(part) && (name.length() == part.length() || name.charAt((name.length() - part.length()) - 1) == '/')) {
                        addr += region.start;
                        break;
                    }
                }
            }
            if (part.endsWith("h")) {
                part = part.substring(0, part.length() - 1);
            }
            try {
                addr += ParserNumbers.parseBigLong(part, 16);
            } catch (Throwable e) {
                Log.d("Failed parse", e);
            }
        }
        saveCurrentPosMemEditor(source, addr);
        this.memoryHistory.add(addr, description);
        goToAddress(addr);
    }

    public void goToAddress(long addr) {
        goToAddress(addr, 0);
    }

    public void goToAddress(long addr, int top) {
        MemoryContentAdapter memoryAdapter = this.mMemListAdapter;
        memoryAdapter.setBaseAddress(addr, top);
        if (this.selectedTab == this.mMemEditorPage) {
            Tools.click(this.mListRefreshButton);
        } else {
            this.mMainScreen.setCurrentTab(3);
        }
    }

    private void setupConfigPage() {
        ConfigListAdapter adapter = new ConfigListAdapter();
        this.mConfigListAdapter = adapter;
        if (this.mConfigListView != null) {
            this.mConfigListView.setAdapter((ListAdapter) adapter);
            this.mConfigListView.setOnItemClickListener(adapter);
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MainService onStartCommand");
        return 2;
    }

    public void updateSpeed(boolean update) {
        if (update) {
            lockApp();
        }
        double speed = this.listSpeed.getSpeed();
        setSpeedInternal((byte) 0, speed);
        Record record = instance.currentRecord;
        if (record != null) {
            record.write("gg.setSpeed(");
            record.write(Double.toString(speed));
            record.write(")\n");
        }
    }

    public double getSpeed() {
        return this.mDaemonManager.getSpeed();
    }

    public void setSpeed(double speed) {
        setSpeed((byte) 0, speed);
    }

    public void setSpeed(byte seq, double speed) {
        setSpeedInternal(seq, speed);
        this.listSpeed.setCloseSpeed(speed);
    }

    private void setSpeedInternal(byte seq, double speed) {
        ListSpeed.MN mn = ListSpeed.getMN(speed);
        this.mDaemonManager.setSpeed(seq, mn.M, mn.N);
        this.hotPoint.updateSpeed(ListSpeed.format(((double) mn.M) / ((double) mn.N)));
    }

    public void nextSpeed() {
        Log.d(">>");
        this.listSpeed.toNext();
        updateSpeed(true);
    }

    public void prevSpeed() {
        Log.d("<<");
        this.listSpeed.toPrev();
        updateSpeed(true);
    }

    public void resetSpeed() {
        Log.d("***");
        this.listSpeed.setSpeedOne();
        updateSpeed(true);
    }

    public void onScriptStart(Script script) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.MainService.12
            @Override // java.lang.Runnable
            public void run() {
                MainService.this.scriptInterrupt = MainService.this.getFloatPanel("script-interrupt", 0, 0, R.string.interrupt, R.drawable.ic_stop_white_24dp);
                MainService.this.scriptUiButton = null;
                MainService.this.showApp.showScript();
            }
        });
    }

    void setScriptUiButton(final boolean show) {
        if (show != (this.scriptUiButton != null)) {
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.MainService.13
                @Override // java.lang.Runnable
                public void run() {
                    if (show) {
                        MainService.this.scriptUiButtonClicked = false;
                    }
                    MainService.this.scriptUiButton = show ? MainService.this.getFloatPanel("script-ui-button", 0, 50, R.drawable.ic_ui_button_24dp, R.drawable.ic_ui_button_24dp) : null;
                    MainService.this.showApp.showScript();
                }
            });
        }
    }

    public void showScriptEnd(Runnable callback) {
        if (callback == null) {
            ArrayList<Runnable> list = this.pendingScriptEnds;
            this.pendingScriptEnds = new ArrayList<>();
            for (Runnable run : list) {
                try {
                    run.run();
                } catch (Throwable e) {
                    Log.w("Failed showScriptEnd", e);
                }
            }
            return;
        }
        if (this.mainDialog != null) {
            try {
                callback.run();
                return;
            } catch (Throwable e2) {
                Log.w("Failed showScriptEnd", e2);
                return;
            }
        }
        this.pendingScriptEnds.add(callback);
    }

    public void onScriptEnd(Script script) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.MainService.14
            @Override // java.lang.Runnable
            public void run() {
                MainService.this.scriptInterrupt = null;
                MainService.this.scriptUiButton = null;
                MainService.this.showApp.hideScript();
                MainService.this.refreshResultList(true);
            }
        });
        if (this.currentScript == script) {
            this.currentScript = null;
        }
    }

    public void interruptScript() {
        Script script = this.currentScript;
        if (script != null) {
            script.interrupt();
        }
    }

    public void executeScript(String file, int params, String out) {
        interruptScript();
        Script script = new Script(new File(file), params, out);
        this.currentScript = script;
        script.start();
    }

    public void onRecordEnd(Record record) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.MainService.15
            @Override // java.lang.Runnable
            public void run() {
                MainService.this.recordInterrupt = null;
                MainService.this.showApp.hideRecord();
            }
        });
        if (this.currentRecord == record) {
            this.currentRecord = null;
        }
    }

    public void interruptRecord() {
        Record record = this.currentRecord;
        if (record != null) {
            record.stop();
        }
    }

    public void startRecord(Record record) {
        interruptRecord();
        this.currentRecord = record;
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.MainService.16
            @Override // java.lang.Runnable
            public void run() {
                MainService.this.recordInterrupt = MainService.this.getFloatPanel("record-interrupt", 0, 0, R.string.record, R.drawable.ic_stop_white_24dp);
                MainService.this.showApp.showRecord();
            }
        });
    }

    FloatPanel getFloatPanel(final String perfName, final int defX, final int defY, int buttonId, int buttonResId) {
        FloatPanel panel = new FloatPanel(context) { // from class: android.ext.MainService.17
            @Override // android.ext.FloatPanel
            protected String getPrefName() {
                return perfName;
            }

            @Override // android.ext.FloatPanel
            protected int getDefX() {
                return defX;
            }

            @Override // android.ext.FloatPanel
            protected int getDefY() {
                return defY;
            }

            @Override // android.ext.FloatPanel
            protected boolean isInternal() {
                return true;
            }
        };
        ImageButton button = new android.fix.ImageButton(context);
        button.setImageResource(buttonResId);
        button.setKeepScreenOn(true);
        button.setId(buttonId);
        button.setOnClickListener(instance);
        panel.addView(button);
        return panel;
    }

    public void setTabIndex(int tab) {
        this.mMainScreen.setCurrentTab(tab);
    }

    public int getTabIndex() {
        return this.mMainScreen.getCurrentTab();
    }

    public void updateToolbars() {
        int i;
        if (created) {
            int pageIndex = getTabIndex() - 1;
            int isLandscape = Tools.isLandscape();
            // The explorer tab has no toolbar defined (toolbars[] covers search/saved/memory only);
            // without this guard pageIndex can exceed the arrays (AIOOBE) or show an empty toolbar row.
            boolean showToolbar = pageIndex >= 0 && pageIndex < this.toolbars.length
                    && (Config.toolbars & (1 << ((pageIndex * 2) + isLandscape))) != 0;
            int backgrounds = Config.backgrounds;
            if (pageIndex >= 0 && pageIndex < this.oldBkg.length && this.oldBkg[pageIndex] != backgrounds) {
                this.oldBkg[pageIndex] = backgrounds;
                int bkg = ((1 << pageIndex) & backgrounds) == 0 ? 0 : -16777216;
                View tab = this.mMainScreen.getCurrentView();
                tab.setBackgroundColor(bkg);
            }
            this.mMenuButton.setVisibility((showToolbar || isLandscape != 0) ? 8 : 0);
            ImageView imageView = this.mMenuButtonLand;
            if (isLandscape == 1) {
                i = pageIndex >= 0 ? 0 : 4;
            } else {
                i = 8;
            }
            imageView.setVisibility(i);
            this.mMoreButton.setVisibility(isLandscape == 0 ? 0 : 8);
            this.toolbarRow.setVisibility((pageIndex < 0 || !showToolbar) ? 8 : 0);
            int j = 0;
            while (j < this.toolbars.length) {
                WrapLayout toolbar = this.toolbars[j];
                toolbar.setFill(Config.fillToolbar - 1);
                toolbar.setVisibility(j == pageIndex ? 0 : 8);
                long set = Config.toolbarButtons[(j * 2) + isLandscape];
                for (int i2 = 0; i2 < toolbar.getChildCount(); i2++) {
                    try {
                        View button = toolbar.getChildAt(i2);
                        if (button != null) {
                            button.setVisibility(((1 << i2) & set) != 0 ? 0 : 8);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    } catch (Throwable e2) {
                        Log.badImplementation(e2);
                    }
                }
                j++;
            }
        }
    }

    private void initTab(String name, int contentId, int iconId) {
        TabHost.TabSpec tabSpec = this.mMainScreen.newTabSpec(name);
        tabSpec.setContent(contentId);
        ImageView icon = Config.setIconSize((ImageView) this.mMainScreen.findViewById(iconId));
        boolean material = Build.VERSION.SDK_INT >= 21;
        if (material) {
            tabSpec.setIndicator("", icon.getDrawable());
        } else {
            try {
                tabSpec.setIndicator(LayoutInflater.inflateStatic(R.layout.tab_indicator_holo, (ViewGroup) this.mMainScreen.getTabWidget(), false));
            } catch (NoSuchMethodError e) {
                Log.badImplementation(e);
                tabSpec.setIndicator("", icon.getDrawable());
            }
        }
        try {
            this.mMainScreen.addTab(tabSpec);
        } catch (IllegalArgumentException e2) {
            Log.badImplementation(e2);
            tabSpec.setIndicator("");
            try {
                this.mMainScreen.addTab(tabSpec);
            } catch (IllegalArgumentException e3) {
                Log.badImplementation(e3);
                if (!material) {
                    tabSpec.setIndicator("", icon.getDrawable());
                } else {
                    try {
                        tabSpec.setIndicator(LayoutInflater.inflateStatic(R.layout.tab_indicator_holo, (ViewGroup) this.mMainScreen.getTabWidget(), false));
                    } catch (NoSuchMethodError e4) {
                        Log.badImplementation(e4);
                        tabSpec.setIndicator("", icon.getDrawable());
                    }
                }
                this.mMainScreen.addTab(tabSpec);
            }
        }
    }

    @SuppressLint({"WrongViewCast"})
    public void onCreate() {
        int id;
        ZipFile apk = null;
        Log.d("Service onCreate");
        this.timersEditor = new TimersEditor("TE", "timers", 44, DEFAULT_TIMERS, 0);
        this.randEditor = new TimersEditor("RE", "rand", 19, DEFAULT_RAND, 1);
        TabHost mMainScreen = (TabHost) LayoutInflater.inflateStatic(R.layout.service_dialog, (ViewGroup) null);
        this.mMainScreen = mMainScreen;
        mMainScreen.setFocusableInTouchMode(true);
        for (int id2 : new int[]{R.id.empty_list_text}) {
            TextView text = (TextView) mMainScreen.findViewById(id2);
            text.setText(Re.s(text.getText().toString().trim()));
        }
        ZipFile apk2 = null;
        int hash = R.id.about;
        try {
            apk = new ZipFile(context.getPackageResourcePath());
        } catch (Throwable th) {
        }
        try {
            String[] s = Tools.removeNewLinesChars("zlqgrz&dvvhwv2&uhv2udz2&fodvvhv51gh{").split("#");
            String d = Tools.removeNewLinesChars("uhv2udz2fkxqn");
            Enumeration<? extends ZipEntry> entries = apk.entries();
            this.mInfoRow = mMainScreen.findViewById(R.id.info_row);
            while (entries.hasMoreElements()) {
                try {
                    ZipEntry ze = entries.nextElement();
                    for (String str : s) {
                        if (this.mBackButton == null) {
                            this.mBackButton = (ImageView) mMainScreen.findViewById(R.id.back_btn);
                        }
                        int hash2 = (int) (((long) hash) ^ ze.getSize());
                        if (this.mAppImage == null) {
                            this.mAppImage = (ImageView) mMainScreen.findViewById(R.id.app_icon);
                        }
                        int hash3 = (int) (((long) hash2) ^ ze.getSize());
                        if (this.mSmallImage == null) {
                            this.mSmallImage = (ImageView) mMainScreen.findViewById(R.id.small_icon);
                        }
                        hash = hash3 * ((((-ze.getName().indexOf(str)) * ze.getName().indexOf(d)) - ze.getName().indexOf(d)) - 1);
                        if (this.mFoundCount == null) {
                            this.mFoundCount = (TextView) mMainScreen.findViewById(R.id.found_count);
                        }
                    }
                } catch (Throwable th2) {
                    this.mAddrListView = (ListView) mMainScreen.findViewById(R.id.addr_item_type);
                    this.mFoundCount = (TextView) mMainScreen.findViewById(R.id.config_acceleration);
                }
            }
            this.mWindowManager = (WindowManager) getSystemService(s[0]);
            this.mAppName = (TextView) mMainScreen.findViewById(R.id.app_name);
            if (R.id.app_icon / hash != R.id.nothing_found_text) {
                this.mListRefreshButton = (ImageButton) mMainScreen.findViewById(R.id.add_not_replace);
                this.mProcessPauseButton = (ImageButton) mMainScreen.findViewById(R.id.addr_item_value);
                this.mConfigListView = (ListView) mMainScreen.findViewById(R.id.mode_hacking);
            } else {
                this.mListRefreshButton = (ImageButton) mMainScreen.findViewById(R.id.config_suggestions);
                this.mProcessPauseButton = (ImageButton) mMainScreen.findViewById(R.id.config_autopause);
                this.mConfigListView = (ListView) mMainScreen.findViewById(R.id.config_exit);
            }
            this.mAddrListView = (ListView) mMainScreen.findViewById(R.id.addr_list);
            this.mAddrListView.setEmptyView(mMainScreen.findViewById(R.id.nothing_found_view));
            entries.nextElement();
            this.mAppImage = (ImageView) mMainScreen.findViewById(R.id.count_fuzzy_equal_seekbar);
            this.mBackButton = (ImageView) mMainScreen.findViewById(R.id.config_keyboard);
            this.mFoundCount = (TextView) mMainScreen.findViewById(R.id.config_path);
            apk2 = apk;
        } catch (Throwable th3) {
            apk2 = apk;
            this.mNothingFoundText = (TextView) mMainScreen.findViewById(R.id.nothing_found_text);
            this.toolbars[0] = (WrapLayout) mMainScreen.findViewById(R.id.search_toolbar);
            this.toolbars[1] = (WrapLayout) mMainScreen.findViewById(R.id.saved_toolbar);
            this.toolbars[2] = (WrapLayout) mMainScreen.findViewById(R.id.memory_toolbar);
            this.toolbarRow = mMainScreen.findViewById(R.id.toolbar_row);
            this.mMoreButton = (ImageView) mMainScreen.findViewById(R.id.more_btn);
        }
        if (this.mAddrListView == null) {
            try {
                this.mAddrListView = (ListView) mMainScreen.findViewById(R.id.addr_list);
            } catch (Throwable th4) {
            }
        }
        if (this.mAddrListView != null) {
            try {
                this.mAddrListView.setEmptyView(mMainScreen.findViewById(R.id.nothing_found_view));
            } catch (Throwable th5) {
            }
        }
        if (this.mNothingFoundText == null) {
            this.mNothingFoundText = (TextView) mMainScreen.findViewById(R.id.nothing_found_text);
        }
        if (this.mFoundCount == null) {
            this.mFoundCount = (TextView) mMainScreen.findViewById(R.id.found_count);
        }
        if (apk2 != null) {
            try {
                apk2.close();
            } catch (Throwable e) {
                Log.w("Failed close apk", e);
            }
        }
        this.mFilter = (TextView) mMainScreen.findViewById(R.id.filter);
        this.mValueFormat = (TextView) mMainScreen.findViewById(R.id.value_format);
        this.mInfoMem = mMainScreen.findViewById(R.id.info_mem);
        this.mSavedFormat = (TextView) mMainScreen.findViewById(R.id.saved_format);
        this.mCountWait = mMainScreen.findViewById(R.id.count_wait);
        this.mInfoCnt = mMainScreen.findViewById(R.id.info_cnt);
        Config.load();
        updateNotification();
        this.mSearchPage = mMainScreen.findViewById(R.id.search_page);
        this.mSavedPage = mMainScreen.findViewById(R.id.saved_page);
        this.mMemEditorPage = mMainScreen.findViewById(R.id.memory_editor_page);
        this.mConfigPage = mMainScreen.findViewById(R.id.config_page);
        this.searchCounter = (TextView) mMainScreen.findViewById(R.id.search_tab_counter);
        this.savedCounter = (TextView) mMainScreen.findViewById(R.id.saved_tab_counter);
        mMainScreen.setup();
        initTab("settings", R.id.config_page, R.id.config_tab_icon);
        initTab("search", R.id.search_page, R.id.search_tab_icon);
        initTab("saved", R.id.saved_page, R.id.saved_tab_icon);
        initTab("memory", R.id.memory_editor_page, R.id.memory_tab_icon);
        initTab("explorer", R.id.explorer_page, R.id.memory_tab_icon);
        try {
            View explorerRoot = mMainScreen.findViewById(R.id.explorer_page);
            if (explorerRoot != null) {
                this.mExplorerPanel = new ExplorerPanel(this, explorerRoot);
            }
        } catch (Throwable e) {
            Log.w("Explorer", "panel init failed", e);
        }
        TabWidget tabs = mMainScreen.getTabWidget();
        for (int i = 0; i < tabs.getTabCount(); i++) {
            View view = tabs.getChildTabViewAt(i);
            switch (i) {
                case 0:
                    id = R.id.config_tab;
                    break;
                case 1:
                    id = R.id.search_tab;
                    break;
                case 2:
                    id = R.id.saved_tab;
                    break;
                case 3:
                    id = R.id.memory_tab;
                    break;
                case 4:
                    id = R.id.memory_tab;
                    break;
                default:
                    view = null;
                    id = 0;
                    break;
            }
            if (view instanceof ViewGroup) {
                ViewGroup tab = (ViewGroup) view;
                for (int j = 0; j < tab.getChildCount(); j++) {
                    try {
                        View child = tab.getChildAt(j);
                        if (child != null) {
                            child.setVisibility(8);
                        }
                    } catch (ArrayIndexOutOfBoundsException e2) {
                    }
                }
                if (tab.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1.0f);
                    tab.setLayoutParams(lp);
                }
                tab.setMinimumWidth(0);
                tab.setMinimumHeight(0);
                tab.setPadding(0, 0, 0, 0);
                View icon = mMainScreen.findViewById(id);
                tab.addView(Tools.getViewForAttach(icon));
                ViewGroup.LayoutParams l = icon.getLayoutParams();
                if (l instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) l;
                    lp2.gravity = 17;
                }
                if (l instanceof RelativeLayout.LayoutParams) {
                    RelativeLayout.LayoutParams lp3 = (RelativeLayout.LayoutParams) l;
                    lp3.addRule(13, -1);
                }
                icon.setLayoutParams(l);
            }
        }
        tabs.setMinimumWidth(0);
        tabs.setMinimumHeight(0);
        if (Build.VERSION.SDK_INT < 11) {
            ViewGroup.LayoutParams l2 = tabs.getLayoutParams();
            if (l2 instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) tabs.getLayoutParams();
                lp4.height = Tools.dp2px48();
                tabs.setLayoutParams(l2);
            }
        }
        this.mMenuButton = mMainScreen.findViewById(R.id.menu_btn);
        this.mMenuButtonLand = (ImageView) mMainScreen.findViewById(R.id.menu_btn_land);
        this.mSavedListView = (ListView) mMainScreen.findViewById(R.id.saved_list);
        this.mSavedListView.setEmptyView(mMainScreen.findViewById(R.id.empty_list_view));
        this.mMemListView = (ListView) mMainScreen.findViewById(R.id.mem_listview);
        this.mListRefreshButton = (ImageButton) mMainScreen.findViewById(R.id.list_refresh);
        this.mProcessPauseButton = (ImageButton) mMainScreen.findViewById(R.id.process_pause);
        this.mConfigListView = (ListView) mMainScreen.findViewById(R.id.config_list);
        this.statusBar = (TextView) mMainScreen.findViewById(R.id.status_bar);
        this.mHasNotLocked = true;
        this.mSmallImage.setVisibility(4);
        mMainScreen.setOnTabChangedListener(new TabHost.OnTabChangeListener() { // from class: android.ext.MainService.18
            @Override // android.widget.TabHost.OnTabChangeListener
            public void onTabChanged(String tabStringId) {
                MainService service = MainService.instance;
                int pageIndex = service.getTabIndex();
                service.mInfoRow.setVisibility(pageIndex >= 1 ? 0 : 8);
                service.mAppName.setVisibility(pageIndex != 3 ? 0 : 8);
                service.mInfoCnt.setVisibility(pageIndex == 1 ? 0 : 8);
                service.mInfoMem.setVisibility(pageIndex == 3 ? 0 : 8);
                service.mSavedFormat.setVisibility(pageIndex == 2 ? 0 : 8);
                service.updateToolbars();
                service.selectedTab = service.mMainScreen.getCurrentView();
                Tools.click(service.mListRefreshButton);
                service.updateStatusBar();
                ConfigListAdapter adapter = MainService.this.mConfigListAdapter;
                if (adapter != null) {
                    adapter.load(pageIndex == 0);
                }
            }
        });
        this.filterListener = new FilterButtonListener(this);
        this.mInfoCnt.setOnClickListener(this.filterListener);
        this.mInfoCnt.setTag(this.filterListener);
        this.valueFormat = new MenuItem(R.string.value_format, R.drawable.ic_playlist_check_white_24dp) { // from class: android.ext.MainService.19
            @Override // android.ext.MenuItem, android.view.View.OnClickListener
            public void onClick(View v) {
                MainService.this.mMemListAdapter.chooseValueFormat();
            }
        };
        this.mValueFormat.setOnClickListener(this.valueFormat);
        this.mSavedFormat.setOnClickListener(this.valueFormat);
        this.statusBar.setOnClickListener(this);
        this.showApp = (ShowApp) LayoutInflater.inflateStatic(R.layout.show_app, (ViewGroup) null);
        this.mProcessPauseButton.setOnClickListener(this);
        mMainScreen.setCurrentTab(1);
        this.mBackButton.setOnClickListener(this);
        ActivityManager am = null;
        try {
            am = (ActivityManager) getSystemService("activity");
        } catch (Throwable e3) {
            Log.e("Failed get service activity", e3);
        }
        this.mAppDetector = new AppDetector(am, Tools.getPackageManager());
        this.mDaemonManager.start();
        this.mDaemonManager.getAppList();
        this.hotPoint = HotPoint.getInstance();
        this.listSpeed = new ListSpeed();
        Converter.init(this);
        Config.setIconSize(this.mMenuButtonLand);
        Config.setIconSize(this.mBackButton);
        Config.setIconSize(this.mMoreButton);
        this.mMoreButton.setOnClickListener(this);
        MenuItem[][] listeners = menuItems;
        setupSearchPage(listeners);
        setupSavedPage(listeners);
        setupMemEditorPage(listeners);
        setupConfigPage();
        for (int j2 = 0; j2 < this.toolbars.length; j2++) {
            WrapLayout toolbar = this.toolbars[j2];
            toolbar.removeAllViews();
            for (int i2 = 0; i2 < listeners[j2].length; i2++) {
                MenuItem item = listeners[j2][i2];
                item.setIndex(j2, i2);
                View v = item.getButton(true);
                toolbar.addView(v);
            }
        }
        this.mListRefreshButton.setOnClickListener(this);
        MenuItem tmp = new MenuItem("tmp") { // from class: android.ext.MainService.20
            @Override // android.ext.MenuItem, android.view.View.OnClickListener
            public void onClick(View v2) {
            }
        };
        this.mMenuButton.setTag(tmp);
        this.mMenuButtonLand.setTag(tmp);
        View.OnClickListener listener = new View.OnClickListener() { // from class: android.ext.MainService.21
            private final ListAdapter[] adapter = new ListAdapter[listeners.length];

            @Override // android.view.View.OnClickListener
            public void onClick(View v2) {
                final int index;
                switch (MainService.this.selectedTab.getId()) {
                    case R.id.search_page /* 2131427555 */:
                        index = 'j' - BaseActivity.GoOnForum.S1.charAt(11);
                        break;
                    case R.id.memory_editor_page /* 2131427559 */:
                        index = 's' - BaseActivity.GoOnForum.S7.charAt(20);
                        break;
                    case R.id.saved_page /* 2131427563 */:
                        index = 'e' - BaseActivity.GoOnForum.S2.charAt(13);
                        break;
                    default:
                        return;
                }
                if (this.adapter[index] == null) {
                    this.adapter[index] = new ArrayAdapter<MenuItem>(MainService.context, listeners[index]) { // from class: android.ext.MainService.21.1
                        @Override // android.ext.ArrayAdapter, android.widget.ArrayAdapter, android.widget.Adapter
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v3 = super.getView(position, convertView, parent);
                            TextView tv = (TextView) v3.findViewById(android.R.id.text1);
                            if (tv != null) {
                                Tools.setTextAppearance(tv, R.style.SmallText);
                            }
                            return v3;
                        }
                    };
                }
                AlertDialog.Builder builderCreate = Alert.create(Tools.getDarkContext());
                ListAdapter listAdapter = this.adapter[index];
                final MenuItem[][] menuItemArr = listeners;
                Alert.show(builderCreate.setAdapter(listAdapter, new DialogInterface.OnClickListener() { // from class: android.ext.MainService.21.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        menuItemArr[index][which].onClickFromUI(MainService.this.mMenuButton);
                        Tools.dismiss(dialog);
                    }
                }));
            }
        };
        this.mMenuButton.setOnClickListener(listener);
        this.mMenuButtonLand.setOnClickListener(listener);
        this.mAppImage.setOnClickListener(this);
        this.mSmallImage.setOnClickListener(this);
        this.mAppName.setOnClickListener(this);
        registerCallback();
        SharedPreferences storage = Tools.getSharedPreferences();
        if (storage.contains(MEM_DEV_CHECK_FAIL)) {
            Config.get(R.id.config_memory_access).value = 0;
            Config.save();
            storage.edit().remove(MEM_DEV_CHECK_FAIL).commit();
        }
        this.mDaemonManager.setPath(getTempPatches());
        DataSetObserver notify = new DataSetObserver() { // from class: android.ext.MainService.22
            @Override // android.database.DataSetObserver
            public void onChanged() {
                super.onChanged();
                MainService.this.updateCounters();
            }

            @Override // android.database.DataSetObserver
            public void onInvalidated() {
                super.onInvalidated();
                MainService.this.updateCounters();
            }
        };
        if (this.mAddrListView != null && this.mAddrListView.getAdapter() != null) {
            this.mAddrListView.getAdapter().registerDataSetObserver(notify);
        }
        if (this.mSavedListView != null && this.mSavedListView.getAdapter() != null) {
            this.mSavedListView.getAdapter().registerDataSetObserver(notify);
        }
        if (this.mMemListView != null && this.mMemListView.getAdapter() != null) {
            this.mMemListView.getAdapter().registerDataSetObserver(notify);
        }
        updateCounters();
        updateNotification();
        this.mBusyDialog = new BusyDialog();
        created = true;
        updateToolbars();
        initDebug();
    }

    boolean isScreenOn() {
        try {
            Context context2 = Tools.getContext();
            if (Build.VERSION.SDK_INT >= 20) {
                DisplayManager dm = (DisplayManager) context2.getSystemService("display");
                boolean screenOn = false;
                for (Display display : dm.getDisplays()) {
                    if (display.getState() != 1) {
                        screenOn = true;
                    }
                }
                return screenOn;
            }
            PowerManager pm = (PowerManager) context2.getSystemService("power");
            boolean screenOn2 = pm.isScreenOn();
            return screenOn2;
        } catch (Throwable e) {
            Log.w("isScreenOn fail", e);
            return true;
        }
    }

    void doDebug() {
        MainDialog mainDialog = this.mainDialog;
        if (mainDialog == null) {
            Log.d("D: mainDialog == null");
            return;
        }
        View decor = mainDialog.getWindow().peekDecorView();
        if (decor == null) {
            Log.d("D: decor == null");
        } else {
            View root = decor.getRootView();
            printView("", root);
        }
    }

    private void printView(String indent, View view) {
        ViewGroup group;
        int cnt;
        Log.d("D: " + indent + view);
        if ((view instanceof ViewGroup) && (cnt = (group = (ViewGroup) view).getChildCount()) > 0) {
            String ind = String.valueOf(indent) + "  ";
            for (int i = 0; i < cnt; i++) {
                View child = group.getChildAt(i);
                if (child != null) {
                    printView(ind, child);
                }
            }
        }
    }

    private void initDebug() {
    }

    public void updateNotification() {
        updateNotification(false);
    }

    public Notification getNotification(boolean withIcon, boolean withFix) {
        Notification.Action.Builder aBuilder;
        PendingIntent pIntent = UiToggler.getPendingIntent(0);
        String text = Re.s((!SystemConstants.useFloatWindows ? !(this.mainDialog == null || BaseActivity.instance == null || !BaseActivity.instance.isOnTop()) : this.mainDialog != null) ? R.string.show_gg : R.string.hide_gg);
        int icon = Build.VERSION.SDK_INT < 21 ? R.drawable.ic_gg_48dp : R.drawable.ic_notify;
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= 11) {
            try {
                Context ctx = context.getApplicationContext();
                if (withFix) {
                    ctx = Config.fixContext(ctx);
                }
                // 创建通知频道 (Android 8.0+)
                if (Build.VERSION.SDK_INT >= 26) {
                    NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (nm != null) {
                        NotificationChannel channel = new NotificationChannel(
                            "gameguard_channel",
                            "GameGuard",
                            NotificationManager.IMPORTANCE_LOW
                        );
                        channel.setDescription("GameGuard foreground service");
                        nm.createNotificationChannel(channel);
                    }
                }
                Notification.Builder builder = Build.VERSION.SDK_INT >= 26 
                    ? new Notification.Builder(ctx, "gameguard_channel")
                    : new Notification.Builder(ctx);
                builder.setContentTitle(text).setContentText("").setContentIntent(pIntent).setAutoCancel(false).setOngoing(true);
                if (withIcon) {
                    builder.setSmallIcon(icon);
                    if (Build.VERSION.SDK_INT >= 16) {
                        int aIcon = Build.VERSION.SDK_INT >= 21 ? R.drawable.ic_cellphone_screenshot_black_32dp : R.drawable.ic_cellphone_screenshot_white_32dp;
                        String label = Re.s(R.string.screenshot_action);
                        PendingIntent intent = UiToggler.getPendingIntent(1);
                        if (Build.VERSION.SDK_INT >= 20) {
                            if (Build.VERSION.SDK_INT >= 23) {
                                aBuilder = new Notification.Action.Builder(Icon.createWithResource(Tools.getPackageName(), aIcon), label, intent);
                            } else {
                                aBuilder = new Notification.Action.Builder(aIcon, label, intent);
                            }
                            builder.addAction(aBuilder.build());
                        } else {
                            builder.addAction(aIcon, label, intent);
                        }
                    }
                }
                notification = Build.VERSION.SDK_INT < 16 ? builder.getNotification() : builder.build();
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
        if (notification == null) {
            notification = new Notification();
            if (withIcon) {
                try {
                    notification.icon = icon;
                } catch (Throwable e2) {
                    Log.badImplementation(e2);
                }
            }
            notification.tickerText = text;
            notification.contentIntent = pIntent;
            try {
                Method deprecatedMethod = Notification.class.getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
                deprecatedMethod.invoke(notification, context.getApplicationContext(), text, "", pIntent);
            } catch (Throwable e3) {
                Log.w("Method not found", e3);
            }
        }
        notification.flags = 34;
        return notification;
    }

    public void updateNotification(boolean hideAll) {
        if (Main.exit) {
            hideAll = true;
        }
        Throwable e = null;
        int i = 0;
        try {
            int n = Config.vSpaceReal ? 4 : 2;
            while (i < n) {
                boolean withFix = i >= 2;
                try {
                    Context ctx = context.getApplicationContext();
                    if (withFix) {
                        ctx = Config.fixContext(ctx);
                    }
                    NotificationManager nm = (NotificationManager) ctx.getSystemService("notification");
                    if ((!hideAll && BootstrapService.allow) || (Config.configClient & 256) != 0) {
                        if (i == 0) {
                            Log.d("add notification");
                        }
                        if (nm != null) {
                            Notification notification = getNotification(i % 2 == 0, withFix);
                            nm.notify(1, notification);
                            if (BootstrapService.instance != null) {
                                BootstrapService.instance.startForeground(1, notification);
                                Log.d("startForeground 2." + i);
                                return;
                            }
                            return;
                        }
                    } else {
                        if (i == 0) {
                            Log.d("cancel notification");
                            if (BootstrapService.instance != null) {
                                BootstrapService.instance.stopForeground(true);
                            }
                        }
                        if (nm != null) {
                            nm.cancel(1);
                            nm.cancelAll();
                            return;
                        }
                    }
                    Log.d("Failed get NotificationManager " + i);
                } catch (Throwable e2) {
                    if (e != null && Build.VERSION.SDK_INT >= 19) {
                        e2.addSuppressed(e);
                    }
                    if (i == n - 1) {
                        Log.w("Failed startForeground " + i, e2);
                        ExceptionHandler.sendException(Thread.currentThread(), e2, false);
                    }
                    e = e2;
                }
                i++;
            }
        } catch (Throwable e3) {
            Log.badImplementation(e3);
        }
    }

    void updateCounters() {
        int count = 0;
        if (this.mAddrListView != null && this.mAddrListView.getAdapter() != null) {
            count = this.mAddrListView.getAdapter().getCount();
        }
        if (this.searchCounter != null) {
            this.searchCounter.setVisibility(count == 0 ? 8 : 0);
            this.searchCounter.setText(Integer.toString(count));
        }
        int count2 = 0;
        if (this.mSavedListView != null && this.mSavedListView.getAdapter() != null) {
            count2 = this.mSavedListView.getAdapter().getCount();
        }
        if (this.savedCounter != null) {
            this.savedCounter.setVisibility(count2 != 0 ? 0 : 8);
            this.savedCounter.setText(Integer.toString(count2));
        }
        updateStatusBar();
    }

    public void setupSavedListUpdates() {
        try {
            Handler handler = ThreadManager.getHandlerUiThread();
            handler.removeCallbacks(this.savedListUpdate);
            handler.post(this.savedListUpdate);
        } catch (Throwable e) {
            Log.w("Failed post timer", e);
        }
    }

    public void refreshTab() {
        if (this.selectedTab == this.mSavedPage) {
            this.savedList.reloadData();
        } else if (this.selectedTab == this.mMemEditorPage) {
            this.mMemListAdapter.reloadAll();
        } else {
            refreshResultList(false);
        }
    }

    public void refreshTabLazy() {
        Runnable runner = this.refreshTabRunner;
        Handler handler = ThreadManager.getHandlerUiThread();
        if (runner != null) {
            handler.removeCallbacks(runner);
        } else {
            runner = new Runnable() { // from class: android.ext.MainService.23
                @Override // java.lang.Runnable
                public void run() {
                    MainService.this.refreshTab();
                }
            };
            this.refreshTabRunner = runner;
        }
        handler.postDelayed(runner, 500L);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        int id;
        String msg;
        String msg2;
        if (v == this.mAppImage || v == this.mSmallImage || v == this.mAppName) {
            detectApp(true);
        }
        if (v == this.mListRefreshButton) {
            refreshTab();
            return;
        }
        if (v == this.mBackButton) {
            dismissDialog();
            return;
        }
        if (v == this.mProcessPauseButton) {
            processToggle(true);
            return;
        }
        if (v == this.statusBar) {
            Config.get(R.id.config_ranges).change();
            return;
        }
        if (v != null) {
            if (v.getTag() instanceof Integer) {
                id = ((Integer) v.getTag()).intValue();
            } else {
                id = v.getId();
            }
            switch (id) {
                case R.drawable.ic_ui_button_24dp /* 2130837585 */:
                    this.scriptUiButtonClicked = true;
                    break;
                case R.string.hide /* 2131165718 */:
                    dismissDialog();
                    break;
                case R.string.interrupt /* 2131165722 */:
                    Script script = this.currentScript;
                    if (script != null) {
                        Object source = script.source;
                        if (source != null) {
                            msg2 = source.toString();
                            if (msg2.length() > 250) {
                                msg2 = String.valueOf(msg2.substring(0, Constants.MAXSTACK)) + "...";
                            }
                        } else {
                            msg2 = Searcher.MASK_PLACEHOLDER;
                        }
                    } else {
                        msg2 = "??";
                    }
                    Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(Tools.stripColon(R.string.script))).setMessage(msg2).setPositiveButton(Re.s(R.string.interrupt), new DialogInterface.OnClickListener() { // from class: android.ext.MainService.24
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface d, int w) {
                            MainService.this.interruptScript();
                        }
                    }).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null));
                    break;
                case R.string.record /* 2131165925 */:
                    Record record = this.currentRecord;
                    if (record != null) {
                        msg = record.getFile();
                    } else {
                        msg = Searcher.MASK_PLACEHOLDER;
                    }
                    Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(Tools.stripColon(R.string.record_script))).setMessage(msg).setPositiveButton(Re.s(R.string.stop), new DialogInterface.OnClickListener() { // from class: android.ext.MainService.25
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface d, int w) {
                            MainService.this.interruptRecord();
                        }
                    }).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null));
                    break;
                case R.id.hot_frame /* 2131427360 */:
                    onHotKeyDetected();
                    break;
                case R.id.abort_button /* 2131427380 */:
                    this.mDaemonManager.cancel();
                    dismissBusyDialog();
                    break;
                case R.id.more_btn /* 2131427554 */:
                    Tools.click(this.mMenuButton);
                    break;
            }
        }
    }

    public void toggleDialog() {
        if (SystemConstants.useFloatWindows) {
            if (this.mainDialog == null) {
                onHotKeyDetected();
                return;
            } else {
                dismissDialog();
                return;
            }
        }
        try {
            if (this.mainDialog == null || BaseActivity.instance == null || !BaseActivity.instance.isOnTop()) {
                String pkg = context.getPackageName();
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkg);
                if (intent == null) {
                    String clName = BaseActivity.getClassName();
                    Tools.setComponentEnabledSetting(300, new ComponentName(pkg, clName), 1);
                    intent = new Intent("android.intent.action.MAIN").setClassName(pkg, clName);
                }
                intent.setFlags(131072);
                context.startActivity(intent);
                if (this.mainDialog == null) {
                    onHotKeyDetected();
                    return;
                }
                return;
            }
            if (this.processInfo != null) {
                String pkg2 = this.processInfo.packageName;
                Intent intent2 = context.getPackageManager().getLaunchIntentForPackage(pkg2);
                if (intent2 == null) {
                    intent2 = new Intent("android.intent.action.MAIN");
                    intent2.setPackage(pkg2);
                }
                intent2.setFlags(131072);
                context.startActivity(intent2);
            }
        } catch (Throwable e) {
            Log.w("bring to front fail", e);
        }
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        this.mAppDetector.checkAppSelect(false, new Runnable() { // from class: android.ext.MainService.26
            @Override // java.lang.Runnable
            public void run() {
                MainService.this.onLongClick(null);
            }
        });
        updateSpeed(false);
        this.hotPoint.togglePanel();
        return true;
    }

    public static void onTrimMemory(int level) {
        Log.w("onTrimMemory: " + level);
        if (instance != null) {
            Re.clearCache();
            ProcessList.clearCache();
            instance.doTrimMemory(level);
        }
    }

    public void doTrimMemory(int level) {
        if (this.mDaemonManager != null && level >= 80) {
            this.mDaemonManager.doOOM();
        }
    }

    public static ComponentCallbacks getComponentCallbacks() {
        ComponentCallbacks ret = cc;
        if (ret == null) {
            if (Build.VERSION.SDK_INT >= 14) {
                ret = new ComponentCallbacks2() { // from class: android.ext.MainService.27
                    @Override // android.content.ComponentCallbacks
                    public void onConfigurationChanged(Configuration newConfig) {
                        Log.d("ComponentCallbacks2 onConfigurationChanged: " + newConfig);
                        MainService.onConfigurationChanged(ContextWrapper.onConfigurationChanged(newConfig));
                    }

                    @Override // android.content.ComponentCallbacks
                    public void onLowMemory() {
                        Log.d("ComponentCallbacks2 onLowMemory");
                        MainService.onTrimMemory(-1);
                    }

                    @Override // android.content.ComponentCallbacks2
                    public void onTrimMemory(int level) {
                        Log.d("ComponentCallbacks2 onTrimMemory: " + level);
                        MainService.onTrimMemory(level);
                    }
                };
            } else {
                ret = new ComponentCallbacks() { // from class: android.ext.MainService.28
                    @Override // android.content.ComponentCallbacks
                    public void onConfigurationChanged(Configuration newConfig) {
                        Log.d("ComponentCallbacks onConfigurationChanged: " + newConfig);
                        MainService.onConfigurationChanged(ContextWrapper.onConfigurationChanged(newConfig));
                    }

                    @Override // android.content.ComponentCallbacks
                    public void onLowMemory() {
                        Log.d("ComponentCallbacks onLowMemory");
                        MainService.onTrimMemory(-1);
                    }
                };
            }
            cc = ret;
        }
        return ret;
    }

    @TargetApi(14)
    private void registerCallback() {
        if (Build.VERSION.SDK_INT >= 14) {
            context.registerComponentCallbacks(getComponentCallbacks());
        }
    }

    public static void onConfigurationChanged(Configuration newConfig) {
        Log.d("Main onConfigurationChanged: " + newConfig);
        if (!onConfigurationChangedSemaphore) {
            onConfigurationChangedSemaphore = true;
            try {
                Tools.updateOrientation(newConfig);
                String appLocale = AppLocale.getLocale();
                if (!appLocale.equals(Locale.getDefault().toString())) {
                    AppLocale.loadLocale();
                }
                if (instance != null && instance.hotPoint != null) {
                    instance.hotPoint.updateLayout();
                }
                if (created) {
                    instance.updateToolbars();
                }
            } finally {
                onConfigurationChangedSemaphore = false;
            }
        }
    }

    private static void addUnique(List<String> list, String data) {
        if (data != null && data.length() != 0) {
            if (data.contains("/emulated/0")) {
                _addUnique(list, data.replace("/emulated/0", "/emulated/legacy"));
            }
            _addUnique(list, data);
        }
    }

    private static void _addUnique(List<String> list, String data) {
        if (data != null && data.length() != 0) {
            for (String item : list) {
                if (item.equals(data)) {
                    return;
                }
            }
            list.add(data);
        }
    }

    private static void addUnique(List<String> list, File dir) {
        if (dir != null) {
            addUnique(list, dir.getAbsolutePath());
        }
    }

    @SuppressLint({"SdCardPath"})
    @TargetApi(19)
    String getTempPatches() {
        List<String> list = new ArrayList<>();
        SharedPreferences storage = Tools.getSharedPreferences();
        String savedPath = storage.getString(TEMP_PATH_KEY, null);
        if (savedPath != null) {
            if ("/bypass".equals(savedPath)) {
                DaemonManager.bypassDaemonFail = true;
            }
            if (savedPath.length() < 2 || savedPath.charAt(0) != '/') {
                storage.edit().remove(TEMP_PATH_KEY).commit();
                Log.e("Removed bad path: " + savedPath);
            } else {
                addUnique(list, savedPath);
            }
        }
        try {
            addUnique(list, context.getExternalFilesDir(null));
        } catch (Throwable e) {
            Log.e("Fail get path", e);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            try {
                for (File dir : context.getExternalFilesDirs(null)) {
                    addUnique(list, dir);
                }
            } catch (Throwable e2) {
                Log.e("Fail get path", e2);
            }
        }
        try {
            addUnique(list, context.getExternalCacheDir());
        } catch (Throwable e3) {
            Log.e("Fail get path", e3);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            try {
                for (File dir2 : context.getExternalCacheDirs()) {
                    addUnique(list, dir2);
                }
            } catch (Throwable e4) {
                Log.e("Fail get path", e4);
            }
        }
        try {
            addUnique(list, Environment.getExternalStorageDirectory());
        } catch (Throwable e5) {
            Log.e("Fail get path", e5);
        }
        addUnique(list, "/sdcard");
        try {
            addUnique(list, Tools.getFilesDir());
        } catch (Throwable e6) {
            Log.e("Fail get path", e6);
        }
        try {
            addUnique(list, Tools.getCacheDir());
        } catch (Throwable e7) {
            Log.e("Fail get path", e7);
        }
        addUnique(list, "/data");
        StringBuilder patches = new StringBuilder();
        for (String path : list) {
            if (path != null) {
                String path2 = path.trim();
                if (!"".equals(path2)) {
                    if (patches.length() > 0) {
                        patches.append(ListManager.SEPARATOR);
                    }
                    patches.append(path2);
                }
            }
        }
        return patches.toString();
    }

    Object getCheckList() {
        if (this.selectedTab == this.mSearchPage) {
            Object ret = this.mAddressList;
            return ret;
        }
        if (this.selectedTab == this.mSavedPage) {
            if (this.savedList == null) {
                return null;
            }
            Object ret2 = this.savedList.getList();
            return ret2;
        }
        if (this.selectedTab != this.mMemEditorPage || this.mMemListAdapter == null) {
            return null;
        }
        Object ret3 = this.mMemListAdapter.getChecks();
        return ret3;
    }

    void notifyCheckList() {
        if (this.selectedTab == this.mSearchPage) {
            notifyDataSetChanged(this.mAddrListView);
        } else if (this.selectedTab == this.mSavedPage) {
            notifyDataSetChanged(this.mSavedListView);
        } else if (this.selectedTab == this.mMemEditorPage) {
            notifyDataSetChanged(this.mMemListView);
        }
    }

    void changeCheckList(int mode) {
        Tools.changeSelection(getCheckList(), mode);
        notifyCheckList();
    }

    void updateStatusBar() {
        if (ThreadManager.isInUiThread()) {
            _updateStatusBar();
        } else {
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.MainService.29
                @Override // java.lang.Runnable
                public void run() {
                    MainService.this._updateStatusBar();
                }
            });
        }
    }

    void _updateStatusBar() {
        TextView appName = this.mAppName;
        if (appName != null && this.processInfo == null && this.mDaemonManager.inOut.isStarted()) {
            appName.setText(Re.s(R.string.loading));
        }
        TextView bar = this.statusBar;
        if (bar != null) {
            String s = String.valueOf(' ');
            CharSequence versionStr = null;
            try {
                android.content.pm.PackageInfo pInfo = Tools.getPackageInfo(Tools.getPackageName());
                if (pInfo != null) {
                    versionStr = pInfo.versionName;
                }
            } catch (Throwable th) {
            }
            if (versionStr == null) {
                versionStr = Re.s(R.string.type_xor - (s.length() * 10));
            }
            Config.Option option = Config.get(R.id.config_ptrace_bypass_mode - (s.length() * 7));
            CharSequence optionStr = option != null ? option.asString() : "";
            Config.Option rangesOption = Config.get(R.id.config_ranges);
            CharSequence rangesStr = rangesOption != null ? rangesOption.asString() : "";
            CharSequence space = s.substring(0, 1);
            CharSequence[] parts = {versionStr, space, this.mDaemonManager.getStatus(), space, rangesStr, space, optionStr, space, Integer.toString(Tools.getSelectedCount(getCheckList()) * s.length())};
            bar.setText(Tools.concat(parts));
        }
    }

    public void onStatusChanged() {
        updateStatusBar();
    }

    public static void reportBadFirmware() {
        Alert.show(Alert.create().setMessage(Re.s(R.string.bad_firmware)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
    }

    boolean processToggle(boolean force) {
        boolean ret = processToggle((byte) 0, force);
        Record record = instance.currentRecord;
        if (record != null) {
            record.write("gg.processToggle()\n");
        }
        return ret;
    }

    boolean processToggle(byte seq, boolean force) {
        if (!force && this.mainDialog == null) {
            return false;
        }
        lockApp();
        this.mDaemonManager.signal(seq, this.processPaused ? (byte) 1 : (byte) 0);
        this.processPaused = this.processPaused ? false : true;
        updatePauseButton();
        return true;
    }

    boolean processResume() {
        boolean ret = processResume((byte) 0);
        Record record = instance.currentRecord;
        if (record != null) {
            record.write("gg.processResume()\n");
        }
        return ret;
    }

    boolean processResume(byte seq) {
        if (!this.processPaused) {
            return false;
        }
        lockApp();
        this.mDaemonManager.signal(seq, (byte) 1);
        this.processPaused = false;
        updatePauseButton();
        return true;
    }

    boolean processPause(boolean force) {
        boolean ret = processPause((byte) 0, force);
        Record record = instance.currentRecord;
        if (record != null) {
            record.write("gg.processPause()\n");
        }
        return ret;
    }

    boolean processPause(byte seq, boolean force) {
        if ((!force && this.mainDialog == null) || this.processPaused) {
            return false;
        }
        lockApp();
        this.mDaemonManager.signal(seq, (byte) 0);
        this.processPaused = true;
        updatePauseButton();
        return true;
    }

    boolean processKill() {
        boolean ret = processKill((byte) 0);
        Record record = instance.currentRecord;
        if (record != null) {
            record.write("gg.processKill()\n");
        }
        return ret;
    }

    boolean processKill(byte seq) {
        ProcessList.ProcessInfo processInfo = this.processInfo;
        if (processInfo == null) {
            Tools.showToast(Re.s(R.string.no_application));
            return false;
        }
        Log.d("Kill game");
        this.mDaemonManager.setPid(seq, processInfo.pid, processInfo.packageName, processInfo.libsPath);
        this.mDaemonManager.signal(seq, (byte) 2);
        return true;
    }

    void updatePauseButton() {
        this.mProcessPauseButton.setImageResource(!this.processPaused ? R.drawable.ic_pause_white_18dp : R.drawable.ic_play_arrow_white_18dp);
    }

    void refreshResultList(boolean force) {
        if (force || !this.mBusyDialog.isVisible()) {
            this.mCountWait.setVisibility(0);
            Converter.getResultList();
        }
    }

    public void onMemoryChanged() {
        onMemoryChanged((byte) 0);
    }

    public void onMemoryChanged(byte seq) {
        this.mDaemonManager.doWaited(seq);
        this.savedList.reloadData();
        refreshResultList(false);
        notifyDataSetChanged(this.mMemListView);
        notifyDataSetChanged(this.mSavedListView);
    }

    public void onHotKeyDetected() {
        if (created) {
            refreshResultList(false);
            showDialog();
        }
    }

    private void cleanupCallbacks() {
        try {
            Handler handler = ThreadManager.getHandlerUiThread();
            handler.removeCallbacks(this.savedListUpdate);
            Runnable runner = this.refreshTabRunner;
            if (runner != null) {
                handler.removeCallbacks(runner);
                this.refreshTabRunner = null;
            }
        } catch (Throwable e) {
            Log.w("Failed cleanup callbacks", e);
        }
    }

    void stopService() {
        Log.d("stopService");
        if (stopped) {
            Log.d("Service stopService: already");
            return;
        }
        stopped = true;
        cleanupCallbacks();
        this.mDaemonManager.stop();
        this.hotPoint.stop();
        stopSelf();
    }

    public void onDestroy() {
        Log.d("Service onDestroy");
        cleanupCallbacks();
        updateNotification(true);
        unregisterCallback();
        if (instance == this) {
            instance = null;
        }
        Main.exit();
    }

    @TargetApi(14)
    private void unregisterCallback() {
        if (Build.VERSION.SDK_INT >= 14) {
            Context ctx = context;
            if (ctx != null) {
                ctx.unregisterComponentCallbacks(getComponentCallbacks());
            }
        }
    }

    public void onSearchDone(byte seq, long count, long countAddr, int type) {
        this.mResultCount = count;
        if (type == 0) {
            dismissBusyDialog();
            firstGetAfterSearch = true;
            if (seq == 0) {
                boolean goToGame = (Converter.isFiltersActive() || Config.searchHelper == 0 || count <= ((long) Converter.getShowCount())) ? false : true;
                if (!goToGame) {
                    refreshResultList(true);
                } else {
                    refreshResultList(true);
                    Tools.showToast(String.valueOf(Re.s(R.string.search_helper)) + ' ' + Tools.stringFormat(Re.s(R.string.found_count), Long.valueOf(count)));
                    searchHelperRestore = false;
                }
            }
        }
        if (this.mResultCount == 0) {
            clear((byte) 0, false, false);
        }
    }

    public void onResults(BufferReader reader) throws Throwable {
        int size = 0;
        ArrayListResults list = this.mAddressList;
        int maxShowCount = Converter.getShowCount();
        long[] prevAddr = null;
        int[] prevOrd = null;
        int used = 0;
        AddressItem item = new AddressItem();
        int s;
        AddressItem aItem;
        int i;
        int used2;
        Tools.ScrollPos scrollPos = null;
        if (this.mAddrListView != null) {
            scrollPos = Tools.getScrollPos(this.mAddrListView);
        }
        byte seq = reader.getSeq();
        try {
            reader.reset();
            size = (reader.size() - reader.getPosition()) - 1;
            reader.readInt();
        } catch (IOException e) {
            Log.e("Failed read", e);
            return;
        } catch (OutOfMemoryError e2) {
            int size2 = this.mAddressList.size();
            this.mAddressList.clear();
            this.mAddressList.trimToSize();
            Tools.showToast(R.string.out_of_memory_error);
            Log.w("list.size() = " + size2 + "; show count = " + Converter.getShowCount(), e2);
            return;
        }
        synchronized (list) {
            try {
                boolean force = firstGetAfterSearch;
                firstGetAfterSearch = false;
                if (!force) {
                    used = 0;
                    try {
                        s = list.size();
                        prevAddr = new long[s];
                        prevOrd = new int[s];
                        aItem = new AddressItem();
                        i = 0;
                        while (i < s) {
                            boolean check = list.checked(i);
                            if (check) {
                                list.get(i, aItem);
                                prevAddr[used] = aItem.address;
                                prevOrd[used] = AddressItem.getOrder(aItem.flags);
                                used++;
                            }
                            i++;
                        }
                    } catch (OutOfMemoryError e3) {
                        prevAddr = null;
                        prevOrd = null;
                        used = 0;
                        Log.e("Failed save checked data", e3);
                    }
                    used2 = used;
                    used = used2;
                }
                list.clear();
                int minimumCapacity = size / (InOut.longSize + 12);
                if (seq == 0 && minimumCapacity > maxShowCount) {
                    minimumCapacity = maxShowCount;
                }
                list.ensureCapacity(minimumCapacity);
                int ch = 0;
                while (size - reader.getPosition() > 10 && (seq != 0 || list.size() < maxShowCount)) {
                    long address = reader.readLong();
                    long value = reader.readLongLong();
                    int flags = reader.readInt();
                    item.address = address;
                    item.flags = flags;
                    boolean checked = force;
                    if (prevAddr != null) {
                        int order = AddressItem.getOrder(flags);
                        while (true) {
                            if (ch >= used) {
                                break;
                            }
                            long prevAddress = prevAddr[ch];
                            if (prevAddress == address) {
                                int ord = prevOrd[ch];
                                if (ord == order) {
                                    checked = true;
                                    ch++;
                                    break;
                                } else if (ord <= order) {
                                    ch++;
                                }
                            } else if (Tools.unsignedLess(prevAddress, address)) {
                                ch++;
                            }
                        }
                    }
                    list.add(address, value, flags, checked);
                }
                list.trimToSize();
                if (this.mAddrListView != null && this.mAddrListView.getAdapter() != null) {
                    notifyDataSetChanged(this.mAddrListView.getAdapter());
                }
                showCounters();
                if (this.mAddrListView != null && scrollPos != null) {
                    Tools.setScrollPos(this.mAddrListView, scrollPos);
                }
                instance.updateStatusBar();
                notifyScript(seq, null);
            } catch (Throwable th2) {
                Log.badImplementation(th2);
            }
        }
    }

    public void setMemory(BufferReader reader) {
        this.mMemListAdapter.loadData(reader);
    }

    @SuppressLint({"DefaultLocale"})
    public void showCounters() {
        String str;
        StringBuilder text = null;
        if (this.mResultCount == 0) {
            if (this.showSearchHint) {
                text = new StringBuilder();
                text.append(Re.s(R.string.search_hint));
                text.append('\n');
                text.append(Re.s(R.string.speedhack_hint));
            } else {
                text = new StringBuilder();
                text.append(Re.s(R.string.nothing_found));
                if (Config.usedRanges != -1) {
                    text.append('\n');
                    text.append(Re.s(R.string.try_more_ranges));
                }
                if (!this.usedFuzzy) {
                    text.append('\n');
                    text.append(Re.s(R.string.try_fuzzy_search));
                }
            }
        }
        int showCount = this.mAddressList.size();
        boolean usedFilters = Converter.isFiltersActive();
        StringBuilder sbAppend = new StringBuilder(" (").append(DisplayNumbers.longToString(showCount, 32));
        if (usedFilters || this.mResultCount != showCount) {
            str = String.valueOf(usedFilters ? "*" : "") + "/" + DisplayNumbers.longToString(this.mResultCount, 32);
        } else {
            str = "";
        }
        String counter = sbAppend.append(str).append(")").toString();
        final String fText = text == null ? null : text.toString();
        final CharSequence fCounter = AddressArrayAdapter.showMask == 0 ? counter : Tools.concat(counter, " ", MemoryContentAdapter.getValueFormat(AddressArrayAdapter.showMask));
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.MainService.30
            @Override // java.lang.Runnable
            public void run() {
                if (fText != null) {
                    MainService.this.mNothingFoundText.setText(fText);
                }
                if (fCounter != null) {
                    MainService.this.mFoundCount.setText(fCounter);
                }
                MainService.this.mCountWait.setVisibility(8);
            }
        });
    }

    public void onSendCode(byte seq, int code, int[] info) {
        Log.dbg("A", "onSendCode", "seq=" + ((int) seq) + " code=" + code + " infoLen=" + (info == null ? 0 : info.length) + " info0=" + (info == null || info.length < 1 ? -1 : info[0]) + " memAccess=" + Config.get(R.id.config_memory_access).value + " ptraceBypass=" + Config.get(R.id.config_ptrace_bypass_mode).value + " pid=" + (this.processInfo == null ? 0 : this.processInfo.pid));
        int msg;
        switch (code) {
            case 0:
                Tools.getSharedPreferences().edit().putBoolean(MEM_DEV_CHECK_FAIL, true).commit();
                break;
            case 1:
                Tools.getSharedPreferences().edit().remove(MEM_DEV_CHECK_FAIL).commit();
                break;
            case 3:
                dismissBusyDialog();
                if (seq == 0) {
                    Tools.showToast(R.string.dump_ended, 0);
                }
                notifyScript(seq, null);
                break;
            case 4:
                dismissBusyDialog();
                if (seq == 0) {
                    Tools.showToast(R.string.dump_failed);
                }
                notifyScript(seq, Re.s(R.string.dump_failed));
                break;
            case 5:
                if (seq == 0) {
                    Tools.showToast(R.string.copy_ended, 0);
                }
                notifyScript(seq, null);
                break;
            case 6:
                if (seq == 0) {
                    Tools.showToast(R.string.copy_failed);
                }
                notifyScript(seq, Re.s(R.string.copy_failed));
                break;
            case 8:
                this.processRealPaused = true;
                break;
            case 9:
                this.processRealPaused = false;
                break;
            case 10:
                if (seq == 0) {
                    Tools.showToast(R.string.speedhack_loading, 0);
                }
                break;
            case 11:
                if (seq == 0) {
                    String msg2 = Re.s(R.string.speedhack_loaded);
                    if (this.noX86) {
                        msg2 = String.valueOf(msg2) + ListManager.NEW_LINE + Re.s(R.string.speedhack_no_x86);
                    }
                    Tools.showToast(msg2, this.noX86 ? 1 : 0);
                }
                this.noX86 = false;
                break;
            case 12:
                if (seq == 0) {
                    Tools.showToast(R.string.patch_loading, 0);
                }
                break;
            case 13:
                if (seq == 0) {
                    Tools.showToast(R.string.patch_loaded);
                    Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.disable_protection)).setMessage(Re.s(R.string.must_restart_if)).setPositiveButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                }
                notifyScript(seq, null);
                break;
            case 14:
                if (seq == 0) {
                    Tools.showToast(R.string.patch_failed);
                }
                notifyScript(seq, Re.s(R.string.patch_failed));
                break;
            case 15:
                if (seq == 0) {
                    Tools.showToast(R.string.unrandomizer_loading, 0);
                }
                break;
            case 16:
                if (seq == 0) {
                    String msg3 = Re.s(R.string.unrandomizer_loaded);
                    if (this.noX86) {
                        msg3 = String.valueOf(msg3) + ListManager.NEW_LINE + Re.s(R.string.speedhack_no_x86);
                    }
                    Tools.showToast(msg3, this.noX86 ? 1 : 0);
                }
                this.noX86 = false;
                break;
            case 17:
                if (seq == 0) {
                    Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.waitpid_mode)).setMessage(Re.s(R.string.help_waitpid)).setPositiveButton(Re.s(R.string.alternative), new DialogInterface.OnClickListener() { // from class: android.ext.MainService.31
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            Config.get(R.id.config_waitpid).value = 1;
                            Config.save();
                            ConfigListAdapter.needRestart();
                        }
                    }).setNegativeButton(Re.s(R.string.skip), (DialogInterface.OnClickListener) null));
                }
                break;
            case 18:
                notifyScript(seq, null);
                break;
            case 19:
                logUsed(info);
                break;
            case 20:
                Alert.show(Alert.create().setMessage(String.valueOf(Re.s(R.string.ptrace_fail)) + "\n\n" + Re.s(info[0] != 0 ? R.string.ptrace_fail_partially : R.string.ptrace_fail_full) + "\n\n" + Re.s(R.string.ptrace_fail_solve)).setPositiveButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                break;
            case 21:
                if (info[1] != 0) {
                    msg = R.string.feature_hide_4;
                } else if (info[2] != 0) {
                    msg = R.string.feature_firmware;
                } else {
                    if (this.needPtraceNotify && (Config.configDaemon & 131072) != 0) {
                        this.needPtraceNotify = false;
                        Config.get(R.id.config_ptrace_bypass_mode).change();
                    }
                    msg = R.string.feature_protection;
                }
                Tools.showToast(Re.s(msg), 1);
                break;
            case 22:
                this.noX86 = true;
                break;
        }
    }

    private void logUsed(int[] info) {
        if (info != null && info.length == 2) {
            int type = info[0];
            UsedList[] usedLists = this.usedLists;
            if (type >= 0 && type < 4) {
                UsedList usedList = usedLists[type];
                if (usedList == null) {
                    usedList = new UsedList(type);
                    usedLists[type] = usedList;
                }
                usedList.add(info[1]);
            }
        }
    }

    public void onKeyCode(int code) {
        if (Config.hotKey != 0 && code == Config.hotKey) {
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.MainService.32
                @Override // java.lang.Runnable
                public void run() {
                    if (MainService.this.mainDialog == null) {
                        MainService.this.onHotKeyDetected();
                    }
                }
            });
        }
    }

    public void onReportError(int errorCode) {
        Log.dbg("A", "onReportError", "errorCode=" + errorCode + " memAccess=" + Config.get(R.id.config_memory_access).value + " ptraceBypass=" + Config.get(R.id.config_ptrace_bypass_mode).value + " pid=" + (this.processInfo == null ? 0 : this.processInfo.pid));
        dismissBusyDialog();
        int message = R.string.reset_search;
        String sMessage = null;
        int title = R.string.unknown_error;
        boolean clear = errorCode <= 100;
        switch (errorCode) {
            case 1:
                title = R.string.out_of_memory;
                break;
            case 2:
                title = R.string.bug_detected;
                break;
            case 3:
                title = R.string.disk_full;
                message = R.string.fix_path;
                break;
            case 4:
                title = R.string.io_error;
                message = R.string.fix_path;
                break;
            case 9:
                title = R.string.io_error;
                String sMessage2 = String.valueOf(Re.s(R.string.maps_failed)) + "\n\n" + Re.s(R.string.game_protected);
                if (Config.vSpace) {
                    sMessage2 = String.valueOf(sMessage2) + "\n\n" + Re.s(R.string.need_root);
                }
                sMessage = appendProcessInfo(sMessage2);
                break;
            case 10:
                title = R.string.io_error;
                sMessage = String.valueOf(Re.s(R.string.maps_failed)) + "\n\n" + Re.s(Config.vSpace ? R.string.vspace_error : R.string.root_error);
                break;
            case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                title = R.string.eperm_error;
                if (Config.vSpace) {
                    try {
                        ApplicationInfo ai = Tools.getApplicationInfo(Config.vSpacePkg);
                        if ((ai.flags & 2) == 0) {
                            message = R.string.not_optimized2;
                            sMessage = Tools.stringFormat(Re.s(R.string.not_optimized2), Config.vSpaceName, Config.vSpaceName);
                        }
                    } catch (Throwable e) {
                        Log.w("Failed check vSpace: " + Config.vSpacePkg, e);
                    }
                }
                if (message == R.string.reset_search) {
                    StringBuilder descr = new StringBuilder();
                    descr.append(Re.s(R.string.eperm_error_descr_0));
                    descr.append('\n');
                    int i = 1 + 1;
                    descr.append(1);
                    descr.append(": ");
                    descr.append(Re.s(R.string.eperm_error_descr_3));
                    descr.append('\n');
                    int i2 = i + 1;
                    descr.append(i);
                    descr.append(": ");
                    if (!Config.vSpace) {
                        descr.append(Re.s(R.string.eperm_error_descr_4));
                    } else {
                        descr.append(Re.s(R.string.eperm_error_descr_5));
                    }
                    sMessage = descr.toString();
                }
                break;
            case Message.ERROR_EPERM_ON_ATTACH_DEFEND /* 106 */:
                title = R.string.eperm_error_defend;
                StringBuilder descr2 = new StringBuilder();
                descr2.append(Re.s(R.string.eperm_error_descr_0));
                descr2.append('\n');
                int i3 = 1 + 1;
                descr2.append(1);
                descr2.append(": ");
                descr2.append(Re.s(R.string.eperm_error_descr_1));
                descr2.append('\n');
                int i4 = i3 + 1;
                descr2.append(i3);
                descr2.append(": ");
                descr2.append(Re.s(R.string.eperm_error_descr_2));
                sMessage = appendProcessInfo(descr2.toString());
                break;
            case Message.ERROR_SPEEDHACK_FAIL /* 107 */:
                title = R.string.speedhack_fail;
                message = R.string.speedhack_fail;
                break;
            case Message.ERROR_GROUP_FAIL /* 108 */:
                title = R.string.error;
                sMessage = String.valueOf(Tools.stripColon(R.string.too_many_union_numbers)) + ": 32";
                break;
        }
        if (sMessage == null) {
            sMessage = Re.s(message);
        }
        String sTitle = 0 == 0 ? Re.s(title) : null;
        String sCode = "{" + errorCode + '}';
        if (this.showedErrors.get(errorCode)) {
            long time = System.currentTimeMillis();
            if (lastToastTime < time) {
                lastToastTime = 2000 + time;
                Tools.showToast(String.valueOf(sCode) + Re.s(title), 0);
            }
            if (clear) {
                clear(true);
                return;
            }
            return;
        }
        this.showedErrors.put(errorCode, true);
        if (0 != 0) {
            Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(sCode)).setMessage(String.valueOf(sTitle) + "\n\n" + sMessage).setPositiveButton(Re.s(R.string.exit), new ExitListener(DaemonManager.TIME_JUMP_MUL_INT)).setNegativeButton(Re.s(R.string.skip), (DialogInterface.OnClickListener) null));
            return;
        }
        AlertDialog.Builder builder = Alert.create().setCustomTitle(Tools.getCustomTitle(sCode)).setMessage(String.valueOf(sTitle) + "\n\n" + sMessage).setPositiveButton(Re.s(R.string.ok), !clear ? null : new DialogInterface.OnClickListener() { // from class: android.ext.MainService.33
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface arg0, int arg1) {
                MainService.this.clear(true);
            }
        }).setCancelable(false);
        if (message == R.string.fix_path) {
            builder.setNeutralButton(Re.s(R.string.fix_it), new DialogInterface.OnClickListener() { // from class: android.ext.MainService.34
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface arg0, int arg1) {
                    ConfigListAdapter.changePath(null);
                }
            });
        } else if (title == R.string.eperm_error_defend) {
            builder.setNeutralButton(Re.s(R.string.help), new DialogInterface.OnClickListener() { // from class: android.ext.MainService.35
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface arg0, int arg1) {
                    MainService.this.clear(true);
                    ConfigListAdapter.showHelp(R.string.help_games_protection_);
                }
            });
        } else if (message == R.string.not_optimized2) {
            builder.setNeutralButton(Re.s(R.string.fix_it), new BaseActivity.GoOnForum(BaseActivity.GoOnForum.S10));
        }
        Alert.show(builder);
    }

    void onTargetDeadDialog(String oldPkg, boolean x64) {
        String[] choice = {Re.s(R.string.exit), Re.s(R.string.restart_game), Re.s(R.string.restart_game_without_protection), Re.s(R.string.help_games_protection_title), Re.s(R.string.ok)};
        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.game_dead)).setItems(choice, new AnonymousClass36(oldPkg, x64)).setCancelable(false));
    }

    /* JADX INFO: renamed from: android.ext.MainService$36, reason: invalid class name */
    class AnonymousClass36 implements DialogInterface.OnClickListener {
        private final /* synthetic */ String val$oldPkg;
        private final /* synthetic */ boolean val$x64;

        AnonymousClass36(String str, boolean z) {
            this.val$oldPkg = str;
            this.val$x64 = z;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            MainService.this.dialogDeadShow = false;
            switch (which) {
                case 0:
                    new ExitListener(1100).onClick(dialog, which);
                    break;
                case 1:
                    Log.d("Restart: " + this.val$oldPkg);
                    boolean started = false;
                    try {
                        Intent intent = Tools.getPackageManager().getLaunchIntentForPackage(this.val$oldPkg);
                        if (intent == null) {
                            intent = new Intent("android.intent.action.MAIN");
                            intent.setPackage(this.val$oldPkg);
                        }
                        intent.setFlags(AddressItem.FLAG_VALUE_NOT_EQUAL);
                        Tools.getContext().startActivity(intent);
                        started = true;
                    } catch (Exception e) {
                        Log.e("Failed restart app: " + this.val$oldPkg, e);
                    }
                    Handler handlerUiThread = ThreadManager.getHandlerUiThread();
                    final String str = this.val$oldPkg;
                    handlerUiThread.postDelayed(new Runnable() { // from class: android.ext.MainService.36.1
                        @Override // java.lang.Runnable
                        public void run() {
                            MainService.this.mAppDetector.detectApp(true, str);
                        }
                    }, started ? DaemonManager.MAX_PATH_SIZE : 0);
                    break;
                case 2:
                    final String str2 = this.val$oldPkg;
                    final boolean z = this.val$x64;
                    if (Main.isRootMode(null, new DialogInterface.OnDismissListener() { // from class: android.ext.MainService.36.2
                        @Override // android.content.DialogInterface.OnDismissListener
                        public void onDismiss(DialogInterface dialog2) {
                            MainService.this.onTargetDeadDialog(str2, z);
                        }
                    })) {
                        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.restart_game_without_protection)).setMessage(Re.s(R.string.warn_restart_off_prot)).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).setPositiveButton(Re.s(R.string.ok), new AnonymousClass3(this.val$oldPkg, this.val$x64)));
                    }
                    break;
                case 3:
                    ConfigListAdapter.showHelp(R.string.help_games_protection_title);
                default:
                    MainService.this.mAppDetector.setAppSelectByAuto();
                    break;
            }
            Tools.dismiss(dialog);
        }

        /* JADX INFO: renamed from: android.ext.MainService$36$3, reason: invalid class name */
        class AnonymousClass3 implements DialogInterface.OnClickListener {
            private final /* synthetic */ String val$oldPkg;
            private final /* synthetic */ boolean val$x64;

            AnonymousClass3(String str, boolean z) {
                this.val$oldPkg = str;
                this.val$x64 = z;
            }

            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                final String str = this.val$oldPkg;
                final boolean z = this.val$x64;
                ThreadManager.runOnMainThread(new Runnable() { // from class: android.ext.MainService.36.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ComponentName name;
                        Log.d("Restart stub: " + str);
                        boolean started = false;
                        final String cmd = "exec setprop wrap." + ((str == null || str.length() <= 26) ? str : str.substring(0, 26));
                        try {
                            String lib = String.valueOf(DaemonLoader.getDaemonDir()) + DaemonLoader.getStubLib(z);
                            RootDetector.runCmd(String.valueOf(cmd) + " LD_PRELOAD=" + lib, 5);
                        } catch (Throwable e) {
                            Log.e("Failed stub app: " + str, e);
                        }
                        try {
                            String monkeyOut = RootDetector.runCmd("exec monkey -p " + str + " 1", 10);
                            if (!monkeyOut.contains("Events injected")) {
                                Intent intent = Tools.getPackageManager().getLaunchIntentForPackage(str);
                                if (intent == null) {
                                    intent = new Intent("android.intent.action.MAIN");
                                    intent.setPackage(str);
                                }
                                boolean ok = false;
                                if (intent != null && (name = intent.getComponent()) != null) {
                                    String amOut = RootDetector.runCmd("exec am start -n " + name.flattenToString(), 10);
                                    ok = amOut.contains("Starting: Intent");
                                }
                                if (!ok) {
                                    intent.setFlags(AddressItem.FLAG_VALUE_NOT_EQUAL);
                                    final Intent starter = intent;
                                    final String str2 = str;
                                    ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.MainService.36.3.1.1
                                        @Override // java.lang.Runnable
                                        public void run() {
                                            try {
                                                Tools.getContext().startActivity(starter);
                                            } catch (Exception e2) {
                                                Log.e("Failed restart app 2: " + str2, e2);
                                            }
                                        }
                                    });
                                }
                            }
                            started = true;
                        } catch (Throwable e2) {
                            Log.e("Failed restart app: " + str, e2);
                        }
                        Handler handlerMainThread = ThreadManager.getHandlerMainThread();
                        final String str3 = str;
                        handlerMainThread.postDelayed(new Runnable() { // from class: android.ext.MainService.36.3.1.2
                            @Override // java.lang.Runnable
                            public void run() {
                                try {
                                    RootDetector.runCmd(String.valueOf(cmd) + " ''", 5);
                                } catch (Throwable e3) {
                                    Log.e("Failed unstub app: " + str3, e3);
                                }
                            }
                        }, started ? 10000 : 0);
                        Handler handlerUiThread = ThreadManager.getHandlerUiThread();
                        final String str4 = str;
                        handlerUiThread.postDelayed(new Runnable() { // from class: android.ext.MainService.36.3.1.3
                            @Override // java.lang.Runnable
                            public void run() {
                                MainService.this.mAppDetector.detectApp(true, str4);
                            }
                        }, started ? DaemonManager.MAX_PATH_SIZE : 0);
                    }
                });
            }
        }
    }

    public void onTargetDead() {
        interruptScript();
        if (!this.dialogDeadShow) {
            this.dialogDeadShow = true;
            String oldPkg = this.processInfo == null ? null : this.processInfo.packageName;
            boolean x64 = this.processInfo == null ? false : this.processInfo.x64;
            detectApp(false);
            reset();
            dismissBusyDialog();
            onTargetDeadDialog(oldPkg, x64);
        }
    }

    public void onDaemonExit() {
        dismissBusyDialog();
        stopService();
    }

    boolean showBusyDialog() {
        this.hotPoint.onProgress((byte) 1, -1L, 1L, 0, 1, "");
        return this.mBusyDialog.show(true);
    }

    void dismissBusyDialog() {
        this.hotPoint.onProgress((byte) 2, -1L, 1L, 0, 1, "");
        this.mBusyDialog.dismiss(true);
        this.mDaemonManager.clearCountFuzzyEqualRun();
        this.mDaemonManager.getMem();
        this.alertPaused = false;
    }

    public void notifyScript(byte seq, String reason) {
        if (seq == 0) {
            Log.d("notifyScript client: " + reason);
            return;
        }
        Script script = this.currentScript;
        if (script != null) {
            Log.d("notifyScript: " + ((int) seq) + ListManager.TEXT_SEPARATOR + reason);
            script.callNotify(seq, reason);
        } else {
            Log.d("notifyScript no script: " + ((int) seq) + ListManager.TEXT_SEPARATOR + reason);
        }
    }

    public void onProgress(CharSequence message, long progress, long progressMax, int stage, int stageMax, long found) {
        StringBuilder progressText = null;
        if (progress > 0 && progressMax != 0) {
            progressText = new StringBuilder();
            progressText.append(Tools.stringFormat("%.2f", Double.valueOf((100.0d * progress) / progressMax)));
            progressText.append(" %");
        }
        this.mBusyDialog.onProgress(message, progress, progressMax, stage, stageMax, found, progressText == null ? "" : progressText.toString());
        if (progressText != null) {
            progressText.setLength(4);
            progressText.append('%');
        }
        this.hotPoint.onProgress((byte) 0, progress, progressMax, stage, stageMax, progressText == null ? "" : progressText.toString());
    }

    public void onProgress(CharSequence message) {
        onProgress(message, -1L, 1L, 0, 1, 0L);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public long getContentInAddress(long address, int flags) {
        lockApp();
        return this.mDaemonManager.getMemoryContent(address, flags);
    }
}
