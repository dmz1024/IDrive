package com.ccpress.izijia.dfy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;

/**
 * Created by administror on 2016/3/30 0030.
 */
public class TopView extends RelativeLayout implements View.OnClickListener {
    private Context ctx;
    private TextView tv_center;
    private ImageView iv_left;
    private ImageView iv_right;
    private ProgressBar pb_load;
    private LeftOnclick leftOnclick;
    private RightOnclick rightOnclick;

    public TopView(Context context) {
        super(context);
        ctx = context;
        init();
    }


    public TopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        init();
    }

    public TopView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ctx = context;
        init();
    }

    private void init() {
        inflate(ctx, R.layout.dfy_top_view, this);
        tv_center = (TextView) findViewById(R.id.tv_center);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        pb_load = (ProgressBar) findViewById(R.id.pb_load);
        iv_right.setVisibility(View.GONE);
        iv_left.setOnClickListener(this);
        iv_right.setOnClickListener(this);
    }


    public void setPbVisibility(boolean visibility) {
        pb_load.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                left();
                break;
            case R.id.iv_right:
                right();
                break;
        }
    }

    private void right() {
        if (rightOnclick != null) {
            rightOnclick.right();
        }
    }

    /**
     * 返回上一个界面
     */
    private void left() {
        if (leftOnclick != null) {
            leftOnclick.left();
        }
    }

    /**
     * textView值
     *
     * @param text
     */
    public void setText(String text) {
        tv_center.setText(text);
    }

    /**
     * string的资源id
     *
     * @param sid
     */
    public void setText(int sid) {
        tv_center.setText(sid);
    }

    public interface LeftOnclick {
        void left();
    }

    public interface RightOnclick {
        void right();
    }

    /**
     * 设置左侧按钮的监听事件
     *
     * @param Onclick
     */
    public void setLeftOnclick(LeftOnclick Onclick) {
        this.leftOnclick = Onclick;
    }

    public void setRightOnclick(RightOnclick Onclick) {
        this.rightOnclick = Onclick;
    }

    /**
     * 如果visibility为true，则iv_right可见
     *
     * @param visibility
     */
    public void setRightVisibility(boolean visibility) {
        if (visibility) {
            iv_right.setVisibility(View.VISIBLE);
        } else {
            iv_right.setVisibility(View.GONE);
        }
    }

    public void setLeftVisibility(boolean visibility) {
        if (visibility) {
            iv_left.setVisibility(View.VISIBLE);
        } else {
            iv_left.setVisibility(View.GONE);
        }
    }

    /**
     * 给右侧按钮设置图片
     *
     * @param rid
     */
    public void setRightImage(int rid) {

        try {
            iv_right.setImageResource(rid);
        } catch (Exception e) {
            throw new IllegalArgumentException("请传入正确的图片资源id");
        }
    }

    /**
     * 给左侧按钮设置图片
     *
     * @param lid
     */
    public void setLeftImage(int lid) {
        try {
            iv_left.setImageResource(lid);
        } catch (Exception e) {
            throw new IllegalArgumentException("请传入正确的图片资源id");
        }
    }
}
