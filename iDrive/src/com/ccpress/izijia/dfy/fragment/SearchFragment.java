package com.ccpress.izijia.dfy.fragment;

import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.view.View;
import com.ccpress.izijia.dfy.activity.DetailsActivity;
import com.ccpress.izijia.dfy.adapter.SearchAdapter;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.entity.City;
import com.ccpress.izijia.dfy.entity.SearchIdrive;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.dfy.view.RefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmz1024 on 2016/3/20.
 */

public class SearchFragment extends FragmentBase<SearchIdrive, SearchAdapter> {

    private List<City> listCity = new ArrayList<City>();

    private Map<String, Object> map = new HashMap<String, Object>();

    private boolean isFirst = true;

    @Override
    protected Map<String, Object> post() {
        map.put("page", page);
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public void initData() {
        listCity.clear();
        page = 1;
        isClear = true;
        super.initData(true);
    }

    @Override
    protected void initData(boolean isFresh) {
        if (isFirst) {
            isFirst = false;
            rl_load.setVisibility(View.GONE);
        } else {
            super.initData(isFresh);
        }
    }

    public Map<String, Object> getMap() {
        return map;
    }

    @Override
    protected void addList(String json) {
        super.addList(json);
        listCity.addAll(JsonUtil.json2List(json, City.class, "province"));
    }


    @Override
    protected String getUrl() {
        return Constant.DFY_SEARCH;
    }

    @Override
    protected SearchAdapter getAdapter(List list) {
        return new SearchAdapter(list);
    }

    @Override
    protected RefreshListView.Mode getMode() {
        return RefreshListView.Mode.BOTH;
    }

    @Override
    protected void onItemClick(List<SearchIdrive> list, int i) {

        if(i<1 || i>mlist.size()){
            return;
        }
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("id", list.get(i-1).getGoods_id());
        startActivity(intent);
    }

    @Override
    protected List<SearchIdrive> getList(String json) {
        return JsonUtil.json2List(json, SearchIdrive.class, "data");
    }

    @Override
    protected boolean getSubmitType() {
        return true;
    }

    @Override
    protected void saveJson(String json) {

    }

    public Map<String, String> getCitys() {
        Map<String, String> cityMap = new ArrayMap<String, String>();
        if (listCity == null) {
            return null;
        }
        for (int i = 0; i < listCity.size(); i++) {
            City city = listCity.get(i);
            if (!cityMap.containsKey(city.getRegion_name())) {
                cityMap.put(city.getRegion_name(), city.getRegion_id());
            }
        }
        return cityMap;
    }

}


