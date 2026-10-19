package android.ext;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.DeadObjectException;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class MainDialog extends Dialog implements DialogInterface.OnDismissListener {
    private View view;

    public MainDialog(Context context, View view) {
        super(context);
        this.view = view;
        requestWindowFeature(1);
        setContentView(Tools.getViewForAttach(view));
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setOnDismissListener(this);
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        Log.d("MainDialog dismiss");
    }

    @Override // android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (super.onKeyDown(keyCode, event)) {
            return true;
        }
        if (keyCode == 82) {
            event.startTracking();
            return true;
        }
        return false;
    }

    @Override // android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (super.onKeyUp(keyCode, event)) {
            return true;
        }
        if (keyCode == 82 && event.isTracking() && !event.isCanceled()) {
            onMenuPressed();
            return true;
        }
        return false;
    }

    public void onMenuPressed() {
        Log.d("MainDialog menu");
        MainService.instance.onMenuPressed();
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("MainDialog back");
        MainService.instance.dismissDialog();
    }

    @Override // android.app.Dialog
    public void show() {
        setup();
        Tools.fixViewParams(getWindow());
        try {
            super.show();
            View decorView = getWindow().getDecorView();
            Tools.fixViews(decorView);
        } catch (Throwable e) {
            Throwable cause = e.getCause();
            if (cause != null && (cause instanceof DeadObjectException)) {
                Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.failed_add_window)).setMessage(Re.s(R.string.system_service_stopped)).setPositiveButton(Re.s(R.string.exit), new ExitListener(800)).setNegativeButton(Re.s(R.string.skip), (DialogInterface.OnClickListener) null));
            }
            Log.e("main show failed", e);
        }
        if (!isShowing()) {
            Log.w("main show fail");
        }
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        try {
            super.dismiss();
            Tools.getViewForAttach(this.view);
        } catch (Throwable e) {
            Throwable cause = e.getCause();
            if (cause != null && (cause instanceof DeadObjectException)) {
                Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.failed_remove_window)).setMessage(Re.s(R.string.system_service_stopped)).setPositiveButton(Re.s(R.string.exit), new ExitListener(900)).setNegativeButton(Re.s(R.string.skip), (DialogInterface.OnClickListener) null));
            }
            Log.e("main dismiss failed", e);
        }
        if (isShowing()) {
            Log.w("main hide fail");
        }
    }

    @TargetApi(11)
    private void setup() {
        this.view.setPadding(0, (Config.configClient & 16) != 0 ? (int) Tools.dp2px(24.0f) : 0, 0, 0);
        Window window = getWindow();
        window.setAttributes(getLayoutParams());
        window.setBackgroundDrawable(new ColorDrawable(0));
        if ((Config.configClient & 2) != 0) {
            window.addFlags(AddressItem.FLAG_REVERT);
        } else {
            window.clearFlags(AddressItem.FLAG_REVERT);
        }
        ShowApp.register(window);
    }

    private WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = SystemConstants.getTypePhone();
        params.width = -1;
        params.height = -1;
        params.format = -2;
        params.horizontalMargin = 0.0f;
        params.horizontalWeight = 1.0f;
        params.verticalMargin = 0.0f;
        params.verticalWeight = 1.0f;
        params.dimAmount = 0.0f;
        return params;
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Throwable e) {
            Log.badImplementation(e);
            return false;
        }
    }

    @Override // android.app.Dialog
    public void setContentView(int layoutResID) {
        try {
            super.setContentView(layoutResID);
        } catch (RuntimeException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.app.Dialog
    public void setContentView(View view) {
        try {
            super.setContentView(view);
        } catch (RuntimeException e) {
            Log.badImplementation(e);
        }
    }

    @Override // android.app.Dialog
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        try {
            super.setContentView(view, params);
        } catch (RuntimeException e) {
            Log.badImplementation(e);
        }
    }
}
