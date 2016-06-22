package com.ccpress.izijia.yd.view.popView;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.yd.adapter.ChooseGvAttrAdapter;
import com.ccpress.izijia.yd.api.MyImageLoader;
import com.ccpress.izijia.yd.entity.ChooseStores;
import com.ccpress.izijia.yd.view.MaxGridView;
import com.ccpress.izijia.yd.view.RotationViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengmingzhi on 16/5/28.
 */
public class PopDescView {
    private Context ctx;
    private PopupWindow popWindow;
    private List<ChooseStores.Lb> listImage;
    private int type;
    private List<ChooseStores.Attr> listAttr;
    private String desc;

    public PopDescView(Context ctx, View view, List<ChooseStores.Lb> listImage, int type) {
        this.ctx = ctx;
        this.listImage = listImage;
        this.type = type;
        popWindow = new PopupWindow(getView(), FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, true);
        popWindow.setBackgroundDrawable(new ColorDrawable());
        popWindow.showAtLocation(view, Gravity.TOP, 0, 0);
    }

    public PopDescView(Context ctx, View view, List<ChooseStores.Lb> listImage, int type, List<ChooseStores.Attr> listAttr) {
        this.listAttr = listAttr;
        this.ctx = ctx;
        this.listImage = listImage;
        this.type = type;
        popWindow = new PopupWindow(getView(), FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, true);
        popWindow.setBackgroundDrawable(new ColorDrawable());
        popWindow.showAtLocation(view, Gravity.TOP, 0, 0);

    }

    public PopDescView(Context ctx, View view, List<ChooseStores.Lb> listImage, int type, String desc) {
        this.desc = desc;
        this.ctx = ctx;
        this.listImage = listImage;
        this.type = type;
        popWindow = new PopupWindow(getView(), FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, true);
        popWindow.setBackgroundDrawable(new ColorDrawable());
        popWindow.showAtLocation(view, Gravity.TOP, 0, 0);
    }

    private View getView() {
        View view = View.inflate(ctx, R.layout.yd_pop_desc_view, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_desc);
        MaxGridView mgv = (MaxGridView) view.findViewById(R.id.mgv);
        RotationViewPager rvp = (RotationViewPager) view.findViewById(R.id.rvp);
        MyImageLoader loader = new MyImageLoader(5);
        List<View> views = new ArrayList<View>();
        for (int i = 0; i < listImage.size(); i++) {
            ImageView iv = new ImageView(ctx);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            loader.get(iv, listImage.get(i).img_original);
            views.add(iv);
        }
        rvp.setImages(views);
        if (type == 1) {
            if (listAttr != null) {
                mgv.setAdapter(new ChooseGvAttrAdapter(ctx, listAttr));
                mgv.setVisibility(View.VISIBLE);
            }
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText("说明：" + desc);
        }


        return view;
    }
}
