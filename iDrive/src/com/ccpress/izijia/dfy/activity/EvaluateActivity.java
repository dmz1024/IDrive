package com.ccpress.izijia.dfy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.callBack.MyCallBack;
import com.ccpress.izijia.dfy.constant.Constant;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.dfy.view.RatingImageView;
import com.ccpress.izijia.dfy.view.TopView;
//import com.ccpress.izijia.dfy.view.UpLoadGridView;
import com.ccpress.izijia.dfy.view.UpLoadGridView;
import com.ccpress.izijia.util.HttpPostUtil;
import com.ccpress.izijia.vo.UserVo;
import net.endlessstudio.util.httpclient.FilePart;
import net.endlessstudio.util.httpclient.Part;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by administror on 2016/3/29 0029.
 */
public class EvaluateActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.iv_star_1)
    private RatingImageView riv_1;
    @ViewInject(R.id.iv_star_2)
    private RatingImageView riv_2;
    @ViewInject(R.id.iv_star_3)
    private RatingImageView riv_3;
    @ViewInject(R.id.et_evaluate)
    private EditText et_evaluate;
    @ViewInject(R.id.iv_thumb)
    private ImageView iv_thumb;
    @ViewInject(R.id.tv_goods_name)
    private TextView tv_goods_name;
    @ViewInject(R.id.tv_submit)
    private TextView tv_submit;
    @ViewInject(R.id.upGrid)
    private UpLoadGridView upGrid;
    private String goodsid;
    private String order_sn;
    private String thumb;
    private String goods_name;
    private UserVo vo;
    private boolean isSub=false;


    @Override
    protected void initView() {
        super.initView();
        tv_submit.setOnClickListener(this);
        vo= Util.getUserInfo();
        Intent intent = getIntent();
        goodsid = intent.getStringExtra("goodsid");
        order_sn = intent.getStringExtra("order_sn");
        thumb = intent.getStringExtra("thumb");
        goods_name = intent.getStringExtra("goods_name");
        tv_goods_name.setText(goods_name);
        x.image().bind(iv_thumb, Constant.DFY + thumb);

        upGrid.setActivity(this);
        upGrid.setMaxItem(5);
        upGrid.setAdapter();

    }

    @Override
    protected int getRid() {
        return R.layout.dfy_activity_evaluate;
    }

    @Override
    protected void initTopView(TopView topView) {
        super.initTopView(topView);
        topView.setText("提交评论");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_submit:
                submit();
                break;
        }
    }

    private void submit() {
        if(isSub){
            CustomToast.showToast("正在加载图片，请稍后...");
            return;
        }
        isSub=true;
        tv_submit.setBackgroundResource(R.color.dfy_999);
        final ProgressDialog pdlog = new ProgressDialog(this);
        pdlog.setMessage("正在提交评论...");
        pdlog.setCancelable(false);
        pdlog.show();
        String evaluate = et_evaluate.getText().toString();
        if(evaluate.equals("")){
            Toast.makeText(this,"请输入评论内容",Toast.LENGTH_SHORT).show();
        }
        int level_1 = riv_1.getStarNum();
        int level_2 = riv_2.getStarNum();
        int level_3 = riv_3.getStarNum();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("xianlu", level_1);
        map.put("canshi", level_2);
        map.put("jiaotong", level_3);
        map.put("uid", vo.getUid());//23734
        map.put("content", evaluate);
        map.put("goodsid", goodsid);
        map.put("order_sn", order_sn);
        map.put("username", vo.getUserName());
        map.put("laiyuan","android");
        List<String> list = upGrid.getmResults();
        for(int i=0;i<list.size();i++){
            map.put("img"+(i+1),bitmaptoString(list.get(i)));
        }

        NetUtil.Post(Constant.DFY_EVALUTE, map, new MyCallBack() {
            @Override
            public void onSuccess(String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    int result = object.getInt("result");
                    String data = object.getString("data");

                    if (result == 0) {
                        CustomToast.showToast("评论成功");
                        Intent intent = new Intent(EvaluateActivity.this, OrderActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        CustomToast.showToast(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFinished() {
                pdlog.cancel();
                isSub=false;
                tv_submit.setBackgroundResource(R.color.dfy_50bbdb);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        upGrid.setImage(requestCode, data);
    }

    public String bitmaptoString(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        //将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

}
