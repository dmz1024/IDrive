package com.trs.main.slidingmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.trs.app.TRSApplication;
import com.trs.mobile.R;
import com.trs.types.Channel;
import com.trs.types.FirstClassMenu;
import com.trs.util.ImageDownloader;
import com.trs.util.Util;
import com.trs.view.checkable.CheckableLinearLayout;
import com.trs.view.checkable.CheckableViewGroup;

/**
 * Created by john on 14-2-19.
 */
public class LeftMenu extends LinearLayout {
	private OnMenuCheckedListener mOnMenuCheckedListener;
	private LinearLayout mItemContainer;
	private FirstClassMenu mMenu;

	public LeftMenu(Context context) {
		super(context);
		initialize();
	}

	private void initialize(){
		mMenu = TRSApplication.app().getFirstClassMenu();

		final LayoutInflater inflator = LayoutInflater.from(getContext());

		setOrientation(VERTICAL);
		setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

		removeAllViews();

		View titleView;

		titleView = inflator.inflate(R.layout.left_menu_title, this, false);

		addView(titleView);

		ScrollView scroll = new ScrollView(getContext());
		scroll.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
		scroll.setVerticalScrollBarEnabled(false);
		scroll.setBackgroundColor(0xffeeeeee);
		addView(scroll, new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

		mItemContainer = new LinearLayout(getContext());
		mItemContainer.setOrientation(VERTICAL);

		scroll.addView(mItemContainer, new ScrollView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

		for(Channel channel: mMenu.getChannelList()){
			mItemContainer.addView(createView(inflator, channel));
		}
	}

	private View createView(LayoutInflater inflator, Channel channel){
		CheckableLinearLayout menuItem;
		menuItem= (CheckableLinearLayout)inflator.inflate(R.layout.left_menu_item, mItemContainer, false);

		ImageView img = (ImageView) menuItem.findViewById(R.id.img);
		TextView text = (TextView) menuItem.findViewById(R.id.text);

		menuItem.setChecked(false);
		new ImageDownloader.Builder().setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).build(channel.getPic(), img).start();
		text.setText(channel.getTitle());

		menuItem.setTag(channel);
		menuItem.setOnCheckedChangeListener(new CheckableViewGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(Checkable view, boolean isChecked) {

				if(isChecked){
					for(int i = 0; i < mItemContainer.getChildCount(); i ++){
						View child = mItemContainer.getChildAt(i);
						if(child instanceof Checkable){
							if(child != view){
								Checkable checkable = (Checkable) child;
								checkable.setChecked(false);
							}
						}
					}

					onMenuChecked((View)view);
				}
			}
		});

		return menuItem;
	}

	private void onMenuChecked(View menuView){
		if(mOnMenuCheckedListener != null){
			mOnMenuCheckedListener.onMenuChecked(menuView);
		}
	}

	public void setOnMenuCheckedListener(OnMenuCheckedListener listener){
		this.mOnMenuCheckedListener = listener;
	}

	public interface OnMenuCheckedListener{
		public void onMenuChecked(View menu);
	}

	public void check(int index){
		int currentIndex = 0;
		for(int i = 0; i < mItemContainer.getChildCount(); i ++){
			View child = mItemContainer.getChildAt(i);
			if(child instanceof Checkable){
				Checkable checkable = (Checkable) child;
				if(currentIndex == index){
					if(!checkable.isChecked()) {
						checkable.setChecked(true);
					}
				}
				else{
					checkable.setChecked(false);
				}
				currentIndex ++;
			}
		}
	}

    public void unCheck(int index){
        View child = mItemContainer.getChildAt(index);
        Checkable checkable = (Checkable) child;
        checkable.setChecked(false);
    }

	public void check(Channel channel){
		for(int i = 0; i < mItemContainer.getChildCount(); i ++){
			if(Util.equals(channel, mItemContainer.getChildAt(i).getTag())){
				check(i);
				break;
			} else{
                unCheck(i);
            }
		}
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		if(child instanceof Checkable){
			super.addView(child, index, params);
		}
		else{
			throw new IllegalArgumentException("Checkable child is required");
		}
	}
}
