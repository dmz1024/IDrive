package com.trs.media.Test;

import android.app.ListActivity;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import com.trs.media.Test.entity.LoadInfo;
import com.trs.mobile.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 14-8-1.
 */
public class DownloadMainActivity extends ListActivity {
    private final DownloadBinder binder = new DownloadBinder();

//    @Override
//    public IBinder onBind(Intent intent) {
//        return binder;
//    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // todo 传过来需要下载 URL
//        intent.getExtras().getString("url");
//        startDownload();
//        return super.onStartCommand(intent, flags, startId);
//    }

    // 固定存放下载的音乐的路径：SD卡目录下
    private static final String SD_PATH = "/mnt/sdcard/";
    // 存放各个下载器
    private Map<String, DownloadMain> downloaders = new HashMap<String, DownloadMain>();
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

                Intent intent = new Intent();
                intent.setAction(DownloadActivity.DownloadUpdateReceiver.ACTION_DOWNLOAD_UPDATEVIEW);
                sendBroadcast(intent);

                ProgressBar bar = ProgressBars.get(url);
                if (bar != null) {
                    // 设置进度条按读取的length长度更新
                    Log.e("bar1", bar.getProgress()+","+bar.getMax() + "");
                    bar.incrementProgressBy(length);

                    if (bar.getProgress() == bar.getMax()) {
                        //Toast.makeText(DownloadActivity.this, "下载完成！", 0).show();
                        // 下载完成后清除进度条并将map中的数据清空
                        LinearLayout layout = (LinearLayout) bar.getParent();
                        layout.removeView(bar);
                        ProgressBars.remove(url);
                        downloaders.get(url).delete(url);
                        downloaders.get(url).reset();
                        downloaders.remove(url);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.mutithread);
        showListView();
    }

    // 显示listView，这里可以随便添加音乐
    private void showListView() {
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("nverqing", "mm.mp3");
        data.add(map);
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.mutithread_item, new String[] { "name" },
                new int[] { R.id.tv_resouce_name });
        setListAdapter(adapter);
    }

    /**
     * 响应开始下载按钮的点击事件
     */
    public void startDownload(View v) {
        // 得到textView的内容
//        LinearLayout layout = (LinearLayout) v.getParent();
//        String musicName = ((TextView) layout.findViewById(R.id.tv_resouce_name)).getText().toString();
        String urlstr = "http://music.baidu.com/data/music/file?link=http://yinyueshiting.baidu.com/data2/music/121949522/1215232761407063661128.mp3?xcode=c7ebe20afe1bdc58cb910d8a64030ebe1185dbd0cbb5163e&song_id=121523276";//URL + musicName;
        String localfile = SD_PATH + "女儿情1";
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
        // 显示进度条
        showProgress(loadInfo, urlstr, v);
        // 调用方法开始下载
        downloader.download();
    }

    /**
     * 显示进度条
     */
    private void showProgress(LoadInfo loadInfo, String url, View v) {
        ProgressBar bar = ProgressBars.get(url);
        if (bar == null) {
            bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            bar.setMax(loadInfo.getFileSize());
            bar.setProgress(loadInfo.getComplete());
            ProgressBars.put(url, bar);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 5);
            ((LinearLayout) ((LinearLayout) v.getParent()).getParent()).addView(bar, params);
        }
    }

    public class DownloadBinder extends Binder {
        public DownloadMainActivity getDownloadService() {
            return DownloadMainActivity.this;
        }
    }

    /**
     * 响应暂停下载按钮的点击事件
     */
    public void pauseDownload(View v) {
//        LinearLayout layout = (LinearLayout) v.getParent();
//        String musicName = ((TextView) layout.findViewById(R.id.tv_resouce_name)).getText().toString();
        String urlstr = "http://music.baidu.com/data/music/file?link=http://yinyueshiting.baidu.com/data2/music/5840226/5837021190800128.mp3?xcode=4f38c14faf8f9d21841741dc7ceea3b9160b378aca61157e&song_id=5837021";
        downloaders.get(urlstr).pause();
    }
}
