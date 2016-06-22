package com.trs.util;

import android.content.Context;
import android.os.Environment;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Created by john on 13-11-18.
 */
public class Util {
	public static enum CacheType{
		Image, Video, Audio
	}
	public static File getCacheDir(Context context, CacheType type){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return new File(context.getExternalCacheDir(), type.toString());
		}
		else{
			return new File(context.getCacheDir(), type.toString());
		}
	}

	public static void close(Closeable closeable){
		if(closeable != null){
			try{
				closeable.close();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}

	abstract public static class GetFileSizeHandler {
		private int fileCount = 0;
		private long fileSize = 0;
		private boolean isCancelled = false;
		public void onFile(File file, int fileCount, long totalSize){};
		public void cancel(){
			isCancelled = true;
		}

		public int getFileCount(){
			return fileCount;
		}

		public long getFileSize(){
			return fileSize;
		}
	}

	/**
	 * WARNING: This would take some time, please do not use it in ui thread.
	 */
	public static void getFileSize(File file, GetFileSizeHandler handler){
		if(handler == null){
			throw new NullPointerException();
		}

		innerGetFileSize(file, handler);
	}

	private static void innerGetFileSize(File file, GetFileSizeHandler handler){

		if(file.exists() && !handler.isCancelled){
			if (!file.isFile()) {
				handler.fileCount ++;
				handler.fileSize += file.length();
				handler.onFile(file, handler.fileCount, handler.fileSize);
			} else if(file.isDirectory()) {
				for(File f: file.listFiles()){
					innerGetFileSize(f, handler);
				}
			}
		}
	}

	public static boolean equals(Object o1, Object o2){
		if(o1 == o2){
			return true;
		}

		if(o1 != null){
			return o1.equals(o2);
		}

		if(o2 != null){
			return o2.equals(o1);
		}

		return false;
	}
}
