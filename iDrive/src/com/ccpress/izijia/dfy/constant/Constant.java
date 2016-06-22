package com.ccpress.izijia.dfy.constant;

import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.util.Util;

/**
 * Created by dmz1024 on 2016/3/17.
 */
public interface Constant {
    /**
     * 东方盈：url
     */
    String DFY = "http://biz.izijia.cn/tour/";
    String DFY_URL = DFY + "api/";
    String DFY_CATEGORY = DFY_URL + "category.php";
    String DFY_SEARCH = DFY_URL + "search.php";
    String DFY_PRODUCT = DFY_URL + "product.php";
    String DFY_GOODSINFO = DFY_URL + "goodsinfo.php";
    String DFYDFY_GOOD_A = DFY + "goods_a.php?id=";
    String DFYDFY_GOOD = DFY + "goods.php?id=";
    String DFY_ORDER = DFY_URL + "order.php";
    String DFY_ORDER_LIST = DFY_URL + "order_list.php";
    String DFY_COLLECT = "http://member.izijia.cn/index.php?s=/interaction/service/addFav";
    String DFY_PAY_ZFB = DFY + "alipay.php";
    String DFY_PAY_YL = DFY + "upopapp.php";
    String DFY_COLLECT_DEL = "http://member.izijia.cn//index.php?s=/favorite/app/delFav";
    String DFY_ORDERINFO = DFY_URL + "orderinfo.php";
    String DFY_EVALUTE = DFY_URL + "comment.php";
    String DFY_TIME = DFY_URL + "time.php";//获取服务器时间戳
    String DFY_ALIPAY_QUERY = DFY + "alipayquery.php";//支付宝支付结果
    String DFY_UPOP_QUERY = DFY + "upopquery.php";//银联支付结果upopquery.php
    String DFY_ISCOLLECT = "http://member.izijia.cn/index.php?s=/Favorite/app/findFav";//判断是否被收藏
    String DFY_NOLOGIN_REG_CODE = "http://member.izijia.cn/index.php?s=/Ucenter/verify/mobileVerify";//获取验证码
    String DFY_NOLOGIN_REG = "http://member.izijia.cn/index.php?s=/Ucenter/app/mobileLogin";//通过验证码登录
    String DFY_BRAND_INFO = DFY_URL + "brandinfo.php";//领队详情
    String DFY_BRAND_GOODS = DFY_URL + "category.php";//领队商品
    String DFY_AD = DFY_URL + "ad.php";//获取首页轮播图url




    /**
     * 请求码和返回码
     */

    int DFY_DETAIL2LOGIN_REQ = 0x100;//详情页跳到登录页的请求码
    int DFY_LOGIN2DETAIL_RES = 0x110;//登录页返回到详情页的返回码

    /**
     * 标识
     */
    String DFY_IS_FROM_DFY = "from_dfy";//从自驾游跳转到登录界面的话不登录预订的按钮将会显示
    String DFY_IS_FROM_Yd= "from_yd";

    /**
     * 数据
     */
    String[] DAYS = {Util.getMyApplication().getString(R.string.azonic), "1-3", "3-7", "7-10", "10-15", "15+"};
    String[] JIERI = {Util.getMyApplication().getString(R.string.azonic), "周末", "春节", "清明", "五一", "端午", "中秋", "国庆",
            "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};

}
