package com.trs.app;

import android.app.Activity;
import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.froyo.commonjar.utils.AppUtils;
import com.trs.fragment.AbsTRSFragment;
import com.trs.main.slidingmenu.AppSetting;
import com.trs.main.slidingmenu.SettingFragment;
import com.trs.mobile.R;
import com.trs.view.TopBar;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;

/**
 * Created by john on 13-11-15.
 */
public abstract class TRSFragmentActivity extends FragmentActivity {
	private Context mContext = this;
	public static final String EXTRA_TITLE = "title";
	private TRSBaseActivityBroadcastReceiver mReceiver;
	private DisplayMetrics metric;
	private TopBar mTopBar;
	private Handler mHandler = new Handler(this);
	private Fragment mFragment;
	private View mNightView;

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionName = intent.getAction();
			if (actionName.equals(SettingFragment.NIGHT_MODE_ACTION)) {
				updateNightModeState();
			}
		}
	};

	private void updateNightModeState() {
		if (AppSetting.getInstance(mContext).isNightMode()) {
			showNightView();
		} else {
			hideNightView();
		}
	}

	public void registerBroadcastReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SettingFragment.NIGHT_MODE_ACTION);
		registerReceiver(broadcastReceiver, intentFilter);
	}

	private void showNightView() {
		if (mNightView == null) {
			WindowManager localWindowManager = getWindowManager();
			WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
			mNightView = new View(getApplicationContext());
			mNightView.setFocusable(false);
			mNightView.setFocusableInTouchMode(false);
			mNightView.setBackgroundColor(Color.argb(153, 0, 8, 13));
			localLayoutParams.type = 2006;
			localLayoutParams.flags = 280;
			localLayoutParams.format = 1;
			localLayoutParams.gravity = 51;
			localLayoutParams.x = 0;
			localLayoutParams.y = 0;
			localLayoutParams.width = -1;
			localLayoutParams.height = -1;
			localWindowManager.addView(this.mNightView, localLayoutParams);
		}
	}

	private void hideNightView() {
		if (mNightView != null) {
			WindowManager localWindowManager = getWindowManager();
			localWindowManager.removeView(mNightView);
			mNightView = null;
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.setDebugMode(true);
		mReceiver = new TRSBaseActivityBroadcastReceiver(this);
		mReceiver.onActivityCreated();
		registerBroadcastReceiver();

		metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mFragment = createFragment();
				if (mFragment != null) {
					getSupportFragmentManager().beginTransaction().replace(getFragmentContainerID(), mFragment).commit();
					if (mFragment instanceof AbsTRSFragment) {
						((AbsTRSFragment) mFragment).notifyDisplay();
					}
				}
			}
		});

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mTopBar = (TopBar) findViewById(R.id.topbar);
				if (mTopBar != null) {
					initializeTopBar(mTopBar);
				}
			}
		});

		updateNightModeState();
	}

	protected void initializeTopBar(TopBar topbar) {
		if (getTitle() != null) {
			mTopBar.setTitleText("");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mReceiver.onActivityDestroyed();
		unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		MobclickAgent.onResume(this);

		TextView titleView = (TextView) findViewById(R.id.header_title);
		if (titleView != null) {
			setTitle(getIntent().getStringExtra(EXTRA_TITLE));
			titleView.setText(getTitle());
		}
	}

	protected Fragment createFragment() {
		return null;
	}

	;

	protected int getFragmentContainerID() {
		return 0;
	}

	public boolean hasBack() {
		return true;
	}

	public boolean hasHome() {
		return true;
	}

	public boolean hasSetting() {
		return true;
	}

	public float getmWidth() {
		return metric.widthPixels;
	}

	public float getmHeight() {
		return metric.heightPixels;
	}

	public float getScale() {
		return getResources().getDisplayMetrics().density;
	}

	public void onBtnBackClick(View view) {
		finish();
	}

	public TopBar getTopBar() {
		return mTopBar;
	}

	public String getTopBarTitle() {
		return null;
	}

	public void skip(Class<? extends Activity> cls) {
		startActivity(new Intent(this, cls));
	}

	public void skip(Class<? extends Activity> cls, Serializable... serializ) {
		Intent intent = new Intent(this, cls);
		Bundle extras = new Bundle();
		for (int i = 0; i < serializ.length; i++) {
			Serializable s = serializ[i];
			// 放对象的规则，以顺序为键
			extras.putSerializable(i + "", s);
		}
		intent.putExtras(extras);
		startActivity(intent);
	}

	public void toast(Object obj) {
		toast(obj, Toast.LENGTH_SHORT);
	}

	public void toast(Object obj, int dur) {
		Toast toast = new Toast(this);
		View layout = makeView(com.froyo.commonjar.R.layout.view_toast);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(AppUtils.getWidth(this),
				AppUtils.getHeight(this));
		layout.setLayoutParams(lp);
		toast.setView(layout);
		toast.setDuration(dur);
		TextView toastText = (TextView) layout.findViewById(com.froyo.commonjar.R.id.tv_toast);
		if (obj != null) {
			toastText.setText(obj.toString());
			toast.show();
		}
	}
	public View makeView(int resId) {
		LayoutInflater inflater = LayoutInflater.from(this);
		return inflater.inflate(resId, null);
	}
}
