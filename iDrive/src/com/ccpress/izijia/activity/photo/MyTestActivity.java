package com.ccpress.izijia.activity.photo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimplePageAdapter;
import com.froyo.commonjar.network.BitmapCache;
import com.froyo.commonjar.network.GetRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.view.CustomListView;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.PhotoDetaiCommentAdapter;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.BespokeVo;
import com.ccpress.izijia.vo.MyPhotoVo;
import com.ccpress.izijia.vo.MyPhotoVo.Pic;
import com.ccpress.izijia.vo.ResponseVo;

public class MyTestActivity extends BaseActivity {
	private RequestQueue mQueue;

	@ViewInject(R.id.vp_pics)
	private ViewPager vp_pics;

	@ViewInject(R.id.ll_container)
	private LinearLayout ll_container;

	@ViewInject(R.id.tv_desc)
	private TextView tv_desc;

	@ViewInject(R.id.tv_local)
	private TextView tv_local;

	@ViewInject(R.id.tv_time)
	private TextView tv_time;

	@ViewInject(R.id.tv_comment)
	private TextView tv_comment;

	@ViewInject(R.id.tv_share)
	private TextView tv_share;

	@ViewInject(R.id.tv_parise)
	private TextView tv_parise;

	private SimplePageAdapter adapter;

	PhotoDetaiCommentAdapter commentAdapter;

	@ViewInject(R.id.lv_page_list)
	private CustomListView listView;

	@ViewInject(R.id.sv_view)
	private ScrollView sv_view;

	@Override
	public void doBusiness() {
		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("内容详情");

		commentAdapter = new PhotoDetaiCommentAdapter(
				new ArrayList<BespokeVo>(), activity,
				R.layout.item_photo_detail_comment);
		listView.setAdapter(commentAdapter);
		for (int i = 0; i < 5; i++) {
			commentAdapter.addItem(new BespokeVo(), 0);
		}
		Utils.setListViewHeightBasedOnChildren(listView);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				sv_view.scrollTo(0, 0);
			}
		}, 300);

		mQueue = Volley.newRequestQueue(activity);

		String id = (String) getVo("0");

		showDialog();
		queryDetail(id);
		queryComment(id, 1);
		changeTextColor(0);
	}

	/**
	 * 获取网络数据
	 * @param id
     */
	private void queryDetail(String id) {
		GetRequest req = new GetRequest(activity, PersonalCenterUtils.buildUrl(
				activity, Const.MY_PHOTO_DETAIL) + "&id=" + id,
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (vo.isSucess()) {
							try {
								MyPhotoVo photo = GsonTools.getVo(
										obj.getJSONObject("data")
												.getJSONObject("detail")
												.toString(), MyPhotoVo.class);
								// if(!Utils.isEmpty(photo.getDesc())){
								// tv_desc.setText(photo.getDesc());
								// }else{
								// tv_desc.setVisibility(View.GONE);
								// }
								tv_desc.setText("2010年以前，台北101是世界第一高楼（但不是世界最高建筑，当时的世界最高建筑是加拿大的CN塔），2010年1月4日迪拜塔的建成（828米）使得台北101退居世界第二高楼。2014年8月3日结构封顶，将于2015年年中投入使用的上海中心大厦（632米）已经远超台北101楼509米。");
								if (!Utils.isEmpty(photo.getPics())) {
									List<Pic> pics = new ArrayList<MyPhotoVo.Pic>();
									for (int i = 0; i < 3; i++) {
										Pic pic = new Pic();
										pic.setPic_desc("台北101多节式外观");
										pic.setCreatetime(System
												.currentTimeMillis()
												/ 1000
												+ "");
										pic.setUpload_address("台湾省台北市信义区" + i
												+ "号");
										pics.add(pic);
									}
									// showPics(photo.getPics());
									showPics(pics);
								}

							} catch (JSONException e) {
							}
						} else {
							toast(vo.getMsg());
						}
					}

					@Override
					public void doFailed() {
						dismissDialog();
						toast("获取数据失败");
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 请求评论数据
	 * @param id
	 * @param pageNum
     */
	private void queryComment(String id, int pageNum) {
		GetRequest req = new GetRequest(activity, PersonalCenterUtils.buildUrl(
				activity, Const.MY_PHOTO_COMMENT + id + "/" + pageNum),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (vo.isSucess()) {
							try {
								MyPhotoVo photo = GsonTools.getVo(
										obj.getJSONObject("data")
												.getJSONObject("detail")
												.toString(), MyPhotoVo.class);
								if (!Utils.isEmpty(photo.getPics())) {

								}

							} catch (JSONException e) {
							}
						} else {
							toast(vo.getMsg());
						}
					}

					@Override
					public void doFailed() {
						toast("获取数据失败");
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 改变Icon
	 * @param position
	 * @param pics
     */
	private void changeIcon(int position, List<Pic> pics) {
		if (ll_container.getChildCount() < 1) {
			return;
		}

		tv_local.setText(pics.get(position).getUpload_address());
		tv_time.setText(Utils.formatTime(pics.get(position).getCreatetime()
				+ "000", "yyyy-MM-dd"));
		for (int i = 0; i < ll_container.getChildCount(); i++) {
			if (i == position) {
				ImageView iv = (ImageView) ll_container.getChildAt(i);
				iv.setImageResource(R.drawable.icon_pic_selected);
			} else {
				ImageView iv = (ImageView) ll_container.getChildAt(i);
				iv.setImageResource(R.drawable.icon_pic_unselected);
			}
		}
	}

	/**
	 * 展示照片
	 * @param pics
     */
	private void showPics(final List<Pic> pics) {
		ImageLoader imageLoader = new ImageLoader(mQueue, BitmapCache.getInstance());
		List<View> views = new ArrayList<View>();
		for (int i = 0; i < pics.size(); i++) {
			NetworkImageView iv = (NetworkImageView) makeView(R.layout.item_pic_detail);
			// iv.setImageUrl(pics.get(i).getPic_path(), imageLoader);
			iv.setBackgroundResource(R.drawable.icon_101);
			views.add(iv);
		}
		adapter = new SimplePageAdapter(activity, views);
		vp_pics.setAdapter(adapter);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
		lp.setMargins(0, 0, 10, 5);
		for (int i = 0; i < pics.size(); i++) {
			ImageView temp = new ImageView(activity);
			temp.setImageResource(R.drawable.icon_pic_unselected);
			ll_container.addView(temp, i, lp);
		}

		changeIcon(0, pics);
		vp_pics.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				changeIcon(arg0, pics);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	/**
	 * 改变字体颜色
	 * @param position
     */
	private void changeTextColor(int position) {
		if (position == 0) {
			tv_comment.setTextColor(Color.parseColor("#50BBDB"));
			tv_parise.setTextColor(Color.parseColor("#999999"));
			tv_share.setTextColor(Color.parseColor("#999999"));

			tv_comment.setCompoundDrawablesWithIntrinsicBounds(null, null,
					null,
					activity.getDrawableRes(R.drawable.icon_tips_selected));
			tv_parise.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					activity.getDrawableRes(R.drawable.icon_tips_unselected));

			tv_share.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					activity.getDrawableRes(R.drawable.icon_tips_unselected));

		} else if (position == 1) {
			tv_share.setTextColor(Color.parseColor("#50BBDB"));
			tv_parise.setTextColor(Color.parseColor("#999999"));
			tv_comment.setTextColor(Color.parseColor("#999999"));

			tv_share.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					activity.getDrawableRes(R.drawable.icon_tips_selected));
			tv_parise.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					activity.getDrawableRes(R.drawable.icon_tips_unselected));

			tv_comment.setCompoundDrawablesWithIntrinsicBounds(null, null,
					null,
					activity.getDrawableRes(R.drawable.icon_tips_unselected));
		} else {
			tv_parise.setTextColor(Color.parseColor("#50BBDB"));
			tv_share.setTextColor(Color.parseColor("#999999"));
			tv_comment.setTextColor(Color.parseColor("#999999"));

			tv_parise.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					activity.getDrawableRes(R.drawable.icon_tips_selected));
			tv_share.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					activity.getDrawableRes(R.drawable.icon_tips_unselected));

			tv_comment.setCompoundDrawablesWithIntrinsicBounds(null, null,
					null,
					activity.getDrawableRes(R.drawable.icon_tips_unselected));
		}
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_photo_test;
	}
}
