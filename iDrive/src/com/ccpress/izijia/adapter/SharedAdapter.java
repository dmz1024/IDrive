package com.ccpress.izijia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.trs.types.UserInfoEntity;
import com.trs.util.ImageDownloader;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;

/**
 * Created by WLH on 2015/5/13 10:50.
 * 信息详情页分享过的人  adapter
 */
public class SharedAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<UserInfoEntity> data = new ArrayList<UserInfoEntity>();

    public SharedAdapter(Context mContext){
        this.mContext = mContext;
    }

    public void setData(ArrayList<UserInfoEntity> data){
        this.data.clear();
        this.data.addAll(data);
    }

    @Override
    public int getCount() {
        if(data != null){
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if(data != null){
            return data.get(i);
        }else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView != null){
            view = convertView;
        }
        else{
            view = LayoutInflater.from(mContext).inflate(R.layout.item_shared, parent, false);
        }

        CircleImageView img = (CircleImageView) view.findViewById(R.id.user_img);
        TextView name = (TextView) view.findViewById(R.id.name);

        UserInfoEntity item = (UserInfoEntity) getItem(position);
        if(item != null){
            new ImageDownloader.Builder()
                    .setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC)
                    .build(item.getAvatar(), img)
                    .start();
            name.setText(item.getName());
        }

        return view;
    }
}
