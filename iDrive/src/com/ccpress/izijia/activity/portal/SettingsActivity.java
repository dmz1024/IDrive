package com.ccpress.izijia.activity.portal;

import java.util.Set;

import com.ccpress.izijia.Constant;
import com.ccpress.izijia.iDriveApplication;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.portal.MyinfoActivity.RefreshNickEvent;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.InfoVo;
import com.ccpress.izijia.vo.ResponseVo;
import com.ccpress.izijia.zxing.EncodingHandler;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.GetRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.AppUtils;
import com.froyo.commonjar.utils.FileOperator;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.InitUtil;
import com.froyo.commonjar.utils.SpUtil;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.froyo.commonjar.xutils.view.annotation.event.OnClick;
import com.google.zxing.WriterException;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import de.greenrobot.event.EventBus;

/**
 * 设置
 * 
 * @author wangyi
 * 
 */
public class SettingsActivity extends BaseActivity {

	@ViewInject(R.id.iv_avatar)
	private ImageView iv_avatar;

	@ViewInject(R.id.tv_code)
	private ImageView tv_code;

	@ViewInject(R.id.tv_name)
	private TextView tv_name;

	@ViewInject(R.id.tv_invite)
	private TextView tv_invite;

	@ViewInject(R.id.tv_cache_size)
	private TextView tv_cache_size;

	@ViewInject(R.id.ll_main)
	private LinearLayout ll_main;

	private InfoVo info = null;

	private RequestQueue mQueue;

	public static String MyInfo="DF";
	SpUtil sp;

	@Override
	public void doBusiness() {
		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("设置");

		EventBus.getDefault().register(this);

		sp= new SpUtil(activity);
		tv_name.setText(sp.getStringValue(Const.USERNAME));

		try {
			Bitmap qrCodeBitmap = EncodingHandler.createQRCode(
					sp.getStringValue(Const.USERNAME), 350);
			tv_code.setImageBitmap(qrCodeBitmap);
		} catch (WriterException e) {
			toast("生成二维码出错");
		}

		mQueue = Volley.newRequestQueue(activity);
		queryInfo(sp.getStringValue(Const.UID));

		double size = FileOperator.getFileOrFilesSize(
				InitUtil.getImageCachePath(activity), FileOperator.SIZETYPE_MB);
		tv_cache_size.setText(size + " M");
	}

	@OnClick(R.id.tv_change_pass)
	void changePass(View view) {
		skip(UpdatePassActivity.class);
	}

	@OnClick(R.id.rl_myinfo)
	void showInfo(View view) {
		if (info != null) {
			skip(MyinfoActivity.class, info);
		}
	}

	@OnClick(R.id.tv_about)
	void zxing(View view) {
		skip(AboutActivity.class,"关于我们",Const.ABOUT);
	}

	@OnClick(R.id.et_invite)
	void update(View view){
		checkNewVersion();
	}

	/**
	 * 版本检查
	 */
	 private void checkNewVersion() {
	        // ---umeng检查更新版本---
	        showDialog();
	        UmengUpdateAgent.setUpdateOnlyWifi(false);
	        UmengUpdateAgent.update(activity);
	        UmengUpdateAgent.setDeltaUpdate(false);
	        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

	            @Override
	            public void onUpdateReturned(int updateStatus,
	                                         UpdateResponse updateInfo) {
	                dismissDialog();
	                switch (updateStatus) {
	                    case UpdateStatus.Yes: // has update
	                        dismissDialog();
	                        UmengUpdateAgent.showUpdateDialog(activity, updateInfo);
	                        break;
	                    case UpdateStatus.No: // has no update
	                        toast("已是最新版本");
	                        activity.dismissDialog();
	                        break;
	                    case UpdateStatus.NoneWifi: // none wifi
	                        dismissDialog();
	                        break;
	                    case UpdateStatus.Timeout: // time out
	                        toast("访问超时");
	                        dismissDialog();
	                        break;
	                }
	            }
				
	        });

	        UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {

	            @Override
	            public void onClick(int status) {
	                switch (status) {
	                    case UpdateStatus.Update:
	                        break;
	                    case UpdateStatus.Ignore:
	                        break;
	                    case UpdateStatus.NotNow:
	                        break;
	                }
	            }
	        });
	    }
	
	@OnClick(R.id.btn_exit)
	void createBit(View view) {
		
		SpUtil sp=new SpUtil(activity);
		sp.clear();
	    setAlias();
		iDriveApplication app = (iDriveApplication) getApplication();
		app.finishActExcept(activity.getClass().getSimpleName());
		skip(LoginActivity.class);
		finish();
	}

	@OnClick(R.id.tv_feedback)
	void feedback(View view) {
		skip(FeedbackActivity.class);
	}

	@OnClick(R.id.rl_clear_cache)
	void clear(View view) {
		showWindow();
	}

	@OnClick(R.id.rl_invite)
	void invite(View view){
		if(info!=null){
			toast(info.getInvite_code());
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			toast(scanResult);
		}
	}

	/**
	 * 请求数据
	 * @param tuid
     */
	private void queryInfo(String tuid) {
		GetRequest req = new GetRequest(activity, PersonalCenterUtils.buildUrl(
				activity, Const.MY_INFO) + "&tuid=" + tuid, new RespListener(
				activity) {

			@Override
			public void getResp(JSONObject obj) {
				dismissDialog();
				ResponseVo vo = GsonTools.getVo(obj.toString(),
						ResponseVo.class);
				if (vo.isSucess()) {
					try {
						info = GsonTools.getVo(obj.getJSONObject("data")
								.getJSONObject("user_info").toString(),
								InfoVo.class);
						tv_invite.setText(info.getInvite_code());
						showAvatar(info.getAvatar());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					activity.toast(vo.getMsg());
				}
			}

			@Override
			public void doFailed() {
				dismissDialog();
			}
		});

		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 个人信息的更新响应
	 * @param event
     */
	public void onEventMainThread(RefreshNickEvent event) {
		if (info == null) {
			return;
		}

		if (!Utils.isEmpty(info.getAvatar())) {
			showAvatar(info.getAvatar());
		}

		if (!Utils.isEmpty(event.getNickName())) {
			info.setNickname(event.getNickName());
		}
		if (!Utils.isEmpty(event.getSex())) {
			info.setSex("男".equals(event.getSex()) ? "1" : "2");
		}
		if (!Utils.isEmpty(event.getSign())) {
			info.setSignature(event.getSign());
		}
	}

	/**
	 * 个人头像
	 * @param url
     */
	private void showAvatar(String url) {
		ImageRequest req = new ImageRequest(url,
				new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap arg0) {
						if (arg0 != null) {
							iv_avatar.setImageBitmap(Utils
									.getRoundedCornerBitmap(arg0));
						}
					}
				}, 300, 300, Config.ARGB_8888, null);
		mQueue.add(req);
		mQueue.start();
	}

	private void showWindow() {

		View popupView = makeView(R.layout.view_clear_cache_window);
		final PopupWindow popupWindow = new PopupWindow(popupView,
				WindowManager.LayoutParams.MATCH_PARENT,
				AppUtils.getHeight(activity));
		LinearLayout window = (LinearLayout) popupView
				.findViewById(R.id.ll_window);
		LinearLayout content = (LinearLayout) popupView
				.findViewById(R.id.ll_content);
		TextView tv_cancel = (TextView) popupView.findViewById(R.id.tv_cancel);

		TextView tv_select = (TextView) popupView.findViewById(R.id.tv_select);

		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
			}
		});
		tv_select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FileOperator.deleteFilesByDirectory(InitUtil
						.getImageCachePath(activity));
				tv_cache_size.setText("0 M");
				popupWindow.dismiss();
			}
		});

		window.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
			}
		});
		content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			}
		});
		popupWindow.showAtLocation(ll_main, Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (MyInfo=="MyInfo"){
			queryInfo(sp.getStringValue(Const.UID));
			MyInfo="DF";
		}
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	/**
	 * 注销退出登录
	 */
	private void setAlias(){
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, ""));
	}
	
	private static final int MSG_SET_ALIAS = 1001;
	
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case MSG_SET_ALIAS:
                JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, new TagAliasCallback() {

                    @Override
                    public void gotResult(int code, String alias, Set<String> tags) {
                        switch (code) {
                        case 0:
							//成功
                            break;
                        case 6002:
                        	//失败
                        	mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                            break;
                        default:
                        }
                    }
            	    
            	});
                break;
            default:
            }
        }
    };

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_settings;
	}
}
