package com.ccpress.izijia.activity.portal;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.ccpress.izijia.R;
import com.ccpress.izijia.componet.TitleBar;

/**
 * 关于/协议页面展示
 * @author wangyi
 *
 */
public class AboutActivity extends BaseActivity {
	
	@ViewInject(R.id.view_wb)
	private WebView wb_view;
	
	@Override
	public void doBusiness() {
		TitleBar bar=new TitleBar(activity);
		bar.showBack();
		bar.setTitle(getVo("0").toString());
		
		wb_view.getSettings().setJavaScriptEnabled(true);
		wb_view.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		wb_view.setWebChromeClient(new WebChromeClient());
		String linkUrl=(String) getVo("1");
		wb_view.loadUrl(linkUrl);
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_about;
	}
}
