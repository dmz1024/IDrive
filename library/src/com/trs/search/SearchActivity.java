
package com.trs.search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import com.trs.app.TRSFragmentActivity;
import com.trs.mobile.R;

/**
 * Created by pompomandtinman on 9/26/13.
 */

public class SearchActivity extends TRSFragmentActivity {
    public static final String EXTRA_DEFAULT_SEARCH_KEY = "cq";
    public static final String DEFAULT_SEARCH_KEY = "重庆";

    public static final String SEARCH_TITLE__BROADCAST = "com.trs.search.title";
    public static final String EXTRA_SEARCH_KEYWORD_BROADCAST = "search_keyword";
    private TitleBroadCast titleBroadCast;
    TextView TitletextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_activity);
        findViewById(R.id.shareimgid).setVisibility(View.GONE);
	}

    class TitleBroadCast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String keyWord = intent.getExtras().getString(EXTRA_SEARCH_KEYWORD_BROADCAST);
            TitletextView.setText(keyWord.equals(DEFAULT_SEARCH_KEY)?"重庆相关":keyWord);
        }
    }

	@Override
	protected Fragment createFragment() {
		Fragment fragment = new SearchFragment();

		Bundle arguments = new Bundle();
		arguments.putString(SearchFragment.EXTRA_TITLE, "搜索");

        // 修改标题的广播注册.
        IntentFilter filter = new IntentFilter();
        titleBroadCast = new TitleBroadCast();
        filter.addAction(SEARCH_TITLE__BROADCAST);
        registerReceiver(titleBroadCast, filter);

//        TitletextView = ((TextView)findViewById(R.id.document_title_text));
//        if(getIntent().getExtras() != null){
//            arguments.putString(EXTRA_DEFAULT_SEARCH_KEY,
//                    getIntent().getStringExtra(EXTRA_DEFAULT_SEARCH_KEY).equals(DEFAULT_SEARCH_KEY)?DEFAULT_SEARCH_KEY:"");
//        }else{
//            TitletextView.setText("搜索");
//        }

        fragment.setArguments(arguments);

		return fragment;
	}

	@Override
	protected int getFragmentContainerID() {
		return R.id.content;
	}
}
