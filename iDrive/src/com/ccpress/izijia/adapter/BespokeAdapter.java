package com.ccpress.izijia.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.R;
import com.froyo.commonjar.network.BitmapCache;
import com.trs.util.log.Log;
import com.ccpress.izijia.activity.LinesDetailImageTextActivity;
import com.ccpress.izijia.activity.ViewSpotDetailActivity;
import com.ccpress.izijia.vo.BespokeVo;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @Des: 看点
 * @author Rhino
 * @version V1.0
 * @created 2015年6月16日 上午9:11:32
 */
public class BespokeAdapter extends SimpleAdapter<BespokeVo> {
	public static String BPA="BPA";
	public BespokeAdapter(List<BespokeVo> data, BaseActivity activity,
			int layoutId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
	}
	ImageLoader imageLoader;
	@Override
	public void doExtra(View convertView, final BespokeVo item,
			final int position) {
		final ViewHolder h = (ViewHolder) holder;

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
		h.tv_title.setText(item.getName());
		h.ll_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/*if (BPA.equals("MystyleActivity")||BPA.equals("")){
					// activity.skip(BespokeDetailActivity.class,item);
					Intent intent = new Intent(activity, ViewSpotDetailActivity.class);
					intent.putExtra(LinesDetailImageTextActivity.EXTRA_LID, item.getSpotid());
					activity.startActivity(intent);
				}*/

			}
		});

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

	public static class ViewHolder {
		ImageView iv_type;
		TextView tv_title;
		CheckBox cb_selected;
		RelativeLayout ll_content;
	}

	public String getSelectIds() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getDataSource().size(); i++) {
			BespokeVo vo = getDataSource().get(i);
			if (vo.isCheck()) {
				sb.append(vo.getId() + ",");
			}
		}
		if (sb.length() == 0) {
			return "";
		}
		return sb.toString().substring(0, sb.toString().length() - 1);
	}

	public List<BespokeVo> getSelectView() {
		List<BespokeVo> temp = new ArrayList<BespokeVo>();
		for (int i = 0; i < getDataSource().size(); i++) {
			BespokeVo vo = getDataSource().get(i);
			if (vo.isCheck()) {
				temp.add(vo);
			}
		}
		return temp;
	}

	public void doDelete() {
		Iterator<BespokeVo> it = getDataSource().iterator();
		while (it.hasNext()) {
			BespokeVo value = it.next();
			if (value.isCheck()) {
				it.remove();
			}
		}
	}

	public void showAll() {
		List<BespokeVo> tempData = new ArrayList<BespokeVo>();
		for (int i = 0; i < getDataSource().size(); i++) {
			BespokeVo vo = getDataSource().get(i);
			vo.setShown(true);
			tempData.add(vo);
		}
		reload(tempData);
	}

	public void hideAll() {
		List<BespokeVo> tempData = new ArrayList<BespokeVo>();
		for (int i = 0; i < getDataSource().size(); i++) {
			BespokeVo vo = getDataSource().get(i);
			//vo.setShown(false);
			tempData.add(vo);
		}
		reload(tempData);
	}

}
