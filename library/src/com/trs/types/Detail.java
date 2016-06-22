package com.trs.types;

import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 13-11-22.
 */
public class Detail {
	public static class PicInfo implements Serializable {
		private String title;
		private String url;

		public PicInfo(){}

		public PicInfo(JSONObject obj){
			JSONObjectHelper helper = new JSONObjectHelper(obj);
			setTitle(helper.getString("title", null));
			setUrl(helper.getString("url", null));
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}

	private String title;
	private String docid;
	private String content;
	private String wbTitle;
	private String wbURL;
	private String wbContent;
	private String wpage;
	final private ArrayList<PicInfo> pics = new ArrayList<PicInfo>();

	public Detail(JSONObject obj){
		JSONObjectHelper helper = new JSONObjectHelper(obj);

		setTitle(helper.getString("title", null));
		setDocid(helper.getString("docid", null));
		setContent(helper.getString("content", null));
		setWbTitle(helper.getString("wbTitle", null));
		setWbURL(helper.getString("wbURL", null));
		setWbContent(helper.getString("wbContent", null));
		setWpage(helper.getString("wpage", null));

		if(obj.has("pics")){
			ArrayList<PicInfo> picList = new ArrayList<PicInfo>();
			try{
				JSONArray array = obj.getJSONArray("pics");
				for(int i = 0; i < array.length(); i ++){
					picList.add(new PicInfo(array.getJSONObject(i)));
				}
			}
			catch(JSONException e){
				e.printStackTrace();
			}

			setPics(picList);
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDocid() {
		return docid;
	}

	public void setDocid(String docid) {
		this.docid = docid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getWbTitle() {
		return wbTitle;
	}

	public void setWbTitle(String wbTitle) {
		this.wbTitle = wbTitle;
	}

	public String getWbURL() {
		return wbURL;
	}

	public void setWbURL(String wbURL) {
		this.wbURL = wbURL;
	}

	public String getWbContent() {
		return wbContent;
	}

	public void setWbContent(String wbContent) {
		this.wbContent = wbContent;
	}

	public String getWpage() {
		return wpage;
	}

	public void setWpage(String wpage) {
		this.wpage = wpage;
	}

	public ArrayList<PicInfo> getPics() {
		return pics;
	}

	public void setPics(List<PicInfo> picList){
		this.pics.clear();
		this.pics.addAll(picList);
	}
}
