package com.ccpress.izijia.dfy.util;


import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dmz1024 on 2016/3/17.
 * LoadDataUtil类
 */
public class FileUtil {


    /**
     * 将数据写入到本地
     * @param url url地址
     * @param json 要保存的数据
     */
    public static void setData2Native(String url, String json) {

        FileOutputStream out = null;

        try {
            out = x.app().openFileOutput(Util.getMa5(url) + ".txt", x.app().MODE_PRIVATE);
            out.write(json.getBytes("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }



    /**
     * 读取本地存储的数据
     * @param url url地址
     * @return
     */
    public static String getDataFromNative(String url) {

        FileInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            byte[] b = new byte[1024];
            out = new ByteArrayOutputStream();
            int length = 0;
            in = x.app().openFileInput(Util.getMa5(url) + ".txt"); //获得输入流
            while ((length = in.read(b)) != -1) {
                out.write(b, 0, length);
            }
            byte[] content = out.toByteArray();
            String json = new String(content, "UTF-8");
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    /**
     * 获取到应用缓存路径
     * @return
     */
    public static String getCachePath(){
        return x.app().getCacheDir().getPath()+"/";
    }


}
