package com.ccpress.izijia.dfy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import com.ccpress.izijia.R;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.entity.UserInfo;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.dfy.view.TopView;
import com.froyo.commonjar.utils.SpUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by dengmingzhi on 16/4/17.
 */
public class NoLoginActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.et_tel)
    private EditText et_tel;

    @ViewInject(R.id.et_reg_code)
    private EditText et_reg_code;

    @ViewInject(R.id.btn_reg_code)
    private Button btn_reg_code;

    @ViewInject(R.id.btn_reg)
    private Button btn_reg;

    private int time = 60;
    private static final int MSG_SET_ALIAS = 1001;

    @Override
    protected int getRid() {
        return R.layout.dfy_activity_nologin_reg;
    }

    @Override
    protected void initTopView(TopView topView) {
        super.initTopView(topView);
        topView.setText("登录");
        btn_reg_code.setOnClickListener(this);
        btn_reg.setOnClickListener(this);
        boolean type = getIntent().getBooleanExtra("type", true);
        btn_reg.setText(type ? "去预定" : "登录");
    }

    /**
     * 获取验证码
     */
    private void reg_code() {
        String telNum = et_tel.getText().toString();
        if (!Util.isMobile(telNum)) {
            CustomToast.showToast("请输入正确格式的手机号");
            return;
        }
        handler.sendEmptyMessage(1);
        btn_reg_code.setEnabled(false);
        btn_reg_code.setBackgroundResource(R.color.dfy_dadada);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("account", telNum);
        map.put("type", "mobile");
        map.put("action", "member");
        NetUtil.Post(Constant.DFY_NOLOGIN_REG_CODE, map, new MyCallBack() {
            @Override
            public void onSuccess(String s) {

                try {
                    JSONObject object = new JSONObject(s);
                    String status = object.getString("status");
                    String info = object.getString("info");
                    if (TextUtils.equals("1", status)) {
                        CustomToast.showToast("验证码已发送至您的手机，请注意查收");
                    } else {
                        CustomToast.showToast(info);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                super.onError(throwable, b);
                time = 0;
            }
        });


    }

    /**
     * 不登录预订
     */
    private void reg() {
        String telNum = et_tel.getText().toString();
        if (!Util.isMobile(telNum)) {
            CustomToast.showToast("请输入正确格式的手机号");
            return;
        }

        String regCode = et_reg_code.getText().toString();

        if (regCode.length() != 6) {
            CustomToast.showToast("请输入6位验证码");
            return;
        }

        final ProgressDialog pdlog = new ProgressDialog(this);
        pdlog.setMessage("正在登录..");
        pdlog.show();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", telNum);
        map.put("password", regCode);

        Log.d("username", telNum);
        Log.d("password", regCode);

        NetUtil.Post(Constant.DFY_NOLOGIN_REG, map, new MyCallBack() {
            @Override
            public void onSuccess(String s) {
                UserInfo userInfo = JsonUtil.getJavaBean(s, UserInfo.class);
                Log.d("userInfo", userInfo.getData().getAuth());
                if (userInfo != null) {
                    if (!userInfo.isResult()) {
                        CustomToast.showToast("验证码或手机号不正确");
                        return;
                    }

                    CustomToast.showToast("登录成功");
                    Log.d("auth", userInfo.getData().getAuth());
                    String auth = userInfo.getData().getAuth();
                    String uid = userInfo.getData().getUserinfo().getUid();
                    String userName = userInfo.getData().getUserinfo().getUsername();
                    String avatar = userInfo.getData().getUserinfo().getAvatar128();

                    SpUtil sp = new SpUtil(Util.getMyApplication());
                    sp.setValue(Const.AUTH, auth);
                    sp.setValue(Const.UID, uid);
                    sp.setValue(Const.USERNAME, userName);
                    sp.setValue(Const.AVATAR, avatar);
                    setAlias(uid);
                    finish();
                }
            }

            @Override
            public void onFinished() {
                pdlog.cancel();
            }
        });


    }

    /**
     * 登录成功是，注册uid到jpush
     */
    private void setAlias(String uid) {
        handler.sendMessage(handler.obtainMessage(MSG_SET_ALIAS, uid));
    }

    private final Handler handler = new Handler() {
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
                                    handler.sendMessageDelayed(handler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                                    break;

                                default:
                            }

                        }

                    });
                    break;

                case 1:
                    time = time - 1;
                    if (time <= 0) {
                        btn_reg_code.setText("点击获取验证码");
                        btn_reg_code.setEnabled(true);
                        btn_reg_code.setBackgroundResource(R.drawable.login_btn_bg);
                        time = 60;
                    } else {
                        btn_reg_code.setText(time + "秒后重新获取");
                        handler.sendEmptyMessageDelayed(1, 1000);
                    }
                    break;
                default:
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reg_code:
                reg_code();
                break;
            case R.id.btn_reg:
                reg();
                break;
        }
    }
}
