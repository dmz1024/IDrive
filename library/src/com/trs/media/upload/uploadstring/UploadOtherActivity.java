package com.trs.media.upload.uploadstring;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by wbq on 14-8-7.
 */
public class UploadOtherActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UploadByOther uploadByOther = new UploadByOther();
        try {
            boolean t = uploadByOther.uploadHttpURLConnection("123","456","crop.png");
            System.out.print(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
