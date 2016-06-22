package com.ccpress.izijia.dfy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.ccpress.izijia.R;

/**
 * Created by administror on 2016/3/29 0029.
 */
public class RatingImageView extends ImageView {
    private boolean isFirst = true;
    private int starNum = 1;

    public RatingImageView(Context context) {
        super(context);
    }

    public RatingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RatingImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int totalX = getWidth();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int downX = (int) event.getX();
                int oneX = totalX / 5;
                if (downX <= oneX) {
                    setImageResource(R.drawable.dfy_star_1);
                    starNum = 1;
                } else if (oneX < downX && downX <= 2 * oneX) {
                    starNum = 2;
                    setImageResource(R.drawable.dfy_star_2);
                } else if (2 * oneX < downX && downX <= 3 * oneX) {
                    starNum = 3;
                    setImageResource(R.drawable.dfy_star_3);
                } else if (3 * oneX < downX && downX <= 4 * oneX) {
                    starNum = 4;
                    setImageResource(R.drawable.dfy_star_4);
                } else if (4 * oneX < downX && downX <= 5 * oneX) {
                    starNum = 5;
                    setImageResource(R.drawable.dfy_star_5);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public int getStarNum() {
        return starNum;
    }

    public void setRating(int count) {
        switch (count) {
            case 0:
                setImageResource(R.drawable.yd_star_0);
                break;
            case 1:
                setImageResource(R.drawable.yd_star_1);
                break;
            case 2:
                setImageResource(R.drawable.yd_star_2);
                break;
            case 3:
                setImageResource(R.drawable.yd_star_3);
                break;
            case 4:
                setImageResource(R.drawable.yd_star_4);
                break;
            case 5:
                setImageResource(R.drawable.yd_star_5);
                break;
        }
    }

}
