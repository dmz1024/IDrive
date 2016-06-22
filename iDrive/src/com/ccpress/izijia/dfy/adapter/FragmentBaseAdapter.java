package com.ccpress.izijia.dfy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.ccpress.izijia.dfy.fragment.FragmentBase;

import java.util.List;

/**
 * Created by dmz1024 on 2016/3/20.
 */
public class FragmentBaseAdapter extends FragmentPagerAdapter {
    private List<FragmentBase> listFragments;

    public FragmentBaseAdapter(FragmentManager fm, List<FragmentBase> listFragments) {
        super(fm);
        this.listFragments=listFragments;
    }

    @Override
    public Fragment getItem(int i) {
        return listFragments.get(i);
    }

    @Override
    public int getCount() {
        return listFragments.size();
    }

}
