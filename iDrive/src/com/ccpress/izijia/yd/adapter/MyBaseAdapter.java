package com.ccpress.izijia.yd.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.ccpress.izijia.yd.util.Utility;

import java.util.List;

/**
 * Created by dengmingzhi on 16/5/29.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {
    protected List<T> list;
    protected Context ctx;
    protected ListView lv;

    public MyBaseAdapter(Context ctx, List<T> list) {
        this.list = list;
        this.ctx = ctx;

    }

    public MyBaseAdapter(Context ctx, List<T> list, ListView lv) {
        this(ctx, list);
        this.lv = lv;
    }

    @Override
    public int getCount() {
        return list.size();
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
        return FullView(i, view);
    }

    public abstract View FullView(int i, View view);

    public void notifi() {
        if (lv != null) {
            notifyDataSetChanged();
            Utility.setListViewHeightBasedOnChildren(lv);
        }
    }
}
