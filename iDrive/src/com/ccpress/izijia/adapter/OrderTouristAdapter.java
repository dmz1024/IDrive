package com.ccpress.izijia.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.yd.adapter.MyBaseAdapter;
import com.ccpress.izijia.yd.entity.YdOrderInfo;

import java.util.List;

/**
 * Created by dengmingzhi on 16/6/1.
 */
public class OrderTouristAdapter extends MyBaseAdapter<YdOrderInfo.Youke> {
    public OrderTouristAdapter(Context ctx, List<YdOrderInfo.Youke> list) {
        super(ctx, list);
    }

    public OrderTouristAdapter(Context ctx, List<YdOrderInfo.Youke> list, ListView lv) {
        super(ctx, list, lv);
    }

    @Override
    public View FullView(int i, View view) {
        TextView textView = (TextView) View.inflate(ctx, R.layout.yd_item_order_tourist, null);
        YdOrderInfo.Youke youke = list.get(i);
        textView.setText(new StringBuffer().append("姓名：").append(youke.name).append("\n\n证件号：").append(youke.zjnum)
                        .append("\n\n人群：").append(youke.renqun)
        );
        return textView;
    }
}
