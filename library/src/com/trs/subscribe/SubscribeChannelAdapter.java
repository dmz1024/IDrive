package com.trs.subscribe;

import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.trs.adapter.BaseListAdapter;
import com.trs.app.DebugData;
import com.trs.db.SubscribeDB;
import com.trs.mobile.BuildConfig;
import com.trs.mobile.R;
import com.trs.types.Channel;
import com.trs.types.SubscribeItem;
import com.trs.types.SubscribeList;
import com.trs.util.ImageDownloader;
import com.trs.util.StringUtil;

import java.util.List;

/**
 * Created by john on 14-6-12.
 */
public class SubscribeChannelAdapter extends BaseListAdapter<Channel> {
	private SubscribeList mSubscribeList;
	private String mTag;
	public SubscribeChannelAdapter(Context context, String tag) {
		super(context);
		this.mTag = tag;
		mSubscribeList = SubscribeDB.getInstance(context).getList(tag);
	}

	@Override
	public int getViewID() {
		return R.layout.subscribe_channel_item;
	}

	@Override
	public void updateView(View view, int position, View convertView, ViewGroup parent) {
		super.updateView(view, position, convertView, parent);

		ImageView img = (ImageView) view.findViewById(R.id.img);
		TextView title = (TextView) view.findViewById(R.id.title);
		CheckBox check = (CheckBox) view.findViewById(R.id.is_subscribed);

		final Channel channel = getItem(position);
		boolean hasImage = !StringUtil.isEmpty(channel.getPic());
		if(BuildConfig.DEBUG && !hasImage){
			channel.setPic(DebugData.ICONS[position % DebugData.ICONS.length]);
			hasImage = true;
		}
		img.setVisibility(hasImage? View.VISIBLE: View.GONE);
		if(hasImage){
			new ImageDownloader.Builder().setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).build(channel.getPic(), img).start();
		}

		title.setText(channel.getTitle());

		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
			check.setVisibility(channel.isSubscribeable()? View.VISIBLE: View.INVISIBLE);
		}
		else{
			check.setAlpha(channel.isSubscribeable()? 1.0f: 0.2f);
		}
		check.setChecked(!channel.isSubscribeable() || mSubscribeList.isSubscribed(channel));
		check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!isChecked && getSubscribeItemCount() <= 1){
					Toast.makeText(getContext(), "不能取消所有订阅. ", Toast.LENGTH_SHORT).show();
					buttonView.setChecked(true);
				}
				else{
					mSubscribeList.setSubscribed(getContext(), channel, isChecked);
				}
			}
		});

		check.setEnabled(channel.isSubscribeable());
	}

	private int getSubscribeItemCount(){
		int disabledCount = 0;
		for(SubscribeItem item: mSubscribeList){
			if(!item.isEnabled()){
				disabledCount ++;
			}
		}

		return getCount() - disabledCount;
	}
}
