package com.ccpress.izijia.adapter;

import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.view.CircleImageView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.vo.MyAttentionVo;
/**
 * 
 * @Des: 我的关注列表
 * @author Rhino 
 * @version V1.0 
 * @created  2015年6月6日 下午4:40:02
 */
public class MyAttentionAdapter extends SimpleAdapter<MyAttentionVo> {

	
	public MyAttentionAdapter(List<MyAttentionVo> data, BaseActivity activity, int layoutId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
	}

	@Override
	public void doExtra(View convertView, MyAttentionVo item, int position) {
		ViewHolder h = (ViewHolder) holder;
		h.iv_header.setImageUrl(item.getUser().getAvatar(), imageLoader);
		h.tv_title.setText(item.getUser().getNickname());
		h.tv_content.setText(Utils.isEmpty(item.getUser().getSignature())?"未填写":item.getUser().getSignature());
		if("1".equals(item.getUser().getSex())){
			h.iv_sex.setImageResource(R.drawable.icon_female);
		}else{
			h.iv_sex.setImageResource(R.drawable.icon_male);
		}
	}

	public static class ViewHolder {
		RelativeLayout rl_content;
		
		TextView tv_title;

		TextView tv_content;
		
		ImageView iv_sex;
		
		CircleImageView iv_header;
	}
}
