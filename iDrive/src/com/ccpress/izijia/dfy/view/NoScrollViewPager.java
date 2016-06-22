package com.ccpress.izijia.dfy.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by administror on 2016/3/21 0021.
 * 重写ViewPager：禁止滑动
 */
public class NoScrollViewPager extends ViewPager {

    private boolean isCanScroll=false;

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        //当setCanScroll传入false时,此时viewPager不可以左右滑动切换界面
        setCanScroll(true);
        super.setCurrentItem(item, smoothScroll);
        setCanScroll(false);
    }


    @Override
    public void scrollTo(int x, int y) {
        if (!isCanScroll){
            return;
        }
        super.scrollTo(x,y);
    }


    public boolean isCanScroll() {
        return isCanScroll;
    }

    public void setCanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }


}
