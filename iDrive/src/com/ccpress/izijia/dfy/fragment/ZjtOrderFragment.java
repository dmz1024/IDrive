package com.ccpress.izijia.dfy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.fragment.ZjtOrderMainFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/26.
 */
public class ZjtOrderFragment extends Fragment implements View.OnClickListener, PopupWindow.OnDismissListener {
    private RelativeLayout rl_left;
    private RelativeLayout rl_right;
    private TextView tv_1;
    private TextView tv_2;
    private PopupWindow pop;
    private ZjtOrderMainFragment orderFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=View.inflate(getActivity(), R.layout.yd_frag_order,null);
        rl_left= (RelativeLayout) view.findViewById(R.id.rl_left);
        rl_right= (RelativeLayout) view.findViewById(R.id.rl_right);
        rl_left.setOnClickListener(this);
        rl_right.setOnClickListener(this);
        tv_1= (TextView) view.findViewById(R.id.tv_1);
        tv_2= (TextView) view.findViewById(R.id.tv_2);
        orderFragment=new ZjtOrderMainFragment();
        getChildFragmentManager().beginTransaction().add(R.id.frag_order,orderFragment).commit();
        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_left:
                left();
                break;
            case R.id.rl_right:
                right();
                break;
        }
    }


    private void right() {
//        iv_right.setVisibility(View.VISIBLE);
        showPop(new RightAdapter(getRightData()));
    }

    private void left() {
//        iv_left.setVisibility(View.VISIBLE);
        showPop(new LeftAdapter(getLeftData()));
    }


    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }


    private void showPop(Adapter adapter) {
        backgroundAlpha(0.5f);
        View view = View.inflate(getActivity(), R.layout.dfy_item_pop_listview, null);
        ListView listView = (ListView) view.findViewById(R.id.lv_content);
        listView.setEnabled(false);
        listView.setAdapter(adapter);
        pop = new PopupWindow(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setOnDismissListener(this);
        pop.showAsDropDown(rl_left);
    }


    private List<String> getLeftData() {
        List<String> list = new ArrayList<String>();
        list.add(getResources().getString(R.string.dfy_order_all));
        list.add(getResources().getString(R.string.dfy_order_dzf));
        list.add(getResources().getString(R.string.dfy_order_dcx));
        list.add(getResources().getString(R.string.dfy_order_dpj));
        list.add(getResources().getString(R.string.dfy_order_yqx));
        return list;
    }

    private List<String> getRightData() {
        List<String> list = new ArrayList<String>();
        list.add(getResources().getString(R.string.dfy_order_xdsj));
        list.add(getResources().getString(R.string.dfy_order_cxsj));
        return list;
    }

    @Override
    public void onResume() {
        super.onResume();
        orderFragment.initData();
    }

    @Override
    public void onDismiss() {
        backgroundAlpha(1f);
    }


    class LeftAdapter extends Adapter {

        public LeftAdapter(List<String> list) {
            super(list);
        }


        @Override
        void OnClick(int i, Map<String, String> map) {

            if (i == 1) {
                map.put("order_status", "0");
            } else if (i == 2) {
                map.put("order_status", "1");
            } else if (i == 3) {
                map.put("order_status", "7");
            } else if (i == 4) {
                map.put("order_status", "2");
            } else if (i == 0) {
                map.put("order_status", "");
            }
            tv_1.setText(list.get(i));
        }
    }

    class RightAdapter extends Adapter {


        public RightAdapter(List<String> list) {
            super(list);
        }

        @Override
        void OnClick(int i, Map<String, String> map) {
            if (i == 0) {
                map.put("orderby", "add_time");
            } else if (i == 1) {
                map.put("orderby", "cyrq");
            }
            tv_2.setText(list.get(i));

        }
    }

    abstract class Adapter extends BaseAdapter {

        public List<String> list;

        public Adapter(List<String> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View v = View.inflate(getActivity(),R.layout.dfy_item_order_pop, null);
            TextView tv = (TextView) v  .findViewById(R.id.tv_content);
            tv.setText(list.get(i));
            v.setOnClickListener(new View .OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String, String> map = orderFragment.map();
                    map.clear();
                    OnClick(i, map);
                    pop.dismiss();
                    orderFragment.setMap(map);
                    orderFragment.initData();

                }
            });
            return v;
        }

        abstract void OnClick(int i, Map<String, String> map);

    }
}
