package com.ccpress.izijia.activity.mystyle;


import android.app.Dialog;
import android.content.Intent;

import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.*;
import android.widget.NumberPicker.OnValueChangeListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.*;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.*;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.*;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.SimpleNaviActivity;
import com.ccpress.izijia.adapter.BespokeEditAdapter;
import com.ccpress.izijia.componet.BottomBar;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.DialogUtil;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.view.DragListView;
import com.ccpress.izijia.vo.BespokeVo;
import com.ccpress.izijia.vo.ResponseVo;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.AppUtils;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.SpUtil;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.froyo.commonjar.xutils.view.annotation.event.OnClick;
import de.greenrobot.event.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.view.KeyEvent;
/**
 * 
 * @Des: 线路编辑
 * @author Rhino
 * @version V1.0
 * @created 2015年6月16日 上午11:16:17
 */
public class EditLineActivity extends BaseActivity implements AMapNaviListener,AMap.OnMarkerClickListener {
	private Dialog mCaculateProgressDialog;
	public static  String LID="lid";
	@ViewInject(R.id.lv_page_list)
	private DragListView clListView;

	BespokeEditAdapter adapter;

	@ViewInject(R.id.map_view)
	private MapView mapView;

	@ViewInject(R.id.ll_main)
	private RelativeLayout ll_main;

	private AMap aMap;
	ArrayList<BespokeVo> data;

	private View popupView;
	PopupWindow popupWindow;

	private LatLonPoint startPoint;
	private LatLonPoint endPoint;
	public static String COME_TYPE="come_type";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapView.onCreate(savedInstanceState);
		mCaculateProgressDialog = DialogUtil.getProgressdialog(this, null);
		AMapNavi.getInstance(this).setAMapNaviListener(this);
		EventBus.getDefault().register(this);
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
				@Override
				public void onMapLoaded() {
				}
			});
			aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker) {

				}
			});
			aMap.setOnMarkerClickListener(this);
		}
		aMap.moveCamera(CameraUpdateFactory.zoomTo(4.5f));
		addLine();
	}

	/**
	 * Makers连线
	 */
	private void addLine(){
			PolylineOptions options = new PolylineOptions();
			aMap.clear();
			options.width(5);
			List<BespokeVo> tempData = adapter.getDataSource();
			options.color(Color.BLUE);
		//makers地图展示
			for (BespokeVo vo : tempData) {
				LatLng lat = new LatLng(Double.parseDouble(vo.getLat()), Double.parseDouble(vo.getLng()));
				options.add(lat);
				Log.e("addLine ", "===" + lat.toString());
				aMap.addMarker(new MarkerOptions().position(lat).icon(BitmapDescriptorFactory.defaultMarker()).draggable(true).perspective(true).title(vo.getName()));
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lat, 6f));
			}

			Polyline line = aMap.addPolyline(options);
			line.setWidth(25.0f);
			line.setVisible(true);

	}
	public static class GetAddViewPointsEvent {
		private List<BespokeVo> data;
		public List<BespokeVo> getData() {
			return data;
		}
		public void setData(List<BespokeVo> data) {
			this.data = data;
		}
	}
	@Override
	public void doBusiness() {
		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("编辑线路");
		//编辑线路点击事件
		bar.showRightText(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				View view = activity
						.makeView(R.layout.view_edit_bespoke_dialog);
				TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
				TextView sure = (TextView) view.findViewById(R.id.tv_sure);
				final EditText et_title = (EditText) view
						.findViewById(R.id.et_title);
				final Dialog dialog = new Dialog(activity, R.style.popFromBottomdialog);
				dialog.setContentView(view);
				//取消
				cancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				//确定
				sure.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						try {
							save(et_title.getText().toString(), dialog);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				dialog.show();
			}
		}, "保存");

		BottomBar bottombar = new BottomBar(activity);
		bottombar.showTv1("快速导航", R.drawable.icon_quik_local,
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						showWindow();
					}
				});

		adapter = new BespokeEditAdapter(new ArrayList<BespokeVo>(), activity,
				R.layout.item_bespoke_edit,clListView);
		clListView.setAdapter(adapter);
		BespokeEditAdapter.EBS="EditLineActivity";
		DragListView.EBS="EditLineActivity";
		data = (ArrayList<BespokeVo>) getVo("0");
		adapter.addItems(data);
	}

	/**
	 * 保存编辑的线路->到我的线路
	 * @param name
	 * @param dialog
	 * @throws JSONException
     */
	private void save(String name, final Dialog dialog)
			throws JSONException {
		showDialog("保存中……");
		RequestQueue mQueue = Volley.newRequestQueue(activity);
		PostParams params = new PostParams();


		JSONArray data = new JSONArray();
		JSONObject obj=new JSONObject();
		JSONArray array = new JSONArray();
		if (COME_TYPE=="Mystyle_Bespoke"){
			for (int i = 0; i < adapter.getCount(); i++) {
				JSONObject temp = new JSONObject();
				BespokeVo tempVo = adapter.getItem(i);
				temp.put("type",tempVo.getType());
				temp.put("lng", tempVo.getLng());
				temp.put("lat", tempVo.getLat());
				temp.put("id", tempVo.getId());
				temp.put("name",tempVo.getName());
				array.put(temp);
			}
			obj.put("come_from_route", "0");
			obj.put("point_arr", array);
			data.put(obj);
			params.put("trip_json",data.toString());

		}else if (COME_TYPE=="Mystyle_Lines") {
			for (int i = 0; i < adapter.getCount(); i++) {
				JSONObject temp = new JSONObject();
				BespokeVo tempVo = adapter.getItem(i);
				temp.put("type","1");
				temp.put("lng", tempVo.getLng());
				temp.put("lat", tempVo.getLat());
				temp.put("id", tempVo.getSoptid());
				array.put(temp);
				}
			obj.put("point_arr",array);
			obj.put("come_from_route", "1");

			data.put(obj);
			params.put("trip_json", data.toString());
		} else if (COME_TYPE=="Mystyle_UserLines") {
			for (int i = 0; i < adapter.getCount(); i++) {
				JSONObject temp = new JSONObject();
				BespokeVo tempVo = adapter.getItem(i);
				temp.put("type","1");
				temp.put("lng", tempVo.getLng());
				temp.put("lat", tempVo.getLat());
				temp.put("id", tempVo.getSoptid());
				array.put(temp);
			}

			obj.put("lid",LID);
			obj.put("point_arr",array);
			obj.put("come_from_route", "1");
			data.put(obj);
			params.put("trip_json", data.toString());

		}

		SpUtil sp = new SpUtil(activity);
		params.put("uid", sp.getStringValue(Const.UID));
		params.put("route_name", name);

		Log.e("save ", data.toString());
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.CRETE_ROUTE),
				new RespListener(activity) {
					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo resp = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (resp.isSucess()) {
							toast("定制成功");
							DragListView.EBS="EditLineActivity";
							finish();
							dialog.dismiss();
						} else {
							toast(resp.getMsg());
						}
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 数据变化，重新连线
	 * @param event
     */
	public void onEventMainThread(GetAddViewPointsEvent event) {
		if (!Utils.isEmpty(event.getData())) {
			List<BespokeVo> data = new ArrayList<BespokeVo>();
			List<BespokeVo> tempData = event.getData();
			if (!Utils.isEmpty(adapter.getDataSource())) {
				data.addAll(adapter.getDataSource());
			}
			data.addAll(tempData);
			for (int i = 0; i < data.size() - 1; i++) {
				for (int j = data.size() - 1; j > i; j--) {
					if (data.get(j).getId().equals(data.get(i).getId())) {
						data.remove(j);
					}
				}
			}
			adapter.removeAll();
			adapter.addItems(data);
		}
		addLine();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = new Intent();
		intent.setAction(Constant.MAP_ROUTE_CLEAR_ACTION);
		sendBroadcast(intent);
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@OnClick(R.id.tv_bottom)
	void local(View view) {
		showWindow();
	}

	//方法废弃
	private void getLocal() {
		LocationManagerProxy mLocationManagerProxy = LocationManagerProxy
				.getInstance(activity);
		mLocationManagerProxy.setGpsEnable(true);
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, 2000, 30000,
				new AMapLocationListener() {

					@Override
					public void onStatusChanged(String arg0, int arg1,
												Bundle arg2) {

					}

					@Override
					public void onProviderEnabled(String arg0) {

					}

					@Override
					public void onProviderDisabled(String arg0) {

					}

					@Override
					public void onLocationChanged(Location arg0) {

					}

					@Override
					public void onLocationChanged(AMapLocation amapLocation) {
						if (amapLocation != null
								&& amapLocation.getAMapException()
								.getErrorCode() == 0) {
							LatLng startPoint = new LatLng(amapLocation
									.getLatitude(), amapLocation.getLongitude());
							aMap.addMarker(new MarkerOptions().position(
									startPoint).icon(
									BitmapDescriptorFactory.defaultMarker()));
							aMap.moveCamera(CameraUpdateFactory
									.changeLatLng(startPoint));
							aMap.moveCamera(CameraUpdateFactory.zoomTo(6f));
						} else {
						}
					}
				});

	}

	/**
	 * 弹出底部window：快速导航List
	 */
	private void showWindow() {
		if (adapter.getCount() < 1) {
			toast("没有可导航的看点");
			return;
		}
		ArrayList<BespokeVo> pickData = (ArrayList<BespokeVo>) adapter
				.getDataSource();
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < pickData.size(); i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("ItemTitle", pickData.get(i).getName());
			map.put("ItemText", "");
			mylist.add(map);
		}

		if (popupView == null) {
			popupView = makeView(R.layout.view_guide_window);
			popupWindow = new PopupWindow(popupView,
					WindowManager.LayoutParams.MATCH_PARENT,
					AppUtils.getHeight(activity));
			LinearLayout window = (LinearLayout) popupView
					.findViewById(R.id.ll_window);
			LinearLayout content = (LinearLayout) popupView
					.findViewById(R.id.ll_content);
			TextView tv_cancel = (TextView) popupView
					.findViewById(R.id.tv_cancle);

			final ListView list = (ListView) popupView.findViewById(R.id.preview_line_list);

			//生成适配器
			SimpleAdapter mSchedule = new SimpleAdapter(this,
					mylist, R.layout.item_preview_popu, new String[] {"ItemTitle", "ItemText"},
					new int[] {R.id.ItemTitle,R.id.ItemText});
			list.setAdapter(mSchedule);

			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					endPoint = new LatLonPoint(Double.parseDouble(adapter
							.getItem(position).getLat()),
							Double.parseDouble(adapter.getItem(position)
									.getLng()));
					popupWindow.dismiss();
					//routeSearch(endPoint);
					calculateDriverRoute(endPoint);
				}
			});

			tv_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					popupWindow.dismiss();
				}
			});

			window.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					popupWindow.dismiss();
				}
			});
			content.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
				}
			});
		}
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			popupWindow.showAtLocation(ll_main, Gravity.BOTTOM
					| Gravity.CENTER_HORIZONTAL, 0, 0);
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;
	}

	/**
	 * 算路
	 * @author WLH
	 * @param mDestination*/
	private void calculateDriverRoute(LatLonPoint mDestination){
		mCaculateProgressDialog.show();

		//起点终点列表
		ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
		ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
		mStartPoints.add(new NaviLatLng(iDriveApplication.app().getLocation().getLatitude(), iDriveApplication.app().getLocation().getLongitude()));
		mEndPoints.add(new NaviLatLng(mDestination.getLatitude(),mDestination.getLongitude()));
		AMapNavi.getInstance(this).calculateDriveRoute(mStartPoints,
				mEndPoints, null, AMapNavi.DrivingFastestTime);
	}


	private void getLocal(final LatLonPoint endPoint) {
		// 初始化定位，只采用网络定位
		LocationManagerProxy mLocationManagerProxy = LocationManagerProxy
				.getInstance(activity);
		mLocationManagerProxy.setGpsEnable(true);
		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
		// 在定位结束后，在合适的生命周期调用destroy()方法
		// 其中如果间隔时间为-1，则定位只定一次,
		// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork,-1, 30000,
				new AMapLocationListener() {

					@Override
					public void onStatusChanged(String arg0, int arg1,
							Bundle arg2) {
					}

					@Override
					public void onProviderEnabled(String arg0) {
					}

					@Override
					public void onProviderDisabled(String arg0) {
					}
					@Override
					public void onLocationChanged(Location arg0) {
					}

					@Override
					public void onLocationChanged(AMapLocation amapLocation) {
						if (amapLocation != null
								&& amapLocation.getAMapException()
										.getErrorCode() == 0) {
							startPoint = new LatLonPoint(amapLocation
									.getLatitude(), amapLocation.getLongitude());
							LatLng startPoint = new LatLng(amapLocation
									.getLatitude(), amapLocation.getLongitude());
							aMap.addMarker(new MarkerOptions().position(
									startPoint).icon(
									BitmapDescriptorFactory.defaultMarker()));
							aMap.moveCamera(CameraUpdateFactory
									.changeLatLng(startPoint));
							aMap.moveCamera(CameraUpdateFactory.zoomTo(12f));
							routeSearch(endPoint);
						} else {
						}
					}
				});

	}

	/**
	 * 线路展示
	 * @param endPoint
     */
	private void routeSearch(LatLonPoint endPoint) {
		RouteSearch routeSearch = new RouteSearch(this);// 初始化routeSearch 对象
		routeSearch.setRouteSearchListener(new OnRouteSearchListener() {

			@Override
			public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {

			}

			@Override
			public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
				try {
					if (rCode == 0) {
						if (result != null && result.getPaths() != null
								&& result.getPaths().size() > 0) {
							DriveRouteResult driveRouteResult = result;
							DrivePath drivePath = driveRouteResult.getPaths()
									.get(0);
							aMap.clear();// 清理地图上的所有覆盖物
							DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
									activity, aMap, drivePath, driveRouteResult
											.getStartPos(), driveRouteResult
											.getTargetPos());

							drivingRouteOverlay.setNodeIconVisibility(false);
							drivingRouteOverlay.addToMap();
							drivingRouteOverlay.zoomToSpan();

						} else {
							toast("暂无规划结果");
						}
					} else if (rCode == 27) {
						toast("网络错误");
					} else if (rCode == 32) {
						toast("key错误");
					} else {
						//toast("未知错误");
					}
				} catch (Exception ex) {

				}
			}

			@Override
			public void onBusRouteSearched(BusRouteResult arg0, int arg1) {

			}
		});// 设置数据回调监听器
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				startPoint, endPoint);
		DriveRouteQuery query = new DriveRouteQuery(fromAndTo, 2, null, null,
				"");
		routeSearch.calculateDriveRouteAsyn(query);
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_edit_line;
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
	}

	@Override
	public void onInitNaviFailure() {

	}

	@Override
	public void onInitNaviSuccess() {

	}

	@Override
	public void onStartNavi(int i) {

	}

	@Override
	public void onTrafficStatusUpdate() {

	}

	@Override
	public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

	}

	@Override
	public void onGetNavigationText(int i, String s) {

	}

	@Override
	public void onEndEmulatorNavi() {

	}

	@Override
	public void onArriveDestination() {

	}

	/**
	 * 导航
	 */
	@Override
	public void onCalculateRouteSuccess() {
		mCaculateProgressDialog.dismiss();
		Intent intent = new Intent(this,
				SimpleNaviActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	@Override
	public void onCalculateRouteFailure(int i) {
		mCaculateProgressDialog.dismiss();
		if(i==2){
			Toast.makeText(this,"网络超时", Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(this,"路线规划失败，请重试", Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onReCalculateRouteForYaw() {

	}

	@Override
	public void onReCalculateRouteForTrafficJam() {

	}

	@Override
	public void onArrivedWayPoint(int i) {

	}

	@Override
	public void onGpsOpenStatus(boolean b) {

	}

	@Override
	public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

	}

	@Override
	public void onNaviInfoUpdate(NaviInfo naviInfo) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
