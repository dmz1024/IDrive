package com.ccpress.izijia.activity.portal;

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
 * 编辑个性签名
 * 
 * @author wangyi
 * 
 */
public class EditSignActivity extends BaseActivity {

	@ViewInject(R.id.et_content)
	private EditText et_nickname;

	@Override
	public void doBusiness() {
		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("个性签名");

		if (!Utils.isEmpty((String) getVo("0"))) {
			et_nickname.setText((String) getVo("0"));
		}
		bar.showRightText(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				update((String)getVo("0"));
			}
		}, "确定");
	}

	private void update(String text) {
		if(Utils.isEmpty(et_nickname.getText().toString())){
			toast("请填写个性签名");
			return;
		}
		if(text.equals(et_nickname.getText().toString())){
			toast("签名并未做任何修改");
			return;
		}
		
		showDialog();
		RequestQueue mQueue = Volley.newRequestQueue(this);

		PostParams params = new PostParams();
		params.put("signature", et_nickname.getText().toString());
		PostRequest req = new PostRequest(activity, params,
				PersonalCenterUtils.buildUrl(activity, Const.ALTER_SIGNATURE),
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						        dismissDialog();
								ResponseVo vo=GsonTools.getVo(obj.toString(),ResponseVo.class);
								toast(vo.getMsg());
								if(vo.isSucess()){
									RefreshNickEvent event=new RefreshNickEvent();
									event.setSign(et_nickname.getText().toString());
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
		return R.layout.activity_edit_sign;
	}

}
