package com.trs.media.Audio;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * @author 张兴胜
 * @version 1.0
 * @email xingshengzhang_vip@163.com
 * 该类为 硬件检测的 公共类
 */
public class EnvironmentShare {
	// 存放录音文件夹的名称
	static String AUDIO_RECORD = "/AudioRecord";
	// 存放视频文件夹的名称
	static String VIDEO_RECORD = "/VideoRecord";
	// 存放照片文件夹的名称
	static String TAKE_PICTURE = "/TakePicture";
	// 日志信息
	static String Tag ="EnvironmentShare";

	/**
	 * 检测当前设备SD是否可用
	 * 
	 * @return 返回"true"表示可用，否则不可用
	 */
	public static boolean haveSdCard() {
		Log.d(Tag, "status===>" + Environment.getExternalStorageState());
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获得SD卡根目录路径
	 * 
	 * @return String类型 SD卡根目录路径
	 */
	public static String getSdCardAbsolutePath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/**
	 * 获得存储 录音文件的文件夹
	 * 
	 * @return File类型 存储 录音文件的文件夹
	 */
	public static File getAudioRecordDir() {
		//获取文件路径path=/mnt/sdcard/AudioRecord
		File audioRecordFile = new File(EnvironmentShare.getSdCardAbsolutePath() + AUDIO_RECORD);
		if (!audioRecordFile.exists()) {
			// 创建目录
			audioRecordFile.mkdir();
		}
		return audioRecordFile;
	}
	/**
	 * 获得存储视频文件的文件夹
	 * @return File类型  存储视频文件的文件夹
	 */
	public static File getVedioRecordDir(){
		// 获取文件路径path=/mnt/sdcard/VedioRecord
		File vedioRecordFile = new File(EnvironmentShare.getSdCardAbsolutePath() +VIDEO_RECORD);
		if(!vedioRecordFile.exists()){
			//创建目录
			vedioRecordFile.mkdir();
		}
		return vedioRecordFile;
	}
	
	/**
	 * 获得存储照片文件的文件夹
	 * @return File类型  存储照片文件的文件夹
	 */
	public static File getTakePictureDir(){
		// 获取文件路径path=/mnt/sdcard/TakePicture
		File photoFile = new File(EnvironmentShare.getSdCardAbsolutePath() +TAKE_PICTURE);
		if(!photoFile.exists()){
			//创建目录
			photoFile.mkdir();
		}
		return photoFile;
	}

	/**
	 * 用Toast显示指定信息
	 * 
	 * @param activity
	 *            Activity类型 要显示提示信息的页面上下文
	 * @param message
	 *            String类型 将显示的提示信息内容
	 * @param isLong
	 *            boolean类型 如果为"true"表示长时间显示，否则为短时间显示
	 */
	public static void showToast(Activity activity, String message,
			boolean isLong) {
		if (message == null || message.equals(""))
			return;
		int showTime = Toast.LENGTH_SHORT;
		if (isLong) {
			showTime = Toast.LENGTH_LONG;
		}

		Toast.makeText(activity, message, showTime).show();
	}

	/**
	 * 用Toast显示指定信息 并设置标题显示 信息
	 * 
	 * @param activity
	 *            Activity类型 要显示提示信息的页面上下文
	 * @param message
	 *            String类型 将显示的提示信息内容
	 * @param isLong
	 *            boolean类型 如果为"true"表示长时间显示，否则为短时间显示
	 */
	public static void showToastAndTitle(Activity activity, String message,
			boolean isLong) {
		activity.setTitle(message);
		showToast(activity, message, isLong);
	}
}
