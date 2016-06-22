
package com.trs.app;

import android.content.Context;
import android.content.SharedPreferences;
import com.trs.wcm.RemoteDataService;
import com.trs.wcm.callback.BaseDataAsynCallback;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ApplicationConfig {

    /**
     * 项目的标识，主要用来标识存储目录的根路径
     */
    public static final String PROJECT_PACKAGE_NAME = "com.trs.xizang.voice";

    public final static int SHARE_TO_NETEASE = 0;
    public final static int SHARE_TO_SINA = 1;
    public final static int SHARE_TO_QQ = 2;
    public final static String ACCESS_TOKEN = "token";
    public final static String ACCESS_TOKEN_SECRET = "secret";
    public final static String ACCESS_USERID = "userid";
    public final static String NOTIFY_LPT_URL = "notifylpturl";
    public final static String NOTIFY_URL = "notifyurl";
    public final static String APP_PACKAGE_NAME = "com.trs.xizang.voice";
    public final static String WEIBO_TYPE = "wbtype";
    public final static String TOKEN = "wtoken";
    public final static String UID = "wuid";
    public final static String EXPIRE = "wexp";
    public final static String OFFICE_UID = "wouid";
    public final static String TOKEN_SECRET = "wbtokensecret";
    public final static String WBAPPKEY = "wbappkey";
    public final static String WBCALLBACK = "wbcallback";
    public final static String WBAPPSECRET = "wbappsecret";
	public final static String UPDATEURL = "updateurl";

	public static interface Listener{
		public void onSuccess();
		public void onError();
	}

    private Map<String, String> mConfigObj;

    private static final ApplicationConfig mInstance = new ApplicationConfig();
	public static ApplicationConfig getInstance() {
		return mInstance;
	}

    private ApplicationConfig() {
        mConfigObj = new HashMap<String, String>();
    }

    /**
     * 初始化系统配置，该请求是异步的，所以需要传入回答函数
     */
    public void initAppConfig(final Context context, String url, final Listener listener) {
		loadCachedConfig(context);

        RemoteDataService oRemoteDataService = new RemoteDataService(context);

	    String csUrl = url;
		if(csUrl == null) {
			return;
		}

        oRemoteDataService.alwaysLoadJSON(csUrl, new BaseDataAsynCallback() {

            @Override
            public void onDataLoad(String result) {

                try {
					JSONObject obj = new JSONObject(result);

                    for (Iterator iterator = obj.keys(); iterator.hasNext();) {
                        String sConfigName = (String)iterator.next();
                        setConfigValue(sConfigName, obj.getString(sConfigName));
                    }

					saveConfigToCache(context);

                    if(listener != null){
						listener.onSuccess();
					}

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String _result) {
				if(listener != null){
					listener.onError();
				}
            }
        });
    }

	private void loadCachedConfig(Context context){
		SharedPreferences sp = getSp(context);
		for(String key: sp.getAll().keySet()){
			mConfigObj.put(key, sp.getString(key, null));
		}
	}

	public void saveConfigToCache(Context context){
		SharedPreferences.Editor editor = getSp(context).edit();
		editor.clear();

		for(String key: mConfigObj.keySet()){
			editor.putString(key, mConfigObj.get(key));
		}

		editor.commit();
	}

	/**
	 * 是否存在缓存数据
	 * @param context
	 * @return
	 */
	public boolean hasCachedValue(Context context){
		return getSp(context).getAll().size() > 0;
	}

    /**
     * 获取指定的配置项
     * @param sConfigName
     * @return
     */
    public String getConfigValue(String sConfigName) {
        return mConfigObj.get(sConfigName.toUpperCase());
    }

	/**
	 * 添加配置项
	 * @param sConfigName
	 * @param sConfigValue
	 */
    public void setConfigValue(String sConfigName, String sConfigValue) {
        mConfigObj.put(sConfigName.toUpperCase(), sConfigValue);
    }

	private SharedPreferences getSp(Context context){
		return context.getSharedPreferences(getSPKey(context), Context.MODE_PRIVATE);
	}

	private String getSPKey(Context context){
		return String.format("%s_app_cfg", context.getPackageName());
	}
}
