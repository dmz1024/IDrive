package com.ccpress.izijia.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.LinesDetailUserUploadActivity;
import com.ccpress.izijia.entity.CareChooseEntity;
import com.ccpress.izijia.util.DipPxUtil;
import com.trs.util.StringUtil;

import java.util.ArrayList;

/**
 * Created by WLH on 2015/6/15 15:19.
 */
public class ExpandableAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private int type;
    private ArrayList<String> group_lines  = new ArrayList<>();
    private ArrayList<String> childArray =  new ArrayList<>();

    private Drawable drawableLeft;
    private Drawable drawableRightDown;
    private Drawable drawableRightUp;

    public ExpandableAdapter(Context mContext, int type){
        this.mContext = mContext;
        this.type = type;
        drawableLeft = mContext.getResources().getDrawable(R.drawable.icon_detail);
        drawableLeft.setBounds(0, 0, DipPxUtil.dip2px(mContext, 12), DipPxUtil.dip2px(mContext, 14));
        drawableRightDown = mContext.getResources().getDrawable(R.drawable.icon_arrow_down);
        drawableRightDown.setBounds(0, 0, DipPxUtil.dip2px(mContext, 14), DipPxUtil.dip2px(mContext, 9));
        drawableRightUp = mContext.getResources().getDrawable(R.drawable.icon_up);
        drawableRightUp.setBounds(0, 0, DipPxUtil.dip2px(mContext, 14), DipPxUtil.dip2px(mContext, 9));

        if(this.type== LinesDetailUserUploadActivity.OfficialLines){
            group_lines.add(mContext.getResources().getString(R.string.line_desc));
            group_lines.add(mContext.getResources().getString(R.string.background));
            group_lines.add(mContext.getResources().getString(R.string.nature_ecology));
            group_lines.add(mContext.getResources().getString(R.string.best_travel_season));
            group_lines.add(mContext.getResources().getString(R.string.recommend_equip));
            group_lines.add(mContext.getResources().getString(R.string.special_tips));

        }else if(this.type== LinesDetailUserUploadActivity.Destination){
            group_lines.add(mContext.getResources().getString(R.string.city_temperament));
            group_lines.add(mContext.getResources().getString(R.string.history_culture));
            group_lines.add(mContext.getResources().getString(R.string.nature_form));
            group_lines.add(mContext.getResources().getString(R.string.best_travel_season));
        }


    }

    public void setGroupLines(ArrayList<CareChooseEntity.Extra> extras){
        if(extras != null){
            this.group_lines.clear();
            this.childArray.clear();
            for (int i=0; i< extras.size(); i++){

                if(!StringUtil.isEmpty(extras.get(i).getDesc())){
                    this.group_lines.add(extras.get(i).getTitle());
                    this.childArray.add(extras.get(i).getDesc());
                }
            }
            Log.e("setGroupLines","extras.size:"+extras.size() + " group_lines size:"+group_lines.size() +" childArray size:"+childArray.size());
            notifyDataSetChanged();
        }
    }

    @Override
    public int getGroupCount() {
        return group_lines.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(childArray .size() > 0){
            return 1;
        }
        return 0;
    }

    @Override
    public String getGroup(int groupPosition) {
        return group_lines.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return childArray.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view;

        if(convertView != null){
            view = convertView;
        }
        else{
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_lines_desc, parent, false);
        }

        TextView mTextTitle = (TextView) view.findViewById(R.id.title);
        if(isExpanded){
            mTextTitle.setCompoundDrawables(drawableLeft,null, drawableRightUp, null);
        }else {
            mTextTitle.setCompoundDrawables(drawableLeft,null, drawableRightDown, null);
        }
        mTextTitle.setText(getGroup(groupPosition));

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view;

        if(convertView != null){
            view = convertView;
        }
        else{
            view = LayoutInflater.from(mContext).inflate(R.layout.item_expandablelist_childview, parent, false);
        }
        TextView mTextTitle = (TextView) view.findViewById(R.id.title);
        if("咨询电话".equals(getGroup(groupPosition))){
            mTextTitle.setAutoLinkMask(Linkify.PHONE_NUMBERS);
        }
        else {
            mTextTitle.setAutoLinkMask(Linkify.MAP_ADDRESSES);
        }

        mTextTitle.setText(getChild(groupPosition, childPosition));
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
