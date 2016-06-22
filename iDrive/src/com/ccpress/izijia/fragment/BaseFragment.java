package com.ccpress.izijia.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.froyo.commonjar.xutils.ViewUtils;
import com.trs.app.TRSFragmentActivity;

/**
 *创建BaseFragment
 */
public abstract class BaseFragment extends Fragment {

	public View rootView;

	private int resId;

	public TRSFragmentActivity activity;

	protected abstract void setListener();

	protected abstract int setLayoutResID();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			activity = (TRSFragmentActivity) getActivity();
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
			rootView = makeView(resId);
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

	public View makeView(int resId) {
		LayoutInflater inflater = LayoutInflater.from(activity);
		return inflater.inflate(resId, null);
	}
}
