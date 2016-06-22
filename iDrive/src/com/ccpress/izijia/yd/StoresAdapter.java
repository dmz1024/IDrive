package com.ccpress.izijia.yd;

/**
 * Created by dengmingzhi on 16/5/26.
 */

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.entity.Idrive;
import com.ccpress.izijia.dfy.util.DensityUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.yd.entity.Stores;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


/**
 * Created by dmz1024 on 2016/3/17.
 */
public class StoresAdapter extends BaseAdapter {
    private int displayWidth;
    private List<Stores.Data> list;
    private LayoutInflater mInflater;
    private ImageOptions options;
    private ViewHolder holder;
    private RelativeLayout.LayoutParams params;

    public StoresAdapter(List<Stores.Data> list) {
        this.list = list;
        this.displayWidth = DensityUtil.getScreenWidth();
        this.mInflater = LayoutInflater.from(Util.getMyApplication());
        options = new ImageOptions.Builder()
                //设置加载过程中的图片
                .setLoadingDrawableId(R.drawable.dfy_icon_de_1)
                        //设置加载失败后的图片
                .setFailureDrawableId(R.drawable.dfy_icon_de_1)
                        //设置使用缓存
                .setUseMemCache(true)
                        //设置显示圆形图片
//                .setCircular(true)
                        //设置支持gif
                .setIgnoreGif(false)
//                .setSize(displayWidth, (int) (displayWidth / 2.8))
                .build();

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Stores.Data getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(R.layout.dfy_item_idrive, null);
            holder = new ViewHolder();
            x.view().inject(holder, view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (params == null) {
            params = (RelativeLayout.LayoutParams) holder.iv_appimg.getLayoutParams();
            params.height = (int) (displayWidth / 2.8);
            params.width = displayWidth;
        }

        holder.iv_appimg.setLayoutParams(params);

        Stores.Data data = getItem(i);


        holder.tv_top.setText(data.supplier_name);
        holder.tv_bottom.setText(data.supplier_title);


            holder.tv_price.setText(data.shop_price);

        x.image().bind(holder.iv_appimg, data.logo, options);
        return view;
    }

    class ViewHolder {
        @ViewInject(R.id.lv_appimg)
        ImageView iv_appimg;
        @ViewInject(R.id.tv_top)
        TextView tv_top;
        @ViewInject(R.id.tv_bottom)
        TextView tv_bottom;
        @ViewInject(R.id.tv_price)
        TextView tv_price;
        @ViewInject(R.id.iv_type)
        ImageView iv_type;
    }

}
