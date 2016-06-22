package com.ccpress.izijia.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.ccpress.izijia.R;
import com.ccpress.izijia.vo.CollectPhotoVo;
import com.ccpress.izijia.vo.LineInfoVo;

public class LineAdapter extends SimpleAdapter<LineInfoVo> {

	public LineAdapter(List<LineInfoVo> data, BaseActivity activity,
			int layoutId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
	}

	@Override
	public void doExtra(View convertView, final LineInfoVo item,
			final int position) {
		final ViewHolder h = (ViewHolder) holder;
		h.tv_title.setText(item.getRoute_name());
		if (item.isSelected()) {
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
					item.setSelected(false);
				} else {
					h.cb_selected.setChecked(true);
					item.setSelected(true);
				}
			}
		});

		h.iv_picture.setImageUrl(item.getThumb(), imageLoader);
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
			LineInfoVo vo = getDataSource().get(i);
			if (vo.isSelected()) {
				sb.append(vo.getId() + ",");
			}
		}
		if (sb.length() == 0) {
			return "";
		}
		return sb.toString().substring(0, sb.toString().length() - 1);
	}

	public void doDelete() {

		Iterator<LineInfoVo> it = getDataSource().iterator();
		while (it.hasNext()) {
			LineInfoVo value = it.next();
			if (value.isSelected()) {
				it.remove();
			}
		}
	}

	public void showAll() {
		List<LineInfoVo> tempData = new ArrayList<LineInfoVo>();
		for (int i = 0; i < getDataSource().size(); i++) {
			LineInfoVo vo = getDataSource().get(i);
			vo.setShown(true);
			tempData.add(vo);
		}
		reload(tempData);
	}

	public void hideAll() {
		List<LineInfoVo> tempData = new ArrayList<LineInfoVo>();
		for (int i = 0; i < getDataSource().size(); i++) {
			LineInfoVo vo = getDataSource().get(i);
			vo.setShown(false);
			tempData.add(vo);
		}
		reload(tempData);
	}
}
