package com.ccpress.izijia.yd.activity;

import com.ccpress.izijia.R;
import com.ccpress.izijia.yd.fragment.YdFragment;

/**
 * Created by dengmingzhi on 16/5/25.
 */
public class YdActivity extends FragmentActivity {

    @Override
    protected void initTitleBar() {
        title_bar.setTvTitleText("营地");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        getSupportFragmentManager().beginTransaction().add(R.id.fg_base, new YdFragment()).commit();
    }
}
