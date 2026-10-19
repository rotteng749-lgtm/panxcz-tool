package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.fix.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class GoToButtonListener extends MenuItem {
    public GoToButtonListener() {
        super(R.string.goto1, R.drawable.ic_forward_white_24dp);
    }

    @Override // android.ext.MenuItem, android.view.View.OnClickListener
    public void onClick(View v) {
        new Impl(null).onClick(v);
    }

    private static class Impl implements View.OnClickListener, DialogInterface.OnClickListener, DialogInterface.OnDismissListener, DialogInterface.OnShowListener {
        private static String lastInput = "";
        private AlertDialog dialog;
        private EditText edit;

        private Impl() {
        }

        /* synthetic */ Impl(Impl impl) {
            this();
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            if (which == -1) {
                onClick(((AlertDialog) dialog).getButton(which));
            }
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            Tools.setButtonListener(dialog, -1, null, this, this.edit);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (v == null || (v.getTag() instanceof MenuItem)) {
                View view = LayoutInflater.inflateStatic(R.layout.service_goto, (ViewGroup) null);
                TextView addr = (TextView) view.findViewById(R.id.addr);
                Tools.setButtonHelpBackground(addr);
                addr.setOnClickListener(this);
                EditText edit = (EditText) view.findViewById(R.id.mem_edit_addr);
                this.edit = edit;
                final RegionList regionList = (RegionList) view.findViewById(R.id.region_from);
                regionList.setTag(edit);
                View.OnClickListener listener = new View.OnClickListener() { // from class: android.ext.GoToButtonListener.Impl.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v2) {
                        int filter;
                        switch (v2.getId()) {
                            case R.id.cb /* 2131427616 */:
                                filter = 16;
                                break;
                            case R.id.ps /* 2131427617 */:
                                filter = 262144;
                                break;
                            case R.id.xa /* 2131427618 */:
                                filter = 16384;
                                break;
                            default:
                                filter = 8;
                                break;
                        }
                        regionList.onClick(regionList, filter);
                    }
                };
                view.findViewById(R.id.cd).setOnClickListener(listener);
                view.findViewById(R.id.cb).setOnClickListener(listener);
                view.findViewById(R.id.ps).setOnClickListener(listener);
                view.findViewById(R.id.xa).setOnClickListener(listener);
                view.findViewById(R.id.history).setOnClickListener(this);
                edit.setText(lastInput);
                edit.setDataType(1);
                AlertDialog dialog = Alert.create().setView(InternalKeyboard.getView(view)).setPositiveButton(Re.s(R.string.goto1), this).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null).create();
                this.dialog = dialog;
                Alert.setOnDismissListener(dialog, this);
                Alert.setOnShowListener(dialog, this);
                Alert.show(dialog, edit);
                return;
            }
            if (v.getId() == R.id.history) {
                Tools.dismiss(this.dialog);
                MainService.instance.memoryHistory.choice();
                return;
            }
            if (v.getId() == R.id.addr) {
                Alert.show(Alert.create().setMessage(String.valueOf(Re.s(R.string.examples)) + ":\nBAFE890\nBAFE890+3F0\nBAFE890-A40\nBAFE890+FE-CD7+44-3E\n\nlibc.so\nlibc.so+E0\nlibc.so-32C").setPositiveButton(Re.s(R.string.help), ConfigListAdapter.getShowHelpListener()).setNegativeButton(Re.s(R.string.cancel), (DialogInterface.OnClickListener) null));
                return;
            }
            try {
                EditText edit2 = this.edit;
                if (edit2 != null) {
                    String str = SearchMenuItem.checkScript(edit2.getText().toString().trim());
                    History.add(str, 1);
                    MainService.instance.goToAddress(null, str, Tools.stripColon(R.string.from_goto));
                }
                Tools.dismiss(this.dialog);
            } catch (NumberFormatException e) {
                SearchMenuItem.inputError(e, this.edit);
            }
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            EditText edit = this.edit;
            if (edit != null) {
                lastInput = edit.getText().toString().trim();
            }
        }
    }
}
