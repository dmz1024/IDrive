package com.ccpress.izijia.dfy.entity;

/**
 * Created by dmz1024 on 2016/3/20.
 */
public class SearchIdrive{
    private String brand_id;//领队ID
    private String brand_name;// 领队名称
    private int click_count;
    private String endtime;//出行日期
    private String goods_appname;//商品APP标题
    private String goods_appimg;//APP首页缩略图
    private String goods_id;//商品id
    private String goods_thumb;
    private int is_new;//新品
    private int is_hot;//热卖
    private int is_best;//置顶
    private int lasttime;//是否过期
    private String seller_note;
    private String shop_price;//商品起价
    private String xc;//行程天数
    private String xingcheng;
    private String zan;

    public int getLasttime() {
        return lasttime;
    }

    public void setLasttime(int lasttime) {
        this.lasttime = lasttime;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public int getClick_count() {
        return click_count;
    }

    public void setClick_count(int click_count) {
        this.click_count = click_count;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
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


    public String getGoods_id() {
        return goods_id;
    }


    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_thumb() {
        return goods_thumb;
    }

    public void setGoods_thumb(String goods_thumb) {
        this.goods_thumb = goods_thumb;
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

    public String getSeller_note() {
        return seller_note;
    }

    public void setSeller_note(String seller_note) {
        this.seller_note = seller_note;
    }


    public String getShop_price() {
        return shop_price;
    }


    public void setShop_price(String shop_price) {
        this.shop_price = shop_price;
    }

    public String getXc() {
        return xc;
    }

    public void setXc(String xc) {
        this.xc = xc;
    }


    public String getXingcheng() {
        return xingcheng;
    }


    public void setXingcheng(String xingcheng) {
        this.xingcheng = xingcheng;
    }

    public String getZan() {
        return zan;
    }

    public void setZan(String zan) {
        this.zan = zan;
    }
}
