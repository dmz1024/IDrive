package com.ccpress.izijia.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.portal.LoginActivity;
import com.ccpress.izijia.constant.Const;
import com.ccpress.izijia.entity.MediaEntity;
import com.ccpress.izijia.fragment.HomeTabFragment;
import com.ccpress.izijia.iDriveApplication;
import com.ccpress.izijia.util.*;
import com.ccpress.izijia.view.WheelView;
import com.froyo.commonjar.utils.SpUtil;
import com.trs.app.TRSFragmentActivity;
import com.trs.util.StringUtil;
import com.trs.util.log.Log;
import com.trs.wcm.LoadWCMJsonTask;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Wu Jingyu
 * Date: 2015/5/14
 * Time: 10:35
 */
public class PostEditActivity extends TRSFragmentActivity {
    public static final String CHECKED_MEDIA_LIST = "CheckedMediaList";
    private int mMediaType;
    private Dialog popDialog;
    private GridView mPostGridView;
    private PostGridViewAdapter mPostGridViewAdapter;
    private ImageView mVidThumbnail;
    private ArrayList<MediaEntity> mImageList = new ArrayList<MediaEntity>();
    private String videoSavePath;
    private String videoThumbnailPath;
    private RelativeLayout mVidPlayLayout;
    private VideoView mVideoView;
    private long videoDuration;
    private static VideoReplayHandler mVideoReplayHandler;
    private TextView tag_value_view;
    private String tag_value;
    private ArrayList<Tag> tag_list = new ArrayList<Tag>();
    private EditText post_edit;
    public  static String POSTCITY="CITY";
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);
//        String n = android.os.Build.MODEL;
//        String n1 = Build.BRAND;
//        String n2 = Build.DEVICE;
//        String n3 = Build.MANUFACTURER;
//        String n4 = Build.BOARD;
//        String n5 = Build.HARDWARE;
//        String n6 = Build.PRODUCT;
//        String n7 = Build.SERIAL;
//        String n8 = Build.TYPE;
//        String n9 = Build.USER;
//        String n10 = Build.HOST;

        mMediaType = getIntent().getIntExtra(MediaPickerActivity.MEDIA_TYPE, MediaPickerActivity.MEDIA_TYPE_IMG);
        if (mMediaType == MediaPickerActivity.MEDIA_TYPE_IMG) {
            mImageList = getIntent().getParcelableArrayListExtra(CHECKED_MEDIA_LIST);
            //增加一个add按钮
            MediaEntity entity = new MediaEntity();
            entity.setId(-1);
            mImageList.add(entity);
        }
        if (mMediaType == MediaPickerActivity.MEDIA_TYPE_VID) {
            videoSavePath = getIntent().getStringExtra(RecordVideoActivity.VIDEO_SAVE_PATH);
        }

        initView();
        mVideoReplayHandler = new VideoReplayHandler();
        loadTag();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mPostGridViewAdapter != null) {
            mPostGridViewAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 选择多张图片方法
     */
    private void PickImage() {

        int selectedMode = MultiImageSelectorActivity.MODE_MULTI;
        boolean showCamera = true;
        int maxNum = 9;
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
     * 布局控件设置
     */
    private void initView() {
        post_edit = (EditText) findViewById(R.id.post_edit);
        mPostGridView = (GridView) findViewById(R.id.post_grid_view);
        mVidThumbnail = (ImageView) findViewById(R.id.vid_thumbnail);
        mVidPlayLayout = (RelativeLayout) findViewById(R.id.video_play_layout);

        if (mMediaType == MediaPickerActivity.MEDIA_TYPE_IMG) {
            mPostGridView.setVisibility(View.VISIBLE);
            mVidThumbnail.setVisibility(View.GONE);
            mPostGridViewAdapter = new PostGridViewAdapter();
            mPostGridView.setAdapter(mPostGridViewAdapter);
            mPostGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    /*if (mImageList.get(i).getId() == -1) {
                        //启动PickerActivity
                        PickImage();
                    }*/
                    PickImage();
                }
            });
        }

        if (mMediaType == MediaPickerActivity.MEDIA_TYPE_VID) {
            mPostGridView.setVisibility(View.GONE);
            mVidThumbnail.setVisibility(View.VISIBLE);
            //增加设置视频缩略图的代码
            mVidThumbnail.setImageBitmap(ThumbnailUtils.createVideoThumbnail(videoSavePath,
                    MediaStore.Images.Thumbnails.MICRO_KIND));
            mVidThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    onPlayVideo();
                    //采用系统默认播放器播放
                    Uri uri = Uri.parse("file://" + videoSavePath);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "video/*");
                    startActivity(intent);
                }
            });
            mVidPlayLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onStopVideo();
                }
            });
            mVideoView = (VideoView) findViewById(R.id.video_play_view);

            int height = getResources().getDimensionPixelOffset(R.dimen.size300);
            float f = (float) ScreenUtil.getScreenWidth(PostEditActivity.this)
                    / (float) ScreenUtil.getScreenHeight(PostEditActivity.this);
            int width = (int) (f * height);
            ViewGroup.LayoutParams pa = mVideoView.getLayoutParams();
            pa.width = width;

            mVideoView.setVideoPath(videoSavePath);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoSavePath);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            videoDuration = Long.parseLong(duration);
        }

        //初始化Tag Layout
        RelativeLayout tag_layout = (RelativeLayout) findViewById(R.id.post_tag);
        tag_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        tag_value_view = (TextView) findViewById(R.id.tag_value);

        //初始化上传发送
        TextView txt_send = (TextView) findViewById(R.id.txt_send);
        txt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(post_edit.getText().toString())) {
                    Toast.makeText(PostEditActivity.this, "请说点您的感受吧", Toast.LENGTH_LONG).show();
                } else {
                    uploadPost();
                }
            }
        });
    }
    /**
     * 开始录制
     */
    private void onPlayVideo() {
        mVidPlayLayout.setVisibility(View.VISIBLE);
        mVideoReplayHandler.sendEmptyMessageDelayed(0, videoDuration);
        mVideoView.resume();
        mVideoView.start();
        mVideoView.requestFocus();
    }

    /**
     * 停止录制
     */
    private void onStopVideo() {
        mVideoView.stopPlayback();
           mVidPlayLayout.setVisibility(View.GONE);
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
                        ScreenUtil.getScreenWidth(PostEditActivity.this)/4);
                convertView.setLayoutParams(param);
            }

            ImageView post_thumbnail = (ImageView) convertView.findViewById(R.id.post_thumbnail);
            RelativeLayout post_thumbnail_bg = (RelativeLayout) convertView.findViewById(R.id.post_thumbnail_bg);
            Log.e("位置啊位置", String.valueOf(position));
            MediaEntity entity = getItem(position);
            if (entity.getId() == -1) {
                post_thumbnail_bg.setVisibility(View.VISIBLE);
                post_thumbnail.setVisibility(View.INVISIBLE);
            } else {
                post_thumbnail_bg.setVisibility(View.INVISIBLE);
                post_thumbnail.setVisibility(View.VISIBLE);
                Log.e("图片地址", entity.getPath());
                //UIL方式加载
//                String path = "file://" + entity.getPath();
//                new ImageDownloader.Builder().
//                        setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC).
//                        build(path, post_thumbnail).start();
                //自定义方式加载
                Bitmap bitmap = ImageUtil.decodeSampledBitmapFromPath(entity.getPath(),
                        ScreenUtil.getScreenWidth(PostEditActivity.this) / 4,
                        ScreenUtil.getScreenWidth(PostEditActivity.this) / 4);

                post_thumbnail.setImageBitmap(bitmap);
            }

            return convertView;
        }
    }

    /**
     * 图片集合
     * @return
     */
    private ArrayList<String> getCheckedImagePathList() {
        ArrayList<String> list = new ArrayList<String>();
        for (MediaEntity entity : mImageList) {
            if (entity.getId() != -1) {
                list.add(entity.getPath());
            }
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case Constant.POST_EDIT_REQUEST_PICKER_CODE:
                    if (mMediaType == MediaPickerActivity.MEDIA_TYPE_IMG) {
                        mImageList.clear();
                        mImageList = data.getParcelableArrayListExtra(CHECKED_MEDIA_LIST);
                        Log.e("WLH", "PostEditActivity select image size:" + mImageList.size());
                        //增加一个add按钮
                        MediaEntity entity = new MediaEntity();
                        entity.setId(-1);
                        mImageList.add(entity);
                    }
                    break;
                default:
                    break;
            }
        } else if (RESULT_CANCELED == resultCode) {
            Log.e("", "Picker Cancel");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 显示底部Dialog
     */
    private void showDialog() {
        if (popDialog == null) {
            popDialog = new Dialog(this, R.style.popFromBottomdialog);
            View contentView = LayoutInflater.from(this).inflate(R.layout.popupview_tag, null);
            popDialog.setContentView(contentView);
            Window dialogWindow = popDialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.FILL_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setAttributes(lp);

            TextView mCancel = (TextView) contentView.findViewById(R.id.txt_cancel);
            TextView mOK = (TextView) contentView.findViewById(R.id.txt_confirm);
            WheelView wheelView = (WheelView) contentView.findViewById(R.id.wheel_view_wv);

            ArrayList<String> chooseitems = new ArrayList<String>();
            for (Tag tag : tag_list) {
                chooseitems.add(tag.title);
            }

            wheelView.setOffset(2);
            wheelView.setItems(chooseitems);
            wheelView.setSeletion(3);
            wheelView.setOnWheelViewListener(new com.ccpress.izijia.view.WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                    android.util.Log.e("WLH", "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
                    tag_value = item;
                }
            });

            mOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tag_value_view.setText(tag_value);
                    popDialog.dismiss();
                }
            });
            mCancel.setOnClickListener(new View.OnClickListener() {//取消
                @Override
                public void onClick(View view) {
                    popDialog.dismiss();
                }
            });
        } else {
            if (popDialog.isShowing()) {
                popDialog.dismiss();
                popDialog = null;
                return;
            }
        }
        popDialog.show();
    }

    private class VideoReplayHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0 && mVidPlayLayout.getVisibility() == View.VISIBLE) {
                mVideoReplayHandler.sendEmptyMessageDelayed(0, videoDuration);
                mVideoView.resume();
                mVideoView.start();
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 上传照片
     */
    private void uploadPost() {
        SpUtil sp = new SpUtil(this);
        final String auth = sp.getStringValue(Const.AUTH);
        final String uid = sp.getStringValue(Const.UID);
        String city="";
              if(iDriveApplication.app().getLocation() != null){
                      city = iDriveApplication.app().getLocation().getCity();
                  }else {
             city=POSTCITY;
               }

        if (auth != null && !auth.equals("") && uid != null && !uid.equals("")) {
            preProcessorMedia();

            try {
                HttpPostUtil post = new HttpPostUtil(Constant.UPLOAD_DATA);
                post.addTextParameter("token", auth);
                post.addTextParameter("uid", uid);
                post.addTextParameter("tag", getTagValue());
                post.addTextParameter("content", post_edit.getText().toString());
                post.addTextParameter("filetype", String.valueOf(mMediaType));
                post.addTextParameter("city", city);

                switch (mMediaType) {
                    case MediaPickerActivity.MEDIA_TYPE_IMG:
                        for (int i = 0; i < mImageList.size() - 1; i++) {
                            post.addFileParameter("filedata" + String.valueOf(i),
                                    new File(mImageList.get(i).getPath()));
                        }
                        break;
                    case MediaPickerActivity.MEDIA_TYPE_VID:
                        post.addFileParameter("filedata1", new File(videoSavePath));
                        post.addFileParameter("filedata2", new File(videoThumbnailPath));
                        break;
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
                                Toast.makeText(PostEditActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent();
                                intent.setAction(Constant.INTERACT_LIST_UPDATE);
                                PostEditActivity.this.sendBroadcast(intent);
                                finish();
                            } else {
                                Toast.makeText(PostEditActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            dismissProcessDialog();
                            Toast.makeText(PostEditActivity.this, "上传失败", Toast.LENGTH_LONG).show();
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
     * 获取标签数据
     */
    private void loadTag() {
        LoadWCMJsonTask task = new LoadWCMJsonTask(this) {
            @Override
            public void onDataReceived(String result, boolean isCache) throws Exception {
                JSONObject obj = new JSONObject(result);
                if (obj.has("code") && obj.getString("code").equals("0")) {
                    JSONArray array = obj.getJSONArray("datas");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject tag_obj = array.getJSONObject(i);
                        Tag tag = new Tag();
                        tag.title = tag_obj.getString("title");
                        tag.tid = tag_obj.getString("tid");
                        tag_list.add(tag);
                    }
                } else {
                    if (obj.has("message")) {
                        Log.v("Library_TabFragment", obj.getString("message"));
                    }
                    Toast.makeText(PostEditActivity.this,
                            "标签下载失败",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(PostEditActivity.this, com.trs.mobile.R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            }
        };

        task.start(Constant.INTERACT_URL_BASE
                + Constant.INTERACT_TAGS_URL
                + "&cid=0");
    }

    /**
     * 标签
     */
    private class Tag {
        public String title;
        public String tid;
    }

    /**
     * 获取值
     * @return
     */
    private String getTagValue() {
        String value = tag_value_view.getText().toString();
        if (value.equals("默认")) {
            return "默认";
        } else {
            return value;
        }
    }

    /**
     * 媒体类型及处理
     */
    private void preProcessorMedia() {
        WindowManager wm = this.getWindowManager();
        int screen_width = wm.getDefaultDisplay().getWidth();
        int screen_height = wm.getDefaultDisplay().getHeight();
        switch (mMediaType) {
            case MediaPickerActivity.MEDIA_TYPE_IMG:
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
                break;
            case MediaPickerActivity.MEDIA_TYPE_VID:
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoSavePath,
                        MediaStore.Images.Thumbnails.MINI_KIND);
                File file = new File(videoSavePath);
                String video_name = file.getName();
                String thumbnail_name = video_name.substring(0, video_name.indexOf("."));
                videoThumbnailPath = this.getCacheDir().getAbsolutePath()
                        + "/" + thumbnail_name + ".jpg";
                try {
                    ImageUtil.storeImage(bitmap, videoThumbnailPath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 发送的  ProcessDialog
     */
    private void showProcessDialog() {
        mProgressDialog = null;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("发送中...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void updateProgressDialog(Integer i) {
        mProgressDialog.setMessage("已上传" + String.valueOf(i) + "%");
    }

    private void dismissProcessDialog() {
        mProgressDialog.dismiss();
    }
}
