package com.ccpress.izijia.dfy.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.util.Util;

import java.util.List;
import java.util.Map;

/**
 * Created by administror on 2016/3/21 0021.
 */
public class ChooseAdapter extends BaseAdapter {
    protected List<String[]> list;
    protected Map<Integer, String> map;

    public ChooseAdapter(List<String[]> list, Map<Integer, String> map) {
        this.list = list;

        this.map = map;
        if (this.map.size() == 0) {
            this.map.put(0, "");
        }
    }

    public Map<Integer, String> getMap() {
        return map;
    }

    protected HolderView holderView;

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String[] getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final HolderView holderView;
        if (view == null) {
            view = View.inflate(Util.getMyApplication(), R.layout.dfy_item_choose, null);
            holderView = new HolderView();
            holderView.textView = (TextView) view.findViewById(R.id.tv_choose);
            holderView.imageView = (ImageView) view.findViewById(R.id.iv_choose);
            view.setTag(holderView);
        } else {
            holderView = (HolderView) view.getTag();
        }

        if (map.size() == 0) {
            if (i == 0) {
                holderView.imageView.setImageResource(R.drawable.dfy_sort_choose_01);
            } else {
                holderView.imageView.setImageResource(R.drawable.dfy_sort_choose);
            }
        } else {
            if (map.containsKey(i)) {
                holderView.imageView.setImageResource(R.drawable.dfy_sort_choose_01);
            } else {
                holderView.imageView.setImageResource(R.drawable.dfy_sort_choose);
            }
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView lv = ((ListView) v.getParent());
                for (int j = 0; j < lv.getChildCount(); j++) {
                    ((ImageView) lv.getChildAt(j).findViewById(R.id.iv_choose)).setImageResource(R.drawable.dfy_sort_choose);
                }
                ((ImageView) v.findViewById(R.id.iv_choose)).setImageResource(R.drawable.dfy_sort_choose_01);
                map.clear();
                map.put(i, list.get(i)[1]);
            }
        });

        holderView.textView.setText(list.get(i)[0]);
        return view;
    }

    class HolderView {
        TextView textView;
        ImageView imageView;
    }
}
