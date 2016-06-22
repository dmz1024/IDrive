package com.ccpress.izijia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by WLH on 2015/5/22 15:29.
 * 嵌套在scrollview里面的listView需要重新计算高度
 */
public class InsideListView extends ListView {


    public InsideListView(Context context) {
        super(context);
    }

    public InsideListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InsideListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,

                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);

    }

}
