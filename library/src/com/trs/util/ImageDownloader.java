package com.trs.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.trs.mobile.R;
import com.trs.util.log.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class ImageDownloader extends ImageLoader {
	public static final String TAG_LARGE_IMAGE = "LARGE_IMAGE";
	public static final BitmapFactory.Options DEC_OPT = new BitmapFactory.Options();
	static {
		DEC_OPT.inInputShareable = true;
		DEC_OPT.inPurgeable = true;
	}

	public static enum OptionsType {
		TOP_PIC, DEFAULT_PIC;
	};

	private String mUrl;
	private ImageView mImageView;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mDisplayImageOptions = createDisplayImageOptions(OptionsType.DEFAULT_PIC);
	private int mDisplayWidth = 0;

	private void checkImageWidth(final String url, final int requiredWidth){
		if(requiredWidth <= 0){
			return;
		}

		Runnable r = new Runnable(){
			public void run(){
				HttpURLConnection conn = null;
				InputStream is = null;

				try{
					conn = (HttpURLConnection)new URL(url).openConnection();
					conn.setRequestMethod("GET");

					conn.connect();

					int code = conn.getResponseCode();

					if(200 <= code && code < 300){
						int contentLength = conn.getContentLength();

						is = conn.getInputStream();

						BitmapFactory.Options opt = new BitmapFactory.Options();
						opt.inJustDecodeBounds = true;

						BitmapFactory.decodeStream(is, null, opt);
						if((float)opt.outWidth / (float)mDisplayWidth >= 2.0f){
							Log.w(TAG_LARGE_IMAGE, String.format("bmp w: %4d req w: %4d size: %6.2fk url: %s", opt.outWidth, requiredWidth, (float) contentLength / 1000f, url));
						}
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				finally{
					if(is != null){
						try{
							is.close();
						}
						catch(IOException e){
							e.printStackTrace();
						}
					}

					if(conn != null){
						conn.disconnect();
					}
				}
			}
		};

		AsyncTask.THREAD_POOL_EXECUTOR.execute(r);
	}

	private BitmapProcessor processor = new BitmapProcessor() {
		@Override
		public Bitmap process(Bitmap bitmap) {
			if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
				return bitmap;
			}

			int width = mDisplayWidth <= 0? mDisplayWidth: mImageView.getWidth();
			if(width <= 0 || bitmap.getWidth() <= width){
				return bitmap;
			}

			if(bitmap.getWidth() / width < 2){
				return bitmap;
			}

			float scale = (float)width / (float)bitmap.getWidth();
			Matrix m = new Matrix();
			m.setScale(scale, scale);

			Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), m, true);
			if (bmp != bitmap) {
				bitmap.recycle();
			}

			return bmp;
		}
	};

	private DisplayImageOptions createDisplayImageOptions(OptionsType type){

		switch(type){
			case TOP_PIC:
				return new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.displayer(new SimpleBitmapDisplayer())
					.decodingOptions(DEC_OPT)
					//.resetViewBeforeLoading(true)
					.preProcessor(processor)
					.showStubImage(R.drawable.default_img_large)
					.showImageOnFail(R.drawable.default_img_large)
					.showImageForEmptyUri(R.drawable.default_img_large)
					.build();
			case DEFAULT_PIC:
				return new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.displayer(new SimpleBitmapDisplayer())
					.decodingOptions(DEC_OPT)
					//.resetViewBeforeLoading(true)
					.preProcessor(processor)
					.showStubImage(R.drawable.default_img_small)
					.showImageOnFail(R.drawable.default_img_small)
					.showImageForEmptyUri(R.drawable.default_img_small)
					.build();
		}

		return null;
	}

	private ImageDownloader(){
		mImageLoader = ImageLoader.getInstance();
	}

	public ImageDownloader(String url, ImageView imageview) {
		this();

		this.mUrl = url;
		this.mImageView = imageview;
	}

	public static class Builder {
		private ImageDownloader mDownload;
		public Builder(){
			mDownload = new ImageDownloader();
		}

		public Builder setOptionsType(OptionsType type) {
			return setOptionsType(type, 0);
		}

		public Builder setOptionsType(OptionsType type, int displayWidth){
			mDownload.setDisplayImageOptionsType(type);
			return this;
		}

		public Builder setOptions(DisplayImageOptions options) {
			mDownload.setDisplayImageOptions(options);
			return this;
		}

		public ImageDownloader build(String url, ImageView imageview) {
			mDownload.setUrl(url);
			mDownload.setImageView(imageview);

			return mDownload;
		}
	}

	public void setUrl(String url){
		this.mUrl = url;
	}

	public void setImageView(ImageView view){
		this.mImageView = view;
	}

	public void setDisplayImageOptionsType(OptionsType type){
		setDisplayImageOptionsType(type, 0);
	}

	public void setDisplayImageOptions(DisplayImageOptions opt){
		this.mDisplayImageOptions = opt;
	}

	/**
	 * displayWidth is not used any more
	 * @param type display image type
	 * @param displayWidth not used any more
	 */
	@Deprecated
	public void setDisplayImageOptionsType(OptionsType type, int displayWidth){
		mDisplayImageOptions = createDisplayImageOptions(type);
		this.mDisplayWidth = displayWidth;
	}

	public void start() {

		if (mUrl == null || mImageView == null) {return;}

		final Runnable displayImageRunnable = new Runnable() {
			@Override
			public void run() {

				if (mDisplayImageOptions != null) {
					mImageLoader.displayImage(mUrl, mImageView, mDisplayImageOptions);
				}
				else {
					mImageLoader.displayImage(mUrl, mImageView);
				}

			}
		};
		displayImageRunnable.run();
	}
}
