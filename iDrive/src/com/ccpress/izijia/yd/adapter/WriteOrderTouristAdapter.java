package com.ccpress.izijia.yd.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.yd.entity.Tourist;
import com.ccpress.izijia.yd.util.Utility;
import com.ccpress.izijia.yd.view.MyAlert;
import com.ccpress.izijia.yd.view.OptionSelect;
import com.ccpress.izijia.yd.view.TextEditView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/29.
 */
public abstract class WriteOrderTouristAdapter extends MyBaseAdapter<Integer> {
    private Map<Integer, Tourist> touristMap = new HashMap<>();
    Map<Integer, Boolean> peosonTypeMap = new HashMap<>();

    public WriteOrderTouristAdapter(Context ctx, List list, ListView lv) {
        super(ctx, list, lv);
    }

    @Override
    public View FullView(final int i, View v) {
        final ViewHolder holder;
        if (v == null) {
            v = View.inflate(ctx, R.layout.yd_item_tourist, null);
            holder = new ViewHolder();
            holder.tv_title = (TextView) v.findViewById(R.id.tv_title);
            holder.tev_name = (TextEditView) v.findViewById(R.id.tev_name);
            holder.rl_type = (RelativeLayout) v.findViewById(R.id.rl_type);
            holder.tv_type = (TextView) v.findViewById(R.id.tv_type);
            holder.tev_card_num = (TextEditView) v.findViewById(R.id.tev_card_num);
            holder.os_sex = (OptionSelect) v.findViewById(R.id.os_sex);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.tv_title.setText("游客" + (i + 1));
        holder.os_sex.setOnSelectChangeListener(new OptionSelect.OnSelectChangeListener() {
            @Override
            public void selectChange(boolean change) {
                touristMap.get(i).peosonType = change;
                peosonTypeMap.put(i, change);
                WriteOrderTouristAdapter.this.selectChange(change);
            }
        });

        holder.rl_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyAlert(ctx, "证件类型") {
                    @Override
                    public void singleSele(String content) {
                        touristMap.get(i).cardType = content;
                        holder.tv_type.setText(content);
                    }
                }.SingleSelection(new String[]{"身份证", "护照", "港澳通行证", "军官证"});
            }
        });

        holder.tev_card_num.setOnTextChangeListener(new TextEditView.OnTextChangeListener() {
            @Override
            public void change(String content) {
                touristMap.get(i).cardNum = content;
            }
        });

        holder.tev_name.setOnTextChangeListener(new TextEditView.OnTextChangeListener() {
            @Override
            public void change(String content) {
                touristMap.get(i).name = content;
            }
        });

        Tourist tourist;
        if (touristMap.size() > 0 && i != touristMap.size()) {
            tourist = touristMap.get(i);
        } else {
            tourist = new Tourist();
            touristMap.put(i, tourist);
        }
        holder.tev_name.setContent(tourist.name);
        holder.tev_card_num.setContent(tourist.cardNum);
        holder.tv_type.setText(tourist.cardType);
        holder.os_sex.setSelect(tourist.peosonType);
        peosonTypeMap.put(i, holder.os_sex.getSelect());
        return v;
    }

    class ViewHolder {
        TextView tv_title;
        TextEditView tev_name;
        RelativeLayout rl_type;
        TextView tv_type;
        TextEditView tev_card_num;
        OptionSelect os_sex;
    }

    public void removeMap() {
        peosonTypeMap.remove(peosonTypeMap.size() - 1);
    }

    /**
     * 返回用户信息
     *
     * @return
     */
    public Map<Integer, Tourist> getTouristMap() {
        return touristMap;
    }

    public Map<Integer, Boolean> getMap() {
        return peosonTypeMap;
    }


    public abstract void selectChange(boolean change);

    public void notifi() {
        notifyDataSetChanged();
        Utility.setListViewHeightBasedOnChildren(lv);

    }
}
