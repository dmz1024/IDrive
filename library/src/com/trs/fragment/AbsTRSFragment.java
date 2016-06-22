package com.trs.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.trs.mobile.R;
import com.trs.view.TopBar;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by john on 14-3-11.
 */
abstract public class AbsTRSFragment extends Fragment {
	public static String EXTRA_TITLE = "title";

	private String mTitle;
	private TopBar mTopBar;
    private boolean mIsShowing = false;

	private boolean mHasNotifyDisplayWhenViewNotReady;
	private boolean mHasCreateView;
	private boolean mHasCalledOnDisplay;
	private Handler mHandler;
	private DisplayMetrics mMetrics = new DisplayMetrics();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
		if(getArguments() != null){
			mTitle = getArguments().getString(EXTRA_TITLE);
		}
		mHandler = new Handler();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mHasCreateView = true;
		View view = super.onCreateView(inflater, container, savedInstanceState);
		mTopBar = (TopBar) getActivity().findViewById(R.id.topbar);
		System.out.println("Fragment: onCreateView activity: " + getActivity() + " resources: " + getResources());
		if(mTopBar != null){
			mTopBar.post(new Runnable() {
				@Override
				public void run() {
					if(getActivity() != null){
						updateTopBar(mTopBar);
					}
				}
			});
		}

		if(mHasNotifyDisplayWhenViewNotReady && !mHasCalledOnDisplay){
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if(getActivity() != null){
						onDisplay();
						mHasCalledOnDisplay = true;
					}
				}
			});
		}
		return view;
	}

	protected void updateTopBar(TopBar topbar){
		if(getTitle() != null){
			System.out.println("Fragment: updateTopBar activity: " + getActivity() + " resources: " + getResources());
			topbar.setTitleText(getTitle());
		}
	}

	/**
	 * This method can be called by using TRSTabFragment and TRSFragmentActivity
	 */
	public void onDisplay(){
        mIsShowing = true;
	};

	/**
	 * This method can be called by using TRSTabFragment and TRSFragmentActivity
	 */
	public void onHide(){
        mIsShowing = false;
	};

	public String getTitle(){
		return mTitle;
	}

	public void notifyDisplay(){
		if(mHasCreateView && !mHasCalledOnDisplay){
			onDisplay();
			mHasCalledOnDisplay = true;
		}
		else{
			mHasNotifyDisplayWhenViewNotReady = true;
		}
	}

	public String getTopTitle(){
		return mTitle;
	}

	public DisplayMetrics getMetrics(){
		return mMetrics;
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd((((Object)this).getClass()).getName());
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(((Object)this).getClass().getName());
	}

    public boolean isShowing() {
        return mIsShowing;
    }
}
