package com.trs.types;

import com.trs.constants.Constants;
import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 13-11-19.
 */
public class Page {
	private int index;
	private int count;
	private String logo;
	private String title;
	private final ArrayList<Topic> topicList = new ArrayList<Topic>();
	private final ArrayList<ListItem> dataList = new ArrayList<ListItem>();

	public Page(){

	}

	public Page(JSONObject obj){
 		JSONObjectHelper objHelper = new JSONObjectHelper(obj);
		JSONObjectHelper pageInfo = objHelper.getJSONObjectHelper(Constants.PAGE_INFO_NAMES);
		setIndex(pageInfo.getInt("page_index", 0));
		setCount(pageInfo.getInt("page_count", 0));

		JSONObjectHelper helper = new JSONObjectHelper(obj);
		setLogo(helper.getString("chnllogo", null));
		setTitle(helper.getString("chnldesc", null));

		if(obj.has("topic_datas")){
			try{
				JSONArray topicArray = obj.getJSONArray("topic_datas");
				for(int i = 0; i < topicArray.length(); i ++){
					Topic topic = new Topic(topicArray.getJSONObject(i));
					topicList.add(topic);
				}
			}
			catch(JSONException e){
				e.printStackTrace();
			}
		}

		if(obj.has("datas")){
			try{
				Object data = obj.get("datas");
				JSONArray dataArray = null;
				if(data instanceof JSONArray){
					dataArray = (JSONArray) data;
				}
				else if(data instanceof JSONObject){
					dataArray = ((JSONObject)data).getJSONArray("docs");
				}

				for(int i = 0; i < dataArray.length(); i ++){
					ListItem item = new ListItem(dataArray.getJSONObject(i));
					dataList.add(item);
				}
			}
			catch(JSONException e){
				e.printStackTrace();
			}
		}
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public ArrayList<Topic> getTopicList() {
		return topicList;
	}

	public void setTopicList(List<Topic> topicList){
		this.topicList.clear();
		this.topicList.addAll(topicList);
	}

	public ArrayList<ListItem> getDataList() {
		return dataList;
	}

	public void setDataList(List<ListItem> dataList){
		this.dataList.clear();
		this.dataList.addAll(dataList);
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
