package com.ccpress.izijia.dfy.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.entity.AffirmOrder;
import com.ccpress.izijia.dfy.pay.PayQuery;
import com.ccpress.izijia.dfy.pay.Pay_yl;
import com.ccpress.izijia.dfy.pay.Pay_zfb;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.view.TopView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by dmz1024 on 2016/3/27.
 */
public class PayActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.iv_back)
    private ImageView iv_back;
    @ViewInject(R.id.tv_money)
    private TextView tv_money;
    @ViewInject(R.id.iv_zfb)
    private ImageView iv_zfb;
    @ViewInject(R.id.iv_wx)
    private ImageView iv_wx;
    @ViewInject(R.id.iv_yl)
    private ImageView iv_yl;
    @ViewInject(R.id.tv_total_money)
    private TextView tv_total_money;
    @ViewInject(R.id.tv_countdown)
    private TextView tv_countdown;
    @ViewInject(R.id.tv_Payment)
    private TextView tv_Payment;
    @ViewInject(R.id.top_view)
    private TopView top_view;
    private AffirmOrder affirm;
    private int payType = 1;
    private String id;
    private String totalMoney;

    @Override
    protected void initView() {
        super.initView();
        initData();
        initOnclick();
    }

    @Override
    protected int getRid() {
        return R.layout.dfy_activity_payment;
    }

    @Override
    protected void initTopView(TopView topView) {
        super.initTopView(topView);
        topView.setText("选择支付方式");

    }

    private void initOnclick() {
        tv_Payment.setOnClickListener(this);
        iv_zfb.setOnClickListener(this);
        iv_wx.setOnClickListener(this);
        iv_yl.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        id = intent.getStringExtra("order_id");
        totalMoney = intent.getStringExtra("totalMoney");
        tv_money.setText("￥" + totalMoney);
        tv_total_money.setText("￥" + totalMoney);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_Payment:
                goPay();//进行付款
                break;
            case R.id.iv_zfb:
                payType(iv_zfb);
                break;
            case R.id.iv_wx:
                payType(iv_wx);
                break;
            case R.id.iv_yl:
                payType(iv_yl);
                break;
        }
    }

    private void goPay() {
        switch (payType) {
            case 1:
                payZFB();//支付宝支付
                break;
            case 2:
                payWX();//微信支付
                break;
            case 3:
                payYL();//银联支付
                break;
        }
    }

    private void payYL() {
        new Pay_yl(this, id, Constant.DFY_PAY_YL).pay();
    }

    private void payWX() {
        CustomToast.showToast("暂不支持微信支付");
    }

    private void payZFB() {
        new Pay_zfb(id, this,Constant.DFY_PAY_ZFB,1).Pay();//通过返回的支付信息进行支付
    }


    private void payType(ImageView iv) {
        iv_zfb.setImageResource(R.drawable.dfy_icon_zhi_circle);
        iv_wx.setImageResource(R.drawable.dfy_icon_zhi_circle);
        iv_yl.setImageResource(R.drawable.dfy_icon_zhi_circle);
        if (iv == iv_zfb) {
            iv_zfb.setImageResource(R.drawable.dfy_icon_zhi_dian);
            payType = 1;//1。支付宝
        } else if (iv == iv_wx) {
            iv_wx.setImageResource(R.drawable.dfy_icon_zhi_dian);
            payType = 2;//2。微信
        } else {
            iv_yl.setImageResource(R.drawable.dfy_icon_zhi_dian);
            payType = 3;//3。银联
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            new PayQuery(this, id).getPayResult(4);
        } else if (str.equalsIgnoreCase("dfy_fail")) {
            CustomToast.showToast(" 支付失败！ ");
        } else if (str.equalsIgnoreCase("cancel")) {
            CustomToast.showToast(" 你已取消了本次订单的支付！ ");
        }
    }


}

