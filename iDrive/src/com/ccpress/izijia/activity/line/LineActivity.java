package com.ccpress.izijia.activity.line;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.LinesDetailUserUploadActivity;
import com.ccpress.izijia.adapter.BespokeAdapter;
import com.ccpress.izijia.adapter.LineAdapter;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.LineInfoVo;
import com.ccpress.izijia.vo.ResponseVo;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @Des: 我的线路
 * @author Rhino
 * @version V1.0
 * @created 2015年5月6日 下午3:25:22
 */
public class LineActivity extends BaseActivity {

	@ViewInject(R.id.list_view)
	private CustomListView listView;

	private LineAdapter adapter;

	private int pageNum = 1;

	RequestQueue mQueue;
	TitleBar bar;

	@Override
	public void doBusiness() {

		mQueue = Volley.newRequestQueue(this);

		bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("我的线路");
		bar.showRightText(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (bar.tv_right.getText().toString().equals("编辑")) {
					bar.tv_right.setText("删除");
					adapter.showAll();
				} else {
					if (Utils.isEmpty(adapter.getSelectIds())) {
						adapter.showAll();
						toast("请选择要删除的线路");
					} else {
						delete(adapter.getSelectIds());
					}
				}
			}
		}, "编辑");

		adapter = new LineAdapter(new ArrayList<LineInfoVo>(), activity,
				R.layout.item_line);
		listView.setAdapter(adapter);
		//list下拉刷新
		listView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pullRefresh();
			}
		});
		//list的item点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				BespokeAdapter.BPA="LineActivity";
				LineInfoVo info=(LineInfoVo) arg0.getAdapter().getItem(arg2);
				skip(LinePreviewActivity.class,info.getId());
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
	 * 刷新数据
	 */
	private void pullRefresh() {
		PostParams params = new PostParams();
		params.put("pageIndex", "1");
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.ROUTE_LIST),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						try {
							List<LineInfoVo> datas = GsonTools.getList(
									obj.getJSONObject("data").getJSONArray(
											"list"), LineInfoVo.class);
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
	 * 加载更多数据
	 */
	private void loadMore() {
		PostParams params = new PostParams();
		params.put("pageIndex", pageNum + "");
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.ROUTE_LIST),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						try {
							List<LineInfoVo> datas = GsonTools.getList(
									obj.getJSONObject("data").getJSONArray(
											"list"), LineInfoVo.class);
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

	/**
	 * 删除数据
	 * @param ids
     */
	private void delete(String ids) {
		showDialog();
		PostParams params = new PostParams();
		params.put("ids", ids);
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.DEL_ROUTE),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (vo.isSucess()) {
							bar.tv_right.setText("编辑");
							adapter.doDelete();
							adapter.hideAll();
						} else {
							toast(vo.getMsg());
						}
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_line;
	}
}