package com.ccpress.izijia.yd.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.activity.DetailsActivity;
import com.ccpress.izijia.dfy.activity.SearchActivity;
import com.ccpress.izijia.dfy.adapter.CarouselAdapter;
import com.ccpress.izijia.dfy.adapter.IdriveAdapter;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.entity.Ad;
import com.ccpress.izijia.dfy.entity.Idrive;
import com.ccpress.izijia.dfy.fragment.FragmentBase;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.dfy.view.RefreshListView;
import com.ccpress.izijia.yd.StoresAdapter;
import com.ccpress.izijia.yd.activity.StoresInfoActivity;
import com.ccpress.izijia.yd.activity.YdFilterActivity;
import com.ccpress.izijia.yd.activity.YdOrderInfoActivity;
import com.ccpress.izijia.yd.api.MyImageLoader;
import com.ccpress.izijia.yd.constant.ConstantApi;
import com.ccpress.izijia.yd.entity.Stores;
import com.ccpress.izijia.yd.view.RotationViewPager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by dmz1024 on 2016/3/20.
 */
public class YdFragment extends FragmentBase<Stores.Data, StoresAdapter> {
    private View v;
    private ViewPager mVp;
    private RotationViewPager vp_2;
    private CarouselAdapter mCarouseAdapter;
    private RelativeLayout all, jingnei, jingwai;
    private TextView tv_all, tv_jingnei, tv_jingwai;
    private ImageView iv_all, iv_jingnei, iv_jingwai;
    private boolean isCarouse = true;
    private MyImageLoader loader = new MyImageLoader(3);

    private Map<String, Object> map = new HashMap<String, Object>();
    private int id = 0;

    public void setId(int id) {
        this.id = id;
    }

    public int getCurrenId() {
        return id;
    }

    private void changeType(int id) {
        this.id = id;
        page = 1;
        isFresh = true;
        isClear = true;
        initData(false);
        //跳转到筛选页面
        Intent intent = new Intent(getActivity(), YdFilterActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
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
    protected void onItemClick(List<Stores.Data> list, int i) {
        if (i < 2 || i >= list.size() + 2) {
            return;
        }
        Intent intent = new Intent(getActivity(), StoresInfoActivity.class);
        intent.putExtra("id", list.get(i - 2).supplier_id);
        intent.putExtra("title", list.get(i - 2).supplier_name);
        intent.putExtra("img", list.get(i - 2).logo);
        startActivity(intent);
    }

    @Override
    protected List<Stores.Data> getList(String json) {
        return JsonUtil.json2List(json, Stores.Data.class, "data");
    }

    @Override
    protected String getUrl() {
        return ConstantApi.STORES;
    }

    @Override
    protected StoresAdapter getAdapter(List<Stores.Data> list) {
        return new StoresAdapter(list);
    }


    @Override
    protected View getHeaderView() {
        v = View.inflate(getActivity(), R.layout.yd_item_header_yd, null);
        initHeaderView();
        return v;
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        int index = 0;
        switch (view.getId()) {
            case R.id.all:
                index = 0;
                break;
            case R.id.jingnei:
                index = 9;
                break;
            case R.id.jingwai:
                index = 10;
                break;
        }
        setCurrentItem(index);
    }


    private void setCurrentItem(int index) {
        ColorStateList csl_999 = getActivity().getResources().getColorStateList(R.color.dfy_999);
        ColorStateList csl_50ddbd = getActivity().getResources().getColorStateList(R.color.dfy_50bbdb);
        ColorStateList csl_fff = getActivity().getResources().getColorStateList(R.color.dfy_fff);
        all.setBackgroundResource(R.drawable.dfy_icon_sort);
        jingnei.setBackgroundResource(R.drawable.dfy_icon_sort);
        jingwai.setBackgroundResource(R.drawable.dfy_icon_sort);
        iv_all.setImageResource(R.drawable.yd_icon_all);
        iv_jingwai.setImageResource(R.drawable.yd_icon_haiwai);
        iv_jingnei.setImageResource(R.drawable.yd_icon_guonei);
        tv_all.setTextColor(csl_999);
        tv_jingnei.setTextColor(csl_999);
        tv_jingwai.setTextColor(csl_999);

        switch (index) {
            case 0:
                tv_all.setTextColor(csl_fff);
                all.setBackgroundColor(getResources().getColor(R.color.dfy_50bbdb));
                iv_all.setImageResource(R.drawable.yd_icon_all_checked);
                break;
            case 9:
                tv_jingnei.setTextColor(csl_fff);
                jingnei.setBackgroundColor(getResources().getColor(R.color.dfy_50bbdb));
                iv_jingnei.setImageResource(R.drawable.yd_icon_guonei_checked);
                break;
            case 10:
                tv_jingwai.setTextColor(csl_fff);
                jingwai.setBackgroundColor(getResources().getColor(R.color.dfy_50bbdb));
                iv_jingwai.setImageResource(R.drawable.yd_icon_haiwai_checked);
                break;
        }
        type = true;
        changeType(index);

    }

    private void initHeaderView() {
        final List<Ad> listAd_1 = new ArrayList<Ad>();
        final List<Ad> listAd_2 = new ArrayList<Ad>();
        //轮播ViewPager
        mVp = (ViewPager) v.findViewById(R.id.vp_carousel);
        vp_2 = (RotationViewPager) v.findViewById(R.id.vp_2);
        mVp.setAdapter(mCarouseAdapter = new CarouselAdapter() {
            @Override
            public void adOnClick(Ad ad) {
                startActivity(new Intent(getActivity(), StoresInfoActivity.class).putExtra("id", ad.getAd_link())
                        .putExtra("title", ad.getAd_name()).putExtra("img", ad.getAd_code()));
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
        iv_all = (ImageView) v.findViewById(R.id.iv_all);
        iv_jingnei = (ImageView) v.findViewById(R.id.iv_jingnei);
        iv_jingwai = (ImageView) v.findViewById(R.id.iv_jingwai);
        Map<String, Object> mapAd = new HashMap<String, Object>();
        mapAd.put("pid", 61);
        NetUtil.Post(ConstantApi.AD, mapAd, new com.ccpress.izijia.dfy.callBack.MyCallBack() {
            @Override
            public void onSuccess(String s) {
                try {
                    JSONArray oArray = new JSONArray(s);
                    for (int i = 0; i < oArray.length(); i++) {
                        Ad ad = new Ad();
                        JSONObject object = oArray.getJSONObject(i);
                        ad.setAd_code(object.getString("ad_code"));
                        ad.setAd_name(object.getString("ad_name"));
                        ad.setAd_link(object.getString("ad_link"));
                        listAd_1.add(ad);
                    }
                    mCarouseAdapter.setAdList(listAd_1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
        mapAd.put("pid", 62);
        NetUtil.Post(ConstantApi.AD, mapAd, new com.ccpress.izijia.dfy.callBack.MyCallBack() {
            @Override
            public void onSuccess(String s) {
                try {
                    JSONArray oArray = new JSONArray(s);
                    for (int i = 0; i < oArray.length(); i++) {
                        Ad ad = new Ad();
                        JSONObject object = oArray.getJSONObject(i);
                        ad.setAd_code(object.getString("ad_code"));
                        ad.setAd_name(object.getString("ad_name"));
                        ad.setAd_link(object.getString("ad_link"));
                        listAd_2.add(ad);
                    }
                    List<View> views = new ArrayList<View>();
                    for (int i = 0; i < listAd_2.size(); i = i + 2) {
                        View view = View.inflate(getActivity(), R.layout.yd_item_ad, null);
                        final Ad ad_1 = listAd_2.get(i);
                        final Ad ad_2 = listAd_2.get(i + 1);
                        ImageView iv_1 = (ImageView) view.findViewById(R.id.iv_1);
                        ImageView iv_2 = (ImageView) view.findViewById(R.id.iv_2);
                        loader.get(iv_1, ad_1.getAd_code());
                        loader.get(iv_2, ad_2.getAd_code());
                        iv_1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(getActivity(), StoresInfoActivity.class).putExtra("id", ad_1.getAd_link())
                                        .putExtra("title", ad_1.getAd_name()).putExtra("img", ad_1.getAd_code()));
                            }
                        });
                        iv_2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(getActivity(), StoresInfoActivity.class).putExtra("id", ad_2.getAd_link())
                                        .putExtra("title", ad_2.getAd_name()).putExtra("img", ad_2.getAd_code()));
                            }
                        });
                        views.add(view);
                    }

                    vp_2.setImages(views);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });


        all.setOnClickListener(this);
        jingnei.setOnClickListener(this);
        jingwai.setOnClickListener(this);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 10) {
                mVp.setCurrentItem(mVp.getCurrentItem() + 1);
            }

        }
    };


    private Timer timer;

    private void timer() {
        if (timer == null) {
            timer = new Timer();
        }
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(10);
            }
        };

        timer.schedule(timerTask, 1000, 3000);
    }
}
