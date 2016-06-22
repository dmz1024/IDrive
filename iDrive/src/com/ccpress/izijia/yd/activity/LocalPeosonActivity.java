package com.ccpress.izijia.yd.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.portal.LoginActivity;
import com.ccpress.izijia.dfy.activity.OrderActivity;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.JsonUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.utils.PersonalCenterUtils;
import com.ccpress.izijia.vo.UserVo;
import com.ccpress.izijia.yd.adapter.LocalPersonAdapter;
import com.ccpress.izijia.yd.api.HttpApi;
import com.ccpress.izijia.yd.api.MyImageLoader;
import com.ccpress.izijia.yd.constant.ConstantApi;
import com.ccpress.izijia.yd.entity.LocalComment;
import com.ccpress.izijia.yd.entity.LocalPeoson;
import com.ccpress.izijia.yd.view.MaxListView;
import com.ccpress.izijia.yd.view.RotationViewPager;
import com.ccpress.izijia.yd.view.StarImageView;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/31.
 */
public class LocalPeosonActivity extends BaseActivity {
    private String id;
    private RotationViewPager rvp;
    private ImageView iv_back;
    private ImageView iv_icon;
    private TextView tv_name;
    private TextView tv_price;
    private FrameLayout fg_star;
    private FrameLayout fg_comment;
    private StarImageView fg_comment_star;
    private TextView tv_star;
    private TextView tv_time;
    private TextView tv_sex;
    private TextView tv_age;
    private TextView tv_area;
    private TextView tv_speciality;
    private TextView tv_content_1;
    private TextView tv_content_2;
    private TextView tv_content_3;
    private TextView tv_content_4;
    private TextView tv_all;
    private Button bt_tel;
    private Button bt_comment;
    private MaxListView lv_comment;
    private EditText et_content;
    private RelativeLayout rl_anonymous;
    private LocalPeoson.Data data;
    private int[] images;
    private int isAnonymous = 1;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private MyImageLoader loader = new MyImageLoader(10);

    @Override
    protected int getRid() {
        return R.layout.yd_local_activity;
    }

    @Override
    protected void initTitleBar() {
        title_bar.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        new HttpApi(ConstantApi.LOCAL, map) {
            @Override
            protected void success(String json) {
                try {
                    LocalPeoson localPeoson = JsonUtil.getJavaBean(json, LocalPeoson.class);
                    if (localPeoson.result == 0) {
                        data = localPeoson.data;
                        fillData();
                    } else {
                        CustomToast.showToast("当地人信息获取失败");
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void error() {
                CustomToast.showToast("当地人信息获取失败");
                finish();
            }
        }.post();
        commentList();
    }

    /**
     * 填充数据
     */
    private void fillData() {
        List<View> views = new ArrayList<View>();
        List<LocalPeoson.Lb> lbs = data.lb;
        for (int i = 0; lbs != null && i < lbs.size(); i++) {
            ImageView iv = new ImageView(this);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            loader.get(iv, lbs.get(i).img_original);
            views.add(iv);
        }
        rvp.setImages(views);

        tv_name.setText(data.goods_name);
        tv_price.setText("￥" + data.shop_price + "/天");
        images = new int[]{R.drawable.yd_star_1, R.drawable.yd_star_2, R.drawable.yd_star_3, R.drawable.yd_star_4, R.drawable.yd_star_5};
        int rank = data.rank;
        if (rank <= 0) {
            fg_star.setBackgroundResource(R.drawable.yd_star_0);
        } else {
            fg_star.setBackgroundResource(images[rank - 1]);
        }
        tv_star.setText(rank + "");
        tv_time.setText("从业时间：" + sdf.format(new Long(data.opentime + "000")));
        tv_sex.setText("性      别：" + data.sex);
        tv_age.setText("年      龄：" + data.nianling + "岁");
        tv_area.setText("地      区：" + data.city.region_name);

        List<LocalPeoson.TypeList> typeLists = data.type_list;
        if (typeLists != null && typeLists.size() > 0) {
            String[] selects = typeLists.get(0).select;
            StringBuffer tcBuffer = new StringBuffer("特     长：");
            for (int i = 0; i < selects.length; i++) {
                tcBuffer.append(selects[i]).append("   ");
            }
            tv_speciality.setText(tcBuffer.toString());
        }

        tv_content_1.setText(data.goods_desc);
        tv_content_2.setText(data.rzgz);
        tv_content_3.setText(data.tksm);
        tv_content_4.setText(data.cwsm);

    }

    /**
     * 获取评论列表
     */
    private void commentList() {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("type", "2");
        new HttpApi(ConstantApi.CLIST, map) {
            @Override
            protected void success(String json) {
                LocalComment localComment = JsonUtil.getJavaBean(json, LocalComment.class);
                if (localComment.result == 0) {
                    List<LocalComment.Data> data1 = localComment.data;
                    tv_all.setText("全部评论(" + localComment.tongji.number + ")");
                    lv_comment.setAdapter(new LocalPersonAdapter(LocalPeosonActivity.this, data1));
                    int rank = data.rank;
                    if (rank <= 0) {
                        fg_comment.setBackgroundResource(R.drawable.yd_star_0);
                    } else {
                        fg_comment.setBackgroundResource(images[rank - 1]);
                    }
                }
            }
        }.post();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_anonymous:
                anonymous();
                break;
            case R.id.bt_comment:
                comment();
                break;
            case R.id.bt_tel:
                tel();
                break;

        }
    }

    /**
     * 联系营地
     */
    private void tel() {
        Uri uri = Uri.parse("tel:" + data.tel1);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(intent);
    }


    /**
     * 提交评价
     */
    private void comment() {
        int starNum = fg_comment_star.getStarNum();
        String content = et_content.getText().toString();
        if (TextUtils.isEmpty(content)) {
            CustomToast.showToast("请输入评论内容");
            return;
        }

        if (!PersonalCenterUtils.isLogin(Util.getMyApplication())) {
            goLogin();
            return;
        }
        UserVo vo = Util.getUserInfo();
        Map<String, String> map = new HashMap<>();
        map.put("uid", vo.getUid());
        map.put("type", "2");
        map.put("laiyuan", "android");
        map.put("id", id);
        map.put("userphoto", vo.getUserPhoto());
        map.put("content", content);
        map.put("hide_username", isAnonymous + "");
        map.put("username", vo.getUserName());
        map.put("rank", fg_comment_star.getStarNum() + "");
        new HttpApi(ConstantApi.COMMENT, map) {
            @Override
            protected void success(String json) {
                try {
                    JSONObject object = new JSONObject(json);
                    int result = object.getInt("result");
                    String data = object.getString("data");
                    if (result == 0) {
                        CustomToast.showToast("评论成功");
                        et_content.setText("");
                        commentList();
                    } else {
                        CustomToast.showToast(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.post(this, "正在提交评论");

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
     * 是否匿名
     */
    private void anonymous() {
        if (isAnonymous == 1) {
            iv_icon.setImageResource(R.drawable.yd_anonymous);
            isAnonymous = 2;
        } else {
            iv_icon.setImageResource(R.drawable.yd_anonymous_select);
            isAnonymous = 1;
        }
    }

    @Override
    protected void initView() {
        rvp = getView(R.id.rvp);
        iv_back = getView(R.id.iv_back);
        iv_back.setOnClickListener(this);
        iv_icon = getView(R.id.iv_icon);
        tv_name = getView(R.id.tv_name);
        tv_price = getView(R.id.tv_price);
        fg_star = getView(R.id.fg_star);
        fg_comment = getView(R.id.fg_comment);
        fg_comment_star = getView(R.id.fg_comment_star);
        fg_comment_star.setCheck(true);
        tv_star = getView(R.id.tv_star);
        tv_time = getView(R.id.tv_time);
        tv_sex = getView(R.id.tv_sex);
        tv_age = getView(R.id.tv_age);
        tv_area = getView(R.id.tv_area);
        tv_speciality = getView(R.id.tv_speciality);
        tv_content_1 = getView(R.id.tv_content_1);
        tv_content_2 = getView(R.id.tv_content_2);
        tv_content_3 = getView(R.id.tv_content_3);
        tv_content_4 = getView(R.id.tv_content_4);
        tv_all = getView(R.id.tv_all);
        bt_tel = getView(R.id.bt_tel);
        bt_tel.setOnClickListener(this);
        bt_comment = getView(R.id.bt_comment);
        bt_comment.setOnClickListener(this);
        lv_comment = getView(R.id.lv_comment);
        et_content = getView(R.id.et_content);
        rl_anonymous = getView(R.id.rl_anonymous);
        rl_anonymous.setOnClickListener(this);
    }
}
