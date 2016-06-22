package com.ccpress.izijia.yd.constant;

/**
 * Created by dengmingzhi on 16/5/25.
 */
public interface ConstantApi {
    //        String YD_URL = "http://camp.website-art.com/api/";
    String YD_URL = "http://biz.izijia.cn/camp/api/";
    String URL = "http://biz.izijia.cn/camp/";
    String CAMPOPTION = YD_URL + "campoptionandroid.php";//营地筛选列表
    String STORES = YD_URL + "stores.php";//营地列表
    String CART = YD_URL + "cart.php";
    String BAOXIAN = YD_URL + "baoxian.php";
    String ORDER = YD_URL + "order.php";
    String ORDER_LIST = YD_URL + "order_list.php";
    String YD_PAY_ZFB = URL + "alipay.php";
    String YD_PAY_YL = URL + "upopapp.php";
    String YD_UPOP_QUERY = URL + "upopquery.php";//银联支付结果upopquery.php
    String YD_ALIPAY_QUERY = URL + "alipayquery.php";//支付宝支付结果
    String OCENTENUSER = URL + "ocenteruser.php";//退款
    String ORDER_INFO = YD_URL + "order_info.php";//营地订单详情
    String LOCAL = YD_URL + "local.php";//当地人信息
    String COMMENT = YD_URL + "comment.php";//点评
    String CAMP_INFO = URL + "supplier.php?go=a&suppId=";//营地详情
    String CLIST = YD_URL + "clist.php";//营地详情
    String AD = YD_URL + "ad.php";

}
