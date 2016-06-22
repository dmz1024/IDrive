package com.ccpress.izijia.yd.entity;

import java.util.List;

/**
 * Created by dengmingzhi on 16/5/31.
 */
public class LocalPeoson {
    public int result;
    public Data data;

    public class Data {
        public String cat_id;
        public int count;
        public String edit_map;
        public String goods_desc;
        public String goods_id;
        public String goods_name;
        public String goods_type;
        public String nianling;
        public String opentime;
        public int rank;
        public String rzgz;
        public String sex;
        public String shop_price;
        public String tel1;
        public String tksm;
        public String cwsm;
        public Area city;
        public Area country;
        public Area province;
        public List<TypeList> type_list;
        public List<Lb> lb;
    }

    public class Area {
        public String region_id;
        public String region_name;
    }

    public class TypeList {
        public String[] select;
    }

    public class Lb {
        public String img_original;
    }

}
