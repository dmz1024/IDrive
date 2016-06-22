package com.soundcloud.android.crop.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;
import com.soundcloud.android.crop.Crop;

import java.io.File;

public class CorpImageMainActivity extends Activity {
    private Context mContext;

    public static String EXTRA_INPUT_PATH = "input_path";
    public static String EXTRA_OUTPUT_PATH = "output_path";

    private String mInputPath;
    private String mOutputPath;

    private ImageView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.corp_activity_main);

        //
        File tempDir = new File(getCacheDir(), "temp");
        if(tempDir != null){
            tempDir.mkdirs();
        }
        mOutputPath = new File(tempDir, String.format("%s.jpg", System.currentTimeMillis())).toString();
        //

        resultView = (ImageView) findViewById(R.id.result_image);

        mInputPath = getIntent().getStringExtra(EXTRA_INPUT_PATH);
        mOutputPath = getIntent().getStringExtra(EXTRA_OUTPUT_PATH);

        if(mInputPath == null || mInputPath.length() == 0){
            return;
        }

        Uri uri = Uri.fromFile(new File(mInputPath));
        beginCrop(uri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        // 剪切完图片的返回
        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void beginCrop(Uri source) {
//        Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Uri outputUri = Uri.fromFile(new File(mOutputPath));
        new Crop(source).output(outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            resultView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
