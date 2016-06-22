package com.ccpress.izijia.yd.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.ccpress.izijia.R;

import java.util.List;

/**
 * Created by dengmingzhi on 16/5/28.
 */
public class ChooseSeatAdapter extends ChooseBaseAdapter<String> {


    public ChooseSeatAdapter(Context ctx, List<String> list) {
        super(ctx, list);
    }

    @Override
    public View FullView(int i, View view) {
        TextView textView=new TextView(ctx);
        textView.setText(list.get(i));
        textView.setTextColor(ctx.getResources().getColor(R.color.dfy_50bbdb));
        textView.setTextSize(10);
        return textView;
    }
}
