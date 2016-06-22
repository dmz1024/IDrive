package com.ccpress.izijia.yd.view.popView;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.yd.constant.ConstantApi;
import com.ccpress.izijia.yd.entity.Option;
import com.ccpress.izijia.yd.entity.options;
import com.ccpress.izijia.yd.view.GridViewText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengmingzhi on 16/5/26.
 */
public abstract class PopOptionView {
    private Context ctx;
    private PopupWindow popWindow;
    private OptionAdapter mAdapter;
    private ListView listView;

    public PopOptionView(Context ctx, View view) {
        this.ctx = ctx;
        popWindow = new PopupWindow(getView(), FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, true);
        popWindow.showAsDropDown(view);

    }

    public void setData() {
        NetUtil.Post(ConstantApi.CAMPOPTION, null, new MyCallBack() {
            @Override
            public void onSuccess(String s) {
                Option option = JsonUtil.getJavaBean(s, Option.class);
                if (option.result == 0) {
                    for (int i = 0; i < option.data.size(); i++) {
                        options op = new options();
                        op.brand_name = "全部";
                        op.brand_id = "";
                        option.data.get(i).option.add(0, op);
                    }
                    listView.setAdapter(mAdapter = new OptionAdapter(option.data));
                }
            }
        });
    }


    private View getView() {
        View view = View.inflate(ctx, R.layout.yd_pop_option, null);
        Button bt_cancle = (Button) view.findViewById(R.id.bt_cancle);
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popWindow.dismiss();
            }
        });
        Button bt_ok = (Button) view.findViewById(R.id.bt_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = listView.getChildCount();
                List<String> ids = new ArrayList<String>();
                for (int i = 0; i < count; i++) {
                    GridViewText gridViewText = (GridViewText) listView.getChildAt(i);
                    ids.add(gridViewText.select);
                }
                ok(ids);
                popWindow.dismiss();
            }
        });
        listView = (ListView) view.findViewById(R.id.lv_option);
        return view;
    }

    public abstract void ok(List<String> ids);


    class OptionAdapter extends BaseAdapter {
        private List<Option.Data> datas;

        public OptionAdapter(List<Option.Data> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            GridViewText gridViewText = new GridViewText(ctx);
            gridViewText.setDatas(datas.get(i).option, datas.get(i).name);
            return gridViewText;
        }
    }
}
