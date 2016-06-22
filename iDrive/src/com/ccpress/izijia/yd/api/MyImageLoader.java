package com.ccpress.izijia.yd.api;

import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.ccpress.izijia.R;
import com.ccpress.izijia.iDriveApplication;


/**
 * Created by dengmingzhi on 16/5/5.
 */
public class MyImageLoader {
    private ImageLoader.ImageListener listener;
    private ImageLoader imageLoader;

    public MyImageLoader(int size) {
        imageLoader = new ImageLoader(iDriveApplication.getmQueue(), new BitmapCache(size));
    }

    /**
     * 加载原图
     * @param iv
     * @param url
     */
    public void get(ImageView iv, String url) {
        listener = ImageLoader.getImageListener(iv, R.drawable.yd_icon_default, R.drawable.yd_icon_default);
        imageLoader.get(url, listener);
    }

    /**
     * 加载指定值范围内的最大图片
     *
     * @param iv
     * @param url
     * @param maxW
     * @param maxH
     */
    public void get(ImageView iv, String url, int maxW, int maxH) {
        listener = ImageLoader.getImageListener(iv, R.drawable.yd_icon_default, R.drawable.yd_icon_default);
        imageLoader.get(url, listener, maxW, maxH);
    }

    /*
     * @param iv
     * @param url
     */
    public void get1(ImageView iv, String url, int defaultRid, int faile) {
        listener = ImageLoader.getImageListener(iv, defaultRid,faile);
        imageLoader.get(url, listener);
    }

}
