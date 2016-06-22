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
import com.ccpress.izijia.dfy.util.CustomToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/8.
 */
public class GridViewText1 extends LinearLayout {
    private TextView tv_title;
    private MyAdapter adapter;
    private String[] list;
    private String[] selects;
    private GridView gv_content;
    private Map<Integer, String> map = new HashMap<>();

    public GridViewText1(Context context) {
        this(context, null);
    }

    public GridViewText1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridViewText1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.item_grid_text, this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        gv_content = (GridView) findViewById(R.id.gv_content);
    }

    public Map<Integer, String> getMap() {
        return map;
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setTitle(int rid) {
        tv_title.setText(rid);
    }

    public void setDatas(String[] list, String[] selects) {
        this.list = list;
        this.selects = selects;
        gv_content.setAdapter(adapter = new MyAdapter());
    }

    public List<String> getList() {
        List<String> listStr = new ArrayList<>();
        int[] arr = new int[map.size()];
        int i = 0;
        for (Integer key : map.keySet()) {
            arr[i] = key;
            i++;
        }
        Arrays.sort(arr);
        for (int j = 0; j < arr.length; j++) {
            listStr.add(map.get(arr[j]));
        }
        return listStr;
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.length;
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
            View v = View.inflate(getContext(), R.layout.item_grid_text_item, null);
            TextView tv_content = (TextView) v.findViewById(R.id.tv_content);
            final ImageView iv_select = (ImageView) v.findViewById(R.id.iv_select);
            tv_content.setText(list[i]);
            String value = list[i];

            if (map.containsKey(i)) {
                iv_select.setVisibility(VISIBLE);
            } else {
                iv_select.setVisibility(INVISIBLE);
            }

            for (int j = 0; selects != null && j < selects.length; j++) {
                if (TextUtils.equals(value, selects[j])) {
                    map.put(i, list[i]);
                    iv_select.setVisibility(VISIBLE);
                    break;
                }
            }

            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (map.containsKey(i)) {
                        map.remove(i);
                        iv_select.setVisibility(INVISIBLE);
                    } else {
                        if (map.size() + 1 > selects.length) {
                            CustomToast.showToast("只能选择" + selects.length + "个位置");
                            return;
                        }
                        map.put(i, list[i]);
                        iv_select.setVisibility(VISIBLE);
                    }
                }
            });

            return v;
        }
    }
}
