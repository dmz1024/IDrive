package com.froyo.commonjar.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.froyo.commonjar.activity.BaseActivity;

/**
 * Utils工具，存放：时间格式化、Bitmap处理、获取视频缩略图、ListView的item同样是listView
 */
public class Utils {

	@SuppressLint("SimpleDateFormat")
	public static String formatTime(long time, String format) {
		return new SimpleDateFormat(format).format(new Date(time));
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatTime(Date time, String format) {
		// yyyy-MM-dd HH:mm:ss
		return new SimpleDateFormat(format).format(time);
	}

	/**
	 * 格式化 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param time
	 * @return
	 */
	public static String formatTime(String time) {
		if (isEmpty(time)) {
			return "传入时间为空";
		}
		return formatTime(Long.parseLong(time), "yyyy-MM-dd HH:mm");
	}

	public static String formatTime(String time,String modle) {
		if (isEmpty(time)) {
			return "传入时间为空";
		}
		return formatTime(Long.parseLong(time),modle );
	}

	public static String formatHour(String time) {
		if (isEmpty(time)) {
			return "传入时间为空";
		}
		return formatTime(Long.parseLong(time), "HH:mm");
	}

	public static boolean isEmpty(List<?> list) {
		return (list == null || list.size() == 0);
	}

	public static boolean isEmpty(File file) {
		return file == null;
	}

	public static <T> boolean isEmpty(T[] array) {
		return ((array == null) || (array.length) == 0);
	}

	public static boolean isEmpty(String val) {
		if (val == null || val.matches("\\s") || val.length() == 0
				|| "null".equalsIgnoreCase(val)) {
			return true;
		}
		return false;
	}

	public static <T> List<T> MapToList(Map<String, T> map) {
		List<T> list = new ArrayList<T>();
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = it.next();
			list.add(map.get(key));
		}
		return list;
	}

	public static boolean isMobileNum(String mobile) {
		if (isEmpty(mobile)) {
			return false;
		}
		Pattern p = Pattern.compile("^[1][3-8]+\\d{9}$");
		Matcher m = p.matcher(mobile);
		return m.matches();
	}

	public static void hideKeyboard(BaseActivity activity) {
		try {
			((InputMethodManager) activity
					.getSystemService(activity.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(activity.getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
		}
	}

	/**
	 * 判断服务是否是运行状态
	 * @param mContext
	 * @param className
     * @return
     */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equalsIgnoreCase(
					className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/**
	 * 将Bitmap通过流的方式转化为File，
	 * @param context
	 * @param bitmap
     * @return
     */
	public static File saveBitmapFile(Context context, Bitmap bitmap) {
		UUID id = UUID.randomUUID();
		File file = new File(InitUtil.getImageCachePath(context) + id + ".png");// 将要保存图片的路径
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 
	 * @Des: 获取视频缩略图
	 * @param @param url
	 * @param @param width
	 * @param @param height
	 * @param @return
	 * @return Bitmap
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static Bitmap createVideoThumbnail(String url, int width, int height) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		int kind = MediaStore.Video.Thumbnails.MINI_KIND;
		try {
			if (Build.VERSION.SDK_INT >= 14) {
				retriever.setDataSource(url, new HashMap<String, String>());
			} else {
				retriever.setDataSource(url);
			}
			bitmap = retriever.getFrameAtTime();
		} catch (IllegalArgumentException ex) {
			// Assume this is a corrupt video file
		} catch (RuntimeException ex) {
			// Assume this is a corrupt video file.
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException ex) {
				// Ignore failures while cleaning up.
			}
		}
		if (kind == Images.Thumbnails.MICRO_KIND && bitmap != null) {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
					ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}

	/**
	 * 主要是用于ListView的item同样是listView的情况
	 * @Des:设置ListView的Adapter后调用此静态方法即可让ListView正确的显示在其父ListView的ListItem中。但是要注意的是，子ListView的每个Item必须是LinearLayout，不能是其他的，
	 * @param @param listView
	 * @return void
	 */

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();

		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		listView.setLayoutParams(params);
	}
	public static void setListViewHeightBasedOnChildrenB(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();

		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		listView.setLayoutParams(params);
	}

	/**
	 *将Bitmap转换为圆角的方法
	 * @param bitmap
	 * @return
     */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		float ratio = 1;
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawColor(Color.TRANSPARENT);

		canvas.drawRoundRect(rectF, bitmap.getWidth() / ratio,
				bitmap.getHeight() / ratio, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
}
