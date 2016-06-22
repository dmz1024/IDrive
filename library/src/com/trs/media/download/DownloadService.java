package com.trs.media.download;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.widget.ProgressBar;
import com.trs.media.download.entity.LoadInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wbq on 14-8-1.
 */
public class DownloadService extends Service{
    private final DownloadBinder binder = new DownloadBinder();
    private static String urlstr;
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // todo 传过来需要下载 URL
        urlstr = intent.getExtras().getString("url");
        startDownload();
        return super.onStartCommand(intent, flags, startId);
    }

    // 固定存放下载的音乐的路径：SD卡目录下
    private static final String SD_PATH = "/mnt/sdcard/";
    // 存放各个下载器
    private static Map<String, DownloadMain> downloaders = new HashMap<String, DownloadMain>();
    // 存放与下载器对应的进度条
    private Map<String, ProgressBar> ProgressBars = new HashMap<String, ProgressBar>();
    /**
     * 利用消息处理机制适时更新进度条
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String url = (String) msg.obj;
                int length = msg.arg1;

                Bundle bundle = new Bundle();
                bundle.putInt("length",length);
                bundle.putInt("AllSize",msg.arg2);
                Intent intent = new Intent();
                intent.setAction(DownloadActivity.DownloadUpdateReceiver.ACTION_DOWNLOAD_UPDATEVIEW);
                intent.putExtras(bundle);
                sendBroadcast(intent);
            }
        }
    };

    /**
     * 响应开始下载按钮的点击事件
     */
    public void startDownload() {
        String localfile = SD_PATH + "女儿情_";
        //设置下载线程数为4，这里是我为了方便随便固定的
        int threadcount = 4;
        // 初始化一个downloader下载器
        DownloadMain downloader = downloaders.get(urlstr);
        if (downloader == null) {
            downloader = new DownloadMain(urlstr, localfile, threadcount, this, mHandler);
            downloaders.put(urlstr, downloader);
        }
        if (downloader.isdownloading())
            return;
        // 得到下载信息类的个数组成集合
        LoadInfo loadInfo = downloader.getDownloadInfo();
        // 调用方法开始下载
        downloader.download();
    }

    public class DownloadBinder extends Binder {
        public DownloadService getDownloadService() {
            return DownloadService.this;
        }
    }

    /**
     * 响应暂停下载按钮的点击事件
     */
    public static void pauseDownload() {
        downloaders.get(urlstr).pause();
    }
}
