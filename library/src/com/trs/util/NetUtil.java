package com.trs.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by john on 13-11-18.
 */
public class NetUtil {
	/**
	 * 检测网络是否连接（注：需要在配置文件即AndroidManifest.xml加入权限）
	 *
	 * @param context
	 * @return true : 网络连接成功
	 * @return false : 网络连接失败
	 * */
	public static boolean isConntected(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		if(null == context){
			return false;
		}
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			// 获取网络连接管理的对象
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null) { // info.isAvailable()当前网络不可用
				// 判断当前网络是否已经连接
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

}
