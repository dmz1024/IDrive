package com.ccpress.izijia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.portal.LoginActivity;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.DialogUtil;
import com.ccpress.izijia.util.PraiseUtil;
import com.froyo.commonjar.utils.SpUtil;
import com.trs.app.TRSFragmentActivity;
import com.trs.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WLH on 2015/5/12 17:50.
 */
public class ReportActivity extends TRSFragmentActivity{

    public static String EXTRA_IS_SHARE_INSIDE = "isShareInside";
    public static String EXTRA_IMGURL = "imgURL";
    public static  String EXTRA_URL="url";

    private TextView mTxtTitle;
    private TextView mTxtSend;
    private EditText mEdit;
    public static String docid;
    public static  String type;
    private String city;

    public static  boolean isShareInside = false;

    public static String title;
    public static String imageURL;
    public static String shareurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        docid = getIntent().getStringExtra(InfoDetailActivity.EXTRA_DOCID);
        type = getIntent().getStringExtra(CommentActivity.EXTRA_TYPE);
        isShareInside =  getIntent().getBooleanExtra(EXTRA_IS_SHARE_INSIDE, false);
        title = getIntent().getStringExtra(EXTRA_TITLE);
        imageURL = getIntent().getStringExtra(EXTRA_IMGURL);
        shareurl=getIntent().getStringExtra(EXTRA_URL);
        if(iDriveApplication.app().getLocation() != null){
            city = iDriveApplication.app().getLocation().getCity();
          }else {
            city="北京市";
        }
        init();
    }

    /**
     * 初始化布局控件
     */
    private void init() {
        mTxtTitle = (TextView)findViewById(R.id.title);
        mTxtSend = (TextView)findViewById(R.id.btn_ok);
        mEdit = (EditText) findViewById(R.id.edit_text);

        if(isShareInside){
            mTxtTitle.setText("分享到互动");
            mEdit.setHint("说点什么吧");
        }else {
            mTxtTitle.setText("举报");
        }
    }

    /**
     * 发送点击
     * @param v
     */
    public void onOkClick(View v){
      String content = mEdit.getText().toString().trim();
        if(StringUtil.isEmpty(content)){
            Toast.makeText(this, "评论内容不能为空~", Toast.LENGTH_SHORT).show();
            return;
        }

        SpUtil sp = new SpUtil(this);
        String uid = sp.getStringValue(Const.UID);
        String token = sp.getStringValue(Const.AUTH);

        if(StringUtil.isEmpty(uid)){//如果未登录，跳转到登录页面
            Intent intent=new Intent(this, LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_NOT_GOTO_HOMEPAGE, true);
            startActivity(intent);
            return;
        }

        mEdit.setText("");

        if(!isShareInside){
           submitReportContent(uid, token, content,city);
        }else {
            submitToInteract(uid, token, content,city);
        }

    }

    public void onBtnBackClick(View v){
                finish();
        }

    /**
     * 上传举报数据
     * @param uid
     * @param token
     * @param content
     * @param city
     */
    private void submitReportContent(String uid, String token, String content,String city){

        String url = Constant.INTERACT_URL_BASE_SERVICE + Constant.INTERACT_REPORT;
        final Map<String, String> map = new HashMap<>();
        map.put("docid", docid);
        map.put("type", type);
        map.put("content", content);
        map.put("token", token);
        map.put("uid", uid);
        map.put("city",city);

        PraiseUtil.PostTask task = new PraiseUtil.PostTask(this, map){
            @Override
            protected void onPostExecute(String message) {
                message = PraiseUtil.parseResponse(ReportActivity.this, message);
                if(StringUtil.isEmpty(message)){
                    message="举报失败~";
                }
                if(message.contains("成功")){
                    DialogUtil.showResultDialog(ReportActivity.this, message, R.drawable.icon_success);
                }else {
                    DialogUtil.showResultDialog(ReportActivity.this, message, R.drawable.icon_delete);
                }
            }
        };

        task.execute(url);
    }


    /**
     * 上传分享数据
     * @param uid
     * @param token
     * @param content
     * @param city
     */
    private void submitToInteract(String uid, String token, String content,String city){
        String url = Constant.INTERACT_URL_BASE_SERVICE + Constant.INTERACT_SHARE;

        final Map<String, String> map = new HashMap<>();
        map.put("docid", docid);
        map.put("type", type);
        map.put("content", content);
        map.put("token", token);
        map.put("uid", uid);
        map.put("city",city);
        map.put("url",shareurl);

        if(isShareInside){
            map.put("title", title);
            map.put("thumb", imageURL);
        }
        PraiseUtil.PostTask task = new PraiseUtil.PostTask(this, map){
            @Override
            protected void onPostExecute(String message) {
                message = PraiseUtil.parseResponse(ReportActivity.this, message);
                if(StringUtil.isEmpty(message)){
                    message="分享失败~";
                }
                Log.e("WKH", "shareInteract message:"+message);
                if(message.contains("成功") || message.contains("已")){
                    DialogUtil.showResultDialogNeedFinish(ReportActivity.this, message, R.drawable.icon_success);
                }else {
                    DialogUtil.showResultDialogNeedFinish(ReportActivity.this, message, R.drawable.icon_delete);
                }
            }
        };

        task.execute(url);
    }


}
