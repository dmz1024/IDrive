package com.ccpress.izijia.dfy.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by administror on 2016/3/31 0031.
 */
public class OrderList implements Serializable {

    private String result;
    private List<Order> list=new ArrayList<Order>();

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Order> getList() {
        return list;
    }

    public void setList(List<Order> list) {
        this.list = list;
    }
}
