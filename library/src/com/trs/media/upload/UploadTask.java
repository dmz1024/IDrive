package com.trs.media.upload;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;
import com.trs.mobile.R;
import com.trs.util.AsyncTask;

/**
 * Created by wbq on 2014/8/19.
 */
public class UploadTask extends AsyncTask<String ,Integer,String>{
    private NotificationManager mNManager;
    private Context mContext;
    private RemoteViews view;
    private Notification mN;
    private PendingIntent pIntent = null;//更新显示

    public UploadTask(Context context){
        this.mContext = context;
    }
    @Override
    protected void onPreExecute() {
        if(mNManager == null){
            mNManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mN = new Notification(android.R.drawable.stat_sys_upload, "test", System.currentTimeMillis());

        view = new RemoteViews(mContext.getPackageName(),R.layout.notifacation_seekbar);
        mN.contentView = view;
        pIntent =  PendingIntent.getActivity(mContext,
                R.string.app_name, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mN.contentIntent = pIntent;
        mN.flags = Notification.FLAG_NO_CLEAR;

    }

    @Override
    protected String doInBackground(String... params) {
        for (int i = 0; i <= 100; i++) {
            // 为了避免频繁发送消息所以每次增长10
            if (i % 10 == 0) {
                try {
                    // 模拟耗时操作
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 更改文字
                mN.contentView.setTextViewText(R.id.noti_tv, i + "%");
                // 更改进度条
                mN.contentView.setProgressBar(R.id.noti_pd, 100,
                        i, false);
                // 发送消息
                mNManager.notify(0, mN);
            }
        }
        return null;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            NotificationCompat.Builder.
        }
    };
}
