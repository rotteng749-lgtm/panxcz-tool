package android.ext;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.ext.BaseActivity;
import android.ext.Config;
import android.ext.EditTextSpeeds;
import android.ext.ExceptionHandler;
import android.ext.ListSpeed;
import android.ext.ProcessList;
import android.ext.Tools;
import android.fix.LayoutInflater;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.sup.ContainerHelpers;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.ActivityMain;
import catch_.me_.if_.you_.can_.MainActivity;
import catch_.me_.if_.you_.can_.R;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ConfigListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
    private static final String HIDE_ICON = "hide-icon";
    private static final int HW = 2;
    static final int RESTORE = 3;
    private static final int ROW_SIZE = 3;
    public static final String SP_USAGE = "mc";
    private static final int SW = 1;
    public static final String DEVS = "\td2dyno - __owner__, __design__, __pm__.\n\tEnyby - __code__, __pm__.\n\tTrasd - __tech__, __pm__.\n\tAqua - __creator__.\n".intern();
    public static long USAGE = 0;
    static final int[] helpConfig = {R.string.help_faq_title, R.string.help_faq, -1, R.string.help_context_title, R.string.help_context, -1, R.string.help_basics_title, R.string.help_basics, 2, R.string.help_floating_icon_title, R.string.help_floating_icon_, -1, R.string.help_scripts_title, R.string.help_scripts, 21, R.string.help_exact_search_title, R.string.help_exact_search, -1, R.string.help_speedhack_title, R.string.help_speedhack, 1, R.string.help_group_search_title, R.string.help_group_search_, 6, R.string.help_range_search_title, R.string.help_range_search, 5, R.string.help_encrypted_search_title, R.string.help_encrypted_search, 7, R.string.search_unknown_value, R.string.help_fuzzy_search, 0, R.string.help_mask_search_title, R.string.help_mask_search, 8, R.string.help_fill_title, R.string.help_fill, 9, R.string.help_string_search_title, R.string.help_text_search, 12, R.string.help_xor_search_title, R.string.help_xor_search, 11, R.string.help_search_filter_title, R.string.help_search_filter, 20, R.string.help_memory_filter_title, R.string.help_memory_filter, 16, R.string.help_others_title, R.string.help_others, 4, R.string.help_typical_usage_title, R.string.help_typical_usage, 3, R.string.help_speed_up_title, R.string.help_speed_up, 10, R.string.help_games_protection_title, R.string.help_games_protection_, 14, R.string.help_hide_gg_title, R.string.help_games_protection_end, 24, R.string.help_hints_title, R.string.help_hints_0, -1, R.string.help_hex_colors_title, R.string.help_hex_colors, -1, R.string.help_pointer_search_title, R.string.help_pointer_search, 17, R.string.help_unaligned_title, R.string.help_unaligned, 22, R.string.help_formula_calculator_title, R.string.help_formula_calculator, 23, R.string.help_add_to_value_title, R.string.help_add_to_value, 13, R.string.unrandomizer, R.string.help_unrandomizer, 18, R.string.restart_game_without_protection, R.string.warn_restart_off_prot, 15, R.string.disable_protection, R.string.warn_patch_off_prot_, -1, R.string.work_without_root_title, R.string.work_without_root, 19, R.string.help_game_not_listed_title, R.string.help_game_not_listed_root, -1, R.string.help_force_close_title, R.string.daemon_killed, -1, R.string.help_lasm_title, R.string.help_lasm, 25};
    static final String[] helpVideos = {"kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh24950vhdufk0xqnqrz0zlwk0nqrzq0fkdqjhv0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh25570vshhg0kdfn0dqg0wlph0mxps0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh248;0krz0xvh0h|h0lfrq0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh248<0krz0vhdufk0hqfu|swhg0ydoxh0jdphjxdugldq2\r\rkwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh24970frpsduh0phwkrgv0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh24<40klvwru|0xvdjh0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh25550h{dpsoh0ri0udqjh0vhdufk0jdphjxdugldq2\r\rkwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh25590krz0wr0vhdufk0urxqghg0ydoxh0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh255<0krz0wr0xvh0rughuhg0jurxs0vhdufk0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh248<0krz0vhdufk0hqfu|swhg0ydoxh0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh25960h{dpsoh0ri0wkh0pdvn0vhdufk0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh255:0krz0wr0xvh0iloo0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh24<50krz0wr0vshhg0xs0vhdufk0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh25;60{ru0vhdufk0jxlgh0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh28:80;330wh{w0vwulqj0kh{0dre0vhdufk0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh26690rswlrq0dgg0wr0ydoxh0gr0qrw0uhsodfh0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh26860lpsorvlrq0qhyhu0orvh0krsh0kdfn0ohyho0klgh0iurp0wkh0jdph0jdphjxdugldq2\r\rkwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh26870lpsorvlrq0qhyhu0orvh0krsh0kdfn0fuhglwv0klgh0iurp0wkh0jdph0jdphjxdugldq2\r\rkwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh26930lpsorvlrq0qhyhu0orvh0krsh0kdfn0fuhglwv0uhvwduw0zlwkrxw0surwhfwlrq0jdphjxdugldq2\r\rkwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh26950lpsorvlrq0qhyhu0orvh0krsh0kdfn0fuhglwv0glvdeoh0surwhfwlrq0jdphjxdugldq2\r\rkwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh28680:530xvh0urrw0iurp0yluwxdo0vsdfh0iru0klgh0jdphjxdugldq0yluwxdo{srvhg0jdphjxdugldq2\r\rkwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh28;30iuhh0iluh0dyrlg0ghwhfwlrq0urrw0iurp0yluwxdo0vsdfh0yluwxdo{srvhg0jdphjxdugldq2\r\rkwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh28;40dyrlg0ghwhfwlrq0urrw0iurp0yluwxdo0vsdfh0yluwxdo{srvhg0jdphjxdugldq2\r\rkwws=22jdphjxdugldq1qhw2iruxp2wdjv2dss(53klgh2\r\rkwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh28;80dyrlg0ghwhfwlrq0urrw0iurp0yluwxdo0vsdfh0pxowlsoh0dffrxqwv0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh26;80uhvwduw0zlwkrxw0surwhfwlrq0uhsdlu0wkh0uherrw0uxq0jdphjxdugldq0jdphjxdugldq2\r\rkwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh26;70uhvwduw0zlwkrxw0surwhfwlrq0uhsdlu0wkh0uherrw0uherrw0ghylfh0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh267:0krz0wr0vhdufk0ilowhu0lq0wkh0phpru|0hglwru0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh26770krz0wr0txlfno|0vhdufk0iru0srlqwhuv0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh26990xqudqgrpl}hu0jdphjxdugldq2\r\rkwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh26;40uxvvldq0vorwv0kdfn0vwruh0xqudqgrpl}hu0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2qrurrw", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh27480ilowhuv0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2ilohv2fdwhjru|290oxd0vfulswv2\r\rkwws=22jdphjxdugldq1qhw2khos2", "kwws=22jdphjxdugldq1qhw2iruxp2ilohv2iloh2:970xqdoljqhg0vhdufk2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh286:0:630irupxod0fdofxodwru0jdphjxdugldq2\r\rkwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh286;0jxq0zdu0vkrrwlqj0jdphv0kdfn0sulfh0irupxod0fdofxodwru0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh28680:530xvh0urrw0iurp0yluwxdo0vsdfh0iru0klgh0jdphjxdugldq0yluwxdo{srvhg0jdphjxdugldq2\r\rkwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh28;30iuhh0iluh0dyrlg0ghwhfwlrq0urrw0iurp0yluwxdo0vsdfh0yluwxdo{srvhg0jdphjxdugldq2\r\rkwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh28;40dyrlg0ghwhfwlrq0urrw0iurp0yluwxdo0vsdfh0yluwxdo{srvhg0jdphjxdugldq2\r\rkwws=22jdphjxdugldq1qhw2iruxp2wdjv2dss(53klgh2\r\rkwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh28;80dyrlg0ghwhfwlrq0urrw0iurp0yluwxdo0vsdfh0pxowlsoh0dffrxqwv0jdphjxdugldq2", "kwws=22jdphjxdugldq1qhw2iruxp2jdoohu|2lpdjh29470il{lqj0d0vfulsw0xvlqj0dvvhpeohu0jdphjxdugldq2"};
    private static boolean helpFixed = false;
    private WeakReference<TextView> mGameSection = new WeakReference<>(null);
    private WeakReference<TextView> mConfigIconTextView = new WeakReference<>(null);
    private WeakReference<ImageView> mConfigIcon = new WeakReference<>(null);
    private boolean updated = false;
    private Tools.ScrollPos scrollPos = null;
    private View[] mViewList = ContainerHelpers.EMPTY_VIEWS;

    public ConfigListAdapter() {
        HotPoint hotPoint = MainService.instance.hotPoint;
        updateConfigIcon(hotPoint.getLayoutAlpha(), hotPoint.getVanishingTime(), null, null, hotPoint.getSize());
        updateData();
    }

    public void load(boolean selected) {
        if (selected) {
            if (this.mViewList.length <= 0) {
                ViewGroup parent = (ViewGroup) LayoutInflater.inflateStatic(R.layout.service_config, (ViewGroup) null);
                this.mGameSection = new WeakReference<>((TextView) parent.findViewById(R.id.game_section));
                this.mConfigIconTextView = new WeakReference<>((TextView) parent.findViewById(R.id.config_icon_text));
                this.mConfigIcon = new WeakReference<>((ImageView) parent.findViewById(R.id.config_icon_example));
                ArrayList<View> mViewList = new ArrayList<>();
                for (int i = 0; i < parent.getChildCount(); i++) {
                    try {
                        View view = parent.getChildAt(i);
                        if (view.getVisibility() != 8) {
                            if (view instanceof TextView) {
                                ((TextView) view).setText(Re.s(((TextView) view).getText().toString().trim()));
                                setIcon((TextView) view);
                            }
                            view.setLayoutParams(new AbsListView.LayoutParams(-1, -2, 0));
                            mViewList.add(view);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                }
                Iterator<View> it = mViewList.iterator();
                while (it.hasNext()) {
                    Tools.getViewForAttach(it.next());
                }
                this.mViewList = (View[]) mViewList.toArray(new View[mViewList.size()]);
                notifyDataSetChanged();
                MainService service = MainService.instance;
                service.mConfigListView.setAdapter((ListAdapter) this);
                HotPoint hotPoint = service.hotPoint;
                setGameName();
                updateConfigIcon(hotPoint.getLayoutAlpha(), hotPoint.getVanishingTime(), null, null, hotPoint.getSize());
                final Tools.ScrollPos scrollPos = this.scrollPos;
                if (scrollPos != null) {
                    Tools.setScrollPos(MainService.instance.mConfigListView, scrollPos);
                    MainService.instance.mConfigListView.post(new Runnable() { // from class: android.ext.ConfigListAdapter.1
                        @Override // java.lang.Runnable
                        public void run() {
                            Tools.setScrollPos(MainService.instance.mConfigListView, scrollPos);
                        }
                    });
                    return;
                }
                return;
            }
            return;
        }
        if (this.mViewList.length != 0) {
            ListView list = MainService.instance.mConfigListView;
            this.scrollPos = Tools.getScrollPos(list);
            this.mViewList = ContainerHelpers.EMPTY_VIEWS;
            notifyDataSetChanged();
            list.setAdapter((ListAdapter) null);
        }
    }

    private void setIcon(TextView tv) {
        int id = tv.getId();
        int icon = 0;
        switch (id) {
            case R.id.help /* 2131427347 */:
                icon = R.drawable.ic_help_circle_outline_white_24dp;
                break;
            case R.id.config_time_jump_panel /* 2131427460 */:
                icon = R.drawable.ic_fast_forward_white_24dp;
                break;
            case R.id.config_unrandomizer /* 2131427462 */:
                icon = R.drawable.ic_dice_multiple_white_24dp;
                break;
            case R.id.config_autopause /* 2131427468 */:
                icon = R.drawable.ic_pause_white_18dp;
                break;
            case R.id.config_saved_list_updates_interval /* 2131427470 */:
                icon = R.drawable.ic_content_save_white_24dp;
                break;
            case R.id.config_speeds /* 2131427471 */:
                icon = R.drawable.ic_speedometer_white_24dp;
                break;
            case R.id.config_history_limit /* 2131427486 */:
                icon = R.drawable.ic_history_white_24dp;
                break;
            case R.id.config_use_small_list_items /* 2131427492 */:
                icon = R.drawable.ic_format_list_bulleted_white_24dp;
                break;
            case R.id.config_icons_size /* 2131427497 */:
                icon = R.drawable.ic_tooltip_edit_white_24dp;
                break;
            case R.id.screenshot /* 2131427512 */:
                icon = R.drawable.ic_cellphone_screenshot_white_24dp;
                break;
        }
        if (icon != 0) {
            Tools.addIconToTextView(tv, Tools.getDrawable(icon), Config.getIconSize());
        }
    }

    public void updateData() {
        for (View view : this.mViewList) {
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                int id = view.getId();
                Config.Option option = Config.get(id);
                boolean appDependent = id == R.id.config_speedhack_functions || id == R.id.config_unrandomizer;
                if (option != null) {
                    tv.setText(option.toString());
                    appDependent = option.appDependent;
                }
                if (appDependent) {
                    tv.setBackgroundColor(SystemConstants.JUMP_ITEM_COLOR);
                }
            }
        }
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View[] list = this.mViewList;
        return position >= list.length ? new View(Tools.getContext()) : list[position];
    }

    @Override // android.widget.BaseAdapter
    public void notifyDataSetChanged() {
        if (!this.updated) {
            this.updated = true;
            updateData();
            this.updated = false;
        }
        super.notifyDataSetChanged();
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mViewList.length;
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    void setGameName() {
        TextView textView = this.mGameSection.get();
        if (textView != null) {
            String text = Re.s(R.string.game_settings);
            MainService service = MainService.instance;
            ProcessList.ProcessInfo pi = service.processInfo;
            String name = pi != null ? pi.name : null;
            if (name != null) {
                text = String.valueOf(Tools.stripColon(text)) + " \"" + name + "\":";
            }
            textView.setText(text);
        }
    }

    void updateConfigIcon(float alpha, int hide, TextView view, ImageView icon, int size) {
        MainService service = MainService.instance;
        String hideConfig = hide < 0 ? Re.s(R.string.icon_hide_never) : Tools.stringFormat(Re.s(R.string.icon_hide_after), Integer.valueOf(hide));
        String text = "fail";
        try {
            text = Tools.stringFormat(Re.s(R.string.config_icon_text), Integer.valueOf(100 - ((int) (100.0f * alpha))), hideConfig);
        } catch (Throwable e) {
            Log.e("Fail setup string for icon config", e);
        }
        TextView textView = this.mConfigIconTextView.get();
        if (textView != null) {
            textView.setText(text);
        }
        if (view != null) {
            view.setText(text);
        }
        service.hotPoint.setSize(size);
        int px = service.hotPoint.getSizePx();
        TimeJumpPanel panel = service.timeJumpPanel;
        if (panel != null) {
            panel.setMinSize(px);
        }
        ImageView imageView = this.mConfigIcon.get();
        if (imageView != null) {
            Tools.setImageAlpha(imageView, 255.0f * alpha);
            Config.setIconSizeEx(imageView, px);
        }
        if (icon != null) {
            Tools.setImageAlpha(icon, 255.0f * alpha);
            Config.setIconSizeEx(icon, px);
        }
        service.hotPoint.setLayoutAlpha(alpha);
        service.hotPoint.setVanishingTime(hide);
        service.notifyDataSetChanged(service.mConfigListView.getAdapter());
    }

    static void needRestart() {
        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.app_name)).setMessage(Re.s(R.string.exit_confirm)).setPositiveButton(Re.s(R.string.restart), new ExitListener(200, true)).setNegativeButton(Re.s(R.string.skip), (DialogInterface.OnClickListener) null));
    }

    private static void saveUsage() {
        new SPEditor().putLong(SP_USAGE, USAGE, 0L).commit();
    }

    public static String getUsage() {
        SharedPreferences sp = Tools.getSharedPreferences();
        SharedPreferences.Editor ed = sp.edit();
        StringBuilder out = new StringBuilder();
        long val = sp.getLong(SP_USAGE, 0L);
        ed.remove(SP_USAGE);
        if (val != 0) {
            out.append("&mc=");
            out.append('=');
            out.append(Long.toHexString(val));
        }
        ed.commit();
        return out.toString();
    }

    /* JADX WARN: Type inference failed for: r4v29, types: [android.ext.ConfigListAdapter$10] */
    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view != null) {
            int viewId = view.getId();
            if (viewId != -1) {
                long bit = 1 << (position % 63);
                long old = USAGE;
                if ((old & bit) == 0) {
                    USAGE |= bit;
                    saveUsage();
                }
                final MainService service = MainService.instance;
                Config.Option option = Config.get(viewId);
                if (option != null) {
                    if (viewId == R.id.config_keyboard) {
                        option.setOnChangeListner(new Config.Option.OnChangeListener() { // from class: android.ext.ConfigListAdapter.2
                            @Override // android.ext.Config.Option.OnChangeListener
                            public int onChange(DialogInterface dialog, int which) {
                                if (which == 1 && !InternalKeyboard.allowUsage()) {
                                    MainService.reportBadFirmware();
                                    return 0;
                                }
                                return which;
                            }
                        });
                    } else if (viewId == R.id.config_icons_size) {
                        option.setOnChangeListner(new Config.Option.OnChangeListener() { // from class: android.ext.ConfigListAdapter.3
                            @Override // android.ext.Config.Option.OnChangeListener
                            public int onChange(DialogInterface dialog, int which) {
                                if (which != Config.iconsSize) {
                                    ConfigListAdapter.needRestart();
                                }
                                return which;
                            }
                        });
                    }
                    option.change();
                    return;
                }
                switch (viewId) {
                    case R.id.help /* 2131427347 */:
                        showHelp();
                        break;
                    case R.id.changelog /* 2131427388 */:
                        ThreadManager.runOnWorkerThread(new Runnable() { // from class: android.ext.ConfigListAdapter.10
                            @Override // java.lang.Runnable
                            public void run() {
                                BufferedReader in = null;
                                final StringBuilder changelog = new StringBuilder();
                                try {
                                    URL url = new URL(Tools.removeNewLinesChars("kwwsv=22jdphjxdugldq1qhw2yhuvlrq1w{w"));
                                    in = new BufferedReader(new InputStreamReader(url.openStream()));
                                } catch (Throwable e) {
                                    Log.e("Load changelog", e);
                                    changelog.append(e.toString());
                                }
                                if (in != null) {
                                    try {
                                        while (true) {
                                            String line = in.readLine();
                                            if (line == null) {
                                                break;
                                            }
                                            changelog.append(line);
                                            changelog.append('\n');
                                            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ConfigListAdapter.10.1
                                                @Override // java.lang.Runnable
                                                public void run() {
                                                    Tools.setClickableText(Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.change_log)).setMessage(changelog.toString()).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null).create()));
                                                }
                                            });
                                        }
                                    } catch (IOException e2) {
                                        Log.e("Load changelog", e2);
                                        changelog.append(e2.toString());
                                    } finally {
                                        try {
                                            in.close();
                                        } catch (Throwable e3) {
                                            Log.badImplementation(e3);
                                        }
                                    }
                                }
                                ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ConfigListAdapter.10.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        Tools.setClickableText(Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.change_log)).setMessage(changelog.toString()).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null).create()));
                                    }
                                });
                            }
                        });
                        break;
                    case R.id.donate /* 2131427452 */:
                        new BaseActivity.GoOnForum(BaseActivity.GoOnForum.S2).onClick(null, 0);
                        break;
                    case R.id.config_reset /* 2131427453 */:
                        service.detectApp(true);
                        break;
                    case R.id.config_exit /* 2131427454 */:
                        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.exit)).setPositiveButton(Re.s(R.string.yes), new ExitListener(300)).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null));
                        break;
                    case R.id.config_kill_game /* 2131427455 */:
                        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.kill_game)).setMessage(Re.s(R.string.kill_game_info_)).setPositiveButton(Re.s(R.string.yes), new DialogInterface.OnClickListener() { // from class: android.ext.ConfigListAdapter.5
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                service.processKill();
                            }
                        }).setNegativeButton(Re.s(R.string.no), (DialogInterface.OnClickListener) null));
                        break;
                    case R.id.config_speedhack_functions /* 2131427459 */:
                        MainService.instance.timersEditor.show();
                        break;
                    case R.id.config_unrandomizer /* 2131427462 */:
                        MainService.instance.randEditor.show();
                        break;
                    case R.id.config_speeds /* 2131427471 */:
                        View v = null;
                        try {
                            v = LayoutInflater.inflateStatic(R.layout.list_speeds_config, (ViewGroup) null);
                        } catch (IndexOutOfBoundsException e) {
                            Log.badImplementation(e);
                            MainService.reportBadFirmware();
                        }
                        if (v != null) {
                            final TextView hint = (TextView) v.findViewById(R.id.hint);
                            TextView text = (TextView) v.findViewById(R.id.message);
                            text.setText(Re.s(text.getText().toString().trim()));
                            final EditTextSpeeds edit = (EditTextSpeeds) v.findViewById(R.id.speeds);
                            TextView default_ = (TextView) v.findViewById(R.id.default_);
                            default_.setText(String.valueOf(Tools.stripColon(R.string.default_)) + ": " + ListSpeed.getDefault());
                            default_.setOnClickListener(new View.OnClickListener() { // from class: android.ext.ConfigListAdapter.7
                                @Override // android.view.View.OnClickListener
                                public void onClick(View v2) {
                                    edit.setText(ListSpeed.getDefault());
                                }
                            });
                            edit.setText(MainService.instance.listSpeed.get());
                            edit.setDataType(16);
                            edit.setOnChangedListener(new EditTextSpeeds.OnChangedListener() { // from class: android.ext.ConfigListAdapter.8
                                public void updateHint() {
                                    String text2;
                                    if (edit == null || hint == null) {
                                        text2 = Searcher.MASK_PLACEHOLDER;
                                    } else {
                                        try {
                                            String list = edit.getText().toString().trim();
                                            int pos = edit.getSelectionStart();
                                            if (pos < 0) {
                                                pos = 0;
                                            }
                                            if (pos >= list.length()) {
                                                pos = list.length() - 1;
                                            }
                                            int start = pos <= 0 ? -1 : list.lastIndexOf(59, pos - 1);
                                            if (start < 0) {
                                                start = -1;
                                            }
                                            int end = list.indexOf(59, pos);
                                            if (end < 0) {
                                                end = list.length();
                                            }
                                            String number = list.substring(start + 1, end);
                                            double speed = Tools.parseTime(number);
                                            if (speed > 0.0d) {
                                                ListSpeed.MN mn = ListSpeed.getMN(speed);
                                                text2 = Tools.stringFormat(Re.s(R.string.speed_info), Tools.longToTime(mn.N), Tools.longToTime(mn.M));
                                            } else {
                                                text2 = "??";
                                            }
                                        } catch (NumberFormatException e2) {
                                            text2 = "???";
                                        }
                                    }
                                    hint.setText(text2);
                                }

                                @Override // android.ext.EditTextSpeeds.OnChangedListener
                                public void onSelectionChanged(EditText obj, int selStart, int selEnd) {
                                    updateHint();
                                }

                                @Override // android.ext.EditTextSpeeds.OnChangedListener
                                public void onTextChanged(EditText obj, CharSequence text2, int start, int lengthBefore, int lengthAfter) {
                                    updateHint();
                                }
                            });
                            Alert.show(Alert.create().setView(InternalKeyboard.getView(v)).setPositiveButton(Re.s(R.string.confirm), new DialogInterface.OnClickListener() { // from class: android.ext.ConfigListAdapter.9
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog, int which) {
                                    String localeStr = edit.getText().toString().trim();
                                    History.add(localeStr, 16);
                                    MainService.instance.listSpeed.set(localeStr);
                                }
                            }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null), edit);
                        }
                        break;
                    case R.id.config_path /* 2131427480 */:
                        changePath(null);
                        break;
                    case R.id.config_su /* 2131427481 */:
                        changeSu();
                        break;
                    case R.id.config_toolbar_buttons /* 2131427495 */:
                        changeToolbarButtons();
                        break;
                    case R.id.config_icon /* 2131427498 */:
                        View v2 = LayoutInflater.inflateStatic(R.layout.service_config_icon, (ViewGroup) null);
                        final SeekBar transparencyBar = (SeekBar) v2.findViewById(R.id.transparency);
                        final SeekBar hideBar = (SeekBar) v2.findViewById(R.id.hide);
                        final SeekBar sizeBar = (SeekBar) v2.findViewById(R.id.size);
                        final TextView text2 = (TextView) v2.findViewById(R.id.config_icon_text);
                        final ImageView icon = (ImageView) v2.findViewById(R.id.config_icon_example);
                        float alpha = service.hotPoint.getLayoutAlpha();
                        int hide = service.hotPoint.getVanishingTime();
                        int size = service.hotPoint.getSize();
                        transparencyBar.setProgress(10 - ((int) (10.0f * alpha)));
                        hideBar.setProgress(hide < 0 ? hideBar.getMax() : hide);
                        sizeBar.setMax(12);
                        sizeBar.setProgress(size);
                        updateConfigIcon(alpha, hide, text2, icon, size);
                        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() { // from class: android.ext.ConfigListAdapter.4
                            @Override // android.widget.SeekBar.OnSeekBarChangeListener
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                float alpha2 = (10 - transparencyBar.getProgress()) / 10.0f;
                                int hide2 = hideBar.getMax() == hideBar.getProgress() ? -1 : hideBar.getProgress();
                                int size2 = sizeBar.getProgress();
                                ConfigListAdapter.this.updateConfigIcon(alpha2, hide2, text2, icon, size2);
                            }

                            @Override // android.widget.SeekBar.OnSeekBarChangeListener
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }

                            @Override // android.widget.SeekBar.OnSeekBarChangeListener
                            public void onStopTrackingTouch(SeekBar seekBar) {
                            }
                        };
                        transparencyBar.setOnSeekBarChangeListener(listener);
                        hideBar.setOnSeekBarChangeListener(listener);
                        sizeBar.setOnSeekBarChangeListener(listener);
                        Alert.show(Alert.create().setPositiveButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null).setView(v2));
                        break;
                    case R.id.hide_icons /* 2131427501 */:
                        CharSequence[] list = {String.valueOf(Re.s(R.string.nothing)) + " (" + Re.s(R.string.default_mark) + ')', Re.s(R.string.app_short_name_sw), Re.s(R.string.app_short_name_hw)};
                        AlertDialog dialog = Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.hide_icons)).setItems(list, new DialogInterface.OnClickListener() { // from class: android.ext.ConfigListAdapter.12
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog2, int which) {
                                ConfigListAdapter.hideIcon(which);
                            }
                        }).create();
                        ListView listView = dialog.getListView();
                        if (listView != null) {
                            Drawable[] drawableArr = new Drawable[3];
                            drawableArr[1] = Tools.getDrawable(R.mipmap.ic_sw_48dp);
                            drawableArr[2] = Tools.getDrawable(R.mipmap.ic_hw_48dp);
                            Tools.addIconsToListViewItems(listView, list, drawableArr, 48, 0);
                        }
                        Alert.show(dialog);
                        break;
                    case R.id.config_language /* 2131427504 */:
                        configLanguage();
                        break;
                    case R.id.config_disable_protection /* 2131427506 */:
                        if (Main.isRootMode()) {
                            Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.disable_protection)).setMessage(Re.s(R.string.warn_patch_off_prot_)).setPositiveButton(Re.s(R.string.ok), new DialogInterface.OnClickListener() { // from class: android.ext.ConfigListAdapter.13
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog2, int which) {
                                    service.mDaemonManager.disableProtection();
                                }
                            }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
                        }
                        break;
                    case R.id.config_clear_history /* 2131427507 */:
                        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.clear_history)).setMessage(Re.s(R.string.clear_history_warn)).setPositiveButton(Re.s(R.string.ok), new DialogInterface.OnClickListener() { // from class: android.ext.ConfigListAdapter.14
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog2, int which) {
                                History.clear();
                            }
                        }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
                        break;
                    case R.id.config_clear_defaults /* 2131427508 */:
                        Alert.show(Alert.create().setMessage(Re.s(R.string.reset_ignore)).setPositiveButton(Re.s(R.string.confirm), new DialogInterface.OnClickListener() { // from class: android.ext.ConfigListAdapter.11
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog2, int which) {
                                Config.ignore = 0L;
                                Config.save();
                            }
                        }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
                        break;
                    case R.id.config_last_stats /* 2131427509 */:
                        View v3 = LayoutInflater.inflateStatic(R.layout.main_new_version, (ViewGroup) null);
                        ((TextView) v3.findViewById(R.id.changelog)).setText(Log.lastSearchLog);
                        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.last_stats)).setView(v3).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                        break;
                    case R.id.show_logcat /* 2131427510 */:
                        showLogcat();
                        break;
                    case R.id.config_region_log /* 2131427511 */:
                        Debug.runCollectRegionLog();
                        break;
                    case R.id.screenshot /* 2131427512 */:
                        takeScreenshot();
                        break;
                    case R.id.about /* 2131427513 */:
                        String message = "__version__ " + ((103.1f * 1) - 7.1f) + " (" + ((int) (((16424.3f * 1) - 431.3f) + 0.5f)) + ")\n\n__developers__\n" + DEVS + "\n__special_thanks__\n\tdanilo_scala,\n\tYoungBai,\n\tKorean L,\n\tiAndroHacker,\n\tMobile46,\n\treduxpl,\n\tjan.eike.kottlarz,\n\ttimo.szillat,\n\tSubZero5,\n\tZuiXan,\n\tAlex,\n\tGanjar Sukmara,\n\tdd09david,\n\tvenomx961,\n\tchatzopoulosapostolos,\n\tWanderMax,\n\tIvan Maldonado,\n\tvesper7dd,\n\tSelfDestruct68,\n\tmallachri,\n\tAzhar Dripana,\n\tIlya Kushnir,\n\twasao0807,\n\tAlex NG,\n\tjeroen.wilmink,\n\tpodluka,\n\tAnty0,\n\tkiilerman496,\n\tthex3only,\n\tmustafayasir6,\n\tHavergal Samosir Jr.,\n\timpykid,\n\t28ado1,\n\tElectroz,\n\tvincensius.august,\n\tRuan Carlos,\n\tNurMarvin,\n\tSkullboyq,\n\tYakiaaa,\n\tag2s20150609,\n\tanitamoellerga,\n\tcoolapk_ooops,\n\tpandecheng,\n\tpkjisoo32,\n\tLeam19999,\n\tminecraft172zzz,\n\tTodd Justin York,\n\tGedablo,\n\trootddos511,\n\t楊萬林,\n\tchetraclr,\n\tcub.madrax,\n\tntnhiu,\n\tbuivanxia10,\n\twl02082201,\n\tBacklift,\n\tvonxom,\n\tPeter Cuevas H,\n\talg1.66336611,\n\tHoangthinh99hp,\n\tJackH4L,\n\tspacemaker2906,\n\tva3kama3eredeti,\n\tAustin Andrews,\n\tsaqibpiash,\n\tsuuyout,\n\tsljivics,\n\tDartel,\n\tdoungvichetra2,\n\t0968688218channa,\n\tCaptainTF,\n\tdbinkv1,\n\tronaldmoura,\n\tlafrite.cassee,\n\tJazeks,\n\tNightMareNivek,\n\tprefix309,\n\tmilad19s,\n\tliato.games,\n\tCmP,\n\t1701silentspawn,\n\tgood7773good7773good7773,\n\tATAA SY,\n\tkhiemhocrot.98,\n\ta0193143,\n\tYaroslav,\n\tzam5355,\n\tlonglix,\n\tsssidiki4,\n\tGeorge,\n\t冰鍊.";
                        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.about)).setMessage(Re.s(message)).setNeutralButton(Re.s(R.string.copy), new DialogInterface.OnClickListener() { // from class: android.ext.ConfigListAdapter.6
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog2, int which) {
                                String text3 = "GG: " + ((108.5f * 1) - 12.5f) + " (" + ((int) (((16532.6f * 1) - 539.6f) + 0.5f)) + "); Android: " + Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ") [" + Main.getVSpace() + ']';
                                Tools.copyText(text3, false);
                            }
                        }).setPositiveButton(Re.s(R.string.site), new BaseActivity.GoOnForum(null)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                        break;
                    default:
                        Log.w("Unknown id in config: " + viewId);
                        break;
                }
            }
        }
    }

    static void takeScreenshot() {
        Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.screenshot)).setMessage(Tools.stringFormat(Re.s(R.string.screenshot_prompt), 8)).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).setPositiveButton(Re.s(R.string.ok), new DialogInterface.OnClickListener() { // from class: android.ext.ConfigListAdapter.15
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                MainService service = MainService.instance;
                try {
                    service.dismissDialog();
                    ThreadManager.getHandlerUiThread().postDelayed(new Runnable() { // from class: android.ext.ConfigListAdapter.15.1
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                MainService service2 = MainService.instance;
                                service2.dismissDialog();
                                service2.hotPoint.show();
                                service2.updateNotification(false);
                            } catch (Throwable e) {
                                Log.e("Failed restore after take screenshot", e);
                            }
                        }
                    }, 8000L);
                    service.updateNotification(true);
                    service.hotPoint.hide();
                    ThreadManager.getHandlerUiThread().postDelayed(new Runnable() { // from class: android.ext.ConfigListAdapter.15.2
                        @Override // java.lang.Runnable
                        public void run() {
                            MainService.instance.mDaemonManager.takeScreenshot();
                        }
                    }, 300L);
                } catch (Throwable e) {
                    Log.e("Failed take screenshot", e);
                    try {
                        service.hotPoint.show();
                    } catch (Throwable e2) {
                        Log.e("Failed restore after take screenshot", e2);
                    }
                }
            }
        }));
    }

    static void hideIcon(int w) {
        int which;
        int i = 10;
        if (w == 3) {
            try {
                which = Tools.getSharedPreferences().getInt(HIDE_ICON, 0);
            } catch (Throwable e) {
                Log.e("Failed disable icon " + w, e);
                return;
            }
        } else {
            which = w;
        }
        if ((which == 1 && (BaseActivity.instance instanceof MainActivity)) || (which == 2 && (BaseActivity.instance instanceof ActivityMain))) {
            if (w != 3) {
                Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.hide_icons)).setMessage(Re.s(R.string.cannot_hide_launcher_icon)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                return;
            }
            return;
        }
        String pkg = Tools.getPackageName();
        PackageManager pm = Tools.getPackageManager();
        ComponentName hw = new ComponentName(pkg, ActivityMain.class.getName());
        ComponentName sw = new ComponentName(pkg, MainActivity.class.getName());
        Log.d("hide A: " + which + ListManager.TEXT_SEPARATOR + sw + ": " + pm.getComponentEnabledSetting(sw) + ListManager.TEXT_SEPARATOR + hw + ": " + pm.getComponentEnabledSetting(hw) + ';');
        if (w == 3) {
            int state = (pm.getComponentEnabledSetting(sw) == 2 ? 1 : 0) + (pm.getComponentEnabledSetting(hw) == 2 ? 10 : 0);
            if (which == 1) {
                i = 1;
            } else if (which != 2) {
                i = 0;
            }
            if (state == i) {
                return;
            }
        }
        if (which != 1) {
            Tools.setComponentEnabledSetting(1, sw, 1);
        }
        if (which != 2) {
            Tools.setComponentEnabledSetting(3, hw, 1);
        }
        if (which == 1) {
            Tools.setComponentEnabledSetting(5, sw, 2);
        }
        if (which == 2) {
            Tools.setComponentEnabledSetting(7, hw, 2);
        }
        if (which != 1) {
            Tools.setComponentEnabledSetting(9, sw, 1);
        }
        if (which != 2) {
            Tools.setComponentEnabledSetting(11, hw, 1);
        }
        Log.d("hide Z: " + which + ListManager.TEXT_SEPARATOR + sw + ": " + pm.getComponentEnabledSetting(sw) + ListManager.TEXT_SEPARATOR + hw + ": " + pm.getComponentEnabledSetting(hw) + ';');
        if (w != 3) {
            new SPEditor().putInt(HIDE_ICON, which, 0).commit();
        }
    }

    static void changeToolbarButtons() {
        final String[] stringValues = new String[MainService.menuItems.length * 2];
        stringValues[0] = String.valueOf(Re.s(R.string.search)) + ", " + Re.s(R.string.portrait);
        int i = 0 + 1;
        stringValues[i] = String.valueOf(Re.s(R.string.search)) + ", " + Re.s(R.string.landscape);
        int i2 = i + 1;
        stringValues[i2] = String.valueOf(Re.s(R.string.saved_list)) + ", " + Re.s(R.string.portrait);
        int i3 = i2 + 1;
        stringValues[i3] = String.valueOf(Re.s(R.string.saved_list)) + ", " + Re.s(R.string.landscape);
        int i4 = i3 + 1;
        stringValues[i4] = String.valueOf(Re.s(R.string.memory_editor)) + ", " + Re.s(R.string.portrait);
        int i5 = i4 + 1;
        stringValues[i5] = String.valueOf(Re.s(R.string.memory_editor)) + ", " + Re.s(R.string.landscape);
        int i6 = i5 + 1;
        Alert.show(Alert.create().setItems(stringValues, new DialogInterface.OnClickListener() { // from class: android.ext.ConfigListAdapter.16
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface d, final int which) {
                int tabIndex = which / 2;
                MenuItem[] items = MainService.menuItems[tabIndex];
                CharSequence[] list = new CharSequence[items.length];
                Drawable[] icons = new Drawable[items.length];
                final boolean[] checked = new boolean[list.length];
                long set = Config.toolbarButtons[which];
                for (int i7 = 0; i7 < list.length; i7++) {
                    list[i7] = Build.VERSION.SDK_INT < 11 ? Tools.colorize(items[i7].toString(), -1) : items[i7].toString();
                    checked[i7] = ((1 << i7) & set) != 0;
                    icons[i7] = items[i7].getDrawable();
                }
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { // from class: android.ext.ConfigListAdapter.16.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which2) {
                        if (which2 == -1 || which2 == -3) {
                            long set2 = -1;
                            if (which2 == -1) {
                                for (int i8 = 0; i8 < checked.length; i8++) {
                                    if (checked[i8]) {
                                        set2 |= (long) (1 << i8);
                                    } else {
                                        set2 &= (long) ((1 << i8) ^ (-1));
                                    }
                                }
                            }
                            if ((((long) ((1 << checked.length) - 1)) & set2) == 0) {
                                set2 |= 1;
                            }
                            Config.toolbarButtons[which] = set2;
                            Config.save();
                        }
                        ConfigListAdapter.changeToolbarButtons();
                    }
                };
                AlertDialog dialog = Alert.create(Tools.getDarkContext()).setCustomTitle(Tools.getCustomTitle(stringValues[which])).setMultiChoiceItems(list, checked, new DialogInterface.OnMultiChoiceClickListener() { // from class: android.ext.ConfigListAdapter.16.2
                    @Override // android.content.DialogInterface.OnMultiChoiceClickListener
                    public void onClick(DialogInterface dialog2, int which2, boolean isChecked) {
                        checked[which2] = isChecked;
                    }
                }).setPositiveButton(Re.s(R.string.save), listener).setNeutralButton(Re.s(R.string.reset_button), listener).setNegativeButton(Re.s(R.string.cancel), listener).create();
                ListView listView = dialog.getListView();
                if (listView != null) {
                    Tools.addIconsToListViewItems(listView, list, icons, Config.getIconSize(), R.style.SmallText);
                }
                Alert.show(dialog);
            }
        }).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
    }

    private static void fixHelp() {
        if (Config.vSpace && !helpFixed) {
            for (int i = 0; i < helpConfig.length; i++) {
                if (helpConfig[i] == R.string.help_game_not_listed_root) {
                    helpConfig[i] = R.string.help_game_not_listed_noroot;
                }
            }
            helpFixed = true;
        }
    }

    public static void showHelp() {
        showHelp(0);
    }

    public static void showHelp(int id) {
        fixHelp();
        int open = 0;
        String[] topics = new String[(helpConfig.length / 3) + 1];
        for (int i = 0; i < helpConfig.length; i++) {
            if (i % 3 == 0) {
                int index = (i / 3) + 1;
                if (id == helpConfig[i] || id == helpConfig[i + 1]) {
                    open = index;
                }
                topics[index] = Re.s(helpConfig[i]);
            }
        }
        topics[0] = Re.s(R.string.video_tutorials);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { // from class: android.ext.ConfigListAdapter.17
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    new BaseActivity.GoOnForum(BaseActivity.GoOnForum.S5).onClick(dialog, which);
                    return;
                }
                int which2 = which - 1;
                String msg = String.valueOf(Re.s(ConfigListAdapter.helpConfig[which2 * 3])) + ":\n" + Re.s(ConfigListAdapter.helpConfig[(which2 * 3) + 1]);
                int links = ConfigListAdapter.helpConfig[(which2 * 3) + 2];
                if (links >= 0) {
                    String msg2 = String.valueOf(msg.trim()) + "\n\n";
                    if (msg2.charAt(msg2.length() - 3) != ':') {
                        msg2 = String.valueOf(msg2) + Re.s(R.string.video) + ":\n";
                    }
                    msg = String.valueOf(msg2) + Tools.removeNewLinesChars(ConfigListAdapter.helpVideos[links]);
                }
                if (ConfigListAdapter.helpConfig[which2 * 3] == R.string.help_floating_icon_title) {
                    msg = String.valueOf(msg.replace(Re.s(R.string.s), Re.s(R.string.app_name))) + "\nhttps://productforums.google.com/forum/#!topic/translate/1Pywh5vI1kE\n\n" + Tools.stripColon(R.string.without_float) + ":\n" + Tools.removeNewLinesChars("kwws=22jdphjxdugldq1qhw2y0573");
                }
                View view = LayoutInflater.inflateStatic(R.layout.main_new_version, (ViewGroup) null);
                TextView text = (TextView) view.findViewById(R.id.changelog);
                Tools.setClickableText(text, msg);
                Tools.colorizeHelp(text.getText());
                AlertDialog.Builder builder = Alert.create().setView(view).setNegativeButton(Re.s(R.string.ok), ConfigListAdapter.getShowHelpListener());
                if (ConfigListAdapter.helpConfig[which2 * 3] == R.string.work_without_root_title) {
                    builder.setPositiveButton(R.string.more, new BaseActivity.GoOnForum(BaseActivity.GoOnForum.S10));
                }
                Alert.show(builder);
                if (Config.vSpace && ConfigListAdapter.helpConfig[which2 * 3] == R.string.help_game_not_listed_title) {
                    try {
                        ApplicationInfo ai = Tools.getApplicationInfo(Config.vSpacePkg);
                        if ((ai.flags & 2) == 0) {
                            Alert.show(Alert.create().setMessage(Tools.stringFormat(Re.s(R.string.not_optimized2), Config.vSpaceName, Config.vSpaceName)).setNeutralButton(Re.s(R.string.fix_it), new BaseActivity.GoOnForum(BaseActivity.GoOnForum.S10)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                        }
                    } catch (Throwable e) {
                        Log.w("Failed check vSpace: " + Config.vSpacePkg, e);
                    }
                    byte b = Config.vSpace64;
                    if (b == 0) {
                        b = (ConfigListAdapter.isClient64() && Config.vSpacePkg.contains("64")) ? (byte) 1 : (byte) 2;
                        Config.vSpace64 = b;
                    }
                    if (b == 1) {
                        try {
                            Alert.show(Alert.create().setMessage(Re.s(R.string.vspace64)).setNegativeButton(Re.s(R.string.ok), (DialogInterface.OnClickListener) null));
                        } catch (Throwable e2) {
                            Log.w("Failed check vSpace: " + Config.vSpacePkg, e2);
                        }
                    }
                }
            }
        };
        if (open == 0) {
            Alert.show(Alert.create().setItems(topics, listener).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
        } else {
            listener.onClick(null, open);
        }
    }

    public static DialogInterface.OnClickListener getShowHelpListener() {
        return new DialogInterface.OnClickListener() { // from class: android.ext.ConfigListAdapter.18
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface d, int which) {
                ConfigListAdapter.showHelp();
            }
        };
    }

    public static boolean isClient64() {
        boolean ret = false;
        if (Build.VERSION.SDK_INT < 21) {
            return false;
        }
        try {
            ret = Build.SUPPORTED_64_BIT_ABIS != null && Build.SUPPORTED_64_BIT_ABIS.length > 0;
            InputStream is = new FileInputStream("/proc/self/auxv");
            byte[] buf = new byte[4096];
            int read = is.read(buf);
            is.close();
            if (read > 0) {
                if (read % 16 == 8) {
                    return false;
                }
                for (int i = 0; i < read; i += 16) {
                    if (buf[i + 4] != 0 || buf[i + 5] != 0 || buf[i + 6] != 0 || buf[i + 7] != 0) {
                        return false;
                    }
                }
                return true;
            }
            return ret;
        } catch (Throwable e) {
            Log.w("Failed check 64 client. Used: " + ret, e);
            return ret;
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [android.ext.ConfigListAdapter$19] */
    private void showLogcat() {
        ThreadManager.runOnWorkerThread(new Runnable() { // from class: android.ext.ConfigListAdapter.19
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                Tools.showToast("Logcat: " + Re.s(R.string.loading), 0);
                StringBuilder log = new StringBuilder();
                Calendar calendar = new GregorianCalendar();
                log.append(calendar.getTime().toString());
                log.append('\n');
                log.append(Debug.getLogcat(true));
                Tools.alertBigText(log.toString(), 3);
            }
        });
    }

    public static void changePath(String path) {
        Log.d("mConfigTempPath onClick");
        View pathView = LayoutInflater.inflateStatic(R.layout.temp_path_config, (ViewGroup) null);
        final EditTextPath pathEdit = (EditTextPath) pathView.findViewById(R.id.temp_file_path);
        SharedPreferences storage = Tools.getSharedPreferences();
        if (path == null) {
            path = storage.getString(MainService.TEMP_PATH_KEY, "");
        }
        pathEdit.setText(path);
        pathEdit.setDataType(4);
        pathEdit.setPathType(0);
        ((TextView) pathView.findViewById(R.id.temp_file_desc)).setText(String.valueOf(Re.s(R.string.path_for_temp_files)) + '\n' + Re.s(R.string.path_must_be));
        pathView.findViewById(R.id.path_selector).setTag(pathEdit);
        final AlertDialog dialog = Alert.create().setView(InternalKeyboard.getView(pathView, false)).setPositiveButton(Re.s(R.string.confirm), (DialogInterface.OnClickListener) null).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).create();
        View.OnClickListener listener = new View.OnClickListener() { // from class: android.ext.ConfigListAdapter.20
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                String path2 = Tools.trimDirPath(pathEdit.getText().toString().trim());
                if (path2.length() > 0) {
                    if (Tools.isPath(path2)) {
                        History.add(path2, 4);
                    } else {
                        return;
                    }
                }
                Log.d("save path: " + path2);
                new SPEditor().putString(MainService.TEMP_PATH_KEY, path2, "").apply();
                MainService service = MainService.instance;
                service.mDaemonManager.setPath(service.getTempPatches());
                service.clear(true);
                Tools.dismiss(dialog);
            }
        };
        TypicalValues.setCacheDirs((Button) pathView.findViewById(R.id.typical_values), pathEdit);
        showChange(dialog, listener, pathEdit);
    }

    private static void showChange(final AlertDialog dialog, final View.OnClickListener listener, android.widget.EditText pathEdit) {
        dialog.setButton(-1, Re.s(R.string.confirm), new DialogInterface.OnClickListener() { // from class: android.ext.ConfigListAdapter.21
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface d, int which) {
                listener.onClick(dialog.getButton(-1));
            }
        });
        Alert.setOnShowListener(dialog, new DialogInterface.OnShowListener() { // from class: android.ext.ConfigListAdapter.22
            @Override // android.content.DialogInterface.OnShowListener
            public void onShow(DialogInterface d) {
                Tools.setButtonListener(d, -1, null, listener);
            }
        });
        Alert.show(dialog, pathEdit);
    }

    public static void changeSu() {
        if (Main.isRootMode()) {
            View pathView = LayoutInflater.inflateStatic(R.layout.temp_path_config, (ViewGroup) null);
            EditTextPath pathEdit = (EditTextPath) pathView.findViewById(R.id.temp_file_path);
            SharedPreferences storage = Tools.getSharedPreferences();
            String path = storage.getString(RootDetector.SU_KEY, "");
            pathEdit.setText(path);
            pathEdit.setDataType(4);
            pathEdit.setPathType(1);
            pathEdit.setHint(RootDetector.SU_KEY);
            ((TextView) pathView.findViewById(R.id.temp_file_desc)).setText(Re.s(R.string.su_command));
            CheckBox useSh = (CheckBox) pathView.findViewById(R.id.use_sh);
            useSh.setChecked(storage.getBoolean(RootDetector.SH_KEY, false));
            useSh.setVisibility(0);
            pathView.findViewById(R.id.path_selector).setTag(pathEdit);
            AlertDialog dialog = Alert.create().setView(InternalKeyboard.getView(pathView, false)).setPositiveButton(Re.s(R.string.confirm), (DialogInterface.OnClickListener) null).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).create();
            View.OnClickListener listener = new AnonymousClass23(pathEdit, useSh, dialog);
            Button btn = (Button) pathView.findViewById(R.id.typical_values);
            String[] typical = {RootDetector.SU_KEY, "ku.sud", "/system/bin/su", "/system/xbin/su", "/su/bin/su", "/system/xbin/bstk/su", "/sbin/su", "/magisk/.core/bin/su", "/magisk/phh/bin/su"};
            btn.setOnClickListener(new TypicalValues(typical, typical));
            btn.setTag(pathEdit);
            showChange(dialog, listener, pathEdit);
        }
    }

    /* JADX INFO: renamed from: android.ext.ConfigListAdapter$23, reason: invalid class name */
    static class AnonymousClass23 implements View.OnClickListener {
        private final /* synthetic */ AlertDialog val$dialog;
        private final /* synthetic */ EditTextPath val$pathEdit;
        private final /* synthetic */ CheckBox val$useSh;

        AnonymousClass23(EditTextPath editTextPath, CheckBox checkBox, AlertDialog alertDialog) {
            this.val$pathEdit = editTextPath;
            this.val$useSh = checkBox;
            this.val$dialog = alertDialog;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            final String su = this.val$pathEdit.getText().toString().trim();
            if (su.length() > 0) {
                final CheckBox checkBox = this.val$useSh;
                new ExceptionHandler.ThreadEx(new Runnable() { // from class: android.ext.ConfigListAdapter.23.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AnonymousClass23.this.hide();
                        try {
                            String out = RootDetector.runCmd("exec id", 10, su);
                            if (out.contains("uid=0")) {
                                final String str = su;
                                final CheckBox checkBox2 = checkBox;
                                ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ConfigListAdapter.23.1.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        AnonymousClass23.this.save(str, checkBox2.isChecked());
                                    }
                                });
                            } else {
                                AnonymousClass23.this.alert(out);
                            }
                        } catch (Throwable e) {
                            AnonymousClass23.this.alert(e.getMessage());
                        }
                        RootDetector.debug = "";
                    }
                }, "CheckSu: " + su).start();
            } else {
                save(su, this.val$useSh.isChecked());
            }
        }

        void hide() {
            final AlertDialog alertDialog = this.val$dialog;
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ConfigListAdapter.23.2
                @Override // java.lang.Runnable
                public void run() {
                    if (MainService.instance != null) {
                        MainService.instance.dismissDialog();
                    }
                    alertDialog.hide();
                }
            });
        }

        void alert(String msg) {
            final AlertDialog alertDialog = this.val$dialog;
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ConfigListAdapter.23.3
                @Override // java.lang.Runnable
                public void run() {
                    alertDialog.show();
                }
            });
            Tools.alertBigText(String.valueOf(msg) + "\n\n" + RootDetector.debug, 0);
        }

        void save(String su, boolean sh) {
            Log.d("su path: " + su + "; sh: " + sh);
            new SPEditor().putString(RootDetector.SU_KEY, su, "").putBoolean(RootDetector.SH_KEY, sh, false).apply();
            if (su.length() != 0) {
                History.add(su, 4);
            }
            Tools.dismiss(this.val$dialog);
            ConfigListAdapter.needRestart();
        }
    }

    public static void setLocale(Configuration config, Locale locale) {
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                config.setLocale(locale);
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
            try {
                config.setLayoutDirection(locale);
            } catch (Throwable e2) {
                Log.badImplementation(e2);
            }
        }
        try {
            config.locale = locale;
        } catch (Throwable e3) {
            Log.e("Failed set locale", e3);
        }
    }

    private static String getLangCode(Configuration c, Locale locale, AssetManager assets, DisplayMetrics metrics) {
        Configuration config = new Configuration(c);
        setLocale(config, locale);
        Resources res = null;
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                res = MainService.context.createConfigurationContext(config).getResources();
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
        if (res == null) {
            res = new Resources(assets, metrics, config);
        }
        String fmt = "";
        try {
            fmt = String.format(locale, "%,d;%,d;%e;%e;%e;%e;", 1234567890, -1234567890, Double.valueOf(1.23456E300d), Double.valueOf(-1.23456E300d), Double.valueOf(1.23456E-300d), Double.valueOf(-1.23456E-300d));
        } catch (Exception e2) {
            Log.badImplementation(e2);
        }
        return String.valueOf(res.getString(R.string.lang_code)) + fmt;
    }

    static void configLanguage() {
        Tools.showToast(R.string.loading, 0);
        ThreadManager.runOnMainThread(new Runnable() { // from class: android.ext.ConfigListAdapter.24
            @Override // java.lang.Runnable
            public void run() {
                ConfigListAdapter.configLanguage_();
            }
        });
    }

    static void configLanguage_() {
        MainService service = MainService.instance;
        if (service != null) {
            final List<String> locales = new ArrayList<>();
            List<CharSequence> localesList = new ArrayList<>();
            Map<String, String> index = new HashMap<>();
            DisplayMetrics metrics = new DisplayMetrics();
            service.mWindowManager.getDefaultDisplay().getMetrics(metrics);
            Resources r = MainService.context.getResources();
            Configuration c = r.getConfiguration();
            AssetManager assets = r.getAssets();
            String[] list = AppLocale.getLocales();
            Arrays.sort(list, new Comparator<String>() { // from class: android.ext.ConfigListAdapter.25
                @Override // java.util.Comparator
                public int compare(String lhs, String rhs) {
                    return (String.valueOf(lhs) + "_zzz").replace('-', '_').compareTo((String.valueOf(rhs) + "_zzz").replace('-', '_'));
                }
            });
            String curCode = Re.s(R.string.lang_code);
            int currentLocale = -1;
            long last = System.currentTimeMillis();
            int i = 0;
            for (String localeName : list) {
                i++;
                if (localeName != null) {
                    try {
                        long now = System.currentTimeMillis();
                        if (now - last > 2000) {
                            last = now;
                            Tools.showToast(String.valueOf((i * 100) / list.length) + "%", 0);
                        }
                        if (localeName.length() == 0) {
                            localeName = "en_US";
                            if (Build.VERSION.SDK_INT >= 21) {
                                "en_US".replace('_', '-');
                            }
                        }
                        Locale locale = AppLocale.getLocaleObject(localeName);
                        String code = getLangCode(c, locale, assets, metrics);
                        if (index.get(code) == null) {
                            if (code.startsWith(curCode) && currentLocale == -1) {
                                currentLocale = locales.size();
                            }
                            String num = "";
                            try {
                                num = String.format(locale, "\n[%,.1f | %.2e]", Double.valueOf(-1234.0d), Double.valueOf(-4.56E-78d));
                            } catch (Throwable e) {
                                Log.badImplementation(e);
                            }
                            index.put(code, localeName);
                            locales.add(localeName);
                            try {
                                SpannableStringBuilder builder = new SpannableStringBuilder();
                                builder.append((CharSequence) locale.getDisplayName(locale));
                                builder.setSpan(new ForegroundColorSpan(ProcessList.ProcessInfo.colorName), 0, builder.length(), 33);
                                int len = builder.length();
                                builder.append((CharSequence) " (");
                                builder.append((CharSequence) localeName);
                                builder.append(')');
                                builder.setSpan(new ForegroundColorSpan(ProcessList.ProcessInfo.colorPid), len, builder.length(), 33);
                                int len2 = builder.length();
                                builder.append((CharSequence) num);
                                builder.setSpan(new RelativeSizeSpan(0.6f), len2, builder.length(), 33);
                                builder.setSpan(new ForegroundColorSpan(ProcessList.ProcessInfo.colorSize), len2, builder.length(), 33);
                                localesList.add(builder);
                            } catch (Throwable e2) {
                                Log.badImplementation(e2);
                                localesList.add(String.valueOf(locale.getDisplayName(locale)) + " (" + localeName + ')' + num);
                            }
                        }
                    } catch (Throwable e3) {
                        Log.e("Failed check locale: " + localeName, e3);
                    }
                }
            }
            getLangCode(c, AppLocale.getLocaleObject(AppLocale.getLocale()), assets, metrics);
            AppLocale.loadLocale();
            final ListAdapter adapter = new ArrayAdapter(MainService.context, localesList);
            final int curLocale = currentLocale;
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.ConfigListAdapter.26
                @Override // java.lang.Runnable
                public void run() {
                    AlertDialog.Builder customTitle = Alert.create(Tools.getDarkContext()).setCustomTitle(Tools.getCustomTitle(R.string.select_language));
                    ListAdapter listAdapter = adapter;
                    int i2 = curLocale;
                    final List list2 = locales;
                    Alert.show(customTitle.setSingleChoiceItems(listAdapter, i2, new DialogInterface.OnClickListener() { // from class: android.ext.ConfigListAdapter.26.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                AppLocale.saveLocale((String) list2.get(which));
                                ConfigListAdapter.needRestart();
                            } catch (IndexOutOfBoundsException e4) {
                                Log.e("Failed get locale for index: " + which, e4);
                            }
                            Tools.dismiss(dialog);
                        }
                    }));
                }
            });
        }
    }
}
