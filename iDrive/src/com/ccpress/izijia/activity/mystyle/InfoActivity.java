package com.ccpress.izijia.activity.mystyle;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.widget.*;
import com.ccpress.izijia.fragment.UserCenterFragment;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import com.trs.util.log.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.GetRequest;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.AppUtils;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.SpUtil;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.view.CustomListView;
import com.froyo.commonjar.view.CustomListView.OnLoadMoreListener;
import com.froyo.commonjar.view.CustomListView.OnRefreshListener;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.froyo.commonjar.xutils.view.annotation.event.OnClick;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.InfoAdapter;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.BespokeVo;
import com.ccpress.izijia.vo.InfoVo;
import com.ccpress.izijia.vo.MyAttentionVo;
import com.ccpress.izijia.vo.ResponseVo;
import com.ccpress.izijia.vo.TrendVo;

import de.greenrobot.event.EventBus;

/**
 * 个人中心
 * 
 * @author wangyi
 * 
 */
public class InfoActivity extends BaseActivity {

	@ViewInject(R.id.btn_msg)
	private Button btn_msg;

	 @ViewInject(R.id.btn_followed)
	private Button btn_followed;

	@ViewInject(R.id.tv_name)
	private TextView tv_name;

	 @ViewInject(R.id.tv_follow)
	private TextView tv_follow;

	 @ViewInject(R.id.tv_fans)
	private TextView tv_fans;

	 @ViewInject(R.id.iv_avatar)
	private ImageView iv_avatar;

	@ViewInject(R.id.ll_container)
	private LinearLayout ll_container;

	private RequestQueue mQueue;

	@ViewInject(R.id.lv_page_list)
	private CustomListView listView;

	private InfoAdapter adapter;

	private InfoVo info;

	public static  String tuid;

	public static String TUID=null;

	private View popupView;

	private PopupWindow popupWindow;

	private TextView tv_photo;

	private int pageNum = 1;

	private String url = Const.DOMAIN
			+ "index.php?s=/interaction/app/getUserInteractionList";
	TitleBar bar;

	/**
	 * 初始化布局，数据加载
	 */
	@Override
	public void doBusiness() {
		bar = new TitleBar(activity);
		bar.setTitle("个人中心");
		bar.showBack();

		tuid = (String) getVo(TUID);

		mQueue = Volley.newRequestQueue(this);
		Log.e("TUID", String.valueOf(tuid));
		queryInfo(tuid);
		pullToRefresh();

		adapter = new InfoAdapter(new ArrayList<TrendVo>(), activity,
				R.layout.item_info);
		listView.setAdapter(adapter);

		listView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				loadMore();
			}
		});
		listView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pullToRefresh();
			}
		});
		pullToRefresh();
	}

	/**
	 * 举报
	 */
	private void doReport() {
		showDialog();
		PostParams params = new PostParams();
		params.put("type", "19");
		params.put("row_id", tuid);
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.REPORT),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (vo.isSucess()) {
							toast("已举报");
						} else {
							toast(vo.getMsg());
						}
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 获取用户中心基本信息
	 * @param tuid
     */
	private void queryInfo(final String tuid) {

		showDialog();
		Log.e("queryURL", PersonalCenterUtils.buildUrl(
				activity, Const.MY_INFO + "&tuid=" + tuid) );

		GetRequest req = new GetRequest(activity, PersonalCenterUtils.buildUrl(
				activity, Const.MY_INFO + "&tuid=" + tuid), new RespListener(
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
						Log.e("getResp ",obj.toString());
						View header = makeView(R.layout.item_header);
						header.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								
							}
						});

						btn_msg = (Button) header.findViewById(R.id.btn_msg);

						btn_msg.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (info != null) {
									skip(MsgDetailActivity.class,
											info.getNickname(), info.getUid());
								}
							}
						});

						btn_followed = (Button) header
								.findViewById(R.id.btn_followed);
						
						btn_followed.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								if ("关注".equals(btn_followed.getText().toString())) {
									showWindow(tuid, "关注");
								} else {
									showWindow(tuid, "取消关注");
								}
								
							}
						});
						tv_name = (TextView) header.findViewById(R.id.tv_name);
						tv_follow = (TextView) header
								.findViewById(R.id.tv_follow);
						tv_fans = (TextView) header.findViewById(R.id.tv_fans);
						iv_avatar = (ImageView) header
								.findViewById(R.id.iv_avatar);

						listView.addHeaderView(header);

						SpUtil sp = new SpUtil(activity);
						if (!tuid.equals(sp.getStringValue(Const.UID))) {
							bar.showRightText(new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									doReport();
								}
							}, "举报");
							btn_followed.setVisibility(View.VISIBLE);
							btn_msg.setVisibility(View.VISIBLE);
						}

						if (info.isFollowed()) {
							btn_followed.setText("已关注");
						} else {
							btn_followed.setText("关注");
						}
						tv_name.setText(info.getNickname());
						tv_follow.setText("关注  \n " + info.getFollowing());
						tv_fans.setText("粉丝  \n " + info.getFans());
						showAvatar(info.getAvatar());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					toast(vo.getMsg());
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
	 * 用户的 头像
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
				}, 0, 0, Config.ARGB_8888, null);
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 弹出的底部window
	 * @param uid
	 * @param text
     */
		private void showWindow(final String uid, final String text) {
		if (popupView == null) {
			popupView = makeView(R.layout.view_follow_window);
			popupWindow = new PopupWindow(popupView,
					WindowManager.LayoutParams.MATCH_PARENT,
					AppUtils.getHeight(activity));
			LinearLayout window = (LinearLayout) popupView
					.findViewById(R.id.ll_window);
			LinearLayout content = (LinearLayout) popupView
					.findViewById(R.id.ll_content);
			TextView tv_cancel = (TextView) popupView
					.findViewById(R.id.tv_cancel);
			tv_photo = (TextView) popupView.findViewById(R.id.tv_photo);

			tv_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
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
		tv_photo.setText(text);
		tv_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (text.equals("关注")) {
					addFollow(uid);
				} else {
					cancleFollow(uid);
				}
			}
		});
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			popupWindow.showAtLocation(ll_container, Gravity.BOTTOM
					| Gravity.CENTER_HORIZONTAL, 0, 0);
		}
	}

	/**
	 * 取消关注
	 * @param id
     */
	private void cancleFollow(String id) {
		activity.showDialog();
		RequestQueue mQueue = Volley.newRequestQueue(activity);

		PostParams params = new PostParams();
		params.put("unfollow_uid", id);
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.CANCEL_ATTENTION),
				new RespListener(activity) {
					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						Log.e("cancleFollow", obj.toString());
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (vo.isSucess()) {
							activity.toast("已取消关注");
							btn_followed.setText("关注");
							popupWindow.dismiss();
						} else {
							activity.toast("取消失败");
						}
					}

					@Override
					public void doFailed() {
						dismissDialog();
						activity.toast("取消失败");
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 关注
	 * @param id
     */
	private void addFollow(String id) {
		activity.showDialog();
		GetRequest req = new GetRequest(activity, PersonalCenterUtils.buildUrl(
				activity, Const.ADD_ATTENTION) + "&follow_uid=" + id,
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						Log.e("addFollow", obj.toString());
						dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (vo.isSucess()) {
							activity.toast("成功关注");
							btn_followed.setText("已关注");
							popupWindow.dismiss();
						} else {
							activity.toast("关注失败");
						}
					}

					@Override
					public void doFailed() {
						dismissDialog();
						activity.toast("关注失败");
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 刷新数据
	 */
	private void pullToRefresh() {
		PostParams params = new PostParams();
		params.put("pageIndex", 1 + "");
		params.put("tuid", tuid);
		GetRequest req = new GetRequest(activity, PersonalCenterUtils.buildUrl(getActivity(),url
				+ "&tuid=" + tuid), new RespListener(activity) {

			@Override
			public void getResp(JSONObject obj) {
				try {
					Log.e("pullToRefresh", obj.toString());
					List<TrendVo> datas = TrendVo.convertToVo(obj
							.getJSONObject("data").getJSONArray("list"));

					if (Utils.isEmpty(datas)) {
						toast("尚未有互动内容");
					} else {

						adapter.removeAll();
						adapter.addItems(datas);
						int total_page = obj.getJSONObject("data").getInt(
								"totalPage");
						if (total_page < 2) {
							listView.setHasNoMoreData();
						} else {
							pageNum = 2;
							listView.setCanLoadMore(true);
						}
					}
				} catch (Exception e) {
				}
				listView.onRefreshComplete();
			}

			@Override
			public void doFailed() {
			//toast("获取数据失败");
				listView.onRefreshComplete();
			}
		});
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 加载更多数据
	 */
	private void loadMore() {
		PostParams params = new PostParams();
		params.put("pageIndex", pageNum + "");
		params.put("tuid", tuid);
		GetRequest req = new GetRequest(activity, url + "&pageIndex=" + pageNum
				+ "&tuid=" + tuid, new RespListener(activity) {

			@Override
			public void getResp(JSONObject obj) {
			    dismissDialog();
				try {
					List<TrendVo> datas = TrendVo.convertToVo(obj
							.getJSONObject("data").getJSONArray("list"));
					int total_page = obj.getJSONObject("data").getInt(
							"totalPage");
					if (!Utils.isEmpty(datas)) {
						pageNum++;
						adapter.addItems(datas);
						if (total_page < pageNum) {
							listView.setHasNoMoreData();
						} else {
							listView.setAutoLoadMore(true);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				listView.onLoadMoreComplete();
			}

			@Override
			public void doFailed() {
			    dismissDialog();
				listView.onLoadMoreComplete();
				listView.setAutoLoadMore(false);
			}
		});
		mQueue.add(req);
		mQueue.start();
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_info;
	}
	
	@Override
	protected void onDestroy() {
		EventBus.getDefault().post(new UserCenterFragment.ReFreshEvent());
		EventBus.getDefault().post(new MyAttentionActivity.ReFreshEvent());
		EventBus.getDefault().post(new MyFansActivity.ReFreshEvent());
		super.onDestroy();
	}
}
