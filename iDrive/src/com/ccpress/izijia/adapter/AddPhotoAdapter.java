package com.ccpress.izijia.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
public class AddPhotoAdapter extends SimpleAdapter<Travel> {

	private TimeCount timer;
	private boolean batch = false;

	private static ConcurrentHashMap<Integer, Boolean> map = new ConcurrentHashMap<Integer, Boolean>();

	private String routeId;

	public AddPhotoAdapter(List<Travel> data, BaseActivity activity,
			int layoutId, String routeId) {
		super(data, activity, layoutId, ViewHolder.class, R.id.class);
		timer = new TimeCount(8000, 1000);
		this.routeId = routeId;
	}

	@Override
	public void doExtra(View convertView, final Travel item, final int position) {
		ViewHolder h = (ViewHolder) holder;
		h.tv_time.setText(item.getDate());
		int width = (AppUtils.getWidth(activity) - 21) / 3;
		LayoutParams lp = new LayoutParams(width, width);

		h.ll_container.removeAllViews();

		for (int i = 0; i < item.getImages().size(); i++) {
			View view = activity.makeView(R.layout.item_view_add_photo);
			CustomNetworkImageView iv_pic1 = (CustomNetworkImageView) view
					.findViewById(R.id.iv_pic1);
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

			iv_pic1.setOnClickListener(new OnClickListener() {

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
						extras.putParcelable("bitmap", item.getImages().get(p)
								.getBitmap());
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

	public void addPhoto(Images image) {
		if (getCount() > 0) {
			String date = Utils.formatTime(System.currentTimeMillis(),
					"yyyy/MM/dd");
			String tempDate = getItem(getCount() - 1).getDate();
			if (date.equals(tempDate)) {
				Travel vo = getItem(getCount() - 1);
				// if (vo.getImages().size() < 3) {
				vo.getImages().add(image);
				replace(vo, getCount() - 1);
				// } else {
				// }
			} else {
				Travel vo = new Travel();
				vo.setDate(date);
				ArrayList<Images> tempArray = new ArrayList<Images>();
				tempArray.add(image);
				vo.setImages(tempArray);
				addItem(vo, getCount());
			}
		} else {
			String date = Utils.formatTime(System.currentTimeMillis(),
					"yyyy/MM/dd");
			Travel vo = new Travel();
			vo.setDate(date);
			ArrayList<Images> tempArray = new ArrayList<Images>();
			tempArray.add(image);
			vo.setImages(tempArray);
			addItem(vo, getCount());
		}
	}

	public void replaceImage(Images image, int position) {
		Travel travel = getItem(getCount() - 1);
		List<Images> temp = travel.getImages();
		temp.remove(position);
		temp.add(position, image);
		travel.setImages(temp);
		replace(travel, getCount() - 1);
	}

	public void doUpLoadAvator() {
		map.clear();
		if (getCount() < 1) {
			activity.toast("没有添加新数据");
			return;
		}
		Travel travel = getItem(getCount() - 1);
		String date = Utils
				.formatTime(System.currentTimeMillis(), "yyyy/MM/dd");
		String tempDate = travel.getDate();
		if (!date.equals(tempDate)) {
			activity.toast("没有添加新数据");
			return;
		}
		timer.start();
		activity.showDialog();
		for (int i = 0; i < travel.getImages().size(); i++) {
			final Images imageVo = travel.getImages().get(i);
			if (imageVo.getBitmap() != null) {
				PostParams params = new PostParams();
				File tempFile = Utils.saveBitmapFile(activity,
						imageVo.getBitmap());
				params.put("file", tempFile);
				params.put("type", 4 + "");
				params.put("desc", imageVo.getDesc());

				final int positon = i;
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
									if (pic_path.startsWith("http")
											|| pic_path.startsWith("https")) {

									} else {
										pic_path = Const.DOMAIN + pic_path;
									}
									Images image = new Images();
									image.setDesc(imageVo.getDesc());
									image.setImage(pic_path);
									image.setId(obj.optString("id"));
									replaceImage(image, positon);
								} else {
								}
							}
						});
				mQueue.add(req);
				mQueue.start();
			}
		}
	}

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
					activity.toast("第" + key + "张上传失败");
					break;
				}
			}
			activity.dismissDialog();
		}
	}

	private void uploadPicBatch() {
		batch = true;
		Travel travel = getItem(getCount() - 1);
		String id = "";
		for (int i = 0; i < travel.getImages().size(); i++) {
			Images tempImage = travel.getImages().get(i);
			if (Utils.isEmpty(id)) {
				id = tempImage.getId();
			} else {
				id = id + "," + tempImage.getId();
			}
		}
		PostParams params = new PostParams();
		params.put("route_id", routeId);
		params.put("pic_ids", id);

		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.ADD_ROUTE_TRAVAL),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						if (obj.optString("result").equals("true")) {
							activity.toast("上传游记照片成功");
//							BaseApplication.APP.finishAct("LineEditActivity");
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
