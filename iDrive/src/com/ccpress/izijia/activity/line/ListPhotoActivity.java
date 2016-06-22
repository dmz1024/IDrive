package com.ccpress.izijia.activity.line;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.R;
import com.froyo.commonjar.network.BitmapCache;


import java.util.List;


/**
 * Created by Hexulan
 * on 2016/3/14.
 * 游记照片的大图展示
 */
public class ListPhotoActivity extends Activity implements OnPageChangeListener{
    /**
     * ViewPager
     */
    private ViewPager viewPager;

    /**
     * 装点点的ImageView数组
     */
    private ImageView[] tips;

    public  static List<String> list;

    private ImageView[] mImageViews;

    public RequestQueue mQueue;
    public ImageLoader imageLoader;

    /**
     * 图片资源id
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_list_phonto);

        ViewGroup group = (ViewGroup)findViewById(R.id.viewGroup);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        mQueue = Volley.newRequestQueue(getBaseContext());
        imageLoader = new ImageLoader(mQueue, BitmapCache.getInstance());


        //将点点加入到ViewGroup中
        tips = new ImageView[list.size()];
        for(int i=0; i<tips.length; i++){
            ImageView imageView = new ImageView(ListPhotoActivity.this);
            imageView.setLayoutParams(new LayoutParams(10,10));
            tips[i] = imageView;
            if(i == 0){
                tips[i].setBackgroundResource(R.drawable.background_tab);
            }else{
                tips[i].setBackgroundResource(R.color.idrive_white);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            group.addView(imageView, layoutParams);
        }



        //将图片装载到数组中

        mImageViews = new ImageView[list.size()];
        for(int i=0; i<mImageViews.length; i++){
            NetworkImageView child=new NetworkImageView(ListPhotoActivity.this);
            child.setScaleType(ImageView.ScaleType.FIT_XY);
            child.setImageUrl(list.get(i), imageLoader);
            mImageViews[i] = child;
        }

        //设置Adapter
        viewPager.setAdapter(new MyAdapter());
        //设置监听，主要是设置点点的背景
        viewPager.setOnPageChangeListener(this);
        //设置ViewPager的默认项
        viewPager.setCurrentItem((0));

    }

    /**
     *
     * @author xiaanming
     *
     */
    public class MyAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mImageViews.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager)container).removeView(mImageViews[position]);

        }

        /**
         * 载入图片进去
         */
        @Override
        public Object instantiateItem(View container, int position) {

            ((ViewPager)container).addView(mImageViews[position], 0);

            return mImageViews[position];
        }

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        setImageBackground(arg0);
    }

    /**
     * 设置选中的tip的背景
     * @param selectItems
     */
    private void setImageBackground(int selectItems){
        for(int i=0; i<tips.length; i++){
            if(i == selectItems){
                tips[i].setBackgroundResource(R.drawable.background_tab);
            }else{
                tips[i].setBackgroundResource(R.color.blue);
            }
        }
    }

}
