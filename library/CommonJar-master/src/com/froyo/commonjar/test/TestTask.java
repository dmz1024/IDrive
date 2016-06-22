//package com.froyo.commonjar.test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.froyo.commonjar.activity.BaseActivity;
//import com.froyo.commonjar.task.BaseTask;
//import com.froyo.commonjar.view.CustomListView;
//
//public class TestTask extends BaseTask<Void, Void, List<String>> {
//
//	private TestAdapter adapter;
//
//	private CustomListView listView;
//
//	/** true:下拉刷新，false:滚动到底部自动加载 */
//	private boolean isPull;
//	
//	public TestTask(BaseActivity activity, CustomListView listView,
//			TestAdapter adapter, boolean isPull) {
//		super(activity);
//		this.adapter = adapter;
//		this.listView = listView;
//		this.isPull = isPull;
//	}
//
//	@Override
//	public List<String> doExecute(Void param) throws Exception {
//		Thread.sleep(1000);
//		List<String> tempArray = new ArrayList<String>();
//		for (int i = 0; i < 15; i++) {
//			tempArray.add("我是第" + i + "个");
//		}
//		return tempArray;
//	}
//
//	@Override
//	public void doResult(List<String> result) throws Exception {
//		adapter.addItems(result);
//		if (isPull) {
//			listView.onRefreshComplete();
//			listView.setCanLoadMore(true);
//		} else {
//			listView.onLoadMoreComplete();
//		}
//	}
//
//}
