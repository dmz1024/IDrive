package com.ccpress.izijia.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.activity.MainActivity;
import com.ccpress.izijia.R;
import com.trs.types.Channel;
import com.trs.util.log.Log;
import java.util.ArrayList;

/**
 * Created by Wu Jingyu
 * Date: 2015/3/6
 * Time: 17:51
 */
public class LeftMenuFragment extends Fragment {
    public static int currentCheckId = R.id.rd_idrive; //表明当前选中的是爱自驾还是互动
    private LeftMenuBroadcastReceiver mReceiver = new LeftMenuBroadcastReceiver();
    private ArrayList<Channel> mTagList = new ArrayList<Channel>();
    private LinearLayout tag_list_layout;
    private int cur_tag_index_idrive = 0;
    private int cur_tag_index_interact = 0;
    public static int RD_IDRIVE=0;
    public static int RD_INTERACT=0;
    public static  String URL="url";
    private View.OnClickListener mTagClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.e("mTagClickListener", "/////////////////////////////////// ");
            //发送广播通知Tag点击
            int index = (Integer) view.getTag();
            Intent intent = new Intent();
            intent.setAction(Constant.TAG_CLICK_ACTION);
            intent.putExtra(Constant.TAG_CLICK_INDEX, index);
            getActivity().sendBroadcast(intent);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_left_menu, container, false);
        initBroadcastReceiver();
        tag_list_layout = (LinearLayout) view.findViewById(R.id.tag_list_layout);
        return view;
    }

    private void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.RADIO_CHECK_ACTION);
        filter.addAction(Constant.INIT_LEFT_MENU_ACTION);
        filter.addAction(Constant.TAG_CLICK_ACTION);
        filter.addAction(Constant.TAB_CHANGE_ACTION);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private class LeftMenuBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.INIT_LEFT_MENU_ACTION)) {
                createListView();
            }

            if (action.equals(Constant.RADIO_CHECK_ACTION)) {
                currentCheckId = intent.getIntExtra(Constant.RADIO_CHECK_ID, R.id.rd_idrive);
               if(currentCheckId == R.id.rd_idrive) {
                    Intent intent1 = new Intent();
                    intent1.setAction(Constant.TOP_POPUP_LIST_CLICK_ACTION);
                    intent1.putExtra(Constant.TOP_POPUP_LIST_CLICK_INDEX,RD_IDRIVE);
                    getActivity().sendBroadcast(intent1);
                }else {
                    Intent intent2 = new Intent();
                    intent2.setAction(Constant.TOP_POPUP_INTERACT_LIST_CLICK_ACTION);
                    intent2.putExtra(Constant.TOP_POPUP_INTERACT_LIST_CLICK_INDEX,RD_INTERACT);
                    getActivity().sendBroadcast(intent2);
                }

            }

            if (action.equals(Constant.TAG_CLICK_ACTION)) {
                int index = intent.getIntExtra(Constant.TAG_CLICK_INDEX, 0);
                checkTag(index);
            }

            if (action.equals(Constant.TAB_CHANGE_ACTION)) {
                int index = intent.getIntExtra(Constant.TAB_CHANGE_INDEX, 0);
                checkTag(index);
            }
        }
    }

    /**
     * 根据currentCheckId，创建List
     */
    private void createListView() {
        if (currentCheckId == R.id.rd_idrive) {
            mTagList = ((MainActivity) getActivity()).getIdriveTagList();
            createTwoColumn(mTagList);
        }
        if (currentCheckId == R.id.rd_hd) {
            mTagList = ((MainActivity) getActivity()).getInteractTagList();
            createOneColumn(mTagList);
        }
    }

    private void addToTagListLayout(View v) {
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                getResources().getDimensionPixelOffset(R.dimen.size50));
        tag_list_layout.addView(v, llp);
    }

    /**
     *
     * 创建第一列Channel的list
     * @param list
     */
    private void createOneColumn(ArrayList<Channel> list) {
        if (list==null || list.size() <= 0) {
            return;
        }

        tag_list_layout.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_left_menu_one_column, null);
            TextView txt = (TextView) v.findViewById(R.id.txt_tag);
            txt.setTag(i);
            txt.setText(list.get(i).getTitle());
            txt.setOnClickListener(mTagClickListener);
            setTextViewStyle(txt);
            addToTagListLayout(v);
        }
    }

    /**
     * 创建第二列Channel的list
     * @param list
     */
    private void createTwoColumn(ArrayList<Channel> list) {
        if (list==null || list.size() <= 0) {
            return;
        }

        tag_list_layout.removeAllViews();
        //对第一个Tag单独处理
        View v_first = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_left_menu_first, null);
        TextView tag_all = (TextView) v_first.findViewById(R.id.tag_all);
        tag_all.setText(list.get(0).getTitle());
        tag_all.setTag(0);
        tag_all.setOnClickListener(mTagClickListener);
        setTextViewStyle(tag_all);
        addToTagListLayout(v_first);

        //如果除开第一个Tag，list为奇数，那么最后一个Tag要单独处理
        Channel c_last = null;
        if ((list.size() - 1) % 2 != 0) {
            c_last = list.get(list.size() - 1);
        }

        for (int i = 1; i < list.size() - 1; i = i + 2) {
            Channel c_l = list.get(i);
            Channel c_r = list.get(i + 1);
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_left_menu, null);
            TextView txt_left = (TextView) v.findViewById(R.id.tag_left);
            txt_left.setText(c_l.getTitle());
            txt_left.setTag(i);
            txt_left.setOnClickListener(mTagClickListener);
            setTextViewStyle(txt_left);

            TextView txt_right = (TextView) v.findViewById(R.id.tag_right);
            txt_right.setText(c_r.getTitle());
            txt_right.setTag(i + 1);
            txt_right.setOnClickListener(mTagClickListener);
            setTextViewStyle(txt_right);

            addToTagListLayout(v);
        }
        if (c_last != null) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_left_menu, null);
            TextView txt_left = (TextView) v.findViewById(R.id.tag_left);
            txt_left.setText(c_last.getTitle());
            txt_left.setTag(list.size() - 1);
            txt_left.setOnClickListener(mTagClickListener);
            setTextViewStyle(txt_left);

            addToTagListLayout(v);
        }
    }

    /**
     * Tag的状态
     * @param index
     */
    private void checkTag(int index) {
        int last_index = getCurrentIndex();

        TextView last = (TextView) tag_list_layout.findViewWithTag(last_index);
        TextView now = (TextView) tag_list_layout.findViewWithTag(index);

        last.setTextColor(getResources().getColor(R.color.detail_black));
        last.getPaint().setFakeBoldText(false);

        now.setTextColor(getResources().getColor(R.color.popup_list_selected_font));
        now.getPaint().setFakeBoldText(true);

        setCurrentIndex(index);
    }

    /**
     * 获取CurrentIndex
     * @return
     */
    private int getCurrentIndex() {
        int cur_index = 0;
        switch (currentCheckId) {
            case R.id.rd_idrive:
                cur_index = cur_tag_index_idrive;
                break;
            case R.id.rd_hd:
                cur_index = cur_tag_index_interact;
                break;
            default:
                break;
        }
        return cur_index;
    }

    /**
     * 设置CurrentIndex
     * @param index
     */
    private void setCurrentIndex(int index) {
        switch (currentCheckId) {
            case R.id.rd_idrive:
                cur_tag_index_idrive = index;
                break;
            case R.id.rd_hd:
                cur_tag_index_interact = index;
                break;
            default:
                break;
        }
    }

    private void setTextViewStyle(TextView txt) {
        if (getCurrentIndex() == (int) txt.getTag()) {
            txt.setTextColor(getResources().getColor(R.color.popup_list_selected_font));
            txt.getPaint().setFakeBoldText(true);
        } else {
            txt.setTextColor(getResources().getColor(R.color.detail_black));
            txt.getPaint().setFakeBoldText(false);
        }
    }
}
