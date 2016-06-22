package com.froyo.commonjar.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.xutils.ViewUtils;

public abstract class BaseFragment extends Fragment {

	public View rootView;

	private int resId;

	public BaseActivity activity;

	protected abstract void setListener();

	protected abstract int setLayoutResID();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			activity = (BaseActivity) getActivity();
		} catch (ClassCastException ex) {
			throw new ClassCastException(getActivity().getClass()
					.getSimpleName() + " 不是BaseActivity");
		}
		resId = setLayoutResID();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = activity.makeView(resId);
			ViewUtils.inject(this, rootView);
			setListener();
			doExtra();
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	public void doExtra() {

	}
}
