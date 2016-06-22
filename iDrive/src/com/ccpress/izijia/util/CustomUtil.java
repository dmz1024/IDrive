package com.ccpress.izijia.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.portal.LoginActivity;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.froyo.commonjar.utils.SpUtil;
import com.trs.util.AsyncTask;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;
import net.endlessstudio.util.Util;
import net.endlessstudio.util.json.JSONObjectHelper;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WLH on 2015/10/10 17:28.
 * 定制工具类
 */
public class CustomUtil {


    public static void CustomOrCancel(final Context context, String docid, String type,
                                       final boolean isCustom, final PraiseUtil.ResultCallback callback){

        SpUtil sp = new SpUtil(context);
        String uid = sp.getStringValue(Const.UID);
        String token = sp.getStringValue(Const.AUTH);

        if(StringUtil.isEmpty(uid)){//如果未登录，跳转到登录页面
            Intent intent=new Intent(context, LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_NOT_GOTO_HOMEPAGE, true);
            context.startActivity(intent);
            return;
        }

        String url = Constant.INTERACT_URL_BASE_SERVICE;

        if(isCustom ){
            url+=Constant.INTERACT_CUSTOM_CANCEL;
        }else {
            url+=Constant.INTERACT_CUSTOM;
        }

        Log.e("WLH", "CustomOrCancel url:" + url);

        final Map<String, String> map = new HashMap<>();
        map.put("docid", docid);
        map.put("type", type);
        map.put("token", token);
        map.put("uid", uid);

        final Dialog dialog = DialogUtil.getProgressdialog(context, "请等待");
        dialog.show();
      PostTask task = new PostTask(context, map){
            @Override
            protected void onPostExecute(String message) {
                dialog.cancel();
                message =customResponse(context, message);
                if(StringUtil.isEmpty(message)){
                    if(isCustom){
                        message= "取消定制失败";
                    }else {
                        message="定制失败";
                    }
                }
                if(message.contains("已")||message.contains("成功")){
                    DialogUtil.showResultDialog(context, message, R.drawable.icon_success);
                    callback.callback(true);
                }else {
                    DialogUtil.showResultDialog(context, message, R.drawable.icon_delete);
                    callback.callback(false);
                }

            }
        };
        task.execute(url);

    }

    public static void CustomOrCancel(final Context context, final String docid, final boolean isCustom, final ResultCallback callback){

        String url=null;
        if(isCustom ){
            DialogUtil.showResultDialog(context, "请去我的定制删除", R.drawable.icon_delete);
                return;
        }else {
            url=PersonalCenterUtils.buildUrl(context,Constant.ADD_CUSTOM_INNER + "&source_type=0" + "&source_id=" + docid);
            Log.e("CustomOrCancel ",url );
        }
        final Map<String, String> map = new HashMap<>();
        final Dialog dialog = DialogUtil.getProgressdialog(context, "请等待");
        dialog.show();
        PostTask task = new PostTask(context, map){
            @Override
            protected void onPostExecute(String message) {
                dialog.cancel();
                message = customResponse(context, message);
                Log.e("onPostExecute ", message);
                DialogUtil.showResultDialog(context, "定制成功", R.drawable.icon_success);
                callback.callback(true);
            }
        };
        task.execute(url);

    }


    /**
     * postTask
     */
    public static class PostTask extends AsyncTask<String, Object, String> {
        private Context context;
        private  Map<String, String> map;
        public PostTask(Context context, Map<String, String> map) {
            this.context = context;
            this.map = map;
        }

        @Override
        protected String doInBackground(String... params) {
            String json= null;
            try {
                Util.HttpResult result = Util.doUrlEncodedFormPost(params[0], map);

                if(result != null){
                    json = result.getResponseString(HTTP.UTF_8);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
        }
    }

    public static String customResponse(Context mContext, String result){
        String str = null;
        if(!StringUtil.isEmpty(result)){
            try {
                JSONObjectHelper helper = new JSONObjectHelper(result);
                str = helper.getString("message","");
                int code  = helper.getInt("code", 1);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return str;
    }

    public static interface ResultCallback {
        public void callback(boolean isSuccess);
    }
}
