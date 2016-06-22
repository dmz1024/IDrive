package com.ccpress.izijia.activity.line;

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.line.LineEditActivity.GetAddViewPointsEvent;
import com.ccpress.izijia.adapter.MyCollectViewAdapter;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.BespokeVo;
import com.ccpress.izijia.vo.CollectVo;
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

import de.greenrobot.event.EventBus;

/**
 * 
 * @Des: 添加看点
 * @author Rhino
 * @version V1.0
 * @created 2015年5月6日 下午3:25:22
 */
public class AddViewPointActivity extends BaseActivity {

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
	private CustomListView page5ListView;
	private CustomListView page6ListView;
	private int pageNum1 = 1;
	private int pageNum2 = 1;
	private int pageNum5 = 1;
	private int pageNum6 = 1;

	private MyCollectViewAdapter adapter1;//看点
	private MyCollectViewAdapter adapter2;//私享点
	private MyCollectViewAdapter adapter5;//高德地图点
	private MyCollectViewAdapter adapter6;//停车发呆地

	private TitleBar bar;

	private RequestQueue mQueue;
	public static int Bespoke=0;
	@Override
	public void doBusiness() {
		bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("添加看点");
		mQueue = Volley.newRequestQueue(this);
		bar.showRightText(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ("完成".equals(bar.tv_right.getText().toString())) {
					// bar.tv_right.setText("添加");
					adapter1.getSelectList();
					List<BespokeVo> selectData = new ArrayList<BespokeVo>();
					if (!Utils.isEmpty(adapter1.getSelectList())) {
						selectData.addAll(adapter1.getSelectList());
					}
					if (!Utils.isEmpty(adapter2.getSelectList())) {
						selectData.addAll(adapter2.getSelectList());
					}
					if (!Utils.isEmpty(adapter5.getSelectList())) {
						selectData.addAll(adapter5.getSelectList());
					}
					if (!Utils.isEmpty(adapter6.getSelectList())) {
						selectData.addAll(adapter6.getSelectList());
					}
							if (Bespoke<=15){
								if (15-Bespoke>=selectData.size()){
									GetAddViewPointsEvent event = new GetAddViewPointsEvent();
									event.setData(selectData);
									EventBus.getDefault().post(event);
									finish();
										}else {
									Toast.makeText(getActivity(),"添加看点数量超过15个",Toast.LENGTH_LONG).show();
									}
						}else {
								Toast.makeText(getActivity(),"无法添加看点，数量超过15个",Toast.LENGTH_LONG).show();
							}

				} else {
					bar.tv_right.setText("完成");
					adapter1.showAll();
					adapter2.showAll();
					adapter5.showAll();
					adapter6.showAll();
				}
			}
		}, "添加");

		List<View> lists = new ArrayList<View>();
		for (int i = 1; i < 5; i++) {

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
				View temp = view.findViewById(R.id.view_divider);
				lists.add(view);
				temp.setVisibility(View.GONE);
				initPage5(view);
			} else if (i == 4) {
				View view = activity.makeView(R.layout.layout_collect_page);
				View temp = view.findViewById(R.id.view_divider);
				lists.add(view);
				temp.setVisibility(View.GONE);
				initPage6(view);
			}
		}

		SimplePageAdapter adapter = new SimplePageAdapter(activity, lists);

		vp_travel.setAdapter(adapter);
		vp_travel.setOffscreenPageLimit(3);
		vp_travel.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				changeBtnBg(position, false);
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
		names.add("高德地图点");
		names.add("停车发呆地");
		addTab(names);
		dismissDialog();
	}

	public void selectItem(int position, List<String> datas, View view) {
		vp_travel.setCurrentItem(position);
	}

	/**
	 * 改变page，数据改变
	 * @param position
	 * @param init
     */
	private void changeBtnBg(int position, boolean init) {

		if (position == 0) {
			initPage(page1ListView, adapter1, position + 1,
					PersonalCenterUtils.buildUrl(activity, Const.COLLECT_VIEW));
		} else if (position == 1) {
			initPage(page2ListView, adapter2, position + 1,
					PersonalCenterUtils.buildUrl(activity, Const.COLLECT_SHARE));
		} else if (position == 2) {
			initPage(page5ListView, adapter5, position + 1,
					PersonalCenterUtils.buildUrl(activity, Const.COLLECT_MAP));
		} else if (position == 3) {
			initPage(page6ListView, adapter6, position + 1,
					PersonalCenterUtils.buildUrl(activity, Const.COLLECT_STOP));
		}
		if (!init) {

			scrollToChild(position,
					(int) (0.3 * tab_container.getChildAt(position).getWidth()));
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
		}else{
			scrollToChild(0,
					(int) (0.3 * tab_container.getChildAt(0).getWidth()));
			for (int i = 0; i < tab_container.getChildCount(); i++) {
				LinearLayout v = (LinearLayout) tab_container.getChildAt(i);
				if (i == 0) {
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

		width = AppUtils.getWidth(activity) / 3;
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
		changeBtnBg(0, true);
		changeBtnBg(1, true);
		changeBtnBg(2, true);
		changeBtnBg(3, true);
	}

	/**
	 * 初始化page
	 * @param listView
	 * @param adapter
	 * @param pageOrder
     * @param url
     */
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
	 * 初始化高德地图点page布局
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
				pullRefresh(page5ListView, adapter5, 3, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_MAP));
			}
		});

		page5ListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				loadMore(page5ListView, adapter5, 3, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_SHARE), pageNum5);
			}
		});
	}

	/**
	 * 初始化停车发呆地page布局
	 * @param view
     */
	private void initPage6(View view) {
		page6ListView = (CustomListView) view.findViewById(R.id.lv_page_list);
		adapter6 = new MyCollectViewAdapter(new ArrayList<CollectVo>(),
				activity, R.layout.item_collect);
		page6ListView.setAdapter(adapter6);
		page6ListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pullRefresh(page6ListView, adapter6, 4, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_STOP));
			}
		});

		page6ListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				loadMore(page6ListView, adapter6, 4, PersonalCenterUtils
						.buildUrl(activity, Const.COLLECT_STOP), pageNum6);
			}
		});
	}

	/**
	 * 数据刷新
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

						} else if (pageOrder == 4) {
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

						listView.onRefreshComplete();
					}

					@Override
					public void doFailed() {
						listView.onRefreshComplete();
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 加载更多数据
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
									if (bar.tv_right.getText().toString().equals("完成")) {
										adapter1.showAll();
									}
									if (totalPage < pageNum1) {
										listView.setHasNoMoreData();
									} else {
										listView.setAutoLoadMore(true);
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
									pageNum2++;
									adapter.addItems(datas);
									adapter2.notifyDataSetChanged();
									if (bar.tv_right.getText().toString().equals("完成")) {
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
						} else if (pageOrder == 3) {
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
									if (bar.tv_right.getText().toString().equals("完成")) {
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

						} else if (pageOrder == 4) {
							try {
								List<CollectVo> datas = GsonTools.getList(
										obj.getJSONObject("data").getJSONArray(
												"list"), CollectVo.class);
								int totalPage = obj.getJSONObject("data")
										.getInt("total_page");
								if (!Utils.isEmpty(datas)) {
									pageNum6++;
									adapter.addItems(datas);
									adapter6.notifyDataSetChanged();
									if (bar.tv_right.getText().toString().equals("完成")) {
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

						}
						listView.onLoadMoreComplete();
					}

					@Override
					public void doFailed() {
						listView.onLoadMoreComplete();
						listView.setAutoLoadMore(false);
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
