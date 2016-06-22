package com.froyo.commonjar.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;

import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.constant.Const;

public class AppUtils {
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getWidth(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	public static int px2sp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (spValue * scale + 0.5f);
	}

	public static int getHeight(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public static PackageInfo getPackageInfo(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	public static boolean needShowGuide(Context context) {
		int currentVersion = getPackageInfo(context).versionCode;
		SpUtil sp = new SpUtil(context);
		int historyVersion = sp.getIntegerValue(Const.APK_VERSION);
		return currentVersion > historyVersion;
	}

	public static void saveNewVersion(Context context) {
		SpUtil sp = new SpUtil(context);
		sp.setValue(Const.APK_VERSION,
				AppUtils.getPackageInfo(context).versionCode);
	}

	public static boolean isStorageExists() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public static void hideKkeyboard(BaseActivity activity) {
		try {
			((InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(activity.getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception ex) {

		}
	}

	/**
	 * 判断网络是否正常连接
	 * 
	 * @param context
	 * @return boolean true：网络连接正常 false：网络连接不正常
	 */
	public static boolean checkNet(Context context) {
		// 获取手机所有连接管理对象（包括wifi，net等连接的管理）
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivityManager != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivityManager.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	/*
	 * 打开设置网络界面
	 */
	public static void setNetworkMethod(final Context context) {
		// 提示对话框
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		try {
			builder.setTitle("网络设置提示")
					.setMessage("网络连接不可用,是否进行设置?")
					.setPositiveButton("设置",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = null;
									// 判断手机系统的版本 即API大于10 就是3.0或以上版本
									if (android.os.Build.VERSION.SDK_INT > 10) {
										intent = new Intent(
												android.provider.Settings.ACTION_WIRELESS_SETTINGS);
									} else {
										intent = new Intent();
										ComponentName component = new ComponentName(
												"com.android.settings",
												"com.android.settings.WirelessSettings");
										intent.setComponent(component);
										intent.setAction("android.intent.action.VIEW");
									}
									context.startActivity(intent);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).show();
		} catch (Exception e) {
		}

	}
}
