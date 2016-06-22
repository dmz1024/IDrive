package com.ccpress.izijia.dfy.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.yd.fragment.YdOrderFragment;
import com.ccpress.izijia.dfy.view.NoScrollViewPager;
import com.ccpress.izijia.dfy.fragment.ZjtOrderFragment;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by administror on 2016/3/31 0031.
 */
public class OrderActivity extends FragmentActivity implements View.OnClickListener {
    @ViewInject(R.id.iv_left)
    private ImageView iv_left;
    @ViewInject(R.id.tv_zjt)
    private TextView tv_zjt;
    @ViewInject(R.id.tv_yd)
    private TextView tv_yd;
    @ViewInject(R.id.vp_order)
    private NoScrollViewPager vp_order;
    @ViewInject(R.id.rl_idrive)
    private RelativeLayout rl_idrive;
    @ViewInject(R.id.rl_yd)
    private RelativeLayout rl_yd;

    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dfy_activity_personal_order);
        x.view().inject(this);
        initView();
        initData();
    }

    private void initView() {
        rl_idrive.setOnClickListener(this);
        rl_yd.setOnClickListener(this);
        iv_left.setOnClickListener(this);
        rl_idrive.setBackgroundResource(R.drawable.rdbtn_idrive);
        rl_yd.setBackgroundResource(R.drawable.rdbtn_hd);
        ColorStateList csl = getResources().getColorStateList(R.color.rdbtn_txt_color);
        if (csl != null) {
            tv_zjt.setTextColor(csl);
            tv_yd.setTextColor(csl);
        }
        rl_idrive.setSelected(true);
    }

    private void initData() {
        fragmentList = new ArrayList<>();
        ZjtOrderFragment zjtOrderFragment = new ZjtOrderFragment();
        YdOrderFragment ydOrderFragment = new YdOrderFragment();
        fragmentList.add(zjtOrderFragment);
        fragmentList.add(ydOrderFragment);
        vp_order.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragmentList.get(i);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        vp_order.setOffscreenPageLimit(fragmentList.size());
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_idrive:
            case R.id.rl_yd:
                rl((RelativeLayout) view);
                break;
            case R.id.iv_left:
                finish();
                break;
        }
    }


    private void rl(RelativeLayout view) {
        if (view == rl_idrive) {
            rl_idrive.setSelected(true);
            rl_yd.setSelected(false);
            if (vp_order.getCurrentItem() == 0) {
                return;
            }
            vp_order.setCurrentItem(0, false);
        } else {
            rl_idrive.setSelected(false);
            rl_yd.setSelected(true);
            if (vp_order.getCurrentItem() == 1) {
                return;
            }
            vp_order.setCurrentItem(1, false);
        }
    }

}
