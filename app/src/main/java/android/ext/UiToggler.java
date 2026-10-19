package android.ext;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class UiToggler extends BroadcastReceiver {
    private static final String ACTION = "action";
    public static final int ACTION_SCREENSHOT = 1;
    public static final int ACTION_TOGGLE_UI = 0;
    private static final String HASH = "check-hash";
    private static final int hash = Tools.RANDOM.nextInt();

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        try {
            Log.w("onReceive broadcast: " + intent + ListManager.TEXT_SEPARATOR + context + ListManager.TEXT_SEPARATOR + MainService.instance);
            int res = 0;
            if (MainService.instance == null) {
                res = 1;
            } else if (intent == null) {
                res = 2;
            } else if (intent.getIntExtra(HASH, 0) != hash) {
                res = 3;
            }
            Log.w("Broadcast: " + res);
            if (res == 0) {
                try {
                    context.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
                } catch (Throwable e) {
                    Log.w("Failed hide status bar", e);
                }
                switch (intent.getIntExtra(ACTION, 0)) {
                    case 1:
                        ConfigListAdapter.takeScreenshot();
                        break;
                    default:
                        MainService.instance.toggleDialog();
                        break;
                }
            }
        } catch (Throwable e2) {
            Log.w("onReceive broadcast fail: " + intent, e2);
        }
    }

    private static Intent getIntent() {
        String pkg = Tools.getPackageName();
        Intent intent = new Intent();
        intent.setClassName(pkg, String.valueOf(pkg) + ".Receiver");
        intent.setAction(pkg);
        intent.setFlags((intent.getFlags() | 32 | AddressItem.FLAG_REVERT) & (-8388625));
        intent.putExtra(HASH, hash);
        return intent;
    }

    public static PendingIntent getPendingIntent(int action) {
        try {
            int flags = 134217728;
            if (Build.VERSION.SDK_INT >= 23) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
            PendingIntent ret = PendingIntent.getBroadcast(Tools.getContext(), Tools.RANDOM.nextInt(), getIntent().putExtra(ACTION, action), flags);
            return ret;
        } catch (Throwable e) {
            Log.badImplementation(e);
            return null;
        }
    }
}
