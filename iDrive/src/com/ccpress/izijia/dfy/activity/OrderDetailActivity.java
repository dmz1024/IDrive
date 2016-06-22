package com.ccpress.izijia.dfy.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.entity.OrderDetail;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.dfy.util.ListViewUtil;
import com.ccpress.izijia.dfy.view.TopView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * Created by dmz1024 on 2016/4/4.
 */
public class OrderDetailActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.tv_goods_name)
    private TextView tv_goods_name;
    @ViewInject(R.id.iv_goDetail)
    private ImageView iv_goDetail;
    @ViewInject(R.id.tv_order_id)
    private TextView tv_order_id;
    @ViewInject(R.id.tv_start_city)
    private TextView tv_start_city;
    @ViewInject(R.id.tv_start_time)
    private TextView tv_start_time;
    @ViewInject(R.id.tv_end_time)
    private TextView tv_end_time;
    @ViewInject(R.id.tv_person_count)
    private TextView tv_person_count;
    @ViewInject(R.id.tv_house_count)
    private TextView tv_house_count;
    @ViewInject(R.id.tv_order_state)
    private TextView tv_order_state;
    @ViewInject(R.id.tv_cy_money)
    private TextView tv_cy_money;
    @ViewInject(R.id.tv_bx_money)
    private TextView tv_bx_money;
    @ViewInject(R.id.tv_order_total_money)
    private TextView tv_order_total_money;
    @ViewInject(R.id.tv_order_total_money1)
    private TextView tv_order_total_money1;
    @ViewInject(R.id.tv_linkman_name)
    private TextView tv_linkman_name;
    @ViewInject(R.id.tv_linkman_tel)
    private TextView tv_linkman_tel;
    @ViewInject(R.id.tv_linkman_email)
    private TextView tv_linkman_email;
    @ViewInject(R.id.lv_order)
    private ListView lv_order;
    @ViewInject(R.id.tv_bill)
    private TextView tv_bill;
    @ViewInject(R.id.tv_bill_title)
    private TextView tv_bill_title;
    @ViewInject(R.id.tv_bill_datail)
    private TextView tv_bill_datail;
    @ViewInject(R.id.tv_bill_name)
    private TextView getTv_bill_name;
    @ViewInject(R.id.tv_bill_tel)
    private TextView tv_bill_tel;
    @ViewInject(R.id.ll_bill)
    private LinearLayout ll_bill;
    @ViewInject(R.id.tv_bill_address)
    private TextView tv_bill_address;
    @ViewInject(R.id.tv_bill_name)
    private TextView tv_bill_name;
    @ViewInject(R.id.rl_godetail)
    private RelativeLayout rl_godetail;

    private String json;
    private String goods_id;

    @Override
    protected int getRid() {
        return R.layout.dfy_activity_order_detail;
    }

    @Override
    protected void initTopView(TopView topView) {
        super.initTopView(topView);
        topView.setText("订单详情");

    }

    @Override
    protected void initView() {
        super.initView();
        rl_godetail.setOnClickListener(this);
        json = getIntent().getStringExtra("json");
        goods_id = getIntent().getStringExtra("goods_id");
        orderWrite(json);
    }

    private void orderWrite(String json) {
        OrderDetail orderDetail = JsonUtil.getJavaBean(json, OrderDetail.class);
        if (orderDetail != null) {
            if (orderDetail.getResult() == 0) {
                tv_goods_name.setText(orderDetail.getGoods_name());
                tv_order_id.setText(tv_order_id.getText() + orderDetail.getOrderxq().getOrder_sn());
                tv_start_city.setText(tv_start_city.getText() + orderDetail.getOrderxq().getCity());
                tv_start_time.setText(tv_start_time.getText() + orderDetail.getOrderxq().getCyrq());
                tv_end_time.setText(tv_end_time.getText() + orderDetail.getOrderxq().getBackrq());
                tv_person_count.setText(tv_person_count.getText() + orderDetail.getOrderxq().getCrnum() + "成人" + orderDetail.getOrderxq().getEtnum() + "儿童");
                tv_house_count.setText(tv_house_count.getText() + orderDetail.getOrderxq().getFangjian());
                tv_order_state.setText(tv_order_state.getText() + orderDetail.getOrderxq().getStatus());

                tv_cy_money.setText(tv_cy_money.getText() + "￥" + orderDetail.getFeiyong().getCytf());
                tv_bx_money.setText(tv_bx_money.getText() + "￥0");
                String amount = orderDetail.getFeiyong().getOrder_amount();
                String payMoney = orderDetail.getFeiyong().getMoney_paid();
                tv_order_total_money.setText(tv_order_total_money.getText() + "￥" + amount);

                String money_paid = (Double.parseDouble(amount) - Double.parseDouble(payMoney))+"";
                if(money_paid.endsWith(".0")){
                    money_paid=money_paid.substring(0,money_paid.indexOf("."));
                }

                tv_order_total_money1.setText(tv_order_total_money1.getText() + "￥" + money_paid);
                tv_linkman_name.setText(tv_linkman_name.getText() + orderDetail.getLxr().getName());
                tv_linkman_tel.setText(tv_linkman_tel.getText() + orderDetail.getLxr().getMobile());
                tv_linkman_email.setText(tv_linkman_email.getText() + orderDetail.getLxr().getEmail());

                lv_order.setAdapter(new TouristAdapter(orderDetail.getYouke()));
                ListViewUtil.setListViewHeightBasedOnChildren(lv_order);

                String neesBill = orderDetail.getFp().getNeed_inv();
                if ("1".equals(neesBill)) {
                    tv_bill_datail.setText(tv_bill_datail.getText() + orderDetail.getFp().getInv_content());
                    tv_bill_address.setText(tv_bill_address.getText() + orderDetail.getFp().getInv_address());
                    tv_bill_name.setText(tv_bill_name.getText() + orderDetail.getFp().getInv_name());
                    tv_bill_tel.setText(tv_bill_tel.getText() + orderDetail.getFp().getInv_mobile());
                    tv_bill_title.setText(tv_bill_title.getText() + orderDetail.getFp().getInv_payee());
                    tv_bill.setText(tv_bill.getText() + "是");
                } else {
                    ll_bill.setVisibility(View.GONE);
                    tv_bill.setText(tv_bill.getText() + "否");
                }


            }
        }
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("id", goods_id);
        startActivity(intent);
    }

    class TouristAdapter extends BaseAdapter {
        private List<OrderDetail.tourist> youke;

        public TouristAdapter(List<OrderDetail.tourist> youke) {
            this.youke = youke;
        }

        @Override
        public int getCount() {
            return youke.size() - 1;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = View.inflate(OrderDetailActivity.this, R.layout.dfy_item_order, null);
            TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
            TextView tv_card = (TextView) v.findViewById(R.id.tv_card);
            TextView tv_tel = (TextView) v.findViewById(R.id.tv_tel);
            tv_name.setText(tv_name.getText() + youke.get(i + 1).getName());
            tv_card.setText(tv_card.getText() + youke.get(i + 1).getZjnum());
            tv_tel.setText(tv_tel.getText() + youke.get(i + 1).getMobile());
            return v;
        }
    }
}
