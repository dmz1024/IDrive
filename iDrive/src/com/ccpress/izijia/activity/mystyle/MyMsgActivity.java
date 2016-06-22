package com.ccpress.izijia.activity.mystyle;

import java.util.ArrayList;
import java.util.List;

import com.ccpress.izijia.fragment.UserCenterFragment;
import com.trs.util.log.Log;
import de.greenrobot.event.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.GetRequest;
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
import com.ccpress.izijia.adapter.MyMsgAdapter;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.MyMsgVo;


/**
 * 
 * @Des: 我的消息列表
 * @author Rhino
 * @version V1.0
 * @created 2015年5月19日 上午11:50:49
 */
public class MyMsgActivity extends BaseActivity {

	@ViewInject(R.id.list_view)
	private CustomListView list_view;

	private MyMsgAdapter adapter;

	private int pageNum = 1;

	RequestQueue mQueue;

	@Override
	public void doBusiness() {

		mQueue = Volley.newRequestQueue(activity);

		TitleBar bar = new TitleBar(activity);
		bar.setTitle("我的消息");
		bar.showBack();

		adapter = new MyMsgAdapter(new ArrayList<MyMsgVo>(), activity,
				R.layout.item_my_msg);

		list_view.setAdapter(adapter);
		//item点击事件，到消息详情
		list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				MyMsgVo vo = (MyMsgVo) arg0.getAdapter().getItem(arg2);
				vo.setHas_new("false");
				((MyMsgVo) arg0.getAdapter().getItem(arg2)).setHas_new("false");
				RelativeLayout view = (RelativeLayout) arg1;
				view.findViewById(R.id.iv_has_msg).setVisibility(View.GONE);
				EventBus.getDefault().unregister(this);
				EventBus.getDefault().post(new UserCenterFragment.ReFreshEvent());
				if (vo != null) {
					skip(MsgDetailActivity.class,
							"和" + vo.getTo_user_nickname() + "的对话",
							vo.getTo_uid(), vo.getTalk_id());
				}
			}
		});



		list_view.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pullRefresh();
			}
		});

		list_view.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				loadMore();
			}
		});
		showDialog();
		pullRefresh();
		dismissDialog();
	}

	/**
	 * 刷新list的数据
	 */
	private void pullRefresh() {
		GetRequest req = new GetRequest(activity, PersonalCenterUtils.buildUrl(
				activity, Const.MY_MSG_LIST), new RespListener(activity) {

			@Override
			public void getResp(JSONObject obj) {
				try {
					List<MyMsgVo> datas = GsonTools.getList(obj.getJSONObject("data").getJSONArray("list"),
							MyMsgVo.class);
					int totalPage = obj.getJSONObject("data").getInt("totalPage");
					if (!Utils.isEmpty(datas)) {
						pageNum = 2;
						adapter.removeAll();
						adapter.addItems(datas);
						if (totalPage < 2) {
							list_view.setHasNoMoreData();
						} else {
							list_view.setAutoLoadMore(true);
							list_view.setCanLoadMore(true);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				list_view.onRefreshComplete();

			}
		});

		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 加载更多的list数据
	 */
		 private void loadMore() {
			 	PostParams params = new PostParams();
				 params.put("pageIndex", pageNum + "");
				 PostRequest req = new PostRequest(activity, params,
				 PersonalCenterUtils.buildUrl(activity, Const.MY_MSG_LIST),
	 				new RespListener(activity) {

				 @Override
				 public void getResp(JSONObject obj) {
					 try {
					 List<MyMsgVo> datas = GsonTools.getList(
						 obj.getJSONObject("data").getJSONArray("list"),MyMsgVo.class);
				 int totalPage = obj.getJSONObject("data").getInt("totalPage");
				 if (!Utils.isEmpty(datas)) {
					 pageNum++;
					 adapter.addItems(datas);
					 if (totalPage < pageNum) {
				 list_view.setHasNoMoreData();
					 } else {
					 list_view.setAutoLoadMore(true);
					 }
						 }
	 					} catch (JSONException e) {
						 e.printStackTrace();
	 						}

						 list_view.onLoadMoreComplete();
				 }

					 @Override
					 public void doFailed() {
					 list_view.onLoadMoreComplete();
					 list_view.setAutoLoadMore(false);
					 }
					 });
						 mQueue.add(req);
					 mQueue.start();
						 }

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_my_msg;
	}

}
