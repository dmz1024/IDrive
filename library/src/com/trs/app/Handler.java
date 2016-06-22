package com.trs.app;

import android.content.Context;
import android.os.Looper;

import java.lang.ref.WeakReference;

/**
 * Created by john on 14-4-2.
 */
public class Handler extends android.os.Handler {
	private WeakReference<Context> mContext;

	public Handler(Context context) {
		this.mContext = new WeakReference<Context>(context);
	}

	public Context getContext(){
		return mContext.get();
	}
}
