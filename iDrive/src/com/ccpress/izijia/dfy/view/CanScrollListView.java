package com.ccpress.izijia.dfy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by dmz1024 on 2016/3/18.
 */
public class CanScrollListView extends ListView {
    public CanScrollListView(Context context) {
        super(context);
    }

    public CanScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public CanScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
