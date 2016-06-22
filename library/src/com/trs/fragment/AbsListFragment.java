package com.trs.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.etsy.android.grid.ExtendableListView;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.trs.adapter.AbsListAdapter;
import com.trs.mobile.R;
import com.trs.readhistory.ReadHistoryManager;
import com.trs.types.ListItem;
import com.trs.types.Page;
import com.trs.util.log.Log;
import net.endlessstudio.util.Util;

/**
 * Created by john on 13-11-18.
 */
public abstract class AbsListFragment extends AbsUrlFragment implements AdapterView.OnItemClickListener{
	public static String EXTRA_HAS_ADS = "hasads";

	public static final String TAG = "AbsListFragment";
	public static final long AUTO_REFRESH_DURATION = 30 * 60 * 1000;
    public static final int INIT_PAGE = -1;
	private PullToRefreshAdapterViewBase mListView;
	private View mRefreshableView;
	private View mLoadingView;
	private int mCurrentIndex = INIT_PAGE;
	private boolean mIsRefresh;
	private boolean mHasRefresh;
	private AbsListAdapter mAdapter;
	private boolean mHasMore = true;
	private View mContnetView;
	private RelativeLayout topViewContainer;

	private boolean hasAds = true;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int index = (int)id;
		if(id >= 0){
			ListItem item = mAdapter.getItem(index);
			onItemClick(item);
		}
	}

	public void loadData(boolean isRefresh){
		mIsRefresh = isRefresh;

		if(isRefresh){
			mHasRefresh = true;
		}

		//只有在需要缓存, 且没有刷新时, 需要尝试加载本地数据
        if(isRefresh || mAdapter.getCount() == 0){
            mCurrentIndex = INIT_PAGE;
        }
//		int requestPageIndex = isRefresh || mAdapter.getCount() == 0? 1: mCurrentIndex + 1;
		int requestPageIndex = isRefresh || mAdapter.getCount() == 0? 0: mCurrentIndex + 1;
		String requestUrl = getRequestUrl(requestPageIndex);
		if(needCacheResult() && !mHasRefresh){
			loadData(requestUrl);
		}
		else{
			loadDataRemote(requestUrl);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		if(getArguments() != null){
			hasAds = getArguments().getBoolean(EXTRA_HAS_ADS, true);
		}

		mContnetView = inflater.inflate(getViewID(), null);

		mListView = (PullToRefreshAdapterViewBase) mContnetView.findViewById(R.id.list_view);


		mLoadingView = mContnetView.findViewById(R.id.loading_view);

		mRefreshableView = mListView.getRefreshableView();

		if(!(mRefreshableView instanceof ListView) && !(mRefreshableView instanceof ExtendableListView)){
			Log.e(TAG, "ExpandableListView or ExpandablePinterest needed");
			return null;
		}

		((AbsListView) mRefreshableView).setSelector(new ColorDrawable(0x00000000));

		if(canRefresh() && canLoadMore()){
			mListView.setMode(PullToRefreshBase.Mode.BOTH);
		}
		else if(canRefresh() && !canLoadMore()){
			mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		}
		else if(!canRefresh() && canLoadMore()){
			mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
		}

		mListView.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener() {
			@Override
			public void onPullEvent(PullToRefreshBase refreshView, PullToRefreshBase.State state, PullToRefreshBase.Mode direction) {
				if((state == PullToRefreshBase.State.PULL_TO_REFRESH || state == PullToRefreshBase.State.REFRESHING)
						&& direction == PullToRefreshBase.Mode.PULL_FROM_START){

					long lastRefreshTime = ReadHistoryManager.getInstance(getActivity()).getTime(getRequestUrl(0));
					String refreshDisplayTime = lastRefreshTime == 0? "-": Util.getRefreshDisplayTime(lastRefreshTime);
					mListView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(refreshDisplayTime + "更新");
				}
			}
		});

		//有顶部视图.
		topViewContainer = new RelativeLayout(getActivity());
		final View topView = getTopView(topViewContainer);
        if(topView != null && topView.getVisibility()!=View.GONE){
			if(mRefreshableView instanceof ListView){
				((ListView)mRefreshableView).addHeaderView(topViewContainer);
			}
			else if(mRefreshableView instanceof ExtendableListView){
				((ExtendableListView)mRefreshableView).addHeaderView(topViewContainer);
			}

			if(topView.getLayoutParams() == null){
//				topView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
//						ViewGroup.LayoutParams.WRAP_CONTENT));
				topView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
			}

			ImageView deleteBtn = new ImageView(getActivity());
			deleteBtn.setImageResource(R.drawable.icon_delete);
			deleteBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ListView list = (ListView) mListView.getRefreshableView();
					list.removeHeaderView(topViewContainer);
				}
			});
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					getResources().getDimensionPixelOffset(R.dimen.size20),
					getResources().getDimensionPixelOffset(R.dimen.size20));
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			deleteBtn.setLayoutParams(params);
			params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.size5);

			topViewContainer.addView(topView);
			if(hasAds){
				topViewContainer.addView(deleteBtn);
			}
        }

		mAdapter = createAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if(mHasMore){
					loadData(false);
				}
				else{
					Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
					mListView.postDelayed(new Runnable() {
						@Override
						public void run() {
							mListView.onRefreshComplete();
						}
					}, 300);
				}
			}
		});

		mListView.setOnItemClickListener(this);

		return mContnetView;
	}

	public void showLoading(){
		mLoadingView.setVisibility(View.VISIBLE);
	}

	public void hideLoading(){
		mLoadingView.setVisibility(View.GONE);
	}

	public void showRefreshing(){
		mListView.post(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG, "show refreshing");
				mListView.setRefreshing();
			}
		});
	}

	public void hideRefreshing(){
		mListView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mListView.onRefreshComplete();
			}
		}, 700);
	}

	protected boolean needCacheResult(){
		return true;
	}

	public boolean hasContent(){
		return mAdapter.getCount() > 0;
	}

    public void setRefresh(boolean flag){
        mIsRefresh = flag;
    }

	protected void onDataReceived(Page page){
		if(mIsRefresh){
			ReadHistoryManager.getInstance(getActivity()).markAsRead(getRequestUrl(0));
			mAdapter.clear();
		}

		mCurrentIndex = page.getIndex();

//		boolean hasMore = mCurrentIndex < page.getCount();
		boolean hasMore = mCurrentIndex < page.getCount() - 1;
		mHasMore = hasMore && canLoadMore();
		mAdapter.addAll(page.getDataList());
		mAdapter.notifyDataSetChanged();
	}

	protected int getItemCount(){
		return mAdapter.getCount();

	}

	protected ListItem getItem(int index){
		return (ListItem) mAdapter.getItem(index);
	}

	protected boolean isRefresh(){
		return mIsRefresh;
	}

	public void onDataReceived(String result, boolean isCache) throws Exception {
		Page page = createPage(result);
		AbsListFragment.this.onDataReceived(page);
	}

	public void onDataError(Throwable t) {
		if(getActivity() != null){
			t.printStackTrace();
            if(t.getMessage().equals("网络不给力")){
                Toast.makeText(getActivity(), "载入数据失败", Toast.LENGTH_LONG).show();
            } else{
                Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_LONG).show();
            }
		}
	}

	protected void onLoadStart() {
		if(!mListView.isRefreshing() && !hasContent()){
			showLoading();
		}
	}

	protected void onLoadEnd() {
		hideLoading();
		mListView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mListView.onRefreshComplete();
			}
		}, 700);
	}

	public PullToRefreshAdapterViewBase getListView(){
		return mListView;
	}

	public int getCurrentIndex(){
		return mCurrentIndex;
	}

	public AbsListAdapter getAdapter(){
		return mAdapter;
	}

	public boolean canRefresh(){
		return true;
	}

	public boolean canLoadMore(){
		return true;
	}

	protected void onItemClick(ListItem item){
		ReadHistoryManager.getInstance(getActivity()).markAsRead(item);
	};

	@Override
	public void onDisplay() {
        super.onDisplay();
		System.out.println("fragment on display");
        if(autoRefresh()){
            long lastRefreshTime = ReadHistoryManager.getInstance(getActivity()).getTime(getRequestUrl(0));
            if(System.currentTimeMillis() - lastRefreshTime > AUTO_REFRESH_DURATION){
                getListView().setRefreshing(true);
            }
        }
	}

    public boolean autoRefresh(){
        return true;
    }

	abstract protected String getRequestUrl(int requestIndex);
	abstract protected AbsListAdapter createAdapter();

	/**
	 * Required view:
	 * PullToRefreshAdapterViewBase with id: list_view
	 * View with id: loading_view
	 * @return
	 */
	abstract protected int getViewID();
    abstract protected View getTopView(RelativeLayout parent);
	abstract protected void loadData(String url);
	abstract protected void loadDataRemote(String url);
	abstract protected Page createPage(String data) throws Exception;
}
