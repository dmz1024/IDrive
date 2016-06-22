package com.trs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;
import com.handmark.pulltorefresh.library.PulltoRefreshPinterest;

/**
 * Created by john on 14-2-18.
 */
public class PullToRefreshPinterest extends PulltoRefreshPinterest{
	private boolean mLoadMoreEnabled = true;
	private OnLoadMoreListener mOnLoadMoreListener;

	public static interface OnLoadMoreListener {
		public void onLoadMore(final PullToRefreshPinterest refreshView);
	}

	public PullToRefreshPinterest(Context context) {
		super(context);
		initialize();
	}

	public PullToRefreshPinterest(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public PullToRefreshPinterest(Context context, Mode mode) {
		super(context, mode);
		initialize();
	}

	public PullToRefreshPinterest(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
		initialize();
	}

	private void initialize(){
		setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				Toast.makeText(getContext(), "On last item visible", Toast.LENGTH_SHORT).show();
//				getFooterLayout().pullToRefresh();
				getFooterLayout().refreshing();
				if(mLoadMoreEnabled){
//					getFooterLayout().releaseToRefresh();
				}
				if(mOnLoadMoreListener != null && mLoadMoreEnabled){
					mOnLoadMoreListener.onLoadMore(PullToRefreshPinterest.this);
				}
			}
		});
	}

	public void onLoadMoreComplete(){
		getFooterLayout().hideAllViews();
	}

	public void setLoadMoreEnabled(boolean enabled){
		this.mLoadMoreEnabled = enabled;
	}

	public void setOnLoadMoreListener(OnLoadMoreListener listener){
		this.mOnLoadMoreListener = listener;
	}
}
