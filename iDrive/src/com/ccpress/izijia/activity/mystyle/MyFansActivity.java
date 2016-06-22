package com.ccpress.izijia.activity.mystyle;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.widget.Toast;
import com.ccpress.izijia.fragment.UserCenterFragment;
import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.SpUtil;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.view.CustomListView;
import com.froyo.commonjar.view.CustomListView.OnLoadMoreListener;
import com.froyo.commonjar.view.CustomListView.OnRefreshListener;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.MyFansAdapter;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.MyAttentionVo;

import de.greenrobot.event.EventBus;

/**
 * 
 * @Des: 我的粉丝
 * @author Rhino
 * @version V1.0
 * @created 2015年5月19日 上午11:50:49
 */
public class MyFansActivity extends BaseActivity {

	@ViewInject(R.id.ct_list)
	private CustomListView listView;

	private MyFansAdapter adapter;

	private int pageNum = 1;

	@Override
	public void doBusiness() {
		
		EventBus.getDefault().register(this);
		
		TitleBar bar = new TitleBar(activity);
		bar.setTitle("粉丝");
		bar.showBack();

		adapter = new MyFansAdapter(new ArrayList<MyAttentionVo>(), activity,
				R.layout.item_my_fans);

		listView.setAdapter(adapter);
		listView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pullToRefresh();
			}
		});

		listView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				loadMore();
			}
		});
		//item点击事件，到他人个人中心
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				MyAttentionVo vo=(MyAttentionVo) arg0.getAdapter().getItem(arg2);
				InfoActivity.TUID=vo.getUser().getUid();
				Intent intent = new Intent(getActivity(), InfoActivity.class);
				intent.putExtra(InfoActivity.TUID,vo.getUser().getUid());
				getActivity().startActivity(intent);
			}
		});
		showDialog();
		pullToRefresh();
	}

	/**
	 * 刷新数据，刷新list
	 */
	private void pullToRefresh(){
		RequestQueue mQueue = Volley.newRequestQueue(this);
		PostParams params = new PostParams();
		params.put("page", 1+"");
		SpUtil sp=new SpUtil(activity);
		params.put("tuid", sp.getStringValue(Const.UID));
		
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.MY_FANS),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						try {
							List<MyAttentionVo> datas = GsonTools.getList(
									obj.getJSONObject("data").getJSONArray(
											"fans"), MyAttentionVo.class);
							if (Utils.isEmpty(datas)) {
								toast("尚未有粉丝");
							} else {
								adapter.removeAll();
								adapter.addItems(datas);
								int total_page= obj.getJSONObject("data").getInt(
										"total_page");
								if(total_page<2){
									listView.setHasNoMoreData();	
								}else{
									pageNum=2;
									listView.setCanLoadMore(true);
								}
							}
						} catch (Exception e) {
						}
						listView.onRefreshComplete();
					}

					@Override
					public void doFailed() {
						dismissDialog();
						toast("获取数据失败");
						listView.onRefreshComplete();
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 加载更多的list数据
	 */
	private void loadMore() {
		RequestQueue mQueue = Volley.newRequestQueue(this);
		PostParams params = new PostParams();
		params.put("page", pageNum+"");
		PostRequest req = new PostRequest(activity, params, PersonalCenterUtils.buildUrl(activity, Const.MY_FANS),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						try {
							List<MyAttentionVo> datas = GsonTools.getList(
									obj.getJSONObject("data").getJSONArray(
											"fans"), MyAttentionVo.class);
							int total_page= obj.getJSONObject("data").getInt(
									"total_page");
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
	
	public void onEventMainThread(ReFreshEvent event) {  
		pullToRefresh();
	}  
	
	public static class ReFreshEvent{
		
	}
	
	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		EventBus.getDefault().post(new UserCenterFragment.ReFreshEvent());
		super.onDestroy();
	}
	
	@Override
	protected int setLayoutResID() {
		return R.layout.activity_my_attention;
	}

}
