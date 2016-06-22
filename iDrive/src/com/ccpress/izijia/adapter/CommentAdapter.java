package com.ccpress.izijia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.entity.CommentItemEntity;
import com.trs.types.UserInfoEntity;
import com.trs.util.ImageDownloader;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;

/**
 * Created by WLH on 2015/5/12 15:42.
 */
public class CommentAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<CommentItemEntity> datas = new ArrayList<CommentItemEntity>();
    private ArrayList<UserInfoEntity> datas1 = new ArrayList<UserInfoEntity>();

    private boolean isComment = true;

    public CommentAdapter(Context mContext, boolean isComment){
        this.mContext = mContext;
        this.isComment = isComment;
    }

    public void setDatas(ArrayList<CommentItemEntity> datas){
        if(datas != null){
            this.datas.clear();
            this.datas.addAll(datas);
        }
    }

    public void setDatas1(ArrayList<UserInfoEntity> datas1){
        if(datas1!=null){
            this.datas1.clear();
            this.datas1.addAll(datas1);
        }
    }

    @Override
    public int getCount() {
        if(!isComment){
            return datas1.size();
        }else {
            return datas.size();
        }
    }

    @Override
    public Object getItem(int i) {
        if(!isComment){
            return datas1.get(i);
        }else {
            return datas.get(i);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false);
        }
        CircleImageView img = (CircleImageView) view.findViewById(R.id.user_img);
        TextView name = (TextView) view.findViewById(R.id.user_name);
        TextView date = (TextView) view.findViewById(R.id.txt_date);
        TextView content = (TextView) view.findViewById(R.id.txt_comment);
        UserInfoEntity user = null;

        if(isComment){
            CommentItemEntity item = (CommentItemEntity) getItem(position);
            if(item !=null){
                user = item.getUser();
                date.setText(item.getDate());
                content.setText(item.getContent());
            }
        }else {
            user = (UserInfoEntity) getItem(position);
        }

        if(user!= null){
            new ImageDownloader.Builder()
                    .setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC)
                    .build(user.getAvatar(), img)
                    .start();
            name.setText(user.getName());
        }

        if(!isComment){
            date.setVisibility(View.GONE);
            content.setVisibility(View.GONE);
        }else {
            date.setVisibility(View.VISIBLE);
            content.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
