package com.ccpress.izijia.yd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by dengmingzhi on 16/5/13.
 */
public class CountTextView extends TextView {
    OnCountChangeListener onCountChangeListener;

    public CountTextView(Context context) {
        super(context);
    }

    public CountTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CountTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (onCountChangeListener == null) {
            return super.onTouchEvent(event);
        }
        int width = getWidth();
        int downX = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int currentCount = Integer.parseInt(getText().toString());
                if (downX <= width / 3) {
                    if(onCountChangeListener.jian()){
                        int count = currentCount - 1;
                        if (count < 0) {
                            count = 0;
                        }
                        setText(count + "");
                    }
                    return true;
                } else if (downX >= (width / 3) * 2) {
                    if(onCountChangeListener.jia()){
                        int count = currentCount + 1;
                        setText(count + "");
                    }
                    return true;
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    public interface OnCountChangeListener {
        boolean jia();
        boolean jian();
    }

    public void setOnCountChangeListener(OnCountChangeListener onCountChangeListener) {
        this.onCountChangeListener = onCountChangeListener;
    }

    public int getCount(){
        return Integer.parseInt(getText().toString());
    }


}
