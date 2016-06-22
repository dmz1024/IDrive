package com.ccpress.izijia.dfy.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.activity.DetailsActivity;
import com.ccpress.izijia.dfy.activity.SearchActivity;
import com.ccpress.izijia.dfy.adapter.CarouselAdapter;
import com.ccpress.izijia.dfy.adapter.IdriveAdapter;
import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.entity.Ad;
import com.ccpress.izijia.dfy.entity.Idrive;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.dfy.view.RefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by dmz1024 on 2016/3/20.
 */
public class IDriveFragment extends FragmentBase<Idrive, IdriveAdapter> {
    private View v;
    private ViewPager mVp;
    private CarouselAdapter mCarouseAdapter;
    private RelativeLayout all, jingnei, jingwai;
    private TextView tv_all, tv_jingnei, tv_jingwai;
    private ImageView iv_all,iv_jingnei,iv_jingwai;
    private RelativeLayout search;
    private boolean isCarouse = true;

    private Map<String, Object> map = new HashMap<String, Object>();
    private int id = 0;

    public void setId(int id) {
        this.id = id;
    }

    public int getCurrenId() {
        return id;
    }

    private void changeType(int id) {
        if (this.id == id) {
            return;
        }
        this.id = id;
        page = 1;
        isFresh = true;
        isClear = true;
        initData(false);
    }

    @Override
    protected RefreshListView.Mode getMode() {
        return RefreshListView.Mode.BOTH;
    }

    @Override
    protected Map<String, Object> post() {
        map.put("id", id);
        map.put("page", page);
        return map;
    }


    @Override
    protected boolean getSubmitType() {
        return true;
    }

    @Override
    protected void onItemClick(List<Idrive> list, int i) {
        if (i < 2 || i-1>mlist.size()) {
            return;
        }
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("id", list.get(i - 2).getGoods_id());
        startActivity(intent);
    }

    @Override
    protected List<Idrive> getList(String json) {
        return JsonUtil.json2List(json, Idrive.class, "data");
    }

    @Override
    protected String getUrl() {
        return Constant.DFY_CATEGORY;
    }

    @Override
    protected IdriveAdapter getAdapter(List<Idrive> list) {
        return new IdriveAdapter(list);
    }


    @Override
    protected View getHeaderView() {
        v = View.inflate(getActivity(), R.layout.dfy_item_header_idrive, null);
        initHeaderView();
        return v;
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == search) {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            getActivity().startActivity(intent);
        } else {
            int index = 0;
            switch (view.getId()) {
                case R.id.all:
                    index = 0;
                    break;
                case R.id.jingnei:
                    index = 1;
                    break;
                case R.id.jingwai:
                    index = 2;
                    break;
            }
            setCurrentItem(index);
        }
    }

    private void setCurrentItem(int index) {
        ColorStateList csl_999 = getActivity().getResources().getColorStateList(R.color.dfy_999);
        ColorStateList csl_50ddbd = getActivity().getResources().getColorStateList(R.color.dfy_50bbdb);
        ColorStateList csl_fff = getActivity().getResources().getColorStateList(R.color.dfy_fff);
        all.setBackgroundResource(R.drawable.dfy_icon_sort);
        jingnei.setBackgroundResource(R.drawable.dfy_icon_sort);
        jingwai.setBackgroundResource(R.drawable.dfy_icon_sort);
        iv_all.setImageResource(R.drawable.dfy_icon_all);
        iv_jingwai.setImageResource(R.drawable.dfy_icon_jingwai);
        iv_jingnei.setImageResource(R.drawable.dfy_icon_jingnei);
        tv_all.setTextColor(csl_999);
        tv_jingnei.setTextColor(csl_999);
        tv_jingwai.setTextColor(csl_999);

        switch (index) {
            case 0:
                tv_all.setTextColor(csl_fff);
                all.setBackgroundColor(getResources().getColor(R.color.dfy_50bbdb));
                iv_all.setImageResource(R.drawable.icon_all_checked);
                break;
            case 1:
                tv_jingnei.setTextColor(csl_fff);
                jingnei.setBackgroundColor(getResources().getColor(R.color.dfy_50bbdb));
                iv_jingnei.setImageResource(R.drawable.icon_jingnei_checked);
                break;
            case 2:
                tv_jingwai.setTextColor(csl_fff);
                jingwai.setBackgroundColor(getResources().getColor(R.color.dfy_50bbdb));
                iv_jingwai.setImageResource(R.drawable.icon_jingwai_checked);
                break;
        }

        changeType(index);

    }

    private void initHeaderView() {
        final List<Ad> listAd = new ArrayList<Ad>();

        //轮播ViewPager
        mVp = (ViewPager) v.findViewById(R.id.vp_carousel);
        mVp.setAdapter(mCarouseAdapter = new CarouselAdapter(){
            @Override
            public void adOnClick(String id) {
                startActivity(new Intent(getActivity(),DetailsActivity.class).putExtra("id",id));
            }
        });

        mVp.setCurrentItem(Integer.MAX_VALUE / 2);
        handler.sendEmptyMessageDelayed(0, 3000);

        timer();
        all = (RelativeLayout) v.findViewById(R.id.all);
        jingnei = (RelativeLayout) v.findViewById(R.id.jingnei);
        jingwai = (RelativeLayout) v.findViewById(R.id.jingwai);
        tv_all = (TextView) v.findViewById(R.id.tv_all);
        tv_jingnei = (TextView) v.findViewById(R.id.tv_jingnei);
        tv_jingwai = (TextView) v.findViewById(R.id.tv_jingwai);
        search = (RelativeLayout) v.findViewById(R.id.rl_search);
        iv_all= (ImageView) v.findViewById(R.id.iv_all);
        iv_jingnei= (ImageView) v.findViewById(R.id.iv_jingnei);
        iv_jingwai= (ImageView) v.findViewById(R.id.iv_jingwai);
        Map<String,Object> mapAd=new HashMap<String,Object>();
        mapAd.put("pid",131);
        NetUtil.Post(Constant.DFY_AD,mapAd,new com.ccpress.izijia.dfy.callBack.MyCallBack(){
            @Override
            public void onSuccess(String s) {
                try {
                    JSONArray oArray=new JSONArray(s);
                    for(int i=0;i<oArray.length();i++){
                        Ad ad=new Ad();
                        JSONObject object=oArray.getJSONObject(i);
                        ad.setAd_code(object.getString("ad_code"));
                        ad.setAd_name(object.getString("ad_name"));
                        ad.setAd_link(object.getString("ad_link"));
                        listAd.add(ad);
                        mCarouseAdapter.setAdList(listAd);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

        all.setOnClickListener(this);
        jingnei.setOnClickListener(this);
        jingwai.setOnClickListener(this);
        search.setOnClickListener(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==10){
                mVp.setCurrentItem(mVp.getCurrentItem() + 1);
            }

        }
    };


    private Timer timer;

    private void timer(){
        if(timer==null){
            timer=new Timer();
        }
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(10);
            }
        };

        timer.schedule(timerTask,1000,3000);
    }
}
