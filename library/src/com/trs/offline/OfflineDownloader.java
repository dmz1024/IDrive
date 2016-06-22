package com.trs.offline;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trs.app.Handler;
import com.trs.app.TRSApplication;
import com.trs.types.*;
import com.trs.util.FileUtil;
import com.trs.util.NetUtil;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;
import com.trs.wcm.RemoteDataService;
import com.trs.wcm.callback.BaseDataAsynCallback;
import com.trs.wcm.callback.IDataAsynCallback;
import com.trs.wcm.util.WCMTools;
import net.endlessstudio.util.Util;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 14-5-8.
 */
public class OfflineDownloader {
	public static final String TAG = "OfflineDownloader";

	public static final int OFFLINE_PAGE_COUNT = 5;//默认离线阅读页数
	private Context context;
	private RemoteDataService rds;
	private FileNameGenerator fileNameGenerator;
	private DiscCacheAware discCache;
    private JsoupUtil jsoupUtil;
    private double downloadPercent;
    private Map<String,Integer> map = new HashMap<String,Integer>();
    public static enum State {
        INIT, CANCEL, DOWNLOADING, FINISHED
    }
    private State downloadState;
    public static final String EXTRA_DOWNLOAD_PERCENT = "offline_download_percent";
    private  int complete = 0;
    private int ToatalDownloadCount = 0;

    /**
     * URL错误导致的异常，比如没有第二页(documents_1.xml),而去请求第二页，的标志位。
     */
    boolean isFileNotFoundException;
    protected final android.os.Handler handler = new android.os.Handler();

	public OfflineDownloader(Context context) {
		this.context = context;
		rds = new RemoteDataService(context);
		fileNameGenerator = new Md5FileNameGenerator();
		discCache = ImageLoader.getInstance().getDiscCache();
        jsoupUtil = new JsoupUtil(context);
        downloadState = State.INIT;
	}

	public static final String[] OFFLINE_TYPES = {"1000", };

    /**
     * @param URL
     * @param handler
     * @throws IOException
     * @throws JSONException
     * 离线下载所有栏目下的文章
     */
	public void startDownload(String URL, Handler handler) throws IOException, JSONException {

//		String firstClassMenu = rds.syncDownload(TRSApplication.app().getFirstClassUrl());
//		FirstClassMenu menu = FirstClassMenu.create(new JSONObject(firstClassMenu));
        FirstClassMenu menu = TRSApplication.app().getFirstClassMenu();
        if(TextUtils.isEmpty(URL) || handler == null || menu.getChannelList().size() < 1){
            return;
        }
        ToatalDownloadCount = menu.getChannelList().size();
        double eachChannelPercent = 100.0 / ToatalDownloadCount;
        Log.i(TAG, "eachChannelPercent:"+eachChannelPercent);
        if (downloadState != State.DOWNLOADING && downloadState != State.FINISHED) {
            downloadState = State.DOWNLOADING;
            downloadPercent = 0;
            complete = 0;
            for(Channel c: menu.getChannelList()){
                downloadPicture(c.getPic());
                Log.i(TAG, "download channel:"+c.getTitle());
                downloadChannelContent(c.getUrl(), eachChannelPercent, handler);
            }
        }
	}

    public boolean isDownloading() {
        return downloadState == State.DOWNLOADING;
    }

    public boolean isCancel() {
        return downloadState == State.CANCEL;
    }

    public void cancel(){
        downloadState = State.CANCEL;
    }

    public State getDownloadState(){
        return downloadState;
    }

	private void downloadPicture(String url) throws IOException {
		if(StringUtil.isEmpty(url)){
			return;
		}

		File cacheFile = discCache.get(url);
		if(cacheFile != null && cacheFile.exists()){
			return;
		}

		cacheFile = new File(TRSApplication.app().getCacheDir(), fileNameGenerator.generate(url));
        downloadPictureFile(url, cacheFile);

		discCache.put(url, cacheFile);

	}
    private void downloadPictureFile(String url, File file) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (!(200 <= responseCode && responseCode < 300)) {
            throw new IOException("Response code: " + responseCode);
        }

        InputStream is = conn.getInputStream();
        if(file.exists()){
            file.delete();
        }

        file.getParentFile().mkdirs();
        file.createNewFile();

        OutputStream os = new FileOutputStream(file);

        byte[] buf = new byte[1024 * 10];
        int readlen;
        while((readlen = is.read(buf)) >= 0){
            os.write(buf, 0, readlen);
            if(isCancel()){
                break;
            }
        }

        os.close();
        is.close();
    }

    /**
     * @author WLH
     * 2014-8-4下午3:52:28
     * 离线下载某栏目下的内容
     */
    public void downloadChannelContent(final String _sUrl, final double channelPercent, final Handler hanlder){

        new Thread() {
            @Override
            public void run() {
                dealWithUrlContent(_sUrl, channelPercent, hanlder);
                completeCount();
                Log.i(TAG, "downloadChannelContent complete count :" + complete);
                if (complete == ToatalDownloadCount) {
                    sendMessage(hanlder);
                }
            }
        }.start();
    }
    public synchronized void completeCount(){
        complete = complete + 1;
    }

    public void dealWithUrlContent(String _sUrl, final double percent, final Handler handler){
        if (isCancel()) {
            Log.i(TAG, "dealWithUrlContent 暂停下载");
            downloadState = State.CANCEL;
            return;
        }
        String sFURL = WCMTools.getPageURL(_sUrl);
        if(sFURL.endsWith("json")|| sFURL.contains("html")){
            String result = syncDownload(_sUrl);
            if(sFURL.endsWith("json")){//如果是json数据
                ArrayList<String> urls = StringUtil.getUrlsFromString(result);
                if(urls != null && urls.size() > 0){
                    int count = urls.size();
                    final double eachUrlPercent = percent/count ;
                    for(int i = 0; i< urls.size(); i++){
                        if (isCancel()) {
                            Log.i(TAG, "loadHtmlContent 暂停下载");
                            downloadState = State.CANCEL;
                            return;
                        }
                        final String url = urls.get(i);
                        if(StringUtil.isImgUrl(url)){//如果json数据里面含有img，则下载该图片
                            new Thread(){
                                @Override
                                public void run() {
                                    if (isCancel()) {
                                        Log.i(TAG, "dealWithUrlContent 暂停下载");
                                        return;
                                    }
                                    try {
                                        downloadPicture(url);
                                        notifyProgress(eachUrlPercent);
                                        sendMessage(handler);
                                    } catch (IOException e) {
                                        notifyProgress(eachUrlPercent);
                                        sendMessage(handler);
                                    }
                                }
                            }.start();

                        }else{//如果json数据里面含有json数据，
                            //TODO
                            if (isCancel()) {
                                Log.i(TAG, "dealWithUrlContent 暂停下载");
                                return;
                            }
                            dealWithUrlContent(url, eachUrlPercent, handler);
                        }
                    }
                }else{
                    notifyProgress(percent);
                    sendMessage(handler);
                }

            }else{//如果不是json数据，则默认为html数据
                loadHtmlContent(result, _sUrl, new BaseDataAsynCallback(){
                    @Override
                    public void onDataLoad(String _result) {
                        notifyProgress(percent);
                    }
                });
            }
        }else{
            notifyProgress(percent);
            sendMessage(handler);
        }
    }
    public synchronized void notifyProgress(double percent){
        downloadPercent = downloadPercent + percent;
    }

    public void sendMessage(Handler handler){
        Message msg = new Message();
        Bundle bundle = new Bundle();
        java.text.DecimalFormat df=new java.text.DecimalFormat("#.##");
        if(complete == ToatalDownloadCount && !isCancel()){
            downloadState = State.FINISHED;
            bundle.putString(EXTRA_DOWNLOAD_PERCENT, Integer.toString(100));
        }else{
            bundle.putString(EXTRA_DOWNLOAD_PERCENT, df.format(downloadPercent));
        }
        msg.setData(bundle);
        if(!isCancel()){
            handler.sendMessage(msg);
        }

        Log.i(TAG, "downloadPercent:"+downloadPercent);
    }

    /**
            * @author WLH
    * 2014-8-5下午4:47:04
            * 加载html里面的img，css，js
    */
    public void loadHtmlContent(final String html, final String html_Url, final BaseDataAsynCallback aysn){
        if (isCancel()) {
            Log.i(TAG, "loadHtmlContent 暂停下载");
            downloadState = State.CANCEL;
            return;
        }
        final ArrayList<String> imgurls = JsoupUtil.getImgUrls(html);
        ArrayList<String> scripturls = JsoupUtil.getScriptUrls(html);
        ArrayList<String> cssurls = JsoupUtil.getCssUrls(html);
        int Total_count = 0;
        if(imgurls != null && imgurls.size() > 0){
            Total_count = Total_count + imgurls.size();
        }
        if(scripturls != null && scripturls.size() > 0){
            Total_count = Total_count + scripturls.size();
        }
        if(cssurls != null && cssurls.size() > 0){
            Total_count = Total_count + cssurls.size();
        }
        final int finalTotal_count = Total_count;
        map.put(html_Url, 0);
        if(imgurls != null && imgurls.size() > 0){
            for(int i = 0; i<imgurls.size();i++){
                if (isCancel()) {
                    Log.i(TAG, "loadHtmlContent 暂停下载");
                    downloadState = State.CANCEL;
                    return;
                }
                final String imgUrl = imgurls.get(i);

                new Thread(){
                    @Override
                    public void run() {
                        try {
                            if (isCancel()) {
                                Log.i(TAG, "dealWithUrlContent 暂停下载");
                                return;
                            }
                            downloadPicture(imgUrl);
                            int count = map.get(html_Url) + 1;
                            map.put(html_Url, count);
                            if (count == finalTotal_count) {
                                jsoupUtil.modifyUrl(html, html_Url);
                                aysn.onDataLoad("");
                            }
                        } catch (IOException e) {
                            aysn.onDataLoad("");
                        }
                    }
                }.start();
            }
        }
        if(scripturls != null && scripturls.size() > 0){
            for(int i = 0; i<scripturls.size();i++){
                if (isCancel()) {
                    Log.i(TAG, "loadHtmlContent 暂停下载");
                    downloadState = State.CANCEL;
                    return;
                }
                final String url = scripturls.get(i);
                loadJSON(url, new IDataAsynCallback() {

                    @Override
                    public void onDataChanged() {
                    }

                    @Override
                    public void onDataLoad(String result,boolean bIsChanged) {
                        int count = map.get(html_Url) + 1;
                        map.put(html_Url, count);
                        if (count == finalTotal_count) {
                            jsoupUtil.modifyUrl(html, html_Url);
                            aysn.onDataLoad("");
                        }
                    }

                    @Override
                    public void onError(String result) {
                        aysn.onDataLoad("");
                    }

                });
            }
        }
        if(cssurls != null && cssurls.size() > 0){
            for(int i = 0; i<cssurls.size();i++){
                if (isCancel()) {
                    Log.i(TAG, "loadHtmlContent 暂停下载");
                    downloadState = State.CANCEL;
                    return;
                }
                final String url = cssurls.get(i);
                loadJSON(url, new IDataAsynCallback() {

                    @Override
                    public void onDataChanged() {
                    }

                    @Override
                    public void onDataLoad(String result,boolean bIsChanged) {
                        int count = map.get(html_Url) + 1;
                        map.put(html_Url, count);
                        if (count == finalTotal_count) {
                            jsoupUtil.modifyUrl(html, html_Url);
                            aysn.onDataLoad("");
                        }
                    }

                    @Override
                    public void onError(String result) {
                        aysn.onDataLoad("");
                    }

                });
            }
        }
    }
    public String syncDownload(String url){
        if (StringUtil.isEmpty(url)) {
            return null;
        }

        // 兼容直接翻斜杠的情况
        final String sFURL = WCMTools.getPageURL(url);
        final String sFLocalJSONPath = rds.getCacheFilePathFromURL(sFURL);
        final String sFLmtURL = rds.getLmtPath(sFURL);

        String sLocalLmtContent = null;
        String sLocalLmtPath = rds.getCacheFilePathFromURL(sFLmtURL);

        if (FileUtil.fileExists(sLocalLmtPath)) {
            try {
                sLocalLmtContent = FileUtil.readFile(sLocalLmtPath, "utf-8");
                System.out.println("sLocalLmtContent=" + sLocalLmtContent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String sLmtContent = null;
        try {
            sLmtContent = doGet(sFLmtURL, 4000);

        } catch (Exception e) {
            android.util.Log.w(TAG, "can not got lmt data from url: " + sFLmtURL);
        }

        try {
            if (!StringUtil.isEmpty(sLmtContent)) {
                FileUtil.writeFile(sLocalLmtPath, sLmtContent, "utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String sJSONContent = null;
        boolean bIsChanged = false;

        if (StringUtil.isEmpty(sLocalLmtContent) || !sLocalLmtContent.equals(sLmtContent)
                || !FileUtil.fileExists(sFLocalJSONPath)) {

            int nTimeout = FileUtil.fileExists(sFLocalJSONPath) ? 2000
                    : 4000;

            try {
                sJSONContent = doGet(sFURL, nTimeout);
            } catch (Exception e) {
                // just skip it
            }

            try {
                if (!StringUtil.isEmpty(sJSONContent)) {
                    FileUtil.writeFile(sFLocalJSONPath, sJSONContent, "utf-8");
                }
            } catch (Exception e) {
                // just skip it
            }
        }

        if (StringUtil.isEmpty(sJSONContent) && FileUtil.fileExists(sFLocalJSONPath)) {
            try {
                sJSONContent = FileUtil.readFile(sFLocalJSONPath, "utf-8");
            } catch (Exception innerEx) {
                // just skip it
            }
        }

        return sJSONContent;
    }

    public void loadJSON(String url, final IDataAsynCallback callback) {

        if (StringUtil.isEmpty(url)) {
            return;
        }

        // 兼容直接翻斜杠的情况
        final String sFURL = WCMTools.getPageURL(url);

        final String sFLocalJSONPath = rds.getCacheFilePathFromURL(sFURL);

        // 没有网络并且本地文件存在，直接读取本地文件
        if (!NetUtil.isConntected(context) && Util.isRemoteUrl(url) && FileUtil.fileExists(sFLocalJSONPath)) {

            try {
                final String sFinalJSONConent = FileUtil.readFile(sFLocalJSONPath, "utf-8");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDataLoad(sFinalJSONConent, false);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 虽然读本地文件出错，但认为是网络不给力
                        callback.onError("网络不可用");
                    }
                });
            }

            return;
        }
        if (!NetUtil.isConntected(context) && Util.isRemoteUrl(url)) {
            return;
        }

        // 有网络
        final String sFLmtURL = rds.getLmtPath(sFURL);

        new Thread(new Runnable() {

            @Override
            public void run() {
                String sLocalLmtContent = null;
                String sLocalLmtPath = rds.getCacheFilePathFromURL(sFLmtURL);

                if (FileUtil.fileExists(sLocalLmtPath)) {
                    try {
                        sLocalLmtContent = FileUtil.readFile(sLocalLmtPath, "utf-8");
                        System.out.println("sLocalLmtContent=" + sLocalLmtContent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                String sLmtContent = null;
                try {
                    sLmtContent = doGet(sFLmtURL, 4000);
                    // sLmtContent = doGet(sFLmtURL, 1);

                } catch (Exception e) {
                    android.util.Log.w(TAG, "can not got lmt data from url: " + sFLmtURL);
                }

                try {
                    if (!StringUtil.isEmpty(sLmtContent)) {
                        FileUtil.writeFile(sLocalLmtPath, sLmtContent, "utf-8");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String sJSONContent = null;
                boolean bIsChanged = false;

                if (StringUtil.isEmpty(sLocalLmtContent) || !sLocalLmtContent.equals(sLmtContent)
                        || !FileUtil.fileExists(sFLocalJSONPath)) {

                    int nTimeout = FileUtil.fileExists(sFLocalJSONPath) ? 2000
                            : 4000;

                    try {
                        sJSONContent = doGet(sFURL, nTimeout);
                    } catch (Exception e) {
                        // just skip it
                        isFileNotFoundException = true;
                    }

                    try {
                        if (!StringUtil.isEmpty(sJSONContent)) {
                            bIsChanged = true;
                            FileUtil.writeFile(sFLocalJSONPath, sJSONContent, "utf-8");
                        }
                    } catch (Exception e) {
                        // just skip it
                    }
                }

                if (StringUtil.isEmpty(sJSONContent) && FileUtil.fileExists(sFLocalJSONPath)) {
                    try {
                        sJSONContent = FileUtil.readFile(sFLocalJSONPath, "utf-8");
                    } catch (Exception innerEx) {
                        // just skip it
                    }
                }

                if (!StringUtil.isEmpty(sJSONContent)) {
                    final String sFinalJSONConent = sJSONContent;
                    final boolean bFIsChanged = bIsChanged;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataLoad(sFinalJSONConent, bFIsChanged);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            boolean isRefreshUrl = sFURL.split("/")[sFURL.split("/").length - 1].split("\\.")[0].split("_").length == 2;
                            if(isFileNotFoundException && isRefreshUrl){
                                callback.onError("没有更多了");
                            } else{
                                // 虽然读本地文件出错，但认为是网络不给力,但是不包含网络请求的URL出错的情况.
                                callback.onError("网络不给力");
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private String doGet(String url, int timeOut) throws IOException {

        URLConnection conn = new URL(url).openConnection();

        conn.setConnectTimeout(4 * 1000);
        conn.setReadTimeout(8 * 1000);
        conn.connect();
        InputStream is = conn.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024 * 10];
        int readlen;
        while ((readlen = is.read(buf)) >= 0) {
            baos.write(buf, 0, readlen);
            if(isCancel()){
                break;
            }
        }

        baos.close();

        String result = new String(baos.toByteArray(), HTTP.UTF_8);

        if (result.startsWith("\ufeff")) {
            result = result.substring(1);
        }

        return result;
    }
}
