package com.trs.view.checkable;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import com.trs.util.log.Log;

import java.lang.reflect.Method;

/**
 * Created by john on 14-6-13.
 */
public class CommonRadioGroup extends LinearLayout {
	private OnCheckedChangeListener mOnCheckedChangeListener;
	private CompoundButton.OnCheckedChangeListener mOnCompondButtonCheckedChangedListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			onChildCheckedChanged(buttonView, isChecked);
		}
	};

	private CheckableViewGroup.OnCheckedChangeListener onCheckableViewGroupCheckedChangedListener = new CheckableViewGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(Checkable view, boolean isChecked) {
			onChildCheckedChanged((View)view, isChecked);
		}
	};

	public CommonRadioGroup(Context context) {
		super(context);

		setOrientation(VERTICAL);
	}

	public CommonRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);

		setOrientation(VERTICAL);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public CommonRadioGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setOrientation(VERTICAL);
	}

	private void onChildCheckedChanged(View view, boolean isChecked) {

		if(isChecked){
			updateChildCheckState(view, isChecked);

			if(mOnCheckedChangeListener != null){
				mOnCheckedChangeListener.onCheckedChanged(this, view.getId());
			}
		}
	}

	private void updateChildCheckState(View stateChangedView, boolean checked){
		if(checked){
			for(int i = 0; i < getChildCount(); i ++){
				View child = getChildAt(i);
				if(child != stateChangedView && child instanceof Checkable){
					((Checkable)child).setChecked(false);
				}
			}
		}
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		super.addView(child, index, params);

		if(child.getId() == View.NO_ID){
			child.setId(child.hashCode());
		}

		if(child instanceof CompoundButton){
			try {
				Method method = CompoundButton.class.getMethod("setOnCheckedChangeWidgetListener", CompoundButton.OnCheckedChangeListener.class);
				method.setAccessible(true);
				method.invoke(child, mOnCompondButtonCheckedChangedListener);
			} catch (Exception e) {
				((CompoundButton)child).setOnCheckedChangeListener(mOnCompondButtonCheckedChangedListener);
				Log.w("CommonRadioGroup", "Can not invoke method: " + "setOnCheckedChangeWidgetListener", e);
			}
		}
		else if(child instanceof CheckableViewGroup){
			((CheckableViewGroup)child).setOnCheckedChangeWidgetListener(onCheckableViewGroupCheckedChangedListener);
		}
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		this.mOnCheckedChangeListener = listener;
	}

	/**
	 * <p>Interface definition for a callback to be invoked when the checked
	 * radio button changed in this group.</p>
	 */
	public interface OnCheckedChangeListener {
		/**
		 * <p>Called when the checked radio button has changed. When the
		 * selection is cleared, checkedId is -1.</p>
		 *
		 * @param group the group in which the checked radio button has changed
		 * @param checkedId the unique identifier of the newly checked radio button
		 */
		public void onCheckedChanged(CommonRadioGroup group, int checkedId);
	}

	public void check(int id){
		for(int i = 0; i < getChildCount(); i ++){
			View child = getChildAt(i);
			if(child instanceof Checkable && child.getId() == id){
				((Checkable) child).setChecked(true);
			}
		}
	}

	public int getCheckedItemId(){
		int checkID = View.NO_ID;
		for(int i = 0; i < getChildCount(); i ++){
			View child = getChildAt(i);
			if(child instanceof Checkable && ((Checkable)child).isChecked()){
				checkID = child.getId();
			}
		}

		return checkID;
	}

	@Override
	public void removeView(View view) {
		super.removeView(view);
	}
}
