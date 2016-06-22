package com.ccpress.izijia.activity.portal;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.SpUtil;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.froyo.commonjar.xutils.view.annotation.event.OnClick;
import com.ccpress.izijia.R;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.vo.ResponseVo;

/**
 * 
 * @Des: 注册
 * @author Rhino
 * @version V1.0
 * @created 2015年5月7日 上午10:00:32
 */
public class RegisterActivity extends BaseActivity {

	@ViewInject(R.id.et_phone)
	private EditText et_phone;

	@ViewInject(R.id.et_verify)
	private EditText et_verify;

	@ViewInject(R.id.et_pass)
	private EditText et_pass;

	@ViewInject(R.id.et_repass)
	private EditText et_repass;

	@ViewInject(R.id.et_invite)
	private EditText et_invite;

	@ViewInject(R.id.cb_agree)
	private CheckBox cb_agree;

	@ViewInject(R.id.tv_code)
	private TextView tv_code;

	private TimeCount timer;

	@Override
	public void doBusiness() {
		TitleBar bar = new TitleBar(activity);
		bar.showBack();
		bar.setTitle("注册");
		timer = new TimeCount(60000, 1000);
	}

	@OnClick(R.id.btn_submit)
	void submit(View view) {
		if (!Utils.isMobileNum(et_phone.getText().toString().trim())) {
			toast("请输入正确的手机号");
			return;
		}
		if (Utils.isEmpty(et_verify.getText().toString().trim())) {
			toast("请输入验证码");
			return;
		}
		if (Utils.isEmpty(et_pass.getText().toString().trim())) {
			toast("请输入密码");
			return;
		}
		if (Utils.isEmpty(et_repass.getText().toString().trim())) {
			toast("请再次输入密码");
			return;
		}

		if (!et_repass.getText().toString().trim()
				.equals(et_pass.getText().toString().trim())) {
			toast("两次输入的密码不同");
			et_repass.setText("");
			return;
		}
		if (!cb_agree.isChecked()) {
			toast("请同意爱自驾注册协议");
			return;
		}

		showDialog("正在注册……");
		RequestQueue mQueue = Volley.newRequestQueue(this);
		PostParams params = new PostParams();
		params.put("mobile", et_phone.getText().toString());
		params.put("verify_code", et_verify.getText().toString());
		params.put("password", et_pass.getText().toString());
		params.put("password2", et_pass.getText().toString());
		if (!Utils.isEmpty(et_invite.getText().toString().trim())) {
			params.put("invite_code", et_invite.getText().toString());
		}
		PostRequest req = new PostRequest(activity, params, Const.REGISTER,
				new RespListener(activity) {

					@Override
					public void getResp(JSONObject obj) {
						dismissDialog();
						ResponseVo vo = GsonTools.getVo(obj.toString(),
								ResponseVo.class);
						if (vo.isSucess()) {
							try {
								toast(vo.getMsg());
								String auth = obj.getJSONObject("data")
										.getString("auth");
								String uid = obj.getJSONObject("data")
										.getJSONObject("userinfo")
										.getString("uid");
								SpUtil sp = new SpUtil(activity);
								sp.setValue(Const.AUTH, auth);
								sp.setValue(Const.UID, uid);
								setAlias(uid);
								skip(AddInfoActivity.class);
								finish();
							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else {
							toast(vo.getMsg());
						}

					}
				});

		mQueue.add(req);
		mQueue.start();
	}

	@OnClick(R.id.tv_code)
	void getVerifyCode(View view) {
		if (Utils.isEmpty(et_phone.getText().toString())) {
			toast("请填写手机号");
			return;
		}

		timer.start();
		RequestQueue mQueue = Volley.newRequestQueue(this);
		PostParams params = new PostParams();
		params.put("mobile", et_phone.getText().toString());
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

	@OnClick(R.id.tv_protocol)
	void showProtocol(View view) {
		skip(AboutActivity.class,"爱自驾用户注册协议",Const.PROTOCOL);
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

	/**
	 * 登录成功是，注册uid到jpush
	 */
	private void setAlias(String uid){
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, uid));
	}
	
	private static final int MSG_SET_ALIAS = 1001;
	
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case MSG_SET_ALIAS:
                JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, new TagAliasCallback() {

                    @Override
                    public void gotResult(int code, String alias, Set<String> tags) {
                        switch (code) {
                        case 0:
                        	//成功
                            break;
                            
                        case 6002:
                        	//失败
                        	mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                            break;
                        
                        default:
                        }
                        
                    }
            	    
            	});
                break;
            default:
            }
        }
    };
	@Override
	protected int setLayoutResID() {
		return R.layout.activity_register;
	}

}
