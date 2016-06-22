package com.ccpress.izijia.yd.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;

/**
 * Created by dengmingzhi on 16/4/22.
 */
public class ViewText extends RelativeLayout {
    private TextView tv_title;
    public ViewText(Context context) {
        this(context,null);
    }

    public ViewText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ViewText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View.inflate(getContext(), R.layout.yd_view_text,this);
        tv_title= (TextView) findViewById(R.id.tv_title);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ViewText);
        String tv_title_text=typedArray.getString(R.styleable.ViewText_ViewText_title);
        typedArray.recycle();
        tv_title.setText(tv_title_text);
    }

    public void setTitle(String title){
        tv_title.setText(title);
    }

}
