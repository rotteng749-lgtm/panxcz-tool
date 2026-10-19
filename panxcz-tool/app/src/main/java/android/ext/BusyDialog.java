package android.ext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.fix.LayoutInflater;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class BusyDialog implements DialogInterface.OnShowListener, View.OnClickListener {
    volatile AlertDialog mBusyDialog;
    long startTime = -1;
    long startProgress = -1;
    long startProgressMax = -1;
    final Runnable shower = new Runnable() { // from class: android.ext.BusyDialog.1
        @Override // java.lang.Runnable
        public void run() {
            AlertDialog dialog = BusyDialog.this.mBusyDialog;
            if (dialog != null) {
                Alert.show(dialog);
            }
        }
    };
    final View view = LayoutInflater.inflateStatic(R.layout.service_busy_dialog, (ViewGroup) null);
    final TextView busyMessage = (TextView) this.view.findViewById(R.id.message);
    final ProgressBar busyProgress = (ProgressBar) this.view.findViewById(R.id.progress_bar);
    final TextView busyProgressText = (TextView) this.view.findViewById(R.id.progress_bar_text);
    final View busyProgressStageLayout = this.view.findViewById(R.id.stage_layout);
    final ProgressBar busyProgressStage = (ProgressBar) this.view.findViewById(R.id.progress_bar_stage);
    final TextView busyProgressStageText = (TextView) this.view.findViewById(R.id.progress_bar_stage_text);
    final TextView busyFoundCount = (TextView) this.view.findViewById(R.id.found_count);
    final TextView busyTime = (TextView) this.view.findViewById(R.id.time);

    public BusyDialog() {
        this.view.findViewById(R.id.help_speed_up).setOnClickListener(this);
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialog) {
        Tools.setButtonListener(dialog, -1, Integer.valueOf(R.string.hide), MainService.instance);
        Tools.setButtonListener(dialog, -2, Integer.valueOf(R.id.abort_button), MainService.instance);
    }

    public boolean show(boolean force) {
        AlertDialog dialog = this.mBusyDialog;
        if (dialog != null) {
            Alert.show(dialog);
            if (force) {
                Log.d("Show busy dialog not first time", new RuntimeException());
            }
        } else if (force) {
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.BusyDialog.2
                @Override // java.lang.Runnable
                public void run() {
                    BusyDialog.this.startTime = -1L;
                    BusyDialog.this.mBusyDialog = Alert.create().setView(Tools.getViewForAttach(BusyDialog.this.view)).setCancelable(false).setPositiveButton(R.string.hide, (DialogInterface.OnClickListener) null).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).create();
                    BusyDialog.this.onProgress("", -1L, 1L, 0, 1, 0L, "");
                    Alert.setOnShowListener(BusyDialog.this.mBusyDialog, BusyDialog.this);
                    if (MainService.instance.mainDialog != null) {
                        Handler handler = ThreadManager.getHandlerUiThread();
                        handler.removeCallbacks(BusyDialog.this.shower);
                        handler.postDelayed(BusyDialog.this.shower, 100L);
                    }
                }
            });
        }
        return true;
    }

    public void dismiss(boolean force) {
        AlertDialog dialog = this.mBusyDialog;
        if (dialog != null) {
            ThreadManager.getHandlerUiThread().removeCallbacks(this.shower);
            Tools.dismiss(dialog);
            if (force) {
                this.mBusyDialog = null;
            }
        }
    }

    public boolean isVisible() {
        return this.mBusyDialog != null;
    }

    public void onProgress(final CharSequence message, final long progress, final long progressMax, final int stage, final int stageMax, final long found, final String progressText) {
        if (isVisible()) {
            if (ThreadManager.isInUiThread()) {
                onProgressUi(message, progress, progressMax, stage, stageMax, found, progressText);
            } else {
                ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.BusyDialog.3
                    @Override // java.lang.Runnable
                    public void run() {
                        BusyDialog.this.onProgressUi(message, progress, progressMax, stage, stageMax, found, progressText);
                    }
                });
            }
        }
    }

    void onProgressUi(CharSequence message, long progress, long progressMax, int stage, int stageMax, long found, String progressText) {
        if (message != null) {
            this.busyMessage.setText(message);
        }
        if (progress < 0 || progressMax == 0) {
            this.busyProgressText.setText(progressText);
            this.busyProgress.setIndeterminate(true);
        } else {
            this.busyProgress.setIndeterminate(false);
            this.busyProgress.setMax((int) progressMax);
            this.busyProgress.setProgress((int) progress);
            this.busyProgressText.setText(progressText);
        }
        if (stage >= 0) {
            if (stageMax <= 1) {
                this.busyProgressStageLayout.setVisibility(8);
            } else {
                this.busyProgressStage.setMax(stageMax);
                this.busyProgressStage.setProgress(stage);
                this.busyProgressStageText.setText(Tools.stringFormat(Re.s(R.string.from), Integer.valueOf(stage), Integer.valueOf(stageMax)));
                this.busyProgressStageLayout.setVisibility(0);
            }
        }
        this.busyFoundCount.setText(Tools.stringFormat(Re.s(R.string.found_count), Long.valueOf(found)));
        long curTime = System.currentTimeMillis();
        if (progress < 0) {
            this.startTime = -1L;
            this.startProgress = -1L;
            this.startProgressMax = -1L;
        } else if (this.startProgressMax != progressMax) {
            this.startTime = curTime;
            this.startProgress = progress;
            this.startProgressMax = progressMax;
        }
        long time = (this.startTime < 0 || curTime - this.startTime < 2000 || progress - this.startProgress == 0) ? -1L : Math.round((((curTime - this.startTime) * (progressMax - progress)) / (progress - this.startProgress)) / 1000.0d);
        if (time <= 0) {
            this.busyTime.setText("");
        } else {
            this.busyTime.setText(String.valueOf(Tools.stripColon(R.string.time_remaining)) + ": " + Tools.longToTime(time));
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        ConfigListAdapter.showHelp(R.string.help_speed_up);
    }
}
