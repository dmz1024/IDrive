package com.ccpress.izijia.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.*;

import com.ccpress.izijia.activity.*;
import com.ccpress.izijia.activity.line.LinePreviewActivity;
import com.ccpress.izijia.activity.portal.LoginActivity;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.dfy.fragment.IDriveFragment;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.LocationUtil;
import com.froyo.commonjar.utils.SpUtil;
import com.trs.app.FragmentFactory;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.entity.TopPopupListEntity;
import com.trs.types.Channel;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;
import com.trs.wcm.LoadWCMJsonTask;
import com.trs.widget.WebView;
import org.json.JSONArray;
import org.json.JSONObject;
import com.ccpress.izijia.utils.PersonalCenterUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Wu Jingyu
 * Date: 2015/3/6
 * Time: 17:33
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    public static final String TOP_CHANNEL_TITLE_IDRIVE = "idrive";
    public static final String TOP_CHANNEL_TITLE_INTERACT = "interact";
    public static String CHANGE_CITY = "";
    public static String CITY = "city";
    private RelativeLayout rd_idrive;
    private TextView txt_idrive;
    private RelativeLayout rd_hd;
    public static int currentCheckId = R.id.rd_idrive;
    private ListView popupListView;
    private RelativeLayout top_popup_shelter;
    private Button edit_group;
    private TopPopupListAdapter popupListViewAdapter;
    private ArrayList<TopPopupListEntity> popupList = new ArrayList<TopPopupListEntity>();
    private int currentPopupListIndex = 0;
    private int idrive_PopupListIndex = 0;
    private int rd_PopupListIndex = 0;
    private HashMap<String, Channel> topChannelMap = new HashMap<String, Channel>();
    private RelativeLayout loading_view;

    private boolean isShow = false;

    private TextView txt_location;
    private ArrayList<TopPopupListEntity> iDrivePopupList = new ArrayList<TopPopupListEntity>();
    private ArrayList<TopPopupListEntity> interactPopupList = new ArrayList<TopPopupListEntity>();
    private FrameLayout frameLayout;
    private ImageView mPhotoBtn;
    private ImageView btn_location;
    private boolean dfy_isShow = false;

    //    private HomFragmentBroadcastReceiver mReceiver = new HomFragmentBroadcastReceiver();
    private IDriveFragment mIdriveFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        initView(v);
        initPopupList(v);
        return v;


    }

    @Override
    public void onResume() {
        super.onResume();
//        initBroadcastReceiver();
        txt_location.setText(HomeFragment.CITY);
        popupList.clear();
        loadInteractPopupListData();
        popupList.addAll(interactPopupList);
        popupListView.setAdapter(popupListViewAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    /**
     * 设置布局数据
     *
     * @param v
     */
    private void initView(View v) {

        //初始化顶部RadioBtn

        frameLayout = (FrameLayout) v.findViewById(R.id.pager_dfy);
        rd_idrive = (RelativeLayout) v.findViewById(R.id.rd_idrive);
        txt_idrive = (TextView) v.findViewById(R.id.txt_idrive);
        rd_idrive.setBackgroundResource(R.drawable.rdbtn_idrive);
        rd_hd = (RelativeLayout) v.findViewById(R.id.rd_hd);
        rd_hd.setBackgroundResource(R.drawable.rdbtn_hd);
        rd_idrive.setOnClickListener(new RadioBtnClickListener());
        rd_hd.setOnClickListener(new RadioBtnClickListener());

        rd_idrive.setSelected(true);

        ColorStateList csl = getActivity().getResources().getColorStateList(R.color.rdbtn_txt_color);
        if (csl != null) {
            ((TextView) v.findViewById(R.id.txt_idrive)).setTextColor(csl);
            ((TextView) v.findViewById(R.id.txt_hd)).setTextColor(csl);
        }

        //初始化拍照按钮
        mPhotoBtn = (ImageView) v.findViewById(R.id.btn_take_photo);
        mPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Constant.DISPLAY_PHOTO_LAYOUT_ACTION);
                getActivity().sendBroadcast(intent);
            }
        });


        //初始化Loading View
        loading_view = (RelativeLayout) v.findViewById(R.id.loading_view);

        //初始化Location txt
        txt_location = (TextView) v.findViewById(R.id.txt_location);
        //根据高德定位，初始化城市和省份
        String city = "";
        if (iDriveApplication.app().getLocation() != null) {
            city = iDriveApplication.app().getLocation().getCity();

        } else {
            LocationUtil.clearCurrentLocation(getActivity().getApplicationContext());
            city = LocationUtil.getCity(getActivity().getApplicationContext());
        }
        CITY = city;
        HomeTabFragment.TAB_CITY = city;
        PostEditActivity.POSTCITY = city;
        LinePreviewActivity.PRECITY = city;
        HelpActivity.HPCITY = city;
        txt_location.setText(city);

        //初始化Location Btn
        btn_location = (ImageView) v.findViewById(R.id.btn_location);
        //城市点击
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), GDLocationListActivity.class);
                intent.putExtra(GDLocationListActivity.GD_IS_PROVINCE_LIST, true);
                intent.putExtra(GDLocationListActivity.GD_IS_NEED_CHOOSE_CITY, true);
                intent.putExtra(GDLocationListActivity.NEARBY_NEEDED, currentCheckId == R.id.rd_hd);
                startActivity(intent);

            }
        });
    }

    /**
     * PopupList的数据设置
     *
     * @param v
     */
    private void initPopupList(View v) {
        top_popup_shelter = (RelativeLayout) v.findViewById(R.id.top_popup_shelter);
        top_popup_shelter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissTopPopupList();
            }
        });

        popupListView = (ListView) v.findViewById(R.id.top_popup_list);
        popupListViewAdapter = new TopPopupListAdapter();
        popupListView.setAdapter(popupListViewAdapter);
        popupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dismissTopPopupList();
                //特殊处理精选频道
                if (popupList.get(i).getName().equals("精选")) {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    WebViewActivity.TITLE_NAME = "手机";
                    WebViewActivity.image = null;
                    WebViewActivity.FLAG = "精选";
                    intent.putExtra("url", popupList.get(i).getUrl());
                    startActivity(intent);
                    return;
                }

                if (currentPopupListIndex != i) {
                    //更新当前选中index，并刷新列表
                    currentPopupListIndex = i;
                    if (currentCheckId == R.id.rd_idrive) {
                        String name = popupList.get(i).getName();
                        if (name.equals("全部")) {
                            txt_idrive.setText("爱自驾");
                            frameLayout.setVisibility(View.GONE);
                            dfy_isShow = false;
                        } else if (name.equals("自驾团")) {
                            txt_idrive.setText("自驾团");
                            frameLayout.setVisibility(View.VISIBLE);
                            if (mIdriveFragment == null) {
                                mIdriveFragment = new IDriveFragment();
                            }
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.pager_dfy, mIdriveFragment).show(mIdriveFragment).commit();
                            dfy_isShow = true;

                        } else {
                            txt_idrive.setText(name);
                            frameLayout.setVisibility(View.GONE);
                            dfy_isShow = false;
                        }
                        idrive_PopupListIndex=currentPopupListIndex;

                        popupListViewAdapter.notifyDataSetChanged();
                        //联网获取数据
                        Intent intent = new Intent();
                        intent.setAction(Constant.TOP_POPUP_LIST_CLICK_ACTION);
                        intent.putExtra(Constant.TOP_POPUP_LIST_CLICK_INDEX, popupList.get(i).getId());
                        LeftMenuFragment.RD_IDRIVE = popupList.get(i).getId();
                        getActivity().sendBroadcast(intent);

                    } else {
                        //联网获取数据
                        rd_PopupListIndex = i;
                        Intent intent = new Intent();
                        intent.setAction(Constant.TOP_POPUP_INTERACT_LIST_CLICK_ACTION);
                        intent.putExtra(Constant.TOP_POPUP_INTERACT_LIST_CLICK_INDEX, popupList.get(i).getId());
                        LeftMenuFragment.RD_INTERACT = popupList.get(i).getId();
                        getActivity().sendBroadcast(intent);
                    }

                }
            }
        });
        loadIDrivePopupListData();
        loadInteractPopupListData();
    }


    /**
     * 创建TopPopupListAdapter
     */
    private class TopPopupListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return popupList.size();
        }

        @Override
        public TopPopupListEntity getItem(int i) {
            return popupList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return popupList.get(i).getId();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_top_popup, parent, false);
            }

            TextView txt_name = (TextView) convertView.findViewById(R.id.channel_name);
            txt_name.setText(popupList.get(position).getName());
            RelativeLayout rl_list_top_popup = (RelativeLayout) convertView.findViewById(R.id.rl_list_top_popup);
            edit_group = (Button) convertView.findViewById(R.id.edit_group);
            edit_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SpUtil sp = new SpUtil(getActivity());
                    String uid = sp.getStringValue(Const.UID);

                    dismissTopPopupList();
                    if (StringUtil.isEmpty(uid)) {//如果未登录，跳转到登录页面
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.putExtra(LoginActivity.EXTRA_NOT_GOTO_HOMEPAGE, true);
                        startActivityForResult(intent, 0);
                        return;
                    } else {
                        //popupList.clear();
                        Intent i = new Intent(getActivity(), EditGroupActivity.class);
                        getActivity().startActivity(i);
                    }
                }
            });

            if (currentPopupListIndex == position) {
                rl_list_top_popup.setBackgroundResource(R.color.idrive_black);
                txt_name.setTextColor(getResources().getColor(R.color.popup_list_selected_font));
                txt_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                txt_name.getPaint().setFakeBoldText(true);

            } else {
                rl_list_top_popup.setBackgroundDrawable(null);
                txt_name.setTextColor(getResources().getColor(R.color.idrive_white));
                txt_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                txt_name.getPaint().setFakeBoldText(false);
            }

            View separate_line = convertView.findViewById(R.id.separate_line);
            if (position == getCount() - 1) {
                separate_line.setVisibility(View.INVISIBLE);
                if (currentCheckId == R.id.rd_hd) {
                    edit_group.setVisibility(View.VISIBLE);
                } else {
                    edit_group.setVisibility(View.GONE);
                }
            } else {
                separate_line.setVisibility(View.VISIBLE);
                edit_group.setVisibility(View.GONE);
            }
            return convertView;
        }
    }


    /**
     * 弹出TopPopupList动画设置
     */
    private void displayTopPopupList() {
        top_popup_shelter.setVisibility(View.VISIBLE);
        popupListView.setVisibility(View.VISIBLE);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        popupListView.startAnimation(animation);
    }

    /**
     * 关闭TopPopupList的动画设置
     */
    private void dismissTopPopupList() {
        top_popup_shelter.setVisibility(View.INVISIBLE);
        popupListView.setVisibility(View.INVISIBLE);

        Animation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(500);
        popupListView.startAnimation(animation);
    }

    /**
     * RadioBtn的点击监听事件
     */
    private class RadioBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.rd_idrive) {
                currentPopupListIndex = idrive_PopupListIndex;
            } else {
                currentPopupListIndex = rd_PopupListIndex;
            }

            popupList.clear();
            if (id == currentCheckId) {
                if (currentCheckId == R.id.rd_idrive) {
                    popupList.addAll(iDrivePopupList);
                } else {
//                    loadInteractPopupListData();
                    popupList.addAll(interactPopupList);
//                    popupListView.setAdapter(popupListViewAdapter);
                }
                if (popupList.size() == 0) {
                    Toast.makeText(getActivity(),
                            getResources().getText(R.string.channels_download_fail),
                            Toast.LENGTH_SHORT).show();

                } else {
                    popupListViewAdapter.notifyDataSetChanged();
                    displayTopPopupList();
                }
            } else {
                if (view.getId() == R.id.rd_hd) {
                    txt_location.setVisibility(View.VISIBLE);
                    btn_location.setVisibility(View.VISIBLE);

                } else {
                    txt_location.setVisibility(View.INVISIBLE);
                    btn_location.setVisibility(View.INVISIBLE);
                }
                popupListViewAdapter.notifyDataSetChanged();
                changeBackground(id);
                currentPopupListIndex = 0;
                switchViewPager(currentCheckId, id);
                currentCheckId = id;
                //发广播通知侧边栏
                Intent intent = new Intent();
                intent.setAction(Constant.RADIO_CHECK_ACTION);
                intent.putExtra(Constant.RADIO_CHECK_ID, currentCheckId);
                getActivity().sendBroadcast(intent);
            }
        }
    }

    /**
     * 改变Tab的fragment
     *
     * @param id
     */
    private void changeBackground(int id) {
        if (id == R.id.rd_idrive) {
            rd_idrive.setSelected(true);
            rd_hd.setSelected(false);
            if (dfy_isShow) {
                frameLayout.setVisibility(View.VISIBLE);
            }
        }
        if (id == R.id.rd_hd) {
            rd_idrive.setSelected(false);
            rd_hd.setSelected(true);
            frameLayout.setVisibility(View.GONE);

        }
    }

    /**
     * Pager的数据设置
     */
    private void initViewPager() {
        Channel channel = getTopChannel(R.id.rd_idrive, currentPopupListIndex);
        Fragment fragment = FragmentFactory.createFragment(getActivity(), channel);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.pager_content, fragment, channel.getTitle());
        transaction.commit();
    }

    /**
     * 获取top的频道信息
     *
     * @param id
     * @param currentIndex
     * @return
     */
    private Channel getTopChannel(int id, int currentIndex) {
        String key = String.valueOf(id) + "-" + String.valueOf(currentIndex);
        if (topChannelMap.containsKey(key)) {
            return topChannelMap.get(key);
        } else {
            Channel channel = new Channel();
            channel.setType("3002");  //爱自驾和互动都使用HomeTabFragment
            switch (id) {
                case R.id.rd_idrive:
                    channel.setTitle(TOP_CHANNEL_TITLE_IDRIVE);
                    channel.setUrl(Constant.IDRIVE_URL_BASE
                            + Constant.IDRIVE_TAGS_URL
                            + "?cid=" + String.valueOf(iDrivePopupList.get(currentIndex).getId()));
                    break;
                case R.id.rd_hd:
                    channel.setTitle(TOP_CHANNEL_TITLE_INTERACT);
                    channel.setUrl(Constant.INTERACT_URL_BASE
                            + Constant.INTERACT_TAGS_URL
                            + "&cid=" + String.valueOf(interactPopupList.get(currentIndex).getId()));
                    break;
                default:
                    break;
            }
            topChannelMap.put(key, channel);
            String s = String.valueOf(topChannelMap.size());
            return channel;
        }
    }

    private void switchViewPager(int fromId, int toId) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment from = findFragmentByChannel(getTopChannel(fromId, currentPopupListIndex));
        Fragment to = findFragmentByChannel(getTopChannel(toId, currentPopupListIndex));
        if (!to.isAdded()) { // 先判断是否被add过
            transaction.hide(from).add(R.id.pager_content, to, getTopChannel(toId, currentPopupListIndex).getTitle()); // 隐藏当前的fragment，add下一个到Activity中
            transaction.commit();
        } else {
            transaction.hide(from).show(to); // 隐藏当前的fragment，显示下一个
            transaction.commit();
        }

    }

    private Fragment findFragmentByChannel(Channel c) {
        Fragment f = getFragmentManager().findFragmentByTag(c.getTitle());
        if (f == null) {
            f = FragmentFactory.createFragment(getActivity(), c);
        }
        return f;
    }


    /**
     * 加载  首页-爱自驾的tab—top的List的数据
     */
    private void loadIDrivePopupListData() {
        SpUtil sp = new SpUtil(getActivity());
        //创建Task获取channel列表
        loading_view.setVisibility(View.VISIBLE);
        LoadWCMJsonTask task = new LoadWCMJsonTask(getActivity()) {
            @Override
            public void onDataReceived(String result, boolean isCache) throws Exception {
                if (getActivity() == null) {
                    return;
                }

                loading_view.setVisibility(View.INVISIBLE);
                iDrivePopupList.clear();
                JSONObject obj = new JSONObject(result);
                if (obj.has("code") && obj.getString("code").equals("0")) {
                    JSONArray array = obj.getJSONArray("datas");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        TopPopupListEntity entity = new TopPopupListEntity();
                        entity.setId(object.getInt("cid"));
                        entity.setName(object.getString("name"));
                        entity.setUrl(object.getString("url"));

                        iDrivePopupList.add(entity);
                    }
                    initViewPager();
                    loadInteractPopupListData();
                } else {
                    if (obj.has("message")) {
                        Log.v(HomeFragment.this.TAG, obj.getString("message"));
                    }
                    Toast.makeText(getActivity(),
                            getResources().getText(R.string.channels_download_fail),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable t) {
                if (getActivity() == null) {
                    return;
                }

                loading_view.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(),
                        getResources().getText(R.string.channels_download_fail),
                        Toast.LENGTH_SHORT).show();
                return;
            }
        };
        task.start(Constant.IDRIVE_URL_BASE + Constant.CHANNELS_URL);
    }

    /**
     * 加载  首页-互动的tab—top的List的数据
     */
    private void loadInteractPopupListData() {
        LoadWCMJsonTask task = new LoadWCMJsonTask(getActivity()) {
            @Override
            public void onDataReceived(String result, boolean isChanged) throws Exception {
                if (getActivity() == null) {
                    return;
                }
                loading_view.setVisibility(View.INVISIBLE);
                interactPopupList.clear();

                JSONObject obj = new JSONObject(result);
                if (obj.has("code") && obj.getString("code").equals("0")) {
                    JSONArray array = obj.getJSONArray("datas");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        TopPopupListEntity entity = new TopPopupListEntity();
                        entity.setId(object.getInt("cid"));
                        entity.setName(object.getString("name"));
                        interactPopupList.add(entity);
                    }
                    //popupListViewAdapter.notifyDataSetChanged();
                } else {
                    if (obj.has("message")) {
                        Log.v(TAG, obj.getString("message"));
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                if (getActivity() == null) {
                    return;
                }

                Toast.makeText(getActivity(),
                        getResources().getText(R.string.channels_download_fail),
                        Toast.LENGTH_SHORT).show();
                return;
            }
        };
        task.start(PersonalCenterUtils.buildUrlMy(getActivity(),
                (Constant.INTERACT_URL_BASE + Constant.INTERACT_CHANNELS_URL)));
    }


}
