package com.ccpress.izijia.activity;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.*;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.entity.InfoDetailEntity;
import com.ccpress.izijia.fragment.HomeInteractListFragment;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.ShareUtil;
import com.trs.app.TRSFragmentActivity;
import com.trs.fragment.WebViewFragment;
import com.trs.main.slidingmenu.AppSetting;
import com.trs.util.log.Log;
import net.endlessstudio.util.Util;

import java.io.File;
import java.io.IOException;

/**
 * Created by Wu Jingyu
 * Date: 2015/9/6
 * Time: 15:53
 */
public class WebViewActivity extends TRSFragmentActivity {
    private WebView mWebView;
    private ImageView btn_back;
    private TextView txt_close;
    private  TextView bar_title;
    public static String TITLE_NAME="目的地";
    public static String FLAG="falg";
    private ImageView web_btn_share;
    private String init_url = "";
    private String current_url = "";
    private ProgressBar mProgress;
    public static String image=null;
    public static String web_title=null;
    public static String id="0";
    public static String type="17";
    public static String web_url=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        init_url = getIntent().getStringExtra("url");
        current_url = init_url;

        mWebView = (WebView) findViewById(R.id.webView);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        txt_close = (TextView) findViewById(R.id.txt_close);
        bar_title = (TextView) findViewById(R.id.bar_title);
        web_btn_share = (ImageView) findViewById(R.id.web_btn_share);
        mProgress = (ProgressBar) findViewById(R.id.progress);

        if (TITLE_NAME.equals("手机精选")){
            web_btn_share.setVisibility(View.GONE);
        }

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.getSettings().setAppCacheEnabled(true);
        //mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        File cacheFile = this.getApplicationContext().getCacheDir();
        if(cacheFile != null){
            mWebView.getSettings().setAppCachePath(cacheFile.getAbsolutePath());
        }
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //mWebView.setInitialScale(100);
//        if (Build.VERSION.SDK_INT < 8) {
//            mWebView.getSettings().setPluginsEnabled(true);
//        }
//        else {
//            mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
//        }
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
//        mWebView.addJavascriptInterface(new WebViewFragment.JavascriptInterface(getActivity()), "weblistener");
        mWebView.getSettings().setDomStorageEnabled(true);

        int textZoom;
        WebSettings.TextSize textSize;
        switch(AppSetting.getInstance(this).getFontSize()){
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
                if(url.equals(init_url)){
                    //小米手机返回不回调  onReceivedTitle(WebView view, String title) 因此这里手动设置Title
                    bar_title.setText(TITLE_NAME);
                    dismissCloseBtn();
                } else {
                    displayCloseBtn();
                }
            }

            @Override
            public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {  //重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                web_url=url;
                view.loadUrl(url);
                web_title=view.getTitle();
//                view.loadUrl("file:///android_asset/2.html");
                return true;
            }

            @Override
            public void onLoadResource(android.webkit.WebView view, String url) {
                super.onLoadResource(view, url);
            }
        });
        //设置bar_title
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                bar_title.setText(title);
                //web_title=title;
                if (!title.equals("手机精选")){
                    web_btn_share.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onProgressChanged(android.webkit.WebView view, int newProgress) {
                if (newProgress < 100) {
                    mProgress.setVisibility(View.VISIBLE);
                    mProgress.setProgress(newProgress);
                } else {
                    mProgress.setProgress(newProgress);
                    mProgress.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        mWebView.loadUrl(init_url);
    }




    private String imageURL="http://member.izijia.cn/appshare.jpg";

    /**
     * 实现分享
     * @param view
     */
    public void OnBtnShareClick(View view){
        Log.e("shouldOverrideUrlLoading ",web_url );
         image = (image==null) ? imageURL : image;
        if (FLAG=="精选"){
            web_title=mWebView.getTitle();
        }
        ShareUtil.showShare(this, id, type,web_title,image,web_url,null);

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    /**
     * 返回
     * @param v
     */
    public void onBtnBackClick(View v) {
        if(mWebView.canGoBack()){
            mWebView.goBack();
            web_btn_share.setVisibility(View.GONE);
        } else {
            finish();
        }
    }

    public void onBtnCloseClick(View v) {
        finish();
    }

    private void displayCloseBtn() {
        txt_close.setVisibility(View.VISIBLE);
    }

    private void dismissCloseBtn()
    {
        txt_close.setVisibility(View.INVISIBLE);
    }
}
