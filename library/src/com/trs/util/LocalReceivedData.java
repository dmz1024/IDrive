package com.trs.util;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.trs.wcm.RemoteDataService;
import com.trs.wcm.callback.IDataAsynCallback;
import com.trs.mobile.R;

public class LocalReceivedData {
	private String result;
	private Context mCurrContext;
	private View mLoadLayout;
	private String mUrl;

	public LocalReceivedData(Context context,View loadlayout,String url) {
		this.mCurrContext = context;
		this.mLoadLayout = loadlayout;
		this.mUrl = url;
	}
	public void getResult() {
		requestNet();
	}
	
	/* 访问网络 */
	private void requestNet()
	{
		mLoadLayout.setVisibility(View.VISIBLE);
		RemoteDataService service = new RemoteDataService(mCurrContext);
		service.loadJSON(mUrl,new OnLocalDataReceivedListener());
	}
	
	
	class OnLocalDataReceivedListener extends OnDataReceivedListener
	{
		@Override
		public void onDataLoad(String result, boolean bIsChanged) {
			mLoadLayout.setVisibility(View.GONE);
			super.onDataLoad(result, bIsChanged);
			if(!bIsChanged){
				requestLocalNet();
			}
		}
	}
	
	private void requestLocalNet(){
		RemoteDataService rms = new RemoteDataService(mCurrContext);
		rms.loadLocalJSON(mUrl, new OnDataReceivedListener());
	}
	
	class OnDataReceivedListener implements IDataAsynCallback {
		@Override
		public void onDataChanged() {}
		@Override
		public void onDataLoad(String result_, boolean bIsChanged) {
			try {
				result = result_;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		@Override
		public void onError(String result) {
			Toast.makeText(mCurrContext,mCurrContext.getString(R.string.data_parse_wrong), Toast.LENGTH_LONG).show();
		}
	}
}
