package com.trs.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.*;
import com.manuelpeinado.quickreturnheader.QuickReturnHeaderHelper;
import com.trs.app.ShowWebImageActivity;
import com.trs.frontia.FrontiaAPI;
import com.trs.main.slidingmenu.AppSetting;
import com.trs.mobile.BuildConfig;
import com.trs.mobile.R;
import com.trs.util.FileUtil;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;
import com.trs.wcm.LoadWCMJsonTask;
import com.trs.wcm.RemoteDataService;
import net.endlessstudio.util.Util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by john on 14-3-18.
 */
public class WebViewFragment extends AbsUrlFragment{
	public static final String TAG = "WebPageActivity";

	public static final String EXTRA_HEADER_LAYOUT_ID = "header_res_id";
	public static final String EXTRA_TITLE = "title";

	private WebView mWebView;
	private View mLoadingView;
	private ProgressBar mProgress;
	private QuickReturnHeaderHelper mQuickReturnHelper;

	public class JavascriptInterface {
		private Context context;
		public JavascriptInterface(Context context) {
			this.context = context;
		}

		public void share(String title, String content, String url, String img){
			if(!StringUtil.isEmpty(title) && !StringUtil.isEmpty(url)){
//				GoShare share = new GoShare(getActivity(), title, content, url, img);
//				share.shareStart();
                FrontiaAPI frontia = FrontiaAPI.getInstance(getActivity().getApplicationContext());
                frontia.goShare(getActivity(), title, content, url, Uri.parse(img));
			}
		}

        public void openImage(final String img) {
            System.out.println(img);
            Handler mHandler = new Handler();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.putExtra("image", img);
                    intent.setClass(context, ShowWebImageActivity.class);
                    context.startActivity(intent);
                }
            });

            System.out.println(img);
        }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		int headerLayoutID = getArguments().getInt(EXTRA_HEADER_LAYOUT_ID);

		mQuickReturnHelper = new QuickReturnHeaderHelper(getActivity(), R.layout.web_view_fragment, headerLayoutID, getFooterBarID());
		View view = mQuickReturnHelper.createView();

//        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.title_layout);
//        TextView titleText = (TextView) relativeLayout.findViewById(R.id.document_title_text);
//        String title = getArguments().getString(EXTRA_TITLE);
//        titleText.setText(title.length() > 11 ? title.substring(0,11) + "..." : title);

		mWebView = (WebView) view.findViewById(R.id.web_view);
		mLoadingView = view.findViewById(R.id.loading_view);
		mProgress = (ProgressBar) view.findViewById(R.id.progress);

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDefaultTextEncodingName("utf-8");
		mWebView.getSettings().setAppCacheEnabled(true);
        //mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		File cacheFile = getActivity().getApplicationContext().getCacheDir();
		if(cacheFile != null){
			mWebView.getSettings().setAppCachePath(cacheFile.getAbsolutePath());
		}
		mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		//mWebView.setInitialScale(100);
		if (Build.VERSION.SDK_INT < 8) {
			mWebView.getSettings().setPluginsEnabled(true);
		}
		else {
			mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
		}
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.addJavascriptInterface(new JavascriptInterface(getActivity()), "weblistener");
		mWebView.getSettings().setDomStorageEnabled(true);

		int textZoom;
		WebSettings.TextSize textSize;
		switch(AppSetting.getInstance(getActivity()).getFontSize()){
			case Small:
				textZoom = 75;
				textSize = WebSettings.TextSize.SMALLER;
				break;
			case Large:
				textZoom = 150;
				textSize = WebSettings.TextSize.LARGER;
				break;
			default:
				textZoom = 100;
				textSize = WebSettings.TextSize.NORMAL;
				break;
		}

		if(Build.VERSION.SDK_INT <= 10){
			mWebView.getSettings().setTextSize(textSize);
		}
		else{
			mWebView.getSettings().setTextZoom(textZoom);
		}

		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedSslError(android.webkit.WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();
			}

			@Override
			public void onPageFinished(android.webkit.WebView view, String url) {
                super.onPageFinished(view, url);

				if(getActivity() == null){
					return;
				}

				hideLoading();
				try {
					String js = String.format(Util.getString(getActivity(), "raw://weblistener", "utf-8"), mWebView.getWidth());
					mWebView.loadUrl("javascript:" + js);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);

			}

			@Override
			public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {  //重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onLoadResource(android.webkit.WebView view, String url) {
				super.onLoadResource(view, url);

				Log.i(TAG, "progress loading resource: " + url);
			}
		});

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(android.webkit.WebView view, int newProgress) {
				Log.i(TAG, "progress: " + newProgress);
				if (newProgress < 100) {
					mProgress.setVisibility(View.VISIBLE);
					mProgress.setProgress(newProgress);
				} else {
					mProgress.setProgress(newProgress);
					mProgress.setVisibility(View.GONE);
				}
				super.onProgressChanged(view, newProgress);
			}

			@Override
			public boolean onJsAlert(android.webkit.WebView view, String url, String message, final JsResult result) {
				if(!BuildConfig.DEBUG){
					return true;
				}

				AlertDialog.Builder b2 = new AlertDialog.Builder(getActivity());
				b2.setMessage(message);
				b2.setPositiveButton("ok",
						new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								result.confirm();
							}
						});

				b2.setCancelable(false);
				b2.create();
				b2.show();
				return true;

			}

			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
//				super.onShowCustomView(view, callback);
			}

			@Override
			public void onHideCustomView() {
//				super.onHideCustomView();
			}
		});

		mWebView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				Log.i(TAG, String.format("download: url: %s userAgent: %s contentDisposition: %s mimetype: %s contentLength: %s",
						url, userAgent, contentDisposition, mimetype, contentDisposition));
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
		mWebView.requestFocus();
		loadData();


		return view;
	}

	protected void loadWebPage(){
		mWebView.loadUrl(getUrl());
	}

	private void showHtml(String html){
		mWebView.loadDataWithBaseURL(getUrl(), html, "text/html", "utf-8", null);
	}

	private void showLoading(){
		Log.i(TAG, "show loading");
		mLoadingView.setVisibility(View.VISIBLE);
	}

	private void hideLoading(){
		Log.i(TAG, "hide loading");
		mLoadingView.setVisibility(View.GONE);
	}

    private void loadData(){
        RemoteDataService rms = new RemoteDataService(this.getActivity());//先尝试加载本地离线下载的html
        String local_url = rms.getCacheFilePathFromURL(getUrl());
        if(FileUtil.fileExists(local_url)){
            try {
                String result = FileUtil.readFile(local_url, "utf-8");
                showHtml(result);
            } catch (Exception e) {
                loadDataWithNetwork();
            }
        }else{
            loadDataWithNetwork();
        }
    }

    private void loadDataWithNetwork(){
        if(getUrl().endsWith(".json")){

            LoadWCMJsonTask loader = new LoadWCMJsonTask(getActivity()) {
                @Override
                public void onDataReceived(String result, boolean isCache) throws Exception {
//					JSONObject obj = new JSONObject(result);
//					mDetail = new Detail(obj);
//					String url = mDetail.getWbURL();
//					showUrl(url);
                }

                @Override
                public void onError(Throwable t) {
                    hideLoading();
                    Toast.makeText(getActivity(), R.string.data_parse_wrong, Toast.LENGTH_LONG).show();
                }

                @Override
                protected void onStart() {
                    showLoading();
                    super.onStart();
                }

                @Override
                protected void onEnd() {
                    hideLoading();
                    super.onEnd();
                }
            };
            loader.start(getUrl());
        }
        else{
            loadWebPage();
        }
    }

	public void onPictureClick(String picName){
		//TODO
	}

	public int getFooterBarID(){
		return 0;
	}

	@Override
	public void onDisplay() {

	}

	@Override
	public void onHide() {

	}

	@Override
	public void onPause() {
		super.onPause();
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1){
			mWebView.onPause();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1){
			mWebView.onResume();
		}
	}

	public WebView getWebView(){
		return mWebView;
	}
}
