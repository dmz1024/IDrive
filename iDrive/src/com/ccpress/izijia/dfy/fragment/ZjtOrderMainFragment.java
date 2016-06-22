package com.ccpress.izijia.dfy.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import com.ccpress.izijia.dfy.activity.OrderDetailActivity;
import com.ccpress.izijia.dfy.adapter.OrderAdapter;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.entity.Order;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.dfy.view.RefreshListView;
import com.ccpress.izijia.vo.UserVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by administror on 2016/3/29 0029.
 */
public class ZjtOrderMainFragment extends FragmentBase<Order, OrderAdapter> {
    private Map<String, String> map = new HashMap<String, String>();
    private String uid;
    private String mobile;
    private UserVo vo;

    public ZjtOrderMainFragment() {
        vo = Util.getUserInfo();
        uid = vo.getUid();

    }

    @Override
    protected void showData() {
        isNew = true;
        super.showData();
    }

    @Override
    protected void initData(boolean isFresh) {
        super.initData(true);
    }

    @Override
    protected Map<String, String> get() {
        map.put("act", "order_list");
        map.put("uid", uid);
        map.put("Mobile", "");
        map.put("page", page + "");
        return map;
    }

    public Map<String, String> map() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public void initData() {
        page = 1;
        isClear = true;
        super.initData(true);
    }

    @Override
    protected String getUrl() {
        return Constant.DFY_ORDER_LIST;
    }

    @Override
    protected OrderAdapter getAdapter(List<Order> list) {
        return new OrderAdapter(list, getActivity());
    }

    @Override
    protected RefreshListView.Mode getMode() {
        return RefreshListView.Mode.END;
    }

    @Override
    protected void onItemClick(final List<Order> list, final int i) {
        if(i>mlist.size()){
            return;
        }

        final ProgressDialog pdlog = new ProgressDialog(getActivity());
        pdlog.setMessage("获取订单详情...");
        pdlog.show();
        final String order_id = list.get(i-1).getOrder_id();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", uid);
        map.put("order_id", order_id);
//        map.put("mobile","");
        NetUtil.Post(Constant.DFY_ORDERINFO, map, new com.ccpress.izijia.dfy.callBack.MyCallBack() {
            @Override
            public void onSuccess(String json) {
                Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                intent.putExtra("goods_id", list.get(i - 1).getGoods_id());
                intent.putExtra("json", json);
                startActivity(intent);
            }

            @Override
            public void onFinished() {
                pdlog.dismiss();
            }
        });


    }

    @Override
    protected List<Order> getList(String json) {
        return JsonUtil.json2List(json, Order.class, "datas");
    }

    @Override
    protected boolean getSubmitType() {
        return false;
    }

    @Override
    protected void saveJson(String json) {
//        super.saveJson(json);
    }


}
