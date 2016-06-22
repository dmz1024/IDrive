package com.trs.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.trs.app.ChannelItemFragmentActivity;
import com.trs.app.TRSApplication;
import com.trs.mobile.R;
import com.trs.types.Channel;
import com.trs.util.ImageDownloader;
import com.trs.wcm.LoadWCMJsonTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 14-3-14.
 */
public class GridFragment extends AbsUrlFragment implements AdapterView.OnItemClickListener{
	public static final String EXTRA_COLUMN_COUNT = "column_count";

	final private ArrayList<Channel> mChannelList = new ArrayList<Channel>();
	private View mLoadingView;
	private GridView mGrid;
	private int mColumnCount = 3;
	private BaseAdapter mAdapter = new BaseAdapter() {
		@Override
		public int getCount() {
			return mChannelList.size();
		}

		@Override
		public Channel getItem(int position) {
			return mChannelList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView;
			if(convertView != null){
				itemView = convertView;
			}
			else{
				itemView = getActivity().getLayoutInflater().inflate(R.layout.list_item_grid, parent, false);
			}

			ImageView icon = (ImageView) itemView.findViewById(R.id.img);
			TextView title = (TextView) itemView.findViewById(R.id.title);

			Channel c = getItem(position);
			new ImageDownloader.Builder().setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).build(c.getPic(), icon).start();
			title.setText(c.getTitle());

			return itemView;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getArguments() != null){
			mColumnCount = getArguments().getInt(EXTRA_COLUMN_COUNT, mColumnCount);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		View view = View.inflate(getActivity(), R.layout.grid_fragment, null);

		mLoadingView = view.findViewById(R.id.loading_view);
		mGrid = (GridView) view.findViewById(R.id.grid_view);
		mGrid.setNumColumns(mColumnCount);
		mGrid.setAdapter(mAdapter);
		mGrid.setOnItemClickListener(this);

		loadData();
		return view;
	}

	private void loadData(){
		LoadWCMJsonTask task = new LoadWCMJsonTask(getActivity()) {
			@Override
			public void onDataReceived(String result, boolean isCache) throws Exception {
				if(getActivity() == null){
					return;
				}

				GridFragment.this.onDataReceived(createChannelList(result));
			}

			@Override
			public void onError(Throwable t) {
				if(getActivity() == null){
					return;
				}

				mLoadingView.setVisibility(View.GONE);
				Toast.makeText(getActivity(), R.string.internet_unavailable, Toast.LENGTH_LONG).show();
			}
		};

		mLoadingView.setVisibility(View.VISIBLE);
		task.start(getUrl());
	}

	@Override
	public String getUrl() {
		switch(TRSApplication.app().getSourceType()){
			case JSON:
				return getRequestJsonUrl();
			case XML:
				return getRequestXmlUrl();
			default:
				return null;
		}
	}

	private String getRequestXmlUrl(){
		String url = super.getUrl();
		if(!url.endsWith("xml")){
			if(!url.endsWith("/")){
				url += "/";
			}

			url += "channels.xml";
		}

		return url;
	}

	private String getRequestJsonUrl(){
		String url = super.getUrl();
		if(!url.endsWith("json")){
			if(!url.endsWith("/")){
				url += "/";
			}

			url += "channels.json";
		}

		return url;
	}

	public void onDataReceived(List<Channel> channel){
		mLoadingView.setVisibility(View.GONE);

		mChannelList.clear();
		mChannelList.addAll(channel);

		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDisplay() {

	}

	@Override
	public void onHide() {

	}

	protected List<Channel> createChannelList(String data) throws JSONException {
		switch(TRSApplication.app().getSourceType()){
			case JSON:
				return createFromJson(data);
			case XML:
				return createFromXML(data);
			default:
				return null;
		}
	}

	private List<Channel> createFromJson(String json) throws JSONException {
		JSONArray array = new JSONArray(json);
		ArrayList<Channel> channelList = new ArrayList<Channel>();
		for(int i = 0; i < array.length(); i ++){
			Channel c = new Channel(array.getJSONObject(i));
			c.setType("0");
			channelList.add(c);
		}

		return channelList;

	}

	private List<Channel> createFromXML(String xml) throws JSONException {
		JSONObject obj = XML.toJSONObject(xml);
		obj = obj.getJSONObject("cs");
		JSONArray array = obj.getJSONArray("c");
		ArrayList<Channel> channelList = new ArrayList<Channel>();
		for(int i = 0; i < array.length(); i ++){
			Channel c = new Channel(array.getJSONObject(i));
			channelList.add(c);
		}

		return channelList;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ChannelItemFragmentActivity.show(getActivity(), mChannelList.get(position));
	}
}
