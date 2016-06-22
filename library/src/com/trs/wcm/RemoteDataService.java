
package com.trs.wcm;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.trs.util.FileUtil;
import com.trs.util.NetUtil;
import com.trs.util.StringUtil;
import com.trs.wcm.callback.IDataAsynCallback;
import com.trs.wcm.util.WCMTools;
import net.endlessstudio.util.Util;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.IOException;

public class RemoteDataService {
	public static final String TAG = "RemoteDataService";
    /**
     * 请求lmt文件时超时的时间设置
     */
    private static final int TIME_OUT_4_LMT = 4000;
    /**
     * 当本地文件存在时，请求超时的时间设置
     */
    private static final int TIME_OUT_WHILE_FILE_EXISTS = 2000;
    /**
     * 当本地文件不存在时，请求超时的时间设置
     */
    private static final int TIME_OUT_WHILE_FILE_NOT_EXISTS = 4000;

    protected final Handler handler = new Handler();
	private Context context;
	public RemoteDataService(Context context){
		this.context = context;
	}

    /**
     * URL错误导致的异常，比如没有第二页(documents_1.xml),而去请求第二页，的标志位。
     */
    boolean isFileNotFoundException;

    /**
     * 尝试去服务器端下载最新数据，下载后存放到本地； 如果下载失败，尝试读取本地之前下载过的数据；如果有则返回，如果没有，则认为失败；
     * 
     * @param url
     * @param callback
     * @return
     */
    public void alwaysLoadJSON(String url, final IDataAsynCallback callback) {

        if (StringUtil.isEmpty(url)) {
            return;
        }

        if (!NetUtil.isConntected(context) && Util.isRemoteUrl(url)) {
            return;
        }

        // 兼容直接翻斜杠的情况
        final String sFUrl = WCMTools.getPageURL(url);
        final String sFLocalFile = getPesisitentFilePathFromURL(sFUrl);

        // 计算超时时间
        int nTimeout = 3 * TIME_OUT_WHILE_FILE_NOT_EXISTS;

        if (new File(sFLocalFile).exists()) {
            nTimeout = TIME_OUT_WHILE_FILE_EXISTS;
        }

        final int nFTimeout = nTimeout;

        new Thread(new Runnable() {

            @Override
            public void run() {

                String json = null;

                try {
                    json = doGet(sFUrl, nFTimeout);
                } catch (Exception e) {
                    e.printStackTrace();

                    if (FileUtil.fileExists(sFLocalFile)) {
                        try {
                            json = FileUtil.readFile(sFLocalFile, "UTF-8");
                        } catch (Exception e1) {
                            // just skip it
                        }
                    } else {
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                callback.onError("网络不可用");
                            }
                        });
                        return;
                    }

                }

                try {
                    if (!StringUtil.isEmpty(json)) {
                        FileUtil.writeFile(sFLocalFile, json, "UTF-8");
                    }
                } catch (Exception e) {
                    Log.w("RemoteDataService",
							String.format("Write cache data failed: [%s]", sFLocalFile));
                }

                final String fJson = json;

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        callback.onDataLoad(fJson, true);
                    }
                });
            }
        }).start();
    }

	public String getPesisitentFilePathFromURL(String url) {
		// 去掉站点的http前缀
		url = url.replaceFirst("^http[s]?://[^/]+/", "");

		return new File(context.getExternalFilesDir("wcm_persist"), url).getAbsolutePath();
	}

	public String getCacheFilePathFromURL(String url) {
        if(url.indexOf(".jsp") != -1){
            url = url.replaceFirst("^http[s]?://", "").replace("=","_").replace("?","_").replace("&","_").replace(".","_");
        } else  if(url.contains(".css") || url.contains(".js") || url.indexOf(".htm") != -1){
            url = url.replaceFirst("^http[s]?://", "").replace("?","_");
        } else{
            // 去掉站点的http前缀
            url = url.replaceFirst("^http[s]?://", "");
        }
		return new File(context.getApplicationContext().getCacheDir(), url).getAbsolutePath();
	}

	public String loadLocalJson(String url){
		if (StringUtil.isEmpty(url)) {
			return null;
		}

		final String filePath = WCMTools.getPageURL(url);
		String localJSONPath = getCacheFilePathFromURL(filePath);

		if (!FileUtil.fileExists(localJSONPath)) {
			return null;
		}

		try {
			return FileUtil.readFile(localJSONPath, "utf-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
     * 优先加载本地的数据，如果本地不存在，则后面的行为与loadJSON行为一致
     * 
     * @param url
     * @param callback
     */
    public void loadLocalJSON(String url, final IDataAsynCallback callback) {
        if (StringUtil.isEmpty(url)) {
            return;
        }

        // 兼容直接翻斜杠的情况
        final String sFURL = WCMTools.getPageURL(url);

        String sLocalJSONPath = getCacheFilePathFromURL(sFURL);

        // 本地文件不存在，直接读取远程数据
        if (!FileUtil.fileExists(sLocalJSONPath)) {
            this.loadJSON(url, callback);
            return;
        }

        // 读取本地文件
        String sJSONConent;
        try {
            sJSONConent = FileUtil.readFile(sLocalJSONPath, "utf-8");
        } catch (Exception e) {
            // 继续走网络的场景
            this.loadJSON(url, callback);
            return;
        }

        final String sFinalJSONConent = sJSONConent;
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onDataLoad(sFinalJSONConent, false);
            }
        });

        if (!NetUtil.isConntected(context) && Util.isRemoteUrl(url)) {
            return;
        }

        // 继续请求时间戳
        final String sFLmtURL = getLmtPath(sFURL);

        new Thread(new Runnable() {

            @Override
            public void run() {
                String sLocalLmtContent = null;
                String sLocalLmtPath = getCacheFilePathFromURL(sFLmtURL);

                if (FileUtil.fileExists(sLocalLmtPath)) {
                    try {
                        sLocalLmtContent = FileUtil.readFile(sLocalLmtPath, "utf-8");
                        System.out.println("sLocalLmtContent=" + sLocalLmtContent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                String sLmtContent;
                try {
                    sLmtContent = doGet(sFLmtURL, TIME_OUT_4_LMT);
                } catch (Exception e) {
                    e.printStackTrace();
                    // just skip it
                    return;
                }

                if (StringUtil.isEmpty(sLocalLmtContent) || !sLocalLmtContent.equals(sLmtContent)) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataChanged();
                        }
                    });

                }
            }
        }).start();

    }

    /**
     * 请求时间戳 尝试去下载时间戳文件(lmt.txt)，如果时间戳发布变化，则需要下载对应的最新数据文件；
     * 如果时间戳没有变化，则直接读取本地文件；如果网络加载文件失败，则尝试读取本地文件；
     * 
     * @param url
     * @param callback
     */
    public void loadJSON(String url, final IDataAsynCallback callback) {

        if (StringUtil.isEmpty(url)) {
            return;
        }

        // 兼容直接翻斜杠的情况
        final String sFURL = WCMTools.getPageURL(url);

        final String sFLocalJSONPath = getCacheFilePathFromURL(sFURL);

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
        final String sFLmtURL = getLmtPath(sFURL);

        new Thread(new Runnable() {

            @Override
            public void run() {
                String sLocalLmtContent = null;
                String sLocalLmtPath = getCacheFilePathFromURL(sFLmtURL);

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
                    sLmtContent = doGet(sFLmtURL, TIME_OUT_4_LMT);
                    // sLmtContent = doGet(sFLmtURL, 1);

                } catch (Exception e) {
					Log.w(TAG, "can not got lmt data from url: " + sFLmtURL);
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

                    int nTimeout = FileUtil.fileExists(sFLocalJSONPath) ? TIME_OUT_WHILE_FILE_EXISTS
                            : TIME_OUT_WHILE_FILE_NOT_EXISTS;

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

	public String syncDownload(String url){
		if (StringUtil.isEmpty(url)) {
			return null;
		}

		// 兼容直接翻斜杠的情况
		final String sFURL = WCMTools.getPageURL(url);
		final String sFLocalJSONPath = getCacheFilePathFromURL(sFURL);
		final String sFLmtURL = getLmtPath(sFURL);

		String sLocalLmtContent = null;
		String sLocalLmtPath = getCacheFilePathFromURL(sFLmtURL);

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
			sLmtContent = doGet(sFLmtURL, TIME_OUT_4_LMT);

		} catch (Exception e) {
			Log.w(TAG, "can not got lmt data from url: " + sFLmtURL);
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

			int nTimeout = FileUtil.fileExists(sFLocalJSONPath) ? TIME_OUT_WHILE_FILE_EXISTS
					: TIME_OUT_WHILE_FILE_NOT_EXISTS;

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

    // http://219.151.34.68/pub/vtibetmobile/sp/bwcp/
    // http://219.151.34.68/pub/vtibetmobile/sp/bwcp/documents.json
    // http://219.151.34.68/pub/vtibetmobile/sp/bwcp/lmt.txt
    // http://219.151.34.68/pub/vtibetmobile/sp/bwcp/201308/t20130827_79959.json
    // http://219.151.34.68/pub/vtibetmobile/sp/bwcp/201308/t20130827_79959_lmt.txt
    public String getLmtPath(String sUrl) {

        if (sUrl.endsWith("/")) {
            return sUrl + "lmt.txt";
        }

        if (sUrl.endsWith("documents.json")) {
            return sUrl.replaceFirst("documents\\.json$", "lmt.txt");
        }

        if (sUrl.endsWith("channels.json")) {
            return sUrl.replaceFirst("channels\\.json$", "lmt.txt");
        }

        // 细览页面
        return sUrl.replaceFirst("\\.json$", "_lmt.txt");
    }

	private String doGet(String url, int timeOut) throws IOException {
		return Util.getString(context, url, HTTP.UTF_8);
	}
}
