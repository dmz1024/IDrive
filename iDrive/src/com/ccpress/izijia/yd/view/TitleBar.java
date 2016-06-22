package com.ccpress.izijia.yd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;


/**
 * Created by dengmingzhi on 16/4/20.
 */
public class TitleBar extends RelativeLayout implements View.OnClickListener {
    private RelativeLayout titleBar;
    private RelativeLayout rl_left;
    private TextView tv_title;
    private TextView tv_right;
    private OnLeftClickListener onLeftListener;
    private OnRightClickListener onRightListener;

    public TitleBar(Context context) {
        this(context,null);
    }



    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    /**
     * 获得我自定义的样式属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        titleBar = (RelativeLayout) View.inflate(getContext(), R.layout.yd_item_title_bar,this);
        rl_left= (RelativeLayout) titleBar.findViewById(R.id.rl_left);
        tv_title= (TextView) titleBar.findViewById(R.id.tv_title);
        tv_right= (TextView) titleBar.findViewById(R.id.tv_right);
        initOnclick();

    }

    /**
     * 设置点击事件
     */
    private void initOnclick() {
        rl_left.setOnClickListener(this);
        tv_right.setOnClickListener(this);
    }


    /**
     * 点击事件处理
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_left:
                if(onLeftListener!=null){
                    onLeftListener.left();
                }
                break;
            case R.id.tv_right:
                if(onRightListener!=null){
                    onRightListener.right();
                }
                break;
        }
    }

    /**
     * 设置回调接口
     * @param onLeft
     */
    public void setOnLeftClickListener(OnLeftClickListener onLeft){
        this.onLeftListener=onLeft;
    }

    public void setOnRightClickListener(OnRightClickListener onRight){
        this.onRightListener=onRight;
    }




    public void setTvTitleText(String text){
        tv_title.setText(text);
    }

    public void setTvTitleText(int rId){
        tv_title.setText(rId);
    }

    public void setTvRightText(String text){
        tv_right.setText(text);
    }

    public void setTvRightText(int rId){
        tv_right.setText(rId);
    }

    public void setTvTitleVisi(boolean visibi){
        tv_title.setVisibility(visibi?View.VISIBLE:View.GONE);
    }

    public void setTvRightVisi(boolean visibi){
        tv_right.setVisibility(visibi?View.VISIBLE:View.GONE);
    }

    public void setRlLeftVisi(boolean visibi){
        rl_left.setVisibility(visibi?View.VISIBLE:View.GONE);
    }


    /**
     * 左侧点击事件监听接口
     */
    public interface OnLeftClickListener {
        void left();
    }

    /**
     * 右侧点击事件监听接口
     */
    public interface OnRightClickListener {
        void right();
    }
}
