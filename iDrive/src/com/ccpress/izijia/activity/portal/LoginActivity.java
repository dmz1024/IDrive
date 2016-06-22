package com.ccpress.izijia.activity.portal;

import java.util.HashMap;
import java.util.Set;

import android.content.Intent;
import android.os.Environment;
import android.widget.TextView;
import com.ccpress.izijia.dfy.activity.NoLoginActivity;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.util.Util;
import com.trs.util.FileUtil;
import com.trs.wcm.RemoteDataService;
import com.trs.wcm.callback.BaseDataAsynCallback;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.froyo.commonjar.activity.BaseActivity;
import com.froyo.commonjar.network.GetRequest;
import com.froyo.commonjar.network.PostParams;
import com.froyo.commonjar.network.PostRequest;
import com.froyo.commonjar.network.RespListener;
import com.froyo.commonjar.utils.GsonTools;
import com.froyo.commonjar.utils.InitUtil;
import com.froyo.commonjar.utils.SpUtil;
import com.froyo.commonjar.utils.Utils;
import com.froyo.commonjar.xutils.view.annotation.ViewInject;
import com.froyo.commonjar.xutils.view.annotation.event.OnClick;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.MainActivity;
import com.ccpress.izijia.componet.TitleBar;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.vo.ResponseVo;
import com.trs.util.log.Log;

/**
 * @author Rhino
 * @version V1.0
 * @Des: 登录
 * @created 2015年5月7日 上午10:00:32
 */
public class LoginActivity extends BaseActivity implements
        PlatformActionListener {

    private static final Utils TextUtils = null;

    public static final String EXTRA_NOT_GOTO_HOMEPAGE = "notgotohomepage";//点赞、收藏等功能调用登录，登录成功之后不需跳转到主界面
    private boolean isJustLogin = false;

    @ViewInject(R.id.et_name)
    private EditText et_name;

    @ViewInject(R.id.et_pass)
    private EditText et_pass;


    @ViewInject(R.id.tv_noLogin)
    private TextView tv_noLogin;

    @ViewInject(R.id.view_noLogin)
    private View view_noLogin;
    private boolean is_dfy;
    private boolean is_yd;
    ;

    @Override
    public void doBusiness() {
        TitleBar bar = new TitleBar(activity);
        bar.setTitle("登录");
        InitUtil.createImageCacheFolder(this);

        /**
         * 判断是否是从自驾团页面跳转过来的,dfy是本公司名称缩写，请不要在意
         */


        is_dfy = getIntent().getBooleanExtra(Constant.DFY_IS_FROM_DFY, false);
        is_yd = getIntent().getBooleanExtra(Constant.DFY_IS_FROM_Yd, false);
        if (is_dfy) {
            tv_noLogin.setText("不登录，直接报名");
            tv_noLogin.setVisibility(View.VISIBLE);
            view_noLogin.setVisibility(View.VISIBLE);
        } else if (is_yd) {
            tv_noLogin.setText("快捷登录");
            tv_noLogin.setVisibility(View.VISIBLE);
            view_noLogin.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 不登录预订
     *
     * @param view
     */
    @OnClick(R.id.tv_noLogin)
    void noLogin_reg(View view) {
        Intent intent = new Intent(this, NoLoginActivity.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        intent.putExtra("type", is_dfy ? true : false);
        startActivity(intent);
        finish();
    }

    /**
     * 实现登录的方法
     *
     * @param view
     */
    @OnClick(R.id.btn_login)
    void login(View view) {
        if (Utils.isEmpty(et_name.getText().toString())) {
            toast("请填写手机号/邮箱");
            return;
        }
        if (Utils.isEmpty(et_pass.getText().toString())) {
            toast("请填写密码");
            return;
        }

        showDialog("努力登录中……");

        RequestQueue mQueue = Volley.newRequestQueue(this);
        PostParams params = new PostParams();
        params.put("username", et_name.getText().toString());
        params.put("password", et_pass.getText().toString());
        PostRequest req = new PostRequest(activity, params, Const.LOGIN,
                new RespListener(activity) {

                    @Override
                    public void getResp(JSONObject obj) {
                        android.util.Log.d("obj", obj.toString());

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
                                String userName = obj.getJSONObject("data")
                                        .getJSONObject("userinfo")
                                        .getString("username");
                                String avatar = obj.getJSONObject("data")
                                        .getJSONObject("userinfo")
                                        .getString("avatar128");


                                SpUtil sp = new SpUtil(activity);
                                sp.setValue(Const.AUTH, auth);
                                sp.setValue(Const.UID, uid);
                                sp.setValue(Const.USERNAME, userName);
                                sp.setValue(Const.AVATAR, avatar);
                                setAlias(uid);
                                isJustLogin = getIntent().getBooleanExtra(EXTRA_NOT_GOTO_HOMEPAGE, false);

                                if (!isJustLogin) {
                                    skip(MainActivity.class);
//									startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    finish();
                                } else {
                                    setResult(Constant.DFY_LOGIN2DETAIL_RES, getIntent());
                                    finish();
                                }

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

    /**
     * 注册
     *
     * @param view
     */
    @OnClick(R.id.tv_register)
    void register(View view) {
        skip(RegisterActivity.class);
    }

    @OnClick(R.id.tv_forget_pass)
    void retrievePass(View view) {
        skip(RetrievePassActivity.class);
    }

    /**
     * 微信登陆
     *
     * @param view
     */
    @OnClick(R.id.iv_weixin)
    void weixinLogin(View view) {
        wxLogin(Wechat.NAME);
    }

    /**
     * 第三方授权
     *
     * @param plat
     */
    private void authorize(Platform plat) {
        if (plat.isValid()) {
            plat.removeAccount(true);
        }
        plat.setPlatformActionListener(this);
        plat.SSOSetting(true);
        plat.showUser(null);
    }

    /**
     * QQ账号登录
     *
     * @param view
     */
    @OnClick(R.id.iv_qq)
    void qq(View view) {
        wxLogin(QQ.NAME);
    }

    /**
     * 新浪微博账号登录
     *
     * @param view
     */
    @OnClick(R.id.iv_weibo)
    void weibo(View view) {
        wxLogin(SinaWeibo.NAME);
    }

    /**
     * ShareSDK实现登陆
     *
     * @param name
     */
    private void wxLogin(String name) {
        ShareSDK.initSDK(activity);
        Platform plat = ShareSDK.getPlatform(name);
        if (plat.isAuthValid()) {
            plat.removeAccount(true);
        }
        plat.setPlatformActionListener(this);
        plat.authorize();
    }

    @Override
    protected int setLayoutResID() {
        return R.layout.activity_login;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Platform plat = (Platform) msg.obj;

            switch (msg.arg1) {
                case 1: {
                    // 成功
                    if (plat.isAuthValid()) {
                        String userId = plat.getDb().getUserId();
                        System.out.println("userId:" + userId);
                        PlatformDb platDB = plat.getDb();
                        System.out.println("token:" + platDB.getToken());
//					String token = platDB.getToken();
//					Log.e(token, "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");
                        System.out.println("uid:" + platDB.getUserId());
                        System.out.println("name:" + platDB.getUserName());
                        System.out.println("openid:" + platDB.get("openid"));
                        String url = "";
                        if (plat.getName().equals(QQ.NAME)) {
                            url = Const.QQ_LOGIN + "&access_token="
                                    + Uri.encode(platDB.getToken());
                        } else if (plat.getName().equals(SinaWeibo.NAME)) {
                            url = Const.SINA_LOGIN + "&access_token="
                                    + Uri.encode(platDB.getToken()) + "&openid="
                                    + Uri.encode(platDB.getUserId());
                        } else {
                            url = Const.WX_LOGIN + "&access_token="
                                    + Uri.encode(platDB.getToken()) + "&openid="
                                    + Uri.encode(platDB.getUserId());
                        }
                        login(url);
                    }
                }
                break;
                case 2: {
                    // 失败
                    toast(plat.getName() + "失败");
                }
                case 3: {
                    // 取消
                    toast(plat.getName() + "取消");
                }
            }

        }
    };

    @Override
    public void onCancel(Platform plat, int action) {
        Message msg = new Message();
        msg.arg1 = 3;
        msg.arg2 = action;
        msg.obj = plat;
        handler.sendMessage(msg);

    }

    @Override
    public void onComplete(Platform plat, int action,
                           HashMap<String, Object> arg2) {
        Message msg = new Message();
        msg.arg1 = 1;
        msg.arg2 = action;
        msg.obj = plat;
        handler.sendMessage(msg);
    }

    @Override
    public void onError(Platform plat, int action, Throwable arg2) {
        Message msg = new Message();
        msg.arg1 = 2;
        msg.arg2 = action;
        msg.obj = plat;
        handler.sendMessage(msg);
    }

    /**
     * 不要的代码你怎么不注释掉，哥们，咱能认真点吗？
     * 成功后，获取服务器返回数据，需要用到的信息保存到SharedPreferences文件中
     *
     * @param url
     */
    private void login(String url) {
        showDialog("努力登录中……");

        RequestQueue mQueue = Volley.newRequestQueue(this);
        GetRequest req = new GetRequest(activity, url, new RespListener(
                activity) {
            @Override
            public void getResp(JSONObject obj) {
                Log.d("ssssssssssssssssss", obj.toString());
                dismissDialog();
                ResponseVo vo = GsonTools.getVo(obj.toString(),
                        ResponseVo.class);
                if (vo.isSucess()) {
                    try {
                        toast(vo.getMsg());

                        String auth = obj.getJSONObject("data").getString(
                                "auth");
                        String uid = obj.getJSONObject("data")
                                .getJSONObject("userinfo").getString("uid");
                        String userName = obj.getJSONObject("data")
                                .getJSONObject("userinfo")
                                .getString("username");
                        String avatar = obj.getJSONObject("data")
                                .getJSONObject("userinfo")
                                .getString("avatar128");
                        SpUtil sp = new SpUtil(activity);

                        sp.setValue(Const.AUTH, auth);
                        sp.setValue(Const.UID, uid);
                        sp.setValue(Const.USERNAME, userName);
                        sp.setValue(Const.AVATAR, avatar);
                        setAlias(uid);
                        skip(MainActivity.class);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                    toast(vo.getMsg());
                }
            }

            @Override
            public void doFailed() {
                dismissDialog();
                toast("登录出错");
            }
        });
        mQueue.add(req);
        mQueue.start();
    }

    /**
     * 登录成功是，注册uid到jpush
     */
    private void setAlias(String uid) {
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
}
