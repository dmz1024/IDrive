package com.ccpress.izijia.vo;

import com.froyo.commonjar.adapter.ParentVo;

/**
 * 
 * @Des: 收藏--详情--看点
 * @author Rhino 
 * @version V1.0 
 * @created  2015年5月13日 上午11:25:36
 */
public class CollectViewPvo extends ParentVo<CollectViewChildVo> {
	
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
