/*
 * Copyright (C) 2013 Manuel Peinado
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manuelpeinado.quickreturnheader;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.cyrilmottier.android.translucentactionbar.NotifyingScrollView;
import com.manuelpeinado.quickreturnheader.ListViewScrollObserver.OnListViewScrollListener;

public class QuickReturnHeaderHelper implements OnGlobalLayoutListener {
    protected static final String TAG = "QuickReturnHeaderHelper";
    private View realHeader;
    private FrameLayout.LayoutParams realHeaderLayoutParams;
	private View realFooter;
	private FrameLayout.LayoutParams realFooterLayoutParams;
    private int headerHeight;
    private int headerTop;
    private int footerHeight;
    private int footerTop;
    private View dummyHeader;
    private View dummyFooter;
    private int contentResId;
    private int headerResId;
    private int footerResId;
    private boolean waitingForExactHeaderHeight = true;
    private boolean waitingForExactFooterHeight = true;
    private Context context;
    private ListView listView;
    private LayoutInflater inflater;
    private View content;
    private ViewGroup mContentContainer;
    private ViewGroup root;
    private int lastTop;
    private boolean headerSnapped = true;
    private boolean footerSnapped = true;
    private OnSnappedChangeListener onHeaderSnappedChangeListener, onFooterSnappedChangeListener;
    private Animation headerAnimation;
    private Animation footerAnimation;
    /**
     * True if the last scroll movement was in the "up" direction.
     */
    private boolean scrollingUp;
    /**
     * Maximum time it takes the show/hide animation to complete. Maximum because it will take much less time if the
     * header is already partially hidden or shown.
     * <p>
     * In milliseconds.
     */
    private static final long ANIMATION_DURATION = 400;

    public interface OnSnappedChangeListener {
        void onSnappedChange(boolean snapped);
    }

    public QuickReturnHeaderHelper(Context context, int contentResId, int headerResId) {
        this.context = context;
        this.contentResId = contentResId;
        this.headerResId = headerResId;
    }

    public QuickReturnHeaderHelper(Context context, int contentResId, int headerResId, int footerResId) {
        this.context = context;
        this.contentResId = contentResId;
        this.headerResId = headerResId;
		this.footerResId = footerResId;
    }

    public View createView() {
        inflater = LayoutInflater.from(context);
        content = inflater.inflate(contentResId, null);

		FrameLayout dummyFrameLayout = new FrameLayout(context);
		if(headerResId != 0){
			realHeader = inflater.inflate(headerResId, dummyFrameLayout, false);
			realHeaderLayoutParams = (FrameLayout.LayoutParams) realHeader.getLayoutParams();
			realHeaderLayoutParams.gravity = Gravity.TOP;
		}

		if(footerResId != 0){
			realFooter = inflater.inflate(footerResId, dummyFrameLayout, false);
			realFooterLayoutParams = (FrameLayout.LayoutParams) realFooter.getLayoutParams();
			realFooterLayoutParams.gravity = Gravity.BOTTOM;
		}

        // Use measured height here as an estimate of the header height, later on after the layout is complete
        // we'll use the actual height
		if(realHeader != null){
			int widthMeasureSpec = MeasureSpec.makeMeasureSpec(realHeaderLayoutParams.width, MeasureSpec.EXACTLY);
			int heightMeasureSpec = MeasureSpec.makeMeasureSpec(realHeaderLayoutParams.height, MeasureSpec.EXACTLY);
			realHeader.measure(widthMeasureSpec, heightMeasureSpec);
			headerHeight = realHeader.getMeasuredHeight();
		}
		if(realFooter != null){
			int widthMeasureSpec = MeasureSpec.makeMeasureSpec(realFooterLayoutParams.width, MeasureSpec.EXACTLY);
			int heightMeasureSpec = MeasureSpec.makeMeasureSpec(realFooterLayoutParams.height, MeasureSpec.EXACTLY);
			realFooter.measure(widthMeasureSpec, heightMeasureSpec);
			footerHeight = realFooter.getMeasuredHeight();
		}

        listView = (ListView) content.findViewById(android.R.id.list);
        if (listView != null) {
            createListView();
        } else {
            createScrollView();
        }
        return root;
    }

    public void setOnHeaderSnappedChangeListener(OnSnappedChangeListener onSnapListener) {
        this.onHeaderSnappedChangeListener = onSnapListener;
    }

    public void setOnFooterSnappedChangeListener(OnSnappedChangeListener onSnapListener) {
        this.onFooterSnappedChangeListener = onSnapListener;
    }

    private void createListView() {
        root = (FrameLayout) inflater.inflate(R.layout.qrh__listview_container, null);
        root.addView(content);

        listView.getViewTreeObserver().addOnGlobalLayoutListener(this);
		listView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        ListViewScrollObserver observer = new ListViewScrollObserver(listView);
        //        listView.setOnScrollListener(this);
        observer.setOnScrollUpAndDownListener(new OnListViewScrollListener() {
            @Override
            public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                onNewScrollHeader(delta);
                onNewScrollFooter(delta);

                headerSnap(headerTop == scrollPosition);
                footerSnap(footerTop == scrollPosition);
            }

            @Override
            public void onScrollIdle() {
                QuickReturnHeaderHelper.this.onHeaderScrollIdle();
                QuickReturnHeaderHelper.this.onFooterScrollIdle();
            }
        });


		if(realHeader != null){
			root.addView(realHeader, realHeaderLayoutParams);
			dummyHeader = new View(context);
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, headerHeight);
			dummyHeader.setLayoutParams(params);
			listView.addHeaderView(dummyHeader);
		}

		if(realFooter != null){
			root.addView(realFooter, realFooterLayoutParams);
			dummyFooter = new View(context);
			AbsListView.LayoutParams params2 = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, footerHeight);
			dummyFooter.setLayoutParams(params2);
			listView.addFooterView(dummyFooter);
		}
    }

    private void createScrollView() {
        root = (FrameLayout) inflater.inflate(R.layout.qrh__scrollview_container, null);

        NotifyingScrollView scrollView = (NotifyingScrollView) root.findViewById(R.id.rqh__scroll_view);
        scrollView.setOnScrollChangedListener(mOnScrollChangedListener);
		scrollView.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);

		if(realHeader != null){
			root.addView(realHeader, realHeaderLayoutParams);
		}
		if(realFooter != null){
			root.addView(realFooter, realFooterLayoutParams);
		}

        mContentContainer = (ViewGroup) root.findViewById(R.id.rqh__container);
        mContentContainer.addView(content, 1);

		if(realHeader != null){
			dummyHeader = mContentContainer.findViewById(R.id.rqh__content_top_margin);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, headerHeight);
			dummyHeader.setLayoutParams(params);
		}
		if(realFooter != null){
			dummyFooter = mContentContainer.findViewById(R.id.rqh__content_bottom_margin);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, footerHeight);
			dummyFooter.setLayoutParams(params);
		}
    }

    private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {
        @Override
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            if (t < 0) {
                onNewScrollHeader(headerHeight - headerTop);
                onNewScrollFooter(footerHeight - footerTop);
            } else {
                onNewScrollHeader(lastTop - t);
                onNewScrollFooter(lastTop - t);
            }
            if (t <= 0) {
                headerTop = 0;
				footerTop = 0;
            }
            headerSnap(headerTop <= - t);
			footerSnap(footerTop <= - t);
            lastTop = t;
        }
        
        @Override
        public void onScrollIdle() {
            QuickReturnHeaderHelper.this.onHeaderScrollIdle();
            QuickReturnHeaderHelper.this.onFooterScrollIdle();
        }
    };

    /**
     * Invoked when the user stops scrolling the content. In response we might start an animation to leave the header in
     * a fully open or fully closed state.
     */
    private void onHeaderScrollIdle() {
        if (headerSnapped) {
            // Only animate when header is out of its natural position (truly over the content).
            return;
        }
        if (headerTop > 0 || headerTop <= -headerHeight) {
            // Fully hidden, to need to animate.
            return;
        }
        if (scrollingUp) {
            hideHeader();
        } else {
            showHeader();
        }
    }

    /**
     * Invoked when the user stops scrolling the content. In response we might start an animation to leave the header in
     * a fully open or fully closed state.
     */
    private void onFooterScrollIdle() {
        if (footerSnapped) {
            // Only animate when header is out of its natural position (truly over the content).
            return;
        }
        if (footerTop > 0 || footerTop <= - footerHeight) {
            // Fully hidden, to need to animate.
            return;
        }
        if (scrollingUp) {
            hideFooter();
        } else {
            showFooter();
        }
    }

    /**
     * Shows the header using a simple downwards translation animation.
     */
    private void showHeader() {
        animateHeader(headerTop, 0);
    }

    /**
     * Hides the header using a simple upwards translation animation.
     */
    private void hideHeader() {
        animateHeader(headerTop, -headerHeight);
    }

    /**
     * Shows the footer using a simple downwards translation animation.
     */
    private void showFooter() {
        animateFooter(footerTop, 0);
//        animateFooter(root.getHeight(), root.getHeight() - footerHeight);
    }

    /**
     * Hides the footer using a simple upwards translation animation.
     */
    private void hideFooter() {
//        animateFooter(root.getHeight() - footerHeight, root.getHeight());
        animateFooter(footerTop, -footerHeight);
    }

    /**
     * Animates the marginTop property of the header between two specified values.
     * @param startTop Initial value for the marginTop property.
     * @param endTop End value for the marginTop property.
     */
    private void animateHeader(final float startTop, float endTop) {
		if(realHeader == null){
			return;
		}

		System.out.println(String.format("animate header from: %.2f to %.2f", startTop, endTop));
        cancelHeaderAnimation();
        final float deltaTop = endTop - startTop;
        headerAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                headerTop = (int) (startTop + deltaTop * interpolatedTime);
                realHeaderLayoutParams.topMargin = headerTop;
                realHeader.setLayoutParams(realHeaderLayoutParams);
            }
        };
        long duration = (long) (deltaTop / (float) headerHeight * ANIMATION_DURATION);
        headerAnimation.setDuration(Math.abs(duration));
        realHeader.startAnimation(headerAnimation);
    }

    /**
     * Animates the marginTop property of the footer between two specified values.
     * @param startTop Initial value for the marginTop property.
     * @param endTop End value for the marginTop property.
     */
    private void animateFooter(final float startTop, float endTop) {
		if(realFooter == null){
			return;
		}

//		System.out.println(String.format("animate footer from: %.2f to %.2f", startTop, endTop));
        cancelFooterAnimation();
        final float deltaTop = endTop - startTop;
        footerAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                footerTop = (int) (startTop + deltaTop * interpolatedTime);
                realFooterLayoutParams.bottomMargin = footerTop;
                realFooter.setLayoutParams(realFooterLayoutParams);
            }
        };
        long duration = (long) (deltaTop / (float) footerHeight * ANIMATION_DURATION);
		footerAnimation.setDuration(Math.abs(duration));
        realFooter.startAnimation(footerAnimation);
    }

    private void cancelAnimation() {
       	cancelHeaderAnimation();
		cancelFooterAnimation();
    }

	private void cancelHeaderAnimation(){
		if (headerAnimation != null) {
			realHeader.clearAnimation();
			headerAnimation = null;
		}
	}

	private void cancelFooterAnimation(){
		if (footerAnimation != null) {
			realFooter.clearAnimation();
			footerAnimation = null;
		}
	}

    private void onNewScrollHeader(int delta) {
		if(realHeader == null){
			return;
		}

        cancelAnimation();
        if (delta > 0) {
            if (headerTop + delta > 0) {
                delta = -headerTop;
            }
        } else if (delta < 0) {
            if (headerTop + delta < -headerHeight) {
                delta = -(headerHeight + headerTop);
            }
        } else {
            return;
        }
        scrollingUp = delta < 0;
        Log.v(TAG, "delta=" + delta);
        headerTop += delta;
        // I'm aware that offsetTopAndBottom is more efficient, but it gave me trouble when scrolling to the bottom of the list
        if (realHeaderLayoutParams.topMargin != headerTop) {
            realHeaderLayoutParams.topMargin = headerTop;
            Log.v(TAG, "topMargin=" + headerTop);
            realHeader.setLayoutParams(realHeaderLayoutParams);
        }
    }

    private void onNewScrollFooter(int delta) {
		if(realFooter == null){
			return;
		}

        cancelAnimation();
        if (delta > 0) {
            if (footerTop + delta > 0) {
                delta = - footerTop;
            }
        } else if (delta < 0) {
            if (footerTop + delta < - footerHeight) {
                delta = -(footerHeight + footerTop);
            }
        } else {
            return;
        }
        scrollingUp = delta < 0;
        Log.v(TAG, "delta=" + delta);
        footerTop += delta;
        // I'm aware that offsetTopAndBottom is more efficient, but it gave me trouble when scrolling to the bottom of the list
        if (realFooterLayoutParams.bottomMargin != footerTop) {
            realFooterLayoutParams.bottomMargin = footerTop;
            Log.v(TAG, "topMargin=" + footerTop);
            realFooter.setLayoutParams(realFooterLayoutParams);
        }
    }

    private void headerSnap(boolean newValue) {
        if (headerSnapped == newValue) {
            return;
        }
        headerSnapped = newValue;
        if (onHeaderSnappedChangeListener != null) {
            onHeaderSnappedChangeListener.onSnappedChange(headerSnapped);
        }
        Log.v(TAG, "header snapped=" + headerSnapped);
    }

    private void footerSnap(boolean newValue) {
        if (footerSnapped == newValue) {
            return;
        }
        footerSnapped = newValue;
        if (onFooterSnappedChangeListener != null) {
            onFooterSnappedChangeListener.onSnappedChange(footerSnapped);
        }
        Log.v(TAG, "footer snapped=" + footerSnapped);
    }

    @Override
    public void onGlobalLayout() {
        if (waitingForExactHeaderHeight && dummyHeader.getHeight() > 0) {
            headerHeight = dummyHeader.getHeight();
            waitingForExactHeaderHeight = false;
            LayoutParams params = dummyHeader.getLayoutParams();
            params.height = headerHeight;
            dummyHeader.setLayoutParams(params);
        }
        if (waitingForExactFooterHeight && dummyFooter.getHeight() > 0) {
            footerHeight = dummyFooter.getHeight();
            waitingForExactFooterHeight = false;
            LayoutParams params = dummyFooter.getLayoutParams();
            params.height = footerHeight;
            dummyFooter.setLayoutParams(params);
        }
    }
}
