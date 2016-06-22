package com.ccpress.izijia.dfy.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by administror on 2016/3/25 0025.
 */
public class Goodinfo implements Serializable {
    private String brand_id;
    private String goodssn;
    private String brand_name;
    private String goods_appname;
    private String goods_fysm;//费用说明
    private String goods_name;
    private String goods_tggz;
    private String goodsid;
    private String result;
    private List<Cyrp> rili = new ArrayList<Cyrp>();
    private String tel;
    private String fangcha;
    private String goods_brief;
    private String fapiao;
    private String goods_thumb;

    public String getGoods_thumb() {
        return goods_thumb;
    }

    public void setGoods_thumb(String goods_thumb) {
        this.goods_thumb = goods_thumb;
    }

    public String getFapiao() {
        return fapiao;
    }

    public void setFapiao(String fapiao) {
        this.fapiao = fapiao;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getGoodssn() {
        return goodssn;
    }

    public void setGoodssn(String goodssn) {
        this.goodssn = goodssn;
    }

    public String getGoods_brief() {
        return goods_brief;
    }

    public void setGoods_brief(String goods_brief) {
        this.goods_brief = goods_brief;
    }

    public String getFangcha() {
        if (fangcha.equals("") || fangcha == null) {
            fangcha = "0";
        }
        return fangcha;
    }

    public void setFangcha(String fangcha) {
        this.fangcha = fangcha;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getGoods_appname() {
        return goods_appname;
    }

    public void setGoods_aaname(String goods_appname) {
        this.goods_appname = goods_appname;
    }

    public String getGoods_fysm() {
        return goods_fysm;
    }

    public void setGoods_fysm(String goods_fysm) {
        this.goods_fysm = goods_fysm;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getGoods_tggz() {
        return goods_tggz;
    }

    public void setGoods_tggz(String goods_tggz) {
        this.goods_tggz = goods_tggz;
    }

    public String getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(String goodsid) {
        this.goodsid = goodsid;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Cyrp> getRili() {
        return rili;
    }

    public void setRili(List<Cyrp> rili) {
        this.rili = rili;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
