package com.trs.media.upload.uploadvideo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import com.trs.mobile.R;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wbq on 2014/8/21.
 */
public class UploadVideoActivity extends Activity{
    private Context mContext = this;
    public static String FILE_PATH = "/sdcard/TestVideo/";
    private String fileName;
    private static String URL = "http://192.168.1.110:8082/MyApplicationServer/MainServer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.uploadvideomain);

        ((Button)findViewById(R.id.uploadId)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeVideo2();
            }
        });
    }

    private void takeVideo2(){
        Intent intent = new Intent(mContext,TestBasicVideo.class);
        SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss");
        File file;
        do {
            File fileFolder = new File(FILE_PATH);
            if(!fileFolder.isDirectory()){
                fileFolder.mkdirs();
            }
            fileName = FILE_PATH + sdf.format(new Date()) + "_" +
                    (int)((Math.random()*900)+100) + ".mp4";
            file = new File(fileName);
        }while (file.exists());
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(TestBasicVideo.EXTRA_PATH,fileName);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 101){
            return;
        }
        File file = new File(fileName);
        if(file.exists() && file.length() != 0){
            upload(file);
        }
    }


    /**
     * 上传文件
     * @param file 需要上传的文件
     * @return 上传是否成功
     */
    private boolean upload(File file){
        try {
            String end = "\r\n";
            String hyphens = "--";
            String boundary = "*****";
            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			/* 允许使用输入流，输出流，不允许使用缓存*/
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
			/* 请求方式*/
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

			/* 当文件不为空，把文件包装并且上传*/
            if(file != null){
                DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
				/* name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的   比如:abc.png*/
                ds.writeBytes(hyphens + boundary + end);
                ds.writeBytes("Content-Disposition: form-data; " + "name=\"file1\";filename=\"" +
                        file.getName() +"\"" + end);
                ds.writeBytes(end);

                InputStream input = new FileInputStream(file);
                int size = 1024;
                byte[] buffer = new byte[size];
                int length = -1;
				/* 从文件读取数据至缓冲区*/
                while((length = input.read(buffer)) != -1){
                    ds.write(buffer, 0, length);
                }
                input.close();
                ds.writeBytes(end);
                ds.writeBytes(hyphens + boundary + hyphens + end);
                ds.flush();

				/* 获取响应码*/
                if(conn.getResponseCode() == 200){
                    return true;
                }
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}

