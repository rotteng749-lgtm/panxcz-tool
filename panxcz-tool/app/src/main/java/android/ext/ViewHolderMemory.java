package android.ext;

import android.ext.ViewHolderBase;
import android.view.View;
import android.widget.TextView;

/* JADX INFO: compiled from: MemoryContentAdapter.java */
/* JADX INFO: loaded from: classes.dex */
class ViewHolderMemory extends ViewHolderBase {
    TextView valueByte;
    TextView valueDouble;
    TextView valueDword;
    TextView valueFloat;
    TextView valueQword;
    TextView valueWord;
    TextView valueXor;

    ViewHolderMemory(View view, ViewHolderBase.Listener listener) {
        super(view, listener);
    }
}
