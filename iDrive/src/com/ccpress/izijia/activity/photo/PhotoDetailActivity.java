package com.ccpress.izijia.activity.photo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.GetRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.view.CustomListView;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.MyPhotoDetailAdapter;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.MyPhotoVo;
import com.ccpress.izijia.vo.MyPhotoVo.Pic;
import com.ccpress.izijia.vo.ResponseVo;

/**
 * 我的相册详情
 * 
 * @author wangyi
 * 
 */
public class PhotoDetailActivity extends BaseActivity {

	private MyPhotoDetailAdapter adapter;

	@ViewInject(R.id.lv_page_list)
	private CustomListView listView;
	
	@ViewInject(R.id.ll_main)
	private LinearLayout ll_main;

	
	private  View popupView;
	
	PopupWindow popupWindow;
	
	@Override
	public void doBusiness() {
		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("内容详情");
		bar.showRightText(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showWindow();
			}
		}, "管理");

		adapter = new MyPhotoDetailAdapter(new ArrayList<Pic>(), activity,
				R.layout.item_collect_photo);
		listView.setAdapter(adapter);

		String id = (String) getVo("0");
		showDialog();
		//请求数据
		RequestQueue mQueue = Volley.newRequestQueue(this);
		GetRequest req = new GetRequest(activity, PersonalCenterUtils.buildUrl(
				activity, Const.MY_PHOTO_DETAIL) + "&id=" + id,
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (vo.isSucess()) {
							try {
								MyPhotoVo photo = GsonTools.getVo(
										obj.getJSONObject("data")
												.getJSONObject("detail")
												.toString(), MyPhotoVo.class);
								if (!Utils.isEmpty(photo.getPics())) {
									adapter.addItems(photo.getPics());
								}

							} catch (JSONException e) {
							}
						} else {
							toast(vo.getMsg());
						}
					}

					@Override
					public void doFailed() {
						dismissDialog();
						toast("获取数据失败");
					}
				});

		mQueue.add(req);
		mQueue.start();

	}

	private void showWindow() {
		if(popupView==null){
			popupView = makeView(R.layout.view_photo_window);
			popupWindow = new PopupWindow(popupView,
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.WRAP_CONTENT);
		}
		if(popupWindow.isShowing()){
			popupWindow.dismiss();
		}else{
			popupWindow.showAtLocation(ll_main,Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
		}
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_photo_detail;
	}

}
