package com.ccpress.izijia.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.LinesDetailUserUploadActivity;
import com.ccpress.izijia.entity.LinesDetailUploadEntity;

import java.util.ArrayList;

/**
 * Created by WLH on 2015/5/22 14:21.
 */
public class PassCityAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<LinesDetailUploadEntity.Pathway> data = new ArrayList<LinesDetailUploadEntity.Pathway>();
    private Intent intent;

    public PassCityAdapter(Context mContext){
        this.mContext = mContext;
    }

    public void setData(ArrayList<LinesDetailUploadEntity.Pathway> data){
        this.data.clear();
        this.data.addAll(data);
    }

    @Override
    public int getCount() {
        if(data!= null){
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(data != null){
            return data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if(convertView != null){
            view = convertView;
        }
        else{
            view = LayoutInflater.from(mContext).inflate(R.layout.item_passcity, parent, false);
        }
        final LinesDetailUploadEntity.Pathway item = (LinesDetailUploadEntity.Pathway) getItem(position);
        TextView mTxtTitle = (TextView) view.findViewById(R.id.name);
        mTxtTitle.setText(item.getCname());


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(mContext, LinesDetailUserUploadActivity.class);
                intent.putExtra(LinesDetailUserUploadActivity.EXTRA_ACT_TYPE, LinesDetailUserUploadActivity.Destination);
                intent.putExtra(LinesDetailUserUploadActivity.EXTRA_LID, item.getCid());
                mContext.startActivity(intent);
            }
        });

        return view;
    }
}
