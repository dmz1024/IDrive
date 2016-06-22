package com.ccpress.izijia.util;

import android.os.Build;
import com.trs.util.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Wu Jingyu
 * Date: 2015/10/12
 * Time: 10:35
 */

public class HttpPostUtil extends AsyncTask<String, Integer, String>{
    private static final String BOUNDARY = "--------DSWFQOIHVQQV712SDAF12";
    private static final String HTTP_BODY_SEPARATE_BOUNDARY = "--" + BOUNDARY + "\r\n";
    private static final String HTTP_BODY_END_BOUNDARY = "--" + BOUNDARY + "--" + "\r\n" + "\r\n";

    private URL url;
    private HttpURLConnection conn;
    private Map<String, String> textParams = new HashMap<String, String>();
    private Map<String, File> fileparams = new HashMap<String, File>();
    private DataOutputStream ds;
    private long totalBodyLength = 0;
    private long totalFileLength = 0;
    private PostCallback mCallback;

    public HttpPostUtil(String url) throws Exception {
        this.url = new URL(url);
    }

    //重新设置要请求的服务器地址，即上传文件的地址。
    public void setUrl(String url) throws Exception {
        this.url = new URL(url);
    }

    //增加一个普通字符串数据到form表单数据中
    public void addTextParameter(String name, String value) {
        textParams.put(name, value);
    }

    //增加一个文件到form表单数据中
    public void addFileParameter(String name, File value) {
        fileparams.put(name, value);
    }

    // 清空所有已添加的form表单数据
    public void clearAllParameters() {
        textParams.clear();
        fileparams.clear();
    }

    //文件上传的connection的一些必须设置
    private void initConnection() throws Exception {
        conn = (HttpURLConnection) this.url.openConnection();
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        if(!Build.BRAND.equals("samsung") && !Build.MANUFACTURER.equals("samsung")) {
            conn.setChunkedStreamingMode(0);
        }

        conn.setConnectTimeout(20 * 1000); //连接超时为20秒
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + BOUNDARY);
    }

    // 发送数据到服务器，返回一个字节包含服务器的返回结果的数组
    public String send() throws Exception {
        initConnection();
        try {
            conn.connect();
        } catch (SocketTimeoutException e) {
            // something
            throw new RuntimeException();
        }
        ds = new DataOutputStream(conn.getOutputStream());
        writeFileParams();
        writeStringParams();
        paramsEnd();
        InputStream in = conn.getInputStream();
        ds.flush();
        ds.close();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            out.write(b);
        }
        conn.disconnect();
        String result = new String(out.toByteArray());
        out.flush();
        out.close();
        return result;
    }

    //普通字符串数据
    private void writeStringParams() throws Exception {
        Set<String> keySet = textParams.keySet();
        for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
            String name = it.next();
            String value = textParams.get(name);
            ds.writeBytes(HTTP_BODY_SEPARATE_BOUNDARY);
            ds.writeBytes("Content-Disposition: form-data; name=\"" + name
                    + "\"\r\n");
            ds.writeBytes("\r\n");
//            ds.writeBytes(encode(value) + "\r\n");  //对value进行编码
            ds.write(value.getBytes());
            ds.writeBytes("\r\n");
        }
    }

    //文件数据
    private void writeFileParams() throws Exception {
        Set<String> keySet = fileparams.keySet();
        int current_write = 0;
        for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
            String name = it.next();
            File value = fileparams.get(name);
            ds.writeBytes(HTTP_BODY_SEPARATE_BOUNDARY);
//            ds.writeBytes("Content-Disposition: form-data; name=\"" + name
//                    + "\"; filename=\"" + encode(value.getName()) + "\"\r\n"); //进行编码
            String tmp = "Content-Disposition: form-data; name=\"" + name
                    + "\"; filename=\"" + value.getName() + "\"\r\n";
            ds.write(tmp.getBytes());
            ds.writeBytes("Content-Type: " + getContentType(value) + "\r\n");
            ds.writeBytes("\r\n");
            //将文件写入ds
            FileInputStream in = new FileInputStream(value);
            byte[] b = new byte[1024];
            int n;
            while ((n = in.read(b)) != -1) {
                ds.write(b, 0, n);
                current_write += n;
                double totalFileLength_f = totalFileLength;
//                double percent_long = current_write/totalFileLength_f;
                int percent =(int) (current_write / totalFileLength_f * 100);
                publishProgress(percent); //发布进度数据
            }
            in.close();

            ds.writeBytes("\r\n");
        }
    }

    //获取文件的上传类型，图片格式为image/png,image/jpg等。非图片为application/octet-stream
    private String getContentType(File f) {
        String fileType = FileTypeUtil.getFileType(f.getAbsolutePath());

        if(fileType==null||fileType.equals("")){
            return "application/octet-stream";
        } else {
            return "image/" + fileType;
        }
    }

    //添加结尾数据
    private void paramsEnd() throws Exception {
//        ds.writeBytes("--" + boundary + "--" + "\r\n");
//        ds.writeBytes("\r\n");
        ds.writeBytes(HTTP_BODY_END_BOUNDARY);
    }

    // 对包含中文的字符串进行转码，此为UTF-8。服务器那边要进行一次解码
    private String encode(String value) throws Exception {
        return URLEncoder.encode(value, "UTF-8");
    }

    @Override
    protected void onPreExecute() {
        if(mCallback!=null){
            mCallback.onProgressStart();
        }
        countTotalFileLength();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return send();
        } catch (Exception e) {
            e.printStackTrace();
            if(mCallback!=null){
                mCallback.onProgressEnd("{\"code\": 1, \"message\": \"连接超时\"}");
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if(mCallback!=null){
            mCallback.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if(mCallback!=null){
            mCallback.onProgressEnd(s);
        }
    }

    private long countTotalFileLength() {
        Set<String> keySet = fileparams.keySet();
        for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
            String name = it.next();
            File value = fileparams.get(name);
            totalFileLength += value.length();
        }

        return totalFileLength;
    }

    private long countBodyLength() {
        //上传文件部分总长度
        Set<String> keySet = fileparams.keySet();
        for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
            String name = it.next();
            File value = fileparams.get(name);
            totalBodyLength += HTTP_BODY_SEPARATE_BOUNDARY.getBytes().length;
            String tmp1 = "Content-Disposition: form-data; name=\"" + name
                    + "\"; filename=\"" + value.getName() + "\r\n";
            totalBodyLength += tmp1.getBytes().length;
            //三个\r\n最后一个应该出现在上传文件写入之后，但这里只计算长度还没开始写入网络流上传所以与顺序无关
            String tmp2 = "Content-Type: " + getContentType(value) + "\r\n" + "\r\n" + "\r\n";
            totalBodyLength += tmp2.getBytes().length;
            totalBodyLength += countTotalFileLength();
        }

        //上传参数部分总长度
        Set<String> keySets = textParams.keySet();
        for (Iterator<String> it = keySets.iterator(); it.hasNext(); ) {
            String name = it.next();
            String value = textParams.get(name);
            totalBodyLength += HTTP_BODY_SEPARATE_BOUNDARY.getBytes().length;
            //三个\r\n最后一个应该出现在上传参数写入之后，但这里只计算长度还没开始写入网络流上传所以与顺序无关
            String tmp1 = "Content-Disposition: form-data; name=\"" + name + "\r\n" + "\r\n" + "\r\n";
            totalBodyLength += tmp1.getBytes().length;
            totalBodyLength += value.getBytes().length;
        }

        //结尾部分长度
        totalBodyLength += HTTP_BODY_END_BOUNDARY.getBytes().length;

        return totalBodyLength;
    }

    public interface PostCallback {
        void onProgressStart();
        void onProgressUpdate(Integer i);
        void onProgressEnd(String s);
    }

    public void setPostCallback(PostCallback callback) {
        this.mCallback = callback;
    }
}
