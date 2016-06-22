package com.ccpress.izijia.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.ccpress.izijia.R;
import com.ccpress.izijia.entity.LinesDetailUploadEntity;

import com.ccpress.izijia.util.DialogUtil;
import com.ccpress.izijia.util.ShareUtil;
import com.trs.app.TRSFragmentActivity;
import com.trs.util.ImageDownloader;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;

import java.util.ArrayList;

/**
 * Created by WLH on 2015/5/19 15:34.
 * 游记照片
 */
public class TravelNotesActivity extends TRSFragmentActivity {

    public static String EXTRA_DATA = "data";
    public static String EXTRA_INDEX = "index";

    private LinearLayout mLinearContent;

    private ArrayList<LinesDetailUploadEntity.TravelNote> data = new ArrayList<LinesDetailUploadEntity.TravelNote>();

    private int scroll_offset = 0;

    private ScrollView mScrollView;
    private int index = 0;
    private boolean[] hasMeasured;
    private Dialog popDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_travel_notes);

        init();
    }

    /**
     * 初始化布局控件
     */
    private void init() {
        data = (ArrayList<LinesDetailUploadEntity.TravelNote>) getIntent().getSerializableExtra(EXTRA_DATA);
        index = getIntent().getIntExtra(EXTRA_INDEX, 0);
        hasMeasured = new boolean[index];
        for(int i=0;i < index; i++){
            hasMeasured[i] = false;
        }

        ((TextView) findViewById(R.id.title)).setText("游记");
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mLinearContent = (LinearLayout) findViewById(R.id.content);
        if (data == null) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            LinesDetailUploadEntity.TravelNote note = data.get(i);
            addTravelNoteDaily(note, i);
        }

        mScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (scroll_offset > 0) {
                    mScrollView.scrollTo(0, scroll_offset);
                }
            }
        }, 300);


    }

    @Override
    protected void onPause() {
        super.onPause();
        scroll_offset = 0;
        index = 0;
    }

    public void OnBtnShareClick(View view){
        ShareUtil.showShare(this, null,null,null,null,null,null);
    }
    public void OnBtnPraiseClick(View view){
        DialogUtil.showResultDialog(this, "点赞成功", R.drawable.icon_success);
    }
    public void OnBtnCommentClick(View view){
        startActivity(new Intent(this, CommentActivity.class));
    }
    public void OnBtnMoreClick(View view){
        showDialog();
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
            RelativeLayout mJoinin = (RelativeLayout) contentView.findViewById(R.id.btn_joinin);
            TextView mTxtJoinin = (TextView) contentView.findViewById(R.id.txt_joinin);
            mTxtJoinin.setText("保存图片");
            RelativeLayout mCancel = (RelativeLayout) contentView.findViewById(R.id.btn_cancel);
            mReport.setOnClickListener(new View.OnClickListener() {//举报
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    TravelNotesActivity.this.startActivity(new Intent(TravelNotesActivity.this, ReportActivity.class));
                }
            });
            mCollect.setOnClickListener(new View.OnClickListener() {//收藏
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                }
            });
            mJoinin.setOnClickListener(new View.OnClickListener() {//保存图片
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();

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

    private void addTravelNoteDaily(LinesDetailUploadEntity.TravelNote note, final int i) {
        if (note == null) {
            return;
        }
        final View noteItem = LayoutInflater.from(this).inflate(R.layout.item_travelnote_daily, null);
        TextView mTxtNoteDesc = (TextView) noteItem.findViewById(R.id.desc_travelnote_item);
        TextView mTxtNoteDate = (TextView) noteItem.findViewById(R.id.date);
        LinearLayout mNoteImages = (LinearLayout) noteItem.findViewById(R.id.Linear_travelnote_item);

        mTxtNoteDesc.setText("第"+(i+1)+"天");//note.getTitle()
        mTxtNoteDate.setText(note.getDate());
        if (note.getImages() == null) {
            return;
        }
        for (LinesDetailUploadEntity.TravelNote.NoteImage noteImage : note.getImages()) {
            if (noteImage == null) {
                continue;
            }
            View imagesItem = LayoutInflater.from(this).inflate(R.layout.item_travelnote_img, null);
            TextView mTxtSpotDesc = (TextView) imagesItem.findViewById(R.id.desc_travelnote_item);
            ImageView mImage = (ImageView) imagesItem.findViewById(R.id.img_travelnote_item);

            mTxtSpotDesc.setText(noteImage.getDesc());
            if (StringUtil.isEmpty(noteImage.getImage())) {
                mImage.setVisibility(View.GONE);
            } else {
                mImage.setVisibility(View.VISIBLE);
                new ImageDownloader.Builder()
                        .setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC)
                        .build(noteImage.getImage(), mImage)
                        .start();
            }
            mNoteImages.addView(imagesItem);
        }

//        if(i == data.size()-1){
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.top_bar_height));
//            noteItem.setLayoutParams(lp);
//        }

        mLinearContent.addView(noteItem);

        if (i < index) {
            Log.e("WLH", " index:" + index + " i:" + i);
            noteItem.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (hasMeasured[i] == false) {
                        hasMeasured[i] = true;
                        scroll_offset += noteItem.getHeight();
                        Log.e("WLH", "scroll_offset:" + scroll_offset);
                    }
                    return true;
                }
            });

        }


    }
}
