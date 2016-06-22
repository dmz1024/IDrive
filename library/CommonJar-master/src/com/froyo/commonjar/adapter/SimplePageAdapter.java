package com.froyo.commonjar.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.froyo.commonjar.activity.BaseActivity;

public class SimplePageAdapter extends PagerAdapter {

	private List<View> viewDatas;

	private BaseActivity act;

	public SimplePageAdapter(BaseActivity act, List<View> viewDatas) {
		this.viewDatas = viewDatas;
		this.act = act;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		container.addView(viewDatas.get(position), 0);
		viewDatas.get(position).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
			}
		});
		return viewDatas.get(position);
	}

	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewDatas.get(position));
	}

	@Override
	public int getCount() {
		return viewDatas.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	public void removeAll() {
		viewDatas.clear();
		notifyDataSetChanged();
	}

	public void addDatas(List<View> views) {
		viewDatas.addAll(views);
		notifyDataSetChanged();
	}

}
