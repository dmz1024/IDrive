package com.trs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import com.trs.util.AsyncTask;
import com.trs.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

/**
 * Created by john on 13-11-15.
 */
public class WebView extends android.webkit.WebView{
	private static class DownloadImageTask extends AsyncTask<String, Integer, File> {
		private static LinkedList<AsyncTask> sTaskList = new LinkedList<AsyncTask>();

		private Context mContext;
		private HttpURLConnection mConnection;

		public DownloadImageTask(Context context){
			if(context == null){
				throw new NullPointerException();
			}

			this.mContext = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			sTaskList.add(this);
		}

		@Override
		protected File doInBackground(String... params) {
			String imgUrl = params[0];
			File localFile = getCacheFilePathFromURL(mContext, imgUrl);

			if(localFile.exists() && localFile.length() > 0){
				return localFile;
			}


			InputStream is = null;
			FileOutputStream fos = null;
			try{
				if(!localFile.exists()){
					localFile.createNewFile();
				}

				mConnection = (HttpURLConnection) new URL(imgUrl).openConnection();
				mConnection.setRequestMethod("GET");
				mConnection.setDoInput(true);
				mConnection.connect();

				int responseCode = mConnection.getResponseCode();
				if(responseCode < 200 && 300 <= responseCode){
					return null;
				}

				is = mConnection.getInputStream();
				byte[] buf = new byte[10 * 1024];
				int readlen;
				fos = new FileOutputStream(localFile);
				while((readlen = is.read(buf)) >= 0){
					fos.write(buf, 0, readlen);
				}

				return localFile;
			}
			catch(Exception e){
				return null;
			}
			finally{
				Util.close(is);
				Util.close(fos);
				if(mConnection != null){
					mConnection.disconnect();
				}

				mConnection = null;
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mConnection.disconnect();

			sTaskList.remove(this);
		}

		@Override
		protected void onPostExecute(File outputFile) {
			super.onPostExecute(outputFile);

			sTaskList.remove(this);
		}

		public static File getCacheFilePathFromURL(Context context, String httpUrl) {
			File cacheRoot = Util.getCacheDir(context, Util.CacheType.Image);
			// 去掉站点的http前缀
			String subPath = httpUrl.replaceFirst("^http[s]?://", "");

			return new File(cacheRoot, subPath);
		}

		public void start(String url){
			executeOnExecutor(THREAD_POOL_EXECUTOR, url);
		}

		public static void cancelAll(){
			for(AsyncTask task: sTaskList){
				task.cancel(true);
			}
		}

	}

	public WebView(Context context) {
		super(context);
		init();
	}

	public WebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public WebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init(){
		WebSettings webSettings = getSettings();

		webSettings.setLightTouchEnabled(true);
		webSettings.setJavaScriptEnabled(true);

		webSettings.setDomStorageEnabled(true);
		webSettings.setDatabaseEnabled(true);

		webSettings.setSupportZoom(true);

		webSettings.setAllowFileAccess(true);

		webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		setInitialScale(50);

		setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		setWebViewClient(new WebViewClient());
		setWebChromeClient(new WebChromeClient());
	}

	public void loadData(String data, String[] images) {
		//TODO download images
		String baseUrl = null;
		if(images.length > 0){
			baseUrl = DownloadImageTask.getCacheFilePathFromURL(getContext(), images[0]).getParentFile().getAbsolutePath();
		}
		super.loadDataWithBaseURL(baseUrl, data, "text/html", "UTF-8", null);

		for(String img: images){
			DownloadImageTask task = new DownloadImageTask(getContext());
			task.start(img);
		}
	}
}
