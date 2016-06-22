package com.ccpress.izijia.componet;

import com.froyo.commonjar.activity.BaseActivity;
import com.ccpress.izijia.R;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 底部导航栏
 * 
 * @author wangyi
 * 
 */
public class BottomBar {

	public BaseActivity activity;

	public TextView tv_1;

	public TextView tv_2;

	public TextView tv_3;

	public TextView tv_4;
	
	public LinearLayout ll_bottom;

	public BottomBar(BaseActivity activity) {
		this.activity = activity;
		tv_1 = (TextView) activity.findViewById(R.id.tv_1);
		tv_2 = (TextView) activity.findViewById(R.id.tv_2);
		tv_3 = (TextView) activity.findViewById(R.id.tv_3);
		tv_4 = (TextView) activity.findViewById(R.id.tv_4);
		ll_bottom=(LinearLayout) activity.findViewById(R.id.ll_bottom);
	}

	public void showTv1(String text, int resId, OnClickListener listener) {
		tv_1.setVisibility(View.VISIBLE);
		tv_1.setText(text);
		tv_1.setOnClickListener(listener);
		tv_1.setCompoundDrawablesWithIntrinsicBounds(null,
				activity.getDrawableRes(resId), null, null);
	}

	public void showTv2(String text, int resId, OnClickListener listener) {
		tv_2.setVisibility(View.VISIBLE);
		tv_2.setText(text);
		tv_2.setOnClickListener(listener);
		tv_2.setCompoundDrawablesWithIntrinsicBounds(null,
				activity.getDrawableRes(resId), null, null);
	}

	public void showTv3(String text, int resId, OnClickListener listener) {
		tv_3.setVisibility(View.VISIBLE);
		tv_3.setText(text);
		tv_3.setOnClickListener(listener);
		tv_3.setCompoundDrawablesWithIntrinsicBounds(null,
				activity.getDrawableRes(resId), null, null);
	}

	public void showTv4(String text, int resId, OnClickListener listener) {
		tv_4.setVisibility(View.VISIBLE);
		tv_4.setText(text);
		tv_4.setOnClickListener(listener);
		tv_4.setCompoundDrawablesWithIntrinsicBounds(null,
				activity.getDrawableRes(resId), null, null);
	}
}
