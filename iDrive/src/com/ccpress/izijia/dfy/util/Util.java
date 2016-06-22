package com.ccpress.izijia.dfy.util;


import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.vo.UserVo;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.utils.SpUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.xutils.x;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by administror on 2016/3/18 0018.
 * 常用的util方法
 */
public class Util {


    /**
     * 获取应用的上下文对象，尽量使用该方法获取上下文，防止context被长时间占用
     *
     * @return
     */
    public static Context getMyApplication() {
        return x.app();
    }


    //用md5将url生成32位字符串
    public static String getMa5(String url) {
        return DigestUtils.md5Hex(url.getBytes());
//        return MD5.md5(url);
    }


    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }


    /**
     * 已弃用，项目需求变更
     * 将获取到的数据进行分页显示
     *
     * @param listAll
     * @param current
     * @param <T>
     * @return
     */
    public static <T> List<T> getList(List<T> listAll, int current) {

        if (listAll != null) {

            int count = listAll.size() / 10;
            int remain = listAll.size() % 10;

            if (current - count >= 1) {
                return null;
            }

            if ((current + 1) > count) {
                return listAll.subList(current * 10, current * 10 + remain);
            }

            return listAll.subList(current * 10, (current + 1) * 10);
        }
        return null;
    }


    /**
     * 拼接url，用于生成保存文件地址
     *
     * @param map
     * @param url
     * @param <T>
     * @return
     */
    public static <T> String getSaveUrl(Map<String, T> map, String url) {
        int i = 0;
        String saveUrl = url;
        if (map != null) {
            for (String key : map.keySet()) {
                i += 1;
                saveUrl = saveUrl + "?" + key + "=" + map.get(key);
                if (i != map.size()) {
                    saveUrl = saveUrl + "&";
                }
            }
        }
        return saveUrl;
    }

    /**
     * 获取ColorStateList
     *
     * @param colorId
     * @return
     */
    public static ColorStateList getColor(int colorId) {
        return getMyApplication().getResources().getColorStateList(colorId);
    }

    /**
     * 将资源id转换成字符串
     *
     * @param sId
     * @return
     */
    public static String getStringBySid(int sId) {
        return getMyApplication().getResources().getString(sId);
    }

    /**
     * 获取用户登录成功之后返回的信息
     *
     * @return
     */
    public static UserVo getUserInfo() {
        SpUtil sp = new SpUtil(getMyApplication());
        UserVo vo = new UserVo();
        vo.setAuth(sp.getStringValue(Const.AUTH));
        vo.setUid(sp.getStringValue(Const.UID));
        vo.setUserName(sp.getStringValue(Const.USERNAME));
        vo.setUserPhoto(sp.getStringValue(Const.AVATAR));
        return vo;
    }

    public static int getWeight() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = getMyApplication().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
//        int screenHeight = dm.heightPixels; // 屏幕高（像素，如：800px）
        return screenWidth;
    }

    public static float getDensity() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = getMyApplication().getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 身份证正则表达式验证
     */
//    private boolean isCard(string s_aStr) {
//
//        Regex reg15 = new Regex( @ "^[1-9]\d{7}((0\[1-9])|(1[0-2]))(([0\[1-9]|1\d|2\d])|3[0-1])\d{2}([0-9]|x|X){1}$");
//        Regex reg18 = new Regex( @"^[1-9]\d{5}[1-9]\d{3}((0\[1-9]))|((1[0-2]))(([0\[1-9]|1\d|2\d])|3[0-1])\d{3}([0-9]|x|X){1}$");
//
//        if (reg15.IsMatch(s_aStr) || reg18.IsMatch(s_aStr)) {
//            return true;
//        }
//        return false;
//    }


}
