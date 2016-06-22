package com.ccpress.izijia.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.mystyle.MystyleActivity;
import com.ccpress.izijia.entity.ViewspotDetailEntity;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.*;
import com.trs.app.TRSFragmentActivity;
import com.trs.util.ImageDownloader;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;
import com.trs.wcm.RemoteDataService;
import com.trs.wcm.callback.BaseDataAsynCallback;
import com.viewpagerindicator.CirclePageIndicator;
import org.json.JSONObject;

/**
 * Created by WLH on 2015/5/25 14:34.
 * 看点详情页、停车发呆地详情页
 */
public class ViewSpotDetailActivity extends TRSFragmentActivity{

    public static final String EXTRA_TYPE = "type";
    private int type = 0;//0：看点详情页 1：停车发呆地详情页

    private TextView mTxtDesc;
    private TextView mTxtAddr;
    private TextView mTxtSeason;
    private TextView mTxtPrice;
    private TextView mTxtTime;
    private View mLoadingView;
    private TextView mTxtTitle;
    private ViewPager mPager;
    private CirclePageIndicator mIndicator;
    private View Linear_address;
    private ImageView mImgDesc;
    private ImageView mImgPrice;
    private ImageView mImgTime;
    private ImageView mImgSeason;
    private TextView mSummary;
    private  TextView mTxtCustom;
    private ImageView mImgPraise;
    private TextView mTxtPraise;

    private Dialog popDialog = null;

    private String Url = Constant.IDRIVE_URL_BASE + Constant.ViewSpot_Detail_url;

    private ViewspotDetailEntity entity;

    private String docid;
    private String detailType = Constant.DETAIL_TYPE_ViewSpot;
    DetailStatusUtil statusUtil = new DetailStatusUtil(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewspot_detail);

        docid = getIntent().getStringExtra(LinesDetailImageTextActivity.EXTRA_LID);

        init();
        initData();
        initStatus();
    }

    /**
     * 获取“点赞”的数据状态
     */
    private void initStatus() {
        statusUtil.getDetailStatus(docid, detailType, new BaseDataAsynCallback() {
            @Override
            public void onDataLoad(String result, boolean bIsChanged) {
                if(statusUtil.IsPraise()){
                    mTxtPraise.setText("已点赞");
                    mImgPraise.setImageResource(R.drawable.icon_praised);
                }
                if (statusUtil.IsCustom()){
                        mTxtCustom.setText("已经定制");
                    }else {
                        mTxtCustom.setText("加入定制");
                    }
            }
        });

    }

    /**
     * 初始化布局控件
     */
    private void init() {
        type = getIntent().getIntExtra(EXTRA_TYPE, 0);

        mPager = (ViewPager) findViewById(R.id.pager);
        mIndicator = (CirclePageIndicator) findViewById(R.id.pager_indicator);
        mPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mPager);

        mTxtDesc = (TextView) findViewById(R.id.desc);
        mTxtAddr = (TextView) findViewById(R.id.addr);
        mTxtSeason = (TextView) findViewById(R.id.season);
        mTxtPrice = (TextView) findViewById(R.id.price);
        mTxtTime = (TextView) findViewById(R.id.time);
        mLoadingView = findViewById(R.id.loading_view);
        mTxtTitle = (TextView) findViewById(R.id.title);
        Linear_address = findViewById(R.id.Linear_address);
        mTxtAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(entity != null){
                    Intent intent = new Intent(ViewSpotDetailActivity.this, InfoMapActivity.class);
                    intent.putExtra("name", entity.getName());
                    intent.putExtra("addr", entity.getAddr());
                    intent.putExtra("geo", entity.getGeo());
                    intent.putExtra(InfoMapActivity.EXTRA_HASLINES, false);
                    startActivity(intent);
                }
            }
        });

        mImgDesc = (ImageView) findViewById(R.id.ic_desc);
        mImgPrice = (ImageView) findViewById(R.id.ic_ticket);
        mImgTime = (ImageView) findViewById(R.id.ic_time);
        mImgSeason = (ImageView) findViewById(R.id.ic_seacon);
        mTxtCustom = (TextView)findViewById(R.id.txt_joinin);
        if(statusUtil.IsCustom()){
            mTxtCustom.setText("已经定制");
        }else {
            mTxtCustom.setText("加入定制");
        }
        //定制
        mTxtCustom.setOnClickListener(new View.OnClickListener() {//加入定制
            @Override
            public void onClick(View view) {
                CustomUtil.CustomOrCancel(ViewSpotDetailActivity.this, docid, detailType,
                        statusUtil.IsCustom(), new PraiseUtil.ResultCallback() {
                            @Override
                            public void callback(boolean isSuccess) {
                                if (isSuccess) {
                                    if (statusUtil.IsCustom()) {
                                        statusUtil.setIsCustom(false);
                                        mTxtCustom.setText("加入定制");
                                    } else {
                                        statusUtil.setIsCustom(true);
                                        mTxtCustom.setText("已经定制");
                                    }
                                }
                            }
                        });

            }
        });

        mSummary = (TextView) findViewById(R.id.summary);
        if(type==1){
            mSummary.setText("详情");
            detailType = Constant.DETAIL_TYPE_PARK;
        }else {
            findViewById(R.id.layout_03).setVisibility(View.GONE);
            findViewById(R.id.layout_05).setVisibility(View.GONE);
        }
        mTxtPraise = (TextView) findViewById(R.id.txt_praise);
        mImgPraise = (ImageView) findViewById(R.id.icon_praise);
    }

    /**
     * 填充布局数据
     */
    private void initData() {
        if(type == 1){
            Url = Constant.IDRIVE_URL_BASE + Constant.ParkDaze_Detail_url;
        }
        Url += docid;
        Log.e("WLH","Viewspot url:"+Url);
        mLoadingView.setVisibility(View.VISIBLE);
        RemoteDataService rd = new RemoteDataService(this);
        rd.loadJSON(Url, new BaseDataAsynCallback() {
            @Override
            public void onDataLoad(String _result) {
                mLoadingView.setVisibility(View.GONE);
                if(!StringUtil.isEmpty(_result)){
                    parseData(_result);
                }
                else {
                    Toast.makeText(ViewSpotDetailActivity.this, "数据加载失败", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onError(String url) {
                mLoadingView.setVisibility(View.GONE);
                Toast.makeText(ViewSpotDetailActivity.this, "数据加载失败", Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * 筛选数据
     * @param result
     */
    private void parseData(String result) {
        try {
            JSONObject object = new JSONObject(result);
            entity = new ViewspotDetailEntity(object);
            if(entity.getMessage().equals("无看点详情")){
               // Toast.makeText(this, entity.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            if(entity!=null){
                if(entity.getImages()!= null){

                    if(entity.getImages().size() < 2){
                        mIndicator.setVisibility(View.GONE);
                    }else {
                        mIndicator.setVisibility(View.VISIBLE);
                    }
                    mAdapter.notifyDataSetChanged();
                }
                mTxtTitle.setText(entity.getName());
                mTxtDesc.setText(entity.getDesc());
                mTxtAddr.setText(entity.getAddr());
                mTxtPrice.setText(entity.getTips());
                mTxtSeason.setText(entity.getSeason());
                mTxtTime.setText(entity.getTime());

                if(type==1){

                    mTxtPrice.setText("发呆方式：" + entity.getPrice());
                    mTxtSeason.setText("发呆时间："+entity.getTime());
                    mTxtTime.setText(entity.getSeason());
                    mImgDesc.setImageResource(R.drawable.icon_daze_desc);

                    mImgTime.setImageResource(R.drawable.icon_daze_reason);
                    mImgPrice.setImageResource(R.drawable.icon_daze_way);
                    mImgSeason.setImageResource(R.drawable.icon_time);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private PagerAdapter mAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            if(entity != null && entity.getImages() != null){
                return entity.getImages().size();
            }
            return 0;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imgView = new ImageView(ViewSpotDetailActivity.this);
            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgView.setBackgroundResource(R.drawable.cqsw_default_pic);

            if(entity != null && entity.getImages() != null){
                ViewspotDetailEntity.Images topic = entity.getImages().get(position);
                if(topic != null){
                    new ImageDownloader.Builder().setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).
                            build(topic.getImage(), imgView).start();
                }
            }



            container.addView(imgView);
            return imgView;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(entity != null && entity.getImages() != null){
                entity.getImages().get(position).getTitle();
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    };

    /**
     * 分享
     * @param view
     */
    public void OnBtnShareClick(View view){
        String title = (entity==null) ? null : entity.getName();
        String imageURL = (entity !=null && entity.getImages()!= null && entity.getImages().size() >0) ? entity.getImages().get(0).getImage() : null;
        String content = (entity==null) ? null : entity.getDesc();
        String url = (entity==null) ? null : entity.getUrl();
        ShareUtil.showShare(this, docid, detailType, title, imageURL, url, content);
    }

    /**
     * 点赞
     * @param view
     */
    public void OnBtnPraiseClick(View view){
        PraiseUtil.PraiseOrCancel(this, docid, detailType, statusUtil.IsPraise(),
                new PraiseUtil.ResultCallback() {
                    @Override
                    public void callback(boolean isSuccess) {
                        if (isSuccess) {
                            if (statusUtil.IsPraise()) {
                                statusUtil.setIsPraise(false);
                                mTxtPraise.setText("点赞");
                                mImgPraise.setImageResource(R.drawable.bottom_praise);
                            } else {
                                statusUtil.setIsPraise(true);
                                mTxtPraise.setText("已点赞");
                                mImgPraise.setImageResource(R.drawable.icon_praised);
                            }
                        }
                    }
                });
    }

    /**
     * 评论
     * @param view
     */
    public void OnBtnCommentClick(View view){
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra(InfoDetailActivity.EXTRA_DOCID, docid);
        intent.putExtra(CommentActivity.EXTRA_TYPE, detailType);
        startActivity(intent);
    }
    public void OnBtnMoreClick(View view){
        showDialog();
    }

    /**
     * 底部Dialog显示
     */
    private void showDialog() {
        popDialog=null;
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
            final TextView mTxtCollect = (TextView) contentView.findViewById(R.id.txt_collect);
            if(statusUtil.IsFavorite()){
                mTxtCollect.setText("已收藏");
            }else {
                mTxtCollect.setText("收藏");
            }
            RelativeLayout mJoinin = (RelativeLayout) contentView.findViewById(R.id.btn_joinin);
            mJoinin.setVisibility(View.GONE);
            final TextView mTxtCustom = (TextView) contentView.findViewById(R.id.txt_joinin);
           /* if(statusUtil.IsCustom()){
                mTxtCustom.setText("已加入定制");
            }else {
                mTxtCustom.setText("加入定制");
            }*/
            RelativeLayout mMyCart = (RelativeLayout) contentView.findViewById(R.id.btn_myCart);
            mMyCart.setVisibility(View.VISIBLE);;
            RelativeLayout mCancel = (RelativeLayout) contentView.findViewById(R.id.btn_cancel);
            mReport.setOnClickListener(new View.OnClickListener() {//举报
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    ViewSpotDetailActivity.this.startActivity(new Intent(ViewSpotDetailActivity.this, ReportActivity.class));
                }
            });
            mCollect.setOnClickListener(new View.OnClickListener() {//收藏
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    CollectUtil.CollectOrCancel(ViewSpotDetailActivity.this, docid, detailType,
                            statusUtil.IsFavorite(), new PraiseUtil.ResultCallback() {
                                @Override
                                public void callback(boolean isSuccess) {
                                    if (isSuccess) {
                                        if (statusUtil.IsFavorite()) {
                                            statusUtil.setIsFavorite(false);
                                            mTxtCollect.setText("收藏");
                                        } else {
                                            statusUtil.setIsFavorite(true);
                                            mTxtCollect.setText("已收藏");
                                        }
                                        Intent intent = new Intent();
                                        intent.setAction(LinesDetailImageTextActivity.EXTRA_ACTION);
                                        ViewSpotDetailActivity.this.sendBroadcast(intent);
                                    }
                                }
                            });
                }
            });

            mMyCart.setOnClickListener(new View.OnClickListener() {//我的定制
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    ViewSpotDetailActivity.this.startActivity(new Intent(ViewSpotDetailActivity.this, MystyleActivity.class));
                }
            });

            mCancel.setOnClickListener(new View.OnClickListener() {//取消
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
