package com.ccpress.izijia.wheel;

import java.util.ArrayList;

/**
 * Created by Wu Jingyu
 * Date: 2015/5/19
 * Time: 10:07
 */
public class TagWheelAdapter implements WheelAdapter {
    private ArrayList<String> mTagList;

    public TagWheelAdapter() {
        mTagList = new ArrayList<String>();
        mTagList.add("踏春");
        mTagList.add("美食");
        mTagList.add("夕阳");
        mTagList.add("亲子");
        mTagList.add("自驾");
        mTagList.add("海岛");
    }

    public TagWheelAdapter(ArrayList<String> list) {
        mTagList = list;
    }

    @Override
    public int getItemsCount() {
        return mTagList.size();
    }

    @Override
    public String getItem(int index) {
        return mTagList.get(index);
    }

    @Override
    public int getMaximumLength() {
        return 3;
    }
}
