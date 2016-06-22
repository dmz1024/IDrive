package com.ccpress.izijia.vo;

import java.io.Serializable;

/**
 * 
 * @Des: 我的关注follow_who，我的粉丝who_follow(客户端不关注这两个字段)
 * @author Rhino
 * @version V1.0
 * @created 2015年6月6日 下午4:37:15
 */
public class MyAttentionVo implements Serializable{
	private String follow_who;
	private boolean isFollowed;
	private User user;

	public String getFollow_who() {
		return follow_who;
	}

	public void setFollow_who(String follow_who) {
		this.follow_who = follow_who;
	}

	public boolean isFollowed() {
		return isFollowed;
	}

	public void setFollowed(boolean isFollowed) {
		this.isFollowed = isFollowed;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public class User implements Serializable{
		private String uid;
		private String following;
		private String title;
		private String avatar;
		private String nickname;
		/**1:已关注，0：未关注*/
		private String fans;
		private String space_url;
		private String score;
		private String signature;

		/** 1男，2女 */
		private String sex;

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getFollowing() {
			return following;
		}

		public void setFollowing(String following) {
			this.following = following;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getAvatar() {
			return avatar;
		}

		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public String getFans() {
			return fans;
		}

		public void setFans(String fans) {
			this.fans = fans;
		}

		public String getSpace_url() {
			return space_url;
		}

		public void setSpace_url(String space_url) {
			this.space_url = space_url;
		}

		public String getScore() {
			return score;
		}

		public void setScore(String score) {
			this.score = score;
		}

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public String getSignature() {
			return signature;
		}

		public void setSignature(String signature) {
			this.signature = signature;
		}
	}
}
