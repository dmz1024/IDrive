package com.ccpress.izijia.adapter;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.toolbox.NetworkImageView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.InfoMapActivity;
import com.ccpress.izijia.activity.LinesDetailImageTextActivity;
import com.ccpress.izijia.activity.ViewSpotDetailActivity;
import com.ccpress.izijia.dfy.activity.DetailsActivity;
import com.ccpress.izijia.dfy.activity.OrderActivity;
import com.ccpress.izijia.vo.BespokeVo;
import com.ccpress.izijia.vo.CollectVo;
import com.ccpress.izijia.yd.activity.StoresInfoActivity;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.froyo.commonjar.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 我的收藏--看点，私享
 * 
 * @author wangyi
 * 
 */
public class MyCollectViewAdapter extends SimpleAdapter<CollectVo> {
	private int type=0;
	public MyCollectViewAdapter(List<CollectVo> data, BaseActivity activity,
			int layoutId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
	}

	public MyCollectViewAdapter(List<CollectVo> data, BaseActivity activity,
								int layoutId,int type) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
		this.type=type;
	}

	@Override
	public void doExtra(View convertView, final CollectVo item,
			final int position) {
		final ViewHolder h = (ViewHolder) holder;
		if (Utils.isEmpty(item.getImage())) {
			h.iv_header.setVisibility(View.GONE);
		} else {
			h.iv_header.setVisibility(View.VISIBLE);
			h.iv_header.setImageUrl(item.getImage(), imageLoader);
		}
		if(type==5){
			h.tv_title.setText(item.getTitle());
			item.setType(5);
		}else if(type==6){
			h.tv_title.setText(item.getTitle());
			item.setType(6);
		}
		else {
			h.tv_title.setText(item.getName());
		}

		h.tv_content.setText(item.getDesc());
		h.rl_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// activity.skip(CollectViewActivity.class);
//				activity.toast("进入看点详情");
				if(item.getType()==1){//看点
					Intent intent = new Intent(activity, ViewSpotDetailActivity.class);
					//intent.putExtra(ViewSpotDetailActivity.EXTRA_TYPE, 0);
					intent.putExtra(LinesDetailImageTextActivity.EXTRA_LID, item.getSoptid());
					activity.startActivity(intent);
				}else if(item.getType()==2){//停车发呆地
					Intent intent = new Intent(activity, ViewSpotDetailActivity.class);
					intent.putExtra(ViewSpotDetailActivity.EXTRA_TYPE, 1);
					intent.putExtra(LinesDetailImageTextActivity.EXTRA_LID, item.getSoptid());
					activity.startActivity(intent);
				}else if(item.getType()==3){
					Intent intent = new Intent(activity, InfoMapActivity.class);
					intent.putExtra("name", item.getName());
					intent.putExtra("addr", item.getDesc());
					intent.putExtra("geo", item.getGeo());
					intent.putExtra("lid",item.getId());
					intent.putExtra("detailType","3");
					intent.putExtra(InfoMapActivity.EXTRA_MYCOLLECT, false);
					activity.startActivity(intent);
				}else if(item.getType()==4) {
					Intent intent = new Intent(activity, InfoMapActivity.class);
					intent.putExtra("name", item.getName());
					intent.putExtra("addr", item.getDesc());
					intent.putExtra("geo", item.getGeo());
					intent.putExtra("lid",item.getId());
					intent.putExtra("detailType","4");
					intent.putExtra(InfoMapActivity.EXTRA_MYCOLLECT, false);
					activity.startActivity(intent);
				}else if(item.getType()==5) {
					Intent intent = new Intent(activity, DetailsActivity.class);
					intent.putExtra("id", item.getObj_id());
					activity.startActivity(intent);
				}else if(item.getType()==6) {
					Intent intent = new Intent(activity, StoresInfoActivity.class);
					intent.putExtra("id", item.getObj_id());
					intent.putExtra("title",item.getTitle());
					intent.putExtra("img",item.getImage());
					activity.startActivity(intent);
				}
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
		RelativeLayout rl_content;
		NetworkImageView iv_header;
		TextView tv_title;
		TextView tv_content;
		CheckBox cb_selected;
	}

	public String getSelectIds() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getDataSource().size(); i++) {
			CollectVo vo = getDataSource().get(i);
			if (vo.isCheck()) {
				sb.append(vo.getId() + ",");
			}
		}
		if (sb.length() == 0) {
			return "";
		}
		return sb.toString().substring(0, sb.toString().length() - 1);
	}
	/**
	 * 添加看点时，标示选择的点
	 * @return
	 */
	public List<BespokeVo> getSelectList() {
		List<BespokeVo> list=new ArrayList<BespokeVo>();
		for (int i = 0; i < getDataSource().size(); i++) {
			CollectVo vo = getDataSource().get(i);
			if (vo.isCheck()) {
				BespokeVo bespokevo=new BespokeVo();
				bespokevo.setGeo(vo.getGeo());
				bespokevo.setId(vo.getId());
				bespokevo.setImage(vo.getImage());
				bespokevo.setLat(vo.getLat());
				bespokevo.setLng(vo.getLng());
				bespokevo.setName(vo.getName());
				bespokevo.setSoptid(vo.getSoptid());
				bespokevo.setSpotid(vo.getSoptid());

				bespokevo.setType(vo.getType());
				bespokevo.setFrom_fav("1");
				list.add(bespokevo);
			}
		}
		return list;
	}

	public void doDelete() {

		Iterator<CollectVo> it = getDataSource().iterator();
		while (it.hasNext()) {
			CollectVo value = it.next();
			if (value.isCheck()) {
				it.remove();
			}
		}
	}

	public void showAll() {
		List<CollectVo> tempData = new ArrayList<CollectVo>();
		for (int i = 0; i < getDataSource().size(); i++) {
			CollectVo vo = getDataSource().get(i);
			vo.setShown(true);
			tempData.add(vo);
		}
		reload(tempData);
	}

	public void hideAll() {
		List<CollectVo> tempData = new ArrayList<CollectVo>();
		for (int i = 0; i < getDataSource().size(); i++) {
			CollectVo vo = getDataSource().get(i);
			vo.setShown(false);
			tempData.add(vo);
		}
		reload(tempData);
	}
}