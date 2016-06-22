package com.ccpress.izijia.dfy.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

/**
 * Created by administror on 2016/3/21 0021.
 */
public class ChoosePagerAdapter extends PagerAdapter {
    private List<ListView> list;

    public ChoosePagerAdapter(List<ListView> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == (ListView) o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        container.addView(list.get(position));
        return list.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(list.get(position));
    }
}
