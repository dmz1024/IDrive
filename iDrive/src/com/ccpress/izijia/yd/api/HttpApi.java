package com.ccpress.izijia.yd.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.trs.util.log.Log;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/11.
 */
public abstract class HttpApi {
    private RequestParams params;
    private ProgressDialog pd;
    private Handler handler;

    /**
     * http类
     *
     * @param url
     * @param map
     */
    public HttpApi(String url, Map<String, String> map) {
        params = new RequestParams(url);
        StringBuffer sf = new StringBuffer(url);
        if (null != map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.addParameter(entry.getKey(), entry.getValue());
                sf.append("&" + entry.getKey() + "=" + entry.getValue());
            }
        }
        Log.d("提交uri", sf.toString());

    }

    public void post() {
        x.http().post(params, new MyCallback());
    }

    public void get() {
        x.http().get(params, new MyCallBack());
    }

    public void get(Context ctx, String msg) {
        pd = new ProgressDialog(ctx);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(msg);
        pd.show();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                get();
            }
        }, 400);
    }


    public void post(Context ctx, String msg) {
        pd = new ProgressDialog(ctx);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(msg);
        pd.show();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                post();
            }
        }, 400);
    }


    protected abstract void success(String json);

    class MyCallback implements Callback.CommonCallback<String> {

        @Override
        public void onSuccess(String json) {
            success(json);
        }

        @Override
        public void onError(Throwable throwable, boolean b) {
            error();
        }

        @Override
        public void onCancelled(CancelledException e) {

        }

        @Override
        public void onFinished() {
            if (pd != null) {
                pd.cancel();
            }
        }
    }

    public void error() {
    }
}
