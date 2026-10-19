package android.ext;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import catch_.me_.if_.you_.can_.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import luaj.LuaThread;

/* JADX INFO: loaded from: classes.dex */
public class Alert {
    static final Runnable shower = new Runnable() { // from class: android.ext.Alert.1
        @Override // java.lang.Runnable
        public void run() {
            Alert.showHint();
        }
    };
    private static volatile int counter = 0;
    private static final List<DialogShowHolder> dialogShowHolder = new ArrayList();
    private static final List<DialogDismissHolder> dialogDismissHolder = new ArrayList();
    private static ArrayList<WeakReference<AlertDialog>> dialogs = new ArrayList<>();

    private static boolean inInstallMode() {
        return MainService.instance == null;
    }

    @TargetApi(11)
    private static void fixDialog(AlertDialog dialog) {
        Window window = dialog.getWindow();
        if (Build.VERSION.SDK_INT >= 11) {
            window.setCallback(new WindowCallbackWrapper(window.getCallback()));
        }
        if (inInstallMode()) {
            window.clearFlags(AddressItem.FLAG_REVERT);
            return;
        }
        window.setType(SystemConstants.getTypePhone());
        Tools.fixViewParams(window);
        if ((Config.configClient & 2) != 0) {
            window.addFlags(AddressItem.FLAG_REVERT);
        } else {
            window.clearFlags(AddressItem.FLAG_REVERT);
        }
    }

    private static void fixListView(AlertDialog dialog) {
        ListView list = null;
        try {
            list = dialog.getListView();
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
        if (list != null) {
            try {
                AdapterView.OnItemClickListener old = list.getOnItemClickListener();
                if (old != null && !(old instanceof OnItemClickListenerWrapper)) {
                    list.setOnItemClickListener(new OnItemClickListenerWrapper(dialog, old, null));
                }
            } catch (Throwable e2) {
                Log.badImplementation(e2);
            }
        }
    }

    private static class OnItemClickListenerWrapper implements AdapterView.OnItemClickListener {
        final AlertDialog dialog;
        final AdapterView.OnItemClickListener old;

        private OnItemClickListenerWrapper(AlertDialog dialog, AdapterView.OnItemClickListener base) {
            this.old = base;
            this.dialog = dialog;
        }

        /* synthetic */ OnItemClickListenerWrapper(AlertDialog alertDialog, AdapterView.OnItemClickListener onItemClickListener, OnItemClickListenerWrapper onItemClickListenerWrapper) {
            this(alertDialog, onItemClickListener);
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                this.old.onItemClick(parent, view, position, id);
            } catch (Throwable e) {
                Log.badImplementation(e);
                Tools.dismiss(this.dialog);
            }
        }
    }

    static void showAndFix(AlertDialog dialog) {
        int i;
        setOnShowListener(dialog, null);
        dialog.show();
        if (!inInstallMode()) {
            fixListView(dialog);
            Window window = dialog.getWindow();
            View decorView = window.getDecorView();
            InternalKeyboard kbdView = (InternalKeyboard) decorView.findViewById(R.id.keyboard);
            if (kbdView != null) {
                kbdView.load(window);
            }
            if (Tools.hasEditText(decorView) && (kbdView == null || (Config.configClient & 1) == 0)) {
                i = 5;
            } else {
                i = 2;
            }
            window.setSoftInputMode(i | 16);
            Tools.fixViews(decorView);
            ShowApp.register(window);
            Handler handler = ThreadManager.getHandlerUiThread();
            handler.removeCallbacks(shower);
            handler.postDelayed(shower, 100L);
            if (counter <= 0) {
                counter = 1;
            } else {
                counter++;
            }
            setOnDismissListener(dialog, null);
            register(dialog);
        }
    }

    static void showHint() {
        if (MainService.instance != null) {
            MainService.instance.showApp.update();
            if (MainService.instance.selectedTab != MainService.instance.mConfigPage) {
                MainService.instance.showApp.showHint();
            }
        }
    }

    static void hideHint() {
        if (MainService.instance != null) {
            counter--;
            if (counter <= 0) {
                ThreadManager.getHandlerUiThread().removeCallbacks(shower);
                MainService.instance.showApp.hideHint();
                counter = 0;
            }
        }
    }

    private static class DialogShowListener implements DialogInterface.OnShowListener {
        private final List<DialogInterface.OnShowListener> listeners;

        private DialogShowListener() {
            this.listeners = new ArrayList();
        }

        /* synthetic */ DialogShowListener(DialogShowListener dialogShowListener) {
            this();
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            Tools.fixDialogButtonsSize((AlertDialog) dialog);
            for (DialogInterface.OnShowListener listener : this.listeners) {
                if (listener != null) {
                    listener.onShow(dialog);
                }
            }
        }

        public void add(DialogInterface.OnShowListener listener) {
            if (listener != null && !this.listeners.contains(listener)) {
                this.listeners.add(listener);
            }
        }
    }

    private static class DialogShowHolder {
        public WeakReference<AlertDialog> dialog;
        public WeakReference<DialogShowListener> listener;

        public DialogShowHolder(AlertDialog dialog, DialogShowListener listener) {
            this.dialog = new WeakReference<>(dialog);
            this.listener = new WeakReference<>(listener);
        }
    }

    public static void setOnShowListener(AlertDialog dialog, DialogInterface.OnShowListener listener) {
        DialogShowHolder holder = null;
        int i = 0;
        while (i < dialogShowHolder.size()) {
            DialogShowHolder hold = dialogShowHolder.get(i);
            if (hold == null) {
                dialogShowHolder.remove(i);
                i--;
            } else {
                AlertDialog d = hold.dialog.get();
                if (d == null) {
                    dialogShowHolder.remove(i);
                    i--;
                } else if (d == dialog) {
                    holder = hold;
                }
            }
            i++;
        }
        if (holder == null) {
            DialogShowListener l = new DialogShowListener(null);
            dialog.setOnShowListener(l);
            holder = new DialogShowHolder(dialog, l);
            dialogShowHolder.add(holder);
        }
        holder.listener.get().add(listener);
    }

    private static class DialogDismissListener implements DialogInterface.OnDismissListener {
        private final List<DialogInterface.OnDismissListener> listeners;

        private DialogDismissListener() {
            this.listeners = new ArrayList();
        }

        /* synthetic */ DialogDismissListener(DialogDismissListener dialogDismissListener) {
            this();
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            Alert.hideHint();
            for (DialogInterface.OnDismissListener listener : this.listeners) {
                if (listener != null) {
                    listener.onDismiss(dialog);
                }
            }
        }

        public void add(DialogInterface.OnDismissListener listener) {
            if (listener != null && !this.listeners.contains(listener)) {
                this.listeners.add(listener);
            }
        }
    }

    private static class DialogDismissHolder {
        public WeakReference<AlertDialog> dialog;
        public WeakReference<DialogDismissListener> listener;

        public DialogDismissHolder(AlertDialog dialog, DialogDismissListener listener) {
            this.dialog = new WeakReference<>(dialog);
            this.listener = new WeakReference<>(listener);
        }
    }

    public static void setOnDismissListener(AlertDialog dialog, DialogInterface.OnDismissListener listener) {
        DialogDismissHolder holder = null;
        int i = 0;
        while (i < dialogDismissHolder.size()) {
            DialogDismissHolder hold = dialogDismissHolder.get(i);
            if (hold == null) {
                dialogDismissHolder.remove(i);
                i--;
            } else {
                AlertDialog d = hold.dialog.get();
                if (d == null) {
                    dialogDismissHolder.remove(i);
                    i--;
                } else if (d == dialog) {
                    holder = hold;
                }
            }
            i++;
        }
        if (holder == null) {
            DialogDismissListener l = new DialogDismissListener(null);
            dialog.setOnDismissListener(l);
            holder = new DialogDismissHolder(dialog, l);
            dialogDismissHolder.add(holder);
        }
        holder.listener.get().add(listener);
    }

    public static void tryShow(AlertDialog dialog) {
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {
            show(dialog);
        }
    }

    public static void show(AlertDialog dialog, final Runnable callback, boolean dismiss) {
        setOnShowListener(dialog, new DialogInterface.OnShowListener() { // from class: android.ext.Alert.2
            @Override // android.content.DialogInterface.OnShowListener
            public void onShow(DialogInterface dialog2) {
                Log.d(callback + ": 30_");
                ThreadManager.getHandlerUiThread().removeCallbacks(callback);
            }
        });
        if (dismiss) {
            setOnDismissListener(dialog, new DialogInterface.OnDismissListener() { // from class: android.ext.Alert.3
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialog2) {
                    Log.d(callback + ": 40_");
                    ThreadManager.getHandlerUiThread().postDelayed(callback, LuaThread.thread_orphan_check_interval);
                }
            });
        }
        ThreadManager.getHandlerUiThread().postDelayed(callback, 3000L);
        Log.d(callback + ": 10_");
        show(dialog);
        Log.d(callback + ": 20_");
    }

    public static AlertDialog show(AlertDialog dialog) {
        show(dialog, (android.widget.EditText) null);
        return dialog;
    }

    public static AlertDialog show(final AlertDialog dialog, android.widget.EditText edit) {
        if (edit != null) {
            edit.setOnEditorActionListener(new EditorActionListener(dialog));
        }
        fixDialog(dialog);
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Alert.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Alert.showAndFix(dialog);
                } catch (Throwable e) {
                    Log.e("Failed show dialog on service", e);
                }
            }
        });
        return dialog;
    }

    public static void show(AlertDialog.Builder builder, Context context) {
        show(builder, (EditText) null);
    }

    public static void show(AlertDialog.Builder builder) {
        show(builder, (EditText) null);
    }

    public static void showAfterDaemonStart(final AlertDialog.Builder builder) {
        DaemonManager.runAfterDaemonStart(new Runnable() { // from class: android.ext.Alert.5
            @Override // java.lang.Runnable
            public void run() {
                Alert.show(builder, (EditText) null);
            }
        });
    }

    public static void show(final AlertDialog.Builder builder, final EditText edit) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.Alert.6
            @Override // java.lang.Runnable
            public void run() {
                Alert.show(builder.create(), edit);
            }
        });
    }

    public static AlertDialog.Builder create() {
        return create(Tools.getContext());
    }

    public static AlertDialog.Builder create(Context context) {
        return new AlertDialog.Builder(context);
    }

    public static void register(AlertDialog dialog) {
        Window window = dialog.getWindow();
        if (window.getAttributes().type >= 2000) {
            ArrayList<WeakReference<AlertDialog>> oldDialogs = dialogs;
            ArrayList<WeakReference<AlertDialog>> newDialogs = new ArrayList<>();
            for (WeakReference<AlertDialog> weak : oldDialogs) {
                AlertDialog d = weak.get();
                if (d != null && d != dialog) {
                    newDialogs.add(weak);
                }
            }
            newDialogs.add(new WeakReference<>(dialog));
            dialogs = newDialogs;
            oldDialogs.clear();
            oldDialogs.trimToSize();
        }
    }

    public static void dismissAll() {
        List<WeakReference<AlertDialog>> oldDialogs = dialogs;
        for (WeakReference<AlertDialog> weak : oldDialogs) {
            AlertDialog d = weak.get();
            if (d != null) {
                try {
                    if (d.isShowing()) {
                        Tools.dismiss(d);
                    }
                } catch (Throwable e) {
                    Log.w("Failed dismiss dialog: " + d, e);
                }
            }
        }
        if (MainService.instance != null) {
            MainService.instance.dismissDialog();
        }
    }
}
