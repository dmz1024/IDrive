package com.ccpress.izijia.dfy.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.FileUtil;
import com.ccpress.izijia.dfy.util.NetUtil;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.dfy.view.RefreshListView;
import com.trs.util.log.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dmz1024 on 2016/3/20.
 * frgment
 */
public abstract class FragmentBase<T, A extends BaseAdapter> extends Fragment implements RefreshListView.OnRefreshListener, View.OnClickListener, AdapterView.OnItemClickListener {
    protected int page = 1;
    protected List<T> mlist;
    private A mAdapter;
    protected RefreshListView mListView;
    protected RelativeLayout rl_load;
    private Button bt_again;
    private TextView tv_load;
    private TextView tv_nodata;
    private ProgressBar pb_load;
    protected boolean isFresh = false;
    protected boolean isLoad = false;
    protected boolean isClear = false;
    private int position;
    protected List<T> currenList = new ArrayList<T>();
    protected boolean isNew = false;//如果下拉刷新了一次，则上拉加载也就默认为去网络读取数据，否则是优先本地
    protected boolean isFirst = true;//判断是否是第一次加载数据
    protected Handler handler = new Handler();
    protected boolean type=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView();
    }

    protected View initView() {
        View view = View.inflate(getActivity(), getRid(), null);
        mListView = (RefreshListView) view.findViewById(R.id.dfy_lv);
        rl_load = (RelativeLayout) view.findViewById(R.id.rl_load);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        bt_again = (Button) view.findViewById(R.id.bt_agin);
        tv_load = (TextView) view.findViewById(R.id.tv_load);
        pb_load = (ProgressBar) view.findViewById(R.id.pb_load);
        bt_again.setOnClickListener(this);
        mListView.setOnRefreshListener(this);
        mListView.setMode(getMode());
        mlist = new ArrayList<T>();
        View headerView = getHeaderView();
        if (headerView != null) {
            mListView.addHeaderView(getHeaderView());
        }
        mListView.setAdapter(mAdapter = getAdapter(mlist));
        mListView.setOnItemClickListener(this);
        init();

        return view;
    }

    protected boolean getType(){
        return false;
    }

    protected void init() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initData(false);
            }
        }, 200);
    }


    protected View getHeaderView() {
        return null;
    }


    /**
     * 设置listView能否刷新或加载
     */
    protected abstract RefreshListView.Mode getMode();

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        onItemClick(mlist, i);
    }

    protected void onItemClick(List<T> list, int i) {

    }

    /**
     * ListViewd的加载更多事件
     */
    @Override
    public void onLoadingMore() {
        position = mlist.size() - 2;
        if (position > 0) {
            isLoad = true;
        }
        showData();
    }

    /**
     * ListViewd的下拉刷新事件
     */

    @Override
    public void onPullRefresh() {
        isNew = true;
        isFresh = true;
        isClear = true;
        page = 1;
        initData(true);
    }

    protected abstract List<T> getList(String json);

    ;

    /**
     * 4种状态：1、正在加载
     * 2、加载失败
     * 3、加载成功
     * 4、为空
     *
     * @param isFresh
     */


    protected void initData(boolean isFresh) {
        currenList.clear();

        if (!isFresh) {
            if (mlist.size() == 0) {
                loadVisibility(true);
                rlLoad(1);
            } else {
                loadVisibility(false);
            }

        }

        String json = null;
        if (!isFresh) {//判断是否是手动刷新数据的，如果是则请求网络数据，否则优先读取本地数据
            json = FileUtil.getDataFromNative(Util.getSaveUrl(getSubmitType() ? post() : get(), getUrl()));
        }
        if (json == null) {
            if (getSubmitType()) {
                NetUtil.Post(getUrl(), post(), new MyCallBack());

            } else {
                NetUtil.Get(getUrl(), get(), new MyCallBack());
            }
        } else {
            //如果在本地读取到了数据  则加入到list
            addList(json);
            loadVisibility(false);
            notifyData();
        }


    }


    protected Map<String, String> get() {
        return null;
    }

    protected Map<String, Object> post() {
        return null;
    }

    /**
     * 返回http提交类型，true为post，false为get
     *
     * @return
     */
    protected abstract boolean getSubmitType();

    protected void addList(String json) {
        List<T> list = getList(json);
        if (list != null) {
            if (isClear) {
                isClear = false;
                mlist.clear();
            }
            currenList.addAll(list);
            mlist.addAll(currenList);
        }
    }

    protected void notifyData() {
        loadSelection();
        pullSelection();
        mAdapter.notifyDataSetChanged();
        mListView.completeRefresh();
    }

    private void loadSelection() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isLoad) {
//                    mListView.setSelection(position);
                    mListView.setSelectionFromTop(position, -70);
                    isLoad = false;
                }
            }
        });

    }

    private void pullSelection() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isFresh) {
                    mListView.setSelection(0);
                    isFresh = false;
                }
            }
        });
    }

    protected void showData() {
        if (mlist.size() > 0) {
            page = page + 1;
            initData(isNew);
        }
    }


    /**
     * 设置资源id
     *
     * @return
     */
    protected int getRid() {
        return R.layout.dfy_item_listview;
    }

    /**
     * 设置访问的url
     *
     * @return
     */
    protected abstract String getUrl();

    /**
     * 设置适配器
     *
     * @param list
     * @return
     */
    protected abstract A getAdapter(List<T> list);

    /**
     * view的点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_agin:
                initData(true);
                break;
        }
    }

    protected class MyCallBack extends com.ccpress.izijia.dfy.callBack.MyCallBack {
        @Override
        public void onSuccess(String json) {
            super.onSuccess(json);
            Log.d("json", json);
            addList(json);
            if (currenList.size() == 0 && mlist.size() == 0) {
                loadVisibility(true);
                rlLoad(3);
                mAdapter.notifyDataSetChanged();
            }



            if (currenList.size() > 0) {
                loadVisibility(false);
                notifyData();
                saveJson(json);
            }
        }

        @Override
        public void onFinished() {
            super.onFinished();
            mListView.completeRefresh();
        }

        @Override
        public void onError(Throwable throwable, boolean b) {
            if (currenList.size() == 0 && mlist.size() == 0) {
                loadVisibility(true);
                rlLoad(2);
            } else {
                CustomToast.showToast(R.string.noData);
            }
        }

    }

    protected void saveJson(final String json) {
        new Thread() {
            @Override
            public void run() {
                String url;
                if (getSubmitType()) {
                    url = Util.getSaveUrl(post(), getUrl());
                } else {
                    url = Util.getSaveUrl(get(), getUrl());
                }
                FileUtil.setData2Native(url, json);
            }
        }.start();
    }

    /**
     * 通过show判断是隐藏和显示listView或者rl_root
     *
     * @param show
     */
    protected void loadVisibility(boolean show) {
        if(type){
            return;
        }
        mListView.setVisibility(View.GONE);
        rl_load.setVisibility(View.GONE);

        if (show) {
            rl_load.setVisibility(View.VISIBLE);
        } else {
            mListView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * rl_load中view的显示状态
     *
     * @param type
     */
    protected void rlLoad(int type) {
        tv_load.setVisibility(View.GONE);
        pb_load.setVisibility(View.GONE);
        bt_again.setVisibility(View.GONE);
        tv_nodata.setVisibility(View.GONE);
        if (type == 1) {
            tv_load.setVisibility(View.VISIBLE);
            pb_load.setVisibility(View.VISIBLE);
        } else if (type == 2) {
            bt_again.setVisibility(View.VISIBLE);
        } else {
            tv_nodata.setVisibility(View.VISIBLE);
        }
    }

}


