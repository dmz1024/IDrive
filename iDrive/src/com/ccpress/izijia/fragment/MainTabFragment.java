package com.ccpress.izijia.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.ImagePickerActivity;
import com.ccpress.izijia.activity.MainActivity;
import com.ccpress.izijia.activity.RecordVideoActivity;
import com.ccpress.izijia.activity.portal.LoginActivity;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.CameraUtil;
import com.ccpress.izijia.util.ImageUtil;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.trs.app.FragmentFactory;
import com.trs.types.Channel;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import java.util.List;

/**
 * Created by Wu Jingyu
 * Date: 2015/3/9
 * Time: 17:37
 */
public class MainTabFragment extends Fragment implements View.OnClickListener{
    private static final int INIT_TAB_INDEX = 0;
    private Context mContext;

    private LinearLayout mBottomLayout;
    private LinearLayout mPhotoLayout;
    private RelativeLayout mShelterLayout;

    private List<Channel> mTabChannelList;
    private Channel mCurrentChannel;
    private FragmentManager mFragmentManager;
    private MainTabBroadcastReceiver mReceiver = new MainTabBroadcastReceiver();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_tab, container, false);
        mContext = getActivity();
        mFragmentManager = getChildFragmentManager();
        //初始化View相关实例
        initView(v);
        //初始化Channel List
        mTabChannelList = iDriveApplication.app().getFirstClassMenu().getChannelList();
        //初始化Tab
        createTab();
        initTab(INIT_TAB_INDEX);
        //注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.DISPLAY_PHOTO_LAYOUT_ACTION);
        getActivity().registerReceiver(mReceiver, intentFilter);
        return v;
    }

    private void initView(View v) {
        //获取底部Tab框架
        mBottomLayout = (LinearLayout) v.findViewById(R.id.bottomId);
        //获取底部弹出框架
        mPhotoLayout = (LinearLayout) v.findViewById(R.id.photo_linear_layout);
        //获取底部弹出框架的遮罩
        mShelterLayout = (RelativeLayout) v.findViewById(R.id.photo_linear_layout_shelter);
        mShelterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissPhotoLayout();
            }
        });
        //设置点击事件
        RelativeLayout btn_video = (RelativeLayout) v.findViewById(R.id.btn_video);
        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), InfoDetailActivity.class);
//                getActivity().startActivity(intent);
                if (CameraUtil.checkCameraHardware(getActivity())) {
                    Intent intent = new Intent(getActivity(), RecordVideoActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(),
                            getResources().getText(R.string.no_camera),
                            Toast.LENGTH_SHORT).show();
                }
                dismissPhotoLayout();
            }
        });
        RelativeLayout btn_photo = (RelativeLayout) v.findViewById(R.id.btn_photo);
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用系统相机拍照
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                MainActivity act = (MainActivity) getActivity();
                act.photoUri = ImageUtil.createNewImageToMediaStore(getActivity());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, act.photoUri);
                act.startActivityForResult(intent, Constant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                dismissPhotoLayout();
            }
        });
        RelativeLayout btn_upload = (RelativeLayout) v.findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chooseImage();
                dismissPhotoLayout();
            }
        });
        RelativeLayout btn_cancel = (RelativeLayout) v.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissPhotoLayout();
            }
        });
    }

    /**
     *批量选择图片
     */
    private void chooseImage(){
        int selectedMode = MultiImageSelectorActivity.MODE_MULTI;
        boolean showCamera = false;
        int maxNum = 9;
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
        // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxNum);
        // 选择模式
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectedMode);
        intent.putExtra(ImagePickerActivity.FROM_WHICH_ACTIVITY, MainActivity.class.toString());
        // 默认选择
//        if(mSelectPath != null && mSelectPath.size()>0){
//            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
//        }
        startActivity(intent);
    }

    /**
     * 底部tab
     */
    private void createTab() {
        int size = mTabChannelList.size();
        for (int i = 0; i < size; i++) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View btn = inflater.inflate(R.layout.btn_tab, null);
            btn.setClickable(true);
            btn.setTag(i);
            //设置底部tab名称
            TextView tx = (TextView) btn.findViewById(R.id.btn_text);
            tx.setText(mTabChannelList.get(i).getTitle());
            //设置按钮文字颜色
            ColorStateList csl = mContext.getResources().getColorStateList(R.color.btn_tab_text_color);
            if(csl != null){
                tx.setTextColor(csl);
            }
            //设置底部tab icon
            String name = getDrawableName(mTabChannelList.get(i).getPic());
            ApplicationInfo appInfo = mContext.getApplicationInfo();
            int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
            ImageView icon = (ImageView) btn.findViewById(R.id.btn_img);
            icon.setImageDrawable(getResources().getDrawable(resID));
            //设置点击事件
            btn.setOnClickListener(this);
            //加入底部BottomLayout中
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            mBottomLayout.addView(btn, params);
        }
    }

    /**
     * Tab数据
     * @param index
     */
    private void initTab(int index){
        setTabSelection(index);
        mCurrentChannel = mTabChannelList.get(index);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Fragment f = FragmentFactory.createFragment(mContext, mCurrentChannel);
        transaction.add(R.id.tab_content, f, mCurrentChannel.getType());
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        int index = (Integer) view.getTag();
        if(mTabChannelList.get(index).getType().equals("6001")
                && !PersonalCenterUtils.isLogin(getActivity())){
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }
        setTabSelection(index);
        switchContent(mTabChannelList.get(index));

        //发送MainTab Change广播
        Intent intent = new Intent();
        intent.setAction(Constant.MAIN_TAB_CHANGE_ACTION);
        intent.putExtra(Constant.MAIN_TAB_CHANGE_INDEX, index);
        getActivity().sendBroadcast(intent);
    }

    /**
     * Channel的目录
     * @param c
     */
    private void switchContent(Channel c) {
        if(!c.equals(mCurrentChannel)){
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            //transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.stay_for_a_while);
            Fragment from = findFragmentByChannel(mCurrentChannel);
            Fragment to = findFragmentByChannel(c);
            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(from).add(R.id.tab_content, to, c.getType()); // 隐藏当前的fragment，add下一个到Activity中
                transaction.commit();
            } else {
                transaction.hide(from).show(to); // 隐藏当前的fragment，显示下一个
                transaction.commit();
            }
//            transaction.replace(R.id.tab_content, to); // 隐藏当前的fragment，显示下一个
//            transaction.commit();
            mCurrentChannel = c;
        }
    }

    private Fragment findFragmentByChannel(Channel c) {
        Fragment f = mFragmentManager.findFragmentByTag(c.getType());
        if(f == null){
            f = FragmentFactory.createFragment(mContext, c);
        }
        return f;
    }

    private void setTabSelection(int tag) {
        clearSelectedState();
        View childView = mBottomLayout.findViewWithTag(tag);
        childView.setSelected(true);
    }

    private void clearSelectedState() {
        for (int i = 0; i < mBottomLayout.getChildCount(); i++) {
            View childView = mBottomLayout.getChildAt(i);
            childView.setSelected(false);
        }
    }

    private void displayPhotoLayout() {
        Animation animationPhotoLayout = new TranslateAnimation(0.0f, 0.0f, 500.0f, 0.0f);
        animationPhotoLayout.setDuration(300);
        mPhotoLayout.setVisibility(View.VISIBLE);
        mPhotoLayout.startAnimation(animationPhotoLayout);

        Animation animationShelter = new AlphaAnimation(0.1f, 1.0f);
        animationShelter.setDuration(200);
        mShelterLayout.setVisibility(View.VISIBLE);
        mShelterLayout.startAnimation(animationShelter);
    }

    /**
         *搁置PhotoLayout
     */
    private void dismissPhotoLayout() {
        Animation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 500.0f);
        animation.setDuration(200);
        mPhotoLayout.setVisibility(View.INVISIBLE);
        mPhotoLayout.startAnimation(animation);

        Animation animationShelter = new AlphaAnimation(1.0f, 0.1f);
        animationShelter.setDuration(200);
        mShelterLayout.setVisibility(View.INVISIBLE);
        mShelterLayout.startAnimation(animationShelter);
    }

    private String getDrawableName(String s) {
        return s.substring(s.lastIndexOf("/") + 1);
    }

    private class MainTabBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Constant.DISPLAY_PHOTO_LAYOUT_ACTION)){
                displayPhotoLayout();
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mCurrentChannel.getType().equals("6001")
                && !PersonalCenterUtils.isLogin(getActivity())){
            setTabSelection(0);
            switchContent(mTabChannelList.get(0));

            //发送MainTab Change广播
            Intent i = new Intent();
            i.setAction(Constant.MAIN_TAB_CHANGE_ACTION);
            i.putExtra(Constant.MAIN_TAB_CHANGE_INDEX, 0);
            getActivity().sendBroadcast(i);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

}
