package com.trs.media;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import com.trs.media.video.VideoRecSurfaceActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wbq on 14-7-30.
 */
public class MediaRecIntentFactory {
    public static enum videRecoderTypeEnum{

        V_INTENT,V_SURFACE, // 启动系统默认视频拍摄;启动自定义的视频拍摄
        I_PICK,I_PHOTO,     // 启动自带图片和拍摄图片.
        R_INTENT,R_DEFSULT  // 启动自带录音和自定义界面的录音.
    }

    private Context mContext;
    private String mFilePath;
    private videRecoderTypeEnum mEnum;

    public MediaRecIntentFactory(Context mContext, String filePath, videRecoderTypeEnum enum_){
        File file = new File(filePath);
        if(!file.isDirectory()){
            file.mkdirs();
        }
        this.mContext = mContext;
        mFilePath = filePath;
        mEnum = enum_;
    }

    public Intent getIntent(){
        if(mEnum != null && mEnum.equals(videRecoderTypeEnum.V_INTENT)){
            Intent intent = new Intent();
            intent.setAction("android.media.action.VIDEO_CAPTURE");
            intent.addCategory("android.intent.category.DEFAULT");
            Uri uri = Uri.fromFile(getVideoFileName());
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            return intent;
        }else if(mEnum.equals(videRecoderTypeEnum.V_SURFACE)){
            Intent intent = new Intent(mContext,VideoRecSurfaceActivity.class);
            File file = getVideoFileName();
            Uri uri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.putExtra("TEST",mFilePath);
            return intent;
        } else if(mEnum.equals(videRecoderTypeEnum.I_PICK)){
//            Intent intent = new Intent();
//            intent.setType("image/*");
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            return i;
        } else if(mEnum.equals(videRecoderTypeEnum.I_PHOTO)){
            File photoFile = new File(mFilePath,
                    String.format("%s.jpg", System.currentTimeMillis()));
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            return intent;
        } else if(mEnum.equals(videRecoderTypeEnum.R_DEFSULT)){
            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            intent.setType("audio/amr");
//            intent.setClassName("com.android.soundrecorder",
//                    "com.android.soundrecorder.SoundRecorder");
            return intent;
        } else{
            return null;
        }
    }

    private File getVideoFileName(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss");
        String fileName = "";
        File file;
        do {
            File fileFolder = new File(mFilePath);
            if(!fileFolder.isDirectory()){
                fileFolder.mkdirs();
            }
            fileName = mFilePath + sdf.format(new Date()) + "_" +
                    (int)((Math.random()*900)+100) + ".mp4";
            file = new File(fileName);
        }while (file.exists());
        return file;
    }
}
