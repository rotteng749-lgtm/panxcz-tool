package android.ext;

import android.os.Build;
import android.os.Handler;
import android.widget.AbsListView;
import android.widget.ListView;

/* JADX INFO: loaded from: classes.dex */
public class FastScrollerFix implements AbsListView.OnScrollListener, Runnable {
    private int lastState = 0;
    private ListView list;

    public FastScrollerFix(ListView list) {
        this.list = list;
        setFastScrollEnabledInternal(list, false);
        try {
            list.setOnScrollListener(this);
        } catch (Throwable e) {
            Log.badImplementation(e);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        setFastScrollEnabledInternal(this.list, false);
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.lastState = scrollState;
        Handler handler = ThreadManager.getHandlerUiThread();
        handler.removeCallbacks(this);
        if (scrollState != 0) {
            setFastScrollEnabledInternal(this.list, true);
        } else {
            handler.postDelayed(this, 1000L);
        }
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (this.lastState == 0) {
            Handler handler = ThreadManager.getHandlerUiThread();
            handler.removeCallbacks(this);
            handler.postDelayed(this, 1000L);
        }
    }

    public static void setFastScrollEnabled(ListView list, boolean state) {
        if (list != null) {
            list.setScrollBarStyle(AddressItem.FLAG_REVERT);
            setFastScrollEnabledInternal(list, state);
        }
    }

    private static void setFastScrollEnabledInternal(ListView list, boolean state) {
        if (list != null) {
            try {
                list.setFastScrollEnabled(state);
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
            if (Build.VERSION.SDK_INT >= 11) {
                try {
                    list.setFastScrollAlwaysVisible(state);
                } catch (Throwable e2) {
                    Log.badImplementation(e2);
                }
            }
        }
    }
}
