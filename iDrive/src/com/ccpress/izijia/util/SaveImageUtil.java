package com.ccpress.izijia.util;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.*;

/**
 * Created by WLH on 2015/5/12 17:02.
 */
public class SaveImageUtil {

    public static void saveImage(Context mContext, String url){
        String oldPath = ImageLoader.getInstance().getDiscCache().get(url).getPath();
        String name = getImageName(oldPath);
        String newPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + name  + ".jpg";

        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newFile = new File(newPath);
            if (oldfile.exists() && !newFile.exists()) {  //文件存在时
                InputStream is = new FileInputStream(oldPath);  //读入原文件
                OutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = is.read(buffer)) != -1) {
                    bytesum += byteread;  //字节数  文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                is.close();
            }
            Toast.makeText(mContext, "已保存当前图片至/sdcard/Download/" + getImageName(newPath) + ".jpg", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(mContext, "保存失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static String getImageName(String path) {
        return path.substring(path.lastIndexOf("/")+1);
    }
}
