package com.trs.fragment;

import android.content.Intent;
import android.os.Bundle;
import com.trs.adapter.AbsListAdapter;
import com.trs.adapter.IconTitleDateSummaryAdapter;
import com.trs.app.TRSApplication;
import com.trs.app.WebViewActivity;
import com.trs.types.ListItem;
import com.trs.types.Page;
import com.trs.types.Topic;
import com.trs.wcm.LoadWCMJsonTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;

/**
 * Created by john on 14-3-12.
 * Create request URL, load data, parse data
 */
public class DocumentListFragment extends AbsDocumentListFragment {
	public static final String EXTRA_CATEGORY = "category";
	private Page mCurrentPage;
	private String mCategory;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle arguments = getArguments();
		if(arguments != null){
			mCategory = arguments.getString(EXTRA_CATEGORY);
		}
	}

	@Override
	protected String getRequestUrl(int requestIndex) {
		switch(TRSApplication.app().getSourceType()){
		case JSON:
			return getJsonRequestUrl(requestIndex);
		case XML:
			return getXmlRequestUrl(requestIndex);
        case SOAP:
            return getSoapRequestUrl(requestIndex);
		default:
			return null;
		}
	}

    private String getSoapRequestUrl(int requestIndex){
        return requestIndex + "";
    }

	private String getJsonRequestUrl(int requestIndex){
		String documentSuffix = "documents.json";
		String url = getUrl().endsWith(documentSuffix)? getUrl().substring(0, getUrl().length() - documentSuffix.length()): getUrl();
		return url + (requestIndex == 0? "documents.json": String.format("documents_%s.json", requestIndex));
	}

	private String getXmlRequestUrl(int requestIndex){
		String documentSuffix = "documents.xml";
		String url = getUrl().endsWith(documentSuffix)? getUrl().substring(0, getUrl().length() - documentSuffix.length()): getUrl();
		return url + (requestIndex == 0? "documents.xml": String.format("documents_%s.xml", requestIndex));
	}

	protected void onTopicClick(Topic topic){

	}

	@Override
	protected Page createPage(String data) {
		switch(TRSApplication.app().getSourceType()){
		case JSON:
			return createPageFromJson(data);
		case XML:
			return createPageFromXml(data);
		default:
			return null;
		}
	}

    private Page createPageFromJson(String json){
		try {
			mCurrentPage = new Page(new JSONObject(json));
			return mCurrentPage;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	private Page createPageFromXml(String xml){
		try {
			JSONObject obj = XML.toJSONObject(xml);
			obj = obj.getJSONObject("ds");
			JSONArray array = obj.getJSONArray("d");
			ArrayList<Topic> topicItem = new ArrayList<Topic>();
			ArrayList<ListItem> dataItem = new ArrayList<ListItem>();
			for(int i = 0; i < array.length(); i ++){
				JSONObject itemObj = array.getJSONObject(i);
				if(itemObj.has("top") && "1".equals(itemObj.getString("top"))){
					Topic topic = new Topic(itemObj);
					topicItem.add(topic);
				}
				else{
					ListItem item = new ListItem(itemObj);
					dataItem.add(item);
				}
			}
			mCurrentPage = new Page();
			mCurrentPage.setTopicList(topicItem);
			mCurrentPage.setDataList(dataItem);
			mCurrentPage.setIndex(getCurrentIndex() + 1);
			mCurrentPage.setCount(mCurrentPage.getIndex() + 2);

			return mCurrentPage;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onItemClick(ListItem item) {
		super.onItemClick(item);

		if(item.getUrl().endsWith("json")){
			//TODO goto json details

		}
		else if(item.getUrl().endsWith("xml")){
			//TODO goto xml details
		}
		else if(item.getUrl().endsWith("html")){
			Intent intent = new Intent(getActivity(), WebViewActivity.class);
			intent.putExtra(WebViewActivity.EXTRA_TITLE, getTitle());
			intent.putExtra(WebViewActivity.EXTRA_URL, item.getUrl());
			startActivity(intent);
		}
	}


	@Override
	protected AbsListAdapter createAdapter() {
		return new IconTitleDateSummaryAdapter(getActivity());
	}

	private LoadWCMJsonTask createLoadWcmJsonTask(){
		LoadWCMJsonTask task = new LoadWCMJsonTask(getActivity()) {
			@Override
			public void onDataReceived(String result, boolean isCache) throws Exception {
				if(getActivity() == null){
					return;
				}

				DocumentListFragment.this.onDataReceived(result, isCache);
			}

			@Override
			public void onError(Throwable t) {
				DocumentListFragment.this.onDataError(t);
			}

			@Override
			protected void onStart() {
				super.onStart();

				if(getActivity() != null){
					DocumentListFragment.this.onLoadStart();
				}
			}

			@Override
			protected void onEnd() {
				super.onEnd();

				if(getActivity() != null){
					DocumentListFragment.this.onLoadEnd();
				}
			}

		};

		return task;
	}

	@Override
	protected void loadData(String url) {
		LoadWCMJsonTask task = createLoadWcmJsonTask();
        if(getActivity() == null){
            return;
        }
		task.start(url);
	}

	@Override
	protected void loadDataRemote(String url) {
		LoadWCMJsonTask task = createLoadWcmJsonTask();
		if(needCacheResult()){
			task.startRemote(url);
		}
		else{
			task.startAlwaysRemote(url);
		}
	}

	public String getCategory() {
		return mCategory;
	}

}
