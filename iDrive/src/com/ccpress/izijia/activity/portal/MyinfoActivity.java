package com.ccpress.izijia.activity.portal;

import java.io.File;

import com.ccpress.izijia.Constant;
import com.ccpress.izijia.activity.MainActivity;
import com.ccpress.izijia.fragment.UserCenterFragment;
import com.ccpress.izijia.util.LocationUtil;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.R;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.InfoVo;
import com.ccpress.izijia.vo.ResponseVo;
import com.ccpress.izijia.zxing.EncodingHandler;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.AppUtils;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.InitUtil;
import com.froyo.commonjar.utils.SpUtil;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.froyo.commonjar.xutils.view.annotation.event.OnClick;
import com.google.zxing.WriterException;

import de.greenrobot.event.EventBus;

/**
 * 个人资料
 * 
 * @author wangyi
 * 
 */
public class MyinfoActivity extends BaseActivity {

	@ViewInject(R.id.ll_main)
	private LinearLayout ll_main;

	@ViewInject(R.id.iv_avatar)
	private ImageView iv_avatar;

	@ViewInject(R.id.iv_code)
	private ImageView iv_code;

	@ViewInject(R.id.tv_nickname)
	private TextView tv_nickname;

	@ViewInject(R.id.tv_sex)
	private TextView tv_sex;

	@ViewInject(R.id.tv_local)
	private TextView tv_local;

	@ViewInject(R.id.tv_sign)
	private TextView tv_sign;

	private RequestQueue mQueue;

	private View popupView;

	private PopupWindow popupWindow;

	private File mCurrentPhotoFile;

	private InfoVo info;

	@Override
	public void doBusiness() {
		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("个人资料");

		EventBus.getDefault().register(this);

		info = (InfoVo) getVo("0");
		SpUtil sp = new SpUtil(activity);

		try {
			Bitmap qrCodeBitmap = EncodingHandler.createQRCode(
					sp.getStringValue(Const.USERNAME), 350);
			iv_code.setImageBitmap(qrCodeBitmap);
		} catch (WriterException e) {
			toast("生成二维码出错");
		}

		mQueue = Volley.newRequestQueue(activity);
		showAvatar(info.getAvatar());
		tv_nickname.setText(info.getNickname());
		tv_sex.setText("1".equals(info.getSex()) ? "男" : "女");
		tv_sign.setText(Utils.isEmpty(info.getSignature()) ? "未填写" : info
				.getSignature());
		//tv_local.setText(sp.getStringValue(Const.CITY));


		/*if(city==""){
			city="北京市";
		};*/
		String city =info.getPos_province() + "  " + info.getPos_city();
		if(info.getPos_province().isEmpty()){
			city=LocationUtil.getGpsProvince(getActivity().getApplicationContext())==null? "北京市" :
					(LocationUtil.getGpsProvince(getActivity().getApplicationContext()));
		}
		tv_local.setText(city);

	}

	/**
	 * 相册、拍照等数据返回处理
	 * @param requestCode
	 * @param resultCode
	 * @param data
     */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			// ==========摄像头===========
			if (requestCode == Const.REQUEST_CODE_IMAGE_CAPTURE
					&& resultCode == Activity.RESULT_OK) {
				if (mCurrentPhotoFile != null) {
					// 方法1：读取缓存图片
					startPhotoZoom(Uri.fromFile(mCurrentPhotoFile));
				}
				// ==========相册============
			} else if (requestCode == Const.REQUEST_CODE_IMAGE_SELECTE) {
				startPhotoZoom(data.getData());
			} else if (requestCode == Const.REQUEST_CODE_IMAGE_CROP) {
				if (data != null) {
					Bundle extras = data.getExtras();
					if (extras == null) {
						return;
					}
					Bitmap photo = extras.getParcelable("data");
					Bitmap bitmap = Utils.getRoundedCornerBitmap(photo);
					doUpLoadAvator(bitmap);
				}

			}
		} catch (Exception e) {
		}
	}

	/**
	 * 个人头像展示
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

	@OnClick(R.id.rl_select_photo)
	void selectPhoto(View view) {
		showWindow();
	}

	@OnClick(R.id.rl_sex)
	void selectSex(View view) {
		skip(UpdateSexActivity.class, tv_nickname.getText().toString(), tv_sex
				.getText().toString().equals("男") ? 1 : 2);
	}

	/**
	 * 名字、性别
	 * @param view
     */
	@OnClick(R.id.rl_nickname)
	void updateNickname(View view) {
		skip(EditNicknameActivity.class, tv_sex.getText().toString()
				.equals("男") ? "1" : "2", tv_nickname.getText().toString());
	}

	public void onEventMainThread(RefreshNickEvent event) {
		if (!Utils.isEmpty(event.getNickName())) {
			tv_nickname.setText(event.getNickName());
		}
		if (!Utils.isEmpty(event.getSex())) {
			tv_sex.setText(event.getSex());
		}
		if (!Utils.isEmpty(event.getSign())) {
			tv_sign.setText(event.getSign());
		}
	}

	public static class RefreshNickEvent {
		private String nickName;

		private String sex;

		private String sign;

		public String getNickName() {
			return nickName;
		}

		public void setNickName(String nickName) {
			this.nickName = nickName;
		}

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public String getSign() {
			return sign;
		}

		public void setSign(String sign) {
			this.sign = sign;
		}
	}

	@OnClick(R.id.rl_sign)
	void updateSign(View view) {
		if (info != null) {
			skip(EditSignActivity.class, info.getSignature());
		} else {
			skip(EditSignActivity.class);
		}
	}

	/**
	 * 二维码
	 * @param view
     */
	@OnClick(R.id.rl_qrcode)
	void showQrcode(View view) {
		if (info != null) {
			skip(MyQRCodeActivity.class, info);
		}
	}

	/**
	 * 更换个人头像，弹出底部window
	 */
	private void showWindow() {
		if (popupView == null) {
			popupView = makeView(R.layout.view_upload_photo_window);
			popupWindow = new PopupWindow(popupView,
					WindowManager.LayoutParams.MATCH_PARENT,
					AppUtils.getHeight(activity));
			LinearLayout window = (LinearLayout) popupView
					.findViewById(R.id.ll_window);
			LinearLayout content = (LinearLayout) popupView
					.findViewById(R.id.ll_content);
			TextView tv_cancel = (TextView) popupView
					.findViewById(R.id.tv_cancel);
			TextView tv_photo = (TextView) popupView
					.findViewById(R.id.tv_photo);
			TextView tv_select = (TextView) popupView
					.findViewById(R.id.tv_select);

			tv_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					popupWindow.dismiss();
				}
			});
			tv_select.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent it_photo = new Intent(Intent.ACTION_PICK, null);
					it_photo.setDataAndType(
							MediaStore.Images.Media.INTERNAL_CONTENT_URI,
							"image/*");
					// 跳转至系统功能
					startActivityForResult(it_photo,
							Const.REQUEST_CODE_IMAGE_SELECTE);
					popupWindow.dismiss();
				}
			});
			tv_photo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 从摄像头拍照取头像
					mCurrentPhotoFile = new File(InitUtil
							.getImageCachePath(activity), "temp.png");
					Intent it_camera = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					it_camera.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(mCurrentPhotoFile));
					startActivityForResult(it_camera,
							Const.REQUEST_CODE_IMAGE_CAPTURE);
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
		}
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			popupWindow.showAtLocation(ll_main, Gravity.BOTTOM
					| Gravity.CENTER_HORIZONTAL, 0, 0);
		}
	}

	/**
	 * 个人头像上传
	 * @param photo
     */
	private void doUpLoadAvator(final Bitmap photo) {
		showDialog("正在上传头像……");
		RequestQueue mQueue = Volley.newRequestQueue(this);
		PostParams params = new PostParams();
		File tempFile = Utils.saveBitmapFile(activity, photo);
		params.put("file", tempFile);
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.ALTER_AVATAR),
				new RespListener(activity) {
					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (vo.isSucess()) {
							iv_avatar.setImageBitmap(photo);
//							Intent intent = new Intent();
//							intent.setAction(Constant.USER_INFO_CHANGE_ACTION);
//							activity.sendBroadcast(intent);
							UserCenterFragment.Avatar="Avatar";
							SettingsActivity.MyInfo="MyInfo";
							//toast("下次登录生效");
						} else {
							toast(vo.getMsg());
						}
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	/** 缩放拍摄图片 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CROP);
	}
	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	@Override
	protected int setLayoutResID() {
		return R.layout.activity_myinfo;
	}
}
