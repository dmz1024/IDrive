package com.ccpress.izijia.dfy.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.fragment.CollectFragment;

/**
* Created by dmz1024 on 2016/3/31.
*/
public class CollectActivity extends FragmentActivity {
    private CollectFragment collectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dfy_activity_collect);
        collectFragment = new CollectFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fl_collect, collectFragment);
        transaction.show(collectFragment);
        transaction.commit();
    }
}
