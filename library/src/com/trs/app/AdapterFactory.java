package com.trs.app;

import android.content.Context;
import com.trs.adapter.AbsListAdapter;
import com.trs.adapter.IconTitleDateSummaryAdapter;

/**
 * Created by john on 14-3-13.
 */
public class AdapterFactory {
	public static AdapterFactory sInstance;

	public static AdapterFactory getInstance(){
		if(sInstance == null){
			sInstance = new AdapterFactory();
		}

		return sInstance;
	}

	private AdapterFactory() {

	}

	public AbsListAdapter createAdapter(Context context, String type){
		//TODO create adapter according type
		return new IconTitleDateSummaryAdapter(context);
	}
}

