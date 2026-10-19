package android.ext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.ext.ParserNumbers;
import android.ext.ProcessList;
import android.ext.Script;
import android.fix.LayoutInflater;
import android.fix.SparseArray;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Locale;
import lasm.LasmConstants;
import luaj.LuaError;
import luaj.compiler.Constants;

/* JADX INFO: loaded from: classes.dex */
public class Config {
    public static final int CONFIG_ALLOW_SUGGESTIONS = 8;
    public static final int CONFIG_CHECK_LIBS = 2048;
    public static final int CONFIG_IGNORE_UNKNOWN_CHARS = 4096;
    public static final int CONFIG_KBD_SMALL = 64;
    public static final int CONFIG_PREVENT_UNLOAD_TOAST = 8192;
    public static final int CONFIG_SHOW_TIME_JUMP_PANEL = 128;
    public static final int CONFIG_USE_AUTOPAUSE = 4;
    public static final int CONFIG_USE_HARDWARE_ACCELERATION = 2;
    public static final int CONFIG_USE_INDENT = 16;
    public static final int CONFIG_USE_INTERNAL_KEYBOARD = 1;
    public static final int CONFIG_USE_NOTIFICATION = 256;
    public static final int CONFIG_USE_SOUND_EFFECTS = 32;
    public static final int CONFIG_VISIBLE_TYPE = 1024;
    public static final int CONFIG_VSPACE_ROOT = 512;
    public static final int CONTEXT_ACTIVITY = 4;
    public static final int CONTEXT_APPLICATION = 3;
    public static final int CONTEXT_CREATE = 2;
    public static final int CONTEXT_INSTRUMENTATION = 0;
    public static final int CONTEXT_SERVICE = 1;
    public static final int HELPER_GO_TO = 1;
    public static final int HELPER_GO_TO_AND_RESTORE = 2;
    public static final int HELPER_OFF = 0;
    private static final String IGNORE = "ignore";
    public static final int IGNORE_BAD_APPS = 3;
    private static final long IGNORE_DEFAULT = 0;
    public static final int IGNORE_FORCE_CLOSE = 6;
    public static final int IGNORE_LOCALE = 7;
    public static final int IGNORE_MIUI = 1;
    public static final int IGNORE_NOT_RANDOM_NAME = 4;
    public static final int IGNORE_PHOENIX_OS = 2;
    public static final int IGNORE_SLOW_EMULATOR = 5;
    private static final String MEMORY_FROM = "memory-from";
    private static final long MEMORY_FROM_DEFAULT = 0;
    private static final String MEMORY_TO = "memory-to";
    private static final long MEMORY_TO_DEFAULT = -1;
    private static final String NEARBY_DISTANCE = "nearby-distance";
    private static final long NEARBY_DISTANCE_DEFAULT = 512;
    private static final int RAND_COUNT = 19;
    public static final int SPEEDS_NOTHING = 0;
    public static final int SPEEDS_SORT = 1;
    public static final int SPEEDS_SORT_AND_REMOVE = 2;
    public static final int STATE_ANONYMOUS = 32;
    public static final int STATE_ASHMEM = 524288;
    public static final int STATE_BAD = 131072;
    public static final int STATE_CODE_APP = 16384;
    public static final int STATE_CODE_SYS = 32768;
    private static final int STATE_COUNT = 15;
    public static final int STATE_C_ALLOC = 4;
    public static final int STATE_C_BSS = 16;
    public static final int STATE_C_DATA = 8;
    public static final int STATE_C_HEAP = 1;
    public static final int STATE_JAVA = 65536;
    public static final int STATE_JAVA_HEAP = 2;
    public static final int STATE_OTHER = -2080896;
    public static final int STATE_PPSSPP = 262144;
    public static final int STATE_STACK = 64;
    public static final int STATE_VIDEO = 1048576;
    private static final int TIMER_COUNT = 21;
    private static final String TOOLBAR_BUTTONS = "toolbar-buttons-";
    private static final long TOOLBAR_BUTTONS_DEFAULT = -1;
    public static volatile int backgrounds;
    public static volatile int configClient;
    public static volatile int configDaemon;
    public static volatile int contextSource;
    public static volatile int copyParams;
    public static volatile int dataInRam;
    public static volatile int fillToolbar;
    public static volatile int floatFlags;
    public static volatile int floatType;
    public static volatile int freezeInterval;
    public static volatile int historyLimit;
    public static volatile int hotKey;
    public static volatile int iconsSize;
    public static volatile long ignore;
    public static volatile long interceptTimers;
    public static volatile long memoryFrom;
    public static volatile long memoryTo;
    public static volatile long nearbyDistance;
    public static volatile int savedListUpdatesInterval;
    public static volatile int searchHelper;
    public static volatile int smallItems;
    public static volatile int speedsParams;
    public static volatile int timeJumpLast;
    public static volatile int toolbars;
    public static volatile int usedRanges;
    public static volatile boolean vSpace;
    public static volatile byte vSpace64;
    public static volatile boolean vSpaceGuest;
    public static volatile String vSpaceName;
    public static volatile String vSpacePkg;
    public static volatile boolean vSpaceReal;
    public static volatile int xorKey;
    public static final long[] toolbarButtons = new long[MainService.menuItems.length * 2];
    static volatile int[] used = {114, 115, 116};
    static SparseArray<Option> list = new SparseArray<>();

    public static void useKeys(int[] ids) {
        int j;
        if (ids != null && ids.length != 0) {
            int[] prev = used;
            int pl = prev.length;
            int il = ids.length;
            int nl = pl + il;
            int[] next = new int[nl];
            System.arraycopy(prev, 0, next, 0, pl);
            System.arraycopy(ids, 0, next, pl, il);
            Arrays.sort(next);
            int i = 1;
            int j2 = 1;
            while (i < nl) {
                if (next[i] != next[i - 1]) {
                    j = j2 + 1;
                    next[j2] = next[i];
                } else {
                    j = j2;
                }
                i++;
                j2 = j;
            }
            used = Arrays.copyOf(next, j2);
        }
    }

    static {
        new OptionHidden(R.id.config_float_type, R.string.empty, -1, "float-type", false);
        new OptionHidden(R.id.config_float_flags, R.string.empty, -1, "float-flags", false);
        new OptionHidden(R.id.config_kbd_small, R.string.empty, 0, "kbd-small", false);
        new OptionHidden(R.id.config_time_jump_last, R.string.empty, 0, "time-jump-last", true);
        new OptionHidden(R.id.config_copy_params, R.string.empty, 0, "copy-params", false);
        new OptionHidden(R.id.record_logcat, R.string.empty, 0, "record-logcat", false);
        new OptionEdit(R.id.config_freeze_interval, R.string.freeze_interval, R.string.microseconds_short, 0, 600000000L, 33000, "freeze-interval", true, R.string.freeze_interval_help_);
        new OptionEdit(R.id.config_saved_list_updates_interval, R.string.saved_list_update_interval, R.string.milliseconds_short, 100, 600000L, DaemonManager.TIME_JUMP_MUL_INT, "saved-list-updates-interval", false, R.string.saved_list_update_interval_help_);
        new OptionEdit(R.id.config_xor_key, R.string.xor_key, R.string.empty, AddressItem.FLAG_FUZZY, ParserNumbers.MAX_UINT, 0, "xor-key", true, true, false, 1);
        new OptionEdit(R.id.config_history_limit, R.string.history_limit, R.string.empty, 0, 100000L, 500, "history-limit", false);
        new OptionSeek(R.id.config_icons_size, R.string.icons_size, 12, 0, "icons-size", false, R.string.icons_size_help) { // from class: android.ext.Config.1
            private WeakReference<ImageView> weakIcon = new WeakReference<>(null);

            @Override // android.ext.Config.OptionSeek
            protected void show() {
                super.show();
                SeekBar seek = this.weakSeek.get();
                if (seek != null) {
                    ImageView icon = new ImageView(seek.getContext());
                    icon.setImageResource(R.drawable.ic_tooltip_edit_white_24dp);
                    this.weakIcon = new WeakReference<>(icon);
                    Config.setIconSize(icon, Config.getIconSize(seek.getProgress()));
                    ViewParent p = seek.getParent();
                    if (p instanceof ViewGroup) {
                        ViewGroup parent = (ViewGroup) p;
                        parent.addView(icon);
                    }
                }
            }

            @Override // android.ext.Config.OptionSeek, android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ImageView icon = this.weakIcon.get();
                if (icon != null) {
                    Config.setIconSize(icon, Config.getIconSize(progress));
                }
            }
        };
        new OptionSingleChoice(R.id.config_ignore_unknown_chars, R.string.ignore_unknown_chars, new int[]{R.string.no, R.string.yes}, 1, "ignore-unknown-chars", false, R.string.ignore_unknown_chars_help);
        new OptionSingleChoice(R.id.config_use_notification, R.string.use_notification, new int[]{R.string.no, R.string.yes}, 0, "use-notification", false, R.string.use_notification_help);
        new OptionSingleChoice(R.id.config_context_source, R.string.context, new int[]{R.string.instrumentation, R.string.service, R.string.create, R.string.application, R.string.activity}, Build.VERSION.SDK_INT >= 29 ? 1 : 0, "context-source", false);
        new OptionSingleChoice(R.id.config_use_sound_effects, R.string.use_sound_effects, new int[]{R.string.no, R.string.yes}, 1, "use-sound-effects", false, R.string.use_sound_effects_help);
        new OptionSingleChoice(R.id.selinux, R.string.empty, new int[]{R.string.work_without_selinux, R.string.work_with_selinux}, 0, "selinux", false);
        new OptionSingleChoice(R.id.config_keyboard, R.string.keyboard, new int[]{R.string.external, R.string.internal}, InternalKeyboard.allowUsage() ? 1 : 0, "use-internal-keyboard", false, R.string.keyboard_help);
        new OptionSingleChoice(R.id.config_suggestions, R.string.allow_suggestions, new int[]{R.string.no, R.string.yes}, 0, "allow-suggestions", false, R.string.allow_suggestions_help);
        new OptionSingleChoice(R.id.config_indent, R.string.indent_for_the_status_bar, new int[]{R.string.no, R.string.yes}, 0, "use-indent", false);
        new OptionSingleChoice(R.id.config_autopause, R.string.autopause, new int[]{R.string.no, R.string.yes}, 0, "use-autopause", false, R.string.autopause_help);
        new OptionSingleChoice(R.id.config_acceleration, R.string.acceleration, new int[]{R.string.software, R.string.hardware_}, 0, "use-hardware-acceleration", false, R.string.acceleration_help_);
        new OptionSingleChoice(R.id.config_skip_memory, R.string.skip_memory, new int[]{R.string.no_slow, R.string.empty_memory, R.string.empty_zygote_memory}, 2, "skip-memory", true);
        new OptionSingleChoice(R.id.config_memory_access, R.string.memory_access, new int[]{R.string.normal, R.string.extended, R.string.experimental}, 0, "memory-access", false) { // from class: android.ext.Config.2
            @Override // android.ext.Config.OptionSingleChoice, android.ext.Config.Option
            public void change() {
                if (Main.isRootMode()) {
                    super.change();
                }
            }
        };
        new OptionSingleChoice(R.id.config_deep_read, R.string.deep_read, new int[]{R.string.no, R.string.yes_slow}, 0, "method", false, R.string.help_deep_read);
        new OptionSingleChoice(R.id.config_calls, R.string.calls, new int[]{R.string.normal, R.string.alternative}, 0, "calls", false, R.string.help_calls);
        new OptionSingleChoice(R.id.config_waitpid, R.string.waitpid_mode, new int[]{R.string.normal, R.string.alternative}, 0, "waitpid", false, R.string.help_waitpid);
        new OptionSingleChoice(R.id.config_fast_freeze, R.string.fast_freeze, new int[]{R.string.no, R.string.yes}, 0, "fast-freeze", true, R.string.help_fast_freeze);
        new OptionSingleChoice(R.id.config_fill_toolbar, R.string.fill_toolbar, new int[]{R.string.from_left, R.string.from_center, R.string.from_right}, 2, "fill-toolbar", false);
        new OptionSingleChoice(R.id.config_search_helper, R.string.search_helper, new int[]{R.string.off, R.string.go_to_game, R.string.go_to_game_and_restore}, 2, "search-helper", false);
        new OptionSingleChoice(R.id.config_speeds_params, R.string.speedhack_speeds_, new int[]{R.string.nothing, R.string.sort, R.string.sort_and_remove_duplicates}, 2, "speeds-params", false);
        new OptionSingleChoice(R.id.config_reset_on_exit, R.string.reset_on_exit_, new int[]{R.string.no, R.string.yes}, 1, "reset-on-exit", false);
        new OptionSingleChoice(R.id.config_check_libs, R.string.check_libs, new int[]{R.string.no, R.string.yes}, 1, "check-libs", false);
        new OptionSingleChoice(R.id.config_visible_type, R.string.use_visible_type, new int[]{R.string.no, R.string.yes}, 1, "visible-type", false, R.string.use_visible_type_help);
        new OptionSingleChoice(R.id.config_prevent_unload, R.string.prevent_unload, new int[]{R.string.no, R.string.level_1, R.string.level_2, R.string.level_3}, 0, "prevent-unload", false, R.string.prevent_unload_help) { // from class: android.ext.Config.3
            @Override // android.ext.Config.OptionSingleChoice, android.ext.Config.Option
            public boolean onClick_(DialogInterface dialog, int which) {
                boolean ret = super.onClick_(dialog, which);
                if (ret && this.value == 3) {
                    Alert.show(Alert.create(Tools.getContext()).setMessage(Re.s(R.string.help_faq_33_a)).setNegativeButton(R.string.ok, (DialogInterface.OnClickListener) null));
                }
                return ret;
            }
        };
        new OptionSingleChoice(R.id.config_ptrace_bypass_mode, R.string.ptrace_bypass_mode, new int[]{R.string.mode_restore, R.string.mode_nothing, R.string.mode_freeze, R.string.off}, 3, "ptrace-bypass", true, R.string.ptrace_bypass_mode_help);
        new OptionSingleChoice(R.id.config_time_jump_panel, R.string.show_time_jump_panel, new int[]{R.string.no, R.string.yes}, 0, "time-jump-panel", true) { // from class: android.ext.Config.4
            @Override // android.ext.Config.OptionSingleChoice, android.ext.Config.Option
            public boolean onClick_(DialogInterface dialog, int which) {
                boolean ret = super.onClick_(dialog, which);
                if (ret) {
                    MainService.instance.hotPoint.updatePanels();
                }
                return ret;
            }
        };
        new OptionSingleChoice(R.id.config_number_locale, R.string.number_locale, null, 0, "number-locale", false) { // from class: android.ext.Config.5
            @Override // android.ext.Config.Option
            protected void init() {
                if (this.value > 0) {
                    String[] list2 = list();
                    if (this.value < list2.length) {
                        AppLocale.setNumberLocale(AppLocale.getLocaleObject(list2[this.value]));
                    }
                }
            }

            private String[] list() {
                return new String[]{"", "en_US", "in_ID", "ru_RU"};
            }

            @Override // android.ext.Config.OptionSingleChoice
            protected void loadMap() {
                super.loadMap();
                if (this.stringValues == null) {
                    String[] list2 = list();
                    CharSequence[] stringValues = new CharSequence[list2.length];
                    for (int i = 0; i < list2.length; i++) {
                        stringValues[i] = example(list2[i]);
                    }
                    this.stringValues = stringValues;
                }
            }

            private String example(String locale) {
                Locale loc;
                try {
                    if (locale.length() == 0) {
                        loc = AppLocale.getLocaleObject();
                        locale = Tools.stripColon(R.string.default_);
                    } else {
                        loc = AppLocale.getLocaleObject(locale);
                    }
                    String num = "";
                    try {
                        num = String.format(loc, "\n[%,.1f | %.2e]", Double.valueOf(-1234.0d), Double.valueOf(-4.56E-78d));
                    } catch (Throwable e) {
                        Log.badImplementation(e);
                    }
                    return String.valueOf(locale) + num;
                } catch (Throwable e2) {
                    String locale2 = locale;
                    Log.badImplementation(e2);
                    return locale2;
                }
            }

            @Override // android.ext.Config.OptionSingleChoice, android.ext.Config.Option
            public boolean onClick_(DialogInterface dialog, int which) {
                boolean ret = super.onClick_(dialog, which);
                if (ret) {
                    ConfigListAdapter.needRestart();
                }
                return ret;
            }
        };
        new OptionSingleChoice(R.id.config_vspace_root, R.string.vspace_root, new int[]{R.string.no, R.string.yes_dangerous}, 0, "vspace-root", false, R.string.help_vspace_root_) { // from class: android.ext.Config.6
            private int old = 0;

            @Override // android.ext.Config.OptionSingleChoice, android.ext.Config.Option
            public boolean onClick_(DialogInterface dialog, int which) {
                boolean ret = false;
                if (which < 0) {
                    if (which == -2) {
                        this.value = 0;
                        Config.save();
                    }
                    if (Config.vSpaceReal && this.old != this.value) {
                        ConfigListAdapter.needRestart();
                    }
                } else {
                    this.old = this.value;
                    ret = super.onClick_(dialog, which);
                    if (ret) {
                        if (which == 1) {
                            final AlertDialog d = Alert.create(Tools.getContext()).setCustomTitle(Tools.getCustomTitle(this.nameRes)).setMessage(Re.s(this.helpRes)).setPositiveButton(R.string.save, this).setNegativeButton(R.string.cancel, this).create();
                            Alert.show(d);
                            Alert.setOnShowListener(d, new DialogInterface.OnShowListener() { // from class: android.ext.Config.6.1
                                @Override // android.content.DialogInterface.OnShowListener
                                public void onShow(DialogInterface dialog2) {
                                    Tools.setClickableText(d);
                                }
                            });
                        } else if (Config.vSpaceReal && this.old != this.value) {
                            ConfigListAdapter.needRestart();
                        }
                    }
                }
                return ret;
            }
        };
        new OptionSingleChoice(R.id.config_ram, R.string.data_in_ram, null, Integer.MAX_VALUE, "data-in-ram", false) { // from class: android.ext.Config.7
            private static final float DATA_MUL = 5.0f;
            private static final float KB_IN_MB = 1024.0f;
            private static final int MIN_COUNTS = 8;

            @Override // android.ext.Config.OptionSingleChoice
            protected void loadMap() {
                super.loadMap();
                if (this.map == null) {
                    int ramKb = Tools.getRamSizeKb();
                    int counts = ramKb > 0 ? ((int) Math.floor(Math.log(ramKb / DATA_MUL) / Math.log(2.0d))) - 8 : 0;
                    if (counts <= 0) {
                        counts = 7;
                    }
                    if (counts >= 20) {
                        counts = 20;
                    }
                    int[] map = new int[counts + 2];
                    CharSequence[] stringValues = new CharSequence[counts + 2];
                    map[0] = 0;
                    stringValues[0] = Re.s(R.string.no);
                    map[map.length - 1] = Integer.MAX_VALUE;
                    stringValues[map.length - 1] = Re.s(R.string.yes);
                    String first_mb = Re.s(R.string.first_mb);
                    for (int i = 1; i <= counts; i++) {
                        int item = 1 << (i + 8);
                        map[i] = item;
                        int value = Math.round((item * DATA_MUL) / KB_IN_MB);
                        stringValues[i] = Tools.stringFormat(first_mb, Integer.valueOf(value));
                    }
                    this.map = map;
                    this.stringValues = stringValues;
                    this.values = null;
                }
            }

            private String getStringValue(int value) {
                if (value == Integer.MAX_VALUE) {
                    return Re.s(R.string.yes);
                }
                if (value == 0) {
                    return Re.s(R.string.no);
                }
                int val = Math.round((DATA_MUL * value) / KB_IN_MB);
                return Tools.stringFormat(Re.s(R.string.first_mb), Integer.valueOf(val));
            }

            @Override // android.ext.Config.OptionSingleChoice, android.ext.Config.Option
            public String toString() {
                return String.valueOf(Tools.stripColon(this.nameRes)) + ": " + getStringValue(this.value);
            }
        };
        new OptionSingleChoice(R.id.config_hot_key, R.string.hot_key, null, 0, "hot-key", false) { // from class: android.ext.Config.8
            @Override // android.ext.Config.OptionSingleChoice, android.ext.Config.Option
            public void change() {
                if (Main.isRootMode()) {
                    super.change();
                }
            }

            @Override // android.ext.Config.OptionSingleChoice
            protected void loadMap() {
                super.loadMap();
                int count = Config.used.length;
                if (this.map == null || this.map.length != count + 1) {
                    int[] map = new int[count + 1];
                    CharSequence[] stringValues = new CharSequence[count + 1];
                    map[0] = 0;
                    stringValues[0] = Re.s(R.string.no);
                    for (int i = 0; i < count; i++) {
                        int key = Config.used[i];
                        map[i + 1] = key;
                        stringValues[i + 1] = Config.getKeyName(key);
                    }
                    this.map = map;
                    this.stringValues = stringValues;
                    this.values = null;
                }
            }

            @Override // android.ext.Config.OptionSingleChoice, android.ext.Config.Option
            public String toString() {
                return String.valueOf(Tools.stripColon(this.nameRes)) + ": " + Config.getKeyName(this.value);
            }
        };
        new OptionMultiChoice(R.id.config_ranges, R.string.set_range, null, 0, "ranges", true) { // from class: android.ext.Config.9
            private int[] colors;
            private CharSequence[] shortValues;
            private CharSequence[] strValues;

            @Override // android.ext.Config.Option
            protected void init() {
                this.defaultValue = 262207;
            }

            @Override // android.ext.Config.OptionSingleChoice
            protected void loadMap() {
                super.loadMap();
                if (this.map == null) {
                    int[] map = new int[15];
                    int[] colors = new int[15];
                    CharSequence[] stringValues = new CharSequence[map.length];
                    map[0] = 2;
                    colors[0] = Tools.getColor(R.color.region_good);
                    stringValues[0] = Tools.colorize("Jh: Java heap", colors[0]);
                    int i = 0 + 1;
                    map[i] = 1;
                    colors[i] = Tools.getColor(R.color.region_good);
                    stringValues[i] = Tools.colorize("Ch: C++ heap", colors[i]);
                    int i2 = i + 1;
                    map[i2] = 4;
                    colors[i2] = Tools.getColor(R.color.region_good);
                    stringValues[i2] = Tools.colorize("Ca: C++ alloc", colors[i2]);
                    int i3 = i2 + 1;
                    map[i3] = 8;
                    colors[i3] = Tools.getColor(R.color.region_good);
                    stringValues[i3] = Tools.colorize("Cd: C++ .data", colors[i3]);
                    int i4 = i3 + 1;
                    map[i4] = 16;
                    colors[i4] = Tools.getColor(R.color.region_good);
                    stringValues[i4] = Tools.colorize("Cb: C++ .bss", colors[i4]);
                    int i5 = i4 + 1;
                    map[i5] = 262144;
                    colors[i5] = Tools.getColor(R.color.region_good);
                    stringValues[i5] = Tools.colorize("PS: PPSSPP", colors[i5]);
                    int i6 = i5 + 1;
                    map[i6] = 32;
                    colors[i6] = Tools.getColor(R.color.region_good);
                    stringValues[i6] = Tools.colorize("A: Anonymous", colors[i6]);
                    int i7 = i6 + 1;
                    map[i7] = 65536;
                    colors[i7] = Tools.getColor(R.color.region_other);
                    stringValues[i7] = Tools.colorize("J: Java", colors[i7]);
                    int i8 = i7 + 1;
                    map[i8] = 64;
                    colors[i8] = Tools.getColor(R.color.region_other);
                    stringValues[i8] = Tools.colorize("S: Stack", colors[i8]);
                    int i9 = i8 + 1;
                    map[i9] = 524288;
                    colors[i9] = Tools.getColor(R.color.region_other);
                    stringValues[i9] = Tools.colorize("As: Ashmem", colors[i9]);
                    int i10 = i9 + 1;
                    map[i10] = 1048576;
                    colors[i10] = Tools.getColor(R.color.region_other);
                    stringValues[i10] = Tools.colorize("V: Video", colors[i10]);
                    int i11 = i10 + 1;
                    map[i11] = -2080896;
                    colors[i11] = Tools.getColor(R.color.region_slow);
                    stringValues[i11] = Tools.colorize(Re.s("O: Other (__slow__)"), colors[i11]);
                    int i12 = i11 + 1;
                    map[i12] = 131072;
                    colors[i12] = Tools.getColor(R.color.region_bad);
                    stringValues[i12] = Tools.colorize(Re.s("B: Bad (__dangerous__)"), colors[i12]);
                    int i13 = i12 + 1;
                    map[i13] = 16384;
                    colors[i13] = Tools.getColor(R.color.region_code);
                    stringValues[i13] = Tools.colorize(Re.s("Xa: Code app (__dangerous__)"), colors[i13]);
                    int i14 = i13 + 1;
                    map[i14] = 32768;
                    colors[i14] = Tools.getColor(R.color.region_system_code);
                    stringValues[i14] = Tools.colorize(Re.s("Xs: Code system (__dangerous__)"), colors[i14]);
                    int i15 = i14 + 1;
                    CharSequence[] shortValues = new CharSequence[map.length];
                    for (int i16 = 0; i16 < map.length; i16++) {
                        String[] parts = stringValues[i16].toString().split(":");
                        shortValues[i16] = Tools.colorize(parts[0], colors[i16]);
                    }
                    this.colors = colors;
                    this.map = map;
                    this.strValues = stringValues;
                    this.shortValues = shortValues;
                    this.values = null;
                    RegionList.setMaps(map, shortValues, colors);
                }
                CharSequence[] stringValues2 = new CharSequence[this.map.length];
                CharSequence[] strValues = this.strValues;
                Context context = Tools.getContext();
                for (int i17 = 0; i17 < strValues.length; i17++) {
                    String str = strValues[i17].toString();
                    try {
                        str = String.valueOf(str) + " [" + Tools.formatFileSize(context, RegionList.getSize(i17)) + ']';
                    } catch (Throwable e) {
                        Log.badImplementation(e);
                        ExceptionHandler.sendException(Thread.currentThread(), e, false);
                    }
                    stringValues2[i17] = Tools.colorize(str, this.colors[i17]);
                }
                this.stringValues = stringValues2;
            }

            @Override // android.ext.Config.Option
            public CharSequence asString() {
                if (this.map == null) {
                    loadMap();
                }
                CharSequence[] ranges = new CharSequence[this.map.length * 2];
                int used2 = 0;
                for (int i = 0; i < this.map.length; i++) {
                    if ((this.map[i] & this.value) == this.map[i]) {
                        if (used2 > 0) {
                            ranges[used2] = ",";
                            used2++;
                        }
                        ranges[used2] = this.shortValues[i];
                        used2++;
                    }
                }
                return Tools.concat((CharSequence[]) Arrays.copyOf(ranges, used2));
            }

            @Override // android.ext.Config.OptionMultiChoice, android.ext.Config.OptionSingleChoice, android.ext.Config.Option
            public boolean onClick_(DialogInterface dialog, int which) {
                Record record;
                boolean ret = super.onClick_(dialog, which);
                if (ret && (record = MainService.instance.currentRecord) != null) {
                    record.write("gg.setRanges(");
                    Script.Consts.logConst(record, record.consts.REGION_, this.value);
                    record.write(")\n");
                }
                return ret;
            }
        };
        new OptionMultiChoiceSh(R.id.config_speedhack_intercept, R.string.speedhack_intercept, null, -1, "intercept", true, 21);
        new OptionMultiChoiceSh(R.id.config_unrandomizer_intercept, R.string.unrandomizer_intercept, null, -520193, "unrand-intercept", true, 19);
        new OptionMultiChoice(R.id.config_toolbars, R.string.toolbars, null, -1, "toolbars", false) { // from class: android.ext.Config.10
            @Override // android.ext.Config.OptionSingleChoice
            protected void loadMap() {
                super.loadMap();
                loadTabMap(true);
            }
        };
        new OptionMultiChoice(R.id.config_use_small_list_items, R.string.small_list_items, null, 0, "small-items", false, R.string.small_list_items_help) { // from class: android.ext.Config.11
            @Override // android.ext.Config.OptionSingleChoice
            protected void loadMap() {
                super.loadMap();
                if (this.changedListener == null) {
                    this.changedListener = new Option.OnChangedListener() { // from class: android.ext.Config.11.1
                        @Override // android.ext.Config.Option.OnChangedListener
                        public void onChanged(int value) {
                            MainService.instance.onMemoryChanged();
                        }
                    };
                }
                loadTabMap(true);
            }
        };
        new OptionMultiChoice(R.id.config_backgrounds, R.string.backgrounds, null, 0, "backgrounds", false) { // from class: android.ext.Config.12
            @Override // android.ext.Config.OptionSingleChoice
            protected void loadMap() {
                super.loadMap();
                loadTabMap(false);
            }
        };
        new OptionMultiChoice(R.id.config_hide_from_game, R.string.hide_from_game, null, 0, "hide", true) { // from class: android.ext.Config.13
            @Override // android.ext.Config.OptionSingleChoice
            protected void loadMap() {
                super.loadMap();
                if (this.map == null) {
                    int[] map = new int[4];
                    CharSequence[] stringValues = new CharSequence[map.length];
                    String slow = " (" + Re.s(R.string.slow) + ')';
                    map[0] = 1;
                    stringValues[0] = String.valueOf(1) + slow;
                    int i = 0 + 1;
                    map[i] = 2;
                    stringValues[i] = String.valueOf(2) + slow;
                    int i2 = i + 1;
                    map[i2] = 4;
                    stringValues[i2] = String.valueOf(3) + slow;
                    int i3 = i2 + 1;
                    map[i3] = 8;
                    stringValues[i3] = String.valueOf(4) + slow;
                    int i4 = i3 + 1;
                    this.map = map;
                    this.stringValues = stringValues;
                    this.values = null;
                    if (this.changedListener == null) {
                        this.changedListener = new Option.OnChangedListener() { // from class: android.ext.Config.13.1
                            @Override // android.ext.Config.Option.OnChangedListener
                            public void onChanged(int value) {
                                StringBuilder msg = null;
                                if ((value & 1) != 0) {
                                    msg = new StringBuilder();
                                    msg.append(Re.s(R.string.hide_1_warn));
                                }
                                if ((value & 8) != 0) {
                                    if (msg == null) {
                                        msg = new StringBuilder();
                                    } else {
                                        msg.append("\n\n");
                                    }
                                    msg.append(Re.s(R.string.hide_4_warn));
                                }
                                if (msg != null) {
                                    Alert.show(Alert.create().setMessage(msg.toString()).setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) null));
                                }
                            }
                        };
                    }
                }
            }
        };
        load();
        vSpace = false;
        vSpaceReal = false;
        vSpaceGuest = false;
        vSpacePkg = "unknown";
        vSpaceName = "unknown";
        vSpace64 = (byte) 0;
    }

    private static void load(SharedPreferences storage) {
        ignore = storage.getLong(IGNORE, 0L);
        memoryFrom = storage.getLong(MEMORY_FROM, 0L);
        memoryTo = storage.getLong(MEMORY_TO, -1L);
        nearbyDistance = storage.getLong(NEARBY_DISTANCE, NEARBY_DISTANCE_DEFAULT);
        for (int i = 0; i < toolbarButtons.length; i++) {
            toolbarButtons[i] = storage.getLong(TOOLBAR_BUTTONS + i, -1L);
        }
        if (MainService.created) {
            MainService.instance.updateToolbars();
        }
    }

    private static void save(SPEditor editor) {
        editor.putLong(IGNORE, ignore, 0L);
        editor.putLong(MEMORY_FROM, memoryFrom, 0L);
        editor.putLong(MEMORY_TO, memoryTo, -1L);
        editor.putLong(NEARBY_DISTANCE, nearbyDistance, NEARBY_DISTANCE_DEFAULT);
        for (int i = 0; i < toolbarButtons.length; i++) {
            editor.putLong(TOOLBAR_BUTTONS + i, toolbarButtons[i], -1L);
        }
    }

    public static void updateConst() {
        SparseArray<Option> list2 = list;
        int flags = list2.get(R.id.config_reset_on_exit).value == 1 ? 0 | 2 : 0;
        if (list2.get(R.id.selinux).value == 1) {
            flags |= 4;
        }
        if (list2.get(R.id.config_deep_read).value == 0) {
            flags |= 65536;
        }
        if (list2.get(R.id.config_calls).value == 1) {
            flags |= 2048;
        }
        if (list2.get(R.id.config_waitpid).value == 1) {
            flags |= 4096;
        }
        if (list2.get(R.id.config_fast_freeze).value == 1) {
            flags |= 262144;
        }
        int value = list2.get(R.id.config_skip_memory).value;
        if (value == 1) {
            flags |= 1;
        }
        if (value == 2) {
            flags |= 1025;
        }
        int value2 = list2.get(R.id.config_memory_access).value;
        if (value2 == 1) {
            flags |= 8;
        }
        if (value2 == 2) {
            flags |= 16;
        }
        int value3 = list2.get(R.id.config_prevent_unload).value;
        if (value3 == 1) {
            flags |= 32;
        }
        if (value3 == 2 || value3 == 3) {
            flags |= 64;
        }
        int value4 = list2.get(R.id.config_hide_from_game).value;
        if ((value4 & 1) != 0) {
            flags |= 128;
        }
        if ((value4 & 2) != 0) {
            flags |= 256;
        }
        if ((value4 & 4) != 0) {
            flags |= 512;
        }
        if ((value4 & 8) != 0) {
            flags |= 32768;
        }
        int value5 = list2.get(R.id.config_ptrace_bypass_mode).value;
        if (value5 == 1) {
            flags |= 8192;
        }
        if (value5 == 2) {
            flags |= 16384;
        }
        if (value5 == 3) {
            flags |= 131072;
        }
        configDaemon = flags;
        int flags2 = list2.get(R.id.config_keyboard).value == 1 ? 0 | 1 : 0;
        if (list2.get(R.id.config_acceleration).value == 1) {
            flags2 |= 2;
        }
        if (list2.get(R.id.config_autopause).value == 1) {
            flags2 |= 4;
        }
        if (list2.get(R.id.config_suggestions).value == 1) {
            flags2 |= 8;
        }
        if (list2.get(R.id.config_indent).value == 1) {
            flags2 |= 16;
        }
        if (list2.get(R.id.config_use_sound_effects).value == 1) {
            flags2 |= 32;
        }
        if (list2.get(R.id.config_kbd_small).value == 1) {
            flags2 |= 64;
        }
        if (list2.get(R.id.config_time_jump_panel).value == 1) {
            flags2 |= 128;
        }
        if (list2.get(R.id.config_use_notification).value == 1) {
            flags2 |= 256;
        }
        if (list2.get(R.id.config_check_libs).value == 1) {
            flags2 |= 2048;
        }
        if (list2.get(R.id.config_ignore_unknown_chars).value == 1) {
            flags2 |= 4096;
        }
        if (list2.get(R.id.config_prevent_unload).value == 3) {
            flags2 |= 8192;
        }
        if (list2.get(R.id.config_vspace_root).value == 1) {
            flags2 |= 512;
        }
        if (list2.get(R.id.config_visible_type).value == 1) {
            flags2 |= 1024;
        }
        configClient = flags2;
        list2.get(R.id.config_number_locale).init();
        fillToolbar = list2.get(R.id.config_fill_toolbar).value;
        dataInRam = list2.get(R.id.config_ram).value;
        usedRanges = list2.get(R.id.config_ranges).value;
        searchHelper = list2.get(R.id.config_search_helper).value;
        speedsParams = list2.get(R.id.config_speeds_params).value;
        freezeInterval = list2.get(R.id.config_freeze_interval).value;
        savedListUpdatesInterval = list2.get(R.id.config_saved_list_updates_interval).value;
        iconsSize = list2.get(R.id.config_icons_size).value;
        toolbars = list2.get(R.id.config_toolbars).value;
        smallItems = list2.get(R.id.config_use_small_list_items).value;
        backgrounds = list2.get(R.id.config_backgrounds).value;
        xorKey = list2.get(R.id.config_xor_key).value;
        floatType = list2.get(R.id.config_float_type).value;
        floatFlags = list2.get(R.id.config_float_flags).value;
        timeJumpLast = list2.get(R.id.config_time_jump_last).value;
        hotKey = list2.get(R.id.config_hot_key).value;
        historyLimit = list2.get(R.id.config_history_limit).value;
        copyParams = list2.get(R.id.config_copy_params).value;
        contextSource = list2.get(R.id.config_context_source).value;
        interceptTimers = (((long) list2.get(R.id.config_speedhack_intercept).value) & 2097151) | (((long) list2.get(R.id.config_unrandomizer_intercept).value) << 21);
    }

    public static class ContextFix extends ContextWrapper {
        public ContextFix(Context base) {
            super(base);
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public String getPackageName() {
            String pkg = Config.vSpacePkg;
            if (pkg == null || pkg.length() == 0 || "unknown".equals(pkg)) {
                return getBaseContext().getPackageName();
            }
            Log.d("Use 1 " + pkg + " instead of " + getBaseContext().getPackageName());
            return pkg;
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public String getOpPackageName() {
            String pkg = Config.vSpacePkg;
            if (pkg == null || pkg.length() == 0 || "unknown".equals(pkg)) {
                return getBaseContext().getOpPackageName();
            }
            Log.d("Use 2 " + pkg + " instead of " + getBaseContext().getPackageName());
            return pkg;
        }
    }

    public static Context fixContext(Context base) {
        return base instanceof ContextFix ? base : new ContextFix(base);
    }

    static View getCustomTitle(int nameRes, int helpRes, boolean appDependent) {
        String subTitle = null;
        if (appDependent) {
            MainService service = MainService.instance;
            ProcessList.ProcessInfo pi = service.processInfo;
            String name = pi != null ? pi.name : null;
            if (name != null) {
                subTitle = String.valueOf(SearchMenuItem.HEX_UTF_16LE) + name + SearchMenuItem.HEX_UTF_16LE;
            }
        }
        return Tools.getCustomTitle(Re.s(nameRes), subTitle, helpRes);
    }

    public static abstract class Option implements DialogInterface.OnClickListener {
        boolean appDependent;
        protected OnChangedListener changedListener;
        int defaultValue;
        int nameRes;
        String storageKey;
        int value;
        int helpRes = -1;
        protected OnChangeListener changeListener = null;

        interface OnChangeListener {
            int onChange(DialogInterface dialogInterface, int i);
        }

        interface OnChangedListener {
            void onChanged(int i);
        }

        public abstract void change();

        public abstract boolean onClick_(DialogInterface dialogInterface, int i);

        public Option(int id, int nameRes, int defaultValue, String storageKey, boolean appDependent) {
            this.nameRes = nameRes;
            this.defaultValue = defaultValue;
            this.storageKey = storageKey;
            this.value = defaultValue;
            this.appDependent = appDependent;
            init();
            Config.list.put(id, this);
        }

        protected void init() {
        }

        public String toString() {
            return String.valueOf(Re.s(this.nameRes)) + ' ' + this.value;
        }

        public CharSequence asString() {
            return toString();
        }

        @Override // android.content.DialogInterface.OnClickListener
        public final void onClick(DialogInterface dialog, int which) {
            OnChangedListener listener2;
            OnChangeListener listener = this.changeListener;
            if (listener != null) {
                which = listener.onChange(dialog, which);
            }
            if (onClick_(dialog, which) && (listener2 = this.changedListener) != null) {
                listener2.onChanged(this.value);
            }
        }

        public void setOnChangeListner(OnChangeListener listener) {
            this.changeListener = listener;
        }

        public void setOnChangedListner(OnChangedListener listener) {
            this.changedListener = listener;
        }
    }

    public static class OptionSingleChoice extends Option {
        int[] map;
        CharSequence[] stringValues;
        int[] values;

        public OptionSingleChoice(int id, int nameRes, int[] values, int defaultValue, String storageKey, boolean appDependent) {
            super(id, nameRes, defaultValue, storageKey, appDependent);
            this.stringValues = null;
            this.map = null;
            this.values = values;
        }

        public OptionSingleChoice(int id, int nameRes, int[] values, int defaultValue, String storageKey, boolean appDependent, int helpRes) {
            this(id, nameRes, values, defaultValue, storageKey, appDependent);
            this.helpRes = helpRes;
        }

        protected void loadMap() {
        }

        @Override // android.ext.Config.Option
        public void change() {
            loadMap();
            CharSequence[] items = this.stringValues;
            if (this.values != null) {
                items = new CharSequence[this.values.length];
                for (int i = 0; i < this.values.length; i++) {
                    items[i] = Re.s(this.values[i]);
                }
            }
            show(items);
        }

        protected void show(CharSequence[] items) {
            int def = valueToId(this.defaultValue);
            CharSequence[] items_ = new CharSequence[items.length];
            int i = 0;
            while (i < items.length) {
                items_[i] = (i != def || items[i] == null) ? items[i] : ((Object) items[i]) + " (" + Re.s(R.string.default_mark) + ')';
                i++;
            }
            Alert.show(Alert.create().setCustomTitle(Config.getCustomTitle(this.nameRes, this.helpRes, this.appDependent)).setSingleChoiceItems(items_, valueToId(this.value), this));
        }

        @Override // android.ext.Config.Option
        public boolean onClick_(DialogInterface dialog, int which) {
            this.value = idToValue(which);
            Config.save();
            Tools.dismiss(dialog);
            return true;
        }

        @Override // android.ext.Config.Option
        public String toString() {
            CharSequence val = null;
            try {
                int id = valueToId(this.value);
                val = this.values != null ? Re.s(this.values[id]) : this.stringValues[id];
            } catch (IndexOutOfBoundsException e) {
            }
            if (val == null) {
                val = "int:" + this.value;
            }
            return String.valueOf(Tools.stripColon(this.nameRes)) + ": " + ((Object) val);
        }

        public int valueToId(int val) {
            loadMap();
            if (this.map != null) {
                int id = 0;
                int i = 1;
                while (true) {
                    if (i >= this.map.length) {
                        break;
                    }
                    if (this.map[i] != val) {
                        i++;
                    } else {
                        id = i;
                        break;
                    }
                }
                return id;
            }
            return val;
        }

        public int idToValue(int id) {
            loadMap();
            return this.map == null ? id : this.map[id];
        }
    }

    public static class OptionMultiChoice extends OptionSingleChoice implements DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnShowListener, AbsListView.OnScrollListener {
        boolean[] selected;

        public OptionMultiChoice(int id, int nameRes, int[] values, int defaultValue, String storageKey, boolean appDependent) {
            super(id, nameRes, values, defaultValue, storageKey, appDependent);
        }

        public OptionMultiChoice(int id, int nameRes, int[] values, int defaultValue, String storageKey, boolean appDependent, int helpRes) {
            this(id, nameRes, values, defaultValue, storageKey, appDependent);
            this.helpRes = helpRes;
        }

        @Override // android.ext.Config.OptionSingleChoice
        protected void show(CharSequence[] items) {
            boolean[] sel = new boolean[items.length];
            this.selected = sel;
            int value_ = this.value;
            for (int i = 0; i < items.length; i++) {
                int val = idToValue(i);
                sel[i] = (value_ & val) == val;
                if (Build.VERSION.SDK_INT < 11 && (items[i] instanceof String)) {
                    items[i] = Tools.colorize(items[i], -1);
                }
            }
            AlertDialog dialog = Alert.create(Tools.getDarkContext()).setCustomTitle(Config.getCustomTitle(this.nameRes, this.helpRes, this.appDependent)).setMultiChoiceItems(items, sel, this).setPositiveButton(R.string.save, this).setNeutralButton(R.string.reset_button, this).setNegativeButton(R.string.cancel, this).create();
            Alert.setOnShowListener(dialog, this);
            Alert.show(dialog);
        }

        @Override // android.content.DialogInterface.OnMultiChoiceClickListener
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            boolean[] sel = this.selected;
            if (sel == null) {
                Log.e("", new NullPointerException(toString()));
                return;
            }
            try {
                sel[which] = isChecked;
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e("", e);
            }
        }

        @Override // android.ext.Config.OptionSingleChoice, android.ext.Config.Option
        public boolean onClick_(DialogInterface dialog, int which) {
            if (which == -1 || which == -3) {
                boolean[] sel = this.selected;
                int newValue = this.defaultValue;
                if (sel != null && which == -1) {
                    for (int i = 0; i < sel.length; i++) {
                        int val = idToValue(i);
                        if (sel[i]) {
                            newValue |= val;
                        } else {
                            newValue &= val ^ (-1);
                        }
                    }
                    this.selected = null;
                }
                this.value = newValue;
                Config.save();
                Tools.dismiss(dialog);
                return true;
            }
            return false;
        }

        @Override // android.ext.Config.OptionSingleChoice, android.ext.Config.Option
        public String toString() {
            return Tools.stripColon(this.nameRes);
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface d) {
            if (Build.VERSION.SDK_INT >= 23 && (d instanceof AlertDialog)) {
                AlertDialog dialog = (AlertDialog) d;
                ListView list = dialog.getListView();
                if (list != null) {
                    list.setOnScrollListener(this);
                }
            }
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            try {
                int size = view.getChildCount();
                for (int i = 0; i < size; i++) {
                    try {
                        View v = view.getChildAt(i);
                        if (v instanceof CheckedTextView) {
                            ((CheckedTextView) v).refreshDrawableState();
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                }
            } catch (Throwable e2) {
                Log.badImplementation(e2);
            }
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            onScrollStateChanged(view, 0);
        }

        protected void loadTabMap(boolean withOrientation) {
            if (this.map == null) {
                int[] map = new int[withOrientation ? 6 : 3];
                CharSequence[] stringValues = new CharSequence[map.length];
                if (withOrientation) {
                    map[0] = 1;
                    stringValues[0] = String.valueOf(Re.s(R.string.search)) + ", " + Re.s(R.string.portrait);
                    int i = 0 + 1;
                    map[i] = 2;
                    stringValues[i] = String.valueOf(Re.s(R.string.search)) + ", " + Re.s(R.string.landscape);
                    int i2 = i + 1;
                    map[i2] = 4;
                    stringValues[i2] = String.valueOf(Re.s(R.string.saved_list)) + ", " + Re.s(R.string.portrait);
                    int i3 = i2 + 1;
                    map[i3] = 8;
                    stringValues[i3] = String.valueOf(Re.s(R.string.saved_list)) + ", " + Re.s(R.string.landscape);
                    int i4 = i3 + 1;
                    map[i4] = 16;
                    stringValues[i4] = String.valueOf(Re.s(R.string.memory_editor)) + ", " + Re.s(R.string.portrait);
                    int i5 = i4 + 1;
                    map[i5] = 32;
                    stringValues[i5] = String.valueOf(Re.s(R.string.memory_editor)) + ", " + Re.s(R.string.landscape);
                    int i6 = i5 + 1;
                } else {
                    map[0] = 1;
                    stringValues[0] = Re.s(R.string.search);
                    int i7 = 0 + 1;
                    map[i7] = 2;
                    stringValues[i7] = Re.s(R.string.saved_list);
                    int i8 = i7 + 1;
                    map[i8] = 4;
                    stringValues[i8] = Re.s(R.string.memory_editor);
                    int i9 = i8 + 1;
                }
                this.map = map;
                this.stringValues = stringValues;
                this.values = null;
            }
        }
    }

    public static class OptionMultiChoiceSh extends OptionMultiChoice {
        private final int cnt;

        public OptionMultiChoiceSh(int id, int nameRes, int[] values, int defaultValue, String storageKey, boolean appDependent, int cnt) {
            super(id, nameRes, values, defaultValue, storageKey, appDependent);
            this.cnt = cnt;
        }

        @Override // android.ext.Config.OptionSingleChoice
        protected void loadMap() {
            super.loadMap();
            if (this.map == null) {
                int[] map = new int[this.cnt];
                CharSequence[] stringValues = new CharSequence[map.length];
                for (int i = 0; i < map.length; i++) {
                    map[i] = 1 << i;
                    stringValues[i] = Integer.toString(i + 1);
                }
                this.map = map;
                this.stringValues = stringValues;
                this.values = null;
            }
        }

        @Override // android.ext.Config.OptionMultiChoice, android.ext.Config.OptionSingleChoice, android.ext.Config.Option
        public boolean onClick_(DialogInterface dialog, int which) {
            boolean ret = super.onClick_(dialog, which);
            if (ret) {
                Tools.showToast(R.string.need_restart);
            }
            return ret;
        }
    }

    public static class OptionEdit extends Option implements DialogInterface.OnShowListener, View.OnClickListener {
        private boolean allowXor;
        private int editDataType;
        long max;
        int min;
        int unitRes;
        private boolean useConverter;
        private WeakReference<AlertDialog> weakDialog;
        private WeakReference<EditText> weakEdit;

        public OptionEdit(int id, int nameRes, int unitRes, int min, long max, int defaultValue, String storageKey, boolean appDependent, boolean useConverter, boolean allowXor, int editDataType) {
            this(id, nameRes, unitRes, min, max, defaultValue, storageKey, appDependent, useConverter, allowXor);
            this.editDataType = editDataType;
        }

        public OptionEdit(int id, int nameRes, int unitRes, int min, long max, int defaultValue, String storageKey, boolean appDependent, boolean useConverter, boolean allowXor) {
            this(id, nameRes, unitRes, min, max, defaultValue, storageKey, appDependent);
            this.useConverter = useConverter;
            this.allowXor = allowXor;
        }

        public OptionEdit(int id, int nameRes, int unitRes, int min, long max, int defaultValue, String storageKey, boolean appDependent) {
            super(id, nameRes, defaultValue, storageKey, appDependent);
            this.weakDialog = new WeakReference<>(null);
            this.weakEdit = new WeakReference<>(null);
            this.useConverter = false;
            this.allowXor = true;
            this.editDataType = 8;
            this.unitRes = unitRes;
            this.min = min;
            this.max = max;
        }

        public OptionEdit(int id, int nameRes, int unitRes, int min, long max, int defaultValue, String storageKey, boolean appDependent, int helpRes) {
            this(id, nameRes, unitRes, min, max, defaultValue, storageKey, appDependent);
            this.helpRes = helpRes;
        }

        @Override // android.ext.Config.Option
        public void change() {
            show();
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            Tools.setButtonListener(dialog, -1, null, this, this.weakEdit.get());
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (v != null && v.getId() == R.id.default_) {
                EditText edit = this.weakEdit.get();
                if (edit != null) {
                    edit.setText(Tools.stringFormat("%,d", Integer.valueOf(this.defaultValue)));
                    return;
                }
                return;
            }
            onClick(this.weakDialog.get(), -1);
        }

        protected void show() {
            View v = LayoutInflater.inflateStatic(R.layout.option_edit, (ViewGroup) null);
            TextView tv = (TextView) v.findViewById(R.id.message);
            tv.setText(Re.s(this.nameRes));
            if (this.helpRes != -1 && this.helpRes != 0) {
                Tools.setButtonHelpBackground(tv);
                tv.setOnClickListener(new View.OnClickListener() { // from class: android.ext.Config.OptionEdit.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v2) {
                        Alert.show(Alert.create().setMessage(String.valueOf(Re.s(OptionEdit.this.nameRes)) + '\n' + Re.s(OptionEdit.this.helpRes)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                    }
                });
            }
            ((TextView) v.findViewById(R.id.hint)).setText(Tools.stringFormat(Re.s(R.string.type_edit_limit), Tools.stringFormat("%,d", Integer.valueOf(this.min)), Tools.stringFormat("%,d", Long.valueOf(this.max))));
            ((TextView) v.findViewById(R.id.unit)).setText(getUnit());
            TextView default_ = (TextView) v.findViewById(R.id.default_);
            default_.setText(String.valueOf(Tools.stripColon(R.string.default_)) + ": " + Tools.stringFormat("%,d", Integer.valueOf(this.defaultValue)));
            default_.setOnClickListener(this);
            EditText edit = (EditText) v.findViewById(R.id.number);
            edit.setText(Tools.stringFormat("%,d", Integer.valueOf(this.value)));
            edit.setDataType(this.editDataType);
            this.weakEdit = new WeakReference<>(edit);
            HexConverter converter = (HexConverter) v.findViewById(R.id.number_converter);
            converter.setTag(new Object[]{edit, 4});
            converter.setVisibility(this.useConverter ? 0 : 8);
            converter.setUseXor(this.allowXor);
            AlertDialog dialog = Alert.create().setView(InternalKeyboard.getView(v)).setPositiveButton(R.string.ok, this).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).create();
            Alert.setOnShowListener(dialog, this);
            this.weakDialog = new WeakReference<>(dialog);
            Alert.show(dialog, edit);
        }

        @Override // android.ext.Config.Option
        public boolean onClick_(DialogInterface dialog, int which) {
            EditText edit = this.weakEdit.get();
            if (edit == null) {
                return false;
            }
            try {
                String val = SearchMenuItem.checkScript(edit.getText().toString().trim());
                ParserNumbers.Result value = ParserNumbers.parseLong(val);
                long min = this.min;
                long max = this.max;
                if (value.value < min || value.value > max) {
                    throw new NumberFormatException(String.valueOf(Tools.stringFormat(Re.s(R.string.number_out_of_range), val)) + Tools.stringFormat(" [%,d, %,d]", Long.valueOf(min), Long.valueOf(max)));
                }
                History.add(val, this.editDataType);
                this.value = Math.max((int) Math.min(value.value, this.max), this.min);
                Config.save();
                Tools.dismiss(this.weakDialog);
                return true;
            } catch (NumberFormatException e) {
                SearchMenuItem.inputError(e, edit);
                return false;
            }
        }

        private String getUnit() {
            return this.unitRes == 0 ? "" : Re.s(this.unitRes);
        }

        @Override // android.ext.Config.Option
        public String toString() {
            return String.valueOf(Re.s(this.nameRes)) + ' ' + Tools.stringFormat("%,d", Integer.valueOf(this.value)) + ' ' + getUnit();
        }
    }

    public static class OptionHidden extends Option {
        public OptionHidden(int id, int nameRes, int defaultValue, String storageKey, boolean appDependent) {
            super(id, nameRes, defaultValue, storageKey, appDependent);
        }

        @Override // android.ext.Config.Option
        public boolean onClick_(DialogInterface dialog, int which) {
            return false;
        }

        @Override // android.ext.Config.Option
        public void change() {
        }
    }

    public static class OptionSeek extends Option implements SeekBar.OnSeekBarChangeListener {
        int max;
        protected WeakReference<SeekBar> weakSeek;

        public OptionSeek(int id, int nameRes, int max, int defaultValue, String storageKey, boolean appDependent) {
            super(id, nameRes, defaultValue, storageKey, appDependent);
            this.weakSeek = new WeakReference<>(null);
            this.max = max;
        }

        public OptionSeek(int id, int nameRes, int max, int defaultValue, String storageKey, boolean appDependent, int helpRes) {
            this(id, nameRes, max, defaultValue, storageKey, appDependent);
            this.helpRes = helpRes;
        }

        @Override // android.ext.Config.Option
        public void change() {
            show();
        }

        protected void show() {
            View v = LayoutInflater.inflateStatic(R.layout.option_seek, (ViewGroup) null);
            SeekBar seek = (SeekBar) v.findViewById(R.id.seek);
            seek.setMax(this.max);
            seek.setProgress(this.value);
            seek.setOnSeekBarChangeListener(this);
            this.weakSeek = new WeakReference<>(seek);
            Alert.show(Alert.create().setCustomTitle(Config.getCustomTitle(this.nameRes, this.helpRes, this.appDependent)).setView(v).setPositiveButton(R.string.ok, this).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).create());
        }

        @Override // android.ext.Config.Option
        public boolean onClick_(DialogInterface dialog, int which) {
            SeekBar sb = this.weakSeek.get();
            if (sb == null) {
                return false;
            }
            this.value = sb.getProgress();
            Config.save();
            return true;
        }

        @Override // android.ext.Config.Option
        public String toString() {
            return Tools.stripColon(this.nameRes);
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    private static String getLoadedPackage() {
        try {
            ProcessList.ProcessInfo info = MainService.instance.processInfo;
            if (info == null) {
                return null;
            }
            return info.packageName;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static void load() {
        boolean needSave = false;
        SharedPreferences storage = Tools.getSharedPreferences();
        load(storage);
        String pkg = getLoadedPackage();
        for (int i = 0; i < list.size(); i++) {
            Option option = list.valueAt(i);
            String key = option.storageKey;
            if (option.appDependent) {
                if (pkg != null) {
                    key = String.valueOf(pkg) + '-' + key;
                    option.value = storage.getInt(key, option.defaultValue);
                }
            } else {
                try {
                    option.value = storage.getInt(key, option.defaultValue);
                } catch (ClassCastException e) {
                    try {
                        option.value = storage.getBoolean(option.storageKey, option.defaultValue == 1) ? 1 : 0;
                        needSave = true;
                    } catch (ClassCastException ex) {
                        Log.e("Failed load data for " + option.storageKey, ex);
                    }
                }
            }
        }
        if (needSave) {
            save();
        } else {
            notifyChanges();
        }
    }

    private static void notifyChanges() {
        updateConst();
        if (MainService.instance != null) {
            if (MainService.instance.mConfigListView != null) {
                MainService.instance.notifyDataSetChanged(MainService.instance.mConfigListView.getAdapter());
                MainService.instance.setupSavedListUpdates();
            }
            MainService.instance.mDaemonManager.sendConfig();
            MainService.instance.updateStatusBar();
        }
    }

    public static void save() {
        SPEditor editor = new SPEditor();
        save(editor);
        String pkg = getLoadedPackage();
        for (int i = 0; i < list.size(); i++) {
            Option option = list.valueAt(i);
            String key = option.storageKey;
            if (option.appDependent) {
                if (pkg != null) {
                    key = String.valueOf(pkg) + '-' + key;
                    editor.putInt(key, option.value, option.defaultValue);
                }
            } else {
                editor.putInt(key, option.value, option.defaultValue);
            }
        }
        editor.commit();
        notifyChanges();
    }

    public static Option get(int id) {
        return list.get(id);
    }

    public static ImageView setIconSize(ImageView button) {
        int size = Tools.dp2px48();
        return setIconSize(button, size, size);
    }

    public static ImageView setIconSize(ImageView button, int iconSize) {
        int size = Tools.dp2px48();
        return setIconSize(button, size, size, iconSize);
    }

    public static ImageView setIconSize(ImageView button, int width, int height) {
        return setIconSize(button, width, height, getIconSize());
    }

    public static ImageView setIconSize(ImageView button, int width, int height, int size) {
        button.setAdjustViewBounds(false);
        button.setMinimumWidth(width);
        button.setMinimumHeight(height);
        int iconSize = (int) Tools.dp2px(size);
        int padX = width > iconSize ? (width - iconSize) / 2 : 0;
        int padY = height > iconSize ? (height - iconSize) / 2 : 0;
        button.setPadding(padX, padY, padX, padY);
        try {
            button.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
        button.invalidate();
        return button;
    }

    public static ImageView setIconSizeEx(ImageView button, int size) {
        button.setAdjustViewBounds(false);
        button.setMinimumWidth(size);
        button.setMinimumHeight(size);
        ViewGroup.LayoutParams lp = button.getLayoutParams();
        if (lp != null) {
            lp.width = size;
            lp.height = size;
            button.setLayoutParams(lp);
        }
        button.setScaleType(ImageView.ScaleType.FIT_CENTER);
        button.invalidate();
        return button;
    }

    public static int getIconSize() {
        return getIconSize(iconsSize);
    }

    public static int getIconSize(int size) {
        return (size * 2) + 24;
    }

    static String getKeyName(int id) {
        switch (id) {
            case 0:
                String name = Re.s(R.string.no);
                return name;
            case 1:
                return "KEY_ESC";
            case 2:
                return "KEY_1";
            case 3:
                return "KEY_2";
            case 4:
                return "KEY_3";
            case 5:
                return "KEY_4";
            case 6:
                return "KEY_5";
            case 7:
                return "KEY_6";
            case 8:
                return "KEY_7";
            case 9:
                return "KEY_8";
            case 10:
                return "KEY_9";
            case 11:
                return "KEY_0";
            case 12:
                return "KEY_MINUS";
            case 13:
                return "KEY_EQUAL";
            case 14:
                return "KEY_BACKSPACE";
            case 15:
                return "KEY_TAB";
            case 16:
                return "KEY_Q";
            case 17:
                return "KEY_W";
            case 18:
                return "KEY_E";
            case 19:
                return "KEY_R";
            case 20:
                return "KEY_T";
            case 21:
                return "KEY_Y";
            case 22:
                return "KEY_U";
            case 23:
                return "KEY_I";
            case 24:
                return "KEY_O";
            case 25:
                return "KEY_P";
            case 26:
                return "KEY_LEFTBRACE";
            case 27:
                return "KEY_RIGHTBRACE";
            case 28:
                return "KEY_ENTER";
            case 29:
                return "KEY_LEFTCTRL";
            case 30:
                return "KEY_A";
            case 31:
                return "KEY_S";
            case 32:
                return "KEY_D";
            case 33:
                return "KEY_F";
            case 34:
                return "KEY_G";
            case 35:
                return "KEY_H";
            case 36:
                return "KEY_J";
            case 37:
                return "KEY_K";
            case 38:
                return "KEY_L";
            case 39:
                return "KEY_SEMICOLON";
            case 40:
                return "KEY_APOSTROPHE";
            case 41:
                return "KEY_GRAVE";
            case 42:
                return "KEY_LEFTSHIFT";
            case 43:
                return "KEY_BACKSLASH";
            case 44:
                return "KEY_Z";
            case 45:
                return "KEY_X";
            case 46:
                return "KEY_C";
            case 47:
                return "KEY_V";
            case LasmConstants.SHL /* 48 */:
                return "KEY_B";
            case LasmConstants.SHR /* 49 */:
                return "KEY_N";
            case 50:
                return "KEY_M";
            case LasmConstants.PROTO /* 51 */:
                return "KEY_COMMA";
            case LasmConstants.GOTO /* 52 */:
                return "KEY_DOT";
            case LasmConstants.OP /* 53 */:
                return "KEY_SLASH";
            case LasmConstants.SET_TOP /* 54 */:
                return "KEY_RIGHTSHIFT";
            case LasmConstants.SKIP_NEXT /* 55 */:
                return "KEY_KPASTERISK";
            case LasmConstants.NIL /* 56 */:
                return "KEY_LEFTALT";
            case LasmConstants.TRUE /* 57 */:
                return "KEY_SPACE";
            case LasmConstants.FALSE /* 58 */:
                return "KEY_CAPSLOCK";
            case 59:
                return "KEY_F1";
            case 60:
                return "KEY_F2";
            case 61:
                return "KEY_F3";
            case 62:
                return "KEY_F4";
            case 63:
                return "KEY_F5";
            case 64:
                return "KEY_F6";
            case LasmConstants.NUMPARAMS /* 65 */:
                return "KEY_F7";
            case LasmConstants.IS_VARARG /* 66 */:
                return "KEY_F8";
            case LasmConstants.MAXSTACKSIZE /* 67 */:
                return "KEY_F9";
            case LasmConstants.UPVAL /* 68 */:
                return "KEY_F10";
            case LasmConstants.LINE /* 69 */:
                return "KEY_NUMLOCK";
            case LasmConstants.LOCAL /* 70 */:
                return "KEY_SCROLLLOCK";
            case LasmConstants.END_LOCAL /* 71 */:
                return "KEY_KP7";
            case LasmConstants.FUNC /* 72 */:
                return "KEY_KP8";
            case LasmConstants.END /* 73 */:
                return "KEY_KP9";
            case LasmConstants.EOL /* 74 */:
                return "KEY_KPMINUS";
            case LasmConstants.RANGE /* 75 */:
                return "KEY_KP4";
            case LasmConstants.LABEL /* 76 */:
                return "KEY_KP5";
            case LasmConstants.V /* 77 */:
                return "KEY_KP6";
            case LasmConstants.U /* 78 */:
                return "KEY_KPPLUS";
            case LasmConstants.INT /* 79 */:
                return "KEY_KP1";
            case LasmConstants.HEX /* 80 */:
                return "KEY_KP2";
            case LasmConstants.STRING /* 81 */:
                return "KEY_KP3";
            case 82:
                return "KEY_KP0";
            case LasmConstants.NL /* 83 */:
                return "KEY_KPDOT";
            case LasmConstants.FLOAT /* 84 */:
            case 195:
            case 196:
            case 197:
            case 198:
            case 199:
            case 249:
            case Constants.MAXSTACK /* 250 */:
            case 251:
            case 252:
            case 253:
            case 254:
            case 255:
            case 266:
            case 267:
            case 268:
            case 269:
            case 270:
            case 271:
            case 280:
            case 281:
            case 282:
            case 283:
            case 284:
            case 285:
            case 286:
            case 287:
            case 300:
            case 301:
            case 302:
            case 319:
            case 329:
            case 338:
            case 339:
            case 340:
            case 341:
            case 342:
            case 343:
            case 344:
            case 345:
            case 346:
            case 347:
            case 348:
            case 349:
            case 350:
            case 351:
            case 443:
            case 444:
            case 445:
            case 446:
            case 447:
            case 452:
            case 453:
            case 454:
            case 455:
            case 456:
            case 457:
            case 458:
            case 459:
            case 460:
            case 461:
            case 462:
            case 463:
            case 485:
            case 486:
            case 487:
            case 488:
            case 489:
            case 490:
            case 491:
            case 492:
            case 493:
            case 494:
            case 495:
            case 496:
            case 507:
            case 508:
            case 509:
            case 510:
            case 511:
            case 524:
            case 525:
            case 526:
            case 527:
            case 543:
            case 548:
            case 549:
            case 550:
            case 551:
            case 552:
            case 553:
            case 554:
            case 555:
            case 556:
            case 557:
            case 558:
            case 559:
            case 561:
            case 562:
            case 563:
            case 564:
            case 565:
            case 566:
            case 567:
            case 568:
            case 569:
            case 570:
            case 571:
            case 572:
            case 573:
            case 574:
            case 575:
            case 583:
            case 584:
            case 585:
            case 586:
            case 587:
            case 588:
            case 589:
            case 590:
            case 591:
            case 594:
            case 595:
            case 596:
            case 597:
            case 598:
            case 599:
            case 600:
            case 601:
            case 602:
            case 603:
            case 604:
            case 605:
            case 606:
            case 607:
            case 608:
            case 609:
            case 610:
            case 611:
            case 612:
            case 613:
            case 614:
            case 615:
            case 616:
            case 617:
            case 618:
            case 619:
            case 620:
            case 621:
            case 622:
            case 623:
            case 624:
            case 625:
            case 626:
            case 627:
            case 628:
            case 629:
            case 630:
            case 631:
            case 632:
            case 633:
            case 634:
            case 635:
            case 636:
            case 637:
            case 638:
            case 639:
            case 640:
            case 641:
            case 642:
            case 643:
            case 644:
            case 645:
            case 646:
            case 647:
            case 648:
            case 649:
            case 650:
            case 651:
            case 652:
            case 653:
            case 654:
            case 655:
            case 656:
            case 657:
            case 658:
            case 659:
            case 660:
            case 661:
            case 662:
            case 663:
            case 664:
            case 665:
            case 666:
            case 667:
            case 668:
            case 669:
            case 670:
            case 671:
            case 672:
            case 673:
            case 674:
            case 675:
            case 676:
            case 677:
            case 678:
            case 679:
            case 680:
            case 681:
            case 682:
            case 683:
            case 684:
            case 685:
            case 686:
            case 687:
            case 688:
            case 689:
            case 690:
            case 691:
            case 692:
            case 693:
            case 694:
            case 695:
            case 696:
            case 697:
            case 698:
            case 699:
            case 700:
            case 701:
            case 702:
            case 703:
            default:
                String name2 = "UNKNOWN_" + id;
                return name2;
            case LasmConstants.DIGIT /* 85 */:
                return "KEY_ZENKAKUHANKAKU";
            case LasmConstants.EXP /* 86 */:
                return "KEY_102ND";
            case LasmConstants.HEXDIGIT /* 87 */:
                return "KEY_F11";
            case 88:
                return "KEY_F12";
            case 89:
                return "KEY_RO";
            case 90:
                return "KEY_KATAKANA";
            case 91:
                return "KEY_HIRAGANA";
            case 92:
                return "KEY_HENKAN";
            case 93:
                return "KEY_KATAKANAHIRAGANA";
            case 94:
                return "KEY_MUHENKAN";
            case 95:
                return "KEY_KPJPCOMMA";
            case 96:
                return "KEY_KPENTER";
            case 97:
                return "KEY_RIGHTCTRL";
            case 98:
                return "KEY_KPSLASH";
            case 99:
                return "KEY_SYSRQ";
            case Message.ERROR_FATAL /* 100 */:
                return "KEY_RIGHTALT";
            case 101:
                return "KEY_LINEFEED";
            case 102:
                return "KEY_HOME";
            case 103:
                return "KEY_UP";
            case 104:
                return "KEY_PAGEUP";
            case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                return "KEY_LEFT";
            case Message.ERROR_EPERM_ON_ATTACH_DEFEND /* 106 */:
                return "KEY_RIGHT";
            case Message.ERROR_SPEEDHACK_FAIL /* 107 */:
                return "KEY_END";
            case Message.ERROR_GROUP_FAIL /* 108 */:
                return "KEY_DOWN";
            case 109:
                return "KEY_PAGEDOWN";
            case 110:
                return "KEY_INSERT";
            case 111:
                return "KEY_DELETE";
            case 112:
                return "KEY_MACRO";
            case 113:
                return "KEY_MUTE";
            case 114:
                return "KEY_VOLUMEDOWN";
            case 115:
                return "KEY_VOLUMEUP";
            case 116:
                return "KEY_POWER";
            case 117:
                return "KEY_KPEQUAL";
            case 118:
                return "KEY_KPPLUSMINUS";
            case 119:
                return "KEY_PAUSE";
            case 120:
                return "KEY_SCALE";
            case 121:
                return "KEY_KPCOMMA";
            case 122:
                return "KEY_HANGEUL";
            case 123:
                return "KEY_HANJA";
            case LuaError.MAX_STRING_LEN /* 124 */:
                return "KEY_YEN";
            case 125:
                return "KEY_LEFTMETA";
            case 126:
                return "KEY_RIGHTMETA";
            case AddressItem.FLAG_AUTO /* 127 */:
                return "KEY_COMPOSE";
            case 128:
                return "KEY_STOP";
            case 129:
                return "KEY_AGAIN";
            case 130:
                return "KEY_PROPS";
            case 131:
                return "KEY_UNDO";
            case 132:
                return "KEY_FRONT";
            case 133:
                return "KEY_COPY";
            case 134:
                return "KEY_OPEN";
            case 135:
                return "KEY_PASTE";
            case 136:
                return "KEY_FIND";
            case 137:
                return "KEY_CUT";
            case 138:
                return "KEY_HELP";
            case 139:
                return "KEY_MENU";
            case 140:
                return "KEY_CALC";
            case 141:
                return "KEY_SETUP";
            case 142:
                return "KEY_SLEEP";
            case 143:
                return "KEY_WAKEUP";
            case 144:
                return "KEY_FILE";
            case 145:
                return "KEY_SENDFILE";
            case 146:
                return "KEY_DELETEFILE";
            case 147:
                return "KEY_XFER";
            case 148:
                return "KEY_PROG1";
            case 149:
                return "KEY_PROG2";
            case 150:
                return "KEY_WWW";
            case 151:
                return "KEY_MSDOS";
            case 152:
                return "KEY_SCREENLOCK";
            case 153:
                return "KEY_DIRECTION";
            case 154:
                return "KEY_CYCLEWINDOWS";
            case 155:
                return "KEY_MAIL";
            case 156:
                return "KEY_BOOKMARKS";
            case 157:
                return "KEY_COMPUTER";
            case 158:
                return "KEY_BACK";
            case 159:
                return "KEY_FORWARD";
            case 160:
                return "KEY_CLOSECD";
            case 161:
                return "KEY_EJECTCD";
            case 162:
                return "KEY_EJECTCLOSECD";
            case 163:
                return "KEY_NEXTSONG";
            case 164:
                return "KEY_PLAYPAUSE";
            case 165:
                return "KEY_PREVIOUSSONG";
            case 166:
                return "KEY_STOPCD";
            case 167:
                return "KEY_RECORD";
            case 168:
                return "KEY_REWIND";
            case 169:
                return "KEY_PHONE";
            case 170:
                return "KEY_ISO";
            case 171:
                return "KEY_CONFIG";
            case 172:
                return "KEY_HOMEPAGE";
            case 173:
                return "KEY_REFRESH";
            case 174:
                return "KEY_EXIT";
            case 175:
                return "KEY_MOVE";
            case 176:
                return "KEY_EDIT";
            case 177:
                return "KEY_SCROLLUP";
            case 178:
                return "KEY_SCROLLDOWN";
            case 179:
                return "KEY_KPLEFTPAREN";
            case 180:
                return "KEY_KPRIGHTPAREN";
            case 181:
                return "KEY_NEW";
            case 182:
                return "KEY_REDO";
            case 183:
                return "KEY_F13";
            case 184:
                return "KEY_F14";
            case 185:
                return "KEY_F15";
            case 186:
                return "KEY_F16";
            case 187:
                return "KEY_F17";
            case 188:
                return "KEY_F18";
            case 189:
                return "KEY_F19";
            case 190:
                return "KEY_F20";
            case 191:
                return "KEY_F21";
            case 192:
                return "KEY_F22";
            case 193:
                return "KEY_F23";
            case 194:
                return "KEY_F24";
            case 200:
                return "KEY_PLAYCD";
            case 201:
                return "KEY_PAUSECD";
            case 202:
                return "KEY_PROG3";
            case 203:
                return "KEY_PROG4";
            case 204:
                return "KEY_DASHBOARD";
            case 205:
                return "KEY_SUSPEND";
            case 206:
                return "KEY_CLOSE";
            case 207:
                return "KEY_PLAY";
            case 208:
                return "KEY_FASTFORWARD";
            case 209:
                return "KEY_BASSBOOST";
            case 210:
                return "KEY_PRINT";
            case 211:
                return "KEY_HP";
            case 212:
                return "KEY_CAMERA";
            case 213:
                return "KEY_SOUND";
            case 214:
                return "KEY_QUESTION";
            case 215:
                return "KEY_EMAIL";
            case 216:
                return "KEY_CHAT";
            case 217:
                return "KEY_SEARCH";
            case 218:
                return "KEY_CONNECT";
            case 219:
                return "KEY_FINANCE";
            case 220:
                return "KEY_SPORT";
            case 221:
                return "KEY_SHOP";
            case 222:
                return "KEY_ALTERASE";
            case 223:
                return "KEY_CANCEL";
            case 224:
                return "KEY_BRIGHTNESSDOWN";
            case 225:
                return "KEY_BRIGHTNESSUP";
            case 226:
                return "KEY_MEDIA";
            case 227:
                return "KEY_SWITCHVIDEOMODE";
            case 228:
                return "KEY_KBDILLUMTOGGLE";
            case 229:
                return "KEY_KBDILLUMDOWN";
            case 230:
                return "KEY_KBDILLUMUP";
            case 231:
                return "KEY_SEND";
            case 232:
                return "KEY_REPLY";
            case 233:
                return "KEY_FORWARDMAIL";
            case 234:
                return "KEY_SAVE";
            case 235:
                return "KEY_DOCUMENTS";
            case 236:
                return "KEY_BATTERY";
            case 237:
                return "KEY_BLUETOOTH";
            case 238:
                return "KEY_WLAN";
            case 239:
                return "KEY_UWB";
            case 240:
                return "KEY_UNKNOWN";
            case 241:
                return "KEY_VIDEO_NEXT";
            case 242:
                return "KEY_VIDEO_PREV";
            case 243:
                return "KEY_BRIGHTNESS_CYCLE";
            case 244:
                return "KEY_BRIGHTNESS_AUTO";
            case 245:
                return "KEY_DISPLAY_OFF";
            case 246:
                return "KEY_WWAN";
            case 247:
                return "KEY_RFKILL";
            case 248:
                return "KEY_MICMUTE";
            case 256:
                return "BTN_0";
            case 257:
                return "BTN_1";
            case 258:
                return "BTN_2";
            case 259:
                return "BTN_3";
            case 260:
                return "BTN_4";
            case 261:
                return "BTN_5";
            case 262:
                return "BTN_6";
            case 263:
                return "BTN_7";
            case 264:
                return "BTN_8";
            case 265:
                return "BTN_9";
            case 272:
                return "BTN_LEFT";
            case 273:
                return "BTN_RIGHT";
            case 274:
                return "BTN_MIDDLE";
            case 275:
                return "BTN_SIDE";
            case 276:
                return "BTN_EXTRA";
            case 277:
                return "BTN_FORWARD";
            case 278:
                return "BTN_BACK";
            case 279:
                return "BTN_TASK";
            case 288:
                return "BTN_TRIGGER";
            case 289:
                return "BTN_THUMB";
            case 290:
                return "BTN_THUMB2";
            case 291:
                return "BTN_TOP";
            case 292:
                return "BTN_TOP2";
            case 293:
                return "BTN_PINKIE";
            case 294:
                return "BTN_BASE";
            case 295:
                return "BTN_BASE2";
            case 296:
                return "BTN_BASE3";
            case 297:
                return "BTN_BASE4";
            case 298:
                return "BTN_BASE5";
            case 299:
                return "BTN_BASE6";
            case 303:
                return "BTN_DEAD";
            case 304:
                return "BTN_A";
            case 305:
                return "BTN_B";
            case 306:
                return "BTN_C";
            case 307:
                return "BTN_X";
            case 308:
                return "BTN_Y";
            case 309:
                return "BTN_Z";
            case 310:
                return "BTN_TL";
            case 311:
                return "BTN_TR";
            case 312:
                return "BTN_TL2";
            case 313:
                return "BTN_TR2";
            case 314:
                return "BTN_SELECT";
            case 315:
                return "BTN_START";
            case 316:
                return "BTN_MODE";
            case 317:
                return "BTN_THUMBL";
            case 318:
                return "BTN_THUMBR";
            case 320:
                return "BTN_TOOL_PEN";
            case 321:
                return "BTN_TOOL_RUBBER";
            case 322:
                return "BTN_TOOL_BRUSH";
            case 323:
                return "BTN_TOOL_PENCIL";
            case 324:
                return "BTN_TOOL_AIRBRUSH";
            case 325:
                return "BTN_TOOL_FINGER";
            case 326:
                return "BTN_TOOL_MOUSE";
            case 327:
                return "BTN_TOOL_LENS";
            case 328:
                return "BTN_TOOL_QUINTTAP";
            case 330:
                return "BTN_TOUCH";
            case 331:
                return "BTN_STYLUS";
            case 332:
                return "BTN_STYLUS2";
            case 333:
                return "BTN_TOOL_DOUBLETAP";
            case 334:
                return "BTN_TOOL_TRIPLETAP";
            case 335:
                return "BTN_TOOL_QUADTAP";
            case 336:
                return "BTN_WHEEL";
            case 337:
                return "BTN_GEAR_UP";
            case 352:
                return "KEY_OK";
            case 353:
                return "KEY_SELECT";
            case 354:
                return "KEY_GOTO";
            case 355:
                return "KEY_CLEAR";
            case 356:
                return "KEY_POWER2";
            case 357:
                return "KEY_OPTION";
            case 358:
                return "KEY_INFO";
            case 359:
                return "KEY_TIME";
            case 360:
                return "KEY_VENDOR";
            case 361:
                return "KEY_ARCHIVE";
            case 362:
                return "KEY_PROGRAM";
            case 363:
                return "KEY_CHANNEL";
            case 364:
                return "KEY_FAVORITES";
            case 365:
                return "KEY_EPG";
            case 366:
                return "KEY_PVR";
            case 367:
                return "KEY_MHP";
            case 368:
                return "KEY_LANGUAGE";
            case 369:
                return "KEY_TITLE";
            case 370:
                return "KEY_SUBTITLE";
            case 371:
                return "KEY_ANGLE";
            case 372:
                return "KEY_ZOOM";
            case 373:
                return "KEY_MODE";
            case 374:
                return "KEY_KEYBOARD";
            case 375:
                return "KEY_SCREEN";
            case 376:
                return "KEY_PC";
            case 377:
                return "KEY_TV";
            case 378:
                return "KEY_TV2";
            case 379:
                return "KEY_VCR";
            case 380:
                return "KEY_VCR2";
            case 381:
                return "KEY_SAT";
            case 382:
                return "KEY_SAT2";
            case 383:
                return "KEY_CD";
            case 384:
                return "KEY_TAPE";
            case 385:
                return "KEY_RADIO";
            case 386:
                return "KEY_TUNER";
            case 387:
                return "KEY_PLAYER";
            case 388:
                return "KEY_TEXT";
            case 389:
                return "KEY_DVD";
            case 390:
                return "KEY_AUX";
            case 391:
                return "KEY_MP3";
            case 392:
                return "KEY_AUDIO";
            case 393:
                return "KEY_VIDEO";
            case 394:
                return "KEY_DIRECTORY";
            case 395:
                return "KEY_LIST";
            case 396:
                return "KEY_MEMO";
            case 397:
                return "KEY_CALENDAR";
            case 398:
                return "KEY_RED";
            case 399:
                return "KEY_GREEN";
            case 400:
                return "KEY_YELLOW";
            case 401:
                return "KEY_BLUE";
            case 402:
                return "KEY_CHANNELUP";
            case 403:
                return "KEY_CHANNELDOWN";
            case 404:
                return "KEY_FIRST";
            case 405:
                return "KEY_LAST";
            case 406:
                return "KEY_AB";
            case 407:
                return "KEY_NEXT";
            case 408:
                return "KEY_RESTART";
            case 409:
                return "KEY_SLOW";
            case 410:
                return "KEY_SHUFFLE";
            case 411:
                return "KEY_BREAK";
            case 412:
                return "KEY_PREVIOUS";
            case 413:
                return "KEY_DIGITS";
            case 414:
                return "KEY_TEEN";
            case 415:
                return "KEY_TWEN";
            case 416:
                return "KEY_VIDEOPHONE";
            case 417:
                return "KEY_GAMES";
            case 418:
                return "KEY_ZOOMIN";
            case 419:
                return "KEY_ZOOMOUT";
            case 420:
                return "KEY_ZOOMRESET";
            case 421:
                return "KEY_WORDPROCESSOR";
            case 422:
                return "KEY_EDITOR";
            case 423:
                return "KEY_SPREADSHEET";
            case 424:
                return "KEY_GRAPHICSEDITOR";
            case 425:
                return "KEY_PRESENTATION";
            case 426:
                return "KEY_DATABASE";
            case 427:
                return "KEY_NEWS";
            case 428:
                return "KEY_VOICEMAIL";
            case 429:
                return "KEY_ADDRESSBOOK";
            case 430:
                return "KEY_MESSENGER";
            case 431:
                return "KEY_DISPLAYTOGGLE";
            case 432:
                return "KEY_SPELLCHECK";
            case 433:
                return "KEY_LOGOFF";
            case 434:
                return "KEY_DOLLAR";
            case 435:
                return "KEY_EURO";
            case 436:
                return "KEY_FRAMEBACK";
            case 437:
                return "KEY_FRAMEFORWARD";
            case 438:
                return "KEY_CONTEXT_MENU";
            case 439:
                return "KEY_MEDIA_REPEAT";
            case 440:
                return "KEY_10CHANNELSUP";
            case 441:
                return "KEY_10CHANNELSDOWN";
            case 442:
                return "KEY_IMAGES";
            case 448:
                return "KEY_DEL_EOL";
            case 449:
                return "KEY_DEL_EOS";
            case 450:
                return "KEY_INS_LINE";
            case 451:
                return "KEY_DEL_LINE";
            case 464:
                return "KEY_FN";
            case 465:
                return "KEY_FN_ESC";
            case 466:
                return "KEY_FN_F1";
            case 467:
                return "KEY_FN_F2";
            case 468:
                return "KEY_FN_F3";
            case 469:
                return "KEY_FN_F4";
            case 470:
                return "KEY_FN_F5";
            case 471:
                return "KEY_FN_F6";
            case 472:
                return "KEY_FN_F7";
            case 473:
                return "KEY_FN_F8";
            case 474:
                return "KEY_FN_F9";
            case 475:
                return "KEY_FN_F10";
            case 476:
                return "KEY_FN_F11";
            case 477:
                return "KEY_FN_F12";
            case 478:
                return "KEY_FN_1";
            case 479:
                return "KEY_FN_2";
            case 480:
                return "KEY_FN_D";
            case 481:
                return "KEY_FN_E";
            case 482:
                return "KEY_FN_F";
            case 483:
                return "KEY_FN_S";
            case 484:
                return "KEY_FN_B";
            case 497:
                return "KEY_BRL_DOT1";
            case 498:
                return "KEY_BRL_DOT2";
            case 499:
                return "KEY_BRL_DOT3";
            case 500:
                return "KEY_BRL_DOT4";
            case 501:
                return "KEY_BRL_DOT5";
            case 502:
                return "KEY_BRL_DOT6";
            case 503:
                return "KEY_BRL_DOT7";
            case 504:
                return "KEY_BRL_DOT8";
            case 505:
                return "KEY_BRL_DOT9";
            case 506:
                return "KEY_BRL_DOT10";
            case 512:
                return "KEY_NUMERIC_0";
            case 513:
                return "KEY_NUMERIC_1";
            case 514:
                return "KEY_NUMERIC_2";
            case 515:
                return "KEY_NUMERIC_3";
            case 516:
                return "KEY_NUMERIC_4";
            case 517:
                return "KEY_NUMERIC_5";
            case 518:
                return "KEY_NUMERIC_6";
            case 519:
                return "KEY_NUMERIC_7";
            case 520:
                return "KEY_NUMERIC_8";
            case 521:
                return "KEY_NUMERIC_9";
            case 522:
                return "KEY_NUMERIC_STAR";
            case 523:
                return "KEY_NUMERIC_POUND";
            case 528:
                return "KEY_CAMERA_FOCUS";
            case 529:
                return "KEY_WPS_BUTTON";
            case 530:
                return "KEY_TOUCHPAD_TOGGLE";
            case 531:
                return "KEY_TOUCHPAD_ON";
            case 532:
                return "KEY_TOUCHPAD_OFF";
            case 533:
                return "KEY_CAMERA_ZOOMIN";
            case 534:
                return "KEY_CAMERA_ZOOMOUT";
            case 535:
                return "KEY_CAMERA_UP";
            case 536:
                return "KEY_CAMERA_DOWN";
            case 537:
                return "KEY_CAMERA_LEFT";
            case 538:
                return "KEY_CAMERA_RIGHT";
            case 539:
                return "KEY_ATTENDANT_ON";
            case 540:
                return "KEY_ATTENDANT_OFF";
            case 541:
                return "KEY_ATTENDANT_TOGGLE";
            case 542:
                return "KEY_LIGHTS_TOGGLE";
            case 544:
                return "BTN_DPAD_UP";
            case 545:
                return "BTN_DPAD_DOWN";
            case 546:
                return "BTN_DPAD_LEFT";
            case 547:
                return "BTN_DPAD_RIGHT";
            case 560:
                return "KEY_ALS_TOGGLE";
            case 576:
                return "KEY_BUTTONCONFIG";
            case 577:
                return "KEY_TASKMANAGER";
            case 578:
                return "KEY_JOURNAL";
            case 579:
                return "KEY_CONTROLPANEL";
            case 580:
                return "KEY_APPSELECT";
            case 581:
                return "KEY_SCREENSAVER";
            case 582:
                return "KEY_VOICECOMMAND";
            case 592:
                return "KEY_BRIGHTNESS_MIN";
            case 593:
                return "KEY_BRIGHTNESS_MAX";
            case 704:
                return "BTN_TRIGGER_HAPPY1";
            case 705:
                return "BTN_TRIGGER_HAPPY2";
            case 706:
                return "BTN_TRIGGER_HAPPY3";
            case 707:
                return "BTN_TRIGGER_HAPPY4";
            case 708:
                return "BTN_TRIGGER_HAPPY5";
            case 709:
                return "BTN_TRIGGER_HAPPY6";
            case 710:
                return "BTN_TRIGGER_HAPPY7";
            case 711:
                return "BTN_TRIGGER_HAPPY8";
            case 712:
                return "BTN_TRIGGER_HAPPY9";
            case 713:
                return "BTN_TRIGGER_HAPPY10";
            case 714:
                return "BTN_TRIGGER_HAPPY11";
            case 715:
                return "BTN_TRIGGER_HAPPY12";
            case 716:
                return "BTN_TRIGGER_HAPPY13";
            case 717:
                return "BTN_TRIGGER_HAPPY14";
            case 718:
                return "BTN_TRIGGER_HAPPY15";
            case 719:
                return "BTN_TRIGGER_HAPPY16";
            case 720:
                return "BTN_TRIGGER_HAPPY17";
            case 721:
                return "BTN_TRIGGER_HAPPY18";
            case 722:
                return "BTN_TRIGGER_HAPPY19";
            case 723:
                return "BTN_TRIGGER_HAPPY20";
            case 724:
                return "BTN_TRIGGER_HAPPY21";
            case 725:
                return "BTN_TRIGGER_HAPPY22";
            case 726:
                return "BTN_TRIGGER_HAPPY23";
            case 727:
                return "BTN_TRIGGER_HAPPY24";
            case 728:
                return "BTN_TRIGGER_HAPPY25";
            case 729:
                return "BTN_TRIGGER_HAPPY26";
            case 730:
                return "BTN_TRIGGER_HAPPY27";
            case 731:
                return "BTN_TRIGGER_HAPPY28";
            case 732:
                return "BTN_TRIGGER_HAPPY29";
            case 733:
                return "BTN_TRIGGER_HAPPY30";
            case 734:
                return "BTN_TRIGGER_HAPPY31";
            case 735:
                return "BTN_TRIGGER_HAPPY32";
            case 736:
                return "BTN_TRIGGER_HAPPY33";
            case 737:
                return "BTN_TRIGGER_HAPPY34";
            case 738:
                return "BTN_TRIGGER_HAPPY35";
            case 739:
                return "BTN_TRIGGER_HAPPY36";
            case 740:
                return "BTN_TRIGGER_HAPPY37";
            case 741:
                return "BTN_TRIGGER_HAPPY38";
            case 742:
                return "BTN_TRIGGER_HAPPY39";
            case 743:
                return "BTN_TRIGGER_HAPPY40";
        }
    }
}
