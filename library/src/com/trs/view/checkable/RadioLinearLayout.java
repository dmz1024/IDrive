package com.trs.view.checkable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import com.trs.mobile.R;

/**
 * Created by john on 14-6-13.
 */
public class RadioLinearLayout extends CheckableLinearLayout {
	public RadioLinearLayout(Context context) {
		super(context);
	}

	public RadioLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void toggle() {
		if (!isChecked()) {
			super.toggle();
		}
	}
}
