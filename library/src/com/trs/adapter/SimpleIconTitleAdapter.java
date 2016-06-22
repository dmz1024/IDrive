package com.trs.adapter;

import android.content.Context;
import com.trs.mobile.R;

public class SimpleIconTitleAdapter extends IconTitleDateSummaryAdapter{

	public SimpleIconTitleAdapter(Context context) {
		super(context);
	}
	
	@Override
	public int getViewID() {
		return  R.layout.list_item_icon_title;
	}
}
