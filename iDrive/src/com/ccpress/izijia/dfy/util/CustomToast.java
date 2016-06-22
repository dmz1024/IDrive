package com.ccpress.izijia.dfy.util;

import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by dmz1024 on 2016/4/6.
 */
public class CustomToast {
    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    public static void showToast(String text) {
        show(text);
        mHandler.postDelayed(r, 2500);
        mToast.show();
    }

    public static void showToast(int sId) {
        show(Util.getMyApplication().getResources().getString(sId));
        mHandler.postDelayed(r, 2500);
        mToast.show();
    }

    public static void showToast(int sId, int time) {
        show(Util.getMyApplication().getResources().getString(sId));
        mHandler.postDelayed(r, time);
        mToast.show();
    }


    public static void showToast(String text, int time) {
        show(text);
        mHandler.postDelayed(r, time);
        mToast.show();
    }

    public static void show(String text) {
        mHandler.removeCallbacks(r);
        if (mToast != null)
            mToast.setText(text);
        else
            mToast = Toast.makeText(Util.getMyApplication(), text, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
    }
}
