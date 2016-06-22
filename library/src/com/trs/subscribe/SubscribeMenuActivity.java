package com.trs.subscribe;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import com.trs.app.TRSFragmentActivity;
import com.trs.mobile.R;
import com.trs.types.Channel;
import com.trs.view.TopBar;

import java.util.ArrayList;

/**
 * Created by john on 14-6-12.
 */
public class SubscribeMenuActivity extends TRSFragmentActivity {
	public static final String EXTRA_CHANNEL_LIST = "channel_list";
	public static final String EXTRA_CHANNEL_TAG = "channel_tag";

	private EditText mSearchKeyword;
	private ListView mListView;
	private ArrayList<Channel> mChannelList;
	private SubscribeChannelAdapter mAdapter;
	private String mChannelTag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mChannelList = (ArrayList<Channel>) getIntent().getSerializableExtra(EXTRA_CHANNEL_LIST);
		mChannelTag = getIntent().getStringExtra(EXTRA_CHANNEL_TAG);
		mChannelTag = mChannelTag == null? "": mChannelTag;

		setContentView(R.layout.subscribe_channel);

		mSearchKeyword = (EditText) findViewById(R.id.search_keyword);
		mListView = (ListView) findViewById(R.id.list);

		mSearchKeyword.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				StringBuilder sb = new StringBuilder();
				sb.append(".*?");
				for(int i = 0; i < s.length(); i ++){
					char c = s.charAt(i);
					sb.append(c).append(".*?");
				}

				ArrayList<Channel> searchResult = new ArrayList<Channel>();
				for(Channel c: mChannelList){
					if(c.getTitle().matches(sb.toString())){
						searchResult.add(c);
					}
				}

				mAdapter.clear();
				mAdapter.addAll(searchResult);
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mAdapter = new SubscribeChannelAdapter(this, mChannelTag);
		mListView.setAdapter(mAdapter);
		mAdapter.addAll(mChannelList);
	}

	@Override
	protected void initializeTopBar(TopBar topbar) {
		super.initializeTopBar(topbar);

		topbar.setTitleText("栏目订阅");
	}
}
