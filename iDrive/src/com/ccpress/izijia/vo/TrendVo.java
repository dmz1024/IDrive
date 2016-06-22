package com.ccpress.izijia.vo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.froyo.commonjar.utils.GsonTools;

public class TrendVo {

	private String content;
	private String title;
	private String time;
	private String type;
	private String docid;
	private String comment;
	private String like;
	private String share;
	private String url;
	private List<Image> image;
	
	private User user;
	

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDocid() {
		return docid;
	}

	public void setDocid(String docid) {
		this.docid = docid;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public String getShare() {
		return share;
	}

	public void setShare(String share) {
		this.share = share;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Image> getImage() {
		return image;
	}

	public void setImage(List<Image> image) {
		this.image = image;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static class User {
		private String user;
		private String sex;
		private String avatar;
		private String name;
		private String uid;

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public String getAvatar() {
			return avatar;
		}

		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

	}

	public static class Image {
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
	public static List<TrendVo> convertToVo(JSONArray array) throws JSONException{
		List<TrendVo> data=new ArrayList<TrendVo>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj=array.getJSONObject(i);
			
			TrendVo vo=new TrendVo();
			vo.setComment(obj.optString("comment"));
			vo.setContent(obj.optString("content"));
			vo.setDocid(obj.optString("docid"));
			vo.setShare(obj.optString("share"));
			
			vo.setTime(obj.optString("time"));
			vo.setTitle(obj.optString("title"));
			vo.setLike(obj.optString("like"));
			
			vo.setType(obj.optString("type"));
			vo.setUrl(obj.optString("url"));
			User ser=GsonTools.getVo(obj.optJSONObject("user").toString(), User.class);
			vo.setUser(ser);
		
			if("14".equals(obj.optString("type"))||"11".equals(obj.optString("type"))){
				List<Image> temp=GsonTools.getList(obj.optJSONArray("image"), Image.class);
				vo.setImage(temp);
			}else{
				Image image=new Image();
				image.setPic(obj.optString("image"));
				List<Image> temp=new ArrayList<Image>();
				temp.add(image);
				vo.setImage(temp);
			}
			data.add(vo);
		}
		
		return data;
	}
}
