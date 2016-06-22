package com.trs.view.checkable;

import android.widget.Checkable;

/**
 * Created by john on 14-6-13.
 */
public interface CheckableViewGroup extends Checkable {
	/**
	 * Interface definition for a callback to be invoked when the checked state
	 * of a compound button changed.
	 */
	public interface OnCheckedChangeListener {
		/**
		 * Called when the checked state of a compound button has changed.
		 *
		 * @param view The compound button view whose state has changed.
		 * @param isChecked  The new checked state of buttonView.
		 */
		void onCheckedChanged(Checkable view, boolean isChecked);
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener listener);

	public void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener);
}
