package com.ccpress.izijia.adapter;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.ChoosenDetailActivity;
import com.ccpress.izijia.activity.LinesDetailUserUploadActivity;
import com.ccpress.izijia.vo.MyOrderVo;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.froyo.commonjar.utils.Utils;

import java.util.List;

/**
 * 我的订单
 * 
 * @author wangyi
 * 
 */
public class MyOrderAdapter extends SimpleAdapter<MyOrderVo> {

	public MyOrderAdapter(List<MyOrderVo> data, BaseActivity activity,
			int layoutId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
	}

	@Override
	public void doExtra(View convertView, final MyOrderVo item, int position) {
		ViewHolder h = (ViewHolder) holder;
		h.tv_title.setText(item.getTitle());
		h.tv_content.setText(item.getLine());
		if (Utils.isEmpty(item.getImage())) {
			h.iv_picture.setVisibility(View.GONE);
		} else {
			h.iv_picture.setVisibility(View.VISIBLE);
			h.iv_picture.setImageUrl(item.getImage(), imageLoader);
		}
		h.fl_container.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(activity, ChoosenDetailActivity.class);
				intent.putExtra(ChoosenDetailActivity.EXTRA_TYPE, 1);
				intent.putExtra(LinesDetailUserUploadActivity.EXTRA_LID, item.getId());
				activity.startActivity(intent);
			}
		});
		// if (item.isSelected()) {
		// h.cb_selected.setChecked(true);
		// } else {
		// h.cb_selected.setChecked(false);
		// }
		// if (item.isShown()) {
		// h.cb_selected.setVisibility(View.VISIBLE);
		// } else {
		// h.cb_selected.setVisibility(View.GONE);
		// }
		// h.cb_selected.setOnCheckedChangeListener(new
		// OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// if (arg1) {
		// item.setSelected(true);
		// } else {
		// item.setSelected(false);
		// }
		// }
		// });

	}

	public static class ViewHolder {
		CheckBox cb_selected;

		TextView tv_title;

		TextView tv_content;

		NetworkImageView iv_picture;
		
		FrameLayout fl_container;
	}
}
