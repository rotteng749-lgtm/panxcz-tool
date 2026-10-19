package android.ext;

import android.content.Context;
import android.content.SharedPreferences;
import android.fix.ContextWrapper;
import android.os.Build;
import android.sup.ContainerHelpers;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* JADX INFO: loaded from: classes.dex */
public class AppLocale {
    private static final Pattern p;
    private static String LOCALE = "locale";
    private static volatile String lastLocale = null;
    private static String CRASHED_VM = "crashed-vm";
    private static String CRASHED_VM2 = "crashed-vm2";
    private static volatile Locale numberLocale = null;
    private static List<Class> updaters = new ArrayList();

    static {
        Pattern pt = null;
        try {
            pt = Pattern.compile("~~~([-_A-Za-z0-9]{1,10})~~~");
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
        p = pt;
    }

    public static Locale getNumberLocale() {
        Locale locale = numberLocale;
        if (locale == null) {
            Locale locale2 = getLocaleObject();
            numberLocale = locale2;
            return locale2;
        }
        return locale;
    }

    public static void setNumberLocale(Locale locale) {
        numberLocale = locale;
    }

    public static Locale getLocaleObject() {
        return getLocaleObject(getLocale());
    }

    public static Locale getLocaleObject(String strLocale) {
        String[] parts = strLocale.split("[-_]");
        if (parts.length > 1) {
            Locale locale = new Locale(parts[0], parts[1]);
            return locale;
        }
        Locale locale2 = new Locale(strLocale);
        return locale2;
    }

    public static void changeLocale(String newLocale) {
        Locale locale = getLocaleObject(newLocale);
        Locale.setDefault(locale);
        ContextWrapper.setOverride(null, locale);
        update(BaseActivity.context);
        update(BaseActivity.appContext);
        update(MainService.context);
        Log.d("Loaded locale: " + newLocale);
        updateLocale();
        if (!newLocale.equals(lastLocale)) {
            lastLocale = newLocale;
            Re.clearCache();
        }
    }

    private static void update(Context context) {
        if (context != null) {
            context.getResources();
        }
    }

    public static void registerClass(Class cl) {
        updaters.add(cl);
    }

    private static void updateLocale() {
        for (Class cl : updaters) {
            try {
                Method m = cl.getMethod("updateLocale", new Class[0]);
                m.invoke(null, new Object[0]);
            } catch (Throwable e) {
                Log.w("Failed call updateLocale for " + cl.getName(), e);
            }
        }
    }

    public static String getLocale() {
        try {
            String locale = Tools.getSharedPreferences().getString(LOCALE, Locale.getDefault().toString());
            return locale;
        } catch (Throwable e) {
            Log.e("Failed get locale", e);
            return "";
        }
    }

    public static void loadLocale() {
        try {
            changeLocale(getLocale());
        } catch (Throwable e) {
            Log.e("Failed load locale", e);
        }
    }

    public static void saveLocale(String locale) {
        new SPEditor().putString(LOCALE, locale).commit();
        changeLocale(locale);
    }

    public static String[] getLocales() {
        String[] list = ContainerHelpers.EMPTY_STRINGS;
        SharedPreferences sp = Tools.getSharedPreferences();
        if (!sp.getBoolean(CRASHED_VM, false)) {
            sp.edit().putBoolean(CRASHED_VM, true).commit();
            list = Tools.getContext().getAssets().getLocales();
            sp.edit().remove(CRASHED_VM).commit();
        }
        List<String> locales = new ArrayList<>();
        locales.add("");
        for (String item : list) {
            if (item != null && !locales.contains(item)) {
                locales.add(item);
            }
        }
        Locale[] locs = null;
        if (!sp.getBoolean(CRASHED_VM2, false)) {
            sp.edit().putBoolean(CRASHED_VM2, true).commit();
            locs = Locale.getAvailableLocales();
            sp.edit().remove(CRASHED_VM2).commit();
        }
        if (locs != null) {
            for (Locale loc : locs) {
                if (loc != null) {
                    String country = loc.getCountry();
                    String lang = loc.getLanguage();
                    String item1 = (country == null || country.length() == 0) ? lang : String.valueOf(lang) + '_' + country;
                    String item2 = (country == null || country.length() == 0) ? lang : String.valueOf(lang) + '-' + country;
                    if (!locales.contains(item1) && !locales.contains(item2)) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            item1 = item2;
                        }
                        locales.add(item1);
                    }
                }
            }
        }
        if (p != null) {
            try {
                ZipFile apk = new ZipFile(Tools.getContext().getPackageResourcePath());
                ZipEntry res = apk.getEntry("resources.arsc");
                InputStream is = apk.getInputStream(res);
                byte[] buffer = new byte[1024];
                String prefix = "";
                while (true) {
                    int count = is.read(buffer);
                    if (count == -1) {
                        break;
                    }
                    String chars = new String(buffer, 0, count, Charset.forName("US-ASCII"));
                    if (chars.indexOf(126) != -1) {
                        Matcher m = p.matcher(String.valueOf(prefix) + chars);
                        while (m.find()) {
                            String item12 = m.group(1);
                            String item22 = item12.replace('_', '-');
                            if (!locales.contains(item12) && !locales.contains(item22)) {
                                if (Build.VERSION.SDK_INT >= 21) {
                                    item12 = item22;
                                }
                                locales.add(item12);
                            }
                        }
                    }
                    prefix = chars.substring(0, Math.min(40, chars.length()));
                }
                is.close();
                apk.close();
            } catch (Throwable e) {
                Log.e("Failed load locales from apk", e);
            }
        }
        return (String[]) locales.toArray(new String[locales.size()]);
    }
}
