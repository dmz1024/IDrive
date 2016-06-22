package com.ccpress.izijia.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.*;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.view.RouteOverLay;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.entity.LinesDetailUploadEntity;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.DialogUtil;
import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.trs.app.TRSFragmentActivity;
import com.trs.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WLH on 2015/5/19 14:02.
 */
public class LinesDetailMapActivity extends TRSFragmentActivity implements AMapNaviListener, AMap.OnMarkerClickListener {
    public static String EXTRA_VIEWPOTS = "viewspots";
    public static String NAME_TEXT=" ";
    public static int POSITION=0;
    public static Boolean FLAG=false;
    public static String TOPBAR="topbar";
    private ViewPager pager;
    private MapView mapView;
    private AMap aMap;
    private RouteOverLay mRouteOverLay;
    private    LocationManager lm;
    private ArrayList<LinesDetailUploadEntity.ViewSpot> data = new ArrayList<LinesDetailUploadEntity.ViewSpot>();
    private ArrayList<Marker> markerArrayList = new ArrayList<>();

    private Dialog mProgressDialog;
    private AutoCompleteTextView searchText;
    public static final String MAP_POINT_ACTION  = "com.ccpress.izijia.LinesDetailMapActivity.mappoints";

    Polyline polyline;

    private float zoomLevel;
    private float defaultZoomLevel = 10f;

    private BroadcastReceiver mapPointReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           // initGPS();
            if(intent!= null && intent.getAction().equals(MAP_POINT_ACTION)){
                ArrayList<LinesDetailUploadEntity.ViewSpot> mapPoint = (ArrayList<LinesDetailUploadEntity.ViewSpot>)intent.getSerializableExtra(LinesDetailMapActivity.EXTRA_VIEWPOTS);
                if(data != null){
                    data.clear();
                    data.addAll(mapPoint);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lines_upload_map);
        init( savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(MAP_POINT_ACTION);
        registerReceiver(mapPointReceiver, filter);
    }

    /**
     * GPS开关检查
     */
    private void initGPS() {
         lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        //判断GPS模块是否开启，如果没有则开启
        if (!lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getBaseContext(), "GPS没有打开,请打开GPS!", Toast.LENGTH_SHORT).show();
            //转到手机设置界面，用户设置GPS
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0); //设置完成后返回到原来的界面
        }
    }

    /**
     * 布局控件设置，初始化MAP
     * @param savedInstanceState
     */
    private void init( Bundle savedInstanceState){
        if (TOPBAR.equals("LinesDetailUserUploadActivity")){
            findViewById(R.id.top_bar_productdetail).setVisibility(View.GONE);
        }
        data = (ArrayList<LinesDetailUploadEntity.ViewSpot>)getIntent().getSerializableExtra(EXTRA_VIEWPOTS);
        zoomLevel = getIntent().getFloatExtra("zoomLevel", defaultZoomLevel);

        mProgressDialog = DialogUtil.getProgressdialog(this, null);

        pager = (ViewPager) findViewById(R.id.upload_map_pager);
        mapView = (MapView) findViewById(R.id.go_map);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setScaleControlsEnabled(false);
            aMap.setOnMarkerClickListener(this);
            aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                @Override
                public void onMapLoaded() {
                    //设置中心点和缩放比例
                    if(markerArrayList.size() > 0){
                        Marker mark = markerArrayList.get(POSITION);
                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(mark.getPosition()));
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
                    }
                }
            });
//            addMarkers();
        }

        mRouteOverLay = new RouteOverLay(aMap, null);
        pager.setAdapter(mAdapter);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("WLH", "onPageSelected position : "+ position +" markerArrayList.size:"+markerArrayList.size());
                if(data != null && position >= 0 && position < data.size()){
                    LinesDetailUploadEntity.ViewSpot spot = data.get(position);
                    if(!StringUtil.isEmpty(spot.getGeo())){
                        LatLng latLng = stringToNaviLatLng(spot.getGeo());
                        if(latLng!= null && aMap!= null){
//                            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 8));
                            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                        }
                    }
                    //坐标显示到地图
                    for(int j = 0; j < markerArrayList.size(); j++){
                        if(markerArrayList.get(position) != null){
                            if(markerArrayList.get(position) != null){
                                if(j == position){
                                    markerArrayList.get(j).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_middle));
                                }else{
                                    markerArrayList.get(j).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_address_small));
                                }
                            }

                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        AMapNavi.getInstance(this).setAMapNaviListener(this);

    }

    /**
     * 清除Markers
     */
    private void clearMarkers(){
        if(markerArrayList != null){
            for (Marker marker: markerArrayList){
                marker.remove();
            }
            markerArrayList.clear();

        }
        if(polyline!= null){
            polyline.remove();
        }
    }

    /**
     * 添加Markers
     */
    private void addMarkers() {
        Log.e("WLH", "data is null?"+(data==null) +" aMap is null?"+(aMap==null));

        clearMarkers();
        ArrayList<LatLng> latLngs = new ArrayList<>();
        if(data != null){
            for(int i=0; i < data.size();i++){
                LinesDetailUploadEntity.ViewSpot spot = data.get(i);
                if(!StringUtil.isEmpty(spot.getGeo())){
                    LatLng latLng = stringToNaviLatLng(spot.getGeo());
                    if(latLng!= null){
                        latLngs.add(latLng);
                        markerArrayList.add(addMarkerToMap(latLng));
                        if(i==POSITION){
                            //Toast.makeText(this, "Integer.parseInt(POSITION): "+ String.valueOf(POSITION), Toast.LENGTH_SHORT).show();
//                            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 8));
                            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                            markerArrayList.get(POSITION).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_middle));
                        }
                    }
                }
            }
        }
        if(latLngs.size() >= 2 && getIntent().getIntExtra(LinesDetailUserUploadActivity.EXTRA_ACT_TYPE, 0) != LinesDetailUserUploadActivity.Destination){
            polyline = aMap.addPolyline((new PolylineOptions())
                    .addAll(latLngs)
                    .width(10).setDottedLine(false).geodesic(true)
                    .color(getResources().getColor(R.color.idrive_blue)));
        }
    }

    private Marker addMarkerToMap(LatLng latLng){
        MarkerOptions markerOption1 = new MarkerOptions().anchor(0.5f, 0.5f)
                .position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_address_small))
                .perspective(true).draggable(true).period(50);
        Marker marker = aMap.addMarker(markerOption1);
        return marker;
    }

    /**
     * 导航坐标
     * @param geo
     * @return
     */
    private LatLng stringToNaviLatLng(String geo){
        if(!StringUtil.isEmpty(geo)){
            String str[] = geo.split(",");
            if(str != null && str.length == 2){
                Double longti = Double.parseDouble(str[0]);
                Double lati = Double.parseDouble(str[1]);
                return  new LatLng(lati, longti);
            }
        }
        return null;
    }
    private TextView mTxtName;
    private TextView mTxtAddress;
    private PagerAdapter mAdapter = new PagerAdapter() {
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            View view = View.inflate(LinesDetailMapActivity.this, R.layout.bottom_bar_map, null);
             mTxtName = (TextView) view.findViewById(R.id.name);
             mTxtAddress = (TextView) view.findViewById(R.id.address);

            final TextView mTxtNavi = (TextView) view.findViewById(R.id.navi);
            final TextView mTxtRoute = (TextView) view.findViewById(R.id.route_show);
            searchText=(AutoCompleteTextView) findViewById(R.id.searchText);
            searchText.setText(data.get(position).getName());
            searchText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            searchText.setInputType(EditorInfo.TYPE_CLASS_TEXT);

            if(data != null && data.get(position)!= null){
                if(FLAG){
                    mTxtName.setText(data.get(POSITION).getName());
                    mTxtAddress.setText(data.get(POSITION).getDesc());
                    mTxtRoute.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mTxtRoute.setVisibility(View.GONE);
                            mTxtNavi.setVisibility(View.VISIBLE);
                            if(!StringUtil.isEmpty(data.get(POSITION).getGeo())){
                                LatLng latLng = stringToNaviLatLng(data.get(POSITION).getGeo());
                                if(latLng != null && iDriveApplication.app().getLocation()!=null ){
                                    Log.e("WLH", "type："+getIntent().getIntExtra(LinesDetailUserUploadActivity.EXTRA_ACT_TYPE, 0));
                                    calculateDriverRoute(AMapNavi.DrivingFastestTime, new NaviLatLng(latLng.latitude, latLng.longitude), null);
                                }
                            }
                        }
                    });

                    FLAG=false;
                }else{
                    mTxtName.setText(data.get(position).getName());
                    mTxtAddress.setText(data.get(position).getDesc());
                    //线路规划
                    mTxtRoute.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mTxtRoute.setVisibility(View.GONE);
                            mTxtNavi.setVisibility(View.VISIBLE);
                            if(!StringUtil.isEmpty(data.get(position).getGeo())){
                                LatLng latLng = stringToNaviLatLng(data.get(position).getGeo());
                                if(latLng != null && iDriveApplication.app().getLocation()!=null ){
                                    Log.e("WLH", "type："+getIntent().getIntExtra(LinesDetailUserUploadActivity.EXTRA_ACT_TYPE, 0));
                                    calculateDriverRoute(AMapNavi.DrivingFastestTime, new NaviLatLng(latLng.latitude, latLng.longitude), null);
                                }
                            }
                        }
                    });
                }
                    //导航
                mTxtNavi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            initGPS();
                            if(lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
                                mTxtNavi.setVisibility(View.GONE);
                                mTxtRoute.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(LinesDetailMapActivity.this,
                                        SimpleNaviActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                              startActivity(intent);
                            }else {
                                Toast.makeText(getBaseContext(), "GPS没有打开,不能开启导航!", Toast.LENGTH_SHORT).show();
                            }
                    }
                });

            }

            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            if(data != null){
                return data.size();
            }
            return 0;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    };



    @Override
    public void onResume() {
        super.onResume();

        mapView.onResume();
        pager.setCurrentItem(0);
        if(aMap ==null){
            aMap = mapView.getMap();
        }
        addMarkers();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        //mapView.onPause();
//        Log.e("WLH", "onPause---");
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(mapPointReceiver);
        super.onDestroy();
        AMapNavi.getInstance(this).removeAMapNaviListener(this);
        mapView.onDestroy();
//        Log.e("WLH", "onDestroy---");
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mapView.onSaveInstanceState(outState);
//        Log.e("WLH", "onSaveInstanceState---");
    }

    /**
     * 线路规划
     * @param driveMode
     * @param mDestination
     * @param wayPoints
     */
    private void calculateDriverRoute(int driveMode, NaviLatLng mDestination, List<NaviLatLng> wayPoints){
        mProgressDialog.show();
        //起点终点列表
        ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
        ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
        mStartPoints.add(new NaviLatLng(iDriveApplication.app().getLocation().getLatitude(), iDriveApplication.app().getLocation().getLongitude()));
        mEndPoints.add(mDestination);
        AMapNavi.getInstance(this).calculateDriveRoute(mStartPoints,
                mEndPoints, wayPoints, driveMode);
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

    @Override
    public void onCalculateRouteSuccess() {
        mProgressDialog.dismiss();
//        Log.e("WLH", "onCalculateRouteSuccess----");
        AMapNaviPath naviPath =  AMapNavi.getInstance(this).getNaviPath();
               if (naviPath == null) {
                        return;
                  }
        // 获取路径规划线路，显示到地图上
                 mRouteOverLay.setRouteInfo(naviPath);
                 mRouteOverLay.addToMap();

    }

    @Override
    public void onCalculateRouteFailure(int i) {
        mProgressDialog.dismiss();
        if(i==2){
            Toast.makeText(this, "网络超时", Toast.LENGTH_SHORT).show();
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
    public boolean onMarkerClick(Marker marker) {

        for(int i = 0; i < markerArrayList.size(); i++){
            if(markerArrayList.get(i) != null){
                if(markerArrayList.get(i).getPosition().equals(marker.getPosition())){
                    markerArrayList.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_middle));
                    pager.setCurrentItem(i);
//                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerArrayList.get(i).getPosition(), (float) 8));
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerArrayList.get(i).getPosition(), zoomLevel));
                }else {
                    markerArrayList.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_address_small));
                }
            }

        }
        return false;
    }

    /**
     * 返回键
     * @param view
     */
    public void onBtnBackClick(View view) {
        mRouteOverLay.removeFromMap();
        Intent intent = new Intent();
        intent.setAction(Constant.MAP_ROUTE_CLEAR_ACTION);
        sendBroadcast(intent);
        finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            mRouteOverLay.removeFromMap();
            Intent intent = new Intent();
            intent.setAction(Constant.MAP_ROUTE_CLEAR_ACTION);
            sendBroadcast(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
