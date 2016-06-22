package com.ccpress.izijia.adapter;

import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.froyo.commonjar.view.CircleImageView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.vo.MyMsgVo;

public class MyMsgAdapter extends SimpleAdapter<MyMsgVo> {

	public MyMsgAdapter(List<MyMsgVo> data, BaseActivity activity, int layoutId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
	}

	@Override
	public void doExtra(View convertView, MyMsgVo item, int position) {
		ViewHolder h = (ViewHolder) holder;
		h.iv_header.setImageUrl(item.getTo_user_avatar(), imageLoader);
		h.tv_content.setText(item.getMessage());
		h.tv_title.setText(item.getTo_user_nickname());
		if("0".equals(item.getHas_new())){
			h.iv_has_msg.setVisibility(View.GONE);
		}else{
			h.iv_has_msg.setVisibility(View.VISIBLE);
		}
	}

	public static class ViewHolder {
		RelativeLayout rl_content;
		
		CircleImageView iv_header;
		
		TextView tv_title;

		TextView tv_content;
		
		ImageView iv_has_msg;
	}
}
