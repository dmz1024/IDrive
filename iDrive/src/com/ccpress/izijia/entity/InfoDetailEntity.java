package com.ccpress.izijia.entity;

import com.trs.types.UserInfoEntity;
import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by WLH on 2015/5/12 11:49.
 */
public class InfoDetailEntity implements Serializable {


    private int code;
    private String message;
    private int docid;
    private String title;
    private  String desc;
    private String geo;
    private String date;
    private ArrayList<InfoMediaEntity> medias = new ArrayList<InfoMediaEntity>();
    private UserInfoEntity user;
    private int commments_c;
    private int share_c;
    private int lick_c;


    private ArrayList<CommentItemEntity> comments = new ArrayList<CommentItemEntity>();
    private ArrayList<UserInfoEntity> share = new ArrayList<UserInfoEntity>();
    private ArrayList<UserInfoEntity> like = new ArrayList<UserInfoEntity>();


    public InfoDetailEntity(JSONObject obj){
        JSONObjectHelper helper = new JSONObjectHelper(obj);
        setCode(helper.getInt("code", 0));
        setMessage(helper.getString("message", null));

        if(obj.has("summary")){
            try {
                JSONObject summary = obj.getJSONObject("summary");
                JSONObjectHelper helper1 = new JSONObjectHelper(summary);
                setDocid(helper1.getInt("docid", 0));
                setTitle(helper1.getString("title", null));
                setDesc(helper1.getString("desc",null));
                setGeo(helper1.getString("geo", null));
                setDate(helper1.getString("date",null));
                if(summary.has("medias")){
                    try{
                        JSONArray mediasArray = summary.getJSONArray("medias");
                        for(int i = 0; i < mediasArray.length(); i ++){
                            InfoMediaEntity topic = new InfoMediaEntity(mediasArray.getJSONObject(i));
                            medias.add(topic);
                        }
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        setUser(new UserInfoEntity(helper.getJSONObject("user",new JSONObject())));
        setCommments_c(helper.getInt("comments_c",0));
        setShare_c(helper.getInt("share_c",0));
        setFavour_c(helper.getInt("like_c",0));



        if (obj.has("comments")){
            try {
                JSONArray mediasArray = obj.getJSONArray("comments");
                for(int i = 0; i < mediasArray.length(); i ++){
                    CommentItemEntity topic = new CommentItemEntity(mediasArray.getJSONObject(i));
                    comments.add(topic);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (obj.has("share")){
            try {
                JSONArray mediasArray = obj.getJSONArray("share");
                for(int i = 0; i < mediasArray.length(); i ++){
                    UserInfoEntity topic = new UserInfoEntity(mediasArray.getJSONObject(i));
                    share.add(topic);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (obj.has("like")){
            try {
                JSONArray mediasArray = obj.getJSONArray("like");
                for(int i = 0; i < mediasArray.length(); i ++){
                    UserInfoEntity topic = new UserInfoEntity(mediasArray.getJSONObject(i));
                    like.add(topic);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void setCode(int code){
        this.code = code;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setDocid(int docid){
        this.docid = docid;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }

    public void setGeo(String geo){
        this.geo = geo;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setUser(UserInfoEntity entity){
        this.user = entity;
    }

    public void setCommments_c(int commments_c){
        this.commments_c = commments_c;
    }

    public void setShare_c(int share_c){
        this.share_c = share_c;
    }

    public void setFavour_c(int favour_c){
        this.lick_c = favour_c;
    }

    public int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }


    public int getDocid(){
        return docid;
    }

    public String getTitle(){
        return title;
    }

    public String getDesc(){
        return desc;
    }
    public String getGeo(){
        return geo;
    }
    public String getDate()
    {
        return date;
    }
    public int getCommments_c(){
        return commments_c;
    }
    public int getShare_c(){
        return share_c;
    }
    public int getFavour_c(){
        return lick_c;
    }

    public ArrayList<InfoMediaEntity> getMedias(){
        return medias;
    }
    public ArrayList<UserInfoEntity> getShare(){
        return share;
    }
    public ArrayList<UserInfoEntity> getFavour(){
        return like;
    }

    public ArrayList<CommentItemEntity> getComments(){
        return comments;
    }

    public UserInfoEntity getUser(){
        return user;
    }


}
