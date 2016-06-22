package com.ccpress.izijia.dfy.callBack;

import com.ccpress.izijia.R;
import com.ccpress.izijia.dfy.util.CustomToast;
import org.xutils.common.Callback;

/**
 * Created by dmz1024 on 2016/3/20.
 */
public class MyCallBack implements Callback.CommonCallback<String> {
    @Override
    public void onSuccess(String s) {

    }

    @Override
    public void onError(Throwable throwable, boolean b) {
        CustomToast.showToast(R.string.noData);
    }

    @Override
    public void onCancelled(CancelledException e) {

    }

    @Override
    public void onFinished() {

    }
}
