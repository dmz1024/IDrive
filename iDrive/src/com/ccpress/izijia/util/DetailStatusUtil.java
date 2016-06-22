package com.ccpress.izijia.util;

import android.content.Context;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.froyo.commonjar.utils.SpUtil;
import com.trs.util.log.Log;
import com.trs.wcm.callback.IDataAsynCallback;
import net.endlessstudio.util.Util;
import net.endlessstudio.util.json.JSONObjectHelper;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WLH on 2015/10/9 12:01.
 * 获取详情状态
 */
public class DetailStatusUtil {


    private boolean isPraise;
    private boolean isFavorite;
    private boolean isCustom;
    private boolean isCart;

    private Context context;
    protected final Handler handler = new Handler();

    public DetailStatusUtil(Context context){
        this.context = context;
    }

    public void getDetailStatus(String docid, String type, final IDataAsynCallback callback){
        SpUtil sp = new SpUtil(context);
        String uid = sp.getStringValue(Const.UID);
        String token = sp.getStringValue(Const.AUTH);

        final String url = Constant.INTERACT_URL_BASE_SERVICE + Constant.INTERACT_DETAIL_STATUS;//String.format(Constant.INTERACT_URL_BASE_SERVICE + Constant.INTERACT_DETAIL_STATUS, docid, type, token, uid);


        final Map<String, String> map = new HashMap<>();
        map.put("docid", docid);
        map.put("type", type);
        map.put("token", token);
        map.put("uid", uid);

        Log.e("WLH", url +"&docid="+docid
                + "&type="+type + "&token="+token + "&uid="+uid);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    final String sJSONContent = Util.getString(context, url, HTTP.UTF_8);
                    Util.HttpResult result = Util.doUrlEncodedFormPost(url, map);

                    if(result != null){

                        final String sJSONContent = result.getResponseString(HTTP.UTF_8);

                        try {
                            JSONObject object = new JSONObject(sJSONContent);
                            JSONObject data = object.getJSONObject("datas");
                            JSONObjectHelper helper = new JSONObjectHelper(data);

                            isPraise = helper.getInt("isPraise",0)==1;
                            isFavorite = helper.getInt("isFavorite",0)==1;
                            isCustom = helper.getInt("isCustom",0)==1;
                            isCart = helper.getInt("isCart",0)==1;

                            Log.e("isCustom",String.valueOf(isCustom));

                            if(object.getInt("code")==0){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onDataLoad(sJSONContent, false);
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("WLH", "getDetailStatus Exception000:"+e.toString());
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("WLH", "getDetailStatus IOException:"+e.toString());
                }
            }
        }).start();
    }
    public void getDetailStatus(String docid, final IDataAsynCallback callback){


        final String url = PersonalCenterUtils.buildUrl(context,Constant.CHECK_CUSTOM_INNER + "&fav_id=" +docid);
        Log.e("getDetailStatus ", url);

        final Map<String, String> map = new HashMap<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Util.HttpResult result = Util.doUrlEncodedFormPost(url, map);
                    if(result != null){
                        final String sJSONContent = result.getResponseString(HTTP.UTF_8);
                        Log.e("WLH", "getDetailStatus sJSONContent:"+sJSONContent);
                        try {
                            JSONObject object = new JSONObject(result.getResponseString("utf_8"));
                                String msg=object.get("msg").toString();
                            Log.e("msg", msg);
                                if (msg.equals("已定制")){
                                    isCustom=true;
                                }else {
                                    isCustom=false;
                                }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("WLH", "getDetailStatus Exception000:"+e.toString());
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("WLH", "getDetailStatus IOException:"+e.toString());
                }
            }
        }).start();
    }

    public boolean IsPraise(){
        return isPraise;
    }
    public boolean IsFavorite(){
        return isFavorite;
    }

    public boolean IsCustom(){
        return isCustom;
    }
    public boolean IsCart(){
        return isCart;
    }

    public void setIsPraise(boolean isPraise){
        this.isPraise = isPraise;
    }

    public void setIsFavorite(boolean isFavorite){
        this.isFavorite = isFavorite;
    }
    public void setIsCustom(boolean isCustom){
        this.isCustom = isCustom;
    }
    public void setIsCart(boolean isCart){
        this.isCart = isCart;
    }

    public void setDetailStatus(ImageView iconPraise,ImageView iconFavorite, ImageView iconCustom, ImageView iconCart,
          TextView txtPraise, TextView txtFavorite, TextView txtCustom, TextView txtCart, int docid, int type){
        SpUtil sp = new SpUtil(context);
        String uid = sp.getStringValue(Const.UID);
        String token = sp.getStringValue(Const.AUTH);

        final String url = String.format(Constant.INTERACT_URL_BASE_SERVICE + Constant.INTERACT_DETAIL_STATUS,
                docid, type, token, uid);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String sJSONContent = Util.getString(context, url, HTTP.UTF_8);

                    try {
                        JSONObject object = new JSONObject(sJSONContent);
                        JSONObject data = object.getJSONObject("data");
                        isPraise = data.getInt("isPraise")==1;
                        isFavorite = data.getInt("isFavorite")==1;
                        isCustom = data.getInt("isCustom")==1;
                        isCart = data.getInt("isCart")==1;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setDetailStatus(ImageView iconPraise,ImageView iconFavorite, ImageView iconCustom, ImageView iconCart,
                                TextView txtPraise, TextView txtFavorite, TextView txtCustom, TextView txtCart, int docid){
        SpUtil sp = new SpUtil(context);
        String uid = sp.getStringValue(Const.UID);
        String token = sp.getStringValue(Const.AUTH);

        final String url = String.format(Constant.INTERACT_URL_BASE_SERVICE + Constant.ADD_CUSTOM_INNER,
                docid, token, uid);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String sJSONContent = Util.getString(context, url, HTTP.UTF_8);

                    try {
                        JSONObject object = new JSONObject(sJSONContent);
                        JSONObject data = object.getJSONObject("data");
                        isPraise = data.getInt("isPraise")==1;
                        isFavorite = data.getInt("isFavorite")==1;
                        isCustom = data.getInt("isCustom")==1;
                        isCart = data.getInt("isCart")==1;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }



}
