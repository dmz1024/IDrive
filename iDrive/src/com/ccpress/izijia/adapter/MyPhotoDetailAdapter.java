package com.ccpress.izijia.adapter;

import java.util.List;

import android.view.View;
import android.widget.CheckBox;

import com.android.volley.toolbox.NetworkImageView;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.GridSimpleAdapter;
import com.ccpress.izijia.R;
import com.ccpress.izijia.vo.MyPhotoVo.Pic;

/**
 * 收藏--照片
 * 
 * @author wangyi
 * 
 */
public class MyPhotoDetailAdapter extends GridSimpleAdapter<Pic> {

	public MyPhotoDetailAdapter(List<Pic> data, BaseActivity activity,
			int layoutId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
	}

	@Override
	public void doExtra(View convertView, Pic item, int position) {
		ViewHolder h = (ViewHolder) holder;
		h.iv_header.setImageUrl(item.getPic_path(), imageLoader);
		h.cb_selected.setVisibility(View.GONE);
	}

	@Override
	public int setColumns() {
		return 3;
	}

	public static class ViewHolder {
		NetworkImageView iv_header;
		CheckBox cb_selected;
	}
}
