package com.ccpress.izijia.dfy.entity;

import java.io.Serializable;

/**
 * Created by administror on 2016/3/25 0025.
 */
public class WriteInfo implements Serializable{
    private String good_name;
    private String brand_name;
    private String attr_value;
    private int cheng_count;
    private int er_count;
    private String goods_fysm;
    private String tdgz;
    private int cheng_price;
    private int er_price;
    private String fangcha;
    private String goods_id;
    private String fapiao;

    public String getFapiao() {
        return fapiao;
    }

    public void setFapiao(String fapiao) {
        this.fapiao = fapiao;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getFangcha() {
        return fangcha;
    }

    public void setFangcha(String fangcha) {
        this.fangcha = fangcha;
    }

    public int getCheng_price() {
        return cheng_price;
    }

    public void setCheng_price(int cheng_price) {
        this.cheng_price = cheng_price;
    }

    public int getEr_price() {
        return er_price;
    }

    public void setEr_price(int er_price) {
        this.er_price = er_price;
    }

    public String getGood_name() {
        return good_name;
    }

    public void setGood_name(String good_name) {
        this.good_name = good_name;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getAttr_value() {
        return attr_value;
    }

    public void setAttr_value(String attr_value) {
        this.attr_value = attr_value;
    }

    public int getCheng_count() {
        return cheng_count;
    }

    public void setCheng_count(int cheng_count) {
        this.cheng_count = cheng_count;
    }

    public int getEr_count() {
        return er_count;
    }

    public void setEr_count(int er_count) {
        this.er_count = er_count;
    }

    public String getGoods_fysm() {
        return goods_fysm;
    }

    public void setGoods_fysm(String goods_fysm) {
        this.goods_fysm = goods_fysm;
    }

    public String getTdgz() {
        return tdgz;
    }

    public void setTdgz(String tdgz) {
        this.tdgz = tdgz;
    }
}
