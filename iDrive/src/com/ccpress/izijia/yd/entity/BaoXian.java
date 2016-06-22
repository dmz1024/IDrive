package com.ccpress.izijia.yd.entity;

import java.util.List;

/**
 * Created by dengmingzhi on 16/5/29.
 */
public class BaoXian {
    public int result;
    public List<Data> data;
    public String tksm;
    public class Data {
        public double crfee;
        public double etfee;
        public String pack_desc;
        public String pack_id;
        public String pack_name;
    }
}
