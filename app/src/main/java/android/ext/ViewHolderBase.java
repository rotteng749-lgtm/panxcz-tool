package android.ext;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: compiled from: MemoryContentAdapter.java */
/* JADX INFO: loaded from: classes.dex */
class ViewHolderBase {
    static final int NOT_SELECTED = -2;
    final TextView address;
    final CheckBox ch;
    final int orientation = MainService.context.getResources().getConfiguration().orientation;
    int pos;
    final TextView region;
    final TextView valueArm;
    final TextView valueArm8;
    final TextView valueHex;
    final TextView valueJava;
    final TextView valueRHex;
    final TextView valueString;
    final TextView valueThumb;
    final View view;

    /* JADX INFO: compiled from: MemoryContentAdapter.java */
    interface Listener extends CompoundButton.OnCheckedChangeListener, View.OnLongClickListener {
    }

    ViewHolderBase(View view, Listener listener) {
        this.ch = (CheckBox) view.findViewById(R.id.ch);
        this.address = (TextView) view.findViewById(R.id.address);
        this.valueHex = (TextView) view.findViewById(R.id.value_hex);
        this.valueRHex = (TextView) view.findViewById(R.id.value_rhex);
        this.valueString = (TextView) view.findViewById(R.id.value_string);
        this.valueJava = (TextView) view.findViewById(R.id.value_java);
        this.valueArm = (TextView) view.findViewById(R.id.value_arm);
        this.valueThumb = (TextView) view.findViewById(R.id.value_thumb);
        this.valueArm8 = (TextView) view.findViewById(R.id.value_arm8);
        this.region = (TextView) view.findViewById(R.id.addr_item_region);
        this.view = view;
        this.view.setTag(this);
        this.ch.setOnCheckedChangeListener(listener);
        this.ch.setOnLongClickListener(listener);
        this.ch.setTag(this);
    }

    boolean wrongOrientation() {
        return this.orientation != MainService.context.getResources().getConfiguration().orientation;
    }

    void updateBackground(boolean checked, int selected) {
        int i;
        View view = this.view;
        if (checked) {
            i = SystemConstants.SELECTED_ITEM_COLOR;
        } else {
            i = this.pos == selected ? SystemConstants.JUMP_ITEM_COLOR : 0;
        }
        view.setBackgroundColor(i);
    }
}
