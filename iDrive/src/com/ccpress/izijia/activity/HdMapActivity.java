package com.ccpress.izijia.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.fragment.HomeFragment;
import com.ccpress.izijia.fragment.HomeTabFragment;
import com.ccpress.izijia.iDriveApplication;
import com.froyo.commonjar.utils.SpUtil;
import com.trs.app.TRSFragmentActivity;

/**
 * Created by WLH on 2015/5/8 15:14.
 * 互动→城市列表→附近地图界面
 */
public class HdMapActivity  extends TRSFragmentActivity implements GeocodeSearch.OnGeocodeSearchListener {

    private MapView mapView;
    private AMap aMap;
    private ImageView addressBack;
    private EditText searchText_map;
    private ProgressDialog progDialog = null;
    private GeocodeSearch geocoderSearch;
    public static  LatLonPoint lp;
    private String  cityCode;
    public static String flag=null;
   public  static String HD_CITY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hd_map);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        searchText_map= (EditText)findViewById(R.id.searchText_map);
        //附近搜索
        searchText_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag="searchText_map";
                LatLng mTarget = aMap.getCameraPosition().target;//获取地图中心点坐标
                lp=new LatLonPoint(mTarget.latitude, mTarget.longitude);
                getAddress(lp);

            }
        });
        init();

    }
   private void initGPS() {
       LocationManager  lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
       //判断GPS模块是否开启，如果没有则开启
       if (!lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
           Toast.makeText(getBaseContext(), "GPS没有打开,请打开GPS!", Toast.LENGTH_SHORT).show();
           //转到手机设置界面，用户设置GPS
           Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
           startActivityForResult(intent, 0); //设置完成后返回到原来的界面
       }
   }

    /**
     * 初始化MAP
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setScaleControlsEnabled(false);
            if(iDriveApplication.app().getLocation() != null){
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(iDriveApplication.app().getLocation().getLatitude(), iDriveApplication.app().getLocation().getLongitude()), (float) 14);
                aMap.animateCamera(update);
            }
        }
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);

    }

    /**
     *  搜索-点击事件
     * @param view
     */
    public void onSearClick(View view){
        flag="searchText_map";
        LatLng mTarget = aMap.getCameraPosition().target;//获取地图中心点坐标
        lp=new LatLonPoint(mTarget.latitude, mTarget.longitude);
        getAddress(new LatLonPoint(mTarget.latitude, mTarget.longitude));

    }

    /**
     * 获取位置信息
     * @param view
     */
    public void onOkClick(View view){
        flag="onOkClick";
       LatLng mTarget = aMap.getCameraPosition().target;//获取地图中心点坐标
        Log.e("WLH", "您选择的坐标longitude："+ mTarget.longitude + " latitude:"+mTarget.latitude);
        getAddress(new LatLonPoint(mTarget.latitude, mTarget.longitude));

    }

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 返回数据处理
     * @param result
     * @param rCode
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        dismissDialog();
        String city=null;
        if (rCode == 0) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                if (result.getRegeocodeAddress().getCity().isEmpty()||result.getRegeocodeAddress().getCity().equals("")||result.getRegeocodeAddress().getCity()==null){
                    city = result.getRegeocodeAddress().getProvince();
                }else {
                    city = result.getRegeocodeAddress().getCity();
                }
                cityCode = result.getRegeocodeAddress().getCityCode();


                try {
                    Thread.currentThread().sleep(1000);//阻断1秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }//阻断1秒

//                HomeFragment.CHANGE_CITY="HdMapActivity";
//                HomeFragment.CITY=city;
//                HomeTabFragment.TAB_CITY=city;
//                Intent intent = new Intent();
//                intent.setAction(Constant.GD_CITY_CHANGE_ACTION);
//                sendBroadcast(intent);
//                if (flag.equals("onOkClick")){
//                    Intent intent1 = new Intent();
//                    intent1.setAction(Constant.TOP_POPUP_INTERACT_LIST_CLICK_ACTION);
//                    intent1.putExtra(Constant.TOP_POPUP_INTERACT_LIST_CLICK_INDEX, 0);
//                    getApplication().sendBroadcast(intent1);
//                    finish();
//                }else {
//                    HomeFragment.CHANGE_CITY="HdMapActivity";
//                    MapNowSearchActivity.CITY=HD_CITY;
//                    MapNowSearchActivity.CITY=city;
//                    Intent intent2=new Intent(HdMapActivity.this,MapNowSearchActivity.class);
//                    startActivity(intent2);
//                    finish();
//                }
                HomeFragment.CITY=city;
                HomeTabFragment.TAB_CITY=city;
                if (flag.equals("onOkClick")){
                    finish();
                }else {
                    MapNowSearchActivity.CITY=HD_CITY;
                    MapNowSearchActivity.CITY=city;
                    Intent intent2=new Intent(HdMapActivity.this,MapNowSearchActivity.class);
                    startActivity(intent2);
                    finish();
                }



            } else {
                Toast.makeText(HdMapActivity.this, "获取位置信息失败",Toast.LENGTH_SHORT).show();

            }
        } else{
            Toast.makeText(HdMapActivity.this, "获取位置信息失败", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

}
