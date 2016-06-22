package com.ccpress.izijia.componet;

import java.util.List;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimplePageAdapter;
import com.froyo.commonjar.utils.AppUtils;
import com.ccpress.izijia.R;

/**
 * 页面滑动空间ViewPager+Fragment
 * 注意：1:初始化控件的layou必须包含view_swip_layout,2:传入的views和names的个数相当
 * @author wangyi
 * 
 */
public abstract class SwipPager {

	private ViewPager viewPager;

	private LinearLayout tabContainer;

	private HorizontalScrollView hsTab;

	private int lastScrollX = 0;

	private int width = 0;

	public int currentIndex = 0;

	private BaseActivity activity;
	
	public SwipPager(BaseActivity activity,SimplePageAdapter adapter,List<String> names) {
		this.activity = activity;
		viewPager=(ViewPager) activity.findViewById(R.id.vp_travel);
		tabContainer=(LinearLayout) activity.findViewById(R.id.ll_tab_container);
		hsTab=(HorizontalScrollView) activity.findViewById(R.id.hs_tab);
		viewPager.setAdapter(adapter);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				changeBtnBg(position);
				currentIndex = position;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		addTab(names);
	}

	private void scrollToChild(int position, int offset) {

		int newScrollX = tabContainer.getChildAt(position).getLeft() + offset;

		if (position > 0 || offset > 0) {
			newScrollX -= width;
		}
		if (position != 0) {
			if (newScrollX != lastScrollX) {
				lastScrollX = newScrollX;
				hsTab.scrollTo(newScrollX, 0);
			}
		} else {
			lastScrollX = 0;
			hsTab.scrollTo(0, 0);
		}
	}

	public void addTab(final List<String> names) {

		if (names.size() < 5) {
			width = AppUtils.getWidth(activity) / names.size();
		} else {
			width = AppUtils.getWidth(activity) / 4;
		}
		for (int i = 0; i < names.size(); i++) {
			final String vo = names.get(i);
			View view = activity.makeView(R.layout.view_collect_tab);
			TextView text = (TextView) view.findViewById(R.id.tv_name);
			text.setText(vo);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,
					LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.CENTER_VERTICAL;
			view.setLayoutParams(lp);

			tabContainer.addView(view);
			final int tempP = i;
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					selectItem(tempP, names, arg0);
				}
			});
		}
		changeBtnBg(0);
	}
	
	public void selectItem(int position, List<String> datas, View view) {
		viewPager.setCurrentItem(position);
	}
	private void changeBtnBg(int position) {

		changeBtn(position);

		scrollToChild(position, (int) (0.3 * tabContainer.getChildAt(position)
				.getWidth()));
		for (int i = 0; i < tabContainer.getChildCount(); i++) {
			LinearLayout v = (LinearLayout) tabContainer.getChildAt(i);
			if (i == position) {
				v.getChildAt(1).setVisibility(View.VISIBLE);
				((TextView) v.getChildAt(0)).setTextColor(activity.getResources()
						.getColor(R.color.base_color));
			} else {
				v.getChildAt(1).setVisibility(View.INVISIBLE);
				((TextView) v.getChildAt(0)).setTextColor(Color
						.parseColor("#333333"));
			}
		}
	}
	
	/**
	 *滑动到指定页，做的事 
	 * @param position
	 */
	public abstract void changeBtn(int position);
	
}
