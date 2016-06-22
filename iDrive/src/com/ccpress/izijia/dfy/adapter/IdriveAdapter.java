package com.ccpress.izijia.dfy.adapter;

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
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


/**
 * Created by dmz1024 on 2016/3/17.
 */
public class IdriveAdapter extends BaseAdapter {
    private int displayWidth;
    private List<Idrive> list;
    private LayoutInflater mInflater;
    private ImageOptions options;
    private ViewHolder holder;
    private RelativeLayout.LayoutParams params;

    public IdriveAdapter(List<Idrive> list) {
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
    public Idrive getItem(int i) {
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

        Idrive idrive = getItem(i);

        if(idrive.getLasttime()==1){
            holder.iv_type.setImageResource(R.drawable.dfy_icon_finish);
        }else if(idrive.getIs_hot()==1){
            holder.iv_type.setImageResource(R.drawable.dfy_icon_hot);
        }else if(idrive.getIs_new()==1){
            holder.iv_type.setImageResource(R.drawable.dfy_icon_new);
        }else {
            holder.iv_type.setImageDrawable(new ColorDrawable());
        }



        holder.tv_top.setText(idrive.getGoods_appname());
        holder.tv_bottom.setText(idrive.getXingcheng());

        if (idrive.getShop_price().lastIndexOf('.') > 0)
            holder.tv_price.setText(idrive.getShop_price().substring(0, idrive.getShop_price().lastIndexOf('.')));
        else
            holder.tv_price.setText(idrive.getShop_price());

        x.image().bind(holder.iv_appimg, idrive.getGoods_appimg(), options);
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
