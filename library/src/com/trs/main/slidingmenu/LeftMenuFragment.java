package com.trs.main.slidingmenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.trs.app.FragmentFactory;
import com.trs.types.Channel;

/**
 * Created by john on 14-3-11.
 */
public class LeftMenuFragment extends MenuFragment implements LeftMenu.OnMenuCheckedListener{
	private LeftMenu mMenu;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mMenu = new LeftMenu(getActivity());
		mMenu.setOnMenuCheckedListener(this);

		mMenu.post(new Runnable() {
			@Override
			public void run() {
				select(0);
			}
		});

		return mMenu;
	}

	public void select(int index){
		mMenu.check(index);
	}

	public void select(Channel channel){
		mMenu.check(channel);
	}

	@Override
	public void onMenuChecked(View menu) {
		Channel channel = (Channel) menu.getTag();
		if(getDisplayFragmentListener() != null){
			getDisplayFragmentListener().displayFragment(channel.getTitle(),
					FragmentFactory.createFragment(getActivity(), channel));
		}
	}
}
