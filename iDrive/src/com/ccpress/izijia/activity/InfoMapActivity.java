package com.ccpress.izijia.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.*;
import com.amap.api.navi.view.RouteOverLay;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.CustomUtil;
import com.ccpress.izijia.util.DetailStatusUtil;
import com.ccpress.izijia.util.DialogUtil;
import com.trs.app.TRSFragmentActivity;
import com.trs.util.StringUtil;
import com.trs.wcm.callback.BaseDataAsynCallback;


import java.util.ArrayList;

/**
 * Created by WLH on 2015/5/14 10:22.
 */
public class InfoMapActivity extends TRSFragmentActivity implements AMapNaviListener {

    public static final String EXTRA_HASLINES = "HASLIENS";
    public static final String EXTRA_MYCOLLECT="MYCOLLECT";

    private MapView mapView;
    private AMap aMap;
    private NaviLatLng mDestination;
    private RouteOverLay mRouteOverLay;
    private Dialog mProgressDialog;
    private Dialog mLinesDialog = null;
    private LocationManager lm;
    private TextView mTextName;
    private TextView mTextAddr;
    private TextView mTextcustom;
    private String name;
    private String addr;
    private String lid ;
    private String detailType;

    private ImageView mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_map);
        mapView = (MapView) findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);
        name = getIntent().getStringExtra("name");
        addr = getIntent().getStringExtra("addr");
        lid = getIntent().getStringExtra("lid");
        detailType = getIntent().getStringExtra("detailType");

        initStatus();
        initDestination();
        init();
        indata();

        if(!getIntent().getBooleanExtra(EXTRA_HASLINES, true)){
            mImg.setVisibility(View.GONE);
            findViewById(R.id.layout).setVisibility(View.GONE);
            findViewById(R.id.view_divider).setVisibility(View.GONE);
            mTextName.setGravity(Gravity.CENTER);
        }

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
     * 填充数据
     */
    private void  indata(){

        mImg = (ImageView) findViewById(R.id.img);
        mTextName = (TextView) findViewById(R.id.name);
        mTextAddr = (TextView) findViewById(R.id.desc);
        mTextcustom=(TextView) findViewById(R.id.lines);


        if(!StringUtil.isEmpty(name)){
            mTextName.setText(name);
        }
        if(!StringUtil.isEmpty(addr)){
            mTextAddr.setText(addr);
        }
        Log.e("LID", String.valueOf(lid));
        try {
            Thread.currentThread().sleep(1000);//阻断1秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(statusUtil.IsCustom()){
            mTextcustom.setText("已定制");
        }else {
            mTextcustom.setText("定制");
        }
    }

    /**
     * 初始化地图
     */
    private void init() {
        findViewById(R.id.search).setVisibility(View.GONE);

        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setScaleControlsEnabled(false);
            if(mDestination != null){
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(mDestination.getLatitude(), mDestination.getLongitude()), (float) 14);
                aMap.animateCamera(update);
                addMarkerToMap();
            }
        }
        mRouteOverLay = new RouteOverLay(aMap, null);
        AMapNavi.getInstance(this).setAMapNaviListener(this);
        mProgressDialog = DialogUtil.getProgressdialog(this, null);

    }

    private void addMarkerToMap(){
        MarkerOptions markerOption1 = new MarkerOptions().anchor(0.5f, 0.5f)
                .position(new LatLng(mDestination.getLatitude(), mDestination.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_address_small))
                .perspective(true).draggable(true).period(50);
        aMap.addMarker(markerOption1);
    }

    private void initDestination(){
        String geo = getIntent().getStringExtra("geo");
        if(!StringUtil.isEmpty(geo)){
            String str[] = geo.split(",");
            if(str != null && str.length == 2){
                Double longti = Double.parseDouble(str[0]);
                Double lati = Double.parseDouble(str[1]);
                Log.e("YLYL",longti+""+"----"+lati+"");
                mDestination = new NaviLatLng(lati, longti);
            }
        }
    }

    public void onLinesClick(View v){setCustom();}//定制
    public void onRouteClick(View v){//路径规划
        findViewById(R.id.navi).setVisibility(View.VISIBLE);
        findViewById(R.id.route_show).setVisibility(View.GONE);
        calculateDriverRoute(AMapNavi.DrivingFastestTime);
    }
    public void onNaviClick(View v){//导航--默认按距离最短导航
        initGPS();
        if(lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
            findViewById(R.id.navi).setVisibility(View.GONE);
            findViewById(R.id.route_show).setVisibility(View.VISIBLE);
            Intent intent = new Intent(InfoMapActivity.this,
                    SimpleNaviActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }else {
            Toast.makeText(getBaseContext(), "GPS没有打开,不能开启导航!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取状态
     */
    private void initStatus() {
        statusUtil.getDetailStatus(lid, new BaseDataAsynCallback() {
            @Override
            public void onDataLoad(String result, boolean bIsChanged) {

            }
        });
    }
    //DETAIL_TYPE_PARK
    DetailStatusUtil statusUtil = new DetailStatusUtil(this);
    public void setCustom(){

        CustomUtil.CustomOrCancel(InfoMapActivity.this, lid,
                statusUtil.IsCustom(), new CustomUtil.ResultCallback() {
                    @Override
                    public void callback(boolean isSuccess) {
                        if (isSuccess) {
                            statusUtil.setIsCustom(true);
                            mTextcustom.setText("已定制");
                        }
                    }
                });
    }

    /**
     * 展示LinesDialog
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
     * 线路划线
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

    @Override
    public void onCalculateRouteSuccess() {
        mProgressDialog.dismiss();
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
            Toast.makeText(this,"网络超时", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this,"路线规划失败，请重试", Toast.LENGTH_SHORT).show();
        Log.e("WLH", "info map CalculateRouteFailure errcode:"+i);
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
