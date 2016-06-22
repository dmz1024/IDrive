/*
 *	History				Who				What
 *  2012-2-12			Administrator			Created.
 */
package com.trs.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Title: TRS 内容协作平台（TRS WCM） <BR>
 * Description: <BR>
 * TODO <BR>
 * Copyright: Copyright (c) 2004-2012 北京拓尔思信息技术股份有限公司 <BR>
 * Company: www.trs.com.cn <BR>
 * 
 * @author Administrator
 * @version 1.0
 */
public class StringUtil {
    /**
     * 判断指定字符串是否为空
     * 
     * @param string
     *            指定的字符串
     * @return 若字符串为空对象（_string==null）或空串（长度为0），则返回true；否则，返回false.
     */
    public static boolean isEmpty(String string) {
        return ((string == null) || (string.trim().length() == 0) || string.equals("null"));
    }

	public static boolean isWebPage(String url){
		if(url == null){
			return false;
		}

		url = url.toLowerCase();
		return url.contains(".jsp") ||
				url.contains(".php") ||
				url.contains(".html") ||
				url.contains(".do") ||
				url.contains("?");
	}

    /**
     * @author WLH
     * 2014-8-4下午4:51:22
     * 返回string里面的所有url
     */
    public static ArrayList<String> getUrlsFromString(String data) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }

        ArrayList<String> urls = new ArrayList<String>();
        Pattern pattern = Pattern
                .compile("(http://|https://){1}[\\w\\.\\-/:]+");
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            urls.add(matcher.group());
            System.out.println("url from json:" + matcher.group());
        }

        return urls;
    }

    public static boolean isImgUrl(String url){
        if(url.endsWith("jpg")|| url.endsWith("jpeg") || url.endsWith("png")|| url.endsWith("bmp")|| url.endsWith("gif")){
            return true;
        }
        return false;
    }
}
