package com.ccpress.izijia.yd.adapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ccpress.izijia.yd.api.MyImageLoader;
import com.ccpress.izijia.yd.util.Utility;
import com.ccpress.izijia.yd.view.MaxListView;

import java.util.List;

/**
 * Created by dengmingzhi on 16/5/27.
 */
public abstract class ChooseBaseAdapter<T> extends BaseAdapter {
    protected List<T> list;
    protected Context ctx;
    protected MaxListView lv;
    protected MyImageLoader loader;

    public ChooseBaseAdapter(Context ctx, List<T> list, MaxListView lv) {
        this(ctx, list);
        this.lv = lv;
        loader=new MyImageLoader(5);
    }

    public ChooseBaseAdapter(Context ctx, List<T> list) {
        this.list = list;
        this.ctx = ctx;
    }

    public ChooseBaseAdapter(List<T> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
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
    protected void notifi(){
        notifyDataSetChanged();
        Utility.setListViewHeightBasedOnChildren(lv);
    }
}
