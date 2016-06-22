package com.ccpress.izijia.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.fragment.CommentListFragment;
import com.ccpress.izijia.util.CommentUtil;
import com.ccpress.izijia.util.PraiseUtil;
import com.ccpress.izijia.util.Utility;
import com.froyo.commonjar.utils.SpUtil;
import com.trs.app.TRSFragmentActivity;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;

/**
 * Created by WLH on 2015/5/13 13:57.
 *评论
 */
public class CommentActivity extends TRSFragmentActivity{

    public static String EXTRA_TYPE = "type";

    private EditText mEdittext;
    private TextView mBtnCancel;
    private TextView mBtnOK;

    private View mBottomBar;

    private String docid;
    private String type;

    private  CommentListFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        ((TextView)findViewById(R.id.title)).setText("评论");

        docid = getIntent().getStringExtra(InfoDetailActivity.EXTRA_DOCID);
        type = getIntent().getStringExtra(EXTRA_TYPE);

        SpUtil sp = new SpUtil(this);
        String uid = sp.getStringValue(Const.UID);
        String token = sp.getStringValue(Const.AUTH);

        String url = String.format(Constant.INTERACT_URL_BASE+Constant.INTERACT_COMMENTLIST, docid, type, Utility.getUTF8XMLString(token), uid);
        Log.e("WLH", "CommentActivity url:" + url);

        init();

        fragment = new CommentListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CommentListFragment.EXTRA_URL, url);
        bundle.putBoolean(CommentListFragment.EXTRA_HAS_ADS, false);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment).commit();
    }

    private void init() {
        mEdittext= (EditText) findViewById(R.id.edit_comment);
        mBtnCancel = (TextView) findViewById(R.id.btn_cancel);
        mBtnOK = (TextView) findViewById(R.id.btn_ok);
        mBottomBar = findViewById(R.id.bottom_bar_comment);

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEdittext.getWindowToken(), 0);
                mBottomBar.requestFocus();
            }
        });
        mEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    mBottomBar.setBackgroundColor(getResources().getColor(R.color.idrive_white));
                    mBtnCancel.setVisibility(View.VISIBLE);
                    mBtnOK.setVisibility(View.VISIBLE);
                }else{
                    mBottomBar.setBackgroundColor(getResources().getColor(R.color.idrive_blue));
                    mBtnCancel.setVisibility(View.GONE);
                    mBtnOK.setVisibility(View.GONE);
                }
            }
        });
        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = mEdittext.getText().toString().trim();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEdittext.getWindowToken(), 0);
                mEdittext.setText("");
                if(StringUtil.isEmpty(comment)){
                    Toast.makeText(CommentActivity.this,"评论内容不能为空~",Toast.LENGTH_SHORT).show();
                }else {
                    CommentUtil.commitComment(CommentActivity.this, docid, type, comment,
                            new PraiseUtil.ResultCallback() {
                                @Override
                                public void callback(boolean isSuccess) {
                                    if(isSuccess){
                                        fragment.showLoading();
                                        fragment.showRefreshing();
                                    }
                                }
                            });
                }
            }
        });
    }


}
