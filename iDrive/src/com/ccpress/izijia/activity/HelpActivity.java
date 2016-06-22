package com.ccpress.izijia.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.portal.LoginActivity;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.entity.MediaEntity;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.*;
import com.ccpress.izijia.utils.CropFileUtils;
import com.froyo.commonjar.utils.AppUtils;
import com.froyo.commonjar.utils.InitUtil;
import com.froyo.commonjar.utils.SpUtil;
import com.trs.app.TRSFragmentActivity;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by YL on 2015/5/25.
 * 救援页面
 */
public class HelpActivity extends TRSFragmentActivity {
    private GridView mPostGridView;
    private PostGridViewAdapter mPostGridViewAdapter;
    public static final String CHECKED_MEDIA_LIST = "CheckedMediaList";
    private RelativeLayout time_settings,updateImg;
    private TextView txt_send,txt_time,geo;
    private  EditText content_help,phonenum_help;
    private ArrayList<MediaEntity> mImageList = new ArrayList<MediaEntity>();
    private Dialog popDialog = null;
    private Dialog popPhotoDialog=null;
    private ProgressDialog mProgressDialog;
    private File hCurrentPhotoFile;
    private String path;
    public  static String HPCITY="HPCITY";
    public static String PHOTOFLAG="photoflag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initView();
    }

    /**
     * 布局控件设置
     */
    private void initView() {
        SharedPreferences sharedPreferences=getSharedPreferences("mySp", Context.MODE_PRIVATE);
        String adress=sharedPreferences.getString("adress","");
        geo= (TextView) this.findViewById(R.id.geo);
        geo.setText(adress);
        time_settings= (RelativeLayout) this.findViewById(R.id.time_settings);
        //时长设置
        time_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        txt_time= (TextView) this.findViewById(R.id.txt_time);
        updateImg= (RelativeLayout) this.findViewById(R.id.updateImg);
        updateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
        content_help= (EditText) this.findViewById(R.id.content_help);
        phonenum_help= (EditText) this.findViewById(R.id.phonenum_help);

        txt_send= (TextView) this.findViewById(R.id.txt_send);
        //发送
        txt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(content_help.getText().toString())) {
                    Toast.makeText(HelpActivity.this, "救援信息不能为空", Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(phonenum_help.getText().toString())) {
                    Toast.makeText(HelpActivity.this, "救援电话不能为空", Toast.LENGTH_LONG).show();
                }

                else {
                    uploadPost();
                }
            }
        });

    }

    /**
     * 上传数据
     */
    private void uploadPost() {
        SpUtil sp = new SpUtil(this);
        final String auth = sp.getStringValue(Const.AUTH);
        final String uid = sp.getStringValue(Const.UID);
        String city="";
        if(iDriveApplication.app().getLocation() != null){
            city = iDriveApplication.app().getLocation().getCity();
        }else {
            city=HPCITY;
        }

        if (auth != null && !auth.equals("") && uid != null && !uid.equals("")) {
            preProcessorMedia();

            try {
                HttpPostUtil post = new HttpPostUtil(Constant.UPLOAD_DATA);
                post.addTextParameter("token", auth);
                post.addTextParameter("uid", uid);
                post.addTextParameter("mobile", phonenum_help.getText().toString());
                post.addTextParameter("timeliness", txt_time.getText().toString());
                post.addTextParameter("addr", geo.getText().toString());
                post.addTextParameter("content", content_help.getText().toString());
                post.addTextParameter("filetype", "1");
                post.addTextParameter("city",city);

                        for (int i = 0; i < mImageList.size() - 1; i++) {
                            post.addFileParameter("filedata" + String.valueOf(i),
                                    new File(mImageList.get(i).getPath()));
                        }
                post.setPostCallback(new HttpPostUtil.PostCallback() {
                    @Override
                    public void onProgressStart() {
                        showProcessDialog();
                    }

                    @Override
                    public void onProgressUpdate(Integer i) {
                        updateProgressDialog(i);
                    }

                    @Override
                    public void onProgressEnd(String s) {
                        try {
                            JSONObject obj = new JSONObject(s);
                            dismissProcessDialog();
                            if (obj.getInt("code") == 0) {
                                Toast.makeText(HelpActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(HelpActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            dismissProcessDialog();
                            Toast.makeText(HelpActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });
                post.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //请先登录
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_NOT_GOTO_HOMEPAGE, true);
            startActivity(intent);
        }
    }

    /**
     * 显示ProcessDialog
     */
    private void showProcessDialog() {
        mProgressDialog = null;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("发送中...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    /**
     * 上传 的ProcessDialog
     * @param i
     */
    private void updateProgressDialog(Integer i) {
        mProgressDialog.setMessage("已上传" + String.valueOf(i) + "%");
    }

    private void dismissProcessDialog() {
        mProgressDialog.dismiss();
    }

    /**
     * 数据更新
     */
    private void preProcessorMedia() {
        WindowManager wm = this.getWindowManager();
        int screen_width = wm.getDefaultDisplay().getWidth();
        int screen_height = wm.getDefaultDisplay().getHeight();
                for (int i = 0; i < mImageList.size() - 1; i++) {
                    String path = mImageList.get(i).getPath();
                    int degree = ImageUtil.readPictureDegree(path);
                    Bitmap bitmap = ImageUtil.decodeSampledBitmapFromPath(path,
                            screen_height / 2, screen_width / 2);
                    if (degree != 0) {
                        bitmap = ImageUtil.rotateImageView(degree, bitmap);
                    }
                    String p = this.getCacheDir().getAbsolutePath()
                            + "/" + path.substring(path.lastIndexOf("/") + 1);
                    try {
                        ImageUtil.storeImage(bitmap, p);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    mImageList.get(i).setPath(p);
                }
    }

    private void showPhotoPopupView(){
        if(popPhotoDialog == null){
            popPhotoDialog = new Dialog(this, R.style.popFromBottomdialog);
            View contentView = LayoutInflater.from(this).inflate(R.layout.popupview_photo_help, null);
            popPhotoDialog.setContentView(contentView);
            Window dialogWindow = popPhotoDialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.FILL_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setAttributes(lp);
            RelativeLayout btn_takephoto= (RelativeLayout) contentView.findViewById(R.id.btn_takephoto);
            btn_takephoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hCurrentPhotoFile = new File(InitUtil
                            .getImageCachePath(HelpActivity.this), "temp.png");
                    Intent it_camera = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    it_camera.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(hCurrentPhotoFile));
                    startActivityForResult(it_camera,
                            Const.REQUEST_CODE_IMAGE_CAPTURE);
                    popPhotoDialog.dismiss();
                }
            });

            RelativeLayout btn_takephotofromcamara= (RelativeLayout) contentView.findViewById(R.id.btn_photo_fromcamara);
            btn_takephotofromcamara.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pickImage();
                    popPhotoDialog.dismiss();
                }
            });
            RelativeLayout btn_cancel= (RelativeLayout) contentView.findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popPhotoDialog.dismiss();
                }
            });

        }else {
            if(popPhotoDialog.isShowing()){
                popPhotoDialog.dismiss();
                popPhotoDialog = null;
                return;
            }
        }
        popPhotoDialog.show();
    }

    /**
     * 使用第三方框架，添加多张图片
     */
    private void pickImage() {

        int selectedMode = MultiImageSelectorActivity.MODE_MULTI;
        boolean showCamera = true;
        int maxNum = 3;
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);// 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxNum);// 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectedMode);// 选择模式
        intent.putExtra(ImagePickerActivity.FROM_WHICH_ACTIVITY, PostEditActivity.class.toString());
        if (getCheckedImagePathList() != null && getCheckedImagePathList().size() > 0) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, getCheckedImagePathList());// 默认选择
        }
        startActivityForResult(intent, Constant.POST_EDIT_REQUEST_PICKER_CODE);
    }

    /**
     * 时长的设置
     */
    private void showDialog() {
        if(popDialog == null){
            popDialog = new Dialog(this, R.style.popFromBottomdialog);
            View contentView = LayoutInflater.from(this).inflate(R.layout.popupview_time, null);
            popDialog.setContentView(contentView);
            Window dialogWindow = popDialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.FILL_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setAttributes(lp);
            final TextView txt_time= (TextView) this.findViewById(R.id.txt_time);
            RelativeLayout btn_twohours= (RelativeLayout) contentView.findViewById(R.id.btn_twohours);
            btn_twohours.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txt_time.setText("2小时");
                    popDialog.dismiss();
                }
            });
            RelativeLayout btn_fourhours= (RelativeLayout) contentView.findViewById(R.id.btn_fourhours);
            btn_fourhours.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popDialog.dismiss();
                    txt_time.setText("4小时");
                }
            });
            RelativeLayout btn_sixhours= (RelativeLayout) contentView.findViewById(R.id.btn_sixhours);
            btn_sixhours.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popDialog.dismiss();
                    txt_time.setText("6小时");
                }
            });
            RelativeLayout btn_cancel= (RelativeLayout) contentView.findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popDialog.dismiss();
                }
            });

        }else {
            if(popDialog.isShowing()){
                popDialog.dismiss();
                popDialog = null;
                return;
            }
        }
        popDialog.show();
    }

    private ArrayList<String> getCheckedImagePathList() {
        ArrayList<String> list = new ArrayList<String>();
        for (MediaEntity entity : mImageList) {
            if (entity.getId() != -1) {
                list.add(entity.getPath());
            }
        }
        return list;
    }

    /**
     * 获取返回的数据
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case  Const.REQUEST_CODE_IMAGE_CAPTURE:
                    PHOTOFLAG="btn_takephoto";
                    if (hCurrentPhotoFile != null) {
                        // 方法1：读取缓存图片
                        Uri uri = Uri.fromFile(hCurrentPhotoFile);
                        path = CropFileUtils.getPath(this, uri);
                        initGridView();
                    }

                case Constant.POST_EDIT_REQUEST_PICKER_CODE:
                        mImageList.clear();
                    PHOTOFLAG="btn_takephotofromcamara";
                        mImageList = data.getParcelableArrayListExtra(CHECKED_MEDIA_LIST);
//                        com.trs.util.log.Log.e("WLH", "PostEditActivity select image size:" + mImageList.size());
                        //增加一个add按钮
                        MediaEntity entity = new MediaEntity();
                        entity.setId(-1);
                        mImageList.add(entity);
                        initGridView();
                    break;
                default:
                    break;
            }
        } else if (RESULT_CANCELED == resultCode) {
            com.trs.util.log.Log.e("", "Picker Cancel");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * tGridView布局
     */
    private void initGridView() {
        RelativeLayout rl_takephoto= (RelativeLayout) this.findViewById(R.id.rl_take_photo);
        rl_takephoto.setVisibility(View.GONE);
        mPostGridView = (GridView) findViewById(R.id.post_grid_view);
        mPostGridView.setVisibility(View.VISIBLE);
        mPostGridViewAdapter = new PostGridViewAdapter();
        mPostGridView.setAdapter(mPostGridViewAdapter);
        mPostGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    //启动PickerActivity
                    pickImage();

            }
        });
    }
    private class PostGridViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public MediaEntity getItem(int i) {
            return mImageList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.grid_item_post, null);
                AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                        android.view.ViewGroup.LayoutParams.FILL_PARENT,
                        ScreenUtil.getScreenWidth(HelpActivity.this) / 4);
                convertView.setLayoutParams(param);
            }

            ImageView post_thumbnail = (ImageView) convertView.findViewById(R.id.post_thumbnail);
            RelativeLayout post_thumbnail_bg = (RelativeLayout) convertView.findViewById(R.id.post_thumbnail_bg);

            MediaEntity entity = getItem(position);
            if (entity.getId() == -1) {
                post_thumbnail_bg.setVisibility(View.VISIBLE);
                post_thumbnail.setVisibility(View.INVISIBLE);
            } else {
                post_thumbnail_bg.setVisibility(View.INVISIBLE);
                post_thumbnail.setVisibility(View.VISIBLE);
                //UIL方式加载
//                String path = "file://" + entity.getPath();
//                new ImageDownloader.Builder().
//                        setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).
//                        build(path, post_thumbnail).start();
                //自定义方式加载
                if (PHOTOFLAG.equals("btn_takephoto")){
                    Bitmap bitmap = ImageUtil.decodeSampledBitmapFromPath(path,
                            ScreenUtil.getScreenWidth(HelpActivity.this) / 4,
                            ScreenUtil.getScreenWidth(HelpActivity.this) / 4);
                    post_thumbnail.setImageBitmap(bitmap);
                }else{
                    Bitmap bitmap = ImageUtil.decodeSampledBitmapFromPath(entity.getPath(),
                            ScreenUtil.getScreenWidth(HelpActivity.this) / 4,
                            ScreenUtil.getScreenWidth(HelpActivity.this) / 4);
                    post_thumbnail.setImageBitmap(bitmap);
                }
            }

            return convertView;
        }
    }

}
