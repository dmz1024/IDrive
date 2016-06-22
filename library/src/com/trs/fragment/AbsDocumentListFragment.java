package com.trs.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.trs.mobile.R;
import com.trs.types.Page;
import com.trs.types.Topic;

/**
 * Created by john on 14-3-12.
 * Auto load data
 * Create topic view
 */
abstract public class AbsDocumentListFragment extends AbsListFragment {
	private RecommendController mRecommendController;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		new Handler().post(new Runnable() {
			@Override
			public void run() {
				loadData(false);
			}
		});

		return view;
	}

	protected void onTopicClick(Topic topic){

	}

	@Override
	protected int getViewID() {
		return R.layout.document_list_fragment;
	}

	@Override
	protected View getTopView(final RelativeLayout parent) {
		if(mRecommendController == null){
			mRecommendController = createRecommendController(parent);
			mRecommendController.setOnClickListener(new RecommendController.OnItemClickListener() {
				@Override
				public void onItemClick(Topic topic) {
					onTopicClick(topic);
				}
			});
		}

		View view = mRecommendController.getView();
		RelativeLayout pa = (RelativeLayout) view.getParent();
		if(pa !=null){
			pa.removeView(view);
		}

        return view;
	}

	@Override
	public void onHide() {

	}

	@Override
	protected void onDataReceived(Page page) {
		super.onDataReceived(page);
		mRecommendController.setTopicList(page.getTopicList());
	}

	@Override
	public void onPause() {
		super.onPause();
		if(mRecommendController != null){
			mRecommendController.onPause();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(mRecommendController != null){
			mRecommendController.onResume();
		}
	}

	protected RecommendController createRecommendController(ViewGroup parent){
		return new RecommendController(getActivity(), parent);
	}
}
