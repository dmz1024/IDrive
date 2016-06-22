package com.ccpress.izijia.entity;

/**
 * Created by Wu Jingyu
 * Date: 2015/5/5
 * Time: 10:37
 */
public class FriendslistEntity {
    private String name;
    private String imgurl;
    private String sex;
    private int mid;

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}
