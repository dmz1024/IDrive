package com.ccpress.izijia.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.libcore.io.DiskLruCache;
import com.ccpress.izijia.util.ImageUtil;
import com.ccpress.izijia.util.ScreenUtil;
import com.trs.app.TRSFragmentActivity;
import com.ccpress.izijia.R;
import com.ccpress.izijia.entity.MediaEntity;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by Wu Jingyu
 * Date: 2015/4/29
 * Time: 16:12
 */
public class MediaPickerActivity extends TRSFragmentActivity {
    public static final String MEDIA_TYPE = "MediaPickerActivityMediaType";
    public static final int MEDIA_TYPE_IMG = 1;
    public static final int MEDIA_TYPE_VID = 2;
    public static final String HAS_PHOTO_BTN = "HasPhotoBtn";
    private static final int MAX_SELECT_NUMBER = 9;
    private int mMediaType;
    private Cursor mCursor;
    private ArrayList<MediaEntity> mMediaList = new ArrayList<MediaEntity>();
    private GridView mGridView;
    private MediaWallAdapter mGridViewAdapter;
    private int mSelectCount = 0;
    private TextView mSelectCountView;
    private boolean hasPhotoBtn = false;
    public static final String CHECKED_MEDIA_PATH_LIST = "CheckedMediaPathList";
    public static final String SELECT_COUNT = "SelectCount";
    public static final String FROM_WHICH_ACTIVITY = "fromAct";
    private String fromAct;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediapicker);

        Intent intent = getIntent();
        mMediaType = intent.getIntExtra(MEDIA_TYPE, 0);
        hasPhotoBtn = intent.getBooleanExtra(HAS_PHOTO_BTN, false);
        mSelectCount = intent.getIntExtra(SELECT_COUNT, 0);
        fromAct = intent.getStringExtra(FROM_WHICH_ACTIVITY);

        initMediaData();
        ArrayList<String> li = intent.getStringArrayListExtra(CHECKED_MEDIA_PATH_LIST);
        if (li != null) {
            checkMeidaList(li);
        }

        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGridViewAdapter.fluchCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出程序时结束所有任务
        mGridViewAdapter.cancelAllTasks();
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.picker_grid_view);
        mGridViewAdapter = new MediaWallAdapter(this);
        mGridView.setAdapter(mGridViewAdapter);
        mGridView.setOnItemClickListener(new MediaPickerGridViewListener());

        mSelectCountView = (TextView) findViewById(R.id.txt_select_number);
        mSelectCountView.setText("(" + mSelectCount + ")");

        RelativeLayout btn_picker_done = (RelativeLayout) findViewById(R.id.btn_picker_done);
        btn_picker_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishThisActivity(false);
            }
        });

        ImageView btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishThisActivity(true);
            }
        });
    }

    private void initMediaData() {
        switch (mMediaType) {
            case MEDIA_TYPE_IMG:
                initImgData();
                break;
            case MEDIA_TYPE_VID:
                initVidData();
                break;
            default:
                break;
        }
    }

    private void initImgData() {
        mMediaList.clear();
        if (hasPhotoBtn) {
            MediaEntity photoBtn = new MediaEntity();
            photoBtn.setId(-1);
            photoBtn.setIsChecked(false);
            mMediaList.add(photoBtn);
        }

        String columns[] = new String[]{
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATE_TAKEN};

        try {
            mCursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columns, null, null, null);
            if (mCursor == null) {
                return;
            }

            int mImagePathColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int mImageIDColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int mImageTitleColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
            int mImageNameColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int mImageContentTypeColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
            int mImageCreateTimeColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);

            ArrayList<MediaEntity> tmpList = new ArrayList<MediaEntity>();
            if (mCursor.moveToFirst()) {
                do {
                    MediaEntity entity = new MediaEntity();
                    entity.setId(mCursor.getInt(mImageIDColumnIndex));
                    entity.setName(mCursor.getString(mImageNameColumnIndex));
                    entity.setTitle(mCursor.getString(mImageTitleColumnIndex));
                    entity.setPath(mCursor.getString(mImagePathColumnIndex));
                    entity.setCreatetime(mCursor.getLong(mImageCreateTimeColumnIndex));
                    entity.setContenttype(mCursor.getString(mImageContentTypeColumnIndex));
                    tmpList.add(entity);
                } while (mCursor.moveToNext());
                Collections.sort(tmpList);
                mMediaList.addAll(tmpList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
    }

    private void initVidData() {
        mMediaList.clear();
        if (hasPhotoBtn) {
            MediaEntity videoBtn = new MediaEntity();
            videoBtn.setId(-1);
            videoBtn.setIsChecked(false);
            mMediaList.add(videoBtn);
        }

        String columns[] = new String[]{
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DATE_TAKEN};

        try {
            mCursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    columns, null, null, null);
            if (mCursor == null) {
                return;
            }

            int mPathColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            int mIDColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int mTitleColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
            int mNameColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int mContentTypeColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE);
            int mCreateTimeColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN);

            ArrayList<MediaEntity> tmpList = new ArrayList<MediaEntity>();
            if (mCursor.moveToFirst()) {
                do {
                    MediaEntity entity = new MediaEntity();
                    entity.setId(mCursor.getInt(mIDColumnIndex));
                    entity.setName(mCursor.getString(mNameColumnIndex));
                    entity.setTitle(mCursor.getString(mTitleColumnIndex));
                    entity.setPath(mCursor.getString(mPathColumnIndex));
                    entity.setCreatetime(mCursor.getLong(mCreateTimeColumnIndex));
                    entity.setContenttype(mCursor.getString(mContentTypeColumnIndex));
                    tmpList.add(entity);
                } while (mCursor.moveToNext());
                Collections.sort(tmpList);
                mMediaList.addAll(tmpList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
    }

    private class MediaWallAdapter extends BaseAdapter {
        private Set<BitmapWorkerTask> taskCollection;
        private LruCache<String, Bitmap> mMemoryCache;
        private DiskLruCache mDiskLruCache;
        private GridView mPhotoWall;

        public MediaWallAdapter(Context context) {
            super();
            mPhotoWall = mGridView;
            taskCollection = new HashSet<BitmapWorkerTask>();

            //设置内存缓存
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            int cacheSize = maxMemory / 8;
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount();
                }
            };

            //设置硬盘缓存
            try {
                // 获取图片缓存路径
                File cacheDir = getDiskCacheDir(context, "thumb");
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }
                // 创建DiskLruCache实例，初始化缓存数据
                mDiskLruCache = DiskLruCache
                        .open(cacheDir, getAppVersion(context), 1, 10 * 1024 * 1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getCount() {
            return mMediaList.size();
        }

        @Override
        public MediaEntity getItem(int i) {
            return mMediaList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.grid_item_media, null);
            } else {
                view = convertView;
            }

            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    android.view.ViewGroup.LayoutParams.FILL_PARENT,
                    ScreenUtil.getScreenWidth(MediaPickerActivity.this) / 3);
            view.setLayoutParams(param);

            final MediaEntity entity = mMediaList.get(position);
            final ImageView select = (ImageView) view.findViewById(R.id.btn_media_check);
            ImageView thumbnail = (ImageView) view.findViewById(R.id.image_thumbnail);
            ImageView videoIcon = (ImageView) view.findViewById(R.id.video_icon);
            final RelativeLayout shelter_picker = (RelativeLayout) view.findViewById(R.id.shelter_picker);

            if (entity.isChecked()) {
                shelter_picker.setVisibility(View.VISIBLE);
                select.setImageResource(R.drawable.btn_media_checked);
            } else {
                shelter_picker.setVisibility(View.GONE);
                select.setImageResource(R.drawable.btn_media_unchecked);
            }
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (entity.isChecked()) {
                        entity.setIsChecked(false);
                        select.setImageResource(R.drawable.btn_media_unchecked);
                        shelter_picker.setVisibility(View.GONE);
                        mSelectCount--;
                    } else {
                        if (mSelectCount != MAX_SELECT_NUMBER) {
                            entity.setIsChecked(true);
                            select.setImageResource(R.drawable.btn_media_checked);
                            shelter_picker.setVisibility(View.VISIBLE);
                            mSelectCount++;
                        } else {
                            Toast.makeText(MediaPickerActivity.this,
                                    getResources().getText(R.string.most_ten_items), Toast.LENGTH_SHORT).show();
                        }
                    }
                    mSelectCountView.setText("(" + mSelectCount + ")");
                }
            });

            String url = entity.getPath();
            thumbnail.setTag(url);
            if (entity.getId() == -1) {
                select.setVisibility(View.GONE);
                videoIcon.setVisibility(View.GONE);
                if (mMediaType == MEDIA_TYPE_IMG) {
                    thumbnail.setImageResource(R.drawable.btn_photo_picker);
                }
                if (mMediaType == MEDIA_TYPE_VID) {
                    thumbnail.setImageResource(R.drawable.btn_video_picker);
                }
            } else {
                if (mMediaType == MEDIA_TYPE_VID) {
                    videoIcon.setVisibility(View.VISIBLE);
                }
                select.setVisibility(View.VISIBLE);
                thumbnail.setImageBitmap(null);
                loadBitmaps(thumbnail, url);
            }

            return view;
        }

        public void loadBitmaps(ImageView imageView, String imageUrl) {
            try {
                Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
                if (bitmap == null) {
                    BitmapWorkerTask task = new BitmapWorkerTask();
                    taskCollection.add(task);
                    task.execute(imageUrl);
                } else {
                    if (imageView != null && bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Bitmap getBitmapFromMemoryCache(String key) {
            return mMemoryCache.get(key);
        }

        private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
            private String imageUrl;

            @Override
            protected Bitmap doInBackground(String... params) {
                imageUrl = params[0];
                FileDescriptor fileDescriptor = null;
                FileInputStream fileInputStream = null;
                DiskLruCache.Snapshot snapShot = null;
                try {
                    // 生成图片URL对应的key
                    final String key = hashKeyForDisk(imageUrl);
                    // 查找key对应的缓存
                    snapShot = mDiskLruCache.get(key);
                    if (snapShot == null) {
                        // 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
                        DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                        if (editor != null) {
                            OutputStream outputStream = editor.newOutputStream(0);
//                            if (downloadUrlToStream(imageUrl, outputStream))
                            if (loadLocalImage(imageUrl, outputStream)) {
                                editor.commit();
                            } else {
                                editor.abort();
                            }
                        }
                        // 缓存被写入后，再次查找key对应的缓存
                        snapShot = mDiskLruCache.get(key);
                    }
                    if (snapShot != null) {
                        fileInputStream = (FileInputStream) snapShot.getInputStream(0);
                        fileDescriptor = fileInputStream.getFD();
                    }
                    // 将缓存数据解析成Bitmap对象
                    Bitmap bitmap = null;
                    if (fileDescriptor != null) {
                        bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    }
                    if (bitmap != null) {
                        // 将Bitmap对象添加到内存缓存当中
                        addBitmapToMemoryCache(params[0], bitmap);
                    }
                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fileDescriptor == null && fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                // 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
                ImageView imageView = (ImageView) mPhotoWall.findViewWithTag(imageUrl);
                if (imageView != null && bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
                taskCollection.remove(this);
            }

            private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
                HttpURLConnection urlConnection = null;
                BufferedOutputStream out = null;
                BufferedInputStream in = null;
                try {
                    final URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
                    out = new BufferedOutputStream(outputStream, 8 * 1024);
                    int b;
                    while ((b = in.read()) != -1) {
                        out.write(b);
                    }
                    return true;
                } catch (final IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    try {
                        if (out != null) {
                            out.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            private boolean loadLocalImage(String urlString, OutputStream outputStream) {
                Bitmap btm = null;
                switch (mMediaType) {
                    case MEDIA_TYPE_IMG:
                        btm = ImageUtil.decodeSampledBitmapFromPath(urlString, 100, 100);
//                        btm = ImageUtils.createImageThumbnail(urlString);
                        break;
                    case MEDIA_TYPE_VID:
                        btm = ThumbnailUtils.createVideoThumbnail(urlString,
                                MediaStore.Images.Thumbnails.MINI_KIND);
                        break;
                    default:
                        break;
                }

                if (btm == null) {
                    return false;
                }
                return btm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            }
        }

        public String hashKeyForDisk(String key) {
            String cacheKey;
            try {
                final MessageDigest mDigest = MessageDigest.getInstance("MD5");
                mDigest.update(key.getBytes());
                cacheKey = bytesToHexString(mDigest.digest());
            } catch (NoSuchAlgorithmException e) {
                cacheKey = String.valueOf(key.hashCode());
            }
            return cacheKey;
        }

        private String bytesToHexString(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        }

        private File getDiskCacheDir(Context context, String uniqueName) {
            String cachePath;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                cachePath = context.getExternalCacheDir().getPath();
            } else {
                cachePath = context.getCacheDir().getPath();
            }
            return new File(cachePath + File.separator + uniqueName);
        }

        private int getAppVersion(Context context) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                return info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return 1;
        }

        public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
            if (getBitmapFromMemoryCache(key) == null) {
                mMemoryCache.put(key, bitmap);
            }
        }

        public void fluchCache() {
            if (mDiskLruCache != null) {
                try {
                    mDiskLruCache.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancelAllTasks() {
            if (taskCollection != null) {
                for (BitmapWorkerTask task : taskCollection) {
                    task.cancel(false);
                }
            }
        }
    }

    private class MediaPickerGridViewListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (mMediaList.get(i).getId() == -1) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoUri = ImageUtil.createNewImageToMediaStore(MediaPickerActivity.this);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, Constant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            } else {
                if (mMediaType == MEDIA_TYPE_IMG) {
                    Intent intent = new Intent(MediaPickerActivity.this, ImageViewActivity.class);
                    intent.putStringArrayListExtra(CHECKED_MEDIA_PATH_LIST, getCheckedMediaPath());
                    intent.putExtra(SELECT_COUNT, mSelectCount);
                    intent.putExtra(ImageViewActivity.INIT_INDEX, hasPhotoBtn ? i - 1 : i);
                    startActivityForResult(intent, mMediaType);
                }
                if (mMediaType == MEDIA_TYPE_VID) {
                    Uri uri = Uri.parse("file://" + mMediaList.get(i).getPath());
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, mMediaList.get(i).getContenttype());
                    startActivity(intent);
                }
            }
        }
    }

    private ArrayList<String> getCheckedMediaPath() {
        ArrayList<String> list = new ArrayList<String>();
        for (MediaEntity en : mMediaList) {
            if (en.isChecked()) {
                list.add(en.getPath());
            }
        }
        return list;
    }

    private ArrayList<MediaEntity> getCheckedMediaList() {
        ArrayList<MediaEntity> list = new ArrayList<MediaEntity>();
        for (MediaEntity en : mMediaList) {
            if (en.isChecked()) {
                list.add(en);
            }
        }
        return list;
    }

    private void unselectMediaList() {
        for (MediaEntity en : mMediaList) {
            if (en.isChecked()) {
                en.setIsChecked(false);
            }
        }
    }

    private void checkMeidaList(ArrayList<String> pathList) {
        if (pathList.size() == 0) {
            unselectMediaList();
            return;
        }

        boolean flag = false;
        for (MediaEntity entity : mMediaList) {
            //去掉ID为1时候entity，因为这是一个按钮
            if (entity.getId() == -1) {
                continue;
            }
            for (String path : pathList) {
                if (entity.getPath().equals(path)) {
                    flag = true;
                    break;
                }
            }
            entity.setIsChecked(flag);
            flag = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case MEDIA_TYPE_IMG:
                    ArrayList<String> li = data.getStringArrayListExtra(CHECKED_MEDIA_PATH_LIST);
                    if (li == null) {
                        return;
                    }
                    checkMeidaList(li);
                    mSelectCount = data.getIntExtra(SELECT_COUNT, 0);
                    mSelectCountView.setText("(" + mSelectCount + ")");
                    mGridViewAdapter.notifyDataSetChanged();
                    break;
                case Constant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                    //添加新Image
                    MediaEntity entity = ImageUtil.getMediaEntityFromMediaStore(
                            MediaPickerActivity.this, photoUri);
                    if(entity != null){
                        entity.setIsChecked(true);
                        mMediaList.add(entity);
                        MediaEntity firstEntity = mMediaList.get(0);
                        mMediaList.remove(0);
                        Collections.sort(mMediaList);
                        mMediaList.add(0, firstEntity);
                        mGridViewAdapter.notifyDataSetChanged();
                        //更新计数器
                        mSelectCount++;
                        mSelectCountView.setText("(" + mSelectCount + ")");
                    }
                    break;
                default:
                    break;
            }
        } else if (RESULT_CANCELED == resultCode) {
            switch (requestCode) {
                case Constant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                    MediaEntity entity = ImageUtil.getMediaEntityFromMediaStore(
                            MediaPickerActivity.this, photoUri);
                    if(entity != null){
                        int result = ImageUtil.deleteNewImageFromMediaStore(this,
                                entity.getPath());
                        if (result == 0) {
                            Log.e("MediaPickerActivity", "Delete fail");
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void finishThisActivity(boolean isCancel) {
        if (!isCancel) {
            Intent intent = new Intent(MediaPickerActivity.this, PostEditActivity.class);
            intent.putExtra(MEDIA_TYPE, mMediaType);
            intent.putExtra(PostEditActivity.CHECKED_MEDIA_LIST, getCheckedMediaList());
            if (fromAct.equals(MainActivity.class.toString())) {
                startActivity(intent);
            }
            if (fromAct.equals(PostEditActivity.class.toString())) {
                setResult(RESULT_OK, intent);
            }
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
