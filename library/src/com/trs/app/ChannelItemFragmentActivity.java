package com.trs.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;
import com.trs.mobile.R;
import com.trs.types.Channel;

/**
 * Created by john on 14-5-6.
 */
public class ChannelItemFragmentActivity extends TRSFragmentActivity {
	public static final String EXTRA_CHANNEL = "channel";
	private Channel mChannel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mChannel = (Channel) getIntent().getSerializableExtra(EXTRA_CHANNEL);

		setContentView(R.layout.fragment_activity);
	}

	@Override
	protected Fragment createFragment() {
		super.createFragment();

		return FragmentFactory.createFragment(this, mChannel);
	}

	@Override
	protected int getFragmentContainerID() {
		return R.id.content;
	}

	public static void show(Context context, Channel channel){
		Intent intent = new Intent(context, ChannelItemFragmentActivity.class);
		intent.putExtra(EXTRA_TITLE, channel.getTitle());
		intent.putExtra(EXTRA_CHANNEL, channel);

		context.startActivity(intent);
	}
}
