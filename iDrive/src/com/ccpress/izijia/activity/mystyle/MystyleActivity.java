package com.ccpress.izijia.activity.mystyle;

import java.util.ArrayList;
import java.util.List;
import com.trs.util.log.Log;
import android.widget.Toast;
import com.froyo.commonjar.xutils.utils.LogUtils;
import org.json.JSONException;
import org.json.JSONObject;
import com.trs.util.log.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.BespokeAdapter;
import com.ccpress.izijia.adapter.MyStyleLineAdapter;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.BespokeVo;
import com.ccpress.izijia.vo.ResponseVo;
import com.ccpress.izijia.vo.StyleLineVo;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.adapter.SimplePageAdapter;
import com.froyo.commonjar.network.GetRequest;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.view.CustomListView;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.froyo.commonjar.xutils.view.annotation.event.OnClick;

/**
 * 
 * @Des: 我的定制
 * @author Rhino
 * @version V1.0
 * @created 2015年5月6日 下午3:25:22
 */
public class MystyleActivity extends BaseActivity {

	@ViewInject(R.id.tv_view)
	private TextView tv_view;

	@ViewInject(R.id.tv_line)
	private TextView tv_line;

	@ViewInject(R.id.vp_pager)
	private ViewPager vp_pager;

	@ViewInject(R.id.tv_bottom)
	private TextView tv_bottom;
	
	@ViewInject(R.id.tv_edit)
	private TextView tv_edit;

	private SimplePageAdapter pageAdapter;

	private TextView tipsLine;

	private CustomListView clListView;

	private CustomListView clListLine;

	private BespokeAdapter adapter;

	private MyStyleLineAdapter lineAdapter;

	private RequestQueue mQueue;

	private UpdateViewReceiver receiver;

	/**
	 *初始化布局控件
	 */
	@Override
	public void doBusiness() {
		mQueue = Volley.newRequestQueue(activity);

		receiver = new UpdateViewReceiver();
		IntentFilter filter = new IntentFilter(Const.ACTION_UPDATE_VIEW);
		registerReceiver(receiver, filter);
		tv_edit.setText("删除");
		List<View> views = new ArrayList<View>();
		View view = makeView(R.layout.view_line_page);
		View line = makeView(R.layout.view_line_page);
		views.add(view);
		views.add(line);
		pageAdapter = new SimplePageAdapter(activity, views);
		vp_pager.setAdapter(pageAdapter);
		//page切换监听
		vp_pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 0) {
					tv_bottom.setVisibility(View.VISIBLE);
				} else {
					tv_bottom.setVisibility(View.GONE);
				}
				changeBtn(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		initView(view);
		initLine(line);
		vp_pager.setCurrentItem(0);
	}

	/**
	 * 看点的数据请求
	 * @param view
     */
	private void initView(View view) {
		clListView = (CustomListView) view.findViewById(R.id.lv_page_list);
		adapter = new BespokeAdapter(new ArrayList<BespokeVo>(), activity,
				R.layout.item_bespoke);
		BespokeAdapter.BPA="MystyleActivity";
		clListView.setAdapter(adapter);
		showDialog();
		GetRequest req = new GetRequest(activity, PersonalCenterUtils.buildUrl(
				activity, Const.VIEW_LIST), new RespListener(activity) {
			@Override
			public void getResp(JSONObject obj) {
				dismissDialog();
				ResponseVo vo = GsonTools.getVo(obj.toString(),
						ResponseVo.class);

				if (vo.isSucess()) {
					try {
						List<BespokeVo> data = GsonTools.getList(
								obj.getJSONArray("data"), BespokeVo.class);
						adapter.addItems(data);
						adapter.showAll();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 线路 的数据请求
	 * @param view
     */
	private void initLine(View view) {
		tipsLine = (TextView) view.findViewById(R.id.tv_tips);
		clListLine = (CustomListView) view.findViewById(R.id.lv_page_list);
		lineAdapter = new MyStyleLineAdapter(new ArrayList<StyleLineVo>(),
				activity, R.layout.item_collect);
		tipsLine.setVisibility(View.GONE);
		clListLine.setAdapter(lineAdapter);
		Log.e("URL",  PersonalCenterUtils.buildUrl(
				activity, Const.LINE_LIST));
		GetRequest req = new GetRequest(activity, PersonalCenterUtils.buildUrl(
				activity, Const.LINE_LIST), new RespListener(activity) {
			@Override
			public void getResp(JSONObject obj) {
				dismissDialog();
				ResponseVo vo = GsonTools.getVo(obj.toString(),
						ResponseVo.class);
				Log.e("initLine:::", obj.toString());

				if (vo.isSucess()) {
					try {
						List<StyleLineVo> data = GsonTools.getList(
								obj.getJSONArray("data"), StyleLineVo.class);
							Log.e("data: ",obj.getJSONArray("data").toString() );

						lineAdapter.addItems(data);
						lineAdapter.showAll();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 编辑与删除de设置
	 */
	@OnClick(R.id.tv_edit)
	void edit(View view) {
		TextView tv = (TextView) view;
		if ("编辑".equals(tv.getText().toString())) {
			adapter.showAll();
			lineAdapter.showAll();
			((TextView) view).setText("删除");
		} else {
			if (vp_pager.getCurrentItem() == 0) {
				deleteViewSpot(view);
			} else {
				deleteViewRoute(view);
			}
		}
	}

	/**
	 * 删除看点
	 * @param view
     */
	void deleteViewSpot(final View view) {
		if (Utils.isEmpty(adapter.getSelectIds())
				&& Utils.isEmpty(lineAdapter.getSelectIds())) {
			toast("尚未选取删除的看点");
			return;
		}
		showDialog("正在删除看点……");
		PostParams params = new PostParams();
		params.put("ids", adapter.getSelectIds());
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.LINE_DELETE),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (vo.isSucess()) {
							adapter.doDelete();
							adapter.showAll();
							lineAdapter.showAll();
//							((TextView) view).setText("编辑");
						}
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	/**
	 * 删除线路
	 * @param view
     */
	void deleteViewRoute(final View view) {
		if (Utils.isEmpty(lineAdapter.getSelectIds())) {
			toast("尚未选取删除的线路");
			return;
		}
		showDialog("正在删除线路……");
		PostParams params = new PostParams();
		params.put("ids", lineAdapter.getSelectIds());
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.LINE_TOUTE),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (vo.isSucess()) {
							lineAdapter.doDelete();
							lineAdapter.showAll();
							adapter.showAll();
//							((TextView) view).setText("编辑");
						}
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	@OnClick(R.id.tv_view)
	void clickView(View view) {
		vp_pager.setCurrentItem(0);
	}

	@OnClick(R.id.tv_line)
	void clickLine(View view) {
		vp_pager.setCurrentItem(1);
	}

	/**
	 * Tab的背景颜色
	 * @param postion
     */
	private void changeBtn(int postion) {
		if (postion == 0) {
			tv_view.setTextColor(Color.parseColor("#50BBDB"));
			tv_view.setBackgroundColor(Color.parseColor("#ffffff"));

			tv_line.setTextColor(Color.parseColor("#ffffff"));
//			tv_line.setBackgroundColor(Color.parseColor("#50BBDB"));
			tv_line.setBackgroundDrawable(getDrawableRes(R.drawable.mystyle_title_right_bg));
		} else {
			tv_line.setTextColor(Color.parseColor("#50BBDB"));
			tv_line.setBackgroundColor(Color.parseColor("#ffffff"));

			tv_view.setTextColor(Color.parseColor("#ffffff"));
//			tv_view.setBackgroundColor(Color.parseColor("#50BBDB"));
			tv_view.setBackgroundDrawable(getDrawableRes(R.drawable.mystyle_title_left_bg));
		}
	}

	@OnClick(R.id.iv_back)
	void back(View view) {
		finish();
	}

	/**
	 * 看点定制
	 * @param view
     */
	@OnClick(R.id.tv_bottom)
	void make(View view) {
		if (vp_pager.getCurrentItem() == 0) {
			if (!Utils.isEmpty(adapter.getSelectView())) {
				if(adapter.getSelectView().size()>16){
					toast("定制看点数量不能超过15个");
				}else {
					EditLineActivity.COME_TYPE="Mystyle_Bespoke";

					skip(EditLineActivity.class,
							(ArrayList<BespokeVo>) adapter.getSelectView());
				}
			} else {
				toast("请选择需要定制的看点");
			}
		}
	}

	public class UpdateViewReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (Const.ACTION_UPDATE_VIEW.equals(intent.getAction())) {
				ArrayList<BespokeVo> data = (ArrayList<BespokeVo>) intent
						.getExtras().getSerializable("0");
				adapter.reload(data);
			}
		}

	}

	@Override
	protected void onDestroy() {
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
		super.onDestroy();
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_mystyle;
	}

}
