package com.ccpress.izijia.yd.activity;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.OrderTouristAdapter;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.yd.adapter.WriteOrderDescAdapter;
import com.ccpress.izijia.yd.api.HttpApi;
import com.ccpress.izijia.yd.constant.ConstantApi;
import com.ccpress.izijia.yd.entity.Desc;
import com.ccpress.izijia.yd.entity.YdOrder;
import com.ccpress.izijia.yd.entity.YdOrderInfo;
import com.ccpress.izijia.yd.view.MaxListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/6/1.
 * 订单详情页
 */

public class YdOrderInfoActivity extends BaseActivity {
    private YdOrder.Data data;
    private RelativeLayout rl_godetail;
    private TextView tv_goods_name;
    private TextView tv_order_info;
    private TextView tv_total_money;
    private TextView tv_obligation_money;
    private TextView tv_linkman_info;
    private MaxListView lv_monry;
    private MaxListView lv_tourist;
    private String order_id;
    private String title;
    private String img;

    @Override
    protected int getRid() {
        return R.layout.yd_activity_order_info;
    }

    @Override
    protected void initTitleBar() {
        title_bar.setTvTitleText("订单详情");
    }

    @Override
    protected void initData() {
        order_id = getIntent().getStringExtra("order_id");
        String uid = getIntent().getStringExtra("uid");
        img = getIntent().getStringExtra("img");
        Map<String, String> map = new HashMap<>();
        map.put("order_id", order_id);
        map.put("uid", uid);
        new HttpApi(ConstantApi.ORDER_INFO, map) {
            @Override
            protected void success(String json) {
                YdOrderInfo orderInfo = JsonUtil.getJavaBean(json, YdOrderInfo.class);
                if (orderInfo.result == 0) {
                    fillData(orderInfo);
                } else {
                    CustomToast.showToast("订单信息获取失败");
                }
            }

            @Override
            public void error() {
                CustomToast.showToast("订单信息获取失败");
            }
        }.post();
    }

    private void fillData(YdOrderInfo orderInfo) {
        title = orderInfo.supplier.supplier_name;
        tv_goods_name.setText(title);
        order_id = orderInfo.data.order_id;
        StringBuffer order_info = new StringBuffer();
        order_info.append("订单编号：").append(orderInfo.data.order_sn).append("\n\n").append("入住日期：").append(orderInfo.data.gotime)
                .append("\n\n").append("离开日期：").append(orderInfo.data.outtime).append("\n\n").append("出行人数：").append(orderInfo.data.crnum).append("名成人").append(orderInfo.data.etnum)
                .append("名儿童").append("\n\n").append("订单状态：").append(orderInfo.data.order_status);
        tv_order_info.setText(order_info.toString());

        List<Desc> descs = new ArrayList<>();
        List<YdOrderInfo.GoodsList> goodsList = orderInfo.goods_list;
        for (int i = 0; goodsList != null && i < goodsList.size(); i++) {
            Desc desc1 = new Desc();
            YdOrderInfo.GoodsList goodsList1 = goodsList.get(i);
            desc1.name = goodsList1.goods_name;
            desc1.count = goodsList1.goods_number;
            desc1.price = goodsList1.goods_price;
            descs.add(desc1);
        }
        Desc desc = new Desc();
        desc.name = "保险总价格";
        desc.count = 1;
        desc.price = orderInfo.data.crfee + orderInfo.data.etfee;
        descs.add(desc);
        lv_monry.setAdapter(new WriteOrderDescAdapter(this, descs, true));
        tv_total_money.setText("订单总金额：" + orderInfo.data.order_amount);
        tv_obligation_money.setText("待付总金额：" + (orderInfo.data.order_amount - orderInfo.data.money_paid));
        tv_linkman_info.setText("姓名：" + orderInfo.data.consignee + "\n\n手机：" + orderInfo.data.mobile);
        lv_tourist.setAdapter(new OrderTouristAdapter(this, orderInfo.youke_list));
    }

    @Override
    protected void initView() {
        rl_godetail = getView(R.id.rl_godetail);
        tv_goods_name = getView(R.id.tv_goods_name);
        tv_order_info = getView(R.id.tv_order_info);
        tv_total_money = getView(R.id.tv_total_money);
        tv_obligation_money = getView(R.id.tv_obligation_money);
        tv_linkman_info = getView(R.id.tv_linkman_info);
        lv_monry = getView(R.id.lv_monry);
        lv_tourist = getView(R.id.lv_tourist);
        rl_godetail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == rl_godetail) {
            godetail();
        }
    }

    /**
     * 跳转到营地详情
     */
    private void godetail() {
        Intent intent = new Intent(this, StoresInfoActivity.class);
        intent.putExtra("id", order_id);
        intent.putExtra("title", title);
        intent.putExtra("img", img);
        startActivity(intent);
    }
}
