package com.ccpress.izijia.yd.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.yd.fragment.FilterFragment;
import com.ccpress.izijia.yd.fragment.YdFragment;
import com.ccpress.izijia.yd.view.MyAlertDialog;
import com.ccpress.izijia.yd.view.YdFilterView;
import com.ccpress.izijia.yd.view.popView.PopOptionView;
import com.trs.util.log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/25.
 */
public class YdFilterActivity extends BaseActivity implements YdFilterView.OnRefreshListener {
    private int id;
    private String title;
    private List<YdFilterView> list;
    private FilterFragment filterFragment;
    private int currentFilter;
    private boolean isdown = false;
    private LocationManager locationManager;
    private String locationProvider;

    @Override
    protected int getRid() {
        return R.layout.yd_activity_fillter;
    }

    @Override
    protected void initTitleBar() {
        title_bar.setTvTitleText(title);
    }

    @Override
    protected void initData() {
        id = getIntent().getIntExtra("id", 0);
        switch (id) {
            case 0:
                title = "全部营地";
                break;
            case 9:
                title = "国内营地";
                break;
            case 10:
                title = "海外营地";
                break;
        }


        filterFragment = new FilterFragment() {
            @Override
            public void onCreated() {
                currentFilter = 0;
                list.get(0).check();
            }

            @Override
            public int getid() {
                return id;
            }
        };

        getSupportFragmentManager().beginTransaction().add(R.id.fg_filter, filterFragment).commit();


    }

    @Override
    protected void initView() {
        YdFilterView yd_1 = (YdFilterView) findViewById(R.id.yd_1);
        YdFilterView yd_2 = (YdFilterView) findViewById(R.id.yd_2);
        YdFilterView yd_3 = (YdFilterView) findViewById(R.id.yd_3);
        YdFilterView yd_4 = (YdFilterView) findViewById(R.id.yd_4);
        list = new ArrayList<>();
        list.add(yd_1);
        list.add(yd_2);
        list.add(yd_3);
        list.add(yd_4);
        yd_1.setOtherView(list);
        yd_2.setOtherView(list);
        yd_3.setOtherView(list);
        yd_4.setOtherView(list);
        yd_1.setOnRefreshListener(this);
        yd_2.setOnRefreshListener(this);
        yd_3.setOnRefreshListener(this);
        yd_4.setOnRefreshListener(this);

    }

    @Override
    public void refresh(YdFilterView ydFilterView, boolean down) {
        this.isdown = down;
        switch (ydFilterView.getId()) {
            case R.id.yd_1:
                currentFilter = 0;
                filterFragment.setFilter("sort_by", "is_click", !down);
                break;
            case R.id.yd_2:
                currentFilter = 1;
                filterFragment.setFilter("sort_by", "shop_price", !down);
                break;
            case R.id.yd_3:
                currentFilter = 2;
                filterFragment.setFilter1("116.383285", "39.998876", !down);
                break;
            case R.id.yd_4:
                openFilterOption();
                break;
        }
    }

    /**
     * 筛选列表
     */
    private void openFilterOption() {
        new PopOptionView(this, list.get(3)) {
            @Override
            public void ok(List<String> ids) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < ids.size(); i++) {
                    map.put("info" + i, ids.get(i));
                }
                filterFragment.setFilter2(map);
            }
        }.setData();
    }


}
