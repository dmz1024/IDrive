package com.trs.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.trs.mobile.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by john on 14-2-25.
 */
public class TopBar extends RelativeLayout {
	private static final int[] ATTRS = {
		android.R.attr.text,
	};

	private ImageButton mLeftButton;
	private ImageButton mRightButton;
	private TextView mTitle;
	private int leftIconId;
	private int rightIconId;
    private int weatherIconId;
	private float titleTextSize;
	private int titleTextColor;
	private String leftIconOnclickAction;
	private String rightIconOnclickAction;
    private ImageButton mWeatherBtn;

	public TopBar(Context context) {
		super(context);
		initializeView();
	}

	public TopBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeView();

		DisplayMetrics dm = getResources().getDisplayMetrics();
		int defaultTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, dm);

		TypedArray a;

		a = context.obtainStyledAttributes(attrs, R.styleable.TopBar);

		leftIconId = a.getResourceId(R.styleable.TopBar_leftIcon, 0);
		rightIconId = a.getResourceId(R.styleable.TopBar_rightIcon, 0);
		titleTextSize = a.getDimensionPixelSize(R.styleable.TopBar_titleTextSize, defaultTextSize);
		titleTextColor = a.getColor(R.styleable.TopBar_titleTextColor, android.R.color.primary_text_dark);
		leftIconOnclickAction = a.getString(R.styleable.TopBar_leftIconOnClickAction);
		rightIconOnclickAction = a.getString(R.styleable.TopBar_rightIconOnClickAction);

        weatherIconId = a.getResourceId(R.styleable.TopBar_weatherIcon,0);
		a.recycle();
		initializeView();
	}

	private void setOnClickAction(View view, String action){
		final String handlerName = action;
		view.setOnClickListener(new OnClickListener() {
			private Method mHandler;

			public void onClick(View v) {
				if (mHandler == null) {
					try {
						mHandler = getContext().getClass().getMethod(handlerName,
								View.class);
					} catch (NoSuchMethodException e) {
						int id = getId();
						String idText = id == NO_ID ? "" : " with id '"
								+ getContext().getResources().getResourceEntryName(
								id) + "'";
						throw new IllegalStateException("Could not find a method " +
								handlerName + "(View) in the activity "
								+ getContext().getClass() + " for onClick handler"
								+ " on view " + TopBar.this.getClass() + idText, e);
					}
				}

				try {
					mHandler.invoke(getContext(), TopBar.this);
				} catch (IllegalAccessException e) {
					throw new IllegalStateException("Could not execute non "
							+ "public method of the activity", e);
				} catch (InvocationTargetException e) {
					throw new IllegalStateException("Could not execute "
							+ "method of the activity", e);
				}
			}
		});
	}

	private void initializeView(){
		mLeftButton = new ImageButton(getContext());
		mLeftButton.setId(R.id.topbar_left_icon);
		mLeftButton.setBackgroundResource(0);
		LayoutParams leftMenuParams = new LayoutParams((int) getResources().getDimension(R.dimen.top_bar_height),
				(int) getResources().getDimension(R.dimen.top_bar_height));
		addView(mLeftButton, leftMenuParams);

		mRightButton = new ImageButton(getContext());
		mRightButton.setId(R.id.topbar_right_icon);
		mRightButton.setBackgroundResource(0);
		LayoutParams rightMenuParams = new LayoutParams((int) getResources().getDimension(R.dimen.top_bar_height),
				(int) getResources().getDimension(R.dimen.top_bar_height));
		rightMenuParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		addView(mRightButton, rightMenuParams);

        mWeatherBtn = new ImageButton(getContext());
        mWeatherBtn.setId(R.id.topbar_weather_icon);
        mWeatherBtn.setBackgroundResource(0);

		mTitle = new TextView(getContext());
		mTitle.setId(R.id.topbar_title);
		mTitle.setGravity(Gravity.CENTER);
		mTitle.setSingleLine();

		LayoutParams titleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		titleParams.addRule(CENTER_IN_PARENT);
		addView(mTitle, titleParams);

		mLeftButton.setImageResource(leftIconId);
		mRightButton.setImageResource(rightIconId);
		mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
		mTitle.setTextColor(titleTextColor);

		if(leftIconOnclickAction != null){
			setOnClickAction(mLeftButton, leftIconOnclickAction);
		}
		if(rightIconOnclickAction != null){
			setOnClickAction(mRightButton, rightIconOnclickAction);
		}

	}

	public void setLeftIcon(int resId){
		mLeftButton.setImageResource(resId);
	}

	public void setLeftIcon(Bitmap bmp){
		mLeftButton.setImageBitmap(bmp);
	}

	public void setLeftIcon(Drawable d){
		mLeftButton.setImageDrawable(d);
	}

	public void setOnLeftIconClickListener(OnClickListener listener){
		mLeftButton.setOnClickListener(listener);
	}

	public void setRightIcon(int resId){
		mRightButton.setImageResource(resId);
	}

	public void setRightIcon(Bitmap bmp){
		mRightButton.setImageBitmap(bmp);
	}

	public void setRightIcon(Drawable d){
		mRightButton.setImageDrawable(d);
	}

	public void setOnRightIconClickListener(OnClickListener listener){
		mRightButton.setOnClickListener(listener);
	}

	public void setTitleTextSize(float size){
		mTitle.setTextSize(size);
	}

	public void setTitleTextSizeRes(int resId){
		mTitle.setTextSize(getResources().getDimension(resId));
	}

	public void setTitleTextColor(int color){
		mTitle.setTextColor(color);
	}

	public void setTitleTextColor(ColorStateList color){
		mTitle.setTextColor(color);
	}

	public void setTitleText(CharSequence text){
		mTitle.setText(text);
	}

	public void setTitleText(int textResId){
		mTitle.setText(textResId);
	}
}
