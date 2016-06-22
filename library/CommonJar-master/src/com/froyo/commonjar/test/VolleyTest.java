package com.froyo.commonjar.test;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.GetRequest;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;

public class VolleyTest {

	public void postMethod(BaseActivity activity, String url) {
		PostParams param = new PostParams();
		param.put("username", "1");
		// param.put("photo", file);（post提交图片或者文件:文件类型）
		RequestQueue mQueue = Volley.newRequestQueue(activity);
		PostRequest req = new PostRequest(activity, param, url,
				new RespListener(activity) {
					@Override
					public void getResp(JSONObject obj) {
						// 返回的json数据
					}
				});
		mQueue.add(req);
		mQueue.start();
		// 开始访问时，可以显示一个对话框，对话框将会在请求结果时，自动关闭
		activity.showDialog();
	}

	public void getMethod(BaseActivity activity, String url) {
		RequestQueue mQueue = Volley.newRequestQueue(activity);
		GetRequest req = new GetRequest(activity, url, new RespListener(
				activity) {

			@Override
			public void getResp(JSONObject obj) {
				// 返回的json数据
			}
		});
		mQueue.add(req);
		mQueue.start();
		// 开始访问时，可以显示一个对话框，对话框将会在请求结果时，自动关闭
		activity.showDialog();
	}

	public void downSingleImage(BaseActivity activity, String url) {
		RequestQueue mQueue = Volley.newRequestQueue(activity);
		ImageRequest imgRequest = new ImageRequest(url,
				new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap bitmap) {
						// 得到返回的图片
					}
				}, 0, 0, Config.ARGB_8888, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {

					}
				});
		mQueue.add(imgRequest);
		mQueue.start();
	}

}
