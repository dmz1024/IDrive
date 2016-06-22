package com.trs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by john on 13-12-16.
 */
public class ObsverableHorizontalScrollView extends HorizontalScrollView {
	public interface ScrollViewListener {
		void onScrollChanged(ObsverableHorizontalScrollView scrollView, int x, int y, int oldx, int oldy);
	}

	private ScrollViewListener scrollViewListener = null;

	public ObsverableHorizontalScrollView(Context context) {
		super(context);
	}

	public ObsverableHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ObsverableHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setScrollViewListener(ScrollViewListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		if(scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
		}
	}
}
