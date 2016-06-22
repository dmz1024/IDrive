package com.ccpress.izijia.adapter;

import java.util.List;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trs.util.log.Log;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.froyo.commonjar.utils.AppUtils;
import com.froyo.commonjar.utils.SpUtil;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.view.CircleImageView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.vo.MsgDetailVo;

public class MsgDetailAdapter extends SimpleAdapter<MsgDetailVo> {

	private String uid;

	public MsgDetailAdapter(List<MsgDetailVo> data, BaseActivity activity,
			int layoutId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
		SpUtil sp = new SpUtil(activity);
		uid = sp.getStringValue(Const.UID);
	}

	@Override
	public void doExtra(View convertView, MsgDetailVo item, int position) {
		ViewHolder h = (ViewHolder) holder;
		if (uid.equals(item.getUid())) {
			item.setSend(true);
		}
		Log.e("time", "eee"+ item.getCreate_time()+ "ggg" + Utils.formatTime(item.getCreate_time() + ""));
		if (!item.isSend()) {

			RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
					AppUtils.dip2px(activity, 40),
					AppUtils.dip2px(activity, 40));
			lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			lp1.addRule(RelativeLayout.BELOW, R.id.tv_time);

			h.iv_avator.setLayoutParams(lp1);

			h.iv_avator.setImageUrl(item.getUser().getAvatar(), imageLoader);

			RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			lp3.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			lp3.addRule(RelativeLayout.RIGHT_OF, R.id.iv_avator);
			h.tv_time.setLayoutParams(lp3);
			h.tv_time.setPadding(28, 0, 0, 0);

			RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			lp2.addRule(RelativeLayout.RIGHT_OF, R.id.iv_avator);
			lp2.addRule(RelativeLayout.BELOW, R.id.tv_time);

			lp2.leftMargin = 8;
			lp2.topMargin = 12;
			h.tv_left.setLayoutParams(lp2);
			// 用户的反馈、回复
			h.tv_left.setGravity(Gravity.CENTER);
			h.tv_left.setTextColor(Color.parseColor("#333333"));
			h.tv_left.setBackgroundResource(R.drawable.icon_faq_left);
			h.tv_time.setText(Utils.formatTime(item.getCreate_time()*1000 + ""));
		} else {
			// 开发者的回复

			RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
					AppUtils.dip2px(activity, 40),
					AppUtils.dip2px(activity, 40));
			lp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			lp1.addRule(RelativeLayout.BELOW, R.id.tv_time);
			h.iv_avator.setLayoutParams(lp1);
			h.iv_avator.setImageUrl(item.getUser().getAvatar(), imageLoader);

			RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			lp3.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			lp3.addRule(RelativeLayout.LEFT_OF, R.id.iv_avator);
			h.tv_time.setLayoutParams(lp3);
			h.tv_time.setPadding(0, 0, 28, 0);

			RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			lp2.addRule(RelativeLayout.LEFT_OF, R.id.iv_avator);
			lp2.addRule(RelativeLayout.BELOW, R.id.tv_time);
			lp2.rightMargin = 8;
			lp2.topMargin = 12;
			h.tv_left.setLayoutParams(lp2);

			h.tv_left.setGravity(Gravity.CENTER);
			h.tv_left.setTextColor(Color.parseColor("#ffffff"));
			h.tv_left.setBackgroundResource(R.drawable.icon_faq_right);
		}

		h.tv_time.setText(Utils.formatTime(item.getCreate_time()*1000 + ""));
		h.tv_left.setText(item.getContent());
	}

	public static class ViewHolder {
		TextView tv_left;
		TextView tv_time;
		CircleImageView iv_avator;
	}
}
