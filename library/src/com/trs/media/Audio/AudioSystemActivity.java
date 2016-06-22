package com.trs.media.Audio;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by wbq on 14-7-31.
 */
public class AudioSystemActivity extends Activity{
    public static final int RESULT_CAPTURE_RECORDER_SOUND = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EnvironmentShare.haveSdCard()) {
            Toast.makeText(this,"SD卡不存在", Toast.LENGTH_LONG).show();
        } else {
            //Intent intent = new Intent("android.provider.MediaStore.RECORD_SOUND");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/amr");
            startActivityForResult(intent, RESULT_CAPTURE_RECORDER_SOUND);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uriRecorder = data.getData();
        Cursor cursor = this.getContentResolver().query(uriRecorder, null, null, null, null);
        if (cursor.moveToNext()) {
    		/* _data：文件的绝对路径 ，_display_name：文件名 */
            String strRecorderPath = cursor.getString(cursor.getColumnIndex("_data"));
            Toast.makeText(this, strRecorderPath,Toast.LENGTH_SHORT).show();
        }
    }
}
