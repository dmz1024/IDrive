package com.ccpress.izijia.adapter;

import java.util.List;

import android.view.View;

import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.ccpress.izijia.R;
import com.ccpress.izijia.vo.BespokeVo;

/**
 * 
 * @Des: 我的照片--详情---评论
 * @author Rhino 
 * @version V1.0 
 * @created  2015年6月16日 上午9:11:32
 */
public class PhotoDetaiCommentAdapter extends SimpleAdapter<BespokeVo> {

	public PhotoDetaiCommentAdapter(List<BespokeVo> data, BaseActivity activity, int layoutId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
	}

	@Override
	public void doExtra(View convertView, BespokeVo item, int position) {
		ViewHolder h=(ViewHolder) holder;
//		h.ll_content.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				activity.skip(BespokeDetailActivity.class);
//			}
//		});
	}

	public static class ViewHolder{
//		 ImageView iv_type;
//		 
//		 TextView tv_title;
//		 
//		 CheckBox cb_selected;
//		 RelativeLayout ll_content;
		 
	}
}
