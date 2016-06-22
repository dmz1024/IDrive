package com.ccpress.izijia.dfy.util;

import com.ccpress.izijia.dfy.entity.Collect;
import com.ccpress.izijia.dfy.entity.Goodinfo;
import com.ccpress.izijia.dfy.entity.UserInfo;
import com.ccpress.izijia.vo.UserVo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by administror on 2016/3/18 0018.
 */
public class JsonUtil {
    /**
     * 解析json数据
     *
     * @param json
     * @return
     */
    public static <T> List<T> json2List(String json, Class<T> tClass, String data) {

        if (json == null) {
            return null;
        }

        try {
            List<T> list = new ArrayList<T>();
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.getJSONArray(data);
            Gson gson = new Gson();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject j = jsonArray.getJSONObject(i);
                    T t = gson.fromJson(j.toString(), tClass);
                    list.add(t);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Goodinfo json2GoodInfo(String json) {
        Gson gson = new Gson();
        Goodinfo goodinfo = gson.fromJson(json, Goodinfo.class);
        return goodinfo;
    }


    public static List<Collect> json2Collect(String json) {
        if (json == null) {
            return null;
        }

        try {
            List<Collect> list = new ArrayList<Collect>();
            JSONObject object = new JSONObject(json);
            JSONObject jsonData = object.getJSONObject("data");
            JSONArray jsonArray=jsonData.getJSONArray("list");
            Gson gson = new Gson();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject j = jsonArray.getJSONObject(i);
                    Collect t = gson.fromJson(j.toString(), Collect.class);
                    list.add(t);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取javaBean
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T getJavaBean(String json, Class<T> cls) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return t;
        }
        return t;
    }


    public static <T> List<T> getListJavaBean(String jsonString,  Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString, new TypeToken<List<T>>() {
            }.getType());
        } catch (Exception e) {
        }
        return list;
    }



    public static List<Map<String, Object>> getListMap(String jsonString) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString,
                    new TypeToken<List<Map<String, Object>>>() {
                    }.getType());
        } catch (Exception e) {
            // TODO: handle exception
        }
        return list;
    }


}


