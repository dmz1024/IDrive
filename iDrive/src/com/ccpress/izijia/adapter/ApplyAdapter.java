package com.ccpress.izijia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ccpress.izijia.R;

import java.util.ArrayList;

/**
 * Created by WLH on 2015/6/18 16:35.
 */
public class ApplyAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<String> data = new ArrayList<>();

    public ApplyAdapter(Context mContext){
        this.mContext = mContext;
    }

    public void setData(ArrayList<String> data){
        if(data != null){
            this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int i) {
        return data.get(i);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.item_act_apply, parent, false);
        }
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(getItem(position));

        return view;
    }
}
