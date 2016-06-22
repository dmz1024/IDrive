package com.ccpress.izijia.adapter;

import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleExpandAdapter;
import com.ccpress.izijia.R;
import com.ccpress.izijia.vo.CollectViewChildVo;
import com.ccpress.izijia.vo.CollectViewPvo;

public class CollectViewAdapter extends
		SimpleExpandAdapter<CollectViewPvo, CollectViewChildVo> {

	public CollectViewAdapter(BaseActivity activity,
			List<CollectViewPvo> parentData, int parentResId,
			int childResId) {
		super(activity, ParentViewHolder.class, ChildViewHolder.class,
				R.id.class, parentData, parentResId, childResId);
	}

	@Override
	public void doParentExtra(View convertView,
			CollectViewPvo item, int groupPosition,
			boolean isExpanded) {
		ParentViewHolder h = (ParentViewHolder) parentHolder;
		if(isExpanded){
			h.expand_image.setImageResource(R.drawable.expand_true);
		}else{
			h.expand_image.setImageResource(R.drawable.expand_false);
		}
		h.expand_title.setText(item.getTitle());
	}

	@Override
	public void doChildExtra(View convertView, CollectViewChildVo item,
			int groupPosition, int childPostion, boolean isLastChild) {
		ChildViewHolder h = (ChildViewHolder) childHolder;
		h.expand_title.setText(item.getContent());
	}

	public static class ParentViewHolder {
		ImageView expand_image;
		TextView expand_title;
	}

	public static class ChildViewHolder {
		TextView expand_title;
	}
}
