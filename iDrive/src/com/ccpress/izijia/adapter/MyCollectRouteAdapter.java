package com.ccpress.izijia.adapter;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.LinesDetailUserUploadActivity;
import com.ccpress.izijia.vo.CollectRouteVo;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.froyo.commonjar.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 我的收藏--线路
 * 
 * @author wangyi
 * 
 */
public class MyCollectRouteAdapter extends SimpleAdapter<CollectRouteVo> {

	public MyCollectRouteAdapter(List<CollectRouteVo> data,
			BaseActivity activity, int layoutId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
	}

	@Override
	public void doExtra(View convertView, final CollectRouteVo item,
			final int position) {
		final ViewHolder h = (ViewHolder) holder;
		h.tv_title.setText(item.getTitle());
		h.tv_content.setText(item.getLine());
		h.tv_content.setVisibility(View.GONE);
		if (Utils.isEmpty(item.getImage())) {
			h.iv_picture.setVisibility(View.GONE);
		} else {
			h.iv_picture.setVisibility(View.VISIBLE);
			h.iv_picture.setImageUrl(item.getImage(), imageLoader);
		}
		h.cb_selected.setVisibility(View.GONE);
		if (item.isCheck()) {
			h.cb_selected.setChecked(true);
		} else {
			h.cb_selected.setChecked(false);
		}
		if (item.isShown()) {
			h.cb_selected.setVisibility(View.VISIBLE);
		} else {
			h.cb_selected.setVisibility(View.GONE);
		}
		h.cb_selected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!h.cb_selected.isChecked()) {
					h.cb_selected.setChecked(false);
					item.setCheck(false);
				} else {
					h.cb_selected.setChecked(true);
					item.setCheck(true);
				}
			}
		});

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(activity, LinesDetailUserUploadActivity.class);
				intent.putExtra(LinesDetailUserUploadActivity.EXTRA_LID, item.getLid());
				if (item.getType().equals("1")){
					intent.putExtra(LinesDetailUserUploadActivity.EXTRA_ACT_TYPE,1);
				}else {
					intent.putExtra(LinesDetailUserUploadActivity.EXTRA_ACT_TYPE,"2");
				}

				activity.startActivity(intent);
			}
		});

	}

	public static class ViewHolder {
		CheckBox cb_selected;

		TextView tv_title;

		TextView tv_content;

		NetworkImageView iv_picture;
	}

	public String getSelectIds() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getDataSource().size(); i++) {
			CollectRouteVo vo = getDataSource().get(i);
			if (vo.isCheck()) {
				sb.append(vo.getId() + ",");
			}
		}
		if (sb.length() == 0) {
			return "";
		}
		return sb.toString().substring(0, sb.toString().length() - 1);
	}

	public void doDelete() {
		Iterator<CollectRouteVo> it = getDataSource().iterator();
		while (it.hasNext()) {
			CollectRouteVo value = it.next();
			if (value.isCheck()) {
				it.remove();
			}
		}
	}

	public void showAll() {
		List<CollectRouteVo> tempData = new ArrayList<CollectRouteVo>();
		for (int i = 0; i < getDataSource().size(); i++) {
			CollectRouteVo vo = getDataSource().get(i);
			vo.setShown(true);
			tempData.add(vo);
		}
		reload(tempData);
	}

	public void hideAll() {
		List<CollectRouteVo> tempData = new ArrayList<CollectRouteVo>();
		for (int i = 0; i < getDataSource().size(); i++) {
			CollectRouteVo vo = getDataSource().get(i);
			vo.setShown(false);
			tempData.add(vo);
		}
		reload(tempData);
	}
}
