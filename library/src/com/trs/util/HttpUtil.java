
package com.trs.util;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpUtil {

    private static final String TAG = "HttpHelper";

    /**
     * 连接超时的时间
     */
    public static int TIMEOUT_CONNECTION = 40000;

    /**
     * 读超时的时间
     */
    public static int TIMEOUT_SOCKET = 60000;

    /**
     * 发出get请求
     *
     * @param sUrl
     * @return 返回请求的结果
     * @throws java.io.IOException
     * @throws org.apache.http.client.ClientProtocolException
     */
    public static String doGet(String sUrl) throws ClientProtocolException, IOException {
        return doGet(sUrl, TIMEOUT_CONNECTION, TIMEOUT_SOCKET);
    }

    /**
     * @param sUrl
     * @param nTimeout
     * @return
     * @throws java.io.IOException
     * @throws org.apache.http.client.ClientProtocolException
     */
    public static String doGet(String sUrl, int nTimeout) throws ClientProtocolException,
			IOException {
        return doGet(sUrl, nTimeout, nTimeout);
    }

    /**
     * @param sUrl
     * @param nTimeoutConn
     * @param nTimeoutSocket
     * @return
     * @throws java.io.IOException
     * @throws org.apache.http.client.ClientProtocolException
     */
    public static String doGet(String sUrl, int nTimeoutConn, int nTimeoutSocket)
            throws ClientProtocolException, IOException {
        if (StringUtil.isEmpty(sUrl)) {
            return null;
        }

        String sResult = null;

        Log.d(TAG, "开始请求：" + sUrl);

        HttpGet httpRequest = new HttpGet(sUrl);
        HttpParams httpParams = new BasicHttpParams();

        // 设置超时时间
        HttpConnectionParams.setConnectionTimeout(httpParams, nTimeoutConn);
//        HttpConnectionParams.setSoTimeout(httpParams, nTimeoutSocket);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler());
        HttpResponse httpResponse = httpClient.execute(httpRequest);

        // 请求成功
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            Log.d(TAG, "请求成功:" + sUrl);

            sResult = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);

            Log.d(TAG, sUrl + "返回內容:" + sResult);

        } else {
            Log.w(TAG, "请求失败:" + sUrl);
        }

        httpClient.getConnectionManager().shutdown();

        return sResult;
    }

    /**
     * @param url
     * @param params
     * @return
     * @throws java.io.IOException
     * @throws org.apache.http.client.ClientProtocolException
     */
    public static String doPost(String url, String params) throws ClientProtocolException, IOException {
        return doPost(url, params, TIMEOUT_CONNECTION, TIMEOUT_SOCKET);
    }

    /**
     * @param url
     * @param params
     * @param nTimeout
     * @return
     * @throws java.io.IOException
     * @throws org.apache.http.client.ClientProtocolException
     */
    public static String doPost(String url, String params, int nTimeout) throws ClientProtocolException, IOException {
        return doPost(url, params, nTimeout, nTimeout);
    }

    /**
     * @param sUrl
     * @param params
     * @param nTimeoutConn
     * @param nTimeoutSocket
     * @return
     * @throws java.io.IOException
     * @throws org.apache.http.client.ClientProtocolException
     */
    public static String doPost(String sUrl, String params, int nTimeoutConn, int nTimeoutSocket)
            throws ClientProtocolException, IOException {

        if (StringUtil.isEmpty(sUrl)) {
            return null;
        }

        params = params == null? "": params;

        String sResult = null;

        // HttpPost连接对象
        HttpPost httpRequest = new HttpPost(sUrl);

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, nTimeoutConn);
        HttpConnectionParams.setSoTimeout(httpParams, nTimeoutSocket);

        httpRequest.setHeader(new BasicHeader("Content-type", "application/json"));

        httpRequest.setEntity((new ByteArrayEntity(params.getBytes())));

        // 取得默认的HttpClient
        HttpClient httpClient = new DefaultHttpClient();

        // 取得HttpResponse
        HttpResponse httpResponse = httpClient.execute(httpRequest);

        // httpstatus.sc_ok 表示连接成功
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            Log.d(TAG, "请求成功:" + sUrl);

            sResult = EntityUtils.toString(httpResponse.getEntity());

            Log.d(TAG, sUrl + "返回內容:" + sResult);
        }

        return sResult;
    }

}
