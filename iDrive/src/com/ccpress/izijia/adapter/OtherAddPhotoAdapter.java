package com.ccpress.izijia.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.trs.util.log.Log;
import android.media.ExifInterface;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.line.EditPhotoActivity;
import com.ccpress.izijia.activity.line.LinePreviewActivity;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.view.CustomNetworkImageView;
import com.ccpress.izijia.vo.LineDetailVo.Travel;
import com.ccpress.izijia.vo.LineDetailVo.Travel.Images;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.AppUtils;
import com.froyo.commonjar.utils.Utils;

/**
 *
 * @Des: 我的线路---添加照片
 * @author Rhino
 * @version V1.0
 * @created 2015年7月9日 上午10:09:45
 */
public class OtherAddPhotoAdapter extends SimpleAdapter<Travel> {

	private TimeCount timer;
	private boolean batch = false;

	private static ConcurrentHashMap<Integer, Boolean> map = new ConcurrentHashMap<Integer, Boolean>();

	private String routeId;

	public OtherAddPhotoAdapter(List<Travel> data, BaseActivity activity,
			int layoutId, String routeId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
		timer = new TimeCount(20000, 500);
		this.routeId = routeId;
	}

	/**
	 * 实现布局逻辑
	 * @param convertView
	 * @param item
	 * @param position
     */
	@Override
	public void doExtra(View convertView, final Travel item, final int position) {
		final ViewHolder h = (ViewHolder) holder;
		h.tv_time.setText(item.getDate());
		int width = (AppUtils.getWidth(activity) - 21) / 3;
		LayoutParams lp = new LayoutParams(width, width);

		h.ll_container.removeAllViews();

		for (int i = 0; i < item.getImages().size(); i++) {
			View view = activity.makeView(R.layout.item_view_add_photo);
			CustomNetworkImageView iv_pic1 = (CustomNetworkImageView) view
					.findViewById(R.id.iv_pic1);
			TextView tv_delete=(TextView) view.findViewById(R.id.tv_delete);
			TextView tv_edit=(TextView) view.findViewById(R.id.tv_edit);
			FrameLayout.LayoutParams lpchild = new LayoutParams(width, width);
			iv_pic1.setLayoutParams(lpchild);
			if (item.getImages().get(i).getBitmap() == null) {
				iv_pic1.setImageUrl(item.getImages().get(i).getImage(),
						imageLoader);
			} else {
				iv_pic1.setLocalImageBitmap(item.getImages().get(i).getBitmap());
			}

			view.setLayoutParams(lp);

			final int p = i;
			//图片的删除，点击事件
			tv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					item.getImages().remove(p);
					h.ll_container.removeViewAt(p);
					if(Utils.isEmpty(item.getImages())){
						removeByPos(position);
					}
				}
			});
			//图片编辑，点击事件
			tv_edit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (item.getImages().get(p).getBitmap() == null) {
						activity.skip(EditPhotoActivity.class, item.getImages()
								.get(p), position, p);
					} else {
						Intent intent = new Intent(activity,
								EditPhotoActivity.class);
						Bundle extras = new Bundle();
						Images temp = item.getImages().get(p);
						Images temImages = new Images();
						temImages.setDesc(temp.getDesc());
						extras.putSerializable("0", temImages);
						extras.putSerializable("1", position);
						extras.putSerializable("2", p);
						extras.putSerializable("3", temp.getImagePath());
						intent.putExtras(extras);
						activity.startActivity(intent);
					}
				}
			});

			h.ll_container.addView(view);

		}
	}

	public static class ViewHolder {

		TextView tv_time;

		LinearLayout ll_container;
	}

	/**
	 * 向容器中加入照片
	 * @param image
     */
	public void addPhoto(Images image) {
		if (getCount() > 0) {
			String date = Utils.formatTime(image.getDate(), "yyyy/MM/dd");
			boolean hasAdd=false;
			for (int i = 0; i < getCount(); i++) {
				String tempDate = getItem(i).getDate();
				if (date.equals(tempDate)) {

					Travel vo = getItem(i);
					vo.getImages().add(image);
					replace(vo, i);
					hasAdd=true;
					break;
				}else if(i==getCount()-1&&!hasAdd){
					Travel vo = new Travel();
					vo.setDate(date);
					ArrayList<Images> tempArray = new ArrayList<Images>();
					tempArray.add(image);
					vo.setImages(tempArray);
					addItem(vo, getCount());
					break;
				}
			}
		} else {
			String date = Utils.formatTime(image.getDate(), "yyyy/MM/dd");
			Travel vo = new Travel();
			vo.setDate(date);
			ArrayList<Images> tempArray = new ArrayList<Images>();
			tempArray.add(image);
			vo.setImages(tempArray);
			addItem(vo, getCount());
		}
	}

	/**
	 * 更换图片
	 * @param id
	 * @param position
	 * @param count
     */
	public void replaceImage(String id, int position,int count) {
			Travel travel = getItem(count);
			List<Images> temp = travel.getImages();
			temp.get(position).setId(id);

	}
	private String lat;
	private String lng;

	/**
	 * 对获取图片的坐标进行切割转换成GPS坐标
	 * @param rationalString
	 * @param ref
     * @return
     */
	private float convertRationalLatLonToFloat(
			String rationalString, String ref) {
		try {
			String[] parts = rationalString.split(",");
			String[] pair;
			pair = parts[0].split("/");
			int degrees = (int) (Float.parseFloat(pair[0].trim())
					/ Float.parseFloat(pair[1].trim()));
			pair = parts[1].split("/");
			int minutes = (int) ((Float.parseFloat(pair[0].trim())
					/ Float.parseFloat(pair[1].trim())));
			pair = parts[2].split("/");
			float seconds = Float.parseFloat(pair[0].trim())
					/ Float.parseFloat(pair[1].trim());
			float result = degrees + (minutes / 60F) + (seconds / (60F * 60F));
			if ((ref.equals("S") || ref.equals("W"))) {
				return -result;
			}
			return result;
		} catch (RuntimeException ex) {
			return 0f;
		}
	}
	private List<String> list=new ArrayList<>();
	/**
	 * 循环上传照片
	 */
	public void doUpLoadAvator() {
		list.clear();
		map.clear();
		if (getCount() < 1) {
			activity.toast("没有添加新数据");
			return;
		}
		activity.showDialog();
		int count = 0;
		timer.start();
		for (int j=0;j< getCount();j++){
		Travel travel = getItem(j);
			for (int i = 0; i < travel.getImages().size(); i++) {
				final Images imageVo = travel.getImages().get(i);
				if (imageVo.getBitmap() != null) {
					PostParams params = new PostParams();
					File tempFile = Utils.saveBitmapFile(activity,
						imageVo.getBitmap());
					params.put("file", tempFile);
					params.put("type", 4 + "");
					params.put("desc",imageVo.getDesc());

					//照片的拍照日期
					params.put("pic_exif_time_app",imageVo.getDate().toString());

				try {
					//android读取图片EXIF信息
					ExifInterface exifInterface=new ExifInterface(imageVo.getImagePath());
					lat=exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
					lng=exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE );

				} catch (Exception e) {
					e.printStackTrace();
				}
				params.put("pic_exif_lat_app",String.valueOf(convertRationalLatLonToFloat(lat,"EN")));
				params.put("pic_exif_lng_app",String.valueOf(convertRationalLatLonToFloat(lng,"EN")));

				Log.e("doUpLoadAvator ", "LLgetDate+" + imageVo.getDate().toString());
				Log.e("doUpLoadAvator ", "LLgetDesc+" + imageVo.getDesc());
				Log.e("doUpLoadAvator ", "LLgetImagePath+" + imageVo.getImagePath());
				Log.e("doUpLoadAvator ", "LL+lat" + String.valueOf(convertRationalLatLonToFloat(lat, "EN")));
				Log.e("doUpLoadAvator ", "LL+lng" +String.valueOf(convertRationalLatLonToFloat(lng,"EN")));

					final int positon=count;
					final int flag=i;
					final int temp=j;
					map.put(positon, false);
				PostRequest req = new PostRequest(activity, params,
						PersonalCenterUtils
								.buildUrl(activity, Const.UPLOAD_PIC),
						new RespListener(activity) {
							@Override
							public void getResp(JSONObject obj) {
								if (obj.optString("result").equals("true")) {
									map.put(positon, true);
									String pic_path = obj.optString("pic_path");
									list.add(obj.optString("id"));
									if (pic_path.startsWith("http") || pic_path.startsWith("https")) {
									} else {
										pic_path = Const.DOMAIN + pic_path;
									}
									replaceImage(obj.optString("id"), flag,(temp));
								}
								else {
								}
							}
						});
				mQueue.add(req);
				mQueue.start();
				}
				count++;
			}
		}

	}

	/**
	 * 计时器操作
	 */
	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			Iterator<Integer> it = map.keySet().iterator();
			boolean flag = true;
			while (it.hasNext()) {
				int key = it.next();
				if (map.get(key)) {
					flag = true;
				} else {
					flag = false;
					break;
				}
			}
			if (flag) {
				if (!batch) {
					uploadPicBatch();
				}
			}
		}


		@Override
		public void onFinish() {
			batch = false;
			Iterator<Integer> it = map.keySet().iterator();
			while (it.hasNext()) {
				int key = it.next();
				if (!map.get(key)) {
					activity.toast("网络不稳，上传失败");
					break;
				}
			}
			activity.dismissDialog();
		}
	}

	/**
	 * 将获取的Images的ID，批量导入到游记中
	 */
	private void uploadPicBatch() {
		batch = true;
		String id = "";
		for (int i=0;i<list.size();i++){
					id= id + list.get(i) +",";
		}

		PostParams params = new PostParams();
		params.put("route_id", routeId);
		params.put("pic_ids", id);
		Log.e( "getResp ",id);
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.ADD_ROUTE_TRAVAL),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						if (obj.optString("result").equals("true")) {
							activity.toast("上传游记照片成功");
								//跳转到线路预览
							activity.skip(LinePreviewActivity.class,routeId);
							activity.finish();
						} else {
							activity.toast("上传游记照片失败");
						}
					}
				});
		mQueue.add(req);
		mQueue.start();
	}
}
