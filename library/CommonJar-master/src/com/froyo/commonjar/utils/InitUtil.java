package com.froyo.commonjar.utils;

import java.io.File;

import android.app.Activity;
import android.content.Context;

import com.froyo.commonjar.constant.Const;


public class InitUtil {
	public static void createDbFolder(Activity activity) {
		FileOperator.createFolder(activity, Const.DB_FOLDER_NAME);

	}

	public static void createImageCacheFolder(Activity activity) {
		FileOperator.createFolder(activity, Const.IMAGE_CACHE_FOLDER_NAME);
	}

	public static void createDb(Activity activity) {
		FileOperator.createFile(activity, Const.DB_NAME, Const.DB_FOLDER_NAME);
	}

	public static String getDbPath(Context context) {
		String folderPath = FileOperator.getPath(context) + File.separator
				+ Const.DB_FOLDER_NAME + File.separator;
		return folderPath + Const.DB_NAME;
	}

	public static String getDbFolderPath(Context context) {
		String folderPath = FileOperator.getPath(context) + File.separator
				+ Const.DB_FOLDER_NAME + File.separator;
		return folderPath;
	}
	
	public static String getImageCachePath(Context context){
		String folderPath = FileOperator.getPath(context) + File.separator
				+ Const.IMAGE_CACHE_FOLDER_NAME + File.separator;
		return folderPath;
	}
	
}
