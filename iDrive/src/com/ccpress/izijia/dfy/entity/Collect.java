package com.ccpress.izijia.dfy.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by dmz1024 on 2016/3/31.
 */
public class Collect {
    private boolean result;
//    url: "",//自驾团链接地址
//    id: "254",//收藏编号
//    obj_id: "",//自驾团编号
//    image: "",//缩略图
//    title: "",//标题

    private String url;
    private String id;
    private String obj_id;
    private String image;
    private String title;

    @Expose
    private boolean isCheck = false;

    @Expose
    private boolean isShown = false;

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

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObj_id() {
        return obj_id;
    }

    public void setObj_id(String obj_id) {
        this.obj_id = obj_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
