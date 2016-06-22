package com.ccpress.izijia.yd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.yd.adapter.ChooseTcAdapter;
import com.ccpress.izijia.yd.adapter.ChooseXxAdapter;
import com.ccpress.izijia.yd.adapter.ChooseZsAndYwAdapter;
import com.ccpress.izijia.yd.api.HttpApi;
import com.ccpress.izijia.yd.constant.ConstantApi;
import com.ccpress.izijia.yd.entity.ChooseStores;
import com.ccpress.izijia.yd.entity.SerializableMap;
import com.ccpress.izijia.yd.view.Cust_Rl_Calen;
import com.ccpress.izijia.yd.view.MaxListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/26.
 */
public class ChooseStoreActivity extends BaseActivity {
    private FrameLayout fg_calen;
    private Intent intent;
    private String id;
    private String title;
    private MaxListView lv_tc;
    private MaxListView lv_zs;
    private MaxListView lv_yw;
    private MaxListView lv_xx;
    private ChooseTcAdapter tcAdapter;
    private ChooseXxAdapter xxAdapter;
    private ChooseZsAndYwAdapter zsAdapter;
    private ChooseZsAndYwAdapter ywAdapter;
    private double totalMoney = 0;
    private TextView tv_total_money;
    private Button bt_order;
    private Cust_Rl_Calen crc;
    public static Map<String, int[]> macMap = new HashMap<>();
    public static List<MaxListView> lls = new ArrayList<>();
    public static boolean isSelect = false;
    private Map<String, Object[]> txMap = new HashMap<>();//用来存放套餐和休闲的数量和价格
    private Map<String, Object[]> zyMap = new HashMap<>();//用来存放营位和住宿信息的数量和价格

    @Override
    protected int getRid() {
        return R.layout.yd_activity_choosestore;
    }

    @Override
    protected void initTitleBar() {
        title_bar.setTvTitleText("选择商品");
    }


    @Override
    protected void initData() {
        isSelect = false;
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        http(map, "");
    }

    private void http(Map<String, String> map, final String msg) {
        HttpApi api = new HttpApi(ConstantApi.CART, map) {
            @Override
            protected void success(String json) {
                Log.d("json", json);
                ChooseStores cs = JsonUtil.getJavaBean(json, ChooseStores.class);
                int result = cs.result;
                if (result == 0) {
                    if (!TextUtils.equals("", msg)) {
                        isSelect = true;
                    }
                    fillData(cs);
                } else {
                    CustomToast.showToast("获取数据失败！");
                    isSelect = false;
                }
            }
        };
        if (TextUtils.isEmpty(msg)) {
            api.post();
        } else {
            api.post(this, msg);
        }
    }


    /**
     * 填充数据
     *
     * @param cs
     */
    private void fillData(ChooseStores cs) {
        macMap.clear();
        lls.clear();
        txMap.clear();
        zyMap.clear();
        totalMoney = 0;
        ChooseStores stores = cs;
        lv_tc.setAdapter(tcAdapter = new ChooseTcAdapter(this, stores.taocan, lv_tc) {

            @Override
            public void jia(String goods_id, String goods_name, int count, double price) {
                setTotalMoney(price);
                txMap.put(goods_id, new Object[]{goods_name, count, price});
            }

            @Override
            public void jian(String goods_id, String goods_name, int count, double price) {
                setTotalMoney(-price);
                if (count == 0) {
                    txMap.remove(goods_id);
                } else {
                    txMap.put(goods_id, new Object[]{goods_name, count, price});
                }

            }
        });

        lv_zs.setAdapter(zsAdapter = new ChooseZsAndYwAdapter(this, stores.zhusu, lv_zs, 1) {

            @Override
            public void jia(String goods_id, String goods_name, List<String> tags, double price) {
                setTotalMoney(price);
                zyMap.put(goods_id, new Object[]{goods_name, tags, price});
            }

            @Override
            public void jian(String goods_id, String goods_name, List<String> tags, double price) {
                setTotalMoney(-price);
                if (tags.size() == 0) {
                    zyMap.remove(goods_id);
                } else {
                    zyMap.put(goods_id, new Object[]{goods_name, tags, price});
                }
            }

            @Override
            public void update(String goods_id, String goods_name, List<String> tags, double price) {
                zyMap.put(goods_id, new Object[]{goods_name, tags, price});
            }

        });

        lv_yw.setAdapter(ywAdapter = new ChooseZsAndYwAdapter(this, stores.yingwei, lv_yw, 2) {

            @Override
            public void jia(String goods_id, String goods_name, List<String> tags, double price) {
                setTotalMoney(price);
                zyMap.put(goods_id, new Object[]{goods_name, tags, price});
            }

            @Override
            public void jian(String goods_id, String goods_name, List<String> tags, double price) {
                setTotalMoney(-price);
                if (tags.size() == 0) {
                    zyMap.remove(goods_id);
                } else {
                    zyMap.put(goods_id, new Object[]{goods_name, tags, price});
                }
            }

            @Override
            public void update(String goods_id, String goods_name, List<String> tags, double price) {
                zyMap.put(goods_id, new Object[]{goods_name, tags, price});
            }
        });
        lv_xx.setAdapter(xxAdapter = new ChooseXxAdapter(this, stores.tese, lv_xx) {

            @Override
            public void jia(String goods_id, String goods_name, int count, double price) {
                setTotalMoney(price);
                txMap.put(goods_id, new Object[]{goods_name, count, price});
            }

            @Override
            public void jian(String goods_id, String goods_name, int count, double price) {
                setTotalMoney(-price);
                if (count == 0) {
                    txMap.remove(goods_id);
                } else {
                    txMap.put(goods_id, new Object[]{goods_name, count, price});
                }
            }
        });

        lls.add(lv_tc);
        lls.add(lv_zs);
        lls.add(lv_yw);

    }

    @Override
    protected void initView() {
        fg_calen = getView(R.id.fg_calen);
        lv_tc = getView(R.id.lv_tc);
        lv_zs = getView(R.id.lv_zs);
        lv_yw = getView(R.id.lv_yw);
        lv_xx = getView(R.id.lv_xx);
        tv_total_money = getView(R.id.tv_total_money);
        bt_order = getView(R.id.bt_order);
        bt_order.setOnClickListener(this);
        intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        fg_calen.addView(crc = new Cust_Rl_Calen(this) {
            @Override
            public void filter(String time1, String time2) {
                Map<String, String> map = new HashMap<>();
                map.put("id", id);
                map.put("addtime", time1);
                map.put("endtime", time2);
                http(map, "正在筛选...");
            }
        });
    }

    //显示订单总金额
    private void setTotalMoney(double price) {
        tv_total_money.setText((totalMoney = totalMoney + price) + "");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_order:
                order();
                break;
        }
    }

    /**
     * 立即预订
     */
    private void order() {
        String[] times = crc.getTvTime();
        if (txMap.size() == 0 && zyMap.size() == 0) {
            CustomToast.showToast("请选择商品");
            return;
        }
        SerializableMap txMyMap = new SerializableMap();
        txMyMap.setMap(txMap);
        SerializableMap yzMyMap = new SerializableMap();
        yzMyMap.setMap(zyMap);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("title", title);
        bundle.putDouble("money", totalMoney);
        bundle.putStringArray("times", times);
        bundle.putSerializable("txMyMap", txMyMap);
        bundle.putSerializable("yzMyMap", yzMyMap);
        Intent i = new Intent(this, WriteOrderActivity.class);
        i.putExtra("bundle", bundle);
        startActivity(i);
    }
}
