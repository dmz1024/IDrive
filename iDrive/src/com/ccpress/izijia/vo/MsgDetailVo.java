package com.ccpress.izijia.vo;

import com.google.gson.annotations.Expose;

/**
 * 
 * @Des: 详情
 * @author Rhino
 * @version V1.0
 * @created 2015年5月19日 下午3:25:34
 */
public class MsgDetailVo {

	@Expose
	private boolean isSend;

	private String talk_id;
	private long create_time;
	private String uid;
	private String content;
	private String id;

	private User user;

	public boolean isSend() {
		return isSend;
	}

	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}


	public String getTalk_id() {
		return talk_id;
	}

	public void setTalk_id(String talk_id) {
		this.talk_id = talk_id;
	}

	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static class User {
		private String avatar;
		private String score;
		private String uid;
		private String nickname;

		public String getAvatar() {
			return avatar;
		}

		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}

		public String getScore() {
			return score;
		}

		public void setScore(String score) {
			this.score = score;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
	}
	
}
