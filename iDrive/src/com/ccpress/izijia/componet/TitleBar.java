package com.ccpress.izijia.componet;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.froyo.commonjar.activity.BaseActivity;
import com.ccpress.izijia.R;

/**
 * 
 * @Des: 导航栏
 * @author Rhino
 * @version V1.0
 * @created 2015年2月4日 下午1:53:16
 */
public class TitleBar {

	private BaseActivity activity;

	private ImageView left;

	public TextView tv_title;

	public TextView tv_right;

	public ImageView iv_right;

	public TitleBar(BaseActivity activity) {
		this.activity = activity;
		left = (ImageView) activity.findViewById(R.id.iv_back);
		tv_title = (TextView) activity.findViewById(R.id.tv_title);
		tv_right = (TextView) activity.findViewById(R.id.tv_right);
		iv_right = (ImageView) activity.findViewById(R.id.iv_right);
	}

	public void setTitle(String text) {
		tv_title.setText(text);
	}

	public void showBack() {
		left.setVisibility(View.VISIBLE);
		left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				activity.finish();
			}
		});
	}

	public void showRightText(OnClickListener listener, String text) {
		tv_right.setVisibility(View.VISIBLE);
		tv_right.setText(text);
		tv_right.setOnClickListener(listener);
	}

	public void showRightImage(OnClickListener listener, int resId) {
		iv_right.setVisibility(View.VISIBLE);
		iv_right.setImageResource(resId);
		iv_right.setOnClickListener(listener);
	}
	
	public void hideTitle(){
		tv_title.setVisibility(View.GONE);
	}
	
	
}
