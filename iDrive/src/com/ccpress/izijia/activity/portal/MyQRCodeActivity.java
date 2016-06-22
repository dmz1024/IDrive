package com.ccpress.izijia.activity.portal;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.utils.AppUtils;
import com.froyo.commonjar.utils.SpUtil;
import com.froyo.commonjar.utils.Utils;
import com.google.zxing.WriterException;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.ccpress.izijia.R;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.vo.InfoVo;
import com.ccpress.izijia.zxing.EncodingHandler;

/**
 * 我的二维码
 * 
 * @author wangyi
 *
 */
public class MyQRCodeActivity extends BaseActivity {

	@ViewInject(R.id.ll_main)
	private LinearLayout ll_main;

	@ViewInject(R.id.iv_avatar)
	private ImageView iv_avatar;

	@ViewInject(R.id.iv_code)
	private ImageView iv_code;

	@ViewInject(R.id.tv_name)
	private TextView tv_name;

	@ViewInject(R.id.tv_local)
	private TextView tv_local;

	private RequestQueue mQueue;

	private View popupView;

	private PopupWindow popupWindow;

	@Override
	public void doBusiness() {
		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("我的二维码");

		InfoVo info = (InfoVo) getVo("0");
		SpUtil sp = new SpUtil(activity);
		try {
			final Bitmap qrCodeBitmap = EncodingHandler.createQRCode(
					sp.getStringValue(Const.USERNAME), AppUtils.getWidth(activity) - AppUtils.dip2px(activity, 12));
			iv_code.setImageBitmap(qrCodeBitmap);
			LayoutParams lp = new LayoutParams(AppUtils.getWidth(activity)
					- AppUtils.dip2px(activity, 12),
					AppUtils.getWidth(activity) - AppUtils.dip2px(activity, 12));
			iv_code.setLayoutParams(lp);

			bar.showRightImage(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					showWindow(qrCodeBitmap);
				}
			}, R.drawable.icon_bar_more);
		} catch (WriterException e) {
			toast("生成二维码出错");
		}
		mQueue = Volley.newRequestQueue(activity);
		showAvatar(info.getAvatar());
		tv_name.setText(info.getNickname());
		tv_local.setText(info.getPos_province() + "  " + info.getPos_city());

	}

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

	private void showWindow(final Bitmap bit) {
		if (popupView == null) {
			popupView = makeView(R.layout.view_save_qrimage_window);
			popupWindow = new PopupWindow(popupView,
					WindowManager.LayoutParams.MATCH_PARENT,
					AppUtils.getHeight(activity));
			LinearLayout window = (LinearLayout) popupView
					.findViewById(R.id.ll_window);
			LinearLayout content = (LinearLayout) popupView
					.findViewById(R.id.ll_content);
			TextView tv_cancel = (TextView) popupView
					.findViewById(R.id.tv_cancel);

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

					// 为要下载的二维码图片设置白色背景
					Bitmap newbit = setBg(bit);

					Utils.saveBitmapFile(activity, newbit);
					popupWindow.dismiss();
					toast("成功保存二维码");
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

	private Bitmap setBg(Bitmap sourceImg) {
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,
				sourceImg.getWidth(), sourceImg.getHeight());
		int white = 0xffffffff;
		for (int i = 0; i < argb.length; i++) {
			argb[i] = white;
		}
		Bitmap background = Bitmap.createBitmap(argb, sourceImg.getWidth(),
				sourceImg.getHeight(), Config.ARGB_8888);

		return toMergeBitmap(background, sourceImg);
	}

	private Bitmap toMergeBitmap(Bitmap background, Bitmap foreground) {
		if (background == null) {
			return null;
		}
		int bgWidth = background.getWidth();
		int bgHeight = background.getHeight();
		Bitmap newbmp = Bitmap
				.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
		Canvas cv = new Canvas(newbmp);
		cv.drawBitmap(background, 0, 0, null);
		cv.drawBitmap(foreground, 0, 0, null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		return newbmp;
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_qrcode;
	}
}
