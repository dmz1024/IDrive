package com.ccpress.izijia.activity.offline;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.MapView;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapManager.OfflineMapDownloadListener;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.offline.OfflineDownloadedAdapter;
import com.ccpress.izijia.activity.offline.OfflineListAdapter;
import com.ccpress.izijia.activity.portal.AboutActivity;
import com.ccpress.izijia.constant.Const;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.froyo.commonjar.xutils.view.annotation.event.OnClick;

/**
 * 
 * @Des: 我的定制
 * @author Rhino
 * @version V1.0
 * @created 2015年5月6日 下午3:25:22
 */
public class OfflineMapActivity extends BaseActivity implements
		OfflineMapDownloadListener {

	@ViewInject(R.id.tv_view)
	private TextView tv_view;

	@ViewInject(R.id.tv_line)
	private TextView tv_line;

	@ViewInject(R.id.vp_pager)
	private ViewPager vp_pager;

	@ViewInject(R.id.progress)
	private ProgressBar progress;

	@ViewInject(R.id.tv_last)
	private TextView tv_last;

	@ViewInject(R.id.tv_total)
	private TextView tv_total;

	@ViewInject(R.id.rl_bottom)
	private RelativeLayout rl_bottom;

	private PageAdapter pageAdapter;

	private OfflineMapManager amapManager = null;// 离线地图下载控制器
	private List<OfflineMapProvince> provinceList = new ArrayList<OfflineMapProvince>();// 保存一级目录的省直辖市
	private ExpandableListView mAllOfflineMapList;
	private ListView mDownLoadedList;

	private OfflineListAdapter adapter;
	private OfflineDownloadedAdapter mDownloadedAdapter;

	private MapView mapView;

	// 刚进入该页面时初始化弹出的dialog
	private ProgressDialog initDialog;

	/**
	 * 更新所有列表
	 */
	private final static int UPDATE_LIST = 0;
	/**
	 * 显示toast log
	 */
	private final static int SHOW_MSG = 1;

	private final static int DISMISS_INIT_DIALOG = 2;
	private final static int SHOW_INIT_DIALOG = 3;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_LIST:

				break;
			case SHOW_MSG:
				break;

			case DISMISS_INIT_DIALOG:
				initDialog.dismiss();
				handler.sendEmptyMessage(UPDATE_LIST);
				break;
			case SHOW_INIT_DIALOG:
				if (initDialog != null) {
					initDialog.show();

				}

				break;

			default:
				break;
			}
		}

	};

	@Override
	public void doBusiness() {
		mapView = new MapView(this);
		sdStutus();
		View provinceContainer = makeView(R.layout.offline_province_listview);
		mAllOfflineMapList = (ExpandableListView) provinceContainer
				.findViewById(R.id.province_download_list);

		amapManager = new OfflineMapManager(this, this);

		initProvinceListAndCityMap();

		// adapter = new OfflineListAdapter(provinceList, cityMap, amapManager,
		// OfflineMapActivity.this);
		adapter = new OfflineListAdapter(provinceList, amapManager,
				OfflineMapActivity.this,rl_bottom);
		// 为列表绑定数据源
		mAllOfflineMapList.setAdapter(adapter);
		// adapter实现了扩展列表的展开与合并监听
		mAllOfflineMapList.setOnGroupCollapseListener(adapter);
		mAllOfflineMapList.setOnGroupExpandListener(adapter);
		mAllOfflineMapList.setGroupIndicator(null);

		View more = makeView(R.layout.offline_downloaded_list);
		mDownLoadedList = (ListView) more
				.findViewById(R.id.downloaded_map_list);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		mDownLoadedList.setLayoutParams(params);
		mDownloadedAdapter = new OfflineDownloadedAdapter(this, amapManager,rl_bottom);
		mDownLoadedList.setAdapter(mDownloadedAdapter);

		List<View> views = new ArrayList<View>();
		views.add(provinceContainer);
		views.add(more);
		pageAdapter = new PageAdapter(activity, views);
		vp_pager.setAdapter(pageAdapter);
		vp_pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				changeBtn(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		vp_pager.setCurrentItem(0);
	}

	/**
	 * sdk内部存放形式为<br>
	 * 省份 - 各自子城市<br>
	 * 北京-北京<br>
	 * ...<br>
	 * 澳门-澳门<br>
	 * 概要图-概要图<br>
	 * <br>
	 * 修改一下存放结构:<br>
	 * 概要图-概要图<br>
	 * 直辖市-四个直辖市<br>
	 * 港澳-澳门香港<br>
	 * 省份-各自子城市<br>
	 */

	/**
	 * 手机SD卡空间显示
	 *
	 *hexulan
	 */
	private void sdStutus(){
		File path= Environment.getExternalStorageDirectory();
		StatFs statFs=new StatFs(path.getPath());
		long blocksize=statFs.getBlockSize();
		long totalblocks=statFs.getBlockCount();
		long availableblocks=statFs.getAvailableBlocks();

		//计算SD卡的空间大小
		long totalsize=blocksize*totalblocks;
		long availablesize=availableblocks*blocksize;

		//转化为可以显示的字符串
		String totalsize_str= Formatter.formatFileSize(this, totalsize);
		String availablesize_strString=Formatter.formatFileSize(this, availablesize);

		tv_last.setText(availablesize_strString);
		tv_total.setText(totalsize_str);
	}

	private void initProvinceListAndCityMap() {

		List<OfflineMapProvince> lists = amapManager
				.getOfflineMapProvinceList();

		provinceList.add(null);
		provinceList.add(null);
		provinceList.add(null);
		// 添加3个null 以防后面添加出现 index out of bounds

		ArrayList<OfflineMapCity> cityList = new ArrayList<OfflineMapCity>();// 以市格式保存直辖市、港澳、全国概要图
		ArrayList<OfflineMapCity> gangaoList = new ArrayList<OfflineMapCity>();// 保存港澳城市
		ArrayList<OfflineMapCity> gaiyaotuList = new ArrayList<OfflineMapCity>();// 保存概要图

		for (int i = 0; i < lists.size(); i++) {
			OfflineMapProvince province = lists.get(i);
			if (province.getCityList().size() != 1) {
				// 普通省份
				provinceList.add(i + 3, province);
				// cityMap.put(i + 3, cities);
			} else {
				String name = province.getProvinceName();
				if (name.contains("香港")) {
					gangaoList.addAll(province.getCityList());
				} else if (name.contains("澳门")) {
					gangaoList.addAll(province.getCityList());
				} else if (name.contains("全国概要图")) {
					gaiyaotuList.addAll(province.getCityList());
				} else {
					// 直辖市
					cityList.addAll(province.getCityList());
				}
			}
		}

		// 添加，概要图，直辖市，港口
		OfflineMapProvince gaiyaotu = new OfflineMapProvince();
		gaiyaotu.setProvinceName("概要图");
		gaiyaotu.setCityList(gaiyaotuList);
		provinceList.set(0, gaiyaotu);// 使用set替换掉刚开始的null

		OfflineMapProvince zhixiashi = new OfflineMapProvince();
		zhixiashi.setProvinceName("直辖市");
		zhixiashi.setCityList(cityList);
		provinceList.set(1, zhixiashi);

		OfflineMapProvince gaogao = new OfflineMapProvince();
		gaogao.setProvinceName("港澳");
		gaogao.setCityList(gangaoList);
		provinceList.set(2, gaogao);

		// cityMap.put(0, gaiyaotuList);// 在HashMap中第0位置添加全国概要图
		// cityMap.put(1, cityList);// 在HashMap中第1位置添加直辖市
		// cityMap.put(2, gangaoList);// 在HashMap中第2位置添加港澳

	}

	/**
	 * 暂停所有下载和等待
	 */
	private void stopAll() {
		if (amapManager != null) {
			amapManager.stop();
		}
	}

	/**
	 * 继续下载所有暂停中
	 */
	private void startAllInPause() {
		if (amapManager == null) {
			return;
		}
		for (OfflineMapCity mapCity : amapManager.getDownloadingCityList()) {
			if (mapCity.getState() == OfflineMapStatus.PAUSE) {
				try {
					amapManager.downloadByCityName(mapCity.getCity());
				} catch (AMapException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 取消所有<br>
	 * 即：删除下载列表中除了已完成的所有<br>
	 * 会在OfflineMapDownloadListener.onRemove接口中回调是否取消（删除）成功
	 */
	private void cancelAll() {
		if (amapManager == null) {
			return;
		}
		for (OfflineMapCity mapCity : amapManager.getDownloadingCityList()) {
			if (mapCity.getState() == OfflineMapStatus.PAUSE) {
				amapManager.remove(mapCity.getCity());
			}
		}
	}

	private void logList() {
		ArrayList<OfflineMapCity> list = amapManager.getDownloadingCityList();

		for (OfflineMapCity offlineMapCity : list) {
			Log.i("amap-city-loading: ", offlineMapCity.getCity() + ","
					+ offlineMapCity.getState());
		}

		ArrayList<OfflineMapCity> list1 = amapManager
				.getDownloadOfflineMapCityList();

		for (OfflineMapCity offlineMapCity : list1) {
			Log.i("amap-city-loaded: ", offlineMapCity.getCity() + ","
					+ offlineMapCity.getState());
		}
	}

	/**
	 * 离线地图下载回调方法
	 */
	@Override
	public void onDownload(int status, int completeCode, String downName) {

		switch (status) {
		case OfflineMapStatus.SUCCESS:
			// changeOfflineMapTitle(OfflineMapStatus.SUCCESS, downName);
			adapter.notifyDataSetChanged();
			mDownloadedAdapter.notifyDataSetChanged();
			break;
		case OfflineMapStatus.LOADING:
			Log.d("amap-download", "download: " + completeCode + "%" + ","
					+ downName);
			// changeOfflineMapTitle(OfflineMapStatus.LOADING, downName);
			adapter.notifyDataSetChanged();
			mDownloadedAdapter.notifyDataSetChanged();
			break;
		case OfflineMapStatus.UNZIP:
			Log.d("amap-unzip", "unzip: " + completeCode + "%" + "," + downName);
			// changeOfflineMapTitle(OfflineMapStatus.UNZIP);
			// changeOfflineMapTitle(OfflineMapStatus.UNZIP, downName);
			break;
		case OfflineMapStatus.WAITING:
			Log.d("amap-waiting", "WAITING: " + completeCode + "%" + ","
					+ downName);
			break;
		case OfflineMapStatus.PAUSE:
			Log.d("amap-pause", "pause: " + completeCode + "%" + "," + downName);
			break;
		case OfflineMapStatus.STOP:
			break;
		case OfflineMapStatus.ERROR:
			Log.e("amap-download", "download: " + " ERROR " + downName);
			break;
		case OfflineMapStatus.EXCEPTION_AMAP:
			Log.e("amap-download", "download: " + " EXCEPTION_AMAP " + downName);
			break;
		case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:
			Log.e("amap-download", "download: " + " EXCEPTION_NETWORK_LOADING "
					+ downName);
			Toast.makeText(OfflineMapActivity.this, "网络异常", Toast.LENGTH_SHORT)
					.show();
			amapManager.pause();
			break;
		case OfflineMapStatus.EXCEPTION_SDCARD:
			Log.e("amap-download", "download: " + " EXCEPTION_SDCARD "
					+ downName);
			break;
		default:
			break;
		}

		handler.sendEmptyMessage(UPDATE_LIST);

	}

	@Override
	public void onCheckUpdate(boolean hasNew, String name) {
		Message message = new Message();
		message.what = SHOW_MSG;
		message.obj = "CheckUpdate " + name + " : " + hasNew;
		handler.sendMessage(message);
	}

	@Override
	public void onRemove(boolean success, String name, String describe) {
		handler.sendEmptyMessage(UPDATE_LIST);

		Message message = new Message();
		message.what = SHOW_MSG;
		message.obj = "onRemove " + name + " : " + success + " , " + describe;
		handler.sendMessage(message);

	}

	@OnClick(R.id.tv_view)
	void clickView(View view) {
		vp_pager.setCurrentItem(0);
	}

	@OnClick(R.id.tv_line)
	void clickLine(View view) {
		vp_pager.setCurrentItem(1);
		startAllInPause();
	}
	
	@OnClick(R.id.tv_right)
	void help(View view){
		skip(AboutActivity.class,"常见问题" ,Const.ABOUT);
	}

	private void changeBtn(int postion) {
		if (postion == 0) {
			adapter.notifyDataSetChanged();
		} else {
			mDownloadedAdapter.notifyDataChange();
		}
		if (postion == 0) {
			tv_view.setTextColor(Color.parseColor("#50BBDB"));
			tv_view.setBackgroundColor(Color.parseColor("#ffffff"));

			tv_line.setTextColor(Color.parseColor("#ffffff"));
			// tv_line.setBackgroundColor(Color.parseColor("#50BBDB"));
			tv_line.setBackgroundDrawable(getDrawableRes(R.drawable.mystyle_title_right_bg));
		} else {
			tv_line.setTextColor(Color.parseColor("#50BBDB"));
			tv_line.setBackgroundColor(Color.parseColor("#ffffff"));

			tv_view.setTextColor(Color.parseColor("#ffffff"));
			// tv_view.setBackgroundColor(Color.parseColor("#50BBDB"));
			tv_view.setBackgroundDrawable(getDrawableRes(R.drawable.mystyle_title_left_bg));
		}
	}

	@OnClick(R.id.iv_back)
	void back(View view) {
		finish();
	}

	@Override
	protected void onDestroy() {
		if (mapView != null) {
			mapView.onDestroy();
		}
		if (amapManager != null) {
			amapManager.destroy();
		}

		if (initDialog != null) {
			initDialog.dismiss();
			initDialog.cancel();
		}
		super.onDestroy();
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_map;
	}

	public class PageAdapter extends PagerAdapter {

		private List<View> viewDatas;

		private BaseActivity act;

		public PageAdapter(BaseActivity act, List<View> viewDatas) {
			this.viewDatas = viewDatas;
			this.act = act;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			container.addView(viewDatas.get(position), 0);
			return viewDatas.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(viewDatas.get(position));
		}

		@Override
		public int getCount() {
			return viewDatas.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		public void removeAll() {
			viewDatas.clear();
			notifyDataSetChanged();
		}

		public void addDatas(List<View> views) {
			viewDatas.addAll(views);
			notifyDataSetChanged();
		}

	}
}
