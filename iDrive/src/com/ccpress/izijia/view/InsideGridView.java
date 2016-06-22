package com.ccpress.izijia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by WLH on 2015/5/13 11:09.
 * 嵌套在scrollview里面的gridView需要重新计算高度
 */
public class InsideGridView extends GridView{
    public InsideGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InsideGridView(Context context) {
        super(context);
    }

    public InsideGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
