package com.froyo.commonjar.task;

import android.os.AsyncTask;

import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.utils.Utils;

public abstract class BaseTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {

	protected BaseActivity activity;

	private boolean doAfterExecute = true;

	private Throwable e;

	public BaseTask(BaseActivity activity) {
		this.activity = activity;
	}

	@Override
	protected Result doInBackground(Params... params) {
		try {
			if (!Utils.isEmpty(params)) {
				return doExecute(params[0]);
			} else {
				return doExecute(null);
			}
		} catch (Exception e) {
			this.e = e;
			cancel(false);
			activity.dismissDialog();
			return null;
		}
	}

	@Override
	protected void onPostExecute(Result result) {
		// 需要执行后续操作
		if (doAfterExecute) {
			try {
				doResult(result);
			} catch (Exception e) {
				e.printStackTrace();
				doException(e);
			}
		}
	}

	@Override
	protected void onCancelled() {
		doAfterExecute = false;
		if (e != null) {
			doException(e);
		}
	}

	public abstract Result doExecute(Params param) throws Exception;

	public abstract void doResult(Result result) throws Exception;

	protected void doException(Throwable e) {
	}

}