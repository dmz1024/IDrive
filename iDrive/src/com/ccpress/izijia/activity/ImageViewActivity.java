package com.ccpress.izijia.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ccpress.izijia.R;
import com.ccpress.izijia.entity.MediaEntity;
import com.ccpress.izijia.fragment.ImageViewFragment;
import com.trs.app.TRSFragmentActivity;
import com.trs.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Wu Jingyu
 * Date: 2014/11/4
 * Time: 9:43
 */
public class ImageViewActivity extends TRSFragmentActivity {
    public static final String INIT_INDEX = "InitIndex";
    public static final String FOLDER_PATH = "folderPath";
    private Context mContext;
    private int init_index;
    private int selectCount;
    private ArrayList<MediaEntity> mMediaList = new ArrayList<MediaEntity>();
    private ArrayList<String> mCheckedMediaPathList;
    private Cursor mCursor;
    private final int mMaxSelectCount = 9;
    private TextView select_count;
    private ImageView btn_media_check;
    private String folderPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_image_view);
        findViewById(R.id.commit).setVisibility(View.GONE);

        init_index = getIntent().getIntExtra(INIT_INDEX, 0);
        selectCount = getIntent().getIntExtra(MediaPickerActivity.SELECT_COUNT, 0);
        mCheckedMediaPathList = getIntent().getStringArrayListExtra(MediaPickerActivity.CHECKED_MEDIA_PATH_LIST);
        folderPath = getIntent().getStringExtra(FOLDER_PATH);

        initImgData();
        initView();
    }

    private void initImgData() {
        String columns[] = new String[]{
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATE_ADDED};
        String selection = StringUtil.isEmpty(folderPath) ? null : columns[0]+" like '%"+folderPath+"%'";
        try {
            mCursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columns, selection, null, null);
            if (mCursor == null) {
                return;
            }

            int mImagePathColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int mImageIDColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int mImageTitleColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
            int mImageNameColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int mImageContentTypeColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
            int mImageCreateTimeColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);

            if (mCursor.moveToFirst()) {
                do {
                    String path = mCursor.getString(mImagePathColumnIndex);
                    MediaEntity entity = new MediaEntity();
                    entity.setId(mCursor.getInt(mImageIDColumnIndex));
                    entity.setName(mCursor.getString(mImageNameColumnIndex));
                    entity.setTitle(mCursor.getString(mImageTitleColumnIndex));
                    entity.setPath(path);
                    entity.setCreatetime(mCursor.getLong(mImageCreateTimeColumnIndex));
                    entity.setContenttype(mCursor.getString(mImageContentTypeColumnIndex));
                    entity.setIsChecked(isChecked(entity.getPath()));

                    if(!StringUtil.isEmpty(path) && !StringUtil.isEmpty(folderPath)){
                        if (path.lastIndexOf("/") == (folderPath+"/").lastIndexOf("/")){
                            mMediaList.add(entity);
                        }
                    }
                    else {
                        mMediaList.add(entity);
                    }


                } while (mCursor.moveToNext());
                Collections.sort(mMediaList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
    }

    private void initView() {
        ViewPager image_viewpager = (ViewPager) findViewById(R.id.image_viewpager);
        image_viewpager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));
        image_viewpager.setCurrentItem(init_index);

        select_count = (TextView) findViewById(R.id.txt_select_number);
        select_count.setText("(" + String.valueOf(selectCount) + ")");

        //��ʼ��ѡ��CheckBox
        btn_media_check = (ImageView) findViewById(R.id.btn_media_check);
        if (mMediaList.get(init_index).isChecked()) {
            btn_media_check.setImageResource(R.drawable.btn_media_checked);
        } else {
            btn_media_check.setImageResource(R.drawable.btn_media_unchecked);
        }
        btn_media_check.setTag(init_index);
        btn_media_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                MediaEntity entity = mMediaList.get(position);
                if (entity.isChecked()) {
                    entity.setIsChecked(false);
                    btn_media_check.setImageResource(R.drawable.btn_media_unchecked);
                    selectCount--;
                } else {
                    if (selectCount != mMaxSelectCount) {
                        entity.setIsChecked(true);
                        btn_media_check.setImageResource(R.drawable.btn_media_checked);
                        selectCount++;
                    } else {
                        Toast.makeText(mContext, getResources().getText(R.string.most_ten_items), Toast.LENGTH_SHORT).show();
                    }
                }
                select_count.setText("(" + selectCount + ")");
            }
        });

        //��ʼ��View Pager
        image_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                MediaEntity entity = mMediaList.get(i);
                if (entity.isChecked()) {
                    btn_media_check.setImageResource(R.drawable.btn_media_checked);
                } else {
                    btn_media_check.setImageResource(R.drawable.btn_media_unchecked);
                }
                btn_media_check.setTag(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        //��ʼ��ȷ����ť
        RelativeLayout btn_picker_done = (RelativeLayout) findViewById(R.id.btn_picker_done);
        btn_picker_done.setVisibility(View.VISIBLE);
        btn_picker_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishThisActivity(false);
            }
        });

        //��ʼ��ȡ����ť
        ImageView btn_cancel = (ImageView) findViewById(R.id.btn_back);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishThisActivity(true);
            }
        });
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MediaEntity entity = mMediaList.get(position);
            ImageViewFragment fragment = new ImageViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("Media_entity", entity.getPath());
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return mMediaList.size();
        }
    }

    private boolean isChecked(String pa) {
        for (String path : mCheckedMediaPathList) {
            if (path.equals(pa)) {
                return true;
            }
        }
        return false;
    }

    private void finishThisActivity(boolean isCancel) {
        if(!isCancel) {
            Intent intent = new Intent();
            intent.putStringArrayListExtra(MediaPickerActivity.CHECKED_MEDIA_PATH_LIST, getCheckedMediaPathList());
            intent.putExtra(MediaPickerActivity.SELECT_COUNT, selectCount);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private ArrayList<String> getCheckedMediaPathList() {
        ArrayList<String> list = new ArrayList<String>();
        for(MediaEntity entity: mMediaList){
            if(entity.isChecked()){
                list.add(entity.getPath());
            }
        }
        return list;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_DOWN) {
            finishThisActivity(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
