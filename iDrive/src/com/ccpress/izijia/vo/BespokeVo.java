package com.ccpress.izijia.vo;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

/**
 * 
 * @Des: 看点
 * @author Rhino
 * @version V1.0
 * @created 2015年6月16日 上午9:12:03
 */

public class BespokeVo implements Serializable {

	private String id;

	private String spotid;
	private String soptid;

	private String geo;

	private String name;

	private String image;

	private String lng;

	private String lat;
	
	/**
	 * 添加看点时，标示是否是新增
	 */
	@Expose
	private String from_fav;

	// 1.看点，2.停车发呆地，3.高德地图点，4.私享点，5.自建线路，6.网站线路
	private int type;

	@Expose
	private boolean isCheck = false;

	@Expose
	private boolean isShown = false;

	public boolean isShown() {
		return isShown;
	}

	public void setShown(boolean isShown) {
		this.isShown = isShown;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSpotid() {
		return spotid;
	}

	public void setSpotid(String spotid) {
		this.spotid = spotid;
	}

	public String getGeo() {
		return geo;
	}

	public void setGeo(String geo) {
		this.geo = geo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public String getSoptid() {
		return soptid;
	}

	public void setSoptid(String soptid) {
		this.soptid = soptid;
	}

	public String getFrom_fav() {
		return from_fav;
	}

	public void setFrom_fav(String from_fav) {
		this.from_fav = from_fav;
	}
}
