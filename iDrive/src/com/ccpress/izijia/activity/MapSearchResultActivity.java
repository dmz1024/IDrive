package com.ccpress.izijia.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.ccpress.izijia.R;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.DialogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trs.app.TRSFragmentActivity;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yl on 2015/5/25.
 * 地图检索后，返回数据处理
 */
public class MapSearchResultActivity extends TRSFragmentActivity implements AMapNaviListener {
    private MapView mapView;
    private AMap aMap;
    private NaviLatLng mDestination;
    private AutoCompleteTextView searchText;
    private TextView mTextName;
    private TextView mTextAddr;
    private Dialog mProgressDialog;
    private Dialog mLinesDialog = null;
    private String name;
    private String addr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapsearchresult);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mTextName = (TextView) findViewById(R.id.name);
        mTextAddr = (TextView) findViewById(R.id.desc);
        searchText = (AutoCompleteTextView) this.findViewById(R.id.searchText);
        name = getIntent().getStringExtra("title");
        addr = getIntent().getStringExtra("address");
        if(!StringUtil.isEmpty(name)){
            mTextName.setText(name);
            searchText.setHint(name);
        }
        if(!StringUtil.isEmpty(addr)){
            mTextAddr.setText(addr);
        }
       // initGPS();
        initDestination();
        init();
    }
    private void initGPS() {
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        //判断GPS模块是否开启，如果没有则开启
        if (!lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getBaseContext(), "GPS没有打开,请打开GPS!", Toast.LENGTH_SHORT).show();
            //转到手机设置界面，用户设置GPS
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0); //设置完成后返回到原来的界面
        }
    }

    /**
     * 终点数据
     */
    private void initDestination(){
        String geo = getIntent().getStringExtra("geo");
        if(!StringUtil.isEmpty(geo)){
            String str[] = geo.split(",");
            if(str != null && str.length == 2){
                Double lati = Double.parseDouble(str[0]);
                Double longti = Double.parseDouble(str[1]);
                mDestination = new NaviLatLng(lati, longti);
            }
        }
    }

    /**
     * 初始化MAP
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.getUiSettings().setZoomControlsEnabled(false);
            if(mDestination != null){
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(mDestination.getLatitude(), mDestination.getLongitude()), (float) 14);
                aMap.animateCamera(update);
                addMarkerToMap();
            }
        }
        AMapNavi.getInstance(this).setAMapNaviListener(this);
        mProgressDialog = DialogUtil.getProgressdialog(this, null);
    }
    private void addMarkerToMap(){
        MarkerOptions markerOption1 = new MarkerOptions().anchor(0.5f, 0.5f)
                .position(new LatLng(mDestination.getLatitude(), mDestination.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_address_small))
                .perspective(true).draggable(true).period(50);
        aMap.addMarker(markerOption1);
    }
    public void onLinesClick(View v){//线路
        showLinesDialog();
    }
    public void onNaviClick(View v){//导航--默认按距离最短导航
        calculateDriverRoute(AMapNavi.DrivingFastestTime);
    }

    /**
     * 显示线路Dialog
     */
    private void showLinesDialog(){
        if(mLinesDialog == null){

            mLinesDialog = new Dialog(this, R.style.popFromBottomdialog);
            View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_lines, null);
            mLinesDialog.setContentView(contentView);
            Window dialogWindow = mLinesDialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.FILL_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setAttributes(lp);

            LinearLayout mRecommand = (LinearLayout) contentView.findViewById(R.id.btn_recommend);
            LinearLayout mAvoidCongestion = (LinearLayout) contentView.findViewById(R.id.btn_avoid_Congestion);
            LinearLayout mNoExpressWays = (LinearLayout) contentView.findViewById(R.id.btn_NoExpressways);
            LinearLayout mSaveMoney = (LinearLayout) contentView.findViewById(R.id.btn_SaveMoney);
            LinearLayout mShortDistance = (LinearLayout) contentView.findViewById(R.id.btn_shortDistance);

            mRecommand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLinesDialog.dismiss();
                    calculateDriverRoute(AMapNavi.DrivingDefault);
                }
            });
            mAvoidCongestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLinesDialog.dismiss();
                    calculateDriverRoute(AMapNavi.DrivingAvoidCongestion);
                }
            });
            mNoExpressWays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLinesDialog.dismiss();
                    calculateDriverRoute(AMapNavi.DrivingNoExpressways);
                }
            });
            mSaveMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLinesDialog.dismiss();
                    calculateDriverRoute(AMapNavi.DrivingSaveMoney);
                }
            });
            mShortDistance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLinesDialog.dismiss();
                    calculateDriverRoute(AMapNavi.DrivingFastestTime);
                }
            });

        }else {
            if(mLinesDialog.isShowing()){
                mLinesDialog.dismiss();
                mLinesDialog = null;
                return;
            }
        }
        mLinesDialog.show();
    }

    /**
     * 线路规划
     * @param driveMode
     */
    private void calculateDriverRoute(int driveMode){
        mProgressDialog.show();
        //起点终点列表
        ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
        ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
        mStartPoints.add(new NaviLatLng(iDriveApplication.app().getLocation().getLatitude(), iDriveApplication.app().getLocation().getLongitude()));
        mEndPoints.add(mDestination);
        AMapNavi.getInstance(this).calculateDriveRoute(mStartPoints,
                mEndPoints, null, driveMode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        mProgressDialog.dismiss();
        Intent intent = new Intent(this,
                SimpleNaviActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    /**
     * 线路显示
     * @param i
     */
    @Override
    public void onCalculateRouteFailure(int i) {
        mProgressDialog.dismiss();
        if(i==2){
            Toast.makeText(this,"网络超时", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this,"路线规划失败，请重试", Toast.LENGTH_SHORT).show();
        android.util.Log.e("WLH", "info map CalculateRouteFailure errcode:" + i);
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
}
