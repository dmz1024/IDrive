package com.ccpress.izijia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by WLH on 2015/6/15 15:11.
 */
public class InsideExpandableListView extends ExpandableListView {
    public InsideExpandableListView(Context context) {
        super(context);
    }

    public InsideExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InsideExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
