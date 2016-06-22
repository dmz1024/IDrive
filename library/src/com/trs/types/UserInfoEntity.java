package com.trs.types;

import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by WLH on 2015/5/12 11:55.
 * 用户信息实体
 */
public class UserInfoEntity implements Serializable{
    private String name;
    private int sex;
    private int uid;
    private String avatar;
    private String score;

    public UserInfoEntity(JSONObject object){
        JSONObjectHelper helper = new JSONObjectHelper(object);
        setName(helper.getString("name", null));
        setSex(helper.getInt("sex", -1));
        setUid(helper.getInt("uid",0));
        setAvatar(helper.getString("avatar", null) + "?" + System.currentTimeMillis());
        setScore(helper.getString("score", null));
    }

    public void setName(String name){
        this.name = name;
    }
    public void setSex(int sex){
        this.sex = sex;
    }
    public void setUid(int uid){
        this.uid = uid;
    }
    public void setAvatar(String avatar){
        this.avatar = avatar;
    }
    public void setScore(String score){
        this.score = score;
    }
    public String getName(){
        return name;
    }
    public int getSex(){
        return sex;
    }
    public int getUid(){
        return uid;
    }
    public String getAvatar(){
        return avatar;
    }
    public String getScore(){
        return score;
    }
}
