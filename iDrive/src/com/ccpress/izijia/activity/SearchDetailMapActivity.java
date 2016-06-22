package com.ccpress.izijia.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.entity.LinesDetailUploadEntity;
import com.ccpress.izijia.fragment.HomeFragment;
import com.ccpress.izijia.fragment.HomeTabFragment;
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
public class SearchDetailMapActivity extends TRSFragmentActivity implements AMapNaviListener,GeocodeSearch.OnGeocodeSearchListener, AMap.OnMarkerClickListener {
    public static String EXTRA_VIEWPOTS = "viewspots";
    public static String CITY_NAME=" ";
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
    private String HD_CITY="";
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
     * 初始化MAp
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
     * 清除makers
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
     * 添加makers
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

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);

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

            View view = View.inflate(SearchDetailMapActivity.this, R.layout.bottom_bar_map, null);
            mTxtName = (TextView) view.findViewById(R.id.name);
            mTxtAddress = (TextView) view.findViewById(R.id.address);

            final TextView mTxtNavi = (TextView) view.findViewById(R.id.navi);
            final TextView mTxtOk = (TextView) view.findViewById(R.id.route_show);
            mTxtOk.setText("确定");
            searchText=(AutoCompleteTextView) findViewById(R.id.searchText);
            searchText.setText(data.get(position).getName());
            searchText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            searchText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                 searchText.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         flag="searchText_map";
                         LatLng mTarget = aMap.getCameraPosition().target;//获取地图中心点坐标
                         getAddress(new LatLonPoint(mTarget.latitude, mTarget.longitude));

                         HomeFragment.CHANGE_CITY="HdMapActivity";
                         MapNowSearchActivity.CITY=HD_CITY;
                         Intent intent=new Intent(SearchDetailMapActivity.this,MapNowSearchActivity.class);
                         startActivity(intent);


                         finish();
                     }
                 });
            if(data != null && data.get(position)!= null){
                if(FLAG){
                    mTxtName.setText(data.get(POSITION).getName());
                    mTxtAddress.setText(data.get(POSITION).getDesc());
                    FLAG=false;
                }else{
                    mTxtName.setText(data.get(position).getName());
                    mTxtAddress.setText(data.get(position).getDesc());
                }
                mTxtOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flag="onOkClick";
                        LatLng mTarget = aMap.getCameraPosition().target;//获取地图中心点坐标
                        getAddress(new LatLonPoint(mTarget.latitude, mTarget.longitude));
                    }
                });

                //导航
                mTxtNavi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initGPS();
                        if(lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){

                            Intent intent = new Intent(SearchDetailMapActivity.this,
                                    SimpleNaviActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                        }else {
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
        Log.e("WLH", "onPause---");
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(mapPointReceiver);
        super.onDestroy();
        AMapNavi.getInstance(this).removeAMapNaviListener(this);
        mapView.onDestroy();
        Log.e("WLH", "onDestroy---");
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mapView.onSaveInstanceState(outState);
        Log.e("WLH", "onSaveInstanceState---");
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

    /**
     * 画线
     */
    @Override
    public void onCalculateRouteSuccess() {
        mProgressDialog.dismiss();
        Log.e("WLH", "onCalculateRouteSuccess----");
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
        Log.e("WLH", "lines map CalculateRouteFailure errcode:" + i);
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
    private String flag;
    private GeocodeSearch geocoderSearch;
    private ProgressDialog progDialog = null;
    /**
     * 获取位置信息
     * @param view
     */

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        showDialog();
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 显示进度条对话框
     */
    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在获取地址");
        progDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }



    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
            dismissDialog();
        String city=null;
        if (rCode == 0) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                if (result.getRegeocodeAddress().getCity().isEmpty() || result.getRegeocodeAddress().getCity().equals("") || result.getRegeocodeAddress().getCity() == null) {
                    city = result.getRegeocodeAddress().getProvince();
                } else {
                    city = result.getRegeocodeAddress().getCity();
                }

                try {
                    Thread.currentThread().sleep(1000);//阻断1秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }//阻断1秒
//                HomeFragment.CHANGE_CITY = "HdMapActivity";
//                HomeFragment.CITY = city;
//                HomeTabFragment.TAB_CITY = city;
//                Intent intent = new Intent();
//                intent.setAction(Constant.GD_CITY_CHANGE_ACTION);
//                sendBroadcast(intent);
//                if (flag.equals("onOkClick")) {
//                    Intent intent1 = new Intent();
//                    intent1.setAction(Constant.TOP_POPUP_INTERACT_LIST_CLICK_ACTION);
//                    intent1.putExtra(Constant.TOP_POPUP_INTERACT_LIST_CLICK_INDEX, 0);
//                    getApplication().sendBroadcast(intent1);
//                    finish();
                HomeFragment.CITY = city;
                HomeTabFragment.TAB_CITY = city;
                if (flag.equals("onOkClick")) {
                    finish();
                } else {
                    MapNowSearchActivity.CITY = city;
                    Intent intent2 = new Intent(SearchDetailMapActivity.this, MapNowSearchActivity.class);
                    startActivity(intent2);
                    finish();
                }
            }else {
                Toast.makeText(SearchDetailMapActivity.this, "获取位置信息失败",Toast.LENGTH_SHORT).show();

            }
        }else {
                Toast.makeText(SearchDetailMapActivity.this, "获取位置信息失败",Toast.LENGTH_SHORT).show();
            }


    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

}