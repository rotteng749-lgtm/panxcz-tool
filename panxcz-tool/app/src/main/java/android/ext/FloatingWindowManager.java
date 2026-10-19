package android.ext;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import catch_.me_.if_.you_.can_.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import luaj.compiler.Constants;

/* JADX INFO: loaded from: classes.dex */
public class FloatingWindowManager {
    private static final String ABOUT_BUTTON_TITLE = "About";
    private static final int SIZE_CHANGE_THRESHOLD = 5;
    private static final String UI_AUTHOR_COPYRIGHT = "© 2025 UmbraAeterna";
    private static final String UI_AUTHOR_DESCRIPTION = "提示：蓝色文字可复制\n联系作者:\nBiliBili UID:{1144894525[1144894525]}\nQQ：{3774160758[3774160758]}\n更新日志 V1.1.0:\n   修复一些bug\n   增加最小化动画\n   增加指示标\n   性能优化\n";
    private static final String UI_AUTHOR_TITLE = "@UmbraAeterna";
    private static final String UI_AUTHOR_VERSION = "版本: 1.1.0";
    private static final long UPDATE_INTERVAL = 50;
    private ScrollView aboutContainer;
    private final Context context;
    private View currentDialogView;
    private View currentIndicator;
    private final float density;
    private ScheduledExecutorService executorService;
    private View floatingView;
    private final String[] functionTitles;
    private View[] functionViews;
    private int initialHeight;
    private float initialResizeTouchX;
    private float initialResizeTouchY;
    private int initialWidth;
    private final int minHeightPx;
    private final int minWidthPx;
    private ValueAnimator minimizeAnimator;
    private WindowManager.LayoutParams minimizeParams;
    private WindowManager.LayoutParams params;
    private View resizeHandle;
    private ValueAnimator restoreAnimator;
    private final String[] scriptAuthorInfo;
    private final Runnable[][][] switchActions;
    private final String[][] switchDescriptions;
    private final boolean[][] switchStates;
    private final int touchSlop;
    private final WindowManager windowManager;
    private int currentFunctionIndex = 0;
    private boolean isMinimizing = false;
    private boolean isRestoring = false;
    private int cachedWidth = 0;
    private int cachedHeight = 0;
    private View minimizedView = null;
    private int savedFunctionIndex = 0;
    private List<View> indicatorViews = new ArrayList();
    private int lastSelectedIndex = -1;

    /* JADX INFO: Access modifiers changed from: private */
    public int getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getScreenHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public FloatingWindowManager(Context context, String[] functionTitles, String[][] switchDescriptions, Runnable[][][] switchActions, String[] scriptAuthorInfo) {
        this.context = context;
        this.functionTitles = functionTitles;
        this.switchDescriptions = switchDescriptions;
        this.switchActions = switchActions;
        this.scriptAuthorInfo = scriptAuthorInfo;
        this.windowManager = (WindowManager) context.getSystemService("window");
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        this.density = metrics.density;
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.minHeightPx = (int) (this.density * 200.0f);
        this.minWidthPx = (int) (300 * this.density);
        this.switchStates = new boolean[switchDescriptions.length][];
        for (int i = 0; i < switchDescriptions.length; i++) {
            this.switchStates[i] = new boolean[switchDescriptions[i].length];
            Arrays.fill(this.switchStates[i], false);
        }
        int corePoolSize = Math.min(1, 4);
        this.executorService = Executors.newScheduledThreadPool(corePoolSize, new ThreadFactory() { // from class: android.ext.FloatingWindowManager.1
            private final AtomicInteger counter = new AtomicInteger();

            @Override // java.util.concurrent.ThreadFactory
            public Thread newThread(Runnable r) {
                Thread t = new ExceptionHandler.ThreadEx(r, "Window-" + this.counter.incrementAndGet());
                t.setDaemon(true);
                return t;
            }
        });
        context.registerComponentCallbacks(new ComponentCallbacks() { // from class: android.ext.FloatingWindowManager.2
            @Override // android.content.ComponentCallbacks
            public void onConfigurationChanged(Configuration newConfig) {
                FloatingWindowManager.this.repositionAllWindows();
            }

            @Override // android.content.ComponentCallbacks
            public void onLowMemory() {
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void repositionAllWindows() {
        if (this.floatingView != null && this.params != null) {
            repositionWindow(this.floatingView, this.params);
        }
        if (this.minimizedView != null && this.minimizeParams != null) {
            repositionWindow(this.minimizedView, this.minimizeParams);
        }
    }

    private void repositionWindow(View view, WindowManager.LayoutParams params) {
        int screenWidth = getScreenWidth();
        int screenHeight = getScreenHeight();
        params.x = constrainPosition(params.x, screenWidth, params.width);
        params.y = constrainPosition(params.y, screenHeight, params.height);
        try {
            this.windowManager.updateViewLayout(view, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int constrainPosition(int value, int max, int size) {
        return Math.max(0, Math.min(max - size, value));
    }

    public void showFloatingWindow() {
        int i;
        if (this.floatingView != null) {
            this.floatingView.setVisibility(0);
            switchFunction(this.savedFunctionIndex);
            return;
        }
        FrameLayout frameLayout = new FrameLayout(this.context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        frameLayout.setLayerType(2, null);
        GradientDrawable rootBackground = new GradientDrawable();
        rootBackground.setColor(Color.argb(220, 50, 50, 50));
        rootBackground.setCornerRadius(this.density * 16.0f);
        frameLayout.setBackground(rootBackground);
        LinearLayout contentLayout = new LinearLayout(this.context);
        contentLayout.setOrientation(1);
        FrameLayout.LayoutParams contentParams = new FrameLayout.LayoutParams(-1, -1);
        contentParams.setMargins(0, 0, 0, (int) (this.density * 30.0f));
        contentLayout.setLayoutParams(contentParams);
        final LinearLayout topBar = createTopBar();
        contentLayout.addView(topBar);
        LinearLayout horizontalLayout = new LinearLayout(this.context);
        horizontalLayout.setOrientation(0);
        horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1.0f));
        ScrollView functionButtonsColumn = createFunctionButtonsColumn();
        horizontalLayout.addView(functionButtonsColumn);
        ScrollView scrollView = createFunctionScrollView();
        horizontalLayout.addView(scrollView);
        contentLayout.addView(horizontalLayout);
        frameLayout.addView(contentLayout);
        addResizeHandle(frameLayout);
        this.floatingView = frameLayout;
        if (Build.VERSION.SDK_INT >= 26) {
            i = 2038;
        } else {
            i = 2002;
        }
        this.params = new WindowManager.LayoutParams(-2, -2, i, 16777480, -3);
        if (this.minimizeParams != null) {
            int width = this.cachedWidth > 0 ? this.cachedWidth : this.minWidthPx;
            int height = this.cachedHeight > 0 ? this.cachedHeight : this.minHeightPx;
            this.params.gravity = 8388659;
            this.params.x = constrainPosition(this.minimizeParams.x, getScreenWidth(), width);
            this.params.y = constrainPosition(this.minimizeParams.y, getScreenHeight(), height);
        } else {
            this.params.gravity = 8388659;
            this.params.x = (int) (this.density * 200.0f);
            this.params.y = (int) (this.density * 200.0f);
        }
        if (this.cachedWidth > 0) {
            this.params.width = this.cachedWidth;
            this.params.height = this.cachedHeight;
        } else {
            this.params.width = (int) (this.density * 300.0f);
            this.params.height = this.minHeightPx;
        }
        topBar.setOnTouchListener(new EnhancedDragListener(frameLayout, this.windowManager, this.params) { // from class: android.ext.FloatingWindowManager.3
            @Override // android.ext.FloatingWindowManager.EnhancedDragListener
            public void onPositionUpdate(int newX, int newY) {
                if (FloatingWindowManager.this.currentDialogView != null && FloatingWindowManager.this.currentDialogView.getParent() != null) {
                    WindowManager.LayoutParams dialogParams = (WindowManager.LayoutParams) FloatingWindowManager.this.currentDialogView.getLayoutParams();
                    dialogParams.x = topBar.getMeasuredWidth() + newX;
                    dialogParams.y = newY;
                    FloatingWindowManager.this.windowManager.updateViewLayout(FloatingWindowManager.this.currentDialogView, dialogParams);
                }
            }

            @Override // android.ext.FloatingWindowManager.EnhancedDragListener
            protected int getScreenWidth() {
                return FloatingWindowManager.this.getScreenWidth();
            }

            @Override // android.ext.FloatingWindowManager.EnhancedDragListener
            protected int getScreenHeight() {
                return FloatingWindowManager.this.getScreenHeight();
            }
        });
        this.windowManager.addView(frameLayout, this.params);
        this.floatingView.post(new Runnable() { // from class: android.ext.FloatingWindowManager$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                FloatingWindowManager.this.lambda$showFloatingWindow$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showFloatingWindow$0() {
        switchFunction(this.savedFunctionIndex);
    }

    private void addResizeHandle(FrameLayout root) {
        this.resizeHandle = new View(this.context) { // from class: android.ext.FloatingWindowManager.4
            private final Paint paint = new Paint();
            private final Path path = new Path();

            {
                this.paint.setColor(Color.argb(200, Constants.MAXSTACK, 210, 50));
                this.paint.setStyle(Paint.Style.FILL);
                this.paint.setAntiAlias(true);
            }

            @Override // android.view.View
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);
                this.path.reset();
                this.path.moveTo(0.0f, h);
                this.path.lineTo(w, h);
                this.path.lineTo(w, 0.0f);
                this.path.close();
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                canvas.drawPath(this.path, this.paint);
            }
        };
        int size = (int) (this.density * 30.0f);
        FrameLayout.LayoutParams handleParams = new FrameLayout.LayoutParams(size, size);
        handleParams.gravity = 8388693;
        this.resizeHandle.setLayoutParams(handleParams);
        this.resizeHandle.setOnTouchListener(new AnonymousClass5());
        root.addView(this.resizeHandle);
    }

    /* JADX INFO: renamed from: android.ext.FloatingWindowManager$5, reason: invalid class name */
    class AnonymousClass5 implements View.OnTouchListener {
        private long lastUpdateTime = 0;

        AnonymousClass5() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case 0:
                    FloatingWindowManager.this.initialResizeTouchX = event.getRawX();
                    FloatingWindowManager.this.initialResizeTouchY = event.getRawY();
                    FloatingWindowManager.this.initialWidth = FloatingWindowManager.this.params.width;
                    FloatingWindowManager.this.initialHeight = FloatingWindowManager.this.params.height;
                    return true;
                case 1:
                    float currentX = event.getRawX();
                    float currentY = event.getRawY();
                    float dx = currentX - FloatingWindowManager.this.initialResizeTouchX;
                    float dy = currentY - FloatingWindowManager.this.initialResizeTouchY;
                    int screenWidthFinal = FloatingWindowManager.this.getScreenWidth();
                    int screenHeightFinal = FloatingWindowManager.this.getScreenHeight();
                    int maxWidthFinal = screenWidthFinal - FloatingWindowManager.this.params.x;
                    int maxHeightFinal = screenHeightFinal - FloatingWindowManager.this.params.y;
                    final int newWidthFinal = Math.max(FloatingWindowManager.this.minWidthPx, Math.min(maxWidthFinal, (int) (FloatingWindowManager.this.initialWidth + dx)));
                    final int newHeightFinal = Math.max(FloatingWindowManager.this.minHeightPx, Math.min(maxHeightFinal, (int) (FloatingWindowManager.this.initialHeight + dy)));
                    ViewGroup.LayoutParams finalParams = FloatingWindowManager.this.floatingView.getLayoutParams();
                    finalParams.width = newWidthFinal;
                    finalParams.height = newHeightFinal;
                    FloatingWindowManager.this.floatingView.setLayoutParams(finalParams);
                    FloatingWindowManager.this.floatingView.post(new Runnable() { // from class: android.ext.FloatingWindowManager$5$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            FloatingWindowManager.AnonymousClass5.this.lambda$onTouch$1(newWidthFinal, newHeightFinal);
                        }
                    });
                    return true;
                case 2:
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - this.lastUpdateTime < FloatingWindowManager.UPDATE_INTERVAL) {
                        return true;
                    }
                    float currentX2 = event.getRawX();
                    float currentY2 = event.getRawY();
                    float dx2 = currentX2 - FloatingWindowManager.this.initialResizeTouchX;
                    float dy2 = currentY2 - FloatingWindowManager.this.initialResizeTouchY;
                    int screenWidth = FloatingWindowManager.this.getScreenWidth();
                    int screenHeight = FloatingWindowManager.this.getScreenHeight();
                    int maxWidth = screenWidth - FloatingWindowManager.this.params.x;
                    int maxHeight = screenHeight - FloatingWindowManager.this.params.y;
                    final int newWidth = Math.max(FloatingWindowManager.this.minWidthPx, Math.min(maxWidth, (int) (FloatingWindowManager.this.initialWidth + dx2)));
                    final int newHeight = Math.max(FloatingWindowManager.this.minHeightPx, Math.min(maxHeight, (int) (FloatingWindowManager.this.initialHeight + dy2)));
                    if (Math.abs(newWidth - FloatingWindowManager.this.params.width) > 5 || Math.abs(newHeight - FloatingWindowManager.this.params.height) > 5) {
                        ViewGroup.LayoutParams layoutParams = FloatingWindowManager.this.floatingView.getLayoutParams();
                        layoutParams.width = newWidth;
                        layoutParams.height = newHeight;
                        FloatingWindowManager.this.floatingView.setLayoutParams(layoutParams);
                        FloatingWindowManager.this.floatingView.post(new Runnable() { // from class: android.ext.FloatingWindowManager$5$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                FloatingWindowManager.AnonymousClass5.this.lambda$onTouch$0(newWidth, newHeight);
                            }
                        });
                        this.lastUpdateTime = currentTime;
                        return true;
                    }
                    return true;
                default:
                    return false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTouch$0(int newWidth, int newHeight) {
            FloatingWindowManager.this.params.width = newWidth;
            FloatingWindowManager.this.params.height = newHeight;
            try {
                FloatingWindowManager.this.windowManager.updateViewLayout(FloatingWindowManager.this.floatingView, FloatingWindowManager.this.params);
                FloatingWindowManager.this.cachedWidth = newWidth;
                FloatingWindowManager.this.cachedHeight = newHeight;
            } catch (Exception e) {
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTouch$1(int newWidthFinal, int newHeightFinal) {
            FloatingWindowManager.this.params.width = newWidthFinal;
            FloatingWindowManager.this.params.height = newHeightFinal;
            try {
                FloatingWindowManager.this.windowManager.updateViewLayout(FloatingWindowManager.this.floatingView, FloatingWindowManager.this.params);
                FloatingWindowManager.this.cachedWidth = newWidthFinal;
                FloatingWindowManager.this.cachedHeight = newHeightFinal;
            } catch (Exception e) {
            }
        }
    }

    private LinearLayout createTopBar() {
        LinearLayout linearLayout = new LinearLayout(this.context);
        linearLayout.setOrientation(0);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, (int) (this.density * 50.0f)));
        GradientDrawable topBarBackground = new GradientDrawable();
        topBarBackground.setColor(Color.argb(255, 60, 60, 60));
        topBarBackground.setCornerRadii(new float[]{this.density * 16.0f, this.density * 16.0f, this.density * 16.0f, this.density * 16.0f, 0.0f, 0.0f, 0.0f, 0.0f});
        linearLayout.setBackground(topBarBackground);
        TextView minimizeButton = createTopButton("−", new View.OnClickListener() { // from class: android.ext.FloatingWindowManager$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FloatingWindowManager.this.lambda$createTopBar$1(view);
            }
        });
        LinearLayout.LayoutParams minimizeParams = new LinearLayout.LayoutParams(-2, -1);
        minimizeParams.gravity = 8388611;
        minimizeButton.setLayoutParams(minimizeParams);
        linearLayout.addView(minimizeButton);
        View spacer = new View(this.context);
        LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(0, 0, 1.0f);
        spacer.setLayoutParams(spacerParams);
        linearLayout.addView(spacer);
        TextView closeButton = createTopButton("×", new View.OnClickListener() { // from class: android.ext.FloatingWindowManager$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FloatingWindowManager.this.showExitDialog(view);
            }
        });
        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(-2, -1);
        closeParams.gravity = 8388613;
        closeButton.setLayoutParams(closeParams);
        linearLayout.addView(closeButton);
        return linearLayout;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createTopBar$1(View v) {
        minimizeFloatingWindow();
    }

    private TextView createTopButton(String text, View.OnClickListener listener) {
        TextView button = new TextView(this.context);
        button.setText(text);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.context.getResources().getDimension(R.dimen.floating_window_top_button_text_size));
        button.setTextColor(-1);
        int padStart = this.context.getResources().getDimensionPixelSize(R.dimen.floating_window_padding_10);
        int padEnd = this.context.getResources().getDimensionPixelSize(R.dimen.floating_window_padding_20);
        button.setPadding(padStart, 0, padEnd, 0);
        button.setOnClickListener(listener);
        return button;
    }

    private ScrollView createFunctionButtonsColumn() {
        ScrollView scrollView = new ScrollView(this.context);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams((int) (this.density * 100.0f), -1));
        LinearLayout linearLayout = new LinearLayout(this.context);
        linearLayout.setOrientation(1);
        int i = -2;
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        linearLayout.setBackgroundColor(Color.argb(200, 40, 40, 40));
        linearLayout.setPadding(0, (int) (this.density * 5.0f), 0, (int) (this.density * 10.0f));
        GradientDrawable indicatorBg = new GradientDrawable();
        indicatorBg.setColor(Color.parseColor("#4FC3F7"));
        indicatorBg.setCornerRadii(new float[]{this.density * 4.0f, this.density * 4.0f, this.density * 4.0f, this.density * 4.0f, this.density * 4.0f, this.density * 4.0f, this.density * 4.0f, this.density * 4.0f});
        int i2 = 0;
        while (i2 < this.functionTitles.length) {
            FrameLayout buttonContainer = new FrameLayout(this.context);
            LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(-1, i);
            containerParams.setMargins((int) (this.density * 5.0f), (int) (this.density * 5.0f), (int) (this.density * 5.0f), (int) (this.density * 5.0f));
            buttonContainer.setLayoutParams(containerParams);
            TextView functionButton = createFunctionButton(this.functionTitles[i2]);
            final int finalI = i2;
            functionButton.setOnClickListener(new View.OnClickListener() { // from class: android.ext.FloatingWindowManager$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    FloatingWindowManager.this.lambda$createFunctionButtonsColumn$2(finalI, view);
                }
            });
            View indicator = new View(this.context);
            FrameLayout.LayoutParams indicatorParams = new FrameLayout.LayoutParams((int) (this.density * 4.0f), -1);
            indicatorParams.gravity = 8388611;
            indicator.setLayoutParams(indicatorParams);
            indicator.setBackground(indicatorBg);
            indicator.setVisibility(4);
            buttonContainer.addView(functionButton);
            buttonContainer.addView(indicator);
            linearLayout.addView(buttonContainer);
            this.indicatorViews.add(indicator);
            if (i2 == 0) {
                indicator.setVisibility(0);
                this.currentIndicator = indicator;
                this.lastSelectedIndex = 0;
            }
            i2++;
            i = -2;
        }
        FrameLayout aboutButtonContainer = new FrameLayout(this.context);
        LinearLayout.LayoutParams aboutContainerParams = new LinearLayout.LayoutParams(-1, -2);
        aboutContainerParams.setMargins((int) (this.density * 5.0f), (int) (this.density * 15.0f), (int) (this.density * 5.0f), 0);
        aboutButtonContainer.setLayoutParams(aboutContainerParams);
        TextView aboutButton = createFunctionButton(ABOUT_BUTTON_TITLE);
        aboutButton.setOnClickListener(new View.OnClickListener() { // from class: android.ext.FloatingWindowManager$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FloatingWindowManager.this.lambda$createFunctionButtonsColumn$3(view);
            }
        });
        View aboutIndicator = new View(this.context);
        FrameLayout.LayoutParams aboutIndicatorParams = new FrameLayout.LayoutParams((int) (this.density * 4.0f), -1);
        aboutIndicatorParams.gravity = 8388611;
        aboutIndicator.setLayoutParams(aboutIndicatorParams);
        aboutIndicator.setBackground(indicatorBg);
        aboutIndicator.setVisibility(4);
        aboutButtonContainer.addView(aboutButton);
        aboutButtonContainer.addView(aboutIndicator);
        linearLayout.addView(aboutButtonContainer);
        this.indicatorViews.add(aboutIndicator);
        scrollView.addView(linearLayout);
        return scrollView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createFunctionButtonsColumn$2(int finalI, View v) {
        moveIndicatorToButton(finalI);
        switchFunction(finalI);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createFunctionButtonsColumn$3(View v) {
        moveIndicatorToButton(this.functionTitles.length);
        showAboutView();
        this.currentFunctionIndex = -1;
    }

    private void moveIndicatorToButton(int index) {
        if (index == this.lastSelectedIndex) {
            return;
        }
        if (this.currentIndicator != null) {
            this.currentIndicator.setVisibility(4);
        }
        if (index >= 0 && index < this.indicatorViews.size()) {
            View newIndicator = this.indicatorViews.get(index);
            newIndicator.setVisibility(0);
            this.currentIndicator = newIndicator;
            newIndicator.setScaleY(0.8f);
            newIndicator.animate().scaleY(1.0f).setDuration(300L).setInterpolator(new OvershootInterpolator()).start();
        }
        this.lastSelectedIndex = index;
    }

    private TextView createFunctionButton(String text) {
        final TextView button = new TextView(this.context);
        button.setText(text);
        button.setTextColor(-1);
        button.setPadding((int) (this.density * 5.0f), (int) (this.density * 12.0f), (int) (this.density * 5.0f), (int) (this.density * 12.0f));
        button.setGravity(17);
        final GradientDrawable buttonBg = new GradientDrawable();
        buttonBg.setColor(Color.argb(150, 50, 50, 50));
        buttonBg.setCornerRadius(this.density * 8.0f);
        button.setBackground(buttonBg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        button.setLayoutParams(params);
        button.setOnTouchListener(new View.OnTouchListener() { // from class: android.ext.FloatingWindowManager$$ExternalSyntheticLambda0
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return FloatingWindowManager.lambda$createFunctionButton$4(buttonBg, button, view, motionEvent);
            }
        });
        return button;
    }

    static /* synthetic */ boolean lambda$createFunctionButton$4(GradientDrawable buttonBg, TextView button, View v, MotionEvent event) {
        boolean insideButton = false;
        switch (event.getAction()) {
            case 0:
                buttonBg.setColor(Color.argb(200, 100, 100, 100));
                button.setBackground(buttonBg);
                return true;
            case 1:
                buttonBg.setColor(Color.argb(150, 50, 50, 50));
                button.setBackground(buttonBg);
                v.performClick();
                return true;
            case 2:
                float x = event.getX();
                float y = event.getY();
                if (x >= 0.0f && x <= v.getWidth() && y >= 0.0f && y <= v.getHeight()) {
                    insideButton = true;
                }
                if (insideButton) {
                    buttonBg.setColor(Color.argb(200, 100, 100, 100));
                } else {
                    buttonBg.setColor(Color.argb(150, 50, 50, 50));
                }
                button.setBackground(buttonBg);
                return true;
            case 3:
                buttonBg.setColor(Color.argb(150, 50, 50, 50));
                button.setBackground(buttonBg);
                return true;
            default:
                return false;
        }
    }

    private ScrollView createFunctionScrollView() {
        Runnable onAction;
        LinearLayout actionLayout;
        int j;
        int i;
        int i2;
        ScrollView scrollView = new ScrollView(this.context);
        int i3 = -1;
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        LinearLayout linearLayout = new LinearLayout(this.context);
        int i4 = 1;
        linearLayout.setOrientation(1);
        scrollView.addView(linearLayout);
        View[] viewArr = new View[this.switchDescriptions.length + 1];
        int i5 = 0;
        while (i5 < this.switchDescriptions.length) {
            LinearLayout linearLayout2 = new LinearLayout(this.context);
            linearLayout2.setOrientation(i4);
            float f = 5.0f;
            int i6 = 0;
            linearLayout2.setPadding(0, (int) (this.density * 5.0f), 0, 0);
            int j2 = 0;
            while (j2 < this.switchDescriptions[i5].length) {
                LinearLayout linearLayout3 = new LinearLayout(this.context);
                linearLayout3.setOrientation(i6);
                linearLayout3.setLayoutParams(new ViewGroup.LayoutParams(i3, (int) (this.density * 48.0f)));
                linearLayout3.setPadding(i6, (int) (this.density * f), i6, (int) (this.density * f));
                final int currentI = i5;
                final int currentJ = j2;
                Runnable onAction2 = null;
                Runnable offAction = null;
                if (this.switchActions != null && currentI < this.switchActions.length && currentJ < this.switchActions[currentI].length && this.switchActions[currentI][currentJ] != null) {
                    if (this.switchActions[currentI][currentJ].length > 0) {
                        onAction2 = this.switchActions[currentI][currentJ][i6];
                    }
                    if (this.switchActions[currentI][currentJ].length <= i4) {
                        onAction = onAction2;
                    } else {
                        offAction = this.switchActions[currentI][currentJ][i4];
                        onAction = onAction2;
                    }
                } else {
                    onAction = null;
                }
                if (((onAction == null || offAction != null) ? i6 : i4) == 0) {
                    TextView descriptionView = new TextView(this.context);
                    descriptionView.setText(this.switchDescriptions[currentI][currentJ]);
                    descriptionView.setTextColor(i3);
                    descriptionView.setPadding((int) (this.density * 10.0f), 0, (int) (this.density * 10.0f), 0);
                    descriptionView.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
                    linearLayout3.addView(descriptionView);
                    Switch switchView = new Switch(this.context);
                    switchView.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
                    switchView.setChecked(this.switchStates[i5][j2]);
                    if (onAction == null && offAction == null) {
                        j = j2;
                        i = 0;
                        i2 = 1;
                        actionLayout = linearLayout3;
                    } else {
                        final Runnable finalOnAction = onAction;
                        final Runnable finalOffAction = offAction;
                        actionLayout = linearLayout3;
                        j = j2;
                        i = 0;
                        i2 = 1;
                        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: android.ext.FloatingWindowManager$$ExternalSyntheticLambda1
                            @Override // android.widget.CompoundButton.OnCheckedChangeListener
                            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                                FloatingWindowManager.this.lambda$createFunctionScrollView$7(currentI, currentJ, finalOnAction, finalOffAction, compoundButton, z);
                            }
                        });
                    }
                    actionLayout.addView(switchView);
                } else {
                    final Button actionButton = new Button(this.context);
                    actionButton.setText(this.switchDescriptions[currentI][currentJ]);
                    actionButton.setTextColor(i3);
                    actionButton.setBackgroundColor(Color.argb(i6, i6, i6, i6));
                    actionButton.setGravity(8388627);
                    actionButton.setPadding((int) (this.density * 16.0f), i6, i6, i6);
                    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(i6, i3, 1.0f);
                    buttonParams.gravity = 16;
                    actionButton.setLayoutParams(buttonParams);
                    actionButton.setOnTouchListener(new View.OnTouchListener() { // from class: android.ext.FloatingWindowManager$$ExternalSyntheticLambda12
                        @Override // android.view.View.OnTouchListener
                        public final boolean onTouch(View view, MotionEvent motionEvent) {
                            return FloatingWindowManager.lambda$createFunctionScrollView$5(actionButton, view, motionEvent);
                        }
                    });
                    final Runnable finalOnAction1 = onAction;
                    actionButton.setOnClickListener(new View.OnClickListener() { // from class: android.ext.FloatingWindowManager$$ExternalSyntheticLambda13
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            FloatingWindowManager.this.lambda$createFunctionScrollView$6(finalOnAction1, view);
                        }
                    });
                    linearLayout3.addView(actionButton);
                    linearLayout3.setGravity(1);
                    actionLayout = linearLayout3;
                    j = j2;
                    i2 = 1;
                    i = 0;
                }
                linearLayout2.addView(actionLayout);
                j2 = j + 1;
                i6 = i;
                i4 = i2;
                i3 = -1;
                f = 5.0f;
            }
            int i7 = i4;
            viewArr[i5] = linearLayout2;
            linearLayout.addView(linearLayout2);
            if (i5 != 0) {
                linearLayout2.setVisibility(8);
            }
            i5++;
            i4 = i7;
            i3 = -1;
        }
        ScrollView aboutPlaceholder = new ScrollView(this.context);
        aboutPlaceholder.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        aboutPlaceholder.setVisibility(8);
        viewArr[this.switchDescriptions.length] = aboutPlaceholder;
        linearLayout.addView(aboutPlaceholder);
        this.functionViews = viewArr;
        return scrollView;
    }

    static /* synthetic */ boolean lambda$createFunctionScrollView$5(Button actionButton, View v, MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                actionButton.setBackgroundColor(Color.argb(255, 100, 100, 100));
                return true;
            case 1:
                actionButton.setBackgroundColor(Color.argb(0, 0, 0, 0));
                v.performClick();
                return true;
            case 2:
                float x = event.getX();
                float y = event.getY();
                boolean insideButton = x >= 0.0f && x <= ((float) v.getWidth()) && y >= 0.0f && y <= ((float) v.getHeight());
                if (insideButton) {
                    actionButton.setBackgroundColor(Color.argb(255, 100, 100, 100));
                } else {
                    actionButton.setBackgroundColor(Color.argb(0, 0, 0, 0));
                }
                return true;
            case 3:
                actionButton.setBackgroundColor(Color.argb(0, 0, 0, 0));
                return true;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createFunctionScrollView$6(Runnable finalOnAction1, View v) {
        if (this.executorService != null && !this.executorService.isShutdown()) {
            this.executorService.execute(finalOnAction1);
        } else {
            finalOnAction1.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createFunctionScrollView$7(int currentI, int currentJ, Runnable finalOnAction, Runnable finalOffAction, CompoundButton buttonView, boolean isChecked) {
        this.switchStates[currentI][currentJ] = isChecked;
        Runnable action = isChecked ? finalOnAction : finalOffAction;
        if (action == null) {
            return;
        }
        if (this.executorService != null && !this.executorService.isShutdown()) {
            this.executorService.execute(action);
        } else {
            action.run();
        }
    }

    private void showAboutView() {
        moveIndicatorToButton(this.functionTitles.length);
        for (int i = 0; i < this.functionTitles.length; i++) {
            this.functionViews[i].setVisibility(8);
        }
        if (this.aboutContainer == null) {
            this.aboutContainer = new ScrollView(this.context);
            this.aboutContainer.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            this.aboutContainer.setPadding((int) (this.density * 10.0f), (int) (this.density * 10.0f), (int) (this.density * 10.0f), (int) (this.density * 10.0f));
            LinearLayout aboutContent = new LinearLayout(this.context);
            aboutContent.setOrientation(1);
            this.aboutContainer.addView(aboutContent);
            if (this.scriptAuthorInfo != null && this.scriptAuthorInfo.length >= 4) {
                addAuthorSection(aboutContent, "脚本作者信息", this.scriptAuthorInfo[0], this.scriptAuthorInfo[1], this.scriptAuthorInfo[2], this.scriptAuthorInfo[3]);
            } else {
                addAuthorSection(aboutContent, "脚本作者信息", "脚本功能", "版本: 1.0.0", "脚本说明", "© 2025 脚本作者");
            }
            addDivider(aboutContent);
            addAuthorSection(aboutContent, "UI作者信息", UI_AUTHOR_TITLE, UI_AUTHOR_VERSION, UI_AUTHOR_DESCRIPTION, UI_AUTHOR_COPYRIGHT);
        }
        ViewGroup aboutPlaceholder = (ViewGroup) this.functionViews[this.functionTitles.length];
        if (this.aboutContainer.getParent() != null) {
            ((ViewGroup) this.aboutContainer.getParent()).removeView(this.aboutContainer);
        }
        aboutPlaceholder.removeAllViews();
        aboutPlaceholder.addView(this.aboutContainer);
        aboutPlaceholder.setVisibility(0);
    }

    private void addAuthorSection(LinearLayout container, String sectionTitle, String title, String version, String description, String copyright) {
        int zero = 0;
        TextView sectionTitleView = new TextView(this.context);
        sectionTitleView.setText(sectionTitle);
        sectionTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.context.getResources().getDimension(R.dimen.floating_window_section_title_text_size));
        sectionTitleView.setTextColor(-1);
        sectionTitleView.setTypeface(null, 1);
        int sectionBottomPadding = this.context.getResources().getDimensionPixelSize(R.dimen.floating_window_padding_10);
        sectionTitleView.setPadding(zero, zero, zero, sectionBottomPadding);
        container.addView(sectionTitleView);
        TextView titleView = new TextView(this.context);
        setTextViewWithClickableCopy(titleView, title);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.context.getResources().getDimension(R.dimen.floating_window_title_text_size));
        titleView.setTextColor(-1);
        int titleBottomPadding = this.context.getResources().getDimensionPixelSize(R.dimen.floating_window_padding_5);
        titleView.setPadding(zero, zero, zero, titleBottomPadding);
        container.addView(titleView);
        TextView versionView = new TextView(this.context);
        setTextViewWithClickableCopy(versionView, version);
        versionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.context.getResources().getDimension(R.dimen.floating_window_text_size));
        versionView.setTextColor(-3355444);
        container.addView(versionView);
        addDivider(container);
        TextView descriptionView = new TextView(this.context);
        setTextViewWithClickableCopy(descriptionView, description);
        descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.context.getResources().getDimension(R.dimen.floating_window_text_size));
        descriptionView.setTextColor(-1);
        int descriptionBottomPadding = this.context.getResources().getDimensionPixelSize(R.dimen.floating_window_padding_10);
        descriptionView.setPadding(zero, zero, zero, descriptionBottomPadding);
        container.addView(descriptionView);
        TextView copyrightView = new TextView(this.context);
        setTextViewWithClickableCopy(copyrightView, copyright);
        copyrightView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.context.getResources().getDimension(R.dimen.floating_window_small_text_size));
        copyrightView.setTextColor(Color.argb(180, 200, 200, 200));
        container.addView(copyrightView);
    }

    private void setTextViewWithClickableCopy(TextView textView, String text) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        Pattern pattern = Pattern.compile("\\{([^\\[\\]]*?)\\[([^\\[\\]]*?)]\\}");
        Matcher matcher = pattern.matcher(text);
        int lastIndex = 0;
        while (matcher.find()) {
            ssb.append((CharSequence) text.substring(lastIndex, matcher.start()));
            lastIndex = matcher.end();
            String displayText = matcher.group(1);
            final String copyText = matcher.group(2);
            int start = ssb.length();
            ssb.append((CharSequence) (displayText != null ? displayText : ""));
            int end = ssb.length();
            ClickableSpan clickableSpan = new ClickableSpan() { // from class: android.ext.FloatingWindowManager.6
                @Override // android.text.style.ClickableSpan
                public void onClick(View widget) {
                    ClipboardManager clipboard = (ClipboardManager) FloatingWindowManager.this.context.getSystemService("clipboard");
                    ClipData clip = ClipData.newPlainText("copied_text", copyText);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(FloatingWindowManager.this.context, "已复制: " + copyText, 0).show();
                }

                @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            ssb.setSpan(clickableSpan, start, end, 33);
            ssb.setSpan(new ForegroundColorSpan(-16776961), start, end, 33);
            ssb.setSpan(new UnderlineSpan(), start, end, 33);
        }
        ssb.append((CharSequence) text.substring(lastIndex));
        textView.setText(ssb);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(0);
    }

    private void addDivider(LinearLayout container) {
        View divider = new View(this.context);
        LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(-1, (int) (this.density * 1.0f));
        divParams.setMargins(0, (int) (this.density * 10.0f), 0, (int) (this.density * 10.0f));
        divider.setLayoutParams(divParams);
        divider.setBackgroundColor(Color.argb(100, 255, 255, 255));
        container.addView(divider);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchFunction(int index) {
        if (this.currentFunctionIndex == index) {
            return;
        }
        moveIndicatorToButton(index);
        if (this.currentFunctionIndex == -1) {
            this.functionViews[this.functionTitles.length].setVisibility(8);
        } else if (this.currentFunctionIndex >= 0 && this.currentFunctionIndex < this.functionTitles.length) {
            this.functionViews[this.currentFunctionIndex].setVisibility(8);
        }
        if (index >= 0 && index < this.functionTitles.length) {
            this.functionViews[index].setVisibility(0);
        }
        this.currentFunctionIndex = index;
        this.savedFunctionIndex = index;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showExitDialog(View anchorView) {
        int i;
        removeViewIfExists(this.currentDialogView);
        final LinearLayout dialogLayout = new LinearLayout(this.context);
        dialogLayout.setOrientation(1);
        dialogLayout.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        GradientDrawable dialogBg = new GradientDrawable();
        dialogBg.setColor(Color.argb(220, 60, 60, 60));
        dialogBg.setCornerRadius(this.density * 8.0f);
        dialogLayout.setBackground(dialogBg);
        TextView confirmButton = new TextView(this.context);
        confirmButton.setText("确认移除");
        confirmButton.setTextColor(-1);
        confirmButton.setBackgroundColor(Color.argb(255, 80, 80, 80));
        confirmButton.setPadding((int) (this.density * 30.0f), (int) (this.density * 10.0f), (int) (this.density * 30.0f), (int) (this.density * 10.0f));
        confirmButton.setOnClickListener(new View.OnClickListener() { // from class: android.ext.FloatingWindowManager$$ExternalSyntheticLambda10
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FloatingWindowManager.this.lambda$showExitDialog$8(view);
            }
        });
        dialogLayout.addView(confirmButton);
        TextView cancelButton = new TextView(this.context);
        cancelButton.setText("取消");
        cancelButton.setTextColor(-1);
        cancelButton.setBackgroundColor(Color.argb(255, 80, 80, 80));
        cancelButton.setPadding((int) (this.density * 30.0f), (int) (this.density * 10.0f), (int) (this.density * 30.0f), (int) (this.density * 10.0f));
        cancelButton.setOnClickListener(new View.OnClickListener() { // from class: android.ext.FloatingWindowManager$$ExternalSyntheticLambda11
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FloatingWindowManager.this.lambda$showExitDialog$9(dialogLayout, view);
            }
        });
        dialogLayout.addView(cancelButton);
        if (Build.VERSION.SDK_INT >= 26) {
            i = 2038;
        } else {
            i = 2002;
        }
        WindowManager.LayoutParams dialogParams = new WindowManager.LayoutParams(-2, -2, i, 264, -3);
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        dialogParams.gravity = 8388659;
        dialogParams.x = location[0] + anchorView.getWidth();
        dialogParams.y = location[1];
        this.windowManager.addView(dialogLayout, dialogParams);
        this.currentDialogView = dialogLayout;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showExitDialog$8(View v) {
        close();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showExitDialog$9(LinearLayout dialogLayout, View v) {
        removeViewIfExists(dialogLayout);
        this.currentDialogView = null;
    }

    private void minimizeFloatingWindow() {
        if (this.isMinimizing || this.isRestoring || this.minimizedView != null) {
            return;
        }
        removeViewIfExists(this.currentDialogView);
        if (this.floatingView != null) {
            this.savedFunctionIndex = this.currentFunctionIndex;
            this.cachedWidth = this.floatingView.getWidth();
            this.cachedHeight = this.floatingView.getHeight();
            this.isMinimizing = true;
            this.minimizeAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
            this.minimizeAnimator.setDuration(300L);
            this.minimizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.ext.FloatingWindowManager$$ExternalSyntheticLambda9
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    FloatingWindowManager.this.lambda$minimizeFloatingWindow$10(valueAnimator);
                }
            });
            this.minimizeAnimator.addListener(new AnimatorListenerAdapter() { // from class: android.ext.FloatingWindowManager.7
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    FloatingWindowManager.this.floatingView.setVisibility(8);
                    FloatingWindowManager.this.createMinimizedView();
                    FloatingWindowManager.this.isMinimizing = false;
                }
            });
            this.minimizeAnimator.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$minimizeFloatingWindow$10(ValueAnimator animation) {
        float alpha = ((Float) animation.getAnimatedValue()).floatValue();
        this.floatingView.setAlpha(alpha);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createMinimizedView() {
        int i;
        this.minimizedView = new View(this.context);
        this.minimizedView.setLayoutParams(new ViewGroup.LayoutParams((int) (this.density * 60.0f), (int) (this.density * 60.0f)));
        this.minimizedView.setAlpha(0.0f);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(1);
        drawable.setColor(Color.argb(200, 50, 50, 50));
        this.minimizedView.setBackground(drawable);
        int i2 = (int) (this.density * 60.0f);
        int i3 = (int) (this.density * 60.0f);
        if (Build.VERSION.SDK_INT >= 26) {
            i = 2038;
        } else {
            i = 2002;
        }
        this.minimizeParams = new WindowManager.LayoutParams(i2, i3, i, 16777480, -3);
        if (this.params != null) {
            this.minimizeParams.gravity = 8388659;
            this.minimizeParams.x = constrainPosition(this.params.x, getScreenWidth(), (int) (this.density * 60.0f));
            this.minimizeParams.y = constrainPosition(this.params.y, getScreenHeight(), (int) (this.density * 60.0f));
        } else {
            this.minimizeParams.gravity = 8388693;
            this.minimizeParams.x = (int) (this.density * 40.0f);
            this.minimizeParams.y = (int) (this.density * 40.0f);
        }
        this.minimizedView.setOnTouchListener(new EnhancedDragListener(this.minimizedView, this.windowManager, this.minimizeParams) { // from class: android.ext.FloatingWindowManager.8
            @Override // android.ext.FloatingWindowManager.EnhancedDragListener
            protected boolean isClick(float dx, float dy) {
                return Math.abs(dx) < ((float) FloatingWindowManager.this.touchSlop) && Math.abs(dy) < ((float) FloatingWindowManager.this.touchSlop);
            }

            @Override // android.ext.FloatingWindowManager.EnhancedDragListener
            protected int getScreenWidth() {
                return FloatingWindowManager.this.getScreenWidth();
            }

            @Override // android.ext.FloatingWindowManager.EnhancedDragListener
            protected int getScreenHeight() {
                return FloatingWindowManager.this.getScreenHeight();
            }
        });
        this.minimizedView.setOnClickListener(new View.OnClickListener() { // from class: android.ext.FloatingWindowManager$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FloatingWindowManager.this.lambda$createMinimizedView$12(view);
            }
        });
        this.windowManager.addView(this.minimizedView, this.minimizeParams);
        this.minimizedView.animate().alpha(1.0f).setDuration(300L).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createMinimizedView$12(View v) {
        if (this.isMinimizing || this.isRestoring) {
            return;
        }
        this.isRestoring = true;
        removeViewIfExists(this.minimizedView);
        this.minimizedView = null;
        if (this.minimizeParams != null) {
            this.params.x = this.minimizeParams.x;
            this.params.y = this.minimizeParams.y;
        }
        this.floatingView.setVisibility(0);
        this.floatingView.setAlpha(0.0f);
        try {
            this.windowManager.updateViewLayout(this.floatingView, this.params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.restoreAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.restoreAnimator.setDuration(300L);
        this.restoreAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.ext.FloatingWindowManager$$ExternalSyntheticLambda4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                FloatingWindowManager.this.lambda$createMinimizedView$11(valueAnimator);
            }
        });
        this.restoreAnimator.addListener(new AnimatorListenerAdapter() { // from class: android.ext.FloatingWindowManager.9
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                FloatingWindowManager.this.isRestoring = false;
                FloatingWindowManager.this.switchFunction(FloatingWindowManager.this.savedFunctionIndex);
            }
        });
        this.restoreAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createMinimizedView$11(ValueAnimator animation) {
        float alpha = ((Float) animation.getAnimatedValue()).floatValue();
        this.floatingView.setAlpha(alpha);
    }

    public void close() {
        if (this.minimizeAnimator != null && this.minimizeAnimator.isRunning()) {
            this.minimizeAnimator.cancel();
        }
        if (this.restoreAnimator != null && this.restoreAnimator.isRunning()) {
            this.restoreAnimator.cancel();
        }
        removeViewIfExists(this.floatingView);
        removeViewIfExists(this.currentDialogView);
        removeViewIfExists(this.minimizedView);
        removeViewIfExists(this.resizeHandle);
        if (this.executorService != null && !this.executorService.isShutdown()) {
            this.executorService.shutdownNow();
            try {
                if (!this.executorService.awaitTermination(1L, TimeUnit.SECONDS)) {
                    this.executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        this.minimizedView = null;
        this.minimizeParams = null;
        this.cachedWidth = 0;
        this.cachedHeight = 0;
        this.resizeHandle = null;
        this.aboutContainer = null;
        this.executorService = null;
        this.currentIndicator = null;
        this.indicatorViews.clear();
        this.lastSelectedIndex = -1;
        this.savedFunctionIndex = 0;
        if (this.switchStates != null) {
            for (boolean[] arr : this.switchStates) {
                Arrays.fill(arr, false);
            }
        }
    }

    private void removeViewIfExists(View view) {
        if (view != null && this.windowManager != null) {
            try {
                this.windowManager.removeView(view);
            } catch (Exception e) {
            }
        }
    }

    private abstract class EnhancedDragListener implements View.OnTouchListener {
        private float initialTouchX;
        private float initialTouchY;
        private int initialX;
        private int initialY;
        private boolean isDragging = false;
        private final WindowManager.LayoutParams layoutParams;
        private final View targetView;
        private final WindowManager windowManager;

        protected abstract int getScreenHeight();

        protected abstract int getScreenWidth();

        public EnhancedDragListener(View target, WindowManager wm, WindowManager.LayoutParams params) {
            this.targetView = target;
            this.windowManager = wm;
            this.layoutParams = params;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case 0:
                    this.initialTouchX = event.getRawX();
                    this.initialTouchY = event.getRawY();
                    if (this.layoutParams != null) {
                        this.initialX = this.layoutParams.x;
                        this.initialY = this.layoutParams.y;
                    }
                    this.isDragging = false;
                    return true;
                case 1:
                    if (this.isDragging) {
                        this.isDragging = false;
                    } else {
                        float dx = event.getRawX() - this.initialTouchX;
                        float dy = event.getRawY() - this.initialTouchY;
                        if (isClick(dx, dy)) {
                            v.performClick();
                        }
                    }
                    return true;
                case 2:
                    float currentX = event.getRawX();
                    float currentY = event.getRawY();
                    if (!this.isDragging && (Math.abs(currentX - this.initialTouchX) > FloatingWindowManager.this.touchSlop || Math.abs(currentY - this.initialTouchY) > FloatingWindowManager.this.touchSlop)) {
                        this.isDragging = true;
                    }
                    if (this.isDragging && this.layoutParams != null) {
                        this.layoutParams.x = this.initialX + ((int) (currentX - this.initialTouchX));
                        this.layoutParams.y = this.initialY + ((int) (currentY - this.initialTouchY));
                        int maxX = getScreenWidth() - this.targetView.getWidth();
                        int maxY = getScreenHeight() - this.targetView.getHeight();
                        this.layoutParams.x = Math.max(0, Math.min(maxX, this.layoutParams.x));
                        this.layoutParams.y = Math.max(0, Math.min(maxY, this.layoutParams.y));
                        try {
                            this.windowManager.updateViewLayout(this.targetView, this.layoutParams);
                            onPositionUpdate(this.layoutParams.x, this.layoutParams.y);
                            return true;
                        } catch (Exception e) {
                        }
                    }
                    return true;
                default:
                    return false;
            }
        }

        protected boolean isClick(float dx, float dy) {
            return Math.abs(dx) < ((float) FloatingWindowManager.this.touchSlop) && Math.abs(dy) < ((float) FloatingWindowManager.this.touchSlop);
        }

        public void onPositionUpdate(int newX, int newY) {
        }
    }
}
