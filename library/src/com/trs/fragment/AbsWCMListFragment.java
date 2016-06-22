package com.trs.fragment;

import android.widget.Toast;
import com.trs.adapter.AbsListAdapter;
import com.trs.app.AdapterFactory;
import com.trs.wcm.LoadWCMJsonTask;

/**
 * Created by john on 14-2-24.
 */
abstract public class AbsWCMListFragment extends AbsListFragment {

	private LoadWCMJsonTask createLoadWcmJsonTask(){
		LoadWCMJsonTask task = new LoadWCMJsonTask(getActivity()) {
			@Override
			public void onDataReceived(String result, boolean isCache) throws Exception {
				if(getActivity() == null){
					return;
				}

				AbsWCMListFragment.this.onDataReceived(result, isCache);
			}

			@Override
			public void onError(Throwable t) {
                t.printStackTrace();
				if(getActivity() != null){
					Toast.makeText(getActivity(), "载入数据失败", Toast.LENGTH_LONG).show();
				}

				AbsWCMListFragment.this.onDataError(t);
			}

			@Override
			protected void onStart() {
				super.onStart();

				if(getActivity() != null){
					AbsWCMListFragment.this.onLoadStart();
				}
			}

			@Override
			protected void onEnd() {
				super.onEnd();

				if(getActivity() != null){
					AbsWCMListFragment.this.onLoadEnd();
				}
			}

		};

		return task;
	}

	@Override
	protected void loadData(String url) {
		LoadWCMJsonTask task = createLoadWcmJsonTask();
		task.start(url);
	}

    @Override
	protected void loadDataRemote(String url) {
		LoadWCMJsonTask task = createLoadWcmJsonTask();
		if(needCacheResult()){
			task.startRemote(url);
		}
		else{
			task.startAlwaysRemote(url);
		}
	}

	@Override
	protected AbsListAdapter createAdapter() {
		//TODO get type
		return AdapterFactory.getInstance().createAdapter(getActivity(), null);
	}
}
