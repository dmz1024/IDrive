package com.ccpress.izijia.dfy.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.ImagePickerActivity;
import com.ccpress.izijia.activity.PostEditActivity;
import com.ccpress.izijia.dfy.util.CustomToast;
import com.ccpress.izijia.dfy.util.Util;
import com.ccpress.izijia.entity.MediaEntity;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;


import java.util.ArrayList;

/**
* Created by dengmingzhi on 16/5/2.
*/
public class UpLoadGridView extends GridView {
    private Activity mActivity;
    private int REQUEST_CODE = 0x1;
    private boolean isThis = false;
    private MyAdapter mAdapter;
    private int maxItem = 1;
    private ArrayList<String> mResults = new ArrayList<>();
    private ArrayList<MediaEntity> mImageList = new ArrayList<MediaEntity>();
//    private List<String> list = new ArrayList<String>();

    public UpLoadGridView(Context context) {
        this(context, null);
    }

    public UpLoadGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UpLoadGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }

    private void init() {
        setNumColumns(AUTO_FIT);
        setColumnWidth(Util.getWeight()/5);
        setGravity(Gravity.CENTER);
        setVerticalSpacing(20);
        setHorizontalSpacing(20);
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

    public void setMaxItem(int maxIntm) {
        this.maxItem = maxIntm;
    }

    public void setActivity(Activity a) {
        this.mActivity = a;
    }

    public void setAdapter() {
        setAdapter(mAdapter = new MyAdapter());
    }

    private void upLoadImage() {
        if (mActivity == null) {
            CustomToast.showToast("请设置Activity");
            return;
        }

        if (mAdapter == null) {
            CustomToast.showToast("请设置Adapter");
            return;
        }

        isThis = true;
        int selectedMode = MultiImageSelectorActivity.MODE_MULTI;
        Intent intent = new Intent(mActivity, ImagePickerActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);// 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxItem);// 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectedMode);// 选择模式
        intent.putExtra(ImagePickerActivity.FROM_WHICH_ACTIVITY, PostEditActivity.class.toString());
        intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mResults);// 默认选择
        mActivity.startActivityForResult(intent, Constant.POST_EDIT_REQUEST_PICKER_CODE);
    }


    public void setImage(int requestCode, Intent data) {
        if (isThis) {
            isThis = false;
            if (requestCode == Constant.POST_EDIT_REQUEST_PICKER_CODE && data != null) {
                mImageList = data.getParcelableArrayListExtra("CheckedMediaList");
                mResults=getCheckedImagePathList();
                if (mResults != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }


    }

    private Bitmap setImg(String strPath) {

        //解析图片时需要使用到的参数都封装在这个对象里了
        BitmapFactory.Options opt = new BitmapFactory.Options();
        //不为像素申请内存，只获取图片宽高
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strPath, opt);
        //拿到图片宽高
        int imageWidth = opt.outWidth;
        int imageHeight = opt.outHeight;

        //计算缩放比例
        int scale = 1;
        int scaleWidth = (int) (imageWidth / (Util.getDensity() * 65));
        if (scaleWidth >= 1) {
            scale = scaleWidth;
        }

        //设置缩放比例
        opt.inSampleSize = scale;
        opt.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(strPath, opt);
    }

    /**
     * 返回图片地址
     *
     * @return
     */
    public ArrayList<String> getmResults() {
        return mResults;
    }

    class MyAdapter extends BaseAdapter {
        private boolean isMax = false;
        private int listCount;

        @Override
        public int getCount() {
            listCount = mResults.size();
            if (maxItem > listCount) {
                isMax = false;
                return listCount + 1;
            }
            isMax = true;
            return listCount;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View v = View.inflate(getContext(), R.layout.dfy_item_cust_uploadimage, null);
            final ImageView iv_img = (ImageView) v.findViewById(R.id.iv_img);
            final View v_dete = v.findViewById(R.id.view_delete);

            if (isMax) {
                v_dete.setVisibility(VISIBLE);
                iv_img.setImageBitmap(setImg(mResults.get(i)));
            } else {
                if (i != listCount) {
                    v_dete.setVisibility(VISIBLE);
                    iv_img.setImageBitmap(setImg(mResults.get(i)));
                }
            }

            v_dete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mResults.remove(i);
                    notifyDataSetChanged();
                }
            });

            iv_img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    upLoadImage();
                }
            });

            return v;
        }
    }
}
