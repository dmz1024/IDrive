package com.trs.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import com.trs.main.slidingmenu.RightMenuFragment;
import com.trs.mobile.R;

/**
 * Created by wbq on 14-6-17.
 */
public class JHSettingActivity extends TRSFragmentActivity{
    private Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jh_setting_activity);

        RightMenuFragment fragment = new RightMenuFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content,fragment).commit();
    }
}
