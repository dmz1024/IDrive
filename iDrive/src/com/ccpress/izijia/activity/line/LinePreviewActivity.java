package com.ccpress.izijia.activity.line;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.*;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.navi.AMapNaviListener;

import com.amap.api.services.route.*;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.activity.SimpleNaviActivity;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.DialogUtil;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.BitmapCache;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.AppUtils;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.view.CustomListView;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.BespokeAdapter;
import com.ccpress.izijia.adapter.TripAdapter;
import com.ccpress.izijia.componet.BottomBar;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.BespokeVo;
import com.ccpress.izijia.vo.LineDetailVo;
import com.ccpress.izijia.vo.LineDetailVo.Travel;

/**
 * 线路预览
 * 
 * @author wangyi
 * 
 */
public class LinePreviewActivity extends BaseActivity implements AMapNaviListener {
	@ViewInject(R.id.lv_page_list)
	private CustomListView listView;

	@ViewInject(R.id.trip_list)
	private CustomListView trip_list;

	private BespokeAdapter adapter;

	@ViewInject(R.id.sv_main)
	private ScrollView sv_main;

	@ViewInject(R.id.rl_main)
	private RelativeLayout rl_main;

	@ViewInject(R.id.tv_title1)
	private TextView tv_title1;


	@ViewInject(R.id.tv_desc)
	private TextView tv_desc;

	@ViewInject(R.id.iv_picture)
	private NetworkImageView iv_picture;

	@ViewInject(R.id.map_view)
	private MapView mapView;

	private TripAdapter tripAdapter;

	private View popupView;
	private AMap aMap;
	private String line_name="";
	private LatLonPoint startPoint;
	private LatLonPoint endPoint;
	private Dialog mCaculateProgressDialog;

	private PopupWindow popupWindow;
	public static String PRECITY="北京市";

	RequestQueue mQueue;
	ImageLoader imageLoader;
	LineDetailVo detail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapView.onCreate(savedInstanceState);
		aMap = mapView.getMap();
		mCaculateProgressDialog = DialogUtil.getProgressdialog(this, null);
		AMapNavi.getInstance(this).setAMapNaviListener(this);
	}
	@Override
	public void doBusiness() {
		mQueue = Volley.newRequestQueue(activity);
		imageLoader = new ImageLoader(mQueue, BitmapCache.getInstance());

		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("线路预览");
		bar.showRightText(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				skip(LineEditActivity.class,
						(ArrayList<BespokeVo>) adapter.getDataSource(),
						detail.getSummary(),
						(ArrayList<Travel>) detail.getTravel());
			}
		}, "线路编辑");

		BottomBar bottombar = new BottomBar(activity);
		//快速导航
		bottombar.showTv1("快速导航", R.drawable.icon_style_guide,
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						showWindow();
					}
				});
		//发布线路到互动
		bottombar.showTv2("发布线路", R.drawable.icon_style_line,
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						new AlertDialog.Builder(activity)
								.setTitle("发布线路")
								.setPositiveButton("发布",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface arg0,
													int arg1) {
												publishLine();
											}
										}).setNegativeButton("取消", null).show();

					}
				});

		adapter = new BespokeAdapter(new ArrayList<BespokeVo>(), activity,
				R.layout.item_bespoke);
		listView.setAdapter(adapter);
		Utils.setListViewHeightBasedOnChildren(listView);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				sv_main.scrollTo(0, 0);
			}
		}, 300);
		tripAdapter = new TripAdapter(new  ArrayList<Travel>(), activity);
		trip_list.setAdapter(tripAdapter);
		queryDetail();
	}

	/**
	 * 请求数据
	 */
	private void queryDetail() {
		showDialog();
		PostParams params = new PostParams();
		params.put("lid", getVo("0").toString());
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.LINE_DETAIL),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						detail = GsonTools.getVo(obj.toString(),
								LineDetailVo.class);
						tv_title1.setText(detail.getSummary().getTitle());
						line_name=detail.getSummary().getTitle();
						iv_picture.setImageUrl(detail.getSummary().getImage(),
								imageLoader);
						tv_desc.setText(detail.getSummary().getDesc());
						adapter.removeAll();
						tripAdapter.removeAll();
						adapter.addItems(detail.getViewspot());
						//Collections.reverse(detail.getTravel());
						tripAdapter.addItems(detail.getTravel());
						Utils.setListViewHeightBasedOnChildren(listView);
						Utils.setListViewHeightBasedOnChildrenB(trip_list);
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								sv_main.scrollTo(0, 0);
							}
						}, 300);

					}
				});
		mQueue.add(req);
		dismissDialog();
	}

	public class DateUtil {
		public  Date stringToDate(String dateString) {
			ParsePosition position = new ParsePosition(0);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date dateValue = simpleDateFormat.parse(dateString, position);
			return dateValue;
		}

	}

	/**
	 * 线路的发布
	 */
	private void publishLine() {
		String city="";
		if(iDriveApplication.app().getLocation() != null){
			 city = iDriveApplication.app().getLocation().getCity();
		}else {
			city=PRECITY;
		}
		showDialog();
		PostParams params = new PostParams();
		params.put("id", getVo("0").toString());
		params.put("city",city);
		params.put("title",line_name);
		params.put("des",line_name);

		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.PUBLISH_LINE),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						if ("1".equals(obj.optString("status"))) {
							dismissDialog();
							toast("发布至互动成功");
						} else {
							dismissDialog();
							toast("发布失败");
						}
					}

					@Override
					public void doFailed() {
						super.doFailed();
						toast("发布失败");
					}
				});
		mQueue.add(req);
	}

	/**
	 * 进行看点定制时弹出的Window
	 */
	private void showWindow() {
		if (Utils.isEmpty(adapter.getDataSource())) {
			toast("没有看点数据");
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
			//取消
			tv_cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					popupWindow.dismiss();
				}
			});
			//list的item点击事件
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					endPoint = new LatLonPoint(Double.parseDouble(adapter
							.getItem(position).getLat()),
							Double.parseDouble(adapter.getItem(position)
									.getLng()));
					popupWindow.dismiss();
//					routeSearch(endPoint);
					calculateDriverRoute(endPoint);
				}
			});
			//弹出窗口的点击事件
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
			popupWindow.showAtLocation(rl_main, Gravity.BOTTOM
					| Gravity.CENTER_HORIZONTAL, 0, 0);
		}
	}


	@Override
	protected void onResume() {
		super.onResume();

		Intent intent = new Intent();
		intent.setAction(Constant.MAP_ROUTE_CLEAR_ACTION);
		sendBroadcast(intent);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		queryDetail();
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
				LocationProviderProxy.AMapNetwork, -1, 30000,
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
							aMap.moveCamera(CameraUpdateFactory.zoomTo(12));
							routeSearch(endPoint);
						} else {
						}
					}
				});

	}

	/**
	 * 线路规划
	 * @param endPoint
     */
	private void routeSearch(LatLonPoint endPoint) {
		RouteSearch routeSearch = new RouteSearch(this);// 初始化routeSearch 对象
		routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {

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
							drivingRouteOverlay.removeFromMap();
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
						toast("未知错误");
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
		RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, 2, null, null,
				"");
		routeSearch.calculateDriveRouteAsyn(query);
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_line_preview;
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
	 *导航
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
