package com.ccpress.izijia.yd.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.yd.activity.ChooseStoreActivity;
import com.ccpress.izijia.yd.entity.ChooseStores;
import com.ccpress.izijia.yd.view.CountTextView;
import com.ccpress.izijia.yd.view.MaxGridView;
import com.ccpress.izijia.yd.view.MaxListView;
import com.ccpress.izijia.yd.view.XCFlowLayout;
import com.ccpress.izijia.yd.view.popView.PopDescView;
import com.ccpress.izijia.yd.view.popView.popUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/27.
 */
public abstract class ChooseZsAndYwAdapter extends ChooseBaseAdapter<ChooseStores.YwAndZs> {
    private int s;
    private Map<Integer, List<String>> mapOption = new HashMap<>();

    public ChooseZsAndYwAdapter(Context ctx, List<ChooseStores.YwAndZs> list, MaxListView lv) {
        super(ctx, list, lv);
    }

    public ChooseZsAndYwAdapter(Context ctx, List<ChooseStores.YwAndZs> list, MaxListView lv, int s) {
        this(ctx, list, lv);
        this.s = s;
    }


    private void seat(TextView tv, List<String> listOption) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i1 = 0; i1 < listOption.size(); i1++) {
            stringBuffer.append(listOption.get(i1)).append(" ");
        }
        tv.setText(stringBuffer.toString());
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View FullView(final int i, View view) {
        final ViewHolder holder;
        if (view == null) {
            view = View.inflate(ctx, R.layout.yd_item_choose_stay_camp, null);
            holder = new ViewHolder();
            holder.iv_img = (ImageView) view.findViewById(R.id.iv_img);
            holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            holder.tv_price = (TextView) view.findViewById(R.id.tv_price);
            holder.tv_count = (CountTextView) view.findViewById(R.id.tv_count);
            holder.tv_edit = (TextView) view.findViewById(R.id.tv_edit);
            holder.gv_option = (MaxGridView) view.findViewById(R.id.gv_option);
            holder.rl_seat = (RelativeLayout) view.findViewById(R.id.rl_seat);
            holder.tv_seat = (TextView) view.findViewById(R.id.tv_content);
            holder.rl_content = (RelativeLayout) view.findViewById(R.id.rl_content);
            holder.bt_clear = (Button) view.findViewById(R.id.bt_clear);
            holder.bt_update = (Button) view.findViewById(R.id.bt_update);
            holder.bt_ok= (Button) view.findViewById(R.id.bt_ok);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ChooseStoreActivity.isSelect) {
                    CustomToast.showToast("请先选择出行时间");
                    return;
                }
                holder.rl_content.setVisibility(View.INVISIBLE);
                holder.rl_seat.setVisibility(View.VISIBLE);
            }
        });

        holder.bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.rl_seat.setVisibility(View.GONE);
                holder.rl_content.setVisibility(View.VISIBLE);
            }
        });
//
        final ChooseStores.YwAndZs ywAndZs = list.get(i);
        final double price;
        if (ChooseStoreActivity.isSelect) {
            price = Double.parseDouble(ywAndZs.amount_price);
        } else {
            price = ywAndZs.shop_price;
        }
        final List<String> listOption;
        if (!mapOption.containsKey(i)) {
            listOption = new ArrayList<>();
        } else {
            listOption = mapOption.get(i);
        }

        final View finalView = view;
        holder.bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] selects = new String[listOption.size()];
                for (int k = 0; k < listOption.size(); k++) {
                    selects[k] = listOption.get(k);
                }
                new popUpdate(ctx, holder.tv_count, ywAndZs.edit_map, ywAndZs.tag, selects) {
                    @Override
                    protected void ok(List<String> list) {
                        listOption.clear();
                        listOption.addAll(list);
                        seat(holder.tv_seat, listOption);
                        ChooseZsAndYwAdapter.this.update(ywAndZs.goods_id, ywAndZs.goods_name, listOption, price);
                    }
                };
            }
        });


        holder.bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double price1 = price * listOption.size();
                listOption.clear();
                mapOption.put(i, listOption);
                holder.tv_count.setText("0");
                seat(holder.tv_seat, listOption);
//                holder.bt_clear.setVisibility(View.GONE);
//                holder.bt_update.setVisibility(View.GONE);
                holder.bt_clear.setEnabled(false);
                holder.bt_update.setEnabled(false);
                ChooseZsAndYwAdapter.this.jian(ywAndZs.goods_id, ywAndZs.goods_name, listOption, price1);
            }
        });

        holder.tv_count.setOnCountChangeListener(new CountTextView.OnCountChangeListener() {
            @Override
            public boolean jia() {
                List<MaxListView> lls = ChooseStoreActivity.lls;
                MaxListView tcListView = lls.get(0);
                int chileCount = tcListView.getCount();
                int totalCount = 0;
                for (int j = 0; j < chileCount; j++) {
                    View view1 = tcListView.getChildAt(j);
                    CountTextView countTextView = (CountTextView) view1.findViewById(R.id.tv_count);
                    TextView tv_id = (TextView) view1.findViewById(R.id.tv_id);
                    String id = tv_id.getText().toString();
                    if (TextUtils.equals(id, ywAndZs.goods_id)) {
                        totalCount += countTextView.getCount();
                    }
                }

                if (totalCount + holder.tv_count.getCount() >= ywAndZs.tag.length) {
                    return false;
                }

//                holder.bt_update.setVisibility(View.VISIBLE);
//                holder.bt_clear.setVisibility(View.VISIBLE);
                holder.bt_clear.setEnabled(true);
                holder.bt_update.setEnabled(true);
                String[] tag = ywAndZs.tag;
                int tagLength = tag.length;
                for (int j = 0; j < tagLength; j++) {
                    if (!listOption.contains(tag[j])) {
                        listOption.add(tag[j]);
                        mapOption.put(i, listOption);
                        break;
                    }
                }
                seat(holder.tv_seat, listOption);
                holder.tv_count.setText((holder.tv_count.getCount() + 1) + "");
                ChooseZsAndYwAdapter.this.jia(ywAndZs.goods_id, ywAndZs.goods_name, listOption, price);
                return true;
            }


            @Override
            public boolean jian() {
                if (holder.tv_count.getCount() <= 0) {
                    return false;
                }
                if (holder.tv_count.getCount() - 1 <= 0) {
//                    holder.bt_clear.setVisibility(View.GONE);
//                    holder.bt_update.setVisibility(View.GONE);
                    holder.bt_clear.setEnabled(false);
                    holder.bt_update.setEnabled(false);

                }
                listOption.remove(listOption.size() - 1);
                mapOption.put(i, listOption);
                holder.tv_count.setText((holder.tv_count.getCount() - 1) + "");
                ChooseZsAndYwAdapter.this.jian(ywAndZs.goods_id, ywAndZs.goods_name, listOption, price);
                seat(holder.tv_seat, listOption);
                return true;
            }
        });
        ChooseStoreActivity.macMap.put(ywAndZs.goods_id, new int[]{s, i});
        holder.tv_title.setText(ywAndZs.goods_name);
        holder.tv_price.setText("￥" + price);
        holder.gv_option.setAdapter(new ChooseGvAttrAdapter(ctx, ywAndZs.attr));

        holder.iv_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PopDescView(ctx, view, ywAndZs.lb, 1, list.get(i).attr);
            }
        });
        loader.get(holder.iv_img, ywAndZs.goods_thumb);
        return view;
    }


    public abstract void jia(String goods_id, String goods_name, List<String> tags, double price);

    public abstract void jian(String goods_id, String goods_name, List<String> tags, double price);

    public abstract void update(String goods_id, String goods_name, List<String> tags, double price);

    class ViewHolder {
        ImageView iv_img;
        TextView tv_title;
        MaxGridView gv_option;
        TextView tv_price;
        CountTextView tv_count;
        TextView tv_edit;
        TextView tv_seat;
        RelativeLayout rl_content;
        RelativeLayout rl_seat;
        Button bt_clear;
        Button bt_update;
        Button bt_ok;
    }
}
