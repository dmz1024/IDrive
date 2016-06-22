package com.trs.app;

import android.content.Context;
import android.os.Bundle;
import com.trs.frontia.FrontiaAPI;
import com.trs.types.Channel;
import com.trs.types.FirstClassMenu;
import com.trs.types.SubscribeItem;
import com.trs.util.AsyncTask;
import com.trs.wcm.RemoteDataService;
import com.trs.wcm.callback.BaseDataAsynCallback;
import org.json.JSONException;

/**
 * Created by john on 14-3-6.
 */
public class SplashActivity extends TRSFragmentActivity {
    private final short SPLASH_SHOW_SECONDS = 2;
    private Context mContext = this;
	private long mShowMainTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mShowMainTime = System.currentTimeMillis() + SPLASH_SHOW_SECONDS * 1000;
//        FrontiaAPI.getInstance(getApplicationContext());

        initData();
	}

    protected void initData(){
        RemoteDataService service = new RemoteDataService(mContext);
        service.loadJSON(TRSApplication.app().getFirstClassUrl(), new BaseDataAsynCallback() {
            @Override
            public void onDataLoad(String result) {
                boolean isCorrectData;
                if(mContext == null){
                    return;
                }
                if(result != null && result.length() != 0){
                    isCorrectData = true;
					try {
						TRSApplication.app().setFirstClassMenu(createFirstClassMenu(result));
					} catch (JSONException e) {
						isCorrectData = false;
						e.printStackTrace();
					}
				} else{
                    isCorrectData = false;
                }
                showView(isCorrectData);
            }
            @Override
            public void onError(String url) {
                super.onError(url);
                // TODO splash error.
            }
        });
    }

	public FirstClassMenu createFirstClassMenu(String data) throws JSONException {
		return FirstClassMenu.create(data);
	}

    protected void showView(final boolean isCorrectData){
		AsyncTask showMainTask = new AsyncTask() {
			@Override
			protected Object doInBackground(Object[] params) {
				if(System.currentTimeMillis() < mShowMainTime){
					try {
						long sleepTime = mShowMainTime - System.currentTimeMillis();
						if(sleepTime > 0){
							Thread.sleep(mShowMainTime - System.currentTimeMillis());
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				return null;
			}

			@Override
			protected void onPostExecute(Object o) {
				showMain();
				finish();
			}
		};

		showMainTask.execute();
	}

	protected void showMain(){
		ViewDisplayer.showMainActivity(this);
	}
}
