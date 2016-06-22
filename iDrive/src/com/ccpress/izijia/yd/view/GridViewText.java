package com.ccpress.izijia.yd.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.yd.entity.Option;
import com.ccpress.izijia.yd.entity.options;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/8.
 */
public class GridViewText extends LinearLayout {
    private TextView tv_title;
    private MyAdapter adapter;
    private List<options> list;
    //    private String[] selects;
    private GridView gv_content;
    //    private Map<Integer, String> map = new HashMap<>();
    public String select = "";

    public GridViewText(Context context) {
        this(context, null);
    }

    public GridViewText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridViewText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.yd_grid_text, this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        gv_content = (GridView) findViewById(R.id.gv_content);
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setTitle(int rid) {
        tv_title.setText(rid);
    }

//    public void setDatas(String[] list, String[] selects) {
//        this.list = list;
//        this.selects = selects;
//        gv_content.setAdapter(adapter = new MyAdapter());
//    }

    public void setDatas(List<options> list, String title) {
//        options option = new options();
//        option.brand_id = "";
//        option.brand_name = "全部";
//        list.add(0, option);
        this.list = list;
        tv_title.setText(title);
        gv_content.setAdapter(adapter = new MyAdapter());
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View v = View.inflate(getContext(), R.layout.yd_grid_text_item, null);
            TextView tv_content = (TextView) v.findViewById(R.id.tv_content);
            final ImageView iv_select = (ImageView) v.findViewById(R.id.iv_select);
            final options option = list.get(i);
            tv_content.setText(option.brand_name);
            if (i == 0) {
                iv_select.setVisibility(VISIBLE);
                select = option.brand_id;
            }
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int count = gv_content.getChildCount();
                    for (int j = 0; j < count; j++) {
                        ImageView iv_select = (ImageView) gv_content.getChildAt(j).findViewById(R.id.iv_select);
                        iv_select.setVisibility(INVISIBLE);
                    }
                    iv_select.setVisibility(VISIBLE);
                    select = option.brand_id;
                }
            });

            return v;
        }
    }
}
