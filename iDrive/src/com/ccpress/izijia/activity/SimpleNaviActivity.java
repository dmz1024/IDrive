package com.ccpress.izijia.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.ccpress.izijia.R;
import com.ccpress.izijia.util.TTSController;

/**
 * Created by WLH on 2015/5/14 15:33.
 * 导航界面
 */
public class SimpleNaviActivity extends Activity implements AMapNaviViewListener {
    //导航View

    private AMapNaviView mAmapAMapNaviView;
    private AMapNaviListener mAmapNaviListener;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simplenavi);
        TTSController.getInstance(this).startSpeaking();
        // 实时导航方式进行导航
        AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);
        init(savedInstanceState);
    }

    /**
     * 初始化
     *
     * @param savedInstanceState
     */
    private void init(Bundle savedInstanceState) {
        mAmapAMapNaviView = (AMapNaviView) findViewById(R.id.simplenavimap);
        mAmapAMapNaviView.onCreate(savedInstanceState);
        mAmapAMapNaviView.setAMapNaviViewListener(this);
        //setAmapNaviViewOptions();
    }

    /**
     * 导航监听
     * @return
     */
  private AMapNaviListener getAMapNaviListener(  ){
        if (mAmapNaviListener == null) {
                         mAmapNaviListener = new AMapNaviListener() {
                             @Override
                                 public void onTrafficStatusUpdate() {
                                         // TODO Auto-generated method stub
                                     }
                                         @Override
                                 public void onStartNavi(int arg0) {
                                         // TODO Auto-generated method stub
                                     }
                                @Override
                                 public void onReCalculateRouteForYaw() {
                                         // 可以在频繁重算时进行设置,例如五次之后
                                         // i++;
                                         // if (i >= 5) {
                                         // AMapNaviViewOptions viewOptions = new
                                         // AMapNaviViewOptions();
                                         // viewOptions.setReCalculateRouteForYaw(false);
                                         // mAmapAMapNaviView.setViewOptions(viewOptions);
                                         // }
                                     }
                             @Override
                             public void onReCalculateRouteForTrafficJam() {

                                     }

                             @Override
                                 public void onLocationChange(AMapNaviLocation location) {
                                     }

                             @Override
                                 public void onInitNaviSuccess() {
                                         // TODO Auto-generated method stub
                                     }

                             @Override
                                 public void onInitNaviFailure() {
                                         // TODO Auto-generated method stub
                                     }

                             @Override
                                 public void onGetNavigationText(int arg0, String arg1) {
                                         // TODO Auto-generated method stub
                                     }

                             @Override
                                 public void onEndEmulatorNavi() {
                                         // TODO Auto-generated method stub
                                     }
                                         @Override
                                         public void onCalculateRouteSuccess() {
                                                }
                                         @Override
                                         public void onCalculateRouteFailure(int arg0) {

                                                  }
                                         @Override
                                         public void onArrivedWayPoint(int arg0) {
                                         // TODO Auto-generated method stub
                                              }
                                         @Override
                                         public void onArriveDestination() {
                                         // TODO Auto-generated method stub
                                            finish();
                                     }
                                         @Override
                                         public void onGpsOpenStatus(boolean arg0) {
                                         // TODO Auto-generated method stub

                                     }

                                        @Override
                                        public void onNaviInfoUpdated(AMapNaviInfo arg0) {
                                        // TODO Auto-generated method stub

                                     }
                                   @Override
                                   public void onNaviInfoUpdate(NaviInfo arg0) {
                                        // TODO Auto-generated method stub
                                    }
                             };
                     }
                 return mAmapNaviListener;
    }
//-----------------------------导航界面回调事件------------------------

    /**
     * 导航界面返回按钮监听
     */
    @Override
    public void onNaviCancel() {
        Intent intent=new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
       finish();
    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviMapMode(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNaviTurnClick() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNextRoadClick() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScanViewButtonClick() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * 返回键监听事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent=new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // ------------------------------生命周期方法---------------------------
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAmapAMapNaviView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
       AMapNavi.getInstance(this).setAMapNaviListener(getAMapNaviListener());
        mAmapAMapNaviView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mAmapAMapNaviView.onPause();
        AMapNavi.getInstance(this).removeAMapNaviListener(getAMapNaviListener());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAmapAMapNaviView.onDestroy();
        TTSController.getInstance(this).stopSpeaking();

    }

    @Override
    public void onLockMap(boolean arg0) {

        // TODO Auto-generated method stub

    }
}
