package com.trs.media.upload.uploadradio;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.trs.mobile.R;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * 
 * @author wbq
 * 
 */
public class RecorderActivity extends Activity {
	
	private final String TAG = "RecorderActivity";
	
	private Button btnStart;
	private Button btnStop;
	private Button btnUpload;
	private TextView text;
	private MediaRecorder recorder;
	private boolean isSDCardExit; // 判断SDCard是否存在
	private File SDPathDir;
	private File tempFile;
	private String urlStr = "http://192.168.1.110:8082/MyApplicationServer/MainServer";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadradiomain);
        
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        text = (TextView) findViewById(R.id.text);
        
        btnStart.setEnabled(true);
    	btnStop.setEnabled(false);
    	btnUpload.setEnabled(false);
        
        isSDCardExit = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    	if(isSDCardExit){
    		SDPathDir = Environment.getExternalStorageDirectory();
    	}
    	
    	buttonListener();
        
    }

    /**
     * 添加按钮事件
     */
    private void buttonListener(){
        // 开始录音
    	btnStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initRecorder();
				startRecorder();
				text.setText("正在录音……");
	    		btnStart.setEnabled(false);
	    		btnStop.setEnabled(true);
	    		btnUpload.setEnabled(true);
			}
		});
        // 停止录音
    	btnStop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stopRecorder();
				text.setText("停止录音……");
		    	btnStart.setEnabled(true);
		    	btnStop.setEnabled(false);
		    	btnUpload.setEnabled(true);
			}
		});
        // 上传录音
    	btnUpload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				text.setText("正在上传……");
				btnStart.setEnabled(true);
		    	btnStop.setEnabled(false);
		    	btnUpload.setEnabled(false);
		    	if(upload(tempFile)){
		    		text.setText("上传成功……");
		    	}else{
		    		text.setText("上传失败……");
		    	    Toast.makeText(RecorderActivity.this, "上传失败", Toast.LENGTH_SHORT);
		    	}
			}
		});
    }

    /**
     * 准备录音
     */
    private void initRecorder(){
    	recorder = new MediaRecorder();
    	/* 设置音频源*/
    	recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    	/* 设置输出格式*/
    	recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    	/* 设置音频编码器*/
    	recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    	
    	try {
    		/* 创建一个临时文件，用来存放录音*/
    		tempFile = File.createTempFile("tempFile", ".amr", SDPathDir);
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	/* 设置录音文件*/
    	recorder.setOutputFile(tempFile.getAbsolutePath());
    }

    /**
     * 开始录音
     */
    private void startRecorder(){
    	try {
    		if(!isSDCardExit){
    			Toast.makeText(this, "请插入SD卡", Toast.LENGTH_LONG).show();
    			return;
    		}
    		recorder.prepare();
    		recorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * 停止录音
     */
    private void stopRecorder(){
    	if(recorder != null){
    		recorder.stop();
    		recorder.release();// 释放资源
    		recorder = null;
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
			URL url = new URL(urlStr);
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
			Log.e(TAG, file.toString());
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
				Log.e(TAG, conn.getResponseCode() + "=======");
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

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if(recorder != null){
			recorder.stop();
    		recorder.release();//释放资源
    		recorder = null;
		}
		super.onStop();
	}
    
}