package com.ccpress.izijia.activity.mystyle;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.widget.Toast;
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
import com.ccpress.izijia.activity.mystyle.MyFansActivity.ReFreshEvent;
import com.ccpress.izijia.adapter.MyAttentionAdapter;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.MyAttentionVo;
import com.ccpress.izijia.vo.ResponseVo;

import de.greenrobot.event.EventBus;

/**
 * 
 * @Des: 我的关注
 * @author Rhino
 * @version V1.0
 * @created 2015年5月19日 上午11:50:49
 */
public class MyAttentionActivity extends BaseActivity {

	@ViewInject(R.id.ct_list)
	private CustomListView listView;

	private MyAttentionAdapter adapter;

	private int pageNum=1;
	
	@Override
	public void doBusiness() {
		
		EventBus.getDefault().register(this);
		TitleBar bar = new TitleBar(activity);
		bar.setTitle("关注");
		bar.showBack();
		
		adapter = new MyAttentionAdapter(new ArrayList<MyAttentionVo>(), activity,
				R.layout.item_my_attention);
		
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
		//item点击事件，他人中心
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				MyAttentionVo vo=(MyAttentionVo) arg0.getAdapter().getItem(arg2);
				//skip(InfoActivity.class,vo.getUser().getUid());

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
	 * 刷新List数据
	 */
	private void pullToRefresh(){
		RequestQueue mQueue = Volley.newRequestQueue(this);
		PostParams params = new PostParams();
		params.put("page", 1+"");
		SpUtil sp=new SpUtil(activity);
		params.put("tuid", sp.getStringValue(Const.UID));

		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.MY_ATTENTION),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						try {
							List<MyAttentionVo> datas = GsonTools.getList(
									obj.getJSONObject("data").getJSONArray(
											"following"), MyAttentionVo.class);
							if (Utils.isEmpty(datas)) {
								toast("尚未关注任何人");
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
							toast("尚未关注任何人");
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
	 *加载更多list数据
	 */
	private void loadMore() {
		RequestQueue mQueue = Volley.newRequestQueue(this);
		PostParams params = new PostParams();
		params.put("page", pageNum+"");

		PostRequest req = new PostRequest(activity, params, PersonalCenterUtils.buildUrl(activity, Const.MY_ATTENTION),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						try {
							List<MyAttentionVo> datas = GsonTools.getList(
									obj.getJSONObject("data").getJSONArray(
											"following"), MyAttentionVo.class);
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

	/**
	 * 响应刷新
	 * @param event
     */
	public void onEventMainThread(ReFreshEvent event) {  
		pullToRefresh();
	}  
	
	public static class ReFreshEvent{
		
	}
	
	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	@Override
	protected int setLayoutResID() {
		return R.layout.activity_my_attention;
	}

}
