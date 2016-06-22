package com.ccpress.izijia.activity.mystyle;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.*;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimplePageAdapter;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.view.CustomListView;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.froyo.commonjar.xutils.view.annotation.event.OnClick;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.BespokeLineDetailAdapter;
import com.ccpress.izijia.adapter.MyStyleLineAdapter;
import com.ccpress.izijia.vo.BespokeVo;

/**
 * 
 * @Des: 我的定制---我的线路
 * @author Rhino
 * @version V1.0
 * @created 2015年5月6日 下午3:25:22
 */
public class MystyleLineActivity extends BaseActivity {

	@ViewInject(R.id.tv_view)
	private TextView tv_view;

	@ViewInject(R.id.tv_line)
	private TextView tv_line;

	@ViewInject(R.id.vp_pager)
	private ViewPager vp_pager;

	private SimplePageAdapter pageAdapter;

	private CustomListView clListView;

	BespokeLineDetailAdapter adapter;

	MyStyleLineAdapter lineAdapter;

	private AMap aMap;

	private View popupView;

	private MapView mapView;

	private LatLng latlng = new LatLng(39.90403 + 200, 116.407525 + 200);

	public static final LatLng BEIJING = new LatLng(39.90403, 116.407525);// 北京市经纬度
	public static final LatLng BEIJING1 = new LatLng(39.90403+0.0005, 116.407525);// 北京市经纬度
	public static final LatLng BEIJING2 = new LatLng(39.90403+0.0007, 116.407525);// 北京市经纬度

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapView.onCreate(savedInstanceState);
		aMap = mapView.getMap();
		aMap.moveCamera(CameraUpdateFactory.zoomTo(12));
		addMarkersToMap();
	}

	/**
	 * 初始化布局控件
	 */
	@Override
	public void doBusiness() {
		List<View> views = new ArrayList<View>();
		View view = makeView(R.layout.view_mystytle_line_detail);
		View line = makeView(R.layout.view_mystytle_map_detail);
		views.add(view);
		views.add(line);
		pageAdapter = new SimplePageAdapter(activity, views);
		vp_pager.setAdapter(pageAdapter);
		vp_pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				changeBtn(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		initView(view);
		initLine(line);
		vp_pager.setCurrentItem(0);
	}

	/**
	 * List的数据处理
	 * @param view
     */
	private void initView(View view) {
		clListView = (CustomListView) view.findViewById(R.id.lv_page_list);
		adapter = new BespokeLineDetailAdapter(new ArrayList<BespokeVo>(),
				activity, R.layout.item_mystyle_line_detail);
		clListView.setAdapter(adapter);
		List<BespokeVo> data = new ArrayList<BespokeVo>();
		for (int i = 0; i < 10; i++) {
			data.add(new BespokeVo());
		}
		adapter.addItems(data);
		Utils.setListViewHeightBasedOnChildren(clListView);
		final ScrollView sv_view = (ScrollView) view.findViewById(R.id.sv_view);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				sv_view.scrollTo(0, 0);
			}
		}, 500);
	}

	private void initLine(View view) {
		mapView = (MapView) view.findViewById(R.id.map_view);
	}

	private void addMarkersToMap() {

		aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(BEIJING)
				.title("北京市1").snippet("北京市1").draggable(true));
		
		MarkerOptions markerOption = new MarkerOptions();
		markerOption.position(BEIJING1);
		markerOption.title("北京市2").snippet("北京市2");
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.arrow));
		Marker marker2 = aMap.addMarker(markerOption);
		// marker旋转90度
		marker2.setRotateAngle(90);

		// 动画效果
		ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
		aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.position(BEIJING2).title("北京市3").icons(giflist)
				.draggable(true).period(10));

		drawMarkers();// 添加10个带有系统默认icon的marker
	}

	/**
	 * makers地图打点
	 *
	 */
	public void drawMarkers() {
		Marker marker = aMap.addMarker(new MarkerOptions()
				.position(latlng)
				.title("北京中心")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.draggable(true));
		marker.showInfoWindow();// 设置默认显示一个infowinfow
		showDriveLine();
	}

	/**
	 * 显示连线
	 */
	private void showDriveLine() {
		RouteSearch routeSearch = new RouteSearch(activity);
		routeSearch.setRouteSearchListener(new OnRouteSearchListener() {

			@Override
			public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {

			}

			@Override
			public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
				if (rCode == 0) {
					if (result != null && result.getPaths() != null
							&& result.getPaths().size() > 0) {
						DrivePath drivePath = result.getPaths().get(0);
//						aMap.clear();// 清理之前的图标
						DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
								activity, aMap, drivePath,
								result.getStartPos(), result.getTargetPos());
//						drivingRouteOverlay.removeFromMap();
						drivingRouteOverlay.addToMap();
						drivingRouteOverlay.zoomToSpan();
					}
				}
			}

			@Override
			public void onBusRouteSearched(BusRouteResult arg0, int arg1) {

			}
		});
		// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式
		// 第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路

		LatLonPoint start = new LatLonPoint(39.90403, 116.407525);
		LatLonPoint end = new LatLonPoint(39.90403 + 0.3, 116.407525);
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				start, end);

		DriveRouteQuery query = new DriveRouteQuery(fromAndTo, 2, null, null,
				"");
		routeSearch.calculateDriveRouteAsyn(query);
	}

	@OnClick(R.id.tv_view)
	void clickView(View view) {
		vp_pager.setCurrentItem(0);
	}

	@OnClick(R.id.tv_line)
	void clickLine(View view) {
		vp_pager.setCurrentItem(1);
	}
	/**
	 * Tab的背景颜色
	 * @param postion
	 */
	private void changeBtn(int postion) {
		if (postion == 0) {
			tv_view.setTextColor(Color.parseColor("#50BBDB"));
			tv_view.setBackgroundColor(Color.parseColor("#ffffff"));

			tv_line.setTextColor(Color.parseColor("#ffffff"));
			tv_line.setBackgroundColor(Color.parseColor("#50BBDB"));
		} else {
			tv_line.setTextColor(Color.parseColor("#50BBDB"));
			tv_line.setBackgroundColor(Color.parseColor("#ffffff"));

			tv_view.setTextColor(Color.parseColor("#ffffff"));
			tv_view.setBackgroundColor(Color.parseColor("#50BBDB"));
		}
	}

	@OnClick(R.id.tv_edit)
	void edit(View view) {
		skip(EditLineActivity.class);
	}

	@OnClick(R.id.iv_back)
	void back(View view) {
		finish();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (mapView != null) {
			mapView.onResume();
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		if (mapView != null) {
			mapView.onPause();
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mapView != null) {
			mapView.onSaveInstanceState(outState);
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mapView != null) {
			mapView.onDestroy();
		}
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_mystyle_line;
	}

}
