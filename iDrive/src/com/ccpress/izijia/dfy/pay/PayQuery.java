package com.ccpress.izijia.dfy.pay;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.ccpress.izijia.activity.MainActivity;
import com.ccpress.izijia.dfy.activity.BaseActivity;
import com.ccpress.izijia.dfy.activity.OrderActivity;
import com.ccpress.izijia.dfy.activity.PayActivity;
import com.ccpress.izijia.dfy.activity.PayOkActivity;
import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.entity.PayDetail;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.yd.activity.YdPayActivity;
import com.ccpress.izijia.yd.constant.ConstantApi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmz1024 on 2016/4/11.
 */
public class PayQuery {
    private String id;
    private Context ctx;
    private int type;
    private int from=0;

    public PayQuery(Context ctx, String id) {
        this.id = id;
        this.ctx = ctx;
    }

    public void getPayResult(final int type, int from) {
        this.from = from;
        getPayResult(type);
    }

    public void getPayResult(final int type) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("order_id", id);
        final ProgressDialog pdlog = new ProgressDialog(ctx);
        pdlog.setMessage("获取支付结果...");
        pdlog.show();
        String url = "";
        this.type = type;
        switch (type) {
            case 1:
                url = Constant.DFY_ALIPAY_QUERY;
                break;
            case 2:
                break;
            case 3:
                url = Constant.DFY_UPOP_QUERY;
                break;
            case 4:
                url = ConstantApi.YD_UPOP_QUERY;
                break;
            case 5:
                url = ConstantApi.YD_ALIPAY_QUERY;
                break;
        }

        NetUtil.Post(url, map, new MyCallBack() {
            @Override
            public void onSuccess(String json) {
                Log.d("json", json);
                if (type == 3 || type == 4) {
                    json = "{" + json.substring(3);
                }
                PayDetail payDetail = JsonUtil.getJavaBean(json, PayDetail.class);
                if (payDetail.getResult().equals("0")) {
                    Intent intent = new Intent(ctx, PayOkActivity.class);
                    intent.putExtra("payDetail", payDetail);
                    ctx.startActivity(intent);
                    ((BaseActivity) ctx).finish();

                } else {
                    goOrderActivity();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                goOrderActivity();
            }

            @Override
            public void onFinished() {
                pdlog.dismiss();
            }
        });
    }

    private void goOrderActivity() {
        CustomToast.showToast("获取支付结果失败，请至订单中心查看", 3500);
        Intent intent = new Intent(ctx, MainActivity.class);
        ctx.startActivity(intent);
        ((BaseActivity) ctx).finish();
    }
}
