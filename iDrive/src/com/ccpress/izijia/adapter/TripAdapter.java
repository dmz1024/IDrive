package com.ccpress.izijia.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;

import android.widget.HorizontalScrollView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import android.widget.Toast;
import com.android.volley.toolbox.NetworkImageView;
import com.ccpress.izijia.activity.line.ListPhotoActivity;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.froyo.commonjar.utils.AppUtils;
import com.ccpress.izijia.R;
import com.ccpress.izijia.vo.LineDetailVo.Travel;
import com.ccpress.izijia.vo.LineDetailVo.Travel.Images;
import com.trs.util.log.Log;
/**
 * 我的线路游记
 * 
 * @author wangyi
 * 
 */
public class TripAdapter extends SimpleAdapter<Travel> {

	public TripAdapter(List<Travel> data, BaseActivity activity) {
		super(data, activity, R.layout.item_trip, ViewHolder.class, R.id.class);

	}
	private List<String> arraylist= new ArrayList<>();
	@Override
	public void doExtra(View convertView, final Travel item, final int position) {
		final ViewHolder h = (ViewHolder) holder;

		h.tv_month.setText(item.getDate());
		int width = (AppUtils.getWidth(activity) - 20) / 3;
		LayoutParams lp = new LayoutParams(width, width);
		lp.rightMargin = 10;
		h.ll_child.removeAllViews();
		for (int i = 0; i < item.getImages().size(); i++) {
			final Images pic = item.getImages().get(i);
			NetworkImageView child=new NetworkImageView(activity);
			child.setLayoutParams(lp);
			child.setScaleType(ScaleType.FIT_XY);
			child.setImageUrl(pic.getImage(), imageLoader);
			h.ll_child.addView(child);
		}

		h.ll_child.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				arraylist.clear();
				for (int i = 0; i < item.getImages().size(); i++) {
					final Images pic = item.getImages().get(i);
					arraylist.add(pic.getImage());
				}

				ListPhotoActivity.list=arraylist;
				Intent inter=new Intent(activity,ListPhotoActivity.class);
				activity.startActivity(inter);
			}
		});
	}

	public static class ViewHolder {
		TextView tv_month;
		// NetworkImageView iv_1;
		// NetworkImageView iv_2;
		// NetworkImageView iv_3;
	//	HorizontalScrollView hs_container;
		LinearLayout ll_child;
	}
}
