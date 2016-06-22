package com.ccpress.izijia.entity;

/**
 * Created by Wu Jingyu
 * Date: 2015/5/5
 * Time: 10:37
 */
public class TopPopupListEntity {
    private int id;
    private String name;
    private String url;
    private int cid;
   // private String uid;

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCid() {
        return cid;
    }


    public void setCid(int cid) {
        this.cid = cid;
    }

//    public void setUid(String uid){ this.uid=uid;}
//    public String getUid(){ return uid;}
}
