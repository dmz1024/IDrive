package com.ccpress.izijia.yd.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by dengmingzhi on 16/6/1.
 */
public class MyAlert {
    private AlertDialog.Builder builder;

    public MyAlert(Context ctx, String title) {
        builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
    }

    /**
     * 单选
     *
     * @param cities
     */
    public void SingleSelection(final String[] cities) {
        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                singleSele(cities[which]);
            }
        });
        builder.show();
    }

    public void singleSele(String content) {

    }

    public void showContent(String content, String posotive) {
        builder.setMessage(content)
                .setPositiveButton(posotive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        posotive();
                    }
                })
                .setCancelable(false)//点击外部不消失
                .show();
    }


    public void posotive() {

    }


}
