package com.ccpress.izijia.yd.entity;

import java.util.List;

/**
 * Created by dengmingzhi on 16/5/26.
 */
public class Option {
    public int result;
    public List<Data> data;

    public class Data {
        public String name;
        public List<options> option;
    }



}
