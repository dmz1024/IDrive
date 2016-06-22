package com.trs.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.trs.app.TRSApplication;
import com.trs.mobile.R;
import com.trs.types.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wubingqian on 14-3-11.
 */
public class BottomTabMainActivity extends Activity{
    // TODO . from server.
    private static int mTabNumbers;
    private Context mContext = this;
    private LinearLayout mBottomLayout;
    private List<Channel> mTabDataList = new ArrayList<Channel>();
    private TRSApplication mApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_bottom_tab);

        mTabDataList.clear();
        mApplication = (TRSApplication) getApplicationContext();
        mTabDataList = mApplication.getFirstClassMenu().getChannelList();

        initView();
        setView();
    }

    private void initView(){
        mBottomLayout = (LinearLayout) findViewById(R.id.bottomId);
    }

    private void setView(){
        for(int i = 0;i < 5;i++){
            Button btn = new Button(mContext);
            btn.setText(mTabDataList.get(i).getTitle());
            btn.setTextSize(10);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1);
            mBottomLayout.addView(btn,params);
        }
    }
}
