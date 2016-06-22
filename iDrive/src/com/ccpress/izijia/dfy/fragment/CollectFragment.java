package com.ccpress.izijia.dfy.fragment;

import android.content.Intent;
import com.ccpress.izijia.dfy.activity.DetailsActivity;
import com.ccpress.izijia.dfy.adapter.CollectAdapter;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.entity.Collect;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.dfy.view.RefreshListView;
import com.ccpress.izijia.vo.UserVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmz1024 on 2016/3/31.
 * 收藏页---已作废
 */
public class CollectFragment extends FragmentBase<Collect, CollectAdapter> {
    private Map<String, String> map = new HashMap<String, String>();
    private UserVo vo;
    public CollectFragment(){
        vo= Util.getUserInfo();
    }

    @Override
    protected Map<String, String> get() {
        map.put("s", "/favorite/app/fav_zjt_list");
        map.put("uid", vo.getUid());
        map.put("auth", vo.getAuth());
        map.put("pageIndex", page + "");
        return map;
    }

    @Override
    protected String getUrl() {
        return Constant.DFY_COLLECT;
    }

    @Override
    protected CollectAdapter getAdapter(List<Collect> list) {
        return new CollectAdapter(list, getActivity());
    }


    @Override
    protected RefreshListView.Mode getMode() {
        return RefreshListView.Mode.BOTH;
    }

    @Override
    protected void onItemClick(List<Collect> list, int i) {
        String id = list.get(i -1).getObj_id();
        if (id.equals("") || id.equals("(null)")) {
            CustomToast.showToast("该商品已下架");
            return;
        }
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    protected List<Collect> getList(String json) {
        return JsonUtil.json2Collect(json);
    }

    @Override
    protected boolean getSubmitType() {
        return false;
    }
}
