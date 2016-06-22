package com.ccpress.izijia.activity.portal;

import org.json.JSONObject;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.froyo.commonjar.xutils.view.annotation.event.OnClick;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.portal.MyinfoActivity.RefreshNickEvent;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.ResponseVo;

import de.greenrobot.event.EventBus;

/**
 * 修改性别
 * 
 * @author wangyi
 * 
 */
public class UpdateSexActivity extends BaseActivity {

	@ViewInject(R.id.cb_male)
	private CheckBox cb_male;

	@ViewInject(R.id.cb_female)
	private CheckBox cb_female;

	private String nickname;

	@Override
	public void doBusiness() {
		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("性别");
		nickname = (String) getVo("0");
		int sex = (Integer) getVo("1");
		if (sex == 1) {
			cb_female.setChecked(false);
			cb_male.setChecked(true);
		} else {
			cb_female.setChecked(true);
			cb_male.setChecked(false);
		}

		cb_male.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					cb_female.setChecked(false);
				} else {
					cb_female.setChecked(true);
				}
			}
		});
		cb_female.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					cb_male.setChecked(false);
				} else {
					cb_male.setChecked(true);
				}
			}
		});
	}

	@OnClick(R.id.btn_submit)
	void submit(View view) {

		RequestQueue mQueue = Volley.newRequestQueue(this);
		PostParams params = new PostParams();
		params.put("nickname", nickname);
		if (cb_female.isChecked()) {
			params.put("sex", "2");
		} else {
			params.put("sex", "1");
		}

		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.SUPPLY_INFO),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						toast(vo.getMsg());
						if (vo.isSucess()) {
							RefreshNickEvent event=new RefreshNickEvent();
							event.setSex(cb_female.isChecked()?"女":"男");
							EventBus.getDefault().post(event);
							finish();
						}
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_update_sex;
	}

}
