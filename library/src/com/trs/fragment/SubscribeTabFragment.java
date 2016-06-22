package com.trs.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.trs.db.SubscribeDB;
import com.trs.mobile.R;
import com.trs.subscribe.SubscribeChannelActivity;
import com.trs.types.Channel;
import com.trs.types.SubscribeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 14-6-12.
 */
public class SubscribeTabFragment extends TabFragment {
	private SubscribeList mSubscribeList;
	private String mSubscribeTag;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		mSubscribeTag = getUrl();
		View btnSubscribe = view.findViewById(R.id.btn_subscribe);
		if(btnSubscribe != null){
			btnSubscribe.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), SubscribeChannelActivity.class);
					ArrayList<Channel> channelList = new ArrayList<Channel>();
					channelList.addAll(SubscribeTabFragment.super.getChannelList());
					intent.putExtra(SubscribeChannelActivity.EXTRA_CHANNEL_LIST, channelList);
					intent.putExtra(SubscribeChannelActivity.EXTRA_CHANNEL_TAG, mSubscribeTag);
					startActivity(intent);
				}
			});
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		SubscribeList newSubscribeList = SubscribeDB.getInstance(getActivity()).getList(mSubscribeTag);
		newSubscribeList.setDefaultSubscribed(true);

		if(mSubscribeList == null || mSubscribeList.isChanged(newSubscribeList)){
			mSubscribeList = newSubscribeList;
			mIsSubscribeListReloaded = true;
			notifyChannelDataChanged();
		}
	}

	@Override
	public int getViewID() {
		return R.layout.subscribe_tab_fragment;
	}

	final private ArrayList<Channel> mChannelList = new ArrayList<Channel>();
	private boolean mIsSubscribeListReloaded = false;

	@Override
	protected List<Channel> getChannelList() {
		if(mIsSubscribeListReloaded || mChannelList.size() == 0){
			mChannelList.clear();

			if(mSubscribeList != null){

				for(Channel channel: super.getChannelList()){
					if(mSubscribeList.isSubscribed(channel)){
						mChannelList.add(channel);
					}
				}

				if(mChannelList.size() == 0 && super.getChannelList().size() > 0){
					mChannelList.add(super.getChannelList().get(0));
				}
			}
			else{
				mChannelList.addAll(super.getChannelList());
			}

			mIsSubscribeListReloaded = false;
		}

		return mChannelList;
	}
}
