package android.ext;

import android.content.res.Resources;
import android.fix.SparseArray;
import android.os.Handler;
import android.sup.ContainerHelpers;
import catch_.me_.if_.you_.can_.R;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import luaj.LuaThread;

/* JADX INFO: loaded from: classes.dex */
public class Re {
    private static volatile WeakReference<Resources> weakRes = new WeakReference<>(null);
    static volatile SparseArray<String> cache = new SparseArray<>();
    static int[] fix = ContainerHelpers.EMPTY_INTS;
    static volatile long lastUse = 0;
    private static final Runnable clear = new Runnable() { // from class: android.ext.Re.1
        @Override // java.lang.Runnable
        public void run() {
            long lastUse2 = Re.lastUse;
            if (lastUse2 != 0 && System.currentTimeMillis() - lastUse2 > LuaThread.thread_orphan_check_interval) {
                Re.clearCache();
            }
            Handler handler = ThreadManager.getHandlerUiThread();
            try {
                handler.removeCallbacks(this);
            } catch (NullPointerException e) {
                Log.badImplementation(e);
            }
            handler.postDelayed(this, 1000L);
        }
    };
    private static Pattern p = Pattern.compile("__([a-z0-9_]+?)__");

    public static void clearCache() {
        SparseArray<String> old = cache;
        if (old.size() > fix.length) {
            SparseArray<String> cache2 = new SparseArray<>();
            for (int i : fix) {
                String val = old.syncGet(i);
                if (val != null) {
                    cache2.put(i, val);
                }
            }
            cache = cache2;
            lastUse = 0L;
        }
    }

    private static Resources getRes() {
        Resources ret = weakRes.get();
        if (ret == null) {
            ret = Tools.getContext().getResources();
            weakRes = new WeakReference<>(ret);
            clear.run();
            SparseArray<String> cache2 = new SparseArray<>();
            ArrayList<Integer> fix2 = fix.length == 0 ? new ArrayList<>() : null;
            int r = i(Tools.removeNewLinesChars("dssbqdph"), R.string.class);
            String t = Tools.removeNewLinesChars("JdphJxdugldq").intern();
            if (fix2 != null) {
                fix2.add(Integer.valueOf(r));
            }
            cache2.put(r, t);
            try {
                String.format(AppLocale.getNumberLocale(), s_(R.string.d), Integer.valueOf(DaemonManager.TIME_JUMP_MUL_INT));
            } catch (Exception e) {
                Log.e("Check format", e);
                if (fix2 != null) {
                    fix2.add(Integer.valueOf(R.string.d));
                }
                cache2.put(R.string.d, "%d");
            }
            String forum = Tools.removeNewLinesChars("kwws=22jdphjxdugldq1qhw2iruxp");
            if (fix2 != null) {
                fix2.add(Integer.valueOf(R.string.forum));
            }
            cache2.put(R.string.forum, forum);
            if (fix2 != null) {
                fix2.add(Integer.valueOf(R.string.decimal));
            }
            cache2.put(R.string.decimal, String.valueOf(ParserNumbers.decimalSeparator));
            if (fix2 != null) {
                fix2.add(Integer.valueOf(R.string.thousands));
            }
            cache2.put(R.string.thousands, ParserNumbers.thousandSeparator);
            if (fix2 != null) {
                fix = Tools.intArray(fix2);
            }
            cache = cache2;
        }
        return ret;
    }

    private static String replaceIncludes(String input) {
        if (input == null) {
            input = "null";
        }
        String out = input;
        if (out.indexOf(95) >= 0) {
            try {
                Matcher m = p.matcher(input);
                while (m.find()) {
                    try {
                        String name = m.group(1);
                        int resId = i(name, R.string.class);
                        if (resId != 0) {
                            String full = m.group(0);
                            out = out.replace(full, s_(resId));
                        }
                    } catch (Throwable e) {
                        Log.e("Exception for: " + input, e);
                    }
                }
            } catch (Throwable e2) {
                Log.e("Exception on: " + input, e2);
            }
        }
        return out;
    }

    public static int i(String name, Class<?> cl) {
        try {
            Field f = cl.getDeclaredField(name);
            Object v = f.get(null);
            if (v instanceof Integer) {
                return ((Integer) v).intValue();
            }
        } catch (Throwable e) {
            Log.w("Resource id for name '" + name + "' not found", e);
        }
        return 0;
    }

    public static String s(int id) {
        return s_(id);
    }

    private static String s_(int id) {
        String out;
        if (id == 0 || id == -1) {
            return "(Invalid resource id " + id + ")";
        }
        SparseArray<String> cache2 = cache;
        Object o = cache2.syncGet(id);
        String replace = o == null ? null : o.toString();
        if (replace == null) {
            try {
                out = getRes().getString(id);
            } catch (Throwable e) {
                out = "Resource for id 0x" + ToolsLight.prefixLongHex(8, id) + " not found";
                Log.e(out, e);
            }
            String replace2 = replaceIncludes(out);
            try {
                cache2.syncPut(id, replace2);
            } catch (Throwable e2) {
                Log.badImplementation(e2);
            }
            lastUse = System.currentTimeMillis();
            return replace2;
        }
        return replace;
    }

    public static String s(String s) {
        return replaceIncludes(s);
    }

    public static String getName(int id) {
        try {
            return getRes().getResourceEntryName(id);
        } catch (Throwable e) {
            String out = "Resource name for id 0x" + ToolsLight.prefixLongHex(8, id) + " not found";
            Log.e(out, e);
            return out;
        }
    }
}
