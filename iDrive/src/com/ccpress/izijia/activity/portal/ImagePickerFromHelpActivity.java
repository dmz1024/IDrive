package com.ccpress.izijia.activity.portal;

import android.content.Intent;
import android.os.Bundle;
import com.ccpress.izijia.R;
import com.ccpress.izijia.activity.HelpActivity;
import com.ccpress.izijia.activity.ImageViewActivity;
import com.ccpress.izijia.activity.MainActivity;
import com.ccpress.izijia.activity.PostEditActivity;
import com.ccpress.izijia.entity.MediaEntity;
import com.trs.util.log.Log;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.bean.Folder;
import me.nereo.multi_image_selector.bean.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WLH on 2015/6/11 11:37.
 */
public class ImagePickerFromHelpActivity extends MultiImageSelectorActivity{
    public static final String MEDIA_TYPE = "MediaPickerActivityMediaType";
    public static final String CHECKED_MEDIA_PATH_LIST = "CheckedMediaPathList";
    public static final String SELECT_COUNT = "SelectCount";
    public static final int MEDIA_TYPE_IMG = 1;
    public static final String FROM_WHICH_ACTIVITY = "fromAct";
    private String fromAct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromAct = getIntent().getStringExtra(FROM_WHICH_ACTIVITY);
    }

    @Override
    protected int getViewID() {
        return R.layout.activity_imagepicker;
    }

    @Override
    public void onCameraShot(File imageFile) {
//        super.onCameraShot(imageFile);
        ArrayList<String> li = new ArrayList<String>();
        li.add(imageFile.getAbsolutePath());
        List<Image> list = getSelectedImages();
        for(Image image: list){
            li.add(image.path);
        }
        SelectedImageChanged(li);
    }

    @Override
    public void onImageUnselected(String path) {
        super.onImageUnselected(path);

    }

    @Override
    public void onImageSelected(String path) {
        super.onImageSelected(path);

    }

    @Override
    public void onPreview(ArrayList<String> resultList, int position, Folder mCurrentFolder) {

        Intent intent = new Intent(this, ImageViewActivity.class);
        intent.putStringArrayListExtra(CHECKED_MEDIA_PATH_LIST, resultList);
        if(resultList != null){
            intent.putExtra(SELECT_COUNT, resultList.size());
        }
        if(mCurrentFolder != null){
            intent.putExtra(ImageViewActivity.FOLDER_PATH, mCurrentFolder.path);
        }
        intent.putExtra(ImageViewActivity.INIT_INDEX, isShowCamera() ? position - 1 : position);
        startActivityForResult(intent, MEDIA_TYPE_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case MEDIA_TYPE_IMG:
                    ArrayList<String> li = data.getStringArrayListExtra(CHECKED_MEDIA_PATH_LIST);
                    if (li == null) {
                        return;
                    }
                    Log.e("WLH", "ImagePickerActivity onActivityResult  li.size:"+li.size());
                    SelectedImageChanged(li);
                    break;
            }
        }
    }

    @Override
    public void onSingleImageSelected(String path) {
        super.onSingleImageSelected(path);

    }

    @Override
    protected void onCommitBtnClick(ArrayList<String> resultList) {
        //super.onCommitBtnClick(resultList);
        finishThisActivity(false);
    }

    private void finishThisActivity(boolean isCancel) {
        if (!isCancel) {
            Intent intent = new Intent(this, HelpActivity.class);
            intent.putExtra(MEDIA_TYPE, MEDIA_TYPE_IMG);
            intent.putExtra(PostEditActivity.CHECKED_MEDIA_LIST, getCheckedImages());
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

    protected ArrayList<MediaEntity> getCheckedImages() {
        List<Image> images = super.getSelectedImages();
        ArrayList<MediaEntity> list = new ArrayList<>();
        if(images != null){
            for (Image image : images){
                MediaEntity entity = new MediaEntity();
                entity.setPath(image.path);
                entity.setName(image.name);
                entity.setCreatetime(image.time);
                list.add(entity);
            }
        }
        Log.e("WLH", "ImagePickerActivity select image size:"+list.size());
        return list;
    }
}
