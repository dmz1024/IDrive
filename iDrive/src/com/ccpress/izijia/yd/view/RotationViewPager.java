package com.ccpress.izijia.yd.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by dengmingzhi on 16/5/11.
 * 轮播ViewPager
 */
public class RotationViewPager extends ViewPager {
    private List<View> views;
    private boolean isDown=false;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    rotation();
                    break;
            }
        }
    };

    private void rotation() {
        if(!isDown){
            int current=getCurrentItem();
            if(current==views.size()-1){
                current=0;
            }else {
                current+=1;
            }
            setCurrentItem(current);
        }
        handler.sendEmptyMessageDelayed(1,2500);
    }

    public RotationViewPager(Context context) {
        super(context);
    }

    public RotationViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置轮播视图
     *
     * @param views
     */
    public void setImages(List<View> views) {
        this.views = views;
        if(views!=null &&views.size()>0){
            setAdapter(new MyAdapter());
            handler.sendEmptyMessageDelayed(1,2000);
        }

    }


    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position),0);
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                isDown=true;
                break;
            case MotionEvent.ACTION_UP:
                isDown=false;
                break;
        }
        return super.onTouchEvent(ev);
    }
}
