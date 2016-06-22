package com.ccpress.izijia.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.adapter.CarTeamAdapter;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.entity.CityEntity;
import com.ccpress.izijia.entity.MediaEntity;
import com.ccpress.izijia.fragment.MapFragment;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.ImageUtil;
import com.froyo.commonjar.utils.SpUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.trs.app.TRSFragmentActivity;
import com.ccpress.izijia.R;
import com.ccpress.izijia.fragment.LeftMenuFragment;
import com.ccpress.izijia.fragment.MainTabFragment;
import com.trs.types.Channel;

import java.util.ArrayList;

public class MainActivity extends TRSFragmentActivity {
    private SlidingMenu mMenu;
    //用于接收从主界面启动相机之后的Uri
    public Uri photoUri;
    //保存爱自驾和互动TagList用于侧边栏显示
    private ArrayList<Channel> mIdriveTagList;
    private ArrayList<Channel> mInteractTagList;
    private MainActivityBroadcastReceiver mReceiver = new MainActivityBroadcastReceiver();
    //保存当前城市实例
    public CityEntity current_location;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_main);
        initSlidingMenu();
        initBroadcastReceiver();

        current_location = new CityEntity();
        if(iDriveApplication.app().getLocation() != null){
            current_location.setCode(iDriveApplication.app().getLocation().getCityCode());
            String city = iDriveApplication.app().getLocation().getCity();
            current_location.setName(city.substring(0, city.length() - 1));
            current_location.setProvince(iDriveApplication.app().getLocation().getProvince());
        }
        CarTeamAdapter.CART_FLAG=true;
        MapFragment.CAR_STATUS="FALSE";
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void initSlidingMenu() {
        // 设置主界面视图
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new MainTabFragment()).commit();
        // 设置滑动菜单的属性值
        mMenu = new SlidingMenu(this);
        mMenu.setMode(SlidingMenu.LEFT);
        //暂时禁用
//        mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
//        mMenu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mMenu.setShadowDrawable(R.drawable.shadow_content_left);
        mMenu.setShadowWidthRes(R.dimen.shadow_width);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mMenu.setBehindOffset((int) (metrics.widthPixels * 0.26));
        mMenu.setFadeDegree(0.35f);
        mMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        // 设置滑动菜单的视图界面
        mMenu.setMenu(R.layout.frame_left_menu);
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new LeftMenuFragment()).commit();
//        mMenu.setSecondaryMenu(R.layout.frame_left_menu);
//        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new LeftMenuFragment()).commit();
    }

    private void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.TAG_CLICK_ACTION);
        filter.addAction(Constant.MAIN_TAB_CHANGE_ACTION);
        filter.addAction(Constant.CITY_CHANGE_ACTION);
        filter.addAction(Constant.GD_CITY_CHANGE_ACTION);
        registerReceiver(mReceiver, filter);
    }

    private class MainActivityBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Constant.TAG_CLICK_ACTION)){
                mMenu.toggle(true);
            }

            if(action.equals(Constant.MAIN_TAB_CHANGE_ACTION)){
                int index = intent.getIntExtra(Constant.MAIN_TAB_CHANGE_INDEX, 0);
                if(index != 0){
                    mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                } else {
                    //暂时禁用
//                    mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                    mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                }
            }

            if(action.equals(Constant.CITY_CHANGE_ACTION)){
            }
            if(action.equals(Constant.GD_CITY_CHANGE_ACTION)){
            }

        }
    }

    private long lastBackKeyDownTick = 0;
    public static final long MAX_DOUBLE_BACK_DURATION = 1500;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mMenu.isMenuShowing()){
                mMenu.toggle();
                return false;
            } else{
                long currentTick = System.currentTimeMillis();
                if(currentTick - lastBackKeyDownTick > MAX_DOUBLE_BACK_DURATION){
                    Toast.makeText(this, R.string.exit_app, Toast.LENGTH_SHORT).show();
                    lastBackKeyDownTick = currentTick;
                } else {
                    iDriveApplication.app().stopLocation();
                    iDriveApplication.app().destoryNavi();
                    finish();
//                    System.exit(0);
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case Constant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                    Intent intent = new Intent(MainActivity.this, PostEditActivity.class);
                    intent.putExtra(MediaPickerActivity.MEDIA_TYPE,
                            MediaPickerActivity.MEDIA_TYPE_IMG);
                    MediaEntity entity = ImageUtil.getMediaEntityFromMediaStore(this, photoUri);
                    ArrayList<MediaEntity> list = new ArrayList<MediaEntity>();
                    list.add(entity);
                    intent.putExtra(PostEditActivity.CHECKED_MEDIA_LIST, list);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        } else if (RESULT_CANCELED == resultCode) {
            switch (requestCode) {
                case Constant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                    MediaEntity entity = ImageUtil.getMediaEntityFromMediaStore(
                            MainActivity.this, photoUri);
                    if(entity != null){
                        int result = ImageUtil.deleteNewImageFromMediaStore(this,
                                entity.getPath());
                        if (result == 0) {
                            Log.e("MainActivity", "Delete fail");
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBroadcastReceiver();

    }

    public ArrayList<Channel> getIdriveTagList() {
        return mIdriveTagList;
    }

    public void setIdriveTagList(ArrayList<Channel> mIdriveTagList) {
        this.mIdriveTagList = mIdriveTagList;
    }

    public ArrayList<Channel> getInteractTagList() {
        return mInteractTagList;
    }

    public void setInteractTagList(ArrayList<Channel> mInteractTagList) {
        this.mInteractTagList = mInteractTagList;
    }
}
