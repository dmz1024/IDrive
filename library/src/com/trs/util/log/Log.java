package com.trs.util.log;

import com.trs.mobile.BuildConfig;

/**
 * Created by pompomandtinman on 9/23/13.
 */
public class Log {
	public static final boolean SHOW_LOG = true;

	private static final boolean INNER_SHOW_LOG = BuildConfig.DEBUG && SHOW_LOG;


	public static void v(String tag, String message){
		if(INNER_SHOW_LOG) android.util.Log.v(tag, message);
	}

	public static void v(String tag, String message, Throwable t){
		if(INNER_SHOW_LOG) android.util.Log.v(tag, message, t);
	}

	public static void d(String tag, String message){
		if(INNER_SHOW_LOG) android.util.Log.d(tag, message);
	}

	public static void d(String tag, String message, Throwable t){
		if(INNER_SHOW_LOG) android.util.Log.d(tag, message);
	}

	public static void i(String tag, String message){
		if(INNER_SHOW_LOG) android.util.Log.i(tag, message);
	}

	public static void i(String tag, String message, Throwable t){
		if(INNER_SHOW_LOG) android.util.Log.i(tag, message, t);
	}

	public static void w(String tag, String message){
		if(INNER_SHOW_LOG) android.util.Log.w(tag, message);
	}

	public static void w(String tag, String message, Throwable t){
		if(INNER_SHOW_LOG) android.util.Log.w(tag, message, t);
	}

	public static void e(String tag, String message){
		if(INNER_SHOW_LOG) android.util.Log.e(tag, message);
	}
	public static void e(String tag, String message, Throwable t){
		if(INNER_SHOW_LOG) android.util.Log.e(tag, message, t);
	}
}
