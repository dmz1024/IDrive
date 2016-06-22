//package com.froyo.commonjar.test;
//
//import java.util.ArrayList;
//
//import com.froyo.commonjar.R;
//import com.froyo.commonjar.activity.BaseActivity;
//import com.froyo.commonjar.view.CustomListView;
//import com.froyo.commonjar.view.CustomListView.OnLoadMoreListener;
//import com.froyo.commonjar.view.CustomListView.OnRefreshListener;
//import com.froyo.commonjar.xutils.view.annotation.ViewInject;
//
//public class TestActivity extends BaseActivity {
//
//	@ViewInject(R.id.list_view)
//	private CustomListView listView;
//
//	private TestAdapter adapter;
//
//	@Override
//	public void doBusiness() {
//		adapter = new TestAdapter(new ArrayList<String>(), activity,
//				R.layout.test_item);
//		listView.setAdapter(adapter);
//		listView.setOnRefreshListener(new OnRefreshListener() {
//
//			@Override
//			public void onRefresh() {
//				new TestTask(activity, listView, adapter, true).execute();
//			}
//		});
//
//		listView.setOnLoadListener(new OnLoadMoreListener() {
//
//			@Override
//			public void onLoadMore() {
//				// listView.setHasNoMoreData();（加载更多，没有更多数据时调用）
//				// listView.setAutoLoadMore(false);(加载更多失败，变为手动加载调用)--（手动加载更多成功应该恢复到自动加载listView.setAutoLoadMore(true)）
//				new TestTask(activity, listView, adapter, false).execute();
//			}
//		});
//
//	}
//
//	@Override
//	protected int setLayoutResID() {
//		return R.layout.activity_test;
//	}
//
//}
