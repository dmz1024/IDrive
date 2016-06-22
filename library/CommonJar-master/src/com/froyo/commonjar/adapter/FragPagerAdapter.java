package com.froyo.commonjar.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragPagerAdapter extends FragmentPagerAdapter {

	private List<FragmentVo> list;

	public FragPagerAdapter(FragmentManager fm, List<FragmentVo> list) {
		super(fm);
		this.list = list;
	}

	@Override
	public Fragment getItem(int postion) {
		return list.get(postion).getFragment();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return list.get(position).getTitle();
	}

	public void addFragment(List<FragmentVo> fragList) {
		list.addAll(fragList);
		notifyDataSetChanged();
	}

	public void addFragment(FragmentVo frag) {
		list.add(frag);
		notifyDataSetChanged();
	}
}
