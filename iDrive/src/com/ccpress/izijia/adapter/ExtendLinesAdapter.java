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
import com.ccpress.izijia.activity.LinesDetailUserUploadActivity;
import com.ccpress.izijia.entity.SummaryEntity;
import com.trs.util.ImageDownloader;

import java.util.ArrayList;

/**
 * Created by WLH on 2015/5/22 14:32.
 */
public class ExtendLinesAdapter extends BaseAdapter{
    private Context mContext;

    private ArrayList<SummaryEntity> dataList = new ArrayList<SummaryEntity>();

    public ExtendLinesAdapter(Context mContext){
        this.mContext = mContext;
    }

    public void setDataList(ArrayList<SummaryEntity> dataList){
        this.dataList.clear();
        this.dataList.addAll(dataList);
    }

    @Override
    public int getCount() {
        if(dataList!= null){
            return dataList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if(dataList!= null){
            return dataList.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = null;
        if(convertView != null ){
            view = convertView;
        }else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_extendline, parent, false);
        }

        SummaryEntity item = (SummaryEntity) getItem(position);

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView sub_title = (TextView) view.findViewById(R.id.sub_title);
        ImageView idrive_list_img = (ImageView) view.findViewById(R.id.idrive_list_img);

        title.setText(item.getLine());
        sub_title.setText(item.getTitle());

        new ImageDownloader.Builder().
                setOptionsType(ImageDownloader.OptionsType.TOP_PIC).
                build(item.getImage(), idrive_list_img).start();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, LinesDetailUserUploadActivity.class);
                intent.putExtra(LinesDetailUserUploadActivity.EXTRA_ACT_TYPE, LinesDetailUserUploadActivity.OfficialLines);
                intent.putExtra(LinesDetailUserUploadActivity.EXTRA_LID,dataList.get(position).getLid());
                mContext.startActivity(intent);
            }
        });
        return view;
    }
}
