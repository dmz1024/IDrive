package com.trs.fragment;

import android.os.Bundle;

/**
 * Created by john on 14-3-11.
 */
abstract public class AbsUrlFragment extends AbsTRSFragment{
	public static String EXTRA_URL = "url";

	private String mUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getArguments() != null){
			mUrl = getArguments().getString(EXTRA_URL);
		}
	}

	public String getUrl(){
		return mUrl;
	}

	public void setUrl(String url){
		this.mUrl = url;
	}


}
