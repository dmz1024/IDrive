package com.ccpress.izijia.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.*;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.mystyle.InfoActivity;
import com.ccpress.izijia.adapter.CommentAdapter;
import com.ccpress.izijia.adapter.SharedAdapter;
import com.ccpress.izijia.entity.InfoDetailEntity;
import com.ccpress.izijia.entity.InfoMediaEntity;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.*;
import com.ccpress.izijia.view.InsideGridView;
import com.ccpress.izijia.vo.MyAttentionVo;
import com.trs.app.TRSFragmentActivity;
import com.trs.util.ImageDownloader;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;
import com.trs.wcm.RemoteDataService;
import com.trs.wcm.callback.BaseDataAsynCallback;
import com.viewpagerindicator.CirclePageIndicator;
import de.hdodenhof.circleimageview.CircleImageView;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WLH on 2015/5/11 16:43.
 */
public class InfoDetailActivity extends TRSFragmentActivity{

    public static String EXTRA_DOCID = "docid";
    public static String EXTRA_DETAIL_TYPE = "detailtype";
    public static String EXTRA_URL = "url";

    private CircleImageView mImgAvatar;
    private TextView mTxtUserName;

    private ViewPager mPagerImg;
    private CirclePageIndicator pageIndicator;
    private TextView mTxtLocation;
    private TextView mTxtDate;
    private ImageView mImgNext;
    private TextView mTxtDescription;
    private View mLinearlocation;

    private ImageView mImgPraise;
    private TextView mTxtPraise;

    private RadioButton mRbComment;
    private RadioButton mRbShare;
    private RadioButton mRbPraise;

    private View mLoadingView;

    private ListView mList;
    private InsideGridView mGrid;
    private CommentAdapter commentAdapter;
    private SharedAdapter sharedAdapter;

    private InfoDetailEntity entity;
    private Dialog popDialog = null;


    private String URL = Constant.INTERACT_URL_BASE + Constant.INTERACT_DETAIL ;   //"raw://info_detail";

    private String docid;

    DetailStatusUtil statusUtil = new DetailStatusUtil(this);

    private String detailType = Constant.DETAIL_TYPE_IMAGES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_detail);

        docid = getIntent().getStringExtra(EXTRA_DOCID);
        detailType = getIntent().getStringExtra(EXTRA_DETAIL_TYPE);

        URL = URL + docid;

        init();

    }

    @Override
    protected void onResume() {
        super.onResume();

        initData();

    }

    /**
     * 布局控件设置
     */
    private void init() {
        ((TextView)findViewById(R.id.title)).setText("内容详情");

        mImgAvatar = (CircleImageView) findViewById(R.id.user_img);


            mImgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(InfoDetailActivity.this, InfoActivity.class);
                    intent.putExtra(InfoActivity.TUID,String.valueOf(entity.getUser().getUid()));
                    startActivity(intent);
                }
            });
        mTxtUserName = (TextView) findViewById(R.id.user_name);

        mPagerImg = (ViewPager) findViewById(R.id.pager);
        pageIndicator = (CirclePageIndicator) findViewById(R.id.pager_indicator);
        mPagerImg.setAdapter(mAdapter);
        int width = (int) (ScreenUtil.getScreenWidth(this)- 2 * getResources().getDimension(R.dimen.size8));
        mPagerImg.setLayoutParams(new RelativeLayout.LayoutParams(width, (int)width*2/3));
        pageIndicator.setViewPager(mPagerImg);

        mTxtLocation = (TextView) findViewById(R.id.txt_location);
        mLinearlocation = findViewById(R.id.Linear_location);
        mTxtDate  = (TextView) findViewById(R.id.txt_date);
        mImgNext = (ImageView) findViewById(R.id.img_next);
        mTxtDescription = (TextView) findViewById(R.id.description);

        mRbComment = (RadioButton) findViewById(R.id.info_comment);
        mRbShare = (RadioButton) findViewById(R.id.info_share);
        mRbPraise = (RadioButton) findViewById(R.id.info_praise);
        commentAdapter = new CommentAdapter(this, true);
        mList = (ListView) findViewById(R.id.listview);
        mList.setAdapter(commentAdapter);
        mGrid = (InsideGridView) findViewById(R.id.grid_view);
        sharedAdapter = new SharedAdapter(this);
        mGrid.setAdapter(sharedAdapter);

        mRbComment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    setAdapter(true, null);
                }
            }
        });
        mRbPraise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    setAdapter(false, "赞");
                }
            }
        });
        mRbShare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    setAdapter(false, "分享");
                }
            }
        });

        mLinearlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(entity!= null && !StringUtil.isEmpty(entity.getGeo())){
                    Intent intent = new Intent(InfoDetailActivity.this, InfoMapActivity.class);
                    intent.putExtra("geo", entity.getGeo());
                    startActivity(intent);
                }
            }
        });

        mLoadingView = findViewById(R.id.loading_view);

        mTxtPraise = (TextView) findViewById(R.id.txt_praise);
        mImgPraise = (ImageView) findViewById(R.id.icon_praise);
    }

    /**
     * 设置Adapter
     * @param isComment
     * @param txt
     */
    private void setAdapter(boolean isComment, String txt){
        commentAdapter = new CommentAdapter(this, isComment);
        if(entity!=null){
            if (isComment){
                mList.setVisibility(View.VISIBLE);
                mGrid.setVisibility(View.GONE);
                commentAdapter.setDatas(entity.getComments());
                commentAdapter.notifyDataSetChanged();
                mList.setAdapter(commentAdapter);
                Utility.setListViewHeightBasedOnChildren(mList);
            }else {
                mList.setVisibility(View.GONE);
                mGrid.setVisibility(View.VISIBLE);
                if(txt.equals("赞")){
                    sharedAdapter.setData(entity.getFavour());
                }else {
                    sharedAdapter.setData(entity.getShare());
                }
                sharedAdapter.notifyDataSetChanged();
                mGrid.setAdapter(sharedAdapter);
            }

        }
    }

    /**
     * 加载数据
     */
    private void initData() {
        if(entity == null){
            mLoadingView.setVisibility(View.VISIBLE);
        }
        RemoteDataService rd = new RemoteDataService(this);
        Log.e("WLH", "InfoDetailActivity URL:"+ URL);
        rd.loadJSON(URL, new BaseDataAsynCallback() {
            @Override
            public void onDataLoad(String _result) {
                parseData(_result);
                mLoadingView.setVisibility(View.GONE);
            }

            @Override
            public void onError(String url) {
                mLoadingView.setVisibility(View.GONE);
                Toast.makeText(InfoDetailActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取状态信息
     */
    private void initStatus() {
        statusUtil.getDetailStatus(docid, detailType, new BaseDataAsynCallback() {
            @Override
            public void onDataLoad(String result, boolean bIsChanged) {
                if(statusUtil.IsPraise()){
                    mTxtPraise.setText("已点赞");
                    mImgPraise.setImageResource(R.drawable.icon_praised);
                }
            }
        });
    }

    /**
     * 数据筛选
     * @param _result
     */
    private void parseData(String _result){
        try {
            JSONObject object = new JSONObject(_result);
            entity = new InfoDetailEntity(object);

            if(entity != null){
                if(entity.getUser() != null ){
                    new ImageDownloader.Builder()
                            .setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC)
                            .build(entity.getUser().getAvatar(), mImgAvatar)
                            .start();
                    mTxtUserName.setText(entity.getUser().getName());
                }
                mTxtLocation.setText(entity.getGeo());
                if(StringUtil.isEmpty(entity.getGeo())){
                    findViewById(R.id.img_location).setVisibility(View.GONE);
                }
                mTxtDate.setText(entity.getDate());
                mTxtDescription.setText(entity.getDesc());
                if(entity.getMedias()!= null && entity.getMedias().size() == 1){
                    pageIndicator.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
                commentAdapter.setDatas(entity.getComments());
                commentAdapter.notifyDataSetChanged();
                mList.setVisibility(View.VISIBLE);
                mGrid.setVisibility(View.GONE);
                Utility.setListViewHeightBasedOnChildren(mList);

                mRbComment.setText("评论 "+entity.getCommments_c());
                mRbShare.setText("分享 "+entity.getShare_c());
                mRbPraise.setText("赞 "+entity.getFavour_c());
                mImgAvatar.setFocusable(true);
                mImgAvatar.setFocusableInTouchMode(true);
                mImgAvatar.requestFocus();
                initStatus();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分享
     * @param view
     */
    public void OnBtnShareClick(View view){
        String title = (entity==null) ? null : entity.getTitle();
        String imageURL = (entity!=null && entity.getMedias()!= null && entity.getMedias().size()>0) ? entity.getMedias().get(0).getImage() : null;
        String content = (entity==null) ? null : entity.getDesc();
        String url = (entity==null) ? null :getIntent().getStringExtra(EXTRA_URL);

        ShareUtil.showShare(this, docid, detailType, title, imageURL, url, content);
    }

    /**
     * 点赞
     * @param view
     */
    public void OnBtnPraiseClick(View view){//点赞
        PraiseUtil.PraiseOrCancel(this, docid, detailType, statusUtil.IsPraise(),
                new PraiseUtil.ResultCallback() {
                    @Override
                    public void callback(boolean isSuccess) {
                        if(isSuccess){
                            if(statusUtil.IsPraise()){
                                statusUtil.setIsPraise(false);
                                mTxtPraise.setText("点赞");
                                mImgPraise.setImageResource(R.drawable.bottom_praise);
                                initData();

                            }else {
                                statusUtil.setIsPraise(true);
                                mTxtPraise.setText("已点赞");
                                mImgPraise.setImageResource(R.drawable.icon_praised);
                                initData();
                            }
                        }

                    }
                });
    }

    /**
     * 评论
     * @param view
     */
    public void OnBtnCommentClick(View view){//评论
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra(EXTRA_DOCID, docid);
        intent.putExtra(CommentActivity.EXTRA_TYPE, detailType);
        //startActivityForResult(intent, 1);
        startActivity(intent);
    }

    public void OnBtnMoreClick(View view){//更多
        showDialog();
    }

    /**
     * 底部的showDialog
     */
    private void showDialog() {
        popDialog = null;
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
            mCollect.setVisibility(contentView.GONE);
            final TextView mTxtCollect = (TextView) contentView.findViewById(R.id.txt_collect);
            if(statusUtil.IsFavorite()){
                mTxtCollect.setText("已收藏");
            }else {
                mTxtCollect.setText("收藏");
            }
            RelativeLayout mJoinin = (RelativeLayout) contentView.findViewById(R.id.btn_joinin);
           TextView mTxtJoinin = (TextView) contentView.findViewById(R.id.txt_joinin);
            mTxtJoinin.setText("保存图片");
            RelativeLayout mCancel = (RelativeLayout) contentView.findViewById(R.id.btn_cancel);
            mReport.setOnClickListener(new View.OnClickListener() {//举报
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    Intent intent = new Intent(InfoDetailActivity.this, ReportActivity.class);
                    intent.putExtra(EXTRA_DOCID, docid);
                    intent.putExtra(CommentActivity.EXTRA_TYPE, detailType);
                    InfoDetailActivity.this.startActivity(intent);
                }
            });

            mCollect.setOnClickListener(new View.OnClickListener() {//收藏
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    CollectUtil.CollectOrCancel(InfoDetailActivity.this, docid, detailType,
                            statusUtil.IsFavorite(), new PraiseUtil.ResultCallback() {
                                @Override
                                public void callback(boolean isSuccess) {
                                    Log.e("WLH","CollectOrCancel isSuccess:"+isSuccess);
                                    if(isSuccess){
                                        if(statusUtil.IsFavorite()){
                                            statusUtil.setIsFavorite(false);
                                            mTxtCollect.setText("收藏");
                                        }else {
                                            statusUtil.setIsFavorite(true);
                                            mTxtCollect.setText("已收藏");
                                        }
                                    }
                                }
                            });
                }
            });

            mJoinin.setOnClickListener(new View.OnClickListener() {//保存图片
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    if(entity != null && entity.getMedias()!= null && entity.getMedias().get(mPagerImg.getCurrentItem()) != null){
                        SaveImageUtil.saveImage(InfoDetailActivity.this, entity.getMedias().get(mPagerImg.getCurrentItem()).getImage());
                    }
                }
            });

            mCancel.setOnClickListener(new View.OnClickListener() {//取消
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                }
            });
        }
        else {
            if(popDialog.isShowing()){
                popDialog.dismiss();
                popDialog = null;
                return;
            }
        }
        popDialog.show();
    }



    private PagerAdapter mAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            if(entity!= null && entity.getMedias()!= null){
                return entity.getMedias().size();
            }
            return 0;

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            RelativeLayout rl = new RelativeLayout(InfoDetailActivity.this);
            rl.setGravity(Gravity.CENTER);

            ImageView imgView = new ImageView(InfoDetailActivity.this);
            imgView.setLayoutParams(new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            imgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            imgView.setBackgroundResource(com.trs.mobile.R.drawable.cqsw_default_pic);

            ImageView play = new ImageView(InfoDetailActivity.this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            play.setLayoutParams(lp);
            play.setImageResource(R.drawable.icon_vedio_play);

            rl.addView(imgView);

            boolean isVideo = false;

            if(entity!= null && entity.getMedias()!= null) {
                final InfoMediaEntity topic = entity.getMedias().get(position);
                if(topic.getIsvideo() == 1){
                    isVideo = true;
                    rl.addView(play);
                }

                new ImageDownloader.Builder().setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).
                        build(topic.getImage(), imgView).start();
                if(isVideo){
                    imgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(InfoDetailActivity.this, PlayVideoActivity.class);
                            intent.putExtra(PlayVideoActivity.EXTRA_URL, topic.getSrcurl());
                            InfoDetailActivity.this.startActivity(intent);
                        }
                    });
                }

            }
            container.addView(rl);
            return rl;
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
}
