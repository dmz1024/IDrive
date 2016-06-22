package com.ccpress.izijia.vo;

import com.google.gson.annotations.Expose;

/**
 * 我的收藏--照片
 * 
 * @author wangyi
 * 
 */
public class CollectPhotoVo {
	
	private String type;
	
	private String id;
	
	private String image;
	
	@Expose
	private boolean isCheck = false;

	@Expose
	private boolean isShown = false;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	
}
