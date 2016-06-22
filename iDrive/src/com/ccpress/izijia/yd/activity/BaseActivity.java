package com.ccpress.izijia.yd.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.ccpress.izijia.R;
import com.ccpress.izijia.yd.view.TitleBar;


/**
 * activity基类
 */

public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener, TitleBar.OnRightClickListener, TitleBar.OnLeftClickListener {

    private LinearLayout ll_root;
    public TitleBar title_bar;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            handler(msg);
        }
    };

    /**
     * 处理Handler事件
     *
     * @param msg
     */
    public void handler(Message msg) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView();
        int rid = getRid();
        if (rid != 0) {
            setContentView(rid);
        }
        initView();
        initData();
        initTitleBar();
    }

    protected abstract int getRid();


    protected abstract void initTitleBar();

    private void initContentView() {
        ViewGroup viewGroup = getView(android.R.id.content);
        viewGroup.removeAllViews();
        ll_root = new LinearLayout(this);
        ll_root.setOrientation(LinearLayout.VERTICAL);
        viewGroup.addView(ll_root);
        LayoutInflater.from(this).inflate(R.layout.yd_activity_base, ll_root, true);
        title_bar = getView(R.id.title_bar);
        title_bar.setOnLeftClickListener(this);
        title_bar.setOnRightClickListener(this);
    }

    protected abstract void initData();

    protected abstract void initView();


    /**
     * 代替findViewById
     *
     * @param id
     * @param <E>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E extends View> E getView(int id) {
        try {
            return (E) findViewById(id);
        } catch (ClassCastException ex) {
            throw ex;
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, ll_root, true);
    }

    @Override
    public void setContentView(View view) {
        ll_root.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        ll_root.addView(view, params);
    }

    /**
     * title_bar右侧点击事件
     */
    @Override
    public void right() {

    }

    @Override
    public void left() {
        finish();
    }

    /**
     * Activity跳转,不销毁当前Activity
     *
     * @param clx
     */
    public void skip(Class clx) {
        startActivity(new Intent(this, clx));
    }

    /**
     * Activity跳转,销毁当前Activity
     *
     * @param clx
     */
    public void skipFinish(Class clx) {
        startActivity(new Intent(this, clx));
        finish();
    }


    /**
     * view点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            left();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
