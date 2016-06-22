package com.trs.offline;

import java.util.ArrayList;

import android.content.Context;
import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trs.app.TRSApplication;
import com.trs.util.FileUtil;
import com.trs.wcm.RemoteDataService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.text.TextUtils;
import android.util.Log;
/**
 * @author WLH
 * 2014-8-4
 * 解析html类
 */
public class JsoupUtil {
	
	private static String TAG = "JsoupUtil";
	private static String Prefix = "http://m.vtibet.com/";
    private RemoteDataService rds;
    private Context context;
    private DiscCacheAware discCacheAware;

    public JsoupUtil(Context context){
        this.context = context;
        rds = new RemoteDataService(context);
        discCacheAware = ImageLoader.getInstance().getDiscCache();
    }

	/**
	 * @author WLH
	 * 2014-8-4上午11:21:47
	 * @return ArrayList<String> 获取  html中的所有图片的url
	 */
	public static ArrayList<String> getImgUrls(String result){
		
		if(TextUtils.isEmpty(result)){
			return null;
		}
		
		ArrayList<String> imgUrls = new ArrayList<String>();
		Document doc = Jsoup.parse(result);
		Elements imgs = doc.select("img");
		for (int i = 0; i < imgs.size(); i++) {
			String url = imgs.get(i).attr("src");
			imgUrls.add(url);
			Log.d(TAG, "img url["+i+"]:"+url);
		}
		return imgUrls;
	}
	
	/**
	 * @author WLH
	 * 2014-8-4下午1:34:53
	 * @return ArrayList<String> 获取 html中的所有javascript的url
	 */
	public static ArrayList<String> getScriptUrls(String result){
		if(TextUtils.isEmpty(result)){
			return null;
		}
		ArrayList<String> scriptUrls = new ArrayList<String>();
		Document doc = Jsoup.parse(result);
		Elements scripts = doc.select("script");
		for (int i = 0; i < scripts.size(); i++) {
			String url = scripts.get(i).attr("src");
			if (!TextUtils.isEmpty(url)) {
				scriptUrls.add(Prefix + url);
				Log.d(TAG, "script url[" + i + "]:" + url + " scripts.size:"+ scripts.size());
			}
		}
		return scriptUrls;
	}
	
	/**
	 * 
	 * @author WLH
	 * 2014-8-4下午1:39:50
	 * @return ArrayList<String> 获取 html中的所有link的url
	 */
	public static ArrayList<String> getCssUrls(String result){
		if(TextUtils.isEmpty(result)){
			return null;
		}
		ArrayList<String> cssUrls = new ArrayList<String>();
		Document doc = Jsoup.parse(result);
		Elements css = doc.select("link[href]");
		for (int i = 0; i < css.size(); i++) {
			if(css.get(i).attr("rel").equals("stylesheet")){
				String url = !TextUtils.isEmpty(css.get(i).attr("href")) ? css.get(i).attr("href"): css.get(i).text();
				if(!TextUtils.isEmpty(url)){
					cssUrls.add(Prefix + url);
					Log.d(TAG, "css url["+i+"]:"+url);
				}
			}
		}
		return cssUrls;
	}
	
	/**
	 * @author WLH
	 * 2014-8-4下午2:13:25
	 * 替换网络图片地址为本地图片地址
	 */
	public void modifyImgUrl(String result, String imgurl, String html_Url){
		Document doc = Jsoup.parse(result);
		Elements imgs = doc.select("img");
		boolean isChanged = false;
		for (int i = 0; i < imgs.size(); i++) {
			if(imgurl.equals(imgs.get(i).attr("src"))){
				imgs.get(i).attr("src", "file://"+rds.getCacheFilePathFromURL(imgurl));
				isChanged = true;
				break;
			}
		}
		if(isChanged){
			try {
				FileUtil.writeFile(rds.getCacheFilePathFromURL(html_Url), doc.html(), "utf-8");
				Log.d(TAG, "modify img oldUrl:"+ imgurl+"  localUrl:"+"file://"+rds.getCacheFilePathFromURL(imgurl));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @author WLH
	 * 2014-8-4下午2:14:00
	 * 替换网络script地址为本地地址
	 */
	public void modifyScriptUrl(String result, String scripturl, String html_Url){
		Document doc = Jsoup.parse(result);
		Elements scripturls = doc.select("script");
		boolean isChanged = false;
		String originUrl = scripturl;
		int index = scripturl.indexOf(Prefix);
		if(index != -1){
			originUrl = originUrl.substring(Prefix.length());
			Log.d(TAG, "modify script originUrl:"+ originUrl+ " scripturls.size:"+scripturls.size());
		}
		for (int i = 0; i < scripturls.size(); i++) {
			Log.d(TAG, "modify script originUrl:"+ originUrl+ "script url[" + i + "]:" + scripturls.get(i).attr("src"));
			if(scripturls.get(i).attr("src").equals(originUrl)){
				scripturls.get(i).attr("src", "file://"+rds.getCacheFilePathFromURL(scripturl));
				isChanged = true;
				break;
			}
		}
		if(isChanged){
			try {
				FileUtil.writeFile(rds.getCacheFilePathFromURL(html_Url), doc.html(), "utf-8");
				Log.d(TAG, "modify script oldUrl:"+ scripturl+"  localUrl:"+"file://"+rds.getCacheFilePathFromURL(scripturl));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @author WLH
	 * 2014-8-4下午2:14:25
	 * 替换网络css地址为本地css地址
	 */
	public void modifyCssUrl(String result, String cssurl, String html_Url){
		Document doc = Jsoup.parse(result);
		Elements cssurls = doc.select("link[href]");
		boolean isChanged = false;
		String originUrl = cssurl;
		int index =  cssurl.indexOf(Prefix.length());
		if(index != -1){
			originUrl = originUrl.substring(index);
			Log.d(TAG, "modify css originUrl:"+ originUrl);
		}
		for (int i = 0; i < cssurls.size(); i++) {
			if(cssurls.get(i).attr("rel").equals("stylesheet")){
				String url = !TextUtils.isEmpty(cssurls.get(i).attr("href")) ? cssurls.get(i).attr("href"): cssurls.get(i).text();
				Log.d(TAG, "modify css url["+i+"]:"+url +"  originUrl:"+originUrl);
				if(originUrl.equals(url)){
					cssurls.get(i).attr("href", "file://"+rds.getCacheFilePathFromURL(cssurl));
					isChanged = true;
					break;
				}
			}
		}
		if(isChanged){
			try {
				FileUtil.writeFile(rds.getCacheFilePathFromURL(html_Url), doc.html(), "utf-8");
				Log.d(TAG, "modify css oldUrl:"+ cssurl+"  localUrl:"+"file://"+rds.getCacheFilePathFromURL(cssurl));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void modifyUrl(String result,String html_Url){
		Document doc = Jsoup.parse(result);
		Elements imgs = doc.select("img");
		for (int i = 0; i < imgs.size(); i++) {
			if(!TextUtils.isEmpty(imgs.get(i).attr("src"))){
//				imgs.get(i).attr("src", "file://"+rds.getCacheFilePathFromURL(imgs.get(i).attr("src")));
                imgs.get(i).attr("src", "file://"+discCacheAware.get(imgs.get(i).attr("src")).getAbsolutePath());
			}
		}
		Elements scripturls = doc.select("script");
		for (int i = 0; i < scripturls.size(); i++) {
			String url = scripturls.get(i).attr("src");
			if(!TextUtils.isEmpty(url)){
				scripturls.get(i).attr("src", "file://"+rds.getCacheFilePathFromURL(scripturls.get(i).attr("src")));
				Log.d(TAG, "modify script url[" + i + "]:" + url);
			}
			
		}
		Elements cssurls = doc.select("link[href]");
		for (int i = 0; i < cssurls.size(); i++) {
			if(cssurls.get(i).attr("rel").equals("stylesheet")){
				String url = !TextUtils.isEmpty(cssurls.get(i).attr("href")) ? cssurls.get(i).attr("href"): cssurls.get(i).text();
				if(!TextUtils.isEmpty(url)){
					Log.d(TAG, "modify css url["+i+"]:"+url);
					cssurls.get(i).attr("href", "file://"+rds.getCacheFilePathFromURL(Prefix + url));
				}
			}
		}
		try {
			FileUtil.writeFile(rds.getCacheFilePathFromURL(html_Url),doc.html(), "utf-8");
			Log.d(TAG,"modify html..........");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
