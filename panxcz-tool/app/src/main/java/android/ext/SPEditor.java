package android.ext;

import android.content.SharedPreferences;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class SPEditor implements SharedPreferences.Editor {
    final SharedPreferences.Editor base;

    public SPEditor() {
        this.base = Tools.getSharedPreferences().edit();
    }

    public SPEditor(SharedPreferences.Editor base) {
        this.base = base;
    }

    public SPEditor putString(String key, String value, String def) {
        if (value.equals(def)) {
            this.base.remove(key);
        } else {
            this.base.putString(key, value);
        }
        return this;
    }

    @Override // android.content.SharedPreferences.Editor
    public SPEditor putString(String key, String value) {
        this.base.putString(key, value);
        return this;
    }

    @Override // android.content.SharedPreferences.Editor
    public SPEditor putStringSet(String key, Set<String> values) {
        this.base.putStringSet(key, values);
        return this;
    }

    public SPEditor putInt(String key, int value, int def) {
        if (value == def) {
            this.base.remove(key);
        } else {
            this.base.putInt(key, value);
        }
        return this;
    }

    @Override // android.content.SharedPreferences.Editor
    public SPEditor putInt(String key, int value) {
        this.base.putInt(key, value);
        return this;
    }

    public SPEditor putLong(String key, long value, long def) {
        if (value == def) {
            this.base.remove(key);
        } else {
            this.base.putLong(key, value);
        }
        return this;
    }

    @Override // android.content.SharedPreferences.Editor
    public SPEditor putLong(String key, long value) {
        this.base.putLong(key, value);
        return this;
    }

    public SPEditor putFloat(String key, float value, float def) {
        if (value == def) {
            this.base.remove(key);
        } else {
            this.base.putFloat(key, value);
        }
        return this;
    }

    @Override // android.content.SharedPreferences.Editor
    public SPEditor putFloat(String key, float value) {
        this.base.putFloat(key, value);
        return this;
    }

    public SPEditor putBoolean(String key, boolean value, boolean def) {
        if (value == def) {
            this.base.remove(key);
        } else {
            this.base.putBoolean(key, value);
        }
        return this;
    }

    @Override // android.content.SharedPreferences.Editor
    public SPEditor putBoolean(String key, boolean value) {
        this.base.putBoolean(key, value);
        return this;
    }

    @Override // android.content.SharedPreferences.Editor
    public SPEditor remove(String key) {
        this.base.remove(key);
        return this;
    }

    @Override // android.content.SharedPreferences.Editor
    public SPEditor clear() {
        this.base.clear();
        return this;
    }

    @Override // android.content.SharedPreferences.Editor
    public boolean commit() {
        return this.base.commit();
    }

    @Override // android.content.SharedPreferences.Editor
    public void apply() {
        this.base.apply();
    }
}
