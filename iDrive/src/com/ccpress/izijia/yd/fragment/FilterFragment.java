package com.ccpress.izijia.yd.fragment;

import android.content.Intent;
import android.os.Bundle;
import com.ccpress.izijia.dfy.activity.DetailsActivity;
import com.ccpress.izijia.dfy.adapter.IdriveAdapter;
import com.ccpress.izijia.dfy.entity.Idrive;
import com.ccpress.izijia.dfy.fragment.FragmentBase;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.dfy.view.RefreshListView;
import com.ccpress.izijia.yd.StoresAdapter;
import com.ccpress.izijia.yd.activity.StoresInfoActivity;
import com.ccpress.izijia.yd.constant.ConstantApi;
import com.ccpress.izijia.yd.entity.Stores;


import java.util.*;

/**
 * Created by dengmingzhi on 16/5/25.
 */
public abstract class FilterFragment extends FragmentBase<Stores.Data, StoresAdapter> {
    private Map<String, Object> map = new HashMap<String, Object>();

    public void setFilter(String key, String value, boolean sort) {
        map.clear();
        page = 1;
        isFresh = true;
        isClear = true;
        mlist.clear();
        notifyData();
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("id", getid());
        filterMap.put("page", page);
        filterMap.put(key, value);
        filterMap.put("sort_order", sort ? "ASC" : "DESC");
        map.putAll(filterMap);
        super.initData(true);
    }

    @Override
    protected void init() {

    }

    public void setFilter1(String x, String y, boolean sort) {
        map.clear();
        page = 1;
        isFresh = true;
        isClear = true;
        mlist.clear();
        notifyData();
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("id", getid());
        filterMap.put("page", page);
        filterMap.put("x", x);
        filterMap.put("y", y);
        filterMap.put("sort_order", sort ? "ASC" : "DESC");
        map.putAll(filterMap);
        super.initData(true);
    }

    public void setFilter2(Map<String, Object> mapid) {
        map.clear();
        page = 1;
        isFresh = true;
        isClear = true;
        mlist.clear();
        notifyData();
        mapid.put("id", getid());
        mapid.put("page", page);
        map.putAll(mapid);
        super.initData(true);
    }

    @Override
    protected RefreshListView.Mode getMode() {
        return RefreshListView.Mode.BOTH;
    }

    @Override
    protected Map<String, Object> post() {
        map.put("id", getid());
        map.put("page", page);
        return map;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCreated();//Activity创建成功调用该事件
    }

    public abstract void onCreated();


    @Override
    protected boolean getSubmitType() {
        return true;
    }

    @Override
    protected void onItemClick(List<Stores.Data> list, int i) {
        if (i == 0 || i >= list.size() + 1) {
            return;
        }
        Intent intent = new Intent(getActivity(), StoresInfoActivity.class);
        intent.putExtra("id", list.get(i - 1).supplier_id);
        intent.putExtra("title", list.get(i - 1).supplier_name);
        intent.putExtra("img",list.get(i-1).logo);
        startActivity(intent);
    }

    @Override
    protected List<Stores.Data> getList(String json) {
        return JsonUtil.json2List(json, Stores.Data.class, "data");
    }

    @Override
    protected String getUrl() {
        return ConstantApi.STORES;
    }

    @Override
    protected StoresAdapter getAdapter(List<Stores.Data> list) {
        return new StoresAdapter(list);
    }

    public abstract int getid();

}
