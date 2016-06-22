package com.trs.main.slidingmenu;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.*;
import com.laomo.zxing.CaptureActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trs.app.TRSApplication;
import com.trs.collect.CollectActivity;
import com.trs.constants.Constants;
import com.trs.frontia.FrontiaAPI;
import com.trs.mobile.BuildConfig;
import com.trs.mobile.R;
import com.trs.search.SearchActivity;
import com.trs.tasks.CheckUpdateTask;
import com.trs.util.AsyncTask;
import com.trs.util.Util;
import com.trs.util.log.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 14-2-20.
 */
public class SettingFragment extends Fragment {
	public static final String TAG = "SettingFragment";
    public static String NIGHT_MODE_ACTION = "com.trs.setting.nightmode";
	private View mView;
	private CheckBox mPushSwitch;
	private CheckBox mNightModeSwitch;
	private RadioGroup mFontSize;
	private TextView mVersion;
    private View mClearCache;
    private TextView mCacheText;

    private ClearCacheTask mClearCacheTask;
    private GetFileSizeTask mGetFileSizeTask;

    @Override
    public void onDestroy() {
        if(mGetFileSizeTask != null){
            mGetFileSizeTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.setting, null);
		mPushSwitch = (CheckBox) mView.findViewById(R.id.push_switch);
		mNightModeSwitch = (CheckBox) mView.findViewById(R.id.night_mode_switch);
		mFontSize = (RadioGroup) mView.findViewById(R.id.setting_font);
		mVersion = (TextView) mView.findViewById(R.id.version);

        mCacheText = (TextView)mView.findViewById(R.id.cachetext);
        mClearCache = mView.findViewById(R.id.btn_clear_cache);

        mGetFileSizeTask = new GetFileSizeTask();
        mGetFileSizeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getCacheDirs());

		if(BuildConfig.DEBUG){
			mVersion.setVisibility(View.VISIBLE);
			mVersion.setText(String.format("v%s", TRSApplication.app().getVersionName()));
		}

		//mPushSwitch.setChecked(PushAPI.isEnabled(getActivity()));
        final FrontiaAPI frontia = FrontiaAPI.getInstance(getActivity().getApplicationContext());
        mPushSwitch.setChecked(frontia.isPushWorking());

		mPushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					//PushAPI.open(getActivity());
                    frontia.startPush();
				}
				else{
//					PushAPI.close(getActivity());
                    frontia.stopPush();
				}
			}
		});

        if(AppSetting.getInstance(getActivity()).isNightMode()){
            mNightModeSwitch.setChecked(true);
        }else{
            mNightModeSwitch.setChecked(false);
        }
		mNightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				AppSetting.getInstance(getActivity()).setNightMode(isChecked);
                    Intent intent = new Intent(NIGHT_MODE_ACTION);
                    getActivity().sendBroadcast(intent);
			}
		});

		int fontSizeCheckId;
		switch(AppSetting.getInstance(getActivity()).getFontSize()){
			case Small:
				fontSizeCheckId = R.id.setting_font_m;
				break;
			case Large:
				fontSizeCheckId = R.id.setting_font_l;
				break;
			default:
				fontSizeCheckId = R.id.setting_font_m;
				break;
		}
		mFontSize.check(fontSizeCheckId);
		mFontSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				AppSetting.FontSize fontSize;
				if(checkedId == R.id.setting_font_s){
					fontSize = AppSetting.FontSize.Small;
				}
				else if(checkedId == R.id.setting_font_l){
					fontSize = AppSetting.FontSize.Large;
				}
				else{
					fontSize = AppSetting.FontSize.Medium;
				}

				AppSetting.getInstance(getActivity()).setFontSize(fontSize);
			}
		});

		mView.findViewById(R.id.search_keyword).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SearchActivity.class);
				startActivity(intent);
			}
		});

		mView.findViewById(R.id.btn_feedback).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					Intent intent = new Intent();
					intent.setClassName(getActivity(), Constants.ACTION_FEEDBACK);
					startActivity(intent);
				}
				catch(ActivityNotFoundException e){
					Log.w("SettingFragment", String.format("Activity %s not found", Constants.ACTION_FEEDBACK));
				}
			}
		});

		mView.findViewById(R.id.btn_collect).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CollectActivity.class);
				startActivity(intent);
			}
		});

        mView.findViewById(R.id.btn_recommend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri= "smsto: ";
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
                intent.putExtra("sms_body", "想知道最近发生哪些大事吗？想知道谁又变“网神”了吗？开电脑看资讯什么的都弱爆了，" +
                        "“浙江金华手机APP”走起！筒子们，跟我一起来关注吧！");
                intent.putExtra("compose_mode", true);
                try{
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    if(getActivity() != null){
                        Toast.makeText(getActivity(),"抱歉，暂无短信功能，无法推荐给朋友。",Toast.LENGTH_LONG).show();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("提示");
                        builder.setMessage("抱歉，暂无短信功能，无法推荐给朋友。");
                        builder.setPositiveButton(R.string.common_sure,new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.show();
                    }
                }
            }
        });

		mView.findViewById(R.id.btn_scan).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                startActivity(new Intent(getActivity(), CaptureActivity.class));
			}
		});

		mView.findViewById(R.id.btn_clear_cache).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

        mView.findViewById(R.id.btn_clear_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mClearCacheTask == null || (mClearCacheTask.getStatus() != AsyncTask.Status.RUNNING && mClearCacheTask.getStatus() != AsyncTask.Status.PENDING)){
                    mClearCacheTask = new ClearCacheTask();
                    mClearCacheTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getCacheDirs());
                    ImageLoader.getInstance().getMemoryCache().clear();
                }
            }
        });

		mView.findViewById(R.id.btn_checkupdate).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckUpdateTask task = new CheckUpdateTask(getActivity()){
					@Override
					public void onIsLatest(CheckUpdateResult result) {
						super.onIsLatest(result);
						Toast.makeText(getActivity(), "已经是最新版本", Toast.LENGTH_LONG).show();
					}
				};
				task.execute();
			}
		});

		mView.findViewById(R.id.btn_about).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					Intent intent = new Intent();
					intent.setClassName(getActivity(), Constants.ACTION_ABOUT);
					startActivity(intent);
				}
				catch(ActivityNotFoundException e){
					Log.w(TAG, String.format("Activity action [%s] not found", Constants.ACTION_ABOUT));
					e.printStackTrace();
				}
			}
		});

		return mView;
	}

    private class ClearCacheTask extends AsyncTask<List<File>, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
			WebView webView = new WebView(getActivity());
			webView.clearCache(true);

			ImageLoader.getInstance().getDiscCache().clear();
			ImageLoader.getInstance().getMemoryCache().clear();
            mClearCache.setEnabled(false);
            mCacheText.setText("[正在清理...]");
        }

        @Override
        protected Boolean doInBackground(List<File>... params) {
            List<File> fileList = params[0];
            for(File f: fileList){
                if(f.isDirectory()){
                    for(File child: f.listFiles()){
                        clearFile(child);
                    }
                }
                else{
                    clearFile(f);
                }
            }

            return null;
        }

        private void clearFile(File file){
            if(file != null && file.exists() && !isCancelled()){
                if(file.isFile()){
                    file.delete();
                }
                else if(file.isDirectory()){
                    for(File f: file.listFiles()){
                        clearFile(f);
                    }

                    file.delete();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mClearCache.setEnabled(true);
            mCacheText.setText("[0.00M]");
        }

        @Override
        protected void onCancelled() {
            mClearCache.setEnabled(true);
            mCacheText.setText("[清理被取消]");
            super.onCancelled();
        }
    }

    private class GetFileSizeTask extends AsyncTask<List<File>, Long, Long>{

        private Util.GetFileSizeHandler mHandler;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mClearCache.setEnabled(false);
            mCacheText.setText(String.format("[%s]", "正在检查..."));
        }

        @Override
        protected Long doInBackground(List<File>... params) {
            final ArrayList<String> checkedDir = new ArrayList<String>();
            final int UPDATE_COUNT = 5;
            mHandler = new Util.GetFileSizeHandler() {
                @Override
                public void onFile(File file, int fileCount, long totalSize) {
                    if(file.isDirectory()){
                        checkedDir.add(file.getAbsolutePath());
                    }

                    if(fileCount % UPDATE_COUNT == 0){
                        publishProgress(totalSize);
                    }

                    super.onFile(file, fileCount, totalSize);
                }
            };

            List<File> dirs = params[0];
            for(File f: dirs){
                if(checkedDir.contains(f.getAbsolutePath())){
                    continue;
                }

                Util.getFileSize(f, mHandler);
            }

            return mHandler.getFileSize();
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            double dSize = (double)values[0] / 1024.0d / 1024.0d;
            mCacheText.setText(String.format("[%s%.2fM]", "正在检查...", formatFileSize(dSize)));
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            mClearCache.setEnabled(true);
            double dSize = (double)result / 1024.0d / 1024.0d;
            mCacheText.setText(String.format("[%.2fM]", formatFileSize(dSize)));
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mClearCache.setEnabled(true);
            mHandler.cancel();
            mCacheText.setText(String.format("[%s]", "未知"));
        }
    }

    private List<File> getCacheDirs(){
        ArrayList<File> fileList = new ArrayList<File>();
        fileList.add(getActivity().getCacheDir());
        fileList.add(getActivity().getExternalCacheDir());
        return fileList;
    }

    private double formatFileSize(double size){
        if(size < 0.01d){
            size = 0d;
        }

        return size;
    }
}
