package com.ccpress.izijia.dfy.entity;

/**
 * Created by dmz1024 on 2016/3/17.
 */

public class Idrive {
    private String goods_id;//商品id
    private String goods_appname;//商品APP标题
    private String goods_appimg;//APP首页缩略图
    private String shop_price;//商品起价
    private String xingcheng;
    private int is_new;//新品
    private int is_hot;//热卖
    private int is_best;//置顶
    private int lasttime;

    public int getLasttime() {
        return lasttime;
    }

    public void setLasttime(int lasttime) {
        this.lasttime = lasttime;
    }

    public String getXingcheng() {
        return xingcheng;
    }

    public void setXingcheng(String xingcheng) {
        this.xingcheng = xingcheng;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_appname() {
        return goods_appname;
    }

    public void setGoods_appname(String goods_appname) {
        this.goods_appname = goods_appname;
    }

    public String getGoods_appimg() {
        return goods_appimg;
    }

    public void setGoods_appimg(String goods_appimg) {
        this.goods_appimg = goods_appimg;
    }

    public String getShop_price() {
        return shop_price;
    }

    public void setShop_price(String shop_price) {
        this.shop_price = shop_price;
    }

    public int getIs_new() {
        return is_new;
    }

    public void setIs_new(int is_new) {
        this.is_new = is_new;
    }

    public int getIs_hot() {
        return is_hot;
    }

    public void setIs_hot(int is_hot) {
        this.is_hot = is_hot;
    }

    public int getIs_best() {
        return is_best;
    }

    public void setIs_best(int is_best) {
        this.is_best = is_best;
    }
}
