package com.ccpress.izijia.util;

import android.app.Dialog;
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
 * Created by WLH on 2015/10/10 15:07.
 */
public class CollectUtil {


    public static void CollectOrCancel(final Context context, String docid, String type,
            final boolean isCollect, final PraiseUtil.ResultCallback callback){

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

        if(isCollect ){
            url+=Constant.INTERACT_COLLECT_CANCEL;
        }else {
            url+=Constant.INTERACT_COLLECT;
        }



        final Map<String, String> map = new HashMap<>();
        map.put("docid", docid);
        map.put("type", type);
        map.put("token", token);
        map.put("uid", uid);

        Log.e("WLH", "CollectOrCancel url:" + url +" docid:"+docid+ " type:"+type+" token:"+token+" uid:"+uid);

        final Dialog dialog = DialogUtil.getProgressdialog(context, "请等待");
        dialog.show();
        PraiseUtil.PostTask task = new PraiseUtil.PostTask(context, map){
            @Override
            protected void onPostExecute(String message) {
                dialog.cancel();
                message = PraiseUtil.parseResponse(context, message);
                if(StringUtil.isEmpty(message)){
                    if(isCollect){
                        message= "取消收藏失败";
                    }else {
                        message="收藏失败";
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

}
