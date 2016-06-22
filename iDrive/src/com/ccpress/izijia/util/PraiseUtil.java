package com.ccpress.izijia.util;

import android.content.Context;
import android.content.Intent;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.portal.LoginActivity;
import com.ccpress.izijia.constant.Const;
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
 * Created by WLH on 2015/10/9 15:19.
 */
public class PraiseUtil {

    public static void PraiseOrCancel(final Context context, String docid, String type,
               final boolean isPraise, final ResultCallback callback){

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

        if(isPraise ){
            url+=Constant.INTERACT_PRAISE_CANCEL;
        }else {
            url+=Constant.INTERACT_PRAISE;
        }

        Log.e("WLH", "PraiseOrCancel url:" + url);

        final Map<String, String> map = new HashMap<>();
        map.put("docid", docid);
        map.put("type", type);
        map.put("token", token);
        map.put("uid", uid);

        PostTask task = new PostTask(context, map){
            @Override
            protected void onPostExecute(String message) {
                message = parseResponse(context, message);
                if(StringUtil.isEmpty(message)){
                    if(isPraise){
                        message= "取消点赞失败";
                    }else {
                        message="点赞失败";
                    }
                }
                if(message.contains("成功")){
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

    public static String parseResponse(Context mContext, String result){
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
