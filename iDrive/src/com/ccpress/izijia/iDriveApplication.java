package com.ccpress.izijia;

import android.location.Location;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.navi.AMapNavi;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.util.LocationUtil;
import com.ccpress.izijia.util.TTSController;
import com.ccpress.izijia.yd.api.MyImageLoader;
import com.trs.app.TRSApplication;
import net.endlessstudio.util.Util;
import org.xutils.x;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Wu Jingyu
 * Date: 2015/3/5
 * Time: 17:45
 */
public class iDriveApplication extends TRSApplication implements AMapLocationListener {
    private static iDriveApplication sInstance;
    private LocationManagerProxy mLocationManagerProxy;
    private AMapLocation location = null;
    public static iDriveApplication application;
    public static RequestQueue mQueue;

    public static iDriveApplication app(){
        return sInstance;
    }

    public static RequestQueue getmQueue() {
        if(mQueue==null){
            mQueue= Volley.newRequestQueue(application);
        }
        return mQueue;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        sInstance = this;
        application=this;

        initCache("city");
        initCache("province");
        initCache("idrive_tags");
        initCache("interact_tags");
        initCache("idrive_channels");
        initCache("interact_channels");
        initCache("meishi_documents");
        initCache("comment_documents");
        initCache("interact_documents");
        initLocation();
        LocationUtil.clearCurrentLocation(this);
        initNavi();

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    private void initLocation() {
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);

        //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
        //在定位结束后，在合适的生命周期调用destroy()方法
        //其中如果间隔时间为-1，则定位只定一次
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 5000, 15, this);//每隔5s// 钟发起一次定位请求

        mLocationManagerProxy.setGpsEnable(true);
    }

    private void initNavi(){
        TTSController ttsManager = TTSController.getInstance(this);// 初始化语音模块
        ttsManager.init();
        AMapNavi.getInstance(this).setAMapNaviListener(ttsManager);// 设置语音模块播报
    }

    public void destoryNavi(){
        AMapNavi.getInstance(this).destroy();// 销毁导航
        TTSController.getInstance(this).stopSpeaking();
        TTSController.getInstance(this).destroy();
    }

    @Override
    public String getApplicationConfigUrl() {
        return null;
    }

    @Override
    public SourceType getSourceType() {
        return SourceType.JSON;
    }

    @Override
    public String getFirstClassUrl() {
        return "raw://first_class_menu";
//        return Constant.FIRST_CLASS_MENU_URL;
    }

    private void initCache(String fileName) {
        String cacheDir = this.getCacheDir().toString();
        try {
            InputStream is = Util.getStream(TRSApplication.app(), "raw://" + fileName);
            OutputStream os = new FileOutputStream(cacheDir + "/" + fileName + ".json");
            int byteCount = 0;
            byte[] bytes = new byte[1024];

            while ((byteCount = is.read(bytes)) != -1) {
                os.write(bytes, 0, byteCount);
            }
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AMapLocation getLocation(){
        return location;
    }

    public void stopLocation() {
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destory();
        }
        mLocationManagerProxy = null;
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0){
            location = aMapLocation;
            LocationUtil.setGpsLocation(getApplicationContext(), location);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
