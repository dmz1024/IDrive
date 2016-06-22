package com.trs.wcm;

import android.content.Context;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;
import com.trs.wcm.callback.IDataAsynCallback;

import java.io.IOException;

/**
 * Created by john on 13-11-21.
 */
abstract public class LoadWCMJsonTask {
	public static final String TAG = "LoadWCMJsonTask";

	abstract private class MyDataAsyncCallback implements IDataAsynCallback{
		@Override
		public void onDataLoad(String result, boolean bIsChanged) {
			try{
				onDataReceived(result, bIsChanged);
			}
			catch(Throwable t){
				onError(t);
			}
		}

		@Override
		final public void onError(String result) {
			onError(new IOException(result));
		}

		@Override
		public void onDataChanged() {
			//do nothing
		}

		abstract void onDataReceived(String result, boolean isChanged) throws Throwable;
		abstract void onError(Throwable t);
	}


	private class OnRemoteDataReceivedListener extends MyDataAsyncCallback {

		@Override
		public void onDataChanged() { /*do nothing*/ }

		@Override
		public void onDataReceived(String result, boolean isChanged) throws Throwable{
			Log.i(TAG, "on remote data received, is changed: " + isChanged);
			Log.i(TAG, "on remote end");
			if(mHasStart){
				LoadWCMJsonTask.this.onEnd();
			}
			Log.i(TAG, "on remote data received");
			LoadWCMJsonTask.this.onDataReceived(result, false);
		}

		@Override
		void onError(Throwable t) {
			Log.i(TAG, "on end");
			if(mHasStart){
				LoadWCMJsonTask.this.onEnd();
			}
			Log.i(TAG, "on error");
			LoadWCMJsonTask.this.onError(t);
		}
	}

	private class OnRefreshReceivedListener extends MyDataAsyncCallback {

		@Override
		public void onDataReceived(String result, boolean isChanged) throws Throwable{
			Log.i(TAG, "on refresh data received, is changed: " + isChanged);
			if(isChanged){
				Log.i(TAG, "on refresh data received, is changed: " + isChanged);
				Log.i(TAG, "on refresh end");
				Log.i(TAG, "on refresh data received");
				LoadWCMJsonTask.this.onDataReceived(result, false);
				if(mHasStart){
					LoadWCMJsonTask.this.onEnd();
				}
				if(mHasRefresh){
					LoadWCMJsonTask.this.onRefreshEnd();
				}
			}
			else{
				boolean cacheDataIsWrong = mCacheDataError != null;
				Log.i(TAG, "cache data is wrong: " + cacheDataIsWrong);
				if(mHasStart){
					LoadWCMJsonTask.this.onEnd();
				}
				if(mHasRefresh){
					LoadWCMJsonTask.this.onRefreshEnd();
				}

				if(cacheDataIsWrong/* and data not changed*/){
					Log.i(TAG, "on error");
					LoadWCMJsonTask.this.onError(mCacheDataError);
				}
			}
		}

		@Override
		void onError(Throwable t) {
			Log.i(TAG, "on refresh end");
			if(mHasStart){
				LoadWCMJsonTask.this.onEnd();
			}
			if(mHasRefresh){
				LoadWCMJsonTask.this.onRefreshEnd();
			}
			Log.i(TAG, "on refresh error");
			LoadWCMJsonTask.this.onError(t);
		}
	}

	private Context mContext;
	private String mURL;
	private Throwable mCacheDataError;
	private boolean mHasStart;
	private boolean mHasRefresh;
	public LoadWCMJsonTask(Context context){
		this.mContext = context;
	}

	/**
	 * Return cache data first, and then remote data
	 * @param url url
	 */
	public void start(String url){
		if(mHasStart){
			throw new RuntimeException("This task has been used, please create new one");
		}

		Log.i(TAG, "start: " + url);
		mHasStart = true;
		this.mURL = url;
		onStart();
		loadDataLocal();
	}

	/**
	 * Compare time stamp, if the same , return cache data, else remote data
	 * @param url url
	 */
	public void startRemote(String url){
		if(mHasStart){
			throw new RuntimeException("This task has been used, please create new one");
		}

		Log.i(TAG, "start remote: " + url);
		mHasStart = true;
		this.mURL = url;
		onStart();
		loadDataRemote();
	}

	/**
	 * Return remote data
	 * @param url url
	 */
	public void startAlwaysRemote(String url){
		if(mHasStart){
			throw new RuntimeException("This task has been used, please create new one");
		}

		Log.i(TAG, "start always remote: " + url);
		mHasStart = true;
		this.mURL = url;
		onStart();
		loadDataAlwaysRemote();
	}

	private void loadDataLocal(){
		Log.i(TAG, "load data local");

		RemoteDataService rms = new RemoteDataService(mContext);
		String result = rms.loadLocalJson(mURL);
		boolean hasCacheData = !StringUtil.isEmpty(result);
		if(hasCacheData){
			try {
				onDataReceived(result, true);
				if(mHasStart){
					onEnd();
				}

			} catch (Exception e) {
                e.printStackTrace();
				mCacheDataError = e;
			}
		}

		if(hasCacheData){
			onRefreshStart();
		}

		rms.loadJSON(mURL, new OnRefreshReceivedListener());
	}

	private void loadDataRemote(){
		Log.i(TAG, "load data remote");
		RemoteDataService rms = new RemoteDataService(mContext);
		rms.loadJSON(mURL, new OnRemoteDataReceivedListener());
	}

	private void loadDataAlwaysRemote(){
		Log.i(TAG, "load data always remote");
		RemoteDataService rms = new RemoteDataService(mContext);
		rms.alwaysLoadJSON(mURL, new OnRemoteDataReceivedListener());
	}

	/**
	 * 当开始请求数据时, 会调用
	 */
	protected void onStart(){
		mHasStart = true;
	}

	/**
	 * 当请求到数据后, 或者出错后会被调用
	 */
	protected void onEnd(){
		mHasStart = false;
	}

	/**
	 * 当直接返回的缓存数据, 并且需要再从服务器刷新数据时, 会被调用
	 */
	protected void onRefreshStart(){
		mHasRefresh = true;
	}

	/**
	 * 当获取到刷新数据后调用
	 */
	protected void onRefreshEnd(){
		mHasRefresh = false;
	}


	abstract public void onDataReceived(String result, boolean isCache) throws Exception;
	abstract public void onError(Throwable t);

	public Context getContext() {
		return mContext;
	}
}
