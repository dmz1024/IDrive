package com.trs.media.upload.uploadpicture;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;
import com.trs.constants.Constants;
import com.trs.mobile.R;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by wbq on 2014/9/1.
 */
public class UploadPictureService extends Service{
    private Context mContext = this;
    public static final String FILE_LOAD = "fileload";

    private NotificationManager mNManager;
    private RemoteViews view;
    private Notification mN;
    private PendingIntent pIntent = null;//更新显示
    private String mFilePath;
    private Button mPauseBtn,mPlayBtn;
    public static final String STATUS_BAR_COVER_CLICK_ACTION = "com.trs.uploadpause";
    public static final String STATUS_BAR_COVER_CLICK_ACTION_PLAY = "com.trs.uploadplay";
    UploadUtil uploadUtil;
    private int mTransfer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        mFilePath = intent.getStringExtra(FILE_LOAD);
        if(intent.getStringExtra(FILE_LOAD) != null){
            uploadImage();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void uploadImage(){
        File file = null;
        try {
            file = new File(new URI(mFilePath));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        uploadUtil = new UploadUtil();
        uploadUtil.uploadFile(file, Constants.SERVER_ADDRESS,handler);
    }

    BroadcastReceiver onClickReceiver_play = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(STATUS_BAR_COVER_CLICK_ACTION_PLAY)) {
                uploadUtil.playUpload(mTransfer);
            }
        }
    };

    BroadcastReceiver onClickReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(STATUS_BAR_COVER_CLICK_ACTION)) {
                uploadUtil.pauseUpload();
            }
        }

        ;
    };
    private void init(){
        if(mNManager == null){
            mNManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mN = new Notification(android.R.drawable.stat_sys_upload, "test", System.currentTimeMillis());

        view = new RemoteViews(this.getPackageName(), R.layout.notifacation_seekbar);
        mN.contentView = view;
        pIntent =  PendingIntent.getActivity(this,
                R.string.app_name, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mN.contentIntent = pIntent;
        mN.flags = Notification.FLAG_NO_CLEAR;

        IntentFilter filter = new IntentFilter();
        filter.addAction(STATUS_BAR_COVER_CLICK_ACTION);
        registerReceiver(onClickReceiver, filter);

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(STATUS_BAR_COVER_CLICK_ACTION_PLAY);
        registerReceiver(onClickReceiver_play, filter1);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mTransfer = msg.getData().getInt("seek");
            Log.e("read_get", mTransfer + "");

            Intent buttonIntent = new Intent(STATUS_BAR_COVER_CLICK_ACTION);
            PendingIntent pendButtonIntent = PendingIntent.getBroadcast(mContext, 0, buttonIntent, 0);
            mN.contentView.setOnClickPendingIntent(R.id.pause, pendButtonIntent);

            Intent buttonIntent1 = new Intent(STATUS_BAR_COVER_CLICK_ACTION_PLAY);
            PendingIntent pendButtonIntent1 = PendingIntent.getBroadcast(mContext, 0, buttonIntent1, 0);
            mN.contentView.setOnClickPendingIntent(R.id.play, pendButtonIntent1);
            int i  = msg.arg1;
            if(i == 100){
                mN.flags = Notification.FLAG_AUTO_CANCEL;
            }
            // 更改文字
            mN.contentView.setTextViewText(R.id.noti_tv, i + "%");
            // 更改进度条
            mN.contentView.setProgressBar(R.id.noti_pd, 100,i, false);
            // 发送消息
            mNManager.notify(0, mN);
        }
    };
}
