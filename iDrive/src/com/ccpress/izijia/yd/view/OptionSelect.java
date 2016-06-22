package com.ccpress.izijia.yd.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;

/**
 * Created by dengmingzhi on 16/5/29.
 */
public class OptionSelect extends RelativeLayout {
    private boolean currentSelect = true;
    private ImageView iv_1;
    private ImageView iv_2;
    private OnSelectChangeListener onSelectChangeListener;

    public OptionSelect(Context context) {
        this(context, null);
    }

    public OptionSelect(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptionSelect(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View.inflate(getContext(), R.layout.yd_cust_select, this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        TextView tv_1 = (TextView) findViewById(R.id.tv_1);
        TextView tv_2 = (TextView) findViewById(R.id.tv_2);
        iv_1 = (ImageView) findViewById(R.id.iv_1);
        iv_2 = (ImageView) findViewById(R.id.iv_2);
        iv_1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSelect) {
                    return;
                } else {
                    iv_1.setImageResource(R.drawable.yd_select_checked);
                    iv_2.setImageResource(R.drawable.yd_select);
                    currentSelect = true;
                    if (onSelectChangeListener != null) {
                        onSelectChangeListener.selectChange(currentSelect);
                    }

                }
            }
        });

        iv_2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentSelect) {
                    return;
                } else {
                    iv_2.setImageResource(R.drawable.yd_select_checked);
                    iv_1.setImageResource(R.drawable.yd_select);
                    currentSelect = false;
                    if (onSelectChangeListener != null) {
                        onSelectChangeListener.selectChange(currentSelect);
                    }

                }
            }


        });
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.OptionSelect);
        String title = typedArray.getString(R.styleable.OptionSelect_OptionSelect_title);
        String title_1 = typedArray.getString(R.styleable.OptionSelect_OptionSelect_tv_1);
        String title_2 = typedArray.getString(R.styleable.OptionSelect_OptionSelect_tv_2);
        typedArray.recycle();
        tv_title.setText(title);
        tv_1.setText(title_1);
        tv_2.setText(title_2);

    }


    public boolean getSelect() {
        return currentSelect;
    }

    public void setOnSelectChangeListener(OnSelectChangeListener onSelectChangeListener) {
        this.onSelectChangeListener = onSelectChangeListener;
    }

    public interface OnSelectChangeListener {
        void selectChange(boolean change);
    }

    public void setSelect(boolean select) {
        currentSelect = select;
        if (currentSelect) {
            iv_1.setImageResource(R.drawable.yd_select_checked);
            iv_2.setImageResource(R.drawable.yd_select);
        } else {
            iv_2.setImageResource(R.drawable.yd_select_checked);
            iv_1.setImageResource(R.drawable.yd_select);
        }
    }
}
