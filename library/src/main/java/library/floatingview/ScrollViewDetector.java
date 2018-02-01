package library.floatingview;

import android.widget.ScrollView;

/**
 * ScrollView滑动监听
 */
public abstract class ScrollViewDetector implements ObservableScrollView.OnScrollChangedListener,ScrollDirectionListener {
    private int mLastScrollY;
    private int mScrollThreshold;
    protected ScrollDirectionListener mScrollDirectionListener;

    @Override
    public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
        boolean isSignificantDelta = Math.abs(t - mLastScrollY) > mScrollThreshold;
        if (isSignificantDelta){
            if (t > mLastScrollY){
                onScrollUp();
            }else {
                onScrollDown();
            }
        }
        mLastScrollY = t;
    }

    public void setScrollThreshold(int scrollThreshold) {
        mScrollThreshold = scrollThreshold;
    }
}
