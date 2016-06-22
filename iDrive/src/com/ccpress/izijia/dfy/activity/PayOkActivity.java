package com.ccpress.izijia.dfy.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.MainActivity;
import com.ccpress.izijia.dfy.entity.PayDetail;
import com.ccpress.izijia.dfy.view.TopView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by administror on 2016/3/23 0023.
 * 支付成功界面
 */
public class PayOkActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.tv_tourist_name)
    private TextView tv_tourist_name;
    @ViewInject(R.id.tv_goods_name)
    private TextView tv_goods_name;
    @ViewInject(R.id.tv_pay_type)
    private TextView tv_pay_type;
    @ViewInject(R.id.tv_pay_order)
    private TextView tv_pay_order;
    @ViewInject(R.id.tv_pay_time)
    private TextView tv_pay_time;
    @ViewInject(R.id.tv_pay_money)
    private TextView tv_pay_money;
    @ViewInject(R.id.tv_pay_tishi)
    private TextView tv_pay_tishi;
    @ViewInject(R.id.tv_back_buy)
    private TextView tv_back_buy;

    @Override
    protected int getRid() {
        return R.layout.dfy_activity_payok;
    }

    @Override
    protected void initTopView(TopView topView) {
        topView.setLeftOnclick(new TopView.LeftOnclick() {
            @Override
            public void left() {
                Intent intent=new Intent(PayOkActivity.this,OrderActivity.class);
                startActivity(intent);
                finish();
            }
        });
        topView.setText(R.string.dfy_pay_detail);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_back_buy.setOnClickListener(this);
        PayDetail payDetail= (PayDetail) getIntent().getSerializableExtra("payDetail");
        tv_tourist_name.setText(payDetail.getName());
        tv_goods_name.setText(payDetail.getTitle());
        tv_pay_type.setText(payDetail.getAccNo()==""?"银联支付":payDetail.getAccNo());
        tv_pay_order.setText(payDetail.getOrder_sn());
        tv_pay_time.setText(payDetail.getPay_time());
        tv_pay_money.setText(payDetail.getMoney());
        tv_pay_tishi.setText("感谢您订购" + payDetail.getTitle() + ",爱自驾预祝你旅途愉快");
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent(PayOkActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Intent intent=new Intent(PayOkActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
