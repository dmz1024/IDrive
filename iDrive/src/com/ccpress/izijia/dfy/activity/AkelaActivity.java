package com.ccpress.izijia.dfy.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.fragment.AkelaFragment;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.view.TopView;


/**
 * Created by administror on 2016/3/23 0023.
 */
public class AkelaActivity extends BaseActivity {
    private AkelaFragment akelaFragment;
    @Override
    protected int getRid() {
        return R.layout.dfy_activity_akela;
    }

    @Override
    protected void initTopView(TopView topView) {
        super.initTopView(topView);
        topView.setText("领队详情");
        akelaFragment=new AkelaFragment(getIntent().getStringExtra("brand"));
        getSupportFragmentManager().beginTransaction().add(R.id.frag_akela,akelaFragment).show(akelaFragment).commit();
    }

    @Override
    protected void initView() {
        super.initView();

    }
}
