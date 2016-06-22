package com.trs.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.baidu.frontia.FrontiaApplication;
import com.froyo.commonjar.application.BaseApplication;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.trs.bus.BusLineInfo;
import com.trs.mobile.R;
import com.trs.tasks.CheckUpdateTask;
import com.trs.types.FirstClassMenu;
import com.trs.util.log.LocalFileUncaughtExceptionHandler;
import com.trs.weather.WeatherInfo;
import net.endlessstudio.util.Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by john on 13-11-15.
 */
public abstract class TRSApplication extends BaseApplication {
	private static TRSApplication sInstance;
	private FirstClassMenu mFirstClassMenu;
	public static final String KEY_FIRST_CLASS_MENU = "first_class_menu";

	public static TRSApplication app(){
		return sInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		sInstance = this;

		Thread.setDefaultUncaughtExceptionHandler(new LocalFileUncaughtExceptionHandler(this,
				Thread.getDefaultUncaughtExceptionHandler()));

		initializeImageLoader();

		ApplicationConfig.getInstance().initAppConfig(this, getApplicationConfigUrl(), new ApplicationConfig.Listener() {
			@Override
			public void onSuccess() {
				//do nothing
			}

			@Override
			public void onError() {
				if(!ApplicationConfig.getInstance().hasCachedValue(TRSApplication.this)){
					Toast.makeText(TRSApplication.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
				}
			}
		});

		//load saved values
		loadWeatherInfo();

		loadFirstClassMenu();
//		PushAPI.initialize(this);
//        FrontiaApplication.initFrontiaApplication(getApplicationContext());
		ViewDisplayer.initialize(this);

		FragmentCache.getInstance().setActivityManager((ActivityManager) getSystemService(ACTIVITY_SERVICE));
	}

	private void initializeImageLoader(){
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this);
		builder.threadPriority(Thread.NORM_PRIORITY - 2);
		builder.denyCacheImageMultipleSizesInMemory();
		builder.discCacheFileNameGenerator(new Md5FileNameGenerator());
		builder.memoryCache(new LRULimitedMemoryCache(3 * 1024 * 1024));
		builder.tasksProcessingOrder(QueueProcessingType.FIFO);
		builder.writeDebugLogs();
		//处理
		// file://android_asset/xxx
		// file://androidraw/xxx
		// assets://xxx
		// raw://xxx
		//的情况
		builder.imageDownloader(new BaseImageDownloader(this){
			@Override
			public InputStream getStream(String imageUri, Object extra) throws IOException {
				if(!imageUri.toLowerCase().startsWith("http")){
					try{
						InputStream is = Util.getStream(TRSApplication.this, imageUri);
						if(is != null){
							return is;
						}
					}
					catch (IllegalArgumentException e){ }
				}

				return super.getStream(imageUri, extra);
			}
		});
		ImageLoaderConfiguration config = builder.build();
		ImageLoader.getInstance().init(config);
	}

	@Override
	public File getCacheDir() {
		Log.i("getCacheDir", "cache sdcard state: " + Environment.getExternalStorageState());
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File cacheDir = getExternalCacheDir();
			if(cacheDir != null && (cacheDir.exists() || cacheDir.mkdirs())){
				Log.i("getCacheDir", "cache dir: " + cacheDir.getAbsolutePath());
				return cacheDir;
			}
		}

		File cacheDir = super.getCacheDir();
		Log.i("getCacheDir", "cache dir: " + cacheDir.getAbsolutePath());

		return cacheDir;
	}

	public File getTempDir(){
		File tempDir = new File(getCacheDir(), "temp");
		if(tempDir != null){
			tempDir.mkdirs();
		}
		return tempDir;
	}

	/*********************** Version control ************************/
	public String getVersionName(){
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		String version = packInfo.versionName;

		return version;
	}

	public int getVersionCode(){
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		int version = packInfo.versionCode;

		return version;
	}

	public boolean needUpdate(CheckUpdateTask.CheckUpdateResult result){
		String version = result.aversion;

		boolean isVersionName = version.contains(".");
		if(isVersionName){
			return needUpdate(version);
		}
		else{
			try{
				return needUpdate(Integer.valueOf(version));
			}
			catch(NumberFormatException e){
				return false;
			}
		}
	}

	public boolean needUpdate(String versionName){
		String[] versions = versionName.split("\\.");
		String[] thisVersions = getVersionName().split("\\.");

		try{
			if(versions.length != thisVersions.length){
				return true;
			}

			for(int i = 0; i < versions.length; i ++){
				if(Integer.valueOf(versions[i]) > Integer.valueOf(thisVersions[i])){
					return true;
				}
			}
		}
		catch(NumberFormatException e){
			return true;
		}

		return false;
	}

	public boolean needUpdate(int versionCode){
		return versionCode > getVersionCode();
	}


	/******************* About first class menu ************************/
	public FirstClassMenu getFirstClassMenu(){
 		return mFirstClassMenu;
	}

	public void setFirstClassMenu(FirstClassMenu mFirstClassMenu) {
		this.mFirstClassMenu = mFirstClassMenu;
		saveFirstClassMenu();
	}

	private void saveFirstClassMenu(){
		if(mFirstClassMenu != null){
			SharedPreferences.Editor editor = getEditor();
			editor.putString(KEY_FIRST_CLASS_MENU, new Gson().toJson(mFirstClassMenu));
			editor.commit();
		}
	}

	private void loadFirstClassMenu(){
		SharedPreferences sp = getSp();
		if(sp.contains(KEY_FIRST_CLASS_MENU)){
			mFirstClassMenu = new Gson().fromJson(sp.getString(KEY_FIRST_CLASS_MENU, "{}"), FirstClassMenu.class);
		}
	}

	/****************** About weather *******************/
	private WeatherInfo mWeatherInfo;
	public static final String ACTION_WEATHER_INFO_UPDATED = "com.trs.action.weather_info_updated";
	public static final String EXTRA_WEATHER_INFO = "weather_info";

	public static final String KEY_WEATHER_INFO = "weather_info";

    /******************About bus*************************/
    private BusLineInfo mBusLineInfo;
    public static final String KEY_BUS_LINE_INFO = "bus_line_info";

	abstract public static class WeatherInfoUpdatedReceiver extends BroadcastReceiver{
		@Override
		final public void onReceive(Context context, Intent intent) {
			WeatherInfo info = (WeatherInfo) intent.getSerializableExtra(EXTRA_WEATHER_INFO);
			onReceive(context, info);
		}

		public void register(Context context){
			IntentFilter filter = new IntentFilter(ACTION_WEATHER_INFO_UPDATED);
			context.registerReceiver(this, filter);
		}

		public void unregister(Context context){
			context.unregisterReceiver(this);
		}

		protected abstract void onReceive(Context context, WeatherInfo info);

	}
	public WeatherInfo getWeatherInfo() {
		return mWeatherInfo;
	}

    public void setBusLineInfo(BusLineInfo info){
        this.mBusLineInfo = info;
        saveBusLineInfo();
    }

    public BusLineInfo getBusLineInfo(){
        return mBusLineInfo;
    }

	public void setWeatherInfo(WeatherInfo mWeatherInfo) {
		this.mWeatherInfo = mWeatherInfo;

		saveWeatherInfo();

		Intent intent = new Intent(ACTION_WEATHER_INFO_UPDATED);
		intent.putExtra(EXTRA_WEATHER_INFO, mWeatherInfo);
		sendBroadcast(intent);
	}

	private void saveWeatherInfo(){
		if(mWeatherInfo != null){
			SharedPreferences.Editor editor = getEditor();
			editor.putString(KEY_WEATHER_INFO, new Gson().toJson(mWeatherInfo));
			editor.commit();
		}
	}

    private void saveBusLineInfo(){
        if(mBusLineInfo != null){
            SharedPreferences.Editor editor = getEditor();
            editor.putString(KEY_BUS_LINE_INFO,new Gson().toJson(mBusLineInfo));
            editor.commit();
        }
    }

	private void loadWeatherInfo(){
		SharedPreferences sp = getSp();
		if(sp.contains(KEY_WEATHER_INFO)){
			String jsonString = sp.getString(EXTRA_WEATHER_INFO, "{}");
			mWeatherInfo = new Gson().fromJson(jsonString, WeatherInfo.class);
		}
	}

	private SharedPreferences getSp(){
		SharedPreferences sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		return sp;
	}

	private SharedPreferences.Editor getEditor(){
		return getSp().edit();
	}

	/***************** About URL ***********************/
	public static enum SourceType{
		JSON, XML,SOAP
	}
	abstract public String getApplicationConfigUrl();
	abstract public SourceType getSourceType();
	abstract public String getFirstClassUrl();

}
