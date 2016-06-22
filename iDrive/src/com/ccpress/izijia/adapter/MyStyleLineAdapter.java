package com.ccpress.izijia.adapter;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import android.app.ProgressDialog;
import com.trs.util.log.Log;
import com.trs.wcm.LoadWCMJsonTask;
import org.json.JSONObject;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.mystyle.EditLineActivity;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.LineDetailVo;
import com.ccpress.izijia.vo.StyleLineVo;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.Utils;
import com.trs.util.log.Log;


/**
 * 我的定制--线路
 * 
 * @author wangyi
 * 
 */
public class MyStyleLineAdapter extends SimpleAdapter<StyleLineVo> {

	public MyStyleLineAdapter(List<StyleLineVo> data, BaseActivity activity,
			int layoutId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
	}

	@Override
	public void doExtra(View convertView, final StyleLineVo item,
			final int position) {
		final ViewHolder h = (ViewHolder) holder;
		h.rl_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ArrayList<StyleLineVo.Trip> trip =new ArrayList<StyleLineVo.Trip>();
				Log.e("item.getType() ::", item.getType());
				queryDetail(item.getLid(),trip,item.getType());
			}
		});
		h.tv_title.setText(item.getLine()==null?item.getTitle():item.getLine());
		h.tv_content.setText(item.getDesc());
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
			StyleLineVo vo = getDataSource().get(i);
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
		Iterator<StyleLineVo> it = getDataSource().iterator();
		while (it.hasNext()) {
			StyleLineVo value = it.next();
			if (value.isCheck()) {
				it.remove();
			}
		}
	}

	public void showAll() {
		List<StyleLineVo> tempData = new ArrayList<StyleLineVo>();
		for (int i = 0; i < getDataSource().size(); i++) {
			StyleLineVo vo = getDataSource().get(i);
			vo.setShown(true);
			tempData.add(vo);
		}
		reload(tempData);
	}

	public void hideAll() {
		List<StyleLineVo> tempData = new ArrayList<StyleLineVo>();
		for (int i = 0; i < getDataSource().size(); i++) {
			StyleLineVo vo = getDataSource().get(i);
			vo.setShown(false);
			tempData.add(vo);
		}
		reload(tempData);
	}
	/** 加载对话框 */
	private ProgressDialog loadingDialog;
	public void showDialog() {
		if (activity == null) {
			return;
		}
		if (loadingDialog == null) {
			loadingDialog = new ProgressDialog(activity);
			loadingDialog.setCanceledOnTouchOutside(false);
		}
		loadingDialog.setMessage("加载中...");
		loadingDialog.show();
	}

	public void dismissDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}

	public List<StyleLineVo> getSelectView() {
		List<StyleLineVo> temp = new ArrayList<StyleLineVo>();
		for (int i = 0; i < getDataSource().size(); i++) {
			StyleLineVo vo = getDataSource().get(i);
			if (vo.isCheck()) {
				temp.add(vo);
			}
		}
		return temp;
	}

public static  String MyLinesType="mylinestype";

	private void queryDetail(final String lid, ArrayList<StyleLineVo.Trip> trip,final String type) {
		showDialog();
		if (type.equals("5")){
			PostParams params = new PostParams();
			params.put("lid",lid);
			final String url="http://member.izijia.cn/index.php?s=/Interaction/index/get_line_detail"+"&lid="+lid;
			PostRequest req = new PostRequest(activity, params,
					PersonalCenterUtils.buildUrl(activity, url),
					new RespListener(activity) {
						@Override
						public void getResp(JSONObject obj) {

							Log.e("getResp ",obj.toString());
							LineDetailVo detail = GsonTools.getVo(obj.toString(),
									LineDetailVo.class);

							if (detail != null && !Utils.isEmpty(detail.getViewspot())) {
								EditLineActivity.COME_TYPE="Mystyle_UserLines";
								activity.skip(EditLineActivity.class,
										(ArrayList) detail.getViewspot());
								dismissDialog();
							} else {

								activity.toast("该线路下无看点数据");
							}
						}
					});

			mQueue.add(req);
		}else {
			LoadWCMJsonTask task = new LoadWCMJsonTask(activity) {
				@Override
				public void onDataReceived(String result, boolean isChanged) throws Exception {
					JSONObject obj = new JSONObject(result);
					Log.e("LoadWCMJsonTask ", obj.toString());
					LineDetailVo detail = GsonTools.getVo(obj.toString(),
							LineDetailVo.class);
					if (detail != null
							&& !Utils.isEmpty(detail.getViewspot())) {
						EditLineActivity.COME_TYPE="Mystyle_Lines";
						EditLineActivity.LID=lid;
						activity.skip(EditLineActivity.class,
								(ArrayList) detail.getViewspot());
					} else {
						activity.toast("该线路下无看点数据");
					}
				}
				@Override
				public void onError(Throwable t) {
					if(activity== null){
						return;
					}
				}
			};
			task.start(PersonalCenterUtils.buildUrlMy(activity, Const.LINE_LINE+"&lid="+lid));

		}
	}
}
