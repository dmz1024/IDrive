package com.ccpress.izijia.dfy.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.entity.Ad;
import com.ccpress.izijia.dfy.util.Util;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by administror on 2016/3/18 0018.
 */
public abstract class CarouselAdapter extends PagerAdapter {
    private List<Ad> adList;
    private ImageOptions options;

    public CarouselAdapter() {
        options = new ImageOptions.Builder()
                //设置加载过程中的图片
                .setLoadingDrawableId(R.drawable.dfy_index_banner_01)
                        //设置加载失败后的图片
                .setFailureDrawableId(R.drawable.dfy_index_banner_01)
                        //设置使用缓存
                .setUseMemCache(true)
                        //设置显示圆形图片
//                .setCircular(true)
                        //设置支持gif
//                .setIgnoreGif(false)
//                .setSize(displayWidth, (int) (displayWidth / 2.8))
                .build();
    }

    private Map<Integer, ImageView> map = new HashMap<Integer, ImageView>();


    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    public void setAdList(List<Ad> adList) {
        this.adList = adList;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == (View) o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView iv = new ImageView(Util.getMyApplication());
        iv.setBackgroundResource(R.drawable.dfy_index_banner_01);
        if (adList != null) {
            if (adList.size() > 0) {
                x.image().bind(iv, adList.get(position % adList.size()).getAd_code(), options);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adOnClick(adList.get(position % adList.size()).getAd_link());
                        adOnClick(adList.get(position % adList.size()));
                    }
                });
            }
        }
        map.put(position, iv);
        container.addView(iv);
        return iv;
    }

    public void adOnClick(String id) {
    }

    ;//广告位点击事件

    public void adOnClick(Ad ad) {
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(map.get(position));
        map.remove(position);
    }


}
