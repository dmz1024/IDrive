package com.trs.media.upload.uploadvideo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.*;
import android.widget.Button;
import android.widget.Chronometer;
import com.trs.mobile.R;
import com.trs.util.log.Log;

import java.io.IOException;

/**
 * Created by Administrator on 14-6-4.
 */
public class TestBasicVideo extends Activity implements SurfaceHolder.Callback {
    public static String EXTRA_PATH = "path";
    private String mVideoPath;

    private Button start;// 开始录制按钮
    private Button stop;// 停止录制按钮
    private MediaRecorder mediarecorder;// 录制视频的类
    private SurfaceView surfaceview;// 显示视频的控件
    // 用来显示视频的一个接口，我靠不用还不行，也就是说用mediarecorder录制视频还得给个界面看
    // 想偷偷录视频的同学可以考虑别的办法。。嗯需要实现这个接口的Callback接口
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Chronometer mTime;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置横屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 选择支持半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.videotest);
        mVideoPath = getIntent().getStringExtra(EXTRA_PATH);
        Log.e("w","12");
        init();
    }


    private void init() {
        start = (Button) this.findViewById(R.id.operaButton);
        start.setText("开始");
        start.setTag("stop");
        start.setOnClickListener(new TestVideoListener());

        surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);
        SurfaceHolder holder = surfaceview.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //// setType必须设置，要不出错.

        holder.addCallback(this);
        camera = Camera.open();

		int[] videoResolution = getSupportedResolution();
//        try {
//            camera.setPreviewDisplay(holder);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(videoResolution[0], videoResolution[1]);
//        parameters.setPreviewFrameRate(5);
//        parameters.setPictureFormat(PixelFormat.JPEG);
//        parameters.set("jpeg-quality", 85);
//        parameters.setPictureSize(200, 200);
//
//        camera.startPreview();
    }

    class TestVideoListener implements View.OnClickListener {

        @SuppressLint("WrongViewCast")
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onClick(View v) {
            if (v.getTag().equals("stop")) {
                mTime = (Chronometer) findViewById(R.id.time);
                mTime.setBase(SystemClock.elapsedRealtime());
                mTime.start();
                start.setText("停止");
                v.setTag("start");
                camera.stopPreview();
                camera.release();
                mediarecorder = new MediaRecorder();// 创建mediarecorder对象

                mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                mediarecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);

//                Camera camera = Camera.open();
//                Camera.Parameters param = camera.getParameters();
//                param.set( "cam_mode", 1 );
//                camera.setParameters( param );
                CamcorderProfile profile = null;
//                if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P))
//                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
//                else
				if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P))
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
                else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_CIF))
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_CIF);
                else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_QVGA))
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_QVGA);
                else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_QCIF))
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_QCIF);
                else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_LOW))
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);

				float vBitRateScale = 15f / (float)profile.videoFrameRate / 2f;
				int videoBitRate = Math.round(profile.videoBitRate * vBitRateScale);
				profile.videoBitRate = Math.max(videoBitRate, 2 * 1024 * 1024);
				profile.audioChannels = 1;
				profile.videoFrameRate = 15;
				profile.audioBitRate = profile.audioBitRate / 2;
				profile.videoBitRate = profile.videoBitRate / 4;

                mediarecorder.setProfile(profile);

                mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());

                mediarecorder.setOutputFile(mVideoPath);//"/sdcard/bg_video_listitem.mp4"
                try {
                    mediarecorder.prepare();
                    mediarecorder.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                mTime.stop();
                start.setText("开始");
                v.setTag("stop");
                if (mediarecorder != null) {
                    mediarecorder.stop();
                    mediarecorder.release();
                    mediarecorder = null;
                }
                camera.release();
                finish();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        surfaceHolder = holder;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        surfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // surfaceDestroyed的时候同时对象设置为null
        surfaceview = null;
        surfaceHolder = null;
        mediarecorder = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
//            camera.stopPreview();
            camera.release();
//            camera=null;
            setResult(101);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

	private static int supportedQuality = -1;
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private int getSupportedQuality(){
		if(supportedQuality == -1){
			supportedQuality = CamcorderProfile.QUALITY_HIGH;

			mediarecorder = new MediaRecorder();// 创建mediarecorder对象

			mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mediarecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);

			final int[] QUALITIES = {
				/*CamcorderProfile.QUALITY_1080P, CamcorderProfile.QUALITY_720P, */CamcorderProfile.QUALITY_480P,
					CamcorderProfile.QUALITY_CIF, CamcorderProfile.QUALITY_QVGA, CamcorderProfile.QUALITY_QCIF,
					CamcorderProfile.QUALITY_LOW};

			for(int q: QUALITIES){
				if (CamcorderProfile.hasProfile(q)){
					supportedQuality = q;
					break;
				}
			}

		}

		return supportedQuality;
	}

	private int[] getSupportedResolution(){
		int width, height;
		switch(supportedQuality){
			case CamcorderProfile.QUALITY_1080P:
				width = 1920;
				height = 1080;
				break;
			case CamcorderProfile.QUALITY_720P:
				width = 1280;
				height = 720;
				break;
			case CamcorderProfile.QUALITY_480P:
				width = 720;
				height = 480;
				break;
			case CamcorderProfile.QUALITY_CIF:
				width = 352;
				height = 288;
				break;
			case CamcorderProfile.QUALITY_QVGA:
				width = 320;
				height = 240;
				break;
			case CamcorderProfile.QUALITY_QCIF:
			case CamcorderProfile.QUALITY_LOW:
			default:
				width = 176;
				height = 144;
				break;
		}

		return new int[]{width, height};
	}
}
