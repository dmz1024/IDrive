package com.trs.main.slidingmenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.trs.fragment.AbsTRSFragment;
import com.trs.mobile.R;

/**
 * Created by john on 14-3-11.
 */
public class RightMenuFragment extends AbsTRSFragment{

	private SettingFragment mSettingFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.right_menu, null);

		mSettingFragment = new SettingFragment();

		getFragmentManager().beginTransaction()
				.replace(R.id.setting_container, mSettingFragment)
				.commit();

		view.setFocusable(true);
		view.setClickable(true);
		return view;
	}
}
