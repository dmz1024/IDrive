package com.ccpress.izijia.activity.car;

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;
import org.json.JSONObject;

import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.ccpress.izijia.R;
import com.ccpress.izijia.adapter.CarTeamAdapter;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.CarTeamVo;

/**
 * 
 * @Des: 我的车队
 * @author Rhino
 * @version V1.0
 * @created 2015年5月6日 下午3:25:22
 */
public class CarTeamActivity extends BaseActivity {

	@ViewInject(R.id.lv_list)
	private ListView listView;

	private CarTeamAdapter adapter;

	@Override
	public void doBusiness() {
		TitleBar bar = new TitleBar(activity);
		bar.setTitle("我的车队");
		bar.showBack();


		adapter = new CarTeamAdapter(new ArrayList<CarTeamVo>(), activity,
				R.layout.item_car_team);
		//设置adapter
		listView.setAdapter(adapter);

		showDialog();
		RequestQueue mQueue = Volley.newRequestQueue(this);
		PostParams params = new PostParams();
		//请求车队信息
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.MY_CAR_TEAM),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						try {
							List<CarTeamVo> datas = GsonTools.getList(
									obj.getJSONArray(
											"data"), CarTeamVo.class);
							if (Utils.isEmpty(datas)) {
								toast("尚未加入任何车队");
							} else {
								adapter.addItems(datas);
							}
						} catch (Exception e) {
							e.printStackTrace();
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

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_car;
	}

}
