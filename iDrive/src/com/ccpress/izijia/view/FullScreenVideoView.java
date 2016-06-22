package com.ccpress.izijia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by WLH on 2015/10/13 16:38.
 */
public class FullScreenVideoView extends VideoView{
    public FullScreenVideoView(Context context) {
        super(context);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
//    {
//        int width = getDefaultSize(getHolder().getSurfaceFrame().width(), widthMeasureSpec);
//        int height = getDefaultSize(getHolder().getSurfaceFrame().height(), heightMeasureSpec);
//        setMeasuredDimension(width , height);
//    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
