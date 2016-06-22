package com.ccpress.izijia.fragment;

import android.app.Dialog;
import android.content.*;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.*;
import android.provider.*;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.location.core.CoordinateConvert;
import com.amap.api.location.core.GeoPoint;
import com.amap.api.maps.*;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.*;
import com.ccpress.izijia.adapter.CarTeamAdapter;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.entity.CarTeamListEntity;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.DialogUtil;
import com.ccpress.izijia.util.LocationUtil;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.LineDetailVo;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.SpUtil;
import com.froyo.commonjar.utils.Utils;
import com.trs.wcm.LoadWCMJsonTask;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * Created by yl
 * Date: 2015/5/25
 * Time: 15:06
 * 地图fragment
 */
public class MapFragment extends Fragment implements LocationSource,AMap.OnMarkerClickListener,
        AMapLocationListener ,GeocodeSearch.OnGeocodeSearchListener,AMapNaviListener {
    private UiSettings mUiSettings;
    private RelativeLayout rl_trafficlayer,top_popup_shelter,currentview,rl_time_map,rl_carteam;
    private LinearLayout ll_map_longclick;
    private TextView addressname,nearbySearch,lines,route_show,navi,time;
    private ImageView trafficlayer_close,trafficlayer_open,img_currentview,time_left,time_right;
    private NaviLatLng mDestination;
    private Marker longMarker;
    private GeocodeSearch geocoderSearch;
    private MapView mapView;
    private TextView nearby_map,help_map;
    private EditText searchText_map;
//    private MarkerOptions markerOptionCar;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private Boolean isOpentraffic=false;
//    private Boolean isCurrentview=false;
    private Dialog mProgressDialog;
    private LocationManager lm;
    private LocationManagerProxy mAMapLocationManager;
    private ArrayList<CarTeamListEntity> arrayList = new ArrayList<CarTeamListEntity>();
    private Dialog mLinesDialog = null;
    private Marker marker;// 定位雷达小图标
    private Marker markerLoction;
//    private ImageView back_start;
    private RouteOverLay mRouteOverLay;
    private SpUtil sp;
    private Marker markerCar;
    private ArrayList<Marker> arrayCar= new ArrayList<>();
    public  static String flag="AA";
    private String str="STR";
    public static String CAR_STATUS="FALSE";

    RequestQueue mQueue;
    private MapFragmentBroadcastReceiver mReceiver = new MapFragmentBroadcastReceiver();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView)v. findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 必须要写
        sp = new SpUtil(getActivity());

        mQueue = Volley.newRequestQueue(getActivity());
        init(v);
        return v;
    }


    /**
     * 检查GPD是否开启
     */
    private void initGPS(){
         lm=(LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //判断GPS模块是否开启，如果没有则开启
        if(!lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
            Toast.makeText(getActivity().getBaseContext(), "GPS没有打开,请打开GPS!", Toast.LENGTH_SHORT).show();
            //转到手机设置界面，用户设置GPS
            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent,0); //设置完成后返回到原来的界面
        }

    }

    private void initBroadcastReceiver() {
        IntentFilter filters = new IntentFilter();
        filters.addAction(Constant.CART_TEAM_STATUS_ACTION);
        filters.addAction(Constant.MAP_ROUTE_CLEAR_ACTION);
        getActivity().registerReceiver(mReceiver, filters);
    }
    private class MapFragmentBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
                if (sp.getStringValue(Const.CAR_TEAM_ID)!=null){
                    if (arrayCar!=null){
                        for (int i = 0; i < arrayCar.size(); i++){
                            arrayCar.get(i).hideInfoWindow();
                            arrayCar.get(i).remove();
                        }
                        arrayCar.clear();
                    }
                    aMap.removecache();


            }
            if (action.equals(Constant.MAP_ROUTE_CLEAR_ACTION)){
                if (mRouteOverLay!=null){
                    mRouteOverLay.removeFromMap();
                }
            }
        }
    }
    /**
     * 初始化AMap对象
     */
    private void init(View view) {

        addressname = (TextView) view.findViewById(R.id.addressname);
        nearbySearch = (TextView) view.findViewById(R.id.nearby_search);
        searchText_map = (EditText) view.findViewById(R.id.searchText_map);
        currentview = (RelativeLayout) view.findViewById(R.id.currentview);
        img_currentview = (ImageView) view.findViewById(R.id.img_currentview);
        time_left = (ImageView) view.findViewById(R.id.time_left);
        time = (TextView) view.findViewById(R.id.time);
        time_right = (ImageView) view.findViewById(R.id.time_right);
        lines = (TextView) view.findViewById(R.id.lines);
        navi = (TextView) view.findViewById(R.id.navi);
        route_show = (TextView) view.findViewById(R.id.route_show);
        top_popup_shelter = (RelativeLayout) view.findViewById(R.id.top_popup_shelter);
        rl_time_map = (RelativeLayout) view.findViewById(R.id.rl_time_map);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setLocationSource(this);
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 是否显示定位按钮
            aMap.setMyLocationEnabled(true);// 是否可触发定位并显示定位层
            aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                }
            });
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
            aMap.setOnMarkerClickListener(this);
            if (iDriveApplication.app().getLocation() != null) {
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(iDriveApplication.app().getLocation().getLatitude(), iDriveApplication.app().getLocation().getLongitude()), (float) 14);
                aMap.animateCamera(update);

                MarkerOptions markerOption1 = new MarkerOptions().anchor(0.5f, 0.5f)
                        .position(new LatLng(iDriveApplication.app().getLocation().getLatitude(), iDriveApplication.app().getLocation().getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_address_small))
                        .perspective(true).draggable(true).period(50);

                markerLoction =aMap.addMarker(markerOption1);
            }
            //地图长按事件
            aMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(final LatLng latLng) {
                    route_show.setVisibility(View.VISIBLE);
                    navi.setVisibility(View.GONE);
                    mDestination = new NaviLatLng(latLng.latitude, latLng.longitude);
                    getAddress(new LatLonPoint(latLng.latitude, latLng.longitude));
                    MarkerOptions markerOption1 = new MarkerOptions().anchor(0.5f, 0.5f)
                            .position(new LatLng(mDestination.getLatitude(), mDestination.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_address_small))
                            .perspective(true).draggable(true).period(50);
                   longMarker= aMap.addMarker(markerOption1);
                    displayDialog();
                    nearbySearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), MapSearchActivity.class);
                            getActivity().startActivity(intent);
                        }
                    });
                    lines.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showLinesDialog(mDestination);
                        }
                    });

                    route_show.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            calculateDriverRoute(AMapNavi.DrivingFastestTime, mDestination);
                            route_show.setVisibility(View.GONE);
                            navi.setVisibility(View.VISIBLE);
                        }
                    });
                    //导航
                    navi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initGPS();
                            if(lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
                                route_show.setVisibility(View.VISIBLE);
                                navi.setVisibility(View.GONE);
                                Intent intent = new Intent(getActivity(),
                                        SimpleNaviActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                getActivity().startActivity(intent);
                            }else {
                                Toast.makeText(getActivity().getBaseContext(), "GPS没有打开,不能开启导航!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            });
        }
        mRouteOverLay = new RouteOverLay(aMap, null);
        AMapNavi.getInstance(getActivity()).setAMapNaviListener(this);
        geocoderSearch = new GeocodeSearch(getActivity());
        geocoderSearch.setOnGeocodeSearchListener(this);
        mProgressDialog = DialogUtil.getProgressdialog(getActivity(), null);

        rl_trafficlayer = (RelativeLayout) view.findViewById(R.id.rl_trafficlayer);
        trafficlayer_close = (ImageView) view.findViewById(R.id.trafficlayer_close);
        trafficlayer_open = (ImageView) view.findViewById(R.id.trafficlayer_open);

        //路况信息
        rl_trafficlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpentraffic) {
                    aMap.setTrafficEnabled(true);
                    isOpentraffic = true;
                    setTraffic(true);
                } else {
                    aMap.setTrafficEnabled(false);
                    isOpentraffic = false;
                    setTraffic(false);
                }
            }
        });
        //附近搜索
        nearby_map = (TextView) view.findViewById(R.id.nearby_map);
        nearby_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapSearchActivity.class);
                getActivity().startActivity(intent);
            }
        });
        searchText_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapFullSearchActivity.class);
                getActivity().startActivity(intent);
            }
        });

        //救援
        help_map = (TextView) view.findViewById(R.id.help_map);
        help_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGPS();
                Intent intent = new Intent(getActivity(), HelpActivity.class);
                LatLng mTarget = aMap.getCameraPosition().target;//获取地图中心点坐标
                getAddress(new LatLonPoint(mTarget.latitude, mTarget.longitude));
                getActivity().startActivity(intent);
            }
        });
        img_currentview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InfoDetailActivity.class);
                getActivity().startActivity(intent);
            }
        });
        top_popup_shelter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longMarker.remove();
                mRouteOverLay.removeFromMap();
                aMap.removecache();
                dismissDialog();
            }
        });
        ll_map_longclick = (LinearLayout) view.findViewById(R.id.ll_map_longclick);
        rl_carteam = (RelativeLayout) view.findViewById(R.id.rl_carteam);
        //车队按钮点击事件
        rl_carteam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (arrayCar!=null){
                    for (int i = 0; i < arrayCar.size(); i++){
                        arrayCar.get(i).remove();
                    }
                    arrayCar.clear();
                }
                aMap.removecache();
                if (CAR_STATUS.equals("FALSE")) {
                    Toast.makeText(getActivity().getBaseContext(), "我的车队没有开启，请开启我的车队", Toast.LENGTH_SHORT).show();
                } else {
                   /* loadData();
                    str="STG"*/;
                        if (flag.equals("AA")){
                            loadData();
                            v.findViewById(R.id.carteam_map_open).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.carteam_map_close).setVisibility(View.GONE);
                            flag="BB";
                            //str="STR";
                      }else {
                            v.findViewById(R.id.carteam_map_close).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.carteam_map_open).setVisibility(View.GONE);
                            flag="AA";
                           // str="STR";
                         }
                }
            }
        });
    }

    /**
     * 获取车队成员信息
     */

    private void loadData() {
            String carId = sp.getStringValue(Const.CAR_TEAM_ID);
            String token = Uri.encode(sp.getStringValue(Const.AUTH));
            LoadWCMJsonTask task = new LoadWCMJsonTask(getActivity()) {
                @Override
                public void onDataReceived(String result, boolean isChanged) throws Exception {
                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("datas");
                    arrayList.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject datas = array.getJSONObject(i);
                        CarTeamListEntity entity = new CarTeamListEntity();
                        entity.setName(datas.getString("name"));
                        entity.setGeo(datas.getString("geo"));
                        arrayList.add(entity);
                    }
                    if (str=="STG"){
                        drawCarteam(arrayList);
                        str="STR";
                    }

                }
                @Override
                public void onError(Throwable t) {
                }
            };
        task.start(Constant.CARTEAM_URL + "&cartid=" + carId + "&token=" + token);
        str="STG";
    }

    /**
     * 线路选择的Dialog
     * @param latLng
     */
    private void showLinesDialog(final NaviLatLng latLng){
        if(mLinesDialog == null){

            mLinesDialog = new Dialog(getActivity(), R.style.popFromBottomdialog);
            View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_lines, null);
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
            //线路选择
            mRecommand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLinesDialog.dismiss();
                    calculateDriverRoute(AMapNavi.DrivingDefault,latLng);
                }
            });
            mAvoidCongestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLinesDialog.dismiss();
                    calculateDriverRoute(AMapNavi.DrivingAvoidCongestion,latLng);
                }
            });
            mNoExpressWays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLinesDialog.dismiss();
                    calculateDriverRoute(AMapNavi.DrivingNoExpressways,latLng);
                }
            });
            mSaveMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLinesDialog.dismiss();
                    calculateDriverRoute(AMapNavi.DrivingSaveMoney,latLng);
                }
            });
            mShortDistance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLinesDialog.dismiss();
                    calculateDriverRoute(AMapNavi.DrivingFastestTime,latLng);
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
     * 线路规划，地图划线
     * @param driveMode
     * @param latLng
     */
    private void calculateDriverRoute(int driveMode,NaviLatLng latLng){
        mProgressDialog.show();
        //起点终点列表
        ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
        ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();

        NaviLatLng mStartPoint= new NaviLatLng(iDriveApplication.app().getLocation().getLatitude(), iDriveApplication.app().getLocation().getLongitude());
        mStartPoints.clear();
        mStartPoints.add(mStartPoint);
        mEndPoints.clear();
        mEndPoints.add(latLng);
        AMapNavi.getInstance(getActivity()).calculateDriveRoute(mStartPoints,
                mEndPoints, null, driveMode);


    }
    private void displayDialog() {
        top_popup_shelter.setVisibility(View.VISIBLE);
        ll_map_longclick.setVisibility(View.VISIBLE);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        ll_map_longclick.startAnimation(animation);
    }

    /**
     * 消除Dialog
     */
    private void dismissDialog() {
        top_popup_shelter.setVisibility(View.INVISIBLE);
        ll_map_longclick.setVisibility(View.INVISIBLE);
        Animation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(500);
        ll_map_longclick.startAnimation(animation);
    }

    /**
     * 路况设置
     * @param isOpentraffic
     */
    public void setTraffic(boolean isOpentraffic) {
        this.isOpentraffic=isOpentraffic;
        if (isOpentraffic==true){
            trafficlayer_close.setVisibility(View.GONE);
            trafficlayer_open.setVisibility(View.VISIBLE);
        }else if(isOpentraffic==false){
            trafficlayer_close.setVisibility(View.VISIBLE);
            trafficlayer_open.setVisibility(View.GONE);
        }
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {

        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (arrayList!=null){
            for (int i = 0; i < arrayCar.size(); i++){
                arrayCar.get(i).hideInfoWindow();
                arrayCar.get(i).remove();
            }
            arrayCar.clear();
            arrayList.clear();
        }


        initBroadcastReceiver();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        //deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    /**
     * 此方法已经废弃
     */
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /**
     * 定位成功后回调函数
     */

    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        if (mListener != null && aLocation != null) {
            markerLoction.remove();
            mListener.onLocationChanged(aLocation);// 显示系统小蓝点
            marker.setPosition(new LatLng(aLocation.getLatitude(), aLocation
                    .getLongitude()));// 定位雷达小图标
            float bearing = aMap.getCameraPosition().bearing;
            aMap.setMyLocationRotateAngle(bearing);// 设置小蓝点旋转角度
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
            mAMapLocationManager.setGpsEnable(true);
            mAMapLocationManager.requestLocationUpdates(
                    LocationProviderProxy.AMapNetwork, 2000, 10, this);
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }

    /**
     * 附近搜索地理编码
     * @param result
     * @param rCode
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String address= result.getRegeocodeAddress().getFormatAddress()
                        + "附近";
                addressname.setText(address);
                SharedPreferences sp=getActivity().getSharedPreferences("mySp", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("adress",address);
                editor.commit();
                Log.e("adress",address);
            } else {
               Toast.makeText(getActivity(),"获取位置信息失败",Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(getActivity(), "获取位置信息失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

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
     * 划线
     */
    @Override
    public void onCalculateRouteSuccess() {
        mProgressDialog.dismiss();
        AMapNaviPath naviPath =  AMapNavi.getInstance(getActivity()).getNaviPath();
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
            Toast.makeText(getActivity(),"网络超时", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getActivity(),"路线规划失败，请重试", Toast.LENGTH_SHORT).show();
        Log.e("WLH", "info map CalculateRouteFailure errcode:" + i);
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

    /**
     * 车队成员-地图展示
     * @param array
     */
    private void drawCarteam(ArrayList<CarTeamListEntity> array) {
        for(int i=0; i<array.size(); i++) {
            CarTeamListEntity entity = array.get(i);
            String geo = entity.getGeo();
            String str[] = geo.split(",");
            if (str != null && str.length == 2) {
                LatLng latLng = new LatLng(Double.valueOf((str[1])), Double.valueOf(str[0]));
                MarkerOptions markerOption = new MarkerOptions().anchor(0.5f, 0.5f)
                        .position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_middle))
                        .perspective(true).draggable(true).period(50).title(entity.getName());
                markerCar = aMap.addMarker(markerOption);
                markerCar.setTitle(entity.getName());
                markerCar.setDraggable(true);
                arrayCar.add(markerCar);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
            }
        }

    }
}
