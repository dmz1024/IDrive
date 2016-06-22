package com.trs.media.upload.Test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import com.trs.mobile.R;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wbq on 14-8-4.
 */
public class UploadTestActivity extends Activity{
    private File file;
    private Handler handler;
    private static final String TAG="MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.i(TAG, "onCreate");

        file = new File(Environment.getExternalStorageDirectory(), "crop.png");
        Log.i(TAG, "照片文件是否存在："+file);
        handler = new Handler();
        handler.post(runnable);
    }

    Runnable runnable=new Runnable() {

        public void run() {
            Log.i(TAG, "runnable run");
            uploadFile(file);
            handler.postDelayed(runnable, 5000);
        }

    };

    /**
     * 上传图片到服务器
     *
     * @param imageFile 包含路径
     */
    public void uploadFile(File imageFile) {
        Log.i(TAG, "upload start");
        try {
            String requestUrl = "http://192.168.1.113:8082/MyApplicationServer/MainServer";
            //请求普通信息
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", "张三");
            params.put("pwd", "zhangsan");
            params.put("age", "21");
            params.put("fileName", imageFile.getName());
            //上传文件
            FormFile formfile = new FormFile(imageFile.getName(), imageFile, "image", "application/octet-stream");

            SocketHttpRequester.post(requestUrl, params, formfile);
            Log.i(TAG, "upload success");
        } catch (Exception e) {
            Log.i(TAG, "upload error");
            e.printStackTrace();
        }
        Log.i(TAG, "upload end");
    }
}
