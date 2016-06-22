package com.trs.media.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.trs.media.download.db.Dao;
import com.trs.media.download.entity.DownloadInfo;
import com.trs.media.download.entity.LoadInfo;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wbq on 14-7-31.
 * 多线程下载一个文件.
 */
public class DownloadMain {
    private String urlstr;            // 下载的地址
    private String filePath;          // 保存路径
    private int threadcount;          // 线程数
    private Handler mHandler;         // 消息处理器
    private Dao dao;                  // 工具类
    private int fileAllSize;          // 所要下载的文件的大小
    private List<DownloadInfo> infos; // 存放下载信息类的集合

    private static final int INIT = 1;//定义三种下载的状态：初始化状态，正在下载状态，暂停状态
    private static final int DOWNLOADING = 2;
    private static final int PAUSE = 3;
    private int state = INIT;

    public DownloadMain(String urlstr, String filePath, int threadcount,
                      Context context, Handler mHandler) {
        this.urlstr = urlstr;
        this.filePath = filePath;
        this.threadcount = threadcount;
        this.mHandler = mHandler;
        dao = new Dao(context);
    }

    public boolean isdownloading() {
        return state == DOWNLOADING;
    }

    /**
     * 判断是否是第一次 下载
     */
    private boolean isFirst(String urlstr) {
        return dao.isHasInfors(urlstr);
    }

    public LoadInfo getDownloadInfo(){
        if(isFirst(urlstr)){
            init();
            // 每个线程下载的程度.
            int range = fileAllSize / threadcount;
            infos = new ArrayList<DownloadInfo>();
            for (int i = 0; i < threadcount - 1; i++) {
                // 每个线程需要的下载任务.
                DownloadInfo info =
                        new DownloadInfo(i, i * range, (i + 1)* range - 1, 0, urlstr);
                infos.add(info);
            }
            DownloadInfo info =
                    new DownloadInfo(threadcount - 1,(threadcount - 1) * range,
                            fileAllSize - 1, 0, urlstr);
            infos.add(info);
            //保存infos中的数据到数据库
            dao.saveInfos(infos);
            //创建一个LoadInfo对象记载下载器的具体信息
            LoadInfo loadInfo = new LoadInfo(fileAllSize, 0, urlstr);
            return loadInfo;
        } else{
            //得到数据库中已有的urlstr的下载器的具体信息
            infos = dao.getInfos(urlstr);
            Log.v("TAG", "not isFirst size=" + infos.size());
            int size = 0;
            int compeleteSize = 0;
            for (DownloadInfo info : infos) {
                compeleteSize += info.getCompeleteSize();
                size += info.getEndPos() - info.getStartPos() + 1;
            }
            return new LoadInfo(size, compeleteSize, urlstr);
        }
    }

    /**
     * 初始化
     */
    private void init() {
        try {
            URL url = new URL(urlstr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            fileAllSize = connection.getContentLength();
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            // 本地访问文件
            RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
            accessFile.setLength(fileAllSize);
            accessFile.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 利用线程开始下载数据
        */
    public void download() {
        if (infos != null) {
            if (state == DOWNLOADING)
                return;
            state = DOWNLOADING;
            for (DownloadInfo info : infos) {
                new MyThread(info.getThreadId(), info.getStartPos(),
                        info.getEndPos(), info.getCompeleteSize(),
                        info.getUrl()).start();
            }
        }
    }

    public class MyThread extends Thread {
        private int threadId;
        private int startPos;
        private int endPos;
        private int compeleteSize;
        private String urlstr;

        public MyThread(int threadId, int startPos, int endPos,
                        int compeleteSize, String urlstr) {
            this.threadId = threadId;
            this.startPos = startPos;
            this.endPos = endPos;
            this.compeleteSize = compeleteSize;
            this.urlstr = urlstr;
        }

        @Override
        public void run() {
            HttpURLConnection connection = null;
            RandomAccessFile randomAccessFile = null;
            InputStream is = null;
            try{
                URL url = new URL(urlstr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Range","bytes="+(startPos + compeleteSize)
                        + "-" + endPos);
                Log.e("bar",startPos + "," + compeleteSize + "," + endPos);
                randomAccessFile = new RandomAccessFile(filePath,"rwd");
                randomAccessFile.seek(startPos + compeleteSize);

                is = connection.getInputStream();
                byte[] buffer = new byte[4096];
                int length = -1;
                while ((length = is.read(buffer)) != -1){
                    randomAccessFile.write(buffer,0,length);
                    compeleteSize += length;
//                    Log.e("TAG",compeleteSize+"");
                    // 更新数据库中的下载信息
                    dao.updataInfos(threadId, compeleteSize, urlstr);
                    // 用消息将下载信息传给进度条，对进度条进行更新
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = urlstr;
                    message.arg1 = length;
                    message.arg2 = fileAllSize;
                    Log.e("ca",length+"");
                    mHandler.sendMessage(message);
                    if(state == PAUSE){
                        return;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                // 各种关闭.
                try {
                    is.close();
                    randomAccessFile.close();
                    connection.disconnect();
                    dao.closeDb();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //删除数据库中urlstr对应的下载器信息
    public void delete(String urlstr) {
        dao.delete(urlstr);
    }

    //设置暂停
    public void pause() {
        state = PAUSE;
    }

    //重置下载状态
    public void reset() {
        state = INIT;
    }
}
