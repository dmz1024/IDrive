package com.trs.main.slidingmenu;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.trs.app.FragmentFactory;
import com.trs.app.JHSettingActivity;
import com.trs.app.TRSApplication;
import com.trs.db.SubscribeDB;
import com.trs.mobile.R;
import com.trs.subscribe.SubscribeMenuActivity;
import com.trs.types.Channel;
import com.trs.types.FirstClassMenu;
import com.trs.types.SubscribeList;
import com.trs.util.ImageDownloader;
import com.trs.view.checkable.CommonRadioGroup;

/**
 * Created by john on 14-3-11.
 */
public class SubscribeLeftMenuFragment extends MenuFragment implements LeftMenu.OnMenuCheckedListener{
	private CommonRadioGroup mRadioGroup;
	private FirstClassMenu mFirstClassMenu;
	private LayoutInflater mLayoutInflater;
	private View mBtnCancelSubscribe;
	private View mBtnSubscribe;
	private View mBtnSetting;
	private View mBtnOK;
	private SubscribeList mSubscribeList;
	private String mSubscribeTag;
	private Channel mSelectedChannel;
	private boolean isFirstTime = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mFirstClassMenu = TRSApplication.app().getFirstClassMenu();
		mLayoutInflater = LayoutInflater.from(getActivity());
		mSubscribeTag = TRSApplication.app().getFirstClassUrl();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.subscribe_left_menu, container, false);
		mRadioGroup = (CommonRadioGroup) view.findViewById(R.id.radio_group);
		mBtnCancelSubscribe = view.findViewById(R.id.btn_cancel_subscribe);
		mBtnSubscribe = view.findViewById(R.id.btn_subscribe);
		mBtnSetting = view.findViewById(R.id.btn_setting);
		mBtnOK = view.findViewById(R.id.btn_ok);

		initView();

		return view;
	}

	private void initView(){

		mRadioGroup.setOnCheckedChangeListener(new CommonRadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CommonRadioGroup group, int checkedId) {
				View itemView = group.findViewById(checkedId);
				Channel channel = (Channel) itemView.getTag(R.id.tag_channel);
				if(channel != null){
					mSelectedChannel = channel;
					Fragment fragment = FragmentFactory.createFragment(getActivity(), channel);
					if(getDisplayFragmentListener() != null){
						getDisplayFragmentListener().displayFragment(channel.getTitle(), fragment);
					}
				}
			}
		});

		mBtnCancelSubscribe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnCancelSubscribeClick(v);
			}
		});

		mBtnSubscribe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnSubscribeClick(v);
			}
		});

		mBtnSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnSetting(v);
			}
		});

		mBtnOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnOKClick(v);
			}
		});
	}

	private void updateView(){
		mRadioGroup.removeAllViews();

		if(mSubscribeList.hasSubscribedItem(mFirstClassMenu.getChannelList())){
			for(Channel channel: mFirstClassMenu.getChannelList()){
				if(mSubscribeList.isSubscribed(channel)){
					mRadioGroup.addView(createItemView(channel));
				}
			}
		}
		else if(mFirstClassMenu.getChannelList().size() > 0){
			mRadioGroup.addView(createItemView(mFirstClassMenu.getChannelList().get(0)));
		}

		if(isFirstTime && mRadioGroup.getChildCount() > 0){
			mRadioGroup.check(mRadioGroup.getChildAt(0).getId());
			isFirstTime = false;
		}
	}

	private View createItemView(final Channel channel){
		final View itemView = mLayoutInflater.inflate(R.layout.subscribe_left_menu_item, mRadioGroup, false);

		ImageView img = (ImageView) itemView.findViewById(R.id.img);
		TextView title = (TextView) itemView.findViewById(R.id.title);
		View btnCancel = itemView.findViewById(R.id.btn_delete);

		new ImageDownloader.Builder().setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).build(channel.getPic(), img).start();
		title.setText(channel.getTitle());

		itemView.setTag(R.id.tag_channel, channel);

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity()).setMessage("是否取消订阅? ").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(channel != null){
							mSubscribeList.setSubscribed(getActivity(), channel, false);
						}

						mRadioGroup.removeView(itemView);
					}
				}).setNegativeButton("取消", null).show();
			}
		});

		return itemView;
	}

	@Override
	public void onResume() {
		super.onResume();

		SubscribeList newSubscribeList = SubscribeDB.getInstance(getActivity()).getList(mSubscribeTag);

		if(mSubscribeList == null || mSubscribeList.isChanged(newSubscribeList)){
			mSubscribeList = newSubscribeList;
			updateView();
		}
	}

	@Override
	public void onMenuChecked(View menu) {
		Channel channel = (Channel) menu.getTag();
		if(getDisplayFragmentListener() != null){
			getDisplayFragmentListener().displayFragment(channel.getTitle(),
					FragmentFactory.createFragment(getActivity(), channel));
		}
	}

	public void onBtnSubscribeClick(View view){
		Intent intent = new Intent(getActivity(), SubscribeMenuActivity.class);
		intent.putExtra(SubscribeMenuActivity.EXTRA_CHANNEL_LIST, mFirstClassMenu.getChannelList());
		intent.putExtra(SubscribeMenuActivity.EXTRA_CHANNEL_TAG, mSubscribeTag);
		startActivity(intent);
	}

	public void onBtnCancelSubscribeClick(View view){
		if(canCancelSubscribe()){
			switchMode(true);
		}
	}

	private boolean canCancelSubscribe(){
		for(Channel c: mFirstClassMenu.getChannelList()){
			if(mSubscribeList.isSubscribed(c) && c.isSubscribeable()){
				return true;
			}
		}

		return false;
	}

	public void onBtnOKClick(View view){
		switchMode(false);
	}

	private void switchMode(boolean isCancelSubscribe){
		mBtnSubscribe.setVisibility(isCancelSubscribe? View.GONE: View.VISIBLE);
		mBtnCancelSubscribe.setVisibility(isCancelSubscribe? View.GONE: View.VISIBLE);
		mBtnSetting.setVisibility(isCancelSubscribe? View.GONE: View.VISIBLE);
		mBtnOK.setVisibility(isCancelSubscribe? View.VISIBLE: View.GONE);

		for(int i = 0; i < mRadioGroup.getChildCount(); i ++){
			View child = mRadioGroup.getChildAt(i);

			View iconGoto = child.findViewById(R.id.icon_goto);
			View btnDelete = child.findViewById(R.id.btn_delete);

			if(iconGoto != null){
				iconGoto.setVisibility(isCancelSubscribe? View.GONE: View.VISIBLE);
			}

			if(btnDelete != null){
				Channel channel = (Channel) child.getTag(R.id.tag_channel);
				btnDelete.setVisibility(isCancelSubscribe && channel.isSubscribeable()? View.VISIBLE: View.GONE);
			}
		}
	}

	public void onBtnSetting(View view){
		startActivity(new Intent(getActivity(), JHSettingActivity.class));
	}
}
