package android.ext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.fix.ContextWrapper;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class ServiceContext extends ContextWrapper {
    public ServiceContext(Context base) {
        super(base);
    }

    public static Context wrap(Context context) {
        if (context != null && !(context instanceof ServiceContext)) {
            return new ServiceContext(context);
        }
        return context;
    }

    @Override // android.fix.ContextWrapper, android.content.ContextWrapper, android.content.Context
    public void startActivity(Intent intent) {
        if (intent != null) {
            intent.setFlags(AddressItem.FLAG_VALUE_NOT_EQUAL);
        }
        super.startActivity(intent);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivity(Intent intent, Bundle options) {
        if (intent != null) {
            intent.setFlags(AddressItem.FLAG_VALUE_NOT_EQUAL);
        }
        super.startActivity(intent, options);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivities(Intent[] intents) {
        if (intents != null) {
            for (Intent intent : intents) {
                if (intent != null) {
                    intent.setFlags(AddressItem.FLAG_VALUE_NOT_EQUAL);
                }
            }
        }
        super.startActivities(intents);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivities(Intent[] intents, Bundle options) {
        if (intents != null) {
            for (Intent intent : intents) {
                if (intent != null) {
                    intent.setFlags(AddressItem.FLAG_VALUE_NOT_EQUAL);
                }
            }
        }
        super.startActivities(intents, options);
    }

    @Override // android.fix.ContextWrapper, android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public Object getSystemService(String name) {
        try {
            Object ret = super.getSystemService(name);
            return ret;
        } catch (Throwable e) {
            Log.e("getSystemService fail: " + name, e);
            return null;
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Context getApplicationContext() {
        Context context = super.getApplicationContext();
        if (context == null) {
            return BaseActivity.appContext;
        }
        return context;
    }

    @Override // android.fix.ContextWrapper, android.content.ContextWrapper, android.content.Context
    public void unregisterReceiver(BroadcastReceiver receiver) {
        try {
            super.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            Log.badImplementation(e);
        }
    }
}
