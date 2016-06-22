package com.trs.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wubingqian on 14-4-2.
 */
public class RegularExpression {

    /**
     * true 仅有大小写字母和数字
     * false 含有非大小写字母和数字的字符
     */
    public static boolean isAZ09(String key){
        if(key == null||key.length() == 0){
            return false;
        }
        boolean flag = false;
        String reg = "[^a-zA-Z0-9]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(key.trim());
        return !matcher.find();
    }

    /**
     * 公交查询字段
     * true 仅含 (字母大小写 数字 '游' '路')
     * false 含有其他
     */
    public static boolean trafficRote(String key){
        if(key == null||key.length() == 0){
            return false;
        }
        boolean flag = false;
        String reg = "[^a-zA-Z0-9\\u6e38\\u8def]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(key.trim());
        return !matcher.find();
    }

    /**
     * false 含有 ~!@#$%^&*(){}<> 其中某个字符
     * true 不含
     */
    public static boolean isNormalCharacter(String key){
        if(key == null||key.length() == 0){
            return false;
        }
        boolean flag = false;
        String reg = "[~!@#$%^&*(){}<>]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(key.trim());
        return !matcher.find();
    }

    /**
     * 删除字符换中 '[' ; ']' ; '"' 字符
     *
     */
    public static String delCharacter(String key){
        String reg = "[\"\\[\\]]";
        Pattern pattern = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(key.trim());
        return matcher.replaceAll("");
    }

    /**
     * 去掉回车换行符号
     */
    public static String delEnder(String key){
        String reg = "\n";
        Pattern pattern = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(key.trim());
        return matcher.replaceAll("");
    }

    /**
     * 保留key中的数字
     *
     */
    public static String obtainNumber(String key){
        String reg = "[^0-9]";
        Pattern pattern = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(key.trim());
        return matcher.replaceAll("");
    }

    /**
     * true 正确emial
     * false 错误
     */
    public static boolean isRightEmail(String key){
        if(key == null||key.length() == 0){
            return false;
        }
        String reg = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(key.trim());
        return matcher.find();
    }

    /**
     * true 仅含有数字
     * false 有非数字
     */
    public static boolean isNumber(String key){
        if(key == null||key.length() == 0){
            return false;
        }
        String reg = "[^0-9]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(key.trim());
        return !matcher.find();
    }

    /**
     * 去掉HTML标签
     */
    public static String delHTMLTag(String htmlStr){
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
        String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
        String regEx_space = "\\s*|\t|\r|\n";//定义空格回车换行符

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签

        Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll(""); // 过滤空格回车标签
        return htmlStr.trim(); // 返回文本字符串
    }
}
