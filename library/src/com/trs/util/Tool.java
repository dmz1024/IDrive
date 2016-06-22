/*
 *	History				Who				What
 *  2012-3-21			Administrator			Created.
 */

package com.trs.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

public class Tool {

    public static boolean checkNetWork(Context context) {
        boolean newWorkOK = false;
        ConnectivityManager connectManager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectManager.getActiveNetworkInfo() != null) {
            newWorkOK = true;
        }
        return newWorkOK;
    }

    public static String getSpecialImageUrl(String _sOriginImageUrl, String _sImageSize) {
        if (StringUtil.isEmpty(_sOriginImageUrl)) {
            return "";
        }
        if (StringUtil.isEmpty(_sImageSize)) {
            return _sOriginImageUrl;
        }

        int nEnd = _sOriginImageUrl.lastIndexOf(".");
        String sTemp = _sOriginImageUrl.substring(0, nEnd);
        String sExtra = _sOriginImageUrl.substring(nEnd);
        return sTemp + _sImageSize + sExtra;
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    /**
     * 工具栏 url 转换为bitmap
     * 
     * @param uriPic
     * @return
     */
    public static Bitmap uRl2Bitmap(String uriPic) {
        URL imageUrl = null;
        Bitmap bitmap = null;
        try {
            /* new URL对象将网址传入 */
            imageUrl = new URL(uriPic);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            /* 取得联机 */
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.connect();
            /* 取得回传的InputStream */
            InputStream is = conn.getInputStream();
            /* 将InputStream变成Bitmap */
            bitmap = BitmapFactory.decodeStream(is);
            /* 关闭InputStream */
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getScaleBitmap(Bitmap srcBitmap, int dstWidth, int dstHeight) {
        if (srcBitmap == null) {
            return null;
        }
        int bitmapWidth = srcBitmap.getWidth();
        int bitmapHeight = srcBitmap.getHeight();
        float scaleWidth = (float)dstWidth / bitmapWidth;
        float scaleHeight = (float)dstHeight / bitmapHeight;
        float scale;
        if (scaleWidth > scaleHeight) {
            scale = scaleWidth;
        } else {
            scale = scaleHeight;
        }
        Matrix m = new Matrix();
        m.postScale(scale, scale);
        Bitmap bp = Bitmap.createBitmap(srcBitmap, 0, 0, bitmapWidth, bitmapHeight, m, true);
        Bitmap finallBp = Bitmap.createBitmap(bp, 0, 0, dstWidth, dstHeight, null, false);
        if (!bp.isRecycled()) {
            bp.recycle();
            System.gc();
        }
        return finallBp;
    }

    public static String getPageUrl(String _url, int _pageIndex) {
        int position = _url.lastIndexOf("/");
        String preOfUrl = _url.substring(0, position);
        String fileName = "documents_" + _pageIndex + ".json";
        return preOfUrl + "/" + fileName;
    }

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month + 1, day, hour, minute,
				second);
    }

    /** 得到当前的年.月.日 */
    public static String getCurrentDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        return format.format(date);
    }

    /** 字节转换成kb Mb Gb */
    public static String byteFormat(long _byte) {
        if ((_byte >> 10) < 1) {
            return _byte + "b";
        } else if ((_byte >> 20) < 1) {
            return String.format("%.2f", (float) _byte / (1024)) + "K";
        } else if ((_byte >> 30) < 1) {
            return String.format("%.2f", (float) _byte / (1024 * 1024)) + "M";
        }
        return String.format("%.2f", (float) _byte / (1024 * 1024 * 1024)) + "G";
    }

    /** 0代表星期一，6代表星期天 */
    public static int getCurrentWeekDay() {
        Date date = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int index = calendar.get(Calendar.DAY_OF_WEEK) - 2; // Java默认从星期天开始
        if (index == -1) {
            index = 6;
        }
        return index;
    }

    /** 从00：00到现在的分钟 */
    public static int getCurrentMinuteInDay() {
        Date date = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int mimute = calendar.get(Calendar.MINUTE);
        return hour * 60 + mimute;

    }

    /** 将hh：mm转化成分钟 */
    public static int getTotalMinute(String _time) {
        String[] array = _time.trim().split(":");
        int hour = Integer.parseInt(array[0]);
        int mimute = Integer.parseInt(array[1]);
        return hour * 60 + mimute;
    }

    /** _time以毫秒为单位 ，转换成hh：mm：ss的形式 */
    public static String translateLongToTime(long _time) {
        _time = _time / 1000; // 转换成秒
        if (_time < 60) {
            return "00:" + String.format("%02d", _time);
        }
        long temp = _time / 60;
        if (temp < 60) {
            return String.format("%02d", temp) + ":" + String.format("%02d", _time % 60);
        }
        return String.format("%02d", temp / 60) + ":" + String.format("%02d", temp % 60) + ":"
                + String.format("%02d", _time % 60);
    }

    /** 判断当前使用的语言类型 */
    public static boolean isChinese(Context context) {
        Resources resource = context.getResources();
        Configuration curConf = resource.getConfiguration();
        String languageType = curConf.locale.getLanguage();
        if (languageType.equals("bo")) {
            return false;
        }
        return true;
    }

    /** 获取本地文件大小 -1表示不存在 */
    public static long getLocalFileSize(String _filePath) {
        File file = new File(_filePath);
        if (!file.exists()) {
            return -1;
        }

        long size = 0L;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            size = inputStream.available();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return size;
    }
}
