package com.ccpress.izijia.dfy.pay;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.alipay.sdk.app.PayTask;
import com.ccpress.izijia.dfy.activity.BaseActivity;
import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.NetUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by administror on 2016/4/1 0001.
 * 支付宝付款封装类
 */
public class Pay_zfb {
    private String info;//从服务器返回的支付信息
    private static final int ZFB_PAY_FLAG = 1;
    private String id;
    private Context ctx;
    private String url;
    private int type;

    public Pay_zfb(String id, Context ctx, String url, int type) {
        this.id = id;
        this.ctx = ctx;
        this.url = url;
        this.type = type;
    }

    public void Pay() {
        final ProgressDialog pdlog = new ProgressDialog(ctx);
        pdlog.setMessage("获取支付信息...");
        pdlog.show();

        Map<String, String> map = new HashMap<String, String>();
        map.put("order_id", id);
        NetUtil.get(url, map, new MyCallBack() {
            @Override
            public void onSuccess(String s) {
                Log.d("s", s);
                try {
                    JSONObject object = new JSONObject(s);
                    final String info = object.getString("dates");
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            PayTask alipay = new PayTask((BaseActivity) ctx);
                            String result = alipay.pay(info, true);
                            Message msg = Message.obtain();
                            msg.what = ZFB_PAY_FLAG;
                            msg.obj = result;
                            handler.sendMessage(msg);
                        }
                    };

                    Thread thread = new Thread(runnable);
                    thread.start();
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ZFB_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        new PayQuery(ctx, id).getPayResult(type == 1 ? 1 : 5);
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            CustomToast.showToast("支付结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            CustomToast.showToast("支付失败");
                        }

                    }
                    break;
                }
            }
        }

    };
}
