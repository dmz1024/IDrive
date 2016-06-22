package com.ccpress.izijia.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.volley.toolbox.NetworkImageView;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.GridSimpleAdapter;
import com.ccpress.izijia.R;
import com.ccpress.izijia.vo.CollectPhotoVo;

/**
 * 收藏--照片
 * 
 * @author wangyi
 * 
 */
public class CollectPhotoAdapter extends GridSimpleAdapter<CollectPhotoVo> {

	public CollectPhotoAdapter(List<CollectPhotoVo> data,
			BaseActivity activity, int layoutId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
	}

	@Override
	public void doExtra(View convertView, final CollectPhotoVo item,
			final int position) {
		final ViewHolder h = (ViewHolder) holder;
		h.iv_header.setImageUrl(item.getImage(), imageLoader);
		if (item.isShown()) {
			h.cb_selected.setVisibility(View.VISIBLE);
		} else {
			h.cb_selected.setVisibility(View.GONE);
		}
		if (item.isCheck()) {
			h.cb_selected.setChecked(true);
		} else {
			h.cb_selected.setChecked(false);
		}
		if (item.isCheck()) {
			h.cb_selected.setChecked(true);
		} else {
			h.cb_selected.setChecked(false);
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
	}

	@Override
	public int setColumns() {
		return 3;
	}

	public static class ViewHolder {
		NetworkImageView iv_header;
		CheckBox cb_selected;
	}

	public String getSelectIds() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getDataSource().size(); i++) {
			CollectPhotoVo vo = getDataSource().get(i);
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
		Iterator<CollectPhotoVo> it = getDataSource().iterator();
		while (it.hasNext()) {
			CollectPhotoVo value = it.next();
			if (value.isCheck()) {
				it.remove();
			}
		}
	}

	public void showAll() {
		List<CollectPhotoVo> tempData = new ArrayList<CollectPhotoVo>();
		for (int i = 0; i < getDataSource().size(); i++) {
			CollectPhotoVo vo = getDataSource().get(i);
			vo.setShown(true);
			tempData.add(vo);
		}
		reload(tempData);
	}

	public void hideAll() {
		List<CollectPhotoVo> tempData = new ArrayList<CollectPhotoVo>();
		for (int i = 0; i < getDataSource().size(); i++) {
			CollectPhotoVo vo = getDataSource().get(i);
			vo.setShown(false);
			tempData.add(vo);
		}
		reload(tempData);
	}
}
