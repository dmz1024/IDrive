package com.froyo.commonjar.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
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

public class PostRequest extends Request<JSONObject> {
	private RespListener listener = null;
	private PostParams params = null;
	private HttpEntity httpEntity = null;

	public PostRequest(final BaseActivity activity,
			PostParams params, String url,
			final RespListener listener) {
		super(Method.POST, url, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				activity.dismissDialog();
				if (error instanceof NoConnectionError) {
					activity.toast("请检查网络");
				}
				listener.doFailed();
			}
		});
		this.params = params;
		this.listener = listener;
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (params != null) {
			httpEntity = params.getEntity();
			try {
				httpEntity.writeTo(baos);
			} catch (IOException e) {
			}
		}
		return baos.toByteArray();
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			return Response.success(new JSONObject(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(response));
		} catch (JSONException e) {
			return Response.error(new ParseError(response));
		}
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders();
		if (null == headers || headers.equals(Collections.emptyMap())) {
			headers = new HashMap<String, String>();
		}
		return headers;
	}

	@Override
	public String getBodyContentType() {
		return httpEntity.getContentType().getValue();
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		listener.onResponse(response);
	}

	@Override
	public RetryPolicy getRetryPolicy() {
		RetryPolicy retryPolicy = new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		return retryPolicy;
	}
}
