package me.nereo.multi_image_selector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import me.nereo.multi_image_selector.bean.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 多图选择
 * Created by Nereo on 2015/4/7.
 */
public abstract class MultiImageSelectorActivity extends FragmentActivity implements MultiImageSelectorFragment.Callback{

    /** 最大图片选择次数，int类型，默认9 */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** 图片选择模式，默认多选 */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /** 是否显示相机，默认显示 */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合  */
    public static final String EXTRA_RESULT = "select_result";
    /** 默认选择集 */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /** 单选 */
    public static final int MODE_SINGLE = 0;
    /** 多选 */
    public static final int MODE_MULTI = 1;
    MultiImageSelectorFragment fragment;

    private ArrayList<String> resultList = new ArrayList<>();
    public  static ArrayList<String> arry = new ArrayList<>();
    private Button mSubmitButton;
    private int mDefaultCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewID());

        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
        int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if(mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }

        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);
        fragment = new MultiImageSelectorFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, fragment)//Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle)
                .commit();

        // 返回按钮
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultList.clear();
                arry.clear();
                finish();
            }
        });

        // 完成按钮
        mSubmitButton = (Button) findViewById(R.id.commit);
        if(resultList == null || resultList.size()<=0){
            mSubmitButton.setText("完成");
            mSubmitButton.setEnabled(false);
        }else{
                mSubmitButton.setText("完成("+resultList.size()+"/"+mDefaultCount+")");
                mSubmitButton.setEnabled(true);

        }
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (resultList.size()>mDefaultCount){
                Toast.makeText(getBaseContext(),"照片数量超过"+String.valueOf(mDefaultCount)+"张",Toast.LENGTH_LONG).show();
            }else {
                arry=resultList;
                onCommitBtnClick(resultList);
            }
            }
        });
    }


    public boolean isShowCamera(){
        return getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
    }

    @Override
    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        if(!resultList.contains(path)) {
            resultList.add(path);
        }
        // 有图片之后，改变按钮状态
        if(resultList.size() > 0){
            mSubmitButton.setText("完成("+resultList.size()+"/"+mDefaultCount+")");
            if(!mSubmitButton.isEnabled()){
                mSubmitButton.setEnabled(true);
            }
        }
    }

    abstract protected int getViewID();

    protected void SelectedImageChanged(ArrayList<String> list){
        fragment.setResultList(list);
        if(resultList!= null){
            resultList.clear();
        }
        resultList.addAll(list);
        if(resultList == null || resultList.size()<=0){
            mSubmitButton.setText("完成");
            mSubmitButton.setEnabled(false);
        }else{
            mSubmitButton.setText("完成("+resultList.size()+"/"+mDefaultCount+")");
            mSubmitButton.setEnabled(true);
        }
    }

    @Override
    public void onImageUnselected(String path) {
        if(resultList.contains(path)){
            resultList.remove(path);
            mSubmitButton.setText("完成("+resultList.size()+"/"+mDefaultCount+")");
        }else{
            mSubmitButton.setText("完成("+resultList.size()+"/"+mDefaultCount+")");
        }
        // 当为选择图片时候的状态
        if(resultList.size() == 0){
            mSubmitButton.setText("完成");
            mSubmitButton.setEnabled(false);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {
        if(imageFile != null) {
            Intent data = new Intent();
            resultList.add(imageFile.getAbsolutePath());
            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    protected List<Image> getSelectedImages(){
        return fragment.getSelectedImages();
    }

    protected void onCommitBtnClick(ArrayList<String> resultList){

       /* if (resultList != null && resultList.size() > 0) {
           // 返回已选择的图片数据
            Intent data = new Intent();
            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
       }*/
    }


}
