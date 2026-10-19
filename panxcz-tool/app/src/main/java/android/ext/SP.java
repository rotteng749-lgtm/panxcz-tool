package android.ext;

import android.content.SharedPreferences;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class SP implements SharedPreferences {
    final SharedPreferences base;

    public static SharedPreferences wrap(SharedPreferences sp) {
        if (!(sp instanceof SP)) {
            return new SP(sp);
        }
        return sp;
    }

    private SP(SharedPreferences base) {
        this.base = base;
    }

    @Override // android.content.SharedPreferences
    public Map<String, ?> getAll() {
        return this.base.getAll();
    }

    @Override // android.content.SharedPreferences
    public String getString(String key, String defValue) {
        try {
            return this.base.getString(key, defValue);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return defValue;
        }
    }

    @Override // android.content.SharedPreferences
    public Set<String> getStringSet(String key, Set<String> defValues) {
        try {
            return this.base.getStringSet(key, defValues);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return defValues;
        }
    }

    @Override // android.content.SharedPreferences
    public int getInt(String key, int defValue) {
        try {
            return this.base.getInt(key, defValue);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return defValue;
        }
    }

    @Override // android.content.SharedPreferences
    public long getLong(String key, long defValue) {
        try {
            return this.base.getLong(key, defValue);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return defValue;
        }
    }

    @Override // android.content.SharedPreferences
    public float getFloat(String key, float defValue) {
        try {
            return this.base.getFloat(key, defValue);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return defValue;
        }
    }

    @Override // android.content.SharedPreferences
    public boolean getBoolean(String key, boolean defValue) {
        try {
            return this.base.getBoolean(key, defValue);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return defValue;
        }
    }

    @Override // android.content.SharedPreferences
    public boolean contains(String key) {
        try {
            return this.base.contains(key);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.content.SharedPreferences
    public SharedPreferences.Editor edit() {
        return this.base.edit();
    }

    @Override // android.content.SharedPreferences
    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        try {
            this.base.registerOnSharedPreferenceChangeListener(listener);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.content.SharedPreferences
    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        try {
            this.base.unregisterOnSharedPreferenceChangeListener(listener);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }
}
