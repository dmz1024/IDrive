package com.trs.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import com.trs.app.TRSApplication;
import com.trs.app.TRSFragmentActivity;
import com.trs.mobile.R;
import com.trs.types.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wbq on 14-7-2.
 */
public abstract class TopTabMainActivity extends TRSFragmentActivity {
    private RadioGroup mTopLayout;
    private FrameLayout mContentLayout;
    private List<Channel> mTabDataList = new ArrayList<Channel>();
    private TRSApplication mApplication;
    private OnMenuCheckedListener mOnMenuCheckedListener;
    private View mPopupLocation;
    private RelativeLayout mTopScrollShowLayout;

    public void setmOnMenuCheckedListener(OnMenuCheckedListener mOnMenuCheckedListener) {
        this.mOnMenuCheckedListener = mOnMenuCheckedListener;
    }

    public interface OnMenuCheckedListener{
        public void onMenuChecked(View menu);
    }

    private void onMenuChecked(View menuView){
        if(mOnMenuCheckedListener != null){
            mOnMenuCheckedListener.onMenuChecked(menuView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewID());

        mTabDataList.clear();
        mApplication = (TRSApplication) getApplicationContext();
        mTabDataList = mApplication.getFirstClassMenu().getChannelList();

        initView();
        getTopView(listener);
    }


    private void initView(){
        mTopScrollShowLayout = (RelativeLayout) findViewById(R.id.extend_layout);
        mTopLayout = (RadioGroup) findViewById(R.id.topId);
        mContentLayout = (FrameLayout) findViewById(R.id.frameLayoutId);
        mPopupLocation = findViewById(R.id.popup_location_id);
    }

    public RelativeLayout getmTopScrollShowLayout() {
        return mTopScrollShowLayout;
    }

    public View getPopupLocation(){
        return mPopupLocation;
    }

    public List<Channel> getTopDataList(){
        return mTabDataList;
    }

    public RadioGroup getTopLayout(){
        return mTopLayout;
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onMenuChecked(v);
        }
    };

    public void replaceFragment(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frameLayoutId,fragment).commit();
    }

    public abstract void getTopView(View.OnClickListener listener);
    public abstract int getViewID();

}
