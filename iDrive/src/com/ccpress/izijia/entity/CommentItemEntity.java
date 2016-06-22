package com.ccpress.izijia.entity;

import com.trs.types.UserInfoEntity;
import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by WLH on 2015/5/12 13:35.
 */
public class CommentItemEntity implements Serializable{

    private String date;
    private String content;
    private UserInfoEntity user;

    public CommentItemEntity(JSONObject object){
        JSONObjectHelper helper = new JSONObjectHelper(object);
        setDate(helper.getString("date", null));
        setContent(helper.getString("content",null));
        setUser(new UserInfoEntity(helper.getJSONObject("user",new JSONObject())));
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setUser(UserInfoEntity entity){
        this.user = entity;
    }

    public String getDate(){
        return date;
    }

    public String getContent(){
        return content;
    }

    public UserInfoEntity getUser(){
        return user;
    }
}
