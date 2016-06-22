package com.ccpress.izijia.util;

import android.content.Context;
import android.content.Intent;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.portal.LoginActivity;
import com.ccpress.izijia.constant.Const;
import com.froyo.commonjar.utils.SpUtil;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WLH on 2015/10/10 10:20.
 */
public class CommentUtil {


    public static void commitComment(final Context context, String docid,
        String type, String content, final PraiseUtil.ResultCallback callback){

        SpUtil sp = new SpUtil(context);
        String uid = sp.getStringValue(Const.UID);
        String token = sp.getStringValue(Const.AUTH);

        if(StringUtil.isEmpty(uid)){//如果未登录，跳转到登录页面
            Intent intent=new Intent(context, LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_NOT_GOTO_HOMEPAGE, true);
            context.startActivity(intent);
            return;
        }

        String url = Constant.INTERACT_URL_BASE_SERVICE + Constant.INTERACT_ADDCOMMENT;


        final Map<String, String> map = new HashMap<>();
        map.put("docid", docid);
        map.put("type", type);
        map.put("content", content);
        map.put("token", token);
        map.put("uid", uid);

        Log.e("WLH", "PraiseOrCancel url:" + url +" docid:"+docid+" type:"+type+ " content:" +content +" token:"+token+" uid:"+uid);

        PraiseUtil.PostTask task = new PraiseUtil.PostTask(context, map){
            @Override
            protected void onPostExecute(String message) {
                message = PraiseUtil.parseResponse(context, message);
                if(StringUtil.isEmpty(message)){
                    message="提交评论失败~";
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

}
