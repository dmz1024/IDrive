package com.ccpress.izijia.yd.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.yd.activity.ChooseStoreActivity;
import com.ccpress.izijia.yd.entity.ChooseStores;
import com.ccpress.izijia.yd.view.CountTextView;
import com.ccpress.izijia.yd.view.MaxListView;
import com.ccpress.izijia.yd.view.popView.PopDescView;

import java.util.List;

/**
 * Created by dengmingzhi on 16/5/27.
 */
public abstract class ChooseXxAdapter extends ChooseBaseAdapter<ChooseStores.Tese> {


    public ChooseXxAdapter(Context ctx, List<ChooseStores.Tese> list, MaxListView lv) {
        super(ctx, list, lv);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View FullView(int i, View view) {
        final ViewHolder holder;
        if (view == null) {
            view = View.inflate(ctx, R.layout.yd_item_choose_xx, null);
            holder = new ViewHolder();
            holder.iv_img = (ImageView) view.findViewById(R.id.iv_img);
            holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            holder.tv_desc = (TextView) view.findViewById(R.id.tv_desc);
            holder.tv_price = (TextView) view.findViewById(R.id.tv_price);
            holder.tv_count = (CountTextView) view.findViewById(R.id.tv_count);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final ChooseStores.Tese tese = list.get(i);
        final double price;
        if (ChooseStoreActivity.isSelect) {
            price = Double.parseDouble(tese.amount_price);
        } else {
            price = tese.shop_price;
        }
        holder.tv_count.setOnCountChangeListener(new CountTextView.OnCountChangeListener() {
            @Override
            public boolean jia() {
                if (!ChooseStoreActivity.isSelect) {
                    CustomToast.showToast("请先选择出行时间");
                    return false;
                }
                ChooseXxAdapter.this.jia(tese.goods_id, tese.goods_name, holder.tv_count.getCount() + 1,price);
                return true;
            }

            @Override
            public boolean jian() {
                if (!ChooseStoreActivity.isSelect) {
                    CustomToast.showToast("请先选择出行时间");
                    return false;
                }
                if (holder.tv_count.getCount() <= 0) {
                    return false;
                } else {
                    ChooseXxAdapter.this.jian(tese.goods_id, tese.goods_name, holder.tv_count.getCount() - 1, price);
                    return true;
                }

            }
        });
        holder.tv_title.setText(tese.goods_name);
        holder.tv_desc.setText(tese.goods_desc);
        holder.tv_price.setText("￥" + price);
        holder.iv_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PopDescView(ctx, view, tese.lb, 2, tese.goods_desc);
            }
        });
        loader.get(holder.iv_img,tese.goods_thumb);
        return view;
    }

    public abstract void jia(String goods_id, String goods_name, int count, double price);

    public abstract void jian(String goods_id, String goods_name, int count, double price);

    class ViewHolder {
        ImageView iv_img;
        TextView tv_title;
        TextView tv_desc;
        TextView tv_price;
        CountTextView tv_count;
    }
}
