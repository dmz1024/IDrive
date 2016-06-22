package com.ccpress.izijia.yd.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.yd.entity.BaoXian;
import com.ccpress.izijia.yd.view.MyAlert;
import com.ccpress.izijia.yd.view.MyAlertDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/29.
 */
public abstract class WriteOderBaoxianAdapter extends MyBaseAdapter<BaoXian.Data> {
    public Map<Integer, String> map = new HashMap<>();

    public WriteOderBaoxianAdapter(Context ctx, List<BaoXian.Data> list) {
        super(ctx, list);
    }

    public WriteOderBaoxianAdapter(Context ctx, List<BaoXian.Data> list, ListView lv) {
        super(ctx, list, lv);
    }

    @Override
    public View FullView(final int i, View view) {
        if (view == null) {
            view = View.inflate(ctx, R.layout.yd_item_baoxian, null);
        }
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_chengren_price = (TextView) view.findViewById(R.id.tv_chengren_price);
        TextView tv_ertong_price = (TextView) view.findViewById(R.id.tv_ertong_price);
        TextView tv_desc = (TextView) view.findViewById(R.id.tv_desc);

        final ImageView iv_select = (ImageView) view.findViewById(R.id.iv_select);
        final BaoXian.Data data = list.get(i);
        tv_name.setText(data.pack_name);
        tv_chengren_price.setText(data.crfee + "");
        tv_ertong_price.setText(data.etfee + "");
        map.put(i, data.pack_id);
        tv_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyAlert(ctx, "详情").showContent(data.pack_desc, "关闭");
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map.containsKey(i)) {
                    iv_select.setImageResource(R.drawable.yd_cam_check);
                    map.remove(i);
                } else {
                    iv_select.setImageResource(R.drawable.yd_cam_checked);
                    map.put(i, data.pack_id);
                }
                select(map);
            }
        });

        return view;
    }

    public abstract void select(Map<Integer, String> map);
}
