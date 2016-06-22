package com.ccpress.izijia.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.drm.DrmManagerClient;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import com.ccpress.izijia.R;
import com.ccpress.izijia.util.CameraUtil;
import com.ccpress.izijia.util.ScreenUtil;
import com.ccpress.izijia.view.CameraSurfaceView;
import com.trs.app.TRSFragmentActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Wu Jingyu
 * Date: 2015/5/15
 * Time: 16:33
 */
public class RecordVideoActivity extends TRSFragmentActivity {
    public static final String TAG = "RecordVideoActivity";
    public static int MAX_VIDEO_RECORDING_LENGTH = 10000;
    private Camera mCamera;
    private CameraSurfaceView mPreview;
    private MediaRecorder mMediaRecorder;
    private ImageView btn_video;
    private RECORD_STATUS mStatus = RECORD_STATUS.STOP;
    private int mCameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;
    private long startRecordTime = 0;
    private View progress_bar;
    public static final String VIDEO_SAVE_PATH = "VideoSavePath";
    private String videoSavePath = "";

    private enum RECORD_STATUS {STOP, RECORDING}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);

        initCamera();
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
        finish();
    }

    /**
     * 初始化Camera
     */
    private void initCamera() {
        mCamera = getCameraInstance();
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            //设置自动对焦
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }

            //设置预览尺寸
            Camera.Size size = CameraUtil.getBestSizeForPreview(this, params);
            if (size != null) {
                int height = size.height;
                int width = size.width;
                params.setPreviewSize(width, height);
            } else {

                Camera.Size preferredSize = params.getPreferredPreviewSizeForVideo();
                if(preferredSize != null){
                    params.setPreviewSize(preferredSize.width, preferredSize.height);
                } else {
                    preferredSize = params.getPreviewSize();
                    params.setPreviewSize(preferredSize.width, preferredSize.height);
                }
            }
            //设置预览方向
            CameraUtil.setCameraDisplayOrientation(this,
                    Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);

            mCamera.setParameters(params);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mPreview = new CameraSurfaceView(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        btn_video = (ImageView) findViewById(R.id.btn_video);
        btn_video.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    onStartRecordVideo();
                }
                if (action == MotionEvent.ACTION_UP) {
                    if (!canStopRecord()) {
                        Toast.makeText(RecordVideoActivity.this,
                                getResources().getText(R.string.record_length_hint),
                                Toast.LENGTH_LONG).show();
                        onStopRecordVideo(true);
                        return true;
                    }
                    onStopRecordVideo(false);
                }
                return true;
            }
        });


        progress_bar = findViewById(R.id.progress_bar);
    }

    /**
     * 录制video开始
     */
    private void onStartRecordVideo() {
        if (prepareVideoRecorder()) {
            mMediaRecorder.start();
            //初始化启动录制时的界面数据
            mStatus = RECORD_STATUS.RECORDING;
            btn_video.setImageResource(R.drawable.btn_video_down);
            startRecordTime = System.currentTimeMillis();
            Animation processBarAnimation = AnimationUtils.loadAnimation(this, R.anim.progress_video_anim);
            progress_bar.setVisibility(View.VISIBLE);
            progress_bar.startAnimation(processBarAnimation);
            processBarAnimation.setFillAfter(true);
        } else {
            releaseMediaRecorder();
        }
    }

    /**
     * 录制video停止
     * @param isCancel
     */
    private void onStopRecordVideo(boolean isCancel) {
        if(mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();  // stop the recording
            } catch (Exception e){
                //这里必须要有try catch
                //否则，当用户点击一下拍摄按钮就放开时由于摄像头还未初始化，stop会失败报异常
                //这里截获异常防止主线程Crash
                e.printStackTrace();
            }
        }
        releaseMediaRecorder(); // release the MediaRecorder object
        if(mCamera != null){
            mCamera.lock();         // take camera access back from MediaRecorder
        }

        // inform the user that recording has stopped
        //初始化停止录制时的界面数据
        mStatus = RECORD_STATUS.STOP;
        btn_video.setImageResource(R.drawable.btn_video_normal);
        progress_bar.clearAnimation();
        progress_bar.setVisibility(View.INVISIBLE);

        if (!isCancel) {
            //跳转到Post界面
            Intent intent = new Intent(RecordVideoActivity.this, PostEditActivity.class);
            intent.putExtra(MediaPickerActivity.MEDIA_TYPE, MediaPickerActivity.MEDIA_TYPE_VID);
            intent.putExtra(VIDEO_SAVE_PATH, videoSavePath);
            startActivity(intent);
            finish();
        } else {
            File file = new File(videoSavePath);
            if(file.exists()){
                file.delete();
            }
        }
    }

    /**
     *调用系统摄像头
     * @return
     */
    private boolean prepareVideoRecorder() {
        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        CamcorderProfile profile = null;
//        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P))
//            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
//        else
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P))
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P))
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH))
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_LOW))
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);

        if (profile != null) {
            float vBitRateScale = 15f / (float) profile.videoFrameRate / 2f;
            int videoBitRate = Math.round(profile.videoBitRate * vBitRateScale);
            profile.videoBitRate = Math.max(videoBitRate, 2 * 1024 * 1024);
            profile.audioChannels = 1;
//            profile.videoFrameRate = 15;
            profile.audioBitRate = profile.audioBitRate / 2;
            profile.videoBitRate = profile.videoBitRate / 4;
            profile.fileFormat = MediaRecorder.OutputFormat.THREE_GPP;
            profile.videoCodec = MediaRecorder.VideoEncoder.H264;
            mMediaRecorder.setProfile(profile);
        }

        // Step 4: Set output file
        File file = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        if (file != null) {
            videoSavePath = file.toString();
        }
        mMediaRecorder.setOutputFile(videoSavePath);

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
        mMediaRecorder.setMaxDuration(MAX_VIDEO_RECORDING_LENGTH);
        mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mediaRecorder, int i, int i1) {
            }
        });
        mMediaRecorder.setOnInfoListener(new MediaRecorderInfoListener());
        if (mCameraPosition == Camera.CameraInfo.CAMERA_FACING_BACK) {//后置摄像头
            mMediaRecorder.setOrientationHint(90);
        } else {//前置摄像头
            mMediaRecorder.setOrientationHint(270);
        }

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Toast.makeText(this,
                    getResources().getText(R.string.camera_busy),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private boolean canStopRecord() {
        return System.currentTimeMillis() - startRecordTime > 1000;
    }

    private class MediaRecorderInfoListener implements MediaRecorder.OnInfoListener {
        @Override
        public void onInfo(MediaRecorder mediarecorder, int whatInfo, int extra) {
            if (whatInfo == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                onStopRecordVideo(false);
//                postVideo();
            }
        }
    }

    private int onOrientationChanged(int orientation) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        orientation = (orientation + 45) / 90 * 90;
        int rotation = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - orientation + 360) % 360;
        } else {  // back-facing camera
            rotation = (info.orientation + orientation) % 360;
        }
        return rotation;
    }
}
