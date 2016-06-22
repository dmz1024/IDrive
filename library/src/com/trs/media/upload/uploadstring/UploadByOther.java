package com.trs.media.upload.uploadstring;

import android.os.Environment;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

/**
 * Created by wbq on 14-8-7.
 */
public class UploadByOther {
    private static String IP = "http://192.168.1.110:8082";
    private static final String BOUNDARY = "---------------------------7db1c523809b2";//数据分割线

    public boolean uploadHttpURLConnection(String username, String password, String path) throws Exception {
        //找到sdcard上的文件
        File file = new File(Environment.getExternalStorageDirectory(), path);
        //仿Http协议发送数据方式进行拼接
        StringBuilder sb = new StringBuilder();
        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data; name=\"username\"" + "\r\n");
        sb.append("\r\n");
        sb.append(username + "\r\n");

        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data; name=\"password\"" + "\r\n");
        sb.append("\r\n");
        sb.append(password + "\r\n");

        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + path + "\"" + "\r\n");
        sb.append("Content-Type: image/pjpeg" + "\r\n");
        sb.append("\r\n");

        byte[] before = sb.toString().getBytes("UTF-8");
        byte[] after = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");

        URL url = new URL(IP + "/MyApplicationServer/MainServer");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        int Code = conn.getResponseCode();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        conn.setRequestProperty("Content-Length", String.valueOf(before.length + file.length() + after.length));
        conn.setRequestProperty("HOST", IP);
        conn.setDoOutput(true);

        OutputStream out = conn.getOutputStream();
        InputStream in = new FileInputStream(file);

        // 写 brfore文件
        out.write(before);

        // 写图片文件
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) != -1)
            out.write(buf, 0, len);

        // 写 after文件
        out.write(after);

        in.close();
        out.close();

        boolean returnValue = false;
        try{
            returnValue =  conn.getResponseCode() == 200;
        } catch (Exception e){
            e.printStackTrace();
        }

        return returnValue;
    }


    public boolean uploadBySocket(String username, String password, String path) throws Exception {
        // 根据path找到SDCard中的文件
        File file = new File(Environment.getExternalStorageDirectory(), path);
        // 组装表单字段和文件之前的数据
        StringBuilder sb = new StringBuilder();

        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data; name=\"username\"" + "\r\n");
        sb.append("\r\n");
        sb.append(username + "\r\n");

        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data; name=\"password\"" + "\r\n");
        sb.append("\r\n");
        sb.append(password + "\r\n");

        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + path + "\"" + "\r\n");
        sb.append("Content-Type: image/pjpeg" + "\r\n");
        sb.append("\r\n");

        // 文件之前的数据
        byte[] before = sb.toString().getBytes("UTF-8");
        // 文件之后的数据
        byte[] after = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");

        URL url = new URL(IP + "/MyApplicationServer/MainServer");

        // 由于HttpURLConnection中会缓存数据, 上传较大文件时会导致内存溢出, 所以我们使用Socket传输
        Socket socket = new Socket(url.getHost(), url.getPort());
        OutputStream out = socket.getOutputStream();
        PrintStream ps = new PrintStream(out, true, "UTF-8");

        // 写出请求头
        ps.println("POST /14_Web/servlet/LoginServlet HTTP/1.1");
        ps.println("Content-Type: multipart/form-data; boundary=" + BOUNDARY);
        ps.println("Content-Length: " + String.valueOf(before.length + file.length() + after.length));
        ps.println("Host: " + IP);

        InputStream in = new FileInputStream(file);

        // 写出数据
        out.write(before);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) != -1)
            out.write(buf, 0, len);

        out.write(after);

        in.close();
        out.close();

        return true;
    }
}
