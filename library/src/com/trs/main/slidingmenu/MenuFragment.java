package com.trs.main.slidingmenu;

import android.support.v4.app.Fragment;

/**
 * Created by john on 14-5-6.
 */
public class MenuFragment extends Fragment {
	public static interface DisplayFragmentListener {
		public void displayFragment(String title, Fragment fragment);
	}

	private DisplayFragmentListener mDisplayFragmentListener;

	public DisplayFragmentListener getDisplayFragmentListener() {
		return mDisplayFragmentListener;
	}

	public void setDisplayFragmentListener(DisplayFragmentListener displayFragmentListener) {
		this.mDisplayFragmentListener = displayFragmentListener;
	}
}
