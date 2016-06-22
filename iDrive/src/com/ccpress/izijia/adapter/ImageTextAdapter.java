package com.ccpress.izijia.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.InfoMapActivity;
import com.ccpress.izijia.activity.LinesDetailImageTextActivity;
import com.ccpress.izijia.activity.LinesDetailUserUploadActivity;
import com.ccpress.izijia.activity.ViewSpotDetailActivity;
import com.ccpress.izijia.entity.LinesDetailUploadEntity;
import com.ccpress.izijia.util.*;
import com.trs.util.ImageDownloader;
import com.trs.util.StringUtil;

import java.util.ArrayList;

/**
 * Created by WLH on 2015/5/20 17:38.
 * 目的地详情，线路详情图文形式的列表对应的adapter
 */
public class ImageTextAdapter extends BaseAdapter{

    public static final int Positive = 1;
    public static final int Negative = 0;

    private ArrayList<LinesDetailUploadEntity.ImageText> data = new ArrayList<LinesDetailUploadEntity.ImageText>();
    private ArrayList<LinesDetailUploadEntity.ViewSpot> data_Viewport = new ArrayList<LinesDetailUploadEntity.ViewSpot>();
    private ArrayList<LinesDetailUploadEntity.Hotel> data_Hotel = new ArrayList<LinesDetailUploadEntity.Hotel>();//旅馆和餐厅公用一个

    private Context mContext;

    private boolean isViewSport = false;//1:看点 0：非看点 默认非看点
    private int collect_Type = 0;//0:带有收藏图标的 1：没有收藏图标 默认带有收藏
    private boolean isHotel = false;//是否是旅馆和特产购物
    private boolean canCustom = false;//是否可以加入定制




    public ImageTextAdapter(Context mContext){
        this.mContext = mContext;
    }

    public void setData(ArrayList<LinesDetailUploadEntity.ImageText> data){
        this.data.clear();
        this.data.addAll(data);
    }

    public void setData_Viewport(ArrayList<LinesDetailUploadEntity.ViewSpot> data_Viewport){
        this.data_Viewport.clear();
        this.data_Viewport.addAll(data_Viewport);
    }

    public void setData_Hotel(ArrayList<LinesDetailUploadEntity.Hotel> data_Hotel){
        this.data_Hotel.clear();
        this.data_Hotel.addAll(data_Hotel);
    }

    public void setType(int collect_Type){
        this.collect_Type = collect_Type;
    }

    public void setIsViewSport(boolean isViewSport){
        this.isViewSport  = isViewSport;
    }

    public void setIsHotel(boolean isHotel){
        this.isHotel = isHotel;
    }

    public void setCanCustom(boolean canCustom){
        this.canCustom = canCustom;
    }

    @Override
    public int getCount() {

        if(isViewSport){
            if(data_Viewport != null){
                return  data_Viewport.size();
            }
        }else if(isHotel){
            if(data_Hotel != null){
                return data_Hotel.size();
            }
        }else {
            if(data!= null){
                return data.size();
            }
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if(isViewSport){
            if(data_Viewport != null){
                return  data_Viewport.get(i);
            }
        }else if(isHotel){
            if(data_Hotel != null){
                return data_Hotel.get(i);
            }
        }else {
            if(data!= null){
                return data.get(i);
            }
        }
        return null;

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        final ViewHolder holder;
        if(convertView != null){
            holder = (ViewHolder) convertView.getTag();
        }else{
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_viewport, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }


        int width = (int) (ScreenUtil.getScreenWidth(mContext)- 2 * mContext.getResources().getDimension(R.dimen.size8));
        holder.mImage.setLayoutParams(new LinearLayout.LayoutParams(width, (int)width*2/3));
        if(collect_Type == Negative){
            holder.mImgCollect.setVisibility(View.GONE);
        }

        if(canCustom){
            holder. mImgCart.setVisibility(View.VISIBLE);
        }

        String spotid = "";
        String detailType="";
        String isCart = "0";
        String isFavourite = "0";

        if(isViewSport){
            detailType = Constant.DETAIL_TYPE_ViewSpot;
            LinesDetailUploadEntity.ViewSpot item = (LinesDetailUploadEntity.ViewSpot) getItem(position);
            if (item != null){
                spotid = item.getSpotid();
                isCart = item.getIsCart();
                isFavourite = item.getIsFavorite();

                holder.mTxtSpotTitle.setText(item.getName());
                if(!StringUtil.isEmpty(item.getImagae())){
                    holder.mImage.setVisibility(View.VISIBLE);
                    new ImageDownloader.Builder()
                            .setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC)
                            .build(item.getImagae(), holder.mImage)
                            .start();
                }else {
                    holder.mImage.setVisibility(View.GONE);
                }

            }
            holder.mTxtSpotDesc.setVisibility(View.GONE);
        }else {
            if(isHotel){
                holder.mImgCollect.setVisibility(View.GONE);
                LinesDetailUploadEntity.Hotel item = (LinesDetailUploadEntity.Hotel) getItem(position);

                if (item != null){
                    if(!StringUtil.isEmpty(item.getName())){
                        holder.mTxtSpotTitle.setVisibility(View.VISIBLE);
                        holder.mTxtSpotTitle.setText(item.getName());
                    }else {
                        holder.mTxtSpotTitle.setVisibility(View.GONE);
                    }
                    holder.mTxtSpotDesc.setText(item.getDesc());
                    if(!StringUtil.isEmpty(item.getImage())){
                        holder.mImage.setVisibility(View.VISIBLE);
                        new ImageDownloader.Builder()
                                .setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC)
                                .build(item.getImage(), holder.mImage)
                                .start();
                    }else {
                        holder.mImage.setVisibility(View.GONE);
                    }
                }
            }else {
                if(collect_Type == Positive){
                    detailType = Constant.DETAIL_TYPE_PARK;
                }
                holder.mTxtSpotDesc.setVisibility(View.VISIBLE);
                LinesDetailUploadEntity.ImageText item = (LinesDetailUploadEntity.ImageText) getItem(position);
                if (item != null){
                    spotid = item.getPid();
                    isCart = item.getIsCart();
                    isFavourite = item.getIsFavorite();
                    if(!StringUtil.isEmpty(item.getName())){
                        holder.mTxtSpotTitle.setVisibility(View.VISIBLE);
                        holder.mTxtSpotTitle.setText(item.getName());
                    }else {
                        holder.mTxtSpotTitle.setVisibility(View.GONE);
                    }

                    if(!StringUtil.isEmpty(item.getImage())){
                        holder.mImage.setVisibility(View.VISIBLE);
                        new ImageDownloader.Builder()
                                .setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC)
                                .build(item.getImage(), holder.mImage)
                                .start();
                    }else {
                        holder.mImage.setVisibility(View.GONE);
                    }
                    holder.mTxtSpotDesc.setText(item.getDesc());
                }
            }
        }


        final String finalSpotid = spotid;

        final DetailStatusUtil statusUtil = new DetailStatusUtil(mContext);
        if(isCart.equals("1")){
            holder.mImgCart.setImageResource(R.drawable.icon_customed);
            statusUtil.setIsCustom(true);
        }else {
            holder.mImgCart.setImageResource(R.drawable.icon_cart);
            statusUtil.setIsCustom(false);
        }
        if(isFavourite.equals("1")){
            holder.mImgCollect.setImageResource(R.drawable.icon_collected);
            statusUtil.setIsFavorite(true);
        }else {
            holder.mImgCollect.setImageResource(R.drawable.icon_collect_gray);
            statusUtil.setIsFavorite(false);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((LinesDetailUserUploadActivity.EXTRA_MY_TYPE+"AA").equals("13AA")){
                    LinesDetailUploadEntity.ViewSpot  itemV = (LinesDetailUploadEntity.ViewSpot) getItem(position);
                    if(itemV.getType().equals("1")){//看点
                        Intent intent = new Intent(mContext, ViewSpotDetailActivity.class);
                        //intent.putExtra(ViewSpotDetailActivity.EXTRA_TYPE, 0);
                        intent.putExtra(LinesDetailImageTextActivity.EXTRA_LID, itemV.getSpotid());
                        mContext.startActivity(intent);
                    }else if(itemV.getType().equals("2")){//停车发呆地
                        Intent intent = new Intent(mContext, ViewSpotDetailActivity.class);
                        intent.putExtra(ViewSpotDetailActivity.EXTRA_TYPE, 1);
                        intent.putExtra(LinesDetailImageTextActivity.EXTRA_LID, itemV.getSpotid());
                        mContext.startActivity(intent);
                    }else if(itemV.getType().equals("3")){
                        Intent intent = new Intent(mContext, InfoMapActivity.class);
                        intent.putExtra("name", itemV.getName());
                        intent.putExtra("addr", itemV.getDesc());
                        intent.putExtra("geo", itemV.getGeo());
                        intent.putExtra("lid",itemV.getId());
                        intent.putExtra("detailType","3");
                        intent.putExtra(InfoMapActivity.EXTRA_MYCOLLECT, false);
                        mContext.startActivity(intent);
                    }else if(itemV.getType().equals("4")) {
                        Intent intent = new Intent(mContext, InfoMapActivity.class);
                        intent.putExtra("name", itemV.getName());
                        intent.putExtra("addr", itemV.getDesc());
                        intent.putExtra("geo", itemV.getGeo());
                        intent.putExtra("lid", itemV.getId());
                        intent.putExtra("detailType", "4");
                        intent.putExtra(InfoMapActivity.EXTRA_MYCOLLECT, false);
                        mContext.startActivity(intent);
                    }
                }else {
                    if(isViewSport){//看点详情
                        Intent intent = new Intent(mContext, ViewSpotDetailActivity.class);
                        intent.putExtra(LinesDetailImageTextActivity.EXTRA_LID, finalSpotid);
                        mContext.startActivity(intent);
                    }else if(!isHotel && collect_Type == Positive){//停车发呆地详情
                        Intent intent = new Intent(mContext, ViewSpotDetailActivity.class);
                        intent.putExtra(ViewSpotDetailActivity.EXTRA_TYPE, 1);
                        intent.putExtra(LinesDetailImageTextActivity.EXTRA_LID, finalSpotid);
                        mContext.startActivity(intent);
                    }
                }
            }
        });



        /**/
        final String finalDetailType = detailType;
        holder.mImgCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CollectUtil.CollectOrCancel(mContext, finalSpotid, finalDetailType,
                        statusUtil.IsFavorite(), new PraiseUtil.ResultCallback() {
                            @Override
                            public void callback(boolean isSuccess) {
                                if (isSuccess) {
                                    if (statusUtil.IsFavorite()) {
                                        statusUtil.setIsFavorite(false);
                                        holder.mImgCollect.setImageResource(R.drawable.icon_collect_gray);
                                    } else {
                                        statusUtil.setIsFavorite(true);
                                        holder.mImgCollect.setImageResource(R.drawable.icon_collected);
                                    }
                                }
                            }
                        });
            }
        });

        holder.mImgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomUtil.CustomOrCancel(mContext, finalSpotid, finalDetailType,
                        statusUtil.IsCustom(), new PraiseUtil.ResultCallback() {
                            @Override
                            public void callback(boolean isSuccess) {
                                if (isSuccess) {
                                    if (statusUtil.IsCustom()) {
                                        statusUtil.setIsCustom(false);
                                        holder.mImgCart.setImageResource(R.drawable.icon_cart);
                                    } else {
                                        statusUtil.setIsCustom(true);
                                        holder.mImgCart.setImageResource(R.drawable.icon_customed);
                                    }
                                }
                            }
                        });
            }
        });

        return convertView;
    }


    private class ViewHolder{
        TextView mTxtSpotTitle;
        ImageView mImgCollect ;
        ImageView mImgCart;
        ImageView mImage;
        TextView mTxtSpotDesc;

        boolean isFavorite;
        boolean isCustom;

        public ViewHolder(View view){
            mTxtSpotTitle = (TextView) view.findViewById(R.id.name_viewport_item);
            mImgCollect= (ImageView) view.findViewById(R.id.collect_image);
            mImgCart= (ImageView) view.findViewById(R.id.collect_cart);
            mImage= (ImageView) view.findViewById(R.id.img_viewport_item);
            mTxtSpotDesc= (TextView) view.findViewById(R.id.desc_viewport_item);
        }
    }
}
