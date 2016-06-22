package com.ccpress.izijia.activity.line;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.KeyEvent;
import com.amap.api.maps.model.*;
import com.ccpress.izijia.Constant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.BespokeEditAdapter;
import com.ccpress.izijia.componet.BottomBar;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.view.DragListView;
import com.ccpress.izijia.vo.BespokeVo;
import com.ccpress.izijia.vo.LineDetailVo.Summary;
import com.ccpress.izijia.vo.LineDetailVo.Travel;
import com.ccpress.izijia.vo.ResponseVo;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.SpUtil;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import de.greenrobot.event.EventBus;

/**
 * 
 * @Des: 线路编辑
 * @author Rhino
 * @version V1.0
 * @created 2015年6月16日 上午11:16:17
 */
public class LineEditActivity extends BaseActivity implements AMap.OnMarkerClickListener{

	@ViewInject(R.id.lv_page_list)
	private DragListView clListView;

	BespokeEditAdapter adapter;

	@ViewInject(R.id.map_view)
	private MapView mapView;

	@ViewInject(R.id.ll_main)
	private RelativeLayout ll_main;

	private AMap aMap;
	
	ArrayList<BespokeVo> data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		mapView.onCreate(savedInstanceState);
		//初始化MAp
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

	@Override
	public void doBusiness() {
		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("编辑线路");
		bar.showRightText(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					save();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, "保存");

		BottomBar bottombar = new BottomBar(activity);
		bottombar.showTv1("添加看点", R.drawable.icon_add_view,
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						AddViewPointActivity.Bespoke=adapter.getCount();
						skip(AddViewPointActivity.class,
								((Summary) getVo("1")).getLid());
					}
				});

		bottombar.showTv2("添加照片", R.drawable.icon_add_photo,
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						skip(OtherAddPhotoActivity.class,
								(ArrayList<Travel>) getVo("2"),
								((Summary) getVo("1")).getLid());
					}
				});
		bottombar.showTv3("路线设置", R.drawable.icon_setting_line,
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						skip(LineSettingsActivity.class,
								(ArrayList<BespokeVo>) adapter.getDataSource(),
								(Summary) getVo("1"));
					}
				});
		adapter = new BespokeEditAdapter(new ArrayList<BespokeVo>(), activity,
				R.layout.item_bespoke_edit, clListView);
		clListView.setAdapter(adapter);
		BespokeEditAdapter.EBS="LineEditActivity";
		DragListView.EBS="LineEditActivity";
		data = (ArrayList<BespokeVo>) getVo("0");
		adapter.addItems(data);
	}

	/**
	 * 数据变化后，刷新adapter数据
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

	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;
	}

	/**
	 *
	 * Maker间连线
	 */
	private void addLine(){

			PolylineOptions options = new PolylineOptions();
			aMap.clear();
			options.width(5);
			List<BespokeVo> tempData = adapter.getDataSource();
			options.color(Color.BLUE);
		//makers地图上显示
			for (BespokeVo vo : tempData) {
				LatLng lat = new LatLng(Double.parseDouble(vo.getLat()), Double.parseDouble(vo.getLng()));
				options.add(lat);
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
		EventBus.getDefault().unregister(this);
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 保存线路到我的线路中
	 * @throws JSONException
     */
	private void save() throws JSONException {
		showDialog("保存中……");
		RequestQueue mQueue = Volley.newRequestQueue(activity);
		PostParams params = new PostParams();
		SpUtil sp = new SpUtil(activity);
		params.put("uid", sp.getStringValue(Const.UID));
		params.put("id", ((Summary) getVo("1")).getLid());

		JSONArray data = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("come_from_route", "0");
		JSONArray array = new JSONArray();
		for (int i = 0; i < adapter.getCount(); i++) {
			JSONObject temp = new JSONObject();
			BespokeVo tempVo = adapter.getItem(i);
			temp.put("type", tempVo.getType());
			temp.put("lng", tempVo.getLng());
			temp.put("lat", tempVo.getLat());
			temp.put("id", tempVo.getId());
			temp.put("from_fav", tempVo.getFrom_fav());
			array.put(temp);
		}
		obj.put("point_arr", array);

		data.put(obj);
		params.put("trip_json", data.toString());
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.SAVE_NEW_POINT),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						ResponseVo resp = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (resp.isSucess()) {
							toast("保存成功");
							finish();
						} else {
							toast(resp.getMsg());
						}
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 *方法 已废弃
	 */
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
							aMap.moveCamera(CameraUpdateFactory.zoomTo(12));
						} else {
						}
					}
				});

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
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
