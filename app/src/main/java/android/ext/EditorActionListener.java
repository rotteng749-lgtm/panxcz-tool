package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class EditorActionListener implements TextView.OnEditorActionListener {
    private WeakReference<AlertDialog> weakDialog;

    public EditorActionListener(AlertDialog dialog) {
        this.weakDialog = new WeakReference<>(null);
        this.weakDialog = new WeakReference<>(dialog);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.widget.TextView.OnEditorActionListener
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        Button button;
        boolean handled = false;
        if (actionId != 6 && actionId != 3 && actionId != 4 && actionId != 2) {
            return false;
        }
        AlertDialog alertDialog = this.weakDialog.get();
        if (0 == 0 && (alertDialog instanceof AlertDialog) && (button = alertDialog.getButton(-1)) != null && button.getVisibility() == 0) {
            Tools.click(button);
            handled = true;
        }
        if (!handled && (alertDialog instanceof DialogInterface.OnClickListener)) {
            ((DialogInterface.OnClickListener) alertDialog).onClick(null, -1);
            handled = true;
        }
        if (!handled && (alertDialog instanceof View.OnClickListener)) {
            ((View.OnClickListener) alertDialog).onClick(v);
            return true;
        }
        return handled;
    }
}
