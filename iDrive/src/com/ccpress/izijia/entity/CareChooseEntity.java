package com.ccpress.izijia.entity;

import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by WLH on 2015/6/18 10:12.
 * 精选和自驾团实体类
 */
public class CareChooseEntity implements Serializable{

    /****精彩瞬间实体类*****/
    public class Moments implements Serializable{
        private String title;
        private String image;
        private String time;
        private String url;
        private String mid;

        public Moments(JSONObject object){
            JSONObjectHelper helper = new JSONObjectHelper(object);
            title = helper.getString("title", null);
            image = helper.getString("image", null);
            time = helper.getString("time", null);
            url = helper.getString("url", null);
            mid = helper.getString("mid", null);
        }

        public String getTitle(){
            return title;
        }
        public String getImage(){
            return image;
        }
        public String getTime(){
            return time;
        }
        public String getUrl(){
            return url;
        }
        public String getMid(){
            return mid;
        }
    }

    /*****活动内容实体类****/
    public class ActContent implements Serializable{
        private String desc;
        private String image;

        public ActContent(JSONObject object){
            JSONObjectHelper helper = new JSONObjectHelper(object);
            desc = helper.getString("desc",null);
            image = helper.getString("image", null);
        }
        public String getDesc(){
            return desc;
        }
        public String getImage(){
            return image;
        }
    }

    /*****自驾团领队信息实体类****/
    public class Guide implements Serializable{
        private String name;
        private String avatar;
        private int year;
        private String experience;
        private String profile;

        public Guide(JSONObject object){
            JSONObjectHelper helper = new JSONObjectHelper(object);
            name = helper.getString("name",null);
            avatar = helper.getString("avatar", null);
            year = helper.getInt("year", 0);
            experience = helper.getString("experience", null);
            profile = helper.getString("profile", null);
        }
        public String getName(){
            return name;
        }
        public String getAvatar(){
            return avatar;
        }
        public int getYear(){
            return year;
        }
        public String getExperience(){
            return experience;
        }
        public String getProfile(){
            return profile;
        }
    }

    /*****自驾团额外信息实体类****/
    public static  class Extra implements Serializable{
        private String desc;
        private String title;

        public Extra(JSONObject object){
            JSONObjectHelper helper = new JSONObjectHelper(object);
            desc = helper.getString("desc",null);
            title = helper.getString("title", null);
        }
        public String getDesc(){
            return desc;
        }
        public String getTitle(){
            return title;
        }
    }


    /*****************************成员变量****************************/
    private String name;//精选、自驾团共有字段
    private ArrayList<ViewspotDetailEntity.Images> images = new ArrayList<ViewspotDetailEntity.Images>();//精选、自驾团共有字段
    private ArrayList<Moments> moments = new ArrayList<>();//精选、自驾团共有字段
    private String time;//精选、自驾团共有字段

    private String addr;//精选字段
    private String geo;//精选字段
    private String organizer;//精选 主办方 字段
    private String hotline;//精选 热线 字段
    private ActContent content;//精选 内容 字段
    private ArrayList<String> apply = new ArrayList<>();//精选 报名方式 字段
    private int status;//精选 状态 字段

    private String price;//自驾团 价格 字段
    private String intro;//自驾团 线路简介 字段
    private String trip;//自驾团 行程字段
    private Guide tour;//自驾团 领队信息字段
    private ArrayList<Extra> extras = new ArrayList<>();//自驾团 字段

    public CareChooseEntity(JSONObject object){
        JSONObjectHelper helper = new JSONObjectHelper(object);
        name = helper.getString("name", null);
        organizer = helper.getString("organizer", null);
        addr = helper.getString("addr", null);
        geo = helper.getString("geo", null);
        time = helper.getString("time", null);
        hotline = helper.getString("hotline", null);
        status = helper.getInt("status", -1);

        price = helper.getString("price", null);
        intro = helper.getString("intro", null);
        trip = helper.getString("trip", null);

        if(object.has("images")){
            try {
                JSONArray array = helper.getJSONArray("images", null);
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        ViewspotDetailEntity.Images item = new ViewspotDetailEntity.Images(array.getJSONObject(i));
                        images.add(item);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(object.has("moments")){
            try {
                JSONArray array = helper.getJSONArray("moments", null);
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        Moments item = new Moments(array.getJSONObject(i));
                        moments.add(item);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(object.has("apply")){
            try {
                JSONArray array = helper.getJSONArray("apply", null);
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        apply.add(array.getString(i));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(object.has("content")){
            try {
                content = new ActContent( object.getJSONObject("content"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(object.has("tour")){
            try {
                tour = new Guide(object.getJSONObject("tour"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(object.has("extras")){
            try {
                JSONArray array = helper.getJSONArray("extras", null);
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        Extra item = new Extra(array.getJSONObject(i));
                        extras.add(item);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getName(){
        return name;
    }
    public String getPrice(){
        return price;
    }

    public String getTime(){
        return time;
    }
    public String getAddr(){
        return addr;
    }
    public String getGeo(){
        return geo;
    }
    public String getOrganizer(){
        return organizer;
    }
    public String getHotline(){
        return hotline;
    }
    public int getStatus(){
        return status;
    }
    public ArrayList<ViewspotDetailEntity.Images> getImages(){
        return images;
    }
    public ArrayList<Moments> getMoments(){
        return moments;
    }
    public ArrayList<String> getApply(){
        return apply;
    }
    public ActContent getContent(){
        return content;
    }

    public String getIntro(){
        return intro;
    }
    public String getTrip(){
        return trip;
    }
    public Guide getGuide(){
        return tour;
    }
    public ArrayList<Extra> getExtras(){
        return extras;
    }

}
