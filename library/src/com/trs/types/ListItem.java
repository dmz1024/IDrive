package com.trs.types;

import com.trs.constants.Constants;
import com.trs.util.StringUtil;
import net.endlessstudio.util.Util;
import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by john on 13-11-18.
 */
public class ListItem implements Serializable{
    private String id;
	private String title;
	private String summary;
	private String date;
	private String imgUrl;
	private String url;
	private String type;
    private String source;
	private String channelname;
	private  String hodong_id;
	private String phone_id;

	private UserInfoEntity user;//评论列表字段

	private String like;
	private String comment;
	private String share;
	ArrayList<GridImage> imageList;

	public ListItem(){

	}

	public ListItem(JSONObject obj){
		JSONObjectHelper helper = new JSONObjectHelper(obj);

		setTitle(helper.getString(Constants.TITLE_NAMES, null));
		setSummary(helper.getString(Constants.SUMMARY_NAMES, null));
		setDate(helper.getString(Constants.DATE_NAMES, null));
		setImgUrl(helper.getString(Constants.IMAGE_URL_NAMES, null));

		if(!StringUtil.isEmpty(getImgUrl()) && getImgUrl().startsWith("[")){
			JSONArray imageArray = null;
			try{
				imageArray = new JSONArray(getImgUrl());
			} catch (JSONException e){
				e.printStackTrace();
			}

			imageList = new ArrayList<GridImage>();
			for(int i=0; i<imageArray.length(); i++){
				JSONObject objj = null;
				try{
					GridImage entity = new GridImage();
					objj = (JSONObject) imageArray.get(i);
					entity.setAddr(objj.getString("addr"));
					entity.setGeo(objj.getString("geo"));
					entity.setPic(objj.getString("pic"));
					imageList.add(entity);
				} catch (JSONException e){
					e.printStackTrace();
				}
			}
		}

		setUrl(helper.getString(Constants.URL_NAMES, null));
		setType(helper.getString("type", null));
        setSource(helper.getString("source", null));
		setChannelname(helper.getString("channelname", null));
        setId(helper.getString(Constants.ID_NAMES, null));
		setHuDongId(helper.getString("hudong_id",null));
		setUser(new UserInfoEntity(helper.getJSONObject("user", new JSONObject())));
		setLike(helper.getString("like", null));
		setComment(helper.getString("comment", null));
		setShare(helper.getString("share", null));
	}

    public void setHuDongId(String hodong_id) {
        this.hodong_id = hodong_id;
    }

    public String getsetHuDongIdId() {

        return hodong_id;
    }
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {

		return id;
	}

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		String plainTitle = Util.removeHtmlTag(title);
		this.title = plainTitle != null? plainTitle.trim(): null;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		String plainSummary = Util.removeHtmlTag(summary);
		this.summary = plainSummary != null? plainSummary.trim(): null;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public String getPhoneId() {
		return phone_id;
	}

	public void setPhoneId(String url) {
		this.phone_id = phone_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getChannelname() {
		return channelname;
	}

	public void setChannelname(String channelname) {
		this.channelname = channelname;
	}

	public void setUser(UserInfoEntity entity){
		this.user = entity;
	}

	public UserInfoEntity getUser(){
		return user;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getShare() {
		return share;
	}

	public void setShare(String share) {
		this.share = share;
	}

	public ArrayList<GridImage> getImageList() {
		return imageList;
	}

	public void setImageList(ArrayList<GridImage> imageList) {
		this.imageList = imageList;
	}

	public static class GridImage {
		private String addr;
		private String geo;
		private String pic;

		public String getAddr() {
			return addr;
		}

		public void setAddr(String addr) {
			this.addr = addr;
		}

		public String getGeo() {
			return geo;
		}

		public void setGeo(String geo) {
			this.geo = geo;
		}

		public String getPic() {
			return pic;
		}

		public void setPic(String pic) {
			this.pic = pic;
		}
	}
}
