package android.ext;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class TypicalValues implements View.OnClickListener, DialogInterface.OnClickListener {
    private android.widget.EditText edit;
    private final String[] names;
    private final String[] values;

    public TypicalValues(String[] names, String[] values) {
        this.names = names;
        this.values = values;
        if (names.length != values.length) {
            throw new IllegalArgumentException("Params must be with same length: " + names.length + " != " + values.length);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v == null) {
            Log.w("Unkwnon view", new NullPointerException());
            return;
        }
        Object tag = v.getTag();
        if (!(tag instanceof android.widget.EditText)) {
            Log.w("Unkwnon tag: " + tag, new IllegalArgumentException());
        } else {
            this.edit = (android.widget.EditText) tag;
            Alert.show(Alert.create().setCustomTitle(Tools.getCustomTitle(R.string.typical_values)).setItems(this.names, this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        if (which < 0 || which >= this.values.length) {
            Log.w("Bad index: " + which + " for " + this.values.length, new IndexOutOfBoundsException());
        } else if (this.edit == null) {
            Log.w("Unkwnon edit", new NullPointerException());
        } else {
            this.edit.setText(this.values[which]);
        }
    }

    public static void setCacheDirs(Button btn, android.widget.EditText pathEdit) {
        String internal;
        String external;
        try {
            internal = Tools.getContext().getFilesDir().getAbsolutePath();
        } catch (Throwable e) {
            internal = "error";
            Log.w("Failed get files dir", e);
        }
        try {
            external = Tools.getContext().getExternalFilesDir(null).getAbsolutePath();
        } catch (Throwable e2) {
            external = "error";
            Log.w("Failed get ext files dir", e2);
        }
        btn.setOnClickListener(new TypicalValues(new String[]{Re.s(R.string.internal_storage), Re.s(R.string.external_storage)}, new String[]{internal, external}));
        btn.setTag(pathEdit);
    }
}
