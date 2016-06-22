package com.ccpress.izijia.entity;

import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by WLH on 2015/5/19 15:09.
 * 线路详情，目的地详情的summary实体类
 */
public class SummaryEntity implements Serializable{
    private String title;
    private String line;
    private String desc;
    private String image;
    private String type;
    private String lid;
    private String url;

    public SummaryEntity(JSONObject object){
        JSONObjectHelper helper = new JSONObjectHelper(object);
        setTitle(helper.getString("title", null));
        setLine(helper.getString("line", null));
        setDesc(helper.getString("desc",null));
        setImage(helper.getString("image", null));
        setType(helper.getString("type", null));
        setLid(helper.getString(new String[]{"lid", "mid"}, null));
        setUrl(helper.getString("url", null));
    }

    public void setTitle(String title){
        this.title = title;
    }
    public void setLine(String line){
        this.line = line;
    }
    public void setDesc(String desc){
        this.desc = desc;
    }
    public void setImage(String image){
        this.image = image;
    }
    public void setType(String type){
        this.type = type;
    }
    public void setLid(String lid){
        this.lid = lid;
    }
    public void setUrl(String url){
        this.url = url;
    }

    public String getTitle(){
        return  title;
    }
    public String getLine(){
        return line;
    }
    public String getDesc(){
        return desc;
    }
    public String getImage(){
        return image;
    }
    public String getType(){
        return type;
    }
    public String getLid(){
        return lid;
    }
    public String getUrl(){
        return url;
    }
}
