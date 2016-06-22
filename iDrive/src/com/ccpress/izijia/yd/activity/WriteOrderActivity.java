package com.ccpress.izijia.yd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.portal.LoginActivity;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.UserVo;
import com.ccpress.izijia.yd.adapter.WriteOderBaoxianAdapter;
import com.ccpress.izijia.yd.adapter.WriteOrderDescAdapter;
import com.ccpress.izijia.yd.adapter.WriteOrderTouristAdapter;
import com.ccpress.izijia.yd.api.HttpApi;
import com.ccpress.izijia.yd.constant.ConstantApi;
import com.ccpress.izijia.yd.entity.*;
import com.ccpress.izijia.yd.view.CountTextView;
import com.ccpress.izijia.yd.view.MaxListView;
import com.ccpress.izijia.yd.view.OptionSelect;
import com.ccpress.izijia.yd.view.TextEditView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/29.
 */
public class WriteOrderActivity extends BaseActivity implements CountTextView.OnCountChangeListener {
    private Map<String, Object[]> txMap;
    private Map<String, Object[]> zyMap;
    private String[] times;
    private String title;
    private double totalMoney;
    private TextEditView tev_name;
    private TextEditView tev_tel;
    private OptionSelect os_sex;
    private CountTextView tv_count;
    private MaxListView lv_courist;
    private MaxListView lv_baoxian;
    private TextView tv_cancle;
    private TextView tv_fp;
    private TextView tv_total_money;
    private TextView tv_desc;
    private Button bt_submit;
    private MaxListView lv_desc;
    private ImageView iv_drop;
    private int tourist_count = 1;
    private String id;
    private WriteOrderTouristAdapter tourist_adapter;
    private List<Integer> tourisList = new ArrayList<>();
    private double money = 0;
    private List<BaoXian.Data> datas = new ArrayList<>();
    private Map<Integer, String> map = new HashMap<>();
    private boolean isFirst = true;
    private boolean isSelect = false;
    private List<Desc> list;

    @Override
    protected int getRid() {
        return R.layout.yd_activity_write_order;
    }

    @Override
    protected void initTitleBar() {
        title_bar.setTvTitleText("填写订单");

    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        id = bundle.getString("id");
        times = bundle.getStringArray("times");
        totalMoney = bundle.getDouble("money");
        title = bundle.getString("title");
        setMoney();
        SerializableMap txMyMap = (SerializableMap) bundle.getSerializable("txMyMap");
        SerializableMap yzMyMap = (SerializableMap) bundle.getSerializable("yzMyMap");
        txMap = txMyMap.getMap();
        zyMap = yzMyMap.getMap();
        baoxian();//获取保险信息
        setDescData();
    }

    private void setDescData() {
        list = new ArrayList<>();
        for (Map.Entry<String, Object[]> entry : txMap.entrySet()) {
            Object[] obj = entry.getValue();
            Desc desc = new Desc();
            desc.name = (String) obj[0];
            desc.count = (int) obj[1];
            desc.price = (double) obj[2];
            list.add(desc);
        }
        StringBuffer sp;
        List<String> listTag;
        for (Map.Entry<String, Object[]> entry : zyMap.entrySet()) {
            Object[] obj = entry.getValue();
            sp = new StringBuffer();
            listTag = (List<String>) obj[1];
            Desc desc = new Desc();
            desc.name = (String) obj[0];
            desc.count = listTag.size();
            desc.price = (double) obj[2];
            list.add(desc);
        }
        lv_desc.setAdapter(new WriteOrderDescAdapter(this, list));
    }

    /**
     * 获取保险信息
     */
    private void baoxian() {
        final Map<String, String> map = new HashMap();
        map.put("id", id);
        new HttpApi(ConstantApi.BAOXIAN, map) {
            @Override
            protected void success(String json) {
                final BaoXian baoXian = JsonUtil.getJavaBean(json, BaoXian.class);
                int result = baoXian.result;
                if (result == 0) {
                    lv_baoxian.setAdapter(new WriteOderBaoxianAdapter(WriteOrderActivity.this, baoXian.data) {
                        @Override
                        public void select(Map<Integer, String> map) {
                            WriteOrderActivity.this.map = map;
                            isSelect = true;
                            computeMoney();
                        }
                    });
                    datas = baoXian.data;
                    computeMoney();
                    tv_cancle.setText(baoXian.tksm);
                } else {
                    CustomToast.showToast("保险信息获取失败!!");
                    finish();
                }
            }

            @Override
            public void error() {
                CustomToast.showToast("保险信息获取失败!!");
                finish();
            }
        }.post();
    }

    private void computeMoney() {
        totalMoney = totalMoney - money;
        money = 0;
        double cprice1 = 0;
        double eprice1 = 0;
        for (int i = 0; i < datas.size(); i++) {
            if (isFirst || !isSelect) {
                cprice1 = cprice1 + datas.get(i).crfee;
                eprice1 = eprice1 + datas.get(i).etfee;
            } else {
                if (map.containsKey(i)) {
                    cprice1 = cprice1 + datas.get(i).crfee;
                    eprice1 = eprice1 + datas.get(i).etfee;
                }
            }
        }
        if (isFirst) {
            isFirst = false;
        }

        Map<Integer, Boolean> map1 = tourist_adapter.getMap();
        for (int i = 0; i < map1.size(); i++) {
            money = money + (map1.get(i) ? cprice1 : eprice1);
        }

        totalMoney = totalMoney + money;
        setMoney();
    }

    @Override
    protected void initView() {
        os_sex = getView(R.id.os_sex);
        lv_desc = getView(R.id.lv_desc);
        tev_name = getView(R.id.tev_name);
        tev_tel = getView(R.id.tev_tel);
        tv_count = getView(R.id.tv_count);
        lv_courist = getView(R.id.lv_courist);
        lv_baoxian = getView(R.id.lv_baoxian);
        tv_cancle = getView(R.id.tv_cancle);
        tv_fp = getView(R.id.tv_fp);
        tv_total_money = getView(R.id.tv_total_money);
        bt_submit = getView(R.id.bt_submit);
        bt_submit.setOnClickListener(this);
        tv_desc = getView(R.id.tv_desc);
        iv_drop = getView(R.id.iv_drop);
        tv_desc.setOnClickListener(this);
        iv_drop.setOnClickListener(this);
        tv_count.setOnCountChangeListener(this);
        tourisList.add(1);
        tourist_adapter = new WriteOrderTouristAdapter(this, tourisList, lv_courist) {
            @Override
            public void selectChange(boolean change) {
                computeMoney();
            }
        };
        lv_courist.setAdapter(tourist_adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_drop:
            case R.id.tv_desc:
                showDesc();
                break;
            case R.id.bt_submit:
                submit();
                break;
        }
    }

    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("notgotohomepage", true);
        intent.putExtra(Constant.DFY_IS_FROM_Yd, true);
        startActivityForResult(intent, Constant.DFY_DETAIL2LOGIN_REQ);
    }

    /**
     * 订单提交
     */
    private void submit() {
        if (!PersonalCenterUtils.isLogin(Util.getMyApplication())) {
            goLogin();
            return;
        }


        final String consignee = tev_name.getContent();
        String sex = os_sex.getSelect() ? "男" : "女";
        final String tel = tev_tel.getContent();
        if (TextUtils.isEmpty(tel)) {
            CustomToast.showToast("请填写联系人姓名!");
            return;
        }
        if (TextUtils.isEmpty(consignee)) {
            CustomToast.showToast("请填写联系方式!");
            return;
        }
        StringBuffer bxBuffer = new StringBuffer();
        if (isSelect) {
            int bxCount = 1;
            for (Map.Entry<Integer, String> entity : map.entrySet()) {
                bxBuffer.append(entity.getValue());
                if (bxCount != map.size()) {
                    bxBuffer.append(",");
                }
                bxCount += 1;
            }
        } else {
            for (int i = 0; i < datas.size(); i++) {
                bxBuffer.append(datas.get(i).pack_id);
                if (i != datas.size() - 1) {
                    bxBuffer.append(",");
                }
            }
        }

        UserVo vo = Util.getUserInfo();
        Map<String, String> orderMap = new HashMap<>();
        String key = Util.getMa5("123456+" + vo.getUid() + "+" + id + "+" + totalMoney);
        orderMap.put("key", key);//(123456+uid+camp+price)
        orderMap.put("camp", id);
        orderMap.put("uid", vo.getUid());
        orderMap.put("username", vo.getUserName());
        orderMap.put("renshu", tv_count.getCount() + "");
        Map<Integer, Tourist> touristMap = tourist_adapter.getTouristMap();
        int touristCount = touristMap.size();
        Tourist tourist;
        StringBuffer youke = new StringBuffer();
        boolean a = true;//判断是否继续向下执行
        for (int i = 0; i < touristCount; i++) {
            tourist = touristMap.get(i);
            String name = tourist.name;
            String cardNum = tourist.cardNum;
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(cardNum)) {
                CustomToast.showToast("请完善游客信息!!");
                a = false;
                break;
            }
            youke = youke.append(tourist.name + "||" + tourist.cardType + "|" + tourist.cardNum + "|" + (tourist.peosonType ? "成人" : "儿童"));
            if (i != touristCount - 1) {
                youke.append("~");
            }
        }
        if (!a) {
            return;//如果游客信息没有填写完整，则不往下继续执行代码
        }

        StringBuffer goodsBuffer = new StringBuffer();
        int goodsCount = 1;
        for (Map.Entry<String, Object[]> entry : txMap.entrySet()) {
            goodsBuffer.append(entry.getKey());
            if (goodsCount != txMap.size() || zyMap.size() > 0) {
                goodsBuffer.append(",");
            }
            goodsCount += 1;
            Object[] obj = entry.getValue();
            orderMap.put("num_" + entry.getKey(), obj[1] + "");
        }


        StringBuffer tagBuffer;
        List<String> listTag;
        goodsCount = 1;
        for (Map.Entry<String, Object[]> entry : zyMap.entrySet()) {

            goodsBuffer.append(entry.getKey());
            if (goodsCount != zyMap.size()) {
                goodsBuffer.append(",");
            }
            Object[] obj = entry.getValue();
            listTag = (List<String>) obj[1];
            tagBuffer = new StringBuffer();
            for (int i = 0; i < listTag.size(); i++) {
                tagBuffer.append(listTag.get(i));
                if (i != listTag.size() - 1) {
                    tagBuffer.append(",");
                }
            }
            orderMap.put("num_" + entry.getKey(), listTag.size() + "");
            orderMap.put("tag_" + entry.getKey(), tagBuffer.toString());
        }
        orderMap.put("youke", youke.toString());
        orderMap.put("gotime", times[0]);
        orderMap.put("outtime", times[1]);
        orderMap.put("consignee", consignee);
        orderMap.put("sex", sex);
        orderMap.put("mobile", tel);
        orderMap.put("email", "");
        orderMap.put("packid", bxBuffer.toString());
        orderMap.put("goodsid", goodsBuffer.toString());
        orderMap.put("order_amount", totalMoney + "");
        orderMap.put("laiyuan", "android");

        new HttpApi(ConstantApi.ORDER, orderMap) {
            @Override
            protected void success(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int result = jsonObject.getInt("result");
                    if (result == 0) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        String order_sn = object.getString("order_sn");
                        String order_id = object.getString("order_id");
                        Bundle bundle = new Bundle();
                        bundle.putString("order_id", order_id);
                        bundle.putStringArray("times", times);
                        bundle.putInt("count", list.size() + (isSelect ? map.size() > 0 ? 1 : 0 : datas.size() > 0 ? 1 : 0));
                        SerializableList<Desc> list1 = new SerializableList<Desc>();
                        list1.setMap(list);
                        bundle.putSerializable("list", list1);
                        bundle.putString("name", consignee);
                        bundle.putString("title", title);
                        bundle.putString("tel", tel);
                        bundle.putInt("peosonCount", tv_count.getCount());
                        bundle.putDouble("totalMoney", totalMoney);
                        bundle.putDouble("baoxian", money);
                        Intent intent = new Intent(WriteOrderActivity.this, YdPayActivity.class);
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                        finish();
                    } else {
                        CustomToast.showToast("订单提交失败");
                    }
                } catch (JSONException e) {

                }

            }
        }.post(this, "正在提交订单");

    }

    /**
     * 显示订单详情
     */
    private void showDesc() {

        if (lv_desc.getVisibility() == View.VISIBLE) {
            lv_desc.setVisibility(View.GONE);
        } else {
            lv_desc.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public boolean jia() {
        tourisList.add(tv_count.getCount() + 1);
        tourist_adapter.notifi();
        computeMoney();
        return true;
    }

    @Override
    public boolean jian() {
        int count = tv_count.getCount() - 1;
        if (count <= 0) {
            return false;
        }
        tourisList.remove(count);
        tourist_adapter.notifi();
        tourist_adapter.removeMap();
        computeMoney();
        return true;
    }

    private void setMoney() {
        tv_total_money.setText(totalMoney + "");
    }

}
