package android.ext;

import android.content.SharedPreferences;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/* JADX INFO: loaded from: classes.dex */
public final class FloatCheck {
    private static final String FLOAT_OFF = "float-off";
    private static final boolean FLOAT_OFF_DEFAULT = false;

    public FloatCheck(boolean async) {
        if (async) {
            ThreadManager.runOnWriteThread(new Runnable() { // from class: android.ext.FloatCheck.1
                @Override // java.lang.Runnable
                public void run() {
                    FloatCheck.this.check();
                }
            });
        } else {
            check();
        }
    }

    public static void setPref(boolean value) {
        new SPEditor().putBoolean(FLOAT_OFF, value, false).commit();
    }

    void check() {
        SharedPreferences pref = Tools.getSharedPreferences();
        if (pref.contains(FLOAT_OFF)) {
            if (pref.getBoolean(FLOAT_OFF, false)) {
                floatOff("pref");
                return;
            }
            return;
        }
        remix();
    }

    private void remix() {
        try {
            Process process = Tools.exec(new String[]{"getprop"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                if (line.contains("ro.build.remixos.version")) {
                    Log.d("Remix-detect: " + line);
                    if (line.contains("[2.")) {
                        floatOff(line);
                    }
                }
            }
            in.close();
        } catch (Throwable e) {
            Log.e("Remix-detect", e);
        }
    }

    private void floatOff(String reason) {
        Log.d("FloatCheck: float off by " + reason);
        SystemConstants.useFloatWindows = false;
    }
}
