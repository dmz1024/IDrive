package com.ccpress.izijia.yd.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.yd.entity.Desc;

import java.util.List;

/**
 * Created by dengmingzhi on 16/5/30.
 */
public class WriteOrderDescAdapter extends MyBaseAdapter<Desc> {
    private boolean type = false;

    public WriteOrderDescAdapter(Context ctx, List<Desc> list) {
        super(ctx, list);
    }

    public WriteOrderDescAdapter(Context ctx, List<Desc> list, boolean type) {
        this(ctx, list);
        this.type = type;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public View FullView(int i, View view) {
        View v;
        if (!type) {
            v = View.inflate(ctx, R.layout.item_write_order_desc, null);
        } else {
            v = View.inflate(ctx, R.layout.yd_item_pay_desc, null);
        }

        TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
        TextView tv_count = (TextView) v.findViewById(R.id.tv_count);
        TextView tv_price = (TextView) v.findViewById(R.id.tv_price);
        Desc desc = list.get(i);
        tv_name.setText(desc.name);
        tv_count.setText("X " + desc.count);
        tv_price.setText("￥ " + desc.price);
        return v;
    }
}
