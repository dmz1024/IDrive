package com.ccpress.izijia.activity.line;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.utils.InitUtil;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.AddPhotoAdapter;
import com.ccpress.izijia.componet.BottomBar;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.CropFileUtils;
import com.ccpress.izijia.vo.LineDetailVo.Travel;
import com.ccpress.izijia.vo.LineDetailVo.Travel.Images;

import de.greenrobot.event.EventBus;

/**
 * 
 * @Des: 添加照片
 * @author Rhino
 * @version V1.0
 * @created 2015年7月8日 下午6:06:37
 */
public class AddPhotoActivity extends BaseActivity {

	@ViewInject(R.id.lv_page_list)
	private ListView listView;

	private AddPhotoAdapter adapter;

	private File mCurrentPhotoFile;

	@Override
	public void doBusiness() {
		EventBus.getDefault().register(this);

		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("添加照片");
		bar.showRightText(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				adapter.doUpLoadAvator();
			}
		}, "完成");

		BottomBar bottombar = new BottomBar(activity);
		bottombar.showTv1("拍照", R.drawable.icon_take_photo,
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						mCurrentPhotoFile = new File(InitUtil
								.getImageCachePath(activity), "temp.png");
						Intent it_camera = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						it_camera.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(mCurrentPhotoFile));
						startActivityForResult(it_camera,
								Const.REQUEST_CODE_IMAGE_CAPTURE);
					}
				});

		bottombar.showTv2("图库", R.drawable.icon_select_photo,
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent it_photo = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
						/*it_photo.setDataAndType(
								MediaStore.Images.Media.INTERNAL_CONTENT_URI,
								"image*//*");*/
						// 跳转至系统功能
						startActivityForResult(it_photo,
								Const.REQUEST_CODE_IMAGE_SELECTE);
					}
				});

		adapter = new AddPhotoAdapter(new ArrayList<Travel>(), activity,
				R.layout.item_add_photo, getVo("1").toString());
		listView.setAdapter(adapter);

		ArrayList<Travel> datas = (ArrayList<Travel>) getVo("0");

		adapter.addItems(datas);
	}

	/**
	 * 照片图片放缩处理
	 * @param uri
     */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 4);
		intent.putExtra("aspectY", 4);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 400);
		intent.putExtra("outputY", 400);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CROP);
	}

	/**
	 * 返回图片结果
	 * @param requestCode
	 * @param resultCode
	 * @param data
     */
	@Override
	protected void onActivityResult(final int requestCode, int resultCode,
			final Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			// ==========摄像头===========
			if (requestCode == Const.REQUEST_CODE_IMAGE_CAPTURE
					&& resultCode == RESULT_OK) {
				if (mCurrentPhotoFile != null) {
					// 方法1：读取缓存图片
					// startPhotoZoom(Uri.fromFile(mCurrentPhotoFile));
					Uri uri = Uri.fromFile(mCurrentPhotoFile);
					String path = CropFileUtils.getPath(activity, uri);
					getBitmap(path);
				}
				// ==========相册============
			} else if (requestCode == Const.REQUEST_CODE_IMAGE_SELECTE) {
				Uri uri = data.getData();
				String path = CropFileUtils.getPath(activity, uri);
				getBitmap(path);
				// startPhotoZoom(data.getData());
			} else if (requestCode == Const.REQUEST_CODE_IMAGE_CROP) {
				if (data != null) {
					Bundle extras = data.getExtras();
					if (extras == null) {
						return;
					}
					Bitmap photo = extras.getParcelable("data");
					// doUpLoadAvator(photo);

					Images image = new Images();
					image.setDesc("");
					image.setImage("");
					image.setBitmap(photo);
					adapter.addPhoto(image);
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 通过Bitmap处理图片
	 * @param path
     */
	private void getBitmap(String path) {
		// 判断是否旋转
		int degree = CropFileUtils.readPictureDegree(path);
		Bitmap newbitmap = null;
		if (degree != 0) {
			Bitmap map = CropFileUtils.getBitmap(activity, path, 1024);
			newbitmap = CropFileUtils.rotaingImageView(degree, map);
		} else {
			newbitmap = CropFileUtils.getBitmap(activity, path, 1024);
		}
		Images image = new Images();
		image.setDesc("");
		image.setImage("");
		image.setBitmap(newbitmap);
		adapter.addPhoto(image);
	}

	public void onEventMainThread(FirstEvent event) {
		adapter.getItem(event.getItemPosition()).getImages()
				.get(event.getChildPosition()).setDesc(event.getDesc());
	}

	public static class FirstEvent {

		private String desc;

		int itemPosition;

		int childPosition;

		public int getItemPosition() {
			return itemPosition;
		}

		public void setItemPosition(int itemPosition) {
			this.itemPosition = itemPosition;
		}

		public int getChildPosition() {
			return childPosition;
		}

		public void setChildPosition(int childPosition) {
			this.childPosition = childPosition;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_add_photo;
	}

}
