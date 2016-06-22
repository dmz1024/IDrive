package com.ccpress.izijia.yd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by dengmingzhi on 16/5/19.
 */
public class MaxListView extends ListView {
    public MaxListView(Context context) {
        super(context);
    }

    public MaxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
