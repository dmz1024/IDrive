package com.ccpress.izijia.adapter;

import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.trs.util.log.Log;
import org.json.JSONObject;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.froyo.commonjar.network.GetRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.view.CircleImageView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.mystyle.InfoActivity;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.MyAttentionVo;
import com.ccpress.izijia.vo.ResponseVo;

/**
 * 
 * @Des: 我的关注列表
 * @author Rhino
 * @version V1.0
 * @created 2015年6月6日 下午4:40:02
 */
public class MyFansAdapter extends SimpleAdapter<MyAttentionVo> {

	public MyFansAdapter(List<MyAttentionVo> data, BaseActivity activity,
			int layoutId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
	}

	@Override
	public void doExtra(View convertView, final MyAttentionVo item, int position) {
		final ViewHolder h = (ViewHolder) holder;
		h.iv_header.setImageUrl(item.getUser().getAvatar(), imageLoader);
		h.tv_title.setText(item.getUser().getNickname());
		if ("1".equals(item.getUser().getSex())) {
			h.iv_sex.setImageResource(R.drawable.icon_female);
		} else {
			h.iv_sex.setImageResource(R.drawable.icon_male);
		}

		if (!item.isFollowed()) {
			h.tv_attention.setText("关注");
			h.tv_attention.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					addFollow(item.getUser().getUid(), item.getUser()
							.getNickname(),h.tv_attention);
					item.setFollowed(true);
					notifyDataSetChanged();
				}
			});
		} else {
			h.tv_attention.setText("已关注");
			h.tv_attention.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					cancleFollow(item.getUser().getUid(), item.getUser()
							.getNickname(),h.tv_attention);
					item.setFollowed(false);
					notifyDataSetChanged();
				}
			});
		}

	}

	public static class ViewHolder {
		RelativeLayout rl_content;

		TextView tv_title;

		TextView tv_attention;

		ImageView iv_sex;

		CircleImageView iv_header;
	}

	/**
	 * 关注
	 * @param id
	 * @param name
	 * @param view
     */
	private void addFollow(String id, final String name,final TextView view) {
		if(view.getText().toString().equals("已关注")){
			return;
		}
		activity.showDialog();
		GetRequest req = new GetRequest(activity,
				PersonalCenterUtils.buildUrl(activity, Const.ADD_ATTENTION)+"&follow_uid="+id,
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						activity.dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (vo.isSucess()) {
							activity.toast("成功关注" + name);
							view.setText("已关注");
						} else {
							activity.toast("关注失败");
						}
					}

					@Override
					public void doFailed() {
						activity.toast("关注失败");
					}
				});
		mQueue.add(req);
		mQueue.start();
	}


	/**
	 * 取消关注
	 * @param id
	 * @param name
	 * @param view
     */
	private void cancleFollow(String id, final String name,final TextView view) {
		activity.showDialog();
		RequestQueue mQueue = Volley.newRequestQueue(activity);

		PostParams params = new PostParams();
		params.put("unfollow_uid", id);
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.CANCEL_ATTENTION),
				new RespListener(activity) {
					@Override
					public void getResp(JSONObject obj) {
						activity.dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);

						if (vo.isSucess()) {

							activity.toast("已取消对"+ name + "的关注");
							view.setText("关注");
						} else {
							activity.toast("取消失败");
						}
					}

					@Override
					public void doFailed() {
						activity.toast("取消失败");
					}
				});
		mQueue.add(req);
		mQueue.start();
	}
}
