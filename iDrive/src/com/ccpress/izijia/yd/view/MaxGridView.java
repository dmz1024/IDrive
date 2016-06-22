package com.ccpress.izijia.yd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by dengmingzhi on 16/5/8.
 */
public class MaxGridView extends GridView {
    public MaxGridView(Context context) {
        super(context);
    }

    public MaxGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
