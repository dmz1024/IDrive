package com.ccpress.izijia.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.*;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by WLH on 2015/5/12 16:44.
 */
public class DialogUtil {
    private Dialog popDialog = null;
    private Context mContext = null;

    public DialogUtil(Context mContext){
        this.mContext = mContext;
    }

    public void showDialog(final String number){
        if(popDialog == null){
            popDialog = new Dialog(mContext, R.style.popFromBottomdialog);
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.popupview_more, null);
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
            RelativeLayout mCancel = (RelativeLayout) contentView.findViewById(R.id.btn_cancel);
            mReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                }
            });
            mCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                }
            });
            mJoinin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                    CallUtil.showDialog(number, mContext);
                }
            });
            mCancel.setOnClickListener(new View.OnClickListener() {
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

    public static void showResultDialog(Context mContext, String result, int imgResId){
        final Dialog popDialog = new Dialog(mContext,R.style.popResultDialog);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_success, null);
        ImageView img = (ImageView) contentView.findViewById(R.id.img);
        TextView mTxtresult = (TextView)contentView.findViewById(R.id.result);
        img.setImageResource(imgResId);
        mTxtresult.setText(result);
        popDialog.setContentView(contentView);
        popDialog.show();

        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                popDialog.dismiss();
                t.cancel();
            }
        }, 1000);
    }
    public static void showResultDialogNeedFinish(final Activity mContext, String result, int imgResId){
        final Dialog popDialog = new Dialog(mContext,R.style.popResultDialog);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_success, null);
        ImageView img = (ImageView) contentView.findViewById(R.id.img);
        TextView mTxtresult = (TextView)contentView.findViewById(R.id.result);
        img.setImageResource(imgResId);
        mTxtresult.setText(result);
        popDialog.setContentView(contentView);
        popDialog.setCanceledOnTouchOutside(false);
        popDialog.setCancelable(false);
        popDialog.show();

        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                popDialog.dismiss();
                t.cancel();
                mContext.finish();
            }
        }, 1000);
    }


    public static Dialog getProgressdialog(Context mContext, String result){
        final Dialog progressDialog = new Dialog(mContext,R.style.popToCenterDialog);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_progress, null);
        TextView mTxtresult = (TextView)contentView.findViewById(R.id.result);
        if(result != null){
            mTxtresult.setText(result);
        }
        progressDialog.setContentView(contentView);
        progressDialog.setCancelable(false);
        return progressDialog;
    }



}
