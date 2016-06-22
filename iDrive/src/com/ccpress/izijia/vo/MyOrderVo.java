package com.ccpress.izijia.vo;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

/**
 * 
 * @Des: 我的订单
 * @author Rhino 
 * @version V1.0 
 * @created  2015年5月27日 上午9:43:00
 */
public class MyOrderVo implements Serializable{
	
	@Expose
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String lid;
	
	private String image;
	
	private String line;
	
	private String title;
	
	private String type;
	
	private String url;
	
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
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
