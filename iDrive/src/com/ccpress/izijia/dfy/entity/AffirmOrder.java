package com.ccpress.izijia.dfy.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmz1024 on 2016/3/27.
 */
public class AffirmOrder implements Serializable {
    private String goods_name;
    private String brand_name;
    private String date;
    private String count;
    private boolean spell;
    private String linkman_name;
    private boolean linkman_sex;
    private String linkman_tel;
    private String linkman_eamil;
    private List<Tourist> listTourist=new ArrayList<Tourist>();
    private boolean isFp;
    private String fpTitle;
    private String fpDetail;
    private String fpAddress;
    private String total;
    private String fpAddressee;//发票收件人
    private String fpTel;
    private String peoCount;//人数 ：1成人2儿童

    public String getPeoCount() {
        return peoCount;
    }

    public void setPeoCount(String peoCount) {
        this.peoCount = peoCount;
    }

    public boolean isLinkman_sex() {
        return linkman_sex;
    }

    public String getFpAddressee() {
        return fpAddressee;
    }

    public void setFpAddressee(String fpAddressee) {
        this.fpAddressee = fpAddressee;
    }

    public String getFpTel() {
        return fpTel;
    }

    public void setFpTel(String fpTel) {
        this.fpTel = fpTel;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public boolean isSpell() {
        return spell;
    }

    public void setSpell(boolean spell) {
        this.spell = spell;
    }

    public String getLinkman_name() {
        return linkman_name;
    }

    public void setLinkman_name(String linkman_name) {
        this.linkman_name = linkman_name;
    }

    public boolean getLinkman_sex() {
        return linkman_sex;
    }

    public void setLinkman_sex(boolean linkman_sex) {
        this.linkman_sex = linkman_sex;
    }

    public String getLinkman_tel() {
        return linkman_tel;
    }

    public void setLinkman_tel(String linkman_tel) {
        this.linkman_tel = linkman_tel;
    }

    public String getLinkman_eamil() {
        return linkman_eamil;
    }

    public void setLinkman_eamil(String linkman_eamil) {
        this.linkman_eamil = linkman_eamil;
    }

    public List<Tourist> getListTourist() {
        return listTourist;
    }

    public void setListTourist(List<Tourist> listTourist) {
        this.listTourist = listTourist;
    }

    public boolean isFp() {
        return isFp;
    }

    public void setFp(boolean isFp) {
        this.isFp = isFp;
    }

    public String getFpTitle() {
        return fpTitle;
    }

    public void setFpTitle(String fpTitle) {
        this.fpTitle = fpTitle;
    }

    public String getFpDetail() {
        return fpDetail;
    }

    public void setFpDetail(String fpDetail) {
        this.fpDetail = fpDetail;
    }

    public String getFpAddress() {
        return fpAddress;
    }

    public void setFpAddress(String fpAddress) {
        this.fpAddress = fpAddress;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }


}
