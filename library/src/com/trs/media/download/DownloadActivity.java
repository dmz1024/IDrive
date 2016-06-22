package com.trs.media.download;

import android.app.ListActivity;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.trs.mobile.R;

/**
 * Created by wbq on 14-8-1.
 */
public class DownloadActivity extends ListActivity{
    private DownloadService mDownloadService;
    private DownloadServiceConnection mDownloadServiceConnection;
    private Intent mDownloadServiceIntent;
    private DownloadUpdateReceiver downloadUpdateReceiver;
    private int barLenght = 0;

    public String XXXXX = "";

    public  class DownloadUpdateReceiver extends BroadcastReceiver{
        public final static String ACTION_DOWNLOAD_UPDATEVIEW = "com.trs.download.uploadview";
        @Override
        public void onReceive(Context context, Intent intent) {
            int fileAllSize = intent.getExtras().getInt("AllSize");
            barLenght += intent.getExtras().getInt("length");
            String logStr = (((float)barLenght/fileAllSize)*100 + "%").split("\\.")[0];
            Log.e("barl",logStr);
            mTextTemp.setText(logStr);
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
    }

    class DownloadServiceConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDownloadService = ((DownloadService.DownloadBinder)service).getDownloadService();
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
        mDownloadServiceIntent = new Intent(this,DownloadService.class);
        Bundle bundle = new Bundle();
        bundle.putString("url","http://music.baidu.com/data/music/file?link=http://yinyueshiting.baidu.com/data2/music/121949522/1215232761407063661128.mp3?xcode=c7ebe20afe1bdc58cb910d8a64030ebe1185dbd0cbb5163e&song_id=121523276");
        mDownloadServiceIntent.putExtras(bundle);
        startService(mDownloadServiceIntent);
    }

    public void pauseDownload(View v) {
        DownloadService.pauseDownload();
    }

    // 显示listView
    private void showListView() {
        setListAdapter(new adapter());
    }

    private TextView mTextTemp;
    class adapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(DownloadActivity.this).inflate(R.layout.mutithread_item,null);
            mTextTemp = (TextView)view.findViewById(R.id.tv_resouce_name);
            return view;
        }
    }
}
