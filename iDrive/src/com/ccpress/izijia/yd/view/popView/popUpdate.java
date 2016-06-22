package com.ccpress.izijia.yd.view.popView;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.yd.api.MyImageLoader;
import com.ccpress.izijia.yd.entity.ChooseStores;
import com.ccpress.izijia.yd.view.GridViewText1;

import java.util.List;

/**
 * Created by dengmingzhi on 16/6/6.
 */
public class popUpdate {
    private Context ctx;
    private PopupWindow popWindow;
    private String campMap;
    private String[] list;
    private String[] selects;

    public popUpdate(Context ctx, View view, String campMap, String[] list, String[] selects) {
        this.ctx = ctx;
        this.campMap = campMap;
        this.list = list;
        this.selects = selects;
        popWindow = new PopupWindow(getView(), FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, true);
        popWindow.showAtLocation(view, Gravity.TOP, 0, 0);

    }

    private View getView() {
        View view = View.inflate(ctx, R.layout.item_pop_update, null);
        ImageView iv_camp = (ImageView) view.findViewById(R.id.iv_camp);
        final GridViewText1 gv_update = (GridViewText1) view.findViewById(R.id.gv_update);
        Button bt_ok = (Button) view.findViewById(R.id.bt_ok);
        Button bt_cancle = (Button) view.findViewById(R.id.bt_cancle);
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
            }
        });
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gv_update.getList().size() != selects.length) {
                    CustomToast.showToast("请选择" + selects.length + "个位置");
                    return;
                }
                ok(gv_update.getList());
                popWindow.dismiss();
            }
        });
        gv_update.setTitle("");
        MyImageLoader loader = new MyImageLoader(2);
        loader.get(iv_camp, campMap);
        gv_update.setDatas(list, selects);
        return view;
    }

    protected void ok(List<String> list) {
    }

}

