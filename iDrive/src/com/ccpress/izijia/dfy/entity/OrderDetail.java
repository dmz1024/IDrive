package com.ccpress.izijia.dfy.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by administror on 2016/4/5 0005.
 */
public class OrderDetail implements Serializable {
    private String goods_name;
    private lxr lxr;
    private orderxq orderxq;
    private feiyong feiyong;
    private List<tourist> youke;
    private int result;
    private fp fp;

    public OrderDetail.fp getFp() {
        return fp;
    }

    public void setFp(OrderDetail.fp fp) {
        this.fp = fp;
    }

    public class fp{
        private String inv_payee;
        private String inv_content;
        private String inv_address;
        private String inv_name;
        private String inv_mobile;
        private String need_inv;

        public String getNeed_inv() {
            return need_inv;
        }

        public void setNeed_inv(String need_inv) {
            this.need_inv = need_inv;
        }

        public String getInv_payee() {
            return inv_payee;
        }

        public void setInv_payee(String inv_payee) {
            this.inv_payee = inv_payee;
        }

        public String getInv_content() {
            return inv_content;
        }

        public void setInv_content(String inv_content) {
            this.inv_content = inv_content;
        }

        public String getInv_address() {
            return inv_address;
        }

        public void setInv_address(String inv_address) {
            this.inv_address = inv_address;
        }

        public String getInv_name() {
            return inv_name;
        }

        public void setInv_name(String inv_name) {
            this.inv_name = inv_name;
        }

        public String getInv_mobile() {
            return inv_mobile;
        }

        public void setInv_mobile(String inv_mobile) {
            this.inv_mobile = inv_mobile;
        }
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public OrderDetail.lxr getLxr() {
        return lxr;
    }

    public void setLxr(OrderDetail.lxr lxr) {
        this.lxr = lxr;
    }

    public OrderDetail.orderxq getOrderxq() {
        return orderxq;
    }

    public void setOrderxq(OrderDetail.orderxq orderxq) {
        this.orderxq = orderxq;
    }

    public OrderDetail.feiyong getFeiyong() {
        return feiyong;
    }

    public void setFeiyong(OrderDetail.feiyong feiyong) {
        this.feiyong = feiyong;
    }

    public List<tourist> getYouke() {
        return youke;
    }

    public void setYouke(List<tourist> youke) {
        this.youke = youke;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public class tourist {

        private String leixing;
        private String mobile;
        private String name;
        private String sex;
        private String zjnum;

        public String getLeixing() {
            return leixing;
        }

        public void setLeixing(String leixing) {
            this.leixing = leixing;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getZjnum() {
            return zjnum;
        }

        public void setZjnum(String zjnum) {
            this.zjnum = zjnum;
        }
    }

    public class orderxq {
        private String backrq;
        private String city;
        private String crnum;
        private String cyrq;
        private String etnum;
        private String fangcha;
        private String fangjian;
        private String order_sn;
        private String status;
        private String add_time;
        private String pay_time;

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public String getPay_time() {
            return pay_time;
        }

        public void setPay_time(String pay_time) {
            this.pay_time = pay_time;
        }

        public String getBackrq() {
            return backrq;
        }

        public void setBackrq(String backrq) {
            this.backrq = backrq;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCrnum() {
            return crnum;
        }

        public void setCrnum(String crnum) {
            this.crnum = crnum;
        }

        public String getCyrq() {
            return cyrq;
        }

        public void setCyrq(String cyrq) {
            this.cyrq = cyrq;
        }

        public String getEtnum() {
            return etnum;
        }

        public void setEtnum(String etnum) {
            this.etnum = etnum;
        }

        public String getFangcha() {
            return fangcha;
        }

        public void setFangcha(String fangcha) {
            this.fangcha = fangcha;
        }

        public String getFangjian() {
            return fangjian;
        }

        public void setFangjian(String fangjian) {
            this.fangjian = fangjian;
        }

        public String getOrder_sn() {
            return order_sn;
        }

        public void setOrder_sn(String order_sn) {
            this.order_sn = order_sn;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public class feiyong {
        private String cytf;
        private String money_paid;
        private String order_amount;

        public String getCytf() {
            return cytf;
        }

        public void setCytf(String cytf) {
            this.cytf = cytf;
        }

        public String getMoney_paid() {
            return money_paid;
        }

        public void setMoney_paid(String money_paid) {
            this.money_paid = money_paid;
        }

        public String getOrder_amount() {
            return order_amount;
        }

        public void setOrder_amount(String order_amount) {
            this.order_amount = order_amount;
        }
    }

    public class lxr {
        private String email;
        private String mobile;
        private String name;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}
