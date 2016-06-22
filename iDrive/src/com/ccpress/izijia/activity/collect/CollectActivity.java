package com.ccpress.izijia.activity.collect;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.HdMapActivity;
import com.ccpress.izijia.adapter.MyCollectRouteAdapter;
import com.ccpress.izijia.adapter.MyCollectViewAdapter;
import com.ccpress.izijia.adapter.MyCollectViewAdapter1;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.dfy.entity.Collect;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.CollectRouteVo;
import com.ccpress.izijia.vo.CollectVo;
import com.ccpress.izijia.vo.CollectVo1;
import com.ccpress.izijia.vo.ResponseVo;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimpleAdapter;
import com.froyo.commonjar.adapter.SimplePageAdapter;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.AppUtils;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.view.CustomListView;
import com.froyo.commonjar.view.CustomListView.OnLoadMoreListener;
import com.froyo.commonjar.view.CustomListView.OnRefreshListener;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import org.json.JSONException;
import org.json.JSONObject;
import com.trs.util.log.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @Des: 我的收藏
 * @author Rhino
 * @version V1.0
 * @created 2015年5月6日 下午3:25:22
 */
public class CollectActivity extends BaseActivity {

	@ViewInject(R.id.vp_travel)
	private ViewPager vp_travel;

	@ViewInject(R.id.ll_tab_container)
	private LinearLayout tab_container;

	@ViewInject(R.id.hs_tab)
	private HorizontalScrollView hs_tab;

	private int lastScrollX = 0;

	private int width = 0;

	public int currentIndex = 0;

	private CustomListView page1ListView;
	private CustomListView page2ListView;
	private CustomListView page3ListView;
	 private CustomListView page4ListView;
	private CustomListView page5ListView;
	private CustomListView page6ListView;
	private CustomListView page7ListView;
	private int pageNum1 = 1;
	private int pageNum2 = 1;
	private int pageNum3 = 1;
	 private int pageNum4 = 1;
	private int pageNum5 = 1;
	private int pageNum6 = 1;
	private int pageNum7 = 1;
	private MyCollectViewAdapter adapter1;
	private MyCollectViewAdapter adapter2;
	private MyCollectRouteAdapter adapter3;
	private MyCollectViewAdapter adapter4;
	private MyCollectViewAdapter adapter5;
	private MyCollectViewAdapter adapter6;
	private MyCollectViewAdapter adapter7;


	/** 保存页面来回切换时，标题栏的状态，以及标题文字和对应的处理事件 */
	private SparseArray<OnClickListener> map = new SparseArray<OnClickListener>();

	private SparseArray<String> titleMap = new SparseArray<String>();

	TitleBar bar;

	private RequestQueue mQueue;

	@Override
	public void doBusiness() {
		bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("我的收藏");
		mQueue = Volley.newRequestQueue(this);

		titleMap.put(1, "编辑");
		titleMap.put(2, "编辑");
		titleMap.put(3, "编辑");
		titleMap.put(4, "编辑");
		titleMap.put(5, "编辑");
		titleMap.put(6, "编辑");
		titleMap.put(7, "编辑");
		//map中记录预删除数据
		map.put(1, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (bar.tv_right.getText().toString().equals("编辑")) {
					bar.tv_right.setText("删除");
					titleMap.put(1, "删除");
					adapter1.showAll();
				} else {
					if (Utils.isEmpty(adapter1.getSelectIds())) {
						toast("请选择要删除的收藏项");
					} else {
						delete(adapter1.getSelectIds(), 1);
					}
				}
			}
		});
		//map中记录预删除数据
		map.put(2, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (bar.tv_right.getText().toString().equals("编辑")) {
					bar.tv_right.setText("删除");
					titleMap.put(2, "删除");
					adapter2.showAll();
				} else {
					if (Utils.isEmpty(adapter2.getSelectIds())) {
						toast("请选择要删除的收藏项");
					} else {
						delete(adapter2.getSelectIds(), 2);
					}
				}
			}
		});

		//map中记录预删除数据
		map.put(3, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (bar.tv_right.getText().toString().equals("编辑")) {
					bar.tv_right.setText("删除");
					titleMap.put(3, "删除");
					adapter3.showAll();
				} else {
					if (Utils.isEmpty(adapter3.getSelectIds())) {
						toast("请选择要删除的收藏项");
					} else {
						delete(adapter3.getSelectIds(), 3);
						Log.e( "adapter3.getSelectIds() ",adapter3.getSelectIds());
					}
				}
			}
		});
		//map中记录预删除数据
		map.put(4, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (bar.tv_right.getText().toString().equals("编辑")) {
					bar.tv_right.setText("删除");
					titleMap.put(4, "删除");
					adapter4.showAll();
				} else {
					if (Utils.isEmpty(adapter4.getSelectIds())) {
						toast("请选择要删除的收藏项");
					} else {
						delete(adapter4.getSelectIds(), 4);
					}
				}
			}
		});
		//map中记录预删除数据

		map.put(5, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (bar.tv_right.getText().toString().equals("编辑")) {
					bar.tv_right.setText("删除");
					titleMap.put(5, "删除");
					adapter5.showAll();
				} else {
					if (Utils.isEmpty(adapter5.getSelectIds())) {
						toast("请选择要删除的收藏项");
					} else {
						delete(adapter5.getSelectIds(), 5);
					}
				}
			}
		});

		map.put(6, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (bar.tv_right.getText().toString().equals("编辑")) {
					bar.tv_right.setText("删除");
					titleMap.put(6, "删除");
					adapter6.showAll();
				} else {
					if (Utils.isEmpty(adapter6.getSelectIds())) {
						toast("请选择要删除的收藏项");
					} else {
						delete(adapter6.getSelectIds(), 6);
					}
				}
			}
		});
		map.put(7, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (bar.tv_right.getText().toString().equals("编辑")) {
					bar.tv_right.setText("删除");
					titleMap.put(7, "删除");
					adapter7.showAll();
				} else {
					if (Utils.isEmpty(adapter7.getSelectIds())) {
						toast("请选择要删除的收藏项");
					} else {
						delete(adapter7.getSelectIds(), 7);
					}
				}
			}
		});


		List<View> lists = new ArrayList<View>();
		for (int i = 1; i < 8; i++) {

			if (i == 1) {
				View view = activity.makeView(R.layout.layout_collect_page);
				View temp = view.findViewById(R.id.view_divider);
				lists.add(view);
				temp.setVisibility(View.GONE);
				initPage1(view);
			} else if (i == 2) {
				View view = activity.makeView(R.layout.layout_collect_page);
				View temp = view.findViewById(R.id.view_divider);
				lists.add(view);
				temp.setVisibility(View.GONE);
				initPage2(view);
			} else if (i == 3) {
				View view = activity.makeView(R.layout.layout_collect_page);
				lists.add(view);
				initPage3(view);
			}
			
			else if (i == 4) {
				View view = activity.makeView(R.layout.layout_collect_page);
				View temp = view.findViewById(R.id.view_divider);
				lists.add(view);
				temp.setVisibility(View.GONE);
				initPage4(view);
			} else if (i == 5) {
				View view = activity.makeView(R.layout.layout_collect_page);
				View temp = view.findViewById(R.id.view_divider);
				lists.add(view);
				temp.setVisibility(View.GONE);
				initPage5(view);
			} else if (i == 6) {
				View view = activity.makeView(R.layout.layout_collect_page);
				View temp = view.findViewById(R.id.view_divider);
				lists.add(view);
				temp.setVisibility(View.GONE);
				initPage6(view);
			}else if (i == 7) {
				View view = activity.makeView(R.layout.layout_collect_page);
				View temp = view.findViewById(R.id.view_divider);
				lists.add(view);
				temp.setVisibility(View.GONE);
				initPage7(view);
			}
		}

		SimplePageAdapter adapter = new SimplePageAdapter(activity, lists);

		vp_travel.setAdapter(adapter);

		vp_travel.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				changeBtnBg(position);
				currentIndex = position;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		List<String> names = new ArrayList<String>();
		names.add("看点");
		names.add("私享点");
		names.add("线路");
		names.add("高德地图点");
		names.add("停车发呆地");
		names.add("自驾团");
		names.add("营地");
		addTab(names);

	}

	public void selectItem(int position, List<String> datas, View view) {
		vp_travel.setCurrentItem(position);
	}

	/**
	 * 位置改变后，adapter的数据改变
	 * @param position
     */
	private void changeBtnBg(int position) {

		bar.showRightText(map.get(position + 1), titleMap.get(position + 1));

		if (position == 0) {
			initPage(page1ListView, adapter1, position + 1,
					PersonalCenterUtils.buildUrl(activity, Const.COLLECT_VIEW));
		} else if (position == 1) {
			initPage(page2ListView, adapter2, position + 1,
					PersonalCenterUtils.buildUrl(activity, Const.COLLECT_SHARE));
		} else if (position == 2) {
			initPage(page3ListView, adapter3, position + 1,
					PersonalCenterUtils.buildUrl(activity, Const.COLLECT_ROUTE));
		}
		else if (position == 3) {
			initPage(page4ListView, adapter4, position + 1,
					PersonalCenterUtils.buildUrl(activity, Const.COLLECT_MAP));
		} else if (position == 4) {
			initPage(page5ListView, adapter5, position + 1,
					PersonalCenterUtils.buildUrl(activity, Const.COLLECT_STOP));
		}else if (position == 5) {
			initPage(page6ListView, adapter6, position + 1,
					PersonalCenterUtils.buildUrl(activity, Const.COLLECT_ZJT));
		}else if (position == 6) {
			initPage(page7ListView, adapter7, position + 1,
					PersonalCenterUtils.buildUrl(activity, Const.COLLECT_YD));
		}

		scrollToChild(position, (int) (0.3 * tab_container.getChildAt(position)
				.getWidth()));
		for (int i = 0; i < tab_container.getChildCount(); i++) {
			LinearLayout v = (LinearLayout) tab_container.getChildAt(i);
			if (i == position) {
				v.getChildAt(1).setVisibility(View.VISIBLE);
				((TextView) v.getChildAt(0)).setTextColor(getResources()
						.getColor(R.color.base_color));
			} else {
				v.getChildAt(1).setVisibility(View.INVISIBLE);
				((TextView) v.getChildAt(0)).setTextColor(Color
						.parseColor("#333333"));
			}
		}
	}

	/**
	 * 点击拖动
	 * @param position
	 * @param offset
     */
	private void scrollToChild(int position, int offset) {

		int newScrollX = tab_container.getChildAt(position).getLeft() + offset;

		if (position > 0 || offset > 0) {
			newScrollX -= width;
		}
		if (position != 0) {
			if (newScrollX != lastScrollX) {
				lastScrollX = newScrollX;
				hs_tab.scrollTo(newScrollX, 0);
			}
		} else {
			lastScrollX = 0;
			hs_tab.scrollTo(0, 0);
		}

	}

	/**
	 * 添加Tab
	 * @param names
     */
	public void addTab(final List<String> names) {

		if (names.size() < 7) {
			width = AppUtils.getWidth(activity) / names.size();
		} else {
			// width = LayoutParams.WRAP_CONTENT;
			width = AppUtils.getWidth(activity) / 3;
		}
		for (int i = 0; i < names.size(); i++) {
			final String vo = names.get(i);
			View view = activity.makeView(R.layout.view_collect_tab);
			TextView text = (TextView) view.findViewById(R.id.tv_name);
			text.setText(vo);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,
					LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.CENTER_VERTICAL;
			view.setLayoutParams(lp);

			tab_container.addView(view);
			final int tempP = i;
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					selectItem(tempP, names, arg0);
				}
			});
		}
		changeBtnBg(0);
	}

	private void initPage(CustomListView listView, SimpleAdapter adapter,
			int pageOrder, String url) {
		if (adapter.isEmpty()) {
			if (pageOrder == 1 && Utils.isEmpty(adapter.getDataSource())) {
				showDialog();
			}
			pullRefresh(listView, adapter, pageOrder, url);
		}
	}

	/**
	 * 初始化看点page布局
	 * @param view
     */
	private void initPage1(View view) {
		page1ListView = (CustomListView) view.findViewById(R.id.lv_page_list);
		adapter1 = new MyCollectViewAdapter(new ArrayList<CollectVo>(),
				activity, R.layout.item_collect);
		page1ListView.setAdapter(adapter1);
		page1ListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pullRefresh(page1ListView, adapter1, 1, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_VIEW));
			}
		});

		page1ListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				// listView.setHasNoMoreData();（加载更多，没有更多数据时调用）
				// listView.setAutoLoadMore(false);(加载更多失败，变为手动加载调用)--（手动加载更多成功应该恢复到自动加载listView.setAutoLoadMore(true)）
				loadMore(page1ListView, adapter1, 1, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_VIEW), pageNum1);
			}
		});
	}

	/**
	 * 初始化私享点page布局
	 * @param view
     */
	private void initPage2(View view) {
		page2ListView = (CustomListView) view.findViewById(R.id.lv_page_list);
		adapter2 = new MyCollectViewAdapter(new ArrayList<CollectVo>(),
				activity, R.layout.item_collect);
		page2ListView.setAdapter(adapter2);
		page2ListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pullRefresh(page2ListView, adapter2, 2, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_SHARE));
			}
		});

		page2ListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				loadMore(page2ListView, adapter2, 2, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_SHARE), pageNum2);
			}
		});

	}

	/**
	 * 初始化线路page布局
	 * @param view
     */
		private void initPage3(View view) {
		page3ListView = (CustomListView) view.findViewById(R.id.lv_page_list);

		adapter3 = new MyCollectRouteAdapter(new ArrayList<CollectRouteVo>(),
				activity, R.layout.item_collect_route);
		page3ListView.setAdapter(adapter3);
		page3ListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pullRefresh(page3ListView, adapter3, 3, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_ROUTE));
			}
		});

		page3ListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				loadMore(page3ListView, adapter3, 3, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_ROUTE), pageNum3);
			}
		});

	}

	/**
	 * 初始化高德地图点Page布局
	 * @param view
     */
	private void initPage4(View view) {
		page4ListView = (CustomListView) view.findViewById(R.id.lv_page_list);
		adapter4 = new MyCollectViewAdapter(new ArrayList<CollectVo>(),
				activity, R.layout.item_collect);
		page4ListView.setAdapter(adapter4);
		page4ListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pullRefresh(page4ListView, adapter4, 4, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_MAP));
			}
		});

		page4ListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				loadMore(page4ListView, adapter4, 4, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_MAP), pageNum4);
			}
		});
	}

	/**
	 * 初始化停车发呆地page布局
	 * @param view
     */
	private void initPage5(View view) {
		page5ListView = (CustomListView) view.findViewById(R.id.lv_page_list);
		adapter5 = new MyCollectViewAdapter(new ArrayList<CollectVo>(),
				activity, R.layout.item_collect);
		page5ListView.setAdapter(adapter5);
		page5ListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pullRefresh(page5ListView, adapter5, 5, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_STOP));
			}
		});

		page5ListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				loadMore(page5ListView, adapter5, 5, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_STOP), pageNum5);
			}
		});
	}

	/**
	 * 初始化自驾团page布局
	 * @param view
	 */
	private void initPage6(View view) {
		page6ListView = (CustomListView) view.findViewById(R.id.lv_page_list);
		adapter6 = new MyCollectViewAdapter(new ArrayList<CollectVo>(),
				activity, R.layout.dfy_item_collect1,5);
		page6ListView.setAdapter(adapter6);
		page6ListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pullRefresh(page6ListView, adapter6, 6, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_ZJT));
			}
		});

		page6ListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				loadMore(page6ListView, adapter6, 6, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_ZJT), pageNum6);
			}
		});
	}

	private void initPage7(View view) {
		page7ListView = (CustomListView) view.findViewById(R.id.lv_page_list);
		adapter7 = new MyCollectViewAdapter(new ArrayList<CollectVo>(),
				activity, R.layout.dfy_item_collect1,6);
		page7ListView.setAdapter(adapter7);
		page7ListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				pullRefresh(page7ListView, adapter7, 7, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_YD));
			}
		});

		page7ListView.setOnLoadListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				loadMore(page7ListView, adapter7, 7, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_YD), pageNum7);
			}
		});
	}

	/**
	 * 刷新数据
	 * @param listView
	 * @param adapter
	 * @param pageOrder
     * @param url
     */
	private void pullRefresh(final CustomListView listView,
			final SimpleAdapter adapter, final int pageOrder, String url) {
		PostParams params = new PostParams();
		params.put("pageIndex", "1");
		PostRequest req = new PostRequest(activity, params, url,
				new RespListener(activity) {
					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						if (pageOrder == 1) {
							try {
								List<CollectVo> datas = GsonTools.getList(
										obj.getJSONObject("data").getJSONArray(
												"list"), CollectVo.class);
								int totalPage = obj.getJSONObject("data")
										.getInt("total_page");
								if (!Utils.isEmpty(datas)) {
									pageNum1 = 2;
									adapter.removeAll();
									adapter.addItems(datas);
									if (totalPage < 2) {
										listView.setHasNoMoreData();
									} else {
										listView.setAutoLoadMore(true);
										page1ListView.setCanLoadMore(true);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else if (pageOrder == 2) {
							try {
								List<CollectVo> datas = GsonTools.getList(
										obj.getJSONObject("data").getJSONArray(
												"list"), CollectVo.class);
								int totalPage = obj.getJSONObject("data")
										.getInt("total_page");
								if (!Utils.isEmpty(datas)) {
									pageNum2 = 2;
									adapter.removeAll();
									adapter.addItems(datas);
									if (totalPage < 2) {
										listView.setHasNoMoreData();
									} else {
										listView.setAutoLoadMore(true);
										page2ListView.setCanLoadMore(true);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else if (pageOrder == 3) {
							try {
								List<CollectRouteVo> datas = GsonTools.getList(
										obj.getJSONObject("data").getJSONArray(
												"list"), CollectRouteVo.class);
								int totalPage = obj.getJSONObject("data")
										.getInt("total_page");
								Log.e("CollectRouteVo", obj.getJSONObject("data").toString());
								if (!Utils.isEmpty(datas)) {
									pageNum3 = 2;
									adapter.removeAll();
									adapter.addItems(datas);
									if (totalPage < 2) {
										listView.setHasNoMoreData();
									} else {
										listView.setAutoLoadMore(true);
										page3ListView.setCanLoadMore(true);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else if (pageOrder == 4) {
							try {
								List<CollectVo> datas = GsonTools.getList(
										obj.getJSONObject("data").getJSONArray(
												"list"), CollectVo.class);
								int totalPage = obj.getJSONObject("data")
										.getInt("total_page");
								if (!Utils.isEmpty(datas)) {
									pageNum4 = 2;
									adapter.removeAll();
									adapter.addItems(datas);
									if (totalPage < 2) {
										listView.setHasNoMoreData();
									} else {
										listView.setAutoLoadMore(true);
										page4ListView.setCanLoadMore(true);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else if (pageOrder == 5) {
							try {
								List<CollectVo> datas = GsonTools.getList(
										obj.getJSONObject("data").getJSONArray(
												"list"), CollectVo.class);
								int totalPage = obj.getJSONObject("data")
										.getInt("total_page");
								if (!Utils.isEmpty(datas)) {
									pageNum5 = 2;
									adapter.removeAll();
									adapter.addItems(datas);
									if (totalPage < 2) {
										listView.setHasNoMoreData();
									} else {
										listView.setAutoLoadMore(true);
										page5ListView.setCanLoadMore(true);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else if (pageOrder ==6) {
							try {
								List<CollectVo> datas = GsonTools.getList(
										obj.getJSONObject("data").getJSONArray(
												"list"), CollectVo.class);
								int totalPage = obj.getJSONObject("data")
										.getInt("total_page");
								if (!Utils.isEmpty(datas)) {
									pageNum6 = 2;
									adapter.removeAll();
									adapter.addItems(datas);
									if (totalPage < 2) {
										listView.setHasNoMoreData();
									} else {
										listView.setAutoLoadMore(true);
										page6ListView.setCanLoadMore(true);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
						else if (pageOrder ==7) {
							try {
								List<CollectVo> datas = GsonTools.getList(
										obj.getJSONObject("data").getJSONArray(
												"list"), CollectVo.class);
								int totalPage = obj.getJSONObject("data")
										.getInt("total_page");
								if (!Utils.isEmpty(datas)) {
									pageNum7 = 2;
									adapter.removeAll();
									adapter.addItems(datas);
									if (totalPage < 2) {
										listView.setHasNoMoreData();
									} else {
										listView.setAutoLoadMore(true);
										page7ListView.setCanLoadMore(true);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					}

					@Override
					public void doFailed() {
						listView.onRefreshComplete();
						dismissDialog();
					}
				});
		mQueue.add(req);
		mQueue.start();
	}


	/**
	 * 加载更改
	 * @param listView
	 * @param adapter
	 * @param pageOrder
	 * @param url
     * @param pageNum
     */
	private void loadMore(final CustomListView listView,
			final SimpleAdapter adapter, final int pageOrder, String url,
			int pageNum) {
		PostParams params = new PostParams();
		params.put("pageIndex", pageNum + "");
		PostRequest req = new PostRequest(activity, params, url,
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						//看点
						if (pageOrder == 1) {
							try {
								List<CollectVo> datas = GsonTools.getList(
										obj.getJSONObject("data").getJSONArray(
												"list"), CollectVo.class);
								int totalPage = obj.getJSONObject("data")
										.getInt("total_page");
								if (!Utils.isEmpty(datas)) {
									pageNum1++;
									adapter.addItems(datas);

									adapter1.notifyDataSetChanged();
										if (bar.tv_right.getText().toString().equals("删除")) {
											adapter1.showAll();
										}

									if (totalPage < pageNum1) {
										listView.setHasNoMoreData();
									} else {
										listView.isShown();
										listView.setAutoLoadMore(true);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else if (pageOrder == 2) {//私享点
							try {
								List<CollectVo> datas = GsonTools.getList(
										obj.getJSONObject("data").getJSONArray(
												"list"), CollectVo.class);
								int totalPage = obj.getJSONObject("data")
										.getInt("total_page");
								if (!Utils.isEmpty(datas)) {
									pageNum2++;
									adapter.addItems(datas);

									adapter2.notifyDataSetChanged();
									if (bar.tv_right.getText().toString().equals("删除")) {
										adapter2.showAll();
									}
									if (totalPage < pageNum2) {
										listView.setHasNoMoreData();
									} else {
										listView.setAutoLoadMore(true);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else if (pageOrder == 3) {//线路
							try {
								List<CollectRouteVo> datas = GsonTools.getList(
										obj.getJSONObject("data").getJSONArray(
												"list"), CollectRouteVo.class);
								int totalPage = obj.getJSONObject("data")
										.getInt("total_page");
								if (!Utils.isEmpty(datas)) {
									pageNum3++;
									adapter.addItems(datas);
									adapter3.notifyDataSetChanged();
									if (bar.tv_right.getText().toString().equals("删除")) {
										adapter3.showAll();
									}

									if (totalPage < pageNum3) {
										listView.setHasNoMoreData();
									} else {
										listView.setAutoLoadMore(true);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else if (pageOrder == 4) {//高德地图点
							try {
								List<CollectVo> datas = GsonTools.getList(
										obj.getJSONObject("data").getJSONArray(
												"list"), CollectVo.class);
								int totalPage = obj.getJSONObject("data")
										.getInt("total_page");
								if (!Utils.isEmpty(datas)) {
									pageNum4++;
									adapter.addItems(datas);
									if (totalPage < pageNum4) {
										listView.setHasNoMoreData();
									} else {
										listView.setAutoLoadMore(true);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else if (pageOrder == 5) {//停车发呆的
							try {
								List<CollectVo> datas = GsonTools.getList(
										obj.getJSONObject("data").getJSONArray(
												"list"), CollectVo.class);
								int totalPage = obj.getJSONObject("data")
										.getInt("total_page");
								if (!Utils.isEmpty(datas)) {
									pageNum5++;
									adapter.addItems(datas);
									adapter5.notifyDataSetChanged();
									if (bar.tv_right.getText().toString().equals("删除")) {
										adapter5.showAll();
									}
									if (totalPage < pageNum5) {
										listView.setHasNoMoreData();
									} else {
										listView.setAutoLoadMore(true);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}else if (pageOrder == 6) {//自驾团
							try {
								List<CollectVo1> datas = GsonTools.getList(
										obj.getJSONObject("data").getJSONArray(
												"list"), CollectVo1.class);
								int totalPage = obj.getJSONObject("data")
										.getInt("total_page");
								if (!Utils.isEmpty(datas)) {
									pageNum6++;
									adapter.addItems(datas);

									adapter6.notifyDataSetChanged();
									if (bar.tv_right.getText().toString().equals("删除")) {
										adapter6.showAll();
									}
									if (totalPage < pageNum6) {
										listView.setHasNoMoreData();
									} else {
										listView.setAutoLoadMore(true);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}else if (pageOrder == 7) {//自驾团
							try {
								List<CollectVo1> datas = GsonTools.getList(
										obj.getJSONObject("data").getJSONArray(
												"list"), CollectVo1.class);
								int totalPage = obj.getJSONObject("data")
										.getInt("total_page");
								if (!Utils.isEmpty(datas)) {
									pageNum7++;
									adapter.addItems(datas);

									adapter7.notifyDataSetChanged();
									if (bar.tv_right.getText().toString().equals("删除")) {
										adapter7.showAll();
									}
									if (totalPage < pageNum7) {
										listView.setHasNoMoreData();
									} else {
										listView.setAutoLoadMore(true);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
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
	 * 删除收藏的内容
	 * @param ids
	 * @param pageOrder
     */
	private void delete(String ids, final int pageOrder) {
		showDialog();
		PostParams params = new PostParams();
		params.put("fav_ids", ids);
		Log.e("delete ", PersonalCenterUtils.buildUrl(activity, Const.DEL_FAV)+ "&fav_ids=" + ids);
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.DEL_FAV),
				new RespListener(activity) {
					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (vo.isSucess()) {
							if (pageOrder == 1) {
								bar.tv_right.setText("编辑");
								titleMap.put(1, "编辑");
								adapter1.doDelete();
								adapter1.hideAll();
							} else if (pageOrder == 2) {
								bar.tv_right.setText("编辑");
								titleMap.put(2, "编辑");
								adapter2.doDelete();
								adapter2.hideAll();
							} else if (pageOrder == 3) {
								bar.tv_right.setText("编辑");
								titleMap.put(3, "编辑");
								adapter3.doDelete();
								adapter3.hideAll();
							}else if (pageOrder == 4) {
								bar.tv_right.setText("编辑");
								titleMap.put(4, "编辑");
								adapter4.doDelete();
								adapter4.hideAll();
							} else if (pageOrder == 5) {
								bar.tv_right.setText("编辑");
								titleMap.put(5, "编辑");
								adapter5.doDelete();
								adapter5.hideAll();
							}else if (pageOrder == 6) {
								bar.tv_right.setText("编辑");
								titleMap.put(6, "编辑");
								adapter6.doDelete();
								adapter6.hideAll();
							}else if (pageOrder == 7) {
								bar.tv_right.setText("编辑");
								titleMap.put(7, "编辑");
								adapter7.doDelete();
								adapter7.hideAll();
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

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_collect;
	}
}
