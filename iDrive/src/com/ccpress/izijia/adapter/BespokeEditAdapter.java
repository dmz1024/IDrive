package com.ccpress.izijia.adapter;

import java.util.List;


import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.line.LineEditActivity;
import com.ccpress.izijia.activity.mystyle.EditLineActivity;

import com.ccpress.izijia.view.DragListView;
import com.ccpress.izijia.vo.BespokeVo;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.froyo.commonjar.utils.Utils;

import de.greenrobot.event.EventBus;

/**
 * 
 * @Des: 看点--编辑
 * @author Rhino
 * @version V1.0
 * @created 2015年6月16日 上午9:11:32
 */
public class 	BespokeEditAdapter extends SimpleAdapter<BespokeVo> {

	private String deleteIds = "";
	public  static String EBS="EventBus";
	private DragListView listView;

	public BespokeEditAdapter(List<BespokeVo> data, BaseActivity activity,
							  int layoutId, DragListView listView) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
		this.listView = listView;

	}

	@Override
	public void doExtra(View convertView, final BespokeVo item,
			final int position) {
		ViewHolder h = (ViewHolder) holder;
		// 1.看点，2.停车发呆地，3.高德地图点，4.私享点，5.自建线路，6.网站线路
		if (item.getType() == 1) {
			h.iv_type.setImageResource(R.drawable.icon_senic_spot);
		} else if (item.getType() == 2) {
			h.iv_type.setImageResource(R.drawable.icon_fadai);
		} else if (item.getType() == 3) {
			h.iv_type.setImageResource(R.drawable.icon_info_map);
		} else if (item.getType() == 4) {
			h.iv_type.setImageResource(R.drawable.icon_hotel);

		} else if (item.getType() == 5) {
			h.iv_type.setImageResource(R.drawable.icon_line);
		} else if (item.getType() == 6) {
			h.iv_type.setImageResource(R.drawable.icon_scenic_spot);
		}
		h.cb_selected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (Utils.isEmpty(deleteIds)) {
					deleteIds = item.getSoptid();
				} else {
					deleteIds = deleteIds + "," + item.getSoptid();
				}
				removeByPos(position);
				listView.stopDrag();
				if (EBS.equals("EditLineActivity")){
					EventBus.getDefault().post(new EditLineActivity.GetAddViewPointsEvent());
				}
				if (EBS.equals("LineEditActivity")){
					EventBus.getDefault().post(new LineEditActivity.GetAddViewPointsEvent());
				}
			}
		});
		h.tv_title.setText(item.getName());

	}

	public String getDeleteIds() {
		return deleteIds;
	}

	public static class ViewHolder {
		ImageView iv_type;

		TextView tv_title;

		ImageView cb_selected;
		ImageView iv_yidong;

		RelativeLayout rl_item;
	}

}
