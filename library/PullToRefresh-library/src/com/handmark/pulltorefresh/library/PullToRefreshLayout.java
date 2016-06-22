package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 14-4-21.
 */
public class PullToRefreshLayout extends PullToRefreshBase<FrameLayout>{

    private static final OnRefreshListener<FrameLayout> defaultOnRefreshListener = new OnRefreshListener<FrameLayout>() {
        @Override
        public void onRefresh(PullToRefreshBase<FrameLayout> refreshView) {

        }
    };


    public PullToRefreshLayout(Context context) {
        super(context);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshLayout(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshLayout(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected FrameLayout createRefreshableView(Context context, AttributeSet attrs) {
        FrameLayout frameLayout = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            frameLayout = new InternalWebViewSDK9(context, attrs);
        } else {
            frameLayout = new FrameLayout(context, attrs);
        }
        return frameLayout;
    }

    @Override
    protected boolean isReadyForPullStart() {
        return mRefreshableView.getScrollY() == 0;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        return mRefreshableView.getScrollY() == 0;
    }

    @Override
    protected void onPtrRestoreInstanceState(Bundle savedInstanceState) {
        super.onPtrRestoreInstanceState(savedInstanceState);
//        mRefreshableView.restoreState(savedInstanceState);
    }

    @Override
    protected void onPtrSaveInstanceState(Bundle saveState) {
        super.onPtrSaveInstanceState(saveState);
//        mRefreshableView.saveState(saveState);
    }

    @TargetApi(9)
    final class InternalWebViewSDK9 extends FrameLayout {

        // WebView doesn't always scroll back to it's edge so we add some
        // fuzziness
        static final int OVERSCROLL_FUZZY_THRESHOLD = 2;

        // WebView seems quite reluctant to overscroll so we use the scale
        // factor to scale it's value
        static final float OVERSCROLL_SCALE_FACTOR = 1.5f;

        public InternalWebViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                                       int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(PullToRefreshLayout.this, deltaX, scrollX, deltaY, scrollY,
                    getScrollRange(), OVERSCROLL_FUZZY_THRESHOLD, OVERSCROLL_SCALE_FACTOR, isTouchEvent);

            return returnValue;
        }

        private int getScrollRange() {
            return (int) Math.max(0, FloatMath.floor((float) (mRefreshableView.getHeight() * 1.5))
                    - (getHeight() - getPaddingBottom() - getPaddingTop()));
        }
    }
}
