package com.trs.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.trs.mobile.R;

/**
 * Created by john on 14-3-19.
 */
public class GridIconAdapter extends IconTitleDateSummaryAdapter {
	public GridIconAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}

	@Override
	public int getViewID() {
		return R.layout.list_item_grid;
	}
}
