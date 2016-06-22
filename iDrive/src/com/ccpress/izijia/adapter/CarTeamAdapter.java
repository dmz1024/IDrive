package com.ccpress.izijia.adapter;


import java.util.ArrayList;
import java.util.List;



import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.fragment.MapFragment;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.trs.wcm.LoadWCMJsonTask;
import org.json.JSONObject;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.SpUtil;
import com.ccpress.izijia.R;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.vo.CarTeamVo;
import com.ccpress.izijia.vo.ResponseVo;

/**
 * 
 * @Des: 我的车队
 * @author Rhino
 * @version V1.0
 * @created 2015年6月6日 下午3:13:11
 */
public class CarTeamAdapter extends SimpleAdapter<CarTeamVo> {

	public static boolean CART_FLAG=false;
	public static  String status="aaa";
	private LocationManager lm;
	private Handler handler;
	private Runnable runnable;
	public static int temp;
	private String CARTEAMURL = "http://member.izijia.cn/index.php?s=/fleet/app/upload_loc";
	public CarTeamAdapter(List<CarTeamVo> data, BaseActivity activity,
			int layoutId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
	}
	/**
	 * 检查GPD是否开启
	 */
	private void initGPS(){
		lm=(LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		//判断GPS模块是否开启，如果没有则开启
		if(!lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
			Toast.makeText(activity.getBaseContext(), "GPS没有打开,请打开GPS!", Toast.LENGTH_SHORT).show();
			//转到手机设置界面，用户设置GPS
			Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			activity.startActivityForResult(intent,0); //设置完成后返回到原来的界面
		}

	}
	@Override
	public void doExtra(View convertView, final CarTeamVo item,
			final int position) {
		final ViewHolder h = (ViewHolder) holder;
		h.tv_title.setText(item.getTeam_title());
		if (CART_FLAG) {
			h.tg_btn.setChecked(false);
		}else{
			final SpUtil sp = new SpUtil(activity);
			if ("1".equals(item.getSwitch_on())) {
				h.tg_btn.setChecked(true);
				sp.setValue(Const.CAR_TEAM_ID, item.getId());
			} else {
				h.tg_btn.setChecked(false);
			}

		}

		h.tg_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				CART_FLAG = false;
				initGPS();

				if (status.equals("aaa")){
					handler.removeCallbacks(runnable);
					handler.postDelayed(runnable, 5000);
					MapFragment.CAR_STATUS="TRUE";
					status = "bbb";
				}else {
					handler.removeCallbacks(runnable);
					status = "aaa";
					MapFragment.CAR_STATUS="FALSE";
					Intent intent = new Intent();
					intent.setAction(Constant.CART_TEAM_STATUS_ACTION);
					activity.sendBroadcast(intent);
				}
				switchOn(item.getId(), h, item, position);
			}
		});
		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				if (status.equals("bbb")){
					handler.removeCallbacks(runnable);
					handler.postDelayed(this, 5000);
					updateCartLocation();
				}
				else {
					handler.removeCallbacks(runnable);
				}
			}
		};
	}
	private void updateCartLocation() {
		LoadWCMJsonTask task = new LoadWCMJsonTask(activity) {
			@Override
			public void onDataReceived(String result, boolean isCache) throws Exception {
			}
			@Override
			public void onError(Throwable t) {
			}
		};
		String lati =String.valueOf(iDriveApplication.app().getLocation().getLatitude());
		String lonti =String.valueOf(iDriveApplication.app().getLocation().getLongitude());

		task.start(PersonalCenterUtils.buildUrl(activity,CARTEAMURL  + "&latitude="+ lati + "&longitude=" + lonti));
	}

	public static class ViewHolder {
		TextView tv_title;
		ToggleButton tg_btn;

	}

	private void switchOn(String carId, final ViewHolder h,
			final CarTeamVo item, final int position) {
		activity.showDialog("正在切换车队……");
		RequestQueue mQueue = Volley.newRequestQueue(activity);
		final SpUtil sp = new SpUtil(activity);
		String url = Const.SWITCH_ON + "&auth="
				+ Uri.encode(sp.getStringValue(Const.AUTH)) + "&uid="
				+ sp.getStringValue(Const.UID);
		PostParams params = new PostParams();
		params.put("team_id", carId);
		String previousId = sp.getStringValue(Const.CAR_TEAM_ID);
		//前后两次相等
		final boolean isClose=previousId.equals(carId);
		if(isClose&&!h.tg_btn.isChecked()){
			params.put("is_on", "0");
			status="aaa";
		}else{
			params.put("is_on", "1");
			MapFragment.CAR_STATUS="TRUE";
			status="bbb";
		}
		PostRequest req = new PostRequest(activity, params, url,
				new RespListener(activity) {
					@Override
					public void getResp(JSONObject obj) {
						activity.dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (vo.isSucess()) {
							List<CarTeamVo> tempData = getDataSource();
							List<CarTeamVo> newData = new ArrayList<CarTeamVo>();
							for (int i = 0; i < tempData.size(); i++) {
								CarTeamVo CarTeamVo = tempData.get(i);
								CarTeamVo.setSwitch_on("0");
								newData.add(CarTeamVo);
							}
							reload(newData);
							if(isClose){
								sp.remove(Const.CAR_TEAM_ID);
							}
							if (h.tg_btn.isChecked()) {
								// 保存信息，同时关闭其他按钮
								item.setSwitch_on("1");
								replace(item, position);
								sp.setValue(Const.CAR_TEAM_ID, item.getId());
							} else {
								// 删除信息
								item.setSwitch_on("0");
								replace(item, position);
								sp.remove(Const.CAR_TEAM_ID);
							}

						} else {
//							activity.toast(vo.getMsg());
						}
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

}
