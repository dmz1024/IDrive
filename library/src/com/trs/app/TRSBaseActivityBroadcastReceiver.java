package com.trs.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.lang.ref.WeakReference;

/**
 * Created by pompomandtinman on 9/24/13.
 */
public class TRSBaseActivityBroadcastReceiver extends BroadcastReceiver implements TRSConstants{

	private WeakReference<Activity> mActivity;
	public TRSBaseActivityBroadcastReceiver(Activity activity){
		mActivity = new WeakReference<Activity>(activity);
	}

	public void onActivityCreated(){
		Activity activity = mActivity.get();
		if(activity != null){
			IntentFilter filter = new IntentFilter();
			filter.addAction(ACTION_EXIT);
			activity.registerReceiver(this, filter);
		}
	}

	public void onActivityDestroyed(){
		Activity activity = mActivity.get();
		if(activity != null){
			activity.unregisterReceiver(this);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(ACTION_EXIT.equals(intent.getAction())){
			finish();
		}
	}

	private void finish(){
		Activity activity = mActivity.get();
		if(activity != null){
			activity.finish();
		}
	}
}
