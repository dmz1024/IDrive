package com.ccpress.izijia.yd.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.vo.UserVo;
import com.ccpress.izijia.yd.activity.CommentActivity;
import com.ccpress.izijia.yd.activity.YdPayActivity;
import com.ccpress.izijia.yd.constant.ConstantApi;
import com.ccpress.izijia.yd.entity.ChooseStores;
import com.ccpress.izijia.yd.entity.Desc;
import com.ccpress.izijia.yd.entity.SerializableList;
import com.ccpress.izijia.yd.entity.YdOrder;
import com.ccpress.izijia.yd.view.MaxListView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/27.
 */
public class ChooseGvAttrAdapter extends ChooseBaseAdapter<ChooseStores.Attr> {
    public ChooseGvAttrAdapter(Context ctx, List<ChooseStores.Attr> list) {
        super(ctx, list);
    }

    @Override
    public View FullView(int i, View view) {
        View v = View.inflate(ctx, R.layout.yd_choose_gv_item, null);
        TextView tv_content = (TextView) v.findViewById(R.id.tv_content);
        tv_content.setText(list.get(i).attr_name + "：" + list.get(i).attr_value);
        return v;
    }

}
