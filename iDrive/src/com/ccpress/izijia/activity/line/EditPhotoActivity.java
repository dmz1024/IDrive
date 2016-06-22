package com.ccpress.izijia.activity.line;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.line.OtherAddPhotoActivity.FirstEvent;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.CropFileUtils;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.view.CustomNetworkImageView;
import com.ccpress.izijia.vo.LineDetailVo.Travel.Images;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.BitmapCache;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;

import de.greenrobot.event.EventBus;

/**
 * 
 * @Des: 编辑照片
 * @author Rhino
 * @version V1.0
 * @created 2015年7月9日 上午10:34:08
 */
public class EditPhotoActivity extends BaseActivity {
	
	@ViewInject(R.id.et_desc)
	private EditText et_desc;
	
	@ViewInject(R.id.iv_thumb)
	private CustomNetworkImageView iv_thumb;

	@Override
	public void doBusiness() {
		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("编辑照片");
		
		Images vo=(Images) getVo("0");
		et_desc.setText(vo.getDesc());
		RequestQueue mQueue=Volley.newRequestQueue(activity);
		ImageLoader imageLoader=new ImageLoader(mQueue, BitmapCache.getInstance());
	
		final int itemPosition=(Integer) getVo("1");
		final int childPosition=(Integer) getVo("2");
		final String path=(String) getVo("3");
		
		if(Utils.isEmpty(path)){
			iv_thumb.setImageUrl(vo.getImage(), imageLoader);
		}else{
			int degree = CropFileUtils.readPictureDegree(path);
			Bitmap newbitmap = null;
			if (degree != 0) {
				Bitmap map = CropFileUtils.getBitmap(activity, path, 1024);
				newbitmap = CropFileUtils.rotaingImageView(degree, map);
			} else {
				newbitmap = CropFileUtils.getBitmap(activity, path, 1024);
			}
			iv_thumb.setBackgroundDrawable(new BitmapDrawable(newbitmap));
		}
		bar.showRightText(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
//				doUpLoadAvator();
				if(Utils.isEmpty(et_desc.getText().toString())){
					toast("描述为空");
					return;
				}
				FirstEvent event=new OtherAddPhotoActivity.FirstEvent();
				event.setChildPosition(childPosition);
				event.setItemPosition(itemPosition);
				event.setDesc(et_desc.getText().toString());
				EventBus.getDefault().post(event); 
				finish();
			}
		}, "完成");
		
	}

	private void doUpLoadAvator() {
		if(Utils.isEmpty(et_desc.getText().toString())){
			toast("描述为空");
			return;
		}
		showDialog();
		RequestQueue mQueue = Volley.newRequestQueue(this);
		PostParams params = new PostParams();
//		File tempFile = Utils.saveBitmapFile(activity, photo);
//		params.put("file", tempFile);
		params.put("type", 4+"");
		params.put("desc", et_desc.getText().toString());

		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.UPLOAD_PIC),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						if(obj.optString("result").equals("true")){
							toast("成功添加描述");
						}else{
							toast("上传失败");
						}
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_edit_photo;
	}

}
