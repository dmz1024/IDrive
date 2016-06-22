package com.ccpress.izijia.yd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.ccpress.izijia.R;

/**
 * Created by administror on 2016/3/29 0029.
 */
public class StarImageView extends ImageView {
    private boolean isFirst = true;
    private int starNum = 1;
    private int[] images;
    private boolean isCkeck=false;

    public StarImageView(Context context) {
        super(context);
    }

    public StarImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StarImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int totalX = getWidth();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int downX = (int) event.getX();
                int oneX = totalX / 5;
                if (isCkeck) {
                    if (downX <= oneX) {
                        starNum = 1;
                        setImageResource(R.drawable.yd_star_1);
                    } else if (oneX < downX && downX <= 2 * oneX) {
                        starNum = 2;
                        setImageResource(R.drawable.yd_star_2);
                    } else if (2 * oneX < downX && downX <= 3 * oneX) {
                        starNum = 3;
                        setImageResource(R.drawable.yd_star_3);
                    } else if (3 * oneX < downX && downX <= 4 * oneX) {
                        starNum = 4;
                        setImageResource(R.drawable.yd_star_4);
                    } else if (4 * oneX < downX && downX <= 5 * oneX) {
                        starNum = 5;
                        setImageResource(R.drawable.yd_star_5);
                    }
                    return true;
                }

                break;
        }
        return super.onTouchEvent(event);
    }

    public int getStarNum() {
        return starNum;
    }

    public void setCheck(boolean check) {
        this.isCkeck = check;
    }


}
