package com.ccpress.izijia.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.GDLocationListActivity;
import com.ccpress.izijia.activity.LocationListActivity;
import com.ccpress.izijia.util.LocationUtil;
import com.trs.app.FragmentFactory;
import com.trs.types.Channel;
import com.trs.util.log.Log;
/**
 * Created by Wu Jingyu
 * Date: 2015/3/24
 * Time: 15:04
 * 我去的Fragment
 */
public class GoFragment extends Fragment {
    private RadioButton mRdLines;
    private RadioButton mRdDes;
    private ImageView mLocation;
    private TextView mTxtLocation;
    private CityChangedBroadcastReceiver receiver = new CityChangedBroadcastReceiver();
    private Fragment fragmentLines;
    private Fragment fragmentDes;

    private boolean isLines = true;
    private   boolean isCityChanged = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initBroadcastReceiver();

        View view = inflater.inflate(R.layout.fragment_go, container, false);
        mRdLines = (RadioButton) view.findViewById(R.id.rd_lines);
        mRdDes = (RadioButton) view.findViewById(R.id.rd_destination);
        mLocation = (ImageView) view.findViewById(R.id.btn_location);
        mTxtLocation = (TextView) view.findViewById(R.id.txt_location);

        //根据高德定位，初始化城市和省份
        String province="";
        String code = "";
        String provinceCode="";
        LocationUtil.clearCurrentLocation(getActivity().getApplicationContext());
            province = LocationUtil.getProvince(getActivity().getApplicationContext());
            provinceCode = LocationUtil.getCurrentSetProvinceCode(getActivity().getApplicationContext());
            if(provinceCode.equals("")){
                code = LocationUtil.getCityCode(getActivity().getApplicationContext());
            } else {
                code = provinceCode;
            }


        Channel channel = new Channel();
        channel.setUrl(Constant.IDRIVE_URL_BASE +
                String.format(Constant.LineList_Url,
                        Constant.CType_Line,
                        code));
        channel.setType("3005");
        fragmentLines = FragmentFactory.createFragment(getActivity(), channel);
        getFragmentManager().beginTransaction().replace(R.id.content, fragmentLines).commit();
        mTxtLocation.setText(province);
        //城市地位点击事件
        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),
                        LocationListActivity.class);
                intent.putExtra(LocationListActivity.IS_PROVINCE_LIST, true);
                if(isLines) {
                    intent.putExtra(LocationListActivity.IS_NEED_CHOOSE_CITY, false);

                } else {
                    intent.putExtra(LocationListActivity.IS_NEED_CHOOSE_CITY, true);

                }

                startActivity(intent);
            }
        });
        //线路Table点击事件
        mRdLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mTxtLocation.setText(LocationUtil.getProvince(getActivity().getApplicationContext()));
                    isLines = true;
                    if(fragmentLines != null){
                        if (isCityChanged){
                            isCityChanged = false;
                            fragmentLines.getArguments().remove(((HomeiDriveListFragment)fragmentDes).EXTRA_URL);
                            fragmentLines.getArguments().putString(((HomeiDriveListFragment) fragmentDes).EXTRA_URL,
                                    Constant.IDRIVE_URL_BASE +
                                    String.format(Constant.LineList_Url,
                                            Constant.CType_Line,
                                            LocationUtil.getCityCode(getActivity().getApplicationContext())));
                        }

                        getFragmentManager().beginTransaction().replace(R.id.content, fragmentLines).commit();
                    }
                }
            }
        });
        //目的地Table点击事件
        mRdDes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    isLines = false;
                    if(fragmentDes == null){
                        Channel channel = new Channel();
                        channel.setType("3005");
                        channel.setUrl(Constant.IDRIVE_URL_BASE +
                                String.format(Constant.LineList_Url,
                                        Constant.CType_Des,
                                        LocationUtil.getCityCode(getActivity().getApplicationContext())));
                        fragmentDes = FragmentFactory.createFragment(getActivity(), channel);
                    }
                    mTxtLocation.setText(LocationUtil.getCity(getActivity().getApplicationContext()));

                    if (isCityChanged){
                        fragmentDes.getArguments().remove(((HomeiDriveListFragment)fragmentDes).EXTRA_URL);
                        fragmentDes.getArguments().putString(((HomeiDriveListFragment) fragmentDes).EXTRA_URL,
                                Constant.IDRIVE_URL_BASE +
                                String.format(Constant.LineList_Url,
                                        Constant.CType_Des,
                                        LocationUtil.getCityCode(getActivity().getApplicationContext())));
                        isCityChanged = false;
                    }
                    getFragmentManager().beginTransaction().replace(R.id.content, fragmentDes).commit();
                }
            }
        });

        return view;
    }

    private void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.CITY_CHANGE_ACTION);
        getActivity().registerReceiver(receiver, filter);
    }

    /**
     * 城市更换的监听
     */
    private class CityChangedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Constant.CITY_CHANGE_ACTION)){
                isCityChanged = true;
                if (isLines) {
                    mTxtLocation.setText(LocationUtil.getProvince(getActivity().getApplicationContext()));
                    fragmentLines.getArguments().remove(((HomeiDriveListFragment) fragmentLines).EXTRA_URL);
                    fragmentLines.getArguments().putString(((HomeiDriveListFragment) fragmentDes).EXTRA_URL,
                            Constant.IDRIVE_URL_BASE +
                            String.format(Constant.LineList_Url,
                                    Constant.CType_Line,
                                    LocationUtil.getProvinceCode(getActivity().getApplicationContext())));
                    ((HomeiDriveListFragment)fragmentLines).setUrl(Constant.IDRIVE_URL_BASE +
                            String.format(Constant.LineList_Url,
                                    Constant.CType_Line,
                                    LocationUtil.getProvinceCode(getActivity().getApplicationContext())));
                    ((HomeiDriveListFragment)fragmentLines).showLoading();
                    ((HomeiDriveListFragment)fragmentLines).loadData(true);
                }else {
                    mTxtLocation.setText(LocationUtil.getCity(getActivity().getApplicationContext()));
                    fragmentDes.getArguments().remove(((HomeiDriveListFragment) fragmentDes).EXTRA_URL);
                    fragmentDes.getArguments().putString(((HomeiDriveListFragment) fragmentDes).EXTRA_URL,
                            Constant.IDRIVE_URL_BASE +
                            String.format(Constant.LineList_Url,
                                    Constant.CType_Des,
                                    LocationUtil.getCityCode(getActivity().getApplicationContext())));
                    ((HomeiDriveListFragment)fragmentDes).setUrl(Constant.IDRIVE_URL_BASE +
                            String.format(Constant.LineList_Url,
                                    Constant.CType_Des,
                                    LocationUtil.getCityCode(getActivity().getApplicationContext())));
                    ((HomeiDriveListFragment)fragmentDes).showLoading();
                    ((HomeiDriveListFragment)fragmentDes).loadData(true);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

}
