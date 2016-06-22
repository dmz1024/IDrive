package com.froyo.commonjar.network;

import java.io.UnsupportedEncodingException;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.froyo.commonjar.activity.BaseActivity;

public class GetRequest extends Request<JSONObject> {

	private RespListener mListener;

	public GetRequest(String url, RespListener listener,
			ErrorListener errorListener) {
		super(Method.GET, url, errorListener);
		this.mListener = listener;
	}

	public GetRequest(final Context activity, String url,
			final RespListener listener) {
		this(url, listener, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				listener.doFailed();
			}
		});
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			return Response.success( new JSONObject(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException e) {
			return Response.error(new ParseError(e));
		}
	}

	// 设置网络超时
	@Override
	public RetryPolicy getRetryPolicy() {

		RetryPolicy retryPolicy = new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		return retryPolicy;

	}

	@Override
	protected void deliverResponse(JSONObject response) {
		mListener.onResponse(response);
	}
}
