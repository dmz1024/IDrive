package com.ccpress.izijia.activity;

import android.app.ActivityGroup;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.mystyle.MystyleActivity;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.entity.LinesDetailUploadEntity;
import com.ccpress.izijia.util.*;
import com.froyo.commonjar.utils.SpUtil;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;
import com.trs.wcm.RemoteDataService;
import com.trs.wcm.callback.BaseDataAsynCallback;
import com.trs.wcm.callback.IDataAsynCallback;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by WLH on 2015/5/15 17:03.
 * 网友上传的线路详情
 */
public class LinesDetailUserUploadActivity extends ActivityGroup {
    public static final String MAP_POINT_INTENT_ACTION  = "com.ccpress.izijia.activity.LinesDetailMapActivity.mappoint";

    public static String EXTRA_ACT_TYPE = "fromact";
    public static String EXTRA_URL = "url";
    public static String EXTRA_LID = "lid";
    public static String EXTRA_MY_TYPE = "MY_type";
    public static final int UserUpload = 0;//用户上传的线路
    public static final int OfficialLines = 1;//官方路线
    public static final int Destination = 2;//目的地

    private int type = 0;
    private String lid = "";


    private String Url = "raw://line_detail_upload";
    DetailStatusUtil statusUtil = new DetailStatusUtil(this);

    private RadioButton mRdBtnImageText;
    private RadioButton mRdBtnMap;
    private View mLoadingView;
    private TabHost mTabHost;
    private String my_type;
    private String detailType = Constant.DETAIL_TYPE_LINE;
    private LinesDetailUploadEntity entity;

    private BroadcastReceiver mapPointReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!= null && intent.getAction().equals(MAP_POINT_INTENT_ACTION)){
                Log.e("WLH", "LinesDetailUserUploadActivity onReceive----");
                ArrayList<LinesDetailUploadEntity.ViewSpot> mapPoint = (ArrayList<LinesDetailUploadEntity.ViewSpot>)intent.getSerializableExtra(LinesDetailMapActivity.EXTRA_VIEWPOTS);
                entity.setViewspot(mapPoint);
                setupTabHost(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_detail_user_upload);
        type = getIntent().getIntExtra(EXTRA_ACT_TYPE, 0);

        my_type=getIntent().getStringExtra(EXTRA_MY_TYPE);
        EXTRA_MY_TYPE=my_type;
        Url = getIntent().getStringExtra(EXTRA_URL);
        lid = getIntent().getStringExtra(EXTRA_LID);


        initStatus();
        init();
        initData();
    }
    public void onBtnBackClick(View view) {
        finish();
    }

    /**
     * 图文、地图tab点击切换
     * @param isFirstSetUp
     */
    private void setupTabHost(boolean isFirstSetUp) {
        if(isFirstSetUp){
            mTabHost = (TabHost) findViewById(R.id.tabhost);
//            mTabHost.setup();
            mTabHost.setup(this.getLocalActivityManager());
        }else {
            mTabHost.clearAllTabs();
        }

        Intent dialIntent = new Intent();
        dialIntent.putExtra(LinesDetailImageTextActivity.EXTRA_ENTITY, entity);
        dialIntent.putExtra(EXTRA_ACT_TYPE, type);
        dialIntent.putExtra(LinesDetailImageTextActivity.EXTRA_LID, lid);
        dialIntent.setClass(this, LinesDetailImageTextActivity.class);
        mTabHost.addTab(buildTabSpec("tab_1", dialIntent));

        Intent mapIntent = new Intent();
        mapIntent.putExtra(LinesDetailMapActivity.EXTRA_VIEWPOTS, entity.getViewspot());
        dialIntent.putExtra(EXTRA_MY_TYPE, my_type);
        mapIntent.putExtra(LinesDetailImageTextActivity.EXTRA_LID, lid);
        mapIntent.setClass(this, LinesDetailMapActivity.class);
        mTabHost.addTab(buildTabSpec("tab_2", mapIntent));

    }

    private TabHost.TabSpec buildTabSpec(String tag, final Intent content) {
        return this.mTabHost.newTabSpec(tag).setIndicator(tag).setContent(content);
    }

    /**
     * 获取定制状态
     */
    private void initStatus() {
        if (type==UserUpload){
            statusUtil.getDetailStatus(lid,"13",new BaseDataAsynCallback() {
                @Override
                public void onDataLoad(String result, boolean bIsChanged) {
                }
            });
        }else {
            statusUtil.getDetailStatus(lid, detailType, new BaseDataAsynCallback() {
                @Override
                public void onDataLoad(String result, boolean bIsChanged) {

                }
            });
            }
    }

    /**
     * 布局控件设置
     */
    private void init(){
        SpUtil sp = new SpUtil(this);
        String uid = sp.getStringValue(Const.UID);
        String token = sp.getStringValue(Const.AUTH);

        if(StringUtil.isEmpty(Url)){
            if(type == 0){
                Url = Constant.INTERACT_URL_BASE + Constant.INTERACT_LINES_UPLOAD +"&lid="+lid +"&uid="+uid+"&token="+ Utility.getUTF8XMLString(token);
                Log.e(Url, "----0000000000 ----");
            }else if(type == OfficialLines){
                Url = Constant.IDRIVE_URL_BASE + String.format(Constant.LineDetail_URL, lid);//"raw://lines_detail";
                Log.e(Url, "--------OfficialLines------ ");
            }else if(type == Destination){
//                mTxtCustom.setVisibility(View.GONE);
                Url = Constant.IDRIVE_URL_BASE + String.format(Constant.DestinationDetail_URL, lid)+"&uid="+uid+"&token="+Utility.getUTF8XMLString(token);//"raw://destination_detail";
                Log.e(Url, "--------Destination------ ");
            }
        }
        Log.e("WLH", "LinesDetailActivity Url:"+Url);

        mLoadingView = findViewById(R.id.loading_view);
        mRdBtnImageText = (RadioButton) findViewById(R.id.rd_image_text);
        mRdBtnMap = (RadioButton) findViewById(R.id.rd_map);


    final TextView mTxtCustom = (TextView)findViewById(R.id.txt_joinin);
        Log.e("imTxtCustomnit ", String.valueOf(type));
        if (type == Destination) {
            mTxtCustom.setVisibility(View.GONE);
        } else {
            mTxtCustom.setVisibility(View.VISIBLE);
            try {
                Thread.currentThread().sleep(1000);//阻断1秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(statusUtil.IsCustom()){
                mTxtCustom.setText("已经定制");
            }else {
                mTxtCustom.setText("加入定制");
            }
        }
        //定制事件
        mTxtCustom.setOnClickListener(new View.OnClickListener() {//加入定制
                @Override
                           public void onClick(View view) {

                    if (type == UserUpload) {
                        Log.e("type", "onClick ");
                        CustomUtil.CustomOrCancel(LinesDetailUserUploadActivity.this, lid, "13",
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
                    } else {
                        CustomUtil.CustomOrCancel(LinesDetailUserUploadActivity.this, lid, detailType,
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
                }
            });



        mRdBtnImageText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b && !mLoadingView.isShown() && entity != null){
                    mTabHost.setCurrentTabByTag("tab_1");
                }
            }
        });
        mRdBtnMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LinesDetailMapActivity.TOPBAR="LinesDetailUserUploadActivity";
                if(b && !mLoadingView.isShown() && entity != null && entity.getViewspot()!= null){
                    mTabHost.setCurrentTabByTag("tab_2");
                }
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(MAP_POINT_INTENT_ACTION);
        registerReceiver(mapPointReceiver, filter);
    }

    /**
     * 填充数据
     */
    private void initData() {
        mLoadingView.setVisibility(View.VISIBLE);
        RemoteDataService rd = new RemoteDataService(this);
        rd.alwaysLoadJSON(Url, new IDataAsynCallback() {
            @Override
            public void onDataLoad(String _result, boolean isChange) {
                mLoadingView.setVisibility(View.GONE);
                if (!StringUtil.isEmpty(_result)) {
                    parseData(_result);
                    return;
                }
                Toast.makeText(LinesDetailUserUploadActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onDataChanged() {
            }
            @Override
            public void onError(String url) {
                mLoadingView.setVisibility(View.GONE);
                Toast.makeText(LinesDetailUserUploadActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 筛选数据
     * @param result
     */
    private void parseData(String result) {
        JSONObject object = null;
        try {
            object = new JSONObject(result);
            entity = new LinesDetailUploadEntity(object);

            if(entity != null){

                setupTabHost(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(mapPointReceiver);
        super.onDestroy();
    }

}
