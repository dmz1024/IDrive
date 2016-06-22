package com.ccpress.izijia.yd.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dengmingzhi on 16/5/30.
 */
public class YdOrder {
    public int result;
    public List<Data> datas;

    public class Data {
        public int dianping;
        public String add_time;
        public String consignee;
        public String gotime;
        public String logo;
        public String mobile;
        public String money_paid;
        public String num;
        public String ocid;
        public String order_amount;
        public String order_id;
        public String order_sn;
        public String order_status;
        public String outtime;
        public String pack_fee;
        public String pay_status;
        public int renshu;
        public String shipping_status;
        public String supplier_id;
        public String supplier_name;
        public String supplier_title;
        public int status;
        public List<Goods> goods;
    }

    public class Goods {
        public double amount_price;
        public String cat_id;
        public String cat_name;
        public String goods_id;
        public String goods_name;
        public int goods_number;
        public String thumb;
        public String url;
        public String tag;

    }
}
