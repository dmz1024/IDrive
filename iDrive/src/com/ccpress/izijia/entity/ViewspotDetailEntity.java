package com.ccpress.izijia.entity;

import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by WLH on 2015/5/25 14:23.
 * 看点详情实体类
 */
public class ViewspotDetailEntity implements Serializable{
    private String name;
    private String desc;
    private String addr;
    private String geo;
    private ArrayList<Images> images = new ArrayList<Images>();
    private String time;
    private String price;
    private String season;
    private String tips;
    private String message;
    private String url;

    public ViewspotDetailEntity(JSONObject object){
        JSONObjectHelper helper = new JSONObjectHelper(object);
        name = helper.getString("name", null);
        desc = helper.getString("desc", null);
        addr = helper.getString("addr", null);
        geo = helper.getString("geo", null);
        message = helper.getString("message", null);
        if(object.has("images")){
            try {
                JSONArray array = helper.getJSONArray("images", null);
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        Images item = new Images(array.getJSONObject(i));
                        images.add(item);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        time = helper.getString("time", null);
        price = helper.getString(new String[]{"price","way"}, null);
        season = helper.getString(new String[]{"season","reason"}, null);
        tips = helper.getString("tips",null);
        url = helper.getString("url", null);

    }



    public String getName(){
        return name;
    }
    public String getDesc(){
        return desc;
    }
    public String getAddr(){
        return addr;
    }
    public String getGeo(){
        return geo;
    }
    public ArrayList<Images> getImages(){
        return images;
    }
    public String getTime(){
        return time;
    }
    public String getPrice(){
        return price;
    }
    public String getSeason(){
        return season;
    }

    public String getTips(){
        return tips;
    }

    public String getUrl(){
        return url;
    }

    public static class Images implements Serializable{
        private String title;
        private String image;

        public Images(JSONObject object){
            JSONObjectHelper helper = new JSONObjectHelper(object);
            title = helper.getString("title",null);
            image = helper.getString("image", null);
        }
        public String getTitle(){
            return title;
        }
        public String getImage(){
            return image;
        }
    }

    public String getMessage(){
        return message;
    }



}
