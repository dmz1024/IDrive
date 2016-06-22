package com.ccpress.izijia.activity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.trs.app.TRSApplication;
import com.ccpress.izijia.R;
import com.trs.util.NetUtil;
import net.endlessstudio.util.Util;

import java.io.InputStream;

/**
 * Created by Wu Jingyu
 * Date: 2015/3/6
 * Time: 16:30
 */
public class iDriveSplashActivity extends com.trs.app.SplashActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_splash);
        if(!NetUtil.isConntected(TRSApplication.app())) {
            Toast.makeText(TRSApplication.app(),
                    getResources().getText(R.string.please_connnect_network),
                    Toast.LENGTH_SHORT).show();
        }

        showView(true);
    }

    @Override
    protected void initData() {
        try{
            InputStream is = Util.getStream(TRSApplication.app(),
                    TRSApplication.app().getFirstClassUrl());
            String JSONString = Util.readStreamString(is, "UTF-8");
            TRSApplication.app().setFirstClassMenu(createFirstClassMenu(JSONString));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
