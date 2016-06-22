package com.ccpress.izijia.dfy.entity;

import java.io.Serializable;

/**
 * Created by dmz1024 on 2016/4/7.
 */
public class PayInfo implements Serializable {
    private String tourist_name;
    private String goods_name;
    private String pay_type;
    private String order_num;
    private String pay_time;
    private String pay_money;

    public String getTourist_name() {
        return tourist_name;
    }

    public void setTourist_name(String tourist_name) {
        this.tourist_name = tourist_name;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getOrder_num() {
        return order_num;
    }

    public void setOrder_num(String order_num) {
        this.order_num = order_num;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getPay_money() {
        return pay_money;
    }

    public void setPay_money(String pay_money) {
        this.pay_money = pay_money;
    }
}
