package com.trs.app;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import com.trs.fragment.WebViewFragment;
import com.trs.mobile.R;

/**
 * Created by john on 14-3-21.
 */
public class WebViewActivity extends TRSFragmentActivity{
	public static final String EXTRA_URL = WebViewFragment.EXTRA_URL;
	public static final String EXTRA_TITLE = WebViewFragment.EXTRA_TITLE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FrameLayout layout = new FrameLayout(this);
		layout.setId(R.id.content);
		layout.setBackgroundColor(0xffffffff);
		setContentView(layout);

		WebViewFragment fragment = new WebViewFragment();
		Bundle bundle = new Bundle();
		bundle.putString(WebViewFragment.EXTRA_URL, getIntent().getStringExtra(EXTRA_URL));
		bundle.putInt(WebViewFragment.EXTRA_HEADER_LAYOUT_ID, R.layout.common_sub_title);
		bundle.putString(WebViewFragment.EXTRA_TITLE, getIntent().getStringExtra(EXTRA_TITLE));
		fragment.setArguments(bundle);

		getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
		fragment.notifyDisplay();
	}

	public void onBtnBackClick(View view){
		finish();
	}
}
