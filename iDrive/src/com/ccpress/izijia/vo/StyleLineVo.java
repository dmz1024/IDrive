package com.ccpress.izijia.vo;

import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * 
 * @author wangyi
 * 
 */
public class StyleLineVo {

	private String id;
	private String lid;
	private String title;
	private String desc;
	private String line;
	private String image;
	private String type;

	private List<Trip> trip;

	@Expose
	private boolean isCheck = false;

	@Expose
	private boolean isShown = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLid() {
		return lid;
	}

	public void setLid(String lid) {
		this.lid = lid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Trip> getTrip() {
		return trip;
	}

	public void setTrip(List<Trip> trip) {
		this.trip = trip;
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

	public static class Trip {
		private String tripid;
		private String tripname;
		private String tripspotid;
		private String tripgeo;

		public String getTripid() {
			return tripid;
		}

		public void setTripid(String tripid) {
			this.tripid = tripid;
		}

		public String getTripname() {
			return tripname;
		}

		public void setTripname(String tripname) {
			this.tripname = tripname;
		}

		public void setTripGeo(String tripgeo){ this.tripgeo = tripgeo; }
		public String getTripGeo(){
			return  tripgeo;
		}

		public String getTripSpotid() {return tripspotid;}
		public void setTripSpotid(String tripspotid) {
			this.tripspotid = tripspotid;
		}
	}
}
