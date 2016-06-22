package com.ccpress.izijia.yd.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.yd.api.MyImageLoader;
import com.ccpress.izijia.yd.entity.LocalComment;

import com.ccpress.izijia.yd.view.CircleImageView;
import com.ccpress.izijia.yd.view.StarImageView;
import java.util.List;

/**
 * Created by dengmingzhi on 16/5/31.
 */
public class LocalPersonAdapter extends MyBaseAdapter<LocalComment.Data> {
    private MyImageLoader loader = new MyImageLoader(10);

    public LocalPersonAdapter(Context ctx, List<LocalComment.Data> list) {
        super(ctx, list);
    }

    public LocalPersonAdapter(Context ctx, List<LocalComment.Data> list, ListView lv) {
        super(ctx, list, lv);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View FullView(int i, View view) {
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(ctx, R.layout.yd_item_comment, null);
            holder = new ViewHolder();
            holder.iv_img = (CircleImageView) view.findViewById(R.id.iv_img);
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.tv_content = (TextView) view.findViewById(R.id.tv_content);
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            holder.siv_star = (StarImageView) view.findViewById(R.id.siv_star);
            view.setTag(holder);
        }else {
            holder= (ViewHolder) view.getTag();
        }
        LocalComment.Data data=list.get(i);
        loader.get(holder.iv_img,data.thumb);
        holder.tv_name.setText(data.user_name);
        holder.tv_content.setText(data.content);
        holder.tv_time.setText(data.add_time);
        switch (data.comment_rank){
            case 0:
                holder.siv_star.setImageResource(R.drawable.yd_star_0);
                break;
            case 1:
                holder.siv_star.setImageResource(R.drawable.yd_star_1);
                break;
            case 2:
                holder.siv_star.setImageResource(R.drawable.yd_star_2);
                break;
            case 3:
                holder.siv_star.setImageResource(R.drawable.yd_star_3);
                break;
            case 4:
                holder.siv_star.setImageResource(R.drawable.yd_star_4);
                break;
            case 5:
                holder.siv_star.setImageResource(R.drawable.yd_star_5);
                break;
        }
        return view;
    }

    class ViewHolder {
        CircleImageView iv_img;
        TextView tv_name;
        TextView tv_content;
        TextView tv_time;
        StarImageView siv_star;
    }
}
