package com.trs.media.upload.uploadpicture;

/**
 * Created by wbq on 2014/8/20.
 */
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.trs.util.AsyncTask;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 *
 * 上传工具类
 */
public class UploadUtil {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10*1000; //超时时间
    private static final String CHARSET = "utf-8"; //设置编码

    private int mTransferred ;

    private File file;
    private String RequestURL;
    private Handler handler;
    private boolean isUploading = true;

    class task extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成
            String PREFIX = "--", LINE_END = "\r\n";
            String CONTENT_TYPE = "multipart/form-data"; //内容类型
            try {
                URL url = new URL(RequestURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(TIME_OUT);
                conn.setConnectTimeout(TIME_OUT);
                conn.setDoInput(true); //允许输入流
                conn.setDoOutput(true); //允许输出流
                conn.setUseCaches(false); //不允许使用缓存
                conn.setRequestMethod("POST"); //请求方式
                conn.setRequestProperty("Charset", CHARSET); //设置编码
                conn.setRequestProperty("connection", "keep-alive");
                conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
                if (file != null){
                    /**
                     * 当文件不为空，把文件包装并且上传
                     */
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    StringBuffer sb = new StringBuffer();
                    sb.append(PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(LINE_END);
                    /**
                     * 这里重点注意：
                     * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                     * filename是文件的名字，包含后缀名的 比如:abc.png
                     */
                    sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + file.getName() + "\"" + LINE_END);

                    sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);

                    sb.append(LINE_END);
                    dos.write(sb.toString().getBytes());

                    RandomAccessFile is = new RandomAccessFile(file,"r");
                    Log.e("seek",mSeek + "");
                    Log.e("seek1",Integer.valueOf(mSeek) + "");
                    is.seek(Integer.valueOf(mSeek));

                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while (isUploading && ((len = is.read(bytes)) != -1)){
                        dos.write(bytes, 0, len);
                        // 传回进度,更新UI
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mTransferred += len;
                        Log.e("read",mTransferred+"");
                        Message msg = new Message();
                        msg.arg1 = (int) ((mTransferred / (float) file.length()) * 100);

                        Bundle bundle = new Bundle();
                        bundle.putInt("seek",mTransferred);
                        msg.setData(bundle);

                        handler.sendMessage(msg);
                    }
                    is.close();
                    dos.write(LINE_END.getBytes());

                    byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();

                    dos.write(end_data);
                    dos.flush();
                    /**
                     * 获取响应码 200=成功
                     * 当响应成功，获取响应的流
                     */
                    int res = conn.getResponseCode();
                    Log.e(TAG, "response code:" + res);
                    // if(res==200)
                    // {
                    Log.e(TAG, "request success");

                    InputStream input = conn.getInputStream();
                    StringBuffer sb1 = new StringBuffer();

                    int ss;
                    while ((ss = input.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    result = sb1.toString();
                    Log.e(TAG, "result : " + result);
                }
            } catch (MalformedURLException e) {
                Log.e("Exception","MalformedURLException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Exception","IOException");
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    };

    /**
     * android上传文件到服务器
     * @param file 需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
     public void uploadFile(File file,String RequestURL,Handler handler)
     {
         this.file = file;
         this.RequestURL = RequestURL;
         this.handler = handler;

         task task = new task();
         task.execute();
     }

    public void pauseUpload(){
        isUploading = false;
    }

    public void playUpload(int seek){
        this.mSeek = seek;
        isUploading = true;

        task task = new task();
        task.execute();
    }

    private int mSeek;
}
