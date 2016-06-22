package com.ccpress.izijia.dfy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by administror on 2016/3/24 0024.
 */
public class JiaAndJianRelativeLayout extends RelativeLayout {
    private JiaAndJianInterface jaji;

    public JiaAndJianRelativeLayout(Context context) {
        super(context);
    }

    public JiaAndJianRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JiaAndJianRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int downX = (int) event.getX();
                if (downX < getWidth() / 3.56) {
                    if (jaji != null) {
                        jaji.jian();
                    }
                } else if (downX > getWidth() / 1.47) {
                    if (jaji != null) {
                        jaji.jia();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    public interface JiaAndJianInterface {
        void jia();

        void jian();
    }

    public void setJiaAndJianInterface(JiaAndJianInterface jaji) {
        this.jaji = jaji;
    }
}
