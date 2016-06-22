package com.trs.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by john on 13-11-28.
 */
public class LoadDataTimeManager {
	public static final String DATA_KEY = "com.trs.key.load_data_time";

	public static void onDataLoad(Context context, String url){
		SharedPreferences sp = context.getSharedPreferences(DATA_KEY, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putLong(getKey(url), System.currentTimeMillis());
	}

	public static long getLoadTime(Context context, String url){
		SharedPreferences sp = context.getSharedPreferences(DATA_KEY, Context.MODE_PRIVATE);
		return sp.getLong(getKey(url), -1);
	}

	private static String getKey(String url){
		return String.valueOf(url.hashCode());
	}


}
