package com.ccpress.izijia.dfy.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.adapter.ChooseAdapter;
import com.ccpress.izijia.dfy.adapter.ChoosePagerAdapter;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.fragment.SearchFragment;
import com.ccpress.izijia.dfy.util.DensityUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.dfy.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmz1024 on 2016/3/20.
 */
public class SearchActivity extends FragmentActivity implements View.OnClickListener, PopupWindow.OnDismissListener {
    private SearchFragment searchFragment;
    private RelativeLayout rl_city, rl_days, rl_time, rl_xu;
    private ImageView xu, days, time, city;
    private ImageView iv_back;
    private TextView tv_search;
    private EditText et_search;
    private TextView tv_xu, tv_city, tv_days, tv_time, pop_tv_cancle, pop_tv_clear, pop_tv_ok, pop_tv_city, pop_tv_days, pop_tv_time;
    private TextView tv_1, tv_2, tv_3, tv_4, tv_5;
    private ImageView iv_1, iv_2, iv_3, iv_4, iv_5;
    private RelativeLayout rl_1, rl_2, rl_3, rl_4, rl_5;
    private NoScrollViewPager pop_vp;
    private PopupWindow popupWindow;
    private PopupWindow popXu;
    private View xuView;
    private View contentView;
    private Map<String, String> mapCity = new HashMap<String, String>();
    private Map<Integer, String> cityMap = new HashMap<Integer, String>();
    private Map<Integer, String> daysMap = new HashMap<Integer, String>();
    private Map<Integer, String> timeMap = new HashMap<Integer, String>();
    private String keywords="";
    private LinearLayout ll_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dfy_activity_search);
        searchFragment = new SearchFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.center_frame, searchFragment);
        transaction.show(searchFragment);
        transaction.commit();
        initView();
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    @SuppressLint("WrongViewCast")
    private void initView() {
        rl_city = (RelativeLayout) findViewById(R.id.rl_city);
        rl_days = (RelativeLayout) findViewById(R.id.rl_days);
        rl_time = (RelativeLayout) findViewById(R.id.rl_time);
        rl_xu = (RelativeLayout) findViewById(R.id.rl_xu);
        ll_bottom= (LinearLayout) findViewById(R.id.bottom);
        xu = (ImageView) findViewById(R.id.xu);
        days = (ImageView) findViewById(R.id.days);
        time = (ImageView) findViewById(R.id.time);
        city = (ImageView) findViewById(R.id.city);
        tv_xu = (TextView) findViewById(R.id.tv_xu);
        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_days = (TextView) findViewById(R.id.tv_days);
        tv_time = (TextView) findViewById(R.id.tv_time);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        et_search = (EditText) findViewById(R.id.et_search);
        rl_city.setOnClickListener(this);
        rl_days.setOnClickListener(this);
        rl_time.setOnClickListener(this);
        rl_xu.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        et_search.setFocusable(true);
        et_search.setFocusableInTouchMode(true);
        et_search.requestFocus();
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    Map<String, Object> map = searchFragment.getMap();
                    map.clear();
                    cityMap.clear();
                    daysMap.clear();
                    timeMap.clear();
                    keywords=et_search.getText().toString();
                    map.put("keywords", keywords);
                    searchFragment.setMap(map);
                    searchFragment.initData();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
                    }

                    iv_back.setFocusable(true);
                    iv_back.setFocusableInTouchMode(true);
                    iv_back.requestFocus();
                    iv_back.requestFocusFromTouch();
                    ll_bottom.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });

        et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    //编辑框得到焦点事件
                    ll_bottom.setVisibility(View.GONE);
                }
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_city:
                showPopu(0);
                break;
            case R.id.rl_days:
                showPopu(1);
                break;
            case R.id.rl_time:
                showPopu(2);
                break;
            case R.id.rl_xu:
                showPopXu();
                break;
            case R.id.tv_cancle:
                popupWindow.dismiss();
                break;
            case R.id.tv_clear:
                tvClear();
                break;
            case R.id.tv_ok:
                tvOk();
                break;
            case R.id.pop_tv_city:
                chooseType(0);
                break;
            case R.id.pop_tv_days:
                chooseType(1);
                break;
            case R.id.pop_tv_time:
                chooseType(2);
                break;
            case R.id.rl_1:
                rl(1, tv_1, iv_1);
                break;
            case R.id.rl_2:
                rl(2, tv_2, iv_2);
                break;
            case R.id.rl_3:
                rl(3, tv_3, iv_3);
                break;
            case R.id.rl_4:
                rl(4, tv_4, iv_4);
                break;
            case R.id.rl_5:
                rl(5, tv_5, iv_5);
                break;
        }
    }

    private void rl(int index, TextView tv, ImageView iv) {
        ColorStateList dfy_333 = Util.getColor(R.color.dfy_333);
        ColorStateList dfy_50bbdb = Util.getColor(R.color.dfy_50bbdb);
        tv_1.setTextColor(dfy_333);
        tv_2.setTextColor(dfy_333);
        tv_3.setTextColor(dfy_333);
        tv_4.setTextColor(dfy_333);
        tv_5.setTextColor(dfy_333);
        iv_1.setVisibility(View.GONE);
        iv_2.setVisibility(View.GONE);
        iv_3.setVisibility(View.GONE);
        iv_4.setVisibility(View.GONE);
        iv_5.setVisibility(View.GONE);
        tv.setTextColor(dfy_50bbdb);
        iv.setVisibility(View.VISIBLE);
        Map<String, Object> map = searchFragment.getMap();
        map.clear();
        switch (index) {
            case 1:
                map.put("sort", "attr_value");
                map.put("order", "ASC");
                break;
            case 2:
                map.put("sort", "sales_count");
                map.put("order", "ASC");
                break;
            case 3:
                map.put("sort", "click_count");
                map.put("order", "ASC");
                break;
            case 4:
                map.put("sort", "shop_price");
                map.put("order", "DESC");
                break;
            case 5:
                map.put("sort", "shop_price");
                map.put("order", "ASC");
                break;
        }
        map.put("keywords",et_search.getText().toString());
        cityMap.clear();
        popXu.dismiss();
        searchFragment.setMap(map);
        searchFragment.initData();

    }

    /**
     * pop中的清空已选
     */
    private void tvClear() {
        cityMap.clear();
        timeMap.clear();
        daysMap.clear();
        popupWindow.dismiss();
    }

    /**
     * pop中的确定
     */
    private void tvOk() {
        Map<String, Object> map = searchFragment.getMap();
        map.clear();
        if (!cityMap.containsKey(0)) {
            for (int i : cityMap.keySet()) {
                String[] city = cityMap.get(i).split("=");
                Log.d("city", city[0] + "--" + city[1]);
                map.put(city[0], city[1]);
            }
        }

        if (!daysMap.containsKey(0)) {
            for (int i : daysMap.keySet()) {
                if (daysMap.get(i).endsWith("+")) {
                    map.put("sxc", "15");
                } else {
                    String[] days = daysMap.get(i).split("-");
                    map.put("sxc", days[0]);
                    map.put("exc", days[1]);
                }

            }
        }

        if (!timeMap.containsKey(0)) {
            for (int i : timeMap.keySet()) {
                String[] time = timeMap.get(i).split("=");
                map.put(time[0], time[1]);
            }
        }


        cityMap.clear();
        popupWindow.dismiss();
        map.put("keywords",keywords);
        searchFragment.setMap(map);
        searchFragment.initData();
    }

    private void showPopXu() {
        backgroundAlpha(0.5f);
        if (xuView == null) {
            xuView = View.inflate(this, R.layout.dfy_pop_xu, null);
            popXu = new PopupWindow(xuView,
                    FrameLayout.LayoutParams.MATCH_PARENT, DensityUtil.getScreenHeight() / 3, true);
            popXu.setOnDismissListener(this);
            // 实例化一个ColorDrawable颜色为半透明
//            ColorDrawable dw = new ColorDrawable(0xb0000000);
//            popXu.setBackgroundDrawable(dw);
            // 设置popWindow的显示和消失动画
            popXu.setAnimationStyle(R.style.mypopwindow_anim_style);
            tv_1 = (TextView) xuView.findViewById(R.id.tv_1);
            iv_1 = (ImageView) xuView.findViewById(R.id.iv_1);
            tv_2 = (TextView) xuView.findViewById(R.id.tv_2);
            iv_2 = (ImageView) xuView.findViewById(R.id.iv_2);
            tv_3 = (TextView) xuView.findViewById(R.id.tv_3);
            iv_3 = (ImageView) xuView.findViewById(R.id.iv_3);
            tv_4 = (TextView) xuView.findViewById(R.id.tv_4);
            iv_4 = (ImageView) xuView.findViewById(R.id.iv_4);
            tv_5 = (TextView) xuView.findViewById(R.id.tv_5);
            iv_5 = (ImageView) xuView.findViewById(R.id.iv_5);
            rl_1 = (RelativeLayout) xuView.findViewById(R.id.rl_1);
            rl_2 = (RelativeLayout) xuView.findViewById(R.id.rl_2);
            rl_3 = (RelativeLayout) xuView.findViewById(R.id.rl_3);
            rl_4 = (RelativeLayout) xuView.findViewById(R.id.rl_4);
            rl_5 = (RelativeLayout) xuView.findViewById(R.id.rl_5);
            rl_1.setOnClickListener(this);
            rl_2.setOnClickListener(this);
            rl_3.setOnClickListener(this);
            rl_4.setOnClickListener(this);
            rl_5.setOnClickListener(this);
        }
        popXu.showAtLocation(rl_xu, Gravity.BOTTOM, 0, 0);
    }

    private void chooseType(int index) {
        updateText(index);
        pop_vp.setCurrentItem(index, false);//false:关闭切换动画
    }


    private void showPopu(int index) {
        initPopView(index);
    }

    /**
     * 给弹出的pop中viewPager填充数据和布局
     */
    private void initAdapter() {
        List<String[]> list_city = new ArrayList<String[]>();
        List<String[]> list_days = new ArrayList<String[]>();
        List<String[]> list_time = new ArrayList<String[]>();
        mapCity = searchFragment.getCitys();
        list_city.add(new String[]{getString(R.string.azonic), ""});
        if (cityMap != null) {

            for (String cityName : mapCity.keySet()) {
                list_city.add(new String[]{cityName, "province=" + mapCity.get(cityName)});

            }
        }

        //给行程天数添加数据
        for (int i = 0; i < Constant.DAYS.length; i++) {
            list_days.add(new String[]{Constant.DAYS[i], Constant.DAYS[i]});
        }


        //给出发时间添加数据
        for (int i = 0; i < Constant.JIERI.length; i++) {
            if (i == 0) {
                list_time.add(new String[]{Constant.JIERI[i], ""});
            } else {
                if (i <= 7) {
                    list_time.add(new String[]{Constant.JIERI[i], "jieri=" + i});
                } else {
                    int month = i - 7;
                    if (month < 10) {
                        list_time.add(new String[]{Constant.JIERI[i], "month=0" + month});
                    } else {
                        list_time.add(new String[]{Constant.JIERI[i], "month=" + month});
                    }
                }
            }
        }

        List<ListView> list = new ArrayList<ListView>();

        ListView lvCity = new ListView(this);
        lvCity.setAdapter(new ChooseAdapter(list_city, cityMap));

        ListView lvDays = new ListView(this);
        lvDays.setAdapter(new ChooseAdapter(list_days, daysMap));

        ListView lvTime = new ListView(this);
        lvTime.setAdapter(new ChooseAdapter(list_time, timeMap));

        list.add(lvCity);
        list.add(lvDays);
        list.add(lvTime);
        pop_vp.setAdapter(new ChoosePagerAdapter(list));
    }

    /**
     * 初始化pop
     */
    private void initPopView(int index) {
        // 一个自定义的布局，作为显示的内容

        contentView = View.inflate(this,
                R.layout.dfy_search_pop_window, null);
        // 设置按钮的点击事件
        pop_tv_cancle = (TextView) contentView.findViewById(R.id.tv_cancle);
        pop_tv_clear = (TextView) contentView.findViewById(R.id.tv_clear);
        pop_tv_ok = (TextView) contentView.findViewById(R.id.tv_ok);

        pop_tv_city = (TextView) contentView.findViewById(R.id.pop_tv_city);
        pop_tv_days = (TextView) contentView.findViewById(R.id.pop_tv_days);
        pop_tv_time = (TextView) contentView.findViewById(R.id.pop_tv_time);

        pop_vp = (NoScrollViewPager) contentView.findViewById(R.id.pop_vp);
        pop_vp.setOffscreenPageLimit(3);
        initAdapter();
        pop_tv_city.setOnClickListener(this);
        pop_tv_days.setOnClickListener(this);
        pop_tv_time.setOnClickListener(this);
        pop_tv_cancle.setOnClickListener(this);
        pop_tv_clear.setOnClickListener(this);
        pop_tv_ok.setOnClickListener(this);
        popupWindow = new PopupWindow(contentView,
                FrameLayout.LayoutParams.MATCH_PARENT, (DensityUtil.getScreenHeight() / 5) * 2, true);
        backgroundAlpha(0.5f);
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupWindow.setOnDismissListener(this);

        popupWindow.showAtLocation(rl_city, Gravity.BOTTOM, 0, 0);
        chooseType(index);
    }

    /**
     * 改变点击文字的颜色和背景(pop中的三个选项卡)
     *
     * @param index
     */
    private void updateText(int index) {
        ColorStateList csl_333 = Util.getColor(R.color.dfy_333);
        ColorStateList csl_50ddbd = Util.getColor(R.color.dfy_50bbdb);
        pop_tv_city.setBackgroundResource(R.color.dfy_ebebeb);
        pop_tv_days.setBackgroundResource(R.color.dfy_ebebeb);
        pop_tv_time.setBackgroundResource(R.color.dfy_ebebeb);

        pop_tv_city.setTextColor(csl_333);
        pop_tv_days.setTextColor(csl_333);
        pop_tv_time.setTextColor(csl_333);

        switch (index) {
            case 0:
                pop_tv_city.setTextColor(csl_50ddbd);
                pop_tv_city.setBackgroundResource(R.color.dfy_fff);
                break;
            case 1:
                pop_tv_days.setTextColor(csl_50ddbd);
                pop_tv_days.setBackgroundResource(R.color.dfy_fff);
                break;
            case 2:
                pop_tv_time.setTextColor(csl_50ddbd);
                pop_tv_time.setBackgroundResource(R.color.dfy_fff);
                break;
        }
    }

    @Override
    public void onDismiss() {
        backgroundAlpha(1f);
    }
}
