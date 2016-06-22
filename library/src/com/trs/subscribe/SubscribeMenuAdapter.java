package com.trs.subscribe;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.trs.adapter.BaseListAdapter;
import com.trs.db.SubscribeDB;
import com.trs.mobile.R;
import com.trs.types.Channel;
import com.trs.types.SubscribeList;
import com.trs.util.ImageDownloader;
import com.trs.util.StringUtil;

/**
 * Created by john on 14-6-12.
 */
public class SubscribeMenuAdapter extends BaseListAdapter<Channel> {
	private SubscribeList mSubscribeList;
	private String tag;
	public SubscribeMenuAdapter(Context context, String tag) {
		super(context);
		this.tag = tag;
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
		img.setVisibility(hasImage? View.VISIBLE: View.GONE);
		if(hasImage){
			new ImageDownloader.Builder().setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).build(channel.getPic(), img).start();
		}

		title.setText(channel.getTitle());

		check.setChecked(mSubscribeList.isSubscribed(channel));
		check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mSubscribeList.setSubscribed(getContext(), channel, isChecked);
			}
		});
	}
}
