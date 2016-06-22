package com.ccpress.izijia.activity.portal;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.froyo.commonjar.xutils.view.annotation.event.OnClick;
import com.ccpress.izijia.R;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.vo.ResponseVo;

/**
 * 找回密码
 * 
 * @author wangyi
 *
 */
public class RetrievePassActivity extends BaseActivity {

	@ViewInject(R.id.et_verify)
	private EditText et_verify;

	@ViewInject(R.id.et_phone)
	private EditText et_phone;

	@ViewInject(R.id.tv_code)
	private TextView tv_code;

	private TimeCount time;

	@Override
	public void doBusiness() {
		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("找回密码");
		time = new TimeCount(60000, 1000);
	}

	@OnClick(R.id.btn_submit)
	void next(View view) {
		if (Utils.isEmpty(et_phone.getText().toString())) {
			toast("请填写手机号");
			return;
		}
		if (Utils.isEmpty(et_verify.getText().toString())) {
			toast("请填写验证码");
			return;
		}
		skip(SetPassActivity.class, et_phone.getText().toString(), et_verify
				.getText().toString());
	}

	@OnClick(R.id.tv_code)
	void getVerifyCode(View view) {
		if (!Utils.isMobileNum(et_phone.getText().toString())) {
			toast("请填写正确手机号");
			return;
		}
		time.start();
		RequestQueue mQueue = Volley.newRequestQueue(this);
		PostParams params = new PostParams();
		params.put("mobile", et_phone.getText().toString());
		params.put("is_find_pwd", "1");
		PostRequest req = new PostRequest(activity, params, Const.VERIFY,
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						toast(vo.getMsg());
					}
				});
		mQueue.add(req);
		mQueue.start();
	}

	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			tv_code.setTextColor(Color.parseColor("#999999"));
			tv_code.setClickable(false);
			tv_code.setText(millisUntilFinished / 1000 + "秒后重新发送");
		}

		@Override
		public void onFinish() {
			tv_code.setText("重新获取验证码");
			tv_code.setClickable(true);
			tv_code.setTextColor(Color.parseColor("#50BBDB"));
		}
	}

	@Override
	protected int setLayoutResID() {
		return R.layout.activity_retrieve_pass;
	}

}
