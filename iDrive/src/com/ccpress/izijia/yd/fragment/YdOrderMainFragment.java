package com.ccpress.izijia.yd.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import com.ccpress.izijia.dfy.fragment.FragmentBase;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.dfy.view.RefreshListView;
import com.ccpress.izijia.vo.UserVo;
import com.ccpress.izijia.yd.activity.YdOrderInfoActivity;
import com.ccpress.izijia.yd.adapter.ChooseGvAttrAdapter;
import com.ccpress.izijia.yd.adapter.YdOrderAdapter;
import com.ccpress.izijia.yd.constant.ConstantApi;
import com.ccpress.izijia.yd.entity.YdOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by administror on 2016/3/29 0029.
 */
public class YdOrderMainFragment extends FragmentBase<YdOrder.Data, YdOrderAdapter> {
    private Map<String, String> map = new HashMap<String, String>();
    private String uid;
    private String mobile;
    private UserVo vo;

    public YdOrderMainFragment() {
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
//        map.put("Mobile", "");
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
        return ConstantApi.ORDER_LIST;
    }

    @Override
    protected YdOrderAdapter getAdapter(List<YdOrder.Data> list) {
        return new YdOrderAdapter(list, getActivity());
    }

    @Override
    protected RefreshListView.Mode getMode() {
        return RefreshListView.Mode.END;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//        if (i >= mlist.size() + 1) {
//            return;
//        }
//        Intent intent = new Intent(getActivity(), YdOrderInfoActivity.class);
//        intent.putExtra("order_id", mlist.get(i - 1));
//        intent.putExtra("uid", vo.getUid());
//        startActivity(intent);
    }

    @Override
    protected List<YdOrder.Data> getList(String json) {
        return JsonUtil.json2List(json, YdOrder.Data.class, "datas");
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
