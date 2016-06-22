package com.ccpress.izijia.adapter;

import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;
import com.android.volley.toolbox.NetworkImageView;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.*;
import com.ccpress.izijia.vo.TrendVo;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.view.CircleImageView;
import com.trs.types.ListItem;
import com.trs.util.StringUtil;

/**
 * 
 * @Des: 个人信息
 * @author Rhino 
 * @version V1.0 
 * @created  2015年6月16日 上午9:11:32
 */
public class InfoAdapter extends SimpleAdapter<TrendVo> {

	public InfoAdapter(List<TrendVo> data, BaseActivity activity, int layoutId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
	}

	@Override
	public void doExtra(View convertView, final TrendVo item, int position) {
	
		ViewHolder h=(ViewHolder) holder;
		
		h.iv_header.setImageUrl(item.getUser().getAvatar(), imageLoader);
		h.tv_name.setText(item.getUser().getName());
		if(!Utils.isEmpty(item.getImage())){
			h.iv_image.setImageUrl(item.getImage().get(0).getPic(), imageLoader);
		}
		
		if("1".equals(item.getUser().getSex())){
			h.iv_sex.setImageResource(R.drawable.icon_male);
		}else{
			h.iv_sex.setImageResource(R.drawable.icon_female);
		}
		h.tv_content.setText(item.getTitle());
		h.tv_desc.setText(item.getContent());
		h.tv_comment.setText(item.getComment());
		h.tv_prise.setText(item.getLike());
		h.tv_share.setText(item.getShare());
		h.tv_time.setText(item.getTime());

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
					if(item != null && !StringUtil.isEmpty(item.getType())){
						if(item.getType().equals(Constant.DETAIL_TYPE_LINE_UPLOAD)){//自建线路
							Intent intent = new Intent(activity, LinesDetailUserUploadActivity.class);
							intent.putExtra(LinesDetailUserUploadActivity.EXTRA_LID, item.getDocid());
							activity.startActivity(intent);
						}else if (item.getType().equals(Constant.DETAIL_TYPE_IMAGES) || item.getType().equals(Constant.DETAIL_TYPE_VIDEO)){//图片组-11   小视频-14
							Intent intent = new Intent(activity, InfoDetailActivity.class);
							intent.putExtra(InfoDetailActivity.EXTRA_DOCID, item.getDocid());
							intent.putExtra(InfoDetailActivity.EXTRA_URL, item.getUrl());
							intent.putExtra(InfoDetailActivity.EXTRA_DETAIL_TYPE, item.getType());
							activity.startActivity(intent);
						}else if (item.getType().equals(Constant.HOME_TYPE_DRIVE)){//首页目的地、线路、精选文章
							Intent intent = new Intent(activity, WebViewActivity.class);
							intent.putExtra("url", item.getUrl());
							activity.startActivity(intent);
						} else if(item.getType().equals(Constant.DETAIL_TYPE_LINE)){//常规线路
							Intent intent = new Intent(activity, LinesDetailUserUploadActivity.class);
							intent.putExtra(LinesDetailUserUploadActivity.EXTRA_LID, item.getDocid());
							intent.putExtra(LinesDetailUserUploadActivity.EXTRA_ACT_TYPE, LinesDetailUserUploadActivity.OfficialLines);
							activity.startActivity(intent);
						}else if(item.getType().equals(Constant.DETAIL_TYPE_DES)){//目的地
							Intent intent = new Intent(activity, LinesDetailUserUploadActivity.class);
							intent.putExtra(LinesDetailUserUploadActivity.EXTRA_ACT_TYPE, LinesDetailUserUploadActivity.Destination);
							intent.putExtra(LinesDetailUserUploadActivity.EXTRA_LID, item.getDocid());
							activity.startActivity(intent);
						}else if(item.getType().equals(Constant.DETAIL_TYPE_ViewSpot)){//看点
							Intent intent = new Intent(activity, ViewSpotDetailActivity.class);
							intent.putExtra(LinesDetailImageTextActivity.EXTRA_LID, item.getDocid());
							activity.startActivity(intent);
						}else if(item.getType().equals(Constant.DETAIL_TYPE_PARK)){//停车发呆地
							Intent intent = new Intent(activity, ViewSpotDetailActivity.class);
							intent.putExtra(ViewSpotDetailActivity.EXTRA_TYPE, 1);
							intent.putExtra(LinesDetailImageTextActivity.EXTRA_LID, item.getDocid());
							activity.startActivity(intent);
						}else if(item.getType().equals(Constant.DETAIL_TYPE_DRIVE)){//手机自驾团
							Intent intent = new Intent(activity, ChoosenDetailActivity.class);
							intent.putExtra(ChoosenDetailActivity.EXTRA_TYPE, 1);
							intent.putExtra(LinesDetailUserUploadActivity.EXTRA_LID, item.getDocid());
							activity.startActivity(intent);
					}
				}
			}
		});
	}

	public static class ViewHolder{
		
		CircleImageView iv_header;
		
		TextView tv_name;
		
		ImageView iv_sex;
		
		
		TextView tv_content;
		
		NetworkImageView iv_image;
		
		TextView tv_desc;
		
		TextView tv_prise;
		
		TextView tv_comment;
		
		TextView tv_share;
		
		TextView tv_time;
		 
	}
}
