package com.ccpress.izijia.yd.entity;

import java.util.List;

/**
 * Created by dengmingzhi on 16/6/1.
 */
public class YdOrderInfo {
    public int result;
    public List<Baoxian> baoxian;
    public List<Youke> youke_list;
    public Supplier supplier;
    public List<GoodsList> goods_list;
    public Data data;
    public class Data {
        public String add_time;
        public String consignee;
        public double crfee;
        public String crnum;
        public String days;
        public String dianping;
        public double etfee;
        public String etnum;
        public String gotime;
        public String mobile;
        public double money_paid;
        public double order_amount;
        public String order_id;
        public String order_sn;
        public String order_status;
        public String outtime;
        public String renshu;
    }

    public class Baoxian {
        public String free_money;
        public String pack_desc;
        public Double pack_fee;
        public String pack_id;
        public String pack_name;
    }

    public class GoodsList {
        public String days;
        public String goods_name;
        public int goods_number;
        public double goods_price;
        public String goods_thumb;
        public double market_price;
        public String subtotal;
        public List<Prices> prices;
    }

    public class Prices {
        public String true_price;
        public String xc_day;
    }

    public class Supplier {
        public String address;
        public String supplier_name;
        public String tel2;
    }

    public class Youke {
        public String mobile;
        public String name;
        public String renqun;
        public String zj;
        public String zjnum;
    }
}
