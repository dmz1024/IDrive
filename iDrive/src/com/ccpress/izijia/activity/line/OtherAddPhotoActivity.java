package com.ccpress.izijia.activity.line;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import android.widget.Toast;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.activity.ImagePickerActivity;
import com.ccpress.izijia.activity.MediaPickerActivity;
import com.ccpress.izijia.entity.MediaEntity;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.utils.InitUtil;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.AddPhotoAdapter;
import com.ccpress.izijia.adapter.OtherAddPhotoAdapter;
import com.ccpress.izijia.componet.BottomBar;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.CropFileUtils;
import com.ccpress.izijia.vo.LineDetailVo.Travel;
import com.ccpress.izijia.vo.LineDetailVo.Travel.Images;

import com.trs.util.log.Log;
import de.greenrobot.event.EventBus;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 
 * @Des: 添加照片---另外一种实现方式：默认进入界面不包含任何图片
 * @author Rhino
 * @version V1.0
 * @created 2015年7月8日 下午6:06:37
 */
public class OtherAddPhotoActivity extends BaseActivity {

	@ViewInject(R.id.lv_page_list)
	private ListView listView;

	private OtherAddPhotoAdapter adapter;
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
		//拍照，调用系统摄像头
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
		//图库（相册）中选择图片
		bottombar.showTv2("图库", R.drawable.icon_select_photo,
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						PickImage();

					}
				});
		adapter = new OtherAddPhotoAdapter(new ArrayList<Travel>(), activity,
				R.layout.item_add_photo, getVo("1").toString());
		listView.setAdapter(adapter);
		toast("请添加照片");
	}

	/**
	 * 第三方框架，添加多张图片
	 */
	private void PickImage() {
		int selectedMode = MultiImageSelectorActivity.MODE_MULTI;
		boolean showCamera = true;
		int maxNum = 9;
		Intent intent = new Intent(this, ImagePickerActivity.class);
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);// 是否显示拍摄图片
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxNum);// 最大可选择图片数量
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectedMode);// 选择模式
		intent.putExtra(ImagePickerActivity.FROM_WHICH_ACTIVITY, OtherAddPhotoActivity.class.toString());
		startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_SELECTE);
	}

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
				ArrayList<String> path ;
				/*getBitmap(path);
				Uri uri = data.getData();
				String path = CropFileUtils.getPath(activity, uri);*/
				path=MultiImageSelectorActivity.arry;
				for (int i=0;i<path.size();i++){
					getBitmap(path.get(i).toString());
				}
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
	 * 通过Bitmap处理图片，并向adapter添加
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
		image.setImagePath(path);
		image.setBitmap(newbitmap);
		 try {
				ExifInterface exif = new ExifInterface(  
						 path);
				if(exif!=null){
					 String time = exif  
	                            .getAttribute(ExifInterface.TAG_DATETIME);
					 if(time!=null){
						 SimpleDateFormat sdf=new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
						 long timeStart=sdf.parse(time).getTime();
						 image.setDate(timeStart);
					 }else{
						 image.setDate(System.currentTimeMillis());
					 }
				}
			} catch (IOException e) {
				 image.setDate(System.currentTimeMillis());
			} catch (ParseException e) {
				 image.setDate(System.currentTimeMillis());
			}  

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
