package com.ccpress.izijia.dfy.adapter;

import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.entity.SearchIdrive;
import com.ccpress.izijia.dfy.util.DensityUtil;
import com.ccpress.izijia.dfy.util.Util;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by dmz1024 on 2016/3/20.
 */
public class SearchAdapter extends BaseAdapter {
    private List<SearchIdrive> list;
    private LayoutInflater mInflater;
    private ImageOptions options;
    private ViewHolder holder;
    private RelativeLayout.LayoutParams ll_params;
    private RelativeLayout.LayoutParams iv_params;

    public SearchAdapter(List<SearchIdrive> list) {
        this.list = list;
        this.mInflater = LayoutInflater.from(Util.getMyApplication());
        options = new ImageOptions.Builder()
                //设置加载过程中的图片
                .setLoadingDrawableId(R.drawable.dfy_icon_de_2)
                        //设置加载失败后的图片
                .setFailureDrawableId(R.drawable.dfy_icon_de_2)
                        //设置使用缓存
                .setUseMemCache(true)
                        //设置显示圆形图片
//                .setCircular(true)
//                        设置支持gif
                .setIgnoreGif(true)
                .setSize((int) (DensityUtil.getScreenWidth() / 3.2), (int) (DensityUtil.getScreenWidth() / 3.3))
                .build();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public SearchIdrive getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(R.layout.dfy_search_item, null);
            holder = new ViewHolder();
            x.view().inject(holder, view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        SearchIdrive idrive = getItem(i);



        if (ll_params == null) {
            ll_params = (RelativeLayout.LayoutParams) holder.ll_root.getLayoutParams();
            ll_params.height = (int) (DensityUtil.getScreenWidth() / 3.3);
        }

        holder.ll_root.setLayoutParams(ll_params);

        if (iv_params == null) {
            iv_params = (RelativeLayout.LayoutParams) holder.iv_left.getLayoutParams();
            iv_params.height = ll_params.height;
            iv_params.width = ll_params.height;
        }

        holder.iv_left.setLayoutParams(iv_params);

        holder.tv_name.setText("<" + idrive.getGoods_appname() + ">");
        holder.tv_xc.setText(idrive.getXc() + "天行程");
        holder.tv_xingcheng.setText("线路：" + idrive.getXingcheng());
        holder.tv_note.setText(idrive.getSeller_note());
        holder.tv_brand_name.setText(idrive.getBrand_name());
        holder.tv_price.setText(idrive.getShop_price());

        if(idrive.getLasttime()==1){
            holder.iv_type.setImageResource(R.drawable.icon_finish);
        }else if(idrive.getIs_hot()==1){
            holder.iv_type.setImageResource(R.drawable.icon_hot);
        }else if(idrive.getIs_new()==1){
            holder.iv_type.setImageResource(R.drawable.icon_new);
        }else {
            holder.iv_type.setImageDrawable(new ColorDrawable());
        }

        x.image().bind(holder.iv_left, idrive.getGoods_thumb(), options);
        return view;
    }

    class ViewHolder {
        @ViewInject(R.id.iv_left)
        ImageView iv_left;
        @ViewInject(R.id.tv_name)
        TextView tv_name;
        @ViewInject(R.id.tv_xc)
        TextView tv_xc;
        @ViewInject(R.id.tv_price)
        TextView tv_price;
        @ViewInject(R.id.tv_xingcheng)
        TextView tv_xingcheng;
        @ViewInject(R.id.tv_note)
        TextView tv_note;
        @ViewInject(R.id.brand_name)
        TextView tv_brand_name;
        @ViewInject(R.id.ll_root)
        LinearLayout ll_root;
        @ViewInject(R.id.iv_type)
        private ImageView iv_type;
    }
}
