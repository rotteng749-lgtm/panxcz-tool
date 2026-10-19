package android.ext;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.fix.LayoutInflater;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;

/* JADX INFO: loaded from: classes.dex */
public class HotPoint extends FloatPanel implements View.OnClickListener, View.OnLongClickListener {
    private static final String ICON_SIZE = "icon-size";
    private static final int ICON_SIZE_DEFAULT = 0;
    public static final int ICON_SIZE_MIN = 12;
    private static final String ICON_VANISHING_TIME = "vanishing-time";
    private static final int ICON_VANISHING_TIME_DEFAULT = -1;
    private static volatile HotPoint instance = null;
    ProgressBar busyProgress;
    ProgressBar busyProgressStage;
    TextView busyProgressText;
    View hotFrame;
    private final Runnable mClearSprite;
    ImageView mDecButton;
    ImageView mHotPointIcon;
    ImageView mIncButton;
    int mSize;
    int mVanishingTime;
    View progressLayout;
    TextView speedPanel;
    private WindowManager.LayoutParams speedPanelParams;
    private boolean speedPanelVisible;

    public static HotPoint getInstance() {
        HotPoint inst = instance;
        if (inst == null) {
            HotPoint inst2 = (HotPoint) LayoutInflater.inflateStatic(R.layout.hot_point_view, (ViewGroup) null);
            inst2.init();
            instance = inst2;
            return inst2;
        }
        return inst;
    }

    void initSpeedPanel() {
        this.speedPanelParams = new WindowManager.LayoutParams();
        this.speedPanelParams.gravity = 51;
        this.speedPanelParams.type = SystemConstants.getTypePhone();
        this.speedPanelParams.format = -2;
        this.speedPanelParams.flags = SystemConstants.getFlags() | 512;
        this.speedPanelParams.alpha = 1.0f;
        this.speedPanelParams.width = -2;
        this.speedPanelParams.height = -2;
    }

    private void init() {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.HotPoint.2
            @Override // java.lang.Runnable
            public void run() {
                HotPoint.this.hotFrame = HotPoint.this.findViewById(R.id.hot_frame);
                HotPoint.this.hotFrame.setOnClickListener(MainService.instance);
                HotPoint.this.mHotPointIcon = (ImageView) HotPoint.this.findViewById(R.id.hot_point_icon);
                HotPoint.this.progressLayout = HotPoint.this.findViewById(R.id.progress_layout);
                HotPoint.this.busyProgressText = (TextView) HotPoint.this.findViewById(R.id.progress_bar_text);
                HotPoint.this.busyProgress = (ProgressBar) HotPoint.this.findViewById(R.id.progress_bar);
                HotPoint.this.busyProgressStage = (ProgressBar) HotPoint.this.findViewById(R.id.progress_bar_stage);
                HotPoint.this.speedPanel = (TextView) Tools.getViewForAttach(HotPoint.this.findViewById(R.id.speed_text));
                HotPoint.this.mIncButton = (ImageView) HotPoint.this.findViewById(R.id.speed_inc);
                HotPoint.this.mDecButton = (ImageView) HotPoint.this.findViewById(R.id.speed_dec);
                HotPoint.this.mIncButton.setOnClickListener(HotPoint.this);
                HotPoint.this.mDecButton.setOnClickListener(HotPoint.this);
                HotPoint.this.speedPanel.setOnLongClickListener(HotPoint.this);
                if (Build.VERSION.SDK_INT >= 11) {
                    HotPoint.this.speedPanel.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: android.ext.HotPoint.2.1
                        @Override // android.view.View.OnLayoutChangeListener
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            HotPoint.this.updateLayout();
                        }
                    });
                }
                HotPoint.this.onProgress((byte) 2, -1L, 1L, 0, 1, "");
                HotPoint.this.initSpeedPanel();
                HotPoint.this.hidePanel();
            }
        });
    }

    public void onProgress(final byte use, final long progress, final long progressMax, final int stage, final int stageMax, final String progressText) {
        if (ThreadManager.isInUiThread()) {
            onProgressUi(use, progress, progressMax, stage, stageMax, progressText);
        } else {
            ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.HotPoint.3
                @Override // java.lang.Runnable
                public void run() {
                    HotPoint.this.onProgressUi(use, progress, progressMax, stage, stageMax, progressText);
                }
            });
        }
    }

    void onProgressUi(byte use, long progress, long progressMax, int stage, int stageMax, String progressText) {
        if (use != 0) {
            this.progressLayout.setVisibility(use == 1 ? 0 : 8);
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
                this.busyProgressStage.setVisibility(8);
                return;
            }
            this.busyProgressStage.setMax(stageMax);
            this.busyProgressStage.setProgress(stage);
            this.busyProgressStage.setVisibility(0);
        }
    }

    public void hidePanel() {
        setVisibilitySpeedhack(8);
    }

    public void showPanel() {
        setVisibilitySpeedhack(0);
    }

    public void togglePanel() {
        setVisibilitySpeedhack(-1);
    }

    private void setVisibilitySpeedhack(final int vis) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.HotPoint.4
            @Override // java.lang.Runnable
            public void run() {
                int v = 8;
                if (vis != -1) {
                    v = vis;
                } else if (HotPoint.this.mIncButton.getVisibility() == 8) {
                    v = 0;
                }
                HotPoint.this.mIncButton.setVisibility(v);
                HotPoint.this.mDecButton.setVisibility(v);
                HotPoint.this.setSpeedPanelVisibility(true);
                HotPoint.this.updateLayout();
                if (v == 0 && HotPoint.this.mVanishingTime >= 0) {
                    HotPoint.this.postCallback(true);
                }
            }
        });
    }

    private void setMargins() {
        if (this.hotFrame != null) {
            int oldX = getMarginX();
            int newX = 0;
            View v = this.hotFrame;
            while (v != this && v != null) {
                newX -= v.getLeft();
                Object parent = v.getParent();
                if (!(parent instanceof View)) {
                    break;
                } else {
                    v = (View) parent;
                }
            }
            if (oldX != newX) {
                setMarginX(newX);
                setLayoutXY();
                updateLayout();
            }
        }
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        setMargins();
    }

    public void setVanishingTime(int time) {
        if (this.mVanishingTime != time) {
            new SPEditor().putInt(ICON_VANISHING_TIME, time, -1).commit();
            this.mVanishingTime = time;
        }
    }

    public int getVanishingTime() {
        return this.mVanishingTime;
    }

    public void setSize(int size) {
        int size2 = Math.max(0, Math.min(12, size));
        if (this.mSize != size2) {
            new SPEditor().putInt(ICON_SIZE, size2, 0).commit();
            this.mSize = size2;
        }
    }

    public int getSize() {
        return this.mSize;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v == this.mIncButton) {
            MainService.instance.nextSpeed();
        } else if (v == this.mDecButton) {
            MainService.instance.prevSpeed();
        }
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        MainService.instance.resetSpeed();
        return true;
    }

    @TargetApi(21)
    public HotPoint(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mClearSprite = new Runnable() { // from class: android.ext.HotPoint.1
            @Override // java.lang.Runnable
            public void run() {
                HotPoint.this.hidePanel();
                Tools.setImageAlpha(HotPoint.this.mHotPointIcon, 0.0f);
            }
        };
    }

    @TargetApi(11)
    public HotPoint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mClearSprite = new Runnable() { // from class: android.ext.HotPoint.1
            @Override // java.lang.Runnable
            public void run() {
                HotPoint.this.hidePanel();
                Tools.setImageAlpha(HotPoint.this.mHotPointIcon, 0.0f);
            }
        };
    }

    public HotPoint(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mClearSprite = new Runnable() { // from class: android.ext.HotPoint.1
            @Override // java.lang.Runnable
            public void run() {
                HotPoint.this.hidePanel();
                Tools.setImageAlpha(HotPoint.this.mHotPointIcon, 0.0f);
            }
        };
    }

    public HotPoint(Context context) {
        super(context);
        this.mClearSprite = new Runnable() { // from class: android.ext.HotPoint.1
            @Override // java.lang.Runnable
            public void run() {
                HotPoint.this.hidePanel();
                Tools.setImageAlpha(HotPoint.this.mHotPointIcon, 0.0f);
            }
        };
    }

    public void updateIcon(final Drawable icon) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.HotPoint.5
            @Override // java.lang.Runnable
            public void run() {
                Tools.addIconToTextView(HotPoint.this.speedPanel, icon, 24 - HotPoint.this.mSize);
            }
        });
    }

    public void updateSpeed(final String format) {
        ThreadManager.runOnUiThread(new Runnable() { // from class: android.ext.HotPoint.6
            @Override // java.lang.Runnable
            public void run() {
                HotPoint.this.speedPanel.setText(format);
                HotPoint.this.updateLayout();
            }
        });
    }

    @Override // android.ext.FloatPanel
    protected String getDebug(String action) {
        return String.valueOf(super.getDebug(action)) + ' ' + this.mVanishingTime + ' ' + this.mSize;
    }

    @Override // android.ext.FloatPanel
    protected String getPrefName() {
        return "pos";
    }

    public int getSizePx() {
        return (int) Tools.dp2px(48 - (this.mSize * 2));
    }

    void postCallback(boolean post) {
        try {
            ThreadManager.getHandlerUiThread().removeCallbacks(this.mClearSprite);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
        if (post) {
            Tools.setImageAlpha(this.mHotPointIcon, 255.0f);
            try {
                ThreadManager.getHandlerUiThread().postDelayed(this.mClearSprite, this.mVanishingTime * DaemonManager.TIME_JUMP_MUL_INT);
            } catch (Throwable e2) {
                Log.badImplementation(e2);
                Tools.setImageAlpha(this.mHotPointIcon, 0.0f);
            }
        }
    }

    @Override // android.ext.FloatPanel
    protected void onShow() {
        super.onShow();
        int size = getSizePx();
        Config.setIconSizeEx(this.mIncButton, size);
        Config.setIconSizeEx(this.mDecButton, size);
        Config.setIconSizeEx(this.mHotPointIcon, size);
        this.hotFrame.setMinimumWidth(size);
        this.hotFrame.setMinimumHeight(size);
        Tools.setWidth(this.busyProgressText, size);
        Tools.setHeight(this.busyProgressText, (size * 20) / 48);
        Tools.setWidth(this.busyProgress, size);
        Tools.setHeight(this.busyProgress, (size * 14) / 48);
        Tools.setWidth(this.busyProgressStage, size);
        Tools.setHeight(this.busyProgressStage, (size * 14) / 48);
        this.busyProgressText.setTextSize(0, (size * 18) / 48);
        this.speedPanel.setTextSize(0, (size * 18) / 48);
        if (this.mVanishingTime < 0) {
            Tools.setImageAlpha(this.mHotPointIcon, 255.0f);
        } else {
            postCallback(true);
        }
        setSpeedPanelVisibility(true);
        TimeJumpPanel.toggle(true);
    }

    void setSpeedPanelVisibility(boolean show) {
        boolean visible = this.mIncButton.getVisibility() == 0;
        if (this.speedPanelVisible) {
            try {
                Tools.removeViewWithWrapper(this.speedPanel);
            } catch (Throwable e) {
                Log.w("Failed hide speed panel", e);
            }
            this.speedPanelVisible = false;
        }
        if (show && visible) {
            this.speedPanelVisible = true;
            try {
                Tools.addViewWithWrapper(this.speedPanel, this.speedPanelParams);
            } catch (Throwable e2) {
                Log.w("Failed add speed panel", e2);
            }
        }
    }

    @Override // android.ext.FloatPanel
    public void updateLayout() {
        super.updateLayout();
        if (this.speedPanelVisible) {
            Point screen = new Point(0, 0);
            Point p = fixCoords(new Point(this.layoutParams.x, this.layoutParams.y), false, screen);
            if (p.x < 0) {
                p.x = 0;
            }
            if (p.x > screen.x + getMarginX()) {
                p.x = screen.x + getMarginX();
            }
            this.speedPanelParams.x = (p.x + (getWidth() / 2)) - (this.speedPanel.getWidth() / 2);
            this.speedPanelParams.y = p.y + getHeight();
            if (screen.y != 0) {
                int h = this.speedPanel.getHeight();
                if (screen.y < p.y + h) {
                    this.speedPanelParams.y = p.y - h;
                }
            }
            Tools.updateViewLayout(this.speedPanel, this.speedPanelParams);
        }
    }

    @Override // android.ext.FloatPanel
    protected void onHide() {
        super.onHide();
        postCallback(false);
        setSpeedPanelVisibility(false);
        TimeJumpPanel.toggle(false);
    }

    public void updatePanels() {
        TimeJumpPanel.toggle(isVisible());
    }

    @Override // android.ext.FloatPanel
    protected void load(SharedPreferences prefs) {
        super.load(prefs);
        this.mVanishingTime = prefs.getInt(ICON_VANISHING_TIME, -1);
        this.mSize = Math.max(0, Math.min(12, prefs.getInt(ICON_SIZE, 0)));
    }

    @Override // android.ext.FloatPanel
    public void setLayoutAlpha(float alpha) {
        this.speedPanelParams.alpha = alpha;
        super.setLayoutAlpha(alpha);
        TimeJumpPanel panel = MainService.instance.timeJumpPanel;
        if (panel != null) {
            panel.setLayoutAlpha(alpha);
        }
    }
}
