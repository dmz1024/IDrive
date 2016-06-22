package com.trs.media.upload.uploadpicture;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.trs.mobile.R;

import java.io.File;

/**
 * Created by wbq on 2014/8/20.
 */
public class UploadUtilActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "uploadImage";
    private static String requestURL = "http://192.168.1.104:8082/MyApplicationServer/MainServer";
    private Button selectImage,uploadImage;
    private ImageView imageView;
    private String picPath = null;

    /** Called when the activity is first created. */

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.uploadutil3main);
        selectImage = (Button) this.findViewById(R.id.selectImage);
        uploadImage = (Button) this.findViewById(R.id.uploadImage);
        selectImage.setOnClickListener(this);
        uploadImage.setOnClickListener(this);

        imageView = (ImageView) this.findViewById(R.id.imageView);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.selectImage) {
            /**
             * 这个是调用android内置的intent，来过滤图片文件 ，同时也可以过滤其他的
             */
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 1);
        } else if (i == R.id.uploadImage) {
            File file = new File(picPath);
            if (file != null) {
//                String request = UploadUtil.uploadFile(file, requestURL,null);
                uploadImage.setText("");
            }
        } else {}
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==Activity.RESULT_OK){
            /**
             * 当选择的图片不为空的话，在获取到图片的途径
             */
            Uri uri = data.getData();
            Log.e(TAG, "uri = "+ uri);
            try {
                String[] pojo = {MediaStore.Images.Media.DATA};

                Cursor cursor = managedQuery(uri, pojo, null, null,null);
                if(cursor!=null)
                {
                    ContentResolver cr = this.getContentResolver();
                    int colunm_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String path = cursor.getString(colunm_index);
                    /**
                     * 这里加这样一个判断主要是为了第三方的软件选择，比如：使用第三方的文件管理器的话，你选择的文件就不一定是图片了，这样的话，我们判断文件的后缀名
                     * 如果是图片格式的话，那么才可以
                     */
                    if(path.endsWith("jpg")||path.endsWith("png")){
                        picPath = path;
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        imageView.setImageBitmap(bitmap);
                    }else{alert();}
                }else{alert();}
            } catch (Exception e) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
    private void alert(){
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("您选择的不是有效的图片") .setPositiveButton("确定",

                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                picPath = null;
                            }
                        }) .create();
        dialog.show();
    }
}
