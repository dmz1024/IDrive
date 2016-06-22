package com.ccpress.izijia.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.LinesDetailImageTextActivity;
import com.ccpress.izijia.activity.ProductDetailActivity;
import com.ccpress.izijia.entity.CareChooseEntity;
import com.trs.util.ImageDownloader;
import com.trs.util.StringUtil;

import java.util.ArrayList;

/**
 * Created by WLH on 2015/6/18 14:05.
 */
public class MomentsAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<CareChooseEntity.Moments> moments = new ArrayList<>();

    public MomentsAdapter(Context mContext){
        this.mContext = mContext;
    }

    public void setMoments(ArrayList<CareChooseEntity.Moments> moments){
        if(moments != null){
            this.moments.clear();
            this.moments.addAll(moments);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {

        return moments.size();
    }

    @Override
    public Object getItem(int i) {
        return moments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(convertView != null){
            view = convertView;
        }
        else{
            view = LayoutInflater.from(mContext).inflate(R.layout.item_moment, parent, false);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.img);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView time = (TextView) view.findViewById(R.id.time);
        final CareChooseEntity.Moments item = (CareChooseEntity.Moments) getItem(position);
        if(item != null){
            if(!StringUtil.isEmpty(item.getImage())){
                new ImageDownloader.Builder()
                        .setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC)
                        .build(item.getImage(), imageView)
                        .start();
            }
            title.setText(item.getTitle());
            time.setText(item.getTime());
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                intent.putExtra(LinesDetailImageTextActivity.EXTRA_LID, item.getMid());
                mContext.startActivity(intent);
            }
        });

        return view;
    }
}
