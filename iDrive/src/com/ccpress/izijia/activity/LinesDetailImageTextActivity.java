package com.ccpress.izijia.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.mystyle.MystyleActivity;
import com.ccpress.izijia.adapter.ExpandableAdapter;
import com.ccpress.izijia.adapter.ExtendLinesAdapter;
import com.ccpress.izijia.adapter.ImageTextAdapter;
import com.ccpress.izijia.adapter.PassCityAdapter;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.entity.LinesDetailUploadEntity;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.*;
import com.ccpress.izijia.view.InsideExpandableListView;
import com.ccpress.izijia.view.InsideListView;
import com.ccpress.izijia.view.WheelView;
import com.froyo.commonjar.utils.SpUtil;
import com.trs.app.TRSFragmentActivity;
import com.trs.util.ImageDownloader;
import com.trs.util.StringUtil;
import com.trs.wcm.RemoteDataService;
import com.trs.wcm.callback.BaseDataAsynCallback;
import de.hdodenhof.circleimageview.CircleImageView;
import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by WLH on 2015/5/19 14:01.
 */
public class LinesDetailImageTextActivity extends TRSFragmentActivity{

    public static String EXTRA_ENTITY = "entity";
    public static String EXTRA_LID = "lid";
    public static int MY_TYPE=0;
    private int type = 0;
    private String lid;

    private CircleImageView mImgAvatar;
    private TextView mTxtUserName;
    private View mUserView;

    private TextView mTxtTitle;
    private TextView mTxtLine;
    private ImageView mImgTop;
    private TextView mTxtDesc;


    private LinearLayout mLinear_TravelNotes;

    private String detailType = Constant.DETAIL_TYPE_LINE_UPLOAD;
    private LinearLayout mLinear_category;
    private TextView mTxt_category;
    private InsideListView mLisView;

    private ImageTextAdapter adapter;
    private ExtendLinesAdapter adapter_ExtendLines;
    private PassCityAdapter adapter_passcity;


    private LinearLayout mLinear_TravelNotesList;

    private View mLinesMiddleView;
    private View mDesMiddleView;
    private View mLinearChoose;

    private View mLoadingView;

    private LinesDetailUploadEntity entity;

    private LinesDetailUploadEntity schedule_Entity = null;

    private Dialog popDialog = null;//底部更多弹出的对话框
    private Dialog popChooseDialog = null;//选择行程或看点弹出的对话框
    private LinesDetailUploadEntity.Trip currentTrip;

    private RadioGroup mRG1;
    private RadioGroup mRG2;
    private RadioButton mRD1;
    private RadioButton mRD2;
    private RadioButton mRD3;
    private RadioButton mRD4;
    private RadioButton mRD5;
    private RadioButton mRD6;
    private RadioButton mRD7;
    private RadioButton mRD8;

    private ImageView mImgPraise;
    private TextView mTxtPraise;

    private View mLayoutDescList;
    private InsideExpandableListView mExpandableListView;
    private ExpandableAdapter mExpandableAdapter;

//    private String Schedule_Url = "raw://schedule_detail";
//    private String Destination_ViewPort_URL = "raw://destination_viewport";
    private ArrayList<LinesDetailUploadEntity.ViewSpot> DesViewSpotList = new ArrayList<LinesDetailUploadEntity.ViewSpot>();

    private int scrollOffset = 0;
    private ScrollView mScrollView;
    private LinearLayout Linear_summary;

    DetailStatusUtil statusUtil = new DetailStatusUtil(this);

    private View mReport;
    private View mMore;


    public static final String EXTRA_ACTION = "LinesDetailImageTextActivity.changestatus";
    private BroadcastReceiver changeStatusBroadcastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.e("WLH", "Action:"+intent.getAction());
            if(intent!= null &&  EXTRA_ACTION.equals(intent.getAction())){
                loadData();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lines_upload_img_text);
        lid = getIntent().getStringExtra(EXTRA_LID);
        initReceiver();
        init();
        initRadioButton();
        initData();
        initStatus();

    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(EXTRA_ACTION);
        registerReceiver(changeStatusBroadcastReceiver, filter);
    }

    /**
     * 获取“点赞”de状态
     * */
    private void initStatus() {
        statusUtil.getDetailStatus(lid, detailType, new BaseDataAsynCallback() {
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
     * 页面布局控件设置
     */
    private void init(){
        type = getIntent().getIntExtra(LinesDetailUserUploadActivity.EXTRA_ACT_TYPE, 0);
        mTxtPraise = (TextView) findViewById(R.id.txt_praise);
        mImgPraise = (ImageView) findViewById(R.id.icon_praise);
        mReport = findViewById(R.id.linear_report);
        mMore = findViewById(R.id.linear_more);

        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        Linear_summary = (LinearLayout) findViewById(R.id.Linear_summary);

        mImgAvatar = (CircleImageView) findViewById(R.id.user_img);
        mTxtUserName = (TextView) findViewById(R.id.user_name);
        mUserView = findViewById(R.id.Linear_user);

        mTxtTitle = (TextView) findViewById(R.id.title);
        mTxtLine = (TextView) findViewById(R.id.line);
        mImgTop = (ImageView) findViewById(R.id.img_top);
        mTxtDesc = (TextView) findViewById(R.id.desc_top);

        mLinear_TravelNotes = (LinearLayout) findViewById(R.id.Linear_travelnote);
        mLinear_category = (LinearLayout) findViewById(R.id.Linear_category);
        mLisView = (InsideListView) findViewById(R.id.listview);
        adapter = new ImageTextAdapter(this);
        adapter_ExtendLines = new ExtendLinesAdapter(this);
        adapter_passcity = new PassCityAdapter(this);
        mLisView.setAdapter(adapter);
        mLinear_TravelNotesList = (LinearLayout) findViewById(R.id.Linear_travelnote_items);

        mLoadingView = findViewById(R.id.loading_view);
        mLinesMiddleView = findViewById(R.id.lines_middle);
        mDesMiddleView = findViewById(R.id.des_middle);

        mLayoutDescList = findViewById(R.id.mLayoutDescList);
        mExpandableListView = (InsideExpandableListView) findViewById(R.id.expandableListView);
        mLoadingView = findViewById(R.id.loading_view);

        if(type == LinesDetailUserUploadActivity.OfficialLines){
            detailType = Constant.DETAIL_TYPE_LINE;
            mLinearChoose = findViewById(R.id.lines_choose_viewspot);
            mTxt_category = (TextView) findViewById(R.id.txt_category);
            mLinearChoose.setOnClickListener(mChooseClickListener);
            mLinesMiddleView.setVisibility(View.VISIBLE);
            mLinear_category.setVisibility(View.VISIBLE);
            mLinearChoose.setVisibility(View.VISIBLE);
            mExpandableAdapter = new ExpandableAdapter(this, 0);
            mExpandableListView.setAdapter(mExpandableAdapter);
        }else if(type == LinesDetailUserUploadActivity.Destination){
            mMore.setVisibility(View.GONE);

            detailType = Constant.DETAIL_TYPE_DES;
            mLinearChoose = findViewById(R.id.lines_choose_localpoint);
            mTxt_category = (TextView) findViewById(R.id.txt_category1);
            mLinearChoose.setOnClickListener(mChooseClickListener);
            mDesMiddleView.setVisibility(View.VISIBLE);
            mLinear_category.setVisibility(View.VISIBLE);
            mLinearChoose.setVisibility(View.VISIBLE);
            mExpandableAdapter = new ExpandableAdapter(this, 0);
            mExpandableListView.setAdapter(mExpandableAdapter);
            mReport.setVisibility(View.VISIBLE);

        }else{
            mLinear_category.setVisibility(View.VISIBLE);
            mLinearChoose = findViewById(R.id.lines_choose_localpoint);
            mTxt_category = (TextView) findViewById(R.id.txt_category1);
            mLinearChoose.setVisibility(View.VISIBLE);
        }
    }

    /**
     * RadioButton的布局设置
     */
    private void initRadioButton(){
        if(type != LinesDetailUserUploadActivity.UserUpload){
            if(type == LinesDetailUserUploadActivity.OfficialLines){
                mRG1 = (RadioGroup) findViewById(R.id.rg1);
                mRG2 = (RadioGroup) findViewById(R.id.rg2);
                mRD1 = (RadioButton) findViewById(R.id.rdbutton1);
                mRD2 = (RadioButton) findViewById(R.id.rdbutton2);
                mRD3 = (RadioButton) findViewById(R.id.rdbutton3);
                mRD4 = (RadioButton) findViewById(R.id.rdbutton4);
                mRD5 = (RadioButton) findViewById(R.id.rdbutton5);
                mRD6 = (RadioButton) findViewById(R.id.rdbutton6);
                mRD7 = (RadioButton) findViewById(R.id.rdbutton7);
                mRD8 = (RadioButton) findViewById(R.id.rdbutton8);
            }else if(type ==LinesDetailUserUploadActivity.Destination ){
                mRG1 = (RadioGroup) findViewById(R.id.rg11);
                mRG2 = (RadioGroup) findViewById(R.id.rg12);
                mRD1 = (RadioButton) findViewById(R.id.rdbutton11);
                mRD2 = (RadioButton) findViewById(R.id.rdbutton12);
                mRD3 = (RadioButton) findViewById(R.id.rdbutton13);
                mRD4 = (RadioButton) findViewById(R.id.rdbutton14);
                mRD5 = (RadioButton) findViewById(R.id.rdbutton15);
                mRD6 = (RadioButton) findViewById(R.id.rdbutton16);
                mRD7 = (RadioButton) findViewById(R.id.rdbutton17);
                mRD8 = (RadioButton) findViewById(R.id.rdbutton18);
            }

            mRD1.setOnCheckedChangeListener(mCheckChangeListener);
            mRD2.setOnCheckedChangeListener(mCheckChangeListener);
            mRD3.setOnCheckedChangeListener(mCheckChangeListener);
            mRD4.setOnCheckedChangeListener(mCheckChangeListener);
            mRD5.setOnCheckedChangeListener(mCheckChangeListener);
            mRD6.setOnCheckedChangeListener(mCheckChangeListener);
            mRD7.setOnCheckedChangeListener(mCheckChangeListener);
            mRD8.setOnCheckedChangeListener(mCheckChangeListener);
        }
        
    }

    private View.OnClickListener mChooseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showChooseDialog();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        scrollOffset = Linear_summary.getHeight()+ mExpandableListView.getHeight();
        Log.e("WLH","scrollOffset:"+scrollOffset);
    }

    /**
     * RadioButton的点击事件
     */
    private RadioButton.OnCheckedChangeListener mCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b){
                switch (compoundButton.getId()){
                    case R.id.rdbutton11:
                    case R.id.rdbutton1:
                        mRG2.clearCheck();
                        if(type == LinesDetailUserUploadActivity.OfficialLines){//行程看点
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            if(schedule_Entity != null){
                                setImgTxtAdapter(ImageTextAdapter.Positive, true, false,false, schedule_Entity.getViewspot(), null, null);
                            }

                        }else if(type == LinesDetailUserUploadActivity.Destination){//目的地详情--当地看点
                            //showCategoryTop(true);
                            mLinearChoose.setVisibility(View.VISIBLE);
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            if(DesViewSpotList!= null){
                                setImgTxtAdapter(ImageTextAdapter.Positive, true,false, true,DesViewSpotList,null, null);
                            }
                        }
                        break;
                    case R.id.rdbutton12:
                    case R.id.rdbutton2:
                        mRG2.clearCheck();
                        if(type == LinesDetailUserUploadActivity.OfficialLines){//玩法解析
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            if(schedule_Entity != null){
                                setImgTxtAdapter(ImageTextAdapter.Negative, false,false,false, null, schedule_Entity.getPlayrule(),null);
                            }

                        }else if(type == LinesDetailUserUploadActivity.Destination){//目的地详情--当地美食
                            //showCategoryTop(false);
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            mLinearChoose.setVisibility(View.GONE);
                            if(entity != null){
                                setImgTxtAdapter(ImageTextAdapter.Negative, false,false,false, null, entity.getFood(),null);
                            }
                        }
                        break;
                    case R.id.rdbutton13:
                    case R.id.rdbutton3:
                        mRG2.clearCheck();
                        if(type == LinesDetailUserUploadActivity.OfficialLines){//沿途美食
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            if(schedule_Entity != null){
                                setImgTxtAdapter(ImageTextAdapter.Negative, false,false,false, null, schedule_Entity.getFood(), null);
                            }
                        }else if(type == LinesDetailUserUploadActivity.Destination){//目的地详情--当地特产
                            //showCategoryTop(false);
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            mLinearChoose.setVisibility(View.GONE);
                            if(entity != null){
                                setImgTxtAdapter(ImageTextAdapter.Negative, false,false,false, null, entity.getSpecial(),null);
                            }
                        }
                        break;
                    case R.id.rdbutton14:
                    case R.id.rdbutton4:

                        if(type == LinesDetailUserUploadActivity.OfficialLines){//旅馆
                            mRG2.clearCheck();
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            if(schedule_Entity != null){
                                setImgTxtAdapter(ImageTextAdapter.Positive, false, true,false, null, null, schedule_Entity.getHotel());
                            }

                        }else if(type == LinesDetailUserUploadActivity.Destination){//目的地详情--娱乐
                            mRG1.clearCheck();
                            //showCategoryTop(false);
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            mLinearChoose.setVisibility(View.GONE);
                            if(entity != null){
                                setImgTxtAdapter(ImageTextAdapter.Negative, false,false,false, null, entity.getYule(),null);
                            }
                        }
                        break;
                    case R.id.rdbutton15:
                    case R.id.rdbutton5:
                        mRG1.clearCheck();
                        if(type == LinesDetailUserUploadActivity.OfficialLines){//餐厅
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            if(schedule_Entity != null){
                                setImgTxtAdapter(ImageTextAdapter.Negative, false, false,false, null, schedule_Entity.getShopping(), null);
                            }
                        }else if(type == LinesDetailUserUploadActivity.Destination){//目的地详情--购物
                            //showCategoryTop(false);
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            if(entity != null){
                                setImgTxtAdapter(ImageTextAdapter.Negative, false,false,false, null, entity.getShopping(),null);
                            }
                        }
                        break;
                    case R.id.rdbutton16:
                    case R.id.rdbutton6:
                        mRG1.clearCheck();
                        if(type == LinesDetailUserUploadActivity.OfficialLines){//停车发呆地
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            if(schedule_Entity != null){
                                setImgTxtAdapter(ImageTextAdapter.Positive, false,false,false, null, schedule_Entity.getParking(), null);
                            }

                        }else if(type == LinesDetailUserUploadActivity.Destination){//目的地详情--延伸线路
                            //showCategoryTop(false);
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            mLinearChoose.setVisibility(View.GONE);
                            if(entity!= null){
                                setAdapter_ExtendLines();
                            }
                        }
                        break;
                    case R.id.rdbutton17:
                    case R.id.rdbutton7:
                        mRG1.clearCheck();
                        if(type == LinesDetailUserUploadActivity.OfficialLines){//途径目的地
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            if(schedule_Entity!= null){
                                setPasscityAdapter();
                            }
                        }else if(type == LinesDetailUserUploadActivity.Destination){//目的地详情--文化活动
                            //showCategoryTop(false);
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            mLinearChoose.setVisibility(View.GONE);
                            if(entity != null){
                                setImgTxtAdapter(ImageTextAdapter.Negative, false, false,false, null, entity.getCulture(),null);
                            }
                        }
                        break;
                    case R.id.rdbutton18:
                    case R.id.rdbutton8:

                        if(type == LinesDetailUserUploadActivity.OfficialLines){//延伸线路
                            mRG1.clearCheck();
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            if(schedule_Entity!= null){
                                setAdapter_ExtendLines();
                            }
                        }else if(type == LinesDetailUserUploadActivity.Destination){//目的地详情--停车发呆地
                            mRG2.clearCheck();
                            //showCategoryTop(false);
                            mLinearChoose.setVisibility(View.GONE);
                            mScrollView.scrollTo(0, Linear_summary.getHeight()+ mExpandableListView.getHeight());
                            if(entity != null){
                                setImgTxtAdapter(ImageTextAdapter.Positive, false,false,true, null, entity.getParking(), null);
                            }
                        }
                        break;
                }
            }
        }
    };

    private void showCategoryTop(boolean isShow){
        if(isShow){
            mTxt_category.setVisibility(View.VISIBLE);
            findViewById(R.id.line_category).setVisibility(View.VISIBLE);
        }else {
            mTxt_category.setVisibility(View.GONE);
            findViewById(R.id.line_category).setVisibility(View.GONE);
        }
    }

    /**
     * 填充数据
     */
    private void initData() {

        entity = (LinesDetailUploadEntity) getIntent().getSerializableExtra(EXTRA_ENTITY);
        if(entity != null){
            if(entity.getSummary()!= null){
                mTxtTitle.setText(entity.getSummary().getTitle());
                mTxtLine.setText(entity.getSummary().getLine());
                if(!StringUtil.isEmpty(entity.getSummary().getImage())){
                    new ImageDownloader.Builder()
                            .setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC)
                            .build(entity.getSummary().getImage(), mImgTop)
                            .start();
                    int width = (int) (ScreenUtil.getScreenWidth(this)- 2 * getResources().getDimension(R.dimen.size8));
                    mImgTop.setLayoutParams(new RelativeLayout.LayoutParams(width, (int)width*2/3));
                }
                if (entity.getSummary().getDesc()==null||entity.getSummary().getDesc().equals("null")){
                    mTxtDesc.setVisibility(View.GONE);
                }else {
                    mTxtDesc.setText(entity.getSummary().getDesc());
                }

            }

            if(type != LinesDetailUserUploadActivity.UserUpload && entity.getTrip() != null && entity.getTrip().size() > 0){
                mTxt_category.setText(entity.getTrip().get(0).getTripname());

            }

            if(entity.getUser() != null ){
                new ImageDownloader.Builder()
                        .setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC)
                        .build(entity.getUser().getAvatar(), mImgAvatar)
                        .start();
                mTxtUserName.setText(entity.getUser().getName());
            }else {
                mUserView.setVisibility(View.GONE);
            }

            if (entity.getTravel() != null&&entity.getTravel().size() > 0){
                mLinear_TravelNotes.setVisibility(View.VISIBLE);
                mLinear_TravelNotesList.removeAllViews();
                addTravelNotes(entity.getTravel());
            }else {
                mLinear_TravelNotes.setVisibility(View.GONE);
            }

            if(type == LinesDetailUserUploadActivity.Destination){
                mTxtDesc.setVisibility(View.GONE);
                mExpandableAdapter.setGroupLines(entity.getExtras());
                loadData();
            }else if(type == LinesDetailUserUploadActivity.UserUpload){
                mUserView.setVisibility(View.GONE);
                mTxt_category.setText("线路看点");
                setImgTxtAdapter(ImageTextAdapter.Positive, true,false,false, entity.getViewspot(), null, null);

            }else if(type == LinesDetailUserUploadActivity.OfficialLines){
                mTxtDesc.setVisibility(View.GONE);
                mExpandableAdapter.setGroupLines(entity.getExtras());
                loadData();
            }
        }
    }

    /**
     * 请求数据
     */
    private void loadData(){
        String url = null;
        int tripID;
        if(currentTrip == null){
            if(entity.getTrip()!= null &&entity.getTrip().size() > 0){
                tripID = entity.getTrip().get(0).getTripid();
            }else {
                return;
            }
        }else {
            tripID = currentTrip.getTripid();
        }
        SpUtil sp = new SpUtil(this);
        String uid = sp.getStringValue(Const.UID);
        String token = sp.getStringValue(Const.AUTH);
        if(type == LinesDetailUserUploadActivity.OfficialLines){
            if(entity != null && entity.getTrip() != null && entity.getTrip().size() > 0){
                url = Constant.IDRIVE_URL_BASE + String.format(Constant.LineTrip_Url,lid) + tripID +"&uid="+uid+"&token="+Utility.getUTF8XMLString(token);//token;//Schedule_Url;
               // Log.e(url, "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL ");
            }
        }else if(type == LinesDetailUserUploadActivity.Destination){
            if(entity != null && entity.getTrip() != null && entity.getTrip().size() > 0){
                url = Constant.IDRIVE_URL_BASE + String.format(Constant.Des_ViewSpotList_Url,lid)+ tripID+"&uid="+uid+"&token="+Utility.getUTF8XMLString(token);//Destination_ViewPort_URL;
            }
        }
//        Log.e("WLH","wheelview URL:"+url);
        if(!StringUtil.isEmpty(url)){
            mLoadingView.setVisibility(View.VISIBLE);
            RemoteDataService rd = new RemoteDataService(this);
            rd.alwaysLoadJSON(url, new BaseDataAsynCallback() {
                @Override
                public void onDataLoad(String _result) {
                    mLoadingView.setVisibility(View.GONE);
                    if(!StringUtil.isEmpty(_result)){
                        parseData(_result);
                    }else {
                        Toast.makeText(LinesDetailImageTextActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onError(String url) {
                    mLoadingView.setVisibility(View.GONE);
                    Toast.makeText(LinesDetailImageTextActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    /**
     * 筛选数据
     * @param _result
     */
    private void parseData(String _result) {
        try {
            if(type == LinesDetailUserUploadActivity.OfficialLines){
                JSONObject object = new JSONObject(_result);
                schedule_Entity = new LinesDetailUploadEntity(object);

                if(schedule_Entity != null){
                    setImgTxtAdapter(ImageTextAdapter.Positive, true,false,false, schedule_Entity.getViewspot(),null, null);
                    Intent intent = new Intent();
                    if(currentTrip==null){
                        intent.setAction(LinesDetailUserUploadActivity.MAP_POINT_INTENT_ACTION);
                    }else {
                        intent.setAction(LinesDetailMapActivity.MAP_POINT_ACTION);
                    }

                    intent.putExtra(LinesDetailMapActivity.EXTRA_VIEWPOTS, schedule_Entity.getViewspot());
                    sendBroadcast(intent);
                }
            }else {
                if (type == LinesDetailUserUploadActivity.Destination) {
                    JSONObjectHelper helper = new JSONObjectHelper(_result);

                    JSONArray array = helper.getJSONArray("datas", new JSONArray());
                    if(array != null){
                        DesViewSpotList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            LinesDetailUploadEntity.ViewSpot topic = new LinesDetailUploadEntity.ViewSpot(array.getJSONObject(i));
                            DesViewSpotList.add(topic);
                        }
                        if(DesViewSpotList!= null){
                            setImgTxtAdapter(ImageTextAdapter.Positive, true,false,true, DesViewSpotList,null, null);
                            Intent intent = new Intent();
                            if(currentTrip==null){
                                intent.setAction(LinesDetailUserUploadActivity.MAP_POINT_INTENT_ACTION);
                            }else {
                                intent.setAction(LinesDetailMapActivity.MAP_POINT_ACTION);
                            }
                            intent.putExtra(LinesDetailMapActivity.EXTRA_VIEWPOTS, DesViewSpotList);
                            sendBroadcast(intent);
                        }
                    }

                }
            }

            mImgTop.setFocusable(true);
            mImgTop.setFocusableInTouchMode(true);
            mImgTop.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建ImgTxtAdapter
     * @param canCollect
     * @param isViewSpot
     * @param isHotel
     * @param canCustom
     * @param dataViewSpot
     * @param dataImageText
     * @param dataHotel
     */
    private void setImgTxtAdapter(int canCollect, boolean isViewSpot, boolean isHotel, boolean canCustom,
        ArrayList<LinesDetailUploadEntity.ViewSpot> dataViewSpot,
        ArrayList<LinesDetailUploadEntity.ImageText> dataImageText,
        ArrayList<LinesDetailUploadEntity.Hotel> dataHotel){
        adapter.setType(canCollect);
        adapter.setIsViewSport(isViewSpot);
        adapter.setIsHotel(isHotel);
        adapter.setCanCustom(canCustom);
        if(isViewSpot ){
            adapter.setData_Viewport(dataViewSpot);
        }else {
            if(isHotel){
                adapter.setData_Hotel(dataHotel);
            }else {
                adapter.setData(dataImageText);
            }

        }
        adapter.notifyDataSetChanged();
        mLisView.setAdapter(adapter);
    }

    private void setPasscityAdapter(){
        adapter_passcity.setData(schedule_Entity.getPathway());
        adapter_passcity.notifyDataSetChanged();
        mLisView.setAdapter(adapter_passcity);
    }

    private void setAdapter_ExtendLines(){
        if(type ==  LinesDetailUserUploadActivity.Destination){
            adapter_ExtendLines.setDataList(entity.getRefway());
        }else {
            adapter_ExtendLines.setDataList(schedule_Entity.getRefway());
        }

        adapter_ExtendLines.notifyDataSetChanged();
        mLisView.setAdapter(adapter_ExtendLines);
    }

    /**
     * 分享
     * @param view
     */
    public void OnBtnShareClick(View view){
        String title = (entity == null) ? null : entity.getSummary().getLine();
        String imageURL = (entity==null) ? null : entity.getSummary().getImage();
        String content = (entity==null) ? null : entity.getSummary().getDesc();
        String url = (entity==null) ? null : entity.getSummary().getUrl();

        ShareUtil.showShare(this, lid, detailType, title, imageURL, url, content);
    }

    /**
     * 点赞
     * @param view
     */
    public void OnBtnPraiseClick(View view){
        PraiseUtil.PraiseOrCancel(this, lid, detailType, statusUtil.IsPraise(),
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
        intent.putExtra(InfoDetailActivity.EXTRA_DOCID, lid);
        intent.putExtra(CommentActivity.EXTRA_TYPE, detailType);
        startActivity(intent);
    }

    /**
     * 更多的showDialog
     * @param view
     */
    public void OnBtnMoreClick(View view){//线路详情--更多、 目的地详情--举报
        if(detailType == Constant.DETAIL_TYPE_DES){
            Intent intent = new Intent(LinesDetailImageTextActivity.this, ReportActivity.class);
            intent.putExtra(InfoDetailActivity.EXTRA_DOCID, lid);
            intent.putExtra(CommentActivity.EXTRA_TYPE, detailType);
            LinesDetailImageTextActivity.this.startActivity(intent);
        }else {
            showDialog();
        }

    }

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
            final TextView mTxtCollect = (TextView) contentView.findViewById(R.id.txt_collect);
            if(statusUtil.IsFavorite()){
                mTxtCollect.setText("已收藏");
            }else {
                mTxtCollect.setText("收藏");
            }

            RelativeLayout mJoinin = (RelativeLayout) contentView.findViewById(R.id.btn_joinin);
             mJoinin.setVisibility(View.GONE);
            final TextView mTxtCustom = (TextView) contentView.findViewById(R.id.txt_joinin);
            if(statusUtil.IsCustom()){
                mTxtCustom.setText("已加入定制");
            }else {
                mTxtCustom.setText("加入定制");
            }

            RelativeLayout mMyCart = (RelativeLayout) contentView.findViewById(R.id.btn_myCart);
            mMyCart.setVisibility(View.VISIBLE);

            RelativeLayout mCancel = (RelativeLayout) contentView.findViewById(R.id.btn_cancel);
            mReport.setOnClickListener(new View.OnClickListener() {//举报
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    LinesDetailImageTextActivity.this.startActivity(new Intent(LinesDetailImageTextActivity.this, ReportActivity.class));
                }
            });
            mCollect.setOnClickListener(new View.OnClickListener() {//收藏
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    CollectUtil.CollectOrCancel(LinesDetailImageTextActivity.this, lid, detailType,
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
                                    }
                                }
                            });
                }
            });

            mJoinin.setOnClickListener(new View.OnClickListener() {//加入定制
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();

                        CustomUtil.CustomOrCancel(LinesDetailImageTextActivity.this, lid, detailType,
                                statusUtil.IsCustom(), new PraiseUtil.ResultCallback() {
                                    @Override
                                    public void callback(boolean isSuccess) {
                                        if (isSuccess) {
                                            if (statusUtil.IsCustom()) {
                                                statusUtil.setIsCustom(false);
                                                mTxtCustom.setText("加入定制");
                                            } else {
                                                statusUtil.setIsCustom(true);
                                                mTxtCustom.setText("已加入定制");
                                            }
                                        }
                                    }
                                });

                }
            });
            mMyCart.setOnClickListener(new View.OnClickListener() {//我的定制
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    LinesDetailImageTextActivity.this.startActivity(new Intent(LinesDetailImageTextActivity.this, MystyleActivity.class));
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

    /**
     * 游记-照片展示
     * @param notes
     */
    private void addTravelNotes(final ArrayList<LinesDetailUploadEntity.TravelNote> notes){
        int margin = (int) this.getResources().getDimension(R.dimen.size20);
        int img_width = (ScreenUtil.getScreenWidth(this) - margin) / 3;

        for(int j = 0; j< notes.size(); j++){
            LinesDetailUploadEntity.TravelNote note = notes.get(j);
            View noteItem = LayoutInflater.from(this).inflate(R.layout.item_travel_note, null);
            TextView mTxtNoteDesc = (TextView) noteItem.findViewById(R.id.desc_travelnote_item);
            TextView mTxtNoteDate = (TextView) noteItem.findViewById(R.id.date);
            LinearLayout mNoteImages = (LinearLayout) noteItem.findViewById(R.id.Linear_travelnote_item);

            mTxtNoteDesc.setText(note.getTitle());
            mTxtNoteDate.setText(note.getDate());
            if(note.getImages() != null){
                for (int i = 0; i < note.getImages().size(); i++) {
                    ImageView img = new ImageView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.height = img_width;
                    params.width = img_width;
                    params.setMargins(5, 0, 5, 0);
                    img.setLayoutParams(params);
                    img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img.setBackgroundResource(R.drawable.cqsw_default_pic);
                    final int finalJ = j;
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(LinesDetailImageTextActivity.this, TravelNotesActivity.class);
                            intent.putExtra(TravelNotesActivity.EXTRA_DATA, entity.getTravel());
                            intent.putExtra(TravelNotesActivity.EXTRA_INDEX, finalJ);
                            LinesDetailImageTextActivity.this.startActivity(intent);
                        }
                    });
                    if (!StringUtil.isEmpty(note.getImages().get(i).getImage())) {
                        new ImageDownloader.Builder()
                                .setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC)
                                .build(note.getImages().get(i).getImage(), img)
                                .start();
                        mNoteImages.addView(img);
                    }

                }
            }
            mLinear_TravelNotesList.addView(noteItem);
        }
    }

    /**
     * 分类的选择
     */
    private void showChooseDialog(){
        popChooseDialog = null;
        if(popChooseDialog == null){
            popChooseDialog = new Dialog(this, R.style.popFromBottomdialog);
            View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_wheelview, null);
            popChooseDialog.setContentView(contentView);
            Window dialogWindow = popChooseDialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.FILL_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setAttributes(lp);


            final TextView mTxttitle= (TextView) contentView.findViewById(R.id.title);
            mTxttitle.setText("选择分类");
            TextView mCancel = (TextView) contentView.findViewById(R.id.txt_cancel);
            TextView mOK = (TextView) contentView.findViewById(R.id.txt_confirm);
            WheelView wheelView = (WheelView) contentView.findViewById(R.id.wheel_view_wv);

            if(entity!= null && entity.getTrip()!= null){

                ArrayList<String> chooseitems = new ArrayList<String>();
                for (LinesDetailUploadEntity.Trip item: entity.getTrip()){
                    chooseitems.add(item.getTripname());
                }
                wheelView.setOffset(2);
                wheelView.setItems(chooseitems);
                wheelView.setSeletion(0);
                wheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                    @Override
                    public void onSelected(int selectedIndex, String item) {
                        Log.e("WLH", "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);

                        mTxttitle.setText(item);
                        currentTrip = entity.getTrip().get(selectedIndex-2);

                    }
                });
            }



            mOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popChooseDialog.dismiss();
                    if(currentTrip!= null){
                        mTxt_category.setText(currentTrip.getTripname());
                        loadData();
                    }

                }
            });
            mCancel.setOnClickListener(new View.OnClickListener() {//取消
                @Override
                public void onClick(View view) {
                    popChooseDialog.dismiss();
                }
            });

        }else {
            if(popChooseDialog.isShowing()){
                popChooseDialog.dismiss();
                popChooseDialog = null;
                return;
            }
        }
        popChooseDialog.show();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(changeStatusBroadcastReceiver);
        super.onDestroy();
    }
}
