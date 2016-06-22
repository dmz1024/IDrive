package com.ccpress.izijia.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Wu Jingyu
 * Date: 2015/5/8
 * Time: 17:36
 */
public class HanziToPinyinUtil {
    public static String getFullPinYin(String source) {
        if(source.contains("重庆")){
            return "CHONGQING";
        }
        if(source.contains("成都")){
            return "CHENGDU";
        }

        if (!Arrays.asList(Collator.getAvailableLocales()).contains(Locale.CHINA)) {
            return source;
        }
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(source);
        if (tokens == null || tokens.size() == 0) {
            return source;
        }
        StringBuilder result = new StringBuilder();
        for (HanziToPinyin.Token token : tokens) {
            if (token.type == HanziToPinyin.Token.PINYIN) {
                result.append(token.target);
            } else {
                result.append(token.source);
            }
        }
        return result.toString();
    }

    public static String getFirstPinYin(String source) {
        if(source.contains("重庆")){
            return "CQ";
        }
        if(source.contains("成都")){
            return "CD";
        }

        if (!Arrays.asList(Collator.getAvailableLocales()).contains(Locale.CHINA) &&
                !Arrays.asList(Collator.getAvailableLocales()).contains(Locale.CHINESE) &&
                !Arrays.asList(Collator.getAvailableLocales()).contains(Locale.SIMPLIFIED_CHINESE) &&
                !Arrays.asList(Collator.getAvailableLocales()).contains(Locale.TRADITIONAL_CHINESE)) {
            return source;
        }
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(source);
        if (tokens == null || tokens.size() == 0) {
            return source;
        }
        StringBuilder result = new StringBuilder();
        for (HanziToPinyin.Token token : tokens) {
            if (token.type == HanziToPinyin.Token.PINYIN) {
                result.append(token.target.charAt(0));
            } else {
                result.append("#");
            }
        }
        return result.toString().toUpperCase();
    }


    public static String getFullPinYinByPinyin4j(String source) {
        if(source.contains("重庆")){
            return "CHONGQING";
        }
        if(source.contains("成都")){
            return "CHENGDU";
        }

        StringBuffer pybf = new StringBuffer();
        char[] arr = source.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString();
    }

    public static String getFirstPinYinByPinyin4j(String source) {
        if(source.contains("重庆")){
            return "CQ";
        }
        if(source.contains("成都")){
            return "CD";
        }

        StringBuffer pybf = new StringBuffer();
        char[] arr = source.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (temp != null) {
                        pybf.append(temp[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim();
    }
}
