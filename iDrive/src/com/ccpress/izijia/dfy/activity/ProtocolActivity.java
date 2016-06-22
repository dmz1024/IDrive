package com.ccpress.izijia.dfy.activity;

import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.view.TopView;

/**
 * Created by dmz1024 on 2016/4/11.
 */
public class ProtocolActivity extends BaseActivity {
    @Override
    protected int getRid() {
        return R.layout.dfy_activity_protocol;
    }

    @Override
    protected void initTopView(TopView topView) {
        super.initTopView(topView);
        topView.setText("旅游协议");
    }
}
