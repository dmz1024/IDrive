package com.ccpress.izijia.vo;

import com.ccpress.izijia.dfy.entity.Collect;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * 收藏列表页
 *
 * @author wangyi
 *
 */
public class CollectVo implements Serializable {

	@Expose
	private static final long serialVersionUID = 1L;

//	url: "",//自驾团链接地址
//	id: "254",//收藏编号
//	obj_id: "",//自驾团编号
//	image: "",//缩略图
//	title: "",//标题

	private String url;
	private String obj_id;
	private String title;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getObj_id() {
		return obj_id;
	}

	public void setObj_id(String obj_id) {
		this.obj_id = obj_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private String id;

	private String image;

	private String desc;

	private String geo;

	private String soptid;

	private String spotid;
	private String docid;

	private String name;

	private String lng;

	private String lat;

	private int type;


	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Expose
	private boolean isCheck = false;

	@Expose
	private boolean isShown = false;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getGeo() {
		return geo;
	}

	public void setGeo(String geo) {
		this.geo = geo;
	}

	public String getSoptid(){ return soptid;}

	public void setSoptid(String soptid) {
		this.soptid = soptid;
	}

	public String getDocid() {return docid;}

	public void setDocid(String docid) {
		this.docid = docid;
	}


	public String getSpotid() {return spotid;	}

	public void setSpotid(String spotid) {this.spotid = spotid;}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public boolean isShown() {
		return isShown;
	}

	public void setShown(boolean isShown) {
		this.isShown = isShown;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}
}
