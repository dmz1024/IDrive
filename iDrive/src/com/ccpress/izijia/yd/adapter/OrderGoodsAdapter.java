package com.ccpress.izijia.yd.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.yd.entity.YdOrder;

import java.util.List;

/**
 * Created by dengmingzhi on 16/5/30.
 */
public class OrderGoodsAdapter extends MyBaseAdapter<YdOrder.Goods> {
    public OrderGoodsAdapter(Context ctx, List<YdOrder.Goods> list) {
        super(ctx, list);
    }

    @Override
    public View FullView(int i, View view) {
        View v = View.inflate(ctx, R.layout.yd_item_order_goods, null);
        TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
        TextView tv_x = (TextView) v.findViewById(R.id.tv_x);
        TextView tv_price = (TextView) v.findViewById(R.id.tv_price);
        YdOrder.Goods goods = list.get(i);
        tv_name.setText(goods.goods_name);
        tv_x.setText("X " + goods.goods_number);
        tv_price.setText("￥ " + goods.amount_price);
        return v;
    }
}
