package com.ccpress.izijia.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.entity.SummaryEntity;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.CallUtil;
import com.ccpress.izijia.util.DialogUtil;
import com.ccpress.izijia.util.ShareUtil;
import com.trs.app.TRSFragmentActivity;
import com.trs.util.ImageDownloader;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;
import com.trs.wcm.LoadWCMJsonTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WLH on 2015/5/6 11:42.
 * 精选、自驾团等不可编辑内容表现形式使用的图文界面
 */
public class ProductDetailActivity extends TRSFragmentActivity{

    private TextView mTxt_Title;
    private LinearLayout mLinear_Content;
    private View mLoadingView;
    private Dialog popDialog = null;

    private String URL = Constant.IDRIVE_URL_BASE +Constant.Moments_Detail_Url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetail);

        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mTxt_Title = (TextView) findViewById(R.id.product_detail_title);
        mLinear_Content = (LinearLayout) findViewById(R.id.product_detail_contnet);
        mLoadingView = findViewById(R.id.loading_view);
    }

    /**
     *填充数据
     */
    private void initData(){

        URL += getIntent().getStringExtra(LinesDetailImageTextActivity.EXTRA_LID);

        Log.e("WLH", "ProductDetailActivity url:"+URL);
        mLoadingView.setVisibility(View.VISIBLE);
        LoadWCMJsonTask task = new LoadWCMJsonTask(this) {
            @Override
            public void onDataReceived(String result, boolean isCache) throws Exception {
                mLoadingView.setVisibility(View.GONE);
                parseData(result);
            }

            @Override
            public void onError(Throwable t) {
                mLoadingView.setVisibility(View.GONE);
                Toast.makeText(ProductDetailActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
            }
        };
        task.start(URL);
    }

    /**
     * 筛选数据
     * @param result
     */
    private void parseData(String result) {
        mLinear_Content.removeAllViews();
        try {
            JSONObject object = new JSONObject(result);

            SummaryEntity entity = new SummaryEntity(object.getJSONObject("summary"));
            if(entity != null){
                mTxt_Title.setText(entity.getTitle());
            }
            JSONArray array = object.getJSONArray("moments");
            if(array == null){
                return;
            }
            for (int i = 0; i< array.length(); i++){
                JSONObject object1 = array.getJSONObject(i);
                View view = LayoutInflater.from(this).inflate(R.layout.content_product_detail, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.product_detail_content_img);
                TextView textView = (TextView) view.findViewById(R.id.product_detail_content_txt);
                String imgUrl = object1.getString("image");
                String imgTitle = object1.getString("desc");
                if(!StringUtil.isEmpty(imgUrl)){
                    imageView.setVisibility(View.VISIBLE);
                    new ImageDownloader.Builder()
                            .setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC)
                            .build(imgUrl, imageView)
                            .start();
                }else {
                    imageView.setVisibility(View.GONE);
                }
                if(!StringUtil.isEmpty(imgTitle)){
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(imgTitle);
                }else {
                    textView.setVisibility(View.GONE);
                }
                mLinear_Content.addView(view);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void OnBtnShareClick(View view){
        ShareUtil.showShare(this, null,null,null,null,null,null);
    }
    public void OnBtnPraiseClick(View view){
        DialogUtil.showResultDialog(this, "点赞成功", R.drawable.icon_success);
    }
    public void OnBtnCommentClick(View view){
        startActivity(new Intent(this, CommentActivity.class));
    }
    public void OnBtnMoreClick(View view){
//        showPopUp(view);
        showDialog();
    }

    private void showDialog(){
        if(popDialog == null){
            popDialog = new Dialog(this, R.style.popFromBottomdialog);
            View contentView = LayoutInflater.from(this).inflate(R.layout.popupview_more, null);
            popDialog.setContentView(contentView);
            Window dialogWindow = popDialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.FILL_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setAttributes(lp);

            RelativeLayout mReport = (RelativeLayout) contentView.findViewById(R.id.btn_report);
            RelativeLayout mCollect = (RelativeLayout) contentView.findViewById(R.id.btn_collect);
            RelativeLayout mJoinin = (RelativeLayout) contentView.findViewById(R.id.btn_joinin);
            RelativeLayout mCancel = (RelativeLayout) contentView.findViewById(R.id.btn_cancel);
            mReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    ProductDetailActivity.this.startActivity(new Intent(ProductDetailActivity.this, ReportActivity.class));
                }
            });
            mCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                }
            });
            mJoinin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    CallUtil.showDialog("4008008810", ProductDetailActivity.this);
                }
            });
            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                }
            });

        }else {
            if(popDialog.isShowing()){
                popDialog.dismiss();
                popDialog = null;
                return;
            }
        }
        popDialog.show();
    }
}
