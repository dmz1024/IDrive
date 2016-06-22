package com.ccpress.izijia.entity;

import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by WLH on 2015/5/12 11:54.
 * 信息详情数据中的多媒体
 */
public class InfoMediaEntity implements Serializable {
    private int isvideo;
    private String image;
    private String srcurl;

    public InfoMediaEntity(JSONObject object){
        JSONObjectHelper helper = new JSONObjectHelper(object);
        setIsvideo(helper.getInt("isvideo", 0));
        setImage(helper.getString("image", null));
        setSrcurl(helper.getString("scrurl", null));
    }

    public void setIsvideo(int isvideo){
        this.isvideo = isvideo;
    }
    public void setImage(String image){
        this.image = image;
    }
    public void setSrcurl(String srcurl){
        this.srcurl  = srcurl;
    }

    public int getIsvideo(){
        return isvideo;
    }
    public String getImage(){
        return image;
    }

    public String getSrcurl(){
        return srcurl;
    }
}
