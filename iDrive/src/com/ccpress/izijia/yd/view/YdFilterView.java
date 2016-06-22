package com.ccpress.izijia.yd.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;

import java.util.List;

/**
 * Created by dengmingzhi on 16/5/25.
 */
public class YdFilterView extends RelativeLayout {
    private TextView tv_title;
    private ImageView iv;
    public boolean isdown = false;
    private List<YdFilterView> list;
    private OnRefreshListener onRefreshListener;
    private boolean type;

    public YdFilterView(Context context) {
        this(context, null);
    }

    public YdFilterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YdFilterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View.inflate(getContext(), R.layout.yd_filter_view, this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv = (ImageView) findViewById(R.id.iv);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.YdFilterView);
        type = typedArray.getBoolean(R.styleable.YdFilterView_YdFilterView_type, true);
//        show = typedArray.getBoolean(R.styleable.YdFilterView_YdFilterView_show, false);
        String title = typedArray.getString(R.styleable.YdFilterView_YdFilterView_title);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                check();
            }
        });


        if (!type) {
            iv.setImageResource(R.drawable.yd_icon_sort);
        }

        tv_title.setText(title);
    }

    public void cleanCheck() {
        tv_title.setTextColor(getResources().getColor(R.color.dfy_333));
        if (type) {
            iv.setImageResource(R.drawable.yd_con_down);
        } else {
            iv.setImageResource(R.drawable.yd_icon_sort);
        }
        isdown = false;
    }

    public void check() {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                YdFilterView ydFilterView = list.get(i);
                if (ydFilterView != this) {
                    ydFilterView.cleanCheck();
                }
            }
        }

        if (!type) {
            iv.setImageResource(R.drawable.yd_icon_sort_check);
        }

        tv_title.setTextColor(getResources().getColor(R.color.dfy_50bbdb));
        if (isdown) {
            if (type) {
                iv.setImageResource(R.drawable.yd_con_up_check);
            }
        } else {
            if (type) {
                iv.setImageResource(R.drawable.yd_con_down_check);
            }
        }

        isdown = !isdown;
        if (onRefreshListener != null) {
            onRefreshListener.refresh(this, !isdown);
        }
    }


    public void setOtherView(List<YdFilterView> list) {
        this.list = list;
    }


    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public interface OnRefreshListener {
        void refresh(YdFilterView ydFilterView, boolean down);
    }

//    private void rotate() {
//        Animation rotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotateAnimation.setDuration(0);
//        rotateAnimation.setFillAfter(true);
//        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                iv.clearAnimation();
//                if (isdown) {
//                    iv.setImageResource(R.drawable.yd_con_down_check);
//
//                } else {
//                    iv.setImageResource(R.drawable.yd_con_up_check);
//                }
//                refresh(isdown);
//                isdown = !isdown;
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        iv.startAnimation(rotateAnimation);
//    }
}
