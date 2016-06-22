package com.ccpress.izijia.activity.portal;

import android.content.Intent;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.fragment.UserCenterFragment;
import org.json.JSONObject;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.portal.MyinfoActivity.RefreshNickEvent;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.ResponseVo;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;

import de.greenrobot.event.EventBus;

/**
 * 编辑昵称
 * 
 * @author wangyi
 * 
 */
public class EditNicknameActivity extends BaseActivity {

	@ViewInject(R.id.et_nickname)
	private EditText et_nickname;

	@Override
	public void doBusiness() {
		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("昵称");
		final String sex = (String) getVo("0");

		et_nickname.setText(getVo("1").toString());

		bar.showRightText(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (Utils.isEmpty(et_nickname.getText().toString())) {
					return;
				}
				showDialog();
				RequestQueue mQueue = Volley.newRequestQueue(activity);
				PostParams params = new PostParams();
				params.put("nickname", et_nickname.getText().toString());
				params.put("sex", sex);
				PostRequest req = new PostRequest(activity, params,
						PersonalCenterUtils.buildUrl(activity,
								Const.SUPPLY_INFO), new RespListener(activity) {

							@Override
							public void getResp(JSONObject obj) {
								dismissDialog();
								ResponseVo vo = GsonTools.getVo(obj.toString(),
										ResponseVo.class);
								toast(vo.getMsg());
								if (vo.isSucess()) {

									RefreshNickEvent event=new RefreshNickEvent();
									event.setNickName(et_nickname.getText().toString());
									EventBus.getDefault().post(event);

//									Intent intent = new Intent();
//									intent.setAction(Constant.USER_INFO_CHANGE_ACTION);
//									activity.sendBroadcast(intent);
									UserCenterFragment.Avatar="Avatar";
									finish();
								}
							}
						});
				mQueue.add(req);
				mQueue.start();
			}
		}, "确定");
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_edit_nickname;
	}

}
