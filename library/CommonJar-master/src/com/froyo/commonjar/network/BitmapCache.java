package com.froyo.commonjar.network;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapCache implements ImageCache {

	/** 目前图片均是保存在内存中 */
	private LruCache<String, Bitmap> mCache;

	private static int maxSize = 50 * 1024 * 1024;
	
	private static final BitmapCache mInstance = new BitmapCache();

	public static BitmapCache getInstance() {
		return BitmapCache.mInstance;
	}

	private BitmapCache() {

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
		mCache.put(url, bitmap);
	}
}
