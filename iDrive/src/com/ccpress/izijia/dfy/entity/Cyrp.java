package com.ccpress.izijia.dfy.entity;

import java.io.Serializable;

/**
 * Created by administror on 2016/3/23 0023.
 * 报名相关类
 */
public class Cyrp implements Serializable {

    private String attr_end;//报名截止日期
    private String attr_etprice;//儿童价
    private String attr_price;//成人价
    private String attr_value;//出发日期

    public String getAttr_end() {
        return attr_end;
    }

    public void setAttr_end(String attr_end) {
        this.attr_end = attr_end;
    }

    public String getAttr_etprice() {
        return attr_etprice;
    }

    public void setAttr_etprice(String attr_etprice) {
        this.attr_etprice = attr_etprice;
    }

    public String getAttr_price() {
        return attr_price;
    }

    public void setAttr_price(String attr_price) {
        this.attr_price = attr_price;
    }

    public String getAttr_value() {
        return attr_value;
    }

    public void setAttr_value(String attr_value) {
        this.attr_value = attr_value;
    }


}
