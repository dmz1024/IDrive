package com.ccpress.izijia.yd.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by dengmingzhi on 16/5/17.
 */
public class MyAlertDialog {
    private Context ctx;
    private AlertDialog.Builder builder;
    public MyAlertDialog(Context ctx,String title, String content,String posotive,String negative){
        this.ctx=ctx;
        builder = new AlertDialog.Builder(this.ctx);
        builder.setTitle(title)
                .setMessage(content)
                .setPositiveButton(posotive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        posotive();
                    }
                })
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        negative();
                    }
                })
                .setCancelable(false)//点击外部不消失
                .show();
    }

    public MyAlertDialog(Context ctx,int title,int content,int posotive,int negative){
        this.ctx=ctx;
        builder = new AlertDialog.Builder(this.ctx);
        builder.setTitle(title)
                .setMessage(content)
                .setPositiveButton(posotive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        posotive();
                    }
                })
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        negative();
                    }
                }).show();
    }

    /**
     * 取消
     */
    public void negative() {

    }

    /**
     * 确认
     */
    public void posotive() {

    }
}
