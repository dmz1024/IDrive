package com.ccpress.izijia.activity.menu;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.view.CustomListView;
import com.froyo.commonjar.view.CustomListView.OnLoadMoreListener;
import com.froyo.commonjar.view.CustomListView.OnRefreshListener;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.MyOrderAdapter;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.MyOrderVo;

/**
 * 
 * @Des: 我的订单
 * @author Rhino
 * @version V1.0
 * @created 2015年5月6日 下午3:25:22
 */
public class MenuActivity extends BaseActivity {

	@ViewInject(R.id.lv_page_list)
	private CustomListView listView;

	private MyOrderAdapter adapter;

	private int pageNum = 1;
	
	RequestQueue mQueue ;

	@Override
	public void doBusiness() {
		mQueue = Volley.newRequestQueue(this);
		
		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("我的订单");
		adapter = new MyOrderAdapter(new ArrayList<MyOrderVo>(), activity,
				R.layout.item_collect_route);
		listView.setAdapter(adapter);

		listView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pullRefresh();
			}
		});

		listView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				loadMore();
			}
		});
		pullRefresh();
		showDialog();
	}

	/**
	 * 刷新内容，上啦刷新、下拉刷新
	 */
	private void pullRefresh() {
		PostParams params = new PostParams();
		params.put("pageIndex", "1");
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.ORDER_LIST),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						try {
							List<MyOrderVo> datas = GsonTools.getList(
									obj.getJSONObject("data").getJSONArray(
											"list"), MyOrderVo.class);
							int totalPage = obj.getJSONObject("data").getInt(
									"totalPage");
							if (!Utils.isEmpty(datas)) {
								pageNum = 2;
								adapter.removeAll();
								adapter.addItems(datas);
								if (totalPage < 2) {
									listView.setHasNoMoreData();
								} else {
									listView.setAutoLoadMore(true);
									listView.setCanLoadMore(true);
								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

						listView.onRefreshComplete();
					}

					@Override
					public void doFailed() {
						dismissDialog();
						listView.onRefreshComplete();
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 加载更多内容
	 */
	private void loadMore() {
		PostParams params = new PostParams();
		params.put("pageIndex", pageNum + "");
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.ORDER_LIST),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						try {
							List<MyOrderVo> datas = GsonTools.getList(
									obj.getJSONObject("data").getJSONArray(
											"list"), MyOrderVo.class);
							int totalPage = obj.getJSONObject("data").getInt(
									"totalPage");
							if (!Utils.isEmpty(datas)) {
								pageNum++;
								adapter.addItems(datas);
								if (totalPage < pageNum) {
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
		return R.layout.activity_menu;
	}

}
