package com.ccpress.izijia.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.TextView;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.activity.MainActivity;
import com.ccpress.izijia.util.GDLocationUtil;
import com.trs.fragment.AbsTRSFragment;
import com.trs.fragment.TabFragment;
import com.ccpress.izijia.R;
import com.trs.types.Channel;
import com.trs.util.log.Log;
import org.json.JSONArray;
import org.json.JSONException;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wu Jingyu
 * Date: 2015/5/5
 * Time: 16:32
 */
public class HomeTabFragment extends TabFragment {
    private HomeTabBroadcastReceiver mReceiver = new HomeTabBroadcastReceiver();
    public static  String TAB_CITY="city";
    private String title;
    private TextView txt_tag;
    private TextView txt_tag_count;
    private int current_channel_id;
    private ArrayList<String> channlelist = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        title = bundle.getString(AbsTRSFragment.EXTRA_TITLE);
        current_channel_id  = Integer.valueOf(bundle.getString(TabFragment.EXTRA_CATEGORY));

        View view = super.onCreateView(inflater, container, savedInstanceState);
        initView(view);
        initPagerListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initBroadcastReceiver();
        loadData();
        notifyChannelDataChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.TAG_CLICK_ACTION);
        filter.addAction(Constant.TOP_POPUP_LIST_CLICK_ACTION);
        filter.addAction(Constant.TOP_POPUP_INTERACT_LIST_CLICK_ACTION);
        filter.addAction(Constant.CITY_CHANGE_ACTION);
        getActivity().registerReceiver(mReceiver, filter);
    }

    private void initView(View v) {
        txt_tag = (TextView) v.findViewById(R.id.txt_tag);
        txt_tag_count = (TextView) v.findViewById(R.id.txt_tag_count);
        if (getChannelList().size() == 0) {
            return;
        }
        txt_tag.setText(getChannelList().get(0).getTitle());
        txt_tag_count.setText(String.valueOf(1) + "/" + String.valueOf(getChannelList().size()));
    }
    //设置Pager的监听
    private void initPagerListener() {
        getPager().setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (getChannelList().size() == 0) {
                    return;
                }

                txt_tag.setText(getChannelList().get(position).getTitle());
                Animation scaleAnimation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(300);
//                scaleAnimation.setInterpolator(new AnticipateOvershootInterpolator(2.0f));
                txt_tag.startAnimation(scaleAnimation);

                txt_tag_count.setText(String.valueOf(position + 1) + "/" + String.valueOf(getChannelList().size()));

                //发广播通知Tab切换
                Intent intent = new Intent();
                intent.setAction(Constant.TAB_CHANGE_ACTION);
                intent.putExtra(Constant.TAB_CHANGE_INDEX, position);
                getActivity().sendBroadcast(intent);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public int getViewID() {
        return R.layout.fragment_home_pager;
    }

    /**
     * 获取ChannelList数据
     * @param data
     * @return
     * @throws JSONException
     */
    @Override
    protected List<Channel> createChannelList(String data) throws JSONException {
        JSONArray array = new JSONArray(data);
        ArrayList<Channel> channelList = new ArrayList<Channel>();
        for (int i = 0; i < 1; i++) {//项目需要，只取第一个channel
            Channel c = new Channel(array.getJSONObject(i));
            if (title.equals(HomeFragment.TOP_CHANNEL_TITLE_IDRIVE)) {
                c.setType("3003");  //HomeiDriveListFragment
                c.setUrl(Constant.IDRIVE_URL_BASE
                        + Constant.CHANNELS_HOME_CONTENTS
                        + "?" + "channelType=" + String.valueOf(current_channel_id)
                        + "&" + "tag=" + String.valueOf(c.getId())
//                        + "&" + "city=" + ((MainActivity) getActivity()).current_location.getCode()
                        + "&" + "pageIndex=");
                channelList.add(c);
            }
            if (title.equals(HomeFragment.TOP_CHANNEL_TITLE_INTERACT)) {
                String url="";
                    url=PersonalCenterUtils.buildUrlMy(getActivity(),url);

                String city=TAB_CITY;

                c.setType("3004");  //HomeInteractListFragment
                c.setUrl(Constant.INTERACT_URL_BASE
                        + Constant.INTERACT_CHANNELS_CONTENTS
                        + "&" + "channelType=" + String.valueOf(current_channel_id)
                        + "&" + "tag=" + String.valueOf(c.getId())
                        + "&" + "city=" + city
                        + "&"+ url + "&pageIndex=");
                channelList.add(c);
            }

        }
        //保存Channel到MainActivity
        if (title.equals(HomeFragment.TOP_CHANNEL_TITLE_IDRIVE)) {
            ((MainActivity) getActivity()).setIdriveTagList(channelList);
        }
        if (title.equals(HomeFragment.TOP_CHANNEL_TITLE_INTERACT)) {
            ((MainActivity) getActivity()).setInteractTagList(channelList);
        }
        //发广播通知侧边栏
        Intent intent = new Intent();
        intent.setAction(Constant.INIT_LEFT_MENU_ACTION);
        getActivity().sendBroadcast(intent);
        return channelList;
    }

    /**
     * 广播接收者
     */
    private class HomeTabBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.TAG_CLICK_ACTION)) {
                int index = intent.getIntExtra(Constant.TAG_CLICK_INDEX, 0);
                getPager().setCurrentItem(index, true);
            }
            if(action.equals(Constant.TOP_POPUP_LIST_CLICK_ACTION)){
                current_channel_id = intent.getIntExtra(Constant.TOP_POPUP_LIST_CLICK_INDEX, 0);
                String url = getUrl();
                String n_url = url.substring(0, url.lastIndexOf("=") + 1);
                setUrl(n_url+ String.valueOf(current_channel_id));

                //加载Tag
               loadData();
                notifyChannelDataChanged();
            }
            if(action.equals(Constant.TOP_POPUP_INTERACT_LIST_CLICK_ACTION)){
                current_channel_id = intent.getIntExtra(Constant.TOP_POPUP_INTERACT_LIST_CLICK_INDEX, 0);
                String url = getUrl();
                String n_url = url.substring(0, url.lastIndexOf("=") + 1);
                setUrl(n_url+ String.valueOf(current_channel_id));
                Log.e("LOADDATEA::", n_url+ String.valueOf(current_channel_id));
                //加载Tag
                loadData();
                notifyChannelDataChanged();
            }
            if(action.equals(Constant.GD_CITY_CHANGE_ACTION)){
                notifyChannelDataChanged();
                initPagerListener();
            }
        }
    }

    /**
     * 刷新Tab
     * @param channelType
     * @param city
     */
    private void refreshCurrentTab(String channelType, String city) {
        for(Channel c:getChannelList()){
            String url = "";
            if(title.equals(HomeFragment.TOP_CHANNEL_TITLE_IDRIVE)){
                url = Constant.IDRIVE_URL_BASE
                        + Constant.CHANNELS_HOME_CONTENTS
                        + "?" + "channelType=" +
                        (channelType.equals("") ? String.valueOf(current_channel_id) : channelType)
                        + "&" + "tag=" + String.valueOf(c.getId())
//                        + "&" + "city=" + code
                        + "&" + "pageIndex=";
            } else {
                url = Constant.INTERACT_URL_BASE
                        + Constant.INTERACT_CHANNELS_CONTENTS
                        + "&" + "channelType=" + String.valueOf(current_channel_id)
                        + "&" + "tag=" + String.valueOf(c.getId())
                        + "&" + "city=" +city
                        + "&" + "pageIndex=";
            }
            Log.e( "refreshCurrentTab ", url);
            c.setUrl(url);

        }
        notifyChannelDataChanged();
    }
}
