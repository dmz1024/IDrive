package com.ccpress.izijia.yd.util;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Field;

/**
 * Created by dengmingzhi on 16/5/27.
 */
public class Utility {
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        //获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    public static void setGridViewHeightBasedOnChildren(GridView gridView) {
        // 获取listview的adapter
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        // 固定列宽，有多少列
        int col = gridView.getNumColumns();// listView.getNumColumns();
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        gridView.setLayoutParams(params);
    }

    public static void setGridViewHeightBasedOnChildren(GridView gridView,
                                                        View... views) {
        //获取对应的adapter
        ListAdapter listAdapter = gridView.getAdapter();

        Class tempGridView = GridView.class; // 获得gridview这个类的class

        int column = -1;
        try {

            Field field = tempGridView.getDeclaredField("mRequestedNumColumns"); // 获得申明的字段
            field.setAccessible(true); // 设置访问权限
            column = Integer.valueOf(field.get(gridView).toString()); // 获取字段的值
        } catch (Exception e1) {
        }
        if (column == -1)
            return;

        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i += column) { // 这边因为一排column个，所以i
            // +=
            // column
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0); // 计算子项View 的宽高

            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        try {
            if (views != null) {
                for (View view : views) {
                    view.measure(0, 0);
                    totalHeight += view.getMeasuredHeight();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight
                + (gridView.getHeight() * (listAdapter.getCount() - 1)) + 10;
        gridView.setLayoutParams(params);
    }
}
