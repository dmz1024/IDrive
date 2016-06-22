package com.ccpress.izijia.vo;

import com.google.gson.annotations.Expose;

/**
 * 
 * @Des: 我的消息
 * @author Rhino
 * @version V1.0
 * @created 2015年5月19日 上午11:53:35
 */
public class MyMsgVo {

	@Expose
	private boolean isSend;

	@Expose
	private long createTime;

	private String message;

	private String talk_id;

	private String to_user_avatar;

	private String to_user_nickname;

	private String to_uid;
	
	/**
	 * 是否有新消息
	 */
	private String has_new;

	public String getHas_new() {
		return has_new;
	}

	public void setHas_new(String has_new) {
		this.has_new = has_new;
	}

	public boolean isSend() {
		return isSend;
	}

	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTalk_id() {
		return talk_id;
	}

	public void setTalk_id(String talk_id) {
		this.talk_id = talk_id;
	}

	public String getTo_user_avatar() {
		return to_user_avatar;
	}

	public void setTo_user_avatar(String to_user_avatar) {
		this.to_user_avatar = to_user_avatar;
	}

	public String getTo_user_nickname() {
		return to_user_nickname;
	}

	public void setTo_user_nickname(String to_user_nickname) {
		this.to_user_nickname = to_user_nickname;
	}

	public String getTo_uid() {
		return to_uid;
	}

	public void setTo_uid(String to_uid) {
		this.to_uid = to_uid;
	}

}
