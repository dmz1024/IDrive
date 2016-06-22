package com.ccpress.izijia.dfy.pay;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmz1024 on 2016/4/8.
 * 银联支付类
 */
public class Pay_yl {
    private Context ctx;
    private String id;
    private String url;

    public Pay_yl(Context ctx, String id, String url) {
        this.ctx = ctx;
        this.id = id;
        this.url = url;
    }

    public void pay() {
        final ProgressDialog pdlog = new ProgressDialog(ctx);
        pdlog.setMessage("获取支付信息...");
        pdlog.show();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("order_id", id);
        NetUtil.Post(url, map, new MyCallBack() {
            @Override
            public void onSuccess(String s) {
                try {
                    String str = "{" + s.substring(3);
                    Log.d("str", str);
                    JSONObject object = new JSONObject(str);
                    String result = object.getString("result");
                    String info = object.getString("dates");
                    Log.d("result", result);
                    Log.d("info", info);
                    String serverMode = "00";
//                    UPPayAssistEx.startPayByJAR(ctx, PayActivity.class, null, null, info, serverMode);
                    UPPayAssistEx.startPay(ctx, null, null, info, serverMode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinished() {
                pdlog.cancel();
            }
        });
    }
}
