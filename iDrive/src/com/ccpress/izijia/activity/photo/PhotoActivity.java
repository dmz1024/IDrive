package com.ccpress.izijia.activity.photo;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.MyPhotoAdapter;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.MyPhotoVo;
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
 * @Des: 我的相册
 * @author Rhino 
 * @version V1.0 
 * @created  2015年5月6日 下午3:25:22
 */
public class PhotoActivity extends BaseActivity {
	@ViewInject(R.id.ct_list)
	private CustomListView listView;
	
	private MyPhotoAdapter adapter;

	private int pageNum=1;
	
	@Override
	public void doBusiness() {
		TitleBar bar =new TitleBar(activity);
		bar.showBack();
		bar.setTitle("我的相册");
		
		adapter = new MyPhotoAdapter(new ArrayList<MyPhotoVo>(), activity,
				R.layout.item_my_photo);
		
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
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				MyPhotoVo vo=(MyPhotoVo) arg0.getAdapter().getItem(arg2);
//				skip(PhotoDetailActivity.class,vo.getId());
//				skip(MyTestActivity.class,vo.getId());


			}
		});
		showDialog();
		pullToRefresh();
	}

	/**
	 * 刷新数据
	 */
	private void pullToRefresh(){
		RequestQueue mQueue = Volley.newRequestQueue(this);
		PostParams params = new PostParams();
		params.put("page", 1+"");
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.MY_PHOTO_LIST),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						try {
							List<MyPhotoVo> datas = GsonTools.getList(
									obj.getJSONObject("data").getJSONArray(
											"list"), MyPhotoVo.class);
							if (Utils.isEmpty(datas)) {
								toast("尚未上传照片");
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
	 * 加载更多
	 */
	private void loadMore() {
		RequestQueue mQueue = Volley.newRequestQueue(this);
		PostParams params = new PostParams();
		params.put("page", pageNum+"");
		PostRequest req = new PostRequest(activity, params, PersonalCenterUtils.buildUrl(activity, Const.MY_PHOTO_LIST),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						try {
							List<MyPhotoVo> datas = GsonTools.getList(
									obj.getJSONObject("data").getJSONArray(
											"list"), MyPhotoVo.class);
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

	
	@Override
	protected int setLayoutResID() {
		return R.layout.activity_photo;
	}

}
