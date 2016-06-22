package com.ccpress.izijia.yd.listener;

/**
 * Created by dengmingzhi on 16/5/28.
 */
public interface OnCountChangeListener {
    void jia(int type, double price,String goods_id, Object[] obj);

    void jian(int type, String goods_id,double price);
}
