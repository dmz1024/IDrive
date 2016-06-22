package com.trs.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.trs.app.TRSApplication;
import com.trs.mobile.R;
import com.trs.types.Channel;
import com.trs.util.ImageDownloader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wubingqian on 14-3-11.
 */
public class GridMainActivity extends Activity{
    // TODO 数据中获取.
    private static int ITEMS_OF_LINE = 3;
    private Context mContext = this;
    private GridView mGridView;
    private List<Channel> mDataList = new ArrayList<Channel>();
    private TRSApplication mApplication;
    DisplayMetrics metrics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_grid);
        metrics = mContext.getResources().getDisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mDataList.clear();
        mApplication = (TRSApplication) getApplicationContext();
        mDataList = mApplication.getFirstClassMenu().getChannelList();

        initView();
        setView();
    }

    private void initView(){
        mGridView = (GridView) findViewById(R.id.gridViewId);
    }

    private void setView(){
        mGridView.setAdapter(new adapter(mDataList, mContext));
        mGridView.setNumColumns(ITEMS_OF_LINE);
        mGridView.setColumnWidth(metrics.widthPixels / ITEMS_OF_LINE);
    }

    class adapter extends BaseAdapter{
        private List<Channel> mList;
        private Context mContext;

        public adapter(List<Channel> list,Context mContext){
            this.mContext = mContext;
            this.mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Holder holder;
            if(view == null){
                view = LayoutInflater.from(mContext).inflate(R.layout.main_grid_item,null);
                view.setMinimumHeight(metrics.widthPixels / ITEMS_OF_LINE);//metrics.heightPixels / (ITEMS_OF_LINE + 3)
                holder = new Holder();
                holder.mPic = (ImageView) view.findViewById(R.id.imgId);
                holder.mText = (TextView) view.findViewById(R.id.textId);
                view.setTag(holder);
            }
            holder = (Holder) view.getTag();
            holder.mText.setText(mList.get(i).getTitle());
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext));
            new ImageDownloader.Builder().
                    setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).
                    build(mList.get(i).getPic(),holder.mPic).start();

            holder.mPic.getLayoutParams().width = (int) ((metrics.widthPixels / ITEMS_OF_LINE) * 0.6);
            holder.mPic.getLayoutParams().height = (int) ((metrics.widthPixels / ITEMS_OF_LINE) * 0.6);
            return view;
        }

        class Holder{
            private ImageView mPic;
            private TextView mText;
        }
    }
}
