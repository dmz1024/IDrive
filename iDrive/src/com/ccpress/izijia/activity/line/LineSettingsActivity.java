package com.ccpress.izijia.activity.line;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.widget.*;
import com.amap.api.services.core.LatLonPoint;
import com.ccpress.izijia.utils.CropFileUtils;
import com.ccpress.izijia.vo.LineDetailVo;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.NumberPicker.OnValueChangeListener;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.BitmapCache;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.AppUtils;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.InitUtil;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.froyo.commonjar.xutils.view.annotation.event.OnClick;
import com.ccpress.izijia.R;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.BespokeVo;
import com.ccpress.izijia.vo.LineDetailVo.Summary;
import com.ccpress.izijia.vo.ResponseVo;

/**
 * 
 * @Des: 线路设置
 * @author Rhino
 * @version V1.0
 * @created 2015年7月8日 上午10:53:40
 */
public class LineSettingsActivity extends BaseActivity {

	private View popupView;

	private PopupWindow popupWindow;
	
	private View popupViewThumb;
	private PopupWindow popupWindowThumb;

	@ViewInject(R.id.ll_main)
	private LinearLayout ll_main;

	@ViewInject(R.id.et_route_name)
	private EditText et_route_name;

	@ViewInject(R.id.et_desc)
	private EditText et_desc;

	@ViewInject(R.id.iv_thumb)
	private NetworkImageView iv_thumb;
	
	@ViewInject(R.id.tv_tag)
	private TextView tv_tag;
	
	private  String pic_path;

	RequestQueue mQueue;

	ImageLoader imageLoader;
	
	Summary summary;
	NumberPicker picker;
	private int currentIndex = 0;
	
	private String tags="";
	
	private File mCurrentPhotoFile;
	
	@Override
	public void doBusiness() {
		mQueue = Volley.newRequestQueue(activity);
		imageLoader = new ImageLoader(mQueue, BitmapCache.getInstance());

		TitleBar bar = new TitleBar(activity);
		bar.setTitle("线路设置");
		bar.showBack();
		bar.showRightText(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				save(tags);
			}
		}, "完成");
		
		summary=(Summary) getVo("1");
		et_route_name.setText(summary.getTitle());
		et_desc.setText(summary.getDesc());
		pic_path=summary.getImage();
		iv_thumb.setImageUrl(pic_path, imageLoader);
	}

	/**
	 * 底部Window：拍照、相册、取消等
	 */
	private void showWindow() {
		if (popupViewThumb == null) {
			popupViewThumb = makeView(R.layout.view_upload_photo_window);
			popupWindowThumb = new PopupWindow(popupViewThumb,
					WindowManager.LayoutParams.MATCH_PARENT,
					AppUtils.getHeight(activity));
			LinearLayout window = (LinearLayout) popupViewThumb
					.findViewById(R.id.ll_window);
			LinearLayout content = (LinearLayout) popupViewThumb
					.findViewById(R.id.ll_content);
			TextView tv_cancel = (TextView) popupViewThumb
					.findViewById(R.id.tv_cancel);
			TextView tv_photo = (TextView) popupViewThumb
					.findViewById(R.id.tv_photo);
			TextView tv_select = (TextView) popupViewThumb
					.findViewById(R.id.tv_select);
			//取消
			tv_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					popupWindowThumb.dismiss();
				}
			});
			//选择
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
					popupWindowThumb.dismiss();
				}
			});
			//摄像头
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
					popupWindowThumb.dismiss();
				}
			});
			window.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					popupWindowThumb.dismiss();
				}
			});
			content.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
				}
			});
		}
		if (popupWindowThumb.isShowing()) {
			popupWindowThumb.dismiss();
		} else {
			popupWindowThumb.showAtLocation(ll_main, Gravity.BOTTOM
					| Gravity.CENTER_HORIZONTAL, 0, 0);
		}
	}

	/**
	 * 封面上传
	 * @param photo
     */
	private void doUpLoadAvator(final Bitmap photo) {
		showDialog("正在上传封面……");
		RequestQueue mQueue = Volley.newRequestQueue(this);
		PostParams params = new PostParams();
		File tempFile = Utils.saveBitmapFile(activity, photo);
		params.put("file", tempFile);
		params.put("type", 4+"");

		if (et_desc.getText().toString().isEmpty()||et_desc.getText().toString()==null){
			params.put("desc", "  ");
		}else {
			params.put("desc", et_desc.getText().toString());
		}


		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.UPLOAD_PIC),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						if(obj.optString("result").equals("true")){
							toast("上传成功");
							pic_path=obj.optString("pic_path");
							iv_thumb.setImageBitmap(photo);
							if(pic_path.startsWith("http")||pic_path.startsWith("https")){
								
							}else{
								pic_path=Const.DOMAIN+pic_path;
							}
						}else{
							toast("上传失败");
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
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 2);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", AppUtils.getWidth(activity)-40);
		intent.putExtra("outputY", (AppUtils.getWidth(activity)-40)/2);
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("return-data", true);
		startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CROP);
	}

	/**
	 * 对图片等信息进行保存
	 * @param tags
     */
	private void save(String tags) {
 		if(Utils.isEmpty(et_route_name.getText().toString())){
			toast("线路名字未填");
			return;
		}
		if(Utils.isEmpty(et_desc.getText().toString())){
			toast("线路描述未填");
			return;
		}
		showDialog();
		PostParams params = new PostParams();
		params.put("route_id", summary.getLid());
		params.put("route_name", et_route_name.getText().toString());
		params.put("thumb", pic_path);
		params.put("desc", et_desc.getText().toString());
		params.put("tag_ids", tags);

		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, PersonalCenterUtils.buildUrlMy(getActivity(),Const.EDIT_ROUTE)),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo vo=GsonTools.getVo(obj.toString(), ResponseVo.class);
						if(vo.isSucess()){
							toast(vo.getMsg());
							finish();
						}
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	@OnClick(R.id.cb_selected)
	void clear(View view) {
		et_route_name.setText("");
	}

	@OnClick(R.id.tv_update_thumb)
	void updateThumb(View view) {
		showWindow();
	}

	@OnClick(R.id.rl_select_tag)
	void selectTag(View view) {
		ArrayList<BespokeVo> pickData=(ArrayList<BespokeVo>) getVo("0");
		showWindow(pickData);
	}

	/**
	 * 初始化window布局控件
	 * @param pickData
     */
	private void showWindow(final ArrayList<BespokeVo> pickData) {
		if (Utils.isEmpty(pickData)) {
			toast("没有看点数据");
			return;
		}
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < pickData.size(); i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("ItemTitle", pickData.get(i).getName());
			map.put("ItemText", "");
			mylist.add(map);
		}
		if (popupView == null) {
			popupView = makeView(R.layout.view_guide_window);
			popupWindow = new PopupWindow(popupView,
					WindowManager.LayoutParams.MATCH_PARENT,
					AppUtils.getHeight(activity));
			LinearLayout window = (LinearLayout) popupView
					.findViewById(R.id.ll_window);
			LinearLayout content = (LinearLayout) popupView
					.findViewById(R.id.ll_content);
			TextView tv_cancel = (TextView) popupView
					.findViewById(R.id.tv_cancle);
			TextView tv_title=(TextView)popupView.findViewById(R.id.tv_title);
			tv_title.setText("选择一个标签");
			final  ListView listView=(ListView)popupView.findViewById(R.id.preview_line_list);
			SimpleAdapter mSchedule = new SimpleAdapter(this,
					mylist, R.layout.item_preview_popu, new String[] {"ItemTitle", "ItemText"},
					new int[] {R.id.ItemTitle,R.id.ItemText});
			listView.setAdapter(mSchedule);


			tv_cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					popupWindow.dismiss();
				}
			});
			tv_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					popupWindow.dismiss();
				}
			});
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					tags=pickData.get(currentIndex).getSoptid();
					tv_tag.setText(pickData.get(currentIndex).getName());
					tags=pickData.get(currentIndex).getSoptid();
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
	 * 返回结果处理
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
					Uri uri = Uri.fromFile(mCurrentPhotoFile);
					String path = CropFileUtils.getPath(activity, uri);
					getBitmap(path);
					//startPhotoZoom(Uri.fromFile(mCurrentPhotoFile));
				}
				// ==========相册============
			} else if (requestCode == Const.REQUEST_CODE_IMAGE_SELECTE) {
				Uri uri = data.getData();
				String path = CropFileUtils.getPath(activity, uri);
				getBitmap(path);

				//startPhotoZoom(data.getData());
			} else if (requestCode == Const.REQUEST_CODE_IMAGE_CROP) {
				Toast.makeText(activity,"ASDFG",Toast.LENGTH_LONG).show();
				if (data != null) {
					Bundle extras = data.getExtras();
					if (extras == null) {
						return;
					}
					Bitmap photo = extras.getParcelable("data");
					doUpLoadAvator(photo);
				}

			}
		} catch (Exception e) {
		}
	}
	@Override
	protected int setLayoutResID() {
		return R.layout.activity_line_settings;
	}
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
		doUpLoadAvator(newbitmap);
	}
}
