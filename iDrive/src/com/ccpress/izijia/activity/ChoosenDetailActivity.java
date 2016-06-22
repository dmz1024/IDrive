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
import com.ccpress.izijia.adapter.ApplyAdapter;
import com.ccpress.izijia.adapter.ExpandableAdapter;
import com.ccpress.izijia.adapter.MomentsAdapter;
import com.ccpress.izijia.entity.CareChooseEntity;
import com.ccpress.izijia.entity.ViewspotDetailEntity;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.DialogUtil;
import com.ccpress.izijia.util.ScreenUtil;
import com.ccpress.izijia.util.ShareUtil;
import com.ccpress.izijia.view.InsideExpandableListView;
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
 * Created by WLH on 2015/6/17 12:02.
 * 精选、自驾团界面
 */
public class ChoosenDetailActivity extends TRSFragmentActivity {

    public static final String EXTRA_TYPE = "type";
    private int type = 0;//0：精选 1：自驾团


    private TextView mTitle;
    private TextView mTxtName;
    private ImageView btn_share;
    private ViewPager mPager;
    private CirclePageIndicator indicator;
    private TextView mTxt001;
    private TextView mTxt01;
    private ImageView mImg01;
    private LinearLayout mLinear02;
    private TextView mTxt02;
    private TextView mTxt003;
    private TextView mTxt03;
    private ImageView mImg03;

    private View mHotline;
    private TextView mTxtHotline;
    private View mLayoutMoment;
    private ImageView mImgMomentDown;
    private ListView mListMoment;
    private MomentsAdapter mMomentAdapter;

    private View mChooseBottom;
    private ImageView mImgAct;
    private TextView mTxtActcontent;
    private View mLayoutApply;
    private ImageView mImgApplyDown;
    private ListView mListApply;
    private ApplyAdapter mApplayAdapter;
    private TextView mTxtStatus;

    private View mSelfDriveBottom;
    private RadioButton mRdLinesIntro;
    private RadioButton mRdTrip;
    private RadioButton mRdGuide;
    private TextView mTextLinesIntro;
    private View mLayoutGuider;
    private CircleImageView mGuiderAvatar;
    private TextView mTextGuiderName;
    private TextView mTextGuiderYear;
    private TextView mTextGuiderExp;
    private TextView mTextGuiderProfile;
    private InsideExpandableListView mExpandableListView;
    private ExpandableAdapter mExpandAdapter;

    private View mLoadingView;
    private String url = Constant.IDRIVE_URL_BASE + Constant.CareChoose_Detail_Url;

    private CareChooseEntity entity;

    private  Dialog popDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosen_detail);
        type = getIntent().getIntExtra(EXTRA_TYPE, 0);

        initView();
        initData();
    }

    /**
     * 布局控件设置
     */
    private void initView() {
        mLoadingView = findViewById(R.id.loading_view);
        mTitle = (TextView) findViewById(R.id.title);
        mTxtName = (TextView) findViewById(R.id.img_title);
        mPager = (ViewPager) findViewById(R.id.pager);
        indicator = (CirclePageIndicator) findViewById(R.id.pager_indicator);
        mPager.setAdapter(mPageAdapter);
        indicator.setViewPager(mPager);
        btn_share=(ImageView)  findViewById(R.id.btn_share);
        findViewById(R.id.txt_joinin).setVisibility(View.GONE);
        btn_share.setVisibility(View.VISIBLE);
        mTxt001 = (TextView) findViewById(R.id.text001);
        mTxt01 = (TextView) findViewById(R.id.text01);
        mImg01 = (ImageView) findViewById(R.id.ic_01);
        mLinear02 = (LinearLayout) findViewById(R.id.lineat02);
        mTxt02 = (TextView) findViewById(R.id.text02);
        mTxt003 = (TextView) findViewById(R.id.text003);
        mTxt03 = (TextView) findViewById(R.id.text03);
        mImg03 = (ImageView) findViewById(R.id.ic_03);

        mHotline = findViewById(R.id.layout_hotline);
        mTxtHotline = (TextView) findViewById(R.id.hotline);
        mLayoutMoment = findViewById(R.id.layout_moment);
        mImgMomentDown= (ImageView) findViewById(R.id.ic_momment_detail);
        mListMoment = (ListView) findViewById(R.id.listview_momment);
        mMomentAdapter = new MomentsAdapter(this);
        mListMoment.setAdapter(mMomentAdapter);
        mLayoutMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListMoment.isShown()){
                    mListMoment.setVisibility(View.GONE);
                    mImgMomentDown.setImageResource(R.drawable.icon_arrow_down);
                }else {
                    mListMoment.setVisibility(View.VISIBLE);
                    mListMoment.setAdapter(mMomentAdapter);
                    mImgMomentDown.setImageResource(R.drawable.icon_up);
                }
            }
        });

        mChooseBottom = findViewById(R.id.layout_choose_bottom);
        mImgAct = (ImageView) findViewById(R.id.act_img);
        mTxtActcontent = (TextView) findViewById(R.id.act_content);
        mLayoutApply = findViewById(R.id.layout_apply);
        mImgApplyDown = (ImageView) findViewById(R.id.ic_apply_detail);
        mListApply = (ListView) findViewById(R.id.listview_apply);
        mTxtStatus = (TextView) findViewById(R.id.act_status);
        mApplayAdapter = new ApplyAdapter(this);
        mListApply.setAdapter(mApplayAdapter);
        mLayoutApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListApply.isShown()){
                    mListApply.setVisibility(View.GONE);
                    mImgApplyDown.setImageResource(R.drawable.icon_arrow_down);
                }else {
                    mListApply.setVisibility(View.VISIBLE);
                    mListApply.setAdapter(mApplayAdapter);
                    mImgApplyDown.setImageResource(R.drawable.icon_up);
                }
            }
        });


        mSelfDriveBottom = findViewById(R.id.layout_selfdrive_group);
        mRdLinesIntro = (RadioButton)findViewById(R.id.line_intro);
        mRdTrip = (RadioButton)findViewById(R.id.line_trip);
        mRdGuide = (RadioButton)findViewById(R.id.line_guide);
        mRdLinesIntro.setOnCheckedChangeListener(mCheckChangeListener);
        mRdTrip.setOnCheckedChangeListener(mCheckChangeListener);
        mRdGuide.setOnCheckedChangeListener(mCheckChangeListener);
        mTextLinesIntro = (TextView) findViewById(R.id.line_description);
        mLayoutGuider = findViewById(R.id.layout_guider);
        mGuiderAvatar = (CircleImageView) findViewById(R.id.guide_avatar);
        mTextGuiderName = (TextView) findViewById(R.id.guider_name);
        mTextGuiderYear = (TextView) findViewById(R.id.guider_year);
        mTextGuiderExp = (TextView) findViewById(R.id.guider_experience);
        mTextGuiderProfile = (TextView) findViewById(R.id.guider_profile);
        mExpandableListView = (InsideExpandableListView) findViewById(R.id.expandableListView);
        mExpandAdapter = new ExpandableAdapter(this, 0);
        mExpandableListView.setAdapter(mExpandAdapter);

        if(type ==0 ){
            mTitle.setText("活动");

        }else if(type == 1){
            mTitle.setText("自驾团");
            mLinear02.setVisibility(View.GONE);
            mTxt001.setText("价格：");
            mImg01.setImageResource(R.drawable.icon_price);
            mTxt003.setText("出发时间：");
            mImg03.setImageResource(R.drawable.icon_time);
            mHotline.setVisibility(View.GONE);
            mChooseBottom.setVisibility(View.GONE);
            mSelfDriveBottom.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 按钮点击监听
     */
    private RadioButton.OnCheckedChangeListener mCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b){
                switch (compoundButton.getId()){
                    case R.id.line_intro:
                        mLayoutGuider.setVisibility(View.GONE);
                        mTextLinesIntro.setVisibility(View.VISIBLE);
                        if(entity != null){
                            mTextLinesIntro.setText(entity.getIntro());
                        }
                        break;
                    case R.id.line_trip:
                        mLayoutGuider.setVisibility(View.GONE);
                        mTextLinesIntro.setVisibility(View.VISIBLE);
                        if(entity != null){
                            mTextLinesIntro.setText(entity.getTrip());
                        }
                        break;
                    case R.id.line_guide:
                        mLayoutGuider.setVisibility(View.VISIBLE);
                        mTextLinesIntro.setVisibility(View.GONE);
                        break;
                }
            }
        }
    };

    /**
     *加载、填充数据
     */
    private void initData() {


        if(type==1){
            url= Constant.IDRIVE_URL_BASE + Constant.SelfDrive_Detail_Url;
        }
        url+=getIntent().getStringExtra(LinesDetailImageTextActivity.EXTRA_LID);

        mLoadingView.setVisibility(View.VISIBLE);
        RemoteDataService rd = new RemoteDataService(this);
        rd.loadJSON(url, new BaseDataAsynCallback() {
            @Override
            public void onDataLoad(String _result) {
                mLoadingView.setVisibility(View.GONE);
                if(!StringUtil.isEmpty(_result)){
                    parseData(_result);
                    return;
                }
                Toast.makeText(ChoosenDetailActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String url) {
                mLoadingView.setVisibility(View.GONE);
                Toast.makeText(ChoosenDetailActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private PagerAdapter mPageAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            if(entity != null && entity.getImages() != null){
                return entity.getImages().size();
            }
            return 0;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imgView = new ImageView(ChoosenDetailActivity.this);
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
     * 筛选数据
     * @param result
     */
    private void parseData(String result) {
        JSONObject object;
        try {
            object = new JSONObject(result);
            entity = new CareChooseEntity(object);
            if(entity != null){
                if(entity.getImages()!= null){

                    if(entity.getImages().size() < 2){
                        indicator.setVisibility(View.GONE);
                    }else {
                        indicator.setVisibility(View.VISIBLE);
                    }
                    mPageAdapter.notifyDataSetChanged();
                }
                mTxtName.setText(entity.getName());
                if(entity.getMoments() == null ||  (entity.getMoments() != null&& entity.getMoments().size() <= 0)){
                    mLayoutMoment.setVisibility(View.GONE);
                }
                mMomentAdapter.setMoments(entity.getMoments());
                int width = (int) (ScreenUtil.getScreenWidth(this)- 2 * getResources().getDimension(R.dimen.size8));
                mPager.setLayoutParams(new RelativeLayout.LayoutParams(width, (int)width*2/3));

                if(type ==0){
                    mTxt01.setText(entity.getAddr());
                    mTxt02.setText(entity.getTime());
                    mTxt03.setText(entity.getOrganizer());
                    mTxtHotline.setText(entity.getHotline());
                    if(entity.getContent() != null){
                        if (!StringUtil.isEmpty(entity.getContent().getImage())){
                            new ImageDownloader.Builder().setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).
                                    build(entity.getContent().getImage(), mImgAct).start();

                            mImgAct.setLayoutParams(new LinearLayout.LayoutParams(width, (int)width*2/3));
                        }
                        mTxtActcontent.setText(entity.getContent().getDesc());
                    }
                    if(entity.getStatus() == 1){
                        mTxtStatus.setText("火热报名中");
                    }else {
                        mTxtStatus.setText("活动已结束");
                    }
                    mApplayAdapter.setData(entity.getApply());
                }else {
                    mTxt01.setText(entity.getPrice());
                    mTxt03.setText(entity.getTime());
                    mTxt001.setVisibility(View.GONE);
                    mTxt003.setVisibility(View.GONE);
                    mExpandAdapter.setGroupLines(entity.getExtras());
                    mTextLinesIntro.setText(entity.getIntro());
                    if(entity.getGuide() != null){
//                        new ImageDownloader.Builder().setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).
//                                build(entity.getGuide().getAvatar(), mGuiderAvatar).start();
                        mGuiderAvatar.setVisibility(View.GONE);
                        mTextGuiderName.setText(entity.getGuide().getName());
                        mTextGuiderYear.setVisibility(View.GONE);
                        //mTextGuiderYear.setText("带队时间："+entity.getGuide().getYear()+"年");
                        mTextGuiderExp.setText(entity.getGuide().getExperience());
                        mTextGuiderProfile.setText(entity.getGuide().getProfile());
                    }
                }
                mPager.setFocusable(true);
                mPager.setFocusableInTouchMode(true);
                mPager.requestFocus();


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static String web_url=null;
    public static String web_image=null;
    public static String web_tile=null;
    public  static String web_id=null;

    /**
     * 分享
     * @param view
     */
    public void OnBtnShareClick(View view){
        if (type==1){
            ShareUtil.showShare(this,web_id,"16",web_tile,web_image,web_url,null);
        }

    }

    /**
     * 点赞
     * @param view
     */
    public void OnBtnPraiseClick(View view){
        DialogUtil.showResultDialog(this, "点赞成功", R.drawable.icon_success);
    }
    public void OnBtnCommentClick(View view){
        startActivity(new Intent(this, CommentActivity.class));
    }

    /**
     * 底部更多
     * @param view
     */
    public void OnBtnMoreClick(View view){
        showDialog();
    }

    /**
     * 底部按钮
     */
    private void showDialog() {

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

            RelativeLayout mCancel = (RelativeLayout) contentView.findViewById(R.id.btn_cancel);
            mReport.setOnClickListener(new View.OnClickListener() {//举报
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    popDialog = null;
                    ChoosenDetailActivity.this.startActivity(new Intent(ChoosenDetailActivity.this, ReportActivity.class));
                }
            });
            mCollect.setOnClickListener(new View.OnClickListener() {//收藏
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    popDialog = null;
                }
            });

            mCancel.setOnClickListener(new View.OnClickListener() {//取消
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    popDialog = null;

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
