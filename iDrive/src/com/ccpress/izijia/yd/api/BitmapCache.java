package com.ccpress.izijia.yd.api;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;


/**
 * Created by dengmingzhi on 16/5/5.
 */
public class BitmapCache implements ImageLoader.ImageCache {

    private LruCache<String, Bitmap> mCache;

    public BitmapCache(int size) {
        int maxSize = size * 1024 * 1024;
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
//        if (bitmap != null && url != null) {
//            saveBitmap(bitmap, MD5.md5(url));
////            new DownFile(url.substring(6), "image", MD5.md5(url) + ".png").downLoadFile();
//            Log.d("url", url.substring(6) + "---" + MD5.md5(url.substring(6)) + ".png");
//        }

        mCache.put(url, bitmap);
    }

//    public void saveBitmap(Bitmap mBitmap, String bitName) {
//        String path = Util.getPath() + "image";
//        File temFile = new File(path);
//        if (!temFile.exists()) {
//            temFile.mkdirs();
//        }
//        File f = new File(path + "/" + bitName + ".png");
//        FileOutputStream fOut = null;
//        try {
//            fOut = new FileOutputStream(f);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//        try {
//            fOut.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            fOut.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}