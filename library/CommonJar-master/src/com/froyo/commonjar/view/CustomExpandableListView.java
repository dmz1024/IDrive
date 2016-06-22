package com.froyo.commonjar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * 
 * @Des: 防止在ScrollervVIew中使用ExpandListView item不能展现的问题
 * @author Rhino 
 * @version V1.0 
 * @created  2015年5月13日 下午1:52:44
 */
public class CustomExpandableListView extends ExpandableListView {
	public CustomExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
		MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
