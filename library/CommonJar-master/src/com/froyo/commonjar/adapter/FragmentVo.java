package com.froyo.commonjar.adapter;

import com.froyo.commonjar.fragment.BaseFragment;

public class FragmentVo {
	
	private BaseFragment fragment;

	private String title;

	public BaseFragment getFragment() {
		return fragment;
	}

	public void setFragment(BaseFragment fragment) {
		this.fragment = fragment;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
