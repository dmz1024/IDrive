package com.ccpress.izijia.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Wu Jingyu
 * Date: 2015/9/18
 * Time: 11:05
 */
public class iDrvieViewPager extends ViewPager{
    private boolean scrollable = false;

    public iDrvieViewPager(Context context) {
        super(context);
    }

    public iDrvieViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (scrollable) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (scrollable) {
            return super.onInterceptTouchEvent(arg0);
        } else {
            return false;
        }

    }

    public boolean isScrollble() {
        return scrollable;
    }

    public void setScrollble(boolean scrollble) {
        this.scrollable = scrollble;
    }
}
