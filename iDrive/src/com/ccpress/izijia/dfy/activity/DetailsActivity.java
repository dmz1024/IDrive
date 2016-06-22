package com.ccpress.izijia.dfy.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.portal.LoginActivity;
import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.entity.Cyrp;
import com.ccpress.izijia.dfy.entity.Goodinfo;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.dfy.view.TopView;
import com.ccpress.izijia.util.ShareUtil;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.UserVo;
import com.trs.types.Detail;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by administror on 2016/3/22 0022.
 */
public class DetailsActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.iv_collect)
    private ImageView iv_collect;
    @ViewInject(R.id.tv_apply)
    private TextView tv_apply;
    @ViewInject(R.id.webview)
    private WebView webView;
    @ViewInject(R.id.rl_collect)
    private RelativeLayout rl_collect;
    @ViewInject(R.id.rl_call)
    private RelativeLayout rl_call;

    private TextView tv_number;
    private TextView tv_phone;
    private TextView tv_Callphone;
    private TextView tv_cancel;

    private boolean isCollect = false;
    private String id;
    private Goodinfo goodinfo;
    private View popView;
    private PopupWindow popCall;
    private boolean isOk = false;
    private UserVo vo;
    private boolean isHav=false;

    private TopView topView;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected int getRid() {
        return R.layout.dfy_activity_details;
    }

    @Override
    protected void initView() {
        super.initView();
        vo = Util.getUserInfo();
        id = getIntent().getStringExtra("id");
        initOnClick();
        isCollect();
        getGoodInfo();

        initWeb();
    }


    private void initOnClick() {
        rl_collect.setOnClickListener(this);
        rl_call.setOnClickListener(this);
        tv_apply.setOnClickListener(this);
    }

    private String imageURL;

    @Override
    protected void initTopView(TopView topView) {
        super.initTopView(topView);
        topView.setPbVisibility(true);
        topView.setText("商品详情");
        topView.setRightImage(R.drawable.dfy_icon_lianjie);
        //分享
        topView.setRightOnclick(new TopView.RightOnclick() {
            @Override
            public void right() {
                ShareUtil.showShare(DetailsActivity.this, goodinfo.getGoodsid(), "16", webView.getTitle(), imageURL, Constant.DFYDFY_GOOD + id, null);
            }
        });
        this.topView = topView;
    }


    /**
     * 获取报名时间和截止时间信息
     */
    private void getGoodInfo() {

        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        NetUtil.get(Constant.DFY_GOODSINFO, map, new MyCallBack() {
            @Override
            public void onSuccess(String json) {
                Log.d("json", json);

                goodinfo = JsonUtil.json2GoodInfo(json);
                if (goodinfo.getResult() == "1") {
                    tv_apply.setText("数据获取失败");
                    tv_apply.setBackgroundResource(R.color.dfy_999);
                    return;
                }

                topView.setRightVisibility(true);
                imageURL=goodinfo.getGoods_thumb();

                long currenZero = new Date().getTime();
                List<Cyrp> listRl = goodinfo.getRili();

                outterLoop:for (int i = 0; i < listRl.size(); i++) {

                    String time = listRl.get(i).getAttr_end() == "" ? listRl.get(i).getAttr_value() : listRl.get(i).getAttr_end();

                    try {
                        long endMillionSeconds = sdf.parse(time).getTime()+100;
                        if (currenZero < endMillionSeconds) {
                            isHav=true;
                            break outterLoop;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                if(!isHav){
                    tv_apply.setText("报名结束");
                    tv_apply.setBackgroundResource(R.color.dfy_999);
                }

                if (!isOk && isHav) {
                    tv_apply.setBackgroundResource(R.color.dfy_50bbdb);
                    tv_apply.setText(R.string.dfy_apply);
                }
                isOk = true;
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                tv_apply.setText("数据获取失败");
                tv_apply.setBackgroundColor(getResources().getColor(R.color.dfy_999));
            }
        });
    }
    /**
     * 判断是否收藏该商品
     */
    private void isCollect() {
        if (!PersonalCenterUtils.isLogin(Util.getMyApplication())) {
            return;
        }

        if (vo.getAuth().equals("")) {
            vo = Util.getUserInfo();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", vo.getUid());
        map.put("type", "20");
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

            @Override
            public void onError(Throwable throwable, boolean b) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_collect:
                collect();//收藏
                break;
            case R.id.rl_call:
                showCall();//弹出拨打电话的pop
                break;
            case R.id.tv_cancel:
                popCall.dismiss();
                break;
            case R.id.tv_Callphone:
                call();//拨打电话
                break;
            case R.id.tv_apply:
                goCalendar();//去选择出行时间界面
                break;


        }
    }

    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("notgotohomepage", true);
        intent.putExtra(Constant.DFY_IS_FROM_DFY, true);
        startActivityForResult(intent, Constant.DFY_DETAIL2LOGIN_REQ);
    }

    private void goCalendar() {
        if(!isHav){
            return;
        }

        if (!isOk) {
            getGoodInfo();
            return;
        }

        if (!PersonalCenterUtils.isLogin(Util.getMyApplication())) {
            goLogin();
            return;
        }


        Intent intent = new Intent(DetailsActivity.this, CalendarActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("goodInfo", goodinfo);
        startActivity(intent);

    }


    private void showCall() {
        backgroundAlpha(0.5f);
        popView = View.inflate(this, R.layout.dfy_pop_telephone, null);
        popCall = new PopupWindow(popView,
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
        popCall.setAnimationStyle(R.style.mypopwindow_anim_style);
        tv_number = (TextView) popView.findViewById(R.id.tv_number);
        tv_phone = (TextView) popView.findViewById(R.id.tv_phone);
        tv_Callphone = (TextView) popView.findViewById(R.id.tv_Callphone);
        tv_cancel = (TextView) popView.findViewById(R.id.tv_cancel);
        tv_number.setOnClickListener(this);
        tv_phone.setOnClickListener(this);
        tv_Callphone.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        popCall.setOnDismissListener(this);

        popCall.showAtLocation(rl_call, Gravity.BOTTOM, 0, 0);
        tv_number.setText("产品编号：" + goodinfo.getGoodssn());
        if (isOk) {
            tv_phone.setText(goodinfo.getTel() + "(咨询热线)");
        } else {
            tv_phone.setText("电话加载失败");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constant.DFY_LOGIN2DETAIL_RES) {
            isCollect();
        }

    }

    private void collect() {

        if (isCollect) {
            return;
        }

        if (!PersonalCenterUtils.isLogin(Util.getMyApplication())) {
            goLogin();
            return;
        }

        vo = Util.getUserInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("docid", id);
        map.put("type", "16");
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


    private void call() {

        popCall.dismiss();
        if (!isOk) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + goodinfo.getTel()));
        startActivity(intent);
    }

    class JavaScriptInterface {

        public void toCalendar() {
            goCalendar();
        }

        public void toAkela() {
            if (!isOk) {
                getGoodInfo();
                return;
            }

            Intent intent = new Intent(DetailsActivity.this, AkelaActivity.class);
            intent.putExtra("brand", goodinfo.getBrand_id());
            startActivity(intent);

        }
    }

    private void initWeb() {
        webView.loadUrl(Constant.DFYDFY_GOOD_A + id);
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
                top_view.setPbVisibility(false);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                top_view.setPbVisibility(false);

            }
        });

        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("GBK");
        webView.addJavascriptInterface(new JavaScriptInterface(), "android");
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }

}


