package com.trs.media.Test;

import android.app.ListActivity;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.SimpleAdapter;
import com.trs.mobile.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wbq on 14-8-1.
 */
public class DownloadActivity extends ListActivity{
    private DownloadMainActivity mDownloadService;
    private DownloadServiceConnection mDownloadServiceConnection;
    private Intent mDownloadServiceIntent;
    private DownloadUpdateReceiver downloadUpdateReceiver;

    public String XXXXX = "";

    public  class DownloadUpdateReceiver extends BroadcastReceiver{
        public final static String ACTION_DOWNLOAD_UPDATEVIEW = "com.trs.download.uploadview";
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mutithread);
        showListView();
        mDownloadServiceConnection = new DownloadServiceConnection();

        IntentFilter intentFilter = new IntentFilter(DownloadUpdateReceiver.ACTION_DOWNLOAD_UPDATEVIEW);
        downloadUpdateReceiver = new DownloadUpdateReceiver();
        registerReceiver(downloadUpdateReceiver,intentFilter);


//        Intent intent = new Intent(DownloadActivity.this,DownloadMainActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("url","");
//        intent.putExtras(bundle);
//        startService(intent);
    }

    class DownloadServiceConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDownloadService = ((DownloadMainActivity.DownloadBinder)service).getDownloadService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(mDownloadServiceIntent);
    }

    /**
     * 响应开始下载按钮的点击事件
     */
    public void startDownload(View v) {
        mDownloadServiceIntent = new Intent(this,DownloadMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url","");
        mDownloadServiceIntent.putExtras(bundle);
        startService(mDownloadServiceIntent);
    }

    public void pauseDownload(View v) {
//        mDownloadService.pauseDownload();
    }

    // 显示listView
    private void showListView() {
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("女儿情", "mm.mp3");
        data.add(map);
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.mutithread_item, new String[] { "name" },
                new int[] { R.id.tv_resouce_name });
        setListAdapter(adapter);
    }
}
