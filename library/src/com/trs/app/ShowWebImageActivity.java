package com.trs.app;

import java.io.*;
import java.net.URL;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trs.mobile.R;
import com.trs.util.ImageDownloader;

/**
 * Created by Wu Jingyu
 * Date: 2014/9/17
 * Time: 15:53
 */
public class ShowWebImageActivity extends Activity {
    private ImageView saveBtn = null;
    private String imagePath = null;
    private ImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_webimage);
        this.imagePath = getIntent().getStringExtra("image");

        saveBtn = (ImageView) findViewById(R.id.save_btn);
        final String path = imagePath;
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cachePath = ImageLoader.getInstance().getDiscCache().get(path).getPath();
                String name = getImageName(cachePath);
                String despath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + name  + ".jpg";
                saveImage(cachePath, despath);
                ShowWebImageActivity.this.finish();
            }
        });

        imageView = (ImageView) findViewById(R.id.show_webimage_imageview);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    imageView.setImageBitmap(((BitmapDrawable) ShowWebImageActivity.loadImageFromUrl(ShowWebImageActivity.this.imagePath)).getBitmap());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
        new ImageDownloader.Builder().
                setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).
                build(this.imagePath, imageView).start();


    }

    public static Drawable loadImageFromUrl(String url) throws IOException {
        URL m = new URL(url);
        InputStream i = (InputStream) m.getContent();
        Drawable d = Drawable.createFromStream(i, "src");
        return d;
    }

    public void saveImage(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newFile = new File(newPath);
            if (oldfile.exists() && !newFile.exists()) {  //文件存在时
                InputStream is = new FileInputStream(oldPath);  //读入原文件
                OutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = is.read(buffer)) != -1) {
                    bytesum += byteread;  //字节数  文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                is.close();
            }
            Toast.makeText(ShowWebImageActivity.this, "已保存至/sdcard/Download/" + getImageName(newPath) + ".jpg", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(ShowWebImageActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public String getImageName(String path) {
        return path.substring(path.lastIndexOf("/")+1);
    }
}
