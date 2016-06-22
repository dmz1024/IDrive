package com.ccpress.izijia.yd.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/27.
 * 套餐适配器
 */

public abstract class ChooseTcAdapter extends ChooseBaseAdapter<ChooseStores.Taocan> {
    private Map<Integer, Integer> map = new HashMap<>();


    public ChooseTcAdapter(Context ctx, List<ChooseStores.Taocan> list, MaxListView lv) {
        super(ctx, list, lv);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View FullView(final int i, View view) {
        final ViewHolder holder;
        if (view == null) {
            view = View.inflate(ctx, R.layout.yd_item_choose_tc, null);
            holder = new ViewHolder();
            holder.iv_img = (ImageView) view.findViewById(R.id.iv_img);
            holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            holder.tv_price = (TextView) view.findViewById(R.id.tv_price);
            holder.tv_sm = (TextView) view.findViewById(R.id.tv_sm);
            holder.tv_desc = (TextView) view.findViewById(R.id.tv_desc);
            holder.tv_id = (TextView) view.findViewById(R.id.tv_id);
            holder.tv_count = (CountTextView) view.findViewById(R.id.tv_count);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (map.containsKey(i)) {
            holder.tv_desc.setVisibility(View.VISIBLE);
        } else {
            holder.tv_desc.setVisibility(View.GONE);
        }
        final ChooseStores.Taocan taocan = list.get(i);
        final double price;
        if (ChooseStoreActivity.isSelect) {
            price = Double.parseDouble(taocan.amount_price);

        } else {
            price = taocan.shop_price;
        }

        holder.tv_id.setText(taocan.mc_id);
        holder.tv_count.setOnCountChangeListener(new CountTextView.OnCountChangeListener() {
            @Override
            public boolean jia() {
                if (!ChooseStoreActivity.isSelect) {
                    CustomToast.showToast("请先选择出行时间");
                    return false;
                }
                int[] mac = ChooseStoreActivity.macMap.get(taocan.mc_id);
                List<MaxListView> lls = ChooseStoreActivity.lls;
                MaxListView tcListView = lls.get(0);
                int totalCount = 0;
                for (int j = 0; j < list.size(); j++) {
                    String id = list.get(j).mc_id;
                    if (!TextUtils.equals(id, "")) {
                        if (TextUtils.equals(id, taocan.mc_id)) {
                            totalCount = totalCount + ((CountTextView) tcListView.getChildAt(j).findViewById(R.id.tv_count)).getCount();
                        }
                    }
                }
                int count = 0;
                if (mac != null) {
                    MaxListView macListView = lls.get(mac[0]);
                    CountTextView macCtv = (CountTextView) macListView.getChildAt(mac[1]).findViewById(R.id.tv_count);
                    count = macCtv.getCount();
                }


                if (count + totalCount >= taocan.maxnum) {
                    return false;
                }
                ChooseTcAdapter.this.jia(taocan.goods_id, taocan.goods_name, holder.tv_count.getCount() + 1, price);
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
                    ChooseTcAdapter.this.jian(taocan.goods_id, taocan.goods_name, holder.tv_count.getCount() - 1, price);
                    return true;
                }
            }
        });
        holder.tv_sm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.tv_desc.getVisibility() == View.VISIBLE) {
                    holder.tv_desc.setVisibility(View.GONE);
                    map.remove(i);
                } else {
                    holder.tv_desc.setVisibility(View.VISIBLE);
                    map.put(i, i);
                }
                notifi();
            }
        });
        loader.get(holder.iv_img, taocan.goods_thumb);
        holder.tv_title.setText(taocan.goods_name);

        holder.tv_price.setText("￥" + price);

        holder.tv_desc.setText(taocan.goods_desc);

        holder.iv_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PopDescView(ctx, view, taocan.lb, 2, list.get(i).goods_desc);
            }
        });
        return view;
    }


    public abstract void jia(String goods_id, String goods_name, int count, double price);

    public abstract void jian(String goods_id, String goods_name, int count, double price);

    class ViewHolder {
        ImageView iv_img;
        TextView tv_title;
        TextView tv_price;
        TextView tv_sm;
        TextView tv_desc;
        TextView tv_id;
        CountTextView tv_count;
    }
}
