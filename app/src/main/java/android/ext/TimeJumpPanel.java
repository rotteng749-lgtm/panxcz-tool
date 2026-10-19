package android.ext;

import android.content.Context;
import android.fix.LayoutInflater;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class TimeJumpPanel extends FloatPanel implements View.OnClickListener {
    Button jumpAsk;
    Button jumpLast;

    private static long getLast() {
        return (long) ((((double) Config.timeJumpLast) / 1000.0d) * 1.0E9d);
    }

    public void setLast() {
        long jump = getLast();
        this.jumpLast.setVisibility(jump == 0 ? 8 : 0);
        this.jumpLast.setText(Tools.doubleToTime(jump / 1.0E9d));
    }

    public static void toggle(boolean show) {
        TimeJumpPanel panel = MainService.instance.timeJumpPanel;
        boolean enabled = (Config.configClient & 128) != 0;
        if (show && enabled) {
            if (panel == null) {
                panel = (TimeJumpPanel) LayoutInflater.inflateStatic(R.layout.time_jump_panel, (ViewGroup) null);
                panel.init();
                MainService.instance.timeJumpPanel = panel;
            }
            panel.show();
            return;
        }
        if (panel != null) {
            panel.hide();
            if (!enabled) {
                MainService.instance.timeJumpPanel = null;
            }
        }
    }

    private void init() {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.TimeJumpPanel.1
            @Override // java.lang.Runnable
            public void run() {
                TimeJumpPanel.this.jumpLast = (Button) TimeJumpPanel.this.findViewById(R.id.time_jump_last);
                TimeJumpPanel.this.jumpAsk = (Button) TimeJumpPanel.this.findViewById(R.id.time_jump);
                TimeJumpPanel.this.jumpLast.setOnClickListener(TimeJumpPanel.this);
                TimeJumpPanel.this.jumpAsk.setOnClickListener(TimeJumpPanel.this);
                TimeJumpPanel.this.setLast();
                HotPoint hotPoint = MainService.instance.hotPoint;
                TimeJumpPanel.this.setLayoutAlpha(hotPoint.getLayoutAlpha());
                TimeJumpPanel.this.setMinSize(hotPoint.getSizePx());
            }
        });
    }

    public void setMinSize(int px) {
        if (this.jumpLast != null) {
            this.jumpLast.setMinWidth(px);
            this.jumpLast.setMinHeight(px);
            this.jumpAsk.setMinWidth(px);
            this.jumpAsk.setMinHeight(px);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.time_jump /* 2131427653 */:
                    TimeJump tj = TimeJump.lastInstance;
                    if (tj != null) {
                        tj.onClick(null);
                    }
                    break;
                case R.id.time_jump_last /* 2131427654 */:
                    long last = getLast();
                    TimeJump.makeJump(last);
                    double jump = last / 1.0E9d;
                    Tools.showToast(String.valueOf(Tools.stripColon(R.string.time_jump)) + ": " + Tools.doubleToTime(jump) + " (" + Double.toString(jump).replaceFirst("\\D?0+$", "") + ')', 0);
                    break;
                default:
                    Log.d("Unknown id: " + Integer.toHexString(v.getId()), new RuntimeException());
                    break;
            }
        }
    }

    public TimeJumpPanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TimeJumpPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TimeJumpPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeJumpPanel(Context context) {
        super(context);
    }

    @Override // android.ext.FloatPanel
    protected String getPrefName() {
        return "time-jump";
    }
}
