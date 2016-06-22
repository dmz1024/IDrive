package com.ccpress.izijia.dfy.entity;

/**
 * Created by dengmingzhi on 16/4/17.
 * 不登录预订返回信息
 */
public class UserInfo {

    private boolean result;

    private data data;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public UserInfo.data getData() {
        return data;
    }

    public void setData(UserInfo.data data) {
        this.data = data;
    }

    public class data{
        private String auth;
        private userinfo userinfo;
        public class userinfo{
            private String uid;
            private String username;
            private String email;
            private String sex;
            private String signature;
            private String title;
            private String fans;
            private String following;
            private String avatar128;
            private String nickname;
            private String score;
            private String mobile;
            private String avatar;
            private String invite;

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
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

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getFans() {
                return fans;
            }

            public void setFans(String fans) {
                this.fans = fans;
            }

            public String getFollowing() {
                return following;
            }

            public void setFollowing(String following) {
                this.following = following;
            }

            public String getAvatar128() {
                return avatar128;
            }

            public void setAvatar128(String avatar128) {
                this.avatar128 = avatar128;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getScore() {
                return score;
            }

            public void setScore(String score) {
                this.score = score;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getInvite() {
                return invite;
            }

            public void setInvite(String invite) {
                this.invite = invite;
            }
        }

        public String getAuth() {
            return auth;
        }

        public void setAuth(String auth) {
            this.auth = auth;
        }

        public UserInfo.data.userinfo getUserinfo() {
            return userinfo;
        }

        public void setUserinfo(UserInfo.data.userinfo userinfo) {
            this.userinfo = userinfo;
        }
    }

}
