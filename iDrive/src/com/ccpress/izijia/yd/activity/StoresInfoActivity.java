package com.ccpress.izijia.yd.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.portal.LoginActivity;
import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.util.ShareUtil;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.UserVo;
import com.ccpress.izijia.yd.constant.ConstantApi;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/26.
 */
public class StoresInfoActivity extends BaseActivity {
    private Intent intent;
    private WebView webView;
    private String id;
    private String title;
    private String image;
    private RelativeLayout rl_collect;
    private RelativeLayout rl_share;
    private ImageView iv_collect;
    private ImageView iv_share;
    private TextView tv_apply;
    private UserVo vo;
    private boolean isCollect = false;

    @Override
    protected int getRid() {
        return R.layout.yd_activity_stroesinfo;
    }

    @Override
    protected void initTitleBar() {
        title_bar.setTvTitleText(title);
    }

    @Override
    protected void initData() {
        intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        image = intent.getStringExtra("img");
        vo = Util.getUserInfo();
        isCollect();
        webView();

    }


    @Override
    protected void initView() {
        webView = getView(R.id.webView);
        tv_apply = getView(R.id.tv_apply);
        iv_share = getView(R.id.iv_share);
        iv_collect = getView(R.id.iv_collect);
        rl_share = getView(R.id.rl_share);
        rl_collect = getView(R.id.rl_collect);
        tv_apply.setOnClickListener(this);
        rl_collect.setOnClickListener(this);
        rl_share.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_apply:
                Intent intent1 = new Intent(this, ChooseStoreActivity.class);
                intent1.putExtra("id", id);
                intent1.putExtra("title", title);
                startActivity(intent1);
                break;
            case R.id.rl_collect:
                collect();
                break;
            case R.id.rl_share:
                share();
                break;
        }

    }

    /**
     * 分享
     */
    private void share() {
        ShareUtil.showShare(this, id, "26", webView.getTitle(), image, ConstantApi.CAMP_INFO + id, null);
    }

    /**
     * 收藏
     */
    private void collect() {
        if (!PersonalCenterUtils.isLogin(Util.getMyApplication())) {
            goLogin();
            return;
        }

        vo = Util.getUserInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("docid", id);
        map.put("type", "24");
        map.put("uid", vo.getUid());
        map.put("token", vo.getAuth());

        NetUtil.Post(Constant.DFY_COLLECT, map, new MyCallBack() {
            @Override
            public void onSuccess(String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    int code = object.getInt("code");
                    String message = object.getString("message");
                    if (code == 0) {
                        isCollect = true;
                        iv_collect.setImageResource(R.drawable.dfy_icon_collect_ok);
                    }
                    CustomToast.showToast(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 跳转到登录页面
     */
    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("notgotohomepage", true);
        intent.putExtra(Constant.DFY_IS_FROM_DFY, true);
        startActivityForResult(intent, Constant.DFY_DETAIL2LOGIN_REQ);
    }

    /**
     * 加载网页
     */
    private void webView() {
        webView.loadUrl(ConstantApi.CAMP_INFO + id);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

            }
        });

        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("GBK");
        webView.addJavascriptInterface(new JavaScriptInterface(), "android");
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }


    /**
     * 判断是否收藏该商品
     */
    private void isCollect() {
        if (!PersonalCenterUtils.isLogin(Util.getMyApplication())) {
            return;
        }

        if (isCollect) {
            return;
        }

        if (vo.getAuth().equals("")) {
            vo = Util.getUserInfo();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", vo.getUid());
        map.put("type", "24");
        map.put("id", id);
        map.put("auth", vo.getAuth());
        NetUtil.Post(Constant.DFY_ISCOLLECT, map, new MyCallBack() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    JSONObject object = new JSONObject(s);
                    JSONObject fav = object.getJSONObject("msg");
                    if (fav.getString("favorite").equals("1")) {
                        isCollect = true;
                        iv_collect.setImageResource(R.drawable.dfy_icon_collect_ok);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        webView.reload();
        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isCollect();
    }

    public class JavaScriptInterface {
        //拨打电话
        public void tel(String telNum) {
            Uri uri = Uri.parse("tel:" + telNum);
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            startActivity(intent);
        }

        //当地人
        public void localPerson(String localId) {
            Intent intent1 = new Intent(StoresInfoActivity.this, LocalPeosonActivity.class);
            intent1.putExtra("id", localId);
            startActivity(intent1);
        }

    }
}
