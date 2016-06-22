package com.trs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.trs.mobile.R;

/**
 * Created by john on 14-2-18.
 */
public class PullToRefreshGridView extends com.handmark.pulltorefresh.library.PullToRefreshGridView{
	private View mLoadMoreView;
	private boolean mLoadMoreEnabled = true;
	private OnLoadMoreListener mOnLoadMoreListener;

	public static interface OnLoadMoreListener {
		public void onLoadMore(final PullToRefreshGridView refreshView);
	}

	public PullToRefreshGridView(Context context) {
		super(context);
		initialize();
	}

	public PullToRefreshGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public PullToRefreshGridView(Context context, Mode mode) {
		super(context, mode);
		initialize();
	}

	public PullToRefreshGridView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
		initialize();
	}

	private void initialize(){
		addLoadMoreView();

		setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				if(mOnLoadMoreListener != null && mLoadMoreEnabled){
					mOnLoadMoreListener.onLoadMore(PullToRefreshGridView.this);
				}
			}
		});
	}

	private void addLoadMoreView(){

		FrameLayout layout = new FrameLayout(getContext());

		mLoadMoreView = LayoutInflater.from(getContext()).inflate(R.layout.pulltorefresh_loadingmore, layout, false);
		layout.addView(mLoadMoreView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

//		getRefreshableView().addFooterView(layout);

		setLoadMoreEnabled(false);
	}

	public void onLoadMoreComplete(){
		mLoadMoreView.setVisibility(View.GONE);
	}

	public void setLoadMoreEnabled(boolean enabled){
		this.mLoadMoreEnabled = enabled;
		mLoadMoreView.setVisibility(enabled? View.VISIBLE: View.GONE);
	}

	public void setOnLoadMoreListener(OnLoadMoreListener listener){
		this.mOnLoadMoreListener = listener;
	}
}
