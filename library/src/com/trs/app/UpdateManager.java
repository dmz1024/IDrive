/*
 *	History				Who				What
 *  2012-5-10			Administrator			Created.
 */

package com.trs.app;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.trs.util.*;
import com.trs.util.log.Log;
import com.trs.mobile.R;
import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Title: TRS 内容协作平台（TRS WCM） <BR>
 * Description: <BR>
 * 负责检测更新、安装更新 <BR>
 * Copyright: Copyright (c) 2004-2012 北京拓尔思信息技术股份有限公司 <BR>
 * Company: www.trs.com.cn <BR>
 * 
 * @author zhangxinjian
 * @version 1.0
 */
public class UpdateManager {
	public static class SDCardNotReadyException extends IOException {
		public SDCardNotReadyException() {
		}

		public SDCardNotReadyException(String detailMessage) {
			super(detailMessage);
		}

		public SDCardNotReadyException(String message, Throwable cause) {
			super(message, cause);
		}

		public SDCardNotReadyException(Throwable cause) {
			super(cause);
		}
	}

    private static final String TAG = "UpdateManager";

    /* 下载中 */
    private static final int DOWNLOAD_DOING = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    /* */
    private static final int SHOW_UPDATE_DIALOG = 3;
    public static final int AUTO_FLAG = 1;
    public static final int MANUAL_FLAG = 2;

    /** 延迟时间，毫秒 */
    private static final int DELAYTIME = 3000;
    /* 保存解析的XML信息 */
    HashMap<String, String> mHashMap;
    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;
    private Context mContext;
    /* 更新进度条 */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            // 正在下载
                case DOWNLOAD_DOING:
                    // 设置进度条位置
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件
                    installApk();
                    break;
                case SHOW_UPDATE_DIALOG:
                    showNoticeDialog();
                default:
                    break;
            }
        };
    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    /**
     * 检测应用更新
     * 
     * @param _nFlag 更新方式，手动检查更新{@link UpdateManager#MANUAL_FLAG}，开启应用自动检查更新
     *            {@link UpdateManager#AUTO_FLAG}
     */
    public void checkUpdate(final int _nFlag) {
        new Thread(new Runnable() {

            /**
             * 下载更新信息，解析到HashMap中
             */
            private void loadUpdateInfo() {
                try {

                    String updateUrl = ApplicationConfig.getInstance().getConfigValue(
                            ApplicationConfig.UPDATEURL);

                    if (StringUtil.isEmpty(updateUrl)) {
                        return;
                    }

                    String sResult = HttpUtil.doGet(updateUrl);

                    if (StringUtil.isEmpty(sResult)) {
                        return;
                    }

                    mHashMap = new HashMap();

					JSONObject obj = new JSONObject(sResult);
					JSONObjectHelper helper = new JSONObjectHelper(obj);
                    // 软件版本
                    mHashMap.put("version", helper.getString("version", null));
                    Log.d(TAG, "version:" + helper.getString("version", null));

                    // 软件名称
                    String sDownloadURL = helper.getString("downloadurl", null);
                    mHashMap.put("downloadurl", sDownloadURL);
                    Log.d(TAG, "downloadurl:" + helper.getString("downloadurl", null));

                    String sFileName = FileUtil.extractFileName(sDownloadURL);
                    mHashMap.put("name", sFileName);
                    Log.d(TAG, "name:" + sFileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * 检查软件是否有更新版本
             *
             * @return
             */
            private boolean isUpdate() {
                // 获取当前软件版本
                float versionCode = getVersionCode(mContext);

                Log.e(TAG, "localVersion:" + versionCode);
                if (null != mHashMap) {
                    try {
                        float serviceCode = Float.valueOf(mHashMap.get("version"));
                        Log.e(TAG, "server Version:" + serviceCode);

                        // 版本判断
                        if (serviceCode > versionCode) {
                            return true;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "server version is not int");
                    }
                }
                return false;
            }

            @Override
            public void run() {
                try {
					Looper.prepare();
					if(_nFlag == AUTO_FLAG){
						Thread.sleep(DELAYTIME);
					}
                    // 检测网络
                    if (Tool.checkNetWork(mContext)) {
                        loadUpdateInfo();
                        if (isUpdate()) {
                            // 显示提示对话框
							if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
								mHandler.post(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(mContext, R.string.soft_update_network_sdcard_not_found,
												Toast.LENGTH_LONG).show();}
								});
								return;
							}
                            mHandler.sendEmptyMessage(SHOW_UPDATE_DIALOG);
                        } else {
                            if (_nFlag == MANUAL_FLAG) {
								mHandler.post(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(mContext, R.string.soft_update_no,
												Toast.LENGTH_LONG).show();}
								});
                            }
                        }
                    } else {
                        if (_nFlag == MANUAL_FLAG) {
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(mContext, R.string.soft_update_network_unavailable,
											Toast.LENGTH_LONG).show();
								}
							});
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    private float getVersionCode(Context context) {
        float versionCode = 1.0f;
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog() {
        DialogInterface.OnClickListener onPositiveButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 显示下载对话框
                showDownloadDialog();
            }
        };

        DialogInterface.OnClickListener onNegativeButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_title)
				.setMessage(R.string.soft_update_info)
				.setPositiveButton(R.string.soft_update_updatebtn, onPositiveButtonClickListener)
				.setNegativeButton(R.string.soft_update_cancel, null)
				.show();
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {
        // 构造软件下载对话框
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_updating);
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.soft_update_progress, null);
        mProgress = (ProgressBar)v.findViewById(R.id.soft_update_progress);
        builder.setView(v);

        // 取消更新
        builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 设置取消状态
                cancelUpdate = true;
            }
        });

        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        // 下载文件
        downloadApk();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }

    /**
     * 下载文件线程
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                // 获得存储的路径
				mSavePath = new File(mContext.getApplicationContext().getCacheDir(), "download").getAbsolutePath();

                String sDownloadURL = mHashMap.get("downloadurl");

                // 创建连接
                URL url = new URL(sDownloadURL);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.connect();

                // 获取文件大小
                int length = conn.getContentLength();
                // 创建输入流
                InputStream is = conn.getInputStream();

                File file = new File(mSavePath);
                // 判断文件目录是否存在
                if (!file.exists()) {
                    file.mkdirs();
                }
                File apkFile = new File(mSavePath, mHashMap.get("name"));
                FileOutputStream fos = new FileOutputStream(apkFile);
                int count = 0;
                // 缓存
                byte buf[] = new byte[1024];
                // 写入到文件中
                do {
                    int numread = is.read(buf);
                    count += numread;
                    // 计算进度条位置
                    progress = (int)(((float)count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWNLOAD_DOING);
                    if (numread < 0) {
                        // 下载完成
                        mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                        break;
                    }
                    // 写入文件
                    fos.write(buf, 0, numread);
                } while (!cancelUpdate);// 点击取消就停止下载.
                fos.close();
                is.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 取消下载对话框显示
            mDownloadDialog.dismiss();
        }
    };

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(mSavePath, mHashMap.get("name"));
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }
}
