package com.trs.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.trs.constants.Constants;
import net.endlessstudio.util.Util;

import java.util.HashMap;

/**
 * Created by john on 14-3-19.
 */
public class ViewDisplayer {
	private static HashMap<String, String> sTypeActivityNameMap = new HashMap<String, String>();
	public static void initialize(Context context){
		sTypeActivityNameMap.putAll(Util.simpleProperty2HashMap(context, Constants.BASE_TYPE_ACTIVITY_MAP_PATH));
		sTypeActivityNameMap.putAll(Util.simpleProperty2HashMap(context, Constants.EXT_TYPE_ACTIVITY_MAP_PATH));
	}

	public static void showFragmentActivity(Context context, String type){
		//TODO
	}

	public static void showMainActivity(Context context){
		showMainActivity(context, TRSApplication.app().getFirstClassMenu().getType());
	}

	public static void showMainActivity(Context context, String type){
		String activityName = sTypeActivityNameMap.get(type);
		if(activityName != null){
			Intent intent = new Intent();
			intent.setClassName(context, activityName);
			if(!(context instanceof Activity)){
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}

			context.startActivity(intent);
		}
	}
}
