package com.ccpress.izijia.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.activity.offline.OfflineMapActivity;
import com.ccpress.izijia.activity.portal.SettingsActivity;
import com.ccpress.izijia.dfy.activity.OrderActivity;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.componet.BadgeView;

import com.froyo.commonjar.network.GetRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.SpUtil;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.froyo.commonjar.xutils.view.annotation.event.OnClick;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.car.CarTeamActivity;
import com.ccpress.izijia.activity.collect.CollectActivity;
import com.ccpress.izijia.activity.line.LineActivity;
import com.ccpress.izijia.activity.menu.MenuActivity;
import com.ccpress.izijia.activity.mystyle.InfoActivity;
import com.ccpress.izijia.activity.mystyle.MyAttentionActivity;
import com.ccpress.izijia.activity.mystyle.MyFansActivity;
import com.ccpress.izijia.activity.mystyle.MyMsgActivity;
import com.ccpress.izijia.activity.mystyle.MystyleActivity;
import com.ccpress.izijia.activity.photo.PhotoActivity;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.InfoVo;
import com.ccpress.izijia.vo.ResponseVo;
import de.greenrobot.event.EventBus;
import com.trs.util.log.Log;
/**
 * 
 * @Des: 首页
 * @author Rhino
 * @version V1.0
 * @created 2015年5月6日 上午11:43:54
 */
public class UserCenterFragment extends BaseFragment {

	@ViewInject(R.id.tv_msg)
	private TextView tv_msg;

	@ViewInject(R.id.tv_name)
	private TextView tv_name;

	@ViewInject(R.id.tv_follow)
	private TextView tv_follow;

	@ViewInject(R.id.tv_fans)
	private TextView tv_fans;

	@ViewInject(R.id.iv_avatar)
	private ImageView iv_avatar;
//	private UserCenterFragmentBroadcastReceiver mReceiver = new UserCenterFragmentBroadcastReceiver();
	private RequestQueue mQueue;
	private BadgeView tips;
	private SpUtil sp;
//	private String  yy="";
//	public static String temp="";
public static String Avatar="ASD";
	@Override
	protected void setListener() {
		EventBus.getDefault().register(this);
		sp = new SpUtil(activity);
		tips = new BadgeView(activity, tv_msg);
		tips.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		tips.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);


//		 sp = new SpUtil(activity);
		mQueue = Volley.newRequestQueue(activity);
//		queryInfo(sp.getStringValue(Const.UID));
		queryInfoAll(sp.getStringValue(Const.UID));
//		temp=sp.getStringValue(Const.UID);
	}
	//我的消息
	@OnClick(R.id.tv_msg)
	void showMsg(View view) {
		activity.skip(MyMsgActivity.class);
	}
	//我的线路
	@OnClick(R.id.rl_line)
	void line(View view) {
		activity.skip(LineActivity.class);
	}
	//我的收藏
	@OnClick(R.id.rl_collect)
	void collect(View view) {
		activity.skip(CollectActivity.class);
	}
	//我的车队
	@OnClick(R.id.rl_car)
	void car(View view) {
		activity.skip(CarTeamActivity.class);
	}
	//我的相册
	@OnClick(R.id.rl_photo)
	void photo(View view) {
		activity.skip(PhotoActivity.class);
	}
	//我的定制
	@OnClick(R.id.rl_mystyle)
	void mystyle(View view) {
		activity.skip(MystyleActivity.class);
	}
	//我的订单
	@OnClick(R.id.rl_menu)
	void menu(View view) {
		activity.skip(OrderActivity.class);
	}
	//离线地图
	@OnClick(R.id.rl_map)
	void offlineMap(View view) {activity.skip(OfflineMapActivity.class);
	}
	//我的关注
	@OnClick(R.id.tv_follow)
	void myAttention(View view) {
		activity.skip(MyAttentionActivity.class);
	}
	//我的粉丝
	@OnClick(R.id.tv_fans)
	void myFans(View view) {
		activity.skip(MyFansActivity.class);
	}

	@OnClick(R.id.btn_setting)
	void mySetting(View view) {
		activity.skip(SettingsActivity.class);
	}
//	private void initBroadcastReceiver() {
//		IntentFilter filters = new IntentFilter();
//		filters.addAction(Constant.USER_INFO_CHANGE_ACTION);
//		getActivity().registerReceiver(mReceiver, filters);
//	}
//	private class UserCenterFragmentBroadcastReceiver extends BroadcastReceiver {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if(action.equals(Constant.USER_INFO_CHANGE_ACTION)){
//				queryInfo(sp.getStringValue(Const.UID));
//			}
//		}
//	}

	/**
	 * 用户信息数据
	 * @param tuid
     */
	private void queryInfoAll(String tuid) {
		GetRequest req = new GetRequest(activity, PersonalCenterUtils.buildUrl(
				getActivity(), Const.MY_INFO) + "&tuid=" + tuid, new RespListener(
				getActivity()) {

			@Override
			public void getResp(JSONObject obj) {
				ResponseVo vo = GsonTools.getVo(obj.toString(),
						ResponseVo.class);
				if (vo.isSucess()) {
					try {
						InfoVo info = GsonTools.getVo(obj.getJSONObject("data")
								.getJSONObject("user_info").toString(),
								InfoVo.class);

						tv_follow.setText("关注  \n " + info.getFollowing());
						tv_fans.setText("粉丝  \n " + info.getFans());
						tv_name.setText(info.getNickname());

							if (info.getMsg_count().equals("0")){
								tips.hide();
							}else {
								tips.toggle();
								tips.setText(info.getMsg_count());
							}
						showAvatar(info.getAvatar());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					activity.toast(vo.getMsg());
				}
			}

			@Override
			public void doFailed() {
			}
		});
		mQueue.add(req);
		mQueue.start();
	}

	private void showAvatar(String url) {
		ImageRequest req = new ImageRequest(url,
				new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap arg0) {
						if (arg0 != null) {
							iv_avatar.setImageBitmap(Utils
									.getRoundedCornerBitmap(arg0));
						}
					}
				}, 300, 300, Config.ARGB_8888, null);
		mQueue.add(req);
		mQueue.start();
	}
	/**
	 * 用户信息数据
	 * @param tuid
	 */
	private void queryInfo(String tuid) {
		GetRequest req = new GetRequest(activity, PersonalCenterUtils.buildUrl(
				getActivity(), Const.MY_INFO) + "&tuid=" + tuid, new RespListener(
				getActivity()) {

			@Override
			public void getResp(JSONObject obj) {
				ResponseVo vo = GsonTools.getVo(obj.toString(),
						ResponseVo.class);
				if (vo.isSucess()) {
					try {
						InfoVo info = GsonTools.getVo(obj.getJSONObject("data")
										.getJSONObject("user_info").toString(),
								InfoVo.class);

						tv_follow.setText("关注  \n " + info.getFollowing());
						tv_fans.setText("粉丝  \n " + info.getFans());
						tv_name.setText(info.getNickname());

						showAvatar(info.getAvatar());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					activity.toast(vo.getMsg());
				}
			}

			@Override
			public void doFailed() {
			}
		});
		mQueue.add(req);
		mQueue.start();
	}

	@OnClick(R.id.iv_avatar)
	void showInfo(View view) {

	}

	public void onEventMainThread(ReFreshEvent event) {
		queryInfo(sp.getStringValue(Const.UID));
	}  
	
	public static class ReFreshEvent{
		
	}
	
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Avatar=="Avatar"){
			queryInfo(sp.getStringValue(Const.UID));
			Avatar="ASD";
		}
		if (Avatar=="MyMsg"){
			queryInfoAll(sp.getStringValue(Const.UID));
			Avatar="ASD";
		}
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.fragment_user_center;
	}


}
