package com.ccpress.izijia.yd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.activity.BaseActivity;
import com.ccpress.izijia.dfy.entity.AffirmOrder;
import com.ccpress.izijia.dfy.pay.PayQuery;
import com.ccpress.izijia.dfy.pay.Pay_yl;
import com.ccpress.izijia.dfy.pay.Pay_zfb;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.view.TopView;
import com.ccpress.izijia.yd.adapter.WriteOrderDescAdapter;
import com.ccpress.izijia.yd.constant.ConstantApi;
import com.ccpress.izijia.yd.entity.Desc;
import com.ccpress.izijia.yd.entity.SerializableList;
import com.ccpress.izijia.yd.view.MaxListView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * Created by dmz1024 on 2016/3/27.
 */
public class YdPayActivity extends BaseActivity implements View.OnClickListener {
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
    @ViewInject(R.id.top_view)
    private TopView top_view;
    @ViewInject(R.id.tv_name)
    private TextView tv_name;
    @ViewInject(R.id.tv_time)
    private TextView tv_time;
    @ViewInject(R.id.rl_show)
    private RelativeLayout rl_show;
    @ViewInject(R.id.lv_desc)
    private MaxListView lv_desc;
    @ViewInject(R.id.tv_linkman)
    private TextView tv_linkman;
    @ViewInject(R.id.tv_tel)
    private TextView tv_tel;
    @ViewInject(R.id.tv_count)
    private TextView tv_count;
    @ViewInject(R.id.tv_show)
    private TextView tv_show;
    @ViewInject(R.id.bt_submit)
    private Button bt_submit;

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
        return R.layout.yd_activity_payment;
    }

    @Override
    protected void initTopView(TopView topView) {
        super.initTopView(topView);
        topView.setText("立即支付");

    }

    private void initOnclick() {
        bt_submit.setOnClickListener(this);
        iv_zfb.setOnClickListener(this);
        iv_wx.setOnClickListener(this);
        iv_yl.setOnClickListener(this);
        tv_show.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        id = bundle.getString("order_id");
        String[] times = bundle.getStringArray("times");
        String title = bundle.getString("title");
        int count = bundle.getInt("count");
        SerializableList<Desc> list = (SerializableList<Desc>) bundle.getSerializable("list");
        String linkman = bundle.getString("name");
        String tel = bundle.getString("tel");
        int peosonCount = bundle.getInt("peosonCount");
        double totalMoney = bundle.getDouble("totalMoney");
        double baoxian = bundle.getDouble("baoxian");
        tv_money.setText("￥" + totalMoney);
        tv_name.setText(title);
        List<Desc> descList = list.getMap();
        if (baoxian > 0) {
            Desc desc = new Desc();
            desc.name = "保险总费用";
            desc.price = baoxian;
            desc.count = 1;
            descList.add(desc);
        }
        tv_time.setText(times[0] + " 入住      " + times[1] + "  离开      共预订" + count + "项");
        tv_linkman.setText(linkman);
        tv_tel.setText(tel);
        tv_count.setText(peosonCount + "");

        lv_desc.setAdapter(new WriteOrderDescAdapter(this, descList, true));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_submit:
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
            case R.id.tv_show:
                show();
                break;
        }
    }

    private void show() {
        if (rl_show.getVisibility() == View.VISIBLE) {
            rl_show.setVisibility(View.GONE);
            tv_show.setText("更多");
        } else {
            rl_show.setVisibility(View.VISIBLE);
            tv_show.setText("收起");
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
        new Pay_yl(this, id, ConstantApi.YD_PAY_YL).pay();
    }

    private void payWX() {
        CustomToast.showToast("暂不支持微信支付");
    }

    private void payZFB() {
        new Pay_zfb(id, this, ConstantApi.YD_PAY_ZFB, 2).Pay();//通过返回的支付信息进行支付
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

