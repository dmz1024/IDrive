package com.ccpress.izijia.yd.entity;

import java.util.List;

/**
 * Created by dengmingzhi on 16/5/27.
 */
public class ChooseStores {
    public int result;
    public List<Taocan> taocan;
    public List<Tese> tese;
    public List<YwAndZs> yingwei;
    public List<YwAndZs> zhusu;

    public class Taocan {
        public String goods_desc;
        public String amount_price;
        public String goods_id;
        public String goods_name;
        public String goods_thumb;
        public int maxnum;
        public String mc_id;
        public double shop_price;
        public String short_name;
        public List<Lb> lb;

    }

    public class Tese {
        public String amount_price;
        public String goods_desc;
        public String goods_id;
        public String goods_name;
        public String goods_thumb;
        public double shop_price;
        public String short_name;
        public List<Lb> lb;
    }

    public class YwAndZs {
        public String amount_price;
        public String edit_map;
        public String goods_desc;
        public String goods_id;
        public String goods_name;
        public String goods_thumb;
        public double shop_price;
        public String short_name;
        public List<Lb> lb;
        public List<Attr> attr;
        public String[] tag;
    }

    public class Lb {
        public String img_original;
    }

    public class Attr {
        public String attr_name;
        public String attr_value;
    }
}
